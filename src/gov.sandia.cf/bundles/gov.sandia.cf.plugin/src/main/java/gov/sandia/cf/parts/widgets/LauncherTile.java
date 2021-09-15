/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.CursorTools;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.tools.ColorTools;

/**
 * A launcher tile to display some content
 * 
 * @author Didier Verstraete
 *
 */
public class LauncherTile extends Composite {

	private CLabel imageLabel;
	private StyledText label;

	private String id;
	private String text;
	private String iconName;
	private Image iconImage;
	private Color backgroundColor;
	private boolean grayedInactive;

	/**
	 * The view manager
	 */
	private IViewManager viewMgr;

	/**
	 * Private constructor to do not permit instantiation except from the Launcher
	 * 
	 * @param viewMgr    the view manager
	 * @param parent     the parent composite
	 * @param style      the SWT style
	 * @param id         the id to display
	 * @param text       the text to display
	 * @param iconName   the icon name
	 * @param background the background color
	 * @param active     is active? or not?
	 */
	LauncherTile(IViewManager viewMgr, Composite parent, int style, final String id, final String text,
			final String iconName, final Color background, final boolean enabled) {
		super(parent, SWT.FILL & style);

		Assert.isNotNull(viewMgr);
		this.viewMgr = viewMgr;
		this.id = id;
		this.text = text;
		this.iconName = iconName;
		this.backgroundColor = background;
		this.grayedInactive = true;

		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gridLayout = new GridLayout();
		this.setLayout(gridLayout);
		super.setEnabled(enabled);
		super.setBackground(backgroundColor);

		// paint the tile
		refresh();
	}

