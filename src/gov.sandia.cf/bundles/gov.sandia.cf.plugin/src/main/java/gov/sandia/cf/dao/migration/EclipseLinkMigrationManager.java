/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.internal.sessions.ArrayRecord;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.cmdline.SqlToolError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.DaoManager;
import gov.sandia.cf.dao.IDBMigrationManager;
import gov.sandia.cf.dao.IMigrationLogRepository;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.dao.hsqldb.HSQLDBDaoManager;
import gov.sandia.cf.dao.migration.tasks.Task_001_QoIITag;
import gov.sandia.cf.dao.migration.tasks.Task_002_RichTextContent;
import gov.sandia.cf.dao.migration.tasks.Task_003_PlanningFieldLongVarchar;
import gov.sandia.cf.dao.migration.tasks.Task_004_GenericParameterRequired;
import gov.sandia.cf.dao.migration.tasks.Task_005_ConfigurationFileTable;
import gov.sandia.cf.dao.migration.tasks.Task_006_SystemRequirementStatementField;
import gov.sandia.cf.dao.migration.tasks.Task_007_ARGParametersLongVarcharFields;
import gov.sandia.cf.dao.migration.tasks.Task_008_UnknownCreationUserXorDate;
import gov.sandia.cf.dao.migration.tasks.Task_009_EvidenceValueToGson;
import gov.sandia.cf.dao.migration.tasks.Task_010_ARGParam_Variable;
import gov.sandia.cf.dao.migration.tasks.Task_011_EvidenceNoAssessable;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * SQL Database version migration class. This class is used to launch version
 * migration change scripts before loading the database schema.
 * 
 * It is specific to EclipseLink and must be re-implemented if the ORM changes.
 * 
 * @author Didier Verstraete
 *
 */
public class EclipseLinkMigrationManager implements IDBMigrationManager {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(EclipseLinkMigrationManager.class);

	/**
	 * The database manager
	 */
	private DaoManager daoManager;

	/**
	 * QUERIES
	 */
	/** Check Table existence query parameter */
	public static final String CHECK_TABLE_EXISTENCE_PARAMETER = "EXISTS_PARAM"; //$NON-NLS-1$
	/** Check Table existence query */
	public static final String CHECK_TABLE_EXISTENCE_QUERY = "SELECT TRUE AS " + CHECK_TABLE_EXISTENCE_PARAMETER //$NON-NLS-1$
			+ " FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE=''BASE TABLE'' AND TABLE_NAME=''{0}''"; //$NON-NLS-1$
	/** Check Table and Column existence query */
	public static final String CHECK_TABLE_AND_COLUMN_EXISTENCE_QUERY = "SELECT TRUE AS " //$NON-NLS-1$
			+ CHECK_TABLE_EXISTENCE_PARAMETER
			+ " FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=''{0}'' AND COLUMN_NAME=''{1}''"; //$NON-NLS-1$

	/**
	 * FOLDER and FILES
	 */
	/** SQL Script Folder */
	public static final String SCRIPT_FOLDER_PATH = "sql/migration/";//$NON-NLS-1$

	/** Error log file suffix */
	public static final String ERROR_LOG_FILE_SUFFIX = "err.log"; //$NON-NLS-1$

	/**
	 * SQL Parser variables
	 */
	/** SQL file extension */
	public static final String SQL_FILE_SUFFIX = ".sql";//$NON-NLS-1$
	/** SQL COMMENT character */
	public static final String SQL_COMMENT_CHAR = "--";//$NON-NLS-1$
	/** SQL END OF QUERY character */
	public static final String SQL_ENDOFQUERY_CHAR = ";";//$NON-NLS-1$
	/** SQL CARRIAGE RETURN character */
	public static final String SQL_CARRIAGERETURN_CHAR = "\n";//$NON-NLS-1$
	/** SQL QUOTE character */
	public static final String SQL_QUOTE_CHAR = "\'";//$NON-NLS-1$

