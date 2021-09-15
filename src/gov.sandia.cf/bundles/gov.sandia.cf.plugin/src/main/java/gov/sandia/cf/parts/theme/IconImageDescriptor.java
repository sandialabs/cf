/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.theme;

import java.io.InputStream;
import java.util.Objects;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

/**
 * Descriptor for CF icon images. Creates the described icon on demand.
 *
 * @author Didier Verstraete
 */
public class IconImageDescriptor extends ImageDescriptor {

	private String iconName;

	private Color color;

	private int size;

	/**
	 * @param iconName the icon name
	 * @param color    the color associated
	 * @param size     the icon size
	 */
	public IconImageDescriptor(final String iconName, final Color color, final int size) {
		this.iconName = iconName;
		this.color = color != null ? color : ConstantTheme.getColor(IconTheme.ICON_COLOR_DEFAULT);
		this.size = size;
	}

	@Override
	public Object createResource(Device device) {

		// Get Image path
		String imagePath = IconTheme.getIconPath(iconName, color);

		// load image
		InputStream resourceAsStream = IconTheme.class.getClassLoader().getResourceAsStream(imagePath);
		
		if (resourceAsStream != null) {
			ImageData imageData = new ImageData(resourceAsStream);
			return new Image(device, imageData.scaledTo(size, size));
		} else {
			return super.getMissingImageDescriptor().createImage();
		}
	}

	@Override
	public void destroyResource(Object previouslyCreatedObject) {
		super.destroyResource(previouslyCreatedObject);
	}

	@Override
	public int hashCode() {
		return Objects.hash(color, iconName, size);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IconImageDescriptor) {
			IconImageDescriptor descr = (IconImageDescriptor) obj;
			return Objects.equals(color, descr.color) && Objects.equals(iconName, descr.iconName) && size == descr.size;
		}

		return false;
	}

	/**
	 * Creates a new IconImageDescriptor given the parameters.
	 *
	 * @param iconName the icon name
	 * @param color    the image color associated
	 * @param size     the image size
	 * @return a CursorResourceDescriptor that describes the given cursor
	 */
	public static IconImageDescriptor createFrom(final String iconName, final Color color, final int size) {
		return new IconImageDescriptor(iconName, color, size);
	}

}
