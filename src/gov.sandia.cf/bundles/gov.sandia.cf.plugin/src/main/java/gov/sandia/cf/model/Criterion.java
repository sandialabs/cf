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
import gov.sandia.cf.tools.RscTools;

/**
 * 
 * The Criterion entity class linked to table CRITERION
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "CRITERION")
public class Criterion implements Serializable, IEntity<Criterion, Integer> {

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
		TYPE("type"), //$NON-NLS-1$
		VALUE("value"), //$NON-NLS-1$
		PHENOMENON("phenomenon"); //$NON-NLS-1$

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
	@NotBlank(message = RscConst.EX_CRITERION_NAME_BLANK)
	private String name;

	/**
	 * The type field linked to TYPE column
	 */
	@Column(name = "TYPE")
	@NotBlank(message = RscConst.EX_CRITERION_TYPE_BLANK)
	private String type;

	/**
	 * The value field linked to VALUE column
	 */
	@Column(name = "VALUE", columnDefinition = "LONGVARCHAR")
	private String value;

	/**
	 * The phenomenon field linked to PHENOMENON_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHENOMENON_ID")
	@NotNull(message = RscConst.EX_CRITERION_PHENOMENON_NULL)
	private Phenomenon phenomenon;

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
	public String getType() {
		return type;
	}

	@SuppressWarnings("javadoc")
	public void setType(String type) {
		this.type = type;
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
	public Phenomenon getPhenomenon() {
		return phenomenon;
	}

	@SuppressWarnings("javadoc")
	public void setPhenomenon(Phenomenon phenomenon) {
		this.phenomenon = phenomenon;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Criterion [" + "id=" + (id != null ? id.toString() : "") + RscTools.COMMA + "name=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (name != null ? name : "") + RscTools.COMMA + "type=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (type != null ? type : "") + RscTools.COMMA + "value=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (value != null ? value : "") + RscTools.COMMA + "phenomenon=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (phenomenon != null ? phenomenon.toString() : "") + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current Criterion
	 */
	public Criterion copy() {
		Criterion entity = new Criterion();
		entity.setName(getName());
		entity.setType(getType());
		entity.setValue(getValue());
		return entity;
	}

}