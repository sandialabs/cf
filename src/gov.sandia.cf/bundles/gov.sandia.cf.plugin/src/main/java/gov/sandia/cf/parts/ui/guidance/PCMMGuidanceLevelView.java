/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.guidance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.pcmm.PCMMSpecification;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.listeners.ViewerSelectionKeepBackgroundColor;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMGuidanceLevelTableLabelProvider;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMGuidanceLevelTreeContentProvider;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMGuidanceLevelTreeSimplifiedContentProvider;
import gov.sandia.cf.parts.viewer.TreeViewerHideSelection;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The PCMM Guidance level view
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMGuidanceLevelView extends Composite {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMGuidanceLevelView.class);

	/**
	 * the table viewer
	 */
	private TreeViewerHideSelection treeLevels;

	/**
	 * a map of table phenomena columns
	 */
	private Map<String, TreeViewerColumn> treeLevelsColumns;

	/**
	 * the pcmm configuration
	 */
	private PCMMSpecification configuration;

	/**
	 * Flag is tree Column or with Line
	 */
	private boolean isTreeWithColumn;

	/**
	 * The view manager
	 */
	private IViewManager viewMgr;

	/**
	 * Construct
	 * 
	 * @param viewMgr       the view manager
	 * @param parent        the parent composite
	 * @param configuration the pcmm specification
	 * @param style         the style
	 */
	public PCMMGuidanceLevelView(IViewManager viewMgr, Composite parent, PCMMSpecification configuration, int style) {
		// Call super
		super(parent, style);

		Assert.isNotNull(viewMgr);
		this.viewMgr = viewMgr;

		// Set configuration
		this.configuration = configuration;

		// Set flag
		isTreeWithColumn = (PartsResourceConstants.PCMM_GUIDANCEVIEW_BREAK_RESIZE < getSize().x);

		// Render page
		renderPage();
	}

	/**
	 * Render page
	 */
	private void renderPage() {
		// Initialize global composite
		GridLayout gridLayout = new GridLayout();
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.setLayout(gridLayout);

		// Render title
		renderTitle();

		// if configuration is null or the PCMM Assess option is not activated
		if (configuration != null && configuration.isPcmmAssessEnabled()) {
			this.addListener(SWT.Resize, event -> {
				if (isTreeWithColumn != PartsResourceConstants.PCMM_GUIDANCEVIEW_BREAK_RESIZE < getSize().x) {
					// Set flag
					isTreeWithColumn = (PartsResourceConstants.PCMM_GUIDANCEVIEW_BREAK_RESIZE < getSize().x);

					// Dispose actual tree
					treeLevels.getTree().dispose();

					// Render tree
					renderTree();
				}
			});
		}

		// Render tree
		renderTree();

		// layout view
		this.layout();

	}

	/**
	 * Render title
	 */
	private void renderTitle() {
		// if configuration is null or the PCMM Assess option is not activated
		if (configuration == null || !configuration.isPcmmAssessEnabled()) {
			logger.warn("PCMM configuration could not be loaded for Guidance Level tree"); //$NON-NLS-1$

			// No data text
			Text txtInfo = new Text(this, SWT.READ_ONLY);
			txtInfo.setText(RscTools.getString(RscConst.MSG_GUIDANCEVIEW_PCMM_WARN_NODATA));
			GridData txtInfoGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			txtInfo.setLayoutData(txtInfoGridData);

		} else {
			// Title
			Label lblLevels = new Label(this, SWT.NONE);
			lblLevels.setForeground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY));
			lblLevels.setText(RscTools.getString(RscConst.MSG_GUIDANCEVIEW_PCMM_SUBTITLE));
			FontTools.setSubtitleFont(viewMgr.getRscMgr(), lblLevels);
		}
	}

	/**
	 * Render tree
	 */
	private void renderTree() {
		if (isTreeWithColumn) {
			renderTreeByColumn();
		} else {
			renderTreeByRow();
		}
		reload();
	}

	/**
	 * Render guidance tree
	 */
	private void renderTreeByColumn() {
		// Tree general properties initialization
		treeLevels = new TreeViewerHideSelection(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

		GridData gdTablePCMM = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		treeLevels.getTree().setLayoutData(gdTablePCMM);
		treeLevels.getTree().setHeaderVisible(true);
		treeLevels.getTree().setLinesVisible(true);

		gdTablePCMM.heightHint = treeLevels.getTree().getItemHeight();
		gdTablePCMM.widthHint = getParent().getSize().x - 2 * ((GridLayout) this.getLayout()).horizontalSpacing;

		AutoResizeViewerLayout treeLayout = new AutoResizeViewerLayout(treeLevels);
		treeLevels.getTree().setLayout(treeLayout);
		treeLevelsColumns = new HashMap<>();
		List<String> columnProperties = new ArrayList<>();

		/**
		 * Fixed columns
		 */
		// 1 - Level
		TreeViewerColumn levelColumn = new TreeViewerColumn(treeLevels, SWT.LEFT | SWT.WRAP);
		levelColumn.getColumn().setText(RscTools.getString(RscConst.MSG_GUIDANCEVIEW_PCMM_TABLE_COL_NAME));
		treeLayout.addColumnData(new ColumnPixelData(PartsResourceConstants.PCMM_GUIDANCEVIEW_ID_COLUMN_WIDTH, true));
		columnProperties.add(levelColumn.getColumn().getText());
		treeLevelsColumns.put(levelColumn.getColumn().getText(), levelColumn);

		/**
		 * Non-fixed columns
		 */
		// get the non-fixed column size
		Set<String> levelDescriptorColumns = getLevelDescriptorFromConfiguration(configuration);
		for (String column : levelDescriptorColumns) {
			TreeViewerColumn tempColumn = new TreeViewerColumn(treeLevels, SWT.LEFT);
			tempColumn.getColumn().setText(column);
			tempColumn.getColumn().setMoveable(true);
			treeLayout
					.addColumnData(new ColumnWeightData(PartsResourceConstants.PCMM_GUIDANCEVIEW_COLUMN_WEIGHT, true));
			columnProperties.add(column);
			treeLevelsColumns.put(column, tempColumn);
		}

		// Tree editors, modifiers, providers
		treeLevels.setColumnProperties(columnProperties.stream().toArray(String[]::new));

		// Label Provider
		PCMMGuidanceLevelTableLabelProvider labelProvider = new PCMMGuidanceLevelTableLabelProvider(viewMgr, treeLevels,
				isTreeWithColumn);
		labelProvider.install();
		treeLevels.setLabelProvider(labelProvider);

		// SWT.EraseItem: this event is called when repainting the tree not on removing
		// an element of the tree
		// In this case, it keeps cell colors when selection is active on cell
		treeLevels.getTree().addListener(SWT.EraseItem, new ViewerSelectionKeepBackgroundColor(treeLevels) {

			@Override
			public boolean isConditionFulfilled(Object data) {
				return data instanceof PCMMLevel || data instanceof PCMMElement || data instanceof PCMMSubelement;
			}
		});
	}

	/**
	 * Render guidance tree
	 */
	private void renderTreeByRow() {
		// Tree general properties initialization
		treeLevels = new TreeViewerHideSelection(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION,
				true, false);

		GridData gdTablePCMM = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		treeLevels.getTree().setLayoutData(gdTablePCMM);
		treeLevels.getTree().setHeaderVisible(true);
		treeLevels.getTree().setLinesVisible(true);

		gdTablePCMM.heightHint = treeLevels.getTree().getItemHeight();
		gdTablePCMM.widthHint = getParent().getSize().x - 2 * ((GridLayout) this.getLayout()).horizontalSpacing;

		AutoResizeViewerLayout treeLayout = new AutoResizeViewerLayout(treeLevels);
		treeLevels.getTree().setLayout(treeLayout);
		treeLevelsColumns = new HashMap<>();
		List<String> columnProperties = new ArrayList<>();

		// Tree - Column - Name
		TreeViewerColumn levelColumn = new TreeViewerColumn(treeLevels, SWT.LEFT | SWT.WRAP);
		levelColumn.getColumn().setText(RscTools.getString(RscConst.MSG_GUIDANCEVIEW_PCMM_TABLE_COL_NAME));
		treeLayout
				.addColumnData(new ColumnPixelData(PartsResourceConstants.PCMM_GUIDANCEVIEW_ID_COLUMN_ROW_WIDTH, true));
		columnProperties.add(levelColumn.getColumn().getText());
		treeLevelsColumns.put(levelColumn.getColumn().getText(), levelColumn);

		// Tree - Column - Info
		TreeViewerColumn tempColumn = new TreeViewerColumn(treeLevels, SWT.LEFT);
		tempColumn.getColumn().setText("");//$NON-NLS-1$
		tempColumn.getColumn().setMoveable(true);
		treeLayout.addColumnData(new ColumnWeightData(PartsResourceConstants.PCMM_GUIDANCEVIEW_COLUMN_WEIGHT, true));
		columnProperties.add("info");//$NON-NLS-1$
		treeLevelsColumns.put("info", tempColumn);//$NON-NLS-1$

		// Tree editors, modifiers, providers
		treeLevels.setColumnProperties(columnProperties.stream().toArray(String[]::new));

		// Label Provider
		PCMMGuidanceLevelTableLabelProvider labelProvider = new PCMMGuidanceLevelTableLabelProvider(viewMgr, treeLevels,
				isTreeWithColumn);
		labelProvider.install();
		treeLevels.setLabelProvider(labelProvider);

		// SWT.EraseItem: this event is called when repainting the tree not on removing
		// an element of the tree
		// In this case, it keeps cell colors when selection is active on cell
		treeLevels.getTree().addListener(SWT.EraseItem, new ViewerSelectionKeepBackgroundColor(treeLevels) {

			@Override
			public boolean isConditionFulfilled(Object data) {
				return data instanceof PCMMLevel || data instanceof PCMMElement || data instanceof PCMMSubelement;
			}
		});
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
	}

	/**
	 * Expand an element and collapse the others
	 * 
	 * @param elementAbbreviation The element abbreviation
	 */
	public void expandElementByAbbreviation(String elementAbbreviation) {

		if (treeLevels == null || !(treeLevels.getInput() instanceof List)
				|| StringUtils.isBlank(elementAbbreviation)) {
			return;
		}

		List<Object> expandedList = new ArrayList<>();

		// Match the element wanted
		for (PCMMElement elt : ((List<?>) treeLevels.getInput()).stream().filter(
				elt -> elt instanceof PCMMElement && ((PCMMElement) elt).getAbbreviation().equals(elementAbbreviation))
				.map(PCMMElement.class::cast).collect(Collectors.toList())) {

			// Expand element
			expandedList.add(elt);

			// Default mode
			if (PCMMMode.DEFAULT.equals(configuration.getMode())) {
				// For each sub-elements
				for (PCMMSubelement sub : elt.getSubElementList()) {
					// Expand sub-element
					expandedList.add(sub);
				}
			}
		}

		// Set expanded elements
		treeLevels.setExpandedElements(expandedList.toArray());
		treeLevels.refresh();
	}

	/**
	 * Reload the view
	 */
	public void reload() {

		if (null != treeLevels && null != treeLevels.getTree() && !treeLevels.getTree().isDisposed()) {
			if (null != configuration) {

				// set content provider
				if (PCMMMode.DEFAULT.equals(configuration.getMode())) {
					treeLevels.setContentProvider(new PCMMGuidanceLevelTreeContentProvider(isTreeWithColumn));
				} else if (PCMMMode.SIMPLIFIED.equals(configuration.getMode())) {
					treeLevels.setContentProvider(new PCMMGuidanceLevelTreeSimplifiedContentProvider(isTreeWithColumn));
				}

				// set PCMM elements
				treeLevels.setInput(configuration.getElements());

			}
			// refresh the viewer
			refreshViewer();
		}

	}

	/**
	 * @param configuration
	 * @return a set of descriptor columns depending of the configuration
	 */
	private Set<String> getLevelDescriptorFromConfiguration(PCMMSpecification configuration) {
		Set<String> descriptorColumns = new HashSet<>();

		if (configuration != null && configuration.getElements() != null) {

			configuration.getElements().stream().filter(Objects::nonNull).forEach(elt -> {

				if (PCMMMode.DEFAULT.equals(configuration.getMode()) && elt.getSubElementList() != null) {
					elt.getSubElementList().stream().filter(Objects::nonNull)
							.forEach(subElt -> descriptorColumns.addAll(getDescriptorColumns(subElt.getLevelList())));

				} else if (PCMMMode.SIMPLIFIED.equals(configuration.getMode()) && elt.getLevelList() != null) {
					descriptorColumns.addAll(getDescriptorColumns(elt.getLevelList()));
				}
			});
		}

		return descriptorColumns;
	}

	/**
	 * @param levelList
	 * @return the string descriptor columns in a set
	 */
	private Set<String> getDescriptorColumns(List<PCMMLevel> levelList) {
		Set<String> descriptorColumns = new HashSet<>();

		for (PCMMLevel level : levelList) {
			if (level != null) {
				level.getLevelDescriptorList().stream().filter(Objects::nonNull)
						.forEach(descriptor -> descriptorColumns.add(descriptor.getName()));
			}
		}

		return descriptorColumns;
	}

}
