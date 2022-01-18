package es.caib.helium.back.controller;

import es.caib.helium.back.command.DefinicioProcesDesplegarCommand;
import es.caib.helium.back.command.DefinicioProcesDesplegarCommand.ACCIO_JBPM;
import es.caib.helium.back.command.DefinicioProcesExportarCommand;
import es.caib.helium.back.command.DefinicioProcesExportarCommand.Exportacio;
import es.caib.helium.back.command.DefinicioProcesExportarCommand.Importacio;
import es.caib.helium.back.command.DefinicioProcesExportarCommand.Upload;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.NodecoHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.back.view.ArxiuView;
import es.caib.helium.client.model.ParellaCodiValor;
import es.caib.helium.logic.intf.dto.ConsultaDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesExpedientDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesExpedientDto.IdAmbEtiqueta;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExecucioMassivaDto;
import es.caib.helium.logic.intf.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.MotorTipusEnum;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.exception.DeploymentException;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exportacio.DefinicioProcesExportacio;
import es.caib.helium.logic.intf.exportacio.DefinicioProcesExportacioCommandDto;
import es.caib.helium.logic.intf.service.ExecucioMassivaService;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Controlador per al manteniment de les definicions de procés. Controla les pipelles del
 * detall i dels recursos. La pipella de tasques la serveix el controlador {@link DefinicioProcesTascaController}
 *
 */
@Controller
@RequestMapping("/v3/definicioProces")
public class DefinicioProcesController extends BaseDefinicioProcesController {
	
