package es.caib.helium.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import es.caib.helium.commons.dto.regles.ReglaTipusEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="HEL_ESTAT_REGLA_VALOR")
@org.hibernate.annotations.Table(
        appliesTo = "HEL_ESTAT_REGLA_VALOR",
        indexes = {
                @Index(name = "hel_regla_valor_fk_i", columnNames = {"regla_id"})})
public class EstatReglaValor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="gen_regla_valor")
    @TableGenerator(name="gen_regla_valor", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
    @Column(name="id")
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="regla_id")
    @ForeignKey(name="hel_regla_valor_fk")
    private EstatRegla regla;

    @Column(name="tipus")
    @Enumerated(EnumType.STRING)
    private ReglaTipusEnum tipus;

    @Column(name="valor")
    private String valor;

}
