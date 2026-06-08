package es.caib.helium.api.controller.rest;

import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;
import es.caib.helium.commons.dto.AnotacioDto;
import es.caib.helium.commons.dto.AnotacioEstatEnumDto;
import es.caib.helium.commons.dto.IntegracioAccioTipusEnumDto;
import es.caib.helium.commons.dto.IntegracioParametreDto;
import es.caib.helium.commons.utils.GlobalProperties;
import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.helper.DistribucioHelper;
import es.caib.helium.logic.helper.MonitorIntegracioHelper;
import es.caib.helium.logic.intf.service.AnotacioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController("/rest/distribucio")
@Tag(name = "Integració distribució - HELIUM", description = "Recepció d'anotacions de registre")
public class DistribucioRestController {

	@Value("${es.caib.helium.anotacions.pendents.comprovar.intents:5}")
	private Integer maxConsultaIntents;

	@Autowired
	private MonitorIntegracioHelper monitorIntegracioHelper;
	@Autowired
	private AnotacioService anotacioService;
	@Autowired
	private DistribucioHelper distribucioHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@PostMapping("/comunicarAnotacionsPendents")
	public ResponseEntity<String> event(@RequestBody List<AnotacioRegistreId> anotacionsDistribucio) {
		log.info("Rebuda la comunicació de " + anotacionsDistribucio.size() + "anotacions de registre de Distribucio. Inici del processament.");
		monitorIntegracioHelper.addAccioOk(
			MonitorIntegracioHelper.INTCODI_DISTRIBUCIO,
			"Rebuda petició de " + (anotacionsDistribucio != null ? anotacionsDistribucio.size() : "null") + " anotacions de registre de Distribucio",
			IntegracioAccioTipusEnumDto.RECEPCIO,
			0,
			new IntegracioParametreDto("ids", ToStringBuilder.reflectionToString(anotacionsDistribucio)));
		// Iteram damunt totes les anotacions rebudes
		for(AnotacioRegistreId id : anotacionsDistribucio) {
			try {
				// Cercam si la anotacio ja existeix al sistema
				List<AnotacioDto> anotacions = anotacioService.findByDistribucioIdAndClauAcces(id.getIndetificador(), id.getClauAcces());

				if(anotacions.isEmpty()) {
					// Guarda la informació mínima a la taula d'anotacions per a que la tasca en segon pla la consulti i processi
					distribucioHelper.encuarAnotacio(id);
//					log.info("Anotació " + id.getIndetificador() + " encuada com a pendent de consulta");
				} else {
//					if (anotacions.size() > 1)
//						log.warn("S'han trobat " + anotacions.size() + " peticions d'anotació per l'identificador de Distribucio " + id.getIndetificador());

					AnotacioDto anotacio = anotacions.get(0);
					String msg = null;
					// Si ja existeix primer es mira si ja està processada.
					if (AnotacioEstatEnumDto.PROCESSADA.equals(anotacio.getEstat())) {
						// Comunica l'estat de processada a Distribucio
						Estat estatDistribucio = Estat.PROCESSADA;
						msg = "La petició ja s'ha processat anteriorment.";
						if (anotacio.getExpedient() != null) {
							msg += " L'anotació ha estat processada a l'expedient " + anotacio.getExpedient().getIdentificador();
						}
						// guarda el missatge a enviar
						comunicarEstat(id, estatDistribucio, msg);

					} else {
						// Mira si l'anotació està en un estat pendent de que es processi per Helium (comunicada amb reintents sense esgotar o pendent automàtic)
						if (anotacioPendentProcessHelium(anotacio))
							continue; // No fa res, el processament ja comunicarà el resultat

						// Posa l'anotació com a comunicada per a que es torni a consultar i processar
						distribucioHelper.resetConsulta(anotacio.getId(), null);
					}
				}
			} catch (Exception e) {
//				log.error("Error rebent la petició d'anotació de registre amb id=" + id.getIndetificador() + " : " + e.getMessage() + ". Es comunica l'error a Distribucio", e);
				comunicarEstat(id, Estat.ERROR, "Error rebent l'anotació amb id " + id.getIndetificador() + ": " + e.getMessage());
			}
		}
		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}

	/** Comprova si l'anotaicó està en un estat en què Helium la processarà automàticament:
	 * - En pendent automàtic.
	 * - Comunicada amb reintents pendents.
	 * @param anotacio
	 * @return
	 */
	private boolean anotacioPendentProcessHelium(AnotacioDto anotacio) {
		return AnotacioEstatEnumDto.PENDENT_AUTO.equals(anotacio.getEstat())
			|| (AnotacioEstatEnumDto.COMUNICADA.equals(anotacio.getEstat())
			&& anotacio.getConsultaIntents() < maxConsultaIntents);
	}

	private void comunicarEstat(AnotacioRegistreId id, Estat estat, String msg) {
		try {
			//distribucioHelper.canviEstat(id, estat, msg);
		} catch(Exception ed) {
//			log.error("Error comunicant l'error de recepció a Distribucio de la petició amb id : " + id.getIndetificador() + ": " + ed.getMessage(), ed);
		}
	}
}
