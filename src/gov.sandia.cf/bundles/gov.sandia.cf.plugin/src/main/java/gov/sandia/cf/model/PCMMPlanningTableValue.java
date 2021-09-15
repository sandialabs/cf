/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;

/**
 * The PCMMPlanningTableValue entity class linked to table
 * PCMM_PLANNING_TABLE_VALUE
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PCMM_PLANNING_TABLE_VALUE")
public class PCMMPlanningTableValue extends GenericValue<PCMMPlanningParam, PCMMPlanningTableValue>
		implements IGenericTableValue {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Field Filter
	 */
	@SuppressWarnings("javadoc")
	public enum Filter implements EntityFilter {
		ELEMENT("element"), //$NON-NLS-1$
		SUBELEMENT("subelement"); //$NON-NLS-1$

		private String field;

		Filter(String field) {
			this.field = field;
		}

		public String getField() {
			return this.field;
		}
	}

	/**
	 * The parameter field linked to PARAMETER_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARAMETER_ID")
	@NotNull(message = RscConst.EX_PCMMPLANNINGVALUE_PCMMPLANNINGPARAM_NULL)
	private PCMMPlanningParam parameter;

	/**
	 * The parameter field linked to PARAMETER_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITEM_ID")
	@NotNull(message = RscConst.EX_PCMMPLANNINGVALUE_PCMMPLANNINGPARAM_NULL)
	private PCMMPlanningTableItem item;

	@Override
	public PCMMPlanningParam getParameter() {
		return parameter;
	}

	@Override
	public void setParameter(PCMMPlanningParam parameter) {
		this.parameter = parameter;
	}

	@SuppressWarnings("javadoc")
	public PCMMPlanningTableItem getItem() {
		return item;
	}

	@SuppressWarnings("javadoc")
	public void setItem(PCMMPlanningTableItem item) {
		this.item = item;
	}

	@Override
	public String toString() {
		return "PCMMPlanningTableValue [parameter=" + parameter + ", item=" + item + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current entity
	 */
	public PCMMPlanningTableValue copy() {
		PCMMPlanningTableValue value = new PCMMPlanningTableValue();
		value.setDateCreation(getDateCreation());
		value.setDateUpdate(getDateUpdate());
		value.setItem(getItem());
		value.setParameter(getParameter());
		value.setUserCreation(getUserCreation());
		value.setUserUpdate(getUserUpdate());
		value.setValue(getValue());
		return value;
	}
}