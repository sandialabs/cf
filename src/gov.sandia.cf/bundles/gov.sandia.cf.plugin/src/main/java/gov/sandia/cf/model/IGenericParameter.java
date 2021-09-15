/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.List;

/**
 * The GenericParameter entity interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IGenericParameter extends Serializable {

	/**
	 * @return the id
	 */
	public Integer getId();

	/**
	 * Sets the id field with @param id
	 * 
	 * @param id the id to set
	 */
	public void setId(Integer id);

	/**
	 * @return the name
	 */
	public String getName();

	/**
	 * Sets the name field with @param name
	 * 
	 * @param name the name to set
	 */
	public void setName(String name);

	/**
	 * @return the type
	 */
	public String getType();

	/**
	 * Sets the type field with @param type
	 * 
	 * @param type the type to set
	 */
	public void setType(String type);

	/**
	 * @return the level
	 */
	public String getLevel();

	/**
	 * Sets the level field with @param level
	 * 
	 * @param level the level to set
	 */
	public void setLevel(String level);

	/**
	 * @return the default value
	 */
	public String getDefaultValue();

	/**
	 * Sets the default value field
	 * 
	 * @param defaultValue the default value
	 */
	public void setDefaultValue(String defaultValue);

	/**
	 * @return the parameter required
	 */
	public String getRequired();

	/**
	 * Sets required field with @param required
	 * 
	 * @param required required to set
	 */
	public void setRequired(String required);

	/**
	 * @return the model
	 */
	public Model getModel();

	/**
	 * Sets the model field with @param model
	 * 
	 * @param model the model to set
	 */
	public void setModel(Model model);

	/**
	 * @return the parameterValueList
	 */
	public List<GenericParameterSelectValue<?>> getParameterValueList();

	/**
	 * @param parameterValueList the parameterValueList to set
	 */
	public void setParameterValueList(List<GenericParameterSelectValue<?>> parameterValueList);

	/**
	 * @return the constraintList
	 */
	public List<GenericParameterConstraint<?>> getConstraintList();

	/**
	 * @param constraintList the constraintList to set
	 */
	public void setConstraintList(List<GenericParameterConstraint<?>> constraintList);

	/**
	 * @return The GenericParameter children list
	 */
	public List<IGenericParameter> getChildren();

	/**
	 * Sets the children field with @param children
	 * 
	 * @param children the GenericParameter children list
	 */
	public void setChildren(List<IGenericParameter> children);

	/**
	 * @return the GenericParameter parent
	 */
	public IGenericParameter getParent();

	/**
	 * Sets the parent field with @param parent
	 * 
	 * @param parent the GenericParameter parent
	 */
	public void setParent(IGenericParameter parent);

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @param classEntity the generic parameter class
	 * 
	 * @return a copy of the current entity
	 */
	public IGenericParameter copy(Class<IGenericParameter> classEntity);
}