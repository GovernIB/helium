/**
 * 
 */
package net.conselldemallorca.helium.integracio.tramitacio;



/**
 * Informació d'un camp d'una procés d'un expedient
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DocumentProces {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;



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

}
