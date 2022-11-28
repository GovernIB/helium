package net.conselldemallorca.helium.v3.core.api.dto.regles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstatReglaValorDto {
    private Long id;
    private Long reglaId;
    private ReglaTipusEnum tipus;
    private String valor;
}
