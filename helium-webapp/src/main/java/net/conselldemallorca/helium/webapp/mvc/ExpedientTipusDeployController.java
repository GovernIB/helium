/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.core.model.exception.DeploymentException;
import net.conselldemallorca.helium.core.model.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
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



/**
 * Controlador per a desplegar definicions de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientTipusDeployController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientTipusDeployController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("desplegamentTipus")
	public List<ParellaCodiValorDto> populateTipus() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto("JBPM", getMessage("txt.desplegament.jbpm") ));
		resposta.add(new ParellaCodiValorDto("EXPORT", getMessage("txt.exportacio.helium") ));
		return resposta;
	}
	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		return dissenyService.getExpedientTipusById(expedientTipusId);
	}
	@ModelAttribute("command")
	public DeployCommand populateCommand(
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		DeployCommand command = new DeployCommand();
		if (expedientTipusId != null)
			command.setExpedientTipusId(expedientTipusId);
		return command;
	}

	@RequestMapping(value = "/expedientTipus/deploy", method = RequestMethod.GET)
	public String exportGet(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				return "expedientTipus/deploy";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/definicioProces/deploy.html";
		}
	}
	@RequestMapping(value = "/expedientTipus/deploy", method = RequestMethod.POST)
	public String exportForm(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam("arxiu") final MultipartFile multipartFile,
			@ModelAttribute("command") DeployCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(command.getExpedientTipusId());
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					new DeployValidator().validate(command, result);
			        if (result.hasErrors()) {
			        	return "expedientTipus/deploy";
			        }
		        	try {
		        		if (command.getTipus().equals("JBPM")) {
		        			dissenyService.deploy(
				        			entorn.getId(),
				        			command.getExpedientTipusId(),
				        			multipartFile.getOriginalFilename(),
				        			multipartFile.getBytes(),
				        			command.getEtiqueta(),
				        			true);
				        	missatgeInfo(request, getMessage("info.arxiu.desplegat") );
		        		} else {
			        		InputStream is = new ByteArrayInputStream(multipartFile.getBytes());
					    	ObjectInputStream input = new ObjectInputStream(is);
					    	Object deserialitzat = input.readObject();
					    	if (deserialitzat instanceof DefinicioProcesExportacio) {
					    		DefinicioProcesExportacio exportacio = (DefinicioProcesExportacio)deserialitzat;
					    		dissenyService.importar(
					        			entorn.getId(),
					        			command.getExpedientTipusId(),
					        			exportacio,
					        			command.getEtiqueta());
					    		missatgeInfo(request, getMessage("info.arxiu.desplegat"));
					    	} else {
					    		missatgeError(request, getMessage("error.arxius.no.valid"));
					    	}
		        		}
						return "redirect:/expedientTipus/definicioProcesLlistat.html?expedientTipusId=" + command.getExpedientTipusId();
		        	} catch (ClassNotFoundException ex) {
		        		missatgeError(request, getMessage("error.arxius.no.valid"), ex.getMessage());
		        	} catch (IOException ex) {
		        		missatgeError(request, getMessage("error.desplegar.arxius"), ex.getMessage());
		        	} catch (DeploymentException ex) {
		        		missatgeError(request, getMessage("error.desplegar.arxius"), ex.getMessage());
		        	}
		        	return "redirect:/expedientTipus/deploy.html?expedientTipusId=" + command.getExpedientTipusId();
				} else {
					return "redirect:/definicioProces/llistat.html";
				}
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/definicioProces/deploy.html";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				byte[].class,
				new ByteArrayMultipartFileEditor());
	}

	public class DeployCommand {
		private Long expedientTipusId;
		private String etiqueta;
		private String tipus;
		private byte[] arxiu;
		public Long getExpedientTipusId() {
			return expedientTipusId;
		}
		public void setExpedientTipusId(Long expedientTipusId) {
			this.expedientTipusId = expedientTipusId;
		}
		public String getEtiqueta() {
			return etiqueta;
		}
		public void setEtiqueta(String etiqueta) {
			this.etiqueta = etiqueta;
		}
		public String getTipus() {
			return tipus;
		}
		public void setTipus(String tipus) {
			this.tipus = tipus;
		}
		public byte[] getArxiu() {
			return arxiu;
		}
		public void setArxiu(byte[] arxiu) {
			this.arxiu = arxiu;
		}
	}



	private class DeployValidator implements Validator {
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(DeployCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "tipus", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "arxiu", "not.blank");
		}
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
	private boolean potDissenyarEntorn(Entorn entorn) {
		return permissionService.filterAllowed(
				entorn,
				Entorn.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}

}
