/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO amb informació d'un expedient de la zonaper.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ZonaperExpedientDto {

	protected String expedientIdentificador;
	protected String expedientClau;
	protected long unitatAdministrativa;
	protected String idioma;
	protected String descripcio;
	protected boolean autenticat;
	protected String representantNif;
	protected String representatNif;
	protected String representatNom;
	protected String representatApe1;
	protected String representatApe2;
	protected String tramitNumero;
	protected boolean avisosHabilitat;
	protected String avisosSMS;
	protected String avisosEmail;
	protected List<ZonaperEventDto> events;
	protected String codiProcediment;

	@Override
	public String toString() {
		return "ZonaperExpedientDto [expedientIdentificador=" + expedientIdentificador + ", expedientClau=" + expedientClau + ", unitatAdministrativa=" + unitatAdministrativa + ", idioma=" + idioma + ", descripcio=" + descripcio + ", autenticat=" + autenticat + ", representantNif=" + representantNif + ", representatNif=" + representatNif + ", representatNom=" + representatNom + ", representatApe1=" + representatApe1 + ", representatApe2=" + representatApe2 + ", tramitNumero=" + tramitNumero + ", avisosHabilitat=" + avisosHabilitat + ", avisosSMS=" + avisosSMS + ", avisosEmail=" + avisosEmail + ", events=" + events + "]";
	}
}
