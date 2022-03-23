/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.evidencestruct;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * The newWizard extensions point to create a new credibility evidence folder
 * structure from the project explorer
 * 
 * @author Didier Verstraete
 *
 */
public class CFEvidenceStructureWizard extends Wizard implements INewWizard {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(CFEvidenceStructureWizard.class);

	/**
	 * the page to select parent project and create credbility evidence structure
	 * folder
	 */
	private CFEvidenceStructurePage pageCredibilityStructureFolder;

	/**
	 * The constructor
	 */
	public CFEvidenceStructureWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		addPage(pageCredibilityStructureFolder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getWindowTitle() {
		return RscTools.getString(RscConst.MSG_NEWCFFOLDERSTRUCTUREWIZARD_WINDOWTITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		pageCredibilityStructureFolder = new CFEvidenceStructurePage(selection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {

		IPath containerFullPath = pageCredibilityStructureFolder.getContainerFullPath();

		if (pageCredibilityStructureFolder.isPageComplete() && containerFullPath != null
				&& !containerFullPath.isEmpty()) {
			try {
				File evidenceStructureFile = new File(
						WorkspaceTools.getStaticFilePath(FileTools.FILE_CREDIBILITY_EVIDENCE_FOLDER_STRUCTURE));
				WorkspaceTools.createFolderStructure(containerFullPath, evidenceStructureFile);
			} catch (CredibilityException | URISyntaxException | IOException e) {
				logger.error("Error while creating credibility evidence folder structure: {}", e.getMessage(), e); //$NON-NLS-1$
				MessageDialog.openError(getShell(), RscTools.getString(RscConst.MSG_EVIDFOLDERSTRUCT_TITLE),
						RscTools.getString(RscConst.ERR_EVIDFOLDERSTRUCT) + e.getMessage());
			}

			return true;
		}

		return false;
	}

}
