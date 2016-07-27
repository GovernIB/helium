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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto.IdAmbEtiqueta;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusDefinicioProcesImportarCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusDefinicioProcesImportarCommand.Importar;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pipella de definicions de processos del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusDefinicioProcesController extends BaseExpedientTipusController {

	@RequestMapping(value = "/{expedientTipusId}/definicionsProces")
	public String definicionsProces(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"definicionsProces");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
		}
		return "v3/expedientTipusDefinicioProces";
	}
	
	@RequestMapping(value="/{expedientTipusId}/definicionsProces/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(required = false) 
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.definicioProcesFindPerDatatable(
						entornActual.getId(),
						expedientTipusId,
						true,
						paginacioParams.getFiltre(),
						paginacioParams));
	}	
	
	/** Mètode per marcar una definició de procés com a inicial per al tipus d'expedient. */
	@RequestMapping(value = "/{expedientTipusId}/definicionsProces/{id}/inicial", method = RequestMethod.GET)
	@ResponseBody
	public boolean inicial(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		boolean ret = false;
		try {
			ret = expedientTipusService.definicioProcesSetInicial(expedientTipusId, id);
		} catch(Exception e) {
			if (ret)
			logger.error("S'ha produit un error al intentar establir la definicio de proces inicial id '" + id + "' pel tipus d'expedient amb id '" + expedientTipusId, e);
		}
		if (ret)
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.definicioProces.llistat.definicioProces.inicial.correcte"));
		else
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.definicioProces.llistat.definicioProces.inicial.error"));
		return ret;
	}
	

	/** Mètode per importar cap al tipus d'expedient la informació d'una definició de procés. */
	@RequestMapping(value = "/{expedientTipusId}/definicionsProces/{id}/importar", method = RequestMethod.GET)
	public String importar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDefinicioProcesImportarCommand command = new ExpedientTipusDefinicioProcesImportarCommand();
		command.setSobreescriure(false);
		model.addAttribute("expedientTipusDefinicioProcesImportarCommand", command);
		model.addAttribute("versions", obtenirParellesVersions(entornActual.getId(), id));

		return "v3/expedientTipusDefinicioProcesImportarForm";
	}
			
	@RequestMapping(value = "/{expedientTipusId}/definicionsProces/{id}/importar", method = RequestMethod.POST)
	public String importarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(Importar.class) ExpedientTipusDefinicioProcesImportarCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
    		model.addAttribute("versions", obtenirParellesVersions(entornActual.getId(), id));
        	return "v3/expedientTipusDefinicioProcesImportarForm";
        } else {
        	if (command.getDefinicioProcesId() == null)
        		command.setDefinicioProcesId(id);
        	if (expedientTipusService.definicioProcesImportar(
        			expedientTipusId, 
        			command.getDefinicioProcesId(),
        			command.isSobreescriure()))
	    		MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.definicioProces.llistat.definicioProces.importar.correcte"));
        	else
	    		MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								"expedient.tipus.definicioProces.llistat.definicioProces.importar.error"));
			return modalUrlTancar(false);
			
        }
	}		
	
	private List<ParellaCodiValorDto> obtenirParellesVersions(Long entornId, Long definicioProcesId) {
		
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		DefinicioProcesExpedientDto definicioProcesIniciExpedientDto = dissenyService.getDefinicioProcesByEntorIdAndProcesId(entornId, definicioProcesId);
		for (IdAmbEtiqueta idAmbEtiqueta: definicioProcesIniciExpedientDto.getListIdAmbEtiqueta()) {
			resposta.add(new ParellaCodiValorDto(idAmbEtiqueta.getId().toString(), idAmbEtiqueta.getEtiqueta()));
		}
		return resposta;
	}
	
	@RequestMapping(value = "/{expedientTipusId}/definicionsProces/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		
		try {
			expedientTipusService.definicioProcesDelete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.definicioProces.llistat.definicioProces.esborrar.correcte"));
			return true;
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.definicioProces.llistat.definicioProces.esborrar.error"));
			logger.error("S'ha produit un error al intentar eliminar la definicio de proces amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId, e);
			return false;
		}
	}
		
	private static final Log logger = LogFactory.getLog(ExpedientTipusDefinicioProcesController.class);
}
