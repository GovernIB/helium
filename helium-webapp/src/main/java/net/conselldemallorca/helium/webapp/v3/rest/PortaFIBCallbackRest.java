/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.portafib.callback.beans.v1.PortaFIBEvent;
import net.conselldemallorca.helium.core.helper.MonitorIntegracioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.model.service.ServiceProxy;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;

/**
 * Implementació dels mètodes per al servei de callback del portafirmes per API
 * REST. També és possible rebre els callbacks per wS SOAP al PortaFIBCallBack.
 * 
 * @see net.conselldemallorca.helium.ws.callbackportafib.PortaFIBCallBack
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/rest/portafib/callback")
public class PortaFIBCallbackRest {

	@Autowired
	private AdminService adminService;

	@Autowired
	private ExpedientDocumentService expedientDocumentService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String get() {
		return "restPortafib";
	}

	/** Mètode on es notifiquen els events del Portasignatures. */
	@RequestMapping(value = "/event", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> event(@RequestBody PortaFIBEvent event) {

		Long documentId = event.getSigningRequest().getID();
		Integer estat = event.getEventTypeID();
		String motiuRebuig = null;
		if (event.getSigningRequest() != null)
			motiuRebuig = event.getSigningRequest().getRejectionReason();

		logger.debug("Rebuda petició al callback de portafirmes API REST v1 (" + "documentId: " + documentId + ", "
				+ "estat: " + estat + ", " + "motiu rebuig: " + motiuRebuig + ")");

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Petició rebuda al callback API REST 1.0";
		List<IntegracioParametreDto> parametres = new ArrayList<IntegracioParametreDto>();
		parametres.add(new IntegracioParametreDto("documentId", new Long(documentId).toString()));
		parametres.add(new IntegracioParametreDto("estat", new Integer(estat).toString()));
		// Transforma el codi d'estat
		TipusEstat tipusEstat;
		switch (estat) {
		case 0:
		case 50:
			tipusEstat = TipusEstat.PENDENT;
			break;
		case 60:
			tipusEstat = TipusEstat.SIGNAT;
			break;
		case 70:
			tipusEstat = TipusEstat.REBUTJAT;
			break;
		case 80:
			tipusEstat = TipusEstat.BLOQUEJAT;
			break;
		default:
			String errorDescripcio = "No es reconeix el codi d'estat (" + estat + ")";
			adminService.monitorAddAccio(MonitorIntegracioHelper.INTCODI_PFIRMA, accioDescripcio,
					IntegracioAccioTipusEnumDto.RECEPCIO, IntegracioAccioEstatEnumDto.ERROR,
					System.currentTimeMillis() - t0, errorDescripcio, null, parametres);
			return new ResponseEntity<String>(errorDescripcio, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Comprova si existeix la petició
		PortasignaturesDto portasignatures = expedientDocumentService.getPortasignaturesByDocumentId(documentId.intValue());
		if (portasignatures != null) {
			Double resposta = -1D;
			boolean processamentOk = false;
			String accio = null;
			try {
				PluginService pluginService = ServiceProxy.getInstance().getPluginService();
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
					processamentOk = pluginService.processarDocumentCallbackPortasignatures(documentId.intValue(),
							false, null);
					resposta = (processamentOk) ? 1D : -1D;
					break;
				case REBUTJAT:
					accio = "Rebutjat";
					String motiu = null;
					if (event.getSigningRequest() != null)
						motiu = event.getSigningRequest().getRejectionReason();
					processamentOk = pluginService.processarDocumentCallbackPortasignatures(documentId.intValue(), true,
							motiu);
					resposta = (processamentOk) ? 1D : -1D;
					break;
				default:
					break;
				}
				logger.info("Fi procés petició callback portasignatures (id=" + documentId + ", estat=" + estat + "-"
						+ accio + ", resposta=" + resposta + ")");
				if (!processamentOk) {
					return new ResponseEntity<String>(
							"El document no s'ha processat correctament (id=" + documentId + ", estat=" + estat + "-Signat, resposta=" + resposta + ")", 
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} catch (Exception ex) {
				logger.error("Error procés petició callback portasignatures (id=" + documentId + ", estat=" + estat
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
			logger.warn(warnMsg);
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
		logger.debug("Rebuda consulta de versió al callback de portafirmes API REST v1");

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

		return new ResponseEntity<Integer>(new Integer(1), HttpStatus.OK);
	}
	
	private static final Log logger = LogFactory.getLog(PortaFIBCallbackRest.class);
}
