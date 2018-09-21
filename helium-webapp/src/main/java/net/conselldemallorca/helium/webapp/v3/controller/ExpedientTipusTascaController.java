/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per a la pipella de tasques del tipus d'expedient. Aquesta pipella
 * permet editar les variables, documents i signatures de les tasques escollint la
 * definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusTascaController extends BaseVariableController {


	/** Pipella de les tasques depenent de la definició de procés seleccionada. */
	@RequestMapping(value = "/{expedientTipusId}/tasques")
	public String tasques(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"tasques");
		}
		omplirModelTasquesPestanya(
				request,
				expedientTipusId,
				model);
		return "v3/expedientTipusTasca";
	}
	
	/** Retorna les dades de les tasques pel datatables de tasques. Es filtra per definició de procés.
	 * @param request
	 * @param expedientTipusId
	 * @param definicioProcesId
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/{expedientTipusId}/tasca/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam Long definicioProcesId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				definicioProcesService.tascaFindPerDatatable(
						entornActual.getId(),
						definicioProcesId,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");		
	}
	
	private void omplirModelTasquesPestanya(
			HttpServletRequest request,
			Long expedientTipusId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			model.addAttribute("baseUrl", expedientTipus.getId());

			// Consulta les possibles definicions de procés amb herència
			PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
			paginacioParams.setPaginaNum(0);
			paginacioParams.setPaginaTamany(Integer.MAX_VALUE);
			paginacioParams.afegirOrdre("jbpmKey", OrdreDireccioDto.ASCENDENT);		
			List<DefinicioProcesDto> definicions =	definicioProcesService.findPerDatatable(
							entornActual.getId(),
							expedientTipusId,
							true,
							null,
							paginacioParams).getContingut();
			// Construeix les possibles opcions per la selecció de la definició de procés
			List<ParellaCodiValorDto> opcionsDefinicions = new ArrayList<ParellaCodiValorDto>();
			for (DefinicioProcesDto definicio : definicions) {
				opcionsDefinicions.add(new ParellaCodiValorDto(definicio.getId().toString(), String.format("%s - %s", definicio.getJbpmKey(), definicio.getJbpmName())));
			}
			// Informa el model
			model.addAttribute("definicions", opcionsDefinicions);		
			
			// Guarda els identificadors de les definicions heretades i sobreescrites
			List<Long> definicionsHeretadesIds = new ArrayList<Long>();
			List<Long> definicionsSobreescriuenIds = new ArrayList<Long>();
			if (expedientTipus.getExpedientTipusPareId() != null) {
				for (DefinicioProcesDto d : definicions) {
					if (d.isHeretat())
						definicionsHeretadesIds.add(d.getId());
					if (d.isSobreescriu())
						definicionsSobreescriuenIds.add(d.getId());
				}
			}
			model.addAttribute("definicionsHeretadesIds", definicionsHeretadesIds);
			model.addAttribute("definicionsSobreescriuenIds", definicionsSobreescriuenIds);
		}
	}
		
	private static final Log logger = LogFactory.getLog(ExpedientTipusTascaController.class);
}
