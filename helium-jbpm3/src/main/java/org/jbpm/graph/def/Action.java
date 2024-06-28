/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jbpm.graph.def;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.dom4j.Element;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.jpdl.xml.Parsable;
import org.jbpm.util.EqualsUtil;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import net.conselldemallorca.helium.jbpm3.api.HeliumActionHandler;
import net.conselldemallorca.helium.jbpm3.api.HeliumApi;
import net.conselldemallorca.helium.jbpm3.api.HeliumApiImpl;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

public class Action implements ActionHandler, Parsable, Serializable {

  private static final long serialVersionUID = 1L;
  
  long id = 0;
  protected String name = null;
  protected boolean isPropagationAllowed = true;
  protected boolean isAsync = false;
  protected boolean isAsyncExclusive = false;
  protected Action referencedAction = null;
  protected Delegation actionDelegation  = null;
  protected String actionExpression = null;
  protected Event event = null;
  protected ProcessDefinition processDefinition = null;
  
  public Action() {
  }

  public Action(Delegation actionDelegate) {
    this.actionDelegation = actionDelegate;
  }
  
  public String toString() {
    String toString = null;
    if (name!=null) {
      toString = "action["+name+"]";
    } else if (actionExpression!=null) {
      toString = "action["+actionExpression+"]";
    } else {
      String className = getClass().getName(); 
      className = className.substring(className.lastIndexOf('.')+1);
      if (name!=null) {
        toString = className+"("+name+")";
      } else {
        toString = className+"("+Integer.toHexString(System.identityHashCode(this))+")";
      }
    }
    return toString;
  }

  public void read(Element actionElement, JpdlXmlReader jpdlReader) {
    String expression = actionElement.attributeValue("expression");
    if (expression!=null) {
      actionExpression = expression;

    } else if (actionElement.attribute("ref-name")!=null) {
      jpdlReader.addUnresolvedActionReference(actionElement, this);

    } else if (actionElement.attribute("class")!=null) {
      actionDelegation = new Delegation();
      actionDelegation.read(actionElement, jpdlReader);
      
    } else {
      jpdlReader.addWarning("action does not have class nor ref-name attribute "+actionElement.asXML());
    }

    String acceptPropagatedEvents = actionElement.attributeValue("accept-propagated-events");
    if ("false".equalsIgnoreCase(acceptPropagatedEvents)
        || "no".equalsIgnoreCase(acceptPropagatedEvents) 
        || "off".equalsIgnoreCase(acceptPropagatedEvents)) {
      isPropagationAllowed = false;
    }

    String asyncText = actionElement.attributeValue("async");
    if ("true".equalsIgnoreCase(asyncText)) {
      isAsync = true;
    } else if ("exclusive".equalsIgnoreCase(asyncText)) {
      isAsync = true;
      isAsyncExclusive = true;
    }
  }

  public void write(Element actionElement) {
    if (actionDelegation!=null) {
      actionDelegation.write(actionElement);
    }
  }

