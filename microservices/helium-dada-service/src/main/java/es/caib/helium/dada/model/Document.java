package es.caib.helium.dada.model;

import es.caib.helium.dada.enums.TipusDocument;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@org.springframework.data.mongodb.core.mapping.Document
public class Document {

    private String procesId;
    private String tascaId;
    private Long documentStoreId;
    private TipusDocument tipus;
    private String codi;
}
