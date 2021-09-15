/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.model.query.NullParameter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Abstract Repository which give a basic CRUD toolset using Jpa
 * 
 * @author Didier Verstraete
 *
 * @param <E> the persisting entity class type
 * @param <I> the entity id class type
 * 
 */
public abstract class AbstractCRUDRepository<E, I> implements ICRUDRepository<E, I> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(AbstractCRUDRepository.class);

	private static final String ALIAS = "e"; //$NON-NLS-1$
	private static final String QUERY_ORDER_BY_KEY = "ORDER BY"; //$NON-NLS-1$
	private static final String QUERY_ORDER_BY_DIR = "ASC"; //$NON-NLS-1$
	private static final String QUERY_SELECT_ALL = "SELECT " + ALIAS + " FROM {0} " + ALIAS; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String FIELD_EMPTY = ""; //$NON-NLS-1$

	/**
	 * Query find id by field
	 */
	public static final String QUERY_FINDID_BY_FIELD = "SELECT p.id FROM {0} p WHERE p.{1} = :{2}"; //$NON-NLS-1$

	/**
	 * Query find id by field except for id
	 */
	public static final String QUERY_FINDID_BY_FIELD_EXCEPT_ID = "SELECT p.id FROM {0} p WHERE p.{1} = :{2} AND p.{3} NOT IN :{4}"; //$NON-NLS-1$

	/**
	 * the entity manager to execute jpa queries
	 */
	private EntityManager entityManager;

	/**
	 * the entity class to retrieve the entity specifications, fields and methods
	 * using Java reflection
	 */
	final Class<E> entityClass;

	/**
	 * AbstractRepository constructor
	 * 
	 * @param entityClass the entity class
	 */
	public AbstractCRUDRepository(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * AbstractRepository constructor
	 * 
	 * @param entityManager the entity manager (must no be null)
	 * @param entityClass   the entity class
	 * 
	 */
	public AbstractCRUDRepository(EntityManager entityManager, Class<E> entityClass) {
		this.entityManager = entityManager;
		this.entityClass = entityClass;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E create(E entity) throws CredibilityException {
		getEntityManager().getTransaction().begin();
		try {
			getEntityManager().persist(entity);
			getEntityManager().getTransaction().commit();
		} catch (ConstraintViolationException e) {

			// construct error message
			StringBuilder str = new StringBuilder("One or more Bean Validation constraints were violated:"); //$NON-NLS-1$
			for (ConstraintViolation<?> c : e.getConstraintViolations()) {
				str.append("\n- ").append(RscTools.getString(c.getMessageTemplate())); //$NON-NLS-1$
			}

			// rollback
			if (getEntityManager().getTransaction().isActive()) {
				getEntityManager().getTransaction().rollback();
			}

			// rethrow
			throw new CredibilityException(str.toString(), e);
		}
		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<E> findAll() {
		TypedQuery<E> query = getEntityManager()
				.createQuery(MessageFormat.format(QUERY_SELECT_ALL, entityClass.getSimpleName()), entityClass);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E findById(I id) {
		return getEntityManager().find(entityClass, id);
	}

	/**
	 * Adds filters to a query string.
	 * 
	 * @param filters             the filters
	 * @param queryString         the query to append the filters
	 * @param paramSuffixVariable the suffix to use in the query to retrieve the
	 *                            values
	 * @return The query string suffixed by the filtering clause
	 */
	private String addWhereFiltersToQuery(Map<EntityFilter, Object> filters, String queryString,
			String paramSuffixVariable) {

		StringBuilder where = new StringBuilder();

		if (null != filters && !filters.isEmpty()) {
			where.append(" WHERE ");//$NON-NLS-1$
			int i = 0;
			for (Entry<EntityFilter, Object> filter : filters.entrySet()) {
				if (0 < i) {
					where.append(" AND ");//$NON-NLS-1$
				}
				// Add where clause
				if (null == filter.getValue() || filter.getValue().equals(NullParameter.NULL)) {
					where.append(ALIAS + "." + filter.getKey().getField() + " IS NULL");//$NON-NLS-1$ //$NON-NLS-2$
				} else if (filter.getValue().equals(NullParameter.NOT_NULL)) {
					where.append(ALIAS + "." + filter.getKey().getField() + " IS NOT NULL");//$NON-NLS-1$ //$NON-NLS-2$
				} else {
					where.append(ALIAS + "." + filter.getKey().getField() + "=:" + filter.getKey().getField() //$NON-NLS-1$ //$NON-NLS-2$
							+ paramSuffixVariable);
				}
				i++;
			}
			queryString += where.toString();
		}
		return queryString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<E> findBy(Map<EntityFilter, Object> filters) {
		return findBy(filters, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<E> findBy(Map<EntityFilter, Object> filters, Map<EntityFilter, String> order) {
		// Prepare query string
		String queryString = MessageFormat.format(QUERY_SELECT_ALL, entityClass.getSimpleName());

		String paramSuffixVariable = "Param";//$NON-NLS-1$

		// Has filters
		queryString = addWhereFiltersToQuery(filters, queryString, paramSuffixVariable);

		// Order by
		if (order != null && !order.isEmpty()) {
			StringBuilder orderBy = new StringBuilder();
			orderBy.append(" " + QUERY_ORDER_BY_KEY + " "); //$NON-NLS-1$ //$NON-NLS-2$
			order.forEach((EntityFilter field, String direction) -> {
				orderBy.append(ALIAS + "." + field.getField()); //$NON-NLS-1$
				orderBy.append(" " + ((direction != null) ? direction : QUERY_ORDER_BY_DIR)); //$NON-NLS-1$
			});
			queryString += orderBy.toString();
		}

		// Create Query
		TypedQuery<E> query = getEntityManager().createQuery(queryString, entityClass);

		// Add parameters if has filters
		if (null != filters && !filters.isEmpty()) {
			for (Entry<EntityFilter, Object> filter : filters.entrySet()) {
				if (null != filter.getValue() && !(filter.getValue() instanceof NullParameter)) {
					query.setParameter(filter.getKey().getField() + paramSuffixVariable, filter.getValue());
				}
			}
		}

		// Return result
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(E entity) {
		if (entity != null) {
			entity = this.merge(entity);
			getEntityManager().getTransaction().begin();
			getEntityManager().remove(entity);
			getEntityManager().getTransaction().commit();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E update(E entityUpdated) throws CredibilityException {

		E entityToUpdate = null;

		if (entityUpdated != null) {

			I id = getEntityId(entityUpdated);
			String idFieldName = getEntityIdFieldName();

			// get actual entity from database
			entityToUpdate = findById(id);

			if (entityToUpdate == null) {
				throw new CredibilityException(
						RscTools.getString(RscConst.EX_DAO_CRUD_ENTITYNOTFOUND, idFieldName, id));
			}

			/*
			 * between entityManager transaction begin and commit, all setted values are
			 * automatically persisted in database
			 */
			getEntityManager().getTransaction().begin();

			// update all fields except @Id of selected entity
			for (Field field : entityUpdated.getClass().getDeclaredFields()) {

				// ignore static fields and id
				/* id field must not be updated */
				if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())
						&& !field.getName().equals(idFieldName)) {

					PropertyDescriptor pdEntityUpdated;
					PropertyDescriptor pdObjectToUpdate;

					try {

						pdEntityUpdated = new PropertyDescriptor(field.getName(), entityUpdated.getClass());
						pdObjectToUpdate = new PropertyDescriptor(field.getName(), entityToUpdate.getClass());

						// set entityToUpdate with entityUpdated values
						pdObjectToUpdate.getWriteMethod().invoke(entityToUpdate,
								pdEntityUpdated.getReadMethod().invoke(entityUpdated));

					} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}

			/*
			 * commit transaction to persist entityToUpdate modifications
			 */
			try {
				getEntityManager().getTransaction().commit();
			} catch (RollbackException e) {

				// construct error message
				StringBuilder str = new StringBuilder(); // $NON-NLS-1$
				if (e.getCause() instanceof ConstraintViolationException) {
					str.append("One or more Bean Validation constraints were violated:"); //$NON-NLS-1$
					for (ConstraintViolation<?> c : ((ConstraintViolationException) e.getCause())
							.getConstraintViolations()) {
						str.append("\n- ").append(RscTools.getString(c.getMessageTemplate())); //$NON-NLS-1$
					}
				}

				// rollback
				if (getEntityManager().getTransaction().isActive()) {
					getEntityManager().getTransaction().rollback();
				}

				// rethrow
				if (e.getCause() instanceof ConstraintViolationException) {
					throw new CredibilityException(str.toString(), e.getCause());
				} else {
					throw new CredibilityException(e.getCause());
				}
			}
		}
		return entityToUpdate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh(E entity) {
		if (entity != null) {
			entity = this.merge(entity);
			getEntityManager().refresh(entity);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E merge(E entity) {
		if (!getEntityManager().contains(entity) && getEntityManager().find(entityClass, getEntityId(entity)) != null) {
			entity = getEntityManager().merge(entity);
		}
		return entity;
	}

	/**
	 * @param entity the entity to parse
	 * @return the entity id
	 */
	@SuppressWarnings("unchecked")
	private I getEntityId(E entity) {

		I id = null;

		if (entity != null) {
			try {
				Field field = getEntityIdField();

				if (field != null) {
					id = (I) new PropertyDescriptor(field.getName(), entity.getClass()).getReadMethod().invoke(entity);
				}
			} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| IntrospectionException e) {
				logger.error(e.getMessage(), e);
			}
		}

		return id;
	}

	/**
	 * @param entity the entity to parse
	 * @return the entity field name
	 */
	private String getEntityIdFieldName() {

		List<Field> idField = null;

		idField = getAllFields(entityClass).stream()
				.filter(field -> field != null && field.isAnnotationPresent(Id.class)).collect(Collectors.toList());

		return idField != null && !idField.isEmpty() && idField.get(0) != null ? idField.get(0).getName() : FIELD_EMPTY;
	}

	/**
	 * @return the entity field
	 */
	private Field getEntityIdField() {

		List<Field> idField = null;

		idField = getAllFields(entityClass).stream()
				.filter(field -> field != null && field.isAnnotationPresent(Id.class)).collect(Collectors.toList());

		return idField != null && !idField.isEmpty() ? idField.get(0) : null;
	}

	/**
	 * @param type the class type
	 * @return the list of all fields with the inherited ones
	 */
	public static List<Field> getAllFields(Class<?> type) {
		return getAllFields(new ArrayList<>(), type);
	}

	/**
	 * Recursive method
	 * 
	 * @param fields the existing fields
	 * @param type   the class type to find types for
	 * @return the list of fields of the class type + the list of fields
	 */
	private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		if (type.getSuperclass() != null) {
			getAllFields(fields, type.getSuperclass());
		}

		return fields;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUnique(EntityFilter field, Object value) {
		return isUniqueExcept(field, null, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUniqueExcept(EntityFilter field, Integer[] id, Object value) {

		// check param
		if (value == null) {
			logger.error("value to check is null"); //$NON-NLS-1$
			return true;
		}
		if (field == null) {
			logger.error("field to check is null"); //$NON-NLS-1$
			return true;
		}

		String paramId = "paramId"; //$NON-NLS-1$
		String paramToCheck = "paramToCheck"; //$NON-NLS-1$

		List<Integer> listWithoutNulls = null;
		if (id != null) {
			listWithoutNulls = Arrays.asList(id).parallelStream().filter(Objects::nonNull).collect(Collectors.toList());
		}

		TypedQuery<Integer> query = null;
		if (listWithoutNulls == null || listWithoutNulls.isEmpty()) {
			query = getEntityManager().createQuery(MessageFormat.format(QUERY_FINDID_BY_FIELD,
					entityClass.getSimpleName(), field.getField(), paramToCheck), Integer.class);
			query.setParameter(paramToCheck, value);
		} else {
			query = getEntityManager().createQuery(MessageFormat.format(QUERY_FINDID_BY_FIELD_EXCEPT_ID,
					entityClass.getSimpleName(), field.getField(), paramToCheck, getEntityIdFieldName(), paramId),
					Integer.class);
			query.setParameter(paramId, listWithoutNulls);
			query.setParameter(paramToCheck, value);
		}

		return query.getResultList() == null || query.getResultList().isEmpty();
	}
}
