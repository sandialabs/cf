/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.PIRTQuery;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.listeners.ComboDropDownKeyListener;
import gov.sandia.cf.parts.listeners.ViewerSelectionKeepBackgroundColor;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.ui.ACredibilitySubView;
import gov.sandia.cf.parts.ui.MainViewManager;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTQoITableHeaderCellModifier;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTQoITableHeaderContentProvider;
import gov.sandia.cf.parts.ui.pirt.editors.QoICreationDateLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.QoIDescriptionLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.QoILabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.QoINameLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.QoITreeContentProvider;
import gov.sandia.cf.parts.viewer.PIRTQoITableQoI;
import gov.sandia.cf.parts.viewer.TableFactory;
import gov.sandia.cf.parts.viewer.TableHeaderBar;
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
 * Quantity of interest view: it is the QoI home page to select, open and delete
 * a QoI
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTQoIView extends ACredibilitySubView<PIRTQoIViewController> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PIRTQoIView.class);

	/**
	 * the description table viewer
	 */
	private TableHeaderBar tableHeaderBar;

	/**
	 * the quantity of interest table viewer
	 */
	private TreeViewerID treeViewer;

	/**
	 * Table buttons
	 */
	private Map<TreeItem, TreeEditor> openEditors;
	private Map<TreeItem, TreeEditor> deleteEditors;
	private Map<TreeItem, TreeEditor> duplicateEditors;
	private Map<TreeItem, TreeEditor> tagEditors;

	/**
	 * The main table composite
	 */
	private Composite compositeTable;

	/** The cbx query. */
	private ComboViewer cbxQuery;

	/**
	 * @param viewController the view manager
	 * @param parent         the parent view
	 * @param style          the view style
	 */
	public PIRTQoIView(PIRTQoIViewController viewController, Composite parent, int style) {
		super(viewController, parent, style);

		// Make sure you dispose these buttons when viewer input changes
		openEditors = new HashMap<>();
		deleteEditors = new HashMap<>();
		duplicateEditors = new HashMap<>();
		tagEditors = new HashMap<>();

		// create the view
		renderPage();
	}

	/** {@inheritDoc} */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_QOIHOMEVIEW_TITLE);
	}

	/** {@inheritDoc} */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_QOIHOMEVIEW_ITEMTITLE);
	}

	/**
	 * Creates the page
	 */
	private void renderPage() {

		logger.debug("Render PIRT QoI Page"); //$NON-NLS-1$

		// Renders
		renderHeaderTable();

		// Render header table
		renderHeaderButtons();

		// Render main table
		renderMainTableComposite();

		// Render footer buttons
		renderFooterButtons();
	}

	/**
	 * Render header table
	 */
	private void renderHeaderTable() {

		logger.debug("Render PIRT QoI Header table"); //$NON-NLS-1$

		// Table Header - Create
		tableHeaderBar = new TableHeaderBar(getViewController().getViewManager(), this,
				RscTools.getString(RscConst.TABLE_QOI_HEADER_BAR_LABEL));
		tableHeaderBar.setContentProvider(new PIRTQoITableHeaderContentProvider());
		tableHeaderBar.setCellModifier(new PIRTQoITableHeaderCellModifier(getViewController()));
	}

	/**
	 * Render header buttons
	 */
	private void renderHeaderButtons() {

		logger.debug("Render PIRT QoI Header buttons"); //$NON-NLS-1$

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

		// Button - Add QoI
		Map<String, String> btnAddQoIData = new HashMap<>();
		btnAddQoIData.put(MainViewManager.BTN_EVENT_PROPERTY, PIRTViewManager.BTN_EVENT_PIRT_QOI_ADD_QOI);
		Map<String, Object> btnAddQoIOptions = new HashMap<>();
		btnAddQoIOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_PIRT_BTN_ADD));
		btnAddQoIOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnAddQoIOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_ADD);
		btnAddQoIOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
		btnAddQoIOptions.put(ButtonTheme.OPTION_DATA, btnAddQoIData);
		Button btnAddQoI = new ButtonTheme(getViewController().getViewManager().getRscMgr(),
				compositeButtonsHeaderRight, SWT.CENTER, btnAddQoIOptions);
		btnAddQoI.addListener(SWT.Selection, event -> getViewController().addQuantityOfInterest());

		/**
		 * PIRT queries composite
		 */
		if (getViewController().getViewManager().getCache().getPIRTQueries() != null
				&& !getViewController().getViewManager().getCache().getPIRTQueries().isEmpty()) {
			renderPIRTQueries(compositeButtonsHeaderLeft);
		}
	}

	/**
	 * Render PIRT query components : combobox and query execution button.
	 * <p>
	 * This feature is optionally activable from the preferences.
	 * 
	 * @param parent the parent composite
	 */
	private void renderPIRTQueries(Composite parent) {

		logger.debug("Render PIRT QoI query component"); //$NON-NLS-1$

		// Query - label
		Label lblQuery = new Label(parent, SWT.CENTER);
		lblQuery.setText(RscTools.getString(RscConst.MSG_QOIHOMEVIEW_LBL_QUERY));
		FontTools.setButtonFont(getViewController().getViewManager().getRscMgr(), lblQuery);

		// Query - Combo list available queries
		cbxQuery = new ComboViewer(parent, SWT.CENTER);
		cbxQuery.setContentProvider(new ArrayContentProvider());
		cbxQuery.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PIRTQuery) element).getName();
			}
		});
		cbxQuery.setInput(getViewController().getViewManager().getCache().getPIRTQueries());
		cbxQuery.getCombo().addKeyListener(new ComboDropDownKeyListener());
		cbxQuery.getControl().setLayoutData(new RowData(250, 30));
		FontTools.setButtonFont(getViewController().getViewManager().getRscMgr(), cbxQuery.getControl());

		// Query - Button - Execute Query
		Map<String, Object> btnQueryOptions = new HashMap<>();
		btnQueryOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_EXECUTE));
		btnQueryOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnQueryOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_QUERY);
		btnQueryOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		Button btnQuery = new ButtonTheme(getViewController().getViewManager().getRscMgr(), parent, SWT.CENTER,
				btnQueryOptions);
		btnQuery.addListener(SWT.Selection, event -> getViewController().queryPIRT());
	}

	/**
	 * Render main table
	 */
	private void renderMainTableComposite() {

		logger.debug("Render PIRT QoI Main table composite"); //$NON-NLS-1$

		// Grid Layout
		compositeTable = new Composite(this, SWT.FILL);
		GridLayout gridLayout = new GridLayout(1, true);
		compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeTable.setLayout(gridLayout);
	}

	/**
	 * Render Quantity Of Interest table
	 */
	private void renderMainTable() {

		logger.debug("Render PIRT QoI main table"); //$NON-NLS-1$

		// Initialize table
		renderMainTableInit();

		// Initialize data
		List<String> columnProperties = new ArrayList<>();
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) treeViewer.getTree().getLayout();

		// Columns - Id
		columnProperties.add(treeViewer.getIdColumn().getColumn().getText());
		treeViewer.getIdColumn()
				.setLabelProvider(new QoILabelProvider(getViewController().getViewManager().getRscMgr()) {
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
		TreeViewerColumn nameColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		nameColumn.getColumn().setText(PIRTQoITableQoI.getColumnSymbolProperty());
		nameColumn.setLabelProvider(new QoINameLabelProvider(getViewController().getViewManager().getRscMgr()));
		treeViewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.QOIHOME_VIEW_TABLEPHEN_NAMECOLUMN_WEIGHT, true));
		columnProperties.add(PIRTQoITableQoI.getColumnSymbolProperty());

		// Columns - Description
		TreeViewerColumn descriptionColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		descriptionColumn.getColumn().setText(PIRTQoITableQoI.getColumnDescriptionProperty());
		descriptionColumn
				.setLabelProvider(new QoIDescriptionLabelProvider(getViewController().getViewManager().getRscMgr()));
		treeViewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.QOIHOME_VIEW_TABLEPHEN_NAMECOLUMN_WEIGHT, true));
		columnProperties.add(PIRTQoITableQoI.getColumnDescriptionProperty());

		// Columns - Creation Date
		TreeViewerColumn creationDateColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		creationDateColumn.getColumn().setText(PIRTQoITableQoI.getColumnCreationDateProperty());
		creationDateColumn
				.setLabelProvider(new QoICreationDateLabelProvider(getViewController().getViewManager().getRscMgr()));
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.QOIHOME_VIEW_TABLEPHEN_CREATIONDATECOLUMN_WIDTH, true));
		columnProperties.add(PIRTQoITableQoI.getColumnCreationDateProperty());
		creationDateColumn.getColumn().notifyListeners(SWT.Selection, new Event());

		// Columns - Actions
		renderMainTableActionColumns(columnProperties);

		// Add listeners
		renderMainTableAddEvents();

		// table editors, modifiers, providers
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
		gdViewer.widthHint = getViewController().getViewManager().getSize().x
				- 2 * ((GridLayout) compositeTable.getLayout()).horizontalSpacing;

		// Tree - Customize
		tree.setHeaderBackground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
		tree.setHeaderForeground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));
	}

	/**
	 * Add action columns into the main table
	 * 
	 * @param columnProperties the column properties to create add action columns
	 *                         into
	 */
	private void renderMainTableActionColumns(List<String> columnProperties) {

		// Get Tree and layout
		Tree tree = treeViewer.getTree();
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) tree.getLayout();

		// Tree - Column - Action Edit
		TreeViewerColumn editColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		editColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_OPEN));
		editColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Edit editor
				TreeEditor editor = null;

				if (!openEditors.containsKey(item)) {

					// Button
					ButtonTheme btnEditItem = TableFactory
							.createOpenButtonColumnAction(getViewController().getViewManager().getRscMgr(), cell);
					btnEditItem.addListener(SWT.Selection,
							event -> getViewController().openQuantityOfInterest(element));

					// Draw cell
					editor = new TreeEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnEditItem, item, cell.getColumnIndex());

					openEditors.put(item, editor);
				}
			}
		});
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.QOIHOME_VIEW_TABLEPHEN_ACTION_COLUMN_WIDTH, false));
		columnProperties.add(PIRTQoITableQoI.getColumnActionEditProperty());

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
							.createCopyButtonColumnAction(getViewController().getViewManager().getRscMgr(), cell);
					btnDuplicateQoI.addListener(SWT.Selection,
							event -> getViewController().duplicateQuantityOfInterest(element));

					// Draw cell
					editor = new TreeEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnDuplicateQoI, item, cell.getColumnIndex());

					duplicateEditors.put(item, editor);
				}
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
					ButtonTheme btnTagQoI = TableFactory
							.createTagButtonColumnAction(getViewController().getViewManager().getRscMgr(), cell);
					btnTagQoI.addListener(SWT.Selection, event -> getViewController().doTagAction(element));
					btnTagQoI.setToolTipText(RscTools.getString(RscConst.MSG_PHENOMENAVIEW_BTN_ADDTAG_TOOLTIP));

					// Draw cell
					editor = new TreeEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnTagQoI, item, cell.getColumnIndex());

					tagEditors.put(item, editor);
				}
			}
		});
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.QOIHOME_VIEW_TABLEPHEN_ACTION_COLUMN_WIDTH, false));
		columnProperties.add(PIRTQoITableQoI.getColumnActionTagProperty());

		// Tree - Column - Action Delete
		TreeViewerColumn actionDeleteColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		actionDeleteColumn.getColumn().setText(PIRTQoITableQoI.getColumnActionDeleteProperty());
		actionDeleteColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				boolean canDelete = false;

				// Button Delete for Quantity Of Interest
				if (element instanceof QuantityOfInterest) {
					// Cast QoI
					QuantityOfInterest qoi = (QuantityOfInterest) element;

					// Get current user
					User currentUser = getViewController().getViewManager().getCache().getUser();

					// Can delete - not tagged
					canDelete = (null == qoi.getTagDate());

					// No user assign to tag (old project)
					canDelete |= (null == qoi.getTagUserCreation());

					// Can delete - Only the person that created the tag can delete it
					canDelete |= (null != qoi.getTagDate() && null != qoi.getTagUserCreation()
							&& qoi.getTagUserCreation().getId().equals(currentUser.getId()));
				}

				// Delete editor
				TreeEditor editor = null;

				if (canDelete && !deleteEditors.containsKey(item)) {

					// Button
					ButtonTheme btnDeleteQoI = TableFactory
							.createDeleteButtonColumnAction(getViewController().getViewManager().getRscMgr(), cell);
					btnDeleteQoI.addListener(SWT.Selection,
							event -> getViewController().deleteQuantityOfInterest(element));

					// Draw cell
					editor = new TreeEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnDeleteQoI, item, cell.getColumnIndex());

					deleteEditors.put(item, editor);
				}
			}
		});
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.QOIHOME_VIEW_TABLEPHEN_ACTION_COLUMN_WIDTH, false));
		columnProperties.add(PIRTQoITableQoI.getColumnActionDeleteProperty());
	}

	/**
	 * Add tree viewer events
	 */
	private void renderMainTableAddEvents() {

		treeViewer.getTree().addListener(SWT.MeasureItem, new Listener() {

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
		treeViewer.addDoubleClickListener(event -> {
			ISelection selection = event.getSelection();
			if (selection != null && !selection.isEmpty()) {
				Object element = ((IStructuredSelection) selection).getFirstElement();
				if (element instanceof QuantityOfInterest) {
					getViewController().openQuantityOfInterest(element);
				}
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
		treeViewer.addDropSupport(DND.DROP_MOVE, transferTypesDrop,
				new QoIDropSupport(getViewController(), treeViewer));
	}

	/**
	 * Render footer buttons
	 */
	private void renderFooterButtons() {

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
		Button btnBack = new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtons, SWT.CENTER,
				btnBackOptions);

		// Footer buttons - Guidance Level
		Map<String, Object> btnHelpLevelOptions = new HashMap<>();
		btnHelpLevelOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_LVLGUIDANCE));
		btnHelpLevelOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_HELP);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLUE);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_LISTENER,
				(Listener) e -> getViewController().getViewManager().openPIRTHelpLevelView());
		new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtons, SWT.PUSH | SWT.CENTER,
				btnHelpLevelOptions);

		// Footer buttons - Help - Create
		Map<String, Object> btnHelpOptions = new HashMap<>();
		btnHelpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		btnHelpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnHelpOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) e -> HelpTools.openContextualHelp());
		new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtons, SWT.CENTER, btnHelpOptions);
		HelpTools.addContextualHelp(compositeButtons, ContextualHelpId.PIRT_QOI);

		// Footer buttons - Back - plug
		getViewController().getViewManager().plugBackHomeButton(btnBack);

		// layout view
		compositeButtons.layout();
	}

	/**
	 * Refresh view
	 */
	void refreshViewer() {
		// Get tree data
		@SuppressWarnings("unchecked")
		List<QuantityOfInterest> input = (List<QuantityOfInterest>) treeViewer.getInput();

		// sort all lists
		if (input != null) {
			input.sort(Comparator.comparing(QuantityOfInterest::getId));
		}

		// Refresh
		treeViewer.refresh(true);
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
	 * Refresh table header.
	 */
	void refreshTableHeader() {
		tableHeaderBar.refreshViewer();
	}

	/**
	 * Refresh the main table
	 */
	void refreshMainTable() {

		// Dispose the table components
		if (treeViewer != null && treeViewer.getTree() != null && !treeViewer.getTree().isDisposed()) {
			treeViewer.getTree().removeAll();
			treeViewer.getTree().dispose();
		}
		treeViewer = null;

		// Refresh
		ViewTools.refreshTableEditors(openEditors);
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
	 * @param item
	 */
	private void refreshTableButtons(TreeItem item) {
		ViewTools.refreshTreeEditor(openEditors.get(item));
		ViewTools.refreshTreeEditor(duplicateEditors.get(item));
		ViewTools.refreshTreeEditor(tagEditors.get(item));
		ViewTools.refreshTreeEditor(deleteEditors.get(item));
	}

	/** {@inheritDoc} */
	@Override
	public void reload() {
		getViewController().reloadData();
	}

	/**
	 * @return the PIRT query selected in the query combo-box
	 */
	PIRTQuery getPIRTQuerySelected() {
		if (cbxQuery == null) {
			return null;
		}

		ISelection selection = cbxQuery.getSelection();

		if (selection.isEmpty()) {
			return null;
		}

		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		return (PIRTQuery) structuredSelection.getFirstElement();
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
		if (treeViewer != null) {
			if (elements == null || elements.length == 0) {
				treeViewer.expandAll();
			} else {
				treeViewer.setExpandedElements(elements);
			}
			treeViewer.refresh();
		}
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

}
