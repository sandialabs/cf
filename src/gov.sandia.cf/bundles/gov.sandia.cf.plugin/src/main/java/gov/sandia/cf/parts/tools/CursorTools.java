/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.tools;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;

import gov.sandia.cf.parts.theme.CursorDescriptor;

/**
 * 
 * The Cursor tools class
 * 
 * @author Didier Verstraete
 *
 */
public class CursorTools {

	/**
	 * Private constructor to not allow instantiation.
	 */
	private CursorTools() {
		// Do not instantiate
	}

	/**
	 * Set the cursor to the control.
	 * 
	 * @param rscMgr     the resource manager used to manage the resources (fonts,
	 *                   colors, images, cursors...)
	 * @param control    the control to update
	 * @param cursorType the cursor type
	 */
	public static void setCursor(ResourceManager rscMgr, Control control, final int cursorType) {
		if (control != null && !control.isDisposed() && rscMgr != null) {
			CursorDescriptor descriptor = CursorDescriptor.createFrom(cursorType);
			control.setCursor((Cursor) rscMgr.create(descriptor));
		}
	}
}
