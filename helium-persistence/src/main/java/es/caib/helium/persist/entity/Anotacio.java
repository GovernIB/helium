/**
 * 
 */
package es.caib.helium.persist.entity;

import es.caib.helium.logic.intf.dto.AnotacioEstatEnumDto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe del model de dades que representa una anotació al registre rebuda com a Backoffice 
 * de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(
		name = "hel_anotacio",
		indexes = {
				@Index(name = "hel_anotacio_expedient_fk_i", columnList = "expedient_id"),
				@Index(name = "hel_anotacio_et_fk_i", columnList = "expedient_tipus_id")
		}
)
public class Anotacio implements Serializable, GenericEntity<Long> {

	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_anot")
	@TableGenerator(name="gen_anot", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;
	
	/** Expedient tipus associat per codi de procediment */
	@ManyToOne(optional=true)
	@JoinColumn(
			name="expedient_tipus_id",
			foreignKey = @ForeignKey(name="hel_expedient_tipus_anotacio_fk"))
	private ExpedientTipus expedientTipus;
	/** Expedient amb el qual s'associa o inclou l'anotació. */
	@ManyToOne(optional=true)
	@JoinColumn(name="expedient_id")
	private Expedient expedient;

	// Dades del processament de la petició d'anotació
	
	/** Estat de l'anotació a Helium. */
	/** Identificador de l'anotació a Distribucio per relacionar la informació amb la petició de distribució */
	@Column(name = "distribucio_id", length = 80, nullable = false)
	private String distribucioId;
	@Column(name = "distribucio_clau_acces", length = 200, nullable = false)
	private String distribucioClauAcces;
	@Enumerated
	@Column(name = "estat", nullable = false)
	private AnotacioEstatEnumDto estat;
	@Column(name = "data_recepcio", nullable = false)
	private Date dataRecepcio;
	@Column(name = "data_processament")
	private Date dataProcessament;
	/** Motiu del rebuig en cas de rebutjar l'anotació */
	@Column(name = "rebuig_motiu", length = 500)
	private String rebuigMotiu;
	
	// Dades pròpies de l'anotació
	
	@Column(name = "aplicacio_codi", length = 20)
	private String aplicacioCodi;
	@Column(name = "aplicacio_versio", length = 15)
	private String aplicacioVersio;
	@Column(name = "assumpte_codi_codi", length = 16)
	private String assumpteCodiCodi;
	@Column(name = "assumpte_codi_desc", length = 100)
	private String assumpteCodiDescripcio;
	@Column(name = "assumpte_tipus_codi", length = 16, nullable = false)
	private String assumpteTipusCodi;
	@Column(name = "assumpte_tipus_desc", length = 100)
	private String assumpteTipusDescripcio;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", nullable = false)
	private Date data;
	@Column(name = "doc_fisica_codi", length = 1)
	private String docFisicaCodi;
	@Column(name = "doc_fisica_desc", length = 100)
	private String docFisicaDescripcio;
	@Column(name = "entitat_codi", length = 21, nullable = false)
	private String entitatCodi;
	@Column(name = "entitat_desc", length = 100)
	private String entitatDescripcio;
	@Column(name = "expedient_numero", length = 80)
	private String expedientNumero;
	@Column(name = "exposa", length = 4000)
	private String exposa;
	@Column(name = "extracte", length = 240)
	private String extracte;
	@Column(name = "procediment_codi", length = 20)
	private String procedimentCodi;
	@Column(name = "identificador", length = 100, nullable = false)
	private String identificador;
	@Column(name = "idioma_codi", length = 2, nullable = false)
	private String idiomaCodi;
	@Column(name = "idioma_desc", length = 100)
	private String idiomaDescripcio;
	@Column(name = "llibre_codi", length = 4, nullable = false)
	private String llibreCodi;
	@Column(name = "llibre_desc", length = 100)
	private String llibreDescripcio;
	@Column(name = "observacions", length = 50)
	private String observacions;
	@Column(name = "oficina_codi", length = 21, nullable = false)
	private String oficinaCodi;
	@Column(name = "oficina_desc", length = 100)
	private String oficinaDescripcio;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "origen_data")
	private Date origenData;
	@Column(name = "origen_registre_num", length = 80)
	private String origenRegistreNumero;
	@Column(name = "ref_externa", length = 16)
	private String refExterna;
	@Column(name = "solicita", length = 4000)
	private String solicita;
	@Column(name = "transport_num", length = 20)
	private String transportNumero;
	@Column(name = "transport_tipus_codi", length = 2)
	private String transportTipusCodi;
	@Column(name = "transport_tipus_desc", length = 100)
	private String transportTipusDescripcio;
	@Column(name = "usuari_codi", length = 20)
	private String usuariCodi;
	@Column(name = "usuari_nom", length = 80)
	private String usuariNom;
	@Column(name = "desti_codi", length = 21, nullable = false)
	private String destiCodi;
	@Column(name = "desti_descripcio", length = 100)
	private String destiDescripcio;
	
