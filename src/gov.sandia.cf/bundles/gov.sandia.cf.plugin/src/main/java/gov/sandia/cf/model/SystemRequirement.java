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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The SystemRequirement entity class linked to table COM_PARAMETER_VALUE
 * 
 * @author Maxime N.
 *
 */
@Entity
@Table(name = "COM_REQUIREMENT")
public class SystemRequirement
		implements Serializable, IGenericTableItem, IEntity<SystemRequirement, Integer>, ISortableByIdEntity {

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
		STATEMENT("statement"), //$NON-NLS-1$
		USERCREATION("userCreation"), //$NON-NLS-1$
		DATE_CREATION("creationDate"), //$NON-NLS-1$
		PARENT("parent"), //$NON-NLS-1$
		MODEL("model"); //$NON-NLS-1$

		private String field;

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
	 * The id field linked to ID column
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	/**
	 * The name field linked to NAME column
	 */
	@Column(name = "STATEMENT", unique = true)
	@NotBlank(message = RscConst.EX_REQUIREMENT_STATEMENT_NULL)
	private String statement;

	/**
	 * The userCreation field linked to USER_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_CREATION_ID")
	@NotNull(message = RscConst.EX_REQUIREMENT_USERCREATION_NULL)
	private User userCreation;

	/**
	 * The creationDate field linked to CREATION_DATE column
	 */
	@Column(name = "CREATION_DATE")
	@NotNull(message = RscConst.EX_REQUIREMENT_CREATIONDATE_NULL)
	private Date creationDate;

	/**
	 * The model field linked to MODEL_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	@NotNull(message = RscConst.EX_REQUIREMENT_MODEL_NULL)
	private Model model;

	/**
	 * The SystemRequirement's children
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@OrderBy("creationDate ASC")
	private List<SystemRequirement> children;

	/**
	 * The SystemRequirement's parent (master)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID")
	private SystemRequirement parent;

	/**
	 * The requirementParameterList field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "requirement", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<SystemRequirementValue> requirementParameterList = new ArrayList<>();

	/**
	 * The generated id
	 */
	@Column(name = "ID_GENERATED")
	private String generatedId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public String getStatement() {
		return statement;
	}

	@SuppressWarnings("javadoc")
	public void setStatement(String statement) {
		this.statement = statement;
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
	public List<SystemRequirement> getChildren() {
		return children;
	}

	@SuppressWarnings("javadoc")
	public void setChildren(List<SystemRequirement> children) {
		this.children = children;
	}

	@SuppressWarnings("javadoc")
	public SystemRequirement getParent() {
		return parent;
	}

	@SuppressWarnings("javadoc")
	public void setParent(SystemRequirement parent) {
		this.parent = parent;
	}

	/**
	 * @return the level
	 */
	public Integer getLevel() {

		int level = 0;
		SystemRequirement parentTmp = getParent();

		while (parentTmp != null) {
			parentTmp = parentTmp.getParent();
			level++;
		}

		return level;
	}

	@Override
	public String getGeneratedId() {
		return generatedId;
	}

	@Override
	public void setGeneratedId(String generatedId) {
		this.generatedId = generatedId;
	}

	@SuppressWarnings("javadoc")
	public List<SystemRequirementValue> getRequirementParameterList() {
		return requirementParameterList;
	}

	@SuppressWarnings("javadoc")
	public void setRequirementParameterList(List<SystemRequirementValue> requirementParameterList) {
		this.requirementParameterList = requirementParameterList;
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current entity
	 */
	public SystemRequirement copy() {
		SystemRequirement entity = new SystemRequirement();
		entity.setCreationDate(getCreationDate());
		entity.setParent(getParent());
		entity.setStatement(getStatement());
		entity.setUserCreation(getUserCreation());
		return entity;
	}

	/**
	 * @return the abstract
	 */
	public String getAbstract() {
		return getStatement();
	}

	@Override
	public String getItemTitle() {
		return getStatement();
	}

	@Override
	public String toString() {
		return "SystemRequirement [" + "id=" + (id != null ? id.toString() : "") + RscTools.COMMA + "statement=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (statement != null ? statement : "") + RscTools.COMMA + "userCreation=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (userCreation != null ? userCreation.toString() : "") + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public List<IGenericTableValue> getValueList() {
		return requirementParameterList.stream().map(IGenericTableValue.class::cast).collect(Collectors.toList());
	}

	/**
	 * Get tree from this node
	 * 
	 * @return A sorted list with level data
	 */
	public List<SystemRequirement> getTree() {
		return getChildrenTree(false);
	}

	/**
	 * Get tree from this node
	 * 
	 * @param includeSelf include the current requirement itself
	 * @return A sorted list with level data
	 */
	public List<SystemRequirement> getChildrenTree(boolean includeSelf) {
		return getTree(includeSelf, new ArrayList<>());
	}

	/**
	 * Get tree from this node
	 * 
	 * @param includeSelf include the current requirement itself
	 * @param tree        the tree of requirement to append
	 * @return A sorted list with level data
	 */
	private List<SystemRequirement> getTree(boolean includeSelf, List<SystemRequirement> tree) {
		// Include parent
		if (includeSelf) {
			tree.add(this);
		}

		// Get Children tree
		if (children != null && !children.isEmpty()) {
			for (SystemRequirement child : children) {
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
	 * @param parent the parent system requirement
	 */
	public void getFullGeneratedId(StringBuilder path, SystemRequirement parent) {
		path.append(RscTools.getString(RscConst.MSG_GENPARAMETER_LEVEL_SEPARATOR));
		path.append((new StringBuilder(parent.getGeneratedId())).reverse().toString());
		if (parent.getParent() != null) {
			getFullGeneratedId(path, parent.getParent());
		}
	}
}
