package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.v3.core.api.dto.DominiDto.OrigenCredencials;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto.TipusAuthDomini;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto.TipusDomini;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;

public class ExpedientTipusDominiCommand {

	private Long id;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 64, groups = {Creacio.class, Modificacio.class})
	private String codi;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private TipusDomini tipus;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String descripcio;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String url;
	private TipusAuthDomini tipusAuth;
	private OrigenCredencials origenCredencials;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String usuari;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String contrasenya;
	@Size(max = 1024, groups = {Creacio.class, Modificacio.class})
	private String sql;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String jndiDatasource;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private Integer cacheSegons = 0;
	private Integer timeout = 0;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String ordreParams;
	@NotNull
	private EntornDto entorn;
	private int numErrors;

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
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
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
	
	public interface Creacio {}
	public interface Modificacio {}
}
