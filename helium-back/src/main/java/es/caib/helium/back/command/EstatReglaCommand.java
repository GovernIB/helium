package es.caib.helium.back.command;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.helium.back.validator.EstatRegla;
import es.caib.helium.commons.dto.regles.AccioEnum;
import es.caib.helium.commons.dto.regles.QueEnum;
import es.caib.helium.commons.dto.regles.QuiEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EstatRegla
public class EstatReglaCommand {
    private Long id;
    @Size(max = 255)
    @NotNull
    private String nom;
    @NotNull
    private Long expedientTipusId;
    private Long estatId;
    @NotNull
    private QuiEnum qui;
    private List<String> quiValor;
    @NotNull
    private QueEnum que;
    private List<String> queValor;
    @NotNull
    private AccioEnum accio;
}
