package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador base per a les pestanyes del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseExpedientTipusController extends BaseController {
	
	@Autowired
	protected ExpedientTipusService expedientTipusService;
	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	protected DissenyService dissenyService;
	@Autowired
	protected ExpedientTipusHelper expedientTipusHelper;

	protected String mostrarInformacioExpedientTipusPerPipelles(
			HttpServletRequest request,
			Long expedientTipusId,
			Model model,
			String pipellaActiva) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPerDissenyar(
				entornActual.getId(),
				expedientTipusId);
		model.addAttribute("expedientTipus", expedientTipus);
		if (pipellaActiva != null) {
			model.addAttribute("pipellaActiva", pipellaActiva);
		} else if (request.getParameter("pipellaActiva") != null)
			model.addAttribute("pipellaActiva", request.getParameter("pipellaActiva"));
		else
			model.addAttribute("pipellaActiva", "dades");
		// Responsable per defecte
		if (expedientTipus.getResponsableDefecteCodi() != null) {
			model.addAttribute(
					"responsableDefecte",
					aplicacioService.findPersonaAmbCodi(
							expedientTipus.getResponsableDefecteCodi()));
		}
		model.addAttribute(
				"definicioProcesInicial",
				dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId));
		return "v3/expedientTipusInfo";
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
}
