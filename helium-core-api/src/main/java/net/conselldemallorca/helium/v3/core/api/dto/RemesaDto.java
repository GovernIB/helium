/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.Date;


/**
 * DTO amb informació d'una notificació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RemesaDto implements Serializable {
	
	private Long id;
	private String codi;
	private DocumentEnviamentEstatEnumDto estat;
	private String producteCodi;
	private String clientCodi;
	private Date dataCreacio;
	private Date dataEnviament;
	private Date dataEmisio;
	private Date dataPrevistaDeposit;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	
	public DocumentEnviamentEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(DocumentEnviamentEstatEnumDto estat) {
		this.estat = estat;
	}
	
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}
	
	public Date getDataEnviament() {
		return dataEnviament;
	}
	public void setDataEnviament(Date dataEnviament) {
		this.dataEnviament = dataEnviament;
	}
	
	public Date getDataEmisio() {
		return dataEmisio;
	}
	public void setDataEmisio(Date dataEmisio) {
		this.dataEmisio = dataEmisio;
	}
	
	public Date getDataPrevistaDeposit() {
		return dataPrevistaDeposit;
	}
	public void setDataPrevistaDeposit(Date dataPrevistaDeposit) {
		this.dataPrevistaDeposit = dataPrevistaDeposit;
	}
	
	public String getProducteCodi() {
		return producteCodi;
	}
	public void setProducteCodi(String producteCodi) {
		this.producteCodi = producteCodi;
	}
	public String getClientCodi() {
		return clientCodi;
	}
	public void setClientCodi(String clientCodi) {
		this.clientCodi = clientCodi;
	}

	private static final long serialVersionUID = 1L;
}
