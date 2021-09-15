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
 * The PCMM element
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PCMMELEMENT")
public class PCMMElement
		implements Serializable, IEntity<PCMMElement, Integer>, IAssessable, IImportable<PCMMElement>, ISelectValue {

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
		COLOR("color"), //$NON-NLS-1$
		ABBREVIATION("abbreviation"), //$NON-NLS-1$
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
	@NotBlank(message = RscConst.EX_PCMMELEMENT_NAME_BLANK)
	private String name;

	/**
	 * The color field linked to COLOR column
	 */
	@Column(name = "COLOR")
	private String color;

	/**
	 * The abbreviation field linked to ABBREVIATION column
	 */
	@Column(name = "ABBREVIATION")
	@NotBlank(message = RscConst.EX_PCMMELEMENT_ABBREV_BLANK)
	private String abbreviation;

	/**
	 * The model field linked to MODEL_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	@NotNull(message = RscConst.EX_PCMMELEMENT_MODEL_NULL)
	private Model model;

	/**
	 * The subElementList field linked to subElementList column
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "element")
	private List<PCMMSubelement> subElementList;

	/**
	 * The subElementList field linked to subElementList column
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "element")
	private List<PCMMLevel> levelList;

	/**
	 * The evidenceList field linked to evidenceList column
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "element")
	private List<PCMMEvidence> evidenceList;

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
	public String getColor() {
		return color;
	}

	@SuppressWarnings("javadoc")
	public void setColor(String color) {
		this.color = color;
	}

	@SuppressWarnings("javadoc")
	public String getAbbreviation() {
		return abbreviation;
	}

	@SuppressWarnings("javadoc")
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
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
	public List<PCMMSubelement> getSubElementList() {
		return subElementList;
	}

	@SuppressWarnings("javadoc")
	public void setSubElementList(List<PCMMSubelement> subElementList) {
		this.subElementList = subElementList;
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

	@Override
	public boolean sameAs(PCMMElement newImportable) {

		if (newImportable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), newImportable.getName());
		boolean sameAbbreviation = StringTools.equals(getAbbreviation(), newImportable.getAbbreviation());
		boolean sameColor = StringTools.equals(getColor(), newImportable.getColor());

		return sameName && sameAbbreviation && sameColor;
	}

	@Override
	public String getSelectName() {
		return getAbstract();
	}

	@Override
	public String getAbstract() {
		return new StringBuilder().append(getAbbreviation()).append(" (").append(getName()).append(")").toString(); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current PCMMLevel
	 */
	public PCMMElement copy() {
		PCMMElement entity = new PCMMElement();
		entity.setName(getName());
		entity.setAbbreviation(getAbbreviation());
		entity.setColor(getColor());
		return entity;
	}
}
