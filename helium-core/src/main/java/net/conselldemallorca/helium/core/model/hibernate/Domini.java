/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa un domini per fer consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_domini",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "entorn_id"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_domini",
		indexes = {
				@Index(name = "hel_domini_entorn_i", columnNames = {"entorn_id"}),
				@Index(name = "hel_domini_exptip_i", columnNames = {"expedient_tipus_id"})})
public class Domini implements Serializable, GenericEntity<Long> {

	public enum TipusDomini {
		CONSULTA_SQL,
		CONSULTA_WS
	}

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String nom;
	@NotNull
	private TipusDomini tipus;
	@MaxLength(255)
	private String url;
	@MaxLength(1024)
	private String sql;
	@MaxLength(255)
	private String jndiDatasource;
	@MaxLength(255)
	private String descripcio;
	private int cacheSegons = 0;
	@MaxLength(255)
	private String ordreParams;

	@NotNull
	private Entorn entorn;
	private ExpedientTipus expedientTipus;

	private Set<Camp> camps = new HashSet<Camp>();



	public Domini() {}
	public Domini(String codi, String nom) {
		this.codi = codi;
		this.nom = nom;
	}
	public Domini(String codi, String nom, Entorn entorn) {
		this.codi = codi;
		this.nom = nom;
		this.entorn = entorn;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_domini")
	@TableGenerator(name="gen_domini", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="codi", length=64, nullable=false)
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

	@Column(name="nom", length=255, nullable=false)
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	@Column(name="tipus", length=255, nullable=false)
	public TipusDomini getTipus() {
		return tipus;
	}
	public void setTipus(TipusDomini tipus) {
		this.tipus = tipus;
	}

	@Column(name="url", length=255)
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name="sqlexpr", length=1024)
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}

	@Column(name="jndi_datasource", length=255)
	public String getJndiDatasource() {
		return jndiDatasource;
	}
	public void setJndiDatasource(String jndiDatasource) {
		this.jndiDatasource = jndiDatasource;
	}

	@Column(name="descripcio", length=255)
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	@Column(name="cache_segons")
	public int getCacheSegons() {
		return cacheSegons;
	}
	public void setCacheSegons(int cacheSegons) {
		this.cacheSegons = cacheSegons;
	}

	@Column(name="ordre_params", length=255)
	public String getOrdreParams() {
		return ordreParams;
	}
	public void setOrdreParams(String ordreParams) {
		this.ordreParams = ordreParams;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="entorn_id")
	@ForeignKey(name="hel_entorn_domini_fk")
	public Entorn getEntorn() {
		return entorn;
	}
	public void setEntorn(Entorn entorn) {
		this.entorn = entorn;
	}

	@ManyToOne
	@JoinColumn(name="expedient_tipus_id")
	@ForeignKey(name="hel_exptip_domini_fk")
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	@OneToMany(mappedBy="domini")
	public Set<Camp> getCamps() {
		return this.camps;
	}
	public void setCamps(Set<Camp> camps) {
		this.camps= camps;
	}
	public void addCamp(Camp camp) {
		getCamps().add(camp);
	}
	public void removeCamp(Camp camp) {
		getCamps().remove(camp);
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
		Domini other = (Domini) obj;
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
