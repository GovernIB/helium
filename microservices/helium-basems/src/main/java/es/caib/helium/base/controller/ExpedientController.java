package es.caib.helium.base.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.helium.client.expedient.expedient.ExpedientClientService;
import es.caib.helium.client.expedient.expedient.model.ExpedientDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(ExpedientController.API_PATH)
public class ExpedientController {

	public static final String API_PATH = "/api/v1/base/expedients";
	public final ExpedientClientService expedientService;
	
	@GetMapping(value = "/{expedientId}")
    public ResponseEntity<ExpedientDto> getExpedientV1(
            @PathVariable("expedientId") Long expedientId) {
		
		var expedient = expedientService.getExpedientV1(expedientId);
		return new ResponseEntity<ExpedientDto>(expedient, HttpStatus.OK);
	}
}
