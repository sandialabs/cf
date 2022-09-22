/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import gov.sandia.cf.dao.CredibilityDaoRuntimeException.CredibilityDaoRuntimeMessage;
import gov.sandia.cf.dao.impl.CriterionRepository;
import gov.sandia.cf.exceptions.CredibilityServiceRuntimeException;
import gov.sandia.cf.exceptions.CredibilityServiceRuntimeException.CredibilityServiceRuntimeMessage;

/**
 * Abstract DAO JUnit test class
 * 
 * @author Didier Verstraete
 *
 */
class DaoManagerTest extends AbstractTestDao {

	@Test
	void test_getRepository_PIRT() {
		assertNotNull(getDaoManager().getRepository(ICriterionRepository.class));
		assertNotNull(getDaoManager().getRepository(ICriterionRepository.class).getEntityManager());
		assertNotNull(getDaoManager().getRepository(IPhenomenonGroupRepository.class));
		assertNotNull(getDaoManager().getRepository(IPhenomenonGroupRepository.class).getEntityManager());
		assertNotNull(getDaoManager().getRepository(IPhenomenonRepository.class));
		assertNotNull(getDaoManager().getRepository(IPhenomenonRepository.class).getEntityManager());
		assertNotNull(getDaoManager().getRepository(IQoIHeaderRepository.class));
		assertNotNull(getDaoManager().getRepository(IQoIHeaderRepository.class).getEntityManager());
		assertNotNull(getDaoManager().getRepository(IQuantityOfInterestRepository.class));
		assertNotNull(getDaoManager().getRepository(IQuantityOfInterestRepository.class).getEntityManager());
	}

	@Test
	void test_getRepository_PCMM() {
		assertNotNull(getDaoManager().getRepository(IPCMMAssessmentRepository.class));
		assertNotNull(getDaoManager().getRepository(IPCMMAssessmentRepository.class).getEntityManager());
		assertNotNull(getDaoManager().getRepository(IPCMMElementRepository.class));
		assertNotNull(getDaoManager().getRepository(IPCMMElementRepository.class).getEntityManager());
		assertNotNull(getDaoManager().getRepository(IPCMMEvidenceRepository.class));
		assertNotNull(getDaoManager().getRepository(IPCMMEvidenceRepository.class).getEntityManager());
		assertNotNull(getDaoManager().getRepository(IPCMMLevelDescRepository.class));
		assertNotNull(getDaoManager().getRepository(IPCMMLevelDescRepository.class).getEntityManager());
		assertNotNull(getDaoManager().getRepository(IPCMMLevelRepository.class));
		assertNotNull(getDaoManager().getRepository(IPCMMLevelRepository.class).getEntityManager());
		assertNotNull(getDaoManager().getRepository(IPCMMSubelementRepository.class));
		assertNotNull(getDaoManager().getRepository(IPCMMSubelementRepository.class).getEntityManager());
	}

	@Test
	void test_getRepository_Others() {
		assertNotNull(getDaoManager().getRepository(IDocumentRepository.class));
		assertNotNull(getDaoManager().getRepository(IDocumentRepository.class).getEntityManager());
		assertNotNull(getDaoManager().getRepository(IModelRepository.class));
		assertNotNull(getDaoManager().getRepository(IModelRepository.class).getEntityManager());
		assertNotNull(getDaoManager().getNativeQueryRepository());
		assertNotNull(getDaoManager().getNativeQueryRepository().getEntityManager());
		assertNotNull(getDaoManager().getRepository(IRoleRepository.class));
		assertNotNull(getDaoManager().getRepository(IRoleRepository.class).getEntityManager());
		assertNotNull(getDaoManager().getRepository(ITagRepository.class));
		assertNotNull(getDaoManager().getRepository(ITagRepository.class).getEntityManager());
		assertNotNull(getDaoManager().getRepository(IUserRepository.class));
		assertNotNull(getDaoManager().getRepository(IUserRepository.class).getEntityManager());
	}

	@Test
	void test_getRepository_NotInterface() {
		try {
			getDaoManager().getRepository(CriterionRepository.class);
		} catch (CredibilityDaoRuntimeException e) {
			assertEquals(CredibilityDaoRuntimeMessage.NOT_INTERFACE.getMessage(), e.getMessage());
		}
	}

	@Test
	void test_getRepository_NotFound() {
		try {
			getDaoManager().getRepository(IDaoRepositoryNotFound.class);
		} catch (CredibilityServiceRuntimeException e) {
			assertEquals(CredibilityServiceRuntimeMessage.NOT_APPSERVICE_INTERFACE
					.getMessage(IDaoRepositoryNotFound.class.getName(), Repository.class.getName()), e.getMessage());
		}
	}

}
