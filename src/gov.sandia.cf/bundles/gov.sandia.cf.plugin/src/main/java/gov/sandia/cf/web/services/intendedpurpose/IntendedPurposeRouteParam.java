/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.intendedpurpose;

/**
 * The Class Intended Purpose Route.
 * 
 * @author Didier Verstraete
 */
public class IntendedPurposeRouteParam {

	private static final String LOCK_LOCKINFO_VAR = "lockinfo"; //$NON-NLS-1$
	private static final String LOCK_INFO_VAR = "information"; //$NON-NLS-1$
	private static final String LOCK_TOKEN_VAR = "token"; //$NON-NLS-1$
	private static final String PURPOSE_INTENDEDPURPOSE_VAR = "intendedPurpose"; //$NON-NLS-1$

	private IntendedPurposeRouteParam() {
		// Do not implement
	}

	/**
	 * Lock info param.
	 *
	 * @return the string
	 */
	public static String lockInfo() {
		return LOCK_LOCKINFO_VAR;
	}

	/**
	 * Information.
	 *
	 * @return the string
	 */
	public static String information() {
		return LOCK_INFO_VAR;
	}

	/**
	 * Lock token.
	 *
	 * @return the string
	 */
	public static String lockToken() {
		return LOCK_TOKEN_VAR;
	}

	/**
	 * Intended purpose.
	 *
	 * @return the string
	 */
	public static String intendedPurpose() {
		return PURPOSE_INTENDEDPURPOSE_VAR;
	}
}
