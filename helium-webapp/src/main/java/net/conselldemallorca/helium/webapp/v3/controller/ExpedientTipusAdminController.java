package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAdminCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusMetadadesNtiCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per al menú Administrador de cercador de tipologies de tipus d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "expedientTipusAdminControllerV3")
@RequestMapping("/v3/cercadorTipologies")
public class ExpedientTipusAdminController extends BaseController {

	@Resource
	private ExpedientTipusService expedientTipusService;
	@Resource
	private PluginHelper pluginHelper;

	/** Resposta GET i POST de la pàgina i formulari de tipologies.
	 * 
	 * @param request
	 * @param model
	 * @return Retorna com si fos una crida post de consulta.
	 */
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
	public String tipologies(
			HttpServletRequest request,
			ExpedientTipusAdminCommand filtreCommand,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) {
		
		if (accio != null && "netejar".equals(accio))
			filtreCommand = new ExpedientTipusAdminCommand();
		
		// Torna a omplir el model
		model.addAttribute("expedientTipusAdminCommand", filtreCommand);		
		// Valida l'accés a l'entorn
		Boolean potAdministrarEntorn = SessionHelper.getSessionManager(request).getPotAdministrarEntorn();
		if (potAdministrarEntorn != null && !potAdministrarEntorn) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"error.permis.administracio.entorn"));
			return "v3/cercadorTipologies";

		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		//Filtrem resultat
		List<ExpedientTipusDto> expedientsTipusDto = new ArrayList<ExpedientTipusDto>();
		expedientsTipusDto = expedientTipusService.findTipologiesByFiltre(filtreCommand.getCodiSIA(), entornActual.getId());
		Map<String, Object> llistatExpedients = this.mostrarLlistat(expedientsTipusDto, filtreCommand);
		model.addAttribute("llistatExpedients", llistatExpedients);
		model.addAllAttributes(llistatExpedients);
		model.addAttribute("expedientsTipusDto", expedientsTipusDto);
		
		return "v3/cercadorTipologies";
	}
	


	
	/** Posa les dades en diferents entrades en un Map<String, Object>
	 * 
	 * @param etdto dades dels expedients per setejar al llistat final.
	 * @param filtreCommand
	 * @return mapa de dades per retornar
	 */
	private Map<String, Object> mostrarLlistat(List<ExpedientTipusDto> etdto, ExpedientTipusAdminCommand filtreCommand) {

		Map<String, Object> dades = new HashMap<String, Object>();	
		Map<String, String> titols = new HashMap<String, String>();  
		Map<String, String> codisSia = new HashMap<String, String>();
		Map<String, String> codisTipologia = new HashMap<String, String>();
		ExpedientTipusMetadadesNtiCommand ntiCommand  = new ExpedientTipusMetadadesNtiCommand();
		for(ExpedientTipusDto expedientTipusDto : etdto) {
				String codiTipusExp = expedientTipusDto.getCodi();
				if(!titols.containsKey(codiTipusExp)) {
					titols.put(codiTipusExp, expedientTipusDto.getNom());
					ntiCommand = ExpedientTipusMetadadesNtiCommand.toCommand(
						expedientTipusDto);
					codisSia.put(codiTipusExp, ntiCommand.getClasificacion());
					//DANI: no estic segura quin és el codi de la tipologia
					codisTipologia.put(codiTipusExp, expedientTipusDto.getCodi());
				}
		}
		
		dades.put("titols", titols);
		dades.put("codisSia", codisSia);
		dades.put("codisTipologia", codisTipologia);
		return dades;
	}


}
