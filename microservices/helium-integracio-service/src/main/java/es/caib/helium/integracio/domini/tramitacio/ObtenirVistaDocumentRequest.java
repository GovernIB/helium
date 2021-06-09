package es.caib.helium.integracio.domini.tramitacio;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObtenirVistaDocumentRequest {
	
	
	//TODO FALTA @VALID
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
