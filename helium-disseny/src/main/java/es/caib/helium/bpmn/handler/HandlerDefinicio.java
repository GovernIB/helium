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
 *       <helium:parametre nom="varMotiu" expressio="${nomVariable}"/>
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
	 * workflow ha d'avaluar en el context de l'execució.
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
	}

}
