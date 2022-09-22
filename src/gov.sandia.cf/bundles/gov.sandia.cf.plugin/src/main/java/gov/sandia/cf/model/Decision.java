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
 * The Decision entity class linked to table DECISION
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "DECISION")
public class Decision implements Serializable, IGenericTableItem, IEntity<Decision, Integer>, ISortableByIdEntity {

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
		TITLE("title"), //$NON-NLS-1$
		USERCREATION("userCreation"), //$NON-NLS-1$
		DATE_CREATION("creationDate"), //$NON-NLS-1$
		PARENT("parent"), //$NON-NLS-1$
		MODEL("model"); //$NON-NLS-1$

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
	 * The id field linked to ID column
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	/**
	 * The title field linked to TITLE column
	 */
	@Column(name = "TITLE", unique = true)
	@NotBlank(message = RscConst.EX_DECISION_TITLE_BLANK)
	private String title;

	/**
	 * The userCreation field linked to USER_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_CREATION_ID")
	@NotNull(message = RscConst.EX_DECISION_USERCREATION_NULL)
	private User userCreation;

	/**
	 * The creationDate field linked to CREATION_DATE column
	 */
	@Column(name = "CREATION_DATE")
	@NotNull(message = RscConst.EX_DECISION_DATECREATION_NULL)
	private Date creationDate;

	/**
	 * The model field linked to MODEL_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	@NotNull(message = RscConst.EX_DECISION_MODEL_NULL)
	private Model model;

	/**
	 * The Decision's children
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@OrderBy("creationDate ASC")
	private List<Decision> children;

	/**
	 * The Decision's parent (master)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID")
	private Decision parent;

	/**
	 * The decision list field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "decision", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<DecisionValue> decisionList = new ArrayList<>();

	/**
	 * The generated id
	 */
	@Column(name = "ID_GENERATED")
	private String generatedId;

	/** {@inheritDoc} */
	public Integer getId() {
		return id;
	}

	/** {@inheritDoc} */
	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public String getTitle() {
		return title;
	}

	@SuppressWarnings("javadoc")
	public void setTitle(String title) {
		this.title = title;
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
	public List<Decision> getChildren() {
		return children;
	}

	@SuppressWarnings("javadoc")
	public void setChildren(List<Decision> children) {
		this.children = children;
	}

	@SuppressWarnings("javadoc")
	public Decision getParent() {
		return parent;
	}

	@SuppressWarnings("javadoc")
	public void setParent(Decision parent) {
		this.parent = parent;
	}

	@SuppressWarnings("javadoc")
	public Integer getLevel() {

		int level = 0;
		Decision parentTmp = getParent();

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
	public List<DecisionValue> getDecisionList() {
		return decisionList;
	}

	@SuppressWarnings("javadoc")
	public void setDecisionList(List<DecisionValue> decisionList) {
		this.decisionList = decisionList;
	}

	@Override
	public String getItemTitle() {
		return getTitle();
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current entity
	 */
	public Decision copy() {
		return new Decision();
	}

	/**
	 * @return the abstract
	 */
	public String getAbstract() {
		StringBuilder str = new StringBuilder();
		str.append(getGeneratedId());
		if (decisionList != null && !decisionList.isEmpty()) {
			str.append(RscTools.SPACE).append(RscTools.HYPHEN).append(RscTools.SPACE);
			str.append(String.join(RscTools.COMMA,
					decisionList.stream()
							.map(value -> value.getParameter().getName() + RscTools.COLON + value.getReadableValue())
							.collect(Collectors.toList())));
		}
		return str.toString();
	}

	@Override
	public String toString() {
		return "Decision [" + "id=" + (id != null ? id.toString() : "") + RscTools.COMMA + "title=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (title != null ? title : "") + RscTools.COMMA + "userCreation=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (userCreation != null ? userCreation.toString() : "") + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/** {@inheritDoc} */
	@Override
	public List<IGenericTableValue> getValueList() {
		return decisionList.stream().map(IGenericTableValue.class::cast).collect(Collectors.toList());
	}

	/**
	 * Get tree from this node
	 * 
	 * @return A sorted list with level data
	 */
	public List<Decision> getTree() {
		return getChildrenTree(false);
	}

	/**
	 * Get tree from this node
	 * 
	 * @param includeSelf include the current decision in the returned tree?
	 * @return A sorted list with level data and the current decision if selected
	 */
	public List<Decision> getChildrenTree(boolean includeSelf) {
		return getTree(includeSelf, new ArrayList<>());
	}

	/**
	 * Get tree from this node
	 * 
	 * @param includeSelf include the current decision in the returned tree?
	 * @param tree        the tree to append the decision children
	 * @return A sorted list with level data and the current decision if selected
	 */
	private List<Decision> getTree(boolean includeSelf, List<Decision> tree) {
		// Include parent
		if (includeSelf) {
			tree.add(this);
		}

		// Get Children tree
		if (children != null && !children.isEmpty()) {
			for (Decision child : children) {
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
	 * @param parent the parent decision
	 */
	public void getFullGeneratedId(StringBuilder path, Decision parent) {
		if (path != null) {
			path.append(RscTools.getString(RscConst.MSG_GENPARAMETER_LEVEL_SEPARATOR));
			path.append((new StringBuilder(parent.getGeneratedId())).reverse().toString());
			if (parent.getParent() != null) {
				getFullGeneratedId(path, parent.getParent());
			}
		}
	}
}