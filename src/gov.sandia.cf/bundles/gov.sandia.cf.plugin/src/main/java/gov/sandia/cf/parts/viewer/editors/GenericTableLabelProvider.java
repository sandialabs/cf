/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer.editors;

import java.util.Optional;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.application.ISystemRequirementApplication;
import gov.sandia.cf.application.configuration.ParameterLinkGson;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.IGenericTableItem;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.widgets.PCMMElementSelectorWidget;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.MathTools;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * Generic table label provider.
 * 
 * @author Didier Verstraete
 *
 */
public class GenericTableLabelProvider extends ColumnLabelProvider {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(GenericTableLabelProvider.class);

	/**
	 * The generic field.
	 */
	private GenericParameter<?> field;

	private IViewManager viewManager;

	/**
	 * The constructor
	 * 
	 * @param field       the generic field to display
	 * @param viewManager the view manager
	 */
	public GenericTableLabelProvider(GenericParameter<?> field, IViewManager viewManager) {
		super();
		Assert.isNotNull(viewManager);

		this.viewManager = viewManager;
		this.field = field;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IGenericTableItem) {
			IGenericTableItem fieldTmp = (IGenericTableItem) element;

			// Retrieve columns values
			if (fieldTmp.getValueList() != null) {
				Optional<IGenericTableValue> tableValue = fieldTmp.getValueList().stream()
						.filter(columnValue -> (columnValue != null && columnValue.getParameter() != null
								&& columnValue.getParameter().getId().equals(field.getId())))
						.findFirst();
				if (tableValue.isPresent()) {
					return getFieldLabel(tableValue.get());
				}
			}
		}
		return null;
	}

	/**
	 * Get the field string value.
	 * 
	 * @param columnValue the column value
	 * @return the column string label
	 */
	private String getFieldLabel(IGenericTableValue columnValue) {

		if (FormFieldType.CREDIBILITY_ELEMENT.getType().equals(field.getType())) {
			return getCredibilityElementValue(columnValue);
		} else if (FormFieldType.DATE.getType().equals(field.getType())) {
			return getDateValue(columnValue);
		} else if (FormFieldType.FLOAT.getType().equals(field.getType())) {
			return getTextValue(columnValue);
		} else if (FormFieldType.LINK.getType().equals(field.getType())) {
			return getLinkValue(columnValue);
		} else if (FormFieldType.RICH_TEXT.getType().equals(field.getType())) {
			return getRichTextValue(columnValue);
		} else if (FormFieldType.SELECT.getType().equals(field.getType())) {
			return getSelectValue(columnValue);
		} else if (FormFieldType.SYSTEM_REQUIREMENT.getType().equals(field.getType())) {
			return getSystemRequirementValue(columnValue);
		} else if (FormFieldType.TEXT.getType().equals(field.getType())) {
			return getTextValue(columnValue);
		}

		return null;
	}

	/**
	 * Get the select field string value.
	 * 
	 * @param columnValue the column value
	 * @return the column string label
	 */
	private String getSelectValue(IGenericTableValue columnValue) {
		// Initialize
		GenericParameterSelectValue<?> parameterValue = null;
		if (columnValue.getValue() != null && field.getParameterValueList() != null) {
			for (GenericParameterSelectValue<?> v : field.getParameterValueList()) {
				if (v.getId().toString().equals(columnValue.getValue())) {
					parameterValue = v;
				}
			}
		}
		return ((parameterValue != null) ? parameterValue.getName() : RscTools.empty());
	}

	/**
	 * Get the credibility element field string value.
	 * 
	 * @param columnValue the column value
	 * @return the column string label
	 */
	private String getCredibilityElementValue(IGenericTableValue columnValue) {

		if (columnValue == null || !MathTools.isInteger(columnValue.getValue())) {
			return RscTools.empty();
		}

		PCMMElement element = null;
		try {
			element = viewManager.getAppManager().getService(IPCMMApplication.class)
					.getElementById(Integer.valueOf(columnValue.getValue()));
		} catch (NumberFormatException | CredibilityException e) {
			logger.error(e.getMessage(), e);
		}

		if (element != null) {
			return element.getAbstract();
		} else if (MathTools.isInteger(columnValue.getValue())
				&& PCMMElementSelectorWidget.NOT_APPLICABLE_ID.equals(Integer.valueOf(columnValue.getValue()))) {
			return PCMMElementSelectorWidget.NOT_APPLICABLE_VALUE;
		} else {
			return RscTools.empty();
		}
	}

	/**
	 * Get the system requirement field string value.
	 * 
	 * @param columnValue the column value
	 * @return the column string label
	 */
	private String getSystemRequirementValue(IGenericTableValue columnValue) {

		if (columnValue == null || !MathTools.isInteger(columnValue.getValue())) {
			return RscTools.empty();
		}

		SystemRequirement requirement = viewManager.getAppManager().getService(ISystemRequirementApplication.class)
				.getRequirementById(Integer.valueOf(columnValue.getValue()));

		return requirement != null ? requirement.getAbstract() : RscTools.empty();
	}

	/**
	 * Get the Text field string value.
	 * 
	 * @param columnValue the column value
	 * @return the column string label
	 */
	private String getTextValue(IGenericTableValue columnValue) {
		return columnValue.getValue();
	}

	/**
	 * Get the Link field string value.
	 * 
	 * @param columnValue the column value
	 * @return the column string label
	 */
	private String getLinkValue(IGenericTableValue columnValue) {
		String linkValue = RscTools.empty();
		if (columnValue.getValue() != null) {
			String jsonString = columnValue.getValue();
			Gson gson = new Gson();
			try {
				ParameterLinkGson linkData = gson.fromJson(jsonString, ParameterLinkGson.class);
				if (linkData != null && linkData.value != null) {
					linkValue = linkData.value;
				}
			} catch (JsonSyntaxException ex) {
				logger.warn(ex.getMessage());
			}
		}
		return linkValue;
	}

	/**
	 * Get the RichText field string value.
	 * 
	 * @param columnValue the column value
	 * @return the column string label
	 */
	private String getRichTextValue(IGenericTableValue columnValue) {
		return StringTools.clearHtml(columnValue.getValue(), true);
	}

	/**
	 * Get the Date field string value.
	 * 
	 * @param columnValue the column value
	 * @return the column string label
	 */
	private String getDateValue(IGenericTableValue columnValue) {
		return DateTools.formatDate(DateTools.parseDate(columnValue.getValue(), DateTools.getDateTimeFormat()),
				DateTools.getDateFormat());
	}

}
