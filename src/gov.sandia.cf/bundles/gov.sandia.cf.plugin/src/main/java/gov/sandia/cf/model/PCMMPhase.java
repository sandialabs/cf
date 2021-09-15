/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * The PCMM phases
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public enum PCMMPhase {

	EVIDENCE("Evidence"), //$NON-NLS-1$
	ASSESS("Assess"), //$NON-NLS-1$
	AGGREGATE("Aggregate"), //$NON-NLS-1$
	STAMP("Stamp"), //$NON-NLS-1$
	PLANNING("Planning"); //$NON-NLS-1$

	/**
	 * the name
	 */
	private String name;

	/**
	 * @param name
	 */
	private PCMMPhase(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * @return the phases as array
	 */
	public static String[] getPhases() {
		String[] phasesArray = new String[] {};
		List<String> phases = new ArrayList<>();

		for (PCMMPhase phase : PCMMPhase.values()) {
			phases.add(phase.getName());
		}

		return phases.toArray(phasesArray);
	}

	/**
	 * @param name the phase name
	 * @return a new PCMM Phase for the given parameter
	 */
	public static PCMMPhase getPhaseFromName(String name) {
		for (PCMMPhase enumPhase : PCMMPhase.values()) {
			if (enumPhase.name != null && enumPhase.name.equals(name)) {
				return enumPhase;
			}
		}
		return null;
	}

}
