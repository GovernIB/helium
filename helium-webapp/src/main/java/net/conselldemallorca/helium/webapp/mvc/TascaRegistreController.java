/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExecucioMassivaService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.webapp.mvc.util.TascaFormUtil;
import net.conselldemallorca.helium.webapp.mvc.util.TramitacioMassiva;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió dels formularis dels camps de tipus registre
 * a dins les tasques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class TascaRegistreController extends CommonRegistreController {

	private TascaService tascaService;
	private ExecucioMassivaService execucioMassivaService;


	@Autowired
	public TascaRegistreController(
			TascaService tascaService,
			DissenyService dissenyService,
			ExecucioMassivaService execucioMassivaService) {
		super(dissenyService);
		this.tascaService = tascaService;
		this.execucioMassivaService = execucioMassivaService;
	}

	@Override
	public void populateOthers(
			HttpServletRequest request,
			String id,
			Object command,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			TascaDto tasca = tascaService.getById(
					entorn.getId(),
					id,
					null,
					null,
					false,
					false);
			List<Camp> camps = new ArrayList<Camp>();
    		for (CampTasca campTasca: tasca.getCamps())
    			camps.add(campTasca.getCamp());
			model.addAttribute(
					"tasca",
					tascaService.getById(
							entorn.getId(),
							id,
							null,
							TascaFormUtil.getValorsFromCommand(
		        					camps,
		        					command,
		        					true,
		    						false),
		    				true,
		    				false));
		}
	}

	@RequestMapping(value = "/tasca/registre", method = RequestMethod.GET)
	public String registreGet(HttpServletRequest request) {
		return super.registreGet(request);
	}
	@RequestMapping(value = "/tasca/registre", method = RequestMethod.POST)
	public String registrePost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "registreId", required = true) Long registreId,
			@RequestParam(value = "index", required = false) Integer index,
			@RequestParam(value = "submit", required = true) String submit,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		return super.registrePost(request, id, registreId, index, submit, command, result, status, model);
	}

	@Override
	public void esborrarRegistre(
			HttpServletRequest request,
			String id,
			String campCodi,
			boolean multiple,
			int index) {
		Entorn entorn = getEntornActiu(request);
		tascaService.esborrarRegistre(entorn.getId(), id, campCodi, index);
		
		if (TramitacioMassiva.isTramitacioMassivaActiu(request, id)) {
			String[] tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			if (tascaIds.length > 1) {
				String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
				try {
					TascaDto task = tascaService.getByIdSenseComprovacio(id);
					Long expTipusId = task.getExpedient().getTipus().getId();
					
					// La primera tasca ja s'ha executat. Programam massivament la resta de tasques
					// ----------------------------------------------------------------------------
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					// Obtenim informació de l'execució massiva
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try { dInici = sdf.parse(parametresTram[0]); } catch (ParseException pe) {};
					}
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true")) bCorreu = true;
					
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("RegEsborrar");
					Object[] params = new Object[3];
					params[0] = entorn.getId();
					params[1] = campCodi;
					params[2] = Integer.valueOf(index);
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
					missatgeInfo(request, getMessage("info.tasca.massiu.registre.esborrar", new Object[] {tIds.length}));
				} catch (Exception e) {
					missatgeError(request, getMessage("error.no.massiu"));
				}
			}
		}
	}
	@Override
	public Object[] getValorRegistre(
			HttpServletRequest request,
			Long entornId,
			String id,
			String campCodi) {
		return (Object[])tascaService.getVariable(entornId, id, campCodi);
	}
	@Override
	public void guardarRegistre(
			HttpServletRequest request,
			String id,
			String campCodi,
			boolean multiple,
			Object[] valors,
			int index) {
		Entorn entorn = getEntornActiu(request);
		tascaService.guardarRegistre(entorn.getId(), id, campCodi, valors, index);
		
		if (TramitacioMassiva.isTramitacioMassivaActiu(request, id)) {
			String[] tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			if (tascaIds.length > 1) {
				String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
				try {
					TascaDto task = tascaService.getByIdSenseComprovacio(id);
					Long expTipusId = task.getExpedient().getTipus().getId();
					
					// La primera tasca ja s'ha executat. Programam massivament la resta de tasques
					// ----------------------------------------------------------------------------
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					// Obtenim informació de l'execució massiva
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try { dInici = sdf.parse(parametresTram[0]); } catch (ParseException pe) {};
					}
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true")) bCorreu = true;
					
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("RegGuardar");
					Object[] params = new Object[4];
					params[0] = entorn.getId();
					params[1] = campCodi;
					params[2] = valors;
					params[3] = Integer.valueOf(index);
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
					missatgeInfo(request, getMessage("info.tasca.massiu.registre.guardar", new Object[] {tIds.length}));
				} catch (Exception e) {
					missatgeError(request, getMessage("error.no.massiu"));
				}
			}
		}
	}
	@Override
	public void guardarRegistre(
			HttpServletRequest request,
			String id,
			String campCodi,
			boolean multiple,
			Object[] valors) {
//		Entorn entorn = getEntornActiu(request);
//		tascaService.guardarRegistre(entorn.getId(), id, campCodi, valors);
		guardarRegistre(request, id, campCodi, multiple, valors, -1);
	}
	@Override
	public String redirectUrl(String id, String campCodi) {
		return "redirect:/tasca/form.html?id=" + id;
	}
	@Override
	public String registreUrl() {
		return "tasca/registre";
	}

}
