package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.ForeignKey;

import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentEstatEnumDto;


/**
 * Classe del model de dades que representa 
 * els procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "hel_procediment")
public class Procediment implements Serializable, GenericEntity<Long>{

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_proc")
	@TableGenerator(name="gen_proc", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	
	@Column(name = "nom", length = 256)
	private String nom;
	
	@Column(name = "codisia", length = 64)
	private String codiSia;
	
	@Column(name = "estat", length = 20) 
	@Enumerated(EnumType.STRING)
	private ProcedimentEstatEnumDto estat; // Vigente, Extinguido
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY) 
	@JoinColumn(name = "unitat_organitzativa_id")
	@ForeignKey(name = "hel_procediment_unitat_fk")
	private UnitatOrganitzativa unitatOrganitzativa;	
	
	@Column(name = "comu") 
	private boolean comu;
	
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


	public String getCodiSia() {
		return codiSia;
	}


	public void setCodiSia(String codiSia) {
		this.codiSia = codiSia;
	}


	public ProcedimentEstatEnumDto getEstat() {
		return estat;
	}


	public void setEstat(ProcedimentEstatEnumDto estat) {
		this.estat = estat;
	}


	public UnitatOrganitzativa getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}


	public void setUnitatOrganitzativa(UnitatOrganitzativa unitatOrganitzativa) {
		this.unitatOrganitzativa = unitatOrganitzativa;
	}

	
	
	public boolean isComu() {
		return comu;
	}
	public void setComu(boolean comu) {
		this.comu = comu;
	}
	public void update(
			String codi, 
			String nom, 
			String codiSia, 
			ProcedimentEstatEnumDto estat, 
			boolean comu,
			UnitatOrganitzativa unitatOrganitzativa) {
		this.codi = codi;
		this.nom = nom;
		this.codiSia = codiSia;
		this.estat = estat;
		this.comu = comu;
		this.unitatOrganitzativa = unitatOrganitzativa;
	}
	


	public static Builder getBuilder(
			String codi, 
			String nom, 
			String codiSia, 
			ProcedimentEstatEnumDto estat, 
			boolean comu,
			UnitatOrganitzativa unitatOrganitzativa) {
		return new Builder(
				codi, 
				nom, 
				codiSia, 
				estat,
				comu,
				unitatOrganitzativa);
	}
	
	public static class Builder {
		Procediment built;
		Builder(
				String codi, 
				String nom, 
				String codiSia, 
				ProcedimentEstatEnumDto estat, 
				boolean comu,
				UnitatOrganitzativa unitatOrganitzativa) {
			built = new Procediment();
			built.codi = codi;
			built.nom = nom;
			built.codiSia = codiSia;
			built.estat = estat;
			built.comu = comu;
			built.unitatOrganitzativa = unitatOrganitzativa;
		}
		
		public Procediment built() {
			return built;
		}
	}


	private static final long serialVersionUID = 5429596550889089134L;
}