	@Autowired
	private ExpedientTipusService expedientTipusService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExecucioMassivaService execucioMassivaService;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	/** Accés al llistat de definicions de procés de l'entorn des del menú de disseny. */
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			return "v3/definicioProcesLlistat";
		} else {
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
				definicioProcesService.findPerDatatable(
						entornActual.getId(),
						null, // expedientTipusId
						true, // incloure globals
						paginacioParams.getFiltre(),
						paginacioParams));
	}	
	
	/** Mètode per esborrar una versió específica des del disseny de la definició de procés. */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		try {
			DefinicioProcesDto definicioProces = definicioProcesService.findAmbIdPermisDissenyar(entornActual.getId(), definicioProcesId);
			if (definicioProces == null)
				throw new NoTrobatException(DefinicioProcesDto.class, definicioProcesId);

			this.deleteDefinicioProces(entornActual.getId(), request, definicioProces);
			
			// Cerca la darrera definició de procés per codi jbpm i expedient Tipus
			ExpedientTipusDto et = definicioProces.getExpedientTipus();
			if (et != null)
				definicioProces = dissenyService.findDarreraDefinicioProcesForExpedientTipus(et.getId());
			else
				definicioProcesService.findByEntornIdAndJbpmKey(entornActual.getId(), jbmpKey);
			
			// Si no es troba la definició de procés anterior o canvia el tipus d'expedient torna al llistat
			if (definicioProces == null 
					|| (et != null && !et.getId().equals(definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : 0L)))
				return "redirect:/v3/definicioProces";
			
		} catch (Exception e) {
			logger.error("Error : (" + e.getClass() + ") " + e.getLocalizedMessage(), e);
			MissatgesHelper.error(request, getMessage(request, "definicio.proces.delete.error", new Object[] {e.getLocalizedMessage()}));
		}
		// Retorna a la pàgina de pipelles		
		return "redirect:/v3/definicioProces/"+jbmpKey;
	}
	
	/** Mètode privat compartit per esborrar una definició de procés. 
	 * @throws Exception 
	 */
	private boolean deleteDefinicioProces(
			Long entornId,
			HttpServletRequest request,
			DefinicioProcesDto definicioProces) throws Exception {
		
		// Comprova si hi ha consultes amb variables que apuntaven a la versió recentment esborrada i en cas afirmatiu avisa
		if (definicioProces.getExpedientTipus() != null && definicioProces.getExpedientTipus().getConsultes() != null) {
			List<ConsultaDto> consultes = expedientTipusService.consultaFindRelacionadesAmbDefinicioProces(
					entornId,
					definicioProces.getExpedientTipus().getId(),
					definicioProces.getJbpmKey(),
					definicioProces.getVersio());
			Set<Long> consultesAvisades = new HashSet<Long>();
			for(ConsultaDto consulta : consultes ) 	
				if (!consultesAvisades.contains(consulta.getId())) {
					consultesAvisades.add(consulta.getId());
					MissatgesHelper.warning(
							request,
							getMessage(request, "definicio.proces.delete.avis.consultes", 
									new Object[] {consulta.getCodi(), consulta.getNom()}));
				}
		}

		// Esborra la definició de procés
		boolean success = false;
		int processosCount = expedientService.findAmbDefinicioProcesId(definicioProces.getId()).size();
		if (processosCount == 0) 
		{			
			// Invoca al servei per despublicar la definició de procés
			definicioProcesService.delete(
					entornId,
					definicioProces.getId());
			MissatgesHelper.success(request, getMessage(request, "definicio.proces.delete.success", 
					new Object[] {
							definicioProces.getJbpmKey(),
							definicioProces.getVersio()}));
			success = true;
		} else {
			MissatgesHelper.error(request, getMessage(request, "definicio.proces.delete.error.processos", 
						new Object[] {
								definicioProces.getJbpmKey(),
								definicioProces.getVersio(),
								processosCount}));
		}
		return success;
	}

	/** Vista de les pipelles per a la definició de procés que mostrarà la darrera versió. */
	@RequestMapping(value = "/{jbmpKey}", method = RequestMethod.GET)
	public String pipelles(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			Model model) {		
		// consulta la darrera definicio de procés de l'entonr i redirigeix cap al mètode
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto definicioProces = definicioProcesService.findByEntornIdAndJbpmKey(
				entornActual.getId(),
				jbmpKey);
		if (definicioProces == null) {
			MissatgesHelper.error(
					request, 
					getMessage(request, 
							"definicio.proces.pipelles.definicio.no.trobada", 
							new Object[] {jbmpKey}));
			return "redirect:/v3/definicioProces";			
		}		
		return "redirect:/v3/definicioProces/" + jbmpKey + "/" + definicioProces.getId();
	}

	/** Vista de les pipelles per a la definició de procés mostrant una específica. */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}", method = RequestMethod.GET)
	public String pipellesDefinicioProces(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			Model model) {		
		return mostrarInformacioDefinicioProcesPerPipelles(
				request,
				jbmpKey,
				definicioProcesId,
				model,
				"detall");
	}

	/** Pipella del detall. */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/detall")
	public String detall(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioDefinicioProcesPerPipelles(
					request,
					jbmpKey,
					definicioProcesId,
					model,
					"detall");
		}
		model.addAttribute("jbpmKey", jbmpKey);
		model.addAttribute("definicioProcesId", definicioProcesId);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto definicioProces = null;
		if (entornActual != null) {
			definicioProces = definicioProcesService.findById(definicioProcesId);
			model.addAttribute("definicioProces", definicioProces);
			if (definicioProces != null) {
				model.addAttribute("subDefinicionsProces", 
						definicioProcesService.findSubDefinicionsProces(definicioProcesId));				
			}
		}		
		return "v3/definicioProcesDetall";
	}
	
	/** Pipella dels recursos. */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/recurs")
	public String recurs(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioDefinicioProcesPerPipelles(
					request,
					jbmpKey,
					definicioProcesId,
					model,
					"recurs");
		}
		model.addAttribute("jbpmKey", jbmpKey);
		model.addAttribute("definicioProcesId", definicioProcesId);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto definicioProces = null;
		if (entornActual != null) {
			definicioProces = definicioProcesService.findById(definicioProcesId);
			model.addAttribute("definicioProces", definicioProces);
			if (definicioProces != null) {
				// Llistat de recursos
				Set<String> recursos = dissenyService.getRecursosNom(definicioProcesId);
				model.addAttribute("recursos", recursos);
			}
		}		
		return "v3/definicioProcesRecurs";
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/recurs/descarregar")
	public String recursDescarregar(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@RequestParam String nom,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto definicioProces = null;
		if (entornActual != null) {
			definicioProces = definicioProcesService.findById(definicioProcesId);
			if (definicioProces != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME,nom);
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_DATA, 
						dissenyService.getRecursContingut(
								definicioProcesId, 
								nom));
			}
		}		
		return "arxiuView";
	}	

	/** Mètode per crear un .zip i descarregar el .par per una versió de la definició de procés.
	 * 
	 * @param request
	 * @param jbmpKey
	 * @param definicioProcesId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/recurs/par")
	public String recursDescarregarPar(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto definicioProces = null;
		if (entornActual != null) {
			definicioProces = definicioProcesService.findById(definicioProcesId);
			if (definicioProces != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, definicioProces.getJbpmKey() + "_v." + definicioProces.getVersio() + ".par");
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_DATA, 
						dissenyService.getParContingut(definicioProcesId));
			}
		}		
		return "arxiuView";
	}	

	/** Modal per exportar la informació del tipus d'expedient. */
	@RequestMapping(value = "/{jbmpKey}/exportar", method = RequestMethod.GET)
	public String exportar(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@RequestParam(required = false) Long definicioProcesId,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto definicioProces = null;
		if (definicioProcesId == null) {
			// si no s'especifica una definició de procés en concret s'hagafa la darrera versió
			definicioProces = definicioProcesService.findByEntornIdAndJbpmKey(
					entornActual.getId(),
					jbmpKey);
			definicioProcesId = definicioProces.getId();
		} else {
			definicioProces = definicioProcesService.findById(definicioProcesId);
		}
		DefinicioProcesExportarCommand command = new DefinicioProcesExportarCommand();
		command.setId(definicioProcesId);
		model.addAttribute("inici", true); // per marcar tots els checboxs inicialment
		model.addAttribute("command", command);
		
		this.omplirModelFormulariExportacio(
				entornActual.getId(),
				definicioProcesId, 
				model, 
				definicioProces);

		return "v3/definicioProcesExportarForm";
	}	
	
	/** Crida Ajax per recarregar les opcions d'exportació quan canvia la versió de la definició de procés
	 * escollida. */
	@RequestMapping(value = "/{jbmpKey}/exportar/{definicioProcesId}/opcions", method = RequestMethod.GET)
	public String exportarOpcions(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);
		DefinicioProcesExportarCommand command = new DefinicioProcesExportarCommand();
		command.setId(definicioProcesId);
		model.addAttribute("inici", true); // per marcar tots els checboxs inicialment
		model.addAttribute("command", command);
		
		this.omplirModelFormulariExportacio(
				entornActual.getId(),
				definicioProcesId, 
				model, 
				definicioProces);

		return "v3/definicioProcesExportarOpcions";
	}	
	
	@RequestMapping(value = "/{jbpmKey}/exportar", method = RequestMethod.POST)
	public String exportarPost(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@ModelAttribute("command")
			@Validated(Exportacio.class) DefinicioProcesExportarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto dto = definicioProcesService.findById(command.getId());
		if (bindingResult.hasErrors()) {
			model.addAttribute("command", command);
			this.omplirModelFormulariExportacio(
					entornActual.getId(),
					command.getId(), 
					model, 
					dto);        	
			return "v3/definicioProcesExportarForm";
        } else {
        	try {
        		model.addAttribute("filename", dto.getJbpmKey() +"_v" + dto.getVersio() + ".exp");
        		DefinicioProcesExportacio definicioProcesExportacio =
        				definicioProcesService.exportar(
        						entornActual.getId(),
        						command.getId(),
        						conversioTipusHelper.convertir(
        								command, 
        								DefinicioProcesExportacioCommandDto.class));
        		model.addAttribute("data", definicioProcesExportacio);
        		return "serialitzarView";
        	} catch(Exception e) {
        		model.addAttribute("command", command);
        		this.omplirModelFormulariExportacio(
    					entornActual.getId(),
    					command.getId(),
    					model,
    					dto);
        		MissatgesHelper.error(
    					request,
    					"Error exportant : " + e.getMessage());
        		return "v3/definicioProcesExportarForm";
        	}
        }
	}
	
	private void omplirModelFormulariExportacio(
			Long entornId,
			Long definicioProcesId,
			Model model, 
			DefinicioProcesDto definicioProces) {		

		model.addAttribute("definicioProces", definicioProces);
		// Select de les versions
		if (definicioProces != null) {
			DefinicioProcesExpedientDto d = dissenyService.getDefinicioProcesByEntorIdAndProcesId(
					entornId,
					definicioProces.getId());
			List<ParellaCodiValor> versions = new ArrayList<ParellaCodiValor>();
			for (IdAmbEtiqueta i : d.getListIdAmbEtiqueta()) {
				versions.add(new ParellaCodiValor(i.getId().toString(), i.getEtiqueta()));
			}
			model.addAttribute("versions", versions);
		}
		model.addAttribute("tasques", definicioProcesService.tascaFindAll(definicioProcesId));
		model.addAttribute("variables", campService.findAllOrdenatsPerCodi(null, definicioProcesId));
		model.addAttribute("documents", documentService.findAll(null, definicioProcesId));
		model.addAttribute("terminis", definicioProcesService.terminiFindAll(definicioProcesId));
		model.addAttribute("agrupacions", campService.agrupacioFindAll(null, definicioProcesId, false));
		model.addAttribute("accions", accioService.findAll(null, definicioProcesId));
	}	
	
	
	/** Modal per importar la informació de la definició de procés des d'un arxiu d'exportació.
	 * Si el paràmetre definicioProcesId està informat llavors el que s'està realitzant és una importació
	 * de les dades d'un fitxer exportat sobre una definició de procés existent.
	 * Si definicioProcesId és null i el paràmetre expedientTipusId està informat llavors el que s'està realitzant
	 * és un desplegament dins del tipus d'expedient.
	 * Si expedientTipusId i definicioProcesId són nuls llavors el que s'està fent és un desplegament dins l'entorn actual. */
	@RequestMapping(value = "/importar", method = RequestMethod.GET)
	public String importar(
			HttpServletRequest request,
			@RequestParam(required = false) Long expedientTipusId,
			@RequestParam(required = false) Long definicioProcesId,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesExportarCommand command = new DefinicioProcesExportarCommand();
		model.addAttribute("command", command);
		if (entornActual != null) {
			model.addAttribute("entorn", entornActual);
			command.setEntornId(entornActual.getId());
			if (expedientTipusId != null) {
				ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
							entornActual.getId(),
							expedientTipusId);
				model.addAttribute("expedientTipus", expedientTipus);
				command.setExpedientTipusId(expedientTipusId);
			}
			if (definicioProcesId != null) { 
				DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);
				if (definicioProces != null 
						&& definicioProces.getEntorn().getId().equals(entornActual.getId())){
					model.addAttribute("definicioProces", definicioProces);
					command.setId(definicioProcesId);
				}
			}
		}
		return "v3/definicioProcesImportarForm";
	}
	
	/** Carrega el formulari per ajax i mostra les opcions per importar les dades del fitxer importat. */
	@RequestMapping(value = "/importar/upload", method = RequestMethod.POST)
	public String importarUploadPost(
			HttpServletRequest request,
			@Validated(Upload.class)
			@ModelAttribute("command")
			DefinicioProcesExportarCommand command,
			BindingResult bindingResult,
			Model model) {

		// Processament del fitxer fet en el validador ExpedientTipusUploadValidator
		DefinicioProcesExportacio exportacio = command.getExportacio(); 			
		if (bindingResult.hasErrors()) {
			// es limitarà a mostrar els errors de validació
		}
		model.addAttribute("inici", true); // per marcar tots els checboxs inicialment
		model.addAttribute("command", command);	
		command.setVersio(exportacio != null ? exportacio.getDefinicioProcesDto().getVersio() : null);
	 	EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		this.omplirModelFormulariImportacio(entornActual.getId(), command.getId(), exportacio, model);

		return "v3/definicioProcesImportarOpcions";
	}		
	
	/** Acció d'enviament del fitxer i les opcions sobre les dades de la definició de procés.
	 * La validació es fa en el <i>DefinicioProcesImportarValidator</i>.
	 * @param request
	 * @param command
	 * @param bindingResult
	 * @param model
	 * @see es.caib.helium.back.validator.DefinicioProcesImportarValidator
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/importar", method = RequestMethod.POST)
	public String importarPost(
			HttpServletRequest request,
			@Validated(Importacio.class)
			@ModelAttribute("command")
			DefinicioProcesExportarCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		// Processament del fitxer fet en el validador ExpedientTipusImportarValidator
		DefinicioProcesExportacio importacio = command.getExportacio(); 	
	 	
		if (bindingResult.hasErrors()) {
    		model.addAttribute("command", command);	    		
    		this.omplirModelFormulariImportacio(entornActual.getId(), command.getId(), importacio, model);
        	return "v3/definicioProcesImportarOpcions";
        } else {
        	DefinicioProcesDto definicioProces = definicioProcesService.importar(
        			entornActual.getId(),
        			command.getExpedientTipusId(),
        			command.getId(), 
        			conversioTipusHelper.convertir(
							command, 
							DefinicioProcesExportacioCommandDto.class),
        			importacio).get(0);
        	
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"definicio.proces.importar.form.success"));
    		// Indica que la importació ha finalitzat per no haver de processar més codi
    		model.addAttribute("importacioFinalitzada", true);
        	if (command.getId() != null) 
	    		return modalUrlTancar();
        	else {        		
        		// retorna la redirecció
        		model.addAttribute("redireccioUrl",  request.getContextPath() + "/v3/definicioProces/" + definicioProces.getJbpmKey());
            	return "v3/definicioProcesImportarOpcions";
        	}
        }
	}	
	
	private void omplirModelFormulariImportacio(
			Long entornId,
			Long definicioProcesId,
			DefinicioProcesExportacio exportacio,
			Model model) {
		DefinicioProcesDto dto = null;
		if (definicioProcesId != null) {
			dto = definicioProcesService.findById(definicioProcesId);
			model.addAttribute("definicioProces", dto);
			// avisa si el codi de la exportació és diferent del codi al qual s'importa
			if (exportacio != null
					&& dto != null
					&& ! dto.getJbpmKey().equals(exportacio.getDefinicioProcesDto().getJbpmKey())) {
				model.addAttribute("exportacio", exportacio);
				model.addAttribute("avisImportacioDefinicioProcesDiferent", true); 
			}
	 	}
		// Per indicar a la pàgina si s'ha pogut fer una importació del fitxer.
		model.addAttribute("fitxerImportat", exportacio != null);
		if (exportacio != null) {
			model.addAttribute("tasques", exportacio.getTasques());
			model.addAttribute("variables", exportacio.getCamps());
			model.addAttribute("documents", exportacio.getDocuments());
			model.addAttribute("terminis", exportacio.getTerminis());
			model.addAttribute("agrupacions", exportacio.getAgrupacions());
			model.addAttribute("accions", exportacio.getAccions());
		}
	}	
	
	/** Modal per desplegar una definició de procés des d'un arxiu d'exportació .par de JBPM.
	 * Si el paràmetre definicioProcesId està informat llavors el que s'està realitzant és un desplegament
	 * sobre una definició de procés existent i es permetrà sobre escriure els handlers.
	 * Si definicioProcesId és null i el paràmetre expedientTipusId està informat llavors el que s'està realitzant
	 * és un desplegament dins del tipus d'expedient.
	 * Si expedientTipusId i definicioProcesId són nuls llavors el que s'està fent és un desplegament dins l'entorn actual. */
	@RequestMapping(value = "/desplegar", method = RequestMethod.GET)
	public String desplegar(
			HttpServletRequest request,
			@RequestParam(required = false) Long expedientTipusId,
			@RequestParam(required = false) Long definicioProcesId,
			Model model) {

		DefinicioProcesDesplegarCommand command = new DefinicioProcesDesplegarCommand();
		command.setAccio(ACCIO_JBPM.JBPM_DESPLEGAR);
		command.setExpedientTipusId(expedientTipusId);
		command.setId(definicioProcesId);
		if (expedientTipusId != null) {
			var expedientTipus = expedientTipusService.findAmbId(expedientTipusId);
			command.setMotorTipus(expedientTipus.getMotorTipus());
		}
		this.omplirModelFormulariDesplegament(command, model, request);
		return "v3/definicioProcesDesplegarForm";
	}
	
	/** Acció d'enviament del fitxer i les opcions sobre les dades de la definició de procés.
	 * La validació es fa en el <i>DefinicioProcesImportarValidator</i>.
	 * @param request
	 * @param command
	 * @param bindingResult
	 * @param model
	 * @see es.caib.helium.back.validator.DefinicioProcesImportarValidator
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/desplegar", method = RequestMethod.POST)
	public String desplegarPost(
			HttpServletRequest request,
			@ModelAttribute("command")
			@Validated(DefinicioProcesDesplegarCommand.Desplegament.class)
			DefinicioProcesDesplegarCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		if (bindingResult.hasErrors()) {
    		this.omplirModelFormulariDesplegament(command, model, request);
        	return "v3/definicioProcesDesplegarForm";
        } else {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
        	boolean error = false;
        	try {
        		if (ACCIO_JBPM.JBPM_DESPLEGAR.equals(command.getAccio())) {

					// Camunda
//					if (command.getFile().getOriginalFilename().endsWith("war")) {
					if (MotorTipusEnum.CAMUNDA.equals(command.getMotorTipus())) {

						byte[] contingutDesplegament = command.getFile().getBytes();
						validateDeploymentFile(contingutDesplegament);

						DefinicioProcesExportacio exportacio = new DefinicioProcesExportacio();
						exportacio.setMotorTipus(command.getMotorTipus());
						exportacio.setNomDeploy(command.getFile().getOriginalFilename());
						exportacio.setContingutDeploy(contingutDesplegament);

						List<DefinicioProcesDto> definicionsProces = definicioProcesService.importar(
								command.getEntornId(),
								command.getExpedientTipusId(),
								command.getId(),
								null,
								exportacio);

						for (var definicioProces: definicionsProces) {
							// Obtenim la anterior definició per copiar dades
							DefinicioProcesDto darreraDefinicioProces =
//									definicioProcesService.findPreviaByEntornIdAndJbpmKey(
									definicioProcesService.findByEntornIdAndJbpmKey(
											entornActual.getId(),
											definicioProces.getJbpmKey());
							// Copia les dades de la darrera versió a la nova
							definicioProcesCopyDades(darreraDefinicioProces, definicioProces, false);
						}
						MissatgesHelper.success(request, getMessage(request, "definicio.proces.desplegar.form.success"));
						if (command.isActualitzarExpedientsActius()) {
							for (var definicioProces : definicionsProces) {
								error = error || updateExpedientsActius(request, entornActual, definicioProces);
							}
						}

					// JBPM
					} else {
						// Recupera la informació del contingut del fitxer
						DefinicioProcesExportacio exportacio =
								dissenyService.getDefinicioProcesExportacioFromContingut(
										command.getFile().getOriginalFilename(),
										command.getFile().getBytes()
								);
						exportacio.setMotorTipus(command.getMotorTipus());
						// Guarda la darrera per copiar dades
						DefinicioProcesDto darreraDefinicioProces =
								definicioProcesService.findByEntornIdAndJbpmKey(
										entornActual.getId(),
										exportacio.getDefinicioProcesDto().getJbpmKey());
						// Realitza la importació com a una nova versió
						exportacio.getDefinicioProcesDto().setEtiqueta(command.getEtiqueta());
						DefinicioProcesDto definicioProces = definicioProcesService.importar(
								command.getEntornId(),
								command.getExpedientTipusId(),
								command.getId(),
								null,    // DefinicioProcesExportacioCommandDto
								exportacio).get(0);
						// Copia les dades de la darrera versió a la nova
						definicioProcesCopyDades(darreraDefinicioProces, definicioProces, true);

						MissatgesHelper.success(request, getMessage(request, "definicio.proces.desplegar.form.success"));
						if (command.isActualitzarExpedientsActius()) {
							updateExpedientsActius(request, entornActual, definicioProces);
						}
					}
        		} else if (ACCIO_JBPM.JBPM_ACTUALITZAR.equals(command.getAccio())){
        			DefinicioProcesDto definicioProces = null;
        			try {
        				definicioProces = dissenyService.updateHandlers(
							entornActual.getId(),
							command.getExpedientTipusId(),
        					command.getFile().getOriginalFilename(),
    						command.getFile().getBytes());
        				if (definicioProces.getExpedientTipus() != null)
	                		MissatgesHelper.success(request, 
	                				getMessage( 
	                						request, 
	                						"definicio.proces.actualitzar.confirmacio.expedientTipus",
	                						new Object[] {
	                								definicioProces.getJbpmKey(),
	                								definicioProces.getVersio(),
	                								definicioProces.getExpedientTipus().getCodi(),
	                								definicioProces.getExpedientTipus().getNom() }));
        				else
	                		MissatgesHelper.success(request, 
	                				getMessage( 
	                						request, 
	                						"definicio.proces.actualitzar.confirmacio.global",
	                						new Object[] {
	                								definicioProces.getJbpmKey(),
	                								definicioProces.getVersio() }));

        			} catch (Exception e) {
        				logger.error("Error : (" + e.getClass() + ") " + e.getLocalizedMessage());
        				MissatgesHelper.error(
        						request,
        						getMessage(
        								request,
        								"definicio.proces.actualitzar.excepcio",
        								new Object[] {e.getMessage()}));
        				error = true;
        			}
            	}
        	} catch (Exception e) {
        		logger.error("Error: (" + e.getClass() + ") " + e.getLocalizedMessage() );
        		MissatgesHelper.error(request, 
        				getMessage(
        						request, 
        						"definicio.proces.desplegar.form.error",
        						new Object[] {e.getLocalizedMessage()}));
        		error = true;
        	}
        	if (error) {
        		this.omplirModelFormulariDesplegament(command, model, request);
            	return "v3/definicioProcesDesplegarForm";
        	} else {
        		return modalUrlTancar(false);        		
        	}
        }
	}

	private boolean updateExpedientsActius(HttpServletRequest request, EntornDto entornActual, DefinicioProcesDto definicioProces) {
		boolean error = false;
		// Programació de la tasca d'actualització d'expedients actius
		ExecucioMassivaDto dto = new ExecucioMassivaDto();
		dto.setDataInici(new Date());
		dto.setEnviarCorreu(false);
		dto.setParam1(definicioProces.getJbpmKey());
		dto.setParam2(execucioMassivaService.serialize(Integer.valueOf(definicioProces.getVersio())));
		dto.setTipus(ExecucioMassivaTipusDto.ACTUALITZAR_VERSIO_DEFPROC);
		dto.setProcInstIds(
				expedientService.findProcesInstanceIdsAmbEntornAndProcessDefinitionName(
						entornActual.getId(),
						definicioProces.getJbpmKey()));
		try {
			execucioMassivaService.crearExecucioMassiva(dto);
			MissatgesHelper.success(request, getMessage(request, "info.canvi.versio.massiu", new Object[]{dto.getProcInstIds().size()}));
		} catch (Exception e) {
			logger.error("Error : (" + e.getClass() + ") " + e.getLocalizedMessage());
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"definicio.proces.desplegar.error.actualitzarExpedientActius",
							new Object[]{e.getMessage()}));
			error = true;
		}
		return error;
	}

	private void definicioProcesCopyDades(DefinicioProcesDto darreraDefinicioProces, DefinicioProcesDto definicioProces, boolean isJbpm) {
		// Copia les dades de la darrera versió a la nova
		if (darreraDefinicioProces != null)
			definicioProcesService.copiarDefinicioProces(
					darreraDefinicioProces.getId(),
					definicioProces.getId());
		// Invoca al mètode per relacionar les darreres definicions de procés
		if (isJbpm && definicioProces.getExpedientTipus() != null)
			definicioProcesService.relacionarDarreresVersions(definicioProces.getExpedientTipus().getId());
	}

	private void validateDeploymentFile(byte[] contingutDesplegament) throws Exception {
		ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(contingutDesplegament));
		// Llegim el fitxer, i comprovam qeu es correspon a un process, i conté alguna definició de procés
		boolean isProcess = false;
		boolean hasProcessDefinition = false;

		ZipEntry zipEntry = null;
		try {
			while ((zipEntry = zis.getNextEntry()) != null) {
				if (zipEntry.getName().endsWith("processes.xml")) {
					String deploymentDescriptorFile = new String(zis.readAllBytes());
					var processArchiveStartIndex = deploymentDescriptorFile.indexOf("<process-archive");
					if (processArchiveStartIndex != -1) {
						isProcess = true;
					}
				} else if (zipEntry.getName().endsWith("bpmn") || zipEntry.getName().endsWith("dmn")) {
					hasProcessDefinition = true;
				}
			}

			if (!isProcess || !hasProcessDefinition)
				throw new DeploymentException(MessageHelper.getInstance().getMessage("exportar.validacio.definicio.deploy.error"));
		} catch (IOException ex) {
			throw new DeploymentException(MessageHelper.getInstance().getMessage("exportar.validacio.definicio.deploy.error"));
//			throw new Exception("Fitxer de desplegament no vàlid", ex);
		}
	}

	private void omplirModelFormulariDesplegament(
			DefinicioProcesDesplegarCommand command,
			Model model,
			HttpServletRequest request) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		// Per indicar a la pàgina si s'ha pogut fer una importació del fitxer.
		model.addAttribute("command", command);
		
		if (entornActual != null) {
			model.addAttribute("entorn", entornActual);
			command.setEntornId(entornActual.getId());
			if (command.getExpedientTipusId() != null) {
				ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
							entornActual.getId(),
							command.getExpedientTipusId());
				model.addAttribute("expedientTipus", expedientTipus);
			}
			if (command.getId() != null) { 
				DefinicioProcesDto definicioProces = definicioProcesService.findById(command.getId());
				if (definicioProces != null 
						&& definicioProces.getEntorn().getId().equals(entornActual.getId()))
					model.addAttribute("definicioProces", definicioProces);
			}
			// Select dels tipus d'expedient de l'entorn
			model.addAttribute("expedientsTipus", expedientTipusService.findAmbEntornPermisDissenyar(entornActual.getId()));
		}
		
		// Select de les accions jbpm
		List<ParellaCodiValor> accions = new ArrayList<ParellaCodiValor>();
		accions.add(new ParellaCodiValor(
				ACCIO_JBPM.JBPM_DESPLEGAR.toString(),
				getMessage(request, "definicio.proces.desplegar.form.accio.desplegar")));
		accions.add(new ParellaCodiValor(
				ACCIO_JBPM.JBPM_ACTUALITZAR.toString(),
				getMessage(request, "definicio.proces.desplegar.form.accio.actualitzar")));
		model.addAttribute("accionsJbpm", accions);
		model.addAttribute("motorsTipus", getMotors(request));
		
	}

	private List<ParellaCodiValor> getMotors(HttpServletRequest request) {
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		resposta.add(new ParellaCodiValor(getMessage(request, "motor.tipus.enum.JBPM"), MotorTipusEnum.JBPM));
		resposta.add(new ParellaCodiValor(getMessage(request, "motor.tipus.enum.CAMUNDA"), MotorTipusEnum.CAMUNDA));
		return resposta;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
	
	private static final Log logger = LogFactory.getLog(DefinicioProcesController.class);
}