/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import java.io.Serializable;
import java.util.Date;


/**
 * DTO amb informació d'un document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentStoreDto implements Serializable {
	
	private String ntiVersion;
	private String ntiIdentificador;
	private String ntiOrgan;
	private String ntiOrigen;
	private String ntiEstatElaboracio;
	private String ntiNomFormat;
	private String ntiTipusDocumental;
	
	private String ntiTipoFirma;
	private String ntiValorCsv;
	private String ntiDefGenCsv;
	
	private Date dataCreacio;
	
	private String ntiIdDocOrigen;
	
	
	public String getNtiVersion() {
		return ntiVersion;
	}
	public void setNtiVersion(String ntiVersion) {
		this.ntiVersion = ntiVersion;
	}
	
	public String getNtiIdentificador() {
		return ntiIdentificador;
	}
	public void setNtiIdentificador(String ntiIdentificador) {
		this.ntiIdentificador = ntiIdentificador;
	}
	
	public String getNtiOrgan() {
		return ntiOrgan;
	}
	public void setNtiOrgan(String ntiOrgan) {
		this.ntiOrgan = ntiOrgan;
	}
	
	public String getNtiOrigen() {
		return ntiOrigen;
	}
	public void setNtiOrigen(String ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}
	
	public String getNtiEstatElaboracio() {
		return ntiEstatElaboracio;
	}
	public void setNtiEstatElaboracio(String ntiEstatElaboracio) {
		this.ntiEstatElaboracio = ntiEstatElaboracio;
	}
	
	public String getNtiNomFormat() {
		return ntiNomFormat;
	}
	public void setNtiNomFormat(String ntiNomFormat) {
		this.ntiNomFormat = ntiNomFormat;
	}
	
	public String getNtiTipusDocumental() {
		return ntiTipusDocumental;
	}
	public void setNtiTipusDocumental(String ntiTipusDocumental) {
		this.ntiTipusDocumental = ntiTipusDocumental;
	}
	
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}
	
	public String getNtiTipoFirma() {
		return ntiTipoFirma;
	}
	public void setNtiTipoFirma(String ntiTipoFirma) {
		this.ntiTipoFirma = ntiTipoFirma;
	}
	
	public String getNtiValorCsv() {
		return ntiValorCsv;
	}
	public void setNtiValorCsv(String ntiValorCsv) {
		this.ntiValorCsv = ntiValorCsv;
	}
	
	public String getNtiDefGenCsv() {
		return ntiDefGenCsv;
	}
	public void setNtiDefGenCsv(String ntiDefGenCsv) {
		this.ntiDefGenCsv = ntiDefGenCsv;
	}
	
	public String getNtiIdDocOrigen() {
		return ntiIdDocOrigen;
	}
	public void setNtiIdDocOrigen(String ntiIdDocOrigen) {
		this.ntiIdDocOrigen = ntiIdDocOrigen;
	}
	
	
	private static final long serialVersionUID = 774909297938469787L;
	
}
