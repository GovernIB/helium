/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.acls.model.Permission;
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
import org.springframework.web.multipart.MultipartFile;

import edu.emory.mathcs.backport.java.util.Arrays;
import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.exportacio.ExpedientTipusExportacio;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Reassignacio;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExecucioMassivaService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.PersonaService;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.model.service.ReassignacioService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;



/**
 * Controlador per la gestió de tipus d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientTipusController extends BaseController {

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;
	private PluginService pluginService;
	private ReassignacioService reassignacioService;
//	private Validator additionalValidator;
	private PersonaService  personaService;
	private ExecucioMassivaService execucioMassivaService;
	@Autowired
	public ExpedientTipusController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService,
			PluginService pluginService,
			ExecucioMassivaService execucioMassivaService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
		this.pluginService = pluginService;
		this.execucioMassivaService = execucioMassivaService;
	}
	
	public PersonaService getPersonaService() {
		return personaService;
	}

	public void setPersonaService(PersonaService personaService) {
		this.personaService = personaService;
	}

//	@Autowired
//	public Validator getAdditionalValidator() {
//		return additionalValidator;
//	}
//	@Autowired
//	public void setAdditionalValidator(Validator additionalValidator) {
//		this.additionalValidator = additionalValidator;
//	}

	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "expedientTipusId", required = false) Long id) {
		if (id != null) {
			return dissenyService.getExpedientTipusById(id);
		}
		return null;
	}

	@RequestMapping(value = "/expedientTipus/llistat")
	public String llistat(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute("llistat", llistatExpedientTipusAmbPermisos(entorn));
			model.addAttribute("command", new DeployCommand());
			return "expedientTipus/llistat";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/delete")
	public String delete(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(id);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				try {
					int numExpedients = expedientService.countAmbExpedientTipusId(id);
					if (numExpedients == 0) {
						dissenyService.deleteExpedientTipus(id);
						missatgeInfo(request, getMessage("info.tipus.exp.esborrat") );
					} else {
						missatgeError(request, getMessage("error.exist.exp.tipexp") );
					}
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.esborrar.tipus.exp"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el tipus d'expedient", ex);
				}
				return "redirect:/expedientTipus/llistat.html";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/info")
	public String info(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(id);
			if (potDissenyarExpedientTipus(entorn, expedientTipus) || potGestionarExpedientTipus(entorn, expedientTipus)) {
				model.addAttribute("command", new DeployCommand());
				model.addAttribute(
						"responsableDefecte",
						getResponsableDefecte(expedientTipus.getResponsableDefecteCodi()));
				model.addAttribute(
						"definicioProcesInicial",
						dissenyService.findDarreraDefinicioProcesForExpedientTipus(id, false));
				return "expedientTipus/info";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/exportar")
	public String export(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				String filename = expedientTipus.getJbpmProcessDefinitionKey();
				if (filename == null) {
					filename = expedientTipus.getCodi();
				}
				filename = filename + ".exp";
				model.addAttribute("filename", filename);
				model.addAttribute("data", dissenyService.exportarExpedientTipus(expedientTipusId));
				return "serialitzarView";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/importar")
	public String importar(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam("arxiu") final MultipartFile multipartFile,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				if (multipartFile.getBytes() == null || multipartFile.getBytes().length == 0) {
					missatgeError(request, getMessage("error.especificar.arxiu.importar") );
					if (expedientTipusId != null) {
						return "redirect:/expedientTipus/info.html?expedientTipusId=" + expedientTipusId;
					} else {
						return "redirect:/expedientTipus/llistat.html";
					}
				}
				InputStream is = new ByteArrayInputStream(multipartFile.getBytes());
		    	ObjectInputStream input = new ObjectInputStream(is);
		    	Object deserialitzat = input.readObject();
		    	if (deserialitzat instanceof ExpedientTipusExportacio) {
		    		ExpedientTipusExportacio exportacio = (ExpedientTipusExportacio)deserialitzat;
		    		dissenyService.importarExpedientTipus(
		        			entorn.getId(), 
		        			expedientTipusId,
		        			exportacio);
		    		missatgeInfo(request, getMessage("info.dades.importat") );
		    		if (expedientTipusId != null) {
						return "redirect:/expedientTipus/info.html?expedientTipusId=" + expedientTipusId;
					} else {
						return "redirect:/expedientTipus/llistat.html";
					}
		    	} else {
		    		missatgeError(request, getMessage("error.arxiu.no.valid") );
		    	}
			} catch (IOException ex) {
				logger.error("Error llegint l'arxiu a importar", ex);
				missatgeError(request, getMessage("error.arxiu.importar") );
			} catch (ClassNotFoundException ex) {
				logger.error("Error llegint l'arxiu a importar", ex);
				missatgeError(request, getMessage("error.arxiu.importar") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.import.dades") + ex.getMessage());
			}
			if (expedientTipusId != null) {
				return "redirect:/expedientTipus/info.html?expedientTipusId=" + expedientTipusId;
			} else {
				return "redirect:/expedientTipus/llistat.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/definicioProcesLlistat")
	public String definicioProcesLlistat(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(id);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				model.addAttribute(
						"llistat",
						dissenyService.findDarreresAmbExpedientTipusEntorn(entorn.getId(), id, true));
				return "expedientTipus/definicioProcesLlistat";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/definicioProcesInicial")
	public String definicioProcesInicial(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long id,
			@RequestParam(value = "jbpmKey", required = true) String jbpmKey,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(id);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				try {
					dissenyService.setDefinicioProcesInicialPerExpedientTipus(id, jbpmKey);
					missatgeInfo(request, getMessage("info.defproc.marcat.inicial", new Object[] {jbpmKey} ) );
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.configurar.defproc.inicial"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut configurar la definició de procés inicial", ex);
				}
				return "redirect:/expedientTipus/definicioProcesLlistat.html?expedientTipusId=" + id;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/definicioProcesEsborrar")
	public String definicioProcesEsborrar(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(id);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
				if (potDissenyarDefinicioProces(entorn, definicioProces)) {
					try {
						List<ExpedientDto> expedients = expedientService.findAmbDefinicioProcesId(definicioProcesId);
						if (expedients.size() == 0) {
							dissenyService.undeploy(entorn.getId(), null, definicioProcesId);
				        	missatgeInfo(request, getMessage("info.defproc.esborrat") );
						} else {
							missatgeError(request, getMessage("error.exist.exp.defproc") );
						}
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut esborrar la definició de procés", ex);
			        	return "redirect:/definicioProces/info.html?definicioProcesId=" + definicioProcesId;
			        }
					return "redirect:/expedientTipus/definicioProcesLlistat.html?expedientTipusId=" + id;
				} else {
					missatgeError(request, getMessage("error.permisos.disseny.defproc"));
					return "redirect:/index.html";
				}
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/netejar_df", method = RequestMethod.GET)
	public String netejar_df(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		model.addAttribute("expedientTipusId", id);
		model.addAttribute("llistat", dissenyService.findDefinicionsProcesNoUtilitzadesExpedientTipus(id));
		return "/expedientTipus/llistatDpNoUs";
	}
	
	@RequestMapping(value = "/expedientTipus/netejar_df", method = RequestMethod.POST)
	public String netejar_df(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "dpId", required = false) Long[] dpId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
//		ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
		List<Long> dfBorrar = new ArrayList<Long>();
		for (Long definicioProcesId : dpId) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			try {
				List<Consulta> consultes = dissenyService.findConsultesAmbEntorn(entorn.getId());
				boolean esborrar = true;
				if (consultes.isEmpty()) {
					if (!dfBorrar.contains(definicioProcesId)) {
						dfBorrar.add(definicioProcesId);
					}
				} else {
					for(Consulta consulta: consultes){
						Set<ConsultaCamp> llistat = consulta.getCamps();
						for(ConsultaCamp c: llistat){
							if((definicioProces.getVersio() == c.getDefprocVersio()) && (definicioProces.getJbpmKey().equals(c.getDefprocJbpmKey()))){
								esborrar = false;
							}
						}
						if(!esborrar){
							missatgeError(request, getMessage("error.exist.cons.df", new Object[]{consulta.getNom(), definicioProces.getJbpmName(), definicioProces.getVersio()}) );
						} else {
							if (!dfBorrar.contains(definicioProcesId)) {
								dfBorrar.add(definicioProcesId);
							}
						}
					}
				}
				if (dfBorrar.contains(definicioProcesId)) {
					try {
						dissenyService.undeploy(entorn.getId(), expedientTipusId, definicioProcesId);
						missatgeInfo(request, getMessage("info.defproc.esborrada", new Object[]{definicioProces.getJbpmName(), definicioProces.getVersio()}) );
					} catch (Exception ex) {
						if (ex instanceof DataIntegrityViolationException || ex.getCause() instanceof DataIntegrityViolationException || (ex.getCause() != null && ex.getCause().getCause() instanceof DataIntegrityViolationException) ||
							ex instanceof ConstraintViolationException || ex.getCause() instanceof ConstraintViolationException || (ex.getCause() != null && ex.getCause().getCause() instanceof ConstraintViolationException)) {
							String msg = (ex instanceof DataIntegrityViolationException || ex instanceof ConstraintViolationException) ? getErrorMsg(ex) : 
										  (ex.getCause() instanceof DataIntegrityViolationException || ex.getCause() instanceof ConstraintViolationException) ? getErrorMsg(ex.getCause()) : getErrorMsg(ex.getCause().getCause());
							if (msg.contains("HELIUM.FK_TASKINST_TASK"))
								msg = getMessage("error.defpro.eliminar.constraint.taskinstance");
							missatgeError(request, getMessage("error.defpro.eliminar.constraint", new Object[] {definicioProces.getJbpmName(), definicioProces.getVersio()}), msg);
						} else { 
							missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
						}
						logger.error("No s'han pogut esborrar les definicions de procés", ex);
					}
				}
	        } catch (Exception ex) {
	        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
	        	logger.error("No s'han pogut esborrar les definicions de procés", ex);
	        }
		}
		
//		try {
//			if (!dfBorrar.isEmpty()) {
//				dissenyService.undeploy(entorn.getId(), dfBorrar);
//				if (msg.length() > 0) 
//					msg = msg.substring(0, msg.length() - 2);
//				missatgeInfo(request, getMessage("info.defproc.esborrades", new Object[]{msg}) );
//			}
//		} catch (Exception ex) {
//			if (ex instanceof DataIntegrityViolationException || ex.getCause() instanceof DataIntegrityViolationException) {
//				missatgeError(request, getMessage("error.defpro.eliminar.constraint"));
//			} else {
//				missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
//			}
//			logger.error("No s'han pogut esborrar les definicions de procés", ex);
//		}
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("llistat", dissenyService.findDefinicionsProcesNoUtilitzadesExpedientTipus(expedientTipusId));
		return "/expedientTipus/llistatDpNoUs";
	}
	
	private String getErrorMsg(Throwable ex) {
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		String errorFull = errors.toString();	
		errorFull = errorFull.replace("'", "&#8217;").replace("\"", "&#8220;").replace("\n", "<br>").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		errorFull = StringEscapeUtils.escapeJavaScript(errorFull);
		return errorFull;
	}
	@RequestMapping(value = "/expedientTipus/afectats_df", method = RequestMethod.GET)
	public String afectats_df(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			ModelMap model) {
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("definicioProcesId", definicioProcesId);
		DefinicioProcesDto definicioProces = dissenyService.getById(definicioProcesId, false);
		model.addAttribute("definicioProces", definicioProces);
		model.addAttribute("llistat", dissenyService.findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(expedientTipusId, Long.parseLong(definicioProces.getJbpmId())));
		return "/expedientTipus/llistatExpedientsDpNoUs";
	}
	
	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value = "/expedientTipus/canvi_versio", method = RequestMethod.POST)
	public String canvi_versio(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "novaDefinicioProcesId", required = true) Long novaDefinicioProcesId,
			@RequestParam(value = "expedientId", required = false) Long[] expedientId,
			ModelMap model) {
		
		if (expedientId == null || expedientId.length == 0) {
			missatgeError(request, getMessage("error.no.exp.selec"));
		} else {
			try {
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(new Date());
				dto.setEnviarCorreu(false);
				
				dto.setExpedientIds(Arrays.asList(expedientId));
				dto.setExpedientTipusId(expedientTipusId);
				dto.setTipus(ExecucioMassivaTipus.ACTUALITZAR_VERSIO_DEFPROC);
				
				Object[] params = new Object[3];
				params[0] = novaDefinicioProcesId;
				params[1] = null;
				dto.setParam2(execucioMassivaService.serialize(params));

				execucioMassivaService.crearExecucioMassiva(dto);
				missatgeInfo(request, getMessage("info.canvi.versio.massiu", new Object[] {expedientId.length}));
			} catch (Exception e) {
				missatgeError(request, getMessage("error.no.massiu"));
				logger.error("Error al programar les accions massives", e);
			}
		}
		
		return "redirect:/expedientTipus/afectats_df.html?expedientTipusId=" + expedientTipusId + "&definicioProcesId=" + definicioProcesId;
		
	}
	
	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value = "/expedientTipus/borrar_logs", method = RequestMethod.POST)
	public String borrar_logs(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "expedientId", required = false) Long[] expedientId,
			ModelMap model) {
		if (expedientId == null || expedientId.length == 0) {
			missatgeError(request, getMessage("error.no.exp.selec"));
		} else {
			ExecucioMassivaDto dto = new ExecucioMassivaDto();
			dto.setDataInici(new Date());
			dto.setEnviarCorreu(false);
			dto.setExpedientIds(Arrays.asList(expedientId));
			dto.setExpedientTipusId(expedientTipusId);
			dto.setTipus(ExecucioMassivaTipus.BUIDARLOG);
			try {
				execucioMassivaService.crearExecucioMassiva(dto);
				missatgeInfo(request, getMessage("info.buidarlog.massiu.executat", new Object[] {expedientId.length}));
			} catch (Exception e) {
				missatgeError(request, getMessage("error.no.massiu"));
				logger.error("Error al programar les accions massives", e);
			}
		}
		
		return "redirect:/expedientTipus/afectats_df.html?expedientTipusId=" + expedientTipusId + "&definicioProcesId=" + definicioProcesId;
	}
	
	@Autowired
	public ReassignacioService getReassignacioService() {
		return reassignacioService;
	}
	@Autowired
	public void setReassignacioService(ReassignacioService reassignacioService) {
		this.reassignacioService = reassignacioService;
	}

	
	@RequestMapping(value = "/expedientTipus/modificar", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) Long id,
			@ModelAttribute("command") ReassignacioCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
			List<Reassignacio> reassignacions = reassignacioService.llistaReassignacionsMod(id);
			Long id2 = reassignacions.get(0).getTipusExpedientId();
			model.addAttribute("llistat", reassignacions);
			return "redirect:/expedientTipus/redireccioLlistat.html?expedientTipusId="+id2;
	}
	
	
	@RequestMapping(value = "/expedientTipus/redireccioLlistat", method = RequestMethod.GET)
	public String reassignarGet(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit, 
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
			@RequestParam(value = "id", required = false) String id,
			@ModelAttribute("command") ReassignacioCommand command,
			ModelMap model) {
		
		if("cancel".equals(submit)){
			return "expedientTipus/redireccioLlistat";
		}
		Set<PersonaDto> destinataris =  personaService.findPersonesAmbPermisosPerExpedientTipus(expedientTipusId.longValue());
		model.addAttribute(
				 "destinataris",
				 destinataris);
		
		
		List<Reassignacio> reassignacions = reassignacioService.llistaReassignacions(expedientTipusId);
		model.addAttribute("llistat", reassignacions);
		return "expedientTipus/redireccioLlistat";
	}
	
	
	
	@RequestMapping(value = "/expedientTipus/redireccioLlistat", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "expedientTipusId", required = false) Long id,
			@ModelAttribute("command") ReassignacioCommand command,
			BindingResult result,
			SessionStatus status) {
			if(id!=null){
				ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(id);
				command.setTipusExpedientId(expedientTipus.getId());
			}
		if ("submit".equals(submit) || submit.length() == 0) {
//			additionalValidator.validate(command, result);
//	        if (result.hasErrors()) {
//	        	return "expedientTipus/redireccioLlistat";
//	        }
			Set<PersonaDto> destinataris =  personaService.findPersonesAmbPermisosPerExpedientTipus(id);
			request.setAttribute(
					 "destinataris",
					 destinataris);
	        try {
	        	if (command.getId() == null) {
	        		reassignacioService.createReassignacio(
	        				command.getUsuariOrigen(),
	        				command.getUsuariDesti(),
	        				command.getDataInici(),
	        				command.getDataFi(),
	        				command.getDataCancelacio(),
	        				command.getTipusExpedientId());
	        	} else {
	        		reassignacioService.updateReassignacio(
	        				command.getId(),
	        				command.getUsuariOrigen(),
	        				command.getUsuariDesti(),
	        				command.getDataInici(),
	        				command.getDataFi(),
	        				command.getDataCancelacio(),
	        				command.getTipusExpedientId());
	        	}
	        	missatgeInfo(request, getMessage("info.reassignacio.produit") );
	        	status.setComplete();
	        } catch (Exception ex) {
	        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut guardar el registre", ex);
	        	return "expedientTipus/redireccioLlistat";
	        }
	        return "redirect:/expedientTipus/redireccioLlistat.html?expedientTipusId="+command.getTipusExpedientId();
		}else if("cancel".equals(submit)){
			return "redirect:/expedientTipus/llistat.html";
		}
		else {
			return "redirect:/expedientTipus/redireccioLlistat.html?expedientTipusId="+command.getTipusExpedientId();
		}
	}
	
	
	@RequestMapping(value = "/expedientTipus/cancelar")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "expedientTipusId", required = false) String expedientTipusId) {
		reassignacioService.deleteReassignacio(id);
		missatgeInfo(request, getMessage("info.reassignacio.cancelat") );
		return "redirect:/expedientTipus/redireccioLlistat.html?expedientTipusId="+expedientTipusId;
	}
	

	private PersonaDto getResponsableDefecte(String codi) {
		if (codi == null)
			return null;
		return pluginService.findPersonaAmbCodi(codi);
	}

	private List<ExpedientTipus> llistatExpedientTipusAmbPermisos(Entorn entorn) {
		List<ExpedientTipus> resposta = new ArrayList<ExpedientTipus>();
		List<ExpedientTipus> llistat = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
		for (ExpedientTipus expedientTipus: llistat) {
			if (potDissenyarExpedientTipus(entorn, expedientTipus) || potGestionarExpedientTipus(entorn, expedientTipus))
				resposta.add(expedientTipus);
		}
		return resposta;
	}

	private boolean potDissenyarExpedientTipus(Entorn entorn, ExpedientTipus expedientTipus) {
		if (potDissenyarEntorn(entorn))
			return true;
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}
	private boolean potGestionarExpedientTipus(Entorn entorn, ExpedientTipus expedientTipus) {
		if (potDissenyarEntorn(entorn))
			return true;
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.MANAGE}) != null;
	}
	private boolean potDissenyarDefinicioProces(Entorn entorn, DefinicioProcesDto definicioProces) {
		if (potDissenyarEntorn(entorn))
			return true;
		if (definicioProces.getExpedientTipus() != null) {
			return permissionService.filterAllowed(
					definicioProces.getExpedientTipus(),
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.DESIGN}) != null;
		}
		return false;
	}
	private boolean potDissenyarEntorn(Entorn entorn) {
		return permissionService.filterAllowed(
				entorn,
				Entorn.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}

	public class DeployCommand {
		private Long expedientTipusId;
		private byte[] arxiu;
		public Long getExpedientTipusId() {
			return expedientTipusId;
		}
		public void setExpedientTipusId(Long expedientTipusId) {
			this.expedientTipusId = expedientTipusId;
		}
		public byte[] getArxiu() {
			return arxiu;
		}
		public void setArxiu(byte[] arxiu) {
			this.arxiu = arxiu;
		}
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusController.class);

}
