/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * The Class ELToolsTest.
 *
 * @author Didier Verstraete
 */
class ELToolsTest {

	/* ************** eval *********** */

	@Test
	void test_eval_String() {
		String characterization = "characterized"; //$NON-NLS-1$
		String eval = "Characterization != 'not characterized'"; //$NON-NLS-1$
		Map<String, String> attributes = Collections.singletonMap("Characterization", characterization); //$NON-NLS-1$

		Boolean result = ELTools.eval(Boolean.class, eval, attributes);
		assertTrue(result);
	}

	@Test
	void test_eval_Int() {
		Integer characterization = 7;
		String eval = "Characterization > 6"; //$NON-NLS-1$
		Map<String, Integer> attributes = Collections.singletonMap("Characterization", characterization); //$NON-NLS-1$

		Boolean result = ELTools.eval(Boolean.class, eval, attributes);
		assertTrue(result);
	}

	@Test
	void test_eval_Sum() {
		Integer characterization = 7;
		String eval = "Characterization + 6"; //$NON-NLS-1$
		Map<String, Integer> attributes = Collections.singletonMap("Characterization", characterization); //$NON-NLS-1$

		Long result = ELTools.eval(Long.class, eval, attributes);
		assertEquals(Long.valueOf(13), result);
	}

	@Test
	void test_eval_EmptyFalse() {
		String characterization = "characterized"; //$NON-NLS-1$
		String eval = "empty Characterization"; //$NON-NLS-1$
		Map<String, String> attributes = Collections.singletonMap("Characterization", characterization); //$NON-NLS-1$

		Boolean result = ELTools.eval(Boolean.class, eval, attributes);
		assertFalse(result);
	}

	@Test
	void test_eval_EmptyTrue() {
		String characterization = ""; //$NON-NLS-1$
		String eval = "empty Characterization"; //$NON-NLS-1$
		Map<String, String> attributes = Collections.singletonMap("Characterization", characterization); //$NON-NLS-1$

		Boolean result = ELTools.eval(Boolean.class, eval, attributes);
		assertTrue(result);
	}

	@Test
	void test_eval_EmptyWithSpace() {
		String characterization = " "; //$NON-NLS-1$
		String eval = "empty Characterization"; //$NON-NLS-1$
		Map<String, String> attributes = Collections.singletonMap("Characterization", characterization); //$NON-NLS-1$

		Boolean result = ELTools.eval(Boolean.class, eval, attributes);
		assertFalse(result);
	}

	@Test
	void test_eval_EmptyNull() {
		String characterization = null;
		String eval = "empty Characterization"; //$NON-NLS-1$
		Map<String, String> attributes = Collections.singletonMap("Characterization", characterization); //$NON-NLS-1$

		Boolean result = ELTools.eval(Boolean.class, eval, attributes);
		assertTrue(result);
	}

	/* ************** parse *********** */

	@Test
	void test_parseEL() {
		String eval = "empty Upper Limit"; //$NON-NLS-1$

		List<String> variable = Arrays.asList("Test", "Upper Limit"); //$NON-NLS-1$ //$NON-NLS-2$

		Set<String> found = ELTools.getVariableSet(eval, variable);

		assertNotNull(found);
		assertEquals(1, found.size());
		assertEquals("Upper Limit", found.iterator().next()); //$NON-NLS-1$
	}
}
