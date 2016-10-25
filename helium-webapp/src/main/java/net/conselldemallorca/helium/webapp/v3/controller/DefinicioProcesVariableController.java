package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

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

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.ValidacioDto;
import net.conselldemallorca.helium.v3.core.api.service.CampService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.v3.core.api.service.ValidacioService;
import net.conselldemallorca.helium.webapp.v3.command.AgrupacioCommand;
import net.conselldemallorca.helium.webapp.v3.command.CampCommand;
import net.conselldemallorca.helium.webapp.v3.command.ValidacioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pestanya de tasques del disseny de les definicions de
 * procés.
 *
 */
@Controller(value = "definicioProcesVariableControllerV3")
@RequestMapping("/v3/definicioProces")
public class DefinicioProcesVariableController extends BaseDefinicioProcesController {

	@Autowired
	protected CampService campService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	protected ExpedientTipusService expedientTipusService;
	@Autowired
	private ValidacioService validacioService;

	/** Pipella del tasques. */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable")
	public String variable(
			HttpServletRequest request, 
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId, 
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioDefinicioProcesPerPipelles(request, jbmpKey, model, "variables");
		}

		omplirModelVariablesPestanya(request, definicioProcesId, model);

		model.addAttribute("jbpmKey", jbmpKey);
		model.addAttribute("definicioProcesId", definicioProcesId);

