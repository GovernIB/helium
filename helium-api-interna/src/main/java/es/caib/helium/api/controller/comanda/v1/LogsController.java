package es.caib.helium.api.controller.comanda.v1;

import java.util.List;

import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import es.caib.helium.logic.intf.service.LogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/logs")
@Tag(
	name = "Logs",
	description = "API REST de consulta dels logs de Helium per mostrar a l'aplicació Comanda.")
public class LogsController {

	@Autowired
	private LogService logService;

	/**
	 * Obtenir contingut complet d'un fitxer de log.
	 * Retorna el contingut i detalls del fitxer de log que es troba dins la carpeta de logs del servidor, i que té el nom indicat
	 * @return a {@code List<FitxerInfo>}
	 */
	@GetMapping
	public List<FitxerInfo> llistarFitxers() {
		return logService.llistarFitxers();
	}

	/**
	 * Obtenir les darreres línies d&#39;un fitxer de log
	 * Retorna les darreres linies del fitxer de log indicat per nom. Concretament es retorna el número de línies indicat al paràmetre nLinies.
	 * @param nomFitxer Nom del firxer (required)
	 * @param nLinies Número de línies a recuperar del firxer (required)
	 * @return {@code List<String>}
	 */
	@GetMapping("/{nomFitxer}/linies/{nLinies}")
	public List<String> llegitDarreresLinies(
			@PathVariable String nomFitxer,
			@PathVariable Long nLinies) {
		return logService.llegirDarreresLinies(nomFitxer, nLinies);
	}

	/**
	 * Obtenir contingut complet d&#39;un fitxer de log
	 * Retorna el contingut i detalls del fitxer de log que es troba dins la carpeta de logs del servidor, i que té el nom indicat
	 * @param nom Nom del firxer (required)
	 * @return {@code FitxerContingut}
	 */
	@GetMapping("/{nom}")
	public FitxerContingut getFitxerByNom(@PathVariable String nom) {
		return logService.getFitxerByNom(nom);
	}
}
