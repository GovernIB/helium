/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * DTO amb informació d'una àrea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientErrorDto {

	public ExpedientErrorDto(ErrorTipusDto errorTipus, String text) {
		super();
		this.errorTipus = errorTipus;
		this.text = text;
	}
	public enum ErrorTipusDto {
		BASIC,
		INTEGRACIONS
	}

	private ErrorTipusDto errorTipus;
	private String text;
	
	public ErrorTipusDto getErrorTipus() {
		return errorTipus;
	}
	public void setErrorTipus(ErrorTipusDto errorTipus) {
		this.errorTipus = errorTipus;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
