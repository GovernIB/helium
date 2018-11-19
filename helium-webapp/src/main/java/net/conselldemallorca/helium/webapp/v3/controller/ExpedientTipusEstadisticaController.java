package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.service.EntornService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.EntornCommand;
import net.conselldemallorca.helium.webapp.v3.command.EntornCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.EntornCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.command.PermisCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

/**
 * Controlador per al manteniment d'entorns.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "entornEstadisticaControllerV3")
@RequestMapping("/v3/estadistica")
public class ExpedientTipusEstadisticaController extends BaseController {

	@Resource
	private ExpedientTipusService expedientTipusService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;


	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		//entornEstadisticaService.getByData(entornId, inici, fi)
		expedientTipusService.findEstadisticaByFiltre(new Date(), new Date(), new Date(), new Date(), new Long(2));
		return "v3/estadisticaEntorns";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String form(
			HttpServletRequest request,
			Model model) {
//		entornEstadisticaService.getAllByFiltre(new Date());
		expedientTipusService.findEstadisticaByFiltre(new Date(), new Date(), new Date(), new Date(), new Long(2));
		return "v3/estadisticaEntorns";
	}
}
