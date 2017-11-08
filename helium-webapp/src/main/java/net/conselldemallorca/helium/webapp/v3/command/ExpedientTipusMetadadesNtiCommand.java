/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;

/**
 * Command per modificar les metadades nti d'un tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusMetadadesNtiCommand {

	private boolean ntiActiu;
	@Size(max = 64, groups = {Modificacio.class})
	private String organisme;
	@Size(max = 64, groups = {Modificacio.class})
	private String classificacio;
	@Size(max = 64, groups = {Modificacio.class})
	private String serieDocumental;
	
	@Size(max = 64, groups = {Modificacio.class})
	private String ntiTipoFirma;
	@Size(max = 128, groups = {Modificacio.class})
	private String ntiValorCsv;
	@Size(max = 128, groups = {Modificacio.class})
	private String ntiDefGenCsv;
	
	
	
	public boolean isNtiActiu() {
		return ntiActiu;
	}
	public void setNtiActiu(boolean ntiActiu) {
		this.ntiActiu = ntiActiu;
	}
	
	public String getOrganisme() {
		return organisme;
	}
	public void setOrganisme(String organisme) {
		this.organisme = organisme;
	}
	
	public String getClassificacio() {
		return classificacio;
	}
	public void setClassificacio(String classificacio) {
		this.classificacio = classificacio;
	}
	
	public String getSerieDocumental() {
		return serieDocumental;
	}
	public void setSerieDocumental(String serieDocumental) {
		this.serieDocumental = serieDocumental;
	}
	
	public String getNtiTipoFirma() {
		return ntiTipoFirma;
	}
	public void setNtiTipoFirma(String ntiTipoFirma) {
		this.ntiTipoFirma = ntiTipoFirma;
	}
	
	public String getNtiValorCsv() {
		return ntiValorCsv;
	}
	public void setNtiValorCsv(String ntiValorCsv) {
		this.ntiValorCsv = ntiValorCsv;
	}
	
	public String getNtiDefGenCsv() {
		return ntiDefGenCsv;
	}
	public void setNtiDefGenCsv(String ntiDefGenCsv) {
		this.ntiDefGenCsv = ntiDefGenCsv;
	}
	
	
	public static ExpedientTipusMetadadesNtiCommand toCommand(ExpedientTipusDto dto) {
		
		ExpedientTipusMetadadesNtiCommand command = new ExpedientTipusMetadadesNtiCommand();
		
		command.setNtiActiu(dto.isNtiActiu());
		command.setOrganisme(dto.getNtiOrgan());
		command.setClassificacio(dto.getNtiClasificacio());
		command.setSerieDocumental(dto.getNtiSerieDocumental());
		command.setNtiTipoFirma(dto.getNtiTipoFirma());
		command.setNtiValorCsv(dto.getNtiValorCsv());
		command.setNtiDefGenCsv(dto.getNtiDefGenCsv());
		
		return command;
	}
	
	
	public interface Modificacio {}
}