	private static final Map<Integer, IMigrationTask> MIGRATION_TASKS;
	static {
		MIGRATION_TASKS = new TreeMap<>();
		MIGRATION_TASKS.put(1, new Task_001_QoIITag());
		MIGRATION_TASKS.put(2, new Task_002_RichTextContent());
		MIGRATION_TASKS.put(3, new Task_003_PlanningFieldLongVarchar());
		MIGRATION_TASKS.put(4, new Task_004_GenericParameterRequired());
		MIGRATION_TASKS.put(5, new Task_005_ConfigurationFileTable());
		MIGRATION_TASKS.put(6, new Task_006_SystemRequirementStatementField());
		MIGRATION_TASKS.put(7, new Task_007_ARGParametersLongVarcharFields());
		MIGRATION_TASKS.put(8, new Task_008_UnknownCreationUserXorDate());
		MIGRATION_TASKS.put(9, new Task_009_EvidenceValueToGson());
		MIGRATION_TASKS.put(10, new Task_010_ARGParam_Variable());
		MIGRATION_TASKS.put(11, new Task_011_EvidenceNoAssessable());
	}

	/**
	 * The constructor
	 * 
	 * @param daoManager the dao manager
	 */
	public EclipseLinkMigrationManager(DaoManager daoManager) {
		if (daoManager == null) {
			throw new IllegalArgumentException(RscTools.getString(RscConst.EX_DBDAOMANAGER_DAOMANAGER_NULL));
		}
		this.daoManager = daoManager;
	}

	@Override
	public void executeMigration() throws CredibilityException, SqlToolError, SQLException, IOException,
			URISyntaxException, CredibilityMigrationException {

		// get the unit of work
		UnitOfWork unitOfWork = daoManager.getEntityManager().unwrap(UnitOfWork.class);

		// check if the database has been generated by Eclipse Link
		// check if the table MODEL exists
		boolean existsTableModel = existsTable(unitOfWork,
				Model.class.getAnnotation(javax.persistence.Table.class).name());

		if (existsTableModel) {

			// Get tasks to execute
			SortedMap<Integer, IMigrationTask> migrationTasksToExecute = getTasksToExecute(unitOfWork);

			// execute migration tasks
			if (migrationTasksToExecute != null) {

				String databaseVersion = daoManager.getRepository(IModelRepository.class).getDatabaseVersion();

				for (Entry<Integer, IMigrationTask> entry : migrationTasksToExecute.entrySet()) {
					IMigrationTask task = entry.getValue();
					if (task != null) {
						try {
							task.execute(daoManager);

							// mark task executed
							daoManager.getRepository(IMigrationLogRepository.class).markTaskAsExecuted(task,
									databaseVersion);

							logger.info("Migration task [{}] committed", task.getName()); //$NON-NLS-1$
						} catch (CredibilityException | CredibilityMigrationException e) {
							daoManager.getRepository(IMigrationLogRepository.class).markTaskInError(task,
									databaseVersion, e.getMessage());

							// write the error log in the workspace at the same level as the .cf file
							String errorMsg = "An error occured during script [{0}] execution.\nThe database version was {1}.\nThe log is:\n{2}\n"; //$NON-NLS-1$
							createErrorLogFileInWorkspace(daoManager,
									MessageFormat.format(errorMsg, task.getName(), databaseVersion, e.getMessage()));

							throw e;
						}
					}
				}
			}
		}
	}

	/**
	 * @param unitOfWork used to execute sql queries
	 * @return the sql scripts list to execute on the current database version. This
	 *         method get the current version from VERSION column in the MODEL table
	 *         and compare it with the name of the scripts listed into the
	 *         SCRIPT_FOLDER_PATH.
	 * @throws IOException                   if an error occured while reading
	 *                                       script.
	 * @throws URISyntaxException            if the script path is not valid.
	 * @throws CredibilityMigrationException if one task is not valid
	 */
	public SortedMap<Integer, IMigrationTask> getTasksToExecute(UnitOfWork unitOfWork)
			throws URISyntaxException, IOException, CredibilityMigrationException {

		SortedMap<Integer, IMigrationTask> tasksToExecute = new TreeMap<>();

		// check task to execute
		// the tasks are ordered in the treemap. Use the same order
		for (Entry<Integer, IMigrationTask> entry : MIGRATION_TASKS.entrySet()) {
			if (isValidScript(entry.getValue(), unitOfWork)) {
				tasksToExecute.put(entry.getKey(), entry.getValue());
			}
		}

		return tasksToExecute;
	}

