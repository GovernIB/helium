package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * InformaciÃƒÂ³ d'una avis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatOrganitzativaDto {

	private Long id;
	private String codi;
	private String denominacio;
	private String nifCif;
	private String codiUnitatSuperior;
	private String codiUnitatArrel;
	private Date dataCreacioOficial;
	private Date dataSupressioOficial;
	private Date dataExtincioFuncional;
	private Date dataAnulacio;
	private Date dataActualitzacio;
	private Date dataSincronitzacio;	
	private String estat; // V: Vigente, E: Extinguido, A: Anulado, T: Transitorio

	private String codiPais;
	private String codiComunitat;
	private String codiProvincia;
	private String codiPostal;
	private String nomLocalitat;
	private String localitat;

	private String adressa;
	private Long tipusVia;
	private String nomVia;
	private String numVia; 
	protected List<String> historicosUO;
	
	private TipusTransicioEnumDto tipusTransicio;
	
	private List<UnitatOrganitzativaDto> noves;
	private Map<String, String> altresUnitatsFusionades;

	private List<UnitatOrganitzativaDto> lastHistoricosUnitats;
	
	private String denominacioUnitatSuperior;
	
	
	public boolean isObsoleta() {
		if (estat.equals("E") || estat.equals("A") || estat.equals("T")) {
			return true;
		} else {
			return false;
		}
	}
	
	public UnitatOrganitzativaDto() {
		
	}
	
	public UnitatOrganitzativaDto(
			String codi,
			String denominacio,
			String codiUnitatSuperior,
			String codiUnitatArrel,
			String estat,
			List<String> historicosUO) {
		this.codi = codi;
		this.denominacio = denominacio;
		this.codiUnitatSuperior = codiUnitatSuperior;
		this.codiUnitatArrel = codiUnitatArrel;
		this.estat = estat;
		this.historicosUO = historicosUO;
	}
	
	public UnitatOrganitzativaDto(
			String codi,
			String denominacio,
			String nifCif,
			Date dataCreacioOficial,
			String estat,
			String codiUnitatSuperior,
			String codiUnitatArrel,
			Long codiPais,
			Long codiComunitat,
			Long codiProvincia,
			String codiPostal,
			String nomLocalitat,
			Long tipusVia,
			String nomVia,
			String numVia,
			List<String> historicosUO) {
		this.codi = codi;
		this.denominacio = denominacio;
		this.nifCif = nifCif;
		this.dataCreacioOficial = dataCreacioOficial;
		this.estat = estat;
		this.codiUnitatSuperior = codiUnitatSuperior;
		this.codiUnitatArrel = codiUnitatArrel;
		this.codiPais = codiPais != null? codiPais.toString() : "";
		this.codiComunitat = codiComunitat != null? codiComunitat.toString() : "";
		this.codiProvincia = codiProvincia != null? codiProvincia.toString() : "";
		this.codiPostal = codiPostal;
		this.nomLocalitat = nomLocalitat;
		this.tipusVia = tipusVia;
		this.nomVia = nomVia;
		this.numVia = numVia;
		this.historicosUO = historicosUO;
	}
	
	public UnitatOrganitzativaDto(
			Long id,
			String codi,
			String denominacio,
			String nifCif,
			Date dataCreacioOficial,
			String estat,
			String codiUnitatSuperior,
			String codiUnitatArrel,
			String codiPais,
			String codiComunitat,
			String codiProvincia,
			String codiPostal,
			String nomLocalitat,
			String localitat,
			Long tipusVia,
			String nomVia,
			String numVia,
			String adressa,
			List<String> historicosUO) {
		this.id = id;
		this.codi = codi;
		this.denominacio = denominacio;
		this.nifCif = nifCif;
		this.dataCreacioOficial = dataCreacioOficial;
		this.estat = estat;
		this.codiUnitatSuperior = codiUnitatSuperior;
		this.codiUnitatArrel = codiUnitatArrel;
		this.codiPais = codiPais != null? codiPais.toString() : "";
		this.codiComunitat = codiComunitat;
		this.codiProvincia = codiProvincia;
		this.codiPostal = codiPostal;
		this.nomLocalitat = nomLocalitat;
		this.localitat=localitat;
		this.tipusVia = tipusVia;
		this.nomVia = nomVia;
		this.numVia = numVia;
		this.adressa=adressa;
		this.historicosUO = historicosUO;
	}
	public String getCodiIDenominacioUnitatSuperior() {
		return codiUnitatSuperior  + (denominacioUnitatSuperior != null ? " - " + denominacioUnitatSuperior : "");
	}

	public String getDenominacioUnitatSuperior() {
		return denominacioUnitatSuperior;
	}
	public void setDenominacioUnitatSuperior(String denominacioUnitatSuperior) {
		this.denominacioUnitatSuperior = denominacioUnitatSuperior;
	}
	public List<UnitatOrganitzativaDto> getLastHistoricosUnitats() {
		return lastHistoricosUnitats;
	}

	public void setLastHistoricosUnitats(List<UnitatOrganitzativaDto> lastHistoricosUnitats) {
		this.lastHistoricosUnitats = lastHistoricosUnitats;
	}
	
	public Map<String, String> getAltresUnitatsFusionades() {
		return altresUnitatsFusionades;
	}
	public void setAltresUnitatsFusionades(Map<String, String> altresUnitatsFusionades) {
		this.altresUnitatsFusionades = altresUnitatsFusionades;
	}
	public TipusTransicioEnumDto getTipusTransicio() {
		return tipusTransicio;
	}
	public void setTipusTransicio(TipusTransicioEnumDto tipusTransicio) {
		this.tipusTransicio = tipusTransicio;
	}
	public List<UnitatOrganitzativaDto> getNoves() {
		return noves;
	}
	public void setNoves(List<UnitatOrganitzativaDto> noves) {
		this.noves = noves;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getDenominacio() {
		return denominacio;
	}
	public void setDenominacio(String denominacio) {
		this.denominacio = denominacio;
	}
	public String getNifCif() {
		return nifCif;
	}
	public void setNifCif(String nifCif) {
		this.nifCif = nifCif;
	}
	public String getCodiUnitatSuperior() {
		return codiUnitatSuperior;
	}
	public void setCodiUnitatSuperior(String codiUnitatSuperior) {
		this.codiUnitatSuperior = codiUnitatSuperior;
	}
	public String getCodiUnitatArrel() {
		return codiUnitatArrel;
	}
	public void setCodiUnitatArrel(String codiUnitatArrel) {
		this.codiUnitatArrel = codiUnitatArrel;
	}
	public String getEstat() {
		return estat;
	}
	public void setEstat(String estat) {
		this.estat = estat;
	}
	public Date getDataCreacioOficial() {
		return dataCreacioOficial;
	}
	public void setDataCreacioOficial(Date dataCreacioOficial) {
		this.dataCreacioOficial = dataCreacioOficial;
	}
	public Date getDataSupressioOficial() {
		return dataSupressioOficial;
	}
	public void setDataSupressioOficial(Date dataSupressioOficial) {
		this.dataSupressioOficial = dataSupressioOficial;
	}
	public Date getDataExtincioFuncional() {
		return dataExtincioFuncional;
	}
	public void setDataExtincioFuncional(Date dataExtincioFuncional) {
		this.dataExtincioFuncional = dataExtincioFuncional;
	}
	public Date getDataAnulacio() {
		return dataAnulacio;
	}
	public void setDataAnulacio(Date dataAnulacio) {
		this.dataAnulacio = dataAnulacio;
	}
	
	public Date getDataActualitzacio() {
		return dataActualitzacio;
	}

	public void setDataActualitzacio(Date dataActualitzacio) {
		this.dataActualitzacio = dataActualitzacio;
	}

	public Date getDataSincronitzacio() {
		return dataSincronitzacio;
	}

	public void setDataSincronitzacio(Date dataSincronitzacio) {
		this.dataSincronitzacio = dataSincronitzacio;
	}

	public String getCodiPais() {
		return codiPais;
	}
	public void setCodiPais(String codiPais) {
		this.codiPais = codiPais;
	}
	public String getCodiComunitat() {
		return codiComunitat;
	}
	public void setCodiComunitat(String codiComunitat) {
		this.codiComunitat = codiComunitat;
	}
	public String getCodiProvincia() {
		return codiProvincia;
	}
	public void setCodiProvincia(String codiProvincia) {
		this.codiProvincia = codiProvincia;
	}
	public String getCodiPostal() {
		return codiPostal;
	}
	public void setCodiPostal(String codiPostal) {
		this.codiPostal = codiPostal;
	}
	public String getNomLocalitat() {
		return nomLocalitat;
	}
	public void setNomLocalitat(String nomLocalitat) {
		this.nomLocalitat = nomLocalitat;
	}
	public String getLocalitat() {
		return localitat;
	}
	public void setLocalitat(String localitat) {
		this.localitat = localitat;
	}
	public String getAdressa() {
		return adressa;
	}
	public void setAdressa(String adressa) {
		this.adressa = adressa;
	}
	public Long getTipusVia() {
		return tipusVia;
	}
	public void setTipusVia(Long tipusVia) {
		this.tipusVia = tipusVia;
	}
	public String getNomVia() {
		return nomVia;
	}
	public void setNomVia(String nomVia) {
		this.nomVia = nomVia;
	}
	public String getNumVia() {
		return numVia;
	}
	public void setNumVia(String numVia) {
		this.numVia = numVia;
	}

	public String getNom() {
		return this.denominacio + " (" + this.codi + ")";
	}
	
	public String getCodiAndNom() {
		return this.codi + " - " + this.denominacio + "";
	}
	
	public List<String> getHistoricosUO() {
		return historicosUO;
	}

	public void setHistoricosUO(List<String> historicosUO) {
		this.historicosUO = historicosUO;
	}



	private static final long serialVersionUID = -5602898182576627524L;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
