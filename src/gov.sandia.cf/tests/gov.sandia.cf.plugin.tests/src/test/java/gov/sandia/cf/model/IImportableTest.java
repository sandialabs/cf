/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

/**
 * 
 * The generic class to test the model class
 * 
 * @author Didier Verstraete
 *
 */
class IImportableTest {

	/**
	 * @return an instance of the MODEL
	 */
	IImportable<?> getModelInstance(Class<? extends IImportable> modelClass) {

		IImportable<?> instance = null;

		try {
			instance = modelClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			fail("Failed with " + modelClass + e.getMessage()); //$NON-NLS-1$
		}

		return instance;
	}

	Set<Class<? extends IImportable>> getListModelClass() {
		Reflections reflections = new Reflections("gov.sandia.cf.model"); //$NON-NLS-1$
		return reflections.getSubTypesOf(IImportable.class).stream().filter(e -> !Modifier.isAbstract(e.getModifiers()))
				.collect(Collectors.toSet());
	}

	@Test
	void testSameAs() {
		for (Class<? extends IImportable> modelClass : getListModelClass()) {
			IImportable modelInstance = getModelInstance(modelClass);
			assertTrue("Failed with " + modelClass, modelInstance.sameAs(modelInstance)); //$NON-NLS-1$
		}
	}

}
