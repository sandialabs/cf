/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.listeners;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

import gov.sandia.cf.tools.MathTools;

/**
 * The float verify listener
 * 
 * @author Didier Verstraete
 *
 */
public class FloatVerifyListener implements VerifyListener {

	@Override
	public void verifyText(VerifyEvent e) {
		Text textToTest = (Text) e.getSource();

		// get old text and create new text by using the VerifyEvent.text
		final String oldString = textToTest.getText();
		String newString = oldString.substring(0, e.start) + e.text + oldString.substring(e.end);

		if (!StringUtils.isBlank(newString) && !MathTools.isFloat(newString)) {
			e.doit = false;
		}
	}

}
