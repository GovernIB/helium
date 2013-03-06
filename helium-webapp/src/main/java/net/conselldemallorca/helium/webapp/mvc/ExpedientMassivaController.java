/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

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

import net.conselldemallorca.helium.core.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Tasca.TipusTasca;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.core.model.service.DocumentService;
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
@SuppressWarnings("unused")
@Controller
public class ExpedientMassivaController extends BaseController {

	public static final String VARIABLE_SESSIO_IDS_MASSIUS = "consultaExpedientsIdsMassius";
	public static final String VARIABLE_SESSIO_IDS_MASSIUS_TE = "consultaExpedientsIdsMassiusTE";
	private ExpedientService expedientService;
	private PermissionService permissionService;
	private DissenyService dissenyService;
	private TascaService tascaService;
	private Validator validator;
	private CommonRegistreController commonRegistreController;
	private ExpedientRegistreController expedientRegistreController;
	private DocumentService documentService;
	private DocumentHelper documentHelper;
	private DocumentStoreDao documentStoreDao;
	public DocumentStoreDao getDocumentStoreDao() {
		return documentStoreDao;
	}


	public void setDocumentStoreDao(DocumentStoreDao documentStoreDao) {
		this.documentStoreDao = documentStoreDao;
	}


	public ExpedientRegistreController getExpedientRegistreController() {
		return expedientRegistreController;
	}


	public void setExpedientRegistreController(
			ExpedientRegistreController expedientRegistreController) {
		this.expedientRegistreController = expedientRegistreController;
	}


	public CommonRegistreController getCommonRegistreController() {
		return commonRegistreController;
	}


	public void setCommonRegistreController(
			CommonRegistreController commonRegistreController) {
		this.commonRegistreController = commonRegistreController;
	}


