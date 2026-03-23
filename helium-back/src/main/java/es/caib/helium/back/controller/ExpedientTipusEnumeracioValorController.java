package es.caib.helium.back.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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

import es.caib.helium.back.command.ExpedientTipusEnumeracioValorCommand;
import es.caib.helium.back.helper.ConversioTipus;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.EnumeracioDto;
import es.caib.helium.commons.dto.ExpedientTipusDto;
import es.caib.helium.commons.dto.ExpedientTipusEnumeracioValorDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.exception.InUseException;
import es.caib.helium.commons.exception.ValidacioException;
import es.caib.helium.logic.intf.service.EnumeracioService;

/**
 * Controlador per a la pipella de variables del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "expedientTipusEnumeracioValorControllerV3")
@RequestMapping("/expedientTipus")
public class ExpedientTipusEnumeracioValorController extends BaseExpedientTipusController {
	
	@Autowired
	protected EnumeracioService enumeracioService;
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valors", method = RequestMethod.GET)
	public String valors(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			Model model) {
		
		ompleDadesModel(request, expedientTipusId, enumeracioId, model, true);

		return "expedientTipusEnumeracioValors";
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
		ExpedientTipusEnumeracioValorCommand command = ConversioTipus.convertir(dto, ExpedientTipusEnumeracioValorCommand.class);
		ompleDadesModel(request, expedientTipusId, enumeracioId, model, false);
		model.addAttribute("expedientTipusEnumeracioValorCommand", command);
		model.addAttribute("mostraUpdate", true);
		//model.addAttribute("inUse", enumeracioService.valorInUse(id));
		return "expedientTipusEnumeracioValors";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/{id}/update", method = RequestMethod.POST)
	public String modificaPost(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			@Validated(ExpedientTipusEnumeracioValorCommand.Modificacio.class) ExpedientTipusEnumeracioValorCommand command,
			BindingResult bindingResult, Model model) {

		ompleDadesModel(request, expedientTipusId, enumeracioId, model, false);		
		if (bindingResult.hasErrors()) {
			model.addAttribute("expedientTipusEnumeracioValorCommand", command);
			model.addAttribute("mostraUpdate", true);
			return "expedientTipusEnumeracioValors";
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
			
			return "expedientTipusEnumeracioValors";
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
			
			if(enumeracioService.valorInUse(id)) {
				ompleDadesModel(request, expedientTipusId, enumeracioId, model, true);
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
							"expedient.tipus.enumeracio.valors.controller.eliminat.error"),
					e);
			logger.error("S'ha produit un error al intentar eliminar el valor del enumerat amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId, e);
		}
		
		ompleDadesModel(request, expedientTipusId, enumeracioId, model, true);
		return "expedientTipusEnumeracioValors";
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
        	return "expedientTipusEnumeracioValors";
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
					enumeracioService.valorsUpsert(expedientTipusId, enumeracioId, entornActual.getId(), valor);
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
        } catch (ValidacioException ex) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.importats.error", new Object[] {ex.getMessage()}));
		} catch (Exception ex) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.importats.error", new Object[] {ex.getMessage()}),
					ex);
		}
		return valors(request, expedientTipusId, enumeracioId, model);
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