/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;

/**
 * The PCMMPlanningTableItem entity class linked to table
 * PCMM_PLANNING_TABLE_ITEM
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PCMM_PLANNING_TABLE_ITEM")
public class PCMMPlanningTableItem extends GenericValueTaggable<PCMMPlanningParam, PCMMPlanningTableItem>
		implements IGenericTableItem {

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
	 * The element field linked to PCMMELEMENT_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCMMELEMENT_ID")
	private PCMMElement element;

	/**
	 * The subelement field linked to PCMMSUBELEMENT_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCMMSUBELEMENT_ID")
	private PCMMSubelement subelement;

	/**
	 * The list of fields
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "item", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<PCMMPlanningTableValue> valueList = new ArrayList<>();

	@SuppressWarnings("javadoc")
	public PCMMElement getElement() {
		return element;
	}

	@SuppressWarnings("javadoc")
	public void setElement(PCMMElement element) {
		this.element = element;
	}

	@SuppressWarnings("javadoc")
	public PCMMSubelement getSubelement() {
		return subelement;
	}

	@SuppressWarnings("javadoc")
	public void setSubelement(PCMMSubelement subelement) {
		this.subelement = subelement;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMPlanningParam getParameter() {
		return parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParameter(PCMMPlanningParam parameter) {
		this.parameter = parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IGenericTableValue> getValueList() {
		List<IGenericTableValue> list = new ArrayList<>();
		list.addAll(valueList);
		return list;
	}

	@SuppressWarnings("javadoc")
	public void setValueList(List<PCMMPlanningTableValue> valueList) {
		this.valueList = valueList;
	}

	@Override
	public String getItemTitle() {
		return getReadableValue();
	}

	@Override
	public String toString() {
		return "PCMMPlanningTableItem [parameter=" + parameter + ", element=" + element + ", subelement=" + subelement //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ ", valueList=" + valueList + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current entity
	 */
	public PCMMPlanningTableItem copy() {
		PCMMPlanningTableItem value = new PCMMPlanningTableItem();
		value.setDateCreation(getDateCreation());
		value.setDateUpdate(getDateUpdate());
		value.setElement(getElement());
		value.setParameter(getParameter());
		value.setSubelement(getSubelement());
		value.setTag(getTag());
		value.setUserCreation(getUserCreation());
		value.setUserUpdate(getUserUpdate());
		value.setValue(getValue());
		return value;
	}
}