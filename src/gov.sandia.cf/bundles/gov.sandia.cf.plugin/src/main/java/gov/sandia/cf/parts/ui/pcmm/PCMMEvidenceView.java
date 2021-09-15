/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewer;
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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.listeners.ViewerSelectionKeepBackgroundColor;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMEvidenceDropSupport;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMEvidenceFilenameColumnLabelProvider;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMEvidenceTreeContentProvider;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMEvidenceTreeSimplifiedContentProvider;
import gov.sandia.cf.parts.viewer.TableFactory;
import gov.sandia.cf.parts.viewer.TreeViewerHideSelection;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.parts.viewer.editors.ColumnViewerSupport;
import gov.sandia.cf.parts.widgets.FancyToolTipSupport;
import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * The PCMM Evidence View
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMEvidenceView extends ACredibilityPCMMView {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMEvidenceView.class);

	/**
	 * Controller
	 */
	private PCMMEvidenceViewController viewCtrl;

	/**
	 * the pcmm element
	 */
	private PCMMElement elementSelected;
	/**
	 * the pcmm elements
	 */
	private List<PCMMElement> elements;
	/**
	 * the viewer
	 */
	private TreeViewerHideSelection treeViewerEvidence;

	/**
	 * The last selected file
	 */
	private IFile lastSelectedFile = null;

	/**
	 * Table editors
	 */
	private Map<TreeItem, TreeEditor> addEditors;
	private Map<TreeItem, TreeEditor> viewEditors;
	private Map<TreeItem, TreeEditor> editEditors;
	private Map<TreeItem, TreeEditor> deleteEditors;

	/**
	 * The aggregate table composite
	 */
	private Composite compositeTable;

	/**
	 * The constructor
	 * 
	 * @param parentView      the parent view
	 * @param elementSelected the element selected
	 * @param style           the style
	 */
	public PCMMEvidenceView(PCMMViewManager parentView, PCMMElement elementSelected, int style) {
		super(parentView, parentView, style);
		this.elementSelected = elementSelected;

		this.viewCtrl = new PCMMEvidenceViewController(this);

		// Make sure you dispose these buttons when viewer input changes
		addEditors = new HashMap<>();
		editEditors = new HashMap<>();
		viewEditors = new HashMap<>();
		deleteEditors = new HashMap<>();

		// lists and maps instantiation
		elements = new ArrayList<>();

		// create the view
		renderPage();
	}

	/** {@inheritDoc} */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_PCMMEVID_TITLE, RscTools.getString(RscConst.MSG_TITLE_EMPTY));
	}

	/** {@inheritDoc} */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_PCMMEVID_ITEM_TITLE);
	}

	/**
	 * Creates the phenomena view and all its components
	 * 
	 * @param parent
	 * 
	 */
	private void renderPage() {

		logger.debug("Creating PCMM view components"); //$NON-NLS-1$

		// Reset title
		setTitle(RscTools.getString(RscConst.MSG_PCMMEVID_TITLE,
				this.elementSelected != null ? this.elementSelected.getName() : RscTools.empty()));

		// Render main table
		renderMainTableComposite();

		// Render footer
		renderFooter();

		// load view datas
		loadDatas();
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
	 * Refresh the main table
	 */
	private void refreshMainTable() {

		if (treeViewerEvidence != null) {
			// dispose the table components
			if (treeViewerEvidence.getTree() != null && !treeViewerEvidence.getTree().isDisposed()) {
				treeViewerEvidence.getTree().removeAll();
				treeViewerEvidence.getTree().dispose();
			}
			treeViewerEvidence = null;
		}
		if (addEditors != null) {
			addEditors.forEach((item, editor) -> ViewTools.disposeViewerEditor(editor));
			addEditors.clear();
		}
		if (viewEditors != null) {
			viewEditors.forEach((item, editor) -> ViewTools.disposeViewerEditor(editor));
			viewEditors.clear();
		}
		if (editEditors != null) {
			editEditors.forEach((item, editor) -> ViewTools.disposeViewerEditor(editor));
			editEditors.clear();
		}
		if (deleteEditors != null) {
			deleteEditors.forEach((item, editor) -> ViewTools.disposeViewerEditor(editor));
			deleteEditors.clear();
		}

		// render the main table
		renderMainTable();

		// relayout the table composite
		compositeTable.layout();
	}

	/**
	 * Render evidence tree
	 */
	private void renderMainTable() {

		// Initialize table
		renderMainTableInit();

		// Initialize data
		List<String> columnProperties = new ArrayList<>();

		// Columns - Tree input
		renderMainTableColumns(columnProperties);

		// Columns - Actions
		renderMainTableActionColumns(columnProperties);

		// Add listeners
		renderMainTableAddEvents();

		// Tree - Properties
		treeViewerEvidence.setColumnProperties(columnProperties.stream().toArray(String[]::new));
		if (PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())) {
			treeViewerEvidence.setContentProvider(new PCMMEvidenceTreeContentProvider(getViewManager()));
		} else if (PCMMMode.SIMPLIFIED.equals(getViewManager().getPCMMConfiguration().getMode())) {
			treeViewerEvidence.setContentProvider(new PCMMEvidenceTreeSimplifiedContentProvider(getViewManager()));
		}

		// Refresh
		treeViewerEvidence.refresh(true);

		// Layout
		treeViewerEvidence.getTree().layout();
	}

	/**
	 * Initialize Main table
	 */
	private void renderMainTableInit() {
		// Tree - Create
		treeViewerEvidence = new TreeViewerHideSelection(compositeTable,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		GridData gdViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		treeViewerEvidence.getTree().setLayoutData(gdViewer);
		treeViewerEvidence.getTree().setHeaderVisible(true);
		treeViewerEvidence.getTree().setLinesVisible(true);

		// Tree - add fancy tooltip support
		FancyToolTipSupport.enableFor(treeViewerEvidence, ToolTip.NO_RECREATE);

		// Tree - Layout
		final AutoResizeViewerLayout viewerLayout = new AutoResizeViewerLayout(treeViewerEvidence);
		treeViewerEvidence.getTree().setLayout(viewerLayout);

		// Tree - Hint
		gdViewer.heightHint = treeViewerEvidence.getTree().getItemHeight();
		gdViewer.widthHint = getViewManager().getSize().x
				- 2 * ((GridLayout) compositeTable.getLayout()).horizontalSpacing;

		// Tree - Customize
		treeViewerEvidence.getTree().setHeaderBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY));
		treeViewerEvidence.getTree().setHeaderForeground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));

	}

	/**
	 * Add main columns
	 * 
	 * @param columnProperties
	 */
	private void renderMainTableColumns(List<String> columnProperties) {

		// Get Tree and layout
		Tree tree = treeViewerEvidence.getTree();
		AutoResizeViewerLayout viewerLayout = (AutoResizeViewerLayout) tree.getLayout();

		// Tree - Columns - File Name
		TreeViewerColumn filenameColumn = new TreeViewerColumn(treeViewerEvidence, SWT.LEFT);
		filenameColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_FILENAME));
		filenameColumn.setLabelProvider(new PCMMEvidenceFilenameColumnLabelProvider(this));
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_NAMECOLUMN_WEIGHT, true));
		columnProperties.add(filenameColumn.getColumn().getText());

		// Tree - Columns - Path
		TreeViewerColumn pathColumn = new TreeViewerColumn(treeViewerEvidence, SWT.LEFT);
		pathColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_FILEPATH));
		pathColumn.setLabelProvider(new PCMMEvidenceColumnLabelProvider(this) {
			@Override
			public String getText(Object element) {
				return element instanceof PCMMEvidence && ((PCMMEvidence) element).getPath() != null
						? ((PCMMEvidence) element).getPath()
						: RscTools.empty();
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_PATHCOLUMN_WEIGHT, true));
		columnProperties.add(pathColumn.getColumn().getText());

		// Tree - Columns - Section
		TreeViewerColumn sectionColumn = new TreeViewerColumn(treeViewerEvidence, SWT.LEFT);
		sectionColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_SECTION));
		sectionColumn.setLabelProvider(new PCMMEvidenceColumnLabelProvider(this) {
			@Override
			public String getText(Object element) {
				return element instanceof PCMMEvidence && ((PCMMEvidence) element).getSection() != null
						? ((PCMMEvidence) element).getSection()
						: RscTools.empty();
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_SECTIONCOLUMN_WEIGHT, true));
		columnProperties.add(sectionColumn.getColumn().getText());

		// Tree - Columns - Description
		TreeViewerColumn descriptionColumn = new TreeViewerColumn(treeViewerEvidence, SWT.LEFT);
		descriptionColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_DESC));
		descriptionColumn.setLabelProvider(new PCMMEvidenceColumnLabelProvider(this) {
			@Override
			public String getText(Object element) {
				return (element instanceof PCMMEvidence && ((PCMMEvidence) element).getDescription() != null)
						? StringTools.clearHtml(((PCMMEvidence) element).getDescription(), true)
						: RscTools.empty();
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_DESCCOLUMN_WEIGHT, true));
		columnProperties.add(descriptionColumn.getColumn().getText());

		// Tree - Columns - User
		TreeViewerColumn userColumn = new TreeViewerColumn(treeViewerEvidence, SWT.LEFT);
		userColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_USER));
		userColumn.setLabelProvider(new PCMMEvidenceColumnLabelProvider(this) {
			@Override
			public String getText(Object element) {
				return element instanceof PCMMEvidence && ((PCMMEvidence) element).getUserCreation() != null
						? ((PCMMEvidence) element).getUserCreation().getUserID()
						: RscTools.empty();
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_USERCOLUMN_WEIGHT, true));
		columnProperties.add(userColumn.getColumn().getText());

		// Tree - Columns - Role
		TreeViewerColumn roleColumn = new TreeViewerColumn(treeViewerEvidence, SWT.LEFT);
		roleColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_ROLE));
		roleColumn.setLabelProvider(new PCMMEvidenceColumnLabelProvider(this) {
			@Override
			public String getText(Object element) {
				return element instanceof PCMMEvidence && ((PCMMEvidence) element).getRoleCreation() != null
						? ((PCMMEvidence) element).getRoleCreation().getName()
						: RscTools.empty();
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_ROLECOLUMN_WEIGHT, true));
		columnProperties.add(roleColumn.getColumn().getText());

	}

	/**
	 * Add action columns
	 * 
	 * @param columnProperties
	 */
	private void renderMainTableActionColumns(List<String> columnProperties) {

		// Get Tree and layout
		Tree tree = treeViewerEvidence.getTree();
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) tree.getLayout();

		// Tree - Column - Action Add
		if (!getViewManager().isTagMode()) {
			addMainTableAddActionColumn(columnProperties, treeViewerLayout);
		}

		// Tree - Column - Action View
		addMainTableViewActionColumn(columnProperties, treeViewerLayout);

		// Tree - Column - Action Edit
		if (!getViewManager().isTagMode()) {
			addMainTableEditActionColumn(columnProperties, treeViewerLayout);
		}

		// Tree - Column - Action delete
		if (!getViewManager().isTagMode()) {
			addMainTableDeleteActionColumn(columnProperties, treeViewerLayout);
		}
	}

	/**
	 * Add the main table add action column
	 * 
	 * @param columnProperties the column properties list
	 * @param treeViewerLayout the tree viewer layout
	 */
	private void addMainTableAddActionColumn(List<String> columnProperties, AutoResizeViewerLayout treeViewerLayout) {

		TreeViewerColumn actionAddColumn = new TreeViewerColumn(treeViewerEvidence, SWT.CENTER);
		actionAddColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_ADD));
		actionAddColumn.setLabelProvider(new PCMMEvidenceColumnLabelProvider(this) {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Add button
				TreeEditor editor = null;

				// Button Add for PCMM Evidence
				if (!addEditors.containsKey(item) && ((element instanceof PCMMSubelement
						&& PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())
						&& elementSelected.getSubElementList().contains(element))
						|| (element instanceof PCMMElement
								&& PCMMMode.SIMPLIFIED.equals(getViewManager().getPCMMConfiguration().getMode())
								&& elementSelected.equals(element)))) {

					// Button
					ButtonTheme btnAddItem = TableFactory.createAddButtonColumnAction(getViewManager().getRscMgr(),
							cell);

					// Footer buttons - Delete- Listener
					btnAddItem.addListener(SWT.Selection, event -> viewCtrl.addEvidence(element));

					// Draw cell
					editor = new TreeEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnAddItem, item, cell.getColumnIndex());

					addEditors.put(item, editor);
				}
				// set cell background
				cell.setBackground(getBackground(element));
				cell.setForeground(getForeground(element));
			}
		});
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_ADD));
	}

	/**
	 * Add the main table view action column
	 * 
	 * @param columnProperties the column properties list
	 * @param treeViewerLayout the tree viewer layout
	 */
	private void addMainTableViewActionColumn(List<String> columnProperties, AutoResizeViewerLayout treeViewerLayout) {

		TreeViewerColumn actionViewColumn = new TreeViewerColumn(treeViewerEvidence, SWT.CENTER);
		actionViewColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_OPEN));
		actionViewColumn.setLabelProvider(new PCMMEvidenceColumnLabelProvider(this) {

			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// View editor
				TreeEditor editor = null;

				// Button View for PCMM Evidence
				if (element instanceof PCMMEvidence && isFromCurrentPCMMElement((PCMMEvidence) element)
						&& !viewEditors.containsKey(item)) {

					// Button
					ButtonTheme btnViewItem = TableFactory.createOpenButtonColumnAction(getViewManager().getRscMgr(),
							cell);

					// Footer buttons - Delete- Listener
					btnViewItem.addListener(SWT.Selection,
							event -> getViewManager().openDocument((PCMMEvidence) element));

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
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_OPEN));
	}

	/**
	 * Add the main table edit action column
	 * 
	 * @param columnProperties the column properties list
	 * @param treeViewerLayout the tree viewer layout
	 */
	private void addMainTableEditActionColumn(List<String> columnProperties, AutoResizeViewerLayout treeViewerLayout) {

		TreeViewerColumn editColumn = new TreeViewerColumn(treeViewerEvidence, SWT.CENTER);
		editColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_EDIT));
		editColumn.setLabelProvider(new PCMMEvidenceColumnLabelProvider(this) {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Button View for PCMM Evidence
				if (element instanceof PCMMEvidence && isFromCurrentPCMMElement((PCMMEvidence) element)
						&& !getViewManager().isTagMode()) {

					// View editor
					TreeEditor editor = null;

					if (!editEditors.containsKey(item)) {

						// Button
						ButtonTheme btnEditItem = TableFactory
								.createEditButtonColumnAction(getViewManager().getRscMgr(), cell);
						btnEditItem.addListener(SWT.Selection, event -> viewCtrl.editEvidence((PCMMEvidence) element));

						// Draw cell
						editor = new TreeEditor(item.getParent());
						editor.grabHorizontal = true;
						editor.grabVertical = true;
						editor.setEditor(btnEditItem, item, cell.getColumnIndex());

						editEditors.put(item, editor);
					}
				}
				// set cell background
				cell.setBackground(getBackground(element));
				cell.setForeground(getForeground(element));
			}
		});
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_EDIT));
	}

	/**
	 * Add the main table delete action column
	 * 
	 * @param columnProperties the column properties list
	 * @param treeViewerLayout the tree viewer layout
	 */
	private void addMainTableDeleteActionColumn(List<String> columnProperties,
			AutoResizeViewerLayout treeViewerLayout) {

		TreeViewerColumn actionDeleteColumn = new TreeViewerColumn(treeViewerEvidence, SWT.CENTER);
		actionDeleteColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_DELETE));
		actionDeleteColumn.setLabelProvider(new PCMMEvidenceColumnLabelProvider(this) {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Button Delete for PCMM Evidence
				if (element instanceof PCMMEvidence && isFromCurrentPCMMElement((PCMMEvidence) element)) {

					// Delete Editor
					TreeEditor editor = null;

					if (!deleteEditors.containsKey(item)) {

						// Button
						ButtonTheme btnDeleteItem = TableFactory
								.createDeleteButtonColumnAction(getViewManager().getRscMgr(), cell);
						btnDeleteItem.addListener(SWT.Selection,
								event -> viewCtrl.deleteEvidence((PCMMEvidence) element));

						// Draw cell
						editor = new TreeEditor(item.getParent());
						editor.grabHorizontal = true;
						editor.grabVertical = true;
						editor.setEditor(btnDeleteItem, item, cell.getColumnIndex());

						deleteEditors.put(item, editor);
					}
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
		treeViewerEvidence.getTree().addListener(SWT.MeasureItem, new Listener() {

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
		treeViewerEvidence.getTree().addListener(SWT.MouseDoubleClick, event -> {
			if (event.type == SWT.MouseDoubleClick) {
				getViewManager().openDocument(getFirstEvidenceSelected());
			}
		});

		// Key press on tree events
		treeViewerEvidence.getTree().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {

				// Get evidence selected if there is one
				PCMMEvidence evidenceSelected = getFirstEvidenceSelected();
				// Open all selected document - ENTER
				if (null != evidenceSelected && (SWT.CR == event.keyCode || SWT.KEYPAD_CR == event.keyCode)) {
					// Get all evidence selected and Open document
					getEvidenceSelectedList().forEach(evidence -> getViewManager().openDocument(evidence));
				}
			}
		});

		// Add drag and drop support to the viewer
		addDragAndDropSupport();

		// modifications on double click
		if (!getViewManager().isTagMode()) {
			ColumnViewerSupport.enableDoubleClickEditing(treeViewerEvidence);
		}

		// disable selection when clicking on the container
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				treeViewerEvidence.setSelection(new StructuredSelection());
			}
		});

		// SWT.EraseItem: this event is called when repainting the tree not on removing
		// an element of the tree
		// In this case, it keeps cell colors when selection is active on cell
		treeViewerEvidence.getTree().addListener(SWT.EraseItem,
				new ViewerSelectionKeepBackgroundColor(treeViewerEvidence) {

					@Override
					public boolean isConditionFulfilled(Object data) {
						return data instanceof PCMMEvidence;
					}
				});
	}

	/**
	 * Render footer
	 */
	private void renderFooter() {
		// Footer buttons - Composite
		Composite compositeButtonsFooter = new Composite(this, SWT.FILL);
		GridLayout gridLayoutButtonsHeader = new GridLayout(2, true);
		compositeButtonsFooter.setLayout(gridLayoutButtonsHeader);
		compositeButtonsFooter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		// Composite for Footer left buttons
		Composite compositeButtonsFooterLeft = new Composite(compositeButtonsFooter, SWT.NONE);
		compositeButtonsFooterLeft.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsFooterLeft.setLayout(new RowLayout());

		// Composite for header right buttons
		Composite compositeButtonsFooterRight = new Composite(compositeButtonsFooter, SWT.NONE);
		compositeButtonsFooterRight.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsFooterRight.setLayout(new RowLayout());

		// Button Back - Create
		Map<String, Object> btnBackOptions = new HashMap<>();
		btnBackOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BACK));
		btnBackOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnBackOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_BACK);
		btnBackOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnBackOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> getViewManager().openLastView());
		new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER, btnBackOptions);

		// Button - Guidance Level
		Map<String, Object> btnHelpLevelOptions = new HashMap<>();
		btnHelpLevelOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_LVLGUIDANCE));
		btnHelpLevelOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_HELP);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLUE);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_LISTENER,
				(Listener) event -> getViewManager().openPCMMHelpLevelView());
		new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.PUSH | SWT.CENTER,
				btnHelpLevelOptions);

		// Footer buttons - Help - Create
		Map<String, Object> btnHelpOptions = new HashMap<>();
		btnHelpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		btnHelpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnHelpOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> HelpTools.openContextualHelp());
		new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER, btnHelpOptions);
		HelpTools.addContextualHelp(compositeButtonsFooter, ContextualHelpId.PCMM_EVIDENCE);

		// layout view
		compositeButtonsFooter.layout();
	}

	/**
	 * Add drag and drop support to the evidence viewer for Eclipse local selection
	 * transfer. External file transfers are not allowed, files must be in the
	 * current workspace.
	 */
	private void addDragAndDropSupport() {

		int transferTypes = DND.DROP_MOVE | DND.DROP_COPY;
		Transfer[] transferObjectTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };

		// drag support
		treeViewerEvidence.addDragSupport(transferTypes, transferObjectTypes, new DragSourceAdapter() {
			@Override
			public void dragFinished(DragSourceEvent event) {
				// refresh the view
				if (getViewManager().isDirty()) {
					refresh();
				}
			}

			@Override
			public void dragSetData(DragSourceEvent event) {
				IStructuredSelection selection = treeViewerEvidence.getStructuredSelection();

				// drag the selection of evidence
				if (LocalSelectionTransfer.getTransfer().isSupportedType(event.dataType) && selection != null
						&& selection.getFirstElement() instanceof PCMMEvidence) {
					event.data = selection;
				}
			}
		});

		// drop support
		treeViewerEvidence.addDropSupport(transferTypes, transferObjectTypes,
				new PCMMEvidenceDropSupport(viewCtrl, treeViewerEvidence));
	}

	/**
	 * Loads view datas from database.
	 */
	private void loadDatas() {
		setPcmmElement(elementSelected);
	}

	/**
	 * @return the viewer
	 */
	public ColumnViewer getTreeViewer() {
		return treeViewerEvidence;
	}

	/**
	 * Refresh the table buttons
	 * 
	 * @param item
	 */
	private void refreshTableButtons(TreeItem item) {
		ViewTools.refreshTreeEditor(addEditors.get(item));
		ViewTools.refreshTreeEditor(viewEditors.get(item));
		ViewTools.refreshTreeEditor(editEditors.get(item));
		ViewTools.refreshTreeEditor(deleteEditors.get(item));
	}

	/**
	 * Refreshes the view
	 */
	public void refreshViewer() {
		treeViewerEvidence.refresh();

		// reset the header controls
		setTitle(RscTools.getString(RscConst.MSG_PCMMEVID_TITLE,
				this.elementSelected != null ? this.elementSelected.getName() : RscTools.empty()));

		// layout view
		this.layout();
	}

	/**
	 * Reloads view
	 */
	@Override
	public void reload() {

		// Trigger GuidanceLevel View
		getViewManager().getCredibilityEditor().setPartProperty(
				CredibilityFrameworkConstants.PART_PROPERTY_ACTIVEVIEW_PCMM_SELECTED_ASSESSABLE,
				elementSelected.getAbbreviation());

		// Show role selection
		showRoleSelection();

		// Get Model
		Model model = getViewManager().getCache().getModel();
		if (model != null) {
			try {
				// Load pcmm elements from database
				elements = getViewManager().getAppManager().getService(IPCMMApplication.class).getElementList(model);

				if (elements != null) {

					// Refresh the table
					refreshMainTable();

					// set pcmm elements
					treeViewerEvidence.setInput(elements);

					// expand and select the selected element in the viewer input
					if (elementSelected != null) {

						// refresh element
						getViewManager().getAppManager().getService(IPCMMApplication.class)
								.refreshElement(elementSelected);

						treeViewerEvidence.setExpandedState(elementSelected, true);

						for (PCMMSubelement sub : elementSelected.getSubElementList()) {

							// refresh subelement
							getViewManager().getAppManager().getService(IPCMMApplication.class).refreshSubelement(sub);

							treeViewerEvidence.setExpandedState(sub, true);
						}
						treeViewerEvidence.reveal(elementSelected);
					}
				}
			} catch (CredibilityException e) {
				MessageDialog.openWarning(getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
						RscTools.getString(RscConst.ERR_PCMMEVID_DIALOG_LOADING_MSG));
				logger.warn("An error has occurred while loading the evidence data:\n{}", e.getMessage(), e); //$NON-NLS-1$
			}
		}

		// refresh the viewer
		refreshViewer();
	}

	/** {@inheritDoc} */
	@Override
	public void roleChanged() {
		refresh();
	}

	/**
	 * Resets the pcmm element selected
	 * 
	 * @param pcmmElement the element to set
	 */
	public void setPcmmElement(PCMMElement pcmmElement) {
		this.elementSelected = pcmmElement;

		// Refresh
		refresh();
	}

	/**
	 * 
	 * Resets the pcmm subelement selected
	 * 
	 * @param subelt the subelement to set
	 */
	public void setSubelementSelected(PCMMSubelement subelt) {
		if (subelt != null) {
			setPcmmElement(subelt.getElement());

			// select the selected element in the viewer input
			treeViewerEvidence.reveal(subelt);
		}
	}

	/**
	 * @return the first evidence selected of the evidence viewer
	 */
	private PCMMEvidence getFirstEvidenceSelected() {

		ISelection selection = treeViewerEvidence.getSelection();
		if (selection != null && !selection.isEmpty()) {
			Object elt = ((IStructuredSelection) selection).getFirstElement();
			if (elt instanceof PCMMEvidence) {
				return (PCMMEvidence) elt;
			}
		}

		return null;
	}

	/**
	 * @return the list of evidence selected of the evidence viewer
	 */
	private List<PCMMEvidence> getEvidenceSelectedList() {

		List<PCMMEvidence> evidenceList = new ArrayList<>();
		ISelection selection = treeViewerEvidence.getSelection();
		if (selection != null && !selection.isEmpty()) {

			// get viewer selection to delete selected
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			for (Object obj : structuredSelection.toList()) {
				if (obj instanceof PCMMEvidence) {
					evidenceList.add((PCMMEvidence) obj);
				}
			}
		}

		return evidenceList;
	}

	/**
	 * Check if the element in parameter is contained in the current selected
	 * element of the parent view
	 * 
	 * @param evidence the evidence to check
	 * @return boolean True if is selected
	 */
	protected boolean isFromCurrentPCMMElement(PCMMEvidence evidence) {
		// Initialize
		boolean isFromCurrentPCMMElement = false;

		// Check the PCMM mode
		if (PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())) {
			isFromCurrentPCMMElement = getPcmmElement() != null && evidence.getSubelement() != null
					&& getPcmmElement().equals(evidence.getSubelement().getElement());
		} else if (PCMMMode.SIMPLIFIED.equals(getViewManager().getPCMMConfiguration().getMode())) {
			isFromCurrentPCMMElement = this.getPcmmElement() != null && getPcmmElement().equals(evidence.getElement());
		}

		return isFromCurrentPCMMElement;
	}

	/**
	 * TODO: TO BE REMOVED, implemented in the controller
	 * 
	 * @return the pcmm element selected
	 */
	PCMMElement getPcmmElement() {
		return elementSelected;
	}

	/**
	 * TODO: TO BE REMOVED, implemented in the controller
	 * 
	 * @return the last selected file
	 */
	IFile getLastSelectedFile() {
		return lastSelectedFile;
	}

	/**
	 * @param lastSelectedFile the last file selected for the evidence
	 */
	void setLastSelectedFile(IFile lastSelectedFile) {
		this.lastSelectedFile = lastSelectedFile;
	}

}
