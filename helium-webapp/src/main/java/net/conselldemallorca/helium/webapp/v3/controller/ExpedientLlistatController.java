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

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientConsultaCommand;
import net.conselldemallorca.helium.webapp.v3.datatables.DatatablesPagina;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.PaginacioHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseStatus;

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
		filtre(request, filtreCommand, bindingResult, accio);
		return "redirect:expedient";
	}
	@RequestMapping(value = "/filtre", method = RequestMethod.POST)
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
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesPagina<ExpedientDto> datatable(
			/*@RequestParam(value = "numero", required = false) String numero,
			@RequestParam(value = "titol", required = false) String titol,
			@RequestParam(value = "expedientTipus", required = false) Long expedientTipusId,
			@RequestParam(value = "estat", required = false) String estat,
			@RequestParam(value = "dataIniciInicial", required = false) Date dataIniciInicial,
			@RequestParam(value = "dataIniciFinal", required = false) Date dataIniciFinal,
			@RequestParam(value = "dataFiInicial", required = false) Date dataFiInicial,
			@RequestParam(value = "dataFiFinal", required = false) Date dataFiFinal,
			@RequestParam(value = "geoposicio", required = false) String geoposicio,
			@RequestParam(value = "geoposX", required = false) Double geoposX,
			@RequestParam(value = "geoposY", required = false) Double geoposY,
			@RequestParam(value = "nomesPendents", required = false) Boolean nomesPendents,
			@RequestParam(value = "nomesAlertes", required = false) Boolean nomesAlertes,
			@RequestParam(value = "mostrarAnulats", required = false) Boolean mostrarAnulats,
			@RequestParam(value = "netejar", required = false) Boolean netejar,*/
			HttpServletRequest request,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientConsultaCommand filtreCommand = getFiltreCommand(request);
		/*if (netejar) {
			SessionHelper.removeAttribute(
					request,
					SessionHelper.VARIABLE_FILTRE_CONSULTA_GENERAL);
			SessionHelper.removeAttribute(
					request,
					SessionHelper.VARIABLE_SELECCIO_CONSULTA_GENERAL);
			filtreCommand = getFiltreCommand(request);
		} else {
			filtreCommand = getFiltreCommand(request);
			filtreCommand.setNumero(numero);
			filtreCommand.setTitol(titol);
			filtreCommand.setExpedientTipusId(expedientTipusId);
			if (estat != null) {
				if ("INICIAT".equals(estat)) filtreCommand.setEstatTipus(EstatTipusDto.INICIAT);
				else if ("FINALITZAT".equals(estat)) filtreCommand.setEstatTipus(EstatTipusDto.FINALITZAT);
				else {
					try {
						Long estatId = Long.parseLong(estat);
						filtreCommand.setEstatTipus(EstatTipusDto.CUSTOM);
						filtreCommand.setEstatId(estatId);
					} catch (Exception e) {}
				}
			} else {
				filtreCommand.setEstatTipus(EstatTipusDto.CUSTOM);
				filtreCommand.setEstatId(null);
			}
			filtreCommand.setDataIniciInicial(dataIniciInicial);
			filtreCommand.setDataIniciFinal(dataIniciFinal);
			filtreCommand.setDataFiInicial(dataFiInicial);
			filtreCommand.setDataFiFinal(dataFiFinal);
			filtreCommand.setGeoReferencia(geoposicio);
			filtreCommand.setGeoPosX(geoposX);
			filtreCommand.setGeoPosY(geoposY);
		}
		if (nomesPendents != null) filtreCommand.setNomesPendents(nomesPendents);
		if (nomesAlertes != null) filtreCommand.setNomesAlertes(nomesAlertes);
		if (mostrarAnulats != null) filtreCommand.setMostrarAnulats(mostrarAnulats);*/
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_GENERAL,
				filtreCommand);
		return PaginacioHelper.getPaginaPerDatatables(
				request,
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
						PaginacioHelper.getPaginacioDtoFromDatatable(request)));
	}

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

	@RequestMapping(value = "/seleccionarTots", method = RequestMethod.POST)
	@ResponseBody
	public Set<Long> seleccionarTots(HttpServletRequest request) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientConsultaCommand filtreCommand = getFiltreCommand(request);
		
		List<Long> ids = expedientService.findIdsPerConsultaGeneral(
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

/*	@RequestMapping(value = "/seleccionar/{expedientId}", method = RequestMethod.GET)
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
	}*/

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

	@RequestMapping(value = "/{expedientId}/suspend", method = RequestMethod.POST)
	public String suspend(HttpServletRequest request, 
			@PathVariable Long expedientId, 
			@RequestParam(value = "motiu", required = true) String motiu,
			Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {		
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.anular(entorn.getId(), expedientId, motiu);
					MissatgesHelper.info(request, getMessage(request, "info.expedient.anulat") );
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.anular.expedient"));
		        	logger.error("No s'ha pogut anular el registre", ex);
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.anular.expedient"));				
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/delete", method = RequestMethod.GET)
	public String deleteAction(
			HttpServletRequest request,
			@PathVariable Long expedientId) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.delete(entorn.getId(), expedientId);
					MissatgesHelper.info(request, getMessage(request, "info.expedient.esborrat") );
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.esborrar.expedient") );
		        	logger.error("No s'ha pogut esborrar el registre", ex);
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.esborrar.expedient") );
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
		}
		return "redirect:/v3/expedient";
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

	private static final Logger logger = LoggerFactory.getLogger(ExpedientLlistatController.class);

}
