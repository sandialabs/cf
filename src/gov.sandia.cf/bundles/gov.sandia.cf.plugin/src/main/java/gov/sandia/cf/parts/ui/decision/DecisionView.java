/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.decision;

import java.util.ArrayList;
import java.util.Arrays;
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

import gov.sandia.cf.application.decision.IDecisionApplication;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.listeners.ViewerSelectionKeepBackgroundColor;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.ui.ACredibilitySubView;
import gov.sandia.cf.parts.ui.MainViewManager;
import gov.sandia.cf.parts.ui.decision.editors.DecisionTreeContentProvider;
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
 * Decision view: it is the Decision home page to select, open and delete an
 * decision
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionView extends ACredibilitySubView<DecisionViewManager> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(DecisionView.class);

	/**
	 * Controller
	 */
	private DecisionViewController viewCtrl;

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
	private Map<TreeItem, TreeEditor> openEditors;
	private Map<TreeItem, TreeEditor> viewEditors;
	private Map<TreeItem, TreeEditor> addEditors;
	private Map<TreeItem, TreeEditor> editEditors;
	private Map<TreeItem, TreeEditor> deleteEditors;

	/**
	 * The constructor
	 * 
	 * @param viewManager The view manager
	 * @param style       The view style
	 */
	public DecisionView(DecisionViewManager viewManager, int style) {
		super(viewManager, viewManager, style);

		this.viewCtrl = new DecisionViewController(this);

		// Make sure you dispose these buttons when viewer input changes
		this.openEditors = new HashMap<>();
		this.viewEditors = new HashMap<>();
		this.addEditors = new HashMap<>();
		this.editEditors = new HashMap<>();
		this.deleteEditors = new HashMap<>();

		// create the view
		renderPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_DECISION_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_DECISION_ITEM);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {

		logger.debug("Reload Decision view"); //$NON-NLS-1$

		// Get Model
		Model model = getViewManager().getCache().getModel();
		List<Decision> decisionList = new ArrayList<>();

		// Get data
		if (model != null) {
			// Get list of decision
			decisionList = this.getViewManager().getAppManager().getService(IDecisionApplication.class)
					.getDecisionRootByModel(model);

			// reload decision spec
			getViewManager().getCache().reloadDecisionSpecification();
		}

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
		treeViewer.setInput(decisionList);

		// Set expanded elements
		if (initialization) {
			treeViewer.expandAll();
		} else {
			treeViewer.setExpandedElements(elements);
		}
		treeViewer.refresh();
	}

	/**
	 * Creates the page
	 */
	private void renderPage() {

		logger.debug("Render Decision page"); //$NON-NLS-1$

		// Render header table
		renderHeaderButtons();

		// Render main table
		renderMainTableComposite();

		// Render footer buttons
		renderFooterButtons();
	}

	/**
	 * Render header buttons
	 */
	private void renderHeaderButtons() {

		logger.debug("Render Decision header buttons"); //$NON-NLS-1$

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

		// Button - Add decision
		Map<String, String> btnAddDecisionData = new HashMap<>();
		btnAddDecisionData.put(MainViewManager.BTN_EVENT_PROPERTY, DecisionViewManager.BTN_EVENT_ADD);
		Map<String, Object> btnAddDecisionOptions = new HashMap<>();
		btnAddDecisionOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_DECISION_BTN_ADD));
		btnAddDecisionOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnAddDecisionOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_ADD);
		btnAddDecisionOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
		btnAddDecisionOptions.put(ButtonTheme.OPTION_DATA, btnAddDecisionData);
		btnAddDecisionOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> viewCtrl.addDecision());
		new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsHeaderRight, SWT.CENTER, btnAddDecisionOptions);
	}

	/**
	 * Render footer buttons
	 */
	private void renderFooterButtons() {

		logger.debug("Render Decision footer buttons"); //$NON-NLS-1$

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
		HelpTools.addContextualHelp(compositeButtons, ContextualHelpId.ANALYST_DECISION);

		// Footer buttons - Back - plug
		getViewManager().plugBackHomeButton(btnBack);

		// layout view
		compositeButtons.layout();
	}

	/**
	 * Render main table composite
	 */
	private void renderMainTableComposite() {

		logger.debug("Render Decision main table composite"); //$NON-NLS-1$

		// Main table composite
		compositeTable = new Composite(this, SWT.FILL);
		GridLayout gridLayout = new GridLayout(1, true);
		compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeTable.setLayout(gridLayout);
	}

	/**
	 * Render system decision tree
	 */
	private void renderMainTable() {

		logger.debug("Render Decision main table"); //$NON-NLS-1$

		// Initialize table
		renderMainTableInit();

		// Initialize data
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) treeViewer.getTree().getLayout();
		List<String> columnProperties = new ArrayList<>();
		columnProperties.add(treeViewer.getIdColumn().getColumn().getText());

		// Title
		TreeViewerColumn symbolColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		symbolColumn.getColumn().setText(RscTools.getString(RscConst.MSG_DECISION_COLUMN_TITLE));
		symbolColumn.setLabelProvider(new DecisionColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return element instanceof Decision ? ((Decision) element).getTitle() : RscTools.empty();
			}
		});
		treeViewerLayout
				.addColumnData(new ColumnWeightData(PartsResourceConstants.GENPARAM_TABLE_TEXT_COLUMN_COEFF, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_DECISION_COLUMN_TITLE));

		// Columns - Parameters
		for (DecisionParam parameter : getViewManager().getCache().getDecisionSpecification().getParameters()) {
			TreeViewerColumn treeCol = TableFactory.createGenericParamTreeColumn(parameter, treeViewer,
					new GenericTableLabelProvider(parameter, getViewManager()) {

						@Override
						public Color getBackground(Object element) {
							return getTreeCellBackground(element);
						}

						@Override
						public Color getForeground(Object element) {
							return getTreeCellForeground(element);
						}
					});
			if (treeCol != null) {
				columnProperties.add(treeCol.getColumn().getText());
			}
		}

		// Columns - Actions
		renderMainTableActionColumns(columnProperties);

		// add drag and drop support on tree to transfer uncertainties
		addDragAndDropSupport();

		// Add listeners
		renderMainTableAddEvents();

		// Tree - Properties
		treeViewer.setColumnProperties(columnProperties.stream().toArray(String[]::new));
		treeViewer.setContentProvider(new DecisionTreeContentProvider());

		// SWT.EraseItem: this event is called when repainting the tree not on removing
		// an element of the tree
		// In this case, it keeps cell colors when selection is active on cell
		treeViewer.getTree().addListener(SWT.EraseItem, new ViewerSelectionKeepBackgroundColor(treeViewer) {

			@Override
			public boolean isConditionFulfilled(Object data) {
				return data instanceof Decision;
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

		// Set width
		treeViewer.getIdColumn().setLabelProvider(new DecisionColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Decision) {
					if (StringUtils.isBlank(((Decision) element).getGeneratedId())) {
						((Decision) element).setGeneratedId(treeViewer.getIdColumnText(element));
					}
					return ((Decision) element).getGeneratedId();
				}
				return treeViewer.getIdColumnText(element);
			}
		});
	}

	/**
	 * Add action columns
	 * 
	 * @param columnProperties
	 */
	private void renderMainTableActionColumns(List<String> columnProperties) {

		// Get Tree and layout
		Tree tree = treeViewer.getTree();
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) tree.getLayout();

		// Tree - Column - Action Add
		TreeViewerColumn addColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		addColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_ADD));
		addColumn.setLabelProvider(new DecisionColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// View editor
				TreeEditor editor = null;

				if (!addEditors.containsKey(item)) {

					// Button
					ButtonTheme btnAddItem = TableFactory.createAddButtonColumnAction(getViewManager().getRscMgr(),
							cell);

					// Footer buttons - Delete- Listener
					btnAddItem.addListener(SWT.Selection, event -> {
						viewCtrl.addDecision((Decision) element);
						treeViewer.refresh(item);
					});

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
		treeViewerLayout.addColumnData(new ColumnPixelData(PartsResourceConstants.TABLE_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_ADD));

		// Tree - Column - Action Open
		TreeViewerColumn openColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		openColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_OPEN));
		openColumn.setLabelProvider(new DecisionColumnLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Open editor
				TreeEditor editor = null;

				// Button Open for Decision
				if (element instanceof Decision && !openEditors.containsKey(item)) {

					// Button
					ButtonTheme btnViewItem = TableFactory.createOpenButtonColumnAction(getViewManager().getRscMgr(),
							cell);

					// Open - Listener
					btnViewItem.addListener(SWT.Selection, event -> viewCtrl.openAllDecisionValues((Decision) element));

					// Draw cell
					editor = new TreeEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnViewItem, item, cell.getColumnIndex());

					openEditors.put(item, editor);
				}
				// set cell background
				cell.setBackground(getBackground(element));
				cell.setForeground(getForeground(element));
			}
		});
		treeViewerLayout
				.addColumnData(new ColumnPixelData(PartsResourceConstants.DECISIONVIEW_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_VIEW));

		// Tree - Column - Action View
		TreeViewerColumn viewColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		viewColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_VIEW));
		viewColumn.setLabelProvider(new DecisionColumnLabelProvider() {

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

					// View - Listener
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
		treeViewerLayout
				.addColumnData(new ColumnPixelData(PartsResourceConstants.DECISIONVIEW_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_VIEW));

		// Tree - Column - Action Edit
		TreeViewerColumn editColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		editColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_EDIT));
		editColumn.setLabelProvider(new DecisionColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// View editor
				TreeEditor editor = null;

				if (!editEditors.containsKey(item)) {

					// Button
					ButtonTheme btnEditItem = TableFactory.createEditButtonColumnAction(getViewManager().getRscMgr(),
							cell);

					// Footer buttons - Delete- Listener
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
		treeViewerLayout
				.addColumnData(new ColumnPixelData(PartsResourceConstants.DECISIONVIEW_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_EDIT));

		// Tree - Column - Action delete
		TreeViewerColumn actionDeleteColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		actionDeleteColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_DELETE));
		actionDeleteColumn.setLabelProvider(new DecisionColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Delete Editor
				TreeEditor editor = null;

				if (!deleteEditors.containsKey(item)) {

					// Button
					ButtonTheme btnDeleteItem = TableFactory
							.createDeleteButtonColumnAction(getViewManager().getRscMgr(), cell);

					// Footer buttons - Delete- Listener
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
		treeViewerLayout
				.addColumnData(new ColumnPixelData(PartsResourceConstants.DECISIONVIEW_ACTIONCOLUMN_WIDTH, true));
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
						&& firstElement instanceof Decision) {
					event.data = firstElement;
				}
			}
		});

		// drop support
		Transfer[] transferTypesDrop = new Transfer[] { LocalSelectionTransfer.getTransfer() };
		treeViewer.addDropSupport(DND.DROP_MOVE, transferTypesDrop, new DecisionDropSupport(viewCtrl, treeViewer));
	}

	/**
	 * Refresh the main table
	 */
	private void refreshMainTable() {

		logger.debug("Refresh Decision main table"); //$NON-NLS-1$

		// Dispose the table components
		if (treeViewer != null && treeViewer.getTree() != null && !treeViewer.getTree().isDisposed()) {
			treeViewer.getTree().removeAll();
			treeViewer.getTree().dispose();
		}
		treeViewer = null;

		// Refresh
		ViewTools.refreshTableEditors(editEditors);
		ViewTools.refreshTableEditors(viewEditors);
		ViewTools.refreshTableEditors(addEditors);
		ViewTools.refreshTableEditors(deleteEditors);
		ViewTools.refreshTableEditors(openEditors);

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
		ViewTools.refreshTreeEditor(addEditors.get(item));
		ViewTools.refreshTreeEditor(deleteEditors.get(item));
		ViewTools.refreshTreeEditor(openEditors.get(item));
	}

	/**
	 * Get First Decision Selected
	 * 
	 * @return the first decision selected of the decision viewer
	 */
	private Decision getSelected() {
		ISelection selection = treeViewer.getSelection();
		if (selection != null && !selection.isEmpty()) {
			Object elt = ((IStructuredSelection) selection).getFirstElement();
			if (elt instanceof Decision) {
				return (Decision) elt;
			}
		}
		return null;
	}

	/**
	 * The Decision column label provider
	 * 
	 * @author Didier Verstraete
	 *
	 */
	private class DecisionColumnLabelProvider extends ColumnLabelProvider {
		@Override
		public Color getBackground(Object element) {
			return getTreeCellBackground(element);
		}

		@Override
		public Color getForeground(Object element) {
			return getTreeCellForeground(element);
		}
	}

	/**
	 * Get cell background color
	 * 
	 * @param element the element to get cell background for
	 * @return Color the color
	 */
	private Color getTreeCellBackground(Object element) {

		if (element instanceof Decision) {
			Decision decision = (Decision) element;

			if (decision.getParent() != null && decision.getChildren() != null && !decision.getChildren().isEmpty()) {
				return ColorTools.toColor(getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT_2));
			} else if (decision.getParent() == null) {
				return ColorTools.toColor(getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT));
			}
			return ColorTools.toColor(getViewManager().getRscMgr(),
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));
		}

		return null;
	}

	/**
	 * Get cell foreground color
	 * 
	 * @param element the element to get cell foreground for
	 * @return Color the color
	 */
	private Color getTreeCellForeground(Object element) {

		// Manage levels
		if (element instanceof Decision) {
			Decision decision = (Decision) element;
			if (decision.getParent() == null) {
				return ColorTools.toColor(getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));
			}

			return ColorTools.toColor(getViewManager().getRscMgr(),
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK));
		}

		return null;
	}

	/**
	 * Keep the current expanded elements and expand the element in parameter
	 * 
	 * @param parent the decision to expand with its children
	 */
	void expandElements(Decision parent) {
		Object[] elements = treeViewer.getExpandedElements();
		List<Object> elementsList = new ArrayList<>(Arrays.asList(elements));
		if (parent != null) {
			elementsList.add(parent);
		}
		treeViewer.setExpandedElements(elementsList.toArray());
	}

	/**
	 * @param decision the decision to get the id for
	 * @return the value of the id column for the decision in parameter
	 */
	String getIdColumnText(Decision decision) {
		if (decision == null) {
			return null;
		}
		return treeViewer.getIdColumnText(decision);
	}
}
