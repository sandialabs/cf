/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.List;

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
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * 
 * The Phenomenon entity class linked to table PHENOMENON
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PHENOMENON")
public class Phenomenon implements Serializable, IEntity<Phenomenon, Integer>, ISortableByIdEntity {

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
		ID_LABEL("idLabel"), //$NON-NLS-1$
		NAME("name"), //$NON-NLS-1$
		IMPORTANCE("importance"), //$NON-NLS-1$
		PHENOMENON_GROUP("phenomenonGroup"); //$NON-NLS-1$

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
	 * The idLabel field linked to ID_LABEL column
	 */
	@Column(name = "ID_LABEL")
	private String idLabel;

	/**
	 * The name field linked to NAME column
	 */
	@Column(name = "NAME")
	@NotBlank(message = RscConst.EX_PHENOMENON_NAME_BLANK)
	private String name;

	/**
	 * The importance field linked to IMPORTANCE column
	 */
	@Column(name = "IMPORTANCE")
	private String importance;

	/**
	 * The phenomenonGroup field linked to PHENOMENONGROUP_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHENOMENONGROUP_ID")
	@NotNull(message = RscConst.EX_PHENOMENON_GROUP_NULL)
	private PhenomenonGroup phenomenonGroup;

	/**
	 * The criterionList field linked to criterionList column
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "phenomenon", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Criterion> criterionList;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public String getIdLabel() {
		return idLabel;
	}

	@SuppressWarnings("javadoc")
	public void setIdLabel(String idLabel) {
		this.idLabel = idLabel;
	}

	@Override
	public String getGeneratedId() {
		return getIdLabel();
	}

	@Override
	public void setGeneratedId(String generatedId) {
		setIdLabel(generatedId);
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
	public String getImportance() {
		return importance;
	}

	@SuppressWarnings("javadoc")
	public void setImportance(String importance) {
		this.importance = importance;
	}

	@SuppressWarnings("javadoc")
	public PhenomenonGroup getPhenomenonGroup() {
		return phenomenonGroup;
	}

	@SuppressWarnings("javadoc")
	public void setPhenomenonGroup(PhenomenonGroup phenomenonGroup) {
		this.phenomenonGroup = phenomenonGroup;
	}

	@SuppressWarnings("javadoc")
	public List<Criterion> getCriterionList() {
		return criterionList;
	}

	@SuppressWarnings("javadoc")
	public void setCriterionList(List<Criterion> criterionList) {
		this.criterionList = criterionList;
	}

	@Override
	public String toString() {
		return "Phenomenon [" + "id=" + (id != null ? id.toString() : "") + RscTools.COMMA + "idLabel=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (idLabel != null ? idLabel : "") + RscTools.COMMA + "name=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (name != null ? name : "") + RscTools.COMMA + "importance=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (importance != null ? importance : "") + RscTools.COMMA + "phenomenonGroup=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (phenomenonGroup != null ? phenomenonGroup.toString() : "") + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current Phenomenon
	 */
	public Phenomenon copy() {
		Phenomenon entity = new Phenomenon();
		entity.setIdLabel(getIdLabel());
		entity.setImportance(getImportance());
		entity.setName(getName());
		return entity;
	}

}