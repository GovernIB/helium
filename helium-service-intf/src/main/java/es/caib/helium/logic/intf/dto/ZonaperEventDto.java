/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import java.util.List;

/**
 * DTO amb informació d'un event de la zonaper.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ZonaperEventDto {

	protected String titol;
	protected String text;
	protected String textSMS;
	protected String enllasConsulta;
	protected List<ZonaperDocumentDto> documents;

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
	public List<ZonaperDocumentDto> getDocuments() {
		return documents;
	}
	public void setDocuments(List<ZonaperDocumentDto> documents) {
		this.documents = documents;
	}

}
