/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.conselldemallorca.helium.core.model.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.report.FieldValue;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.PaginatedList;
import net.conselldemallorca.helium.webapp.mvc.util.TascaFormUtil;

import org.displaytag.properties.SortOrderEnum;
import org.displaytag.tags.TableTagParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
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
 * Controlador per la gestió de consultes dissenyades
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientConsultaDissenyController extends BaseController {

	private static final String VARIABLE_SESSIO_SELCON_COMMAND = "expedientTipusConsultaDissenyCommand";
	private static final String VARIABLE_SESSIO_FILTRE_COMMAND = "expedientTipusConsultaFiltreCommand";
	public static final String VARIABLE_SESSIO_IDS_MASSIUS_TE = "consultaExpedientsIdsMassiusTE";
	private static final String VARIABLE_SESSIO_SELCON_COMMAND_TE = "expedientTipusConsultaDissenyCommandTE";
	public static final String VARIABLE_SESSIO_IDS_MASSIUS = "consultaExpedientsIdsMassius";
	

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;
	@Autowired
	public ExpedientConsultaDissenyController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.dissenyService  = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("commandSeleccioConsulta")
	public ExpedientConsultaDissenyCommand populateCommandSeleccioConsulta(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
			@RequestParam(value = "consultaId", required = false) Long consultaId,
			@RequestParam(value = "canviar", required = false) Boolean canviar){
		ExpedientConsultaDissenyCommand command = (ExpedientConsultaDissenyCommand)session.getAttribute(VARIABLE_SESSIO_SELCON_COMMAND);
		
		if (command == null){
			command = new ExpedientConsultaDissenyCommand();
		}

		if (consultaId != null && expedientTipusId == null) {
			command.setConsultaId(command.getConsultaId());
			command.setExpedientTipusId(dissenyService.getConsultaById(consultaId).getExpedientTipus().getId());
		}
		if (canviar != null && canviar.booleanValue()) {
			command.setExpedientTipusId(expedientTipusId);
			command.setConsultaId(consultaId);
			if (command.getExpedientTipusId() != null && !command.getExpedientTipusId().equals(expedientTipusId) ){
				command.setConsultaId(null);
				session.removeAttribute(VARIABLE_SESSIO_FILTRE_COMMAND);}
		}	

		session.setAttribute(VARIABLE_SESSIO_SELCON_COMMAND, command);
			return command;
		
	}
	@ModelAttribute("commandFiltre")
	public Object populateCommandFiltre(HttpSession session) {
		ExpedientConsultaDissenyCommand commandSeleccioConsulta =
			(ExpedientConsultaDissenyCommand)session.getAttribute(VARIABLE_SESSIO_SELCON_COMMAND);
		if (commandSeleccioConsulta != null && commandSeleccioConsulta.getConsultaId() != null) {
			List<Camp> camps = dissenyService.findCampsPerCampsConsulta(
					commandSeleccioConsulta.getConsultaId(),
					TipusConsultaCamp.FILTRE,
					true);
			Object command = TascaFormUtil.getCommandForFiltre(
					camps,
					null,
					null,
					null);
			return command;
		}
		return null;
	}
	
	
	@ModelAttribute("valorsBoolea")
	public List<ParellaCodiValorDto> valorsBoolea() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto("true",  getMessage("comuns.si") ));
		resposta.add(new ParellaCodiValorDto("false",  getMessage("comuns.no") ));
		return resposta;
	}
	
	@RequestMapping(value = "/expedient/consultaDisseny")
	public String consultaDisseny(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "dir", required = false) String dir,
			@RequestParam(value = "objectsPerPage", required = false) String objectsPerPage,
			HttpSession session,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientConsultaDissenyCommand commandSeleccio =
				(ExpedientConsultaDissenyCommand)model.get("commandSeleccioConsulta");
			populateModelCommon(entorn, model, commandSeleccio);
			Object commandFiltre = session.getAttribute(VARIABLE_SESSIO_FILTRE_COMMAND);
		
			if (commandFiltre != null && commandSeleccio != null && commandSeleccio.getConsultaId() != null) {
				model.addAttribute("commandFiltre", commandFiltre);
				List<Camp> camps = dissenyService.findCampsPerCampsConsulta(
						commandSeleccio.getConsultaId(),
						TipusConsultaCamp.FILTRE,
						true);
				Map<String, Object> valors = TascaFormUtil.getValorsFromCommand(
						camps,
						commandFiltre,
						true,
						true);
				Map<String, Object> valorsPerService = new HashMap<String, Object>();
				for (String clau: valors.keySet()) {
					String clauPerService = clau.replaceFirst("_", ".");
					Object valor = valors.get(clau);
					/*if (valor instanceof Object[])
						System.out.println("@@@ " + clauPerService + ": [" + ((Object[])valor)[0] + ", " + ((Object[])valor)[1] + "]");
					else
						System.out.println("@@@ " + clauPerService + ": " + valor);*/
					valorsPerService.put(
							clauPerService,
							valor);
				}
				boolean export = request.getParameter(TableTagParameters.PARAMETER_EXPORTING) != null;
				
				model.addAttribute(
						"expedients",
						getPaginaExpedients(
								entorn.getId(),
								commandSeleccio.getConsultaId(),
								valorsPerService,
								page,
								sort,
								dir,
								objectsPerPage,
								export));
			}
			return "expedient/consultaDisseny";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/consultaDissenyResultat", method = RequestMethod.POST)
	public String consultaDissenyResultat(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "idsExp", required = false) List<String> idsExp,
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
			ExpedientConsultaDissenyCommand commandSeleccio =
				(ExpedientConsultaDissenyCommand)model.get("commandSeleccioConsulta");
			if (commandSeleccio.getConsultaId() != null) {
				Consulta consulta = dissenyService.getConsultaById(commandSeleccio.getConsultaId());
				
				if (permissionService.filterAllowed(
						consulta.getExpedientTipus(),
						ExpedientTipus.class,
						new Permission[] {
							ExtendedPermission.ADMINISTRATION,
							ExtendedPermission.READ}) == null) {
					missatgeError(request, getMessage("error.consulta.tipexp.noaut"));
					return "redirect:/expedient/consultaDisseny.html";
				}
			}
			if ("submit".equals(submit) || submit.length() == 0) {
				if (result.hasErrors()) {
					populateModelCommon(entorn, model, commandSeleccio);
					return "expedient/consultaDisseny";
				} else {
					request.getSession().removeAttribute(ExpedientMassivaController.VARIABLE_SESSIO_IDS_MASSIUS_TE);
					session.setAttribute(VARIABLE_SESSIO_FILTRE_COMMAND, command);
				}
			} else if ("informe".equals(submit)) {
				if (result.hasErrors()) {
					populateModelCommon(entorn, model, commandSeleccio);
					return "expedient/consultaDisseny";
				} else {
					session.setAttribute(VARIABLE_SESSIO_FILTRE_COMMAND, command);
					model.addAttribute("idsSeleccionats",request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE));
					return "redirect:/expedient/consultaDissenyInforme.html";
				}
			} else if ("netejar".equals(submit)) {
				session.removeAttribute(VARIABLE_SESSIO_FILTRE_COMMAND);
				request.getSession().removeAttribute(ExpedientMassivaController.VARIABLE_SESSIO_IDS_MASSIUS);
				request.getSession().removeAttribute(ExpedientMassivaController.VARIABLE_SESSIO_IDS_MASSIUS_TE);
			
			} 

			//***********************
			
			if ("massiva".equals(submit)) {
				session.setAttribute(VARIABLE_SESSIO_SELCON_COMMAND_TE, commandSeleccio);
				commandSeleccio.setMassivaActiu(true);
				
			} else if ("nomassiva".equals(submit)) {
				request.getSession().removeAttribute(ExpedientMassivaController.VARIABLE_SESSIO_IDS_MASSIUS_TE);
				session.removeAttribute(VARIABLE_SESSIO_SELCON_COMMAND_TE);
			}
			else if ("clean".equals(submit)) {
				request.getSession().removeAttribute(ExpedientMassivaController.VARIABLE_SESSIO_IDS_MASSIUS);
				request.getSession().removeAttribute(ExpedientMassivaController.VARIABLE_SESSIO_IDS_MASSIUS_TE);
			}
			//***********************

			return "redirect:/expedient/consultaDisseny.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
		}
	}

	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/consultaDissenyInforme")
	public String consultaDissenyInforme(
			HttpServletRequest request,
			HttpSession session,
			ModelMap model){
		Entorn entorn = getEntornActiu(request);
		List<Long> ids = new ArrayList<Long>();
		if(request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE)!=null){
			ids = (List<Long>) request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
		}
		if (entorn != null) {
			ExpedientConsultaDissenyCommand commandSeleccio =
				(ExpedientConsultaDissenyCommand)model.get("commandSeleccioConsulta");
			populateModelCommon(entorn, model, commandSeleccio);
			Object commandFiltre = session.getAttribute(VARIABLE_SESSIO_FILTRE_COMMAND);
			if (commandFiltre != null) {
				Consulta consulta = dissenyService.getConsultaById(commandSeleccio.getConsultaId());
				if (consulta.getInformeNom() != null) {
					model.addAttribute("commandFiltre", commandFiltre);
					List<Camp> camps = dissenyService.findCampsPerCampsConsulta(
							commandSeleccio.getConsultaId(),
							TipusConsultaCamp.FILTRE,
							true);
					Map<String, Object> valors = TascaFormUtil.getValorsFromCommand(
							camps,
							commandFiltre,
							true,
							true);
					
					//si no s'ha seleccionat cap expedient mostra missatge d'errror
					if(valors.size()==0){
						missatgeError(request, getMessage("error.no.exp.selec"));
						
						return "redirect:/expedient/consultaDisseny.html";}
					
					Map<String, Object> valorsPerService = new HashMap<String, Object>();
					for (String clau: valors.keySet()) {
						String clauPerService = clau.replaceFirst("_", ".");
						Object valor = valors.get(clau);
						valorsPerService.put(
								clauPerService,
								valor);
					}

					
					List<ExpedientConsultaDissenyDto> expedients = expedientService.findAmbEntornConsultaDisseny(
							entorn.getId(),
							commandSeleccio.getConsultaId(),
							valorsPerService,
							ExpedientCamps.EXPEDIENT_CAMP_ID,
							true);
					
					List<ExpedientConsultaDissenyDto> expedientsTE = new ArrayList<ExpedientConsultaDissenyDto>();
					
					if(ids.size()>0){
						for(int c=0;c<=expedients.size()-1;c++){
							
							for(int b=1;b<=ids.size()-1;b++){
							
								if(expedients.get(c).getExpedient().getId().equals(ids.get(b))){
									expedientsTE.add(expedients.get(c));
								}
							}
						}

					}
					
					
					if(expedientsTE.size()>0){
						model.addAttribute(
								JasperReportsView.MODEL_ATTRIBUTE_REPORTDATA,
								getDadesDatasource(expedientsTE));
					}else{

						model.addAttribute(
								JasperReportsView.MODEL_ATTRIBUTE_REPORTDATA,
								getDadesDatasource(expedients));
						
					}
					
					model.addAttribute(
							JasperReportsView.MODEL_ATTRIBUTE_REPORTCONTENT,
							consulta.getInformeContingut());
					/*if (consulta.getSubConsultes().size() > 0) {
						String[] subreports = new String[consulta.getSubConsultes().size()];
						int index = 0;
						for (Consulta subconsulta: consulta.getSubConsultes())  {
							String subreportCodi = subconsulta.getCodi();
							subreports[index++] = subreportCodi;
							List<ExpedientConsultaDissenyDto> expedientsSub = expedientService.findAmbEntornConsultaDisseny(
									entorn.getId(),
									subconsulta.getId(),
									valorsPerService,
									ExpedientCamps.EXPEDIENT_CAMP_ID,
									true);
							model.addAttribute(
									JasperReportsView.MODEL_ATTRIBUTE_SUBREPORTDATA_PREFIX + subreportCodi,
									getDadesDatasource(expedientsSub));
						}
						model.addAttribute(
								JasperReportsView.MODEL_ATTRIBUTE_SUBREPORTS,
								subreports);
					}*/
					return "jasperReportsView";
				} else {
					missatgeError(request, getMessage("error.consulta.informe.nonhiha"));
				}
			} else {
				missatgeError(request, getMessage("error.consulta.informe.nofiltre"));
			}
			return "redirect:/expedient/consultaDisseny.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
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
						new DecimalFormat("#,###.00"),
						true));
		binder.registerCustomEditor(
				Boolean.class,
				new CustomBooleanEditor(true));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Termini.class,
				new TerminiTypeEditor());
	}

	public class ExpedientConsultaDissenyCommand {
		private Long expedientTipusId;
		private Long consultaId;
		private boolean massivaActiu = false;
		
		
		public Long getExpedientTipusId() {
			return expedientTipusId;
		}
		public void setExpedientTipusId(Long expedientTipusId) {
			this.expedientTipusId = expedientTipusId;
		}
		public Long getConsultaId() {
			return consultaId;
		}
		public void setConsultaId(Long consultaId) {
			this.consultaId = consultaId;
		}
		public boolean isMassivaActiu() {
			return massivaActiu;
		}
		public void setMassivaActiu(boolean massivaActiu) {
			this.massivaActiu = massivaActiu;
		}
	}



	private void populateModelCommon(
			Entorn entorn,
			ModelMap model,
			ExpedientConsultaDissenyCommand commandSeleccio) {
		List<ExpedientTipus> tipus = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
		permissionService.filterAllowed(
				tipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.READ});
		model.addAttribute("expedientTipus", tipus);
		if (commandSeleccio != null) {
			
			
				List<Consulta> consultes = dissenyService.findConsultesAmbEntornIExpedientTipusOrdenat(
						entorn.getId(),
						commandSeleccio.getExpedientTipusId());
				Iterator<Consulta> it = consultes.iterator();
				while (it.hasNext()) {
					Consulta consulta = it.next();
					boolean hiEs = false;
					for (ExpedientTipus expedientTipus: tipus) {
						if (consulta.getExpedientTipus().equals(expedientTipus)) {
							hiEs = true;
							break;
						}
					}
					if (!hiEs || consulta.isOcultarActiu())
						it.remove();
				}
				model.addAttribute("consultes", consultes);
				if (commandSeleccio.getConsultaId() != null) {
					model.addAttribute(
							"consulta",
							dissenyService.getConsultaById(commandSeleccio.getConsultaId()));
					model.addAttribute(
							"campsFiltre",
							dissenyService.findCampsPerCampsConsulta(
									commandSeleccio.getConsultaId(),
									TipusConsultaCamp.FILTRE,
									true));
					model.addAttribute(
							"campsInforme",
							dissenyService.findCampsPerCampsConsulta(
									commandSeleccio.getConsultaId(),
									TipusConsultaCamp.INFORME,
									true));
				}
				if (commandSeleccio.getExpedientTipusId() != null) {
					List<Estat> estats = dissenyService.findEstatAmbExpedientTipus(commandSeleccio.getExpedientTipusId());
					afegirEstatsInicialIFinal(estats);
					model.addAttribute("estats", estats);
				}

		}
	}

	private void afegirEstatsInicialIFinal(List<Estat> estats) {
		Estat iniciat = new Estat();
		iniciat.setId(new Long(0));
		iniciat.setNom( getMessage("expedient.consulta.iniciat") );
		estats.add(0, iniciat);
		Estat finalitzat = new Estat();
		finalitzat.setId(new Long(-1));
		finalitzat.setNom( getMessage("expedient.consulta.finalitzat") );
		estats.add(finalitzat);
	}

	private List<Map<String, FieldValue>> getDadesDatasource(List<ExpedientConsultaDissenyDto> expedients) {
		List<Map<String, FieldValue>> dadesDataSource = new ArrayList<Map<String, FieldValue>>();
		for (ExpedientConsultaDissenyDto dadesExpedient: expedients) {
			ExpedientDto expedient = dadesExpedient.getExpedient();
			Map<String, FieldValue> mapFila = new HashMap<String, FieldValue>();
			for (String clau: dadesExpedient.getDadesExpedient().keySet()) {
				DadaIndexadaDto dada = dadesExpedient.getDadesExpedient().get(clau);
				mapFila.put(
						dada.getReportFieldName(),
						toReportField(expedient, dada));
			}
			dadesDataSource.add(mapFila);
		}
		return dadesDataSource;
	}


	private FieldValue toReportField(ExpedientDto expedient, DadaIndexadaDto dadaIndex) {
		FieldValue field = new FieldValue(
				dadaIndex.getDefinicioProcesCodi(),
				dadaIndex.getReportFieldName(),
				dadaIndex.getEtiqueta());
		if (!dadaIndex.isMultiple()) {
			field.setValor(dadaIndex.getValor());
			if ("expedient%estat".equals(field.getCampCodi())) {
				if (expedient.getDataFi() != null) {
					field.setValorMostrar(this.getMessage("expedient.consulta.finalitzat"));
				} else {
					if (expedient.getEstat() != null)
						field.setValorMostrar(expedient.getEstat().getNom());
					else
						field.setValorMostrar(this.getMessage("expedient.consulta.iniciat"));
				}
			} else {
				field.setValorMostrar(dadaIndex.getValorMostrar());
			}
			if (dadaIndex.isOrdenarPerValorMostrar())
				field.setValorOrdre(dadaIndex.getValorMostrar());
			else
				field.setValorOrdre(dadaIndex.getValorIndex());
		} else {
			field.setValorMultiple(dadaIndex.getValorMultiple());
			field.setValorMostrarMultiple(dadaIndex.getValorMostrarMultiple());
			field.setValorOrdreMultiple(dadaIndex.getValorIndexMultiple());
			if (dadaIndex.isOrdenarPerValorMostrar())
				field.setValorOrdreMultiple(dadaIndex.getValorMostrarMultiple());
			else
				field.setValorOrdreMultiple(dadaIndex.getValorIndexMultiple());
		}
		field.setMultiple(dadaIndex.isMultiple());
		return field;
	}

	private PaginatedList getPaginaExpedients(
			Long entornId,
			Long consultaId,
			Map<String, Object> valors,
			String page,
			String sort,
			String dir,
			String objectsPerPage,
			boolean export) {
		int maxResults = getObjectsPerPage(objectsPerPage);
		int pagina = (page != null) ? new Integer(page).intValue() : 1;
		int firstRow = (pagina - 1) * maxResults;
		boolean isAsc = (dir == null) || "asc".equals(dir);
		// Si no s'especifica columna per cercar ordena en sentit descendent
		if (sort == null || sort.length() == 0)
			isAsc = false;
		//
		PaginatedList paginatedList = new PaginatedList();
		int fullsize = expedientService.countAmbEntornConsultaDisseny(
				entornId,
				consultaId,
				valors); 
		paginatedList.setFullListSize(fullsize);
		paginatedList.setObjectsPerPage(export? fullsize : maxResults);
		paginatedList.setPageNumber(export? 1 : pagina);
		String sortOk = (sort != null && sort.length() > 0) ? sort : "dataInici";
		paginatedList.setSortCriterion(sortOk);
		paginatedList.setSortDirection((isAsc) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING);
		List<ExpedientConsultaDissenyDto> dadesExpedients = expedientService.findAmbEntornConsultaDisseny(
				entornId,
				consultaId,
				valors,
				sort,
				isAsc,
				(export? 0 : firstRow),
				(export? fullsize : maxResults));
		paginatedList.setList(dadesExpedients);
		// ------ //
		/*for (ExpedientConsultaDissenyDto dadesExpedient: dadesExpedients) {
			mostrarDadesExpedient(dadesExpedient);
		}*/
		// ------ //
		return paginatedList;
	}
	
	
	
	/*private void mostrarDadesExpedient(ExpedientConsultaDissenyDto dadesExpedient) {
		System.out.println(">>>>>> (" + dadesExpedient.getExpedient().getId() + ") " + dadesExpedient.getExpedient().getIdentificador());
		if (dadesExpedient.getDadesExpedient() != null) {
			for (String clau: dadesExpedient.getDadesExpedient().keySet()) {
				DadaIndexadaDto dada = dadesExpedient.getDadesExpedient().get(clau);
				System.out.println(">>> " + dada.getCampCodi() + ": (" + dada.getValorIndex() + ") " + dada.getValorMostrar());
			}
		}
	}*/
	
}
