/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.CFVariable;

/**
 * This class gives methods to interact with execution OS
 * 
 * @author Didier Verstraete
 *
 */
public class SystemTools {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SystemTools.class);

	/**
	 * WINDOWS string
	 */
	public static final String WINDOWS = "Windows"; //$NON-NLS-1$

	private static Clipboard clipboard = null;

	/**
	 * Private constructor to not allow instantiation.
	 */
	private SystemTools() {
	}

	/**
	 * 
	 * @param variable the CF variable to get
	 * @return the system variable resolved
	 */
	public static String get(final CFVariable variable) {
		if (variable == null) {
			return null;
		}

		String result = System.getProperty(variable.getCommand());
		if (result == null) {
			result = System.getenv(variable.getCommand());
		}
		return result != null ? result : RscTools.empty();
	}

	/**
	 * 
	 * @return true if the execution is Windows, otherwise false
	 */
	public static boolean isWindows() {
		String osName = get(CFVariable.OS_NAME);
		return osName != null && osName.startsWith(WINDOWS);
	}

	/**
	 * @return the hostname
	 */
	public static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.warn(e.getMessage());
		}
		return null;
	}

	/**
	 * Copy the text into the clipboard
	 * 
	 * @param text the text to copy
	 */
	public static void copyToClipboard(String text) {
		TextTransfer textTransfer = TextTransfer.getInstance();
		getClipBoard().setContents(new Object[] { text }, new Transfer[] { textTransfer });
	}

	/**
	 * @return the clipboard
	 */
	private static Clipboard getClipBoard() {
		if (SystemTools.clipboard == null) {
			SystemTools.clipboard = new Clipboard(Display.getCurrent());
		}
		return SystemTools.clipboard;
	}
}
