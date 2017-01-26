package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaAnyDto;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ExpedientTipusExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ExpedientTipusExportacioCommandDto;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusExportarCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusExportarCommand.Exportacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusExportarCommand.Importacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusExportarCommand.Upload;
import net.conselldemallorca.helium.webapp.v3.command.PermisCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;

/**
 * Controlador per al manteniment de tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "expedientTipusControllerV3")
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusController extends BaseExpedientTipusController {

	@Autowired
	private ExpedientTipusService expedientTipusService;
	@Autowired
	private ExecucioMassivaService execucioMassivaService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private DissenyService dissenyService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;



	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		return "v3/expedientTipusLlistat";
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
				expedientTipusService.findPerDatatable(
						entornActual.getId(),
						paginacioParams.getFiltre(),
						paginacioParams));
	}	

	@RequestMapping(value = "/{expedientTipusId}", method = RequestMethod.GET)
	public String pipelles(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		return mostrarInformacioExpedientTipusPerPipelles(
				request,
				expedientTipusId,
				model,
				null);
	}
	
	/** Pipella d'informació. */
	@RequestMapping(value = "/{expedientTipusId}/informacio")
	public String informacio(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"informacio");
		}
		omplirModelPestanyaInformacio(
				request,
				expedientTipusId,
				model);		
		return "v3/expedientTipusInformacio";
	}
	private void omplirModelPestanyaInformacio(
			HttpServletRequest request,
			Long expedientTipusId,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			// Responsable per defecte
			if (expedientTipus.getResponsableDefecteCodi() != null) {
				model.addAttribute(
						"responsableDefecte",
						aplicacioService.findPersonaAmbCodi(
								expedientTipus.getResponsableDefecteCodi()));
			}
			model.addAttribute(
					"definicioProcesInicial",
					dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId));
		}
		// Permisos per a les accions
		boolean potEscriure;
		if (entornActual.isPermisDesign()) {
			potEscriure = true;
		} else {
			try {
				expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
						expedientTipusId);
				potEscriure = true;
			} catch (Exception e){
				potEscriure = false;
			}
		}
		model.addAttribute("potEscriure", potEscriure);
		
		boolean potEsborrar;
		if (entornActual.isPermisDesign()) {
			potEsborrar = true;
		} else {
			try {
				expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
						expedientTipusId);
				potEsborrar = true;
			} catch (Exception e){
				potEsborrar = false;
			}
		}		
		model.addAttribute("potEsborrar", potEsborrar);
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			Model model) {
		model.addAttribute("expedientTipusCommand", new ExpedientTipusCommand());
		return "v3/expedientTipusForm";
	}
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@Validated(Creacio.class) ExpedientTipusCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/expedientTipusForm";
        } else {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
    		// Transforma els llistats d'anys i valors 
    		List<Integer> sequenciesAny = new ArrayList<Integer>();
    		List<Long> sequenciesValor = new ArrayList<Long>();
    		for (int i = 0; i < command.getSequenciesAny().size(); i++ ){
    			sequenciesAny.add(Integer.parseInt(command.getSequenciesAny().get(i)));
    			sequenciesValor.add(Long.parseLong(command.getSequenciesValor().get(i)));
    		}
    		expedientTipusService.create(
    				entornActual.getId(),
    				conversioTipusHelper.convertir(
    						command,
    						ExpedientTipusDto.class),
    				sequenciesAny,
        			sequenciesValor);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedientTipus",
					"expedient.tipus.controller.creat");
        }
	}

	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto dto = expedientTipusService.findAmbIdPermisDissenyar(
				entornActual.getId(),
				id);
		ExpedientTipusCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusCommand.class);
		for (Integer key: dto.getSequenciaAny().keySet()) {
			SequenciaAnyDto any = dto.getSequenciaAny().get(key);
			command.getSequenciesAny().add(any.getAny().toString());
			command.getSequenciesValor().add(any.getSequencia().toString());
		}
		model.addAttribute("expedientTipusCommand", command);
		return "v3/expedientTipusForm";
	}
	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long id,
			@Validated(Modificacio.class) ExpedientTipusCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/expedientTipusForm";
        } else {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
    		// Transforma els llistats d'anys i valors 
    		List<Integer> sequenciesAny = new ArrayList<Integer>();
    		List<Long> sequenciesValor = new ArrayList<Long>();
    		for (int i = 0; i < command.getSequenciesAny().size(); i++ ){
    			sequenciesAny.add(Integer.parseInt(command.getSequenciesAny().get(i)));
    			sequenciesValor.add(Long.parseLong(command.getSequenciesValor().get(i)));
    		}
        	expedientTipusService.update(
        			entornActual.getId(),
        			conversioTipusHelper.convertir(
    						command,
    						ExpedientTipusDto.class),
        			sequenciesAny,
        			sequenciesValor);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedientTipus",
					"expedient.tipus.controller.modificat");
        }
	}

	@RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (!expedientService.existsExpedientAmbEntornTipusITitol(
				entornActual.getId(),
				id,
				null)) {
			expedientTipusService.delete(
					entornActual.getId(),
					id);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.controller.eliminat"));
		} else {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.controller.eliminar.expedients.relacionats"));
		}			
		return "redirect:/v3/expedientTipus";
	}
	
	/** Modal per exportar la informació del tipus d'expedient. */
	@RequestMapping(value = "/{expedientTipusId}/exportar", method = RequestMethod.GET)
	public String exportar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto dto = expedientTipusService.findAmbIdPermisDissenyar(
				entornActual.getId(),
				expedientTipusId);

		ExpedientTipusExportarCommand command = new ExpedientTipusExportarCommand();
		command.setId(expedientTipusId);
		command.setEntornId(entornActual.getId());
		command.setIntegracioSistra(true);
		command.setIntegracioForms(true);
		model.addAttribute("inici", true); // per marcar tots els checboxs inicialment
		model.addAttribute("expedientTipus", dto);
		model.addAttribute("command", command);
		this.omplirModelFormulariExportacio(expedientTipusId, model, dto);

		return "v3/expedientTipusExportarForm";
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/exportar", method = RequestMethod.POST)
	public String exportarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@ModelAttribute("command")
			@Validated(Exportacio.class) ExpedientTipusExportarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto dto = expedientTipusService.findAmbIdPermisDissenyar(
				entornActual.getId(),
				expedientTipusId);
		if (bindingResult.hasErrors()) {
			model.addAttribute("expedientTipus", dto);
			model.addAttribute("command", command);
			this.omplirModelFormulariExportacio(expedientTipusId, model, dto);
        	return "v3/expedientTipusExportarForm";
        } else {
			model.addAttribute("filename", dto.getCodi() + ".exp");
			ExpedientTipusExportacio expedientTipusExportacio = 
					expedientTipusService.exportar(
							entornActual.getId(),
							expedientTipusId,
							conversioTipusHelper.convertir(
									command, 
									ExpedientTipusExportacioCommandDto.class));
			model.addAttribute("data", expedientTipusExportacio);
			return "serialitzarView";        	
        }
	}	
	
	private void omplirModelFormulariExportacio(
			Long expedientTipusId,
			Model model, 
			ExpedientTipusDto dto) {
		model.addAttribute("estats", dto.getEstats());
		model.addAttribute("variables", campService.findAllOrdenatsPerCodi(expedientTipusId, null));
		model.addAttribute("agrupacions", campService.agrupacioFindAll(expedientTipusId, null));
		
		// Map<definicioCodi, List<ParellaCodiValorDto>> map amb les versions agrupades per codi jbpm
		Map<String, List<Integer>> versionsMap = new HashMap<String, List<Integer>>();
		// Map<definicioCodi, darrera versió> map amb les darreres versions per codi jbpm
		Map<String, Integer> darreresVersionsMap = new HashMap<String, Integer>();
		List<Integer> versions;
		List<DefinicioProcesDto> definicions = new ArrayList<DefinicioProcesDto>();
		String jbpmKey;
		for (DefinicioProcesDto definicio : 
						expedientTipusService.definicioFindAll(expedientTipusId)) {
			jbpmKey = definicio.getJbpmKey();
			versions = versionsMap.get(jbpmKey);
			if (versions == null) {
				versions =  new ArrayList<Integer>();
				versionsMap.put(jbpmKey, versions);
				definicions.add(definicio);
			}
			versions.add(definicio.getVersio());
			// Guarda el valor de la versió major per a la definició de procés
			if (darreresVersionsMap.get(jbpmKey) == null || definicio.getVersio() > darreresVersionsMap.get(jbpmKey))
				darreresVersionsMap.put(jbpmKey, definicio.getVersio());
		}
		for (List<Integer> v : versionsMap.values())
			Collections.sort(v);
		model.addAttribute("definicions", definicions);		
		model.addAttribute("definicionsVersions", versionsMap);
		model.addAttribute("darreresVersions", darreresVersionsMap);
		model.addAttribute("enumeracions", expedientTipusService.enumeracioFindAll(expedientTipusId, false));
		model.addAttribute("documents", documentService.findAll(expedientTipusId, null));
		model.addAttribute("terminis", terminiService.findAll(expedientTipusId, null));
		model.addAttribute("accions", accioService.findAll(expedientTipusId, null));
		model.addAttribute("dominis", expedientTipusService.dominiFindAll(expedientTipusId, false));
		model.addAttribute("consultes", expedientTipusService.consultaFindAll(expedientTipusId));
	}	
	
	/** Modal per importar la informació del fitxer d'un tipus d'expedient. */
	@RequestMapping(value = "/importar", method = RequestMethod.GET)
	public String importar(
			HttpServletRequest request,
			@RequestParam(required = false) Long expedientTipusId,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = null;
		if (expedientTipusId != null)
			expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
				entornActual.getId(),
				expedientTipusId);

		ExpedientTipusExportarCommand command = new ExpedientTipusExportarCommand();
		command.setId(expedientTipusId);
		model.addAttribute("expedientTipus", expedientTipus);
		model.addAttribute("command", command);

		return "v3/expedientTipusImportarForm";
	}	
	
	/** Carrega el formulari per ajax i mostra les opcions per importar les dades del fitxer importat. */
	@RequestMapping(value = "/importar/upload", method = RequestMethod.POST)
	public String importarUploadPost(
			HttpServletRequest request,
			@Validated(Upload.class)
			@ModelAttribute("command")
			ExpedientTipusExportarCommand command,
			BindingResult bindingResult,
			Model model) {

		// Processament del fitxer fet en el validador ExpedientTipusUploadValidator
		ExpedientTipusExportacio exportacio = command.getExportacio(); 	
		
		if (bindingResult.hasErrors()) {
			// es limitarà a mostrar els errors de validació
		}
		command.setIntegracioSistra(true);
		command.setIntegracioForms(true);
		model.addAttribute("inici", true); // per marcar tots els checboxs inicialment
		model.addAttribute("command", command);	
	 	EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		this.omplirModelFormulariImportacio(entornActual.getId(), command.getId(), exportacio, model);

		return "v3/expedientTipusImportarOpcions";
	}	

	/** Acció d'enviament del fitxer i les opcions sobre les dades de l'expedient d'exportació.
	 * La validació es fa en el <i>ExpedientTipusImportarValidator</i>.
	 * @param request
	 * @param expedientTipusId
	 * @param command
	 * @param bindingResult
	 * @param model
	 * @see net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusImportarValidator
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/importar", method = RequestMethod.POST)
	public String importarPost(
			HttpServletRequest request,
			@Validated(Importacio.class)
			@ModelAttribute("command")
			ExpedientTipusExportarCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		// Processament del fitxer fet en el validador ExpedientTipusImportarValidator
		ExpedientTipusExportacio importacio = command.getExportacio(); 	
	 	
		if (bindingResult.hasErrors()) {
    		model.addAttribute("command", command);	    		
    		this.omplirModelFormulariImportacio(entornActual.getId(), command.getId(), importacio, model);
        	return "v3/expedientTipusImportarOpcions";
        } else {
        	ExpedientTipusDto expedientTipus = expedientTipusService.importar(
        			entornActual.getId(),
        			command.getId(), 
        			conversioTipusHelper.convertir(
							command, 
							ExpedientTipusExportacioCommandDto.class),
        			importacio);
        	
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.importar.form.success"));
    		// Indica que la importació ha finalitzat per no haver de processar més codi
    		model.addAttribute("importacioFinalitzada", true);
        	if (command.getId() != null) 
	    		return modalUrlTancar();
        	else {        		
        		// retorna la redirecció
        		model.addAttribute("redireccioUrl",  request.getContextPath() + "/v3/expedientTipus/" + expedientTipus.getId());
            	return "v3/expedientTipusImportarOpcions";
        	}
        }
	}	
	
	private void omplirModelFormulariImportacio(
			Long entornId,
			Long expedientTipusId,
			ExpedientTipusExportacio exportacio,
			Model model) {
		ExpedientTipusDto dto = null;
		if (expedientTipusId != null) {
			dto = expedientTipusService.findAmbIdPermisDissenyar(
					entornId,
					expedientTipusId);
			model.addAttribute("expedientTipus", dto);
			
			// avisa si el codi de la exportació és diferent del codi al qual s'importa
			if (exportacio != null && ! dto.getCodi().equals(exportacio.getCodi())) {
				model.addAttribute("exportacio", exportacio);
				model.addAttribute("avisImportacioExpedientTipusDiferent", true); 
			}
	 	}
	 	
		// Per indicar a la pàgina si s'ha pogut fer una importació del fitxer.
		model.addAttribute("fitxerImportat", exportacio != null);
		
		if (exportacio != null) {
			model.addAttribute("estats", exportacio.getEstats());
			model.addAttribute("variables", exportacio.getCamps());
			model.addAttribute("agrupacions", exportacio.getAgrupacions());
			List<DefinicioProcesDto> definicions = new ArrayList<DefinicioProcesDto>(); 
			for (DefinicioProcesExportacio definicioExportat : exportacio.getDefinicions()) {
				definicions.add(definicioExportat.getDefinicioProcesDto());
			}
			model.addAttribute("definicions", definicions);		

			model.addAttribute("enumeracions", exportacio.getEnumeracions());
			model.addAttribute("documents", exportacio.getDocuments());
			model.addAttribute("terminis", exportacio.getTerminis());
			model.addAttribute("accions", exportacio.getAccions());
			model.addAttribute("dominis", exportacio.getDominis());
			model.addAttribute("consultes", exportacio.getConsultes());			
		}
	}
	
	@RequestMapping(value = "/{id}/permis", method = RequestMethod.GET)
	public String permisGet(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		model.addAttribute(
				"expedientTipus",
				expedientTipusService.findAmbIdPermisDissenyar(
						entornActual.getId(),
						id));
		return "v3/expedientTipusPermis";
	}
	
	@RequestMapping(value = "/{id}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse permisDatatable(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.permisFindAll(
						entornActual.getId(),
						id));
	}

	@RequestMapping(value = "/{id}/permis/new", method = RequestMethod.GET)
	public String permisNewGet(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		model.addAttribute(
				"expedientTipus",
				expedientTipusService.findAmbIdPermisDissenyar(
						entornActual.getId(),
						id));
		model.addAttribute(new PermisCommand());
		return "v3/expedientTipusPermisForm";
	}
	@RequestMapping(value = "/{id}/permis/new", method = RequestMethod.POST)
	public String permisNewPost(
			HttpServletRequest request,
			@PathVariable Long id,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		return permisUpdatePost(
				request,
				id,
				null,
				command,
				bindingResult,
				model);
	}
	@RequestMapping(value = "/{id}/permis/{permisId}", method = RequestMethod.GET)
	public String permisUpdateGet(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long permisId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		model.addAttribute(
				"expedientTipus",
				expedientTipusService.findAmbIdPermisDissenyar(
						entornActual.getId(),
						id));
		PermisDto permis = expedientTipusService.permisFindById(
				entornActual.getId(),
				id,
				permisId);
		model.addAttribute(
				conversioTipusHelper.convertir(
						permis,
						PermisCommand.class));
		return "v3/expedientTipusPermisForm";
	}
	@RequestMapping(value = "/{id}/permis/{permisId}", method = RequestMethod.POST)
	public String permisUpdatePost(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long permisId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
        if (bindingResult.hasErrors()) {
        	model.addAttribute(
    				"expedientTipus",
    				expedientTipusService.findAmbIdPermisDissenyar(
    						entornActual.getId(),
    						id));
        	return "v3/expedientTipusPermisForm";
        } else {
    		expedientTipusService.permisUpdate(
    				entornActual.getId(),
    				id,
    				conversioTipusHelper.convertir(
    						command,
    						PermisDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedientTipus/" + id + "/permis",
					"expedient.tipus.controller.permis.actualitzat");
        }
	}

	@RequestMapping(value = "/{id}/permis/{permisId}/delete")
	public String permisDelete(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long permisId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		expedientTipusService.permisDelete(
				entornActual.getId(),
				id,
				permisId);
		model.addAttribute(
				"expedientTipus",
				expedientTipusService.findAmbIdPermisDissenyar(
						entornActual.getId(),
						id));
		model.addAttribute(new PermisCommand());
		return "redirect:/v3/expedientTipus/" + id + "/permis";
	}
	
	//eliminar versions de definicons de procés
	@RequestMapping(value = "/{id}/netejarDp", method = RequestMethod.GET)
	public String netejarDp(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		model.addAttribute(
				"expedientTipus",
				expedientTipusService.findAmbIdPermisDissenyar(
						entornActual.getId(),
						id));
		return "v3/llistatDpNoUs";
	}
	
	@RequestMapping(value = "/{id}/netejarDp/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse netejarDpDatatable(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				dissenyService.findDefinicionsProcesNoUtilitzadesExpedientTipus(
						entornActual.getId(),
						id,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}
	
	@RequestMapping(value = "/{id}/netejarSelectedDp", method = RequestMethod.GET)
	public String netejarSelectedDp(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		try {
			SessionManager sessionManager = SessionHelper.getSessionManager(request);
			Set<Long> seleccio = sessionManager.getSeleccioConsultaDpNoUtilitzades(id);
			
			if (seleccio != null && seleccio.size() > 0) {
				Long longIds[] = new Long[seleccio.size()];
				longIds = seleccio.toArray(longIds);
			
				Date dInici = new Date();
				ExecucioMassivaDto emdto = new ExecucioMassivaDto();
				emdto.setTipus(ExecucioMassivaTipusDto.ELIMINAR_VERSIO_DEFPROC);
				emdto.setDataInici(dInici);
				emdto.setDefProcIds(longIds);
				emdto.setExpedientTipusId(id);
				emdto.setEnviarCorreu(false);
				
				execucioMassivaService.crearExecucioMassiva(emdto);
				
				MissatgesHelper.success(
						request,
						"S'ha programat l'eliminació de les definicions de procés");
			} else {
				MissatgesHelper.warning(
						request, 
						"No s'ha seleccionat cap definició de procés");
	        	logger.error("No s'ha seleccionat cap definició de procés");
			}
			
		} catch (Exception ex) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"error.proces.peticio"));
        	logger.error("No s'han pogut programar l'eliminació de les definicions de procés", ex);
		}
		
		return "redirect:/v3/expedientTipus/" + id + "/netejarDp";
	}
	
	
	//expedients afectats per una definició de procés que vol ser borrada
	@RequestMapping(value = "/{id}/afectatsDp/{dpId}", method = RequestMethod.GET)
	public String afectatsDp(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long dpId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		model.addAttribute(
				"expedientTipus",
				expedientTipusService.findAmbIdPermisDissenyar(
						entornActual.getId(),
						id));
		model.addAttribute(
				"definicioProces",
				dissenyService.getById(dpId));
		return "v3/llistatAfectatsDp";
	}
	
	@RequestMapping(value = "/{id}/afectatsDp/{dpId}/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse netejarDpDatatable(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long dpId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto definicioProces = dissenyService.getById(dpId);
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				dissenyService.findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
						entornActual.getId(),
						id,
						Long.parseLong(definicioProces.getJbpmId()),
						paginacioParams),
				"id");
	}
	
	@RequestMapping(value = "/{id}/eliminarLogs/{dpId}", method = RequestMethod.GET)
	public String eliminarLogs(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long dpId,
			Model model) {
		try {
			SessionManager sessionManager = SessionHelper.getSessionManager(request);
			List<Long> seleccio = sessionManager.getSeleccioConsultaAfectatsDp(dpId);
			
			if (seleccio != null && seleccio.size() > 0) {
				Date dInici = new Date();
				ExecucioMassivaDto emdto = new ExecucioMassivaDto();
				emdto.setDataInici(dInici);
				emdto.setEnviarCorreu(false);
				emdto.setExpedientIds(seleccio);
				emdto.setExpedientTipusId(id);
				emdto.setTipus(ExecucioMassivaTipusDto.BUIDARLOG);
				
				execucioMassivaService.crearExecucioMassiva(emdto);
				
				MissatgesHelper.success(
						request,
						"S'ha programat l'eliminació dels logs dels expedients seleccionats");
			} else {
				MissatgesHelper.warning(
						request, 
						"No s'ha seleccionat cap expedient");
	        	logger.error("No s'ha seleccionat cap expedient");
			}
			
		} catch (Exception ex) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"error.proces.peticio"));
        	logger.error("No s'han pogut programar l'eliminació dels logs dels expedients seleccionats", ex);
		}
		
		return "redirect:/v3/expedientTipus/" + id + "/afectatsDp/" + dpId;
	}
	
	
	@RequestMapping(value = "/{id}/selectionDp/{global}")
	@ResponseBody
	public Set<Long> seleccioDp(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable String global,
			@RequestParam(value = "ids[]", required = false) Long[] ids,
			@RequestParam(value = "method", required = false) String method
			) {
		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaDpNoUtilitzades(id);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioConsultaDpNoUtilitzades(id,seleccio);
		}
		
		if ("selection".equalsIgnoreCase(global)) {
			if ("add".equalsIgnoreCase(method) && ids != null) {
				for (Long idu: ids) {
					if (!seleccio.contains(idu)) {
						seleccio.add(idu);
					}
				}
			} else if ("remove".equalsIgnoreCase(method) && ids != null) {
				for (Long idu: ids) {
					if (seleccio.contains(idu)) {
						seleccio.remove(idu);
					}
				}
			}	
		} else if ("clear".equalsIgnoreCase(global)) {
			seleccio.clear();
		} else if ("all".equalsIgnoreCase(global)) {
			seleccio.clear();
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			seleccio.addAll(dissenyService.findIdsDefinicionsProcesNoUtilitzadesExpedientTipus(entornActual.getId(), id));
		}

		sessionManager.setSeleccioConsultaDpNoUtilitzades(id, seleccio);
		return seleccio;
	}
	
	@RequestMapping(value = "/{id}/afectatsDp/{dpId}/{global}")
	@ResponseBody
	public List<Long> afectatsDp(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long dpId,
			@PathVariable String global,
			@RequestParam(value = "ids[]", required = false) Long[] ids,
			@RequestParam(value = "method", required = false) String method
			) {
		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		List<Long> seleccio = sessionManager.getSeleccioConsultaAfectatsDp(dpId);
		if (seleccio == null) {
			seleccio = new ArrayList<Long>();
			sessionManager.setSeleccioConsultaAfectatsDp(dpId,seleccio);
		}
		
		if ("selection".equalsIgnoreCase(global)) {
			if ("add".equalsIgnoreCase(method) && ids != null) {
				for (Long idu: ids) {
					if (!seleccio.contains(idu)) {
						seleccio.add(idu);
					}
				}
			} else if ("remove".equalsIgnoreCase(method) && ids != null) {
				for (Long idu: ids) {
					if (seleccio.contains(idu)) {
						seleccio.remove(idu);
					}
				}
			}	
		} else if ("clear".equalsIgnoreCase(global)) {
			seleccio.clear();
		} else if ("all".equalsIgnoreCase(global)) {
			seleccio.clear();
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			DefinicioProcesDto definicioProces = dissenyService.getById(dpId);
			seleccio.addAll(dissenyService.findIdsExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
					entornActual.getId(), 
					id, 
					Long.parseLong(definicioProces.getJbpmId())));
		}

		sessionManager.setSeleccioConsultaAfectatsDp(dpId,seleccio);
		return seleccio;
	}
	
	@RequestMapping(value = "{id}/borra_logsexps", method = RequestMethod.POST)
	@ResponseBody
	public String borra_logsExps(
			HttpServletRequest request,
			@PathVariable Long id,
			@RequestParam(value = "definicioProcesId", required = true) String definicioProcesId,
			@RequestParam(value = "expedientsId", required = false) String expedients,
			ModelMap model) throws Exception {
		String response = "{\"resultat\":\"";
		if (expedients == null || expedients.isEmpty()) {
			response += getMessage(request, "error.no.exp.selec");
		} else {
			List<Long> expedientIds = new ArrayList<Long>();
			expedients = expedients.substring(1, expedients.length() - 1);
			String[] expedientsId = expedients.split(",");
			for (String expedient: expedientsId) {
				Long expedientId = Long.parseLong(expedient);
				expedientIds.add(expedientId);
			}
			ExecucioMassivaDto dto = new ExecucioMassivaDto();
			dto.setDataInici(new Date());
			dto.setEnviarCorreu(false);
			dto.setExpedientIds(expedientIds);
			dto.setExpedientTipusId(id);
			dto.setTipus(ExecucioMassivaTipusDto.BUIDARLOG);
			dto.setParam1(definicioProcesId);
			try {
				execucioMassivaService.crearExecucioMassiva(dto);
				response += getMessage(request, "info.defproc.esborrar.massiu.executat", new Object[] {expedientIds.size()});
			} catch (Exception e) {
				logger.error("Error al programar les accions massives", e);
				response += getMessage(request, "error.no.massiu");
			}
		}
		response += "\"}";
		return response;
	}

	/** Acció del menú de la pestanya d'informació per iniciar una tasca en segon pla per actualitzar 
	 * les plantilles dels documents de les definicions de procés amb la informació dels documents de la darrera versió de la definició de procés.
	 * Aquesta acció és per mantenir compatibilitat amb els tipus d'expedient amb la informació dins de les definicions
	 * de procés.
	 */
	@RequestMapping(value = "/{expedientTipusId}/propagarPlantilles")
	public String propagarPlantilles(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		// Programa la execució massiva
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExecucioMassivaDto dto = new ExecucioMassivaDto();
		dto.setExpedientTipusId(expedientTipusId);
		dto.setTipus(ExecucioMassivaTipusDto.PROPAGAR_PLANTILLES);
		dto.setEnviarCorreu(false);
		// Passa les id's de les darreres definicions de procés del tipus d'expedient
		List<Long> definicioProcesIds = new ArrayList<Long>();
		for (DefinicioProcesDto darreraVersio : definicioProcesService.findAll(entornActual.getId(), expedientTipusId))
			definicioProcesIds.add(darreraVersio.getId());
		if (definicioProcesIds.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "exptipus.info.propagar.plantilles.error.cap"));
		} else {
			dto.setDefProcIds(definicioProcesIds.toArray(new Long[definicioProcesIds.size()]));
			try {
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"exptipus.info.propagar.plantilles.success"));
			} catch(Exception e) {
				MissatgesHelper.error(
						request,
						getMessage(
								request,
								"exptipus.info.propagar.plantilles.error",
								new Object[] {e.getMessage()}));
			}			
		}
		return "redirect:/v3/expedientTipus/"+expedientTipusId;
	}
	
	/** Acció del menú de la pestanya d'informació per iniciar una tasca en segon pla per 
	 * actualitzar les plantilles dels documents de les definicions de procés
	 * amb la informació dels documents de la darrera versió de la definició de procés. 
	 * Aquesta acció és per mantenir compatibilitat amb els tipus d'expedient amb la informació dins de les definicions
	 * de procés.
	 */
	@RequestMapping(value = "/{expedientTipusId}/propagarConsultes")
	public String propagarConsultes(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		// Programa la execució massiva
		ExecucioMassivaDto dto = new ExecucioMassivaDto();
		dto.setExpedientTipusId(expedientTipusId);
		dto.setTipus(ExecucioMassivaTipusDto.PROPAGAR_CONSULTES);
		dto.setEnviarCorreu(false);
		// Passa les id's de les consultes del tipus d'expedient
		List<Long> consultesIds = new ArrayList<Long>();
		for (ConsultaDto consulta : expedientTipusService.consultaFindAll(expedientTipusId))
			consultesIds.add(consulta.getId());
		if (consultesIds.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "exptipus.info.propagar.consultes.error.cap"));
		} else {
			dto.setDefProcIds(consultesIds.toArray(new Long[consultesIds.size()]));
			try {
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"exptipus.info.propagar.consultes.success"));
			} catch(Exception e) {
				MissatgesHelper.error(
						request,
						getMessage(
								request,
								"exptipus.info.propagar.consultes.error",
								new Object[] {e.getMessage()}));
			}
		}
		return "redirect:/v3/expedientTipus/"+expedientTipusId;
	}	
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusController.class);
}
