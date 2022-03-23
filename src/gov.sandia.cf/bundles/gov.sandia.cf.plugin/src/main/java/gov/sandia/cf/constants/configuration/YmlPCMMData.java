/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.constants.configuration;

/**
 * This class contains the PCMM data constants.
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public class YmlPCMMData {
	/**
	 * PCMM attribute
	 */
	public static final String CONF_PCMM = "PCMM"; //$NON-NLS-1$
	public static final String CONF_DATA = "Data"; //$NON-NLS-1$

	/**
	 * Root attributes
	 */
	public static final String CONF_PCMM_TAG = "Tag"; //$NON-NLS-1$
	public static final String CONF_PCMM_PLANNING = "Planning"; //$NON-NLS-1$
	public static final String CONF_PCMM_EVIDENCE = "Evidence"; //$NON-NLS-1$
	public static final String CONF_PCMM_ASSESSMENT = "Assessment"; //$NON-NLS-1$

	/**
	 * Tag attributes
	 */
	public static final String CONF_PCMM_TAG_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_PCMM_TAG_TAGDATE = "TagDate"; //$NON-NLS-1$
	public static final String CONF_PCMM_TAG_NAME = "Name"; //$NON-NLS-1$
	public static final String CONF_PCMM_TAG_DESCRIPTION = "Description"; //$NON-NLS-1$
	public static final String CONF_PCMM_TAG_TAGUSER = "User"; //$NON-NLS-1$

	/**
	 * Planning attributes
	 */
	public static final String CONF_PLANNING_QUESTION_VALUES = "PlanningQuestionValues"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAMETER_VALUES = "PlanningParameterValues"; //$NON-NLS-1$
	public static final String CONF_PLANNING_TABLE_ITEMS = "PlanningTableItems"; //$NON-NLS-1$
	/** Question values */
	public static final String CONF_PLANNING_QUESTION_VALUE_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_PLANNING_QUESTION_VALUE_VALUE = "Value"; //$NON-NLS-1$
	public static final String CONF_PLANNING_QUESTION_VALUE_QUESTION = "Question"; //$NON-NLS-1$
	public static final String CONF_PLANNING_QUESTION_VALUE_CREATIONDATE = "CreationDate"; //$NON-NLS-1$
	public static final String CONF_PLANNING_QUESTION_VALUE_CREATIONUSER = "CreationUser"; //$NON-NLS-1$
	public static final String CONF_PLANNING_QUESTION_VALUE_UPDATEDATE = "UpdateDate"; //$NON-NLS-1$
	public static final String CONF_PLANNING_QUESTION_VALUE_UPDATEUSER = "UpdateUser"; //$NON-NLS-1$
	public static final String CONF_PLANNING_QUESTION_VALUE_TAG = "Tag"; //$NON-NLS-1$
	/** Parameter values */
	public static final String CONF_PLANNING_PARAM_VALUE_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_VALUE_VALUE = "Value"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_VALUE_PARAMETER = "Parameter"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_VALUE_ELEMENT = "Element"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_VALUE_SUBELEMENT = "Subelement"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_VALUE_CREATIONDATE = "CreationDate"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_VALUE_CREATIONUSER = "CreationUser"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_VALUE_UPDATEDATE = "UpdateDate"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_VALUE_UPDATEUSER = "UpdateUser"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_VALUE_TAG = "Tag"; //$NON-NLS-1$
	/** Parameter table items */
	public static final String CONF_PLANNING_PARAM_TABLE_ITEM_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_ITEM_PARAMETER = "Parameter"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_ITEM_ELEMENT = "Element"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_ITEM_SUBELEMENT = "Subelement"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_ITEM_VALUES = "Values"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_ITEM_CREATIONDATE = "CreationDate"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_ITEM_CREATIONUSER = "CreationUser"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_ITEM_UPDATEDATE = "UpdateDate"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_ITEM_UPDATEUSER = "UpdateUser"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_ITEM_TAG = "Tag"; //$NON-NLS-1$
	/** Parameter table values */
	public static final String CONF_PLANNING_PARAM_TABLE_VALUE_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_VALUE_VALUE = "Value"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_VALUE_PARAMETER = "Parameter"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_VALUE_TABLEITEM = "TableItem"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_VALUE_CREATIONDATE = "CreationDate"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_VALUE_CREATIONUSER = "CreationUser"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_VALUE_UPDATEDATE = "UpdateDate"; //$NON-NLS-1$
	public static final String CONF_PLANNING_PARAM_TABLE_VALUE_UPDATEUSER = "UpdateUser"; //$NON-NLS-1$

	/**
	 * Evidence attributes
	 */
	public static final String CONF_PCMM_EVIDENCE_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_PCMM_EVIDENCE_PATH = "Path"; //$NON-NLS-1$
	public static final String CONF_PCMM_EVIDENCE_NAME = "Name"; //$NON-NLS-1$
	public static final String CONF_PCMM_EVIDENCE_DESC = "Description"; //$NON-NLS-1$
	public static final String CONF_PCMM_EVIDENCE_ELEMENT = "Element"; //$NON-NLS-1$
	public static final String CONF_PCMM_EVIDENCE_SUBELEMENT = "Subelement"; //$NON-NLS-1$
	public static final String CONF_PCMM_EVIDENCE_DATEFILE = "DateFile"; //$NON-NLS-1$
	public static final String CONF_PCMM_EVIDENCE_CREATIONDATE = "CreationDate"; //$NON-NLS-1$
	public static final String CONF_PCMM_EVIDENCE_UPDATEDATE = "UpdateDate"; //$NON-NLS-1$
	public static final String CONF_PCMM_EVIDENCE_TYPE = "Type"; //$NON-NLS-1$
	public static final String CONF_PCMM_EVIDENCE_USER = "User"; //$NON-NLS-1$
	public static final String CONF_PCMM_EVIDENCE_ROLE = "Role"; //$NON-NLS-1$
	public static final String CONF_PCMM_EVIDENCE_TAG = "Tag"; //$NON-NLS-1$

	/**
	 * Assessment attributes
	 */
	public static final String CONF_PCMM_ASSESSMENT_ASSESSMENT = "Assessment"; //$NON-NLS-1$
	public static final String CONF_PCMM_ASSESSMENT_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_PCMM_ASSESSMENT_COMMENT = "Comment"; //$NON-NLS-1$
	public static final String CONF_PCMM_ASSESSMENT_ELEMENT = "Element"; //$NON-NLS-1$
	public static final String CONF_PCMM_ASSESSMENT_SUBELEMENT = "Subelement"; //$NON-NLS-1$
	public static final String CONF_PCMM_ASSESSMENT_CREATIONDATE = "CreationDate"; //$NON-NLS-1$
	public static final String CONF_PCMM_ASSESSMENT_UPDATEDATE = "UpdateDate"; //$NON-NLS-1$
	public static final String CONF_PCMM_ASSESSMENT_LEVEL = "Level"; //$NON-NLS-1$
	public static final String CONF_PCMM_ASSESSMENT_USER = "User"; //$NON-NLS-1$
	public static final String CONF_PCMM_ASSESSMENT_ROLE = "Role"; //$NON-NLS-1$
	public static final String CONF_PCMM_ASSESSMENT_TAG = "Tag"; //$NON-NLS-1$

	private YmlPCMMData() {
		// Do not instantiate
	}
}
