/**
 * 
 */
package es.caib.helium.integracio.plugins.tramitacio;

import java.util.List;


/**
 * 
 * @author Limit Tecnologies
 */
public class Event {

	protected String titol;
	protected String text;
	protected String textSMS;
	protected String enllasConsulta;
	protected List<DocumentEvent> documents;

	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTextSMS() {
		return textSMS;
	}
	public void setTextSMS(String textSMS) {
		this.textSMS = textSMS;
	}
	public String getEnllasConsulta() {
		return enllasConsulta;
	}
	public void setEnllasConsulta(String enllasConsulta) {
		this.enllasConsulta = enllasConsulta;
	}
	public List<DocumentEvent> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentEvent> documents) {
		this.documents = documents;
	}

}
