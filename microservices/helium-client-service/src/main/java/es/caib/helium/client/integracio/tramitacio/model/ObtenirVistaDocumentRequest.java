package es.caib.helium.client.integracio.tramitacio.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObtenirVistaDocumentRequest {
	
	
	protected String referenciaGD;
	protected long referenciaCodi;
	protected String referenciaClau;
	protected String plantillaTipus;
	protected String idioma;

	@Override
	public String toString() {
		return "[" + referenciaCodi + ", " + referenciaClau + "]";
	}
}
