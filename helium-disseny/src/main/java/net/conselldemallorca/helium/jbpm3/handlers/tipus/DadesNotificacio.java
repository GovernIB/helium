/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una notificació per al seu enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class DadesNotificacio {
	
	
	private Date caducitat;
	private String concepte;
	private String serveiTipus;
	private String grupCodi;
	private String descripcio;
	private Long documentId;
	private String documentArxiuNom;
	private byte[] documentArxiuContingut;
	private String emisorDir3Codi;
	private Date enviamentDataProgramada;
	public enum EnviamentTipus {
		NOTIFICACIO,
		COMUNICACIO
	}
	private EnviamentTipus enviamentTipus;
	private List<DadesEnviament> enviaments;
	private String procedimentCodi;
	private Integer retard;
	public enum Idioma {
		ES,
		CA
	}
	private Idioma idioma;
	

	public String getEmisorDir3Codi() {
		return emisorDir3Codi;
	}
	public void setEmisorDir3Codi(String emisorDir3Codi) {
		this.emisorDir3Codi = emisorDir3Codi;
	}
	public EnviamentTipus getEnviamentTipus() {
		return enviamentTipus;
	}
	public void setEnviamentTipus(EnviamentTipus enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}
	public String getConcepte() {
		return concepte;
	}
	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
	}
	public Integer getRetard() {
		return retard;
	}
	public void setRetard(Integer retard) {
		this.retard = retard;
	}
	public Date getCaducitat() {
		return caducitat;
	}
	public void setCaducitat(Date caducitat) {
		this.caducitat = caducitat;
	}
	public String getDocumentArxiuNom() {
		return documentArxiuNom;
	}
	public void setDocumentArxiuNom(String documentArxiuNom) {
		this.documentArxiuNom = documentArxiuNom;
	}
	public byte[] getDocumentArxiuContingut() {
		return documentArxiuContingut;
	}
	public void setDocumentArxiuContingut(byte[] documentArxiuContingut) {
		this.documentArxiuContingut = documentArxiuContingut;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public List<DadesEnviament> getEnviaments() {
		return enviaments;
	}
	public void setEnviaments(List<DadesEnviament> enviaments) {
		this.enviaments = enviaments;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public Long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}
	public String getServeiTipus() {
		return serveiTipus;
	}
	public void setServeiTipus(String serveiTipus) {
		this.serveiTipus = serveiTipus;
	}
	public String getGrupCodi() {
		return grupCodi;
	}
	public void setGrupCodi(String grupCodi) {
		this.grupCodi = grupCodi;
	}
	public Idioma getIdioma() {
		return idioma;
	}
	public void setIdioma(Idioma idioma) {
		this.idioma = idioma;
	}

}
