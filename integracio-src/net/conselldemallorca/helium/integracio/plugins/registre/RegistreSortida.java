/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

import java.util.List;

/**
 * Registre de sortida o notificació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreSortida {

	private DadesOficina dadesOficina;
	private DadesInteressat dadesInteressat;
	private DadesRepresentat dadesRepresentat;
	private DadesAssumpte dadesAssumpte;
	private List<DocumentRegistre> documents;

	public DadesOficina getDadesOficina() {
		return dadesOficina;
	}
	public void setDadesOficina(DadesOficina dadesOficina) {
		this.dadesOficina = dadesOficina;
	}
	public DadesInteressat getDadesInteressat() {
		return dadesInteressat;
	}
	public void setDadesInteressat(DadesInteressat dadesInteressat) {
		this.dadesInteressat = dadesInteressat;
	}
	public DadesRepresentat getDadesRepresentat() {
		return dadesRepresentat;
	}
	public void setDadesRepresentat(DadesRepresentat dadesRepresentat) {
		this.dadesRepresentat = dadesRepresentat;
	}
	public DadesAssumpte getDadesAssumpte() {
		return dadesAssumpte;
	}
	public void setDadesAssumpte(DadesAssumpte dadesAssumpte) {
		this.dadesAssumpte = dadesAssumpte;
	}
	public List<DocumentRegistre> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentRegistre> documents) {
		this.documents = documents;
	}

}
