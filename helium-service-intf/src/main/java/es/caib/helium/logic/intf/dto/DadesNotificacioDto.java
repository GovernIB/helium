/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;


/**
 * Informació d'una notificació per al seu enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class DadesNotificacioDto {
	
	private Long id;
	private Date caducitat;
	private String concepte;
	private String descripcio;
	private String documentNom;
	private String documentArxiuNom;
	private byte[] documentArxiuContingut;
	private String documentArxiuUuid;
	private String documentArxiuCsv;
	private String emisorDir3Codi;
	private Date enviamentDataProgramada;
	private EnviamentTipusEnumDto enviamentTipus;
	private ServeiTipusEnumDto serveiTipusEnum;
	private List<DadesEnviamentDto> enviaments;
	private String procedimentCodi;
	private Integer retard;
	private String grupCodi;
	private Long documentId;
	private NotificacioEstatEnumDto estat;
	private Date enviatData;
	private Boolean error;
	private String errorDescripcio;
	private Long justificantId;
	private String justificantArxiuNom;
	private Long expedientId;
	private boolean entregaPostalActiva;
	private String enviamentIdentificador;
	private String enviamentReferencia;
	private IdiomaEnumDto idioma;


	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getJustificantId() {
		return justificantId;
	}
	public void setJustificantId(Long justificantId) {
		this.justificantId = justificantId;
	}
	public String getJustificantArxiuNom() {
		return justificantArxiuNom;
	}
	public void setJustificantArxiuNom(String justificantArxiuNom) {
		this.justificantArxiuNom = justificantArxiuNom;
	}
	public Long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}
	public NotificacioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}
	public Date getEnviatData() {
		return enviatData;
	}
	public void setEnviatData(Date enviatData) {
		this.enviatData = enviatData;
	}
	public Boolean getError() {
		return error != null ? error.booleanValue() : false;
	}
	public void setError(Boolean error) {
		this.error = error;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getEmisorDir3Codi() {
		return emisorDir3Codi;
	}
	public void setEmisorDir3Codi(String emisorDir3Codi) {
		this.emisorDir3Codi = emisorDir3Codi;
	}
	public ServeiTipusEnumDto getServeiTipusEnum() {
		return serveiTipusEnum;
	}
	public void setServeiTipusEnum(ServeiTipusEnumDto serveiTipusEnum) {
		this.serveiTipusEnum = serveiTipusEnum;
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
	public String getDocumentNom() {
		return documentNom;
	}
	public void setDocumentNom(String documentNom) {
		this.documentNom = documentNom;
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
	public List<DadesEnviamentDto> getEnviaments() {
		return enviaments;
	}
	public void setEnviaments(List<DadesEnviamentDto> enviaments) {
		this.enviaments = enviaments;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
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
	public IdiomaEnumDto getIdioma() {
		return idioma;
	}
	public void setIdioma(IdiomaEnumDto idioma) {
		this.idioma = idioma;
	}
	public String getGrupCodi() {
		return grupCodi;
	}
	public void setGrupCodi(String grupCodi) {
		this.grupCodi = grupCodi;
	}
	public boolean isEntregaPostalActiva() {
		return entregaPostalActiva;
	}
	public void setEntregaPostalActiva(boolean entregaPostalActiva) {
		this.entregaPostalActiva = entregaPostalActiva;
	}
	public String getEnviamentIdentificador() {
		return enviamentIdentificador;
	}
	public void setEnviamentIdentificador(String enviamentIdentificador) {
		this.enviamentIdentificador = enviamentIdentificador;
	}
	public String getEnviamentReferencia() {
		return enviamentReferencia;
	}
	public void setEnviamentReferencia(String enviamentReferencia) {
		this.enviamentReferencia = enviamentReferencia;
	}


}
