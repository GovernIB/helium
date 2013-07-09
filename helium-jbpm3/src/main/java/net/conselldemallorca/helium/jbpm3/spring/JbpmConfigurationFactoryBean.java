package net.conselldemallorca.helium.jbpm3.spring;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.configuration.ObjectFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * Factory bean that produces the (singleton) jBPM configuration bean.
 * 
 * Features : - returns the singleton JbpmConfiguration object for this application
 *            - allows the injection of the Spring-configured session factory, which will then be used by
 *              jBPM to access the database
 *            - allows to start a prefdefined nr of job executor threads and shuts them down properly
 *              when the application context goes doen.
 * 
 * @author Joram Barrez
 */
@SuppressWarnings("rawtypes")
public class JbpmConfigurationFactoryBean implements FactoryBean, InitializingBean, ApplicationListener {
	
	/** Logger for this class. */
	private static final Log LOG = LogFactory.getLog(JbpmConfigurationFactoryBean.class);
	
	/** The singleton object that this factory produces */
	private JbpmConfiguration jbpmConfiguration;
	
	/** The jBPM object factory */
	private ObjectFactory objectFactory;
	
	/** Indicates whether the job executor must be started */
	private boolean startJobExecutor;
	
	/** The Hibernate session factory used by jBPM and the application */
	private SessionFactory sessionFactory;

	@PersistenceContext
	private EntityManager entityManager;
	
	private SpringMassiuExecutor springMassiuExecutor;
	/**
	 * Default constructor.
	 */
	public JbpmConfigurationFactoryBean() {
		
	}
	
	public Object getObject() throws Exception {
		return jbpmConfiguration;
	}

	public Class getObjectType() {
		return JbpmConfiguration.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public void afterPropertiesSet() throws Exception {
		
		LOG.info("All properties set. Initializing the jBPM configuration");
		
		// Create jbpm Config object
		JbpmConfiguration.Configs.setDefaultObjectFactory(objectFactory);
		jbpmConfiguration = new JbpmConfiguration(objectFactory);
		
		// Inject session factory
		JbpmContext ctx = null;
		try {
			 ctx = jbpmConfiguration.createJbpmContext();
			 ctx.setSessionFactory(
					 new JbpmSessionFactory(
							 sessionFactory,
							 entityManager));
			 LOG.info("SessionFactory injected in the jBPM config. jBPM will now use this session factory "
					 + "to create its Hibernate sessions");
		} finally {
			if (ctx != null) {
				ctx.close();
			}
		}
		
		LOG.info("Starting accions massives ...");
		getMassiuExecutor().start();
		LOG.info("Accions massives started.");
		
		// Start job executor if needed
		if (startJobExecutor) {
			LOG.info("Starting job executor ...");
			jbpmConfiguration.startJobExecutor();
			LOG.info("Job executor started.");
		}
	}

	public synchronized SpringMassiuExecutor getMassiuExecutor() {
		if (springMassiuExecutor == null) {
			try {
				springMassiuExecutor = (SpringMassiuExecutor) this.objectFactory.createObject("jbpm.massiva.executor");
			} catch (ClassCastException e) {
				throw new JbpmException(
						"jbpm configuration object under key 'jbpm.massiva.executor' is not a "	+ SpringMassiuExecutor.class.getName(), e);
			}
		}
		return springMassiuExecutor;
	}

	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof ContextClosedEvent) {
			jbpmConfiguration.getJobExecutor().stop();
			getMassiuExecutor().stop();
		}
	}

	public void setObjectFactory(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	public void setStartJobExecutor(boolean startJobExecutor) {
		this.startJobExecutor = startJobExecutor;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
