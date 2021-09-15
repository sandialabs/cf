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
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.parts.ui.pcmm.PCMMAssessView;
import gov.sandia.cf.parts.viewer.editors.CFComboBoxCellEditor;

/**
 * @author Didier Verstraete
 *
 */
public class PCMMLevelAchievedEditingSupport extends EditingSupport {

	/**
	 * The assessment view
	 */
	private PCMMAssessView view;
	/**
	 * The property edited by this editor
	 */
	private String propertyName;
	/**
	 * The viewer
	 */
	private final ColumnViewer viewer;

	/**
	 * A map containing all the cell editors by pcmm subelement (TableItem)
	 */
	private Map<PCMMSubelement, ComboBoxCellEditor> cellEditors;
	/**
	 * A map of map to associate the combobox editors to the right pcmm level
	 */
	private Map<PCMMSubelement, Map<PCMMLevel, Integer>> comboItems;

	/**
	 * The constructor
	 * 
	 * @param assessView   the assess view
	 * @param viewer       the column viewer
	 * @param propertyName the property name
	 */
	public PCMMLevelAchievedEditingSupport(PCMMAssessView assessView, ColumnViewer viewer, String propertyName) {
		super(viewer);
		this.view = assessView;
		this.propertyName = propertyName;
		this.viewer = viewer;
		this.cellEditors = new HashMap<>();
		this.comboItems = new HashMap<>();

		populateCellEditors();
	}

	/**
	 * Refresh the editor data contained in the comboboxes
	 */
	public void refreshData() {
		populateCellEditors();
	}

	/**
	 * Populate the cell editors comboboxes
	 */
	protected void populateCellEditors() {
		if (viewer instanceof TableViewer) {
			populateTableCellEditors((TableViewer) viewer);
		} else if (viewer instanceof TreeViewer) {
			populateTreeCellEditors((TreeViewer) viewer);
		}
	}

	/**
	 * Populate the cell editors comboboxes for a tableViewer
	 * 
	 * @param table the table viewer
	 */
	protected void populateTableCellEditors(TableViewer table) {

		if (table != null) {
			for (TableItem item : table.getTable().getItems()) {
				Object data = item.getData();
				if (data instanceof PCMMSubelement) {

					PCMMSubelement subelt = (PCMMSubelement) data;
					List<String> cbxItems = new ArrayList<>();
					Map<PCMMLevel, Integer> mapLevelItems = new HashMap<>();
					int i = 0;
					for (PCMMLevel level : subelt.getLevelList()) {
						cbxItems.add(level.getName());
						mapLevelItems.put(level, i);
						i++;
					}

					CFComboBoxCellEditor editor = new CFComboBoxCellEditor(table.getTable(),
							cbxItems.stream().toArray(String[]::new));
					cellEditors.put(subelt, editor);

					comboItems.put(subelt, mapLevelItems);
				}
			}
		}
	}

	/**
	 * Populate the cell editors comboboxes for a treeViewer
	 * 
	 * @param tree the tree viewer
	 */
	protected void populateTreeCellEditors(TreeViewer tree) {
		if (tree != null) {
			populateTreeCellEditors(tree.getTree().getItems());
		}
	}

	/**
	 * Populate the cell editors comboboxes for a set of TreeItem recursively
	 * 
	 * @param items the tree items
	 */
	protected void populateTreeCellEditors(TreeItem[] items) {

		if (items != null) {
			for (TreeItem item : items) {
				Object data = item.getData();

				if (data instanceof PCMMSubelement) {

					PCMMSubelement subelt = (PCMMSubelement) data;
					List<String> cbxItems = new ArrayList<>();
					Map<PCMMLevel, Integer> mapLevelItems = new HashMap<>();
					int i = 0;
					for (PCMMLevel level : subelt.getLevelList()) {
						cbxItems.add(level.getName());
						mapLevelItems.put(level, i);
						i++;
					}

					CFComboBoxCellEditor editor = new CFComboBoxCellEditor(item.getParent(),
							cbxItems.stream().toArray(String[]::new));
					cellEditors.put(subelt, editor);

					comboItems.put(subelt, mapLevelItems);
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
	protected boolean canEdit(Object element) {
		return viewer != null && viewer.getCellModifier() != null
				&& viewer.getCellModifier().canModify(element, propertyName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		PCMMAssessment assessment = this.view.getAssessmentsBySubelt().get(element);
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
		if (element instanceof PCMMSubelement && userInputValue instanceof Integer) {
			Integer index = (Integer) userInputValue;
			if (index < ((PCMMSubelement) element).getLevelList().size()) {
				PCMMLevel levelSelected = null;
				if (index >= 0) {
					levelSelected = ((PCMMSubelement) element).getLevelList().get((Integer) userInputValue);
				}
				if (viewer.getCellModifier().canModify(element, propertyName)) {
					viewer.getCellModifier().modify(element, propertyName, levelSelected);
				}
			}
		}
	}

	/**
	 * @return the assess view
	 */
	public PCMMAssessView getView() {
		return view;
	}

	/**
	 * @return the property name
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @return the viewer
	 */
	@Override
	public ColumnViewer getViewer() {
		return viewer;
	}

}
