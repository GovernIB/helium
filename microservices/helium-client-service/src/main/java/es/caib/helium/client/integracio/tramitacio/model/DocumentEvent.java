package es.caib.helium.client.integracio.tramitacio.model;

import es.caib.helium.client.integracio.tramitacio.enums.DocumentEventTipus;
import lombok.Data;

@Data
public class DocumentEvent {

	private String nom;
	private DocumentEventTipus tipus;
	private String referencia;
	private String arxiuNom;
	private byte[] arxiuContingut;
}
