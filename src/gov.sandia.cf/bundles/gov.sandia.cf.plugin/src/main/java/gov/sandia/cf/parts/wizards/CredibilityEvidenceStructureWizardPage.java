/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards;

import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.misc.ContainerSelectionGroup;

import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * A newWizard page to create a new credibility process extending the eclipse
 * WizardNewFileCreationPage class
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("restriction")
public class CredibilityEvidenceStructureWizardPage extends WizardPage implements Listener {

	// sizing constants
	private static final int SIZING_CONTAINER_GROUP_HEIGHT = 250;

	private static final int SIZING_SELECTION_PANE_WIDTH = 320;

	private static final int SIZING_SELECTION_PANE_HEIGHT = 300;

	private IStructuredSelection currentSelection;

	// widgets
	private ContainerSelectionGroup containerGroup;

	private Text containerNameField;

	/**
	 * Creates a new folder creation wizard page. If the initial resource selection
	 * contains exactly one container resource then it will be used as the default
	 * container resource.
	 *
	 * @param selection the current resource selection
	 */
	public CredibilityEvidenceStructureWizardPage(IStructuredSelection selection) {
		super(RscTools.getString(RscConst.MSG_NEWCFFOLDERSTRUCTUREWIZARD_PAGENAME));
		setTitle(RscTools.getString(RscConst.MSG_NEWCFFOLDERSTRUCTUREWIZARD_PAGETITLE));
		setDescription(RscTools.getString(RscConst.MSG_NEWCFFOLDERSTRUCTUREWIZARD_PAGEDESC));
		this.currentSelection = selection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(Composite parent) {// top level group
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));

		HelpTools.addContextualHelp(composite, ContextualHelpId.PLANNING);

		containerGroup = new ContainerSelectionGroup(composite, this, false,
				IDEWorkbenchMessages.WizardNewFolderMainPage_folderLabel, false, SIZING_CONTAINER_GROUP_HEIGHT,
				SIZING_SELECTION_PANE_WIDTH);

		containerNameField = new Text(containerGroup, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = SIZING_SELECTION_PANE_HEIGHT;
		containerNameField.setLayoutData(gd);
		containerNameField.setFont(this.getFont());
		containerNameField.setEnabled(false);

		initializePage();
		validatePage();

		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
	}

	/**
	 * Initializes this page's controls.
	 */
	protected void initializePage() {

		Iterator<?> it = currentSelection.iterator();

		if (it.hasNext()) {

			// get the resource
			Object next = it.next();
			IResource selectedResource = Adapters.adapt(next, IResource.class);

			// set the resource project
			containerGroup.setSelectedContainer(getResourceParent(selectedResource));

			// validate page
			setPageComplete(validatePage());
		}
	}

	/**
	 * @param selectedResource
	 * @return the resource parent project (IContainer).
	 */
	private IContainer getResourceParent(IResource selectedResource) {

		IContainer project = null;

		if (selectedResource != null) {
			if (selectedResource.getType() == IResource.FILE) {
				selectedResource = selectedResource.getParent();
			}

			if (selectedResource.isAccessible()) {

				IResource initial = ResourcesPlugin.getWorkspace().getRoot().findMember(selectedResource.getFullPath());

				if (initial != null) {
					if (initial instanceof IContainer) {
						// if it is the container
						project = (IContainer) initial;
					} else {
						// if it is a child get the parent
						project = initial.getParent();
					}
				}
			}
		}

		return project;
	}

	/**
	 * Returns whether this page's controls currently all contain valid values.
	 *
	 * @return <code>true</code> if all controls are valid, and <code>false</code>
	 *         if at least one is invalid
	 */
	protected boolean validatePage() {
		return getContainerFullPath() != null && !getContainerFullPath().isEmpty();
	}

	/**
	 * Creates a container resource handle for the container with the given
	 * workspace path. This method does not create the resource.
	 *
	 * @param containerPath the path of the container resource to create a handle
	 *                      for
	 * @return the new container resource handle
	 */
	protected IContainer createContainerHandle(IPath containerPath) {
		if (containerPath.segmentCount() == 1)
			return IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getProject(containerPath.segment(0));
		return IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getFolder(containerPath);
	}

	/**
	 * The <code>CredibilityEvidenceStructureWizardPage</code> implementation of
	 * this <code>Listener</code> method handles all events and enablements for
	 * controls on this page. Subclasses may extend.
	 */
	@Override
	public void handleEvent(Event ev) {
		setPageComplete(validatePage());
		String path = RscTools.empty();
		if (containerGroup != null && containerGroup.getContainerFullPath() != null) {
			path = containerGroup.getContainerFullPath().toPortableString();
		}
		containerNameField.setText(path);
	}

	/**
	 * Sets whether this page is complete.
	 * <p>
	 * This information is typically used by the wizard to decide when it is okay to
	 * move on to the next page or finish up.
	 * </p>
	 *
	 * @param complete <code>true</code> if this page is complete, and
	 *                 <code>false</code> otherwise
	 * @see #isPageComplete()
	 */
	@Override
	public void setPageComplete(boolean complete) {
		super.setPageComplete(complete);
		if (isCurrentPage()) {
			getContainer().updateButtons();
		}
	}

	/**
	 * Returns the path of the currently selected container or null if no container
	 * has been selected.
	 *
	 * @return The path of the container, or <code>null</code>
	 */
	public IPath getContainerFullPath() {
		return containerGroup.getContainerFullPath();
	}

}
