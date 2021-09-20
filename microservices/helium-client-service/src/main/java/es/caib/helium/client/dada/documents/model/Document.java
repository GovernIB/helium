package es.caib.helium.client.dada.documents.model;

import es.caib.helium.client.dada.documents.enums.TipusDocument;
import lombok.Data;

@Data
public class Document {

    private String procesId;
    private String tascaId;
    private Long documentStoreId;
    private TipusDocument tipus;
    private String codi;
}
