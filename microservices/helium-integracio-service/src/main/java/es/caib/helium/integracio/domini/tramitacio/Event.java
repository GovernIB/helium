package es.caib.helium.integracio.domini.tramitacio;

import java.util.List;

import lombok.Data;

@Data
public class Event {

	// TODO FALTA AFEGIR VALIDACIONS
	private String titol;
	private String text;
	private String textSMS;
	private String enllasConsulta;
	private List<DocumentEvent> documents;
}
