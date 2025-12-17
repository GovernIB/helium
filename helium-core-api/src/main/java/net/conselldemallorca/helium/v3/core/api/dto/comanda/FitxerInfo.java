package net.conselldemallorca.helium.v3.core.api.dto.comanda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FitxerInfo {
    private String nom;
    // Mida del fitxer en bytes
    private long mida;
    // Data de creació del fitxer", example = "2024-01-15T10:30:00"
    private String dataCreacio;
    // Data de modificació del fitxer", example = "2024-01-20T14:45:00"
    private String dataModificacio;
}
