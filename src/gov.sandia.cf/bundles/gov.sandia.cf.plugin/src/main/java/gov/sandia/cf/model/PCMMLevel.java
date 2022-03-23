/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
import gov.sandia.cf.tools.MathTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.StringTools;

/**
 * The PCMM level
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PCMMLEVEL")
public class PCMMLevel implements Serializable, IEntity<PCMMLevel, Integer>, IImportable<PCMMLevel>, ISelectValue {

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
		ELEMENT("element"), //$NON-NLS-1$
		SUBELEMENT("subelement"); //$NON-NLS-1$

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
	@Column(name = "NAME")
	@NotBlank(message = RscConst.EX_PCMMLEVEL_NAME_BLANK)
	private String name;

	/**
	 * The code field linked to CODE column
	 */
	@Column(name = "CODE")
	@NotNull(message = RscConst.EX_PCMMLEVEL_CODE_NULL)
	private Integer code;

	/**
	 * The element field linked to PCMMELEMENT_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCMMELEMENT_ID")
	private PCMMElement element;

	/**
	 * The subelement field linked to PCMMSUBELEMENT_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCMMSUBELEMENT_ID")
	private PCMMSubelement subelement;

	/**
	 * The level field linked to levelDescriptorList column
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "level", cascade = CascadeType.PERSIST)
	private List<PCMMLevelDescriptor> levelDescriptorList;

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
	public Integer getCode() {
		return code;
	}

	@SuppressWarnings("javadoc")
	public void setCode(Integer code) {
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
	public PCMMSubelement getSubelement() {
		return subelement;
	}

	@SuppressWarnings("javadoc")
	public void setSubelement(PCMMSubelement subelement) {
		this.subelement = subelement;
	}

	@SuppressWarnings("javadoc")
	public List<PCMMLevelDescriptor> getLevelDescriptorList() {
		return levelDescriptorList;
	}

	@SuppressWarnings("javadoc")
	public void setLevelDescriptorList(List<PCMMLevelDescriptor> levelDescriptorList) {
		this.levelDescriptorList = levelDescriptorList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameKey(PCMMLevel newImportable) {
		return newImportable != null && Objects.equals(getCode(), newImportable.getCode());
	}

	@Override
	public boolean sameAs(PCMMLevel importable) {

		if (importable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), importable.getName());
		boolean sameCode = MathTools.equals(getCode(), importable.getCode());
		boolean sameElement = (getElement() == null && importable.getElement() == null)
				|| (getElement() != null && importable.getElement() != null
						&& StringTools.equals(getElement().getName(), importable.getElement().getName()));
		boolean sameSubelement = (getSubelement() == null && importable.getSubelement() == null)
				|| (getSubelement() != null && importable.getSubelement() != null
						&& StringTools.equals(getSubelement().getName(), importable.getSubelement().getName()));

		return sameName && sameCode && sameElement && sameSubelement;
	}

	@Override
	public String getAbstract() {
		return new StringBuilder().append(getCode()).append(" - ").append(getName()).toString(); //$NON-NLS-1$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current PCMMLevel
	 */
	public PCMMLevel copy() {
		PCMMLevel entity = new PCMMLevel();
		entity.setName(getName());
		entity.setCode(getCode());
		return entity;
	}

	@Override
	public String getSelectName() {
		return getName();
	}
}
