/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.io.Serializable;
import java.util.List;

import javax.sql.DataSource;

import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.impl.AbstractQueryImpl;
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

	public HibernateGenericDao(final Class<T> p_persistentClass) {
		m_persistentClass = p_persistentClass;
	}

	@SuppressWarnings("unchecked")
	public T getById(final ID p_id, final boolean p_lock) {
		T entity;
		if (p_lock) {
			entity = (T)getSession().get(
					getPersistentClass(),
					p_id,
					LockOptions.UPGRADE);
		} else {
			entity = (T)getSession().get(
					getPersistentClass(),
					p_id);
		}
		return entity;
	}

	public T saveOrUpdate(final T p_entity) {
		getSession().saveOrUpdate(p_entity);
		return p_entity;
	}

	public T merge(final T p_entity) {
		getSession().merge(p_entity);
		return p_entity;
	}

	public void evict(final T p_entity) {
		getSession().evict(p_entity);
	}

	public void flush() {
		getSession().flush();
	}

	@SuppressWarnings("unchecked")
	public void delete(final ID id) {
		final T entity = (T)getSession().get(getPersistentClass(), id);
		if (entity != null)
			getSession().delete(entity);
	}

	public void delete(final T p_entity) {
		getSession().delete(p_entity);
	}

	public List<T> findAll() {
		return findByCriteria();
	}
	public List<T> findOrderedAll(
			final String sort[],
			final boolean asc) {
		return findOrderedByCriteria(
				sort,
				asc);
	}
	public List<T> findPagedAndOrderedAll(
			final String sort[],
			final boolean asc,
			final int firstRow,
			final int maxResults) {
		return findPagedAndOrderedByCriteria(
				firstRow,
				maxResults,
				sort,
				asc);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int getCountAll() {
		return ((Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(final Session session) {
				return getCountByCriteria();
			}
		})).intValue();
	}

	public List<T> findByExample(final T p_exampleInstance) {
		return findByCriteria(Example.create(p_exampleInstance));
	}
	public List<T> findOrderedByExample(
			final T p_exampleInstance,
			final String[] sort,
			final boolean asc) {
		return findOrderedByCriteria(
				sort,
				asc,
				Example.create(p_exampleInstance));
	}
	public List<T> findPagedAndOrderedByExample(
			final T p_exampleInstance,
			final String[] sort,
			final boolean asc,
			final int firstRow,
			final int maxResults) {
		return findPagedAndOrderedByCriteria(
				firstRow,
				maxResults,
				sort,
				asc,
				Example.create(p_exampleInstance));
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int getCountByExample(final T p_exampleInstance) {
		return ((Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(final Session session) {
				return getCountByCriteria(Example.create(p_exampleInstance));
			}
		})).intValue();
	}

	
	public List<T> findByCriteria(final Criterion... p_criterion) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		addCriterion(crit, p_criterion);
		return findByCriteria(crit);
	}

	public List<T> findByCriteria(final Criteria crit) {
		return getResultList(crit);
	}

	public List<T> findOrderedByCriteria(
			final String[] sort,
			final boolean asc,
			final Criterion... p_criterion) {
		
		Criteria crit = getSession().createCriteria(getPersistentClass());
		addCriterion(crit, p_criterion);
		if (sort != null) {
			for (String s: sort)
				addSort(crit, s, asc);
		}
		
		return getResultList(crit);
	}
	
	public List<T> findPagedAndOrderedByCriteria(
			final int firstRow,
			final int maxResults,
			final String[] sort,
			final boolean asc,
			final Criterion... p_criterion) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		addCriterion(crit, p_criterion);
		return findPagedAndOrderedByCriteria(
				firstRow,
				maxResults,
				sort,
				asc,
				crit);
	}
	
	public List<T> findPagedAndOrderedByCriteria(
			final int firstRow,
			final int maxResults,
			final String[] sort,
			final boolean asc,
			final Criteria crit) {
		if (sort != null) {
			for (String s: sort)
				addSort(crit, s, asc);
		}
		if (firstRow >= 0)
			crit.setFirstResult(firstRow);
		if (maxResults >= 0)
			crit.setMaxResults(maxResults);
		
		return getResultList(crit);
	}
	
	public int getCountByCriteria(final Criterion... p_criterion) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		addCriterion(crit, p_criterion);
		return getCountByCriteria(crit);
	}
	
	public int getCountByCriteria(final Criteria crit) {
		crit.setProjection(Projections.rowCount());
		final Long result = (Long)crit.uniqueResult();
		return (result == null) ? 0 : result.intValue();
	}

	public Class<T> getPersistentClass() {
		return m_persistentClass;
	}

	public void setPersistentClass(final Class<T> persistentClass) {
		m_persistentClass = persistentClass;
	}

	public void setDataSource(final DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	public NamedParameterJdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	/*public String getCriteriaSql(final Criteria criteria) {
		final CriteriaImpl criteriaImpl = (CriteriaImpl)criteria;
		final SessionImplementor session = criteriaImpl.getSession();
		final SessionFactoryImplementor factory = session.getFactory();
		final CriteriaQueryTranslator translator = new CriteriaQueryTranslator(
				factory,
				criteriaImpl,
				criteriaImpl.getEntityOrClassName(),
				CriteriaQueryTranslator.ROOT_SQL_ALIAS);
		final String[] implementors = factory.getImplementors(
				criteriaImpl.getEntityOrClassName());
		final CriteriaJoinWalker walker = new CriteriaJoinWalker(
				(OuterJoinLoadable)factory.getEntityPersister(implementors[0]),
				translator,
				factory,
				criteriaImpl,
				criteriaImpl.getEntityOrClassName(),
				session.getEnabledFilters());
		return walker.getSQLString();
	}*/
	public static String getQuerySql(final Query query) {
		String result = query.getQueryString();
		if(query instanceof AbstractQueryImpl) {
			final Object[] values = ((AbstractQueryImpl)query).valueArray();
			for(Object value : values) {
				result = result.replaceFirst(
						"\\\\?",
						(value instanceof String) ? "'" + value + "'" : "" + value);
			}
		}
		return result;
	}



	protected void addSort(final Criteria crit, final String sort, final boolean asc) {
		if (sort != null) {
			String column = null;
			if (sort.contains(".")) {
				final String[] sortParts = sort.split("\\.");
				final String sortAlias = sortParts[0].substring(0, 1);
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

	protected void addCriterion(Criteria crit, final Criterion[] p_criterion) {
		for (Criterion c: p_criterion) {
			crit.add(c);
		}
	}

	@SuppressWarnings("unchecked")
	private List<T> getResultList(final Criteria crit) {
		List<T> result = null;
		// Comprobamos si la entidad es cacheable
		if (getPersistentClass().getAnnotation(org.hibernate.annotations.Cache.class) == null || !isCached()) {
			result = crit.list();
		} else {
			result = crit.setCacheable(true).list();
		}
		return result;
	}
	
	private boolean isCached() {
		String cached = GlobalProperties.getInstance().getProperty("app.hibernate.cache");
		if (cached == null)
			cached = "false";
		boolean isCached = "true".equalsIgnoreCase(cached);
		if (!isCached) {
			getSession().setCacheMode(CacheMode.IGNORE);
		}
		return isCached;
	}
}
