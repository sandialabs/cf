/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.jupiter.api.Test;

import gov.sandia.cf.exceptions.CredibilityException;

/**
 * The Class StringUtilsTest.
 *
 * @author Didier Verstraete
 */
class RuntimeToolsTest {

	@Test
	void test_processBuilder_execSync() throws IOException, InterruptedException, ExecutionException, TimeoutException {
		ProcessBuilder builder = new ProcessBuilder();
		if (SystemTools.isWindows()) {
			builder.command("cmd.exe", "/c", "dir"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			builder.command("sh", "-c", "ls"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		Process process = builder.start();
		StringBuilder strBuilder = new StringBuilder();
		StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), strBuilder::append);
		Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
		int exitCode = process.waitFor();
		assert exitCode == 0;
		future.get(10, TimeUnit.SECONDS);

		assertTrue(strBuilder.length() > 0);
	}

	@Test
	void test_execSync()
			throws IOException, InterruptedException, ExecutionException, TimeoutException, CredibilityException {
		ProcessBuilder builder = new ProcessBuilder();
		if (SystemTools.isWindows()) {
			builder.command("cmd.exe", "/c", "dir"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			builder.command("sh", "-c", "ls"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		StringBuilder infoLog = new StringBuilder();
		StringBuilder errorLog = new StringBuilder();

		RuntimeTools.execute(errorLog, infoLog, builder, new NullProgressMonitor());

		assertTrue(infoLog.length() > 0);
		assertTrue(errorLog.length() <= 0);
	}
}
