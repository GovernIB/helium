/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientConsultaCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.PaginacioHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientLlistatController {

	private static final String[] COLUMNES_LLISTAT = new String[] {"id", "identificador", "dataInici", "dataFi", "tipus.nom", "estat.nom", "aturat", "anulat", "id"};
	private static final Map<String, String[]> COLUMNES_MAPEIG_ORDENACIO;
	static {
		COLUMNES_MAPEIG_ORDENACIO = new HashMap<String, String[]>();
		COLUMNES_MAPEIG_ORDENACIO.put("identificador", new String[] {"numero", "titol"});
		COLUMNES_MAPEIG_ORDENACIO.put("estat.nom", new String[] {"estat", "dataFi", "dataInici"});
	}

	@Resource(name="expedientServiceV3")
	private ExpedientService expedientService;
	@Resource(name="dissenyServiceV3")
	private DissenyService dissenyService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(getFiltreCommand(request));
		omplirModelGet(request, model);
		return "v3/expedientLlistat";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ExpedientConsultaCommand filtreCommand,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			MissatgesHelper.error(
					request,
					"S'han produït errors en la validació dels camps del filtre");
			omplirModelGet(request, model);
			return "v3/expedientLlistat";
		} else {
			SessionHelper.setAttribute(
					request,
					SessionHelper.VARIABLE_FILTRE_CONSULTA_GENERAL,
					filtreCommand);
			return "redirect:expedient";
		}
		
	}

	@RequestMapping(value = "/filtre/netejar", method = RequestMethod.GET)
	public String filtreNetejar(HttpServletRequest request) {
		SessionHelper.removeAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_GENERAL);
		SessionHelper.removeAttribute(
				request,
				SessionHelper.VARIABLE_SELECCIO_CONSULTA_GENERAL);
		return "redirect:../../expedient";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	public String datatable(
			HttpServletRequest request,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientConsultaCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(
				"pagina",
				PaginacioHelper.getPaginaPerDatatables(
						request,
						COLUMNES_LLISTAT,
						expedientService.findPerConsultaGeneralPaginat(
								entornActual.getId(),
								filtreCommand.getExpedientTipusId(),
								filtreCommand.getTitol(),
								filtreCommand.getNumero(),
								filtreCommand.getDataIniciInicial(),
								filtreCommand.getDataIniciFinal(),
								filtreCommand.getDataFiInicial(),
								filtreCommand.getDataFiFinal(),
								filtreCommand.getEstatTipus(),
								filtreCommand.getEstatId(),
								filtreCommand.getGeoPosX(),
								filtreCommand.getGeoPosY(),
								filtreCommand.getGeoReferencia(),
								filtreCommand.isNomesPendents(),
								filtreCommand.isNomesAlertes(),
								filtreCommand.isMostrarAnulats(),
								PaginacioHelper.getPaginacioDtoFromDatatable(
										request,
										COLUMNES_LLISTAT,
										COLUMNES_MAPEIG_ORDENACIO))));
		return "v3/datatableData";
	}

	@RequestMapping(value = "/seleccionar/{expedientId}", method = RequestMethod.GET)
	@ResponseBody
	public Set<Long> seleccionar(
			HttpServletRequest request,
			@PathVariable String expedientId) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaGeneral();
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioConsultaGeneral(seleccio);
		}
		String[] ids = (expedientId.contains(",")) ? expedientId.split(",") : new String[] {expedientId};
		for (String id: ids) {
			try {
				seleccio.add(Long.parseLong(id));
			} catch (NumberFormatException ex) {}
		}
		return seleccio;
	}
	@RequestMapping(value = "/deseleccionar/{expedientId}", method = RequestMethod.GET)
	@ResponseBody
	public Set<Long> deseleccionar(
			HttpServletRequest request,
			@PathVariable String expedientId) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaGeneral();
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioConsultaGeneral(seleccio);
		}
		String[] ids = (expedientId.contains(",")) ? expedientId.split(",") : new String[] {expedientId};
		for (String id: ids) {
			try {
				seleccio.remove(Long.parseLong(id));
			} catch (NumberFormatException ex) {}
		}
		return seleccio;
	}

	@RequestMapping(value = "/estatsPerTipus/{expedientTipusId}", method = RequestMethod.GET)
	@ResponseBody
	public List<EstatDto> estatsPerExpedientTipus(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId) {
		return dissenyService.findEstatByExpedientTipus(expedientTipusId);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}


	private void omplirModelGet(
			HttpServletRequest request,
			Model model) {
		ExpedientConsultaCommand filtreCommand = getFiltreCommand(request);
		if (filtreCommand.getExpedientTipusId() != null) {
			model.addAttribute(
					"estats",
					dissenyService.findEstatByExpedientTipus(
							filtreCommand.getExpedientTipusId()));
		}
	}
	private ExpedientConsultaCommand getFiltreCommand(
			HttpServletRequest request) {
		ExpedientConsultaCommand filtreCommand = SessionHelper.getSessionManager(request).getFiltreConsultaGeneral();
		if (filtreCommand == null) {
			filtreCommand = new ExpedientConsultaCommand();
			SessionHelper.setAttribute(
					request,
					SessionHelper.VARIABLE_FILTRE_CONSULTA_GENERAL,
					filtreCommand);
		}
		return filtreCommand;
	}

}
