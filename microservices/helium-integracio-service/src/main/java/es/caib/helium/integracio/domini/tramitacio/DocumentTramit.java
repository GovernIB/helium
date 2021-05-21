package es.caib.helium.integracio.domini.tramitacio;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentTramit {

	private String nom;
	private String identificador;
	private int instanciaNumero;
	private DocumentTelematic documentTelematic;
	private DocumentPresencial documentPresencial;

	@Override
	public String toString() {
		return "DocumentTramit [nom=" + nom + ", identificador=" + identificador + ", instanciaNumero=" + instanciaNumero + ", documentTelematic=" + documentTelematic + ", documentPresencial=" + documentPresencial + "]";
	}
}
