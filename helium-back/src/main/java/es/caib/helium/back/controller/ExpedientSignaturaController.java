/**
 * 
 */
package es.caib.helium.back.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.commons.dto.ConsultesPortafibFiltreDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.PortasignaturesDto;
import es.caib.helium.logic.intf.service.PortasignaturesService;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedient")
public class ExpedientSignaturaController extends BaseExpedientController {

    @Autowired 
    private PortasignaturesService portasignaturesService;

    @RequestMapping(value = "/{expedientId}/signatures", method = RequestMethod.GET)
    public String getSignatures(
            HttpServletRequest request,
            @PathVariable Long expedientId,
            Model model) {

        return "expedientSignatura";
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
