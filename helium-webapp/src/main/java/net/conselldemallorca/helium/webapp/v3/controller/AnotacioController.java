package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.emiserv.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.dto.AnotacioAccioEnumDto;
import es.caib.helium.logic.intf.dto.AnotacioDto;
import es.caib.helium.logic.intf.dto.AnotacioEstatEnumDto;
import es.caib.helium.logic.intf.dto.AnotacioFiltreDto;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.ArxiuFirmaDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.ParellaCodiValorDto;
import es.caib.helium.logic.intf.service.AnotacioService;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.command.AnotacioAcceptarCommand;
import net.conselldemallorca.helium.webapp.v3.command.AnotacioAcceptarCommand.CrearIncorporar;
import net.conselldemallorca.helium.webapp.v3.command.AnotacioFiltreCommand;
import net.conselldemallorca.helium.webapp.v3.command.AnotacioRebutjarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.EnumHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per visualitzar la llista de peticions d'anotacions que han arribat a Helium
 * com a Backoffice de Distribució amb la possibilitat d'acceptar o rebutjar anotacions.
 *
 */
@Controller
@RequestMapping("/v3/anotacio")
public class AnotacioController extends BaseExpedientController {
	
	@Autowired
	private AnotacioService anotacioService;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	private @Autowired ExpedientIniciController expedientIniciController;

	private static final String SESSION_ATTRIBUTE_FILTRE = "AnotacioController.session.filtre";
	
