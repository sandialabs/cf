/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IImportApplication;
import gov.sandia.cf.application.IImportDecisionApp;
import gov.sandia.cf.application.IImportPCMMApp;
import gov.sandia.cf.application.IImportPIRTApp;
import gov.sandia.cf.application.IImportQoIPlanningApp;
import gov.sandia.cf.application.IImportSysRequirementApp;
import gov.sandia.cf.application.IImportUncertaintyApp;
import gov.sandia.cf.application.configuration.ConfigurationFileType;
import gov.sandia.cf.application.configuration.ConfigurationSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.DecisionConstraint;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionSelectValue;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningParamConstraint;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionConstraint;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTAdequacyColumnLevelGuideline;
import gov.sandia.cf.model.PIRTDescriptionHeader;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.QoIPlanningConstraint;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningSelectValue;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.SystemRequirementConstraint;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementSelectValue;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyConstraint;
import gov.sandia.cf.model.UncertaintyGroup;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Import Application manager for methods that are specific to the import.
 * 
 * @author Didier Verstraete
 * 
 */
public class ImportApplication extends AApplication implements IImportApplication {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ImportApplication.class);

	/**
	 * ImportApplication constructor
	 */
	public ImportApplication() {
		super();
	}

	/**
	 * ImportApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ImportApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	/** {@inheritDoc} */
	@Override
	public <M extends IImportable<M>> Map<ImportActionType, List<?>> analyzeImport(List<M> newImportableList,
			List<M> existingImportableList) {

		logger.debug("Analyzing class import."); //$NON-NLS-1$

		Map<ImportActionType, List<?>> questionsAnalysis = new EnumMap<>(ImportActionType.class);
		List<M> toAdd = new ArrayList<>();
		List<M> toDelete = new ArrayList<>();
		List<M> noChanges = new ArrayList<>();

		if (existingImportableList == null || existingImportableList.isEmpty()) {
			// new planning questions TO ADD
			if (newImportableList != null && !newImportableList.isEmpty()) {
				toAdd.addAll(newImportableList);
			}
		} else {

			// existing planning questions TO DELETE
			if (newImportableList == null || newImportableList.isEmpty()) {
				toDelete.addAll(existingImportableList.stream().collect(Collectors.toList()));
			} else {
				// search questions TO DELETE in the existing questions
				for (M existingQuestion : existingImportableList) {
					long nbMatch = newImportableList.stream()
							.filter(newQuestion -> existingQuestion.sameAs(newQuestion)).count();

					if (nbMatch > 0) {
						noChanges.add(existingQuestion);
					} else {
						toDelete.add(existingQuestion);
					}
				}

				// search questions TO ADD in the new questions
				for (M newQuestion : newImportableList) {
					long nbMatch = existingImportableList.stream()
							.filter(existingQuestion -> newQuestion.sameAs(existingQuestion)).count();

					if (nbMatch <= 0) {
						toAdd.add(newQuestion);
					}
				}
			}
		}

		questionsAnalysis.put(ImportActionType.TO_ADD, toAdd);
		questionsAnalysis.put(ImportActionType.TO_DELETE, toDelete);
		questionsAnalysis.put(ImportActionType.NO_CHANGES, noChanges);

		return questionsAnalysis;
	}

	/** {@inheritDoc} */
	@Override
	public <M extends IImportable<M>> List<?> getChangesToAdd(List<M> newImportableList,
			List<M> existingImportableList) {

		// analyze changes
		Map<ImportActionType, List<?>> analyzeImport = analyzeImport(newImportableList, existingImportableList);

		// get list to add
		List<?> toAdd = analyzeImport != null && analyzeImport.containsKey(ImportActionType.TO_ADD)
				? analyzeImport.get(ImportActionType.TO_ADD)
				: null;

		return toAdd != null ? toAdd : new ArrayList<>();
	}

	/** {@inheritDoc} */
	@Override
	public <M extends IImportable<M>> void importChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {

		// import QoI Planning changes
		getAppMgr().getService(IImportQoIPlanningApp.class).importQoIPlanningChanges(model, toImport);

		// import PIRT changes
		getAppMgr().getService(IImportPIRTApp.class).importPIRTChanges(model, toImport);

		// import PCMM changes
		getAppMgr().getService(IImportPCMMApp.class).importPCMMChanges(model, toImport);

		// import Uncertainty changes
		getAppMgr().getService(IImportUncertaintyApp.class).importUncertaintyChanges(model, toImport);

		// import System Requirement changes
		getAppMgr().getService(IImportSysRequirementApp.class).importSysRequirementChanges(model, toImport);

		// import Decision changes
		getAppMgr().getService(IImportDecisionApp.class).importDecisionChanges(model, toImport);
	}

	/** {@inheritDoc} */
	@Override
	public <M extends IImportable<M>> List<M> getChanges(
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport, Class<M> importClass,
			ImportActionType importAction) {

		List<M> toAdd = new ArrayList<>();

		if (toImport.containsKey(importClass) && toImport.get(importClass).containsKey(importAction)) {
			toAdd.addAll(toImport.get(importClass).get(importAction).stream().map(importClass::cast)
					.collect(Collectors.toList()));
		}

		return toAdd;
	}

	/** {@inheritDoc} */
	@Override
	public void importConfiguration(Model model, ConfigurationSchema confSchema)
			throws CredibilityException, IOException {

		// Import PIRT specification in database
		getAppMgr().getService(IImportPIRTApp.class).importPIRTSpecification(model,
				confSchema.getFile(ConfigurationFileType.PIRT));

		getAppMgr().getService(IImportQoIPlanningApp.class).importQoIPlanningSpecification(model,
				confSchema.getFile(ConfigurationFileType.QOIPLANNING));

		// Import PCMM specification in database
		getAppMgr().getService(IImportPCMMApp.class).importPCMMSpecification(model,
				confSchema.getFile(ConfigurationFileType.PCMM));

		// Import Planning specification in database
		getAppMgr().getService(IImportUncertaintyApp.class).importUncertaintySpecification(model,
				confSchema.getFile(ConfigurationFileType.UNCERTAINTY));
		getAppMgr().getService(IImportSysRequirementApp.class).importSysRequirementSpecification(model,
				confSchema.getFile(ConfigurationFileType.SYSTEM_REQUIREMENT));
		getAppMgr().getService(IImportDecisionApp.class).importDecisionSpecification(model,
				confSchema.getFile(ConfigurationFileType.DECISION));
	}

	/** {@inheritDoc} */
	@Override
	public String getImportableName(Class<?> importClass) {

		String className = RscTools.empty();

		// Decision
		if (DecisionConstraint.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_DECISIONCONSTRAINT);
		} else if (DecisionParam.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_DECISIONPARAM);
		} else if (DecisionSelectValue.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_DECISIONSELECTVALUE);
		}

		// PCMM
		else if (PCMMElement.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PCMMELEMENT);
		} else if (PCMMLevel.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PCMMLEVEL);
		} else if (PCMMLevelColor.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PCMMLEVELCOLOR);
		} else if (PCMMLevelDescriptor.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PCMMLEVELDESCRIPTOR);
		} else if (PCMMOption.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PCMMPHASE);
		} else if (PCMMPlanningParam.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PCMMPLANNINGPARAM);
		} else if (PCMMPlanningParamConstraint.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PCMMPLANNINGPARAMCONSTRAINT);
		} else if (PCMMPlanningQuestion.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PCMMPLANNINGQUESTION);
		} else if (PCMMPlanningQuestionConstraint.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PCMMPLANNINGQUESTIONCONSTRAINT);
		} else if (PCMMSubelement.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PCMMSUBELEMENT);
		}

		// PIRT
		else if (PIRTAdequacyColumn.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PIRTADEQUACYCOLUMN);
		} else if (PIRTAdequacyColumnGuideline.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PIRTADEQUACYCOLUMNGUIDELINE);
		} else if (PIRTAdequacyColumnLevelGuideline.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PIRTADEQUACYCOLUMNLEVELGUIDELINE);
		} else if (PIRTDescriptionHeader.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PIRTDESCRIPTIONHEADER);
		} else if (PIRTLevelDifferenceColor.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PIRTLEVELDIFFCOLOR);
		} else if (PIRTLevelImportance.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PIRTLEVELIMPORTANCE);
		}

		// QoI Planning
		else if (QoIPlanningConstraint.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_QOIPLANNINGCONSTRAINT);
		} else if (QoIPlanningParam.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_QOIPLANNINGPARAM);
		} else if (QoIPlanningSelectValue.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_QOIPLANNINGSELECTVALUE);
		}

		// Global Data
		else if (Role.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_USER);
		} else if (User.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_USER);
		}

		// System Requirement Schema
		else if (SystemRequirementConstraint.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_REQUIREMENTCONSTRAINT);
		} else if (SystemRequirementParam.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_REQUIREMENTPARAM);
		} else if (SystemRequirementSelectValue.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_REQUIREMENTSELECTVALUE);
		}

		// Uncertainty Schema
		else if (UncertaintyConstraint.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_UNCERTAINTYCONSTRAINT);
		} else if (UncertaintyParam.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_UNCERTAINTYPARAM);
		} else if (UncertaintySelectValue.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_UNCERTAINTYSELECTVALUE);
		}

		// Uncertainty Data
		else if (UncertaintyGroup.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_UNCERTAINTYGROUP);
		} else if (Uncertainty.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_UNCERTAINTY);
		} else if (UncertaintyValue.class.equals(importClass)) {
			className = RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_UNCERTAINTYVALUE);
		}

		return className;
	}

	/** {@inheritDoc} */
	@Override
	public <M extends IImportable<M>> boolean sameListContent(List<M> list1, List<M> list2) {
		return listContainsOther(list1, list2) && listContainsOther(list2, list1);
	}

	/**
	 * @param <M>   the importable entity
	 * @param list1 the list 1
	 * @param list2 the list 2
	 * @return true if the list1 is contained in the list2
	 */
	private <M extends IImportable<M>> boolean listContainsOther(List<M> list1, List<M> list2) {
		if (list1 == null) {
			return list2 == null;
		} else if (list2 != null) {
			for (M column1 : list1) {
				boolean contains = false;
				for (M column2 : list2) {
					if (column1.sameAs(column2)) {
						contains = true;
						break;
					}
				}

				if (!contains) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> getListOfImportableFromAnalysis(
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis) {

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = new HashMap<>();

		if (analysis == null) {
			return toChange;
		}

		for (Entry<Class<?>, Map<ImportActionType, List<?>>> entryClass : analysis.entrySet()) {

			if (entryClass.getKey() == null || !IImportable.class.isAssignableFrom(entryClass.getKey())) {
				continue;
			}

			toChange.put(entryClass.getKey(), new EnumMap<>(ImportActionType.class));

			for (Entry<ImportActionType, List<?>> entryAction : entryClass.getValue().entrySet()) {

				if (!(entryAction.getKey() instanceof ImportActionType)) {
					continue;
				}

				toChange.get(entryClass.getKey()).put(entryAction.getKey(), new ArrayList<>());

				if (entryAction.getValue() != null) {
					toChange.get(entryClass.getKey()).get(entryAction.getKey()).addAll(
							entryAction.getValue().stream().map(IImportable.class::cast).collect(Collectors.toList()));
				}
			}
		}

		return toChange;
	}
}
