/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTTreeAdequacyColumnType;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.listeners.ViewerSelectionKeepBackgroundColor;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.ui.ACredibilitySubView;
import gov.sandia.cf.parts.ui.MainViewManager;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTAdequacyColumnLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTImportanceColumnLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTPhenTableHeaderCellModifier;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTPhenTableHeaderContentProvider;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTPhenTreePhenomenaCellModifier;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTPhenTreePhenomenaContentProvider;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTPhenomenonDropSupport;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTPhenomenonImportanceCellEditor;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTPhenomenonLevelCellEditor;
import gov.sandia.cf.parts.viewer.PIRTPhenomenaTreePhenomena;
import gov.sandia.cf.parts.viewer.PIRTQoITableQoI;
import gov.sandia.cf.parts.viewer.TableFactory;
import gov.sandia.cf.parts.viewer.TableHeaderBar;
import gov.sandia.cf.parts.viewer.TreeViewerHideSelection;
import gov.sandia.cf.parts.viewer.TreeViewerID;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.parts.viewer.editors.ColumnViewerSupport;
import gov.sandia.cf.parts.widgets.FancyToolTipSupport;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * An implementation of the phenomena view with a treeViewer
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTPhenomenaView extends ACredibilitySubView<PIRTPhenomenaViewController> {

	/**
	 * the description table viewer
	 */
	private TableHeaderBar tableHeaderBar;

	/**
	 * the tree viewer
	 */
	private TreeViewerID treeViewer;

	/**
	 * Table buttons
	 */
	private Map<TreeItem, TreeEditor> addEditors;
	private Map<TreeItem, TreeEditor> viewEditors;
	private Map<TreeItem, TreeEditor> editEditors;
	private Map<TreeItem, TreeEditor> deleteEditors;

	/**
	 * The assess table composite
	 */
	private Composite compositeTable;

	/**
	 * Buttons events PIRT
	 */
	/** PIRT ADD PHENOMENON GROUP button event name */
	public static final String BTN_EVENT_PIRT_PHEN_ADD_PHENOMENON_GROUP = "PIRT_ADD_PHENOMENON_GROUP"; //$NON-NLS-1$
	/** PIRT DELETE button event name */
	public static final String BTN_EVENT_PIRT_PHEN_DELETE = "PIRT_REMOVE"; //$NON-NLS-1$
	/** PIRT RESET button event name */
	public static final String BTN_EVENT_PIRT_PHEN_RESET = "PIRT_RESET"; //$NON-NLS-1$
	/** PIRT CLOSE button event name */
	public static final String BTN_EVENT_PIRT_PHEN_CLOSE = "PIRT_CLOSE"; //$NON-NLS-1$

	/**
	 * @param parent         the parent composite
	 * @param viewController the parent view
	 * @param style          the view style
	 */
	public PIRTPhenomenaView(PIRTPhenomenaViewController viewController, Composite parent, int style) {
		super(viewController, parent, style);

		// Make sure you dispose these buttons when viewer input changes
		addEditors = new HashMap<>();
		viewEditors = new HashMap<>();
		editEditors = new HashMap<>();
		deleteEditors = new HashMap<>();

		// Override title
		this.setTitle(this.getTitle());

		// Render page
		renderPage();
	}

	/** {@inheritDoc} */
	@Override
	public String getTitle() {
		QuantityOfInterest qoiSelected = getViewController().getQoISelected();
		if (null != qoiSelected) {
			if (null != qoiSelected.getTagDate()) {
				this.lblTitle.setImage(IconTheme.getIconImage(getViewController().getViewManager().getRscMgr(),
						IconTheme.ICON_NAME_TAG, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BROWN), 30));
			}
			return RscTools.getString(RscConst.MSG_PHENOMENAVIEW_TITLE, qoiSelected.getSymbol());
		} else {
			return RscTools.getString(RscConst.MSG_PHENOMENAVIEW_TITLE, RscTools.empty());
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_PHENOMENAVIEW_ITEMTITLE);
	}

	/**
	 * Creates the phenomena view and all its components
	 * 
	 * @param parent
	 * 
	 */
	private void renderPage() {

		// Renders
		renderHeaderTable();

		// Render header buttons
		if (!getViewController().isTagged()) {
			renderHeaderButtons();
		}

		// Render the main table composite
		renderMainTableComposite();

		// Render footer buttons
		renderFooterButtons();
	}

	/**
	 * Render header table
	 */
	private void renderHeaderTable() {

		QuantityOfInterest qoiSelected = getViewController().getQoISelected();

		// Table Header - Create
		if (qoiSelected != null) {
			String tableHeaderBarName = qoiSelected.getSymbol();
			tableHeaderBar = new TableHeaderBar(getViewController().getViewManager(), this, tableHeaderBarName);
			tableHeaderBar.setContentProvider(new PIRTPhenTableHeaderContentProvider(getViewController()));
			tableHeaderBar.setCellModifier(new PIRTPhenTableHeaderCellModifier(getViewController()));
		}
	}

	/**
	 * Render header buttons
	 */
	private void renderHeaderButtons() {

		// Header buttons - Composite
		Composite compositeButtonsHeader = new Composite(this, SWT.FILL);
		GridLayout gridLayoutButtonsHeader = new GridLayout(2, false);
		compositeButtonsHeader.setLayout(gridLayoutButtonsHeader);
		compositeButtonsHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		// Composite for header left buttons
		Composite compositeButtonsHeaderLeft = new Composite(compositeButtonsHeader, SWT.NONE);
		compositeButtonsHeaderLeft.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
		compositeButtonsHeaderLeft.setLayout(new RowLayout());

		// Composite for header right buttons
		Composite compositeButtonsHeaderRight = new Composite(compositeButtonsHeader, SWT.NONE);
		compositeButtonsHeaderRight.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsHeaderRight.setLayout(new RowLayout());

		// Button - Add Phenomenon Group
		Map<String, String> btnAddPhenomenonGroupData = new HashMap<>();
		btnAddPhenomenonGroupData.put(MainViewManager.BTN_EVENT_PROPERTY, BTN_EVENT_PIRT_PHEN_ADD_PHENOMENON_GROUP);
		Map<String, Object> btnAddPhenomenonGroupOptions = new HashMap<>();
		btnAddPhenomenonGroupOptions.put(ButtonTheme.OPTION_TEXT,
				RscTools.getString(RscConst.MSG_PHENOMENAVIEW_BTN_ADDGROUP));
		btnAddPhenomenonGroupOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnAddPhenomenonGroupOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_ADD);
		btnAddPhenomenonGroupOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
		btnAddPhenomenonGroupOptions.put(ButtonTheme.OPTION_DATA, btnAddPhenomenonGroupData);
		btnAddPhenomenonGroupOptions.put(ButtonTheme.OPTION_LISTENER,
				(Listener) event -> getViewController().addPhenomenonGroupAction());
		btnAddPhenomenonGroupOptions.put(ButtonTheme.OPTION_ENABLED, !getViewController().isTagged());
		new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtonsHeaderRight, SWT.RIGHT,
				btnAddPhenomenonGroupOptions);

		// button Reset
		Map<String, String> btnResetData = new HashMap<>();
		btnResetData.put(MainViewManager.BTN_EVENT_PROPERTY, BTN_EVENT_PIRT_PHEN_RESET);
		Map<String, Object> btnResetOptions = new HashMap<>();
		btnResetOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_RESET));
		btnResetOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnResetOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_RESET);
		btnResetOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_RED);
		btnResetOptions.put(ButtonTheme.OPTION_DATA, btnResetData);
		btnResetOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> getViewController().resetAction());
		btnResetOptions.put(ButtonTheme.OPTION_ENABLED, !getViewController().isTagged());
		new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtonsHeaderLeft, SWT.RIGHT,
				btnResetOptions);
	}

	/**
	 * Render main table
	 */
	private void renderMainTableComposite() {

		// Grid Layout
		compositeTable = new Composite(this, SWT.FILL);
		GridLayout gridLayout = new GridLayout();
		compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		compositeTable.setLayout(gridLayout);
	}

	/**
	 * Render PIRT Phenomena tree
	 */
	private void renderMainTable() {

		// Initialize tree
		renderMainTableInit();

		// Initialize data
		List<String> columnProperties = new ArrayList<>();

		// Get Tree and layout
		Tree tree = treeViewer.getTree();
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) tree.getLayout();

		// Columns - Id
		treeViewer.getIdColumn().setLabelProvider(new PIRTPhenomenaColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof PhenomenonGroup) {
					if (StringUtils.isBlank(((PhenomenonGroup) element).getIdLabel())) {
						((PhenomenonGroup) element).setIdLabel(treeViewer.getIdColumnText(element));
					}
					return ((PhenomenonGroup) element).getIdLabel();
				} else if (element instanceof Phenomenon) {
					if (StringUtils.isBlank(((Phenomenon) element).getIdLabel())) {
						((Phenomenon) element).setIdLabel(treeViewer.getIdColumnText(element));
					}
					return ((Phenomenon) element).getIdLabel();
				}
				return treeViewer.getIdColumnText(element);
			}
		});
		columnProperties.add(treeViewer.getIdColumn().getColumn().getText());

		// Columns - Phenomena (Text type)
		TreeViewerColumn phenomenaColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		phenomenaColumn.getColumn().setText(PIRTPhenomenaTreePhenomena.getColumnPhenomenaProperty());
		phenomenaColumn.setLabelProvider(new PIRTPhenomenaColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof PhenomenonGroup) {
					return ((PhenomenonGroup) element).getName();
				} else if (element instanceof Phenomenon) {
					return ((Phenomenon) element).getName();
				}
				return RscTools.empty();
			}
		});
		treeViewerLayout
				.addColumnData(new ColumnWeightData(PartsResourceConstants.PHEN_VIEW_TREEPHEN_TXT_COLUMN_COEFF, true));
		columnProperties.add(PIRTPhenomenaTreePhenomena.getColumnPhenomenaProperty());

		// Columns - Importance (Level type)
		TreeViewerColumn importanceColumn = new TreeViewerColumn(treeViewer, SWT.CENTER);
		importanceColumn.getColumn().setText(PIRTPhenomenaTreePhenomena.getColumnImportanceProperty());
		importanceColumn.setEditingSupport(
				new PIRTPhenomenonImportanceCellEditor(importanceColumn.getViewer(), getViewController()));
		importanceColumn.setLabelProvider(new PIRTImportanceColumnLabelProvider(
				getViewController().getPirtConfiguration(), getViewController().getViewManager().getRscMgr()));
		treeViewerLayout
				.addColumnData(new ColumnWeightData(PartsResourceConstants.PHEN_VIEW_TREEPHEN_LVL_COLUMN_COEFF, true));
		columnProperties.add(PIRTPhenomenaTreePhenomena.getColumnImportanceProperty());

		// Columns - construct generated tree columns from PIRT configuration
		if (getViewController().getViewManager().getCache().getPIRTSpecification() != null) {
			for (PIRTAdequacyColumn column : getViewController().getViewManager().getCache().getPIRTSpecification()
					.getColumns()) {

				TreeViewerColumn tempColumn = null;

				// cell editor depending of column type
				if (PIRTTreeAdequacyColumnType.TEXT.getType().equals(column.getType())) {
					tempColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
					tempColumn.getColumn().setText(column.getName());
					tempColumn.setLabelProvider(new ColumnLabelProvider() {
						@Override
						public String getText(Object element) {
							if (element instanceof Phenomenon) {
								Optional<Criterion> found = ((Phenomenon) element).getCriterionList().stream()
										.filter(Objects::nonNull)
										.filter(c -> column.getName() != null && column.getName().equals(c.getName()))
										.findFirst();
								if (found.isPresent()) {
									return found.get().getValue();
								}
							}
							return RscTools.empty();
						}
					});
					treeViewerLayout.addColumnData(
							new ColumnWeightData(PartsResourceConstants.PHEN_VIEW_TREEPHEN_TXT_COLUMN_COEFF, true));
				} else if (PIRTTreeAdequacyColumnType.LEVELS.getType().equals(column.getType())) {
					tempColumn = new TreeViewerColumn(treeViewer, SWT.CENTER);
					tempColumn.getColumn().setText(column.getName());
					tempColumn.setEditingSupport(
							new PIRTPhenomenonLevelCellEditor(tempColumn.getViewer(), getViewController(), column));
					treeViewerLayout.addColumnData(
							new ColumnWeightData(PartsResourceConstants.PHEN_VIEW_TREEPHEN_LVL_COLUMN_COEFF, true));
					tempColumn.setLabelProvider(new PIRTAdequacyColumnLabelProvider(
							getViewController().getPirtConfiguration(), column, getViewController().getViewManager()));
				}

				if (null != tempColumn) {
					columnProperties.add(column.getName());
				}
			}
		}

		// Columns - Actions
		renderMainTableActionColumns(columnProperties);

		// Add listeners
		renderMainTableAddEvents();

		// tree editors, modifiers, providers
		treeViewer.setColumnProperties(columnProperties.stream().toArray(String[]::new));
		treeViewer.setContentProvider(new PIRTPhenTreePhenomenaContentProvider());
		treeViewer.setCellModifier(new PIRTPhenTreePhenomenaCellModifier());

		// add drag and drop support on PIRT tree to transfer phenomenon to another
		// group
		addDragAndDropSupport();

		// SWT.EraseItem: this event is called when repainting the tree not on removing
		// an element of the tree
		// In this case, it keeps cell colors when selection is active on cell
		tree.addListener(SWT.EraseItem, new ViewerSelectionKeepBackgroundColor(treeViewer) {

			@Override
			public boolean isConditionFulfilled(Object data) {
				return data instanceof Phenomenon;
			}
		});

		// Refresh
		treeViewer.refresh(true);

		// Layout
		treeViewer.getTree().layout();
	}

	/**
	 * Initialize Main table
	 */
	private void renderMainTableInit() {
		// Create
		treeViewer = new TreeViewerID(compositeTable,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		GridData gdViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);

		Tree tree = treeViewer.getTree();
		tree.setLayoutData(gdViewer);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		FancyToolTipSupport.enableFor(treeViewer, ToolTip.NO_RECREATE);

		// Customize table
		tree.setHeaderBackground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
		tree.setHeaderForeground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));

		// Layout
		final AutoResizeViewerLayout treeLayout = new AutoResizeViewerLayout(treeViewer);
		treeViewer.setLayout(treeLayout);

		// Set height and width
		gdViewer.heightHint = tree.getItemHeight();
		gdViewer.widthHint = getViewController().getViewManager().getSize().x - 2 * getLayout().horizontalSpacing;
	}

	/**
	 * Add action column
	 * 
	 * @param columnProperties
	 */
	private void renderMainTableActionColumns(List<String> columnProperties) {

		// Get Tree and layout
		Tree tree = treeViewer.getTree();
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) tree.getLayout();

		// Tree - Column - Action Add
		if (!getViewController().isTagged()) {
			TreeViewerColumn addColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
			addColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_ADD));
			addColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public void update(ViewerCell cell) {
					// Get item
					TreeItem item = (TreeItem) cell.getItem();
					Object element = cell.getElement();

					// View editor
					TreeEditor editor = null;

					if (element instanceof PhenomenonGroup && !addEditors.containsKey(item)) {

						// Button
						ButtonTheme btnAddItem = TableFactory
								.createAddButtonColumnAction(getViewController().getViewManager().getRscMgr(), cell);
						btnAddItem.addListener(SWT.Selection, event -> {
							getViewController().addPhenomenonAction((PhenomenonGroup) element);
							treeViewer.refresh(item);
						});

						// Draw cell
						editor = new TreeEditor(item.getParent());
						editor.grabHorizontal = true;
						editor.grabVertical = true;
						editor.setEditor(btnAddItem, item, cell.getColumnIndex());

						addEditors.put(item, editor);
					}
				}
			});
			treeViewerLayout.addColumnData(new ColumnPixelData(PartsResourceConstants.TABLE_ACTIONCOLUMN_WIDTH, true));
			columnProperties.add(RscTools.getString(RscConst.MSG_BTN_ADD));
		}

		// Actions view details
		TreeViewerColumn actionViewColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		actionViewColumn.getColumn().setText(PIRTPhenomenaTreePhenomena.getColumnActionViewProperty());
		actionViewColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object data = item.getData();
				Object element = cell.getElement();

				// View editor
				TreeEditor editor = null;

				if (!viewEditors.containsKey(element)) {

					// Button
					ButtonTheme btnViewItem = TableFactory
							.createViewButtonColumnAction(getViewController().getViewManager().getRscMgr(), cell);
					btnViewItem.addListener(SWT.Selection, event -> getViewController().viewElement(data));

					// Draw cell
					editor = new TreeEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnViewItem, item, cell.getColumnIndex());

					viewEditors.put(item, editor);
				}
			}
		});
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PIRT_PHEN_TABLEPHEN_ACTION_COLUMN_WIDTH, true));
		columnProperties.add(PIRTPhenomenaTreePhenomena.getColumnActionViewProperty());

		// Actions edit
		if (!getViewController().isTagged()) {
			TreeViewerColumn actionEditColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
			actionEditColumn.getColumn().setText(PIRTPhenomenaTreePhenomena.getColumnActionEditProperty());
			actionEditColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public void update(ViewerCell cell) {
					// Get item
					TreeItem item = (TreeItem) cell.getItem();
					Object data = item.getData();
					Object element = cell.getElement();

					// Edit editor
					TreeEditor editor = null;

					if (!editEditors.containsKey(element)) {

						// Button
						ButtonTheme btnEditItem = TableFactory
								.createEditButtonColumnAction(getViewController().getViewManager().getRscMgr(), cell);
						btnEditItem.addListener(SWT.Selection, event -> getViewController().updateElement(data));

						// Draw cell
						editor = new TreeEditor(item.getParent());
						editor.grabHorizontal = true;
						editor.grabVertical = true;
						editor.setEditor(btnEditItem, item, cell.getColumnIndex());

						editEditors.put(item, editor);
					}
				}
			});
			treeViewerLayout.addColumnData(
					new ColumnPixelData(PartsResourceConstants.PIRT_PHEN_TABLEPHEN_ACTION_COLUMN_WIDTH, true));
			columnProperties.add(PIRTPhenomenaTreePhenomena.getColumnActionEditProperty());
		}

		// Actions delete
		if (!getViewController().isTagged()) {
			TreeViewerColumn actionDeleteColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
			actionDeleteColumn.getColumn().setText(PIRTQoITableQoI.getColumnActionDeleteProperty());
			actionDeleteColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public void update(ViewerCell cell) {
					// Get item
					TreeItem item = (TreeItem) cell.getItem();
					Object element = cell.getElement();

					// Delete Button
					TreeEditor editor = null;

					if (!deleteEditors.containsKey(element)) {

						// Button
						ButtonTheme btnDeleteItem = TableFactory
								.createDeleteButtonColumnAction(getViewController().getViewManager().getRscMgr(), cell);
						btnDeleteItem.addListener(SWT.Selection, event -> getViewController().deleteElement(element));

						// Draw cell
						editor = new TreeEditor(item.getParent());
						editor.grabHorizontal = true;
						editor.grabVertical = true;
						editor.setEditor(btnDeleteItem, item, cell.getColumnIndex());

						deleteEditors.put(item, editor);
					}
				}
			});
			treeViewerLayout.addColumnData(
					new ColumnPixelData(PartsResourceConstants.PIRT_PHEN_TABLEPHEN_ACTION_COLUMN_WIDTH, true));
			columnProperties.add(PIRTPhenomenaTreePhenomena.getColumnActionDeleteProperty());
		}
	}

	/**
	 * Add tree viewer events
	 */
	private void renderMainTableAddEvents() {
		// Get tree
		Tree tree = treeViewer.getTree();

		tree.addListener(SWT.MeasureItem, new Listener() {

			private TreeItem previousItem = null;

			@Override
			public void handleEvent(Event event) {
				event.height = PartsResourceConstants.TABLE_ROW_HEIGHT;
				TreeItem item = (TreeItem) event.item;
				if (item != null && !item.equals(previousItem)) {
					previousItem = item;
					refreshTableButtons(item);
				}
			}
		});

		// tree view on double click
		ColumnViewerSupport.enableDoubleClickEditing(treeViewer);

		// Listener - Selection change
		treeViewer.addSelectionChangedListener(event -> {
			if (getViewController().getQoISelected() == null) {
				disableTreeSelection(treeViewer);
			}
		});

		// Listener - Double click open dialog to update phenomenon or group
		treeViewer.addDoubleClickListener(event -> {
			// Get selection
			ISelection selection = treeViewer.getSelection();
			if (selection != null && !selection.isEmpty()) {
				// Get element
				Object firstObjectSelected = ((IStructuredSelection) selection).getFirstElement();
				if (getViewController().getQoISelected() != null && firstObjectSelected != null) {
					getViewController().viewElement(firstObjectSelected);
				}
			}
		});

		// disable selection when clicking on the container
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				tableHeaderBar.getTableHeader().setSelection(new StructuredSelection());
				treeViewer.setSelection(new StructuredSelection());
			}
		});
	}

	/**
	 * Render footer buttons
	 */
	private void renderFooterButtons() {
		// Footer buttons - Composite
		Composite compositeButtonsFooter = new Composite(this, SWT.FILL);
		GridLayout gridLayoutButtonsHeader = new GridLayout(2, false);
		compositeButtonsFooter.setLayout(gridLayoutButtonsHeader);
		compositeButtonsFooter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		// Composite for Footer left buttons
		Composite compositeButtonsFooterLeft = new Composite(compositeButtonsFooter, SWT.NONE);
		compositeButtonsFooterLeft.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsFooterLeft.setLayout(new RowLayout());

		// Composite for header right buttons
		Composite compositeButtonsFooterRight = new Composite(compositeButtonsFooter, SWT.NONE);
		compositeButtonsFooterRight.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));
		compositeButtonsFooterRight.setLayout(new RowLayout());

		// Footer buttons - Back - Create
		Map<String, Object> btnBackOptions = new HashMap<>();
		btnBackOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BACK));
		btnBackOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnBackOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_BACK);
		btnBackOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnBackOptions.put(ButtonTheme.OPTION_LISTENER,
				(Listener) event -> getViewController().getViewManager().openHomePage());
		new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtonsFooterLeft,
				SWT.PUSH | SWT.CENTER, btnBackOptions);

		// button Close
		Map<String, Object> btnCloseOptions = new HashMap<>();
		btnCloseOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_CLOSE));
		btnCloseOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnCloseOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_CLOSE);
		btnCloseOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnCloseOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> getViewController().getViewManager()
				.closePage(getViewController().getQoISelected()));
		new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtonsFooterLeft,
				SWT.PUSH | SWT.CENTER, btnCloseOptions);

		// Button - Guidance Level
		Map<String, Object> btnHelpLevelOptions = new HashMap<>();
		btnHelpLevelOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_LVLGUIDANCE));
		btnHelpLevelOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_HELP);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLUE);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_LISTENER,
				(Listener) event -> getViewController().getViewManager().openPIRTHelpLevelView());
		new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtonsFooterLeft,
				SWT.PUSH | SWT.CENTER, btnHelpLevelOptions);

		// Footer buttons - Help - Create
		Map<String, Object> btnHelpOptions = new HashMap<>();
		btnHelpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		btnHelpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnHelpOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> HelpTools.openContextualHelp());
		new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER,
				btnHelpOptions);
		HelpTools.addContextualHelp(compositeButtonsFooter, ContextualHelpId.PIRT_PHENOMENA_VIEW);

		// Button - Tag
		Map<String, Object> btnTagOptions = new HashMap<>();
		btnTagOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_TAG));
		btnTagOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnTagOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_TAG);
		btnTagOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BROWN);
		btnTagOptions.put(ButtonTheme.OPTION_ENABLED, !getViewController().isTagged());
		btnTagOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> getViewController().doTagAction());
		ButtonTheme btnTag = new ButtonTheme(getViewController().getViewManager().getRscMgr(),
				compositeButtonsFooterRight, SWT.PUSH | SWT.CENTER, btnTagOptions);
		btnTag.setToolTipText(RscTools.getString(RscConst.MSG_PHENOMENAVIEW_BTN_ADDTAG_TOOLTIP));

		// Hide right footer on tagged qoi
		if (getViewController().isTagged()) {
			compositeButtonsFooterRight.setVisible(false);
		}

		// layout view
		compositeButtonsFooter.layout();
	}

	/**
	 * Add drag and drop support to the tree
	 */
	private void addDragAndDropSupport() {

		// drag support
		Transfer[] transferTypesDrag = new Transfer[] { LocalSelectionTransfer.getTransfer() };
		treeViewer.addDragSupport(DND.DROP_MOVE, transferTypesDrag, new DragSourceAdapter() {

			@Override
			public void dragSetData(DragSourceEvent event) {
				IStructuredSelection selection = treeViewer.getStructuredSelection();
				Object firstElement = selection.getFirstElement();

				if (LocalSelectionTransfer.getTransfer().isSupportedType(event.dataType)
						&& firstElement instanceof Phenomenon) {
					event.data = firstElement;
				}
			}
		});

		// drop support
		Transfer[] transferTypesDrop = new Transfer[] { LocalSelectionTransfer.getTransfer() };
		treeViewer.addDropSupport(DND.DROP_MOVE, transferTypesDrop,
				new PIRTPhenomenonDropSupport(getViewController(), treeViewer));
	}

	/**
	 * Refresh the main table
	 */
	void refreshMainTable() {

		// dispose the table components
		if (treeViewer != null && treeViewer.getTree() != null && !treeViewer.getTree().isDisposed()) {
			treeViewer.getTree().removeAll();
			treeViewer.getTree().dispose();
		}
		treeViewer = null;

		// Refresh
		ViewTools.refreshTableEditors(addEditors);
		ViewTools.refreshTableEditors(editEditors);
		ViewTools.refreshTableEditors(viewEditors);
		ViewTools.refreshTableEditors(deleteEditors);

		// render the main table
		renderMainTable();

		// relayout the table composite
		compositeTable.layout();
	}

	/**
	 * Refresh the table buttons
	 * 
	 * @param item
	 */
	private void refreshTableButtons(TreeItem item) {
		ViewTools.refreshTreeEditor(addEditors.get(item));
		ViewTools.refreshTreeEditor(editEditors.get(item));
		ViewTools.refreshTreeEditor(viewEditors.get(item));
		ViewTools.refreshTreeEditor(deleteEditors.get(item));
	}

	/** {@inheritDoc} */
	@Override
	public void reload() {
		getViewController().reloadData();
	}

	/**
	 * Disable selection on @param treeViewerPhenomena and set focus on this view
	 * 
	 * @param treeViewerPhenomena the tree viewer
	 */
	private void disableTreeSelection(TreeViewerHideSelection treeViewerPhenomena) {
		if (treeViewerPhenomena != null && treeViewerPhenomena.getTree() != null) {
			treeViewerPhenomena.getTree().deselectAll();
			this.setFocus();
			this.forceFocus();
		}
	}

	/**
	 * The PIRT Phenomena column label provider
	 * 
	 * @author Didier Verstraete
	 *
	 */
	private class PIRTPhenomenaColumnLabelProvider extends ColumnLabelProvider {

		@Override
		public Color getBackground(Object element) {
			return (element instanceof PhenomenonGroup)
					? ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
							ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT))
					: null;
		}

		@Override
		public Color getForeground(Object element) {
			return (element instanceof PhenomenonGroup)
					? ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
							ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE))
					: null;
		}
	}

	/**
	 * @param group the group to get the id for
	 * @return the value of the id column for the group in parameter
	 */
	String getIdColumnTextPhenomenonGroup(PhenomenonGroup group) {
		if (group == null) {
			return null;
		}
		return treeViewer.getIdColumnText(group);
	}

	/**
	 * @param phenomenon the phenomenon to get the id for
	 * @return the value of the id column for the phenomenon in parameter
	 */
	String getIdColumnTextPhenomenon(Phenomenon phenomenon) {
		if (phenomenon == null) {
			return null;
		}
		return treeViewer.getIdColumnText(phenomenon);
	}

	/**
	 * Keep the current expanded elements and expand the element in parameter
	 * 
	 * @param parent the group to expand with its children
	 */
	void expandElements(PhenomenonGroup parent) {
		Object[] elements = treeViewer.getExpandedElements();
		List<Object> elementsList = new ArrayList<>(Arrays.asList(elements));
		if (parent != null) {
			elementsList.add(parent);
		}
		treeViewer.setExpandedElements(elementsList.toArray());
	}

	/**
	 * Update the table header bar name
	 * 
	 * @param name the new name of the table bar
	 */
	void updateTableHeaderBarName(String name) {
		tableHeaderBar.updateHeaderBarName(name);
	}

	/**
	 * Sets the table header data.
	 *
	 * @param data the new table header data
	 */
	void setTableHeaderData(Object data) {
		if (tableHeaderBar != null) {
			tableHeaderBar.setInput(data);
		}
	}

	/**
	 * Gets the tree expanded elements.
	 *
	 * @return the tree expanded elements
	 */
	Object[] getTreeExpandedElements() {
		Object[] elements = (new ArrayList<Object>()).toArray();
		if (treeViewer != null) {
			elements = treeViewer.getExpandedElements();
		}
		return elements;
	}

	/**
	 * Sets the tree expanded elements.
	 *
	 * @param elements the new tree expanded elements
	 */
	void setTreeExpandedElements(Object[] elements) {
		if (elements == null || elements.length == 0) {
			treeViewer.expandAll();
		} else {
			treeViewer.setExpandedElements(elements);
		}
		treeViewer.refresh();
	}

	/**
	 * Sets the tree data.
	 *
	 * @param data the new tree data
	 */
	void setTreeData(Object data) {
		if (treeViewer != null) {
			treeViewer.setInput(data);
		}
	}

	/**
	 * Gets the tree data.
	 *
	 * @return the tree data
	 */
	Object getTreeData() {
		return treeViewer != null ? treeViewer.getInput() : null;
	}

}
