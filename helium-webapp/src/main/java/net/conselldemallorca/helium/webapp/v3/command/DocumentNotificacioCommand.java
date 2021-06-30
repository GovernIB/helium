/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.helium.logic.intf.dto.EnviamentTipusEnumDto;
import es.caib.helium.logic.intf.dto.ServeiTipusEnumDto;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentNotificacioCommand {

	@NotEmpty
	private List<Long> interessatsIds = new ArrayList<Long>();
	private Long representantId = null;
	@NotEmpty
	private String concepte;
	@NotNull
	private ServeiTipusEnumDto serveiTipusEnum;
	private String grupCodi;
	
	private String descripcio;
	@NotNull
	private EnviamentTipusEnumDto enviamentTipus;
	private Date enviamentDataProgramada;
	@NotNull
	private Date caducitat;
	private int retard;
	private boolean entregaPostalActiva;
	
	private IdiomaEnumDto idioma;
	
//	private EntregaPostalTipus entregaPostalTipus;
//	private EntregaPostalViaTipus  entregaPostalViaTipus;
//	private String entregaPostalViaNom;
//	private String entregaPostalNumeroCasa;
//	private String entregaPostalNumeroQualificador;
//	private String entregaPostalPuntKm;
//	private String entregaPostalApartatCorreus;
//	private String entregaPostalPortal;	
//	private String entregaPostalEscala;
//	private String entregaPostalPlanta;
//	private String entregaPostalPorta;
//	private String entregaPostalBloc;
//	private String entregaPostalComplement;
//	private String entregaPostalCodiPostal;
//	private String entregaPostalPoblacio;
//	private String entregaPostalMunicipiCodi;
//	private String entregaPostalProvinciaCodi;
//	private String entregaPostalPaisCodi;
//	private String entregaPostalLinea1;
//	private String entregaPostalLinea2;
//	private Integer entregaPostalCie;
//	private String entregaPostalFormatSobre;
//	private String entregaPostalFormatFulla;
//	
//	private boolean entregaDehObligat;
//	private String entregaDehProcedimentCodi;
//	
	
	
	
	public String getGrupCodi() {
		return grupCodi;
	}
	public ServeiTipusEnumDto getServeiTipusEnum() {
		return serveiTipusEnum;
	}
	public void setServeiTipusEnum(ServeiTipusEnumDto serveiTipusEnum) {
		this.serveiTipusEnum = serveiTipusEnum;
	}
	public void setGrupCodi(String grupCodi) {
		this.grupCodi = grupCodi;
	}
	public List<Long> getInteressatsIds() {
		return interessatsIds;
	}
	public void setInteressatsIds(List<Long> interessatsIds) {
		this.interessatsIds = interessatsIds;
	}
	public Long getRepresentantId() {
		return representantId;
	}
	public void setRepresentantId(Long representantId) {
		this.representantId = representantId;
	}
	public String getConcepte() {
		return concepte;
	}
	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public EnviamentTipusEnumDto getEnviamentTipus() {
		return enviamentTipus;
	}
	public void setEnviamentTipus(EnviamentTipusEnumDto enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
	}
	public Date getCaducitat() {
		return caducitat;
	}
	public void setCaducitat(Date caducitat) {
		this.caducitat = caducitat;
	}
	public int getRetard() {
		return retard;
	}
	public void setRetard(int retard) {
		this.retard = retard;
	}
	public boolean isEntregaPostalActiva() {
		return entregaPostalActiva;
	}
	public void setEntregaPostalActiva(boolean entregaPostalActiva) {
		this.entregaPostalActiva = entregaPostalActiva;
	}
	public IdiomaEnumDto getIdioma() {
		return idioma;
	}
	public void setIdioma(IdiomaEnumDto idioma) {
		this.idioma = idioma;
	}
	
}
