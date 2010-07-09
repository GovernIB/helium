/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.jbpm3.integracio.ValidationException;
import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * Controlador per la gestió de tasques
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class TascaController extends BaseController {

	private String TAG_FORM_INICI = "<!--helium:form-inici-->";
	private String TAG_FORM_FI = "<!--helium:form-fi-->";

	private TascaService tascaService;
	private DissenyService dissenyService;



	@Autowired
	public TascaController(
			TascaService tascaService,
			DissenyService dissenyService) {
		this.tascaService = tascaService;
		this.dissenyService = dissenyService;
	}

	@RequestMapping(value = "/tasca/personaLlistat")
	public String personaLlistat(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute("personaLlistat", tascaService.findTasquesPersonals(entorn.getId()));
			model.addAttribute("grupLlistat", tascaService.findTasquesGrup(entorn.getId()));
			return "tasca/personaLlistat";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/grupLlistat")
	public String grupLlistat(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute("personaLlistat", tascaService.findTasquesPersonals(entorn.getId()));
			model.addAttribute("grupLlistat", tascaService.findTasquesGrup(entorn.getId()));
			return "tasca/grupLlistat";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/info")
	public String info(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			TascaDelegacioCommand command = new TascaDelegacioCommand();
			command.setTaskId(id);
			model.addAttribute(
					"command",
					command);
			try {
				model.addAttribute(
						"tasca",
						tascaService.getById(entorn.getId(), id));
			} catch (Exception ex) {
				logger.error("S'ha produït un error processant la seva petició", ex);
				missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
				return "redirect:/tasca/personaLlistat.html";
			}
			return "tasca/info";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/agafar")
	public String agafar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				tascaService.agafar(entorn.getId(), id);
				missatgeInfo(request, "La tasca està disponible en el seu llistat de tasques personals");
				return "redirect:/tasca/info.html?id=" + id;
			} catch (Exception ex) {
	        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut agafar la tasca", ex);
	        	return "redirect:/tasca/grupLlistat.html";
	        }
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/completar")
	public String completar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "pipella", required = false) String pipella,
			@RequestParam(value = "submit", required = false) String submit,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			TascaDto tasca = tascaService.getById(entorn.getId(), id);
			try {
				boolean found = false;
				for (String outcome: tasca.getOutcomes()) {
					if (outcome != null && outcome.equals(submit)) {
						tascaService.completar(entorn.getId(), id, true, null, outcome);
						found = true;
						break;
					}
				}
				if (!found) {
					tascaService.completar(entorn.getId(), id, true);
				}
				missatgeInfo(request, "La tasca s'ha completat amb èxit");
				return "redirect:/tasca/personaLlistat.html";
			} catch (Exception ex) {
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					missatgeError(
		        			request,
		        			ex.getCause().getMessage());
				} else {
					missatgeError(
		        			request,
		        			"Error al finalitzar la tasca",
		        			(ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage());
				}
	        	logger.error("No s'ha pogut finalitzar la tasca", ex);
	        	if ("info".equals(pipella)) {
	        		return "redirect:/tasca/info.html?id=" + tasca.getId();
	        	} else if ("form".equals(pipella)) {
	        		return "redirect:/tasca/form.html?id=" + tasca.getId();
	        	} else if ("documents".equals(pipella)) {
	        		return "redirect:/tasca/documents.html?id=" + tasca.getId();
	        	} else if ("signatures".equals(pipella)) {
	        		return "redirect:/tasca/signatures.html?id=" + tasca.getId();
	        	} else {
	        		return "redirect:/tasca/info.html?id=" + tasca.getId();
	        	}
	        }
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/formRecurs")
	public String formRecurs(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				TascaDto tasca = tascaService.getById(entorn.getId(), id);
				byte[] contingut = dissenyService.getDeploymentResource(
						tasca.getDefinicioProces().getId(),
						tasca.getRecursForm());
				String text = textFormRecursProcessat(tasca, new String(contingut, "UTF-8"));
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_FILENAME,
						tasca.getRecursForm().substring(tasca.getRecursForm().lastIndexOf("/") + 1));
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_DATA,
						text.getBytes());
				return "arxiuView";
			} catch (Exception ex) {
				missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
				logger.error("No s'ha pogut mostrar l'arxiu", ex);
				return "redirect:/tasca/info.html?id=" + id;
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/executarAccio")
	public String executarAccio(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "accio", required = true) String accio,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				tascaService.executarAccio(
						entorn.getId(),
						id,
						accio);
				missatgeInfo(request, "L'acció s'ha executat amb èxit");
			} catch (Exception ex) {
				missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
				logger.error("No s'ha pogut mostrar l'arxiu", ex);
			}
			return "redirect:/tasca/form.html?id=" + id;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}



	private String textFormRecursProcessat(TascaDto tasca, String text) {
		int indexFormInici = text.indexOf(TAG_FORM_INICI);
		int indexFormFi = text.indexOf(TAG_FORM_FI);
		if (indexFormInici != -1 && indexFormFi != -1) {
			return text.substring(
					indexFormInici + TAG_FORM_INICI.length(),
					indexFormFi);
		}
		return null;
	}

	private static final Log logger = LogFactory.getLog(TascaController.class);

}
