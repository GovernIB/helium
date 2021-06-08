package es.caib.helium.integracio.domini.registre;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DadesNotificacio extends DadesAnotacio {

	private boolean justificantRecepcio;
	private String avisTitol;
	private String avisText;
	private String avisTextSms;
	private String oficiTitol;
	private String oficiText;
	private TramitSubsanacio oficiTramitSubsanacio;
}
