package es.caib.helium.logic.flowable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.ExtensionAttribute;
import org.flowable.bpmn.model.ExtensionElement;

import es.caib.helium.bpmn.handler.HandlerDefinicio;
import es.caib.helium.bpmn.handler.HeliumBpmnXml;

/**
 * Lectura de l'extensió de Helium ({@link HeliumBpmnXml}) a partir del model BPMN de Flowable.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public final class FlowableHeliumBpmn {

	private FlowableHeliumBpmn() {}

	/**
	 * Retorna la definició del handler de Helium declarada a l'element, o null si no en té.
	 */
	public static HandlerDefinicio llegirHandler(BaseElement element) {
		if (element == null) {
			return null;
		}
		ExtensionElement handler = primer(element.getExtensionElements(), HeliumBpmnXml.ELEMENT_HANDLER);
		if (handler == null) {
			return null;
		}
		List<HandlerDefinicio.Parametre> parametres = new ArrayList<>();
		for (ExtensionElement parametre : tots(handler.getChildElements(), HeliumBpmnXml.ELEMENT_PARAMETRE)) {
			parametres.add(new HandlerDefinicio.Parametre(
				atribut(parametre, HeliumBpmnXml.ATTR_NOM),
				atribut(parametre, HeliumBpmnXml.ATTR_VALOR),
				atribut(parametre, HeliumBpmnXml.ATTR_EXPRESSIO)));
		}
		return new HandlerDefinicio(
			atribut(handler, HeliumBpmnXml.ATTR_CLASSE),
			atribut(handler, HeliumBpmnXml.ATTR_EXPRESSIO),
			parametres);
	}

	private static ExtensionElement primer(Map<String, List<ExtensionElement>> elements, String nom) {
		List<ExtensionElement> trobats = tots(elements, nom);
		return trobats.isEmpty() ? null : trobats.get(0);
	}

	private static List<ExtensionElement> tots(Map<String, List<ExtensionElement>> elements, String nom) {
		if (elements == null || elements.get(nom) == null) {
			return Collections.emptyList();
		}
		List<ExtensionElement> resultat = new ArrayList<>();
		for (ExtensionElement e : elements.get(nom)) {
			if (HeliumBpmnXml.NAMESPACE.equals(e.getNamespace())) {
				resultat.add(e);
			}
		}
		return resultat;
	}

	/** Valor d'un atribut sense espai de noms (o amb el de Helium). */
	private static String atribut(ExtensionElement element, String nom) {
		List<ExtensionAttribute> atributs = element.getAttributes().get(nom);
		if (atributs == null) {
			return null;
		}
		for (ExtensionAttribute a : atributs) {
			if (a.getNamespace() == null || a.getNamespace().isEmpty() || HeliumBpmnXml.NAMESPACE.equals(a.getNamespace())) {
				return a.getValue();
			}
		}
		return null;
	}

}