	/**
	 * @param task       the migration task to execute
	 * @param unitOfWork to execute sql queries
	 * @return true if the current script can be executed and has not been executed
	 *         on the current database, or has been executed but is in error,
	 *         otherwise false.
	 * @throws CredibilityMigrationException if the task is not valid
	 */
	public boolean isValidScript(IMigrationTask task, UnitOfWork unitOfWork) throws CredibilityMigrationException {

		boolean isValid = false;

		if (task == null) {
			throw new CredibilityMigrationException(RscTools.getString(RscConst.EX_MIGRATIONDAO_TASK_NULL));
		} else if (StringUtils.isBlank(task.getName())) {
			throw new CredibilityMigrationException(RscTools.getString(RscConst.EX_MIGRATIONDAO_TASKNAME_BLANK));
		}

		Boolean lastExecutionInError = daoManager.getRepository(IMigrationLogRepository.class)
				.isLastExecutionInError(task.getName());

		// if the script is not in the table, execute it
		if (lastExecutionInError == null) {
			isValid = true;
		} else {
			// if the script has been executed and is in error, try to re-execute it
			isValid = lastExecutionInError;
		}

		return isValid;
	}

	/**
	 * @param unitOfWork the unit of work to query
	 * @param table      the database table
	 * @return true if the table in parameter exists in the database, otherwise
	 *         false
	 */
	public static boolean existsTable(UnitOfWork unitOfWork, String table) {
		boolean existsTable = false;

		List<?> result = unitOfWork.executeSQL(MessageFormat.format(CHECK_TABLE_EXISTENCE_QUERY, table));
		if (result != null) {
			Iterator<?> iterator = result.iterator();
			if (iterator.hasNext()) {
				ArrayRecord next = (ArrayRecord) iterator.next();
				Object returned = next.get(CHECK_TABLE_EXISTENCE_PARAMETER);
				if (returned != null) {
					existsTable = Boolean.valueOf(returned.toString());
				}
			}
		}
		return existsTable;
	}

	/**
	 * @param unitOfWork the unit of work to query
	 * @param table      the database table to check
	 * @param field      the table field to check
	 * @return true if the table field in parameter exists in the database,
	 *         otherwise false.
	 */
	public static boolean existsColumnInTable(UnitOfWork unitOfWork, String table, String field) {
		boolean existsTable = false;

		List<?> result = unitOfWork
				.executeSQL(MessageFormat.format(CHECK_TABLE_AND_COLUMN_EXISTENCE_QUERY, table, field));
		if (result != null) {
			Iterator<?> iterator = result.iterator();
			if (iterator.hasNext()) {
				ArrayRecord next = (ArrayRecord) iterator.next();
				Object returned = next.get(CHECK_TABLE_EXISTENCE_PARAMETER);
				if (returned != null) {
					existsTable = Boolean.valueOf(returned.toString());
				}
			}
		}
		return existsTable;
	}

