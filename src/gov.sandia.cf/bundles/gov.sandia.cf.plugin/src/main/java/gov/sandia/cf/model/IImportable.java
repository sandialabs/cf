/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

/**
 * The CF model Importable interface
 * 
 * @author Didier Verstraete
 **
 * @param <M> the model type
 */
public interface IImportable<M> {

	/**
	 * @param newImportable the importable to compare with
	 * @return true if the importable in parameter has the same field values as this
	 *         class.
	 */
	boolean sameAs(M newImportable);

	/**
	 * @return the importable abstract
	 */
	String getAbstract();
}
