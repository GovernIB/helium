/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;

/**
 * DTO amb informació d'una reassignació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReassignacioDto {

	private Long id;
	private String usuariOrigen;
	private String usuariDesti;
	private Date dataInici;
	private Date dataFi;
	private Date dataCancelacio;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsuariOrigen() {
		return usuariOrigen;
	}
	public void setUsuariOrigen(String usuariOrigen) {
		this.usuariOrigen = usuariOrigen;
	}
	public String getUsuariDesti() {
		return usuariDesti;
	}
	public void setUsuariDesti(String usuariDesti) {
		this.usuariDesti = usuariDesti;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public Date getDataCancelacio() {
		return dataCancelacio;
	}
	public void setDataCancelacio(Date dataCancelacio) {
		this.dataCancelacio = dataCancelacio;
	}

}
