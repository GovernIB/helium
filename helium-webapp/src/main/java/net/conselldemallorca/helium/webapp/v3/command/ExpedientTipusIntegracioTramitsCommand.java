/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Command per modificar les dades d'integració dels tipus d'expedient amb Sistra.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusIntegracioTramitsCommand {

	@NotNull(groups = {Modificacio.class})
	private Long id;
	private boolean actiu;
	@Size(max = 64, groups = {Modificacio.class})
	private String tramitCodi;
	
	private boolean notificacionsActivades;
	@NotNull(groups = {Modificacio.class})
	private String notificacioOrganCodi;
	@NotNull(groups = {Modificacio.class})
	private String notificacioOficinaCodi;
	@NotNull(groups = {Modificacio.class})
	private String notificacioUnitatAdministrativa;
	@NotNull(groups = {Modificacio.class})
	private String notificacioCodiProcediment;
	private String notificacioAvisTitol;
	private String notificacioAvisText;
	private String notificacioAvisTextSms;
	private String notificacioOficiTitol;
	private String notificacioOficiText;
	
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
	public String getTramitCodi() {
		return tramitCodi;
	}
	public void setTramitCodi(String tramitCodi) {
		this.tramitCodi = tramitCodi;
	}
	
	public boolean isNotificacionsActivades() {
		return notificacionsActivades;
	}
	public void setNotificacionsActivades(boolean notificacionsActivades) {
		this.notificacionsActivades = notificacionsActivades;
	}
	public String getNotificacioOrganCodi() {
		return notificacioOrganCodi;
	}
	public void setNotificacioOrganCodi(String notificacioOrganCodi) {
		this.notificacioOrganCodi = notificacioOrganCodi;
	}
	public String getNotificacioOficinaCodi() {
		return notificacioOficinaCodi;
	}
	public void setNotificacioOficinaCodi(String notificacioOficinaCodi) {
		this.notificacioOficinaCodi = notificacioOficinaCodi;
	}
	public String getNotificacioUnitatAdministrativa() {
		return notificacioUnitatAdministrativa;
	}
	public void setNotificacioUnitatAdministrativa(String notificacioUnitatAdministrativa) {
		this.notificacioUnitatAdministrativa = notificacioUnitatAdministrativa;
	}
	public String getNotificacioCodiProcediment() {
		return notificacioCodiProcediment;
	}
	public void setNotificacioCodiProcediment(String notificacioCodiProcediment) {
		this.notificacioCodiProcediment = notificacioCodiProcediment;
	}
	public String getNotificacioAvisTitol() {
		return notificacioAvisTitol;
	}
	public void setNotificacioAvisTitol(String notificacioAvisTitol) {
		this.notificacioAvisTitol = notificacioAvisTitol;
	}
	public String getNotificacioAvisText() {
		return notificacioAvisText;
	}
	public void setNotificacioAvisText(String notificacioAvisText) {
		this.notificacioAvisText = notificacioAvisText;
	}
	public String getNotificacioAvisTextSms() {
		return notificacioAvisTextSms;
	}
	public void setNotificacioAvisTextSms(String notificacioAvisTextSms) {
		this.notificacioAvisTextSms = notificacioAvisTextSms;
	}
	public String getNotificacioOficiTitol() {
		return notificacioOficiTitol;
	}
	public void setNotificacioOficiTitol(String notificacioOficiTitol) {
		this.notificacioOficiTitol = notificacioOficiTitol;
	}
	public String getNotificacioOficiText() {
		return notificacioOficiText;
	}
	public void setNotificacioOficiText(String notificacioOficiText) {
		this.notificacioOficiText = notificacioOficiText;
	}

	public interface Modificacio {}
}
