/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ModalHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TerminiTypeEditorHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
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

/**
 * Controlador per a la tramitació de taques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class TascaTramitacioController extends BaseController {

	protected String TAG_PARAM_REGEXP = "<!--helium:param-(.+?)-->";
	public static final String VARIABLE_SESSIO_CAMP_FOCUS = "helCampFocus";

	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected TascaService tascaService;
	@Autowired
	protected DissenyService dissenyService;
	@Autowired
	protected ExecucioMassivaService execucioMassivaService;

	protected Validator validatorGuardar;
	protected Validator validatorValidar;

	@SuppressWarnings("rawtypes")
	protected Object populateCommand(
			HttpServletRequest request, 
			String tascaId,
			Model model) {		
		Object commandSessio = TascaFormHelper.recuperarCommandTemporal(request, true);
		if (commandSessio != null) {
			return commandSessio;
		} else {
			try {
				return TascaFormHelper.getCommandForCamps(tascaService.findDades(tascaId), null, request, new HashMap<String, Object>(), new HashMap<String, Class>(), false);
			} catch (TascaNotFoundException ex) {
				MissatgesHelper.error(request, ex.getMessage());
				logger.error("No s'han pogut encontrar la tasca: " + ex.getMessage(), ex);
			} catch (Exception ignored) {} 
		}
		return null;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}", method = RequestMethod.GET)
	public String tramitar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		if (ModalHelper.isModal(request))
			return "redirect:/modal/v3/expedient/" + expedientId + "/tasca/" + tascaId + "/form";
		else
			return "redirect:/v3/expedient/" + expedientId + "/tasca/" + tascaId + "/form";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		if (tasca.getRecursForm() != null && tasca.getRecursForm().length() > 0) {
			try {
				byte[] contingut = dissenyService.getDeploymentResource(tasca.getDefinicioProces().getId(), tasca.getRecursForm());
				model.addAttribute("formRecursParams", getFormRecursParams(new String(contingut, "UTF-8")));
			} catch (Exception ex) {
				logger.error("No s'han pogut extreure els parametres del recurs", ex);
			}
		}
		model.addAttribute("tasca", tasca);
		
		// Omple les dades del formulari i les de només lectura
		model.addAttribute("command", populateCommand(request, tascaId, model));
		
		List<TascaDadaDto> dadesNomesLectura = new ArrayList<TascaDadaDto>();
		List<TascaDadaDto> dades = tascaService.findDades(tascaId);
		Iterator<TascaDadaDto> itDades = dades.iterator();
		while (itDades.hasNext()) {
			TascaDadaDto dada = itDades.next();
			if (dada.isReadOnly()) {
				dadesNomesLectura.add(dada);
				itDades.remove();
			}
		}
		model.addAttribute("dadesNomesLectura", dadesNomesLectura);
		model.addAttribute("dades", dades);
		
		// Omple els documents per adjuntar i els de només lectura
		List<TascaDocumentDto> documents = tascaService.findDocuments(tascaId);
		List<TascaDocumentDto> documentsNomesLectura = new ArrayList<TascaDocumentDto>();
		Iterator<TascaDocumentDto> itDocuments = documents.iterator();
		while (itDocuments.hasNext()) {
			TascaDocumentDto document = itDocuments.next();
			if (document.isReadOnly()) {
				if (document.getId() != null)
					documentsNomesLectura.add(document);
				itDocuments.remove();
			}
		}
		model.addAttribute("documentsNomesLectura", documentsNomesLectura);
		model.addAttribute("documents", documents);
		
		return "v3/expedientTascaTramitacio";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@Valid @ModelAttribute("command") Object command,
			BindingResult result, 
			SessionStatus status,
			@RequestParam(value = "accio", required = false) String accio,
			ModelMap model) {
				
		return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/camp/{campId}/valorsSeleccio", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> valorsSeleccio(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable Long campId,
			Model model) {
		return tascaService.findllistaValorsPerCampDesplegable(
				tascaId,
				campId,
				null,
				new HashMap<String, Object>());
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/camp/{campId}/valorsSeleccio/{valor}", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> valorsSeleccio(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable Long campId,
			@PathVariable String valor,
			Model model) {
		return tascaService.findllistaValorsPerCampDesplegable(
				tascaId,
				campId,
				valor,
				new HashMap<String, Object>());
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/camp/{campId}/valorSeleccioInicial/{valor}", method = RequestMethod.GET)
	@ResponseBody
	public SeleccioOpcioDto valorsSeleccioInicial(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable Long campId,
			@PathVariable String valor,
			Model model) {
		List<SeleccioOpcioDto> valorsSeleccio = tascaService.findllistaValorsPerCampDesplegable(
				tascaId,
				campId,
				null,
				getMapDelsValors(valor));
		for (SeleccioOpcioDto sel : valorsSeleccio) {
			if (sel.getCodi().equals(valor)) {
				return sel;
			}
		}
		return new SeleccioOpcioDto();
	}

	@ModelAttribute("listTerminis")
	public List<ParellaCodiValorDto> valors12(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (int i=0; i <= 12 ; i++)		
			resposta.add(new ParellaCodiValorDto(String.valueOf(i), i));
		return resposta;
	}

	private Map<String, Object> getMapDelsValors(String valors) {
		if (valors == null)
			return null;
		Map<String, Object> resposta = new HashMap<String, Object>();
		String[] parelles = valors.split(",");
		for (int i = 0; i < parelles.length; i++) {
			String[] parts = parelles[i].split(":");
			if (parts.length == 2)
				resposta.put(parts[0], parts[1]);
		}
		return resposta;
	}
	
	private Map<String, String> getFormRecursParams(String text) {
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
				new CustomBooleanEditor(false));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				TerminiDto.class,
				new TerminiTypeEditorHelper());
	}

	private static final Logger logger = LoggerFactory.getLogger(TascaTramitacioController.class);

}
