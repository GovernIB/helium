/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * DTO amb informació d'una notificació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TramitSistraDto implements Serializable {
	
	private Long id;
	private String sistraTramitCodi;
	private TramitSistraEnumDto tipus;
	private String codiVarIdentificadorExpedient;
	private AccioDto accio;
	private List<MapeigSistraDto> mapeigSistras = new ArrayList<MapeigSistraDto>();
	private ExpedientTipusDto expedientTipus;
	
	private Long numVariables = 0L;
	private Long numDocuments = 0L;
	private Long numAdjunts = 0L;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSistraTramitCodi() {
		return sistraTramitCodi;
	}
	public void setSistraTramitCodi(String sistraTramitCodi) {
		this.sistraTramitCodi = sistraTramitCodi;
	}
	public TramitSistraEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(TramitSistraEnumDto tipus) {
		this.tipus = tipus;
	}
	public String getCodiVarIdentificadorExpedient() {
		return codiVarIdentificadorExpedient;
	}
	public void setCodiVarIdentificadorExpedient(String codiVarIdentificadorExpedient) {
		this.codiVarIdentificadorExpedient = codiVarIdentificadorExpedient;
	}
	public AccioDto getAccio() {
		return accio;
	}
	public void setAccio(AccioDto accio) {
		this.accio = accio;
	}
	public List<MapeigSistraDto> getMapeigSistras() {
		return mapeigSistras;
	}
	public void setMapeigSistras(List<MapeigSistraDto> mapeigSistras) {
		this.mapeigSistras = mapeigSistras;
	}
	public ExpedientTipusDto getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipusDto expedientTipus) {
		this.expedientTipus = expedientTipus;
	}
	
	
	public Long getNumVariables() {
		return numVariables;
	}
	public void setNumVariables(Long numVariables) {
		this.numVariables = numVariables;
	}
	public Long getNumDocuments() {
		return numDocuments;
	}
	public void setNumDocuments(Long numDocuments) {
		this.numDocuments = numDocuments;
	}
	public Long getNumAdjunts() {
		return numAdjunts;
	}
	public void setNumAdjunts(Long numAdjunts) {
		this.numAdjunts = numAdjunts;
	}


	private static final long serialVersionUID = 1L;
}
