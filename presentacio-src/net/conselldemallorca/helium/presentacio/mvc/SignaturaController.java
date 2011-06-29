/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.io.ObjectInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.integracio.plugins.signatura.applet.RespostaSignatura;
import net.conselldemallorca.helium.model.dto.ArxiuDto;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.service.DocumentService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;

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
 * @author Josep Gayà <josepg@limit.es>
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

	@RequestMapping(value = "/signatura/descarregarAmbToken")
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
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/signatura/signarAmbTokenCaib", method = RequestMethod.POST)
	public void signarDocument(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="data", required = true) final MultipartFile multipartFile) throws ServletException {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				ObjectInputStream inputFromApplet = new ObjectInputStream(multipartFile.getInputStream());
				RespostaSignatura resposta = (RespostaSignatura)inputFromApplet.readObject();
				tascaService.signarDocumentAmbToken(
						entorn.getId(),
						resposta.getToken(),
						(byte[])resposta.getSignatura());
				logger.info("Firma del document amb el token " + resposta.getToken() + " processada correctament");
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
					missatgeInfo(request, "La signatura del document s'ha processat correctament");
				} else {
					logger.error("Signatura del document amb el token " + token + " processada amb error de custòdia");
					missatgeError(request, "Error en la validació de la signatura");
				}
			} catch(Exception ex) {
				logger.error("Error rebent la signatura del document", ex);
				missatgeError(request, "Error rebent la signatura del document");
		    }
			return "redirect:/tasca/signatures.html?id=" + taskId;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
				document = expedientService.getDocument(id, false, false, false);
			else if (token != null)
				document = tascaService.getDocumentAmbToken(token, true);
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

	/*@RequestMapping(value = "/signatura/descarregar")
	public String descarregar(
			HttpServletRequest request,
			@RequestParam("id") final Long id,
			ModelMap model) {
		DocumentDto document = expedientService.descarregarSignatura(id);
		if (document != null) {
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, document.getArxiuNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, document.getArxiuContingut());
			return "arxiuView";
		}
		return null;
	}*/



	private static final Log logger = LogFactory.getLog(SignaturaController.class);

}
