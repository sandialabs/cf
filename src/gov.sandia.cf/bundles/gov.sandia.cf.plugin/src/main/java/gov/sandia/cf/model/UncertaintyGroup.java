/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.List;

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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The UncertaintyGroup entity class linked to table COM_UNCERTAINTY_GROUP
 * 
 * @author Maxime N.
 *
 */
@Entity
@Table(name = "COM_UNCERTAINTY_GROUP")
public class UncertaintyGroup implements Serializable, IEntity<UncertaintyGroup, Integer>, ISelectValue {

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
		NAME("name"), //$NON-NLS-1$
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
	 * The name field linked to NAME column
	 */
	@Column(name = "NAME")
	@NotBlank(message = RscConst.EX_UNCERTAINTYGROUP_NAME_BLANK)
	private String name;

	/**
	 * The parameterValueList field
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "group", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Uncertainty> uncertainties;

	/**
	 * The model field linked to MODEL_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	@NotNull(message = RscConst.EX_UNCERTAINTYGROUP_MODEL_NULL)
	private Model model;

	/**
	 * The userCreation field linked to USER_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_CREATION_ID")
	private User userCreation;

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
	public String getName() {
		return name;
	}

	@SuppressWarnings("javadoc")
	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("javadoc")
	public List<Uncertainty> getUncertainties() {
		return uncertainties;
	}

	@SuppressWarnings("javadoc")
	public void setUncertainties(List<Uncertainty> uncertainties) {
		this.uncertainties = uncertainties;
	}

	@SuppressWarnings("javadoc")
	public Model getModel() {
		return model;
	}

	@SuppressWarnings("javadoc")
	public void setModel(Model model) {
		this.model = model;
	}

	@Override
	public String getSelectName() {
		return getName();
	}

	@Override
	public String toString() {
		return "UncertaintyGroup [" + "id=" + (id != null ? id.toString() : "") + RscTools.COMMA + "userCreation=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (userCreation != null ? userCreation.toString() : "") + "]" + RscTools.COMMA + "name=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ (name != null ? name : "") + RscTools.COMMA + "value="; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current entity
	 */
	public UncertaintyGroup copy() {
		UncertaintyGroup entity = new UncertaintyGroup();
		entity.setName(getName());
		return entity;
	}

}