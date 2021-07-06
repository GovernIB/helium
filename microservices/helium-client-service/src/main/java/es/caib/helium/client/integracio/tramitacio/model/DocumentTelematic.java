package es.caib.helium.client.integracio.tramitacio.model;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentTelematic {

	private String arxiuNom;
	private String arxiuExtensio;
	private byte[] arxiuContingut;
	private String referenciaGestorDocumental;
	private List<Signatura> signatures;

	private long referenciaCodi;
	private String referenciaClau;
	private Boolean estructurat;
	
	@Override
	public String toString() {
		return "DocumentTelematic [arxiuNom=" + arxiuNom + ", arxiuExtensio=" + arxiuExtensio + ", arxiuContingut=" + Arrays.toString(arxiuContingut) + ", referenciaGestorDocumental=" + referenciaGestorDocumental + ", signatures=" + signatures + ", referenciaCodi=" + referenciaCodi + ", referenciaClau=" + referenciaClau + ", estructurat=" + estructurat + "]";
	}
}
