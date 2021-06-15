package net.conselldemallorca.helium.jbpm3.spring;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Allows to use Spring-managed beans with injected dependencies within a jBPM process.
 * In the jBPM configuration, you just need to specify the name of the bean as declared in the application context:
 * 
 * <action class="be.jorambarrez.jbpm3_spring_integration.SpringDelegation" config-type="bean">
 * 		<beanName>mySpringBean</beanName>
 * </action>
 * 
 * @author Joram Barrez
 */
@SuppressWarnings("serial")
public class SpringDelegation implements ApplicationContextAware, DecisionHandler, 
									   ActionHandler, AssignmentHandler {
	
	/** The name of the bean as declared in the Spring config. */
	private String beanName;
	
	/** The injected application context, statically kept such that new instances easily can access it. */
	private static ApplicationContext APPLICATION_CTX;
	
	/** Local cache of the delegated bean */
	private Object bean;

	/** Delegation method for a DecisionHandler */
	public String decide(ExecutionContext executionContext) throws Exception {
		DecisionHandler dh = getBean();
		return dh.decide(executionContext);
	}

	/** Delegation for an ActionHandler */
	public void execute(ExecutionContext executionContext) throws Exception {
		ActionHandler ah = getBean();
		ah.execute(executionContext);
	}

	/** Delegation for an AssignmentHandler */
	public void assign(Assignable assignable, ExecutionContext executionContext) throws Exception {
		AssignmentHandler ah = getBean();
		ah.assign(assignable, executionContext);
	}
	
	/* GETTERS and SETTERS */
	
	@SuppressWarnings("unchecked")
	private <T> T getBean() {
		if (bean == null) {
			bean = (T) APPLICATION_CTX.getBean(beanName);
		}
		return (T) bean;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringDelegation.APPLICATION_CTX = applicationContext;
	}

	
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

}