	/**
	 * 
	 * Execute the script in parameter.
	 * 
	 * @param unitOfWork used to execute sql queries
	 * @param script     the sql script to execute
	 * 
	 * @throws SQLException       if a sql exception occurs
	 * @throws IOException        if a file read exception occurs
	 * @throws SqlToolError       if a sql tool error occurs
	 * @throws URISyntaxException if a file path is not valid
	 */
	public static void executeSQLScript(UnitOfWork unitOfWork, String script)
			throws SQLException, IOException, SqlToolError, URISyntaxException {
		logger.info("Executing sql migration script: {}", script); //$NON-NLS-1$

		// get the jdbc url
		String jdbcUrl = null;
		Connection connection = null;
		if (unitOfWork.getActiveSession() != null) {
			jdbcUrl = (String) unitOfWork.getActiveSession().getProperty(HSQLDBDaoManager.JDBC_URL);
		}

		try {
			// get the jdbc connection
			connection = DriverManager.getConnection(jdbcUrl);

			connection.setAutoCommit(false);

			// get the script path
			Path scriptPath = Paths.get(WorkspaceTools.getStaticFilePath(SCRIPT_FOLDER_PATH + script));

			// create a sql script
			SqlFile sqlFile = new SqlFile(new File(scriptPath.toString()));
			sqlFile.setConnection(connection);

			// execute the script
			sqlFile.execute();
			connection.commit();
			connection.setAutoCommit(true);

		} catch (SQLException | IOException | SqlToolError | URISyntaxException e) {
			if (connection != null) {
				connection.rollback();
			}

			throw e;
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * @param scriptPath the script to parse and split
	 * @return the sql queries contained in the script file. This method reads all
	 *         the lines from the script and parse it to delete the comments and
	 *         transform it to a list of executable sql queries.
	 * @throws IOException if an error occured while reading script.
	 */
	public List<String> splitSqlQueries(Path scriptPath) throws IOException {

		List<String> queries = new ArrayList<>();

		StringBuilder contentBuilder = new StringBuilder();

		// read file lines
		Stream<String> stream = Files.lines(scriptPath, StandardCharsets.UTF_8);

		// drop comment lines
		List<String> filter = stream.filter(s -> !s.startsWith(SQL_COMMENT_CHAR) && !s.trim().isEmpty()
				&& !s.contentEquals(SQL_CARRIAGERETURN_CHAR)).collect(Collectors.toList());
		stream.close();

		if (!filter.isEmpty()) {
			// concatenate file

			filter.forEach(contentBuilder::append);

			// split queries
			String[] split = contentBuilder.toString().split(SQL_ENDOFQUERY_CHAR);

			// to list
			queries = Arrays.asList(split);
		}

		return queries;
	}

	/**
	 * Write the error log into a file in the workspace next to .cf file. The file
	 * will be name "<cf-file-name>-<current date formatted
	 * yyyyMMddhhmmss>-err.log".
	 * 
	 * @param errorlog the error log
	 */
	private static void createErrorLogFileInWorkspace(DaoManager daoManager, String errorlog) {

		try {

			// create a file with the log
			File dbFolder = new File(daoManager.getDatabaseDirectoryPath());

			if (dbFolder.exists()) {

				String cfFileName = dbFolder.getParentFile().getName()
						.replaceAll(FileTools.CREDIBILITY_TMP_FOLDER_ZIPPED_NAME, RscTools.empty())
						.replaceFirst(RscTools.HYPHEN, RscTools.empty());
				File cfFileFolder = dbFolder.getParentFile().getParentFile();
				String errorFilePath = cfFileFolder.getPath() + FileTools.PATH_SEPARATOR + cfFileName + RscTools.HYPHEN
						+ DateTools.formatDate(DateTools.getCurrentDate(), DateTools.getDateFormattedDateTimeHash())
						+ RscTools.HYPHEN + ERROR_LOG_FILE_SUFFIX;

				// create error file
				File errorFile = FileTools.createFile(errorFilePath);

				// add the error log into
				boolean append = true;
				FileTools.writeStringInFile(errorFile, errorlog, append);

			}
		} catch (IOException | CredibilityException e) {
			logger.error("Impossible to create the error file for the sql migration:\n{}", e.getMessage(), e);//$NON-NLS-1$
		}
	}

	/**
	 * @param variable the variable to parse
	 * @return the sql variable with sql quotes escaped.
	 */
	public String sqlVariableQuote(String variable) {
		String sqlVar = variable;
		if (sqlVar != null) {
			sqlVar = sqlVar.replace(SQL_QUOTE_CHAR, SQL_QUOTE_CHAR + SQL_QUOTE_CHAR);
		}
		return sqlVar;
	}

	@SuppressWarnings("javadoc")
	public static Map<Integer, IMigrationTask> getMigrationTasks() {
		return MIGRATION_TASKS;
	}

}
