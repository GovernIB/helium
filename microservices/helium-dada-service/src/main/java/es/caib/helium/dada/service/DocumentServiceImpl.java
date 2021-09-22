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
public class DocumentServiceImpl implements DocumentService {

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
            if (existeix.isPresent()) {
                existeix.get().setDocumentStoreId(doc.getDocumentStoreId());
                var guardat = documentRepository.save(existeix.get());
                log.info(" Document " + doc + guardat != null ? "" : " no " + " actualitzat " + "correctament");
                return guardat != null;
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

    @Override
    public boolean updateDocument(Document doc) throws Exception {

        try {
            var document = documentRepository.findByProcesIdAndCodi(doc.getProcesId(), doc.getCodi());
            if (!document.isPresent()) {
                log.info("No existeix el document amb procesId " + doc.getProcesId() + " i codi " + doc.getCodi());
                return false;
            }
            document.get().setDocumentStoreId(doc.getDocumentStoreId());
            var updated = documentRepository.save(document.get());
            log.info(" Document " + doc + updated != null ? "" : " no " + " guardat " + "correctament");
            return updated != null;
        } catch (Exception ex) {
            var error = "Error al actualitzar el document " + doc;
            log.error(error, ex);
            throw new DocumentException(error, ex);
        }
    }

    @Override
    public boolean deleteDocument(String procesId, String codi) throws Exception {

        try {
            var deleted = documentRepository.deleteByProcesIdAndCodi(procesId, codi);
            log.info("Document amb procesId " + procesId + " codi " + codi + (deleted == 1 ? " esborrat correctament" : " no existeix"));
            return deleted == 1;
        } catch (Exception ex) {
            var error = "Error esborrant el document amb procesId " + procesId + " codi " + codi;
            log.error(error, ex);
            throw new DocumentException(error, ex);
        }
    }

}
