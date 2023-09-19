/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesCarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.EntornService;
import net.conselldemallorca.helium.v3.core.api.service.PortafirmesFluxService;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pàgina inicial (index).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class AplicacioController {

	@Autowired
	private AdminService adminService;
	@Autowired
	private EntornService entornService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private PortafirmesFluxService portafirmesFluxService;
	@Autowired
	private EntornHelper entornHelper;


	@RequestMapping(value = "/v3", method = RequestMethod.GET)
	public String get(HttpServletRequest request) {
		return "redirect:/v3/index";
	}

	@RequestMapping(value = "/v3/missatges", method = RequestMethod.GET)
	public String getMissatges(HttpServletRequest request) {
		return "v3/missatges";//request.getSession().getAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_SUCCESS)
	}

	@RequestMapping(value = "/v3/utils/modalTancar", method = RequestMethod.GET)
	public String modalTancar() {
		return "v3/utils/modalTancar";
	}

	@RequestMapping(value = "/v3/index", method = RequestMethod.GET)
	public String index(HttpServletRequest request) {
		UsuariPreferenciesDto preferencies = SessionHelper.getSessionManager(request).getPreferenciesUsuari();
		if (preferencies != null) {
			if (preferencies.getListado() == 2 && 
					preferencies.getConsultaId() != null && 
					SessionHelper.getSessionManager(request).getEntornActual().getCodi().equals(preferencies.getDefaultEntornCodi())) {
				// Informes
				return "redirect:/v3/informe?consultaId="+preferencies.getConsultaId();
			} else if (preferencies.getListado() == 1) {
				// Tareas
				return "redirect:/v3/tasca";
			} else if (preferencies.getConsultaId() != null && 
						SessionHelper.getSessionManager(request).getEntornActual().getCodi().equals(preferencies.getDefaultEntornCodi())) {
				// Consulta per defecte
				return "redirect:/v3/expedient/consulta/" + preferencies.getConsultaId();
			} 
		}
		// Expedientes
		return "redirect:/v3/expedient";
	}

	@RequestMapping(value = "/v3/metrics", method = RequestMethod.GET)
	@ResponseBody
	public String metrics(
			HttpServletRequest request) {
		return adminService.getMetrics();
	}

	@RequestMapping(value = "/v3/metriques", method = RequestMethod.GET)
	public String metricsView(
			HttpServletRequest request,
			Model model) {
		PersonaDto persona = (PersonaDto)request.getSession().getAttribute("dadesPersona");
		model.addAttribute("metriques", adminService.getMetrics());
		model.addAttribute("entorns", 
				(persona != null && persona.isAdmin())?entornService.findActiusAll():
					(entornHelper.esAdminEntorn(EntornActual.getEntornId()))? entornService.findActiusAmbPermisAdmin():new ArrayList<EntornDto>());
		return "v3/metrics";
	}

	
	
	/** Suggest pels valors inicials per a la selecció múltiple de usuaris o càrrecs des de l'edició de fluxos simples
	 * en el disseny de documents o enviament al portafirmes de documents des de la gestió de documents. Arriba un text amb els codis separats
	 * per coma "," on els condis de persones venen tal qual i els càrrecs arriben com "CARREC[codi_carrec]".
	 * 
	 * @param text
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/v3/personaCarrec/suggestInici/{text}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public String personaCarrecSuggestInici(
			@PathVariable String text,
			Model model) {
		String response = null;
		if (text != null) {
			StringBuilder json = new StringBuilder("[");
			String [] codis = text.split(",");
			net.conselldemallorca.helium.v3.core.api.dto.PersonaDto persona;
			for (int i = 0; i < codis.length; i++) {
				try {
					persona = aplicacioService.findPersonaCarrecAmbCodi(codis[i]);				
				} catch(Exception e) {
					persona = new net.conselldemallorca.helium.v3.core.api.dto.PersonaDto();
					persona.setCodi(codis[i]);
					persona.setNomSencer(codis[i] + " (no trobat)");
				}
				json.append("{\"codi\":\"").append(persona.getCodi()).append("\", \"nom\":\"").append(persona.getNomSencer()).append("\"}");
				if (i < codis.length - 1) {
					json.append(",");
				}
			}
			json.append("]");
			response = json.toString();
		}
		return response;
	}
	
	/** Consulta Ajax de la llista de càrrecs definida al Portafirmes. */
	@RequestMapping(value = "/v3/portasig/carrecs", method = RequestMethod.GET)
	@ResponseBody
	public List<PortafirmesCarrecDto> recuperarCarrecs(
			HttpServletRequest request, 
			Model model) {
		return portafirmesFluxService.recuperarCarrecs();
	}
	


//	@RequestMapping(value = "/send/sistra1", method = RequestMethod.GET)
//	@ResponseBody
//	public String sendSistra1(HttpServletRequest request) {
//		try {
//			BantelFacade bantelFacade = ContextLoader.getCurrentWebApplicationContext().getBean(BantelFacade.class);
//			ReferenciasEntrada referenciasEntrada = new ReferenciasEntrada();
//			ReferenciaEntrada referenciaEntrada = new ReferenciaEntrada();
//			referenciaEntrada.setNumeroEntrada("BTE/" + getRandomNumber() + "/2023");
//			referenciaEntrada.setClaveAcceso(getRandomString());
//			referenciasEntrada.getReferenciaEntrada().add(referenciaEntrada);
//			bantelFacade.avisoEntradas(referenciasEntrada);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return "ERROR: " + ex.getCause();
//		}
//		return "OK";
//	}
//
//	private String getRandomString() {
//
//		int leftLimit = 97; // letter 'a'
//		int rightLimit = 122; // letter 'z'
//		int targetStringLength = 10;
//		Random random = new Random();
//		StringBuilder buffer = new StringBuilder(targetStringLength);
//		for (int i = 0; i < targetStringLength; i++) {
//			int randomLimitedInt = leftLimit + (int)
//					(random.nextFloat() * (rightLimit - leftLimit + 1));
//			buffer.append((char) randomLimitedInt);
//		}
//		return buffer.toString();
//	}
//	private int getRandomNumber() {
//		return getRandomNumber(1, 999999);
//	}
//	private int getRandomNumber(int min, int max) {
//		return (int) ((Math.random() * (max - min)) + min);
//	}
}
