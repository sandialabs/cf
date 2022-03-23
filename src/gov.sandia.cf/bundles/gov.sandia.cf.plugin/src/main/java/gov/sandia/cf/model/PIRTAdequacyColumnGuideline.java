/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.StringTools;

/**
 * Describes the PIRT Adequacy columns ranking guidelines
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PIRT_ADEQUACY_COLUMN_GUIDELINE")
public class PIRTAdequacyColumnGuideline implements Serializable, IEntity<PIRTAdequacyColumnGuideline, Integer>,
		IImportable<PIRTAdequacyColumnGuideline> {

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
	@NotBlank(message = RscConst.EX_PIRTADEQUACYCOLUMNGUIDELINE_NAME_BLANK)
	private String name;

	/**
	 * the column description
	 */
	@Column(name = "DESCRIPTION", columnDefinition = "LONGVARCHAR")
	private String description;

	/**
	 * the list of level guidelines
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "adequacyColumnGuideline")
	private List<PIRTAdequacyColumnLevelGuideline> listLevelGuidelines;

	/**
	 * 
	 * The constructor
	 */
	public PIRTAdequacyColumnGuideline() {
	}

	/**
	 * 
	 * The constructor
	 * 
	 * @param name               the column name
	 * @param description        the column description
	 * @param mapLevelGuidelines the level guideline map
	 */
	public PIRTAdequacyColumnGuideline(String name, String description, Map<String, String> mapLevelGuidelines) {
		super();
		this.name = name;
		this.description = description;
		if (mapLevelGuidelines != null) {
			this.listLevelGuidelines = new ArrayList<>();
			for (Entry<String, String> entry : mapLevelGuidelines.entrySet()) {
				if (entry != null) {
					this.listLevelGuidelines
							.add(new PIRTAdequacyColumnLevelGuideline(entry.getKey(), entry.getValue(), this));
				}
			}
		}
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
	public List<PIRTAdequacyColumnLevelGuideline> getLevelGuidelines() {
		return listLevelGuidelines;
	}

	@SuppressWarnings("javadoc")
	public void setLevelGuidelines(List<PIRTAdequacyColumnLevelGuideline> levelGuidelines) {
		this.listLevelGuidelines = levelGuidelines;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameKey(PIRTAdequacyColumnGuideline newImportable) {
		return newImportable != null && StringTools.equals(getName(), newImportable.getName());
	}

	@Override
	public boolean sameAs(PIRTAdequacyColumnGuideline newImportable) {

		if (newImportable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), newImportable.getName());
		boolean sameDescription = StringTools.equals(getDescription(), newImportable.getDescription());

		return sameName && sameDescription;
	}

	@Override
	public String getAbstract() {
		return getName();
	}

	@Override
	public String toString() {
		return "PIRTAdequacyColumnGuideline [id=" + id + ", name=" + name + ", description=" + description + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current PIRTAdequacyColumnGuideline
	 */
	public PIRTAdequacyColumnGuideline copy() {
		PIRTAdequacyColumnGuideline entity = new PIRTAdequacyColumnGuideline();
		entity.setName(getName());
		entity.setDescription(getDescription());
		return entity;
	}
}
