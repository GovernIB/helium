/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientLogDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.model.service.TerminiService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.InformacioRetroaccioDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.TramitacioException;
import net.conselldemallorca.helium.v3.core.api.exception.TramitacioHandlerException;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;



/**
 * Controlador per la gestió d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedient")
public class ExpedientController extends BaseController {

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private TerminiService terminiService;
	private PluginService pluginService;
	private PermissionService permissionService;
	private AdminService adminService;



	@Autowired
	public ExpedientController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			TerminiService terminiService,
			PluginService pluginService,
			PermissionService permissionService,
			AdminService adminService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.terminiService = terminiService;
		this.pluginService = pluginService;
		this.permissionService = permissionService;
		this.adminService = adminService;
	}

	@RequestMapping(value = "llistat")
	public String llistat(
			HttpServletRequest request,
			ModelMap model) {
		final Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<ExpedientTipus> tipus = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
			permissionService.filterAllowed(
					tipus,
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.READ});
			final List<ExpedientDto> expedients = expedientService.findAmbEntorn(entorn.getId());
			Iterator<ExpedientDto> it = expedients.iterator();
			while (it.hasNext()) {
				final ExpedientDto expedient = it.next();
				if (!tipus.contains(expedient.getTipus()))
					it.remove();
			}
			model.addAttribute("llistat", expedients);
			return "expedient/llistat";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "delete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.getById(id);
			if (potEsborrarExpedient(expedient)) {
				try {
					expedientService.delete(entorn.getId(), id);
					missatgeInfo(request, getMessage("info.expedient.esborrat") );
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.esborrar.expedient"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el registre", ex);
				}
			} else {
				missatgeError(request, getMessage("error.permisos.esborrar.expedient"));				
			}
			return "redirect:/expedient/consulta.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "anular")
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
			return "redirect:/expedient/consulta.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	
	@RequestMapping(value = "desanular")
	public String desanular(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.getById(id);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.desanular(entorn.getId(), id);
					missatgeInfo(request, getMessage("info.expedient.desanulat"));
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.activar.expedient"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut activar el registre", ex);
				}
				return "redirect:/expedient/consulta.html";
			} else {
				missatgeError(request, getMessage("error.permisos.desanular.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	

	@RequestMapping(value = "info")
	public String info(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				adminService.mesuraTemporalIniciar("Expedient INFORMACIO", "expedient", expedient.getTipus().getNom());
				model.addAttribute(
						"expedient",
						expedient);
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(
						id,
						true,
						false,
						false);
				List<Accio> accions = dissenyService.findAccionsVisiblesAmbDefinicioProces(instanciaProces.getDefinicioProces().getId());
				// Filtra les accions sense permisos per a l'usuari actual
				Iterator<Accio> it = accions.iterator();
				while (it.hasNext()) {
					Accio accio = it.next();
					String rols = accio.getRols();
					if (rols != null && rols.length() > 0) {
						boolean permesa = false;
						String[] llistaRols = rols.split(",");
						for (String rol: llistaRols) {
							if (request.isUserInRole(rol)) {
								permesa = true;
								break;
							}
						}
						if (!permesa)
							it.remove();
					}
				}
				model.addAttribute("accions", accions);
				model.addAttribute(
						"instanciaProces",
						instanciaProces);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id));
				if (instanciaProces.isImatgeDisponible()) {
					model.addAttribute(
							"activeTokens",
							expedientService.getActiveTokens(id, true));
				}
				adminService.mesuraTemporalCalcular("Expedient INFORMACIO", "expedient", expedient.getTipus().getNom());
				try {
					model.addAttribute(
							"relacionarCommand",
							new ExpedientRelacionarCommand());
					return "expedient/info";					
				} catch (Exception ex) {
					logger.error("S'ha produït un error processant la seva petició", ex);
					missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
					return "redirect:/tasca/personaLlistat.html";
				}				
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "dades")
	public String dades(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "ambTasques", required = false) Boolean ambTasques,
			@RequestParam(value = "ambOcults", required = false) Boolean ambOcults,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				adminService.mesuraTemporalIniciar("Expedient DADES", "expedient", expedient.getTipus().getNom());
				model.addAttribute(
						"expedient",
						expedient);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id));
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, false, true, false));
				if (ambTasques != null && ambTasques.booleanValue()) {
					model.addAttribute(
							"tasques",
							expedientService.findTasquesPerInstanciaProces(id, true));
				}
				model.addAttribute("ambOcults", ambOcults == null ? false : ambOcults);
				adminService.mesuraTemporalCalcular("Expedient DADES", "expedient", expedient.getTipus().getNom());
				return "expedient/dades";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "documents")
	public String documents(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "ambTasques", required = false) Boolean ambTasques,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				adminService.mesuraTemporalIniciar("Expedient DOCUMENTS", "expedient", expedient.getTipus().getNom());
				model.addAttribute(
						"expedient",
						expedient);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id));
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, false, false, true));
				model.addAttribute(
						"portasignaturesPendent",
						expedientService.findDocumentsPendentsPortasignatures(id));
				if (ambTasques != null && ambTasques.booleanValue()) {
					model.addAttribute(
							"tasques",
							expedientService.findTasquesPerInstanciaProces(id, true));
				}
				adminService.mesuraTemporalCalcular("Expedient DOCUMENTS", "expedient", expedient.getTipus().getNom());
				return "expedient/documents";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "documentPsignaReintentar")
	public String documentPsignaReintentar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "psignaId", required = true) Integer psignaId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				if (pluginService.processarDocumentPendentPortasignatures(psignaId))
					missatgeInfo(request, getMessage("expedient.psigna.reintentar.ok"));
				else
					missatgeError(request, getMessage("expedient.psigna.reintentar.error"));
				return "redirect:/expedient/documents.html?id=" + id;
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "timeline")
	public String timeline(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				model.addAttribute(
						"expedient",
						expedient);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id));
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, false, false, false));
				return "expedient/timeline";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "timelineXml")
	public String timelineXml(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, false, false, false));
				model.addAttribute(
						"terminisIniciats",
						terminiService.findIniciatsAmbProcessInstanceId(id));
				return "expedient/timelineXml";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "tasques")
	public String tasques(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				adminService.mesuraTemporalIniciar("Expedient TASQUES", "expedient", expedient.getTipus().getNom());
				model.addAttribute(
						"expedient",
						expedient);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id));
//				model.addAttribute(
//						"instanciaProces",
//						expedientService.getInstanciaProcesById(id, false, false, false, false));
				List<TascaDto> tasques = expedientService.findTasquesPerInstanciaProces(id, false);
				Collections.sort(tasques, new Comparator<TascaDto>() {
					public int compare(TascaDto t1, TascaDto t2) {
						return new Long(t1.getId()).compareTo(new Long(t2.getId()));
					}
				});
				List<String> tasquesId = new ArrayList<String>();
				for (TascaDto tasca: tasques){
					tasquesId.add(tasca.getId());
				}
				model.addAttribute("expedientLogIds", tasquesId.isEmpty() ? null : expedientService.findLogIdTasquesById(tasquesId));
				model.addAttribute(
						"tasques",
						tasques);
				adminService.mesuraTemporalCalcular("Expedient TASQUES", "expedient", expedient.getTipus().getNom());
				return "expedient/tasques";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "imatgeProces")
	public String imatgeProces(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, true, false, false);
				if (instanciaProces.isImatgeDisponible()) {
					String resourceName = "processimage.jpg";
					model.addAttribute(
							ArxiuView.MODEL_ATTRIBUTE_FILENAME,
							resourceName);
					model.addAttribute(
							ArxiuView.MODEL_ATTRIBUTE_DATA,
							dissenyService.getImatgeDefinicioProces(
									instanciaProces.getDefinicioProces().getId()));
					return "arxiuView";
				} else {
					missatgeError(request, getMessage("error.info.expedient.imatgeproces"));
					return "redirect:/expedient/consulta.html";
				}
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "registre")
	public String registre(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "tipus_retroces", required = false) Integer tipus_retroces,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				adminService.mesuraTemporalIniciar("Expedient REGISTRE", "expedient", expedient.getTipus().getNom());
				model.addAttribute(
						"expedient",
						expedient);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id));
				List<ExpedientLogDto> logs = null;
				if (tipus_retroces == null || tipus_retroces != 0) {
					logs = expedientService.getLogsPerTascaOrdenatsPerData(expedient, id);
				} else {
					logs = expedientService.getLogsOrdenatsPerData(expedient, id);
				}
				if (logs == null || logs.isEmpty()) {
					model.addAttribute(
							"registre",
							expedientService.getRegistrePerExpedient(expedient.getId()));
					adminService.mesuraTemporalCalcular("Expedient REGISTRE", "expedient", expedient.getTipus().getNom());
					return "expedient/registre";
				} else {
					// Llevam els logs retrocedits
					Iterator<ExpedientLogDto> itLogs = logs.iterator();
					while (itLogs.hasNext()) {
						ExpedientLogDto log = itLogs.next();
						if ("RETROCEDIT".equals(log.getEstat()) || "RETROCEDIT_TASQUES".equals(log.getEstat()) || ("EXPEDIENT_MODIFICAR".equals(log.getAccioTipus()) && (tipus_retroces == null || tipus_retroces != 0)))
							itLogs.remove();
					}
					model.addAttribute("logs", logs);
					model.addAttribute(
							"tasques",
							expedientService.getTasquesPerLogExpedient(expedient.getId()));
					adminService.mesuraTemporalCalcular("Expedient REGISTRE", "expedient", expedient.getTipus().getNom());
					return "expedient/log";
				}
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "logRetrocedit")
	public String logRetrocedit(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "logId", required = true) Long logId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, false, false, false));
				List<InformacioRetroaccioDto> logs = expedientService.findLogsRetroceditsOrdenatsPerData(logId);
				model.addAttribute("logs", logs);
				model.addAttribute(
						"tasques",
						expedientService.getTasquesPerLogExpedient(expedient.getId()));
				return "expedient/logRetrocedit";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	@RequestMapping(value = "logAccionsTasca")
	public String logAccionsTasca(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "targetId", required = true) Long targetId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, false, false, false));
				List<ExpedientLogDto> logs = expedientService.findLogsTascaOrdenatsPerData(targetId);
				model.addAttribute("logs", logs);
				model.addAttribute(
						"tasques",
						expedientService.getTasquesPerLogExpedient(expedient.getId()));
				return "expedient/logRetrocedit";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "accio")
	public String accio(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "submit", required = true) String jbpmAction,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			boolean permesa = false;
			Accio accio = expedientService.getAccio(id, jbpmAction);
			if (accio.getRols() == null || accio.getRols().length() == 0) {
				permesa = true;
			} else {
				String[] llistaRols = accio.getRols().split(",");
				for (String rol: llistaRols) {
					if (request.isUserInRole(rol)) {
						permesa = true;
						break;
					}
				}
			}
			if (permesa) {
				if (accio.isPublica() || potModificarExpedient(expedient)) {
					try {
						expedientService.executarAccio(id, jbpmAction);
						missatgeInfo(request, getMessage("info.accio.executat"));
					} catch (PermisDenegatException ex) {
						missatgeError(
								request, 
								getMessage("error.executar.accio") + ": " + getMessage("error.permisos.modificar.expedient"));
						logger.error(getMessage("error.executar.accio") +" "+ accio.getId() + ": "+ ex.getLocalizedMessage(), ex);
					} catch (TramitacioException ex) {
						String nomAccio = accio.getId().toString();
						nomAccio = accio.getNom();
						missatgeError(
				    			request,
				    			getMessage("error.executar.accio") + " " + nomAccio + ": " + ex.getMessage());
						logger.error(getMessage("error.executar.accio") +" "+ accio.getId() + ": "+ ex.getLocalizedMessage(), ex);
					} catch (TramitacioHandlerException ex) {
						String nomAccio = accio.getId().toString();
						nomAccio = accio.getNom();
						missatgeError(
				    			request,
				    			getMessage("error.executar.accio") + " " + nomAccio + ": " + ex.getMessage());
						logger.error(getMessage("error.executar.accio") +" "+ accio.getId() + ": "+ ex.getLocalizedMessage(), ex);
					}
					return "redirect:/expedient/info.html?id=" + id;
				} else {
					missatgeError(request, getMessage("error.permisos.modificar.expedient"));
					return "redirect:/expedient/consulta.html";
				}
			} else {
				missatgeError(request, getMessage("error.accio.no.permesa"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "retrocedir")
	public String retrocedir(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "tipus_retroces", required = false) Integer tipus_retroces,
			@RequestParam(value = "logId", required = true) Long logId,
			@RequestParam(value = "retorn", required = true) String retorn) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				if (tipus_retroces == null || tipus_retroces != 0) {
					expedientService.retrocedirFinsLog(logId, true);
				} else {
					expedientService.retrocedirFinsLog(logId, false);
				}
			}catch (JbpmException ex ) {
				Long entornId = entorn.getId();
				String numeroExpedient = id;
				missatgeError(request, getMessage("error.executar.retroces") + ": "+ ex.getCause().getMessage());
				logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+" No s'ha pogut executar el retrocés", ex);
			}
			
			if("t".equals(retorn)){
				return "redirect:/expedient/tasques.html?id=" + id;
			}else{
				return "redirect:/expedient/registre.html?id=" + id;
			}
			
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	private boolean potConsultarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.SUPERVISION,
					ExtendedPermission.READ}) != null;
	}
	private boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}
	private boolean potEsborrarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DELETE}) != null;
	}
	
	/*private boolean isSignaturaFileAttached() {
		return "true".equalsIgnoreCase((String)GlobalProperties.getInstance().get("app.signatura.plugin.file.attached"));
	}*/

	private static final Log logger = LogFactory.getLog(ExpedientController.class);

}
