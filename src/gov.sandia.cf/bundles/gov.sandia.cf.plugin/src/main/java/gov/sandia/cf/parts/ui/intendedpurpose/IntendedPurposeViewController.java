/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.intendedpurpose;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.intendedpurpose.IIntendedPurposeApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.parts.ui.AViewController;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * Intended Purpose view controller: Used to control the Intended Purpose view
 * 
 * @author Didier Verstraete
 *
 */
public class IntendedPurposeViewController extends AViewController<IntendedPurposeViewManager, IntendedPurposeView>
		implements IIntendedPurposeViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(IntendedPurposeViewController.class);

	/**
	 * The object to manage
	 */
	private IntendedPurpose intendedPurpose;

	/**
	 * Instantiates a new intended purpose view controller.
	 *
	 * @param viewMgr the view mgr
	 */
	IntendedPurposeViewController(IntendedPurposeViewManager viewMgr) {
		super(viewMgr);
		super.setView(new IntendedPurposeView(this, SWT.NONE));
	}

	/**
	 * Reload data.
	 */
	void reloadData() {
		logger.debug("Reload Intended Purpose view"); //$NON-NLS-1$

		try {
			intendedPurpose = getViewManager().getAppManager().getService(IIntendedPurposeApp.class)
					.get(getViewManager().getCache().getModel());
		} catch (CredibilityException e) {
			logger.error("An error occured while loading the intended purpose", e); //$NON-NLS-1$
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE),
					e.getMessage());
		}

		getView().refreshContent();
	}

	/**
	 * @return the intended purpose
	 */
	IntendedPurpose getIntendedPurpose() {
		return intendedPurpose;
	}

	/**
	 * update if the new value changed and set description, otherwise do nothing.
	 *
	 * @param value the value
	 */
	void changedDescription(String value) {

		if (intendedPurpose == null || value == null || value.equals(intendedPurpose.getDescription())) {
			return;
		}

		// update
		intendedPurpose.setDescription(StringTools.removeNonPrintableChars(value));
		updateIntendedPurpose();
	}

	/**
	 * update if the new value changed and set reference, otherwise do nothing.
	 *
	 * @param value the value
	 */
	void changedReference(String value) {

		if (intendedPurpose == null || value == null || value.equals(intendedPurpose.getReference())) {
			return;
		}

		// update
		intendedPurpose.setReference(value);
		updateIntendedPurpose();
	}

	/**
	 * Update the intended purpose
	 */
	void updateIntendedPurpose() {

		if (intendedPurpose != null) {
			try {
				// update
				intendedPurpose = getViewManager().getAppManager().getService(IIntendedPurposeApp.class)
						.updateIntendedPurpose(getViewManager().getCache().getModel(), null, intendedPurpose,
								getViewManager().getCache().getUser());

				// set save state
				getViewManager().viewChanged();

			} catch (CredibilityException e) {
				logger.error("An error occured while updating the intended purpose", e); //$NON-NLS-1$
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE),
						e.getMessage());
			}
		}
	}
}
