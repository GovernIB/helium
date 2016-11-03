/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO amb informació d'una enumeració per a l'exportació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnumeracioExportacio implements Serializable{

	private String codi;
	private String nom;
	private List<EnumeracioValorExportacio> valors = new ArrayList<EnumeracioValorExportacio>();
	
	public EnumeracioExportacio(
			String codi,
			String nom) {
		this.codi = codi;
		this.nom = nom;
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
	public List<EnumeracioValorExportacio> getValors() {
		return valors;
	}
	public void setValors(List<EnumeracioValorExportacio> valors) {
		this.valors = valors;
	}

	private static final long serialVersionUID = 1L;
}
