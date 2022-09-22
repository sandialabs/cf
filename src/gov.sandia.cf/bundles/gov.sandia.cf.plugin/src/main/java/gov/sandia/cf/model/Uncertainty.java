/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * The Uncertainty entity class linked to table UNCERTAINTY
 * 
 * @author Maxime N.
 *
 */
@Entity
@Table(name = "UNCERTAINTY")
public class Uncertainty implements Serializable, IGenericTableItem, IEntity<Uncertainty, Integer>, ISelectValue,
		IImportable<Uncertainty>, ISortableByIdEntity {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Field Filter
	 */
	@SuppressWarnings("javadoc")
	public enum Filter implements EntityFilter {
		ID("id"), //$NON-NLS-1$
		NAME("name"), //$NON-NLS-1$
		USERCREATION("userCreation"), //$NON-NLS-1$
		DATE_CREATION("creationDate"), //$NON-NLS-1$
		PARENT("parent"), //$NON-NLS-1$
		MODEL("model"); //$NON-NLS-1$

		private String field;

		Filter(String field) {
			this.field = field;
		}

		public String getField() {
			return this.field;
		}
	}

	/**
	 * The id field linked to ID column
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	/**
	 * The name field linked to NAME column
	 */
	@Column(name = "NAME")
	private String name;

	/** The parent. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID")
	private Uncertainty parent;

	/** The children. */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@OrderBy("creationDate ASC")
	private List<Uncertainty> children;

	/**
	 * The model field linked to MODEL_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	@NotNull(message = RscConst.EX_UNCERTAINTY_MODEL_NULL)
	private Model model;

	/**
	 * The userCreation field linked to USER_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_CREATION_ID")
	@NotNull(message = RscConst.EX_UNCERTAINTY_USERCREATION_NULL)
	private User userCreation;
	/**
	 * The creationDate field linked to CREATION_DATE column
	 */
	@Column(name = "CREATION_DATE")
	@NotNull(message = RscConst.EX_UNCERTAINTY_DATECREATION_NULL)
	private Date creationDate;

	/**
	 * The generated id
	 */
	@Column(name = "ID_GENERATED")
	private String generatedId;

	/**
	 * The uncertaintyValueList field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "uncertainty", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<UncertaintyValue> values = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public String getName() {
		return name;
	}

	@SuppressWarnings("javadoc")
	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("javadoc")
	public User getUserCreation() {
		return userCreation;
	}

	@SuppressWarnings("javadoc")
	public void setUserCreation(User userCreation) {
		this.userCreation = userCreation;
	}

	@SuppressWarnings("javadoc")
	public Date getCreationDate() {
		return Optional.ofNullable(creationDate).map(Date::getTime).map(Date::new).orElse(null);
	}

	@SuppressWarnings("javadoc")
	public void setCreationDate(Date creationDate) {
		this.creationDate = Optional.ofNullable(creationDate).map(Date::getTime).map(Date::new).orElse(creationDate);
	}

	@SuppressWarnings("javadoc")
	public Model getModel() {
		return model;
	}

	@SuppressWarnings("javadoc")
	public void setModel(Model model) {
		this.model = model;
	}

	@SuppressWarnings("javadoc")
	public List<Uncertainty> getChildren() {
		return children;
	}

	@SuppressWarnings("javadoc")
	public void setChildren(List<Uncertainty> children) {
		this.children = children;
	}

	@SuppressWarnings("javadoc")
	public Uncertainty getParent() {
		return parent;
	}

	@SuppressWarnings("javadoc")
	public void setParent(Uncertainty parent) {
		this.parent = parent;
	}

	@Override
	public String getGeneratedId() {
		return generatedId;
	}

	@Override
	public void setGeneratedId(String generatedId) {
		this.generatedId = generatedId;
	}

	@Override
	public List<IGenericTableValue> getValueList() {
		return values.stream().map(IGenericTableValue.class::cast).collect(Collectors.toList());
	}

	@SuppressWarnings("javadoc")
	public List<UncertaintyValue> getValues() {
		return values;
	}

	@SuppressWarnings("javadoc")
	public void setValues(List<UncertaintyValue> values) {
		this.values = values;
	}

	@Override
	public String getItemTitle() {
		return (getParent() != null ? getParent().getName() : "Uncertainty") //$NON-NLS-1$
				+ (getId() != null ? RscTools.DOT + getId() : RscTools.empty());
	}

	@SuppressWarnings("javadoc")
	public Integer getLevel() {

		int level = 0;
		Uncertainty parentTmp = getParent();

		while (parentTmp != null) {
			parentTmp = parentTmp.getParent();
			level++;
		}

		return level;
	}

	@Override
	public String toString() {
		return "Uncertainty [" + "id=" + (id != null ? id.toString() : "") + RscTools.COMMA + "name=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (name != null ? name : "") + RscTools.COMMA + "userCreation=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (userCreation != null ? userCreation.toString() : "") + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public String getSelectName() {
		return getName();
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current entity
	 */
	public Uncertainty copy() {
		Uncertainty entity = new Uncertainty();
		entity.setModel(getModel());
		entity.setUserCreation(getUserCreation());
		entity.setName(getName());
		entity.setParent(getParent());
		entity.setCreationDate(getCreationDate());
		entity.setValues(getValues());
		return entity;
	}

	/**
	 * Get tree from this node
	 * 
	 * @return A sorted list with level data
	 */
	public List<Uncertainty> getTree() {
		return getChildrenTree(false);
	}

	/**
	 * Get tree from this node
	 * 
	 * @param includeSelf include the current uncertainty in the returned tree?
	 * @return A sorted list with level data and the current uncertainty if selected
	 */
	public List<Uncertainty> getChildrenTree(boolean includeSelf) {
		return getTree(includeSelf, new ArrayList<>());
	}

	/**
	 * Get tree from this node
	 * 
	 * @param includeSelf include the current uncertainty in the returned tree?
	 * @param tree        the tree to append the uncertainty children
	 * @return A sorted list with level data and the current uncertainty if selected
	 */
	private List<Uncertainty> getTree(boolean includeSelf, List<Uncertainty> tree) {
		// Include parent
		if (includeSelf) {
			tree.add(this);
		}

		// Get Children tree
		if (children != null && !children.isEmpty()) {
			for (Uncertainty child : children) {
				child.getTree(true, tree);
			}
		}

		// Return the tree
		return tree;
	}

	/**
	 * Get full generated id path string
	 * 
	 * @return The full generated id path to string
	 */
	public String getFullGeneratedId() {
		// Initialize
		StringBuilder path = new StringBuilder();
		path.append((new StringBuilder(this.getGeneratedId())).reverse().toString());

		// If parent
		if (getParent() != null) {
			getFullGeneratedId(path, getParent());
		}

		return path.reverse().toString();
	}

	/**
	 * Recursively add parent id into the generated full path
	 * 
	 * @param path   the path to append
	 * @param parent the parent uncertainty
	 */
	public void getFullGeneratedId(StringBuilder path, Uncertainty parent) {
		if (path != null) {
			path.append(RscTools.getString(RscConst.MSG_GENPARAMETER_LEVEL_SEPARATOR));
			path.append((new StringBuilder(parent.getGeneratedId())).reverse().toString());
			if (parent.getParent() != null) {
				getFullGeneratedId(path, parent.getParent());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameKey(Uncertainty newImportable) {
		return newImportable != null && StringTools.equals(getName(), newImportable.getName());
	}

	@Override
	public boolean sameAs(Uncertainty newImportable) {
		if (newImportable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), newImportable.getName());
		boolean sameLevel = getLevel().equals(newImportable.getLevel());

		return sameName && sameLevel;
	}

	@Override
	public String getAbstract() {
		return getName();
	}
}