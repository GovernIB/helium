package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.jbpm3.handlers.exception.ValidationException;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.FormulariExternDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ModalHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Controlador per a la tramitació de taques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class TascaTramitacioController extends BaseTascaController {

	public static final String VARIABLE_SESSIO_CAMP_FOCUS = "helCampFocus";
	public static final String VARIABLE_COMMAND_TRAMITACIO = "variableCommandTramitacio";

	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected TascaService tascaService;
	@Autowired
	protected ExecucioMassivaService execucioMassivaService;

	@ModelAttribute("command")
	public Object modelAttributeCommand(
			HttpServletRequest request,
			String tascaId,
			String[] tasquesTramitar,
			Model model) {
		// S'ha de inicialitzar el command abans de processar el RequestMapping
		// del POSTs amb les modificacions al formulari de la tasca
		if (tascaId != null && !tascaId.isEmpty()) {
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
			return TascaFormHelper.getCommandBuitForCamps(
					tascaService.findDades(tascaId),
					campsAddicionals,
					campsAddicionalsClasses,
					false);
		}
		return null;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}", method = RequestMethod.GET)
	public String pipelles(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		SessionHelper.removeAttribute(request,VARIABLE_TRAMITACIO_MASSIVA);
		return mostrarInformacioTascaPerPipelles(
				request,
				tascaId,
				model,
				"form");
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form", method = RequestMethod.GET)
	public String form(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioTascaPerPipelles(
					request,
					tascaId,
					model,
					"form");
		}
		emplenarModelFormulari(
				request,
				tascaId,
				model);
		return "v3/tascaForm";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/guardar", method = RequestMethod.POST)
	public String guardar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@Valid @ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			Model model) {
		List<TascaDadaDto> tascaDades = tascaService.findDades(tascaId);
		//afegirVariablesInstanciaProces(tascaDades, tascaId);
		TascaFormValidatorHelper validator = new TascaFormValidatorHelper(
				tascaService,
				tascaDades);
		Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(
				tascaDades,
				command,
				false);
		guardarForm(
				validator,
				variables,
				command,
				result,
				request,
				tascaId,
				expedientId);
		status.setComplete();
		return mostrarInformacioTascaPerPipelles(
				request,
				tascaId,
				model,
				null);
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/validar", method = RequestMethod.POST)
	public String validar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@Valid @ModelAttribute("command") Object command, 
			BindingResult result, 
			SessionStatus status, 
			Model model) {
		if (tascaService.isTascaValidada(tascaId)) {
			return mostrarInformacioTascaPerPipelles(
					request,
					tascaId,
					model,
					null);
		}
		List<TascaDadaDto> tascaDades = tascaService.findDades(tascaId);
		//afegirVariablesInstanciaProces(tascaDades, tascaId);
		TascaFormValidatorHelper validator = new TascaFormValidatorHelper(
				tascaService,
				tascaDades);
		//validator.setTasca(tascaDades);
		Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(
				tascaDades,
				command,
				false);
		if (guardarForm(
				validator,
				variables,
				command,
				result,
				request,
				tascaId,
				expedientId)) {
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
			Object commandValidar = TascaFormHelper.getCommandForCamps(
					tascaDades,
					variables,
					campsAddicionals,
					campsAddicionalsClasses,
					false);
			validarForm(
					validator,
					variables,
					commandValidar,
					result,
					request,
					tascaId,
					expedientId);
		}
		status.setComplete();
		SessionHelper.setAttribute(request,VARIABLE_COMMAND_TRAMITACIO+tascaId, command);
		SessionHelper.setAttribute(request,VARIABLE_COMMAND_BINDING_RESULT_TRAMITACIO+tascaId, result);
		return mostrarInformacioTascaPerPipelles(
				request,
				tascaId,
				model,
				result.hasErrors() ? "form" : null);
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/completar", method = RequestMethod.POST)
	public String completar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@RequestParam(value = "transicio", required = false) String transicio,
			@ModelAttribute("command") Object command, 
			BindingResult result, 
			SessionStatus status, 
			Model model) {
		validar(request, expedientId, tascaId, command, result, status, model);
		if (result.hasErrors() || !accioCompletarForm(request, tascaId, expedientId, transicio)) {
			return mostrarInformacioTascaPerPipelles(
					request,
					tascaId,
					model,
					result.hasErrors() ? "form" : null);
		}
		status.setComplete();
		return modalUrlTancar();
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/restaurar", method = RequestMethod.POST)
	public String restaurar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			SessionStatus status, 
			Model model) {
		try {
			accioRestaurarForm(request, tascaId, expedientId); 	
        } catch (Exception ex) {
        	MissatgesHelper.error(request, ex.getMessage());
        	logger.error("No s'ha pogut restaurar el formulari en la tasca " + tascaId, ex);
        }
		status.setComplete();
		return mostrarInformacioTascaPerPipelles(
				request,
				tascaId,
				model,
				"form");
	}

	@RequestMapping(value = {
			"/{expedientId}/tasca/{tascaId}/accio",
			"/{expedientId}/tasca/{tascaId}/form/accio"
			}, method = RequestMethod.POST)
	public String accio(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@RequestParam(value = "accioCamp", required = true) String accioCamp,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			Model model) {
		List<TascaDadaDto> tascaDades = tascaService.findDades(tascaId);
		//afegirVariablesInstanciaProces(tascaDades, tascaId);
		TascaFormValidatorHelper validator = new TascaFormValidatorHelper(
				tascaService,
				tascaDades);
		Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(
				tascaDades,
				command,
				false);
		boolean guardado = guardarForm(
				validator,
				variables,
				command,
				result,
				request,
				tascaId,
				expedientId);
		status.setComplete();
		if (guardado && accioExecutarAccio(request, tascaId, accioCamp)) {
			model.addAttribute("campFocus", accioCamp);
		}
		return mostrarInformacioTascaPerPipelles(
				request,
				tascaId,
				model,
				"form");
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/document", method = RequestMethod.GET)
	public String document(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioTascaPerPipelles(
					request,
					tascaId,
					model,
					"document");
		}
		// Omple els documents per adjuntar i els de només lectura
		List<TascaDocumentDto> documents = tascaService.findDocuments(tascaId);
		
		Iterator<TascaDocumentDto> itDocuments = documents.iterator();
		while (itDocuments.hasNext()) {
			TascaDocumentDto document = itDocuments.next();
			if (document.isReadOnly()) {
				if (document.getId() != null)
					itDocuments.remove();
			}
		}
		model.addAttribute("documents", documents);
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("tasca", tascaService.findAmbIdPerTramitacio(tascaId));
		return "v3/tascaDocument";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/isPermetreFinalitzar", method = RequestMethod.POST)
	@ResponseBody
	public String isPermetreFinalitzar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		StringBuffer resposta = new StringBuffer();
//		if (!tascaService.isTascaValidada(tascaId)) {
//			resposta.append(getMessage(request, "tasca.tramitacio.form.no.validat") + ".\n");
//		}
		if (!tascaService.isDocumentsComplet(tascaId)) {
			resposta.append(getMessage(request, "tasca.tramitacio.documents.no.complet") + ".\n");
		}
		if (!tascaService.isSignaturesComplet(tascaId)) {
			resposta.append(getMessage(request, "tasca.tramitacio.firmes.no.complet") + ".");
		}
		return resposta.toString();
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/isDocumentsComplet", method = RequestMethod.POST)
	@ResponseBody
	public boolean isDocumentsComplet(@PathVariable String tascaId) {
		return tascaService.isDocumentsComplet(tascaId);
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/isSignaturesComplet", method = RequestMethod.POST)
	@ResponseBody
	public boolean isSignaturesComplet(@PathVariable String tascaId) {
		return tascaService.isSignaturesComplet(tascaId);
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/signatura", method = RequestMethod.GET)
	public String signatura(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioTascaPerPipelles(
					request,
					tascaId,
					model,
					"signatura");
		}		
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("tasca", tascaService.findAmbIdPerTramitacio(tascaId));
		model.addAttribute("signatures", tascaService.findDocumentsSignar(tascaId));
		return "v3/tascaSignatura";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/document/adjuntar", method = RequestMethod.POST)
	public String documentAdjuntar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@RequestParam(value = "docId", required = false) Long docId,
			@RequestParam(value = "arxiu", required = false) final CommonsMultipartFile arxiu,	
			@RequestParam(value = "data", required = false) Date data,
			Model model) {
		try {
			byte[] contingutArxiu = IOUtils.toByteArray(arxiu.getInputStream());
			String nomArxiu = arxiu.getOriginalFilename();
			ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
			if (!tasca.isValidada()) {
				MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
			} else if (!expedientService.isExtensioDocumentPermesa(nomArxiu)) {
				MissatgesHelper.error(request, getMessage(request, "error.extensio.document"));
			} else if (nomArxiu.isEmpty() || contingutArxiu.length == 0) {
				MissatgesHelper.error(request, getMessage(request, "error.especificar.document"));
			} else {
				accioDocumentAdjuntar(
						request,
						tascaId,
						docId,
						nomArxiu,
						contingutArxiu,
						(data == null) ? new Date() : data).toString();
			}
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.guardar.document") + ": " + ex.getLocalizedMessage());
			logger.error("Error al adjuntar el document " + docId, ex);
		}
		return mostrarInformacioTascaPerPipelles(
				request,
				tascaId,
				model,
				"document");
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/document/{docId}/generar", method = RequestMethod.GET)
	public String documentGenerarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@PathVariable Long docId,
			@RequestParam(value = "data", required = false) Date data,
			Model model) {
		try {
			ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
			if (!tasca.isValidada()) {
				MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
			} else {
				DocumentDto generat = accioDocumentGenerar(
						request,
						tascaId, 
						docId, 
						(data == null) ? new Date() : data);
				if (generat != null) {
					if (!generat.isAdjuntarAuto()) {
						model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, generat.getArxiuNom());
						model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, generat.getArxiuContingut());
						return "arxiuView";
					}
				}
			}
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.generar.document") + ": " + ex.getLocalizedMessage());
			logger.error("Error generant el document " + docId, ex);
		}

		return mostrarInformacioTascaPerPipelles(
				request,
				tascaId,
				model,
				"document");
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/document/{documentId}/{documentCodi}/descarregar", method = RequestMethod.GET)
	public String documentDescarregar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@PathVariable Long documentId,
			@PathVariable String documentCodi,
			Model model) {
		ArxiuDto arxiu = tascaService.getArxiuPerDocumentIdCodi(
			tascaId,
			documentId,
			documentCodi);
		model.addAttribute(
				ArxiuView.MODEL_ATTRIBUTE_FILENAME,
				arxiu.getNom());
		model.addAttribute(
				ArxiuView.MODEL_ATTRIBUTE_DATA,
				arxiu.getContingut());
		return "arxiuView";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/document/{docId}/esborrar", method = RequestMethod.GET)
	public String documentEsborrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@PathVariable Long docId,	
			@RequestParam(value = "data", required = false) Date data,
			Model model) {
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		if (!tasca.isValidada()) {
			MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
		} else {
			accioDocumentEsborrar(
					request,
					tascaId,
					docId,
					(data == null) ? new Date() : data);
		}

		return mostrarInformacioTascaPerPipelles(
				request,
				tascaId,
				model,
				"document");
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/signarAmbToken", method = RequestMethod.POST)
	public String signarAmbToken(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@RequestParam(value="taskId", required = true) String taskId,
			@RequestParam(value="docId", required = true) Long docId,			
			@RequestParam(value="token", required = true) String token,
			@RequestParam(value="data", required = true) String[] data,
			Model model) {
		boolean signat = false;
		try {
			StringBuffer aData = new StringBuffer();
			for (String dat : data) {
				aData.append(dat);
			}			
			signat = tascaService.signarDocumentTascaAmbToken(
					expedientId,
					docId,
					taskId,
					token,
					Base64.decodeBase64(aData.toString().getBytes()));
			if (signat) {
				logger.info("Signatura del document amb el token " + token + " processada correctament");
				MissatgesHelper.info(
	        			request,
	        			getMessage(request, "info.signatura.doc.processat") );
			} else {
				logger.error("Signatura del document amb el token " + token + " processada amb error de custòdia");
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.validar.signatura") );
			}
		} catch(Exception ex) {
			logger.error("Error rebent la signatura del document", ex);
			MissatgesHelper.error(
        			request,
        			getMessage(request, "error.rebre.signatura") );
	    }

		return mostrarInformacioTascaPerPipelles(
				request,
				tascaId,
				model,
				"signatura");
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/formExtern", method = RequestMethod.GET)
	@ResponseBody
	public FormulariExternDto formExtern(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		return tascaService.formulariExternIniciar(tascaId);
	}

	@RequestMapping(value = "/formExtern", method = RequestMethod.GET)
	public String formExtern(
			HttpServletRequest request,
			@RequestParam(value = "width", required = false) final String width,
			@RequestParam(value = "height", required = false) final String height,
			@RequestParam(value = "url", required = true) final String url,
			Model model) throws ServletException {
		try {
			model.addAttribute("tittle", getMessage(request, "tasca.form.dades_form"));
			model.addAttribute("url", url);
			model.addAttribute("height", height);
			return "v3/utils/modalIframe";
		} catch(Exception ex) {
			logger.error("Error al comprobar el formulario externo", ex);
			throw new ServletException(ex);
	    }
	}

	@RequestMapping(value = "/massivaTramitacioTasca", method = RequestMethod.GET)
	public String massivaTramitacio(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) Boolean correu,
			Model model) throws IOException, ServletException {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		if (seleccio == null || seleccio.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.tasc.selec"));
			return modalUrlTancar(true);
		}
		String tascaId = guardarDatosTramitacionMasiva(request, seleccio, inici, correu);

		Object command = inicialitzarTascaFormCommand(
				request,
				tascaId,
				model);	
		
		SessionHelper.setAttribute(request,VARIABLE_COMMAND_TRAMITACIO+tascaId, command);
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		return getReturnUrl(request, tasca.getExpedientId(), tascaId, "form");
	}

	@RequestMapping(value = "/massivaTramitacioTasca/taula", method = RequestMethod.GET)
	public String massivaTramitacioTaula(
			HttpServletRequest request,
			Model model) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();		
		model.addAttribute("tasques", tascaService.findDadesPerIds(seleccio));
		
		return "v3/import/tasquesMassivaTaula";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/icones/{docId}", method = RequestMethod.GET)
	public String icones(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@PathVariable Long docId,
			Model model) {
		model.addAttribute("document", tascaService.findDocument(tascaId, docId));
		model.addAttribute("tasca", tascaService.findAmbIdPerTramitacio(tascaId));
		return "v3/expedientTascaTramitacioSignarIcones";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Long.class,
				new CustomNumberEditor(Long.class, true));
		binder.registerCustomEditor(
				Double.class,
				new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(
				BigDecimal.class,
				new CustomNumberEditor(
						BigDecimal.class,
						new DecimalFormat("#,##0.00"),
						true));
		binder.registerCustomEditor(
				Boolean.class,
				new CustomBooleanEditor(true));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}



	private void emplenarModelFormulari(
			HttpServletRequest request,
			String tascaId,
			Model model) {		
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		model.addAttribute("tasca", tasca);
		List<TascaDadaDto> dades = tascaService.findDades(tascaId);
		if (logger.isDebugEnabled()) {
			logger.debug("Dades de la tasca (id=" + tascaId + ", titol=" + tasca.getTitol() + ", numDades=" + dades.size() + ")");
			for (TascaDadaDto dada: dades) {
				logger.debug("    Dada (" +
						"varCodi=" + dada.getVarCodi() + ", " +
						"campEtiqueta=" + dada.getCampEtiqueta() + ", " +
						"campTipus=" + dada.getCampTipus() + ", " +
						"text=" + dada.getTextMultiple() + ", " +
						"class=" + dada.getJavaClassMultiple() + ")");
			}
		}
		Iterator<TascaDadaDto> itDades = dades.iterator();
		while (itDades.hasNext()) {
			TascaDadaDto dada = itDades.next();
			if (dada.isReadOnly()) {
				itDades.remove();
			}
		}
		model.addAttribute("dades", dades);
		if (tasca.getTascaRecursForm() != null && tasca.getTascaRecursForm().length() > 0) {
			try {
				byte[] contingut = dissenyService.getDeploymentResource(
						tasca.getDefinicioProcesId(),
						tasca.getTascaRecursForm());
				model.addAttribute("formRecursParams", getFormRecursParams(new String(contingut, "UTF-8")));
			} catch (Exception ex) {
				logger.error("No s'han pogut extreure els parametres del recurs", ex);
			}
		}
		List<ParellaCodiValorDto> listTerminis = new ArrayList<ParellaCodiValorDto>();
		for (int i = 0; i <= 12 ; i++)		
			listTerminis.add(new ParellaCodiValorDto(String.valueOf(i), i));
		model.addAttribute("listTerminis", listTerminis);	
		
		Object tascaFormCommand = SessionHelper.getAttribute(request,VARIABLE_COMMAND_TRAMITACIO+tascaId);
		SessionHelper.removeAttribute(request,VARIABLE_COMMAND_TRAMITACIO+tascaId);		
		if (tascaFormCommand == null) {
			tascaFormCommand = inicialitzarTascaFormCommand(
					request,
					tascaId,
					model);
			TascaFormHelper.ompleMultiplesBuits(
					tascaFormCommand,
					dades, 
					false);
		} else {
			Object bindingResult = SessionHelper.getAttribute(request,VARIABLE_COMMAND_BINDING_RESULT_TRAMITACIO+tascaId);
			SessionHelper.removeAttribute(request,VARIABLE_COMMAND_BINDING_RESULT_TRAMITACIO+tascaId);
			model.addAttribute("org.springframework.validation.BindingResult.command", bindingResult);
		}
		model.addAttribute("command", tascaFormCommand);
		model.addAttribute("isModal", ModalHelper.isModal(request));
	}

	private Object inicialitzarTascaFormCommand(
			HttpServletRequest request,
			String tascaId,
			Model model) {
		Map<String, Object> campsAddicionals = new HashMap<String, Object>();
		Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
		return TascaFormHelper.getCommandForCamps(
				tascaService.findDades(tascaId),
				null,
				campsAddicionals,
				campsAddicionalsClasses,
				false);
	}

	private boolean guardarForm(
			TascaFormValidatorHelper validator, 
			Map<String, Object> variables, 
			Object command, 
			BindingResult result, 
			HttpServletRequest request,
			String tascaId,
			Long expedientId) {
		validator.validate(command, result);
		if (result.hasErrors() || !accioGuardarForm(request, tascaId, expedientId, variables)) {
			MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
			return false;
		}
		return true;
	}
	
	private boolean validarForm(
			TascaFormValidatorHelper validator, 
			Map<String, Object> variables,
			Object commandValidar, 
			BindingResult result, 
			HttpServletRequest request,
			String tascaId,
			Long expedientId) {
		validator.setValidarObligatoris(true);
		validator.setValidarExpresions(true);
		validator.validate(commandValidar, result);
		if (result.hasErrors()) {
			MissatgesHelper.error(request, getMessage(request, "error.validacio"));
			return false;
		} else if (!accioValidarForm(request, tascaId, expedientId, variables)) {
			MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
			return false;
		}
		
		return true;
	}

	private boolean accioRestaurarForm(
			HttpServletRequest request, 
			String tascaId,
			Long expedientId) {
		boolean resposta = false;
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			try {		
				String[] tascaIds = (String[]) datosTramitacionMasiva.get("tasquesTramitar");
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds(tascaIds);
				// dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Restaurar");
				Object[] params = new Object[3];
				params[0] = entorn.getId();
				params[1] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[2] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				tascaService.restaurar(tascaId, expedientId);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.restaurar", new Object[] { tascaIds.length }));
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut restaurat les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.restaurar(tascaId, expedientId);
				MissatgesHelper.info(request, getMessage(request, "info.formulari.restaurat"));
				resposta = true;
			} catch (Exception ex) {
				String descripcioTasca = getDescripcioTascaPerMissatgeUsuari(tascaId);
				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + " " + descripcioTasca);
				logger.error("No s'ha pogut restaurat les dades del formulari en la tasca " + descripcioTasca, ex);
			}
		}
		return resposta;
	}
	
	private boolean accioGuardarForm(
			HttpServletRequest request, 
			String tascaId, 
			Long expedientId,
			Map<String, Object> variables) {
		boolean resposta = false;
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			try {
				tascaService.guardar(tascaId, expedientId, variables);
				
				String[] tascaIds = (String[]) datosTramitacionMasiva.get("tasquesTramitar");
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds(tascaIds);
//				dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Guardar");
				Object[] params = new Object[4];
				params[0] = entorn.getId();
				params[1] = variables;
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.guardar", new Object[] {tascaIds.length}));
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut guardar les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.guardar(tascaId, expedientId, variables);
				MissatgesHelper.info(request, getMessage(request, "info.dades.form.guardat"));
				resposta = true;
			} catch (Exception ex) {
				String descripcioTasca = getDescripcioTascaPerMissatgeUsuari(tascaId);
				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + " " + descripcioTasca);
				logger.error("No s'ha pogut guardar les dades del formulari en la tasca " + descripcioTasca, ex);
			}
		}
		return resposta;
	}

	private boolean accioValidarForm(
			HttpServletRequest request, 
			String tascaId,
			Long expedientId,
			Map<String, Object> variables) {
		boolean resposta = false;
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			try {		
				String[] tascaIds = (String[]) datosTramitacionMasiva.get("tasquesTramitar");
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds(tascaIds);
//				dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Validar");
				Object[] params = new Object[4];
				params[0] = entorn.getId();
				params[1] = variables;
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.validar(tascaId, expedientId, variables);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.validar", new Object[] {tascaIds.length}));
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut validar el formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.validar(tascaId, expedientId, variables);
				MissatgesHelper.info(request, getMessage(request, "info.formulari.validat"));
				resposta = true;
			} catch (Exception ex) {
				String descripcioTasca = getDescripcioTascaPerMissatgeUsuari(tascaId);
				MissatgesHelper.error(request, getMessage(request, "error.validar.formulari") + " " + descripcioTasca);
				logger.error("No s'ha pogut validar el formulari en la tasca " + descripcioTasca, ex);
			}
		}
		return resposta;
	}

	private boolean accioExecutarAccio(
			HttpServletRequest request,
			String tascaId,
			String accio) {
		boolean resposta = false;
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			try {
				String[] tascaIds = (String[]) datosTramitacionMasiva.get("tasquesTramitar");
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds(tascaIds);					
				dto.setExpedientTipusId(null);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Accio");
				Object[] params = new Object[4];
				params[0] = entorn.getId();
				params[1] = accio;
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.executarAccio(tascaId, accio);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.accio", new Object[] {tascaIds.length}));
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut guardar les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.executarAccio(tascaId, accio);
				MissatgesHelper.info(request, getMessage(request, "info.accio.executat"));
				resposta = true;
			} catch (Exception ex) {
				String descripcioTasca = getDescripcioTascaPerMissatgeUsuari(tascaId);
				if (ex.getCause() != null && ex instanceof ValidationException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " " + descripcioTasca + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.executar.accio") + " " + descripcioTasca + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
					logger.error("No s'ha pogut executar l'acció " + tascaId, ex);
				}
			}
		}
		return resposta;
	}
	
	private boolean accioCompletarForm(
			HttpServletRequest request,
			String tascaId,
			Long expedientId,
			String transicio) {
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		String transicioSortida = null;
		for (String outcome: tasca.getOutcomes()) {
			if (outcome != null && outcome.equals(transicio)) {
				transicioSortida = outcome;
				break;
			}
		}
		boolean resposta = false;
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			try {				
				String[] tascaIds = (String[]) datosTramitacionMasiva.get("tasquesTramitar");
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds(tascaIds);
//				dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Completar");
				Object[] params = new Object[4];
				params[0] = entorn.getId();
				params[1] = transicio;
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.completar(tascaId, expedientId, transicioSortida);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.completar", new Object[] {tascaIds.length}));
				resposta = true;
			} catch (Exception ex) {				
				if (ex instanceof IllegalStateException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " " + getDescripcioTascaPerMissatgeUsuari(tasca) + ": " + ex.getCause().getMessage());
				} else if (ex.getCause() != null && ex instanceof ValidationException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " " + getDescripcioTascaPerMissatgeUsuari(tasca) + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.finalitzar.tasca") + " " + getDescripcioTascaPerMissatgeUsuari(tasca) + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
					logger.error("No s'ha pogut finalitzar la tasca massiu" + tascaId, ex);
				}
			}
		} else {
			try {
				tascaService.completar(tascaId, expedientId, transicioSortida);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.completat"));
				resposta = true;
			} catch (Exception ex) {
				if (ex.getCause() != null && ex instanceof ValidationException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " " + getDescripcioTascaPerMissatgeUsuari(tasca) + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.finalitzar.tasca") + " " + getDescripcioTascaPerMissatgeUsuari(tasca) + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
					logger.error("No s'ha pogut finalitzar la tasca " + tascaId, ex);
				}
	        }
		}
		return resposta;
	}

	private Long accioDocumentAdjuntar(
			HttpServletRequest request,
			String tascaId,
			Long docId,
			String nomArxiu,
			byte[] contingutArxiu,
			Date data) {
		Long documentStoreId = null;
		
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		TascaDocumentDto document = tascaService.findDocument(tascaId, docId);
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			try {				
				String[] tascaIds = (String[]) datosTramitacionMasiva.get("tasquesTramitar");
				// Guardamos el documento de la primera tarea
				documentStoreId = tascaService.guardarDocumentTasca(
						entorn.getId(),
						tascaId,
						document.getDocumentCodi(),
						(data == null) ? new Date() : data,
						nomArxiu,
						contingutArxiu,
						null);
				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();				
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds(tascaIds);					
				dto.setExpedientTipusId(null);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("DocGuardar");
				Object[] params = new Object[7];
				params[0] = entorn.getId();				
				params[1] = document.getDocumentCodi();
				params[2] = (data == null) ? (data == null) ? new Date() : data : data;
				params[3] = contingutArxiu;
				params[4] = nomArxiu;
				params[5] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[6] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);

				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.document.guardar", new Object[] {tascaIds.length}));
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut guardar les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				documentStoreId = tascaService.guardarDocumentTasca(
					entorn.getId(),
					tascaId,
					document.getDocumentCodi(),
					data,
					nomArxiu,
					contingutArxiu,
					null);
				MissatgesHelper.info(request, getMessage(request, "info.document.adjuntat"));
			} catch (Exception ex) {
				String descripcioTasca = getDescripcioTascaPerMissatgeUsuari(tascaId);
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.guardar.document") + " " + descripcioTasca + ": " + 
	        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
				logger.error("No s'ha pogut guardar el document " + tascaId, ex);
			}
		}
		return documentStoreId;
	}
	private boolean accioDocumentEsborrar(
			HttpServletRequest request,
			String tascaId,
			Long docId,
			Date data) {
		boolean resposta = false;
		TascaDocumentDto document = tascaService.findDocument(tascaId, docId);
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			try {
				String[] tascaIds = (String[]) datosTramitacionMasiva.get("tasquesTramitar");
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds(tascaIds);					
				dto.setExpedientTipusId(null);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("DocEsborrar");
				Object[] params = new Object[4];
				params[0] = entorn.getId();				
				params[1] = document.getDocumentCodi();
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.esborrarDocument(
						tascaId,
						document.getDocumentCodi(),
						null);
				
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.document.esborrar", new Object[] {tascaIds.length}));
				
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut guardar les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.esborrarDocument(
						tascaId,
						document.getDocumentCodi(),
						null);
				MissatgesHelper.info(request, getMessage(request, "info.document.esborrat"));
				resposta = true;
	        } catch (Exception ex) {
				String descripcioTasca = getDescripcioTascaPerMissatgeUsuari(tascaId);
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.esborrar.document") + " " + descripcioTasca + ": " + 
	        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
				logger.error("No s'ha pogut esborrar el document '" + docId + "' a la tasca " + tascaId, ex);
	        }
		}
		return resposta;
	}

	private DocumentDto accioDocumentGenerar(
			HttpServletRequest request,
			String tascaId,
			Long docId,
			Date data) {
		DocumentDto generat = null;
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			try {				
				String[] tascaIds = (String[]) datosTramitacionMasiva.get("tasquesTramitar");
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds(tascaIds);					
				dto.setExpedientTipusId(null);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("DocGuardar");
				Object[] params = new Object[7];
				params[0] = entorn.getId();
				TascaDocumentDto document = tascaService.findDocument(tascaId, docId);
				params[1] = document.getDocumentCodi();
				params[2] = (data == null) ? new Date() : data;
				generat = expedientService.generarDocumentAmbPlantillaTasca(
						tascaId,
						docId);
				params[3] = generat.getArxiuContingut();
				params[4] = generat.getArxiuNom();
				params[5] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[6] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);

				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.document.generar", new Object[] {tascaIds.length}));
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut generar el document massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				generat = expedientService.generarDocumentAmbPlantillaTasca(
						tascaId,
						docId);
				MissatgesHelper.info(request, getMessage(request, "info.document.generat"));
			} catch (Exception ex) {
				String descripcioTasca = getDescripcioTascaPerMissatgeUsuari(tascaId);
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.generar.document") + " " + descripcioTasca + ": " + 
	        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
				logger.error("No s'ha pogut generar el document " + tascaId, ex);
			}
		}
		return generat;
	}

	private String getDescripcioTascaPerMissatgeUsuari(
			String tascaId) {
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		return getDescripcioTascaPerMissatgeUsuari(tasca);
	}
	private String getDescripcioTascaPerMissatgeUsuari(
			ExpedientTascaDto tasca) {
		return tasca.getTitol() + " - " + tasca.getExpedientIdentificador();
	}

	private static final Logger logger = LoggerFactory.getLogger(TascaTramitacioController.class);

}
