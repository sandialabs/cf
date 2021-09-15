/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IExportApplication;
import gov.sandia.cf.application.configuration.ExportOptions;
import gov.sandia.cf.application.configuration.YmlWriterGlobalData;
import gov.sandia.cf.application.configuration.decision.DecisionSpecification;
import gov.sandia.cf.application.configuration.decision.YmlWriterDecisionSchema;
import gov.sandia.cf.application.configuration.pcmm.PCMMSpecification;
import gov.sandia.cf.application.configuration.pcmm.YmlWriterPCMMData;
import gov.sandia.cf.application.configuration.pcmm.YmlWriterPCMMSchema;
import gov.sandia.cf.application.configuration.pirt.PIRTSpecification;
import gov.sandia.cf.application.configuration.pirt.YmlWriterPIRTData;
import gov.sandia.cf.application.configuration.pirt.YmlWriterPIRTSchema;
import gov.sandia.cf.application.configuration.qoiplanning.QoIPlanningSpecification;
import gov.sandia.cf.application.configuration.qoiplanning.YmlWriterQoIPlanningSchema;
import gov.sandia.cf.application.configuration.requirement.SystemRequirementSpecification;
import gov.sandia.cf.application.configuration.requirement.YmlWriterSystemRequirementSchema;
import gov.sandia.cf.application.configuration.uncertainty.UncertaintySpecification;
import gov.sandia.cf.application.configuration.uncertainty.YmlWriterUncertaintyData;
import gov.sandia.cf.application.configuration.uncertainty.YmlWriterUncertaintySchema;
import gov.sandia.cf.exceptions.CredibilityException;

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
	public void exportData(final File schemaFile, final Map<ExportOptions, Object> options,
			final PCMMSpecification pcmmSpecification, final UncertaintySpecification specification)
			throws CredibilityException, IOException {

		// erase the content
		boolean append = false;

		logger.debug("Export Global data"); //$NON-NLS-1$
		YmlWriterGlobalData writerGlobal = new YmlWriterGlobalData();
		writerGlobal.writeGlobalData(schemaFile, options, append);

		// append to the previous content
		append = true;

		logger.debug("Export PIRT data"); //$NON-NLS-1$
		YmlWriterPIRTData writerPIRT = new YmlWriterPIRTData();
		writerPIRT.writePIRTData(schemaFile, options, append);

		logger.debug("Export PCMM data"); //$NON-NLS-1$
		YmlWriterPCMMData writerPCMM = new YmlWriterPCMMData();
		writerPCMM.writePCMMData(schemaFile, options, pcmmSpecification, append);

		logger.debug("Export Uncertainty data"); //$NON-NLS-1$
		YmlWriterUncertaintyData writerUncertainty = new YmlWriterUncertaintyData();
		writerUncertainty.writeUncertaintyData(schemaFile, options, specification, append);

		// TODO add the other features export options
	}
}
