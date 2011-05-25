/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.exception.NotFoundException;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.TascaFormUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;



/**
 * Controlador per la gestió dels documents de les tasques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class TascaDocumentsController extends BaseController {

	private TascaService tascaService;



	@Autowired
	public TascaDocumentsController(
			TascaService tascaService) {
		this.tascaService = tascaService;
	}

	@SuppressWarnings("unchecked")
	@ModelAttribute("commandReadOnly")
	public Object populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				TascaDto tasca = tascaService.getById(entorn.getId(), id);
				Map<String, Object> campsAddicionals = new HashMap<String, Object>();
				campsAddicionals.put("id", id);
				campsAddicionals.put("entornId", entorn.getId());
				campsAddicionals.put("procesScope", null);
				Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
				campsAddicionalsClasses.put("id", String.class);
				campsAddicionalsClasses.put("entornId", Long.class);
				campsAddicionalsClasses.put("procesScope", Map.class);
				Object command = TascaFormUtil.getCommandForTasca(
						tasca,
						campsAddicionals,
						campsAddicionalsClasses);
				return command;
			} catch (NotFoundException ignored) {}
		}
		return null;
	}

	@RequestMapping(value = "/tasca/documents")
	public String documents(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (model.get("commandReadOnly") == null) {
				missatgeError(request, "Aquesta tasca ja no està disponible");
				return "redirect:/tasca/personaLlistat.html";
			}
			TascaDto tasca = tascaService.getById(entorn.getId(), id);
			model.addAttribute("tasca", tasca);
			for (DocumentTasca document: tasca.getDocuments()) {
				DocumentExpedientCommand command = new DocumentExpedientCommand();
				command.setData(new Date());
				model.addAttribute(
						"documentCommand_" + document.getDocument().getCodi(),
						command);
			}
			return "tasca/documents";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/documentGuardar")
	public String documentGuardar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "codi", required = true) String codi,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam("contingut") final MultipartFile multipartFile,
			@ModelAttribute("command") DocumentExpedientCommand command,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (model.get("commandReadOnly") == null) {
				missatgeError(request, "Aquesta tasca ja no està disponible");
				return "redirect:/tasca/personaLlistat.html";
			}
			if ("submit".equals(submit) || submit.length() == 0) {
				if (command.getData() != null && command.getContingut() != null) {
					if (multipartFile.getSize() > 0) {
						try {
							String nomArxiu = multipartFile.getOriginalFilename();
							tascaService.guardarDocument(
									entorn.getId(),
									id,
									codi,
									nomArxiu,
									command.getData(),
									command.getContingut());
							missatgeInfo(request, "El document s'ha guardat correctament");
				        } catch (Exception ex) {
				        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
				        	logger.error("No s'ha pogut guardar el document", ex);
				        }
					} else {
						missatgeError(request, "S'ha d'especificar un document");
					}
				} else {
					missatgeError(request, "Ha d'especificar la data");
				}
			}
			return "redirect:/tasca/documents.html?id=" + id;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/documentEsborrar")
	public String documentEsborrar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "codi", required = true) String codi,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (model.get("commandReadOnly") == null) {
				missatgeError(request, "Aquesta tasca ja no està disponible");
				return "redirect:/tasca/personaLlistat.html";
			}
			try {
				tascaService.esborrarDocument(
						entorn.getId(),
						id,
						codi);
				missatgeInfo(request, "El document s'ha esborrat correctament");
	        } catch (Exception ex) {
	        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar el document", ex);
	        }
			return "redirect:/tasca/documents.html?id=" + id;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/documentDescarregar")
	public String documentDescarregar(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "codi", required = true) String codi) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DocumentDto document = tascaService.getDocument(
					entorn.getId(),
					id,
					codi);
			if (document != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, document.getArxiuNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, document.getArxiuContingut());
			}
			return "arxiuView";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/documentGenerar")
	public String documentGenerar(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "documentId", required = true) Long documentId,
			@RequestParam(value = "data", required = false) Date data) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				DocumentDto document = tascaService.generarDocumentPlantilla(
						entorn.getId(),
						documentId,
						id,
						(data != null) ? data : new Date());
				if (document != null) {
					model.addAttribute(
							ArxiuConvertirView.MODEL_ATTRIBUTE_FILENAME,
							document.getArxiuNom());
					model.addAttribute(
							ArxiuConvertirView.MODEL_ATTRIBUTE_DATA,
							document.getArxiuContingut());
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
	        	logger.error("Error generant el document " + documentId + " per la tasca " + id, ex);
	        	return "redirect:/tasca/documents.html?id=" + id;
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
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


	private static final Log logger = LogFactory.getLog(TascaDocumentsController.class);

}