	@OneToMany(
			mappedBy = "anotacio",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<AnotacioInteressat> interessats = new ArrayList<AnotacioInteressat>();
	
	@OneToMany(
			mappedBy = "anotacio",
			cascade = { CascadeType.ALL },
			orphanRemoval = true,
			fetch = FetchType.LAZY)
	private List<AnotacioAnnex> annexos = new ArrayList<AnotacioAnnex>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public static Builder getBuilder(
			String distribucioId,
			String distribucioClauAcces,
			Date dataRecepcio,
			AnotacioEstatEnumDto estat,
			String assumpteTipusCodi,
			Date data,
			String entitatCodi,
			String identificador,
			String idiomaCodi,
			String llibreCodi,
			String oficinaCodi,
			String destiCodi,
			ExpedientTipus expedientTipus,
			Expedient expedient) {
		return new Builder(
				distribucioId,
				distribucioClauAcces,
				dataRecepcio,
				estat,
				assumpteTipusCodi,
				data,
				entitatCodi,
				identificador,
				idiomaCodi,
				llibreCodi,
				oficinaCodi,
				destiCodi,
				expedientTipus,
				expedient);
	}
	

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		Anotacio built;

		Builder(
				String distribucioId,
				String distribucioClauAcces,
				Date dataRecepcio,
				AnotacioEstatEnumDto estat,
				String assumpteTipusCodi,
				Date data,
				String entitatCodi,
				String identificador,
				String idiomaCodi,
				String llibreCodi,
				String oficinaCodi,
				String destiCodi,
				ExpedientTipus expedientTipus,
				Expedient expedient) {
			built = new Anotacio();
			built.distribucioId = distribucioId;
			built.distribucioClauAcces = distribucioClauAcces;
			built.dataRecepcio = dataRecepcio;
			built.estat = estat;
			built.assumpteTipusCodi = assumpteTipusCodi;
			built.data = data;
			built.entitatCodi = entitatCodi;
			built.identificador = identificador;
			built.idiomaCodi = idiomaCodi;
			built.llibreCodi = llibreCodi;
			built.oficinaCodi = oficinaCodi;
			built.destiCodi = destiCodi;
			built.expedientTipus = expedientTipus;
			built.expedient = expedient;
		}

		public Builder aplicacioCodi(String aplicacioCodi) {
			built.aplicacioCodi = aplicacioCodi;
			return this;
		}

		public Builder aplicacioVersio(String aplicacioVersio) {
			built.aplicacioVersio = aplicacioVersio;
			return this;
		}

		public Builder assumpteCodiCodi(String assumpteCodiCodi) {
			built.assumpteCodiCodi = assumpteCodiCodi;
			return this;
		}

		public Builder assumpteCodiDescripcio(String assumpteCodiDescripcio) {
			built.assumpteCodiDescripcio = assumpteCodiDescripcio;
			return this;
		}

		public Builder assumpteTipusDescripcio(String assumpteTipusDescripcio) {
			built.assumpteTipusDescripcio = assumpteTipusDescripcio;
			return this;
		}

		public Builder docFisicaCodi(String docFisicaCodi) {
			built.docFisicaCodi = docFisicaCodi;
			return this;
		}

		public Builder docFisicaDescripcio(String docFisicaDescripcio) {
			built.docFisicaDescripcio = docFisicaDescripcio;
			return this;
		}

		public Builder entitatDescripcio(String entitatDescripcio) {
			built.entitatDescripcio = entitatDescripcio;
			return this;
		}

		public Builder expedientNumero(String expedientNumero) {
			built.expedientNumero = expedientNumero;
			return this;
		}

		public Builder exposa(String exposa) {
			built.exposa = exposa;
			return this;
		}

		public Builder extracte(String extracte) {
			built.extracte = extracte;
			return this;
		}
		public Builder procedimentCodi(String procedimentCodi) {
			built.procedimentCodi = procedimentCodi;
			return this;
		}

		public Builder idiomaDescripcio(String idiomaDescripcio) {
			built.idiomaDescripcio = idiomaDescripcio;
			return this;
		}

		public Builder llibreDescripcio(String llibreDescripcio) {
			built.llibreDescripcio = llibreDescripcio;
			return this;
		}

		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		
		public Builder oficinaDescripcio(String oficinaDescripcio) {
			built.oficinaDescripcio = oficinaDescripcio;
			return this;
		}

		public Builder origenData(Date origenData) {
			built.origenData = origenData;
			return this;
		}

		public Builder origenRegistreNumero(String origenRegistreNumero) {
			built.origenRegistreNumero = origenRegistreNumero;
			return this;
		}

		public Builder refExterna(String refExterna) {
			built.refExterna = refExterna;
			return this;
		}

		public Builder solicita(String solicita) {
			built.solicita = solicita;
			return this;
		}

		public Builder transportNumero(String transportNumero) {
			built.transportNumero = transportNumero;
			return this;
		}

		public Builder transportTipusCodi(String transportTipusCodi) {
			built.transportTipusCodi = transportTipusCodi;
			return this;
		}

		public Builder transportTipusDescripcio(String transportTipusDescripcio) {
			built.transportTipusDescripcio = transportTipusDescripcio;
			return this;
		}

		public Builder usuariCodi(String usuariCodi) {
			built.usuariCodi = usuariCodi;
			return this;
		}

		public Builder usuariNom(String usuariNom) {
			built.usuariNom = usuariNom;
			return this;
		}

		public Builder destiDescripcio(String destiDescripcio) {
			built.destiDescripcio = destiDescripcio;
			return this;
		}

		public Anotacio build() {
			return built;
		}
	}
	
	

