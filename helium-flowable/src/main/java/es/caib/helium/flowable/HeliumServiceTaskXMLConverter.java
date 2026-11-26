package es.caib.helium.flowable;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.converter.ServiceTaskXMLConverter;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionAttribute;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FieldExtension;
import org.flowable.bpmn.model.ImplementationType;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.engine.delegate.JavaDelegate;

import es.caib.helium.bpmn.handler.HeliumBpmnHandler;
import es.caib.helium.bpmn.handler.HeliumBpmnXml;

/**
 * Conversor XML de les service tasks que entén l'extensió de Helium.
 *
 * <p>Flowable només sap executar service tasks amb atributs propis ({@code flowable:class},
 * {@code flowable:delegateExpression}...) i el seu validador rebutja la resta abans de
 * desplegar. Aquest conversor tradueix, en el moment de llegir el XML, les service tasks que
 * declaren un {@code <helium:handler>} a una service task de tipus {@code delegateExpression}
 * que apunta a {@link HeliumHandlerJavaDelegate}. Així el XML desplegat continua sent BPMN 2.0
 * genèric i el validador i el parser de Flowable veuen una tasca normal.</p>
 *
 * <p>Per compatibilitat amb les definicions ja desplegades, les tasques en format antic
 * ({@code flowable:class} + {@code flowable:field}) que apunten a un handler de Helium també es
 * redirigeixen a l'adaptador. La resta de service tasks amb atributs de Flowable no es modifiquen.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumServiceTaskXMLConverter extends ServiceTaskXMLConverter {

	private static volatile boolean registrat;

	/**
	 * Registra el conversor a {@link BpmnXMLConverter}. El registre de Flowable és estàtic,
	 * així que afecta tots els {@link BpmnXMLConverter} de la JVM. És idempotent.
	 */
	public static synchronized void registrar() {
		if (!registrat) {
			BpmnXMLConverter.addConverter(new HeliumServiceTaskXMLConverter());
			registrat = true;
		}
	}

	@Override
	protected BaseElement convertXMLToElement(XMLStreamReader xtr, BpmnModel model) throws Exception {
		// S'ha de llegir abans de super, que avança el lector pels elements fills
		String expressio = xtr.getAttributeValue(HeliumBpmnXml.NAMESPACE, ATTR_EXPRESSION);
		BaseElement element = super.convertXMLToElement(xtr, model);
		if (element instanceof ServiceTask) {
			ServiceTask serviceTask = (ServiceTask) element;
			if (!adaptarExpressioHelium(serviceTask, expressio)) {
				adaptarHandlerHelium(serviceTask);
			}
		}
		return element;
	}

	/** Atribut {@code helium:expression} de les service tasks de tipus expressió. */
	public static final String ATTR_EXPRESSION = "expression";
	private static final String ATTR_RESULT_VARIABLE_NAME = "resultVariableName";

	/**
	 * Service task de tipus expressió ({@code helium:expression}): l'executa {@link HeliumHandlerJavaDelegate}, que
	 * desa el resultat a les dades de l'expedient (i no a una variable del motor). L'expressió es deixa com a
	 * atribut de la tasca per poder-la llegir en temps d'execució.
	 *
	 * @return true si la tasca és una expressió de Helium.
	 */
	static boolean adaptarExpressioHelium(ServiceTask serviceTask, String expressio) {
		if (StringUtils.isEmpty(expressio)
				|| StringUtils.isNotEmpty(serviceTask.getType())
				|| StringUtils.isNotEmpty(serviceTask.getImplementationType())) {
			return false;
		}
		serviceTask.addAttribute(atributHelium(ATTR_EXPRESSION, expressio));
		if (StringUtils.isNotEmpty(serviceTask.getResultVariableName())) {
			// Flowable no admet resultVariableName amb delegateExpression: el desa l'adaptador a les dades de l'expedient
			serviceTask.addAttribute(atributHelium(ATTR_RESULT_VARIABLE_NAME, serviceTask.getResultVariableName()));
			serviceTask.setResultVariableName(null);
		}
		serviceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
		serviceTask.setImplementation(HeliumHandlerJavaDelegate.DELEGATE_EXPRESSION);
		return true;
	}

	private static ExtensionAttribute atributHelium(String nom, String valor) {
		ExtensionAttribute atribut = new ExtensionAttribute();
		atribut.setName(nom);
		atribut.setNamespace(HeliumBpmnXml.NAMESPACE);
		atribut.setNamespacePrefix(HeliumBpmnXml.PREFIX);
		atribut.setValue(valor);
		return atribut;
	}

	/** Nom de la dada on es desa el resultat d'una expressió de Helium, o null si no se'n desa. */
	public static String resultVariableNameHelium(org.flowable.bpmn.model.FlowElement element) {
		String nom = element.getAttributeValue(HeliumBpmnXml.NAMESPACE, ATTR_RESULT_VARIABLE_NAME);
		return StringUtils.isNotBlank(nom) ? nom.trim() : null;
	}

	/** Expressió de Helium de la service task, o null si no n'és una. */
	public static String expressioHelium(org.flowable.bpmn.model.FlowElement element) {
		if (!(element instanceof ServiceTask)) {
			return null;
		}
		String expressio = element.getAttributeValue(HeliumBpmnXml.NAMESPACE, ATTR_EXPRESSION);
		return StringUtils.isNotEmpty(expressio) ? expressio : null;
	}

	static void adaptarHandlerHelium(ServiceTask serviceTask) {
		if (StringUtils.isNotEmpty(serviceTask.getType())) {
			return;
		}
		if (StringUtils.isEmpty(serviceTask.getImplementationType())) {
			if (FlowableHeliumBpmn.llegirHandler(serviceTask) == null) {
				return;
			}
		} else if (ImplementationType.IMPLEMENTATION_TYPE_CLASS.equals(serviceTask.getImplementationType())
				&& esHandlerHelium(serviceTask.getImplementation())) {
			// Format antic (flowable:class + flowable:field) apuntant a un handler de Helium: els handlers ja no
			// són JavaDelegate, així que es passen a l'adaptador perquè les definicions ja desplegades i les
			// instàncies en curs continuïn funcionant.
			serviceTask.addExtensionElement(handlerDesDeCampsFlowable(serviceTask));
			serviceTask.setFieldExtensions(new ArrayList<>());
		} else {
			// La tasca està definida amb atributs de Flowable que no corresponen a un handler de Helium
			return;
		}
		serviceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
		serviceTask.setImplementation(HeliumHandlerJavaDelegate.DELEGATE_EXPRESSION);
	}

	/** Indica si la classe és a l'aplicació i és un handler de Helium (i no un JavaDelegate de Flowable). */
	private static boolean esHandlerHelium(String classe) {
		if (StringUtils.isEmpty(classe)) {
			return false;
		}
		try {
			Class<?> c = Class.forName(classe.trim(), false, HeliumServiceTaskXMLConverter.class.getClassLoader());
			return HeliumBpmnHandler.class.isAssignableFrom(c) && !JavaDelegate.class.isAssignableFrom(c);
		} catch (ClassNotFoundException | LinkageError ex) {
			return false;
		}
	}

	private static ExtensionElement handlerDesDeCampsFlowable(ServiceTask serviceTask) {
		ExtensionElement handler = elementHelium(HeliumBpmnXml.ELEMENT_HANDLER);
		handler.addAttribute(atribut(HeliumBpmnXml.ATTR_CLASSE, serviceTask.getImplementation().trim()));
		for (FieldExtension camp : serviceTask.getFieldExtensions()) {
			ExtensionElement parametre = elementHelium(HeliumBpmnXml.ELEMENT_PARAMETRE);
			parametre.addAttribute(atribut(HeliumBpmnXml.ATTR_NOM, camp.getFieldName()));
			if (StringUtils.isNotEmpty(camp.getExpression())) {
				parametre.addAttribute(atribut(HeliumBpmnXml.ATTR_EXPRESSIO, camp.getExpression()));
			} else {
				parametre.addAttribute(atribut(HeliumBpmnXml.ATTR_VALOR, camp.getStringValue()));
			}
			handler.addChildElement(parametre);
		}
		return handler;
	}

	private static ExtensionElement elementHelium(String nom) {
		ExtensionElement element = new ExtensionElement();
		element.setNamespace(HeliumBpmnXml.NAMESPACE);
		element.setNamespacePrefix(HeliumBpmnXml.PREFIX);
		element.setName(nom);
		return element;
	}

	private static ExtensionAttribute atribut(String nom, String valor) {
		ExtensionAttribute atribut = new ExtensionAttribute(nom);
		atribut.setValue(valor);
		return atribut;
	}

}
