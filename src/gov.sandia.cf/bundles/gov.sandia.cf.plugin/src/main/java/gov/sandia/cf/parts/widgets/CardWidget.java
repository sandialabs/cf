/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import gov.sandia.cf.tools.ColorTools;

/**
 * Card Widget used in a Card container
 * 
 * @author Didier Verstraete
 *
 */
public class CardWidget extends Composite {

	/**
	 * Construct
	 * 
	 * @param parent the parent composite
	 * @param style  the style
	 */
	public CardWidget(Composite parent, int style) {
		super(parent, style | SWT.BORDER);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		setLayout(new GridLayout(1, true));
		setBackground(new Color(getDisplay(), ColorTools.DEFAULT_RGB_COLOR));
	}

}
