/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.ExpedientTipusDominiCommand;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.NodecoHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.DominiDto.OrigenCredencials;
import es.caib.helium.logic.intf.dto.DominiDto.TipusAuthDomini;
import es.caib.helium.logic.intf.dto.DominiDto.TipusDomini;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.ParellaCodiValorDto;
import es.caib.helium.logic.intf.service.DominiService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador per a la pipella de variables del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusDominiController extends BaseExpedientTipusController {

	@Autowired
	protected DominiService dominiService;

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	@ModelAttribute("tipusDominis")
	public List<ParellaCodiValorDto> populateTipusDominis() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (TipusDomini tipus: TipusDomini.values()) {
			resposta.add(new ParellaCodiValorDto(tipus.name(), "expedient.tipus.domini.tipus." + tipus.name()));
		}
		
		return resposta;
	}
	
	@ModelAttribute("tipusAutenticacio")
	public List<ParellaCodiValorDto> populateTipusAutenticacio() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (TipusAuthDomini tipus: TipusAuthDomini.values()) {
			resposta.add(new ParellaCodiValorDto(tipus.name(), "expedient.tipus.domini.tipus.auth." + tipus.name()));
		}
		return resposta;
	}
	
	@ModelAttribute("credencialsOrigen")
	public List<ParellaCodiValorDto> populateOrigenCredencials() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (OrigenCredencials tipus: OrigenCredencials.values()) {
			resposta.add(new ParellaCodiValorDto(tipus.name(), "expedient.tipus.domini.origen." + tipus.name()));
		}
		return resposta;
	}
	
	@RequestMapping(value = "/{expedientTipusId}/dominis")
	public String dominis(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"dominis");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
		}
		return "v3/expedientTipusDomini";
	}

	@RequestMapping(value="/{expedientTipusId}/dominis/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				dominiService.findPerDatatable(
						entornActual.getId(),
						expedientTipusId,
						false, // incloure globals
						paginacioParams.getFiltre(),
						paginacioParams));		
	}
	
	@RequestMapping(value = "/{expedientTipusId}/domini/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(required = false) Long agrupacioId,
			Model model) {
		ExpedientTipusDominiCommand command = new ExpedientTipusDominiCommand();
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("expedientTipusDominiCommand", command);
		return "v3/expedientTipusDominiForm";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/domini/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusDominiCommand.Creacio.class) ExpedientTipusDominiCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("expedientTipusId", expedientTipusId);
        	return "v3/expedientTipusDominiForm";
        } else {
        	// Verificar permisos
        	EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
        	dominiService.create(
        			entornActual.getId(),
    				expedientTipusId,
    				conversioTipusHelper.convertir(
    						command,
    						DominiDto.class));    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.domini.controller.creat"));
			return modalUrlTancar(false);	
			
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/domini/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		DominiDto dto = dominiService.findAmbId(expedientTipusId, id);
		ExpedientTipusDominiCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusDominiCommand.class);
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusDominiCommand", command);
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("heretat", dto.isHeretat());
		return "v3/expedientTipusDominiForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/domini/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(ExpedientTipusDominiCommand.Modificacio.class) ExpedientTipusDominiCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("expedientTipusId", expedientTipusId);
    		model.addAttribute("heretat", dominiService.findAmbId(expedientTipusId, id).isHeretat());
       	return "v3/expedientTipusDominiForm";
        } else {
        	dominiService.update(
        			conversioTipusHelper.convertir(
    						command,
    						DominiDto.class));
        	MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.domini.controller.modificat"));
			return modalUrlTancar(false);
        }
	}
	
	@RequestMapping(value = "/{expedientTipusId}/domini/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean borrar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		try {
			dominiService.delete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.domini.controller.eliminat"));
			return true;
		} catch (Exception e) {
			logger.error("No s'ha pogut eliminar el nomini", e);
			MissatgesHelper.error(
					request, 
					getMessage(request, "expedient.tipus.domini.controller.eliminat.no"));
			return false;
		}
	}
	
	/** Mètode per provar un domini */
	@RequestMapping(value="domini/{dominiId}/test", method = RequestMethod.GET)
	public String test(
			HttpServletRequest request,
			@PathVariable Long dominiId,
			Model model) {
		model.addAttribute("dominiId", dominiId);
		return "v3/provaDomini";
	}
	
	/** Mètode per provar un domini */
	@RequestMapping(value="domini/{dominiId}/test", method = RequestMethod.POST, consumes="application/json", produces = "application/json") 
	@ResponseBody
	public ResponseEntity<Object> testS(
			HttpServletRequest request,
			@PathVariable Long dominiId,
			Model model,
			@RequestBody Cmd params) {
		String[] codis = params.getCodi();
		String[] tipus = params.getTipusParam();
		String[] values = params.getPar();
		Map<String, Object> parametres = new HashMap<String, Object>();
		for(int i = 0; i<codis.length; i++) {

			if(tipus[i].equals("string")){
			    parametres.put(codis[i],values[i].toString());
			}
			if(tipus[i].equals("int")){
			    parametres.put(codis[i],Long.parseLong(values[i]));
			}
			if(tipus[i].equals("float")){
			    parametres.put(codis[i],Double.parseDouble(values[i]));
			}
			if(tipus[i].equals("boolean")){
			    parametres.put(codis[i],Boolean.parseBoolean(values[i]));
			}
			if(tipus[i].equals("date")){
			    String[] dataSplit = values[i].split("/");
			    Calendar data = new GregorianCalendar();
			    data.set(Integer.parseInt(dataSplit[2]),Integer.parseInt(dataSplit[1]),Integer.parseInt(dataSplit[0]));
			    parametres.put(codis[i],data);
			}
			if(tipus[i].equals("price")){
			    String dat = values[i];
			    BigDecimal datBDecimal = new BigDecimal(new Double(dat));
			    parametres.put(codis[i], datBDecimal);
			}
		}
		try {
			return new ResponseEntity<Object>(dissenyService.consultaDomini(dominiId,params.getCodiDomini(), parametres),HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(e,HttpStatus.BAD_REQUEST);
		}
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusDominiController.class);
}
