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

import gov.sandia.cf.model.query.EntityFilter;

/**
 * The GlobalConfiguration entity class linked to table GLOBAL_CONFIGURATION
 * 
 * @author Didier Verstraete
 */
@Entity
@Table(name = "GLOBAL_CONFIGURATION")
public class GlobalConfiguration implements Serializable, IEntity<GlobalConfiguration, Integer> {

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
		OPEN_LINK_BROWSER_OPTS("openLinkBrowserOpts"); //$NON-NLS-1$

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
	 * The openLinkBrowserOpts field linked to OPEN_LINK_BROWSER_OPTS column
	 */
	@Column(name = "OPEN_LINK_BROWSER_OPTS")
	private String openLinkBrowserOpts = OpenLinkBrowserOption.CF_PREFERENCE.name();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public String getOpenLinkBrowserOpts() {
		return openLinkBrowserOpts;
	}

	@SuppressWarnings("javadoc")
	public void setOpenLinkBrowserOpts(String openLinkBrowserOpts) {
		this.openLinkBrowserOpts = openLinkBrowserOpts;
	}

	@Override
	public String toString() {
		return "GlobalConfiguration [id=" + id + ", openLinkBrowserOpts=" + openLinkBrowserOpts + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current GlobalConfiguration
	 */
	public GlobalConfiguration copy() {
		GlobalConfiguration entity = new GlobalConfiguration();
		entity.setOpenLinkBrowserOpts(getOpenLinkBrowserOpts());
		return entity;
	}

}