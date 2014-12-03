/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientConsultaCommand;
import net.conselldemallorca.helium.webapp.v3.datatables.DatatablesPagina;
import net.conselldemallorca.helium.webapp.v3.helper.PaginacioHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientLlistatController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private DissenyService dissenyService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		ExpedientConsultaCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(filtreCommand);
		if (filtreCommand.isConsultaRealitzada()) {
			omplirModelGet(request, model);
		}
		return "v3/expedientLlistat";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ExpedientConsultaCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		//filtre(request, filtreCommand, bindingResult, accio);
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(
					request,
					SessionHelper.VARIABLE_FILTRE_CONSULTA_GENERAL);
		} else {
			SessionHelper.setAttribute(
					request,
					SessionHelper.VARIABLE_FILTRE_CONSULTA_GENERAL,
					filtreCommand);
		}
		return "redirect:expedient";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesPagina<ExpedientDto> datatable(
			HttpServletRequest request,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientConsultaCommand filtreCommand = getFiltreCommand(request);
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_GENERAL,
				filtreCommand);
		
		DatatablesPagina<ExpedientDto> result = null;
		try {
			result = PaginacioHelper.getPaginaPerDatatables(
					request,
					expedientService.findAmbFiltrePaginat(
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
							PaginacioHelper.getPaginacioDtoFromDatatable(request)));
		} catch (Exception e) {
			logger.error("No se pudo obtener la lista de expedientes", e);
		}
		return result;
	}
	/*@RequestMapping(value = "/filtre", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void filtre(
			HttpServletRequest request,
			@Valid ExpedientConsultaCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(
					request,
					SessionHelper.VARIABLE_FILTRE_CONSULTA_GENERAL);
		} else {
			SessionHelper.setAttribute(
					request,
					SessionHelper.VARIABLE_FILTRE_CONSULTA_GENERAL,
					filtreCommand);
		}
	}*/
	@RequestMapping(value = "/selection", method = RequestMethod.POST)
	@ResponseBody
	public Set<Long> seleccio(
			HttpServletRequest request,
			@RequestParam(value = "ids", required = false) String ids) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaGeneral();
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioConsultaGeneral(seleccio);
		}
		if (ids != null) {
			String[] idsparts = (ids.contains(",")) ? ids.split(",") : new String[] {ids};
			for (String id: idsparts) {
				try {
					long l = Long.parseLong(id.trim());
					if (l >= 0) {
						seleccio.add(l);
					} else {
						seleccio.remove(-l);
					}
				} catch (NumberFormatException ex) {}
			}
		}
		return seleccio;
	}

	@RequestMapping(value = "/seleccioTots")
	@ResponseBody
	public Set<Long> seleccioTots(HttpServletRequest request) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientConsultaCommand filtreCommand = getFiltreCommand(request);
		List<Long> ids = expedientService.findIdsAmbFiltre(
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
						filtreCommand.isMostrarAnulats());		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaGeneral();
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioConsultaGeneral(seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				try {
					if (id >= 0) {
						seleccio.add(id);
					} else {
						seleccio.remove(-id);
					}
				} catch (NumberFormatException ex) {}
			}
			Iterator<Long> iterador = seleccio.iterator();
			while( iterador.hasNext() ) {
				if (!ids.contains(iterador.next())) {
					iterador.remove();
				}
			}
		}
		return seleccio;
	}

	@RequestMapping(value = "/consultas/{expedientTipusId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ConsultaDto> consultasTipus(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		return dissenyService.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornActual.getId(),expedientTipusId);
	}

	@RequestMapping(value = "/seleccioNetejar")
	@ResponseBody
	public Set<Long> seleccioNetejar(HttpServletRequest request) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> ids = sessionManager.getSeleccioConsultaGeneral();
		ids.clear();
		return ids;
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
			filtreCommand.setConsultaRealitzada(true);
			SessionHelper.setAttribute(
					request,
					SessionHelper.VARIABLE_FILTRE_CONSULTA_GENERAL,
					filtreCommand);
		}
		ExpedientTipusDto expedientTipusActual = SessionHelper.getSessionManager(request).getExpedientTipusActual();
		if (expedientTipusActual != null)
			filtreCommand.setExpedientTipusId(expedientTipusActual.getId());
		return filtreCommand;
	}

	protected static final Log logger = LogFactory.getLog(ExpedientLlistatController.class);
}
