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
 * Describes the PIRT Adequacy columns ranking level guidelines
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PIRT_ADEQUACY_LEVEL_GUIDELINE")
public class PIRTAdequacyColumnLevelGuideline implements Serializable,
		IEntity<PIRTAdequacyColumnLevelGuideline, Integer>, IImportable<PIRTAdequacyColumnLevelGuideline> {

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
		DESCRIPTION("description"); //$NON-NLS-1$

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
	 * the column name
	 */
	@Column(name = "NAME")
	@NotBlank(message = RscConst.EX_PIRTADEQUACYLEVELGUIDELINE_NAME_BLANK)
	private String name;

	/**
	 * the column description
	 */
	@Column(name = "DESCRIPTION", columnDefinition = "LONGVARCHAR")
	private String description;

	/**
	 * the adequacy column guideline
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PIRT_ADEQUACY_GUIDELINE_ID")
	@NotNull(message = RscConst.EX_PIRTADEQUACYLEVELGUIDELINE_COLUMN_NULL)
	private PIRTAdequacyColumnGuideline adequacyColumnGuideline;

	/**
	 * The constructor
	 */
	public PIRTAdequacyColumnLevelGuideline() {
	}

	/**
	 * 
	 * The constructor
	 * 
	 * @param name                    the name
	 * @param description             the description
	 * @param adequacyColumnGuideline the PIRT adequacy Column Guideline
	 */
	public PIRTAdequacyColumnLevelGuideline(String name, String description,
			PIRTAdequacyColumnGuideline adequacyColumnGuideline) {
		super();
		this.name = name;
		this.description = description;
		this.adequacyColumnGuideline = adequacyColumnGuideline;
	}

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
	public String getDescription() {
		return description;
	}

	@SuppressWarnings("javadoc")
	public void setDescription(String description) {
		this.description = description;
	}

	@SuppressWarnings("javadoc")
	public PIRTAdequacyColumnGuideline getAdequacyColumnGuideline() {
		return adequacyColumnGuideline;
	}

	@SuppressWarnings("javadoc")
	public void setAdequacyColumnGuideline(PIRTAdequacyColumnGuideline adequacyColumnGuideline) {
		this.adequacyColumnGuideline = adequacyColumnGuideline;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameKey(PIRTAdequacyColumnLevelGuideline importable) {

		if (importable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), importable.getName());
		boolean sameGuideline = (getAdequacyColumnGuideline() == null
				&& importable.getAdequacyColumnGuideline() == null)
				|| (getAdequacyColumnGuideline() != null
						&& getAdequacyColumnGuideline().sameKey(importable.getAdequacyColumnGuideline()));

		return sameName && sameGuideline;
	}

	@Override
	public boolean sameAs(PIRTAdequacyColumnLevelGuideline importable) {

		if (importable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), importable.getName());
		boolean sameDescription = StringTools.equals(getDescription(), importable.getDescription());
		boolean sameGuideline = (getAdequacyColumnGuideline() == null
				&& importable.getAdequacyColumnGuideline() == null)
				|| (getAdequacyColumnGuideline() != null
						&& getAdequacyColumnGuideline().sameAs(importable.getAdequacyColumnGuideline()));

		return sameName && sameGuideline && sameDescription;
	}

	@Override
	public String getAbstract() {
		return getName();
	}

	@Override
	public String toString() {
		return "PIRTAdequacyColumnLevelGuideline [id=" + id + ", name=" + name + ", description=" + description //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ ", adequacyColumnGuideline=" + adequacyColumnGuideline + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current PIRTAdequacyColumnLevelGuideline
	 */
	public PIRTAdequacyColumnLevelGuideline copy() {
		PIRTAdequacyColumnLevelGuideline entity = new PIRTAdequacyColumnLevelGuideline();
		entity.setName(getName());
		entity.setDescription(getDescription());
		entity.setAdequacyColumnGuideline(getAdequacyColumnGuideline());
		return entity;
	}
}
