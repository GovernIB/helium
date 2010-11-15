/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.io.ObjectInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.integracio.plugins.signatura.applet.RespostaSignatura;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;

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



	@Autowired
	public SignaturaController(
			TascaService tascaService,
			ExpedientService expedientService,
			DissenyService dissenyService) {
		this.tascaService = tascaService;
		this.expedientService = expedientService;
	}

	@RequestMapping(value = "/signatura/descarregarAmbToken")
	public String documentAmbToken(
			HttpServletRequest request,
			@RequestParam(value = "token", required = true) String token,
			@RequestParam(value = "noe", required = false) String noe,
			ModelMap model) {
		DocumentDto document = tascaService.getDocumentAmbToken(token, true);
		if (document != null) {
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, document.getArxiuNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, document.getArxiuContingut());
			return "arxiuView";
		}
		return null;
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
				/*FileOutputStream fos = new FileOutputStream("c:/signat");
				fos.write(Base64.decode(data));
				fos.close();*/
				tascaService.signarDocumentAmbToken(
						entorn.getId(),
						token,
						data.getBytes());
				logger.info("Firma del document amb el token " + token + " processada correctament");
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
	public String verificarDocument(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("id") final String id,
			ModelMap model) throws ServletException {
		try {
			DocumentDto document = expedientService.getDocument(new Long(id));
			model.addAttribute("document", document);
			model.addAttribute(
					"instanciaProces",
					expedientService.getInstanciaProcesById(
							document.getProcessInstanceId(),
							false));
			model.addAttribute("signatures", expedientService.verificarDocument(new Long(id)));
			return "signatura/verificar";
		} catch(Exception ex) {
			logger.error("Error rebent la firma del document", ex);
			throw new ServletException(ex);
	    }
	}



	private static final Log logger = LogFactory.getLog(SignaturaController.class);

}
