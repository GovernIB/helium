package es.caib.helium.base.controller;

import es.caib.helium.client.domini.domini.DominiClient;
import es.caib.helium.client.domini.domini.model.ConsultaDominisDades;
import es.caib.helium.client.domini.entorn.model.DominiDto;
import es.caib.helium.client.model.PagedList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(DominiController.API_PATH)
public class DominiController {

    public static final String API_PATH = "/api/v1/base/domini";
    private final DominiClient dominiClient;
    
	@GetMapping(value = "consulta/resultats/paginat", consumes = "application/json")
	public ResponseEntity<PagedList<DominiDto>> consultaResultats(
			@RequestParam("entornId") Long entornId,
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
			@RequestParam(value = "expedientTipusPareId", required = false) Long expedientTipusPareId,
			@RequestParam(value = "filtre", required = false) String filtre,
			@RequestParam("page") Integer page,
			@RequestParam("size") Integer size,
			@RequestParam("sort") List<String> sort) throws Exception {

		ConsultaDominisDades consultaDominisDades = ConsultaDominisDades.builder()
				.entornId(entornId)
				.expedientTipusId(expedientTipusId)
				.expedientTipusPareId(expedientTipusPareId)
				.filtre(filtre)
				.page(page)
				.size(size)
				.sort(sort)
				.build();
		var resultat = dominiClient.listDominisV1(consultaDominisDades);
		return new ResponseEntity<>(resultat, HttpStatus.OK);
	}

}
