/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlotState;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.general.PieDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;

/**
 * 
 * A custom RingPlot inheritance to make JFreeChart simple labels displayed in
 * multi-line.
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMRingPlot extends RingPlot implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1872730759879238240L;

	/**
	 * The char separator
	 */
	public static final String CHAR_SEPARATOR = "\n"; //$NON-NLS-1$

	/**
	 * Creates a new plot for the specified dataset.
	 *
	 * @param dataset the dataset (<code>null</code> permitted).
	 */
	public PCMMRingPlot(PieDataset dataset) {
		super(dataset);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void drawSimpleLabels(Graphics2D g2, @SuppressWarnings("rawtypes") List keys, double totalValue,
			Rectangle2D plotArea, Rectangle2D pieArea, PiePlotState state) {

		Composite originalComposite = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

		Rectangle2D labelsArea = super.getSimpleLabelOffset().createInsetRectangle(pieArea);
		double runningTotal = 0.0;
		Iterator<?> iterator = keys.iterator();
		while (iterator.hasNext()) {
			Comparable<?> key = (Comparable<?>) iterator.next();
			boolean include;
			double v = 0.0;
			Number n = getDataset().getValue(key);
			if (n == null) {
				include = !getIgnoreNullValues();
			} else {
				v = n.doubleValue();
				include = getIgnoreZeroValues() ? v > 0.0 : v >= 0.0;
			}

			if (include) {
				runningTotal = runningTotal + v;
				// work out the mid angle (0 - 90 and 270 - 360) = right,
				// otherwise left
				double mid = getStartAngle()
						+ (getDirection().getFactor() * ((runningTotal - v / 2.0) * 360) / totalValue);

				Arc2D arc = new Arc2D.Double(labelsArea, getStartAngle(), mid - getStartAngle(), Arc2D.OPEN);
				int x = (int) arc.getEndPoint().getX();
				int y = (int) arc.getEndPoint().getY();

				PieSectionLabelGenerator myLabelGenerator = getLabelGenerator();
				if (myLabelGenerator == null) {
					continue;
				}
				String label = myLabelGenerator.generateSectionLabel(super.getDataset(), key);
				if (label == null) {
					continue;
				}
				g2.setFont(super.getLabelFont());
				FontMetrics fm = g2.getFontMetrics();
				Rectangle2D bounds = TextUtilities.getTextBounds(label, g2, fm);
				Rectangle2D out = super.getLabelPadding().createOutsetRectangle(bounds);
				Shape bg = ShapeUtilities.createTranslatedShape(out, x - bounds.getCenterX(), y - bounds.getCenterY());
				if (super.getLabelShadowPaint() != null && super.getShadowGenerator() == null) {
					Shape shadow = ShapeUtilities.createTranslatedShape(bg, super.getShadowXOffset(),
							super.getShadowYOffset());
					g2.setPaint(super.getLabelShadowPaint());
					g2.fill(shadow);
				}
				if (super.getLabelBackgroundPaint() != null) {
					g2.setPaint(super.getLabelBackgroundPaint());
					g2.fill(bg);
				}
				if (super.getLabelOutlinePaint() != null && super.getLabelOutlineStroke() != null) {
					g2.setPaint(super.getLabelOutlinePaint());
					g2.setStroke(super.getLabelOutlineStroke());
					g2.draw(bg);
				}

				g2.setPaint(super.getLabelPaint());
				g2.setFont(super.getLabelFont());

				// Custom implementation
				String[] splittedLabel = label.split(CHAR_SEPARATOR);
				if (splittedLabel != null && splittedLabel.length > 0) {
					int countLabel = -splittedLabel.length / 2;
					int labelHeight = (int) bounds.getHeight();
					int halfCount = (splittedLabel.length % 2 == 0) ? (labelHeight / 2) : 0;
					for (String subLabel : splittedLabel) {
						TextUtilities.drawAlignedString(subLabel, g2, x, (y + (countLabel * labelHeight) + (halfCount)),
								TextAnchor.CENTER);
						countLabel++;
					}
				}

			}
		}

		g2.setComposite(originalComposite);

	}
}
