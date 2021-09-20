package es.caib.helium.dada.service;

import es.caib.helium.dada.model.Document;

import java.util.List;

public interface DocumentService {

    List<Document> getDocumentsByProcesId(String procesId) throws Exception;

    boolean guardarDocument(Document doc) throws Exception;

}
