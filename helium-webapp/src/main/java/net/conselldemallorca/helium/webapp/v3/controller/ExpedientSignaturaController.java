/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.ConsultesPortafibFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.service.PortasignaturesService;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientSignaturaController extends BaseExpedientController {

    @Autowired 
    private PortasignaturesService portasignaturesService;

    @RequestMapping(value = "/{expedientId}/signatures", method = RequestMethod.GET)
    public String getSignatures(
            HttpServletRequest request,
            @PathVariable Long expedientId,
            Model model) {

        return "v3/expedientSignatura";
    }

	/** Mètode per retornar les dades pel datatable d'signatures dins de la gestió de l'expedient. Filtra
	 * per expedientId.
	 * @param request
	 * @param expedientId
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/{expedientId}/signatura/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {

		ConsultesPortafibFiltreDto filtre = new ConsultesPortafibFiltreDto();
		filtre.setExpedientId(expedientId);
		
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		PaginaDto<PortasignaturesDto> resultat = portasignaturesService.findAmbFiltrePaginat(
				paginacioParams,
				filtre);
		return DatatablesHelper.getDatatableResponse(request, null, resultat);
	}
}
