/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The Class NewCFProcessWebProjectTypePage.
 *
 * @author Didier Verstraete
 */
public class NewCFProcessWebProjectTypePage extends WizardPage implements INewCFProcessWebProjectTypePage {
	/**
	 * the parent wizard
	 */
	private NewCFProcessWizard parent;

	/** The web project type. */
	private CFWebProjectType webProjectType;

	/** The button new web project. */
	private Button buttonNewWebProject;

	/** The button existing web project. */
	private Button buttonExistingWebProject;

	/**
	 * The constructor
	 * 
	 * @param parent the parent wizard
	 */
	public NewCFProcessWebProjectTypePage(NewCFProcessWizard parent) {
		super(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECTTYPE_PAGE_PAGENAME));
		setTitle(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECTTYPE_PAGE_TITLE));
		setDescription(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECTTYPE_PAGE_DESCRIPTION));

		this.parent = parent;

		this.webProjectType = null;
	}

	/** {@inheritDoc} */
	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);

		// label
		FormFactory.createLabel(container, RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECTTYPE_PAGE_LBL));

		// backend type toggle button - Local Database
		buttonNewWebProject = new Button(container, SWT.TOGGLE);
		buttonNewWebProject.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		((GridData) buttonNewWebProject.getLayoutData()).heightHint = 40;
		buttonNewWebProject.setText(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECTTYPE_PAGE_NEWPROJECT_BTN));

		// backend type toggle button - Web
		buttonExistingWebProject = new Button(container, SWT.TOGGLE);
		buttonExistingWebProject.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		((GridData) buttonExistingWebProject.getLayoutData()).heightHint = 40;
		buttonExistingWebProject
				.setText(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECTTYPE_PAGE_EXISTINGPROJECT_BTN));

		// backend type toggle button - Local Database - Event
		buttonNewWebProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				handleToggleButtonSelection(source.getSelection(), CFWebProjectType.NEW);
			}
		});

		// backend type toggle button - Web - Event
		buttonExistingWebProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				handleToggleButtonSelection(source.getSelection(), CFWebProjectType.EXISTING);
			}
		});

		setControl(container);
	}

	/**
	 * Handle toggle button selection.
	 *
	 * @param selection   the selection
	 * @param projectType the project type
	 */
	private void handleToggleButtonSelection(boolean selection, CFWebProjectType projectType) {

		Button source = null;
		Button otherButton = null;

		// get buttons
		if (CFWebProjectType.EXISTING.equals(projectType)) {
			source = buttonExistingWebProject;
			otherButton = buttonNewWebProject;
		} else {
			source = buttonNewWebProject;
			otherButton = buttonExistingWebProject;
		}

		// apply selection
		if (selection) {
			this.webProjectType = projectType;

			// set check icon
			source.setImage(IconTheme.getIconImage(this.parent.getResourceManager(), IconTheme.ICON_NAME_UPTODATE,
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN)));

			otherButton.setImage(null);
			otherButton.setSelection(false);
		} else {
			NewCFProcessWebProjectTypePage.this.webProjectType = null;

			source.setImage(null);
		}

		// layout
		source.requestLayout();
		otherButton.requestLayout();

		// refresh Next button
		setPageComplete(isPageValid());
	}

	@Override
	public IWizardPage getPreviousPage() {
		return parent.getPageWebSetup();
	}

	@Override
	public boolean isPageComplete() {
		return super.isPageComplete() && isPageValid();
	}

	/**
	 * Checks if is page valid.
	 *
	 * @return true, if is page valid
	 */
	private boolean isPageValid() {
		return this.webProjectType != null;
	}

	@Override
	public boolean isNewProject() {
		return CFWebProjectType.NEW.equals(this.webProjectType);
	}

	@Override
	public boolean isExistingProject() {
		return CFWebProjectType.EXISTING.equals(this.webProjectType);
	}

	@Override
	public CFWebProjectType getWebProjectType() {
		return webProjectType;
	}

}
