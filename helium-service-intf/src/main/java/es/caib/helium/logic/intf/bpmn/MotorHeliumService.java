package es.caib.helium.logic.intf.bpmn;

import java.util.List;
import java.util.Map;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.engine.WTaskInstance;
import es.caib.helium.bpmn.handler.HeliumBpmnHandler;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;

/**
 * Serveis de Helium que fa servir la implementació del bpmn BPMN 2.0 (p. ex. {@code helium-flowable}).
 *
 * <p>És el contracte en sentit invers a {@link WorkflowEngineApi}: Helium crida el bpmn amb {@code WorkflowEngineApi}
 * i el bpmn crida Helium amb aquesta interfície, de manera que la implementació del bpmn no depèn de les classes
 * internes de Helium ({@code helium-service}). La implementa {@code helium-service}.</p>
 *
 * <p>No és un servei de l'aplicació: només l'ha de fer servir la implementació del bpmn, no es publica com a EJB i
 * les capes web no l'han de cridar. Per la mateixa raó, la implementació del bpmn només ha de fer servir aquest
 * paquet i {@code WorkflowEngineApi} de {@code helium-service-intf}.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MotorHeliumService {

	/**
	 * Dades de l'expedient visibles des de la instància de procés, per resoldre les expressions del bpmn
	 * (condicions, paràmetres dels handlers...). Les dades no es desen al bpmn.
	 */
	Map<String, Object> getDadesProces(String processInstanceId);

	/** {@link HeliumApi} per executar un handler en una instància de procés. */
	HeliumApi getHeliumApi(String processInstanceId);

	/**
	 * Classe d'un handler: de l'aplicació (les interfícies de {@code helium-disseny} es resolen a la seva
	 * implementació) o dels recursos (handlers propis) del tipus d'expedient o de la definició de procés.
	 */
	Class<? extends HeliumBpmnHandler> resoldreClasseHandler(
			String classe,
			String processInstanceId,
			String processDefinitionId);

	/** Contingut d'un recurs (handler propi...) desat a Helium per a la definició de procés, o null. */
	byte[] getRecursDefinicioProces(String processDefinitionId, String recursNom);

	/** Codis dels usuaris d'un grup. */
	List<String> getUsuarisGrup(String grupCodi);

	/** S'ha creat una tasca al bpmn: Helium n'actualitza la informació (llistat de tasques...). */
	void tascaCreada(String taskId);

	/** Ha canviat l'estat d'una tasca: Helium ho comunica (p. ex. a COMANDA). */
	void tascaEstatModificat(WTaskInstance tasca, EstatTasca estat);

	/** Estat d'una tasca per comunicar-lo fora del bpmn. */
	enum EstatTasca {
		PENDENT,
		INICIADA,
		FINALITZADA,
		CANCELADA
	}

}