  public void execute(ExecutionContext executionContext) throws Exception {
    ClassLoader surroundingClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(JbpmConfiguration.getProcessClassLoader(executionContext.getProcessDefinition()));

      if (referencedAction != null) {
        referencedAction.execute(executionContext);

      } else if (actionExpression != null) {
        JbpmExpressionEvaluator.evaluate(actionExpression, executionContext);

      } else if (actionDelegation != null) {
        ActionHandler actionHandler = (ActionHandler) actionDelegation.getInstance();

        // Es comprova que s'hagi trobat l'acció
        if (actionHandler == null) {
        	throw new JbpmException("No és pot executar l'acció \"" + this.getProcessDefinition() + "\" de la definició " + executionContext.getProcessDefinition().getName() + " v" + executionContext.getProcessDefinition().getVersion() 
        							+ ". No s'ha trobat el handler \"" + actionDelegation.getClassName() + "\""
        							+ (this.getEvent() != null ? " a l'event \"" + this.getEvent() + "\" del node \"" + this.getEvent().getGraphElement()  : "\"") + ".");
        }
        
        MetricRegistry metricRegistry = Jbpm3HeliumBridge.getInstanceService().getMetricRegistry();
        ProcessInstanceExpedient expedient = executionContext.getProcessInstance().getExpedient();
        
//        missatge d'execució en segón pla
        Long tokenId = executionContext.getToken().getId();
        Long taskId = Jbpm3HeliumBridge.getInstanceService().getTaskInstanceIdByTokenId(tokenId);
        boolean isTascaEnSegonPla =  Jbpm3HeliumBridge.getInstanceService().isTascaEnSegonPla(taskId);
        
        if (isTascaEnSegonPla) {
        	DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        	String dataHandler = df.format(new Date());
        	String errorText = "Executant handler " + actionHandler.getClass().getName() + "...";
        	Jbpm3HeliumBridge.getInstanceService().addMissatgeExecucioTascaSegonPla(taskId, new String[]{dataHandler, errorText});
        }
        
        final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						Action.class,
						"handler"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						Action.class,
						"handler.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						Action.class,
						"handler",
						expedient.getEntorn().getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						Action.class,
						"handler.count",
						expedient.getEntorn().getCodi()));
		countEntorn.inc();
		final Timer timerTipexp = metricRegistry.timer(
				MetricRegistry.name(
						Action.class,
						"handler",
						expedient.getEntorn().getCodi(),
						expedient.getTipus().getCodi()));
		final Timer.Context contextTipexp = timerTipexp.time();
		Counter countTipexp = metricRegistry.counter(
				MetricRegistry.name(
						Action.class,
						"handler.count",
						expedient.getEntorn().getCodi(),
						expedient.getTipus().getCodi()));
		countTipexp.inc();
		try {
			// Si el handler implementa HeliumActionhandler passarem l'HeliumApi enlloc de l'ExecutionContext 
			if (actionHandler instanceof HeliumActionHandler) {
				HeliumApi heliumApi = new HeliumApiImpl(executionContext);
				((HeliumActionHandler)actionHandler).execute(heliumApi);
			} else {
				actionHandler.execute(executionContext);
			}
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
			contextTipexp.stop();
		}
      }
    } finally {
      Thread.currentThread().setContextClassLoader(surroundingClassLoader);
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void setName(String name) {
    // if the process definition is already set
    if (processDefinition!=null) {
      // update the process definition action map
      Map actionMap = processDefinition.getActions();
      // the != string comparison is to avoid null pointer checks.  it is no problem if the body is executed a few times too much :-)
      if ( (this.name != name)
           && (actionMap!=null) ) {
        actionMap.remove(this.name);
        actionMap.put(name, this);
      }
    }

    // then update the name
    this.name = name;
  }
  
  // equals ///////////////////////////////////////////////////////////////////
  // hack to support comparing hibernate proxies against the real objects
  // since this always falls back to ==, we don't need to overwrite the hashcode
  public boolean equals(Object o) {
    return EqualsUtil.equals(this, o);
  }
  
  // getters and setters //////////////////////////////////////////////////////

  public boolean acceptsPropagatedEvents() {
    return isPropagationAllowed;
  }

  public boolean isPropagationAllowed() {
    return isPropagationAllowed;
  }
  public void setPropagationAllowed(boolean isPropagationAllowed) {
    this.isPropagationAllowed = isPropagationAllowed;
  }

  public long getId() {
    return id;
  }
  public String getName() {
    return name;
  }
  public Event getEvent() {
    return event;
  }
  public ProcessDefinition getProcessDefinition() {
    return processDefinition;
  }
  public void setProcessDefinition(ProcessDefinition processDefinition) {
    this.processDefinition = processDefinition;
  }
  public Delegation getActionDelegation() {
    return actionDelegation;
  }
  public void setActionDelegation(Delegation instantiatableDelegate) {
    this.actionDelegation = instantiatableDelegate;
  }
  public Action getReferencedAction() {
    return referencedAction;
  }
  public void setReferencedAction(Action referencedAction) {
    this.referencedAction = referencedAction;
  }
  public boolean isAsync() {
    return isAsync;
  }
  public boolean isAsyncExclusive() {
    return isAsyncExclusive;
  }
  public String getActionExpression() {
    return actionExpression;
  }
  public void setActionExpression(String actionExpression) {
    this.actionExpression = actionExpression;
  }
  public void setEvent(Event event) {
    this.event = event;
  }
  public void setAsync(boolean isAsync) {
    this.isAsync = isAsync;
  }
}
