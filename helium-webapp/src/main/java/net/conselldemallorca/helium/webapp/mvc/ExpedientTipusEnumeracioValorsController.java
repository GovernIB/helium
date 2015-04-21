/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador per la gestió de tipus d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientTipusEnumeracioValorsController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;
	private Validator annotationValidator;


	@Autowired
	public ExpedientTipusEnumeracioValorsController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService,
			PluginService pluginService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("command")
	public EnumeracioValorsCommand populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) Long id) {
		EnumeracioValorsCommand command = new EnumeracioValorsCommand();
		if (id != null)
			command.setEnumeracioId(id);
		return command;
	}
	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			Enumeracio enumeracio = dissenyService.getEnumeracioById(id);
			if (enumeracio != null)
				return enumeracio.getExpedientTipus();
		}
		return null;
	}

	@RequestMapping(value = "/expedientTipus/enumeracioValors", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Enumeracio enumeracio = dissenyService.getEnumeracioById(id);
			if (potGestionarExpedientTipus(entorn, enumeracio.getExpedientTipus())) {
				model.addAttribute("llistat", dissenyService.findEnumeracioValorsAmbEnumeracio(id));
				ImportCommand commandImportacio = new ImportCommand();
				commandImportacio.setEnumeracioId(id);
				model.addAttribute("commandImportacio", commandImportacio);
				return "expedientTipus/enumeracioValors";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/enumeracioValors", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@ModelAttribute("command") EnumeracioValorsCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Enumeracio enumeracio = dissenyService.getEnumeracioById(command.getEnumeracioId());
			if (potGestionarExpedientTipus(entorn, enumeracio.getExpedientTipus())) {
				if ("submit".equals(submit) || submit.length() == 0) {
					command.setEnumeracioId(enumeracio.getId());
					annotationValidator.validate(command, result);
					new EnumeracioValorsValidator(dissenyService).validate(command, result);
			        if (result.hasErrors()) {
			        	model.addAttribute("llistat", dissenyService.findEnumeracioValorsAmbEnumeracio(command.getEnumeracioId()));
			        	ImportCommand commandImportacio = new ImportCommand();
						commandImportacio.setEnumeracioId(command.getEnumeracioId());
						model.addAttribute("commandImportacio", commandImportacio);
			        	return "expedientTipus/enumeracioValors";
			        }
			        try {
			        	EnumeracioValors enumeracioValors = new EnumeracioValors();
			        	enumeracioValors.setId(command.getId());
			        	enumeracioValors.setCodi(command.getCodi());
			        	enumeracioValors.setNom(command.getNom());
			        	enumeracioValors.setEnumeracio(enumeracio);
			        	if (command.getId() == null)
			        		dissenyService.createEnumeracioValors(enumeracioValors);
			        	else
			        		dissenyService.updateEnumeracioValors(enumeracioValors);
			        	missatgeInfo(request, getMessage("info.enum.guardat"));
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        	return "expedientTipus/enumeracioValors";
			        }
			        return "redirect:/expedientTipus/enumeracioValors.html?id=" + command.getEnumeracioId();
				} else {
					return "redirect:/expedientTipus/enumeracioLlistat.html?expedientTipusId=" + expedientTipusId;
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

	@RequestMapping(value = "/expedientTipus/enumeracioValorsEsborrar")
	public String delete(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			EnumeracioValors enumeracioValors = dissenyService.getEnumeracioValorsById(id);
			if (enumeracioValors != null){
				if (potGestionarExpedientTipus(entorn, enumeracioValors.getEnumeracio().getExpedientTipus())) {
					try {
						dissenyService.deleteEnumeracioValors(id);
						missatgeInfo(request, getMessage("info.enum.esborrat") );
					} catch (Exception ex) {
						missatgeError(request, getMessage("error.esborrar.enum"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut esborrar el valor de l'enumeració", ex);
					}
					return "redirect:/expedientTipus/enumeracioValors.html?id=" + enumeracioValors.getEnumeracio().getId();
				} else {
					missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
					return "redirect:/index.html";
				}
			} else {
				missatgeError(request, getMessage("error.no.enumeracio.selec") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	@RequestMapping(value = "/expedientTipus/enumeracioValorsForm", method = RequestMethod.GET)
	public String formGetValors(	
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "enumeracioId", required = true) Long enumeracioId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Enumeracio enumeracio = dissenyService.getEnumeracioById(enumeracioId);
			if (potGestionarExpedientTipus(entorn, enumeracio.getExpedientTipus())) {	
				EnumeracioValors enumeracioValors = dissenyService.findEnumeracioValorsAmbId(enumeracioId, id);
				model.addAttribute("expedientTipus", enumeracio.getExpedientTipus());
				model.addAttribute("command", enumeracioValors);
				model.addAttribute("enumeracioId", enumeracioId);
				return "expedientTipus/enumeracioValorsForm";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
		}
	}			
	
	@RequestMapping(value = "/expedientTipus/enumeracioValorsForm", method = RequestMethod.POST)
	public String formPostValors(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "enumeracioId", required = true) Long enumeracioId,
			@RequestParam(value = "ordre", required = false) Integer ordre,
			@ModelAttribute("command") EnumeracioValorsCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {	
			Enumeracio enumeracio = dissenyService.getEnumeracioById(enumeracioId);
			if (potGestionarExpedientTipus(entorn, enumeracio.getExpedientTipus())) {
				if ("submit".equals(submit) || submit.length() == 0) {
			        try {
			        	EnumeracioValors enumeracioValors = new EnumeracioValors();
			        	enumeracioValors.setId(command.getId());
			        	enumeracioValors.setCodi(command.getCodi());
			        	enumeracioValors.setNom(command.getNom());
			        	enumeracioValors.setEnumeracio(enumeracio);
			        	enumeracioValors.setOrdre(ordre);
			        	if (command.getId() == null){
			        		dissenyService.createEnumeracioValors(enumeracioValors);
			        		missatgeInfo(request, getMessage("info.enum.creat") );			        		
			        	} else {
			        		dissenyService.updateEnumeracioValors(enumeracioValors);
			        		missatgeInfo(request, getMessage("info.enum.guardat"));					        		
			        	}
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        	return "expedientTipus/enumeracioValorsForm";
			       }
				}
				return "redirect:/expedientTipus/enumeracioValors.html?id=" + command.getEnumeracioId();
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}	
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/enumeracioValorsPujar")
	public String pujarValor(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "enumeracioId", required = true) Long enumeracioId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Enumeracio enumeracio = dissenyService.getEnumeracioById(enumeracioId);
			if (potGestionarExpedientTipus(entorn, enumeracio.getExpedientTipus())) {
				try {
					dissenyService.goUpEnumeracioValor(id);
				} catch (Exception ex) {
		        	missatgeError(request, getMessage("error.ordre.enumeracio"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut canviar l'ordre del valor de l'enumeració", ex);
		        }
				return "redirect:/expedientTipus/enumeracioValors.html?id=" + enumeracioId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedientTipus/enumeracioValorsBaixar")
	public String baixarValor(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "enumeracioId", required = true) Long enumeracioId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Enumeracio enumeracio = dissenyService.getEnumeracioById(enumeracioId);
			if (potGestionarExpedientTipus(entorn, enumeracio.getExpedientTipus())) {
				try {
					dissenyService.goDownEnumeracioValor(id);
				} catch (Exception ex) {
		        	missatgeError(request, getMessage("error.ordre.enumeracio"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut canviar l'ordre del valor de l'enumeració", ex);
		        }
				return "redirect:/expedientTipus/enumeracioValors.html?id=" + enumeracioId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/enumeracioImportar")
	public String importar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "arxiu", required = true) final MultipartFile multipartFile,
			@RequestParam(value = "eliminarValorsAntics", required = false) Boolean eliminarValorsAntics) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				if (multipartFile.getBytes() == null || multipartFile.getBytes().length == 0) {
					missatgeError(request, getMessage("error.especificar.arxiu.importar"));
				} else {
					
					if (eliminarValorsAntics != null && eliminarValorsAntics) {
						dissenyService.deleteValorsByEnumeracio(id);
					}
					
					Enumeracio enumeracio = dissenyService.getEnumeracioById(id);
					BufferedReader br = new BufferedReader(
							new InputStreamReader(multipartFile.getInputStream()));
					String linia = br.readLine();
					while (linia != null) {
						String[] columnes = linia.split(";");
						if (columnes.length == 0)
							columnes = linia.split(",");
						if (columnes.length > 1) {
							EnumeracioValors enumeracioValors = new EnumeracioValors();
				        	enumeracioValors.setId(null);
				        	enumeracioValors.setCodi(columnes[0]);
				        	enumeracioValors.setNom(columnes[1]);
				        	enumeracioValors.setEnumeracio(enumeracio);
			        		dissenyService.createOrUpdateEnumeracioValors(enumeracioValors);
						}
						linia = br.readLine();
					}
					missatgeInfo(request, getMessage("info.enum.valors.importats"));
				}
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.ordre.enumeracio"), ex.getLocalizedMessage());
	        	logger.error("No s'han pogut importar els valors de l'enumeració " + id, ex);
	        }
			return "redirect:/expedientTipus/enumeracioValors.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	public class ImportCommand {
		private Long enumeracioId;
		private boolean eliminarValorsAntics;
		private byte[] arxiu;
		public Long getEnumeracioId() {
			return enumeracioId;
		}
		public void setEnumeracioId(Long enumeracioId) {
			this.enumeracioId = enumeracioId;
		}
		public boolean isEliminarValorsAntics() {
			return eliminarValorsAntics;
		}
		public void setEliminarValorsAntics(boolean eliminarValorsAntics) {
			this.eliminarValorsAntics = eliminarValorsAntics;
		}
		public byte[] getArxiu() {
			return arxiu;
		}
		public void setArxiu(byte[] arxiu) {
			this.arxiu = arxiu;
		}
	}



	private boolean potGestionarExpedientTipus(Entorn entorn, ExpedientTipus expedientTipus) {
		if (potDissenyarEntorn(entorn))
			return true;
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN,
					ExtendedPermission.MANAGE}) != null;
	}
	private boolean potDissenyarEntorn(Entorn entorn) {
		return permissionService.filterAllowed(
				entorn,
				Entorn.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusEnumeracioValorsController.class);

}