	public String getAplicacioCodi() {
		return aplicacioCodi;
	}

	public void setAplicacioCodi(String aplicacioCodi) {
		this.aplicacioCodi = aplicacioCodi;
	}

	public String getAplicacioVersio() {
		return aplicacioVersio;
	}

	public void setAplicacioVersio(String aplicacioVersio) {
		this.aplicacioVersio = aplicacioVersio;
	}

	public String getAssumpteCodiCodi() {
		return assumpteCodiCodi;
	}

	public void setAssumpteCodiCodi(String assumpteCodiCodi) {
		this.assumpteCodiCodi = assumpteCodiCodi;
	}

	public String getAssumpteCodiDescripcio() {
		return assumpteCodiDescripcio;
	}

	public void setAssumpteCodiDescripcio(String assumpteCodiDescripcio) {
		this.assumpteCodiDescripcio = assumpteCodiDescripcio;
	}

	public String getAssumpteTipusCodi() {
		return assumpteTipusCodi;
	}

	public void setAssumpteTipusCodi(String assumpteTipusCodi) {
		this.assumpteTipusCodi = assumpteTipusCodi;
	}

	public String getAssumpteTipusDescripcio() {
		return assumpteTipusDescripcio;
	}

	public void setAssumpteTipusDescripcio(String assumpteTipusDescripcio) {
		this.assumpteTipusDescripcio = assumpteTipusDescripcio;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getDocFisicaCodi() {
		return docFisicaCodi;
	}

	public void setDocFisicaCodi(String docFisicaCodi) {
		this.docFisicaCodi = docFisicaCodi;
	}

	public String getDocFisicaDescripcio() {
		return docFisicaDescripcio;
	}

	public void setDocFisicaDescripcio(String docFisicaDescripcio) {
		this.docFisicaDescripcio = docFisicaDescripcio;
	}

	public String getEntitatCodi() {
		return entitatCodi;
	}

	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}

	public String getEntitatDescripcio() {
		return entitatDescripcio;
	}

	public void setEntitatDescripcio(String entitatDescripcio) {
		this.entitatDescripcio = entitatDescripcio;
	}

	public String getExpedientNumero() {
		return expedientNumero;
	}

	public void setExpedientNumero(String expedientNumero) {
		this.expedientNumero = expedientNumero;
	}

	public String getExposa() {
		return exposa;
	}

	public void setExposa(String exposa) {
		this.exposa = exposa;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public String getIdiomaCodi() {
		return idiomaCodi;
	}

	public void setIdiomaCodi(String idiomaCodi) {
		this.idiomaCodi = idiomaCodi;
	}

	public String getIdiomaDescripcio() {
		return idiomaDescripcio;
	}
	public void setIdiomaDescripcio(String idiomaDescripcio) {
		this.idiomaDescripcio = idiomaDescripcio;
	}

	public String getExtracte() {
		return extracte;
	}
	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}
	
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	public Expedient getExpedient() {
		return expedient;
	}
	public void setExpedient(Expedient expedient) {
		this.expedient = expedient;
	}
	
	public void setidiomaDescripcio(String idiomaDescripcio) {
		this.idiomaDescripcio = idiomaDescripcio;
	}

