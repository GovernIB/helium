package es.caib.helium.client.integracio.arxiu.model;

import es.caib.helium.client.integracio.arxiu.enums.ContingutTipus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ContingutArxiu {

    protected String identificador;
    protected String nom;
    protected String descripcio;
    protected ContingutTipus tipus;
    protected String versio;
    protected List<Firma> firmes;
    protected ExpedientMetadades expedientMetadades;
    protected DocumentMetadades documentMetadades;

    public ContingutArxiu(ContingutTipus tipus) {
        this.tipus = tipus;
    }
}
