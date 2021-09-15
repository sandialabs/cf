/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.ExportOptions;
import gov.sandia.cf.application.configuration.arg.YmlARGStructure;
import gov.sandia.cf.application.configuration.pirt.PIRTSpecification;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PIRTTreeAdequacyColumnType;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 *
 * JUnit test class for the ARG Report PIRT Application Controller
 * 
 * @author Didier Verstraete
 */
@RunWith(JUnitPlatform.class)
class ReportARGPIRTAppTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ReportARGPIRTAppTest.class);

	@Test
	void test_generateStructurePIRT_Empty() throws CredibilityException {

		// construct
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PIRT_INCLUDE, true);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPIRTApp.class).generateStructurePIRT(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PIRT_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PIRT_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertTrue(((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).isEmpty());
	}

	@Test
	void test_generateStructurePIRT_Working_WIth_QoIHeader_QoIPlanning_Phenomenon_Criterion()
			throws CredibilityException, URISyntaxException, IOException {

		// construct database
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml")); //$NON-NLS-1$
		getAppManager().getService(IImportPIRTApp.class).importPIRTSpecification(model, confFile);
		PIRTSpecification pirtSpecs = getAppManager().getService(IPIRTApplication.class).loadPIRTConfiguration(model);
		QuantityOfInterest newQoI = TestEntityFactory.getNewQoI(getDaoManager(), model);
		QoIPlanningParam newQoIPlanningParam = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewQoIPlanningValue(getDaoManager(), newQoIPlanningParam, newQoI, null);
		PhenomenonGroup newPhenomenonGroup = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), newQoI);
		Phenomenon newPhenomenon = TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup,
				pirtSpecs.getLevelsListSortedByLevelDescending().get(0).getIdLabel());
		TestEntityFactory.getNewCriterion(getDaoManager(), newPhenomenon, "Math. Model Formulation", //$NON-NLS-1$
				PIRTTreeAdequacyColumnType.LEVELS.getType(),
				pirtSpecs.getLevelsListSortedByLevelDescending().get(0).getIdLabel());
		TestEntityFactory.getNewQoIHeader(getDaoManager(), newQoI, null);
		TestEntityFactory.getNewPIRTDescriptionHeader(getDaoManager());
		getAppManager().getService(IPIRTApplication.class).refresh(newQoI);
		QuantityOfInterest newQoI2 = TestEntityFactory.getNewQoI(getDaoManager(), model);
		PhenomenonGroup newPhenomenonGroup2 = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), newQoI2);
		Phenomenon newPhenomenon2 = TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup2,
				pirtSpecs.getLevelsListSortedByLevelDescending().get(0).getIdLabel());
		TestEntityFactory.getNewCriterion(getDaoManager(), newPhenomenon2, "Comments", //$NON-NLS-1$
				PIRTTreeAdequacyColumnType.RICH_TEXT.getType(), "My long comment.<br>On <g>two</g> lines."); //$NON-NLS-1$
		getAppManager().getService(IPIRTApplication.class).refresh(newQoI2);

		// construct options
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.MODEL, model);
		options.put(ExportOptions.PIRT_SPECIFICATION, pirtSpecs);
		options.put(ExportOptions.PIRT_QOI_LIST, new HashMap<QuantityOfInterest, Map<ExportOptions, Object>>() {
			private static final long serialVersionUID = 1L;
			{
				put(newQoI, new HashMap<ExportOptions, Object>() {
					private static final long serialVersionUID = 1L;
					{
						put(ExportOptions.PIRT_QOI_INCLUDE, true);
						put(ExportOptions.PIRT_QOI_TAG, newQoI);
					}
				});
				put(newQoI2, new HashMap<ExportOptions, Object>() {
					private static final long serialVersionUID = 1L;
					{
						put(ExportOptions.PIRT_QOI_INCLUDE, true);
						put(ExportOptions.PIRT_QOI_TAG, newQoI2);
					}
				});
			}
		});
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPIRTApp.class).generateStructurePIRT(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PIRT_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PIRT_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(3, ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
	}

}
