/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.dto.configuration.PIRTQuery;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit test class for the PIRT Application Controller
 */
@RunWith(JUnitPlatform.class)
class PIRTApplicationQueryTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PIRTApplicationQueryTest.class);

	@Test
	void testQueryCRUDWorking() {

		try {

			// create criterion
			Criterion criterion = TestEntityFactory.getNewCriterion(getDaoManager(), null);
			assertNotNull(criterion);

			// Create Query
			PIRTQuery query = new PIRTQuery();
			query.setQuery("select c.* from criterion c"); //$NON-NLS-1$
			query.setResultType("Criterion"); //$NON-NLS-1$

			// Create Input list
			List<String> criteriaInputList = null;
			List<String> criteriaList = null;

			// Check criteria list to null
			query.setCriteriaList(criteriaList);
			List<Object> res = getAppManager().getService(IPIRTApplication.class).executeQuery(query,
					criteriaInputList);
			assertNotNull(res);

			// Check criteria list empty
			criteriaList = new ArrayList<String>();
			query.setCriteriaList(criteriaList);
			res = getAppManager().getService(IPIRTApplication.class).executeQuery(query, criteriaInputList);
			assertNotNull(res);

			// Check criteria input list null
			criteriaList.add("name"); //$NON-NLS-1$
			query.setCriteriaList(criteriaList);
			res = getAppManager().getService(IPIRTApplication.class).executeQuery(query, criteriaInputList);
			assertNotNull(res);

			// Check criteria input list empty
			criteriaInputList = new ArrayList<String>();
			query.setCriteriaList(criteriaList);
			res = getAppManager().getService(IPIRTApplication.class).executeQuery(query, criteriaInputList);
			assertNotNull(res);

			// All Good 1 result
			query.setQuery("select * from criterion c where name = {0}"); //$NON-NLS-1$
			criteriaInputList.add("'NAME'"); //$NON-NLS-1$
			res = getAppManager().getService(IPIRTApplication.class).executeQuery(query, criteriaInputList);
			assertNotNull(res);
			assertEquals(1, res.size());

			// All Good no result
			criteriaInputList = new ArrayList<String>();
			criteriaInputList.add("'NAMEF'"); //$NON-NLS-1$
			res = getAppManager().getService(IPIRTApplication.class).executeQuery(query, criteriaInputList);
			assertNotNull(res);
			assertTrue(res.isEmpty());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

	}
}
