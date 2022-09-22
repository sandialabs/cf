/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ResourceManager;
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
import gov.sandia.cf.tools.RscTools;

/**
 * The abstract Helper widget
 * 
 * @author Didier Verstraete
 *
 */
public abstract class AHelperWidget extends Composite {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(AHelperWidget.class);

	private boolean editable;

	// alert message
	private CLabel textHelper;

	private Notification notification;

	/**
	 * The resource manager
	 */
	private ResourceManager rscMgr;

	/**
	 * The constructor
	 * 
	 * @param rscMgr   the resource manager used to manage the resources (fonts,
	 *                 colors, images, cursors...)
	 * @param parent   the composite parent
	 * @param style    the style
	 * @param editable is editable?
	 */
	protected AHelperWidget(ResourceManager rscMgr, Composite parent, int style, boolean editable) {
		super(parent, style);

		Assert.isNotNull(rscMgr);
		this.rscMgr = rscMgr;

		// view Manager
		this.editable = editable;

		// create widget
		logger.debug("Create select widget content"); //$NON-NLS-1$

		// layout data
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		this.setLayout(gridLayout);
		GridData gdContainer = new GridData(SWT.FILL, SWT.FILL, true, false);
		this.setLayoutData(gdContainer);
	}

	/**
	 * Create the helper
	 */
	public void createHelper() {
		createHelper(this, new GridData(SWT.FILL, SWT.TOP, true, false));
	}

	/**
	 * Create the helper
	 * 
	 * @param parent the parent composite
	 */
	public void createHelper(Composite parent) {
		createHelper(parent, new GridData(SWT.FILL, SWT.TOP, true, false));
	}

	/**
	 * Create the helper
	 * 
	 * @param dataLabel the grid data to set
	 */
	public void createHelper(GridData dataLabel) {
		createHelper(this, dataLabel);
	}

	/**
	 * Create the helper
	 * 
	 * @param parent    the parent composite
	 * @param dataLabel the grid data to set
	 */
	public void createHelper(Composite parent, GridData dataLabel) {

		// set helper
		textHelper = new CLabel(parent, SWT.WRAP);
		textHelper.setLayoutData(dataLabel);
		textHelper.setBackground(getBackground());
		clearHelper();
	}

	/**
	 * @return is editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @return the resource manager
	 */
	public ResourceManager getRscMgr() {
		return rscMgr;
	}

	/**
	 * Set an alert under the field
	 * 
	 * @param notification the notification to display
	 */
	public void setHelper(Notification notification) {

		this.notification = notification;

		applyNotification();
	}

	/**
	 * Set a message to the existing notification. If there is no notification,
	 * create an info notification.
	 * 
	 * @param message the message to display
	 */
	public void appendHelper(String message) {

		if (this.notification != null && this.notification.getMessages() != null) {
			this.notification.getMessages().add(message);
		} else {
			this.notification = NotificationFactory.getNewInfo(message);
		}

		applyNotification();
	}

	/**
	 * Apply the notification to the helper widget
	 */
	private void applyNotification() {
		if (textHelper == null) {
			logger.warn("The helper has not been instantiated"); //$NON-NLS-1$
			return;
		}

		if (notification != null) {
			textHelper.setText(String.join(RscTools.CARRIAGE_RETURN, notification.getMessages()));
			textHelper.setVisible(true);
			textHelper.setForeground(FormFactory.getNotificationColor(rscMgr, notification));
			textHelper.setImage(NotificationFactory.getNotificationIcon(rscMgr, notification));
			((GridData) textHelper.getLayoutData()).heightHint = textHelper.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;

			textHelper.requestLayout();
			textHelper.getParent().requestLayout();
			this.requestLayout();
		} else {
			clearHelper();
		}
	}

	/**
	 * Clear the helper and remove it
	 */
	public void clearHelper() {

		this.notification = null;

		if (textHelper == null) {
			logger.warn("The helper has not been instantiated"); //$NON-NLS-1$
			return;
		}

		textHelper.setText(null);
		textHelper.setVisible(false);
		textHelper.setForeground(null);
		textHelper.setImage(null);
		((GridData) textHelper.getLayoutData()).heightHint = 0;

		textHelper.requestLayout();
		textHelper.getParent().requestLayout();
		this.requestLayout();
	}

	/**
	 * @param <C> the helper widget class
	 * @return the control associated to the helper widget
	 */
	public abstract <C extends AHelperWidget> C getControl();

	/**
	 * @return the helper height
	 */
	public int getHelperHeight() {
		return textHelper != null ? ((GridData) textHelper.getLayoutData()).heightHint : 0;
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		if (textHelper != null) {
			textHelper.setBackground(getBackground());
		}
	}

	/**
	 * Gets the notification.
	 *
	 * @return the notification
	 */
	public Notification getNotification() {
		return notification;
	}
}
