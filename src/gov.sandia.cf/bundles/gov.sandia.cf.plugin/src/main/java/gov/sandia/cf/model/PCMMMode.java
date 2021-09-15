/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

/**
 * 
 * #114: (see https://gitlab.com/iwf/cf/-/issues/114) The PCMM Mode:
 * 
 * DEFAULT: this is the default mode. PCMM Elements have Subelements. The
 * evidence, assessments, and aggregation is done at the subelement level.
 * 
 * SIMPLIFIED: this mode is activated if there is levels attached to a PCMM
 * Element. The evidence, assessments and aggregation is done at the Element
 * level. Even if one Element has subelements, they will not be considered.
 * 
 * The two modes are exclusive.
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public enum PCMMMode {

	DEFAULT, SIMPLIFIED;

	/**
	 */
	private PCMMMode() {
	}

}
