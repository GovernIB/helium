/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * Objecte de domini que representa un domini per fer consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DominiDto extends HeretableDto implements Serializable {

	public enum TipusDomini {
		CONSULTA_SQL,
		CONSULTA_WS
	}
	public enum TipusAuthDomini {
		NONE,
		HTTP_BASIC,
		USERNAMETOKEN
	}
	public enum OrigenCredencials {
		ATRIBUTS,
		PROPERTIES
	}

	private Long id;
	private String codi;
	private String nom;
	private TipusDomini tipus;
	private String url;
	private TipusAuthDomini tipusAuth;
	private OrigenCredencials origenCredencials;
	private String usuari;
	private String contrasenya;
	private String sql;
	private String jndiDatasource;
	private String descripcio;
	private int cacheSegons = 0;
	private Integer timeout = 0;
	private String ordreParams;

	private EntornDto entorn;

	private int numErrors;



	public DominiDto() {}
	public DominiDto(String codi, String nom) {
		this.codi = codi;
		this.nom = nom;
	}
	public DominiDto(String codi, String nom, EntornDto entorn) {
		this.codi = codi;
		this.nom = nom;
		this.entorn = entorn;
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
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public TipusDomini getTipus() {
		return tipus;
	}
	public void setTipus(TipusDomini tipus) {
		this.tipus = tipus;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public TipusAuthDomini getTipusAuth() {
		return tipusAuth;
	}
	public void setTipusAuth(TipusAuthDomini tipusAuth) {
		this.tipusAuth = tipusAuth;
	}
	public OrigenCredencials getOrigenCredencials() {
		return origenCredencials;
	}
	public void setOrigenCredencials(OrigenCredencials origenCredencials) {
		this.origenCredencials = origenCredencials;
	}
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public String getContrasenya() {
		return contrasenya;
	}
	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getJndiDatasource() {
		return jndiDatasource;
	}
	public void setJndiDatasource(String jndiDatasource) {
		this.jndiDatasource = jndiDatasource;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public int getCacheSegons() {
		return cacheSegons;
	}
	public void setCacheSegons(int cacheSegons) {
		this.cacheSegons = cacheSegons;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public String getOrdreParams() {
		return ordreParams;
	}
	public void setOrdreParams(String ordreParams) {
		this.ordreParams = ordreParams;
	}
	public EntornDto getEntorn() {
		return entorn;
	}
	public void setEntorn(EntornDto entorn) {
		this.entorn = entorn;
	}
	public int getNumErrors() {
		return numErrors;
	}
	public void setNumErrors(int numErrors) {
		this.numErrors = numErrors;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result + ((entorn == null) ? 0 : entorn.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DominiDto other = (DominiDto) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (entorn == null) {
			if (other.entorn != null)
				return false;
		} else if (!entorn.equals(other.entorn))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
