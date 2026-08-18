/**
 *
 */
package es.caib.helium.back.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import es.caib.helium.commons.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.service.AdminService;
import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.logic.intf.service.EntornService;
import es.caib.helium.logic.intf.service.PortafirmesFluxService;
import lombok.Builder;
import lombok.Data;

/**
 * Controlador per a la pàgina inicial (index).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class AplicacioController extends BaseController {

	@Autowired
	private AdminService adminService;
	@Autowired
	private EntornService entornService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private PortafirmesFluxService portafirmesFluxService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String get(HttpServletRequest request) {
		return "redirect:/index";
	}

	@RequestMapping(value = "/missatges", method = RequestMethod.GET)
	public String getMissatges(HttpServletRequest request) {
		return "missatges";//request.getSession().getAttribute(es.caib.helium.back.helper.MissatgesHelper.SESSION_ATTRIBUTE_SUCCESS)
	}

	@RequestMapping(value = "/utils/modalTancar", method = RequestMethod.GET)
	public String modalTancar() {
		return "utils/modalTancar";
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(HttpServletRequest request) {
		UsuariPreferenciesDto preferencies = SessionHelper.getSessionManager(request).getPreferenciesUsuari();
		if (preferencies != null) {
			if (preferencies.getListado() == 2 &&
					preferencies.getConsultaId() != null &&
					SessionHelper.getSessionManager(request).getEntornActual().getCodi().equals(preferencies.getDefaultEntornCodi())) {
				// Informes
				return "redirect:/informe?consultaId="+preferencies.getConsultaId();
			} else if (preferencies.getListado() == 1) {
				// Tareas
				return "redirect:/tasca";
			} else if (preferencies.getConsultaId() != null &&
						SessionHelper.getSessionManager(request).getEntornActual().getCodi().equals(preferencies.getDefaultEntornCodi())) {
				// Consulta per defecte
				return "redirect:/expedient/consulta/" + preferencies.getConsultaId();
			}
		}
		// Expedientes
		return "redirect:/expedient";
	}

	@RequestMapping(value = "/metrics", method = RequestMethod.GET)
	@ResponseBody
	public String metrics(
			HttpServletRequest request) {
		return adminService.getMetrics();
	}

	@RequestMapping(value = "/metriques", method = RequestMethod.GET)
	public String metricsView(
			HttpServletRequest request,
			Model model) {
		PersonaDto persona = (PersonaDto)request.getSession().getAttribute("dadesPersona");
		model.addAttribute("metriques", adminService.getMetrics());
		model.addAttribute("entorns",
				(persona != null && persona.isAdmin())?entornService.findActiusAll():
					(SessionHelper.getSessionManager(request).getEntornActual().isPermisAdministration() ? entornService.findActiusAmbPermisAdmin():new ArrayList<EntornDto>()));
		return "metrics";
	}



	/** Suggest pels valors inicials per a la selecció múltiple de usuaris o càrrecs des de l'edició de fluxos simples
	 * en el disseny de documents o enviament al portafirmes de documents des de la gestió de documents. Arriba un text amb els codis separats
	 * per coma "," on els condis de persones venen tal qual i els càrrecs arriben com "CARREC[codi_carrec]".
	 *
	 * @param text -
	 * @param model -
	 */
	@RequestMapping(value = "/personaCarrec/suggestInici/{text}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public List<CodiNom> personaCarrecSuggestInici(
			@PathVariable String text,
			Model model) {
		List<CodiNom> response = new ArrayList<CodiNom>();
		if (text != null) {
			String [] codis = text.split(",");
			es.caib.helium.commons.dto.PersonaDto persona;
			for (String codi : codis) {
				try {
					persona = aplicacioService.findPersonaCarrecAmbCodi(codi);
				} catch (Exception e) {
					persona = new PersonaDto();
					persona.setCodi(codi);
					persona.setNomSencer(codi + " (no trobat)");
				}
				response.add(CodiNom
					.builder()
					.codi(persona.getCodi())
					.nom(persona.getNomSencerCodi())
					.build());
			}
		}
		return response;
	}

	/** Consulta Ajax de la llista de càrrecs definida al Portafirmes. */
	@RequestMapping(value = "/portasig/carrecs", method = RequestMethod.GET)
	@ResponseBody
	public List<PortafirmesCarrecDto> recuperarCarrecs(
			HttpServletRequest request,
			Model model) {
		return portafirmesFluxService.recuperarCarrecs();
	}

	@RequestMapping(value = "/usernames", method = RequestMethod.GET)
	public String canviCodiUsuariView(
			HttpServletRequest request,
			Model model) {
		return "usuariCodiForm";
	}

	@RequestMapping(value = "/usernames/{codiAntic}/changeTo/{codiNou}", method = RequestMethod.POST, produces = "application/json" )
	@ResponseBody
	public UsuariChangeResponse setCanviCodis(
			HttpServletRequest request,
			@PathVariable("codiAntic") String codiAntic,
			@PathVariable("codiNou") String codiNou) {
		long t0 = System.currentTimeMillis();
		try {

			Long registresModificats = adminService.canviarCodiUsusari(codiAntic, codiNou);
			return UsuariChangeResponse.builder()
					.estat(ResultatEstatEnum.OK)
					.registresModificats(registresModificats)
					.duracio(System.currentTimeMillis() - t0)
					.build();
		} catch (Exception e) {
			return UsuariChangeResponse.builder()
					.estat(ResultatEstatEnum.ERROR)
					.errorMessage(getMessage(request, "usuari.codi.mapeig.error", null) + ": " + e.getMessage())
					.duracio(System.currentTimeMillis() - t0)
					.build();
		}
	}

	@Data
	@Builder
	public static class UsuariChangeValidation {
		private boolean usuariAnticExists;
		private boolean usuariNouExists;
	}

	@Data
	@Builder
	public static class UsuariChangeResponse {
		private ResultatEstatEnum estat;
		private String errorMessage;
		private Long registresModificats;
		private Long duracio;
	}

	public enum ResultatEstatEnum { OK, ERROR }

}
