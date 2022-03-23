/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.List;

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
import gov.sandia.cf.tools.StringTools;

/**
 * The PCMM subelement
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PCMMSUBELEMENT")
public class PCMMSubelement
		implements Serializable, IEntity<PCMMSubelement, Integer>, IAssessable, IImportable<PCMMSubelement> {

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
		CODE("code"), //$NON-NLS-1$
		ELEMENT("element"); //$NON-NLS-1$

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
	@NotBlank(message = RscConst.EX_PCMMSUBELEMENT_NAME_BLANK)
	private String name;

	/**
	 * The code field linked to CODE column
	 */
	@Column(name = "CODE")
	@NotBlank(message = RscConst.EX_PCMMSUBELEMENT_CODE_BLANK)
	private String code;

	/**
	 * The element field linked to PCMMELEMENT_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCMMELEMENT_ID")
	@NotNull(message = RscConst.EX_PCMMSUBELEMENT_PCMMELEMENT_NULL)
	private PCMMElement element;

	/**
	 * The evidenceList field linked to evidenceList column
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "subelement")
	private List<PCMMEvidence> evidenceList;

	/**
	 * The levelList field linked to levelList column
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "subelement")
	private List<PCMMLevel> levelList;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("javadoc")
	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("javadoc")
	public String getCode() {
		return code;
	}

	@SuppressWarnings("javadoc")
	public void setCode(String code) {
		this.code = code;
	}

	@SuppressWarnings("javadoc")
	public PCMMElement getElement() {
		return element;
	}

	@SuppressWarnings("javadoc")
	public void setElement(PCMMElement element) {
		this.element = element;
	}

	@SuppressWarnings("javadoc")
	public List<PCMMEvidence> getEvidenceList() {
		return evidenceList;
	}

	@SuppressWarnings("javadoc")
	public void setEvidenceList(List<PCMMEvidence> evidenceList) {
		this.evidenceList = evidenceList;
	}

	public List<PCMMLevel> getLevelList() {
		return levelList;
	}

	@SuppressWarnings("javadoc")
	public void setLevelList(List<PCMMLevel> levelList) {
		this.levelList = levelList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameKey(PCMMSubelement newImportable) {
		return newImportable != null && StringTools.equals(getCode(), newImportable.getCode());
	}

	@Override
	public boolean sameAs(PCMMSubelement importable) {

		if (importable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), importable.getName());
		boolean sameCode = StringTools.equals(getCode(), importable.getCode());
		boolean sameElement = (getElement() == null && importable.getElement() == null)
				|| (getElement() != null && importable.getElement() != null
						&& StringTools.equals(getElement().getName(), importable.getElement().getName()));

		return sameName && sameCode && sameElement;
	}

	@Override
	public String getAbstract() {
		return new StringBuilder().append(getCode()).append(" - ").append(getName()).toString(); //$NON-NLS-1$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current PCMMSubelement
	 */
	public PCMMSubelement copy() {
		PCMMSubelement entity = new PCMMSubelement();
		entity.setName(getName());
		entity.setCode(getCode());
		return entity;
	}
}
