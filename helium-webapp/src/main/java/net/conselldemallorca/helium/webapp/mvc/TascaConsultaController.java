/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.core.model.dto.PaginaLlistatDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.dto.TascaLlistatDto;
import net.conselldemallorca.helium.core.model.hibernate.Area;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExecucioMassivaService;
import net.conselldemallorca.helium.core.model.service.OrganitzacioService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.PaginatedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.properties.SortOrderEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Controlador per a mostrar els llistats de tasques pendents
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class TascaConsultaController extends BaseController {
	
	private static final String SESSION_ATTRIBUTE_TCON_SELCON_COMMAND = "TascaConsultaController.selcon.command";
	private static final String SESSION_ATTRIBUTE_TCON_FILTRE_COMMAND = "TascaConsultaController.filtre.command";
	private static final String SESSION_ATTRIBUTE_CURRENT_SORT_TASCA = "TascaConsultaController.current.sort";
	private static final String SESSION_ATTRIBUTE_CURRENT_DIR_TASCA = "TascaConsultaController.current.dir";
	public static final String SESSION_ATTRIBUTE_TCON_IDS_MASSIUS = "TascaConsultaIdsMassius";
	
	private TascaService tascaService;
	private DissenyService dissenyService;
	private PermissionService permissionService;
	private PluginService pluginService;
	private OrganitzacioService organitzacioService;
	private ExecucioMassivaService execucioMassivaService;

	@Autowired
	public TascaConsultaController(
			TascaService tascaService,
			DissenyService dissenyService,
			PermissionService permissionService,
			PluginService pluginService,
			OrganitzacioService organitzacioService,
			ExecucioMassivaService execucioMassivaService) {
		this.tascaService = tascaService;
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
		this.pluginService = pluginService;
		this.organitzacioService = organitzacioService;
		this.execucioMassivaService = execucioMassivaService;
	}

	@ModelAttribute("commandSelConsulta")
	public TascaConsultaCommand populateCommandSeleccioConsulta(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
			@RequestParam(value = "canviar", required = false) Boolean canviar){
		TascaConsultaCommand command = (TascaConsultaCommand)session.getAttribute(SESSION_ATTRIBUTE_TCON_SELCON_COMMAND);
		if (command == null) {
			command = new TascaConsultaCommand();
		}
		if (canviar != null && canviar.booleanValue()) {
			command.setExpedientTipusId(expedientTipusId);
			session.removeAttribute(SESSION_ATTRIBUTE_TCON_FILTRE_COMMAND);
		}
		session.setAttribute(SESSION_ATTRIBUTE_TCON_SELCON_COMMAND, command);
		return command;
	}
	@ModelAttribute("commandFiltre")
	public TascaConsultaFiltreCommand populateCommandFiltre(HttpSession session) {
		Object tascaConsultaFiltreCommand = session.getAttribute(SESSION_ATTRIBUTE_TCON_FILTRE_COMMAND);
		if (tascaConsultaFiltreCommand != null)
			return (TascaConsultaFiltreCommand)tascaConsultaFiltreCommand;
		else
			return new TascaConsultaFiltreCommand();
	}

	@RequestMapping(value = "/tasca/consultaTasques")
	public String tasquesLlistatGet(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "dir", required = false) String dir,
			@RequestParam(value = "objectsPerPage", required = false) String objectsPerPage,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			TascaConsultaCommand commandSeleccio = (TascaConsultaCommand)session.getAttribute(SESSION_ATTRIBUTE_TCON_SELCON_COMMAND);
			populateModelCommon(entorn, model, session,  commandSeleccio);
			return "tasca/consultaTasques";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
		}
	}
	
	@RequestMapping(value = "/tasca/consultaTasquesResultat") //, method = RequestMethod.POST)
	public String consultaDissenyResultat(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "dir", required = false) String dir,
			@RequestParam(value = "objectsPerPage", required = false) String objectsPerPage,
			@ModelAttribute("commandFiltre") Object command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if ("massiu".equals(submit)) {
				return "redirect:/tasca/execucioMassiva.html";
			}
			session.setAttribute(SESSION_ATTRIBUTE_TCON_FILTRE_COMMAND, command);
			TascaConsultaCommand commandSeleccio = (TascaConsultaCommand)session.getAttribute(SESSION_ATTRIBUTE_TCON_SELCON_COMMAND);
			populateModelCommon(entorn, model, session, commandSeleccio);
			if ("submit".equals(submit) || submit.length() == 0) {
				populateModelFiltre(model, session);
				if (result.hasErrors()) {
					return "tasca/consultaTasques";
				}
				String sortCalculat = sort;
				if (sort != null && sort.length() > 0) {
					request.getSession().setAttribute(SESSION_ATTRIBUTE_CURRENT_SORT_TASCA, sort);
				} else {
					String sortSessio = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_CURRENT_SORT_TASCA);
					if (sortSessio != null)
						sortCalculat = sortSessio;
				}
				String dirCalculat = dir;
				if (dir != null && dir.length() > 0) {
					request.getSession().setAttribute(SESSION_ATTRIBUTE_CURRENT_DIR_TASCA, dir);
				} else {
					String dirSessio = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_CURRENT_DIR_TASCA);
					if (dirSessio != null)
						dirCalculat = dirSessio;
				}
				TascaConsultaFiltreCommand tascaConsultaFiltreCommand = new TascaConsultaFiltreCommand();
				if (command != null)  {
					tascaConsultaFiltreCommand = (TascaConsultaFiltreCommand)command;
				}
				PaginatedList pagina = getPaginaTasques(
						entorn,
						commandSeleccio.getExpedientTipusId(),
						tascaConsultaFiltreCommand,
						page,
						sortCalculat,
						dirCalculat,
						objectsPerPage);
				model.addAttribute("llistat", pagina);
			} else if ("clean".equals(submit)) {
				session.removeAttribute(SESSION_ATTRIBUTE_TCON_FILTRE_COMMAND);
				session.removeAttribute(SESSION_ATTRIBUTE_TCON_IDS_MASSIUS);
				model.addAttribute("commandFiltre", new TascaConsultaFiltreCommand());
			} 
			model.addAttribute("objectsPerPage", objectsPerPage);
			return "tasca/consultaTasques";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
		}
	}
	
	@ModelAttribute("reassignarCommand")
	public ReassignarCommand populateReassignarCommand(){
		return new ReassignarCommand();
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/tasca/execucioMassiva", method = RequestMethod.GET)
	public String tasquesMassiva(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = (List<Long>)request.getSession().getAttribute(SESSION_ATTRIBUTE_TCON_IDS_MASSIUS);
			if (ids == null || ids.isEmpty()) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				return "redirect:/tasca/consultaTasques.html";
			}
			List<TascaLlistatDto> tasquesTramitacioMassiva = tascaService.findTasquesAmbId(entorn.getId(), ids.subList(1, ids.size()));
			model.addAttribute("tasques", tasquesTramitacioMassiva);
			
			//TODO: Variables... si totes les tasques són la mateixa.
			
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
		}
		return "tasca/massivaInfo";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/tasca/massivaReassignar")
	public String accioReassignar(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = true) String inici,
			@RequestParam(value = "correu", required = true) String correu,
			@ModelAttribute("reassignarCommand") ReassignarCommand command,
			BindingResult result,
			ModelMap model) {
		model.addAttribute("inici", inici);
		model.addAttribute("correu", correu);
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = (List<Long>)request.getSession().getAttribute(SESSION_ATTRIBUTE_TCON_IDS_MASSIUS);
			if (ids == null || ids.size() <= 1) {
				missatgeError(request, getMessage("error.no.tasc.selec"));
				return "redirect:/tasca/consultaTasques.html";
			}
			String tipus = request.getParameter("tipusExpressio"); 
			ReassignarValidator validator = new ReassignarValidator();
			validator.setTipus(tipus);
			validator.validate(command, result);
			if (result.hasErrors()) {
				missatgeError(request, getMessage("error.executar.reassignacio"), result.toString());
				return "redirect:/tasca/consultaTasques.html";
	        }
			
			int numExp = ids.size() - 1;
			// Obtenim informació de l'execució massiva
			// Data d'inici
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Date dInici = new Date();
			if (inici != null) {
				try { dInici = sdf.parse(inici); } catch (ParseException pe) {};
			}
			// Enviar correu
			Boolean bCorreu = false;
			if (correu != null && correu.equals("true")) bCorreu = true;
			
			TascaDto tasca = tascaService.getByIdSenseComprovacioIDades(ids.get(1).toString());
			Expedient expedientPrimer = tasca.getExpedient(); //expedientService.getById(ids.get(1));
			if (potModificarOReassignarExpedient(expedientPrimer)) {
				String expression = command.getExpression();
				if ("user".equals(tipus)) {
					expression = "user(" + command.getUsuari() + ")";
				} else if ("grup".equals(tipus)) {
					Area grup = organitzacioService.getAreaById(command.getGrup());
					expression = "group(" + grup.getCodi() + ")";
				}
				try {
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					String[] tasquesId = new String[ids.size() - 1];
					for (int i = 0; i < ids.size() - 1; i++)
						tasquesId[i] = ids.get(i + 1).toString();
					dto.setTascaIds(tasquesId);
					dto.setExpedientTipusId(ids.get(0));
					dto.setTipus(ExecucioMassivaTipus.REASSIGNAR);
					dto.setParam1(expression);
					Object[] params = new Object[] {entorn.getId()};
    				dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
					missatgeInfo(request, getMessage("info.accio.massiu.reassignat", new Object[] {numExp}));
				} catch (Exception e) {
					missatgeError(request, getMessage("error.no.massiu"));
					logger.error("Error al programar les accions massives", e);
				}
			} else {
				missatgeError(request, getMessage("info.massiu.permisos.no"));
			}
			return "redirect:/tasca/consultaTasques.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/tasca/massivaIds", method = RequestMethod.POST)
	public String consultaMassiva(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "tasquesId[]", required = true) Long[] tasquesId,
			@RequestParam(value = "checked", required = true) boolean checked,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = (List<Long>)request.getSession().getAttribute(SESSION_ATTRIBUTE_TCON_IDS_MASSIUS);
			// Emmagatzema els ids d'expedient seleccionats a dins una llista.
			// El primer element de la llista és l'id del tipus d'expedient.
			if (ids == null) {
				ids = new ArrayList<Long>();
				request.getSession().setAttribute(SESSION_ATTRIBUTE_TCON_IDS_MASSIUS, ids);
			}
			// S'assegura que el primer element sigui l'id del tipus d'expedient
			if (ids.isEmpty()) {
				ids.add(expedientTipusId);
			} else if (!expedientTipusId.equals(ids.get(0))) {
				// Si el tipus d'expedient ha canviat reinicia la llista
				ids.clear();
				ids.add(expedientTipusId);
			}
			for (Long eid: tasquesId) {
				if (checked) {
					ids.add(eid);
				} else {
					ids.remove(eid);
					if (ids.size() == 1)
						ids.clear();
				}
			}
		}
		return null;
	}
	
	private PaginatedList getPaginaTasques(
			Entorn entorn,
			Long expedientTipusId,
			TascaConsultaFiltreCommand command,
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
		PaginaLlistatDto dadesLlistat = tascaService.findTasquesConsultaFiltre(
				entorn.getId(),
				expedientTipusId,
				command.getResponsable(),
				command.getJbpmName(),
				command.getTitol(),
				command.getDataCreacioInici(),
				command.getDataCreacioFi(),
				command.getMostrarTasquesGrup(),
				firstRow,
				maxResults,
				sort,
				isAsc);
		paginatedList.setFullListSize(dadesLlistat.getCount());
		paginatedList.setList(dadesLlistat.getLlistat());
		return paginatedList;
	}
	
	private void populateModelCommon(Entorn entorn, ModelMap model, HttpSession session, TascaConsultaCommand commandSeleccio) {
//		TascaConsultaCommand commandSeleccio = (TascaConsultaCommand)session.getAttribute(SESSION_ATTRIBUTE_TCON_SELCON_COMMAND);
		List<ExpedientTipus> tipus = dissenyService.findExpedientTipusAmbEntornOrdenat(entorn.getId(), "nom");
		permissionService.filterAllowed(
				tipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.REASSIGNMENT});
		model.addAttribute("expedientsTipus", tipus);
		
		if (commandSeleccio != null) {
			if (commandSeleccio.getExpedientTipusId() != null) { 
				ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(commandSeleccio.getExpedientTipusId());
				model.addAttribute("expedientTipus", expedientTipus);
				
				// Tasques
				List<Tasca> tasques = new ArrayList<Tasca>();
				
				for (DefinicioProcesDto defProc: dissenyService.findDefinicionsProcesAmbExpedientTipus(expedientTipus)) {
					tasques.addAll(dissenyService.findTasquesAmbDefinicioProces(defProc.getId()));
				}
				Collections.sort(
						tasques, 
						new Comparator<Tasca>() {
							public int compare(Tasca t1, Tasca t2) {
								if (t1 != null && t2 == null)
									return -1;
								if (t1 == null && t2 == null)
									return 0;
								if (t1 == null && t2 != null)
									return 1;
								return t1.getNom().compareTo(t2.getNom());
							}
						}
				);
				model.addAttribute("tasques", tasques);
			}
		}
	}
	
	private void populateModelFiltre(ModelMap model, HttpSession session) {
		Object commandFiltre = session.getAttribute(SESSION_ATTRIBUTE_TCON_FILTRE_COMMAND);
		if (commandFiltre != null) {
			TascaConsultaFiltreCommand filtre = (TascaConsultaFiltreCommand)commandFiltre;
			if (filtre.getResponsable() != null && !"".equals(filtre.getResponsable())) {
				List<PersonaDto> responsable = pluginService.findPersonaLikeNomSencer(filtre.getResponsable());
				if (!responsable.isEmpty())
					model.addAttribute("responsable", responsable.get(0));
			}
			model.addAttribute("commandFiltre", commandFiltre);
		}
	}
	
	private boolean potModificarOReassignarExpedient(Expedient expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.REASSIGNMENT}) != null;
	}
	
	public class TascaConsultaCommand {
		private Long expedientTipusId;
		
		public Long getExpedientTipusId() {
			return expedientTipusId;
		}
		public void setExpedientTipusId(Long expedientTipusId) {
			this.expedientTipusId = expedientTipusId;
		}
	}
	
	public class TascaConsultaFiltreCommand {
		private Long tascaId;
		private String jbpmName;
		private String titol;
		private String responsable;
		private Date dataCreacioInici;
		private Date dataCreacioFi;
		private Boolean mostrarTasquesGrup;
		public TascaConsultaFiltreCommand() {
			this.mostrarTasquesGrup = true;
		}
		public Long getTascaId() {
			return tascaId;
		}
		public void setTascaId(Long tascaId) {
			this.tascaId = tascaId;
		}
		public String getJbpmName() {
			return jbpmName;
		}
		public void setJbpmName(String jbpmName) {
			this.jbpmName = jbpmName;
		}
		public String getTitol() {
			return titol;
		}
		public void setTitol(String titol) {
			this.titol = titol;
		}
		public String getResponsable() {
			return responsable;
		}
		public void setResponsable(String responsable) {
			this.responsable = responsable;
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
		public Boolean getMostrarTasquesGrup() {
			return mostrarTasquesGrup;
		}
		public void setMostrarTasquesGrup(Boolean mostrarTasquesGrup) {
			this.mostrarTasquesGrup = mostrarTasquesGrup;
		}
	}
	
	public class ReassignarCommand {

		private String usuari;
		private Long grup;
		private String expression;
		
		public String getUsuari() {
			return usuari;
		}
		public void setUsuari(String usuari) {
			this.usuari = usuari;
		}
		public Long getGrup() {
			return grup;
		}
		public void setGrup(Long grup) {
			this.grup = grup;
		}
		public String getExpression() {
			return expression;
		}
		public void setExpression(String expression) {
			this.expression = expression;
		}
	}
	
	private class ReassignarValidator implements Validator {
		
		private String tipus;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ReassignarCommand.class);
		}
		public void validate(Object obj, Errors errors) {
			if ("user".equals(tipus)) {
				ValidationUtils.rejectIfEmpty(errors, "usuari", "not.blank");
			} else if ("grup".equals(tipus)) {
				ValidationUtils.rejectIfEmpty(errors, "grup", "not.blank");
			} else if ("expr".equals(tipus)) {
				ValidationUtils.rejectIfEmpty(errors, "expression", "not.blank");
			}
		}
		public void setTipus(String tipus) {
			this.tipus = tipus;
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

	private static final Log logger = LogFactory.getLog(TascaConsultaController.class);

}
