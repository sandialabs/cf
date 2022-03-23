/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
 * The Configuration File entity class linked to table CONFIGURATION_FILE
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "CONFIGURATION_FILE")
public class ConfigurationFile implements Serializable, IEntity<ConfigurationFile, Integer> {

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
		PATH("path"), //$NON-NLS-1$
		CF_FEATURE("feature"), //$NON-NLS-1$
		USER_IMPORT("userImport"), //$NON-NLS-1$
		MODEL("model"); //$NON-NLS-1$

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
	 * The name path linked to PATH column
	 */
	@Column(name = "PATH")
	@NotBlank(message = RscConst.EX_CONFFILE_PATH_BLANK)
	private String path;

	/**
	 * The feature linked to FEATURE column
	 */
	@Column(name = "FEATURE")
	@NotNull(message = RscConst.EX_CONFFILE_FEATURE_NULL)
	@Enumerated(EnumType.STRING)
	private CFFeature feature;

	/**
	 * The userImport field linked to USER_IMPORT_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_IMPORT_ID")
	private User userImport;

	/**
	 * The dateImport linked to DATE_IMPORT column
	 */
	@Column(name = "DATE_IMPORT")
	@NotNull(message = RscConst.EX_CONFFILE_DATEIMPORT_NULL)
	private Date dateImport;

	/**
	 * The model field linked to MODEL_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	@NotNull(message = RscConst.EX_CONFFILE_MODEL_NULL)
	private Model model;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 
	 * Sets the id field with @param id
	 * 
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public String getPath() {
		return path;
	}

	@SuppressWarnings("javadoc")
	public void setPath(String path) {
		this.path = path;
	}

	@SuppressWarnings("javadoc")
	public CFFeature getFeature() {
		return feature;
	}

	@SuppressWarnings("javadoc")
	public void setFeature(CFFeature feature) {
		this.feature = feature;
	}

	@SuppressWarnings("javadoc")
	public User getUserImport() {
		return userImport;
	}

	@SuppressWarnings("javadoc")
	public void setUserImport(User userImport) {
		this.userImport = userImport;
	}

	@SuppressWarnings("javadoc")
	public Date getDateImport() {
		return Optional.ofNullable(dateImport).map(Date::getTime).map(Date::new).orElse(null);
	}

	@SuppressWarnings("javadoc")
	public void setDateImport(Date dateImport) {
		this.dateImport = Optional.ofNullable(dateImport).map(Date::getTime).map(Date::new).orElse(dateImport);
	}

	@SuppressWarnings("javadoc")
	public Model getModel() {
		return model;
	}

	@SuppressWarnings("javadoc")
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ConfigurationFile [" + "id=" + (id != null ? id.toString() : "") + RscTools.COMMA + "path=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (path != null ? path : "") + RscTools.COMMA + "feature=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (feature != null ? feature : "") + RscTools.COMMA + "userImport=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (userImport != null ? userImport.toString() : "") + RscTools.COMMA + "dateImport=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (dateImport != null ? dateImport.toString() : "") + RscTools.COMMA + "model=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (model != null ? model.toString() : "") + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current Configuration File
	 */
	public ConfigurationFile copy() {
		ConfigurationFile entity = new ConfigurationFile();
		entity.setPath(getPath());
		entity.setFeature(getFeature());
		entity.setUserImport(getUserImport());
		entity.setDateImport(getDateImport());
		entity.setModel(getModel());
		return entity;
	}

}