	@Autowired
	public ExpedientMassivaController(
			ExpedientService expedientService,
			PermissionService permissionService,
			DissenyService dissenyService,
			TascaService tascaService,
			DocumentService documentService,
			DocumentHelper documentHelper) {
		this.expedientService = expedientService;
		this.permissionService = permissionService;
		this.dissenyService = dissenyService;
		this.tascaService = tascaService;
		this.documentService = documentService;
		this.documentHelper = documentHelper;
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

	

	
	
	@ModelAttribute("documentCommandMas")
	public DocumentExpedientCommand populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "procesInstanceId", required = false) String procesInstanceId,
			@RequestParam(value = "docId", required = false) Long docId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if(procesInstanceId!=null && docId!=null){
				DocumentExpedientCommand command = new DocumentExpedientCommand();
				DocumentDto dto = documentService.documentInfo(docId);
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesInstanceId, false, false, true);
				Document document = dissenyService.findDocumentAmbDefinicioProcesICodi(
						instanciaProces.getDefinicioProces().getId(),
						dto.getDocumentCodi());
				command.setDocId(docId);
				if (dto.isAdjunt())
					command.setNom(dto.getAdjuntTitol());
				else
					command.setNom(document.getNom());
				command.setData(dto.getDataDocument());
				return command;
			}
		}
		return null;
	}
	
	
	//modificar documents massivament als expedients seleccionats
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/documentModificarMas", method = RequestMethod.GET)
	public String documentModificarGet(
			HttpServletRequest request,
			@RequestParam(value = "docId", required = true) Long docId,
			@RequestParam(value = "nom", required = true) String nom,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			List<ExpedientDto> expedients = getExpedientsMassius(
					ids.subList(1, ids.size()));
			String procesInstanceId = expedients.get(0).getProcessInstanceId();
			model.addAttribute("expedients", expedients);
			for(ExpedientDto exp: expedients){
				if (potModificarExpedient(exp)) {
							DocumentDto doc = documentService.documentInfo(docId);
							if(doc.getAdjuntTitol().equals(nom)){
								if (!doc.isSignat()) {
									model.addAttribute("expedient", exp);
									model.addAttribute("document", doc);
									model.addAttribute(
											"documentDisseny",
											dissenyService.getDocumentById(docId));
									return "/expedient/documentFormMassiva";
								} else {
									missatgeError(request, getMessage("error.modificar.doc.signat") );
									return "redirect:/expedient/documents.html?id=" + procesInstanceId;
								}
							}
							else{
								missatgeError(request, getMessage("error.modificar.document.tipus") );
								InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(
										procesInstanceId,
										false,
										true,
										true);
								List<Document> documents = instanciaProces.getDocuments();
								model.addAttribute(
										"documents",
										documents);
								return "redirect:/expedient/massivaInfo.html";
							}
					
				} else {
					missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				}
			}
			
			return "/expedient/massivaInfo";
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
			@ModelAttribute("command") DocumentExpedientCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = null;
			if(request.getParameter("targetModificarDoc").equals("disseny")){
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			}else{
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			}
			if (ids == null || ids.size() == 0) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				return "redirect:/expedient/massivaInfo.html";
			}
			List<ExpedientDto> expedients = getExpedientsMassius(
					ids.subList(1, ids.size()));
			for(ExpedientDto exp: expedients){
				if (potModificarExpedient(exp)) {
					if ("submit".equals(submit) || submit.length() == 0) {
						
						new DocumentAdjuntCrearValidator().validate(command, result);
				        if (result.hasErrors())
				        	return "expedient/documentAdjuntForm";
				        String nomArxiu = "arxiu";
				        if (multipartFile.getSize() > 0) {
							try {
								nomArxiu = multipartFile.getOriginalFilename();
							} catch (Exception ignored) {}
						}
						try{
							new DocumentModificarValidator().validate(command, result);
						        if (result.hasErrors()) {
						        	model.addAttribute("expedient", exp);
									model.addAttribute(
											"document",
											documentService.documentInfo(command.getDocId()));
						        	return "expedient/documentFormMassiva";
						        }
				        }catch(Exception ex){
				        	Long entornId = entorn.getId();
							String numeroExpedient = exp.getIdentificador();
							logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+" No s'ha pogut crear el document adjunt", ex);
				        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
				        }
						try {
							DocumentDto doc = documentService.documentInfo(command.getDocId());
							String idExp = exp.getProcessInstanceId();
							if (!doc.isAdjunt() ) {
								documentService.guardarDocumentProces(
										idExp,
										doc.getDocumentCodi(),
										null,
										command.getData(),
										(multipartFile.getSize() > 0) ? multipartFile.getOriginalFilename() : null,
										(multipartFile.getSize() > 0) ? command.getContingut() : null,
										false);
								}else{
										documentService.guardarAdjunt(
										idExp,
										doc.getAdjuntId(),
										command.getNom(),
										command.getData(),
										(multipartFile.getSize() > 0) ? multipartFile.getOriginalFilename() : null,
										(multipartFile.getSize() > 0) ? command.getContingut() : null);
							}
							
				        } catch (Exception ex) {
				        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
				        	logger.error("No s'ha pogut guardar el document", ex);
				        }
					}
				} else {
					missatgeError(request, getMessage("error.permisos.modificar.expedient"));
					return "redirect:/expedient/consulta.html";
				}
			}//final For expedients
			missatgeInfo(request, getMessage("info.document.guardat") );
			if(request.getParameter("targetModificarDoc").equals("disseny")){
				return "redirect:/expedient/massivaInfoTE.html";
			}
			return "redirect:/expedient/massivaInfo.html";	
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
			@ModelAttribute("scriptCommandMas") ExpedientEinesScriptCommand command,
			BindingResult result,
			SessionStatus status) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = null;
			if(request.getParameter("targetScript").equals("disseny")){
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			}else{
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			}
			
			if (ids == null || ids.size() == 0) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				return "redirect:/expedient/massivaInfo.html";
			}
				new ExpedientScriptValidator().validate(command, result);
				if (result.hasErrors()) {
					return "redirect:/expedient/massivaInfo.html";
		        }
				List<ExpedientDto> expedients = getExpedientsMassius(
						ids.subList(1, ids.size()));
				for (ExpedientDto exp: expedients) {
					if (potModificarExpedient(exp)) {
						try {
							expedientService.evaluateScript(
									exp.getProcessInstanceId(),
									command.getScript(),
									null);
							
						} catch (Exception ex) {
							Long entornId = entorn.getId();
							String numeroExpedient = id;
							logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+" No s'ha pogut executar l'script", ex);
							missatgeError(request, getMessage("error.executar.script"), getMissageFinalCadenaExcepcions(ex));
							return "redirect:/expedient/massivaInfo.html";
						}
					}else {
						missatgeError(request, getMessage("error.permisos.modificar.expedient"));
						return "redirect:/expedient/massivaInfo.html";
					
					}
				}
				missatgeInfo(request, getMessage("info.script.executat"));
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
			@ModelAttribute("aturarCommandMas") ExpedientEinesAturarCommand command,
			BindingResult result,
			SessionStatus status) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			int aturats=0;
			List<Long> ids = null;
			if(request.getParameter("targetAturar").equals("disseny")){
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			}else{
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			}
			if (ids == null || ids.size() == 0) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				return "redirect:/expedient/massivaInfo.html";
			}
			new ExpedientAturarValidator().validate(command, result);
			if (result.hasErrors()) {
				missatgeError(request, getMessage("error.aturar.expedient"),result.toString());
				return "redirect:/expedient/massivaInfo.html";
	        }
			List<ExpedientDto> expedients = getExpedientsMassius(
					ids.subList(1, ids.size()));
			for (ExpedientDto exp: expedients) {
			
				if (potModificarExpedient(exp)) {
					if (!exp.isAturat()) {
						try {
							expedientService.aturar(
									exp.getProcessInstanceId(),
									command.getMotiu(),
									null);
							aturats+=1;
							
						} catch (Exception ex) {
							missatgeError(request, getMessage("error.aturar.expedient"), ex.getLocalizedMessage());
				        	logger.error("No s'ha pogut aturar l'expedient", ex);
				        	return "redirect:/expedient/massivaInfo.html";
						}
					} else {
						missatgeError(request, getMessage("error.expedient.ja.aturat") );
					}
				}else {
					missatgeError(request, getMessage("error.permisos.modificar.expedient"));
					return "redirect:/expedient/massivaInfo.html";
				}
			}
				missatgeInfo(request, getMessage("info.expedient.aturats") +" "+ aturats+" "+ "expedient(s)");
				if(request.getParameter("targetAturar").equals("disseny")){
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
			@ModelAttribute("modificarVariableCommand") Object command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = null;
			if(request.getParameter("targetModificarVar").equals("disseny")){
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			}else{
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			}
			
			if (ids == null || ids.size() == 0) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				return "redirect:/expedient/massivaInfo.html";
			}
			List<ExpedientDto> expedients = getExpedientsMassius(
					ids.subList(1, ids.size()));
			for(ExpedientDto exp: expedients){
				if (potModificarExpedient(exp)) {
					String idPI = exp.getProcessInstanceId();
					TascaDto tasca = createTascaAmbVar(entorn.getId(), idPI, taskId, var);
					List<Camp> camps = new ArrayList<Camp>();
		    		for (CampTasca campTasca: tasca.getCamps())
		    			camps.add(campTasca.getCamp());
					if ("submit".equals(submit) || submit.length() == 0) {
						((TascaFormValidator)validator).setTasca(tasca);
						validator.validate(command, result);
						if (result.hasErrors()) {
							model.addAttribute("expedients", expedients);
							model.addAttribute("tasca", tasca);
				        	model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tasca, command));
				        	return "redirect:/expedient/massivaInfo.html";
				        }
						try {
							if (idPI != null) {
						        expedientService.updateVariable(
						        		idPI,
						        		var,
						        		PropertyUtils.getSimpleProperty(command, var));
							} else {
								tascaService.updateVariable(
										entorn.getId(),
										taskId,
										var,
										PropertyUtils.getSimpleProperty(command, var));
							}
					        
				        } catch (Exception ex) {
							missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
				        	logger.error("No s'ha pogut modificat la dada", ex);
				        	return "redirect:/expedient/massivaInfo.html";
						}
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
						model.addAttribute("expedients", expedients);
						model.addAttribute("tasca", tasca);
			        	model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tasca, command));
			        	return "redirect:/expedient/massivaInfo.html";
					} else if("cancel".equals("submit")){
						
						return "redirect:/expedient/massivaInfo.html";
					}
				} else {
					missatgeError(request, getMessage("error.permisos.modificar.expedient"));
					return "redirect:/expedient/massivaInfo.html";
				}
			
		
		    }//if del For expedients
		    missatgeInfo(request, getMessage("info.variable.modificada"));
		    if(request.getParameter("targetModificarVar").equals("disseny")){
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
			@RequestParam(value = "targetModificarVar", required = false) String targetModificarVar,
			@RequestParam(value = "var", required = true) String var,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = getExpedient(entorn.getId(), id, taskId);
			if (potModificarExpedient(expedient)) {
				model.addAttribute("expedient", expedient);
				TascaDto tasca = createTascaAmbVar(entorn.getId(), id, taskId, var);
				Object command = model.get("modificarVariableCommand");
				model.addAttribute("tasca", tasca);
				model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tasca, command));
				return "/expedient/dadaFormMassiva";
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
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
			boolean teDocuments = false;
			if (expedients.size() > 0) {
				String piid = expedients.get(0).getProcessInstanceId();
				// Definicions de procés per al canvi de versió
				model.addAttribute(
						"definicioProces",
						dissenyService.findDefinicioProcesAmbProcessInstanceId(piid));
				
				for(ExpedientDto exp: expedients){
					String pi = exp.getProcessInstanceId();
					List<DocumentStore> storeList = documentStoreDao.findAmbProcessInstanceId(pi);
					for(DocumentStore docS: storeList){
						if(!docS.isSignat() && docS.isAdjunt()){
							teDocuments = true;
							model.addAttribute(
									"docStoreId",
									docS.getId());
						}
					}
					storeList.clear();
				}
				// Accions per a executar
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(
						piid,
						false,
						true,
						true);
				model.addAttribute(
						"instanciaProces",
						instanciaProces);
				Set<Camp> camp = instanciaProces.getCamps();
				List<Camp> llistaCamps = new ArrayList<Camp>();
				for(Camp c: camp){
					llistaCamps.add(c);
				}
				Collections.sort(llistaCamps, new ComparadorCampCodi());
				model.addAttribute(
						"camps",
						llistaCamps);
				List<Document> documents = instanciaProces.getDocuments();
				model.addAttribute(
						"documents",
						documents);
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
				Set<Camp> camp = instanciaProces.getCamps();
				List<Camp> llistaCamps = new ArrayList<Camp>();
				for(Camp c: camp){
					llistaCamps.add(c);
				}
				Collections.sort(llistaCamps, new ComparadorCampCodi());
				model.addAttribute(
						"camps",
						llistaCamps);
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
			@ModelAttribute("canviVersioProcesCommand") CanviVersioProcesCommand command,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = new ArrayList<Long>();
			if(request.getParameter("targetVersio").equals("disseny")){
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
			@ModelAttribute("execucioAccioCommand") ExecucioAccioCommand command,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = new ArrayList<Long>();
			if(request.getParameter("targetAccio").equals("disseny")){
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
					System.out.println(">>> Executant acció (codi=" + command.getAccioId() + ") en l'expedient " + expedient.getIdentificador());
					expedientService.executarAccio(expedient.getProcessInstanceId(), command.getAccioId());
					System.out.println(">>> Acció executada (codi=" + command.getAccioId() + ") en l'expedient " + expedient.getIdentificador());
					numOk++;
				} catch (Exception ex) {
					missatgeError(
			    			request,
			    			getMessage("error.expedient.accio.masiva") + " " + expedient.getIdentificador(),
			    			ex.getLocalizedMessage());
					logger.error("No s'ha pogut excutar l'acció " + command.getAccioId() + " del procés " + expedient.getProcessInstanceId(), ex);
					error = true;
				}
			}
			if (numOk > 0) {
				if (!error)
					missatgeInfo(request, getMessage("info.accio.executat"));
				else
					missatgeInfo(request, getMessage("info.accio.executat.nprimers", new Object[] {new Integer(numOk)}));
			}
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
	public String expedientReindexar(
			HttpServletRequest request,
			@RequestParam(value="targetIdx",required=false) String targetIdx,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Long> ids = null;
			if(request.getParameter("targetIdx").equals("disseny")){
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS_TE);
			}
			else{
				ids = (List<Long>)request.getSession().getAttribute(VARIABLE_SESSIO_IDS_MASSIUS);
			}
			if (ids == null || ids.size() == 0) {
				missatgeError(request, getMessage("error.no.exp.selec"));
				if(request.getParameter("targetIdx").equals("disseny")){
					return "redirect:/expedient/massivaInfoTE.html";
				}else{
					return "redirect:/expedient/massivaInfo.html"; }
			}
			List<ExpedientDto> expedients = getExpedientsMassius(ids.subList(1, ids.size()));
			
			boolean error = false;
			int numOk = 0;
			for(ExpedientDto exp: expedients){
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
			}
			if(request.getParameter("targetIdx").equals("disseny")){
				return "redirect:/expedient/massivaInfoTE.html";
			}else {
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

	
	private String getMissageFinalCadenaExcepcions(Throwable ex) {
		if (ex.getCause() == null) {
			return ex.getClass().getName() + ": " + ex.getMessage();
		} else {
			return getMissageFinalCadenaExcepcions(ex.getCause());
		}
	}
	

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
					false, true, false);
			camp = dissenyService.findCampAmbDefinicioProcesICodi(
					instanciaProces.getDefinicioProces().getId(),
					var);
			valor = instanciaProces.getVariables().get(var);
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
					false, true, false);
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
	
	public class ComparadorCampCodi implements Comparator<Camp> {
	    public int compare(Camp c1, Camp c2) {
	        return c1.getCodi().compareToIgnoreCase(c2.getCodi());
	    }
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientMassivaController.class);

}
