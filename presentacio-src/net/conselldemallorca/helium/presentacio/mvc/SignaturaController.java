/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.io.ObjectInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaSignatura;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;
import net.conselldemallorca.helium.util.GlobalProperties;

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
	private DissenyService dissenyService;



	@Autowired
	public SignaturaController(
			TascaService tascaService,
			ExpedientService expedientService,
			DissenyService dissenyService) {
		this.tascaService = tascaService;
		this.expedientService = expedientService;
		this.dissenyService = dissenyService;
	}

	@RequestMapping(value = "/signatura/descarregarAmbToken")
	public String documentAmbToken(
			HttpServletRequest request,
			@RequestParam(value = "token", required = true) String token,
			ModelMap model) {
		DocumentDto document = tascaService.getDocumentAmbToken(token, true);
		if (document != null) {
			if (!document.isSignat()) {
				String conversioActiu = (String)GlobalProperties.getInstance().get("app.conversio.signatura.actiu");
				String extensio = (String)GlobalProperties.getInstance().get("app.conversio.signatura.extension");
				String estampaActiu = (String)GlobalProperties.getInstance().get("app.conversio.signatura.estampa.actiu");
				String estampaPosX = (String)GlobalProperties.getInstance().get("app.conversio.signatura.estampa.posx");
				String estampaPosY = (String)GlobalProperties.getInstance().get("app.conversio.signatura.estampa.posy");
				String estampaRotation = (String)GlobalProperties.getInstance().get("app.conversio.signatura.estampa.rotation");
				model.addAttribute(
						ArxiuConvertirView.MODEL_ATTRIBUTE_FILENAME,
						document.getArxiuNom());
				model.addAttribute(
						ArxiuConvertirView.MODEL_ATTRIBUTE_DATA,
						document.getArxiuContingut());
				boolean conversionEnabled = ("true".equalsIgnoreCase(conversioActiu));
				model.addAttribute(
						ArxiuConvertirView.MODEL_ATTRIBUTE_CONVERSIONENABLED,
						conversionEnabled);
				model.addAttribute(
						ArxiuConvertirView.MODEL_ATTRIBUTE_OUTEXTENSION,
						extensio);
				if ("true".equalsIgnoreCase(estampaActiu)) {
					model.addAttribute(
							ArxiuConvertirView.MODEL_ATTRIBUTE_ESTAMPA,
							(String)GlobalProperties.getInstance().get("app.base.url") + "/signatura/verificar.html?id=" + token);
					model.addAttribute(
							ArxiuConvertirView.MODEL_ATTRIBUTE_ESTAMPA_POSX,
							new Float(estampaPosX));
					model.addAttribute(
							ArxiuConvertirView.MODEL_ATTRIBUTE_ESTAMPA_POSY,
							new Float(estampaPosY));
					model.addAttribute(
							ArxiuConvertirView.MODEL_ATTRIBUTE_ESTAMPA_ROTATION,
							new Float(estampaRotation));
				}
				return "arxiuConvertirView";
			} else {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, document.getArxiuNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, document.getArxiuContingut());
				return "arxiuView";
			}
		}
		return null;
	}

	@RequestMapping(value = "/signatura/signarAmbToken", method = RequestMethod.POST)
	public void signarDocument(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("data") final MultipartFile multipartFile) throws ServletException {
		try {
			ObjectInputStream inputFromApplet = new ObjectInputStream(multipartFile.getInputStream());
			RespostaSignatura resposta = (RespostaSignatura)inputFromApplet.readObject();
			tascaService.signarDocumentAmbToken(
					resposta.getToken(),
					resposta.getArxiuNom(),
					resposta.getSignatura());
			logger.info("Firma del document amb el token " + resposta.getToken() + " processada correctament");
			response.getWriter().write("OK");
		} catch(Exception ex) {
			logger.error("Error rebent la firma del document", ex);
			throw new ServletException(ex);
	    }
	}

	@RequestMapping(value = "/signatura/verificar", method = RequestMethod.GET)
	public String verificarDocument(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("id") final String id,
			ModelMap model) throws ServletException {
		try {
			DocumentDto document = tascaService.getDocumentAmbToken(id, false);
			InstanciaProcesDto ip = expedientService.getInstanciaProcesById(
					document.getProcessInstanceId(),
					false);
			model.addAttribute("instanciaProces", ip);
			model.addAttribute(
					"doccamp",
					dissenyService.findDocumentAmbDefinicioProcesICodi(
							ip.getDefinicioProces().getId(),
							document.getDocumentCodi()));
			model.addAttribute("document", document);
			model.addAttribute("signatures", expedientService.verificarDocument(new Long(id)));
			return "signatura/verificar";
		} catch(Exception ex) {
			logger.error("Error rebent la firma del document", ex);
			throw new ServletException(ex);
	    }
	}



	private static final Log logger = LogFactory.getLog(SignaturaController.class);

}
