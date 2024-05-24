/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import net.conselldemallorca.helium.v3.core.api.dto.EnviamentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioEnviamentEstatEnumDto;

/**
 * Command per al filtre d'expedients dels arxius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioFiltreCommand {


	private EnviamentTipusEnumDto tipusEnviament;
	private String concepte;
	private NotificacioEnviamentEstatEnumDto estat;
	private Date dataInicial;
	private Date dataFinal;
	private String interessat;
	private String numeroExpedient;
	private Long expedientTipusId;
	private String nomDocument;
	private String unitatOrganitzativaCodi;
	private String procedimentCodi;
	private Long expedientId;

	public EnviamentTipusEnumDto getTipusEnviament() {
		return tipusEnviament;
	}

	public void setTipusEnviament(EnviamentTipusEnumDto tipusEnviament) {
		this.tipusEnviament = tipusEnviament;
	}

	public NotificacioEnviamentEstatEnumDto getEstat() {
		return estat;
	}

	public void setEstat(NotificacioEnviamentEstatEnumDto estat) {
		this.estat = estat;
	}

	public String getConcepte() {
		return concepte;
	}

	public void setConcepte(String concepte) {
		this.concepte = concepte;
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

	public Long getExpedientId() {
		return expedientId;
	}

	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
