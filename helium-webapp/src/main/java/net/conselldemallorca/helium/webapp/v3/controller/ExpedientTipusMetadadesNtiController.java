/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusMetadadesNtiCommand;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper.AjaxFormResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pestanya de d'integració amb metadades
 * nti del manteniment dels tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusMetadadesNtiController extends BaseExpedientTipusController {

	@Autowired
	private ExpedientHelper expedientHelper;
	
	@RequestMapping(value = "/{expedientTipusId}/metadadesNti")
	public String nti(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"consultes");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			ExpedientTipusMetadadesNtiCommand command = ExpedientTipusMetadadesNtiCommand.toCommand(
					expedientTipus);
			model.addAttribute("expedientTipusMetadadesNtiCommand", command);
			this.afegirDadesUnitatOrganitzativa(request, model, expedientTipus.getNtiOrgano());
		}
		return "v3/expedientTipusMetadadesNti";
	}
	
	/** Mètode per comprovar l'estat de la UO i afegir informació per a que es pinti al model. 
	 * @param request */
	private void afegirDadesUnitatOrganitzativa(HttpServletRequest request, Model model, String codiUo) {
		// Cercar UO, afegir info al model.
		String unitatOrganitzativaError = null;
		UnitatOrganitzativaDto uo = null;
		if (codiUo != null) {
			uo = unitatOrganitzativaService.findByCodi(codiUo);
			if (uo != null) {
				if (!"V".equals(uo.getEstat())) {
					String estat = getMessage(request, "expedient.tipus.metadades.nti.unitat.organitzativa.estat." + uo.getEstat());
					unitatOrganitzativaError = getMessage(request, "expedient.tipus.metadades.nti.unitat.organitzativa.no.vigent", new Object[] {codiUo, estat});
					// consulta les unitats històriques per la UO
					uo = unitatOrganitzativaService.getLastHistoricos(uo);
				}
				model.addAttribute("unitatOrganitzativaTipusTransicio", uo.getTipusTransicio());
			} else {
				unitatOrganitzativaError = getMessage(request, "expedient.tipus.metadades.nti.unitat.organitzativa.no.trobada", new Object[] {codiUo});
			}
		}
		model.addAttribute("unitatOrganitzativa", uo);
		model.addAttribute("unitatOrganitzativaError", unitatOrganitzativaError);
		model.addAttribute("codiUo", codiUo);
	}


	@RequestMapping(value = "/{expedientTipusId}/metadadesNti", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse ntiPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusMetadadesNtiCommand.Modificacio.class) ExpedientTipusMetadadesNtiCommand command,
			BindingResult bindingResult) throws PermisDenegatException, IOException {
		AjaxFormResponse response = AjaxHelper.generarAjaxFormOk();
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null) {
			return response;
		}
		if (command.isActiu()) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			String missatgeError = null;
						
			if (!command.isProcedimentComu() && (command.getOrgano() == null || "".equals(command.getOrgano().trim()))) {
				bindingResult.rejectValue("organo", "NotEmpty");
			}
			if (!expedientTipus.isNtiActiu() && !command.isProcedimentComu() && command.getOrgano() != null) {
				Long expedientsExistents = expedientHelper.countByEntornIdAndTipus(expedientTipusId);
				if(expedientsExistents>0) {
					bindingResult.rejectValue("procedimentComu", "error.exist.exp.tipexp.no.procediment.comu");
					missatgeError = "error.exist.exp.tipexp.no.procediment.comu";
				}
			}
			if (command.getClasificacion() == null || "".equals(command.getClasificacion().trim())) {
				bindingResult.rejectValue("clasificacion", "NotEmpty");
			}
			if(!expedientTipus.isProcedimentComu() && command.isProcedimentComu()) {
				Long expedientsExistents = expedientHelper.countByEntornIdAndTipus(expedientTipusId);
				if(expedientsExistents>0) {;
					bindingResult.rejectValue("procedimentComu", "error.exist.exp.tipexp.no.procediment.comu");
					missatgeError = "error.exist.exp.tipexp.no.procediment.comu";
				} else {
					command.setOrgano(null);
				}
			}
			if (bindingResult.hasErrors()) {
		        MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								missatgeError!=null ? missatgeError : "expedient.tipus.metadades.nti.validacio"));
				response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
			} else {
				expedientTipusService.updateMetadadesNti(
						entornActual.getId(),
						expedientTipusId,
						command.isActiu(),
						command.getOrgano(), 
						command.getClasificacion(),
						command.getSerieDocumental(),
						command.isArxiuActiu(),
						command.isProcedimentComu());
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.metadades.nti.controller.guardat"));
			}
		} else {
			if (command.isProcedimentComu()) {
				String missatgeError = "error.exist.exp.tipexp.marcar.procediment.no.comu";
				bindingResult.rejectValue("procedimentComu", missatgeError);
				MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								missatgeError!=null ? missatgeError : "expedient.tipus.metadades.nti.validacio"));
				response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
			} else {
				expedientTipusService.updateMetadadesNti(
						entornActual.getId(),
						expedientTipusId,
						command.isActiu(),
						command.getOrgano(), 
						command.getClasificacion(),
						command.getSerieDocumental(),
						command.isArxiuActiu(),
						command.isProcedimentComu());
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.metadades.nti.controller.guardat"));
			}
		}
    	return response;
	}
	
	@RequestMapping(value = "/{expedientTipusId}/comprovarOrgan/{codiUo}", method = RequestMethod.GET)
	
	public String comprovarOrgan(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable String codiUo,
			Model model) {

		this.afegirDadesUnitatOrganitzativa(request, model, codiUo);

    	return "v3/unitatOrganitzativaComprovar";
	}	
}
