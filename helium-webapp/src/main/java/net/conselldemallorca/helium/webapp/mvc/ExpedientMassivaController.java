/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dao.LuceneDao;
import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controlador per la tramitació massiva d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientMassivaController extends BaseController {

	public static final String VARIABLE_SESSIO_IDS_MASSIUS = "consultaExpedientsIdsMassius";
	public static final String VARIABLE_SESSIO_IDS_MASSIUS_TE = "consultaExpedientsIdsMassiusTE";
	private ExpedientService expedientService;
	private DissenyService dissenyService;
	private LuceneDao luceneDao;
	private ExpedientConsultaDissenyController expedientConsultaDissenyController;
	@Autowired
	public ExpedientMassivaController(
			ExpedientService expedientService,
			DissenyService dissenyService) {
		this.expedientService = expedientService;
		this.dissenyService = dissenyService;
	}

	@ModelAttribute("canviVersioProcesCommand")
	public CanviVersioProcesCommand populateCanviVersioProcesCommand() {
		return new CanviVersioProcesCommand();
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/massivaIds")
	public String consultaMassiva(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "expedientId", required = true) Long expedientId,
			@RequestParam(value = "checked", required = true) boolean checked,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			// Emmagatzema els ids d'expedient seleccionats a dins una llista.
			// El primer element de la llista és l'id del tipus d'expedient.
			if (ids == null) {
				ids = new ArrayList<Long>();
				request.getSession().setAttribute(VARIABLE_SESSIO_IDS_MASSIUS, ids);
			}
			// S'assegura que el primer element sigui l'id del tipus d'expedient
			if (ids.size() == 0) {
				ids.add(expedientTipusId);
			} else if (!expedientTipusId.equals(ids.get(0))) {
				// Si el tipus d'expedient ha canviat reinicia la llista
				ids.clear();
				ids.add(expedientTipusId);
			}
			if (checked) {
				ids.add(expedientId);
			} else {
				ids.remove(expedientId);
				if (ids.size() == 1)
					ids.clear();
			}
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/massivaIdsTE")
	public String consultaMassivaTE(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "expedientId", required = true) Long expedientId,
			@RequestParam(value = "checked", required = true) boolean checked,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			
			List<Long> ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			// Emmagatzema els ids d'expedient seleccionats a dins una llista.
			// El primer element de la llista és l'id del tipus d'expedient.
			if (ids == null) {
				ids = new ArrayList<Long>();
				request.getSession().setAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE, ids);
			}
			// S'assegura que el primer element sigui l'id del tipus d'expedient
			if (ids.size() == 0) {
				ids.add(expedientTipusId);
			} else if (!expedientTipusId.equals(ids.get(0))) {
				// Si el tipus d'expedient ha canviat reinicia la llista
				ids.clear();
				ids.add(expedientTipusId);
			}
			if (checked) {
				ids.add(expedientId);
			} else {
				ids.remove(expedientId);
				if (ids.size() == 1)
					ids.clear();
			}
		}
		return null;
	}
	


	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/massivaInfo")
	public String infoMassiva(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			if (ids == null || ids.size() == 0) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				return "redirect:/expedient/consulta.html";
			}
			List<ExpedientDto> expedients = getExpedientsMassius(
					ids.subList(1, ids.size()));
			model.addAttribute("expedients", expedients);
			if (expedients.size() > 0) {
				String piid = expedients.get(0).getProcessInstanceId();
				// Definicions de procés per al canvi de versió
				model.addAttribute(
						"definicioProces",
						dissenyService.findDefinicioProcesAmbProcessInstanceId(piid));
				// Accions per a executar
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(
						piid,
						false,
						false,
						false);
				model.addAttribute(
						"instanciaProces",
						instanciaProces);
			}
			return "/expedient/massivaInfo";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/massivaInfoTE")
	public String infoMassivaTipusExpedient(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			if (ids == null || ids.size() == 0) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				return "redirect:/expedient/consultaDisseny.html";
			}
			List<ExpedientDto> expedients = getExpedientsMassius(
					ids.subList(1, ids.size()));
			model.addAttribute("expedients", expedients);
			if (expedients.size() > 0) {
				String piid = expedients.get(0).getProcessInstanceId();
				// Definicions de procés per al canvi de versió
				model.addAttribute(
						"definicioProces",
						dissenyService.findDefinicioProcesAmbProcessInstanceId(piid));
				// Accions per a executar
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(
						piid,
						false,
						false,
						false);
				model.addAttribute(
						"instanciaProces",
						instanciaProces);
			}
			return "/expedient/massivaInfo";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
		}
	}
	

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/massivaCanviVersio")
	public String accioCanviVersio(
			HttpServletRequest request,
			@RequestParam(value="target",required=false) String target,
			@ModelAttribute("canviVersioProcesCommand") CanviVersioProcesCommand command,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = new ArrayList<Long>();
			if(request.getParameter("target").equals("disseny")){
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			}
			else{
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			}

			if (ids == null || ids.size() == 0) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				return "redirect:/expedient/consulta.html";
			}
			List<ExpedientDto> expedients = getExpedientsMassius(
					ids.subList(1, ids.size()));
			boolean error = false;
			int numOk = 0;
			for (ExpedientDto expedient: expedients) {
				try {
					DefinicioProcesDto definicioProces = dissenyService.getById(command.getDefinicioProcesId(), false);
					expedientService.changeProcessInstanceVersion(expedient.getProcessInstanceId(), definicioProces.getVersio());
					numOk++;
				} catch (Exception ex) {
					missatgeError(
			    			request,
			    			getMessage("error.expedient.accio.masiva") + " " + expedient.getIdentificador(),
			    			ex.getLocalizedMessage());
					logger.error("No s'ha pogut canviar la versió del procés " + expedient.getProcessInstanceId(), ex);
					error = true;
				}
			}
			if (numOk > 0) {
				if (!error)
					missatgeInfo(request, getMessage("info.canvi.versio.realitzat"));
				else
					missatgeInfo(request, getMessage("info.canvi.versio.realitzat.nprimers", new Object[] {new Integer(numOk)}));
			}
			if(request.getParameter("target").equals("disseny")){
				return "redirect:/expedient/massivaInfoTE.html";
			}else if(request.getParameter("target").equals("consulta")){
				return "redirect:/expedient/massivaInfo.html";
			}
			
			return "redirect:/expedient/massivaInfo.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/massivaExecutarAccio")
	public String accioExecutarAccio(
			HttpServletRequest request,
			@RequestParam(value="target2",required=false) String target2,
			@RequestParam(value = "submit", required = true) String jbpmAction,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = new ArrayList<Long>();
			if(request.getParameter("target2").equals("disseny")){
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			}
			else{
			
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			}
			if (ids == null || ids.size() == 0) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				return "redirect:/expedient/consulta.html";
			}
			List<ExpedientDto> expedients = getExpedientsMassius(
					ids.subList(1, ids.size()));
			boolean error = false;
			int numOk = 0;
			for (ExpedientDto expedient: expedients) {
				try {
					System.out.println(">>> Executant acció (codi=" + jbpmAction + ") en l'expedient " + expedient.getIdentificador());
					expedientService.executarAccio(expedient.getProcessInstanceId(), jbpmAction);
					System.out.println(">>> Acció executada (codi=" + jbpmAction + ") en l'expedient " + expedient.getIdentificador());
					numOk++;
				} catch (Exception ex) {
					missatgeError(
			    			request,
			    			getMessage("error.expedient.accio.masiva") + " " + expedient.getIdentificador(),
			    			ex.getLocalizedMessage());
					logger.error("No s'ha pogut excutar l'acció " + jbpmAction + " del procés " + expedient.getProcessInstanceId(), ex);
					error = true;
				}
			}
			if (numOk > 0) {
				if (!error)
					missatgeInfo(request, getMessage("info.accio.executat"));
				else
					missatgeInfo(request, getMessage("info.accio.executat.nprimers", new Object[] {new Integer(numOk)}));
			}
			if(request.getParameter("target2").equals("disseny")){
				return "redirect:/expedient/massivaInfoTE.html";
			}else if(request.getParameter("target2").equals("consulta")){
				return "redirect:/expedient/massivaInfo.html";
			}
			return "redirect:/expedient/massivaInfo.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
		}
	}



	private List<ExpedientDto> getExpedientsMassius(List<Long> ids) {
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		for (Long id: ids) {
			resposta.add(expedientService.getById(id));
		}
		return resposta;
	}

	private static final Log logger = LogFactory.getLog(ExpedientMassivaController.class);

}
