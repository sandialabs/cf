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
 * The DecisionParam entity class
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "DECISION_PARAM")
public class DecisionParam extends GenericParameter<DecisionParam>
		implements IEntity<DecisionParam, Integer>, IImportable<DecisionParam> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The parameter's children (for tables)
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@OrderBy("id ASC")
	private List<DecisionParam> children;

	/**
	 * The parameterValueList field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parameter", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<DecisionSelectValue> parameterValueList;

	/**
	 * The parameterConstraints field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parameter", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<DecisionConstraint> constraintList;

	/**
	 * The parameter parent
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID")
	private DecisionParam parent;

	@Override
	public DecisionParam getParent() {
		return parent;
	}

	@Override
	public void setParent(DecisionParam parent) {
		this.parent = parent;
	}

	/** {@inheritDoc} */
	@Override
	public List<GenericParameter<DecisionParam>> getChildren() {
		return children != null ? children.stream().map(GenericParameter.class::cast).collect(Collectors.toList())
				: null;
	}

	/** {@inheritDoc} */
	@Override
	public void setChildren(List<GenericParameter<DecisionParam>> children) {
		if (children != null) {
			this.children = children.stream().filter(DecisionParam.class::isInstance).map(DecisionParam.class::cast)
					.collect(Collectors.toList());
		} else {
			this.children = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<GenericParameterSelectValue<DecisionParam>> getParameterValueList() {
		return parameterValueList != null
				? parameterValueList.stream().map(GenericParameterSelectValue.class::cast).collect(Collectors.toList())
				: null;
	}

	/** {@inheritDoc} */
	@Override
	public void setParameterValueList(List<GenericParameterSelectValue<DecisionParam>> parameterValueList) {
		if (parameterValueList != null) {
			this.parameterValueList = parameterValueList.stream().filter(DecisionSelectValue.class::isInstance)
					.map(DecisionSelectValue.class::cast).collect(Collectors.toList());
		} else {
			this.parameterValueList = null;
		}
	}

	/**
	 * @return the constraintList
	 */
	public List<GenericParameterConstraint<DecisionParam>> getConstraintList() {
		return constraintList != null
				? constraintList.stream().map(GenericParameterConstraint.class::cast).collect(Collectors.toList())
				: null;
	}

	/**
	 * @param constraintList the constraintList to set
	 */
	public void setConstraintList(List<GenericParameterConstraint<DecisionParam>> constraintList) {
		if (constraintList != null) {
			this.constraintList = constraintList.stream().filter(DecisionConstraint.class::isInstance)
					.map(DecisionConstraint.class::cast).collect(Collectors.toList());
		} else {
			this.constraintList = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameKey(DecisionParam newImportable) {
		return newImportable != null && StringTools.equals(getName(), newImportable.getName());
	}

	/** {@inheritDoc} */
	@Override
	public boolean sameAs(DecisionParam importable) {

		if (importable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), importable.getName());
		boolean sameType = StringTools.equals(getType(), importable.getType());
		boolean sameLevel = StringTools.equals(getLevel(), importable.getLevel());
		boolean sameIsRequired = StringTools.equals(getRequired(), importable.getRequired());

		return sameName && sameType && sameIsRequired && sameLevel;
	}

	/** {@inheritDoc} */
	@Override
	public String getAbstract() {
		return getName();
	}

	/** {@inheritDoc} */
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
	public DecisionParam copy() {
		DecisionParam entity = new DecisionParam();
		entity.setLevel(getLevel());
		entity.setName(getName());
		entity.setType(getType());
		entity.setRequired(getRequired());
		return entity;
	}
}