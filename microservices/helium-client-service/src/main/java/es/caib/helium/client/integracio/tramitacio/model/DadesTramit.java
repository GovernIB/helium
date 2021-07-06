package es.caib.helium.client.integracio.tramitacio.model;

import java.util.Date;
import java.util.List;

import es.caib.helium.client.integracio.tramitacio.enums.AutenticacioTipus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DadesTramit {

	private String numero;
	private long clauAcces;
	private String identificador;
	private long unitatAdministrativa;
	private int versio;
	private Date data;
	private String idioma;
	private String registreNumero;
	private Date registreData;
	private String preregistreTipusConfirmacio;
	private String preregistreNumero;
	private Date preregistreData;
	private AutenticacioTipus autenticacioTipus;
	private String tramitadorNif;
	private String tramitadorNom;
	private String interessatNif;
	private String interessatNom;
	private String representantNif;
	private String representantNom;
	private boolean signat;
	private boolean avisosHabilitats;
	private String avisosSms;
	private String avisosEmail;
	private boolean notificacioTelematicaHabilitada;
	private List<DocumentTramit> documents;

	@Override
	public String toString() {
		return "DadesTramit [numero=" + numero + ", clauAcces=" + clauAcces + ", identificador=" + identificador + ", unitatAdministrativa=" + unitatAdministrativa + ", versio=" + versio + ", data=" + data + ", idioma=" + idioma + ", registreNumero=" + registreNumero + ", registreData=" + registreData + ", preregistreTipusConfirmacio=" + preregistreTipusConfirmacio + ", preregistreNumero=" + preregistreNumero + ", preregistreData=" + preregistreData + ", autenticacioTipus=" + autenticacioTipus + ", tramitadorNif=" + tramitadorNif + ", tramitadorNom=" + tramitadorNom + ", interessatNif=" + interessatNif + ", interessatNom=" + interessatNom + ", representantNif=" + representantNif + ", representantNom=" + representantNom + ", signat=" + signat + ", avisosHabilitats=" + avisosHabilitats
				+ ", avisosSms=" + avisosSms + ", avisosEmail=" + avisosEmail + ", notificacioTelematicaHabilitada=" + notificacioTelematicaHabilitada + ", documents=" + documents + "]";
	}
}
