/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.intendedpurpose;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.intendedpurpose.IIntendedPurposeApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.model.Notification;
import gov.sandia.cf.model.dto.EntityLockInfo;
import gov.sandia.cf.model.dto.IntendedPurposeDto;
import gov.sandia.cf.parts.constants.ViewMode;
import gov.sandia.cf.parts.ui.AViewController;
import gov.sandia.cf.parts.ui.ICredibilityView;
import gov.sandia.cf.tools.GsonTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.web.WebClientRuntimeException;
import gov.sandia.cf.web.WebEvent;
import gov.sandia.cf.web.WebNotification;
import gov.sandia.cf.web.WebNotificationMapper;
import gov.sandia.cf.web.WebappConstants;
import gov.sandia.cf.web.services.intendedpurpose.IIntendedPurposeWebClient;
import gov.sandia.cf.web.services.intendedpurpose.IntendedPurposeMapper;

/**
 * Intended Purpose view controller: Used to control the Intended Purpose view.
 *
 * @author Didier Verstraete
 */
public class IntendedPurposeWebViewController extends AViewController<IntendedPurposeViewManager>
		implements IIntendedPurposeViewController {

	/** the logger. */
	private static final Logger logger = LoggerFactory.getLogger(IntendedPurposeWebViewController.class);

	/** The view. */
	private IntendedPurposeWebView view;

	/** The intended purpose. */
	private IntendedPurpose intendedPurpose;

	/** The lock token. */
	private String lockToken;

	/** The locked. */
	private boolean locked;

	/** The lock info. */
	private EntityLockInfo lockInfo;

	/**
	 * Instantiates a new intended purpose view controller.
	 *
	 * @param viewMgr the view mgr
	 */
	public IntendedPurposeWebViewController(IntendedPurposeViewManager viewMgr) {
		super(viewMgr);
		this.view = new IntendedPurposeWebView(viewMgr, this, SWT.NONE);
		this.lockToken = null;
	}

	/**
	 * Reload intended purpose.
	 *
	 * @return the intended purpose reloaded
	 */
	IntendedPurpose reloadIntendedPurpose() {
		try {
			// get intended purpose
			intendedPurpose = view.getViewManager().getAppManager().getService(IIntendedPurposeApp.class)
					.get(view.getViewManager().getCache().getModel());

			if (getViewManager().isWebConnection()) {
				// is locked?
				locked = lockToken == null
						&& view.getViewManager().getAppManager().getService(IIntendedPurposeWebClient.class)
								.isLocked(view.getViewManager().getCache().getModel());

				if (locked) {
					lockView();
				} else {
					unlockView();
				}
			}

		} catch (CredibilityException e) {
			logger.error("An error occured while loading the intended purpose", e); //$NON-NLS-1$
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_TITLE),
					e.getMessage());
		} catch (WebClientRuntimeException e) {
			logger.error("An error occured while loading the intended purpose", e); //$NON-NLS-1$
			lockView();
		}

		return intendedPurpose;
	}

	/**
	 * Checks if is locked.
	 *
	 * @return true, if is locked
	 */
	boolean isLocked() {
		return locked;
	}

	/**
	 * Gets the intended purpose.
	 *
	 * @return the intended purpose
	 */
	IntendedPurpose getIntendedPurpose() {
		return intendedPurpose;
	}

	/**
	 * Do edit action.
	 * 
	 * Load the view in UPDATE mode.
	 */
	void doEditAction() {
		try {
			// CONCURRENCY SUPPORT: lock edition
			if (getViewManager().isWebConnection()) {
				// initialize lock with a stub value before request call to avoid view async
				// lock
				if (lockToken == null) { // reuse previous one
					lockToken = "MyLOCK"; //$NON-NLS-1$
					lockToken = getViewManager().getWebClient().getService(IIntendedPurposeWebClient.class).lock(
							getViewManager().getCache().getModel(), getViewManager().getCache().getUser().getUserID());
				}
			}

			// open the edition mode
			view.setViewMode(ViewMode.UPDATE);
			view.reload();

		} catch (WebClientRuntimeException e) {
			lockView();
			lockToken = null;
			logger.error(e.getMessage(), e);
		} catch (CredibilityException e) {
			view.displayWarning(RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_TITLE),
					RscTools.getString(RscConst.ERR_INTENDEDPURPOSE_LOCK) + RscTools.CARRIAGE_RETURN + e.getMessage());
			lockView();
			lockToken = null;
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Do cancel edit action.
	 */
	void doCancelEditAction() {
		try {
			// CONCURRENCY SUPPORT: lock edition
			if (getViewManager().isWebConnection()) {
				getViewManager().getWebClient().getService(IIntendedPurposeWebClient.class)
						.unlock(getViewManager().getCache().getModel(), lockToken);
				lockToken = null;
			}

			// return to the VIEW mode
			view.setViewMode(ViewMode.VIEW);
			view.reload();

		} catch (WebClientRuntimeException e) {
			lockView();
			logger.error(e.getMessage(), e);
		} catch (CredibilityException e) {
			view.displayWarning(RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_TITLE),
					RscTools.getString(RscConst.ERR_INTENDEDPURPOSE_UNLOCK) + RscTools.CARRIAGE_RETURN
							+ e.getMessage());
			lockView();
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Do edit done action.
	 * 
	 * Load the view in VIEW mode.
	 */
	void doEditDoneAction() {

		// update
		updateIntendedPurpose();

		if (lockToken != null) {
			try {
				view.getViewManager().getAppManager().getService(IIntendedPurposeWebClient.class)
						.unlock(getViewManager().getCache().getModel(), lockToken);
				lockToken = null;

			} catch (WebClientRuntimeException e) {
				logger.error(e.getMessage(), e);
			} catch (CredibilityException e) {
				view.displayWarning(RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_TITLE),
						RscTools.getString(RscConst.ERR_INTENDEDPURPOSE_LOCK) + RscTools.CARRIAGE_RETURN
								+ e.getMessage());
				logger.error(e.getMessage(), e);
			}
		}

		// return to the VIEW mode
		view.setViewMode(ViewMode.VIEW);
		view.reload();
	}

	/**
	 * Changed.
	 *
	 * @param intendedPurpose the intended purpose
	 * @return true, if successful
	 */
	boolean changed(IntendedPurpose intendedPurpose) {
		if (intendedPurpose == null) {
			return false;
		}
		return changedDescription(intendedPurpose.getDescription()) || changedReference(intendedPurpose.getReference());
	}

	/**
	 * Changed description.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	boolean changedDescription(String value) {
		return intendedPurpose != null && !StringUtils.equals(value, intendedPurpose.getDescription());
	}

	/**
	 * Changed reference.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	boolean changedReference(String value) {
		return intendedPurpose != null && !StringUtils.equals(value, intendedPurpose.getReference());
	}

	/**
	 * Update intended purpose.
	 */
	void updateIntendedPurpose() {

		// get the view data
		IntendedPurpose toUpdate = view.getViewData();

		if (intendedPurpose != null && changed(toUpdate)) {

			// populate
			intendedPurpose.setDescription(toUpdate.getDescription());
			intendedPurpose.setReference(toUpdate.getReference());

			try {
				// update
				intendedPurpose = view.getViewManager().getAppManager().getService(IIntendedPurposeApp.class)
						.updateIntendedPurpose(getViewManager().getCache().getModel(), lockToken, intendedPurpose,
								getViewManager().getCache().getUser());

				// set save state
				view.getViewManager().viewChanged();
				lockToken = null;

			} catch (CredibilityException e) {
				logger.error("An error occured while updating the intended purpose", e); //$NON-NLS-1$
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_TITLE),
						e.getMessage());
			}
		}
	}

	/**
	 * Quit the view.
	 */
	@Override
	public void quit() {
		if (ViewMode.UPDATE.equals(view.getViewMode())) {
			if (changed(view.getViewData())) {
				boolean update = view.displayQuestion(RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_TITLE),
						RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_DLG_QUIT_SAVEBEFOREEXIT));
				if (update) {
					doEditDoneAction();
				} else {
					doCancelEditAction();
				}
			} else {
				doCancelEditAction();
			}
		}
	}

	/**
	 * Gets the view.
	 *
	 * @return the view
	 */
	@Override
	public Control getViewControl() {
		return view;
	}

	/**
	 * Gets the view.
	 *
	 * @return the view
	 */
	@Override
	public ICredibilityView getView() {
		return view;
	}

	/**
	 * Gets the lock info.
	 *
	 * @return the lock info
	 */
	public EntityLockInfo getLockInfo() {
		return lockInfo;
	}

	/**
	 * Unlock view.
	 */
	void unlockView() {
		view.unlock();
	}

	/**
	 * Lock view.
	 */
	void lockView() {

		// cancel edition if someone is editing
		if (ViewMode.UPDATE.equals(view.getViewMode())) {
			doCancelEditAction();
		}

		view.lock();
	}

	@Override
	public void handleWebEvent(WebEvent e) {

		if (view == null || view.isDisposed()) {
			return; // TODO close connection before
		}

		if (WebappConstants.CF_WEB_CONST_MESSAGE.equals(e.id)) {
			String data = e.data.toString();
			WebNotification webNotification = GsonTools.getFromGson(data, WebNotification.class);
			view.getDisplay().syncExec(() -> {
				Notification notification = WebNotificationMapper.toNotification(webNotification);
				if (notification == null) {
					view.clearFlashMessage();
				} else {
					view.setFlashMessage(notification);
				}
			});
		} else if (WebappConstants.CF_WEB_CONST_LOCK.equals(e.id)) {

			lockInfo = GsonTools.getFromGson(e.data.toString(), EntityLockInfo.class);

			// lock the view if someone else is locking intended purpose
			if (lockToken == null) {
				view.getDisplay().syncExec(() -> {
					if (lockInfo == null) {
						unlockView();
					} else {
						lockView();
					}
				});
			}
		} else if (WebappConstants.CF_WEB_CONST_DATA_INTENDEDPURPOSE.equals(e.id)) {

			// reload the view if the data comes from the server
			if (lockToken == null) {
				IntendedPurposeDto intendedPurposeDto = GsonTools.getFromGson(e.data.toString(),
						IntendedPurposeDto.class);
				intendedPurpose = IntendedPurposeMapper.toApp(intendedPurposeDto);
				view.getDisplay().syncExec(() -> view.reload());
			}
		}
	}
}
