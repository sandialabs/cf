/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets.filebrowser;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * File Browser part
 * 
 * @author Didier Verstraete
 *
 */
public class FileBrowserPart extends Composite {

	/**
	 * FileBrowser tree
	 */
	private TreeViewer viewer;

	/**
	 * @param parent the parent composite
	 * @param style  the view style
	 */
	public FileBrowserPart(Composite parent, int style) {
		super(parent, style);
		createControls(parent);
	}

	/**
	 * Creates the view and controls
	 * 
	 * @param parent the parent composite
	 */
	public void createControls(Composite parent) {
		viewer = new TreeViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new FileBrowserContentProvider());
		viewer.setLabelProvider(
				new DelegatingStyledCellLabelProvider(new FileBrowserLabelProvider(createDirectoryImageDescriptor())));
	}

	/**
	 * Sets the input file for the file browser
	 * 
	 * @param inputFile must be a file (not null)
	 */
	public void setInput(File inputFile) {
		viewer.setInput(inputFile);
	}

	/**
	 * @return the selected File in the filebrowser
	 */
	public File getSelection() {
		if (viewer.getSelection() instanceof IStructuredSelection) {

			Object firstElement = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
			if (firstElement instanceof IAdaptable) {
				IProject project = ((IAdaptable) firstElement).getAdapter(IProject.class);
				if (project != null && project.getFullPath() != null && !project.getFullPath().isEmpty()) {
					return new File(WorkspaceTools.toOsPath(project.getFullPath()));
				}
			}
		}
		return null;
	}

	/**
	 * @return the icon associated to the directory in the treeviewer
	 */
	private ImageDescriptor createDirectoryImageDescriptor() {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(PartsResourceConstants.DIR_IMG_DESCRIPTOR);
	}

}
