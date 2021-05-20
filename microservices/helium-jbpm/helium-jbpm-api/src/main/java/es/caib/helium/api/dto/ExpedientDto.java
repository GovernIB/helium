/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * DTO amb informació d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@NoArgsConstructor
public class ExpedientDto extends ControlPermisosDto implements Serializable {
	
	private static final String SEPARADOR_SISTRA = "#";

	public enum EstatTipusDto {
		INICIAT,
		FINALITZAT,
		CUSTOM
	}
	public enum IniciadorTipusDto {
		INTERN,
		SISTRA
	}

	private Long id;

	private String processInstanceId;
	private String titol;
	private String numero;
	private String numeroDefault;
	private Date dataInici = new Date();
	private Date dataFi;
	private String comentari;
	private String infoAturat;
	private IniciadorTipusDto iniciadorTipus;
	private String iniciadorCodi;
	private String responsableCodi;
	private boolean anulat;
	private String grupCodi;
	private String comentariAnulat;
	private Double geoPosX;

	private Double geoPosY;
	private String geoReferencia;
	private String registreNumero;

	private Date registreData;
	private Long unitatAdministrativa;
	private String idioma;
	private boolean autenticat;
	protected String tramitadorNif;
	protected String tramitadorNom;
	protected String interessatNif;
	protected String interessatNom;
	protected String representantNif;
	protected String representantNom;
	private boolean avisosHabilitats = false;
	private String avisosEmail;
	private String avisosMobil;
	private boolean notificacioTelematicaHabilitada = false;
	private String tramitExpedientIdentificador;
	private String tramitExpedientClau;
	private String bantelEntradaNum;
	private EntornDto entorn;

	private ExpedientTipusDto tipus;
	private EstatTipusDto estatTipus;
	private EstatDto estat;
	private String errorDesc;

	private String errorFull;
	private List<InteressatDto> interessats;

	private List<NotificacioDto> notificacions;
	private boolean errorsIntegracions;

	private PersonaDto iniciadorPersona;

	private PersonaDto responsablePersona;
	private PermisDto permisos;

	private boolean usuariActualRead;
	private boolean ambRetroaccio;

	private Date reindexarData;
	private boolean reindexarError;
	private Long alertesTotals;

	private Long alertesPendents;
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


	public ExpedientDto(IniciadorTipusDto iniciadorTipus, String iniciadorCodi, ExpedientTipusDto tipus, EntornDto entorn, String processInstanceId) {
		this.iniciadorTipus = iniciadorTipus;
		this.iniciadorCodi = iniciadorCodi;
		this.tipus = tipus;
		this.entorn = entorn;
		this.processInstanceId = processInstanceId;
	}

	public String getInteressat() {
		if(interessatNif == null) return null;
		return interessatNom + " (" + interessatNif + ")";
	}

	public String getEstatNom() {
		if (getEstat() != null)
			return estat.getNom();
		else if (getDataFi() != null)
			return "Finalizat";
		return "Iniciat"; 
	}

	public String getNumeroIdentificador() {
		if (tipus.isTeNumero())
			return getNumero();
		return this.getNumeroDefault();
	}

	public String getIdentificador() {
		String identificador = null;
		if (tipus.isTeNumero() && tipus.isTeTitol())
			identificador = "[" + getNumero() + "] " + getTitol();
		else if (tipus.isTeNumero() && !tipus.isTeTitol())
			identificador = getNumero();
		else if (!tipus.isTeNumero() && tipus.isTeTitol())
			identificador = getTitol();
		if (identificador == null || "[null] null".equals(identificador))
			return this.getNumeroDefault();
		else
			return identificador;
	}

	public String getIdentificadorOrdenacio() {
		if (!tipus.isTeNumero() && tipus.isTeTitol()) {
			return getIdentificador();
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataInici);
			int anyInici = cal.get(Calendar.YEAR);
			return new Integer(anyInici).toString() + new DecimalFormat("0000000000000000000").format(id);
		}
	}
	public String getIdentificadorLimitat() {
		if (getIdentificador() != null && getIdentificador().length() > 100)
			return getIdentificador().substring(0, 100) + " (...)";
		else
			return getIdentificador();
	}

	public String getNumeroEntradaSistra() {
		if (iniciadorTipus.equals(IniciadorTipusDto.SISTRA)) {
			return iniciadorCodi.split(SEPARADOR_SISTRA)[0];
		}
		return null;
	}
	public String getClaveAccesoSistra() {
		if (iniciadorTipus.equals(IniciadorTipusDto.SISTRA)) {
			return iniciadorCodi.split(SEPARADOR_SISTRA)[1];
		}
		return null;
	}

	public static String crearIniciadorCodiPerSistra(String numeroEntrada, String claveAcceso) {
		return numeroEntrada + SEPARADOR_SISTRA + claveAcceso;
	}

	public boolean isAturat() {
		return infoAturat != null;
	}
	public String getIdentificacioPerLogs() {
		return getIdentificadorLimitat();
	}
	
	public boolean isAmbErrors () {
		return (reindexarError || errorsIntegracions || errorDesc != null);
	}

	@Override
	public String toString() {
		return "ExpedientDto [id=" + id + ", processInstanceId=" + processInstanceId + ", titol=" + titol + ", numero=" + numero + ", numeroDefault=" + numeroDefault + ", dataInici=" + dataInici + ", dataFi=" + dataFi + ", comentari=" + comentari + ", infoAturat=" + infoAturat + ", iniciadorTipus=" + iniciadorTipus + ", iniciadorCodi=" + iniciadorCodi + ", responsableCodi=" + responsableCodi + ", anulat=" + anulat + ", grupCodi=" + grupCodi + ", comentariAnulat=" + comentariAnulat + ", geoPosX=" + geoPosX + ", geoPosY=" + geoPosY + ", geoReferencia=" + geoReferencia + ", registreNumero=" + registreNumero + ", registreData=" + registreData + ", unitatAdministrativa=" + unitatAdministrativa + ", idioma=" + idioma + ", autenticat=" + autenticat + ", tramitadorNif=" + tramitadorNif
				+ ", tramitadorNom=" + tramitadorNom + ", interessatNif=" + interessatNif + ", interessatNom=" + interessatNom + ", representantNif=" + representantNif + ", representantNom=" + representantNom + ", avisosHabilitats=" + avisosHabilitats + ", avisosEmail=" + avisosEmail + ", avisosMobil=" + avisosMobil + ", notificacioTelematicaHabilitada=" + notificacioTelematicaHabilitada + ", tramitExpedientIdentificador=" + tramitExpedientIdentificador + ", tramitExpedientClau=" + tramitExpedientClau + ", bantelEntradaNum=" + bantelEntradaNum + ", entorn=" + entorn + ", tipus=" + tipus + ", estatTipus=" + estatTipus + ", estat=" + estat + ", errorDesc=" + errorDesc + ", errorFull=" + errorFull + ", errorsIntegracions=" + errorsIntegracions + ", iniciadorPersona=" + iniciadorPersona
				+ ", responsablePersona=" + responsablePersona + "]";
	}

	private static final long serialVersionUID = -9207907579002520198L;

}