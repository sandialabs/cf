/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import gov.sandia.cf.application.configuration.pirt.PIRTSpecification;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.tools.RscTools;

/**
 * The PIRT importance column label provider
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTImportanceColumnLabelProvider extends ColumnLabelProvider {

	/**
	 * The PIRT configuration
	 */
	private PIRTSpecification pirtConfiguration;

	/**
	 * Constructor
	 * 
	 * @param pirtConfiguration the PIRT configuration
	 */
	public PIRTImportanceColumnLabelProvider(PIRTSpecification pirtConfiguration) {
		Assert.isNotNull(pirtConfiguration);
		this.pirtConfiguration = pirtConfiguration;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Phenomenon) {
			Phenomenon phenomenon = (Phenomenon) element;
			PIRTLevelImportance pirtLevel = null;
			if (phenomenon.getImportance() != null) {
				pirtLevel = pirtConfiguration.getLevels().get(phenomenon.getImportance());
			}
			return (pirtLevel != null ? pirtLevel.getLabel() : RscTools.empty());
		}

		return RscTools.empty();
	}

	@Override
	public Color getBackground(Object element) {
		return (element instanceof PhenomenonGroup) ? ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT)
				: null;
	}

	@Override
	public Color getForeground(Object element) {
		return (element instanceof PhenomenonGroup) ? ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE) : null;
	}

}
