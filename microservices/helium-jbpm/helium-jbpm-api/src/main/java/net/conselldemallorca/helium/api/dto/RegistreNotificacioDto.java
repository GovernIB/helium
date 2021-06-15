/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO amb informació d'una notificació telemàtica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
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

	@Getter @Setter
	public class RegistreNotificacioTramitSubsanacioParametreDto {
		private String parametre;
		private String valor;
	}

}
