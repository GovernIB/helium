package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProgresActualitzacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProgresActualitzacioDto.ActualitzacioInfo;
import net.conselldemallorca.helium.v3.core.api.service.ProcedimentService;
import net.conselldemallorca.helium.webapp.v3.command.ProcedimentFiltreCommand;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per al manteniment dels procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/procediment")
public class ProcedimentController extends BaseController{
	

	@Autowired
	private ProcedimentService procedimentService;

	private static final String SESSION_ATTRIBUTE_FILTRE = "ProcedimentController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "ProcedimentController.session.seleccio";

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request, 
			Model model) {
		
		ProcedimentFiltreCommand procedimentFiltreCommand = getFiltreCommand(request);
		model.addAttribute("procedimentFiltreCommand", procedimentFiltreCommand);
		this.modelEstats(model);
		
		return "v3/procedimentLlistat";
	}	
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request, 
			@Valid ProcedimentFiltreCommand filtreCommand, 
			BindingResult bindingResult, 
			@RequestParam(value = "accio", required = false) String accio, 
			Model model) {
		
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(
					request,
					SESSION_ATTRIBUTE_FILTRE);
		} else {
			if (!bindingResult.hasErrors()) {
				SessionHelper.setAttribute(
						request,
						SESSION_ATTRIBUTE_FILTRE,
						filtreCommand);
			}
		}
		return "redirect:procediment";
		
	}	

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		
		ProcedimentFiltreCommand procedimentFiltreCommand = getFiltreCommand(request);
		
		return DatatablesHelper.getDatatableResponse(
				request, 
				procedimentService.findAmbFiltre(
						ProcedimentFiltreCommand.asDto(procedimentFiltreCommand), 
						DatatablesHelper.getPaginacioDtoFromRequest(request)), 
				SESSION_ATTRIBUTE_SELECCIO);
	}
	
	private ProcedimentFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ProcedimentFiltreCommand procedimentFiltreCommand = (ProcedimentFiltreCommand) SessionHelper.getAttribute(
				request, 
				SESSION_ATTRIBUTE_FILTRE);
		if (procedimentFiltreCommand == null) {
			procedimentFiltreCommand = new ProcedimentFiltreCommand();
			SessionHelper.setAttribute(
					request, 
					SESSION_ATTRIBUTE_FILTRE, 
					procedimentFiltreCommand);
		}
		
		return procedimentFiltreCommand;
	}
	
	
	/** Posa els valors de l'enumeració estats en el model */
	private void modelEstats(Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
		for(ProcedimentEstatEnumDto estat : ProcedimentEstatEnumDto.values())
			opcions.add(new ParellaCodiValorDto(
					estat.name(),
					MessageHelper.getInstance().getMessage("procediment.estat.enum." + estat.name())));		

		model.addAttribute("estats", opcions);
	}

	// Mètode per actualitzar procediments
	
	@RequestMapping(value = "/actualitzar")
	public String actualitzar(
			HttpServletRequest request, 
			Model model) throws Exception {
		
		model.addAttribute("isUpdatingProcediments", procedimentService.isUpdatingProcediments());
		return "v3/procedimentActualitzacioForm";
	}
	
	@RequestMapping(value = "/actualitzar", method = RequestMethod.POST)
	public String actualitzacioAutomaticaPost(
			HttpServletRequest request,
			Model model) {

		try {
			procedimentService.actualitzaProcediments();
		} catch (Exception e) {
			String errMsg = "Error inesperat al actualitzar els procediments: " + e.toString();
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
			return "v3/procedimentActualitzacioForm";
		}

		return getAjaxControllerReturnValueSuccess(
				request,
				"v3/procedimentActualitzacioForm",
				"procediment.controller.actualitzar.ok");
	}

	@RequestMapping(value = "/actualitzar/progres", method = RequestMethod.GET)
	@ResponseBody
	public ProgresActualitzacioDto getProgresActualitzacio(
			HttpServletRequest request,
			@RequestParam(value = "index", required = false) Integer index) {
		
		return this.getCopiaProgres(
					procedimentService.getProgresActualitzacio(), 
					index);
	}


	/** Mètode per retornar una còpia amb una subllista de informació en comptes de retornar tota
	 * la informació de progrés.
	 * 
	 * @param progres Objecte amb la informació del progrés.
	 * @param index Índex a partir de la cual es retorna la llista.
	 * @return
	 */
	private ProgresActualitzacioDto getCopiaProgres(ProgresActualitzacioDto progres, Integer index) {

		ProgresActualitzacioDto ret = new ProgresActualitzacioDto();
		if (progres != null) {
			ret.setError(progres.isError());
			ret.setErrorMsg(progres.getErrorMsg());
			ret.setFinished(progres.isFinished());
			ret.setNumElementsActualitzats(progres.getNumElementsActualitzats());
			ret.setNumOperacions(progres.getNumOperacions());
			ret.setProgres(progres.getProgres());
			ret.setNNous(progres.getNNous());
			ret.setNExtingits(progres.getNExtingits());
			ret.setNCanvis(progres.getNCanvis());
			ret.setNAvisos(progres.getNAvisos());
			ret.setNErrors(progres.getNErrors());
			ret.setAvisos(progres.getAvisos());
			ret.setNInfo(progres.getNInfo());
			List<ActualitzacioInfo> infoList = progres.getInfo();
			if (index != null) {
				int lenght = infoList.size();
				for (int i = Math.min(index, lenght - 1); i < lenght; i++) {
					ret.addInfo(infoList.get(i));
				}
			} else {
				ret.setInfo(infoList);
			}
			
		} else {
			ret.addInfo("No hi ha cap procés actualment");
			ret.setFinished(true);
		}

		return ret;
	}


	private static final Log logger = LogFactory.getLog(ProcedimentController.class);
}
