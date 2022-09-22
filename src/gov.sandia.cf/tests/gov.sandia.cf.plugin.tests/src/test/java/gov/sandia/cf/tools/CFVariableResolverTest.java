/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import gov.sandia.cf.constants.CFVariable;
import gov.sandia.cf.exceptions.CredibilityException;

/**
 * The Class CFVariableResolverTest.
 *
 * @author Didier Verstraete
 */
class CFVariableResolverTest {

	@Test
	void test_resolveAll_systemVar() throws CredibilityException {
		String toResolve = Arrays
				.stream(new CFVariable[] { CFVariable.WORKSPACE, CFVariable.HOSTNAME, CFVariable.JAVA_VERSION,
						CFVariable.OS_NAME, CFVariable.USER_NAME, CFVariable.USER_HOME })
				.map(CFVariable::get).collect(Collectors.joining("bla")); //$NON-NLS-1$
		String resolved = CFVariableResolver.resolveAll(toResolve);

		// test
		for (CFVariable var : CFVariable.values()) {
			assertFalse(resolved.contains(var.get()));
		}
	}

//	@Test
//	void test_resolveAll() throws CredibilityException {
	// TODO solve all variables, system and path
//	}

	@Test
	void test_resolveAll_with_variable_not_recognized() {
		String toResolve = "bla ${MY_VAR_UNRECOGNIZED} test"; //$NON-NLS-1$
		CredibilityException exception = assertThrows(CredibilityException.class, () -> {
			CFVariableResolver.resolveAll(toResolve);
		});
		assertEquals(RscTools.getString(RscConst.EX_CFVARRESOLVER_VAR_NOTRECOGNIZED, "${MY_VAR_UNRECOGNIZED}"), //$NON-NLS-1$
				exception.getMessage());
	}

	@Test
	void test_removeAll() {
		String toRemove = Arrays.stream(CFVariable.values()).map(CFVariable::get).collect(Collectors.joining("bla")); //$NON-NLS-1$
		String removed = CFVariableResolver.removeAll(toRemove);

		// test
		for (CFVariable var : CFVariable.values()) {
			assertFalse(removed.contains(var.get()));
		}
	}

//	@Test
//	void test_resolve_CF_FILEDIR() {
	// TODO to implement with SWTBot
//	}

//	@Test
//	void test_resolve_CF_FILENAME() {
	// TODO to implement with SWTBot
//	}

//	@Test
//	void test_resolve_CF_HOMEDIR() {
	// TODO to implement with SWTBot
//	}

//	@Test
//	void test_resolve_CF_WORKDIR() {
	// TODO to implement with SWTBot
//	}

//	@Test
//	void test_resolve_PROJECT() {
	// TODO to implement with SWTBot
//	}

	@Test
	void test_resolve_WORKSPACE() throws CredibilityException {
		assertNotNull(CFVariableResolver.resolve(CFVariable.WORKSPACE));
	}

	@Test
	void test_resolve_HOSTNAME() throws CredibilityException {
		assertNotNull(CFVariableResolver.resolve(CFVariable.HOSTNAME));
	}

	@Test
	void test_resolve_JAVA_VERSION() throws CredibilityException {
		assertNotNull(CFVariableResolver.resolve(CFVariable.JAVA_VERSION));
	}

	@Test
	void test_resolve_OS_NAME() throws CredibilityException {
		assertNotNull(CFVariableResolver.resolve(CFVariable.OS_NAME));
	}

	@Test
	void test_resolve_USER_NAME() throws CredibilityException {
		assertNotNull(CFVariableResolver.resolve(CFVariable.USER_NAME));
	}

	@Test
	void test_resolve_USER_HOME() throws CredibilityException {
		assertNotNull(CFVariableResolver.resolve(CFVariable.USER_HOME));
	}

	@Test
	void test_resolve_Null() {
		CredibilityException exception = assertThrows(CredibilityException.class, () -> {
			CFVariableResolver.resolve(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_CFVARRESOLVER_VAR_NULL), exception.getMessage());
	}
}
