/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import gov.sandia.cf.model.query.EntityFilter;

/**
 * The GenericValueTaggable entity class. Generic Value that can be tagged.
 * 
 * @author Didier Verstraete
 * @param <P> The generic parameter inherited class
 * @param <E> The entity class
 *
 */
@MappedSuperclass
public abstract class GenericValueTaggable<P extends GenericParameter<P>, E> extends GenericValue<P, E> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Field Filter
	 */
	@SuppressWarnings("javadoc")
	public enum Filter implements EntityFilter {
		TAG("tag"); //$NON-NLS-1$

		private String field;

		/**
		 * Filter
		 * 
		 * @param field
		 */
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
	 * The tag field linked to TAG_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "TAG_ID")
	private Tag tag;

	@SuppressWarnings("javadoc")
	public Tag getTag() {
		return tag;
	}

	@SuppressWarnings("javadoc")
	public void setTag(Tag tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return "GenericValueTaggable [tag=" + tag + ", [" + super.toString() + "]]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

}