	public String getLlibreCodi() {
		return llibreCodi;
	}

	public void setLlibreCodi(String llibreCodi) {
		this.llibreCodi = llibreCodi;
	}

	public String getLlibreDescripcio() {
		return llibreDescripcio;
	}

	public void setLlibreDescripcio(String llibreDescripcio) {
		this.llibreDescripcio = llibreDescripcio;
	}

	public String getObservacions() {
		return observacions;
	}

	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}

	public String getOficinaCodi() {
		return oficinaCodi;
	}

	public void setOficinaCodi(String oficinaCodi) {
		this.oficinaCodi = oficinaCodi;
	}

	public String getOficinaDescripcio() {
		return oficinaDescripcio;
	}

	public void setOficinaDescripcio(String oficinaDescripcio) {
		this.oficinaDescripcio = oficinaDescripcio;
	}

	public Date getOrigenData() {
		return origenData;
	}

	public void setOrigenData(Date origenData) {
		this.origenData = origenData;
	}

	public String getOrigenRegistreNumero() {
		return origenRegistreNumero;
	}

	public void setOrigenRegistreNumero(String origenRegistreNumero) {
		this.origenRegistreNumero = origenRegistreNumero;
	}

	public String getRefExterna() {
		return refExterna;
	}

	public void setRefExterna(String refExterna) {
		this.refExterna = refExterna;
	}

	public String getSolicita() {
		return solicita;
	}

	public void setSolicita(String solicita) {
		this.solicita = solicita;
	}

	public String getTransportNumero() {
		return transportNumero;
	}

	public void setTransportNumero(String transportNumero) {
		this.transportNumero = transportNumero;
	}

	public String getTransportTipusCodi() {
		return transportTipusCodi;
	}

	public void setTransportTipusCodi(String transportTipusCodi) {
		this.transportTipusCodi = transportTipusCodi;
	}

	public String getTransportTipusDescripcio() {
		return transportTipusDescripcio;
	}

	public void setTransportTipusDescripcio(String transportTipusDescripcio) {
		this.transportTipusDescripcio = transportTipusDescripcio;
	}

	public String getUsuariCodi() {
		return usuariCodi;
	}

	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}

	public String getUsuariNom() {
		return usuariNom;
	}

	public void setUsuariNom(String usuariNom) {
		this.usuariNom = usuariNom;
	}

	public String getDestiCodi() {
		return destiCodi;
	}

	public void setDestiCodi(String destiCodi) {
		this.destiCodi = destiCodi;
	}

	public String getDestiDescripcio() {
		return destiDescripcio;
	}

	public void setDestiDescripcio(String destiDescripcio) {
		this.destiDescripcio = destiDescripcio;
	}

	public List<AnotacioInteressat> getInteressats() {
		return interessats;
	}

	public void setInteressats(List<AnotacioInteressat> interessats) {
		this.interessats = interessats;
	}

	public List<AnotacioAnnex> getAnnexos() {
		return annexos;
	}

	public void setAnnexos(List<AnotacioAnnex> annexos) {
		this.annexos = annexos;
	}

	
	public String getDistribucioId() {
		return distribucioId;
	}
	public void setDistribucioId(String distribucioId) {
		this.distribucioId = distribucioId;
	}
	public String getDistribucioClauAcces() {
		return distribucioClauAcces;
	}
	public void setDistribucioClauAcces(String distribucioClauAcces) {
		this.distribucioClauAcces = distribucioClauAcces;
	}
	public AnotacioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(AnotacioEstatEnumDto estat) {
		this.estat = estat;
	}
	public Date getDataRecepcio() {
		return dataRecepcio;
	}
	public void setDataRecepcio(Date dataRecepcio) {
		this.dataRecepcio = dataRecepcio;
	}
	public Date getDataProcessament() {
		return dataProcessament;
	}
	public void setDataProcessament(Date dataProcessament) {
		this.dataProcessament = dataProcessament;
	}
	public String getRebuigMotiu() {
		return rebuigMotiu;
	}
	public void setRebuigMotiu(String rebuigMotiu) {
		this.rebuigMotiu = rebuigMotiu;
	}
	
	/** Per informar a la llista d'anotacions si té error d'annexos. */
	@Transient
	public boolean 	isErrorAnnexos() {
		boolean errorAnnexos = false;
		for (AnotacioAnnex annex : this.getAnnexos()) {
			if (annex.getError() != null) {
				errorAnnexos = true;
				break;
			}
		}
		return errorAnnexos;
	}
	
	private static final long serialVersionUID = 1815997738055924981L;
}
