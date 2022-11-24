package net.conselldemallorca.helium.v3.core.api.dto.regles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstatReglaDto {
    private Long id;
    private int ordre;
    private String nom;
    private Long expedientTipusId;
    private Long estatId;
    private QuiEnum qui;
    private Set<String> quiValor;
    private QueEnum que;
    private Set<String> queValor;
    private AccioEnum accio;
}
