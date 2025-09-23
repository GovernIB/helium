package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HEL_EXPLOT_FET")
public class ExplotacioFets implements Serializable, GenericEntity<Long> {
	
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_explot_fet")
	@TableGenerator(name="gen_explot_fet", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	private Long id;

	@Column(name = "EXP_OBE", nullable = false)
	private Long expedientsOberts;

	@Column(name = "EXP_TAN", nullable = false)
	private Long expedientsTancats;

	@Column(name = "EXP_ANUL", nullable = false)
	private Long expedientsAnulats;

	@Column(name = "EXP_NO_ANUL", nullable = false)
	private Long expedientsNoAnulats;
	
	@Column(name = "EXP_TOTAL", nullable = false)
	private Long expedientsTotals;

	@Column(name = "EXP_ARX", nullable = false)
	private Long expedientsArxiu;

	@Column(name = "TAS_PEN", nullable = false)
	private Long tasquesPendents;

	@Column(name = "TAS_FIN", nullable = false)
	private Long tasquesFinalitzades;

	@Column(name = "AN_PEN", nullable = false)
	private Long anotacionPendents;

	@Column(name = "AN_PROC", nullable = false)
	private Long anotacionProcessades;

	@Column(name = "CO_PINBAL", nullable = false)
	private Long peticionsPinbal;

	@Column(name = "CO_NOTIB", nullable = false)
	private Long peticionsNotib;

	@Column(name = "CO_PORTAFIB", nullable = false)
	private Long peticionsPortafib;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DIMENSIO_ID", nullable = false)
	private ExplotacioDimensio dimensio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TEMPS_ID", nullable = false)
	private ExplotacioTemps temps;

	private static final long serialVersionUID = -559122472996151413L;
}
