/**
 * 
 */
package es.caib.helium.persist.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Classe del model de dades que representa un interessat
 * d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(
		name = "hel_anotacio_interessat",
		indexes = {
				@Index(name = "hel_anotacio_inter_fk_i", columnList = "anotacio_id"),
				@Index(name = "hel_representant_anotacio_fk_i", columnList = "representant_id")
		}
)
public class AnotacioInteressat implements Serializable, GenericEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_anot_inter")
	@TableGenerator(name="gen_anot_inter", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;
	@Column(name = "adresa", length = 160)
	private String adresa;
	@Column(name = "canal", length = 30)
	private String canal;
	@Column(name = "cp", length = 5)
	private String cp;
	@Column(name = "doc_numero", length = 17)
	private String documentNumero;
	@Enumerated(EnumType.STRING)
	@Column(name = "doc_tipus", length = 15)
	private String documentTipus;
	@Column(name = "email", length = 160)
	private String email;
	@Column(name = "llinatge1", length = 30)
	private String llinatge1;
	@Column(name = "llinatge2", length = 30)
	private String llinatge2;
	@Column(name = "nom", length = 30)
	private String nom;
	@Column(name = "observacions", length = 160)
	private String observacions;
	@Column(name = "municipi_codi", length = 100)
	private String municipiCodi;
	@Column(name = "pais_codi", length = 4)
	private String paisCodi;
	@Column(name = "provincia_codi", length = 100)
	private String provinciaCodi;
	@Column(name = "municipi", length = 200)
	private String municipi;
	@Column(name = "pais", length = 200)
	private String pais;
	@Column(name = "provincia", length = 200)
	private String provincia;
	@Column(name = "rao_social", length = 80)
	private String raoSocial;
	@Column(name = "telefon", length = 20)
	private String telefon;
	@Enumerated(EnumType.STRING)
	@Column(name = "tipus", length = 40, nullable = false)
	private String tipus;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(
			name = "representant_id",
			foreignKey = @ForeignKey(name = "hel_interessat_representant_fk"))
	private AnotacioInteressat representant;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "anotacio_id",
			foreignKey = @ForeignKey(name = "hel_interessat_anotacio_fk"))
	private Anotacio anotacio;
	
	@Column(name = "organ_codi", length = 9)
	private String organCodi;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public static Builder getBuilder(
			String tipus) {
		return new Builder(
				tipus);
	}

	public static class Builder {
		AnotacioInteressat built;

		Builder(
				String tipus) {
			built = new AnotacioInteressat();
			built.tipus = tipus;
		}

		public Builder adresa(String adresa) {
			built.adresa = adresa;
			return this;
		}
		public Builder canal(String canal) {
			built.canal = canal;
			return this;
		}
		public Builder cp(String cp) {
			built.cp = cp;
			return this;
		}
		public Builder documentNumero(String documentNumero) {
			built.documentNumero = documentNumero;
			return this;
		}
		public Builder documentTipus(String documentTipus) {
			built.documentTipus = documentTipus;
			return this;
		}
		public Builder email(String email) {
			built.email = email;
			return this;
		}
		public Builder llinatge1(String llinatge1) {
			built.llinatge1 = llinatge1;
			return this;
		}
		public Builder llinatge2(String llinatge2) {
			built.llinatge2 = llinatge2;
			return this;
		}
		public Builder municipiCodi(String municipiCodi) {
			built.municipiCodi = municipiCodi;
			return this;
		}
		public Builder nom(String nom) {
			built.nom = nom;
			return this;
		}
		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		public Builder paisCodi(String paisCodi) {
			built.paisCodi = paisCodi;
			return this;
		}
		public Builder provinciaCodi(String provinciaCodi) {
			built.provinciaCodi = provinciaCodi;
			return this;
		}
		public Builder raoSocial(String raoSocial) {
			built.raoSocial = raoSocial;
			return this;
		}
		public Builder telefon(String telefon) {
			built.telefon = telefon;
			return this;
		}
		public Builder representant(AnotacioInteressat representant) {
			built.representant = representant;
			return this;
		}
		public Builder anotacio(Anotacio anotacio) {
			built.anotacio = anotacio;
			return this;
		}
		public Builder pais(String pais) {
			built.pais = pais;
			return this;
		}
		public Builder provincia(String provincia) {
			built.provincia = provincia;
			return this;
		}
		public Builder municipi(String municipi) {
			built.municipi = municipi;
			return this;
		}		
		public Builder organCodi(String organCodi) {
			built.organCodi = organCodi;
			return this;
		}			
		public AnotacioInteressat build() {
			return built;
		}
	}

	public String getOrganCodi() {
		return organCodi;
	}
	public void setOrganCodi(String organCodi) {
		this.organCodi = organCodi;
	}
	public String getAdresa() {
		return adresa;
	}
	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}
	public String getCanal() {
		return canal;
	}
	public void setCanal(String canal) {
		this.canal = canal;
	}
	public String getCp() {
		return cp;
	}
	public void setCp(String cp) {
		this.cp = cp;
	}
	public String getDocumentNumero() {
		return documentNumero;
	}
	public void setDocumentNumero(String documentNumero) {
		this.documentNumero = documentNumero;
	}
	public String getDocumentTipus() {
		return documentTipus;
	}
	public void setDocumentTipus(String documentTipus) {
		this.documentTipus = documentTipus;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public String getMunicipiCodi() {
		return municipiCodi;
	}
	public void setMunicipiCodi(String municipiCodi) {
		this.municipiCodi = municipiCodi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public String getPaisCodi() {
		return paisCodi;
	}
	public void setPaisCodi(String paisCodi) {
		this.paisCodi = paisCodi;
	}
	public String getProvinciaCodi() {
		return provinciaCodi;
	}
	public void setProvinciaCodi(String provinciaCodi) {
		this.provinciaCodi = provinciaCodi;
	}
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}

	public AnotacioInteressat getRepresentant() {
		return representant;
	}
	public void setRepresentant(AnotacioInteressat representant) {
		this.representant = representant;
	}

	public Anotacio getAnotacio() {
		return anotacio;
	}
	public void setAnotacio(Anotacio anotacio) {
		this.anotacio = anotacio;
	}
	public String getMunicipi() {
		return municipi;
	}
	public void setMunicipi(String municipi) {
		this.municipi = municipi;
	}

	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	private static final long serialVersionUID = -3789226945502544187L;
}