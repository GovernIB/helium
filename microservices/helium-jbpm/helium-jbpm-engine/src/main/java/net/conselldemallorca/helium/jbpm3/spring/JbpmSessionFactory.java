/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.spring;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.persistence.EntityManager;

import org.hibernate.Cache;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.TypeHelper;
import org.hibernate.classic.Session;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;

/**
 * SessionFactory per a fer feina amb l'EntityManager.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings({"serial", "rawtypes", "deprecation"})
public class JbpmSessionFactory implements SessionFactory {

	private SessionFactory delegate;
    private EntityManager entityManager;

	public JbpmSessionFactory(
			SessionFactory delegate,
			EntityManager entityManager) {
		this.delegate = delegate;
		this.entityManager = entityManager;
	}

	public Reference getReference() throws NamingException {
		return delegate.getReference();
	}

	public Session openSession() throws HibernateException {
		return delegate.openSession();
	}

	public Session getCurrentSession() throws HibernateException {
		return (Session)entityManager.getDelegate();
	}

	public ClassMetadata getClassMetadata(Class persistentClass)
			throws HibernateException {
		return delegate.getClassMetadata(persistentClass);
	}

	public ClassMetadata getClassMetadata(String entityName)
			throws HibernateException {
		return delegate.getClassMetadata(entityName);
	}

	public CollectionMetadata getCollectionMetadata(String roleName)
			throws HibernateException {
		return delegate.getCollectionMetadata(roleName);
	}

	public Map<String,ClassMetadata> getAllClassMetadata() throws HibernateException {
		return delegate.getAllClassMetadata();
	}

	public Map getAllCollectionMetadata() throws HibernateException {
		return delegate.getAllCollectionMetadata();
	}

	public Statistics getStatistics() {
		return delegate.getStatistics();
	}

	public void close() throws HibernateException {
		delegate.close();
	}

	public boolean isClosed() {
		return delegate.isClosed();
	}

	public void evict(Class persistentClass) throws HibernateException {
		delegate.evict(persistentClass);
	}

	public void evict(Class persistentClass, Serializable id)
			throws HibernateException {
		delegate.evict(persistentClass, id);
	}

	public void evictEntity(String entityName) throws HibernateException {
		delegate.evictEntity(entityName);
	}

	public void evictEntity(String entityName, Serializable id)
			throws HibernateException {
		delegate.evictEntity(entityName, id);
	}

	public void evictCollection(String roleName) throws HibernateException {
		delegate.evictCollection(roleName);
	}

	public void evictCollection(String roleName, Serializable id)
			throws HibernateException {
		delegate.evictCollection(roleName, id);
	}

	public void evictQueries() throws HibernateException {
		delegate.evictQueries();
	}

	public void evictQueries(String cacheRegion) throws HibernateException {
		delegate.evictQueries(cacheRegion);
	}

	public StatelessSession openStatelessSession() {
		return delegate.openStatelessSession();
	}

	public StatelessSession openStatelessSession(Connection connection) {
		return delegate.openStatelessSession(connection);
	}

	public Set getDefinedFilterNames() {
		return delegate.getDefinedFilterNames();
	}

	public FilterDefinition getFilterDefinition(String filterName)
			throws HibernateException {
		return delegate.getFilterDefinition(filterName);
	}

	public Cache getCache() {
		return delegate.getCache();
	}

	public boolean containsFetchProfileDefinition(String name) {
		return delegate.containsFetchProfileDefinition(name);
	}

	public TypeHelper getTypeHelper() {
		return delegate.getTypeHelper();
	}

	public Session openSession(Interceptor interceptor)
			throws HibernateException {
		return delegate.openSession(interceptor);
	}

	public Session openSession(Connection connection) {
		return delegate.openSession(connection);
	}

	public Session openSession(Connection connection, Interceptor interceptor) {
		return delegate.openSession(connection, interceptor);
	}

}
