/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import java.io.Serializable;

import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesSimpleTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesTipusEnumDto;

/**
 * DTO amb informació d'un document per a l'exportació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentExportacio implements Serializable {

	private String codi;
	private String nom;
	private boolean plantilla;
	private String descripcio;
	private byte[] arxiuContingut;
	private String arxiuNom;
	private String codiCampData;
	private String contentType;
	private String custodiaCodi;
	private Integer tipusDocPortasignatures;
	private boolean adjuntarAuto;
	private String convertirExtensio;
	private String extensionsPermeses;
	private boolean ignored;
	private boolean generarNomesTasca;
	private boolean portafirmesActiu = false;
	private PortafirmesTipusEnumDto portafirmesFluxTipus;	
	private PortafirmesSimpleTipusEnumDto portafirmesSequenciaTipus;
	private String[] portafirmesResponsables;
	private String portafirmesFluxId;
	private String portafirmesFluxNom;
	private NtiTipoDocumentalEnumDto ntiTipoDocumental;
	private NtiOrigenEnumDto ntiOrigen;
	private NtiEstadoElaboracionEnumDto ntiEstadoElaboracion;	
	
	public DocumentExportacio(
			String codi,
			String nom,
			String descripcio,
			byte[] arxiuContingut,
			String arxiuNom,
			boolean plantilla) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.arxiuContingut = arxiuContingut;
		this.arxiuNom = arxiuNom;
		this.plantilla = plantilla;
	}	

	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public boolean isPlantilla() {
		return plantilla;
	}

	public void setPlantilla(boolean plantilla) {
		this.plantilla = plantilla;
	}

	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public byte[] getArxiuContingut() {
		return arxiuContingut;
	}

	public void setArxiuContingut(byte[] arxiuContingut) {
		this.arxiuContingut = arxiuContingut;
	}

	public String getArxiuNom() {
		return arxiuNom;
	}

	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}

	public String getCodiCampData() {
		return codiCampData;
	}

	public void setCodiCampData(String codiCampData) {
		this.codiCampData = codiCampData;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getCustodiaCodi() {
		return custodiaCodi;
	}

	public void setCustodiaCodi(String custodiaCodi) {
		this.custodiaCodi = custodiaCodi;
	}

	public Integer getTipusDocPortasignatures() {
		return tipusDocPortasignatures;
	}

	public void setTipusDocPortasignatures(Integer tipusDocPortasignatures) {
		this.tipusDocPortasignatures = tipusDocPortasignatures;
	}

	public boolean isAdjuntarAuto() {
		return adjuntarAuto;
	}

	public void setAdjuntarAuto(boolean adjuntarAuto) {
		this.adjuntarAuto = adjuntarAuto;
	}

	public String getConvertirExtensio() {
		return convertirExtensio;
	}

	public void setConvertirExtensio(String convertirExtensio) {
		this.convertirExtensio = convertirExtensio;
	}

	public String getExtensionsPermeses() {
		return extensionsPermeses;
	}

	public void setExtensionsPermeses(String extensionsPermeses) {
		this.extensionsPermeses = extensionsPermeses;
	}

	public boolean isIgnored() {
		return ignored;
	}

	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}

	public NtiTipoDocumentalEnumDto getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}

	public void setNtiTipoDocumental(NtiTipoDocumentalEnumDto ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}

	public boolean isGenerarNomesTasca() {
		return generarNomesTasca;
	}

	public void setGenerarNomesTasca(boolean generarNomesTasca) {
		this.generarNomesTasca = generarNomesTasca;
	}

	public boolean isPortafirmesActiu() {
		return portafirmesActiu;
	}

	public void setPortafirmesActiu(boolean portafirmesActiu) {
		this.portafirmesActiu = portafirmesActiu;
	}

	public PortafirmesTipusEnumDto getPortafirmesFluxTipus() {
		return portafirmesFluxTipus;
	}

	public void setPortafirmesFluxTipus(PortafirmesTipusEnumDto portafirmesFluxTipus) {
		this.portafirmesFluxTipus = portafirmesFluxTipus;
	}

	public PortafirmesSimpleTipusEnumDto getPortafirmesSequenciaTipus() {
		return portafirmesSequenciaTipus;
	}

	public void setPortafirmesSequenciaTipus(PortafirmesSimpleTipusEnumDto portafirmesSequenciaTipus) {
		this.portafirmesSequenciaTipus = portafirmesSequenciaTipus;
	}

	public String[] getPortafirmesResponsables() {
		return portafirmesResponsables;
	}

	public void setPortafirmesResponsables(String[] portafirmesResponsables) {
		this.portafirmesResponsables = portafirmesResponsables;
	}

	public String getPortafirmesFluxId() {
		return portafirmesFluxId;
	}

	public void setPortafirmesFluxId(String portafirmesFluxId) {
		this.portafirmesFluxId = portafirmesFluxId;
	}

	public String getPortafirmesFluxNom() {
		return portafirmesFluxNom;
	}

	public void setPortafirmesFluxNom(String portafirmesFluxNom) {
		this.portafirmesFluxNom = portafirmesFluxNom;
	}
	
	public NtiOrigenEnumDto getNtiOrigen() {
		return ntiOrigen;
	}

	public void setNtiOrigen(NtiOrigenEnumDto ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}

	public NtiEstadoElaboracionEnumDto getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}

	public void setNtiEstadoElaboracion(NtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
	}

	private static final long serialVersionUID = 1L;
}