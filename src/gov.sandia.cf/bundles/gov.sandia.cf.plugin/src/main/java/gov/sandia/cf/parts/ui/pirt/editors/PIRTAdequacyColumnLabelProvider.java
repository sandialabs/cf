/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import java.util.Optional;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.PIRTTreeAdequacyColumnType;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.RscTools;

/**
 * The PIRT adequacy column label provider
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTAdequacyColumnLabelProvider extends ColumnLabelProvider {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PIRTAdequacyColumnLabelProvider.class);

	private PIRTSpecification pirtConfiguration;
	private PIRTAdequacyColumn column;
	private IViewManager viewMgr;

	/**
	 * Constructor
	 * 
	 * @param pirtConfiguration the PIRT configuration
	 * @param column            the PIRT adequacy column
	 * @param viewMgr           the view manager
	 */
	public PIRTAdequacyColumnLabelProvider(PIRTSpecification pirtConfiguration, PIRTAdequacyColumn column,
			IViewManager viewMgr) {
		Assert.isNotNull(viewMgr);
		Assert.isNotNull(pirtConfiguration);
		Assert.isNotNull(column);
		this.pirtConfiguration = pirtConfiguration;
		this.column = column;
		this.viewMgr = viewMgr;
	}

	@Override
	public String getText(Object element) {
		if (pirtConfiguration.getLevels() != null && element instanceof Phenomenon
				&& ((Phenomenon) element).getCriterionList() != null) {
			for (Criterion criterion : ((Phenomenon) element).getCriterionList()) {
				// retrieve level columns
				PIRTLevelImportance pirtLevel = null;
				if (criterion != null && criterion.getName() != null && criterion.getName().equals(column.getName())
						&& PIRTTreeAdequacyColumnType.LEVELS.getType().equals(criterion.getType())) {
					pirtLevel = pirtConfiguration.getLevels().get(criterion.getValue());
					return (pirtLevel != null ? pirtLevel.getLabel() : RscTools.empty());
				}
			}
		}
		return RscTools.empty();
	}

	@Override
	public Color getBackground(Object element) {

		// Phenomenon Group background
		if (element instanceof PhenomenonGroup) {
			return ColorTools.toColor(viewMgr.getRscMgr(),
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT));
		}

		// Phenomenon background
		else if (pirtConfiguration.getLevels() != null && element instanceof Phenomenon
				&& ((Phenomenon) element).getCriterionList() != null) {

			Phenomenon phenomenon = (Phenomenon) element;
			PIRTLevelImportance importanceTemp = pirtConfiguration.getLevels().get(phenomenon.getImportance());

			// retrieve current criterion
			Optional<Criterion> criterionFound = phenomenon.getCriterionList().stream()
					.filter(criterion -> criterion != null && criterion.getName() != null
							&& criterion.getName().equals(column.getName())
							&& PIRTTreeAdequacyColumnType.LEVELS.getType().equals(criterion.getType()))
					.findFirst();

			if (importanceTemp != null && criterionFound.isPresent()) {

				// retrieve PIRT level for this criterion
				PIRTLevelImportance pirtLevel = pirtConfiguration.getLevels().get(criterionFound.get().getValue());

				try {
					// return color compared to importance column
					RGB rgbColor = ColorTools
							.stringRGBToColor(viewMgr.getAppManager().getService(IPIRTApplication.class)
									.getBackgroundColor(pirtConfiguration, importanceTemp, pirtLevel));
					return rgbColor != null ? new Color(Display.getCurrent(), rgbColor) : null;
				} catch (CredibilityException e) {
					logger.error("An error occured while getting the level color: {}", e.getMessage(), //$NON-NLS-1$
							e);
				}
			}
		}

		return null;
	}

	@Override
	public Color getForeground(Object element) {
		return (element instanceof PhenomenonGroup)
				? ColorTools.toColor(viewMgr.getRscMgr(), ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE))
				: null;
	}

}
