/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.urls.StandardPieURLGenerator;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.Rotation;
import org.jfree.util.UnitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.pcmm.PCMMSpecification;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PCMMAggregation;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * This class contains methods to create a new PCMM Chart
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMChartFactory {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMChartFactory.class);

	/**
	 * Chart sizes
	 */
	private static final int PCMMCHART_SIZE_DEFAULT = 800;
	private static final int PCMMSTAMP_SIZE_MIN = 120;
	/** PCMM CHART MIN SIZE */
	public static final int PCMMCHART_SIZE_MIN = 120;

	/**
	 * Chart plot label font sizes
	 */
	private static final int PLOT_FONT_SIZE_NAME = 10;
	private static final int PLOT_FONT_SIZE_ABBREVIATION = 8;
	private static final int PLOT_PADDING_BREAK = 5;

	/**
	 * the chart font
	 */
	private static final String CHART_FONT = "Cambria"; //$NON-NLS-1$
	private static final String JFREECHART_THEME = "JFree"; //$NON-NLS-1$

	/**
	 * Do not instantiate.
	 */
	private PCMMChartFactory() {
	}

	/**
	 * @param parent            the parent composite
	 * @param pcmmSpecification the pcmm specification
	 * @return the newly created PCMM wheel chart
	 */
	public static ChartComposite createPCMMWheelChart(Composite parent, PCMMSpecification pcmmSpecification) {

		/**
		 * Calculated plot label font sizes
		 */
		int plotNameSize = 0; // Calculated
		int plotAbbreviationSize = 0; // Calculated

		// check if the chart can be created
		boolean canCreateChart = true;

		if (parent == null || parent.isDisposed()) {
			logger.error("Impossible to create a chart with parent null or disposed"); //$NON-NLS-1$
			canCreateChart = false;
		}

		if (pcmmSpecification == null) {
			logger.error("Impossible to create a chart with pcmmSpecification null"); //$NON-NLS-1$
			canCreateChart = false;
		}

		Map<String, PCMMElement> elements = new LinkedHashMap<>();

		if (canCreateChart && pcmmSpecification.getElements() != null) {
			// Create not sorted map by name
			pcmmSpecification.getElements().forEach(element -> elements.put(element.getName(), element));
		} else {
			logger.debug("There is no elements in the PCMM Specification"); //$NON-NLS-1$
			canCreateChart = false;
		}

		logger.debug("Creating PCMM ring chart"); //$NON-NLS-1$

		if (canCreateChart) {
			// Create dataset
			DefaultPieDataset dataset = new DefaultPieDataset();

			// ring plot defintion
			PCMMRingPlot plot = new PCMMRingPlot(dataset); // custom ring plot
			plot.setStartAngle(90);
			plot.setDirection(Rotation.CLOCKWISE);
			plot.setInteriorGap(0.02);
			plot.setSectionOutlinesVisible(true);
			plot.setLabelGenerator(new StandardPieSectionLabelGenerator());
			plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
			plot.setToolTipGenerator(new StandardPieToolTipGenerator());
			plot.setURLGenerator(new StandardPieURLGenerator());

			// create chart
			JFreeChart chart = new JFreeChart(RscTools.empty(), JFreeChart.DEFAULT_TITLE_FONT, plot, false);
			new StandardChartTheme(JFREECHART_THEME).apply(chart);

			chart.setAntiAlias(true);
			chart.setTextAntiAlias(true);
			chart.setBorderVisible(false);

			// set first background color
			Color bgColor = new Color(parent.getDisplay(), ColorTools.DEFAULT_RGB_COLOR);
			java.awt.Color bgColorAwt = new java.awt.Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());
			chart.setBackgroundPaint(bgColorAwt);

			// initializing sections from configuration
			double elementValue = 1;
			for (PCMMElement element : elements.values()) {

				// set dataset name
				dataset.setValue(element.getName(), elementValue);
				// paint sections
				plot.setSectionPaint(element.getName(), ColorTools.stringRGBToAwtColor(element.getColor()));
				// paint section outlines
				plot.setSectionOutlinePaint(element.getName(), java.awt.Color.white);

			}

			// chart properties
			plot.setCircular(true);
			plot.setOutlineVisible(false);
			plot.setShadowPaint(null);
			plot.setSimpleLabels(true);
			double labelOffset = 0.14;
			plot.setSimpleLabelOffset(
					new RectangleInsets(UnitType.RELATIVE, labelOffset, labelOffset, labelOffset, labelOffset));
			plot.setLabelBackgroundPaint(null);
			plot.setLabelOutlinePaint(null);
			plot.setLabelShadowPaint(null);
			plot.setSectionDepth(0.55);
			plot.setSeparatorsVisible(false);
			plot.setIgnoreZeroValues(true);
			plot.setBackgroundPaint(null);
			plot.setToolTipGenerator((PieDataset arg0, @SuppressWarnings("rawtypes") Comparable arg1) -> {
				if (elements.containsKey(arg1) && elements.get(arg1) != null) {
					return elements.get(arg1).getName();
				}
				return null;
			});

			// labels
			plot.setLabelPaint(java.awt.Color.white);
			FontData fontData = parent.getFont().getFontData()[0];
			Map<TextAttribute, Integer> chartLabelFontAttributes = new HashMap<>();
			chartLabelFontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			Font chartLabelFont = new Font(fontData.getName(), Font.BOLD, PLOT_FONT_SIZE_NAME);
			chartLabelFont = chartLabelFont.deriveFont(chartLabelFontAttributes);
			plot.setLabelFont(chartLabelFont);
			plot.setLabelGap(0.02);
			plot.setMaximumLabelWidth(0.01);

			// Chart specific Composite to contain chart
			ChartComposite chartComposite = new ChartComposite(parent, SWT.NONE, chart, false, false, false, false,
					false);
			GridData gdChartComposite = new GridData(SWT.CENTER, SWT.CENTER, true, true);
			gdChartComposite.heightHint = PCMMCHART_SIZE_DEFAULT;
			gdChartComposite.widthHint = PCMMCHART_SIZE_DEFAULT;
			gdChartComposite.minimumWidth = PCMMCHART_SIZE_MIN;
			gdChartComposite.minimumHeight = PCMMCHART_SIZE_MIN;
			chartComposite.setLayoutData(gdChartComposite);
			chartComposite.setBackground(parent.getBackground());

			// Get max label size
			JPanel dummyPanel = new JPanel();
			for (PCMMElement element : elements.values()) {
				// Split labels
				String[] splittedLabels = element.getName().split(" "); //$NON-NLS-1$
				for (int i = 0; i < splittedLabels.length; i++) {
					int labelSize = dummyPanel.getFontMetrics(((RingPlot) chart.getPlot()).getLabelFont())
							.stringWidth(splittedLabels[i]);
					if (labelSize > plotNameSize) {
						plotNameSize = labelSize;
					}
				}
				int abbreviationSize = dummyPanel.getFontMetrics(((RingPlot) chart.getPlot()).getLabelFont())
						.stringWidth(element.getAbbreviation());
				if (abbreviationSize > plotAbbreviationSize) {
					plotAbbreviationSize = abbreviationSize;
				}
			}
			plotNameSize += PLOT_PADDING_BREAK;

			final int plotNameSizeFinal = plotNameSize;
			final int plotAbbreviationSizeFinal = plotAbbreviationSize;

			// Chart label generator depending of the size of the chart
			((RingPlot) chart.getPlot()).setLabelGenerator(new PieSectionLabelGenerator() {
				@Override
				public String generateSectionLabel(PieDataset arg0, @SuppressWarnings("rawtypes") Comparable arg1) {
					// Get sizes
					int plotSize = Math.min(chartComposite.getSize().x, chartComposite.getSize().y) / 4;

					// Check elements key
					if (elements.containsKey(arg1) && elements.get(arg1) != null) {
						if (plotSize < plotNameSizeFinal) {
							// Scaled font size
							double scale = ((double) plotSize / (double) plotAbbreviationSizeFinal);
							double sizeScaled = PLOT_FONT_SIZE_ABBREVIATION * scale;

							// Create font
							Font newChartLabelFont = new Font(fontData.getName(), Font.BOLD,
									(int) Math.ceil(sizeScaled));

							// Apply new font
							if (plot.getLabelFont().getSize() != newChartLabelFont.getSize()) {
								plot.setLabelFont(newChartLabelFont);
							}

							// Abbreviation
							return elements.get(arg1).getAbbreviation();
						} else {
							// Scaled font size
							double scale = ((double) plotSize / (double) plotNameSizeFinal);
							double sizeScaled = PLOT_FONT_SIZE_NAME * scale;

							// Create font
							Font newChartLabelFont = new Font(fontData.getName(), Font.BOLD,
									(int) Math.ceil(sizeScaled));

							// Apply new font
							if (plot.getLabelFont().getSize() != newChartLabelFont.getSize()) {
								plot.setLabelFont(newChartLabelFont);
							}

							// Full name
							String labelBreak = null;
							if (elements.get(arg1).getName() != null) {
								String[] labelArray = elements.get(arg1).getName().split(" "); //$NON-NLS-1$
								labelBreak = String.join(PCMMRingPlot.CHAR_SEPARATOR, labelArray);
							}
							return labelBreak;
						}
					}
					return null;
				}

				@Override
				@SuppressWarnings("rawtypes")
				public AttributedString generateAttributedSectionLabel(PieDataset arg0, Comparable arg1) {
					return null;
				}
			});

			return chartComposite;
		}

		return null;
	}

	/**
	 * @param parent          the parent composite
	 * @param configuration   the pcmm specification
	 * @param aggregationData the aggregation data
	 * @return a new PCMM Stamp chart composite with the data in parameter
	 * @throws CredibilityException if a parameter is not valid.
	 */
	public static ChartComposite createPCMMStampChart(Composite parent, PCMMSpecification configuration,
			Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregationData) throws CredibilityException {

		/**
		 * check if the chart can be created
		 */
		// check parent
		if (parent == null || parent.isDisposed()) {
			throw new CredibilityException("Impossible to create a chart with parent null or disposed"); //$NON-NLS-1$
		}

		// check configuration
		if (configuration == null) {
			throw new CredibilityException("Impossible to create a chart with pcmmSpecification null"); //$NON-NLS-1$
		} else {
			// check pcmm elements
			if (configuration.getElements() == null) {
				throw new CredibilityException("There is no elements in the PCMM Specification"); //$NON-NLS-1$
			}
		}

		// check data
		if (aggregationData == null) {
			throw new CredibilityException("Impossible to create a chart with aggregationData null"); //$NON-NLS-1$
		}

		logger.debug("Creating PCMM Stamp chart"); //$NON-NLS-1$

		// create the dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		int startAngle = 0;

		// initializing sections from configuration
		String serie = RscTools.getString(RscConst.MSG_PCMMSTAMP_SERIE_NAME);
		int maxValue = 0;
		// start angle for the chart
		int nbElements = configuration.getElements().size();
		if (nbElements != 0) {
			startAngle = 360 / nbElements;
		}

		// get the max value of the levels
		if (configuration.getLevelColors() != null) {
			PCMMLevelColor maxLevel = configuration.getLevelColors().values().stream()
					.max(Comparator.comparing(PCMMLevelColor::getCode)).orElseThrow(NoSuchElementException::new);
			maxValue = maxLevel != null && maxLevel.getCode() != null ? maxLevel.getCode() : 0;
		}

		// list aggregation by element
		for (PCMMElement element : configuration.getElements()) {

			int aggregationResult = 0;
			PCMMElement elementInAggregation = getSameElementInList(element, aggregationData.keySet());

			if (aggregationData.containsKey(elementInAggregation)) {
				PCMMAggregation<PCMMElement> pcmmAggregationElement = aggregationData.get(elementInAggregation);
				if (pcmmAggregationElement != null && pcmmAggregationElement.getLevel() != null
						&& pcmmAggregationElement.getLevel().getCode() != null) {
					aggregationResult = pcmmAggregationElement.getLevel().getCode();
				}
			}
			// set dataset
			dataset.addValue(aggregationResult, serie, element.getName());
		}

		// define spider plot for PCMM stamp
		SpiderWebPlot plot = new SpiderWebPlot(dataset);
		plot.setMaxValue(maxValue);
		plot.setOutlineVisible(false);
		plot.setStartAngle(startAngle);
		plot.setInteriorGap(0.40);
		plot.setBackgroundPaint(null);
		plot.setToolTipGenerator(new StandardCategoryToolTipGenerator());
		plot.setSeriesPaint(0, java.awt.Color.blue);
		plot.setSeriesOutlinePaint(0, java.awt.Color.blue);
		Stroke stroke = new BasicStroke(2.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 10.0f,
				new float[] { 10.0f, 7.5f }, 0.0f);
		plot.setSeriesOutlineStroke(0, stroke);
		JFreeChart chart = new JFreeChart(RscTools.empty(), TextTitle.DEFAULT_FONT, plot, false);
		ChartUtilities.applyCurrentTheme(chart);

		// labels
		plot.setLabelPaint(java.awt.Color.black);
		plot.setLabelFont(new java.awt.Font(CHART_FONT, java.awt.Font.BOLD, 13));

		// Chart specific Composite to contain chart
		boolean chartCompositePopup = false;
		ChartComposite chartComposite = new ChartComposite(parent, SWT.FILL, chart, chartCompositePopup,
				chartCompositePopup, chartCompositePopup, chartCompositePopup, chartCompositePopup);
		GridData gdChartComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gdChartComposite.minimumHeight = PCMMSTAMP_SIZE_MIN;
		gdChartComposite.minimumWidth = PCMMSTAMP_SIZE_MIN;
		chartComposite.setLayoutData(gdChartComposite);

		return chartComposite;
	}

	/**
	 * @param element     the element
	 * @param elementList the element list
	 * @return the PCMM element that is in the list with the same string attributes
	 */
	private static PCMMElement getSameElementInList(PCMMElement element, Set<PCMMElement> elementList) {
		PCMMElement eltToReturn = null;
		if (element != null && elementList != null) {
			for (PCMMElement eltTmp : elementList) {
				if (StringTools.equals(eltTmp.getName(), element.getName())
						&& StringTools.equals(eltTmp.getColor(), element.getColor())
						&& StringTools.equals(eltTmp.getAbbreviation(), element.getAbbreviation())) {
					eltToReturn = eltTmp;
					break;
				}
			}
		}
		return eltToReturn;
	}
}
