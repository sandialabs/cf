/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.IPCMMEvidenceRepository;
import gov.sandia.cf.dao.migration.tasks.Task_012_PCMMEvidenceValue_LongVarchar;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * The Class Task_012_PCMMEvidenceValue_LongVarcharTest.
 *
 * @author Didier Verstraete
 */
class Task_012_PCMMEvidenceValue_LongVarcharTest extends AbstractTestDao {

	/** the logger. */
	public static final Logger logger = LoggerFactory.getLogger(Task_012_PCMMEvidenceValue_LongVarcharTest.class);

	/**
	 * Test migration task not migrated.
	 */
	@Test
	void test_MigrationTask_NotMigrated() {

		// Needed to execute a migration task
		TestEntityFactory.getNewModel(getDaoManager());

		// test
		try {
			boolean changed = new Task_012_PCMMEvidenceValue_LongVarchar().execute(getDaoManager());
			assertTrue(changed);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}

		String longText = "What is Lorem Ipsum?\r\n" //$NON-NLS-1$
				+ "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " //$NON-NLS-1$
				+ "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " //$NON-NLS-1$
				+ "when an unknown printer took a galley of type and scrambled it to make a type specimen book. " //$NON-NLS-1$
				+ "It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. " //$NON-NLS-1$
				+ "It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, " //$NON-NLS-1$
				+ "and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.\r\n" //$NON-NLS-1$
				+ "\r\n" + "Why do we use it?\r\n" //$NON-NLS-1$ //$NON-NLS-2$
				+ "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. " //$NON-NLS-1$
				+ "The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', " //$NON-NLS-1$
				+ "making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, " //$NON-NLS-1$
				+ "and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, " //$NON-NLS-1$
				+ "sometimes on purpose (injected humour and the like).\r\n" + "\r\n" + "\r\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ "Where does it come from?\r\n" //$NON-NLS-1$
				+ "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. " //$NON-NLS-1$
				+ "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, " //$NON-NLS-1$
				+ "and going through the cites of the word in classical literature, discovered the undoubtable source. " //$NON-NLS-1$
				+ "Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, " //$NON-NLS-1$
				+ "written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. " //$NON-NLS-1$
				+ "The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.\r\n" //$NON-NLS-1$
				+ "\r\n" //$NON-NLS-1$
				+ "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. " //$NON-NLS-1$
				+ "Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, " //$NON-NLS-1$
				+ "accompanied by English versions from the 1914 translation by H. Rackham.\r\n" + "\r\n" //$NON-NLS-1$ //$NON-NLS-2$
				+ "Where can I get some?\r\n" //$NON-NLS-1$
				+ "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, " //$NON-NLS-1$
				+ "by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, " //$NON-NLS-1$
				+ "you need to be sure there isn't anything embarrassing hidden in the middle of text. " //$NON-NLS-1$
				+ "All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. " //$NON-NLS-1$
				+ "It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. " //$NON-NLS-1$
				+ "The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc."; //$NON-NLS-1$

		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null);
		evidence.setValue(longText);
		try {
			getDaoManager().getRepository(IPCMMEvidenceRepository.class).update(evidence);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}
}
