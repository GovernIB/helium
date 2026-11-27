package es.caib.helium.logic.bpmn;

import java.util.Calendar;
import java.util.Date;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.model.TerminiInfo;
import lombok.Setter;

/**
 * Handler per calcular la data de fi d'un termini a partir d'una data d'inici, i desar-la a una dada de l'expedient.
 *
 * <p>Paràmetres: {@code data} és la data d'inici en format {@value #FORMAT_DATA} (si no s'informa, la data actual),
 * {@code sumarUnDia} suma un dia a la data d'inici, {@code termini} és la durada en format {@code anys/mesos/dies}
 * (si no s'informa, la del termini configurat) i {@code varDataFi} és el codi de la dada on es desa el resultat. Els
 * paràmetres d'entrada poden ser expressions que llegeixin les dades de l'expedient, p. ex. {@code ${dataInici}}.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class TerminiCalcularDataFiHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.TerminiCalcularDataFiHandler {

	private String terminiCodi;
	private String data;
	private String sumarUnDia;
	private String termini;
	private String varDataFi;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		if (isBuit(varDataFi)) {
			throw new HeliumHandlerException("Cal indicar la dada on desar la data de fi (varDataFi)");
		}
		// Si no hi ha cap termini amb aquest codi, la HeliumApi llança l'error
		TerminiInfo definicio = heliumApi.getTermini(terminiCodi);
		TerminiInfo durada = isBuit(termini) ? definicio : TerminiInfo.valueFromString(termini.trim());
		Date dataFi = heliumApi.terminiCalcularDataFi(
			getDataInici(),
			durada.getAnys(),
			durada.getMesos(),
			durada.getDies() != null ? durada.getDies() : 0,
			definicio.isLaborable());
		heliumApi.setVariable(varDataFi.trim(), dataFi);
	}

	private Date getDataInici() {
		Date valor = isBuit(data) ? new Date() : asDate(data);
		if (!isBuit(sumarUnDia)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(valor);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			return cal.getTime();
		}
		return valor;
	}
}
