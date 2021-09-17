package es.caib.helium.client.integracio.arxiu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentContingut {

    private byte[] contingut;
    private long tamany = -1L;
    private String tipusMime;
    private String arxiuNom;
}
