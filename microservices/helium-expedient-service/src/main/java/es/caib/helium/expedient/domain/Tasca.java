/**
 * 
 */
package es.caib.helium.expedient.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

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
@Table(	name="hel_tasca")
@SequenceGenerator(name = "hel_tasca_gen", sequenceName = "hel_tasca_seq", allocationSize = 1)
public class Tasca implements Persistable<Long> {

	/** Identificador intern auto generat. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hel_tasca_gen")
	private Long id;

	/** Identificador de la tasca que es correspon amb l'identificador de la instància de la tasca */
	@NotEmpty
	@Size(max = 64)
	@Column(name="tasca_id", nullable=false, length = 64)
	private String tascaId;
	
	@ManyToOne(optional=false, cascade={CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name="proces_id", foreignKey = @ForeignKey(name="HEL_TASCA_EXP_FK"))
	private Proces proces;

	@Size(max = 255)
	@Column(name="nom", length=255, nullable=false)
	private String nom;

	@Size(max = 255)
	@Column(name="titol", length=255, nullable=false)
	private String titol;

	@Column(name="agafada")
	private boolean agafada;

	@Column(name="cancelada")
	private boolean cancelada;
	
	@Column(name="suspesa")
	private boolean suspesa;
		
	@Column(name="completada")
	private boolean completada;
	
	@Column(name="assignada")
	private boolean assignada;
	
	@Column(name="marcada_finalitzar")
	private Date marcadaFinalitzar;
	
	@Column(name="error_finalitzacio", length = 1000, nullable = true)
	private String errorFinalitzacio;
	
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

	@Builder.Default
	@Column(name="prioritat")
	private Integer prioritat = 3;
	
	@OneToMany(mappedBy="tasca", cascade={CascadeType.ALL}, fetch = FetchType.LAZY)
	private List<Responsable> responsables;

	@OneToMany(mappedBy="tasca", cascade={CascadeType.ALL}, fetch = FetchType.LAZY)
	private List<Grup> grups;

	@Override
	public boolean isNew() {
		return id != null;
	}
}
