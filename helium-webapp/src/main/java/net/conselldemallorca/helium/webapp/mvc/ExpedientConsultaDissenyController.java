package net.conselldemallorca.helium.webapp.mvc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.conselldemallorca.helium.core.model.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.AdminService;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.report.FieldValue;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.PaginatedList;
import net.conselldemallorca.helium.webapp.mvc.util.TascaFormUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	public static final String VARIABLE_SESSIO_SELCON_COMMAND = "expedientTipusConsultaDissenyCommand";
	public static final String VARIABLE_SESSIO_FILTRE_COMMAND = "expedientTipusConsultaFiltreCommand";
	public static final String VARIABLE_SESSIO_IDS_MASSIUS_TE = "consultaExpedientsIdsMassiusTE";
	public static final String VARIABLE_SESSIO_IDS_MASSIUS = "consultaExpedientsIdsMassius";
	public static final String VARIABLE_SESSIO_PARAMS = "consultaExpedientsParams";



	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;
	private AdminService adminService;
	
	@Autowired
	public ExpedientConsultaDissenyController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService,
			AdminService adminService) {
		this.dissenyService  = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
		this.adminService = adminService;
	}

	@ModelAttribute("commandSeleccioConsulta")
	public ExpedientConsultaDissenyCommand populateCommandSeleccioConsulta(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
			@RequestParam(value = "consultaId", required = false) Long consultaId,
			@RequestParam(value = "canviar", required = false) Boolean canviar){
		ExpedientConsultaDissenyCommand command = (ExpedientConsultaDissenyCommand)session.getAttribute(VARIABLE_SESSIO_SELCON_COMMAND);
		if (command == null) {
			command = new ExpedientConsultaDissenyCommand();
		}
		if(expedientTipusId != null){
			command.setExpedientTipusId(expedientTipusId); 
		}
		if (canviar != null && canviar.booleanValue()) {
		command.setConsultaId(consultaId);
		if (consultaId != null && expedientTipusId == null) {
			command.setExpedientTipusId(
					dissenyService.getConsultaById(consultaId).getExpedientTipus().getId());
			}
			session.removeAttribute(VARIABLE_SESSIO_FILTRE_COMMAND);
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

	
	@RequestMapping(value = "/expedient/dissenyAnular")
	public String anular(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id, @RequestParam(value = "motiu", required = true) String motiu) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.getById(id);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.anular(entorn.getId(), id, motiu);
					missatgeInfo(request, getMessage("info.expedient.anulat") );
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.anular.expedient"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el registre", ex);
				}
			} else {
				missatgeError(request, getMessage("error.permisos.anular.expedient"));				
			}
			return "redirect:/expedient/consultaDisseny.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
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
				Consulta consulta = null;
				if (MesuresTemporalsHelper.isActiu()) {
					consulta = dissenyService.getConsultaById(commandSeleccio.getConsultaId());
					adminService.getMesuresTemporalsHelper().mesuraIniciar("INFORME: " + consulta.getCodi(), "report", null, null, "LLISTAT");
				}
				model.addAttribute("expedientTipusId", commandSeleccio.getExpedientTipusId());
				List<Camp> camps = dissenyService.findCampsPerCampsConsulta(
						commandSeleccio.getConsultaId(),
						TipusConsultaCamp.FILTRE,
						true);
				Map<String, Object> valors = TascaFormUtil.getValorsFromCommand(
						camps,
						commandFiltre,
						true,
						true);
				boolean export = request.getParameter(TableTagParameters.PARAMETER_EXPORTING) != null;
				commandFiltre = TascaFormUtil.getCommandForFiltre(
						camps,
						valors,
						null,
						null);
				model.addAttribute("commandFiltre", commandFiltre);
				model.addAttribute(
						"expedients",
						getPaginaExpedients(
								entorn.getId(),
								commandSeleccio.getConsultaId(),
								getValorsPerService(camps, valors),
								page,
								sort,
								dir,
								objectsPerPage,
								export));
				if (MesuresTemporalsHelper.isActiu())
					adminService.getMesuresTemporalsHelper().mesuraCalcular("INFORME: " + consulta.getCodi(), "report", null, null, "LLISTAT");
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
//				commandSeleccio.setMassivaActiu(false);
				if (permissionService.filterAllowed(
						consulta.getExpedientTipus(),
						ExpedientTipus.class,
						new Permission[] {
							ExtendedPermission.ADMINISTRATION,
							ExtendedPermission.SUPERVISION,
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
				commandSeleccio.setMassivaActiu(false);
			
			} else if ("ejecucionMasivaTotsTipus".equals(submit)) {
				session.removeAttribute(VARIABLE_SESSIO_FILTRE_COMMAND);
				return "redirect:/expedient/massivaInfoTE.html?expedientTipusId="+commandSeleccio.getExpedientTipusId();
			}
			
			model.addAttribute("objectsPerPage", objectsPerPage);
			
			//***********************
			
//			if ("massiva".equals(submit)) {
//				session.setAttribute(VARIABLE_SESSIO_SELCON_COMMAND_TE, commandSeleccio);
//				commandSeleccio.setMassivaActiu(true);
//				
//			} else if ("nomassiva".equals(submit)) {
//				request.getSession().removeAttribute(ExpedientMassivaController.VARIABLE_SESSIO_IDS_MASSIUS_TE);
//				session.removeAttribute(VARIABLE_SESSIO_SELCON_COMMAND_TE);
//			}
//			else 
			if ("clean".equals(submit)) {
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

	@RequestMapping(value = "/expedient/consultaDissenyInformeParams", method = RequestMethod.POST)
	public String consultaDissenyInformeParams(
			HttpServletRequest request,
			HttpSession session,
			ModelMap model) {
		ExpedientConsultaDissenyCommand commandSeleccioConsulta =
				(ExpedientConsultaDissenyCommand)session.getAttribute(
						VARIABLE_SESSIO_SELCON_COMMAND);
		if (commandSeleccioConsulta != null && commandSeleccioConsulta.getConsultaId() != null) {
			List<ConsultaCamp> params = dissenyService.findCampsConsulta(
					commandSeleccioConsulta.getConsultaId(),
					TipusConsultaCamp.PARAM);
			Map<String, Object> valorsParams = new HashMap<String, Object>();
			for (ConsultaCamp param: params) {
				String valorRequest = request.getParameter(param.getCampCodi());
				Object paramValor = null;
				switch (param.getParamTipus()) {
				case TEXT:
					if (valorRequest != null && valorRequest.length() > 0)
						paramValor = valorRequest;
					break;
				case SENCER:
					if (valorRequest != null && valorRequest.length() > 0)
						paramValor = new Long(valorRequest);
					break;
				case FLOTANT:
					if (valorRequest != null && valorRequest.length() > 0)
						paramValor = new Double(valorRequest);
					break;
				case DATA:
					if (valorRequest != null && valorRequest.length() > 0) {
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						try {
							paramValor = sdf.parse(valorRequest);
						} catch (Exception ignored) {}
					}
					break;
				case BOOLEAN:
					paramValor = new Boolean(valorRequest != null);
					break;
				}
				if (paramValor != null)
					valorsParams.put(
							param.getCampCodi(),
							paramValor);
			}
			/*for (String clau: valorsParams.keySet())
				System.out.println(">>> PARAM " + clau + ": " + valorsParams.get(clau));*/
			session.setAttribute(VARIABLE_SESSIO_PARAMS, valorsParams);
		}
		return "redirect:consultaDissenyInforme.html";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/consultaDissenyInforme")
	public String consultaDissenyInforme(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipId", required = false) Long expedientTipId,
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
			if(commandSeleccio.getExpedientTipusId()==null){commandSeleccio.setExpedientTipusId(expedientTipId);}
			if (commandFiltre != null) {
				populateModelCommon(entorn, model, commandSeleccio);
				Consulta consulta = dissenyService.getConsultaById(commandSeleccio.getConsultaId());
				adminService.getMesuresTemporalsHelper().mesuraIniciar("INFORME: " + consulta.getCodi(), "report", null, null, "INFORME");
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
					List<ExpedientConsultaDissenyDto> expedients = expedientService.findAmbEntornConsultaDisseny(
							entorn.getId(),
							commandSeleccio.getConsultaId(),
							getValorsPerService(camps, valors),
							ExpedientCamps.EXPEDIENT_CAMP_ID,
							true);
					List<ExpedientConsultaDissenyDto> expedientsTE = new ArrayList<ExpedientConsultaDissenyDto>();
					if (ids.size() > 0) {
						for (int c = 0; c <= expedients.size() -1 ; c++) {
							for (int b = 1; b <= ids.size() - 1; b++) {
								if (expedients.get(c).getExpedient().getId().equals(ids.get(b))) {
									expedientsTE.add(expedients.get(c));
								}
							}
						}
					}
					if (expedientsTE.size() > 0) {
						model.addAttribute(
								JasperReportsView.MODEL_ATTRIBUTE_REPORTDATA,
								getDadesDatasource(expedientsTE));
					} else {
						model.addAttribute(
								JasperReportsView.MODEL_ATTRIBUTE_REPORTDATA,
								getDadesDatasource(expedients));
					}
					String extensio = consulta.getInformeNom().substring(
							consulta.getInformeNom().lastIndexOf(".") + 1);
					String formatExportacio = consulta.getFormatExport();
					request.setAttribute("formatJR", formatExportacio);
					String nom = consulta.getInformeNom().substring(0,
							consulta.getInformeNom().lastIndexOf("."));
					if ("zip".equals(extensio)) {
						HashMap<String, byte[]> reports = unZipReports(consulta
								.getInformeContingut());
						model.addAttribute(
								JasperReportsView.MODEL_ATTRIBUTE_REPORTCONTENT,
								reports.get(nom + ".jrxml"));
						reports.remove(nom + ".jrxml");
						model.addAttribute(
								JasperReportsView.MODEL_ATTRIBUTE_SUBREPORTS,
								reports);
						return "jasperReportsView";
					} else
						model.addAttribute(
								JasperReportsView.MODEL_ATTRIBUTE_REPORTCONTENT,
								consulta.getInformeContingut());
					model.addAttribute(
							JasperReportsView.MODEL_ATTRIBUTE_CONSULTA,
							consulta.getCodi());
					List<ConsultaCamp> params = dissenyService.findCampsConsulta(
							consulta.getId(),
							TipusConsultaCamp.PARAM);
					if (params != null && !params.isEmpty()) {
						model.addAttribute(
								JasperReportsView.MODEL_ATTRIBUTE_PARAMS,
								session.getAttribute(VARIABLE_SESSIO_PARAMS));
					}
					adminService.getMesuresTemporalsHelper().mesuraCalcular("INFORME: " + consulta.getCodi(), "report", null, null, "INFORME");
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
		binder.setAutoGrowNestedPaths(false);
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
				Termini.class,
				new TerminiTypeEditor());
	}

	public class ExpedientConsultaDissenyCommand {
		private Long expedientTipusId;
		private Long consultaId;
		private boolean massivaActiu = true;
		
		
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
		List<ExpedientTipus> tipus = dissenyService.findExpedientTipusAmbEntornOrdenat(entorn.getId(), "nom");
		permissionService.filterAllowed(
				tipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.SUPERVISION,
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
					List<ConsultaCamp> params = dissenyService.findCampsConsulta(
							commandSeleccio.getConsultaId(),
							TipusConsultaCamp.PARAM);
					model.addAttribute("paramsInforme", params);
					model.addAttribute(
							"paramsCommand",
							TascaFormUtil.getCommandForParams(
									params,
									null,
									null,
									null));
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
		iniciat.setId(Long.parseLong("0"));
		iniciat.setCodi("0");
		iniciat.setNom( getMessage("expedient.consulta.iniciat") );
		estats.add(0, iniciat);
		Estat finalitzat = new Estat();
		finalitzat.setId(Long.parseLong("-1"));
		finalitzat.setCodi("-1");
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
			if (dadaIndex.isOrdenarPerValorMostrar()){
				field.setValorOrdreMultiple(dadaIndex.getValorMostrarMultiple());
			}
			else
//			{
//				String valorsMult="[";
//				List<String> valorsMostrar = new ArrayList<String>();
//				for(Object valMult:dadaIndex.getValorMultiple()){
//					if(valMult instanceof String){
//						valorsMult+=(String)valMult;	
//					}
//				}
//				valorsMult = valorsMult.trim();
//				valorsMult = valorsMult+"]";
//				valorsMostrar.add(valorsMult);
//				field.setValorOrdreMultiple(valorsMostrar);
//				field.setValorMostrar(valorsMult);
//				field.setValorMostrarMultiple(valorsMostrar);
//
//			}
				
				
				
				
				
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
	
	
	public HashMap<String, byte[]> unZipReports(byte[] zipContent) {

		byte[] buffer = new byte[4096];
		HashMap<String, byte[]> docs = new HashMap<String, byte[]>();

		try {

			// get the zip file content
			ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(
					zipContent));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = ze.getName();
				byte[] fileContent;

				// System.out.print("\n >>>> "+ fileName);

				ByteArrayOutputStream fos = new ByteArrayOutputStream();

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				fileContent = fos.toByteArray();
				docs.put(fileName, fileContent);
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			// System.out.println("\nDone");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return docs;
	}

	public static Map<String, Object> getValorsPerService(
			List<Camp> camps,
			Map<String, Object> valors) {
		Map<String, Object> valorsPerService = new HashMap<String, Object>();
		for (Camp camp: camps) {
			String clau = TascaFormUtil.getCampCodi(
					camp,
					true,
					false);
			if (camp.getDefinicioProces() != null) {
				String definicioProcesKey = camp.getDefinicioProces().getJbpmKey();
				String clauPerService = definicioProcesKey + clau.substring(definicioProcesKey.length()).replaceFirst("_", ".");
				valorsPerService.put(
						clauPerService,
						valors.get(clau));
			} else {
				valorsPerService.put(
						clau,
						valors.get(clau));
			}
		}
		return valorsPerService;
	}
	private boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientConsultaDissenyController.class);
}
