/**
 * 
 */
package net.conselldemallorca.helium.ws.tramitacio.v1;

import java.util.Date;



/**
 * Informació d'un document d'una tasca en tramitació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentTasca {

	private String codi;
	private String nom;
	private String descripcio;
	private Long id;
	private String arxiu;
	private Date data;
	private String urlCustodia;



	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getArxiu() {
		return arxiu;
	}
	public void setArxiu(String arxiu) {
		this.arxiu = arxiu;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getUrlCustodia() {
		return urlCustodia;
	}
	public void setUrlCustodia(String urlCustodia) {
		this.urlCustodia = urlCustodia;
	}

}
