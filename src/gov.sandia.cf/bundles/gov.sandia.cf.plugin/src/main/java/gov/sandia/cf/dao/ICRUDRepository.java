/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.query.EntityFilter;

/**
 * Interface which give a basic CRUD toolset
 * 
 * @author Didier Verstraete
 *
 * @param <E> the persisting entity class type
 * @param <I> the entity id class type
 * 
 */
public interface ICRUDRepository<E, I> extends IRepository {
	/**
	 * @param entity
	 * 
	 *               Persists the @param entity in database using Jpa
	 * @return the created entity
	 * @throws CredibilityException throw credibility exception if the entity to
	 *                              create does not fit expected entity criteria.
	 */
	public E create(E entity) throws CredibilityException;

	/**
	 * @return all the entity from database
	 */
	public List<E> findAll();

	/**
	 * @param id the id of the entity to find
	 * 
	 * @return the entity with the param id, null if not present
	 */
	public E findById(I id);

	/**
	 * @param filters the filter map
	 * @return the elements that matches the filters in parameter
	 */
	List<E> findBy(Map<EntityFilter, Object> filters);

	/**
	 * @param filters the request filters
	 * @param order   the request order
	 * @return the elements that matches the filters in parameter and order by order
	 *         data
	 */
	List<E> findBy(Map<EntityFilter, Object> filters, Map<EntityFilter, String> order);

	/**
	 * Deletes the param entity from database
	 * 
	 * @param entity the entity to delete
	 */
	public void delete(E entity);

	/**
	 * Updates the param entityUpdated and persist modifications in database. The
	 * entity must exists in database and the entityUpdated must have the id field
	 * set
	 * 
	 * @param entityUpdated the entity to update
	 * @throws CredibilityException throw credibility exception if the entity to
	 *                              update does not fit expected entity criteria.
	 * 
	 * @return the entity updated
	 */
	public E update(E entityUpdated) throws CredibilityException;

	/**
	 * Refresh the entity after update.
	 * 
	 * @param entity the entity to refresh
	 */
	public void refresh(E entity);

	/**
	 * Merge the entity with the entity manager, reconnect with the database
	 * 
	 * @param entity the entity to merge
	 * 
	 * @return the merged entity
	 */
	public E merge(E entity);

	/**
	 * @param field the field to check
	 * @param value the value to check unicity
	 * @return true if the value is unique in the table entity and for the field
	 *         specified
	 */
	boolean isUnique(EntityFilter field, Object value);

	/**
	 * @param field the field to check
	 * @param id    the id array to do not search in
	 * @param value the value to check unicity
	 * @return true if the value is unique in the table entity and for the field
	 *         specified except for id array
	 */
	boolean isUniqueExcept(EntityFilter field, Integer[] id, Object value);
}
