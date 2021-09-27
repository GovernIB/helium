package es.caib.helium.back.controller;

import es.caib.helium.back.command.PassarelaFirmaEnviarCommand;
import es.caib.helium.back.command.TascaConsultaCommand;
import es.caib.helium.back.helper.EnumHelper;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.ModalHelper;
import es.caib.helium.back.helper.NodecoHelper;
import es.caib.helium.back.helper.ObjectTypeEditorHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.back.helper.SessionHelper.SessionManager;
import es.caib.helium.back.helper.TascaFormHelper;
import es.caib.helium.back.helper.TascaFormValidatorHelper;
import es.caib.helium.back.passarelafirma.PassarelaFirmaConfig;
import es.caib.helium.back.passarelafirma.PassarelaFirmaHelper;
import es.caib.helium.back.view.ArxiuView;
import es.caib.helium.client.expedient.tasca.TascaClientService;
import es.caib.helium.client.model.ParellaCodiValor;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.CampAgrupacioDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.DocumentTipusFirmaEnumDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExecucioMassivaDto;
import es.caib.helium.logic.intf.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.FirmaTascaDto;
import es.caib.helium.logic.intf.dto.FormulariExternDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.dto.ReproDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.dto.TascaDocumentDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.SistemaExternConversioDocumentException;
import es.caib.helium.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.exception.TramitacioException;
import es.caib.helium.logic.intf.exception.TramitacioHandlerException;
import es.caib.helium.logic.intf.exception.TramitacioValidacioException;
import es.caib.helium.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.logic.intf.service.DefinicioProcesService;
import es.caib.helium.logic.intf.service.DocumentService;
import es.caib.helium.logic.intf.service.ExecucioMassivaService;
import es.caib.helium.logic.intf.service.ExpedientDocumentService;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.ReproService;
import es.caib.helium.logic.intf.service.TascaService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signature.api.StatusSignature;
import org.fundaciobit.plugins.signature.api.StatusSignaturesSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
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

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Controlador per a la tramitació de taques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/tasca")
public class TascaTramitacioController extends BaseTascaController {

	public static final String VARIABLE_SESSIO_CAMP_FOCUS = "helCampFocus";
	public static final String VARIABLE_COMMAND_TRAMITACIO = "variableCommandTramitacio";

	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected ExpedientDocumentService expedientDocumentService;
	@Autowired
	protected TascaService tascaService;
	@Autowired
	protected ExecucioMassivaService execucioMassivaService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired 
	private DefinicioProcesService definicioProcesService;
	@Autowired
	private PassarelaFirmaHelper passarelaFirmaHelper;
	@Autowired
	private ReproService reproService;
	@Resource
	private DocumentService documentService;
	@Resource
	private TascaClientService tascaClientService;

	@Autowired
	private Validator validator;
	
	@ModelAttribute("command")
	public Object modelAttributeCommand(
			HttpServletRequest request,
			String tascaId,
			String[] tasquesTramitar,
			Model model) throws Exception {
		// S'ha de inicialitzar el command abans de processar el RequestMapping
		// del POSTs amb les modificacions al formulari de la tasca
		if (tascaId != null && !tascaId.isEmpty()) {
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
			return TascaFormHelper.getCommandBuitForCamps(
					tascaService.findDades(tascaId),
					campsAddicionals,
					campsAddicionalsClasses);
		}
		return null;
	}

	@RequestMapping(value = "/{tascaId}", method = RequestMethod.GET)
	public String pipelles(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@RequestParam(required=false) Long reproId,
			Model model) throws Exception {
		SessionHelper.removeAttribute(request,VARIABLE_TRAMITACIO_MASSIVA);
		boolean bloquejarEdicioTasca = tascaService.isEnSegonPla(tascaId);
		model.addAttribute("bloquejarEdicioTasca", bloquejarEdicioTasca);
		
		Map<String,Object> variables = null;
		if (reproId != null) {
			variables = reproService.findValorsById(reproId);
			List<TascaDadaDto> tascaDades = tascaService.findDades(tascaId);
//			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
//			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
			Object command = TascaFormHelper.getCommandForCamps(
					tascaDades,
					variables,
					null, //campsAddicionals,
					null); //campsAddicionalsClasses);
			model.addAttribute("command", command);
			SessionHelper.setAttribute(request,VARIABLE_COMMAND_TRAMITACIO+tascaId, command);
		}

		if (bloquejarEdicioTasca) {
			MissatgesHelper.warning(request, getMessage(request, "expedient.tasca.segon.pla.bloquejada"));
		}
		
		try {
			return mostrarInformacioTascaPerPipelles(
					request,
					tascaId,
					model,
					null,
					null);
		} catch (Exception ex) {
			MissatgesHelper.warning(request, getMessage(request, "expedient.tasca.segon.pla.finalitzada"));
			if (ModalHelper.isModal(request)) {
				return modalUrlTancar(false);
			} else {
				return "v3/entitatNoDisponible";
			}
		    
		}
	}

	@RequestMapping(value = "/{tascaId}/{pipellaActiva}", method = RequestMethod.GET)
	public String pipellesActiva(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable String pipellaActiva,
			Model model) throws Exception {
		SessionHelper.removeAttribute(request,VARIABLE_TRAMITACIO_MASSIVA);
		model.addAttribute("bloquejarEdicioTasca", tascaService.isEnSegonPla(tascaId));
		
		return mostrarInformacioTascaPerPipelles(
				request,
				tascaId,
				model,
				pipellaActiva,
				null);
	}

	@RequestMapping(value = "/{tascaId}/form", method = RequestMethod.GET)
	public String form(
			HttpServletRequest request,
			@PathVariable String tascaId,
			Model model) throws Exception {
		model.addAttribute("bloquejarEdicioTasca", tascaService.isEnSegonPla(tascaId));
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		List<ReproDto> repros = reproService.findReprosByUsuariTipusExpedient(tasca.getExpedientTipusId(), tasca.getJbpmName());
		model.addAttribute("repros", repros);
		
		Map<String,Object> valors = null;
				
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioTascaPerPipelles(
					request,
					tascaId,
					model,
					"form",
					valors);
		}
		
		emplenarModelFormulari(
				request,
				tascaId,
				model,
				null);
		
