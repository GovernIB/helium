package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.emiserv.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.DominiService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusDominiCommand;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per al manteniment de les dominis a nivell d'entorn.
 *
 */
@Controller(value = "dominiControllerV3")
@RequestMapping("/v3/domini")
public class DominiController extends BaseDissenyController {
	
	@Autowired
	private DominiService dominiService;
	@Autowired
	private DissenyService dissenyService;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	/** Accés al llistat de dominins de l'entorn des del menú de disseny. */
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			return "v3/dominiLlistat";
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:/v3";
		}
	}
	
	@RequestMapping(value="/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				dominiService.findPerDatatable(
						entornActual.getId(),
						null, // expedientTipusId
						true, // incloure globals
						paginacioParams.getFiltre(),
						paginacioParams));
	}	
	
	/** Formulari per crear un nou domini. */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request, 
			Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			ExpedientTipusDominiCommand command = new ExpedientTipusDominiCommand();
			model.addAttribute("expedientTipusDominiCommand", command);
			return "v3/expedientTipusDominiForm";
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:/v3";
		}
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request, 
			@Validated(ExpedientTipusDominiCommand.Creacio.class) ExpedientTipusDominiCommand command,
			BindingResult bindingResult, Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			try {
				if (bindingResult.hasErrors()) {
					return "v3/expedientTipusDominiForm";
				} else {
				
					EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
					
					dominiService.create(
							entornActual.getId(), 
							null, //expedientTipusId 
							conversioTipusHelper.convertir(
									command, 
									DominiDto.class));
					
		    		MissatgesHelper.success(
							request, 
							getMessage(
									request, 
									"expedient.tipus.domini.controller.creat"));
				}
			} catch (Exception ex) {
				MissatgesHelper.error(request, 
						getMessage(
								request, 
								"expedient.tipus.domini.controller.creat.error",
								new Object[] {ex.getLocalizedMessage()}));
				logger.error("No s'ha pogut guardar l'enumeració", ex);
				return "v3/expedientTipusDominiForm";
		    }
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
		}
		return modalUrlTancar(true);
	}	
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request, 
			@PathVariable Long id,
			Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			DominiDto dto = dominiService.findAmbId(null, id);
			ExpedientTipusDominiCommand command = conversioTipusHelper.convertir(dto, ExpedientTipusDominiCommand.class);
			model.addAttribute("expedientTipusDominiCommand", command);
			model.addAttribute("heretat", dto.isHeretat());
			return "v3/expedientTipusDominiForm";
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:/v3";
		}
		
	}

	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request, 
			@PathVariable Long id,
			@Validated(ExpedientTipusDominiCommand.Modificacio.class) ExpedientTipusDominiCommand command,
			BindingResult bindingResult, Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			try {
				if (bindingResult.hasErrors()) {
		    		model.addAttribute("heretat", dominiService.findAmbId(null, id).isHeretat());
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
					return modalUrlTancar(true);
				}
			} catch (Exception ex) {
				MissatgesHelper.error(request, 
						getMessage(
								request, 
								"expedient.tipus.domini.controller.creat.error",
								new Object[] {ex.getLocalizedMessage()}));
				logger.error("No s'ha pogut guardar l'enumerat: " + id, ex);
	    		model.addAttribute("heretat", dominiService.findAmbId(null, id).isHeretat());
				return "v3/expedientTipusDominiForm";
		    }
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:/v3";
		}		
	}	

	
	/** Mètode per esborrar un domini. */
	@RequestMapping(value = "/{dominiId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long dominiId,
			Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			try {
				dominiService.delete(dominiId);
				MissatgesHelper.success(request, getMessage(request, "expedient.tipus.domini.controller.eliminat"));
			}catch (ValidacioException ex) {
				MissatgesHelper.error(request, ex.getMessage());
			}
			return modalUrlTancar(false);
		} else {
			return "redirect:/v3";
		}
	}
	
	/** Mètode per provar un domini */
	@RequestMapping(value="/{dominiId}/test", method = RequestMethod.GET)
	public String test(
			HttpServletRequest request,
			@PathVariable Long dominiId,
			Model model) {
		model.addAttribute("dominiId", dominiId);
		return "v3/provaDomini";
	}
	
	/** Mètode per provar un domini */
	@RequestMapping(value="/{dominiId}/test", method = RequestMethod.POST, consumes="application/json", produces = "application/json") 
	@ResponseBody
	public ResponseEntity<Object> testS(
			HttpServletRequest request,
			@PathVariable Long dominiId,
			Model model,
			@RequestBody Cmd params,
			BindingResult bindingResult) {
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
			return new ResponseEntity<Object>(AjaxHelper.generarAjaxFormOk(dissenyService.consultaDomini(dominiId,params.getCodiDomini(), parametres)),HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(AjaxHelper.generarAjaxFormErrors(e.getLocalizedMessage(), bindingResult),HttpStatus.BAD_REQUEST);
		}
	}
	
	

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	private static final Log logger = LogFactory.getLog(DominiController.class);
}

class Cmd{
	private String codiDomini;
	private String[] codi;
	private String[] tipusParam;
	private String[] par;
	
	public Cmd() {}
	
	public Cmd(String codiDomini, String[] codi, String[] tipusParam, String[] par) {
		super();
		this.codiDomini = codiDomini;
		this.codi = codi;
		this.tipusParam = tipusParam;
		this.par = par;
	}
	public String getCodiDomini() {
		return codiDomini;
	}
	public void setCodiDomini(String codiDomini) {
		this.codiDomini = codiDomini;
	}
	public String[] getCodi() {
		return codi;
	}
	public void setCodi(String[] codi) {
		this.codi = codi;
	}
	public String[] getTipusParam() {
		return tipusParam;
	}
	public void setTipusParam(String[] tipusParam) {
		this.tipusParam = tipusParam;
	}
	public String[] getPar() {
		return par;
	}
	public void setPar(String[] par) {
		this.par = par;
	}
	
	
}
