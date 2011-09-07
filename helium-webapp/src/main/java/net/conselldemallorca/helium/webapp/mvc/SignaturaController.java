/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.io.ObjectInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.DocumentService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;



/**
 * Controlador per a la signatura de documents
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class SignaturaController extends BaseController {

	private TascaService tascaService;
	private ExpedientService expedientService;
	private DocumentService documentService;



	@Autowired
	public SignaturaController(
			TascaService tascaService,
			ExpedientService expedientService,
			DocumentService documentService) {
		this.tascaService = tascaService;
		this.expedientService = expedientService;
		this.documentService = documentService;
	}

	/*@RequestMapping(value = "/signatura/arxiuPerSignar")
	public String documentAmbToken(
			HttpServletRequest request,
			@RequestParam(value = "token", required = true) String token,
			@RequestParam(value = "noe", required = false) Boolean noe,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			boolean estampar = (noe != null) ? !noe.booleanValue() : true;
			ArxiuDto arxiu = documentService.arxiuDocumentPerSignar(token, estampar);
			if (arxiu != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
			}
			return "arxiuView";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}*/

	@RequestMapping(value = "/signatura/signarAmbTokenCaib", method = RequestMethod.POST)
	public void signarDocument(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="data", required = true) final MultipartFile multipartFile) throws ServletException {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				ObjectInputStream inputFromApplet = new ObjectInputStream(multipartFile.getInputStream());
				Object[] resposta = (Object[])inputFromApplet.readObject();
				tascaService.signarDocumentAmbToken(
						entorn.getId(),
						(String)resposta[0],
						(byte[])resposta[2]);
				logger.info("Firma del document amb el token " + resposta[0] + " processada correctament");
				response.getWriter().write("OK");
			} catch(Exception ex) {
				logger.error("Error rebent la firma del document", ex);
				throw new ServletException(ex);
		    }
		} else {
			try {
				response.getWriter().write("KO");
			} catch(Exception ex) {
				logger.error("Error al escriure la resposta de la signatura", ex);
		    }
		}
	}

	@RequestMapping(value = "/signatura/signarAmbTokenAFirma", method = RequestMethod.POST)
	public String signarDocumentAFirma(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="taskId", required = true) String taskId,
			@RequestParam(value="token", required = true) String token,
			@RequestParam(value="data", required = true) String data) throws ServletException {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				boolean custodiat = tascaService.signarDocumentAmbToken(
						entorn.getId(),
						token,
						Base64.decodeBase64(data.getBytes()));
				if (custodiat) {
					logger.info("Signatura del document amb el token " + token + " processada correctament");
					missatgeInfo(request, getMessage("info.signatura.doc.processat") );
				} else {
					logger.error("Signatura del document amb el token " + token + " processada amb error de custòdia");
					missatgeError(request, getMessage("error.validar.signatura") );
				}
			} catch(Exception ex) {
				logger.error("Error rebent la signatura del document", ex);
				missatgeError(request, getMessage("error.rebre.signatura") );
		    }
			return "redirect:/tasca/signatures.html?id=" + taskId;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = {"/signatura/verificar", "/signatura/verificarIntern"}, method = RequestMethod.GET)
	public String verificarSignatura(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "id", required = false) final Long id,
			@RequestParam(value = "token", required = false) final String token,
			ModelMap model) throws ServletException {
		try {
			DocumentDto document = null;
			if (id != null)
				document = expedientService.getDocument(
						id,
						false,
						false,
						false);
			else if (token != null)
				document = documentService.arxiuDocumentInfo(token);
			model.addAttribute("document", document);
			model.addAttribute(
					"instanciaProces",
					expedientService.getInstanciaProcesById(
							document.getProcessInstanceId(),
							false));
			model.addAttribute("signatures", expedientService.verificarSignatura(document.getId()));
			return "signatura/verificar";
		} catch(Exception ex) {
			logger.error("Error rebent la firma del document", ex);
			throw new ServletException(ex);
	    }
	}



	private static final Log logger = LogFactory.getLog(SignaturaController.class);

}
