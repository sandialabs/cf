/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import gov.sandia.cf.tools.RscTools;

/**
 * RoundedLabel component to display text within a circle
 * 
 * @author Didier Verstraete
 *
 */
public class RoundedLabel extends Canvas {

	private static final int MARGIN = 3;

	private String text = null;

	/**
	 * The constructor
	 * 
	 * @param parent the parent composite
	 * @param style  the style
	 */
	public RoundedLabel(Composite parent, int style) {
		super(parent, style);

		text = RscTools.empty();

		addPaintListener(e -> RoundedLabel.this.paintControl(e));
	}

	/**
	 * Paint the CircleLabel
	 * 
	 * @param e the event
	 */
	void paintControl(PaintEvent e) {
		Canvas canvas = (Canvas) e.widget;
		int width = canvas.getBounds().width;
		int height = canvas.getBounds().height;
		e.gc.fillRoundRectangle(0, 0, width - 2, height - 2, 15, 15);
		e.gc.setBackground(getBackground());
		e.gc.drawText(text, 2 * MARGIN, 0);
	}

	/**
	 * @return the current text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the current text
	 * 
	 * @param text the label text
	 */
	public void setText(String text) {
		this.text = text;
		redraw();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		redraw();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		GC gc = new GC(this);

		Point pt = gc.stringExtent(text);

		gc.dispose();

		return new Point(pt.x + 4 * MARGIN, pt.y);
	}
}
