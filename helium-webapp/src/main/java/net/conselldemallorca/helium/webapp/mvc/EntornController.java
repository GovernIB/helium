/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.exportacio.EntornExportacio;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.EntornService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;



/**
 * Controlador per la gestió d'entorns
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class EntornController extends BaseController {

	private EntornService entornService;
	private ExpedientService expedientService;
	private DissenyService dissenyService;
	
	private Validator annotationValidator;
	private Validator additionalValidator;



	@Autowired
	public EntornController(
			EntornService entornService,
			ExpedientService expedientService,
			DissenyService dissenyService) {
		this.entornService = entornService;
		this.expedientService = expedientService;
		this.dissenyService = dissenyService;
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
	        	missatgeInfo(request, getMessage("info.entorn.guardat") );
	        	status.setComplete();
	        } catch (Exception ex) {
	        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
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
		try {
			entornService.delete(id);
		} catch (Exception ex) {
			missatgeError(request, getMessage("error.proces.peticio"), ex.getMessage());
        	logger.error("No s'ha pogut eliminar l'entorn", ex);
        	return "redirect:/entorn/llistat.html";
		}
		missatgeInfo(request, getMessage("info.entorn.esborrat") );
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
				missatgeError(request, getMessage("error.especificar.arxiu.importar") );
				return "redirect:/entorn/form.html?id=" + entornId;
			}
			InputStream is = new ByteArrayInputStream(multipartFile.getBytes());
	    	ObjectInputStream input = new ObjectInputStream(is);
	    	Object deserialitzat = input.readObject();
	    	if (deserialitzat instanceof EntornExportacio) {
	    		EntornExportacio exportacio = (EntornExportacio)deserialitzat;
	    		entornService.importar(entornId, exportacio);
	    		missatgeInfo(request, getMessage("info.entorn.dades.importat") );
	        	return "redirect:/entorn/llistat.html";
	    	} else {
	    		missatgeError(request, getMessage("error.arxius.no.valid") );
	    	}
		} catch (IOException ex) {
			logger.error("Error llegint l'arxiu a importar", ex);
			missatgeError(request, getMessage("error.arxiu.importar") + ex.getMessage());
		} catch (ClassNotFoundException ex) {
			logger.error("Error llegint l'arxiu a importar", ex);
			missatgeError(request, getMessage("error.arxiu.importar") + ex.getMessage());
		} catch (Exception ex) {
			logger.error("Error en la importació de dades", ex);
			missatgeError(request, getMessage("error.import.dades") + ex.getMessage());
		}
		return "redirect:/entorn/form.html?id=" + entornId;
	}

	@RequestMapping(value = "/entorn/reindexar")
	public String reindexar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		expedientService.luceneReindexarEntorn(id);
		missatgeInfo(request, getMessage("info.entorn.reindexat") );
		return "redirect:/entorn/llistat.html";
	}

	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}
	
	@RequestMapping(value = "/entorn/netejar_df", method = RequestMethod.GET)
	public String netejar_df(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		model.addAttribute("entornId", id);
		model.addAttribute("llistat", dissenyService.findDefinicionsProcesNoUtilitzadesEntorn(id));
		return "/entorn/llistatDpNoUs";
	}

	@RequestMapping(value = "/entorn/netejar_df", method = RequestMethod.POST)
	public String netejar_df(
			HttpServletRequest request,
			@RequestParam(value = "entornId", required = true) Long entornId,
			@RequestParam(value = "dpId", required = false) Long[] dpId,
			ModelMap model) {
		Entorn entorn = entornService.getById(entornId);
		List<Long> dfBorrar = new ArrayList<Long>();
		for (Long definicioProcesId : dpId) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entornId, definicioProcesId);
			try {
				List<Consulta> consultes = dissenyService.findConsultesAmbEntorn(entorn.getId());
				boolean esborrar = true;
				if (consultes.isEmpty()) {
					if (!dfBorrar.contains(definicioProcesId)) {
						dfBorrar.add(definicioProcesId);
					}
				} else {
					for(Consulta consulta: consultes){
						Set<ConsultaCamp> llistat = consulta.getCamps();
						for(ConsultaCamp c: llistat){
							if((definicioProces.getVersio() == c.getDefprocVersio()) && (definicioProces.getJbpmKey().equals(c.getDefprocJbpmKey()))){
								esborrar = false;
							}
						}
						if(!esborrar){
							missatgeError(request, getMessage("error.exist.cons.df", new Object[]{consulta.getNom(), definicioProces.getJbpmName(), definicioProces.getVersio()}) );
						} else {
							if (!dfBorrar.contains(definicioProcesId)) {
								dfBorrar.add(definicioProcesId);
							}
						}
					}
				}
				if (dfBorrar.contains(definicioProcesId)) {
					try {
						dissenyService.undeploy(entorn.getId(), null, definicioProcesId);
						missatgeInfo(request, getMessage("info.defproc.esborrada", new Object[]{definicioProces.getJbpmName(), definicioProces.getVersio()}) );
					} catch (DataIntegrityViolationException ex) {
						missatgeError(request, getMessage("error.defpro.eliminar.constraint", new Object[] {definicioProces.getJbpmName(), definicioProces.getVersio()}));
						logger.error("No s'han pogut esborrar les definicions de procés", ex);
					}
				}
	        } catch (Exception ex) {
	        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
	        	logger.error("No s'han pogut esborrar les definicions de procés", ex);
	        }
		}
		
//		try {
//			if (!dfBorrar.isEmpty()) {
//				dissenyService.undeploy(entorn.getId(), dfBorrar);
//				if (msg.length() > 0) 
//					msg = msg.substring(0, msg.length() - 2);
//				missatgeInfo(request, getMessage("info.defproc.esborrades", new Object[]{msg}) );
//			}
//		} catch (Exception ex) {
//			missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
//			logger.error("No s'han pogut esborrar les definicions de procés", ex);
//		}
		model.addAttribute("entornId", entornId);
		model.addAttribute("llistat", dissenyService.findDefinicionsProcesNoUtilitzadesEntorn(entornId));
		return "/entorn/llistatDpNoUs";
	}

	protected class EntornValidator implements Validator {
		private EntornService entornService;
		public EntornValidator(EntornService entornService) {
			this.entornService = entornService;
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
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
