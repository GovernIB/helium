/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.ExpedientTipusEstatCommand;
import es.caib.helium.back.command.ImportarDadesCommand;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.NodecoHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.back.validator.CodiValidator;
import es.caib.helium.client.model.ParellaCodiValor;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.EstatDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador per a la pipella de variables del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusEstatController extends BaseExpedientTipusController {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	@ModelAttribute("listEstats")
	public List<ParellaCodiValor> populateValorEstats() {
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		for (int i = 0; i <= 12; i++) {
			resposta.add(new ParellaCodiValor(Integer.toString(i), i));
		}
		return resposta;
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estats")
	public String estats(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"estats");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
		}
		return "v3/expedientTipusEstat";
	}

	@RequestMapping(value="/{expedientTipusId}/estats/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		paginacioParams.getOrdres().clear();
		paginacioParams.afegirOrdre("ordre", OrdreDireccioDto.ASCENDENT);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.estatFindPerDatatable(
						expedientTipusId,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");		
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estat/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(required = false) Long agrupacioId,
			Model model) {
		ExpedientTipusEstatCommand command = new ExpedientTipusEstatCommand();
		
		command.setExpedientTipusId(expedientTipusId);
		
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("expedientTipusEstatCommand", command);
		return "v3/expedientTipusEstatForm";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estat/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusEstatCommand.Creacio.class) ExpedientTipusEstatCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("expedientTipusId", expedientTipusId);
        	return "v3/expedientTipusEstatForm";
        } else {
        	// Verificar permisos
    		expedientTipusService.estatCreate(
    				expedientTipusId,
    				conversioTipusHelper.convertir(
    						command,
    						EstatDto.class));    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.estat.controller.creat"));
			return modalUrlTancar(false);	
			
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/estat/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		EstatDto dto = expedientTipusService.estatFindAmbId(expedientTipusId, id);
		ExpedientTipusEstatCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusEstatCommand.class);
		command.setExpedientTipusId(expedientTipusId);
		
		model.addAttribute("expedientTipusEstatCommand", command);
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("heretat", dto.isHeretat());
		return "v3/expedientTipusEstatForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/estat/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(ExpedientTipusEstatCommand.Modificacio.class) ExpedientTipusEstatCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("expedientTipusId", expedientTipusId);
    		model.addAttribute("heretat", expedientTipusService.estatFindAmbId(expedientTipusId, id).isHeretat());
        	return "v3/expedientTipusEstatForm";
        } else {
        	expedientTipusService.estatUpdate(
        			conversioTipusHelper.convertir(
    						command,
    						EstatDto.class));
        	MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.estat.controller.modificat"));
			return modalUrlTancar(false);
        }
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estat/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean borrar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		try {
			expedientTipusService.estatDelete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.estat.controller.eliminat"));
			return true;
		} catch (Exception e) {
			logger.error(e);
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.tipus.estat.controller.eliminat.no",
							new Object[] {e.getLocalizedMessage()}));
			return false;
		}
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/moure/{posicio}", method = RequestMethod.GET)
	@ResponseBody
	public boolean moure(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			@PathVariable int posicio,
			Model model) {
		boolean ret = false;

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto tipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
				entornActual.getId(), 
				expedientTipusId); 
		
		boolean herencia = tipus.getExpedientTipusPareId() != null;
		if (herencia) {
			EstatDto estat = expedientTipusService.estatFindAmbId(expedientTipusId, estatId);
			boolean correcte = true;
			if (estat.isHeretat()) {
				MissatgesHelper.error(
				request, 
				getMessage(
						request, 
						"expedient.tipus.estat.controller.moure.heretat.error"));
				correcte = false;
			} else {
				// rectifica la posició restant tots els heretats que té per davant
				int nHeretats = 0;
				for(EstatDto e : expedientTipusService.estatFindAll(expedientTipusId, true))
					if (e.isHeretat())
						nHeretats ++;
				if (posicio < nHeretats) {
					MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.tipus.estat.controller.moure.heretat.error"));
					correcte = false;
				} else {
					posicio = posicio - nHeretats;
				}
			}
			if (correcte)
				ret = expedientTipusService.estatMoure(estatId, posicio);
			else
				ret = false;
		}
		else {
			ret = expedientTipusService.estatMoure(estatId, posicio);
		}
		return ret;
	}
	
	
	
	@RequestMapping(value = "/{expedientTipusId}/estat/exportar", method = RequestMethod.GET)
	@ResponseBody
	public void exportar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long expedientTipusId) throws Exception {


        	try {
        		List<EstatDto> estats = expedientTipusService.estatFindAll(
        				expedientTipusId, 
        				true); // amb herència
        		
        		String estatsString = "";
        		for(EstatDto estat: estats){
        			estatsString +=
        					estat.getCodi()+";"+estat.getNom()+"\n";
        		}
        		
        		MissatgesHelper.success(
    					request, 
    					getMessage(
    							request, 
    							"expedient.tipus.estat.exportar.controller.success"));        			

        		response.setHeader("Pragma", "");
        		response.setHeader("Expires", "");
        		response.setHeader("Cache-Control", "");
        		response.setHeader("Content-Disposition", "attachment; filename=\""
        				+ "estats_exp.csv" + "\"");
        		response.setContentType("text/plain");
        		response.getOutputStream().write(estatsString.getBytes());
        
        	} catch(Exception e) {
        		logger.error(e);
        		MissatgesHelper.error(
        				request,
        				getMessage(
        						request, 
        						"expedient.tipus.estat.exportar.controller.error",
        						new Object[]{e.getLocalizedMessage()}));
        		throw(e);
        	}        
	}	

	
	/** Mètode per obrir un formulari d'importació de dades d'estats. */
	@RequestMapping(value = "/{expedientTipusId}/estat/importar", method = RequestMethod.GET)
	public String importar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute(new ImportarDadesCommand());
		return "v3/expedientTipusEstatImportarForm";
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/estat/importar", method = RequestMethod.POST)
	public String importarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ImportarDadesCommand.Importar.class) ImportarDadesCommand command,
			BindingResult bindingResult,
			Model model) {
		if (command.getMultipartFile() == null || command.getMultipartFile().getSize() == 0) {
			bindingResult.rejectValue("multipartFile", "expedient.tipus.estat.importar.controller.validacio.multipartFile.buit");
		}
        if (bindingResult.hasErrors()) {
        	model.addAttribute("importarDadesCommand", command);
        	return "v3/expedientTipusEstatImportarForm";
        } else {

			int insercions = 0;
			int actualitzacions = 0;
        	try {
    			if (command.isEliminarValorsAntics()) {
    				for (EstatDto estat : expedientTipusService.estatFindAll(expedientTipusId, false))
   						expedientTipusService.estatDelete(estat.getId());
    			}
    			BufferedReader br = new BufferedReader(new InputStreamReader(command.getMultipartFile().getInputStream()));
    			String linia = br.readLine();
    			EstatDto estat = new EstatDto();
    			String codi;
    			String nom;
    			while (linia != null) {
    				String[] columnes = linia.contains(";") ? linia.split(";") : linia.split(",");
    				if (columnes.length > 1) {
    					codi = columnes[0];
    					// Comprova que el codi sigui vàlid
    					if (! CodiValidator.isValid(codi)) {
    		        		MissatgesHelper.error(
    		        				request,
    		        				getMessage(
    		        						request, 
    		        						"expedient.tipus.estat.importar.controller.error.codi",
    		        						new Object[]{codi}));
    					} else {
    						// Completa la inserció o actualització
        					nom = columnes[1];
        					estat = expedientTipusService.estatFindAmbCodi(expedientTipusId, codi);
        					if (estat == null) {
        						estat = new EstatDto();
        						estat.setCodi(codi);
        						estat.setNom(nom);
        						expedientTipusService.estatCreate(expedientTipusId, estat);
        						insercions++;
        					} else {
        						estat.setNom(nom);
        						expedientTipusService.estatUpdate(estat);
        						actualitzacions++;
        					}
    					}
    				}
    				linia = br.readLine();
    			}
        	} catch(Exception e) {
        		logger.error(e);
        		MissatgesHelper.error(
        				request,
        				getMessage(
        						request, 
        						"expedient.tipus.estat.importar.controller.error",
        						new Object[]{e.getLocalizedMessage()}));
        	}
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.estat.importar.controller.success",
							new Object[] {insercions, actualitzacions}));        		
			return modalUrlTancar(false);	
        }
	}	

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusEstatController.class);
}