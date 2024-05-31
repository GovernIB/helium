/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import net.conselldemallorca.helium.v3.core.api.dto.EnviamentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioEstatEnumDto;

/**
 * Command per al filtre d'expedients dels arxius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioFiltreCommand implements Serializable {

	private static final long serialVersionUID = 5351582872698404634L;
	
	private EnviamentTipusEnumDto tipus;
	private String concepte;
	private NotificacioEstatEnumDto estat;
	private Date dataInicial;
	private Date dataFinal;
	private String interessat;
	private String expedientNumero;
	private Long expedientTipusId;
	private String nomDocument;
	private String unitatOrganitzativaCodi;
	private String procedimentCodi;
	private Long expedientId;
	private Long entornId;
	private Long tipusId;

	public EnviamentTipusEnumDto getTipus() {
		return tipus;
	}

	public void setTipus(EnviamentTipusEnumDto tipus) {
		this.tipus = tipus;
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

	public NotificacioEstatEnumDto getEstat() {
		return estat;
	}

	public void setEstat(NotificacioEstatEnumDto estat) {
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

	public String getExpedientNumero() {
		return expedientNumero;
	}

	public void setExpedientNumero(String expedientNumero) {
		this.expedientNumero = expedientNumero;
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
