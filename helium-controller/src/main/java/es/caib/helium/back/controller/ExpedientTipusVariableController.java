/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.AgrupacioCommand;
import es.caib.helium.back.command.CampCommand;
import es.caib.helium.back.command.ExpedientTipusCampRegistreCommand;
import es.caib.helium.back.command.ValidacioCommand;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.NodecoHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.client.model.ParellaCodiValor;
import es.caib.helium.logic.intf.dto.CampAgrupacioDto;
import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.CampRegistreDto;
import es.caib.helium.logic.intf.dto.CampTipusDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.ValidacioDto;
import es.caib.helium.logic.intf.service.ValidacioService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Controlador per a la pipella de variables del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusVariableController extends BaseVariableController {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ValidacioService validacioService;

	@RequestMapping(value = "/{expedientTipusId}/variables")
	public String variables(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"variables");
		}
		omplirModelVariablesPestanya(
				request,
				expedientTipusId,
				model);
		return "v3/expedientTipusVariable";
	}
	
	/** Retorna les dades de les variables pel datatables de variables. Es pot filtrar per agrupació
	 * amb la particularitat que si la agrupacioId és -2 llavors s'han de mostrar totes les variables i
	 * si és -1 s'han de mostrar les variables sense agrupació.
	 * @param request
	 * @param expedientTipusId
	 * @param agrupacioId
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/{expedientTipusId}/variable/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(required = false) Long agrupacioId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		if (agrupacioId == null)
			agrupacioId = AGRUPACIO_TOTES;
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				campService.findPerDatatable(
						expedientTipusId,
						null,
						agrupacioId == AGRUPACIO_TOTES,
						agrupacioId >= 0L ? agrupacioId : null,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}	
			
	@RequestMapping(value = "/{expedientTipusId}/variable/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(required = false) Long agrupacioId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		CampCommand command = new CampCommand();
		command.setAgrupacioId(agrupacioId);
		command.setExpedientTipusId(expedientTipusId);
		command.setDominiCacheText(true);
		model.addAttribute("campCommand", command);
		this.omplirModelVariableForm(
				request, 
				entornActual.getId(),
				expedientTipusId, 
				model);
		return "v3/expedientTipusVariableForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/variable/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(CampCommand.Creacio.class) CampCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
    		this.omplirModelVariableForm(
    				request, 
    				entornActual.getId(),
    				expedientTipusId, 
    				model);
        	return "v3/expedientTipusVariableForm";
        } else {
        	// Verificar permisos
    		campService.create(
    				expedientTipusId,
    				null,
        			CampCommand.asCampDto(command));    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.camp.controller.creat"));
			return modalUrlTancar(false);			
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/variable/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		CampDto dto = campService.findAmbId(expedientTipusId, id);
		CampCommand command = conversioTipusHelper.convertir(
				dto,
				CampCommand.class);
		command.setAgrupacioId(dto.getAgrupacio() != null? dto.getAgrupacio().getId() : null);
		command.setEnumeracioId(dto.getEnumeracio() != null? dto.getEnumeracio().getId() : null);
		command.setDominiId(dto.getDomini() != null? dto.getDomini().getId() : null);
		command.setConsultaId(dto.getConsulta() != null? dto.getConsulta().getId() : null);
		model.addAttribute("heretat", dto.isHeretat());

		model.addAttribute("campCommand", command);
		this.omplirModelVariableForm(
				request, 
				entornActual.getId(),
				expedientTipusId, 
				model);
		return "v3/expedientTipusVariableForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/variable/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(CampCommand.Modificacio.class) CampCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
    		model.addAttribute("heretat", campService.findAmbId(expedientTipusId, id));
    		this.omplirModelVariableForm(
    				request, 
    				entornActual.getId(),
    				expedientTipusId, 
    				model);
        	return "v3/expedientTipusVariableForm";
        } else {
        	campService.update(
        			CampCommand.asCampDto(command));
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.camp.controller.modificat"));
			return modalUrlTancar(false);
        }
	}
	
	
	/**
	 * Mètode Ajax per moure una variable de posició dins la seva agrupació.
	 * @param request
	 * @param expedientTipusId
	 * @param id
	 * @param posicio
	 * @return
	 */
	@RequestMapping(value = "/{expedientTipusId}/variable/{id}/moure/{posicio}")
	@ResponseBody
	public boolean mourePosicio(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@PathVariable int posicio) {
		
		return campService.mourePosicio(id, posicio);
	}

	@RequestMapping(value = "/{expedientTipusId}/variable/{id}/agrupar/{agrupacioId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean agrupar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@PathVariable Long agrupacioId,
			Model model) {
		
		return campService.afegirAgrupacio(id, agrupacioId);
	}
	
	@RequestMapping(value = "/{expedientTipusId}/variable/{id}/desagrupar", method = RequestMethod.GET)
	@ResponseBody
	public boolean desagrupar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		
		return campService.remoureAgrupacio(id);
	}

	@RequestMapping(value = "/{expedientTipusId}/variable/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		
		if (!validaEsborratCamp(request, expedientTipusId, id)) {
			return false;
		}
		try {
			// Esborra la variable
			campService.delete(id);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.camp.llistat.accio.esborrar.correcte"));
			return true;
		} catch(Exception e) {
			logger.error("S'ha produit un error al intentar eliminar la variable amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId, e);
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.camp.llistat.accio.esborrar.error", 
							new Object[] {e.getMessage()}));
		}
		return false;
	}
	
	// Mètodes pel manteniment d'agrupacions

	/** Obre una modal amb un llistat per reordenar les agrupacions. */
	@RequestMapping(value = "/{expedientTipusId}/agrupacio", method = RequestMethod.GET)
	public String agrupacions(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {

		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("baseUrl", "expedientTipus/" + expedientTipusId);
		
		return "v3/expedientTipusAgrupacio";
	}
	
	/** Mètode per obtenir les agrupacions per al select. */
	@RequestMapping(value = "/{expedientTipusId}/agrupacio/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValor> agrupacionsSelect(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		return obtenirParellesAgrupacions(request, expedientTipusId, null, true);
	}

	/** Obre una modal amb un llistat per reordenar les agrupacions. */
	@RequestMapping(value = "/{expedientTipusId}/agrupacio/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse agrupacioDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
				
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				campService.agrupacioFindPerDatatable(
						expedientTipusId,
						null,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
		
	}
	
	@RequestMapping(value = "/{expedientTipusId}/agrupacio/new", method = RequestMethod.GET)
	public String agrupacioNova(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		AgrupacioCommand command = new AgrupacioCommand();
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("agrupacioCommand", command);
		return "v3/expedientTipusAgrupacioForm";
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/agrupacio/new", method = RequestMethod.POST)
	public String agrupacioNovaPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(AgrupacioCommand.Creacio.class) AgrupacioCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/expedientTipusAgrupacioForm";
        } else {
        	// Verificar permisos
    		campService.agrupacioCreate(
    				expedientTipusId,
    				null,
    				conversioTipusHelper.convertir(
    						command,
    						CampAgrupacioDto.class));    	
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.campAgrupacio.controller.creat"));
			return modalUrlTancar(false);
        }
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/agrupacio/{id}/update", method = RequestMethod.GET)
	public String agrupacioModificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		CampAgrupacioDto dto = campService.agrupacioFindAmbId(id);
		AgrupacioCommand command = conversioTipusHelper.convertir(
				dto,
				AgrupacioCommand.class);
		model.addAttribute("agrupacioCommand", command);
		return "v3/expedientTipusAgrupacioForm";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/agrupacio/{id}/update", method = RequestMethod.POST)
	public String agrupacioModificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(AgrupacioCommand.Modificacio.class) AgrupacioCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/expedientTipusAgrupacioForm";
        } else {
        	campService.agrupacioUpdate(
        			conversioTipusHelper.convertir(
    						command,
    						CampAgrupacioDto.class));
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.campAgrupacio.controller.modificat"));
			return modalUrlTancar(false);		
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/agrupacio/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean agrupacioDelete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		
		try {
			campService.agrupacioDelete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.camp.llistat.agrupacio.boto.esborrar.correcte"));
			return true;
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.camp.llistat.agrupacio.boto.esborrar.error"));
			logger.error("S'ha produit un error al intentar eliminar la agrupació amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId, e);
			return false;
		}
	}
	/**
	 * Mètode Ajax per moure una agrupació de posició dins del tipus d'expedient.
	 * @param request
	 * @param expedientTipusId
	 * @param id
	 * @param posicio
	 * @return
	 */
	@RequestMapping(value = "/{expedientTipusId}/agrupacio/{id}/moure/{posicio}")
	@ResponseBody
	public boolean agrupacioMourePosicio(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@PathVariable int posicio) {
		
		return campService.agrupacioMourePosicio(id, posicio);
	}		

	// Mètodes pel manteniment de validacions de variables
	
	@RequestMapping(value = "/{expedientTipusId}/variable/{campId}/validacio", method = RequestMethod.GET)
	public String validacions(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long campId,
			Model model) {
		
		omplirModelValidacionsForm(expedientTipusId, campId, model);

		ValidacioCommand command = new ValidacioCommand();
		command.setExpedientTipusId(expedientTipusId);
		command.setCampId(campId);
		model.addAttribute("validacioCommand", command);

		return "v3/expedientTipusValidacio";
	}	
	
	@RequestMapping(value="/{expedientTipusId}/variable/{campId}/validacio/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse validacioDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long campId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				validacioService.validacioFindPerDatatable(
						campId,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}		
	
	@RequestMapping(value = "/{expedientTipusId}/variable/{campId}/validacio/new", method = RequestMethod.POST)
	public String validacioNovaPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long campId,
			@Validated(ValidacioCommand.Creacio.class) ValidacioCommand command,
			BindingResult bindingResult,
			Model model) {
		model.addAttribute("camp", campService.findAmbId(expedientTipusId, campId));
        if (bindingResult.hasErrors()) {
    		omplirModelValidacionsForm(expedientTipusId, campId, model);
        	model.addAttribute("mostraCreate", true);
        	return "v3/expedientTipusValidacio";
        } else {
        	// Verificar permisos
        	validacioService.validacioCreate(
    				campId,
    				conversioTipusHelper.convertir(
    						command,
    						ValidacioDto.class));    		
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.campValidacio.controller.creat"));
        	return validacions(request, expedientTipusId, campId, model);
        }
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/variable/{campId}/validacio/{id}/update", method = RequestMethod.POST)
	public String validacioModificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long campId,
			@PathVariable Long id,
			@Validated(ValidacioCommand.Modificacio.class) ValidacioCommand command,
			BindingResult bindingResult,
			Model model) {
		model.addAttribute("camp", campService.findAmbId(expedientTipusId, campId));
        if (bindingResult.hasErrors()) {
    		omplirModelValidacionsForm(expedientTipusId, campId, model);
        	model.addAttribute("mostraUpdate", true);
        	return "v3/expedientTipusValidacio";
        } else {
        	validacioService.validacioUpdate(
        			conversioTipusHelper.convertir(
    						command,
    						ValidacioDto.class));
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.campValidacio.controller.modificat"));
        	return validacions(request, expedientTipusId, campId, model);
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/variable/{campId}/validacio/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean validacioDelete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long campId,
			@PathVariable Long id,
			Model model) {
				
		try {
			validacioService.validacioDelete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.campValidacio.controller.eliminar.success"));			
			return true;
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.campValidacio.controller.eliminar.error"));
			logger.error("S'ha produit un error al intentar eliminar la validació amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId, e);
			return false;
		}
	}
	
	/**
	 * Mètode Ajax per moure una validació d'una variable de posició dins la seva agrupació.
	 * @param request
	 * @param expedientTipusId
	 * @param id
	 * @param posicio
	 * @return
	 */
	@RequestMapping(value = "/{expedientTipusId}/variable/{campId}/validacio/{id}/moure/{posicio}")
	@ResponseBody
	public boolean validacioMourePosicio(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long campId,
			@PathVariable Long id,
			@PathVariable int posicio) {
		
		return validacioService.validacioMourePosicio(id, posicio);
	}	
	
	private void omplirModelValidacionsForm(
			Long expedientTipusId,
			Long campId,
			Model model) {
		model.addAttribute("basicUrl", "expedientTipus/" + expedientTipusId);
		model.addAttribute("expedientTipusId", expedientTipusId);
		CampDto camp = campService.findAmbId(expedientTipusId, campId);
		model.addAttribute("camp", camp);
		model.addAttribute("heretat", camp.isHeretat());		
	}
	
	private void omplirModelVariablesPestanya(
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
			// agrupacions heretades
			List<Long> agrupacionsHeretadesIds = new ArrayList<Long>();
			List<Long> agrupacionsSobreescriuenIds = new ArrayList<Long>();
			if (expedientTipus.getExpedientTipusPareId() != null) {
				List<CampAgrupacioDto> agrupacions = campService.agrupacioFindAll(expedientTipusId, null, true);
				for (CampAgrupacioDto a : agrupacions) {
					if (a.isHeretat())
						agrupacionsHeretadesIds.add(a.getId());
					if (a.isSobreescriu())
						agrupacionsSobreescriuenIds.add(a.getId());
				}
			}
			model.addAttribute("agrupacionsHeretadesIds", agrupacionsHeretadesIds);
			model.addAttribute("agrupacionsSobreescriuenIds", agrupacionsSobreescriuenIds);
		}
		this.omplirModelAgrupacions(
				request, 
				expedientTipusId, 
				null,
				model,
				true);
	}
	
	private void omplirModelVariableForm(
			HttpServletRequest request,
			Long entornId,
			Long expedientTipusId,
			Model model) {
		
		// TipusCamp
		List<ParellaCodiValor> tipusCamp = new ArrayList<ParellaCodiValor>();
		for (CampTipusDto campTipus : CampTipusDto.values()) {
			tipusCamp.add(new ParellaCodiValor(campTipus.toString(), campTipus));
		}
		model.addAttribute("tipusCamp",tipusCamp);
		
		// Agrupacions
		this.omplirModelAgrupacions(
				request, 
				expedientTipusId,
				null,
				model,
				false);
		
		// Enumeracions
		model.addAttribute("enumeracions", expedientTipusService.enumeracioFindAll(expedientTipusId, true));
		
		// Dominis
		model.addAttribute("dominis", expedientTipusService.dominiFindAll(entornId, expedientTipusId, true));
		
		// Consultes
		model.addAttribute("consultes", expedientTipusService.consultaFindAll(expedientTipusId));
		
		//Accions
		model.addAttribute("definicionsProces", 
				expedientTipusService.definicioProcesFindJbjmKey(
						entornId, 
						expedientTipusId, 
						true,
						true));
	}
		
	// Mètodes pel manteniment dels camps de variables de tipus registre
	
	@RequestMapping(value = "/{expedientTipusId}/variable/{campId}/campRegistre", method = RequestMethod.GET)
	public String campsRegistre(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long campId,
			Model model) {
		
		omplirModelCampsRegistreForm(expedientTipusId, campId, model);

		ExpedientTipusCampRegistreCommand command = new ExpedientTipusCampRegistreCommand();
		command.setExpedientTipusId(expedientTipusId);
		command.setRegistreId(campId);
		command.setObligatori(true);
		command.setLlistar(true);
		model.addAttribute("expedientTipusCampRegistreCommand", command);
		model.addAttribute("variables", new ArrayList<ParellaCodiValor>());		

		return "v3/expedientTipusCampRegistre";
	}	
	
	@RequestMapping(value="/{expedientTipusId}/variable/{campId}/campRegistre/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse campRegistreDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long campId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				campService.registreFindPerDatatable(
						campId,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}		
	
	@RequestMapping(value = "/{expedientTipusId}/variable/{campId}/campRegistre/new", method = RequestMethod.POST)
	public String campRegistreNouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long campId,
			@Validated(ExpedientTipusCampRegistreCommand.Creacio.class) ExpedientTipusCampRegistreCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	omplirModelCampsRegistreForm(expedientTipusId, campId, model);
    		model.addAttribute("variables", obtenirParellesCampRegistre(
    				expedientTipusId,
    				campId, 
    				null));		
        	model.addAttribute("mostraCreate", true);
        	return "v3/expedientTipusCampRegistre";
        } else {
        	// Verificar permisos
    		campService.registreCreate(
    				campId,
    				conversioTipusHelper.convertir(
    						command,
    						CampRegistreDto.class));    		
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.campRegistre.controller.creat"));
        	return campsRegistre(request, expedientTipusId, campId, model);
        }
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/variable/{campId}/campRegistre/{id}/update", method = RequestMethod.POST)
	public String campRegistreModificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long campId,
			@PathVariable Long id,
			@Validated(ExpedientTipusCampRegistreCommand.Modificacio.class) ExpedientTipusCampRegistreCommand command,
			BindingResult bindingResult,
			Model model) {
		model.addAttribute("camp", campService.findAmbId(expedientTipusId, campId));
        if (bindingResult.hasErrors()) {
        	omplirModelCampsRegistreForm(expedientTipusId, campId, model);
    		model.addAttribute("variables", obtenirParellesCampRegistre(
    				expedientTipusId,
    				campId, 
    				command.getMembreId()));		
        	model.addAttribute("mostraUpdate", true);
        	return "v3/expedientTipusCampRegistre";
        } else {
        	campService.registreUpdate(
        			conversioTipusHelper.convertir(
    						command,
    						CampRegistreDto.class));
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.campRegistre.controller.modificat"));
        	return campsRegistre(request, expedientTipusId, campId, model);
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/variable/{campId}/campRegistre/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean campRegistreDelete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long campId,
			@PathVariable Long id,
			Model model) {
				
		try {
			campService.registreDelete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.campRegistre.controller.eliminar.success"));			
			return true;
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.campRegistre.controller.eliminar.error"));
			logger.error("S'ha produit un error al intentar eliminar el camp registre amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId + "'", e);
			return false;
		}
	}
	
	/**
	 * Mètode Ajax per moure una validació d'una variable de posició dins la seva agrupació.
	 * @param request
	 * @param expedientTipusId
	 * @param id
	 * @param posicio
	 * @return
	 */
	@RequestMapping(value = "/{expedientTipusId}/variable/{campId}/campRegistre/{id}/moure/{posicio}")
	@ResponseBody
	public boolean campRegistreMourePosicio(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long campId,
			@PathVariable Long id,
			@PathVariable int posicio) {
		
		return campService.registreMourePosicio(id, posicio);
	}	
	
	/** Mètode per obtenir les possibles variables per al select a l'edició d'un registre via ajax. */
	@RequestMapping(value = "/{expedientTipusId}/variable/{campId}/campRegistre/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValor> campRegistreSelect(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long campId,
			@RequestParam(required = false) Long membreId,
			Model model) {
		return obtenirParellesCampRegistre(expedientTipusId, campId, membreId);
	}	
	
	private void omplirModelCampsRegistreForm(
			Long expedientTipusId,
			Long campId,
			Model model) {
		model.addAttribute("basicUrl", "expedientTipus/" + expedientTipusId);
		model.addAttribute("expedientTipusId", expedientTipusId);
		CampDto camp = campService.findAmbId(expedientTipusId, campId);
		model.addAttribute("camp", camp);
		model.addAttribute("heretat", camp.isHeretat());
	}
		
	/**
	 * Retorna les parelles de codi i valor per als registres. Treu els que ja estan seleccionats
	 * i les variables de tipus registre. A més, si està marcat filtrar també
	 * treu la variable del propi registre.
	 * @param expedientTipusId
	 * @param registreId
	 * @return
	 */
	private List<ParellaCodiValor> obtenirParellesCampRegistre(
			Long expedientTipusId,
			Long registreId,
			Long membreId) {
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		// Obté totes les variables del tipus d'expedient
		List<CampDto> variables = campService.findAllOrdenatsPerCodi(expedientTipusId, null);
		// Consulta els camps del registre
		List<CampDto> camps = campService.registreFindMembresAmbRegistreId(registreId);
		Iterator<CampDto> it = variables.iterator();
		while (it.hasNext()) {
			CampDto camp = it.next();
			if ((registreId.equals(camp.getId())) // Treu el propi registre
				|| CampTipusDto.REGISTRE.equals(camp.getTipus()) ) {// Treu les variables tipus registre
				it.remove();
			} else if (membreId == null || !membreId.equals(camp.getId())) {
				// Treu els que ja pertanyen al registre a no ser que sigui el que conicideix amb memberId per als updates
				for (CampDto campRegistre : camps) {
					if (campRegistre.getId().equals(camp.getId())) {
						it.remove();
						break;
					}
				}
			}
		}		
		for (CampDto variable : variables) {
			resposta.add(new ParellaCodiValor(
					variable.getId().toString(), 
					variable.getCodi() + " / " + variable.getEtiqueta()));
		}
		return resposta;
	}	

	private static final Log logger = LogFactory.getLog(ExpedientTipusVariableController.class);
}
