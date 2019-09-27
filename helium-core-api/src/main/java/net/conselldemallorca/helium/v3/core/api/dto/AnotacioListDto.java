/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.Date;


/**
 * DTO amb informació d'una enumeració pel llistat d'anotacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AnotacioListDto implements Serializable {

	private Long id;
	
	/** Expedient tipus associat per codi de procediment */
	private ExpedientTipusDto expedientTipus;
	/** Expedient amb el qual s'associa o inclou l'anotació. */
	private ExpedientDto expedient;

	// Dades del processament de la petició d'anotació
	
	/** Estat de l'anotació a Helium. */
	private AnotacioEstatEnumDto estat;
	private Date dataRecepcio;
	private Date dataProcessament;
	
	// Dades pròpies de l'anotació
	
	private String aplicacioCodi;
	private String assumpteCodiCodi;
	private Date data;
	private String entitatCodi;
	private String entitatDescripcio;
	private String expedientNumero;
	private String extracte;
	private String procedimentCodi;
	private String identificador;
	private String llibreCodi;
	private String llibreDescripcio;
	private String oficinaCodi;
	private String oficinaDescripcio;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getAplicacioCodi() {
		return aplicacioCodi;
	}
	public void setAplicacioCodi(String aplicacioCodi) {
		this.aplicacioCodi = aplicacioCodi;
	}
	public String getAssumpteCodiCodi() {
		return assumpteCodiCodi;
	}
	public void setAssumpteCodiCodi(String assumpteCodiCodi) {
		this.assumpteCodiCodi = assumpteCodiCodi;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
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
	
	private static final long serialVersionUID = 2160387490004131252L;	
}
