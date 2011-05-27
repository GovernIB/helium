/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

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
 * @author Limit Tecnologies <limit@limit.es>
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
				missatgeError(request, getMessage("error.especificar.dest.delegacio") );
			} else {
				try {
					tascaService.delegacioCrear(
							entorn.getId(),
							command.getTaskId(),
							command.getActorId(),
							command.getComentari(),
							command.isSupervisada());
					missatgeInfo(request, getMessage("info.tasca.delegada") );
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut delegar la tasca " + taskId, ex);
				}
			}
			return "redirect:/tasca/info.html?id=" + taskId;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
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
				missatgeInfo(request, getMessage("info.delegacio.cancelat") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut cancel·lar la delegació de la tasca " + taskId, ex);
			}
			return "redirect:/tasca/info.html?id=" + taskId;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}


	private static final Log logger = LogFactory.getLog(TascaDelegacioController.class);

}
