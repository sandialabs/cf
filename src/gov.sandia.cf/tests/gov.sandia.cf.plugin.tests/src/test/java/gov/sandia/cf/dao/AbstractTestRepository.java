/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;

import org.junit.jupiter.api.Test;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IEntity;

/**
 * @author Didier Verstraete
 *
 *         Abstract DAO JUnit test class
 */
abstract class AbstractTestRepository<MODEL extends IEntity<MODEL, PK>, PK, REPOSITORYMGR extends AbstractCRUDRepository<MODEL, PK>>
		extends AbstractTestDao {

	/**
	 * @return the REPOSITORYMGR class
	 */
	abstract Class<REPOSITORYMGR> getRepositoryClass();

	/**
	 * @return the MODEL class
	 */
	abstract Class<MODEL> getModelClass();

	/**
	 * @param model the model to fulfill
	 * @return the MODEL fulfilled
	 */
	abstract MODEL getModelFulfilled(MODEL model);

	/**
	 * @return the repository manager
	 */
	REPOSITORYMGR getRepository() {
		REPOSITORYMGR instance = null;
		try {
			instance = getRepositoryClass().getDeclaredConstructor().newInstance();
			instance.setEntityManager(getDaoManager().getEntityManager());
		} catch (InstantiationException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (InvocationTargetException e) {
			fail(e.getMessage());
		} catch (NoSuchMethodException e) {
			fail(e.getMessage());
		} catch (SecurityException e) {
			fail(e.getMessage());
		}
		return instance;
	}

	/**
	 * Compare the strings of the expected and actual entities
	 * 
	 * @param expected
	 * @param actual
	 */
	void compareString(MODEL expected, MODEL actual) {

		for (Field field : actual.getClass().getDeclaredFields()) {

			// check only strings
			if (!Modifier.isFinal(field.getModifiers()) && field.getType().isAssignableFrom(String.class)) {
				PropertyDescriptor pd;
				try {
					pd = new PropertyDescriptor(field.getName(), getModelClass());
					assertEquals(pd.getReadMethod().invoke(expected), pd.getReadMethod().invoke(actual));
				} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					fail(e.getMessage());
				}
			}
		}
	}

	/**
	 * @return an instance of the MODEL
	 */
	MODEL getModelInstance() {

		MODEL instance = null;

		try {
			instance = getModelClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			fail(e.getMessage());
		}

		return instance;
	}

	/**
	 * @param model
	 * @return a model with string fields fulfilled with their field name
	 */
	MODEL fulfillModelStrings(MODEL model) {
		for (Field field : AbstractCRUDRepository.getAllFields(model.getClass())) {

			// assign only strings
			if (!Modifier.isFinal(field.getModifiers()) && field.getType().isAssignableFrom(String.class)) {
				PropertyDescriptor pd;
				try {
					pd = new PropertyDescriptor(field.getName(), getModelClass());
					pd.getWriteMethod().invoke(model, field.getName());
				} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					fail(e.getMessage());
				}
			}
		}

		return model;
	}

	@Test
	void testInstantiate_ConstructorNoParameter() {
		try {
			// constructor without argument
			REPOSITORYMGR instance = getRepositoryClass().getDeclaredConstructor().newInstance();
			instance.setEntityManager(getDaoManager().getEntityManager());
			assertNotNull(instance);
			assertEquals(getDaoManager().getEntityManager(), instance.getEntityManager());

		} catch (InstantiationException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (InvocationTargetException e) {
			fail(e.getMessage());
		} catch (NoSuchMethodException e) {
			fail(e.getMessage());
		} catch (SecurityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testInstantiate_ConstructorWithParameter() {
		try {

			// constructor with argument
			Class<?>[] cArg = new Class[1];
			cArg[0] = EntityManager.class;

			REPOSITORYMGR instance2 = getRepositoryClass().getDeclaredConstructor(cArg)
					.newInstance(getDaoManager().getEntityManager());
			assertNotNull(instance2);
			assertEquals(getDaoManager().getEntityManager(), instance2.getEntityManager());

		} catch (InstantiationException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (InvocationTargetException e) {
			fail(e.getMessage());
		} catch (NoSuchMethodException e) {
			fail(e.getMessage());
		} catch (SecurityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testCRUD() {

		// instantiate the model with Strings fulfilled
		MODEL toCreate = getModelFulfilled(getModelInstance());

		// create
		try {
			toCreate = getRepository().create(toCreate);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// select all
		List<MODEL> all = getRepository().findAll();
		assertNotNull(all);
		assertFalse(all.isEmpty());
		assertEquals(MessageFormat.format("The size is: {0} and should be 1.", all.size()), 1, all.size()); //$NON-NLS-1$
		MODEL first = all.get(0);
		compareString(first, toCreate);

		// findbyid
		MODEL firstFound = getRepository().findById(first.getId());
		assertNotNull(firstFound);
		assertSame(firstFound, first);

		// refresh
		getRepository().refresh(firstFound);

		// merge
		getRepository().getEntityManager().detach(firstFound);
		firstFound = getRepository().merge(firstFound);
		assertNotNull(firstFound);

		// update
		String updateSuffix = "UPDATED"; //$NON-NLS-1$
		for (Field field : toCreate.getClass().getDeclaredFields()) {

			// assign only strings
			if (!Modifier.isFinal(field.getModifiers()) && field.getType().isAssignableFrom(String.class)) {
				PropertyDescriptor pd;
				try {
					pd = new PropertyDescriptor(field.getName(), getModelClass());
					pd.getWriteMethod().invoke(toCreate, field.getName() + updateSuffix);
				} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					fail(e.getMessage());
				}
			}
		}
		try {
			MODEL updated = getRepository().update(firstFound);
			MODEL updatedFound = getRepository().findById(firstFound.getId());
			assertSame(updated, updatedFound);
			compareString(updated, updatedFound);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// delete
		getRepository().delete(first);
		all = getRepository().findAll();
		assertTrue(all.isEmpty());

	}

	@SuppressWarnings("unchecked")
	@Test
	void testExecuteQuery() {

		// instantiate the model with data fulfilled
		MODEL toCreate = getModelFulfilled(getModelInstance());

		// create
		try {
			toCreate = getRepository().create(toCreate);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// list by native query
		EntityManager entityManager = getDaoManager().getEntityManager();

		// get annotation Table on the Model to get the table name
		Table[] annotationsByType = getModelClass().getAnnotationsByType(javax.persistence.Table.class);
		Table table = annotationsByType[0];

		// query
		Query nativeQuery = entityManager.createNativeQuery("SELECT * FROM " + table.name(), getModelClass()); //$NON-NLS-1$
		List<MODEL> resultList = nativeQuery.getResultList();

		assertTrue(!resultList.isEmpty());

		// delete
		getRepository().delete(toCreate);

	}

}
