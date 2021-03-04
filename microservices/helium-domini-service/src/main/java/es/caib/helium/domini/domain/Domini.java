/**
 * 
 */
package es.caib.helium.domini.domain;

import es.caib.helium.domini.model.OrigenCredencialsEnum;
import es.caib.helium.domini.model.TipusAuthEnum;
import es.caib.helium.domini.model.TipusDominiEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Objecte de domini que representa un domini per fer consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(	name="hel_domini",
		uniqueConstraints={@UniqueConstraint(columnNames = {"codi", "entorn_id"})},
		indexes = {
			@Index(name = "hel_domini_entorn_i", columnList = "entorn_id"),
			@Index(name = "hel_domini_exptip_i", columnList = "expedient_tipus_id")
		})
@SequenceGenerator(name = "hel_domini_gen", sequenceName = "hel_domini_seq", allocationSize = 1)
public class Domini implements Persistable<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hel_domini_gen")
	@Column(name="id")
	private Long id;

	@NotBlank
	@Size(max = 64)
	@Column(name="codi", length=64, nullable=false)
	private String codi;

	@NotBlank
	@Size(max = 255)
	@Column(name="nom", length=255, nullable=false)
	private String nom;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name="tipus", nullable=false)
	private TipusDominiEnum tipus;

	@Size(max = 255)
	@Column(name="url", length=255)
	private String url;

	@Enumerated(EnumType.ORDINAL)
	@Column(name="tipus_auth")
	private TipusAuthEnum tipusAuth;

	@Enumerated(EnumType.ORDINAL)
	@Column(name="origen_creds")
	private OrigenCredencialsEnum origenCredencials;

	@Size(max = 255)
	@Column(name="usuari")
	private String usuari;

	@Size(max = 255)
	@Column(name="contrasenya")
	private String contrasenya;

	@Size(max = 1024)
	@Column(name="sqlexpr", length=1024)
	private String sql;

	@Size(max = 255)
	@Column(name="jndi_datasource", length=255)
	private String jndiDatasource;

	@Size(max = 255)
	@Column(name="descripcio", length=255)
	private String descripcio;

	@Column(name="cache_segons")
	private int cacheSegons = 0;

	@Column(name="timeout")
	private Integer timeout = 0;

	@Size(max = 255)
	@Column(name="ordre_params", length=255)
	private String ordreParams;

	@NotNull
	@Column(name="entorn_id")
	private Long entorn;

	@Column(name="expedient_tipus_id")
	private Long expedientTipus;

//	@OneToMany(mappedBy="domini")
//	private Set<Camp> camps = new HashSet<Camp>();
//	public void addCamp(Camp camp) {
//		getCamps().add(camp);
//	}

	@Override
	public boolean isNew() {
		return id != null;
	}

	@Transient
	public boolean isDominiIntern() {
		return id == null || id.equals(0L);
	}

	private static final long serialVersionUID = 1L;

}
