/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.nebula.widgets.progresscircle.ProgressCircle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.RandomTools;

/**
 * The PCMM circle Progression Bar composite
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMProgressBarCirclePart extends Composite {

	private PCMMSpecification configuration;
	private ResourceManager rscMgr;

	/**
	 * The constructor
	 * 
	 * @param parent        the parent view
	 * @param rscMgr        the resource manager
	 * @param configuration the configuration class
	 * @param style         the inherited style
	 */
	public PCMMProgressBarCirclePart(Composite parent, ResourceManager rscMgr, PCMMSpecification configuration,
			int style) {
		super(parent, style);

		Assert.isNotNull(rscMgr);
		this.rscMgr = rscMgr;

		this.configuration = configuration;

		// create part
		createPart();
	}

	/**
	 * Creates a new progression composite
	 */
	private void createPart() {
		this.setLayout(new GridLayout(2, false));

		GridData lblProgressBarGridData = new GridData(SWT.NONE, SWT.NONE, true, true, 1, 1);
		lblProgressBarGridData.grabExcessHorizontalSpace = true;
		lblProgressBarGridData.horizontalAlignment = SWT.LEFT;
		this.setLayoutData(lblProgressBarGridData);

		int progressCircleSize = 40;
		int progressCircleThickness = 8;
		boolean showText = false;

		if (configuration != null && configuration.getElements() != null) {

			for (PCMMElement element : configuration.getElements()) {

				// create progression
				Label label = new Label(this, SWT.CENTER);
				label.setText(element.getName());

				String maximumPattern = "%d/"; //$NON-NLS-1$
				ProgressCircle barCircle = new ProgressCircle(this, SWT.NONE);
				barCircle.setCircleSize(progressCircleSize);
				barCircle.setMaximum(3);
				barCircle.setSelection(Math.round((float) (RandomTools.getInt() * 3) / 10.0F));
				barCircle.setThickness(progressCircleThickness);
				barCircle.setShowText(showText);
				barCircle.setTextPattern(maximumPattern + barCircle.getMaximum());
				barCircle.setHighlightColor(ColorTools.toColor(rscMgr, element.getColor()));
			}
		}
	}
}
