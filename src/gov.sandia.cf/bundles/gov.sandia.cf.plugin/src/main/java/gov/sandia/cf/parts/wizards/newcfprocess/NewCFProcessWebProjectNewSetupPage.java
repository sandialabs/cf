/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.TextWidget;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The Class NewCFProcessWebProjectNewSetupPage.
 *
 * @author Didier Verstraete
 */
public class NewCFProcessWebProjectNewSetupPage extends WizardPage implements INewCFProcessWebProjectNewSetupPage {
	/**
	 * the parent wizard
	 */
	private NewCFProcessWizard parent;

	/** The text application. */
	private TextWidget textApplication;

	/** The text contact. */
	private TextWidget textContact;

	/** The model. */
	private Model model;

	/**
	 * The constructor
	 * 
	 * @param parent the parent wizard
	 */
	public NewCFProcessWebProjectNewSetupPage(NewCFProcessWizard parent) {
		super(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_PAGENAME));
		setTitle(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_TITLE));
		setDescription(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_DESCRIPTION));

		this.parent = parent;

		this.model = new Model();
	}

	/** {@inheritDoc} */
	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);

		// label
		FormFactory.createLabel(container, RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_LBL));

		// label
		Label labelApplication = new Label(container, SWT.NONE);
		labelApplication
				.setText(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_APPLICATION_LBL));
		GridData gdlabelApplication = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelApplication.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		labelApplication.setLayoutData(gdlabelApplication);

		// text application
		textApplication = FormFactory.createTextWidget(this.parent.getResourceManager(), container, true, null);
		GridData gdTextApplication = new GridData(GridData.FILL_HORIZONTAL);
		textApplication.setLayoutData(gdTextApplication);
		textApplication.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				validateApplication();
			}
		});
		textApplication.setHelper(NotificationFactory.getNewInfo(
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_APPLICATION_HELPER_INFO)));

		// label
		Label labelContact = new Label(container, SWT.NONE);
		labelContact.setText(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_CONTACT_LBL));
		GridData gdlabelContact = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelContact.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		labelContact.setLayoutData(gdlabelContact);

		// text contact
		textContact = FormFactory.createTextWidget(this.parent.getResourceManager(), container, true, null);
		GridData gdTextContact = new GridData(GridData.FILL_HORIZONTAL);
		textContact.setLayoutData(gdTextContact);
		textContact.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				validateContact();
			}
		});

		setControl(container);
	}

	/**
	 * Validate application field.
	 */
	private void validateApplication() {

		textApplication.clearHelper();

		if (StringUtils.isEmpty(textApplication.getValue())) {
			textApplication.setHelper(NotificationFactory.getNewWarning(
					RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_APPLICATION_HELPER_WARN)));
		}

		model.setApplication(textApplication.getValue());
		setPageComplete(isPageValid());
	}

	/**
	 * Validate contact field.
	 */
	private void validateContact() {
		model.setContact(textContact.getValue());
		setPageComplete(isPageValid());
	}

	@Override
	public IWizardPage getPreviousPage() {
		return parent.getPageWebProjectType();
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
		return model != null && model.getApplication() != null;
	}

	@Override
	public Model getModel() {
		return model;
	}

}
