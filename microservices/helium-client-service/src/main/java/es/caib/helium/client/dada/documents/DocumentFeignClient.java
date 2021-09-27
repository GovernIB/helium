package es.caib.helium.client.dada.documents;


import es.caib.helium.client.dada.documents.model.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public interface DocumentFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = DocumentMsApiPath.GET_DOCUMENTS_BY_PROCES_ID)
    ResponseEntity<List<Document>> getDocumentsByProcesId(@PathVariable("procesId") String procesId);

    @RequestMapping(method = RequestMethod.POST, value = DocumentMsApiPath.GUARDAR_DOCUMENT)
    ResponseEntity<Boolean> guardarDocument(@RequestBody Document doc);

    @RequestMapping(method = RequestMethod.DELETE, value = DocumentMsApiPath.DELETE_DOCUMENT)
    ResponseEntity<Void> deleteDocument(@PathVariable("procesId") String procesId, @PathVariable("codi") String codi);
 }
