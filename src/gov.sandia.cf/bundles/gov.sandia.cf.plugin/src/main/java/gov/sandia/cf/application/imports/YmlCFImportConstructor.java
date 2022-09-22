/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.imports;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;

import gov.sandia.cf.model.DecisionConstraint;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionSelectValue;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterConstraint;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningParamConstraint;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionConstraint;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningSelectValue;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMPlanningTableValue;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.QoIPlanningConstraint;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningSelectValue;
import gov.sandia.cf.model.QoIPlanningValue;
import gov.sandia.cf.model.SystemRequirementConstraint;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementSelectValue;
import gov.sandia.cf.model.UncertaintyConstraint;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;

/**
 * The Yml tools class with static methods
 * 
 * @author Didier Verstraete
 *
 */
public class YmlCFImportConstructor extends Constructor {

	private Class<?> lastParentType;
	private List<Class<?>> parentList;
	private Map<Class<?>, Class<?>> selectValueMapping;
	private Map<Class<?>, Class<?>> constraintMapping;
	private Map<Class<?>, Class<?>> valueMapping;

	/**
	 * Instantiates a new yml CF import constructor.
	 *
	 * @param type the type
	 */
	public YmlCFImportConstructor(Class<?> type) {
		super(type);

		lastParentType = null;
		loadParentList();
		loadSelectValueMapping();
		loadConstraintMapping();
		loadValueMapping();

		yamlClassConstructors.put(NodeId.mapping, getMapping());
	}

	/**
	 * Gets the mapping.
	 *
	 * @return the mapping
	 */
	private Constructor.ConstructMapping getMapping() {
		return new Constructor.ConstructMapping() {
			@Override
			public Object construct(Node node) {
				MappingNode mnode = (MappingNode) node;

				if (parentList.contains(node.getType())) {
					lastParentType = node.getType();
				}

				Class<?> typeToInstantiate = getTypeToInstantiate(node);
				if (typeToInstantiate != null) {
					try {
						return typeToInstantiate.getDeclaredConstructor().newInstance();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					}
				}

				return super.construct(mnode);
			}
		};
	}

	private Class<?> getTypeToInstantiate(Node node) {

		Class<?> typeToInstantiate = null;

		if (GenericParameterSelectValue.class.equals(node.getType())
				&& selectValueMapping.containsKey(lastParentType)) {
			typeToInstantiate = selectValueMapping.get(lastParentType);
		} else if (GenericParameterConstraint.class.equals(node.getType())
				&& constraintMapping.containsKey(lastParentType)) {
			typeToInstantiate = constraintMapping.get(lastParentType);
		} else if (GenericParameter.class.equals(node.getType())) {
			typeToInstantiate = lastParentType;
		} else if (GenericValue.class.equals(node.getType()) && valueMapping.containsKey(lastParentType)) {
			typeToInstantiate = valueMapping.get(lastParentType);
		} else if (IGenericTableValue.class.equals(node.getType()) && valueMapping.containsKey(lastParentType)) {
			typeToInstantiate = valueMapping.get(lastParentType);
		}

		return typeToInstantiate;
	}

	private void loadParentList() {
		parentList = new ArrayList<>();
		parentList.add(DecisionParam.class);
		parentList.add(SystemRequirementParam.class);
		parentList.add(UncertaintyParam.class);
		parentList.add(PCMMPlanningTableItem.class);
		parentList.add(PCMMPlanningParam.class);
		parentList.add(PCMMPlanningQuestion.class);
		parentList.add(PCMMPlanningQuestion.class);
		parentList.add(QoIPlanningParam.class);
	}

	private void loadSelectValueMapping() {
		selectValueMapping = new HashMap<>();
		selectValueMapping.put(DecisionParam.class, DecisionSelectValue.class);
		selectValueMapping.put(SystemRequirementParam.class, SystemRequirementSelectValue.class);
		selectValueMapping.put(UncertaintyParam.class, UncertaintySelectValue.class);
		selectValueMapping.put(PCMMPlanningParam.class, PCMMPlanningSelectValue.class);
		selectValueMapping.put(QoIPlanningParam.class, QoIPlanningSelectValue.class);
	}

	private void loadConstraintMapping() {
		constraintMapping = new HashMap<>();
		constraintMapping.put(DecisionParam.class, DecisionConstraint.class);
		constraintMapping.put(SystemRequirementParam.class, SystemRequirementConstraint.class);
		constraintMapping.put(UncertaintyParam.class, UncertaintyConstraint.class);
		constraintMapping.put(PCMMPlanningParam.class, PCMMPlanningParamConstraint.class);
		constraintMapping.put(PCMMPlanningParam.class, PCMMPlanningQuestionConstraint.class);
		constraintMapping.put(QoIPlanningParam.class, QoIPlanningConstraint.class);
	}

	private void loadValueMapping() {
		valueMapping = new HashMap<>();
		valueMapping.put(PCMMPlanningTableItem.class, PCMMPlanningTableValue.class);
		valueMapping.put(PCMMPlanningParam.class, PCMMPlanningValue.class);
		valueMapping.put(PCMMPlanningQuestion.class, PCMMPlanningQuestionValue.class);
		valueMapping.put(QoIPlanningParam.class, QoIPlanningValue.class);
	}
}
