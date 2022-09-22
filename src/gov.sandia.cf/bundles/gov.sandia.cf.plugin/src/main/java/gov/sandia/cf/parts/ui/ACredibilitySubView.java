/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.model.Notification;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.tools.RscTools;

/**
 * Qn abstract class to force the credibility views to implement methods needed
 * for the view managers
 * 
 * @author Didier Verstraete
 *
 * @param <C> the view controller
 */
public abstract class ACredibilitySubView<C extends IViewController> extends ACredibilityView<C> {

	private static final Logger logger = LoggerFactory.getLogger(ACredibilitySubView.class);

	private Composite notificationComposite;
	private CLabel notificationLabel;
	private Notification notification;

	/**
	 * The constructor.
	 *
	 * @param viewController the view controller
	 * @param parent         the parent composite
	 * @param style          the style
	 */
	protected ACredibilitySubView(C viewController, Composite parent, int style) {
		super(viewController, parent, style);

		// breadcrumb
		super.createBreadcrumb(this);

		// notification header
		notificationComposite = new Composite(this, SWT.FILL);
		notificationComposite.setLayout(new GridLayout(1, false));
		notificationComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		notificationLabel = new CLabel(notificationComposite, SWT.CENTER);
		notificationLabel.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
		notificationLabel.setVisible(false);
		clearFlashMessage();
	}

	/**
	 * Sets the flash message.
	 *
	 * @param notification the new flash message
	 */
	public void setFlashMessage(Notification notification) {
		this.notification = notification;

		applyNotification();
	}

	/**
	 * Apply the notification to the helper widget
	 */
	private void applyNotification() {
		if (notificationLabel == null) {
			logger.warn("The helper has not been instantiated"); //$NON-NLS-1$
			return;
		}

		if (notification != null) {
			notificationComposite.setBackground(NotificationFactory
					.getNotificationBackgroundColor(getViewController().getViewManager().getRscMgr(), notification));
			notificationLabel.setText(String.join(RscTools.CARRIAGE_RETURN, notification.getMessages()));
			notificationLabel.setVisible(true);
			notificationLabel.setForeground(NotificationFactory
					.getNotificationColor(getViewController().getViewManager().getRscMgr(), notification));
			notificationLabel.setImage(NotificationFactory
					.getNotificationIcon(getViewController().getViewManager().getRscMgr(), notification));
			((GridData) notificationLabel.getLayoutData()).heightHint = notificationLabel.computeSize(SWT.DEFAULT,
					SWT.DEFAULT).y;
			FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), notificationLabel);

			notificationLabel.requestLayout();
			notificationLabel.getParent().requestLayout();
			this.requestLayout();
		} else {
			clearFlashMessage();
		}
	}

	/**
	 * Clear the flash message and remove it
	 */
	public void clearFlashMessage() {

		this.notification = null;

		if (notificationLabel == null) {
			logger.warn("The helper has not been instantiated"); //$NON-NLS-1$
			return;
		}

		notificationComposite.setBackground((Color) null);
		notificationLabel.setText(null);
		notificationLabel.setVisible(false);
		notificationLabel.setForeground(null);
		notificationLabel.setImage(null);
		((GridData) notificationLabel.getLayoutData()).heightHint = 0;

		notificationLabel.requestLayout();
		notificationLabel.getParent().requestLayout();
		this.requestLayout();
	}
}
