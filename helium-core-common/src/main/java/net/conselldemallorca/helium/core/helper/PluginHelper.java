/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.Transicio;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.custodia.CustodiaPlugin;
import net.conselldemallorca.helium.integracio.plugins.custodia.CustodiaPluginException;
import net.conselldemallorca.helium.integracio.plugins.gesdoc.GestioDocumentalPlugin;
import net.conselldemallorca.helium.integracio.plugins.persones.DadesPersona;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPlugin;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPluginException;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.DocumentPortasignatures;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PasSignatura;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PortasignaturesPlugin;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PortasignaturesPluginException;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesAssumpte;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesExpedient;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesInteressat;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesOficina;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesRepresentat;
import net.conselldemallorca.helium.integracio.plugins.registre.DocumentRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreAssentament;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreAssentamentInteressat;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreEntrada;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistrePlugin;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistrePluginException;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistrePluginRegWeb3;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreSortida;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaConsultaRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantDetallRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.TramitSubsanacio;
import net.conselldemallorca.helium.integracio.plugins.registre.TramitSubsanacioParametre;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;
import net.conselldemallorca.helium.integracio.plugins.signatura.SignaturaPlugin;
import net.conselldemallorca.helium.integracio.plugins.signatura.SignaturaPluginException;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.Event;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.Signatura;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPlugin;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginException;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreAnnexDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreAnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreIdDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreNotificacioDto.RegistreNotificacioTramitSubsanacioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDocumentDto.TramitDocumentSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperEventDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperExpedientDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.registre.RegistreAnnex;
import net.conselldemallorca.helium.v3.core.api.registre.RegistreAnotacio;
import net.conselldemallorca.helium.v3.core.api.registre.RegistreInteressat;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.PortasignaturesRepository;

