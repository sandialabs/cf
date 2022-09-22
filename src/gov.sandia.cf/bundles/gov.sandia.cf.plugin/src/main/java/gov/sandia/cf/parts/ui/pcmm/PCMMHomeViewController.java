/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MenuItem;
import org.jfree.chart.entity.PieSectionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.parts.ui.AViewController;
import gov.sandia.cf.parts.widgets.ITagAction;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * PCMM home controller: Used to control the PCMM Home view
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMHomeViewController extends AViewController<PCMMViewManager, PCMMHomeView>
		implements SelectionListener, ITagAction {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMHomeViewController.class);

	/** Buttons events PIRT */
	public static final String BTN_EVENT_PCMM_CLOSE = "PCMM_CLOSE"; //$NON-NLS-1$

	/**
	 * The pie section attribute
	 */
	public static final String PIE_SECTION_ATTRIBUTE = "PIE_SECTION_ATTRIBUTE"; //$NON-NLS-1$

	/**
	 * The command names for buttons
	 */
	/** PCMM PLANNING event COMMAND */
	public static final String PLANNING_COMMAND = "PLANNING_COMMAND"; //$NON-NLS-1$
	/** PCMM EVIDENCE event COMMAND */
	public static final String EVIDENCE_COMMAND = "EVIDENCE_COMMAND"; //$NON-NLS-1$
	/** PCMM ASSESS event COMMAND */
	public static final String ASSESS_COMMAND = "ASSESS_COMMAND"; //$NON-NLS-1$

	/**
	 * The pcmm phases enabled
	 */
	private List<PCMMPhase> phaseEnabled;

	/**
	 * the pcmm elements map
	 */
	private Map<String, PCMMElement> elements;

	/**
	 * the tag list
	 */
	private List<Tag> tagList;

	/**
	 * Configuration
	 */
	private PCMMSpecification pcmmConfiguration;

	/**
	 * Constructor.
	 *
	 * @param viewManager  the view manager
	 * @param phaseEnabled the phase enabled
	 */
	PCMMHomeViewController(PCMMViewManager viewManager, final List<PCMMPhase> phaseEnabled) {
		super(viewManager);
		super.setView(new PCMMHomeView(this, SWT.NONE));

		// Initialize
		this.elements = new LinkedHashMap<>();
		this.pcmmConfiguration = getViewManager().getPCMMConfiguration();

		if (phaseEnabled == null) {
			this.phaseEnabled = new ArrayList<>();
		} else {
			this.phaseEnabled = phaseEnabled;
		}

		List<PCMMPhase> enabledAndActivatedPhases = null;
		if (pcmmConfiguration != null) {
			enabledAndActivatedPhases = this.phaseEnabled.stream().filter(this.pcmmConfiguration.getPhases()::contains)
					.collect(Collectors.toList());
		}

		// set title if there is only one phase activated
		if (enabledAndActivatedPhases != null && enabledAndActivatedPhases.size() == 1) {
			String title = MessageFormat.format("{0} - {1}", RscTools.getString(RscConst.MSG_PCMMVIEW_TITLE), //$NON-NLS-1$
					enabledAndActivatedPhases.get(0));
			setTitle(title);
		}

		// Refresh
		refresh();
	}

	/**
	 * Reload data.
	 */
	void reloadData() {
		// Show role selection
		getView().showRoleSelection();

		// Get Model
		Model model = getViewManager().getCache().getModel();
		if (model != null) {
			try {
				// Load the PCMM elements
				List<PCMMElement> elementsList = getViewManager().getAppManager().getService(IPCMMApplication.class)
						.getElementList(model);

				// If element list is not empty
				if (null != elementsList && !elementsList.isEmpty()) {

					// Create not sorted map by name
					elementsList.forEach(element -> this.elements.put(element.getName(), element));

					// load the tags
					tagList = getViewManager().getAppManager().getService(IPCMMApplication.class).getTags();
				}

			} catch (CredibilityException e) {
				MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_PCMMHOME_DIALOG_TITLE),
						RscTools.getString(RscConst.ERR_PCMMHOME_DIALOG_LOADING_MSG));
				logger.warn("An error occurred while loading the home data:\n{}", e.getMessage(), e); //$NON-NLS-1$
			}

			// repaint the view composites
			getView().renderMain();

			// reload footer
			getView().reloadFooterButtons();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {

		// handle context menu selection
		String command = (String) ((MenuItem) e.getSource()).getData();

		PieSectionEntity pieSectionEntity = (PieSectionEntity) ((MenuItem) e.getSource()).getParent()
				.getData(PIE_SECTION_ATTRIBUTE);

		PCMMElement pcmmElementSelected = this.elements.get(pieSectionEntity.getSectionKey().toString());

		// WARNING: Hardcoded - Check if PIRT have QoI and it's PMMF element
		boolean isAvailabledAccess = true;
		if (null != pcmmElementSelected.getAbbreviation() && pcmmElementSelected.getAbbreviation().equals("PMMF")) { //$NON-NLS-1$
			Model model = getViewManager().getCache().getModel();
			if (null != model) {
				List<QuantityOfInterest> qois = getViewManager().getAppManager().getService(IPIRTApplication.class)
						.getRootQoI(model);
				isAvailabledAccess = (null != qois && !qois.isEmpty());
			}
		}

		// Check access
		if (isAvailabledAccess) {

			// open planning view
			if (command.equals(PLANNING_COMMAND)) {
				getViewManager().openPCMMPlanningView(pcmmElementSelected);
			}
			// open evidence view
			else if (command.equals(EVIDENCE_COMMAND)) {
				getViewManager().openPCMMEvidenceView(pcmmElementSelected);
			}
			// open pcmm assess view
			else if (command.equals(ASSESS_COMMAND)) {
				getViewManager().openPCMMAssessView(pcmmElementSelected);
			}
		} else {
			// Open error dialog
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PCMMHOME_PREREQUISITE_TITLE),
					RscTools.getString(RscConst.ERR_PCMMHOME_PREREQUISITE_TXT));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// redirect to widget selected method
		widgetSelected(e);
	}

	/**
	 * Tag the current PCMM, and prompt the user to enter the tag parameters
	 */
	@Override
	public void tagCurrentPCMM() {

		boolean canTag = checkEditorSaveNeed();

		if (canTag) {
			// create new tag dialog
			NewTagDialog tagDialog = new NewTagDialog(getViewManager(), getView().getShell());
			Tag newTag = tagDialog.openDialog();

			// if the user entered a tag
			if (newTag != null) {
				newTag.setUserCreation(getViewManager().getCache().getUser());
				try {
					// tag
					getViewManager().getAppManager().getService(IPCMMApplication.class).tagCurrent(newTag);

					// save the credibility process
					getViewManager().viewChanged();

					// alert the user
					MessageDialog.openInformation(getView().getShell(),
							RscTools.getString(RscConst.MSG_TAG_DIALOG_VIEWTITLE),
							RscTools.getString(RscConst.MSG_TAG_PART_TAG_SUCCESS));

					// reload the view
					tagList = getViewManager().getAppManager().getService(IPCMMApplication.class).getTags();
					getView().setTagList(tagList);

				} catch (CredibilityException e) {
					MessageDialog.openWarning(getView().getShell(),
							RscTools.getString(RscConst.MSG_PCMMHOME_DIALOG_TITLE),
							RscTools.getString(RscConst.ERR_TAG_DIALOG_TAGGING_MSG));
					logger.warn("An error has occurred while tagging the current PCMM state:\n{}", e.getMessage(), e); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Open a dialog to manage the tags
	 */
	@Override
	public void manageTags() {

		boolean canTag = checkEditorSaveNeed();

		if (canTag) {
			// open the tag manager dialog
			PCMMManageTagDialog tagDialog = new PCMMManageTagDialog(getViewManager(), getView().getShell());
			tagDialog.open();

			// reload the view
			tagList = getViewManager().getAppManager().getService(IPCMMApplication.class).getTags();
			getView().setTagList(tagList);
		}
	}

	/**
	 * The tag selection changed
	 */
	@Override
	public void tagSelectionChanged(Tag newTag) {
		// change the view behavior and reload data
		getViewManager().setSelectedTag(newTag);
	}

	/**
	 * If the editor is in a dirty state, this methods asks the user to save it.
	 * 
	 * @return true if the editor is clean to be tagged, otherwise false
	 */
	private boolean checkEditorSaveNeed() {

		boolean canContinue = true;

		// if the editor needs to be saved
		if (getViewManager().getCredibilityEditor().isDirty()) {

			canContinue = false;

			// ask to save before tagging
			boolean confirmSave = MessageDialog.openQuestion(getView().getShell(),
					RscTools.getString(RscConst.MSG_TAG_DIALOG_VIEWTITLE),
					RscTools.getString(RscConst.MSG_EDITOR_SAVE_BEFORE));

			// save the credibility process
			if (confirmSave) {
				getViewManager().doSave();
				canContinue = true;
			}
		}

		return canContinue;
	}

	/**
	 * View changed.
	 */
	public void viewChangedAndRefresh() {

		// Set view changed
		getViewManager().viewChanged();

		// Refresh
		getView().refresh();
	}

	/**
	 * Refresh if changed.
	 */
	public void refreshIfChanged() {

		// Refresh
		if (getViewManager().isDirty()) {
			getView().refresh();
		}
	}

	/**
	 * {@link PCMMViewManager#isTagMode()}
	 * 
	 * @return true if the selected tag is a real tag, otherwise false
	 */
	public boolean isTagMode() {
		return getViewManager().isTagMode();
	}

	/**
	 * @return the pcmm configuration
	 */
	public PCMMSpecification getPCMMConfiguration() {
		return getViewManager().getPCMMConfiguration();
	}

	/**
	 * Gets the phase enabled.
	 *
	 * @return the phase enabled
	 */
	public List<PCMMPhase> getPhaseEnabled() {
		return phaseEnabled;
	}

	/**
	 * Gets the elements.
	 *
	 * @return the elements
	 */
	public Map<String, PCMMElement> getElements() {
		return elements;
	}

	/**
	 * Gets the tag list.
	 *
	 * @return the tag list
	 */
	public List<Tag> getTagList() {
		return tagList;
	}

	/**
	 * @return the pcmm mode
	 */
	public PCMMMode getPCMMMode() {
		return getViewManager().getPCMMConfiguration() != null ? getViewManager().getPCMMConfiguration().getMode()
				: null;
	}

}
