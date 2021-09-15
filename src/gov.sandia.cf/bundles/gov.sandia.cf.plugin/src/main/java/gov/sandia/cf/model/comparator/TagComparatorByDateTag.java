/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.comparator;

import java.io.Serializable;
import java.util.Comparator;

import gov.sandia.cf.model.Tag;

/**
 * Tag comparator by date tag.
 * 
 * This comparator sorts by the chronological date.
 * 
 * @author Didier Verstraete
 *
 */
public class TagComparatorByDateTag implements Comparator<Tag>, Serializable {

	private static final long serialVersionUID = 7279774197268612200L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Tag o1, Tag o2) {

		int compare = 0;

		if (o1 == null && o2 == null)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;

		if (o1.getDateTag() == null) {
			compare = (o2.getDateTag() == null) ? 0 : -1;
		} else {
			compare = (o2.getDateTag() == null) ? 1 : o1.getDateTag().compareTo(o2.getDateTag());
		}

		return compare;
	}

}
