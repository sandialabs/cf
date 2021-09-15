/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.uncertainty;

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

import gov.sandia.cf.application.IUncertaintyApplication;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyGroup;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.listeners.ViewerSelectionKeepBackgroundColor;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.ui.ACredibilitySubView;
import gov.sandia.cf.parts.ui.MainViewManager;
import gov.sandia.cf.parts.ui.uncertainty.editors.UncertaintyTreeContentProvider;
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
 * Uncertainty view: it is the Uncertainty home page to select, open and delete
 * an uncertainty
 * 
 * @author Maxime N.
 *
 */
public class UncertaintyView extends ACredibilitySubView<UncertaintyViewManager> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(UncertaintyView.class);

	/**
	 * Controller
	 */
	private UncertaintyViewController viewCtrl;

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
	private Map<TreeItem, TreeEditor> addEditors;
	private Map<TreeItem, TreeEditor> openEditors;
	private Map<TreeItem, TreeEditor> viewEditors;
	private Map<TreeItem, TreeEditor> editEditors;
	private Map<TreeItem, TreeEditor> deleteEditors;

	/**
	 * The constructor
	 * 
	 * @param viewManager The view manager
	 * @param style       The view style
	 */
	public UncertaintyView(UncertaintyViewManager viewManager, int style) {
		super(viewManager, viewManager, style);

		this.viewCtrl = new UncertaintyViewController(this);

		// Make sure you dispose these buttons when viewer input changes
		this.addEditors = new HashMap<>();
		this.openEditors = new HashMap<>();
		this.viewEditors = new HashMap<>();
		this.editEditors = new HashMap<>();
		this.deleteEditors = new HashMap<>();

		// create the view
		if (getViewManager().getCache().getUncertaintySpecification() != null) {
			renderPage();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_UNCERTAINTY_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_UNCERTAINTY_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {

		logger.debug("Reload Uncertainty view"); //$NON-NLS-1$

		// Get Model
		Model model = getViewManager().getCache().getModel();
		List<UncertaintyGroup> uncertaintyGroupList = new ArrayList<>();

		// Get data
		if (model != null) {
			uncertaintyGroupList = this.getViewManager().getAppManager().getService(IUncertaintyApplication.class)
					.getUncertaintyGroupByModel(model);

			// reload system requirement spec
			getViewManager().getCache().reloadUncertaintySpecification();
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
		treeViewer.setInput(uncertaintyGroupList);

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

		logger.debug("Render Uncertainty page"); //$NON-NLS-1$

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

		logger.debug("Render Uncertainty header buttons"); //$NON-NLS-1$

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

		// Button - Add Group
		Map<String, String> btnAddUncertaintyGroupData = new HashMap<>();
		btnAddUncertaintyGroupData.put(MainViewManager.BTN_EVENT_PROPERTY, UncertaintyViewManager.BTN_EVENT_ADD_GROUP);
		Map<String, Object> btnAddUncertaintyGroupOptions = new HashMap<>();
		btnAddUncertaintyGroupOptions.put(ButtonTheme.OPTION_TEXT,
				RscTools.getString(RscConst.MSG_UNCERTAINTY_BTN_ADD_GROUP));
		btnAddUncertaintyGroupOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnAddUncertaintyGroupOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_ADD);
		btnAddUncertaintyGroupOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
		btnAddUncertaintyGroupOptions.put(ButtonTheme.OPTION_DATA, btnAddUncertaintyGroupData);
		btnAddUncertaintyGroupOptions.put(ButtonTheme.OPTION_LISTENER,
				(Listener) event -> viewCtrl.addUncertaintyGroup());
		new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsHeaderRight, SWT.CENTER,
				btnAddUncertaintyGroupOptions);
	}

	/**
	 * Render footer buttons
	 */
	private void renderFooterButtons() {

		logger.debug("Render Uncertainty footer buttons"); //$NON-NLS-1$

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
		HelpTools.addContextualHelp(compositeButtons, ContextualHelpId.UNCERTAINTY);

		// Footer buttons - Back - plug
		getViewManager().plugBackHomeButton(btnBack);

		// layout view
		compositeButtons.layout();
	}

	/**
	 * Render main table composite
	 */
	private void renderMainTableComposite() {

		logger.debug("Render Uncertainty main table composite"); //$NON-NLS-1$

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

		logger.debug("Render Uncertainty main table"); //$NON-NLS-1$

		// Initialize table
		renderMainTableInit();

		// Initialize data
		List<String> columnProperties = new ArrayList<>();
		columnProperties.add(treeViewer.getIdColumn().getColumn().getText());

		// Column - Group Name
		columnProperties.add(renderMainTableColumnGroupName().getColumn().getText());

		// Columns - Parameters
		for (UncertaintyParam parameter : getViewManager().getCache().getUncertaintySpecification().getParameters()) {
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

		// Add listeners
		renderMainTableAddEvents();

		// Tree - Properties
		treeViewer.setColumnProperties(columnProperties.stream().toArray(String[]::new));
		treeViewer.setContentProvider(new UncertaintyTreeContentProvider());

		// SWT.EraseItem: this event is called when repainting the tree not on removing
		// an element of the tree
		// In this case, it keeps cell colors when selection is active on cell
		treeViewer.getTree().addListener(SWT.EraseItem, new ViewerSelectionKeepBackgroundColor(treeViewer) {
			@Override
			public boolean isConditionFulfilled(Object data) {
				return data instanceof Uncertainty;
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
		treeViewer.getTree().setLayoutData(gdViewer);
		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);

		FancyToolTipSupport.enableFor(treeViewer, ToolTip.NO_RECREATE);

		// Tree - Layout
		final AutoResizeViewerLayout viewerLayout = new AutoResizeViewerLayout(treeViewer);
		treeViewer.setLayout(viewerLayout);

		// Tree - Hint
		gdViewer.heightHint = treeViewer.getTree().getItemHeight();
		gdViewer.widthHint = getViewManager().getSize().x
				- 2 * ((GridLayout) compositeTable.getLayout()).horizontalSpacing;

		// Tree - Customize
		treeViewer.getTree().setHeaderBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY));
		treeViewer.getTree().setHeaderForeground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));

		treeViewer.getIdColumn().setLabelProvider(new UncertaintyColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return treeViewer.getIdColumnText(element);
			}
		});
	}

	/**
	 * Add group column name
	 * 
	 * @return the tree column created
	 */
	private TreeViewerColumn renderMainTableColumnGroupName() {
		// Get layout
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) treeViewer.getTree().getLayout();

		// Tree - Columns - Element/Sub-element
		TreeViewerColumn elementColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		elementColumn.getColumn().setText(RscTools.getString(RscConst.MSG_UNCERTAINTY_GROUP_NAME));
		elementColumn.setLabelProvider(new UncertaintyColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof UncertaintyGroup) {
					// Get UncertaintyGroup Name
					return ((UncertaintyGroup) element).getName();
				}
				return RscTools.empty();
			}
		});
		treeViewerLayout
				.addColumnData(new ColumnWeightData(PartsResourceConstants.UNCERTAINTYVIEW_GROUP_COLUMN_COEFF, true));
		return elementColumn;
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
		addColumn.setLabelProvider(new UncertaintyColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// View editor
				TreeEditor editor = null;

				if (element instanceof UncertaintyGroup && !addEditors.containsKey(item)) {

					// Button
					ButtonTheme btnAddItem = TableFactory.createAddButtonColumnAction(getViewManager().getRscMgr(),
							cell);
					btnAddItem.addListener(SWT.Selection, event -> {
						viewCtrl.addUncertainty((UncertaintyGroup) element);
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
		openColumn.setLabelProvider(new UncertaintyColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Open editor
				TreeEditor editor = null;

				// Button Open for Uncertainty
				if (element instanceof Uncertainty && !openEditors.containsKey(item)) {

					// Button
					ButtonTheme btnViewItem = TableFactory.createOpenButtonColumnAction(getViewManager().getRscMgr(),
							cell);
					btnViewItem.addListener(SWT.Selection,
							event -> viewCtrl.openAllUncertaintyValues((Uncertainty) element));

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
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_VIEW));

		// Tree - Column - Action View
		TreeViewerColumn viewColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		viewColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_VIEW));
		viewColumn.setLabelProvider(new UncertaintyColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Button Open for Uncertainty
				// View editor
				TreeEditor editor = null;
				if (element instanceof Uncertainty && !viewEditors.containsKey(item)) {

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
		editColumn.setLabelProvider(new UncertaintyColumnLabelProvider() {
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
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_EDIT));

		// Tree - Column - Action delete
		TreeViewerColumn actionDeleteColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		actionDeleteColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_DELETE));
		actionDeleteColumn.setLabelProvider(new UncertaintyColumnLabelProvider() {
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
				viewCtrl.openAllUncertaintyValues(getSelected());
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
		ViewTools.refreshTableEditors(addEditors);
		ViewTools.refreshTableEditors(openEditors);
		ViewTools.refreshTableEditors(viewEditors);
		ViewTools.refreshTableEditors(editEditors);
		ViewTools.refreshTableEditors(deleteEditors);

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
		ViewTools.refreshTreeEditor(addEditors.get(item));
		ViewTools.refreshTreeEditor(openEditors.get(item));
		ViewTools.refreshTreeEditor(viewEditors.get(item));
		ViewTools.refreshTreeEditor(editEditors.get(item));
		ViewTools.refreshTreeEditor(deleteEditors.get(item));
	}

	/**
	 * Get First Uncertainty Selected
	 * 
	 * @return the first evidence selected of the evidence viewer
	 */
	private Uncertainty getSelected() {
		ISelection selection = treeViewer.getSelection();
		if (selection != null && !selection.isEmpty()) {
			Object elt = ((IStructuredSelection) selection).getFirstElement();
			if (elt instanceof Uncertainty) {
				return (Uncertainty) elt;
			}
		}
		return null;
	}

	/**
	 * The Uncertainty column label provider
	 * 
	 * @author Didier Verstraete
	 *
	 */
	private class UncertaintyColumnLabelProvider extends ColumnLabelProvider {
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
		if (element instanceof UncertaintyGroup) {
			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT);
		} else if (element instanceof Uncertainty) {
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

		if (element instanceof UncertaintyGroup) {
			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE);
		} else if (element instanceof Uncertainty) {
			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK);
		}
		return null;
	}

	/**
	 * Keep the current expanded elements and expand the element in parameter
	 * 
	 * @param parent the uncertainty group to expand with its children
	 */
	void expandElements(UncertaintyGroup parent) {
		Object[] elements = treeViewer.getExpandedElements();
		List<Object> elementsList = new ArrayList<>(Arrays.asList(elements));
		if (parent != null) {
			elementsList.add(parent);
		}
		treeViewer.setExpandedElements(elementsList.toArray());
	}

	/**
	 * @param uncertainty the uncertainty to get the id for
	 * @return the value of the id column for the uncertainty in parameter
	 */
	String getIdColumnText(Uncertainty uncertainty) {
		if (uncertainty == null) {
			return null;
		}
		return treeViewer.getIdColumnText(uncertainty);
	}

	/**
	 * @param decision the decision to get the id for
	 * @return the value of the id column for the decision in parameter
	 */
	@SuppressWarnings("unchecked")
	List<UncertaintyGroup> getTreeInput() {
		return (List<UncertaintyGroup>) treeViewer.getInput();
	}
}
