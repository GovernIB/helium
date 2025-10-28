package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@Table(name = "HEL_EXPLOT_DIM",
	uniqueConstraints = {@UniqueConstraint(name = "HEL_EXPLOT_DIM_UK", columnNames = {"UNITAT_ORGANITZATIVA_ID", "ENTORN_ID", "TIPUS_ID"})})
public class ExplotacioDimensio implements Serializable, GenericEntity<Long> {

	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_explot_dim")
	@TableGenerator(name="gen_explot_dim", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	private Long id;

	@Column(name = "UNITAT_ORGANITZATIVA_ID")
	private Long unitatOrganitzativaId;

	@Column(name = "UNITAT_ORGANITZATIVA_CODI", length = 9)
	private String unitatOrganitzativaCodi;

	@Column(name = "ENTORN_ID", nullable = false)
	private Long entornId;

	@Column(name = "ENTORN_CODI", nullable = false, length = 64)
	private String entornCodi;

	@Column(name = "TIPUS_ID", nullable = false)
	private Long tipusId;

	@Column(name = "TIPUS_CODI", nullable = false, length = 64)
	private String tipusCodi;

	private static final long serialVersionUID = -4611558539835700615L;
}
