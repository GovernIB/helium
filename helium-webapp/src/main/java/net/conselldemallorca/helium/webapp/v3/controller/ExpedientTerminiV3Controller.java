/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTerminiModificarCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTerminiModificarCommand.TerminiModificacioTipus;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per a la pàgina d'informació de l'termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTerminiV3Controller extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;

	@Autowired
	private TerminiService terminiService;

	@RequestMapping(value = "/{expedientId}/terminis", method = RequestMethod.GET)
	public String terminis(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("terminis", dissenyService.findTerminisAmbExpedientId(expedientId));
		model.addAttribute("iniciats", dissenyService.findIniciatsAmbExpedientId(expedientId));
			
		return "v3/expedient/terminis";
	}

	@RequestMapping(value = "/{expedientId}/{terminiId}/terminiIniciar", method = RequestMethod.GET)
	public String terminiIniciar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long terminiId,
			Model model) {
		try {
			if (!NodecoHelper.isNodeco(request)) {
				mostrarInformacioExpedientPerPipella(request, expedientId, model, "terminis", expedientService);
			}
			terminiService.iniciar(terminiId,expedientId,new Date(),true);
			MissatgesHelper.info(request, getMessage(request, "info.termini.iniciat"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.iniciar.termini"));
        	logger.error("No s'ha pogut iniciar el termini", ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/{terminiId}/terminiPausar", method = RequestMethod.GET)
	public String terminiPausar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long terminiId,
			Model model) {
		try {
			if (!NodecoHelper.isNodeco(request)) {
				mostrarInformacioExpedientPerPipella(request, expedientId, model, "terminis", expedientService);
			}
			terminiService.pausar(terminiId, new Date());
			MissatgesHelper.info(request, getMessage(request, "info.termini.aturat"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.aturar.termini"));
			logger.error("No s'ha pogut aturar el termini", ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/{terminiId}/terminiContinuar", method = RequestMethod.GET)
	public String terminiContinuar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long terminiId,
			Model model) {
		try {
			if (!NodecoHelper.isNodeco(request)) {
				mostrarInformacioExpedientPerPipella(request, expedientId, model, "terminis", expedientService);
			}
			terminiService.continuar(terminiId, new Date());
			MissatgesHelper.info(request, getMessage(request, "info.termini.continuat"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.continuat.termini"));
			logger.error("No s'ha pogut continuat el termini", ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/{terminiId}/terminiCancelar", method = RequestMethod.GET)
	public String terminiCancelar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long terminiId,
			Model model) {
		try {
			if (!NodecoHelper.isNodeco(request)) {
				mostrarInformacioExpedientPerPipella(request, expedientId, model, "terminis", expedientService);
			}
			terminiService.cancelar(terminiId, new Date());
			MissatgesHelper.info(request, getMessage(request, "info.termini.cancelat"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.cancelat.termini"));
			logger.error("No s'ha pogut cancel·lar el termini", ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/modal/{expedientId}/{terminiId}/terminiModificar", method = RequestMethod.GET)
	public String terminiModificar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long terminiId,
			Model model) {
		TerminiIniciatDto terminiIniciat = dissenyService.findIniciatAmbId(terminiId);
		ExpedientTerminiModificarCommand expedientTerminiModificarCommand = new ExpedientTerminiModificarCommand();
		expedientTerminiModificarCommand.setTerminiId(terminiId);
		expedientTerminiModificarCommand.setNom(terminiIniciat.getTermini().getNom());
		expedientTerminiModificarCommand.setAnys(terminiIniciat.getAnys());
		expedientTerminiModificarCommand.setMesos(terminiIniciat.getMesos());
		expedientTerminiModificarCommand.setDies(terminiIniciat.getDies());
		expedientTerminiModificarCommand.setDataInici(terminiIniciat.getDataInici());
		expedientTerminiModificarCommand.setDataFi(terminiIniciat.getDataFi());
		model.addAttribute(expedientTerminiModificarCommand);
		model.addAttribute("listTerminis", valors12());
		model.addAttribute("listTipus", getTipus(request));
		return "v3/expedient/terminiModificar";
	}
	
	public List<ParellaCodiValorDto> getTipus(HttpServletRequest request) {
		List<ParellaCodiValorDto> tipus = new ArrayList<ParellaCodiValorDto>();
		tipus.add(new ParellaCodiValorDto(getMessage(request, "termini.durada"),TerminiModificacioTipus.DURADA));
		tipus.add(new ParellaCodiValorDto(getMessage(request, "termini.data_fi"),TerminiModificacioTipus.DATA_FI));
		tipus.add(new ParellaCodiValorDto(getMessage(request, "termini.data_ini"),TerminiModificacioTipus.DATA_INICI));
		return tipus;
	}

	private List<ParellaCodiValorDto> valors12() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (int i=0; i <= 12 ; i++)		
			resposta.add(new ParellaCodiValorDto(String.valueOf(i), i));
		return resposta;
	}
	
	@RequestMapping(value = "/modal/{expedientId}/{terminiId}/terminiModificar", method = RequestMethod.POST)
	public String terminiModificar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long terminiId,
			@Valid @ModelAttribute ExpedientTerminiModificarCommand expedientTerminiModificarCommand,
			BindingResult result,
			SessionStatus status,
			Model model) {
		try {
			Date inicio = null;
			if (TerminiModificacioTipus.DURADA.name().equals(expedientTerminiModificarCommand.getTipus())) {
				TerminiIniciatDto terminiIniciat = dissenyService.findIniciatAmbId(terminiId);
				inicio = terminiIniciat.getDataInici();
			} else if (TerminiModificacioTipus.DATA_INICI.name().equals(expedientTerminiModificarCommand.getTipus())) {
				inicio = expedientTerminiModificarCommand.getDataInici();
			} else {
				inicio = expedientTerminiModificarCommand.getDataFi();
			}
			terminiService.modificar(
					terminiId,
					expedientId,
					inicio,
					expedientTerminiModificarCommand.getAnys(),
					expedientTerminiModificarCommand.getMesos(),
					expedientTerminiModificarCommand.getDies(),
					TerminiModificacioTipus.DATA_FI.name().equals(expedientTerminiModificarCommand.getTipus()));
			MissatgesHelper.info(request, getMessage(request, "info.termini.modificat"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.modificar.termini"));
        	logger.error("No s'ha pogut modificar el termini", ex);
		}
		return modalUrlTancar();
	}	

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTerminiV3Controller.class);
}
