/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;


/**
 * Command per afegir un document a una tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnumeracioValorsCommand {

	private Long id;
	private String codi;
	private String nom;
	private Long enumeracioId;

	public EnumeracioValorsCommand() {}

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

	public Long getEnumeracioId() {
		return enumeracioId;
	}
	public void setEnumeracioId(Long enumeracioId) {
		this.enumeracioId = enumeracioId;
	}
}
