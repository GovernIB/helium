/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.TascaFormUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió dels formularis dels camps de tipus registre
 * a dins les tasques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class CommonRegistreController extends BaseController {

	protected DissenyService dissenyService;
	private Validator validator;


	@Autowired
	public CommonRegistreController(
			DissenyService dissenyService) {
		this.dissenyService = dissenyService;
		this.validator = new CommonRegistreValidator(dissenyService);
	}

	@ModelAttribute("registre")
	public Camp populateRegistre(
			HttpServletRequest request,
			@RequestParam(value = "registreId", required = true) Long registreId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Camp registre = dissenyService.getCampById(registreId);
			return registre;
		}
		return null;
	}

	@ModelAttribute("command")
	public Object populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "registreId", required = true) Long registreId,
			@RequestParam(value = "index", required = false) Integer index,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Camp camp = dissenyService.getCampById(registreId);
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			campsAddicionals.put("id", id);
			campsAddicionals.put("registreId", registreId);
			campsAddicionals.put("entornId", entorn.getId());
			campsAddicionals.put("index", index);
			campsAddicionals.put("procesScope", null);
			@SuppressWarnings("rawtypes")
			Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
			campsAddicionalsClasses.put("id", String.class);
			campsAddicionalsClasses.put("registreId", Long.class);
			campsAddicionalsClasses.put("entornId", Long.class);
			campsAddicionalsClasses.put("index", Integer.class);
			campsAddicionalsClasses.put("procesScope", Map.class);
			Map<String, Object> valors = null;
			if (index != null) {
				Object[] valorRegistre = ExpedientMassivaRegistreController.getRegistreMassiuSessio(request, id, camp.getCodi());
				if (valorRegistre == null) {
					valorRegistre = getValorRegistre(
							request,
							entorn.getId(),
							id,
							camp.getCodi());
				}
				if (camp.isMultiple()) {
					if (index < valorRegistre.length) {
						valors = new HashMap<String, Object>();
						for (int i = 0; i < camp.getRegistreMembres().size(); i++) {
							CampRegistre campRegistre = camp.getRegistreMembres().get(i);
							valors.put(
									campRegistre.getMembre().getCodi(),
									Array.get(valorRegistre[index], i));
						}
					}
				} else {
					valors = new HashMap<String, Object>();
					
					Object[] valorRegistreTmp = new Object[camp.getRegistreMembres().size()];
					for (int i = 0; i < valorRegistre.length; i++) {
						valorRegistreTmp[i] =  valorRegistre[i];
					}
					valorRegistre = valorRegistreTmp;
					
					for (int i = 0; i < camp.getRegistreMembres().size(); i++) {
						CampRegistre campRegistre = camp.getRegistreMembres().get(i);
						valors.put(
								campRegistre.getMembre().getCodi(),
								valorRegistre[i]);
					}
				}
			}
			Object command = TascaFormUtil.getCommandForRegistre(
					camp,
					valors,
					campsAddicionals,
					campsAddicionalsClasses);
			populateOthers(request, id, command, model);
			return command;
		}
		return null;
	}

	@ModelAttribute("valorsPerSuggest")
	public Map<String, List<Object>> populateValorsPerSuggest(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			TascaDto tasca = (TascaDto)model.get("tasca");
			if (tasca != null) {
				Object command = model.get("command");
				return TascaFormUtil.getValorsPerSuggest(tasca, command);
			}
		}
		return null;
	}

	protected String registreGet(HttpServletRequest request) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			return registreUrl();
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	protected String registrePost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "registreId", required = true) Long registreId,
			@RequestParam(value = "index", required = false) Integer index,
			@RequestParam(value = "submit", required = true) String submit,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Camp camp = (Camp)model.get("registre");
			List<Camp> camps = new ArrayList<Camp>();
    		for (CampRegistre campRegistre: camp.getRegistreMembres())
    			camps.add(campRegistre.getMembre());
    		model.addAttribute("tancarRegistre", new Boolean(false));
			if ("submit".equals(submit) || submit.length() == 0) {
				validator.validate(command, result);
		        if (!result.hasErrors()) {
			        try {
			        	Map<String, Object> valorsCommand = TascaFormUtil.getValorsFromCommand(
	        					camps,
	        					command,
	        					true,
	    						false);
			        	List<CampRegistre> membres = camp.getRegistreMembres();
			        	Object[] valors = new Object[membres.size()];
			        	for (int i = 0; i < membres.size(); i++) {
			        		CampRegistre campRegistre = membres.get(i);
			        		valors[i] = valorsCommand.get(campRegistre.getMembre().getCodi());
			        	}
			        	if (index != null) {
				        	guardarRegistre(
									request,
				        			id,
				        			camp.getCodi(),
				        			camp.isMultiple(),
				        			valors,
				        			index.intValue());
			        	} else {
			        		guardarRegistre(
									request,
				        			id,
				        			camp.getCodi(),
				        			camp.isMultiple(),
				        			valors);
			        	}
			        	status.setComplete();
			        	model.addAttribute("tancarRegistre", new Boolean(true));
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.guardar.dades"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar les dades", ex);
			        }
		        }
			} else {
				model.addAttribute("tancarRegistre", new Boolean(true));
			}
			return registreUrl();
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	protected String esborrarMembre(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "registreId", required = true) Long registreId,
			@RequestParam(value = "index", required = true) int index) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Camp camp = dissenyService.getCampById(registreId);
			try {
				esborrarRegistre(
						request,
						id,
						camp.getCodi(),
						camp.isMultiple(),
						index);
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.esborrar.registre"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar el registre", ex);
	        }
			return redirectUrl(id, camp.getCodi());
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	protected Object[] getValorRegistre(
			HttpServletRequest request,
			String campCodi,
			String id) {
		Entorn entorn = getEntornActiu(request);
		return getValorRegistre(request, entorn.getId(), id, campCodi);//camp.getCodi());
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Long.class,
				new CustomNumberEditor(Long.class, true));
		binder.registerCustomEditor(
				Double.class,
				new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(
				BigDecimal.class,
				new CustomNumberEditor(
						BigDecimal.class,
						new DecimalFormat("#,##0.00"),
						true));
		binder.registerCustomEditor(
				Boolean.class,
				new CustomBooleanEditor(false));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Termini.class,
				new TerminiTypeEditor());
	}

	public void populateOthers(
			HttpServletRequest request,
			String id,
			Object command,
			ModelMap model) {
	}

	public abstract Object[] getValorRegistre(
			HttpServletRequest request,
			Long entornId,
			String id,
			String campCodi);
	public abstract void guardarRegistre(
			HttpServletRequest request,
			String id,
			String campCodi,
			boolean multiple,
			Object[] valors,
			int index);
	public abstract void guardarRegistre(
			HttpServletRequest request,
			String id,
			String campCodi,
			boolean multiple,
			Object[] valors);
	public abstract void esborrarRegistre(
			HttpServletRequest request,
			String id,
			String campCodi,
			boolean multiple,
			int index);
	public abstract String redirectUrl(
			String id,
			String campCodi);
	public abstract String registreUrl();



	private static final Log logger = LogFactory.getLog(CommonRegistreController.class);

}
