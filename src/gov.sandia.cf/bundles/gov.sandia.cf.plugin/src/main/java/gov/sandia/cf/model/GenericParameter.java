/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.configuration.YmlGenericSchema;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The GenericParameter entity class
 * 
 * @author Didier Verstraete
 * @param <T> The generic parameter inherited class
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class GenericParameter<T extends GenericParameter<T>> implements Serializable {

	private static final long serialVersionUID = 4184820050582681422L;

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(GenericParameter.class);

	/**
	 * Field Filter
	 */
	@SuppressWarnings("javadoc")
	public enum Filter implements EntityFilter {
		ID("id"), //$NON-NLS-1$
		NAME("name"), //$NON-NLS-1$
		LEVEL("level"), //$NON-NLS-1$
		TYPE("type"), //$NON-NLS-1$
		DEFAULT_VALUE("defaultValue"), //$NON-NLS-1$
		REQUIRED("required"), //$NON-NLS-1$
		MODEL("model"), //$NON-NLS-1$
		PARENT("parent"); //$NON-NLS-1$

		private String field;

		/**
		 * Filter
		 * 
		 * @param field
		 */
		Filter(String field) {
			this.field = field;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getField() {
			return this.field;
		}
	}

	/**
	 * The id field linked to ID column
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	/**
	 * The name field linked to NAME column
	 */
	@Column(name = "NAME", columnDefinition = "LONGVARCHAR")
	@NotBlank(message = RscConst.EX_GENPARAMETER_NAME_BLANK)
	private String name;

	/**
	 * The level field linked to LEVEL column
	 */
	@Column(name = "LEVEL", columnDefinition = "varchar(16) default '" + YmlGenericSchema.DEFAULT_LEVEL + "'")
	@NotBlank(message = RscConst.EX_GENPARAMETER_NAME_BLANK)
	private String level = YmlGenericSchema.DEFAULT_LEVEL;

	/**
	 * The type field linked to TYPE column
	 */
	@Column(name = "TYPE")
	@NotBlank(message = RscConst.EX_GENPARAMETER_TYPE_BLANK)
	private String type;

	/**
	 * The defaultValue field linked to DEFAULT_VALUE column
	 */
	@Column(name = "DEFAULT_VALUE")
	private String defaultValue;

	/**
	 * The required field linked to REQUIRED column
	 */
	@Column(name = "REQUIRED")
	@NotNull(message = RscConst.EX_GENPARAMETER_REQUIRED_NULL)
	private String required;

	/**
	 * The model field linked to MODEL_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	@NotNull(message = RscConst.EX_GENPARAMETER_MODEL_NULL)
	private Model model;

	@SuppressWarnings("javadoc")
	public Integer getId() {
		return id;
	}

	@SuppressWarnings("javadoc")
	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public String getName() {
		return name;
	}

	@SuppressWarnings("javadoc")
	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("javadoc")
	public String getType() {
		return type;
	}

	@SuppressWarnings("javadoc")
	public void setType(String type) {
		this.type = type;
	}

	@SuppressWarnings("javadoc")
	public String getLevel() {
		return level;
	}

	@SuppressWarnings("javadoc")
	public void setLevel(String level) {
		this.level = level;
	}

	@SuppressWarnings("javadoc")
	public String getDefaultValue() {
		return defaultValue;
	}

	@SuppressWarnings("javadoc")
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@SuppressWarnings("javadoc")
	public String getRequired() {
		return required;
	}

	@SuppressWarnings("javadoc")
	public void setRequired(String required) {
		this.required = required;
	}

	@SuppressWarnings("javadoc")
	public Model getModel() {
		return model;
	}

	@SuppressWarnings("javadoc")
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * @return the parameterValueList
	 */
	public abstract List<GenericParameterSelectValue<T>> getParameterValueList();

	/**
	 * @param parameterValueList the parameterValueList to set
	 */
	public abstract void setParameterValueList(List<GenericParameterSelectValue<T>> parameterValueList);

	/**
	 * @return the constraintList
	 */
	public abstract List<GenericParameterConstraint<T>> getConstraintList();

	/**
	 * @param constraintList the constraintList to set
	 */
	public abstract void setConstraintList(List<GenericParameterConstraint<T>> constraintList);

	/**
	 * @return The GenericParameter children list
	 */
	public abstract List<GenericParameter<T>> getChildren();

	/**
	 * Sets the children field with @param children
	 * 
	 * @param children the GenericParameter children list
	 */
	public abstract void setChildren(List<GenericParameter<T>> children);

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public abstract T getParent();

	/**
	 * Sets the parent.
	 *
	 * @param parent the new parent
	 */
	public abstract void setParent(T parent);

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @param classEntity the generic parameter class
	 * @return a copy of the current entity
	 */
	public T copy(Class<T> classEntity) {
		T entity = null;
		try {
			entity = classEntity.newInstance();
			entity.setDefaultValue(getDefaultValue());
			entity.setLevel(getLevel());
			entity.setModel(getModel());
			entity.setName(getName());
			entity.setParent(getParent());
			entity.setRequired(getRequired());
			entity.setType(getType());
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("Entity copy error: {}", e.getMessage(), e); //$NON-NLS-1$
		}

		return entity;
	}

	@Override
	public String toString() {
		return "GenericParameter [" + "id=" + (id != null ? id.toString() : "") + RscTools.COMMA + "name=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (name != null ? name : "") + RscTools.COMMA + "type=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (type != null ? type : "") + RscTools.COMMA + "Default Value=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (defaultValue != null ? defaultValue : "") + RscTools.COMMA + "Required=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (required != null ? required : "") + RscTools.COMMA + "model=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (model != null ? model.toString() : "") + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}