/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.StringTools;

/**
 * The PCMM level descriptor
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PCMMLEVELDESC")
public class PCMMLevelDescriptor
		implements Serializable, IEntity<PCMMLevelDescriptor, Integer>, IImportable<PCMMLevelDescriptor> {

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
		VALUE("value"), //$NON-NLS-1$
		LEVEL("level"); //$NON-NLS-1$

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
	@NotBlank(message = RscConst.EX_PCMMLEVELDESCRIPTOR_NAME_BLANK)
	private String name;

	/**
	 * The value field linked to VALUE column
	 */
	@Column(name = "VALUE", length = 1500)
	private String value;

	/**
	 * The level field linked to PCMMLEVEL_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCMMLEVEL_ID")
	@NotNull(message = RscConst.EX_PCMMLEVELDESCRIPTOR_LEVEL_NULL)
	private PCMMLevel level;

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
	public String getValue() {
		return value;
	}

	@SuppressWarnings("javadoc")
	public void setValue(String value) {
		this.value = value;
	}

	@SuppressWarnings("javadoc")
	public PCMMLevel getLevel() {
		return level;
	}

	@SuppressWarnings("javadoc")
	public void setLevel(PCMMLevel level) {
		this.level = level;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameKey(PCMMLevelDescriptor newImportable) {
		return newImportable != null && StringTools.equals(getName(), newImportable.getName());
	}

	@Override
	public boolean sameAs(PCMMLevelDescriptor importable) {

		if (importable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), importable.getName());
		boolean sameValue = StringTools.equals(getValue(), importable.getValue());
		boolean sameLevel = (getLevel() == null && importable.getLevel() == null)
				|| (getLevel() != null && importable.getLevel() != null
						&& StringTools.equals(getLevel().getName(), importable.getLevel().getName()));

		return sameName && sameValue && sameLevel;
	}

	@Override
	public String getAbstract() {
		return getName();
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current PCMMLevelDescriptor
	 */
	public PCMMLevelDescriptor copy() {
		PCMMLevelDescriptor entity = new PCMMLevelDescriptor();
		entity.setName(getName());
		entity.setValue(getValue());
		return entity;
	}
}
