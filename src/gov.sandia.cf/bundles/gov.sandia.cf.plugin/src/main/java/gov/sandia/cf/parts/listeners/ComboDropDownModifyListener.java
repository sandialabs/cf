/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.listeners;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;

import gov.sandia.cf.tools.RscTools;

/**
 * 
 * This listener restrict combo modification except for combo items
 * 
 * @author Didier Verstraete
 *
 */
public class ComboDropDownModifyListener implements ModifyListener {

	private String previousItem;

	/**
	 * Constructor
	 */
	public ComboDropDownModifyListener() {
		previousItem = RscTools.empty();
	}

	/** {@inheritDoc} */
	@Override
	public void modifyText(ModifyEvent e) {

		Combo source = (Combo) e.getSource();

		if (source != null) {
			String inputText = source.getText();
			if (!previousItem.equals(inputText)) {
				int index = source.getSelectionIndex();
				if (index == -1) {
					previousItem = RscTools.empty();
					source.setText(RscTools.empty());
				} else {
					previousItem = source.getItem(index);
					source.setText(source.getItem(index));
					source.select(index);
				}
			}
		}
	}

}
