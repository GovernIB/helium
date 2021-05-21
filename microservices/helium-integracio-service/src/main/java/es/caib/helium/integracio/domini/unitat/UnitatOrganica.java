package es.caib.helium.integracio.domini.unitat;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitatOrganica implements Serializable, Comparable<UnitatOrganica> {

	private String codi;
	private String denominacio;
	private String nifCif;
	private String nivellAdministracio;
	private String tipusEntitatPublica;
	private String tipusUnitatOrganica;
	private String poder;
	private String sigles;
	private String codiUnitatSuperior;
	private String codiUnitatArrel;
	private Long nivellJerarquic;
	private Date dataCreacioOficial;
	private Date dataSupressioOficial;
	private Date dataExtincioFuncional;
	private Date dataAnulacio;
	private String estat; // V: Vigente, E: Extinguido, A: Anulado, T: Transitorio
	private String codiPais;
	private String codiComunitat;
	private String codiProvincia;
	private String codiPostal;
	private String nomLocalitat;
	private String adressa;
	private Long tipusVia;
	private String nomVia;
	private String numVia;

	public UnitatOrganica() {

	}

	public UnitatOrganica(String codi, String denominacio, String nifCif, Date dataCreacioOficial, String estat) {
		this.codi = codi;
		this.denominacio = denominacio;
		this.nifCif = nifCif;
		this.dataCreacioOficial = dataCreacioOficial;
		this.estat = estat;
	}

	public UnitatOrganica(String codi, String denominacio, String nifCif, Date dataCreacioOficial, String estat,
			String codiUnitatSuperior, String codiUnitatArrel) {
		this.codi = codi;
		this.denominacio = denominacio;
		this.nifCif = nifCif;
		this.dataCreacioOficial = dataCreacioOficial;
		this.estat = estat;
		this.codiUnitatSuperior = codiUnitatSuperior;
		this.codiUnitatArrel = codiUnitatArrel;
	}

	public UnitatOrganica(String codi, String denominacio, String nifCif, Date dataCreacioOficial, String estat,
			String codiUnitatSuperior, String codiUnitatArrel, Long codiPais, Long codiComunitat, Long codiProvincia,
			String codiPostal, String nomLocalitat, Long tipusVia, String nomVia, String numVia) {
		this.codi = codi;
		this.denominacio = denominacio;
		this.nifCif = nifCif;
		this.dataCreacioOficial = dataCreacioOficial;
		this.estat = estat;
		this.codiUnitatSuperior = codiUnitatSuperior;
		this.codiUnitatArrel = codiUnitatArrel;
		this.codiPais = codiPais != null ? codiPais.toString() : "";
		this.codiComunitat = codiComunitat != null ? codiComunitat.toString() : "";
		this.codiProvincia = codiProvincia != null ? codiProvincia.toString() : "";
		this.codiPostal = codiPostal;
		this.nomLocalitat = nomLocalitat;
		this.tipusVia = tipusVia;
		this.nomVia = nomVia;
		this.numVia = numVia;
	}

	public UnitatOrganica(String codi, String denominacio, String nifCif, String nivellAdministracio,
			String tipusEntitatPublica, String tipusUnitatOrganica, String poder, String sigles,
			String codiUnitatSuperior, String codiUnitatArrel, Long nivellJerarquic, Date dataCreacioOficial,
			Date dataSupressioOficial, Date dataExtincioFuncional, Date dataAnulacio, String estat, String codiPais,
			String codiComunitat, String codiProvincia, String codiPostal, String nomLocalitat, String adressa,
			Long tipusVia, String nomVia, String numVia) {
		super();
		this.codi = codi;
		this.denominacio = denominacio;
		this.nifCif = nifCif;
		this.nivellAdministracio = nivellAdministracio;
		this.tipusEntitatPublica = tipusEntitatPublica;
		this.tipusUnitatOrganica = tipusUnitatOrganica;
		this.poder = poder;
		this.sigles = sigles;
		this.codiUnitatSuperior = codiUnitatSuperior;
		this.codiUnitatArrel = codiUnitatArrel;
		this.nivellJerarquic = nivellJerarquic;
		this.dataCreacioOficial = dataCreacioOficial;
		this.dataSupressioOficial = dataSupressioOficial;
		this.dataExtincioFuncional = dataExtincioFuncional;
		this.dataAnulacio = dataAnulacio;
		this.estat = estat;
		this.codiPais = codiPais;
		this.codiComunitat = codiComunitat;
		this.codiProvincia = codiProvincia;
		this.codiPostal = codiPostal;
		this.nomLocalitat = nomLocalitat;
		this.adressa = adressa;
		this.tipusVia = tipusVia;
		this.nomVia = nomVia;
		this.numVia = numVia;
	}

	@JsonProperty("codigo")
	public void setCodi(String codi) {
		this.codi = codi;
	}

	@JsonProperty("denominacion")
	public void setDenominacio(String denominacio) {
		this.denominacio = denominacio;
	}

	@JsonProperty("localidad")
	public void setNomLocalitat(String nomLocalitat) {
		this.nomLocalitat = nomLocalitat;
	}

	@Override
	public int compareTo(UnitatOrganica o) {
		return denominacio.compareToIgnoreCase(o.getDenominacio());
	}

	private static final long serialVersionUID = -5602898182576627524L;

}
