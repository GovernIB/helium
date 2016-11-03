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
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.service.AccioService;
import net.conselldemallorca.helium.v3.core.api.service.CampService;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador base per a les pestanyes de la definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseDefinicioProcesController extends BaseController {
	
	@Autowired
	protected DefinicioProcesService definicioProcesService;
	@Autowired
	protected DissenyService dissenyService;
	@Autowired
	protected CampService campService;
	@Autowired
	protected DocumentService documentService;
	@Autowired
	protected AccioService accioService;
	@Autowired
	protected TerminiService terminiService;
	
	protected String mostrarInformacioDefinicioProcesPerPipelles(
			HttpServletRequest request,
			String jbmpKey,
			Model model,
			String pipellaActiva) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto definicioProces = null;
		if (entornActual != null) {
			definicioProces = definicioProcesService.findByEntornIdAndJbpmKey(
					entornActual.getId(),
					jbmpKey);
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
		}
		
		if (pipellaActiva != null)
			model.addAttribute("pipellaActiva", pipellaActiva);
		else if (request.getParameter("pipellaActiva") != null)
			model.addAttribute("pipellaActiva", request.getParameter("pipellaActiva"));
		else
			model.addAttribute("pipellaActiva", "detall");		
				
		return "v3/definicioProcesPipelles";
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
