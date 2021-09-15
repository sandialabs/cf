/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.List;

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
 * The Role
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "ROLE")
public class Role implements Serializable, IEntity<Role, Integer>, IImportable<Role> {

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
		NAME("name"); //$NON-NLS-1$

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
	@NotBlank(message = RscConst.EX_ROLE_NAME_BLANK)
	private String name;

	/**
	 * The userList field linked to userList column
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rolePCMM")
	private List<User> userList;

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

	@Override
	public boolean sameAs(Role newImportable) {
		return newImportable != null && StringTools.equals(getName(), newImportable.getName());
	}

	@Override
	public String getAbstract() {
		return getName();
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current Role
	 */
	@Override
	public Role copy() {
		Role entity = new Role();
		entity.setName(getName());
		return entity;
	}

}
