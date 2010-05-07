/**
 * 
 */
package net.conselldemallorca.helium.model.dto;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.model.hibernate.Expedient;


/**
 * DTO amb informació d'un expedient
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ExpedientDto extends Expedient {

	private Persona iniciadorPersona;
	private Persona responsablePersona;
	private String bantelEntradaNum;



	public Persona getIniciadorPersona() {
		return iniciadorPersona;
	}
	public void setIniciadorPersona(Persona iniciadorPersona) {
		this.iniciadorPersona = iniciadorPersona;
	}
	public Persona getResponsablePersona() {
		return responsablePersona;
	}
	public void setResponsablePersona(Persona responsablePersona) {
		this.responsablePersona = responsablePersona;
	}
	public String getBantelEntradaNum() {
		return bantelEntradaNum;
	}
	public void setBantelEntradaNum(String bantelEntradaNum) {
		this.bantelEntradaNum = bantelEntradaNum;
	}



	private static final long serialVersionUID = 1L;

}
