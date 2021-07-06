/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import es.caib.helium.logic.intf.dto.ExpedientTipusDto;

/**
 * Command per modificar les metadades nti d'un tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusMetadadesNtiCommand {

	private boolean actiu;
	@Size(max = 256, groups = {Modificacio.class})
	private String organo;
	@Size(max = 44, groups = {Modificacio.class})
	private String clasificacion;
	@Size(max = 16, groups = {Modificacio.class})
	private String serieDocumental;
	private boolean arxiuActiu;

	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}
	public String getOrgano() {
		return organo;
	}
	public void setOrgano(String organo) {
		this.organo = organo;
	}
	public String getClasificacion() {
		return clasificacion;
	}
	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}
	public String getSerieDocumental() {
		return serieDocumental;
	}
	public void setSerieDocumental(String serieDocumental) {
		this.serieDocumental = serieDocumental;
	}
	public boolean isArxiuActiu() {
		return arxiuActiu;
	}
	public void setArxiuActiu(boolean arxiuActiu) {
		this.arxiuActiu = arxiuActiu;
	}

	public static ExpedientTipusMetadadesNtiCommand toCommand(ExpedientTipusDto dto) {
		ExpedientTipusMetadadesNtiCommand command = new ExpedientTipusMetadadesNtiCommand();
		command.setActiu(dto.isNtiActiu());
		command.setOrgano(dto.getNtiOrgano());
		command.setClasificacion(dto.getNtiClasificacion());
		command.setSerieDocumental(dto.getNtiSerieDocumental());
		command.setArxiuActiu(dto.isArxiuActiu());
		return command;
	}
	
	public interface Modificacio {}

}
