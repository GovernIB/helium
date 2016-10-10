package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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

import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto.IdAmbEtiqueta;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacioCommandDto;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesExportarCommand;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesExportarCommand.Exportacio;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesExportarCommand.Importacio;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesExportarCommand.Upload;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per al manteniment de les definicions de procés. Controla les pipelles del
 * detall i dels recursos. La pipella de tasques la serveix el controlador {@link DefinicioProcesTascaController}
 *
 */
@Controller(value = "definicioProcesControllerV3")
@RequestMapping("/v3/definicioProces")
public class DefinicioProcesController extends BaseDefinicioProcesController {
	
	@Autowired
	DefinicioProcesService definicioProcesService;
	@Autowired
	private ExpedientTipusService expedientTipusService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	/** Vista de les pipelles per a la definició de procés. */
	@RequestMapping(value = "/{jbmpKey}", method = RequestMethod.GET)
	public String pipelles(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			Model model) {		
		return mostrarInformacioDefinicioProcesPerPipelles(
				request,
				jbmpKey,
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
			List<ParellaCodiValorDto> versions = new ArrayList<ParellaCodiValorDto>();
			for (IdAmbEtiqueta i : d.getListIdAmbEtiqueta()) {
				versions.add(new ParellaCodiValorDto(i.getId().toString(), i.getEtiqueta()));
			}
			model.addAttribute("versions", versions);
		}
		model.addAttribute("tasques", definicioProcesService.tascaFindAll(definicioProcesId));
		model.addAttribute("variables", definicioProcesService.campFindAllOrdenatsPerCodi(definicioProcesId));
		model.addAttribute("documents", definicioProcesService.documentFindAllOrdenatsPerCodi(definicioProcesId));
		model.addAttribute("terminis", definicioProcesService.terminiFindAll(definicioProcesId));
		model.addAttribute("agrupacions", definicioProcesService.agrupacioFindAll(definicioProcesId));
		model.addAttribute("accions", definicioProcesService.accioFindAll(definicioProcesId));
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
	 * @see net.conselldemallorca.helium.webapp.v3.validator.DefinicioProcesImportarValidator
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
        			importacio);
        	
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

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
}
