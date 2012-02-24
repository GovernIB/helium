/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.io.Serializable;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.impl.AbstractQueryImpl;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.loader.criteria.CriteriaJoinWalker;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Classe que implementa el patró genericDao per Hibernate
 * agafat de https://www.hibernate.org/328.html
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HibernateGenericDao<T, ID extends Serializable> extends HibernateDaoSupport implements GenericDao<T, ID> {

	private Class<T> m_persistentClass;
	
	private NamedParameterJdbcTemplate jdbcTemplate;



	public HibernateGenericDao() {
	}

	public HibernateGenericDao(Class<T> p_persistentClass) {
		m_persistentClass = p_persistentClass;
	}

	@SuppressWarnings("unchecked")
	public T getById(ID p_id, boolean p_lock) {
		T entity;
		if (p_lock) {
			entity = (T)getSession().get(
					getPersistentClass(),
					p_id,
					LockMode.UPGRADE);
		} else {
			entity = (T)getSession().get(
					getPersistentClass(),
					p_id);
		}
		return entity;
	}

	public T saveOrUpdate(T p_entity) {
		getSession().saveOrUpdate(p_entity);
		return p_entity;
	}

	public T merge(T p_entity) {
		getSession().merge(p_entity);
		return p_entity;
	}

	public void evict(T p_entity) {
		getSession().evict(p_entity);
	}

	public void flush() {
		getSession().flush();
	}

	@SuppressWarnings("unchecked")
	public void delete(ID id) {
		T entity = (T)getSession().get(getPersistentClass(), id);
		if (entity != null)
			getSession().delete(entity);
	}

	public void delete(T p_entity) {
		getSession().delete(p_entity);
	}

	public List<T> findAll() {
		return findByCriteria();
	}
	public List<T> findOrderedAll(
			String sort[],
			boolean asc) {
		return findOrderedByCriteria(
				sort,
				asc);
	}
	public List<T> findPagedAndOrderedAll(
			String sort[],
			boolean asc,
			int firstRow,
			int maxResults) {
		return findPagedAndOrderedByCriteria(
				firstRow,
				maxResults,
				sort,
				asc);
	}

	public int getCountAll() {
		return ((Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				return getCountByCriteria();
			}
		})).intValue();
	}

	public List<T> findByExample(T p_exampleInstance) {
		return findByCriteria(Example.create(p_exampleInstance));
	}
	public List<T> findOrderedByExample(
			T p_exampleInstance,
			String[] sort,
			boolean asc) {
		return findOrderedByCriteria(
				sort,
				asc,
				Example.create(p_exampleInstance));
	}
	public List<T> findPagedAndOrderedByExample(
			T p_exampleInstance,
			String[] sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		return findPagedAndOrderedByCriteria(
				firstRow,
				maxResults,
				sort,
				asc,
				Example.create(p_exampleInstance));
	}
	public int getCountByExample(final T p_exampleInstance) {
		return ((Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				return getCountByCriteria(Example.create(p_exampleInstance));
			}
		})).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(Criterion... p_criterion) {
		Criteria crit = getSession().createCriteria(
				getPersistentClass());
		for (Criterion c: p_criterion) {
			crit.add(c);
		}
		return crit.list();
	}
	@SuppressWarnings("unchecked")
	public List<T> findOrderedByCriteria(
			String[] sort,
			boolean asc,
			Criterion... p_criterion) {
		Criteria crit = getSession().createCriteria(
				getPersistentClass());
		for (Criterion c: p_criterion) {
			crit.add(c);
		}
		if (sort != null) {
			for (String s: sort)
				addSort(crit, s, asc);
		}
		return crit.list();
	}
	@SuppressWarnings("unchecked")
	public List<T> findPagedAndOrderedByCriteria(
			int firstRow,
			int maxResults,
			String[] sort,
			boolean asc,
			Criterion... p_criterion) {
		Criteria crit = getSession().createCriteria(
				getPersistentClass());
		for (Criterion c: p_criterion) {
			crit.add(c);
		}
		if (sort != null) {
			for (String s: sort)
				addSort(crit, s, asc);
		}
		if (firstRow >= 0)
			crit.setFirstResult(firstRow);
		if (maxResults >= 0)
			crit.setMaxResults(maxResults);
		return crit.list();
	}
	public int getCountByCriteria(Criterion... p_criterion) {
		Criteria crit = getSession().createCriteria(
				getPersistentClass());
		for (Criterion c: p_criterion) {
			crit.add(c);
		}
		crit.setProjection(Projections.rowCount());
		Integer result = (Integer)crit.uniqueResult();
		return (result == null) ? 0 : result.intValue();
	}



	public Class<T> getPersistentClass() {
		return m_persistentClass;
	}

	public void setPersistentClass(Class<T> persistentClass) {
		m_persistentClass = persistentClass;
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	public NamedParameterJdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	public String getCriteriaSql(Criteria criteria) {
		CriteriaImpl criteriaImpl = (CriteriaImpl)criteria;
		SessionImplementor session = criteriaImpl.getSession();
		SessionFactoryImplementor factory = session.getFactory();
		CriteriaQueryTranslator translator = new CriteriaQueryTranslator(
				factory,
				criteriaImpl,
				criteriaImpl.getEntityOrClassName(),
				CriteriaQueryTranslator.ROOT_SQL_ALIAS);
		String[] implementors = factory.getImplementors(
				criteriaImpl.getEntityOrClassName());
		CriteriaJoinWalker walker = new CriteriaJoinWalker(
				(OuterJoinLoadable)factory.getEntityPersister(implementors[0]),
				translator,
				factory,
				criteriaImpl,
				criteriaImpl.getEntityOrClassName(),
				session.getEnabledFilters());
		return walker.getSQLString();
	}
	public static String getQuerySql(Query query) {
		String result = query.getQueryString();
		if(query instanceof AbstractQueryImpl) {
			Object[] values = ((AbstractQueryImpl)query).valueArray();
			for(Object value : values) {
				result = result.replaceFirst(
						"\\\\?",
						(value instanceof String) ? "'" + value + "'" : "" + value);
			}
		}
		return result;
	}



	protected void addSort(Criteria crit, String sort, boolean asc) {
		if (sort != null) {
			String column = null;
			if (sort.contains(".")) {
				String[] sortParts = sort.split("\\.");
				String sortAlias = sortParts[0].substring(0, 1);
				crit.createAlias(sortParts[0], sortAlias);
				column = sortAlias + "." + sortParts[1];
			} else {
				column = sort;
			}
			if (asc)
				crit.addOrder(Order.asc(column));
			else
				crit.addOrder(Order.desc(column));
		}
	}

}
