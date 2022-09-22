/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import gov.sandia.cf.model.dto.configuration.PIRTQuery;
import gov.sandia.cf.parts.ui.AViewController;

/**
 * PIRT QoI view controller: Used to control the PIRT QoI view.
 *
 * @author Didier Verstraete
 * @param <T> the generic type
 */
public class PIRTQueryResultViewController<T> extends AViewController<PIRTViewManager, PIRTQueryResultView<T>> {

	/** the pirt query. */
	private PIRTQuery pirtQuery;

	/** the pirt query result. */
	private List<T> queryResult;

	/**
	 * Instantiates a new PIRT qo I view controller.
	 *
	 * @param viewManager the view manager
	 * @param parent      the parent
	 * @param pirtQuery   the pirt query
	 * @param queryResult the query result
	 */
	PIRTQueryResultViewController(PIRTViewManager viewManager, Composite parent, PIRTQuery pirtQuery,
			List<T> queryResult) {
		super(viewManager);

		this.pirtQuery = pirtQuery;
		this.queryResult = queryResult;

		super.setView(new PIRTQueryResultView<>(this, parent, SWT.NONE));
	}

	/**
	 * Reload data.
	 */
	void reloadData() {
		if (queryResult != null && !queryResult.isEmpty()) {
			getView().setTableData(queryResult);
		}
		getView().refreshViewer();
	}

	/**
	 * Gets the pirt query.
	 *
	 * @return the pirt query
	 */
	public PIRTQuery getPirtQuery() {
		return pirtQuery;
	}

	/**
	 * Gets the query result.
	 *
	 * @return the query result
	 */
	public List<T> getQueryResult() {
		return queryResult;
	}

}
