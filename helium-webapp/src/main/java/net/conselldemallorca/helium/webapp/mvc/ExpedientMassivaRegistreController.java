/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;

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
 * a dins l'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientMassivaRegistreController extends CommonRegistreController {

	public static final String CLAU_SESSIO_PREFIX_REGISTRE_MASS = "Modificar_reg_massiu_";
	
	private ExpedientService expedientService;

	@Autowired
	public ExpedientMassivaRegistreController(
			ExpedientService expedientService,
			DissenyService dissenyService) {
		super(dissenyService);
		this.expedientService = expedientService;
	}

	@ModelAttribute("instanciaProces")
	public InstanciaProcesDto populateInstanciaProces(
			@RequestParam(value = "id", required = true) String id) {
		return expedientService.getInstanciaProcesById(id, true, true, true);
	}
	
	@RequestMapping(value = "/expedient/varRegistreMassiva", method = RequestMethod.GET)
	public String registreGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "registreId", required = true) Long registreId,
			@RequestParam(value = "campCodi", required = true) String campCodi,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) String correu) {
		if (getRegistreMassiuSessio(request, id, campCodi) == null)
			setRegistreMassiuSessio(request, id, campCodi, getValorRegistre(request, campCodi, id));
		return super.registreGet(request); 
	}
	@RequestMapping(value = "/expedient/varRegistreMassiva", method = RequestMethod.POST)
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

	@RequestMapping(value = "/expedient/varRegistreMassivaEsborrar")
	public String esborrarMembre(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "registreId", required = true) Long registreId,
			@RequestParam(value = "index", required = true) int index) {
		return super.esborrarMembre(request, id, registreId, index);
	}

	@Override
	public void esborrarRegistre(
			HttpServletRequest request,
			String id,
			String campCodi,
			boolean multiple,
			int index) {
		
		if (multiple) {
			Object valor = getRegistreMassiuSessio(request, id, campCodi);
			if (valor != null) {
				Object[] valorMultiple = (Object[])valor;
				if (valorMultiple.length > 0) {
					Object[] valorNou = new Object[valorMultiple.length - 1];
					for (int i = 0; i < valorNou.length; i++)
						valorNou[i] = (i < index) ? valorMultiple[i] : valorMultiple[i + 1];
						setRegistreMassiuSessio(request, id, campCodi, valorNou);
				}
			}
		} else {
			setRegistreMassiuSessio(request, id, campCodi, new Object[0]);
		}
		
//		expedientService.esborrarRegistre(id, campCodi, index);
	}
	@Override
	public Object[] getValorRegistre(
			HttpServletRequest request,
			Long entornId,
			String id,
			String campCodi) {
		return (Object[])expedientService.getVariable(id, campCodi);
	}
	@Override
	public void guardarRegistre(
			HttpServletRequest request,
			String id,
			String campCodi,
			boolean multiple,
			Object[] valors,
			int index) {
		
		if (multiple) {
			Object valor = getRegistreMassiuSessio(request, id, campCodi);
			if (valor == null) {
				setRegistreMassiuSessio(request, id, campCodi, valors);
			} else {
				Object[] valorMultiple = (Object[])valor;
				if (index != -1) {
					valorMultiple[index] = valors;
					setRegistreMassiuSessio(request, id, campCodi, valorMultiple);
				} else {
					Object[] valorNou = new Object[valorMultiple.length + 1];
					for (int i = 0; i < valorMultiple.length; i++)
						valorNou[i] = valorMultiple[i];
					valorNou[valorMultiple.length] = valors;
					setRegistreMassiuSessio(request, id, campCodi, valorNou);
				}
			}
		} else {
			setRegistreMassiuSessio(request, id, campCodi, valors);
		}
		
//		expedientService.guardarRegistre(id, campCodi, valors, index);
	}
	@Override
	public void guardarRegistre(
			HttpServletRequest request,
			String id,
			String registre,
			boolean multiple,
			Object[] valors) {
		
		guardarRegistre(request, id, registre, multiple, valors, -1);

//		expedientService.guardarRegistre(id, campCodi, valors);
	}
	@Override
	public String redirectUrl(String id, String campCodi) {
		return "redirect:/expedient/dadaModificarMas.html?id=" + id + "&var=" + campCodi+ "&inici=&correu=false";
	}
	@Override
	public String registreUrl() {
		return "expedient/varRegistreMassiva";
	}

	public static void setRegistreMassiuSessio(HttpServletRequest request, String id, String campCodi, Object[] valor) {
		request.getSession().setAttribute(getRegistreMassiuSessioKey(id, campCodi), valor);
	}
	public static Object[] getRegistreMassiuSessio(HttpServletRequest request, String id, String campCodi) {
		return (Object[])request.getSession().getAttribute(getRegistreMassiuSessioKey(id, campCodi));
	}
	
	public static void removeRegistreMassiuSessio(HttpServletRequest request, String id, String campCodi) {
		request.getSession().removeAttribute(getRegistreMassiuSessioKey(id, campCodi));
	}
	public static String getRegistreMassiuSessioKey(String id, String campCodi) {
		return ExpedientMassivaRegistreController.CLAU_SESSIO_PREFIX_REGISTRE_MASS + campCodi + "_" + id;
	}
}
