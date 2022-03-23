/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import gov.sandia.cf.application.pcmm.IPCMMEvidenceApp;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.GenericCFDialog;
import gov.sandia.cf.parts.listeners.SortTableColumnListener;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.viewer.TableFactory;
import gov.sandia.cf.parts.viewer.TableViewerHideSelection;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.parts.viewer.editors.ColumnViewerSupport;
import gov.sandia.cf.parts.viewer.editors.GenericTableListContentProvider;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * PCMMEvidence list dialog
 * 
 * @author Maxime N
 */
public class PCMMEvidenceListDialog extends GenericCFDialog<PCMMViewManager> {
	/**
	 * Data
	 */
	private int minDialogWidth = 600;
	private int minDialogHeight = 250;

	/**
	 * Input description
	 */
	protected TableViewerHideSelection evidenceTable;

	/**
	 * View Editor
	 */
	private Map<TableItem, TableEditor> openEditors;
	private Map<TableItem, TableEditor> viewEditors;

	/**
	 * The element/sub-element selected
	 */
	protected IAssessable item;

	/**
	 * List of evidence
	 */
	protected List<PCMMEvidence> evidence;

	/**
	 * *
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 * @param item        the item to tag
	 * @param tag         the tag
	 */
	public PCMMEvidenceListDialog(PCMMViewManager viewManager, Shell parentShell, IAssessable item, Tag tag) {
		super(viewManager, parentShell);
		this.init(item, tag, null);
	}

	/**
	 * Constructor
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 * @param item        the item to tag
	 * @param tag         the tag
	 * @param role        the pcmm role
	 */
	public PCMMEvidenceListDialog(PCMMViewManager viewManager, Shell parentShell, IAssessable item, Tag tag,
			Role role) {
		// Call super
		super(viewManager, parentShell);
		this.init(item, tag, role);
	}

	/**
	 * Initialize
	 * 
	 * @param viewManager
	 * @param parentShell
	 * @param item
	 * @param tag
	 * @param role
	 */
	private void init(IAssessable item, Tag tag, Role role) {
		// Initialize properties
		this.item = item;
		this.openEditors = new HashMap<>();
		this.viewEditors = new HashMap<>();

		// Initialize filters
		Map<EntityFilter, Object> filters = new HashMap<>();

		// Filters - Element / Sub-element
		if (PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())) {
			filters.put(PCMMEvidence.Filter.SUBELEMENT, (PCMMSubelement) item);
		} else if (PCMMMode.SIMPLIFIED.equals(getViewManager().getPCMMConfiguration().getMode())) {
			filters.put(PCMMEvidence.Filter.ELEMENT, (PCMMElement) item);
		}
		// Filters - Role
		if (null != role) {
			filters.put(PCMMEvidence.Filter.ROLECREATION, role);
		}

		// Filters - Tag
		filters.put(PCMMEvidence.Filter.TAG, tag);

