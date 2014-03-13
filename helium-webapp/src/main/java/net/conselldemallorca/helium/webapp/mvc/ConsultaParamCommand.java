/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusParamConsultaCamp;

import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Command pel canvi de contrasenya del perfil
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaParamCommand {

	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String descripcio;
	@NotNull
	private TipusParamConsultaCamp tipus;



	public ConsultaParamCommand() {}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public TipusParamConsultaCamp getTipus() {
		return tipus;
	}
	public void setTipus(TipusParamConsultaCamp tipus) {
		this.tipus = tipus;
	}
	public void setTipus(String tipus) {
		if (tipus == null)
			this.tipus = null;
		else
			this.tipus = TipusParamConsultaCamp.valueOf(tipus);
	}

}
