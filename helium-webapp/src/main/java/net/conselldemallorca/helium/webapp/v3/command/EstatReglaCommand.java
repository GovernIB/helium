package net.conselldemallorca.helium.webapp.v3.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.conselldemallorca.helium.v3.core.api.dto.regles.AccioEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QueEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QuiEnum;
import net.conselldemallorca.helium.webapp.v3.validator.EstatRegla;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

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
    @NotNull
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
