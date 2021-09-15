/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import gov.sandia.cf.tools.StringTools;

/**
 * The PCMMPlanningParam entity class
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PCMM_PLANNING_PARAM")
public class PCMMPlanningParam extends GenericParameter<PCMMPlanningParam>
		implements IEntity<PCMMPlanningParam, Integer>, IImportable<PCMMPlanningParam> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The parameter's children (for tables)
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@OrderBy("id ASC")
	private List<PCMMPlanningParam> children;

	/**
	 * The parameterValueList field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parameter", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<PCMMPlanningSelectValue> parameterValueList;

	/**
	 * The parameterConstraints field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parameter", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<PCMMPlanningParamConstraint> constraintList;

	/** {@inheritDoc} */
	@Override
	public List<GenericParameter<PCMMPlanningParam>> getChildren() {
		return children != null ? children.stream().map(GenericParameter.class::cast).collect(Collectors.toList())
				: null;
	}

	/** {@inheritDoc} */
	@Override
	public void setChildren(List<GenericParameter<PCMMPlanningParam>> children) {
		if (children != null) {
			this.children = children.stream().filter(PCMMPlanningParam.class::isInstance)
					.map(PCMMPlanningParam.class::cast).collect(Collectors.toList());
		} else {
			this.children = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<GenericParameterSelectValue<PCMMPlanningParam>> getParameterValueList() {
		return parameterValueList != null
				? parameterValueList.stream().map(GenericParameterSelectValue.class::cast).collect(Collectors.toList())
				: null;
	}

	/** {@inheritDoc} */
	@Override
	public void setParameterValueList(List<GenericParameterSelectValue<PCMMPlanningParam>> parameterValueList) {
		if (parameterValueList != null) {
			this.parameterValueList = parameterValueList.stream().filter(PCMMPlanningSelectValue.class::isInstance)
					.map(PCMMPlanningSelectValue.class::cast).collect(Collectors.toList());
		} else {
			this.parameterValueList = null;
		}
	}

	/**
	 * @return the constraintList
	 */
	public List<GenericParameterConstraint<PCMMPlanningParam>> getConstraintList() {
		return constraintList != null
				? constraintList.stream().map(GenericParameterConstraint.class::cast).collect(Collectors.toList())
				: null;
	}

	/**
	 * @param constraintList the constraintList to set
	 */
	public void setConstraintList(List<GenericParameterConstraint<PCMMPlanningParam>> constraintList) {
		if (constraintList != null) {
			this.constraintList = constraintList.stream().filter(PCMMPlanningParamConstraint.class::isInstance)
					.map(PCMMPlanningParamConstraint.class::cast).collect(Collectors.toList());
		} else {
			this.constraintList = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return super.toString();
	}

	/** {@inheritDoc} */
	@Override
	public boolean sameAs(PCMMPlanningParam importable) {

		if (importable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), importable.getName());
		boolean sameType = StringTools.equals(getType(), importable.getType());
		boolean sameIsRequired = StringTools.equals(getRequired(), importable.getRequired());

		return sameName && sameType && sameIsRequired;
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
	 * @return a copy of the current entity
	 */
	@Override
	public PCMMPlanningParam copy() {
		return super.copy(PCMMPlanningParam.class);
	}
}