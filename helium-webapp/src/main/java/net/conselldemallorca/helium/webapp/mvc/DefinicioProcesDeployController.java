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
public class DefinicioProcesDeployController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;



	@Autowired
	public DefinicioProcesDeployController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("desplegamentTipus")
	public List<ParellaCodiValorDto> populateTipus() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto("JBPM", "Desplegament jBPM"));
		resposta.add(new ParellaCodiValorDto("EXPORT", "Exportació Helium"));
		return resposta;
	}
	@ModelAttribute("command")
	public DeployCommand populateCommand(
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId) {
		DeployCommand command = new DeployCommand();
		if (expedientTipusId != null)
			command.setExpedientTipusId(expedientTipusId);
		return command;
	}

	@RequestMapping(value = "/definicioProces/deploy", method = RequestMethod.GET)
	public String exportGet(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<ExpedientTipus> expedientTipus = llistatExpedientTipusPerDissenyar(request);
			if (expedientTipus.size() > 0 || potDissenyarEntorn(entorn)) {
				model.addAttribute("expedientTipus", expedientTipus);
				return "definicioProces/deploy";
			} else {
				missatgeError(request, "No té permisos per desplegar arxius a dins aquest entorn");
				return "redirect:/definicioProces/deploy.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/definicioProces/deploy.html";
		}
	}
	@RequestMapping(value = "/definicioProces/deploy", method = RequestMethod.POST)
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
			List<ExpedientTipus> expedientTipus = llistatExpedientTipusPerDissenyar(request);
			if (expedientTipus.size() > 0 || potDissenyarEntorn(entorn)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					new DeployValidator().validate(command, result);
			        if (result.hasErrors()) {
			        	model.addAttribute("expedientTipus", expedientTipus);
			        	return "definicioProces/deploy";
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
				        	missatgeInfo(request, "L'arxiu s'ha desplegat amb èxit");
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
					    		missatgeInfo(request, "L'arxiu s'ha desplegat amb èxit");
					        	return "redirect:/definicioProces/llistat.html";
					    	} else {
					    		missatgeError(request, "Aquest arxiu no és un arxiu d'exportació vàlid");
					    	}
		        		}
						return "redirect:/definicioProces/llistat.html";
		        	} catch (ClassNotFoundException ex) {
		        		missatgeError(request, "Aquest arxiu no és un arxiu d'exportació vàlid", ex.getMessage());
		        	} catch (IOException ex) {
		        		missatgeError(request, "Error desplegant l'arxiu", ex.getMessage());
		        	} catch (DeploymentException ex) {
		        		missatgeError(request, "Error desplegant l'arxiu", ex.getMessage());
		        	}
		        	return "redirect:/definicioProces/deploy.html";
				} else {
					return "redirect:/definicioProces/llistat.html";
				}
			} else {
				missatgeError(request, "No té permisos per desplegar arxius a dins aquest entorn");
				return "redirect:/definicioProces/deploy.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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

	private List<ExpedientTipus> llistatExpedientTipusPerDissenyar(HttpServletRequest request) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<ExpedientTipus> resposta = new ArrayList<ExpedientTipus>();
			List<ExpedientTipus> llistat = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
			for (ExpedientTipus expedientTipus: llistat) {
				if (potDissenyarExpedientTipus(entorn, expedientTipus))
					resposta.add(expedientTipus);
			}
			return resposta;
		}
		return new ArrayList<ExpedientTipus>();
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
