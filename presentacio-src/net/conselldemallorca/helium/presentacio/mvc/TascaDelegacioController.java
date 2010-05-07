/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per a la delegació de tasques
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class TascaDelegacioController extends BaseController {

	private TascaService tascaService;



	@Autowired
	public TascaDelegacioController(
			TascaService tascaService) {
		this.tascaService = tascaService;
	}

	@RequestMapping(value = "/tasca/delegacioCrear")
	public String crear(
			HttpServletRequest request,
			@RequestParam(value = "taskId", required = true) String taskId,
			@ModelAttribute("command") TascaDelegacioCommand command,
			BindingResult result,
			SessionStatus status) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (command.getActorId() == null || command.getActorId().equals("")) {
				missatgeError(request, "No ha especificat cap destinatari per a la delegació");
			} else {
				try {
					tascaService.delegacioCrear(
							entorn.getId(),
							command.getTaskId(),
							command.getActorId(),
							command.getComentari(),
							command.isSupervisada());
					missatgeInfo(request, "La tasca ha estat delegada amb èxit");
				} catch (Exception ex) {
					missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut delegar la tasca " + taskId, ex);
				}
			}
			return "redirect:/tasca/info.html?id=" + taskId;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/delegacioCancelar")
	public String cancelar(
			HttpServletRequest request,
			@RequestParam(value = "taskId", required = true) String taskId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				tascaService.delegacioCancelar(entorn.getId(), taskId);
				missatgeInfo(request, "La delegació s'ha cancel·lat correctament");
			} catch (Exception ex) {
				missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut cancel·lar la delegació de la tasca " + taskId, ex);
			}
			return "redirect:/tasca/info.html?id=" + taskId;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}


	private static final Log logger = LogFactory.getLog(TascaDelegacioController.class);

}
