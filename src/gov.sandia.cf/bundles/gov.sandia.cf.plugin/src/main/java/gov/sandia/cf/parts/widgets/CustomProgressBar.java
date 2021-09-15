/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.tools.RscTools;

/**
 * A custom progress bar with percent label
 * 
 * @author Didier Verstraete
 *
 */
public class CustomProgressBar extends Composite {

	private ProgressBar bar = null;

	private Label lblProgress = null;

	private boolean displayPercent = false;

	private String defaultValue = "  0"; //$NON-NLS-1$

	/**
	 * The constructor
	 * 
	 * 
	 * @param viewMgr        the view manager
	 * @param parent         the parent composite
	 * @param style          the style
	 * @param displayPercent boolean used to activate or not the percent label
	 */
	public CustomProgressBar(IViewManager viewMgr, Composite parent, int style, boolean displayPercent) {
		super(parent, style);
		this.displayPercent = displayPercent;

		Assert.isNotNull(viewMgr);

		// composite layout
		this.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		this.setLayout(gridLayout);

		// Composite for the progress
		bar = new ProgressBar(this, SWT.BORDER);
		GridData barProgressBarGridData = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		bar.setLayoutData(barProgressBarGridData);
		bar.setState(SWT.PAUSED);

		// display the percent label or not
		if (displayPercent) {
			lblProgress = new Label(this, SWT.NONE);
			lblProgress.setText(defaultValue + RscTools.PERCENT);
			FontTools.setImportantTextFont(viewMgr.getRscMgr(), lblProgress);
		}
	}

	/**
	 * Set the max value of the progress
	 * 
	 * @param max the progress max value
	 */
	public void setMaximum(int max) {
		bar.setMaximum(max);

		if (displayPercent) {
			updateLblProgressText();
		}
	}

	/**
	 * Set the current value of the progress
	 * 
	 * @param current the progress current value
	 */
	public void setCurrent(int current) {
		bar.setSelection(current);

		if (displayPercent) {
			updateLblProgressText();
		}
	}

	/**
	 * Update the label text
	 */
	private void updateLblProgressText() {
		String text = defaultValue + RscTools.PERCENT;
		if (bar.getMaximum() > 0 && bar.getSelection() > 0) {
			int percent = (int) (((float) bar.getSelection() / (float) bar.getMaximum()) * 100);
			text = percent + RscTools.PERCENT;
		}
		lblProgress.setText(text);
		lblProgress.requestLayout();
	}
}
