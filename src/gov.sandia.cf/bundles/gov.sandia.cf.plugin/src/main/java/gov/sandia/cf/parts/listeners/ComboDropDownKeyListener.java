/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Combo;

import gov.sandia.cf.tools.RscTools;

/**
 * 
 * This key listener restricts the combo text field to combo item data
 * 
 * @author Didier Verstraete
 *
 */
public class ComboDropDownKeyListener implements KeyListener {

	private String selectedItem = RscTools.empty();
	private int index = -1;
	private boolean change = false;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyPressed(KeyEvent e) {

		Combo source = (Combo) e.getSource();

		if (source != null) {
			if (e.character == SWT.BS) {
				selectedItem = RscTools.empty();
				index = -1;
				change = true;
			} else if (Character.isLetterOrDigit(e.character)) {
				change = true;
				String[] items = source.getItems();
				String key = Character.toString(e.character);
				for (int i = 0; i < items.length; i++) {
					if (items[i].toLowerCase().startsWith(key.toLowerCase())) {
						selectedItem = items[i];
						index = i;
						return;
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (change) {

			Combo source = (Combo) e.getSource();

			if (source != null) {
				if (selectedItem.length() > 0) {
					source.select(index);
					source.setText(selectedItem);
				} else {
					source.setText(RscTools.empty());
					selectedItem = RscTools.empty();
					index = -1;
				}
			}
		}
		change = false;
	}
}
