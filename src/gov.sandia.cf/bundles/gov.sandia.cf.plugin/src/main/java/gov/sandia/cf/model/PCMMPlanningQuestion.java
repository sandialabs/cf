/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.StringTools;

/**
 * The PCMM Planning Question
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PCMM_PLANNING_QUESTION")
public class PCMMPlanningQuestion extends GenericParameter<PCMMPlanningQuestion>
		implements IEntity<PCMMPlanningQuestion, Integer>, IImportable<PCMMPlanningQuestion> {

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

		/**
		 * Filter
		 * 
		 * @param field
		 */
		Filter(String field) {
			this.field = field;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getField() {
			return this.field;
		}
	}

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

	/** {@inheritDoc} */
	@Override
	public List<GenericParameter<PCMMPlanningQuestion>> getChildren() {
		return new ArrayList<>();
	}

	/** {@inheritDoc} */
	@Override
	public void setChildren(List<GenericParameter<PCMMPlanningQuestion>> children) {
		// not used
	}

	/** {@inheritDoc} */
	@Override
	public List<GenericParameterSelectValue<PCMMPlanningQuestion>> getParameterValueList() {
		return new ArrayList<>();
	}

	/** {@inheritDoc} */
	@Override
	public void setParameterValueList(List<GenericParameterSelectValue<PCMMPlanningQuestion>> parameterValueList) {
		// not used
	}

	/**
	 * @return the constraintList
	 */
	public List<GenericParameterConstraint<PCMMPlanningQuestion>> getConstraintList() {
		return new ArrayList<>();
	}

	/**
	 * @param constraintList the constraintList to set
	 */
	public void setConstraintList(List<GenericParameterConstraint<PCMMPlanningQuestion>> constraintList) {
		// not used
	}

	/** {@inheritDoc} */
	@Override
	public boolean sameAs(PCMMPlanningQuestion importable) {

		if (importable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), importable.getName());
		boolean sameType = StringTools.equals(getType(), importable.getType());
		boolean sameIsRequired = StringTools.equals(getRequired(), importable.getRequired());
		boolean sameElement = (getElement() == null && importable.getElement() == null)
				|| (getElement() != null && importable.getElement() != null
						&& StringTools.equals(getElement().getName(), importable.getElement().getName()));
		boolean sameSubelement = (getSubelement() == null && importable.getSubelement() == null)
				|| (getSubelement() != null && importable.getSubelement() != null
						&& StringTools.equals(getSubelement().getName(), importable.getSubelement().getName()));

		return sameName && sameType && sameIsRequired && sameElement && sameSubelement;
	}

	/** {@inheritDoc} */
	@Override
	public String getAbstract() {
		return getName();
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current PCMMPlanningQuestion
	 */
	public PCMMPlanningQuestion copy() {
		PCMMPlanningQuestion entity = super.copy(PCMMPlanningQuestion.class);
		entity.setElement(getElement());
		entity.setSubelement(getSubelement());
		return entity;
	}
}
