/**
 * 
 */
package es.caib.helium.integracio.plugins.registre;

import java.util.List;

/**
 * Registre de sortida o notificació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreNotificacio {

	private DadesExpedient dadesExpedient;
	private DadesOficina dadesOficina;
	private DadesInteressat dadesInteressat;
	private DadesRepresentat dadesRepresentat;
	private DadesNotificacio dadesNotificacio;
	private List<DocumentRegistre> documents;

	public DadesExpedient getDadesExpedient() {
		return dadesExpedient;
	}
	public void setDadesExpedient(DadesExpedient dadesExpedient) {
		this.dadesExpedient = dadesExpedient;
	}
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
	public DadesNotificacio getDadesNotificacio() {
		return dadesNotificacio;
	}
	public void setDadesNotificacio(DadesNotificacio dadesNotificacio) {
		this.dadesNotificacio = dadesNotificacio;
	}
	public List<DocumentRegistre> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentRegistre> documents) {
		this.documents = documents;
	}

}
