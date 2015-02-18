package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.helper.ModalHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

/**
 * Controlador base per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseTascaController extends BaseController {

	@Autowired
	protected TascaService tascaService;
	@Autowired
	protected DissenyService dissenyService;

	protected String mostrarInformacioTascaPerPipelles(
			HttpServletRequest request,
			String tascaId,
			Model model,
			String pipellaActiva) {
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		model.addAttribute("tasca", tasca);
		
		List<Object> nomesLectura = new ArrayList<Object>();
		
		List<TascaDadaDto> dades = tascaService.findDades(tascaId);
		Iterator<TascaDadaDto> itDades = dades.iterator();
		while (itDades.hasNext()) {
			TascaDadaDto dada = itDades.next();
			if (dada.isReadOnly()) {
				nomesLectura.add(dada);
				itDades.remove();
			}
		}
		
		List<TascaDocumentDto> documents = tascaService.findDocuments(tascaId);
		Iterator<TascaDocumentDto> itDocuments = documents.iterator();
		while (itDocuments.hasNext()) {
			TascaDocumentDto document = itDocuments.next();
			if (document.isReadOnly()) {
				if (document.getId() != null)
					nomesLectura.add(document);
			}
		}
		
		model.addAttribute("nomesLectura", nomesLectura);		
		
		model.addAttribute(
				"hasFormulari",
				tascaService.hasFormulari(tascaId));
		model.addAttribute(
				"hasDocuments",
				tascaService.hasDocuments(tascaId));
		model.addAttribute(
				"hasSignatures",
				tascaService.hasSignatures(tascaId));
		if (pipellaActiva != null)
			model.addAttribute("pipellaActiva", pipellaActiva);
		else if (request.getParameter("pipellaActiva") != null)
			model.addAttribute("pipellaActiva", request.getParameter("pipellaActiva"));
		else
			model.addAttribute("pipellaActiva", "form");
		model.addAttribute(
				"isModal",
				ModalHelper.isModal(request));
		return "v3/tascaPipelles";
	}

	protected String getReturnUrl(
			HttpServletRequest request,
			Long expedientId,
			String tascaId,
			String sufix) {
		String suf = (sufix != null) ? "/" + sufix : "";
		if (ModalHelper.isModal(request))
			return "redirect:/modal/v3/expedient/" + expedientId + "/tasca/" + tascaId + suf;
		else
			return "redirect:/v3/expedient/" + expedientId + "/tasca/" + tascaId + suf;
	}
}
