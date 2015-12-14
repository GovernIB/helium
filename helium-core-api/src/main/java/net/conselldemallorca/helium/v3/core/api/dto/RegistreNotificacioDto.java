/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO amb informació d'una notificació telemàtica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreNotificacioDto extends RegistreAnotacioDto {

	private String expedientIdentificador;
	private String expedientClau;
	private String expedientUnitatAdministrativa;

	private boolean notificacioJustificantRecepcio;
	private String notificacioAvisTitol;
	private String notificacioAvisText;
	private String notificacioAvisTextSms;
	private String notificacioOficiTitol;
	private String notificacioOficiText;

	private String tramitSubsanacioIdentificador;
	private int tramitSubsanacioVersio;
	private String tramitSubsanacioDescripcio;
	private List<RegistreNotificacioTramitSubsanacioParametreDto> tramitSubsanacioParametres;

	public String getExpedientIdentificador() {
		return expedientIdentificador;
	}
	public void setExpedientIdentificador(String expedientIdentificador) {
		this.expedientIdentificador = expedientIdentificador;
	}
	public String getExpedientClau() {
		return expedientClau;
	}
	public void setExpedientClau(String expedientClau) {
		this.expedientClau = expedientClau;
	}
	public String getExpedientUnitatAdministrativa() {
		return expedientUnitatAdministrativa;
	}
	public void setExpedientUnitatAdministrativa(
			String expedientUnitatAdministrativa) {
		this.expedientUnitatAdministrativa = expedientUnitatAdministrativa;
	}
	public boolean isNotificacioJustificantRecepcio() {
		return notificacioJustificantRecepcio;
	}
	public void setNotificacioJustificantRecepcio(
			boolean notificacioJustificantRecepcio) {
		this.notificacioJustificantRecepcio = notificacioJustificantRecepcio;
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
	public String getTramitSubsanacioIdentificador() {
		return tramitSubsanacioIdentificador;
	}
	public void setTramitSubsanacioIdentificador(
			String tramitSubsanacioIdentificador) {
		this.tramitSubsanacioIdentificador = tramitSubsanacioIdentificador;
	}
	public int getTramitSubsanacioVersio() {
		return tramitSubsanacioVersio;
	}
	public void setTramitSubsanacioVersio(int tramitSubsanacioVersio) {
		this.tramitSubsanacioVersio = tramitSubsanacioVersio;
	}
	public String getTramitSubsanacioDescripcio() {
		return tramitSubsanacioDescripcio;
	}
	public void setTramitSubsanacioDescripcio(String tramitSubsanacioDescripcio) {
		this.tramitSubsanacioDescripcio = tramitSubsanacioDescripcio;
	}
	public List<RegistreNotificacioTramitSubsanacioParametreDto> getTramitSubsanacioParametres() {
		return tramitSubsanacioParametres;
	}
	public void setTramitSubsanacioParametres(
			List<RegistreNotificacioTramitSubsanacioParametreDto> tramitSubsanacioParametres) {
		this.tramitSubsanacioParametres = tramitSubsanacioParametres;
	}

	public void afegirTramitSubsanacioParametre(
			String nom,
			String valor) {
		if (tramitSubsanacioParametres == null) {
			tramitSubsanacioParametres = new ArrayList<RegistreNotificacioTramitSubsanacioParametreDto>();
		}
		RegistreNotificacioTramitSubsanacioParametreDto param = new RegistreNotificacioTramitSubsanacioParametreDto();
		param.setParametre(nom);
		param.setValor(valor);
		tramitSubsanacioParametres.add(param);
	}

	public class RegistreNotificacioTramitSubsanacioParametreDto {
		private String parametre;
		private String valor;
		public String getParametre() {
			return parametre;
		}
		public void setParametre(String parametre) {
			this.parametre = parametre;
		}
		public String getValor() {
			return valor;
		}
		public void setValor(String valor) {
			this.valor = valor;
		}

	}

}
