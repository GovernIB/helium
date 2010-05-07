/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.util.Date;

import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;

/**
 * Command per fer una consulta general d'expedients
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ExpedientConsultaGeneralCommand {

	private String titol;
	private String numero;
	private Date dataInici1;
	private Date dataInici2;
	private ExpedientTipus expedientTipus;
	private Long estat;



	public ExpedientConsultaGeneralCommand() {}



	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Date getDataInici1() {
		return dataInici1;
	}
	public void setDataInici1(Date dataInici1) {
		this.dataInici1 = dataInici1;
	}
	public Date getDataInici2() {
		return dataInici2;
	}
	public void setDataInici2(Date dataInici2) {
		this.dataInici2 = dataInici2;
	}
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}
	public Long getEstat() {
		return estat;
	}
	public void setEstat(Long estat) {
		this.estat = estat;
	}

}
