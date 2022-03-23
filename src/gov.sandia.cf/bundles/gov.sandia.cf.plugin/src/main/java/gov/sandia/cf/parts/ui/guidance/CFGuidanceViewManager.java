/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.guidance;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.launcher.CredibilityEditor;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The PCMM Guidance level view manager
 * 
 * @author Didier Verstraete
 *
 */
public class CFGuidanceViewManager extends ViewPart implements IPropertyChangeListener, IPartListener2 {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(CFGuidanceViewManager.class);

	/**
	 * a map of table phenomena columns
	 */
	private Map<CredibilityEditor, Map<CFFeature, Composite>> mapViews;

	/**
	 * the layout manager to hide/show the different views
	 */
	private StackLayout layout;
	/**
	 * the top content panel
	 */
	private Composite contentPanel;

	/**
	 * The default panel
	 */
	private Composite defaultPanel;

	/**
	 * Construct
	 */
	public CFGuidanceViewManager() {
		super();

		// Add the IPartListener2 to the page
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.addPartListener(this);

		mapViews = new HashMap<>();
	}

	/** {@inheritDoc} */
	@Inject
	public void createPartControl(Composite parent) {

		logger.debug("Create Guidance View part."); //$NON-NLS-1$

		// create content panel
		contentPanel = new Composite(parent, SWT.BORDER);
		layout = new StackLayout();
		contentPanel.setLayout(layout);

		// load default panel
		defaultPanel = new Composite(contentPanel, SWT.NONE);
		defaultPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gridLayout = new GridLayout(1, true);
		defaultPanel.setLayout(gridLayout);
		Text txtInfo = new Text(defaultPanel, SWT.READ_ONLY);
		txtInfo.setText(RscTools.getString(RscConst.MSG_GUIDANCEVIEW_WELCOME_INFO));
		GridData txtInfoGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		txtInfo.setLayoutData(txtInfoGridData);

		// display defaultPanel first
		layout.topControl = defaultPanel;
		contentPanel.layout();
	}

	/**
	 * Set the credibility editor to view its guidance level information
	 * 
	 * @param actualCredibilityEditor the actual credibility editor to set
	 */
	public void setCredibilityEditor(CredibilityEditor actualCredibilityEditor) {

		logger.debug("Set new credibility editor"); //$NON-NLS-1$

		if (actualCredibilityEditor != null && !actualCredibilityEditor.isInError() && contentPanel != null
				&& !contentPanel.isDisposed()) {

			// add property change listener to track the current cf view and change the
			// guidance level behavior
			actualCredibilityEditor.addPartPropertyListener(this);

			// Editor has changed
			if (!mapViews.containsKey(actualCredibilityEditor)) {

				// Create a new view associated to this credibility editor
				PIRTSpecification pirtConfiguration = actualCredibilityEditor.getCache().getPIRTSpecification();
				PCMMSpecification pcmmConfiguration = actualCredibilityEditor.getCache().getPCMMSpecification();

				// create guidance views
				PIRTGuidanceLevelView pirtView = new PIRTGuidanceLevelView(actualCredibilityEditor.getViewMgr(),
						contentPanel, pirtConfiguration, SWT.NONE);
				PCMMGuidanceLevelView pcmmView = new PCMMGuidanceLevelView(actualCredibilityEditor.getViewMgr(),
						contentPanel, pcmmConfiguration, SWT.NONE);

				// put it into maps
				Map<CFFeature, Composite> guidanceViewMap = new EnumMap<>(CFFeature.class);
				guidanceViewMap.put(CFFeature.PIRT, pirtView);
				guidanceViewMap.put(CFFeature.PCMM, pcmmView);
				mapViews.put(actualCredibilityEditor, guidanceViewMap);
			}

			// Get active view
			String activeView = actualCredibilityEditor
					.getPartProperty(CredibilityFrameworkConstants.PART_PROPERTY_ACTIVEVIEW);

			if (activeView != null) {
				CFFeature feature = CFFeature.valueOf(activeView);

				// Manage Layout
				manageLayout(actualCredibilityEditor, feature);
				contentPanel.layout();
			}
		}
	}