/**
 * Helper per a accedir a la funcionalitat dels plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component("pluginHelperV3")
public class PluginHelper {

	private static final String CACHE_PERSONA_ID = "personaPluginCache";

	@Resource
	private PortasignaturesRepository portasignaturesRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private MonitorIntegracioHelper monitorIntegracioHelper;

	private PersonesPlugin personesPlugin;
	private boolean personesPluginEvaluat = false;
	private TramitacioPlugin tramitacioPlugin;
	private boolean tramitacioPluginEvaluat = false;
	private RegistrePlugin registrePlugin;
	private boolean registrePluginEvaluat = false;
	private GestioDocumentalPlugin gestioDocumentalPlugin;
	private RegistrePluginRegWeb3 registrePluginRegWeb3;
	private boolean registrePluginRegWeb3Evaluat = false;
	private boolean gestioDocumentalPluginEvaluat = false;
	private PortasignaturesPlugin portasignaturesPlugin;
	private boolean portasignaturesPluginEvaluat = false;
	private CustodiaPlugin custodiaPlugin;
	private boolean custodiaPluginEvaluat = false;
	private SignaturaPlugin signaturaPlugin;
	private boolean signaturaPluginEvaluat = false;



	public List<PersonaDto> personaFindLikeNomSencer(String text) {
		long t0 = System.currentTimeMillis();
		try {
			List<DadesPersona> persones = getPersonesPlugin().findLikeNomSencer(text);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_PERSONA,
					"Consulta d'usuaris amb like",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					new IntegracioParametreDto("text", text));
			if (persones == null)
				return new ArrayList<PersonaDto>();
			return conversioTipusHelper.convertirList(persones, PersonaDto.class);
		} catch (PersonesPluginException ex) {
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_PERSONA,
					"Consulta d'usuaris amb like",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					"El plugin ha retornat una excepció",
					ex,
					new IntegracioParametreDto("text", text));
			logger.error(
					"No s'han pogut consultar persones amb el text (text=" + text + ")",
					ex);
			throw new SistemaExternException(
					null,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					"No s'han pogut consultar persones amb el text (text=" + text + ")",
					ex);
		}
	}
	
	public List<Portasignatures> findPendentsPortasignaturesPerProcessInstanceId(String processInstanceId) {		
		List<Portasignatures> psignas = portasignaturesRepository.findPendentsPerProcessInstanceId(processInstanceId);
		Iterator<Portasignatures> it = psignas.iterator();
		while (it.hasNext()) {
			Portasignatures psigna = it.next();
			if (	!TipusEstat.PENDENT.equals(psigna.getEstat()) &&
					!TipusEstat.SIGNAT.equals(psigna.getEstat()) &&
					!TipusEstat.REBUTJAT.equals(psigna.getEstat()) &&
					!TipusEstat.ERROR.equals(psigna.getEstat()) &&
					!(TipusEstat.PROCESSAT.equals(psigna.getEstat()) && Transicio.REBUTJAT.equals(psigna.getTransition()))) {
				it.remove();
			}
		}
		return psignas;
	}

	public PersonaDto personaFindAmbCodi(String codi) {
		Cache personaCache = cacheManager.getCache(CACHE_PERSONA_ID);
		if (personaCache.get(codi) == null) {
			long t0 = System.currentTimeMillis();
			try {
				DadesPersona dadesPersona = getPersonesPlugin().findAmbCodi(codi);
				monitorIntegracioHelper.addAccioOk(
						MonitorIntegracioHelper.INTCODI_PERSONA,
						"Consulta d'usuari amb codi",
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						new IntegracioParametreDto("codi", codi));
				
				if (dadesPersona == null)
					throw new NoTrobatException(DadesPersona.class, codi);
				
				PersonaDto dto = conversioTipusHelper.convertir(
						dadesPersona,
						PersonaDto.class);
				if (dto != null) {
					personaCache.put(codi, dto);
				}
				return dto;
			} catch (PersonesPluginException ex) {
				monitorIntegracioHelper.addAccioError(
						MonitorIntegracioHelper.INTCODI_PERSONA,
						"Consulta d'usuari amb codi",
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						"El plugin ha retornat una excepció",
						ex,
						new IntegracioParametreDto("codi", codi));
				logger.error(
						"No s'han pogut consultar persones amb el codi (codi=" + codi + ")",
						ex);
				throw new SistemaExternException(
						null,
						null, 
						null, 
						null, 
						null, 
						null, 
						null, 
						null, 
						null, 
						"No s'han pogut consultar persones amb el codi (codi=" + codi + ")",
						ex);
			}
		} else {
			return (PersonaDto)personaCache.get(codi).get();
		}
	}

	public List<String> personaFindRolsAmbCodi(String codi) throws Exception {
		return getPersonesPlugin().findRolsAmbCodi(codi);
	}
	
	public boolean personaIsPluginActiu() {
		return getPersonesPlugin() != null;
	}

	public boolean personaIsSyncActiu() {
		String syncActiu = GlobalProperties.getInstance().getProperty("app.persones.plugin.sync.actiu");
		return "true".equalsIgnoreCase(syncActiu);
	}

	public void tramitacioZonaperExpedientCrear(
			ExpedientDto expedient,
			ZonaperExpedientDto dadesExpedient) {
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"expedientIdentificador",
						dadesExpedient.getExpedientIdentificador()),
				new IntegracioParametreDto(
						"expedientClau",
						dadesExpedient.getExpedientClau()),
				new IntegracioParametreDto(
						"unitatAdministrativa",
						dadesExpedient.getUnitatAdministrativa()),
				new IntegracioParametreDto(
						"descripcio",
						dadesExpedient.getDescripcio())
		};
		long t0 = System.currentTimeMillis();
		try {
			PublicarExpedientRequest request = conversioTipusHelper.convertir(
					dadesExpedient,
					PublicarExpedientRequest.class);
			getTramitacioPlugin().publicarExpedient(request);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_SISTRA,
					"Creació d'expedient",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
		} catch (TramitacioPluginException ex) {
			String errorDescripcio = "No s'ha pogut crear l'expedient (" +
					"expedientIdentificador=" + dadesExpedient.getExpedientIdentificador() + ", " +
					"expedientClau=" + dadesExpedient.getExpedientClau() + ", " +
					"unitatAdministrativa=" + dadesExpedient.getUnitatAdministrativa() + ", " +
					"descripcio=" + dadesExpedient.getDescripcio() + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_SISTRA,
					"Creació d'expedient",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(SISTRA. Crear expedient: " + errorDescripcio + ")", 
					ex);
		}
	}
	public void tramitacioZonaperEventCrear(
			Expedient expedient,
			ZonaperEventDto dadesEvent) {
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"expedientIdentificador",
						expedient.getTramitExpedientIdentificador()),
				new IntegracioParametreDto(
						"expedientClau",
						expedient.getTramitExpedientClau()),
				new IntegracioParametreDto(
						"unitatAdministrativa",
						expedient.getUnitatAdministrativa()),
				new IntegracioParametreDto(
						"interessatNif",
						expedient.getInteressatNif()),
				new IntegracioParametreDto(
						"eventTitol",
						dadesEvent.getTitol()),
				new IntegracioParametreDto(
						"eventText",
						dadesEvent.getText())
		};
		if (expedient.getTramitExpedientIdentificador() == null || expedient.getTramitExpedientClau() == null)
			throw new SistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(SISTRA. Crear event)", 
					"Abans s'ha de crear un expedient per a poder publicar un event");
		PublicarEventRequest request = new PublicarEventRequest();
		request.setExpedientIdentificador(expedient.getTramitExpedientIdentificador());
		request.setExpedientClau(expedient.getTramitExpedientClau());
		request.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
		request.setRepresentatNif(expedient.getInteressatNif());
		request.setRepresentatNom(expedient.getInteressatNom());	
		request.setEvent(
				conversioTipusHelper.convertir(
						dadesEvent,
						Event.class));
		long t0 = System.currentTimeMillis();
		try {
			getTramitacioPlugin().publicarEvent(request);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_SISTRA,
					"Creació d'event",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
		} catch (TramitacioPluginException ex) {
			String errorDescripcio = "No s'ha pogut crear l'event (" +
					"expedientIdentificador=" + expedient.getTramitExpedientIdentificador() + ", " +
					"expedientClau=" + expedient.getTramitExpedientClau() + ", " +
					"unitatAdministrativa=" + expedient.getUnitatAdministrativa() + ", " +
					"interessatNif=" + expedient.getInteressatNif() + ", " +
					"eventTitol=" + dadesEvent.getTitol() + ", " +
					"eventText=" + dadesEvent.getText() + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_SISTRA,
					"Creació d'event",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(SISTRA. Crear event: " + errorDescripcio + ")", 
					ex);
		}
	}
	public TramitDto tramitacioObtenirDadesTramit(
			String numero,
			String clau) {
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"numero",
						numero),
				new IntegracioParametreDto(
						"clau",
						clau)
		};
		long t0 = System.currentTimeMillis();
		try {
			ObtenirDadesTramitRequest request = new ObtenirDadesTramitRequest();
			request.setNumero(numero);
			request.setClau(clau);
			DadesTramit dadesTramit = getTramitacioPlugin().obtenirDadesTramit(request);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_SISTRA,
					"Obtenir dades del tràmit",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
			return toTramitDto(dadesTramit);
		} catch (TramitacioPluginException ex) {
			String errorDescripcio = "No s'ha pogut obtenir la informació del tràmit (" +
					"numero=" + numero + ", " +
					"clau=" + clau + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_SISTRA,
					"Obtenir dades del tràmit",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					null,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					"(SISTRA. Tramitació: " + errorDescripcio + ")", 
					ex);
		}
	}

	public RespostaAnotacioRegistre tramitacioRegistrarNotificacio(
			RegistreNotificacio registreNotificacio,
			Expedient expedient,
			boolean crearExpedient) {
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"expedientIdentificador",
						registreNotificacio.getDadesExpedient().getIdentificador()),
				new IntegracioParametreDto(
						"expedientClau",
						registreNotificacio.getDadesExpedient().getClau()),
				new IntegracioParametreDto(
						"expedientUnitatAdministrativa",
						registreNotificacio.getDadesExpedient().getUnitatAdministrativa()),
				new IntegracioParametreDto(
						"interessatNif",
						registreNotificacio.getDadesInteressat().getNif()),
				new IntegracioParametreDto(
						"interessatNom",
						registreNotificacio.getDadesInteressat().getNom()),
				new IntegracioParametreDto(
						"interessatCognom1",
						registreNotificacio.getDadesInteressat().getCognom1()),
				new IntegracioParametreDto(
						"interessatCognom2",
						registreNotificacio.getDadesInteressat().getCognom2()),
				new IntegracioParametreDto(
						"interessatNomAmbCognoms",
						registreNotificacio.getDadesInteressat().getNomAmbCognoms()),
				new IntegracioParametreDto(
						"assumpte",
						registreNotificacio.getDadesNotificacio().getAssumpte())
		};
		
		
		try {
			logger.info("###===> Entrem en apartat relacionat amb expedients ");
			logger.info("###===> Comprovant si existeix expedient en la zona personal de l'interessat");
			if (!getTramitacioPlugin().existeixExpedient(
					new Long(registreNotificacio.getDadesExpedient().getUnitatAdministrativa()),
					registreNotificacio.getDadesExpedient().getIdentificador())) {
					crearExpedientPerNotificacio(registreNotificacio, expedient, parametres);
			}
		} catch (Exception ex) {
			String errorDescripcio = "No s'han pogut crear l'expedient a la zona persoanl (" +
					"expedientIdentificador=" + registreNotificacio.getDadesExpedient().getIdentificador() + ", " +
					"expedientClau=" + registreNotificacio.getDadesExpedient().getClau() + ", " +
					"oficinaOrganCodi=" + registreNotificacio.getDadesOficina().getOrganCodi() + ", " +
					"oficinaCodi=" + registreNotificacio.getDadesOficina().getOficinaCodi() + ")";
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(SISTRA. Creació d'expedient a la zona personal: " + errorDescripcio + ")", 
					ex);
		}
		
		long t0 = System.currentTimeMillis();
		RespostaAnotacioRegistre resposta = null;
		try {
			logger.info("###===> Entrem en apartat relacionat amb notificacions ");
			resposta = getTramitacioPlugin().registrarNotificacio(registreNotificacio);
			
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_SISTRA,
					"Registrar notificació",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
			
		} catch (TramitacioPluginException ex) {
			String errorDescripcio = "No s'han pogut registrar la notificació (" +
					"expedientIdentificador=" + registreNotificacio.getDadesExpedient().getIdentificador() + ", " +
					"expedientClau=" + registreNotificacio.getDadesExpedient().getClau() + ", " +
					"oficinaOrganCodi=" + registreNotificacio.getDadesOficina().getOrganCodi() + ", " +
					"oficinaCodi=" + registreNotificacio.getDadesOficina().getOficinaCodi() + ")";
			logger.error(
					errorDescripcio,
					ex);
			
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(SISTRA. Registrar notificació: " + errorDescripcio + ")", 
					ex);
		}
		return resposta;
	}
	
	private void crearExpedientPerNotificacio(RegistreNotificacio registreNotificacio, Expedient expedient, IntegracioParametreDto[] parametres) throws Exception {
		logger.info("###===> Preparem l'expedient a crear a la zona personal");
		
		ZonaperExpedientDto zonaperExpedient = new ZonaperExpedientDto();
		
		zonaperExpedient.setExpedientIdentificador(registreNotificacio.getDadesExpedient().getIdentificador());
		zonaperExpedient.setExpedientClau(registreNotificacio.getDadesExpedient().getClau());
		zonaperExpedient.setIdioma(registreNotificacio.getDadesNotificacio().getIdiomaCodi());
		
		try {
			zonaperExpedient.setUnitatAdministrativa(Long.parseLong(registreNotificacio.getDadesExpedient().getUnitatAdministrativa()));
		} catch (NumberFormatException e) {
			throw new NumberFormatException("La unitat administrativa ha de ser un valor numèric");
		}
		
		zonaperExpedient.setTramitNumero(expedient.getNumeroEntradaSistra());
		zonaperExpedient.setAutenticat(registreNotificacio.getDadesInteressat().isAutenticat());
		zonaperExpedient.setRepresentantNif(registreNotificacio.getDadesInteressat().getNif());
		zonaperExpedient.setRepresentatNif(registreNotificacio.getDadesInteressat().getNif());
		
		zonaperExpedient.setRepresentatNom(
				registreNotificacio.getDadesInteressat().getNom() != null ? 
						registreNotificacio.getDadesInteressat().getNom() : 
						registreNotificacio.getDadesInteressat().getNomAmbCognoms());
		zonaperExpedient.setRepresentatApe1(registreNotificacio.getDadesInteressat().getCognom1());
		zonaperExpedient.setRepresentatApe2(registreNotificacio.getDadesInteressat().getCognom2());
		
		zonaperExpedient.setAvisosHabilitat(true);
		zonaperExpedient.setAvisosEmail(registreNotificacio.getDadesInteressat().getEmail());
		zonaperExpedient.setAvisosSMS(registreNotificacio.getDadesInteressat().getMobil());
		zonaperExpedient.setDescripcio(expedient.getTitol());
		zonaperExpedient.setCodiProcediment(expedient.getTipus().getNotificacioCodiProcediment());
		
		PublicarExpedientRequest request = conversioTipusHelper.convertir(
				zonaperExpedient,
				PublicarExpedientRequest.class);
		
		logger.info("###===> Cridem al plugin per crear l'expedient a la zona personal");
		
		getTramitacioPlugin().publicarExpedient(request);
		
		expedient.setTramitExpedientIdentificador(registreNotificacio.getDadesExpedient().getIdentificador());
		expedient.setTramitExpedientClau(registreNotificacio.getDadesExpedient().getClau());
		expedientRepository.save(expedient);
		
		logger.info("###===> S'ha cridat al mètode per acutaltizar expedient Helium correctament.");
	}

	public RespostaJustificantRecepcio tramitacioObtenirJustificant(
			String registreNumero) {
		long t0 = System.currentTimeMillis();
		try {
			RespostaJustificantRecepcio resposta = getTramitacioPlugin().obtenirJustificantRecepcio(registreNumero);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_SISTRA,
					"Obtenir justificant",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					new IntegracioParametreDto(
							"registreNumero",
							registreNumero));
			return resposta;
		} catch (Exception ex) {
			String errorDescripcio = "No s'han pogut obtenir el justificant de recepció (" +
					"registreNumero=" + registreNumero + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_SISTRA,
					"Obtenir justificant",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					new IntegracioParametreDto(
							"registreNumero",
							registreNumero));
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					null,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					"(SISTRA. Obtenir justificant: " + errorDescripcio + ")", 
					ex);
		}
	}

	public RespostaJustificantDetallRecepcio tramitacioObtenirJustificantDetall(
			String registreNumero) {
		long t0 = System.currentTimeMillis();
		try {
			RespostaJustificantDetallRecepcio resposta = getTramitacioPlugin().obtenirJustificantDetallRecepcio(registreNumero);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_SISTRA,
					"Obtenir detall justificant",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					new IntegracioParametreDto(
							"registreNumero",
							registreNumero));
			return resposta;
		} catch (Exception ex) {
			String errorDescripcio = "No s'han pogut obtenir el detall del justificant de recepció (" +
					"registreNumero=" + registreNumero + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_SISTRA,
					"Obtenir detall justificant",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					new IntegracioParametreDto(
							"registreNumero",
							registreNumero));
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					null,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					"(SISTRA. Obtenir justificant detall: " + errorDescripcio + ")", 
					ex);
		}
	}

	public RegistreIdDto registreAnotacioEntrada(
			RegistreAnotacioDto anotacio,
			Expedient expedient) {
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"organCodi",
						anotacio.getOrganCodi()),
				new IntegracioParametreDto(
						"oficinaCodi",
						anotacio.getOficinaCodi()),
				new IntegracioParametreDto(
						"entitatCodi",
						anotacio.getEntitatCodi()),
				new IntegracioParametreDto(
						"unitatAdministrativa",
						anotacio.getUnitatAdministrativa()),
				new IntegracioParametreDto(
						"interessatNif",
						anotacio.getInteressatNif()),
				new IntegracioParametreDto(
						"assumpteExtracte",
						anotacio.getAssumpteExtracte())
		};
		long t0 = System.currentTimeMillis();
		try {
			RespostaAnotacioRegistre resposta = getRegistrePlugin().registrarEntrada(
					toRegistreEntrada(anotacio));
			if (!resposta.isOk()) {
				String errorDescripcio = "No s'ha pogut registrar l'entrada (" +
						getDescripcioErrorRegistre(anotacio) +
						"errorCodi=" + resposta.getErrorCodi() + ", " +
						"errorDescripcio=" + resposta.getErrorDescripcio() + ")";
				monitorIntegracioHelper.addAccioError(
						MonitorIntegracioHelper.INTCODI_REGISTRE,
						"Anotació d'entrada",
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						errorDescripcio,
						parametres);
				
				throw new SistemaExternException(
						expedient.getEntorn().getId(),
						expedient.getEntorn().getCodi(), 
						expedient.getEntorn().getNom(), 
						expedient.getId(), 
						expedient.getTitol(), 
						expedient.getNumero(), 
						expedient.getTipus().getId(), 
						expedient.getTipus().getCodi(), 
						expedient.getTipus().getNom(), 
						"(Registre d'entrada)", 
						errorDescripcio);
			} else {
				monitorIntegracioHelper.addAccioOk(
						MonitorIntegracioHelper.INTCODI_REGISTRE,
						"Anotació d'entrada",
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						parametres);
				RegistreIdDto registreId = new RegistreIdDto();
				registreId.setNumero(resposta.getNumero());
				registreId.setData(resposta.getData());
				return registreId;
			}
		} catch (RegistrePluginException ex) {
			String errorDescripcio = "No s'ha pogut registrar l'entrada (" +
					getDescripcioErrorRegistre(anotacio) + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_REGISTRE,
					"Anotació d'entrada",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(Registre d'entrada: " + errorDescripcio + ")", 
					ex);
		}
	}
	public RegistreIdDto registreAnotacioSortida(
			RegistreAnotacioDto anotacio,
			Expedient expedient) {
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"organCodi",
						anotacio.getOrganCodi()),
				new IntegracioParametreDto(
						"oficinaCodi",
						anotacio.getOficinaCodi()),
				new IntegracioParametreDto(
						"entitatCodi",
						anotacio.getEntitatCodi()),
				new IntegracioParametreDto(
						"unitatAdministrativa",
						anotacio.getUnitatAdministrativa()),
				new IntegracioParametreDto(
						"interessatNif",
						anotacio.getInteressatNif()),
				new IntegracioParametreDto(
						"assumpteExtracte",
						anotacio.getAssumpteExtracte())
		};
		long t0 = System.currentTimeMillis();
		try {
			RespostaAnotacioRegistre resposta = getRegistrePlugin().registrarSortida(
					toRegistreSortida(anotacio));
			if (!resposta.isOk()) {
				String errorDescripcio = "No s'ha pogut registrar la sortida (" +
						getDescripcioErrorRegistre(anotacio) +
						"errorCodi=" + resposta.getErrorCodi() + ", " +
						"errorDescripcio=" + resposta.getErrorDescripcio() + ")";
				monitorIntegracioHelper.addAccioError(
						MonitorIntegracioHelper.INTCODI_REGISTRE,
						"Anotació de sortida",
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						errorDescripcio,
						parametres);
				throw new SistemaExternException(
						expedient.getEntorn().getId(),
						expedient.getEntorn().getCodi(), 
						expedient.getEntorn().getNom(), 
						expedient.getId(), 
						expedient.getTitol(), 
						expedient.getNumero(), 
						expedient.getTipus().getId(), 
						expedient.getTipus().getCodi(), 
						expedient.getTipus().getNom(), 
						"(Registre de sortida)", 
						errorDescripcio);
			} else {
				monitorIntegracioHelper.addAccioOk(
						MonitorIntegracioHelper.INTCODI_REGISTRE,
						"Anotació de sortida",
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						parametres);
				RegistreIdDto registreId = new RegistreIdDto();
				registreId.setNumero(resposta.getNumero());
				registreId.setData(resposta.getData());
				return registreId;
			}
		} catch (RegistrePluginException ex) {
			String errorDescripcio = "No s'ha pogut registrar la sortida (" +
					getDescripcioErrorRegistre(anotacio) + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_REGISTRE,
					"Anotació de sortida",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(Registre de sortida: " + errorDescripcio + ")", 
					ex);
		}
	}
	
	public RegistreIdDto registreAnotacioSortida(
			RegistreAnotacio anotacio,
			Expedient expedient) {
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"organCodi",
						anotacio.getOrgan()),
				new IntegracioParametreDto(
						"oficinaCodi",
						anotacio.getOficinaCodi()),
				new IntegracioParametreDto(
						"llibreCodi",
						anotacio.getLlibreCodi())
		};
		long t0 = System.currentTimeMillis();
		try {
			RespostaAnotacioRegistre resposta = getRegistrePluginRebWeb3().registrarSortida(
					toRegistreAssentament(anotacio),
					"Helium",
					"3.2");
			if (!resposta.isOk()) {
				String errorDescripcio = "No s'ha pogut registrar la sortida (" +
						"errorCodi=" + resposta.getErrorCodi() + ", " +
						"errorDescripcio=" + resposta.getErrorDescripcio() + ")";
				monitorIntegracioHelper.addAccioError(
						MonitorIntegracioHelper.INTCODI_REGISTRE,
						"Anotació de sortida",
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						errorDescripcio,
						parametres);
				throw new SistemaExternException(
						expedient.getEntorn().getId(),
						expedient.getEntorn().getCodi(), 
						expedient.getEntorn().getNom(), 
						expedient.getId(), 
						expedient.getTitol(), 
						expedient.getNumero(), 
						expedient.getTipus().getId(), 
						expedient.getTipus().getCodi(), 
						expedient.getTipus().getNom(), 
						"(Registre de sortida)", 
						errorDescripcio);
			} else {
				monitorIntegracioHelper.addAccioOk(
						MonitorIntegracioHelper.INTCODI_REGISTRE,
						"Anotació de sortida",
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						parametres);
				RegistreIdDto registreId = new RegistreIdDto();
				registreId.setNumero(resposta.getNumeroRegistroFormateado());
				registreId.setData(resposta.getData());
				return registreId;
			}
		} catch (RegistrePluginException ex) {
			String errorDescripcio = "No s'ha pogut registrar la sortida";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_REGISTRE,
					"Anotació de sortida",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(Registre de sortida: " + errorDescripcio + ")", 
					ex);
		}
	}
	
	public RegistreIdDto registreNotificacio(
			RegistreNotificacioDto notificacio,
			Expedient expedient) {
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"organCodi",
						notificacio.getOrganCodi()),
				new IntegracioParametreDto(
						"oficinaCodi",
						notificacio.getOficinaCodi()),
				new IntegracioParametreDto(
						"entitatCodi",
						notificacio.getEntitatCodi()),
				new IntegracioParametreDto(
						"unitatAdministrativa",
						notificacio.getUnitatAdministrativa()),
				new IntegracioParametreDto(
						"interessatNif",
						notificacio.getInteressatNif()),
				new IntegracioParametreDto(
						"assumpteExtracte",
						notificacio.getAssumpteExtracte())
		};
		long t0 = System.currentTimeMillis();
		try {
			RespostaAnotacioRegistre resposta = getRegistrePlugin().registrarNotificacio(
					toRegistreNotificacio(notificacio));
			if (!resposta.isOk()) {
				String errorDescripcio = "No s'han pogut registrar la notificació (" +
						getDescripcioErrorRegistre(notificacio) +
						"errorCodi=" + resposta.getErrorCodi() + ", " +
						"errorDescripcio=" + resposta.getErrorDescripcio() + ")";
				monitorIntegracioHelper.addAccioError(
						MonitorIntegracioHelper.INTCODI_REGISTRE,
						"Notificació",
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						errorDescripcio,
						parametres);
				throw new SistemaExternException(
						expedient.getEntorn().getId(),
						expedient.getEntorn().getCodi(), 
						expedient.getEntorn().getNom(), 
						expedient.getId(), 
						expedient.getTitol(), 
						expedient.getNumero(), 
						expedient.getTipus().getId(), 
						expedient.getTipus().getCodi(), 
						expedient.getTipus().getNom(), 
						"(Registre de notificació)", 
						errorDescripcio);
			} else {
				monitorIntegracioHelper.addAccioOk(
						MonitorIntegracioHelper.INTCODI_REGISTRE,
						"Notificació",
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						parametres);
				RegistreIdDto registreId = new RegistreIdDto();
				registreId.setNumero(resposta.getNumero());
				registreId.setData(resposta.getData());
				return registreId;
			}
		} catch (RegistrePluginException ex) {
			String errorDescripcio = "No s'ha pogut registrar la notificació (" +
					getDescripcioErrorRegistre(notificacio) + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_REGISTRE,
					"Notificació",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(Registre de notificacio: " + errorDescripcio + ")", 
					ex);
		}
	}
	public Date registreDataJustificantRecepcio(
			String numeroRegistre,
			Expedient expedient) {
		long t0 = System.currentTimeMillis();
		try {
			RespostaJustificantRecepcio resposta = getRegistrePlugin().obtenirJustificantRecepcio(numeroRegistre);
			if (!resposta.isOk()) {
				String errorDescripcio = "No s'han pogut obtenir la data del justificant de la notificació (" +
						"numeroRegistre=" + numeroRegistre + ", " +
						"errorCodi=" + resposta.getErrorCodi() + ", " +
						"errorDescripcio=" + resposta.getErrorDescripcio() + ")";
				monitorIntegracioHelper.addAccioError(
						MonitorIntegracioHelper.INTCODI_REGISTRE,
						"Obtenir data del justificant de recepció",
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						errorDescripcio,
						new IntegracioParametreDto(
								"numeroRegistre",
								numeroRegistre));
				throw new SistemaExternException(
						expedient != null ? expedient.getEntorn().getId() : null,
						expedient != null ? expedient.getEntorn().getCodi() : null,
						expedient != null ? expedient.getEntorn().getNom() : null,
						expedient != null ? expedient.getId() : null,
						expedient != null ? expedient.getTitol() : null,
						expedient != null ? expedient.getNumero() : null,
						expedient != null ? expedient.getTipus().getId() : null,
						expedient != null ? expedient.getTipus().getCodi() : null,
						expedient != null ? expedient.getTipus().getNom() : null,
						"(Registre data de justificant)", 
						errorDescripcio);
			} else {
				monitorIntegracioHelper.addAccioOk(
						MonitorIntegracioHelper.INTCODI_REGISTRE,
						"Obtenir data del justificant de recepció",
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						new IntegracioParametreDto(
								"numeroRegistre",
								numeroRegistre));
				return resposta.getData();
			}
		} catch (RegistrePluginException ex) {
			String errorDescripcio = "No s'han pogut obtenir la data del justificant de la notificació (" +
					"numeroRegistre=" + numeroRegistre + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_REGISTRE,
					"Obtenir data del justificant de recepció",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					new IntegracioParametreDto(
							"numeroRegistre",
							numeroRegistre));
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(Registre data de justificant: " + errorDescripcio + ")", 
					ex);
		}
	}
	public String registreOficinaNom(
			String codi,
			Expedient expedient) {
		long t0 = System.currentTimeMillis();
		try {
			String oficinaNom = getRegistrePlugin().obtenirNomOficina(codi);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_REGISTRE,
					"Obtenir nom de l'oficina",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					new IntegracioParametreDto(
							"oficinaCodi",
							codi));
			return oficinaNom;
		} catch (RegistrePluginException ex) {
			String errorDescripcio = "No s'ha pogut obtenir el nom de l'oficina de registre (" +
					"codi=" + codi + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_REGISTRE,
					"Obtenir nom de l'oficina",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					new IntegracioParametreDto(
							"oficinaCodi",
							codi));
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(Registre oficina nom: " + errorDescripcio + ")", 
					ex);
		}
	}
	
	public String registreOficinaNom(
			String numRegistre,
			String usuariCodi,
			String entitatCodi,
			Expedient expedient) {
		
		long t0 = System.currentTimeMillis();
		try {
			RespostaConsultaRegistre respostaConsultaRegistre = getRegistrePluginRebWeb3().obtenirRegistrSortida(
					numRegistre,
					usuariCodi,
					entitatCodi);
			
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_REGISTRE,
					"Obtenir nom de l'oficina",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					new IntegracioParametreDto(
							"numRegistre",
							numRegistre),
					new IntegracioParametreDto(
							"usuariCodi",
							usuariCodi),
					new IntegracioParametreDto(
							"entitatCodi",
							entitatCodi));
			
			return respostaConsultaRegistre.getOficinaDenominacio();
		} catch (RegistrePluginException ex) {
			String errorDescripcio = "No s'ha pogut obtenir el nom de l'oficina de registre (" +
					"numRegistre=" + numRegistre + ", " +
					"usuariCodi=" + usuariCodi + ", " +
					"entitatCodi=" + entitatCodi + ")";
			
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_REGISTRE,
					"Obtenir nom de l'oficina",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					new IntegracioParametreDto(
							"numRegistre",
							numRegistre),
					new IntegracioParametreDto(
							"usuariCodi",
							usuariCodi),
					new IntegracioParametreDto(
							"entitatCodi",
							entitatCodi));
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(Registre oficina nom: " + errorDescripcio + ")", 
					ex);
		}
	}
	
	public boolean registreIsPluginActiu() {
		return getRegistrePlugin() != null;
	}
	
	public boolean registreIsPluginRebWeb3Actiu() {
		return getRegistrePluginRebWeb3() != null;
	}

	public String gestioDocumentalCreateDocument(
			Expedient expedient,
			String documentCodi,
			String documentDescripcio,
			Date documentData,
			String documentArxiuNom,
			byte[] documentArxiuContingut) {
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"expedient",
						expedient.getIdentificador()),
				new IntegracioParametreDto(
						"documentCodi",
						documentCodi),
				new IntegracioParametreDto(
						"documentDescripcio",
						documentDescripcio),
				new IntegracioParametreDto(
						"documentData",
						documentData),
				new IntegracioParametreDto(
						"documentArxiuNom",
						documentArxiuNom)
		};
		long t0 = System.currentTimeMillis();
		try {
			String expedientTipus = null;
			if (gestionDocumentalIsTipusExpedientDirecte()) {
				expedientTipus = expedient.getTipus().getCodi();
			} else if (gestionDocumentalIsTipusExpedientNou()) {
				expedientTipus = expedient.getEntorn().getCodi() + "_" + expedient.getTipus().getCodi();
			} else {
				expedientTipus = expedient.getEntorn().getCodi() + "#" + expedient.getTipus().getCodi();
			}
			String documentId = getGestioDocumentalPlugin().createDocument(
					expedient.getNumeroIdentificador(),
					expedientTipus,
					documentCodi,
					documentDescripcio,
					documentData,
					documentArxiuNom,
					documentArxiuContingut);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_GESDOC,
					"Pujar document",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
			return documentId;
		} catch (Exception ex) {
			String errorDescripcio = "No s'han pogut guardar el document a la gestió documental (" +
					"expedientIdentificador=" + expedient.getIdentificador() + ", " +
					"documentCodi=" + documentCodi + ", " +
					"documentDescripcio=" + documentDescripcio + ", " +
					"documentData=" + documentData + ", " +
					"documentArxiuNom=" + documentArxiuNom + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_GESDOC,
					"Pujar document",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(GESTIÓ DOCUMENTAL. Crear document: " + errorDescripcio + ")", 
					ex);
		}
	}
	public byte[] gestioDocumentalObtenirDocument(
			String documentId) {
		long t0 = System.currentTimeMillis();
		try {
			byte[] contingut = getGestioDocumentalPlugin().retrieveDocument(documentId);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_GESDOC,
					"Obtenir document",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					new IntegracioParametreDto(
							"documentId",
							documentId));
			return contingut;
		} catch (Exception ex) {
			String errorDescripcio = "No s'han pogut llegir el document de la gestió documental (documentId=" + documentId + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_GESDOC,
					"Obtenir document",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					new IntegracioParametreDto(
							"documentId",
							documentId));
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					null,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					"(GESTIÓ DOCUMENTAL. Crear document: " + errorDescripcio + ")", 
					ex);
		}
	}
	public void gestioDocumentalDeleteDocument(
			String documentId,
			Expedient expedient) {
		long t0 = System.currentTimeMillis();
		try {
			if (getGestioDocumentalPlugin() != null)
				getGestioDocumentalPlugin().deleteDocument(documentId);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_GESDOC,
					"Esborrar document",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					new IntegracioParametreDto(
							"documentId",
							documentId));
		} catch (Exception ex) {
			String errorDescripcio = "No s'han pogut esborrar el document de la gestió documental (documentId=" + documentId + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_GESDOC,
					"Obtenir document",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					new IntegracioParametreDto(
							"documentId",
							documentId));
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(GESTIÓ DOCUMENTAL. Delete document: " + errorDescripcio + ")", 
					ex);
		}
	}

	public boolean gestioDocumentalIsPluginActiu() {
		return getGestioDocumentalPlugin() != null;
	}

	public Integer portasignaturesEnviar(
			DocumentDto document,
			List<DocumentDto> annexos,
			PersonaDto persona,
			List<PersonaDto> personesPas1,
			int minSignatarisPas1,
			List<PersonaDto> personesPas2,
			int minSignatarisPas2,
			List<PersonaDto> personesPas3,
			int minSignatarisPas3,
			Expedient expedient,
			String importancia,
			Date dataLimit,
			Long tokenId,
			Long processInstanceId,
			String transicioOK,
			String transicioKO) {
		
		IntegracioParametreDto[] parametres = null;
		long t0 = System.currentTimeMillis();

		try {
			
			parametres = new IntegracioParametreDto[9];
			parametres[0] = new IntegracioParametreDto(
					"expedient",
					expedient != null ? expedient.getIdentificador() : null);
			parametres[1] = new IntegracioParametreDto(
					"documentCodi",
					document != null ? document.getDocumentCodi() : null);
			parametres[2] = new IntegracioParametreDto(
					"documentNom",
					document != null ? document.getDocumentNom() : null);
			parametres[3] = new IntegracioParametreDto(
					"documentTipus",
					document != null ? document.getTipusDocPortasignatures() : null);
			parametres[4] = new IntegracioParametreDto(
					"arxiuNom",
					document != null ? document.getArxiuNom() : null);
			parametres[5] = new IntegracioParametreDto(
					"personaCodi",
					persona != null ? persona.getCodi() : null);
			parametres[6] = new IntegracioParametreDto(
					"personesCodiPas1",
					personestoString(personesPas1));
			parametres[7] = new IntegracioParametreDto(
					"personesCodiPas2",
					personestoString(personesPas2));
			parametres[8] = new IntegracioParametreDto(
					"personesCodiPas3",
					personestoString(personesPas3));
			
			if (document == null) {
				throw new NullPointerException("El document per a enviar a portafirmes es null.");
			}
			
			Integer resposta = getPortasignaturesPlugin().uploadDocument(
					getDocumentPortasignatures(document, expedient),
					getAnnexosPortasignatures(annexos, expedient),
					false,
					getPassesSignatura(
							getSignatariIdPerPersona(persona),
							personesPas1,
							minSignatarisPas1,
							personesPas2,
							minSignatarisPas2,
							personesPas3,
							minSignatarisPas3),
					expedient.getIdentificador(),
					importancia,
					dataLimit);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_PFIRMA,
					"Enviar document a firmar",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
			Calendar cal = Calendar.getInstance();
			Portasignatures portasignatures = new Portasignatures();
			portasignatures.setDocumentId(resposta);
			portasignatures.setTokenId(tokenId);
			portasignatures.setDataEnviat(cal.getTime());
			portasignatures.setEstat(TipusEstat.PENDENT);
			portasignatures.setDocumentStoreId(document.getId());
			portasignatures.setTransicioOK(transicioOK);
			portasignatures.setTransicioKO(transicioKO);
			portasignatures.setExpedient(expedient);
			portasignatures.setProcessInstanceId(processInstanceId.toString());
			portasignaturesRepository.save(portasignatures);
			return resposta;
		} catch (Exception ex) {
			String errorDescripcio = "No s'han pogut enviar el document al portafirmes (" +
					"documentId=" + (document == null ? "NULL" : document.getId()) + ", " +
					"destinatari=" + (persona == null ? "NULL" : persona.getCodi()) + ", " +
					"expedient=" + (expedient == null ? "NULL" : expedient.getIdentificador()) + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_PFIRMA,
					"Enviar document a firmar",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(PORTASIGNATURES. Enviar: " + errorDescripcio + ")", 
					ex);
		}
	}
	
	private String personestoString(List<PersonaDto> persones) {
		if (persones == null)
			return null;
		
		String sPersones = "";
		for (PersonaDto persona: persones) {
			if (persona != null)
				sPersones += persona.getCodi() + ",";
		}
		if (sPersones.length() > 0)
			sPersones = sPersones.substring(0, sPersones.length() - 1);
		return sPersones;
	}

	public void portasignaturesCancelar(
			List<Integer> documentIds) {
		StringBuilder ids = new StringBuilder();
		boolean first = true;
		for (Integer documentId: documentIds) {
			if (first)
				first = false;
			else
				ids.append(", ");
			ids.append(documentId.toString());
		}
		long t0 = System.currentTimeMillis();
		try {
			getPortasignaturesPlugin().deleteDocuments(
					documentIds);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_PFIRMA,
					"Cancel·lació d'enviaments de documents",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					new IntegracioParametreDto(
							"documentIds",
							ids.toString()));
			for (Integer documentId: documentIds) {
				Portasignatures portasignatures = portasignaturesRepository.findByDocumentId(documentId);
				if (portasignatures != null) {
					portasignatures.setEstat(TipusEstat.CANCELAT);
				}
			}
		} catch (PortasignaturesPluginException ex) {
			String errorDescripcio = "No s'han pogut cancel·lar els enviaments al portafirmes (" +
					"ids=" + ids.toString() + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_PFIRMA,
					"Cancel·lació d'enviaments de documents",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					new IntegracioParametreDto(
							"documentIds",
							ids.toString()));
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					null,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					"(PORTASIGNATURES. Cancelar: " + errorDescripcio + ")", 
					ex);
		}
	}

	public String custodiaAfegirSignatura(
			Long documentId,
			String gesdocId,
			String nomArxiuSignat,
			String codiTipusCustodia,
			byte[] signatura) {
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"documentId",
						documentId),
				new IntegracioParametreDto(
						"gesdocId",
						gesdocId),
				new IntegracioParametreDto(
						"nomArxiuSignat",
						nomArxiuSignat),
				new IntegracioParametreDto(
						"codiTipusCustodia",
						codiTipusCustodia)
		};
		long t0 = System.currentTimeMillis();
		try {
			String custodiaId = getCustodiaPlugin().addSignature(
					documentId.toString(),
					gesdocId,
					nomArxiuSignat,
					codiTipusCustodia,
					signatura);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_CUSTODIA,
					"Enviament de document a custòdia",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
			return custodiaId;
		} catch (CustodiaPluginException ex) {
			String errorDescripcio = "No s'ha pogut afegir la signatura a la custòdia (" +
					"documentId=" + documentId + ", " +
					"gesdocId=" + gesdocId + ", " +
					"nomArxiuSignat=" + nomArxiuSignat + ", " +
					"codiTipusCustodia=" + codiTipusCustodia + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_CUSTODIA,
					"Enviament de document a custòdia",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					null,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					"(CUSTÒDIA. Afegir signatura: " + errorDescripcio + ")", 
					ex);
		}
	}

	public List<RespostaValidacioSignatura> custodiaDadesValidacioSignatura(
			String documentId) {
		long t0 = System.currentTimeMillis();
		try {
			List<RespostaValidacioSignatura> validacions = getCustodiaPlugin().dadesValidacioSignatura(
					documentId);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_CUSTODIA,
					"Obtenció de dades de validació de signatura",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					new IntegracioParametreDto(
							"documentId",
							documentId));
			return validacions;
		} catch (CustodiaPluginException ex) {
			String errorDescripcio = "No s'han pogut obtenir les dades de les signatures de la custòdia (documentId=" + documentId + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_CUSTODIA,
					"Obtenció de dades de validació de signatura",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					new IntegracioParametreDto(
							"documentId",
							documentId));
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					null,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					"(CUSTÒDIA. Dades validació signatura: " + errorDescripcio + ")", 
					ex);
		}
	}

	public List<byte[]> custodiaObtenirSignatures(
			String documentId) {
		long t0 = System.currentTimeMillis();
		try {
			List<byte[]> signatures = getCustodiaPlugin().getSignatures(documentId);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_CUSTODIA,
					"Obtenció de signatures",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					new IntegracioParametreDto(
							"documentId",
							documentId));
			return signatures;
		} catch (CustodiaPluginException ex) {
			String errorDescripcio = "No s'han pogut obtenirles signatures de la custòdia (documentId=" + documentId + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_CUSTODIA,
					"Obtenció de signatures",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					new IntegracioParametreDto(
							"documentId",
							documentId));
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					null,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					"(CUSTÒDIA. Obtenir signatures: " + errorDescripcio + ")", 
					ex);
		}
	}

	public byte[] custodiaObtenirSignaturesAmbArxiu(
			String documentId) {
		long t0 = System.currentTimeMillis();
		try {
			byte[] signaturesAmbArxiu = getCustodiaPlugin().getSignaturesAmbArxiu(documentId);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_CUSTODIA,
					"Obtenció de signatures amb arxiu",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					new IntegracioParametreDto(
							"documentId",
							documentId));
			return signaturesAmbArxiu;
		} catch (CustodiaPluginException ex) {
			String errorDescripcio = "No s'han pogut obtenirles signatures amb arxiu de la custòdia (documentId=" + documentId + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_CUSTODIA,
					"Obtenció de signatures amb arxiu",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					new IntegracioParametreDto(
							"documentId",
							documentId));
			logger.error(
					errorDescripcio,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					null,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					"(CUSTÒDIA. Obtenir signatures amb arxiu: " + errorDescripcio + ")", 
					ex);
		}
	}

	public void custodiaEsborrarSignatures(
			String documentId,
			Expedient expedient) {
		long t0 = System.currentTimeMillis();
		try {
			getCustodiaPlugin().deleteSignatures(documentId);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_CUSTODIA,
					"Esborrar documents custodiats",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					new IntegracioParametreDto(
							"documentId",
							documentId));
		} catch (CustodiaPluginException ex) {
			String errorDescripcio = "No s'ha pogut esborrar el document de la custòdia (documentId=" + documentId + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_CUSTODIA,
					"Esborrar documents custodiats",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					new IntegracioParametreDto(
							"documentId",
							documentId));
			logger.error(errorDescripcio,ex);
			
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(CUSTÒDIA: Esborrar signatures: " + errorDescripcio + ")", 
					ex);
		}
	}

	public String custodiaObtenirUrlComprovacioSignatura(
			String documentId) {
		long t0 = System.currentTimeMillis();
		try {
			String url = getCustodiaPlugin().getUrlComprovacioSignatura(documentId);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_CUSTODIA,
					"Obtenir URL de comprovació de signatura",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					new IntegracioParametreDto(
							"documentId",
							documentId));
			return url;
		} catch (CustodiaPluginException ex) {
			String errorDescripcio = "No s'ha pogut obtenir url de comprovació de la custòdia (documentId=" + documentId + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_CUSTODIA,
					"Obtenir URL de comprovació de signatura",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					new IntegracioParametreDto(
							"documentId",
							documentId));
			logger.error(
					documentId,
					ex);
			throw SistemaExternException.tractarSistemaExternException(
					null,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					"(CUSTÒDIA. Obtenir URL comprovació: " + errorDescripcio + ")", 
					ex);
		}
	}

	public boolean custodiaPotObtenirInfoSignatures() {
		return getCustodiaPlugin().potObtenirInfoSignatures();
	}

	public boolean custodiaIsValidacioImplicita() {
		return getCustodiaPlugin().isValidacioImplicita();
	}
	
	public boolean custodiaIsPluginActiu() {
		return getCustodiaPlugin() != null;
	}

	public RespostaValidacioSignatura signaturaVerificar(
			byte[] document,
			byte[] signatura,
			boolean obtenirDadesCertificat) {
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"document",
						(document != null ? document.length : 0) + " bytes"),
				new IntegracioParametreDto(
						"signatura",
						(signatura != null ? signatura.length : 0) + " bytes"),
				new IntegracioParametreDto(
						"obtenirDadesCertificat",
						new Boolean(obtenirDadesCertificat).toString())
		};
		long t0 = System.currentTimeMillis();
		try {
			RespostaValidacioSignatura resposta = getSignaturaPlugin().verificarSignatura(
					document,
					signatura,
					obtenirDadesCertificat);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_FIRMA,
					"Validació de signatura",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
			return resposta;
		} catch (SignaturaPluginException ex) {
			String errorDescripcio = "No s'han pogut verificar la signatura";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_FIRMA,
					"Validació de signatura",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			logger.error(errorDescripcio, ex);
			throw SistemaExternException.tractarSistemaExternException(
					null,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					"(PLUGIN SIGNATURA. Validació de signatura: " + errorDescripcio + ")", 
					ex);
		}
	}

	public Portasignatures findPortasignaturesInfo(String processInstanceId, Long documentStoreId) {		
		Portasignatures psigna = portasignaturesRepository.findByProcessInstanceIdAndDocumentStoreId(processInstanceId, documentStoreId);
		return psigna;
	}

	private TramitDto toTramitDto(DadesTramit dadesTramit) {
		TramitDto dto = conversioTipusHelper.convertir(
				dadesTramit,
				TramitDto.class);
		if (dto.getDocuments() != null)
			dto.getDocuments().clear();
		if (dadesTramit.getDocuments() != null) {
			List<TramitDocumentDto> documents = new ArrayList<TramitDocumentDto>();
			for (DocumentTramit dt: dadesTramit.getDocuments()) {
				TramitDocumentDto doc = new TramitDocumentDto();
				doc.setNom(dt.getNom());
				doc.setIdentificador(dt.getIdentificador());
				doc.setInstanciaNumero(dt.getInstanciaNumero());
				if (dt.getDocumentTelematic() != null) {
					doc.setTelematic(true);
					doc.setTelematicArxiuNom(dt.getDocumentTelematic().getArxiuNom());
					doc.setTelematicArxiuExtensio(dt.getDocumentTelematic().getArxiuExtensio());
					doc.setTelematicArxiuContingut(dt.getDocumentTelematic().getArxiuContingut());
					doc.setTelematicReferenciaGestorDocumental(dt.getDocumentTelematic().getReferenciaGestorDocumental());
					doc.setTelematicReferenciaCodi(dt.getDocumentTelematic().getReferenciaCodi());
					doc.setTelematicReferenciaClau(dt.getDocumentTelematic().getReferenciaClau());
					doc.setTelematicEstructurat(dt.getDocumentTelematic().getEstructurat());
					if (dt.getDocumentTelematic().getSignatures() != null) {
						List<TramitDocumentSignaturaDto> telematicSignatures = new ArrayList<TramitDocumentSignaturaDto>();
						for (Signatura s: dt.getDocumentTelematic().getSignatures()) {
							telematicSignatures.add(
									doc.newDocumentSignatura(
											s.getSignatura(),
											s.getFormat()));
						}
						doc.setTelematicSignatures(telematicSignatures);
					}
				}
				if (dt.getDocumentPresencial() != null) {
					doc.setPresencial(true);
					doc.setPresencialTipus(dt.getDocumentPresencial().getTipus());
					doc.setPresencialDocumentCompulsar(dt.getDocumentPresencial().getDocumentCompulsar());
					doc.setPresencialFotocopia(dt.getDocumentPresencial().getFotocopia());
					doc.setPresencialSignatura(dt.getDocumentPresencial().getSignatura());
				}
				documents.add(doc);
			}
			dto.setDocuments(documents);
		}
		return dto;
	}

	private RegistreEntrada toRegistreEntrada(RegistreAnotacioDto anotacio) {
		RegistreEntrada registreEntrada = new RegistreEntrada();
		DadesOficina dadesOficina = new DadesOficina();
		dadesOficina.setOrganCodi(anotacio.getOrganCodi());
		dadesOficina.setOficinaCodi(anotacio.getOficinaCodi());
		registreEntrada.setDadesOficina(dadesOficina);
		DadesInteressat dadesInteressat = new DadesInteressat();
		dadesInteressat.setEntitatCodi(anotacio.getEntitatCodi());
		dadesInteressat.setAutenticat(anotacio.isInteressatAutenticat());
		dadesInteressat.setNif(anotacio.getInteressatNif());
		dadesInteressat.setNomAmbCognoms(anotacio.getInteressatNomAmbCognoms());
		dadesInteressat.setPaisCodi(anotacio.getInteressatPaisCodi());
		dadesInteressat.setPaisNom(anotacio.getInteressatPaisNom());
		dadesInteressat.setProvinciaCodi(anotacio.getInteressatProvinciaCodi());
		dadesInteressat.setProvinciaNom(anotacio.getInteressatProvinciaNom());
		dadesInteressat.setMunicipiCodi(anotacio.getInteressatMunicipiCodi());
		dadesInteressat.setMunicipiNom(anotacio.getInteressatMunicipiNom());
		registreEntrada.setDadesInteressat(dadesInteressat);
		DadesRepresentat dadesRepresentat = new DadesRepresentat();
		dadesRepresentat.setNif(anotacio.getRepresentatNif());
		dadesRepresentat.setNomAmbCognoms(anotacio.getRepresentatNomAmbCognoms());
		registreEntrada.setDadesRepresentat(dadesRepresentat);
		DadesAssumpte dadesAssumpte = new DadesAssumpte();
		dadesAssumpte.setIdiomaCodi(anotacio.getAssumpteIdiomaCodi());
		dadesAssumpte.setTipus(anotacio.getAssumpteTipus());
		dadesAssumpte.setAssumpte(anotacio.getAssumpteExtracte());
		dadesAssumpte.setUnitatAdministrativa(anotacio.getUnitatAdministrativa());
		dadesAssumpte.setRegistreNumero(anotacio.getAssumpteRegistreNumero());
		dadesAssumpte.setRegistreAny(anotacio.getAssumpteRegistreAny());
		registreEntrada.setDadesAssumpte(dadesAssumpte);
		if (anotacio.getAnnexos() != null) {
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			for (RegistreAnnexDto annex: anotacio.getAnnexos()) {
				DocumentRegistre document = new DocumentRegistre();
				document.setNom(annex.getNom());
				document.setData(annex.getData());
				document.setIdiomaCodi(annex.getIdiomaCodi());
				document.setArxiuNom(annex.getArxiuNom());
				document.setArxiuContingut(annex.getArxiuContingut());
				documents.add(document);
			}
			registreEntrada.setDocuments(documents);
		}
		return registreEntrada;
	}
	private RegistreSortida toRegistreSortida(RegistreAnotacioDto anotacio) {
		RegistreSortida registreSortida = new RegistreSortida();
		DadesOficina dadesOficina = new DadesOficina();
		dadesOficina.setOrganCodi(anotacio.getOrganCodi());
		dadesOficina.setOficinaCodi(anotacio.getOficinaCodi());
		registreSortida.setDadesOficina(dadesOficina);
		DadesInteressat dadesInteressat = new DadesInteressat();
		dadesInteressat.setEntitatCodi(anotacio.getEntitatCodi());
		dadesInteressat.setAutenticat(anotacio.isInteressatAutenticat());
		dadesInteressat.setNif(anotacio.getInteressatNif());
		dadesInteressat.setNomAmbCognoms(anotacio.getInteressatNomAmbCognoms());
		dadesInteressat.setPaisCodi(anotacio.getInteressatPaisCodi());
		dadesInteressat.setPaisNom(anotacio.getInteressatPaisNom());
		dadesInteressat.setProvinciaCodi(anotacio.getInteressatProvinciaCodi());
		dadesInteressat.setProvinciaNom(anotacio.getInteressatProvinciaNom());
		dadesInteressat.setMunicipiCodi(anotacio.getInteressatMunicipiCodi());
		dadesInteressat.setMunicipiNom(anotacio.getInteressatMunicipiNom());
		registreSortida.setDadesInteressat(dadesInteressat);
		DadesRepresentat dadesRepresentat = new DadesRepresentat();
		dadesRepresentat.setNif(anotacio.getRepresentatNif());
		dadesRepresentat.setNomAmbCognoms(anotacio.getRepresentatNomAmbCognoms());
		registreSortida.setDadesRepresentat(dadesRepresentat);
		DadesAssumpte dadesAssumpte = new DadesAssumpte();
		dadesAssumpte.setIdiomaCodi(anotacio.getAssumpteIdiomaCodi());
		dadesAssumpte.setTipus(anotacio.getAssumpteTipus());
		dadesAssumpte.setAssumpte(anotacio.getAssumpteExtracte());
		dadesAssumpte.setUnitatAdministrativa(anotacio.getUnitatAdministrativa());
		dadesAssumpte.setRegistreNumero(anotacio.getAssumpteRegistreNumero());
		dadesAssumpte.setRegistreAny(anotacio.getAssumpteRegistreAny());
		registreSortida.setDadesAssumpte(dadesAssumpte);
		if (anotacio.getAnnexos() != null) {
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			for (RegistreAnnexDto annex: anotacio.getAnnexos()) {
				DocumentRegistre document = new DocumentRegistre();
				document.setNom(annex.getNom());
				document.setData(annex.getData());
				document.setIdiomaCodi(annex.getIdiomaCodi());
				document.setArxiuNom(annex.getArxiuNom());
				document.setArxiuContingut(annex.getArxiuContingut());
				documents.add(document);
			}
			registreSortida.setDocuments(documents);
		}
		return registreSortida;
	}
	private RegistreAssentament toRegistreAssentament(RegistreAnotacio anotacio) {
		RegistreAssentament registreAssentament = new RegistreAssentament();
		
		registreAssentament.setNumero(anotacio.getNumero());
		registreAssentament.setData(anotacio.getData());
		registreAssentament.setIdentificador(anotacio.getIdentificador());
		registreAssentament.setOrgan(anotacio.getOrgan());
		registreAssentament.setOrganDescripcio(anotacio.getOrganDescripcio());
		registreAssentament.setOficinaCodi(anotacio.getOficinaCodi());
		registreAssentament.setOficinaDescripcio(anotacio.getOficinaDescripcio());
		registreAssentament.setLlibreCodi(anotacio.getLlibreCodi());
		registreAssentament.setLlibreDescripcio(anotacio.getLlibreDescripcio());
		registreAssentament.setExtracte(anotacio.getExtracte());
		registreAssentament.setAssumpteTipusCodi(anotacio.getAssumpteTipusCodi());
		registreAssentament.setAssumpteTipusDescripcio(anotacio.getAssumpteTipusDescripcio());
		registreAssentament.setAssumpteCodi(anotacio.getAssumpteCodi());
		registreAssentament.setAssumpteDescripcio(anotacio.getAssumpteDescripcio());
		registreAssentament.setReferencia(anotacio.getReferencia());
		registreAssentament.setExpedientNumero(anotacio.getExpedientNumero());
		registreAssentament.setIdiomaCodi(anotacio.getIdiomaCodi());
		registreAssentament.setIdiomaDescripcio(anotacio.getIdiomaDescripcio());
		registreAssentament.setTransportTipusCodi(anotacio.getTransportTipusCodi());
		registreAssentament.setTransportTipusDescripcio(anotacio.getTransportTipusDescripcio());
		registreAssentament.setTransportNumero(anotacio.getTransportNumero());
		registreAssentament.setUsuariCodi(anotacio.getUsuariCodi());
		registreAssentament.setUsuariNom(anotacio.getUsuariNom());
		registreAssentament.setUsuariContacte(anotacio.getUsuariContacte());
		registreAssentament.setAplicacioCodi(anotacio.getAplicacioCodi());
		registreAssentament.setAplicacioVersio(anotacio.getAplicacioVersio());
		registreAssentament.setDocumentacioFisicaCodi(anotacio.getDocumentacioFisicaCodi());
		registreAssentament.setDocumentacioFisicaDescripcio(anotacio.getDocumentacioFisicaDescripcio());
		registreAssentament.setObservacions(anotacio.getObservacions());
		registreAssentament.setExposa(anotacio.getExposa());
		registreAssentament.setSolicita(anotacio.getSolicita());
		
		List<RegistreAssentamentInteressat> interessats = new ArrayList<RegistreAssentamentInteressat>();

		for (RegistreInteressat regInt: anotacio.getInteressats()) {
			interessats.add(toRegistreAssentamentInteressat(regInt));
		}
		
		registreAssentament.setInteressats(interessats);
		
		if (anotacio.getAnnexos() != null) {
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			for (RegistreAnnex annex: anotacio.getAnnexos()) {
				DocumentRegistre document = new DocumentRegistre();
				document.setNom(annex.getTitol());
				document.setData(annex.getDataCaptura());
				document.setArxiuNom(annex.getFitxerNom());
				document.setArxiuContingut(annex.getFitxerContingut());
				documents.add(document);
			}
			registreAssentament.setDocuments(documents);
		}
		
		return registreAssentament;
	}
	
	private RegistreAssentamentInteressat toRegistreAssentamentInteressat(RegistreInteressat interessat) {
		RegistreAssentamentInteressat registreAssentamentInteressat = new RegistreAssentamentInteressat();
		
		registreAssentamentInteressat.setTipus(interessat.getTipus());
		registreAssentamentInteressat.setDocumentTipus(interessat.getDocumentTipus());
		registreAssentamentInteressat.setDocumentNum(interessat.getDocumentNum());
		registreAssentamentInteressat.setNom(interessat.getNom());
		registreAssentamentInteressat.setLlinatge1(interessat.getLlinatge1());
		registreAssentamentInteressat.setLlinatge2(interessat.getLlinatge2());
		registreAssentamentInteressat.setRaoSocial(interessat.getRaoSocial());
		registreAssentamentInteressat.setPais(interessat.getPais());
		registreAssentamentInteressat.setProvincia(interessat.getProvincia());
		registreAssentamentInteressat.setMunicipi(interessat.getMunicipi());
		registreAssentamentInteressat.setAdresa(interessat.getAdresa());
		registreAssentamentInteressat.setCodiPostal(interessat.getCodiPostal());
		registreAssentamentInteressat.setEmail(interessat.getEmail());
		registreAssentamentInteressat.setTelefon(interessat.getTelefon());
		registreAssentamentInteressat.setEmailHabilitat(interessat.getEmailHabilitat());
		registreAssentamentInteressat.setCanalPreferent(interessat.getCanalPreferent());
		registreAssentamentInteressat.setObservacions(interessat.getObservacions());
		registreAssentamentInteressat.setTipus(interessat.getTipus());
		
		if (interessat.getRepresentant() != null) {
			registreAssentamentInteressat.setRepresentant(toRegistreAssentamentInteressat(interessat.getRepresentant()));
		}
		
		return registreAssentamentInteressat;
	}
	
	private RegistreNotificacio toRegistreNotificacio(
			RegistreNotificacioDto notificacio) {
		RegistreNotificacio registreNotificacio = new RegistreNotificacio();
		DadesExpedient dadesExpedient = new DadesExpedient();
		dadesExpedient.setIdentificador(notificacio.getExpedientIdentificador());
		dadesExpedient.setClau(notificacio.getExpedientClau());
		dadesExpedient.setUnitatAdministrativa(notificacio.getExpedientUnitatAdministrativa());
		registreNotificacio.setDadesExpedient(dadesExpedient);
		DadesOficina dadesOficina = new DadesOficina();
		dadesOficina.setOrganCodi(notificacio.getOrganCodi());
		dadesOficina.setOficinaCodi(notificacio.getOficinaCodi());
		registreNotificacio.setDadesOficina(dadesOficina);
		DadesInteressat dadesInteressat = new DadesInteressat();
		dadesInteressat.setEntitatCodi(notificacio.getEntitatCodi());
		dadesInteressat.setAutenticat(notificacio.isInteressatAutenticat());
		dadesInteressat.setNif(notificacio.getInteressatNif());
		dadesInteressat.setNomAmbCognoms(notificacio.getInteressatNomAmbCognoms());
		dadesInteressat.setPaisCodi(notificacio.getInteressatPaisCodi());
		dadesInteressat.setPaisNom(notificacio.getInteressatPaisNom());
		dadesInteressat.setProvinciaCodi(notificacio.getInteressatProvinciaCodi());
		dadesInteressat.setProvinciaNom(notificacio.getInteressatProvinciaNom());
		dadesInteressat.setMunicipiCodi(notificacio.getInteressatMunicipiCodi());
		dadesInteressat.setMunicipiNom(notificacio.getInteressatMunicipiNom());
		registreNotificacio.setDadesInteressat(dadesInteressat);
		DadesRepresentat dadesRepresentat = new DadesRepresentat();
		dadesRepresentat.setNif(notificacio.getRepresentatNif());
		dadesRepresentat.setNomAmbCognoms(notificacio.getRepresentatNomAmbCognoms());
		registreNotificacio.setDadesRepresentat(dadesRepresentat);
		DadesNotificacio dadesNotificacio = new DadesNotificacio();
		dadesNotificacio.setJustificantRecepcio(notificacio.isNotificacioJustificantRecepcio());
		dadesNotificacio.setAvisTitol(notificacio.getNotificacioAvisTitol());
		dadesNotificacio.setAvisText(notificacio.getNotificacioAvisText());
		dadesNotificacio.setAvisTextSms(notificacio.getNotificacioAvisTextSms());
		dadesNotificacio.setOficiTitol(notificacio.getNotificacioOficiTitol());
		dadesNotificacio.setOficiText(notificacio.getNotificacioOficiText());
		dadesNotificacio.setIdiomaCodi(notificacio.getAssumpteIdiomaCodi());
		dadesNotificacio.setTipus(notificacio.getAssumpteTipus());
		dadesNotificacio.setAssumpte(notificacio.getAssumpteExtracte());
		dadesNotificacio.setUnitatAdministrativa(notificacio.getUnitatAdministrativa());
		dadesNotificacio.setRegistreNumero(notificacio.getAssumpteRegistreNumero());
		dadesNotificacio.setRegistreAny(notificacio.getAssumpteRegistreAny());
		if (notificacio.getTramitSubsanacioIdentificador() != null) {
			TramitSubsanacio tramitSubsanacio = new TramitSubsanacio();
			tramitSubsanacio.setIdentificador(notificacio.getTramitSubsanacioIdentificador());
			tramitSubsanacio.setVersio(notificacio.getTramitSubsanacioVersio());
			tramitSubsanacio.setDescripcio(notificacio.getTramitSubsanacioDescripcio());
			if (notificacio.getTramitSubsanacioParametres() != null) {
				List<TramitSubsanacioParametre> parametres = new ArrayList<TramitSubsanacioParametre>();
				for (RegistreNotificacioTramitSubsanacioParametreDto param: notificacio.getTramitSubsanacioParametres()) {
					TramitSubsanacioParametre p = new TramitSubsanacioParametre();
					p.setParametre(param.getParametre());
					p.setValor(param.getValor());
					parametres.add(p);
				}
				tramitSubsanacio.setParametres(parametres);
			}
			dadesNotificacio.setOficiTramitSubsanacio(tramitSubsanacio);
		}
		registreNotificacio.setDadesNotificacio(dadesNotificacio);
		if (notificacio.getAnnexos() != null) {
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			for (RegistreAnnexDto annex: notificacio.getAnnexos()) {
				DocumentRegistre document = new DocumentRegistre();
				document.setNom(annex.getNom());
				document.setData(annex.getData());
				document.setIdiomaCodi(annex.getIdiomaCodi());
				document.setArxiuNom(annex.getArxiuNom());
				document.setArxiuContingut(annex.getArxiuContingut());
				documents.add(document);
			}
			registreNotificacio.setDocuments(documents);
		}
		return registreNotificacio;
	}

	private DocumentPortasignatures getDocumentPortasignatures(
			DocumentDto document,
			Expedient expedient) {
		DocumentPortasignatures documentPs = new DocumentPortasignatures();
		documentPs.setTitol(
				expedient.getIdentificador() + ": " + document.getDocumentNom());
		documentPs.setArxiuNom(document.getVistaNom());
		documentPs.setArxiuContingut(document.getVistaContingut());
		documentPs.setTipus(document.getTipusDocPortasignatures());
		documentPs.setSignat(document.isSignat());
		documentPs.setReference(document.getId().toString());
		return documentPs;
	}
	private List<DocumentPortasignatures> getAnnexosPortasignatures(
			List<DocumentDto> annexos,
			Expedient expedient) {
		if (annexos == null)
			return null;
		List<DocumentPortasignatures> resposta = new ArrayList<DocumentPortasignatures>();
		for (DocumentDto document: annexos)
			resposta.add(getDocumentPortasignatures(document, expedient));
		return resposta;
	}
	private PasSignatura[] getPassesSignatura(
			String signatariId,
			List<PersonaDto> personesPas1,
			int minSignatarisPas1,
			List<PersonaDto> personesPas2,
			int minSignatarisPas2,
			List<PersonaDto> personesPas3,
			int minSignatarisPas3) {
		if (personesPas1 != null && personesPas1.size() > 0) {
			List<PasSignatura> passes = new ArrayList<PasSignatura>();
			PasSignatura pas = new PasSignatura();
			List<String> signataris = getSignatariIdsPerPersones(personesPas1);
			pas.setSignataris(signataris.toArray(new String[signataris.size()]));
			pas.setMinSignataris(minSignatarisPas1);
			passes.add(pas);
			if (personesPas2 != null && personesPas2.size() > 0) {
				pas = new PasSignatura();
				signataris = getSignatariIdsPerPersones(personesPas2);
				pas.setSignataris(signataris.toArray(new String[signataris.size()]));
				pas.setMinSignataris(minSignatarisPas2);
				passes.add(pas);
			}
			if (personesPas3 != null && personesPas3.size() > 0) {
				pas = new PasSignatura();
				signataris = getSignatariIdsPerPersones(personesPas3);
				pas.setSignataris(signataris.toArray(new String[signataris.size()]));
				pas.setMinSignataris(minSignatarisPas3);
				passes.add(pas);
			}
			return passes.toArray(new PasSignatura[passes.size()]);
		} else if (signatariId != null) {
			PasSignatura[] passes = new PasSignatura[1];
			PasSignatura pas = new PasSignatura();
			pas.setMinSignataris(1);
			pas.setSignataris(new String[] {signatariId});
			passes[0] = pas;
			return passes;
		} else {
			PasSignatura[] passes = new PasSignatura[0];
			return passes;
		}
	}
	private List<String> getSignatariIdsPerPersones(List<PersonaDto> persones) {
		List<String> signatariIds = new ArrayList<String>();
		for (PersonaDto persona: persones) {
			String signatariId = getSignatariIdPerPersona(persona);
			if (signatariId != null)
				signatariIds.add(signatariId);
		}
		return signatariIds;
	}
	private String getSignatariIdPerPersona(PersonaDto persona) {
		if (persona == null)
			return null;
		String signatariId = persona.getDni();
		if (isIdUsuariPerCodi())
			signatariId = persona.getCodi();
		if (isIdUsuariPerDni())
			signatariId = persona.getDni();
		return signatariId;
	}

	private String getDescripcioErrorRegistre(
			RegistreAnotacioDto anotacio) {
		return "organCodi=" + anotacio.getOrganCodi() + ", " +
				"oficinaCodi=" + anotacio.getOficinaCodi() + ", " +
				"entitatCodi=" + anotacio.getEntitatCodi() + ", " +
				"unitatAdministrativa=" + anotacio.getUnitatAdministrativa() + ", " +
				"unitatAdministrativa=" + anotacio.getAssumpteExtracte() + ", " +
				"unitatAdministrativa=" + anotacio.getUnitatAdministrativa() + ", " +
				"annexos=" + ((anotacio.getAnnexos() != null) ? anotacio.getAnnexos().size() : 0);
	}

	private boolean isIdUsuariPerDni() {
		return "dni".equalsIgnoreCase(GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.usuari.id"));
	}
	private boolean isIdUsuariPerCodi() {
		return "codi".equalsIgnoreCase(GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.usuari.id"));
	}

	private boolean gestionDocumentalIsTipusExpedientNou() {
		return "true".equalsIgnoreCase(
				GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.tipus.nou"));
	}

	private boolean gestionDocumentalIsTipusExpedientDirecte() {
		return "true".equalsIgnoreCase(
				GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.tipus.directe"));
	}

	private PersonesPlugin getPersonesPlugin() {
		if (!personesPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.persones.plugin.class");
			if (pluginClass != null) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					personesPlugin = (PersonesPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw SistemaExternException.tractarSistemaExternException(
							null,
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							"No s'ha pogut instanciar el plugin de persones (class=" + pluginClass + ")", 
							ex);
				}
			}
			personesPluginEvaluat = true;
		}
		return personesPlugin;
	}
	private TramitacioPlugin getTramitacioPlugin() {
		if (!tramitacioPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.class");
			if (pluginClass == null) {
				String bantelUrl = GlobalProperties.getInstance().getProperty("app.bantel.entrades.url");
				if (bantelUrl.contains("v1")) {
					pluginClass = "net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginSistrav1";
				} else {
					pluginClass = "net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginSistrav2";
				}
			}
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					tramitacioPlugin = (TramitacioPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw SistemaExternException.tractarSistemaExternException(
							null,
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null,
							"No s'ha pogut instanciar el plugin de tramitació (class=" + pluginClass + ")",
							ex);
				}
			}
		}
		return tramitacioPlugin;
	}
	private RegistrePlugin getRegistrePlugin() {
		if (!registrePluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.registre.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					registrePlugin = (RegistrePlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw SistemaExternException.tractarSistemaExternException(
							null,
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null,
							"No s'ha pogut instanciar el plugin de registre (class=" + pluginClass + ")",
							ex);
				}
			}
		}
		return registrePlugin;
	}
	
	private RegistrePluginRegWeb3 getRegistrePluginRebWeb3() {
		if (!registrePluginRegWeb3Evaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.registre.plugin.rw3.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					registrePluginRegWeb3 = (RegistrePluginRegWeb3)clazz.newInstance();
				} catch (Exception ex) {
					throw SistemaExternException.tractarSistemaExternException(
							null,
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null,
							"No s'ha pogut instanciar el plugin de registre (class=" + pluginClass + ")",
							ex);
				}
			}
		}
		return registrePluginRegWeb3;
	}
	
	private GestioDocumentalPlugin getGestioDocumentalPlugin() {
		if (!gestioDocumentalPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					gestioDocumentalPlugin = (GestioDocumentalPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw SistemaExternException.tractarSistemaExternException(
							null,
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null,
							"No s'ha pogut instanciar el plugin de gestió documental (class=" + pluginClass + ")",
							ex);
				}
			}
		}
		return gestioDocumentalPlugin;
	}

	private PortasignaturesPlugin getPortasignaturesPlugin() {
		if (!portasignaturesPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.class");
			if ((pluginClass != null) && (pluginClass.length() > 0)) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					portasignaturesPlugin = (PortasignaturesPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw SistemaExternException.tractarSistemaExternException(
							null,
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null,
							"No s'ha pogut instanciar el plugin de portafirmes (class=" + pluginClass + ")",
							ex);
				}
			}
		}
		return portasignaturesPlugin;
	}
	private CustodiaPlugin getCustodiaPlugin() {
		if (!custodiaPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.custodia.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					custodiaPlugin = (CustodiaPlugin)clazz.newInstance();
					custodiaPluginEvaluat = true;
				} catch (Exception ex) {
					throw SistemaExternException.tractarSistemaExternException(
							null,
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null,
							"No s'ha pogut instanciar el plugin de custòdia (class=" + pluginClass + ")",
							ex);
				}
			}
		}
		return custodiaPlugin;
	}
	private SignaturaPlugin getSignaturaPlugin() {
		if (!signaturaPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.signatura.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					signaturaPlugin = (SignaturaPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw SistemaExternException.tractarSistemaExternException(
							null,
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null, 
							null,
							"No s'ha pogut instanciar el plugin de signatura (class=" + pluginClass + ")",
							ex);
				}
			}
		}
		return signaturaPlugin;
	}

	private static final Log logger = LogFactory.getLog(PluginHelper.class);
}
