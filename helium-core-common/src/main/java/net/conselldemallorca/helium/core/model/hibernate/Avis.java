/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import net.conselldemallorca.helium.v3.core.api.dto.AvisNivellEnumDto;


/**
 * Classe del model de dades que representa una alerta d'error en segn pla.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="hel_avis")
public class Avis implements Serializable, GenericEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_avis")
	@TableGenerator(name="gen_avis", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;
	@Column(name = "assumpte", length = 256, nullable = false)
	private String assumpte;
	@Column(name = "missatge", length = 2048, nullable = false)
	private String missatge;
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inici", nullable = false)
	private Date dataInici;
	@Temporal(TemporalType.DATE)
	@Column(name = "data_final", nullable = false)
	private Date dataFinal;
	@Column(name = "hora_inici", length = 5, nullable = true)
	private String horaInici;
	@Column(name = "hora_fi", length = 5, nullable = true)
	private String horaFi;
	@Column(name = "actiu", nullable = false)
	private Boolean actiu;
	@Column(name = "avis_nivell", length = 2048, nullable = false)
	@Enumerated(EnumType.STRING)
	private AvisNivellEnumDto avisNivell;
	
	
	public void update(
			String assumpte,
			String missatge,
			Date dataInici,
			Date dataFinal,
			String horaInici,
			String horaFi,
			AvisNivellEnumDto avisNivell) {
		this.assumpte = assumpte;
		this.missatge = missatge;
		this.dataInici = dataInici;
		this.dataFinal = dataFinal;
		this.horaInici = horaInici;
		this.horaFi = horaFi;
		this.avisNivell = avisNivell;
	}
	
	public void updateActiva(
			Boolean actiu) {
		this.actiu = actiu;
	}
	

	public static Builder getBuilder(
			String assumpte,
			String missatge,
			Date dataInici,
			Date dataFinal,
			String horaInici,
			String horaFi,
			AvisNivellEnumDto avisNivell) {
		return new Builder(
				assumpte,
				missatge,
				dataInici,
				dataFinal,
				horaInici,
				horaFi,
				avisNivell);
	}


	public static class Builder {
		Avis built;
		Builder(
				String assumpte,
				String missatge,
				Date dataInici,
				Date dataFinal,
				String horaInici,
				String horaFi,
				AvisNivellEnumDto avisNivell) {
			built = new Avis();
			built.assumpte = assumpte;
			built.missatge = missatge;
			built.dataInici = dataInici;
			built.dataFinal = dataFinal;
			built.horaInici = horaInici;
			built.horaFi = horaFi;
			built.actiu = true;
			built.avisNivell = avisNivell;
		}
		public Avis build() {
			return built;
		}
	}
	
	public String getAssumpte() {
		return assumpte;
	}

	public String getMissatge() {
		return missatge;
	}

	public Date getDataInici() {
		return dataInici;
	}

	public Date getDataFinal() {
		return dataFinal;
	}
	
	public String getHoraInici() {
		return horaInici;
	}

	public String getHoraFi() {
		return horaFi;
	}

	public Boolean getActiu() {
		return actiu;
	}

	public AvisNivellEnumDto getAvisNivell() {
		return avisNivell;
	}	
	
	public Long getId() {
		return id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	private static final long serialVersionUID = -2299453443943600172L;

}
