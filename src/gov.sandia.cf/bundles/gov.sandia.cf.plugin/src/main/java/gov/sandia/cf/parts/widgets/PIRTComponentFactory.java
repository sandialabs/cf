/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.PIRTTreeAdequacyColumnType;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.listeners.ViewerSelectionKeepBackgroundColor;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTAdequacyColumnLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTImportanceColumnLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTPhenTablePhenomenaContentProvider;
import gov.sandia.cf.parts.viewer.PIRTPhenomenaTreePhenomena;
import gov.sandia.cf.parts.viewer.TableViewerHideSelection;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.tools.ColorTools;

/**
 * This class contains methods to create a new PIRT table
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTComponentFactory {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PIRTComponentFactory.class);

	private static final int DEFAULT_HORIZONTAL_SPACING = 5;

	/**
	 * Do not instantiate.
	 */
	private PIRTComponentFactory() {
	}

	/**
	 * Create a pirt table that contains presentation data for the PIRT card
	 * 
	 * @param parent            the parent composite
	 * @param pirtConfiguration the pirt configuration
	 * @param viewMgr           the view manager
	 * @return the created pirt table
	 */
	public static TableViewerHideSelection createPIRTTable(Composite parent, PIRTSpecification pirtConfiguration,
			IViewManager viewMgr) {

		TableViewerHideSelection tableViewerPhenomena = null;

		// construct generated tree columns from PIRT configuration
		if (pirtConfiguration == null || pirtConfiguration.getColumns() == null) {
			logger.warn("credibility PIRTConfiguration could not be loaded for PIRT tree"); //$NON-NLS-1$
			return null;
		}

		// Create
		tableViewerPhenomena = new TableViewerHideSelection(parent, SWT.BORDER);
		Table tablePhenomena = tableViewerPhenomena.getTable();

		// Customize table
		tablePhenomena.setHeaderBackground(ColorTools.toColor(viewMgr.getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
		tablePhenomena.setHeaderForeground(
				ColorTools.toColor(viewMgr.getRscMgr(), ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));

		// Layout
		final AutoResizeViewerLayout treeViewerPhenomenaLayout = new AutoResizeViewerLayout(tableViewerPhenomena);
		tablePhenomena.setLayout(treeViewerPhenomenaLayout);

		// Grid Data
		int horizontalSpacing = parent.getLayout() instanceof GridLayout
				? ((GridLayout) parent.getLayout()).horizontalSpacing
				: DEFAULT_HORIZONTAL_SPACING;
		GridData gdViewer = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdViewer.widthHint = tablePhenomena.getSize().x - 2 * horizontalSpacing;

		// Set options
		tablePhenomena.setLayoutData(gdViewer);
		tablePhenomena.setHeaderVisible(false);
		tablePhenomena.setLinesVisible(true);

		// Initialize data
		List<String> columnProperties = new ArrayList<>();
		// Importance (Level type)
		TableViewerColumn importanceColumn = new TableViewerColumn(tableViewerPhenomena, SWT.CENTER);
		importanceColumn.getColumn().setText(PIRTPhenomenaTreePhenomena.getColumnImportanceProperty());
		importanceColumn
				.setLabelProvider(new PIRTImportanceColumnLabelProvider(pirtConfiguration, viewMgr.getRscMgr()));
		treeViewerPhenomenaLayout
				.addColumnData(new ColumnWeightData(PartsResourceConstants.PHEN_VIEW_TREEPHEN_LVL_COLUMN_COEFF, true));
		columnProperties.add(PIRTPhenomenaTreePhenomena.getColumnImportanceProperty());

		int countNBColumn = 2;
		int count = 1;

		for (PIRTAdequacyColumn column : pirtConfiguration.getColumns()) {

			// add only two columns on the home screen
			if (countNBColumn < count) {
				break;
			}
			count++;

			TableViewerColumn tempColumn = null;

			// cell editor depending of column type
			if (PIRTTreeAdequacyColumnType.LEVELS.getType().equals(column.getType())) {
				tempColumn = new TableViewerColumn(tableViewerPhenomena, SWT.CENTER);
				tempColumn.getColumn().setText(column.getName());
				treeViewerPhenomenaLayout.addColumnData(
						new ColumnWeightData(PartsResourceConstants.PHEN_VIEW_TREEPHEN_LVL_COLUMN_COEFF, true));
			}

			if (null != tempColumn) {
				tempColumn.setLabelProvider(new PIRTAdequacyColumnLabelProvider(pirtConfiguration, column, viewMgr));
			}

			columnProperties.add(column.getName());
		}

		// tree editors, modifiers, providers
		tableViewerPhenomena.setColumnProperties(columnProperties.stream().toArray(String[]::new));
		tableViewerPhenomena.setContentProvider(new PIRTPhenTablePhenomenaContentProvider());
		tablePhenomena.addListener(SWT.MeasureItem, event -> event.height = 25);

		// SWT.EraseItem: this event is called when repainting the tree not on removing
		// an element of the tree
		// In this case, it keeps cell colors when selection is active on cell
		tablePhenomena.addListener(SWT.EraseItem, new ViewerSelectionKeepBackgroundColor(tableViewerPhenomena) {

			@Override
			public boolean isConditionFulfilled(Object data) {
				return data instanceof Phenomenon;
			}

		});

		// create fake data
		tableViewerPhenomena.setInput(createPIRTTableWidgetData(columnProperties, pirtConfiguration));
		tableViewerPhenomena.refresh(true);

		return tableViewerPhenomena;
	}

	/**
	 * @param columnProperties
	 * @param pirtConfiguration
	 * @return data for the PIRT table widget on home view
	 */
	private static List<PhenomenonGroup> createPIRTTableWidgetData(List<String> columnProperties,
			PIRTSpecification pirtConfiguration) {

		// group
		PhenomenonGroup group = new PhenomenonGroup();

		// phenomenon
		List<Phenomenon> phenomenonList = new ArrayList<>();

		if (pirtConfiguration != null && pirtConfiguration.getLevels() != null) {

			List<PIRTLevelImportance> levels = new ArrayList<>(pirtConfiguration.getLevels().values());
			int levelSize = levels.size() - 1;

			for (int i = 0; i <= 2; i++) {

				List<Criterion> criterionList = new ArrayList<>();
				Phenomenon phenomenon = new Phenomenon();
				phenomenon.setImportance(levels.get(levelSize).getName());

				levelSize--;
				if (levelSize < 0) {
					levelSize = levels.size() - 1;
				}

				if (columnProperties != null && columnProperties.size() > 1) {
					for (int j = 1; j <= 2; j++) {
						Criterion criterion = new Criterion();
						criterion.setName(columnProperties.get(j));
						criterion.setType(PIRTTreeAdequacyColumnType.LEVELS.getType());
						criterion.setValue(levels.get(levelSize).getName());
						criterionList.add(criterion);

						levelSize--;
						if (levelSize < 0) {
							levelSize = levels.size() - 1;
						}
					}
				}

				phenomenon.setCriterionList(criterionList);
				phenomenonList.add(phenomenon);
			}
		}

		group.setPhenomenonList(phenomenonList);

		return Arrays.asList(group);
	}
}
