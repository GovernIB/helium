package es.caib.helium.back.command;


import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.helium.back.command.UnitatOrganitzativaCommand.Creacio;
import es.caib.helium.back.command.UnitatOrganitzativaCommand.Modificacio;
import es.caib.helium.back.helper.ConversioTipus;
import es.caib.helium.back.validator.Avis;
import es.caib.helium.commons.dto.UnitatOrganitzativaDto;
import es.caib.helium.commons.dto.UnitatOrganitzativaEstatEnumDto;

/**
 * Command per al manteniment d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Avis(groups = { Creacio.class, Modificacio.class })
public class UnitatOrganitzativaCommand {
	
	
	private String codi;
	private String denominacio;
	private String cif;
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
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif;
	}
	public static UnitatOrganitzativaCommand asCommand(UnitatOrganitzativaDto dto) {
		UnitatOrganitzativaCommand command = ConversioTipus.convertir(
				dto,
				UnitatOrganitzativaCommand.class);
		return command;
	}
	public static UnitatOrganitzativaDto asDto(UnitatOrganitzativaCommand command) {
		UnitatOrganitzativaDto dto = ConversioTipus.convertir(
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
