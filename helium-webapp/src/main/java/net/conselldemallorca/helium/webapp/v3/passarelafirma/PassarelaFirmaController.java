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

import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

/**
 * Controller per a les accions de la passarel·la de firma.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping(value = PassarelaFirmaHelper.CONTEXTWEB)
public class PassarelaFirmaController {

	public static final boolean stepSelectionWhenOnlyOnePlugin = true;

	@Autowired
	private PassarelaFirmaHelper passarelaFirmaHelper;



	@RequestMapping(value = "/selectsignmodule/{signaturesSetId}")
	public String selectSignModules(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("signaturesSetId") String signaturesSetId,
			Model model) throws Exception {
		List<PassarelaFirmaPlugin> pluginsFiltered = passarelaFirmaHelper.getAllPlugins(
				request,
				signaturesSetId);
		// Si només hi ha un mòdul de firma llavors anar a firmar directament
		if (stepSelectionWhenOnlyOnePlugin) {
			if (pluginsFiltered.size() == 1) {
				PassarelaFirmaPlugin modul = pluginsFiltered.get(0);
				long pluginID = modul.getPluginId();
				return "redirect:" +
						PassarelaFirmaHelper.CONTEXTWEB + "/showsignaturemodule/" +
						pluginID + "/" + signaturesSetId;
			}
		}
		PassarelaFirmaConfig pfss = passarelaFirmaHelper.getSignaturesSet(
				request,
				signaturesSetId);
		// Si cap modul compleix llavors mostrar missatge
		if (pluginsFiltered.size() == 0) {
			String msg = "No existeix cap mòdul de firma que passi els filtres";
			if (pfss == null) {
				MissatgesHelper.error(request, msg);
			} else {
				StatusSignaturesSet sss = pfss.getStatusSignaturesSet();
				sss.setErrorMsg(msg);
				sss.setErrorException(null);
				sss.setStatus(StatusSignaturesSet.STATUS_FINAL_ERROR);
			}
			return "redirect:" + pfss.getUrlFinal();
		}
		model.addAttribute("documentId", pfss.getDocumentId());
		model.addAttribute("signaturesSetId", signaturesSetId);
		model.addAttribute("plugins", pluginsFiltered);
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
		String urlToPluginWebPage = passarelaFirmaHelper.signDocuments(
				request,
				signaturesSetID);
		return new RedirectView(urlToPluginWebPage, false);
	}

	private static final String REQUEST_PLUGIN_MAPPING = "/requestPlugin/{signaturesSetId}/{signatureIndex}/**";
	@RequestMapping(value = REQUEST_PLUGIN_MAPPING)
	public void requestPlugin(
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
		
		// TODO: BORRAR - Codi per proves!!
//		if (query.equalsIgnoreCase("isfinished")) {
//			query = "discover";
//		}
		// Fi
		
		
		passarelaFirmaHelper.requestPlugin(
				request,
				response,
				signaturesSetId,
				signatureIndex,
				query);
	}

	@RequestMapping(value = "/final/{signaturesSetId}")
	public String finalProcesDeFirma(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("signaturesSetId") String signaturesSetId) throws Exception {
		PassarelaFirmaConfig pss = passarelaFirmaHelper.finalitzarProcesDeFirma(
				request,
				signaturesSetId);
		return "redirect:" + pss.getUrlFinalHelium() + "?signaturesSetId=" + signaturesSetId;
	}



	protected static Logger log = LoggerFactory.getLogger(PassarelaFirmaController.class);

}
