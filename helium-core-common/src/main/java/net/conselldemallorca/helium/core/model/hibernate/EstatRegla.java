package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.conselldemallorca.helium.v3.core.api.dto.regles.AccioEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QueEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QuiEnum;

@Data
//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="HEL_ESTAT_REGLA",
        uniqueConstraints={@UniqueConstraint(columnNames={"nom", "estat_id"})})
@org.hibernate.annotations.Table(
        appliesTo = "HEL_ESTAT_REGLA",
        indexes = {
                @Index(name = "hel_regla_entorn_fk_i", columnNames = {"entorn_id"}),
                @Index(name = "hel_regla_exptipus_fk_i", columnNames = {"expedient_tipus_id"}),
                @Index(name = "hel_regla_estat_fk_i", columnNames = {"estat_id"})})
public class EstatRegla implements Serializable, GenericEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="gen_regla")
    @TableGenerator(name="gen_regla", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
    @Column(name="id")
    private Long id;

    @Column(name="nom", length = 255)
    private String nom;

    @NotNull
    @ManyToOne(optional=false)
    @JoinColumn(name="expedient_tipus_id")
    @ForeignKey(name="hel_regla_exptipus_fk")
    private ExpedientTipus expedientTipus;

    @NotNull
    @ManyToOne(optional=false)
    @JoinColumn(name="estat_id")
    @ForeignKey(name="hel_regla_estat_fk")
    private Estat estat;

    @NotNull
    @ManyToOne(optional=false)
    @JoinColumn(name="entorn_id")
    @ForeignKey(name="hel_regla_entorn_fk")
    private Entorn entorn;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name="qui")
    private QuiEnum qui;

    @ElementCollection
    @CollectionTable(name="HEL_ESTAT_REGLA_VALOR_QUI", joinColumns=@JoinColumn(name="regla_id"))
    @Column(name = "valor")
    private Set<String> quiValor;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name="que")
    private QueEnum que;

    @ElementCollection
    @CollectionTable(name="HEL_ESTAT_REGLA_VALOR_QUE", joinColumns=@JoinColumn(name="regla_id"))
    @Column(name = "valor")
    private Set<String> queValor;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name="accio")
    private AccioEnum accio;

    @NotNull
    @Column(name="ordre")
    private int ordre;

}
