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

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.StringTools;

/**
 * Describes the PIRT adequacy column properties
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PIRT_ADEQUACY_COLUMN")
public class PIRTAdequacyColumn
		implements Serializable, IEntity<PIRTAdequacyColumn, Integer>, IImportable<PIRTAdequacyColumn> {

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
		TYPE("type"); //$NON-NLS-1$

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
	@NotBlank(message = RscConst.EX_PIRTADEQUACYCOLUMN_NAME_BLANK)
	private String name;

	/**
	 * the column idLabel
	 */
	@Column(name = "ID_LABEL")
	@NotBlank(message = RscConst.EX_PIRTADEQUACYCOLUMN_IDLABEL_BLANK)
	private String idLabel;

	/**
	 * the column type
	 */
	@Column(name = "TYPE")
	@NotBlank(message = RscConst.EX_PIRTADEQUACYCOLUMN_TYPE_BLANK)
	private String type;

	/**
	 * The constructor
	 */
	public PIRTAdequacyColumn() {
	}

	/**
	 * 
	 * The constructor
	 * 
	 * @param name    the name
	 * @param idLabel the idLabel
	 * @param type    the type
	 */
	public PIRTAdequacyColumn(String name, String idLabel, String type) {
		super();
		this.name = name;
		this.idLabel = idLabel;
		this.type = type;
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
	public String getType() {
		return type;
	}

	@SuppressWarnings("javadoc")
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameKey(PIRTAdequacyColumn newImportable) {
		return newImportable != null && StringTools.equals(getName(), newImportable.getName());
	}

	@Override
	public boolean sameAs(PIRTAdequacyColumn newImportable) {

		if (newImportable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), newImportable.getName());
		boolean sameIdLabel = StringTools.equals(getIdLabel(), newImportable.getIdLabel());
		boolean sameType = StringTools.equals(getType(), newImportable.getType());

		return sameName && sameIdLabel && sameType;
	}

	@Override
	public String getAbstract() {
		return getName();
	}

	@Override
	public String toString() {
		return "PIRTTreeAdequacyColumn [id=" + id + ", name=" + name + ", idLabel=" + idLabel + ", type=" + type + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current PIRTTreeAdequacyColumn
	 */
	public PIRTAdequacyColumn copy() {
		PIRTAdequacyColumn entity = new PIRTAdequacyColumn();
		entity.setIdLabel(getIdLabel());
		entity.setName(getName());
		entity.setType(getType());
		return entity;
	}
}
