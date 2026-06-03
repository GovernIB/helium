package es.caib.helium.api.controller.rest;

import es.caib.helium.commons.dto.*;
import es.caib.helium.logic.helper.MonitorIntegracioHelper;
import es.caib.helium.logic.intf.service.AdminService;
import es.caib.helium.logic.intf.service.ExpedientDocumentService;
import es.caib.portafib.callback.beans.v1.PortaFIBEvent;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rest/portafib/callback")
@Tag(
	name = "Portafib",
	description = "API REST d'integració amb Portafib.")
public class PortaFIBCallbackRest {

	@Autowired
	private AdminService adminService;
	@Autowired
	private ExpedientDocumentService expedientDocumentService;

	@GetMapping
	public String get() {
		return "restPortafib";
	}

	/** Mètode on es notifiquen els events del Portasignatures. */
	@PostMapping("/event")
	public ResponseEntity<String> event(@RequestBody PortaFIBEvent event) {
		long documentId =  event.getSigningRequest().getID();
		int estat = event.getEventTypeID();
		String motiuRebuig = null;
		if (event.getSigningRequest() != null)
			motiuRebuig = event.getSigningRequest().getRejectionReason();

		log.debug("Rebuda petició al callback de portafirmes API REST v1 (" + "documentId: " + documentId + ", "
			+ "estat: " + estat + ", " + "motiu rebuig: " + motiuRebuig + ")");

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Petició rebuda al callback API REST 1.0";
		List<IntegracioParametreDto> parametres = new ArrayList<IntegracioParametreDto>();
		parametres.add(new IntegracioParametreDto("documentId",Long.toString(documentId)));
		parametres.add(new IntegracioParametreDto("estat", Integer.toString(estat)));
		// Transforma el codi d'estat
		PortafirmesEstatEnum tipusEstat;
		switch (estat) {
			case 0:
			case 50:
				tipusEstat = PortafirmesEstatEnum.PENDENT;
				break;
			case 60:
				tipusEstat = PortafirmesEstatEnum.SIGNAT;
				break;
			case 70:
				tipusEstat = PortafirmesEstatEnum.REBUTJAT;
				break;
			case 80:
				tipusEstat = PortafirmesEstatEnum.BLOQUEJAT;
				break;
			default:
				String errorDescripcio = "No es reconeix el codi d'estat (" + estat + ")";
				adminService.monitorAddAccio(MonitorIntegracioHelper.INTCODI_PFIRMA, accioDescripcio,
					IntegracioAccioTipusEnumDto.RECEPCIO, IntegracioAccioEstatEnumDto.ERROR,
					System.currentTimeMillis() - t0, errorDescripcio, null, parametres);
				return new ResponseEntity<String>(errorDescripcio, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Comprova si existeix la petició
		PortasignaturesDto portasignatures = expedientDocumentService.getPortasignaturesByDocumentId(Long.valueOf(documentId).intValue());
		if (portasignatures != null) {
			double resposta = -1D;
			boolean processamentOk = false;
			String accio = null;
			ProcessDocumentPortafibRunnable runnable = null;
			String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
			try {
				switch (tipusEstat) {
					case BLOQUEJAT:
						resposta = 1D;
						accio = "Bloquejat";
						processamentOk = true;
						break;
					case PENDENT:
						resposta = 1D;
						accio = "Pendent";
						processamentOk = true;
						break;
					case SIGNAT:
						accio = "Signat";
						runnable = new ProcessDocumentPortafibRunnable(Long.valueOf(documentId).intValue(), false, null, usuariCodi);
						break;
					case REBUTJAT:
						accio = "Rebutjat";
						String motiu = null;
						if (event.getSigningRequest() != null)
							motiu = event.getSigningRequest().getRejectionReason();
						runnable = new ProcessDocumentPortafibRunnable(Long.valueOf(documentId).intValue(), true, motiu, usuariCodi);
						break;
					default:
						break;
				}

				// Processam el document en segon plà #1898
				if(runnable != null)
					(new Thread(runnable)).start();

				log.info("Fi procés petició callback portasignatures (id=" + documentId + ", estat=" + estat + "-"
					+ accio + ", resposta=" + resposta + ")");
			} catch (Exception ex) {
				log.error("Error procés petició callback portasignatures (id=" + documentId + ", estat=" + estat
					+ ", resposta=" + resposta + "): " + ex.getMessage());
				String errorDescripcio = "El procés petició callback del portasignatures no ha finalitzat correctament";
				parametres.add(new IntegracioParametreDto("processamentOk", processamentOk));
				adminService.monitorAddAccio(MonitorIntegracioHelper.INTCODI_PFIRMA, accioDescripcio,
					IntegracioAccioTipusEnumDto.RECEPCIO, IntegracioAccioEstatEnumDto.ERROR,
					System.currentTimeMillis() - t0, errorDescripcio, ex, parametres);
				return new ResponseEntity<String>(errorDescripcio, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			// Avís als logs en comtpes de retorna error #1413
			String warnMsg = "Petició amb id " + documentId + " no trobada a Helium.";
			parametres.add(new IntegracioParametreDto("advertencia", warnMsg));
			log.warn(warnMsg);
		}
		adminService.monitorAddAccio(MonitorIntegracioHelper.INTCODI_PFIRMA, accioDescripcio,
			IntegracioAccioTipusEnumDto.RECEPCIO, IntegracioAccioEstatEnumDto.OK, System.currentTimeMillis() - t0,
			null, null, parametres);
		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}

	/** Mètode per consultar la versió. Es retorna 1. */
	@RequestMapping(value = "/versio", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Integer> versio() {
		log.debug("Rebuda consulta de versió al callback de portafirmes API REST v1");

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta de la versió del callback REST 1.0";
		List<IntegracioParametreDto> parametres = new ArrayList<IntegracioParametreDto>();

		adminService.monitorAddAccio(
			MonitorIntegracioHelper.INTCODI_PFIRMA,
			accioDescripcio,
			IntegracioAccioTipusEnumDto.RECEPCIO,
			IntegracioAccioEstatEnumDto.OK,
			System.currentTimeMillis() - t0,
			null, null,
			parametres);

		return new ResponseEntity<Integer>(1, HttpStatus.OK);
	}
}
