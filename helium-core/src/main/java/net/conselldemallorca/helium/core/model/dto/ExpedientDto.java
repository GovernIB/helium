/**
 * 
 */
package net.conselldemallorca.helium.core.model.dto;

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

	public String getIdentificadorLimitat() {
		if (getIdentificador() != null && getIdentificador().length() > 100)
			return getIdentificador().substring(0, 100) + " (...)";
		else
			return getIdentificador();
	}



	private static final long serialVersionUID = 1L;

}
