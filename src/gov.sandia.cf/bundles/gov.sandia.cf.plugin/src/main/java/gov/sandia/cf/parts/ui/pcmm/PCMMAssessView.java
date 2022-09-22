/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMEvidenceApp;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.listeners.ViewerSelectionKeepBackgroundColor;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMAssessTreeContentProvider;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMAssessTreeSimplifiedContentProvider;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMLevelAchievedEditingSupport;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMLevelAchievedSimplifiedEditingSupport;
import gov.sandia.cf.parts.viewer.TableFactory;
import gov.sandia.cf.parts.viewer.TreeViewerHideSelection;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * The PCMM Assess view
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAssessView extends ACredibilityPCMMView<PCMMAssessViewController> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMAssessView.class);
	/**
	 * the viewer
	 */
	private TreeViewer treeViewerPCMM;

	/**
	 * The column names sorted
	 */
	private List<String> columnProperties;

	/**
	 * The editing support of the level achieved column
	 */
	private PCMMLevelAchievedEditingSupport levelAchievedEditingSupport;

	/**
	 * Table buttons
	 */
	private Map<TreeItem, TreeEditor> openEvidenceEditors;
	private Map<TreeItem, TreeEditor> assessEditors;
	private Map<TreeItem, TreeEditor> deleteEditors;

	/**
	 * The assess table composite
	 */
	private Composite compositeTable;

	/**
	 * The constructor.
	 *
	 * @param viewController the view controller
	 * @param style          the view style
	 */
	public PCMMAssessView(PCMMAssessViewController viewController, int style) {
		super(viewController, viewController.getViewManager(), style);

		// Initialize
		columnProperties = new ArrayList<>();

		deleteEditors = new HashMap<>();

		// Make sure you dispose these buttons when viewer input changes
		openEvidenceEditors = new HashMap<>();
		assessEditors = new HashMap<>();

		// create the view
		renderPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_PCMMASSESS_TITLE, RscTools.getString(RscConst.MSG_TITLE_EMPTY));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_PCMMASSESS_ITEM_TITLE);
	}

	/**
	 * Render page
	 * 
	 * @param parent
	 */
	private void renderPage() {
		logger.debug("Creating PCMM view components"); //$NON-NLS-1$

		PCMMElement elementSelected = getViewController().getElementSelected();

		// Header
		setTitle(RscTools.getString(RscConst.MSG_PCMMASSESS_TITLE,
				elementSelected != null ? elementSelected.getName() : RscTools.empty()));

		// Render tree
		renderMainTableComposite();

		// Render footer
		renderFooter();

		// Layout
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
		if (assessEditors != null) {
			assessEditors.forEach((item, editor) -> ViewTools.disposeViewerEditor(editor));
			assessEditors.clear();
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
	 * Render main table
	 */
	private void renderMainTable() {

		// get data
		PCMMSpecification pcmmConfiguration = getViewController().getPcmmConfiguration();
		PCMMElement elementSelected = getViewController().getElementSelected();

		// Tree - Create
		treeViewerPCMM = new TreeViewerHideSelection(compositeTable,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		GridData gdViewerPCMM = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		treeViewerPCMM.getTree().setLayoutData(gdViewerPCMM);
		treeViewerPCMM.getTree().setHeaderVisible(true);
		treeViewerPCMM.getTree().setLinesVisible(true);

		// Tree - Layout
		final AutoResizeViewerLayout viewerLayout = new AutoResizeViewerLayout(treeViewerPCMM);
		treeViewerPCMM.getTree().setLayout(viewerLayout);
		gdViewerPCMM.heightHint = treeViewerPCMM.getTree().getItemHeight();
		gdViewerPCMM.widthHint = getViewController().getViewManager().getSize().x
				- 2 * ((GridLayout) compositeTable.getLayout()).horizontalSpacing;

		// Tree - Columns - Id
		TreeViewerColumn idColumn = new TreeViewerColumn(treeViewerPCMM, SWT.CENTER);
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (PCMMMode.SIMPLIFIED.equals(getViewController().getPCMMMode()) && element instanceof PCMMElement) {
					// Get PCMMElement abbreviation
					return ((PCMMElement) element).getAbbreviation();
				} else if (PCMMMode.DEFAULT.equals(getViewController().getPCMMMode())
						&& element instanceof PCMMSubelement) {
					// Get PCMMSubelement Code
					return ((PCMMSubelement) element).getCode();
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
				if (PCMMMode.SIMPLIFIED.equals(getViewController().getPCMMMode())) {
					if (element instanceof PCMMElement) {
						// Get PCMMElement Name
						return ((PCMMElement) element).getName();
					}
				} else if (PCMMMode.DEFAULT.equals(getViewController().getPCMMMode())) {
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
				if (PCMMMode.SIMPLIFIED.equals(getViewController().getPCMMMode()) && element instanceof PCMMElement) {
					// Get PCMMElement
					PCMMElement elt = (PCMMElement) element;

					// Get Assessment
					PCMMAssessment assessment = getViewController().getAssessmentsByElt().get(elt);

					// Get PCMMLevel name
					String levelSelectedName = RscTools.empty();
					if (assessment != null) {
						PCMMLevel level = assessment.getLevel();
						if (level != null) {
							levelSelectedName = level.getName();
						}
					}
					return levelSelectedName;
				} else if (PCMMMode.DEFAULT.equals(getViewController().getPCMMMode())
						&& element instanceof PCMMSubelement) {
					// Get PCMMSubelement
					PCMMSubelement subelt = (PCMMSubelement) element;

					// Get PCMMAssessment
					PCMMAssessment assessment = getViewController().getAssessmentsBySubelt().get(subelt);

					// Get PCMMLevel name
					String levelSelectedName = RscTools.empty();
					if (assessment != null) {
						PCMMLevel level = assessment.getLevel();
						if (level != null) {
							levelSelectedName = level.getName();
						}
					}
					return levelSelectedName;
				}
				return RscTools.empty();
			}

			@Override
			public Color getBackground(Object element) {
				if (pcmmConfiguration != null) {
					if (PCMMMode.DEFAULT.equals(getViewController().getPCMMMode())) {
						if (element instanceof PCMMSubelement) {
							PCMMSubelement subelt = (PCMMSubelement) element;
							if (!getViewController().isFromCurrentPCMMElement(subelt.getElement())) {
								return getTreeCellBackgroud(element);
							} else {
								// Get PCMMAssessment
								PCMMAssessment assessment = getViewController().getAssessmentsBySubelt().get(subelt);
								if (assessment != null && assessment.getLevel() != null
										&& assessment.getLevel().getCode() != null
										&& pcmmConfiguration.getLevelColors() != null && pcmmConfiguration
												.getLevelColors().containsKey(assessment.getLevel().getCode())) {
									return new Color(Display.getCurrent(), ColorTools.stringRGBToColor(pcmmConfiguration
											.getLevelColors().get(assessment.getLevel().getCode()).getFixedColor()));
								}
							}
						} else {
							return getTreeCellBackgroud(element);
						}
					} else if (PCMMMode.SIMPLIFIED.equals(getViewController().getPCMMMode())) {
						// PCMM Element in gray
						if (element instanceof PCMMElement) {

							// retrieve the pcmm assessment for this element
							PCMMAssessment assessment = getViewController().getAssessmentsByElt().get(element);
							if (assessment != null && assessment.getLevel() != null
									&& assessment.getLevel().getCode() != null
									&& pcmmConfiguration.getLevelColors() != null && pcmmConfiguration.getLevelColors()
											.containsKey(assessment.getLevel().getCode())) {
								return new Color(Display.getCurrent(), ColorTools.stringRGBToColor(pcmmConfiguration
										.getLevelColors().get(assessment.getLevel().getCode()).getFixedColor()));

							} else {
								return getTreeCellBackgroud(element);
							}
						} else {
							return getTreeCellBackgroud(element);
						}
					}
				}
				return null;
			}
		});
		viewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.PCMM_VIEW_TABLEASSESS_LVLCOLUMN_WEIGHT, true));
		columnProperties.add(levelAchievedColumn.getColumn().getText());

		// Tree - Columns - Evidence
		TreeViewerColumn evidenceColumn = new TreeViewerColumn(treeViewerPCMM, SWT.CENTER);
		evidenceColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMASSESS_TABLE_COL_EVIDLINKS));
		evidenceColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// Check mode
				if (PCMMMode.DEFAULT.equals(getViewController().getViewManager().getPCMMConfiguration().getMode())
						&& element instanceof PCMMSubelement) {
					// Initialize filters
					Map<EntityFilter, Object> filters = new HashMap<>();

					// Sub-element
					filters.put(PCMMEvidence.Filter.SUBELEMENT, element);

					// Tag
					filters.put(PCMMEvidence.Filter.TAG, getViewController().getViewManager().getSelectedTag());

					// Get evidences
					List<PCMMEvidence> evidences = getViewController().getViewManager().getAppManager()
							.getService(IPCMMEvidenceApp.class).getEvidenceBy(filters);

					// Get number of PCMMEvidence
					int nbEvidence = evidences.size();
					String value = " - "; //$NON-NLS-1$
					if (nbEvidence > 0) {
						value = nbEvidence == 1 ? RscTools.getString(RscConst.MSG_PCMMASSESS_TABLE_COL_EVID_LABEL_SING)
								: RscTools.getString(RscConst.MSG_PCMMASSESS_TABLE_COL_EVID_LABEL_PLUR, nbEvidence);
					}
					return value;
				} else if (PCMMMode.SIMPLIFIED
						.equals(getViewController().getViewManager().getPCMMConfiguration().getMode())
						&& element instanceof PCMMElement) {
					// Initialize filters
					Map<EntityFilter, Object> filters = new HashMap<>();

					// Element
					filters.put(PCMMEvidence.Filter.ELEMENT, element);

					// Tag
					filters.put(PCMMEvidence.Filter.TAG, getViewController().getViewManager().getSelectedTag());

					// Get evidences
					List<PCMMEvidence> evidences = getViewController().getViewManager().getAppManager()
							.getService(IPCMMEvidenceApp.class).getEvidenceBy(filters);

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

		// Tree - Column - Action Open PCMMAssessView
		TreeViewerColumn actionOpenEvidenceColumn = new TreeViewerColumn(treeViewerPCMM, SWT.CENTER);
		actionOpenEvidenceColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_OPEN));
		actionOpenEvidenceColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Button Open for PCMM Assess
				if ((element instanceof PCMMSubelement && PCMMMode.DEFAULT == getViewController().getPCMMMode()
						&& elementSelected != null && (elementSelected.getSubElementList().contains(element)))
						|| (element instanceof PCMMElement && elementSelected != null
								&& PCMMMode.SIMPLIFIED == getViewController().getPCMMMode())
								&& elementSelected.equals(element)) {

					// Open editor
					TreeEditor editor = null;
					if (!openEvidenceEditors.containsKey(item)) {

						// button
						ButtonTheme btnOpenItem = TableFactory
								.createOpenButtonColumnAction(getViewController().getViewManager().getRscMgr(), cell);

						// Listener
						btnOpenItem.addListener(SWT.Selection, event -> {
							/**
							 * Check the PCMM mode
							 */
							if (PCMMMode.DEFAULT.equals(getViewController().getPCMMMode())
									&& element instanceof PCMMSubelement) {
								// get the selected sub-element to associate the subelement with
								PCMMEvidenceListDialog evidencesDialog = new PCMMEvidenceListDialog(
										getViewController().getViewManager(), getShell(), (PCMMSubelement) element,
										getViewController().getViewManager().getSelectedTag());
								evidencesDialog.openDialog();
							} else if (PCMMMode.SIMPLIFIED.equals(getViewController().getPCMMMode())
									&& element instanceof PCMMElement) {
								// get the selected element to associate the element with
								PCMMEvidenceListDialog evidencesDialog = new PCMMEvidenceListDialog(
										getViewController().getViewManager(), getShell(), (PCMMElement) element,
										getViewController().getViewManager().getSelectedTag());
								evidencesDialog.openDialog();
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
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEASSESS_ACTIONCOLUMN_WIDTH, false));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_OPEN));

		// Tree - Columns - Comments
		TreeViewerColumn commentColumn = new TreeViewerColumn(treeViewerPCMM, SWT.LEFT);
		commentColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMASSESS_TABLE_COL_COMMENTS));
		commentColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (PCMMMode.SIMPLIFIED.equals(getViewController().getPCMMMode()) && element instanceof PCMMElement) {
					// retrieve pcmm assessment from subelement
					PCMMElement elt = (PCMMElement) element;
					PCMMAssessment assessment = getViewController().getAssessmentsByElt().get(elt);

					return assessment != null && assessment.getComment() != null
							? StringTools.clearHtml(assessment.getComment(), true)
							: RscTools.empty();
				} else if (PCMMMode.DEFAULT.equals(getViewController().getPCMMMode())
						&& element instanceof PCMMSubelement) {
					// Get PCMMSubelement
					PCMMSubelement subelt = (PCMMSubelement) element;

					// Get PCMMAssessment
					PCMMAssessment assessment = getViewController().getAssessmentsBySubelt().get(subelt);

					// Get Comment
					return assessment != null && assessment.getComment() != null
							? StringTools.clearHtml(assessment.getComment(), true)
							: RscTools.empty();
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

		// Tree - Column - Action Assess PCMMAssessView
		if (!getViewController().getViewManager().isTagMode()) {
			TreeViewerColumn actionAssessColumn = new TreeViewerColumn(treeViewerPCMM, SWT.CENTER);
			actionAssessColumn.getColumn().setText(RscTools.getString(RscConst.MSG_PCMMASSESS_BTN_ASSESS));
			actionAssessColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public void update(ViewerCell cell) {
					// Get item
					TreeItem item = (TreeItem) cell.getItem();
					Object element = cell.getElement();

					// Button Assess for PCMM Assess
					if ((element instanceof PCMMSubelement && PCMMMode.DEFAULT == getViewController().getPCMMMode()
							&& elementSelected != null && (elementSelected.getSubElementList().contains(element)))
							|| (element instanceof PCMMElement && elementSelected != null
									&& PCMMMode.SIMPLIFIED == getViewController().getPCMMMode())
									&& elementSelected.equals(element)) {

						// Assess editor
						TreeEditor assessEditor = null;

						// Check is tagged
						if (!assessEditors.containsKey(item)) {
							// Initialize
							int iconSize = PartsResourceConstants.TABLE_ACTION_ICON_SIZE;
							String iconName = IconTheme.ICON_NAME_ADD;
							if (element instanceof PCMMElement
									&& null != getViewController().getAssessmentsByElt().get(element)) {
								iconName = IconTheme.ICON_NAME_EDIT;
							}
							if (element instanceof PCMMSubelement
									&& null != getViewController().getAssessmentsBySubelt().get(element)) {
								iconName = IconTheme.ICON_NAME_EDIT;
							}

							// Buttons
							Map<String, Object> btnAssessItemOptions = new HashMap<>();
							btnAssessItemOptions.put(ButtonTheme.OPTION_TEXT, ""); //$NON-NLS-1$
							btnAssessItemOptions.put(ButtonTheme.OPTION_OUTLINE, false);
							btnAssessItemOptions.put(ButtonTheme.OPTION_ICON, iconName);
							btnAssessItemOptions.put(ButtonTheme.OPTION_ICON_SIZE, iconSize);
							btnAssessItemOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
							ButtonTheme assessButton = new ButtonTheme(getViewController().getViewManager().getRscMgr(),
									(Composite) cell.getViewerRow().getControl(), SWT.CENTER, btnAssessItemOptions);

							// Listener
							assessButton.addListener(SWT.Selection, event -> {
								/**
								 * Check the PCMM mode
								 */
								if (PCMMMode.DEFAULT.equals(getViewController().getPCMMMode())
										&& element instanceof PCMMSubelement) {
									// get the selected sub-element to associate the element with
									getViewController().openAssessDialog((PCMMSubelement) element);
								} else if (PCMMMode.SIMPLIFIED.equals(getViewController().getPCMMMode())
										&& element instanceof PCMMElement) {
									// get the selected element to associate the element with
									getViewController().openAssessDialog((PCMMElement) element);
								}
							});

							// Draw cell
							assessEditor = new TreeEditor(item.getParent());
							assessEditor.grabHorizontal = true;
							assessEditor.grabVertical = true;
							assessEditor.setEditor(assessButton, item, cell.getColumnIndex());

							assessEditors.put(item, assessEditor);
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
					new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEASSESS_ACTIONCOLUMN_WIDTH, false));
			columnProperties.add(RscTools.getString(RscConst.MSG_PCMMASSESS_BTN_ASSESS));
		}
		// Tree - Column - Action Delete PCMMAssessView
		if (!getViewController().getViewManager().isTagMode()) {
			TreeViewerColumn actionDeleteColumn = new TreeViewerColumn(treeViewerPCMM, SWT.CENTER);
			actionDeleteColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_DELETE));
			actionDeleteColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public void update(ViewerCell cell) {
					// Get item
					TreeItem item = (TreeItem) cell.getItem();
					Object element = cell.getElement();

					// Button Delete for PCMM Assess
					if ((element instanceof PCMMSubelement && PCMMMode.DEFAULT == getViewController().getPCMMMode()
							&& elementSelected != null && (elementSelected.getSubElementList().contains(element)))
							|| (element instanceof PCMMElement && elementSelected != null
									&& PCMMMode.SIMPLIFIED == getViewController().getPCMMMode())
									&& elementSelected.equals(element)) {

						// Delete editor
						TreeEditor deleteEditor = null;
						// if the assessment already exists
						if (!deleteEditors.containsKey(item)
								&& (element instanceof PCMMElement
										&& null != getViewController().getAssessmentsByElt().get(element))
								|| (element instanceof PCMMSubelement
										&& null != getViewController().getAssessmentsBySubelt().get(element))) {

							// Button
							ButtonTheme btnDeleteItem = TableFactory.createDeleteButtonColumnAction(
									getViewController().getViewManager().getRscMgr(), cell);

							// Listener
							btnDeleteItem.addListener(SWT.Selection, event -> getViewController().delete(element));

							// Draw cell
							deleteEditor = new TreeEditor(item.getParent());
							deleteEditor.grabHorizontal = true;
							deleteEditor.grabVertical = true;
							deleteEditor.setEditor(btnDeleteItem, item, cell.getColumnIndex());
							deleteEditors.put(item, deleteEditor);
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
					new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEASSESS_ACTIONCOLUMN_WIDTH, false));
			columnProperties.add(RscTools.getString(RscConst.MSG_BTN_DELETE));
		}
		// Tree - Columns - Properties
		treeViewerPCMM.setColumnProperties(columnProperties.stream().toArray(String[]::new));

		/**
		 * Check the PCMM Mode
		 */
		if (PCMMMode.DEFAULT.equals(getViewController().getPCMMMode())) {
			treeViewerPCMM.setContentProvider(new PCMMAssessTreeContentProvider());
			levelAchievedEditingSupport = new PCMMLevelAchievedEditingSupport(getViewController(), treeViewerPCMM,
					levelAchievedColumn.getColumn().getText());
		} else if (PCMMMode.SIMPLIFIED.equals(getViewController().getPCMMMode())) {
			treeViewerPCMM.setContentProvider(new PCMMAssessTreeSimplifiedContentProvider());
			levelAchievedEditingSupport = new PCMMLevelAchievedSimplifiedEditingSupport(getViewController(),
					treeViewerPCMM, levelAchievedColumn.getColumn().getText());
		}

		// level achieved column specific cell editor
		levelAchievedColumn.setEditingSupport(levelAchievedEditingSupport);

		// cells modifier
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

		// viewer modifications on double click on viewer columns
		treeViewerPCMM.getTree().addListener(SWT.MouseDoubleClick, event -> {
			if (event.type == SWT.MouseDoubleClick) {
				if (PCMMMode.DEFAULT.equals(getViewController().getPCMMMode())) {
					// if it is the evidence column, open the evidence view for the selected
					// subelement
					PCMMSubelement firstSubelementSelected = getFirstSubelementSelected();

					// assess only for editable rows
					if (!getViewController().getViewManager().isTagMode() && elementSelected != null
							&& firstSubelementSelected != null
							&& elementSelected.equals(firstSubelementSelected.getElement())) {
						getViewController().openAssessDialog(firstSubelementSelected);
					}
				} else if (PCMMMode.SIMPLIFIED.equals(getViewController().getPCMMMode())) {
					// if it is the evidence column, open the evidence view for the selected
					// element
					PCMMElement firstElementSelected = getFirstElementSelected();

					// assess only for editable rows
					if (!getViewController().getViewManager().isTagMode() && elementSelected != null
							&& firstElementSelected != null && elementSelected.equals(firstElementSelected)) {
						getViewController().openAssessDialog(firstElementSelected);
					}
				}
			}
		});

		// viewer modifications on double click
		// enable viewer editor only on double click
		ColumnViewerEditorActivationStrategy activationSupport = new ColumnViewerEditorActivationStrategy(
				treeViewerPCMM) {
			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				// Enable editor only with mouse double click
				if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION) {
					EventObject source = event.sourceEvent;
					return !(source instanceof MouseEvent && ((MouseEvent) source).button == 3);
				}
				return false;
			}
		};
		TreeViewerEditor.create(treeViewerPCMM, null, activationSupport, ColumnViewerEditor.DEFAULT);

		// disable selection when clicking on the container
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				treeViewerPCMM.setSelection(new StructuredSelection());
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

		// Button - Guidance Level
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
		HelpTools.addContextualHelp(compositeButtonsFooter, ContextualHelpId.PCMM_ASSESS);

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
	 * Refreshes the view
	 */
	void refreshViewer() {
		treeViewerPCMM.refresh();

		PCMMElement elementSelected = getViewController().getElementSelected();

		// resets the header controls
		setTitle(RscTools.getString(RscConst.MSG_PCMMASSESS_TITLE,
				elementSelected != null ? elementSelected.getName() : RscTools.empty()));

		// layout view
		this.layout();

		// refresh the level editor
		levelAchievedEditingSupport.refreshData();
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
	 * Refresh the table buttons
	 * 
	 * @param item
	 */
	private void refreshTableButtons(TreeItem item) {
		ViewTools.refreshTreeEditor(openEvidenceEditors.get(item));
		ViewTools.refreshTreeEditor(assessEditors.get(item));
		ViewTools.refreshTreeEditor(deleteEditors.get(item));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {
		getViewController().reloadData();
	}

	/**
	 * Expand the tree with elementSelected value if it not null and in default mode
	 */
	void expandSelectedElement() {
		PCMMElement elementSelected = getViewController().getElementSelected();
		if (elementSelected != null) {
			if (PCMMMode.DEFAULT.equals(getViewController().getPCMMMode())) {
				treeViewerPCMM.setExpandedState(elementSelected, true);
				for (PCMMSubelement sub : elementSelected.getSubElementList()) {
					treeViewerPCMM.setExpandedState(sub, true);
				}
			}
			treeViewerPCMM.reveal(elementSelected);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void roleChanged() {
		refreshRole();
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
	 * @return the first subelement selected of the pcmm viewer
	 */
	public PCMMSubelement getFirstSubelementSelected() {
		ISelection selection = treeViewerPCMM.getSelection();
		if (selection != null && !selection.isEmpty()) {
			Object elt = ((IStructuredSelection) selection).getFirstElement();
			if (elt instanceof PCMMSubelement) {
				return (PCMMSubelement) elt;
			}
		}
		return null;
	}

	/**
	 * Get cell background color
	 * 
	 * @param element the element
	 * @return Color the color
	 */
	private Color getTreeCellBackgroud(Object element) {
		if (PCMMMode.DEFAULT.equals(getViewController().getPCMMMode())) {
			// PCMM Element in gray
			if (element instanceof PCMMElement) {
				return ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
						getViewController().isFromCurrentPCMMElement(((PCMMElement) element))
								? ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT)
								: ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY));
			} else if (element instanceof PCMMSubelement) {
				return ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
						getViewController().isFromCurrentPCMMElement(((PCMMSubelement) element).getElement())
								? ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)
								: ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY_LIGHT));
			}
		} else if (PCMMMode.SIMPLIFIED.equals(getViewController().getPCMMMode()) && element instanceof PCMMElement) {
			// PCMM Element in gray
			return ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
					getViewController().isFromCurrentPCMMElement(((PCMMElement) element))
							? ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)
							: ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY_LIGHT));
		}

		return null;
	}

	/**
	 * Get cell foreground color
	 * 
	 * @param element the element
	 * @return Color the color
	 */
	public Color getTreeCellForeground(Object element) {
		if (PCMMMode.DEFAULT.equals(getViewController().getPCMMMode())) {
			if (element instanceof PCMMElement) {
				return ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));
			} else if (element instanceof PCMMSubelement) {
				return ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
						getViewController().isFromCurrentPCMMElement((((PCMMSubelement) element).getElement()))
								? ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK)
								: ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY));
			}
		} else if (PCMMMode.SIMPLIFIED.equals(getViewController().getPCMMMode()) && element instanceof PCMMElement) {
			return ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
					getViewController().isFromCurrentPCMMElement(((PCMMElement) element))
							? ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK)
							: ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY));
		}
		return null;
	}

	/**
	 * @return the column properties of the viewer
	 */
	public List<String> getColumnProperties() {
		return columnProperties;
	}
}
