package net.conselldemallorca.helium.webapp.v3.command;


import org.apache.commons.lang.builder.ToStringBuilder;

import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaEstatEnumDto;
import net.conselldemallorca.helium.webapp.v3.command.UnitatOrganitzativaCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.UnitatOrganitzativaCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.validator.Avis;

/**
 * Command per al manteniment d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Avis(groups = { Creacio.class, Modificacio.class })
public class UnitatOrganitzativaCommand {
	
	
	private String codi;
	private String denominacio;
	
	private String codiUnitatSuperior;
	private UnitatOrganitzativaEstatEnumDto estat;
	
	

	public String getCodiUnitatSuperior() {
		return codiUnitatSuperior;
	}
	public void setCodiUnitatSuperior(String codiUnitatSuperior) {
		this.codiUnitatSuperior = codiUnitatSuperior;
	}

	public UnitatOrganitzativaEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(UnitatOrganitzativaEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getDenominacio() {
		return denominacio;
	}
	public void setDenominacio(String denominacio) {
		this.denominacio = denominacio;
	}
	public static UnitatOrganitzativaCommand asCommand(UnitatOrganitzativaDto dto) {
		UnitatOrganitzativaCommand command = ConversioTipusHelper.convertir(
				dto,
				UnitatOrganitzativaCommand.class);
		return command;
	}
	public static UnitatOrganitzativaDto asDto(UnitatOrganitzativaCommand command) {
		UnitatOrganitzativaDto dto = ConversioTipusHelper.convertir(
				command,
				UnitatOrganitzativaDto.class);
		return dto;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public interface Creacio {};
	public interface Modificacio {};
}
