package net.conselldemallorca.helium.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Email;
import org.springmodules.validation.bean.conf.loader.annotation.handler.InThePast;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

/**
 * Objecte de domini que representa una persona
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Entity
@Table(name="hel_persona")
@org.hibernate.annotations.Table(
		appliesTo = "hel_persona",
		indexes = @Index(name = "hel_persona_relleu_i", columnNames = {"relleu_id"}))
public class Persona implements Serializable, GenericEntity<Long> {

	public enum Sexe {
		SEXE_HOME,
		SEXE_DONA}

	public enum Fonts {
		FONT_INTERNA,
		FONT_EXTERNA}

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String nom;
	@NotBlank
	@MaxLength(255)
	private String llinatge1;
	@MaxLength(255)
	private String llinatge2;
	@MaxLength(64)
	private String dni;
	@InThePast
	private Date dataNaixement;
	@NotBlank
	@MaxLength(255)
	@Email
	private String email;
	private Sexe sexe = Sexe.SEXE_HOME;
	private boolean avisCorreu;

	private Fonts font;
	private Persona relleu;



	public Persona() {}
	public Persona(String codi, String nom, String llinatge1, String email) {
		this.codi = codi;
		this.nom = nom;
		this.llinatge1 = llinatge1;
		this.email = email;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_persona")
	@TableGenerator(name="gen_persona", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="codi", length=64, nullable=false, unique=true)
	public String getCodi() {
		return this.codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

	@Column(name="nom", length=255, nullable=false)
	public String getNom() {
		return this.nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	@Column(name="llinatge1", length=255, nullable=false)
	public String getLlinatge1() {
		return this.llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}

	@Column(name="llinatge2", length=255)
	public String getLlinatge2() {
		return this.llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}

	@Column(name="llinatges")
	public String getLlinatges() {
		if (llinatge2 != null && llinatge2.length() > 0)
			return getLlinatge1() + " " + getLlinatge2();
		else
			return getLlinatge1();
	}
	public void setLlinatges(String llinatges) {
		if (llinatges != null && llinatge1 == null && llinatge2 == null) {
			int index = llinatges.lastIndexOf(" ");
			if (index == -1) {
				setLlinatge1(llinatges);
				setLlinatge2(null);
			} else {
				setLlinatge1(llinatges.substring(0, index));
				setLlinatge2(llinatges.substring(index + 1));
			}
		}
	}

	@Column(name="dni", length=64)
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}

	@Column(name="nom_sencer")
	public String getNomSencer() {
		StringBuffer nomSencer = new StringBuffer();
		nomSencer.append(getNom()); nomSencer.append(" ");
		nomSencer.append(getLlinatge1());
		if (getLlinatge2() != null && getLlinatge2().length() > 0) {
			nomSencer.append(" ");
			nomSencer.append(getLlinatge2());
		}
		return nomSencer.toString();
	}
	public void setNomSencer(String nomSencer) {
		if (nomSencer != null) {
			int index = nomSencer.indexOf(" ");
			if (index != -1) {
				setNom(nomSencer.substring(0, index));
				setLlinatges(nomSencer.substring(index + 1));
			}
		}
	}

	@Column(name="data_naixement")
	@Temporal(TemporalType.DATE)
	public Date getDataNaixement() {
		return this.dataNaixement;
	}
	public void setDataNaixement(Date dataNaixement) {
		this.dataNaixement = dataNaixement;
	}

	@Column(name="email")
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@Enumerated
	@Column(name="sexe", nullable=false)
	public Sexe getSexe() {
		return this.sexe;
	}
	public void setSexe(Sexe sexe) {
		this.sexe = sexe;
	}

	@Column(name="avis_correu", nullable=false)
	public Boolean getAvisCorreu() {
		return this.avisCorreu;
	}
	public void setAvisCorreu(Boolean avisCorreu) {
		this.avisCorreu = avisCorreu;
	}

	@Column(name="font", nullable=true)
	public Fonts getFont() {
		return this.font;
	}
	public void setFont(Fonts font) {
		this.font = font;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="relleu_id")
	@ForeignKey(name="hel_relleu_persona_fk")
	public Persona getRelleu() {
		return this.relleu;
	}
	public void setRelleu(Persona relleu) {
		this.relleu = relleu;
	}

	@Transient
	public String getInicialsNomSencer() {
		StringBuffer inicials = new StringBuffer();
		String[] strings = getNomSencer().split(" ");
		for (int i = 0; i < strings.length; i++){
			if (strings[i].length() > 0 && !"i".equalsIgnoreCase(strings[i]) && !"de".equalsIgnoreCase(strings[i])) {
				strings[i] = strings[i].trim();
				char inicial = strings[i].charAt(0);
				inicials.append(inicial);
			}
		}
		return inicials.toString();
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
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
		Persona other = (Persona) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Persona id=" + id + ", codi= " + codi + ", nom=" + getNomSencer() + ", email =" + email + ", sexe=" + sexe;
	}

	private static final long serialVersionUID = 1L;

}
