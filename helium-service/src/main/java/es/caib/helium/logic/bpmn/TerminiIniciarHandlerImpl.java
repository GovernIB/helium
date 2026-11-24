package es.caib.helium.logic.bpmn;


import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.integracio.HeliumBridge;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.integracio.Termini;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.disseny.handler.TerminiIniciarHandler;
import es.caib.helium.bpmn.model.TerminiInfo;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

/**
 * Implementació del handler per iniciar un termini de l'expedient.
 *
 * <p>Paràmetres: {@code data} és la data d'inici (o de fi, si {@code esDataFi} és {@code true}) en format
 * {@value #FORMAT_DATA}; si no s'informa s'agafa la data actual. {@code termini} és la durada en format
 * {@code anys/mesos/dies}; si no s'informa s'agafa la del termini configurat. Tots dos poden ser una expressió
 * que llegeixi les dades de l'expedient, p. ex. {@code ${dataInici}}.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class TerminiIniciarHandlerImpl extends AbstractHeliumActionHandler implements TerminiIniciarHandler {

	private String terminiCodi;
	private String data;
	private String termini;
	private String sumarUnDia;
	private String esDataFi;

	public void execute(HeliumApi executionContext) throws BpmnException {
		String tercod = terminiCodi;
		TerminiInfo terminiInfo = HeliumBridge
			    				.getInstanceService()
								.getTerminiIniciatAmbCodi(executionContext, tercod);
		if (terminiInfo != null) {
			if (!isBuit(termini)) {
				Termini vt = Termini.valueFromString(termini.trim());
				executionContext.getTerminiIniciatAmbProcessInstanceITerminiCodi(
					executionContext.getProcessInstance().getId(),
					tercod
				);
				HeliumBridge.getInstanceService().terminiIniciar(
					tercod,
					getProcessInstanceId(executionContext),
					getData(),
					vt.getAnys(),
					vt.getMesos(),
					vt.getDies(),
					esDataFi());
			} else {
				HeliumBridge.getInstanceService().terminiIniciar(
					tercod,
					getProcessInstanceId(executionContext),
					getData(),
					esDataFi());
			}
		} else {
			throw new BpmnException("No existeix cap termini amb aquest codi '" + terminiCodi + "'");
		}
	}

	private Date getData() {
		Date valor = isBuit(data) ? new Date() : asDate(data);
		if (!esDataFi() && !isBuit(sumarUnDia)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(valor);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			return cal.getTime();
		}
		return valor;
	}

	private boolean esDataFi() {
		return asBoolean(esDataFi);
	}
}
