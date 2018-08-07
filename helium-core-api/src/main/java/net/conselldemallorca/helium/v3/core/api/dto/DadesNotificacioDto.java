/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una notificació per al seu enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class DadesNotificacioDto {

	private String emisorDir3Codi;
	private EnviamentTipusEnumDto enviamentTipus;
	private String concepte;
	private String descripcio;
	private Date enviamentDataProgramada;
	private Integer retard;
	private Date caducitat;
	private String documentArxiuNom;
	private byte[] documentArxiuContingut;
	private String procedimentCodi;
	private String pagadorPostalDir3Codi;
	private String pagadorPostalContracteNum;
	private Date pagadorPostalContracteDataVigencia;
	private String pagadorPostalFacturacioClientCodi;
	private String pagadorCieDir3Codi;
	private Date pagadorCieContracteDataVigencia;
	private List<DadesEnviamentDto> enviaments;
	private String seuProcedimentCodi; // IDENTIFICADOR_PROCEDIMENT_SISTRA (falta)
	private String seuExpedientSerieDocumental; // agafat exp RIPEA
	private String seuExpedientUnitatOrganitzativa; // UNITAT_ADMINISTRATIVA_SISTRA
	private String seuExpedientIdentificadorEni; // agafat exp RIPEA
	private String seuExpedientTitol; // agafat exp RIPEA
	private String seuRegistreOficina; // OFICINA
	private String seuRegistreLlibre; // LLIBRE
	private String seuRegistreOrgan; // ORGAN_CODI (falta)
	private String seuIdioma;
	private String seuAvisTitol;
	private String seuAvisText;
	private String seuAvisTextMobil;
	private String seuOficiTitol;
	private String seuOficiText;

	public String getEmisorDir3Codi() {
		return emisorDir3Codi;
	}
	public void setEmisorDir3Codi(String emisorDir3Codi) {
		this.emisorDir3Codi = emisorDir3Codi;
	}
	public EnviamentTipusEnumDto getEnviamentTipus() {
		return enviamentTipus;
	}
	public void setEnviamentTipus(EnviamentTipusEnumDto enviamentTipus) {
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
	public String getPagadorPostalDir3Codi() {
		return pagadorPostalDir3Codi;
	}
	public void setPagadorPostalDir3Codi(String pagadorPostalDir3Codi) {
		this.pagadorPostalDir3Codi = pagadorPostalDir3Codi;
	}
	public String getPagadorPostalContracteNum() {
		return pagadorPostalContracteNum;
	}
	public void setPagadorPostalContracteNum(String pagadorPostalContracteNum) {
		this.pagadorPostalContracteNum = pagadorPostalContracteNum;
	}
	public Date getPagadorPostalContracteDataVigencia() {
		return pagadorPostalContracteDataVigencia;
	}
	public void setPagadorPostalContracteDataVigencia(Date pagadorPostalContracteDataVigencia) {
		this.pagadorPostalContracteDataVigencia = pagadorPostalContracteDataVigencia;
	}
	public String getPagadorPostalFacturacioClientCodi() {
		return pagadorPostalFacturacioClientCodi;
	}
	public void setPagadorPostalFacturacioClientCodi(String pagadorPostalFacturacioClientCodi) {
		this.pagadorPostalFacturacioClientCodi = pagadorPostalFacturacioClientCodi;
	}
	public String getPagadorCieDir3Codi() {
		return pagadorCieDir3Codi;
	}
	public void setPagadorCieDir3Codi(String pagadorCieDir3Codi) {
		this.pagadorCieDir3Codi = pagadorCieDir3Codi;
	}
	public Date getPagadorCieContracteDataVigencia() {
		return pagadorCieContracteDataVigencia;
	}
	public void setPagadorCieContracteDataVigencia(Date pagadorCieContracteDataVigencia) {
		this.pagadorCieContracteDataVigencia = pagadorCieContracteDataVigencia;
	}
	public List<DadesEnviamentDto> getEnviaments() {
		return enviaments;
	}
	public void setEnviaments(List<DadesEnviamentDto> enviaments) {
		this.enviaments = enviaments;
	}
	public String getSeuProcedimentCodi() {
		return seuProcedimentCodi;
	}
	public void setSeuProcedimentCodi(String seuProcedimentCodi) {
		this.seuProcedimentCodi = seuProcedimentCodi;
	}
	public String getSeuExpedientSerieDocumental() {
		return seuExpedientSerieDocumental;
	}
	public void setSeuExpedientSerieDocumental(String seuExpedientSerieDocumental) {
		this.seuExpedientSerieDocumental = seuExpedientSerieDocumental;
	}
	public String getSeuExpedientUnitatOrganitzativa() {
		return seuExpedientUnitatOrganitzativa;
	}
	public void setSeuExpedientUnitatOrganitzativa(String seuExpedientUnitatOrganitzativa) {
		this.seuExpedientUnitatOrganitzativa = seuExpedientUnitatOrganitzativa;
	}
	public String getSeuExpedientIdentificadorEni() {
		return seuExpedientIdentificadorEni;
	}
	public void setSeuExpedientIdentificadorEni(String seuExpedientIdentificadorEni) {
		this.seuExpedientIdentificadorEni = seuExpedientIdentificadorEni;
	}
	public String getSeuExpedientTitol() {
		return seuExpedientTitol;
	}
	public void setSeuExpedientTitol(String seuExpedientTitol) {
		this.seuExpedientTitol = seuExpedientTitol;
	}
	public String getSeuRegistreOficina() {
		return seuRegistreOficina;
	}
	public void setSeuRegistreOficina(String seuRegistreOficina) {
		this.seuRegistreOficina = seuRegistreOficina;
	}
	public String getSeuRegistreLlibre() {
		return seuRegistreLlibre;
	}
	public void setSeuRegistreLlibre(String seuRegistreLlibre) {
		this.seuRegistreLlibre = seuRegistreLlibre;
	}
	public String getSeuRegistreOrgan() {
		return seuRegistreOrgan;
	}
	public void setSeuRegistreOrgan(String seuRegistreOrgan) {
		this.seuRegistreOrgan = seuRegistreOrgan;
	}
	public String getSeuIdioma() {
		return seuIdioma;
	}
	public void setSeuIdioma(String seuIdioma) {
		this.seuIdioma = seuIdioma;
	}
	public String getSeuAvisTitol() {
		return seuAvisTitol;
	}
	public void setSeuAvisTitol(String seuAvisTitol) {
		this.seuAvisTitol = seuAvisTitol;
	}
	public String getSeuAvisText() {
		return seuAvisText;
	}
	public void setSeuAvisText(String seuAvisText) {
		this.seuAvisText = seuAvisText;
	}
	public String getSeuAvisTextMobil() {
		return seuAvisTextMobil;
	}
	public void setSeuAvisTextMobil(String seuAvisTextMobil) {
		this.seuAvisTextMobil = seuAvisTextMobil;
	}
	public String getSeuOficiTitol() {
		return seuOficiTitol;
	}
	public void setSeuOficiTitol(String seuOficiTitol) {
		this.seuOficiTitol = seuOficiTitol;
	}
	public String getSeuOficiText() {
		return seuOficiText;
	}
	public void setSeuOficiText(String seuOficiText) {
		this.seuOficiText = seuOficiText;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
