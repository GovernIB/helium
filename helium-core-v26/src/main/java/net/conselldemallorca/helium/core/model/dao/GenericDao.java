/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.Criterion;

/**
 * Interfície per al patró GenericDao
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GenericDao<T, ID extends Serializable> {

	T getById(ID id, boolean lock);

	T saveOrUpdate(T entity);

	void delete(T entity);

	void delete(ID id);

	List<T> findAll();
	List<T> findOrderedAll(String[] sort, boolean asc);
	public List<T> findPagedAndOrderedAll(String[] sort, boolean asc, int firstRow, int maxResults);
	
	List<T> findByExample(T exampleInstance);
	public List<T> findOrderedByExample(T p_exampleInstance, String[] sort, boolean asc);
	public List<T> findPagedAndOrderedByExample(T p_exampleInstance, String[] sort, boolean asc, int firstRow, int maxResults);

	List<T> findByCriteria(Criterion... p_criterion);
	public List<T> findOrderedByCriteria(String[] sort, boolean asc, Criterion... p_criterion);
	public List<T> findPagedAndOrderedByCriteria(int firstRow, int maxResults, String[] sort, boolean asc, Criterion... p_criterion);
	public int getCountByCriteria(Criterion... p_criterion);

	void setPersistentClass(Class<T> persistentClass);

}
