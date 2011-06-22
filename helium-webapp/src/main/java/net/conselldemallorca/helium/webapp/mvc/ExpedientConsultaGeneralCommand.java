/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.Date;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Command per fer una consulta general d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientConsultaGeneralCommand {

	private String titol;
	private String numero;
	private Date dataInici1;
	private Date dataInici2;
	private ExpedientTipus expedientTipus;
	private Long estat;
	private Double geoPosX;
	private Double geoPosY;
	private String geoReferencia;



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
	public Double getGeoPosX() {
		return geoPosX;
	}
	public void setGeoPosX(Double geoPosX) {
		this.geoPosX = geoPosX;
	}
	public Double getGeoPosY() {
		return geoPosY;
	}
	public void setGeoPosY(Double geoPosY) {
		this.geoPosY = geoPosY;
	}
	public String getGeoReferencia() {
		return geoReferencia;
	}
	public void setGeoReferencia(String geoReferencia) {
		this.geoReferencia = geoReferencia;
	}

}
