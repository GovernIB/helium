/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;



/**
 * Controlador per la gestió de consultes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ConsultaController extends BaseController {

	private DissenyService dissenyService;
	private Validator annotationValidator;



	@Autowired
	public ConsultaController(
			DissenyService dissenyService) {
		this.dissenyService  = dissenyService;
	}

	@ModelAttribute("expedientTipus")
	public List<ExpedientTipus> populateExpedientTipus(HttpServletRequest request) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null)
			return dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
		return null;
	}

	@ModelAttribute("command")
	public ConsultaCommand populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			Consulta consulta = dissenyService.getConsultaById(id);
			ConsultaCommand command = new ConsultaCommand();
			command.setId(consulta.getId());
			command.setEntorn(consulta.getEntorn());
			command.setCodi(consulta.getCodi());
			command.setDescripcio(consulta.getDescripcio());
			command.setExpedientTipus(consulta.getExpedientTipus());
			command.setNom(consulta.getNom());
			command.setFormatExport(consulta.getFormatExport());
			command.setInformeNom(consulta.getInformeNom());
			command.setInformeContingut(consulta.getInformeContingut());
			command.setValorsPredefinits(consulta.getValorsPredefinits());
			command.setExportarActiu(consulta.isExportarActiu());
			command.setOcultarActiu(consulta.isOcultarActiu());
			return command;
		}
		return new ConsultaCommand();
	}

	@RequestMapping(value = "/consulta/llistat", method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute("llistat", dissenyService.findConsultesAmbEntorn(entorn.getId()));
			model.addAttribute("tipusFiltre", ConsultaCamp.TipusConsultaCamp.FILTRE);
			model.addAttribute("tipusInforme", ConsultaCamp.TipusConsultaCamp.INFORME);
			return "consulta/llistat";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	
	@ModelAttribute("formatsExportacio")
	public String[] formatsExportacio() {
		String[] formatsExportacio = {"PDF","ODT","RTF","HTML","CSV","XLS","XML"};
		return formatsExportacio;
	}
	
	

	@RequestMapping(value = "/consulta/form", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			return "consulta/form";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/consulta/form", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) Long id,
//			@RequestParam(value = "informeContingut", required = true) byte[] informeContingut,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "informeContingut", required = false) final MultipartFile multipartFile,
			@RequestParam(value = "informeContingut_deleted", required = false) final String deleted,
			@RequestParam(value = "formatExport", required = false) String formatExport,
			@ModelAttribute("command") ConsultaCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		
		if (entorn != null) {
			Consulta consulta = new Consulta();
			if(id != null) consulta = dissenyService.getConsultaById(id);
			
			consulta.setId(command.getId());
			consulta.setEntorn(entorn);
			consulta.setCodi(command.getCodi());
			consulta.setNom(command.getNom());
			consulta.setDescripcio(command.getDescripcio());
			consulta.setExpedientTipus(command.getExpedientTipus());
			consulta.setFormatExport(command.getFormatExport());
			consulta.setValorsPredefinits(command.getValorsPredefinits());
			consulta.setExportarActiu(command.isExportarActiu());
			consulta.setOcultarActiu(command.isOcultarActiu());
			
			
			
			if ("submit".equals(submit) || submit.length() == 0) {
				if("deleted".equalsIgnoreCase(deleted)){
					consulta.setEntorn(entorn);
					consulta.setInformeNom(null);
					consulta.setInformeContingut(null);
					consulta.setFormatExport(formatExport);
				}
				if (multipartFile != null && multipartFile.getSize() > 0) {
					try {
						consulta.setEntorn(entorn);
						consulta.setInformeContingut(multipartFile.getBytes());
						consulta.setInformeNom(multipartFile.getOriginalFilename());
						consulta.setFormatExport(formatExport);
					} catch (Exception ignored) {}
				}
				
				annotationValidator.validate(command, result);
		        if (result.hasErrors()) {
		        	return "consulta/form";
		        }
		        try {
		        	if (command.getId() == null)
		        		dissenyService.createConsulta(consulta);
		        	else {
		        		dissenyService.updateConsulta(consulta, "deleted".equalsIgnoreCase(deleted));
		        	}
		        	missatgeInfo(request, getMessage("info.consulta.guardat") );
		        	status.setComplete();
		        } catch (Exception ex) {
		        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut guardar la consulta", ex);
		        	return "consulta/form";
		        }
			}
			return "redirect:/consulta/llistat.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	
	@RequestMapping(value = "/consulta/delete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.deleteConsulta(id);
				missatgeInfo(request, getMessage("info.consulta.guardat") );
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.esborrar.consulta"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar el registre", ex);
	        }
			return "redirect:/consulta/llistat.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/consulta/informeDownload")
	public String downloadAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				Consulta consulta = dissenyService.getConsultaById(id);
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_FILENAME,
						consulta.getInformeNom());
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_DATA,
						consulta.getInformeContingut());
				return "arxiuView";
			} catch (Exception ignored) {
				return "redirect:/consulta/llistat.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				ExpedientTipus.class,
				new ExpedientTipusTypeEditor(dissenyService));
		binder.registerCustomEditor(
				byte[].class,
				new ByteArrayMultipartFileEditor());
	}

	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	private static final Log logger = LogFactory.getLog(ConsultaController.class);

}
