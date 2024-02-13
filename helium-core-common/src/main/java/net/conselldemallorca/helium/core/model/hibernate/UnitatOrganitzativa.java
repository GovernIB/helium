/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import net.conselldemallorca.helium.v3.core.api.dto.TipusTransicioEnumDto;


/**
 * Classe del model de dades que representa una alerta d'error en segn pla.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="hel_unitat_organitzativa")
public class UnitatOrganitzativa implements Serializable, GenericEntity<Long> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_unitat_organitzativa")
	@TableGenerator(name="gen_unitat_organitzativa", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;
	
	@JoinTable(name = "hel_uo_sinc_rel", joinColumns = {
	@JoinColumn(name = "antiga_uo", referencedColumnName = "id", nullable = false) }, inverseJoinColumns = {
	@JoinColumn(name = "nova_uo", referencedColumnName = "id", nullable = false) })
	@ManyToMany
	private List<UnitatOrganitzativa> noves = new ArrayList<UnitatOrganitzativa>();
			
	@ManyToMany(mappedBy = "noves")
	private List<UnitatOrganitzativa> antigues = new ArrayList<UnitatOrganitzativa>();
		
	@Column(name = "tipus_transicio", length = 12)
	@Enumerated(EnumType.STRING)
	private TipusTransicioEnumDto tipusTransicio;


	@Column(name = "codi", length = 9, nullable = false)
	private String codi;
	@Column(name = "denominacio", length = 300, nullable = false)
	private String denominacio;
	@Column(name = "nif_cif", length = 9)
	private String nifCif;
	@Column(name = "codi_unitat_superior", length = 9)
	private String codiUnitatSuperior;

	@Column(name = "codi_unitat_arrel", length = 9) 
	private String codiUnitatArrel;
				
	@Column(name = "data_creacio_oficial") 
	private Date dataCreacioOficial;
	@Column(name = "data_supressio_oficial") 
	private Date dataSupressioOficial;
	@Column(name = "data_extincio_funcional") 
	private Date dataExtincioFuncional;
	@Column(name = "data_anulacio") 
	private Date dataAnulacio;
	@Column(name = "estat", length = 1) 
	private String estat; // V: Vigente, E: Extinguido, A: Anulado, T: Transitorio

	@Column(name = "codi_pais", length = 3) 
	private String codiPais;
	@Column(name = "codi_comunitat", length = 1) 
	private String codiComunitat;
	@Column(name = "codi_provincia", length = 1) 
	private String codiProvincia;
	@Column(name = "codi_postal", length = 5) 
	private String codiPostal;
	@Column(name = "nom_localitat", length = 50) 
	private String nomLocalitat;
	@Column(name = "localitat", length = 40) 
	private String localitat;

	@Column(name = "adressa", length = 70) 
	private String adressa;
	@Column(name = "tipus_via") 
	private Long tipusVia;
	@Column(name = "nom_via", length = 200) 
	private String nomVia;
	@Column(name = "num_via", length = 100) 
	private String numVia;

			
			
	public List<UnitatOrganitzativa> getNoves() {
			return noves;
	}
	public TipusTransicioEnumDto getTipusTransicio() {
		return tipusTransicio;
	}

	public void updateTipusTransicio(TipusTransicioEnumDto tipusTransicio) {
		this.tipusTransicio = tipusTransicio;
	}

	public void addNova(UnitatOrganitzativa nova) {
		noves.add(nova);
	}

	public void addAntiga(UnitatOrganitzativa antiga) {
		antigues.add(antiga);
	}

	public List<UnitatOrganitzativa> getAntigues() {
		return antigues;
	}

	public String getCodi() {
		return codi;
	}

	public String getDenominacio() {
		return denominacio;
	}

	public String getNifCif() {
			return nifCif;
	}

	public String getCodiUnitatSuperior() {
		return codiUnitatSuperior;
	}

	public String getCodiUnitatArrel() {
		return codiUnitatArrel;
	}
				
	public Date getDataCreacioOficial() {
		return dataCreacioOficial;
	}

	public Date getDataSupressioOficial() {
		return dataSupressioOficial;
	}

	public Date getDataExtincioFuncional() {
		return dataExtincioFuncional;
	}

	public Date getDataAnulacio() {
		return dataAnulacio;
	}

	public String getEstat() {
		return estat;
	}

	public String getCodiPais() {
		return codiPais;
	}

	public String getCodiComunitat() {
		return codiComunitat;
	}

	public String getCodiProvincia() {
		return codiProvincia;
	}

	public String getCodiPostal() {
		return codiPostal;
	}

	public String getNomLocalitat() {
		return nomLocalitat;
	}

	public String getLocalitat() {
		return localitat;
	}

	public String getAdressa() {
		return adressa;
	}

	public Long getTipusVia() {
		return tipusVia;
	}
	
	public String getNomVia() {
		return nomVia;
	}

	public String getNumVia() {
		return numVia;
	}
			
	public String getCodiAndNom() {
		return this.codi + " - " + this.denominacio + "";
	}
			
	public void update(
			String codi,
			String denominacio,
			String nifCif,
			String estat, 
			String codiUnitatSuperior,
			String codiUnitatArrel,
			String codiPais,
			String codiComunitat,
			String codiProvincia,
			String codiPostal,
			String localitat,
			Long tipusVia,
			String nomVia,
			String numVia) {
				this.codi = codi;
				this.denominacio = denominacio;
				this.nifCif = nifCif;
				this.estat = estat;
				this.codiUnitatSuperior = codiUnitatSuperior;
				this.codiUnitatArrel = codiUnitatArrel;
				this.codiPais = codiPais;
				this.codiComunitat = codiComunitat;
				this.codiProvincia = codiProvincia;
				this.codiPostal = codiPostal;
				this.localitat = localitat;
				this.tipusVia = tipusVia;
				this.nomVia = nomVia;
				this.numVia = numVia;
		}
			
		public void update(
			String codi,
			String denominacio,
			String nifCif,
			String codiUnitatSuperior,
			String codiUnitatArrel,
			Date dataCreacioOficial,
			Date dataSupressioOficial,
			Date dataExtincioFuncional,
			Date dataAnulacio,
			String estat, 
			String codiPais,
			String codiComunitat,
			String codiProvincia,
			String codiPostal,
			String nomLocalitat,
			String localitat,
			String adressa,
			Long tipusVia,
			String nomVia,
			String numVia) {
				this.codi = codi;
				this.denominacio = denominacio;
				this.nifCif = nifCif;
				this.codiUnitatSuperior = codiUnitatSuperior;
				this.codiUnitatArrel = codiUnitatArrel;
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
				this.localitat = localitat;
				this.adressa = adressa;
				this.tipusVia = tipusVia;
				this.nomVia = nomVia;
				this.numVia = numVia;
			}
			
		public static Builder getBuilder(
				String codi,
				String denominacio,
				String nifCif,
				String codiUnitatSuperior,
				String codiUnitatArrel,
				Date dataCreacioOficial,
				Date dataSupressioOficial,
				Date dataExtincioFuncional,
				Date dataAnulacio,
				String estat,
				String codiPais,
				String codiComunitat,
				String codiProvincia,
				String codiPostal,
				String nomLocalitat,
				String localitat,
				String adressa,
				Long tipusVia,
				String nomVia,
				String numVia) {
			return new Builder(
					 codi,
					 denominacio,
					 nifCif,
					 codiUnitatSuperior,
					 codiUnitatArrel,
					 dataCreacioOficial,
					 dataSupressioOficial,
					 dataExtincioFuncional,
					 dataAnulacio,
					 estat,
					 codiPais,
					 codiComunitat,
					 codiProvincia,
					 codiPostal,
					 nomLocalitat,
					 localitat,
					 adressa,
					 tipusVia,
					 nomVia,
					 numVia);
		}
		
		
	public static class Builder {
		UnitatOrganitzativa built;
		Builder(
				String codi,
				String denominacio,
				String nifCif,
				String codiUnitatSuperior,
				String codiUnitatArrel,
				Date dataCreacioOficial,
				Date dataSupressioOficial,
				Date dataExtincioFuncional,
				Date dataAnulacio,
				String estat,
				String codiPais,
				String codiComunitat,
				String codiProvincia,
				String codiPostal,
				String nomLocalitat,
				String localitat,
				String adressa,
				Long tipusVia,
				String nomVia,
				String numVia
				) {
			built = new UnitatOrganitzativa();
			built.codi = codi;
			built.denominacio = denominacio;
			built.nifCif = nifCif;
			built.codiUnitatSuperior = codiUnitatSuperior;
			built.codiUnitatArrel = codiUnitatArrel;
			built.dataCreacioOficial = dataCreacioOficial;
			built.dataSupressioOficial = dataSupressioOficial;
			built.dataExtincioFuncional = dataExtincioFuncional;
			built.dataAnulacio = dataAnulacio;
			built.estat = estat;
			built.codiPais = codiPais;
			built.codiComunitat = codiComunitat;
			built.codiProvincia = codiProvincia;
			built.codiPostal = codiPostal;
			built.nomLocalitat = nomLocalitat;
			built.localitat = localitat;
			built.adressa = adressa;
			built.tipusVia = tipusVia;
			built.nomVia = nomVia;
			built.numVia = numVia;
		}
				
		public UnitatOrganitzativa build() {
				return built;
		}
	}
			
			
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
