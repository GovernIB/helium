/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.MonitorTascaInfo;
import net.conselldemallorca.helium.v3.core.api.service.MonitorTasquesService;
import net.conselldemallorca.helium.v3.core.api.service.TascaProgramadaService;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.PaginacioHelper;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/tasca-segon-pla")
public class ScheduledController extends BaseController {

	@Autowired
	private MonitorTasquesService monitorTasquesService;
	@Autowired
	private TascaProgramadaService tascaProgramadaService;

	    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String schedulingGet(HttpServletRequest request, Model model) {
    	return "v3/segonPla";
    }  
    
    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) {
		List<MonitorTascaInfo> tasques = monitorTasquesService.findAll();
		try {
			Collections.sort(tasques, new Comparator<MonitorTascaInfo>() {
				@Override
				public int compare(MonitorTascaInfo o1, MonitorTascaInfo o2) {
					return o1.getCodi().compareTo(o2.getCodi());
				}
			});
		} catch (Exception ex) {}
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				tasques);
	}    
    
    @RequestMapping(value = "/restart/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> schedulingRestart(
			HttpServletRequest request,
			@PathVariable String id) {
    	Map<String, Object> response = new HashMap<String, Object>();
    	
    	try {
    		//Posa la tasca en espera
    		monitorTasquesService.reiniciarTasquesEnSegonPla(id);
    		
    		//Reprograma la tasca
//    		tascaProgramadaService.restartSchedulledTasks(id);
    		
    		if (!"totes".equals(id)) {
    			String nomTasca = getMessage(request, "administracio.tasquesSegonPla.codi."+id);
    			MissatgesHelper.success(request, getMessage(request, "administracio.tasquesSegonPla.reiniciat.ok", new Object[]{nomTasca}));
    		} else {
    			MissatgesHelper.success(request, getMessage(request, "administracio.tasquesSegonPla.reiniciat.totes"));
    		}
    	} catch (Exception e) {
    		MissatgesHelper.error(request, getMessage(request, "administracio.tasquesSegonPla.reiniciat.ko"), e);
		}
    	return response;
    }
}
