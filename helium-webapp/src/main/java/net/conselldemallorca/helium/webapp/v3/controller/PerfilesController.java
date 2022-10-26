package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto.Sexe;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.EntornService;
import net.conselldemallorca.helium.webapp.v3.command.PersonaUsuariCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per la gestió d'perfils
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/perfil")
public class PerfilesController extends BaseController {

	@Resource(name="entornServiceV3")
	private EntornService entornService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private DissenyService dissenyService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(getFiltreCommand(request, model));
		return "v3/persona/perfil";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session= request.getSession(false);
		SecurityContextHolder.clearContext();
        session= request.getSession(false);
        if(session != null) {
           session.invalidate();
        }// Només per Jboss
        for(Cookie cookie : request.getCookies()) {
        	Cookie ck = new Cookie(cookie.getName(), null);
			ck.setPath(request.getContextPath());
			ck.setMaxAge(0);
			response.addCookie(ck);
        }
        return "redirect:/";
	}
	

	@RequestMapping(value = "/consulta/{entornCodi}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExpedientTipusDto> getExpedientTipus(
			HttpServletRequest request,
			@PathVariable String entornCodi) {
		for (EntornDto entorn: entornService.findActiusAmbPermisAcces()) {
			if (entorn.getCodi().equals(entornCodi)) {
				List<ExpedientTipusDto> expedientsTipus = dissenyService.findExpedientTipusAmbPermisReadUsuariActual(entorn.getId());
				return expedientsTipus;

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
		for (EntornDto entorn: entornService.findActiusAmbPermisAcces()) {
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
		resposta.add(new ParellaCodiValorDto("0", getMessage(request, "perfil.usuari.completa")));
		resposta.add(new ParellaCodiValorDto("1", getMessage(request, "perfil.usuari.reducida")));
		return resposta;
	}

	@ModelAttribute("pantallas")
	public List<ParellaCodiValorDto> paginas(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto("0", getMessage(request, "perfil.usuari.expedients")));
		resposta.add(new ParellaCodiValorDto("1", getMessage(request, "perfil.usuari.tasques")));
		resposta.add(new ParellaCodiValorDto("2", getMessage(request, "perfil.usuari.informes")));
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
		UsuariPreferenciesDto preferencies = aplicacioService.getUsuariPreferencies();
		if (preferencies == null)
			preferencies = new UsuariPreferenciesDto();
		EntornDto entornUsuari = null;
		List<EntornDto> entorns = entornService.findActiusAmbPermisAcces();
		for (EntornDto entorn: entorns) {
			if (entorn.getCodi().equals(preferencies.getDefaultEntornCodi())) {
				entornUsuari = entorn;
				break;
			}
		}
		// Comprova que l'usuari pugi accedir a l'entorn que té configurat
		if (preferencies.getDefaultEntornCodi() != null && entornUsuari == null) {
			MissatgesHelper.error(request, getMessage(request, "info.perfil.entorn.error", new Object[] {preferencies.getDefaultEntornCodi()}));			
		}
		filtreCommand.setEntornCodi(preferencies.getDefaultEntornCodi());
		
		List<ExpedientTipusDto> expedientTipusConConsultas = new ArrayList<ExpedientTipusDto>();
		ExpedientTipusDto expedientTipusUsuari = null;
		if (entornUsuari != null) {
			List<ExpedientTipusDto> expedientTipus = dissenyService.findExpedientTipusAmbPermisReadUsuariActual(entornUsuari.getId());
			model.addAttribute("expedientTipus", expedientTipus);
			for (ExpedientTipusDto expTip : expedientTipus) {
				if (!expTip.getConsultes().isEmpty()) {
					expedientTipusConConsultas.add(expTip);
				}
				if (preferencies.getExpedientTipusDefecteId() != null 
						&& expTip.getId().equals(preferencies.getExpedientTipusDefecteId())) {
					expedientTipusUsuari = expTip;
				}
			}
		}
		model.addAttribute("expedientTipusConConsultas", expedientTipusConConsultas);
		List<ConsultaDto> consultes = null;
		if (preferencies.getExpedientTipusDefecteId() != null) {
			// Comprova que l'usuari pugui accedir al tipus d'expedient configurat
			if (expedientTipusUsuari == null) {
				MissatgesHelper.error(request, getMessage(request, "info.perfil.tipus.error", new Object[] {preferencies.getExpedientTipusDefecteId()}));
			} else {
				filtreCommand.setExpedientTipusId(preferencies.getExpedientTipusDefecteId());
				consultes = dissenyService.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornUsuari.getId(),filtreCommand.getExpedientTipusId());
				if (preferencies.getConsultaId() != null) {
					ConsultaDto consultaUsuari = null;
					for (ConsultaDto c : consultes) {
						if (c.getId().equals(preferencies.getConsultaId())) {
							consultaUsuari = c;
							break;
						}
					}
					if (consultaUsuari == null) {
						MissatgesHelper.error(request, getMessage(request, "info.perfil.consulta.error", new Object[] {preferencies.getConsultaId()}));						
						filtreCommand.setConsultaId(null);
					} else {
						filtreCommand.setConsultaId(preferencies.getConsultaId());
					}
				}
			}
		}
		model.addAttribute("consultes", consultes != null ? consultes : new ArrayList<ConsultaDto>());
		filtreCommand.setCabeceraReducida(preferencies.isCabeceraReducida());
		filtreCommand.setFiltroExpedientesActivos(preferencies.isFiltroTareasActivas());
		filtreCommand.setListado(preferencies.getListado());
		filtreCommand.setNumElementosPagina(preferencies.getNumElementosPagina());
		filtreCommand.setExpedientTipusDefecteId(preferencies.getExpedientTipusDefecteId());
		
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
			@RequestParam(value = "accio", required = false) String accio,
			@Valid @ModelAttribute PersonaUsuariCommand personaUsuariCommand,
			BindingResult result,
			Model model) {
        try {
    		if ("Guardar".equals(accio)) {
	        	UsuariPreferenciesDto preferencies = new UsuariPreferenciesDto();
	        	preferencies.setCodi(request.getUserPrincipal().getName());
	        	preferencies.setCabeceraReducida(personaUsuariCommand.isCabeceraReducida());
	        	preferencies.setConsultaId(personaUsuariCommand.getConsultaId());
	        	preferencies.setExpedientTipusDefecteId(personaUsuariCommand.getExpedientTipusDefecteId());
	        	preferencies.setDefaultEntornCodi(personaUsuariCommand.getEntornCodi());
	        	preferencies.setFiltroTareasActivas(personaUsuariCommand.isFiltroExpedientesActivos());
	        	preferencies.setListado(personaUsuariCommand.getListado());
	        	preferencies.setNumElementosPagina(personaUsuariCommand.getNumElementosPagina());
	        	adminService.updatePerfil(preferencies);
	        	SessionHelper.getSessionManager(request).setPreferenciesUsuari(preferencies);		        	
        	} else if ("Modificar".equals(accio) && !result.hasErrors()) {
        		PersonaDto persona = new PersonaDto();
        		persona.setCodi(request.getUserPrincipal().getName());
        		persona.setDni(personaUsuariCommand.getDni());
        		persona.setEmail(personaUsuariCommand.getEmail());
        		persona.setLlinatge1(personaUsuariCommand.getLlinatge1());
        		persona.setLlinatge2(personaUsuariCommand.getLlinatge2());
        		persona.setNom(personaUsuariCommand.getNom());
        		persona.setSexe(personaUsuariCommand.isHombre() ? Sexe.SEXE_HOME : Sexe.SEXE_DONA);
        		adminService.updatePersona(persona);
        	} else {
                MissatgesHelper.error(request, getMessage(request, "error.guardar.perfil"));
                
                PersonaUsuariCommand pars = getFiltreCommand(request, model);
                personaUsuariCommand.setCabeceraReducida(pars.isCabeceraReducida());
                personaUsuariCommand.setConsultaId(pars.getConsultaId());
                personaUsuariCommand.setEntornCodi(pars.getEntornCodi());
                personaUsuariCommand.setExpedientTipusId(pars.getExpedientTipusId());
                personaUsuariCommand.setExpedientTipusDefecteId(pars.getExpedientTipusDefecteId());
                personaUsuariCommand.setFiltroExpedientesActivos(pars.isFiltroExpedientesActivos());
                personaUsuariCommand.setListado(pars.getListado());
                personaUsuariCommand.setNumElementosPagina(pars.getNumElementosPagina());
                	                
        		return "v3/persona/perfil";
        	}
    		SessionHelper.getSessionManager(request).setFiltreConsultaGeneral(null);
			MissatgesHelper.success(request,getMessage(request, "info.perfil.guardat"));
        } catch (Exception ex) {
        	MissatgesHelper.error(request, getMessage(request, "error.guardar.perfil"));
        	logger.error("No s'ha pogut guardar el perfil", ex);
        }
		return get(request, model);
	}

	private PersonaDto getPersonaActual(
			HttpServletRequest request) {
		String usuariCodi = request.getUserPrincipal().getName();
		return aplicacioService.findPersonaAmbCodi(usuariCodi);
	}

	private static final Logger logger = LoggerFactory.getLogger(PerfilesController.class);
}
