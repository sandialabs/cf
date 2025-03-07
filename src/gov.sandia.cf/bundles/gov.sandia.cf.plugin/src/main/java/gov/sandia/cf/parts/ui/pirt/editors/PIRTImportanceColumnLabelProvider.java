/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.tools.ColorTools;
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
	private ResourceManager rscMgr;

	/**
	 * Constructor
	 * 
	 * @param pirtConfiguration the PIRT configuration
	 * @param rscMgr            the resource manager
	 */
	public PIRTImportanceColumnLabelProvider(PIRTSpecification pirtConfiguration, ResourceManager rscMgr) {
		Assert.isNotNull(pirtConfiguration);
		this.pirtConfiguration = pirtConfiguration;
		this.rscMgr = rscMgr;
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
		return (element instanceof PhenomenonGroup)
				? ColorTools.toColor(rscMgr, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT))
				: null;
	}

	@Override
	public Color getForeground(Object element) {
		return (element instanceof PhenomenonGroup)
				? ColorTools.toColor(rscMgr, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE))
				: null;
	}

}
