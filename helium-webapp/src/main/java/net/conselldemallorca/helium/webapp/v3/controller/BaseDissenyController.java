package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto.IdAmbEtiqueta;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador base per als diferents controladors del disseny per al manteniment
 * de tipus d'expedient o definicions de procés.
 * 
 */
public class BaseDissenyController extends BaseController {
	
	@Autowired
	protected ExpedientTipusService expedientTipusService;
	@Autowired
	protected DissenyService dissenyService;
	@Autowired
	protected DefinicioProcesService definicioProcesService;

	protected String mostrarInformacioExpedientTipusPerPipelles(
			HttpServletRequest request,
			Long expedientTipusId,
			Model model,
			String pipellaActiva) {
		
		if (pipellaActiva != null)
			model.addAttribute("pipellaActiva", pipellaActiva);
		else if (request.getParameter("pipellaActiva") != null)
			model.addAttribute("pipellaActiva", request.getParameter("pipellaActiva"));
		else
			model.addAttribute("pipellaActiva", "informacio");
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
		}
		
		return "v3/expedientTipusPipelles";
	}
	
	protected String mostrarInformacioDefinicioProcesPerPipelles(
			HttpServletRequest request,
			String jbmpKey,
			Long definicioProcesId,
			Model model,
			String pipellaActiva) {
				
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();

		DefinicioProcesDto definicioProces = null;
		if (entornActual != null) {
			if (definicioProcesId != null)
				definicioProces = definicioProcesService.findAmbIdAndEntorn(entornActual.getId(), definicioProcesId);
			else
				definicioProces = definicioProcesService.findByEntornIdAndJbpmKey(entornActual.getId(), jbmpKey);
			
			// Comprova si pot dissenyar la definició de procés
			if (!potDissenyarDefinicioProces(request, definicioProces)) {
					MissatgesHelper.error(request, getMessage(request, "error.permisos.disseny.defproc"));
					return "redirect:/";
			}
			// Recupera el tipus d'expedient amb els permisos
			if (definicioProces.getExpedientTipus() != null)
				definicioProces.setExpedientTipus(
						expedientTipusService.findAmbIdPermisDissenyarDelegat(
								entornActual.getId(),
								definicioProces.getExpedientTipus().getId()));
			
			model.addAttribute("definicioProces", definicioProces);
			
			// Select de les versions
			if (definicioProces != null) {
				DefinicioProcesExpedientDto d = dissenyService.getDefinicioProcesByEntorIdAndProcesId(
						entornActual.getId(), 
						definicioProces.getId());
				List<ParellaCodiValorDto> versions = new ArrayList<ParellaCodiValorDto>();
				for (IdAmbEtiqueta i : d.getListIdAmbEtiqueta()) {
					versions.add(new ParellaCodiValorDto(i.getId().toString(), i.getEtiqueta()));
				}
				model.addAttribute("versions", versions);
			}

			if (pipellaActiva != null)
				model.addAttribute("pipellaActiva", pipellaActiva);
			else if (request.getParameter("pipellaActiva") != null)
				model.addAttribute("pipellaActiva", request.getParameter("pipellaActiva"));
			else
				model.addAttribute("pipellaActiva", "detall");		
					
			return "v3/definicioProcesPipelles";
			
		} else {
			return "redirect:/";
		}
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
	
	private boolean potDissenyarDefinicioProces(
			HttpServletRequest request,
			DefinicioProcesDto definicioProces) {
		
		boolean ret = true;
		if (!SessionHelper.getSessionManager(request).getPotDissenyarEntorn())
			if (definicioProces.getExpedientTipus() == null 
			|| !SessionHelper.getSessionManager(request).getPotDissenyarExpedientTipus()) {
				ret = false;
			}
		return ret;
	}
}
