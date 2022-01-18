/**
 * 
 */
package es.caib.helium.logic.helper;

import es.caib.helium.client.engine.model.WProcessDefinition;
import es.caib.helium.client.engine.model.WProcessInstance;
import es.caib.helium.client.engine.model.WTaskInstance;
import es.caib.helium.client.model.ParellaCodiValor;
import es.caib.helium.integracio.plugins.registre.DadesAssumpte;
import es.caib.helium.integracio.plugins.registre.DadesExpedient;
import es.caib.helium.integracio.plugins.registre.DadesInteressat;
import es.caib.helium.integracio.plugins.registre.DadesNotificacio;
import es.caib.helium.integracio.plugins.registre.DadesOficina;
import es.caib.helium.integracio.plugins.registre.DocumentRegistre;
import es.caib.helium.integracio.plugins.registre.RegistreNotificacio;
import es.caib.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import es.caib.helium.integracio.plugins.registre.RespostaJustificantDetallRecepcio;
import es.caib.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import es.caib.helium.logic.helper.TascaSegonPlaHelper.InfoSegonPla;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi;
import es.caib.helium.logic.intf.dto.AreaDto;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.CampTascaDto;
import es.caib.helium.logic.intf.dto.CarrecDto;
import es.caib.helium.logic.intf.dto.DadesNotificacioDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.DocumentDissenyDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.DocumentTascaDto;
import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.DominiRespostaColumnaDto;
import es.caib.helium.logic.intf.dto.DominiRespostaFilaDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.EnumeracioValorDto;
import es.caib.helium.logic.intf.dto.EstatDto;
import es.caib.helium.logic.intf.dto.ExpedientDadaDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.FestiuDto;
import es.caib.helium.logic.intf.dto.InteressatDto;
import es.caib.helium.logic.intf.dto.InteressatTipusEnumDto;
import es.caib.helium.logic.intf.dto.NotificacioDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.dto.ReassignacioDto;
import es.caib.helium.logic.intf.dto.ReferenciaNotificacio;
import es.caib.helium.logic.intf.dto.ReferenciaRDSJustificanteDto;
import es.caib.helium.logic.intf.dto.RegistreAnnexDto;
import es.caib.helium.logic.intf.dto.RegistreAnotacioDto;
import es.caib.helium.logic.intf.dto.RegistreIdDto;
import es.caib.helium.logic.intf.dto.RegistreNotificacioDto;
import es.caib.helium.logic.intf.dto.RespostaJustificantDetallRecepcioDto;
import es.caib.helium.logic.intf.dto.RespostaJustificantRecepcioDto;
import es.caib.helium.logic.intf.dto.RespostaNotificacio;
import es.caib.helium.logic.intf.dto.RespostaNotificacio.NotificacioEstat;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.dto.TerminiDto;
import es.caib.helium.logic.intf.dto.TerminiIniciatDto;
import es.caib.helium.logic.intf.dto.TramitDto;
import es.caib.helium.logic.intf.dto.ZonaperEventDto;
import es.caib.helium.logic.intf.dto.ZonaperExpedientDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.extern.domini.FilaResultat;
import es.caib.helium.logic.intf.registre.RegistreAnotacio;
import es.caib.helium.logic.intf.service.Jbpm3HeliumService;
import es.caib.helium.logic.intf.util.GlobalProperties;
import es.caib.helium.logic.ms.DominiMs;
import es.caib.helium.logic.util.EntornActual;
import es.caib.helium.persist.entity.Alerta;
import es.caib.helium.persist.entity.Area;
import es.caib.helium.persist.entity.Camp;
import es.caib.helium.persist.entity.DefinicioProces;
import es.caib.helium.persist.entity.Document;
import es.caib.helium.persist.entity.DocumentNotificacio;
import es.caib.helium.persist.entity.DocumentStore;
import es.caib.helium.persist.entity.Entorn;
import es.caib.helium.persist.entity.Enumeracio;
import es.caib.helium.persist.entity.EnumeracioValors;
import es.caib.helium.persist.entity.Estat;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.Interessat;
import es.caib.helium.persist.entity.Reassignacio;
import es.caib.helium.persist.entity.Tasca;
import es.caib.helium.persist.entity.Termini;
import es.caib.helium.persist.entity.TerminiIniciat;
import es.caib.helium.persist.repository.AreaRepository;
import es.caib.helium.persist.repository.CampRepository;
import es.caib.helium.persist.repository.CampTascaRepository;
import es.caib.helium.persist.repository.CarrecRepository;
import es.caib.helium.persist.repository.DefinicioProcesRepository;
import es.caib.helium.persist.repository.DocumentRepository;
import es.caib.helium.persist.repository.DocumentStoreRepository;
import es.caib.helium.persist.repository.DocumentTascaRepository;
import es.caib.helium.persist.repository.EntornRepository;
import es.caib.helium.persist.repository.EnumeracioRepository;
import es.caib.helium.persist.repository.EnumeracioValorsRepository;
import es.caib.helium.persist.repository.EstatRepository;
import es.caib.helium.persist.repository.ExpedientRepository;
import es.caib.helium.persist.repository.ExpedientTipusRepository;
import es.caib.helium.persist.repository.FestiuRepository;
import es.caib.helium.persist.repository.InteressatRepository;
import es.caib.helium.persist.repository.ReassignacioRepository;
import es.caib.helium.persist.repository.TascaRepository;
import es.caib.helium.persist.repository.TerminiIniciatRepository;
import es.caib.helium.persist.util.ThreadLocalInfo;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
/**
 * Service que implementa la funcionalitat necessària per
 * a integrar Helium i jBPM.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class Jbpm3HeliumHelper implements Jbpm3HeliumService {

	@Resource
	private EntornRepository entornRepository;
	@Resource
	private DominiMs dominiMs;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private InteressatRepository interessatRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private AreaRepository areaRepository;
	@Resource
	private CarrecRepository carrecRepository;
	@Resource
	private FestiuRepository festiuRepository;
	@Resource
	private ReassignacioRepository reassignacioRepository;
//	@Resource
//	private AlertaRepository alertaRepository;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private EnumeracioValorsRepository enumeracioValorsRepository;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private DocumentTascaRepository documentTascaRepository;
//	@Resource
//	private TerminiRepository terminiRepository;
	
//	@Resource
//	private RegistreRepository registreRepository;

	
	@Resource(name = "documentHelper")
	private DocumentHelper documentHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private DominiHelper dominiHelper;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private WorkflowRetroaccioApi workflowRetroaccioApi;
	@Resource
	private VariableHelper variableHelper;	
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private TerminiHelper terminiHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private MailHelper mailHelper;
	@Resource
	private NotificacioHelper notificacioElectronicaHelper;

	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
//	@Resource
//	private MesuresTemporalsHelper mesuresTemporalsHelper;

	@Resource
	private IndexHelper indexHelper;

//	@Resource
//	private MetricRegistry metricRegistry;
	
	@Resource
	private TascaSegonPlaHelper tascaSegonPlaHelper;
	
	@Resource
	private DefinicioProcesHelper definicioProcesHelper;
	
	@Resource 
	private AlertaHelper alertaHelper;
	@Resource
	private GlobalProperties globalProperties;



	@Override
	public String getUsuariCodiActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getName();
	}

	@Override
	public EntornDto getEntornActual() {
		Long entornId = EntornActual.getEntornId();
		logger.debug("Obtenint entorn actual (idEntornActual=" + entornId + ")");
		if (entornId == null)
			return null;
		return conversioTipusServiceHelper.convertir(
				entornRepository.findById(entornId),
				EntornDto.class);
	}

	@Override
	public ExpedientDto getExpedientIniciant() {
		logger.debug("Obtenint expedient en fase d'inici");
		return conversioTipusServiceHelper.convertir(
				ThreadLocalInfo.getExpedient(),
				ExpedientDto.class);
	}

	@Override
	public ExpedientDto getExpedientAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero) {
		logger.debug("Obtenint expedient donat entorn, tipus i número (" +
				"entornId=" + entornId + ", " +
				"expedientTipusCodi=" + expedientTipusCodi + ", " +
				"numero=" + numero + ")");
		Optional<Entorn> entornOptional = entornRepository.findById(entornId);
		if (entornOptional.isPresent())
			throw new NoTrobatException(Entorn.class, entornId);
		Entorn entorn = entornOptional.get();
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
				entorn,
				expedientTipusCodi);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusCodi);
		return conversioTipusServiceHelper.convertir(
				expedientRepository.findByEntornAndTipusAndNumero(
						entorn,
						expedientTipus,
						numero),
				ExpedientDto.class);
	}

	@Override
	public void luceneDeleteExpedient(String processInstanceId) {
		logger.debug("Esborra expedient donada una instància de procés (" +
				"processInstanceId=" + processInstanceId + ")");
		indexHelper.expedientIndexLuceneDelete(processInstanceId);
	}
	
	@Override
	public ExpedientDto getExpedientArrelAmbProcessInstanceId(
			String processInstanceId) {
		logger.debug("Obtenint expedient donada una instància de procés (processInstanceId=" + processInstanceId + ")");
		return conversioTipusServiceHelper.convertir(
				getExpedientDonatProcessInstanceId(processInstanceId),
				ExpedientDto.class);
	}
	
	@Override
	public EntornDto getEntornAmbProcessInstanceId(
			String processInstanceId) {
		logger.debug("Obtenint expedient donada una instància de procés (processInstanceId=" + processInstanceId + ")");
		return conversioTipusServiceHelper.convertir(
				getEntornDonatProcessInstanceId(processInstanceId),
				EntornDto.class);
	}

	@Override
	public DefinicioProcesDto getDefinicioProcesAmbJbpmKeyIProcessInstanceId(
			String jbpmKey,
			String processInstanceId) {
		logger.debug("Obtenint la darrera versió de la definició de procés donat el codi jBPM i el processInstanceId (jbpmKey=" + jbpmKey + ", processInstanceId=" + processInstanceId +")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		DefinicioProces defincioProces = definicioProcesHelper.findDarreraVersioDefinicioProces(
				expedient.getTipus(), 
				jbpmKey);
		return conversioTipusServiceHelper.convertir(
				defincioProces,
				DefinicioProcesDto.class);
	}
	
	@Override
	public DefinicioProcesDto getDefinicioProcesAmbJbpmKeyIVersio(
			String jbpmKey,
			int version) {
		logger.debug("Obtenint la definició de procés donat el codi jBPM i la versió (jbpmKey=" + jbpmKey + ", version=" + version +")");
		return conversioTipusServiceHelper.convertir(
				definicioProcesRepository.findByJbpmKeyAndVersio(
						jbpmKey,
						version),
				DefinicioProcesDto.class);
	}

	@Override
	public DefinicioProcesDto getDarreraVersioAmbEntornIJbpmKey(
			Long entornId,
			String jbpmKey) {
		logger.debug("Obtenint la darrera versió de la definició de procés donat l'entorn i el codi jBPM (entornId=" + entornId + ", jbpmKey=" + jbpmKey + ")");
		return conversioTipusServiceHelper.convertir(
				definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
						entornId,
						jbpmKey),
				DefinicioProcesDto.class);
	}

	@Override
	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(
			String processInstanceId) {
		logger.debug("Obtenint la definició de procés donada la instància de procés (processInstanceId=" + processInstanceId + ")");
		return conversioTipusServiceHelper.convertir(
				getDefinicioProcesDonatProcessInstanceId(processInstanceId),
				DefinicioProcesDto.class);
	}

	@Override
	public PersonaDto getPersonaAmbCodi(String codi) {
		logger.debug("Obtenint persona (codi=" + codi + ")");
		return conversioTipusServiceHelper.convertir(
				pluginHelper.personaFindAmbCodi(codi),
				PersonaDto.class);
	}

	@Override
	public AreaDto getAreaAmbEntornICodi(
			Long entornId,
			String codi) {
		logger.debug("Obtenint area donat l'entorn i el codi (" +
				"entornId=" + entornId + ", " +
				"codi=" + codi + ")");
		Optional<Entorn> entornOptional = entornRepository.findById(entornId);
		if (entornOptional.isPresent())
			throw new NoTrobatException(Entorn.class, entornId);
		Entorn entorn = entornOptional.get();
		return conversioTipusServiceHelper.convertir(
				areaRepository.findByEntornAndCodi(
						entorn,
						codi),
				AreaDto.class);
	}

	@Override
	public CarrecDto getCarrecAmbEntornIAreaICodi(
			Long entornId,
			String areaCodi,
			String carrecCodi) {
		logger.debug("Obtenint carrec donat l'entorn, l'àrea i el codi (" +
				"entornId=" + entornId + ", " +
				"areaCodi=" + areaCodi + ", " +
				"carrecCodi=" + carrecCodi + ")");
		Optional<Entorn> entornOptional = entornRepository.findById(entornId);
		if (entornOptional.isPresent())
			throw new NoTrobatException(Entorn.class, entornId);
		Entorn entorn = entornOptional.get();

		Area area = areaRepository.findByEntornAndCodi(
				entorn,
				areaCodi);
		if (area == null)
			throw new NoTrobatException(Area.class, areaCodi);
		return conversioTipusServiceHelper.convertir(
				carrecRepository.findByEntornAndAreaAndCodi(
						entorn,
						area,
						carrecCodi),
				CarrecDto.class);
	}

	@Override
	public List<FestiuDto> findFestiusAll() {
		logger.debug("Obtenint la llista de tots els festius");
		return conversioTipusServiceHelper.convertirList(
				festiuRepository.findAll(),
				FestiuDto.class);
	}

	@Override
	public ReassignacioDto findReassignacioActivaPerUsuariOrigen(
			String processInstanceId,
			String usuariCodi) {
		logger.debug("Obtenint reassignació activa per a l'usuari ("
				+ "processInstanceId=" + processInstanceId + ", "
				+ "usuariCodi=" + usuariCodi + ")");
		Date ara = new Date();
		
		Reassignacio reassignacio = null;
		// Cerca primer pel tipus d'expedient
		if (processInstanceId != null && !"".equals(processInstanceId.trim())) {
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
			if (expedient != null) {
				reassignacio = reassignacioRepository.findByUsuariAndTipusExpedientId(
						usuariCodi, 
						expedient.getTipus().getId(), 
						ara, 
						ara);
			}
		}
		// Si no es troba cerca una redirecció global
		if (reassignacio == null) {
			reassignacio = reassignacioRepository.findByUsuari(
					usuariCodi, 
					ara, 
					ara);
		}
		return conversioTipusServiceHelper.convertir(
				reassignacio,
				ReassignacioDto.class);
	}

	
	
	@Override
	public void interessatCrear(
			InteressatDto interessat) {
		
		this.validaInteressat(interessat);

		Expedient expedient = expedientHelper.findById(interessat.getExpedientId());
		
		if (interessatRepository.findByCodiAndExpedient(interessat.getCodi(), expedient) != null) {
			throw new ValidacioException("Ja existeix un interessat amb aquest codi");
		}
		
		Interessat interessatEntity = new Interessat(
				interessat.getId(),
				interessat.getCodi(),
				interessat.getNom(),
				interessat.getNif(),
				interessat.getDir3Codi(),
				interessat.getLlinatge1(), 
				interessat.getLlinatge2(), 
				InteressatTipusEnumDto.valueOf(interessat.getTipus().toString()),
				interessat.getEmail(), 
				interessat.getTelefon(),
				expedient,
				interessat.getEntregaPostal(),
				interessat.getEntregaTipus(),
				interessat.getLinia1(),
				interessat.getLinia2(),
				interessat.getCodiPostal(),
				interessat.getEntregaDeh(),
				interessat.getEntregaDehObligat());
		
		interessatRepository.save(interessatEntity);
	}
	
	@Override
	public void interessatModificar(
			InteressatDto interessat) {
		
		this.validaInteressat(interessat);

		Expedient expedient = expedientHelper.findById(interessat.getExpedientId());
		
		if (interessatRepository.findByCodiAndExpedient(interessat.getCodi(), expedient) == null) {
			throw new ValidacioException("Un interessat amb aquest codi no existeix");
		}
		
		Interessat interessatEntity = interessatRepository.findByCodiAndExpedient(
				interessat.getCodi(), 
				expedient);
		
		interessatEntity.setNom(interessat.getNom());
		interessatEntity.setNif(interessat.getNif());
		interessatEntity.setLlinatge1(interessat.getLlinatge1());
		interessatEntity.setLlinatge2(interessat.getLlinatge2());
		interessatEntity.setTipus(InteressatTipusEnumDto.valueOf(interessat.getTipus().toString()));
				interessatEntity.setEmail(interessat.getEmail());
		interessatEntity.setTelefon(interessat.getTelefon());
		interessatEntity.setEntregaPostal(interessat.getEntregaPostal());
		interessatEntity.setEntregaTipus(interessat.getEntregaTipus());
		interessatEntity.setLinia1(interessat.getLinia1());
		interessatEntity.setLinia2(interessat.getLinia2());
		interessatEntity.setCodiPostal(interessat.getCodiPostal());
		interessatEntity.setEntregaDeh(interessat.getEntregaDeh());
		interessatEntity.setEntregaDehObligat(interessat.getEntregaDehObligat());
	}
	
	
	private void validaInteressat(InteressatDto interessat) throws ValidacioException{
		List<String> errors = new ArrayList<String>();
		
		if (interessat.getTipus() != null) {
			switch (interessat.getTipus()) {
			case ADMINISTRACIO:
				if (interessat.getDir3Codi() == null || interessat.getDir3Codi().isEmpty()) {
					// Codi DIR3 per administracions
					errors.add("El codi DIR3 és obligatori per interessats de tipus Administració");
				}
				break;
			case FISICA:
			case JURIDICA:
				break;
			}
		}
		// Camps obligatoris
		if (interessat.getCodi() == null || interessat.getCodi().isEmpty())
			errors.add("El codi és obligatori");
		if (interessat.getNif() == null || interessat.getNif().isEmpty())
			errors.add("El NIF/CIF/DNI és obligatori");
		if (interessat.getNom() == null || interessat.getNom().isEmpty())
			errors.add("El nom/raó social és obligatori");
		if (interessat.getTipus() == null)
			errors.add("El tipus d'interessat és obligatori");
		
		// Llinatge1 per persones físiques
		if (interessat.getTipus() == es.caib.helium.logic.intf.integracio.notificacio.InteressatTipusEnum.FISICA 
				&& (interessat.getLlinatge1() == null || interessat.getLlinatge1().isEmpty())) {
			errors.add("Si el tipus de persona és física llavors el llinatge és obligatori");
		}
		
		// Línies entrega postal
		if (interessat.getEntregaPostal()) {
			if(interessat.getTipus() == null) {
				errors.add("El tipus d'entrega postal és obligatori si està habilidada l'entrega postal");
			}
			if(interessat.getLinia1() == null || interessat.getLinia1().isEmpty()) {
				errors.add("La línia 1 és obligatòria si està habilitada l'entrega postal");
			}
			if (interessat.getLinia2() == null || interessat.getLinia2().isEmpty()) {
				errors.add("La línia 2 és obligatòria si està habilitada l'entrega postal");
			}
			if (interessat.getCodiPostal() == null || interessat.getCodiPostal().isEmpty()) {
				errors.add("El codi postal és obligatori si està habilitada l'entrega postal");
			}
		}
		// email per entregues DEH
		if (interessat.getEntregaDeh() && (interessat.getEmail() == null || interessat.getEmail().isEmpty())) {
			errors.add("L'email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)");
		}

		if (!errors.isEmpty()) {
			StringBuilder errorMsg = new StringBuilder("Errors de validació de l'interessat: [");
			for (int i = 0; i < errors.size(); i++) {
				errorMsg.append(errors.get(i));
				if (i < errors.size() -1)
					errorMsg.append(", ");
			}
			errorMsg.append("]");
			throw new ValidacioException(errorMsg.toString());
		}

	}

	@Override
	public void interessatEliminar(
			InteressatDto interessat) {
		
		Expedient expedient = expedientHelper.findById(interessat.getExpedientId());
		
		if (interessatRepository.findByCodiAndExpedient(interessat.getCodi(), expedient) == null) {
			throw new ValidacioException("Un interessat amb aquest codi no existeix");
		}
		
		Interessat interessatEntity = interessatRepository.findByCodiAndExpedient(
				interessat.getCodi(), 
				expedient);
		List<Interessat> interessats = expedient.getInteressats();
		interessats.remove(interessatEntity);
		expedient.setInteressats(interessats);
		interessatRepository.delete(interessatEntity);
		
	}
	

	@Override
	public void alertaCrear(
			Long entornId,
			Long expedientId,
			Date data,
			String usuariCodi,
			String text) {
		logger.debug("Creant alerta (" +
				"entornId=" + entornId + ", " +
				"expedientId=" + expedientId + ", " +
				"data=" + data + ", " +
				"usuariCodi=" + usuariCodi + ", " +
				"text=" + text + ")");
		Optional<Entorn> entornOptional = entornRepository.findById(entornId);
		if (entornOptional.isPresent())
			throw new NoTrobatException(Entorn.class, entornId);
		Entorn entorn = entornOptional.get();
		Expedient expedient = expedientHelper.findById(expedientId);
		
		alertaHelper.crearAlerta(entorn, expedient, data, usuariCodi, text);
	}

	@Override
	public void alertaEsborrarAmbTaskInstanceId(String taskInstanceId) {
		logger.debug("Esborrant alertes amb taskInstance (" +
				"taskInstanceId=" + taskInstanceId + ")");
		Date ara = new Date();
		List<TerminiIniciat> terminis = terminiIniciatRepository.findByTaskInstanceId(taskInstanceId);
		for (TerminiIniciat termini: terminis) {
			for (Alerta alerta: termini.getAlertes()) {
				alerta.setDataEliminacio(ara);
			}
		}
	}

	@Override
	public void expedientModificarEstat(
			String processInstanceId,
			String estatCodi) {
		logger.debug("Modificant estat de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"estatCodi=" + estatCodi + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		Estat estat = estatRepository.findByExpedientTipusAndCodiAmbHerencia(
				expedient.getTipus().getId(), 
				estatCodi);
		if (estat == null)
			throw new NoTrobatException(Estat.class, estatCodi);
		expedientHelper.update(
				expedient,
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				estat.getId(),
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				false);
	}

	@Override
	public void expedientModificarComentari(
			String processInstanceId,
			String comentari) {
		logger.debug("Modificant comentari de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"comentari=" + comentari + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.update(
				expedient,
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				comentari,
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				false);
	}

	@Override
	public void expedientModificarGeoref(
			String processInstanceId,
			Double posx,
			Double posy,
			String referencia) {
		logger.debug("Modificant georeferència de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"posx=" + posx + ", " +
				"posy=" + posy + ", " +
				"referencia=" + referencia + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.update(
				expedient,
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				posx,
				posy,
				referencia,
				expedient.getGrupCodi(),
				false);
	}

	@Override
	public void expedientModificarGrup(
			String processInstanceId,
			String grupCodi) {
		logger.debug("Modificant grup de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"grupCodi=" + grupCodi + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.update(
				expedient,
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				grupCodi,
				false);
	}

	@Override
	public void expedientModificarNumero(
			String processInstanceId,
			String numero) {
		logger.debug("Modificant número de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"numero=" + numero + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.update(
				expedient,
				numero,
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				false);
	}

	@Override
	public void expedientModificarResponsable(
			String processInstanceId,
			String responsableCodi) {
		logger.debug("Modificant responsable de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"responsableCodi=" + responsableCodi + ")");
		if (pluginHelper.personaFindAmbCodi(responsableCodi) == null)
			throw new NoTrobatException(PersonaDto.class, responsableCodi);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.update(
				expedient,
				expedient.getNumero(),
				expedient.getTitol(),
				responsableCodi,
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				false);
	}

	@Override
	public void expedientModificarTitol(
			String processInstanceId,
			String titol) {
		logger.debug("Modificant títol de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"titol=" + titol + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.update(
				expedient,
				expedient.getNumero(),
				titol,
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				false);
	}

	@Override
	public void expedientAturar(
			String processInstanceId,
			String motiu) {
		logger.debug("Aturant expedient (processInstanceId=" + processInstanceId + ", motiu=" + motiu + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.aturar(
				expedient,
				motiu,
				null);
	}

	@Override
	public void expedientReprendre(
			String processInstanceId) {
		logger.debug("Reprenent expedient (processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.reprendre(
				expedient,
				null);
	}

	@Override
	public boolean expedientReindexar(
			String processInstanceId) {
		logger.debug("Reindexant expedient (processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		return indexHelper.expedientIndexLuceneRecrear(expedient);
	}

	@Override
	public void expedientEliminaInformacioRetroaccio(
			String processInstanceId) {
		logger.debug("Buidant logs expedient (processInstanceId=" + processInstanceId + ")");
//		Long expId = workflowEngineApi.expedientFindByProcessInstanceId(processInstanceId);
//		if (expId == null)
//			throw new NoTrobatException(WProcessInstance.class, processInstanceId);
//		Expedient expedient = expedientRepository.getById(expId);
		WProcessInstance pi = workflowEngineApi.getRootProcessInstance(processInstanceId);
		if (pi == null)
			throw new NoTrobatException(WProcessInstance.class, processInstanceId);
		workflowRetroaccioApi.eliminaInformacioRetroaccioProces(pi.getId()); //expedient.getProcessInstanceId());
	}

	@Override
	public ArxiuDto documentGenerarAmbPlantilla(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			Date dataDocument) {
		logger.debug("Generant document amb plantilla (" +
				"taskInstanceId=" + taskInstanceId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"dataDocument=" + dataDocument + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, processInstanceId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		ExpedientTipus expedientTipus = expedient.getTipus();
		Document document;
		if (expedientTipus.isAmbInfoPropia())
			document = documentRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(),
					documentCodi,
					expedientTipus.getExpedientTipusPare() != null);
		else
			document = documentRepository.findByDefinicioProcesAndCodi(
					definicioProces, 
					documentCodi);
		if (document == null) {
			throw new NoTrobatException(Document.class, documentCodi);
		}
		ArxiuDto generat = documentHelper.generarDocumentAmbPlantillaIConvertir(
				expedient,
				document,
				taskInstanceId,
				processInstanceId,
				dataDocument);
		documentHelper.crearDocument(
				taskInstanceId,
				processInstanceId,
				document.getCodi(),
				dataDocument,
				false,
				null,
				generat.getNom(),
				generat.getContingut(),
				null,
				null,
				null,
				null);
		return generat;
	}

	@Override
	public void documentFirmaServidor(
			String processInstanceId,
			String documentCodi,
			String motiu) throws Exception {
		
		Long documentStoreId = documentHelper.findDocumentStorePerInstanciaProcesAndDocumentCodi(
				processInstanceId, 
				documentCodi);
		
		documentHelper.firmaServidor(
				processInstanceId,
				documentStoreId,
				motiu);
	}

	@Override
	public void createDadesTasca(String taskId) throws Exception {
		tascaHelper.createDadesTasca(taskId);
	}

	@Override
	public TerminiDto getTerminiAmbProcessInstanceICodi(
			String processInstanceId,
			String terminiCodi) {
		logger.debug("Obtenint termini donada la instància de procés i el codi (" +
				"processInstanceId=" + processInstanceId + "," +
				"terminiCodi=" + terminiCodi + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		Termini termini = null;
		if (expedient.getTipus().isAmbInfoPropia()) {
			termini = terminiHelper.findAmbExpedientTipusICodi(
					expedient.getTipus(), 
					terminiCodi);
		} else {
			DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
			termini = terminiHelper.findAmbDefinicioProcesICodi(
					definicioProces,
					terminiCodi);
		}				
		if (termini == null)
			throw new NoTrobatException(Termini.class, terminiCodi);
		return conversioTipusServiceHelper.convertir(
				termini,
				TerminiDto.class);
	}

	@Override
	public TerminiIniciatDto getTerminiIniciatAmbProcessInstanceITerminiCodi(
			String processInstanceId,
			String terminiCodi) {
		logger.debug("Obtenint termini iniciat donada la instància de procés i el codi (" +
				"processInstanceId=" + processInstanceId + ", " +
				"terminiCodi=" + terminiCodi + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		TerminiIniciat terminiIniciat = null;
		if (expedient.getTipus().isAmbInfoPropia()) {
			terminiIniciat = terminiHelper.findIniciatAmbExpedientTipusICodi(
					expedient.getTipus(),
					processInstanceId,
					terminiCodi);
		} else {
			DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
			terminiIniciat = terminiHelper.findIniciatAmbDefinicioProcesICodi(
					definicioProces,
					processInstanceId,
					terminiCodi);
		}				
		if (terminiIniciat == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiCodi);
		return conversioTipusServiceHelper.convertir(
				terminiIniciat,
				TerminiIniciatDto.class);
	}

	@Override
	public void configurarTerminiIniciatAmbDadesJbpm(
			Long terminiIniciatId,
			String taskInstanceId,
			Long timerId) {
		logger.debug("Configurant termini iniciat (" +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"taskInstanceId=" + taskInstanceId + ", " +
				"timerId=" + timerId + ")");
		TerminiIniciat terminiIniciat = terminiHelper.findTerminiIniciatById(terminiIniciatId);
		if (terminiIniciat == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
		terminiIniciat.setTaskInstanceId(taskInstanceId);
		if (timerId != null)
			terminiIniciat.afegirTimerId(timerId.longValue());
	}

	@Override
	public Date terminiCalcularDataInici(
			Date fi,
			int anys,
			int mesos,
			int dies,
			boolean laborable,
			String processInstanceId) {
		logger.debug("Calculant data d'inici de termini a partir d'una data de fi (" +
				"fi=" + fi + ", " +
				"anys=" + anys + ", " +
				"mesos=" + mesos + ", " +
				"dies=" + dies + ", " +
				"laborable=" + laborable + ")");
		return terminiHelper.getDataIniciTermini(
				fi, 
				anys, 
				mesos, 
				dies, 
				laborable,
				processInstanceId);
	}

	@Override
	public Date terminiCalcularDataFi(
			Date inici,
			int anys,
			int mesos,
			int dies,
			boolean laborable,
			String processInstanceId) {
		logger.debug("Calculant data de fi de termini a partir d'una data d'inici (" +
				"inici=" + inici + ", " +
				"anys=" + anys + ", " +
				"mesos=" + mesos + ", " +
				"dies=" + dies + ", " +
				"laborable=" + laborable + ")");
		return terminiHelper.getDataFiTermini(inici, anys, mesos, dies, laborable, processInstanceId);
	}

	@Override
	public void terminiIniciar(
			String terminiCodi,
			String processInstanceId,
			Date data,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi) {
		logger.debug("Iniciant termini (" +
				"terminiCodi=" + terminiCodi + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"data=" + data + ", " +
				"anys=" + anys + ", " +
				"mesos=" + mesos + ", " +
				"dies=" + dies + ", " +
				"esDataFi=" + esDataFi + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
		Termini termini = terminiHelper.findAmbDefinicioProcesICodi(
				definicioProces,
				terminiCodi);
		if (termini == null)
			throw new NoTrobatException(Termini.class, terminiCodi);
		terminiHelper.iniciar(
				termini.getId(),
				processInstanceId,
				data,
				anys,
				mesos,
				dies,
				esDataFi,
				false);
		
	}

	@Override
	public void terminiIniciar(
			String terminiCodi,
			String processInstanceId,
			Date data,
			boolean esDataFi) {
		logger.debug("Iniciant termini (" +
				"terminiCodi=" + terminiCodi + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"data=" + data + ", " +
				"esDataFi=" + esDataFi + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
		ExpedientTipus expedientTipus = definicioProces.getExpedientTipus() != null? 
											definicioProces.getExpedientTipus() 
											: null;

		Termini termini = null;
		if (expedientTipus != null && expedientTipus.isAmbInfoPropia()) {
			termini = terminiHelper.findAmbExpedientTipusICodi(
					expedientTipus, 
					terminiCodi);
		} else {
			termini = terminiHelper.findAmbDefinicioProcesICodi(
					definicioProces,
					terminiCodi);
		}				
		if (termini == null)
			throw new NoTrobatException(Termini.class, terminiCodi);
		terminiHelper.iniciar(
				termini.getId(),
				processInstanceId,
				data,
				esDataFi,
				false);
	}

	@Override
	public void terminiCancelar(
			Long terminiIniciatId,
			Date data) {
		logger.debug("Cancelant termini iniciat (" +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"data=" + data + ")");
		TerminiIniciat termini = terminiHelper.findTerminiIniciatById(terminiIniciatId);
		if (termini == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
		terminiHelper.cancelar(terminiIniciatId, data, false);
	}

	@Override
	public void terminiPausar(
			Long terminiIniciatId,
			Date data) {
		logger.debug("Pausant termini iniciat (" +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"data=" + data + ")");
		TerminiIniciat termini = terminiHelper.findTerminiIniciatById(terminiIniciatId);
		if (termini == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
		terminiHelper.pausar(terminiIniciatId, data, false);
	}

	@Override
	public void terminiContinuar(
			Long terminiIniciatId,
			Date data) {
		logger.debug("Continuant termini iniciat (" +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"data=" + data + ")");
		TerminiIniciat termini = terminiHelper.findTerminiIniciatById(terminiIniciatId);
		if (termini == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
		terminiHelper.continuar(terminiIniciatId, data, false);
	}

	@Override
	public List<DominiRespostaFilaDto> dominiConsultar(
			String processInstanceId,
			String dominiCodi,
			String dominiId,
			Map<String, Object> parametres) {
		logger.debug("Executant una consulta de domini (" +
				"processInstanceId=" + processInstanceId + ", " +
				"dominiCodi=" + dominiCodi + ", " +
				"dominiId=" + dominiId + ", " +
				"parametres=" + parametres + ")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		DominiDto domini;
		// Dominis del tipus d'expedient
		domini = dominiMs.findByExpedientTipusAndCodiAmbHerencia(
				expedient.getEntorn().getId(),
				expedient.getTipus().getId(),
				expedient.getTipus().getTipusPareId(),
				dominiCodi);
		// Si no el troba el busca a l'entorn
		if (domini == null)
			domini = dominiMs.findAmbCodi(
					expedient.getEntorn().getId(), 
					null, 
					dominiCodi);
		if (domini == null)
			throw new NoTrobatException(DominiDto.class, dominiCodi);
		List<FilaResultat> files = dominiHelper.consultar(
				domini.getId(),
				dominiId,
				parametres);
		List<DominiRespostaFilaDto> resposta = new ArrayList<DominiRespostaFilaDto>();
		if (files != null) {
			for (FilaResultat fila: files) {
				DominiRespostaFilaDto filaDto = new DominiRespostaFilaDto();
				for (ParellaCodiValor columna: fila.getColumnes()) {
					DominiRespostaColumnaDto columnaDto = new DominiRespostaColumnaDto();
					columnaDto.setCodi(columna.getCodi());
					columnaDto.setValor(columna.getValor());
					filaDto.getColumnes().add(columnaDto);
				}
				resposta.add(filaDto);
			}
		}
		return resposta;
	}
	
	@Override
	public List<DominiRespostaFilaDto> dominiInternConsultar(
			String processInstanceId,
			String dominiId,
			Map<String, Object> parametres) throws Exception {
		logger.debug("Executant una consulta de domini (" +
				"processInstanceId=" + processInstanceId + ", " +
				"dominiId=" + dominiId + ", " +
				"parametres=" + parametres + ")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);

		List<ParellaCodiValor> paramsConsulta = new ArrayList<ParellaCodiValor>();
		paramsConsulta.add(
				new ParellaCodiValor(
						"entorn",
						expedient.getEntorn().getCodi()));
		if (parametres != null) {
			for (String codi: parametres.keySet()) {
				paramsConsulta.add(new ParellaCodiValor(
						codi,
						parametres.get(codi)));
			}
		}
		
		List<FilaResultat> files = dominiHelper.consultaDominiIntern(
				dominiId,
				paramsConsulta);
		List<DominiRespostaFilaDto> resposta = new ArrayList<DominiRespostaFilaDto>();
		if (files != null) {
			for (FilaResultat fila: files) {
				DominiRespostaFilaDto filaDto = new DominiRespostaFilaDto();
				for (ParellaCodiValor columna: fila.getColumnes()) {
					DominiRespostaColumnaDto columnaDto = new DominiRespostaColumnaDto();
					columnaDto.setCodi(columna.getCodi());
					columnaDto.setValor(columna.getValor());
					filaDto.getColumnes().add(columnaDto);
				}
				resposta.add(filaDto);
			}
		}
		return resposta;
	}

	@Override
	public List<EnumeracioValorDto> enumeracioConsultar(
			String processInstanceId,
			String enumeracioCodi) {
		logger.debug("Consultant els valors d'una enumeració (" +
				"processInstanceId=" + processInstanceId + ", " +
				"enumeracioCodi=" + enumeracioCodi + ")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		Enumeracio enumeracio = enumeracioRepository.findByEntornAndExpedientTipusAndCodi(
				expedient.getEntorn(),
				expedient.getTipus(),
				enumeracioCodi);
		if (enumeracio == null) {
			enumeracio = enumeracioRepository.findByEntornAndCodi(
					expedient.getEntorn(),
					enumeracioCodi);
		}
		if (enumeracio == null)
			throw new NoTrobatException(Enumeracio.class, enumeracioCodi);
		return conversioTipusServiceHelper.convertirList(
				enumeracio.getEnumeracioValors(),
				EnumeracioValorDto.class);
	}
	

	@Override
	public void enumeracioSetValor(
			String processInstanceId,
			String enumeracioCodi,
			String codi,
			String valor) throws NoTrobatException {
		logger.debug("Fixant el valor d'una enumeració (" +
				"processInstanceId=" + processInstanceId + ", " +
				"enumeracioCodi=" + enumeracioCodi + ", " +
				"codi=" + codi + ", " +
				"valor=" + valor + ")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		Enumeracio enumeracio = enumeracioRepository.findByEntornAndExpedientTipusAndCodi(
				expedient.getEntorn(),
				expedient.getTipus(),
				enumeracioCodi);
		if (enumeracio == null) {
			enumeracio = enumeracioRepository.findByEntornAndCodi(
					expedient.getEntorn(),
					enumeracioCodi);
		}
		if (enumeracio == null)
			throw new NoTrobatException(Enumeracio.class, enumeracioCodi);
		
		EnumeracioValors enumeracioValor = enumeracioValorsRepository.findByEnumeracioAndCodi(enumeracio, codi);
		if (enumeracioValor == null)
			throw new NoTrobatException(EnumeracioValors.class, codi);
		enumeracioValor.setNom(valor);		
		enumeracioValorsRepository.save(enumeracioValor);
	}

	@Override
	public List<CampTascaDto> findCampsPerTaskInstance(
			String taskInstanceId) {
		logger.debug("Consultant els camps del formulari de la tasca (" +
				"taskInstanceId=" + taskInstanceId + ")");
		WTaskInstance task = workflowEngineApi.getTaskById(taskInstanceId);
		if (task == null)
			throw new NoTrobatException(WTaskInstance.class, taskInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, task.getProcessDefinitionId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getTaskName(),
				definicioProces);
		if (tasca == null)
			throw new NoTrobatException(Tasca.class, task.getTaskName());
		ExpedientTipus tipus = expedientTipusHelper.findAmbProcessInstanceId(task.getProcessInstanceId());
		if (tipus == null)
			throw new NoTrobatException(ExpedientTipus.class, task.getTaskName());
		return conversioTipusServiceHelper.convertirList(
				campTascaRepository.findAmbTascaIdOrdenats(tasca.getId(), tipus.getId()),
				CampTascaDto.class);
	}

	@Override
	public List<DocumentTascaDto> findDocumentsPerTaskInstance(
			String taskInstanceId) {
		logger.debug("Consultant els documents de la tasca (taskInstanceId=" + taskInstanceId + ")");
		WTaskInstance task = workflowEngineApi.getTaskById(taskInstanceId);
		if (task == null)
			throw new NoTrobatException(WTaskInstance.class, taskInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, task.getProcessDefinitionId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getTaskName(),
				definicioProces);
		if (tasca == null)
			throw new NoTrobatException(Tasca.class, task.getTaskName());
		ExpedientTipus tipus = expedientTipusHelper.findAmbProcessInstanceId(task.getProcessInstanceId());
		if (tipus == null)
			throw new NoTrobatException(ExpedientTipus.class, task.getTaskName());
		return conversioTipusServiceHelper.convertirList(
				documentTascaRepository.findAmbTascaOrdenats(tasca.getId(), tipus.getId()),
				DocumentTascaDto.class);
	}

	@Override
	public String getCodiVariablePerDocumentCodi(String documentCodi) {
		logger.debug("Obtenint el codi de variable jBPM pel document (" +
				"documentCodi=" + documentCodi + ")");
		return documentHelper.getVarPerDocumentCodi(documentCodi, false);
	}

	@Override
	public DocumentDto getDocumentInfo(Long documentStoreId) {
		logger.debug("Obtenint informació del document (" +
				"documentStoreId=" + documentStoreId + ")");
		return this.getDocumentInfo(
						documentStoreId,
						false,
						false,
						false,
						false,
						false, // Per notificar
						false);
	}

	@Override
	public DocumentDto getDocumentInfo(Long documentStoreId,
			boolean ambContingutOriginal,
			boolean ambContingutSignat,
			boolean ambContingutVista,
			boolean perSignar,
			boolean perNotificar,
			boolean ambSegellSignatura) {
		logger.debug("Obtenint informació del document (" +
				"documentStoreId=" + documentStoreId + ", " + 
				"ambContingutOriginal=" + ambContingutOriginal + ", " + 
				"ambContingutSignat=" + ambContingutSignat + ", " + 
				"ambContingutVista=" + ambContingutVista + ", " + 
				"perSignar=" + perSignar + ", " + 
				"ambSegellSignatura=" + ambSegellSignatura + ")");
		return conversioTipusServiceHelper.convertir(
				documentHelper.toDocumentDto(
						documentStoreId,
						ambContingutOriginal,
						ambContingutSignat,
						ambContingutVista,
						ambContingutVista,
						perNotificar, // Per notificar
						ambSegellSignatura),
				DocumentDto.class);
	}

	@Override
	public ArxiuDto getArxiuPerMostrar(Long documentStoreId) {
		logger.debug("Obtenint arxiu del document (" +
				"documentStoreId=" + documentStoreId + ")");
		DocumentDto document = documentHelper.toDocumentDto(
				documentStoreId,
				false,
				false,
				false,
				false,
				false, // Per notificar
				false);
		if (document == null) {
			return null;
		}
		return documentHelper.getArxiuPerDocumentStoreId(
				documentStoreId,
				false,
				false);
	}

	@Override
	public Long documentExpedientGuardar(
			String processInstanceId,
			String documentCodi,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) throws Exception {
		logger.debug("Guardant un document a dins l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"data=" + data + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ")");
		return documentHelper.crearActualitzarDocument(
				null,
				processInstanceId,
				documentCodi,
				data,
				arxiuNom,
				arxiuContingut,
				null,
				null,
				null,
				null);
	}

	@Override
	public void desfinalitzarExpedient(String processInstanceId) throws Exception{
		logger.debug("Desfinalitzant expedient(processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.desfinalitzar(
				expedient,
				null);
	}
	
	@Override
	public void finalitzarExpedient(String processInstanceId) throws Exception {
		logger.debug("Finalitzant expedient (processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.finalitzar(expedient.getId());
	}
	
	
	
	@Override
	public boolean tokenActivar(String tokenId, boolean activar) {
		logger.debug("tokenActivar (" +
				"tokenId=" + tokenId + ", " +
				"activar=" + activar + ")");
		try {
			return workflowEngineApi.tokenActivar(tokenId, activar);
		} catch (Exception ex) {
			return false;
		} 
	}

	@Override
	public Long documentExpedientAdjuntar(
			String processInstanceId,
			String adjuntId,
			String adjuntTitol,
			Date adjuntData,
			String arxiuNom,
			byte[] arxiuContingut) {
		logger.debug("Guardant un document adjunt a dins l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"adjuntId=" + adjuntId + ", " +
				"adjuntTitol=" + adjuntTitol + ", " +
				"adjuntData=" + adjuntData + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ")");
		return documentHelper.crearDocument(
				null,
				processInstanceId,
				null,
				adjuntData,
				true,
				adjuntTitol,
				arxiuNom,
				arxiuContingut,
				null,
				null,
				null,
				null);
	}

	@Override
	public void documentExpedientEsborrar(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) throws Exception {
		logger.debug("Esborrant un document de dins l'expedient (" +
				"taskInstanceId=" + taskInstanceId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ")");
		documentHelper.esborrarDocument(
				taskInstanceId,
				processInstanceId,
				documentCodi);
	}

	@Override
	public void documentExpedientGuardarDadesRegistre(
			Long documentStoreId,
			String registreNumero,
			Date registreData,
			String registreOficinaCodi,
			String registreOficinaNom,
			boolean registreEntrada) {
		logger.debug("Esborrant un document de dins l'expedient (" +
				"documentStoreId=" + documentStoreId + ", " +
				"registreNumero=" + registreNumero + ", " +
				"registreData=" + registreData + ", " +
				"registreOficinaCodi=" + registreOficinaCodi + ", " +
				"registreOficinaNom=" + registreOficinaNom + ", " +
				"registreEntrada=" + registreEntrada + ")");
		documentHelper.guardarDadesRegistre(
				documentStoreId,
				registreNumero,
				registreData,
				registreOficinaCodi,
				registreOficinaNom,
				registreEntrada);
	}

	@Override
	public void emailSend(
			String fromAddress,
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<ArxiuDto> attachments)  {
		logger.debug("Enviant correu (" +
				"fromAddress=" + fromAddress + ", " +
				"recipients=" + recipients + ", " +
				"ccRecipients=" + ccRecipients + ", " +
				"bccRecipients=" + bccRecipients + ", " +
				"subject=" + subject + ", " +
				"text=" + text + ")");
		try {
			mailHelper.send(
					fromAddress,
					recipients,
					ccRecipients,
					bccRecipients,
					subject,
					text,
					conversioTipusServiceHelper.convertirList(
							attachments,
							ArxiuDto.class));
		} catch (Exception e) {
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
					"(Enviament de mail '" + subject + "')", 
					e);
		}
	}

	@Override
	public boolean isRegistreActiu() {
		logger.debug("Comprovant si el plugin de registre està actiu");
		return pluginHelper.registreIsPluginActiu();
	}
	
	@Override
	public boolean isRegistreRegWeb3Actiu() {
		logger.debug("Comprovant si el plugin de registre està actiu");
		return pluginHelper.registreIsPluginRebWeb3Actiu();
	}

	@Override
	public RegistreIdDto registreAnotacioEntrada(
			RegistreAnotacioDto anotacio,
			Long expedientId) {
		Expedient expedient = expedientHelper.findById(expedientId);
		
		RegistreIdDto respostaPlugin = pluginHelper.registreAnotacioEntrada(
				anotacio,
				expedient);
		RegistreIdDto resposta = new RegistreIdDto();
		resposta.setNumero(respostaPlugin.getNumero());
		resposta.setData(respostaPlugin.getData());
		return resposta;
	}

	@Override
	public RegistreIdDto registreAnotacioSortida(
			RegistreAnotacioDto anotacio,
			Long expedientId) {
		Expedient expedient = expedientHelper.findById(expedientId);
		
		RegistreIdDto respostaPlugin = pluginHelper.registreAnotacioSortida(
				anotacio,
				expedient);
		RegistreIdDto resposta = new RegistreIdDto();
		resposta.setNumero(respostaPlugin.getNumero());
		resposta.setData(respostaPlugin.getData());
		return resposta;
	}

	@Override
	public Date registreNotificacioComprovarRecepcio(
			String registreNumero,
			Long expedientId) {
		Expedient expedient = null;
		if (expedientId != null) {
			expedient = expedientHelper.findById(expedientId);

		}
		
		return pluginHelper.registreDataJustificantRecepcio(registreNumero, expedient);
	}

	@Override
	public String registreObtenirOficinaNom(
			String oficinaCodi,
			Long expedientId) {
		Expedient expedient = expedientHelper.findById(expedientId);
		
		return pluginHelper.registreOficinaNom(oficinaCodi, expedient);
	}
	
	@Override
	public String registreObtenirOficinaNom(
			String numRegistre,
			String usuariCodi,
			String entitatCodi,
			Long expedientId) {
		Expedient expedient = expedientHelper.findById(expedientId);
		
		return pluginHelper.registreOficinaNom(
				numRegistre, 
				usuariCodi,
				entitatCodi,
				expedient);
	}

	@Override
	public RegistreIdDto notificacioCrear(
			RegistreNotificacioDto notificacio,
			Long expedientId,
			boolean crearExpedient) {
		Expedient expedient = expedientHelper.findById(expedientId);
		
		RegistreNotificacio registreNotificacio = new RegistreNotificacio();
		DadesOficina dadesOficina = new DadesOficina();
		dadesOficina.setOrganCodi(notificacio.getOrganCodi());
		dadesOficina.setOficinaCodi(notificacio.getOficinaCodi());
		registreNotificacio.setDadesOficina(dadesOficina);
		DadesInteressat dadesInteressat = new DadesInteressat();
		dadesInteressat.setAutenticat(true);
		dadesInteressat.setEntitatCodi(notificacio.getEntitatCodi());
		dadesInteressat.setNomAmbCognoms(notificacio.getInteressatNomAmbCognoms());
		dadesInteressat.setNom(notificacio.getInteressatNom());
		dadesInteressat.setCognom1(notificacio.getInteressatCognom1());
		dadesInteressat.setCognom2(notificacio.getInteressatCognom2());
		dadesInteressat.setMunicipiCodi(notificacio.getInteressatMunicipiCodi());
		dadesInteressat.setMunicipiNom(notificacio.getInteressatMunicipiNom());
		dadesInteressat.setProvinciaCodi(notificacio.getInteressatProvinciaCodi());
		dadesInteressat.setProvinciaNom(notificacio.getInteressatProvinciaNom());
		dadesInteressat.setPaisCodi(notificacio.getInteressatPaisCodi());
		dadesInteressat.setPaisNom(notificacio.getInteressatPaisNom());
		dadesInteressat.setNif(notificacio.getInteressatNif());
		dadesInteressat.setEmail(notificacio.getInteressatEmail());
		dadesInteressat.setMobil(notificacio.getInteressatMobil());
		registreNotificacio.setDadesInteressat(dadesInteressat);
		DadesExpedient dadesExpedient = new DadesExpedient();
		dadesExpedient.setIdentificador(notificacio.getExpedientIdentificador());
		dadesExpedient.setClau(notificacio.getExpedientClau());
		dadesExpedient.setUnitatAdministrativa(notificacio.getExpedientUnitatAdministrativa());
		registreNotificacio.setDadesExpedient(dadesExpedient);		
		DadesAssumpte dadesAssumpte = new DadesAssumpte();
		String idiomaExtracte = notificacio.getAssumpteIdiomaCodi();
		dadesAssumpte.setAssumpte(notificacio.getAssumpteExtracte());
		dadesAssumpte.setIdiomaCodi(
				(idiomaExtracte != null) ? idiomaExtracte : "ca");
		dadesAssumpte.setTipus(
				notificacio.getAssumpteTipus());
		dadesAssumpte.setRegistreNumero(
				notificacio.getAssumpteRegistreNumero());
		dadesAssumpte.setRegistreAny(
				notificacio.getAssumpteRegistreAny());
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
		registreNotificacio.setDadesNotificacio(dadesNotificacio);
		if (notificacio.getAnnexos() != null) {
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			for (RegistreAnnexDto annex: notificacio.getAnnexos()) {
				DocumentRegistre document = new DocumentRegistre();
				document.setNom(annex.getNom());
				document.setIdiomaCodi((annex.getIdiomaCodi() != null) ? annex.getIdiomaCodi() : "ca");
				document.setData(annex.getData());
				document.setArxiuNom(annex.getArxiuNom());
				document.setArxiuContingut(annex.getArxiuContingut());
				documents.add(document);
			}
			registreNotificacio.setDocuments(documents);
		}
		
		logger.info("###===> INICIANT MÈTODES PER A REGISTRAR NOTIFICACIÓ.");
		
		RespostaAnotacioRegistre respostaPlugin = pluginHelper.tramitacioRegistrarNotificacio(
			registreNotificacio,
			expedient,
			crearExpedient);
		
		logger.info("###===> Resposta registre notificacio plugin: ");
		logger.info("###========> Numero: " + respostaPlugin.getNumero());
		logger.info("###========> Data: " + respostaPlugin.getData());
		
		if (respostaPlugin.getReferenciaRDSJustificante() != null) {
			logger.info("###========> Just.Codi: " + respostaPlugin.getReferenciaRDSJustificante().getCodigo());
			logger.info("###========> Just.Clau: " + respostaPlugin.getReferenciaRDSJustificante().getClave());
		}
		
	
		if (respostaPlugin.isOk()) {
			RegistreIdDto resposta = new RegistreIdDto();
			resposta.setNumero(respostaPlugin.getNumero());
			resposta.setData(respostaPlugin.getData());
			ReferenciaRDSJustificanteDto referenciaRDSJustificante = new ReferenciaRDSJustificanteDto();
			referenciaRDSJustificante.setClave(respostaPlugin.getReferenciaRDSJustificante().getClave());
			referenciaRDSJustificante.setCodigo(respostaPlugin.getReferenciaRDSJustificante().getCodigo());
			resposta.setReferenciaRDSJustificante(referenciaRDSJustificante);			
			return resposta;
		} else {
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
					"(Registre data de justificant)", 
					"[" + respostaPlugin.getErrorCodi() + "]: " + respostaPlugin.getErrorDescripcio());
		}
	}

	@Override
	public RespostaJustificantRecepcioDto notificacioElectronicaJustificant(
			String registreNumero) {
		RespostaJustificantRecepcio resposta = pluginHelper.tramitacioObtenirJustificant(
				registreNumero);
		if (!resposta.isError()) {
			return conversioTipusServiceHelper.convertir(resposta, RespostaJustificantRecepcioDto.class);
		} else {
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
					"(Justificació de recepció)", 
					"[" + resposta.getErrorCodi() + "]: " + resposta.getErrorDescripcio());
		}
	}

	@Override
	public RespostaJustificantDetallRecepcioDto notificacioElectronicaJustificantDetall(
			String registreNumero) {
		RespostaJustificantDetallRecepcio resposta =  pluginHelper.tramitacioObtenirJustificantDetall(
				registreNumero);
		if (!resposta.isError()) {
			return conversioTipusServiceHelper.convertir(resposta, RespostaJustificantDetallRecepcioDto.class);
		} else {
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
					"(Justificació de recepció detallada)", 
					"[" + resposta.getErrorCodi() + "]: " + resposta.getErrorDescripcio());
		}
	}

	@Override
	public void notificacioGuardar(
			ExpedientDto expedient,
			NotificacioDto notificacio) {
		logger.debug("Guardant una notificació de l'expedient (" +
				"expedientId=" + expedient.getId() + ", " +
				"numero=" + notificacio.getRegistreNumero() + ", " +
				"data=" + notificacio.getEnviamentData() + ", " +
				"RDSClave=" + notificacio.getRdsClau() + ", " +
				"RDSCodigo=" + notificacio.getRdsCodi() + ")");
		notificacioElectronicaHelper.create(
				expedient,
				notificacio);
	}

	@Override
	public boolean notificacioEsborrar(
			String numero,
			String clave,
			Long codigo) {
		logger.debug("Esborrar una notificació de l'expedient (" +
				"numero=" + numero + ", " +
				"RDSClave=" + clave + ", " +
				"RDSCodigo=" + codigo + ")");
		return notificacioElectronicaHelper.delete(
				numero,
				clave,
				codigo);
	}
	
	@Override
	public RespostaNotificacio altaNotificacio(DadesNotificacioDto dadesNotificacio) {
		Expedient expedient = expedientHelper.findById(dadesNotificacio.getExpedientId());
		
		// Notifica i guarda la informació
		DocumentNotificacio notificacio = notificacioElectronicaHelper.altaNotificacio(expedient, dadesNotificacio);
		
		// Transforma la informació a una resposta
		RespostaNotificacio resposta = new RespostaNotificacio(); 
		resposta.setEstat(NotificacioEstat.valueOf(notificacio.getEstat().name()));
		resposta.setIdentificador(notificacio.getEnviamentIdentificador());
		List<ReferenciaNotificacio> referencies = new ArrayList<ReferenciaNotificacio>();
		// TODO: obtenir totes les referencies per cada enviament
		//for (notificacio.getEnviaments)
		ReferenciaNotificacio referencia = new ReferenciaNotificacio();
		referencia.setTitularNif(notificacio.getTitularNif());
		referencia.setReferencia(notificacio.getEnviamentReferencia());
		referencies.add(referencia);
		resposta.setReferencies(referencies);
		
		return resposta;
	}

	@Override
	public Integer portasignaturesEnviar(
			Long documentId,
			List<Long> annexosId,
			PersonaDto persona,
			List<PersonaDto> personesPas1,
			int minSignatarisPas1,
			List<PersonaDto> personesPas2,
			int minSignatarisPas2,
			List<PersonaDto> personesPas3,
			int minSignatarisPas3,
			Long expedientId,
			String importancia,
			Date dataLimit,
			Long tokenId,
			Long processInstanceId,
			String transicioOK,
			String transicioKO) {
		DocumentStore documentStore = documentStoreRepository.findById(documentId)
				.orElseThrow(() -> new NoTrobatException(DocumentStore.class, documentId));
		// Valida que no sigui ja un document firmat
		if (documentStore.isSignat()) 
			throw new ValidacioException("No es pot enviar a firmar al Portasignatures un document que ja està signat");
		DocumentDto document = documentHelper.toDocumentDto(
				documentId,
				false,
				false,
				true,
				true,
				false, // Per notificar
				(documentStore == null || documentStore.getArxiuUuid() == null) );
		List<DocumentDto> annexos = null;
		if (annexosId != null) {
			annexos = new ArrayList<DocumentDto>();
			for (Long docId: annexosId) {
				DocumentDto docDto = documentHelper.toDocumentDto(
						docId,
						false,
						false,
						true,
						false,
						false, // Per notificar
						false);
				if (docDto != null){
					annexos.add(docDto);
				}
			}
		}
		return pluginHelper.portasignaturesEnviar(
				document,
				annexos,
				persona,
				personesPas1,
				minSignatarisPas1,
				personesPas2,
				minSignatarisPas2,
				personesPas3,
				minSignatarisPas3,
				expedientHelper.findById(expedientId),
				importancia,
				dataLimit,
				tokenId,
				processInstanceId,
				transicioOK,
				transicioKO);
	}

	@Override
	public void portasignaturesEliminar(
			List<Integer> documentIds) {
		pluginHelper.portasignaturesCancelar(documentIds);
	}

	@Override
	public void zonaperExpedientCrear(
			ExpedientDto expedient,
			ZonaperExpedientDto dadesExpedient) {
		
			String identificador = expedient.getNumeroDefault();
			String clau = Long.toString(System.currentTimeMillis());
			dadesExpedient.setExpedientIdentificador(identificador);
			dadesExpedient.setExpedientClau(clau);
			pluginHelper.tramitacioZonaperExpedientCrear(expedient, dadesExpedient);
			Expedient ex = expedientHelper.findById(expedient.getId());
			ex.setTramitExpedientIdentificador(identificador);
			ex.setTramitExpedientClau(clau);
	}

	@Override
	public void zonaperEventCrear(
			String processInstanceId,
			ZonaperEventDto dadesEvent) {
		
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		pluginHelper.tramitacioZonaperEventCrear(expedient, dadesEvent);
	}

	@Override
	public TramitDto getTramit(String numero, String clau) {
		logger.debug("Obtenint dades del tràmit (numero=" + numero + ", clau=" + clau + ")");
		return conversioTipusServiceHelper.convertir(
				pluginHelper.tramitacioObtenirDadesTramit(numero, clau),
				TramitDto.class);
	}

	@Override
	public ArxiuDto getArxiuGestorDocumental(String id) {
		logger.debug("Obtenint arxiu de la gestió documental (id=" + id + ")");
		ArxiuDto arxiu = new ArxiuDto();
		arxiu.setContingut(
				pluginHelper.gestioDocumentalObtenirDocument(id));
		return arxiu;
	}

	@Override
	public EstatDto findEstatAmbEntornIExpedientTipusICodi(
			Long entornId,
			String expedientTipusCodi,
			String estatCodi) {
		logger.debug("Obtenint l'estat donat l'entorn, el tipus d'expedient i el codi (" +
				"entornId=" + entornId + ", " +
				"expedientTipusCodi=" + expedientTipusCodi + ", " +
				"estatCodi=" + estatCodi + ")");
		Optional<Entorn> entornOptional = entornRepository.findById(entornId);
		if (entornOptional.isPresent())
			throw new NoTrobatException(Entorn.class, entornId);
		Entorn entorn = entornOptional.get();

		ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
				entorn,
				expedientTipusCodi);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusCodi);
		return conversioTipusServiceHelper.convertir(
				estatRepository.findByExpedientTipusAndCodiAmbHerencia(
						expedientTipus.getId(), 
						estatCodi),
				EstatDto.class);
	}

	@Override
	public DocumentDissenyDto getDocumentDisseny(
			Long definicioProcesId,
			String processInstanceId,
			String documentCodi) {
		logger.debug("Obtenint el document de disseny donada la definició de procés i el codi (" +
				"definicioProcesId=" + definicioProcesId + ", " +
				"documentCodi=" + documentCodi + ")");
		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId)
				.orElseThrow(() -> new NoTrobatException(DefinicioProces.class, definicioProcesId));
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		ExpedientTipus expedientTipus = expedient.getTipus();
		
		if (expedientTipus.isAmbInfoPropia())
			return conversioTipusServiceHelper.convertir(
					documentRepository.findByExpedientTipusAndCodi(
							expedientTipus.getId(), 
							documentCodi,
							expedientTipus.getExpedientTipusPare() != null),
					DocumentDissenyDto.class);
		else
			return conversioTipusServiceHelper.convertir(
					documentRepository.findByDefinicioProcesAndCodi(
							definicioProces, 
							documentCodi),
					DocumentDissenyDto.class);
	}

	@Override
	public void expedientRelacionar(
			Long expedientIdOrigen,
			Long expedientIdDesti) {
		logger.debug("Relacionant els expedients (" +
				"expedientIdOrigen=" + expedientIdOrigen + ", " +
				"expedientIdDesti=" + expedientIdDesti + ")");
		Expedient origen = expedientHelper.findById(expedientIdOrigen);
		Expedient desti  = expedientHelper.findById(expedientIdDesti);
		expedientHelper.relacioCrear(
				origen,
				desti);
	}

	@Override
	public void tokenRedirigir(
			long tokenId,
			String nodeName,
			boolean cancelarTasques) {
		logger.debug("Redirigint el token (" +
				"tokenId=" + tokenId + ", " +
				"nodeName=" + nodeName + ", " +
				"cancelarTasques=" + cancelarTasques + ")");
		expedientHelper.tokenRetrocedir(
				Long.toString(tokenId),
				nodeName,
				cancelarTasques);
	}

	@Override
	public ExpedientDadaDto getDadaPerProcessInstance(
			String processInstanceId,
			String varCodi) throws Exception {
		logger.debug("Obtenint la dada de l'instància de procés (processInstanceId=" + processInstanceId + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(
				processInstanceId);
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, processInstanceId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		ExpedientTipus expedientTipus = expedient.getTipus();
		Camp camp;
		if (expedientTipus.isAmbInfoPropia()) {
			camp = campRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(),
					varCodi,
					expedientTipus.getExpedientTipusPare() != null);
		} else {
			camp = campRepository.findByDefinicioProcesAndCodi(
					definicioProces,
					varCodi);
		}
		
		if (camp == null)
			throw new NoTrobatException(Camp.class, varCodi);
		ExpedientDadaDto resposta = new ExpedientDadaDto();
		Object valor = workflowEngineApi.getProcessInstanceVariable(
				processInstanceId,
				varCodi);
		resposta.setText(
				variableHelper.getTextPerCamp(
						camp, 
						valor, 
						null, 
						null, 
						processInstanceId,
						null,
						null));
		return resposta;
	}
	
	@Override
	public TascaDadaDto getDadaPerTaskInstance(
			String processInstanceId,
			String taskInstanceId,
			String varCodi) throws Exception {
		logger.debug("Obtenint la dada de l'instància de tasca (taskInstanceId=" + taskInstanceId + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(
				processInstanceId);
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		ExpedientTipus expedientTipus = expedient.getTipus();
		Camp camp;
		if (expedientTipus.isAmbInfoPropia()) {
			camp = campRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(),
					varCodi,
					expedientTipus.getExpedientTipusPare() != null);
		} else {
			camp = campRepository.findByDefinicioProcesAndCodi(
					definicioProces,
					varCodi);
		}
		
		if (camp == null)
			throw new NoTrobatException(Camp.class,varCodi);
		TascaDadaDto resposta = new TascaDadaDto();
		Object valor = workflowEngineApi.getTaskInstanceVariable(
				taskInstanceId,
				varCodi);
		resposta.setText(
				variableHelper.getTextPerCamp(
						camp, 
						valor, 
						null, 
						taskInstanceId, 
						null,
						null,
						null));
		return resposta;
	}

	@Override
	public ExpedientDto findExpedientAmbMateixTipusINumero(
			Long entornId,
			Long expedientTipusId,
			String numero) {
		logger.debug("findExpedientsAmbMateixTipusINumero (" +
				"entornId=" + entornId + ", " +
				"numero=" + numero + ", " +
				"expedientTipusId=" + expedientTipusId + ")");
		Optional<Entorn> entornOptional = entornRepository.findById(entornId);
		if (entornOptional.isPresent())
			throw new NoTrobatException(Entorn.class, entornId);
		Entorn entorn = entornOptional.get();

		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId)
				.orElseThrow(() -> new NoTrobatException(ExpedientTipus.class, expedientTipusId));
		Expedient expedient = expedientRepository.findByEntornAndTipusAndNumero(
				entorn,
				expedientTipus,
				numero);
		if (expedient == null)
			return null;
		return conversioTipusServiceHelper.convertir(expedient, ExpedientDto.class);
	}

	@Override
	public List<ExpedientDto> findExpedientsConsultaGeneral(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long estatId,
			boolean nomesIniciats,
			boolean nomesFinalitzats) {
		logger.debug("Consultant expedients (" +
				"entornId=" + entornId + ", " +
				"titol=" + titol + ", " +
				"numero=" + numero + ", " +
				"dataInici1=" + dataInici1 + ", " +
				"dataInici2=" + dataInici2 + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"estatId=" + estatId + ", " +
				"nomesFinalitzats=" + nomesFinalitzats + ", " +
				"nomesFinalitzats=" + nomesFinalitzats + ")");
		Optional<Entorn> entornOptional = entornRepository.findById(entornId);
		if (entornOptional.isPresent())
			throw new NoTrobatException(Entorn.class, entornId);
		Entorn entorn = entornOptional.get();

		ExpedientTipus expedientTipus = null;
		Estat estat = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusRepository.findById(expedientTipusId)
					.orElseThrow(() -> new NoTrobatException(ExpedientTipus.class, expedientTipusId));
			if (estatId != null) {
				estat = estatRepository.findById(estatId)
						.orElseThrow(() -> new NoTrobatException(Estat.class, estatId));
			}
		}
		return conversioTipusServiceHelper.convertirList(
				expedientHelper.findByFiltreGeneral(
						entorn,
						titol,
						numero,
						dataInici1,
						dataInici2,
						expedientTipus,
						estat,
						nomesIniciats,
						nomesFinalitzats),
				ExpedientDto.class);
	}
	
	@Override
	public List<ExpedientDto> findExpedientsConsultaDadesIndexades(
			Long entornId, 
			String expedientTipusCodi, 
			Map<String, Object> filtreValors) {
		logger.debug("Consultant expedients a Lucene (" +
				"entornId=" + entornId + ", " +
				"expedientTipusCodi=" + expedientTipusCodi + ", " +
				"filtreValors=" + filtreValors + ")");
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		
		Optional<Entorn> entornOptional = entornRepository.findById(entornId);
		if (entornOptional.isPresent())
			throw new NoTrobatException(Entorn.class, entornId);
		Entorn entorn = entornOptional.get();

		// comprovar l'accés a l'entorn
		ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(entorn, expedientTipusCodi);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusCodi);
		// comprovar l'accés al tipus d'expedient

		// Construeix la llista de camps
		Map<String, Camp> campsIndexatsPerCodi = new HashMap<String, Camp>();
		Set<Camp> camps = null;
		if (expedientTipus.isAmbInfoPropia()) {
			if (expedientTipus.getExpedientTipusPare() != null) {
				// Camps heretats
				for (Camp camp: expedientTipus.getExpedientTipusPare().getCamps())
					campsIndexatsPerCodi.put(camp.getCodi(), camp);
			}
			camps = expedientTipus.getCamps();
		} else {
			// Troba la definició de procés principial
			for (DefinicioProces definicioProces : expedientTipus.getDefinicionsProces())
				if (expedientTipus.getJbpmProcessDefinitionKey() != null 
				&& expedientTipus.getJbpmProcessDefinitionKey().equals(definicioProces.getJbpmKey())) {
					camps = definicioProces.getCamps();
					break;
				}
		}
		// Els camps del TE o de la DP sobreescriuen els heretats
		if (camps != null)
			for (Camp camp: camps)
				campsIndexatsPerCodi.put(camp.getCodi(), camp);
		List<Camp> filtreCamps = new ArrayList<Camp>(campsIndexatsPerCodi.values());

		// consultar a l'índex
		List<Long> expedientsIds = indexHelper.findExpedientsIdsByFiltre(
				entorn,
				expedientTipus,
				filtreCamps,
				filtreValors);
		Expedient expedient;
		for (Long expedientId : expedientsIds) {
			expedient = expedientHelper.findAmbEntornIId(entornId, expedientId);
			if (expedient != null 
					&& !expedient.isAnulat())
				resposta.add(conversioTipusServiceHelper.convertir(expedient, ExpedientDto.class));
		}
		return resposta;
	}

	@Override
	@Transactional
	public void initializeDefinicionsProces() {
		List<ExpedientTipus> llistat = expedientTipusRepository.findAll();
		for (ExpedientTipus expedientTipus: llistat) {
			Hibernate.initialize(expedientTipus.getDefinicionsProces());
		}
	}

	// TODO: Mètriques
//	@Override
//	public boolean mesuraIsActiu() {
//		return MesuresTemporalsHelper.isActiu();
//	}
//	@Override
//	public void mesuraIniciar(String clau, String familia, String tipusExpedient, String tasca, String detall) {
//		mesuresTemporalsHelper.mesuraIniciar(clau, familia, tipusExpedient, tasca, detall);
//	}
//	@Override
//	public void mesuraCalcular(String clau, String familia, String tipusExpedient, String tasca, String detall) {
//		mesuresTemporalsHelper.mesuraCalcular(clau, familia, tipusExpedient, tasca, detall);
//	}

	@Override
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateExpedientError(
			Long jobId,
			Long expedientId,
			String errorDesc,
			String errorFull) {
		logger.error("JOB (" + jobId + "): Actualitzant error de l'expedient");
//		if (jobId != null)
//			workflowEngineApi.retryJob(jobId);
		Expedient expedient = expedientHelper.findById(expedientId);
		expedient.setErrorDesc(errorDesc);
		expedient.setErrorFull(errorFull);
		expedientRepository.save(expedient);
	}

	@Override
	public String getHeliumProperty(String propertyName) {
		return globalProperties.getProperty(propertyName);
	}

//	@Override
//	public MetricRegistry getMetricRegistry() {
//		return metricRegistry;
//	}
	
	@Override
	public List<String> getRolsByCodi(String codi) {
		List<String> rols = new ArrayList<String>();
		try {
			if (pluginHelper.personaIsPluginActiu())
				rols = pluginHelper.personaFindRolsAmbCodi(codi);
		} catch (Exception ex) {
			// En cas que hi hagi una excepció,retornarem la llista buida 
			logger.error("Error consultant els ros per l'usuari " + codi, ex);
		}
		return rols;
	}
	
	@Override
	public void setErrorTascaSegonPla(String taskId, Exception ex) {
		if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
			Map<String, InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
			if (map.containsKey(taskId)) {
				map.get(taskId).setError((ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
			}
		}
	}

	@Override
	public String getTaskInstanceIdByTokenId(Long tokenId) {
		return workflowEngineApi.getTaskInstanceIdByExecutionTokenId(String.valueOf(tokenId));
	}
	
	@Override
	public void addMissatgeExecucioTascaSegonPla(String taskId, String[] message) {
		if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
			Map<String, InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
			if (map.containsKey(taskId)) {
				map.get(taskId).addMessage(message);
			}
		}
	}
	
	@Override
	public boolean isTascaEnSegonPla(String taskId) {
		boolean result = false;
		if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
			Map<String, InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
			result = map.containsKey(taskId);
		}
		
		return result;
	}

	@Override
	public void afegirInstanciaProcesPerVerificarFinalitzacio(String processInstanceId) {
		ThreadLocalInfo.addProcessInstanceFinalitzatIds(processInstanceId);
	}



	private Expedient getExpedientDonatProcessInstanceId(
			String processInstanceId) {
		return expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
	}
	private DefinicioProces getDefinicioProcesDonatProcessInstanceId(
			String processInstanceId) {
		WProcessInstance processInstance = workflowEngineApi.getProcessInstance(processInstanceId);
		if (processInstance == null)
			throw new NoTrobatException(WProcessInstance.class, processInstanceId);

		return definicioProcesRepository.findByJbpmId(
				processInstance.getProcessDefinitionId());
	}

	private Entorn getEntornDonatProcessInstanceId(
			String processInstanceId) {
		WProcessInstance processInstance = workflowEngineApi.getRootProcessInstance(processInstanceId);
		if (processInstance == null)
			throw new NoTrobatException(WProcessInstance.class, processInstanceId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		return expedient.getEntorn();
	}

	@Override
	public List<DefinicioProcesDto> findSubDefinicionsProces(Long definicioProcesId) {
		List<DefinicioProcesDto> resposta = new ArrayList<DefinicioProcesDto>();
		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId).get();
		for (WProcessDefinition pd : workflowEngineApi.getSubProcessDefinitions(definicioProces.getJbpmId())) {
			resposta.add(conversioTipusServiceHelper.convertir(
					definicioProcesRepository.findByJbpmId(pd.getId()),
					DefinicioProcesDto.class));
		}
		return resposta;
	}

	@Override
	public RegistreIdDto registreAnotacioSortida(RegistreAnotacio anotacio, Long expedientId) {
		
		Expedient expedient = expedientHelper.findById(expedientId);
		
		RegistreIdDto respostaPlugin = pluginHelper.registreAnotacioSortida(
				anotacio,
				expedient);
		RegistreIdDto resposta = new RegistreIdDto();
		resposta.setNumero(respostaPlugin.getNumero());
		resposta.setData(respostaPlugin.getData());
		return resposta;
	}

	private static final Logger logger = LoggerFactory.getLogger(Jbpm3HeliumHelper.class);
}