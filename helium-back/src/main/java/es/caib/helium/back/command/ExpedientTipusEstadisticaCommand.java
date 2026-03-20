package es.caib.helium.back.command;

import java.util.List;
import java.util.Map;

import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.ExpedientTipusDto;
import es.caib.helium.commons.dto.MostrarAnulatsDto;

public class ExpedientTipusEstadisticaCommand {
	
	private String numero;
	private String titol;
	private Integer anyInicial;
	private Integer anyFinal;
	private Long expedientTipusId;
	private MostrarAnulatsDto mostrarAnulats = MostrarAnulatsDto.NO;
	private Boolean aturat;
	private String estat;
	private Long entornId;
	private Map<EntornDto, List<ExpedientTipusDto>> expTipusAgrupatsPerEntorn;
	
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
	public Boolean getAturat() {
		return aturat;
	}
	public void setAturat(Boolean aturat) {
		this.aturat = aturat;
	}
	
	public String getEstat() {
		return this.estat;
	}
	public void setEstat(String estat) {
		this.estat = estat;
	}
	public Long getEntornId() {
		return entornId;
	}
	public void setEntornId(Long entornId) {
		this.entornId = entornId;
	}
	public Map<EntornDto, List<ExpedientTipusDto>> getExpTipusAgrupatsPerEntorn() {
		return expTipusAgrupatsPerEntorn;
	}
	public void setExpTipusAgrupatsPerEntorn(Map<EntornDto, List<ExpedientTipusDto>> expTipusAgrupatsPerEntorn) {
		this.expTipusAgrupatsPerEntorn = expTipusAgrupatsPerEntorn;
	}
	
	
}
