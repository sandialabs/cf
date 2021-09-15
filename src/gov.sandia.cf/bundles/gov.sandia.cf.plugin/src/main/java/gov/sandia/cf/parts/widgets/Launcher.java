/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.tools.ColorTools;

/**
 * A graphic composite displaying tiles to launch the application
 * 
 * @author Didier Verstraete
 *
 */
public class Launcher extends Composite {

	private Map<String, LauncherTile> tiles;
	/**
	 * The view manager
	 */
	private IViewManager viewMgr;

	/**
	 * Private constructor to force using the LauncherFactory
	 * 
	 * @param viewMgr the view manager
	 * @param parent  the parent composite
	 * @param style   the SWT style
	 */
	Launcher(IViewManager viewMgr, Composite parent, int style) {
		super(parent, style);

		Assert.isNotNull(viewMgr);
		this.viewMgr = viewMgr;

		tiles = new HashMap<>();

		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gdImage = new GridLayout();
		gdImage.makeColumnsEqualWidth = true;
		gdImage.numColumns = 1;
		gdImage.horizontalSpacing = 10;
		gdImage.verticalSpacing = 10;
		this.setLayout(gdImage);
		this.setBackground(new Color(getDisplay(), ColorTools.DEFAULT_RGB_COLOR));
		this.addControlListener(new LauncherControlAdapater());
	}

	/**
	 * Create a new tile. If the id already exists, replace the existing one by the
	 * new tile.
	 * 
	 * @param id         the id to get the tile
	 * @param text       the text to display
	 * @param iconName   the icon name
	 * @param background the background color
	 * @param active     is active? or not?
	 * @return the newly created tile
	 */
	public LauncherTile addTile(final String id, final String text, final String iconName, final Color background,
			final boolean active) {

		// if it already
		if (tiles.containsKey(id)) {
			tiles.get(id).dispose();
		}

		LauncherTile tile = new LauncherTile(viewMgr, this, SWT.NONE, id, text, iconName, background, active);
		tiles.put(id, tile);

		int numColumns = ((GridLayout) getLayout()).numColumns;
		if (tiles.size() > 1) {

			int newNumColumns = (int) Math.floor(Math.sqrt(tiles.size()));

			if (newNumColumns != numColumns) {
				// floor the square root of the tiles number:
				// 4 tiles => 2 columns, 9 tiles => 3 columns
				((GridLayout) getLayout()).numColumns = newNumColumns;
				this.layout();
			}
		} else if (numColumns != 1) {
			((GridLayout) getLayout()).numColumns = 1;
			this.layout();
		}

		return tile;
	}

	/**
	 * @return the list of tiles
	 */
	public List<LauncherTile> getTiles() {
		return (List<LauncherTile>) tiles.values();
	}

	/**
	 * @param text the index of the tile
	 * @return the tile for index text
	 */
	public LauncherTile getTile(String text) {
		return tiles.getOrDefault(text, null);
	}

	/**
	 * 
	 * The launcher control adapter to resize each tile
	 * 
	 * @author Didier Verstraete
	 *
	 */
	private class LauncherControlAdapater extends ControlAdapter {
		@Override
		public void controlResized(final ControlEvent e) {

			// compute width
			int maxWidth = 0;
			for (final Control c : getChildren()) {
				if (c instanceof Composite && ((Composite) c).getClientArea().width > maxWidth) {
					maxWidth = ((Composite) c).getClientArea().width;
				}
			}
			for (final Control c : getChildren()) {
				if (c instanceof Composite) {
					((GridData) ((Composite) c).getLayoutData()).widthHint = maxWidth;
				}
			}

			// compute height
			int maxHeight = 0;
			for (final Control c : getChildren()) {
				if (c instanceof Composite && ((Composite) c).getClientArea().height > maxHeight) {
					maxHeight = ((Composite) c).getClientArea().height;
				}
			}
			for (final Control c : getChildren()) {
				if (c instanceof Composite) {
					((GridData) ((Composite) c).getLayoutData()).heightHint = maxHeight;
				}
			}
		}
	}

}
