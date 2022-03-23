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

import gov.sandia.cf.launcher.CFBackendConnectionType;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * A newWizard page to create a new credibility process with a text and a
 * FileDialog to select the credibility link file
 * 
 * @author Didier Verstraete
 *
 */
public class NewCFProcessBackendSelectionPage extends WizardPage implements INewCFProcessBackendSelectionPage {
	/**
	 * the parent wizard
	 */
	private NewCFProcessWizard parent;

	/** The backend connection type. */
	private CFBackendConnectionType backendConnectionType;

	/** The button local file. */
	private Button buttonLocalFile;

	/** The button web. */
	private Button buttonWeb;

	/**
	 * The constructor
	 * 
	 * @param parent the parent wizard
	 */
	public NewCFProcessBackendSelectionPage(NewCFProcessWizard parent) {
		super(RscTools.getString(RscConst.MSG_NEWCFPROCESS_BACKENDTYPE_PAGE_PAGENAME));
		setTitle(RscTools.getString(RscConst.MSG_NEWCFPROCESS_BACKENDTYPE_PAGE_TITLE));
		setDescription(RscTools.getString(RscConst.MSG_NEWCFPROCESS_BACKENDTYPE_PAGE_DESCRIPTION));

		this.parent = parent;

		this.backendConnectionType = null;
	}

	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);

		// label
		FormFactory.createLabel(container, RscTools.getString(RscConst.MSG_NEWCFPROCESS_BACKENDTYPE_PAGE_LBL));

		// backend type toggle button - Local Database
		buttonLocalFile = new Button(container, SWT.TOGGLE);
		buttonLocalFile.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		((GridData) buttonLocalFile.getLayoutData()).heightHint = 40;
		buttonLocalFile.setText(RscTools.getString(RscConst.MSG_NEWCFPROCESS_BACKENDTYPE_PAGE_LOCALFILE_BTN));

		// backend type toggle button - Web
		buttonWeb = new Button(container, SWT.TOGGLE);
		buttonWeb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		((GridData) buttonWeb.getLayoutData()).heightHint = 40;
		buttonWeb.setText(RscTools.getString(RscConst.MSG_NEWCFPROCESS_BACKENDTYPE_PAGE_WEB_BTN));

		// backend type toggle button - Local Database - Event
		buttonLocalFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				handleToggleButtonSelection(source.getSelection(), CFBackendConnectionType.FILE);
			}
		});

		// backend type toggle button - Web - Event
		buttonWeb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				handleToggleButtonSelection(source.getSelection(), CFBackendConnectionType.WEB);
			}
		});

		setControl(container);
	}

	/**
	 * Handle toggle button selection.
	 *
	 * @param selection   the selection
	 * @param backendType the backend type
	 */
	private void handleToggleButtonSelection(boolean selection, CFBackendConnectionType backendType) {

		Button source = null;
		Button otherButton = null;

		// get buttons
		if (CFBackendConnectionType.WEB.equals(backendType)) {
			source = buttonWeb;
			otherButton = buttonLocalFile;
		} else {
			source = buttonLocalFile;
			otherButton = buttonWeb;
		}

		// apply selection
		if (selection) {
			this.backendConnectionType = backendType;

			// set check icon
			source.setImage(IconTheme.getIconImage(this.parent.getResourceManager(), IconTheme.ICON_NAME_UPTODATE,
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN)));

			otherButton.setImage(null);
			otherButton.setSelection(false);
		} else {
			NewCFProcessBackendSelectionPage.this.backendConnectionType = null;

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
		return parent.getPageNewCredibilityFile();
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
		return this.backendConnectionType != null;
	}

	@Override
	public boolean isLocalFile() {
		return CFBackendConnectionType.FILE.equals(this.backendConnectionType);
	}

	@Override
	public boolean isWeb() {
		return CFBackendConnectionType.WEB.equals(this.backendConnectionType);
	}
}
