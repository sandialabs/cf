/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The Uncertainty entity class linked to table COM_UNCERTAINTY
 * 
 * @author Maxime N.
 *
 */
@Entity
@Table(name = "COM_UNCERTAINTY")
public class Uncertainty implements Serializable, IGenericTableItem, IEntity<Uncertainty, Integer> {

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
		USERCREATION("userCreation"), //$NON-NLS-1$
		GROUP("group"); //$NON-NLS-1$

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
	 * The userCreation field linked to USER_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_CREATION_ID")
	@NotNull(message = RscConst.EX_UNCERTAINTY_USERCREATION_NULL)
	private User userCreation;

	/**
	 * The group field linked to GROUP_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GROUP_ID")
	@NotNull(message = RscConst.EX_UNCERTAINTY_GROUP_NULL)
	private UncertaintyGroup group;

	/**
	 * The uncertaintyParameterList field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "uncertainty", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<UncertaintyValue> uncertaintyParameterList = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public User getUserCreation() {
		return userCreation;
	}

	@SuppressWarnings("javadoc")
	public void setUserCreation(User userCreation) {
		this.userCreation = userCreation;
	}

	@SuppressWarnings("javadoc")
	public UncertaintyGroup getGroup() {
		return group;
	}

	@SuppressWarnings("javadoc")
	public void setGroup(UncertaintyGroup group) {
		this.group = group;
	}

	@Override
	public List<IGenericTableValue> getValueList() {
		return uncertaintyParameterList.stream().map(IGenericTableValue.class::cast).collect(Collectors.toList());
	}

	@SuppressWarnings("javadoc")
	public List<UncertaintyValue> getUncertaintyParameterList() {
		return uncertaintyParameterList;
	}

	@SuppressWarnings("javadoc")
	public void setUncertaintyParameterList(List<UncertaintyValue> uncertaintyParameterList) {
		this.uncertaintyParameterList = uncertaintyParameterList;
	}

	@Override
	public String toString() {
		return "Uncertainty [" + "id=" + (id != null ? id.toString() : "") + RscTools.COMMA + "userCreation=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (userCreation != null ? userCreation.toString() : "") + "]" //$NON-NLS-1$ //$NON-NLS-2$
				+ (group != null ? group.toString() : "") + "]" //$NON-NLS-1$ //$NON-NLS-2$
				+ (uncertaintyParameterList != null ? uncertaintyParameterList.toString() : "") + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current entity
	 */
	public Uncertainty copy() {
		return new Uncertainty();
	}
}