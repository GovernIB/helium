package es.caib.helium.client.tramitacio.v1;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TramitacioExpedientInfo {
	public enum IniciadorTipus {INTERN,SISTRA}

	private String titol;
	private String numero;
	private String numeroDefault;
	private String identificador;
	private Date dataInici;
	private Date dataFi;
	private String comentari;
	private String infoAturat;
	private IniciadorTipus iniciadorTipus;
	private String iniciadorCodi;
	private String responsableCodi;

	private Double geoPosX;
	private Double geoPosY;
	private String geoReferencia;
	private String registreNumero;
	private Date registreData;
	private Long unitatAdministrativa;
	private String idioma;
	private boolean autenticat;
	private String tramitadorNif;
	private String tramitadorNom;
	private String interessatNif;
	private String interessatNom;
	private String representantNif;
	private String representantNom;
	private boolean avisosHabilitats;
	private String avisosEmail;
	private String avisosMobil;
	private boolean notificacioTelematicaHabilitada;
	private String tramitExpedientIdentificador;
	private String tramitExpedientClau;

	private String estatCodi;
	private String estatNom;
	private String expedientTipusCodi;
	private String expedientTipusNom;
	private String entornCodi;

	private String processInstanceId;
}
