/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.DocumentRepository;
import gov.sandia.cf.model.Document;

/**
 * JUnit class to test the DocumentRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class DocumentRepositoryTest extends AbstractTestRepository<Document, Integer, DocumentRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(DocumentRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<DocumentRepository> getRepositoryClass() {
		return DocumentRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<Document> getModelClass() {
		return Document.class;
	}

	@Override
	Document getModelFulfilled(Document model) {
		fulfillModelStrings(model);
		return model;
	}

}
