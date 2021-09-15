/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.tools;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;

/**
 * 
 * The Image tools class
 * 
 * @author Didier Verstraete
 *
 */
public class ImageTools {

	/**
	 * Private constructor to not allow instantiation.
	 */
	private ImageTools() {
		// Do not instantiate
	}

	/**
	 * Get or create the image.
	 * 
	 * @param rscMgr    the resource manager used to manage the resources (fonts,
	 *                  colors, images, cursors...)
	 * @param imageData the image data. If null return null.
	 * @return the image found in the resource manager or the newly created image.
	 */
	public static Image getImage(ResourceManager rscMgr, final ImageData imageData) {
		if (imageData != null && rscMgr != null) {

			// Get Image Resource
			ImageDescriptor descriptor = ImageDescriptor.createFromImageDataProvider(new ImageDataProvider() {
				@Override
				public ImageData getImageData(int zoom) {
					return imageData;
				}
			});

			return rscMgr.createImage(descriptor);
		}

		return null;
	}
}
