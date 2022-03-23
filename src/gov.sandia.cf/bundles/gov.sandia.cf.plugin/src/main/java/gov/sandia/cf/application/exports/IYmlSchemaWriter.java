/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.exports;

import java.io.File;
import java.io.IOException;

import gov.sandia.cf.exceptions.CredibilityException;

/**
 * @author Didier Verstraete
 *
 * @param <S> the specification type
 */
public interface IYmlSchemaWriter<S> {

	/**
	 * @param ymlSchema the YML configuration file
	 * @return a Specification class loaded with @param ymlSchema.
	 * @throws CredibilityException if an error occurs while processing file.
	 */
	/**
	 * @param cfSchemaFile  the yaml cf schema configuration file
	 * @param specification the specification to write
	 * @param withIds       add the id field to the export
	 * @param append        append to the output file or erase it
	 * 
	 * @throws CredibilityException if an error occurs while processing file
	 * @throws IOException          if one of the files is not found
	 */
	public void writeSchema(final File cfSchemaFile, final S specification, final boolean withIds, final boolean append)
			throws CredibilityException, IOException;
}
