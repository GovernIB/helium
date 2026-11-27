package es.caib.helium.flowable;

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

	/** Una dada que es passa entre un procés i un subprocés ({@code helium:entrada} o {@code helium:sortida}). */
	public static final class Mapatge {
		private final String origen;
		private final String expressio;
		private final String desti;

		Mapatge(String origen, String expressio, String desti) {
			this.origen = buit(origen) ? null : origen.trim();
			this.expressio = buit(expressio) ? null : expressio.trim();
			this.desti = buit(desti) ? this.origen : desti.trim();
		}

		/** Codi de la dada d'origen, o null si el valor surt d'una expressió. */
		public String getOrigen() {
			return origen;
		}
		/** Expressió que dona el valor (s'avalua a la instància d'origen), o null. */
		public String getExpressio() {
			return expressio;
		}
		/** Codi de la dada de destí (per defecte, el mateix que l'origen). */
		public String getDesti() {
			return desti;
		}

		private static boolean buit(String text) {
			return text == null || text.trim().isEmpty();
		}
	}

	/**
	 * Dades que es passen a un subprocés ({@link HeliumBpmnXml#ELEMENT_ENTRADA}) o que en retorna
	 * ({@link HeliumBpmnXml#ELEMENT_SORTIDA}), declarades a la {@code callActivity}. Es descarten les que no tenen
	 * destí (ni origen ni destí indicats).
	 */
	public static List<Mapatge> llegirMapatges(BaseElement element, String nomElement) {
		if (element == null) {
			return Collections.emptyList();
		}
		List<Mapatge> mapatges = new ArrayList<>();
		for (ExtensionElement e : tots(element.getExtensionElements(), nomElement)) {
			Mapatge mapatge = new Mapatge(
				atribut(e, HeliumBpmnXml.ATTR_ORIGEN),
				atribut(e, HeliumBpmnXml.ATTR_EXPRESSIO),
				atribut(e, HeliumBpmnXml.ATTR_DESTI));
			if (mapatge.getDesti() != null && (mapatge.getOrigen() != null || mapatge.getExpressio() != null)) {
				mapatges.add(mapatge);
			}
		}
		return mapatges;
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
