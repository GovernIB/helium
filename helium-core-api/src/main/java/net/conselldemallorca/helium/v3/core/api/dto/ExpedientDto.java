/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * DTO amb informació d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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

	public ExpedientDto(IniciadorTipusDto iniciadorTipus, String iniciadorCodi, ExpedientTipusDto tipus, EntornDto entorn, String processInstanceId) {
		this.iniciadorTipus = iniciadorTipus;
		this.iniciadorCodi = iniciadorCodi;
		this.tipus = tipus;
		this.entorn = entorn;
		this.processInstanceId = processInstanceId;
	}

	public ExpedientDto() {}

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

	private boolean errorsIntegracions;

	private PersonaDto iniciadorPersona;
	private PersonaDto responsablePersona;
	
	private String nomEstat;
	
	private PermisDto permisos;
	private boolean usuariActualRead;


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getNumeroDefault() {
		return numeroDefault;
	}
	public void setNumeroDefault(String numeroDefault) {
		this.numeroDefault = numeroDefault;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public String getComentari() {
		return comentari;
	}
	public void setComentari(String comentari) {
		this.comentari = comentari;
	}
	public String getInfoAturat() {
		return infoAturat;
	}
	public void setInfoAturat(String infoAturat) {
		this.infoAturat = infoAturat;
	}
	public IniciadorTipusDto getIniciadorTipus() {
		return iniciadorTipus;
	}
	public void setIniciadorTipus(IniciadorTipusDto iniciadorTipus) {
		this.iniciadorTipus = iniciadorTipus;
	}
	public String getIniciadorCodi() {
		return iniciadorCodi;
	}
	public void setIniciadorCodi(String iniciadorCodi) {
		this.iniciadorCodi = iniciadorCodi;
	}
	public String getResponsableCodi() {
		return responsableCodi;
	}
	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}
	public boolean isAnulat() {
		return anulat;
	}
	public void setAnulat(boolean anulat) {
		this.anulat = anulat;
	}
	public String getGrupCodi() {
		return grupCodi;
	}
	public void setGrupCodi(String grupCodi) {
		this.grupCodi = grupCodi;
	}
	public String getComentariAnulat() {
		return comentariAnulat;
	}
	public void setComentariAnulat(String comentariAnulat) {
		this.comentariAnulat = comentariAnulat;
	}
	public Double getGeoPosX() {
		return geoPosX;
	}
	public void setGeoPosX(Double geoPosX) {
		this.geoPosX = geoPosX;
	}
	public Double getGeoPosY() {
		return geoPosY;
	}
	public void setGeoPosY(Double geoPosY) {
		this.geoPosY = geoPosY;
	}
	public String getGeoReferencia() {
		return geoReferencia;
	}
	public void setGeoReferencia(String geoReferencia) {
		this.geoReferencia = geoReferencia;
	}
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}
	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}
	public Long getUnitatAdministrativa() {
		return unitatAdministrativa;
	}
	public void setUnitatAdministrativa(Long unitatAdministrativa) {
		this.unitatAdministrativa = unitatAdministrativa;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	public boolean isAutenticat() {
		return autenticat;
	}
	public void setAutenticat(boolean autenticat) {
		this.autenticat = autenticat;
	}
	public String getTramitadorNif() {
		return tramitadorNif;
	}
	public void setTramitadorNif(String tramitadorNif) {
		this.tramitadorNif = tramitadorNif;
	}
	public String getTramitadorNom() {
		return tramitadorNom;
	}
	public void setTramitadorNom(String tramitadorNom) {
		this.tramitadorNom = tramitadorNom;
	}
	public String getInteressatNif() {
		return interessatNif;
	}
	public void setInteressatNif(String interessatNif) {
		this.interessatNif = interessatNif;
	}
	public String getInteressatNom() {
		return interessatNom;
	}
	public void setInteressatNom(String interessatNom) {
		this.interessatNom = interessatNom;
	}
	public String getRepresentantNif() {
		return representantNif;
	}
	public void setRepresentantNif(String representantNif) {
		this.representantNif = representantNif;
	}
	public String getRepresentantNom() {
		return representantNom;
	}
	public void setRepresentantNom(String representantNom) {
		this.representantNom = representantNom;
	}
	public boolean isAvisosHabilitats() {
		return avisosHabilitats;
	}
	public void setAvisosHabilitats(boolean avisosHabilitats) {
		this.avisosHabilitats = avisosHabilitats;
	}
	public String getAvisosEmail() {
		return avisosEmail;
	}
	public void setAvisosEmail(String avisosEmail) {
		this.avisosEmail = avisosEmail;
	}
	public String getAvisosMobil() {
		return avisosMobil;
	}
	public void setAvisosMobil(String avisosMobil) {
		this.avisosMobil = avisosMobil;
	}
	public boolean isNotificacioTelematicaHabilitada() {
		return notificacioTelematicaHabilitada;
	}
	public void setNotificacioTelematicaHabilitada(
			boolean notificacioTelematicaHabilitada) {
		this.notificacioTelematicaHabilitada = notificacioTelematicaHabilitada;
	}
	public String getTramitExpedientIdentificador() {
		return tramitExpedientIdentificador;
	}
	public void setTramitExpedientIdentificador(String tramitExpedientIdentificador) {
		this.tramitExpedientIdentificador = tramitExpedientIdentificador;
	}
	public String getTramitExpedientClau() {
		return tramitExpedientClau;
	}
	public void setTramitExpedientClau(String tramitExpedientClau) {
		this.tramitExpedientClau = tramitExpedientClau;
	}
	public String getBantelEntradaNum() {
		return bantelEntradaNum;
	}
	public void setBantelEntradaNum(String bantelEntradaNum) {
		this.bantelEntradaNum = bantelEntradaNum;
	}
	public EntornDto getEntorn() {
		return entorn;
	}
	public void setEntorn(EntornDto entorn) {
		this.entorn = entorn;
	}
	public ExpedientTipusDto getTipus() {
		return tipus;
	}
	public void setTipus(ExpedientTipusDto tipus) {
		this.tipus = tipus;
	}
	public EstatTipusDto getEstatTipus() {
		return estatTipus;
	}
	public void setEstatTipus(EstatTipusDto estatTipus) {
		this.estatTipus = estatTipus;
	}
	public EstatDto getEstat() {
		return estat;
	}
	public void setEstat(EstatDto estat) {
		this.estat = estat;
	}
	public String getErrorDesc() {
		return errorDesc;
	}
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
	public String getErrorFull() {
		return errorFull;
	}
	public void setErrorFull(String errorFull) {
		this.errorFull = errorFull;
	}
	public boolean isErrorsIntegracions() {
		return errorsIntegracions;
	}
	public void setErrorsIntegracions(boolean errorsIntegracions) {
		this.errorsIntegracions = errorsIntegracions;
	}
	public PersonaDto getIniciadorPersona() {
		return iniciadorPersona;
	}
	public void setIniciadorPersona(PersonaDto iniciadorPersona) {
		this.iniciadorPersona = iniciadorPersona;
	}
	public PersonaDto getResponsablePersona() {
		return responsablePersona;
	}
	public void setResponsablePersona(PersonaDto responsablePersona) {
		this.responsablePersona = responsablePersona;
	}
	public PermisDto getPermisos() {
		return permisos;
	}
	public void setPermisos(PermisDto permisos) {
		this.permisos = permisos;
	}
	public boolean isUsuariActualRead() {
		return usuariActualRead;
	}
	public void setUsuariActualRead(boolean usuariActualRead) {
		this.usuariActualRead = usuariActualRead;
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
		if (identificador == null)
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
		return null;
	}

	@Override
	public String toString() {
		return "ExpedientDto [id=" + id + ", processInstanceId=" + processInstanceId + ", titol=" + titol + ", numero=" + numero + ", numeroDefault=" + numeroDefault + ", dataInici=" + dataInici + ", dataFi=" + dataFi + ", comentari=" + comentari + ", infoAturat=" + infoAturat + ", iniciadorTipus=" + iniciadorTipus + ", iniciadorCodi=" + iniciadorCodi + ", responsableCodi=" + responsableCodi + ", anulat=" + anulat + ", grupCodi=" + grupCodi + ", comentariAnulat=" + comentariAnulat + ", geoPosX=" + geoPosX + ", geoPosY=" + geoPosY + ", geoReferencia=" + geoReferencia + ", registreNumero=" + registreNumero + ", registreData=" + registreData + ", unitatAdministrativa=" + unitatAdministrativa + ", idioma=" + idioma + ", autenticat=" + autenticat + ", tramitadorNif=" + tramitadorNif
				+ ", tramitadorNom=" + tramitadorNom + ", interessatNif=" + interessatNif + ", interessatNom=" + interessatNom + ", representantNif=" + representantNif + ", representantNom=" + representantNom + ", avisosHabilitats=" + avisosHabilitats + ", avisosEmail=" + avisosEmail + ", avisosMobil=" + avisosMobil + ", notificacioTelematicaHabilitada=" + notificacioTelematicaHabilitada + ", tramitExpedientIdentificador=" + tramitExpedientIdentificador + ", tramitExpedientClau=" + tramitExpedientClau + ", bantelEntradaNum=" + bantelEntradaNum + ", entorn=" + entorn + ", tipus=" + tipus + ", estatTipus=" + estatTipus + ", estat=" + estat + ", errorDesc=" + errorDesc + ", errorFull=" + errorFull + ", errorsIntegracions=" + errorsIntegracions + ", iniciadorPersona=" + iniciadorPersona
				+ ", responsablePersona=" + responsablePersona + "]";
	}

	public String getNomEstat() {
		return nomEstat;
	}

	public void setNomEstat(String nomEstat) {
		this.nomEstat = nomEstat;
	}

	private static final long serialVersionUID = -9207907579002520198L;

}
