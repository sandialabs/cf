/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.MathTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.StringTools;

/**
 * Describes PIRT Importance Level
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PIRT_LEVEL_IMPORTANCE")
public class PIRTLevelImportance
		implements Serializable, IEntity<PIRTLevelImportance, Integer>, IImportable<PIRTLevelImportance>, ISelectValue {

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
		LEVEL("level"), //$NON-NLS-1$
		LABEL("label"), //$NON-NLS-1$
		FIXED_COLOR("fixedColor"), //$NON-NLS-1$
		FIXED_COLOR_DESCRIPTION("fixedColorDescription"); //$NON-NLS-1$

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
	 * the importance level name
	 */
	@Column(name = "NAME")
	@NotBlank(message = RscConst.EX_PIRTLEVELIMPORTANCE_NAME_BLANK)
	private String name;

	/**
	 * the importance level id
	 */
	@Column(name = "ID_LABEL")
	@NotBlank(message = RscConst.EX_PIRTLEVELIMPORTANCE_IDLABEL_BLANK)
	private String idLabel;

	/**
	 * the importance level numerical level to compare with other levels
	 */
	@Column(name = "LEVEL")
	@NotNull(message = RscConst.EX_PIRTLEVELIMPORTANCE_LEVEL_BLANK)
	private Integer level;

	/**
	 * the importance level label to show
	 */
	@Column(name = "LABEL")
	@NotBlank(message = RscConst.EX_PIRTLEVELIMPORTANCE_LABEL_BLANK)
	private String label;

	/**
	 * the importance level fixed color
	 */
	private String fixedColor;

	/**
	 * the importance level fixed color description
	 */
	@Column(name = "FIXEDCOLORDESC", columnDefinition = "LONGVARCHAR")
	private String fixedColorDescription;

	/**
	 * The constructor
	 */
	public PIRTLevelImportance() {
	}

	/**
	 * 
	 * The constructor
	 * 
	 * @param idLabel               the idLabel
	 * @param name                  the name
	 * @param level                 the level
	 * @param label                 the label
	 * @param fixedColor            the fixed rgb color
	 * @param fixedColorDescription the fixed color description
	 */
	public PIRTLevelImportance(String idLabel, String name, Integer level, String label, String fixedColor,
			String fixedColorDescription) {
		super();
		this.name = name;
		this.idLabel = idLabel;
		this.level = level;
		this.label = label;
		this.fixedColor = fixedColor;
		this.fixedColorDescription = fixedColorDescription;
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
	public String getIdLabel() {
		return idLabel;
	}

	@SuppressWarnings("javadoc")
	public void setIdLabel(String idLabel) {
		this.idLabel = idLabel;
	}

	@SuppressWarnings("javadoc")
	public Integer getLevel() {
		return level;
	}

	@SuppressWarnings("javadoc")
	public void setLevel(Integer level) {
		this.level = level;
	}

	@SuppressWarnings("javadoc")
	public String getLabel() {
		return label;
	}

	@SuppressWarnings("javadoc")
	public void setLabel(String label) {
		this.label = label;
	}

	@SuppressWarnings("javadoc")
	public String getFixedColor() {
		return fixedColor;
	}

	@SuppressWarnings("javadoc")
	public void setFixedColor(String fixedColor) {
		this.fixedColor = fixedColor;
	}

	@SuppressWarnings("javadoc")
	public String getFixedColorDescription() {
		return fixedColorDescription;
	}

	@SuppressWarnings("javadoc")
	public void setFixedColorDescription(String fixedColorDescription) {
		this.fixedColorDescription = fixedColorDescription;
	}

	@Override
	public boolean sameAs(PIRTLevelImportance newImportable) {

		if (newImportable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), newImportable.getName());
		boolean sameIdLabel = StringTools.equals(getIdLabel(), newImportable.getIdLabel());
		boolean sameType = StringTools.equals(getLabel(), newImportable.getLabel());
		boolean sameFixedColor = StringTools.equals(getFixedColor(), newImportable.getFixedColor());
		boolean sameLevel = MathTools.equals(getLevel(), newImportable.getLevel());

		return sameName && sameIdLabel && sameType && sameFixedColor && sameLevel;
	}

	@Override
	public String getAbstract() {
		return getName();
	}

	@Override
	public String toString() {
		return "PIRTLevelImportance [id=" + id + ", name=" + name + ", idLabel=" + idLabel + ", level=" + level //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ ", label=" + label + ", fixedColor=" + fixedColor + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current PIRTLevelImportance
	 */
	public PIRTLevelImportance copy() {
		PIRTLevelImportance entity = new PIRTLevelImportance();
		entity.setIdLabel(getIdLabel());
		entity.setName(getName());
		entity.setFixedColor(getFixedColor());
		entity.setLabel(getLabel());
		entity.setLevel(getLevel());
		return entity;
	}

	@Override
	public String getSelectName() {
		return getName();
	}
}
