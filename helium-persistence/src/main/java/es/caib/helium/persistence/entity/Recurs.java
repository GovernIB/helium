package es.caib.helium.persistence.entity;

import es.caib.helium.commons.config.BaseConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Objecte de domini que representa un recurs associat a un tipus d'expedient o a una instància de procés. Aquests
 * recursos son els que antigament es carregaven amb els fitxers .par al desplegar una instància de procés jBPM.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_recurs")
@Getter
@Setter
@NoArgsConstructor
public class Recurs implements Serializable, GenericEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "gen_registre")
	@TableGenerator(name = "gen_registre", table = "hel_idgen", pkColumnName = "taula", valueColumnName = "valor")
	@Column(name = "id")
	private Long id;
	@Column(name = "nom", length = 255, nullable = false)
	private String nom;
	@Column(name = "classe", nullable = false)
	private boolean classe;
	@Column(name = "handler", nullable = false)
	private boolean handler;
	@Column(name = "data_creacio", nullable = false)
	private Date dataCreacio;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "contingut")
	private byte[] contingut;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "expedient_tipus_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = "hel_recurs_exptip_fk"),
		nullable = false)
	private ExpedientTipus expedientTipus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "definicio_proces_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "recurs_defproc_fk"))
	private DefinicioProces definicioProces;

	public String getNomClasse() {
		if (nom != null) {
			return nom.replace('/', '.').replace(".class", "");
		} else {
			return null;
		}
	}

	@Builder
	public Recurs(
		String nom,
		boolean classe,
		boolean handler,
		Date dataCreacio,
		byte[] contingut,
		ExpedientTipus expedientTipus,
		DefinicioProces definicioProces) {
		this.nom = nom;
		this.classe = classe;
		this.handler = handler;
		this.dataCreacio = dataCreacio;
		this.contingut = contingut;
		this.expedientTipus = expedientTipus;
		this.definicioProces = definicioProces;
	}

}
