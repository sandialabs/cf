/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.MessageFormat;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.configuration.YmlGenericSchema;
import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.IPCMMPlanningParamRepository;
import gov.sandia.cf.dao.IPCMMPlanningQuestionRepository;
import gov.sandia.cf.dao.IQoIPlanningParamRepository;
import gov.sandia.cf.dao.ISystemRequirementParamRepository;
import gov.sandia.cf.dao.migration.tasks.Task_004_GenericParameterRequired;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * The Class Task_004_GenericParameterRequiredTest.
 *
 * @author Didier Verstraete
 */
class Task_004_GenericParameterRequiredTest extends AbstractTestDao {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(Task_004_GenericParameterRequiredTest.class);

	@Test
	void test_MigrationTask_NotMigrated() {

		// initialize data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		PCMMPlanningParam pcmmPlanningParamTrue = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), newModel);
		PCMMPlanningParam pcmmPlanningParamFalse = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), newModel);

		PCMMPlanningQuestion pcmmPlanningQuestionTrue = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				null);
		PCMMPlanningQuestion pcmmPlanningQuestionFalse = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				null);

		QoIPlanningParam qoiPlanningParamTrue = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), newModel);
		QoIPlanningParam qoiPlanningParamFalse = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), newModel);

		SystemRequirementParam sysReqParamTrue = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(),
				newModel, null);
		SystemRequirementParam sysReqParamFalse = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(),
				newModel, null);

		TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel, null);
		TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel, null);

		// alter table - inject column to be deleted and migrated
		String sqlAlterTable = "ALTER TABLE {0} ADD COLUMN IS_REQUIRED BOOLEAN;"; //$NON-NLS-1$
		String sqlUpdate = "UPDATE {0} SET IS_REQUIRED={1} WHERE ID={2};"; //$NON-NLS-1$

		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);

		unitOfWork.executeNonSelectingSQL(MessageFormat.format(sqlAlterTable, "PCMM_PLANNING_PARAM")); //$NON-NLS-1$
		unitOfWork.executeNonSelectingSQL(
				MessageFormat.format(sqlUpdate, "PCMM_PLANNING_PARAM", "true", pcmmPlanningParamTrue.getId())); //$NON-NLS-1$ //$NON-NLS-2$
		unitOfWork.executeNonSelectingSQL(
				MessageFormat.format(sqlUpdate, "PCMM_PLANNING_PARAM", "false", pcmmPlanningParamFalse.getId())); //$NON-NLS-1$ //$NON-NLS-2$

		unitOfWork.executeNonSelectingSQL(MessageFormat.format(sqlAlterTable, "PCMM_PLANNING_QUESTION")); //$NON-NLS-1$
		unitOfWork.executeNonSelectingSQL(MessageFormat.format(sqlUpdate, "PCMM_PLANNING_QUESTION", "true", //$NON-NLS-1$ //$NON-NLS-2$
				pcmmPlanningQuestionTrue.getId()));
		unitOfWork.executeNonSelectingSQL(MessageFormat.format(sqlUpdate, "PCMM_PLANNING_QUESTION", "false", //$NON-NLS-1$ //$NON-NLS-2$
				pcmmPlanningQuestionFalse.getId()));

		unitOfWork.executeNonSelectingSQL(MessageFormat.format(sqlAlterTable, "QOI_PLANNING_PARAM")); //$NON-NLS-1$
		unitOfWork.executeNonSelectingSQL(
				MessageFormat.format(sqlUpdate, "QOI_PLANNING_PARAM", "true", qoiPlanningParamTrue.getId())); //$NON-NLS-1$ //$NON-NLS-2$
		unitOfWork.executeNonSelectingSQL(
				MessageFormat.format(sqlUpdate, "QOI_PLANNING_PARAM", "false", qoiPlanningParamFalse.getId())); //$NON-NLS-1$ //$NON-NLS-2$

		unitOfWork.executeNonSelectingSQL(MessageFormat.format(sqlAlterTable, "COM_REQUIREMENT_PARAM")); //$NON-NLS-1$
		unitOfWork.executeNonSelectingSQL(
				MessageFormat.format(sqlUpdate, "COM_REQUIREMENT_PARAM", "true", sysReqParamTrue.getId())); //$NON-NLS-1$ //$NON-NLS-2$
		unitOfWork.executeNonSelectingSQL(
				MessageFormat.format(sqlUpdate, "COM_REQUIREMENT_PARAM", "false", sysReqParamFalse.getId())); //$NON-NLS-1$ //$NON-NLS-2$

		// test
		try {
			boolean changed = new Task_004_GenericParameterRequired().execute(getDaoManager());
			assertTrue(changed);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}

		// check IS_REQUIRED columns deletion
		String fieldIsRequired = "IS_REQUIRED"; //$NON-NLS-1$
		assertFalse(
				EclipseLinkMigrationManager.existsColumnInTable(unitOfWork, "PCMM_PLANNING_PARAM", fieldIsRequired)); //$NON-NLS-1$
		assertFalse(
				EclipseLinkMigrationManager.existsColumnInTable(unitOfWork, "PCMM_PLANNING_QUESTION", fieldIsRequired)); //$NON-NLS-1$
		assertFalse(EclipseLinkMigrationManager.existsColumnInTable(unitOfWork, "QOI_PLANNING_PARAM", fieldIsRequired)); //$NON-NLS-1$
		assertFalse(
				EclipseLinkMigrationManager.existsColumnInTable(unitOfWork, "COM_REQUIREMENT_PARAM", fieldIsRequired)); //$NON-NLS-1$
		assertFalse(
				EclipseLinkMigrationManager.existsColumnInTable(unitOfWork, "COM_UNCERTAINTY_PARAM", fieldIsRequired)); //$NON-NLS-1$

		// check data migration from IS_REQUIRED to REQUIRED
		getDaoManager().getRepository(IPCMMPlanningParamRepository.class).refresh(pcmmPlanningParamTrue);
		assertEquals(YmlGenericSchema.CONF_GENERIC_TRUE_VALUE, pcmmPlanningParamTrue.getRequired());
		getDaoManager().getRepository(IPCMMPlanningParamRepository.class).refresh(pcmmPlanningParamFalse);
		assertEquals(YmlGenericSchema.CONF_GENERIC_FALSE_VALUE, pcmmPlanningParamFalse.getRequired());

		getDaoManager().getRepository(IPCMMPlanningQuestionRepository.class).refresh(pcmmPlanningQuestionTrue);
		assertEquals(YmlGenericSchema.CONF_GENERIC_TRUE_VALUE, pcmmPlanningQuestionTrue.getRequired());
		getDaoManager().getRepository(IPCMMPlanningQuestionRepository.class).refresh(pcmmPlanningQuestionFalse);
		assertEquals(YmlGenericSchema.CONF_GENERIC_FALSE_VALUE, pcmmPlanningQuestionFalse.getRequired());

		getDaoManager().getRepository(IQoIPlanningParamRepository.class).refresh(qoiPlanningParamTrue);
		assertEquals(YmlGenericSchema.CONF_GENERIC_TRUE_VALUE, qoiPlanningParamTrue.getRequired());
		getDaoManager().getRepository(IQoIPlanningParamRepository.class).refresh(qoiPlanningParamFalse);
		assertEquals(YmlGenericSchema.CONF_GENERIC_FALSE_VALUE, qoiPlanningParamFalse.getRequired());

		getDaoManager().getRepository(ISystemRequirementParamRepository.class).refresh(sysReqParamTrue);
		assertEquals(YmlGenericSchema.CONF_GENERIC_TRUE_VALUE, sysReqParamTrue.getRequired());
		getDaoManager().getRepository(ISystemRequirementParamRepository.class).refresh(sysReqParamFalse);
		assertEquals(YmlGenericSchema.CONF_GENERIC_FALSE_VALUE, sysReqParamFalse.getRequired());
	}

	@Test
	void test_MigrationTask_AlreadyMigrated() {

		// initialize data
		TestEntityFactory.getNewModel(getDaoManager());

		// test
		try {
			boolean changed = new Task_004_GenericParameterRequired().execute(getDaoManager());
			assertFalse(changed);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}
	}
}
