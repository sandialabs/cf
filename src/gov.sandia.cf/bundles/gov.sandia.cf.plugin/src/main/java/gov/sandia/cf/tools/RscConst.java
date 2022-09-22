/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

/**
 * Manages resources like icons, images,...
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public class RscConst {

	/*
	 * WARNING: the non-key constants should not be added to this file but to
	 * RscTools class. This file is tested to match with the Message bundle. Every
	 * message key is tested.
	 */

	/************************
	 * Exception Keys
	 ************************/

	/**
	 * Entities
	 */

	/* ConfigurationFile */
	public static final String EX_CONFFILE_PATH_BLANK = "ex.conf_file.path.blank"; //$NON-NLS-1$
	public static final String EX_CONFFILE_FEATURE_NULL = "ex.conf_file.feature.blank"; //$NON-NLS-1$
	public static final String EX_CONFFILE_MODEL_NULL = "ex.conf_file.model.blank"; //$NON-NLS-1$
	public static final String EX_CONFFILE_DATEIMPORT_NULL = "ex.conf_file.date_import.blank"; //$NON-NLS-1$

	/* Criterion */
	public static final String EX_CRITERION_PHENOMENON_NULL = "ex.criterion.phenomenon.null"; //$NON-NLS-1$
	public static final String EX_CRITERION_NAME_BLANK = "ex.criterion.name.blank"; //$NON-NLS-1$
	public static final String EX_CRITERION_TYPE_BLANK = "ex.criterion.type.blank"; //$NON-NLS-1$

	/* Decision */
	public static final String EX_DECISION_TITLE_BLANK = "ex.decision.title.blank"; //$NON-NLS-1$
	public static final String EX_DECISION_MODEL_NULL = "ex.decision.model.user"; //$NON-NLS-1$
	public static final String EX_DECISION_USERCREATION_NULL = "ex.decision.user_creation.null"; //$NON-NLS-1$
	public static final String EX_DECISION_DATECREATION_NULL = "ex.decision.date_creation.null"; //$NON-NLS-1$
	public static final String EX_DECISIONVALUE_DECISION_NULL = "ex.decision_value.decision.null"; //$NON-NLS-1$
	public static final String EX_DECISIONVALUE_GENPARAMETER_NULL = "ex.decision_value.parameter.null"; //$NON-NLS-1$

	/* Generic Parameter */
	public static final String MSG_GENPARAMETER_LEVEL_SEPARATOR = "msg.gen_parameter.level_separator"; //$NON-NLS-1$
	public static final String EX_GENPARAMETER_MODEL_NULL = "ex.gen_parameter.model.null"; //$NON-NLS-1$
	public static final String EX_GENPARAMETER_NAME_BLANK = "ex.gen_parameter.name.blank"; //$NON-NLS-1$
	public static final String EX_GENPARAMETER_TYPE_BLANK = "ex.gen_parameter.type.blank"; //$NON-NLS-1$
	public static final String EX_GENPARAMETER_REQUIRED_NULL = "ex.gen_parameter.required.null"; //$NON-NLS-1$
	public static final String EX_GENPARAMETER_LEVEL_NULL = "ex.gen_parameter.level.null"; //$NON-NLS-1$

	/* Generic Parameter Constraint */
	public static final String EX_GENPARAMETERCONSTRAINT_TYPE_BLANK = "ex.gen_parameter_constraint.type.blank"; //$NON-NLS-1$
	public static final String EX_GENPARAMETERCONSTRAINT_RULE_NULL = "ex.gen_parameter_constraint.rule.blank"; //$NON-NLS-1$
	public static final String EX_GENPARAMETERCONSTRAINT_PARAMETER_NULL = "ex.gen_parameter_constraint.parameter.null"; //$NON-NLS-1$

	/* Generic Value */
	public static final String EX_GENERICVALUE_USERCREATION_NULL = "ex.generic_value.user_creation.null"; //$NON-NLS-1$
	public static final String EX_GENERICVALUE_DATECREATION_NULL = "ex.generic_value.date_creation.null"; //$NON-NLS-1$

	/* Generic Parameter Value List */
	public static final String EX_GENPARAMVALUELIST_GENPARAMETER_NULL = "ex.gen_param_value_list.gen_parameter.null"; //$NON-NLS-1$

	/* Intended Purpose */
	public static final String EX_INTENDEDPURPOSE_DATECREATION_NULL = "ex.intended_purpose.date_creation.null"; //$NON-NLS-1$
	public static final String EX_INTENDEDPURPOSE_USERCREATION_NULL = "ex.intended_purpose.user_creation.null"; //$NON-NLS-1$
	public static final String EX_INTENDEDPURPOSE_MODEL_NULL = "ex.intended_purpose.model.null"; //$NON-NLS-1$

	/* Migration Log */
	public static final String EX_MIGRATIONLOG_DATABASEVERSION_BLANK = "ex.migration_log.database_version.blank"; //$NON-NLS-1$
	public static final String EX_MIGRATIONLOG_SCRIPTNAME_BLANK = "ex.migration_log.script_name.blank"; //$NON-NLS-1$
	public static final String EX_MIGRATIONLOG_DATEEXECUTION_NULL = "ex.migration_log.date_execution.null"; //$NON-NLS-1$

	/* Model */
	public static final String EX_MODEL_VERSION_BLANK = "ex.model.version.blank"; //$NON-NLS-1$
	public static final String EX_MODEL_VERSIONORIGIN_BLANK = "ex.model.version_origin.blank"; //$NON-NLS-1$

	/* PCMM Assessment */
	public static final String EX_PCMMASSESSMENT_CREATIONDATE_NULL = "ex.pcmm_assessment.creation_date.null"; //$NON-NLS-1$
	public static final String EX_PCMMASSESSMENT_ROLE_NULL = "ex.pcmm_assessment.role.null"; //$NON-NLS-1$
	public static final String EX_PCMMASSESSMENT_USER_NULL = "ex.pcmm_assessment.user.null"; //$NON-NLS-1$

	/* PCMM Element */
	public static final String EX_PCMMELEMENT_MODEL_NULL = "ex.pcmm_element.model.null"; //$NON-NLS-1$
	public static final String EX_PCMMELEMENT_NAME_BLANK = "ex.pcmm_element.name.blank"; //$NON-NLS-1$
	public static final String EX_PCMMELEMENT_ABBREV_BLANK = "ex.pcmm_element.abbreviation.blank"; //$NON-NLS-1$

	/* PCMM Evidence */
	public static final String EX_PCMMEVIDENCE_CREATIONDATE_NULL = "ex.pcmm_evidence.creation_date.null"; //$NON-NLS-1$
	public static final String EX_PCMMEVIDENCE_ROLE_NULL = "ex.pcmm_evidence.role.null"; //$NON-NLS-1$
	public static final String EX_PCMMEVIDENCE_USER_NULL = "ex.pcmm_evidence.user.null"; //$NON-NLS-1$
	public static final String EX_PCMMEVIDENCE_PATH_BLANK = "ex.pcmm_evidence.value.blank"; //$NON-NLS-1$

	/* PCMM Level */
	public static final String EX_PCMMLEVEL_NAME_BLANK = "ex.pcmm_level.name.blank"; //$NON-NLS-1$
	public static final String EX_PCMMLEVEL_CODE_NULL = "ex.pcmm_level.code.null"; //$NON-NLS-1$

	/* PCMM Level Descriptor */
	public static final String EX_PCMMLEVELDESCRIPTOR_LEVEL_NULL = "ex.pcmm_level_descriptor.level.null"; //$NON-NLS-1$
	public static final String EX_PCMMLEVELDESCRIPTOR_NAME_BLANK = "ex.pcmm_level_descriptor.name.blank"; //$NON-NLS-1$

	/* PCMM Option */
	public static final String EX_PCMMOPTION_PHASE_NULL = "ex.pcmm_option.phase.null"; //$NON-NLS-1$

	/* PCMM Planning Value */
	public static final String EX_PCMMPLANNINGVALUE_PCMMPLANNINGPARAM_NULL = "ex.pcmm_planning_value.parameter.null"; //$NON-NLS-1$

	/* PCMM Subelement */
	public static final String EX_PCMMSUBELEMENT_PCMMELEMENT_NULL = "ex.pcmm_subelement.pcmm_element.null"; //$NON-NLS-1$
	public static final String EX_PCMMSUBELEMENT_CODE_BLANK = "ex.pcmm_subelement.code.blank"; //$NON-NLS-1$
	public static final String EX_PCMMSUBELEMENT_NAME_BLANK = "ex.pcmm_subelement.name.blank"; //$NON-NLS-1$

	/* Phenomenon */
	public static final String EX_PHENOMENON_GROUP_NULL = "ex.phenomenon.group.null"; //$NON-NLS-1$
	public static final String EX_PHENOMENON_NAME_BLANK = "ex.phenomenon.name.blank"; //$NON-NLS-1$

	/* Phenomenon Group */
	public static final String EX_PHENOMENONGROUP_QOI_NULL = "ex.phenomenon_group.qoi.null"; //$NON-NLS-1$
	public static final String EX_PHENOMENONGROUP_NAME_BLANK = "ex.phenomenon_group.name.blank"; //$NON-NLS-1$

	/* PIRT Adequacy Column */
	public static final String EX_PIRTADEQUACYCOLUMN_NAME_BLANK = "ex.pirt_adequacy_column.name.blank"; //$NON-NLS-1$
	public static final String EX_PIRTADEQUACYCOLUMN_IDLABEL_BLANK = "ex.pirt_adequacy_column.id_label.blank"; //$NON-NLS-1$
	public static final String EX_PIRTADEQUACYCOLUMN_TYPE_BLANK = "ex.pirt_adequacy_column.type.blank"; //$NON-NLS-1$

	/* PIRT Adequacy Column Guideline */
	public static final String EX_PIRTADEQUACYCOLUMNGUIDELINE_NAME_BLANK = "ex.pirt_adequacy_column_guideline.name.blank"; //$NON-NLS-1$

	/* PIRT Adequacy Column Level Guideline */
	public static final String EX_PIRTADEQUACYLEVELGUIDELINE_NAME_BLANK = "ex.pirt_adequacy_level_guideline.name.blank"; //$NON-NLS-1$
	public static final String EX_PIRTADEQUACYLEVELGUIDELINE_COLUMN_NULL = "ex.pirt_adequacy_level_guideline.column.blank"; //$NON-NLS-1$

	/* PIRT Description Header */
	public static final String EX_PIRTDESCHEADER_NAME_BLANK = "ex.pirt_desc_header.name.blank"; //$NON-NLS-1$
	public static final String EX_PIRTDESCHEADER_IDLABEL_BLANK = "ex.pirt_desc_header.id_label.blank"; //$NON-NLS-1$

	/* PIRT Level Difference Color */
	public static final String EX_PIRTLEVELDIFFCOLOR_MIN_NULL = "ex.pirt_level_diff_color.min.null"; //$NON-NLS-1$
	public static final String EX_PIRTLEVELDIFFCOLOR_MAX_NULL = "ex.pirt_level_diff_color.max.null"; //$NON-NLS-1$
	public static final String EX_PIRTLEVELDIFFCOLOR_COLOR_BLANK = "ex.pirt_level_diff_color.color.blank"; //$NON-NLS-1$

	/* PIRT Level Importance */
	public static final String EX_PIRTLEVELIMPORTANCE_NAME_BLANK = "ex.pirt_level_importance.name.blank"; //$NON-NLS-1$
	public static final String EX_PIRTLEVELIMPORTANCE_IDLABEL_BLANK = "ex.pirt_level_importance.id_label.blank"; //$NON-NLS-1$
	public static final String EX_PIRTLEVELIMPORTANCE_LEVEL_BLANK = "ex.pirt_level_importance.level.blank"; //$NON-NLS-1$
	public static final String EX_PIRTLEVELIMPORTANCE_LABEL_BLANK = "ex.pirt_level_importance.label.blank"; //$NON-NLS-1$

	/* Quantity of Interest */
	public static final String EX_QOI_MODEL_NULL = "ex.qoi.model.null"; //$NON-NLS-1$
	public static final String EX_QOI_SYMBOL_BLANK = "ex.qoi.symbol.blank"; //$NON-NLS-1$
	public static final String EX_QOI_CREATIONDATE_NULL = "ex.qoi.creation_date.null"; //$NON-NLS-1$
	public static final String EX_QOI_USERCREATION_NULL = "ex.qoi.creation_user.null"; //$NON-NLS-1$

	/* Quantity of Interest Header */
	public static final String EX_QOIHEADER_QOI_NULL = "ex.qoi_header.qoi.null"; //$NON-NLS-1$
	public static final String EX_QOIHEADER_NAME_BLANK = "ex.qoi_header.name.blank"; //$NON-NLS-1$
	public static final String EX_QOIHEADER_CREATIONDATE_NULL = "ex.qoi_header.creation_date.null"; //$NON-NLS-1$
	public static final String EX_QOIHEADER_USERCREATION_NULL = "ex.qoi_header.creation_user.null"; //$NON-NLS-1$

	/* QoIPlanning */
	public static final String EX_QOIPLANNINGVALUE_QOI_NULL = "ex.qoi_planning_value.qoi.null"; //$NON-NLS-1$
	public static final String EX_QOIPLANNINGVALUE_GENPARAMETER_NULL = "ex.qoi_planning_value.parameter.null"; //$NON-NLS-1$

	/* Requirement Parameter */
	public static final String EX_REQUIREMENTPARAMETER_REQUIREMENT_NULL = "ex.com_requirement_parameter.com_requirement.null"; //$NON-NLS-1$
	public static final String EX_REQUIREMENTPARAMETER_GENPARAMETER_NULL = "ex.com_requirement_parameter.parameter.blank"; //$NON-NLS-1$

	/* Role */
	public static final String EX_ROLE_NAME_BLANK = "ex.role.name.blank"; //$NON-NLS-1$

	/* System Requirement */
	public static final String EX_REQUIREMENT_STATEMENT_NULL = "ex.com_requirement.statement.null"; //$NON-NLS-1$
	public static final String EX_REQUIREMENT_CREATIONDATE_NULL = "ex.com_requirement.creation_date.null"; //$NON-NLS-1$
	public static final String EX_REQUIREMENT_USERCREATION_NULL = "ex.com_requirement.user_creation.null"; //$NON-NLS-1$
	public static final String EX_REQUIREMENT_MODEL_NULL = "ex.com_requirement.model.null"; //$NON-NLS-1$

	/* Tag */
	public static final String EX_TAG_NAME_BLANK = "ex.tag.name.blank"; //$NON-NLS-1$
	public static final String EX_TAG_DATETAG_NULL = "ex.tag.date_tag.null"; //$NON-NLS-1$
	public static final String EX_TAG_USERCREATION_NULL = "ex.tag.user_creation.null"; //$NON-NLS-1$

	/* Uncertainty */
	public static final String EX_UNCERTAINTY_USERCREATION_NULL = "ex.uncertainty.user_creation.null"; //$NON-NLS-1$
	public static final String EX_UNCERTAINTY_DATECREATION_NULL = "ex.uncertainty.date_creation.null"; //$NON-NLS-1$
	public static final String EX_UNCERTAINTY_MODEL_NULL = "ex.uncertainty.model.null"; //$NON-NLS-1$

	/* Uncertainty Parameter */
	public static final String EX_UNCERTAINTYPARAMETER_UNCERTAINTY_NULL = "ex.uncertainty_parameter.uncertainty.null"; //$NON-NLS-1$
	public static final String EX_UNCERTAINTYPARAMETER_GENPARAMETER_NULL = "ex.uncertainty_parameter.parameter.blank"; //$NON-NLS-1$

	/* User */
	public static final String EX_USER_USERID_BLANK = "ex.user.userID.blank"; //$NON-NLS-1$

	/**
	 * Classes
	 */
	/* Abstract CRUD Repository */
	public static final String EX_DAO_CRUD_ENTITYNOTFOUND = "ex.dao.crud.entity_not_found"; //$NON-NLS-1$
	public static final String EX_DAO_HSQLDB_FILEPATH_NULL = "ex.dao.hsqldb.file_path_null"; //$NON-NLS-1$
	public static final String EX_DAO_HSQLDB_NOT_CLOSED = "ex.dao.hsqldb.not_closed"; //$NON-NLS-1$

	/* App Manager */
	public static final String EX_APPMGR_DAOMGR_NULL = "ex.appmgr.daomgr_null"; //$NON-NLS-1$

	/* CFVariableResolver */
	public static final String EX_CFVARRESOLVER_VAR_NULL = "ex.cf_var_resolver.variable.null"; //$NON-NLS-1$
	public static final String EX_CFVARRESOLVER_VAR_NOTRECOGNIZED = "ex.cf_var_resolver.variable.not_recognized"; //$NON-NLS-1$

	/* CredibilityEditor */
	public static final String EX_CREDEDITOR_DBMIGRATION = "ex.crededitor.database_migration"; //$NON-NLS-1$
	public static final String EX_CREDEDITOR_LOAD_WEB_CONCURRENCYSUPPORT_NOT_ACTIVATED = "ex.crededitor.load.web.concurrency_support_not_activated"; //$NON-NLS-1$
	public static final String EX_CREDEDITOR_OPEN_TMPFOLDERCREATIONUNSUCCESSFUL = "ex.crededitor.open.tmp_folder_creation_unsuccessful"; //$NON-NLS-1$
	public static final String EX_CREDEDITOR_OPENING = "ex.crededitor.opening"; //$NON-NLS-1$
	public static final String EX_CREDEDITOR_PLUGIN_VERSION_EMPTY = "ex.crededitor.plugin_version_empty"; //$NON-NLS-1$
	public static final String EX_CREDEDITOR_TMPFOLDER_ACCESSDENIED = "ex.crededitor.tmp_folder.access_denied"; //$NON-NLS-1$
	public static final String EX_CREDEDITOR_SAVE_DELETEPREVCFUNSUCCESSFUL = "ex.crededitor.save.delete_prev_cf_unsuccessful"; //$NON-NLS-1$
	public static final String EX_CREDEDITOR_SAVE_TMPFOLDERNULL = "ex.crededitor.save.tmp_folder_null"; //$NON-NLS-1$
	public static final String EX_CREDEDITOR_VERSION_MISMATCH = "ex.crededitor.version_mismatch"; //$NON-NLS-1$
	public static final String EX_CREDEDITOR_CF_FILE_CORRUPTED = "ex.crededitor.cf_file.corrupted"; //$NON-NLS-1$

	/* Configuration Loader */
	public static final String EX_CONFLOADER_YAML_ENGINE_NULL = "ex.conf_loader.yaml_engine.null"; //$NON-NLS-1$
	public static final String EX_CONFLOADER_YAMLCONF_NOTEXISTS = "ex.conf_loader.yaml_conf_file.not_exists"; //$NON-NLS-1$
	public static final String EX_CONFLOADER_YAMLCONF_NOTYML = "ex.conf_loader.yaml_conf_file.not_yml"; //$NON-NLS-1$
	public static final String EX_CONFLOADER_YAMLFILEREADER_NULL = "ex.conf_loader.yaml_filereader.null"; //$NON-NLS-1$
	public static final String EX_CONFLOADER_YAMLCONF_DELETION_ERROR = "ex.conf_loader.yaml_conf_file.deletion_impossible"; //$NON-NLS-1$
	public static final String EX_CONFLOADER_YAMLCONF_EMPTYSPECS = "ex.conf_loader.yaml_conf_file.empty_specs"; //$NON-NLS-1$
	public static final String EX_CONFLOADER_CFFILE_NOTEXISTS = "ex.conf_loader.credibility_file_not_exists"; //$NON-NLS-1$

	/* DaoManager */
	public static final String EX_DAOMANAGER_LOADING = "ex.daomanager.loading"; //$NON-NLS-1$
	public static final String EX_DAOMANAGER_ALREADY_INIT = "ex.daomanager.already_init"; //$NON-NLS-1$

	/* DBMigrationManager */
	public static final String EX_DBDAOMANAGER_DAOMANAGER_NULL = "ex.dbmigrationmanager.dbmanager_null"; //$NON-NLS-1$

	/* ExtensionTool */
	public static final String EX_EXTENSIONTOOL_EXECUTE_ERROR = "ex.extensiontool.execute.error"; //$NON-NLS-1$

	/* Filetools */
	public static final String EX_FILETOOLS_EMPTYNULL = "ex.filetools.empty_or_null"; //$NON-NLS-1$
	public static final String EX_FILETOOLS_EVID_FOLDER_STRUCUTURE_NOTEXIST = "ex.filetools.evid_folder_structure.not_exist"; //$NON-NLS-1$
	public static final String EX_FILETOOLS_EVID_FOLDER_STRUCUTURE_NULL = "ex.filetools.evid_folder_structure.null"; //$NON-NLS-1$
	public static final String EX_FILETOOLS_ROOTPATH_NOTEXIST = "ex.filetools.root_path.not_exist"; //$NON-NLS-1$
	public static final String EX_FILETOOLS_MOVE_SOURCENULL = "ex.filetools.move.source_null"; //$NON-NLS-1$
	public static final String EX_FILETOOLS_MOVE_DELETEPREVRSCUNSUCCESSFUL = "ex.filetools.move.delete_prev_rsc_unsuccessful"; //$NON-NLS-1$
	public static final String EX_FILETOOLS_CREATEFILE_UNSUCCESSFUL = "ex.filetools.create_file.unsuccessful"; //$NON-NLS-1$

	/* ImageBadget */
	public static final String EX_IMAGE_BADGET_IMAGENULL = "ex.image_badget.image_null"; //$NON-NLS-1$

	/* ZipTools */
	public static final String EX_ZIPTOOLS_FILEOUTSIDE = "ex.ziptools.file_outside"; //$NON-NLS-1$
	public static final String EX_ZIPTOOLS_CREATERSC_UNSUCCESSFUL = "ex.ziptools.create_rsc.unsuccessful"; //$NON-NLS-1$

	/* Decision Application */
	public static final String EX_DECISION_ADD_DECISIONROW_NULL = "ex.decision.add.decision_row.null"; //$NON-NLS-1$
	public static final String EX_DECISION_ADD_DECISIONROW_TITLEDUPLICATED = "ex.decision.add.decision_row.title_duplicated"; //$NON-NLS-1$
	public static final String EX_DECISION_ADD_DECISIONROW_USERNULL = "ex.decision.add.decision_row.user_null"; //$NON-NLS-1$
	public static final String EX_DECISION_ADD_DECISIONROW_MODELNULL = "ex.decision.add.decision_row.model_null"; //$NON-NLS-1$

	public static final String EX_DECISION_UPDATE_DECISIONROW_NULL = "ex.decision.update.row.null"; //$NON-NLS-1$
	public static final String EX_DECISION_UPDATE_DECISIONROW_IDNULL = "ex.decision.update.row.id_null"; //$NON-NLS-1$
	public static final String EX_DECISION_UPDATE_DECISIONROW_USERNULL = "ex.decision.update.row.user_null"; //$NON-NLS-1$

	public static final String EX_DECISION_DELETE_DECISIONROW_NULL = "ex.decision.delete.row.null"; //$NON-NLS-1$
	public static final String EX_DECISION_DELETE_DECISIONROW_IDNULL = "ex.decision.delete.row.id_null"; //$NON-NLS-1$

	public static final String EX_DECISION_DELETE_DECISIONVALUE_NULL = "ex.decision.delete.value.null"; //$NON-NLS-1$
	public static final String EX_DECISION_DELETE_DECISIONVALUE_IDNULL = "ex.decision.delete.value.id_null"; //$NON-NLS-1$

	public static final String EX_DECISION_DELETE_DECISIONPARAM_NULL = "ex.decision.delete.param.null"; //$NON-NLS-1$
	public static final String EX_DECISION_DELETE_DECISIONPARAM_IDNULL = "ex.decision.delete.param.id_null"; //$NON-NLS-1$

	public static final String EX_DECISION_DELETE_SELECTVALUE_NULL = "ex.decision.delete.select_value.null"; //$NON-NLS-1$
	public static final String EX_DECISION_DELETE_SELECTVALUE_IDNULL = "ex.decision.delete.select_value.id_null"; //$NON-NLS-1$

	public static final String EX_DECISION_DELETE_CONSTRAINT_NULL = "ex.decision.delete.constraint.null"; //$NON-NLS-1$
	public static final String EX_DECISION_DELETE_CONSTRAINT_IDNULL = "ex.decision.delete.constraint.id_null"; //$NON-NLS-1$

	/* Global Application */
	public static final String EX_GLB_GENPARAMVALUELIST_IMPORT_PARAMETERNULL = "ex.glb.gen_param_value_list.import.parameter_null"; //$NON-NLS-1$
	public static final String EX_GLB_GENPARAMVALUELIST_ADD_NULL = "ex.glb.gen_param_value_list.add.null"; //$NON-NLS-1$
	public static final String EX_GLB_LOAD_MODEL_NULL = "ex.glb.load.model_null"; //$NON-NLS-1$
	public static final String EX_GLB_GENPARAMVALUELIST_DELETE_NULL = "ex.glb.gen_param_value_list.delete.null"; //$NON-NLS-1$
	public static final String EX_GLB_GENPARAMVALUELIST_DELETE_IDNULL = "ex.glb.gen_param_value_list.delete.id_null"; //$NON-NLS-1$

	public static final String EX_UPDATEMODEL_MODELNULL = "ex.glb.update_model.model_null"; //$NON-NLS-1$
	public static final String EX_UPDATEMODEL_IDNULL = "ex.glb.update_model.id_null"; //$NON-NLS-1$
	public static final String EX_UPDATEMODEL_NAMENULL = "ex.glb.update_model.name_null"; //$NON-NLS-1$
	public static final String EX_UPDATEMODEL_URINULL = "ex.glb.update_model.uri_null"; //$NON-NLS-1$

	public static final String EX_UPDATEGLBCONF_CONFNULL = "ex.glb.update_global_conf.conf_null"; //$NON-NLS-1$
	public static final String EX_UPDATEGLBCONF_IDNULL = "ex.glb.update_global_conf.id_null"; //$NON-NLS-1$

	/* Import Application */
	public static final String MSG_IMPORTAPP_IMPORTNAME_DECISIONCONSTRAINT = "msg.import_app.import_name.decision_constraint"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_DECISIONPARAM = "msg.import_app.import_name.decision_param"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_DECISIONSELECTVALUE = "msg.import_app.import_name.decision_select_value"; //$NON-NLS-1$

	public static final String MSG_IMPORTAPP_IMPORTNAME_PCMMELEMENT = "msg.import_app.import_name.pcmm_element"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_PCMMLEVEL = "msg.import_app.import_name.pcmm_level"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_PCMMLEVELCOLOR = "msg.import_app.import_name.pcmm_level_color"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_PCMMLEVELDESCRIPTOR = "msg.import_app.import_name.pcmm_level_descriptor"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_PCMMPHASE = "msg.import_app.import_name.pcmm_phase"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_PCMMPLANNINGPARAM = "msg.import_app.import_name.pcmm_planning_param"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_PCMMPLANNINGPARAMCONSTRAINT = "msg.import_app.import_name.pcmm_planning_param_constraint"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_PCMMPLANNINGQUESTION = "msg.import_app.import_name.pcmm_planning_question"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_PCMMPLANNINGQUESTIONCONSTRAINT = "msg.import_app.import_name.pcmm_planning_question_constraint"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_PCMMSUBELEMENT = "msg.import_app.import_name.pcmm_subelement"; //$NON-NLS-1$

	public static final String MSG_IMPORTAPP_IMPORTNAME_PIRTADEQUACYCOLUMN = "msg.import_app.import_name.pirt_adequacy_column"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_PIRTADEQUACYCOLUMNGUIDELINE = "msg.import_app.import_name.pirt_adequacy_column_guideline"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_PIRTADEQUACYCOLUMNLEVELGUIDELINE = "msg.import_app.import_name.pirt_adequacy_column_level_guideline"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_PIRTDESCRIPTIONHEADER = "msg.import_app.import_name.pirt_description_header"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_PIRTLEVELDIFFCOLOR = "msg.import_app.import_name.pirt_level_diff_color"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_PIRTLEVELIMPORTANCE = "msg.import_app.import_name.pirt_level_importance"; //$NON-NLS-1$

	public static final String MSG_IMPORTAPP_IMPORTNAME_QOIPLANNINGCONSTRAINT = "msg.import_app.import_name.qoi_planning_constraint"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_QOIPLANNINGPARAM = "msg.import_app.import_name.qoi_planning_param"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_QOIPLANNINGSELECTVALUE = "msg.import_app.import_name.qoi_planning_select_value"; //$NON-NLS-1$

	public static final String MSG_IMPORTAPP_IMPORTNAME_ROLE = "msg.import_app.import_name.role"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_USER = "msg.import_app.import_name.user"; //$NON-NLS-1$

	public static final String MSG_IMPORTAPP_IMPORTNAME_REQUIREMENTCONSTRAINT = "msg.import_app.import_name.requirement_constraint"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_REQUIREMENTPARAM = "msg.import_app.import_name.requirement_param"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_REQUIREMENTSELECTVALUE = "msg.import_app.import_name.requirement_select_value"; //$NON-NLS-1$

	public static final String MSG_IMPORTAPP_IMPORTNAME_UNCERTAINTYCONSTRAINT = "msg.import_app.import_name.uncertainty_constraint"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_UNCERTAINTYPARAM = "msg.import_app.import_name.uncertainty_param"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_UNCERTAINTYSELECTVALUE = "msg.import_app.import_name.uncertainty_select_value"; //$NON-NLS-1$

	public static final String MSG_IMPORTAPP_IMPORTNAME_UNCERTAINTY = "msg.import_app.import_name.uncertainty"; //$NON-NLS-1$
	public static final String MSG_IMPORTAPP_IMPORTNAME_UNCERTAINTYVALUE = "msg.import_app.import_name.uncertainty_value"; //$NON-NLS-1$

	public static final String EX_IMPORTAPP_CONF_SCHEMAFILE_NOTEXISTS = "ex.import_app.conf.schema_file_not_exists"; //$NON-NLS-1$
	public static final String EX_IMPORTAPP_CONF_PCMMSPECS_NULL = "ex.import_app.conf.pcmm_specs_null"; //$NON-NLS-1$
	public static final String EX_IMPORTAPP_MODELNULL = "ex.import_app.model_null"; //$NON-NLS-1$

	/* Import Uncertainty Application */
	public static final String ERR_IMPORT_UNCERTAINTY_APP_USER_NULL = "err.import_uncertainty_app.user.null"; //$NON-NLS-1$

	/* Message Route */
	public static final String EX_MESSAGEROUTE_URI_MEMBERID_NULL = "ex.message_route.uri.memberid_null"; //$NON-NLS-1$
	public static final String EX_MESSAGEROUTE_URI_MODEL_NULL = "ex.message_route.uri.model_null"; //$NON-NLS-1$

	/* Model Route */
	public static final String EX_MODELROUTE_URI_MODEL_NULL = "ex.model_route.uri.model_null"; //$NON-NLS-1$

	/* Model Web Client */
	public static final String EX_MODELWEBCLIENT_LOAD_MODEL_NULL = "ex.model_webclient.load.model_null"; //$NON-NLS-1$
	public static final String EX_MODELWEBCLIENT_CREATE_MODEL_NULL = "ex.model_webclient.create.model_null"; //$NON-NLS-1$
	public static final String EX_MODELWEBCLIENT_DELETE_MODEL_NULL = "ex.model_webclient.delete.model_null"; //$NON-NLS-1$

	/* Migration Dao */
	public static final String EX_MIGRATIONDAO_TASK_NULL = "ex.migration_dao.task_null"; //$NON-NLS-1$
	public static final String EX_MIGRATIONDAO_TASKNAME_BLANK = "ex.migration_dao.taskname_blank"; //$NON-NLS-1$
	public static final String EX_MIGRATIONDAO_DAOMGR_NULL = "ex.migration_dao.daomgr_null"; //$NON-NLS-1$
	public static final String EX_MIGRATIONDAO_TASK8_UNKNOWNUSER_NOTCREATED = "ex.migration_dao.task8.unknown_user_not_created"; //$NON-NLS-1$

	/* PIRT Application */
	public static final String EX_PIRT_GETBGCOLOR_CONFNULL = "ex.pirt.get_bg_color.configuration_null"; //$NON-NLS-1$
	public static final String EX_PIRT_TAG_QOINULL = "ex.pirt.tag.qoi_null"; //$NON-NLS-1$
	public static final String EX_PIRT_TAG_MODELNULL = "ex.pirt.tag.model_null"; //$NON-NLS-1$
	public static final String EX_PIRT_GETQOIBYID_IDNULL = "ex.pirt.get_qoi_by_id.id_null"; //$NON-NLS-1$
	public static final String EX_PIRT_ADDQOI_QOINULL = "ex.pirt.add_qoi.qoi_null"; //$NON-NLS-1$
	public static final String EX_PIRT_ADDQOI_USERNULL = "ex.pirt.add_qoi.creation_user_null"; //$NON-NLS-1$
	public static final String EX_PIRT_ADDQOI_NAMEDUPLICATED = "ex.pirt.add_qoi.name_duplicated"; //$NON-NLS-1$
	public static final String EX_PIRT_DUPLICATEQOI_QOINULL = "ex.pirt.copy_qoi.qoi_null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEQOI_QOINULL = "ex.pirt.update_qoi.qoi_null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEQOI_IDNULL = "ex.pirt.update_qoi.id_null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEQOI_QOINOTFOUND = "ex.pirt.update_qoi.not_found"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEQOI_NAMEDUPLICATED = "ex.pirt.update_qoi.name_duplicated"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEQOI_QOINULL = "ex.pirt.delete_qoi.qoi_null"; //$NON-NLS-1$

	public static final String EX_PIRT_ADDQOIHEADER_QOIHEADERNULL = "ex.pirt.add_qoi_header.qoi_header_null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEQOIHEADER_QOIHEADERNULL = "ex.pirt.update_qoi_header.qoi_header_null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEQOIHEADER_IDNULL = "ex.pirt.update_qoi_header.id_null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEQOIHEADER_QOIHEADERNULL = "ex.pirt.delete_qoi_header.qoi_header_null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEQOIHEADER_IDNULL = "ex.pirt.delete_qoi_header.id_null"; //$NON-NLS-1$

	public static final String EX_PIRT_ADDPHENGROUP_GROUPNULL = "ex.pirt.add_phen_group.group_null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPHENGROUP_GROUPNULL = "ex.pirt.update_phen_group.group_null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPHENGROUP_IDNULL = "ex.pirt.update_phen_group.id_null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPHENGROUP_GROUPNULL = "ex.pirt.delete_phen_group.group_null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPHENGROUP_IDNULL = "ex.pirt.delete_phen_group.id_null"; //$NON-NLS-1$

	public static final String EX_PIRT_ADDPHENOMENON_PHENOMENONNULL = "ex.pirt.add_phenomenon.phenomenon_null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPHENOMENON_PHENOMENONNULL = "ex.pirt.update_phenomenon.phenomenon_null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPHENOMENON_IDNULL = "ex.pirt.update_phenomenon.id_null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPHENOMENON_PHENOMENONNULL = "ex.pirt.delete_phenomenon.phenomenon_null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPHENOMENON_IDNULL = "ex.pirt.delete_phenomenon.id_null"; //$NON-NLS-1$

	public static final String EX_PIRT_ADDCRITERION_CRITERIONNULL = "ex.pirt.add_criterion.criterion_null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATECRITERION_CRITERIONNULL = "ex.pirt.update_criterion.criterion_null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATECRITERION_IDNULL = "ex.pirt.update_criterion.id_null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETECRITERION_CRITERIONNULL = "ex.pirt.delete_criterion.criterion_null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETECRITERION_IDNULL = "ex.pirt.delete_criterion.id_null"; //$NON-NLS-1$

	public static final String EX_PIRT_ADDPIRTADEQCOLUMNGUIDELINE_COLUMNNULL = "ex.pirt.add_adeq_column_guideline.null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPIRTADEQCOLUMNGUIDELINE_COLUMNNULL = "ex.pirt.update_adeq_column_guideline.null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPIRTADEQCOLUMNGUIDELINE_IDNULL = "ex.pirt.update_adeq_column_guideline.id_null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPIRTADEQCOLUMNGUIDELINE_COLUMNNULL = "ex.pirt.delete_adeq_column_guideline.null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPIRTADEQCOLUMNGUIDELINE_IDNULL = "ex.pirt.delete_adeq_column_guideline.id_null"; //$NON-NLS-1$

	public static final String EX_PIRT_ADDPIRTADEQCOLUMNLEVELGUIDELINE_COLUMNNULL = "ex.pirt.add_adeq_column_level_guideline.null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPIRTADEQCOLUMNLEVELGUIDELINE_COLUMNNULL = "ex.pirt.update_adeq_column_level_guideline.null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPIRTADEQCOLUMNLEVELGUIDELINE_IDNULL = "ex.pirt.update_adeq_column_level_guideline.id_null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPIRTADEQCOLUMNLEVELGUIDELINE_COLUMNNULL = "ex.pirt.delete_adeq_column_level_guideline.null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPIRTADEQCOLUMNLEVELGUIDELINE_IDNULL = "ex.pirt.delete_adeq_column_level_guideline.id_null"; //$NON-NLS-1$

	public static final String EX_PIRT_ADDPIRTADEQCOLUMN_COLUMNNULL = "ex.pirt.add_adeq_column.null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPIRTADEQCOLUMN_COLUMNNULL = "ex.pirt.update_adeq_column.null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPIRTADEQCOLUMN_IDNULL = "ex.pirt.update_adeq_column.id_null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPIRTADEQCOLUMN_COLUMNNULL = "ex.pirt.delete_adeq_column.null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPIRTADEQCOLUMN_IDNULL = "ex.pirt.delete_adeq_column.id_null"; //$NON-NLS-1$

	public static final String EX_PIRT_ADDPIRTDESCHEADER_COLUMNNULL = "ex.pirt.add_desc_header.null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPIRTDESCHEADER_COLUMNNULL = "ex.pirt.update_desc_header.null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPIRTDESCHEADER_IDNULL = "ex.pirt.update_desc_header.id_null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPIRTDESCHEADER_COLUMNNULL = "ex.pirt.delete_desc_header.null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPIRTDESCHEADER_IDNULL = "ex.pirt.delete_desc_header.id_null"; //$NON-NLS-1$

	public static final String EX_PIRT_ADDPIRTLEVELDIFFCOLOR_COLUMNNULL = "ex.pirt.add_level_diff_color.null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPIRTLEVELDIFFCOLOR_COLUMNNULL = "ex.pirt.update_level_diff_color.null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPIRTLEVELDIFFCOLOR_IDNULL = "ex.pirt.update_level_diff_color.id_null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPIRTLEVELDIFFCOLOR_COLUMNNULL = "ex.pirt.delete_level_diff_color.null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPIRTLEVELDIFFCOLOR_IDNULL = "ex.pirt.delete_level_diff_color.id_null"; //$NON-NLS-1$

	public static final String EX_PIRT_ADDPIRTLEVELIMPORTANCE_COLUMNNULL = "ex.pirt.add_level_importance.null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPIRTLEVELIMPORTANCE_COLUMNNULL = "ex.pirt.update_level_importance.null"; //$NON-NLS-1$
	public static final String EX_PIRT_UPDATEPIRTLEVELIMPORTANCE_IDNULL = "ex.pirt.update_level_importance.id_null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPIRTLEVELIMPORTANCE_COLUMNNULL = "ex.pirt.delete_level_importance.null"; //$NON-NLS-1$
	public static final String EX_PIRT_DELETEPIRTLEVELIMPORTANCE_IDNULL = "ex.pirt.delete_level_importance.id_null"; //$NON-NLS-1$

	public static final String EX_PIRT_LEVELDIFFERENCECOLOR_CONFNULL = "ex.pirt.level_difference_color.conf_null"; //$NON-NLS-1$
	public static final String EX_PIRT_LEVELDIFFERENCECOLOR_IMPORTANCENULL = "ex.pirt.level_difference_color.importance_null"; //$NON-NLS-1$

	/* QoIPlanning Application */

	public static final String EX_QOIPLANNING_DELETE_QOIPLANNINGVALUE_NULL = "ex.qoiplanning.delete.qoiplanning_value.null"; //$NON-NLS-1$
	public static final String EX_QOIPLANNING_DELETE_QOIPLANNINGVALUE_IDNULL = "ex.qoiplanning.delete.qoiplanning_value.id_null"; //$NON-NLS-1$

	public static final String EX_QOIPLANNING_DELETE_QOIPLANNINGPARAM_NULL = "ex.qoiplanning.delete.qoiplanning_param.null"; //$NON-NLS-1$
	public static final String EX_QOIPLANNING_DELETE_QOIPLANNINGPARAM_IDNULL = "ex.qoiplanning.delete.qoiplanning_param.id_null"; //$NON-NLS-1$

	public static final String EX_QOIPLANNING_DELETE_QOIPLANNINGSELECTVALUE_NULL = "ex.qoiplanning.delete.qoiplanning_select_value.null"; //$NON-NLS-1$
	public static final String EX_QOIPLANNING_DELETE_QOIPLANNINGSELECTVALUE_IDNULL = "ex.qoiplanning.delete.qoiplanning_select_value.id_null"; //$NON-NLS-1$

	public static final String EX_QOIPLANNING_DELETE_QOIPLANNINGCONSTRAINT_NULL = "ex.qoiplanning.delete.qoiplanning_constraint.null"; //$NON-NLS-1$
	public static final String EX_QOIPLANNING_DELETE_QOIPLANNINGCONSTRAINT_IDNULL = "ex.qoiplanning.delete.qoiplanning_constraint.id_null"; //$NON-NLS-1$

	/* PCMM Application */

	public static final String EX_PCMM_ADDPCMMOPTION_NULL = "ex.pcmm.add_pcmm_option.null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEPCMMOPTION_NULL = "ex.pcmm.update_pcmm_option.null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEPCMMOPTION_IDNULL = "ex.pcmm.update_pcmm_option.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETEPCMMOPTION_NULL = "ex.pcmm.delete_pcmm_option.null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETEPCMMOPTION_IDNULL = "ex.pcmm.delete_pcmm_option.id_null"; //$NON-NLS-1$

	public static final String EX_PCMM_AGGREGATESUBELT_ELTLISTNULL = "ex.pcmm.aggregate_subelt.elt_list_null"; //$NON-NLS-1$
	public static final String EX_PCMM_AGGREGATESUBELT_CONFLEVELCOLORLISTNULL = "ex.pcmm.aggregate_subelt.conf_level_color_list_null"; //$NON-NLS-1$
	public static final String EX_PCMM_AGGREGATESUBELT_AGGREGMAPNULL = "ex.pcmm.aggregate_subelt.aggreg_map_null"; //$NON-NLS-1$
	public static final String EX_PCMM_AGGREGATESUBELT_ELTNULL = "ex.pcmm.aggregate_subelt.elt_null"; //$NON-NLS-1$
	public static final String EX_PCMM_AGGREGATESUBELT_ITEMNULL = "ex.pcmm.aggregate_subelt.item_null"; //$NON-NLS-1$

	public static final String EX_PCMM_GETASSESSTBYID_IDNULL = "ex.pcmm.get_assesst_by_id.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_ADDASSESSTBYID_ASSESSTNULL = "ex.pcmm.add_assesst_by_id.assesst_null"; //$NON-NLS-1$
	public static final String EX_PCMM_ADDASSESSTBYID_ELEMENTNULL = "ex.pcmm.add_assesst_by_id.element_null"; //$NON-NLS-1$
	public static final String EX_PCMM_ADDASSESSTBYID_SUBELEMENTNULL = "ex.pcmm.add_assesst_by_id.subelement_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEASSESSTBYID_ASSESSTNULL = "ex.pcmm.update_assesst_by_id.assesst_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEASSESSTBYID_IDNULL = "ex.pcmm.update_assesst_by_id.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEASSESSTBYID_FORBIDDEN = "ex.pcmm.update_assesst_by_id.forbidden"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEASSESSTBYID_ELEMENTNULL = "ex.pcmm.udapte_assesst_by_id.element_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEASSESSTBYID_SUBELEMENTNULL = "ex.pcmm.update_assesst_by_id.subelement_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEASSESSTBYID_DIFFUSERNULL = "ex.pcmm.update_assesst_by_id.different_user_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEASSESSTBYID_DIFFROLENULL = "ex.pcmm.update_assesst_by_id.different_role_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETEASSESSTBYID_ASSESSTNULL = "ex.pcmm.delete_assesst_by_id.assesst_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETEASSESSTBYID_IDNULL = "ex.pcmm.delete_assesst_by_id.id_null"; //$NON-NLS-1$

	public static final String EX_PCMM_GETELTLIST_MODELNULL = "ex.pcmm.get_elt_list.model_null"; //$NON-NLS-1$
	public static final String EX_PCMM_GETELTBYID_IDNULL = "ex.pcmm.get_elt_by_id.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_ADDELT_ELTNULL = "ex.pcmm.add_elt.elt_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEELT_ELTNULL = "ex.pcmm.update_elt.elt_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEELT_IDNULL = "ex.pcmm.update_elt.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETEELT_ELTNULL = "ex.pcmm.delete_elt.elt_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETEELT_IDNULL = "ex.pcmm.delete_elt.id_null"; //$NON-NLS-1$

	public static final String EX_PCMM_GETEVIDENCEBYID_IDNULL = "ex.pcmm.get_evidence_by_id.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_GETEVIDENCEBYELT_ELTNULL = "ex.pcmm.get_evidence_by_elt.elt_null"; //$NON-NLS-1$
	public static final String EX_PCMM_ADDEVIDENCE_EVIDENCENULL = "ex.pcmm.add_evidence.evidence_null"; //$NON-NLS-1$
	public static final String EX_PCMM_EVIDENCE_INVALIDPATH = "ex.pcmm.evidence.invalid_path"; //$NON-NLS-1$
	public static final String EX_PCMM_EVIDENCE_INVALIDURL = "ex.pcmm.evidence.invalid_url"; //$NON-NLS-1$
	public static final String EX_PCMM_EVIDENCE_NOASSESSABLE = "ex.pcmm.evidence.no_assessable"; //$NON-NLS-1$
	public static final String EX_PCMM_EVIDENCE_NOTFOUND = "ex.pcmm.evidence.not_found"; //$NON-NLS-1$
	public static final String EX_PCMM_EVIDENCE_MORETHANONEASSESSABLE = "ex.pcmm.evidence.more_than_one_assessable"; //$NON-NLS-1$
	public static final String EX_PCMM_ADDEVIDENCE_ALREADYEXISTS = "ex.pcmm.add_evidence.already_exists"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEEVIDENCE_ELTNULL = "ex.pcmm.update_evidence.evidence_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEEVIDENCE_IDNULL = "ex.pcmm.update_evidence.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEEVIDENCE_NOTFOUND = "ex.pcmm.update_evidence.not_found"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEEVIDENCE_ALREADYEXISTS = "ex.pcmm.update_evidence.already_exists"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETEEVIDENCE_ELTNULL = "ex.pcmm.delete_evidence.evidence_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETEEVIDENCE_IDNULL = "ex.pcmm.delete_evidence.id_null"; //$NON-NLS-1$

	public static final String EX_PCMM_GETLEVELBYID_IDNULL = "ex.pcmm.get_level_by_id.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_ADDLEVEL_LEVELNULL = "ex.pcmm.add_level.level_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATELEVEL_LEVELNULL = "ex.pcmm.update_level.level_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATELEVEL_IDNULL = "ex.pcmm.update_level.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETELEVEL_LEVELNULL = "ex.pcmm.delete_level.level_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETELEVEL_IDNULL = "ex.pcmm.delete_level.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_GETLEVELDESCBYID_IDNULL = "ex.pcmm.get_leveldesc_by_id.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_ADDLEVELDESC_LEVELDESCNULL = "ex.pcmm.add_leveldesc.leveldesc_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATELEVELDESC_LEVELDESCNULL = "ex.pcmm.update_leveldesc.leveldesc_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATELEVELDESC_IDNULL = "ex.pcmm.update_leveldesc.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETELEVELDESC_LEVELDESCNULL = "ex.pcmm.delete_leveldesc.leveldesc_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETELEVELDESC_IDNULL = "ex.pcmm.delete_leveldesc.id_null"; //$NON-NLS-1$

	public static final String EX_PCMM_GETSUBELTBYID_IDNULL = "ex.pcmm.get_subelt_by_id.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_ADDSUBELT_SUBELTNULL = "ex.pcmm.add_subelt.subelt_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATESUBELT_SUBELTNULL = "ex.pcmm.update_subelt.subelt_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATESUBELT_IDNULL = "ex.pcmm.update_subelt.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETESUBELT_SUBELTNULL = "ex.pcmm.delete_subelt.subelt_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETESUBELT_IDNULL = "ex.pcmm.delete_subelt.id_null"; //$NON-NLS-1$

	public static final String EX_PCMM_GETROLEBYID_IDNULL = "ex.pcmm.get_role_by_id.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_ADDROLE_ROLENULL = "ex.pcmm.add_role.role_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEROLE_ROLENULL = "ex.pcmm.update_role.role_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATEROLE_IDNULL = "ex.pcmm.update_role.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETEROLE_ROLENULL = "ex.pcmm.delete_role.role_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETEROLE_IDNULL = "ex.pcmm.delete_role.id_null"; //$NON-NLS-1$

	public static final String EX_PCMM_ADDLEVELCOLOR_NULL = "ex.pcmm.add_level_color.null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATELEVELCOLOR_NULL = "ex.pcmm.update_level_color.null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATELEVELCOLOR_IDNULL = "ex.pcmm.update_level_color.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETELEVELCOLOR_NULL = "ex.pcmm.delete_level_color.null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETELEVELCOLOR_IDNULL = "ex.pcmm.delete_level_color.id_null"; //$NON-NLS-1$

	public static final String EX_PCMM_TAGCURRENT_TAGNULL = "ex.pcmm.tag_current.tag_null"; //$NON-NLS-1$
	public static final String EX_PCMM_TAGCURRENT_USERNULL = "ex.pcmm.tag_current.user_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATETAG_TAGNULL = "ex.pcmm.update_tag.tag_null"; //$NON-NLS-1$
	public static final String EX_PCMM_UPDATETAG_IDNULL = "ex.pcmm.update_tag.id_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETETAG_TAGNULL = "ex.pcmm.delete_tag.tag_null"; //$NON-NLS-1$
	public static final String EX_PCMM_DELETETAG_IDNULL = "ex.pcmm.delete_tag.id_null"; //$NON-NLS-1$

	public static final String EX_PCMM_PROGRESS_COMPUTE_ELTNULL = "ex.pcmm.progress.compute.elt_null"; //$NON-NLS-1$
	public static final String EX_PCMM_PROGRESS_COMPUTE_CONFNULL = "ex.pcmm.progress.compute.conf_null"; //$NON-NLS-1$

	/* PCMM Planning Application */
	public static final String EX_PCMMPLANNING_IMPORTCONF_MODELNULL = "ex.pcmm_planning.import_conf.model_null"; //$NON-NLS-1$

	public static final String EX_PCMMPLANNING_ADDPARAM_BADINSTANCE = "ex.pcmm_planning.add_param.bad_instance"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_ADDPARAM_NULL = "ex.pcmm_planning.add_param.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_UPDATEPARAM_NULL = "ex.pcmm_planning.update_param.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_UPDATEPARAM_IDNULL = "ex.pcmm_planning.update_param.id_null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_DELETEPARAM_NULL = "ex.pcmm_planning.delete_param.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_DELETEPARAM_IDNULL = "ex.pcmm_planning.delete_param.id_null"; //$NON-NLS-1$

	public static final String EX_PCMMPLANNING_ADDQUESTION_NULL = "ex.pcmm_planning.add_question.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_DELETEQUESTION_NULL = "ex.pcmm_planning.delete_question.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_DELETEQUESTION_IDNULL = "ex.pcmm_planning.delete_question.id_null"; //$NON-NLS-1$

	public static final String EX_PCMMPLANNING_ADDVALUE_NULL = "ex.pcmm_planning.add_value.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_UPDATEVALUE_NULL = "ex.pcmm_planning.update_value.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_UPDATEVALUE_IDNULL = "ex.pcmm_planning.update_value.id_null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_DELETEVALUE_NULL = "ex.pcmm_planning.delete_value.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_DELETEVALUE_IDNULL = "ex.pcmm_planning.delete_value.id_null"; //$NON-NLS-1$

	public static final String EX_PCMMPLANNING_ADDQUESTIONVALUE_NULL = "ex.pcmm_planning.add_question_value.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_UPDATEQUESTIONVALUE_NULL = "ex.pcmm_planning.update_question_value.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_UPDATEQUESTIONVALUE_IDNULL = "ex.pcmm_planning.update_question_value.id_null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_DELETEQUESTIONVALUE_NULL = "ex.pcmm_planning.delete_question_value.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_DELETEQUESTIONVALUE_IDNULL = "ex.pcmm_planning.delete_question_value.id_null"; //$NON-NLS-1$

	public static final String EX_PCMMPLANNING_ADDTABLEITEM_NULL = "ex.pcmm_planning.add_table_item.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_REFRESHTABLEITEM_NULL = "ex.pcmm_planning.refresh_table_item.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_DELETETABLEITEM_NULL = "ex.pcmm_planning.delete_table_item.null"; //$NON-NLS-1$

	public static final String EX_PCMMPLANNING_ADDTABLEVALUE_NULL = "ex.pcmm_planning.add_table_value.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_UPDATETABLEVALUE_NULL = "ex.pcmm_planning.update_table_value.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_UPDATETABLEVALUE_IDNULL = "ex.pcmm_planning.update_table_value.id_null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_DELETETABLEVALUE_NULL = "ex.pcmm_planning.delete_table_value.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_DELETETABLEVALUE_IDNULL = "ex.pcmm_planning.delete_table_value.id_null"; //$NON-NLS-1$

	public static final String EX_PCMMPLANNING_TAG_NULL = "ex.pcmm_planning.tag.null"; //$NON-NLS-1$
	public static final String EX_PCMMPLANNING_TAG_IDNULL = "ex.pcmm_planning.tag.id_null"; //$NON-NLS-1$

	/* Intended Purpose Application */
	public static final String EX_INTENDEDPURPOSE_UPDATE_INTENDEDPURPOSE_NULL = "ex.intended_purpose.update.intended_purpose.null"; //$NON-NLS-1$
	public static final String EX_INTENDEDPURPOSE_UPDATE_USER_NULL = "ex.intended_purpose.update.user.null"; //$NON-NLS-1$
	public static final String EX_INTENDEDPURPOSE_UPDATE_MODEL_NULL = "ex.intended_purpose.update.model.null"; //$NON-NLS-1$

	/* Report ARG Execution App */

	public static final String MSG_REPORTARGEXEC_GETARGTYPES_JOB_INIT = "msg.report_arg_exec.get_arg_types.job.init"; //$NON-NLS-1$
	public static final String MSG_REPORTARGEXEC_GETARGTYPES_JOB_EXECWITHPRESCRIPT = "msg.report_arg_exec.get_arg_types.job.exec_with_prescript"; //$NON-NLS-1$
	public static final String MSG_REPORTARGEXEC_GETARGTYPES_JOB_EXEC = "msg.report_arg_exec.get_arg_types.job.exec"; //$NON-NLS-1$
	public static final String MSG_REPORTARGEXEC_GETARGTYPES_JOB_RETRIEVERESULT = "msg.report_arg_exec.get_arg_types.job.retrieve_result"; //$NON-NLS-1$

	public static final String MSG_REPORTARGEXEC_GETARGVERSION_JOB_INIT = "msg.report_arg_exec.get_arg_version.job.init"; //$NON-NLS-1$
	public static final String MSG_REPORTARGEXEC_GETARGVERSION_JOB_EXECWITHPRESCRIPT = "msg.report_arg_exec.get_arg_version.job.exec_with_prescript"; //$NON-NLS-1$
	public static final String MSG_REPORTARGEXEC_GETARGVERSION_JOB_EXEC = "msg.report_arg_exec.get_arg_version.job.exec"; //$NON-NLS-1$
	public static final String MSG_REPORTARGEXEC_GETARGVERSION_JOB_RETRIEVERESULT = "msg.report_arg_exec.get_arg_version.job.retrieve_result"; //$NON-NLS-1$

	/* System Requirements Application */

	public static final String EX_SYSREQUIREMENT_ADD_REQUIREMENTGROUP_NULL = "ex.sysrequirement.add.requirement_group.null"; //$NON-NLS-1$

	public static final String EX_SYSREQUIREMENT_ADD_REQUIREMENTROW_NULL = "ex.sysrequirement.add.requirement_row.null"; //$NON-NLS-1$
	public static final String EX_SYSREQUIREMENT_ADD_REQUIREMENTROW_STATEMENTDUPLICATED = "ex.sysrequirement.add.requirement_row.statement_duplicated"; //$NON-NLS-1$
	public static final String EX_SYSREQUIREMENT_ADD_REQUIREMENTROW_USERNULL = "ex.sysrequirement.add.requirement_row.user_null"; //$NON-NLS-1$
	public static final String EX_SYSREQUIREMENT_ADD_REQUIREMENTROW_MODELNULL = "ex.sysrequirement.add.requirement_row.model_null"; //$NON-NLS-1$

	public static final String EX_SYSREQUIREMENT_UPDATE_REQUIREMENTGROUP_NULL = "ex.sysrequirement.update.requirement_group.null"; //$NON-NLS-1$
	public static final String EX_SYSREQUIREMENT_UPDATE_REQUIREMENTGROUP_IDNULL = "ex.sysrequirement.update.requirement_group.id_null"; //$NON-NLS-1$

	public static final String EX_SYSREQUIREMENT_UPDATE_REQUIREMENTROW_NULL = "ex.sysrequirement.update.requirement_row.null"; //$NON-NLS-1$
	public static final String EX_SYSREQUIREMENT_UPDATE_REQUIREMENTROW_IDNULL = "ex.sysrequirement.update.requirement_row.id_null"; //$NON-NLS-1$
	public static final String EX_SYSREQUIREMENT_UPDATE_REQUIREMENTROW_USERNULL = "ex.sysrequirement.update.requirement_row.user_null"; //$NON-NLS-1$

	public static final String EX_SYSREQUIREMENT_DELETE_REQUIREMENTGROUP_NULL = "ex.sysrequirement.delete.requirement_group.null"; //$NON-NLS-1$
	public static final String EX_SYSREQUIREMENT_DELETE_REQUIREMENTGROUP_IDNULL = "ex.sysrequirement.delete.requirement_group.id_null"; //$NON-NLS-1$

	public static final String EX_SYSREQUIREMENT_DELETE_REQUIREMENTROW_NULL = "ex.sysrequirement.delete.requirement_row.null"; //$NON-NLS-1$
	public static final String EX_SYSREQUIREMENT_DELETE_REQUIREMENTROW_IDNULL = "ex.sysrequirement.delete.requirement_row.id_null"; //$NON-NLS-1$

	public static final String EX_SYSREQUIREMENT_DELETE_REQUIREMENTVALUE_NULL = "ex.sysrequirement.delete.requirement_value.null"; //$NON-NLS-1$
	public static final String EX_SYSREQUIREMENT_DELETE_REQUIREMENTVALUE_IDNULL = "ex.sysrequirement.delete.requirement_value.id_null"; //$NON-NLS-1$

	public static final String EX_SYSREQUIREMENT_DELETE_REQUIREMENTPARAM_NULL = "ex.sysrequirement.delete.requirement_param.null"; //$NON-NLS-1$
	public static final String EX_SYSREQUIREMENT_DELETE_REQUIREMENTPARAM_IDNULL = "ex.sysrequirement.delete.requirement_param.id_null"; //$NON-NLS-1$

	public static final String EX_SYSREQUIREMENT_DELETE_REQUIREMENTSELECTVALUE_NULL = "ex.sysrequirement.delete.requirement_select_value.null"; //$NON-NLS-1$
	public static final String EX_SYSREQUIREMENT_DELETE_REQUIREMENTSELECTVALUE_IDNULL = "ex.sysrequirement.delete.requirement_select_value.id_null"; //$NON-NLS-1$

	/* Uncertainty Application */

	public static final String EX_UNCERTAINTY_ADD_UNCERTAINTY_NULL = "ex.uncertainty.add.uncertainty_row.null"; //$NON-NLS-1$
	public static final String EX_UNCERTAINTY_ADD_UNCERTAINTYROW_USERNULL = "ex.uncertainty.add.uncertainty_row.user_null"; //$NON-NLS-1$
	public static final String EX_UNCERTAINTY_ADD_UNCERTAINTY_MODELNULL = "ex.uncertainty.add.uncertainty.model_null"; //$NON-NLS-1$

	public static final String EX_UNCERTAINTY_UPDATE_UNCERTAINTYROW_NULL = "ex.uncertainty.update.uncertainty_row.null"; //$NON-NLS-1$
	public static final String EX_UNCERTAINTY_UPDATE_UNCERTAINTYROW_IDNULL = "ex.uncertainty.update.uncertainty_row.id_null"; //$NON-NLS-1$
	public static final String EX_UNCERTAINTY_UPDATE_UNCERTAINTYROW_USERNULL = "ex.uncertainty.update.uncertainty_row.user_null"; //$NON-NLS-1$

	public static final String EX_UNCERTAINTY_DELETE_UNCERTAINTYROW_NULL = "ex.uncertainty.delete.uncertainty_row.null"; //$NON-NLS-1$
	public static final String EX_UNCERTAINTY_DELETE_UNCERTAINTYROW_IDNULL = "ex.uncertainty.delete.uncertainty_row.id_null"; //$NON-NLS-1$

	public static final String EX_UNCERTAINTY_DELETE_UNCERTAINTYVALUE_NULL = "ex.uncertainty.delete.uncertainty_value.null"; //$NON-NLS-1$
	public static final String EX_UNCERTAINTY_DELETE_UNCERTAINTYVALUE_IDNULL = "ex.uncertainty.delete.uncertainty_value.id_null"; //$NON-NLS-1$

	public static final String EX_UNCERTAINTY_DELETE_UNCERTAINTYPARAM_NULL = "ex.uncertainty.delete.uncertainty_param.null"; //$NON-NLS-1$
	public static final String EX_UNCERTAINTY_DELETE_UNCERTAINTYPARAM_IDNULL = "ex.uncertainty.delete.uncertainty_param.id_null"; //$NON-NLS-1$

	public static final String EX_UNCERTAINTY_DELETE_UNCERTAINTYSELECTVALUE_NULL = "ex.uncertainty.delete.uncertainty_select_value.null"; //$NON-NLS-1$
	public static final String EX_UNCERTAINTY_DELETE_UNCERTAINTYSELECTVALUE_IDNULL = "ex.uncertainty.delete.uncertainty_select_value.id_null"; //$NON-NLS-1$

	public static final String EX_UNCERTAINTY_DELETE_UNCERTAINTYCONSTRAINT_NULL = "ex.uncertainty.delete.uncertainty_constraint.null"; //$NON-NLS-1$
	public static final String EX_UNCERTAINTY_DELETE_UNCERTAINTYCONSTRAINT_IDNULL = "ex.uncertainty.delete.uncertainty_constraint.id_null"; //$NON-NLS-1$

	/**
	 * Version Tokenizer
	 */
	public static final String EX_VERSION_TOKENIZER_NULL_PARAM = "ex.version_tokenizer.null_param"; //$NON-NLS-1$

	/** Validator */
	public static final String EX_CONTAINERVALIDATOR_NOTVALID = "ex.container_validator.not_valid"; //$NON-NLS-1$
	public static final String EX_YMLVALIDATOR_NOTVALID = "ex.yml_validator.not_valid"; //$NON-NLS-1$

	/************************
	 * Exception Keys
	 ************************/

	/************************
	 * Message Keys
	 ************************/
	/* Default */
	public static final String DATE_FORMAT = "date.format"; //$NON-NLS-1$
	public static final String DATE_FORMAT_SHORT = "date.format-short"; //$NON-NLS-1$
	public static final String DATETIME_FORMAT = "datetime.format"; //$NON-NLS-1$
	public static final String EMPTY_STRING = "empty_string"; //$NON-NLS-1$
	public static final String CARRIAGE_RETURN = "carriage_return"; //$NON-NLS-1$
	public static final String MANDATORY_FIELD = "mandatory_field"; //$NON-NLS-1$
	public static final String ERROR_TITLE = "error_title"; //$NON-NLS-1$
	public static final String MSG_YES = "msg.yes"; //$NON-NLS-1$
	public static final String MSG_NO = "msg.no"; //$NON-NLS-1$
	public static final String MSG_BTN_ADD = "msg.btn.add"; //$NON-NLS-1$
	public static final String MSG_BTN_BACK = "msg.btn.back"; //$NON-NLS-1$
	public static final String MSG_BTN_BROWSE = "msg.btn.browse"; //$NON-NLS-1$
	public static final String MSG_BTN_CANCEL = "msg.btn.cancel"; //$NON-NLS-1$
	public static final String MSG_BTN_CLEAR = "msg.btn.clear"; //$NON-NLS-1$
	public static final String MSG_BTN_CLOSE = "msg.btn.close"; //$NON-NLS-1$
	public static final String MSG_BTN_CONFIGURATION = "msg.btn.configuration"; //$NON-NLS-1$
	public static final String MSG_BTN_CONFIRM = "msg.btn.confirm"; //$NON-NLS-1$
	public static final String MSG_BTN_CONNECT = "msg.btn.connect"; //$NON-NLS-1$
	public static final String MSG_BTN_COPY = "msg.btn.copy"; //$NON-NLS-1$
	public static final String MSG_BTN_CREATE = "msg.btn.create"; //$NON-NLS-1$
	public static final String MSG_BTN_DELETE = "msg.btn.delete"; //$NON-NLS-1$
	public static final String MSG_BTN_DONE = "msg.btn.done"; //$NON-NLS-1$
	public static final String MSG_BTN_EDIT = "msg.btn.edit"; //$NON-NLS-1$
	public static final String MSG_BTN_EXECUTE = "msg.btn.execute"; //$NON-NLS-1$
	public static final String MSG_BTN_EXPORT = "msg.btn.export"; //$NON-NLS-1$
	public static final String MSG_BTN_IMPORT = "msg.btn.import"; //$NON-NLS-1$
	public static final String MSG_BTN_OPEN = "msg.btn.open"; //$NON-NLS-1$
	public static final String MSG_BTN_QUIT = "msg.btn.quit"; //$NON-NLS-1$
	public static final String MSG_BTN_RESET = "msg.btn.reset"; //$NON-NLS-1$
	public static final String MSG_BTN_SAVE = "msg.btn.save"; //$NON-NLS-1$
	public static final String MSG_BTN_SELECT = "msg.btn.select"; //$NON-NLS-1$
	public static final String MSG_BTN_TAG = "msg.btn.tag"; //$NON-NLS-1$
	public static final String MSG_BTN_UPDATE = "msg.btn.update"; //$NON-NLS-1$
	public static final String MSG_BTN_VIEW = "msg.btn.view"; //$NON-NLS-1$
	public static final String MSG_BTN_LVLGUIDANCE = "msg.btn.levels_guidance"; //$NON-NLS-1$
	public static final String MSG_DECISION = "msg.decision"; //$NON-NLS-1$
	public static final String MSG_DECISION_GROUP = "msg.decision.group"; //$NON-NLS-1$
	public static final String MSG_PIRT = "msg.pirt"; //$NON-NLS-1$
	public static final String MSG_QOIPLANNING = "msg.qoiplanning"; //$NON-NLS-1$
	public static final String MSG_PCMM = "msg.pcmm"; //$NON-NLS-1$
	public static final String MSG_COM = "msg.communicate"; //$NON-NLS-1$
	public static final String MSG_UNCERTAINTY = "msg.uncertainty"; //$NON-NLS-1$
	public static final String MSG_UNCERTAINTY_GROUP = "msg.uncertainty.group"; //$NON-NLS-1$
	public static final String MSG_SYSREQUIREMENT = "msg.requirement"; //$NON-NLS-1$
	public static final String MSG_SYSREQUIREMENT_GROUP = "msg.requirement.group"; //$NON-NLS-1$
	public static final String MSG_CREDIBILITYVIEW_TITLE = "msg.credibility_view.title"; //$NON-NLS-1$
	public static final String MSG_CREDIBILITYVIEW_ITEMTITLE = "msg.credibility_view.item_title"; //$NON-NLS-1$
	public static final String MSG_TITLE_EMPTY = "msg.title.empty"; //$NON-NLS-1$
	public static final String MSG_EMPTY = "msg.empty"; //$NON-NLS-1$
	public static final String MSG_NO_DATA = "msg.no_data"; //$NON-NLS-1$
	public static final String MSG_OBJECT_NULL = "msg.object.null"; //$NON-NLS-1$
	public static final String MSG_RICHTEXT_CLICK_BAR_DEFAULT = "msg.richtext.click_bar.default"; //$NON-NLS-1$
	public static final String MSG_RICHTEXT_CLICK_BAR = "msg.richtext.click_bar"; //$NON-NLS-1$
	public static final String MSG_LBL_REQUIRED = "msg.lbl.required"; //$NON-NLS-1$
	public static final String MSG_LBL_REQUIRED_WITH_CONDITION = "msg.lbl.required_with_condition"; //$NON-NLS-1$

	/* Global */
	public static final String MSG_VERSION_TABNAME = "msg.version.tabname"; //$NON-NLS-1$
	public static final String MSG_VERSION_TABNAME_WITHVERSION = "msg.version.tabname.with_version"; //$NON-NLS-1$
	public static final String MSG_VERSION_CURRENT_LABEL = "msg.version_current.label"; //$NON-NLS-1$
	public static final String MSG_VERSION_ORIGIN_LABEL = "msg.version_origin.label"; //$NON-NLS-1$
	public static final String MSG_VERSION_ORIGIN_UNDEFINED = "msg.version_origin.undefined"; //$NON-NLS-1$
	public static final String MSG_VERSION_UNDEFINED = "msg.version.undefined"; //$NON-NLS-1$
	public static final String MSG_EDITOR_SAVE_BEFORE = "msg.editor.save_before"; //$NON-NLS-1$
	public static final String MSG_EDITOR_SAVE_BEGINTASK = "msg.editor.save.begin_task"; //$NON-NLS-1$
	public static final String MSG_EDITOR_SAVE_COPYTASK = "msg.editor.save.copy_task"; //$NON-NLS-1$
	public static final String MSG_EDITOR_SAVE_ZIPTASK = "msg.editor.save.zip_task"; //$NON-NLS-1$
	public static final String MSG_EDITOR_SAVE_REMOVEOLDTASK = "msg.editor.save.remove_old_task"; //$NON-NLS-1$
	public static final String MSG_EDITOR_DELETE_TMPFOLDER = "msg.editor.delete.tmp_folder"; //$NON-NLS-1$
	public static final String MSG_CONNECTED = "msg.connected"; //$NON-NLS-1$
	public static final String MSG_NOT_CONNECTED = "msg.not_connected"; //$NON-NLS-1$
	public static final String MSG_SAVED = "msg.saved"; //$NON-NLS-1$
	public static final String MSG_NOT_SAVED = "msg.not_saved"; //$NON-NLS-1$
	public static final String MSG_VERSION_ORIGIN_BEFORE_0_2_0 = "msg.version_origin_before_0.2.0"; //$NON-NLS-1$

	/* Generic Table */
	public static final String MSG_TABLE_COLUMN_ID = "msg.table.column.id"; //$NON-NLS-1$

	/* Widgets */
	public static final String MSG_LINKWIDGET_BTN_FILE = "msg.link_widget.btn.file"; //$NON-NLS-1$
	public static final String MSG_LINKWIDGET_BTN_URL = "msg.link_widget.btn.url"; //$NON-NLS-1$
	public static final String MSG_LINKWIDGET_CAPTION_LBL = "msg.link_widget.caption.lbl"; //$NON-NLS-1$
	public static final String ERR_LINKWIDGET_FILE_NOTFILE = "err.link_widget.file.not_file"; //$NON-NLS-1$
	public static final String ERR_LINKWIDGET_URL_NOTVALID = "err.link_widget.url.not_valid"; //$NON-NLS-1$

	/* New Credibility Evidence Folder Structure - Wizard */
	public static final String MSG_EVIDFOLDERSTRUCT_TITLE = "msg.new_cred_evid_folder_structure.title"; //$NON-NLS-1$
	public static final String MSG_NEWCFFOLDERSTRUCTUREWIZARD_WINDOWTITLE = "msg.new_cf_folder_structure_wizard.window_title"; //$NON-NLS-1$
	public static final String ERR_EVIDFOLDERSTRUCT = "err.new_cred_evid_folder_structure"; //$NON-NLS-1$
	public static final String ERR_EVIDFOLDERSTRUCT_SETUP_INCOMPLETE = "err.new_cred_evid_folder_structure.setup_incomplete"; //$NON-NLS-1$
	public static final String ERR_EVIDFOLDERSTRUCT_CONTAINER_NULL = "err.new_cred_evid_folder_structure.container_null"; //$NON-NLS-1$
	public static final String ERR_NEWCFFOLDERSTRUCTUREWIZARD_ERROR_OCCURED = "err.new_cf_folder_structure_wizard.error_occured"; //$NON-NLS-1$
	/* New Credibility Evidence Folder Structure - Wizard Page */
	public static final String MSG_NEWCFFOLDERSTRUCTUREWIZARD_PAGENAME = "msg.new_cf_folder_structure_wizard.page_name"; //$NON-NLS-1$
	public static final String MSG_NEWCFFOLDERSTRUCTUREWIZARD_PAGETITLE = "msg.new_cf_folder_structure_wizard.page_title"; //$NON-NLS-1$
	public static final String MSG_NEWCFFOLDERSTRUCTUREWIZARD_PAGEDESC = "msg.new_cf_folder_structure_wizard.page_description"; //$NON-NLS-1$

	/* New Credibility Process - Wizard */
	public static final String MSG_NEWCFPROCESS_WIZARD_WINDOWTITLE = "msg.new_cf_process_wizard.window_title"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_WIZARD_CFFILE_MISSING = "err.new_cf_process_wizard.cf_file.missing"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_WIZARD_PIRTFILE_MISSING = "err.new_cf_process_wizard.pirt_file.missing"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_WIZARD_PCMMFILE_MISSING = "err.new_cf_process_wizard.pcmm_file.missing"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_WIZARD_WORKDIR_MISSING = "err.new_cf_process_wizard.working_dir.missing"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_WIZARD_ERROR_OCCURED = "err.new_cf_process_wizard.error_occured"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_WIZARD_WEB_PROJECT_TYPE_NULL = "err.new_cf_process_wizard.web.project_type_null"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_WIZARD_WEB_MODEL_NULL = "err.new_cf_process_wizard.web.model_null"; //$NON-NLS-1$
	/* New Credibility Process - File Selection Wizard Page */
	public static final String MSG_NEWCFPROCESS_FILESELECTION_PAGE_PAGENAME = "msg.new_cf_process_wizard.file_selection_page.page_name"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_FILESELECTION_PAGE_TITLE = "msg.new_cf_process_wizard.file_selection_page.title"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_FILESELECTION_PAGE_DESCRIPTION = "msg.new_cf_process_wizard.file_selection_page.description"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_FILESELECTION_PAGE_CFFILEALREADYEXISTS = "err.new_cf_process_wizard.file_selection_page.cf_file_already_exists"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_FILESELECTION_PAGE_FILENAME = "msg.new_cf_process_wizard.file_selection_page.file_name"; //$NON-NLS-1$
	/* New Credibility Process - Backend Type Selection Wizard Page */
	public static final String MSG_NEWCFPROCESS_BACKENDTYPE_PAGE_PAGENAME = "msg.new_cf_process.backendtype_page.page_name"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_BACKENDTYPE_PAGE_TITLE = "msg.new_cf_process.backendtype_page.title"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_BACKENDTYPE_PAGE_DESCRIPTION = "msg.new_cf_process.backendtype_page.description"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_BACKENDTYPE_PAGE_LBL = "msg.new_cf_process.backendtype_page.lbl"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_BACKENDTYPE_PAGE_LOCALFILE_BTN = "msg.new_cf_process.backendtype_page.local_file.btn"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_BACKENDTYPE_PAGE_WEB_BTN = "msg.new_cf_process.backendtype_page.web.btn"; //$NON-NLS-1$
	/* New Credibility Process - Local Setup Wizard Page */
	public static final String MSG_NEWCFPROCESS_LOCALSETUP_PAGE_PAGENAME = "msg.new_cf_process_wizard.localsetup_page.page_name"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_LOCALSETUP_PAGE_TITLE = "msg.new_cf_process_wizard.localsetup_page.title"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_LOCALSETUP_PAGE_DESCRIPTION = "msg.new_cf_process_wizard.localsetup_page.description"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_LOCALSETUP_PAGE_CONF_FOLDER_PATH = "msg.new_cf_process_wizard.localsetup_page.conf_folder_path"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_LOCALSETUP_PAGE_PIRT_SCHEMA_PATH = "msg.new_cf_process_wizard.localsetup_page.pirt_schema_path"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_LOCALSETUP_PAGE_QOIPLANNING_SCHEMA_PATH = "msg.new_cf_process_wizard.localsetup_page.qoiplanning_schema_path"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_LOCALSETUP_PAGE_PCMM_SCHEMA_PATH = "msg.new_cf_process_wizard.localsetup_page.pcmm_schema_path"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_LOCALSETUP_PAGE_UNCERTAINTY_SCHEMA_PATH = "msg.new_cf_process_wizard.localsetup_page.uncertainty_schema_path"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_LOCALSETUP_PAGE_REQUIREMENT_SCHEMA_PATH = "msg.new_cf_process_wizard.localsetup_page.sys_requirement_schema_path"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_LOCALSETUP_PAGE_DECISION_SCHEMA_PATH = "msg.new_cf_process_wizard.localsetup_page.decision_schema_path"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_LOCALSETUP_PAGE_BTN_ADVANCED = "msg.new_cf_process_wizard.localsetup_page.btn.advanced"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_LOCALSETUP_PAGE_EMPTYFILE = "err.new_cf_process_wizard.localsetup_page.empty_file"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_LOCALSETUP_PAGE_BAD_FILE = "err.new_cf_process_wizard.localsetup_page.bad_file"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_LOCALSETUP_PAGE_SAME_FILES = "err.new_cf_process_wizard.localsetup_page.same_files"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_LOCALSETUP_PAGE_BAD_CONF_FOLDER_PATH = "err.new_cf_process_wizard.localsetup_page.bad_conf_folder_path"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_LOCALSETUP_PAGE_BAD_STRUCT_FOLDER_PATH = "err.new_cf_process_wizard.localsetup_page.bad_struct_folder_path"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_LOCALSETUP_PAGE_EVID_PARENT_PATH = "err.new_cf_process_wizard.localsetup_page.lbl_evidence_parent_path"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_LOCALSETUP_PAGE_CHBX_EVID_STRUCT = "err.new_cf_process_wizard.localsetup_page.chbx_evidence_struct"; //$NON-NLS-1$
	/* New Credibility Process - Local Setup Advanced Wizard Page */
	public static final String MSG_NEWCFPROCESS_LOCALSETUPADVANCED_PAGE_TITLE = "msg.new_cf_process_wizard.localsetup_advanced_page.advanced.title"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_LOCALSETUPADVANCED_PAGE_BTN_BACK_TO_DEFAULT = "msg.new_cf_process_wizard.localsetup_advanced_page.btn.back_to_default"; //$NON-NLS-1$
	/* New Credibility Process - Web Setup Wizard Page */
	public static final String MSG_NEWCFPROCESS_WEBSETUP_PAGE_PAGENAME = "msg.new_cf_process.websetup_page.page_name"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBSETUP_PAGE_TITLE = "msg.new_cf_process.websetup_page.title"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBSETUP_PAGE_DESCRIPTION = "msg.new_cf_process.websetup_page.description"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBSETUP_PAGE_SERVER_LBL = "msg.new_cf_process.websetup_page.server.lbl"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBSETUP_PAGE_SERVER_TEST_BTN = "msg.new_cf_process.websetup_page.server_test.btn"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBSETUP_PAGE_SERVER_TEST_VALID_LBL = "msg.new_cf_process.websetup_page.server_test.valid.lbl"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBSETUP_PAGE_SERVER_TEST_NOTVALID_LBL = "msg.new_cf_process.websetup_page.server_test.not_valid.lbl"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_WEBSETUP_PAGE_SERVER_URL_NOTVALID = "err.new_cf_process.websetup_page.server_url_not_valid"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_WEBSETUP_PAGE_SERVER_UNREACHABLE = "err.new_cf_process.websetup_page.server_unreachable"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_WEBSETUP_PAGE_BAD_STRUCT_FOLDER_PATH = "err.new_cf_process.websetup_page.bad_struct_folder_path"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_WEBSETUP_PAGE_EVID_PARENT_PATH = "err.new_cf_process.websetup_page.lbl_evidence_parent_path"; //$NON-NLS-1$
	public static final String ERR_NEWCFPROCESS_WEBSETUP_PAGE_CHBX_EVID_STRUCT = "err.new_cf_process.websetup_page.chbx_evidence_struct"; //$NON-NLS-1$
	/* New Credibility Process - Web Project Type Wizard Page */
	public static final String MSG_NEWCFPROCESS_WEBPROJECTTYPE_PAGE_PAGENAME = "msg.new_cf_process.webproject_type_page.page_name"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECTTYPE_PAGE_TITLE = "msg.new_cf_process.webproject_type_page.title"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECTTYPE_PAGE_DESCRIPTION = "msg.new_cf_process.webproject_type_page.description"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECTTYPE_PAGE_LBL = "msg.new_cf_process.webproject_type_page.lbl"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECTTYPE_PAGE_NEWPROJECT_BTN = "msg.new_cf_process.webproject_type_page.new_project.btn"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECTTYPE_PAGE_EXISTINGPROJECT_BTN = "msg.new_cf_process.webproject_type_page.existing_project.btn"; //$NON-NLS-1$
	/* New Credibility Process - Web Project New Setup Wizard Page */
	public static final String MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_PAGENAME = "msg.new_cf_process.webproject_newsetup_page.page_name"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_TITLE = "msg.new_cf_process.webproject_newsetup_page.title"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_DESCRIPTION = "msg.new_cf_process.webproject_newsetup_page.description"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_LBL = "msg.new_cf_process.webproject_newsetup_page.lbl"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_APPLICATION_LBL = "msg.new_cf_process.webproject_newsetup_page.application.lbl"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_APPLICATION_HELPER_INFO = "msg.new_cf_process.webproject_newsetup_page.application.helper_info"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_APPLICATION_HELPER_WARN = "msg.new_cf_process.webproject_newsetup_page.application.helper_warn"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECT_NEWSETUP_PAGE_CONTACT_LBL = "msg.new_cf_process.webproject_newsetup_page.contact.lbl"; //$NON-NLS-1$
	/* New Credibility Process - Web Project Existing Setup Wizard Page */
	public static final String MSG_NEWCFPROCESS_WEBPROJECT_EXISTINGSETUP_PAGE_PAGENAME = "msg.new_cf_process.webproject_existingsetup_page.page_name"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECT_EXISTINGSETUP_PAGE_TITLE = "msg.new_cf_process.webproject_existingsetup_page.title"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECT_EXISTINGSETUP_PAGE_DESCRIPTION = "msg.new_cf_process.webproject_existingsetup_page.description"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECT_EXISTINGSETUP_PAGE_LBL = "msg.new_cf_process.webproject_existingsetup_page.lbl"; //$NON-NLS-1$
	public static final String MSG_NEWCFPROCESS_WEBPROJECT_EXISTINGSETUP_PAGE_TABLE_COLUMN_MODEL = "msg.new_cf_process.webproject_existingsetup_page.table_column.model"; //$NON-NLS-1$

	/* Credibility Editor */
	public static final String WRN_CREDIBILITYEDITOR_CFTMPFOLDER_TITLE = "wrn.credibility_editor.cftmp_exist.title"; //$NON-NLS-1$
	public static final String WRN_CREDIBILITYEDITOR_CFTMPFOLDER_NOTRECOVERABLE = "wrn.credibility_editor.cftmp_exist.not_recoverable"; //$NON-NLS-1$
	public static final String WRN_CREDIBILITYEDITOR_CFTMPFOLDER_CONFIRMRECOVER = "wrn.credibility_editor.cftmp_exist.confirm_recovering"; //$NON-NLS-1$
	public static final String WRN_CREDIBILITYEDITOR_DBMIGRATION_CONFIRM_TITLE = "wrn.credibility_editor.database_migration.confirm.title"; //$NON-NLS-1$
	public static final String WRN_CREDIBILITYEDITOR_DBMIGRATION_CONFIRM_TXT = "wrn.credibility_editor.database_migration.confirm.txt"; //$NON-NLS-1$
	public static final String ERR_CREDIBILITYEDITOR_TITLE = "err.credibility_editor.title"; //$NON-NLS-1$
	public static final String ERR_CREDIBILITYEDITOR_LOADING = "err.credibility_editor.loading_impossible"; //$NON-NLS-1$
	public static final String ERR_CREDIBILITYEDITOR_MOVE = "err.credibility_editor.move_impossible"; //$NON-NLS-1$
	public static final String ERR_CREDIBILITYEDITOR_SAVING = "err.credibility_editor.saving_impossible"; //$NON-NLS-1$
	public static final String ERR_CREDIBILITYEDITOR_BADINPUT = "err.credibility_editor.bad_input"; //$NON-NLS-1$
	public static final String ERR_CREDIBILITYEDITOR_DBMIGRATION_TXT = "err.credibility_editor.database_migration.txt"; //$NON-NLS-1$
	public static final String ERR_CREDIBILITYEDITOR_DB_NOT_RECOVERABLE = "err.credibility_editor.database_not_recoverable"; //$NON-NLS-1$

	/** Generic Parameter Fields */
	public static final String ERR_GENERICPARAM_PARAMETER_DESIRED = "err.generic_param.parameter.desired"; //$NON-NLS-1$
	public static final String ERR_GENERICPARAM_PARAMETER_DESIRED_CONDITION_NOTVALID = "err.generic_param.parameter.desired.condition_not_valid"; //$NON-NLS-1$
	public static final String ERR_GENERICPARAM_PARAMETER_REQUIRED = "err.generic_param.parameter.required"; //$NON-NLS-1$
	public static final String ERR_GENERICPARAM_PARAMETER_REQUIRED_CONDITION_NOTVALID = "err.generic_param.parameter.required.condition_not_valid"; //$NON-NLS-1$
	public static final String ERR_GENERICPARAM_VALUE_CONSTRAINT_NOTVALID = "err.generic_param.value.constraint.not_valid"; //$NON-NLS-1$
	public static final String ERR_GENERICPARAM_VALUE_CONSTRAINT_EXCEPTION = "err.generic_param.value.constraint.exception"; //$NON-NLS-1$

	/*******************************
	 * Authentication
	 */
	public static final String MSG_WEB_DIALOG_AUTHENTICATION_TITLE = "msg.web.dialog.authentication.title"; //$NON-NLS-1$
	public static final String MSG_WEB_DIALOG_AUTHENTICATION_DESCRIPTION = "msg.web.dialog.authentication.desc"; //$NON-NLS-1$
	public static final String MSG_WEB_DIALOG_AUTHENTICATION_SERVER_LBL = "msg.web.dialog.authentication.server.lbl"; //$NON-NLS-1$
	public static final String MSG_WEB_DIALOG_AUTHENTICATION_USERID_LBL = "msg.web.dialog.authentication.userid.lbl"; //$NON-NLS-1$
	public static final String MSG_WEB_DIALOG_AUTHENTICATION_USERPASSWORD_LBL = "msg.web.dialog.authentication.user_password.lbl"; //$NON-NLS-1$
	public static final String MSG_WEB_DIALOG_AUTHENTICATION_USERPASSWORD_EXPERIMENTAL_HELPER = "msg.web.dialog.authentication.experimental.helper"; //$NON-NLS-1$
	public static final String ERR_WEB_DIALOG_AUTHENTICATION_LOADUSERFAILS = "err.web.dialog.authentication.load_user_fails"; //$NON-NLS-1$
	public static final String ERR_WEB_DIALOG_AUTHENTICATION_HOST_UNREACHABLE = "err.web.dialog.authentication.host_unreachable"; //$NON-NLS-1$
	public static final String ERR_WEB_DIALOG_AUTHENTICATION_INVALID_CREDENTIALS = "err.web.dialog.authentication.invalid_credentials"; //$NON-NLS-1$
	public static final String ERR_WEB_DIALOG_AUTHENTICATION_INVALID_MODEL = "err.web.dialog.authentication.invalid_model"; //$NON-NLS-1$

	/** Configuration View */
	public static final String MSG_CONF_VIEW_ITEMTITLE = "msg.conf_view.item_title"; //$NON-NLS-1$
	public static final String MSG_CONF_VIEW_TAB_GLOBAL = "msg.conf_view.tab.global"; //$NON-NLS-1$
	public static final String MSG_CONF_VIEW_TAB_IMPORT = "msg.conf_view.tab.import"; //$NON-NLS-1$
	public static final String MSG_CONF_VIEW_TAB_EXPORT = "msg.conf_view.tab.export"; //$NON-NLS-1$

	/** Global configuration View */
	public static final String MSG_CONF_GLOBALVIEW_TITLE = "msg.conf_globalview.title"; //$NON-NLS-1$
	public static final String MSG_CONF_GLOBALVIEW_ITEMTITLE = "msg.conf_globalview.item_title"; //$NON-NLS-1$
	public static final String MSG_CONF_GLOBALVIEW_BROWSER_TITLE = "msg.conf_globalview.browser.title"; //$NON-NLS-1$
	public static final String ERR_CONF_GLOBALVIEW_TITLE = "err.conf_globalview.title"; //$NON-NLS-1$
	public static final String ERR_CONF_GLOBALVIEW_CONFNULL = "err.conf_globalview.conf_null"; //$NON-NLS-1$

	/** Import View */
	public static final String MSG_CONF_IMPORTVIEW_TITLE = "msg.conf_importview.title"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_ITEMTITLE = "msg.conf_importview.item_title"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_DECISION_TITLE = "msg.conf_importview.decision.title"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_QOIPLANNING_TITLE = "msg.conf_importview.qoi_planning.title"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_PIRT_TITLE = "msg.conf_importview.pirt_title"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_PCMM_TITLE = "msg.conf_importview.pcmm_title"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_UNCERTAINTY_TITLE = "msg.conf_importview.uncertainty_title"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_REQUIREMENTS_TITLE = "msg.conf_importview.requirements.title"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_DECISION_SCHEMAPATH = "msg.conf_importview.decision.schema_path"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_QOIPLANNING_SCHEMAPATH = "msg.conf_importview.qoi_planning.schema_path"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_PIRT_SCHEMAPATH = "msg.conf_importview.pirt_schema_path"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_PCMM_SCHEMAPATH = "msg.conf_importview.pcmm_schema_path"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_UNCERTAINTY_SCHEMAPATH = "msg.conf_importview.uncertainty_schema_path"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_REQUIREMENTS_SCHEMAPATH = "msg.conf_importview.requirements_schema_path"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_IMPORT_SUCCESS = "msg.conf_importview.import.success"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_IMPORT_NOTHING = "msg.conf_importview.import.nothing"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_IMPORT_NEEDSAVE = "msg.conf_importview.import.needs_save"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_IMPORT_CANCELLED = "msg.conf_importview.import.cancelled"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_IMPORT_FILE_NOTEXISTS = "msg.conf_importview.import.file.not_exists"; //$NON-NLS-1$
	public static final String MSG_CONF_IMPORTVIEW_IMPORT_FILE_NOTVALID = "msg.conf_importview.import.file.not_valid"; //$NON-NLS-1$
	public static final String ERR_CONF_IMPORTVIEW_IMPORT_ERROR_OCCURED = "err.conf_importview.import.error_occured"; //$NON-NLS-1$

	/** Export View */
	public static final String MSG_CONF_EXPORTVIEW_TITLE = "msg.conf_exportview.title"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_ITEMTITLE = "msg.conf_exportview.item.title"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_DECISION_TITLE = "msg.conf_exportview.decision.title"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_QOIPLANNING_TITLE = "msg.conf_exportview.qoi_planning.title"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_PIRT_TITLE = "msg.conf_exportview.pirt.title"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_PCMM_TITLE = "msg.conf_exportview.pcmm.title"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_UNCERTAINTY_TITLE = "msg.conf_exportview.uncertainty.title"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_SYSREQUIREMENTS_TITLE = "msg.conf_exportview.sys_requirements.title"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_DATA_TITLE = "msg.conf_exportview.data.title"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_DECISION_SCHEMAPATH = "msg.conf_exportview.decision.schema_path"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_QOIPLANNING_SCHEMAPATH = "msg.conf_exportview.qoi_planning.schema_path"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_PIRTSCHEMAPATH = "msg.conf_exportview.pirt.schema_path"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_PCMMSCHEMAPATH = "msg.conf_exportview.pcmm.schema_path"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_UNCERTAINTYSCHEMAPATH = "msg.conf_exportview.uncertainty.schema_path"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_SYSREQUIREMENTS_SCHEMAPATH = "msg.conf_exportview.sys_requirements.schema_path"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_DATA_LBL = "msg.conf_exportview.data_lbl"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_DATA_PIRT_LBL = "msg.conf_exportview.data_pirt_lbl"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_DATA_PCMM_LBL = "msg.conf_exportview.data_pcmm_lbl"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_DATA_INTPURPOSE_LBL = "msg.conf_exportview.data_intendedpurpose_lbl"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_DATA_DECISION_LBL = "msg.conf_exportview.data_decision_lbl"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_DATA_SYSREQ_LBL = "msg.conf_exportview.data_sysreq_lbl"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_DATA_UNCERTAINTY_LBL = "msg.conf_exportview.data_uncertainty_lbl"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_NEEDSAVE = "msg.conf_exportview.needs_save"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_EXPORT_CANCELLED = "msg.conf_exportview.export.cancelled"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_EXPORT_SUCCESS = "msg.conf_exportview.export.success"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_EXPORT_FILE_NULL = "msg.conf_exportview.export.file_null"; //$NON-NLS-1$
	public static final String ERR_CONF_EXPORTVIEW_EXPORT_ERROR_OCCURED = "err.conf_exportview.export.error_occured"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_DATAEXPORT_SUCCESS = "msg.conf_exportview.data_export.success"; //$NON-NLS-1$
	public static final String MSG_CONF_EXPORTVIEW_DATAEXPORT_FILE_NULL = "msg.conf_exportview.data_export.file_null"; //$NON-NLS-1$
	public static final String ERR_CONF_EXPORTVIEW_DATAEXPORT_ERROR_OCCURED = "err.conf_exportview.data_export.error_occured"; //$NON-NLS-1$
	public static final String MSG_EXPORTVIEW_PIRT_QOI_LBL = "msg.conf_exportview.pirt.qoi_lbl"; //$NON-NLS-1$
	public static final String MSG_EXPORTVIEW_PCMM_TAG_LBL = "msg.conf_exportview.pcmm.tag_lbl"; //$NON-NLS-1$
	public static final String MSG_EXPORTVIEW_PCMM_TAG_CURRENT = "msg.conf_exportview.pcmm.tag_current"; //$NON-NLS-1$
	public static final String MSG_EXPORTVIEW_PCMM_FEATURES_LBL = "msg.conf_exportview.pcmm.features_lbl"; //$NON-NLS-1$
	public static final String MSG_EXPORTVIEW_PCMM_CHECKBOX_ASSESSMENT = "msg.conf_exportview.pcmm.checkbox_assessment"; //$NON-NLS-1$
	public static final String MSG_EXPORTVIEW_PCMM_CHECKBOX_EVIDENCE = "msg.conf_exportview.pcmm.checkbox_evidence"; //$NON-NLS-1$
	public static final String MSG_EXPORTVIEW_PCMM_CHECKBOX_PLANNING = "msg.conf_exportview.pcmm.checkbox_planning"; //$NON-NLS-1$
	public static final String MSG_EXPORTVIEW_INTPURPOSE_LBL = "msg.conf_exportview.intended_purpose.lbl"; //$NON-NLS-1$
	public static final String MSG_EXPORTVIEW_DECISION_LBL = "msg.conf_exportview.decision.lbl"; //$NON-NLS-1$
	public static final String MSG_EXPORTVIEW_SYSREQ_LBL = "msg.conf_exportview.sys_req.lbl"; //$NON-NLS-1$
	public static final String MSG_EXPORTVIEW_UNCERTAINTY_LBL = "msg.conf_exportview.uncertainty.lbl"; //$NON-NLS-1$

	/** Import Dialog */
	public static final String MSG_IMPORTDLG_TITLE = "msg.import_dlg.title"; //$NON-NLS-1$
	public static final String MSG_IMPORTDLG_SUBTITLE = "msg.import_dlg.subtitle"; //$NON-NLS-1$
	public static final String MSG_IMPORTDLG_DESC = "msg.import_dlg.description"; //$NON-NLS-1$
	public static final String MSG_IMPORTDLG_IMPORTCLASS_TOADD = "msg.import_dlg.lbl.import_class.to_add"; //$NON-NLS-1$
	public static final String MSG_IMPORTDLG_IMPORTCLASS_TOUPDATE = "msg.import_dlg.lbl.import_class.to_update"; //$NON-NLS-1$
	public static final String MSG_IMPORTDLG_IMPORTCLASS_TODELETE = "msg.import_dlg.lbl.import_class.to_delete"; //$NON-NLS-1$
	public static final String MSG_IMPORTDLG_IMPORTCLASS_NOCHANGES = "msg.import_dlg.lbl.import_class.no_changes"; //$NON-NLS-1$
	public static final String MSG_IMPORTDLG_NOCHANGES = "msg.import_dlg.lbl.no_changes"; //$NON-NLS-1$
	public static final String MSG_IMPORTDLG_BTN_CONFIRM = "msg.import_dlg.btn.confirm"; //$NON-NLS-1$
	public static final String MSG_IMPORTDLG_WRN_DATADELETION_CONFIRM = "msg.import_dlg.warn.data_deletion_confirm"; //$NON-NLS-1$

	/** Home View */
	public static final String MSG_HOMEVIEW_BTN_PLANNING = "msg.home_view.btn.planning"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_PIRT = "msg.home_view.btn.pirt"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_PCMM = "msg.home_view.btn.pcmm"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_COMMUNICATE = "msg.home_view.btn.communicate"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_PIRT_REF = "msg.home_view.btn.pirt.reference"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_PCMM_REF = "msg.home_view.btn.pcmm.reference"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_COM_REF = "msg.home_view.btn.communicate.reference"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_GEN_REPORT = "msg.home_view.btn.generate_report"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_TXT_PIRT = "msg.home_view.txt.pirt"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_TXT_PCMM = "msg.home_view.txt.pcmm"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_PIRT_PROGRESS_LABEL = "msg.home_view.pirt.progress.label"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_PCMM_WARNING_LABEL = "msg.home_view.pcmm.warning.label"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_PCMM_ERROR_LABEL = "msg.home_view.pcmm.error.label"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_PCMM_PROGRESS_LABEL = "msg.home_view.pcmm.progress.label"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_DIALOG_TITLE = "msg.home_view.dialog.title"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_PCMM_PREREQUISITE_TITLE = "msg.home_view.pcmm.prerequisite.title"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_PCMM_PREREQUISITE_TXT = "msg.home_view.pcmm.prerequisite.txt"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_INTENDEDPURPOSE = "msg.home_view.btn.intended_purpose"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_REQUIREMENT = "msg.home_view.btn.requirement"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_QOIPLANNER = "msg.home_view.btn.qoi_planner"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_UNCERTAINTY = "msg.home_view.btn.uncertainty"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_PCMMPLANNING = "msg.home_view.btn.pcmm_planning"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_DECISION = "msg.home_view.btn.decision"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_LIMITRISKS = "msg.home_view.btn.limit_risks"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_PEERREVIEW = "msg.home_view.btn.peer_review"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_EVIDPACKAGE_OVERVIEW = "msg.home_view.btn.evidpackage_overview"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_EVIDPACKAGE = "msg.home_view.btn.evidpackage"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_PCMMASSESS = "msg.home_view.btn.pcmm_assess"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_DOCSTRUCTURE = "msg.home_view.btn.doc_structure"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_MARGINBOUNDS = "msg.home_view.btn.margin_bounds"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_DELETE_PROJECT = "msg.home_view.btn.delete_project"; //$NON-NLS-1$
	public static final String MSG_HOMEVIEW_BTN_DELETE_PROJECT_CONFIRM = "msg.home_view.btn.delete_project.confirm"; //$NON-NLS-1$

	public static final String ERR_HOMEVIEW_PIRTREFFILE_TITLE = "err.home_view.pirt_ref_file.title"; //$NON-NLS-1$
	public static final String ERR_HOMEVIEW_PIRTREFFILE_MSG = "err.home_view.pirt_ref_file.msg"; //$NON-NLS-1$
	public static final String ERR_HOMEVIEW_PCMMREFFILE_TITLE = "err.home_view.pcmm_ref_file.title"; //$NON-NLS-1$
	public static final String ERR_HOMEVIEW_PCMMREFFILE_MSG = "err.home_view.pcmm_ref_file.msg"; //$NON-NLS-1$
	public static final String ERR_HOMEVIEW_PCMM_PROGRESS_ERROR = "err.home_view.pcmm.progress.error.msg"; //$NON-NLS-1$
	public static final String ERR_HOMEVIEW_PCMM_ERROR_BADGE_ERROR = "err.home_view.pcmm.error.bagde.error.msg"; //$NON-NLS-1$
	public static final String ERR_HOMEVIEW_PCMM_WARNING_BADGE_ERROR = "err.home_view.pcmm.warning.badge.error.msg"; //$NON-NLS-1$

	/**************************
	 * PIRT
	 */
	public static final String MSG_PIRT_MODEL_PHEN = "msg.pirt.model.phenomenon"; //$NON-NLS-1$
	public static final String MSG_PIRT_MODEL_PHENGROUP = "msg.pirt.model.phenomenon_group"; //$NON-NLS-1$
	public static final String MSG_PIRT_BTN_ADD = "msg.pirt.btn.add"; //$NON-NLS-1$
	/* Add QoI Dialog */
	public static final String MSG_ADDQOI_PAGE_NAME = "msg.add_qoi.page_name"; //$NON-NLS-1$
	public static final String MSG_ADDQOI_TITLE = "msg.add_qoi.title"; //$NON-NLS-1$
	public static final String MSG_ADDQOI_DESCRIPTION = "msg.add_qoi.description"; //$NON-NLS-1$
	public static final String ERR_ADDQOI_SYMBOL_MANDATORY = "err.add_qoi.symbol.mandatory"; //$NON-NLS-1$

	public static final String MSG_QOI_FIELD_SYMBOL = "msg.qoi.field.symbol"; //$NON-NLS-1$
	public static final String MSG_QOI_FIELD_DESCRIPTION = "msg.qoi.field.description"; //$NON-NLS-1$
	/* Copy QoI */
	public static final String MSG_COPYQOI_PAGE_NAME = "msg.copy_qoi.page_name"; //$NON-NLS-1$
	public static final String MSG_COPYQOI_NAME_SUFFIX = "msg.copy_qoi.name_suffix"; //$NON-NLS-1$
	public static final String ERR_COPYQOI_NAME_DUPLICATED = "err.copy_qoi.name.duplicated"; //$NON-NLS-1$
	/* Qoi description dialog */
	public static final String MSG_QOIDESC_PAGE_NAME = "msg.qoi_desc.page_name"; //$NON-NLS-1$
	public static final String MSG_QOIDESC_TITLE = "msg.qoi_desc.title"; //$NON-NLS-1$
	public static final String MSG_QOITAGDESC_TITLE = "msg.qoi_tagdesc.title"; //$NON-NLS-1$

	/* Tag QoI Dialog */
	public static final String MSG_TAGQOI_PAGE_NAME = "msg.tag_qoi.page_name"; //$NON-NLS-1$
	public static final String MSG_TAGQOI_TITLE = "msg.tag_qoi.title"; //$NON-NLS-1$
	public static final String MSG_TAGQOI_DESCRIPTION = "msg.tag_qoi.description"; //$NON-NLS-1$
	public static final String MSG_TAGQOI_FIELD_QOI = "msg.tag_qoi.field.qoi"; //$NON-NLS-1$
	public static final String MSG_TAGQOI_FIELD_DESCRIPTION = "msg.tag_qoi.field.description"; //$NON-NLS-1$
	public static final String ERR_TAGQOI_QOI_NULL = "err.tag_qoi.qoi_null"; //$NON-NLS-1$
	/* Phenomenon Dialog */
	public static final String MSG_DIALOG_PHEN_PAGENAME_ADD = "msg.dialog.phen.page_name.add"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHEN_PAGENAME_EDIT = "msg.dialog.phen.page_name.edit"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHEN_PAGENAME_VIEW = "msg.dialog.phen.page_name.view"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHEN_TITLE = "msg.dialog.phen.title"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHEN_MESSAGE = "msg.dialog.phen.message"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHEN_GROUP = "msg.dialog.phen.group"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHEN_ADEQUACY = "msg.dialog.phen.adequacy"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHEN_ID = "msg.dialog.phen.id"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHEN_LABEL = "msg.dialog.phen.label"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHEN_IMPORTANCE = "msg.dialog.phen.importance"; //$NON-NLS-1$
	public static final String ERR_DIALOG_PHEN_GROUP = "err.dialog.phen.group.mandatory"; //$NON-NLS-1$
	public static final String ERR_DIALOG_PHEN_ID = "err.dialog.phen.id.mandatory"; //$NON-NLS-1$
	public static final String ERR_DIALOG_PHEN_LABEL = "err.dialog.phen.label.mandatory"; //$NON-NLS-1$
	public static final String ERR_DIALOG_PHEN_IMPORTANCE = "err.dialog.phen.importance.mandatory"; //$NON-NLS-1$
	/* Phenomenon Group Dialog */
	public static final String MSG_DIALOG_PHENGROUP_PAGENAME_ADD = "msg.dialog.phen_group.page_name.add"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHENGROUP_PAGENAME_EDIT = "msg.dialog.phen_group.page_name.edit"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHENGROUP_PAGENAME_VIEW = "msg.dialog.phen_group.page_name.view"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHENGROUP_TITLE = "msg.dialog.phen_group.title"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHENGROUP_DESCRIPTION = "msg.dialog.phen_group.description"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHENGROUP_ID = "msg.dialog.phen_group.id"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PHENGROUP_LABEL = "msg.dialog.phen_group.label"; //$NON-NLS-1$
	public static final String ERR_DIALOG_PHENGROUP_NAME_MANDATORY = "err.dialog.phen_group.label.mandatory"; //$NON-NLS-1$
	/* File Tools */
	public static final String ERR_FILETOOLS_OPENFILE_TITLE = "err.file_tools.open_file.title"; //$NON-NLS-1$
	public static final String ERR_FILETOOLS_OPENFILE_DESC = "err.file_tools.open_file.desc"; //$NON-NLS-1$
	/* PIRT Query */
	public static final String MSG_PIRT_TAB_QUERY = "msg.pirt.tab.query"; //$NON-NLS-1$
	/* Phenomena View */
	public static final String MSG_PHENOMENAVIEW_TITLE = "msg.phenomena_view.title"; //$NON-NLS-1$
	public static final String MSG_PHENOMENAVIEW_ITEMTITLE = "msg.phenomena_view.item_title"; //$NON-NLS-1$
	public static final String MSG_PHENOMENAVIEW_BTN_ADDGROUP = "msg.phenomena_view.btn.add_group"; //$NON-NLS-1$
	public static final String MSG_PHENOMENAVIEW_BTN_ADDPHENOMENON = "msg.phenomena_view.btn.add_phenomenon"; //$NON-NLS-1$
	public static final String MSG_PHENOMENAVIEW_BTN_ADDTAG_TOOLTIP = "msg.phenomena_view.btn.add_tag_tooltip"; //$NON-NLS-1$
	public static final String MSG_PHENOMENAVIEW_TAGCONFIRM_TITLE = "msg.phenomena_view.tag_confirm.title"; //$NON-NLS-1$
	public static final String MSG_PHENOMENAVIEW_TAGCONFIRM_QUESTION = "msg.phenomena_view.tag_confirm.question"; //$NON-NLS-1$
	public static final String MSG_PHENOMENAVIEW_TAGCONFIRM_SUCCESS = "msg.phenomena_view.tag_confirm.success"; //$NON-NLS-1$
	public static final String MSG_PHENOMENAVIEW_DELETECONFIRM_TITLE = "msg.phenomena_view.delete_confirm.title"; //$NON-NLS-1$
	public static final String MSG_PHENOMENAVIEW_DELETECONFIRM_QUESTIONGROUP = "msg.phenomena_view.delete_confirm.question_group"; //$NON-NLS-1$
	public static final String MSG_PHENOMENAVIEW_DELETECONFIRM_QUESTIONPHENOMENON = "msg.phenomena_view.delete_confirm.question_phenomenon"; //$NON-NLS-1$
	public static final String MSG_PHENOMENAVIEW_RESETCONFIRM_TITLE = "msg.phenomena_view.reset_confirm.title"; //$NON-NLS-1$
	public static final String MSG_PHENOMENAVIEW_RESETCONFIRM_QUESTION = "msg.phenomena_view.reset_confirm.question"; //$NON-NLS-1$
	public static final String WRN_PHENOMENAVIEW_ADDING_PHENOMENON_GROUPNOTPRESENT_TITLE = "wrn.phenomena_view.adding_phenomenon.group_not_present.title"; //$NON-NLS-1$
	public static final String WRN_PHENOMENAVIEW_ADDING_PHENOMENON_GROUPNOTPRESENT_DESC = "wrn.phenomena_view.adding_phenomenon.group_not_present.description"; //$NON-NLS-1$
	public static final String WRN_PHENOMENAVIEW_ADDING_PHENOMENON_GROUPNOTASSOCIATED_TITLE = "wrn.phenomena_view.adding_phenomenon.group_not_associated.title"; //$NON-NLS-1$
	public static final String WRN_PHENOMENAVIEW_ADDING_PHENOMENON_GROUPNOTASSOCIATED_DESC = "wrn.phenomena_view.adding_phenomenon.group_not_associated.description"; //$NON-NLS-1$
	public static final String ERR_PHENOMENAVIEW_TITLE = "err.phenomena_view.title"; //$NON-NLS-1$
	public static final String ERR_PHENOMENAVIEW_TAGGING = "err.phenomena_view.tagging"; //$NON-NLS-1$
	public static final String ERR_PHENOMENAVIEW_ADDING_PHENGROUP = "err.phenomena_view.adding.phen_group"; //$NON-NLS-1$
	public static final String ERR_PHENOMENAVIEW_ADDING_PHENOMENON = "err.phenomena_view.adding.phenomenon"; //$NON-NLS-1$
	public static final String ERR_PHENOMENAVIEW_UPDATING_PHENGROUP = "err.phenomena_view.updating.phen_group"; //$NON-NLS-1$
	public static final String ERR_PHENOMENAVIEW_UPDATING_PHENOMENON = "err.phenomena_view.updating.phenomenon"; //$NON-NLS-1$
	public static final String ERR_PHENOMENAVIEW_RESETTING = "err.phenomena_view.resetting"; //$NON-NLS-1$
	public static final String ERR_PHENOMENAVIEW_UPDATING = "err.phenomena_view.updating"; //$NON-NLS-1$
	public static final String ERR_PHENOMENAVIEW_UPDATING_QOINAME = "err.phenomena_view.updating.qoi_name"; //$NON-NLS-1$
	public static final String ERR_PHENOMENAVIEW_UPDATING_QOIHEADER = "err.phenomena_view.updating.qoi_header"; //$NON-NLS-1$
	/* QoI Home View */
	public static final String MSG_QOIHOMEVIEW_TITLE = "msg.qoi_home_view.title"; //$NON-NLS-1$
	public static final String MSG_QOIHOMEVIEW_ITEMTITLE = "msg.qoi_home_view.item_title"; //$NON-NLS-1$
	public static final String MSG_QOIHOMEVIEW_LBL_QUERY = "msg.qoi_home_view.lbl.query"; //$NON-NLS-1$
	public static final String MSG_QOIHOMEVIEW_DLG_QUERY_TITLE = "msg.qoi_home_view.dlg.query.title"; //$NON-NLS-1$
	public static final String MSG_QOIHOMEVIEW_DLG_QUERY_EMPTY = "msg.qoi_home_view.dlg.query.empty"; //$NON-NLS-1$
	public static final String MSG_QOIHOMEVIEW_DELETECONFIRM_TITLE = "msg.qoi_home_view.delete_confirm.title"; //$NON-NLS-1$
	public static final String MSG_QOIHOMEVIEW_DELETECONFIRM_MSG = "msg.qoi_home_view.delete_confirm.message"; //$NON-NLS-1$
	public static final String MSG_QOIHOMEVIEW_DELETECONFIRM_MSG_TAGSUFFIX = "msg.qoi_home_view.delete_confirm.message_tagsuffix"; //$NON-NLS-1$
	public static final String MSG_QOIHOMEVIEW_DUPLICATECONFIRM_TITLE = "msg.qoi_home_view.copy_confirm.title"; //$NON-NLS-1$
	public static final String MSG_QOIHOMEVIEW_DUPLICATECONFIRM_MSG = "msg.qoi_home_view.copy_confirm.message"; //$NON-NLS-1$
	public static final String MSG_QOIHOMEVIEW_DUPLICATECONFIRM_SUCCESS = "msg.qoi_home_view.copy_confirm.success"; //$NON-NLS-1$
	public static final String MSG_QOIHOMEVIEW_TAGCONFIRM_TITLE = "msg.qoi_home_view.tag_confirm.title"; //$NON-NLS-1$
	public static final String MSG_QOIHOMEVIEW_TAGCONFIRM_QUESTION = "msg.qoi_home_view.tag_confirm.question"; //$NON-NLS-1$
	public static final String MSG_QOIHOMEVIEW_TAGCONFIRM_SUCCESS = "msg.qoi_home_view.tag_confirm.success"; //$NON-NLS-1$
	public static final String ERR_QOIHOMEVIEW_TITLE = "err.qoi_home_view.title"; //$NON-NLS-1$
	public static final String ERR_QOIHOMEVIEW_UPDATING_HEADER = "err.qoi_home_view.updating.header"; //$NON-NLS-1$
	public static final String ERR_QOIHOMEVIEW_EXECUTING_QUERY = "err.qoi_home_view.executing_query"; //$NON-NLS-1$
	public static final String ERR_QOIHOMEVIEW_DUPLICATING = "err.qoi_home_view.duplicating"; //$NON-NLS-1$
	public static final String ERR_QOIHOMEVIEW_TAGGING_QOINULL = "err.qoi_home_view.tagging.qoi_null"; //$NON-NLS-1$
	public static final String ERR_QOIHOMEVIEW_TAGGING_NOTQOI = "err.qoi_home_view.tagging.not_qoi"; //$NON-NLS-1$
	/* QoI Tabbed View */
	public static final String MSG_QOITABBEDVIEW_QOIHOME = "msg.qoi_tabbed_view.qoi_home"; //$NON-NLS-1$
	public static final String MSG_QOITABBEDVIEW_QOINONAME = "msg.qoi_tabbed_view.qoi_no_name"; //$NON-NLS-1$
	public static final String MSG_QOITABBEDVIEW_TAB_NAME_TAGGED = "msg.qoi_tabbed_view.tab.name"; //$NON-NLS-1$

	/* PIRT Phenomena Table Header */
	public static final String TABLE_PIRT_HEADER_BAR_LABEL = "table.pirt.header.bar.label"; //$NON-NLS-1$
	public static final String TABLE_PIRT_HEADER_ROW_NAME = "table.pirt.header.row.name"; //$NON-NLS-1$
	public static final String TABLE_PIRT_HEADER_ROW_DESCRIPTION = "table.pirt.header.row.description"; //$NON-NLS-1$
	public static final String TABLE_PIRT_HEADER_ROW_CREATIONDATE = "table.pirt.header.row.creation_date"; //$NON-NLS-1$
	public static final String TABLE_PIRT_HEADER_ROW_ISTAGGED = "table.pirt.header.row.is_tagged"; //$NON-NLS-1$
	public static final String TABLE_PIRT_HEADER_ROW_TAGDATE = "table.pirt.header.row.tag_date"; //$NON-NLS-1$
	public static final String TABLE_PIRT_HEADER_ROW_TAGDESCRIPTION = "table.pirt.header.row.tag_description"; //$NON-NLS-1$
	/* PIRT Phenomena Table Phenomena */
	public static final String TABLE_PIRT_PHENOMENA_COLUMN_ID = "table.pirt.phenomena.column.id"; //$NON-NLS-1$
	public static final String TABLE_PIRT_PHENOMENA_COLUMN_PHENOMENA = "table.pirt.phenomena.column.phenomena"; //$NON-NLS-1$
	public static final String TABLE_PIRT_PHENOMENA_COLUMN_IMPORTANCE = "table.pirt.phenomena.column.importance"; //$NON-NLS-1$
	/* PIRT QoI Table Header */
	public static final String TABLE_QOI_HEADER_BAR_LABEL = "table.qoi.header.bar.label"; //$NON-NLS-1$
	public static final String TABLE_QOI_HEADER_ROW_APPLICATION = "table.qoi.header.row.application"; //$NON-NLS-1$
	public static final String TABLE_QOI_HEADER_ROW_CONTACT = "table.qoi.header.row.contact"; //$NON-NLS-1$
	/* PIRT QoI Table QoI */
	public static final String TABLE_PIRT_QOI_COLUMN_SYMBOL = "table.pirt.qoi.column.symbol"; //$NON-NLS-1$
	public static final String TABLE_PIRT_QOI_COLUMN_DESCRIPTION = "table.pirt.qoi.column.description"; //$NON-NLS-1$
	public static final String TABLE_PIRT_QOI_COLUMN_CREATIONDATE = "table.pirt.qoi.column.creation_date"; //$NON-NLS-1$
	public static final String TABLE_PIRT_QOI_COLUMN_ISTAGGED = "table.pirt.qoi.column.is_tagged"; //$NON-NLS-1$
	public static final String TABLE_PIRT_QOI_COLUMN_TAGDATE = "table.pirt.qoi.column.tag_date"; //$NON-NLS-1$
	public static final String TABLE_PIRT_QOI_COLUMN_TAGDESCRIPTION = "table.pirt.qoi.column.tag_description"; //$NON-NLS-1$

	/* Preference Page */
	public static final String PREFS_GLOBAL_DISPLAY_VERSION_NUMBER = "prefs.global.display_version_number"; //$NON-NLS-1$
	public static final String PREFS_GLOBAL_DISPLAY_VERSION_ORIGIN_NUMBER = "prefs.global.display_version_origin_number"; //$NON-NLS-1$
	public static final String PREFS_PIRT_QUERY_FILE = "prefs.pirt.query_file"; //$NON-NLS-1$
	public static final String PREFS_GLOBAL_ARG_EXECUTABLE = "prefs.global.arg_executable"; //$NON-NLS-1$
	public static final String PREFS_GLOBAL_ARG_SETENV = "prefs.global.arg_setenv"; //$NON-NLS-1$
	public static final String PREFS_GLOBAL_OPEN_LINK_BROWSER_OPTION = "prefs.global.open_link_browser_option_key"; //$NON-NLS-1$

	/* Developer Options Preference Page */
	public static final String PREFS_DEVOPTS_DESCRIPTION = "prefs.devopts.description"; //$NON-NLS-1$
	public static final String PREFS_DEVOPTS_REPORT_INLINEWORD_KEY = "prefs.devopts.report_inlineword.lbl"; //$NON-NLS-1$
	public static final String PREFS_DEVOPTS_CONCURRENCY_SUPPORT_KEY = "prefs.devopts.concurrency_support.lbl"; //$NON-NLS-1$
	public static final String PREFS_DEVOPTS_LOG_LEVEL_KEY = "prefs.devopts.log_level.lbl"; //$NON-NLS-1$

	/* PIRT Query Result View */
	public static final String MSG_PIRT_QUERYRSVIEW_TITLE = "msg.query_rs_view.title"; //$NON-NLS-1$
	public static final String MSG_PIRT_QUERYRSVIEW_ITEMTITLE = "msg.query_rs_view.item_title"; //$NON-NLS-1$
	/* PIRT Query Criteria Dialog */
	public static final String MSG_PIRT_DLG_QUERY_CRIT_TITLE = "msg.pirt.dlg_query_crit.title"; //$NON-NLS-1$
	public static final String MSG_PIRT_DLG_QUERY_CRIT_MSG = "msg.pirt.dlg_query_crit.msg"; //$NON-NLS-1$

	/**************************
	 * PCMM
	 */
	public static final String MSG_PCMM_CODENAME = "msg.pcmm.code_name"; //$NON-NLS-1$
	public static final String MSG_PCMM_PHASES_EVIDENCE = "msg.pcmm.phases.evidence"; //$NON-NLS-1$
	public static final String MSG_PCMM_PHASES_ASSESS = "msg.pcmm.phases.assess"; //$NON-NLS-1$
	public static final String MSG_PCMM_PHASES_AGGREGATE = "msg.pcmm.phases.aggregate"; //$NON-NLS-1$
	public static final String MSG_PCMM_NO_ROLE = "msg.pcmm.no_role"; //$NON-NLS-1$
	public static final String MSG_PCMM_ASSESSMENT_INCOMPLETE_MSG = "msg.pcmm.assessment_incomplete.msg"; //$NON-NLS-1$
	public static final String MSG_PCMM_ASSESSMENT_INCOMPLETE_SIMPLIFIED_MSG = "msg.pcmm.assessment_incomplete.simplified.msg"; //$NON-NLS-1$
	/* PCMM View */
	public static final String MSG_PCMMVIEW_TITLE = "msg.pcmm_view.title"; //$NON-NLS-1$
	public static final String MSG_PCMMVIEW_ITEMTITLE = "msg.pcmm_view.item_title"; //$NON-NLS-1$
	/* PCMM Home View */
	public static final String MSG_PCMMHOME_LBL_PROGRESS = "msg.pcmm_home.lbl.progress"; //$NON-NLS-1$
	public static final String MSG_PCMMHOME_BTN_AGGREGATE = "msg.pcmm_home.btn.aggregate"; //$NON-NLS-1$
	public static final String MSG_PCMMHOME_BTN_PCMMSTAMP = "msg.pcmm_home.btn.pcmm_stamp"; //$NON-NLS-1$
	public static final String MSG_PCMMHOME_DIALOG_TITLE = "msg.pcmm_home.dialog.title"; //$NON-NLS-1$
	public static final String ERR_PCMMHOME_DIALOG_LOADING_MSG = "err.pcmm_home.dialog.loading.msg"; //$NON-NLS-1$
	public static final String ERR_PCMMHOME_PREREQUISITE_TITLE = "err.pcmm_home.prerequisite.title"; //$NON-NLS-1$
	public static final String ERR_PCMMHOME_PREREQUISITE_TXT = "err.pcmm_home.prerequisite.txt"; //$NON-NLS-1$
	/* PCMM Select Role Dialog */
	public static final String MSG_PCMMSELECTROLE_PAGE_NAME = "msg.pcmm_select_role.page_name"; //$NON-NLS-1$
	public static final String MSG_PCMMSELECTROLE_TITLE = "msg.pcmm_select_role.title"; //$NON-NLS-1$
	public static final String MSG_PCMMSELECTROLE_DESCRIPTION = "msg.pcmm_select_role.description"; //$NON-NLS-1$
	public static final String ERR_PCMMSELECTROLE_ROLE_MANDATORY = "err.dialog.select_role.role.mandatory"; //$NON-NLS-1$
	public static final String MSG_PCMMSELECTROLE_ROLE = "msg.pcmm_select_role.role"; //$NON-NLS-1$
	public static final String MSG_PCMMSELECTROLE_ROLE_LABEL = "msg.pcmm_select_role.role.label"; //$NON-NLS-1$
	/* PCMM Assess View */
	public static final String MSG_PCMMASSESS_TITLE = "msg.pcmm_assess.title"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_ITEM_TITLE = "msg.pcmm_assess.item_title"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_TABLE_COL_ELMTSUBELMT = "msg.pcmm_assess.table.column.elmt_subelmt"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_TABLE_COL_LVLACHIEVED = "msg.pcmm_assess.table.column.level_achieved"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_TABLE_COL_EVIDLINKS = "msg.pcmm_assess.table.column.evidence_links"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_TABLE_COL_COMMENTS = "msg.pcmm_assess.table.column.comments"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_TABLE_COL_VIEW = "msg.pcmm_assess.table.column.view"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_TABLE_COL_EVID_LABEL_SING = "msg.pcmm_assess.table.column.evidence.lbl.singular"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_TABLE_COL_EVID_LABEL_PLUR = "msg.pcmm_assess.table.column.evidence.lbl.plural"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_BTN_EXAMINE_EVIDENCE = "msg.pcmm_assess.btn.examine_evidence"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_BTN_ASSESS = "msg.pcmm_assess.btn.assess"; //$NON-NLS-1$
	public static final String ERR_PCMMASSESS_TITLE = "err.pcmm_assess.title"; //$NON-NLS-1$
	public static final String ERR_PCMMASSESS_UPDATING = "err.pcmm_assess.updating"; //$NON-NLS-1$
	public static final String ERR_PCMMASSESS_TITLE_ROLE_MANDATORY = "err.pcmm_assess.title.role.mandatory"; //$NON-NLS-1$
	public static final String ERR_PCMMASSESS_DESC_ROLE_MANDATORY = "err.pcmm_assess.desc.role.mandatory"; //$NON-NLS-1$
	public static final String ERR_PCMMASSESS_ASSESS = "err.pcmm_assess.assess"; //$NON-NLS-1$
	public static final String ERR_PCMMASSESS_DELETE = "err.pcmm_assess.delete"; //$NON-NLS-1$
	/* PCMM Assess Dialog */
	public static final String MSG_PCMMASSESS_DIALOG_ASSESS_BTN_ASSESS = "msg.pcmm_assess.dialog.assess.btn.assess"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_DIALOG_ASSESS_PAGE_NAME = "msg.pcmm_assess.dialog.assess.page_name"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_DIALOG_ASSESS_TITLE = "msg.pcmm_assess.dialog.assess.title"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_DIALOG_ASSESS_SIMPLIFIED_TITLE = "msg.pcmm_assess.dialog.assess.simplified.title"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_DIALOG_ASSESS_SUBTITLE = "msg.pcmm_assess.dialog.assess.subtitle"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_DIALOG_ASSESS_LBL_CODE = "msg.pcmm_assess.dialog.assess.lbl.code"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_DIALOG_ASSESS_LBL_NAME = "msg.pcmm_assess.dialog.assess.lbl.name"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_DIALOG_ASSESS_SIMPLIFIED_LBL_NAME = "msg.pcmm_assess.dialog.assess.simplified.lbl.name"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_DIALOG_ASSESS_LBL_LEVELACHIEVED = "msg.pcmm_assess.dialog.assess.lbl.level_achieved"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_DIALOG_ASSESS_LBL_COMMENTS = "msg.pcmm_assess.dialog.assess.lbl.comments"; //$NON-NLS-1$
	public static final String ERR_PCMMASSESS_DIALOG_ASSESS_LEVELACHIEVED_MANDATORY = "err.pcmm_assess.dialog.assess.level_achieved.mandatory"; //$NON-NLS-1$
	public static final String ERR_PCMMASSESS_DIALOG_ASSESS_COMMENT_REQUIRED = "err.pcmm_assess.dialog.assess.comment.required"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_DIALOG_DELETE_ASSESSMENT = "msg.pcmm_assess.dialog.assess.delete_assessment"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_DIALOG_DELETE_ASSESSMENT_SIMPLIFIED = "msg.pcmm_assess.dialog.assess.delete_assessment.simplified"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_DIALOG_TITLE = "msg.pcmm_assess.assess.dialog.title"; //$NON-NLS-1$
	public static final String ERR_PCMMASSESS_DIALOG_LOADING_MSG = "err.pcmm_assess.assess.dialog.loading.msg"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_DIALOG_ASSESS_NO_EVIDENCE = "msg.pcmm_assess.dialog.assess.no_evidence"; //$NON-NLS-1$
	public static final String MSG_PCMMASSESS_DIALOG_ASSESS_NO_LEVELS = "msg.pcmm_assess.dialog.assess.no_levels"; //$NON-NLS-1$
	/* PCMM Aggregation View */
	public static final String MSG_PCMM_AGGREGATE_TITLE = "msg.pcmm.aggregate.title"; //$NON-NLS-1$
	public static final String MSG_PCMM_AGGREGATE_ITEMTITLE = "msg.pcmm.aggregate.item_title"; //$NON-NLS-1$
	public static final String MSG_PCMMAGGREG_BTN_DETAILS = "msg.pcmm_aggreg.btn.details"; //$NON-NLS-1$
	public static final String MSG_PCMMAGGREG_DETAILS_EMPTY_ROLE = "msg.pcmm_aggreg.details.empty_role"; //$NON-NLS-1$
	public static final String MSG_PCMMAGGREG_DETAILS_EMPTY_USER = "msg.pcmm_aggreg.details.empty_user"; //$NON-NLS-1$
	public static final String MSG_PCMMAGGREG_DETAILS_EMPTY_NAME = "msg.pcmm_aggreg.details.empty_name"; //$NON-NLS-1$
	public static final String MSG_PCMMAGGREG_DIALOG_TITLE = "msg.aggreg.dialog.title"; //$NON-NLS-1$
	public static final String MSG_PCMMAGGREG_FILTER_LABEL = "msg.aggreg.filter.label.msg"; //$NON-NLS-1$
	public static final String MSG_PCMMAGGREG_FILTER_ROLE_LABEL = "msg.aggreg.filterg.role.label.msg"; //$NON-NLS-1$
	public static final String ERR_PCMMAGGREG_DIALOG_LOADING_MSG = "err.aggreg.dialog.loading.msg"; //$NON-NLS-1$
	/* PCMM Aggregation Dialog */
	public static final String MSG_PCMM_DIALOG_AGGREG_TITLE = "msg.dialog.aggreg.title"; //$NON-NLS-1$
	public static final String MSG_PCMM_DIALOG_AGGREG_SUBTITLE = "msg.dialog.aggreg.subtitle"; //$NON-NLS-1$
	public static final String MSG_PCMM_DIALOG_AGGREG_LBL_COMMENTS = "msg.dialog.aggreg.lbl.comments"; //$NON-NLS-1$
	public static final String MSG_PCMM_DIALOG_AGGREG_CREATE_DIALOG_TITLE = "msg.dialog.aggreg.create_dialog.title"; //$NON-NLS-1$
	public static final String ERR_PCMM_DIALOG_AGGREG_CREATE_DIALOG_MSG = "err.dialog.aggreg.create_dialog.msg"; //$NON-NLS-1$
	/* PCMM Evidence View */
	public static final String MSG_PCMMEVID_TITLE = "msg.pcmm_evidence.title"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_ITEM_TITLE = "msg.pcmm_evidence.item_title"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_OPEN_BROWSERNAME = "msg.pcmm_evidence.open.browser_name"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_TABLE_COL_FILENAME = "msg.pcmm_evidence.table.column.filename"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_TABLE_COL_FILEPATH = "msg.pcmm_evidence.table.column.filepath"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_TABLE_COL_SECTION = "msg.pcmm_evidence.table.column.section"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_TABLE_COL_DESC = "msg.pcmm_evidence.table.column.desc"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_TABLE_COL_USER = "msg.pcmm_evidence.table.column.user"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_TABLE_COL_ROLE = "msg.pcmm_evidence.table.column.role"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_DELETE_CONFIRM_TITLE = "msg.pcmm_evidence.delete_confirm.title"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_DELETE_CONFIRM_QUESTION = "msg.pcmm_evidence.delete_confirm.question"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_MULTI_DELETE_CONFIRM_QUESTION = "msg.pcmm_evidence.multi.delete_confirm.question"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_ADD_TITLE = "msg.pcmm_evidence.add.title"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_EDIT_TITLE = "msg.pcmm_evidence.add.title"; //$NON-NLS-1$
	public static final String ERR_PCMMEVID_ADD_BADSELECT_NOTSUBELEMENT_MSG = "err.pcmm_evidence.add.bad_select.not_subelement.message"; //$NON-NLS-1$
	public static final String ERR_PCMMEVID_ADD_BADSELECT_NOTELEMENT_MSG = "err.pcmm_evidence.add.bad_select.not_element.message"; //$NON-NLS-1$
	public static final String ERR_PCMMEVID_DESC_ROLE_MANDATORY = "err.pcmm_evidence.desc.role.mandatory"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_DIALOG_TITLE = "msg.pcmm_evidence.dialog.title"; //$NON-NLS-1$
	public static final String MSG_PCMMEVID_DIALOG_LIST = "msg.pcmm_evidence.dialog.list"; //$NON-NLS-1$
	public static final String ERR_PCMMEVID_DIALOG_ADDING_MSG = "err.pcmm_evidence.dialog.adding.msg"; //$NON-NLS-1$
	public static final String ERR_PCMMEVID_DIALOG_DELETING_MSG = "err.pcmm_evidence.dialog.deleting.msg"; //$NON-NLS-1$
	public static final String ERR_PCMMEVID_DIALOG_UPDATING_MSG = "err.pcmm_evidence.dialog.updating.msg"; //$NON-NLS-1$
	public static final String ERR_PCMMEVID_DIALOG_OPENING_MSG = "err.pcmm_evidence.dialog.opening.msg"; //$NON-NLS-1$
	public static final String ERR_PCMMEVID_DIALOG_LOADING_MSG = "err.pcmm_evidence.dialog.loading.msg"; //$NON-NLS-1$

	/* PCMM Evidence Dialog */
	public static final String MSG_DIALOG_PCMMEVIDENCE_TITLE = "msg.dialog.pcmm_evidence.title"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PCMMEVIDENCE_DESCRIPTION = "msg.dialog.pcmm_evidence.description"; //$NON-NLS-1$
	public static final String MSG_DLG_ADDEVID_TITLE = "msg.pcmm_evidence.add.title"; //$NON-NLS-1$
	public static final String MSG_DLG_EDITEVID_TITLE = "msg.pcmm_evidence.edit.title"; //$NON-NLS-1$
	public static final String MSG_DLG_VIEWEVID_TITLE = "msg.pcmm_evidence.view.title"; //$NON-NLS-1$
	public static final String MSG_DLG_ADDEVID_LBL_EVID = "msg.dialog.add_evidence.lbl.evidence"; //$NON-NLS-1$
	public static final String MSG_DLG_ADDEVID_LBL_DESC = "msg.dialog.add_evidence.lbl.desc"; //$NON-NLS-1$
	public static final String MSG_DLG_ADDEVID_LBL_SECTION = "msg.dialog.add_evidence.lbl.section"; //$NON-NLS-1$
	public static final String MSG_DLG_ADDEVID_LBL_IMG_CAPTION = "msg.dialog.add_evidence.lbl.image_caption"; //$NON-NLS-1$
	public static final String MSG_DLG_ADDEVID_DESC = "msg.dialog.add_evidence.desc"; //$NON-NLS-1$
	public static final String MSG_DIALOG_PCMMEVIDENCE_LBL_REMOVEFILECHANGED = "msg.pcmm_evidence.lbl_remove_file_changed"; //$NON-NLS-1$
	public static final String ERR_DLG_ADDEVID_FILE_ALREADYEXISTS = "err.dialog.add_evidence.file.already_exists"; //$NON-NLS-1$
	public static final String ERR_DLG_ADDEVID_SECTION_ALREADYEXISTS = "err.dialog.add_evidence.section.already_exists"; //$NON-NLS-1$
	public static final String ERR_DLG_ADDEVID_URL_ALREADYEXISTS = "err.dialog.add_evidence.url.already_exists"; //$NON-NLS-1$
	public static final String ERR_DLG_ADDEVID_FILE_NOTFILE = "err.dialog.add_evidence.file.not_file"; //$NON-NLS-1$
	public static final String ERR_DLG_ADDEVID_URL_NOTVALID = "err.dialog.add_evidence.url.not_valid"; //$NON-NLS-1$

	/* PCMM Planning View */
	public static final String MSG_PCMMPLANNING_TITLE = "msg.pcmm_planning.title"; //$NON-NLS-1$
	public static final String MSG_PCMMPLANNING_ITEM_TITLE = "msg.pcmm_planning.item_title"; //$NON-NLS-1$
	public static final String MSG_PCMMPLANNING_DIALOG_TITLE = "msg.pcmm_planning.dialog.title"; //$NON-NLS-1$
	public static final String MSG_PCMMPLANNING_TABLE_BTN_ADDPARAMETER = "msg.pcmm_planning.table.btn_add_parameter"; //$NON-NLS-1$
	public static final String ERR_PCMMPLANNING_DIALOG_LOADING_MSG = "err.pcmm_planning.dialog.loading.msg"; //$NON-NLS-1$
	public static final String ERR_PCMMPLANNING_TITLE = "err.pcmm_planning.title"; //$NON-NLS-1$
	public static final String ERR_PCMMPLANNING_UPDATING = "err.pcmm_planning.updating"; //$NON-NLS-1$
	/* PCMM Stamp View */
	public static final String MSG_PCMMSTAMP_TITLE = "msg.pcmm_stamp.title"; //$NON-NLS-1$
	public static final String MSG_PCMMSTAMP_ITEMTITLE = "msg.pcmm_stamp.item_title"; //$NON-NLS-1$
	public static final String MSG_PCMMSTAMP_SERIE_NAME = "msg.pcmm_stamp.serie_name"; //$NON-NLS-1$
	public static final String MSG_PCMMSTAMP_LBL_DESCRIPTION = "msg.pcmm_stamp.lbl_description"; //$NON-NLS-1$
	public static final String MSG_PCMMSTAMP_DIALOG_TITLE = "msg.pcmm_stamp.dialog.title"; //$NON-NLS-1$
	public static final String ERR_PCMMSTAMP_DIALOG_LOADING_MSG = "err.pcmm_stamp.dialog.loading.msg"; //$NON-NLS-1$

	/* Tag Widget */
	public static final String MSG_TAG_PART_TITLE = "msg.tag.part.title"; //$NON-NLS-1$
	public static final String MSG_TAG_PART_COMBO_LABEL = "msg.tag.part.combo.label"; //$NON-NLS-1$
	public static final String MSG_TAG_PART_COMBO_DEFAULTTAG = "msg.tag.part.combo.default_tag"; //$NON-NLS-1$
	public static final String MSG_TAG_PART_BTN_NEWTAG = "msg.tag.part.btn.new_tag"; //$NON-NLS-1$
	public static final String MSG_TAG_PART_BTN_NEWTAG_TOOLTIP = "msg.tag.part.btn.new_tag_tooltip"; //$NON-NLS-1$
	public static final String MSG_TAG_PART_BTN_MANAGETAG = "msg.tag.part.btn.manage_tag"; //$NON-NLS-1$
	public static final String MSG_TAG_PART_TAG_SUCCESS = "msg.tag.part.tag.success"; //$NON-NLS-1$
	public static final String ERR_TAG_PART_UPDATING = "err.tag.part.updating"; //$NON-NLS-1$

	/* Tag Dialogs */
	public static final String MSG_TAG_DIALOG_VIEWTITLE = "msg.tag.dialog.viewtitle"; //$NON-NLS-1$
	public static final String MSG_TAG_NEWTAGDIALOG_TITLE = "msg.tag.newtagdialog.title"; //$NON-NLS-1$
	public static final String MSG_TAG_NEWTAGDIALOG_MSG = "msg.tag.newtagdialog.msg"; //$NON-NLS-1$
	public static final String MSG_TAG_NEWTAGDIALOG_INFO = "msg.tag.newtagdialog.info"; //$NON-NLS-1$
	public static final String MSG_TAG_NEWTAGDIALOG_LBL = "msg.tag.newtagdialog.lbl"; //$NON-NLS-1$
	public static final String MSG_TAG_NEWTAGDIALOG_DESC = "msg.tag.newtagdialog.desc"; //$NON-NLS-1$
	public static final String ERR_TAG_NEWTAGDIALOG_NO_NAME = "err.tag.newtagdialog.no_name"; //$NON-NLS-1$
	public static final String MSG_TAG_MGRTAGDIALOG_TITLE = "msg.tag.mgrtagdialog.title"; //$NON-NLS-1$
	public static final String MSG_TAG_MGRTAGDIALOG_MSG = "msg.tag.mgrtagdialog.msg"; //$NON-NLS-1$
	public static final String MSG_TAG_MGRTAGDIALOG_LBL = "msg.tag.mgrtagdialog.lbl"; //$NON-NLS-1$
	public static final String MSG_TAG_MGRTAGDIALOG_DATE = "msg.tag.mgrtagdialog.date"; //$NON-NLS-1$
	public static final String MSG_TAG_MGRTAGDIALOG_USER = "msg.tag.mgrtagdialog.user"; //$NON-NLS-1$
	public static final String MSG_TAG_MGRTAGDIALOG_DESC = "msg.tag.mgrtagdialog.desc"; //$NON-NLS-1$
	public static final String MSG_TAG_MGRTAGDIALOG_DELETE_CONFIRM_QUESTION = "msg.tag.mgrtagdialog.delete_confirm.question"; //$NON-NLS-1$
	public static final String MSG_TAG_MGRTAGDIALOG_TAGVIEWDIALOG_TITLE = "msg.tag.mgrtagdialog.taviewdialog.title"; //$NON-NLS-1$
	public static final String MSG_TAG_DIALOG_DELETE_SUCCESS = "msg.tag.mgrtagdialog.delete.success"; //$NON-NLS-1$
	public static final String ERR_TAG_DIALOG_TAGGING_MSG = "err.tag.dialog.tagging.msg"; //$NON-NLS-1$
	public static final String ERR_TAG_DIALOG_DELETING_MSG = "err.tag.dialog.deleting.msg"; //$NON-NLS-1$
	public static final String ERR_TAG_DIALOG_DELETING_SELECTEDTAG_MSG = "err.tag.dialog.deleting.selected_tag.msg"; //$NON-NLS-1$

	/**************************
	 * Planning
	 */
	/* Intended Purpose View */
	public static final String MSG_INTENDEDPURPOSE_TITLE = "msg.intended_purpose.title"; //$NON-NLS-1$
	public static final String MSG_INTENDEDPURPOSE_ITEM = "msg.intended_purpose.item"; //$NON-NLS-1$
	public static final String MSG_INTENDEDPURPOSE_HEADER_LBL = "msg.intended_purpose.header_lbl"; //$NON-NLS-1$
	public static final String MSG_INTENDEDPURPOSE_DESCRIPTION = "msg.intended_purpose.form.description"; //$NON-NLS-1$
	public static final String MSG_INTENDEDPURPOSE_REFERENCE = "msg.intended_purpose.form.reference"; //$NON-NLS-1$
	public static final String MSG_INTENDEDPURPOSE_BTN_BACK_TOOLTIP = "msg.intended_purpose.btn.back_tooltip"; //$NON-NLS-1$
	public static final String MSG_INTENDEDPURPOSE_BTN_CANCEL_TOOLTIP = "msg.intended_purpose.btn.cancel_tooltip"; //$NON-NLS-1$
	public static final String MSG_INTENDEDPURPOSE_BTN_EDIT_TOOLTIP = "msg.intended_purpose.btn.edit_tooltip"; //$NON-NLS-1$
	public static final String MSG_INTENDEDPURPOSE_BTN_DONE_TOOLTIP = "msg.intended_purpose.btn.done_tooltip"; //$NON-NLS-1$
	public static final String MSG_INTENDEDPURPOSE_DLG_QUIT_SAVEBEFOREEXIT = "msg.intended_purpose.dlg.quit.save_before_exit"; //$NON-NLS-1$
	public static final String MSG_INTENDEDPURPOSE_LOCKED = "msg.intended_purpose.locked"; //$NON-NLS-1$
	public static final String MSG_INTENDEDPURPOSE_LOCKED_BY = "msg.intended_purpose.locked_by"; //$NON-NLS-1$

	/* Intended Purpose View */
	public static final String ERR_INTENDEDPURPOSE_LOCK = "err.intended_purpose.lock"; //$NON-NLS-1$
	public static final String ERR_INTENDEDPURPOSE_UNLOCK = "err.intended_purpose.unlock"; //$NON-NLS-1$

	/* Uncertainty View */
	public static final String MSG_UNCERTAINTY_TITLE = "msg.uncertainty.title"; //$NON-NLS-1$
	public static final String MSG_UNCERTAINTY_BTN_ADD = "msg.uncertainty.btn_add"; //$NON-NLS-1$
	public static final String MSG_UNCERTAINTY_BTN_ADD_GROUP = "msg.uncertainty.btn_add_group"; //$NON-NLS-1$
	public static final String MSG_UNCERTAINTY_DELETECONFIRM_TITLE = "msg.uncertainty.delete_confirm.title"; //$NON-NLS-1$
	public static final String MSG_UNCERTAINTY_GROUP_DELETECONFIRM = "msg.uncertainty.group.delete_confirm"; //$NON-NLS-1$
	public static final String MSG_UNCERTAINTY_NAME = "msg.uncertainty.name"; //$NON-NLS-1$
	public static final String MSG_UNCERTAINTY_DELETECONFIRM = "msg.uncertainty.delete_confirm"; //$NON-NLS-1$
	public static final String ERR_UNCERTAINTY_NAME_MANDATORY = "err.uncertainty.name.mandatory"; //$NON-NLS-1$
	public static final String ERR_UNCERTAINTY_NAME_DUPLICATED = "err.uncertainty.name.duplicated"; //$NON-NLS-1$

	/* Uncertainty dialog */
	public static final String MSG_DIALOG_UNCERTAINTY_TITLE = "msg.dialog.uncertainty.title"; //$NON-NLS-1$
	public static final String MSG_DIALOG_UNCERTAINTY_DESCRIPTION = "msg.dialog.uncertainty.description"; //$NON-NLS-1$
	public static final String MSG_DIALOG_UNCERTAINTY_GROUP_TITLE = "msg.dialog.uncertainty_group.title"; //$NON-NLS-1$
	public static final String MSG_DIALOG_UNCERTAINTY_GROUP_DESCRIPTION = "msg.dialog.uncertainty_group.description"; //$NON-NLS-1$
	public static final String MSG_DIALOG_UNCERTAINTY_PAGENAME_ADD = "msg.dialog.uncertainty.add"; //$NON-NLS-1$
	public static final String MSG_DIALOG_UNCERTAINTY_PAGENAME_EDIT = "msg.dialog.uncertainty.edit"; //$NON-NLS-1$
	public static final String MSG_DIALOG_UNCERTAINTY_PAGENAME_VIEW = "msg.dialog.uncertainty.view"; //$NON-NLS-1$
	public static final String MSG_DIALOG_UNCERTAINTY_GROUP = "msg.dialog.uncertainty.group"; //$NON-NLS-1$
	public static final String MSG_DIALOG_UNCERTAINTY_GROUP_PAGENAME_ADD = "msg.dialog.uncertainty_group.add"; //$NON-NLS-1$
	public static final String MSG_DIALOG_UNCERTAINTY_GROUP_PAGENAME_EDIT = "msg.dialog.uncertainty_group.edit"; //$NON-NLS-1$
	public static final String MSG_DIALOG_UNCERTAINTY_GROUP_PAGENAME_VIEW = "msg.dialog.uncertainty_group.view"; //$NON-NLS-1$
	public static final String ERR_DIALOG_UNCERTAINTY_TITLE = "err.dialog.uncertainty.title"; //$NON-NLS-1$
	public static final String ERR_DIALOG_UNCERTAINTY_ADD = "err.dialog.uncertainty.add"; //$NON-NLS-1$
	public static final String ERR_DIALOG_UNCERTAINTY_GROUP = "err.dialog.uncertainty.group"; //$NON-NLS-1$

	/* Requirement View */
	public static final String MSG_SYSREQUIREMENT_TITLE = "msg.requirement.title"; //$NON-NLS-1$
	public static final String MSG_SYSREQUIREMENT_ITEM = "msg.requirement.item"; //$NON-NLS-1$
	public static final String MSG_SYSREQUIREMENT_BTN_ADD = "msg.requirement.btn_add"; //$NON-NLS-1$
	public static final String MSG_SYSREQUIREMENT_BTN_ADD_GROUP = "msg.requirement.btn_add_group"; //$NON-NLS-1$
	public static final String MSG_SYSREQUIREMENT_DELETECONFIRM_TITLE = "msg.requirement.delete_confirm.title"; //$NON-NLS-1$
	public static final String MSG_SYSREQUIREMENT_GROUP_NAME = "msg.requirement_group.name"; //$NON-NLS-1$
	public static final String MSG_SYSREQUIREMENT_STATEMENT = "msg.requirement.statement"; //$NON-NLS-1$
	public static final String MSG_SYSREQUIREMENT_GROUP_DELETECONFIRM = "msg.requirement_group.delete_confirm"; //$NON-NLS-1$
	public static final String MSG_SYSREQUIREMENT_DELETECONFIRM = "msg.requirement.delete_confirm"; //$NON-NLS-1$
	public static final String ERR_SYSREQUIREMENT_STATEMENT_MANDATORY = "err.requirement.title.mandatory"; //$NON-NLS-1$
	public static final String ERR_SYSREQUIREMENT_STATEMENT_DUPLICATED = "err.requirement.title.duplicated"; //$NON-NLS-1$

	/* Requirement group dialog */
	public static final String MSG_DIALOG_SYSREQUIREMENT_GROUP_NAME = "msg.dialog.requirement_group.name"; //$NON-NLS-1$

	/* Requirement dialog */
	public static final String MSG_DIALOG_SYSREQUIREMENT_TITLE = "msg.dialog.requirement.title"; //$NON-NLS-1$
	public static final String MSG_DIALOG_SYSREQUIREMENT_GROUP_TITLE = "msg.dialog.requirement.group.title"; //$NON-NLS-1$
	public static final String MSG_DIALOG_SYSREQUIREMENT_DESCRIPTION = "msg.dialog.requirement.description"; //$NON-NLS-1$
	public static final String MSG_DIALOG_SYSREQUIREMENT_GROUP_DESCRIPTION = "msg.dialog.requirement.group.description"; //$NON-NLS-1$
	public static final String MSG_DIALOG_SYSREQUIREMENT_PAGENAME_ADD = "msg.dialog.requirement.add"; //$NON-NLS-1$
	public static final String MSG_DIALOG_SYSREQUIREMENT_PAGENAME_EDIT = "msg.dialog.requirement.edit"; //$NON-NLS-1$
	public static final String MSG_DIALOG_SYSREQUIREMENT_PAGENAME_VIEW = "msg.dialog.requirement.view"; //$NON-NLS-1$
	public static final String MSG_DIALOG_SYSREQUIREMENT_GROUP_PAGENAME_ADD = "msg.dialog.requirement.group.add"; //$NON-NLS-1$
	public static final String MSG_DIALOG_SYSREQUIREMENT_GROUP_PAGENAME_EDIT = "msg.dialog.requirement.group.edit"; //$NON-NLS-1$
	public static final String MSG_DIALOG_SYSREQUIREMENT_GROUP_PAGENAME_VIEW = "msg.dialog.requirement.group.view"; //$NON-NLS-1$
	public static final String MSG_DIALOG_SYSREQUIREMENT_PARENT = "msg.dialog.requirement.parent"; //$NON-NLS-1$
	public static final String MSG_DIALOG_SYSREQUIREMENT_NO_PARENT = "msg.dialog.requirement.no_parent"; //$NON-NLS-1$
	public static final String ERR_DIALOG_SYSREQUIREMENT_TITLE = "err.dialog.requirement.title"; //$NON-NLS-1$
	public static final String ERR_DIALOG_SYSREQUIREMENT_ADD = "err.dialog.requirement.add"; //$NON-NLS-1$
	public static final String ERR_DIALOG_SYSREQUIREMENT_GROUP = "err.dialog.requirement.group"; //$NON-NLS-1$

	/* Quantity of Interest Planning View */
	public static final String MSG_QOIPLANNING_TITLE = "msg.qoi_planning.title"; //$NON-NLS-1$
	public static final String MSG_QOIPLANNING_ITEM = "msg.qoi_planning.item"; //$NON-NLS-1$
	public static final String MSG_QOIPLANNING_BTN_ADD = "msg.qoi_planning.btn_add"; //$NON-NLS-1$
	public static final String MSG_QOIPLANNING_DLG_TITLE = "msg.qoi_planning"; //$NON-NLS-1$

	/* Quantity of Interest Planning dialog */
	public static final String MSG_QOIPLANNING_DIALOG_TITLE = "msg.qoi_planning.dialog.title"; //$NON-NLS-1$
	public static final String MSG_QOIPLANNING_DIALOG_DESCRIPTION = "msg.qoi_planning.dialog.description"; //$NON-NLS-1$
	public static final String MSG_QOIPLANNING_DIALOG_PLANNING_SEPARATOR = "msg.qoi_planning.dialog.planning_separator"; //$NON-NLS-1$
	public static final String MSG_QOIPLANNING_DIALOG_PAGENAME_ADD = "msg.qoi_planning.dialog.pagename.add"; //$NON-NLS-1$
	public static final String MSG_QOIPLANNING_DIALOG_PAGENAME_EDIT = "msg.qoi_planning.dialog.pagename.edit"; //$NON-NLS-1$
	public static final String MSG_QOIPLANNING_DIALOG_PAGENAME_VIEW = "msg.qoi_planning.dialog.pagename.view"; //$NON-NLS-1$
	public static final String MSG_QOIPLANNING_DIALOG_PARENT = "msg.qoi_planning.dialog.parent"; //$NON-NLS-1$
	public static final String ERR_QOIPLANNING_DIALOG_TITLE = "err.qoi_planning.dialog.title"; //$NON-NLS-1$
	public static final String ERR_QOIPLANNING_DIALOG_ADD = "err.qoi_planning.dialog.add"; //$NON-NLS-1$
	public static final String ERR_QOIPLANNING_DIALOG_UPDATE = "err.qoi_planning.dialog.update"; //$NON-NLS-1$
	public static final String ERR_QOIPLANNING_DIALOG_QOI = "err.qoi_planning.dialog.qoi"; //$NON-NLS-1$

	/* Decision View */
	public static final String MSG_DECISION_TITLE = "msg.decision.title"; //$NON-NLS-1$
	public static final String MSG_DECISION_ITEM = "msg.decision.item"; //$NON-NLS-1$
	public static final String MSG_DECISION_COLUMN_TITLE = "msg.decision.column.title"; //$NON-NLS-1$
	public static final String MSG_DECISION_BTN_ADD = "msg.decision.btn_add"; //$NON-NLS-1$
	public static final String MSG_DECISION_DELETECONFIRM_TITLE = "msg.decision.delete_confirm.title"; //$NON-NLS-1$
	public static final String MSG_DECISION_DELETECONFIRM = "msg.decision.delete_confirm"; //$NON-NLS-1$
	public static final String MSG_DECISION_GROUP_DELETECONFIRM = "msg.decision.group.delete_confirm"; //$NON-NLS-1$
	public static final String ERR_ADDDECISION_TITLE_MANDATORY = "err.add_decision.title.mandatory"; //$NON-NLS-1$
	public static final String ERR_DECISION_TITLE_DUPLICATED = "err.decision.title.duplicated"; //$NON-NLS-1$

	/* Decision dialog */
	public static final String MSG_DIALOG_DECISION_TITLE = "msg.dialog.decision.title"; //$NON-NLS-1$
	public static final String MSG_DIALOG_DECISION_GROUP_TITLE = "msg.dialog.decision.group.title"; //$NON-NLS-1$
	public static final String MSG_DIALOG_DECISION_DESCRIPTION = "msg.dialog.decision.description"; //$NON-NLS-1$
	public static final String MSG_DIALOG_DECISION_GROUP_DESCRIPTION = "msg.dialog.decision.group.description"; //$NON-NLS-1$
	public static final String MSG_DIALOG_DECISION_PAGENAME_ADD = "msg.dialog.decision.add"; //$NON-NLS-1$
	public static final String MSG_DIALOG_DECISION_PAGENAME_EDIT = "msg.dialog.decision.edit"; //$NON-NLS-1$
	public static final String MSG_DIALOG_DECISION_PAGENAME_VIEW = "msg.dialog.decision.view"; //$NON-NLS-1$
	public static final String MSG_DIALOG_DECISION_GROUP_PAGENAME_ADD = "msg.dialog.decision.group.add"; //$NON-NLS-1$
	public static final String MSG_DIALOG_DECISION_GROUP_PAGENAME_EDIT = "msg.dialog.decision.group.edit"; //$NON-NLS-1$
	public static final String MSG_DIALOG_DECISION_GROUP_PAGENAME_VIEW = "msg.dialog.decision.group.view"; //$NON-NLS-1$
	public static final String MSG_DIALOG_DECISION_PARENT = "msg.dialog.decision.parent"; //$NON-NLS-1$
	public static final String MSG_DIALOG_DECISION_NO_PARENT = "msg.dialog.decision.no_parent"; //$NON-NLS-1$
	public static final String ERR_DIALOG_DECISION_TITLE = "err.dialog.decision.title"; //$NON-NLS-1$
	public static final String ERR_DIALOG_DECISION_ADD = "err.dialog.decision.add"; //$NON-NLS-1$

	/**************************
	 * Report View
	 */
	public static final String MSG_REPORTVIEW_TITLE = "msg.reportview.title"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_GENERATED = "msg.reportview.generated"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_BTN_GENERATE = "msg.reportview.btn.generate"; //$NON-NLS-1$

	public static final String MSG_REPORTVIEW_ARGSETUP_TITLE = "msg.reportview.arg_setup.title"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGSETUP_CHBOX_LOCAL_CONF = "msg.reportview.arg_setup.chbox.use_local_conf"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGSETUP_BTN_OPENPREFS = "msg.reportview.arg_setup.btn.open_prefs"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGSETUP_QUESTION_LOCAL_CONF_BYDEFAULT = "msg.reportview.arg_setup.question.use_local_conf_by_default"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGSETUP_QUESTION_NOTSETTED_OPENPREFS = "msg.reportview.arg_setup.question.notsetted_openprefs"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGSETUP_QUESTION_ASKTOPERSISTPREFS = "msg.reportview.arg_setup.question.ask_to_persist_prefs"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGSETUP_VERSION_WARN = "msg.reportview.arg_setup.version.warn"; //$NON-NLS-1$

	public static final String MSG_REPORTVIEW_ARGCONSOLE_TITLE = "msg.reportview.arg_console.title"; //$NON-NLS-1$

	public static final String MSG_REPORTVIEW_ARGPARAM_TITLE = "msg.reportview.arg_param.title"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGPARAM_OUTPUTFOLDER = "msg.reportview.arg_param.output_folder"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGPARAM_OUTPUTFOLDER_MSG = "msg.reportview.arg_param.output_folder.msg"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGPARAM_PARAMFILE = "msg.reportview.arg_param.parameters_file"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGPARAM_STRUCTFILE = "msg.reportview.arg_param.structure_file"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGPARAM_FILENAME = "msg.reportview.arg_param.filename"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGPARAM_REPORTTITLE = "msg.reportview.arg_param.report_title"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGPARAM_AUTHOR = "msg.reportview.arg_param.author"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGPARAM_BACKENDTYPE = "msg.reportview.arg_param.backend-type"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGPARAM_CHBOX_INLINE_WORD_DOC = "msg.reportview.arg_param.chbox.inline_word_doc"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_ARGPARAM_REPORTTYPE = "msg.reportview.arg_param.report_type"; //$NON-NLS-1$

	public static final String MSG_REPORTVIEW_PLANNING_TITLE = "msg.reportview.planning.title"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_PLANNING_CHECKBOX_INTENDEDPURPOSE = "msg.reportview.planning.checkbox_intended_purpose"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_PLANNING_CHECKBOX_UNCERTAINTY = "msg.reportview.planning.checkbox_uncertainty"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_PLANNING_CHECKBOX_REQUIREMENT = "msg.reportview.planning.checkbox_requirement"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_PLANNING_CHECKBOX_QOIPLANNER = "msg.reportview.planning.checkbox_qoiplanner"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_PLANNING_CHECKBOX_DECISION = "msg.reportview.planning.checkbox_decision"; //$NON-NLS-1$

	public static final String MSG_REPORTVIEW_PIRT_TITLE = "msg.reportview.pirt.title"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_PIRT_QOI_CURRENT = "msg.reportview.pirt.qoi.current"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_PIRT_QOI_LIST = "msg.reportview.pirt.qoi.list"; //$NON-NLS-1$

	public static final String MSG_REPORTVIEW_PCMM_TITLE = "msg.reportview.pcmm.title"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_PCMM_SELECT_TAG = "msg.reportview.pcmm.select_tag"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_PCMM_SELECT_CHECKBOXES = "msg.reportview.pcmm.select_checkboxes"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_PCMM_CHECKBOX_ASSESSMENT = "msg.reportview.pcmm.checkbox_assessment"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_PCMM_CHECKBOX_EVIDENCE = "msg.reportview.pcmm.checkbox_evidence"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_PCMM_CHECKBOX_PLANNING = "msg.reportview.pcmm.checkbox_planning"; //$NON-NLS-1$

	public static final String MSG_REPORTVIEW_CUSTOMENDING_TITLE = "msg.reportview.custom_ending.title"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_CUSTOMENDING_LABEL = "msg.reportview.custom_ending.label"; //$NON-NLS-1$

	public static final String MSG_REPORTVIEW_GENERATE_REPORT_PROCESSING = "msg.reportview.generate_report.processing"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_GENERATE_REPORT_JOB_TITLE = "msg.reportview.generate_report.job_title"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_GENERATE_REPORT_TASK_PARAMETERSFILE = "msg.reportview.generate_report.task_parametersfile"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_GENERATE_REPORT_TASK_PARAMETERSFILE_SUB1 = "msg.reportview.generate_report.task_parametersfile.sub1"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_GENERATE_REPORT_TASK_PARAMETERSFILE_SUB2 = "msg.reportview.generate_report.task_parametersfile.sub2"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_GENERATE_REPORT_TASK_STRUCTUREFILE = "msg.reportview.generate_report.task_structurefile"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_GENERATE_REPORT_TASK_STRUCTUREFILE_SUB1 = "msg.reportview.generate_report.task_structurefile.sub1"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_GENERATE_REPORT_TASK_STRUCTUREFILE_SUB2 = "msg.reportview.generate_report.task_structurefile.sub2"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_GENERATE_REPORT_TASK_STRUCTUREFILE_SUB3 = "msg.reportview.generate_report.task_structurefile.sub3"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_GENERATE_REPORT_TASK_DELETEPREVIOUS = "msg.reportview.generate_report.task_deleteprevious"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_GENERATE_REPORT_TASK_GENREPORT = "msg.reportview.generate_report.task_generatereport"; //$NON-NLS-1$
	public static final String MSG_REPORTVIEW_GENERATE_REPORT_TASK_OPENREPORT = "msg.reportview.generate_report.task_openreport"; //$NON-NLS-1$

	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_NULL = "err.reportview.generate_report.arg_param_null"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGSETUP_ARGEXECPATH_EMPTY = "err.reportview.generate_report.arg_setup.arg_exec_path.empty"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_PARAMETERSFILE_EMPTY = "err.reportview.generate_report.parameters_file.empty"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_PARAMETERSFILE_NOTEXIST = "err.reportview.generate_report.parameters_file.not_exists"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_PARAMETERSFILE_NOTFILE = "err.reportview.generate_report.parameters_file.not_file"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_PARAMETERSFILE_SAMEASSTRUCTUREFILE = "err.reportview.generate_report.parameters_file.same_as_structure_file"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_STRUCTUREFILE_EMPTY = "err.reportview.generate_report.structure_file.empty"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_STRUCTUREFILE_NOTEXIST = "err.reportview.generate_report.structure_file.not_exists"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_STRUCTUREFILE_NOTFILE = "err.reportview.generate_report.structure_file.not_file"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_STRUCTUREFILE_SPECIALCHARS = "err.reportview.generate_report.structure_file.special_chars"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_STRUCTUREFILE_SAMEASPARAMETERSFILE = "err.reportview.generate_report.structure_file.same_as_structure_file"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_OUTPUT_EMPTY = "err.reportview.generate_report.output.empty"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_OUTPUT_NOTEXIST = "err.reportview.generate_report.output.not_exists"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_OUTPUT_NOTDIRECTORY = "err.reportview.generate_report.output.not_directory"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_CUSTOMENDINGFILE_EMPTY = "err.reportview.generate_report.custom_ending_file.empty"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_CUSTOMENDINGFILE_NOTEXIST = "err.reportview.generate_report.custom_ending_file.not_exists"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_CUSTOMENDINGFILE_NOTFILE = "err.reportview.generate_report.custom_ending_file.not_file"; //$NON-NLS-1$

	public static final String EX_ARG_COMMAND_EXCEPTION = "ex.arg.command.exception"; //$NON-NLS-1$

	public static final String EX_CONFREPORT_COMMAND_NULL = "ex.confreport.command.null"; //$NON-NLS-1$
	public static final String EX_CONFREPORT_OPTIONS_NULL = "ex.confreport.options.null"; //$NON-NLS-1$
	public static final String EX_CONFREPORT_PRESCRIPT_FILE_NOTEXISTS = "ex.confreport.pre_script_file.not_exists"; //$NON-NLS-1$
	public static final String EX_CONFREPORT_PRESCRIPT_FILE_NOTREADABLE = "ex.confreport.pre_script_file.not_readable"; //$NON-NLS-1$
	public static final String EX_CONFREPORT_ARG_PARAM_NULL = "ex.confreport.arg_parameters.null"; //$NON-NLS-1$
	public static final String EX_CONFREPORT_TMP_SCRIPT_WRITE_ERROR = "ex.confreport.tmp_script.write_error"; //$NON-NLS-1$
	public static final String EX_CONFREPORT_YAML_PARAMETERS_FILE_NOTEXISTS = "ex.confreport.yaml_parameters_file.not_exists"; //$NON-NLS-1$
	public static final String EX_CONFREPORT_YAML_STRUCTURE_FILE_NOTEXISTS = "ex.confreport.yaml_structure_file.not_exists"; //$NON-NLS-1$
	public static final String EX_REPORTVIEW_REPORT_ALREADYOPENED = "ex.reportview.report.already_opened"; //$NON-NLS-1$
	public static final String EX_REPORTVIEW_DELETE_REPORT = "ex.reportview.report.delete_error"; //$NON-NLS-1$
	public static final String EX_REPORTVIEW_OPEN_REPORT = "ex.reportview.report.not_exists"; //$NON-NLS-1$
	public static final String ERR_REPORTVIEW_ARG_PATH = "err.reportview.report.arg.path"; //$NON-NLS-1$

	/**************************
	 * Guidance view
	 */
	/* Guidance View */
	public static final String MSG_GUIDANCEVIEW_WELCOME_INFO = "msg.guidance_view.welcome_info"; //$NON-NLS-1$

	/* PIRT Ranking Guidance View */
	public static final String MSG_GUIDANCEVIEW_PIRT_LEVEL_SUBTITLE = "msg.guidance_view.pirt.level.subtitle"; //$NON-NLS-1$
	public static final String MSG_GUIDANCEVIEW_PIRT_LEVEL_DIFFERENCECOLOR_SUBTITLE = "msg.guidance_view.pirt.level_diff_colors.subtitle"; //$NON-NLS-1$
	public static final String MSG_GUIDANCEVIEW_PIRT_TABLELEVEL_COL_ID = "msg.guidance_view.pirt.table_level.column.id"; //$NON-NLS-1$
	public static final String MSG_GUIDANCEVIEW_PIRT_TABLELEVEL_COL_NAME = "msg.guidance_view.pirt.table_level.column.name"; //$NON-NLS-1$
	public static final String MSG_GUIDANCEVIEW_PIRT_TABLELEVEL_COL_LABEL = "msg.guidance_view.pirt.table_level.column.label"; //$NON-NLS-1$
	public static final String MSG_GUIDANCEVIEW_PIRT_TREELEVEL_COL_LEVELNAME = "msg.guidance_view.pirt.tree_level.column.level_name"; //$NON-NLS-1$
	public static final String MSG_GUIDANCEVIEW_PIRT_TREELEVEL_COL_LEVELDESC = "msg.guidance_view.pirt.tree_level.column.level_description"; //$NON-NLS-1$
	public static final String MSG_GUIDANCEVIEW_PIRT_TABLELEVELDIFFCOLOR_COL_DIFF = "msg.guidance_view.pirt.table_level_diff_color.column.diff"; //$NON-NLS-1$
	public static final String MSG_GUIDANCEVIEW_PIRT_TABLELEVELDIFFCOLOR_COL_COLOR = "msg.guidance_view.pirt.table_level_diff_color.column.color"; //$NON-NLS-1$
	public static final String MSG_GUIDANCEVIEW_PIRT_WARN_NODATA = "msg.guidance_view.pirt.warn.no_data"; //$NON-NLS-1$

	/* PCMM Level Guidance View */
	public static final String MSG_GUIDANCEVIEW_PCMM_SUBTITLE = "msg.guidance_view.pcmm.subtitle"; //$NON-NLS-1$
	public static final String MSG_GUIDANCEVIEW_PCMM_TABLE_COL_NAME = "msg.guidance_view.pcmm.table.column.name"; //$NON-NLS-1$
	public static final String MSG_GUIDANCEVIEW_PCMM_WARN_NODATA = "msg.guidance_view.pcmm.warn.no_data"; //$NON-NLS-1$

	/**************************
	 * Notifications
	 */
	public static final String NOTIFICATION_PCMM_EVIDENCE_ERR_FILE_NOT_EXISTS = "notification.pcmm.evidence.err.file_not_exists"; //$NON-NLS-1$
	public static final String NOTIFICATION_PCMM_EVIDENCE_WARN_DUPLICATE_PATH = "notification.pcmm.evidence.warn.duplicate_path"; //$NON-NLS-1$
	public static final String NOTIFICATION_PCMM_EVIDENCE_WARN_DUPLICATE_PATH_NOSECTION = "notification.pcmm.evidence.warn.duplicate_path.no_section"; //$NON-NLS-1$
	public static final String NOTIFICATION_PCMM_EVIDENCE_WARN_UPDATED_FILE = "notification.pcmm.evidence.warn.updated_file"; //$NON-NLS-1$

	/**************************
	 * ARG Report
	 */
	public static final String MSG_ARG_REPORT_PLANNING_INTENDEDPURPOSE_TITLE = "msg.arg.report.intended_purpose.title"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PLANNING_INTENDEDPURPOSE_STRING = "msg.arg.report.intended_purpose.string"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PLANNING_INTENDEDPURPOSE_REFERENCE_CAPTION = "msg.arg.report.intended_purpose.reference.caption"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PLANNING_REQUIREMENT_TITLE = "msg.arg.report.planning_requirement.title"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PLANNING_REQUIREMENT_STRING = "msg.arg.report.planning_requirement.string"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PLANNING_QOIPLANNER_TITLE = "msg.arg.report.planning_qoiplanner.title"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PLANNING_QOIPLANNER_STRING = "msg.arg.report.planning_qoiplanner.string"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PLANNING_UNCERTAINTY_TITLE = "msg.arg.report.planning_uncertainty.title"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PLANNING_UNCERTAINTY_STRING = "msg.arg.report.planning_uncertainty.string"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PLANNING_DECISION_TITLE = "msg.arg.report.planning_decision.title"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PLANNING_DECISION_STRING = "msg.arg.report.planning_decision.string"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PIRT_TITLE = "msg.arg.report.pirt.title"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PIRT_TABLE_CAPTION = "msg.arg.report.pirt.table.caption"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PIRT_STRING = "msg.arg.report.pirt.string"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PIRT_PIRTTABLE_TITLE = "msg.arg.report.pirt.pirt_table.title"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_TITLE = "msg.arg.report.pcmm.title"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_STRING = "msg.arg.report.pcmm.string"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_PLANNING_TITLE = "msg.arg.report.pcmm.planning_title"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_PLANNING_STRING = "msg.arg.report.pcmm.planning_string"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_EVIDENCE_TITLE = "msg.arg.report.pcmm.evidence_title"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_EVIDENCE_STRING = "msg.arg.report.pcmm.evidence_string"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_ASSESSMENT_TITLE = "msg.arg.report.pcmm.assessment_title"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_ASSESSMENT_STRING = "msg.arg.report.pcmm.assessment_string"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_LAST_UPDATED = "msg.arg.report.last_updated"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_EVIDENCE_VALUE_CREATED_AT = "msg.arg.report.pcmm.evidence.value.created_at"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_EVIDENCE_VALUE_UPDATED_AT = "msg.arg.report.pcmm.evidence.value.updated_at"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_EVIDENCE_VALUE_NAME = "msg.arg.report.pcmm.evidence.value.name"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_EVIDENCE_VALUE_DESCRIPTION = "msg.arg.report.pcmm.evidence.value.description"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_EVIDENCE_VALUE_SECTION = "msg.arg.report.pcmm.evidence.value.section"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_EVIDENCE_VALUE_USER = "msg.arg.report.pcmm.evidence.value.user"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_EVIDENCE_VALUE_ROLE = "msg.arg.report.pcmm.evidence.value.role"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_EVIDENCE_VALUE_PATH_FILE = "msg.arg.report.pcmm.evidence.value.path_file"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_EVIDENCE_VALUE_PATH_URL = "msg.arg.report.pcmm.evidence.value.path_url"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_EVIDENCE_CAPTION_LBL = "msg.arg.report.pcmm.evidence.caption.label"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_ASSESSMENT_VALUE_CREATED_AT = "msg.arg.report.pcmm.assessment.value.created_at"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_ASSESSMENT_VALUE_UPDATED_AT = "msg.arg.report.pcmm.assessment.value.updated_at"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_ASSESSMENT_VALUE_COMMENT = "msg.arg.report.pcmm.assessment.value.comment"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_ASSESSMENT_VALUE_USER = "msg.arg.report.pcmm.assessment.value.user"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_ASSESSMENT_VALUE_ROLE = "msg.arg.report.pcmm.assessment.value.role"; //$NON-NLS-1$
	public static final String MSG_ARG_REPORT_PCMM_ASSESSMENT_VALUE_LEVEL = "msg.arg.report.pcmm.assessment.value.level"; //$NON-NLS-1$

	/************************
	 * End of Message Keys
	 ************************/

	/**
	 * Do not instantiate this.
	 */
	private RscConst() {
	}
}
