/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.exports;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.decision.YmlWriterDecisionSchema;
import gov.sandia.cf.application.pcmm.YmlWriterPCMMSchema;
import gov.sandia.cf.application.pirt.YmlWriterPIRTSchema;
import gov.sandia.cf.application.qoiplanning.YmlWriterQoIPlanningSchema;
import gov.sandia.cf.application.requirement.YmlWriterSystemRequirementSchema;
import gov.sandia.cf.application.uncertainty.YmlWriterUncertaintySchema;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.constants.configuration.YmlGlobalData;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionSelectValue;
import gov.sandia.cf.model.IEntity;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningSelectValue;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMPlanningTableValue;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningSelectValue;
import gov.sandia.cf.model.QoIPlanningValue;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementSelectValue;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.DecisionSpecification;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.model.dto.configuration.QoIPlanningSpecification;
import gov.sandia.cf.model.dto.configuration.SystemRequirementSpecification;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;
import gov.sandia.cf.model.dto.yml.YmlAllDataDto;

/**
 * Import Application manager for methods that are specific to the export.
 * 
 * @author Didier Verstraete
 * 
 */
public class ExportApplication extends AApplication implements IExportApplication {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ExportApplication.class);

	/**
	 * Constructor
	 */
	public ExportApplication() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ExportApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exportDecisionSchema(final File schemaFile, final DecisionSpecification specification)
			throws CredibilityException, IOException {
		logger.debug("Export Decision schema"); //$NON-NLS-1$
		boolean append = false;
		YmlWriterDecisionSchema writer = new YmlWriterDecisionSchema();
		writer.writeSchema(schemaFile, specification, false, append);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exportQoIPlanningSchema(final File schemaFile, final QoIPlanningSpecification specification)
			throws CredibilityException, IOException {
		logger.debug("Export QoI Planning schema"); //$NON-NLS-1$
		boolean append = false;
		YmlWriterQoIPlanningSchema writer = new YmlWriterQoIPlanningSchema();
		writer.writeSchema(schemaFile, specification, false, append);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exportPIRTSchema(final File schemaFile, final PIRTSpecification specification)
			throws CredibilityException, IOException {
		logger.debug("Export PIRT schema"); //$NON-NLS-1$
		boolean append = false;
		YmlWriterPIRTSchema writer = new YmlWriterPIRTSchema();
		writer.writeSchema(schemaFile, specification, false, append);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exportPCMMSchema(final File schemaFile, final PCMMSpecification specification)
			throws CredibilityException, IOException {
		logger.debug("Export PCMM schema"); //$NON-NLS-1$
		boolean append = false;
		YmlWriterPCMMSchema writer = new YmlWriterPCMMSchema();
		writer.writeSchema(schemaFile, specification, false, append);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exportUncertaintySchema(final File schemaFile, final UncertaintySpecification specification)
			throws CredibilityException, IOException {
		logger.debug("Export Uncertainty schema"); //$NON-NLS-1$
		boolean append = false;
		boolean withIds = false;
		YmlWriterUncertaintySchema writer = new YmlWriterUncertaintySchema();
		writer.writeSchema(schemaFile, specification, withIds, append);
	}

	@Override
	public void exportSysRequirementsSchema(File schemaFile, SystemRequirementSpecification specification)
			throws CredibilityException, IOException {
		logger.debug("Export System Requirements schema"); //$NON-NLS-1$
		boolean append = false;
		boolean withIds = false;
		YmlWriterSystemRequirementSchema writer = new YmlWriterSystemRequirementSchema();
		writer.writeSchema(schemaFile, specification, withIds, append);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exportData(final File schemaFile, final Map<ExportOptions, Object> exportOptions)
			throws CredibilityException, IOException {

		// YML options and engine for further use (export with snakeyml)
		DumperOptions ymlOptions = new DumperOptions();
		ymlOptions.setIndent(2);
		ymlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(prepareYmlRepresenter(), ymlOptions);

		// erase the content
		boolean append = false;

		logger.debug("Export Global data"); //$NON-NLS-1$
		YmlWriterAllData writerAllData = new YmlWriterAllData();
		writerAllData.writeData(yaml, schemaFile, exportOptions, append);
	}

	/**
	 * Prepare yml representer.
	 *
	 * @return the representer
	 */
	private Representer prepareYmlRepresenter() {

		// create representer
		Representer representer = new Representer() {
			@Override
			protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue,
					Tag customTag) {
				// if value of property is null, ignore it.
				if (propertyValue == null) {
					return null;
				}
				// if property is an id, ignore it.
				else if (javaBean instanceof IEntity<?, ?> && property.getName().equals(YmlGlobalData.CONF_GLB_ID)) {
					return null;
				} else {
					return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
				}
			}

		};

		// add entities as defined tags
		representer.addClassTag(YmlAllDataDto.class, Tag.MAP);

		representer.addClassTag(Model.class, Tag.MAP);
		representer.addClassTag(Role.class, Tag.MAP);
		representer.addClassTag(User.class, Tag.MAP);

		representer.addClassTag(IntendedPurpose.class, Tag.MAP);

		representer.addClassTag(QoIPlanningParam.class, Tag.MAP);
		representer.addClassTag(QoIPlanningSelectValue.class, Tag.MAP);
		representer.addClassTag(QoIPlanningValue.class, Tag.MAP);

		representer.addClassTag(UncertaintyParam.class, Tag.MAP);
		representer.addClassTag(UncertaintySelectValue.class, Tag.MAP);
		representer.addClassTag(Uncertainty.class, Tag.MAP);

		representer.addClassTag(SystemRequirementParam.class, Tag.MAP);
		representer.addClassTag(SystemRequirementSelectValue.class, Tag.MAP);
		representer.addClassTag(SystemRequirement.class, Tag.MAP);

		representer.addClassTag(DecisionParam.class, Tag.MAP);
		representer.addClassTag(DecisionSelectValue.class, Tag.MAP);
		representer.addClassTag(Decision.class, Tag.MAP);

		representer.addClassTag(QuantityOfInterest.class, Tag.MAP);

		representer.addClassTag(PCMMElement.class, Tag.MAP);
		representer.addClassTag(PCMMPlanningParam.class, Tag.MAP);
		representer.addClassTag(PCMMPlanningSelectValue.class, Tag.MAP);
		representer.addClassTag(PCMMPlanningQuestion.class, Tag.MAP);

		representer.addClassTag(PCMMAssessment.class, Tag.MAP);
		representer.addClassTag(PCMMEvidence.class, Tag.MAP);
		representer.addClassTag(PCMMPlanningValue.class, Tag.MAP);
		representer.addClassTag(PCMMPlanningQuestionValue.class, Tag.MAP);
		representer.addClassTag(PCMMPlanningTableItem.class, Tag.MAP);
		representer.addClassTag(PCMMPlanningTableValue.class, Tag.MAP);

		return representer;
	}

}
