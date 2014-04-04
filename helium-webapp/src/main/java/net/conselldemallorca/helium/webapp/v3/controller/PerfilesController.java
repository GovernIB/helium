/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto.Sexe;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.webapp.v3.command.PersonaUsuariCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per la gestió d'perfils
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/perfil")
public class PerfilesController extends BaseController {

	@Autowired
	private PluginService pluginService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private ExpedientService expedientService;
	
	@Autowired
	private DissenyService dissenyService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(getFiltreCommand(request, model));
		return "v3/persona/perfil";
	}

	@RequestMapping(value = "/consulta/{entornCodi}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExpedientTipusDto> getExpedientTipus(
			HttpServletRequest request,
			@PathVariable String entornCodi) {
		for (EntornDto entorn: adminService.findEntornAmbPermisReadUsuariActual()) {
			if (entorn.getCodi().equals(entornCodi)) {
				List<ExpedientTipusDto> expedientTipusConConsultas = dissenyService.findExpedientTipusAmbPermisDissenyUsuariActual(entorn.getId());
				Iterator<ExpedientTipusDto> it = expedientTipusConConsultas.iterator();
				while (it.hasNext()) {
					ExpedientTipusDto expTip = it.next();
					if (!expTip.isConConsultasActivasPorTipo()) {
						it.remove();
					}
				}
				return expedientTipusConConsultas;
			}
		}
		return null;
	}

	@RequestMapping(value = "/consulta/{expedientTipusId}/{entornCodi}", method = RequestMethod.GET)
	@ResponseBody
	public List<ConsultaDto> getConsultas(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable String entornCodi) {
		for (EntornDto entorn: adminService.findEntornAmbPermisReadUsuariActual()) {
			if (entorn.getCodi().equals(entornCodi)) {
				return dissenyService.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entorn.getId(),expedientTipusId);
			}
		}
		return null;
	}

	@ModelAttribute("numElementsPagina")
	public List<ParellaCodiValorDto> numElementsPagina(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto("10", "10"));
		resposta.add(new ParellaCodiValorDto("50", "50"));
		resposta.add(new ParellaCodiValorDto("100", "100"));
		return resposta;
	}

	@ModelAttribute("cabeceras")
	public List<ParellaCodiValorDto> cabeceras(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto("0", "Completa"));
		resposta.add(new ParellaCodiValorDto("1", "Reducida"));
		return resposta;
	}

	@ModelAttribute("pantallas")
	public List<ParellaCodiValorDto> paginas(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto("0", "Expedients"));
		resposta.add(new ParellaCodiValorDto("1", "Tasques"));
		resposta.add(new ParellaCodiValorDto("2", "Informes"));
		return resposta;
	}

	@ModelAttribute("sexes")
	public List<ParellaCodiValorDto> sexes(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto("0", getMessage(request, "txt.dona")));
		resposta.add(new ParellaCodiValorDto("1", getMessage(request, "txt.home")));
		return resposta;
	}
	
	private PersonaUsuariCommand getFiltreCommand(HttpServletRequest request, Model model) {		
		PersonaUsuariCommand filtreCommand = new PersonaUsuariCommand();
		UsuariPreferenciesDto preferencies = SessionHelper.getSessionManager(request).getPreferenciesUsuari();
		
		EntornDto entornUsuari = null;
		List<EntornDto> entorns = adminService.findEntornAmbPermisReadUsuariActual();
		if (preferencies.getDefaultEntornCodi() == null) {
			entornUsuari = SessionHelper.getSessionManager(request).getEntornActual();
		} else {
			for (EntornDto entorn: entorns) {
				if (entorn.getCodi().equals(preferencies.getDefaultEntornCodi())) {
					entornUsuari = entorn;
					break;
				}
			}
		}
		
		filtreCommand.setCabeceraReducida(preferencies.isCabeceraReducida());
		filtreCommand.setEntornCodi(preferencies.getDefaultEntornCodi());
		if (preferencies.getConsultaId() != null) {
			ConsultaDto consulta = dissenyService.findConsulteById(preferencies.getConsultaId());
			filtreCommand.setConsultaId(preferencies.getConsultaId());		
			filtreCommand.setExpedientTipusId(consulta.getExpedientTipus().getId());
			model.addAttribute("consultes", dissenyService.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornUsuari.getId(),filtreCommand.getExpedientTipusId()));
		} else {
			model.addAttribute("consultes", new ArrayList<ConsultaDto>());
		}
		
		List<ExpedientTipusDto> expedientTipusConConsultas = dissenyService.findExpedientTipusAmbPermisDissenyUsuariActual(entornUsuari.getId());
		Iterator<ExpedientTipusDto> it = expedientTipusConConsultas.iterator();
		while (it.hasNext()) {
			ExpedientTipusDto expTip = it.next();
			if (!expTip.isConConsultasActivasPorTipo()) {
				it.remove();
			}
		}
		model.addAttribute("expedientTipus", expedientTipusConConsultas);
		filtreCommand.setFiltroExpedientesActivos(preferencies.isFiltroTareasActivas());
		filtreCommand.setListado(preferencies.getListado());
		filtreCommand.setNumElementosPagina(preferencies.getNumElementosPagina());
		
		PersonaDto usuari = getPersonaActual(request);
		filtreCommand.setNom(usuari.getNom());
		filtreCommand.setDni(usuari.getDni());
		filtreCommand.setEmail(usuari.getEmail());
		filtreCommand.setLlinatge1(usuari.getLlinatge1());
		filtreCommand.setLlinatge2(usuari.getLlinatge2());
		filtreCommand.setHombre(usuari.getSexe().equals(Sexe.SEXE_HOME));
		return filtreCommand;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@ModelAttribute("PersonaUsuariCommand") PersonaUsuariCommand personaUsuariCommand,
			@RequestParam(value = "accio", required = false) String accio,
			BindingResult result,
			SessionStatus status,
			Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
	        try {
	        	if ("Guardar".equals(accio)) {
		        	UsuariPreferenciesDto preferencies = new UsuariPreferenciesDto();
		        	preferencies.setCodi(request.getUserPrincipal().getName());
		        	preferencies.setCabeceraReducida(personaUsuariCommand.isCabeceraReducida());
		        	preferencies.setConsultaId(personaUsuariCommand.getConsultaId());
		        	preferencies.setDefaultEntornCodi(personaUsuariCommand.getEntornCodi());
		        	preferencies.setFiltroTareasActivas(personaUsuariCommand.isFiltroExpedientesActivos());
		        	preferencies.setListado(personaUsuariCommand.getListado());
		        	preferencies.setNumElementosPagina(personaUsuariCommand.getNumElementosPagina());
		        	adminService.updatePerfil(preferencies);
		        	SessionHelper.getSessionManager(request).setPreferenciesUsuari(preferencies);
		        	
		        	MissatgesHelper.info(request,getMessage(request, "info.perfil.guardat"));
	        	} else if ("Modificar".equals(accio)) {
	        		PersonaDto persona = new PersonaDto();
	        		persona.setCodi(request.getUserPrincipal().getName());
	        		persona.setDni(personaUsuariCommand.getDni());
	        		persona.setEmail(personaUsuariCommand.getEmail());
	        		persona.setLlinatge1(personaUsuariCommand.getLlinatge1());
	        		persona.setLlinatge2(personaUsuariCommand.getLlinatge2());
	        		persona.setNom(personaUsuariCommand.getNom());
	        		persona.setSexe(personaUsuariCommand.isHombre() ? Sexe.SEXE_HOME : Sexe.SEXE_DONA);
	        		adminService.updatePersona(persona);
	        		MissatgesHelper.info(request,getMessage(request, "info.perfil.guardat"));
	        	} else {
	        		MissatgesHelper.error(request, getMessage(request, "error.guardar.perfil"));
	        	}	        	
	        } catch (Exception ex) {
	        	MissatgesHelper.error(request, getMessage(request, "error.guardar.perfil"));
	        	logger.error("No s'ha pogut guardar el perfil", ex);
	        }
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		return get(request, model);
	}

	private PersonaDto getPersonaActual(
			HttpServletRequest request) {
		String usuariCodi = request.getUserPrincipal().getName();
		return pluginService.findPersonaAmbCodi(usuariCodi);
	}

	private static final Logger logger = LoggerFactory.getLogger(PerfilesController.class);
}
