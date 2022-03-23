/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.imports;

import java.io.File;
import java.io.IOException;

import gov.sandia.cf.exceptions.CredibilityException;

/**
 * The Interface IYmlReader.
 *
 * @author Didier Verstraete
 * @param <S> the specification type
 */
public interface IYmlReader<S> {

	/**
	 * @param ymlSchema the YML configuration file
	 * @return a Specification class loaded with @param ymlSchema.
	 * @throws CredibilityException if an error occurs while processing file.
	 * @throws IOException          if a reading exception occurs
	 */
	public S load(File ymlSchema) throws CredibilityException, IOException;

	/**
	 * @param ymlSchema the file to scan.
	 * @return true if the file contains one of the Communicate keys, otherwise
	 *         false.
	 */
	public boolean isValid(File ymlSchema);
}
