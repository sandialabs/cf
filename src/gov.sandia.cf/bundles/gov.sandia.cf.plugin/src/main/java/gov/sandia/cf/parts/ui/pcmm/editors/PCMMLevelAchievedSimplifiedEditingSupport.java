/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.parts.ui.pcmm.PCMMAssessViewController;
import gov.sandia.cf.parts.viewer.editors.CFComboBoxCellEditor;

/**
 * @author Didier Verstraete
 *
 */
public class PCMMLevelAchievedSimplifiedEditingSupport extends PCMMLevelAchievedEditingSupport {

	/**
	 * A map containing all the cell editors by pcmm element (TableItem)
	 */
	private Map<PCMMElement, ComboBoxCellEditor> cellEditors;
	/**
	 * A map of map to associate the combobox editors to the right pcmm level
	 */
	private Map<PCMMElement, Map<PCMMLevel, Integer>> comboItems;

	/**
	 * The constructor
	 * 
	 * @param assessViewController the assess view
	 * @param viewer               the column viewer
	 * @param propertyName         the property name
	 */
	public PCMMLevelAchievedSimplifiedEditingSupport(PCMMAssessViewController assessViewController, ColumnViewer viewer,
			String propertyName) {
		super(assessViewController, viewer, propertyName);
		this.cellEditors = new HashMap<>();
		this.comboItems = new HashMap<>();

		populateCellEditors();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void populateTableCellEditors(TableViewer table) {

		if (table != null) {
			for (TableItem item : table.getTable().getItems()) {
				Object data = item.getData();
				if (data instanceof PCMMElement) {

					PCMMElement elt = (PCMMElement) data;
					List<String> cbxItems = new ArrayList<>();
					Map<PCMMLevel, Integer> mapLevelItems = new HashMap<>();
					int i = 0;
					for (PCMMLevel level : elt.getLevelList()) {
						cbxItems.add(level.getName());
						mapLevelItems.put(level, i);
						i++;
					}

					CFComboBoxCellEditor editor = new CFComboBoxCellEditor(table.getTable(),
							cbxItems.stream().toArray(String[]::new));
					cellEditors.put(elt, editor);

					comboItems.put(elt, mapLevelItems);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void populateTreeCellEditors(TreeItem[] items) {

		if (items != null) {
			for (TreeItem item : items) {
				Object data = item.getData();

				if (data instanceof PCMMElement) {

					PCMMElement elt = (PCMMElement) data;
					List<String> cbxItems = new ArrayList<>();
					Map<PCMMLevel, Integer> mapLevelItems = new HashMap<>();
					int i = 0;
					for (PCMMLevel level : elt.getLevelList()) {
						cbxItems.add(level.getName());
						mapLevelItems.put(level, i);
						i++;
					}

					CFComboBoxCellEditor editor = new CFComboBoxCellEditor(item.getParent(),
							cbxItems.stream().toArray(String[]::new));
					cellEditors.put(elt, editor);
					comboItems.put(elt, mapLevelItems);
				}

				// recursive call to retrieve subelement items
				populateTreeCellEditors(item.getItems());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return cellEditors.get(element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		PCMMAssessment assessment = getViewController().getAssessmentsByElt().get(element);
		Integer value = -1;
		if (assessment != null && comboItems.get(element) != null) {
			value = comboItems.get(element).get(assessment.getLevel());
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object userInputValue) {
		if (element instanceof PCMMElement && userInputValue instanceof Integer) {
			Integer index = (Integer) userInputValue;
			if (index < ((PCMMElement) element).getLevelList().size()) {
				PCMMLevel levelSelected = null;
				if (index >= 0) {
					levelSelected = ((PCMMElement) element).getLevelList().get((Integer) userInputValue);
				}
				if (getViewer().getCellModifier().canModify(element, getPropertyName())) {
					getViewer().getCellModifier().modify(element, getPropertyName(), levelSelected);
				}
			}
		}
	}
}
