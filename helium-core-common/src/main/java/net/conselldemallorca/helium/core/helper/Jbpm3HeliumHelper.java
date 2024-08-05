/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;

import org.hibernate.Hibernate;
import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;

import net.conselldemallorca.helium.core.common.ThreadLocalInfo;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.helper.TascaSegonPlaHelper.InfoSegonPla;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Area;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentNotificacio;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Interessat;
import net.conselldemallorca.helium.core.model.hibernate.PeticioPinbal;
import net.conselldemallorca.helium.core.model.hibernate.Reassignacio;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.StringUtilsHelium;
import net.conselldemallorca.helium.integracio.plugins.pinbal.DadesConsultaPinbal;
import net.conselldemallorca.helium.integracio.plugins.pinbal.Funcionari;
import net.conselldemallorca.helium.integracio.plugins.pinbal.Titular;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesAssumpte;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesExpedient;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesInteressat;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesOficina;
import net.conselldemallorca.helium.integracio.plugins.registre.DocumentRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantDetallRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.AreaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesConsultaPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiRespostaColumnaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiRespostaFilaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.FestiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalEstatEnum;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxBlocDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReferenciaNotificacio;
import net.conselldemallorca.helium.v3.core.api.dto.ReferenciaRDSJustificanteDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreAnnexDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreAnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreIdDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaJustificantDetallRecepcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaJustificantRecepcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaNotificacio;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaNotificacio.NotificacioEstat;
import net.conselldemallorca.helium.v3.core.api.dto.ScspAtributos;
import net.conselldemallorca.helium.v3.core.api.dto.ScspConfirmacioPeticioPinbal;
import net.conselldemallorca.helium.v3.core.api.dto.ScspJustificant;
import net.conselldemallorca.helium.v3.core.api.dto.ScspRespostaPinbal;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperEventDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperExpedientDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.registre.RegistreAnotacio;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.v3.core.api.service.Jbpm3HeliumService;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;
import net.conselldemallorca.helium.v3.core.repository.AreaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.CarrecRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioValorsRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.FestiuRepository;
import net.conselldemallorca.helium.v3.core.repository.InteressatRepository;
import net.conselldemallorca.helium.v3.core.repository.PeticioPinbalRepository;
import net.conselldemallorca.helium.v3.core.repository.ReassignacioRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;
import net.conselldemallorca.helium.v3.core.repository.UnitatOrganitzativaRepository;

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
	private DominiRepository dominiRepository;
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
	@Resource
	private AlertaRepository alertaRepository;
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
	@Resource
	private TerminiRepository terminiRepository;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Resource
	private PeticioPinbalRepository peticioPinbalRepository;	
	
	@Resource
	private RegistreRepository registreRepository;

	
	@Resource(name = "documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ExpedientTipusService expedientTipusService;
	@Resource
	private DominiHelper dominiHelper;
	@Resource
	private JbpmHelper jbpmHelper;
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
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;

	@Resource
	private IndexHelper indexHelper;

	@Resource
	private MetricRegistry metricRegistry;
	
	@Resource
	private TascaSegonPlaHelper tascaSegonPlaHelper;
	
	@Resource
	private DefinicioProcesHelper definicioProcesHelper;
	
	@Resource 
	private AlertaHelper alertaHelper;

	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	
	@Resource
	private ExpedientRegistreHelper expedientRegistreHelper;
	
	@Resource
	private PlantillaHelper plantillaHelper;	


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
		return conversioTipusHelper.convertir(
				entornRepository.findOne(entornId),
				EntornDto.class);
	}

	@Override
	public ExpedientDto getExpedientIniciant() {
		logger.debug("Obtenint expedient en fase d'inici");
		return conversioTipusHelper.convertir(
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
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new NoTrobatException(Entorn.class, entornId);
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
				entorn,
				expedientTipusCodi);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusCodi);
		return conversioTipusHelper.convertir(
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
		return conversioTipusHelper.convertir(
				getExpedientDonatProcessInstanceId(processInstanceId),
				ExpedientDto.class);
	}
	
	@Override
	public EntornDto getEntornAmbProcessInstanceId(
			String processInstanceId) {
		logger.debug("Obtenint expedient donada una instància de procés (processInstanceId=" + processInstanceId + ")");
		return conversioTipusHelper.convertir(
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
		return conversioTipusHelper.convertir(
				defincioProces,
				DefinicioProcesDto.class);
	}
	
	@Override
	public DefinicioProcesDto getDefinicioProcesAmbJbpmKeyIVersio(
			String jbpmKey,
			int version) {
		logger.debug("Obtenint la definició de procés donat el codi jBPM i la versió (jbpmKey=" + jbpmKey + ", version=" + version +")");
		return conversioTipusHelper.convertir(
				definicioProcesRepository.findByJbpmKeyAndVersio(
						jbpmKey,
						version),
				DefinicioProcesDto.class);
	}

	@Override
	public DefinicioProcesDto getDarreraVersioAmbEntornIJbpmKey(
			Long entornId,
			Long expedientTipusId,
			String jbpmKey) {
		logger.debug("Obtenint la darrera versió de la definició de procés donat l'entorn, tipus i el codi jBPM (entornId=" + entornId + ", expedientTipusId=" + expedientTipusId + ", jbpmKey=" + jbpmKey + ")");
		return conversioTipusHelper.convertir(
				definicioProcesRepository.findDarreraVersioAmbEntornTipusIJbpmKey(
						entornId,
						expedientTipusId == null,
						expedientTipusId != null? expedientTipusId : 0L,
						jbpmKey),
				DefinicioProcesDto.class);
	}

	@Override
	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(
			String processInstanceId) {
		logger.debug("Obtenint la definició de procés donada la instància de procés (processInstanceId=" + processInstanceId + ")");
		return conversioTipusHelper.convertir(
				getDefinicioProcesDonatProcessInstanceId(processInstanceId),
				DefinicioProcesDto.class);
	}

	@Override
	public PersonaDto getPersonaAmbCodi(String codi) {
		logger.debug("Obtenint persona (codi=" + codi + ")");
		return conversioTipusHelper.convertir(
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
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new NoTrobatException(Entorn.class, entornId);
		return conversioTipusHelper.convertir(
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
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new NoTrobatException(Entorn.class, entornId);
		Area area = areaRepository.findByEntornAndCodi(
				entorn,
				areaCodi);
		if (area == null)
			throw new NoTrobatException(Area.class, areaCodi);
		return conversioTipusHelper.convertir(
				carrecRepository.findByEntornAndAreaAndCodi(
						entorn,
						area,
						carrecCodi),
				CarrecDto.class);
	}

	@Override
	public List<FestiuDto> findFestiusAll() {
		logger.debug("Obtenint la llista de tots els festius");
		return conversioTipusHelper.convertirList(
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
		return conversioTipusHelper.convertir(
				reassignacio,
				ReassignacioDto.class);
	}

	
	
	@Override
	public void interessatCrear(
			InteressatDto interessat) {
		
		this.validaInteressat(interessat);

		Expedient expedient = expedientRepository.findOne(interessat.getExpedientId());
		
		if (interessatRepository.findByCodiAndExpedient(interessat.getCodi(), expedient) != null) {
			throw new ValidacioException("Ja existeix un interessat amb aquest codi");
		}
		
		Interessat interessatEntity = new Interessat(
				interessat.getId(),
				interessat.getCodi(),
				interessat.getNom(),
				interessat.getDocumentIdent(),
				interessat.getDir3Codi(),
				interessat.getLlinatge1(), 
				interessat.getLlinatge2(), 
				interessat.getTipus(),
				interessat.getEmail(), 
				interessat.getTelefon(),
				expedient,
				interessat.getEntregaPostal(),
				interessat.getEntregaTipus(),
				interessat.getLinia1(),
				interessat.getLinia2(),
				interessat.getCodiPostal(),
				interessat.getEntregaDeh(),
				interessat.getEntregaDehObligat(),
				interessat.getTipusDocIdent(),
				interessat.getDireccio(),
				interessat.getObservacions(),
				interessat.getEs_representant(),
				interessat.getRaoSocial(),
				interessat.getPais(),
				interessat.getProvincia(),
				interessat.getMunicipi(),
				interessat.getCanalNotif(),
				interessat.getCodiDire()
				);
		if(expedient.getInteressats()!=null)
			expedient.getInteressats().add(interessatEntity);
		else {
			List<Interessat> interessatsList = new ArrayList<Interessat>();
			interessatsList.add(interessatEntity);
			expedient.setInteressats(interessatsList);
		}
		if (expedient.isArxiuActiu()) {
			// Modifiquem l'expedient a l'arxiu.
			pluginHelper.arxiuExpedientModificar(expedient);
		}
		
		interessatRepository.save(interessatEntity);
	}
	
	@Override
	public void interessatModificar(
			InteressatDto interessat) {
		
		this.validaInteressat(interessat);

		Expedient expedient = expedientRepository.findOne(interessat.getExpedientId());
		
		if (interessatRepository.findByCodiAndExpedient(interessat.getCodi(), expedient) == null) {
			throw new ValidacioException("Un interessat amb aquest codi no existeix");
		}
		
		Interessat interessatEntity = interessatRepository.findByCodiAndExpedient(
				interessat.getCodi(), 
				expedient);
		
		interessatEntity.setNom(interessat.getNom());
		interessatEntity.setDocumentIdent(interessat.getDocumentIdent());
		interessatEntity.setLlinatge1(interessat.getLlinatge1());
		interessatEntity.setLlinatge2(interessat.getLlinatge2());
		interessatEntity.setTipus(interessat.getTipus());
		interessatEntity.setEmail(interessat.getEmail());
		interessatEntity.setTelefon(interessat.getTelefon());
		interessatEntity.setEntregaPostal(interessat.getEntregaPostal());
		interessatEntity.setEntregaTipus(interessat.getEntregaTipus());
		interessatEntity.setLinia1(interessat.getLinia1());
		interessatEntity.setLinia2(interessat.getLinia2());
		interessatEntity.setCodiPostal(interessat.getCodiPostal());
		interessatEntity.setEntregaDeh(interessat.getEntregaDeh());
		interessatEntity.setEntregaDehObligat(interessat.getEntregaDehObligat());
		
		if (expedient.isArxiuActiu()) {
			// Modifiquem l'expedient a l'arxiu.
			pluginHelper.arxiuExpedientModificar(expedient);
		}
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
		if (interessat.getDocumentIdent() == null || interessat.getDocumentIdent().isEmpty())
			errors.add("El NIF/CIF/DNI és obligatori");
		if (interessat.getNom() == null || interessat.getNom().isEmpty())
			errors.add("El nom/raó social és obligatori");
		if (interessat.getTipus() == null)
			errors.add("El tipus d'interessat és obligatori");
		
		// Llinatge1 per persones físiques
		if (interessat.getTipus() == InteressatTipusEnumDto.FISICA && (interessat.getLlinatge1() == null || interessat.getLlinatge1().isEmpty())) {			
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
		
		Expedient expedient = expedientRepository.findOne(interessat.getExpedientId());
		
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
		if (expedient.isArxiuActiu()) {
			// Modifiquem l'expedient a l'arxiu.
			pluginHelper.arxiuExpedientModificar(expedient);
		}
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
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new NoTrobatException(Entorn.class, entornId);
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new NoTrobatException(Expedient.class, expedientId);
		
		alertaHelper.crearAlerta(entorn, expedient, data, usuariCodi, text);
	}

	@Override
	public void alertaEsborrarAmbTaskInstanceId(long taskInstanceId) {
		logger.debug("Esborrant alertes amb taskInstance (" +
				"taskInstanceId=" + taskInstanceId + ")");
		Date ara = new Date();
		List<TerminiIniciat> terminis = terminiIniciatRepository.findByTaskInstanceId(
				new Long(taskInstanceId).toString());
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
	public void expedientBuidaLogs(
			String processInstanceId) {
		logger.debug("Buidant logs expedient (processInstanceId=" + processInstanceId + ")");
		ProcessInstanceExpedient piexp = jbpmHelper.expedientFindByProcessInstanceId(processInstanceId);
		if (piexp == null)
			throw new NoTrobatException(ProcessInstanceExpedient.class, processInstanceId);
		jbpmHelper.deleteProcessInstanceTreeLogs(piexp.getProcessInstanceId());
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
		Document document = documentHelper.getDocumentByTipusExpedientOrDefinicioProcesAndCodi(expedient.getTipus(), definicioProces, documentCodi);

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
			String motiu,
			byte[] contingut,
			Long documentStoreId) throws ValidacioException {
		
		if(documentStoreId==null) {
			documentStoreId = documentHelper.findDocumentStorePerInstanciaProcesAndDocumentCodi(
					processInstanceId, 
					documentCodi);
		}
		
		documentHelper.firmaServidor(
				processInstanceId,
				documentStoreId,
				motiu,
				contingut !=null ? contingut : null);
	}

	@Override
	public void createDadesTasca(Long taskId) {
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
		return conversioTipusHelper.convertir(
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
		return conversioTipusHelper.convertir(
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
		
		Termini termini = this.getTermini(processInstanceId, terminiCodi);		
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
		Termini termini = this.getTermini(processInstanceId, terminiCodi);		
		terminiHelper.iniciar(
				termini.getId(),
				processInstanceId,
				data,
				esDataFi,
				false);
	}

	private Termini getTermini(String processInstanceId, String terminiCodi) {

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
		return termini;

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
		Domini domini;
		// Dominis del tipus d'expedient
		domini = dominiRepository.findByExpedientTipusAndCodiAmbHerencia(
				expedient.getTipus().getId(),
				dominiCodi);
		// Si no el troba el busca a l'entorn
		if (domini == null)
			domini = dominiRepository.findByEntornAndCodi(
					expedient.getEntorn(),
					dominiCodi);
		if (domini == null)
			throw new NoTrobatException(Domini.class, dominiCodi);
		List<FilaResultat> files = dominiHelper.consultar(
				domini,
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
		return conversioTipusHelper.convertirList(
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
			long taskInstanceId) {
		logger.debug("Consultant els camps del formulari de la tasca (" +
				"taskInstanceId=" + taskInstanceId + ")");
		JbpmTask task = jbpmHelper.getTaskById(new Long(taskInstanceId).toString());
		if (task == null)
			throw new NoTrobatException(JbpmTask.class, taskInstanceId);
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
		return conversioTipusHelper.convertirList(
				campTascaRepository.findAmbTascaIdOrdenats(tasca.getId(), tipus.getId()),
				CampTascaDto.class);
	}

	@Override
	public List<DocumentTascaDto> findDocumentsPerTaskInstance(
			long taskInstanceId) {
		logger.debug("Consultant els documents de la tasca (taskInstanceId=" + taskInstanceId + ")");
		JbpmTask task = jbpmHelper.getTaskById(new Long(taskInstanceId).toString());
		if (task == null)
			throw new NoTrobatException(JbpmTask.class, taskInstanceId);
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
		return conversioTipusHelper.convertirList(
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
				"perNotificar=" + perNotificar + ", " + 
				"ambSegellSignatura=" + ambSegellSignatura + ")");
		return conversioTipusHelper.convertir(
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
				false,
				null);
	}

	@Override
	public Long documentExpedientGuardar(
			String processInstanceId,
			String documentCodi,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) {
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
		//Tancam expedient al arxiu, firmant els documents sense firma amb firma servidor
		expedientHelper.finalitzar(expedient.getId(), true);
	}
	
	
	
	@Override
	public boolean tokenActivar(long tokenId, boolean activar) {
		logger.debug("tokenActivar (" +
				"tokenId=" + tokenId + ", " +
				"activar=" + activar + ")");
		try {
			return jbpmHelper.tokenActivar(tokenId, activar);
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
			String documentCodi) {
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
					conversioTipusHelper.convertirList(
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
					"EMAIL",
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
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new NoTrobatException(Expedient.class, expedientId);
		
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
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new NoTrobatException(Expedient.class, expedientId);
		
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
			expedient = expedientRepository.findOne(expedientId);
			if (expedient == null)
				throw new NoTrobatException(Expedient.class, expedientId);
		}
		
		return pluginHelper.registreDataJustificantRecepcio(registreNumero, expedient);
	}

	@Override
	public String registreObtenirOficinaNom(
			String oficinaCodi,
			Long expedientId) {
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new NoTrobatException(Expedient.class, expedientId);
		
		return pluginHelper.registreOficinaNom(oficinaCodi, expedient);
	}
	
	@Override
	public String registreObtenirOficinaNom(
			String numRegistre,
			String usuariCodi,
			String entitatCodi,
			Long expedientId) {
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new NoTrobatException(Expedient.class, expedientId);
		
		return pluginHelper.registreOficinaNom(
				numRegistre, 
				usuariCodi,
				entitatCodi,
				expedient);
	}

	@Override
	public ScspRespostaPinbal consultaPinbal(
			DadesConsultaPinbalDto dadesConsultaPinbal,
			Long expedientId,
			String processInstanceId,
			Long tokenId)
			throws SistemaExternException, NoTrobatException {
		
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new NoTrobatException(Expedient.class, expedientId);
		
		Entorn entorn = entornRepository.findOne(expedient.getEntorn().getId());
		if (entorn == null)
			throw new NoTrobatException(Entorn.class, expedient.getEntorn().getId());
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
				entorn,
				expedient.getTipus().getCodi());
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedient.getTipus().getCodi());

		if (dadesConsultaPinbal.getDocumentCodi()==null) {
			throw new RuntimeException("No s'ha especificat cap codi de document per associar el justificant de la petició PINBAL.");
		} else {
			Document document = documentHelper.getDocumentByTipusExpedientOrDefinicioProcesAndCodi(
					expedientTipus,
					getDefinicioProcesDonatProcessInstanceId(processInstanceId),
					dadesConsultaPinbal.getDocumentCodi());
			if (document==null)
				throw new RuntimeException("No s'ha trobat el codi de document "+dadesConsultaPinbal.getDocumentCodi()+ " per el procés "+processInstanceId+".");
		}
		
		ScspRespostaPinbal respostaPinbal = new ScspRespostaPinbal();
		ScspConfirmacioPeticioPinbal scspConfirmacioPeticioPinbal = null;
		Long documentStoreJusificantId = null;
		Date dataPecicio = Calendar.getInstance().getTime();
		
		if(dadesConsultaPinbal.isAsincrona()) {
			
			scspConfirmacioPeticioPinbal = (ScspConfirmacioPeticioPinbal) pluginHelper.consultaPinbal(
						this.convertirDadesDeDto(dadesConsultaPinbal, expedientTipus, expedient), 
						expedient, 
						null);//Al ser consultaGenèrica, li passen directament ells el serveiCodi
			
			if(scspConfirmacioPeticioPinbal!=null && scspConfirmacioPeticioPinbal.getAtributos()!=null) {

				ScspAtributos scspAtributos = scspConfirmacioPeticioPinbal.getAtributos();
				respostaPinbal.setIdPeticion(scspAtributos.getIdPeticion());
				dadesConsultaPinbal.setCodiProcediment(scspAtributos.getCodigoCertificado());
				
				Calendar cal = Calendar.getInstance();
				if (scspAtributos.getEstado()!=null && scspAtributos.getEstado().getTiempoEstimadoRespuesta()!=null) {
					cal.add(Calendar.HOUR, scspAtributos.getEstado().getTiempoEstimadoRespuesta());
				}
				respostaPinbal.setDataProcessament(cal.getTime()); //Data prevista
				
				DadesDocumentDto documentJusificantProvissional = plantillaHelper.generaJustificantTemporalPinbal(
						dadesConsultaPinbal.getCodiProcediment(),
						scspAtributos.getIdPeticion(),
						cal.getTime());

				//Cream sempre un nou documentStore, que s'associará a la variable JBPM.
				//Aixi mantenim els dos documentStore de les peticions pinbal successives, pero al expedient nomes es veu la darrera.
				documentStoreJusificantId = documentHelper.crearDocument(
						null,
						processInstanceId,
						dadesConsultaPinbal.getDocumentCodi(),
						new Date(),
						false, //isAdjunt
						null,  //adjuntTitol
						documentJusificantProvissional.getArxiuNom(),
						documentJusificantProvissional.getArxiuContingut(),
						null, // arxiuUuid
						documentHelper.getContentType(documentJusificantProvissional.getArxiuNom()),
						false, //ambFirma
						false, //firmaSeparada
						null,  //firmaContingut
						null,  //ntiOrigen
						null,  //ntiEstadoElaboracion
						null,  //ntiTipoDocumental
						null,  //ntiIdDocumentoOrigen
						true,  //documentValid
						null,  //documentError
						null,  //annexId
						null); //annexosPerNotificar
			}	 
		} else {
			respostaPinbal = (ScspRespostaPinbal) pluginHelper.consultaPinbal(
					this.convertirDadesDeDto(dadesConsultaPinbal, expedientTipus, expedient), 
					expedient, 
					null);//Al ser consultaGenèrica, li passen directament ells el serveiCodi
			
			ScspJustificant justificant = new ScspJustificant();
			
			if (respostaPinbal!=null) {
				justificant = respostaPinbal.getJustificant();
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
						"(Petició" + (dadesConsultaPinbal.isAsincrona() ? "asíncrona" : "síncrona" ) +"Pinbal)", 
						null);//"[" + respostaPinbal.getErrorCodi() + "]: " + respostaPlugin.getErrorDescripcio());
			}
			
			if(justificant!=null) {
				//Cream sempre un nou documentStore, que s'associará a la variable JBPM.
				//Aixi mantenim els dos documentStore de les peticions pinbal successives, pero al expedient nomes es veu la darrera.
				documentStoreJusificantId = documentHelper.crearDocument(
						null,
						processInstanceId,
						dadesConsultaPinbal.getDocumentCodi(),
						new Date(),
						false, //isAdjunt
						null,  //adjuntTitol
						justificant.getNom(),
						justificant.getContingut(),
						null, // arxiuUuid
						documentHelper.getContentType(justificant.getNom()),
						false, //ambFirma
						false, //firmaSeparada
						null,  //firmaContingut
						null,  //ntiOrigen
						null,  //ntiEstadoElaboracion
						null,  //ntiTipoDocumental
						null,  //ntiIdDocumentoOrigen
						true,  //documentValid
						null,  //documentError
						null,  //annexId
						null); //annexosPerNotificar
			}			
		}
		guardaPeticioPinbalSenseError(
				expedient,
				documentStoreRepository.findOne(documentStoreJusificantId),
				dadesConsultaPinbal,
				respostaPinbal.getIdPeticion(),
				dataPecicio,
				respostaPinbal.getDataProcessament(),
				tokenId);
		
		return respostaPinbal;
	}
	
	@Override
	public ScspRespostaPinbal consultaDadesIdentitatPinbalSVDDGPCIWS02(
			DadesConsultaPinbalDto dadesConsultaPinbal,
			Long expedientId,
			String processInstanceId,
			Long tokenId)
			throws SistemaExternException, NoTrobatException {

		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new NoTrobatException(Expedient.class, expedientId);
		
		Entorn entorn = entornRepository.findOne(expedient.getEntorn().getId());
		if (entorn == null)
			throw new NoTrobatException(Entorn.class, expedient.getEntorn().getId());
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
				entorn,
				expedient.getTipus().getCodi());
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedient.getTipus().getCodi());
		
		if (dadesConsultaPinbal.getDocumentCodi()==null) {
			throw new RuntimeException("No s'ha especificat cap codi de document per associar el justificant de la petició PINBAL.");
		} else {
			Document document = documentHelper.getDocumentByTipusExpedientOrDefinicioProcesAndCodi(
					expedientTipus,
					getDefinicioProcesDonatProcessInstanceId(processInstanceId),
					dadesConsultaPinbal.getDocumentCodi());
			if (document==null)
				throw new RuntimeException("No s'ha trobat el codi de document "+dadesConsultaPinbal.getDocumentCodi()+ " per el procés "+processInstanceId+".");
		}
		
		ScspRespostaPinbal respostaPinbal = new ScspRespostaPinbal();
		ScspConfirmacioPeticioPinbal scspConfirmacioPeticioPinbal = null;
		Long documentStoreJusificantId = null;
		Date dataPecicio = Calendar.getInstance().getTime();
		
		if(dadesConsultaPinbal.isAsincrona()) {
			
			scspConfirmacioPeticioPinbal = (ScspConfirmacioPeticioPinbal)pluginHelper.consultaPinbal(
					this.convertirDadesDeDto(dadesConsultaPinbal, expedientTipus, expedient), 
					expedient, 
					PluginHelper.serveiConsultaDades);

//			scspConfirmacioPeticioPinbal = new ScspConfirmacioPeticioPinbal();
//			scspConfirmacioPeticioPinbal.setAtributos(new ScspAtributos());
//			scspConfirmacioPeticioPinbal.getAtributos().setEstado(new ScspEstado());
//			scspConfirmacioPeticioPinbal.getAtributos().getEstado().setTiempoEstimadoRespuesta(300);
//			scspConfirmacioPeticioPinbal.getAtributos().setIdPeticion("PROVA_ASYNC");
			
			if(scspConfirmacioPeticioPinbal!=null && scspConfirmacioPeticioPinbal.getAtributos()!=null) {
				
				ScspAtributos scspAtributos = scspConfirmacioPeticioPinbal.getAtributos();
				respostaPinbal.setIdPeticion(scspAtributos.getIdPeticion());
				dadesConsultaPinbal.setCodiProcediment(PluginHelper.serveiConsultaDades);
				
				Calendar cal = Calendar.getInstance();
				if (scspAtributos.getEstado()!=null && scspAtributos.getEstado().getTiempoEstimadoRespuesta()!=null) {
					cal.add(Calendar.HOUR, scspAtributos.getEstado().getTiempoEstimadoRespuesta());
				}
				respostaPinbal.setDataProcessament(cal.getTime()); //Data prevista
				
				DadesDocumentDto documentJusificantProvissional = plantillaHelper.generaJustificantTemporalPinbal(
					dadesConsultaPinbal.getCodiProcediment(),
					scspAtributos.getIdPeticion(),
					cal.getTime());

				//Cream sempre un nou documentStore, que s'associará a la variable JBPM.
				//Aixi mantenim els dos documentStore de les peticions pinbal successives, pero al expedient nomes es veu la darrera.
				documentStoreJusificantId = documentHelper.crearDocument(
						null,
						processInstanceId,
						dadesConsultaPinbal.getDocumentCodi(),
						new Date(),
						false, //isAdjunt
						null,  //adjuntTitol
						documentJusificantProvissional.getArxiuNom(),
						documentJusificantProvissional.getArxiuContingut(),
						null, // arxiuUuid
						documentHelper.getContentType(documentJusificantProvissional.getArxiuNom()),
						false, //ambFirma
						false, //firmaSeparada
						null,  //firmaContingut
						null,  //ntiOrigen
						null,  //ntiEstadoElaboracion
						null,  //ntiTipoDocumental
						null,  //ntiIdDocumentoOrigen
						true,  //documentValid
						null,  //documentError
						null,  //annexId
						null); //annexosPerNotificar
			}
		}else {
			respostaPinbal = (ScspRespostaPinbal) pluginHelper.consultaPinbal(
				this.convertirDadesDeDto(dadesConsultaPinbal, expedientTipus, expedient), 
				expedient, 
				PluginHelper.serveiConsultaDades);
		
			ScspJustificant justificant = new ScspJustificant();
		
			if (respostaPinbal!=null) {
				justificant = respostaPinbal.getJustificant();
			}else {
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
						"(Petició " + (dadesConsultaPinbal.isAsincrona() ? "asíncrona" : "síncrona" ) +" Pinbal servei : "+PluginHelper.serveiConsultaDades +")", 
						null);
			}
			if(justificant!=null) {
				//Cream sempre un nou documentStore, que s'associará a la variable JBPM.
				//Aixi mantenim els dos documentStore de les peticions pinbal successives, pero al expedient nomes es veu la darrera.
				documentStoreJusificantId = documentHelper.crearDocument(
						null,
						processInstanceId,
						dadesConsultaPinbal.getDocumentCodi(),
						new Date(),
						false, //isAdjunt
						null,  //adjuntTitol
						justificant.getNom(),
						justificant.getContingut(),
						null, // arxiuUuid
						documentHelper.getContentType(justificant.getNom()),
						false, //ambFirma
						false, //firmaSeparada
						null,  //firmaContingut
						null,  //ntiOrigen
						null,  //ntiEstadoElaboracion
						null,  //ntiTipoDocumental
						null,  //ntiIdDocumentoOrigen
						true,  //documentValid
						null,  //documentError
						null,  //annexId
						null); //annexosPerNotificar
			}
		}
		
		guardaPeticioPinbalSenseError(
				expedient,
				documentStoreRepository.findOne(documentStoreJusificantId),
				dadesConsultaPinbal,
				respostaPinbal.getIdPeticion(),
				dataPecicio,
				respostaPinbal.getDataProcessament(),
				tokenId);		
		
		return respostaPinbal;
	}

	private void guardaPeticioPinbalSenseError(
			Expedient expedient,
			DocumentStore justificant,
			DadesConsultaPinbalDto dadesConsultaPinbal,
			String idPeticioPinbal,
			Date dataPeticio,
			Date dataPrevista,
			Long tokenId) {
		//Cream la petició pinbal per el historic de peticions
		PeticioPinbal peticio = new PeticioPinbal();
		peticio.setDataPeticio(dataPeticio);
		peticio.setAsincrona(dadesConsultaPinbal.isAsincrona());
		if (dadesConsultaPinbal.isAsincrona()) {
			peticio.setEstat(PeticioPinbalEstatEnum.PENDENT);
			peticio.setDataPrevista(dataPrevista);
		} else {
			peticio.setEstat(PeticioPinbalEstatEnum.TRAMITADA);
		}
		peticio.setExpedient(expedient);
		peticio.setTipus(expedient.getTipus());
		peticio.setEntorn(expedient.getTipus().getEntorn());
		peticio.setProcediment(dadesConsultaPinbal.getCodiProcediment());
		peticio.setUsuari(SecurityContextHolder.getContext().getAuthentication().getName());
		peticio.setPinbalId(idPeticioPinbal);
		peticio.setDocument(justificant);
		peticio.setTokenId(tokenId);
		peticio.setTransicioOK(dadesConsultaPinbal.getTransicioOK());
		peticio.setTransicioKO(dadesConsultaPinbal.getTransicioKO());
		peticioPinbalRepository.save(peticio);
	}
	
	@Override
	public Object verificacioDadesIdentitatPinbalSVDDGPCIWS02(
			DadesConsultaPinbalDto dadesConsultaPinbal,
			Long expedientId,
			String processInstanceId,
			Long tokenId) throws SistemaExternException, NoTrobatException {
		
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new NoTrobatException(Expedient.class, expedientId);
		
		Entorn entorn = entornRepository.findOne(expedient.getEntorn().getId());
		if (entorn == null)
			throw new NoTrobatException(Entorn.class, expedient.getEntorn().getId());
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
				entorn,
				expedient.getTipus().getCodi());
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedient.getTipus().getCodi());
		
		if (dadesConsultaPinbal.getDocumentCodi()==null) {
			throw new RuntimeException("No s'ha especificat cap codi de document per associar el justificant de la petició PINBAL.");
		} else {
			Document document = documentHelper.getDocumentByTipusExpedientOrDefinicioProcesAndCodi(
					expedientTipus,
					getDefinicioProcesDonatProcessInstanceId(processInstanceId),
					dadesConsultaPinbal.getDocumentCodi());
			if (document==null)
				throw new RuntimeException("No s'ha trobat el codi de document "+dadesConsultaPinbal.getDocumentCodi()+ " per el procés "+processInstanceId+".");
		}		
		
		ScspRespostaPinbal respostaPinbal = new ScspRespostaPinbal();
		ScspConfirmacioPeticioPinbal scspConfirmacioPeticioPinbal = null;
		Long documentStoreJusificantId = null;
		Date dataPeticio = Calendar.getInstance().getTime();
		
		if(dadesConsultaPinbal.isAsincrona()) {
			
			scspConfirmacioPeticioPinbal = (ScspConfirmacioPeticioPinbal)pluginHelper.consultaPinbal(
					this.convertirDadesDeDto(dadesConsultaPinbal, expedientTipus, expedient), 
					expedient, 
					PluginHelper.serveiVerificacioDades);
			
			if(scspConfirmacioPeticioPinbal!=null && scspConfirmacioPeticioPinbal.getAtributos()!=null) {
				
				ScspAtributos scspAtributos = scspConfirmacioPeticioPinbal.getAtributos();
				respostaPinbal.setIdPeticion(scspAtributos.getIdPeticion());
				dadesConsultaPinbal.setCodiProcediment(PluginHelper.serveiVerificacioDades);
				
				Calendar cal = Calendar.getInstance();
				if (scspAtributos.getEstado()!=null && scspAtributos.getEstado().getTiempoEstimadoRespuesta()!=null) {
					cal.add(Calendar.HOUR, scspAtributos.getEstado().getTiempoEstimadoRespuesta());
				}				
				respostaPinbal.setDataProcessament(cal.getTime()); //Data prevista
				
				DadesDocumentDto documentJusificantProvissional = plantillaHelper.generaJustificantTemporalPinbal(
						dadesConsultaPinbal.getCodiProcediment(),
						scspAtributos.getIdPeticion(),
						cal.getTime());
	
				//Cream sempre un nou documentStore, que s'associará a la variable JBPM.
				//Aixi mantenim els dos documentStore de les peticions pinbal successives, pero al expedient nomes es veu la darrera.
				documentStoreJusificantId = documentHelper.crearDocument(
						null,
						processInstanceId,
						dadesConsultaPinbal.getDocumentCodi(),
						new Date(),
						false, //isAdjunt
						null,  //adjuntTitol
						documentJusificantProvissional.getArxiuNom(),
						documentJusificantProvissional.getArxiuContingut(),
						null, // arxiuUuid
						documentHelper.getContentType(documentJusificantProvissional.getArxiuNom()),
						false, //ambFirma
						false, //firmaSeparada
						null,  //firmaContingut
						null,  //ntiOrigen
						null,  //ntiEstadoElaboracion
						null,  //ntiTipoDocumental
						null,  //ntiIdDocumentoOrigen
						true,  //documentValid
						null,  //documentError
						null,  //annexId
						null); //annexosPerNotificar
			}
		} else {
		
			respostaPinbal = (ScspRespostaPinbal) pluginHelper.consultaPinbal(
					this.convertirDadesDeDto(dadesConsultaPinbal, expedientTipus, expedient), 
					expedient, 
					PluginHelper.serveiVerificacioDades);
			
			ScspJustificant justificant = new ScspJustificant();
			
			if (respostaPinbal!=null) {
				justificant = respostaPinbal.getJustificant();	
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
						"(Petició " + (dadesConsultaPinbal.isAsincrona() ? "asíncrona" : "síncrona" )+" Pinbal servei : "+PluginHelper.serveiVerificacioDades +")", 
						null);
			}
			
			if(justificant!=null) {
				//Cream sempre un nou documentStore, que s'associará a la variable JBPM.
				//Aixi mantenim els dos documentStore de les peticions pinbal successives, pero al expedient nomes es veu la darrera.
				documentStoreJusificantId = documentHelper.crearDocument(
						null,
						processInstanceId,
						dadesConsultaPinbal.getDocumentCodi(),
						new Date(),
						false, //isAdjunt
						null,  //adjuntTitol
						justificant.getNom(),
						justificant.getContingut(),
						null, // arxiuUuid
						documentHelper.getContentType(justificant.getNom()),
						false, //ambFirma
						false, //firmaSeparada
						null,  //firmaContingut
						null,  //ntiOrigen
						null,  //ntiEstadoElaboracion
						null,  //ntiTipoDocumental
						null,  //ntiIdDocumentoOrigen
						true,  //documentValid
						null,  //documentError
						null,  //annexId
						null); //annexosPerNotificar
			}
		}
		
		guardaPeticioPinbalSenseError(
				expedient,
				documentStoreRepository.findOne(documentStoreJusificantId),
				dadesConsultaPinbal,
				respostaPinbal.getIdPeticion(),
				dataPeticio,
				respostaPinbal.getDataProcessament(),
				tokenId);			
		
		return respostaPinbal;
	}

	@Override
	public Object dadesTributariesPinbalSVDCCAACPASWS01(
			DadesConsultaPinbalDto dadesConsultaPinbal,
			Long expedientId,
			String processInstanceId,
			Long tokenId) throws SistemaExternException, NoTrobatException {
		
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new NoTrobatException(Expedient.class, expedientId);
		
		Entorn entorn = entornRepository.findOne(expedient.getEntorn().getId());
		if (entorn == null)
			throw new NoTrobatException(Entorn.class, expedient.getEntorn().getId());
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
				entorn,
				expedient.getTipus().getCodi());
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedient.getTipus().getCodi());
		
		if (dadesConsultaPinbal.getDocumentCodi()==null) {
			throw new RuntimeException("No s'ha especificat cap codi de document per associar el justificant de la petició PINBAL.");
		} else {
			Document document = documentHelper.getDocumentByTipusExpedientOrDefinicioProcesAndCodi(
					expedientTipus,
					getDefinicioProcesDonatProcessInstanceId(processInstanceId),
					dadesConsultaPinbal.getDocumentCodi());
			if (document==null)
				throw new RuntimeException("No s'ha trobat el codi de document "+dadesConsultaPinbal.getDocumentCodi()+ " per el procés "+processInstanceId+".");
		}		
		
		ScspRespostaPinbal respostaPinbal = new ScspRespostaPinbal();
		ScspConfirmacioPeticioPinbal scspConfirmacioPeticioPinbal = null;
		Long documentStoreJusificantId = null;
		Date dataPecicio = Calendar.getInstance().getTime();		
		
		if(dadesConsultaPinbal.isAsincrona()) {
			
			scspConfirmacioPeticioPinbal = (ScspConfirmacioPeticioPinbal)pluginHelper.consultaPinbal(
					this.convertirDadesDeDto(dadesConsultaPinbal, expedientTipus, expedient), 
					expedient, 
					PluginHelper.serveiObligacionsTributaries);
			
			if(scspConfirmacioPeticioPinbal!=null && scspConfirmacioPeticioPinbal.getAtributos()!=null) {
				
				ScspAtributos scspAtributos = scspConfirmacioPeticioPinbal.getAtributos();
				respostaPinbal.setIdPeticion(scspAtributos.getIdPeticion());
				dadesConsultaPinbal.setCodiProcediment(PluginHelper.serveiObligacionsTributaries);
				
				Calendar cal = Calendar.getInstance();
				if (scspAtributos.getEstado()!=null && scspAtributos.getEstado().getTiempoEstimadoRespuesta()!=null) {
					cal.add(Calendar.HOUR, scspAtributos.getEstado().getTiempoEstimadoRespuesta());
				}
				respostaPinbal.setDataProcessament(cal.getTime()); //Data prevista
				
				DadesDocumentDto documentJusificantProvissional = plantillaHelper.generaJustificantTemporalPinbal(
						dadesConsultaPinbal.getCodiProcediment(),
						scspAtributos.getIdPeticion(),
						cal.getTime());
	
				//Cream sempre un nou documentStore, que s'associará a la variable JBPM.
				//Aixi mantenim els dos documentStore de les peticions pinbal successives, pero al expedient nomes es veu la darrera.
				documentStoreJusificantId = documentHelper.crearDocument(
						null,
						processInstanceId,
						dadesConsultaPinbal.getDocumentCodi(),
						new Date(),
						false, //isAdjunt
						null,  //adjuntTitol
						documentJusificantProvissional.getArxiuNom(),
						documentJusificantProvissional.getArxiuContingut(),
						null, // arxiuUuid
						documentHelper.getContentType(documentJusificantProvissional.getArxiuNom()),
						false, //ambFirma
						false, //firmaSeparada
						null,  //firmaContingut
						null,  //ntiOrigen
						null,  //ntiEstadoElaboracion
						null,  //ntiTipoDocumental
						null,  //ntiIdDocumentoOrigen
						true,  //documentValid
						null,  //documentError
						null,  //annexId
						null); //annexosPerNotificar		
			}
		} else {
		
			respostaPinbal = (ScspRespostaPinbal) pluginHelper.consultaPinbal(
					this.convertirDadesDeDto(dadesConsultaPinbal, expedientTipus, expedient), 
					expedient, 
					PluginHelper.serveiObligacionsTributaries);
			
			ScspJustificant justificant = new ScspJustificant();
			
			if (respostaPinbal!=null) {
				justificant = respostaPinbal.getJustificant();
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
						"(Petició síncrona Pinbal servei : "+PluginHelper.serveiObligacionsTributaries +")", 
						null);
			}
			
			if(justificant!=null) {
				//Cream sempre un nou documentStore, que s'associará a la variable JBPM.
				//Aixi mantenim els dos documentStore de les peticions pinbal successives, pero al expedient nomes es veu la darrera.
				documentStoreJusificantId = documentHelper.crearDocument(
						null,
						processInstanceId,
						dadesConsultaPinbal.getDocumentCodi(),
						new Date(),
						false, //isAdjunt
						null,  //adjuntTitol
						justificant.getNom(),
						justificant.getContingut(),
						null, // arxiuUuid
						documentHelper.getContentType(justificant.getNom()),
						false, //ambFirma
						false, //firmaSeparada
						null,  //firmaContingut
						null,  //ntiOrigen
						null,  //ntiEstadoElaboracion
						null,  //ntiTipoDocumental
						null,  //ntiIdDocumentoOrigen
						true,  //documentValid
						null,  //documentError
						null,  //annexId
						null); //annexosPerNotificar	
			}
		}
		
		guardaPeticioPinbalSenseError(
				expedient,
				documentStoreRepository.findOne(documentStoreJusificantId),
				dadesConsultaPinbal,
				respostaPinbal.getIdPeticion(),
				dataPecicio,
				respostaPinbal.getDataProcessament(),
				tokenId);			
		
		return respostaPinbal;
	}
	
	private DadesConsultaPinbal convertirDadesDeDto(DadesConsultaPinbalDto dadesDto, ExpedientTipus expedientTipus, Expedient expedient) {
		Titular titular = new Titular();
		Funcionari funcionari = new Funcionari();
		
		if(dadesDto.getInteressatCodi()!=null && !"".equals(dadesDto.getInteressatCodi())/* && dadesDto.getTitular()!=null*/){
			Interessat interessat= interessatRepository.findByCodiAndExpedient(dadesDto.getInteressatCodi(), expedient);
			if(interessat!=null)
				titular = new Titular(
						interessat.getDocumentIdent(),
						dadesDto.getTitular()!=null ? dadesDto.getTitular().getTipoDocumentacion() : null,//el que ens passin ells??
						null, //nomComplet no en te
						interessat.getNom(),
						interessat.getLlinatge1(),
						interessat.getLlinatge2());
			else
				throw new NoTrobatException(Interessat.class, dadesDto.getInteressatCodi());
		}
		//Si no tenim les dades de l'interessat, hem de tenir les dades del Titular
		else if(dadesDto.getTitular()!=null)
			titular = new Titular(
					dadesDto.getTitular().getDocumentacion(),
					dadesDto.getTitular().getTipoDocumentacion(),
					dadesDto.getTitular().getNombreCompleto(),
					dadesDto.getTitular().getNombre(),
					dadesDto.getTitular().getApellido1(),
					dadesDto.getTitular().getApellido2());

		PersonaDto personaDto = this.getPersonaAmbCodi(getUsuariCodiActual());		
		funcionari = new Funcionari(
				personaDto.getNomSencer(),
				personaDto.getDni(),
				personaDto.getCodi()
				);
	
		dadesDto.setCodiProcediment(expedientTipus.getNtiClasificacion());
		dadesDto.setEntitat_CIF(expedientTipus.getPinbalNifCif());
		String codiUO=null;
		try
	    {
			UnitatOrganitzativa uo = null;
			if(expedientTipus.isProcedimentComu() && expedient.getUnitatOrganitzativa()!=null){
				codiUO = expedient.getUnitatOrganitzativa().getCodi();	
			} else {
				codiUO = expedientTipus.getNtiOrgano();
			}
			uo = this.unitatOrganitzativaRepository.findByCodi(codiUO);
			dadesDto.setUnitatTramitadora(StringUtilsHelium.abreuja(uo.getDenominacio(), 64));
	    }
	    catch (Exception e)
	    {
	      logger.error("Error d'integraicó consultant la UO: " + 
	    		  codiUO!=null ? codiUO : expedientTipus.getNtiOrgano()
	    		+  " " + e.getMessage(), e);
	      logger.warn("Com a unitat tramitadora per la consulta a PINBAL es fixa el codi DIR3 " + expedientTipus.getNtiOrgano());
	      dadesDto.setUnitatTramitadora(codiUO!=null ? codiUO : expedientTipus.getNtiOrgano());
	    }
		
		return new DadesConsultaPinbal(
				titular, 
				funcionari, 
				dadesDto.getXmlDadesEspecifiques(), 
				dadesDto.getServeiCodi(), 
				dadesDto.getDocumentCodi(),
				dadesDto.getFinalitat(),
				dadesDto.getConsentiment(), 
				dadesDto.getInteressatCodi(),
				dadesDto.getCodiProcediment(), 
				dadesDto.getEntitat_CIF(),
				dadesDto.getUnitatTramitadora(),
				dadesDto.isAsincrona(),
				dadesDto.getAnyNaixement());
	}
	
	@Override
	public RegistreIdDto notificacioCrear(
			RegistreNotificacioDto notificacio,
			Long expedientId,
			boolean crearExpedient) {
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new NoTrobatException(Expedient.class, expedientId);
		
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
			return conversioTipusHelper.convertir(resposta, RespostaJustificantRecepcioDto.class);
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
			return conversioTipusHelper.convertir(resposta, RespostaJustificantDetallRecepcioDto.class);
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
		Expedient expedient = expedientRepository.findOne(dadesNotificacio.getExpedientId());
		
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
			String transicioKO,
			String portafirmesFluxId) {
		DocumentStore documentStore = documentStoreRepository.findOne(documentId);
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
		
		// Si es posa un destinatari llavors es posa en el pas 1
		if (persona != null) {
			if (personesPas1 == null) {
				personesPas1 = new ArrayList<PersonaDto>();
			}
			personesPas1.add(0, persona);
			minSignatarisPas1 = 1;
		}
		
		List<PortafirmesFluxBlocDto> blocList = new ArrayList<PortafirmesFluxBlocDto>();
		List<String> personesPasList = new ArrayList<String>();
		
		if(personesPas1 != null && !personesPas1.isEmpty()) {
			for (PersonaDto p : personesPas1) {
				personesPasList.add(p.getCodi());
			}
			PortafirmesFluxBlocDto bloc = new PortafirmesFluxBlocDto(minSignatarisPas1,personesPasList);
			blocList.add(bloc);
		} 
		if(personesPas2 != null && !personesPas2.isEmpty()) {
			personesPasList = new ArrayList<String>();
			for (PersonaDto p : personesPas2) {
				personesPasList.add(p.getCodi());
			}
			PortafirmesFluxBlocDto bloc = new PortafirmesFluxBlocDto(minSignatarisPas2,personesPasList);
			blocList.add(bloc);
		} 
		if(personesPas3 != null) {
			personesPasList = new ArrayList<String>();
			for (PersonaDto p : personesPas3) {
				personesPasList.add(p.getCodi());
			}
			PortafirmesFluxBlocDto bloc = new PortafirmesFluxBlocDto(minSignatarisPas3,personesPasList);
			blocList.add(bloc);
		}

		return pluginHelper.portasignaturesEnviar( 
					document,
					annexos,
					blocList,
					expedientRepository.findOne(expedientId),
					importancia,
					dataLimit,
					tokenId,
					processInstanceId,
					transicioOK,
					transicioKO,
					null,
					portafirmesFluxId!=null ? portafirmesFluxId : document.getPortafirmesFluxId(),
					document.getPortafirmesFluxTipus());//PortafirmesTipusEnumDto fluxTipus
	}

	@Override
	public void portasignaturesEliminar(
			Integer documentId) {
		pluginHelper.portasignaturesCancelar(documentId);
	}

	@Override
	public void zonaperExpedientCrear(
			ExpedientDto expedient,
			ZonaperExpedientDto dadesExpedient) {
		
			String identificador = expedient.getNumeroDefault();
			String clau = new Long(System.currentTimeMillis()).toString();
			dadesExpedient.setExpedientIdentificador(identificador);
			dadesExpedient.setExpedientClau(clau);
			pluginHelper.tramitacioZonaperExpedientCrear(expedient, dadesExpedient);
			Expedient ex = expedientRepository.findOne(expedient.getId());
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
		return conversioTipusHelper.convertir(
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
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new NoTrobatException(Entorn.class, entornId);
		ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
				entorn,
				expedientTipusCodi);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusCodi);
		return conversioTipusHelper.convertir(
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
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, definicioProcesId);
		ExpedientTipus expedientTipus = expedient.getTipus();
		
		if (expedientTipus.isAmbInfoPropia())
			return conversioTipusHelper.convertir(
					documentRepository.findByExpedientTipusAndCodi(
							expedientTipus.getId(), 
							documentCodi,
							expedientTipus.getExpedientTipusPare() != null),
					DocumentDissenyDto.class);
		else
			return conversioTipusHelper.convertir(
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
		Expedient origen = expedientRepository.findOne(expedientIdOrigen);
		if (origen == null)
			throw new NoTrobatException(Expedient.class, expedientIdOrigen);
		Expedient desti = expedientRepository.findOne(expedientIdDesti);
		if (desti == null)
			throw new NoTrobatException(Expedient.class, expedientIdDesti);
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
				new Long(tokenId).toString(),
				nodeName,
				cancelarTasques);
	}

	@Override
	public ExpedientDadaDto getDadaPerProcessInstance(
			String processInstanceId,
			String varCodi) {
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
		Object valor = jbpmHelper.getProcessInstanceVariable(
				processInstanceId,
				varCodi);
		resposta.setText(
				variableHelper.getTextPerCamp(
						camp, 
						valor, 
						null, 
						null, 
						processInstanceId));
		return resposta;
	}
	
	@Override
	public TascaDadaDto getDadaPerTaskInstance(
			String processInstanceId,
			String taskInstanceId,
			String varCodi) {
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
		Object valor = jbpmHelper.getTaskInstanceVariable(
				taskInstanceId,
				varCodi);
		resposta.setText(
				variableHelper.getTextPerCamp(
						camp, 
						valor, 
						null, 
						taskInstanceId, 
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
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new NoTrobatException(Entorn.class, entornId);
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		Expedient expedient = expedientRepository.findByEntornAndTipusAndNumero(
				entorn,
				expedientTipus,
				numero);
		if (expedient == null)
			return null;
		return conversioTipusHelper.convertir(expedient, ExpedientDto.class);
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
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new NoTrobatException(Entorn.class, entornId);
		ExpedientTipus expedientTipus = null;
		Estat estat = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
			if (expedientTipus == null)
				throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
			if (estatId != null) {
				estat = estatRepository.findOne(estatId);
				if (estat == null)
					throw new NoTrobatException(Estat.class, estatId);
			}
		}
		return conversioTipusHelper.convertirList(
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
		
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new NoTrobatException(Entorn.class, entornId);
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
				resposta.add(conversioTipusHelper.convertir(expedient, ExpedientDto.class));
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

	@Override
	public boolean mesuraIsActiu() {
		return MesuresTemporalsHelper.isActiu();
	}
	@Override
	public void mesuraIniciar(String clau, String familia, String tipusExpedient, String tasca, String detall) {
		mesuresTemporalsHelper.mesuraIniciar(clau, familia, tipusExpedient, tasca, detall);
	}
	@Override
	public void mesuraCalcular(String clau, String familia, String tipusExpedient, String tasca, String detall) {
		mesuresTemporalsHelper.mesuraCalcular(clau, familia, tipusExpedient, tasca, detall);
	}

	@Override
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateExpedientError(
			Long jobId,
			Long expedientId,
			String errorDesc,
			String errorFull) {
		logger.error("JOB (" + jobId + "): Actualitzant error de l'expedient");
//		if (jobId != null)
//			jbpmHelper.retryJob(jobId);
		Expedient expedient = expedientRepository.findOne(expedientId);
		expedient.setErrorDesc(errorDesc);
		expedient.setErrorFull(errorFull);
		expedientRepository.save(expedient);
	}

	@Override
	public String getHeliumProperty(String propertyName) {
		return GlobalProperties.getInstance().getProperty(propertyName);
	}

	@Override
	public MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}
	
	@Override
	public List<String> getRolsByCodi(String codi) {
		List<String> rols = new ArrayList<String>();
		try {
			if (pluginHelper.personaIsPluginActiu())
				rols = pluginHelper.personaFindRolsAmbCodi(codi);
		} catch (Exception ex) {
			// En cas que hi hagi una excepció,retornarem la llista buida 
		}
		return rols;
	}
	
	@Override
	public void setErrorTascaSegonPla(Long taskId, Exception ex) {
		if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
			Map<Long, InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
			if (map.containsKey(taskId)) {
				map.get(taskId).setError((ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
			}
		}
	}

	@Override
	public Long getTaskInstanceIdByTokenId(Long tokenId) {
		return jbpmHelper.getTaskInstanceIdByTokenId(tokenId);
	}
	
	@Override
	public void addMissatgeExecucioTascaSegonPla(Long taskId, String[] message) {
		if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
			Map<Long, InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
			if (map.containsKey(taskId)) {
				map.get(taskId).addMessage(message);
			}
		}
	}
	
	@Override
	public boolean isTascaEnSegonPla(Long taskId) {
		boolean result = false;
		if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
			Map<Long, InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
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
		JbpmProcessInstance processInstance = jbpmHelper.getProcessInstance(processInstanceId);
		if (processInstance == null)
			throw new NoTrobatException(JbpmProcessInstance.class, processInstanceId);
		
		return definicioProcesRepository.findByJbpmId(
				processInstance.getProcessDefinitionId());
	}

	private Entorn getEntornDonatProcessInstanceId(
			String processInstanceId) {
		JbpmProcessInstance processInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		if (processInstance == null)
			throw new NoTrobatException(JbpmProcessInstance.class, processInstanceId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		return expedient.getEntorn();
	}

	@Override
	public List<DefinicioProcesDto> findSubDefinicionsProces(Long definicioProcesId) {
		List<DefinicioProcesDto> resposta = new ArrayList<DefinicioProcesDto>();
		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId);
		for (JbpmProcessDefinition pd : jbpmHelper.getSubProcessDefinitions(definicioProces.getJbpmId())) {
			resposta.add(conversioTipusHelper.convertir(
					definicioProcesRepository.findByJbpmId(pd.getId()),
					DefinicioProcesDto.class));
		}
		return resposta;
	}

	@Override
	public RegistreIdDto registreAnotacioSortida(RegistreAnotacio anotacio, Long expedientId) {
		
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new NoTrobatException(Expedient.class, expedientId);
		
		RegistreIdDto respostaPlugin = pluginHelper.registreAnotacioSortida(
				anotacio,
				expedient);
		RegistreIdDto resposta = new RegistreIdDto();
		resposta.setNumero(respostaPlugin.getNumero());
		resposta.setData(respostaPlugin.getData());
		return resposta;
	}

	private static final Logger logger = LoggerFactory.getLogger(Jbpm3HeliumHelper.class);




	@Override
	public ExpedientTipusDto findExpedientTipusAmbEntorniCodi(Long entornId, String expedientTipusCodi)
			throws NoTrobatException, PermisDenegatException {
		
			return conversioTipusHelper.convertir(
					expedientTipusRepository.findByEntornAndCodi(
							entornRepository.findOne(entornId), 
							expedientTipusCodi),
					ExpedientTipusDto.class);
	}

	@Override
	public byte[] getZipPerNotificar(Long expedientId, List<ExpedientDocumentDto> documentsPerAfegir) {
		Expedient expedient = expedientRepository.findOne(expedientId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(baos);
		ZipEntry ze;
		ArxiuDto arxiu;
		try {
			// Consulta l'arbre de processos
			List<InstanciaProcesDto> arbreProcessos =
					expedientHelper.getArbreInstanciesProces(String.valueOf(expedient.getProcessInstanceId()));
			// Llistat de noms dins del zip per no repetir-los.
			Set<String> nomsArxius = new HashSet<String>();
			for (InstanciaProcesDto instanciaProces: arbreProcessos) {
				// Per cada instancia de proces consulta els documents.
				for (ExpedientDocumentDto document : documentsPerAfegir) {
					// Consulta l'arxiu del document
					DocumentStore documentStore = documentStoreRepository.findOne(document.getId());
					if (documentStore == null) {
						throw new NoTrobatException(
								DocumentStore.class,
								document.getId());
					}
					// Consulta el contingut.
					arxiu = documentHelper.getArxiuPerDocumentStoreId(
							document.getId(),
							false,
							false,
							null);
					// Crea l'entrada en el zip
					String recursNom = this.getZipRecursNom(
							expedient, 
							instanciaProces, 
							document, 
							arxiu,
							nomsArxius);
					ze = new ZipEntry(recursNom);
					out.putNextEntry(ze);
					out.write(arxiu.getContingut());
					out.closeEntry();
				}
			}			
			out.close();
		} catch (Exception e) {
			String errMsg = "Error construint el zip dels documents per notificar " + expedient.getIdentificador() + ": " + e.getMessage();
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg, e);
		}
		return baos.toByteArray();
	}

	/** Estableix en nom de l'arxiu a partir del document i l'extensió de l'arxiu. Afegeix una carpeta
	 * si el procés no és el principal, corregeix els caràcters estranys i vigila que no es repeteixin.
	 * 
	 * @param expedient Per determinar si és el procés principal
	 * @param instanciaProces Per determinar si és el procés principal i crear una carpeta en cas contrari.
	 * @param document Per recuperar el nom per l'arxiu
	 * @param arxiu Per recuperar l'extensió
	 * @param nomsArxius Per controlar la llista de noms utilitzats.
	 * @return
	 */
	private String getZipRecursNom(Expedient expedient, InstanciaProcesDto instanciaProces,
			ExpedientDocumentDto document, ArxiuDto arxiu, Set<String> nomsArxius) {
		String recursNom;
		// Nom
		String nom;
		// Segons si és adjunt o document
		if (document.isAdjunt())
			nom = document.getAdjuntTitol();
		else
			nom = document.getDocumentNom();
		nom = nom.replaceAll("/", "_");
		// Carpeta
		String carpeta = null;
		if (!instanciaProces.getId().equals(expedient.getProcessInstanceId())) {
			// Carpeta per un altre procés
			carpeta = instanciaProces.getId() + " - " + instanciaProces.getTitol();
			carpeta = carpeta.replaceAll("/", "_");
		}
		// Extensió
		String extensio = arxiu.getExtensio();

		// Vigila que no es repeteixi
		int comptador = 0;
		do {
			recursNom = (carpeta != null ? carpeta + "/" : "") +
						nom + 
						(comptador > 0 ? " (" + comptador + ")" : "") +
						"." + extensio;
			comptador++;
		} while (nomsArxius.contains(recursNom));

		// Guarda en nom com a utiltizat
		nomsArxius.add(recursNom);
		return recursNom;
	}

	@Override
	public DocumentDto findDocumentAmbId(Long documentStoreId) throws NoTrobatException {
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		DocumentDto dto = documentHelper.toDocumentDto(
				documentStoreId,
				false,
				false,
				true,
				true,
				true, // Per notificar
				(documentStore == null || documentStore.getArxiuUuid() == null));
		if (dto == null) {
			throw new NoTrobatException(DocumentDto.class, documentStoreId);
		}
		return dto;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientDocumentDto findOneAmbInstanciaProces(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		logger.debug("Consulta un document de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		return documentHelper.findOnePerInstanciaProces(
				processInstanceId,
				documentStoreId);
	}
	
	@Override
	@Transactional
	public Long guardarDocumentProces(
			String processInstanceId, 
			String documentCodi,
			String titol,
			Date data, 
			String arxiu,
			byte[] contingut,
			List<ExpedientDocumentDto> annexosPerNotificar) {
		
		logger.debug("Guardar document procés (" +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"data=" + data + ", " +
				"titol=" + titol + ", " +
				"arxiu=" + arxiu + ")");
		
		Long documentStoreId = null;
		
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);

		ExpedientDocumentDto expDocDto = this.findOneAmbInstanciaProces(expedient.getId(), processInstanceId, documentCodi);
		if(expDocDto != null) { 
			documentStoreId = expDocDto.getId();
			this.update( 
					expedient.getId(),
					processInstanceId,
					expDocDto.getId(), //Long documentStoreId,
					(data != null ? data : expDocDto.getDataDocument()),
					expDocDto.getAdjuntTitol(), //String adjuntTitol
					arxiu,
					contingut,
					new MimetypesFileTypeMap().getContentType(arxiu),
					false, //boolean ambFirma,
					false, //boolean firmaSeparada,
					null, //byte[] firmaContingut,
					expDocDto.getNtiOrigen(), //NtiOrigenEnumDto ntiOrigen,
					expDocDto.getNtiEstadoElaboracion(), //NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
					expDocDto.getNtiTipoDocumental(), //NtiTipoDocumentalEnumDto ntiTipoDocumental,
					expDocDto.getNtiIdOrigen() //String ntiIdOrigen
					);
		} else {
			documentStoreId = this.create(
					expedient.getId(),
					processInstanceId,
					documentCodi, // null en el cas dels adjunts
					(data != null ? data : new Date()),
					titol, // Títol en el cas dels adjunts (al crear ja li posa el nom del document)
					arxiu,
					contingut,
					documentHelper.getContentType(arxiu),//new MimetypesFileTypeMap().getContentType(arxiu),
					false, //command.isAmbFirma(),
					false, //DocumentTipusFirmaEnumDto.SEPARAT.equals(command.getTipusFirma()),
					null, //firmaContingut,
					null, //command.getNtiOrigen(),
					null, //command.getNtiEstadoElaboracion(),
					null, //command.getNtiTipoDocumental(),
					null,  //command.getNtiIdOrigen()
					annexosPerNotificar);
		}
		return documentStoreId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientDocumentDto findOneAmbInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String documentCodi) {
		logger.debug("Consulta un document de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		return documentHelper.findOnePerInstanciaProces(
				processInstanceId,
				documentCodi);
	}
	
	@Override
	@Transactional
	public void update(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			Date data,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut,
			String arxiuContentType,
			boolean ambFirma,
			boolean firmaSeparada,
			byte[] firmaContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen) {
		logger.debug("Modificar document de l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ", " +
				"data=" + data + ", " +
				"adjuntTitol=" + adjuntTitol + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ", " +
				"arxiuContentType=" + arxiuContentType + ", " +
				"ambFirma=" + ambFirma + ", " +
				"firmaSeparada=" + firmaSeparada + ", " +
				"firmaContingut=" + firmaContingut + ", " +
				"ntiOrigen=" + ntiOrigen + ", " +
				"ntiEstadoElaboracion=" + ntiEstadoElaboracion + ", " +
				"ntiTipoDocumental=" + ntiTipoDocumental + ", " +
				"ntiIdOrigen=" + ntiIdOrigen + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DOC_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		DocumentStore documentStore = documentStoreRepository.findByIdAndProcessInstanceId(
				documentStoreId,
				processInstanceId);
		if (documentStore == null) {
			throw new NoTrobatException(
					DocumentStore.class, 
					documentStoreId);
		}
		String documentCodi = documentStore.getCodiDocument();
		String arxiuNomAntic = documentStore.getArxiuNom();
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_DOCUMENT_MODIFICAR,
				documentCodi);
		documentHelper.actualitzarDocument(
				documentStoreId,
				null,
				processInstanceId,
				data,
				documentStore.isAdjunt() ? adjuntTitol : null,
				arxiuNom,
				arxiuContingut,
				arxiuContentType,
				ambFirma,
				firmaSeparada,
				firmaContingut,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				ntiIdOrigen);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		expedientRegistreHelper.crearRegistreModificarDocumentInstanciaProces(
				expedient.getId(),
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				documentCodi,
				arxiuNomAntic,
				arxiuNom);
	}
	
	@Override
	@Transactional
	public Long create(
			Long expedientId,
			String processInstanceId,
			String documentCodi,
			Date data,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut,
			String arxiuContentType,
			boolean ambFirma,
			boolean firmaSeparada,
			byte[] firmaContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen,
			List<ExpedientDocumentDto> annexosPerNotificar) {
		logger.debug("Crear document a dins l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"data=" + data + ", " +
				"adjuntTitol=" + adjuntTitol + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ", " +
				"arxiuContentType=" + arxiuContentType + ", " +
				"ambFirma=" + ambFirma + ", " +
				"firmaSeparada=" + firmaSeparada + ", " +
				"firmaContingut=" + firmaContingut + ", " +
				"ntiOrigen=" + ntiOrigen + ", " +
				"ntiEstadoElaboracion=" + ntiEstadoElaboracion + ", " +
				"ntiTipoDocumental=" + ntiTipoDocumental + ", " +
				"ntiIdOrigen=" + ntiIdOrigen + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DOC_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		boolean isAdjunt = documentCodi == null;
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				isAdjunt ? 
						ExpedientLogAccioTipus.PROCES_DOCUMENT_ADJUNTAR 
						: ExpedientLogAccioTipus.PROCES_DOCUMENT_AFEGIR,
				documentCodi);
		Long documentStoreId = documentHelper.crearDocument(
				null,
				processInstanceId,
				documentCodi,
				data,
				isAdjunt,
				isAdjunt ? adjuntTitol : null,
				arxiuNom,
				arxiuContingut,
				null, // arxiuUuid
				arxiuContentType,
				ambFirma,
				firmaSeparada,
				firmaContingut,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				ntiIdOrigen,
				true,
				null,
				null,
				annexosPerNotificar);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		expedientRegistreHelper.crearRegistreCrearDocumentInstanciaProces(
				expedient.getId(),
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				documentCodi,
				arxiuNom);
		return documentStoreId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ArxiuDto arxiuFindAmbDocument(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		logger.debug("Consulta de l'arxiu del document de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore == null) {
			throw new NoTrobatException(
					DocumentStore.class,
					documentStoreId);
		}
		expedientHelper.comprovarInstanciaProces(
				expedient,
				documentStore.getProcessInstanceId());
		return documentHelper.getArxiuPerDocumentStoreId(
				documentStoreId,
				false,
				true,
				null);
	}


}
