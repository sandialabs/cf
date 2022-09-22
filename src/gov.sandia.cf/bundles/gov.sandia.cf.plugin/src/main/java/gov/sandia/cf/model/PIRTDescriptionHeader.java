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
 * Pirt header attributes.
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PIRT_DESC_HEADER")
public class PIRTDescriptionHeader implements Serializable, IEntity<PIRTDescriptionHeader, Integer>,
		IImportable<PIRTDescriptionHeader>, ISortableByIdEntity {

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
		NAME("name"); //$NON-NLS-1$

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
	 * first header table column name
	 */
	@Column(name = "NAME")
	@NotBlank(message = RscConst.EX_PIRTDESCHEADER_NAME_BLANK)
	private String name;

	/**
	 * attribute id
	 */
	@Column(name = "ID_LABEL")
	@NotBlank(message = RscConst.EX_PIRTDESCHEADER_IDLABEL_BLANK)
	private String idLabel;

	/**
	 * Constructor
	 */
	public PIRTDescriptionHeader() {
	}

	/**
	 * 
	 * The constructor
	 * 
	 * @param name    the name
	 * @param idLabel the idLabel
	 */
	public PIRTDescriptionHeader(String name, String idLabel) {
		super();
		this.name = name;
		this.idLabel = idLabel;
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

	@Override
	public String getGeneratedId() {
		return getIdLabel();
	}

	@Override
	public void setGeneratedId(String generatedId) {
		setIdLabel(generatedId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameKey(PIRTDescriptionHeader newImportable) {
		return newImportable != null && StringTools.equals(getName(), newImportable.getName());
	}

	@Override
	public boolean sameAs(PIRTDescriptionHeader newImportable) {

		if (newImportable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), newImportable.getName());
		boolean sameIdLabel = StringTools.equals(getIdLabel(), newImportable.getIdLabel());

		return sameName && sameIdLabel;
	}

	@Override
	public String getAbstract() {
		return getName();
	}

	@Override
	public String toString() {
		return "PIRTDescriptionHeader [id=" + id + ", name=" + name + ", idLabel=" + idLabel + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current PIRTDescriptionHeader
	 */
	public PIRTDescriptionHeader copy() {
		PIRTDescriptionHeader entity = new PIRTDescriptionHeader();
		entity.setIdLabel(getIdLabel());
		entity.setName(getName());
		return entity;
	}
}
