/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;

/**
 * The table to store the SQL migration log
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "MIGRATION_LOG")
public class MigrationLog implements Serializable, IEntity<MigrationLog, Integer> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Field Filter
	 */
	@SuppressWarnings("javadoc")
	public enum Filter implements EntityFilter {
		ID("id"), //$NON-NLS-1$
		DATABASE_VERSION("databaseVersion"), //$NON-NLS-1$
		SCRIPT_NAME("scriptName"), //$NON-NLS-1$
		DATE_EXECUTION("dateExecution"), //$NON-NLS-1$
		IS_ERROR("isError"), //$NON-NLS-1$
		EXECUTION_LOG("executionLog"); //$NON-NLS-1$

		private String field;

		Filter(String field) {
			this.field = field;
		}

		public String getField() {
			return this.field;
		}
	}

	/**
	 * The id field linked to ID column
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	/**
	 * The databaseVersion field linked to DATABASE_VERSION column
	 */
	@Column(name = "DATABASE_VERSION")
	private String databaseVersion;

	/**
	 * The scriptName field linked to SCRIPT_NAME column
	 */
	@Column(name = "SCRIPT_NAME")
	@NotBlank(message = RscConst.EX_MIGRATIONLOG_SCRIPTNAME_BLANK)
	private String scriptName;

	/**
	 * The dateExecution field linked to DATE_EXECUTION column
	 */
	@Column(name = "DATE_EXECUTION")
	@NotNull(message = RscConst.EX_MIGRATIONLOG_DATEEXECUTION_NULL)
	private Date dateExecution;

	/**
	 * The isError field linked to IS_ERROR column
	 */
	@Column(name = "IS_ERROR")
	private Boolean isError;

	/**
	 * The executionLog field linked to EXECUTION_LOG column
	 */
	@Column(name = "EXECUTION_LOG", columnDefinition = "LONGVARCHAR")
	private String executionLog;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public String getDatabaseVersion() {
		return databaseVersion;
	}

	@SuppressWarnings("javadoc")
	public void setDatabaseVersion(String databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	@SuppressWarnings("javadoc")
	public String getScriptName() {
		return scriptName;
	}

	@SuppressWarnings("javadoc")
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	@SuppressWarnings("javadoc")
	public Date getDateExecution() {
		return Optional.ofNullable(dateExecution).map(Date::getTime).map(Date::new).orElse(null);
	}

	@SuppressWarnings("javadoc")
	public void setDateExecution(Date dateExecution) {
		this.dateExecution = Optional.ofNullable(dateExecution).map(Date::getTime).map(Date::new).orElse(dateExecution);
	}

	@SuppressWarnings("javadoc")
	public Boolean getIsError() {
		return isError;
	}

	@SuppressWarnings("javadoc")
	public void setIsError(Boolean isError) {
		this.isError = isError;
	}

	@SuppressWarnings("javadoc")
	public String getExecutionLog() {
		return executionLog;
	}

	@SuppressWarnings("javadoc")
	public void setExecutionLog(String executionLog) {
		this.executionLog = executionLog;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MigrationLog copy() {
		MigrationLog copy = new MigrationLog();
		copy.setDatabaseVersion(getDatabaseVersion());
		copy.setDateExecution(getDateExecution());
		copy.setExecutionLog(getExecutionLog());
		copy.setIsError(getIsError());
		copy.setScriptName(getScriptName());
		return copy;
	}

}
