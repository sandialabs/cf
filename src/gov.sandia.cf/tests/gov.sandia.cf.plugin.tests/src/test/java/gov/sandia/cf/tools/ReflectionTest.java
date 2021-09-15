/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.Id;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.model.Model;

@RunWith(JUnitPlatform.class)
class ReflectionTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(ReflectionTest.class);

	@Test
	void testReflection() throws IntrospectionException, IllegalAccessException, InvocationTargetException {

		Model model = new Model();
		model.setId(1);
		model.setApplication("myName");//$NON-NLS-1$
		model.setContact("uri");//$NON-NLS-1$

		for (Field field : model.getClass().getDeclaredFields()) {

			if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
				Class<?> type = field.getType();
				String name = field.getName();

				// print fields name and type and has @javax.persistence.Id annotation
				logger.info("{} : {} : {}", type, name, field.isAnnotationPresent(Id.class));//$NON-NLS-1$

				PropertyDescriptor pd;
				try {
					pd = new PropertyDescriptor(name, model.getClass());

					// print fields getter value
					logger.info("{}", pd.getReadMethod().invoke(model));//$NON-NLS-1$

				} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Test
	void testUpdateFieldReflection() {

		// first model to copy
		Model entityUpdated = new Model();
		entityUpdated.setId(2);
		entityUpdated.setApplication("myNameUpdated");//$NON-NLS-1$
		entityUpdated.setContact("uriUpdated");//$NON-NLS-1$

		// second model to update
		Model entityToUpdate = new Model();
		entityToUpdate.setId(1);
		entityToUpdate.setApplication("myName");//$NON-NLS-1$
		entityToUpdate.setContact("uri");//$NON-NLS-1$

		assertNotEquals(entityUpdated.getId(), entityToUpdate.getId());
		assertNotEquals(entityUpdated.getApplication(), entityToUpdate.getApplication());
		assertNotEquals(entityUpdated.getContact(), entityToUpdate.getContact());

		for (Field field : entityUpdated.getClass().getDeclaredFields()) {

			// ignore static fields
			if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
				PropertyDescriptor pdEntityUpdated;
				PropertyDescriptor pdObjectToUpdate;
				try {
					pdEntityUpdated = new PropertyDescriptor(field.getName(), entityUpdated.getClass());
					pdObjectToUpdate = new PropertyDescriptor(field.getName(), entityToUpdate.getClass());
					// set entityToUpdate with entityUpdated values
					pdObjectToUpdate.getWriteMethod().invoke(entityToUpdate,
							pdEntityUpdated.getReadMethod().invoke(entityUpdated));
				} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		assertEquals(entityUpdated.getId(), entityToUpdate.getId());
		assertEquals(entityUpdated.getApplication(), entityToUpdate.getApplication());
		assertEquals(entityUpdated.getContact(), entityToUpdate.getContact());
	}

	@Test
	void testClassNameReflection() {

		Model entity = new Model();
		entity.setId(1);
		entity.setApplication("myName");//$NON-NLS-1$
		entity.setContact("uri");//$NON-NLS-1$

		logger.info(entity.getClass().getSimpleName());
	}

}
