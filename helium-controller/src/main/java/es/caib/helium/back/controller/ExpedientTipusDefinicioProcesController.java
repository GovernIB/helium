/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.ExpedientTipusDefinicioProcesIncorporarCommand;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.NodecoHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.client.model.ParellaCodiValor;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesExpedientDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesExpedientDto.IdAmbEtiqueta;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.exception.ExportException;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
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
				definicioProcesService.findPerDatatable(
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
	

	/** Mètode per incorporar cap al tipus d'expedient la informació d'una definició de procés. */
	@RequestMapping(value = "/{expedientTipusId}/definicionsProces/{id}/incorporar", method = RequestMethod.GET)
	public String incorporar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDefinicioProcesIncorporarCommand command = new ExpedientTipusDefinicioProcesIncorporarCommand();
		command.setSobreescriure(false);
		command.setTasques(false);		
		model.addAttribute("expedientTipusDefinicioProcesImportarCommand", command);
		model.addAttribute("versions", obtenirParellesVersions(entornActual.getId(), id));

		DefinicioProcesDto definicioProces = definicioProcesService.findById(id);
		model.addAttribute("potCanviarTasques", definicioProces.getExpedientTipus() != null 
												&& definicioProces.getExpedientTipus().getId().equals(expedientTipusId));

		return "v3/expedientTipusDefinicioProcesIncorporarForm";
	}
			
	@RequestMapping(value = "/{expedientTipusId}/definicionsProces/{id}/incorporar", method = RequestMethod.POST)
	public String incorporarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(ExpedientTipusDefinicioProcesIncorporarCommand.Incorporar.class) ExpedientTipusDefinicioProcesIncorporarCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
    		model.addAttribute("versions", obtenirParellesVersions(entornActual.getId(), id));
    		model.addAttribute("definicioProces", definicioProcesService.findById(id));
    		DefinicioProcesDto definicioProces = definicioProcesService.findById(id);
    		model.addAttribute("potCanviarTasques", expedientTipusId.equals(definicioProces.getExpedientTipus().getId()));
        	return "v3/expedientTipusDefinicioProcesIncorporarForm";
        } else {
        	if (command.getDefinicioProcesId() == null)
        		command.setDefinicioProcesId(id);
        	try {
            	expedientTipusService.definicioProcesIncorporar(
            			expedientTipusId, 
            			command.getDefinicioProcesId(),
            			command.isSobreescriure(),
            			command.isTasques());
    	    		MissatgesHelper.success(
    						request, 
    						getMessage(
    								request, 
    								"expedient.tipus.definicioProces.llistat.definicioProces.incorporar.correcte"));
        		
        	} catch (ExportException e) {
	    		MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								"expedient.tipus.definicioProces.llistat.definicioProces.incorporar.error",
								new Object[] {e.getLocalizedMessage()}));
        	}
			return modalUrlTancar(false);
			
        }
	}		
	
	private List<ParellaCodiValor> obtenirParellesVersions(Long entornId, Long definicioProcesId) {
		
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		DefinicioProcesExpedientDto definicioProcesIniciExpedientDto = dissenyService.getDefinicioProcesByEntorIdAndProcesId(entornId, definicioProcesId);
		for (IdAmbEtiqueta idAmbEtiqueta: definicioProcesIniciExpedientDto.getListIdAmbEtiqueta()) {
			resposta.add(new ParellaCodiValor(idAmbEtiqueta.getId().toString(), idAmbEtiqueta.getEtiqueta()));
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
