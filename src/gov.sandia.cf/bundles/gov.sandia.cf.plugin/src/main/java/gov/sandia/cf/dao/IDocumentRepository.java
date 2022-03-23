/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import gov.sandia.cf.model.Document;

/**
 * the Document repository interface
 * 
 * @author Didier Verstraete
 *
 */
@Repository
public interface IDocumentRepository extends ICRUDRepository<Document, Integer> {

}
