package net.conselldemallorca.helium.webapp.v3.command;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;

public class ExpedientTipusEstadisticaCommand {
	
	private String numero;
	private String titol;
	private Long estatId;
	private Integer anyInicial;
	private Integer anyFinal;
	private Long expedientTipusId;
	private MostrarAnulatsDto mostrarAnulats = MostrarAnulatsDto.NO;
	private Boolean aturat;
	private EstatTipusDto estatTipus;
	
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public MostrarAnulatsDto getMostrarAnulats() {
		return mostrarAnulats;
	}
	public Integer getAnyInicial() {
		return anyInicial;
	}
	public void setAnyInicial(Integer anyInicial) {
		this.anyInicial = anyInicial;
	}
	public Integer getAnyFinal() {
		return anyFinal;
	}
	public void setAnyFinal(Integer anyFinal) {
		this.anyFinal = anyFinal;
	}
	public void setMostrarAnulats(MostrarAnulatsDto mostrarAnulats) {
		this.mostrarAnulats = mostrarAnulats;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public Long getEstatId() {
		return estatId;
	}
	public void setEstatId(Long estatId) {
		this.estatId = estatId;
	}
	public Boolean getAturat() {
		return aturat;
	}
	public void setAturat(Boolean aturat) {
		this.aturat = aturat;
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
	public EstatTipusDto getEstatTipus() {
		return estatTipus;
	}
	public void setEstatTipus(EstatTipusDto estatTipus) {
		this.estatTipus = estatTipus;
	}
	
	
}
