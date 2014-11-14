package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;

/**
 * Resposta a una petició per obtenir justificant de recepció
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaJustificantRecepcioDto {

	private Date data;

	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}

}
