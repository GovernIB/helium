/**
 * 
 */
package es.caib.helium.integracio.plugins.tramitacio;


/**
 * 
 * @author Limit Tecnologies
 */
public class ObtenirVistaDocumentRequest {

	protected String referenciaGD;
	protected long referenciaCodi;
	protected String referenciaClau;
	protected String plantillaTipus;
	protected String idioma;

	public String getReferenciaGD() {
		return referenciaGD;
	}
	public void setReferenciaGD(String referenciaGD) {
		this.referenciaGD = referenciaGD;
	}
	public long getReferenciaCodi() {
		return referenciaCodi;
	}
	public void setReferenciaCodi(long referenciaCodi) {
		this.referenciaCodi = referenciaCodi;
	}
	public String getReferenciaClau() {
		return referenciaClau;
	}
	public void setReferenciaClau(String referenciaClau) {
		this.referenciaClau = referenciaClau;
	}
	public String getPlantillaTipus() {
		return plantillaTipus;
	}
	public void setPlantillaTipus(String plantillaTipus) {
		this.plantillaTipus = plantillaTipus;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}

	@Override
	public String toString() {
		return "[" + referenciaCodi + ", " + referenciaClau + "]";
	}

}
