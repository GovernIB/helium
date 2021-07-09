package es.caib.helium.back.controller;

import es.caib.helium.back.command.ExpedientTipusEnumeracioValorCommand;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.EnumeracioDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusEnumeracioValorDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.service.EnumeracioService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controlador per a la pipella de variables del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "expedientTipusEnumeracioValorControllerV3")
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusEnumeracioValorController extends BaseExpedientTipusController {
	
	@Autowired
	protected EnumeracioService enumeracioService;

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valors", method = RequestMethod.GET)
	public String valors(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			Model model) {
		
		ompleDadesModel(request, expedientTipusId, enumeracioId, model, true);

		return "v3/expedientTipusEnumeracioValors";
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request, 
				null, 
				enumeracioService.valorFindPerDatatable(
						enumeracioId, 
						paginacioParams.getFiltre(), paginacioParams));
	}
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/{id}/moure/{posicio}", method = RequestMethod.GET)
	@ResponseBody
	public boolean mourer(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			@PathVariable int posicio,
			Model model) {
		return enumeracioService.valorMoure(id, posicio);
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/{id}/update", method = RequestMethod.GET)
	public String modifica(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			Model model) {
		ExpedientTipusEnumeracioValorDto dto = enumeracioService.valorFindAmbId(id);
		ExpedientTipusEnumeracioValorCommand command = conversioTipusHelper.convertir(dto, ExpedientTipusEnumeracioValorCommand.class);
		ompleDadesModel(request, expedientTipusId, enumeracioId, model, false);		
		model.addAttribute("expedientTipusEnumeracioValorCommand", command);
		model.addAttribute("mostraUpdate", true);
		return "v3/expedientTipusEnumeracioValors";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/{id}/update", method = RequestMethod.POST)
	public String modificaPost(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			@Validated(ExpedientTipusEnumeracioValorCommand.Modificacio.class) ExpedientTipusEnumeracioValorCommand command,
			BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			ompleDadesModel(request, expedientTipusId, enumeracioId, model, false);		
			model.addAttribute("expedientTipusEnumeracioValorCommand", command);
			model.addAttribute("mostraUpdate", true);
			return "v3/expedientTipusEnumeracioValors";
		} else {		
			ExpedientTipusEnumeracioValorDto dto = ExpedientTipusEnumeracioValorCommand.asExpedientTipusEnumeracioValorDto(command);
			
			//Conservam l´ordre anterior
			ExpedientTipusEnumeracioValorDto dto_antic = enumeracioService.valorFindAmbId(id);
			dto.setOrdre(dto_antic.getOrdre());
			
			enumeracioService.valorUpdate(dto);
			
			ompleDadesModel(request, expedientTipusId, enumeracioId, model, true);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.controller.modificat"));				
			
			return "v3/expedientTipusEnumeracioValors";
		}
	}
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			Model model) {
		try {
			enumeracioService.valorDelete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.controller.eliminat"));			
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.controller.eliminat.us"));
			logger.error("S'ha produit un error al intentar eliminar el valor del enumerat amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId, e);
		}
		
		ompleDadesModel(request, expedientTipusId, enumeracioId, model, true);
		return "v3/expedientTipusEnumeracioValors";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			@Validated(ExpedientTipusEnumeracioValorCommand.Creacio.class) ExpedientTipusEnumeracioValorCommand command,
			BindingResult bindingResult, Model model) {
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("mostraCreate", true);
			ompleDadesModel(request, expedientTipusId, enumeracioId, model, false);
			model.addAttribute("expedientTipusEnumeracioValorCommand", command);
        	return "v3/expedientTipusEnumeracioValors";
		} else {
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			ExpedientTipusEnumeracioValorDto dto = ExpedientTipusEnumeracioValorCommand.asExpedientTipusEnumeracioValorDto(command);
			
			
			enumeracioService.valorsCreate(expedientTipusId, enumeracioId, entornActual.getId(), dto);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.controller.creat"));
        	return valors(request, expedientTipusId, enumeracioId, model);
		}
	}
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/importar", method = RequestMethod.POST)
	public String importar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			@RequestParam(value = "multipartFile", required = true) final MultipartFile multipartFile,
			@RequestParam(value = "eliminarValorsAntics", required = false) Boolean eliminarValorsAntics,
			Model model) {

		try {
			
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			
			if (multipartFile.getBytes() == null || multipartFile.getBytes().length == 0) {
				MissatgesHelper.error(
						request,
						getMessage(
								request,
								"error.especificar.arxiu.importar"));
	        	return valors(request, expedientTipusId, enumeracioId, model);				
			} else {
				
				if (eliminarValorsAntics != null && eliminarValorsAntics) {
					enumeracioService.enumeracioDeleteAllByEnumeracio(enumeracioId);
				}
				List<ExpedientTipusEnumeracioValorDto> valors = new ArrayList<ExpedientTipusEnumeracioValorDto>();
				Set<String> valorsCodis = new HashSet<String>();
				BufferedReader br = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
				String linia = br.readLine();
				while (linia != null) {
					String[] columnes = linia.contains(";") ? linia.split(";") : linia.split(",");
					if (columnes.length > 1) {
						ExpedientTipusEnumeracioValorDto enumeracioValors = new ExpedientTipusEnumeracioValorDto();
			        	enumeracioValors.setId(null);
			        	// Per evitar caràcters estranys al codi de l'enumeració
			        	String codi = columnes[0];
			        	while (!codi.matches("^\\w.*")) {
			        		codi = codi.substring(1);
			        	}
			        	enumeracioValors.setCodi(codi);
			        	enumeracioValors.setNom(columnes[1]);
			        	// Comprova que no existeixi ja
			        	if( enumeracioService.valorFindAmbCodi(expedientTipusId, enumeracioId, codi) != null)
			        		throw new ValidacioException(
			        				getMessage(
											request,
											"expedient.tipus.enumeracio.valors.importats.duplicat",
											new Object[] {codi}));
			        	// Valida que no s'importin dos codis
			        	if (valorsCodis.contains(codi))
			        		throw new ValidacioException(
			        				getMessage(
											request,
											"expedient.tipus.enumeracio.valors.importats.duplicat",
											new Object[] {codi}));
			        	else
			        		valorsCodis.add(codi);
			        	valors.add(enumeracioValors);
					}
					linia = br.readLine();
				}
				for(ExpedientTipusEnumeracioValorDto valor : valors)
					enumeracioService.valorsCreate(expedientTipusId, enumeracioId, entornActual.getId(), valor);
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"expedient.tipus.enumeracio.valors.importats"));
	        	return valors(request, expedientTipusId, enumeracioId, model);
			}
		} catch (Exception ex) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.importats.error", new Object[] {ex.getMessage()}));
        	return valors(request, expedientTipusId, enumeracioId, model);
        }
	}
	
	private void ompleDadesModel(
			HttpServletRequest request,
			Long expedientTipusId,
			Long enumeracioId,
			Model model,
			boolean ficaCommand) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		if (entornActual != null) {
			
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(entornActual.getId(),	expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			
			EnumeracioDto enumeracio = enumeracioService.findAmbId(expedientTipusId, enumeracioId);
			model.addAttribute("enumeracio", enumeracio);
			
			if (ficaCommand) {
				ExpedientTipusEnumeracioValorCommand command = new ExpedientTipusEnumeracioValorCommand();
				command.setExpedientTipusId(expedientTipusId);
				command.setEnumeracioId(enumeracioId);
				model.addAttribute("expedientTipusEnumeracioValorCommand", command);
			}
			model.addAttribute("heretat", enumeracio.isHeretat());
		}
	}

	private static final Log logger = LogFactory.getLog(ExpedientTipusDocumentController.class);
}