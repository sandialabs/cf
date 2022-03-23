/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.pcmm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.dao.IPCMMLevelRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAggregation;
import gov.sandia.cf.model.PCMMAggregationLevel;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * Manage PCMM Aggregate Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAggregateApp extends AApplication implements IPCMMAggregateApp {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMAggregateApp.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCompleteAggregation(Model model, Tag tag) throws CredibilityException {

		boolean isCompleteAggregation = false;

		List<PCMMElement> elements = getAppMgr().getService(IPCMMApplication.class).getElementList(model);

		if (elements != null) {
			isCompleteAggregation = true;
			for (PCMMElement element : elements) {
				for (PCMMSubelement sub : element.getSubElementList()) {
					Map<EntityFilter, Object> filters = new HashMap<>();
					filters.put(PCMMAssessment.Filter.TAG, tag);
					List<PCMMAssessment> assessmentBySubelement = getAppMgr().getService(IPCMMAssessmentApp.class)
							.getAssessmentBySubelement(sub, filters);
					if (assessmentBySubelement == null || assessmentBySubelement.isEmpty()) {
						isCompleteAggregation = false;
						break;
					}
				}
			}
		}

		return isCompleteAggregation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCompleteAggregationSimplified(Model model, Tag tag) throws CredibilityException {

		boolean isCompleteAggregation = false;

		List<PCMMElement> elements = getAppMgr().getService(IPCMMApplication.class).getElementList(model);

		if (elements != null) {
			isCompleteAggregation = true;
			for (PCMMElement element : elements) {
				Map<EntityFilter, Object> filters = new HashMap<>();
				filters.put(PCMMAssessment.Filter.TAG, tag);
				List<PCMMAssessment> assessmentByElement = getAppMgr().getService(IPCMMAssessmentApp.class)
						.getAssessmentByElement(element, filters);
				if (assessmentByElement == null || assessmentByElement.isEmpty()) {
					isCompleteAggregation = false;
					break;
				}
			}
		}

		return isCompleteAggregation;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregateSubelements(PCMMSpecification configuration,
			List<PCMMElement> elements, Map<EntityFilter, Object> filters) throws CredibilityException {

		// check elements
		if (elements == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ELTLISTNULL));
		}
		if (configuration == null || configuration.getLevelColors() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_CONFLEVELCOLORLISTNULL));
		}

		return aggregateSubelements(configuration, aggregateAssessments(configuration, elements, filters));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregateSubelements(PCMMSpecification configuration,
			Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> mapAggregationBySubelement)
			throws CredibilityException {

		Map<PCMMElement, PCMMAggregation<PCMMElement>> aggegationMap = new HashMap<>();

		// check elements
		if (mapAggregationBySubelement == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_AGGREGMAPNULL));
		}
		if (configuration == null || configuration.getLevelColors() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_CONFLEVELCOLORLISTNULL));
		}

		// get the data used to compute the aggregation
		Map<PCMMElement, Integer> mapNbElement = new HashMap<>();
		Map<PCMMElement, Integer> mapSum = new HashMap<>();
		for (PCMMAggregation<PCMMSubelement> aggregSub : mapAggregationBySubelement.values()) {

			if (aggregSub.getItem() != null && aggregSub.getItem().getElement() != null) {
				// Get element
				PCMMElement elementTmp = aggregSub.getItem().getElement();

				// the sum of the level codes
				int sum = 0;
				if (aggregSub.getLevel() != null && aggregSub.getLevel().getCode() != null) {
					sum = aggregSub.getLevel().getCode();
				}

				// the number of element
				int nbElement = 0;
				if (aggregSub.getLevel() != null && aggregSub.getLevel().getCode() != null) {
					nbElement = 1;
				}

				if (!mapNbElement.containsKey(elementTmp)) {

					// initialize element maps
					mapNbElement.put(elementTmp, nbElement);
					mapSum.put(elementTmp, sum);

				} else {

					// add an element level to the maps
					mapNbElement.put(elementTmp, mapNbElement.get(elementTmp) + nbElement);
					mapSum.put(elementTmp, mapSum.get(elementTmp) + sum);

				}
			}
		}

		// aggregate the result
		for (Entry<PCMMElement, Integer> entry : mapNbElement.entrySet()) {

			if (entry != null) {

				PCMMElement elementTmp = entry.getKey();
				int nbAggregation = entry.getValue();

				if (elementTmp != null) {
					// retrieve the sum and the number of elements
					int sum = mapSum.get(elementTmp);

					// create the aggregation result
					PCMMAggregation<PCMMElement> aggregation = new PCMMAggregation<>();
					aggregation.setItem(elementTmp);

					// aggregate
					if (nbAggregation > 0) {
						float average = (((float) sum) / ((float) nbAggregation));
						int code = (int) Math.ceil((double) average); // round to the highest closest int

						// Set level
						List<PCMMLevel> levels = getDaoManager().getRepository(IPCMMLevelRepository.class)
								.findByPCMMElement(elementTmp);
						aggregation.setLevel(getClosestLevelForCode(configuration, levels, code));
					}

					// put the aggregation in the map to return
					aggegationMap.put(elementTmp, aggregation);
				}
			}
		}

		return aggegationMap;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggregateAssessments(PCMMSpecification configuration,
			List<PCMMElement> elements, Map<EntityFilter, Object> filters) throws CredibilityException {

		Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggegation = new HashMap<>();

		// check elements
		if (elements == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ELTLISTNULL));
		} else {
			for (PCMMElement element : elements) {
				aggegation.putAll(aggregateAssessments(configuration, element, filters));
			}
		}
		return aggegation;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggregateAssessments(PCMMSpecification configuration,
			PCMMElement element, Map<EntityFilter, Object> filters) throws CredibilityException {

		logger.debug("Aggregating assessments for PCMM subelements"); //$NON-NLS-1$

		Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggegation = new HashMap<>();

		// check element
		if (element == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ELTNULL));
		} else {
			if (element.getSubElementList() != null) {
				for (PCMMSubelement subelt : element.getSubElementList()) {
					aggegation.put(subelt, aggregateAssessments(configuration, subelt, getAppMgr()
							.getService(IPCMMAssessmentApp.class).getAssessmentBySubelement(subelt, filters)));
				}
			}
		}
		return aggegation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregateAssessmentSimplified(PCMMSpecification configuration,
			List<PCMMElement> elements, Map<EntityFilter, Object> filters) throws CredibilityException {

		logger.debug("Aggregating assessments for PCMM elements"); //$NON-NLS-1$

		Map<PCMMElement, PCMMAggregation<PCMMElement>> aggegation = new HashMap<>();

		// check elements
		if (elements == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ELTLISTNULL));
		} else {
			for (PCMMElement element : elements) {
				aggegation.put(element, aggregateAssessments(configuration, element,
						getAppMgr().getService(IPCMMAssessmentApp.class).getAssessmentByElement(element, filters)));
			}
		}
		return aggegation;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T extends IAssessable> PCMMAggregation<T> aggregateAssessments(PCMMSpecification configuration, T item,
			List<PCMMAssessment> assessmentList) throws CredibilityException {

		PCMMAggregation<T> aggregation = null;

		// check item
		if (item == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ITEMNULL));
		}

		if (assessmentList != null) {
			int sum = 0;
			int nbAssessments = 0;
			aggregation = new PCMMAggregation<>();
			ArrayList<String> commentList = new ArrayList<>();
			for (PCMMAssessment assessmt : assessmentList) {
				// compute the aggregation
				PCMMLevel level = assessmt.getLevel();
				if (level != null && level.getCode() != null) {
					sum += level.getCode();
					nbAssessments++;
				}
				commentList.add(StringTools.clearHtml(assessmt.getComment()));
			}

			// fill the aggregation
			aggregation.setCommentList(commentList);
			aggregation.setItem(item);
			if (nbAssessments > 0) {
				double average = (((double) sum) / ((double) nbAssessments));
				int code = (int) Math.ceil(average); // round to the closest int

				// Get fresh levels
				List<PCMMLevel> levels = new ArrayList<>();
				if (item instanceof PCMMElement) {
					levels = getDaoManager().getRepository(IPCMMLevelRepository.class)
							.findByPCMMElement((PCMMElement) item);
				} else if (item instanceof PCMMSubelement) {
					levels = getDaoManager().getRepository(IPCMMLevelRepository.class)
							.findByPCMMSubelement((PCMMSubelement) item);
				}
				aggregation.setLevel(getClosestLevelForCode(configuration, levels, code));
			}
		}
		return aggregation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMAggregationLevel getClosestLevelForCode(PCMMSpecification configuration, List<PCMMLevel> levels,
			int code) {

		if (levels == null) {
			return null;
		}

		// search for the closest level
		PCMMLevel closestLevel = null;
		PCMMAggregationLevel aggLevel = null;

		// sort the level list by code
		levels.sort(getLevelComparator());

		for (PCMMLevel level : levels.stream().filter(Objects::nonNull).collect(Collectors.toList())) {
			if (closestLevel == null) {
				closestLevel = level;
			}
			if (level.getCode() != null) {
				// if the level code is exactly the same as the parameter, return it
				if (level.getCode() <= code) {
					closestLevel = level;
				} else {
					break;
				}
			}
		}

		// Get level
		aggLevel = new PCMMAggregationLevel();
		aggLevel.setCode(code);
		if (null != configuration.getLevelColors() && configuration.getLevelColors().get(code) != null) {
			aggLevel.setName(configuration.getLevelColors().get(code).getName());
		}

		return aggLevel;
	}

	/**
	 * @return a level comparator based on the level code
	 */
	private Comparator<PCMMLevel> getLevelComparator() {
		return (o1, o2) -> {
			if (o1 == null && o2 == null)
				return 0;
			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1;
			return Comparator.comparing(PCMMLevel::getCode, Comparator.nullsFirst(Comparator.naturalOrder()))
					.compare(o1, o2);
		};
	}
}
