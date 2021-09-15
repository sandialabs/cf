/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.List;

/**
 * The PCMM Assessment
 * 
 * @author Didier Verstraete
 *
 * @param <T> the assessable entity type
 */
public class PCMMAggregation<T extends IAssessable> implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * All the user comments
	 */
	private List<String> commentList;

	/**
	 * The level computed
	 */
	private PCMMAggregationLevel level;

	/**
	 * The item to assess
	 */
	private T item;

	/**
	 * @return the list of comments
	 */
	public List<String> getCommentList() {
		return commentList;
	}

	/**
	 * Set the list of comments
	 * 
	 * @param commentList the aggregation comment list
	 */
	public void setCommentList(List<String> commentList) {
		this.commentList = commentList;
	}

	/**
	 * @return the level aggregated
	 */
	public PCMMAggregationLevel getLevel() {
		return level;
	}

	/**
	 * Set the level
	 * 
	 * @param level the aggregation level
	 */
	public void setLevel(PCMMAggregationLevel level) {
		this.level = level;
	}

	/**
	 * @return the item to assess
	 */
	public T getItem() {
		return item;
	}

	/**
	 * Set the item to assess
	 * 
	 * @param item the entity item
	 */
	public void setItem(T item) {
		this.item = item;
	}

}
