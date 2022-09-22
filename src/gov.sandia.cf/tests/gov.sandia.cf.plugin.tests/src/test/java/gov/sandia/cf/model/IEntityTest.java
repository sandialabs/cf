/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import gov.sandia.cf.tools.MathTools;

/**
 * 
 * The generic class to test the model class
 * 
 * @author Didier Verstraete
 */
class IEntityTest {

	/**
	 * Gets the model instance.
	 *
	 * @param modelClass the model class
	 * @return an instance of the MODEL
	 */
	IEntity<?, ?> getModelInstance(Class<? extends IEntity> modelClass) {

		IEntity<?, ?> instance = null;

		try {
			instance = modelClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			fail("Failed with " + modelClass + e.getMessage()); //$NON-NLS-1$
		}

		return instance;
	}

	/**
	 * @return a test value for the id
	 */
	Integer getIdTestValue() {
		return MathTools.getRandomIntBase10();
	}

	void compareFields(IEntity<?, ?> expected, IEntity<?, ?> actual) {

		for (Field field : actual.getClass().getDeclaredFields()) {

			// check only strings
			if (!Modifier.isFinal(field.getModifiers())) {
				PropertyDescriptor pd;
				try {
					pd = new PropertyDescriptor(field.getName(), expected.getClass());
					if (pd.getReadMethod().isAccessible()) {
						assertEquals(pd.getReadMethod().invoke(expected), pd.getReadMethod().invoke(actual));
					}
				} catch (IntrospectionException e) {
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					fail(e.getMessage());
				}
			}
		}
	}

	Set<Class<? extends IEntity>> getListModelClass() {
		Reflections reflections = new Reflections("gov.sandia.cf.model"); //$NON-NLS-1$
		return reflections.getSubTypesOf(IEntity.class).stream().filter(e -> !Modifier.isAbstract(e.getModifiers()))
				.collect(Collectors.toSet());
	}

	@Test
	void testToString() {
		for (Class<? extends IEntity> modelClass : getListModelClass()) {
			IEntity<?, ?> modelInstance = getModelInstance(modelClass);
			String string = modelInstance.toString();
			assertNotNull("Failed with " + modelClass, string); //$NON-NLS-1$
		}
	}

	@Test
	void testCopy() {
		for (Class<? extends IEntity> modelClass : getListModelClass()) {
			IEntity<?, ?> modelInstance = getModelInstance(modelClass);
			IEntity<?, ?> modelCopied = (IEntity<?, ?>) modelInstance.copy();
			compareFields(modelCopied, modelInstance);
		}
	}

	@Test
	void testSetId() {
		for (Class<? extends IEntity> modelClass : getListModelClass()) {
			IEntity modelInstance = getModelInstance(modelClass);
			Integer idTest = getIdTestValue();
			modelInstance.setId(idTest);
			assertSame("Failed with " + modelClass, modelInstance.getId(), idTest); //$NON-NLS-1$
		}
	}

}
