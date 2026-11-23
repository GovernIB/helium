package es.caib.helium.bpmn.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Definició d'un handler dins una service task BPMN, independent del motor de workflow.
 *
 * <p>Es correspon amb l'extensió de Helium dins el BPMN 2.0 (veure {@link HeliumBpmnXml}):</p>
 * <pre>{@code
 * <bpmn:serviceTask id="..." implementation="http://www.caib.es/helium/bpmn/handler">
 *   <bpmn:extensionElements>
 *     <helium:handler classe="es.caib.helium.logic.bpmn.ExpedientAturarHandler">
 *       <helium:parametre nom="motiu" valor="Motiu fix"/>
 *       <!-- o bé, amb una expressió que llegeix les dades de l'expedient: -->
 *       <helium:parametre nom="motiu" expressio="${motiuAturada}"/>
 *     </helium:handler>
 *     <!-- o bé, amb el handler obtingut d'una expressió: <helium:handler expressio="${unHandler}"> -->
 *   </bpmn:extensionElements>
 * </bpmn:serviceTask>
 * }</pre>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@ToString
public class HandlerDefinicio {

	/** Classe del handler (o interfície de helium-disseny). Null si el handler s'obté amb una expressió. */
	private final String classe;
	/**
	 * Expressió que el motor avalua per obtenir la instància del handler (ha d'implementar
	 * {@link HeliumBpmnHandler}). Null si el handler s'indica per classe.
	 */
	private final String expressio;
	private final List<Parametre> parametres;

	public HandlerDefinicio(String classe, List<Parametre> parametres) {
		this(classe, null, parametres);
	}

	public HandlerDefinicio(String classe, String expressio, List<Parametre> parametres) {
		this.classe = classe;
		this.expressio = expressio;
		this.parametres = parametres == null
			? Collections.emptyList()
			: Collections.unmodifiableList(new ArrayList<>(parametres));
	}

	/** Indica si el handler s'obté avaluant una expressió en lloc de crear-lo a partir de la classe. */
	public boolean isPerExpressio() {
		return expressio != null && !expressio.trim().isEmpty();
	}

	/**
	 * Paràmetre d'un handler. Té un valor literal o bé una expressió que el motor de
	 * workflow ha d'avaluar en el context de l'execució. Si té tots dos, el valor és el valor per defecte que
	 * s'utilitza quan l'expressió retorna null o fa referència a una dada sense valor.
	 */
	@Getter
	@ToString
	@AllArgsConstructor
	public static class Parametre {
		private final String nom;
		private final String valor;
		private final String expressio;

		public boolean isExpressio() {
			return expressio != null && !expressio.trim().isEmpty();
		}

		/**
		 * Crea el paràmetre a partir d'un valor que pot ser un literal o una expressió: és una expressió si conté
		 * {@code ${...}} o {@code #{...}}.
		 */
		public static Parametre deText(String nom, String text) {
			return esExpressio(text) ? new Parametre(nom, null, text) : new Parametre(nom, text, null);
		}

		/** Indica si el text és (o conté) una expressió ({@code ${...}} o {@code #{...}}). */
		public static boolean esExpressio(String text) {
			return text != null && (text.contains("${") || text.contains("#{"));
		}
	}

}
