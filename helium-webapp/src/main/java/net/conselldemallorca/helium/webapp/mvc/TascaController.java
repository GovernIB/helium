/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.jbpm3.integracio.ValidationException;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.dto.TascaLlistatDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.model.service.TerminiService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador per la gestió de tasques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class TascaController extends BaseController {

	private String TAG_FORM_INICI = "<!--helium:form-inici-->";
	private String TAG_FORM_FI = "<!--helium:form-fi-->";

	private TascaService tascaService;
	private TerminiService terminiService;
	private DissenyService dissenyService;
	private PermissionService permissionService;


	@Autowired
	public TascaController(
			TascaService tascaService,
			TerminiService terminiService,
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.tascaService = tascaService;
		this.terminiService = terminiService;
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}
	
	@ModelAttribute("prioritats")
	public List<HashMap<String, Object>> populateEstats(HttpServletRequest request) {
		List<HashMap<String, Object>> resposta = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> moltAlta = new HashMap<String, Object>();
		moltAlta.put("value", 2);
		moltAlta.put("label", getMessage("txt.m_alta") );
		resposta.add(moltAlta);
		HashMap<String, Object> alta = new HashMap<String, Object>();
		alta.put("value", 1);
		alta.put("label", getMessage("txt.alta") );
		resposta.add(alta);
		HashMap<String, Object> normal = new HashMap<String, Object>();
		normal.put("value", 0);
		normal.put("label", getMessage("txt.normal") );
		resposta.add(normal);
		HashMap<String, Object> baixa = new HashMap<String, Object>();
		baixa.put("value", -1);
		baixa.put("label", getMessage("txt.baixa") );
		resposta.add(baixa);
		HashMap<String, Object> moltBaixa = new HashMap<String, Object>();
		moltBaixa.put("value", -2);
		moltBaixa.put("label", getMessage("txt.m_baixa") );
		resposta.add(moltBaixa);
		return resposta;
	}

	@ModelAttribute("commandPersonaFiltre")
	public TascaPersonaFiltreCommand populateCommandPersonaFiltre(HttpServletRequest request) {
		Object tascaPersonaFiltreCommand = request.getSession().getAttribute("commandTascaPersonaFiltre");
		if (tascaPersonaFiltreCommand != null)
			return (TascaPersonaFiltreCommand)tascaPersonaFiltreCommand;
		else
			return new TascaPersonaFiltreCommand();
	}
	
	@ModelAttribute("commandGrupFiltre")
	public TascaGrupFiltreCommand populateCommandGrupFiltre(HttpServletRequest request) {
		Object tascaGrupFiltreCommand = request.getSession().getAttribute("commandTascaGrupFiltre");
		if (tascaGrupFiltreCommand != null)
			return (TascaGrupFiltreCommand)tascaGrupFiltreCommand;
		else
			return new TascaGrupFiltreCommand();
	}
	
	@RequestMapping(value = "/tasca/personaLlistat", method = RequestMethod.GET)
	public String personaLlistat(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			String paramColumna = new ParamEncoder("registre").encodeParameterName(TableTagParameters.PARAMETER_SORT);
			String columna = request.getParameter(paramColumna);
			columna = ((columna!=null)&&(!columna.equals("")))?columna:"0";
			String paramOrdre = new ParamEncoder("registre").encodeParameterName(TableTagParameters.PARAMETER_ORDER);
			String ordre = request.getParameter(paramOrdre);	// Descending is 2, Ascending is 1.
			ordre = ((ordre!=null)&&(!ordre.equals("")))?ordre:"0";
			
			TascaPersonaFiltreCommand tascaPersonaFiltreCommand = (TascaPersonaFiltreCommand)model.get("commandPersonaFiltre");
			String expedient = request.getParameter("exp");
			if (expedient != null) tascaPersonaFiltreCommand.setExpedient(expedient);
			
			if (!ordre.equals("0")) {
				tascaPersonaFiltreCommand.setColumna(columna);
				tascaPersonaFiltreCommand.setOrdre(ordre);
				request.getSession().setAttribute("commandTascaPersonaFiltre", tascaPersonaFiltreCommand);
			}
			List<TascaLlistatDto> tasquesPersonalsFiltre = tascaService.findTasquesPersonalsFiltre(
					entorn.getId(),
					tascaPersonaFiltreCommand.getNom(),
					tascaPersonaFiltreCommand.getExpedient(),
					tascaPersonaFiltreCommand.getTipusExpedient(),
					tascaPersonaFiltreCommand.getDataCreacioInici(),
					tascaPersonaFiltreCommand.getDataCreacioFi(),
					tascaPersonaFiltreCommand.getPrioritat(),
					tascaPersonaFiltreCommand.getDataLimitInici(),
					tascaPersonaFiltreCommand.getDataLimitFi(),
					tascaPersonaFiltreCommand.getColumna(),
					tascaPersonaFiltreCommand.getOrdre());
			model.addAttribute("terminisIniciats", findTerminisIniciatsPerTasques(tasquesPersonalsFiltre));
			model.addAttribute("personaLlistat", tasquesPersonalsFiltre);
			model.addAttribute("personaLlistatAll", tascaService.getTotalTasquesPersona(entorn.getId()));
			model.addAttribute("grupLlistatAll", tascaService.getTotalTasquesGrup(entorn.getId()));
			model.addAttribute("command", tascaPersonaFiltreCommand);
			model.addAttribute("tipusExp", llistatExpedientTipusAmbPermisos(entorn));
			return "tasca/personaLlistat";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	@RequestMapping(value = "/tasca/personaLlistat", method = RequestMethod.POST)
	public String personaLlistatPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("commandPersonaFiltre") TascaPersonaFiltreCommand command) {
		if ("submit".equals(submit)) {
			request.getSession().setAttribute("commandTascaPersonaFiltre", command);
		} else if ("clean".equals(submit)) {
			request.getSession().removeAttribute("commandTascaPersonaFiltre");
		}
		return "redirect:/tasca/personaLlistat.html";
	}

	@RequestMapping(value = "/tasca/grupLlistat", method = RequestMethod.GET)
	public String grupLlistat(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute("personaLlistatAll", tascaService.getTotalTasquesPersona(entorn.getId()));
			
			String paramColumna = new ParamEncoder("registre").encodeParameterName(TableTagParameters.PARAMETER_SORT);
			String columna = request.getParameter(paramColumna);
			columna = ((columna!=null)&&(!columna.equals("")))?columna:"0";
			String paramOrdre = new ParamEncoder("registre").encodeParameterName(TableTagParameters.PARAMETER_ORDER);
			String ordre = request.getParameter(paramOrdre);	// Descending is 2, Ascending is 1.
			ordre = ((ordre!=null)&&(!ordre.equals("")))?ordre:"0";
			
			TascaGrupFiltreCommand tascaGrupFiltreCommand = (TascaGrupFiltreCommand)model.get("commandGrupFiltre");
			if (!ordre.equals("0")) {
				tascaGrupFiltreCommand.setColumna(columna);
				tascaGrupFiltreCommand.setOrdre(ordre);
				request.getSession().setAttribute("commandTascaGrupFiltre", tascaGrupFiltreCommand);
			}
			List<TascaLlistatDto> tasquesGrupFiltre = tascaService.findTasquesGrupFiltre(
					entorn.getId(),
					tascaGrupFiltreCommand.getNom(),
					tascaGrupFiltreCommand.getExpedient(),
					tascaGrupFiltreCommand.getTipusExpedient(),
					tascaGrupFiltreCommand.getDataCreacioInici(),
					tascaGrupFiltreCommand.getDataCreacioFi(),
					tascaGrupFiltreCommand.getPrioritat(),
					tascaGrupFiltreCommand.getDataLimitInici(),
					tascaGrupFiltreCommand.getDataLimitFi(),
					tascaGrupFiltreCommand.getColumna(),
					tascaGrupFiltreCommand.getOrdre());
			model.addAttribute("terminisIniciats", findTerminisIniciatsPerTasques(tasquesGrupFiltre));
			model.addAttribute("grupLlistat", tasquesGrupFiltre);
			model.addAttribute("grupLlistatAll", tascaService.getTotalTasquesGrup(entorn.getId()));
			
			model.addAttribute("command", tascaGrupFiltreCommand);
			model.addAttribute("tipusExp", llistatExpedientTipusAmbPermisos(entorn));
			
			return "tasca/grupLlistat";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	@RequestMapping(value = "/tasca/grupLlistat", method = RequestMethod.POST)
	public String grupLlistatPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("commandGrupFiltre") TascaGrupFiltreCommand command) {
		if ("submit".equals(submit)) {
			request.getSession().setAttribute("commandTascaGrupFiltre", command);
		} else if ("clean".equals(submit)) {
			request.getSession().removeAttribute("commandTascaGrupFiltre");
		}
		return "redirect:/tasca/grupLlistat.html";
	}

	@RequestMapping(value = "/tasca/info")
	public String info(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			TascaDelegacioCommand command = new TascaDelegacioCommand();
			command.setTaskId(id);
			model.addAttribute(
					"command",
					command);
			try {
				model.addAttribute(
						"tasca",
						tascaService.getById(entorn.getId(), id));
			} catch (Exception ex) {
				logger.error("S'ha produït un error processant la seva petició", ex);
				missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
				return "redirect:/tasca/personaLlistat.html";
			}
			return "tasca/info";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/agafar")
	public String agafar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				tascaService.agafar(entorn.getId(), id);
				missatgeInfo(request, getMessage("info.tasca.disponible.personals") );
				return "redirect:/tasca/info.html?id=" + id;
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut agafar la tasca", ex);
	        	return "redirect:/tasca/grupLlistat.html";
	        }
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/completar")
	public String completar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "pipella", required = false) String pipella,
			@RequestParam(value = "submit", required = false) String submit,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			TascaDto tasca = tascaService.getById(entorn.getId(), id);
			try {
				boolean found = false;
				for (String outcome: tasca.getOutcomes()) {
					if (outcome != null && outcome.equals(submit)) {
						tascaService.completar(entorn.getId(), id, true, null, outcome);
						found = true;
						break;
					}
				}
				if (!found) {
					tascaService.completar(entorn.getId(), id, true);
				}
				missatgeInfo(request, getMessage("info.tasca.completat") );
				return "redirect:/tasca/personaLlistat.html";
			} catch (Exception ex) {
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					missatgeError(
		        			request,
		        			ex.getCause().getMessage());
				} else {
					missatgeError(
		        			request,
		        			getMessage("error.finalitzar.tasca"),
		        			(ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage());
					logger.error("No s'ha pogut finalitzar la tasca", ex);
				}
	        	if ("info".equals(pipella)) {
	        		return "redirect:/tasca/info.html?id=" + tasca.getId();
	        	} else if ("form".equals(pipella)) {
	        		return "redirect:/tasca/form.html?id=" + tasca.getId();
	        	} else if ("documents".equals(pipella)) {
	        		return "redirect:/tasca/documents.html?id=" + tasca.getId();
	        	} else if ("signatures".equals(pipella)) {
	        		return "redirect:/tasca/signatures.html?id=" + tasca.getId();
	        	} else {
	        		return "redirect:/tasca/info.html?id=" + tasca.getId();
	        	}
	        }
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/formRecurs")
	public String formRecurs(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				TascaDto tasca = tascaService.getById(entorn.getId(), id);
				byte[] contingut = dissenyService.getDeploymentResource(
						tasca.getDefinicioProces().getId(),
						tasca.getRecursForm());
				String text = textFormRecursProcessat(tasca, new String(contingut, "UTF-8"));
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_FILENAME,
						tasca.getRecursForm().substring(tasca.getRecursForm().lastIndexOf("/") + 1));
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_DATA,
						text.getBytes());
				return "arxiuView";
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
				logger.error("No s'ha pogut mostrar l'arxiu", ex);
				return "redirect:/tasca/info.html?id=" + id;
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/executarAccio")
	public String executarAccio(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "accio", required = true) String accio,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				tascaService.executarAccio(
						entorn.getId(),
						id,
						accio);
				missatgeInfo(request, getMessage("info.accio.executat") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
				logger.error("No s'ha pogut mostrar l'arxiu", ex);
			}
			return "redirect:/tasca/form.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	private String textFormRecursProcessat(TascaDto tasca, String text) {
		int indexFormInici = text.indexOf(TAG_FORM_INICI);
		int indexFormFi = text.indexOf(TAG_FORM_FI);
		if (indexFormInici != -1 && indexFormFi != -1) {
			return text.substring(
					indexFormInici + TAG_FORM_INICI.length(),
					indexFormFi);
		}
		return null;
	}
	
	private List<ExpedientTipus> llistatExpedientTipusAmbPermisos(Entorn entorn) {
		List<ExpedientTipus> resposta = new ArrayList<ExpedientTipus>();
		List<ExpedientTipus> llistat = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
		for (ExpedientTipus expedientTipus: llistat) {
			if (potDissenyarExpedientTipus(entorn, expedientTipus))
				resposta.add(expedientTipus);
		}
		return resposta;
	}
	
	private boolean potDissenyarExpedientTipus(Entorn entorn, ExpedientTipus expedientTipus) {
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}
	
	public class TascaPersonaFiltreCommand {
		private String nom;
		private String expedient;
		private Long tipusExpedient;
		private Date dataCreacioInici;
		private Date dataCreacioFi;
		private Integer prioritat;
		private Date dataLimitInici;
		private Date dataLimitFi;
		private String columna = "3";
		private String ordre = "2";
		public TascaPersonaFiltreCommand() {}
		public String getNom() {
			return nom;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
		public String getExpedient() {
			return expedient;
		}
		public void setExpedient(String expedient) {
			this.expedient = expedient;
		}
		public Long getTipusExpedient() {
			return tipusExpedient;
		}
		public void setTipusExpedient(Long tipusExpedient) {
			this.tipusExpedient = tipusExpedient;
		}
		public Date getDataCreacioInici() {
			return dataCreacioInici;
		}
		public void setDataCreacioInici(Date dataCreacioInici) {
			this.dataCreacioInici = dataCreacioInici;
		}
		public Date getDataCreacioFi() {
			return dataCreacioFi;
		}
		public void setDataCreacioFi(Date dataCreacioFi) {
			this.dataCreacioFi = dataCreacioFi;
		}
		public Integer getPrioritat() {
			return prioritat;
		}
		public void setPrioritat(Integer prioritat) {
			this.prioritat = prioritat;
		}
		public Date getDataLimitInici() {
			return dataLimitInici;
		}
		public void setDataLimitInici(Date dataLimitInici) {
			this.dataLimitInici = dataLimitInici;
		}
		public Date getDataLimitFi() {
			return dataLimitFi;
		}
		public void setDataLimitFi(Date dataLimitFi) {
			this.dataLimitFi = dataLimitFi;
		}
		public String getColumna() {
			return columna;
		}
		public void setColumna(String columna) {
			this.columna = columna;
		}
		public String getOrdre() {
			return ordre;
		}
		public void setOrdre(String ordre) {
			this.ordre = ordre;
		}
	}
	
	public class TascaGrupFiltreCommand {
		private String nom;
		private String expedient;
		private Long tipusExpedient;
		private Date dataCreacioInici;
		private Date dataCreacioFi;
		private Integer prioritat;
		private Date dataLimitInici;
		private Date dataLimitFi;
		private String columna = "3";
		private String ordre = "2";
		public TascaGrupFiltreCommand() {}
		public String getNom() {
			return nom;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
		public String getExpedient() {
			return expedient;
		}
		public void setExpedient(String expedient) {
			this.expedient = expedient;
		}
		public Long getTipusExpedient() {
			return tipusExpedient;
		}
		public void setTipusExpedient(Long tipusExpedient) {
			this.tipusExpedient = tipusExpedient;
		}
		public Date getDataCreacioInici() {
			return dataCreacioInici;
		}
		public void setDataCreacioInici(Date dataCreacioInici) {
			this.dataCreacioInici = dataCreacioInici;
		}
		public Date getDataCreacioFi() {
			return dataCreacioFi;
		}
		public void setDataCreacioFi(Date dataCreacioFi) {
			this.dataCreacioFi = dataCreacioFi;
		}
		public Integer getPrioritat() {
			return prioritat;
		}
		public void setPrioritat(Integer prioritat) {
			this.prioritat = prioritat;
		}
		public Date getDataLimitInici() {
			return dataLimitInici;
		}
		public void setDataLimitInici(Date dataLimitInici) {
			this.dataLimitInici = dataLimitInici;
		}
		public Date getDataLimitFi() {
			return dataLimitFi;
		}
		public void setDataLimitFi(Date dataLimitFi) {
			this.dataLimitFi = dataLimitFi;
		}
		public String getColumna() {
			return columna;
		}
		public void setColumna(String columna) {
			this.columna = columna;
		}
		public String getOrdre() {
			return ordre;
		}
		public void setOrdre(String ordre) {
			this.ordre = ordre;
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}



	private List<TerminiIniciat> findTerminisIniciatsPerTasques(List<TascaLlistatDto> tasques) {
		List<TerminiIniciat> resposta = new ArrayList<TerminiIniciat>();
		if (tasques != null) {
			String[] taskInstanceIds = new String[tasques.size()];
			int i = 0;
			for (TascaLlistatDto tasca: tasques)
				taskInstanceIds[i++] = tasca.getId();
			List<TerminiIniciat> terminis = terminiService.findIniciatsAmbTaskInstanceIds(taskInstanceIds);
			for (TascaLlistatDto tasca: tasques) {
				boolean found = false;
				for (TerminiIniciat termini: terminis) {
					if (termini.getTaskInstanceId().equals(tasca.getId())) {
						resposta.add(termini);
						found = true;
						break;
					}
				}
				if (!found)
					resposta.add(null);
			}
		}
		return resposta;
	}

	private static final Log logger = LogFactory.getLog(TascaController.class);

}
