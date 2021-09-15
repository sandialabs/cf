/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.guidance;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.pirt.PIRTSpecification;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.listeners.ViewerSelectionKeepBackgroundColor;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTGuidanceLevelLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTGuidelinesLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTGuidelinesTreeContentProvider;
import gov.sandia.cf.parts.viewer.TableViewerHideSelection;
import gov.sandia.cf.parts.viewer.TreeViewerHideSelection;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.parts.viewer.editors.GenericTableListContentProvider;
import gov.sandia.cf.parts.viewer.editors.GenericTreeListContentProvider;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The PCMM Guidance level view
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTGuidanceLevelView extends Composite {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PIRTGuidanceLevelView.class);

	/**
	 * the tree level viewer
	 */
	private TreeViewerHideSelection treeLevels;
	/**
	 * the table level difference color viewer
	 */
	private TableViewerHideSelection tableLevelDifferenceColors;

	private SashForm sashForm;

	/**
	 * the pirt configuration
	 */
	private PIRTSpecification configuration;

	/**
	 * The view manager
	 */
	private IViewManager viewMgr;

	/**
	 * Construct
	 * 
	 * @param viewMgr       the view manager
	 * @param parent        the parent composite
	 * @param configuration the configuration
	 * @param style         the style
	 */
	public PIRTGuidanceLevelView(IViewManager viewMgr, Composite parent, PIRTSpecification configuration, int style) {
		super(parent, style);

		Assert.isNotNull(viewMgr);
		this.viewMgr = viewMgr;
		this.configuration = configuration;
		createPage(parent);
	}

	/**
	 * Creates the view
	 * 
	 * @param parent the parent composite
	 */
	private void createPage(Composite parent) {

		// Initialize global composite
		GridLayout gridLayout = new GridLayout();
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.setLayout(gridLayout);

		// if configuration is null or the PCMM Assess option is not activated
		if (configuration == null) {

			logger.warn("PIRT configuration could not be loaded."); //$NON-NLS-1$

			// no data text
			Text txtInfo = new Text(this, SWT.READ_ONLY);
			txtInfo.setText(RscTools.getString(RscConst.MSG_GUIDANCEVIEW_PIRT_WARN_NODATA));
			GridData txtInfoGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			txtInfo.setLayoutData(txtInfoGridData);

		} else {

			// Create the SashForm with VERTICAL
			sashForm = FormFactory.createVerticalSash(this);

			// Composites into the sashform
			Composite topComposite = new Composite(sashForm, SWT.NONE);
			topComposite.setLayout(new GridLayout());
			topComposite.setBackground(this.getBackground());
			Composite bottomComposite = new Composite(sashForm, SWT.NONE);
			bottomComposite.setLayout(new GridLayout());
			bottomComposite.setBackground(this.getBackground());

			/**
			 * Tree Level
			 */
			Label lblLevels = new Label(topComposite, SWT.NONE);
			lblLevels.setText(RscTools.getString(RscConst.MSG_GUIDANCEVIEW_PIRT_LEVEL_SUBTITLE));
			FontTools.setSubtitleFont(viewMgr.getRscMgr(), lblLevels);

			// tree general properties initialization
			treeLevels = new TreeViewerHideSelection(topComposite,
					SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION, true, false);

			GridData gdTreeLevelPCMM = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			treeLevels.getTree().setLayoutData(gdTreeLevelPCMM);
			treeLevels.getTree().setHeaderVisible(true);
			treeLevels.getTree().setLinesVisible(true);

			gdTreeLevelPCMM.heightHint = PartsResourceConstants.PIRT_GUIDANCEVIEW_TABLE_MIN_HEIGHT;
			gdTreeLevelPCMM.widthHint = parent.getSize().x - 2 * ((GridLayout) this.getLayout()).horizontalSpacing;

			AutoResizeViewerLayout treeLayout = new AutoResizeViewerLayout(treeLevels);
			treeLevels.getTree().setLayout(treeLayout);
			List<String> columnProperties = new ArrayList<>();

			// 1 - Level
			TreeViewerColumn idColumn = new TreeViewerColumn(treeLevels, SWT.LEFT | SWT.WRAP);
			idColumn.getColumn().setText(RscTools.getString(RscConst.MSG_GUIDANCEVIEW_PIRT_TREELEVEL_COL_LEVELNAME));
			treeLayout
					.addColumnData(new ColumnPixelData(PartsResourceConstants.PIRT_GUIDANCEVIEW_ID_COLUMN_WIDTH, true));
			columnProperties.add(idColumn.getColumn().getText());

			// 2 - Description
			TreeViewerColumn nameColumn = new TreeViewerColumn(treeLevels, SWT.LEFT | SWT.WRAP);
			nameColumn.getColumn().setText(RscTools.getString(RscConst.MSG_GUIDANCEVIEW_PIRT_TREELEVEL_COL_LEVELDESC));
			treeLayout
					.addColumnData(new ColumnWeightData(PartsResourceConstants.PIRT_GUIDANCEVIEW_COLUMN_WEIGHT, true));
			columnProperties.add(nameColumn.getColumn().getText());

			// Tree editors, modifiers, providers
			treeLevels.setColumnProperties(columnProperties.stream().toArray(String[]::new));

			// Label Provider
			PIRTGuidelinesLabelProvider pirtGuidanceLevelTableLabelProvider = new PIRTGuidelinesLabelProvider(viewMgr,
					treeLevels);
			pirtGuidanceLevelTableLabelProvider.install();
			treeLevels.setLabelProvider(pirtGuidanceLevelTableLabelProvider);
			treeLevels.setContentProvider(new PIRTGuidelinesTreeContentProvider());

			// SWT.EraseItem: this event is called when repainting the tree not on removing
			// an element of the tree
			// In this case, it keeps cell colors when selection is active on cell
			treeLevels.getTree().addListener(SWT.EraseItem, new ViewerSelectionKeepBackgroundColor(treeLevels) {

				@Override
				public boolean isConditionFulfilled(Object data) {
					return data instanceof PIRTAdequacyColumnGuideline;
				}
			});

			/**
			 * Table Level Difference Colors
			 */
			// Level Difference Colors
			Label lblLevelDifferenceColors = new Label(bottomComposite, SWT.NONE);
			lblLevelDifferenceColors
					.setText(RscTools.getString(RscConst.MSG_GUIDANCEVIEW_PIRT_LEVEL_DIFFERENCECOLOR_SUBTITLE));
			FontTools.setSubtitleFont(viewMgr.getRscMgr(), lblLevelDifferenceColors);

			// table general properties initialization
			tableLevelDifferenceColors = new TableViewerHideSelection(bottomComposite,
					SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION, true, false);

			GridData gdTableLevelDifferenceColorsPCMM = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			tableLevelDifferenceColors.getTable().setLayoutData(gdTableLevelDifferenceColorsPCMM);
			tableLevelDifferenceColors.getTable().setHeaderVisible(true);
			tableLevelDifferenceColors.getTable().setLinesVisible(true);
			gdTableLevelDifferenceColorsPCMM.heightHint = PartsResourceConstants.PIRT_GUIDANCEVIEW_TABLE_MIN_HEIGHT;
			gdTableLevelDifferenceColorsPCMM.widthHint = parent.getSize().x
					- 2 * ((GridLayout) this.getLayout()).horizontalSpacing;
			final TableLayout tableLevelDifferenceColorsLayout = new TableLayout();
			tableLevelDifferenceColors.getTable().setLayout(tableLevelDifferenceColorsLayout);
			List<String> columnPropertiesLevelDifferenceColors = new ArrayList<>();

			// 1 - Range
			TableViewerColumn rangeLDCColumn = new TableViewerColumn(tableLevelDifferenceColors, SWT.LEFT | SWT.WRAP);
			rangeLDCColumn.getColumn()
					.setText(RscTools.getString(RscConst.MSG_GUIDANCEVIEW_PIRT_TABLELEVELDIFFCOLOR_COL_DIFF));
			tableLevelDifferenceColorsLayout.addColumnData(
					new ColumnPixelData(PartsResourceConstants.PIRT_GUIDANCEVIEW_DEFAULT_COLUMN_WIDTH, true));
			columnPropertiesLevelDifferenceColors.add(rangeLDCColumn.getColumn().getText());
			rangeLDCColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return (element instanceof PIRTLevelColorDescriptor)
							? ((PIRTLevelColorDescriptor) element).getDescription()
							: null;
				}
			});

			// 2 - Color
			TableViewerColumn colorLDCColumn = new TableViewerColumn(tableLevelDifferenceColors, SWT.LEFT | SWT.WRAP);
			colorLDCColumn.getColumn()
					.setText(RscTools.getString(RscConst.MSG_GUIDANCEVIEW_PIRT_TABLELEVELDIFFCOLOR_COL_COLOR));
			tableLevelDifferenceColorsLayout.addColumnData(
					new ColumnPixelData(PartsResourceConstants.PIRT_GUIDANCEVIEW_COLOR_COLUMN_WIDTH, true));
			columnPropertiesLevelDifferenceColors.add(colorLDCColumn.getColumn().getText());
			colorLDCColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return RscTools.empty();
				}

				@Override
				public Color getBackground(Object element) {
					return (element instanceof PIRTLevelColorDescriptor)
							? new Color(Display.getCurrent(), ((PIRTLevelColorDescriptor) element).getColor())
							: null;
				}
			});

			// Tree editors, modifiers, providers
			tableLevelDifferenceColors
					.setColumnProperties(columnPropertiesLevelDifferenceColors.stream().toArray(String[]::new));
			tableLevelDifferenceColors.setContentProvider(new GenericTableListContentProvider());

			// SWT.EraseItem: this event is called when repainting the tree not on removing
			// an element of the tree
			// In this case, it keeps cell colors when selection is active on cell
			tableLevelDifferenceColors.getTable().addListener(SWT.EraseItem,
					new ViewerSelectionKeepBackgroundColor(tableLevelDifferenceColors) {

						@Override
						public boolean isConditionFulfilled(Object data) {
							return data instanceof PIRTLevelColorDescriptor;
						}
					});

			// load data
			reload();
		}
	}

	/**
	 * Refreshes the view
	 */
	public void refreshViewer() {
		treeLevels.refresh();

		// recompute columns size
		Tree tree = treeLevels.getTree();
		for (int i = 0, n = tree.getColumnCount(); i < n; i++) {
			tree.getColumn(i).pack();
		}

		tableLevelDifferenceColors.refresh();

		// recompute columns size
		Table table = tableLevelDifferenceColors.getTable();
		for (int i = 0, n = table.getColumnCount(); i < n; i++) {
			table.getColumn(i).pack();
		}
	}

	/**
	 * Reload the view
	 */
	public void reload() {

		if (null != configuration) {

			// set the tree levels input
			if (null != treeLevels && null != treeLevels.getTree() && !treeLevels.getTree().isDisposed()) {
				loadTreeLevelInput();
			}

			// set the table level colors input
			if (null != tableLevelDifferenceColors && null != tableLevelDifferenceColors.getTable()
					&& !tableLevelDifferenceColors.getTable().isDisposed()) {
				loadTableLevelColorsInput();
			}
		}

		// refresh the viewer
		refreshViewer();
	}

	/**
	 * Load the tree level input.
	 */
	private void loadTreeLevelInput() {

		List<PIRTAdequacyColumnGuideline> pirtAdequacyGuidelines = configuration.getPirtAdequacyGuidelines();
		if (pirtAdequacyGuidelines != null && !pirtAdequacyGuidelines.isEmpty()) {
			treeLevels.setInput(pirtAdequacyGuidelines);
			PIRTGuidelinesLabelProvider labelProvider = new PIRTGuidelinesLabelProvider(viewMgr, treeLevels);
			labelProvider.install();
			treeLevels.setLabelProvider(labelProvider);
			treeLevels.setContentProvider(new PIRTGuidelinesTreeContentProvider());

			// set the sash form weights
			sashForm.setWeights(new int[] { 3, 2 });
		} else {
			treeLevels.setInput(configuration.getLevelsListSortedByLevelDescending());
			treeLevels.setLabelProvider(new PIRTGuidanceLevelLabelProvider());
			treeLevels.setContentProvider(new GenericTreeListContentProvider());

			// set the sash form weights
			sashForm.setWeights(new int[] { 1, 1 });
		}
	}

	/**
	 * Load the table level colors.
	 */
	private void loadTableLevelColorsInput() {

		List<PIRTLevelColorDescriptor> colors = new ArrayList<>();

		// add level difference colors
		if (configuration.getColors() != null) {
			for (PIRTLevelDifferenceColor color : configuration.getColors()) {
				if (color != null) {
					StringBuilder description = new StringBuilder();
					description.append(color.getDescription());
					if (color.getExplanation() != null && !color.getExplanation().isEmpty()) {
						description.append(" (").append(color.getExplanation()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					colors.add(new PIRTLevelColorDescriptor(description.toString(),
							ColorTools.stringRGBToColor(color.getColor())));
				}
			}
		}

		// add fixed colors
		if (configuration.getLevelsListSortedByLevelDescending() != null) {
			for (PIRTLevelImportance color : configuration.getLevelsListSortedByLevelDescending()) {
				if (color != null && color.getFixedColor() != null) {

					StringBuilder description = new StringBuilder();
					description.append(MessageFormat.format("{0} ({1})", color.getName(), color.getLabel())); //$NON-NLS-1$
					if (color.getFixedColorDescription() != null && !color.getFixedColorDescription().isEmpty()) {
						description.append(" (").append(color.getFixedColorDescription()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
					}

					colors.add(new PIRTLevelColorDescriptor(description.toString(),
							ColorTools.stringRGBToColor(color.getFixedColor())));
				}
			}
		}

		tableLevelDifferenceColors.setInput(colors);
	}
}
