/**
 * 
 */
package net.conselldemallorca.helium.core.model.dto;

import net.conselldemallorca.helium.core.model.hibernate.Camp;


/**
 * DTO amb informació d'un camp de l'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientDadaDto {

	private Camp camp;
	private Object valor;



	public Camp getCamp() {
		return camp;
	}
	public void setCamp(Camp camp) {
		this.camp = camp;
	}

	public Object getValor() {
		return valor;
	}
	public void setValor(Object valor) {
		this.valor = valor;
	}

}
