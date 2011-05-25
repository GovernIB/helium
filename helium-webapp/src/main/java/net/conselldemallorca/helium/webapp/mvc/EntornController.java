/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.exportacio.EntornExportacio;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.EntornService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;



/**
 * Controlador per la gestió d'entorns
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class EntornController extends BaseController {

	private EntornService entornService;
	private Validator annotationValidator;
	private Validator additionalValidator;



	@Autowired
	public EntornController(
			EntornService entornService) {
		this.entornService = entornService;
		additionalValidator = new EntornValidator(entornService);
	}

	@ModelAttribute("commandImportacio")
	public ImportCommand populateCommandImportacio() {
		return new ImportCommand();
	}

	@ModelAttribute("command")
	public Entorn populateCommand(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			return entornService.getById(id);
		}
		Entorn entorn = new Entorn();
		entorn.setActiu(true);
		return entorn;
	}

	@RequestMapping(value = "/entorn/llistat")
	public String llistat(
			HttpServletRequest request,
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "dir", required = false) String dir,
			@RequestParam(value = "objectsPerPage", required = false) String objectsPerPage,
			ModelMap model) {
		int pagina = (page != null) ? new Integer(page).intValue() : 1;
		int firstRow = (pagina - 1) * getObjectsPerPage(objectsPerPage);
		boolean isAsc = "asc".equals(dir);
		model.addAttribute(
				"llistat",
				newPaginatedList(
						pagina,
						sort,
						isAsc,
						getObjectsPerPage(objectsPerPage),
						entornService.countAll(),
						entornService.findPagedAndOrderedAll(
								sort,
								isAsc,
								firstRow,
								getObjectsPerPage(objectsPerPage))));
		return "entorn/llistat";
	}

	@RequestMapping(value = {"/entorn/form", "/noaentorn/form"}, method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			ModelMap model) {
		setIsAdmin(request, model);
		return "entorn/form";
	}
	@RequestMapping(value = {"/entorn/form", "/noaentorn/form"}, method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") Entorn command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		if ("submit".equals(submit) || submit.length() == 0) {
			annotationValidator.validate(command, result);
			additionalValidator.validate(command, result);
	        if (result.hasErrors()) {
	        	setIsAdmin(request, model);
	        	return "entorn/form";
	        }
	        Entorn saved = null;
	        try {
	        	if (command.getId() == null)
	        		saved = entornService.create(command);
	        	else
	        		saved = entornService.update(command);
	        	missatgeInfo(request, "L'entorn s'ha guardat correctament");
	        	status.setComplete();
	        } catch (Exception ex) {
	        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut guardar el registre", ex);
	        	setIsAdmin(request, model);
	        	return "entorn/form";
	        }
	        if (isAdmin(request))
	        	return "redirect:/entorn/llistat.html";
	        else
	        	return "redirect:/noaentorn/form.html?id=" + saved.getId();
		} else {
			if (isAdmin(request))
				return "redirect:/entorn/llistat.html";
			else
				return "redirect:/noaentorn/form.html?id=" + command.getId();
		}
	}

	@RequestMapping(value = "/entorn/delete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		entornService.delete(id);
		missatgeInfo(request, "L'entorn s'ha esborrat correctament");
		return "redirect:/entorn/llistat.html";
	}

	@RequestMapping(value = "/entorn/exportar")
	public String exportar(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "id", required = true) Long entornId) {
		Entorn entorn = entornService.getById(entornId);
		String filename = "entorn_" + entorn.getCodi() + ".exp";
		model.addAttribute("filename", filename);
		model.addAttribute("data", entornService.exportar(entornId));
		return "serialitzarView";
	}

	@RequestMapping(value = "/entorn/importar")
	public String importar(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "id", required = true) Long entornId,
			@RequestParam("arxiu") final MultipartFile multipartFile) {
		try {
			if (multipartFile.getBytes() == null || multipartFile.getBytes().length == 0) {
				missatgeError(request, "No s'ha especificat l'arxiu a importar");
				return "redirect:/entorn/form.html?id=" + entornId;
			}
			InputStream is = new ByteArrayInputStream(multipartFile.getBytes());
	    	ObjectInputStream input = new ObjectInputStream(is);
	    	Object deserialitzat = input.readObject();
	    	if (deserialitzat instanceof EntornExportacio) {
	    		EntornExportacio exportacio = (EntornExportacio)deserialitzat;
	    		entornService.importar(entornId, exportacio);
	    		missatgeInfo(request, "Les dades de l'entorn s'han importat correctament");
	        	return "redirect:/entorn/llistat.html";
	    	} else {
	    		missatgeError(request, "Aquest arxiu no és un arxiu d'exportació vàlid");
	    	}
		} catch (IOException ex) {
			logger.error("Error llegint l'arxiu a importar", ex);
			missatgeError(request, "Error llegint l'arxiu a importar: " + ex.getMessage());
		} catch (ClassNotFoundException ex) {
			logger.error("Error llegint l'arxiu a importar", ex);
			missatgeError(request, "Error llegint l'arxiu a importar: " + ex.getMessage());
		} catch (Exception ex) {
			logger.error("Error en la importació de dades", ex);
			missatgeError(request, "Error en la importació de dades: " + ex.getMessage());
		}
		return "redirect:/entorn/form.html?id=" + entornId;
	}



	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	protected class EntornValidator implements Validator {
		private EntornService entornService;
		public EntornValidator(EntornService entornService) {
			this.entornService = entornService;
		}
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Entorn.class);
		}
		public void validate(Object target, Errors errors) {
			Entorn command = (Entorn)target;
			Entorn repetit = entornService.findAmbCodi(command.getCodi());
			if (repetit != null && !repetit.getId().equals(command.getId())) {
				errors.rejectValue("codi", "error.entorn.codi.repetit");
			}
		}
	}



	private boolean isAdmin(HttpServletRequest request) {
		return !request.getRequestURI().contains("/noaentorn/");
	}
	private void setIsAdmin(
			HttpServletRequest request,
			ModelMap model) {
		model.addAttribute("isAdmin", isAdmin(request));
	}

	public class ImportCommand {
		private Long id;
		private byte[] arxiu;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public byte[] getArxiu() {
			return arxiu;
		}
		public void setArxiu(byte[] arxiu) {
			this.arxiu = arxiu;
		}
	}

	private static final Log logger = LogFactory.getLog(EntornController.class);

}