	/** Accés al llistat d'anotacions des de l'opció a la capçalera per ususaris amb permís de relacionar expedients. */
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		AnotacioFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(filtreCommand);
		this.modelEstats(model);
		this.modelExpedientsTipus(SessionHelper.getSessionManager(request).getEntornActual(), model);
		return "v3/anotacioLlistat";
	}
	
	/** Mètode quan s'envia el formulari del filtre. Actualitza el filtre en sessió. */
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid AnotacioFiltreCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return "redirect:anotacio";
	}
	
	@RequestMapping(value="/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		AnotacioFiltreCommand filtreCommand = getFiltreCommand(request);
		
		return DatatablesHelper.getDatatableResponse(
					request,
					null,
					anotacioService.findAmbFiltrePaginat(
							entornActual.getId(),
							conversioTipusHelper.convertir(filtreCommand, AnotacioFiltreDto.class),
							DatatablesHelper.getPaginacioDtoFromRequest(request)));		
	}

	/** Mètode per obtenir o inicialitzar el filtre del formulari de cerca.
	 * 
	 * @param request
	 * @return
	 */
	private AnotacioFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		AnotacioFiltreCommand filtreCommand = (AnotacioFiltreCommand) SessionHelper.getAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new AnotacioFiltreCommand();
			filtreCommand.setEstat(AnotacioEstatEnumDto.PENDENT);
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return filtreCommand;
	}
	
	/** Mètode per veure el detall d'una anotació provinent de Distribució
	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String detall(
			HttpServletRequest request, 
			@PathVariable Long id,
			Model model) {
		AnotacioDto anotacio = anotacioService.findAmbId(id);
		model.addAttribute("anotacio", anotacio);
		return "v3/anotacioDetall";
	}
	
	/** Mètode per obrir el formulari per acceptar la petició d'anotació i processar-la
	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{anotacioId}/acceptar", method = RequestMethod.GET)
	public String acceptar(
			HttpServletRequest request, 
			@PathVariable Long anotacioId,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		AnotacioDto anotacio = anotacioService.findAmbId(anotacioId);
		Long expedientTipusId = anotacio.getExpedientTipus() != null ? 
											anotacio.getExpedientTipus().getId() 
											: null;
		Long expedientId = expedientTipusId != null && anotacio.getExpedient() != null ? 
											anotacio.getExpedient().getId() 
											: null;
		AnotacioAcceptarCommand anotacioAcceptarCommand = new AnotacioAcceptarCommand();
		anotacioAcceptarCommand.setEntornId(EntornActual.getEntornId());
		anotacioAcceptarCommand.setId(anotacioId);
		anotacioAcceptarCommand.setExpedientTipusId(expedientTipusId);
		anotacioAcceptarCommand.setExpedientId(expedientId);
		anotacioAcceptarCommand.setAny(Calendar.getInstance().get(Calendar.YEAR));
		// Determina l'acció per defecte depenent de les dades de l'anotació
		if (expedientId != null)
			anotacioAcceptarCommand.setAccio(AnotacioAccioEnumDto.INCORPORAR);
		else if (expedientTipusId != null) {
			anotacioAcceptarCommand.setAccio(AnotacioAccioEnumDto.CREAR);
			anotacioAcceptarCommand.setNumero(expedientService.getNumeroExpedientActual(EntornActual.getEntornId(), expedientTipusId, anotacioAcceptarCommand.getAny()));
		}
		anotacioAcceptarCommand.setAssociarInteressats(true);
		model.addAttribute("anotacio", anotacio);
		model.addAttribute("anotacioAcceptarCommand", anotacioAcceptarCommand);
		
		this.modelAccions(model);
		this.modelExpedientsTipus(entornActual, model);
		
		return "v3/anotacioAcceptar";
	}
	
	/** Mètode per tractar la petició post d'acceptar una petició d'anotació de registre de Distribucio.
	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{anotacioId}/acceptar", method = RequestMethod.POST)
	public String acceptarPost(
			HttpServletRequest request, 
			HttpServletResponse response, 
			@PathVariable Long anotacioId,
			@Validated(CrearIncorporar.class) AnotacioAcceptarCommand command,
			BindingResult bindingResult,
			Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		AnotacioDto anotacio = anotacioService.findAmbId(anotacioId);
		if (bindingResult.hasErrors()) {
			model.addAttribute("anotacio", anotacio);			
			this.modelAccions(model);
			this.modelExpedientsTipus(entornActual, model);
			
			return "v3/anotacioAcceptar";
		}
		String ret = null;
		try {
			switch(command.getAccio()) {
			case GUARDAR:
				// Guarda le opcions de tipus d'expedient i expedient
				anotacioService.updateExpedient(
						command.getId(),
						command.getExpedientTipusId(),
						command.getExpedientId());
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"anotacio.form.acceptar.guardar.success",
								new Object[] {
										command.getExpedientId(),
										anotacio.getIdentificador()}));
				ret = this.modalUrlTancar(false); 
				break;
			case CREAR:
				// Afegeix la informació de l'anotació a la sessió i redirigeix cap al formulari de creació
				request.getSession().setAttribute(ExpedientIniciController.CLAU_SESSIO_ANOTACIO, command);
				request.getSession().setAttribute(ExpedientIniciController.CLAU_SESSIO_NUMERO, command.getNumero());
				request.getSession().setAttribute(ExpedientIniciController.CLAU_SESSIO_TITOL, command.getTitol());
				request.getSession().setAttribute(ExpedientIniciController.CLAU_SESSIO_ANY, command.getAny());

				ret = expedientIniciController.iniciarPost(
						request, 
						command.getExpedientTipusId(), 
						null, 
						command.getId(), 
						model);
				break;
			case INCORPORAR:
				// Incorpora la informació de l'anotació a l'expedient
				anotacio = anotacioService.incorporarExpedient(
						command.getId(),
						command.getExpedientTipusId(),
						command.getExpedientId(),
						command.isAssociarInteressats(),
						true);
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"anotacio.form.acceptar.incorporar.success",
								new Object[] {
										anotacio.getIdentificador(),
										anotacio.getExpedient().getIdentificadorLimitat()}));
				// Comprova si hi ha cap annex amb error per advertir a l'usuari
				if (anotacio.isErrorAnnexos())
					MissatgesHelper.warning(
							request, 
							getMessage(	request, 
										"anotacio.form.acceptar.incorporar.errorAnnexos",
										new Object[] { anotacio.getIdentificador(), anotacio.getExpedient().getIdentificadorLimitat()}));
				ret = this.modalUrlTancar(false); 
				break;		
			default:
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"anotacio.form.acceptar.noaccio",
								new Object[] {
										command.getExpedientId(),
										anotacio.getIdentificador()}));
				ret = this.modalUrlTancar(false); 
			}
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"anotacio.form.acceptar.error",
							new Object[] {command.getAccio(), e.getMessage()}));
			ret = this.modalUrlTancar(false);
		}
		return ret;
	}
	
	/** Mètode pel suggest d'expedients inicial
	 * 
	 * @param text
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/suggest/expedient/inici/{expedientId}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public Map<String, String> suggestExpedientInici(
			@PathVariable String expedientId,
			Model model) {
		Map<String, String> resultat = null;
		ExpedientDto expedient = null;
		if (expedientId != null) {
			expedient = expedientService.findAmbId(Long.valueOf(expedientId));
			if (expedient != null) {
				resultat = new HashMap<String, String>();
				resultat.put("codi", String.valueOf(expedient.getId()));
				resultat.put("nom", expedient.getIdentificadorLimitat());
			}
		}
		return resultat;
	}

	/** Mètode per cercar un expedient per número o títol per a un control de tipus suggest
	 * 
	 * @param text
	 * 			Tetxt per filtrar.
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/suggest/expedient/llista/{expedientTipusId}/{text}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public List<Map<String, String>> suggestExpedientLlista(
			@PathVariable Long expedientTipusId,
			@PathVariable String text,
			Model model) {
		List<Map<String, String>> resultat = new ArrayList<Map<String,String>>();
		Map<String, String> item;
		List<ExpedientDto> llista = new ArrayList<ExpedientDto>();
		if (expedientTipusId != null) {
			llista = expedientService.findPerSuggest(expedientTipusId, text);
		}
		for (ExpedientDto expedientDto: llista) {
			item = new HashMap<String, String>();
			item.put("codi", String.valueOf(expedientDto.getId()));
			item.put("nom", expedientDto.getIdentificadorLimitat());
			resultat.add(item);
		}
		return resultat;

	}
		
	/** Mètode per obrir el formulari per rebutjar la petició d'anotació de registre de Distribució.
	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{anotacioId}/rebutjar", method = RequestMethod.GET)
	public String rebutjar(
			HttpServletRequest request, 
			@PathVariable Long anotacioId,
			Model model) {
		
		AnotacioDto anotacio = anotacioService.findAmbId(anotacioId);
		AnotacioRebutjarCommand anotacioRebutjarCommand = new AnotacioRebutjarCommand();
		anotacioRebutjarCommand.setAnotacioId(anotacioId);

		model.addAttribute("anotacio", anotacio);
		model.addAttribute("anotacioRebutjarCommand", anotacioRebutjarCommand);
				
		return "v3/anotacioRebutjar";
	}
	
	/** Mètode per tractar la petició de rebuig d'una petició d'anotació de registre.

	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{anotacioId}/rebutjar", method = RequestMethod.POST)
	public String rebutjarPost(
			HttpServletRequest request, 
			@PathVariable Long anotacioId,
			@Valid AnotacioRebutjarCommand command,
			BindingResult bindingResult,
			Model model) {
		
		AnotacioDto anotacio = anotacioService.findAmbId(anotacioId);
		if (bindingResult.hasErrors()) {
			model.addAttribute("anotacio", anotacio);			
			return "v3/anotacioRebutjar";
		}		
		String ret;
		try {
			anotacioService.rebutjar(
					anotacioId, 
					command.getObservacions());
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"anotacio.form.rebutjar.accio.rebutjar.success"));
			ret = this.modalUrlTancar(false);
		} catch(Exception e) {
			model.addAttribute("anotacio", anotacio);
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"anotacio.form.rebutjar.accio.rebutjar.error",
							new Object[] {e.getMessage()}));
			ret = "v3/anotacioRebutjar";
		}
		return ret; 
	}
	
	/** Mètode per esborrar una petició d'anotació pendent. Comprova que estigui pendent.
	 * 
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		try {
			AnotacioDto anotacio = anotacioService.findAmbId(id);
			anotacioService.delete(id);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"anotacio.llistat.accio.esborrar.success",
							new Object[] {anotacio.getIdentificador()}));
		} catch (Exception e) {
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"anotacio.llistat.accio.esborrar.error",
							new Object[] {id, e.getMessage()}));
		}
		return "redirect:/v3/anotacio";
	}
	
	
	@RequestMapping(value = "/{anotacioId}/annex/{annexId}/descarregar", method = RequestMethod.GET)
	public String descarregarAnnex(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long anotacioId,
			@PathVariable Long annexId,
			Model model) throws IOException {
		boolean success = false;
		String error = null;
		Exception ex = null;
		try {
			ArxiuDto arxiu = anotacioService.getAnnexContingut(annexId);
			if (arxiu != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
			}
			success = true;
		} catch (SistemaExternException e) {
			error = e.getPublicMessage();	
			ex = e;
		} catch (Exception e) {
			error = e.getMessage();
			ex = e;
		}
		if (success)
			return "arxiuView";
		else {
			String errMsg = getMessage(
					request, 
					"anotacio.annex.descarregar.error",
					new Object[] {error});
			logger.error(errMsg, ex);
			MissatgesHelper.error(
					request, 
					errMsg);
			return "redirect:" + request.getHeader("referer");
		}
	}
	
	@RequestMapping(value = "/{anotacioId}/annex/{annexId}/firmaInfo", method = RequestMethod.GET)
	public String firmaInfo(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long anotacioId,
			@PathVariable Long annexId,
			Model model) throws IOException {
		try {
			List<ArxiuFirmaDto> firmes = anotacioService.getAnnexFirmes(annexId); 
			
			model.addAttribute("annexId",annexId);
			model.addAttribute("firmes", firmes);
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Error obtenint les firmes: " + e.getMessage());
		}
		return "v3/anotacioAnnexFirmes";
	}

	/** Mètode per reintentar el processament de l'annex per guardar-lo a Helium dins l'arxiu o la BBDD 
	 * des de la taula d'annexos de l'expedient. */
	@RequestMapping(value = "/{anotacioId}/annex/{annexId}/reintentar", method = RequestMethod.GET)
	public String reintentarAnnex(
			HttpServletRequest request,
			@PathVariable Long anotacioId,
			@PathVariable Long annexId) {
		try {
			this.anotacioService.reintentarAnnex(anotacioId, annexId);
			MissatgesHelper.success(
					request, 
					getMessage(request, "anotacio.annex.reintentar.success"));
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(request, "anotacio.annex.reintentar.error", new Object[] {e.getMessage()}));
		}
		return "redirect:/modal/v3/anotacio/" + anotacioId;
	}	
	

	/** Posa els valors de l'enumeració estats en el model */
	private void modelEstats(Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
		for(AnotacioEstatEnumDto estat : AnotacioEstatEnumDto.values())
			opcions.add(new ParellaCodiValorDto(
					estat.name(),
					MessageHelper.getInstance().getMessage("enum.anotacio.estat." + estat.name())));		

		model.addAttribute("estats", opcions);
	}

	/** Posa els valors de l'enumeració accions en el model */
	private void modelAccions(Model model) {
		model.addAttribute(
				"accions",
				EnumHelper.getOptionsForEnum(
						AnotacioAccioEnumDto.class,
						"enum.anotacio.accio."));
		model.addAttribute(
				"anysSeleccionables", 
				BaseExpedientIniciController.getAnysSeleccionables());
	}

	/** Posa els expedients tipus al model als quals l'usuari té permís per consultar a l'entorn i estan configurats amb integració amb Distribucio
	 * @param entornActual */
	private void modelExpedientsTipus(EntornDto entornActual, Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
		
		if (entornActual != null) {
			for(ExpedientTipusDto expedientTipus : expedientTipusService.findAmbEntornPermisAnotacio(entornActual.getId()))
				if (expedientTipus.isDistribucioActiu())
					opcions.add(new ParellaCodiValorDto(
							expedientTipus.getId().toString(),
							String.format("%s - %s", expedientTipus.getCodi(), expedientTipus.getNom())));		
		}
		model.addAttribute("expedientsTipus", opcions);
	}
	
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	private static final Log logger = LogFactory.getLog(AnotacioController.class);
}
