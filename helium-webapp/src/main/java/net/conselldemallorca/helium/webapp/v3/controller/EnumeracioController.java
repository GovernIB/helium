package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.EnumeracioService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEnumeracioCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEnumeracioValorCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per al manteniment de les enumeracions a nivell d'entorn.
 *
 */
@Controller(value = "enumeracioControllerV3")
@RequestMapping("/v3/enumeracio")
public class EnumeracioController extends BaseDissenyController {
	
	@Autowired
	private EnumeracioService enumeracioService;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	/** Accés al llistat d'enumeracions de l'entorn des del menú de disseny. */
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			return "v3/enumeracioLlistat";
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:/v3";
		}
	}
	
	@RequestMapping(value="/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				enumeracioService.findPerDatatable(
						entornActual.getId(),
						null, // expedientTipusId
						true, // incloure globals
						paginacioParams.getFiltre(),
						paginacioParams));
	}	
	
	/** Formulari per crear una nova enumeració. */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String nova(
			HttpServletRequest request, 
			Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			ExpedientTipusEnumeracioCommand command = new ExpedientTipusEnumeracioCommand();
			model.addAttribute("expedientTipusEnumeracioCommand", command);
			return "v3/expedientTipusEnumeracioForm";
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:/v3";
		}
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String novaPost(
			HttpServletRequest request, 
			@Validated(ExpedientTipusEnumeracioCommand.Creacio.class) ExpedientTipusEnumeracioCommand command,
			BindingResult bindingResult, Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			try {
				if (bindingResult.hasErrors()) {
					return "v3/expedientTipusEnumeracioForm";
				} else {
				
					EnumeracioDto dto = ExpedientTipusEnumeracioCommand.asEnumeracioDto(command);
					EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
					
					enumeracioService.create(
							entornActual.getId(), 
							null, //expedientTipusId 
							dto);
					
		    		MissatgesHelper.success(
							request, 
							getMessage(
									request, 
									"expedient.tipus.enumeracio.controller.creat"));
				}
			} catch (Exception ex) {
				MissatgesHelper.error(request, 
						getMessage(
								request, 
								"expedient.tipus.enumeracio.controller.creat.error",
								new Object[] {ex.getLocalizedMessage()}));
				logger.error("No s'ha pogut guardar l'enumeració", ex);
				return "v3/expedientTipusEnumeracioForm";
		    }
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
		}
		return modalUrlTancar(true);
	}	
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request, 
			@PathVariable Long id,
			Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			EnumeracioDto dto = enumeracioService.findAmbId(null, id);
			ExpedientTipusEnumeracioCommand command = conversioTipusHelper.convertir(dto, ExpedientTipusEnumeracioCommand.class);
			model.addAttribute("expedientTipusEnumeracioCommand", command);
			return "v3/expedientTipusEnumeracioForm";
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:/v3";
		}
		
	}

	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request, 
			@PathVariable Long id,
			@Validated(ExpedientTipusEnumeracioCommand.Modificacio.class) ExpedientTipusEnumeracioCommand command,
			BindingResult bindingResult, Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			try {
				if (bindingResult.hasErrors()) {
					return "v3/expedientTipusEnumeracioForm";
				} else {
					EnumeracioDto dto = ExpedientTipusEnumeracioCommand.asEnumeracioDto(command);
					enumeracioService.update(dto);
					
		    		MissatgesHelper.success(
							request, 
							getMessage(
									request, 
									"expedient.tipus.enumeracio.controller.modificat"));
				}
			} catch (Exception ex) {
				MissatgesHelper.error(request, 
						getMessage(
								request, 
								"expedient.tipus.enumeracio.controller.creat.error",
								new Object[] {ex.getLocalizedMessage()}));
				logger.error("No s'ha pogut guardar l'enumerat: " + id, ex);
				return "v3/expedientTipusEnumeracioForm";
		    }
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:/v3";
		}		
		return modalUrlTancar(true);
	}	

	
	/** Mètode per esborrar una versió específica des del disseny de la definició de procés. */
	@RequestMapping(value = "/{enumeracioId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long enumeracioId,
			Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			try {
				enumeracioService.delete(enumeracioId);
				MissatgesHelper.success(request, getMessage(request, "expedient.tipus.enumeracio.controller.eliminat"));
			}catch (ValidacioException ex) {
				MissatgesHelper.error(request, ex.getMessage());
			}
			return modalUrlTancar(false);
		} else {
			return "redirect:/v3";
		}
	}	
	
	
	// VALORS
	
	@RequestMapping(value = "/{enumeracioId}/valors", method = RequestMethod.GET)
	public String valors(
			HttpServletRequest request,
			@PathVariable Long enumeracioId,
			Model model) {
		
		ompleDadesModel(request, enumeracioId, model, true);

		return "v3/expedientTipusEnumeracioValors";
	}

	@RequestMapping(value = "/{enumeracioId}/valor/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse valorsDatatable(
			HttpServletRequest request, 
			@PathVariable Long enumeracioId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request, 
				null, 
				enumeracioService.valorFindPerDatatable(
						enumeracioId, 
						paginacioParams.getFiltre(), 
						paginacioParams));
	}
	
	@RequestMapping(value = "/{enumeracioId}/valor/{id}/moure/{posicio}", method = RequestMethod.GET)
	@ResponseBody
	public boolean valorMoure(
			HttpServletRequest request, 
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			@PathVariable int posicio,
			Model model) {
		return enumeracioService.valorMoure(id, posicio);
	}

	@RequestMapping(value = "/{enumeracioId}/valor/{id}/update", method = RequestMethod.GET)
	public String valorModificar(
			HttpServletRequest request, 
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			Model model) {
		ExpedientTipusEnumeracioValorDto dto = enumeracioService.valorFindAmbId(id);
		ExpedientTipusEnumeracioValorCommand command = conversioTipusHelper.convertir(dto, ExpedientTipusEnumeracioValorCommand.class);
		ompleDadesModel(request, enumeracioId, model, false);		
		model.addAttribute("expedientTipusEnumeracioValorCommand", command);
		model.addAttribute("mostraUpdate", true);
		return "v3/expedientTipusEnumeracioValors";
	}
	
	@RequestMapping(value = "/{enumeracioId}/valor/{id}/update", method = RequestMethod.POST)
	public String valorModificaPost(
			HttpServletRequest request, 
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			@Validated(ExpedientTipusEnumeracioValorCommand.Modificacio.class) ExpedientTipusEnumeracioValorCommand command,
			BindingResult bindingResult, Model model) {

		ompleDadesModel(request, enumeracioId, model, true);
		if (bindingResult.hasErrors()) {
			model.addAttribute("expedientTipusEnumeracioValorCommand", command);
			model.addAttribute("mostraUpdate", true);
			return "v3/expedientTipusEnumeracioValors";
		} else {		
		
			ExpedientTipusEnumeracioValorDto dto = ExpedientTipusEnumeracioValorCommand.asExpedientTipusEnumeracioValorDto(command);
			//Conservam l´ordre anteriro
			ExpedientTipusEnumeracioValorDto dto_antic = enumeracioService.valorFindAmbId(id);
			dto.setOrdre(dto_antic.getOrdre());
			
			enumeracioService.valorUpdate(dto);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.controller.modificat"));				
			
			return "v3/expedientTipusEnumeracioValors";
		}
	}
	
	@RequestMapping(value = "/{enumeracioId}/valor/{id}/delete", method = RequestMethod.GET)
	public String valorDelete(
			HttpServletRequest request, 
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
			logger.error("S'ha produit un error al intentar eliminar el valor del enumerat amb id '" + id + "'", e);
		}
		
		ompleDadesModel(request, enumeracioId, model, true);
		return "v3/expedientTipusEnumeracioValors";
	}
	
	@RequestMapping(value = "/{enumeracioId}/valor/new", method = RequestMethod.POST)
	public String valorNouPost(
			HttpServletRequest request, 
			@PathVariable Long enumeracioId,
			@Validated(ExpedientTipusEnumeracioValorCommand.Creacio.class) ExpedientTipusEnumeracioValorCommand command,
			BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("mostraCreate", true);
			ompleDadesModel(request, enumeracioId, model, false);
			model.addAttribute("expedientTipusEnumeracioValorCommand", command);
        	return "v3/expedientTipusEnumeracioValors";
		} else {
		
			ExpedientTipusEnumeracioValorDto dto = ExpedientTipusEnumeracioValorCommand.asExpedientTipusEnumeracioValorDto(command);
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			
			enumeracioService.valorsCreate(null, enumeracioId, entornActual.getId(), dto);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.controller.creat"));
        	return valors(request, enumeracioId, model);
		}
	}
	
	@RequestMapping(value = "/{enumeracioId}/valor/importar", method = RequestMethod.POST)
	public String valorImportar(
			HttpServletRequest request,
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
	        	return valors(request, enumeracioId, model);				
			} else {
				
				if (eliminarValorsAntics != null && eliminarValorsAntics) {
					enumeracioService.enumeracioDeleteAllByEnumeracio(enumeracioId);
				}
				
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
			        	enumeracioService.valorsCreate(null, enumeracioId, entornActual.getId(), enumeracioValors);
					}
					linia = br.readLine();
				}
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"expedient.tipus.enumeracio.valors.importats"));
			}
		} catch (Exception ex) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.importats.error"));
        }
    	return valors(request, enumeracioId, model);
	}	
	
	private void ompleDadesModel(
			HttpServletRequest request,
			Long enumeracioId,
			Model model,
			boolean ficaCommand) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		if (entornActual != null) {
						
			EnumeracioDto enumeracio = enumeracioService.findAmbId(null, enumeracioId);
			model.addAttribute("enumeracio", enumeracio);
			
			if (ficaCommand) {
				ExpedientTipusEnumeracioValorCommand command = new ExpedientTipusEnumeracioValorCommand();
				command.setEnumeracioId(enumeracioId);
				model.addAttribute("expedientTipusEnumeracioValorCommand", command);
			}
			model.addAttribute("heretat", enumeracio.isHeretat());
		}
	}


	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	private static final Log logger = LogFactory.getLog(EnumeracioController.class);
}
