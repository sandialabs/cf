/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.ModelRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;

/**
 * JUnit tests to check the abstract DAO and hsqldb connection and querying
 * 
 * @author Didier Verstraete
 *
 */
class HSQLDBAndAbstractCRUDRepositoryTest extends AbstractTestDao {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(HSQLDBAndAbstractCRUDRepositoryTest.class);

	ModelRepository getRepository() {
		return new ModelRepository(getDaoManager().getEntityManager());
	}

	/**
	 * test CRUD querying methods for Model class (INSERT,SELECT,UPDATE,DELETE)
	 */
	@Test
	void testAbstractRepositoryWithModelRepositoryCRUD() {

		// create
		Model modelToCreate = new Model();
		modelToCreate.setVersion("Version"); //$NON-NLS-1$
		modelToCreate.setVersionOrigin("VersionOrigin"); //$NON-NLS-1$
		modelToCreate.setApplication("Application"); //$NON-NLS-1$
		modelToCreate.setContact("Test"); //$NON-NLS-1$
		try {
			getRepository().create(modelToCreate);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// select all
		List<Model> models = getRepository().findAll();
		assertEquals(1, models.size());
		Model model = models.get(0);
		assertEquals("Application", model.getApplication()); //$NON-NLS-1$
		assertEquals("Test", model.getContact()); //$NON-NLS-1$

		// findbyid
		Model model1 = getRepository().findById(model.getId());
		assertNotNull(model1);

		// update
		model1.setApplication("my_new_name"); //$NON-NLS-1$
		model1.setContact("uri_modified"); //$NON-NLS-1$
		try {
			getRepository().update(model1);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		Model model2 = getRepository().findById(model1.getId());
		assertEquals(model2.getApplication(), model1.getApplication());
		assertEquals(model2.getContact(), model1.getContact());

		// delete
		getRepository().delete(model);
		models = getRepository().findAll();
		assertTrue(models.isEmpty());
	}

	@Test
	@SuppressWarnings("unchecked")
	void testExecuteQueries() {

		// create
		Model modelToCreate = new Model();
		modelToCreate.setVersion("Version"); //$NON-NLS-1$
		modelToCreate.setVersionOrigin("VersionOrigin"); //$NON-NLS-1$
		modelToCreate.setApplication("/test/me"); //$NON-NLS-1$
		modelToCreate.setContact("Test Me"); //$NON-NLS-1$
		try {
			Model createdModel = getRepository().create(modelToCreate);
			assertNotNull(createdModel);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// list by native query
		EntityManager entityManager = getDaoManager().getEntityManager();
		Query nativeQuery;
		Class<Model> resultClass = Model.class;
		nativeQuery = entityManager.createNativeQuery("SELECT * FROM Model", resultClass); //$NON-NLS-1$
		List<Model> resultList = nativeQuery.getResultList();
		assertNotNull(resultList);

		resultList.forEach(e -> logger.info("{}", e)); //$NON-NLS-1$

		// delete
		getRepository().delete(modelToCreate);

	}

}
