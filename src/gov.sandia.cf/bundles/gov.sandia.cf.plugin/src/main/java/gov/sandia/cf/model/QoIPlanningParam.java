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
 * The QoIPlanningParam entity class
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "QOI_PLANNING_PARAM")
public class QoIPlanningParam extends GenericParameter<QoIPlanningParam>
		implements IEntity<QoIPlanningParam, Integer>, IImportable<QoIPlanningParam> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The parameter's children (for tables)
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@OrderBy("id ASC")
	private List<QoIPlanningParam> children;

	/**
	 * The parameterValueList field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parameter", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<QoIPlanningSelectValue> parameterValueList;

	/**
	 * The parameterConstraints field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parameter", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<QoIPlanningConstraint> constraintList;

	/**
	 * The parameter parent
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID")
	private QoIPlanningParam parent;

	@Override
	public QoIPlanningParam getParent() {
		return parent;
	}

	@Override
	public void setParent(QoIPlanningParam parent) {
		this.parent = parent;
	}

	/** {@inheritDoc} */
	@Override
	public List<GenericParameter<QoIPlanningParam>> getChildren() {
		return children != null ? children.stream().map(GenericParameter.class::cast).collect(Collectors.toList())
				: null;
	}

	/** {@inheritDoc} */
	@Override
	public void setChildren(List<GenericParameter<QoIPlanningParam>> children) {
		if (children != null) {
			this.children = children.stream().filter(QoIPlanningParam.class::isInstance)
					.map(QoIPlanningParam.class::cast).collect(Collectors.toList());
		} else {
			this.children = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<GenericParameterSelectValue<QoIPlanningParam>> getParameterValueList() {
		return parameterValueList != null
				? parameterValueList.stream().map(GenericParameterSelectValue.class::cast).collect(Collectors.toList())
				: null;
	}

	/** {@inheritDoc} */
	@Override
	public void setParameterValueList(List<GenericParameterSelectValue<QoIPlanningParam>> parameterValueList) {
		if (parameterValueList != null) {
			this.parameterValueList = parameterValueList.stream().filter(QoIPlanningSelectValue.class::isInstance)
					.map(QoIPlanningSelectValue.class::cast).collect(Collectors.toList());
		} else {
			this.parameterValueList = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<GenericParameterConstraint<QoIPlanningParam>> getConstraintList() {
		return constraintList != null
				? constraintList.stream().map(GenericParameterConstraint.class::cast).collect(Collectors.toList())
				: null;
	}

	/** {@inheritDoc} */
	@Override
	public void setConstraintList(List<GenericParameterConstraint<QoIPlanningParam>> constraintList) {
		if (constraintList != null) {
			this.constraintList = constraintList.stream().filter(QoIPlanningConstraint.class::isInstance)
					.map(QoIPlanningConstraint.class::cast).collect(Collectors.toList());
		} else {
			this.constraintList = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameKey(QoIPlanningParam newImportable) {
		return newImportable != null && StringTools.equals(getName(), newImportable.getName());
	}

	/** {@inheritDoc} */
	@Override
	public boolean sameAs(QoIPlanningParam importable) {

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
	public QoIPlanningParam copy() {
		return super.copy(QoIPlanningParam.class);
	}
}