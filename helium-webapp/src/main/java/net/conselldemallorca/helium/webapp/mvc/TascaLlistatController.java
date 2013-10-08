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

import net.conselldemallorca.helium.core.model.dto.PaginaLlistatDto;
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
import net.conselldemallorca.helium.webapp.mvc.util.PaginatedList;

import org.displaytag.properties.SortOrderEnum;
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
 * Controlador per a mostrar els llistats de tasques pendents
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class TascaLlistatController extends BaseController {
	
	private static final String SESSION_ATTRIBUTE_CURRENT_SORT_PERSONA = "TascaLlistatController.persona.current.sort";
	private static final String SESSION_ATTRIBUTE_CURRENT_DIR_PERSONA = "TascaLlistatController.persona.current.dir";
	private static final String SESSION_ATTRIBUTE_CURRENT_SORT_GRUP = "TascaLlistatController.grup.current.sort";
	private static final String SESSION_ATTRIBUTE_CURRENT_DIR_GRUP = "TascaLlistatController.grup.current.dir";
	
	private TascaService tascaService;
	private TerminiService terminiService;
	private DissenyService dissenyService;
	private PermissionService permissionService;

	@Autowired
	public TascaLlistatController(
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
	public TascaFiltreCommand populateCommandPersonaFiltre(HttpServletRequest request) {
		Object tascaPersonaFiltreCommand = request.getSession().getAttribute("commandTascaPersonaFiltre");
		if (tascaPersonaFiltreCommand != null)
			return (TascaFiltreCommand)tascaPersonaFiltreCommand;
		else
			return new TascaFiltreCommand();
	}

	@ModelAttribute("commandGrupFiltre")
	public TascaFiltreCommand populateCommandGrupFiltre(HttpServletRequest request) {
		Object tascaGrupFiltreCommand = request.getSession().getAttribute("commandTascaGrupFiltre");
		if (tascaGrupFiltreCommand != null)
			return (TascaFiltreCommand)tascaGrupFiltreCommand;
		else
			return new TascaFiltreCommand();
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/tasca/personaLlistat", method = RequestMethod.GET)
	public String personaLlistatGet(
			HttpServletRequest request,
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "dir", required = false) String dir,
			@RequestParam(value = "objectsPerPage", required = false) String objectsPerPage,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			String sortCalculat = sort;
			if (sort != null && sort.length() > 0) {
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_CURRENT_SORT_PERSONA,
						sort);
			} else {
				String sortSessio = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_CURRENT_SORT_PERSONA);
				if (sortSessio != null)
					sortCalculat = sortSessio;
			}
			
			String dirCalculat = dir;
			if (dir != null && dir.length() > 0) {
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_CURRENT_DIR_PERSONA,
						dir);
			} else {
				String dirSessio = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_CURRENT_DIR_PERSONA);
				if (dirSessio != null)
					dirCalculat = dirSessio;
			}
			TascaFiltreCommand tascaPersonaFiltreCommand = (TascaFiltreCommand)model.get("commandPersonaFiltre");
			request.getSession().setAttribute("commandTascaPersonaFiltre", tascaPersonaFiltreCommand);
			String expedient = request.getParameter("exp");
			try{
				byte[] exped = expedient.getBytes("ISO-8859-1");
				String expeNT = new String(exped);
				if (expedient != null) tascaPersonaFiltreCommand.setExpedient(expeNT);
			}catch(Exception e){
				e.getStackTrace();
			}
			PaginatedList pagina = getPaginaTasquesPersonals(
					entorn,
					tascaPersonaFiltreCommand,
					page,
					sortCalculat,
					dirCalculat,
					objectsPerPage);
			model.addAttribute("personaLlistat", pagina);
			model.addAttribute("personaLlistatCount", pagina.getFullListSize());
			model.addAttribute(
					"grupLlistatCount",
					tascaService.countTasquesGrupEntorn(
							entorn.getId(),
							null));
			model.addAttribute(
					"terminisIniciats",
					findTerminisIniciatsPerTasques(
							(List<TascaLlistatDto>)pagina.getList()));
			model.addAttribute("command", tascaPersonaFiltreCommand);
			model.addAttribute("tipusExp", llistatExpedientTipusAmbPermisos(entorn));
			
			// Asignamos los valores del filtro a la pestaña de grup
			model.addAttribute("grupLlistatCount", getPaginaTasquesGrup(entorn,tascaPersonaFiltreCommand,null,null,null,objectsPerPage).getFullListSize());
			request.getSession().setAttribute("commandTascaGrupFiltre", tascaPersonaFiltreCommand);
			
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
			@ModelAttribute("commandPersonaFiltre") TascaFiltreCommand command) {
		if ("submit".equals(submit)) {
			request.getSession().setAttribute("commandTascaPersonaFiltre", command);
		} else if ("clean".equals(submit)) {
			request.getSession().removeAttribute("commandTascaPersonaFiltre");
		}
		return "redirect:/tasca/personaLlistat.html";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/tasca/grupLlistat", method = RequestMethod.GET)
	public String grupLlistatGet(
			HttpServletRequest request,
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "dir", required = false) String dir,
			@RequestParam(value = "objectsPerPage", required = false) String objectsPerPage,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			String sortCalculat = sort;
			if (sort != null && sort.length() > 0) {
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_CURRENT_SORT_GRUP,
						sort);
			} else {
				String sortSessio = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_CURRENT_SORT_GRUP);
				if (sortSessio != null)
					sortCalculat = sortSessio;
			}
			
			String dirCalculat = dir;
			if (dir != null && dir.length() > 0) {
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_CURRENT_DIR_GRUP,
						dir);
			} else {
				String dirSessio = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_CURRENT_DIR_GRUP);
				if (dirSessio != null)
					dirCalculat = dirSessio;
			}
			TascaFiltreCommand tascaGrupFiltreCommand = (TascaFiltreCommand)model.get("commandGrupFiltre");
			request.getSession().setAttribute("commandTascaGrupFiltre", tascaGrupFiltreCommand);
			PaginatedList pagina = getPaginaTasquesGrup(
					entorn,
					tascaGrupFiltreCommand,
					page,
					sortCalculat,
					dirCalculat,
					objectsPerPage);
			model.addAttribute("grupLlistat", pagina);
			model.addAttribute("grupLlistatCount", pagina.getFullListSize());
			model.addAttribute(
					"personaLlistatCount",
					tascaService.countTasquesPersonalsEntorn(
							entorn.getId(),
							null));
			model.addAttribute(
					"terminisIniciats",
					findTerminisIniciatsPerTasques(
							(List<TascaLlistatDto>)pagina.getList()));
			model.addAttribute("command", tascaGrupFiltreCommand);
			model.addAttribute("tipusExp", llistatExpedientTipusAmbPermisos(entorn));
			
			// Asignamos los valores del filtro a la pestaña de personals
			model.addAttribute("personaLlistatCount", getPaginaTasquesPersonals(entorn,tascaGrupFiltreCommand,null,null,null,objectsPerPage).getFullListSize());
			request.getSession().setAttribute("commandTascaPersonaFiltre", tascaGrupFiltreCommand);
			
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
			@ModelAttribute("commandGrupFiltre") TascaFiltreCommand command) {
		if ("submit".equals(submit)) {
			request.getSession().setAttribute("commandTascaGrupFiltre", command);
		} else if ("clean".equals(submit)) {
			request.getSession().removeAttribute("commandTascaGrupFiltre");
		}
		return "redirect:/tasca/grupLlistat.html";
	}

	private List<ExpedientTipus> llistatExpedientTipusAmbPermisos(Entorn entorn) {
		List<ExpedientTipus> llistat = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
		permissionService.filterAllowed(
				llistat,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.SUPERVISION,
					ExtendedPermission.READ});
		return llistat;
	}

	public class TascaFiltreCommand {
		private String nom;
		private String expedient;
		private Long tipusExpedient;
		private Date dataCreacioInici;
		private Date dataCreacioFi;
		private Integer prioritat;
		private Date dataLimitInici;
		private Date dataLimitFi;
		public TascaFiltreCommand() {}
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

	private PaginatedList getPaginaTasquesPersonals(
			Entorn entorn,
			TascaFiltreCommand command,
			String page,
			String sort,
			String dir,
			String objectsPerPage) {
		int maxResults = getObjectsPerPage(objectsPerPage);
		int pagina = (page == null) ? 1: Integer.valueOf(page);
		int firstRow = (pagina - 1) * maxResults;
		boolean isAsc = (dir == null) || "asc".equals(dir);
		// Ordenació per defecte
		if (sort == null || sort.length() == 0) {
			sort = "dataCreacio";
			isAsc = false;
		}
		//
		PaginatedList paginatedList = new PaginatedList();
		paginatedList.setObjectsPerPage(maxResults);
		paginatedList.setPageNumber(pagina);
		paginatedList.setSortCriterion(sort);
		paginatedList.setSortDirection((isAsc) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING);
		PaginaLlistatDto dadesLlistat = tascaService.findTasquesPersonalsFiltre(
				entorn.getId(),
				null,
				command.getNom(),
				command.getExpedient(),
				command.getTipusExpedient(),
				command.getDataCreacioInici(),
				command.getDataCreacioFi(),
				command.getPrioritat(),
				command.getDataLimitInici(),
				command.getDataLimitFi(),
				firstRow,
				maxResults,
				sort,
				isAsc);
		paginatedList.setFullListSize(dadesLlistat.getCount());
		paginatedList.setList(dadesLlistat.getLlistat());
		return paginatedList;
	}
	private PaginatedList getPaginaTasquesGrup(
			Entorn entorn,
			TascaFiltreCommand command,
			String page,
			String sort,
			String dir,
			String objectsPerPage) {
		int maxResults = getObjectsPerPage(objectsPerPage);
		int pagina = (page == null) ? 1 : Integer.valueOf(page);
		int firstRow = (pagina - 1) * maxResults;
		boolean isAsc = (dir == null) || "asc".equals(dir);
		// Ordenació per defecte
		if (sort == null || sort.length() == 0) {
			sort = "dataCreacio";
			isAsc = false;
		}
		//
		PaginatedList paginatedList = new PaginatedList();
		paginatedList.setObjectsPerPage(maxResults);
		paginatedList.setPageNumber(pagina);
		paginatedList.setSortCriterion(sort);
		paginatedList.setSortDirection((isAsc) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING);
		PaginaLlistatDto dadesLlistat = tascaService.findTasquesGrupFiltre(
				entorn.getId(),
				null,
				command.getNom(),
				command.getExpedient(),
				command.getTipusExpedient(),
				command.getDataCreacioInici(),
				command.getDataCreacioFi(),
				command.getPrioritat(),
				command.getDataLimitInici(),
				command.getDataLimitFi(),
				firstRow,
				maxResults,
				sort,
				isAsc);
		paginatedList.setFullListSize(dadesLlistat.getCount());
		paginatedList.setList(dadesLlistat.getLlistat());
		return paginatedList;
	}

}
