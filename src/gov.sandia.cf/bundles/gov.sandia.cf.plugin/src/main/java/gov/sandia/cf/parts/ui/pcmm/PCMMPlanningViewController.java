/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMPlanningApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.GenericValueTaggable;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.IGenericTableItem;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMPlanningTableValue;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * PCMM Evidence view controller: Used to control the PCMM Evidence view
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMPlanningViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMPlanningViewController.class);

	/**
	 * The view
	 */
	private PCMMPlanningView view;

	PCMMPlanningViewController(PCMMPlanningView view) {
		Assert.isNotNull(view);
		this.view = view;
	}

	/**
	 * Change the parameter value
	 * 
	 * @param field     the pcmm planning parameter to update
	 * @param textValue the new value
	 */
	void changeParameterValue(GenericParameter<?> field, IAssessable assessable, String textValue) {

		if (field instanceof PCMMPlanningParam) {
			changePlanningParameterValue((PCMMPlanningParam) field, assessable, textValue);
		} else if (field instanceof PCMMPlanningQuestion) {
			changePlanningQuestionValue((PCMMPlanningQuestion) field, textValue);
		}
	}

	/**
	 * Change the question value
	 * 
	 * @param field     the pcmm planning parameter to update
	 * @param textValue the new value
	 */
	void changePlanningQuestionValue(PCMMPlanningQuestion field, String textValue) {

		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericValue.Filter.PARAMETER, field);
		filters.put(GenericValueTaggable.Filter.TAG, view.getViewManager().getSelectedTag());

		List<PCMMPlanningQuestionValue> values = view.getViewManager().getAppManager()
				.getService(IPCMMPlanningApplication.class).getPlanningQuestionValueBy(filters);

		if (values == null || values.isEmpty()) {

			// fill in planning value
			PCMMPlanningQuestionValue value = new PCMMPlanningQuestionValue();
			value.setParameter(field);
			value.setValue(textValue);
			value.setUserCreation(view.getViewManager().getCache().getUser());
			value.setDateCreation(DateTools.getCurrentDate());

			try {
				// add planning value
				view.getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
						.addPlanningQuestionValue(value);

				// trigger need save action
				view.getViewManager().viewChanged();

			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PCMMPLANNING_TITLE),
						RscTools.getString(RscConst.ERR_PCMMPLANNING_UPDATING) + e.getMessage());
			}
		} else {
			// for each value
			values.forEach(value -> {

				// fill in planning value
				value.setValue(textValue);
				value.setUserUpdate(view.getViewManager().getCache().getUser());
				value.setDateUpdate(DateTools.getCurrentDate());
				try {

					// update planning value
					view.getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
							.updatePlanningQuestionValue(value, view.getViewManager().getCache().getUser());

					// trigger need save action
					view.getViewManager().viewChanged();

				} catch (CredibilityException e) {
					logger.error(e.getMessage(), e);
					MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PCMMPLANNING_TITLE),
							RscTools.getString(RscConst.ERR_PCMMPLANNING_UPDATING) + e.getMessage());
				}
			});
		}
	}

	/**
	 * Change the parameter value
	 * 
	 * @param field     the pcmm planning parameter to update
	 * @param textValue the new value
	 */
	void changePlanningParameterValue(PCMMPlanningParam field, IAssessable assessable, String textValue) {

		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericValue.Filter.PARAMETER, field);
		filters.put(GenericValueTaggable.Filter.TAG, view.getViewManager().getSelectedTag());
		if (assessable instanceof PCMMElement) {
			filters.put(PCMMPlanningValue.Filter.ELEMENT, assessable);
		} else if (assessable instanceof PCMMSubelement) {
			filters.put(PCMMPlanningValue.Filter.SUBELEMENT, assessable);
		}

		List<PCMMPlanningValue> values = view.getViewManager().getAppManager()
				.getService(IPCMMPlanningApplication.class).getPlanningValueBy(filters);

		if (values == null || values.isEmpty()) {

			// fill in planning value
			PCMMPlanningValue value = new PCMMPlanningValue();
			value.setParameter(field);
			value.setValue(textValue);
			value.setUserCreation(view.getViewManager().getCache().getUser());
			value.setDateCreation(DateTools.getCurrentDate());
			if (assessable instanceof PCMMElement) {
				value.setElement((PCMMElement) assessable);
			} else if (assessable instanceof PCMMSubelement) {
				value.setSubelement((PCMMSubelement) assessable);
			}

			try {
				// add planning value
				view.getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
						.addPlanningValue(value);

				// trigger need save action
				view.getViewManager().viewChanged();

			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PCMMPLANNING_TITLE),
						RscTools.getString(RscConst.ERR_PCMMPLANNING_UPDATING) + e.getMessage());
			}
		} else {
			// for each value
			values.forEach(value -> {

				if ((textValue == null && value.getValue() != null)
						|| (textValue != null && !textValue.equals(value.getValue()))) {
					// fill in planning value
					value.setValue(textValue);
					value.setUserUpdate(view.getViewManager().getCache().getUser());
					value.setDateUpdate(DateTools.getCurrentDate());
					try {

						// update planning value
						view.getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
								.updatePlanningValue(value, view.getViewManager().getCache().getUser());

						// trigger need save action
						view.getViewManager().viewChanged();

					} catch (CredibilityException e) {
						logger.error(e.getMessage(), e);
						MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PCMMPLANNING_TITLE),
								RscTools.getString(RscConst.ERR_PCMMPLANNING_UPDATING) + e.getMessage());
					}
				}
			});
		}
	}

	/**
	 * @param field      the pcmm planning parameter
	 * @param assessable the pcmm element or subelement
	 * @return the pcmm planning value
	 */
	GenericValue<?, ?> getPlanningValue(GenericParameter<?> field, IAssessable assessable) {

		// create filters
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericValue.Filter.PARAMETER, field);
		filters.put(GenericValueTaggable.Filter.TAG, view.getViewManager().getSelectedTag());

		// retrieve values
		List<GenericValue<?, ?>> values = new ArrayList<>();
		if (field instanceof PCMMPlanningParam) {

			if (assessable instanceof PCMMElement) {
				filters.put(PCMMPlanningValue.Filter.ELEMENT, assessable);
			} else if (assessable instanceof PCMMSubelement) {
				filters.put(PCMMPlanningValue.Filter.SUBELEMENT, assessable);
			}

			values.addAll(view.getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
					.getPlanningValueBy(filters));
		} else if (field instanceof PCMMPlanningQuestion) {
			values.addAll(view.getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
					.getPlanningQuestionValueBy(filters));
		}

		return !values.isEmpty() ? values.get(0) : null;
	}

	/**
	 * @param field      the pcmm planning parameter
	 * @param assessable the pcmm element or subelement
	 * @return planning value as a string
	 */
	String getPlanningValueAsText(GenericParameter<?> field, IAssessable assessable) {
		return getPlanningValueAsText(getPlanningValue(field, assessable));
	}

	/**
	 * @param value the planning value to retrieve
	 * @return planning value as a string
	 */
	String getPlanningValueAsText(GenericValue<?, ?> value) {
		return value != null && value.getValue() != null ? value.getValue() : RscTools.empty();
	}

	/**
	 * Add a planning table item.
	 * 
	 * @param treeViewer the tree viewer to refresh
	 * @param field      the parent field
	 * @param assessable the pcmm element or subelement to associate
	 */
	void addPlanningTableItem(TreeViewer treeViewer, PCMMPlanningParam field, IAssessable assessable) {

		if (field != null) {

			// create pcmm planning table item
			PCMMPlanningTableItem item = new PCMMPlanningTableItem();
			item.setParameter(field);
			item.setDateCreation(DateTools.getCurrentDate());
			item.setUserCreation(view.getViewManager().getCache().getUser());
			if (assessable instanceof PCMMElement) {
				item.setElement((PCMMElement) assessable);
			} else if (assessable instanceof PCMMSubelement) {
				item.setSubelement((PCMMSubelement) assessable);
			}

			// add planning table item
			try {
				item = view.getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
						.addPlanningTableItem(item);

				// trigger need save action
				view.getViewManager().viewChanged();
			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PCMMPLANNING_TITLE),
						RscTools.getString(RscConst.ERR_PCMMPLANNING_UPDATING) + e.getMessage());
			}

			// add values
			if (item != null && field.getChildren() != null && !field.getChildren().isEmpty()) {
				for (GenericParameter<?> column : field.getChildren()) {
					addPlanningTableValue(item, column, RscTools.empty());
				}

				// refresh planning table item
				try {
					view.getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
							.refreshPlanningTableItem(item);
				} catch (CredibilityException e) {
					logger.error(e.getMessage(), e);
					MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PCMMPLANNING_TITLE),
							RscTools.getString(RscConst.ERR_PCMMPLANNING_UPDATING) + e.getMessage());
				}
			}

			// reload the tree
			@SuppressWarnings("unchecked")
			List<PCMMPlanningTableItem> input = (List<PCMMPlanningTableItem>) treeViewer.getInput();
			input.add(item);
			treeViewer.setInput(input);
			treeViewer.refresh();
			treeViewer.setSelection(new StructuredSelection(item));
		}
	}

	/**
	 * Add a planning table value.
	 * 
	 * @param item   the item to associate the value to
	 * @param column the column to associate the value to
	 * @param value  the value to add
	 */
	PCMMPlanningTableValue addPlanningTableValue(PCMMPlanningTableItem item, GenericParameter<?> column, String value) {

		PCMMPlanningTableValue addedValue = null;

		if (column instanceof PCMMPlanningParam) {

			// create pcmm planning table value
			PCMMPlanningTableValue columnValue = new PCMMPlanningTableValue();
			columnValue.setParameter((PCMMPlanningParam) column);
			columnValue.setItem(item);
			columnValue.setDateCreation(DateTools.getCurrentDate());
			columnValue.setUserCreation(view.getViewManager().getCache().getUser());
			columnValue.setValue(value);

			// add planning table item
			try {
				addedValue = view.getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
						.addPlanningTableValue(columnValue);
			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PCMMPLANNING_TITLE),
						RscTools.getString(RscConst.ERR_PCMMPLANNING_UPDATING) + e.getMessage());
			}
		}

		return addedValue;
	}

	/**
	 * Delete a planning table item.
	 * 
	 * @param treeViewer the tree viewer to refresh
	 * @param item       the item to delete
	 */
	void deletePlanningTableItem(TreeViewer treeViewer, Object item) {

		if (item instanceof PCMMPlanningTableItem) {

			// delete planning table value
			try {
				view.getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
						.deletePlanningTableItem((PCMMPlanningTableItem) item);

				// trigger need save action
				view.getViewManager().viewChanged();
			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PCMMPLANNING_TITLE),
						RscTools.getString(RscConst.ERR_PCMMPLANNING_UPDATING) + e.getMessage());
			}

			// reload the tree
			@SuppressWarnings("unchecked")
			List<PCMMPlanningTableItem> input = (List<PCMMPlanningTableItem>) treeViewer.getInput();
			input.remove(item);
			treeViewer.setInput(input);
			view.refreshTableItemActionButtons(treeViewer);
			treeViewer.refresh();
		}
	}

	/**
	 * Update the planning table value
	 * 
	 * 
	 * @param item  the item changed
	 * @param value the value to update
	 */
	void changePlanningTableValue(IGenericTableItem item, IGenericTableValue value) {
		if (value instanceof PCMMPlanningTableValue) {
			try {
				// update the table value
				view.getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
						.updatePlanningTableValue((PCMMPlanningTableValue) value,
								view.getViewManager().getCache().getUser());

				// trigger need save action
				view.getViewManager().viewChanged();
			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PCMMPLANNING_TITLE),
						RscTools.getString(RscConst.ERR_PCMMPLANNING_UPDATING) + e.getMessage());
			}
		}
	}

}
