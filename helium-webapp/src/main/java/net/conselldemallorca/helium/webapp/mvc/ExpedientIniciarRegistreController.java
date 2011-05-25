/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.service.DissenyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió dels formularis dels camps de tipus registre
 * a dins les tasques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientIniciarRegistreController extends CommonRegistreController {

	public static final String PREFIX_REGISTRE_SESSIO = "ExpedientIniciarRegistreController_reg_";



	@Autowired
	public ExpedientIniciarRegistreController(
			DissenyService dissenyService) {
		super(dissenyService);
	}

	@RequestMapping(value = "/expedient/iniciarRegistre", method = RequestMethod.GET)
	public String registreGet(HttpServletRequest request) {
		return super.registreGet(request);
	}
	@RequestMapping(value = "/expedient/iniciarRegistre", method = RequestMethod.POST)
	public String registrePost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "registreId", required = true) Long registreId,
			@RequestParam(value = "index", required = false) Integer index,
			@RequestParam(value = "submit", required = true) String submit,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		return super.registrePost(request, id, registreId, index, submit, command, result, status, model);
	}

	@Override
	public void esborrarRegistre(HttpServletRequest request, String id, String campCodi, int index) {
		esborrarRegistre(request, campCodi, index);
	}
	@Override
	public Object[] getValorRegistre(HttpServletRequest request, Long entornId, String id, String campCodi) {
		return (Object[])request.getSession().getAttribute(PREFIX_REGISTRE_SESSIO + campCodi);
	}
	@Override
	public void guardarRegistre(HttpServletRequest request, String id, String campCodi, Object[] valors,
			int index) {
		guardarRegistre(request, campCodi, valors, index);
	}
	@Override
	public void guardarRegistre(HttpServletRequest request, String id, String campCodi, Object[] valors) {
		guardarRegistre(request, campCodi, valors, -1);
	}
	@Override
	public String redirectUrl(String id, String campCodi) {
		return "redirect:/expedient/iniciarPasForm.html?expedientTipusId=" + id;
	}
	@Override
	public String registreUrl() {
		return "expedient/iniciarRegistre";
	}



	private void guardarRegistre(
			HttpServletRequest request,
			String campCodi,
			Object[] valors,
			int index) {
		Object valor = request.getSession().getAttribute(PREFIX_REGISTRE_SESSIO + campCodi);
		if (valor == null) {
			request.getSession().setAttribute(
					PREFIX_REGISTRE_SESSIO + campCodi,
					new Object[]{valors});
		} else {
			Object[] valorMultiple = (Object[])valor;
			if (index != -1) {
				valorMultiple[index] = valors;
				request.getSession().setAttribute(
						PREFIX_REGISTRE_SESSIO + campCodi,
						valor);
			} else {
				Object[] valorNou = new Object[valorMultiple.length + 1];
				for (int i = 0; i < valorMultiple.length; i++)
					valorNou[i] = valorMultiple[i];
				valorNou[valorMultiple.length] = valors;
				request.getSession().setAttribute(
						PREFIX_REGISTRE_SESSIO + campCodi,
						valorNou);
			}
		}
	}
	public void esborrarRegistre(
			HttpServletRequest request,
			String campCodi,
			int index) {
		Object valor = request.getSession().getAttribute(PREFIX_REGISTRE_SESSIO + campCodi);
		if (valor != null) {
			Object[] valorMultiple = (Object[])valor;
			if (valorMultiple.length > 0) {
				Object[] valorNou = new Object[valorMultiple.length - 1];
				for (int i = 0; i < valorNou.length; i++)
					valorNou[i] = (i < index) ? valorMultiple[i] : valorMultiple[i + 1];
					request.getSession().setAttribute(
							PREFIX_REGISTRE_SESSIO + campCodi,
							valorNou);
			}
		}
	}

}
