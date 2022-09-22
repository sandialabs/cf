/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.dialogs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.ui.IViewManager;

/**
 * Generic Credibility Dialog (resizable and scrollable)
 * 
 * @author Didier Verstraete
 *
 * @param <V> the view manager type
 */
public abstract class GenericCFScrolledDialog<V extends IViewManager> extends GenericCFSmallDialog<V> {

	private static final Logger logger = LoggerFactory.getLogger(GenericCFScrolledDialog.class);

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 */
	protected GenericCFScrolledDialog(V viewManager, Shell parentShell) {
		super(viewManager, parentShell);
		Assert.isNotNull(viewManager);
		Assert.isNotNull(parentShell);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		logger.debug("Create QoI Planning dialog area"); //$NON-NLS-1$

		Composite container = (Composite) super.createDialogArea(parent);

		// scroll container
		ScrolledComposite scrollContainer = new ScrolledComposite(container, SWT.H_SCROLL | SWT.V_SCROLL);
		GridData scrollScData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrollScData.widthHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_X;
		scrollScData.heightHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_Y;
		scrollContainer.setLayoutData(scrollScData);
		scrollContainer.setLayout(new GridLayout());

		// Select content type
		Composite content = createDialogScrolledContent(scrollContainer);
		Assert.isNotNull(content);

		// set scroll container size
		scrollContainer.setContent(content);
		scrollContainer.setExpandHorizontal(true);
		scrollContainer.setExpandVertical(true);
		scrollContainer
				.setMinSize(content.computeSize(PartsResourceConstants.DESCRIPTIVE_DIALOG_MIN_SIZE_X, SWT.DEFAULT));
		content.addListener(SWT.Resize, e -> scrollContainer
				.setMinSize(content.computeSize(PartsResourceConstants.DESCRIPTIVE_DIALOG_MIN_SIZE_X, SWT.DEFAULT)));
		content.addPaintListener(e -> scrollContainer
				.setMinSize(content.computeSize(PartsResourceConstants.DESCRIPTIVE_DIALOG_MIN_SIZE_X, SWT.DEFAULT)));

		// Load data
		loadDataAfterCreation();

		// Return Control
		return container;
	}

	/**
	 * Creates the default dialog scrolled content.
	 *
	 * @param parent the parent
	 * @return the composite
	 */
	protected Composite createDefaultDialogScrolledContent(Composite parent) {
		// form container
		Composite formContainer = new Composite(parent, SWT.NONE);
		GridData scData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scData.widthHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_X;
		scData.heightHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_Y;
		formContainer.setLayoutData(scData);
		GridLayout gridLayout = new GridLayout(2, false);
		formContainer.setLayout(gridLayout);

		return formContainer;
	}

	/**
	 * Creates the dialog scrolled content.
	 *
	 * @param parent the parent
	 * @return the composite
	 */
	protected abstract Composite createDialogScrolledContent(Composite parent);

	/**
	 * Load data (to be extended).
	 */
	protected void loadDataAfterCreation() {
	}

}
