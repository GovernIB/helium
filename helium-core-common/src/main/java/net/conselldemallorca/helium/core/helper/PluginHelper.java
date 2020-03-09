/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fundaciobit.plugins.validatesignature.api.CertificateInfo;
import org.fundaciobit.plugins.validatesignature.api.IValidateSignaturePlugin;
import org.fundaciobit.plugins.validatesignature.api.SignatureDetailInfo;
import org.fundaciobit.plugins.validatesignature.api.SignatureRequestedInformation;
import org.fundaciobit.plugins.validatesignature.api.TimeStampInfo;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureRequest;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentExtensio;
import es.caib.plugins.arxiu.api.DocumentFormat;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.DocumentTipus;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaPerfil;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
import net.conselldemallorca.helium.core.model.hibernate.DocumentNotificacio;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.Transicio;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.custodia.CustodiaPlugin;
import net.conselldemallorca.helium.integracio.plugins.custodia.CustodiaPluginException;
import net.conselldemallorca.helium.integracio.plugins.firma.FirmaPlugin;
import net.conselldemallorca.helium.integracio.plugins.gesdoc.GestioDocumentalPlugin;
import net.conselldemallorca.helium.integracio.plugins.notificacio.Notificacio;
import net.conselldemallorca.helium.integracio.plugins.notificacio.NotificacioPlugin;
import net.conselldemallorca.helium.integracio.plugins.notificacio.RespostaConsultaEstatEnviament;
import net.conselldemallorca.helium.integracio.plugins.notificacio.RespostaConsultaEstatNotificacio;
import net.conselldemallorca.helium.integracio.plugins.notificacio.RespostaEnviar;
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
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreInteressatDocumentTipusEnum;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreInteressatTipusEnum;
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
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaPerfilEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoFirmaEnumDto;
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
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
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
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private MonitorIntegracioHelper monitorIntegracioHelper;
	@Resource
	private DocumentHelperV3 documentHelperV3;
	@Resource
	private NotificacioHelper notificacioElectronicaHelper;
	@Resource
	private UsuariActualHelper usuariActualHelper;

	private PersonesPlugin personesPlugin;
	private TramitacioPlugin tramitacioPlugin;
	private RegistrePlugin registrePlugin;
	private GestioDocumentalPlugin gestioDocumentalPlugin;
	private RegistrePluginRegWeb3 registrePluginRegWeb3;
	private PortasignaturesPlugin portasignaturesPlugin;
	private CustodiaPlugin custodiaPlugin;
	private SignaturaPlugin signaturaPlugin;
	private FirmaPlugin firmaPlugin;
	private IArxiuPlugin arxiuPlugin;
	private NotificacioPlugin notificacioPlugin;
	private IValidateSignaturePlugin validaSignaturaPlugin;



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
				if (dadesPersona == null) {
					throw new NoTrobatException(DadesPersona.class, codi);
				}
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
		String pluginClass = GlobalProperties.getInstance().getProperty("app.persones.plugin.class");
		return pluginClass != null && !pluginClass.isEmpty();
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
			RespostaAnotacioRegistre resposta = getRegistrePluginRegWeb3().registrarSortida(
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
			RespostaConsultaRegistre respostaConsultaRegistre = getRegistrePluginRegWeb3().obtenirRegistreSortida(
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
		String pluginClass = GlobalProperties.getInstance().getProperty("app.registre.plugin.class");
		return pluginClass != null && !pluginClass.isEmpty();
	}
	
	public boolean registreIsPluginRebWeb3Actiu() {
		String pluginClass = GlobalProperties.getInstance().getProperty("app.registre.plugin.rw3.class");
		return pluginClass != null && !pluginClass.isEmpty();
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
		String pluginClass = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.class");
		return pluginClass != null && !pluginClass.isEmpty();
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
					"destinatari",
					getSignatariIdPerPersona(persona));
			parametres[6] = new IntegracioParametreDto(
					"destinatariPas1",
					personestoString(personesPas1));
			parametres[7] = new IntegracioParametreDto(
					"destinatariPas2",
					personestoString(personesPas2));
			parametres[8] = new IntegracioParametreDto(
					"destinatari3",
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
					this.getRemitentNom(expedient.getIdentificador()),
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

	/** Llargada màxima acceptada pel WS del Portasignatures pel camp del remitent. */
	private static final int PORTASIGNATURES_REMITENT_MAX_LENGTH = 100;
	/** Mètode per obtenir el nom del remitent per l'enviament al portasignatures.
	 * 
	 * @return Retorna el nom de l'usuari actual, el codi o "Helium" en cas que no es trobi.
	 */
	private String getRemitentNom(String expedient) {
		String remitent = "Helium. " + expedient;
		try {
			String codiUsuari = usuariActualHelper.getUsuariActual();
			if (codiUsuari != null) {
				PersonaDto persona = this.personaFindAmbCodi(codiUsuari);
				if (persona != null && persona.getNomSencer() != null && !"".equals(persona.getNomSencer())) {
					remitent = persona.getNomSencer();
				} else {
					remitent = codiUsuari;
				}
			}
		} catch(Exception e) {
			logger.error("Error consultant el nom per l'usuari actual:" + e.getMessage(), e);
		}
		return StringUtils.abbreviate(remitent, PORTASIGNATURES_REMITENT_MAX_LENGTH);
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
		String pluginClass = GlobalProperties.getInstance().getProperty("app.custodia.plugin.class");
		return pluginClass != null && !pluginClass.isEmpty();
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

	public byte[] firmaServidor(
			Expedient expedient,
			DocumentStore documentStore,
			ArxiuDto arxiu,
			net.conselldemallorca.helium.integracio.plugins.firma.FirmaTipus firmaTipus,
			String motiu) {
		String accioDescripcio = "Firma en servidor de document";
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"expedientIdentificador",
						expedient.getIdentificador()),
				new IntegracioParametreDto(
						"expedientNumero",
						expedient.getNumero()),
				new IntegracioParametreDto(
						"expedientTipusId",
						expedient.getTipus().getId()),
				new IntegracioParametreDto(
						"expedientTipusCodi",
						expedient.getTipus().getCodi()),
				new IntegracioParametreDto(
						"expedientTipusNom",
						expedient.getTipus().getNom()),
				new IntegracioParametreDto(
						"documentCodi",
						documentStore.getCodiDocument()),
				new IntegracioParametreDto(
						"arxiuNom",
						arxiu.getNom()),
				new IntegracioParametreDto(
						"arxiuTamany",
						arxiu.getTamany())
		};
		long t0 = System.currentTimeMillis();
		try {
			return getFirmaPlugin().firmar(
					firmaTipus,
					motiu,
					arxiu.getNom(),
					arxiu.getContingut());
		} catch (Exception ex) {
			String errorDescripcio = "No s'han pogut firmar el document: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_FIRMA_SERV,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
		
	}

	public ContingutArxiu arxiuExpedientCrear(
			Expedient expedient) {
		String accioDescripcio = "Creació d'expedient";
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"identificador",
						expedient.getIdentificador()),
				new IntegracioParametreDto(
						"numero",
						expedient.getNumero()),
				new IntegracioParametreDto(
						"expedientTipusId",
						expedient.getTipus().getId()),
				new IntegracioParametreDto(
						"expedientTipusCodi",
						expedient.getTipus().getCodi()),
				new IntegracioParametreDto(
						"expedientTipusNom",
						expedient.getTipus().getNom())
		};
		long t0 = System.currentTimeMillis();
		try {
			ContingutArxiu expedientCreat = getArxiuPlugin().expedientCrear(
					toArxiuExpedient(
							expedient.getIdentificador(),
							Arrays.asList(obtenirNtiOrgano(expedient)),
							expedient.getDataInici(),
							obtenirNtiClasificacion(expedient),
							false,
							null,
							obtenirNtiSerieDocumental(expedient),
							expedient.getArxiuUuid()));
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
			return expedientCreat;
		} catch (Exception ex) {
			String errorDescripcio = "No s'han pogut crear l'expedient a l'arxiu: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
	}
	
	public void arxiuExpedientModificar(
			Expedient expedient) {
		String accioDescripcio = "Modificació d'expedient";
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"identificador",
						expedient.getIdentificador()),
				new IntegracioParametreDto(
						"numero",
						expedient.getNumero()),
				new IntegracioParametreDto(
						"expedientTipusId",
						expedient.getTipus().getId()),
				new IntegracioParametreDto(
						"expedientTipusCodi",
						expedient.getTipus().getCodi()),
				new IntegracioParametreDto(
						"expedientTipusNom",
						expedient.getTipus().getNom())
		};
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().expedientModificar(
					toArxiuExpedient(
							expedient.getIdentificador(),
							Arrays.asList(obtenirNtiOrgano(expedient)),
							expedient.getDataInici(),
							obtenirNtiClasificacion(expedient),
							expedient.getDataFi() != null,
							null,
							obtenirNtiSerieDocumental(expedient),
							expedient.getArxiuUuid()));
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
		} catch (Exception ex) {
			String errorDescripcio = "No s'han pogut modificar l'expedient a l'arxiu: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
	}
	
	public void arxiuExpedientTancar(
			String arxiuUuid) {
		String accioDescripcio = "Tancar l'expedient";
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"arxiuUuid",
						arxiuUuid)
		};
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().expedientTancar(arxiuUuid);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut tancar l'expedient a l'arxiu: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
	}
	
	public void arxiuExpedientReobrir(
			String arxiuUuid) {
		String accioDescripcio = "Reobrir l'expedient tancat";
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"arxiuUuid",
						arxiuUuid)
		};
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().expedientReobrir(arxiuUuid);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut reobrir l'expedient a l'arxiu: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
	}

	public void arxiuExpedientEsborrar(
			String arxiuUuid) {
		String accioDescripcio = "Esborrat d'expedient";
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"arxiuUuid",
						arxiuUuid)
		};
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().expedientEsborrar(arxiuUuid);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut esborrar l'expedient a l'arxiu: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
	}

	public es.caib.plugins.arxiu.api.Expedient arxiuExpedientInfo(
			String arxiuUuid) {
		String accioDescripcio = "Consulta d'informació de l'expedient";
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"arxiuUuid",
						arxiuUuid)
		};
		long t0 = System.currentTimeMillis();
		try {
			es.caib.plugins.arxiu.api.Expedient exp = getArxiuPlugin().expedientDetalls(arxiuUuid, null);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
			return exp;
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut consultar la informació de l'expedient: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
	}
	
	public boolean arxiuExisteixExpedient(String arxiuUuid) {
		try {
			es.caib.plugins.arxiu.api.Expedient exp = getArxiuPlugin().expedientDetalls(arxiuUuid, null);
			return exp != null;
		} catch (Exception ex) {
			return false;
		}
	}

	public ContingutArxiu arxiuDocumentCrearActualitzar(
			Expedient expedient,
			String documentNom,
			DocumentStore documentStore,
			ArxiuDto arxiu) {
		return arxiuDocumentCrearActualitzar(
			expedient,
			documentNom,
			documentStore,
			arxiu,
			false,
			false,
			null);
	}
	
	public ContingutArxiu arxiuDocumentCrearActualitzar(
			Expedient expedient,
			String documentNom,
			DocumentStore documentStore,
			ArxiuDto arxiu,
			boolean ambFirma,
			boolean firmaSeparada,
			List<ArxiuFirmaDto> firmes) {
		String accioDescripcio;
		if (documentStore.getArxiuUuid() == null) {
			accioDescripcio = "Creació de document";
		} else {
			accioDescripcio = "Modificació de document";
		}
		List<IntegracioParametreDto> parametres = new ArrayList<IntegracioParametreDto>();
		parametres.add(
				new IntegracioParametreDto(
						"expedient",
						expedient.getIdentificador()));
		parametres.add(
				new IntegracioParametreDto(
						"documentNom",
						documentNom));
		parametres.add(
				new IntegracioParametreDto(
						"arxiuNom",
						arxiu.getNom()));
		parametres.add(
				new IntegracioParametreDto(
						"arxiuTamany",
						arxiu.getTamany()));
		long t0 = System.currentTimeMillis();
		try {
			String nomAmbExtensio = documentNom + "." + arxiu.getExtensio();
			ContingutArxiu documentPerRetornar;
			if (documentStore.getArxiuUuid() == null) {
				documentPerRetornar = getArxiuPlugin().documentCrear(
						toArxiuDocument(
								null,
								nomAmbExtensio,
								arxiu,
								ambFirma,
								firmaSeparada,
								firmes,
								null,
								obtenirNtiOrigen(documentStore),
								Arrays.asList(obtenirNtiOrgano(expedient)),
								documentStore.getDataCreacio(),
								obtenirNtiEstadoElaboracion(documentStore),
								obtenirNtiTipoDocumental(documentStore),
								documentStore.getNtiIdDocumentoOrigen(),
								getExtensioPerArxiu(arxiu),
								(firmes != null ? DocumentEstat.DEFINITIU : DocumentEstat.ESBORRANY)),
						expedient.getArxiuUuid());
			} else {
				documentPerRetornar = getArxiuPlugin().documentModificar(
						toArxiuDocument(
								documentStore.getArxiuUuid(),
								nomAmbExtensio,
								arxiu,
								ambFirma,
								firmaSeparada,
								firmes,
								null,
								obtenirNtiOrigen(documentStore),
								Arrays.asList(obtenirNtiOrgano(expedient)),
								documentStore.getDataCreacio(),
								obtenirNtiEstadoElaboracion(documentStore),
								obtenirNtiTipoDocumental(documentStore),
								documentStore.getNtiIdDocumentoOrigen(),
								getExtensioPerArxiu(arxiu),
								(firmes != null ? DocumentEstat.DEFINITIU : DocumentEstat.ESBORRANY)));
			}
			// Paràmetres de resposta
			parametres.add(
					new IntegracioParametreDto(
							"resposta",
							documentPerRetornar != null));
			new IntegracioParametreDto(
					"respostaIdentificador",
					(documentPerRetornar != null ? documentPerRetornar.getIdentificador() : "-"));
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres.toArray(new IntegracioParametreDto[parametres.size()]));
			return documentPerRetornar;
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut actualitzar la informació del document: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres.toArray(new IntegracioParametreDto[parametres.size()]));
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
	}

	public ContingutArxiu arxiuDocumentGuardarPdfFirmat(
			Expedient expedient,
			DocumentStore documentStore,
			String documentNom,
			ArxiuDto firmaPdf) {
		String accioDescripcio = "Guardar PDF firmat com a document definitiu";
		List<IntegracioParametreDto> parametres = new ArrayList<IntegracioParametreDto>();
		parametres.add(
				new IntegracioParametreDto(
						"id",
						documentStore.getId().toString()));
		parametres.add(
				new IntegracioParametreDto(
						"documentNom",
						documentNom));
		parametres.add(
				new IntegracioParametreDto(
						"firmaPdfFitxerNom",
						firmaPdf.getNom()));
		parametres.add(
				new IntegracioParametreDto(
						"firmaPdfFitxerTipusMime",
						firmaPdf.getTipusMime()));
		parametres.add(
				new IntegracioParametreDto(
						"firmaPdfFitxerTamany",
						new Long(firmaPdf.getTamany()).toString()));
		long t0 = System.currentTimeMillis();
		try {
			ContingutArxiu documentPerRetornar = getArxiuPlugin().documentModificar(
					toArxiuDocument(
							documentStore.getArxiuUuid(),
							documentNom + ".pdf",
							null,
							firmaPdf,
							FirmaTipus.PADES,
							FirmaPerfil.EPES,
							null,
							obtenirNtiOrigen(documentStore),
							Arrays.asList(obtenirNtiOrgano(expedient)),
							documentStore.getDataCreacio(),
							obtenirNtiEstadoElaboracion(documentStore),
							obtenirNtiTipoDocumental(documentStore),
							documentStore.getNtiIdDocumentoOrigen(),
							DocumentExtensio.PDF,
							DocumentEstat.DEFINITIU));
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres.toArray(new IntegracioParametreDto[parametres.size()]));
			return documentPerRetornar;
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut guardar el PDF firmat com a document definitiu: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres.toArray(new IntegracioParametreDto[parametres.size()]));
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
	}

	public ContingutArxiu arxiuDocumentGuardarFirmaCadesDetached(
			Expedient expedient,
			DocumentStore documentStore,
			String documentNom,
			ArxiuDto firmaCades) {
		String accioDescripcio = "Guardar firma CAdES pel document";
		List<IntegracioParametreDto> parametres = new ArrayList<IntegracioParametreDto>();
		parametres.add(
				new IntegracioParametreDto(
						"id",
						documentStore.getId().toString()));
		parametres.add(
				new IntegracioParametreDto(
						"documentNom",
						documentNom));
		parametres.add(
				new IntegracioParametreDto(
						"firmaCadesFitxerNom",
						firmaCades.getNom()));
		parametres.add(
				new IntegracioParametreDto(
						"firmaCadesFitxerTipusMime",
						firmaCades.getTipusMime()));
		parametres.add(
				new IntegracioParametreDto(
						"firmaCadesFitxerTamany",
						new Long(firmaCades.getTamany()).toString()));
		long t0 = System.currentTimeMillis();
		try {
			ArxiuDto arxiu = new ArxiuDto();
			arxiu.setNom(documentStore.getArxiuNom());
			ContingutArxiu documentPerRetornar = getArxiuPlugin().documentModificar(
					toArxiuDocument(
							documentStore.getArxiuUuid(),
							documentNom + "." + arxiu.getExtensio(),
							null,
							firmaCades,
							FirmaTipus.CADES_DET,
							FirmaPerfil.BES,
							null,
							obtenirNtiOrigen(documentStore),
							Arrays.asList(obtenirNtiOrgano(expedient)),
							documentStore.getDataCreacio(),
							obtenirNtiEstadoElaboracion(documentStore),
							obtenirNtiTipoDocumental(documentStore),
							documentStore.getNtiIdDocumentoOrigen(),
							getExtensioPerArxiu(arxiu),
							DocumentEstat.DEFINITIU));
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres.toArray(new IntegracioParametreDto[parametres.size()]));
			return documentPerRetornar;
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut guardar la firma CAdES pel document: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres.toArray(new IntegracioParametreDto[parametres.size()]));
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
	}

	public es.caib.plugins.arxiu.api.Document arxiuDocumentInfo(
			String arxiuUuid,
			String versio,
			boolean ambContingut,
			boolean isSignat) {
		String accioDescripcio = "Consulta d'un document";
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"arxiuUuid",
						arxiuUuid),
				new IntegracioParametreDto(
						"versio",
						versio),
				new IntegracioParametreDto(
						"ambContingut",
						ambContingut)
		};
		long t0 = System.currentTimeMillis();
		try {
			es.caib.plugins.arxiu.api.Document documentDetalls = getArxiuPlugin().documentDetalls(
					arxiuUuid,
					versio,
					ambContingut);
			if (ambContingut) {
				boolean isFirmaPades = false;
				if (isSignat && documentDetalls.getFirmes() != null) {
					for (Firma firma: documentDetalls.getFirmes()) {
						if (FirmaTipus.PADES.equals(firma.getTipus())) {
							isFirmaPades = true;
							break;
						}
					}
				}
				if (isFirmaPades) {
					DocumentContingut documentContingut = getArxiuPlugin().documentImprimible(
							arxiuUuid);
					if (documentContingut != null && documentContingut.getContingut() != null) {
						documentDetalls.getContingut().setContingut(
								documentContingut.getContingut());
						documentDetalls.getContingut().setTamany(
								documentContingut.getContingut().length);
						documentDetalls.getContingut().setTipusMime("application/pdf");
					}
				}
			}
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
			return documentDetalls;
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut consultar la informació del document: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
	}

	public void arxiuDocumentEsborrar(
			String arxiuUuid) {
		String accioDescripcio = "Consulta d'un document";
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"arxiuUuid",
						arxiuUuid)
		};
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().documentEsborrar(arxiuUuid);
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut esborrar el document: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
	}
	
	
	public RespostaEnviar altaNotificacio(Expedient expedient, DadesNotificacioDto dadesNotificacio) {
		
		RespostaEnviar resposta;
		
		String accioDescripcio = "Alta de notificació";
		String nifDestinataris = "";
		for (DadesEnviamentDto dadesEnviament: dadesNotificacio.getEnviaments()) {
			nifDestinataris += dadesEnviament.getTitular().getDni() + ", ";
		}
		if (!nifDestinataris.isEmpty())
			nifDestinataris = nifDestinataris.substring(0, nifDestinataris.length() - 2);
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto(
						"expedient.id",
						expedient.getId()),
				new IntegracioParametreDto(
						"expedient",
						expedient.getIdentificadorLimitat()),
				new IntegracioParametreDto(
						"documentArxiuNom",
						dadesNotificacio.getDocumentArxiuNom()),
				new IntegracioParametreDto(
						"titularsNif",
						nifDestinataris)
		};
		long t0 = System.currentTimeMillis();
		
		try {
			Notificacio notificacio = conversioTipusHelper.convertir(dadesNotificacio, Notificacio.class);
			// Informa de l'estat actual
			notificacio.setUsuariCodi(usuariActualHelper.getUsuariActual());		
			// Invoca el servei
			resposta = getNotificacioPlugin().enviar(notificacio);
			
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_NOTIB,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut enviar l'alta de notificació: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_NOTIB,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
		return resposta;
	}
	
	public void notificacioActualitzarEstat(
			DocumentNotificacio notificacio) {
		Expedient expedient = notificacio.getExpedient();
		
		String accioDescripcio = "Consulta d'estat d'una notificació electrònica";
		
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto("expedientId", expedient.getId()),
				new IntegracioParametreDto("expedientTitol", expedient.getTitol()),
				new IntegracioParametreDto("expedientTipusId", expedient.getTipus().getId()),
				new IntegracioParametreDto("identificador", notificacio.getEnviamentIdentificador()),
				new IntegracioParametreDto("referencia", notificacio.getEnviamentReferencia())
		};
		long t0 = System.currentTimeMillis();
		
		try {
			RespostaConsultaEstatNotificacio resposta = getNotificacioPlugin().consultarNotificacio(notificacio.getEnviamentIdentificador());
			// Revisa que no sigui una resposta amb error de consulta
			if (resposta.getEstat() == null && resposta.isError()) {
				throw new net.conselldemallorca.helium.integracio.plugins.SistemaExternException("Resposta d'error en de la consulta de la notificació \"" + notificacio.getEnviamentIdentificador() + "\" al NOTIB: resposta.errorDescripcio= " + resposta.getErrorDescripcio());
			}

			if (resposta.getEstat() != null) {
				switch(resposta.getEstat()){
					case ENVIADA:
						notificacio.setEstat(NotificacioEstatEnumDto.ENVIADA);
						break;
					case FINALITZADA:
						notificacio.setEstat(NotificacioEstatEnumDto.FINALITZADA);
						break;
					case PENDENT:
						notificacio.setEstat(NotificacioEstatEnumDto.PENDENT);
						break;
					case PROCESSADA:
						notificacio.setEstat(NotificacioEstatEnumDto.PROCESSADA);
						break;
					case REGISTRADA:
						notificacio.setEstat(NotificacioEstatEnumDto.REGISTRADA);
						break;
				}
			} 
			notificacio.setError(resposta.isError());
			notificacio.setErrorDescripcio(resposta.getErrorDescripcio());
					
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_NOTIB,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					parametres);
			
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut consultar l'estat de la notificació amb identificador " + notificacio.getEnviamentIdentificador() + ": " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_NOTIB,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
	}
	
	public void notificacioActualitzarEstatEnviament(
			DocumentNotificacio notificacio) {
		Expedient expedient = notificacio.getExpedient();
		DocumentStore document = notificacio.getDocument();
		DocumentStore certificacio = notificacio.getEnviamentCertificacio();
		
		String accioDescripcio = "Consulta d'estat de l'enviament d'una notificació electrònica";
		
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto("expedientId", expedient.getId()),
				new IntegracioParametreDto("expedientTitol", expedient.getTitol()),
				new IntegracioParametreDto("expedientTipusId", expedient.getTipus().getId()),
				new IntegracioParametreDto("identificador", notificacio.getEnviamentIdentificador()),
				new IntegracioParametreDto("referencia", notificacio.getEnviamentReferencia())
		};
		long t0 = System.currentTimeMillis();
		
		try {
			RespostaConsultaEstatEnviament resposta = getNotificacioPlugin().consultarEnviament(notificacio.getEnviamentReferencia());
			// Revisa que no sigui una resposta amb error de consulta
			if (resposta.getEstat() == null && resposta.isError()) {
				throw new net.conselldemallorca.helium.integracio.plugins.SistemaExternException("Resposta d'error en de la consulta de l'enviament \"" + notificacio.getEnviamentReferencia() + "\" al NOTIB: resposta.errorDescripcio= " + resposta.getErrorDescripcio());
			}
			Long gestioDocumentalId = certificacio != null ? certificacio.getId() : null;
			if (resposta.getCertificacioData() != null) {
				byte[] certificacioContingut = resposta.getCertificacioContingut();
				// Cetificació Títol: [títol document notificat] - Justificant [NIF interessat]
				ExpedientDocumentDto expedientDocument = documentHelperV3.findDocumentPerDocumentStoreId(document.getProcessInstanceId(), document.getId());
				String certificacioTitol = expedientDocument.getDocumentNom() + " - Justificant " + notificacio.getTitularNif();
				String certificacioArxiuExtensio = resposta.getCertificacioTipusMime() != null 
													&& resposta.getCertificacioTipusMime().toLowerCase().contains("xml") ?
															  "xml"
															: "pdf";
				String certificacioArxiuNom = "certificacio_" + notificacio.getEnviamentReferencia() + "." + certificacioArxiuExtensio;				
				if (gestioDocumentalId != null && notificacio.getEnviamentCertificacioData().before(DateUtils.truncate(resposta.getCertificacioData(), Calendar.SECOND))) {
					gestioDocumentalId = documentHelperV3.actualitzarDocument(
							gestioDocumentalId, 
							null, 
							expedient.getProcessInstanceId(), 
							resposta.getCertificacioData(), 
							certificacioTitol, 
							certificacioArxiuNom,  
							certificacioContingut, 
							NtiOrigenEnumDto.ADMINISTRACIO, 
							NtiEstadoElaboracionEnumDto.ALTRES, 
							NtiTipoDocumentalEnumDto.CERTIFICAT, 
							null);
				} else if (gestioDocumentalId == null) {
					gestioDocumentalId = documentHelperV3.crearDocument(
							null, 
							expedient.getProcessInstanceId(), 
							null, 
							resposta.getCertificacioData(), 
							true, 
							certificacioTitol, 
							certificacioArxiuNom, 
							certificacioContingut, 
							NtiOrigenEnumDto.ADMINISTRACIO, 
							NtiEstadoElaboracionEnumDto.ALTRES, 
							NtiTipoDocumentalEnumDto.CERTIFICAT, 
							null);
				}
			}
			
			if (resposta.getEstat() != null) {
				notificacio.updateEnviamentEstat(
						resposta.getEstat(),
						resposta.getEstatData(),
						resposta.getEstatOrigen(),
						resposta.getCertificacioData(),
						resposta.getCertificacioOrigen(),
						gestioDocumentalId != null ? documentStoreRepository.findOne(gestioDocumentalId) : null,
						resposta.isError(),
						resposta.getErrorDescripcio());
			} else {
				throw new Exception("L'estat de la resposta és null i no es pot tractar.");
			}
					
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_NOTIB,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					parametres);
			
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut consultar l'estat de la notificació amb referència " + notificacio.getEnviamentReferencia() + ": " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_NOTIB,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
	}
	
	// NOTIB -- Fi

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
	
	/** Empra el plugin de validació de firmes d'@firma per obtenir la informació de les firmes
	 * del document passat com a paràmetre. 
	 * 
	 * @param documentStore
	 * 			Informació del document per poder posar informació al monitor d'integracions.
	 * @param documentContingut
	 * 			Contingut del document firmat. Si és attached llavors conté les pròpies firmes.
	 * @param firmaContingut
	 * 			Contingut de la firma en cas que sigui dettached.
	 * @param contentType
	 * 			Tipus MIME 
	 * @return
	 */
	public List<ArxiuFirmaDto> validaSignaturaObtenirFirmes(
			DocumentStore documentStore, 
			byte[] documentContingut,
			byte[] firmaContingut,
			String contentType) {
		String accioDescripcio = "Obtenir informació de document firmat";
		Map<String, Object> accioParams = new HashMap<String, Object>();
		accioParams.put("documentStore", documentStore != null ? 
											"[id=" + documentStore.getId() + 
											", codi=" + documentStore.getCodiDocument() +
											", nom=" + documentStore.getArxiuNom() + "]"
											: "null" );
		accioParams.put("documentContingut.length", documentContingut != null? documentContingut.length : -1);
		accioParams.put("firmaContingut.length", firmaContingut != null? firmaContingut.length : -1);
		accioParams.put("contentType", contentType);		
		long t0 = System.currentTimeMillis();
		try {
			ValidateSignatureRequest validationRequest = new ValidateSignatureRequest();
			if (firmaContingut != null) {
				validationRequest.setSignedDocumentData(documentContingut);
				validationRequest.setSignatureData(firmaContingut);
			} else {
				validationRequest.setSignatureData(documentContingut);
			}
			SignatureRequestedInformation sri = new SignatureRequestedInformation();
			sri.setReturnSignatureTypeFormatProfile(true);
			sri.setReturnCertificateInfo(true);
			sri.setReturnValidationChecks(false);
			sri.setValidateCertificateRevocation(false);
			sri.setReturnCertificates(false);
			sri.setReturnTimeStampInfo(false);
			validationRequest.setSignatureRequestedInformation(sri);
			ValidateSignatureResponse validateSignatureResponse = getValidaSignaturaPlugin().validateSignature(validationRequest);
			List<ArxiuFirmaDetallDto> detalls = new ArrayList<ArxiuFirmaDetallDto>();
			List<ArxiuFirmaDto> firmes = new ArrayList<ArxiuFirmaDto>();
			ArxiuFirmaDto firma = new ArxiuFirmaDto();
			if (validateSignatureResponse.getSignatureDetailInfo() != null) {
				for (SignatureDetailInfo signatureInfo: validateSignatureResponse.getSignatureDetailInfo()) {
					ArxiuFirmaDetallDto detall = new ArxiuFirmaDetallDto();
					TimeStampInfo timeStampInfo = signatureInfo.getTimeStampInfo();
					if (timeStampInfo != null) {
						detall.setData(timeStampInfo.getCreationTime());
					} else {
						detall.setData(signatureInfo.getSignDate());
					}
					CertificateInfo certificateInfo = signatureInfo.getCertificateInfo();
					if (certificateInfo != null) {
						detall.setResponsableNif(certificateInfo.getNifResponsable());
						detall.setResponsableNom(certificateInfo.getNombreApellidosResponsable());
						detall.setEmissorCertificat(certificateInfo.getOrganizacionEmisora());
					}
					detalls.add(detall);
				}
				firma.setAutofirma(false);
				if (firmaContingut != null) {
					firma.setContingut(firmaContingut);
				} else {
					firma.setContingut(documentContingut);
				}
				firma.setDetalls(detalls);
				firma.setPerfil(toArxiuFirmaPerfilEnum(validateSignatureResponse.getSignProfile()));
				firma.setTipus(toArxiuFirmaTipusEnum(
						validateSignatureResponse.getSignType(),
						validateSignatureResponse.getSignFormat()));
				firma.setTipusMime(contentType);
				firmes.add(firma);
			}			
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_VALIDASIG,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					IntegracioParametreDto.toIntegracioParametres(accioParams));
			
			return firmes;
		} catch (Exception ex) {
			String errorDescripcio = ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_VALIDASIG,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					IntegracioParametreDto.toIntegracioParametres(accioParams));
			throw tractarExcepcioEnSistemaExtern(errorDescripcio, ex);
		}
	}
	
	private ArxiuFirmaPerfilEnumDto toArxiuFirmaPerfilEnum(String perfil) {		
		ArxiuFirmaPerfilEnumDto perfilFirma = null;
		if("AdES-BES".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnumDto.BES;
		} else if("AdES-EPES".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnumDto.EPES;
		} else if("PAdES-LTV".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnumDto.LTV;
		} else if("AdES-T".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnumDto.T;
		} else if("AdES-C".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnumDto.C;
		} else if("AdES-X".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnumDto.X;
		} else if("AdES-XL".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnumDto.XL;
		} else if("AdES-A".equals(perfil)) {
			perfilFirma = ArxiuFirmaPerfilEnumDto.A;
		}
		return perfilFirma;
	}
	
	private NtiTipoFirmaEnumDto toArxiuFirmaTipusEnum(
			String tipus,
			String format) {		
		NtiTipoFirmaEnumDto tipusFirma = null;
		if (tipus.equals("PAdES") || format.equals("implicit_enveloped/attached")) {
			tipusFirma = NtiTipoFirmaEnumDto.PADES;
		} else if (tipus.equals("XAdES") && format.equals("explicit/detached")) {
			tipusFirma = NtiTipoFirmaEnumDto.XADES_DET;
		} else if (tipus.equals("XAdES") && format.equals("implicit_enveloping/attached")) {
			tipusFirma = NtiTipoFirmaEnumDto.XADES_ENV;
		} else if (tipus.equals("CAdES") && format.equals("explicit/detached")) {
			tipusFirma = NtiTipoFirmaEnumDto.CADES_DET;
		} else if (tipus.equals("CAdES") && format.equals("implicit_enveloping/attached")) {
			tipusFirma = NtiTipoFirmaEnumDto.CADES_ATT;
		}
		return tipusFirma;
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
		registreAssentament.setEntitatCodi(anotacio.getEntitatCodi());
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
				document.setTipusDocument(annex.getTipusDocument());
				document.setTipusDocumental(annex.getTipusDocumental());
				document.setOrigen(annex.getOrigen());
				document.setModeFirma(annex.getFirmaMode());
				document.setObservacions(annex.getObservacions());
				document.setValidesa(annex.getValidesa());
				documents.add(document);
			}
			registreAssentament.setDocuments(documents);
		}
		return registreAssentament;
	}
	
	private RegistreAssentamentInteressat toRegistreAssentamentInteressat(RegistreInteressat interessat) {
		RegistreAssentamentInteressat registreAssentamentInteressat = new RegistreAssentamentInteressat();
		registreAssentamentInteressat.setTipus(interessat.getTipus() != null ? RegistreInteressatTipusEnum.valorAsEnum(interessat.getTipus().getValor()) : null);
		registreAssentamentInteressat.setDocumentTipus(interessat.getDocumentTipus() != null ? RegistreInteressatDocumentTipusEnum.valorAsEnum(interessat.getDocumentTipus().getValor()) : null);
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

	private es.caib.plugins.arxiu.api.Expedient toArxiuExpedient(
			String nom,
			List<String> ntiOrgans,
			Date ntiDataObertura,
			String ntiClassificacio,
			boolean expedientFinalitzat,
			List<String> ntiInteressats,
			String serieDocumental,
			String arxiuUuid) {
		es.caib.plugins.arxiu.api.Expedient expedient = new es.caib.plugins.arxiu.api.Expedient();
		expedient.setNom(this.treureCaractersEstranys(nom));
		expedient.setIdentificador(arxiuUuid);
		ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setDataObertura(ntiDataObertura);
		metadades.setClassificacio(ntiClassificacio);
		if (!expedientFinalitzat) {
			metadades.setEstat(ExpedientEstat.OBERT);
		} else {
			metadades.setEstat(ExpedientEstat.TANCAT);
		}
		metadades.setOrgans(ntiOrgans);
		metadades.setInteressats(ntiInteressats);
		metadades.setSerieDocumental(serieDocumental);
		expedient.setMetadades(metadades);
		return expedient;
	}

	private String treureCaractersEstranys(String nom) {
		String nomRevisat;
		if (nom != null)
			nomRevisat = nom.trim().replace("&", "&amp;").replaceAll("[~\"#%*:<\n\r\t>/?/|\\\\ ]", "_");
		else 
			nomRevisat = null;
		return nomRevisat;
	}

	private static List<FirmaTipus> TIPUS_FIRMES_ATTACHED = Arrays.asList(FirmaTipus.CADES_ATT, FirmaTipus.PADES, FirmaTipus.XADES_ENV);

	/** Mètode per obtenir un objecte Document per crear o actualitzar a l'arxiu. */
	private es.caib.plugins.arxiu.api.Document toArxiuDocument(
			String identificador,
			String nom,
			ArxiuDto fitxer,
			ArxiuDto firma,
			FirmaTipus firmaTipus,
			FirmaPerfil firmaPerfil,
			String ntiIdentificador,
			NtiOrigenEnumDto ntiOrigen,
			List<String> ntiOrgans,
			Date ntiDataCaptura,
			NtiEstadoElaboracionEnumDto ntiEstatElaboracio,
			NtiTipoDocumentalEnumDto ntiTipusDocumental,
			String ntiIdDocumentOrigen,
			DocumentExtensio extensio,
			DocumentEstat estat) {
		List<ArxiuFirmaDto> firmes = null;
		if (firma!= null) {
			ArxiuFirmaDto arxiuFirma = new ArxiuFirmaDto();
			arxiuFirma.setAutofirma(false);
			arxiuFirma.setContingut(firma.getContingut());
			arxiuFirma.setFitxerNom(firma.getNom());
			arxiuFirma.setTipusMime(firma.getTipusMime());
			arxiuFirma.setTipus(NtiTipoFirmaEnumDto.valueOf(firmaTipus.name()));
			arxiuFirma.setPerfil(ArxiuFirmaPerfilEnumDto.valueOf(firmaPerfil.name()));
			firmes = Arrays.asList(arxiuFirma);
		}
		return toArxiuDocument(
				identificador,
				nom,
				fitxer,
				firma != null,
				firmaTipus != null && !TIPUS_FIRMES_ATTACHED.contains(firmaTipus),
				firmes, // firmes
				ntiIdentificador,
				ntiOrigen,
				ntiOrgans,
				ntiDataCaptura,
				ntiEstatElaboracio,
				ntiTipusDocumental,
				ntiIdDocumentOrigen,
				extensio,
				estat);
	}
	
	/**  Mètode per obtenir un objecte Document per crear o actualitzar a l'arxiu. Aquest mètode rep la llista de firmes. */
	private es.caib.plugins.arxiu.api.Document toArxiuDocument(
			String identificador,
			String nom,
			ArxiuDto fitxer,
			boolean documentAmbFirma,
			boolean firmaSeparada,
			List<ArxiuFirmaDto> firmes,
			String ntiIdentificador,
			NtiOrigenEnumDto ntiOrigen,
			List<String> ntiOrgans,
			Date ntiDataCaptura,
			NtiEstadoElaboracionEnumDto ntiEstatElaboracio,
			NtiTipoDocumentalEnumDto ntiTipusDocumental,
			String ntiIdDocumentOrigen, 
			DocumentExtensio extensio,
			DocumentEstat estat) {
		es.caib.plugins.arxiu.api.Document document = new es.caib.plugins.arxiu.api.Document();
		document.setNom(nom);
		document.setIdentificador(identificador);
		DocumentMetadades metadades = new DocumentMetadades();
		metadades.setIdentificador(ntiIdentificador);
		if (ntiOrigen != null) {
			switch (ntiOrigen) {
			case CIUTADA:
				metadades.setOrigen(ContingutOrigen.CIUTADA);
				break;
			case ADMINISTRACIO:
				metadades.setOrigen(ContingutOrigen.ADMINISTRACIO);
				break;
			}
		}
		metadades.setDataCaptura(ntiDataCaptura);
		DocumentEstatElaboracio estatElaboracio = null;
		switch (ntiEstatElaboracio) {
		case ORIGINAL:
			estatElaboracio = DocumentEstatElaboracio.ORIGINAL;
			break;
		case COPIA_CF:
			estatElaboracio = DocumentEstatElaboracio.COPIA_CF;
			break;
		case COPIA_DP:
			estatElaboracio = DocumentEstatElaboracio.COPIA_DP;
			break;
		case COPIA_PR:
			estatElaboracio = DocumentEstatElaboracio.COPIA_PR;
			break;
		case ALTRES:
			estatElaboracio = DocumentEstatElaboracio.ALTRES;
			break;
		}
		metadades.setEstatElaboracio(estatElaboracio);
		metadades.setIdentificadorOrigen(ntiIdDocumentOrigen);
		DocumentTipus tipusDocumental = null;
		switch (ntiTipusDocumental) {
		case RESOLUCIO:
			tipusDocumental = DocumentTipus.RESOLUCIO;
			break;
		case ACORD:
			tipusDocumental = DocumentTipus.ACORD;
			break;
		case CONTRACTE:
			tipusDocumental = DocumentTipus.CONTRACTE;
			break;
		case CONVENI:
			tipusDocumental = DocumentTipus.CONVENI;
			break;
		case DECLARACIO:
			tipusDocumental = DocumentTipus.DECLARACIO;
			break;
		case COMUNICACIO:
			tipusDocumental = DocumentTipus.COMUNICACIO;
			break;
		case NOTIFICACIO:
			tipusDocumental = DocumentTipus.NOTIFICACIO;
			break;
		case PUBLICACIO:
			tipusDocumental = DocumentTipus.PUBLICACIO;
			break;
		case JUSTIFICANT_RECEPCIO:
			tipusDocumental = DocumentTipus.JUSTIFICANT_RECEPCIO;
			break;
		case ACTA:
			tipusDocumental = DocumentTipus.ACTA;
			break;
		case CERTIFICAT:
			tipusDocumental = DocumentTipus.CERTIFICAT;
			break;
		case DILIGENCIA:
			tipusDocumental = DocumentTipus.DILIGENCIA;
			break;
		case INFORME:
			tipusDocumental = DocumentTipus.INFORME;
			break;
		case SOLICITUD:
			tipusDocumental = DocumentTipus.SOLICITUD;
			break;
		case DENUNCIA:
			tipusDocumental = DocumentTipus.DENUNCIA;
			break;
		case ALEGACIO:
			tipusDocumental = DocumentTipus.ALEGACIO;
			break;
		case RECURS:
			tipusDocumental = DocumentTipus.RECURS;
			break;
		case COMUNICACIO_CIUTADA:
			tipusDocumental = DocumentTipus.COMUNICACIO_CIUTADA;
			break;
		case FACTURA:
			tipusDocumental = DocumentTipus.FACTURA;
			break;
		case ALTRES_INCAUTATS:
			tipusDocumental = DocumentTipus.ALTRES_INCAUTATS;
			break;
		default:
			tipusDocumental = DocumentTipus.ALTRES;
			break;
		}
		metadades.setTipusDocumental(tipusDocumental);
		// Contingut i firmes
		DocumentContingut contingut = null;
		// Si no està firmat posa el contingut on toca
		if (!documentAmbFirma && fitxer != null) {
			// Sense firma
			contingut = new DocumentContingut();
			contingut.setArxiuNom(fitxer.getNom());
			contingut.setContingut(fitxer.getContingut());
			contingut.setTipusMime(fitxer.getTipusMime());
			document.setContingut(contingut);
		} else {
			// Amb firma
			if (!firmaSeparada) {
				// attached
				Firma firma = new Firma();
				ArxiuFirmaDto primeraFirma;
				if (fitxer != null) {
					firma.setFitxerNom(fitxer.getNom());
					firma.setContingut(fitxer.getContingut());
					firma.setTipusMime(fitxer.getTipusMime());
					if (firmes != null && !firmes.isEmpty()) {
						primeraFirma = firmes.get(0);
						setFirmaTipusPerfil(firma, primeraFirma);
						firma.setCsvRegulacio(primeraFirma.getCsvRegulacio());
					}
				} else if (firmes != null && !firmes.isEmpty()) {
					primeraFirma = firmes.get(0);
					setFirmaTipusPerfil(firma, primeraFirma);
					firma.setFitxerNom(primeraFirma.getFitxerNom());
					firma.setContingut(primeraFirma.getContingut());
					firma.setTipusMime(primeraFirma.getTipusMime());
					firma.setCsvRegulacio(primeraFirma.getCsvRegulacio());
				}
				document.setFirmes(Arrays.asList(firma));
			} else {
				// detached
				contingut = new DocumentContingut();
				contingut.setArxiuNom(fitxer.getNom());
				contingut.setContingut(fitxer.getContingut());
				contingut.setTipusMime(fitxer.getTipusMime());
				document.setContingut(contingut);
				document.setFirmes(new ArrayList<Firma>());
				for (ArxiuFirmaDto firmaDto: firmes) {
					Firma firma = new Firma();
					firma.setFitxerNom(firmaDto.getFitxerNom());
					firma.setContingut(firmaDto.getContingut());
					firma.setTipusMime(firmaDto.getTipusMime());
					setFirmaTipusPerfil(firma, firmaDto);
					firma.setCsvRegulacio(firmaDto.getCsvRegulacio());
					document.getFirmes().add(firma);
				}
			}
		}
		if (extensio != null) {
			metadades.setExtensio(extensio);
			DocumentFormat format = null;
			switch (extensio) {
			case AVI:
				format = DocumentFormat.AVI;
				break;
			case CSS:
				format = DocumentFormat.CSS;
				break;
			case CSV:
				format = DocumentFormat.CSV;
				break;
			case DOCX:
				format = DocumentFormat.SOXML;
				break;
			case GML:
				format = DocumentFormat.GML;
				break;
			case GZ:
				format = DocumentFormat.GZIP;
				break;
			case HTM:
				format = DocumentFormat.XHTML; // HTML o XHTML!!!
				break;
			case HTML:
				format = DocumentFormat.XHTML; // HTML o XHTML!!!
				break;
			case JPEG:
				format = DocumentFormat.JPEG;
				break;
			case JPG:
				format = DocumentFormat.JPEG;
				break;
			case MHT:
				format = DocumentFormat.MHTML;
				break;
			case MHTML:
				format = DocumentFormat.MHTML;
				break;
			case MP3:
				format = DocumentFormat.MP3;
				break;
			case MP4:
				format = DocumentFormat.MP4V; // MP4A o MP4V!!!
				break;
			case MPEG:
				format = DocumentFormat.MP4V; // MP4A o MP4V!!!
				break;
			case ODG:
				format = DocumentFormat.OASIS12;
				break;
			case ODP:
				format = DocumentFormat.OASIS12;
				break;
			case ODS:
				format = DocumentFormat.OASIS12;
				break;
			case ODT:
				format = DocumentFormat.OASIS12;
				break;
			case OGA:
				format = DocumentFormat.OGG;
				break;
			case OGG:
				format = DocumentFormat.OGG;
				break;
			case PDF:
				format = DocumentFormat.PDF; // PDF o PDFA!!!
				break;
			case PNG:
				format = DocumentFormat.PNG;
				break;
			case PPTX:
				format = DocumentFormat.SOXML;
				break;
			case RTF:
				format = DocumentFormat.RTF;
				break;
			case SVG:
				format = DocumentFormat.SVG;
				break;
			case TIFF:
				format = DocumentFormat.TIFF;
				break;
			case TXT:
				format = DocumentFormat.TXT;
				break;
			case WEBM:
				format = DocumentFormat.WEBM;
				break;
			case XLSX:
				format = DocumentFormat.SOXML;
				break;
			case ZIP:
				format = DocumentFormat.ZIP;
				break;
			case CSIG:
				format = DocumentFormat.CSIG;
				break;
			case XSIG:
				format = DocumentFormat.XSIG;
				break;
			case XML:
				format = DocumentFormat.XML;
				break;
			}
			metadades.setFormat(format);
		}
		metadades.setOrgans(ntiOrgans);
		document.setMetadades(metadades);
		document.setEstat(estat);
		return document;
	}
	
	private void setFirmaTipusPerfil(
			Firma firma,
			ArxiuFirmaDto arxiuFirmaDto) {
		if (arxiuFirmaDto.getTipus() != null) {
			switch(arxiuFirmaDto.getTipus()) {
			case CSV:
				firma.setTipus(FirmaTipus.CSV);
				break;
			case XADES_DET:
				firma.setTipus(FirmaTipus.XADES_DET);
				break;
			case XADES_ENV:
				firma.setTipus(FirmaTipus.XADES_ENV);
				break;
			case CADES_DET:
				firma.setTipus(FirmaTipus.CADES_DET);
				break;
			case CADES_ATT:
				firma.setTipus(FirmaTipus.CADES_ATT);
				break;
			case PADES:
				firma.setTipus(FirmaTipus.PADES);
				break;
			case SMIME:
				firma.setTipus(FirmaTipus.SMIME);
				break;
			case ODT:
				firma.setTipus(FirmaTipus.ODT);
				break;
			case OOXML:
				firma.setTipus(FirmaTipus.OOXML);
				break;
			}
		}
		if (arxiuFirmaDto.getPerfil() != null) {
			switch(arxiuFirmaDto.getPerfil()) {
			case BES:
				firma.setPerfil(FirmaPerfil.BES);
				break;
			case EPES:
				firma.setPerfil(FirmaPerfil.EPES);
				break;
			case LTV:
				firma.setPerfil(FirmaPerfil.LTV);
				break;
			case T:
				firma.setPerfil(FirmaPerfil.T);
				break;
			case C:
				firma.setPerfil(FirmaPerfil.C);
				break;
			case X:
				firma.setPerfil(FirmaPerfil.X);
				break;
			case XL:
				firma.setPerfil(FirmaPerfil.XL);
				break;
			case A:
				firma.setPerfil(FirmaPerfil.A);
				break;
			}
		}
	}
	private String obtenirNtiOrgano(Expedient expedient) {
		if (expedient.getNtiOrgano() != null && !expedient.getNtiOrgano().isEmpty()) {
			return expedient.getNtiOrgano();
		} else {
			return expedient.getTipus().getNtiOrgano();
		}
	}
	
	private String obtenirNtiClasificacion(Expedient expedient) {
		if (expedient.getNtiClasificacion() != null && !expedient.getNtiClasificacion().isEmpty()) {
			return expedient.getNtiClasificacion();
		} else {
			return expedient.getTipus().getNtiClasificacion();
		}
	}

	private String obtenirNtiSerieDocumental(Expedient expedient) {
		if (expedient.getNtiSerieDocumental() != null && !expedient.getNtiSerieDocumental().isEmpty()) {
			return expedient.getNtiSerieDocumental();
		} else {
			return expedient.getTipus().getNtiSerieDocumental();
		}
	}
	
	private NtiOrigenEnumDto obtenirNtiOrigen(DocumentStore document) {
		return document.getNtiOrigen() != null ? document.getNtiOrigen() : NtiOrigenEnumDto.ADMINISTRACIO;
	}
	
	private NtiEstadoElaboracionEnumDto obtenirNtiEstadoElaboracion(DocumentStore document) {
		return document.getNtiEstadoElaboracion() != null ? document.getNtiEstadoElaboracion() : NtiEstadoElaboracionEnumDto.ORIGINAL;
	}
	
	private NtiTipoDocumentalEnumDto obtenirNtiTipoDocumental(DocumentStore document) {
		return document.getNtiTipoDocumental() != null ? document.getNtiTipoDocumental() : NtiTipoDocumentalEnumDto.ALTRES;
	}
	
	private DocumentPortasignatures getDocumentPortasignatures(
			DocumentDto document,
			Expedient expedient) {
		DocumentPortasignatures documentPs = new DocumentPortasignatures();
		// Llargada màxima pel títol 255. Abreuja l'identificador de l'expedient a 90 i tot plegat a 255
		documentPs.setTitol(StringUtils.abbreviate(StringUtils.abbreviate(expedient.getIdentificador(), 90) + ": " + document.getDocumentNom(), 254));
		documentPs.setArxiuNom(document.getVistaNom());
		documentPs.setArxiuContingut(document.getVistaContingut());
		documentPs.setTipus(document.getTipusDocPortasignatures());
		documentPs.setSignat(document.isSignat());
		documentPs.setReference(document.getId().toString());
		// Llargada màxima per la descripciól 255. Abreuja l'identificador de l'expedient a 90 i tot plegat a 255
		documentPs.setDescripcio(StringUtils.abbreviate(String.format("Document \"%s\" de l'expedient \"%s\"", document.getDocumentNom(), StringUtils.abbreviate(expedient.getIdentificador(), 90)), 254));
		logger.debug("Afegit document portafirmes (" +
				"arxiuNom=" + document.getVistaNom() + ", " +
				"arxiuContingut=" + document.getVistaContingut().length + ", " +
				"tipus=" + document.getTipusDocPortasignatures() + ", " +
				"signat=" + document.isSignat() + ")");
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
		if (persona == null) {
			return null;
		}
		String signatariId = persona.getDni();
		if (isIdUsuariPerCodi()) {
			signatariId = persona.getCodi();
		}
		if (isIdUsuariPerDni()) {
			signatariId = persona.getDni();
		}
		return signatariId;
	}
	
	private String personestoString(List<PersonaDto> persones) {
		if (persones == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (PersonaDto persona: persones) {
			if (persona != null) {
				sb.append(getSignatariIdPerPersona(persona));
				sb.append(",");
			}
		}
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1).toString();
		} else {
			return "";
		}
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

	private DocumentExtensio getExtensioPerArxiu(ArxiuDto arxiu) {
		String fitxerExtensio = arxiu.getExtensio();
		String extensioAmbPunt = (fitxerExtensio.startsWith(".")) ? fitxerExtensio.toLowerCase() : "." + fitxerExtensio.toLowerCase();
		return DocumentExtensio.toEnum(extensioAmbPunt);
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
		if (personesPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.persones.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					personesPlugin = (PersonesPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw tractarExcepcioEnSistemaExtern(
							"Error al crear la instància del plugin de persones (" +
							"pluginClass=" + pluginClass + ")",
							ex);
				}
			} else {
				throw tractarExcepcioEnSistemaExtern(
						"No està configurada la classe per al plugin de persones",
						null);
			}
		}
		return personesPlugin;
	}
	private TramitacioPlugin getTramitacioPlugin() {
		if (tramitacioPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.class");
			if (pluginClass == null || pluginClass.isEmpty()) {
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
					throw tractarExcepcioEnSistemaExtern(
							"Error al crear la instància del plugin de tramitació (" +
							"pluginClass=" + pluginClass + ")",
							ex);
				}
			} else {
				throw tractarExcepcioEnSistemaExtern(
						"No està configurada la classe per al plugin de tramitació",
						null);
			}
		}
		return tramitacioPlugin;
	}
	private RegistrePlugin getRegistrePlugin() {
		if (registrePlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.registre.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					registrePlugin = (RegistrePlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw tractarExcepcioEnSistemaExtern(
							"Error al crear la instància del plugin de registre (" +
							"pluginClass=" + pluginClass + ")",
							ex);
				}
			} else {
				throw tractarExcepcioEnSistemaExtern(
						"No està configurada la classe per al plugin de registre",
						null);
			}
		}
		return registrePlugin;
	}
	
	private RegistrePluginRegWeb3 getRegistrePluginRegWeb3() {
		if (registrePluginRegWeb3 == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.registre.plugin.rw3.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					registrePluginRegWeb3 = (RegistrePluginRegWeb3)clazz.newInstance();
				} catch (Exception ex) {
					throw tractarExcepcioEnSistemaExtern(
							"Error al crear la instància del plugin de REGWEB3 (" +
							"pluginClass=" + pluginClass + ")",
							ex);
				}
			} else {
				throw tractarExcepcioEnSistemaExtern(
						"No està configurada la classe per al plugin de REGWEB3",
						null);
			}
		}
		return registrePluginRegWeb3;
	}

	private GestioDocumentalPlugin getGestioDocumentalPlugin() {
		if (gestioDocumentalPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					gestioDocumentalPlugin = (GestioDocumentalPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw tractarExcepcioEnSistemaExtern(
							"Error al crear la instància del plugin de gestió documental (" +
							"pluginClass=" + pluginClass + ")",
							ex);
				}
			} else {
				throw tractarExcepcioEnSistemaExtern(
						"No està configurada la classe per al plugin de gestió documental",
						null);
			}
		}
		return gestioDocumentalPlugin;
	}

	private PortasignaturesPlugin getPortasignaturesPlugin() {
		if (portasignaturesPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.class");
			if ((pluginClass != null) && (pluginClass.length() > 0)) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					portasignaturesPlugin = (PortasignaturesPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw tractarExcepcioEnSistemaExtern(
							"Error al crear la instància del plugin de portafirmes (" +
							"pluginClass=" + pluginClass + ")",
							ex);
				}
			} else {
				throw tractarExcepcioEnSistemaExtern(
						"No està configurada la classe per al plugin de portafirmes",
						null);
			}
		}
		return portasignaturesPlugin;
	}
	private CustodiaPlugin getCustodiaPlugin() {
		if (custodiaPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.custodia.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					custodiaPlugin = (CustodiaPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw tractarExcepcioEnSistemaExtern(
							"Error al crear la instància del plugin de custòdia (" +
							"pluginClass=" + pluginClass + ")",
							ex);
				}
			} else {
				throw tractarExcepcioEnSistemaExtern(
						"No està configurada la classe per al plugin de custòdia",
						null);
			}
		}
		return custodiaPlugin;
	}
	private SignaturaPlugin getSignaturaPlugin() {
		if (signaturaPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.signatura.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					signaturaPlugin = (SignaturaPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw tractarExcepcioEnSistemaExtern(
							"Error al crear la instància del plugin de signatura (" +
							"pluginClass=" + pluginClass + ")",
							ex);
				}
			} else {
				throw tractarExcepcioEnSistemaExtern(
						"No està configurada la classe per al plugin de signatura",
						null);
			}
		}
		return signaturaPlugin;
	}
	private FirmaPlugin getFirmaPlugin() {
		if (firmaPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.firma.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					firmaPlugin = (FirmaPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw tractarExcepcioEnSistemaExtern(
							"Error al crear la instància del plugin de firma en servidor (" +
							"pluginClass=" + pluginClass + ")",
							ex);
				}
			} else {
				throw tractarExcepcioEnSistemaExtern(
						"No està configurada la classe per al plugin de firma en servidor",
						null);
			}
		}
		return firmaPlugin;
	}
	private IArxiuPlugin getArxiuPlugin() {
		if (arxiuPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty(
					"app.arxiu.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					if (GlobalProperties.getInstance().isLlegirSystem()) {
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class).newInstance(
								"app.");
					} else {
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class,
								Properties.class).newInstance(
								"app.",
								GlobalProperties.getInstance().findAll());
					}
				} catch (Exception ex) {
					throw tractarExcepcioEnSistemaExtern(
							"Error al crear la instància del plugin d'arxiu digital (" +
							"pluginClass=" + pluginClass + ")",
							ex);
				}
			} else {
				throw tractarExcepcioEnSistemaExtern(
						"No està configurada la classe per al plugin d'arxiu digital",
						null);
			}
		}
		return arxiuPlugin;
	}
	private NotificacioPlugin getNotificacioPlugin() {
		if (notificacioPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.notificacio.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					notificacioPlugin = (NotificacioPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw tractarExcepcioEnSistemaExtern(
							"Error al crear la instància del plugin de NOTIFICACIÓ (" +
							"pluginClass=" + pluginClass + ")",
							ex);
				}
			} else {
				throw tractarExcepcioEnSistemaExtern(
						"No està configurada la classe per al plugin de NOTIFICACIÓ",
						null);
			}
		}
		return notificacioPlugin;
	}
	
	private IValidateSignaturePlugin getValidaSignaturaPlugin() {		
		if (validaSignaturaPlugin == null) {
			//es.caib.ripea.plugin.validatesignature.class
			String pluginClass = GlobalProperties.getInstance().getProperty("app.validatesignature.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {					
					Class<?> clazz = Class.forName(pluginClass);
					if (GlobalProperties.getInstance().isLlegirSystem()) {
						validaSignaturaPlugin = (IValidateSignaturePlugin)clazz.getDeclaredConstructor(
								String.class).newInstance(
								"app.");
					} else {
						validaSignaturaPlugin = (IValidateSignaturePlugin)clazz.getDeclaredConstructor(
								String.class,
								Properties.class).newInstance(
								"app.",
								GlobalProperties.getInstance().findAll());
					}
				} catch (Exception ex) {
					throw tractarExcepcioEnSistemaExtern(
							"Error al crear la instància del plugin de VALIDACIO SIGNATURES (" +
							"pluginClass=" + pluginClass + ")",
							ex);
				}
			} else {
				throw tractarExcepcioEnSistemaExtern(
						"No està configurada la classe per al plugin de VALIDACIO SIGNATURES",
						null);
			}
		}
		return validaSignaturaPlugin;
	}

	private SistemaExternException tractarExcepcioEnSistemaExtern(
			String missatge,
			Throwable ex) {
		return SistemaExternException.tractarSistemaExternException(
				null,
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null,
				missatge,
				ex);
	}

	private static final Log logger = LogFactory.getLog(PluginHelper.class);

}
