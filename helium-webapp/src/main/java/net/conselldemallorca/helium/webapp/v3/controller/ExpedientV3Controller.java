/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientV3Controller extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;

	@RequestMapping(value = "/{expedientId}", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		return mostrarInformacioExpedientPerPipella(
				request,
				expedientId,
				model,
				null);
	}

	@RequestMapping(value = "/{expedientId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long expedientId) {
		expedientService.delete(expedientId);
		MissatgesHelper.success(
				request,
				getMessage(
						request,
						"info.expedient.esborrat"));
			
		return "redirect:/v3/expedient/";
	}

	@RequestMapping(value = "/{expedientId}/reindexa", method = RequestMethod.GET)
	public String reindexa(
			HttpServletRequest request,
			@PathVariable Long expedientId, 
			Model model) {
		try {
			expedientService.luceneReindexarExpedient(expedientId);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"info.expedient.reindexat"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.reindexar.expedient"));
		}
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/imatgeDefProces", method = RequestMethod.GET)
	public String imatgeProces(
			HttpServletRequest request,
			@PathVariable(value = "expedientId") Long expedientId, 
			Model model) {
		ArxiuDto imatge = expedientService.getImatgeDefinicioProces(
				expedientId,
				null);
		model.addAttribute(
				ArxiuView.MODEL_ATTRIBUTE_FILENAME,
				imatge.getNom());
		model.addAttribute(
				ArxiuView.MODEL_ATTRIBUTE_DATA,
				imatge.getContingut());
		return "arxiuView";
	}
	
	@RequestMapping(value = "/{expedientId}/updateDefinicioProces/{versio}", method = RequestMethod.GET)
	@ResponseBody
	public String changeDefProc(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable int versio,
			ModelMap model) {
		String nom = null;
		try {
			nom = expedientService.canviVersioDefinicioProces(
					expedientId,
					versio);
			MissatgesHelper.success(request, getMessage(request, "info.canvi.versio.realitzat") );
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.canviar.versio.proces"));
		}
	        	
		return JSONValue.toJSONString(nom);
	}
	
	@RequestMapping(value = "/{expedientId}/buidalog", method = RequestMethod.GET)
	public String buidaLog(
			HttpServletRequest request,
			@PathVariable Long expedientId) {
		try {
			ExpedientDto expedient = expedientService.findAmbId(expedientId);
			if (expedient.isPermisAdministration()) {
				expedientService.buidarLogExpedient(expedient.getProcessInstanceId());
				MissatgesHelper.success(request, getMessage(request, "info.expedient.buidatlog"));
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.buidarlog.expedient") + ": " + ex.getLocalizedMessage());
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/desfinalitzar", method = RequestMethod.GET)
	public String desfinalitzar(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		try {
			expedientService.desfinalitzar(expedientId);
			MissatgesHelper.success(request, getMessage(request, "info.expedient.reprendre") );
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.reprendre.expedient"));
			logger.error(getMessage(request, "error.reprendre.expedient"), ex);
		}
		
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				byte[].class,
				new ByteArrayMultipartFileEditor());
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
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
				Object.class,
				new ObjectTypeEditorHelper());
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientV3Controller.class);
}
