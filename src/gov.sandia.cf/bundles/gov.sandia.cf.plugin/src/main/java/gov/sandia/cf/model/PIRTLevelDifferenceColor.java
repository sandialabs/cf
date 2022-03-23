/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.List;

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
 * 
 * Describes Pirt color difference attributes
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PIRT_LEVEL_DIFF_COLOR")
public class PIRTLevelDifferenceColor
		implements Serializable, IEntity<PIRTLevelDifferenceColor, Integer>, IImportable<PIRTLevelDifferenceColor> {

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
		COLOR("color"), //$NON-NLS-1$
		DESCRIPTION("description"), //$NON-NLS-1$
		EXPLANATION("explanation"), //$NON-NLS-1$
		MIN("min"), //$NON-NLS-1$
		MAX("max"); //$NON-NLS-1$

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
	 * Color corresponding to specified range of level difference
	 */
	@Column(name = "COLOR")
	@NotBlank(message = RscConst.EX_PIRTLEVELDIFFCOLOR_COLOR_BLANK)
	private String color;

	/**
	 * The color description
	 */
	@Column(name = "DESCRIPTION", columnDefinition = "LONGVARCHAR")
	private String description;

	/**
	 * The color explanation
	 */
	@Column(name = "EXPLANATION", columnDefinition = "LONGVARCHAR")
	private String explanation;

	/**
	 * Minimum level difference affected to this color
	 */
	@Column(name = "MIN")
	@NotNull(message = RscConst.EX_PIRTLEVELDIFFCOLOR_MIN_NULL)
	private Integer min;

	/**
	 * Maximum level difference affected to this color
	 */
	@Column(name = "MAX")
	@NotNull(message = RscConst.EX_PIRTLEVELDIFFCOLOR_MAX_NULL)
	private Integer max;

	/**
	 * The constructor
	 */
	public PIRTLevelDifferenceColor() {
	}

	/**
	 * The constructor
	 * 
	 * @param color       the pirt color
	 * @param minMax      the min max values list
	 * @param description the pirt level color description
	 * @param explanation the pirt level color explanation
	 */
	public PIRTLevelDifferenceColor(String color, List<Integer> minMax, String description, String explanation) {
		this.color = color;
		this.min = minMax.get(0);
		this.max = minMax.get(1);
		this.description = description;
		this.explanation = explanation;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
	public Integer getMin() {
		return min;
	}

	@SuppressWarnings("javadoc")
	public void setMin(Integer min) {
		this.min = min;
	}

	@SuppressWarnings("javadoc")
	public Integer getMax() {
		return max;
	}

	@SuppressWarnings("javadoc")
	public void setMax(Integer max) {
		this.max = max;
	}

	@SuppressWarnings("javadoc")
	public boolean isInRange(int currValue) {
		return currValue >= min && currValue <= max;
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
	public String getExplanation() {
		return explanation;
	}

	@SuppressWarnings("javadoc")
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameKey(PIRTLevelDifferenceColor newImportable) {
		return newImportable != null && StringTools.equals(getDescription(), newImportable.getDescription());
	}

	@Override
	public boolean sameAs(PIRTLevelDifferenceColor newImportable) {

		if (newImportable == null) {
			return false;
		}

		boolean sameColor = StringTools.equals(getColor(), newImportable.getColor());
		boolean sameDescription = StringTools.equals(getDescription(), newImportable.getDescription());
		boolean sameMin = MathTools.equals(getMin(), newImportable.getMin());
		boolean sameMax = MathTools.equals(getMax(), newImportable.getMax());

		return sameColor && sameDescription && sameMin && sameMax;
	}

	@Override
	public String getAbstract() {
		return getDescription();
	}

	@Override
	public String toString() {
		return "PIRTLevelDifferenceColor [id=" + id + ", color=" + color + ", description=" + description + ", min=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ min + ", max=" + max + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current PIRTLevelDifferenceColor
	 */
	public PIRTLevelDifferenceColor copy() {
		PIRTLevelDifferenceColor entity = new PIRTLevelDifferenceColor();
		entity.setColor(getColor());
		entity.setDescription(getDescription());
		entity.setMax(getMax());
		entity.setMin(getMin());
		return entity;
	}
}
