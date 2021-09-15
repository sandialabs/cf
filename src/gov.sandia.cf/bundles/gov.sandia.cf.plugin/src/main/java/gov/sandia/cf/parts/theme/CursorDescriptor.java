/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.theme;

import java.util.Objects;

import org.eclipse.jface.resource.DeviceResourceDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Device;

/**
 * Descriptor for a cursor. Creates the described cursor on demand.
 * 
 * @author Didier Verstraete
 */
public class CursorDescriptor extends DeviceResourceDescriptor {

	private int cursorType;

	/**
	 * Constructor. Min value for SWT cursor is SWT.CURSOR_ARROW and Max value is
	 * SWT.CURSOR_HAND. Otherwise default type is set to SWT.CURSOR_ARROW.
	 * 
	 * @param cursorType the SWT cursor type (Arrow, Hand...)
	 */
	public CursorDescriptor(final int cursorType) {
		this.cursorType = cursorType <= SWT.CURSOR_HAND && cursorType >= SWT.CURSOR_ARROW ? cursorType
				: SWT.CURSOR_ARROW;
	}

	@Override
	public Object createResource(Device device) {
		return new Cursor(device, cursorType);
	}

	@Override
	public void destroyResource(Object previouslyCreatedObject) {
		if (previouslyCreatedObject instanceof Cursor) {
			((Cursor) previouslyCreatedObject).dispose();
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(cursorType);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CursorDescriptor) {
			CursorDescriptor descr = (CursorDescriptor) obj;
			return descr.cursorType == cursorType;
		}

		return false;
	}

	/**
	 * Creates a new CursorDescriptor given the SWT cursor type.
	 *
	 * @param cursorType the SWT cursor type
	 * @return a CursorResourceDescriptor that describes the given cursor
	 */
	public static CursorDescriptor createFrom(final int cursorType) {
		return new CursorDescriptor(cursorType);
	}

}
