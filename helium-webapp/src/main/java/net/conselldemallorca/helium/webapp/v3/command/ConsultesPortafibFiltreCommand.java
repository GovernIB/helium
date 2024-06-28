package net.conselldemallorca.helium.webapp.v3.command;

import java.io.Serializable;
import java.util.Date;

import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesEstatEnum;

public class ConsultesPortafibFiltreCommand implements Serializable {

	private static final long serialVersionUID = -4947089586164633202L;
	
	private Long id;
	private Long entornId;
	private Long tipusId;
	private Long expedientId;
	private String numeroExpedient;
	private String documentNom;
	private PortafirmesEstatEnum estat;
	private Date dataPeticioIni;
	private Date dataPeticioFi;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEntornId() {
		return entornId;
	}
	public void setEntornId(Long entornId) {
		this.entornId = entornId;
	}
	public Long getTipusId() {
		return tipusId;
	}
	public void setTipusId(Long tipusId) {
		this.tipusId = tipusId;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getNumeroExpedient() {
		return numeroExpedient;
	}
	public void setNumeroExpedient(String numeroExpedient) {
		this.numeroExpedient = numeroExpedient;
	}
	public PortafirmesEstatEnum getEstat() {
		return estat;
	}
	public void setEstat(PortafirmesEstatEnum estat) {
		this.estat = estat;
	}
	public Date getDataPeticioIni() {
		return dataPeticioIni;
	}
	public void setDataPeticioIni(Date dataPeticioIni) {
		this.dataPeticioIni = dataPeticioIni;
	}
	public Date getDataPeticioFi() {
		return dataPeticioFi;
	}
	public void setDataPeticioFi(Date dataPeticioFi) {
		this.dataPeticioFi = dataPeticioFi;
	}
	public String getDocumentNom() {
		return documentNom;
	}
	public void setDocumentNom(String documentNom) {
		this.documentNom = documentNom;
	}
}