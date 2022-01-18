package es.caib.helium.back.controller;

import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.ModalHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.back.helper.SessionHelper.SessionManager;
import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.ReproDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.dto.TascaDocumentDto;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.ReproService;
import es.caib.helium.logic.intf.service.TascaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controlador base per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseTascaController extends BaseController {
	protected String TAG_PARAM_REGEXP = "<!--helium:param-(.+?)-->";
	protected static final String VARIABLE_TRAMITACIO_MASSIVA = "variableTramitacioMassiva";
	protected static final String VARIABLE_COMMAND_BINDING_RESULT_TRAMITACIO = "variableBindingResultTramitacio";

	@Autowired
	protected TascaService tascaService;
	@Autowired
	protected DissenyService dissenyService;
	@Autowired
	private ReproService reproService;
	

	protected String mostrarInformacioTascaPerPipelles(
			HttpServletRequest request,
			String tascaId,
			Model model,
			String pipellaActiva,
			Map<String, Object> valorsFormulariExtern
			) throws Exception {
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		List<ReproDto> repros = reproService.findReprosByUsuariTipusExpedient(tasca.getExpedientTipusId(), tasca.getJbpmName());
		model.addAttribute("tasca", tasca);
		model.addAttribute("expedientId", tasca.getExpedientId());
		model.addAttribute("repros", repros);
		List<Object> nomesLectura = new ArrayList<Object>();
		
		List<TascaDadaDto> dades = tascaService.findDades(tascaId);
		Iterator<TascaDadaDto> itDades = dades.iterator();
		while (itDades.hasNext()) {
			TascaDadaDto dada = itDades.next();
			if (dada.isReadOnly()) {				
				setErrorValidate(request, tascaId, dada);
				nomesLectura.add(dada);
			}
		}
		
		List<TascaDocumentDto> documents = tascaService.findDocuments(tascaId);
		Iterator<TascaDocumentDto> itDocuments = documents.iterator();
		while (itDocuments.hasNext()) {
			TascaDocumentDto document = itDocuments.next();
			if (document.isReadOnly() && document.getId() != null) {
				nomesLectura.add(document);
			}
		}
		
		model.addAttribute("nomesLectura", nomesLectura);		
		
		String pipellaPerDefecte = null;
		if (tascaService.hasFormulari(tascaId)) {
			model.addAttribute(
					"hasFormulari",
					true);
			pipellaPerDefecte = "form";
		}
		if (tascaService.hasDocumentsNotReadOnly(tascaId)) {
			model.addAttribute(
					"hasDocuments",
					true);
			if (pipellaPerDefecte == null || ( tasca.isValidada() && ! tasca.isDocumentsComplet()))
				pipellaPerDefecte = "document";
		}
		if (tascaService.hasSignatures(tascaId)) {
			model.addAttribute(
					"hasSignatures",
					true);
			if (pipellaPerDefecte == null || ( tasca.isValidada() && tasca.isDocumentsComplet() && ! tasca.isSignaturesComplet()))
				pipellaPerDefecte = "signatura";
		}
		if (pipellaActiva != null)
			model.addAttribute("pipellaActiva", pipellaActiva);
		else if (request.getParameter("pipellaActiva") != null)
			model.addAttribute("pipellaActiva", request.getParameter("pipellaActiva").substring("pipella-".length()));
		else {
			model.addAttribute("pipellaActiva", pipellaPerDefecte != null ? pipellaPerDefecte : "form");
		}
		model.addAttribute("isModal", ModalHelper.isModal(request));
		
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			model.addAttribute("tasquesTramitar", datosTramitacionMasiva.get("tasquesTramitar"));
		}
		
		//Si tenim error d'execució en segón pla, el mostrarem
		if (tasca.getErrorFinalitzacio() != null && pipellaActiva != null) {
			MissatgesHelper.error(request, "Error execució segon pla: " + tasca.getErrorFinalitzacio());
		}
		
		if (pipellaActiva != null && 
				(pipellaActiva.equalsIgnoreCase("document") && request.getMethod().equalsIgnoreCase("POST")) || 
				request.getRequestURI().split("/")[request.getRequestURI().split("/").length -1].equalsIgnoreCase("esborrar")){
			if (ModalHelper.isModal(request)) {
				return "redirect:/modal/v3/tasca/" + tascaId + "/" + pipellaActiva;
			} else {
				return "redirect:/v3/tasca/" + tascaId + "/" + pipellaActiva;
			}
		}else{
			return "v3/tascaPipelles";	
		}
	}
	
	protected String mostrarInformacioTascaPerPipellesAmbCommand(
			HttpServletRequest request,
			String tascaId,
			Model model,
			Object command,
			String pipellaActiva) throws Exception {
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		List<ReproDto> repros = reproService.findReprosByUsuariTipusExpedient(tasca.getExpedientTipusId(), tasca.getJbpmName());
		model.addAttribute("tasca", tasca);
		model.addAttribute("expedientId", tasca.getExpedientId());
		model.addAttribute("repros", repros);
		List<Object> nomesLectura = new ArrayList<Object>();
		
		List<TascaDadaDto> dades = tascaService.findDades(tascaId);
		Iterator<TascaDadaDto> itDades = dades.iterator();
		while (itDades.hasNext()) {
			TascaDadaDto dada = itDades.next();
			if (dada.isReadOnly()) {				
				setErrorValidate(request, tascaId, dada);
				nomesLectura.add(dada);
			}
		}
		
		List<TascaDocumentDto> documents = tascaService.findDocuments(tascaId);
		Iterator<TascaDocumentDto> itDocuments = documents.iterator();
		while (itDocuments.hasNext()) {
			TascaDocumentDto document = itDocuments.next();
			if (document.isReadOnly() && document.getId() != null) {
				nomesLectura.add(document);
			}
		}
		
		model.addAttribute("nomesLectura", nomesLectura);		
		
		String pipellaPerDefecte = null;
		if (tascaService.hasFormulari(tascaId)) {
			model.addAttribute(
					"hasFormulari",
					true);
			pipellaPerDefecte = "form";
		}
		if (tascaService.hasDocumentsNotReadOnly(tascaId)) {
			model.addAttribute(
					"hasDocuments",
					true);
			if (pipellaPerDefecte == null || ( tasca.isValidada() && ! tasca.isDocumentsComplet()))
				pipellaPerDefecte = "document";
		}
		if (tascaService.hasSignatures(tascaId)) {
			model.addAttribute(
					"hasSignatures",
					true);
			if (pipellaPerDefecte == null || ( tasca.isValidada() && tasca.isDocumentsComplet() && ! tasca.isSignaturesComplet()))
				pipellaPerDefecte = "signatura";
		}
		if (pipellaActiva != null)
			model.addAttribute("pipellaActiva", pipellaActiva);
		else if (request.getParameter("pipellaActiva") != null)
			model.addAttribute("pipellaActiva", request.getParameter("pipellaActiva").substring("pipella-".length()));
		else {
			model.addAttribute("pipellaActiva", pipellaPerDefecte != null ? pipellaPerDefecte : "form");
		}
		model.addAttribute("isModal", ModalHelper.isModal(request));
		
		Map<String, Object> datosTramitacionMasiva = getDatosTramitacionMasiva(request);
		if (datosTramitacionMasiva != null) {
			model.addAttribute("tasquesTramitar", datosTramitacionMasiva.get("tasquesTramitar"));
		}
		
		if (command != null) {
			model.addAttribute("command", command);
		}
		
		//Si tenim error d'execució en segón pla, el mostrarem
		if (tasca.getErrorFinalitzacio() != null && pipellaActiva != null) {
			MissatgesHelper.error(request, "Error execució segon pla: " + tasca.getErrorFinalitzacio());
		}
		
		if (pipellaActiva != null && 
				(pipellaActiva.equalsIgnoreCase("document") && request.getMethod().equalsIgnoreCase("POST")) || 
				request.getRequestURI().split("/")[request.getRequestURI().split("/").length -1].equalsIgnoreCase("esborrar")){
			if (ModalHelper.isModal(request)) {
				return "redirect:/modal/v3/tasca/" + tascaId + "/" + pipellaActiva;
			} else {
				return "redirect:/v3/tasca/" + tascaId + "/" + pipellaActiva;
			}
		}else{
			return "v3/tascaPipelles";	
		}
	}

	private void setErrorValidate(HttpServletRequest request, String tascaId, TascaDadaDto dada) {		
		Object bindingResult = SessionHelper.getAttribute(request,VARIABLE_COMMAND_BINDING_RESULT_TRAMITACIO+tascaId);
		if (bindingResult != null && ((BindingResult) bindingResult).hasErrors()) {
			for (FieldError error : ((BindingResult) bindingResult).getFieldErrors()) {
				if (dada.getVarCodi().equals(error.getField())) {
					dada.setError(getMessage(request, "lectura." + error.getCode()));
					break;
				}
			}
		}	
	}

	protected String getReturnUrl(
			HttpServletRequest request,
			String tascaId,
			String sufix) {
		String suf = (sufix != null) ? "/" + sufix : "";
		if (ModalHelper.isModal(request))
			return "redirect:/modal/v3/tasca/" + tascaId + suf;
		else
			return "redirect:/v3/tasca/" + tascaId + suf;
	}

	protected String guardarDatosTramitacionMasiva(HttpServletRequest request, Set<String> seleccio, String inici, Boolean correu) {
		Map<String, Object> camps = new HashMap<String, Object>();		
		String[] tascaIds = new String[seleccio.size()];
		int i = 0;
		for (String tId : seleccio) {
			tascaIds[i++] = tId.toString();
		}

		Date iniciMassiva = new Date();
		if (inici != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			try {
				iniciMassiva = sdf.parse(inici);
			} catch (ParseException e) {}
		}
		
		camps.put("inici", iniciMassiva);
		camps.put("correu", correu);
		camps.put("tasquesTramitar", tascaIds);
		SessionHelper.setAttribute(request,VARIABLE_TRAMITACIO_MASSIVA, camps);
		return tascaIds[0];
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getDatosTramitacionMasiva(HttpServletRequest request) {
		Object obj = SessionHelper.getAttribute(request, VARIABLE_TRAMITACIO_MASSIVA);
		if (obj instanceof Map) {
			return (Map<String, Object>) obj;
		}
		return null;
	}

	protected Map<String, String> getFormRecursParams(String text) {
		Map<String, String> params = new HashMap<String, String>();
		Pattern pattern = Pattern.compile(TAG_PARAM_REGEXP);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String[] paramParts = matcher.group(1).split(":");
			if (paramParts.length == 2) {
				params.put(paramParts[0], paramParts[1]);
			}
		}
		return params;
	}
	
	protected Set<String> getSeleccioConsultaTasca(HttpServletRequest request) {
		Map<String, Object> dadesTramitacio = getDatosTramitacionMasiva(request);
		String[] ids = (String[]) dadesTramitacio.get("tasquesTramitar");
		Set<String> mySet = new HashSet<String>();
		for (String id: ids) {
			mySet.add(id);
		}
		return mySet;
	}
	
	protected void esborrarSeleccio(HttpServletRequest request) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<String> ids = sessionManager.getSeleccioConsultaTasca();
		ids.clear();
	}
}