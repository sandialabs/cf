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
 * The QuantityOfInterest entity class linked to table QOI
 * 
 * @author Didier Verstraete
 */
@Entity
@Table(name = "QOI")
public class QuantityOfInterest
		implements Serializable, IEntity<QuantityOfInterest, Integer>, IGenericTableItem, ISortableByIdEntity {

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
		SYMBOL("symbol"), //$NON-NLS-1$
		DESCRIPTION("description"), //$NON-NLS-1$
		DATE_CREATION("creationDate"), //$NON-NLS-1$
		USER_CREATION("userCreation"), //$NON-NLS-1$
		DATE_UPDATE("updateDate"), //$NON-NLS-1$
		USER_UPDATE("userUpdate"), //$NON-NLS-1$
		TAG("tag"), //$NON-NLS-1$
		DATE_TAG("tagDate"), //$NON-NLS-1$
		TAG_DESCRIPTION("tagDescription"), //$NON-NLS-1$
		TAG_USER_CREATION("tagUserCreation"), //$NON-NLS-1$
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
	 * The symbol field linked to NAME column
	 */
	@Column(name = "NAME")
	@NotBlank(message = RscConst.EX_QOI_SYMBOL_BLANK)
	private String symbol;

	/**
	 * The description field linked to DESCRIPTION column
	 */
	@Column(name = "DESCRIPTION", columnDefinition = "LONGVARCHAR")
	private String description;

	/**
	 * The creationDate field linked to CREATION_DATE column
	 */
	@Column(name = "CREATION_DATE")
	@NotNull(message = RscConst.EX_QOI_CREATIONDATE_NULL)
	private Date creationDate;

	/**
	 * The userCreation field linked to USER_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_CREATION_ID")
	private User userCreation;

	/**
	 * The creationDate field linked to UPDATE_DATE column
	 */
	@Column(name = "UPDATE_DATE")
	private Date updateDate;

	/**
	 * The userCreation field linked to USER_UPDATE_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_UPDATE_ID")
	private User userUpdate;

	/**
	 * The tag field linked to TAG column
	 */
	@Column(name = "TAG")
	private String tag;

	/**
	 * The tagDate field linked to TAG_DATE column
	 */
	@Column(name = "TAG_DATE")
	private Date tagDate;

	/**
	 * The tag field linked to TAG_DESCRIPTION column
	 */
	@Column(name = "TAG_DESCRIPTION")
	private String tagDescription;

	/**
	 * The tagUserCreation field linked to TAG_USER_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TAG_USER_CREATION_ID")
	private User tagUserCreation;

	/**
	 * The model field linked to MODEL_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	@NotNull(message = RscConst.EX_QOI_MODEL_NULL)
	private Model model;

	/**
	 * The qoiHeaderList field linked to qoiHeaderList column
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "qoi", cascade = { CascadeType.REMOVE, CascadeType.REFRESH })
	private List<QoIHeader> qoiHeaderList;

	/**
	 * The qoiPlanningList field linked to qoiPlanningList column
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "qoi", cascade = { CascadeType.REMOVE, CascadeType.REFRESH })
	private List<QoIPlanningValue> qoiPlanningList;

	/**
	 * The phenomenonGroupList field linked to phenomenonGroupList column
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "qoi", cascade = { CascadeType.REMOVE, CascadeType.REFRESH })
	private List<PhenomenonGroup> phenomenonGroupList;

	/**
	 * The QuantityOfInterest's children (versions)
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = { CascadeType.REMOVE, CascadeType.REFRESH })
	@OrderBy("creationDate DESC")
	private List<QuantityOfInterest> children;

	/**
	 * The QuantityOfInterest's parent (master)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID")
	private QuantityOfInterest parent;

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
	public String getSymbol() {
		return symbol;
	}

	@SuppressWarnings("javadoc")
	public void setSymbol(String name) {
		this.symbol = name;
	}

	@SuppressWarnings("javadoc")
	public String getDescription() {
		return description;
	}

	@SuppressWarnings("javadoc")
	public void setDescription(String description) {
		this.description = description;
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
	public User getUserCreation() {
		return userCreation;
	}

	@SuppressWarnings("javadoc")
	public void setUserCreation(User userCreation) {
		this.userCreation = userCreation;
	}

	@SuppressWarnings("javadoc")
	public Date getUpdateDate() {
		return Optional.ofNullable(updateDate).map(Date::getTime).map(Date::new).orElse(null);
	}

	@SuppressWarnings("javadoc")
	public void setUpdateDate(Date updateDate) {
		this.updateDate = Optional.ofNullable(updateDate).map(Date::getTime).map(Date::new).orElse(updateDate);
	}

	@SuppressWarnings("javadoc")
	public User getUserUpdate() {
		return userUpdate;
	}

	@SuppressWarnings("javadoc")
	public void setUserUpdate(User userUpdate) {
		this.userUpdate = userUpdate;
	}

	@SuppressWarnings("javadoc")
	public String getTag() {
		return tag;
	}

	@SuppressWarnings("javadoc")
	public void setTag(String tag) {
		this.tag = tag;
	}

	@SuppressWarnings("javadoc")
	public Date getTagDate() {
		return Optional.ofNullable(tagDate).map(Date::getTime).map(Date::new).orElse(null);
	}

	@SuppressWarnings("javadoc")
	public void setTagDate(Date tagDate) {
		this.tagDate = Optional.ofNullable(tagDate).map(Date::getTime).map(Date::new).orElse(null);
	}

	@SuppressWarnings("javadoc")
	public String getTagDescription() {
		return tagDescription;
	}

	@SuppressWarnings("javadoc")
	public void setTagDescription(String tagDescription) {
		this.tagDescription = tagDescription;
	}

	@SuppressWarnings("javadoc")
	public User getTagUserCreation() {
		return tagUserCreation;
	}

	@SuppressWarnings("javadoc")
	public void setTagUserCreation(User tagUserCreation) {
		this.tagUserCreation = tagUserCreation;
	}

	@SuppressWarnings("javadoc")
	public Model getModel() {
		return model;
	}

	@SuppressWarnings("javadoc")
	public void setModel(Model model) {
		this.model = model;
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
	public List<QoIHeader> getQoiHeaderList() {
		return qoiHeaderList;
	}

	@SuppressWarnings("javadoc")
	public void setQoiHeaderList(List<QoIHeader> qoiHeaderList) {
		this.qoiHeaderList = qoiHeaderList;
	}

	@SuppressWarnings("javadoc")
	public List<QoIPlanningValue> getQoiPlanningList() {
		return qoiPlanningList;
	}

	@SuppressWarnings("javadoc")
	public void setQoiPlanningList(List<QoIPlanningValue> qoiPlanningList) {
		this.qoiPlanningList = qoiPlanningList;
	}

	@Override
	public List<IGenericTableValue> getValueList() {
		return qoiPlanningList != null
				? qoiPlanningList.stream().map(IGenericTableValue.class::cast).collect(Collectors.toList())
				: new ArrayList<>();
	}

	@SuppressWarnings("javadoc")
	public List<PhenomenonGroup> getPhenomenonGroupList() {
		return phenomenonGroupList;
	}

	@SuppressWarnings("javadoc")
	public void setPhenomenonGroupList(List<PhenomenonGroup> phenomenonGroupList) {
		this.phenomenonGroupList = phenomenonGroupList;
	}

	@SuppressWarnings("javadoc")
	public List<QuantityOfInterest> getChildren() {
		return children;
	}

	@SuppressWarnings("javadoc")
	public void setChildren(List<QuantityOfInterest> children) {
		this.children = children;
	}

	@SuppressWarnings("javadoc")
	public QuantityOfInterest getParent() {
		return parent;
	}

	@SuppressWarnings("javadoc")
	public void setParent(QuantityOfInterest parent) {
		this.parent = parent;
	}

	@Override
	public String getItemTitle() {
		return getSymbol();
	}

	@SuppressWarnings("javadoc")
	public Integer getLevel() {

		int level = 0;
		QuantityOfInterest parentTmp = getParent();

		while (parentTmp != null) {
			parentTmp = parentTmp.getParent();
			level++;
		}

		return level;
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current QuantityOfInterest
	 */
	@Override
	public QuantityOfInterest copy() {
		QuantityOfInterest entity = new QuantityOfInterest();
		entity.setCreationDate(getCreationDate());
		entity.setUserCreation(getUserCreation());
		entity.setUpdateDate(getUpdateDate());
		entity.setUserUpdate(getUserUpdate());
		entity.setSymbol(getSymbol());
		entity.setDescription(getDescription());
		entity.setTag(getTag());
		entity.setTagDate(getTagDate());
		entity.setTagDescription(getTagDescription());
		entity.setModel(getModel());
		return entity;
	}

	@Override
	public String toString() {
		return "QuantityOfInterest [" + "id=" + (id != null ? id.toString() : "") + RscTools.COMMA + "symbol=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (symbol != null ? symbol : "") + RscTools.COMMA + "creationDate=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (creationDate != null ? creationDate.toString() : "") + RscTools.COMMA + "tag=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (tag != null ? tag : "") + RscTools.COMMA + "tagDate=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (tagDate != null ? tagDate.toString() : "") + RscTools.COMMA + "parent=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (parent != null ? parent.toString() : "") + RscTools.COMMA + "model=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (model != null ? model.toString() : "") + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}