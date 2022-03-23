/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;

/**
 * 
 * The PCMM phases
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PCMM_OPTION")
public class PCMMOption implements IImportable<PCMMOption>, Serializable, IEntity<PCMMOption, Integer> {

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
		PHASE("phase"); //$NON-NLS-1$

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
	 * the name
	 */
	@Column(name = "PHASE")
	@NotNull(message = RscConst.EX_PCMMOPTION_PHASE_NULL)
	@Enumerated(EnumType.STRING)
	private PCMMPhase phase;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public PCMMPhase getPhase() {
		return phase;
	}

	@SuppressWarnings("javadoc")
	public void setPhase(PCMMPhase phase) {
		this.phase = phase;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameKey(PCMMOption newPhase) {
		return newPhase != null && Objects.equals(getPhase(), newPhase.getPhase());
	}

	@Override
	public boolean sameAs(PCMMOption newPhase) {
		if (newPhase == null) {
			return false;
		}

		return (getPhase() == null && newPhase.getPhase() == null)
				|| (getPhase() != null && getPhase().equals(newPhase.getPhase()));
	}

	@Override
	public String getAbstract() {
		return getPhase() != null ? getPhase().getName() : ""; //$NON-NLS-1$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current PCMMOption
	 */
	public PCMMOption copy() {
		PCMMOption entity = new PCMMOption();
		entity.setPhase(getPhase());
		return entity;
	}
}
