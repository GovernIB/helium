/**
 * 
 */
package net.conselldemallorca.helium.core.model.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;


/**
 * DTO amb informació d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientDto extends Expedient {

	private PersonaDto iniciadorPersona;
	private PersonaDto responsablePersona;
	private String bantelEntradaNum;
	private List<ExpedientDto> relacionats;

	public PersonaDto getIniciadorPersona() {
		return iniciadorPersona;
	}
	public void setIniciadorPersona(PersonaDto iniciadorPersona) {
		this.iniciadorPersona = iniciadorPersona;
	}
	public PersonaDto getResponsablePersona() {
		return responsablePersona;
	}
	public void setResponsablePersona(PersonaDto responsablePersona) {
		this.responsablePersona = responsablePersona;
	}
	public String getBantelEntradaNum() {
		return bantelEntradaNum;
	}
	public void setBantelEntradaNum(String bantelEntradaNum) {
		this.bantelEntradaNum = bantelEntradaNum;
	}
	public List<ExpedientDto> getRelacionats() {
		return relacionats;
	}
	public void setRelacionats(List<ExpedientDto> relacionats) {
		this.relacionats = relacionats;
	}

	public void addExpedientRelacionat(ExpedientDto relacionat) {
		if (relacionats == null)
			relacionats = new ArrayList<ExpedientDto>();
		relacionats.add(relacionat);
	}

	public String getIdentificadorLimitat() {
		if (getIdentificador() != null && getIdentificador().length() > 100)
			return StringEscapeUtils.escapeHtml(getIdentificador().substring(0, 100) + " (...)");
		else
			return StringEscapeUtils.escapeHtml(getIdentificador());
	}

	private static final long serialVersionUID = 1L;
}
