/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.arg;

import org.eclipse.core.runtime.IPath;

import gov.sandia.cf.constants.CFVariable;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscTools;

/**
 * The ARG Parameters Factory.
 * 
 * @author Didier Verstraete
 *
 */
public class ARGParametersFactory {

	/**
	 * The default python command
	 */
	public static final String PYTHON_EXEC_DEFAULT = "python"; //$NON-NLS-1$
	/**
	 * The default report name
	 */
	public static final String CF_REPORT_DEFAULT_NAME = CFVariable.CF_FILENAME.get() + "-Report"; //$NON-NLS-1$
	/**
	 * The default report number
	 */
	public static final String CF_REPORT_DEFAULT_NUMBER = "-1.0.0"; //$NON-NLS-1$
	/**
	 * The default parameters file name
	 */
	public static final String CF_REPORT_DEFAULT_PARAMETERS_FILE = "parameters.yml"; //$NON-NLS-1$
	/**
	 * The default structure file name
	 */
	public static final String CF_REPORT_DEFAULT_STRUCTURE_FILE = "structure.yml"; //$NON-NLS-1$
	/**
	 * The default report title
	 */
	public static final String CF_REPORT_DEFAULT_TITLE = CFVariable.CF_FILENAME.get() + " Report"; //$NON-NLS-1$
	/**
	 * The default author
	 */
	public static final String CF_REPORT_DEFAULT_AUTHOR = RscTools.empty();

	private ARGParametersFactory() {
		// do not use it
	}

	/**
	 * @param cfProjectPath the cf project path
	 * @return the default ARG parameters
	 */
	public static ARGParameters getDefaultParameters(final IPath cfProjectPath) {

		String cfProjectPathString = CFVariable.WORKSPACE.get();
		if (cfProjectPath != null) {
			cfProjectPathString = FileTools.append(CFVariable.WORKSPACE.get(), cfProjectPath.toString());
		}
		ARGParameters parameters = new ARGParameters();

		// ARG Setup
		parameters.setUseArgLocalConf(false);
		parameters.setPythonExecPath(PYTHON_EXEC_DEFAULT);
		parameters.setArgExecPath(RscTools.empty());
		parameters.setArgPreScript(RscTools.empty());

		// ARG Parameters
		parameters.setBackendType(ARGBackendDefault.WORD.getBackend());
		parameters.setInlineWordDoc(true);
		parameters.setFilename(CF_REPORT_DEFAULT_NAME);
		parameters.setNumber(CF_REPORT_DEFAULT_NUMBER);
		parameters.setOutput(cfProjectPathString);
		parameters.setReportType(ARGReportTypeDefault.REPORT.getType());
		parameters.setParametersFilePath(FileTools.append(cfProjectPathString, CF_REPORT_DEFAULT_PARAMETERS_FILE));
		parameters.setStructureFilePath(FileTools.append(cfProjectPathString, CF_REPORT_DEFAULT_STRUCTURE_FILE));
		parameters.setTitle(CF_REPORT_DEFAULT_TITLE);
		parameters.setAuthor(CF_REPORT_DEFAULT_AUTHOR);

		// default options
		parameters.setPlanningEnabled(true);
		parameters.setPlanningIntendedPurposeEnabled(true);
		parameters.setPlanningSysReqEnabled(true);
		parameters.setPlanningQoIPlannerEnabled(null);
		parameters.setPlanningUncertaintyEnabled(true);
		parameters.setPlanningDecisionEnabled(true);

		parameters.setPirtEnabled(true);

		parameters.setPcmmEnabled(true);
		parameters.setPcmmPlanningEnabled(true);
		parameters.setPcmmEvidenceEnabled(true);
		parameters.setPcmmAssessmentEnabled(true);

		parameters.setCustomEndingEnabled(false);
		parameters.setCustomEndingFilePath(RscTools.empty());

		return parameters;
	}
}
