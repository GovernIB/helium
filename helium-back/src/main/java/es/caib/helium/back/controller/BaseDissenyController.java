package es.caib.helium.back.controller;

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

import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.ObjectTypeEditorHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.commons.dto.DefinicioProcesDto;
import es.caib.helium.commons.dto.DefinicioProcesExpedientDto;
import es.caib.helium.commons.dto.DefinicioProcesExpedientDto.IdAmbEtiqueta;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.ExpedientTipusDto;
import es.caib.helium.commons.dto.ParellaCodiValorDto;
import es.caib.helium.logic.intf.service.DefinicioProcesService;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;

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
		
		return "expedientTipusPipelles";
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
			if (definicioProcesId != null) {
				definicioProces = definicioProcesService.findAmbIdPermisDissenyarDelegat(entornActual.getId(), definicioProcesId);
			}

			if (definicioProces == null) {
				MissatgesHelper.error(
						request, 
						getMessage(request, 
								"definicio.proces.pipelles.definicio.no.trobada", 
								new Object[] {jbmpKey}));
				return "redirect:/definicioProces";			
			}		

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
					
			return "definicioProcesPipelles";
			
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
