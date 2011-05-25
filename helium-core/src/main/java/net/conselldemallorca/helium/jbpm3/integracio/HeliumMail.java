/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import net.conselldemallorca.helium.core.model.dao.DaoProxy;
import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmException;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.VariableResolver;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.mail.AddressResolver;
import org.jbpm.util.XmlUtil;
import org.springframework.mail.MailException;

/**
 * Enviament de correus des de jBPM amb Helium
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("unchecked")
public class HeliumMail implements ActionHandler {

	String template = null;
	String actors = null;
	String to = null;
	String bcc = null;
	String bccActors = null;
	String subject = null;
	String text = null;

	ExecutionContext executionContext = null;

	public HeliumMail() {
	}

	public HeliumMail(String template, String actors, String to, String subject, String text) {
		this.template = template;
		this.actors = actors;
		this.to = to;
		this.subject = subject;
		this.text = text;
	}

	public HeliumMail(String template, String actors, String to, String bccActors, String bcc, String subject, String text) {
		this.template = template;
		this.actors = actors;
		this.to = to;
		this.bccActors = bccActors;
		this.bcc = bcc;
		this.subject = subject;
		this.text = text;
	}

	public void execute(ExecutionContext executionContext) {
		this.executionContext = executionContext;
		send();
	}

	public void send() {
		if (template != null) {
			Properties properties = getMailTemplateProperties(template);
			if (actors == null) {
				actors = properties.getProperty("actors");
			}
			if (to == null) {
				to = properties.getProperty("to");
			}
			if (subject == null) {
				subject = properties.getProperty("subject");
			}
			if (text == null) {
				text = properties.getProperty("text");
			}
			if (bcc == null) {
				bcc = properties.getProperty("bcc");
			}
			if (bccActors == null) {
				bccActors = properties.getProperty("bccActors");
			}
		}
		send(
				getFromAddress(),
				getRecipients(),
				getBccRecipients(),
				getSubject(),
				getText());
	}

	public void send(
			String fromAddress,
			List recipients,
			List bccRecipients,
			String subject,
			String text) {
		if (((recipients == null) || (recipients.isEmpty())) && ((bccRecipients == null) || (bccRecipients.isEmpty()))) {
			logger.debug("El missatge no s'envia perquè no hi ha destinataris");
			return;
		}
	    try {
	    	int retries = getRetries();
	    	while (0 < retries) {
	    		retries--;
	    		try {
	    			DaoProxy.getInstance().getMailDao().send(
	    					fromAddress,
	    					recipients,
	    					null,
	    					bccRecipients,
	    					subject,
	    					text);
	    			break;
	    		} catch (MailException msgex) {
	    			if (retries == 0)
	    				throw msgex;
	    			logger.error("No s'ha pogut enviar el missatge, reintentant: " + msgex);
	    			Thread.sleep(1000);
	    		}
	    	}
	    } catch (Exception e) {
	    	throw new JbpmException("No s'ha pogut enviar el missatge", e);
	    }
	}

	public List getRecipients() {
		List recipients = new ArrayList();
		if (actors != null) {
			String evaluatedActors = evaluate(actors);
			List tokenizedActors = tokenize(evaluatedActors);
			if (tokenizedActors != null) {
				recipients.addAll(resolveAddresses(tokenizedActors));
			}
		}
		if (to != null) {
			String resolvedTo = evaluate(to);
			recipients.addAll(tokenize(resolvedTo));
		}
		return recipients;
	}

	public List getBccRecipients() {
		List recipients = new ArrayList();
		if (bccActors != null) {
			String evaluatedActors = evaluate(bccActors);
			List tokenizedActors = tokenize(evaluatedActors);
			if (tokenizedActors != null) {
				recipients.addAll(resolveAddresses(tokenizedActors));
			}
		}
		if (bcc != null) {
			String resolvedTo = evaluate(bcc);
			recipients.addAll(tokenize(resolvedTo));
		}
	    if (JbpmConfiguration.Configs.hasObject("jbpm.mail.bcc.address")) {
	    	recipients.addAll(tokenize(JbpmConfiguration.Configs.getString("jbpm.mail.bcc.address")));
	    }
	    return recipients;
	}

	public String getSubject() {
		if (subject == null)
			return null;
		return evaluate(subject);
	}

	public String getText() {
		if (text == null)
			return null;
		return evaluate(text);
	}

	public String getFromAddress() {
		String globalFrom = GlobalProperties.getInstance().getProperty("app.correu.remitent");
		if (globalFrom != null)
			return globalFrom;
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.from.address"))
			return JbpmConfiguration.Configs.getString("jbpm.mail.from.address");
		return "jbpm@noreply";
	}
	
	public int getRetries() {
		String retries = GlobalProperties.getInstance().getProperty("app.correu.reintents");
		if (retries != null) {
			try {
				return Integer.parseInt(retries);
			} catch (Exception ignored) {}
		}
		return 5;
	}

	protected List tokenize(String text) {
		if (text == null) {
			return null;
		}
		List list = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(text, ";:");
		while (tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken());
		}
		return list;
	}

	protected Collection resolveAddresses(List actorIds) {
		List emailAddresses = new ArrayList();
		Iterator iter = actorIds.iterator();
		while (iter.hasNext()) {
			String actorId = (String)iter.next();
			AddressResolver addressResolver = (AddressResolver)JbpmConfiguration.Configs.getObject("jbpm.mail.address.resolver");
			Object resolvedAddresses = addressResolver.resolveAddress(actorId);
			if (resolvedAddresses != null) {
				if (resolvedAddresses instanceof String) {
					emailAddresses.add((String)resolvedAddresses);
				} else if (resolvedAddresses instanceof Collection) {
					emailAddresses.addAll((Collection)resolvedAddresses);
				} else if (resolvedAddresses instanceof String[]) {
					emailAddresses.addAll(Arrays.asList((String[])resolvedAddresses));
				} else {
					throw new JbpmException(
							"Address resolver '" + addressResolver +
							"' returned '" + resolvedAddresses.getClass().getName() +
							"' instead of a String, Collection or String-array: " + resolvedAddresses);
				}
			}
		}
		return emailAddresses;
	}


	static Map templates = null;
	static Map templateVariables = null;

	synchronized Properties getMailTemplateProperties(String templateName) {
		if (templates == null) {
			templates = new HashMap();
			String mailTemplatesResource = JbpmConfiguration.Configs.getString("resource.mail.templates");
			org.w3c.dom.Element mailTemplatesElement = XmlUtil.parseXmlResource(mailTemplatesResource, false).getDocumentElement();
			List mailTemplateElements = XmlUtil.elements(mailTemplatesElement, "mail-template");
			Iterator iter = mailTemplateElements.iterator();
			while (iter.hasNext()) {
				org.w3c.dom.Element mailTemplateElement = (org.w3c.dom.Element)iter.next();
				Properties templateProperties = new Properties();
				addTemplateProperty(mailTemplateElement, "actors", templateProperties);
				addTemplateProperty(mailTemplateElement, "to", templateProperties);
				addTemplateProperty(mailTemplateElement, "subject", templateProperties);
				addTemplateProperty(mailTemplateElement, "text", templateProperties);
				addTemplateProperty(mailTemplateElement, "bcc", templateProperties);
				addTemplateProperty(mailTemplateElement, "bccActors", templateProperties);
				templates.put(mailTemplateElement.getAttribute("name"), templateProperties);
			}
			templateVariables = new HashMap();
			List variableElements = XmlUtil.elements(mailTemplatesElement, "variable");
			iter = variableElements.iterator();
			while (iter.hasNext()) {
				org.w3c.dom.Element variableElement = (org.w3c.dom.Element)iter.next();
				templateVariables.put(variableElement.getAttribute("name"), variableElement.getAttribute("value"));
			}
			templateVariables.put("heliumBaseUrl", GlobalProperties.getInstance().getProperty("app.base.url"));
		}
		return (Properties)templates.get(templateName);
	}

	void addTemplateProperty(org.w3c.dom.Element mailTemplateElement, String property, Properties templateProperties) {
		org.w3c.dom.Element element = XmlUtil.element(mailTemplateElement, property);
		if (element != null) {
			templateProperties.put(property, XmlUtil.getContentText(element));
		}
	}

	String evaluate(String expression) {
		if (expression == null) {
			return null;
		}
		VariableResolver variableResolver = JbpmExpressionEvaluator.getUsedVariableResolver();
		if (variableResolver != null) {
			variableResolver = new MailVariableResolver(templateVariables, variableResolver);
		}
		return (String)JbpmExpressionEvaluator.evaluate(expression, executionContext, variableResolver, JbpmExpressionEvaluator.getUsedFunctionMapper());
	}

	class MailVariableResolver implements VariableResolver, Serializable {
		private static final long serialVersionUID = 1L;
		Map templateVariables = null;
		VariableResolver variableResolver = null;

		public MailVariableResolver(Map templateVariables, VariableResolver variableResolver) {
			this.templateVariables = templateVariables;
			this.variableResolver = variableResolver;
		}

		public Object resolveVariable(String pName) throws ELException {
			if ((templateVariables != null) && (templateVariables.containsKey(pName))) {
				return templateVariables.get(pName);
			}
			return variableResolver.resolveVariable(pName);
		}
	}

	private static final Log logger = LogFactory.getLog(HeliumMail.class);

	private static final long serialVersionUID = 1L;

}
