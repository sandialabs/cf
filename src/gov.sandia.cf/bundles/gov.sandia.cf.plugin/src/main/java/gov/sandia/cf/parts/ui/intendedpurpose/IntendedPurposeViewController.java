/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.intendedpurpose;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IIntendedPurposeApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Intended Purpose view controller: Used to control the Intended Purpose view
 * 
 * @author Didier Verstraete
 *
 */
public class IntendedPurposeViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(IntendedPurposeViewController.class);

	/**
	 * The Intended Purpose view
	 */
	private IntendedPurposeView view;

	/**
	 * The object to manage
	 */
	private IntendedPurpose intendedPurpose;

	IntendedPurposeViewController(IntendedPurposeView view) {
		Assert.isNotNull(view);
		this.view = view;
	}

	/**
	 * @return the intended purpose reloaded
	 */
	IntendedPurpose reloadIntendedPurpose() {
		try {
			intendedPurpose = view.getViewManager().getAppManager().getService(IIntendedPurposeApp.class)
					.get(view.getViewManager().getCache().getModel());
		} catch (CredibilityException e) {
			logger.error("An error occured while loading the intended purpose", e); //$NON-NLS-1$
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE), e.getMessage());
		}
		return intendedPurpose;
	}

	/**
	 * @return the intended purpose
	 */
	IntendedPurpose getIntendedPurpose() {
		return intendedPurpose;
	}

	/**
	 * update if the new value changed and set description, otherwise do nothing.
	 */
	void changedDescription(String value) {

		if (intendedPurpose == null || value == null || value.equals(intendedPurpose.getDescription())) {
			return;
		}

		// update
		intendedPurpose.setDescription(value);
		updateIntendedPurpose();
	}

	/**
	 * update if the new value changed and set reference, otherwise do nothing.
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
				intendedPurpose = view.getViewManager().getAppManager().getService(IIntendedPurposeApp.class)
						.updateIntendedPurpose(intendedPurpose, view.getViewManager().getCache().getUser());

				// set save state
				view.getViewManager().viewChanged();

			} catch (CredibilityException e) {
				logger.error("An error occured while updating the intended purpose", e); //$NON-NLS-1$
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE),
						e.getMessage());
			}
		}
	}

}