	/**
	 * Refresh the tile
	 */
	private void refresh() {

		// dispose composite children
		ViewTools.disposeChildren(this);

		// create image label
		imageLabel = new CLabel(this, SWT.NONE);
		imageLabel.setText(null);
		imageLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));

		// create text label
		label = new StyledText(this, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
		label.setText(text);
		label.setEditable(false);
		label.setEnabled(false);
		CursorTools.setCursor(viewMgr.getRscMgr(), label, SWT.CURSOR_ARROW);
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		FontTools.setButtonFont(viewMgr.getRscMgr(), label);

		Color textColor = null;
		Color selectionGrayedBackgroundTmp = ColorTools.grayedColor(backgroundColor, 30);
		Color inactiveGrayedBackgroundTmp = ColorTools.grayedColor(backgroundColor);
		Color clickGrayedBackgroundTmp = ColorTools.grayedColor(backgroundColor, 60);

		if (selectionGrayedBackgroundTmp.equals(inactiveGrayedBackgroundTmp)) {
			selectionGrayedBackgroundTmp = ColorTools.minusColor(backgroundColor, 10);
			inactiveGrayedBackgroundTmp = ColorTools.minusColor(backgroundColor, 20);
			clickGrayedBackgroundTmp = ColorTools.minusColor(backgroundColor, 30);
		}

		final Color selectionGrayedBackground = selectionGrayedBackgroundTmp;
		final Color inactiveGrayedBackground = inactiveGrayedBackgroundTmp;
		final Color clickGrayedBackground = clickGrayedBackgroundTmp;

		final PaintListener paintListener = e -> {
			Rectangle bounds = this.getBounds();
			e.gc.setAntialias(SWT.ON);
			e.gc.setLineWidth(4);
			e.gc.setForeground(backgroundColor);
			e.gc.drawRectangle(2, 2, bounds.width - 4, bounds.height - 4);
		};

		final Listener mouseOverListener = event -> {
			super.setBackground(selectionGrayedBackground);
			label.setBackground(selectionGrayedBackground);
		};
		final Listener mouseExitListener = event -> {
			super.setBackground(backgroundColor);
			label.setBackground(backgroundColor);
		};
		final Listener mouseUpListener = event -> this.notifyListeners(SWT.Selection, event);
		final Listener mouseDownListener = event -> {
			super.setBackground(clickGrayedBackground);
			label.setBackground(clickGrayedBackground);
		};

		if (isEnabled()) {
			// colors
			super.setBackground(backgroundColor);
			label.setBackground(backgroundColor);
			textColor = ConstantTheme.getAssociatedColor(backgroundColor);

			// listeners
			this.addPaintListener(paintListener);
			this.addListener(SWT.MouseEnter, mouseOverListener);
			this.addListener(SWT.MouseExit, mouseExitListener);
			this.addListener(SWT.MouseUp, mouseUpListener);
			this.addListener(SWT.MouseDown, mouseDownListener);
			CursorTools.setCursor(viewMgr.getRscMgr(), this, SWT.CURSOR_HAND);

			imageLabel.addListener(SWT.MouseEnter, mouseOverListener);
			imageLabel.addListener(SWT.MouseExit, mouseExitListener);
			imageLabel.addListener(SWT.MouseUp, mouseUpListener);
			imageLabel.addListener(SWT.MouseDown, mouseDownListener);

			label.addListener(SWT.MouseEnter, mouseOverListener);
			label.addListener(SWT.MouseExit, mouseExitListener);
			label.addListener(SWT.MouseUp, mouseUpListener);
			label.addListener(SWT.MouseDown, mouseDownListener);
		} else {
			if (grayedInactive) {
				super.setBackground(inactiveGrayedBackground);
				label.setBackground(inactiveGrayedBackground);
				textColor = ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GRAY);
			} else {
				super.setBackground(backgroundColor);
				label.setBackground(backgroundColor);
				textColor = ConstantTheme.getAssociatedColor(backgroundColor);
			}
		}

		iconImage = IconTheme.getIconImage(viewMgr.getRscMgr(), iconName, textColor, 50);

		// set image and text color
		imageLabel.setImage(iconImage);
		label.setForeground(textColor);

		// add resize control listener to change to image and text to only text
		this.addControlListener(new ControlAdapter() {

			boolean first = true;
			boolean small = false;
			Point imageLabelSize = null;
			Font font = null;

			@Override
			public void controlResized(final ControlEvent e) {
				if (first) {
					imageLabelSize = imageLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					small = imageLabelSize.y + label.getSize().y + 20 <= getSize().y;
					font = label.getFont();
					first = false;
				}
				if (imageLabelSize.y + label.getSize().y + 20 > getSize().y) {
					if (!small) {
						imageLabel.setImage(null);
						imageLabel.setVisible(false);
						imageLabel.setSize(0, 0);
						((GridLayout) getLayout()).numColumns = 2;
						label.setFont(font);
						FontTools.setBoldFont(viewMgr.getRscMgr(), label);
						small = true;
						layout();
					}
				} else if (small) {
					imageLabel.setImage(iconImage);
					imageLabel.setVisible(true);
					imageLabel.setSize(imageLabelSize);
					((GridLayout) getLayout()).numColumns = 1;
					label.setFont(font);
					FontTools.setButtonFont(viewMgr.getRscMgr(), label);
					small = false;
					layout();
				}
			}
		});

		this.layout();
	}

	@SuppressWarnings("javadoc")
	public Image getImage() {
		return imageLabel.getImage();
	}

	@SuppressWarnings("javadoc")
	public void setImage(Image imageLabel) {
		this.iconImage = imageLabel;

		// refresh the tile
		refresh();
	}

	@SuppressWarnings("javadoc")
	public String getId() {
		return id;
	}

	@SuppressWarnings("javadoc")
	public String getText() {
		return label.getText();
	}

	@SuppressWarnings("javadoc")
	public void setText(String label) {
		this.label.setText(label);

		// refresh the tile
		refresh();
	}

	@SuppressWarnings("javadoc")
	public boolean isGrayedInactive() {
		return grayedInactive;
	}

	@SuppressWarnings("javadoc")
	public void setGrayedInactive(boolean grayedInactive) {
		this.grayedInactive = grayedInactive;

		// refresh the tile
		refresh();
	}

	@Override
	public void setBackground(Color background) {
		super.setBackground(background);
		this.backgroundColor = background;

		// refresh the tile
		refresh();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		// refresh the tile
		refresh();
	}

}
