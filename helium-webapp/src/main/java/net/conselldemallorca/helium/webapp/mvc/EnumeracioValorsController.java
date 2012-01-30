/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador per la gestió d'enumeracions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class EnumeracioValorsController extends BaseController {

	private DissenyService dissenyService;
	private Validator annotationValidator;



	@Autowired
	public EnumeracioValorsController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService  = dissenyService;
	}

	@ModelAttribute("command")
	public EnumeracioValorsCommand populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) Long id) {
		EnumeracioValorsCommand command = new EnumeracioValorsCommand();
		if (id != null)
			command.setEnumeracioId(id);
		return command;
	}

	@RequestMapping(value = "/enumeracio/valors", method = RequestMethod.GET)
	public String formValorsGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute("llistat", dissenyService.findEnumeracioValorsAmbEnumeracio(id));
			ImportCommand commandImportacio = new ImportCommand();
			commandImportacio.setEnumeracioId(id);
			model.addAttribute("commandImportacio", commandImportacio);
			return "enumeracio/valors";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/enumeracio/valors", method = RequestMethod.POST)
	public String formValorsPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") EnumeracioValorsCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if ("submit".equals(submit) || submit.length() == 0) {
				Enumeracio enumeracio = dissenyService.getEnumeracioById(command.getEnumeracioId());
				annotationValidator.validate(command, result);
				new EnumeracioValorsValidator(dissenyService).validate(command, result);
		        if (result.hasErrors()) {
		        	model.addAttribute("llistat", dissenyService.findEnumeracioValorsAmbEnumeracio(command.getEnumeracioId()));
		        	return "enumeracio/valors";
		        }
		        try {
		        	EnumeracioValors enumeracioValors = new EnumeracioValors();
		        	enumeracioValors.setId(null);
		        	enumeracioValors.setCodi(command.getCodi());
		        	enumeracioValors.setNom(command.getNom());
		        	enumeracioValors.setEnumeracio(enumeracio);
		        	if (command.getId() == null)
		        		dissenyService.createEnumeracioValors(enumeracioValors);
		        	else
		        		dissenyService.updateEnumeracioValors(enumeracioValors);
		        	missatgeInfo(request, getMessage("info.enum.guardat") );
		        	status.setComplete();
		        } catch (Exception ex) {
		        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut guardar el registre", ex);
		        	return "enumeracio/valors";
		        }
				return "redirect:/enumeracio/valors.html?id=" + command.getEnumeracioId();
			} else {
				return "redirect:/enumeracio/llistat.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/enumeracio/deleteValors")
	public String deleteValorsAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			EnumeracioValors enumeracioValors = dissenyService.getEnumeracioValorsById(id);
			try {
				dissenyService.deleteEnumeracioValors(id);
				missatgeInfo(request, getMessage("info.enum.esborrat"));
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.esborrar.enum"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar l'enumeració", ex);
	        }
			return "redirect:/enumeracio/valors.html?id=" + enumeracioValors.getEnumeracio().getId();
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/enumeracio/valorsPujar")
	public String pujarValor(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			EnumeracioValors enumeracioValors = dissenyService.getEnumeracioValorsById(id);
			try {
				dissenyService.goUpEnumeracioValor(id);
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.ordre.enumeracio"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut canviar l'ordre del valor de l'enumeració", ex);
	        }
			return "redirect:/enumeracio/valors.html?id=" + enumeracioValors.getEnumeracio().getId();
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/enumeracio/valorsBaixar")
	public String baixarValor(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			EnumeracioValors enumeracioValors = dissenyService.getEnumeracioValorsById(id);
			try {
				dissenyService.goDownEnumeracioValor(id);
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.ordre.enumeracio"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut canviar l'ordre del valor de l'enumeració", ex);
	        }
			return "redirect:/enumeracio/valors.html?id=" + enumeracioValors.getEnumeracio().getId();
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/enumeracio/importar")
	public String importar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "arxiu", required = true) final MultipartFile multipartFile) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				if (multipartFile.getBytes() == null || multipartFile.getBytes().length == 0) {
					missatgeError(request, getMessage("error.especificar.arxiu.importar"));
				} else {
					Enumeracio enumeracio = dissenyService.getEnumeracioById(id);
					BufferedReader br = new BufferedReader(
							new InputStreamReader(multipartFile.getInputStream()));
					String linia = br.readLine();
					while (linia != null) {
						String[] columnes = linia.split(";");
						if (columnes.length == 0)
							columnes = linia.split(",");
						if (columnes.length > 1) {
							EnumeracioValors enumeracioValors = new EnumeracioValors();
				        	enumeracioValors.setId(null);
				        	enumeracioValors.setCodi(columnes[0]);
				        	enumeracioValors.setNom(columnes[1]);
				        	enumeracioValors.setEnumeracio(enumeracio);
			        		dissenyService.createEnumeracioValors(enumeracioValors);
						}
						linia = br.readLine();
					}
					missatgeInfo(request, getMessage("info.enum.valors.importats"));
				}
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.ordre.enumeracio"), ex.getLocalizedMessage());
	        	logger.error("No s'han pogut importar els valors de l'enumeració " + id, ex);
	        }
			return "redirect:/enumeracio/valors.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	public class ImportCommand {
		private Long enumeracioId;
		private byte[] arxiu;
		public Long getEnumeracioId() {
			return enumeracioId;
		}
		public void setEnumeracioId(Long enumeracioId) {
			this.enumeracioId = enumeracioId;
		}
		public byte[] getArxiu() {
			return arxiu;
		}
		public void setArxiu(byte[] arxiu) {
			this.arxiu = arxiu;
		}
	}



	private static final Log logger = LogFactory.getLog(EnumeracioValorsController.class);
}
