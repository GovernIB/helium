package es.caib.helium.dada.controller;

import es.caib.helium.dada.model.Document;
import es.caib.helium.dada.service.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(DocumentController.API_PATH)
public class DocumentController {

    public static final String API_PATH = "/api/v1/dades/documents";

    private final DocumentService documentService;

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Void> handleException(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(value = "{procesId}", produces = "application/json")
    public ResponseEntity<List<Document>> getDocumentsByProcesId(@PathVariable("procesId") String procesId) throws Exception {

        var documents = documentService.getDocumentsByProcesId(procesId);

        if (documents == null || documents.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Boolean> guardarDocument(
            @Valid @RequestBody Document document,
            BindingResult errors) throws Exception {

        if (errors.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        var created = documentService.guardarDocument(document);
        if (created) {
            return new ResponseEntity<>(created, HttpStatus.OK);
        }
        return new ResponseEntity<>(created, HttpStatus.CONFLICT);
    }
}

