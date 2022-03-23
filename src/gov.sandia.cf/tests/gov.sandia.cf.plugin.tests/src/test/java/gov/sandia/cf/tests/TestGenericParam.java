/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tests;

import java.util.List;
import java.util.stream.Collectors;

import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterConstraint;
import gov.sandia.cf.model.GenericParameterSelectValue;

/**
 * Test generic parameter class
 * 
 * @author Didier Verstraete
 *
 */
public class TestGenericParam extends GenericParameter<TestGenericParam> {

	private static final long serialVersionUID = 1L;

	private List<TestGenericParam> children;
	private List<GenericParameterSelectValue<TestGenericParam>> parameterValueList;
	private List<GenericParameterConstraint<TestGenericParam>> constraintList;
	private TestGenericParam parent;

	@Override
	public TestGenericParam getParent() {
		return parent;
	}

	@Override
	public void setParent(TestGenericParam parent) {
		this.parent = parent;
	}

	@Override
	public List<GenericParameter<TestGenericParam>> getChildren() {
		return children != null ? children.stream().map(GenericParameter.class::cast).collect(Collectors.toList())
				: null;
	}

	@Override
	public void setChildren(List<GenericParameter<TestGenericParam>> children) {
		if (children != null) {
			this.children = children.stream().filter(TestGenericParam.class::isInstance)
					.map(TestGenericParam.class::cast).collect(Collectors.toList());
		} else {
			this.children = null;
		}
	}

	@Override
	public void setParameterValueList(List<GenericParameterSelectValue<TestGenericParam>> parameterValueList) {
		this.parameterValueList = parameterValueList;
	}

	@Override
	public void setConstraintList(List<GenericParameterConstraint<TestGenericParam>> constraintList) {
		this.constraintList = constraintList;
	}

	@Override
	public List<GenericParameterSelectValue<TestGenericParam>> getParameterValueList() {
		return parameterValueList;
	}

	@Override
	public List<GenericParameterConstraint<TestGenericParam>> getConstraintList() {
		return constraintList;
	}
}
