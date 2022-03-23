/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import gov.sandia.cf.tools.StringTools;

/**
 * The UncertaintyParam entity class
 * 
 * @author Maxime N.
 *
 */
@Entity
@Table(name = "UNCERTAINTY_PARAM")
public class UncertaintyParam extends GenericParameter<UncertaintyParam>
		implements IEntity<UncertaintyParam, Integer>, IImportable<UncertaintyParam> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The parameter's children (for tables)
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@OrderBy("id ASC")
	private List<UncertaintyParam> children;

	/**
	 * The parameterValueList field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parameter", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<UncertaintySelectValue> parameterValueList;

	/**
	 * The parameterConstraints field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parameter", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<UncertaintyConstraint> constraintList;

	/**
	 * The parameter parent
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID")
	private UncertaintyParam parent;

	@Override
	public UncertaintyParam getParent() {
		return parent;
	}

	@Override
	public void setParent(UncertaintyParam parent) {
		this.parent = parent;
	}

	/** {@inheritDoc} */
	@Override
	public List<GenericParameter<UncertaintyParam>> getChildren() {
		return children != null ? children.stream().map(GenericParameter.class::cast).collect(Collectors.toList())
				: null;
	}

	/** {@inheritDoc} */
	@Override
	public void setChildren(List<GenericParameter<UncertaintyParam>> children) {
		if (children != null) {
			this.children = children.stream().filter(UncertaintyParam.class::isInstance)
					.map(UncertaintyParam.class::cast).collect(Collectors.toList());
		} else {
			this.children = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GenericParameterSelectValue<UncertaintyParam>> getParameterValueList() {
		return parameterValueList != null
				? parameterValueList.stream().map(GenericParameterSelectValue.class::cast).collect(Collectors.toList())
				: null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParameterValueList(List<GenericParameterSelectValue<UncertaintyParam>> parameterValueList) {
		if (parameterValueList != null) {
			this.parameterValueList = parameterValueList.stream().filter(UncertaintySelectValue.class::isInstance)
					.map(UncertaintySelectValue.class::cast).collect(Collectors.toList());
		} else {
			this.parameterValueList = null;
		}
	}

	/**
	 * @return the constraintList
	 */
	public List<GenericParameterConstraint<UncertaintyParam>> getConstraintList() {
		return constraintList != null
				? constraintList.stream().map(GenericParameterConstraint.class::cast).collect(Collectors.toList())
				: null;
	}

	/**
	 * @param constraintList the constraintList to set
	 */
	public void setConstraintList(List<GenericParameterConstraint<UncertaintyParam>> constraintList) {
		if (constraintList != null) {
			this.constraintList = constraintList.stream().filter(UncertaintyConstraint.class::isInstance)
					.map(UncertaintyConstraint.class::cast).collect(Collectors.toList());
		} else {
			this.constraintList = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameKey(UncertaintyParam newImportable) {
		return newImportable != null && StringTools.equals(getName(), newImportable.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameAs(UncertaintyParam importable) {

		if (importable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), importable.getName());
		boolean sameLevel = StringTools.equals(getLevel(), importable.getLevel());
		boolean sameDefaultValue = StringTools.equals(getDefaultValue(), importable.getDefaultValue());
		boolean sameType = StringTools.equals(getType(), importable.getType());
		boolean sameIsRequired = StringTools.equals(getRequired(), importable.getRequired());

		return sameName && sameLevel && sameDefaultValue && sameType && sameIsRequired;
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
	public UncertaintyParam copy() {
		return super.copy(UncertaintyParam.class);
	}
}