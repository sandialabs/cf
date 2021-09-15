/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.comparator;

import java.io.Serializable;
import java.util.Comparator;

import gov.sandia.cf.model.QuantityOfInterest;

/**
 * Qoi Tagged comparator by date tag.
 * 
 * This comparator sorts by the chronological date.
 * 
 * @author Maxime N.
 *
 */
public class QoiTaggedComparator implements Comparator<QuantityOfInterest>, Serializable {

	private static final long serialVersionUID = 7279774197268612200L;

	/**s
	 * {@inheritDoc}
	 */
	@Override
	public int compare(QuantityOfInterest o1, QuantityOfInterest o2) {

		int compare = 0;

		if (o1 == null && o2 == null)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;

		if (o1.getTagDate() == null) {
			compare = (o2.getTagDate() == null) ? 0 : -1;
		} else {
			compare = (o2.getTagDate() == null) ? 1 : o1.getTagDate().compareTo(o2.getTagDate());
		}

		return compare;
	}

}
