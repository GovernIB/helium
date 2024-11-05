/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioDistribucioCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusIntegracioDistribucio;
/**
 * Command per modificar les dades d'integració amb distribució.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusIntegracioDistribucio(groups = {Modificacio.class})
public class ExpedientTipusIntegracioDistribucioCommand {

//	public enum SiNo {
//		SI,
//		NO
//	}

	@NotNull(groups = {Modificacio.class})
	private Long id;
	private boolean actiu;
	@Size(max = 200, groups = {Modificacio.class})
	private String codiProcediment;
	@Size(max = 20, groups = {Modificacio.class})
	private String codiAssumpte;
	private boolean procesAuto;
	private boolean sistra;
	private Boolean presencial;
	private boolean enviarCorreuAnotacions;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}
	public String getCodiProcediment() {
		return codiProcediment;
	}
	public void setCodiProcediment(String codiProcediment) {
		this.codiProcediment = codiProcediment;
	}
	public String getCodiAssumpte() {
		return codiAssumpte;
	}
	public void setCodiAssumpte(String codiAssumpte) {
		this.codiAssumpte = codiAssumpte;
	}
	
	public boolean isProcesAuto() {
		return procesAuto;
	}
	public void setProcesAuto(boolean procesAuto) {
		this.procesAuto = procesAuto;
	}
	public boolean isSistra() {
		return sistra;
	}
	public void setSistra(boolean sistra) {
		this.sistra = sistra;
	}

	public Boolean getPresencial() {
		return presencial;
	}
	public void setPresencial(Boolean presencial) {
		this.presencial = presencial;
	}

	public boolean isEnviarCorreuAnotacions() {
		return enviarCorreuAnotacions;
	}
	public void setEnviarCorreuAnotacions(boolean enviarCorreuAnotacions) {
		this.enviarCorreuAnotacions = enviarCorreuAnotacions;
	}

	public interface Modificacio {}
}
