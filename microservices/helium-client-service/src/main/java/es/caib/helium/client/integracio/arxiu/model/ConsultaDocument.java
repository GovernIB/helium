package es.caib.helium.client.integracio.arxiu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDocument {

    private String versio;
    private boolean ambContingut;
    private boolean isSignat;
    private Long entornId;
}
