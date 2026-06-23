package es.caib.helium.api.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.helium.logic.intf.service.AnotacioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController("/rest/distribucio")
@Tag(name = "Integració distribució - HELIUM", description = "Recepció d'anotacions de registre")
public class DistribucioRestController {

	@Value("${es.caib.helium.anotacions.pendents.comprovar.intents:5}")
	private Integer maxConsultaIntents;

	@Autowired
	private AnotacioService anotacioService;

	@PostMapping("/comunicarAnotacionsPendents")
	public ResponseEntity<String> event(@RequestBody List<AnotacioRegistreId> ids) {
		log.info("Rebuda la comunicació de " + ids.size() + "anotacions de registre de Distribucio. Inici del processament.");
		try {
			anotacioService.comunicarAnotacionsPendents(ids);
		} catch(Exception e) {
			log.error("Error en el tractament de la comunicació d'anotacions pendents: " + e.getMessage());
			return new ResponseEntity<String>("Error processant les anotacions: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}
}