		// Get evidence
		evidence = getViewManager().getAppManager().getService(IPCMMEvidenceApp.class).getEvidenceBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// Set the new title of the dialog
		newShell.setText(RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_LIST));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		if (PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())) {
			setTitle((null != ((PCMMSubelement) item).getElement()) ? ((PCMMSubelement) item).getElement().getName()
					: null);
			String message = (((PCMMSubelement) item).getCode() != null ? ((PCMMSubelement) item).getCode()
					: RscTools.HYPHEN) + RscTools.COLON
					+ (((PCMMSubelement) item).getName() != null ? ((PCMMSubelement) item).getName() : RscTools.HYPHEN);
			setMessage(message);
		} else if (PCMMMode.SIMPLIFIED.equals(getViewManager().getPCMMConfiguration().getMode())) {
			setTitle(((PCMMElement) item).getName());
			setMessage(null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		// form container
		Composite innerContainer = new Composite(container, SWT.NONE);
		innerContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gridLayout = new GridLayout(1, true);
		innerContainer.setLayout(gridLayout);
		gridLayout.verticalSpacing = PartsResourceConstants.DEFAULT_GRIDDATA_V_INDENT;

		/**
		 * viewer tag
		 */
		// viewer general properties initialization
		evidenceTable = new TableViewerHideSelection(innerContainer,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		GridData gdViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		evidenceTable.getTable().setLayoutData(gdViewer);
		evidenceTable.getTable().setHeaderVisible(true);
		evidenceTable.getTable().setLinesVisible(true);
		final AutoResizeViewerLayout viewerLayout = new AutoResizeViewerLayout(evidenceTable);
		evidenceTable.getTable().setLayout(viewerLayout);
		gdViewer.heightHint = evidenceTable.getTable().getItemHeight();

		// sort column listener
		SortTableColumnListener sortListener = new SortTableColumnListener(evidenceTable);
		evidenceTable.getTable().setHeaderForeground(ColorTools.toColor(getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));
		evidenceTable.getTable().setHeaderBackground(ColorTools.toColor(getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));

		/**
		 * construct fixed viewer columns
		 */
		List<String> columnList = new ArrayList<>();

		// Name
		TableViewerColumn nameColumn = new TableViewerColumn(evidenceTable, SWT.LEFT);
		nameColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_FILENAME));
		nameColumn.getColumn().addListener(SWT.Selection, sortListener);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return (element instanceof PCMMEvidence) ? ((PCMMEvidence) element).getName() : RscTools.empty();
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_NAMECOLUMN_WEIGHT, true));
		columnList.add(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_FILENAME));

		// File path
		TableViewerColumn pathColumn = new TableViewerColumn(evidenceTable, SWT.LEFT);
		pathColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_FILEPATH));
		pathColumn.getColumn().addListener(SWT.Selection, sortListener);
		pathColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return (element instanceof PCMMEvidence) ? ((PCMMEvidence) element).getPath() : RscTools.empty();
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_PATHCOLUMN_WEIGHT, true));
		columnList.add(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_FILEPATH));

		// Description
		TableViewerColumn descColumn = new TableViewerColumn(evidenceTable, SWT.LEFT);
		descColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_DESC));
		descColumn.getColumn().addListener(SWT.Selection, sortListener);
		descColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof PCMMEvidence) {
					return StringTools.clearHtml(((PCMMEvidence) element).getDescription(), true);
				} else {
					return RscTools.empty();
				}
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_DESCCOLUMN_WEIGHT, true));
		columnList.add(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_DESC));

		// User
		TableViewerColumn userColumn = new TableViewerColumn(evidenceTable, SWT.LEFT);
		userColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_USER));
		userColumn.getColumn().addListener(SWT.Selection, sortListener);
		userColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof PCMMEvidence) {
					return ((PCMMEvidence) element).getUserCreation().getUserID();
				} else {
					return RscTools.empty();
				}
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_USERCOLUMN_WEIGHT, true));
		columnList.add(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_USER));

		// role
		TableViewerColumn roleColumn = new TableViewerColumn(evidenceTable, SWT.LEFT);
		roleColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_ROLE));
		roleColumn.getColumn().addListener(SWT.Selection, sortListener);
		roleColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return (element instanceof PCMMEvidence) ? ((PCMMEvidence) element).getRoleCreation().getName()
						: RscTools.empty();
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_ROLECOLUMN_WEIGHT, true));
		columnList.add(RscTools.getString(RscConst.MSG_PCMMEVID_TABLE_COL_ROLE));

		// Tree - Column - Action Open
		TableViewerColumn actionOpenColumn = new TableViewerColumn(evidenceTable, SWT.LEFT);
		actionOpenColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_OPEN));
		actionOpenColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TableItem itemTemp = (TableItem) cell.getItem();
				Object element = cell.getElement();

				// Button View for PCMM Evidence
				if (element instanceof PCMMEvidence) {

					// View editor
					TableEditor editor = null;
					if (!openEditors.containsKey(itemTemp)) {

						// Button
						ButtonTheme btnViewItem = TableFactory
								.createOpenButtonColumnAction(getViewManager().getRscMgr(), cell);

						// Footer buttons - Delete- Listener
						btnViewItem.addListener(SWT.Selection,
								event -> getViewManager().openDocument((PCMMEvidence) element));

						// Draw cell
						editor = new TableEditor(itemTemp.getParent());
						editor.grabHorizontal = true;
						editor.grabVertical = true;
						editor.setEditor(btnViewItem, itemTemp, cell.getColumnIndex());

						openEditors.put(itemTemp, editor);
					}
				} else {
					cell.setBackground(getBackground(element));
					cell.setForeground(getForeground(element));
				}
			}
		});
		viewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_ACTIONCOLUMN_WIDTH, true));
		columnList.add(RscTools.getString(RscConst.MSG_BTN_OPEN));

		// Tree - Column - Action View
		TableViewerColumn actionViewColumn = new TableViewerColumn(evidenceTable, SWT.LEFT);
		actionViewColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_VIEW));
		actionViewColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TableItem itemTemp = (TableItem) cell.getItem();
				Object element = cell.getElement();

				// Button View for PCMM Evidence
				if (element instanceof PCMMEvidence) {

					// View editor
					TableEditor editor = null;
					if (!viewEditors.containsKey(itemTemp)) {

						// Button
						ButtonTheme btnViewItem = TableFactory
								.createViewButtonColumnAction(getViewManager().getRscMgr(), cell);

						// View - Listener
						btnViewItem.addListener(SWT.Selection,
								event -> new PCMMEvidenceViewDialog(getViewManager(), getShell())
										.openDialog((PCMMEvidence) element));

						// Draw cell
						editor = new TableEditor(itemTemp.getParent());
						editor.grabHorizontal = true;
						editor.grabVertical = true;
						editor.setEditor(btnViewItem, itemTemp, cell.getColumnIndex());

						viewEditors.put(itemTemp, editor);
					}
				} else {
					cell.setBackground(getBackground(element));
					cell.setForeground(getForeground(element));
				}
			}
		});
		viewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_ACTIONCOLUMN_WIDTH, true));
		columnList.add(RscTools.getString(RscConst.MSG_BTN_VIEW));

		// table settings
		String[] columnProperties = columnList.stream().toArray(String[]::new);
		evidenceTable.setColumnProperties(columnProperties);
		evidenceTable.setContentProvider(new GenericTableListContentProvider());

		// modifications on double click
		ColumnViewerSupport.enableDoubleClickEditing(evidenceTable);

		// set table row height to DEFAULT
		evidenceTable.getTable().addListener(SWT.MeasureItem, new Listener() {
			private TableItem previousItem = null;

			@Override
			public void handleEvent(Event event) {
				event.height = PartsResourceConstants.TABLE_ROW_HEIGHT;
				TableItem itemTmp = (TableItem) event.item;
				if (itemTmp != null && !itemTmp.equals(previousItem)) {
					previousItem = itemTmp;
					refreshTableButtons(itemTmp);
				}
			}
		});

		// load the data
		evidenceTable.setInput(evidence);
		evidenceTable.refresh();

		// Return main container
		return container;
	}

	/**
	 * Refresh the table buttons
	 * 
	 * @param item
	 */
	private void refreshTableButtons(TableItem item) {
		ViewTools.refreshTableEditor(openEditors.get(item));
		ViewTools.refreshTableEditor(viewEditors.get(item));
	}

	/**
	 * Reload the view data
	 */
	public void reload() {
		// not used
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(minDialogWidth), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(minDialogHeight), shellSize.y));
	}

	/**
	 * open the dialog
	 */
	public void openDialog() {
		open();
	}
}
