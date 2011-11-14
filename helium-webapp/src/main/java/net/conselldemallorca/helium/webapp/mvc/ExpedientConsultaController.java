/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.PaginatedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.properties.SortOrderEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;


/**
 * Controlador per la gestió d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientConsultaController extends BaseController {

	public static final String VARIABLE_SESSIO_COMMAND = "consultaExpedientsCommand";

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientConsultaController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("expedientTipus")
	public List<ExpedientTipus> populateExpedientTipus(
			HttpServletRequest request) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<ExpedientTipus> tipus = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
			permissionService.filterAllowed(
					tipus,
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.READ});
			return tipus;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/consulta", method = RequestMethod.GET)
	public String consultaGet(
			HttpServletRequest request,
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "dir", required = false) String dir,
			@RequestParam(value = "objectsPerPage", required = false) String objectsPerPage,
			HttpSession session,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientConsultaGeneralCommand command = (ExpedientConsultaGeneralCommand)session.getAttribute(VARIABLE_SESSIO_COMMAND);
			if (command != null) {
				PaginatedList pagina = getPaginaExpedients(
						entorn,
						command,
						page,
						sort,
						dir,
						objectsPerPage);
				List<String> piis = new ArrayList<String>();
				for (ExpedientDto expedient: (List<ExpedientDto>)pagina.getList())
					piis.add(expedient.getProcessInstanceId());
				model.addAttribute("command", command);
				model.addAttribute("llistat", pagina);
				model.addAttribute("piis", piis);
			} else {
				model.addAttribute("command", new ExpedientConsultaGeneralCommand());
			}
			List<Estat> estats;
			if (command != null && command.getExpedientTipus() != null)
				estats = dissenyService.findEstatAmbExpedientTipus(command.getExpedientTipus().getId());
			else
				estats = new ArrayList<Estat>();
			afegirEstatsInicialIFinal(estats);
			model.addAttribute("estats", estats);
			return "expedient/consulta";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/consulta", method = RequestMethod.POST)
	public String consultaPost(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ExpedientConsultaGeneralCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if ("submit".equals(submit)) {
				session.setAttribute(VARIABLE_SESSIO_COMMAND, command);
				return "redirect:/expedient/consulta.html";
			} else if ("clean".equals(submit)) {
				session.removeAttribute(VARIABLE_SESSIO_COMMAND);
				return "redirect:/expedient/consulta.html";
			}
			return "expedient/consulta";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/consultaEstats")
	public String consultaEstats(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Estat> estats;
			if (id != null)
				estats = dissenyService.findEstatAmbExpedientTipus(id);
			else
				estats = new ArrayList<Estat>();
			afegirEstatsInicialIFinal(estats);
			model.addAttribute("estats", estats);
			return "expedient/consultaEstats";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				ExpedientTipus.class,
				new ExpedientTipusTypeEditor(dissenyService));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

	public void afegirEstatsInicialIFinal(List<Estat> estats) {
		Estat iniciat = new Estat();
		iniciat.setId(new Long(0));
		iniciat.setNom( getMessage("expedient.consulta.iniciat") );
		estats.add(0, iniciat);
		Estat finalitzat = new Estat();
		finalitzat.setId(new Long(-1));
		finalitzat.setNom( getMessage("expedient.consulta.finalitzat") );
		estats.add(finalitzat);
	}



	private PaginatedList getPaginaExpedients(
			Entorn entorn,
			ExpedientConsultaGeneralCommand command,
			String page,
			String sort,
			String dir,
			String objectsPerPage) {
		boolean iniciat = false;
		boolean finalitzat = false;
		Long estatId = null;
		if (command.getEstat() != null) {
			iniciat = (command.getEstat() == 0);
			finalitzat = (command.getEstat() == -1);
			estatId = (!iniciat && !finalitzat) ? command.getEstat() : null;
		}
		int maxResults = getObjectsPerPage(objectsPerPage);
		int pagina = (page != null) ? new Integer(page).intValue() : 1;
		int firstRow = (pagina - 1) * maxResults;
		boolean isAsc = (dir == null) || "asc".equals(dir);
		// Si no s'especifica columna per cercar ordena en sentit descendent
		if (sort == null || sort.length() == 0)
			isAsc = false;
		//
		PaginatedList paginatedList = new PaginatedList();
		paginatedList.setFullListSize(expedientService.countAmbEntornConsultaGeneral(
				entorn.getId(),
				command.getTitol(),
				command.getNumero(),
				command.getDataInici1(),
				command.getDataInici2(),
				(command.getExpedientTipus() != null) ? command.getExpedientTipus().getId() : null,
				estatId,
				iniciat,
				finalitzat,
				command.getGeoPosX(),
				command.getGeoPosY(),
				command.getGeoReferencia(),
				command.isMostrarAnulats()));
		paginatedList.setObjectsPerPage(maxResults);
		paginatedList.setPageNumber(pagina);
		paginatedList.setSortCriterion(sort);
		paginatedList.setSortDirection((isAsc) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING);
		List<ExpedientDto> expedients = expedientService.findAmbEntornConsultaGeneralPaginat(
				entorn.getId(),
				command.getTitol(),
				command.getNumero(),
				command.getDataInici1(),
				command.getDataInici2(),
				(command.getExpedientTipus() != null) ? command.getExpedientTipus().getId() : null,
				estatId,
				iniciat,
				finalitzat,
				command.getGeoPosX(),
				command.getGeoPosY(),
				command.getGeoReferencia(),
				command.isMostrarAnulats(),
				firstRow,
				maxResults,
				sort,
				isAsc);
		paginatedList.setList(expedients);
		return paginatedList;
	}

	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(ExpedientConsultaController.class);

}
