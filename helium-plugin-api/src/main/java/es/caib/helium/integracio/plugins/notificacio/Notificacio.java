/**
 * 
 */
package es.caib.helium.integracio.plugins.notificacio;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;

/**
 * Informació d'una notificació per al seu enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class Notificacio {

	private Date caducitat;
	private String concepte;
	private String descripcio;
	private String documentArxiuNom;
	private byte[] documentArxiuContingut;
	private String documentArxiuUuid;
	private String documentArxiuCsv;
	private String emisorDir3Codi;
	private Date enviamentDataProgramada;
	private EnviamentTipus enviamentTipus;
	private List<Enviament> enviaments;
	private String grupCodi;
	private String procedimentCodi;
	private Integer retard;
	private String usuariCodi;
	private String numExpedient;
	private IdiomaEnumDto idioma;
	

	public String getGrupCodi() {
		return grupCodi;
	}
	public void setGrupCodi(String grupCodi) {
		this.grupCodi = grupCodi;
	}
	public String getDocumentArxiuUuid() {
		return documentArxiuUuid;
	}
	public void setDocumentArxiuUuid(String documentArxiuUuid) {
		this.documentArxiuUuid = documentArxiuUuid;
	}
	public String getDocumentArxiuCsv() {
		return documentArxiuCsv;
	}
	public void setDocumentArxiuCsv(String documentArxiuCsv) {
		this.documentArxiuCsv = documentArxiuCsv;
	}
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
	public List<Enviament> getEnviaments() {
		return enviaments;
	}
	public void setEnviaments(List<Enviament> enviaments) {
		this.enviaments = enviaments;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public String getNumExpedient() {
		return numExpedient;
	}
	public void setNumExpedient(String numExpedient) {
		this.numExpedient = numExpedient;
	}

	public IdiomaEnumDto getIdioma() {
		return idioma;
	}
	public void setIdioma(IdiomaEnumDto idioma) {
		this.idioma = idioma;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
