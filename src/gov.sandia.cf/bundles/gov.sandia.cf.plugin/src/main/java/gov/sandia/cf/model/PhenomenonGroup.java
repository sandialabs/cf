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
 * The PhenomenonGroup entity class linked to table PHENOMENAGROUP
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PHENOMENAGROUP")
public class PhenomenonGroup
		implements Serializable, IEntity<PhenomenonGroup, Integer>, ISelectValue, ISortableByIdEntity {

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
		QOI("qoi"); //$NON-NLS-1$

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
	@NotBlank(message = RscConst.EX_PHENOMENONGROUP_NAME_BLANK)
	private String name;

	/**
	 * The qoi field linked to QOI_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QOI_ID")
	@NotNull(message = RscConst.EX_PHENOMENONGROUP_QOI_NULL)
	private QuantityOfInterest qoi;

	/**
	 * The phenomenonList field linked to phenomenonList column
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "phenomenonGroup", cascade = { CascadeType.REMOVE,
			CascadeType.REFRESH })
	private List<Phenomenon> phenomenonList;

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

	@SuppressWarnings("javadoc")
	public String getName() {
		return name;
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
	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("javadoc")
	public QuantityOfInterest getQoi() {
		return qoi;
	}

	@SuppressWarnings("javadoc")
	public void setQoi(QuantityOfInterest qoi) {
		this.qoi = qoi;
	}

	@SuppressWarnings("javadoc")
	public List<Phenomenon> getPhenomenonList() {
		return phenomenonList;
	}

	@SuppressWarnings("javadoc")
	public void setPhenomenonList(List<Phenomenon> phenomenonList) {
		this.phenomenonList = phenomenonList;
	}

	@Override
	public String toString() {
		return "PhenomenonGroup [" + "id=" + (id != null ? id.toString() : "") + RscTools.COMMA + "idLabel=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (idLabel != null ? idLabel : "") + RscTools.COMMA + "name=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (name != null ? name : "") + RscTools.COMMA + "qoi=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (qoi != null ? qoi.toString() : "") + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current PhenomenonGroup
	 */
	public PhenomenonGroup copy() {
		PhenomenonGroup entity = new PhenomenonGroup();
		entity.setName(getName());
		entity.setIdLabel(getIdLabel());
		return entity;
	}

	@Override
	public String getSelectName() {
		return getIdLabel() + RscTools.COLON + getName();
	}

}