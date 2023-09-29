package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entitat per representar la relació N a N entre estats del tipus d'expedient i les accions
 * del tipus d'expedient en el cas de sortida a l'estat per expedients basats en estats en comptes
 * de flux.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="HEL_ESTAT_ACCIO_SORTIDA")
@org.hibernate.annotations.Table(
        appliesTo = "HEL_ESTAT_ACCIO_SORTIDA",
        indexes = {
                @Index(name = "hel_estacc_sort_estat_fk_i", columnNames = {"estat_id"})})
public class EstatAccioSortida implements Serializable, GenericEntity<Long> {

	private static final long serialVersionUID = -4527982774750769578L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="gen_regla")
    @TableGenerator(name="gen_regla", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
    @Column(name="id")
    private Long id;

    @NotNull
    @ManyToOne(optional=false)
    @JoinColumn(name="estat_id")
    @ForeignKey(name="hel_estacc_sort_estat_fk")
    private Estat estat;

    @NotNull
    @ManyToOne(optional=false)
    @JoinColumn(name="accio_id")
    @ForeignKey(name="hel_estacc_sort_accio_fk")
    private Accio accio;

    @NotNull
    @Column(name="ordre")
    private int ordre;

}
