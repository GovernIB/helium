/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'un domini per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DominiExportacio implements Serializable {

	private String codi;
	private String nom;
	private String tipus;
	private String url;
	private String sql;
	private String jndiDatasource;
	private String descripcio;
	private int cacheSegons = 0;



	public DominiExportacio(
			String codi,
			String nom,
			String tipus) {
		this.codi = codi;
		this.nom = nom;
		this.tipus = tipus;
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
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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



	private static final long serialVersionUID = 1L;

}
