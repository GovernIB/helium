/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Tasca.TipusTasca;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.core.model.service.DocumentService;
import net.conselldemallorca.helium.core.model.service.ExecucioMassivaService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.TascaFormUtil;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.acls.Permission;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import edu.emory.mathcs.backport.java.util.Collections;


/**
 * Controlador per la tramitació massiva d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientMassivaController extends BaseController {

	public static final String VARIABLE_SESSIO_IDS_MASSIUS = "consultaExpedientsIdsMassius";
	public static final String VARIABLE_SESSIO_IDS_MASSIUS_TE = "consultaExpedientsIdsMassiusTE";

	private ExecucioMassivaService execucioMassivaService;
	private ExpedientService expedientService;
	private PermissionService permissionService;
	private DissenyService dissenyService;
	private TascaService tascaService;
	private Validator validator;
	private DocumentService documentService;



	@Autowired
	public ExpedientMassivaController(
			ExecucioMassivaService execucioMassivaService,
			ExpedientService expedientService,
			PermissionService permissionService,
			DissenyService dissenyService,
			TascaService tascaService,
			DocumentService documentService,
			DocumentHelper documentHelper) {
		this.execucioMassivaService = execucioMassivaService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
		this.dissenyService = dissenyService;
		this.tascaService = tascaService;
		this.documentService = documentService;
		this.validator = new TascaFormValidator(expedientService);
	}

	@ModelAttribute("execucioAccioCommand")
	public ExecucioAccioCommand populateExecucioAccioCommand() {
		return new ExecucioAccioCommand();
	}
	
	@ModelAttribute("canviVersioProcesCommand")
	public CanviVersioProcesCommand populateCanviVersioProcesCommand() {
		return new CanviVersioProcesCommand();
	}
	
	@ModelAttribute("scriptCommandMas")
	public ExpedientEinesScriptCommand populateScriptCommand() {
		return new ExpedientEinesScriptCommand();
	}

	@ModelAttribute("aturarCommandMas")
	public ExpedientEinesAturarCommand populateAturarCommand() {
		return new ExpedientEinesAturarCommand();
	}
	
	@ModelAttribute("modificarVariablesMasCommand")
	public ModificarVariablesCommand populateModificarVariablesCommand(){
		return new ModificarVariablesCommand();
	}
	
	
	@ModelAttribute("documentCommandForm")
	public DocumentExpedientCommand populateDocumentExpedientCommand(){
		return new DocumentExpedientCommand();
	}
	
	

	@SuppressWarnings("rawtypes")
	@ModelAttribute("modificarVariableCommand")
	public Object populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "var", required = false) String var) {
		if (var != null) {
			Entorn entorn = getEntornActiu(request);
			if (entorn != null) {
				Map<String, Object> campsAddicionals = new HashMap<String, Object>();
				Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
				if (id != null) {
					campsAddicionals.put("id", id);
					campsAddicionalsClasses.put("id", String.class);
				}
				if (taskId != null) {
					campsAddicionals.put("taskId", taskId);
					campsAddicionalsClasses.put("taskId", String.class);
				}
				campsAddicionals.put("var", var);
				campsAddicionalsClasses.put("var", String.class);
				Object command = TascaFormUtil.getCommandForTasca(
						createTascaAmbVar(entorn.getId(), id, taskId, var),
						campsAddicionals,
						campsAddicionalsClasses);
				return command;
			}
		} 
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@ModelAttribute("documentCommandMas")
	public DocumentExpedientCommand populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "docId", required = false) Long docId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		DocumentExpedientCommand command = new DocumentExpedientCommand();
		command.setData(new Date());
		if (entorn != null) {
			if(id!=null && docId!=null){
//				DocumentExpedientCommand command = new DocumentExpedientCommand();
//				command.setData(new Date());
				DocumentDto dto = null; //documentService.documentInfo(docId);
				command.setDocId(docId);
				List<Long> ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
				List<ExpedientDto> expedients = getExpedientsMassius(
						ids.subList(1, ids.size()));
				for(ExpedientDto expedient: expedients){
					dto = documentService.documentPerProces(
							expedient.getProcessInstanceId(), 
							docId, 
							false);
					if (dto != null) {
							command.setCodi(dto.getDocumentCodi());
							command.setNom(dto.getDocumentNom());
							command.setData(dto.getDataDocument());
							break;
					}
				}
			}
			return command;
		}
		return null;
	}
	
	//modificar documents massivament als expedients seleccionats
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/documentModificarMas", method = RequestMethod.GET)
	public String documentModificarGet(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "docId", required = true) Long docId,
			@RequestParam(value = "inici", required = true) String inici,
			@RequestParam(value = "correu", required = true) String correu,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		Object command = model.get("documentCommandMas");
		model.clear();
		model.addAttribute("inici", inici);
		model.addAttribute("correu", correu);
		if (entorn != null) {
			List<Long> ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			List<ExpedientDto> expedients = getExpedientsMassius(
					ids.subList(1, ids.size()));
			int numExp = ids.size() - 1;
			// Modificar document
			// ---------------------------------------------
			if ("subdoc".equals(submit) || submit.length() == 0) {
				model.addAttribute("command", command);
				for(ExpedientDto expedient: expedients){
					if (potModificarExpedient(expedient)) {
						DocumentDto doc = documentService.documentPerProces(
								expedient.getProcessInstanceId(), 
								docId, 
								false);
						if (doc != null) {
//							model.addAttribute("expedient", expedient);
							model.addAttribute("document", doc);
							model.addAttribute(
									"documentDisseny",
									dissenyService.getDocumentById(docId));
							return "expedient/documentFormMassiva";
						}
					} else {
						missatgeError(request, getMessage("error.permisos.modificar.expedient"));
						return "/expedient/massivaInfo";
					}
				}
				model.addAttribute(
						"documentDisseny",
						dissenyService.getDocumentById(docId));
				return "expedient/documentFormMassiva";
			
			// Adjuntar document
			// ---------------------------------------------
			} else if ("adjunt".equals(submit)) {
				ExpedientDto expedientPrimer = expedientService.getById(ids.get(1));
				if (potModificarExpedient(expedientPrimer)) {
					DocumentExpedientCommand cmd = new DocumentExpedientCommand();
					cmd.setData(new Date());
					model.addAttribute("command", cmd);
					return "expedient/documentAdjuntFormMassiva";
				} else {
					missatgeError(request, getMessage("error.permisos.modificar.expedient"));
					return "/expedient/massivaInfo";
				}
			
			// Esborrar document
			// ---------------------------------------------
			} else if ("delete".equals(submit)) {
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
				
				ExpedientDto expedientPrimer = expedientService.getById(ids.get(1));
				if (potModificarExpedient(expedientPrimer)) {
					try {
						ExecucioMassivaDto dto = new ExecucioMassivaDto();
						dto.setDataInici(dInici);
						dto.setEnviarCorreu(bCorreu);
						dto.setExpedientIds(ids.subList(1, ids.size()));
						dto.setExpedientTipusId(ids.get(0));
						dto.setTipus(ExecucioMassivaTipus.MODIFICAR_DOCUMENT);
						dto.setParam1(null);
						Object[] params = new Object[4];
						params[0] = docId;
						params[1] = null;
						params[2] = "delete";
						dto.setParam2(serialize(params));
						execucioMassivaService.crearExecucioMassiva(dto);
						missatgeInfo(request, getMessage("info.document.massiu.esborrar", new Object[] {numExp}));
					} catch (Exception e) {
						missatgeError(request, getMessage("error.no.massiu"));
					}
				} else {
					missatgeError(request, getMessage("error.permisos.modificar.expedient"));
					return "/expedient/massivaInfo";
				}
				
			// Autogenerar document a partir de plantilla
			// ---------------------------------------------
			} else if ("generar".equals(submit)) {
			
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
						
				ExpedientDto expedientPrimer = expedientService.getById(ids.get(1));
				if (potModificarExpedient(expedientPrimer)) {
					try {
						ExecucioMassivaDto dto = new ExecucioMassivaDto();
						dto.setDataInici(dInici);
						dto.setEnviarCorreu(bCorreu);
						dto.setExpedientIds(ids.subList(1, ids.size()));
						dto.setExpedientTipusId(ids.get(0));
						dto.setTipus(ExecucioMassivaTipus.MODIFICAR_DOCUMENT);
						dto.setParam1(null);
						Object[] params = new Object[4];
						params[0] = docId;
						params[1] = new Date();
						params[2] = "generate";
						dto.setParam2(serialize(params));
						execucioMassivaService.crearExecucioMassiva(dto);
						missatgeInfo(request, getMessage("info.document.massiu.generar", new Object[] {numExp}));
					} catch (Exception e) {
						missatgeError(request, getMessage("error.no.massiu"));
					}
				} else {
					missatgeError(request, getMessage("info.massiu.permisos.no"));
				}
			}
			return "redirect:/expedient/massivaInfo.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/documentModificarMas", method = RequestMethod.POST)
	public String documentModificarPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam("contingut") final MultipartFile multipartFile,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) String correu,
			@ModelAttribute("documentCommandMas") DocumentExpedientCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		
		Entorn entorn = getEntornActiu(request);
		model.addAttribute("inici", inici);
		model.addAttribute("correu", correu);
		if (entorn != null) {
			List<Long> ids = null;
			if(request.getParameter("targetModificarDoc").equals("disseny")){
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			}else{
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			}
			if (ids == null || ids.size() <= 1) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				return "redirect:/expedient/massivaInfo.html";
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
			
			ExpedientDto expedientPrimer = expedientService.getById(ids.get(1));
			if (potModificarExpedient(expedientPrimer)) {
				
				if ("submit".equals(submit) || submit.length() == 0) {
					try {
						new DocumentModificarValidator().validate(command, result);
					        if (result.hasErrors()) {
								model.addAttribute(
										"document",
										documentService.documentInfo(command.getDocId()));
					        	return "expedient/documentFormMassiva";
					        }
			        } catch(Exception ex) {
			        	Long entornId = entorn.getId();
						String numeroExpedient = expedientPrimer.getIdentificador();
						logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+" No s'ha pogut crear el document adjunt", ex);
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        }
					try {
						ExecucioMassivaDto dto = new ExecucioMassivaDto();
						dto.setDataInici(dInici);
						dto.setEnviarCorreu(bCorreu);
						dto.setExpedientIds(ids.subList(1, ids.size()));
						dto.setExpedientTipusId(ids.get(0));
						dto.setTipus(ExecucioMassivaTipus.MODIFICAR_DOCUMENT);
						Object[] params = new Object[4];
						params[0] = command.getDocId();
						// Modificar document
						if (multipartFile.getSize() > 0) {
							dto.setParam1(multipartFile.getOriginalFilename());
							params[1] = command.getData();
							params[2] = command.getCodi();
							params[3] = command.getContingut();
						// Modificar data
						} else {
							dto.setParam1(null);
							params[1] = command.getData();
							params[2] = "date";
							params[3] = null;
						}
						
						dto.setParam2(serialize(params));
						execucioMassivaService.crearExecucioMassiva(dto);
					} catch(Exception ex) {
			        	missatgeError(request, getMessage("error.no.massiu"));
			        }
				} else if ("adjunt".equals(submit)) {
					new DocumentAdjuntCrearValidator().validate(command, result);
			        if (result.hasErrors())
			        	return "expedient/documentAdjuntFormMassiva";
					try {
						ExecucioMassivaDto dto = new ExecucioMassivaDto();
						dto.setDataInici(dInici);
						dto.setEnviarCorreu(bCorreu);
						dto.setExpedientIds(ids.subList(1, ids.size()));
						dto.setExpedientTipusId(ids.get(0));
						dto.setTipus(ExecucioMassivaTipus.MODIFICAR_DOCUMENT);
						Object[] params = new Object[4];
						params[0] = null;
						params[1] = command.getData();
						params[2] = command.getNom();
						if (multipartFile.getSize() > 0) {
							dto.setParam1(multipartFile.getOriginalFilename());
							params[3] = command.getContingut();
						}
						dto.setParam2(serialize(params));
						execucioMassivaService.crearExecucioMassiva(dto);
			        } catch(Exception ex) {
			        	missatgeError(request, getMessage("error.no.massiu"));
			        }
				} else if ("cancel".equals(submit)) {
					return "redirect:/expedient/massivaInfo.html";
				}
				missatgeInfo(request, getMessage("info.document.massiu.guardat", new Object[] {numExp}));
			} else {
				missatgeError(request, getMessage("info.massiu.permisos.no"));
			}
			if(request.getParameter("targetModificarDoc").equals("disseny")){
				return "redirect:/expedient/massivaInfoTE.html";
			}
			return "redirect:/expedient/massivaInfo.html";	
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	@RequestMapping(value = "/expedient/documentGenerarMas", method = RequestMethod.GET)
	public String documentGenerarGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "docId", required = true) Long docId,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) String correu,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				//documentService.documentInfo(docId);
				DocumentDto doc = documentService.documentPerProces(
						id, 
						docId, 
						false);
				DocumentDto generat = documentService.generarDocumentPlantilla(
						entorn.getId(),
						doc.getDocumentId(),
						null,
						id,
						new Date(),
						false,
						false,
						null);
				if (generat != null) {
					model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, generat.getArxiuNom());
					model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, generat.getArxiuContingut());
				}
				return "arxiuView";
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.generar.document"), ex.getLocalizedMessage());
	        	logger.error("Error generant el document " + docId + " per la instància de procés " + id, ex);
	        	return "redirect:/expedient/documentModificarMas.html?id=" + id + "&docId=" + docId + "&inici=" + inici + "&correu=" + correu + "&submit=subdoc";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	public class DocumentModificarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Object.class);
		}
		public void validate(Object command, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "data", "not.blank");
		}
	}
	
	//executar un script massivament als expedients seleccionats
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/scriptMas", method = RequestMethod.POST)
	public String script(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "inici", required = true) String inici,
			@RequestParam(value = "correu", required = true) String correu,
			@ModelAttribute("scriptCommandMas") ExpedientEinesScriptCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		model.addAttribute("inici", inici);
		model.addAttribute("correu", correu);
		if (entorn != null) {
			List<Long> ids = null;
			if(request.getParameter("targetScript").equals("disseny")){
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			}else{
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			}
			
			if (ids == null || ids.size() <= 1) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				if(request.getParameter("targetScript").equals("disseny")){
					return "redirect:/expedient/massivaInfoTE.html";
				}
				return "redirect:/expedient/massivaInfo.html";
			}
			int numExp = ids.size() - 1;
			new ExpedientScriptValidator().validate(command, result);
			if (result.hasErrors()) {
				if(request.getParameter("targetScript").equals("disseny")){
					return "redirect:/expedient/massivaInfoTE.html";
				}
				return "redirect:/expedient/massivaInfo.html";
	        }
			
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
			
			ExpedientDto expedientPrimer = expedientService.getById(ids.get(1));
			if (potModificarExpedient(expedientPrimer)) {
				try {
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setExpedientIds(ids.subList(1, ids.size()));
					dto.setExpedientTipusId(ids.get(0));
					dto.setTipus(ExecucioMassivaTipus.EXECUTAR_SCRIPT);
					dto.setParam2(serialize(command.getScript()));
					execucioMassivaService.crearExecucioMassiva(dto);
					missatgeInfo(request, getMessage("info.script.massiu.executat", new Object[] {numExp}));
				} catch (Exception e) {
					missatgeError(request, getMessage("error.no.massiu"));
				}
			} else {
				missatgeError(request, getMessage("info.massiu.permisos.no"));
			}
			if(request.getParameter("targetScript").equals("disseny")){
				return "redirect:/expedient/massivaInfoTE.html";
			}
			return "redirect:/expedient/massivaInfo.html";	
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	
	//aturar massivament els expedients seleccionats
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/aturarMas", method = RequestMethod.POST)
	public String aturar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "inici", required = true) String inici,
			@RequestParam(value = "correu", required = true) String correu,
			@ModelAttribute("aturarCommandMas") ExpedientEinesAturarCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		model.addAttribute("inici", inici);
		model.addAttribute("correu", correu);
		if (entorn != null) {
			List<Long> ids = null;
			if(request.getParameter("targetAturar").equals("disseny")){
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			}else{
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			}
			if (ids == null || ids.size() <= 1) { // Primer element de la llista: id tipus expedient
				missatgeError(request, getMessage("error.no.exp.selec"));
				if(request.getParameter("targetAturar").equals("disseny")){
					return "redirect:/expedient/massivaInfoTE.html";
				}
				return "redirect:/expedient/massivaInfo.html";
			}
			int numExp = ids.size() - 1;
			new ExpedientAturarValidator().validate(command, result);
			if (result.hasErrors()) {
				missatgeError(request, getMessage("error.aturar.expedient"),result.toString());
				if(request.getParameter("targetAturar").equals("disseny")){
					return "redirect:/expedient/massivaInfoTE.html";
				}
				return "redirect:/expedient/massivaInfo.html";
	        }
			
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
			
			ExpedientDto expedientPrimer = expedientService.getById(ids.get(1));
			if (potModificarExpedient(expedientPrimer)) {
				try {
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setExpedientIds(ids.subList(1, ids.size()));
					dto.setExpedientTipusId(ids.get(0));
					dto.setTipus(ExecucioMassivaTipus.ATURAR_EXPEDIENT);
					dto.setParam2(serialize(command.getMotiu()));
					execucioMassivaService.crearExecucioMassiva(dto);
					missatgeInfo(request, getMessage("info.expedient.massiu.aturats", new Object[] {numExp}));
				} catch (Exception e) {
					missatgeError(request, getMessage("error.no.massiu"));
				}
			} else {
				missatgeError(request, getMessage("info.massiu.permisos.no"));
			}
			if(request.getParameter("targetAturar").equals("disseny")){
				return "redirect:/expedient/massivaInfoTE.html";
			}
			return "redirect:/expedient/massivaInfo.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/dadaModificarMas", method = RequestMethod.GET)
	public String dadaModificar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "var", required = true) String var,
			@RequestParam(value = "inici", required = true) String inici,
			@RequestParam(value = "correu", required = true) String correu,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = getExpedient(entorn.getId(), id, taskId);
			
			if (potModificarExpedient(expedient)) {
				TascaDto tasca = createTascaAmbVar(entorn.getId(), id, taskId, var);
				Object command = model.get("modificarVariableCommand");
				model.clear();
				model.addAttribute("command", command);
				model.addAttribute("tasca", tasca);
				model.addAttribute("expedient", expedient);
				model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tasca, command));
				model.addAttribute("inici", inici);
				model.addAttribute("correu", correu);
				return "/expedient/dadaFormMassiva";
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
			}
			if(request.getParameter("targetModificarVar").equals("disseny")){
				return "redirect:/expedient/massivaInfoTE.html";
			}
			return "redirect:/expedient/massivaInfo.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/dadaModificarMas", method = RequestMethod.POST)
	public String dadaModificar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "var", required = false) String var,
			@RequestParam(value = "helMultipleIndex", required = false) Integer index,
			@RequestParam(value = "helMultipleField", required = false) String field,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) String correu,
			@ModelAttribute("modificarVariableCommand") Object command,
			BindingResult result,
			SessionStatus status,
			ModelMap model)  throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.clear();
			model.addAttribute("command", command);
			model.addAttribute("inici", inici);
			model.addAttribute("correu", correu);
			
			List<Long> ids = null;
			if(request.getParameter("targetModificarVar").equals("disseny")){
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			}else{
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			}
			if (ids == null || ids.size() <= 1) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				if(request.getParameter("targetModificarVar").equals("disseny")){
					return "redirect:/expedient/massivaInfoTE.html";
				}
				return "redirect:/expedient/massivaInfo.html";
			}
			int numExp = ids.size() - 1;
			
//			List<ExpedientDto> expedients = getExpedientsMassius(ids.subList(1, ids.size()));
			
			ExpedientDto expedientPrimer = expedientService.getById(ids.get(1));
			if (potModificarExpedient(expedientPrimer)) {
				
				String idPI = expedientPrimer.getProcessInstanceId();
				TascaDto tasca = createTascaAmbVar(entorn.getId(), idPI, taskId, var);
				List<Camp> camps = new ArrayList<Camp>();
	    		for (CampTasca campTasca: tasca.getCamps())
	    			camps.add(campTasca.getCamp());
				
	    		// Executam la acció massiva
	    		if ("submit".equals(submit) || submit.length() == 0) {
					
	    			((TascaFormValidator)validator).setTasca(tasca);
					validator.validate(command, result);
					if (result.hasErrors()) {
//						model.addAttribute("expedients", expedients);
						model.addAttribute("tasca", tasca);
			        	model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tasca, command));
			        	if(request.getParameter("targetModificarVar").equals("disseny")){
							return "redirect:/expedient/massivaInfoTE.html";
						}
			        	return "redirect:/expedient/massivaInfo.html";
			        }
					
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
	    			try {
	    				ExecucioMassivaDto dto = new ExecucioMassivaDto();
	    				dto.setDataInici(dInici);
	    				dto.setEnviarCorreu(bCorreu);
	    				dto.setExpedientIds(ids.subList(1, ids.size()));
	    				dto.setExpedientTipusId(ids.get(0));
	    				dto.setTipus(ExecucioMassivaTipus.MODIFICAR_VARIABLE);
	    				dto.setParam1(var);
	    				Object[] params = new Object[] {entorn.getId(), taskId, PropertyUtils.getSimpleProperty(command, var)};
	    				dto.setParam2(serialize(params));
	    				execucioMassivaService.crearExecucioMassiva(dto);
	    				missatgeInfo(request, getMessage("info.dada.massiu.modificat", new Object[] {var, numExp}));
	    			} catch (Exception e) {
	    				missatgeError(request, getMessage("info.dada.massiu.error", new Object[] {var}), e.getLocalizedMessage());
	    				return "redirect:/expedient/massivaInfo.html";
	    			}
	    			
	    		// Afegim elements a una variable múltiple
				} else if ("multipleAdd".equals(submit)) {
					try {
						if (field != null)
							PropertyUtils.setSimpleProperty(
									command,
									field,
									TascaFormUtil.addMultiple(field, command, camps));
					} catch (Exception ex) {
						missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
						logger.error("No s'ha pogut afegir el camp múltiple", ex);
					}
					model.addAttribute("tasca", tasca);
		        	model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tasca, command));
		        	return "/expedient/dadaFormMassiva";
				} else  if ("multipleRemove".equals(submit)) {
					try {
						if (field != null && index != null)
							PropertyUtils.setSimpleProperty(
									command,
									field,
									TascaFormUtil.deleteMultiple(field, command, camps, index));
					} catch (Exception ex) {
						missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
						logger.error("No s'ha pogut modificar el camp múltiple", ex);
					}
//					model.addAttribute("expedient", expedient);
					model.addAttribute("tasca", tasca);
		        	model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tasca, command));
		        	return "/expedient/dadaFormMassiva";
		        	// Cancel·lam
				} else if("cancel".equals("submit")){
					if(request.getParameter("targetModificarVar").equals("disseny")){
						return "redirect:/expedient/massivaInfoTE.html";
					}
					return "redirect:/expedient/massivaInfo.html";
				}
			} else {
				missatgeError(request, getMessage("info.massiu.permisos.no"));
			}
		    if(request.getParameter("targetModificarVar").equals("disseny")){
				return "redirect:/expedient/massivaInfoTE.html";
			}
			return "redirect:/expedient/massivaInfo.html";
			
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
		
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
				DefinicioProcesDto definicioProces = dissenyService.findDefinicioProcesAmbProcessInstanceId(piid);
				model.addAttribute("definicioProces", definicioProces);
				
				// Accions per a executar
				if (definicioProces != null) {
					List <Accio> accions = dissenyService.findAccionsAmbDefinicioProces(definicioProces.getId());
					model.addAttribute("accions", accions); //findAccionsJbpmOrdenades(definicioProces.getId()));
				}
				
				// Documents
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(
						piid,
						false,
						true,
						true);
				model.addAttribute("instanciaProces", instanciaProces);

				List<Document> documents = instanciaProces.getDocuments();
				model.addAttribute("documents", documents);
//				List<DocumentDto> docsAdjunts = new ArrayList<DocumentDto>(); 
//				
//				for (ExpedientDto exp: expedients) {
//					InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(
//							exp.getProcessInstanceId(),
//							false,
//							true,
//							true);
//					for (DocumentDto document: instanciaProces.getVarsDocuments().values()) {
//						if (!document.isSignat() && document.isAdjunt()) {
//							docsAdjunts.add(document);
//						}
//					}
//				}
//				model.addAttribute("docsAdjunts", docsAdjunts);
				
				Set<Camp> camp = instanciaProces.getCamps();
				List<Camp> llistaCamps = new ArrayList<Camp>();
				for(Camp c: camp){
					llistaCamps.add(c);
				}
				Collections.sort(llistaCamps, new ComparadorCampCodi());
				model.addAttribute("camps",	llistaCamps);
			}
			return "expedient/massivaInfo";
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
				DefinicioProcesDto definicioProces = dissenyService.findDefinicioProcesAmbProcessInstanceId(piid);
				model.addAttribute("definicioProces", definicioProces);
				
				// Accions per a executar
				if (definicioProces != null) {
					model.addAttribute("accions", dissenyService.findAccionsJbpmOrdenades(definicioProces.getId()));
				}
				
				// Documents
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(
						piid,
						false,
						true,
						true);
				model.addAttribute("instanciaProces", instanciaProces);
				List<Document> documents = instanciaProces.getDocuments();
				model.addAttribute("documents", documents);
				
				Set<Camp> camp = instanciaProces.getCamps();
				List<Camp> llistaCamps = new ArrayList<Camp>();
				for(Camp c: camp){
					llistaCamps.add(c);
				}
				Collections.sort(llistaCamps, new ComparadorCampCodi());
				model.addAttribute("camps", llistaCamps);
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
			@RequestParam(value = "inici", required = true) String inici,
			@RequestParam(value = "correu", required = true) String correu,
			@ModelAttribute("canviVersioProcesCommand") CanviVersioProcesCommand command,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		model.addAttribute("inici", inici);
		model.addAttribute("correu", correu);
		if (entorn != null) {
			List<Long> ids = new ArrayList<Long>();
			if(request.getParameter("targetVersio").equals("disseny")){
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			}
			else{
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			}

			if (ids == null || ids.size() <= 1) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				return "redirect:/expedient/consulta.html";
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
			
			try {
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(bCorreu);
				dto.setExpedientIds(ids.subList(1, ids.size()));
				dto.setExpedientTipusId(ids.get(0));
				dto.setTipus(ExecucioMassivaTipus.ACTUALITZAR_VERSIO_DEFPROC);
				dto.setParam2(serialize(command.getDefinicioProcesId()));
				execucioMassivaService.crearExecucioMassiva(dto);
				missatgeInfo(request, getMessage("info.canvi.versio.massiu", new Object[] {numExp}));
			} catch (Exception e) {
				missatgeError(request, getMessage("error.no.massiu"));
			}
			if(request.getParameter("targetVersio").equals("disseny")){
				return "redirect:/expedient/massivaInfoTE.html";
			}else if(request.getParameter("targetVersio").equals("consulta")){
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
			@RequestParam(value = "inici", required = true) String inici,
			@RequestParam(value = "correu", required = true) String correu,
			@ModelAttribute("execucioAccioCommand") ExecucioAccioCommand command,
			ModelMap model) {
		model.addAttribute("inici", inici);
		model.addAttribute("correu", correu);
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = new ArrayList<Long>();
			if(request.getParameter("targetAccio").equals("disseny")){
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			}
			else{
			
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			}
			if (ids == null || ids.size() <= 1) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				return "redirect:/expedient/consulta.html";
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
			
			try {
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(bCorreu);
				dto.setExpedientIds(ids.subList(1, ids.size()));
				dto.setExpedientTipusId(ids.get(0));
				dto.setTipus(ExecucioMassivaTipus.EXECUTAR_ACCIO);
				dto.setParam2(serialize(command.getAccioId()));
				execucioMassivaService.crearExecucioMassiva(dto);
				missatgeInfo(request, getMessage("info.accio.massiu.executat", new Object[] {numExp}));
			} catch (Exception e) {
				missatgeError(request, getMessage("error.no.massiu"));
			} 
			
//			List<ExpedientDto> expedients = getExpedientsMassius(
//					ids.subList(1, ids.size()));
//			boolean error = false;
//			int numOk = 0;
//			for (ExpedientDto expedient: expedients) {
//				try {
//					expedientService.executarAccio(expedient.getProcessInstanceId(), command.getAccioId());
//					numOk++;
//				} catch (Exception ex) {
//					missatgeError(
//			    			request,
//			    			getMessage("error.expedient.accio.masiva") + " " + expedient.getIdentificador(),
//			    			ex.getLocalizedMessage());
//					logger.error("No s'ha pogut excutar l'acció " + command.getAccioId() + " del procés " + expedient.getProcessInstanceId(), ex);
//					error = true;
//				}
//			}
//			if (numOk > 0) {
//				if (!error)
//					missatgeInfo(request, getMessage("info.accio.executat"));
//				else
//					missatgeInfo(request, getMessage("info.accio.executat.nprimers", new Object[] {new Integer(numOk)}));
//			}
			if(request.getParameter("targetAccio").equals("disseny")){
				return "redirect:/expedient/massivaInfoTE.html";
			}else {
				return "redirect:/expedient/massivaInfo.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/reindexarMas")
	public String accioReindexar(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = true) String inici,
			@RequestParam(value = "correu", required = true) String correu,
			@RequestParam(value="targetIdx",required=false) String targetIdx,
			ModelMap model) {
		model.addAttribute("inici", inici);
		model.addAttribute("correu", correu);
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = null;
			if (request.getParameter("targetIdx").equals("disseny")){
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			} else {
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			}
			if (ids == null || ids.size() <= 1) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				if(request.getParameter("targetIdx").equals("disseny")){
					return "redirect:/expedient/massivaInfoTE.html";
				}else{
					return "redirect:/expedient/massivaInfo.html"; 
				}
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
			
			ExpedientDto expedientPrimer = expedientService.getById(ids.get(1));
			if (potModificarExpedient(expedientPrimer)) {
				try {
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setExpedientIds(ids.subList(1, ids.size()));
					dto.setExpedientTipusId(ids.get(0));
					dto.setTipus(ExecucioMassivaTipus.REINDEXAR);
					execucioMassivaService.crearExecucioMassiva(dto);
					missatgeInfo(request, getMessage("info.accio.massiu.executat", new Object[] {numExp}));
				} catch (Exception e) {
					missatgeError(request, getMessage("error.no.massiu"));
				}
			} else {
				missatgeError(request, getMessage("info.massiu.permisos.no"));
			}
			
			/*boolean error = false;
			int numOk = 0;
			for (ExpedientDto exp: expedients) {
				if (potModificarExpedient(exp)) {
					String idPI = exp.getProcessInstanceId();
					try {
						expedientService.luceneReindexarExpedient(idPI);
						numOk++;
					} catch (Exception ex) {
						missatgeError(request, getMessage("error.reindexar.expedient") + " " + exp.getIdentificador(), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut reindexar l'expedient " + exp.getIdentificador(), ex);
			        	error = true;
					}
				}
			}
			if (numOk > 0) {
				if (!error)
					missatgeInfo(request, getMessage("info.reindexar.executat"));
				else
					missatgeInfo(request, getMessage("info.reindexar.executat.nprimers", new Object[] {new Integer(numOk)}));
			}*/
			if (request.getParameter("targetIdx").equals("disseny")) {
				return "redirect:/expedient/massivaInfoTE.html";
			} else {
				return "redirect:/expedient/massivaInfo.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	private boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}

	private class ExpedientScriptValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEinesScriptCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "script", "not.blank");
		}
	}

