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

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;

/**
 * The User
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "USER")
public class User implements Serializable, IEntity<User, Integer> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The unknown user id
	 */
	public static final String UNKNOWN_USERID = "45bfa2ab4edb7035d49c37cd772d6d43"; //$NON-NLS-1$

	/**
	 * Field Filter
	 */
	@SuppressWarnings("javadoc")
	public enum Filter implements EntityFilter {
		ID("id"), //$NON-NLS-1$
		USERID("userID"), //$NON-NLS-1$
		ROLE_PCMM("rolePCMM"); //$NON-NLS-1$

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
	 * The userID field linked to USERID column
	 */
	@Column(name = "USERID")
	@NotBlank(message = RscConst.EX_USER_USERID_BLANK)
	private String userID;

	/**
	 * The rolePCMM field linked to ROLE_PCMM_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CURRENT_ROLE_ID")
	private Role rolePCMM;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public String getUserID() {
		return userID;
	}

	@SuppressWarnings("javadoc")
	public void setUserID(String userID) {
		this.userID = userID;
	}

	@SuppressWarnings("javadoc")
	public Role getRolePCMM() {
		return rolePCMM;
	}

	@SuppressWarnings("javadoc")
	public void setRolePCMM(Role rolePCMM) {
		this.rolePCMM = rolePCMM;
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current User
	 */
	@Override
	public User copy() {
		User user = new User();
		user.setUserID(getUserID());
		return user;
	}

	@Override
	public String toString() {
		return "User [userID=" + userID + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
