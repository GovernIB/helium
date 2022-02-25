package net.conselldemallorca.helium.webapp.v3.passarelafirma;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.fundaciobit.plugins.signature.api.StatusSignaturesSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import net.conselldemallorca.helium.integracio.plugins.firmaweb.FirmaWebPluginPortafibRest;
import net.conselldemallorca.helium.webapp.v3.controller.BaseController;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

/**
 * Controller per a les accions de la passarel·la de firma.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping(value = PassarelaFirmaHelper.CONTEXTWEB)
public class PassarelaFirmaController extends BaseController {

	public static final boolean stepSelectionWhenOnlyOnePlugin = true;

	@Autowired
	private PassarelaFirmaHelper passarelaFirmaHelper;



	@RequestMapping(value = "/selectsignmodule/{signaturesSetId}")
	public String selectSignModules(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("signaturesSetId") String signaturesSetId,
			Model model) throws Exception {
		try {
			List<FirmaWebPluginPortafibRest> pluginsFiltered = passarelaFirmaHelper.getAllPlugins(
				request,
				signaturesSetId);
			// Si només hi ha un mòdul de firma llavors anar a firmar directament
			if (stepSelectionWhenOnlyOnePlugin) {
				if (pluginsFiltered.size() == 1) {
					FirmaWebPluginPortafibRest modul = pluginsFiltered.get(0);
					long pluginID = modul.getPluginId();
					return "redirect:" +
							PassarelaFirmaHelper.CONTEXTWEB + "/showsignaturemodule/" +
							pluginID + "/" + signaturesSetId;
				}
			}
			// Si hi ha més d'un mòdul de firma llavors els mostra llistats
			PassarelaFirmaConfig pfss = passarelaFirmaHelper.getSignaturesSet(
					request,
					signaturesSetId);
			// Si cap modul compleix llavors mostrar missatge
			if (pluginsFiltered.size() == 0) {
				String msg = getMessage(request, "passarelafirma.controller.error.filtres");
				MissatgesHelper.error(request, msg);
				if (pfss != null) {
					StatusSignaturesSet sss = pfss.getStatusSignaturesSet();
					sss.setErrorMsg(msg);
					sss.setErrorException(null);
					sss.setStatus(StatusSignaturesSet.STATUS_FINAL_ERROR);
				}
			} else {
				model.addAttribute("documentId", pfss.getDocumentId());
				model.addAttribute("signaturesSetId", signaturesSetId);
				model.addAttribute("plugins", pluginsFiltered);
			}
		} catch(Exception e) {
			MissatgesHelper.error(request, e.getMessage());
		}
		return "v3/passarelaFirma/passarelaFirmaSeleccio";
	}

	@RequestMapping(value = "/showsignaturemodule/{pluginId}/{signaturesSetID}")
	public RedirectView showSignatureModule(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("pluginId") Long pluginId,
			@PathVariable("signaturesSetID") String signaturesSetID) throws Exception {
		PassarelaFirmaConfig pfss = passarelaFirmaHelper.getSignaturesSet(
				request,
				signaturesSetID);
		pfss.setPluginId(pluginId);
		String urlToPluginWebPage;
		
		try {
			urlToPluginWebPage = passarelaFirmaHelper.signDocuments(
					request,
					signaturesSetID,
					pfss.getUrlFinal());
		} catch (Throwable e) {
			log.error(e.getMessage());
			urlToPluginWebPage = ESQUEMA_PREFIX + PassarelaFirmaHelper.CONTEXTWEB + "/selectsignmodule/" + signaturesSetID;
			MissatgesHelper.error(
					request,
					getMessage(request, "error.signatura", new Object[] {e.getLocalizedMessage()}));
		}
		return new RedirectView(urlToPluginWebPage, false);
	}

	private static final String REQUEST_PLUGIN_MAPPING = "/requestPlugin/{signaturesSetId}/{signatureIndex}/**";
	@RequestMapping(value = REQUEST_PLUGIN_MAPPING)
	public String requestPlugin(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String signaturesSetId,
			@PathVariable int signatureIndex) throws Exception {
		String servletPath = request.getServletPath();
		int indexBarra = StringUtils.ordinalIndexOf(
				servletPath,
				"/",
				StringUtils.countMatches(
						PassarelaFirmaHelper.CONTEXTWEB + REQUEST_PLUGIN_MAPPING,
						"/"));
		String query = servletPath.substring(indexBarra + 1);		
		
		return passarelaFirmaHelper.requestPlugin(
				request,
				response,
				signaturesSetId,
				signatureIndex,
				query);
	}

	//@RequestMapping(value = "/final/{signaturesSetId}")
	//@RequestMapping(value = "/final")
	//@RequestMapping(value = "/{tascaId}/document/{documentCodi}/firmaPassarelaFinal/{signaturesSetId}")
//	@RequestMapping(value = "/firmaPassarelaFinal/{signaturesSetId}/**")
//	public String finalProcesDeFirma(
//			HttpServletRequest request,
//			HttpServletResponse response,
//			@PathVariable("signaturesSetId") String signaturesSetId)
//			//@PathVariable("transactionId") String transactionId) 
//			throws Exception {
//		PassarelaFirmaConfig pss = passarelaFirmaHelper.finalitzarProcesDeFirma(
//				request,
//				signaturesSetId);
//				//transactionId);
//				//pss.getTransactionId());
//		return "redirect:" + pss.getUrlFinalHelium() + "?signaturesSetId=" + signaturesSetId;
//	}



	protected static Logger log = LoggerFactory.getLogger(PassarelaFirmaController.class);

}
