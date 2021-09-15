/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.util.Optional;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.tools.StringTools;

/**
 * The Generic Value field widget for Generic Parameter values
 * 
 * @author Didier Verstraete
 * @param <P> The generic parameter inherited class
 *
 */
public class GenericValueFieldWidget<P extends GenericParameter<P>>
		extends FormFieldWidget<GenericParameterSelectValue<P>> {

	/**
	 * The generic parameter
	 */
	private P parameter;

	/**
	 * The generic field widget constructor
	 * 
	 * @param viewManager the view manager to query database
	 * @param parent      the composite parent
	 * @param parameter   the generic parameter
	 * @param editable    is editable?
	 */
	public GenericValueFieldWidget(IViewManager viewManager, Composite parent, P parameter, boolean editable) {
		super(viewManager, parent, toType(parameter), editable, getId(parameter), getName(parameter),
				parameter.getParameterValueList());

		// set parameter
		Assert.isNotNull(parameter);
		this.parameter = parameter;

		// set default value
		if (!org.apache.commons.lang3.StringUtils.isBlank(parameter.getDefaultValue())) {
			setDefaultValue(parameter.getDefaultValue());
		}
	}

	/**
	 * @param parameter the generic parameter
	 * @return generic parameter type
	 */
	private static FormFieldType toType(GenericParameter<?> parameter) {
		Optional<FormFieldType> type = FormFieldType.getType(parameter.getType());
		return type.isPresent() ? type.get() : null;
	}

	/**
	 * @param parameter the generic parameter
	 * @return generic parameter name
	 */
	private static String getName(GenericParameter<?> parameter) {
		return parameter != null ? parameter.getName() : null;
	}

	/**
	 * @param parameter the generic parameter
	 * @return generic parameter id
	 */
	private static String getId(GenericParameter<?> parameter) {
		return parameter != null ? gov.sandia.cf.tools.StringTools.getOrEmpty(parameter.getId()) : null;
	}

	/**
	 * @return the generic parameter
	 */
	public P getParameter() {
		return parameter;
	}

	/**
	 * Set the generic value
	 * 
	 * @param value the generic value
	 */
	public void setValue(GenericValue<P, ?> value) {

		if (value == null) {
			return;
		}

		super.setValue(StringTools.getOrEmpty(value.getValue()));
	}

	/**
	 * Set the default value for edition
	 * 
	 * @param value the generic value
	 */
	private void setDefaultValue(String textValue) {
		if (super.isEditable()) {
			super.setValue(textValue);
		}
	}

}
