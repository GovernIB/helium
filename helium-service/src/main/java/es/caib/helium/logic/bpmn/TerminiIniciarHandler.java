package es.caib.helium.logic.bpmn;


import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.integracio.Termini;
import es.caib.helium.bpmn.exception.BpmnException;
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
public class TerminiIniciarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.TerminiIniciarHandler {

	private String terminiCodi;
	private String data;
	private String termini;
	private String sumarUnDia;
	private String esDataFi;

	public void execute(HeliumApi executionContext) throws BpmnException {
		// Si no hi ha cap termini amb aquest codi, la HeliumApi llança l'error
		if (!isBuit(termini)) {
			Termini vt = Termini.valueFromString(termini.trim());
			executionContext.terminiIniciar(
				terminiCodi,
				getData(),
				vt.getAnys(),
				vt.getMesos(),
				vt.getDies(),
				esDataFi());
		} else {
			executionContext.terminiIniciar(
				terminiCodi,
				getData(),
				esDataFi());
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
