/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.pirt;

import java.util.List;

/**
 * Define a PIRT Query loaded by configuration
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTQuery {

	/**
	 * The PIRT Query id
	 */
	private String id;
	/**
	 * The PIRT Query name
	 */
	private String name;
	/**
	 * The PIRT Query executable query
	 */
	private String query;
	/**
	 * The PIRT Query result type
	 */
	private String resultType;
	/**
	 * The criteria list
	 */
	private List<String> criteriaList;

	/**
	 * Constructor
	 */
	public PIRTQuery() {
	}

	/**
	 * Constructor
	 * 
	 * @param id           the query id
	 * @param name         the query name
	 * @param query        the executable SQL query
	 * @param resultType   the result type
	 * @param criteriaList the criteria list
	 */
	public PIRTQuery(String id, String name, String query, String resultType, List<String> criteriaList) {
		this.id = id;
		this.name = name;
		this.query = query;
		this.resultType = resultType;
		this.criteriaList = criteriaList;
	}

	@SuppressWarnings("javadoc")
	public String getId() {
		return id;
	}

	@SuppressWarnings("javadoc")
	public void setId(String id) {
		this.id = id;
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
	public String getQuery() {
		return query;
	}

	@SuppressWarnings("javadoc")
	public void setQuery(String query) {
		this.query = query;
	}

	@SuppressWarnings("javadoc")
	public String getResultType() {
		return resultType;
	}

	@SuppressWarnings("javadoc")
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	@SuppressWarnings("javadoc")
	public List<String> getCriteriaList() {
		return criteriaList;
	}

	@SuppressWarnings("javadoc")
	public void setCriteriaList(List<String> criteriaList) {
		this.criteriaList = criteriaList;
	}

}
