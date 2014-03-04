/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

/**
 * Command per a realitzar consultes d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientInformeCommand {

	private Long expedientTipusId;
	private Long consultaId;
	
	private boolean nomesPendents = true;
	private boolean nomesAlertes;
	private boolean mostrarAnulats;

	private boolean filtreDesplegat = false;
	private boolean tramitacioMassivaActivada = false;
	private boolean consultaRealitzada = false;

	public ExpedientInformeCommand() {
	}

	public Long getExpedientTipusId() {
		return expedientTipusId;
	}

	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}

	public Long getConsultaId() {
		return consultaId;
	}

	public void setConsultaId(Long consultaId) {
		this.consultaId = consultaId;
	}

	public boolean isFiltreDesplegat() {
		return filtreDesplegat;
	}

	public void setFiltreDesplegat(boolean filtreDesplegat) {
		this.filtreDesplegat = filtreDesplegat;
	}

	public boolean isTramitacioMassivaActivada() {
		return tramitacioMassivaActivada;
	}

	public void setTramitacioMassivaActivada(boolean tramitacioMassivaActivada) {
		this.tramitacioMassivaActivada = tramitacioMassivaActivada;
	}

	public boolean isConsultaRealitzada() {
		return consultaRealitzada;
	}

	public void setConsultaRealitzada(boolean consultaRealitzada) {
		this.consultaRealitzada = consultaRealitzada;
	}

	public boolean isNomesPendents() {
		return nomesPendents;
	}

	public void setNomesPendents(boolean nomesPendents) {
		this.nomesPendents = nomesPendents;
	}

	public boolean isMostrarAnulats() {
		return mostrarAnulats;
	}

	public void setMostrarAnulats(boolean mostrarAnulats) {
		this.mostrarAnulats = mostrarAnulats;
	}

	public boolean isNomesAlertes() {
		return nomesAlertes;
	}

	public void setNomesAlertes(boolean nomesAlertes) {
		this.nomesAlertes = nomesAlertes;
	}
}
