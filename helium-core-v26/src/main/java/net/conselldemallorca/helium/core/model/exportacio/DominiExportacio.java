/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;

import net.conselldemallorca.helium.core.model.hibernate.Domini.OrigenCredencials;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusAuthDomini;



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
	private TipusAuthDomini tipusAuth;
	private OrigenCredencials origenCredencials;
	private String usuari;
	private String contrasenya;
	private String sql;
	private String jndiDatasource;
	private String descripcio;
	private int cacheSegons = 0;
	private int timeout = 0;



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
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	private static final long serialVersionUID = 1L;

}
