/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IIntendedPurposeApp;
import gov.sandia.cf.dao.IIntendedPurposeRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Intended Purpose Application manager
 * 
 * @author Didier Verstraete
 * 
 */
public class IntendedPurposeApp extends AApplication implements IIntendedPurposeApp {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(IntendedPurposeApp.class);

	/**
	 * Intended Purpose Application constructor
	 */
	public IntendedPurposeApp() {
		super();
	}

	/**
	 * Intended Purpose Application constructor
	 * 
	 * @param appMgr the application manager
	 */
	public IntendedPurposeApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isIntendedPurposeEnabled(Model model) {
		// Always activated
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public IntendedPurpose get(Model model) throws CredibilityException {

		// check if it does not already exists
		IntendedPurpose intendedPurpose = getDaoManager().getRepository(IIntendedPurposeRepository.class)
				.getFirst(model);

		if (intendedPurpose == null) {
			intendedPurpose = new IntendedPurpose();
			intendedPurpose.setModel(model);
			String currentDate = DateTools.formatDate(DateTools.getCurrentDate(), DateTools.DATE_TIME_UTC_FORMAT);
			logger.debug("Intended Purpose created at {}", currentDate);//$NON-NLS-1$

			intendedPurpose = getDaoManager().getRepository(IIntendedPurposeRepository.class).create(intendedPurpose);
		}

		return intendedPurpose;
	}

	/** {@inheritDoc} */
	@Override
	public IntendedPurpose updateIntendedPurpose(IntendedPurpose intendedPurpose, User userUpdate)
			throws CredibilityException {
		if (intendedPurpose == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_INTENDEDPURPOSE_UPDATE_INTENDEDPURPOSE_NULL));
		} else if (userUpdate == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_INTENDEDPURPOSE_UPDATE_USER_NULL));
		}

		IntendedPurpose intendedPurposeDefault = get(intendedPurpose.getModel());
		IntendedPurpose toReturn = null;

		if (intendedPurposeDefault != null) {

			// set id to merge
			intendedPurpose.setId(intendedPurposeDefault.getId());
			intendedPurpose.setDateUpdate(DateTools.getCurrentDate());
			intendedPurpose.setUserUpdate(userUpdate);

			// update
			toReturn = getDaoManager().getRepository(IIntendedPurposeRepository.class).update(intendedPurpose);
		}

		return toReturn;
	}

}
