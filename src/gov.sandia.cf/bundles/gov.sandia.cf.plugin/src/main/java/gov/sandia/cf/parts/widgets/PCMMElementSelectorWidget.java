/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.parts.ui.IViewManager;

/**
 * The Credibility Element widget to select one.
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMElementSelectorWidget extends SelectWidget<PCMMElement> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMElementSelectorWidget.class);

	/** PCMM Selector NOT APPLICABLE ID */
	public static final Integer NOT_APPLICABLE_ID = -1;
	/** PCMM Selector NOT APPLICABLE VALUE */
	public static final String NOT_APPLICABLE_VALUE = "N/A";//$NON-NLS-1$

	private IViewManager viewMgr;

	/**
	 * @param viewMgr  the view manager
	 * @param parent   the parent composite
	 * @param style    the style
	 * @param editable is editable?
	 * @param id       the id data to associate
	 */
	public PCMMElementSelectorWidget(IViewManager viewMgr, Composite parent, int style, boolean editable, Object id) {
		super(viewMgr.getRscMgr(), parent, style, editable, id);

		Assert.isNotNull(viewMgr);
		this.viewMgr = viewMgr;

		// load data
		loadSelectValues();
	}

	/**
	 * Load the select values
	 */
	private void loadSelectValues() {
		try {
			List<PCMMElement> elementList = viewMgr.getAppManager().getService(IPCMMApplication.class)
					.getElementList(viewMgr.getCache().getModel());
			elementList.add(new PCMMElement() {
				private static final long serialVersionUID = 1L;

				@Override
				public Integer getId() {
					return NOT_APPLICABLE_ID;
				}

				@Override
				public String getSelectName() {
					return NOT_APPLICABLE_VALUE;
				}
			});
			super.setSelectValues(elementList);
		} catch (CredibilityException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
