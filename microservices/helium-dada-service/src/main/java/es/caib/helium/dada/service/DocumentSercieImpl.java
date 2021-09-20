package es.caib.helium.dada.service;

import es.caib.helium.dada.exception.DocumentException;
import es.caib.helium.dada.model.Document;
import es.caib.helium.dada.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentSercieImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    public List<Document> getDocumentsByProcesId(String procesId) throws Exception {

        try {
            var documents = documentRepository.findByProcesId(procesId);
            log.debug("Consulta de documents correcta per el procesId " + procesId);
            return !documents.isEmpty() ? documents.get() : new ArrayList<>();
        } catch (Exception ex) {
            var error = "Error obtinguent els documents amb procesId " + procesId;
            log.error(error, ex);
            throw new DocumentException(error, ex);
        }
    }

    public boolean guardarDocument(Document doc) throws Exception {

        try {
            var existeix = documentRepository.findByProcesIdAndCodi(doc.getProcesId(), doc.getCodi());
            if (existeix.isPresent() && !existeix.get().isEmpty()) {
                log.info("Ja existeix el document amb procesId " + doc.getProcesId() + " i codi " + doc.getCodi());
                return false;
            }
            var guardat = documentRepository.save(doc);
            log.info(" Document " + doc + guardat != null ? "" : " no " + " guardat " + "correctament");
            return guardat != null;
        } catch (Exception ex) {
            var error = "Error al crear document " + doc;
            log.error(error, ex);
            throw new DocumentException(error, ex);
        }
    }

}
