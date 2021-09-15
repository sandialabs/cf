/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.pirt;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.configuration.IYmlReader;
import gov.sandia.cf.application.configuration.YmlReaderGlobal;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.tools.RscTools;

/**
 * This class loads credibility PIRT queries. The actual implementation is
 * stored in a yaml file
 * 
 * @author Didier Verstraete
 *
 */
public class YmlReaderPIRTQueries implements IYmlReader<List<PIRTQuery>> {

	/**
	 * the loggerF
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlReaderPIRTQueries.class);
	/**
	 * Defines all file keywords constants to link to query file
	 */
	/**
	 * CONF_PIRT constant
	 */
	public static final String CONF_PIRT = "PIRT"; //$NON-NLS-1$
	/**
	 * CONF_PIRT_QUERIES constant
	 */
	public static final String CONF_PIRT_QUERIES = "Queries"; //$NON-NLS-1$
	/**
	 * CONF_PIRT_QUERIES_NAME constant
	 */
	public static final String CONF_PIRT_QUERIES_NAME = "Name"; //$NON-NLS-1$
	/**
	 * CONF_PIRT_QUERIES_QUERY constant
	 */
	public static final String CONF_PIRT_QUERIES_QUERY = "Query"; //$NON-NLS-1$
	/**
	 * CONF_PIRT_QUERIES_RESULTTYPE constant
	 */
	public static final String CONF_PIRT_QUERIES_RESULTTYPE = "ResultType"; //$NON-NLS-1$
	/**
	 * CONF_PIRT_QUERIES_CRITERIA constant
	 */
	public static final String CONF_PIRT_QUERIES_CRITERIA = "Criteria"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PIRTQuery> load(File yamlQueryFile) throws CredibilityException, IOException {
		return read(yamlQueryFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(File ymlSchema) {

		try (FileReader fileReader = new FileReader(ymlSchema)) {

			// yaml reader
			Map<?, ?> yamlSpecifications = new Yaml().load(fileReader);

			if (yamlSpecifications != null) {

				if (yamlSpecifications.get(CONF_PIRT) instanceof Map) {
					return true;
				}

				// retrieve level colors
				if (yamlSpecifications.containsKey(CONF_PIRT_QUERIES)) {
					return true;
				}

			}
		} catch (Exception e) {
			logger.warn("PIRT Query file not valid", e); //$NON-NLS-1$
		}

		return false;
	}

	/**
	 * @param yamlQueriesFile the yaml query files
	 * @return the pirt query list
	 * 
	 *         throws FileNotFoundException: if @param yamlFile is not found or does
	 *         not exist
	 * @throws IOException          if an error occured while reading the file
	 * @throws CredibilityException if an error occurs during reading process
	 */
	private List<PIRTQuery> read(File yamlQueriesFile) throws CredibilityException, IOException {

		List<PIRTQuery> pirtQueries = new ArrayList<>();

		// read
		Map<String, Object> yamlSpecifications = YmlReaderGlobal.loadYmlFile(yamlQueriesFile);

		if (yamlSpecifications != null) {

			// read PIRT specifications
			Map<?, ?> yamlPIRTSpecifications = yamlSpecifications;
			if (yamlSpecifications.get(YmlReaderPIRTQueries.CONF_PIRT) instanceof Map) {
				yamlPIRTSpecifications = (Map<?, ?>) yamlSpecifications.get(YmlReaderPIRTQueries.CONF_PIRT);
			}

			if (yamlPIRTSpecifications != null) {

				// retrieve queries
				if (yamlPIRTSpecifications.get(YmlReaderPIRTQueries.CONF_PIRT_QUERIES) instanceof Map) {
					@SuppressWarnings("unchecked")
					Map<String, Map<String, Object>> yamlQueries = (Map<String, Map<String, Object>>) yamlPIRTSpecifications
							.get(YmlReaderPIRTQueries.CONF_PIRT_QUERIES);
					pirtQueries = createPIRTQuery(yamlQueries);
				} else {
					logger.warn("credibility configuration missing " + YmlReaderPIRTQueries.CONF_PIRT_QUERIES + " tag"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}

		return pirtQueries;

	}

	/**
	 * @param yamlPIRTQueries the yaml pirt queries
	 * @return a list of PIRTQuery classes from @param yamlPIRTQueries definition
	 */
	private List<PIRTQuery> createPIRTQuery(Map<String, Map<String, Object>> yamlPIRTQueries) {

		List<PIRTQuery> listPIRTQueries = new ArrayList<>();

		if (yamlPIRTQueries != null) {
			for (Entry<String, Map<String, Object>> mapEntry : yamlPIRTQueries.entrySet()) {

				if (mapEntry != null) {

					String queryId = mapEntry.getKey();
					Map<String, Object> yamlProperties = mapEntry.getValue();

					// retrieve name
					String queryName = (String) yamlProperties.getOrDefault(YmlReaderPIRTQueries.CONF_PIRT_QUERIES_NAME,
							RscTools.empty());

					// retrieve query
					String query = (String) yamlProperties.getOrDefault(YmlReaderPIRTQueries.CONF_PIRT_QUERIES_QUERY,
							RscTools.empty());

					// retrieve result type
					String resultType = (String) yamlProperties
							.getOrDefault(YmlReaderPIRTQueries.CONF_PIRT_QUERIES_RESULTTYPE, RscTools.empty());

					// retrieve criteria
					@SuppressWarnings("unchecked")
					List<String> criteria = (List<String>) yamlProperties
							.getOrDefault(YmlReaderPIRTQueries.CONF_PIRT_QUERIES_CRITERIA, null);

					// create new adequacy column
					PIRTQuery pirtQuery = new PIRTQuery(queryId, queryName, query, resultType, criteria);
					listPIRTQueries.add(pirtQuery);
				}
			}
		}

		return listPIRTQueries;
	}
}
