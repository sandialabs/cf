/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.qoiplanning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.User;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.listeners.ViewerSelectionKeepBackgroundColor;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.ui.ACredibilitySubView;
import gov.sandia.cf.parts.ui.MainViewManager;
import gov.sandia.cf.parts.ui.pirt.QoIDropSupport;
import gov.sandia.cf.parts.ui.pirt.editors.QoIDescriptionLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.QoILabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.QoINameLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.QoITreeContentProvider;
import gov.sandia.cf.parts.viewer.PIRTQoITableQoI;
import gov.sandia.cf.parts.viewer.TableFactory;
import gov.sandia.cf.parts.viewer.TreeViewerID;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.parts.viewer.editors.ColumnViewerSupport;
import gov.sandia.cf.parts.viewer.editors.GenericTableLabelProvider;
import gov.sandia.cf.parts.widgets.FancyToolTipSupport;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * QoI Planning view: it is the QoI Planning home page to select, open and
 * delete a QoI and its planning.
 * 
 * @author Didier Verstraete
 *
 */
public class QoIPlanningView extends ACredibilitySubView<QoIPlanningViewManager> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(QoIPlanningView.class);

	/**
	 * The view controller
	 */
	private QoIPlanningViewController viewCtrl;

	/**
	 * The main table composite
	 */
	private Composite compositeTable;

	/**
	 * the tree viewer
	 */
	private TreeViewerID treeViewer;

	/**
	 * Button list
	 */
	private Map<TreeItem, TreeEditor> viewEditors;
	private Map<TreeItem, TreeEditor> editEditors;
	private Map<TreeItem, TreeEditor> duplicateEditors;
	private Map<TreeItem, TreeEditor> tagEditors;
	private Map<TreeItem, TreeEditor> deleteEditors;

	/**
	 * The constructor
	 * 
	 * @param viewManager The view manager
	 * @param style       The view style
	 */
	public QoIPlanningView(QoIPlanningViewManager viewManager, int style) {
		super(viewManager, viewManager, style);

		this.viewCtrl = new QoIPlanningViewController(this);

		// Make sure you dispose these buttons when viewer input changes
		this.viewEditors = new HashMap<>();
		this.editEditors = new HashMap<>();
		this.duplicateEditors = new HashMap<>();
		this.tagEditors = new HashMap<>();
		this.deleteEditors = new HashMap<>();

		// create the view
		renderPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_QOIPLANNING_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_QOIPLANNING_ITEM);
	}

	/**
	 * Creates the page
	 */
	private void renderPage() {

		logger.debug("Render QoI Planning page"); //$NON-NLS-1$

		// Render header table
		renderHeaderButtons();

		// Render main table
		renderMainTableComposite();

		// Render footer buttons
		renderFooterButtons();

		// Refresh
		refresh();
	}

	/**
	 * Render header buttons
	 */
	private void renderHeaderButtons() {

		logger.debug("Render QoI Planning header buttons"); //$NON-NLS-1$

		/**
		 * Header buttons
		 */
		// Header buttons - Composite
		Composite compositeButtonsHeader = new Composite(this, SWT.FILL);
		GridLayout gridLayoutButtonsHeader = new GridLayout(2, false);
		compositeButtonsHeader.setLayout(gridLayoutButtonsHeader);
		compositeButtonsHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		// Composite for header left buttons
		Composite compositeButtonsHeaderLeft = new Composite(compositeButtonsHeader, SWT.NONE);
		compositeButtonsHeaderLeft.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsHeaderLeft.setLayout(new RowLayout());

		// Composite for header right buttons
		Composite compositeButtonsHeaderRight = new Composite(compositeButtonsHeader, SWT.NONE);
		compositeButtonsHeaderRight.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsHeaderRight.setLayout(new RowLayout());

		// Button - Add qoi
		Map<String, String> btnAddQoIData = new HashMap<>();
		btnAddQoIData.put(MainViewManager.BTN_EVENT_PROPERTY, QoIPlanningViewManager.BTN_EVENT_ADD_QOI);
		Map<String, Object> btnAddQoIOptions = new HashMap<>();
		btnAddQoIOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_PIRT_BTN_ADD));
		btnAddQoIOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnAddQoIOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_ADD);
		btnAddQoIOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
		btnAddQoIOptions.put(ButtonTheme.OPTION_DATA, btnAddQoIData);
		btnAddQoIOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> viewCtrl.addQuantityOfInterest());
		new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsHeaderRight, SWT.CENTER, btnAddQoIOptions);
	}

	/**
	 * Render footer buttons
	 */
	private void renderFooterButtons() {

		logger.debug("Render QoI Planning footer buttons"); //$NON-NLS-1$

		// Footer buttons - Composite
		Composite compositeButtons = new Composite(this, SWT.NONE);
		compositeButtons.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtons.setLayout(new RowLayout());

		// Footer buttons - Back - Create
		Map<String, Object> btnBackOptions = new HashMap<>();
		btnBackOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BACK));
		btnBackOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnBackOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_BACK);
		btnBackOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		ButtonTheme btnBack = new ButtonTheme(getViewManager().getRscMgr(), compositeButtons, SWT.CENTER,
				btnBackOptions);

		// Footer buttons - Help - Create
		Map<String, Object> btnHelpOptions = new HashMap<>();
		btnHelpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		btnHelpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnHelpOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> HelpTools.openContextualHelp());

		ButtonTheme btnHelp = new ButtonTheme(getViewManager().getRscMgr(), compositeButtons, SWT.CENTER,
				btnHelpOptions);
		RowData btnLayoutData = new RowData();
		btnHelp.setLayoutData(btnLayoutData);
		HelpTools.addContextualHelp(compositeButtons, ContextualHelpId.QOIPLANNING);

		// Footer buttons - Back - plug
		getViewManager().plugBackHomeButton(btnBack);

		// layout view
		compositeButtons.layout();
	}

	/**
	 * Render main table composite
	 */
	private void renderMainTableComposite() {

		logger.debug("Render QoI Planning main table composite"); //$NON-NLS-1$

		// Main table composite
		compositeTable = new Composite(this, SWT.FILL);
		GridLayout gridLayout = new GridLayout(1, true);
		compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeTable.setLayout(gridLayout);
	}

	/**
	 * Render evidence tree
	 */
	private void renderMainTable() {

		logger.debug("Render QoI Planning main table"); //$NON-NLS-1$

		// Initialize table
		renderMainTableInit();

		// Initialize data
		List<String> columnProperties = new ArrayList<>();
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) treeViewer.getTree().getLayout();

		// Columns - Id
		columnProperties.add(treeViewer.getIdColumn().getColumn().getText());
		treeViewer.getIdColumn().setLabelProvider(new QoILabelProvider(getViewManager().getRscMgr()) {
			@Override
			public String getText(Object element) {
				if (element instanceof QuantityOfInterest) {
					if (StringUtils.isBlank(((QuantityOfInterest) element).getGeneratedId())) {
						((QuantityOfInterest) element).setGeneratedId(treeViewer.getIdColumnText(element));
					}
					return ((QuantityOfInterest) element).getGeneratedId();
				}
				return treeViewer.getIdColumnText(element);
			}
		});

		// Columns - Symbol
		TreeViewerColumn symbolColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		symbolColumn.getColumn().setText(PIRTQoITableQoI.getColumnSymbolProperty());
		symbolColumn.setLabelProvider(new QoINameLabelProvider(getViewManager().getRscMgr()));
		treeViewerLayout
				.addColumnData(new ColumnWeightData(PartsResourceConstants.GENPARAM_TABLE_TEXT_COLUMN_COEFF, true));
		columnProperties.add(PIRTQoITableQoI.getColumnSymbolProperty());

		// Columns - Description
		TreeViewerColumn descriptionColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		descriptionColumn.getColumn().setText(PIRTQoITableQoI.getColumnDescriptionProperty());
		descriptionColumn.setLabelProvider(new QoIDescriptionLabelProvider(getViewManager().getRscMgr()));
		treeViewerLayout
				.addColumnData(new ColumnWeightData(PartsResourceConstants.GENPARAM_TABLE_TEXT_COLUMN_COEFF, true));
		columnProperties.add(PIRTQoITableQoI.getColumnDescriptionProperty());

		// Columns - Parameters
		if (getViewManager().getCache().getQoIPlanningSpecification() != null) {
			for (QoIPlanningParam parameter : getViewManager().getCache().getQoIPlanningSpecification()
					.getParameters()) {
				TreeViewerColumn treeCol = TableFactory.createGenericParamTreeColumn(parameter, treeViewer,
						new GenericTableLabelProvider(parameter, getViewManager()) {

							private QoILabelProvider labelprovider;

							private QoILabelProvider getPIRTQoILabelProvider() {
								if (labelprovider == null) {
									labelprovider = new QoILabelProvider(getViewManager().getRscMgr());
								}
								return labelprovider;
							}

							@Override
							public Color getBackground(Object element) {
								return getPIRTQoILabelProvider().getBackground(element);
							}

							@Override
							public Color getForeground(Object element) {
								return getPIRTQoILabelProvider().getForeground(element);
							}
						});
				if (treeCol != null) {
					columnProperties.add(treeCol.getColumn().getText());
				}
			}
		}

		// Columns - Actions
		renderMainTableActionColumns(columnProperties);

		// Add listeners
		renderMainTableAddEvents();

		// Tree - Properties
		treeViewer.setColumnProperties(columnProperties.stream().toArray(String[]::new));
		treeViewer.setContentProvider(new QoITreeContentProvider());

		// add drag and drop support on tree to move, reorder
		addDragAndDropSupport();

		// Refresh
		treeViewer.refresh(true);

		// SWT.EraseItem: this event is called when repainting the tree not on removing
		// an element of the tree
		// In this case, it keeps cell colors when selection is active on cell
		treeViewer.getTree().addListener(SWT.EraseItem, new ViewerSelectionKeepBackgroundColor(treeViewer) {

			@Override
			public boolean isConditionFulfilled(Object data) {
				return data instanceof QuantityOfInterest;
			}
		});

		// Layout
		treeViewer.getTree().layout();
	}

	/**
	 * Initialize Main table
	 */
	private void renderMainTableInit() {
		// Tree - Create
		treeViewer = new TreeViewerID(compositeTable,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		GridData gdViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);

		Tree tree = treeViewer.getTree();
		tree.setLayoutData(gdViewer);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		FancyToolTipSupport.enableFor(treeViewer, ToolTip.NO_RECREATE);

		// Tree - Layout
		final AutoResizeViewerLayout viewerLayout = new AutoResizeViewerLayout(treeViewer);
		treeViewer.setLayout(viewerLayout);

		// Tree - Hint
		gdViewer.heightHint = tree.getItemHeight();
		gdViewer.widthHint = getViewManager().getSize().x
				- 2 * ((GridLayout) compositeTable.getLayout()).horizontalSpacing;

		// Tree - Customize
		tree.setHeaderBackground(ColorTools.toColor(getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
		tree.setHeaderForeground(ColorTools.toColor(getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));
	}

	/**
	 * Add action columns
	 * 
	 * @param columnProperties the column properties to render action columns
	 */
	private void renderMainTableActionColumns(List<String> columnProperties) {

		// Get Tree and layout
		Tree tree = treeViewer.getTree();
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) tree.getLayout();

		// Tree - Column - Action View
		TreeViewerColumn viewColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		viewColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_VIEW));
		viewColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// View editor
				TreeEditor editor = null;

				if (!viewEditors.containsKey(item)) {

					// Button
					ButtonTheme btnViewItem = TableFactory.createViewButtonColumnAction(getViewManager().getRscMgr(),
							cell);
					btnViewItem.addListener(SWT.Selection, event -> viewCtrl.viewElement(element));

					// Draw cell
					editor = new TreeEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnViewItem, item, cell.getColumnIndex());

					viewEditors.put(item, editor);
				}
				// set cell background
				cell.setBackground(getBackground(element));
				cell.setForeground(getForeground(element));
			}
		});
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_VIEW));

		// Tree - Column - Action Edit
		TreeViewerColumn editColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		editColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_EDIT));
		editColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// View editor
				TreeEditor editor = null;

				if (!editEditors.containsKey(item) && element instanceof QuantityOfInterest
						&& null == ((QuantityOfInterest) element).getTagDate()) {

					// Button
					ButtonTheme btnEditItem = TableFactory.createEditButtonColumnAction(getViewManager().getRscMgr(),
							cell);
					btnEditItem.addListener(SWT.Selection, event -> viewCtrl.updateElement(element));

					// Draw cell
					editor = new TreeEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnEditItem, item, cell.getColumnIndex());

					editEditors.put(item, editor);
				}
				// set cell background
				cell.setBackground(getBackground(element));
				cell.setForeground(getForeground(element));
			}
		});
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_EDIT));

		// Tree - Column - Action Duplicate
		TreeViewerColumn duplicateColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		duplicateColumn.getColumn().setText(PIRTQoITableQoI.getColumnActionDuplicateProperty());
		duplicateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Duplicate editor
				TreeEditor editor = null;

				// Duplicate is available for not tagged qoi only
				if (!duplicateEditors.containsKey(item) && element instanceof QuantityOfInterest
						&& null == ((QuantityOfInterest) element).getTagDate()) {

					// Button
					ButtonTheme btnDuplicateQoI = TableFactory
							.createCopyButtonColumnAction(getViewManager().getRscMgr(), cell);
					btnDuplicateQoI.addListener(SWT.Selection, event -> viewCtrl.duplicateQuantityOfInterest(element));

					// Draw cell
					editor = new TreeEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnDuplicateQoI, item, cell.getColumnIndex());

					duplicateEditors.put(item, editor);
				}
				// set cell background
				cell.setBackground(getBackground(element));
				cell.setForeground(getForeground(element));
			}
		});
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.QOIHOME_VIEW_TABLEPHEN_ACTION_COLUMN_WIDTH, false));
		columnProperties.add(PIRTQoITableQoI.getColumnActionDuplicateProperty());

		// Tree - Column - Action Tag
		TreeViewerColumn tagColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		tagColumn.getColumn().setText(PIRTQoITableQoI.getColumnActionTagProperty());
		tagColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Tag editor
				TreeEditor editor = null;

				// Tag is available for not tagged qoi only
				if (!tagEditors.containsKey(item) && element instanceof QuantityOfInterest
						&& null == ((QuantityOfInterest) element).getTagDate()) {

					// Button
					ButtonTheme btnTagQoI = TableFactory.createTagButtonColumnAction(getViewManager().getRscMgr(),
							cell);
					btnTagQoI.addListener(SWT.Selection, event -> viewCtrl.doTagAction(element));
					btnTagQoI.setToolTipText(RscTools.getString(RscConst.MSG_PHENOMENAVIEW_BTN_ADDTAG_TOOLTIP));

					// Draw cell
					editor = new TreeEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnTagQoI, item, cell.getColumnIndex());

					tagEditors.put(item, editor);
				}
				// set cell background
				cell.setBackground(getBackground(element));
				cell.setForeground(getForeground(element));
			}
		});
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.QOIHOME_VIEW_TABLEPHEN_ACTION_COLUMN_WIDTH, false));
		columnProperties.add(PIRTQoITableQoI.getColumnActionTagProperty());

		// Tree - Column - Action delete
		TreeViewerColumn actionDeleteColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		actionDeleteColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_DELETE));
		actionDeleteColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				boolean canDelete = false;

				if (element instanceof QuantityOfInterest) {
					// Cast QoI
					QuantityOfInterest qoi = (QuantityOfInterest) element;

					// Get current user
					User currentUser = getViewManager().getCache().getUser();

					// Can delete - not tagged
					canDelete = (null == qoi.getTagDate());

					// No user assign to tag (old project)
					canDelete |= (null == qoi.getTagUserCreation());

					// Can delete - Only the person that created the tag can delete it
					canDelete |= (null != qoi.getTagDate() && null != qoi.getTagUserCreation()
							&& qoi.getTagUserCreation().getId().equals(currentUser.getId()));
				}

				// Delete Editor
				TreeEditor editor = null;

				if (canDelete && !deleteEditors.containsKey(item)) {

					// Button
					ButtonTheme btnDeleteItem = TableFactory
							.createDeleteButtonColumnAction(getViewManager().getRscMgr(), cell);
					btnDeleteItem.addListener(SWT.Selection, event -> viewCtrl.deleteElement(element));

					// Draw cell
					editor = new TreeEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnDeleteItem, item, cell.getColumnIndex());

					deleteEditors.put(item, editor);
				}

				// set cell background
				cell.setBackground(getBackground(element));
				cell.setForeground(getForeground(element));
			}
		});
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_DELETE));

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

		// Tree - Listener - Double Click
		tree.addListener(SWT.MouseDoubleClick, event -> {
			if (event.type == SWT.MouseDoubleClick) {
				viewCtrl.viewElement(getSelected());
			}
		});

		ColumnViewerSupport.enableDoubleClickEditing(treeViewer);

		// disable selection when clicking on the container
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				treeViewer.setSelection(new StructuredSelection());
			}
		});
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
						&& firstElement instanceof QuantityOfInterest) {
					event.data = firstElement;
				}
			}
		});

		// drop support
		Transfer[] transferTypesDrop = new Transfer[] { LocalSelectionTransfer.getTransfer() };
		treeViewer.addDropSupport(DND.DROP_MOVE, transferTypesDrop, new QoIDropSupport(viewCtrl, treeViewer));
	}

	/**
	 * Refresh view
	 */
	void refreshViewer() {

		logger.debug("Refresh QoI Planning tree"); //$NON-NLS-1$

		// Get tree data
		@SuppressWarnings("unchecked")
		List<QuantityOfInterest> input = (List<QuantityOfInterest>) treeViewer.getInput();

		// sort all lists
		if (input != null) {
			input.sort(Comparator.comparing(QuantityOfInterest::getId));
		}

		treeViewer.refresh(true);
	}

	/**
	 * Refresh the main table
	 */
	private void refreshMainTable() {
		// Dispose the table components
		if (treeViewer != null && treeViewer.getTree() != null && !treeViewer.getTree().isDisposed()) {
			treeViewer.getTree().removeAll();
			treeViewer.getTree().dispose();
		}
		treeViewer = null;

		// Refresh
		ViewTools.refreshTableEditors(editEditors);
		ViewTools.refreshTableEditors(viewEditors);
		ViewTools.refreshTableEditors(tagEditors);
		ViewTools.refreshTableEditors(duplicateEditors);
		ViewTools.refreshTableEditors(deleteEditors);

		// render the main table
		renderMainTable();

		// re-layout the table composite
		compositeTable.layout();
	}

	/**
	 * Refresh the table buttons
	 * 
	 * @param item the tree item to refresh
	 */
	private void refreshTableButtons(TreeItem item) {
		ViewTools.refreshTreeEditor(editEditors.get(item));
		ViewTools.refreshTreeEditor(viewEditors.get(item));
		ViewTools.refreshTreeEditor(tagEditors.get(item));
		ViewTools.refreshTreeEditor(duplicateEditors.get(item));
		ViewTools.refreshTreeEditor(deleteEditors.get(item));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {
		// Get Model
		Model model = getViewManager().getCache().getModel();
		List<QuantityOfInterest> qoiList = new ArrayList<>();

		// Get data
		if (model != null) {
			// Get list of qoi
			qoiList = this.getViewManager().getAppManager().getService(IPIRTApplication.class).getRootQoI(model);

			// reload qoi planning specification
			getViewManager().getCache().reloadQoIPlanningSpecification();
		}

		/**
		 * Refresh the table
		 */
		// Get expanded elements
		Object[] elements = (new ArrayList<Object>()).toArray();
		boolean initialization = true;
		if (treeViewer != null) {
			initialization = false;
			elements = treeViewer.getExpandedElements();
		}

		// Refresh the table
		refreshMainTable();

		// Set input
		treeViewer.setInput(qoiList);

		// Set expanded elements
		if (initialization) {
			treeViewer.expandAll();
		} else {
			treeViewer.setExpandedElements(elements);
		}
		treeViewer.refresh();
	}

	/**
	 * Get First Requirement Selected
	 * 
	 * @return the first evidence selected of the evidence viewer
	 */
	private QuantityOfInterest getSelected() {
		ISelection selection = treeViewer.getSelection();
		if (selection != null && !selection.isEmpty()) {
			Object elt = ((IStructuredSelection) selection).getFirstElement();
			if (elt instanceof QuantityOfInterest) {
				return (QuantityOfInterest) elt;
			}
		}
		return null;
	}

}
