/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import gov.sandia.cf.application.tools.GenericParameterTools;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionValue;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * 
 * JUnit test class for the Generic Parameter Application Controller
 * 
 * @author Didier Verstraete
 *
 */
class GenericParameterToolsTest extends AbstractTestApplication {

	/*******************************
	 * sortTableValuesByParameterId
	 *******************************/

	@Test
	void test_sortTableValuesByParameterId_Null() {

		Model model = TestEntityFactory.getNewModel(getDaoManager());
		Decision newDecision = TestEntityFactory.getNewDecision(getDaoManager(), model, null, null);
		DecisionParam newDecisionParam1 = TestEntityFactory.getNewDecisionParam(getDaoManager(), model, null);
		DecisionValue newDecisionValue1 = TestEntityFactory.getNewDecisionValue(getDaoManager(), newDecision,
				newDecisionParam1, null);
		DecisionParam newDecisionParam2 = TestEntityFactory.getNewDecisionParam(getDaoManager(), model, null);
		DecisionValue newDecisionValue2 = TestEntityFactory.getNewDecisionValue(getDaoManager(), newDecision,
				newDecisionParam2, null);
		DecisionParam newDecisionParam3 = TestEntityFactory.getNewDecisionParam(getDaoManager(), model, null);
		DecisionValue newDecisionValue3 = TestEntityFactory.getNewDecisionValue(getDaoManager(), newDecision,
				newDecisionParam3, null);

		List<IGenericTableValue> valuesSorted = GenericParameterTools
				.sortTableValuesByParameterId(Arrays.asList(newDecisionValue3, newDecisionValue1, newDecisionValue2));

		assertEquals(3, valuesSorted.size());
		assertEquals(newDecisionValue1, valuesSorted.get(0));
		assertEquals(newDecisionValue2, valuesSorted.get(1));
		assertEquals(newDecisionValue3, valuesSorted.get(2));
	}
}
