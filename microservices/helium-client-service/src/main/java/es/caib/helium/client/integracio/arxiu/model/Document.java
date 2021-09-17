package es.caib.helium.client.integracio.arxiu.model;

import es.caib.helium.client.integracio.arxiu.enums.DocumentEstat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document extends ContingutArxiu {

    private DocumentEstat estat;
    private DocumentContingut contingut;
}
