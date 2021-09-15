/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.requirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import gov.sandia.cf.application.ISystemRequirementApplication;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.listeners.ViewerSelectionKeepBackgroundColor;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.ui.ACredibilitySubView;
import gov.sandia.cf.parts.ui.MainViewManager;
import gov.sandia.cf.parts.ui.requirement.editors.SystemRequirementTreeContentProvider;
import gov.sandia.cf.parts.viewer.TableFactory;
import gov.sandia.cf.parts.viewer.TreeViewerID;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.parts.viewer.editors.ColumnViewerSupport;
import gov.sandia.cf.parts.viewer.editors.GenericTableLabelProvider;
import gov.sandia.cf.parts.widgets.FancyToolTipSupport;
import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Requirement view: it is the Requirement home page to select, open and delete
 * an requirement
 * 
 * @author Maxime N.
 *
 */
public class SystemRequirementView extends ACredibilitySubView<SystemRequirementViewManager> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SystemRequirementView.class);

	/**
	 * Controller
	 */
	private SystemRequirementViewController viewCtrl;

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
	public SystemRequirementView(SystemRequirementViewManager viewManager, int style) {
		super(viewManager, viewManager, style);

		this.viewCtrl = new SystemRequirementViewController(this);

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
		return RscTools.getString(RscConst.MSG_SYSREQUIREMENT_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_SYSREQUIREMENT_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {

		logger.debug("Reload System requirement view"); //$NON-NLS-1$

		// Get Model
		Model model = getViewManager().getCache().getModel();
		List<SystemRequirement> requirementList = new ArrayList<>();

		// Get data
		if (model != null) {
			// Get list of system requirements
			requirementList = this.getViewManager().getAppManager().getService(ISystemRequirementApplication.class)
					.getRequirementRootByModel(model);

			// reload system requirement spec
			getViewManager().getCache().reloadSystemRequirementSpecification();
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
		treeViewer.setInput(requirementList);

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

		logger.debug("Render System requirement page"); //$NON-NLS-1$

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

		logger.debug("Render System requirement header buttons"); //$NON-NLS-1$

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

		// Button - Add requirement
		Map<String, String> btnAddRequirementData = new HashMap<>();
		btnAddRequirementData.put(MainViewManager.BTN_EVENT_PROPERTY, SystemRequirementViewManager.BTN_EVENT_ADD_GROUP);
		Map<String, Object> btnAddRequirementOptions = new HashMap<>();
		btnAddRequirementOptions.put(ButtonTheme.OPTION_TEXT,
				RscTools.getString(RscConst.MSG_SYSREQUIREMENT_BTN_ADD_GROUP));
		btnAddRequirementOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnAddRequirementOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_ADD);
		btnAddRequirementOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
		btnAddRequirementOptions.put(ButtonTheme.OPTION_DATA, btnAddRequirementData);
		btnAddRequirementOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> viewCtrl.addRequirement());
		new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsHeaderRight, SWT.CENTER,
				btnAddRequirementOptions);
	}

	/**
	 * Render footer buttons
	 */
	private void renderFooterButtons() {

		logger.debug("Render System requirement footer buttons"); //$NON-NLS-1$

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
		HelpTools.addContextualHelp(compositeButtons, ContextualHelpId.SYSTEM_REQUIREMENT);

		// Footer buttons - Back - plug
		getViewManager().plugBackHomeButton(btnBack);

		// layout view
		compositeButtons.layout();
	}

	/**
	 * Render main table composite
	 */
	private void renderMainTableComposite() {

		logger.debug("Render System requirement main table composite"); //$NON-NLS-1$

		// Main table composite
		compositeTable = new Composite(this, SWT.FILL);
		GridLayout gridLayout = new GridLayout(1, true);
		compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeTable.setLayout(gridLayout);
	}

	/**
	 * Render system requirement tree
	 */
	private void renderMainTable() {

		logger.debug("Render System requirement main table"); //$NON-NLS-1$

		// Initialize table
		renderMainTableInit();

		// Initialize data
		List<String> columnProperties = new ArrayList<>();

		// Get Tree and layout
		Tree tree = treeViewer.getTree();
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) tree.getLayout();

		// Columns - Id
		treeViewer.getIdColumn().setLabelProvider(new SystemRequirementColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof SystemRequirement) {
					((SystemRequirement) element).setGeneratedId(treeViewer.getIdColumnText(element));
				}
				return treeViewer.getIdColumnText(element);
			}
		});
		columnProperties.add(treeViewer.getIdColumn().getColumn().getText());

		// Columns - statement
		TreeViewerColumn statementColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		statementColumn.getColumn().setText(RscTools.getString(RscConst.MSG_SYSREQUIREMENT_STATEMENT));
		treeViewerLayout
				.addColumnData(new ColumnWeightData(PartsResourceConstants.GENPARAM_TABLE_TEXT_COLUMN_COEFF, true));
		statementColumn.setLabelProvider(new SystemRequirementColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String statement = null;
				if (element instanceof SystemRequirement) {
					statement = ((SystemRequirement) element).getStatement();
				}
				return statement;
			}
		});
		columnProperties.add(RscTools.getString(RscConst.MSG_SYSREQUIREMENT_STATEMENT));

		// Columns - Parameters
		if (getViewManager().getCache().getSystemRequirementSpecification() != null) {
			for (SystemRequirementParam parameter : getViewManager().getCache().getSystemRequirementSpecification()
					.getParameters()) {
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
		}

		// Columns - Actions
		renderMainTableActionColumns(columnProperties);

		// Add listeners
		renderMainTableAddEvents();

		// Tree - Properties
		treeViewer.setColumnProperties(columnProperties.stream().toArray(String[]::new));
		treeViewer.setContentProvider(new SystemRequirementTreeContentProvider());

		// SWT.EraseItem: this event is called when repainting the tree not on removing
		// an element of the tree
		// In this case, it keeps cell colors when selection is active on cell
		treeViewer.getTree().addListener(SWT.EraseItem, new ViewerSelectionKeepBackgroundColor(treeViewer) {

			@Override
			public boolean isConditionFulfilled(Object data) {
				return data instanceof SystemRequirement;
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
		treeViewer = new TreeViewerID(compositeTable, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
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
		tree.setHeaderBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY));
		tree.setHeaderForeground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));
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
		TreeViewerColumn addColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		addColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_ADD));
		addColumn.setLabelProvider(new SystemRequirementColumnLabelProvider() {
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
					btnAddItem.addListener(SWT.Selection, event -> {
						viewCtrl.addRequirement((SystemRequirement) element);
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
		openColumn.setLabelProvider(new SystemRequirementColumnLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Open editor
				TreeEditor editor = null;

				// Button Open for SystemRequirement
				if (element instanceof SystemRequirement && !openEditors.containsKey(item)) {

					// Button
					ButtonTheme btnViewItem = TableFactory.createOpenButtonColumnAction(getViewManager().getRscMgr(),
							cell);

					// Open - Listener
					btnViewItem.addListener(SWT.Selection, event -> viewCtrl.openAll((SystemRequirement) element));

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
		viewColumn.setLabelProvider(new SystemRequirementColumnLabelProvider() {
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
		treeViewerLayout.addColumnData(new ColumnPixelData(PartsResourceConstants.TABLE_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_VIEW));

		// Tree - Column - Action Edit
		TreeViewerColumn editColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		editColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_EDIT));
		editColumn.setLabelProvider(new SystemRequirementColumnLabelProvider() {
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
		treeViewerLayout.addColumnData(new ColumnPixelData(PartsResourceConstants.TABLE_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_EDIT));

		// Tree - Column - Action delete
		TreeViewerColumn actionDeleteColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		actionDeleteColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_DELETE));
		actionDeleteColumn.setLabelProvider(new SystemRequirementColumnLabelProvider() {
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
		treeViewerLayout.addColumnData(new ColumnPixelData(PartsResourceConstants.TABLE_ACTIONCOLUMN_WIDTH, true));
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
		ViewTools.refreshTableEditors(openEditors);
		ViewTools.refreshTableEditors(editEditors);
		ViewTools.refreshTableEditors(viewEditors);
		ViewTools.refreshTableEditors(deleteEditors);
		ViewTools.refreshTableEditors(addEditors);

		// render the main table
		renderMainTable();

		// re-layout the table composite
		compositeTable.layout();
	}

	/**
	 * Refresh the table buttons
	 * 
	 * @param item
	 */
	private void refreshTableButtons(TreeItem item) {
		ViewTools.refreshTreeEditor(openEditors.get(item));
		ViewTools.refreshTreeEditor(editEditors.get(item));
		ViewTools.refreshTreeEditor(viewEditors.get(item));
		ViewTools.refreshTreeEditor(deleteEditors.get(item));
		ViewTools.refreshTreeEditor(addEditors.get(item));
	}

	/**
	 * Get First Requirement Selected
	 * 
	 * @return the first requirement selected of the requirement viewer
	 */
	private SystemRequirement getSelected() {
		ISelection selection = treeViewer.getSelection();
		if (selection != null && !selection.isEmpty()) {
			Object elt = ((IStructuredSelection) selection).getFirstElement();
			if (elt instanceof SystemRequirement) {
				return (SystemRequirement) elt;
			}
		}
		return null;
	}

	/**
	 * The SystemRequirement column label provider
	 * 
	 * @author Didier Verstraete
	 *
	 */
	private class SystemRequirementColumnLabelProvider extends ColumnLabelProvider {
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
	 * @param element
	 * @return Color the color
	 */
	private Color getTreeCellBackground(Object element) {

		if (element instanceof SystemRequirement) {
			SystemRequirement req = (SystemRequirement) element;

			if (req.getParent() != null && req.getChildren() != null && !req.getChildren().isEmpty()) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT_2);
			} else if (req.getParent() == null) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT);
			}
			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE);
		}

		return null;
	}

	/**
	 * Get cell foreground color
	 * 
	 * @param element
	 * @return Color the color
	 */
	private Color getTreeCellForeground(Object element) {

		// Manage levels
		if (element instanceof SystemRequirement) {
			SystemRequirement req = (SystemRequirement) element;
			if (req.getParent() == null) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE);
			}

			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK);
		}

		return null;
	}

	/**
	 * Keep the current expanded elements and expand the element in parameter
	 * 
	 * @param parent the system requirement to expand with its children
	 */
	void expandElements(SystemRequirement parent) {
		Object[] elements = treeViewer.getExpandedElements();
		List<Object> elementsList = new ArrayList<>(Arrays.asList(elements));
		if (parent != null) {
			elementsList.add(parent);
		}
		treeViewer.setExpandedElements(elementsList.toArray());
	}

	/**
	 * @param requirement the requirement to get the id for
	 * @return the value of the id column for the requirement in parameter
	 */
	String getIdColumnText(SystemRequirement requirement) {
		if (requirement == null) {
			return null;
		}
		return treeViewer.getIdColumnText(requirement);
	}
}
