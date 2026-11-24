package es.caib.helium.bpmn.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ExpedientInfo {

	public enum IniciadorTipus {
		INTERN,
		SISTRA}

	private Long id;
	private String titol;
	private String numero;
	private String numeroDefault;
	private Date dataInici;
	private Date dataFi;
	private String comentari;
	private String infoAturat;
	private String comentariAnulat;
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

	private boolean ntiActiu;
	private String ntiVersion;
	private String ntiIdentificador;
	private String ntiOrgano;
	private String ntiSerieDocumental;
	private String ntiClasificacion;
	private String ntiTipoFirma;
	private String ntiCsv;
	private String ntiDefGenCsv;
	private boolean arxiuActiu;
	private String arxiuUuid;

	private String estatCodi;
	private String estatNom;
	private String expedientTipusCodi;
	private String expedientTipusNom;
	private String entornCodi;
	private String entornNom;

	private String processInstanceId;
	private boolean ambRetroaccio;
}
