package es.caib.helium.client.dada.documents;


import es.caib.helium.client.dada.documents.model.Document;

import java.util.List;

public interface DocumentClient {

    List<Document> getDocumentsByProcesId(String procesId) throws Exception;

    boolean guardarDocument(Document doc) throws Exception;
}
