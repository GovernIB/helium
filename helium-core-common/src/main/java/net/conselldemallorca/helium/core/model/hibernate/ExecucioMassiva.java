/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa una acció massiva a damunt
 * els expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_exec_massiva")
@org.hibernate.annotations.Table(
		appliesTo = "hel_exec_massiva",
		indexes = @Index(name = "hel_exemas_usuari_i", columnNames = {"usuari"}))
public class ExecucioMassiva implements Serializable, GenericEntity<Long> {

	public enum ExecucioMassivaTipus {
		EXECUTAR_TASCA,
		ACTUALITZAR_VERSIO_DEFPROC,
		ELIMINAR_VERSIO_DEFPROC,
		EXECUTAR_SCRIPT,
		EXECUTAR_ACCIO,
		ATURAR_EXPEDIENT,
		MODIFICAR_VARIABLE,
		MODIFICAR_DOCUMENT,
		REINDEXAR,
		REASSIGNAR,
		BUIDARLOG,
		REPRENDRE_EXPEDIENT,
		REPRENDRE,
		PROPAGAR_PLANTILLES,
		PROPAGAR_CONSULTES,
		FINALITZAR_EXPEDIENT,
		MIGRAR_EXPEDIENT,
		ALTA_MASSIVA,
		// Accions amb anotacions
		REINTENTAR_CONSULTA_ANOTACIONS,
		REINTENTAR_PROCESSAMENT_ANOTACIONS,
		REINTENTAR_PROCESSAMENT_ANOTACIONS_NOMES_ANNEXOS,
		REINTENTAR_MAPEIG_ANOTACIONS,
		ESBORRAR_ANOTACIONS
	}

	private Long id;
	@NotNull
	@MaxLength(64)
	private String usuari;
	@NotNull
	private ExecucioMassivaTipus tipus;
	@NotNull
	private Date dataInici;
	private Date dataFi;
	@MaxLength(255)
	private String param1;
	private byte[] param2;
	private Boolean enviarCorreu;
	private ExpedientTipus expedientTipus;
	private List<ExecucioMassivaExpedient> expedients = new ArrayList<ExecucioMassivaExpedient>();
	private Long entorn;
	
	@MaxLength(2000)
	private String rols;

	public ExecucioMassiva() {}
	public ExecucioMassiva(String usuari, ExecucioMassivaTipus tipus) {
		this.usuari = usuari;
		this.tipus = tipus;
		this.dataInici = new Date();
		
//		Collection<GrantedAuthority> authorities = auth.getAuthorities() != null ? (Collection<GrantedAuthority>)auth.getAuthorities() : null;
//		this.setAuthenticationRoles(authorities);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_estat")
	@TableGenerator(name="gen_estat", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="usuari", length=64, nullable=false)
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}

	@Column(name="tipus", nullable=false)
	public ExecucioMassivaTipus getTipus() {
		return tipus;
	}
	public void setTipus(ExecucioMassivaTipus tipus) {
		this.tipus = tipus;
	}

	@Column(name="data_inici", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}

	@Column(name="data_fi")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}

	@Column(name="param1", length=255)
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}

	@Lob
	@Column(name="param2")
	public byte[] getParam2() {
		return param2;
	}
	public void setParam2(byte[] param2) {
		this.param2 = param2;
	}

	@Column(name="env_correu")
	public Boolean getEnviarCorreu() {
		return enviarCorreu;
	}
	public void setEnviarCorreu(Boolean enviarCorreu) {
		this.enviarCorreu = enviarCorreu;
	}
	
	@ManyToOne(optional=true)
	@JoinColumn(name="expedient_tipus_id")
	@ForeignKey(name="hel_exptipus_exemas_fk")
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	@OneToMany(mappedBy="execucioMassiva", cascade=CascadeType.ALL)
	@OrderBy("ordre asc")
	public List<ExecucioMassivaExpedient> getExpedients() {
		return this.expedients;
	}
	public void setExpedients(List<ExecucioMassivaExpedient> expedients) {
		this.expedients = expedients;
	}
	public void addExpedient(ExecucioMassivaExpedient expedient) {
		getExpedients().add(expedient);
	}
	public void removeExpedient(ExecucioMassivaExpedient expedient) {
		getExpedients().remove(expedient);
	}
	
	@Column(name="entorn")
	public Long getEntorn() {
		return entorn;
	}
	public void setEntorn(Long entorn) {
		this.entorn = entorn;
	}
	
	@Column(name="rols")
	public String getRols() {
		return rols;
	}
	public void setRols(String rols) {
		this.rols = rols;
	}
	
	@Transient
	public Principal getAuthenticationPrincipal() {
		final String user = usuari;
		
		Principal principal = new Principal() {
			public String getName() {
				return user;
			}
		};
		
		return principal;
	}
	
	@Transient
	public List<GrantedAuthority> getAuthenticationRoles() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if (rols != null && !rols.isEmpty()) {
			for (String rol: rols.split(",")) {
				authorities.add(new SimpleGrantedAuthority(rol));
			}
		}
		return authorities;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataInici == null) ? 0 : dataInici.hashCode());
		result = prime * result
				+ ((expedientTipus == null) ? 0 : expedientTipus.hashCode());
		result = prime * result + ((tipus == null) ? 0 : tipus.hashCode());
		result = prime * result + ((usuari == null) ? 0 : usuari.hashCode());
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
		ExecucioMassiva other = (ExecucioMassiva) obj;
		if (dataInici == null) {
			if (other.dataInici != null)
				return false;
		} else if (!dataInici.equals(other.dataInici))
			return false;
		if (expedientTipus == null) {
			if (other.expedientTipus != null)
				return false;
		} else if (!expedientTipus.equals(other.expedientTipus))
			return false;
		if (tipus != other.tipus)
			return false;
		if (usuari == null) {
			if (other.usuari != null)
				return false;
		} else if (!usuari.equals(other.usuari))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;
}
