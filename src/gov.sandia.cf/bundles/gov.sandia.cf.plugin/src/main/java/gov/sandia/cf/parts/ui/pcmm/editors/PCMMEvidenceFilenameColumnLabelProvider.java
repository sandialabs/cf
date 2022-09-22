/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm.editors;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

import gov.sandia.cf.application.pcmm.IPCMMEvidenceApp;
import gov.sandia.cf.model.NotificationType;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.parts.ui.pcmm.PCMMEvidenceColumnLabelProvider;
import gov.sandia.cf.parts.ui.pcmm.PCMMEvidenceViewController;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * The PCMM Evidence filename column label provider
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMEvidenceFilenameColumnLabelProvider extends PCMMEvidenceColumnLabelProvider {

	/**
	 * The constructor.
	 *
	 * @param viewController the view controller
	 */
	public PCMMEvidenceFilenameColumnLabelProvider(PCMMEvidenceViewController viewController) {
		super(viewController);
	}

	@Override
	public String getText(Object element) {
		if (element != null) {
			if (element instanceof PCMMElement) {
				return ((PCMMElement) element).getName() != null ? ((PCMMElement) element).getName() : RscTools.empty();

			} else if (element instanceof PCMMSubelement) {

				return ((PCMMSubelement) element).getName() != null ? ((PCMMSubelement) element).getName()
						: RscTools.empty();

			} else if (element instanceof PCMMEvidence) {

				return ((PCMMEvidence) element).getName() != null ? ((PCMMEvidence) element).getName()
						: RscTools.empty();
			}
		}
		return RscTools.empty();
	}

	@Override
	public Image getImage(Object element) {
		// Initialize
		Image iconEvidence = null;
		boolean hasError = false;
		boolean hasWarning = false;

		if (element instanceof PCMMEvidence) {
			PCMMEvidence evidence = (PCMMEvidence) element;

			// Get notifications
			Map<NotificationType, List<String>> notificiations = getViewController().getViewManager().getAppManager()
					.getService(IPCMMEvidenceApp.class).getEvidenceNotifications(evidence, evidence.getId());

			// Get Error notifications
			if (notificiations.containsKey(NotificationType.ERROR)
					&& !notificiations.get(NotificationType.ERROR).isEmpty()) {
				hasError = true;
			} else if (notificiations.containsKey(NotificationType.WARN)
					&& !notificiations.get(NotificationType.WARN).isEmpty()) {
				// Get Warning notifications
				hasWarning = true;
			}

			// Check errors and warning
			if (hasError) {
				// Display error icon
				iconEvidence = FormFactory.getErrorIcon(getViewController().getViewManager().getRscMgr());
			} else if (hasWarning) {
				// Display warning icon
				iconEvidence = FormFactory.getWarningIcon(getViewController().getViewManager().getRscMgr());
			}
		}
		return iconEvidence;
	}

	@Override
	public String getToolTipText(Object element) {
		// Initialize
		boolean hasNotification = false;
		StringBuilder html = new StringBuilder();

		// Check is PCMMEvidence
		if (element instanceof PCMMEvidence) {
			// Initialize
			PCMMEvidence evidence = (PCMMEvidence) element;
			StringBuilder notificationsToString = new StringBuilder();

			// Get notifications
			Map<NotificationType, List<String>> notificiations = getViewController().getViewManager().getAppManager()
					.getService(IPCMMEvidenceApp.class).getEvidenceNotifications(evidence, evidence.getId());

			// Get Error notifications
			if (notificiations.containsKey(NotificationType.ERROR)
					&& !notificiations.get(NotificationType.ERROR).isEmpty()) {
				hasNotification = true;
				notificiations.get(NotificationType.ERROR).forEach(msg -> {
					notificationsToString.append("<div class=\"error\">"); //$NON-NLS-1$
					notificationsToString.append(StringTools.nl2br(msg));
					notificationsToString.append("</div>"); //$NON-NLS-1$
				});
			}
			// Get Warning notifications
			if (notificiations.containsKey(NotificationType.WARN)
					&& !notificiations.get(NotificationType.WARN).isEmpty()) {
				hasNotification = true;
				notificiations.get(NotificationType.WARN).forEach(msg -> {
					notificationsToString.append("<div class=\"warning\">"); //$NON-NLS-1$
					notificationsToString.append(StringTools.nl2br(msg));
					notificationsToString.append("</div>"); //$NON-NLS-1$
				});
			}

			// Check has notification
			if (hasNotification) {
				html.append("<html><body style='font-size: 14px'>"); //$NON-NLS-1$
				html.append("<style>"); //$NON-NLS-1$
				html.append(
						".error {color: #7d1a23;padding: 10px 10px;background: #eabcc0;margin: 10px;border-radius: 5px;font-family: sans-serif;}"); //$NON-NLS-1$
				html.append(
						".warning {color: #000000;padding: 10px 10px;background: #ffcd00;margin: 10px;border-radius: 5px;font-family: sans-serif;}"); //$NON-NLS-1$
				html.append("</style>"); //$NON-NLS-1$
				html.append(StringTools.nl2br(notificationsToString.toString()));
				html.append("</body></html>"); //$NON-NLS-1$
			}
		}
		return (hasNotification) ? html.toString() : null;
	}
}
