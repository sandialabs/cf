/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets.filebrowser;

import java.io.File;
import java.io.FilenameFilter;

import gov.sandia.cf.parts.constants.PartsResourceConstants;

/**
 *
 * the filename filter class to show only directories
 * 
 * @author Didier Verstraete
 */
public class DirectoryFilterName implements FilenameFilter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(File dir, String name) {
		if (dir != null) {
			File tempFile = new File(dir.getPath() + PartsResourceConstants.PATH_SEP + name);
			return tempFile.isDirectory();
		}
		return false;
	}
}