//	private String getMissageFinalCadenaExcepcions(Throwable ex) {
//		if (ex.getCause() == null) {
//			return ex.getClass().getName() + ": " + ex.getMessage();
//		} else {
//			return getMissageFinalCadenaExcepcions(ex.getCause());
//		}
//	}

	private List<ExpedientDto> getExpedientsMassius(List<Long> ids) {
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		for (Long id: ids) {
			resposta.add(expedientService.getById(id));
		}
		return resposta;
	}
	private class ExpedientAturarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEinesAturarCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "motiu", "not.blank");
		}
	}

	public List<String> getVariablesProces(
			Set<Camp> camps,
			Map<String, Object> varsComText) {
		List<String> resposta = new ArrayList<String>();
		for (String codi: varsComText.keySet()) {
			boolean trobat = false;
			for (Camp camp: camps) {
				if (camp.getCodi().equals(codi)) {
					resposta.add(codi);
					trobat = true;
					break;
				}
			}
			if (!trobat)
				resposta.add(codi);
		}
		return resposta;
	}
	
	private TascaDto createTascaAmbVar(
			Long entornId,
			String id,
			String taskId,
			String var) {
		Camp camp = null;
		Object valor = null;
		InstanciaProcesDto instanciaProces = null;
		TascaDto tasca = null;
		if (taskId == null) {
			instanciaProces = expedientService.getInstanciaProcesById(
					id, 
					false, 
					true, 
					false);
			camp = dissenyService.findCampAmbDefinicioProcesICodi(
					instanciaProces.getDefinicioProces().getId(), 
					var);
			valor = instanciaProces.getVariable(var);
		} else {
			tasca = tascaService.getById(
					entornId,
					taskId,
					null,
					null,
					true,
					true);
			instanciaProces = expedientService.getInstanciaProcesById(
					tasca.getProcessInstanceId(),
					false, 
					true, 
					false);
			for (CampTasca campTasca: tasca.getCamps()) {
				if (campTasca.getCamp().getCodi().equals(var)) {
					camp = campTasca.getCamp();
					break;
				}
			}
			valor = tasca.getVariable(var);
		}
		if (camp == null) {
			camp = new Camp();
			camp.setTipus(TipusCamp.STRING);
			camp.setCodi(var);
			camp.setEtiqueta(var);
			valor = tasca.getVariable(var);
		}
		TascaDto tascaNova = new TascaDto();
		tascaNova.setId(taskId);
		tascaNova.setTipus(TipusTasca.FORM);
		tascaNova.setProcessInstanceId(instanciaProces.getId());
		List<CampTasca> camps = new ArrayList<CampTasca>();
		CampTasca campTasca = new CampTasca();
		campTasca.setCamp(camp);
		camps.add(campTasca);
		tascaNova.setCamps(camps);
		if (taskId == null)
			tascaNova.setDefinicioProces(instanciaProces.getDefinicioProces());
		else
			tascaNova.setDefinicioProces(tasca.getDefinicioProces());
		if (valor != null) {
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(camp.getCodi(), valor);
			tascaNova.setVariables(variables);
			if (taskId == null) {
				tascaNova.setValorsDomini(instanciaProces.getValorsDomini());
				tascaNova.setValorsMultiplesDomini(instanciaProces.getValorsMultiplesDomini());
				tascaNova.setVarsComText(instanciaProces.getVarsComText());
			} else {
				tascaNova.setValorsDomini(tasca.getValorsDomini());
				tascaNova.setValorsMultiplesDomini(tasca.getValorsMultiplesDomini());
				tascaNova.setVarsComText(tasca.getVarsComText());
			}
		}
		return tascaNova;
	}
	
	private ExpedientDto getExpedient(Long entornId, String id, String taskId) {
		if (id != null)
			return expedientService.findExpedientAmbProcessInstanceId(id);
		if (taskId != null) {
			TascaDto tasca = tascaService.getById(
					entornId,
					taskId,
					null,
					null,
					false,
					false);
			return expedientService.findExpedientAmbProcessInstanceId(tasca.getProcessInstanceId());
		}
		return null;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				byte[].class,
				new ByteArrayMultipartFileEditor());
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
	
	public class DocumentAdjuntCrearValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Object.class);
		}
		public void validate(Object command, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "nom", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "data", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "contingut", "not.blank");
		}
	}
	
	public byte[] serialize(Object obj) throws Exception{
		byte[] bytes = null;
		if (obj != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			bos.close();
			bytes = bos.toByteArray();
//		} catch (IOException ex) {
//			// TODO: Handle the exception
		}
		return bytes;
	}

//	public Object deserialize(byte[] bytes) {
//		Object obj = null;
//		try {
//			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
//			ObjectInputStream ois = new ObjectInputStream(bis);
//			obj = ois.readObject();
//		} catch (IOException ex) {
//			// TODO: Handle the exception
//		} catch (ClassNotFoundException ex) {
//			// TODO: Handle the exception
//		}
//		return obj;
//	}

	public class ComparadorCampCodi implements Comparator<Camp> {
	    public int compare(Camp c1, Camp c2) {
	        return c1.getCodi().compareToIgnoreCase(c2.getCodi());
	    }
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientMassivaController.class);

}
