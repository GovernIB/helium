/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;



/**
 * Controlador per la gestió de definicions de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/definicioProces")
public class DefinicioProcesController extends BaseController {

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public DefinicioProcesController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	@RequestMapping(value = "llistat", method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (potDissenyarEntorn(entorn)) {
				model.addAttribute("llistat", dissenyService.findDarreresAmbEntorn(entorn.getId()));
				return "definicioProces/llistat";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.entorn") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "info", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("command", new DeployCommand());
				model.addAttribute("definicioProces", dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId));
				model.addAttribute("subDefinicionsProces", dissenyService.findSubDefinicionsProces(definicioProcesId));
				return "definicioProces/info";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "delete")
	public String delete(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				try {
					List<ExpedientDto> expedients = expedientService.findAmbDefinicioProcesId(definicioProcesId);
					List<Consulta> consultes = dissenyService.findConsultesAmbEntorn(entorn.getId());
					boolean esborrar = true;
					if (expedients.size() == 0) {
						for(Consulta consulta: consultes){
							Set<ConsultaCamp> llistat = consulta.getCamps();
							for(ConsultaCamp c: llistat){
								if((definicioProces.getVersio() == c.getDefprocVersio()) && (definicioProces.getJbpmKey().equals(c.getDefprocJbpmKey()))){
									esborrar = false;
								}
							}
						}
						if(!esborrar){
							missatgeError(request, getMessage("error.exist.cons") );
						} else {
							dissenyService.undeploy(entorn.getId(), null, definicioProcesId);
			        		missatgeInfo(request, getMessage("info.defproc.esborrat") );
						}
					} else {
						missatgeError(request, getMessage("error.exist.exp.defproc") );
					}
		        } catch (Exception ex) {
		        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar la definició de procés", ex);
		        	return "redirect:/definicioProces/info.html?definicioProcesId=" + definicioProcesId;
		        }
				return "redirect:/definicioProces/llistat.html";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "export")
	public String export(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				String filename = definicioProces.getJbpmKey();
				if (definicioProces.getEtiqueta() != null)
					filename = filename + "_" + definicioProces.getEtiqueta() + ".exp";
				else
					filename = filename + "_v" + definicioProces.getVersio() + ".exp";
				model.addAttribute("filename", filename);
				model.addAttribute("data", dissenyService.exportar(definicioProcesId));
				return "serialitzarView";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "configurar")
	public String configurar(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam("arxiu") final MultipartFile multipartFile,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				InputStream is = new ByteArrayInputStream(multipartFile.getBytes());
		    	ObjectInputStream input = new ObjectInputStream(is);
		    	Object deserialitzat = input.readObject();
		    	if (deserialitzat instanceof DefinicioProcesExportacio) {
		    		DefinicioProcesExportacio exportacio = (DefinicioProcesExportacio)deserialitzat;
		    		dissenyService.configurarAmbExportacio(
		        			entorn.getId(),
		        			definicioProcesId,
		        			exportacio);
		    		missatgeInfo(request, getMessage("info.dades.importat") );
		        	return "redirect:/definicioProces/llistat.html";
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
				logger.error("Error al importar les dades", ex);
				if (ex.getMessage() != null)
					missatgeError(request, getMessage("error.import.dades") + ex.getMessage());
				else
					missatgeError(request, getMessage("error.import.dades"));
			}
			return "redirect:/definicioProces/info.html?definicioProcesId=" + definicioProcesId;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "recursLlistat")
	public String resources(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId));
				model.addAttribute("recursos", dissenyService.findDeploymentResources(definicioProcesId));
				return "definicioProces/recursLlistat";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "recursDescarregar")
	public String resource(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "resourceName", required = true) String resourceName) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_FILENAME,
						resourceName);
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_DATA,
						dissenyService.getDeploymentResource(definicioProcesId, resourceName));
				return "arxiuView";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
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
		private Long definicioProcesId;
		private byte[] arxiu;
		public Long getDefinicioProcesId() {
			return definicioProcesId;
		}
		public void setDefinicioProcesId(Long definicioProcesId) {
			this.definicioProcesId = definicioProcesId;
		}
		public byte[] getArxiu() {
			return arxiu;
		}
		public void setArxiu(byte[] arxiu) {
			this.arxiu = arxiu;
		}
	}

	private static final Log logger = LogFactory.getLog(DefinicioProcesController.class);

}
