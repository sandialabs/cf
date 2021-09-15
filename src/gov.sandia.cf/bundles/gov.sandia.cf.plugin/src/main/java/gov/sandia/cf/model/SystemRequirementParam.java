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
 * The SystemRequirementParam entity class
 * 
 * @author Maxime N.
 *
 */
@Entity
@Table(name = "COM_REQUIREMENT_PARAM")
public class SystemRequirementParam extends GenericParameter<SystemRequirementParam>
		implements IEntity<SystemRequirementParam, Integer>, IImportable<SystemRequirementParam> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The parameter's children (for tables)
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@OrderBy("id ASC")
	private List<SystemRequirementParam> children;

	/**
	 * The parameterValueList field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parameter", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<SystemRequirementSelectValue> parameterValueList;

	/**
	 * The parameterConstraints field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parameter", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<SystemRequirementConstraint> constraintList;

	/** {@inheritDoc} */
	@Override
	public List<GenericParameter<SystemRequirementParam>> getChildren() {
		return children != null ? children.stream().map(GenericParameter.class::cast).collect(Collectors.toList())
				: null;
	}

	/** {@inheritDoc} */
	@Override
	public void setChildren(List<GenericParameter<SystemRequirementParam>> children) {
		if (children != null) {
			this.children = children.stream().filter(SystemRequirementParam.class::isInstance)
					.map(SystemRequirementParam.class::cast).collect(Collectors.toList());
		} else {
			this.children = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GenericParameterSelectValue<SystemRequirementParam>> getParameterValueList() {
		return parameterValueList != null
				? parameterValueList.stream().map(GenericParameterSelectValue.class::cast).collect(Collectors.toList())
				: null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParameterValueList(List<GenericParameterSelectValue<SystemRequirementParam>> parameterValueList) {
		if (parameterValueList != null) {
			this.parameterValueList = parameterValueList.stream().filter(SystemRequirementSelectValue.class::isInstance)
					.map(SystemRequirementSelectValue.class::cast).collect(Collectors.toList());
		} else {
			this.parameterValueList = null;
		}
	}

	/**
	 * @return the constraintList
	 */
	public List<GenericParameterConstraint<SystemRequirementParam>> getConstraintList() {
		return constraintList != null
				? constraintList.stream().map(GenericParameterConstraint.class::cast).collect(Collectors.toList())
				: null;
	}

	/**
	 * @param constraintList the constraintList to set
	 */
	public void setConstraintList(List<GenericParameterConstraint<SystemRequirementParam>> constraintList) {
		if (constraintList != null) {
			this.constraintList = constraintList.stream().filter(SystemRequirementConstraint.class::isInstance)
					.map(SystemRequirementConstraint.class::cast).collect(Collectors.toList());
		} else {
			this.constraintList = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameAs(SystemRequirementParam importable) {

		if (importable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), importable.getName());
		boolean sameType = StringTools.equals(getType(), importable.getType());
		boolean sameLevel = StringTools.equals(getLevel(), importable.getLevel());
		boolean sameIsRequired = StringTools.equals(getRequired(), importable.getRequired());

		return sameName && sameType && sameIsRequired && sameLevel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAbstract() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return super.toString();
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current entity
	 */
	@Override
	public SystemRequirementParam copy() {
		return super.copy(SystemRequirementParam.class);
	}
}