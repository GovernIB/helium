/**
 * 
 */
package es.caib.helium.expedient.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.domain.Persistable;

import es.caib.helium.expedient.model.ExpedientEstatTipusEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Objecte d'expedient que representa un expedient pel llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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

	@Size(max = 255)
	@Column(name="titol", length=255, nullable=true)
	private String titol;

	@NotNull
	@Column(name="data_inici", nullable=false)
	private Date dataInici;
	
	@Column(name="data_fi", nullable=true)
	private Date dataFi;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name="estat_tipus", length = 16, nullable = false)
	private ExpedientEstatTipusEnum estatTipus;
	
	@Column(name="estat_id")
	private Long estatId;

	@Column(name="aturat", nullable=false)
	private boolean aturat;
	
	@Size(max = 1024)
	@Column(name="info_aturat", nullable=true)
	private String infoAturat;

	@Column(name="anulat", nullable=false)
	private boolean anulat;
	
	@Size(max = 255)
	@Column(name="comentari_aturat", nullable=true)
	private String comentariAnulat;
	
	@Column(name="alertes_totals", nullable=false)
	private long alertesTotals;

	@Column(name="alertes_pendents", nullable=false)
	private long alertesPendents;

	@Column(name="amb_errors", nullable=false)
	private boolean ambErrors;

	@Override
	public boolean isNew() {
		return id != null;
	}
}