		return "v3/expedientTipusVariable";
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable/datatable")
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request, 
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId, 
			@RequestParam(required = false) Long agrupacioId, 
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request, 
				null,
				campService.findPerDatatable(
						null, 
						definicioProcesId, 
						agrupacioId,
						paginacioParams.getFiltre(), 
						paginacioParams),
				"id");
	}
	
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@RequestParam(required = false) Long agrupacioId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		CampCommand command = new CampCommand();
		command.setAgrupacioId(agrupacioId);
		command.setDefinicioProcesId(definicioProcesId);
		model.addAttribute("campCommand", command);
		this.omplirModelVariableForm(
				request, 
				entornActual.getId(),
				definicioProcesId, 
				model);
		return "v3/expedientTipusVariableForm";
	}
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@Validated(CampCommand.Creacio.class) CampCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
    		this.omplirModelVariableForm(
    				request, 
    				entornActual.getId(),
    				definicioProcesId, 
    				model);
        	return "v3/expedientTipusVariableForm";
        } else {
        	// Verificar permisos
    		campService.create(
    				null,
    				definicioProcesId,
        			CampCommand.asCampDto(command));    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.camp.controller.creat"));
			return modalUrlTancar(false);			
        }
	}
	
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		CampDto dto = campService.findAmbId(id);
		CampCommand command = conversioTipusHelper.convertir(
				dto,
				CampCommand.class);
		command.setAgrupacioId(dto.getAgrupacio() != null? dto.getAgrupacio().getId() : null);
		command.setEnumeracioId(dto.getEnumeracio() != null? dto.getEnumeracio().getId() : null);
		command.setDominiId(dto.getDomini() != null? dto.getDomini().getId() : null);
		command.setConsultaId(dto.getConsulta() != null? dto.getConsulta().getId() : null);
		
		model.addAttribute("campCommand", command);
		this.omplirModelVariableForm(
				request, 
				entornActual.getId(),
				definicioProcesId, 
				model);
		return "v3/expedientTipusVariableForm";
	}
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id,
			@Validated(CampCommand.Modificacio.class) CampCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
    		this.omplirModelVariableForm(
    				request, 
    				entornActual.getId(),
    				definicioProcesId, 
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
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable/{id}/moure/{posicio}")
	@ResponseBody
	public boolean mourePosicio(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id,
			@PathVariable int posicio) {
		
		return campService.mourePosicio(id, posicio);
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable/{id}/agrupar/{agrupacioId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean agrupar(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id,
			@PathVariable Long agrupacioId,
			Model model) {
		
		return campService.afegirAgrupacio(id, agrupacioId);
	}
	
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable/{id}/desagrupar", method = RequestMethod.GET)
	@ResponseBody
	public boolean desagrupar(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id,
			Model model) {
		
		return campService.remoureAgrupacio(id);
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id,
			Model model) {
		
		// Valida que la variable no s'utilitzi en cap registre o consulta
		try {
			campService.delete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.camp.llistat.accio.esborrar.correcte"));
			return true;
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.camp.llistat.accio.esborrar.error"));
			logger.error("S'ha produit un error al intentar eliminar la variable amb id '" + id + "' de la definicio de procés amb id '" + definicioProcesId, e);
			return false;
		}
	}
	
	// Mètodes pel manteniment de validacions de variables
	
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable/{campId}/validacio", method = RequestMethod.GET)
	public String validacions(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long campId,
			Model model) {
		
		model.addAttribute("basicUrl", ("definicioProces" + "/" + jbmpKey + "/" + definicioProcesId.toString()));
		model.addAttribute("definicioProcesId", definicioProcesId);
		model.addAttribute("camp", campService.findAmbId(campId));

		ValidacioCommand command = new ValidacioCommand();
		command.setDefinicioProcesId(definicioProcesId);
		command.setCampId(campId);
		model.addAttribute("validacioCommand", command);

		return "v3/expedientTipusValidacio";
	}	
	
	@RequestMapping(value="/{jbmpKey}/{definicioProcesId}/variable/{campId}/validacio/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse validacioDatatable(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
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
	
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable/{campId}/validacio/new", method = RequestMethod.POST)
	public String validacioNovaPost(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long campId,
			@Validated(ValidacioCommand.Creacio.class) ValidacioCommand command,
			BindingResult bindingResult,
			Model model) {
		model.addAttribute("camp", campService.findAmbId(campId));
        if (bindingResult.hasErrors()) {
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
        	return validacions(request, jbmpKey, definicioProcesId, campId, model);
        }
	}	
	
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable/{campId}/validacio/{id}/update", method = RequestMethod.POST)
	public String validacioModificarPost(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long campId,
			@PathVariable Long id,
			@Validated(ValidacioCommand.Modificacio.class) ValidacioCommand command,
			BindingResult bindingResult,
			Model model) {
		model.addAttribute("camp", campService.findAmbId(campId));
        if (bindingResult.hasErrors()) {
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
			return validacions(request, jbmpKey, definicioProcesId, campId, model);
        }
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable/{campId}/validacio/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean validacioDelete(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
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
			logger.error("S'ha produit un error al intentar eliminar la validació amb id '" + id + "' de la definició de procés amb id '" + definicioProcesId, e);
			return false;
		}
	}
	
	/**
	 * Mètode Ajax per moure una validació d'una variable de posició dins la seva agrupació.
	 * @param request
	 * @param expedientTipusId
	 * @param id
	 * @param posicio
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variable/{campId}/validacio/{id}/moure/{posicio}")
	@ResponseBody
	public boolean validacioMourePosicio(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long campId,
			@PathVariable Long id,
			@PathVariable int posicio) {
		
		return validacioService.validacioMourePosicio(id, posicio);
	}
	
	private void omplirModelVariableForm(
			HttpServletRequest request,
			Long entornId,
			Long definicioProcesId,
			Model model) {
		
		// TipusCamp
		List<ParellaCodiValorDto> tipusCamp = new ArrayList<ParellaCodiValorDto>();
		for (CampTipusDto campTipus : CampTipusDto.values()) {
			tipusCamp.add(new ParellaCodiValorDto(campTipus.toString(), campTipus));
		}
		model.addAttribute("tipusCamp",tipusCamp);
		
		// Agrupacions
		this.omplirModelAgrupacions(
				request, 
				definicioProcesId, 
				model);
		
		DefinicioProcesDto definicioProces = definicioProcesService.findAmbIdAndEntorn(entornId,
				definicioProcesId);
		model.addAttribute("definicioProces", definicioProces);
		
		if (definicioProces.getExpedientTipus() != null) {
			Long expedientTipusId = definicioProces.getExpedientTipus().getId();
			model.addAttribute("enumeracions", expedientTipusService.enumeracioFindAll(expedientTipusId));
			model.addAttribute("dominis", expedientTipusService.dominiFindAll(expedientTipusId));
			model.addAttribute("consultes", expedientTipusService.consultaFindAll(expedientTipusId));
		} else {
			model.addAttribute("enumeracions", definicioProcesService.enumeracioFindByEntorn(entornId));
			model.addAttribute("dominis", definicioProcesService.dominiFindByEntorn(entornId));
			model.addAttribute("consultes", definicioProcesService.consultaFindByEntorn(entornId));
		}
	}

	private void omplirModelVariablesPestanya(HttpServletRequest request, Long definicioProcesId, Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			DefinicioProcesDto definicioProces = definicioProcesService.findAmbIdAndEntorn(entornActual.getId(),
					definicioProcesId);
			model.addAttribute("definicioProces", definicioProces);
			model.addAttribute("baseUrl", (definicioProces.getJbpmKey() + "/" + definicioProces.getId().toString()));
		}
		this.omplirModelAgrupacions(request, definicioProcesId, model);
	}

	private void omplirModelAgrupacions(HttpServletRequest request, Long definicioProcesId, Model model) {
		model.addAttribute("agrupacions", obtenirParellesAgrupacions(definicioProcesId));
	}

	private List<ParellaCodiValorDto> obtenirParellesAgrupacions(Long definicioProcesId) {
		List<CampAgrupacioDto> agrupacions = campService.agrupacioFindAll(null, definicioProcesId);
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (CampAgrupacioDto agrupacio : agrupacions) {
			resposta.add(new ParellaCodiValorDto(agrupacio.getId().toString(), agrupacio.getNom()));
		}
		return resposta;
	}

	// Mètodes pel manteniment d'agrupacions

	/** Obre una modal amb un llistat per reordenar les agrupacions. */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/agrupacio", method = RequestMethod.GET)
	public String agrupacions(
			HttpServletRequest request, 
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			Model model) {

		model.addAttribute("definicioProcesId", definicioProcesId);
		model.addAttribute("baseUrl", "definicioProces/" + jbmpKey + "/" + definicioProcesId);

		return "v3/expedientTipusAgrupacio";
	}

	/** Mètode per obtenir les agrupacions per al select. */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/agrupacio/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> agrupacionsSelect(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			Model model) {
		return obtenirParellesAgrupacions(definicioProcesId);
	}

	/** Obre una modal amb un llistat per reordenar les agrupacions. */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/agrupacio/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse agrupacioDatatable(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			Model model) {

		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, null, 
				campService.agrupacioFindPerDatatable(
						null,
						definicioProcesId, 
						paginacioParams.getFiltre(), 
						paginacioParams), 
				"id");

	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/agrupacio/new", method = RequestMethod.GET)
	public String agrupacioNova(
			HttpServletRequest request, 
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId, 
			Model model) {
		AgrupacioCommand command = new AgrupacioCommand();
		command.setDefinicioProcesId(definicioProcesId);
		model.addAttribute("agrupacioCommand", command);
		return "v3/expedientTipusAgrupacioForm";
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/agrupacio/new", method = RequestMethod.POST)
	public String agrupacioNovaPost(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@Validated(AgrupacioCommand.Creacio.class) AgrupacioCommand command,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return "v3/expedientTipusAgrupacioForm";
		} else {
			// Verificar permisos
			campService.agrupacioCreate(
					null,
					definicioProcesId,
					conversioTipusHelper.convertir(command, CampAgrupacioDto.class));
			MissatgesHelper.success(request, getMessage(request, "expedient.tipus.campAgrupacio.controller.creat"));
			return modalUrlTancar(false);
		}
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/agrupacio/{id}/update", method = RequestMethod.GET)
	public String agrupacioModificar(
			HttpServletRequest request, 
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id, 
			Model model) {
		CampAgrupacioDto dto = campService.agrupacioFindAmbId(id);
		AgrupacioCommand command = conversioTipusHelper.convertir(dto,
				AgrupacioCommand.class);
		model.addAttribute("agrupacioCommand", command);
		return "v3/expedientTipusAgrupacioForm";
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/agrupacio/{id}/update", method = RequestMethod.POST)
	public String agrupacioModificarPost(
			HttpServletRequest request, 
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id,
			@Validated(AgrupacioCommand.Modificacio.class) AgrupacioCommand command,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return "v3/expedientTipusAgrupacioForm";
		} else {
			campService.agrupacioUpdate(conversioTipusHelper.convertir(command, CampAgrupacioDto.class));
			MissatgesHelper.success(request, getMessage(request, "expedient.tipus.campAgrupacio.controller.modificat"));
			return modalUrlTancar(false);
		}
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/agrupacio/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean agrupacioDelete(
			HttpServletRequest request, 
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id, Model model) {

		try {
			campService.agrupacioDelete(id);

			MissatgesHelper.success(request,
					getMessage(request, "expedient.tipus.camp.llistat.agrupacio.boto.esborrar.correcte"));
			return true;
		} catch (Exception e) {
			MissatgesHelper.error(request,
					getMessage(request, "expedient.tipus.camp.llistat.agrupacio.boto.esborrar.error"));
			logger.error("S'ha produit un error al intentar eliminar la agrupació amb id '" + id
					+ "' de la definició de procés amb id '" + definicioProcesId, e);
			return false;
		}
	}

	/**
	 * Mètode Ajax per moure una agrupació de posició dins de la definicio de
	 * procés
	 * 
	 * @param request
	 * @param expedientTipusId
	 * @param id
	 * @param posicio
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/agrupacio/{id}/moure/{posicio}")
	@ResponseBody
	public boolean agrupacioMourePosicio(
			HttpServletRequest request, 
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id, @PathVariable int posicio) {

		return campService.agrupacioMourePosicio(id, posicio);
	}

	private static final Log logger = LogFactory.getLog(DefinicioProcesVariableController.class);
}
