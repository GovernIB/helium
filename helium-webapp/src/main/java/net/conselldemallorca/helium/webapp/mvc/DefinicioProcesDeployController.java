/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.core.model.exception.DeploymentException;
import net.conselldemallorca.helium.core.model.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.core.model.exportacio.ExpedientTipusExportacio;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExecucioMassivaService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
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

	public static final String TIPUS_EXPORTACIO_JBPM = "JBPM";
	public static final String TIPUS_EXPORTACIO_DEFPRC = "EXPORTDEFPRC";
	public static final String TIPUS_EXPORTACIO_TIPEXP = "EXPORTTIPEXP";

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;
	private ExecucioMassivaService execucioMassivaService;



	@Autowired
	public DefinicioProcesDeployController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService,
			ExecucioMassivaService execucioMassivaService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
		this.execucioMassivaService = execucioMassivaService;
	}

	@ModelAttribute("desplegamentTipus")
	public List<ParellaCodiValorDto> populateTipus() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(TIPUS_EXPORTACIO_JBPM, getMessage("txt.desplegament.jbpm") ));
		resposta.add(new ParellaCodiValorDto(TIPUS_EXPORTACIO_DEFPRC, getMessage("txt.exportacio.definicioProces") ));
		resposta.add(new ParellaCodiValorDto(TIPUS_EXPORTACIO_TIPEXP, getMessage("txt.exportacio.tipusExpedient") ));
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
				missatgeError(request, getMessage("error.permisos.despl.arxius.entorn"));
				return "redirect:/definicioProces/deploy.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
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
		        		if (command.getTipus().equals(TIPUS_EXPORTACIO_JBPM)) {
		        			DefinicioProces dp = dissenyService.deploy(
				        			entorn.getId(),
				        			command.getExpedientTipusId(),
				        			multipartFile.getOriginalFilename(),
				        			multipartFile.getBytes(),
				        			command.getEtiqueta(),
				        			true);
				        	missatgeInfo(request, getMessage("info.arxiu.desplegat"));
//				        	if (command.getExpedientTipusId() != null && command.isActualitzarProcessosActius()) {
				        	if (command.isActualitzarProcessosActius()) {
				        		try {
//					        		expedientService.actualitzarProcessInstancesADarreraVersio(
//					        				dp.getJbpmKey());
//					        		missatgeInfo(request, getMessage("info.arxiu.desplegat.actualitzat"));
					        		
					        		// Obtenim informació de l'execució massiva
					    			
				    				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				    				dto.setDataInici(new Date());
				    				dto.setEnviarCorreu(false);
				    				dto.setParam1(dp.getJbpmKey());
				    				dto.setParam2(execucioMassivaService.serialize(Integer.valueOf(dp.getVersio())));
//				    				dto.setExpedientTipusId(command.getExpedientTipusId());
				    				dto.setTipus(ExecucioMassivaTipus.ACTUALITZAR_VERSIO_DEFPROC);
				    				List<JbpmProcessInstance> procInstances = expedientService.findProcessInstancesWithProcessDefinitionNameAndEntorn(dp.getJbpmKey(), entorn.getId());
				    				List<String> pi_ids = new ArrayList<String>();
//				    				List<Long> exp_ids = new ArrayList<Long>();
				    				for (JbpmProcessInstance pi: procInstances) {
				    					pi_ids.add(pi.getId());
//				    					exp_ids.add(expedientService.findExpedientAmbProcessInstanceId(pi.getId()).getId());
				    				}
//				    				dto.setExpedientIds(exp_ids);
				    				dto.setProcInstIds(pi_ids);
				    				execucioMassivaService.crearExecucioMassiva(dto);
				    				
				    				missatgeInfo(request, getMessage("info.canvi.versio.massiu", new Object[] {pi_ids.size()}));
				    			} catch (Exception e) {
				    				missatgeError(request, getMessage("error.no.massiu"));
				    				logger.error("Error al programar les actualitzacions de versuó de procés", e);
				    			}
				        	}
		        		} else if (command.getTipus().equals(TIPUS_EXPORTACIO_DEFPRC)) {
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
					    		missatgeInfo(request, getMessage("info.arxiu.desplegat") );
					        	return "redirect:/definicioProces/llistat.html";
					    	} else {
					    		missatgeError(request, getMessage("error.arxius.no.valid") );
					    	}
		        		} else if (command.getTipus().equals(TIPUS_EXPORTACIO_TIPEXP)) {
		        			InputStream is = new ByteArrayInputStream(multipartFile.getBytes());
		    		    	ObjectInputStream input = new ObjectInputStream(is);
		    		    	Object deserialitzat = input.readObject();
		    		    	if (deserialitzat instanceof ExpedientTipusExportacio) {
		    		    		ExpedientTipusExportacio exportacio = (ExpedientTipusExportacio)deserialitzat;
		    		    		dissenyService.importarExpedientTipus(
		    		        			entorn.getId(), 
		    		        			null,
		    		        			exportacio);
		    		    		missatgeInfo(request, getMessage("info.dades.importat") );
		    						return "redirect:/expedientTipus/llistat.html";
		    		    	} else {
		    		    		missatgeError(request, getMessage("error.arxiu.no.valid") );
		    		    	}
		        		}
						return "redirect:/definicioProces/llistat.html";
		        	} catch (ClassNotFoundException ex) {
		        		missatgeError(request, getMessage("error.arxius.no.valid"), ex.getMessage());
		        	} catch (IOException ex) {
		        		missatgeError(request, getMessage("error.desplegar.arxius"), ex.getMessage());
		        	} catch (DeploymentException ex) {
		        		missatgeError(request, getMessage("error.desplegar.arxius"), ex.getMessage());
		        	}
		        	return "redirect:/definicioProces/deploy.html";
				} else {
					return "redirect:/definicioProces/llistat.html";
				}
			} else {
				missatgeError(request, getMessage("error.permisos.despl.arxius.entorn"));
				return "redirect:/definicioProces/deploy.html";
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
		private boolean actualitzarProcessosActius;
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
		public boolean isActualitzarProcessosActius() {
			return actualitzarProcessosActius;
		}
		public void setActualitzarProcessosActius(boolean actualitzarProcessosActius) {
			this.actualitzarProcessosActius = actualitzarProcessosActius;
		}
	}

	private class DeployValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
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

	private static final Log logger = LogFactory.getLog(DefinicioProcesDeployController.class);

}
