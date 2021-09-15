/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import gov.sandia.cf.tools.RscTools;

/**
 * CircleLabel component to display text within a circle
 * 
 * @author Didier Verstraete
 *
 */
public class CircleLabel extends Canvas {

	private static final int MARGIN = 1;

	private String text = null;

	/**
	 * The constructor
	 * 
	 * @param parent the parent composite
	 * @param style  the style
	 */
	public CircleLabel(Composite parent, int style) {
		super(parent, style);

		text = RscTools.empty();
		setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));

		addPaintListener(this::paintControl);
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
		int max = Math.max(width, height);
		e.gc.setBackground(getBackground());
		e.gc.setForeground(getBackground());
		e.gc.fillOval(0, 0, max, max);
		e.gc.setForeground(getForeground());
		Point textExtent = e.gc.textExtent(getText());
		e.gc.drawText(text, (canvas.getBounds().width / 2) - (textExtent.x / 2),
				(canvas.getBounds().height / 2) - (textExtent.y / 2), true);
	}

	/**
	 * @return the current text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text the text to set
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
	public void setForeground(Color color) {
		super.setForeground(color);
		redraw();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		GC gc = new GC(this);

		Point pt = gc.stringExtent(text);
		int max = Math.max(pt.x, pt.y);

		gc.dispose();

		return new Point(max + 4 * MARGIN, pt.y + 4 * MARGIN);
	}
}
