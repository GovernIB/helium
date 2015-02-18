package net.conselldemallorca.helium.webapp.v3.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.command.TascaConsultaCommand;
import net.conselldemallorca.helium.webapp.v3.datatables.DatatablesPagina;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.PaginacioHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controlador per al llistat de tasques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/tasca")
public class TascaLlistatV3Controller extends BaseExpedientController {
	
	@Autowired
	private AdminService adminService;
	@Autowired
	private TascaService tascaService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		TascaConsultaCommand filtreCommand = getFiltreCommand(request);
		filtreCommand.setConsultaTramitacioMassivaTascaId(null);
		model.addAttribute(filtreCommand);
		EntornDto entornActual = (EntornDto) SessionHelper.getAttribute(request, SessionHelper.VARIABLE_ENTORN_ACTUAL_V3);
		if (entornActual == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
		} else {
			model.addAttribute("entornId", entornActual.getId());
		}
		return "v3/tascaLlistat";
	}

	@ModelAttribute("prioritats")
	public List<ParellaCodiValorDto> populateEstats(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(getMessage(request, "txt.m_alta"), 2));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "txt.alta"), 1));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "txt.normal"), 0));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "txt.baixa"), -1));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "txt.m_baixa"), -2));
		return resposta;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid TascaConsultaCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		filtre(request, filtreCommand, bindingResult, accio);
		return "redirect:tasca";
	}

	@RequestMapping(value = "/{tascaId}/massiva", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public String tramitarMassiva(
			HttpServletRequest request,
			@PathVariable String tascaId, 
			@Valid TascaConsultaCommand filtreCommand,
			BindingResult bindingResult) {
		filtreCommand.setConsultaTramitacioMassivaTascaId(tascaId);
		SessionHelper.getSessionManager(request).setFiltreConsultaTasca(filtreCommand);
		return "v3/tascaLlistat";
	}
	
	@RequestMapping(value = "/filtre", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void filtre(
			HttpServletRequest request,
			@Valid TascaConsultaCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			SessionHelper.getSessionManager(request).removeFiltreConsultaTasca();
		} else {
			SessionHelper.getSessionManager(request).setFiltreConsultaTasca(filtreCommand);
		}
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesPagina<ExpedientTascaDto> datatable(
			HttpServletRequest request,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		TascaConsultaCommand filtreCommand = getFiltreCommand(request);
		SessionHelper.getSessionManager(request).setFiltreConsultaTasca(filtreCommand);
		DatatablesPagina<ExpedientTascaDto> result = null;
		try {
			result = PaginacioHelper.getPaginaPerDatatables(
					request,
					tascaService.findPerFiltrePaginat(
							entornActual.getId(),
							filtreCommand.getConsultaTramitacioMassivaTascaId(),
							filtreCommand.getExpedientTipusId(),
							request.getUserPrincipal().getName(),
							filtreCommand.getTitol(),
							filtreCommand.getTasca(),
							filtreCommand.getResponsable(),
							filtreCommand.getExpedient(),
							filtreCommand.getDataCreacioInicial(),
							filtreCommand.getDataCreacioFinal(),
							filtreCommand.getDataLimitInicial(),
							filtreCommand.getDataLimitFinal(),
							filtreCommand.getPrioritat(),
							filtreCommand.isMostrarTasquesPersonals(),
							filtreCommand.isMostrarTasquesGrup(),
							PaginacioHelper.getPaginacioDtoFromDatatable(request)));
		} catch (Exception e) {
			if (entornActual == null)
				MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			else {
				MissatgesHelper.error(request, e.getMessage());
				logger.error("No se pudo obtener la lista de tareas", e);
			}
			result = PaginacioHelper.getPaginaPerDatatables(
					request,
					new PaginaDto<ExpedientTascaDto>());
		}
		return result;
	}

	@RequestMapping(value = "/tasques/{entornId}/{expedientTipusId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> definicioProcesByTipusExpedient(
			@PathVariable Long entornId,
			@PathVariable Long expedientTipusId,
			Model model) {
		return dissenyService.findTasquesAmbDefinicioProcesByTipusExpedientIdByEntornId(entornId, expedientTipusId);
	}

	@RequestMapping(value = "/seleccioTots")
	@ResponseBody
	public Set<Long> seleccioTots(HttpServletRequest request) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		TascaConsultaCommand filtreCommand = getFiltreCommand(request);
		List<Long> ids = tascaService.findIdsPerFiltre(
			entornActual.getId(),
			filtreCommand.getExpedientTipusId(),
			request.getUserPrincipal().getName(),
			filtreCommand.getTitol(),
			filtreCommand.getTasca(),
			filtreCommand.getResponsable(),
			filtreCommand.getExpedient(),
			filtreCommand.getDataCreacioInicial(),
			filtreCommand.getDataCreacioFinal(),
			filtreCommand.getDataLimitInicial(),
			filtreCommand.getDataLimitFinal(),
			filtreCommand.getPrioritat(),
			filtreCommand.isMostrarTasquesPersonals(),
			filtreCommand.isMostrarTasquesGrup());
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioConsultaTasca(seleccio);
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

	@RequestMapping(value = "/seleccioNetejar")
	@ResponseBody
	public Set<Long> seleccioNetejar(HttpServletRequest request) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> ids = sessionManager.getSeleccioConsultaTasca();
		ids.clear();
		return ids;
	}
	
	@RequestMapping(value = "/selection", method = RequestMethod.POST)
	@ResponseBody
	public Set<Long> seleccio(
			HttpServletRequest request,
			@RequestParam(value = "ids", required = false) String ids) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioConsultaTasca(seleccio);
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

	@RequestMapping(value = "/filtre/netejar", method = RequestMethod.GET)
	public String filtreNetejar(HttpServletRequest request) {
		SessionHelper.getSessionManager(request).removeFiltreConsultaTasca();
		return "redirect:/v3/tasca";
	}
	
	@RequestMapping(value = "/pendentsCompletar", method = RequestMethod.GET)
	public String tasquesCompletar(HttpServletRequest request, 
			Model model) {		
		model.addAttribute("tasques", adminService.getTasquesCompletar());
		return "v3/pendentsCompletar"; 
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}

	private TascaConsultaCommand getFiltreCommand(
			HttpServletRequest request) {
		TascaConsultaCommand filtreCommand = SessionHelper.getSessionManager(request).getFiltreConsultaTasca();
		if (filtreCommand == null) {
			filtreCommand = new TascaConsultaCommand();
			filtreCommand.setConsultaRealitzada(true);
			SessionHelper.getSessionManager(request).setFiltreConsultaTasca(filtreCommand);
		}
		ExpedientTipusDto expedientTipusActual = SessionHelper.getSessionManager(request).getExpedientTipusActual();
		if (expedientTipusActual != null)
			filtreCommand.setExpedientTipusId(expedientTipusActual.getId());
		return filtreCommand;
	}
	
	protected static final Log logger = LogFactory.getLog(TascaLlistatV3Controller.class);
}
