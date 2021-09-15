/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

/**
 * CF Import Action type
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public enum ImportActionType {
	TO_ADD, TO_DELETE, TO_UPDATE, NO_CHANGES;

	ImportActionType() {
	}
}
