/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per al filtre d'anotacions de distribució.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioFiltreDto {

	private DocumentNotificacioTipusEnumDto tipusEnviament;
	private String concepte;
	private NotificacioEstatEnumDto estat;
	private Date dataInicial;
	private Date dataFinal;
	private String interessat;
	private String numeroExpedient;
	private Long expedientTipusId;
	private String nomDocument;
	private String unitatOrganitzativaCodi;
	private String procedimentCodi;
	
	
	public DocumentNotificacioTipusEnumDto getTipusEnviament() {
		return tipusEnviament;
	}

	public void setTipusEnviament(DocumentNotificacioTipusEnumDto tipusEnviament) {
		this.tipusEnviament = tipusEnviament;
	}

	public String getConcepte() {
		return concepte;
	}

	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}

	public NotificacioEstatEnumDto getEstat() {
		return estat;
	}

	public void setEstat(NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public String getInteressat() {
		return interessat;
	}

	public void setInteressat(String interessat) {
		this.interessat = interessat;
	}

	public String getNumeroExpedient() {
		return numeroExpedient;
	}

	public void setNumeroExpedient(String numeroExpedient) {
		this.numeroExpedient = numeroExpedient;
	}

	public Long getExpedientTipusId() {
		return expedientTipusId;
	}

	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}

	public String getNomDocument() {
		return nomDocument;
	}

	public void setNomDocument(String nomDocument) {
		this.nomDocument = nomDocument;
	}

	public String getUnitatOrganitzativaCodi() {
		return unitatOrganitzativaCodi;
	}

	public void setUnitatOrganitzativaCodi(String unitatOrganitzativaCodi) {
		this.unitatOrganitzativaCodi = unitatOrganitzativaCodi;
	}

	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	
}
