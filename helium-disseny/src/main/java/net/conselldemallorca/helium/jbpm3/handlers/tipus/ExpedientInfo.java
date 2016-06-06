/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.Date;

/**
 * Classe per retornar la informació d'un expedient als handlers.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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

	private String estatCodi;
	private String estatNom;
	private String expedientTipusCodi;
	private String expedientTipusNom;
	private String entornCodi;
	private String entornNom;

	private long processInstanceId;
	private boolean ambRetroaccio;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public IniciadorTipus getIniciadorTipus() {
		return iniciadorTipus;
	}
	public void setIniciadorTipus(IniciadorTipus iniciadorTipus) {
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
	public String getEstatCodi() {
		return estatCodi;
	}
	public void setEstatCodi(String estatCodi) {
		this.estatCodi = estatCodi;
	}
	public String getEstatNom() {
		return estatNom;
	}
	public void setEstatNom(String estatNom) {
		this.estatNom = estatNom;
	}
	public String getExpedientTipusCodi() {
		return expedientTipusCodi;
	}
	public void setExpedientTipusCodi(String expedientTipusCodi) {
		this.expedientTipusCodi = expedientTipusCodi;
	}
	public String getExpedientTipusNom() {
		return expedientTipusNom;
	}
	public void setExpedientTipusNom(String expedientTipusNom) {
		this.expedientTipusNom = expedientTipusNom;
	}
	public String getEntornCodi() {
		return entornCodi;
	}
	public void setEntornCodi(String entornCodi) {
		this.entornCodi = entornCodi;
	}
	public String getEntornNom() {
		return entornNom;
	}
	public void setEntornNom(String entornNom) {
		this.entornNom = entornNom;
	}

	public long getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getComentariAnulat() {
		return comentariAnulat;
	}
	public void setComentariAnulat(String comentariAnulat) {
		this.comentariAnulat = comentariAnulat;
	}
	public boolean isAmbRetroaccio() {
		return ambRetroaccio;
	}
	public void setAmbRetroaccio(boolean ambRetroaccio) {
		this.ambRetroaccio = ambRetroaccio;
	}
	
	@Override
	public String toString() {
		return "{ " +
				"id: " + getId() + ", " +
				"titol" + getTitol() + ", " +
				"numero" + getNumero() + ", " +
				"numeroDefault" + getNumeroDefault() +
				"}";
	}
}
