package es.caib.helium.client.dada.documents;


import es.caib.helium.client.dada.documents.model.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentClientImpl implements DocumentClient {

    private final DocumentFeignClient documentFeignClient;

    private final String missatgeLog = "Cridant Document Service - ";

    @Override
    public List<Document> getDocumentsByProcesId(String procesId) throws Exception {

        log.debug(missatgeLog + "getDocumentsByProcesId - procesIdId: " + procesId);
        var response = documentFeignClient.getDocumentsByProcesId(procesId);
        return response != null ? response.getBody() : new ArrayList<>();
    }

    @Override
    public boolean guardarDocument(Document doc) throws Exception {

        log.debug(missatgeLog + " creant expedient: " + doc);
        var response = documentFeignClient.guardarDocument(doc);
        return response != null ? response.getBody() : false;
    }
}
