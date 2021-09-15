/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.tools;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;

import gov.sandia.cf.parts.constants.PartsResourceConstants;

/**
 * 
 * The Font tools class
 * 
 * @author Didier Verstraete
 *
 */
public class FontTools {

	/**
	 * Private constructor to not allow instantiation.
	 */
	private FontTools() {
		// Do not instantiate
	}

	/**
	 * Set the font of the control in parameter to bold
	 * 
	 * @param rscMgr  the resource manager used to manage the resources (fonts,
	 *                colors, images, cursors...)
	 * @param control the control to update
	 */
	public static void setNormalFont(ResourceManager rscMgr, Control control) {
		if (control != null && !control.isDisposed() && rscMgr != null) {
			FontDescriptor descriptor = FontDescriptor.createFrom(control.getFont()).setStyle(SWT.NORMAL);
			control.setFont(rscMgr.createFont(descriptor));
		}
	}

	/**
	 * Set the font of the control in parameter to bold
	 * 
	 * @param rscMgr  the resource manager used to manage the resources (fonts,
	 *                colors, images, cursors...)
	 * @param control the control to update
	 */
	public static void setBoldFont(ResourceManager rscMgr, Control control) {
		if (control != null && !control.isDisposed() && rscMgr != null) {
			FontDescriptor descriptor = FontDescriptor.createFrom(control.getFont()).setStyle(SWT.BOLD);
			control.setFont(rscMgr.createFont(descriptor));
		}
	}

	/**
	 * Set the font of the control to the height in parameter
	 * 
	 * @param rscMgr  the resource manager used to manage the resources (fonts,
	 *                colors, images, cursors...)
	 * @param control the control to update
	 * @param height  the font height
	 */
	public static void setFontSizeTo(ResourceManager rscMgr, Control control, int height) {
		if (control != null && !control.isDisposed() && rscMgr != null) {
			FontDescriptor descriptor = FontDescriptor.createFrom(control.getFont()).setHeight(height);
			control.setFont(rscMgr.createFont(descriptor));
		}
	}

	/**
	 * Set the control with the title font
	 * 
	 * @param rscMgr  the resource manager used to manage the resources (fonts,
	 *                colors, images, cursors...)
	 * @param control the control to update
	 */
	public static void setTitleFont(ResourceManager rscMgr, Control control) {
		if (control != null && !control.isDisposed()) {
			FontData[] fD = control.getFont().getFontData();
			int height = fD[0].getHeight();
			setFontSizeTo(rscMgr, control, height + PartsResourceConstants.DEFAULT_TITLE_ADD_PIXEL_TO_FONT);
			setBoldFont(rscMgr, control);
		}
	}

	/**
	 * Set the control with the subtitle font
	 * 
	 * @param rscMgr  the resource manager used to manage the resources (fonts,
	 *                colors, images, cursors...)
	 * @param control the control to update
	 */
	public static void setSubtitleFont(ResourceManager rscMgr, Control control) {
		if (control != null && !control.isDisposed()) {
			FontData[] fD = control.getFont().getFontData();
			int height = fD[0].getHeight();
			setFontSizeTo(rscMgr, control, height + PartsResourceConstants.DEFAULT_SUBTITLE_ADD_PIXEL_TO_FONT);
			setBoldFont(rscMgr, control);
		}
	}

	/**
	 * Set important text font
	 * 
	 * @param rscMgr  the resource manager used to manage the resources (fonts,
	 *                colors, images, cursors...)
	 * @param control the control to update
	 */
	public static void setImportantTextFont(ResourceManager rscMgr, Control control) {
		if (control != null && !control.isDisposed()) {
			FontData[] fD = control.getFont().getFontData();
			int height = fD[0].getHeight();
			setFontSizeTo(rscMgr, control, height + PartsResourceConstants.DEFAULT_IMPORTANTTEXT_ADD_PIXEL_TO_FONT);
		}
	}

	/**
	 * Set the control with the button font
	 * 
	 * @param rscMgr  the resource manager used to manage the resources (fonts,
	 *                colors, images, cursors...)
	 * @param control the control to update
	 */
	public static void setButtonFont(ResourceManager rscMgr, Control control) {
		if (control != null && !control.isDisposed()) {
			FontData[] fD = control.getFont().getFontData();
			int height = fD[0].getHeight();
			setFontSizeTo(rscMgr, control, height + PartsResourceConstants.DEFAULT_BUTTON_ADD_PIXEL_TO_FONT);
			setBoldFont(rscMgr, control);
		}
	}

	/**
	 * @param rscMgr   the resource manager used to manage the resources (fonts,
	 *                 colors, images, cursors...)
	 * @param fondData the font data
	 * @return the font associated to the fontdata or create it.
	 */
	public static Font getFont(ResourceManager rscMgr, FontData fondData) {
		if (fondData != null && rscMgr != null) {
			return rscMgr.createFont(FontDescriptor.createFrom(fondData));
		}
		return null;
	}
}
