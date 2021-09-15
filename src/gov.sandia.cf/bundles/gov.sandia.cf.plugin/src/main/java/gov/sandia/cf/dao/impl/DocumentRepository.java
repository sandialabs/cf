/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IDocumentRepository;
import gov.sandia.cf.model.Document;

/**
 * Model entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class DocumentRepository extends AbstractCRUDRepository<Document, Integer> implements IDocumentRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public DocumentRepository() {
		super(Document.class);
	}

	/**
	 * ModelRepository constructor
	 * 
	 * @param entityManager
	 *            the entity manager for this repository to execute queries (must
	 *            not be null)
	 * 
	 */
	public DocumentRepository(EntityManager entityManager) {
		super(entityManager, Document.class);
	}

}