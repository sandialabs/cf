/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Creates an image badget
 * 
 * @author Didier Verstraete
 *
 */
public class ImageBadget extends CompositeImageDescriptor {

	@SuppressWarnings("javadoc")
	public enum BadgetPosition {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER;
	}

	private ImageData baseImageData;
	private ImageData badgetData;
	private BadgetPosition badgetPosition = BadgetPosition.CENTER;
	/**
	 * The resource manager
	 */
	private ResourceManager rscMgr;

	/**
	 * Constructor
	 * 
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 */
	public ImageBadget(ResourceManager rscMgr) {
		Assert.isNotNull(rscMgr);
		this.rscMgr = rscMgr;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image createImage() {
		if (baseImageData == null || badgetData == null)
			throw new IllegalArgumentException(RscTools.getString(RscConst.EX_IMAGE_BADGET_IMAGENULL));

		return rscMgr
				.createImage(ImageDescriptor.createFromImageDataProvider(zoom -> ImageBadget.this.getImageData(100)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void drawCompositeImage(final int width, final int height) {
		// this is to determine where the badget top left corner should go, relative to
		// the base image behind it
		int xPos = 0;
		int yPos = 0;

		switch (badgetPosition) {
		case TOP_LEFT:
			break;

		case TOP_RIGHT:
			xPos = baseImageData.width - badgetData.width;
			break;

		case BOTTOM_LEFT:
			yPos = baseImageData.height - badgetData.height;
			break;

		case BOTTOM_RIGHT:
			xPos = baseImageData.width - badgetData.width;
			yPos = baseImageData.height - badgetData.height;
			break;

		case CENTER:
			xPos = (baseImageData.width - badgetData.width) / 2;
			yPos = (baseImageData.height - badgetData.height) / 2;
			break;

		default:
			break;
		}

		drawImage(zoom -> baseImageData, 0, 0);
		drawImage(zoom -> badgetData, xPos, yPos);
	}

	/**
	 * @param overlayImagePosition the overlay position for the image
	 * @return the badget
	 */
	public ImageBadget getBadget(final BadgetPosition overlayImagePosition) {
		this.badgetPosition = overlayImagePosition;
		return this;
	}

	/**
	 * @param baseImageData the base image
	 * @return the base image
	 */
	public ImageBadget setBaseImageData(final ImageData baseImageData) {
		this.baseImageData = baseImageData;
		return this;
	}

	/**
	 * @param baseImage the base image
	 * @return the image badget
	 */
	public ImageBadget setBaseImage(final Image baseImage) {
		this.baseImageData = baseImage.getImageData();
		return this;
	}

	/**
	 * @param overlayImageData the overlay image
	 * @return the image badget
	 */
	public ImageBadget setbadgetData(final ImageData overlayImageData) {
		this.badgetData = overlayImageData;
		return this;
	}

	/**
	 * @param badgetImage the image badget
	 * @return the image badget
	 */
	public ImageBadget setBadgetImage(final Image badgetImage) {
		this.badgetData = badgetImage.getImageData();
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point getSize() {
		// The size of the composite image is determined by the maximum size between the
		// two building images,
		// although keep in mind that the base image always comes underneath the
		// overlaying one.
		return new Point(Math.max(baseImageData.width, badgetData.width),
				Math.max(baseImageData.height, badgetData.height));
	}

	/**
	 * @param rscMgr                the resource manager used to manage the
	 *                              resources (fonts, colors, images, cursors...)
	 * @param numberOfNotifications the number of notifications
	 * @return a red circle with a white bold number inside it. Does not cache the
	 *         final image.
	 */
	public static Image createNotification(ResourceManager rscMgr, int numberOfNotifications) {
		return createBadgetImage(rscMgr, 14, 14, numberOfNotifications,
				Display.getDefault().getSystemColor(SWT.COLOR_RED),
				Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	}

	/**
	 * @param rscMgr                the resource manager used to manage the
	 *                              resources (fonts, colors, images, cursors...)
	 * @param numberOfNotifications the number of notifications
	 * @param backgroundColor       the background color
	 * @param foregroundColor       the text color
	 * @return a circle with a white bold number inside it
	 */
	public static Image createBadget(ResourceManager rscMgr, int numberOfNotifications, final Color backgroundColor,
			final Color foregroundColor) {
		return createBadgetImage(rscMgr, 23, 23, numberOfNotifications, backgroundColor, foregroundColor);
	}

	/**
	 * Creates a red circle with a white bold number inside it. Does not cache the
	 * final image.
	 * 
	 * @param rscMgr                the resource manager used to manage the
	 *                              resources (fonts, colors, images, cursors...)
	 * @param width                 the width
	 * @param height                the height
	 * @param numberOfNotifications the number of notifications
	 * @param backgroundColor       the background color
	 * @param foregroundColor       the text color
	 * @return the image created.
	 */
	public static final Image createBadgetImage(ResourceManager rscMgr, int width, int height,
			final int numberOfNotifications, final Color backgroundColor, final Color foregroundColor) {

		// Parameters
		int fontSize = 100;
		int decorationWidth = width;
		String textToDraw = String.valueOf(numberOfNotifications);
		final int numberLength = Integer.toString(numberOfNotifications).length();

		if (numberLength > 3) {
			// spetrila, 2014.12.17: - set a width that fits the text
			// - smaller height since we will have a rounded rectangle and not a circle
			// - smaller font size so the new text will fit(set to 999+) if we have
			// a number of notifications with more than 3 digits
			decorationWidth += numberLength * 2;
			height -= 4;

			fontSize = 80;
			textToDraw = "999+"; //$NON-NLS-1$
		} else if (numberLength > 2) {
			// spetrila, 2014.12.17: - set a width that fits the text
			// - smaller height since we will have a rounded rectangle and not a circle
			decorationWidth += numberLength * 1.5;
			height -= 4;
		}

		final Font font = new Font(Display.getDefault(), "Calibri", width / 2, SWT.BOLD); //$NON-NLS-1$

		final Image canvas = new Image(null, decorationWidth, height);

		final GC gc = new GC(canvas);

		gc.setAntialias(SWT.ON);
		gc.setAlpha(0);
		gc.fillRectangle(0, 0, decorationWidth, height);

		gc.setAlpha(255);
		gc.setBackground(backgroundColor);

		// spetrila, 2014.12.17: In case we have more than two digits in the number of
		// notifications,
		// we will change the decoration to a rounded rectangle so it can contain
		// all of the digits in the notification number
		if (decorationWidth == width)
			gc.fillOval(0, 0, decorationWidth - 1, height - 1);
		else
			gc.fillRoundRectangle(0, 0, decorationWidth, height, 10, 10);

		final FontData fontData = font.getFontData()[0];
		fontData.setHeight((int) (fontData.getHeight() * fontSize / 100.0 + 0.5));
		fontData.setStyle(SWT.BOLD);

		final Font newFont = new Font(Display.getCurrent(), fontData);

		gc.setFont(newFont);
		gc.setForeground(foregroundColor);

		final Point textSize = gc.stringExtent(textToDraw);
		final int xPos = (decorationWidth - textSize.x) / 2;
		final int yPos = (height - textSize.y) / 2;
		gc.drawText(textToDraw, xPos + 1, yPos, true);

		gc.dispose();

		final ImageData imgData = canvas.getImageData();

		// Remove white transparent pixels
		final int whitePixel = imgData.palette.getPixel(new RGB(255, 255, 255));
		imgData.transparentPixel = whitePixel;

		final Image finalImage = rscMgr.createImage(ImageDescriptor.createFromImageDataProvider(zoom -> imgData));

		canvas.dispose();
		font.dispose();
		newFont.dispose();

		return finalImage;
	}

}