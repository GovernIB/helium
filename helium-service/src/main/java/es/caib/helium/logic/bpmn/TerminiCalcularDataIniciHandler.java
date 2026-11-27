package es.caib.helium.logic.bpmn;

import java.util.Calendar;
import java.util.Date;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.model.TerminiInfo;
import lombok.Setter;

/**
 * Handler per calcular la data d'inici d'un termini a partir d'una data de fi, i desar-la a una dada de l'expedient.
 *
 * <p>Paràmetres: {@code data} és la data de fi en format {@value #FORMAT_DATA} (si no s'informa, la data actual),
 * {@code restarUnDia} resta un dia a la data de fi, {@code termini} és la durada en format {@code anys/mesos/dies}
 * (si no s'informa, la del termini configurat) i {@code varDataInici} és el codi de la dada on es desa el resultat.
 * Els paràmetres d'entrada poden ser expressions que llegeixin les dades de l'expedient, p. ex. {@code ${dataFi}}.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class TerminiCalcularDataIniciHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.TerminiCalcularDataIniciHandler {

	private String terminiCodi;
	private String data;
	private String restarUnDia;
	private String termini;
	private String varDataInici;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		if (isBuit(varDataInici)) {
			throw new HeliumHandlerException("Cal indicar la dada on desar la data d'inici (varDataInici)");
		}
		// Si no hi ha cap termini amb aquest codi, la HeliumApi llança l'error
		TerminiInfo definicio = heliumApi.getTermini(terminiCodi);
		TerminiInfo durada = isBuit(termini) ? definicio : TerminiInfo.valueFromString(termini.trim());
		Date dataInici = heliumApi.terminiCalcularDataInici(
			getDataFi(),
			durada.getAnys(),
			durada.getMesos(),
			durada.getDies() != null ? durada.getDies() : 0,
			definicio.isLaborable());
		heliumApi.setVariable(varDataInici.trim(), dataInici);
	}

	private Date getDataFi() {
		Date valor = isBuit(data) ? new Date() : asDate(data);
		if (!isBuit(restarUnDia)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(valor);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			return cal.getTime();
		}
		return valor;
	}
}
