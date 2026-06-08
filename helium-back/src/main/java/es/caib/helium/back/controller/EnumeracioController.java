package es.caib.helium.back.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import es.caib.helium.back.command.ExpedientTipusEnumeracioCommand;
import es.caib.helium.back.command.ExpedientTipusEnumeracioValorCommand;
import es.caib.helium.back.helper.ConversioTipus;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.EnumeracioDto;
import es.caib.helium.commons.dto.ExpedientTipusEnumeracioValorDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.exception.InUseException;
import es.caib.helium.commons.exception.ValidacioException;
import es.caib.helium.logic.intf.service.EnumeracioService;

/**
 * Controlador per al manteniment de les enumeracions a nivell d'entorn.
 *
 */
@Controller(value = "enumeracioControllerV3")
@RequestMapping("/enumeracio")
public class EnumeracioController extends BaseDissenyController {

	@Autowired
	private EnumeracioService enumeracioService;

	/** Accés al llistat d'enumeracions de l'entorn des del menú de disseny. */
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			return "enumeracioLlistat";
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:";
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
			return "expedientTipusEnumeracioForm";
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:";
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
					return "expedientTipusEnumeracioForm";
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
								new Object[] {ex.getLocalizedMessage()}),
						ex);
				logger.error("No s'ha pogut guardar l'enumeració", ex);
				return "expedientTipusEnumeracioForm";
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
			ExpedientTipusEnumeracioCommand command = ConversioTipus.convertir(dto, ExpedientTipusEnumeracioCommand.class);
			model.addAttribute("expedientTipusEnumeracioCommand", command);
			return "expedientTipusEnumeracioForm";
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:";
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
					return "expedientTipusEnumeracioForm";
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
								new Object[] {ex.getLocalizedMessage()}),
						ex);
				logger.error("No s'ha pogut guardar l'enumerat: " + id, ex);
				return "expedientTipusEnumeracioForm";
		    }
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:";
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
			} catch (ValidacioException ex) {
				MissatgesHelper.error(request, ex.getMessage());
			} catch (Exception ex) {
				MissatgesHelper.error(request, ex.getMessage(), ex);
			}
			return modalUrlTancar(false);
		} else {
			return "redirect:";
		}
	}


	// VALORS

	@RequestMapping(value = "/{enumeracioId}/valors", method = RequestMethod.GET)
	public String valors(
			HttpServletRequest request,
			@PathVariable Long enumeracioId,
			Model model) {

		ompleDadesModel(request, enumeracioId, model, true);

		return "expedientTipusEnumeracioValors";
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
		ExpedientTipusEnumeracioValorCommand command = ConversioTipus.convertir(dto, ExpedientTipusEnumeracioValorCommand.class);
		ompleDadesModel(request, enumeracioId, model, false);
		model.addAttribute("expedientTipusEnumeracioValorCommand", command);
		model.addAttribute("mostraUpdate", true);
		//model.addAttribute("inUse", enumeracioService.valorInUse(id));
		return "expedientTipusEnumeracioValors";
	}

	@RequestMapping(value = "/{enumeracioId}/valor/{id}/update", method = RequestMethod.POST)
	public String valorModificaPost(
			HttpServletRequest request,
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			@Validated(ExpedientTipusEnumeracioValorCommand.Modificacio.class) ExpedientTipusEnumeracioValorCommand command,
			BindingResult bindingResult, Model model) {

		ompleDadesModel(request, enumeracioId, model, false);
		if (bindingResult.hasErrors()) {
			model.addAttribute("expedientTipusEnumeracioValorCommand", command);
			model.addAttribute("mostraUpdate", true);
			return "expedientTipusEnumeracioValors";
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

			return "expedientTipusEnumeracioValors";
		}
	}

	@RequestMapping(value = "/{enumeracioId}/valor/{id}/delete", method = RequestMethod.GET)
	public String valorDelete(
			HttpServletRequest request,
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			Model model) {
		try {

			if(enumeracioService.valorInUse(id)) {
				ompleDadesModel(request, enumeracioId, model, true);
				MissatgesHelper.error(
						request,
						getMessage(
								request,
								"expedient.tipus.enumeracio.valors.controller.eliminat.us"));
				return "expedientTipusEnumeracioValors";
			}

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
							"expedient.tipus.enumeracio.valors.controller.eliminat.error"));
			logger.error("S'ha produit un error al intentar eliminar el valor del enumerat amb id '" + id + "'", e);
		}

		ompleDadesModel(request, enumeracioId, model, true);
		return "expedientTipusEnumeracioValors";
	}

	@RequestMapping(value = "/{enumeracioId}/valor/new", method = RequestMethod.POST)
	public String valorNouPost(
			HttpServletRequest request,
			@PathVariable Long enumeracioId,
			@Validated(ExpedientTipusEnumeracioValorCommand.Creacio.class) ExpedientTipusEnumeracioValorCommand command,
			BindingResult bindingResult,
			Model model) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("mostraCreate", true);
			ompleDadesModel(request, enumeracioId, model, false);
			model.addAttribute("expedientTipusEnumeracioValorCommand", command);
        	return "expedientTipusEnumeracioValors";
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
			} else {
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
			        	// Valida que no s'importin dos codis
			        	if (valorsCodis.contains(codi))
			        		throw new ValidacioException(
			        				getMessage(
											request,
											"expedient.tipus.enumeracio.valors.importats.duplicat",
											new Object[] {codi}));

		        		valorsCodis.add(codi);
			        	valors.add(enumeracioValors);
					}
					linia = br.readLine();
				}
				for(ExpedientTipusEnumeracioValorDto valor : valors)
					enumeracioService.valorsUpsert(null, enumeracioId, entornActual.getId(), valor);
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"expedient.tipus.enumeracio.valors.importats"));

				if (eliminarValorsAntics) {
					List<String> valorsEnUs = new ArrayList<String>();
					for (ExpedientTipusEnumeracioValorDto valor : enumeracioService.valorsFind(enumeracioId)) {
						if (!valorsCodis.contains(valor.getCodi())) {
							if (enumeracioService.valorInUse(valor.getId())) {
								valorsEnUs.add(valor.getCodi());
							} else {
								enumeracioService.valorDelete(valor.getId());
							}
						}
					}
					if (!valorsEnUs.isEmpty()){
						MissatgesHelper.error(
								request,
								getMessage(
										request,
										"expedient.tipus.enumeracio.valors.importar.eliminar.anterior.en.us",
										new Object[] { valorsEnUs }));
					}
				}
			}
		} catch (InUseException ex) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.controller.action.us"));
        } catch (Exception ex) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.importats.error", new Object[] {ex.getMessage()}));
        }
    	return valors(request, enumeracioId, model);
	}


	@RequestMapping(value = "/{enumeracioId}/valor/exportar", method = RequestMethod.GET)
	@ResponseBody
	public void exportar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long enumeracioId) throws Exception {

        	try {
    			EnumeracioDto enumeracio = enumeracioService.findAmbId(null, enumeracioId);
        		List<ExpedientTipusEnumeracioValorDto> enumeracioValors = enumeracioService.valorsFind(enumeracioId);

        		String estatsString = "";
        		for(ExpedientTipusEnumeracioValorDto enumeracioValor: enumeracioValors){
        			estatsString +=
        					enumeracioValor.getCodi()+";"+enumeracioValor.getNom()+";"+enumeracioValor.getOrdre()+"\n";
        		}

        		MissatgesHelper.success(
    					request,
    					getMessage(
    							request,
    							"expedient.tipus.enumeracio.valors.exportats"));

        		response.setHeader("Pragma", "");
        		response.setHeader("Expires", "");
        		response.setHeader("Cache-Control", "");
        		response.setHeader("Content-Disposition", "attachment; filename=\""
        				+ enumeracio.getCodi() + ".csv" + "\"");
        		response.setContentType("text/plain");
        		response.getOutputStream().write(estatsString.getBytes());
        	} catch(Exception e) {
        		logger.error("Error exportant valors per l'enumeració amb id " + enumeracioId, e);
        		MissatgesHelper.error(
        				request,
        				getMessage(
        						request,
        						"expedient.tipus.enumeracio.valors.exportats.error",
        						new Object[]{e.getLocalizedMessage()}));
        		throw(e);
        	}
	}

	private boolean valorExisteix(Long expedientTipusId, Long enumeracioId, String codi) {
		ExpedientTipusEnumeracioValorDto valor = enumeracioService.valorFindAmbCodi(expedientTipusId, enumeracioId, codi);
		return valor != null;
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
