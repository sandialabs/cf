/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets.filebrowser;

import java.io.File;

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * Provides the directory browser content
 * 
 * @author Didier Verstraete
 *
 */
public class FileBrowserContentProvider implements ITreeContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof File) {
			File[] files = ((File) inputElement).listFiles(new DirectoryFilterName());
			if (files != null) {
				return files;
			}
		}
		return new Object[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof File) {
			return ((File) parentElement).listFiles(new DirectoryFilterName());
		}
		return new Object[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof File) {
			return ((File) element).getParentFile();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasChildren(Object element) {
		return element instanceof File && ((File) element).isDirectory();
	}

}
