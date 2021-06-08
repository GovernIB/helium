package es.caib.helium.integracio.domini.tramitacio;

import es.caib.helium.integracio.enums.tramitacio.DocumentEventTipus;
import lombok.Data;

@Data
public class DocumentEvent {

	//TODO FALTA AFEGIR VALIDACIONS
	private String nom;
	private DocumentEventTipus tipus;
	private String referencia;
	private String arxiuNom;
	private byte[] arxiuContingut;
}
