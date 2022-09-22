/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.application.pcmm.IPCMMAssessmentApp;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.parts.ui.AViewController;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * PCMM Assess view controller: Used to control the PCMM Assess view
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAssessViewController extends AViewController<PCMMViewManager, PCMMAssessView> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMAssessViewController.class);

	/**
	 * The PCMM Assess view column indexes
	 */
	/** PCMM Assess table ID index */
	public static final int ID_INDEX = 0;
	/** PCMM Assess table NAME index */
	public static final int NAME_INDEX = 1;
	/** PCMM Assess table LEVEL index */
	public static final int LEVEL_INDEX = 2;
	/** PCMM Assess table EVIDENCE index */
	public static final int EVIDENCE_INDEX = 3;
	/** PCMM Assess table COMMENTS index */
	public static final int COMMENTS_INDEX = 4;

	/**
	 * PCMMSpecification
	 */
	private PCMMSpecification pcmmConfiguration;

	/**
	 * the pcmm element
	 */
	private PCMMElement elementSelected;

	/**
	 * the pcmm elements
	 */
	private List<PCMMElement> elements;

	/**
	 * the map of pcmm assessments indexed by element for the current user and role
	 */
	private Map<PCMMElement, PCMMAssessment> assessmentsByElt;

	/**
	 * the map of pcmm assessments indexed by subelement for the current user and
	 * role
	 */
	private Map<PCMMSubelement, PCMMAssessment> assessmentsBySubelt;

	/**
	 * Instantiates a new PCMM assess view controller.
	 *
	 * @param viewManager the view manager
	 */
	PCMMAssessViewController(PCMMViewManager viewManager) {
		super(viewManager);

		elements = new ArrayList<>();
		assessmentsByElt = new HashMap<>();
		assessmentsBySubelt = new HashMap<>();

		// Set PCMM configuration
		pcmmConfiguration = getViewManager().getPCMMConfiguration();

		super.setView(new PCMMAssessView(this, SWT.NONE));

		// Refresh
		refresh();
	}

	void reloadData() {

		// Trigger GuidanceLevel View
		if (elementSelected != null) {
			getViewManager().getCredibilityEditor().setPartProperty(
					CredibilityFrameworkConstants.PART_PROPERTY_ACTIVEVIEW_PCMM_SELECTED_ASSESSABLE,
					elementSelected.getAbbreviation());
		}

		// Show role selection
		getView().showRoleSelection();

		// Get Model
		Model model = getViewManager().getCache().getModel();
		if (model != null) {

			try {
				/**
				 * Load pcmm elements from database
				 */
				elements = getViewManager().getAppManager().getService(IPCMMApplication.class).getElementList(model);

				assessmentsBySubelt.clear();

				if (elements != null) {

					/**
					 * Load assessments for the current viewer
					 */
					loadAssessments();

					/**
					 * Refresh the table
					 */
					getView().refreshMainTable();

					// set pcmm elements
					// WARNING: this instruction must be done after populating
					// assessmentsBySubeltId because it is used to display some column labels
					getView().setTreeData(elements);

					// expand and select the selected element in the viewer input
					getView().expandSelectedElement();
				}

			} catch (CredibilityException e) {
				MessageDialog.openWarning(getView().getShell(),
						RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_TITLE),
						RscTools.getString(RscConst.ERR_PCMMASSESS_DIALOG_LOADING_MSG));
				logger.error("An error has occurred while loading assessment data:\n{}", e.getMessage(), e); //$NON-NLS-1$
			}
		}

		// refresh the viewer
		getView().refreshViewer();
	}

	/**
	 * Load assessments for the current viewer
	 */
	private void loadAssessments() {
		if (PCMMMode.DEFAULT.equals(getPCMMMode())) {
			loadAssessmentsDefault();
		} else if (PCMMMode.SIMPLIFIED.equals(getPCMMMode())) {
			loadAssessmentsSimplified();
		}
	}

	/**
	 * Load assessments for the current viewer in default mode (assessments on a
	 * PCMM subelement)
	 */
	private void loadAssessmentsDefault() {
		if (elements != null) {
			elements.forEach(elt -> elt.getSubElementList().forEach(sub -> {
				List<PCMMAssessment> assessments = null;
				try {
					assessments = getViewManager().getAppManager().getService(IPCMMAssessmentApp.class)
							.getAssessmentByRoleAndUserAndSubeltAndTag(getViewManager().getCache().getCurrentPCMMRole(),
									getViewManager().getCache().getUser(), sub, getViewManager().getSelectedTag());
				} catch (CredibilityException e) {
					logger.error("Failed to load the PCMM assessments for: {}", sub, e); //$NON-NLS-1$
				}
				assessmentsBySubelt.put(sub,
						(assessments != null && !assessments.isEmpty()) ? assessments.get(0) : null);
			}));
		}
	}

	/**
	 * Load assessments for the current viewer in simplified mode (assessments on a
	 * PCMM element)
	 */
	private void loadAssessmentsSimplified() {
		if (elements != null) {
			elements.forEach(elt -> {
				List<PCMMAssessment> assessments = null;
				try {
					assessments = getViewManager().getAppManager().getService(IPCMMAssessmentApp.class)
							.getAssessmentByRoleAndUserAndEltAndTag(getViewManager().getCache().getCurrentPCMMRole(),
									getViewManager().getCache().getUser(), elt, getViewManager().getSelectedTag());
				} catch (CredibilityException e) {
					logger.error("Failed to load the PCMM assessments for: {}", elt, e); //$NON-NLS-1$
				}
				assessmentsByElt.put(elt, (assessments != null && !assessments.isEmpty()) ? assessments.get(0) : null);
			});
		}
	}

	/**
	 * Execute "assess" action on element in parameter
	 * 
	 * This method is used in PCMM mode SIMPLIFIED
	 * 
	 * @param elt the element to assess
	 */
	void openAssessDialog(PCMMElement elt) {
		if (elt != null) {

			PCMMAssessment assessment = assessmentsByElt.get(elt);
			if (assessment == null) {
				assessment = new PCMMAssessment();
				assessment.setElement(elt);
			}

			PCMMAssessDialog assessDialog = new PCMMAssessDialog(getViewManager(), getView().getShell(), assessment);
			PCMMAssessment assessmentToReturn = assessDialog.openDialog();

			// persist in database
			if (null != assessmentToReturn) {
				assess(assessmentToReturn);
			}
		}
	}

	/**
	 * Execute "assess" action on subelement in parameter
	 * 
	 * This method is used in PCMM mode DEFAULT
	 * 
	 * @param elt the subelement to assess
	 */
	void openAssessDialog(PCMMSubelement elt) {
		if (elt != null) {

			PCMMAssessment assessment = assessmentsBySubelt.get(elt);
			if (assessment == null) {
				assessment = new PCMMAssessment();
				assessment.setSubelement(elt);
			}

			PCMMAssessDialog assessDialog = new PCMMAssessDialog(getViewManager(), getView().getShell(), assessment);
			PCMMAssessment assessmentToReturn = assessDialog.openDialog();

			// persist in database
			if (null != assessmentToReturn) {
				assess(assessmentToReturn);
			}
		}
	}

	/**
	 * Create or update the assessment for the level in parameter for the current
	 * user
	 * 
	 * @param updatedAssessment the assessment to assess
	 */
	private void assess(PCMMAssessment updatedAssessment) {

		try {
			// Initialize
			Role currentRole = getViewManager().getCurrentUserRole();
			User currentUser = getViewManager().getCache().getUser();

			// It's new
			if (null == updatedAssessment.getId() && null == updatedAssessment.getRoleCreation()
					&& null == updatedAssessment.getUserCreation()) {

				// Check element/sub-element according to mode
				if (PCMMMode.DEFAULT.equals(getPCMMMode())) {
					//
					if (null != updatedAssessment.getSubelement()) {
						// Clear dirty database
						clearAssessment(updatedAssessment.getId(), currentRole, currentUser,
								updatedAssessment.getSubelement());

					} else {
						throw new CredibilityException(
								RscTools.getString(RscConst.EX_PCMM_ADDASSESSTBYID_SUBELEMENTNULL));
					}
				} else if (PCMMMode.SIMPLIFIED.equals(getPCMMMode())) {
					if (null != updatedAssessment.getElement()) {
						// Clear dirty database
						clearAssessment(updatedAssessment.getId(), updatedAssessment.getRoleCreation(),
								updatedAssessment.getUserCreation(), updatedAssessment.getElement());

					} else {
						throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_ADDASSESSTBYID_ELEMENTNULL));
					}
				}

				// Set role and user
				updatedAssessment.setRoleCreation(currentRole);
				updatedAssessment.setUserCreation(currentUser);

				// create the assessment
				getViewManager().getAppManager().getService(IPCMMAssessmentApp.class).addAssessment(updatedAssessment);

				// trigger view change to save
				getViewManager().viewChanged();
			}

			// It's an update
			else if (null != updatedAssessment.getId()) {
				// Check role and User correspond
				if (updatedAssessment.getRoleCreation() == currentRole
						&& updatedAssessment.getUserCreation() == currentUser) {

					// Check mode
					if (PCMMMode.DEFAULT.equals(getPCMMMode())) {
						if (null != updatedAssessment.getSubelement()) {
							// Clear dirty database
							clearAssessment(updatedAssessment.getId(), updatedAssessment.getRoleCreation(),
									updatedAssessment.getUserCreation(), updatedAssessment.getSubelement());

						} else {
							throw new CredibilityException(
									RscTools.getString(RscConst.EX_PCMM_ADDASSESSTBYID_SUBELEMENTNULL));
						}
					} else if (PCMMMode.SIMPLIFIED.equals(getPCMMMode())) {
						if (null != updatedAssessment.getElement()) {
							// Clear dirty database
							clearAssessment(updatedAssessment.getId(), updatedAssessment.getRoleCreation(),
									updatedAssessment.getUserCreation(), updatedAssessment.getElement());

						} else {
							throw new CredibilityException(
									RscTools.getString(RscConst.EX_PCMM_ADDASSESSTBYID_ELEMENTNULL));
						}
					}

					// Update the assessment
					getViewManager().getAppManager().getService(IPCMMAssessmentApp.class)
							.updateAssessment(updatedAssessment, currentUser, currentRole);

					// trigger view change to save
					getViewManager().viewChanged();
				} else {
					// Not possible but we know
					throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_FORBIDDEN));
				}
			}

			// Errors
			else {
				if (updatedAssessment.getId() == null) {
					throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_IDNULL));
				} else if (updatedAssessment.getUserCreation() == null) {
					throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMASSESSMENT_USER_NULL));
				} else if (updatedAssessment.getRoleCreation() == null) {
					throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMASSESSMENT_ROLE_NULL));
				}
			}

			// Refresh
			getView().refresh();

		} catch (CredibilityException e) {
			logger.error("An error has occurred while loading assessment data:\n{}", e.getMessage(), e); //$NON-NLS-1$
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PCMMASSESS_ASSESS),
					e.getMessage());
		}
	}

	/**
	 * Assess from the cell modifier directly, cell by cell.
	 * 
	 * @param subelt   the subelement to associate
	 * @param property the property updated
	 * @param value    the new value
	 */
	public void assessSubelementFromCellModifier(PCMMSubelement subelt, String property, Object value) {

		int columnIndex = getColumnIndex(property);

		if (subelt != null) {

			// retrieve pcmm assessment from subelement
			PCMMAssessment assessment = assessmentsBySubelt.get(subelt);

			boolean updated = false;
			boolean creationMode = false;

			// create or update assessment
			if (assessment == null) {
				assessment = new PCMMAssessment();
				assessment.setRoleCreation(getViewManager().getCache().getCurrentPCMMRole());
				assessment.setUserCreation(getViewManager().getCache().getUser());
				assessment.setSubelement(subelt);
				creationMode = true;
			}
			try {
				switch (columnIndex) {
				case LEVEL_INDEX:
					if (creationMode || null == assessment.getLevel()
							|| (null != assessment.getLevel() && !assessment.getLevel().equals(value))) {
						PCMMLevel newLevel = (PCMMLevel) value;

						// check if the subelement has evidence before assessing
						if (assessment.getSubelement() != null && assessment.getSubelement().getLevelList() != null
								&& !assessment.getSubelement().getLevelList().isEmpty()) {
							PCMMLevel levelRoot = assessment.getSubelement().getLevelList().get(0);
							if (levelRoot != null && newLevel != null
									&& assessment.getSubelement().getEvidenceList().isEmpty()
									&& Integer.compare(levelRoot.getId(), newLevel.getId()) != 0) {
								throw new CredibilityException(RscTools.getString(
										RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_NO_EVIDENCE, levelRoot.getName()));
							}
						}

						// set the assessment level
						assessment.setLevel(newLevel);
						updated = true;
					}
					break;

				case COMMENTS_INDEX:
					if (creationMode || !StringTools.equals(assessment.getComment(), (String) value)) {
						assessment.setComment((String) value);
						updated = true;
					}
					break;
				default:
					break;

				}

				// if it is a real modification, do it
				if (updated) {

					// persist modifications in database
					if (creationMode) {
						getViewManager().getAppManager().getService(IPCMMAssessmentApp.class).addAssessment(assessment);

					} else {
						getViewManager().getAppManager().getService(IPCMMAssessmentApp.class).updateAssessment(
								assessment, getViewManager().getCache().getUser(),
								getViewManager().getCache().getCurrentPCMMRole());
					}

					// trigger view change
					getViewManager().viewChanged();

					// Refresh the parent
					getView().refresh();

				}

			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PCMMASSESS_TITLE),
						RscTools.getString(RscConst.ERR_PCMMASSESS_UPDATING) + property
								+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			}
		}
	}

	/**
	 * Assess from the cell modifier directly, cell by cell.
	 * 
	 * @param elt      the element to associate
	 * @param property the property updated
	 * @param value    the new value
	 */
	public void assessSimplifiedFromCellModifier(PCMMElement elt, String property, Object value) {

		if (elt != null) {
			int index = getView().getColumnProperties().indexOf(property);

			// retrieve pcmm assessment from element
			PCMMAssessment assessment = assessmentsByElt.get(elt);

			boolean updated = false;
			boolean creationMode = false;

			// create or update assessment
			if (assessment == null) {
				assessment = new PCMMAssessment();
				assessment.setRoleCreation(getViewManager().getCache().getCurrentPCMMRole());
				assessment.setUserCreation(getViewManager().getCache().getUser());
				assessment.setElement(getView().getFirstElementSelected());
				creationMode = true;
			}

			if (assessment.getElement() == null) {
				assessment.setElement(getView().getFirstElementSelected());
			}

			try {
				switch (index) {
				case LEVEL_INDEX:
					if (creationMode || !((assessment.getLevel() == value)
							|| (assessment.getLevel() != null && assessment.getLevel().equals(value)))) {
						PCMMLevel levelRoot = assessment.getElement().getLevelList().get(0);
						PCMMLevel newLevel = (PCMMLevel) value;
						if (levelRoot != null && newLevel != null && assessment.getElement() != null
								&& assessment.getElement().getEvidenceList().isEmpty()
								&& !levelRoot.getId().equals(newLevel.getId())) {
							throw new CredibilityException(RscTools
									.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_NO_EVIDENCE, levelRoot.getName()));
						}
						assessment.setLevel(newLevel);
						updated = true;
					}
					break;

				case COMMENTS_INDEX:
					if (creationMode || !StringTools.equals(assessment.getComment(), (String) value)) {
						assessment.setComment((String) value);
						updated = true;
					}
					break;
				default:
					break;

				}

				// if it is a real modification, do it
				if (updated) {

					// persist modifications in database
					if (creationMode) {
						getViewManager().getAppManager().getService(IPCMMAssessmentApp.class).addAssessment(assessment);

					} else {
						getViewManager().getAppManager().getService(IPCMMAssessmentApp.class).updateAssessment(
								assessment, getViewManager().getCache().getUser(),
								getViewManager().getCache().getCurrentPCMMRole());
					}

					// trigger view change
					getViewManager().viewChanged();

					// Refresh the parent
					getView().refresh();

				}

			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PCMMASSESS_TITLE),
						RscTools.getString(RscConst.ERR_PCMMASSESS_UPDATING) + property
								+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			}
		}
	}

	/**
	 * Clear multiple assessments for a same role/user/element
	 * 
	 * @param id      the id of the kept assessment
	 * @param role    the role
	 * @param user    the user
	 * @param element the element
	 * @throws CredibilityException if an error occured during clearing process.
	 */
	void clearAssessment(Integer id, Role role, User user, PCMMElement element) throws CredibilityException {
		// Get assessments to clear
		List<PCMMAssessment> assessments = getViewManager().getAppManager().getService(IPCMMAssessmentApp.class)
				.getAssessmentByRoleAndUserAndEltAndTag(role, user, element, getViewManager().getSelectedTag());

		// Delete assessments
		for (PCMMAssessment assessment : assessments) {
			if (id == null || (!id.equals(assessment.getId()))) {
				getViewManager().getAppManager().getService(IPCMMAssessmentApp.class).deleteAssessment(assessment);
			}

		}
	}

	/**
	 * Clear multiple assessments for a same role/user/element
	 * 
	 * @param id         the id of the kept assessment
	 * @param role       the role
	 * @param user       the user
	 * @param subelement the subelement
	 * @throws CredibilityException if an error occured during clearing process.
	 */
	void clearAssessment(Integer id, Role role, User user, PCMMSubelement subelement) throws CredibilityException {
		// Get assessments to clear
		List<PCMMAssessment> assessments = getViewManager().getAppManager().getService(IPCMMAssessmentApp.class)
				.getAssessmentByRoleAndUserAndSubeltAndTag(role, user, subelement, getViewManager().getSelectedTag());

		// Delete assessments
		for (PCMMAssessment assessment : assessments) {
			if (id == null || (!id.equals(assessment.getId()))) {
				getViewManager().getAppManager().getService(IPCMMAssessmentApp.class).deleteAssessment(assessment);
			}

		}
	}

	/**
	 * Delete the assessment in parameter after user confirmation.
	 *
	 * @param element the element
	 */
	void delete(Object element) {

		/**
		 * Check the PCMM mode
		 */
		PCMMAssessment assessment = null;
		if (PCMMMode.DEFAULT.equals(pcmmConfiguration.getMode()) && element instanceof PCMMSubelement) {
			// get the selected sub-element to delete the assessment with
			assessment = assessmentsBySubelt.get(element);
		} else if (PCMMMode.SIMPLIFIED.equals(pcmmConfiguration.getMode()) && element instanceof PCMMElement) {
			// get the selected element to delete the assessment with
			assessment = assessmentsByElt.get(element);
		}

		if (assessment == null) {
			return;
		}

		// constructs confirm message
		String title = RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_TITLE);
		String message = RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_DELETE_ASSESSMENT,
				assessment.getSubelement() != null ? assessment.getSubelement().getName() : RscTools.empty());

		if (PCMMMode.SIMPLIFIED.equals(getPCMMMode())) {
			title = RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_SIMPLIFIED_TITLE);
			message = RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_DELETE_ASSESSMENT_SIMPLIFIED,
					assessment.getElement() != null ? assessment.getElement().getName() : RscTools.empty());
		}

		// confirm dialog
		boolean confirm = MessageDialog.openConfirm(getView().getShell(), title, message);

		if (confirm) {
			try {

				// delete the assessment
				getViewManager().getAppManager().getService(IPCMMAssessmentApp.class).deleteAssessment(assessment);

				// trigger view change
				getViewManager().viewChanged();

			} catch (CredibilityException e) {
				logger.error(RscTools.getString(RscConst.ERR_PCMMASSESS_DELETE + e.getMessage()), e);
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PCMMASSESS_TITLE),
						RscTools.getString(RscConst.ERR_PCMMASSESS_DELETE + e.getMessage()));
			}

			// Refresh
			getView().refresh();
		}
	}

	/**
	 * @return a map of assessements indexed by subelement
	 */
	public Map<PCMMSubelement, PCMMAssessment> getAssessmentsBySubelt() {
		return assessmentsBySubelt;
	}

	/**
	 * @return a map of assessements indexed by element
	 */
	public Map<PCMMElement, PCMMAssessment> getAssessmentsByElt() {
		return assessmentsByElt;
	}

	/**
	 * @param columnName the column name
	 * @return the column properties of the viewer
	 */
	public int getColumnIndex(String columnName) {
		return getView().getColumnProperties().indexOf(columnName);
	}

	/**
	 * @return the pcmm element
	 */
	public PCMMElement getElementSelected() {
		return elementSelected;
	}

	/**
	 * Resets the pcmm element selected
	 * 
	 * @param pcmmElement the element to set
	 */
	public void setElementSelected(PCMMElement pcmmElement) {
		this.elementSelected = pcmmElement;

		// Refresh
		refresh();
	}

	/**
	 * Gets the pcmm configuration.
	 *
	 * @return the pcmm configuration
	 */
	public PCMMSpecification getPcmmConfiguration() {
		return pcmmConfiguration;
	}

	/**
	 * Gets the elements.
	 *
	 * @return the elements
	 */
	public List<PCMMElement> getElements() {
		return elements;
	}

	/**
	 * @return the current PCMM Mode
	 */
	PCMMMode getPCMMMode() {
		return pcmmConfiguration.getMode();
	}

	/**
	 * @param element the element
	 * @return true if the element in parameter is contained in the current selected
	 *         element of the parent view
	 */
	boolean isFromCurrentPCMMElement(PCMMElement element) {
		return getElementSelected() != null && (getElementSelected().equals(element));
	}

	/**
	 * {@link PCMMViewManager#isTagMode()}
	 * 
	 * @return true if the selected tag is a real tag, otherwise false
	 */
	public boolean isTagMode() {
		return getViewManager().isTagMode();
	}

}
