/**
 * 
 */
package es.caib.helium.expedient.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.domain.Persistable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Objecte tasca que representa la informació d'una tasca pel llistat de tasques actives. 
 * Les tasques pertanyen a un expedient, per tant es relaciona amb l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(	name="hel_tasca",
		indexes = {
			@Index(name = "hel_tasca_exp_i", columnList = "expedient_id")
		})
public class Tasca implements Persistable<Long> {

	/** Identificador de la tasca que es correspon amb l'identificador a Helium de la instància de la tasca */
	@Id
	@NotNull
	@Column(name="id", nullable=false)
	private Long id;
	
	@ManyToOne(optional=false, cascade={CascadeType.ALL})
	@JoinColumn(name="expedient_id")
	// FK HEL_TASCA_EXP_FK
	private Expedient expedient;

	@Size(max = 255)
	@Column(name="nom", length=255, nullable=false)
	private String nom;

	@Size(max = 255)
	@Column(name="titol", length=255, nullable=false)
	private String titol;

	@Column(name="agafada")
	private boolean afagada;

	@Column(name="cancelada")
	private boolean cancelada;
	
	@Column(name="suspesa")
	private boolean suspesa;
		
	@Column(name="completada")
	private boolean completada;
	
	@Column(name="assignada")
	private boolean assignada;
	
	@Column(name="marcada_finalitzar")
	private boolean marcadaFinalitzar;
	
	@Column(name="error_finalitzacio")
	private boolean errorFinalitzacio;
	
	@Column(name="data_fins")
	private Date dataFins;

	@Column(name="data_fi")
	private Date dataFi;

	@Column(name="inici_finalitzacio")
	private Date iniciFinalitzacio;

	@NotNull
	@Column(name="data_creacio", nullable = false)
	private Date dataCreacio;
	
	@Size(max = 255)
	@Column(name="usuari_assignat")
	private String usuariAssignat;

	@Size(max = 255)
	@Column(name="grup_assignat")
	private String grupAssignat;
	
	@OneToMany(mappedBy="tasca", cascade={CascadeType.ALL})
	private List<Responsable> responsables;
	
	@Override
	public boolean isNew() {
		return id != null;
	}
}
