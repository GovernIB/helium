package es.caib.helium.client.integracio.tramitacio.model;

import java.util.List;

import lombok.Data;

@Data
public class Event {

	private String titol;
	private String text;
	private String textSMS;
	private String enllasConsulta;
	private List<DocumentEvent> documents;
}
