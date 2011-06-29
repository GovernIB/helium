/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.hibernate.Document;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;
import net.conselldemallorca.helium.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.util.GlobalProperties;

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



/**
 * Controlador per a modificar els documents dels expedients
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class ExpedientDocumentModificarController extends BaseController {

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientDocumentModificarController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("command")
	public DocumentExpedientCommand populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "docId", required = true) Long docId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DocumentExpedientCommand command = new DocumentExpedientCommand();
			DocumentDto dto = expedientService.getDocument(docId, false, false, false);
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, false);
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
		return null;
	}

	@RequestMapping(value = "/expedient/documentModificar", method = RequestMethod.GET)
	public String documentModificarGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "docId", required = true) Long docId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				DocumentDto doc = expedientService.getDocument(docId, false, false, false);
				if (!doc.isSignat()) {
					model.addAttribute("expedient", expedient);
					model.addAttribute("document", doc);
					model.addAttribute(
							"documentDisseny",
							dissenyService.getDocumentById(docId));
					return "expedient/documentForm";
				} else {
					missatgeError(request, "No es pot modificar un document signat");
					return "redirect:/expedient/documents.html?id=" + id;
				}
			} else {
				missatgeError(request, "No té permisos per modificar aquest expedient");
			}
			return "redirect:/expedient/consulta.html";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/documentModificar", method = RequestMethod.POST)
	public String documentModificarPost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam("contingut") final MultipartFile multipartFile,
			@ModelAttribute("command") DocumentExpedientCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					new DocumentModificarValidator().validate(command, result);
			        if (result.hasErrors()) {
			        	model.addAttribute("expedient", expedient);
						model.addAttribute("document", expedientService.getDocument(command.getDocId(), false, false, false));
			        	return "expedient/documentForm";
			        }
					try {
						DocumentDto doc = expedientService.getDocument(command.getDocId(), false, false, false);
						if (!doc.isAdjunt()) {
							expedientService.guardarDocument(
									id,
									doc.getDocumentId(),
									command.getData(),
									(multipartFile.getSize() > 0) ? multipartFile.getOriginalFilename() : null,
									(multipartFile.getSize() > 0) ? command.getContingut() : null);
						} else {
							expedientService.guardarAdjunt(
									id,
									doc.getAdjuntId(),
									command.getNom(),
									command.getData(),
									(multipartFile.getSize() > 0) ? multipartFile.getOriginalFilename() : null,
									(multipartFile.getSize() > 0) ? command.getContingut() : null);
						}
						missatgeInfo(request, "El document s'ha guardat correctament");
			        } catch (Exception ex) {
			        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el document", ex);
			        }
				}
				return "redirect:/expedient/documents.html?id=" + id;
			} else {
				missatgeError(request, "No té permisos per modificar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/documentGenerar", method = RequestMethod.GET)
	public String documentGenerarGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "docId", required = true) Long docId,
			@RequestParam(value = "data", required = false) Date data,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				DocumentDto doc = expedientService.getDocument(docId, false, false, false);
				DocumentDto generat = expedientService.generarDocumentPlantilla(
						doc.getDocumentId(),
						id,
						(data != null) ? data : new Date());
				if (generat != null) {
					model.addAttribute(
							ArxiuConvertirView.MODEL_ATTRIBUTE_FILENAME,
							generat.getArxiuNom());
					model.addAttribute(
							ArxiuConvertirView.MODEL_ATTRIBUTE_DATA,
							generat.getArxiuContingut());
					model.addAttribute(
							ArxiuConvertirView.MODEL_ATTRIBUTE_CONVERSIONENABLED,
							"true".equalsIgnoreCase(GlobalProperties.getInstance().getProperty("app.conversio.gentasca.actiu")));
					model.addAttribute(
							ArxiuConvertirView.MODEL_ATTRIBUTE_OUTEXTENSION,
							GlobalProperties.getInstance().getProperty("app.conversio.gentasca.extension"));
				}
				return "arxiuConvertirView";				
			} catch (Exception ex) {
				missatgeError(request, "No s'ha pogut generar el document", ex.getLocalizedMessage());
	        	logger.error("Error generant el document " + docId + " per la instància de procés " + id, ex);
	        	return "redirect:/expedient/documentModificar.html?id=" + id + "&docId=" + docId;
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	public class DocumentModificarValidator implements Validator {
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Object.class);
		}
		public void validate(Object command, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "data", "not.blank");
		}
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



	private boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientDocumentModificarController.class);

}
