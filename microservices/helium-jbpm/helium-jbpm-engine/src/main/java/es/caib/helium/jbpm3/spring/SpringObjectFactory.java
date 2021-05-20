package es.caib.helium.jbpm3.spring;

import org.jbpm.configuration.ObjectFactory;
import org.jbpm.configuration.ObjectFactoryParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Implementation of a jBPM objectfatory.
 * First, the Spring container is used to retrieve a certain bean.
 * If not found in the Spring container, the default jBPM object factory is consulted.
 * 
 * @author Joram Barrez
 */
@SuppressWarnings("serial")
public class SpringObjectFactory implements ObjectFactory, ApplicationContextAware {
	
	/** Spring container */
	private ApplicationContext applicationContext;
	
	/** Default jBPM object factory */
	private ObjectFactory jbpmObjectFactory;
	
	public SpringObjectFactory(String jbpmConfigFile) {
		this.jbpmObjectFactory = ObjectFactoryParser.parseResource(jbpmConfigFile);		
	}

	public Object createObject(String objectName) {
		if (applicationContext.containsBean(objectName)) { 
			return applicationContext.getBean(objectName);
		} else if (jbpmObjectFactory.hasObject(objectName)) {
			return jbpmObjectFactory.createObject(objectName);
		} else {
			return null;
		}
	}

	public boolean hasObject(String objectName) {
		return applicationContext.containsBean(objectName) || jbpmObjectFactory.hasObject(objectName);
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
