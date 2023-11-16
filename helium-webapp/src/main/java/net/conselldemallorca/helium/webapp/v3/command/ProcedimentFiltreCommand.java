package net.conselldemallorca.helium.webapp.v3.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentFiltreDto;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;

/** Command pel filtre de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentFiltreCommand {
	
	private String codi;
	private String nom;
	private String codiSia;
	private ProcedimentEstatEnumDto estat;
	private String unitatOrganitzativa;
	
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCodiSia() {
		return codiSia;
	}
	public void setCodiSia(String codiSia) {
		this.codiSia = codiSia;
	}
	public ProcedimentEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(ProcedimentEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}
	public void setUnitatOrganitzativa(String unitatOrganitzativa) {
		this.unitatOrganitzativa = unitatOrganitzativa;
	}
	
	public static ProcedimentFiltreCommand asCommand(ProcedimentFiltreDto dto) {
		ProcedimentFiltreCommand command = ConversioTipusHelper.convertir(
				dto, 
				ProcedimentFiltreCommand.class);
		return command;
	}
	
	public static ProcedimentFiltreDto asDto(ProcedimentFiltreCommand command) {
		ProcedimentFiltreDto dto = ConversioTipusHelper.convertir(
				command, 
				ProcedimentFiltreDto.class);
		return dto;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}
