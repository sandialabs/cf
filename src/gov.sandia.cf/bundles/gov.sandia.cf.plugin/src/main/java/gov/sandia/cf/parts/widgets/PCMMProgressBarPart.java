/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.application.configuration.pcmm.PCMMSpecification;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.parts.ui.pcmm.PCMMViewManager;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The PCMM linear progress Bar composite
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMProgressBarPart extends Composite {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMProgressBarPart.class);

	/**
	 * The PCMM view manager
	 */
	private PCMMViewManager viewManager;

	/**
	 * The PCMM elements
	 */
	private List<PCMMElement> elements;

	/**
	 * The elements label map
	 */
	private Map<PCMMElement, Label> mapElementLabel;

	private Set<PCMMElement> overridedElementLabel;

	/**
	 * Set the decrease mode
	 */
	boolean isDecreasedMode = false;

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager
	 * @param parent      the parent view
	 * @param elements    the elements
	 * @param style       the style
	 */
	public PCMMProgressBarPart(PCMMViewManager viewManager, Composite parent, List<PCMMElement> elements, int style) {
		super(parent, style);
		this.viewManager = viewManager;
		this.elements = elements;
		this.mapElementLabel = new HashMap<>();
		this.overridedElementLabel = new HashSet<>();

		// create part
		createPart();
	}

	/**
	 * Creates the part components
	 */
	private void createPart() {

		// properties
		int compositeNbColumn = 3;
		int labelNbColumn = 2;
		int progressNbColumn = 1;
		int compositeMinWidth = 120;
		int progressMinWidth = 80;

		GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, true, 1, 1);
		gridData.minimumWidth = compositeMinWidth;
		this.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout(compositeNbColumn, false);
		this.setLayout(gridLayout);

		if (elements != null) {

			for (PCMMElement element : elements) {

				// create progress
				Label label = new Label(this, SWT.NULL);
				label.setText(element.getName());
				label.setToolTipText(element.getName());
				GridData lblProgressBarGridData = new GridData(SWT.FILL, SWT.NONE, false, true, labelNbColumn, 1);
				lblProgressBarGridData.horizontalAlignment = SWT.LEFT;
				label.setLayoutData(lblProgressBarGridData);

				mapElementLabel.put(element, label);

				// set the element name depending of the composite size
				this.addListener(SWT.Resize, new Listener() {
					@Override
					public void handleEvent(Event event) {

						int currentSize = (PCMMProgressBarPart.this.getSize().x - (gridLayout.marginWidth * 4));

						// compute element name size
						GC gc = new GC(label);
						int x = gc.textExtent(element.getName()).x;
						gc.dispose();

						if (currentSize < x + progressMinWidth) {
							overridedElementLabel.add(element);
						} else {
							overridedElementLabel.remove(element);
						}

						refreshElementNames();
					}
				});

				// compute progress
				int maxProgress = 0;
				int currentProgress = 0;
				PCMMSpecification configuration = viewManager.getCache().getPCMMSpecification();
				try {
					maxProgress = viewManager.getAppManager().getService(IPCMMApplication.class)
							.computeMaxProgress(configuration);
					currentProgress = viewManager.getAppManager().getService(IPCMMApplication.class)
							.computeCurrentProgressByElement(element, viewManager.getSelectedTag(), configuration);
				} catch (CredibilityException e) {
					logger.error(RscTools.getString(RscConst.ERR_HOMEVIEW_PCMM_PROGRESS_ERROR), e);
					MessageDialog.openError(getShell(), RscTools.getString(RscConst.MSG_PCMMHOME_DIALOG_TITLE),
							RscTools.getString(RscConst.ERR_HOMEVIEW_PCMM_PROGRESS_ERROR));
				}

				// create progress component
				CustomProgressBar bar = new CustomProgressBar(viewManager, this, SWT.NULL, false);
				GridData barProgressBarGridData = new GridData(SWT.FILL, SWT.NONE, true, false, progressNbColumn, 1);
				barProgressBarGridData.horizontalAlignment = SWT.RIGHT;
				barProgressBarGridData.minimumWidth = progressMinWidth;
				bar.setMaximum(maxProgress);
				bar.setCurrent(currentProgress);
				bar.setLayoutData(barProgressBarGridData);
			}

			// set min height for the progress composite
			Point computedSize = this.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			gridData.minimumHeight = computedSize.y;
		}
	}

	/**
	 * Refresh the element names (name or abbreviation)
	 */
	private void refreshElementNames() {
		if (overridedElementLabel.isEmpty() && isDecreasedMode) {
			increaseElementNames();
		} else if (!overridedElementLabel.isEmpty() && !isDecreasedMode) {
			decreaseElementNames();
		}
	}

	/**
	 * Put long name in PCMM element labels
	 */
	private void increaseElementNames() {
		if (mapElementLabel != null) {
			for (Entry<PCMMElement, Label> elementsTmp : mapElementLabel.entrySet()) {

				if (elementsTmp != null) {
					PCMMElement element = elementsTmp.getKey();
					Label label = elementsTmp.getValue();

					if (label != null) {
						if (element != null) {
							label.setText(element.getName());
						}
						label.requestLayout();
					}
				}
			}

			isDecreasedMode = false;
		}
	}

	/**
	 * Put abbreviation in PCMM Element labels
	 */
	private void decreaseElementNames() {

		if (mapElementLabel != null) {
			for (Entry<PCMMElement, Label> elementsTmp : mapElementLabel.entrySet()) {

				if (elementsTmp != null) {
					PCMMElement element = elementsTmp.getKey();
					Label label = elementsTmp.getValue();

					if (label != null) {
						if (element != null) {
							label.setText(element.getAbbreviation());
						}
						label.requestLayout();
					}
				}
			}

			isDecreasedMode = true;
		}
	}
}
