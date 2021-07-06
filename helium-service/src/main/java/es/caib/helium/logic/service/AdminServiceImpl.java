/**
 * 
 */
package es.caib.helium.logic.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.helper.HibernateHelper;
import es.caib.helium.logic.helper.MailHelper;
import es.caib.helium.logic.helper.MonitorDominiHelper;
import es.caib.helium.logic.helper.MonitorIntegracioHelper;
import es.caib.helium.logic.intf.dto.*;
import es.caib.helium.logic.intf.dto.PersonaDto.Sexe;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.service.AdminService;
import es.caib.helium.logic.util.GlobalProperties;
import es.caib.helium.ms.domini.DominiMs;
import es.caib.helium.ms.domini.client.model.Domini;
import es.caib.helium.persist.entity.Entorn;
import es.caib.helium.persist.entity.Persona;
import es.caib.helium.persist.entity.Reassignacio;
import es.caib.helium.persist.entity.UsuariPreferencies;
import es.caib.helium.persist.repository.EntornRepository;
import es.caib.helium.persist.repository.PersonaRepository;
import es.caib.helium.persist.repository.ReassignacioRepository;
import es.caib.helium.persist.repository.UsuariPreferenciesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Servei per gestionar la configuració de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AdminServiceImpl implements AdminService {

	@Resource
	private EntornRepository entornRepository;
	@Resource
	private DominiMs dominiMs;
	@Resource
	private ReassignacioRepository reassignacioRepository;
	@Resource
	private UsuariPreferenciesRepository usuariPreferenciesRepository;
	@Resource
	private PersonaRepository personaRepository;

	@Resource
	private HibernateHelper hibernateHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
//	@Resource
//	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private MonitorIntegracioHelper monitorIntegracioHelper;
	@Resource
	private MonitorDominiHelper monitorDominiHelper;
	@Resource
	private MailHelper mailHelper;

//	@Autowired
//	private MetricRegistry metricRegistry;



	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMetrics() {
		logger.debug("Consultant les mètriques de l'aplicació");
		try {
			return getApplictionMetrics();
		} catch (Exception ex) {
			logger.error("Error al generar les mètriques de l'aplicació", ex);
			return "ERR";
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Scheduled(cron="0 30 23 * * *")
	public void metricsEmailResponsables() {
		logger.debug("Enviant email amb les mètriques de l'aplicació");
		String destinataris = getCorreuMetriquesDestinataris();
		if (destinataris != null && !destinataris.isEmpty()) {
			try {
				List<String> recipients = new ArrayList<String>();
				for (String recipient: destinataris.split(",")) {
					recipients.add(recipient.trim());
				}
				String fromAddress = getCorreuRemitent();
				List<ArxiuDto> attachments = new ArrayList<ArxiuDto>();
				attachments.add(
						new ArxiuDto(
								"metrics.json",
								getApplictionMetrics().getBytes()));
				mailHelper.send(
						fromAddress,
						recipients,
						null,
						null,
						"Mètriques Helium " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
						"Mètriques generades per a monitoritzar l'ús de l'aplicació.",
						attachments);
			} catch (Exception ex) {
				logger.error("Error al enviar per correu les mètriques de l'aplicació", ex);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IntegracioDto> monitorIntegracioFindAll() {
		logger.debug("Consultant la llista d'integracions disponibles");
		return monitorIntegracioHelper.findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IntegracioAccioDto> monitorIntegracioFindAccionsByIntegracio(
			String integracioCodi) {
		logger.debug("Consultant la llista d'accions per a la integració (" +
				"integracioCodi=" + integracioCodi + ")");
		return monitorIntegracioHelper.findAccionsByIntegracioCodi(integracioCodi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<DominiDto> monitorDominiFindByEntorn(
			Long entornId) {
		logger.debug("Consultant la llista de dominis donat un entorn (" +
				"entornId=" + entornId + ")");
		Entorn entorn = null;
		if (entornId != null) {
			entorn = entornRepository.findById(entornId).orElseThrow(() -> new NoTrobatException(Entorn.class,entornId));
		}
		return monitorDominiHelper.findByEntorn(entorn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IntegracioAccioDto> monitorDominiFindAccionsByDomini(
			Long dominiId) {
		logger.debug("Consultant la llista d'accions per al domini (" +
				"dominiId=" + dominiId + ")");
		if (dominiId != 0L) {
			// Domini no intern
			DominiDto domini = dominiMs.get(dominiId);
			if (domini == null) {
				throw new NoTrobatException(Domini.class,dominiId);
			}
		}
		return monitorDominiHelper.findAccionsByDomini(dominiId);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void monitorAddAccio(
			String integracioCodi,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			IntegracioAccioEstatEnumDto estat,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable,
			List<IntegracioParametreDto> parametres) {
		
		IntegracioParametreDto[] params = new IntegracioParametreDto[parametres.size()];
		for (int i = 0; i < parametres.size(); i ++)
			params[i] = parametres.get(i);
		switch(estat) {
		case ERROR:
			monitorIntegracioHelper.addAccioError(
					integracioCodi, 
					descripcio, 
					tipus, 
					tempsResposta, 
					errorDescripcio, 
					params);
			break;
		case OK:
			monitorIntegracioHelper.addAccioOk(
					integracioCodi, 
					errorDescripcio, 
					tipus, 
					tempsResposta,  
					params);
			break;
		}
	}

	// TODO: Mètriques
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MesuraTemporalDto> mesuraTemporalFindByFamilia(
			String familia,
			boolean ambDetall) {
		logger.debug("Consultant el llistat de mesures temporals per família (" +
				"familia=" + familia + ", " +
				"ambDetall=" + ambDetall + ")");
//		return mesuresTemporalsHelper.getEstadistiques(familia, ambDetall);
		return null;
	}

	// TODO: Mètriques
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MesuraTemporalDto> mesuraTemporalFindByTipusExpedient() {
		logger.debug("Consultant el llistat de mesures temporals dels tipus d'expedient");
//		return mesuresTemporalsHelper.getEstadistiquesTipusExpedient();
		return null;
	}

	// TODO: Mètriques
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MesuraTemporalDto> mesuraTemporalFindByTasca() {
		logger.debug("Consultant el llistat de mesures temporals de les tasques");
//		return mesuresTemporalsHelper.getEstadistiquesTasca();
		return null;
	}

	// TODO: Mètriques
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> mesuraTemporalFindFamiliesAll() {
		logger.debug("Consultant el llistat de famílies de mesures temporals");
//		return mesuresTemporalsHelper.getIntervalsFamilia();
		return null;
	}

	// TODO: Mètriques
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mesuraTemporalIniciar(
			String clau,
			String familia) {
		logger.debug("Consultant el llistat de famílies de mesures temporals");
//		mesuresTemporalsHelper.mesuraIniciar(
//				clau,
//				familia);
	}

	// TODO: Mètriques
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mesuraTemporalIniciar(
			String clau,
			String familia,
			String tipusExpedient) {
//		mesuresTemporalsHelper.mesuraIniciar(
//				clau,
//				familia,
//				tipusExpedient);
	}

	// TODO: Mètriques
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mesuraTemporalIniciar(
			String clau,
			String familia,
			String tipusExpedient,
			String tasca,
			String detall) {
//		mesuresTemporalsHelper.mesuraIniciar(
//				clau,
//				familia,
//				tipusExpedient,
//				tasca,
//				detall);
	}

	// TODO: Mètriques
	@Override
	public void mesuraTemporalCalcular(
			String clau,
			String familia) {
//		mesuresTemporalsHelper.mesuraCalcular(
//				clau,
//				familia);
	}

	// TODO: Mètriques
	@Override
	public void mesuraTemporalCalcular(
			String clau,
			String familia,
			String tipusExpedient) {
//		mesuresTemporalsHelper.mesuraCalcular(
//				clau,
//				familia,
//				tipusExpedient);
	}

	// TODO: Mètriques
	@Override
	public void mesuraTemporalCalcular(
			String clau,
			String familia,
			String tipusExpedient,
			String tasca,
			String detall) {
//		mesuresTemporalsHelper.mesuraCalcular(
//				clau,
//				familia,
//				tipusExpedient,
//				tasca,
//				detall);
	}

	// TODO: Mètriques
	@Override
	public boolean mesuraTemporalIsActive() {
//		return MesuresTemporalsHelper.isActiu();
		return false;
	}

	@Override
	public boolean isStatisticActive() {
		return hibernateHelper.isStatisticActive();
	}

	@Override
	public List<MesuraTemporalDto> getHibernateStatistics(
			String familia,
			boolean exportar) {
		return hibernateHelper.getHibernateStatistics(familia, exportar);
	}

	// TODO: Mètriques
	@Override
	public List<TascaCompleteDto> getTasquesCompletar() {
//		return mesuresTemporalsHelper.getTasquesCompletar();
		return null;
	}
	
	@Transactional
	@Override
	public void updatePerfil(UsuariPreferenciesDto preferencies) {
		UsuariPreferencies usuari = usuariPreferenciesRepository.findByCodi(preferencies.getCodi());
		if (usuari == null)
			usuari = new UsuariPreferencies();
		
		usuari.setCodi(preferencies.getCodi());
		usuari.setCabeceraReducida(preferencies.isCabeceraReducida());
		usuari.setConsultaId(preferencies.getConsultaId());
		usuari.setExpedientTipusDefecteId(preferencies.getExpedientTipusDefecteId());
		usuari.setDefaultEntornCodi(preferencies.getDefaultEntornCodi());
		usuari.setFiltroTareasActivas(preferencies.isFiltroTareasActivas());
		usuari.setListado(preferencies.getListado());
		usuari.setNumElementosPagina(preferencies.getNumElementosPagina());
		usuariPreferenciesRepository.save(usuari);
	}

	@Transactional
	@Override
	public void setIdiomaPref(String usuari, String idioma) {
		UsuariPreferencies usuariPreferencies = usuariPreferenciesRepository.findByCodi(usuari);
		if (usuariPreferencies != null)
		{
			usuariPreferencies.setIdioma(idioma);
			usuariPreferenciesRepository.save(usuariPreferencies);
		}		
	}

	@Transactional
	@Override
	public void updatePersona(PersonaDto personaDto) {
		Persona persona = personaRepository.findByCodi(personaDto.getCodi());
		persona.setDni(personaDto.getDni());
		persona.setEmail(personaDto.getEmail());
		persona.setLlinatge1(personaDto.getLlinatge1());
		persona.setLlinatge2(personaDto.getLlinatge2());
		persona.setNom(personaDto.getNom());
		persona.setSexe(personaDto.getSexe().equals(Sexe.SEXE_HOME) ? Persona.Sexe.SEXE_HOME : Persona.Sexe.SEXE_DONA);
		personaRepository.save(persona);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<ReassignacioDto> llistaReassignacions() {
		return conversioTipusHelper.convertirList(reassignacioRepository.findLlistaActius(Calendar.getInstance().getTime()), ReassignacioDto.class);
	}
	
	@Transactional
	@Override
	public void createReassignacio(
			String usuariOrigen,
			String usuariDesti,
			Date dataInici,
			Date dataFi,
			Date dataCancelacio,
			Long tipusExpedientId) {
		Reassignacio reassignacio = new Reassignacio();
		reassignacio.setUsuariOrigen(usuariOrigen);
		reassignacio.setUsuariDesti(usuariDesti);
		reassignacio.setDataInici(dataInici);
		reassignacio.setDataFi(dataFi);
		reassignacio.setDataCancelacio(dataCancelacio);
		reassignacio.setTipusExpedientId(tipusExpedientId);
		reassignacioRepository.save(reassignacio);
	}

	@Transactional
	@Override
	public void updateReassignacio(
			Long id,
			String usuariOrigen,
			String usuariDesti,
			Date dataInici,
			Date dataFi,
			Date dataCancelacio,
			Long tipusExpedientId) {
		Reassignacio reassignacio = reassignacioRepository.findById(id).get();
		reassignacio.setUsuariOrigen(usuariOrigen);
		reassignacio.setUsuariDesti(usuariDesti);
		reassignacio.setDataInici(dataInici);
		reassignacio.setDataFi(dataFi);
		reassignacio.setDataCancelacio(dataCancelacio);
		reassignacio.setTipusExpedientId(tipusExpedientId);
		reassignacioRepository.save(reassignacio);
	}

	@Transactional
	@Override
	public void deleteReassignacio(Long id) {
		Reassignacio reassignacio = reassignacioRepository.findById(id).orElse(null);
		if (reassignacio != null) {
			reassignacio.setDataCancelacio(Calendar.getInstance().getTime());
			reassignacioRepository.save(reassignacio);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public ReassignacioDto findReassignacioById(Long id) {
		return conversioTipusHelper.convertir(reassignacioRepository.findById(id), ReassignacioDto.class);
	}



	// TODO: Mètriques
	private String getApplictionMetrics() throws JsonProcessingException {
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.registerModule(
//				new MetricsModule(
//						TimeUnit.SECONDS,
//						TimeUnit.MILLISECONDS,
//						false));
//		return mapper.writeValueAsString(metricRegistry);
		return null;
	}

	private String getCorreuRemitent() {
		return GlobalProperties.getInstance().getProperty("app.correu.remitent");
	}
	private String getCorreuMetriquesDestinataris() {
		return GlobalProperties.getInstance().getProperty("app.correu.metrics.recipients");
	}

	private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);


	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IntegracioDto> monitorIntegracioFindAllEntronActual() {
		logger.debug("Consultant la llista d'integracions disponibles");
		return monitorIntegracioHelper.findAllEntornActual();
	}

	@Override
	public List<IntegracioAccioDto> monitorIntegracioFindAccionsByIntegracioEntornActual(String integracioCodi) {
		logger.debug("Consultant la llista d'accions per a la integració (" +
				"integracioCodi=" + integracioCodi + ")");
		return monitorIntegracioHelper.findAccionsByIntegracioCodiEntornActual(integracioCodi);
	}

	// TODO: Mètriques
	@Override
	public List<TascaCompleteDto> getTasquesCompletarAdminEntorn() {
//		return mesuresTemporalsHelper.getTasquesCompletarAdminEntiorn();
		return null;
	}

}
