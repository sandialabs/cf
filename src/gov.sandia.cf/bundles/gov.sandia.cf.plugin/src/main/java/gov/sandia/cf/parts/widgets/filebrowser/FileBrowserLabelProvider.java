/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets.filebrowser;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import gov.sandia.cf.parts.constants.PartsResourceConstants;

/**
 * Provides the labels for directory browser
 * 
 * @author Didier Verstraete
 *
 */
public class FileBrowserLabelProvider extends LabelProvider implements IStyledLabelProvider {

	/**
	 * Image descriptor
	 */
	private ImageDescriptor directoryImage;
	/**
	 * the resource manager to get images
	 */
	private ResourceManager resourceManager;

	private static final String FILES_SIZE_FORMAT = "({0})"; //$NON-NLS-1$

	/**
	 * @param directoryImage the image descriptor
	 */
	public FileBrowserLabelProvider(ImageDescriptor directoryImage) {
		this.directoryImage = directoryImage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StyledString getStyledText(Object element) {
		if (element instanceof File) {
			File file = (File) element;
			StyledString styledString = new StyledString(getFileName(file));
			String[] files = file.list();
			if (files != null) {
				styledString.append(MessageFormat.format(FILES_SIZE_FORMAT, files.length),
						PartsResourceConstants.FILE_BROWSER_COUNTER_STYLER);
			}
			return styledString;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof File && ((File) element).isDirectory()) {
			return getResourceManager().createImage(directoryImage);
		}

		return super.getImage(element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if (resourceManager != null) {
			resourceManager.dispose();
			resourceManager = null;
		}
	}

	/**
	 * @return the resource manager
	 */
	protected ResourceManager getResourceManager() {
		if (resourceManager == null) {
			resourceManager = new LocalResourceManager(JFaceResources.getResources());
		}
		return resourceManager;
	}

	/**
	 * @param file
	 * @return the file path or name
	 */
	private String getFileName(File file) {
		String name = file.getName();
		return name.isEmpty() ? file.getPath() : name;
	}

}
