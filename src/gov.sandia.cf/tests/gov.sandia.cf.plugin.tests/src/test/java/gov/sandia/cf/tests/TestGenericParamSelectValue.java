/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tests;

import gov.sandia.cf.model.GenericParameterSelectValue;

/**
 * Test generic parameter class
 * 
 * @author Didier Verstraete
 *
 */
public class TestGenericParamSelectValue extends GenericParameterSelectValue<TestGenericParam> {

	private static final long serialVersionUID = 1L;

	private TestGenericParam parameter;

	@Override
	public TestGenericParam getParameter() {
		return parameter;
	}

	@Override
	public void setParameter(TestGenericParam parameter) {
		this.parameter = parameter;
	}
}
