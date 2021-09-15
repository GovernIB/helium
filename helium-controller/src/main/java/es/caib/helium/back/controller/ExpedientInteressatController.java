/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.InteressatCommand;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.client.model.ParellaCodiValor;
import es.caib.helium.logic.intf.dto.InteressatDto;
import es.caib.helium.logic.intf.dto.InteressatTipusEnumDto;
import es.caib.helium.logic.intf.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.helium.logic.intf.service.ExpedientInteressatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador per a la pàgina d'informació de l'termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientInteressatController extends BaseExpedientController {


	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;


	@RequestMapping(value="/{expedientId}/interessat", method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		
		model.addAttribute("expedientId", expedientId);	
		return "v3/interessatLlistat";
	}

	@RequestMapping(value="/{expedientId}/interessat/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		
		Map<String, String[]> mapeigOrdenacions = new HashMap<String, String[]>();
		mapeigOrdenacions.put("fullNom", new String[] {"nom", "llinatge1", "llinatge2"});
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request, null, mapeigOrdenacions);
		
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientInteressatService.findPerDatatable(
						expedientId,
						paginacioParams.getFiltre(),
						paginacioParams));
	}

	@RequestMapping(value = "/{expedientId}/interessat/new", method = RequestMethod.GET)
	public String newGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute(new InteressatCommand());
		model.addAttribute("expedientId", expedientId);
		return "v3/interessatForm";
	}
	@RequestMapping(value = "/{expedientId}/interessat/new", method = RequestMethod.POST)
	public String newPost(
			HttpServletRequest request,
			@Validated(InteressatCommand.Creacio.class) InteressatCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/interessatForm";
        } else {
        	expedientInteressatService.create(
    				conversioTipusHelper.convertir(
    						command,
    						InteressatDto.class));
			MissatgesHelper.success(request, getMessage(request, "interessat.controller.creat") );
			return modalUrlTancar(false);
        }
	}

	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/update", method = RequestMethod.GET)
	public String updateGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			Model model) {
		InteressatDto dto = expedientInteressatService.findOne(
				interessatId);
		
		model.addAttribute(
				conversioTipusHelper.convertir(
						dto,
						InteressatCommand.class));
		return "v3/interessatForm";
	}
	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/update", method = RequestMethod.POST)
	public String updatePost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			@Validated(InteressatCommand.Modificacio.class) InteressatCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/interessatForm";
        } else {
        	expedientInteressatService.update(
        			conversioTipusHelper.convertir(
    						command,
    						InteressatDto.class));
			MissatgesHelper.success(request, getMessage(request, "interessat.controller.modificat") );
			return modalUrlTancar(false);
        }
	}
	
	
	@ModelAttribute("interessatTipusEstats")
	public List<ParellaCodiValor> populateEstats(HttpServletRequest request) {
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		resposta.add(new ParellaCodiValor(getMessage(request, "interessat.tipus.enum.ADMINISTRACIO"), InteressatTipusEnumDto.ADMINISTRACIO));
		resposta.add(new ParellaCodiValor(getMessage(request, "interessat.tipus.enum.FISICA"), InteressatTipusEnumDto.FISICA));
		resposta.add(new ParellaCodiValor(getMessage(request, "interessat.tipus.enum.JURIDICA"), InteressatTipusEnumDto.JURIDICA));
		return resposta;
	}
	
	@ModelAttribute("NotificaDomiciliConcretTipus")
	public List<ParellaCodiValor> populateDomiciliTipus(HttpServletRequest request) {
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		//resposta.add(new ParellaCodiValor(getMessage(request, "domiciliconcret.tipus.enum.NACIONAL"), NotificaDomiciliConcretTipusEnumDto.NACIONAL));
		//resposta.add(new ParellaCodiValor(getMessage(request, "domiciliconcret.tipus.enum.ESTRANGER"), NotificaDomiciliConcretTipusEnumDto.ESTRANGER));
		//resposta.add(new ParellaCodiValor(getMessage(request, "domiciliconcret.tipus.enum.APARTAT_CORREUS"), NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS));
		resposta.add(new ParellaCodiValor(getMessage(request, "domiciliconcret.tipus.enum.SENSE_NORMALITZAR"), NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR));
		return resposta;
	}
	
	@ModelAttribute("serveiTipusEstats")
	public List<ParellaCodiValor> populateServeiEstats(HttpServletRequest request) {
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		resposta.add(new ParellaCodiValor(getMessage(request, "notifica.servei.tipus.enum.NORMAL"), ServeiTipusEnumDto.NORMAL));
		resposta.add(new ParellaCodiValor(getMessage(request, "notifica.servei.tipus.enum.URGENT"), ServeiTipusEnumDto.URGENT));
		return resposta;
	}

	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			Model model) {
		try {
			expedientInteressatService.delete(interessatId);
			MissatgesHelper.success(request, getMessage(request, "interessat.controller.esborrat"));
		} catch (Exception ex) {
			String errMsg = getMessage(request, "interessat.controller.esborrar.error", new Object[] {ex.getMessage()});
			logger.error(errMsg, ex);
			MissatgesHelper.error(request, errMsg);
		}
		return "redirect:/v3/expedient/"+expedientId+"?pipellaActiva=interessats";
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientInteressatController.class);

}
