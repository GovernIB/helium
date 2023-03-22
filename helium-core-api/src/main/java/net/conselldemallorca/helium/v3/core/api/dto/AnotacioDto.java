/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * DTO amb informació d'una anotació pel detall.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AnotacioDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4668602421975982686L;

	private Long id;
	
	// Dades del processament de la petició d'anotació
	
	/** Estat de l'anotació a Helium. */
	private AnotacioEstatEnumDto estat;
	private Date dataRecepcio;
	private Date dataProcessament;
	private String errorProcessament;
	private int consultaIntents;
	private String consultaError;
	private Date consultaData;
	
	private String distribucioId;
	private String distribucioClauAcces;
	private String rebuigMotiu;
	
	private String distibucioErrorNotificacio = "Error notificant estat al notib";
	private Date distribucioErrorData = new Date();
	
	/** Expedient tipus associat per codi de procediment */
	private ExpedientTipusDto expedientTipus;
	/** Expedient amb el qual s'associa o inclou l'anotació. */
	private ExpedientDto expedient;

	// Dades pròpies de l'anotació

	private List<AnotacioInteressatDto> interessats = new ArrayList<AnotacioInteressatDto>();
	private List<AnotacioAnnexDto> annexos = new ArrayList<AnotacioAnnexDto>();

	// Per mostrar errors d'annexos al llistat
	private boolean errorAnnexos;
	private boolean annexosInvalids;
	private boolean annexosEsborranys;
	//Per mostrar si s'està processant per threads de la Tasca programada
	private boolean processant;


	private String aplicacioCodi;
	private String aplicacioVersio;
	private String assumpteCodiCodi;
	private String assumpteCodiDescripcio;
	private String assumpteTipusCodi;
	private String assumpteTipusDescripcio;
	private Date data;
	private String docFisicaCodi;
	private String docFisicaDescripcio;
	private String entitatCodi;
	private String entitatDescripcio;
	private String expedientNumero;
	private String exposa;
	private String extracte;
	private String procedimentCodi;
	private String identificador;
	private String idiomaCodi;
	private String idiomaDescripcio;
	private String llibreCodi;
	private String llibreDescripcio;
	private String observacions;
	private String oficinaCodi;
	private String oficinaDescripcio;
	private Date origenData;
	private String origenRegistreNumero;
	private String refExterna;
	private String solicita;
	private String transportNumero;
	private String transportTipusCodi;
	private String transportTipusDescripcio;
	private String usuariCodi;
	private String usuariNom;
	private String destiCodi;
	private String destiDescripcio;

	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public AnotacioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(AnotacioEstatEnumDto estat) {
		this.estat = estat;
	}
	public Date getDataRecepcio() {
		return dataRecepcio;
	}
	public void setDataRecepcio(Date dataRecepcio) {
		this.dataRecepcio = dataRecepcio;
	}
	public Date getDataProcessament() {
		return dataProcessament;
	}
	public void setDataProcessament(Date dataProcessament) {
		this.dataProcessament = dataProcessament;
	}
	public String getErrorProcessament() {
		return errorProcessament;
	}
	public void setErrorProcessament(String errorProcessament) {
		this.errorProcessament = errorProcessament;
	}
	public String getDistribucioId() {
		return distribucioId;
	}
	public void setDistribucioId(String distribucioId) {
		this.distribucioId = distribucioId;
	}
	public String getDistribucioClauAcces() {
		return distribucioClauAcces;
	}
	public void setDistribucioClauAcces(String distribucioClauAcces) {
		this.distribucioClauAcces = distribucioClauAcces;
	}
	public String getRebuigMotiu() {
		return rebuigMotiu;
	}
	public void setRebuigMotiu(String rebuigMotiu) {
		this.rebuigMotiu = rebuigMotiu;
	}
	public String getDistibucioErrorNotificacio() {
		return distibucioErrorNotificacio;
	}
	public void setDistibucioErrorNotificacio(String distibucioErrorNotificacio) {
		this.distibucioErrorNotificacio = distibucioErrorNotificacio;
	}
	public Date getDistribucioErrorData() {
		return distribucioErrorData;
	}
	public void setDistribucioErrorData(Date distribucioErrorData) {
		this.distribucioErrorData = distribucioErrorData;
	}
	public ExpedientTipusDto getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipusDto expedientTipus) {
		this.expedientTipus = expedientTipus;
	}
	public ExpedientDto getExpedient() {
		return expedient;
	}
	public void setExpedient(ExpedientDto expedient) {
		this.expedient = expedient;
	}
	public List<AnotacioInteressatDto> getInteressats() {
		return interessats;
	}
	public void setInteressats(List<AnotacioInteressatDto> interessats) {
		this.interessats = interessats;
	}
	public List<AnotacioAnnexDto> getAnnexos() {
		return annexos;
	}
	public void setAnnexos(List<AnotacioAnnexDto> annexos) {
		this.annexos = annexos;
	}
	public boolean isErrorAnnexos() {
		return errorAnnexos;
	}
	public void setErrorAnnexos(boolean errorAnnexos) {
		this.errorAnnexos = errorAnnexos;
	}
	public boolean isAnnexosInvalids() {
		return annexosInvalids;
	}
	public void setAnnexosInvalids(boolean annexosInvalids) {
		this.annexosInvalids = annexosInvalids;
	}
	public boolean isAnnexosEsborranys() {
		return annexosEsborranys;
	}
	public void setAnnexosEsborranys(boolean annexosEsborranys) {
		this.annexosEsborranys = annexosEsborranys;
	}
	public String getAplicacioCodi() {
		return aplicacioCodi;
	}
	public void setAplicacioCodi(String aplicacioCodi) {
		this.aplicacioCodi = aplicacioCodi;
	}
	public String getAplicacioVersio() {
		return aplicacioVersio;
	}
	public void setAplicacioVersio(String aplicacioVersio) {
		this.aplicacioVersio = aplicacioVersio;
	}
	public String getAssumpteCodiCodi() {
		return assumpteCodiCodi;
	}
	public void setAssumpteCodiCodi(String assumpteCodiCodi) {
		this.assumpteCodiCodi = assumpteCodiCodi;
	}
	public String getAssumpteCodiDescripcio() {
		return assumpteCodiDescripcio;
	}
	public void setAssumpteCodiDescripcio(String assumpteCodiDescripcio) {
		this.assumpteCodiDescripcio = assumpteCodiDescripcio;
	}
	public String getAssumpteTipusCodi() {
		return assumpteTipusCodi;
	}
	public void setAssumpteTipusCodi(String assumpteTipusCodi) {
		this.assumpteTipusCodi = assumpteTipusCodi;
	}
	public String getAssumpteTipusDescripcio() {
		return assumpteTipusDescripcio;
	}
	public void setAssumpteTipusDescripcio(String assumpteTipusDescripcio) {
		this.assumpteTipusDescripcio = assumpteTipusDescripcio;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getDocFisicaCodi() {
		return docFisicaCodi;
	}
	public void setDocFisicaCodi(String docFisicaCodi) {
		this.docFisicaCodi = docFisicaCodi;
	}
	public String getDocFisicaDescripcio() {
		return docFisicaDescripcio;
	}
	public void setDocFisicaDescripcio(String docFisicaDescripcio) {
		this.docFisicaDescripcio = docFisicaDescripcio;
	}
	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getEntitatDescripcio() {
		return entitatDescripcio;
	}
	public void setEntitatDescripcio(String entitatDescripcio) {
		this.entitatDescripcio = entitatDescripcio;
	}
	public String getExpedientNumero() {
		return expedientNumero;
	}
	public void setExpedientNumero(String expedientNumero) {
		this.expedientNumero = expedientNumero;
	}
	public String getExposa() {
		return exposa;
	}
	public void setExposa(String exposa) {
		this.exposa = exposa;
	}
	public String getExtracte() {
		return extracte;
	}
	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getIdiomaCodi() {
		return idiomaCodi;
	}
	public void setIdiomaCodi(String idiomaCodi) {
		this.idiomaCodi = idiomaCodi;
	}
	public String getIdiomaDescripcio() {
		return idiomaDescripcio;
	}
	public void setIdiomaDescripcio(String idiomaDescripcio) {
		this.idiomaDescripcio = idiomaDescripcio;
	}
	public String getLlibreCodi() {
		return llibreCodi;
	}
	public void setLlibreCodi(String llibreCodi) {
		this.llibreCodi = llibreCodi;
	}
	public String getLlibreDescripcio() {
		return llibreDescripcio;
	}
	public void setLlibreDescripcio(String llibreDescripcio) {
		this.llibreDescripcio = llibreDescripcio;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public String getOficinaCodi() {
		return oficinaCodi;
	}
	public void setOficinaCodi(String oficinaCodi) {
		this.oficinaCodi = oficinaCodi;
	}
	public String getOficinaDescripcio() {
		return oficinaDescripcio;
	}
	public void setOficinaDescripcio(String oficinaDescripcio) {
		this.oficinaDescripcio = oficinaDescripcio;
	}
	public Date getOrigenData() {
		return origenData;
	}
	public void setOrigenData(Date origenData) {
		this.origenData = origenData;
	}
	public String getOrigenRegistreNumero() {
		return origenRegistreNumero;
	}
	public void setOrigenRegistreNumero(String origenRegistreNumero) {
		this.origenRegistreNumero = origenRegistreNumero;
	}
	public String getRefExterna() {
		return refExterna;
	}
	public void setRefExterna(String refExterna) {
		this.refExterna = refExterna;
	}
	public String getSolicita() {
		return solicita;
	}
	public void setSolicita(String solicita) {
		this.solicita = solicita;
	}
	public String getTransportNumero() {
		return transportNumero;
	}
	public void setTransportNumero(String transportNumero) {
		this.transportNumero = transportNumero;
	}
	public String getTransportTipusCodi() {
		return transportTipusCodi;
	}
	public void setTransportTipusCodi(String transportTipusCodi) {
		this.transportTipusCodi = transportTipusCodi;
	}
	public String getTransportTipusDescripcio() {
		return transportTipusDescripcio;
	}
	public void setTransportTipusDescripcio(String transportTipusDescripcio) {
		this.transportTipusDescripcio = transportTipusDescripcio;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public String getUsuariNom() {
		return usuariNom;
	}
	public void setUsuariNom(String usuariNom) {
		this.usuariNom = usuariNom;
	}
	public String getDestiCodi() {
		return destiCodi;
	}
	public void setDestiCodi(String destiCodi) {
		this.destiCodi = destiCodi;
	}
	public String getDestiDescripcio() {
		return destiDescripcio;
	}
	public void setDestiDescripcio(String destiDescripcio) {
		this.destiDescripcio = destiDescripcio;
	}
	public int getConsultaIntents() {
		return consultaIntents;
	}
	public void setConsultaIntents(int consultaIntents) {
		this.consultaIntents = consultaIntents;
	}
	public String getConsultaError() {
		return consultaError;
	}
	public void setConsultaError(String consultaError) {
		this.consultaError = consultaError;
	}
	public Date getConsultaData() {
		return consultaData;
	}
	public void setConsultaData(Date consultaData) {
		this.consultaData = consultaData;
	}
	public boolean isProcessant() {
		return processant;
	}
	public void setProcessant(boolean processant) {
		this.processant = processant;
	}
	
}
