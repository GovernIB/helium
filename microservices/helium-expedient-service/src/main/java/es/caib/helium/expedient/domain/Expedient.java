/**
 * 
 */
package es.caib.helium.expedient.domain;

import es.caib.helium.expedient.model.ExpedientEstatTipusEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * Objecte d'expedient que representa un expedient pel llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(	name="hel_expedient",
		indexes = {
			@Index(name = "hel_expedient_entorn_i", columnList = "entorn_id"),
			@Index(name = "hel_expedient_exptip_i", columnList = "expedient_tipus_id")
		})
public class Expedient implements Persistable<Long> {

	/** Identificador de l'expedient que es correspon amb l'identificador a Helium */
	@Id
	@NotNull
	@Column(name="id", nullable=false)
	private Long id;

	@NotNull
	@Column(name="entorn_id", nullable=false)
	private Long entornId;

	@NotNull
	@Column(name="expedient_tipus_id", nullable=false)
	private Long expedientTipusId;

	@NotBlank
	@NotEmpty
	@Size(max = 255)
	@Column(name="process_instance_id", nullable=false)
	private String processInstanceId;

	@NotBlank
	@Size(max = 64)
	@Column(name="numero", length=64, nullable=false)
	private String numero;

	@NotBlank
	@Size(max = 64)
	@Column(name="numero_default", length=64, nullable=false)
	private String numeroDefault;

	
	@Size(max = 255)
	@Column(name="titol", length=255)
	private String titol;

	@NotNull
	@Column(name="data_inici", nullable=false)
	private Date dataInici;
	
	@Column(name="data_fi")
	private Date dataFi;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name="estat_tipus", length = 16, nullable = false)
	private ExpedientEstatTipusEnum estatTipus;
	
	@Column(name="estat_id")
	private Long estatId;

	@Column(name="aturat")
	private boolean aturat;
	
	@Size(max = 1024)
	@Column(name="info_aturat")
	private String infoAturat;

	@Column(name="anulat")
	private boolean anulat;
	
	@Size(max = 255)
	@Column(name="comentari_anulat")
	private String comentariAnulat;
	
	@Column(name="alertes_totals")
	private Long alertesTotals;

	@Column(name="alertes_pendents")
	private Long alertesPendents;

	@Column(name="amb_errors")
	private boolean ambErrors;

	@OneToMany(mappedBy="expedient", cascade={CascadeType.ALL}, fetch = FetchType.LAZY)
	private List<Proces> processos;
	
	@Override
	public boolean isNew() {
		return id != null;
	}
}