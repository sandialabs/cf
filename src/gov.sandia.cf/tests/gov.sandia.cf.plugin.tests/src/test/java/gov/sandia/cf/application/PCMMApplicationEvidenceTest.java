/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMEvidenceApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.NotificationType;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tests.TestTools;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * JUnit test class for the PCMM Application Controller - Evidence
 * 
 * @author Maxime N.
 *
 */
class PCMMApplicationEvidenceTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(PCMMApplicationEvidenceTest.class);

	@Test
	void testEvidenceCRUDWorking() throws CoreException, CredibilityException {

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create role
		Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(defaultRole);

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// create element
		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		assertNotNull(element);

		// create sub-element
		PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), element);
		assertNotNull(subelement);

		// ************************
		// Create evidence
		// ************************
		PCMMEvidence evidence = new PCMMEvidence();
		evidence.setName("My_Evidence"); //$NON-NLS-1$
		evidence.setDescription("My_Description"); //$NON-NLS-1$
		IFile newFile = TestEntityFactory.getNewFile("MyProject", "test.txt"); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(newFile);
		evidence.setFilePath(newFile.getFullPath().toPortableString());
		evidence.setUserCreation(defaultUser);
		evidence.setRoleCreation(defaultRole);
		evidence.setSubelement(subelement);
		PCMMEvidence addedEvidence = getPCMMEvidenceApp().addEvidence(evidence);

		// Test creation
		assertNotNull(addedEvidence);
		assertNotNull(addedEvidence.getId());
		assertEquals(FormFieldType.LINK_FILE, addedEvidence.getType());
		// Check if the evidence path has replaced "\\" by "/" (see gitlab issue #262).
		assertEquals(newFile.getFullPath().toPortableString(), addedEvidence.getPath());

		// ************************
		// Get Evidence by Id
		// ************************
		PCMMEvidence evidenceById = getPCMMEvidenceApp().getEvidenceById(addedEvidence.getId());

		// Check
		assertNotNull(evidenceById);
		assertEquals(evidenceById.getId(), addedEvidence.getId());

		// ******************************
		// Create new Tag & associate it
		// ******************************
		Tag newTag = new Tag();
		newTag.setName("My_Tag"); //$NON-NLS-1$
		newTag.setUserCreation(defaultUser);
		newTag.setDateTag(new Date());
		Tag createdTag = getPCMMApp().tagCurrent(newTag);

		// ******************************
		// Check tag is associated
		// ******************************
		List<PCMMEvidence> evidenceByTag = getPCMMEvidenceApp().getEvidenceByTag(createdTag);

		// Check evidence list
		assertNotNull(evidenceByTag);
		assertEquals("evidenceByTag list size: " + evidenceByTag.size(), 1, evidenceByTag.size()); //$NON-NLS-1$

		// Check evidence found
		PCMMEvidence foundEvidence = evidenceByTag.get(0);
		assertNotNull(foundEvidence);
		assertEquals(createdTag, foundEvidence.getTag());
		assertNotEquals(addedEvidence.getId(), foundEvidence.getId());
		assertEquals("My_Evidence", foundEvidence.getName()); //$NON-NLS-1$
		assertEquals("My_Description", foundEvidence.getDescription()); //$NON-NLS-1$
		assertEquals(newFile.getFullPath().toPortableString(), foundEvidence.getPath()); // $NON-NLS-1$
		assertEquals(FormFieldType.LINK_FILE, foundEvidence.getType());
		assertEquals(defaultUser, foundEvidence.getUserCreation());
		assertEquals(defaultRole, foundEvidence.getRoleCreation());

		// ******************************
		// Update evidence
		// ******************************
		// Create updated evidence
		PCMMEvidence updatedEvidence = null;
		addedEvidence.setName("My_Evidence_Updated"); //$NON-NLS-1$

		// Update
		updatedEvidence = getPCMMEvidenceApp().updateEvidence(addedEvidence);
		assertEquals(addedEvidence.getName(), updatedEvidence.getName());
		assertNotNull(updatedEvidence);

		// ******************************
		// Get all evidences
		// ******************************
		List<PCMMEvidence> evidences = getPCMMEvidenceApp().getAllEvidence();
		assertFalse(evidences.isEmpty());

		// ******************************
		// Delete evidence
		// ******************************
		getPCMMEvidenceApp().deleteEvidence(updatedEvidence);
		assertNull(getPCMMEvidenceApp().getEvidenceById(updatedEvidence.getId()));

		// ************************
		// Create evidence
		// ************************
		evidence = new PCMMEvidence();
		evidence.setName("My_Evidence"); //$NON-NLS-1$
		evidence.setFilePath(newFile.getFullPath().toPortableString());
		evidence.setUserCreation(defaultUser);
		evidence.setRoleCreation(defaultRole);
		evidence.setDescription("My_Description"); //$NON-NLS-1$
		evidence.setElement(element);
		List<PCMMEvidence> addedEvidenceList = new ArrayList<>();
		addedEvidence = getPCMMEvidenceApp().addEvidence(evidence);
		addedEvidenceList.add(addedEvidence);

		// ******************************
		// Delete evidence List
		// ******************************
		getPCMMEvidenceApp().deleteEvidence(addedEvidenceList);
		assertNull(getPCMMEvidenceApp().getEvidenceById(addedEvidenceList.get(0).getId()));

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	/* ************** getEvidenceById ************* */

	@Test
	void test_getEvidenceById_Id_Null() {
		// ************************
		// Check Evidence null
		// ************************
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMEvidenceApp.class).getEvidenceById(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_GETEVIDENCEBYID_IDNULL), e.getMessage());
	}

	/* ************** addEvidence ************* */

	@Test
	void test_addEvidence_URL() throws CredibilityException {

		// Initialize
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		Role newRole = TestEntityFactory.getNewRole(getDaoManager());
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);

		PCMMEvidence evidence = new PCMMEvidence();
		evidence.setName("My_Evidence"); //$NON-NLS-1$
		evidence.setDescription("My_Description"); //$NON-NLS-1$
		evidence.setURL("http://sandia.gov"); //$NON-NLS-1$
		evidence.setUserCreation(newUser);
		evidence.setRoleCreation(newRole);
		evidence.setSubelement(newPCMMSubelement);
		PCMMEvidence addedEvidence = getPCMMEvidenceApp().addEvidence(evidence);

		// Test creation
		assertNotNull(addedEvidence);
		assertNotNull(addedEvidence.getId());
		assertEquals(FormFieldType.LINK_URL, addedEvidence.getType());
		assertEquals("http://sandia.gov", addedEvidence.getPath()); //$NON-NLS-1$
	}

	@Test
	void test_addEvidence_Error_Null() {

		// ************************
		// Check Evidence null
		// ************************
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().addEvidence(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_ADDEVIDENCE_EVIDENCENULL), e.getMessage());
	}

	@Test
	void test_addEvidence_Error_UserNull() throws CoreException {

		// construct
		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMElement element2 = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		IFile newFile = TestEntityFactory.getNewFile(null, null);
		PCMMEvidence evidence2 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element, newFile)
				.copy();
		evidence2.setElement(element2);
		evidence2.setUserCreation(null);

		// Check Evidence user null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().addEvidence(evidence2);
		});
		assertTrue(e.getCause() instanceof ConstraintViolationException);
		assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
				RscConst.EX_PCMMEVIDENCE_USER_NULL));

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_addEvidence_Error_InvalidPath() {

		// construct
		PCMMEvidence evidence2 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null).copy();
		evidence2.setFilePath("/invalid/path"); //$NON-NLS-1$

		// Check Evidence role null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().addEvidence(evidence2);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDPATH, evidence2.getPath()), e.getMessage());
	}

	@Test
	void test_addEvidence_Error_RoleNull() throws CoreException {

		// construct
		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMElement element2 = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		IFile newFile = TestEntityFactory.getNewFile(null, null);
		PCMMEvidence evidence2 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element, newFile)
				.copy();
		evidence2.setElement(element2);
		evidence2.setRoleCreation(null);

		// Check Evidence role null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().addEvidence(evidence2);
		});
		assertTrue(e.getCause() instanceof ConstraintViolationException);
		assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
				RscConst.EX_PCMMEVIDENCE_ROLE_NULL));

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_addEvidence_Error_PathNull() {

		// create
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), createdElement);
		PCMMEvidence evidence2 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, subelement).copy();
		evidence2.setFilePath(null);

		// Check Evidence path null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().addEvidence(evidence2);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDPATH, evidence2.getPath()), e.getMessage());
	}

	@Test
	void test_addEvidence_Error_PathEmpty() {

		// create
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), createdElement);
		PCMMEvidence evidence2 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, subelement).copy();
		evidence2.setFilePath(""); //$NON-NLS-1$

		// Check Evidence path null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().addEvidence(evidence2);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDPATH, evidence2.getPath()), e.getMessage());
	}

	@Test
	void test_addEvidence_Error_URLNull() {

		// create
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), createdElement);
		PCMMEvidence evidence2 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, subelement).copy();
		evidence2.setURL(null); // $NON-NLS-1$

		// Check Evidence path null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().addEvidence(evidence2);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDURL, evidence2.getPath()), e.getMessage());
	}

	@Test
	void test_addEvidence_Error_URLEmpty() {

		// create
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), createdElement);
		PCMMEvidence evidence2 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, subelement).copy();
		evidence2.setURL(""); //$NON-NLS-1$

		// Check Evidence path null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().addEvidence(evidence2);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDURL, evidence2.getPath()), e.getMessage());
	}

	@Test
	void test_addEvidence_Error_URLInvalid() {

		// create
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), createdElement);
		PCMMEvidence evidence2 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, subelement).copy();
		evidence2.setURL("/hy/d.ssss"); //$NON-NLS-1$

		// Check Evidence path null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().addEvidence(evidence2);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDURL, evidence2.getPath()), e.getMessage());
	}

	@Test
	void test_addEvidence_Error_PathAlreadyExistsSameElementModeSimplified()
			throws CredibilityException, CoreException {

		// create
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		IFile newFile = TestEntityFactory.getNewFile();
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, createdElement,
				newFile);
		PCMMEvidence evidence2 = evidence.copy();
		evidence2.setFilePath(evidence.getPath());

		// Check Evidence already exists with same path
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().addEvidence(evidence2);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_ADDEVIDENCE_ALREADYEXISTS, evidence2.getPath(),
				evidence2.getElement().getName()), e.getMessage());

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_addEvidence_Error_PathAlreadyExistsSameElementModeDefault() throws CredibilityException, CoreException {

		// create
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), createdElement);
		IFile newFile = TestEntityFactory.getNewFile(null, "evidence.txt"); //$NON-NLS-1$
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, subelement, newFile);
		PCMMEvidence evidence2 = evidence.copy();
		evidence2.setFilePath(evidence.getPath());

		// Check Evidence already exists with same path
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().addEvidence(evidence2);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_ADDEVIDENCE_ALREADYEXISTS, evidence2.getPath(),
				evidence2.getSubelement().getName()), e.getMessage());

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_addEvidence_Ok_PathAlreadyExistsDifferentElementModeSimplified()
			throws CredibilityException, CoreException {

		// create
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		PCMMElement createdElement2 = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		IFile newFile = TestEntityFactory.getNewFile();
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, createdElement,
				newFile);
		PCMMEvidence evidence2 = evidence.copy();
		evidence2.setElement(createdElement2);
		evidence2.setFilePath(evidence.getPath());

		// Check Evidence already exists with same path
		evidence2 = getPCMMEvidenceApp().addEvidence(evidence2);
		assertNotNull(evidence2);
		assertNotNull(evidence2.getId());

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_addEvidence_Ok_PathAlreadyExistsDifferentElementModeDefault() throws CredibilityException, CoreException {

		// create
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), createdElement);
		PCMMSubelement subelement2 = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), createdElement);
		IFile newFile = TestEntityFactory.getNewFile(null, null);
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, subelement, newFile);
		assertNotNull(evidence);
		PCMMEvidence evidence2 = evidence.copy();
		evidence2.setSubelement(subelement2);
		evidence2.setFilePath(evidence.getPath());

		// Check Evidence already exists with same path
		evidence2 = getPCMMEvidenceApp().addEvidence(evidence2);
		assertNotNull(evidence2);
		assertNotNull(evidence2.getId());

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_addEvidence_Error_NoAssessable() {

		// create
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null).copy();
		evidence.setElement(null);
		evidence.setSubelement(null);

		// Check Evidence path null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().addEvidence(evidence);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_NOASSESSABLE), e.getMessage());
	}

	@Test
	void test_addEvidence_Error_MoreThanOneAssessable() {

		// create
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), createdElement);
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, subelement).copy();
		evidence.setElement(createdElement);
		evidence.setSubelement(subelement);

		// Check Evidence path null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().addEvidence(evidence);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_MORETHANONEASSESSABLE), e.getMessage());
	}

	/* ************** updateEvidence ************* */

	@Test
	void test_updateEvidence_Error_Null() {

		// Check Evidence null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().updateEvidence(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATEEVIDENCE_ELTNULL), e.getMessage());
	}

	@Test
	void test_updateEvidence_Error_IdNull() {

		PCMMEvidence evidence = new PCMMEvidence();

		// Check Evidence id null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().updateEvidence(evidence);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATEEVIDENCE_IDNULL), e.getMessage());
	}

	@Test
	void test_updateEvidence_Error_PathNull() {

		// create evidence
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null);
		evidence.setFilePath(null);

		// Check Evidence path null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().updateEvidence(evidence);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDPATH, evidence.getPath()), e.getMessage());
	}

	@Test
	void test_updateEvidence_Error_PathEmpty() {

		// create evidence
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null);
		evidence.setFilePath(""); //$NON-NLS-1$

		// Check Evidence path null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().updateEvidence(evidence);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDPATH, evidence.getPath()), e.getMessage());
	}

	@Test
	void test_updateEvidence_Error_BadURL() {

		// create evidence
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null);
		evidence.setURL("invalid_url"); //$NON-NLS-1$

		// Check Evidence path null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().updateEvidence(evidence);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDURL, evidence.getPath()), e.getMessage());
	}

	@Test
	void test_updateEvidence_Ok_NoPathChanges() throws CredibilityException, CoreException {

		// create evidence
		IFile newFile = TestEntityFactory.getNewFile(null, "evidence.txt"); //$NON-NLS-1$
		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element, newFile);
		assertNotNull(evidence.getPath());
		Integer evidenceId = evidence.getId();
		evidence.setDescription("My new description<li></li>"); //$NON-NLS-1$

		// Check Evidence already exists with same path
		// update without path changes
		evidence = getPCMMEvidenceApp().updateEvidence(evidence);
		assertNotNull(evidence);
		assertEquals(evidenceId, evidence.getId());
		assertEquals("My new description<li></li>", evidence.getDescription()); //$NON-NLS-1$

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_updateEvidence_Error_NoAssessable() {

		// create
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null);
		evidence.setElement(null);
		evidence.setSubelement(null);

		// Check Evidence path null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().updateEvidence(evidence);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_NOASSESSABLE), e.getMessage());
	}

	@Test
	void test_updateEvidence_Error_MoreThanOneAssessable() {

		// create
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), createdElement);
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, subelement);
		evidence.setElement(createdElement);
		evidence.setSubelement(subelement);

		// Check Evidence path null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().updateEvidence(evidence);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_MORETHANONEASSESSABLE), e.getMessage());
	}

	/* ************** deleteEvidence ************* */

	@Test
	void test_deleteEvidence_evidence_null() {

		PCMMEvidence evidence = null;

		// Check Evidence null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().deleteEvidence(evidence);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETEEVIDENCE_ELTNULL), e.getMessage());
	}

	@Test
	void test_deleteEvidence_evidence_id_null() {

		// Create evidence
		PCMMEvidence evidence = new PCMMEvidence();

		// Check Evidence id null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().deleteEvidence(evidence);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETEEVIDENCE_IDNULL), e.getMessage());
	}

	@Test
	void test_deleteEvidence_list_null() {

		// Check Evidence list null
		List<PCMMEvidence> addedEvidenceList = null;
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().deleteEvidence(addedEvidenceList);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETEEVIDENCE_ELTNULL), e.getMessage());
	}

	/* ************** evidenceChanged ************* */

	@Test
	void test_evidenceChanged_Changed() throws CoreException, CredibilityException {

		// create evidence
		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		IFile file = TestEntityFactory.getNewFile("DesiredProject", "evidence.txt"); //$NON-NLS-1$ //$NON-NLS-2$
		PCMMEvidence newPCMMEvidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element, file);
		newPCMMEvidence.setDateFile(DateTools.getDefault1900Date()); // to bypass date file change on filesystem
		getPCMMEvidenceApp().updateEvidence(newPCMMEvidence);

		// change the file content
		String initialString = "text"; //$NON-NLS-1$
		file.appendContents(new ByteArrayInputStream(initialString.getBytes()), true, true, new NullProgressMonitor());

		// test
		boolean evidenceChanged = getPCMMEvidenceApp().evidenceChanged(newPCMMEvidence);
		assertTrue(evidenceChanged);

		// delete file
		file.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_evidenceChanged_NotChanged() throws CoreException, CredibilityException {

		// create evidence
		IFile newFile = TestEntityFactory.getNewFile(null, "evidence.txt"); //$NON-NLS-1$
		PCMMEvidence newPCMMEvidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null, newFile);

		// test
		boolean evidenceChanged = getPCMMEvidenceApp().evidenceChanged(newPCMMEvidence);
		assertFalse(evidenceChanged);

		// delete file
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	/* ************** getAllEvidenceNotifications ************* */

	@Test
	void test_getAllEvidenceNotifications_3Cases() throws CoreException, CredibilityException {

		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);

		// evidence 1: file does not exist
		PCMMEvidence evidenceFileDoesNotExist = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null,
				element, WorkspaceTools.getFileInWorkspaceForPath(new Path("MyProject/File.txt"))); //$NON-NLS-1$

		// evidence 2: file changed
		IFile file = TestEntityFactory.getNewFile("DesiredProject", "evidence.txt"); //$NON-NLS-1$ //$NON-NLS-2$
		PCMMEvidence evidenceFileChanged = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element,
				file);
		evidenceFileChanged.setDateFile(DateTools.getDefault1900Date()); // to bypass date file change on filesystem
		getPCMMEvidenceApp().updateEvidence(evidenceFileChanged);

		// evidence 3: everything is fine
		PCMMEvidence evidenceOk = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element);

		// test
		Map<PCMMEvidence, Map<NotificationType, List<String>>> evidenceNotifications = getPCMMEvidenceApp()
				.getAllEvidenceNotifications();
		assertTrue(evidenceNotifications.get(evidenceFileDoesNotExist).get(NotificationType.WARN).isEmpty());
		assertFalse(evidenceNotifications.get(evidenceFileDoesNotExist).get(NotificationType.ERROR).isEmpty());
		assertFalse(evidenceNotifications.get(evidenceFileChanged).get(NotificationType.WARN).isEmpty());
		assertTrue(evidenceNotifications.get(evidenceFileChanged).get(NotificationType.ERROR).isEmpty());
		assertTrue(evidenceNotifications.get(evidenceOk).get(NotificationType.WARN).isEmpty());
		assertTrue(evidenceNotifications.get(evidenceOk).get(NotificationType.ERROR).isEmpty());

		// delete file
		file.getProject().delete(true, new NullProgressMonitor());
	}

	/* ************** findEvidenceWarningNotification ************* */

	@Test
	void test_findEvidenceWarningNotification_1Warning() throws CoreException, CredibilityException {

		// evidence 1: file does not exist - error
		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element,
				WorkspaceTools.getFileInWorkspaceForPath(new Path("MyProject/File.txt"))); //$NON-NLS-1$

		// evidence 2: file changed - warning
		IFile file = TestEntityFactory.getNewFile("DesiredProject", "evidence.txt"); //$NON-NLS-1$ //$NON-NLS-2$
		PCMMEvidence evidenceFileChanged = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element,
				file);
		evidenceFileChanged.setDateFile(DateTools.getDefault1900Date()); // to bypass date file change on filesystem
		getPCMMEvidenceApp().updateEvidence(evidenceFileChanged);

		// evidence 3: everything is fine
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element);

		// test
		int evidenceNotifications = getPCMMEvidenceApp().findEvidenceWarningNotification();
		assertEquals(1, evidenceNotifications);

		// delete file
		file.getProject().delete(true, new NullProgressMonitor());
	}

	/* ************** findEvidenceErrorNotification ************* */

	@Test
	void test_findEvidenceErrorNotification_1Error() throws CoreException, CredibilityException {

		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);

		// evidence 1: file does not exist - error
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element,
				WorkspaceTools.getFileInWorkspaceForPath(new Path("MyProject/File.txt"))); //$NON-NLS-1$

		// evidence 2: file changed - warning
		IFile file = TestEntityFactory.getNewFile("DesiredProject", "evidence.txt"); //$NON-NLS-1$ //$NON-NLS-2$
		PCMMEvidence evidenceFileChanged = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element,
				file);
		evidenceFileChanged.setDateFile(DateTools.getDefault1900Date()); // to bypass date file change on filesystem
		getPCMMEvidenceApp().updateEvidence(evidenceFileChanged);

		// evidence 3: everything is fine
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element);

		// test
		int evidenceNotifications = getPCMMEvidenceApp().findEvidenceErrorNotification();
		assertEquals(1, evidenceNotifications);

		// delete file
		file.getProject().delete(true, new NullProgressMonitor());
	}

	/* ************** getEvidenceNotifications ************* */

	@Test
	void test_getEvidenceNotifications_FileDoesNotExist() throws CoreException, CredibilityException {

		PCMMEvidence newPCMMEvidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null);
		newPCMMEvidence.setFilePath("MyProject/File.txt"); //$NON-NLS-1$

		// test
		Map<NotificationType, List<String>> evidenceNotifications = getPCMMEvidenceApp()
				.getEvidenceNotifications(newPCMMEvidence, newPCMMEvidence.getId());
		assertTrue(evidenceNotifications.containsKey(NotificationType.ERROR));
		assertTrue(evidenceNotifications.get(NotificationType.ERROR).contains(RscTools
				.getString(RscConst.NOTIFICATION_PCMM_EVIDENCE_ERR_FILE_NOT_EXISTS, newPCMMEvidence.getName())));
	}

	@Test
	void test_getEvidenceNotifications_FileChanged() throws CoreException, CredibilityException {

		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		IFile file = TestEntityFactory.getNewFile("DesiredProject", "evidence.txt"); //$NON-NLS-1$ //$NON-NLS-2$
		PCMMEvidence newPCMMEvidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element, file);
		newPCMMEvidence.setDateFile(DateTools.getDefault1900Date()); // to bypass date file change on filesystem
		getPCMMEvidenceApp().updateEvidence(newPCMMEvidence);

		// change the file content
		String initialString = "text"; //$NON-NLS-1$
		file.appendContents(new ByteArrayInputStream(initialString.getBytes()), true, true, new NullProgressMonitor());

		// test
		Map<NotificationType, List<String>> evidenceNotifications = getPCMMEvidenceApp()
				.getEvidenceNotifications(newPCMMEvidence, newPCMMEvidence.getId());
		assertTrue(evidenceNotifications.containsKey(NotificationType.WARN));
		assertTrue(evidenceNotifications.get(NotificationType.WARN).contains(
				RscTools.getString(RscConst.NOTIFICATION_PCMM_EVIDENCE_WARN_UPDATED_FILE, newPCMMEvidence.getName())));

		// delete file
		file.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_getEvidenceNotifications_EvidenceDuplicated_OtherPCMMElement()
			throws CoreException, CredibilityException {

		IFile file = TestEntityFactory.getNewFile("DesiredProject", "evidence.txt"); //$NON-NLS-1$ //$NON-NLS-2$
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement elt1 = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMElement elt2 = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMEvidence evid1 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, elt1, file);
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, elt2, file);

		// test
		Map<NotificationType, List<String>> evidenceNotifications = getPCMMEvidenceApp().getEvidenceNotifications(evid1,
				evid1.getId());
		assertTrue(evidenceNotifications.containsKey(NotificationType.WARN));

		// delete file
		file.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_getEvidenceNotifications_EvidenceDuplicated_SamePCMMElement() throws CoreException, CredibilityException {

		IFile file = TestEntityFactory.getNewFile("DesiredProject", "evidence.txt"); //$NON-NLS-1$ //$NON-NLS-2$
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement elt1 = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMEvidence evid1 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, elt1, file);
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, elt1, file);

		// test
		Map<NotificationType, List<String>> evidenceNotifications = getPCMMEvidenceApp().getEvidenceNotifications(evid1,
				evid1.getId());
		assertTrue(evidenceNotifications.containsKey(NotificationType.ERROR));

		// delete file
		file.getProject().delete(true, new NullProgressMonitor());
	}

	/* ************** getDuplicatedEvidenceNotification ************* */

	@Test
	void test_getDuplicatedEvidenceNotification_EvidenceDuplicated_OtherPCMMElement()
			throws CoreException, CredibilityException {

		IFile file = TestEntityFactory.getNewFile("DesiredProject", "evidence.txt"); //$NON-NLS-1$ //$NON-NLS-2$
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement elt1 = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMElement elt2 = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMEvidence evid1 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, elt1, file);
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, elt2, file);

		// test
		Map<NotificationType, String> evidenceNotifications = getPCMMEvidenceApp()
				.getDuplicatedEvidenceNotification(evid1, evid1.getId());
		assertTrue(evidenceNotifications.containsKey(NotificationType.WARN));

		// delete file
		file.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_getDuplicatedEvidenceNotification_EvidenceDuplicated_SamePCMMElement()
			throws CoreException, CredibilityException {

		IFile file = TestEntityFactory.getNewFile("DesiredProject", "evidence.txt"); //$NON-NLS-1$ //$NON-NLS-2$
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement elt1 = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMEvidence evid1 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, elt1, file);
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, elt1, file);

		// test
		Map<NotificationType, String> evidenceNotifications = getPCMMEvidenceApp()
				.getDuplicatedEvidenceNotification(evid1, evid1.getId());
		assertTrue(evidenceNotifications.containsKey(NotificationType.ERROR));

		// delete file
		file.getProject().delete(true, new NullProgressMonitor());
	}

	/* ************** getEvidenceByTag ************* */

	@Test
	void test_getEvidenceByTag_Ok() throws CoreException, CredibilityException {

		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		Tag tag = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element);
		PCMMEvidence evidenceTag = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element);
		evidenceTag.setTag(tag);
		getPCMMEvidenceApp().updateEvidence(evidenceTag);

		// test
		List<PCMMEvidence> evidenceByTag = getPCMMEvidenceApp().getEvidenceByTag(tag);
		assertTrue(evidenceByTag.contains(evidenceTag));
		assertFalse(evidenceByTag.contains(evidence));
	}

	@Test
	void test_getEvidenceByTag_Ok_TagList() throws CoreException, CredibilityException {

		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		Tag tag1 = TestEntityFactory.getNewTag(getDaoManager(), null);
		Tag tag2 = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element);
		PCMMEvidence evidenceTag1 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element);
		evidenceTag1.setTag(tag1);
		getPCMMEvidenceApp().updateEvidence(evidenceTag1);
		PCMMEvidence evidenceTag2 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element);
		evidenceTag2.setTag(tag2);
		getPCMMEvidenceApp().updateEvidence(evidenceTag2);

		// test
		List<PCMMEvidence> evidenceByTag = getPCMMEvidenceApp().getEvidenceByTag(Arrays.asList(tag1, tag2));
		assertTrue(evidenceByTag.contains(evidenceTag1));
		assertTrue(evidenceByTag.contains(evidenceTag2));
		assertFalse(evidenceByTag.contains(evidence));
	}

	/* ************** findDuplicateEvidenceByPath ************* */

	@Test
	void test_findDuplicateEvidenceByPath_1Copy() throws CoreException, CredibilityException {

		PCMMEvidence evidence1 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null,
				"http://my.com"); //$NON-NLS-1$
		PCMMEvidence evidence2 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null,
				"http://my.com"); //$NON-NLS-1$
		PCMMEvidence evidence3 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null,
				"http://test.com"); //$NON-NLS-1$

		// test
		List<PCMMEvidence> evidenceList = getPCMMEvidenceApp().findDuplicateEvidenceByPath(evidence1);
		assertTrue(evidenceList.contains(evidence1));
		assertTrue(evidenceList.contains(evidence2));
		assertFalse(evidenceList.contains(evidence3));
	}

	/* ************** checkEvidenceWithSamePathInAssessable ************* */

	@Test
	void test_checkEvidenceWithSamePathInAssessable_SameURL() {

		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element, "http://my.com"); //$NON-NLS-1$
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element, "http://my.com"); //$NON-NLS-1$
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element, "http://test.com"); //$NON-NLS-1$

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().checkEvidenceWithSamePathInAssessable("http://my.com", null, element); //$NON-NLS-1$
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_ADDEVIDENCE_ALREADYEXISTS, "http://my.com", //$NON-NLS-1$
				element.getName() + ", " + element.getName()), e.getMessage()); //$NON-NLS-1$
	}

	@Test
	void test_checkEvidenceWithSamePathInAssessable_SameFile() throws CoreException {

		IFile file = TestEntityFactory.getNewFile("DesiredProject", "evidence.txt"); //$NON-NLS-1$ //$NON-NLS-2$
		IFile file2 = TestEntityFactory.getNewFile("DesiredProject", "evidence2.txt"); //$NON-NLS-1$ //$NON-NLS-2$

		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element, file);
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element, file);
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element, file2);

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMEvidenceApp().checkEvidenceWithSamePathInAssessable(file.getFullPath().toString(), null, element);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_ADDEVIDENCE_ALREADYEXISTS, file.getFullPath().toString(), // $NON-NLS-1$
				element.getName() + ", " + element.getName()), e.getMessage()); //$NON-NLS-1$

		// delete files
		file.getProject().delete(true, new NullProgressMonitor());
		file2.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_checkEvidenceWithSamePathInAssessable_Different() {

		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element, "http://my.com"); //$NON-NLS-1$
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element, "http://myDiff.com"); //$NON-NLS-1$
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, element, "http://test.com"); //$NON-NLS-1$

		// test
		try {
			getPCMMEvidenceApp().checkEvidenceWithSamePathInAssessable("http://myEvidence.com", null, element); //$NON-NLS-1$
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}
}
