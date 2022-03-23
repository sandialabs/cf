/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMAssessmentApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * PCMM Assess view controller: Used to control the PCMM Assess view
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAssessViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMAssessViewController.class);

	/**
	 * The view
	 */
	private PCMMAssessView view;

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

	PCMMAssessViewController(PCMMAssessView view) {
		Assert.isNotNull(view);
		this.view = view;
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

			PCMMAssessment assessment = view.getAssessmentsByElt().get(elt);
			if (assessment == null) {
				assessment = new PCMMAssessment();
				assessment.setElement(elt);
			}

			PCMMAssessDialog assessDialog = new PCMMAssessDialog(view.getViewManager(), view.getShell(), assessment);
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

			PCMMAssessment assessment = view.getAssessmentsBySubelt().get(elt);
			if (assessment == null) {
				assessment = new PCMMAssessment();
				assessment.setSubelement(elt);
			}

			PCMMAssessDialog assessDialog = new PCMMAssessDialog(view.getViewManager(), view.getShell(), assessment);
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
			Role currentRole = view.getViewManager().getCurrentUserRole();
			User currentUser = view.getViewManager().getCache().getUser();

			// It's new
			if (null == updatedAssessment.getId() && null == updatedAssessment.getRoleCreation()
					&& null == updatedAssessment.getUserCreation()) {

				// Check element/sub-element according to mode
				if (PCMMMode.DEFAULT.equals(view.getPCMMMode())) {
					//
					if (null != updatedAssessment.getSubelement()) {
						// Clear dirty database
						clearAssessment(updatedAssessment.getId(), updatedAssessment.getRoleCreation(),
								updatedAssessment.getUserCreation(), updatedAssessment.getSubelement());

					} else {
						throw new CredibilityException(
								RscTools.getString(RscConst.EX_PCMM_ADDASSESSTBYID_SUBELEMENTNULL));
					}
				} else if (PCMMMode.SIMPLIFIED.equals(view.getPCMMMode())) {
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
				view.getViewManager().getAppManager().getService(IPCMMAssessmentApp.class)
						.addAssessment(updatedAssessment);

				// trigger view change to save
				view.getViewManager().viewChanged();
			}

			// It's an update
			else if (null != updatedAssessment.getId()) {
				// Check role and User correspond
				if (updatedAssessment.getRoleCreation() == currentRole
						&& updatedAssessment.getUserCreation() == currentUser) {

					// Check mode
					if (PCMMMode.DEFAULT.equals(view.getPCMMMode())) {
						if (null != updatedAssessment.getSubelement()) {
							// Clear dirty database
							clearAssessment(updatedAssessment.getId(), updatedAssessment.getRoleCreation(),
									updatedAssessment.getUserCreation(), updatedAssessment.getSubelement());

						} else {
							throw new CredibilityException(
									RscTools.getString(RscConst.EX_PCMM_ADDASSESSTBYID_SUBELEMENTNULL));
						}
					} else if (PCMMMode.SIMPLIFIED.equals(view.getPCMMMode())) {
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
					view.getViewManager().getAppManager().getService(IPCMMAssessmentApp.class)
							.updateAssessment(updatedAssessment, currentUser, currentRole);

					// trigger view change to save
					view.getViewManager().viewChanged();
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
			view.refresh();

		} catch (CredibilityException e) {
			logger.error("An error has occurred while loading assessment data:\n{}", e.getMessage(), e); //$NON-NLS-1$
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PCMMASSESS_ASSESS),
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
			PCMMAssessment assessment = view.getAssessmentsBySubelt().get(subelt);

			boolean updated = false;
			boolean creationMode = false;

			// create or update assessment
			if (assessment == null) {
				assessment = new PCMMAssessment();
				assessment.setRoleCreation(view.getViewManager().getCache().getCurrentPCMMRole());
				assessment.setUserCreation(view.getViewManager().getCache().getUser());
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
						view.getViewManager().getAppManager().getService(IPCMMAssessmentApp.class)
								.addAssessment(assessment);

					} else {
						view.getViewManager().getAppManager().getService(IPCMMAssessmentApp.class).updateAssessment(
								assessment, view.getViewManager().getCache().getUser(),
								view.getViewManager().getCache().getCurrentPCMMRole());
					}

					// trigger view change
					view.getViewManager().viewChanged();

					// Refresh the parent
					view.refresh();

				}

			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PCMMASSESS_TITLE),
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
			int index = getColumnProperties().indexOf(property);

			// retrieve pcmm assessment from element
			PCMMAssessment assessment = view.getAssessmentsByElt().get(elt);

			boolean updated = false;
			boolean creationMode = false;

			// create or update assessment
			if (assessment == null) {
				assessment = new PCMMAssessment();
				assessment.setRoleCreation(view.getViewManager().getCache().getCurrentPCMMRole());
				assessment.setUserCreation(view.getViewManager().getCache().getUser());
				assessment.setElement(view.getFirstElementSelected());
				creationMode = true;
			}

			if (assessment.getElement() == null) {
				assessment.setElement(view.getFirstElementSelected());
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
						view.getViewManager().getAppManager().getService(IPCMMAssessmentApp.class)
								.addAssessment(assessment);

					} else {
						view.getViewManager().getAppManager().getService(IPCMMAssessmentApp.class).updateAssessment(
								assessment, view.getViewManager().getCache().getUser(),
								view.getViewManager().getCache().getCurrentPCMMRole());
					}

					// trigger view change
					view.getViewManager().viewChanged();

					// Refresh the parent
					view.refresh();

				}

			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PCMMASSESS_TITLE),
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
		List<PCMMAssessment> assessments = view.getViewManager().getAppManager().getService(IPCMMAssessmentApp.class)
				.getAssessmentByRoleAndUserAndEltAndTag(role, user, element, view.getViewManager().getSelectedTag());

		// Delete assessments
		for (PCMMAssessment assessment : assessments) {
			if (id == null || (!id.equals(assessment.getId()))) {
				view.getViewManager().getAppManager().getService(IPCMMAssessmentApp.class).deleteAssessment(assessment);
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
		List<PCMMAssessment> assessments = view.getViewManager().getAppManager().getService(IPCMMAssessmentApp.class)
				.getAssessmentByRoleAndUserAndSubeltAndTag(role, user, subelement,
						view.getViewManager().getSelectedTag());

		// Delete assessments
		for (PCMMAssessment assessment : assessments) {
			if (id == null || (!id.equals(assessment.getId()))) {
				view.getViewManager().getAppManager().getService(IPCMMAssessmentApp.class).deleteAssessment(assessment);
			}

		}
	}

	/**
	 * Delete the assessment in parameter after user confirmation
	 * 
	 * @param assessment the assessment to delete
	 */
	void delete(PCMMAssessment assessment) {

		if (assessment != null) {

			// constructs confirm message
			String title = RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_TITLE);
			String message = RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_DELETE_ASSESSMENT,
					assessment.getSubelement() != null ? assessment.getSubelement().getName() : RscTools.empty());

			if (PCMMMode.SIMPLIFIED.equals(view.getPCMMMode())) {
				title = RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_SIMPLIFIED_TITLE);
				message = RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_DELETE_ASSESSMENT_SIMPLIFIED,
						assessment.getElement() != null ? assessment.getElement().getName() : RscTools.empty());
			}

			// confirm dialog
			boolean confirm = MessageDialog.openConfirm(view.getShell(), title, message);

			if (confirm) {
				try {

					// delete the assessment
					view.getViewManager().getAppManager().getService(IPCMMAssessmentApp.class)
							.deleteAssessment(assessment);

					// trigger view change
					view.getViewManager().viewChanged();

				} catch (CredibilityException e) {
					logger.error(RscTools.getString(RscConst.ERR_PCMMASSESS_DELETE + e.getMessage()), e);
					MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PCMMASSESS_TITLE),
							RscTools.getString(RscConst.ERR_PCMMASSESS_DELETE + e.getMessage()));
				}

				// Refresh
				view.refresh();
			}
		}
	}

	/**
	 * @return a map of assessements indexed by subelement
	 */
	public Map<PCMMSubelement, PCMMAssessment> getAssessmentsBySubelt() {
		return view.getAssessmentsBySubelt();
	}

	/**
	 * @return a map of assessements indexed by element
	 */
	public Map<PCMMElement, PCMMAssessment> getAssessmentsByElt() {
		return view.getAssessmentsByElt();
	}

	/**
	 * @return the column properties of the viewer
	 */
	public List<String> getColumnProperties() {
		return view.getColumnProperties();
	}

	/**
	 * @param columnName the column name
	 * @return the column properties of the viewer
	 */
	public int getColumnIndex(String columnName) {
		return view.getColumnProperties().indexOf(columnName);
	}

	/**
	 * @return the pcmm element selected
	 */
	public PCMMElement getPcmmElement() {
		return view.getPcmmElement();
	}

	/**
	 * {@link PCMMViewManager#isTagMode()}
	 * 
	 * @return true if the selected tag is a real tag, otherwise false
	 */
	public boolean isTagMode() {
		return view.getViewManager().isTagMode();
	}

}
