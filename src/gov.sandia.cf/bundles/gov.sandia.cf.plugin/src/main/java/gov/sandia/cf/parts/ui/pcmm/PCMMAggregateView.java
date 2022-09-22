/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.application.pcmm.IPCMMAssessmentApp;
import gov.sandia.cf.application.pcmm.IPCMMEvidenceApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PCMMAggregation;
import gov.sandia.cf.model.PCMMAggregationLevel;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.listeners.ComboDropDownKeyListener;
import gov.sandia.cf.parts.listeners.ViewerSelectionKeepBackgroundColor;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMAggregateViewerCellModifier;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMAssessTreeContentProvider;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMAssessTreeSimplifiedContentProvider;
import gov.sandia.cf.parts.viewer.TableFactory;
import gov.sandia.cf.parts.viewer.TreeViewerHideSelection;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.parts.viewer.editors.ColumnViewerSupport;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The PCMM Aggregate view
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAggregateView extends ACredibilityPCMMView<PCMMAggregateViewController> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMAggregateView.class);

	/**
	 * the viewer
	 */
	private TreeViewer treeViewerPCMM;

	/**
	 * Table editors
	 */
	private Map<TreeItem, TreeEditor> openEvidenceEditors;
	private Map<TreeItem, TreeEditor> viewDetailsEditors;

	/**
	 * The assess table composite
	 */
	private Composite compositeTable;

	/**
	 * Instantiates a new PCMM aggregate view.
	 *
	 * @param viewController the view controller
	 * @param style          the view style
	 */
	public PCMMAggregateView(PCMMAggregateViewController viewController, int style) {
		super(viewController, viewController.getViewManager(), style);

		// Make sure you dispose these buttons when viewer input changes
		openEvidenceEditors = new HashMap<>();
		viewDetailsEditors = new HashMap<>();

		// create the view
		renderPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_PCMM_AGGREGATE_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_PCMM_AGGREGATE_ITEMTITLE);
	}

	/**
	 * Render page
	 * 
	 * @param parent
	 */
	private void renderPage() {

		logger.debug("Creating PCMM view components"); //$NON-NLS-1$

		// Render filters
		renderFilters();

		// Render Tree
		renderMainTableComposite();

		// Render footer
		renderFooter();

		// layout view
		this.layout();
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
	void refreshMainTable() {
		if (treeViewerPCMM != null) {
			// dispose the table components
			if (treeViewerPCMM.getTree() != null && !treeViewerPCMM.getTree().isDisposed()) {
				treeViewerPCMM.getTree().removeAll();
				treeViewerPCMM.getTree().dispose();
			}
			treeViewerPCMM = null;
		}
		if (openEvidenceEditors != null) {
			openEvidenceEditors.forEach((item, editor) -> ViewTools.disposeViewerEditor(editor));
			openEvidenceEditors.clear();
		}
		if (viewDetailsEditors != null) {
			viewDetailsEditors.forEach((item, editor) -> ViewTools.disposeViewerEditor(editor));
			viewDetailsEditors.clear();
		}

		// render the main table
		renderMainTable();

		// relayout the table composite
		compositeTable.layout();
	}

	/**
	 * Render aggregate tree
	 */
	private void renderMainTable() {

		// get data
		PCMMSpecification pcmmConfiguration = getViewController().getPcmmConfiguration();

		// viewer general properties initialization
		treeViewerPCMM = new TreeViewerHideSelection(compositeTable,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		GridData gdTablePCMM = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		treeViewerPCMM.getTree().setLayoutData(gdTablePCMM);
		treeViewerPCMM.getTree().setHeaderVisible(true);
		treeViewerPCMM.getTree().setLinesVisible(true);
		final AutoResizeViewerLayout viewerLayout = new AutoResizeViewerLayout(treeViewerPCMM);
		treeViewerPCMM.getTree().setLayout(viewerLayout);
		gdTablePCMM.heightHint = treeViewerPCMM.getTree().getItemHeight();
		gdTablePCMM.widthHint = getViewController().getViewManager().getSize().x
				- 2 * ((GridLayout) compositeTable.getLayout()).horizontalSpacing;
		List<String> columnProperties = new ArrayList<>();

		// Tree - Columns - Id
		TreeViewerColumn idColumn = new TreeViewerColumn(treeViewerPCMM, SWT.CENTER);
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element != null) {
					if (PCMMMode.SIMPLIFIED.equals(pcmmConfiguration.getMode()) && element instanceof PCMMElement) {
						// Get PCMMElement abbreviation
						return ((PCMMElement) element).getAbbreviation();
					} else if (PCMMMode.DEFAULT.equals(pcmmConfiguration.getMode())
							&& element instanceof PCMMSubelement) {
						// Get PCMMSubelement Code
						return ((PCMMSubelement) element).getCode();
					}
				}
				return RscTools.empty();
			}

			@Override
			public Color getBackground(Object element) {
				return getTreeCellBackgroud(element);
			}

			@Override
			public Color getForeground(Object element) {
				return getTreeCellForeground(element);
			}
		});
		idColumn.getColumn().setText(RscTools.empty());
		viewerLayout
				.addColumnData(new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEASSESS_IDCOLUMN_PIXEL, true));
		columnProperties.add(idColumn.getColumn().getText());

		// Tree - Columns - Element/Sub-element
		TreeViewerColumn elementColumn = new TreeViewerColumn(treeViewerPCMM, SWT.LEFT);
		elementColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMASSESS_TABLE_COL_ELMTSUBELMT));
		elementColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (PCMMMode.SIMPLIFIED.equals(pcmmConfiguration.getMode()) && element instanceof PCMMElement) {
					// Get PCMMElement Name
					return ((PCMMElement) element).getName();
				} else if (PCMMMode.DEFAULT.equals(pcmmConfiguration.getMode())) {
					if (element instanceof PCMMElement) {
						// Get PCMMElement Name
						return ((PCMMElement) element).getName();
					} else if (element instanceof PCMMSubelement) {
						// Get PCMMSubelement Name
						return ((PCMMSubelement) element).getName();
					}
				}
				return RscTools.empty();
			}

			@Override
			public Color getBackground(Object element) {
				return getTreeCellBackgroud(element);
			}

			@Override
			public Color getForeground(Object element) {
				return getTreeCellForeground(element);
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEASSESS_NAMECOLUMN_WEIGHT, true));
		columnProperties.add(elementColumn.getColumn().getText());

		// Tree - Columns - Level Achieved
		TreeViewerColumn levelAchievedColumn = new TreeViewerColumn(treeViewerPCMM, SWT.CENTER);
		levelAchievedColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMASSESS_TABLE_COL_LVLACHIEVED));
		levelAchievedColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element != null) {
					if (element instanceof PCMMElement) {
						// Get PCMMElement
						PCMMElement elt = (PCMMElement) element;

						// Get PCMMAggregation
						PCMMAggregation<PCMMElement> aggregation = getViewController().getAggregatedElementsMap()
								.get(elt);

						// Get PCMMLevel name
						String levelSelectedName = RscTools.empty();
						if (aggregation != null) {
							PCMMAggregationLevel level = aggregation.getLevel();
							if (level != null) {
								levelSelectedName = level.getName();
							}
						}
						return levelSelectedName;
					} else if (element instanceof PCMMSubelement) {
						// Get PCMMSubelement
						PCMMSubelement subelt = (PCMMSubelement) element;

						// Get PCMMAggregation
						PCMMAggregation<PCMMSubelement> aggregation = getViewController().getAggregatedSubelementsMap()
								.get(subelt);

						// Get PCMMLevel name
						String levelSelectedName = RscTools.empty();
						if (aggregation != null) {
							PCMMAggregationLevel level = aggregation.getLevel();
							if (level != null) {
								levelSelectedName = level.getName();
							}
						}
						return levelSelectedName;
					}
				}
				return RscTools.empty();
			}

			@Override
			public Color getBackground(Object element) {
				Color color = null;
				if (pcmmConfiguration != null) {

					color = getTreeCellBackgroud(element);
					RGB levelColor = null;
					PCMMAggregation<?> aggregation = null;

					// Get PCMMAggregation
					if (element instanceof PCMMSubelement) {
						aggregation = getViewController().getAggregatedSubelementsMap().get(element);
					} else if (element instanceof PCMMElement) {
						aggregation = getViewController().getAggregatedElementsMap().get(element);
					}

					// get rgb color
					if (aggregation != null && aggregation.getLevel() != null
							&& aggregation.getLevel().getCode() != null && pcmmConfiguration.getLevelColors() != null
							&& pcmmConfiguration.getLevelColors().containsKey(aggregation.getLevel().getCode())) {
						levelColor = ColorTools.stringRGBToColor(pcmmConfiguration.getLevelColors()
								.get(aggregation.getLevel().getCode()).getFixedColor());

						// get color
						if (PCMMMode.DEFAULT.equals(pcmmConfiguration.getMode())) {
							if (element instanceof PCMMSubelement) {
								color = new Color(Display.getCurrent(), levelColor);
							} else if (element instanceof PCMMElement) {
								// PCMM Element in gray
								color = new Color(Display.getCurrent(), ColorTools.grayedRgb(levelColor));
							}

						} else if (PCMMMode.SIMPLIFIED.equals(pcmmConfiguration.getMode())
								&& element instanceof PCMMElement) {
							color = new Color(Display.getCurrent(), levelColor);
						}
					}
				}
				return color;
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEASSESS_LVLCOLUMN_WEIGHT, true));
		columnProperties.add(levelAchievedColumn.getColumn().getText());

		// Evidence
		TreeViewerColumn evidenceColumn = new TreeViewerColumn(treeViewerPCMM, SWT.CENTER);
		evidenceColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMASSESS_TABLE_COL_EVIDLINKS));
		evidenceColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// Check mode
				if (PCMMMode.DEFAULT.equals(pcmmConfiguration.getMode()) && element instanceof PCMMSubelement) { // Check
																													// sub-element
					// Initialize filters
					Map<EntityFilter, Object> entityFilters = new HashMap<>();

					// Sub-element
					entityFilters.put(PCMMEvidence.Filter.SUBELEMENT, element);

					// Tag
					entityFilters.put(PCMMEvidence.Filter.TAG, getViewController().getViewManager().getSelectedTag());

					// Get evidences
					List<PCMMEvidence> evidences = getViewController().getViewManager().getAppManager()
							.getService(IPCMMEvidenceApp.class).getEvidenceBy(entityFilters);

					// Get number of PCMMEvidence
					int nbEvidence = evidences.size();
					String value = " - "; //$NON-NLS-1$
					if (nbEvidence > 0) {
						value = nbEvidence == 1 ? RscTools.getString(RscConst.MSG_PCMMASSESS_TABLE_COL_EVID_LABEL_SING)
								: RscTools.getString(RscConst.MSG_PCMMASSESS_TABLE_COL_EVID_LABEL_PLUR, nbEvidence);
					}
					return value;
				} else if (PCMMMode.SIMPLIFIED.equals(pcmmConfiguration.getMode()) && element instanceof PCMMElement) { // Check
																														// element
					// Initialize filters
					Map<EntityFilter, Object> entityFilters = new HashMap<>();

					// Element
					entityFilters.put(PCMMEvidence.Filter.ELEMENT, element);

					// Tag
					entityFilters.put(PCMMEvidence.Filter.TAG, getViewController().getViewManager().getSelectedTag());

					// Get evidences
					List<PCMMEvidence> evidences = getViewController().getViewManager().getAppManager()
							.getService(IPCMMEvidenceApp.class).getEvidenceBy(entityFilters);

					// Get number of PCMMEvidence
					int nbEvidence = evidences.size();
					String value = " - "; //$NON-NLS-1$
					if (nbEvidence > 0) {
						value = nbEvidence == 1 ? RscTools.getString(RscConst.MSG_PCMMASSESS_TABLE_COL_EVID_LABEL_SING)
								: RscTools.getString(RscConst.MSG_PCMMASSESS_TABLE_COL_EVID_LABEL_PLUR, nbEvidence);
					}
					return value;
				}

				return RscTools.empty();
			}

			@Override
			public Color getBackground(Object element) {
				return getTreeCellBackgroud(element);
			}

			@Override
			public Color getForeground(Object element) {
				return getTreeCellForeground(element);
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEASSESS_EVIDCOLUMN_WEIGHT, true));
		columnProperties.add(evidenceColumn.getColumn().getText());

		// Tree - Column - Action Open Evidence PCMMAggregateView
		TreeViewerColumn actionOpenEvidenceColumn = new TreeViewerColumn(treeViewerPCMM, SWT.CENTER);
		actionOpenEvidenceColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_OPEN));
		actionOpenEvidenceColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Button Open for PCMM Aggregate
				if ((element instanceof PCMMSubelement && PCMMMode.DEFAULT == pcmmConfiguration.getMode())
						|| (element instanceof PCMMElement && PCMMMode.SIMPLIFIED == pcmmConfiguration.getMode())) {

					// Open editor
					TreeEditor editor = null;

					if (!openEvidenceEditors.containsKey(item)) {

						// Button
						ButtonTheme btnOpenItem = TableFactory
								.createOpenButtonColumnAction(getViewController().getViewManager().getRscMgr(), cell);

						// Open - Listener
						btnOpenItem.addListener(SWT.Selection, event -> {
							/**
							 * Check the PCMM mode
							 */
							if (PCMMMode.DEFAULT.equals(pcmmConfiguration.getMode())) {
								// get the selected sub-element to associate the evidence with
								PCMMSubelement subelementSelected = (PCMMSubelement) element;
								if (null != subelementSelected) {
									PCMMEvidenceListDialog evidencesDialog = new PCMMEvidenceListDialog(
											getViewController().getViewManager(), getShell(), subelementSelected,
											getViewController().getViewManager().getSelectedTag());
									evidencesDialog.openDialog();
								}
							} else if (PCMMMode.SIMPLIFIED.equals(pcmmConfiguration.getMode())) {
								// get the selected element to associate the evidence with
								PCMMElement elementSelected = (PCMMElement) element;
								if (null != elementSelected) {
									PCMMEvidenceListDialog evidencesDialog = new PCMMEvidenceListDialog(
											getViewController().getViewManager(), getShell(), elementSelected,
											getViewController().getViewManager().getSelectedTag());
									evidencesDialog.openDialog();
								}
							}
						});

						// Draw cell
						editor = new TreeEditor(item.getParent());
						editor.grabHorizontal = true;
						editor.grabVertical = true;
						editor.setEditor(btnOpenItem, item, cell.getColumnIndex());

						openEvidenceEditors.put(item, editor);
					}
				} else {
					cell.setBackground(getBackground(element));
					cell.setForeground(getForeground(element));
				}
			}

			@Override
			public Color getBackground(Object element) {
				return getTreeCellBackgroud(element);
			}

			@Override
			public Color getForeground(Object element) {
				return getTreeCellForeground(element);
			}
		});
		viewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEASSESS_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_OPEN));

		// Comments
		TreeViewerColumn commentColumn = new TreeViewerColumn(treeViewerPCMM, SWT.LEFT);
		commentColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMASSESS_TABLE_COL_COMMENTS));
		commentColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element != null) {
					if (PCMMMode.SIMPLIFIED.equals(pcmmConfiguration.getMode()) && element instanceof PCMMElement) {
						// Get PCMMSubelement
						PCMMElement elt = (PCMMElement) element;

						// Get PCMMAggregation
						PCMMAggregation<PCMMElement> aggregation = getViewController().getAggregatedElementsMap()
								.get(elt);

						String comments = RscTools.empty();
						if (aggregation != null && aggregation.getCommentList() != null
								&& !aggregation.getCommentList().isEmpty()) {
							comments = aggregation.getCommentList().get(0);
							if (aggregation.getCommentList().size() > 1) {
								comments += RscTools.THREE_DOTS;
							}
						}
						return comments;

					} else if (PCMMMode.DEFAULT.equals(pcmmConfiguration.getMode())
							&& element instanceof PCMMSubelement) {
						// Get PCMMSubelement
						PCMMSubelement subelt = (PCMMSubelement) element;

						// Get PCMMAggregation
						PCMMAggregation<PCMMSubelement> aggregation = getViewController().getAggregatedSubelementsMap()
								.get(subelt);

						String comments = RscTools.empty();
						if (aggregation != null && aggregation.getCommentList() != null
								&& !aggregation.getCommentList().isEmpty()) {
							comments = aggregation.getCommentList().get(0);
							if (aggregation.getCommentList().size() > 1) {
								comments += RscTools.THREE_DOTS;
							}
						}
						return comments;
					}
				}
				return RscTools.empty();
			}

			@Override
			public Color getBackground(Object element) {
				return getTreeCellBackgroud(element);
			}

			@Override
			public Color getForeground(Object element) {
				return getTreeCellForeground(element);
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEASSESS_COMMENTSCOLUMN_WEIGHT, true));
		columnProperties.add(commentColumn.getColumn().getText());

		// Tree - Column - View Details PCMMAggregateView
		TreeViewerColumn actionViewDetailsColumn = new TreeViewerColumn(treeViewerPCMM, SWT.CENTER);
		actionViewDetailsColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMASSESS_TABLE_COL_VIEW));
		actionViewDetailsColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Button View Details for PCMM Aggregate
				if (((element instanceof PCMMSubelement && PCMMMode.DEFAULT == pcmmConfiguration.getMode())
						|| (element instanceof PCMMElement && PCMMMode.SIMPLIFIED == pcmmConfiguration.getMode()))
						&& hasAssessment(element)) {

					// View editor
					TreeEditor editor = null;

					if (!viewDetailsEditors.containsKey(item)) {

						// Button
						ButtonTheme btnViewItem = TableFactory
								.createViewButtonColumnAction(getViewController().getViewManager().getRscMgr(), cell);

						// Footer buttons - Delete- Listener
						btnViewItem.addListener(SWT.Selection, event -> {
							/**
							 * Check the PCMM mode
							 */
							if (PCMMMode.DEFAULT.equals(pcmmConfiguration.getMode())) {
								// get the selected sub-element to associate the evidence with
								PCMMSubelement subelementSelected = (PCMMSubelement) element;
								if (null != subelementSelected) {
									showAggregationDetails(subelementSelected);
								}
							} else if (PCMMMode.SIMPLIFIED.equals(pcmmConfiguration.getMode())) {
								// get the selected element to associate the evidence with
								PCMMElement elementSelected = (PCMMElement) element;
								if (null != elementSelected) {
									showAggregationDetails(elementSelected);
								}
							}
						});

						// Draw cell
						editor = new TreeEditor(item.getParent());
						editor.grabHorizontal = true;
						editor.grabVertical = true;
						editor.setEditor(btnViewItem, item, cell.getColumnIndex());

						viewDetailsEditors.put(item, editor);
					}
				} else {
					cell.setBackground(getBackground(element));
					cell.setForeground(getForeground(element));
				}
			}

			@Override
			public Color getBackground(Object element) {
				return getTreeCellBackgroud(element);
			}

			@Override
			public Color getForeground(Object element) {
				return getTreeCellForeground(element);
			}
		});
		viewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEASSESS_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_VIEW));

		// Set properties
		treeViewerPCMM.setColumnProperties(columnProperties.stream().toArray(String[]::new));
		if (PCMMMode.DEFAULT.equals(pcmmConfiguration.getMode())) {
			treeViewerPCMM.setContentProvider(new PCMMAssessTreeContentProvider());
		} else if (PCMMMode.SIMPLIFIED.equals(pcmmConfiguration.getMode())) {
			treeViewerPCMM.setContentProvider(new PCMMAssessTreeSimplifiedContentProvider());
		}
		// Tree - Customize
		treeViewerPCMM.getTree()
				.setHeaderBackground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
		treeViewerPCMM.getTree()
				.setHeaderForeground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));
		treeViewerPCMM.getTree().addListener(SWT.MeasureItem, new Listener() {

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

		// Set Editor
		treeViewerPCMM.setCellEditors(new CellEditor[] { new TextCellEditor(treeViewerPCMM.getTree()),
				new TextCellEditor(treeViewerPCMM.getTree()), new TextCellEditor(treeViewerPCMM.getTree()),
				new TextCellEditor(treeViewerPCMM.getTree()), new TextCellEditor(treeViewerPCMM.getTree()) });
		treeViewerPCMM.setCellModifier(new PCMMAggregateViewerCellModifier(getViewController(), columnProperties));

		// table modifications on double click
		ColumnViewerSupport.enableDoubleClickEditing(treeViewerPCMM);

		// disable selection when clicking on the container
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				treeViewerPCMM.setSelection(new StructuredSelection());
			}
		});
		// show PCMM subelement details on double click or evidences
		treeViewerPCMM.getTree().addListener(SWT.MouseDoubleClick, event -> {
			if (event.type == SWT.MouseDoubleClick) {
				if (PCMMMode.DEFAULT.equals(pcmmConfiguration.getMode())) {
					// if it is the evidence column, open the evidence view for the selected
					// subelement
					PCMMSubelement firstSubelementSelected = getFirstSubelementSelected();
					if (firstSubelementSelected != null && hasAssessment(firstSubelementSelected)) {
						showAggregationDetails(firstSubelementSelected);
					}
				} else if (PCMMMode.SIMPLIFIED.equals(pcmmConfiguration.getMode())) {
					// if it is the evidence column, open the evidence view for the selected
					// subelement
					PCMMElement firstElementSelected = getFirstElementSelected();
					if (firstElementSelected != null && hasAssessment(firstElementSelected)) {
						showAggregationDetails(firstElementSelected);
					}
				}
			}
		});

		// SWT.EraseItem: this event is called when repainting the tree not on removing
		// an element of the tree
		// In this case, it keeps cell colors when selection is active on cell
		treeViewerPCMM.getTree().addListener(SWT.EraseItem, new ViewerSelectionKeepBackgroundColor(treeViewerPCMM) {

			@Override
			public boolean isConditionFulfilled(Object data) {
				return data instanceof PCMMSubelement;
			}

		});

		// Layout
		treeViewerPCMM.getTree().layout();
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
		btnBackOptions.put(ButtonTheme.OPTION_LISTENER,
				(Listener) event -> getViewController().getViewManager().openHome());
		new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER,
				btnBackOptions);

		// Footer buttons - Guidance Level
		Map<String, Object> btnHelpLevelOptions = new HashMap<>();
		btnHelpLevelOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_LVLGUIDANCE));
		btnHelpLevelOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_HELP);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLUE);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_LISTENER,
				(Listener) event -> getViewController().getViewManager().openPCMMHelpLevelView());
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
		HelpTools.addContextualHelp(compositeButtonsFooter, ContextualHelpId.PCMM_AGGREGATE);

		// layout view
		compositeButtonsFooter.layout();
	}

	/**
	 * @return the viewer
	 */
	public ColumnViewer getViewer() {
		return treeViewerPCMM;
	}

	/**
	 * Refresh the table buttons
	 * 
	 * @param item
	 */
	private void refreshTableButtons(TreeItem item) {
		ViewTools.refreshTreeEditor(openEvidenceEditors.get(item));
		ViewTools.refreshTreeEditor(viewDetailsEditors.get(item));
	}

	/**
	 * Refreshes the view
	 */
	public void refreshViewer() {
		treeViewerPCMM.refresh();

		// layout view
		this.layout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {
		getViewController().reloadData(false);
	}

	/**
	 * Create filters
	 */
	private void renderFilters() {
		// form container
		Composite formFilterContainer = new Composite(this, SWT.NONE);
		formFilterContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout gridLayoutFormFilter = new GridLayout(1, false);
		formFilterContainer.setLayout(gridLayoutFormFilter);

		// label Filters
		Label lblFilter = new Label(formFilterContainer, SWT.LEFT);
		lblFilter.setText(RscTools.getString(RscConst.MSG_PCMMAGGREG_FILTER_LABEL));
		lblFilter.setForeground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
		GridData lblFilterGridData = new GridData();
		lblFilter.setLayoutData(lblFilterGridData);
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), lblFilter);

		// form container
		Composite formRoleContainer = new Composite(formFilterContainer, SWT.NONE);
		formRoleContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout gridLayoutForm = new GridLayout(2, false);
		formRoleContainer.setLayout(gridLayoutForm);

		// label role
		Label lblRole = new Label(formRoleContainer, SWT.RIGHT);
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), lblRole);
		lblRole.setText(RscTools.getString(RscConst.MSG_PCMMAGGREG_FILTER_ROLE_LABEL));
		GridData lblSubtitleGridData = new GridData();
		lblRole.setLayoutData(lblSubtitleGridData);

		// Get roles and the selected one
		List<Role> roles = getViewController().getViewManager().getAppManager().getService(IPCMMApplication.class)
				.getRoles();
		Role roleSelected = new Role();
		roles.add(roleSelected);

		// Combo-box role
		ComboViewer cbxRole = new ComboViewer(formRoleContainer, SWT.LEFT | SWT.READ_ONLY);
		GridData dataImportance = new GridData();
		cbxRole.getCombo().setLayoutData(dataImportance);
		cbxRole.setContentProvider(new ArrayContentProvider());
		cbxRole.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return (null != ((Role) element).getName()) ? ((Role) element).getName() : "All"; //$NON-NLS-1$
			}
		});
		cbxRole.setInput(roles);
		cbxRole.getCombo().addKeyListener(new ComboDropDownKeyListener());

		// Set the role selected
		cbxRole.setSelection(new StructuredSelection(roleSelected));

		// Listener - On change Role
		cbxRole.addSelectionChangedListener(event -> {
			// Initialize
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			getViewController().putFilter(PCMMAssessment.Filter.ROLECREATION, selection.getFirstElement());

			// Reload
			getViewController().reloadData(true);
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void roleChanged() {
		// unused: no role selection for this view
	}

	/**
	 * @return the first element selected of the pcmm viewer
	 */
	public PCMMElement getFirstElementSelected() {

		ISelection selection = treeViewerPCMM.getSelection();
		if (selection != null && !selection.isEmpty()) {
			Object elt = ((IStructuredSelection) selection).getFirstElement();
			if (elt instanceof PCMMElement) {
				return (PCMMElement) elt;
			}
		}

		return null;
	}

	/**
	 * @return the first subelement selected of the pcmm table
	 */
	private PCMMSubelement getFirstSubelementSelected() {

		ISelection selection = treeViewerPCMM.getSelection();
		if (selection != null && !selection.isEmpty()) {
			Object elt = ((IStructuredSelection) selection).getFirstElement();
			if (elt instanceof PCMMSubelement) {
				return (PCMMSubelement) elt;
			}
		}

		return null;
	}

	Object[] getExpandedElements() {
		if (treeViewerPCMM != null) {
			return treeViewerPCMM.getExpandedElements();
		}
		return new Object[0];
	}

	/**
	 * Sets the tree elements.
	 *
	 * @param data the new tree data
	 */
	void setTreeData(Object data) {
		if (treeViewerPCMM != null) {
			treeViewerPCMM.setInput(data);
		}
	}

	/**
	 * Sets the expanded elements.
	 *
	 * @param expanded the new expanded elements
	 */
	void setExpandedElements(Object[] expanded) {
		if (expanded == null || expanded.length == 0) {
			treeViewerPCMM.expandAll();
		} else {
			treeViewerPCMM.setExpandedElements(expanded);
		}
		treeViewerPCMM.refresh();
	}

	/**
	 * Display a dialog with the element details.
	 *
	 * @param element the element
	 */
	private void showAggregationDetails(PCMMElement element) {

		List<PCMMAssessment> assessmentByElement;
		try {
			assessmentByElement = getViewController().getViewManager().getAppManager()
					.getService(IPCMMAssessmentApp.class)
					.getAssessmentByElement(element, getViewController().getFilters());
			PCMMAggregationDetailsDialog dlg = new PCMMAggregationDetailsDialog(getViewController().getViewManager(),
					getShell(), assessmentByElement, element);
			dlg.openDialog();
		} catch (CredibilityException e) {
			MessageDialog.openWarning(getShell(),
					RscTools.getString(RscConst.MSG_PCMM_DIALOG_AGGREG_CREATE_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_PCMM_DIALOG_AGGREG_CREATE_DIALOG_MSG));
			logger.error("An error has occurred while creating the aggregation details dialog:\n{}", e.getMessage(), e); //$NON-NLS-1$
		}
	}

	/**
	 * Display a dialog with the subelement details
	 * 
	 * @param subElement
	 */
	private void showAggregationDetails(PCMMSubelement subElement) {
		List<PCMMAssessment> assessmentBySubelement;
		try {
			// Get assessment details
			assessmentBySubelement = getViewController().getViewManager().getAppManager()
					.getService(IPCMMAssessmentApp.class)
					.getAssessmentBySubelement(subElement, getViewController().getFilters());

			// Open dialog
			PCMMAggregationDetailsDialog dlg = new PCMMAggregationDetailsDialog(getViewController().getViewManager(),
					getShell(), assessmentBySubelement, subElement);
			dlg.openDialog();
		} catch (CredibilityException e) {
			MessageDialog.openWarning(getShell(),
					RscTools.getString(RscConst.MSG_PCMM_DIALOG_AGGREG_CREATE_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_PCMM_DIALOG_AGGREG_CREATE_DIALOG_MSG));
			logger.error("An error has occurred while creating the aggregation details dialog:\n{}", e.getMessage(), e); //$NON-NLS-1$
		}
	}

	/**
	 * @param element
	 * @return true if the element in parameter has assessments, otherwise false
	 */
	private boolean hasAssessment(Object element) {
		boolean hasAssessment = false;
		PCMMSpecification pcmmConfiguration = getViewController().getPcmmConfiguration();

		if (element instanceof PCMMSubelement && PCMMMode.DEFAULT == pcmmConfiguration.getMode()) {
			PCMMAggregation<PCMMSubelement> pcmmAggregation = getViewController().getAggregatedSubelementsMap()
					.get(element);
			hasAssessment = pcmmAggregation != null && pcmmAggregation.getLevel() != null;
		} else if (element instanceof PCMMElement && PCMMMode.SIMPLIFIED == pcmmConfiguration.getMode()) {
			PCMMAggregation<PCMMElement> pcmmAggregation = getViewController().getAggregatedElementsMap().get(element);
			hasAssessment = pcmmAggregation != null && pcmmAggregation.getLevel() != null;
		}

		return hasAssessment;
	}

	/**
	 * Get cell background color
	 * 
	 * @param element
	 * @return Color the color
	 */
	private Color getTreeCellBackgroud(Object element) {

		PCMMSpecification pcmmConfiguration = getViewController().getPcmmConfiguration();

		if (PCMMMode.DEFAULT.equals(pcmmConfiguration.getMode())) {
			// PCMM Element in gray
			if (element instanceof PCMMElement) {
				return ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT));
			} else if (element instanceof PCMMSubelement) {
				return ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));
			}
		} else if (PCMMMode.SIMPLIFIED.equals(pcmmConfiguration.getMode()) && element instanceof PCMMElement) {
			// PCMM Element in gray
			return ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));
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

		PCMMSpecification pcmmConfiguration = getViewController().getPcmmConfiguration();

		if (PCMMMode.DEFAULT.equals(pcmmConfiguration.getMode())) {
			if (element instanceof PCMMElement) {
				return ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));
			} else if (element instanceof PCMMSubelement) {
				return ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK));

			}
		} else if (PCMMMode.SIMPLIFIED.equals(pcmmConfiguration.getMode()) && element instanceof PCMMElement) {
			return ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK));
		}
		return null;
	}
}
