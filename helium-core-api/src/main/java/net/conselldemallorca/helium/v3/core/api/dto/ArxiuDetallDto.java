/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Informació d'un contingut emmagatzemada a l'arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuDetallDto extends ArxiuContingutDto {

	/** Descripcio en cas dels documents */
	private String eniVersio;
	private String eniIdentificador;
	private NtiOrigenEnumDto eniOrigen;
	private Date eniDataObertura;
	private String eniClassificacio;
	private NtiExpedienteEstadoEnumDto eniEstat;
	private List<String> eniOrgans;
	private List<String> eniInteressats;
	private Date eniDataCaptura;
	private NtiEstadoElaboracionEnumDto eniEstatElaboracio;
	private NtiTipoDocumentalEnumDto eniTipusDocumental;
	private String eniFormat;
	private String eniExtensio;
	private String eniDocumentOrigenId;
	private String serieDocumental;
	private String eniCsv;
	private String eniCsvDef;
	private List<ArxiuDetallDto> versionsDocument;
	private boolean expedientTancat;

	private Map<String, Object> metadadesAddicionals;

	private String contingutTipusMime;
	private String contingutArxiuNom;
	
	private List<ArxiuFirmaDto> firmes;
	private List<ArxiuContingutDto> fills;

	private ArxiuEstat arxiuEstat;

	public String getEniVersio() {
		return eniVersio;
	}
	public void setEniVersio(String eniVersio) {
		this.eniVersio = eniVersio;
	}
	public String getEniIdentificador() {
		return eniIdentificador;
	}
	public void setEniIdentificador(String eniIdentificador) {
		this.eniIdentificador = eniIdentificador;
	}
	public NtiOrigenEnumDto getEniOrigen() {
		return eniOrigen;
	}
	public void setEniOrigen(NtiOrigenEnumDto eniOrigen) {
		this.eniOrigen = eniOrigen;
	}
	public Date getEniDataObertura() {
		return eniDataObertura;
	}
	public void setEniDataObertura(Date eniDataObertura) {
		this.eniDataObertura = eniDataObertura;
	}
	public String getEniClassificacio() {
		return eniClassificacio;
	}
	public void setEniClassificacio(String eniClassificacio) {
		this.eniClassificacio = eniClassificacio;
	}
	public NtiExpedienteEstadoEnumDto getEniEstat() {
		return eniEstat;
	}
	public void setEniEstat(NtiExpedienteEstadoEnumDto eniEstat) {
		this.eniEstat = eniEstat;
	}
	public List<String> getEniOrgans() {
		return eniOrgans;
	}
	public void setEniOrgans(List<String> eniOrgans) {
		this.eniOrgans = eniOrgans;
	}
	public List<String> getEniInteressats() {
		return eniInteressats;
	}
	public void setEniInteressats(List<String> eniInteressats) {
		this.eniInteressats = eniInteressats;
	}
	public Date getEniDataCaptura() {
		return eniDataCaptura;
	}
	public void setEniDataCaptura(Date eniDataCaptura) {
		this.eniDataCaptura = eniDataCaptura;
	}
	public NtiEstadoElaboracionEnumDto getEniEstatElaboracio() {
		return eniEstatElaboracio;
	}
	public void setEniEstatElaboracio(NtiEstadoElaboracionEnumDto eniEstatElaboracio) {
		this.eniEstatElaboracio = eniEstatElaboracio;
	}
	public NtiTipoDocumentalEnumDto getEniTipusDocumental() {
		return eniTipusDocumental;
	}
	public void setEniTipusDocumental(NtiTipoDocumentalEnumDto eniTipusDocumental) {
		this.eniTipusDocumental = eniTipusDocumental;
	}
	public String getEniFormat() {
		return eniFormat;
	}
	public void setEniFormat(String eniFormat) {
		this.eniFormat = eniFormat;
	}
	public String getEniExtensio() {
		return eniExtensio;
	}
	public void setEniExtensio(String eniExtensio) {
		this.eniExtensio = eniExtensio;
	}
	public String getEniDocumentOrigenId() {
		return eniDocumentOrigenId;
	}
	public void setEniDocumentOrigenId(String eniDocumentOrigenId) {
		this.eniDocumentOrigenId = eniDocumentOrigenId;
	}
	public String getSerieDocumental() {
		return serieDocumental;
	}
	public void setSerieDocumental(String serieDocumental) {
		this.serieDocumental = serieDocumental;
	}
	public String getEniCsv() {
		return eniCsv;
	}
	public void setEniCsv(String eniCsv) {
		this.eniCsv = eniCsv;
	}
	public String getEniCsvDef() {
		return eniCsvDef;
	}
	public void setEniCsvDef(String eniCsvDef) {
		this.eniCsvDef = eniCsvDef;
	}
	public Map<String, Object> getMetadadesAddicionals() {
		return metadadesAddicionals;
	}
	public void setMetadadesAddicionals(Map<String, Object> metadadesAddicionals) {
		this.metadadesAddicionals = metadadesAddicionals;
	}
	public String getContingutTipusMime() {
		return contingutTipusMime;
	}
	public void setContingutTipusMime(String contingutTipusMime) {
		this.contingutTipusMime = contingutTipusMime;
	}
	public String getContingutArxiuNom() {
		return contingutArxiuNom;
	}
	public void setContingutArxiuNom(String contingutArxiuNom) {
		this.contingutArxiuNom = contingutArxiuNom;
	}
	public List<ArxiuFirmaDto> getFirmes() {
		return firmes;
	}
	public void setFirmes(List<ArxiuFirmaDto> firmes) {
		this.firmes = firmes;
	}
	public List<ArxiuContingutDto> getFills() {
		return fills;
	}
	public void setFills(List<ArxiuContingutDto> fills) {
		this.fills = fills;
	}

	public ArxiuEstat getArxiuEstat() {
		return arxiuEstat;
	}
	public void setArxiuEstat(ArxiuEstat arxiuEstat) {
		this.arxiuEstat = arxiuEstat;
	}

	public List<ArxiuDetallDto> getVersionsDocument() {
		return versionsDocument;
	}
	public void setVersionsDocument(List<ArxiuDetallDto> versionsDocument) {
		this.versionsDocument = versionsDocument;
	}

	public boolean isExpedientTancat() {
		return expedientTancat;
	}
	public void setExpedientTancat(boolean expedientTancat) {
		this.expedientTancat = expedientTancat;
	}




	private static final long serialVersionUID = -2124829280908976623L;

}
