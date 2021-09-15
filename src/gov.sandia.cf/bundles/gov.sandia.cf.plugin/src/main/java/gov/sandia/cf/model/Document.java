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

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscTools;

/**
 * 
 * The Document entity class linked to table DOCUMENT
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "DOCUMENT")
public class Document implements Serializable, IEntity<Document, Integer> {

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
		LOCATION_URI("locationURI"), //$NON-NLS-1$
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
	private String name;

	/**
	 * The locationURI field linked to LOCATION_URI column
	 */
	@Column(name = "LOCATION_URI")
	private String locationURI;

	/**
	 * The phenomenon field linked to PHENOMENON_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHENOMENON_ID")
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
	public String getLocationURI() {
		return locationURI;
	}

	@SuppressWarnings("javadoc")
	public void setLocationURI(String locationURI) {
		this.locationURI = locationURI;
	}

	@SuppressWarnings("javadoc")
	public Phenomenon getPhenomenon() {
		return phenomenon;
	}

	@SuppressWarnings("javadoc")
	public void setPhenomenon(Phenomenon phenomenon) {
		this.phenomenon = phenomenon;
	}

	@Override
	public String toString() {
		return "Document [" + "id=" + (id != null ? id.toString() : "") + RscTools.COMMA + "name=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (name != null ? name : "") + RscTools.COMMA + "locationURI=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (locationURI != null ? locationURI : "") + RscTools.COMMA + "phenomenon=" //$NON-NLS-1$//$NON-NLS-2$
				+ (phenomenon != null ? phenomenon.toString() : "") + "]"; //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current Document
	 */
	public Document copy() {
		Document entity = new Document();
		entity.setLocationURI(getLocationURI());
		entity.setName(getName());
		return entity;
	}

}