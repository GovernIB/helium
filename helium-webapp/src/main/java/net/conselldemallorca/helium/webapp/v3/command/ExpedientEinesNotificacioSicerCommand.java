/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.Date;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Command per aturar expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientEinesNotificacioSicerCommand {

	@NotEmpty
	@Size(max = 4)
	private String codiRemesa;
	@NotEmpty
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private Date dataEmisio;
	@NotEmpty
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private Date dataPrevistaDeposit;
	
	public String getCodiRemesa() {
		return codiRemesa;
	}
	public void setCodiRemesa(String codiRemesa) {
		this.codiRemesa = codiRemesa;
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

	
}
