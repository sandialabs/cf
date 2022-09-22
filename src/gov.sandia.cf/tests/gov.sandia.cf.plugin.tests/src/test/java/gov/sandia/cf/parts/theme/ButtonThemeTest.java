/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.Test;

import gov.sandia.cf.tools.ColorTools;

/**
 * The Class ButtonThemeTest.
 *
 * @author Didier Verstraete
 */
class ButtonThemeTest {

	@Test
	void defaultButton() {
		// Get parent
		Shell parent = new Shell();
		ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources(), parent);

		// Create button
		ButtonTheme bt = new ButtonTheme(resourceManager, parent, SWT.NONE);

		// Default
		assertEquals(bt.getBackground(),
				ColorTools.toColor(resourceManager, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
		assertEquals(bt.getForeground(),
				ColorTools.toColor(resourceManager, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));

		parent.dispose();
	}

	@Test
	void disabledButton() {

		// Get parent
		Shell parent = new Shell();
		ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources(), parent);

		// with customization
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("BTN_ID", "999"); //$NON-NLS-1$ //$NON-NLS-2$
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
			}
		};

		Map<String, Object> options = new HashMap<String, Object>();
		options.put(ButtonTheme.OPTION_TEXT, "My_button");//$NON-NLS-1$
		options.put(ButtonTheme.OPTION_DATA, data);
		options.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_ADD);
		options.put(ButtonTheme.OPTION_ICON_SIZE, 20);
		options.put(ButtonTheme.OPTION_LISTENER, listener);
		options.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
		options.put(ButtonTheme.OPTION_ENABLED, false);
		options.put(ButtonTheme.OPTION_OUTLINE, false);

		ButtonTheme bt = new ButtonTheme(resourceManager, parent, SWT.NONE, options);

		// Test disabled
		assertEquals(false, bt.getEnabled());
		assertNotEquals(ColorTools.toColor(resourceManager, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN)),
				bt.getBackground());
		bt.setEnabled(true);

		assertEquals("My_button", bt.getText());//$NON-NLS-1$
		assertEquals(ColorTools.toColor(resourceManager, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN)),
				bt.getBackground());
		assertEquals(
				ColorTools.toColor(resourceManager,
						ConstantTheme.getAssociatedColor(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN))),
				bt.getForeground());
		assertEquals(listener, bt.getListeners(SWT.Selection)[0]);
		assertEquals(data.get("BTN_ID"), bt.getData("BTN_ID"));//$NON-NLS-1$ //$NON-NLS-2$

		parent.dispose();
	}

	@Test
	void outlineButton() {
		// Get parent
		Shell parent = new Shell();
		ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources(), parent);

		// Text btn outline
		Map<String, Object> options = new HashMap<String, Object>();
		options.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
		options.put(ButtonTheme.OPTION_OUTLINE, true);

		ButtonTheme bt = new ButtonTheme(resourceManager, parent, SWT.NONE, options);
		assertEquals(ColorTools.toColor(resourceManager, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN)),
				bt.getForeground());
		assertEquals(
				ColorTools.toColor(resourceManager,
						ConstantTheme.getAssociatedColor(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN))),
				bt.getBackground());

		parent.dispose();
	}

	@Test
	void nullOptionsButton() {
		// Get parent
		Shell parent = new Shell();
		ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources(), parent);

		// Text btn outline
		Map<String, Object> options = new HashMap<String, Object>();
		options.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
		options.put(ButtonTheme.OPTION_OUTLINE, null);
		options.put(ButtonTheme.OPTION_ENABLED, null);
		options.put(ButtonTheme.OPTION_DATA, null);
		options.put(ButtonTheme.OPTION_ICON, null);

		ButtonTheme bt = new ButtonTheme(resourceManager, parent, SWT.NONE, options);
		assertEquals(true, bt.getEnabled());
		assertEquals(ColorTools.toColor(resourceManager, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN)),
				bt.getBackground());
		assertEquals(
				ColorTools.toColor(resourceManager,
						ConstantTheme.getAssociatedColor(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN))),
				bt.getForeground());

		parent.dispose();
	}

}