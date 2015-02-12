/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.Date;

import javax.validation.constraints.AssertTrue;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Command per a realitzar consultes d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientConsultaCommand {

	private String titol;
	private String numero;
	private Long expedientTipusId;
	private EstatTipusDto estatTipus;
	private Long estatId;
	private Date dataIniciInicial;
	private Date dataIniciFinal;
	private Date dataFiInicial;
	private Date dataFiFinal;
	private Double geoPosX;
	private Double geoPosY;
	private String geoReferencia;
	private boolean nomesPendents;
	private boolean nomesAlertes;
	private boolean mostrarAnulats;
	
	private boolean mostrarTasquesPersonals = false;
	private boolean mostrarTasquesUsuari = false;

	private boolean filtreDesplegat = false;
	private boolean tramitacioMassivaActivada = false;
	private boolean consultaRealitzada = false;

	public ExpedientConsultaCommand() {
	}

	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public EstatTipusDto getEstatTipus() {
		return estatTipus;
	}
	public void setEstatTipus(EstatTipusDto estatTipus) {
		this.estatTipus = estatTipus;
	}
	public Long getEstatId() {
		return estatId;
	}
	public void setEstatId(Long estatId) {
		this.estatId = estatId;
	}
	public Date getDataIniciInicial() {
		return dataIniciInicial;
	}
	public void setDataIniciInicial(Date dataIniciInicial) {
		this.dataIniciInicial = dataIniciInicial;
	}
	public Date getDataIniciFinal() {
		return dataIniciFinal;
	}
	public void setDataIniciFinal(Date dataIniciFinal) {
		this.dataIniciFinal = dataIniciFinal;
	}
	public Date getDataFiInicial() {
		return dataFiInicial;
	}
	public void setDataFiInicial(Date dataFiInicial) {
		this.dataFiInicial = dataFiInicial;
	}
	public Date getDataFiFinal() {
		return dataFiFinal;
	}
	public void setDataFiFinal(Date dataFiFinal) {
		this.dataFiFinal = dataFiFinal;
	}
	public boolean isMostrarTasquesUsuari() {
		return mostrarTasquesUsuari;
	}

	public void setMostrarTasquesUsuari(boolean mostrarTasquesUsuari) {
		this.mostrarTasquesUsuari = mostrarTasquesUsuari;
	}
	public Double getGeoPosX() {
		return geoPosX;
	}
	public void setGeoPosX(Double geoPosX) {
		this.geoPosX = geoPosX;
	}
	public Double getGeoPosY() {
		return geoPosY;
	}
	public void setGeoPosY(Double geoPosY) {
		this.geoPosY = geoPosY;
	}
	public String getGeoReferencia() {
		return geoReferencia;
	}
	public void setGeoReferencia(String geoReferencia) {
		this.geoReferencia = geoReferencia;
	}
	public boolean isNomesPendents() {
		return nomesPendents;
	}
	public void setNomesPendents(boolean nomesPendents) {
		this.nomesPendents = nomesPendents;
	}
	public boolean isNomesAlertes() {
		return nomesAlertes;
	}
	public void setNomesAlertes(boolean nomesAlertes) {
		this.nomesAlertes = nomesAlertes;
	}
	public boolean isMostrarAnulats() {
		return mostrarAnulats;
	}
	public void setMostrarAnulats(boolean mostrarAnulats) {
		this.mostrarAnulats = mostrarAnulats;
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
	public boolean isMostrarTasquesPersonals() {
		return mostrarTasquesPersonals;
	}
	public void setMostrarTasquesPersonals(boolean mostrarTasquesPersonals) {
		this.mostrarTasquesPersonals = mostrarTasquesPersonals;
	}

	public String getEstatText() {
		if (EstatTipusDto.CUSTOM.equals(estatTipus))
			return (estatId != null) ? estatId.toString() : null;
		else
			return (estatTipus != null) ? estatTipus.toString() : null;
	}
	public void setEstatText(String estatText) {
		if (estatText == null || estatText.length() == 0) {
			estatTipus = null;
			estatId = null;
		} else {
			try {
				estatTipus = EstatTipusDto.CUSTOM;
				estatId = Long.parseLong(estatText);
		    } catch (NumberFormatException nfe) {
		    	estatTipus = EstatTipusDto.valueOf(estatText);
		    }
		}
	}

	@AssertTrue
	public boolean isValidRangDataInici() {
		if (dataIniciInicial == null || dataIniciFinal == null)
			return true;
		return dataIniciFinal.compareTo(dataIniciInicial) >= 0;
	}
	@AssertTrue
	public boolean isValidRangDataFi() {
		if (dataFiInicial == null || dataFiFinal == null)
			return true;
		return dataFiFinal.compareTo(dataFiInicial) >= 0;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
