package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.List;

public class ExpedientFinalitzarDto implements Serializable {

	private static final long serialVersionUID = -4499497016646517392L;
	private ExpedientDto expedient;
	private List<DocumentFinalitzarDto> documentsFinalitzar;
	private List<DocumentFinalitzarDto> documentsPendentsFirma;
	private String motiuFinalitzar;
	private String accio;
	private boolean error = false;
	private boolean pendentsFirma = false;
	private boolean expedientNti = false;
	
	public ExpedientDto getExpedient() {
		return expedient;
	}
	public void setExpedient(ExpedientDto expedient) {
		this.expedient = expedient;
	}
	public List<DocumentFinalitzarDto> getDocumentsFinalitzar() {
		return documentsFinalitzar;
	}
	public void setDocumentsFinalitzar(List<DocumentFinalitzarDto> documentsFinalitzar) {
		this.documentsFinalitzar = documentsFinalitzar;
	}
	public String getMotiuFinalitzar() {
		return motiuFinalitzar;
	}
	public void setMotiuFinalitzar(String motiuFinalitzar) {
		this.motiuFinalitzar = motiuFinalitzar;
	}
	public String getAccio() {
		return accio;
	}
	public void setAccio(String accio) {
		this.accio = accio;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public boolean isPendentsFirma() {
		return pendentsFirma;
	}
	public void setPendentsFirma(boolean pendentsFirma) {
		this.pendentsFirma = pendentsFirma;
	}
	public boolean isExpedientNti() {
		return expedientNti;
	}
	public void setExpedientNti(boolean expedientNti) {
		this.expedientNti = expedientNti;
	}
	public List<DocumentFinalitzarDto> getDocumentsPendentsFirma() {
		return documentsPendentsFirma;
	}
	public void setDocumentsPendentsFirma(List<DocumentFinalitzarDto> documentsPendentsFirma) {
		this.documentsPendentsFirma = documentsPendentsFirma;
	}
}