		return "v3/tascaForm";
	}

	@RequestMapping(value = "/{tascaId}/guardar", method = RequestMethod.POST)
	public String guardar(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@Valid @ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			Model model) throws Exception {
		List<TascaDadaDto> tascaDades = tascaService.findDades(tascaId);
		String processInstanceId = getProcessInstanceIdByTaskInstanceId(tascaId);
		//afegirVariablesInstanciaProces(tascaDades, tascaId);
		TascaFormValidatorHelper validatorHelper = new TascaFormValidatorHelper(
				tascaService,
				tascaDades,
				processInstanceId);
		Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(
				tascaDades,
				command,
				false);
		Object commandValidar = TascaFormHelper.getCommandForCamps(
				tascaDades,
				variables,
				null,
				null,
				true,
				validatorHelper.isValidarObligatoris(),
				validatorHelper.isValidarExpresions(),
				processInstanceId);
		guardarForm(
				validatorHelper,
				variables,
				commandValidar,
				result,
				request,
				tascaId);
		status.setComplete();
		
		model.addAttribute("command", null);
		
		return this.getRedireccioInici(request, tascaId);
	}

	@RequestMapping(value = "/{tascaId}/validar", method = RequestMethod.POST)
	public String validar(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@Valid @ModelAttribute("command") Object command, 
			BindingResult result, 
			SessionStatus status, 
			Model model) throws Exception {
		if (tascaService.isTascaValidada(tascaId)) {
			return mostrarInformacioTascaPerPipelles(
					request,
					tascaId,
					model,
					null,
					null);
		}
		String processInstanceId = getProcessInstanceIdByTaskInstanceId(tascaId);
		List<TascaDadaDto> tascaDades = tascaService.findDades(tascaId);
		//afegirVariablesInstanciaProces(tascaDades, tascaId);
		TascaFormValidatorHelper validatorHelper = new TascaFormValidatorHelper(
				tascaService,
				tascaDades,
				processInstanceId);
		//validator.setTasca(tascaDades);
		Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(
				tascaDades,
				command,
				false);
		Object commandValidar = TascaFormHelper.getCommandForCamps(
				tascaDades,
				variables,
				null,
				null,
				true,
				validatorHelper.isValidarObligatoris(),
				validatorHelper.isValidarExpresions(),
				processInstanceId);
		if (guardarForm(
				validatorHelper,
				variables,
				commandValidar,
				result,
				request,
				tascaId)) {
			// Recarrega les dades guardades
			tascaDades = tascaService.findDades(tascaId);
			validatorHelper.setTascaDades(tascaDades); // Actualitzam les dades de la tasca al validador
//			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
//			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
			validarForm(
					validatorHelper,
					variables,
					commandValidar,
					result,
					request,
					tascaId,
					processInstanceId);
		}
		status.setComplete();
		SessionHelper.setAttribute(request,VARIABLE_COMMAND_TRAMITACIO+tascaId, command);
		SessionHelper.setAttribute(request,VARIABLE_COMMAND_BINDING_RESULT_TRAMITACIO+tascaId, result);
		
		return this.getRedireccioInici(request, tascaId);
	}

	@RequestMapping(value = "/{tascaId}/completar", method = RequestMethod.POST)
	public String completar(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@RequestParam(value = "transicio", required = false) String transicio,
			@ModelAttribute("command") Object command, 
			BindingResult result, 
			SessionStatus status, 
			Model model) throws Exception {
		validar(request, tascaId, command, result, status, model);
		if (result.hasErrors() || !accioCompletarForm(request, tascaId, transicio)) {
			return mostrarInformacioTascaPerPipelles(
					request,
					tascaId,
					model,
					(result.hasErrors() ? "form" : null),
					null);
		}
		status.setComplete();
		
		TascaConsultaCommand filtreCommand = SessionHelper.getSessionManager(request).getFiltreConsultaTasca();
		if (filtreCommand != null) {
			filtreCommand.setConsultaTramitacioMassivaTascaId(null);
			SessionHelper.getSessionManager(request).setFiltreConsultaTasca(filtreCommand);
		}
		
		if (ModalHelper.isModal(request))
			return modalUrlTancar(false);
		else
			return "redirect:/v3/tasca";
	}

	@RequestMapping(value = "/{tascaId}/restaurar", method = RequestMethod.POST)
	public String restaurar(
			HttpServletRequest request,
			@PathVariable String tascaId,
			SessionStatus status, 
			Model model) {
		try {
			accioRestaurarForm(request, tascaId); 	
        } catch (Exception ex) {
        	MissatgesHelper.error(request, ex.getMessage());
        	logger.error("No s'ha pogut restaurar el formulari en la tasca " + tascaId, ex);
        }
		status.setComplete();
		return this.getRedireccioInici(request, tascaId);
	}

	@RequestMapping(value = {
			"/{tascaId}/accio",
			"/{tascaId}/form/accio"
			}, method = RequestMethod.POST)
	public String accio(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@RequestParam(value = "accioCamp", required = true) String accioCamp,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			Model model) throws Exception {
		String processInstanceId = getProcessInstanceIdByTaskInstanceId(tascaId);
		List<TascaDadaDto> tascaDades = tascaService.findDades(tascaId);
		//afegirVariablesInstanciaProces(tascaDades, tascaId);
		TascaFormValidatorHelper validatorHelper = new TascaFormValidatorHelper(
				tascaService,
				tascaDades,
				processInstanceId);
		Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(
				tascaDades,
				command,
				false);
		Object commandValidar = TascaFormHelper.getCommandForCamps(
				tascaDades,
				variables,
				null,
				null,
				true,
				validatorHelper.isValidarObligatoris(),
				validatorHelper.isValidarExpresions(),
				processInstanceId);
		boolean guardado = guardarForm(
				validatorHelper,
				variables,
				commandValidar,
				result,
				request,
				tascaId);
		status.setComplete();
		if (guardado && accioExecutarAccio(request, tascaId, accioCamp)) {
			model.addAttribute("campFocus", accioCamp);
		}
		return mostrarInformacioTascaPerPipelles(
				request,
				tascaId,
				model,
				"form",
				null);
	}
	
	@RequestMapping(value = "/{tascaId}/accio", method = RequestMethod.GET)
	public String accioGet(
			HttpServletRequest request,
			@PathVariable String tascaId,
			Model model) {
		
		return this.getRedireccioInici(request, tascaId);
	}

	@RequestMapping(value = "/{tascaId}/document", method = RequestMethod.GET)
	public String document(
			HttpServletRequest request,
			@PathVariable String tascaId,
			Model model) throws Exception {
		model.addAttribute("bloquejarEdicioTasca", tascaService.isEnSegonPla(tascaId));
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioTascaPerPipelles(
					request,
					tascaId,
					model,
					"document",
					null);
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
		model.addAttribute("tasca", tascaService.findAmbIdPerTramitacio(tascaId));
		model.addAttribute("isModal", ModalHelper.isModal(request));
		model.addAttribute(
				"tipusFirmaOptions",
				EnumHelper.getOptionsForEnum(
						DocumentTipusFirmaEnumDto.class,
						"enum.document.tipus.firma."));		
		return "v3/tascaDocument";
	}

	@RequestMapping(value = "/{tascaId}/isPermetreFinalitzar", method = RequestMethod.POST)
	@ResponseBody
	public String isPermetreFinalitzar(
			HttpServletRequest request,
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

	@RequestMapping(value = "/{tascaId}/isDocumentsComplet", method = RequestMethod.POST)
	@ResponseBody
	public boolean isDocumentsComplet(@PathVariable String tascaId) {
		return tascaService.isDocumentsComplet(tascaId);
	}

	@RequestMapping(value = "/{tascaId}/isSignaturesComplet", method = RequestMethod.POST)
	@ResponseBody
	public boolean isSignaturesComplet(@PathVariable String tascaId) {
		return tascaService.isSignaturesComplet(tascaId);
	}

	@RequestMapping(value = "/{tascaId}/signatura", method = RequestMethod.GET)
	public String signatura(
			HttpServletRequest request,
			@PathVariable String tascaId,
			Model model) throws Exception {
		model.addAttribute("bloquejarEdicioTasca", tascaService.isEnSegonPla(tascaId));
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioTascaPerPipelles(
					request,
					tascaId,
					model,
					"signatura",
					null);
		}		
		model.addAttribute("tasca", tascaService.findAmbIdPerTramitacio(tascaId));
		model.addAttribute("signatures", tascaService.findDocumentsSignar(tascaId));
		model.addAttribute("passarelaFirmaEnviarCommand", new PassarelaFirmaEnviarCommand());
		model.addAttribute("numPluginsPassarela", passarelaFirmaHelper.getNumberPossiblePlugins());
		return "v3/tascaSignatura";
	}

	@RequestMapping(value = "/{tascaId}/document/{documentId}/firmaPassarela", method = RequestMethod.GET)
	public String firmaPassarelaGet(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable Long documentId,
			Model model) throws IOException {
		try {

			// Troba la definició de la tasca
			ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
			ExpedientDto expedient = expedientService.findAmbId(tasca.getExpedientId());
			// Troba el document de la tasca 
			FirmaTascaDto firma = definicioProcesService.tascaFirmaFindAmbTascaDocument(tasca.getTascaId(), documentId, expedient.getTipus().getId());
			if (firma == null)
				throw new NoTrobatException(FirmaTascaDto.class, "tascaId=" + tasca.getTascaId() + ", documentId = " + documentId);
			// Recupera la informació del document
			String documentCodi = firma.getDocument().getCodi();
			DocumentDto documentDto = tascaService.getDocumentPerDocumentCodi(tascaId, documentCodi);	        
			ArxiuDto arxiuPerFirmar = expedientDocumentService.findArxiuAmbTokenPerSignar(
	        		documentDto.getTokenSignatura()); 
			PersonaDto usuariActual = aplicacioService.findPersonaActual();
			String modalStr = (ModalHelper.isModal(request)) ? "/modal" : "";
			
			String procesFirmaUrl = passarelaFirmaHelper.iniciarProcesDeFirma(
					request,
					arxiuPerFirmar,
					documentId.toString(),
					usuariActual.getDni(),
					"Firma",
					"Illes Balears",
					usuariActual.getEmail(),
					LocaleContextHolder.getLocale().getLanguage(),
					modalStr + "/v3/tasca/" + tascaId + "/document/" + documentCodi + "/firmaPassarelaFinal",
					false);
			return "redirect:" + procesFirmaUrl;
		} catch (Exception e) {
			String errMsg = getMessage(
					request, 
					"document.controller.firma.passarela.inici.error",
					new Object[] {e.getLocalizedMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(
					request,
					errMsg);			
			return "v3/passarelaFirma/passarelaFiFirma";
		}
	}

	@RequestMapping(value = "/{tascaId}/document/{documentCodi}/firmaPassarelaFinal")
	public String firmaPassarelaFinal(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable String documentCodi,
			@RequestParam("signaturesSetId") String signaturesSetId,
			Model model) throws Exception {
		PassarelaFirmaConfig signaturesSet = passarelaFirmaHelper.finalitzarProcesDeFirma(
				request,
				signaturesSetId);
		StatusSignaturesSet status = signaturesSet.getStatusSignaturesSet();
		switch (status.getStatus()) {
		case StatusSignaturesSet.STATUS_FINAL_OK:
			FileInfoSignature firmaInfo = signaturesSet.getFileInfoSignatureArray()[0];
			StatusSignature firmaStatus = firmaInfo.getStatusSignature();
									
			if (firmaStatus.getStatus() == StatusSignature.STATUS_FINAL_OK) {
				if (firmaStatus.getSignedData() == null || !firmaStatus.getSignedData().exists()) {
					firmaStatus.setStatus(StatusSignature.STATUS_FINAL_ERROR);
					String msg = "L'estat indica que ha finalitzat correctament però el fitxer firmat o no s'ha definit o no existeix";
					firmaStatus.setErrorMsg(msg);
					MissatgesHelper.error(
							request,
							getMessage(
									request, 
									"document.controller.firma.passarela.final.ok.nofile"));
				} else {
					try {
						FileInputStream fis = new FileInputStream(firmaStatus.getSignedData());
						DocumentDto document = tascaService.getDocumentPerDocumentCodi(
								tascaId, 
								documentCodi);
						tascaService.signarDocumentTascaAmbToken(
								tascaId, 
								document.getTokenSignatura(), 
								IOUtils.toByteArray(fis));
						
						MissatgesHelper.success(
								request,
								getMessage(
										request, 
										"document.controller.firma.passarela.final.ok"));
					} catch (Exception e) {
						String errMsg = getMessage(
								request, 
								"document.controller.firma.passarela.final.error.validacio",
								new Object[] {ExceptionUtils.getRootCauseMessage(e)});
						logger.error("Error en la signatura del document. " + errMsg, e);
						MissatgesHelper.error(
								request,
								errMsg);
					}
				}
			} else {
				MissatgesHelper.error(
						request,
						getMessage(
								request, 
								"document.controller.firma.passarela.final.ok.statuserr",
								new Object[] {firmaStatus.getStatus(), firmaStatus.getErrorMsg()}));
			}
			break;
		case StatusSignaturesSet.STATUS_FINAL_ERROR:
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"document.controller.firma.passarela.final.error",
							new Object[] {
									status.getErrorMsg() != null? status.getErrorMsg() : "",
									status.getErrorException() != null? status.getErrorException().getMessage() : ""
							}));
			break;
		case StatusSignaturesSet.STATUS_CANCELLED:
			MissatgesHelper.warning(
					request,
					getMessage(
							request, 
							"document.controller.firma.passarela.final.cancel"));
			break;
		default:
			MissatgesHelper.warning(
					request,
					getMessage(
							request, 
							"document.controller.firma.passarela.final.desconegut"));
		}
		passarelaFirmaHelper.closeSignaturesSet(
				request,
				signaturesSet);
		return "v3/passarelaFirma/passarelaFiFirma";
	}

	@RequestMapping(value = "/{tascaId}/verificarSignatura/{documentStoreId}/{documentCodi}", method = RequestMethod.GET)
	public String verificarSignatura(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			@PathVariable Long documentStoreId,
			@PathVariable String documentCodi,
			@RequestParam(value = "urlVerificacioCustodia", required = false) final String urlVerificacioCustodia,
			ModelMap model) throws ServletException {
		try {
			if (urlVerificacioCustodia != null && !urlVerificacioCustodia.isEmpty()) {
				model.addAttribute("tittle", getMessage(request, "expedient.document.verif_signatures"));
				model.addAttribute("url", urlVerificacioCustodia);
				return "v3/utils/modalIframe";
			}
			model.addAttribute(
					"signatura",
					tascaService.getDocumentPerDocumentCodi(
							tascaId.toString(), 
							documentCodi));
			model.addAttribute("signatures", expedientDocumentService.verificarSignatura(documentStoreId));
			return "v3/expedientTascaTramitacioSignarVerificar";
		} catch(Exception ex) {
			logger.error("Error al verificar la signatura", ex);
			throw new ServletException(ex);
	    }
	}

	@RequestMapping(value = "/{tascaId}/document/{documentId}/adjuntar", method = RequestMethod.POST)
	public String documentAdjuntar(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable Long documentId,
			@RequestParam(value = "arxiu", required = false) final CommonsMultipartFile arxiu,
			@RequestParam(value = "ambFirma", required = false, defaultValue = "false") boolean ambFirma,
			@RequestParam(value = "tipusFirma", required = false, defaultValue = "ADJUNT") DocumentTipusFirmaEnumDto tipusFirma,
			@RequestParam(value = "firma", required = false) final CommonsMultipartFile firma,	
			@RequestParam(value = "data", required = false) Date data,
			Model model) throws Exception {
		try {
			byte[] contingutArxiu = IOUtils.toByteArray(arxiu.getInputStream());
			String arxiuContentType = arxiu.getContentType();
			byte[] firmaContingut = null;
			if (firma != null && firma.getSize() > 0) {
				firmaContingut = IOUtils.toByteArray(firma.getInputStream());
			}
			String nomArxiu = arxiu.getOriginalFilename();

			ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
			ExpedientDto expedient = expedientService.findAmbId(tasca.getExpedientId());
			// Validacions
			boolean error = false;
			if (!tasca.isValidada()) {
				MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
				error = true;
			}
			if (!expedientDocumentService.isExtensioPermesaPerTasca(
					tascaId,
					documentId,
					nomArxiu)) {
				MissatgesHelper.error(request, getMessage(request, "error.extensio.document"));
				error = true;
			}
			if (nomArxiu.isEmpty() || contingutArxiu.length == 0) {
				MissatgesHelper.error(request, getMessage(request, "error.especificar.document"));
				error = true;
			} 
			if (ambFirma 
					&& DocumentTipusFirmaEnumDto.SEPARAT.equals(tipusFirma)
					&& (firma == null || firma.isEmpty())) {
				MissatgesHelper.error(request, getMessage(request, "error.especificar.document.firma"));
				error = true;
			} 
			// Per evitar problemes amb el tancament del arxiu es valida que només es puguin pujar documents convertibles a pdf 
			if(expedient.isArxiuActiu() && !documentService.isArxiuConvertiblePdf(nomArxiu)) {
				MissatgesHelper.error(request, getMessage(request, "document.validacio.convertible.error"));
				error = true;
			} 
			if (!error) {
				TascaDocumentDto doc = tascaService.findDocument(tascaId, documentId, expedient.getTipus().getId());
				accioDocumentAdjuntar(
						request,
						tascaId,
						doc.getDocumentCodi(),
						(data == null) ? new Date() : data,
						nomArxiu,
						contingutArxiu,
						arxiuContentType,
						ambFirma,
						tipusFirma,
						firmaContingut);
			}
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.guardar.document") + ": " + ex.getLocalizedMessage());
			logger.error("Error al adjuntar el document a la tasca(" +
					"tascaId=" + tascaId + ", " +
					"documentId=" + documentId + ")",
					ex);
		}
		return mostrarInformacioTascaPerPipelles(
				request,
				tascaId,
				model,
				"document",
				null);
	}

	@RequestMapping(value = "/{tascaId}/document/{documentCodi}/generar", method = RequestMethod.GET)
	public String documentGenerarGet(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable String documentCodi,
			@RequestParam(value = "data", required = false) Date data,
			Model model) throws Exception {
		try {
			ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
			if (!tasca.isValidada()) {
				MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
			} else {
				ArxiuDto generat = accioDocumentGenerar(
						request,
						tascaId, 
						documentCodi, 
						(data == null) ? new Date() : data);
				if (generat != null) {
					model.addAttribute(
							ArxiuView.MODEL_ATTRIBUTE_FILENAME,
							generat.getNom());
					model.addAttribute(
							ArxiuView.MODEL_ATTRIBUTE_DATA,
							generat.getContingut());
					return "arxiuView";
				}
			}
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.generar.document") + ": " + ex.getLocalizedMessage());
			logger.error("Error generant el document (" +
					"tascaId=" + tascaId + ", " +
					"documentCodi=" + documentCodi + ")",
					ex);
		}
		return mostrarInformacioTascaPerPipelles(
				request,
				tascaId,
				model,
				"document",
				null);
	}

	@RequestMapping(value = "/{tascaId}/document/{documentCodi}/descarregar", method = RequestMethod.GET)
	public String documentDescarregar(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable String documentCodi,
			Model model) {
		try {
			ArxiuDto arxiu = tascaService.getArxiuPerDocumentCodi(
					tascaId,
					documentCodi);
			model.addAttribute(
					ArxiuView.MODEL_ATTRIBUTE_FILENAME,
					arxiu.getNom());
			model.addAttribute(
					ArxiuView.MODEL_ATTRIBUTE_DATA,
					arxiu.getContingut());
		} catch (Exception ex) {
			logger.error("Error al descarregar el document", ex);
			if (ex instanceof SistemaExternException)
				MissatgesHelper.error(request,((SistemaExternException)ex).getPublicMessage());
			else
				MissatgesHelper.error(request,ex.getMessage());
			
			return "redirect:/modal/v3/tasca/" + tascaId + "/document";
		}	
		return "arxiuView";
	}

	@RequestMapping(value = "/{tascaId}/document/{documentCodi}/esborrar", method = RequestMethod.GET)
	public String documentEsborrar(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable String documentCodi,	
			@RequestParam(value = "data", required = false) Date data,
			Model model) throws Exception {
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		if (!tasca.isValidada()) {
			MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
		} else {
			accioDocumentEsborrar(
					request,
					tascaId,
					documentCodi);
		}
		return mostrarInformacioTascaPerPipelles(
				request,
				tascaId,
				model,
				"document",
				null);
	}

	@RequestMapping(value = "/{tascaId}/signarAmbToken", method = RequestMethod.POST)
	public String signarAmbToken(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@RequestParam(value="taskId", required = true) String taskId,
			@RequestParam(value="token", required = true) String token,
			@RequestParam(value="data", required = true) String[] data,
			Model model) throws Exception {
		boolean signat = false;
		try {
			StringBuffer aData = new StringBuffer();
			for (String dat : data) {
				aData.append(dat);
			}			
			signat = tascaService.signarDocumentTascaAmbToken(
					taskId,
					token,
					Base64.decodeBase64(aData.toString().getBytes()));
			if (signat) {
				logger.info("Signatura del document amb el token " + token + " processada correctament");
				MissatgesHelper.success(
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
				"signatura",
				null);
	}

	@RequestMapping(value = "/{tascaId}/formExtern", method = RequestMethod.GET)
	@ResponseBody
	public FormulariExternDto formExtern(
			HttpServletRequest request,
			@PathVariable String tascaId,
			Model model) {
		return tascaService.formulariExternObrir(tascaId);
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
		Set<String> seleccio = sessionManager.getSeleccioConsultaTasca();
		if (seleccio == null || seleccio.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.tasc.selec"));
			return modalUrlTancar(false);
		}
		String tascaId = guardarDatosTramitacionMasiva(request, seleccio, inici, correu);
		try {
			ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
			return getReturnUrl(request, tasca.getId(), "form");
		} catch(Exception e) {
			return modalUrlTancar(false);
		}
	}

	@RequestMapping(value = "/massivaTramitacioTasca/taula", method = RequestMethod.GET)
	public String massivaTramitacioTaula(
			HttpServletRequest request,
			Model model) {
//		SessionManager sessionManager = SessionHelper.getSessionManager(request);
//		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();		
		Set<String> seleccio = getSeleccioConsultaTasca(request);		
		model.addAttribute("tasques", tascaService.findAmbIds(seleccio));
		
		return "v3/import/tasquesMassivaTaula";
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
			Model model,
			Map<String, Object> valorsFormulariExtern) throws Exception {
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		model.addAttribute("tasca", tasca);
		List<TascaDadaDto> dades = tascaService.findDades(tascaId);
		/*if (logger.isDebugEnabled()) {
			logger.debug("Dades de la tasca (id=" + tascaId + ", titol=" + tasca.getTitol() + ", numDades=" + dades.size() + ")");
			for (TascaDadaDto dada: dades) {
				logger.debug("    Dada (" +
						"varCodi=" + dada.getVarCodi() + ", " +
						"campEtiqueta=" + dada.getCampEtiqueta() + ", " +
						"campTipus=" + dada.getCampTipus() + ", " +
						"text=" + dada.getTextMultiple() + ", " +
						"class=" + dada.getJavaClassMultiple() + ")");
			}
		}*/
		
		Map<CampAgrupacioDto, List<TascaDadaDto>> mapDades = new TreeMap<CampAgrupacioDto, List<TascaDadaDto>>(
				// Comparador d'ordre d'agrupacions, primer la null, després heretades i finalment pròpies
				new Comparator<CampAgrupacioDto>() {
                    @Override
                    public int compare(CampAgrupacioDto a1, CampAgrupacioDto a2) {
                    	// El null va davant
                    	if (a1 == null && a2 == null)
                    		return 0;
                    	else if (a1 == null)
                    		return -1;
                    	else if (a2 == null )
                    		return 1;
                    	else {
                    		// Després van els heretats
                    		if (a1.isHeretat() && a2.isHeretat())
                    			return Integer.compare(a1.getOrdre(), a2.getOrdre());
                    		else if (a1.isHeretat() && !a2.isHeretat())
                    			return -1;
                    		else if (!a1.isHeretat() && a2.isHeretat())
                    			return 1;
                    		else
                    			// Si no retorna l'ordre normal
                    			return Integer.compare(a1.getOrdre(), a2.getOrdre());
                    	}
                    }
                });
		
		
		Iterator<TascaDadaDto> itDades = dades.iterator();
		while (itDades.hasNext()) {
			TascaDadaDto dada = itDades.next();
			if (dada.isReadOnly()) {
				itDades.remove();
			}
		}
		
		if(tasca.isMostrarAgrupacions()) {
			TreeMap<Long, CampAgrupacioDto> agrupacions = new TreeMap<Long, CampAgrupacioDto>();
			mapDades.put(null, new ArrayList<TascaDadaDto>());
			for(TascaDadaDto dada : dades) {
				CampAgrupacioDto agrupacio = null;
				if(dada.getAgrupacio() != null) {
					if(!agrupacions.containsKey(dada.getAgrupacio().getId())) {
						agrupacions.put(dada.getAgrupacio().getId(), dada.getAgrupacio());
						mapDades.put(dada.getAgrupacio(), new ArrayList<TascaDadaDto>());
					}
					agrupacio = agrupacions.get(dada.getAgrupacio().getId());
				}
				mapDades.get(agrupacio).add(dada);
			}
		} else {
			mapDades.put(null, dades);
		}
		model.addAttribute("dades", dades);
		model.addAttribute("dadesMap", mapDades);
		
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
		List<ParellaCodiValor> listTerminis = new ArrayList<ParellaCodiValor>();
		for (int i = 0; i <= 12 ; i++)		
			listTerminis.add(new ParellaCodiValor(String.valueOf(i), i));
		model.addAttribute("listTerminis", listTerminis);
		
		Object tascaFormCommand = SessionHelper.getAttribute(request,VARIABLE_COMMAND_TRAMITACIO+tascaId);
		SessionHelper.removeAttribute(request,VARIABLE_COMMAND_TRAMITACIO+tascaId);		
		if (tascaFormCommand == null) {
			tascaFormCommand = inicialitzarTascaFormCommand(
					request,
					tascaId,
					model,
					valorsFormulariExtern);
		} else {
			Object bindingResult = SessionHelper.getAttribute(request,VARIABLE_COMMAND_BINDING_RESULT_TRAMITACIO+tascaId);
			SessionHelper.removeAttribute(request,VARIABLE_COMMAND_BINDING_RESULT_TRAMITACIO+tascaId);
			model.addAttribute("org.springframework.validation.BindingResult.command", bindingResult);
		}
//		TascaFormHelper.ompleMultiplesBuits(
//				tascaFormCommand,
//				dades,
//				false);
		model.addAttribute("command", tascaFormCommand);
		model.addAttribute("isModal", ModalHelper.isModal(request));
	}

	private Object inicialitzarTascaFormCommand(
			HttpServletRequest request,
			String tascaId,
			Model model,
			Map<String, Object> valorsFormulariExtern) throws Exception {
//		Map<String, Object> campsAddicionals = new HashMap<String, Object>();
//		Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
		
		return TascaFormHelper.getCommandForCamps(
				tascaService.findDades(tascaId),
				valorsFormulariExtern,
				null, //campsAddicionals,
				null); //campsAddicionalsClasses);
	}

	private boolean guardarForm(
			TascaFormValidatorHelper validatorHelper,
			Map<String, Object> variables, 
			Object command, 
			BindingResult result, 
			HttpServletRequest request,
			String tascaId) {
//		validator.validate(command, result);
		validatorHelper.validate(command, result);
		if (result.hasErrors() || !accioGuardarForm(request, tascaId, variables)) {
			MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
			return false;
		}
		return true;
	}
	
	private boolean validarForm(
			TascaFormValidatorHelper validatorHelper,
			Map<String, Object> variables,
			Object commandValidar, 
			BindingResult result, 
			HttpServletRequest request,
			String tascaId,
			String processInstanceId) throws Exception {
		validatorHelper.setValidarObligatoris(true);
		validatorHelper.setValidarExpresions(true);
		commandValidar = TascaFormHelper.getCommandForCamps(
				validatorHelper.getTascaDades(),
				variables,
				null, //campsAddicionals,
				null, //campsAddicionalsClasses);
				true,
				validatorHelper.isValidarObligatoris(),
				validatorHelper.isValidarExpresions(),
				processInstanceId);
//		validator.validate(commandValidar, result);
		validatorHelper.validate(commandValidar, result);
		if (result.hasErrors()) {
			MissatgesHelper.error(request, getMessage(request, "error.validacio"));
			return false;
		} else if (!accioValidarForm(request, tascaId, variables)) {
			MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
			return false;
		}
		
		return true;
	}

	private boolean accioRestaurarForm(
			HttpServletRequest request, 
			String tascaId) {
		boolean resposta = false;
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			try {		
				String[] tascaIds = (String[]) datosTramitacionMasiva.get("tasquesTramitar");
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
//				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds((String[]) ArrayUtils.removeElement(tascaIds, tascaId));
				// dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Restaurar");
				Object[] params = new Object[1];
				params[0] = entorn.getId();
//				params[1] = auth.getCredentials();
//				List<String> rols = new ArrayList<String>();
//				for (GrantedAuthority gauth : auth.getAuthorities()) {
//					rols.add(gauth.getAuthority());
//				}
//				params[2] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				tascaService.restaurar(tascaId);
				MissatgesHelper.success(request, getMessage(request, "info.tasca.massiu.restaurar", new Object[] { tascaIds.length }));
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut restaurat les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.restaurar(tascaId);
				MissatgesHelper.success(request, getMessage(request, "info.formulari.restaurat"));
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
			Map<String, Object> variables) {
		if (logger.isDebugEnabled()) {
			logger.debug("Guardant dades de la tasca (id=" + tascaId + ")");
			for (String var: variables.keySet()) {
				Object valor = variables.get(var);
				String valorComString = TascaFormHelper.varValorToString(valor);
				logger.debug("    Variable (" +
						"varCodi=" + var + ", " +
						"class=" + ((valor != null) ? valor.getClass().getName() : "null") + ", " +
						"valor=" + valorComString + ")");
			}
		}
		boolean resposta = false;
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			try {
				ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
				tascaService.guardar(tascaId, variables);
				
				String[] tascaIds = (String[]) datosTramitacionMasiva.get("tasquesTramitar");
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds((String[])ArrayUtils.removeElement(tascaIds, tascaId));
				dto.setExpedientTipusId(tasca.getExpedientTipusId());
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Guardar");
				Object[] params = new Object[2];
				params[0] = entorn.getId();
				params[1] = variables;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.success(request, getMessage(request, "info.tasca.massiu.guardar", new Object[] {tascaIds.length}));
				resposta = true;
				esborrarSeleccio(request);
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut guardar les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.guardar(tascaId, variables);
				MissatgesHelper.success(request, getMessage(request, "info.dades.form.guardat"));
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
			Map<String, Object> variables) {
		boolean resposta = false;
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			try {		
				String[] tascaIds = (String[]) datosTramitacionMasiva.get("tasquesTramitar");
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
//				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
//				dto.setTascaIds(tascaIds);
				dto.setTascaIds((String[])ArrayUtils.removeElement(tascaIds, tascaId));
//				dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Validar");
				Object[] params = new Object[2];
				params[0] = entorn.getId();
				params[1] = variables;
//				params[2] = auth.getCredentials();
//				List<String> rols = new ArrayList<String>();
//				for (GrantedAuthority gauth : auth.getAuthorities()) {
//					rols.add(gauth.getAuthority());
//				}
//				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.validar(tascaId, variables);
				MissatgesHelper.success(request, getMessage(request, "info.tasca.massiu.validar", new Object[] {tascaIds.length}));
				resposta = true;
				esborrarSeleccio(request);
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut validar el formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.validar(tascaId, variables);
				MissatgesHelper.success(request, getMessage(request, "info.formulari.validat"));
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
//				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds(tascaIds);					
				dto.setExpedientTipusId(null);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Accio");
				Object[] params = new Object[2];
				params[0] = entorn.getId();
				params[1] = accio;
//				params[2] = auth.getCredentials();
//				List<String> rols = new ArrayList<String>();
//				for (GrantedAuthority gauth : auth.getAuthorities()) {
//					rols.add(gauth.getAuthority());
//				}
//				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.executarAccio(tascaId, accio);
				MissatgesHelper.success(request, getMessage(request, "info.tasca.massiu.accio", new Object[] {tascaIds.length}));
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut guardar les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.executarAccio(tascaId, accio);
				MissatgesHelper.success(request, getMessage(request, "info.accio.executat"));
				resposta = true;
			} catch (Exception ex) {
				String descripcioTasca = getDescripcioTascaPerMissatgeUsuari(tascaId);
				
				if (ex instanceof ValidacioException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " " + descripcioTasca + ": " + ex.getMessage());
				}  else if (ex instanceof TramitacioValidacioException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " " + descripcioTasca + ": " + ex.getMessage());
				} else if (ex instanceof TramitacioHandlerException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.executar.accio") + " " + descripcioTasca + ": " + ((TramitacioHandlerException)ex).getPublicMessage());
				} else if (ex instanceof TramitacioException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.executar.accio") + " " + descripcioTasca + ": " + ((TramitacioException)ex).getPublicMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.executar.accio") + " " + descripcioTasca + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
		        }
				logger.error("No s'ha pogut executar l'acció " + tascaId, ex);
			}
		}
		return resposta;
	}
	
	private boolean accioCompletarForm(
			HttpServletRequest request,
			String tascaId,
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
//				Authentication auth = SecurityContextHolder.getContext().getAuthentication();

				tascaService.completarMassiu(tascaId, transicioSortida);
				
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds((String[])ArrayUtils.removeElement(tascaIds, tascaId));
//				dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Completar");
				Object[] params = new Object[2];
				params[0] = entorn.getId();
				params[1] = transicioSortida;
//				params[2] = auth.getCredentials();
//				List<String> rols = new ArrayList<String>();
//				for (GrantedAuthority gauth : auth.getAuthorities()) {
//					rols.add(gauth.getAuthority());
//				}
//				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				
				execucioMassivaService.crearExecucioMassiva(dto);
				
				MissatgesHelper.success(request, getMessage(request, "info.tasca.massiu.completar", new Object[] {tascaIds.length}));
				resposta = true;
				esborrarSeleccio(request);
			} catch (ValidacioException ex) {
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.validacio.tasca") + " " + getDescripcioTascaPerMissatgeUsuari(tasca) + ": " + ex.getMessage());
				logger.error("No s'ha pogut finalitzar la tasca massiu" + tascaId, ex);
			} catch (TramitacioValidacioException ex) {
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.validacio.tasca") + " : " + ex.getCause().getMessage());
				logger.error("No s'ha pogut finalitzar la tasca massiu" + tascaId, ex);
			} catch (TramitacioHandlerException ex) {
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.validacio.tasca") + " : " + ex.getPublicMessage());
				logger.error("No s'ha pogut finalitzar la tasca massiu" + tascaId, ex);
			} catch (TramitacioException ex) {
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.finalitzar.tasca") + " " + getDescripcioTascaPerMissatgeUsuari(tasca) + ": " + 
	        					ex.getPublicMessage());
				logger.error("No s'ha pogut finalitzar la tasca massiu" + tascaId, ex);
			} catch (SistemaExternException ex) {
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.finalitzar.tasca") + " : " + ex.getPublicMessage());
				logger.error("No s'ha pogut finalitzar la tasca massiu" + tascaId, ex);
			} catch (Exception ex) {
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.finalitzar.tasca") + " " + getDescripcioTascaPerMissatgeUsuari(tasca) + ": " + 
	        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
				logger.error("No s'ha pogut finalitzar la tasca massiu" + tascaId, ex);
			}	
		} else {
			try {
				tascaService.completar(tascaId, transicioSortida);
				if (tasca.isTascaFinalitzacioSegonPla()) {
					MissatgesHelper.success(request, getMessage(request, "info.tasca.finalitza.segon.pla"));
				} else {
					MissatgesHelper.success(request, getMessage(request, "info.tasca.completat"));
				}
				resposta = true;
				
			} catch (ValidacioException ex) {
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.validacio.tasca") + " " + getDescripcioTascaPerMissatgeUsuari(tasca) + ": " + ex.getMessage());
				logger.error("No s'ha pogut finalitzar la tasca " + tascaId, ex);
			} catch (TramitacioValidacioException ex) {
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.validacio.tasca") + " : " + ex.getCause().getMessage());
				logger.error("No s'ha pogut finalitzar la tasca " + tascaId, ex);
			} catch (TramitacioHandlerException ex) {
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.validacio.tasca") + " : " + ex.getPublicMessage());
				logger.error("No s'ha pogut finalitzar la tasca " + tascaId, ex);
			} catch (TramitacioException ex) {
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.finalitzar.tasca") + " " + getDescripcioTascaPerMissatgeUsuari(tasca) + ": " + 
	        					ex.getPublicMessage());
				logger.error("No s'ha pogut finalitzar la tasca " + tascaId, ex);
			} catch (SistemaExternException ex) {
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.finalitzar.tasca") + " : " + ex.getPublicMessage());
				logger.error("No s'ha pogut finalitzar la tasca " + tascaId, ex);
			} catch (Exception ex) {
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.finalitzar.tasca") + " " + getDescripcioTascaPerMissatgeUsuari(tasca) + ": " + 
	        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
				logger.error("No s'ha pogut finalitzar la tasca " + tascaId, ex);
			}
		}
		return resposta;
	}

	private Long accioDocumentAdjuntar(
			HttpServletRequest request,
			String tascaId,
			String documentCodi,
			Date data,
			String nomArxiu,
			byte[] contingutArxiu, 
			String arxiuContentType, 
			boolean ambFirma, 
			DocumentTipusFirmaEnumDto tipusFirma, 
			byte[] firmaContingut) {
		Long documentStoreId = null;
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		boolean firmaSeparada = DocumentTipusFirmaEnumDto.SEPARAT.equals(tipusFirma);
		if (datosTramitacionMasiva != null)	{
			// Programa l'execució massiva
			try
			{				
				String[] tascaIds = (String[]) datosTramitacionMasiva.get("tasquesTramitar");
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds((String[])ArrayUtils.removeElement(tascaIds, tascaId));					
				dto.setExpedientTipusId(null);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("DocGuardar");
				Object[] params = new Object[9];
				params[0] = entorn.getId();				
				params[1] = documentCodi;
				params[2] = (data == null) ? (data == null) ? new Date() : data : data;
				params[3] = contingutArxiu;
				params[4] = nomArxiu;
				params[5] = arxiuContentType;
				params[6] = ambFirma;
				params[7] = firmaSeparada;
				params[8] = firmaContingut;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
								
				documentStoreId = tascaService.guardarDocumentTasca(
						entorn.getId(),
						tascaId,
						documentCodi,
						(data == null) ? new Date() : data,
						nomArxiu,
						contingutArxiu,
						arxiuContentType,
						ambFirma,
						firmaSeparada,
						firmaContingut,
						null);
				MissatgesHelper.success(request, getMessage(request, "info.tasca.massiu.document.guardar", new Object[] {tascaIds.length}));
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut guardar les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			// Guarda el document per la tasca
			try {
				documentStoreId = tascaService.guardarDocumentTasca(
						entorn.getId(),
						tascaId,
						documentCodi,
						(data == null) ? new Date() : data,
						nomArxiu,
						contingutArxiu,
						arxiuContentType,
						ambFirma,
						firmaSeparada,
						firmaContingut,
						null);
				MissatgesHelper.success(request, getMessage(request, "info.document.adjuntat"));
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
			String documentCodi) {
		boolean resposta = false;
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			try {
				String[] tascaIds = (String[]) datosTramitacionMasiva.get("tasquesTramitar");
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds((String[])ArrayUtils.removeElement(tascaIds, tascaId));					
				dto.setExpedientTipusId(null);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("DocEsborrar");
				Object[] params = new Object[2];
				params[0] = entorn.getId();				
				params[1] = documentCodi;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.esborrarDocument(
						tascaId,
						documentCodi,
						null);
				
				MissatgesHelper.success(request, getMessage(request, "info.tasca.massiu.document.esborrar", new Object[] {tascaIds.length}));
				
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut guardar les dades del formulari massiu a la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.esborrarDocument(
						tascaId,
						documentCodi,
						null);
				MissatgesHelper.success(request, getMessage(request, "info.document.esborrat"));
				resposta = true;
	        } catch (Exception ex) {
				String descripcioTasca = getDescripcioTascaPerMissatgeUsuari(tascaId);
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.esborrar.document") + " " + descripcioTasca + ": " + 
	        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
				logger.error("No s'ha pogut esborrar el document de la tasca (" +
						"tascaId=" + tascaId + ", " +
						"documentCodi=" + documentCodi + ")", ex);
	        }
		}
		return resposta;
	}

	private ArxiuDto accioDocumentGenerar(
			HttpServletRequest request,
			String tascaId,
			String documentCodi,
			Date data) {
		ArxiuDto generat = null;
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			try {				
				String[] tascaIds = (String[]) datosTramitacionMasiva.get("tasquesTramitar");
//				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici((Date) datosTramitacionMasiva.get("inici"));
				dto.setEnviarCorreu((Boolean) datosTramitacionMasiva.get("correu"));
				dto.setTascaIds((String[])ArrayUtils.removeElement(tascaIds, tascaId));					
				dto.setExpedientTipusId(null);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("DocGenerar");
				Object[] params = new Object[5];
				params[0] = entorn.getId();
				params[1] = documentCodi;
				params[2] = (data == null) ? new Date() : data;
				// Genera el document per a la tasca
				generat = expedientDocumentService.generarAmbPlantillaPerTasca(
						tascaId,
						documentCodi);
				// Si és null vol dir que s'ha auto ajuntat i s'ha de fer per la resta de tasques
				if (generat == null) {
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					MissatgesHelper.success(
							request,
							getMessage(request, "info.tasca.massiu.document.generar", new Object[] {tascaIds.length}));
				}
			} catch (SistemaExternConversioDocumentException ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu") + " : " + ex.getPublicMessage());
				logger.error("No s'ha pogut generar el document massiu en la tasca " + tascaId, ex);
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut generar el document massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				generat = expedientDocumentService.generarAmbPlantillaPerTasca(
						tascaId,
						documentCodi);
				MissatgesHelper.success(request, getMessage(request, "info.document.generat"));
			} catch (SistemaExternConversioDocumentException ex) {
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.generar.document") + " : " + 
	        					ex.getPublicMessage());
				logger.error("No s'ha pogut generar el document '" + documentCodi + "' de la tasca '" + tascaId + "'", ex);
			} catch (Exception ex) {
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.generar.document") + " : " + 
	        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
				logger.error("No s'ha pogut generar el document '" + documentCodi + "' de la tasca '" + tascaId + "'", ex);
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
	
	/** Retorna una redirecció amb a l'inici de la tramitació de la tasca normal o massivament depenent de si té dades
	 * de tramitació massiva per evitar que es perdin.
	 * 
	 * @param request
	 * @param tascaId
	 * @return
	 */
	private String getRedireccioInici(HttpServletRequest request, String tascaId) {
		String ret;
		Map<String, Object> dadesTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (dadesTramitacionMasiva != null) {
			ret = "redirect:/modal/v3/tasca/" + tascaId + "/form";
		} else {
			ret = "redirect:/modal/v3/tasca/" + tascaId;
		}
		return ret;
	}

	private String getProcessInstanceIdByTaskInstanceId(String taskInstanceId) {
		var tasca = tascaClientService.getTascaV1(taskInstanceId);
		if (tasca == null) {
			return null;
		}
		return tasca.getProcesId();
	}

	private static final Logger logger = LoggerFactory.getLogger(TascaTramitacioController.class);

}
