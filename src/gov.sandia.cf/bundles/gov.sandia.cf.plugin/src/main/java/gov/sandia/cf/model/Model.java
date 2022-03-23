/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

/**
 * The Model entity class linked to table MODEL
 * 
 * @author Didier Verstraete
 */
@Entity
@Table(name = "MODEL")
public class Model implements Serializable, IEntity<Model, Integer> {

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
		APPLICATION("application"), //$NON-NLS-1$
		CONTACT("contact"), //$NON-NLS-1$
		VERSION_ORIGIN("versionOrigin"), //$NON-NLS-1$
		VERSION("version"); //$NON-NLS-1$

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
	 * The name field linked to APPLICATION column
	 */
	@Column(name = "APPLICATION")
	private String application;

	/**
	 * The name field linked to CONTACT column
	 */
	@Column(name = "CONTACT")
	private String contact;

	/**
	 * The name field linked to VERSION_ORIGIN
	 */
	@Column(name = "VERSION_ORIGIN")
	private String versionOrigin;

	/**
	 * The name field linked to VERSION
	 */
	@Column(name = "VERSION")
	@NotBlank(message = RscConst.EX_MODEL_VERSION_BLANK)
	private String version;

	/**
	 * The confFileList field linked to model column
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "model")
	private List<ConfigurationFile> confFileList;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public String getApplication() {
		return application;
	}

	@SuppressWarnings("javadoc")
	public void setApplication(String application) {
		this.application = application;
	}

	@SuppressWarnings("javadoc")
	public String getContact() {
		return contact;
	}

	@SuppressWarnings("javadoc")
	public void setContact(String contact) {
		this.contact = contact;
	}

	@SuppressWarnings("javadoc")
	public String getVersionOrigin() {
		return this.versionOrigin;
	}

	@SuppressWarnings("javadoc")
	public void setVersionOrigin(String versionOrigin) {
		this.versionOrigin = versionOrigin;
	}

	@SuppressWarnings("javadoc")
	public String getVersion() {
		return this.version;
	}

	@SuppressWarnings("javadoc")
	public void setVersion(String version) {
		this.version = version;
	}

	@SuppressWarnings("javadoc")
	public List<ConfigurationFile> getConfFileList() {
		return confFileList;
	}

	@SuppressWarnings("javadoc")
	public void setConfFileList(List<ConfigurationFile> confFileList) {
		this.confFileList = confFileList;
	}

	/**
	 * @param feature the CF feature
	 * @return the configuration file associated to the feature
	 */
	public ConfigurationFile getConfFile(CFFeature feature) {
		Optional<ConfigurationFile> findFirst = Optional.empty();
		if (confFileList != null && feature != null) {

			// search for the latest (comparing dateImport) conf file with the same feature
			findFirst = confFileList.stream().filter(c -> feature.equals(c.getFeature()))
					.sorted(Comparator.comparing(ConfigurationFile::getDateImport).reversed()).findFirst();
		}
		return findFirst.isPresent() ? findFirst.get() : null;
	}

	@Override
	public String toString() {
		return "Model [id=" + id + ", application=" + application + ", contact=" + contact + ", versionOrigin=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ versionOrigin + ", version=" + version + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current Model
	 */
	public Model copy() {
		Model entity = new Model();
		entity.setApplication(getApplication());
		entity.setContact(getContact());
		entity.setVersion(getVersion());
		entity.setVersionOrigin(getVersionOrigin());
		return entity;
	}

}