	/**
	 * Reload the views
	 * 
	 * @param credibilityEditor the credibility editor
	 */
	public void reload(CredibilityEditor credibilityEditor) {

		logger.debug("Reload credibility editor guidance."); //$NON-NLS-1$

		if (contentPanel != null && !contentPanel.isDisposed()) {

			// dispose the current views
			Map<CFFeature, Composite> credibilityEditorViews = mapViews.get(credibilityEditor);
			if (credibilityEditorViews != null) {
				Composite pirtGuidanceView = credibilityEditorViews.get(CFFeature.PIRT);
				if (pirtGuidanceView != null && !pirtGuidanceView.isDisposed()) {
					pirtGuidanceView.dispose();
				}
				Composite pcmmGuidanceView = credibilityEditorViews.get(CFFeature.PCMM);
				if (pcmmGuidanceView != null && !pcmmGuidanceView.isDisposed()) {
					pcmmGuidanceView.dispose();
				}
			}

			// Create a new view associated to this credibility editor
			PIRTSpecification pirtConfiguration = credibilityEditor.getCache().getPIRTSpecification();
			PCMMSpecification pcmmConfiguration = credibilityEditor.getCache().getPCMMSpecification();

			// create guidance views
			PIRTGuidanceLevelView pirtView = new PIRTGuidanceLevelView(credibilityEditor.getViewMgr(), contentPanel,
					pirtConfiguration, SWT.NONE);
			PCMMGuidanceLevelView pcmmView = new PCMMGuidanceLevelView(credibilityEditor.getViewMgr(), contentPanel,
					pcmmConfiguration, SWT.NONE);

			// put it into maps
			Map<CFFeature, Composite> guidanceViewMap = new EnumMap<>(CFFeature.class);
			guidanceViewMap.put(CFFeature.PIRT, pirtView);
			guidanceViewMap.put(CFFeature.PCMM, pcmmView);
			mapViews.put(credibilityEditor, guidanceViewMap);

			contentPanel.layout();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void partActivated(IWorkbenchPartReference ref) {
		// If the Editor I'm interested in was updated ...
		// Check it's a Credibility Editor
		IWorkbenchPart part = ref.getPart(true);
		if (part instanceof EditorPart) {
			if (part instanceof CredibilityEditor) {
				setCredibilityEditor((CredibilityEditor) part);
			} else {
				layout.topControl = defaultPanel;
				if (!contentPanel.isDisposed()) {
					contentPanel.layout();
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void partClosed(IWorkbenchPartReference ref) {
		// Clear view and data
		IWorkbenchPart part = ref.getPart(true);
		if (part instanceof CredibilityEditor && mapViews.containsKey(part)) {
			Map<CFFeature, Composite> mapViewsClosedEditor = mapViews.get(part);
			if (mapViewsClosedEditor != null) {
				// Dispose the views associated to the closed credibility editor
				for (Composite view : mapViewsClosedEditor.values()) {
					if (!view.isDisposed()) {
						view.dispose();
					}
				}
			}
			mapViews.remove(part);
			if (contentPanel != null && !contentPanel.isDisposed()) {
				layout.topControl = defaultPanel;
				contentPanel.layout();
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		// not used in this view
	}

	/** {@inheritDoc} */
	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		// not used in this view
	}

	/** {@inheritDoc} */
	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		// not used in this view
	}

	/** {@inheritDoc} */
	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
		// not used in this view
	}

	/** {@inheritDoc} */
	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		// not used in this view
	}

	/** {@inheritDoc} */
	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
		// not used in this view
	}

	/**
	 * Manage layout
	 * 
	 * @param credibilityEditor the credibility editor to display
	 * @param activeViewFeature the active view feature for the credibility editor
	 *                          in parameter
	 */
	private void manageLayout(CredibilityEditor credibilityEditor, CFFeature activeViewFeature) {

		logger.debug("Change Guidance view layout"); //$NON-NLS-1$

		// Get Guidance Views associated to the editor
		Map<CFFeature, Composite> mapViewsCurrentEditor = null;

		if (mapViews != null) {
			mapViewsCurrentEditor = mapViews.get(credibilityEditor);
		}

		// Guidance Level is available only in PIRT and PCMM views
		Composite topControl = null;

		if (mapViewsCurrentEditor != null) {

			// PIRT view selected
			if (CFFeature.PIRT.equals(activeViewFeature)) {
				Composite pirtGuidanceLevelView = mapViewsCurrentEditor.get(CFFeature.PIRT);
				if (pirtGuidanceLevelView != null && !pirtGuidanceLevelView.isDisposed()) {
					topControl = pirtGuidanceLevelView;
				}
			}

			// PCMM or PCMM Planning view selected
			else if (CFFeature.PCMM.equals(activeViewFeature) || CFFeature.PCMM_PLANNING.equals(activeViewFeature)) {
				Composite pcmmGuidanceLevelView = mapViewsCurrentEditor.get(CFFeature.PCMM);
				if (pcmmGuidanceLevelView != null && !pcmmGuidanceLevelView.isDisposed()) {
					topControl = pcmmGuidanceLevelView;
				}
			}
		}

		// if there is no specific view, load the default view
		if (topControl == null) {
			layout.topControl = defaultPanel;
		} else {
			layout.topControl = topControl;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setFocus() {
		// do not set focus
	}

	/** {@inheritDoc} */
	@Override
	public void propertyChange(PropertyChangeEvent event) {

		if (event == null) {
			return;
		}

		String property = event.getProperty();
		if (CredibilityFrameworkConstants.PART_PROPERTY_ACTIVEVIEW.equals(property)) {
			if (event.getNewValue() instanceof String) {
				CFFeature feature = CFFeature.valueOf((String) event.getNewValue());
				openActiveView(feature);
			}
		}
		// PCMM Open element/sub-element
		else if (CredibilityFrameworkConstants.PART_PROPERTY_ACTIVEVIEW_PCMM_SELECTED_ASSESSABLE.equals(property)) {
			openPCMMItem();
		}
	}

	/**
	 * Open the guidance for the corresponding active view.
	 * 
	 * @param activeViewFeature the active view feature
	 */
	private void openActiveView(CFFeature activeViewFeature) {

		// Check if it's a Credibility Editor
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (part instanceof CredibilityEditor) {
			if (mapViews != null && mapViews.containsKey(part)) {
				// Manage Layout
				manageLayout((CredibilityEditor) part, activeViewFeature);
			} else {
				layout.topControl = defaultPanel;
			}
		} else {
			layout.topControl = defaultPanel;
		}

		// Refresh
		if (!contentPanel.isDisposed()) {
			contentPanel.layout();
		}
	}

	/**
	 * Open PCMM element or subelement selected into the guidance view. This method
	 * uses the CredibilityEditor part properties.
	 */
	private void openPCMMItem() {
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (part instanceof CredibilityEditor) {
			String itemCode = ((CredibilityEditor) part)
					.getPartProperty(CredibilityFrameworkConstants.PART_PROPERTY_ACTIVEVIEW_PCMM_SELECTED_ASSESSABLE);
			if (!RscTools.empty().equals(itemCode) && layout.topControl instanceof PCMMGuidanceLevelView) {
				PCMMGuidanceLevelView pcmmView = (PCMMGuidanceLevelView) layout.topControl;
				pcmmView.expandElementByAbbreviation(itemCode);
			}
		}
	}

}
