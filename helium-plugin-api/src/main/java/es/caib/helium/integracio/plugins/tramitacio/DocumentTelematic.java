/**
 * 
 */
package es.caib.helium.integracio.plugins.tramitacio;

import java.util.Arrays;
import java.util.List;


/**
 * 
 * @author Limit Tecnologies
 */
public class DocumentTelematic {

	protected String arxiuNom;
	protected String arxiuExtensio;
	protected byte[] arxiuContingut;
	protected String referenciaGestorDocumental;
	protected List<Signatura> signatures;

	protected long referenciaCodi;
	protected String referenciaClau;
	protected Boolean estructurat;



	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
	public String getArxiuExtensio() {
		return arxiuExtensio;
	}
	public void setArxiuExtensio(String arxiuExtensio) {
		this.arxiuExtensio = arxiuExtensio;
	}
	public byte[] getArxiuContingut() {
		return arxiuContingut;
	}
	public void setArxiuContingut(byte[] arxiuContingut) {
		this.arxiuContingut = arxiuContingut;
	}
	public String getReferenciaGestorDocumental() {
		return referenciaGestorDocumental;
	}
	public void setReferenciaGestorDocumental(String referenciaGestorDocumental) {
		this.referenciaGestorDocumental = referenciaGestorDocumental;
	}
	public List<Signatura> getSignatures() {
		return signatures;
	}
	public void setSignatures(List<Signatura> signatures) {
		this.signatures = signatures;
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
	public Boolean getEstructurat() {
		return estructurat;
	}
	public void setEstructurat(Boolean estructurat) {
		this.estructurat = estructurat;
	}
	@Override
	public String toString() {
		return "DocumentTelematic [arxiuNom=" + arxiuNom + ", arxiuExtensio=" + arxiuExtensio + ", arxiuContingut=" + Arrays.toString(arxiuContingut) + ", referenciaGestorDocumental=" + referenciaGestorDocumental + ", signatures=" + signatures + ", referenciaCodi=" + referenciaCodi + ", referenciaClau=" + referenciaClau + ", estructurat=" + estructurat + "]";
	}
}
