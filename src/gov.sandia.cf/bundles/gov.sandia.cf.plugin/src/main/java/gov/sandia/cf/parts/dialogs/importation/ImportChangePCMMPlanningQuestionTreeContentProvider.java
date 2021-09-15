/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.dialogs.importation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMSubelement;

/**
 * The import changes PCMM Planning Questions tree content provider.
 * 
 * @author Didier Verstraete
 *
 */
public class ImportChangePCMMPlanningQuestionTreeContentProvider extends ImportChangeTreeContentProvider {

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof Map || element instanceof Entry;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof PCMMPlanningQuestion) {
			PCMMElement pcmmElt = ((PCMMPlanningQuestion) element).getElement();
			PCMMSubelement pcmmSubelt = ((PCMMPlanningQuestion) element).getSubelement();
			if (pcmmElt != null) {
				return pcmmElt;
			} else if (pcmmSubelt != null) {
				return pcmmSubelt;
			}
		}
		return null;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List<?>) {
			Map<String, Map<String, List<PCMMPlanningQuestion>>> elements = new TreeMap<>();
			elements.put(ALL, new TreeMap<>());

			for (Object value : (List<?>) inputElement) {
				if (value instanceof PCMMPlanningQuestion) {

					// get assessable
					String assessableKey = getAssessableKey((PCMMPlanningQuestion) value);

					if (assessableKey != null) {

						// add assessable parent entry
						if (!elements.get(ALL).containsKey(assessableKey)) {
							elements.get(ALL).put(assessableKey, new ArrayList<>());
						}

						// add question value to this assessable
						elements.get(ALL).get(assessableKey).add((PCMMPlanningQuestion) value);
					}
				}
			}
			return new Object[] { elements };
		}
		return new Object[0];
	}

	/**
	 * @param question the pcmm planning question
	 * @return the assessable key as a concatenation of code and name
	 */
	private String getAssessableKey(PCMMPlanningQuestion question) {

		// get assessable
		String assessableKey = null;
		PCMMElement pcmmElt = question.getElement();
		PCMMSubelement pcmmSubelt = question.getSubelement();
		if (pcmmElt != null) {
			assessableKey = pcmmElt.getAbbreviation() + " - " + pcmmElt.getName(); //$NON-NLS-1$
		} else if (pcmmSubelt != null) {
			assessableKey = pcmmSubelt.getCode() + " - " + pcmmSubelt.getName(); //$NON-NLS-1$
		}

		return assessableKey;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Map && ((Map<?, ?>) parentElement).containsKey(ALL)) {
			Object values = ((Map<?, ?>) parentElement).get(ALL);
			if (values instanceof Map && !((Map<?, ?>) values).isEmpty()) {
				return ((Map<?, ?>) values).entrySet().toArray();
			}
		} else if (parentElement instanceof Entry && ((Entry<?, ?>) parentElement).getValue() instanceof List) {
			return ((List<?>) ((Entry<?, ?>) parentElement).getValue()).toArray();
		}
		return new Object[0];
	}

}
