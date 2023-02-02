/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DefinicioProcesHelper;
import net.conselldemallorca.helium.core.helper.DominiHelper;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.HerenciaHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusParamConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.EstatAccioEntrada;
import net.conselldemallorca.helium.core.model.hibernate.EstatAccioSortida;
import net.conselldemallorca.helium.core.model.hibernate.EstatRegla;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.model.hibernate.Reassignacio;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaDefaultAny;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.Validacio;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto.TipusConsultaCamp;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEstadisticaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisEstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PrincipalTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaAnyDto;
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaDefaultAnyDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.EstatAccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.EstatReglaDto;
import net.conselldemallorca.helium.v3.core.api.exception.DeploymentException;
import net.conselldemallorca.helium.v3.core.api.exception.ExportException;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.exportacio.AccioExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.AgrupacioExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.CampExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ConsultaCampExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ConsultaExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DocumentExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DominiExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.EnumeracioExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.EnumeracioValorExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.EstatExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ExpedientTipusExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ExpedientTipusExportacioCommandDto;
import net.conselldemallorca.helium.v3.core.api.exportacio.MapeigSistraExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.RegistreMembreExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.TascaExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.TerminiExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ValidacioExportacio;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.v3.core.repository.AccioRepository;
import net.conselldemallorca.helium.v3.core.repository.AnotacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampAgrupacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampValidacioRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaCampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioValorsRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatAccioEntradaRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatAccioSortidaRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatReglaRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.FirmaTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.MapeigSistraRepository;
import net.conselldemallorca.helium.v3.core.repository.ReassignacioRepository;
import net.conselldemallorca.helium.v3.core.repository.SequenciaAnyRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

/**
 * Implementació del servei per a gestionar tipus d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientTipusServiceImpl implements ExpedientTipusService {

	@Resource
	private EntornHelper entornHelper;
	@Resource
	private DefinicioProcesHelper definicioProcesHelper;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private SequenciaAnyRepository sequenciaRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private CampAgrupacioRepository campAgrupacioRepository;
	@Resource
	private CampValidacioRepository campValidacioRepository;
	@Resource
	private CampRegistreRepository campRegistreRepository;
	@Resource
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private EnumeracioValorsRepository enumeracioValorsRepository;	
	@Resource
	private DominiRepository dominiRepository;
	@Resource
	private ReassignacioRepository reassignacioRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private ConsultaCampRepository consultaCampRepository;
	@Resource
	private AccioRepository accioRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private TerminiRepository terminiRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private MapeigSistraRepository mapeigSistraRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private DocumentTascaRepository documentTascaRepository;
	@Resource
	private FirmaTascaRepository firmaTascaRepository;
	@Resource
	private AnotacioRepository anotacioRepository;
	@Resource
	private EstatReglaRepository estatReglaRepository;
	@Resource
	private EstatAccioEntradaRepository estatAccioEntradaRepository;
	@Resource
	private EstatAccioSortidaRepository estatAccioSortidaRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource(name = "permisosHelperV3") 
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private DominiHelper dominiHelper;
	@Resource
	private PluginHelper pluginHelper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ExpedientTipusDto create(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor) {
		logger.debug(
				"Creant nou tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipus=" + expedientTipus + ", " +
				"sequenciesAny=" + sequenciesAny + ", " +
				"sequenciesValor=" + sequenciesValor + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				true);
		ExpedientTipus entity = new ExpedientTipus();
		entity.setEntorn(entorn);
		entity.setCodi(expedientTipus.getCodi());
		entity.setNom(expedientTipus.getNom());
		entity.setTipus(expedientTipus.getTipus());
		entity.setAmbInfoPropia(expedientTipus.isAmbInfoPropia());
		entity.setHeretable(expedientTipus.isHeretable());
		if (expedientTipus.getExpedientTipusPareId() != null)
			entity.setExpedientTipusPare(expedientTipusRepository.findOne(expedientTipus.getExpedientTipusPareId()));
		else
			entity.setExpedientTipusPare(null);
		entity.setTeTitol(expedientTipus.isTeTitol());
		entity.setDemanaTitol(expedientTipus.isDemanaTitol());
		entity.setTeNumero(expedientTipus.isTeNumero());
		entity.setDemanaNumero(expedientTipus.isDemanaNumero());
		entity.setExpressioNumero(expedientTipus.getExpressioNumero());
		entity.setReiniciarCadaAny(expedientTipus.isReiniciarCadaAny());
		entity.setSequencia(expedientTipus.getSequencia());
		entity.setResponsableDefecteCodi(expedientTipus.getResponsableDefecteCodi());
		entity.setRestringirPerGrup(expedientTipus.isRestringirPerGrup());
		entity.setSeleccionarAny(expedientTipus.isSeleccionarAny());
		entity.setAmbRetroaccio(expedientTipus.isAmbRetroaccio());
		entity.setReindexacioAsincrona(expedientTipus.isReindexacioAsincrona());
		entity.setDiesNoLaborables(expedientTipus.getDiesNoLaborables());
		entity.setNotificacionsActivades(expedientTipus.isNotificacionsActivades());
		entity.setNotificacioOrganCodi(expedientTipus.getNotificacioOrganCodi());
		entity.setNotificacioOficinaCodi(expedientTipus.getNotificacioOficinaCodi());
		entity.setNotificacioUnitatAdministrativa(expedientTipus.getNotificacioUnitatAdministrativa());
		entity.setNotificacioCodiProcediment(expedientTipus.getNotificacioCodiProcediment());
		entity.setNotificacioAvisTitol(expedientTipus.getNotificacioAvisTitol());
		entity.setNotificacioAvisText(expedientTipus.getNotificacioAvisText());
		entity.setNotificacioAvisTextSms(expedientTipus.getNotificacioAvisTextSms());
		entity.setNotificacioOficiTitol(expedientTipus.getNotificacioOficiTitol());
		entity.setNotificacioOficiText(expedientTipus.getNotificacioOficiText());
		
		if (expedientTipus.isReiniciarCadaAny()) {
			for (int i = 0; i < sequenciesAny.size(); i++) {
				SequenciaAny anyEntity = new SequenciaAny(
						entity, 
						sequenciesAny.get(i), 
						sequenciesValor.get(i));
				entity.getSequenciaAny().put(anyEntity.getAny(), anyEntity);
			}
		}

		if (ExpedientTipusTipusEnumDto.ESTAT.equals(entity.getTipus())) {
			// Associa el tipus al flux senzill
			entity.setJbpmProcessDefinitionKey(
							this.getDefinicioProcesEstats(entorn)
								.getJbpmKey());
		}

		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(entity),
				ExpedientTipusDto.class);
	}

	private DefinicioProces getDefinicioProcesEstats(Entorn entorn) {
		DefinicioProces definicioProces = definicioProcesHelper.findDarreraVersioDefinicioProces(null, DefinicioProcesService.HELIUM_JBPM_FLOW);
		if (definicioProces == null) {
			definicioProces = definicioProcesHelper.desplegarJbpm(
					DefinicioProcesService.HELIUM_JBPM_FLOW + ".par",
					this.getContingutHelJbpmFlow(),
					entorn,
					null);
			logger.info("Desplegada la definició de procés pel flux hel_jbpm_flow per expedients basats en estats: " + definicioProces.getIdPerMostrar() );
		}
		return definicioProces;
	}

	private byte[] getContingutHelJbpmFlow() {
		byte[] contingut;
		InputStream is = null;
		String fitxer = "/par/hel_jbpm.par";
		try {
			is = getClass().getResourceAsStream(fitxer);
			contingut = IOUtils.toByteArray(is);
		} catch(Exception e) {
			throw new RuntimeException("Error obtenint el contingut de " + fitxer + ": " + e.getClass() + ": " + e.getMessage(), e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch(Exception e) {e.printStackTrace();}
		}
		return contingut;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ExpedientTipusDto update(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny,
			List<Long> sequenciesValor) {

		logger.debug(
				"Modificant tipus d'expedient existent (" +
				"entornId=" + entornId + ", " +
				"expedientTipus=" + expedientTipus + ", " +
				"sequenciesAny=" + sequenciesAny + ", " +
				"sequenciesValor=" + sequenciesValor + ")");
		
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				false);

		ExpedientTipus entity = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipus.getId());
				
		entity.setNom(expedientTipus.getNom());
		entity.setTipus(expedientTipus.getTipus());
		entity.setAmbInfoPropia(expedientTipus.isAmbInfoPropia());
		entity.setHeretable(expedientTipus.isHeretable());
		if (expedientTipus.getExpedientTipusPareId() != null)
			entity.setExpedientTipusPare(expedientTipusRepository.findOne(expedientTipus.getExpedientTipusPareId()));
		else
			entity.setExpedientTipusPare(null);
		entity.setTeTitol(expedientTipus.isTeTitol());
		entity.setDemanaTitol(expedientTipus.isDemanaTitol());
		entity.setTeNumero(expedientTipus.isTeNumero());
		entity.setDemanaNumero(expedientTipus.isDemanaNumero());
		entity.setExpressioNumero(expedientTipus.getExpressioNumero());
		entity.setReiniciarCadaAny(expedientTipus.isReiniciarCadaAny());
		entity.setSequencia(expedientTipus.getSequencia());
		entity.setResponsableDefecteCodi(expedientTipus.getResponsableDefecteCodi());
		entity.setRestringirPerGrup(expedientTipus.isRestringirPerGrup());
		entity.setSeleccionarAny(expedientTipus.isSeleccionarAny());
		entity.setDiesNoLaborables(expedientTipus.getDiesNoLaborables());
		entity.setNotificacionsActivades(expedientTipus.isNotificacionsActivades());
		entity.setNotificacioOrganCodi(expedientTipus.getNotificacioOrganCodi());
		entity.setNotificacioOficinaCodi(expedientTipus.getNotificacioOficinaCodi());
		entity.setNotificacioUnitatAdministrativa(expedientTipus.getNotificacioUnitatAdministrativa());
		entity.setNotificacioCodiProcediment(expedientTipus.getNotificacioCodiProcediment());
		entity.setNotificacioAvisTitol(expedientTipus.getNotificacioAvisTitol());
		entity.setNotificacioAvisText(expedientTipus.getNotificacioAvisText());
		entity.setNotificacioAvisTextSms(expedientTipus.getNotificacioAvisTextSms());
		entity.setNotificacioOficiTitol(expedientTipus.getNotificacioOficiTitol());
		entity.setNotificacioOficiText(expedientTipus.getNotificacioOficiText());
		while (entity.getSequenciaAny().size() > 0) {
			SequenciaAny s = entity.getSequenciaAny().get(entity.getSequenciaAny().firstKey());			
			entity.getSequenciaAny().remove(s.getAny());
			sequenciaRepository.delete(s);
		}
		if (expedientTipus.isReiniciarCadaAny()) {
			for (int i = 0; i < sequenciesAny.size(); i++) {
				SequenciaAny sequenciaEntity = new SequenciaAny(
						entity,
						sequenciesAny.get(i),
						sequenciesValor.get(i));
				entity.getSequenciaAny().put(sequenciaEntity.getAny(), sequenciaEntity);
				sequenciaRepository.save(sequenciaEntity);
			}
		}
		// Només poden configurar la retroacció els dissenyadors de l'entorn
		if (entornHelper.potDissenyarEntorn(entornId)) {
			entity.setAmbRetroaccio(expedientTipus.isAmbRetroaccio());
			entity.setReindexacioAsincrona(expedientTipus.isReindexacioAsincrona());
		}

		if (ExpedientTipusTipusEnumDto.ESTAT.equals(entity.getTipus())) {
			// Associa el tipus al flux senzill
			entity.setJbpmProcessDefinitionKey(
							this.getDefinicioProcesEstats(entorn)
								.getJbpmKey());
		}

		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(entity),
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ExpedientTipusDto updateIntegracioForms(
			Long entornId, 
			Long expedientTipusId, 
			String url, 
			String usuari,
			String contrasenya) {
		logger.debug(
				"Modificant tipus d'expedient amb dades d'integracio amb formularis externs (" +
				"entornId=" + entornId + ", " +
				"expedientTipus=" + expedientTipusId + ", " +
				"url=" + url + ", " +
				"usuari=" + usuari + ", " +
				"contrasenya=" + contrasenya + ")");
		
		ExpedientTipus entity = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
		
		entity.setFormextUrl(url);
		entity.setFormextUsuari(usuari);
		entity.setFormextContrasenya(contrasenya);

		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(entity),
				ExpedientTipusDto.class);	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ExpedientTipusDto updateIntegracioDistribucio(
			Long entornId, 
			Long expedientTipusId, 
			boolean actiu, 
			String codiProcediment,
			String codiAssumpte,
			boolean procesAuto,
			boolean sistra) {
		logger.debug(
				"Modificant tipus d'expedient amb dades d'integracio amb distribucio externs (" +
				"entornId=" + entornId + ", " +
				"expedientTipus=" + expedientTipusId + ", " +
				"actiu=" + actiu + ", " +
				"codiProcediment=" + codiProcediment + ", " +
				"codiAssumpte=" + codiAssumpte + ", " +
				"procesAuto=" + procesAuto + ", " +
				"sistra=" + sistra + ")");
		
		ExpedientTipus entity = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
		
		entity.setDistribucioActiu(actiu);
		entity.setDistribucioCodiProcediment(codiProcediment);
		entity.setDistribucioCodiAssumpte(codiAssumpte);
		entity.setDistribucioProcesAuto(procesAuto);
		entity.setDistribucioSistra(sistra);

		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(entity),
				ExpedientTipusDto.class);	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ExpedientTipusDto updateIntegracioTramits(
			boolean isActiu,
			Long entornId, 
			Long expedientTipusId, 
			String tramitCodi,
			boolean notificacionsActivades,
			String notificacioOrganCodi,
			String notificacioOficinaCodi,
			String notificacioUnitatAdministrativa,
			String notificacioCodiProcediment,
			String notificacioAvisTitol,
			String notificacioAvisText,
			String notificacioAvisTextSms,
			String notificacioOficiTitol,
			String notificacioOficiText) {
		logger.debug(
				"Modificant tipus d'expedient amb dades d'integracio amb tramits de Sistra (" +
				"entornId=" + entornId + ", " +
				"expedientTipus=" + expedientTipusId + ", " +
				"tramitCodi=" + tramitCodi + ")");
		
		ExpedientTipus entity = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
		entity.setSistraActiu(isActiu);
		entity.setSistraTramitCodi(tramitCodi);
		entity.setNotificacionsActivades(notificacionsActivades);
		entity.setNotificacioOrganCodi(notificacioOrganCodi);
		entity.setNotificacioOficinaCodi(notificacioOficinaCodi);
		entity.setNotificacioUnitatAdministrativa(notificacioUnitatAdministrativa);
		entity.setNotificacioCodiProcediment(notificacioCodiProcediment);
		entity.setNotificacioAvisTitol(notificacioAvisTitol);
    	entity.setNotificacioAvisText(notificacioAvisText);
    	entity.setNotificacioAvisTextSms(notificacioAvisTextSms);
    	entity.setNotificacioOficiTitol(notificacioOficiTitol);
    	entity.setNotificacioOficiText(notificacioOficiText);

		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(entity),
				ExpedientTipusDto.class);	
	}	

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Esborrant el tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ")");
		entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				true);
		ExpedientTipus entity = expedientTipusRepository.findOne(expedientTipusId);
		
		// Lleva la possible relació amb les anotacions
		for (Anotacio anotacio : anotacioRepository.findByExpedientTipusId(expedientTipusId)) {
			anotacio.setExpedientTipus(null);
		}
		
		expedientTipusRepository.delete(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusExportacio exportar(
			Long entornId,
			Long expedientTipusId,
			ExpedientTipusExportacioCommandDto command) {
		logger.debug(
				"Exportant el tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId = " + command.getId() + ", " + 
				"command = " + command + ")");
		// Control d'accés
		ExpedientTipus tipus = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
					command.getId());
		
		// Construeix l'objecte
		ExpedientTipusExportacio exportacio = new ExpedientTipusExportacio();
		// Dades del tipus d'expedient
		exportacio.setAnyActual(tipus.getAnyActual());
		exportacio.setCodi(tipus.getCodi());
		exportacio.setDemanaNumero(tipus.getDemanaNumero());
		exportacio.setDemanaTitol(tipus.getDemanaTitol());
		exportacio.setExpressioNumero(tipus.getExpressioNumero());
		exportacio.setJbpmProcessDefinitionKey(tipus.getJbpmProcessDefinitionKey());
		exportacio.setNom(tipus.getNom());
		exportacio.setReiniciarCadaAny(tipus.isReiniciarCadaAny());
		exportacio.setResponsableDefecteCodi(tipus.getResponsableDefecteCodi());
		exportacio.setRestringirPerGrup(tipus.isRestringirPerGrup());
		exportacio.setSeleccionarAny(tipus.isSeleccionarAny());
		exportacio.setAmbRetroaccio(tipus.isAmbRetroaccio());
		exportacio.setTipus(tipus.getTipus());
		exportacio.setAmbInfoPropia(tipus.isAmbInfoPropia());
		exportacio.setReindexacioAsincrona(tipus.isReindexacioAsincrona());
		exportacio.setSequencia(tipus.getSequencia());
		exportacio.setSequenciaDefault(tipus.getSequenciaDefault());
		exportacio.setTeNumero(tipus.getTeNumero());
		exportacio.setTeTitol(tipus.getTeTitol());
		exportacio.setTramitacioMassiva(tipus.isTramitacioMassiva());						
		exportacio.setHeretable(tipus.isHeretable());
		exportacio.setExpedientTipusPareCodi(tipus.getExpedientTipusPare() != null? 
				tipus.getExpedientTipusPare().getCodi()
				: null);
		Map<Integer,SequenciaAnyDto> sequenciaAnyMap = new HashMap<Integer, SequenciaAnyDto>();
		for (Entry<Integer, SequenciaAny> entry : tipus.getSequenciaAny().entrySet()) {
			SequenciaAny value = entry.getValue();
			SequenciaAnyDto valueDto = new SequenciaAnyDto();
			valueDto.setAny(value.getAny());
			valueDto.setId(value.getId());
			valueDto.setSequencia(value.getSequencia());
			sequenciaAnyMap.put(entry.getKey(), valueDto);
		}					
		exportacio.setSequenciaAny(sequenciaAnyMap);
		Map<Integer,SequenciaDefaultAnyDto> sequenciaAnyDefaultMap = new HashMap<Integer, SequenciaDefaultAnyDto>();
		for (Entry<Integer, SequenciaDefaultAny> entry : tipus.getSequenciaDefaultAny().entrySet()) {
			SequenciaDefaultAny value = entry.getValue();
			SequenciaDefaultAnyDto valueDto = new SequenciaDefaultAnyDto();
			valueDto.setAny(value.getAny());
			valueDto.setId(value.getId());
			valueDto.setSequenciaDefault(value.getSequenciaDefault());							
			sequenciaAnyDefaultMap.put(entry.getKey(), valueDto);
		}					    
		exportacio.setSequenciaDefaultAny(sequenciaAnyDefaultMap);
		// Metadades NTI i Arxiu
		exportacio.setNtiActiu(tipus.isNtiActiu());
		exportacio.setNtiOrgano(tipus.getNtiOrgano());
		exportacio.setNtiClasificacion(tipus.getNtiClasificacion());
		exportacio.setNtiSerieDocumental(tipus.getNtiSerieDocumental());
		exportacio.setArxiuActiu(tipus.isArxiuActiu());
		
		//Integracio amb PINBAL
		exportacio.setPinbalActiu(tipus.isPinbalActiu());
		exportacio.setPinbalNifCif(tipus.getPinbalNifCif());
		
		// Integracio amb DISTRIBUCIO
		exportacio.setDistribucioActiu(tipus.isDistribucioActiu());
		exportacio.setDistribucioCodiAssumpte(tipus.getDistribucioCodiAssumpte());
		exportacio.setDistribucioCodiProcediment(tipus.getDistribucioCodiProcediment());
		exportacio.setDistribucioProcesAuto(tipus.isDistribucioProcesAuto());
		exportacio.setDistribucioSistra(tipus.isDistribucioSistra());
		exportacio.setSistraActiu(tipus.isSistraActiu());
		// Integració amb NOTIB
		exportacio.setNotibActiu(tipus.getNotibActiu());
		exportacio.setNotibEmisor(tipus.getNotibEmisor());
		exportacio.setNotibCodiProcediment(tipus.getNotibCodiProcediment());
		// Integració amb forms
		if (command.isIntegracioForms()) {
			exportacio.setFormextUrl(tipus.getFormextUrl());
			exportacio.setFormextUsuari(tipus.getFormextUsuari());
			exportacio.setFormextContrasenya(tipus.getFormextContrasenya());
		}
		// Integració amb Sistra
		if (command.isIntegracioSistra()) {
			exportacio.setSistraTramitCodi(tipus.getSistraTramitCodi());
			for (MapeigSistra mapeig : tipus.getMapeigSistras())
				exportacio.getSistraMapejos().add(
						new MapeigSistraExportacio(
								mapeig.getCodiHelium(), 
								mapeig.getCodiSistra(), 
								MapeigSistraDto.TipusMapeig.valueOf(mapeig.getTipus().toString())));
		}
		// Estats
		if (command.getEstats().size() > 0)
			for (Estat estat: tipus.getEstats()) 
				if (command.getEstats().contains(estat.getCodi()))
					exportacio.getEstats().add(getEstatExportacio(estat, true));
		// Variables
		if (command.getVariables().size() > 0)
			for (Camp camp : tipus.getCamps()) 
				if (command.getVariables().contains(camp.getCodi())) {
					boolean necessitaDadesExternes = 
							TipusCamp.SELECCIO.equals(camp.getTipus()) 
							|| TipusCamp.SUGGEST.equals(camp.getTipus());	
					boolean necessitaDadesExternesEntorn = 
							(camp.getDomini() != null && camp.getDomini().getExpedientTipus() == null)
							|| (camp.getEnumeracio() != null && camp.getEnumeracio().getExpedientTipus() == null);
					CampExportacio campExportacio = new CampExportacio(
		                    camp.getCodi(),
		                    CampTipusDto.valueOf(camp.getTipus().toString()),
		                    camp.getEtiqueta(),
		                    camp.getObservacions(),
		                    (necessitaDadesExternes) ? camp.getDominiId() : null,
		                    (necessitaDadesExternes) ? camp.getDominiParams() : null,
		                    (necessitaDadesExternes) ? camp.getDominiCampText() : null,
		                    (necessitaDadesExternes) ? camp.getDominiCampValor() : null,
		                    (necessitaDadesExternes) ? camp.getConsultaParams() : null,
		                    (necessitaDadesExternes) ? camp.getConsultaCampText() : null,
		                    (necessitaDadesExternes) ? camp.getConsultaCampValor() : null,
		                    camp.isMultiple(),
		                    camp.isOcult(),
		                    camp.getDominiIntern(),
		                    camp.isDominiCacheText(),
		                    (necessitaDadesExternes && camp.getEnumeracio() != null) ? camp.getEnumeracio().getCodi() : null,
		                    (necessitaDadesExternes && camp.getDomini() != null) ? camp.getDomini().getCodi() : null,
		                    (necessitaDadesExternes && camp.getConsulta() != null) ? camp.getConsulta().getCodi() : null,
		                    (camp.getAgrupacio() != null) ? camp.getAgrupacio().getCodi() : null,
		                    camp.getDefprocJbpmKey(),
		                    camp.getJbpmAction(),
		                    camp.getOrdre(),
		                    camp.isIgnored(),
		                    necessitaDadesExternesEntorn);
					exportacio.getCamps().add(campExportacio);
					// Afegeix les validacions del camp
					for (Validacio validacio: camp.getValidacions()) {
						campExportacio.addValidacio(new ValidacioExportacio(
								validacio.getNom(),
								validacio.getExpressio(),
								validacio.getMissatge(),
								validacio.getOrdre()));
					}
					// Afegeix els membres dels camps de tipus registre
					for (CampRegistre membre: camp.getRegistreMembres()) {
						campExportacio.addRegistreMembre(new RegistreMembreExportacio(
								membre.getMembre().getCodi(),
								membre.isObligatori(),
								membre.isLlistar(),
								membre.getOrdre()));
					}
				}
		// Agrupacions
		if (command.getAgrupacions().size() > 0)
			for (CampAgrupacio agrupacio: tipus.getAgrupacions()) 
				if (command.getAgrupacions().contains(agrupacio.getCodi()))
					exportacio.getAgrupacions().add(new AgrupacioExportacio(
							agrupacio.getCodi(),
							agrupacio.getNom(),
							agrupacio.getDescripcio(),
							agrupacio.getOrdre()));
		
		// Definicions de procés
		if (command.getDefinicionsProces().size() > 0)
			for (DefinicioProces definicio: tipus.getDefinicionsProces()) 
				if (command.getDefinicionsProces().contains(definicio.getJbpmKey()) 
						&& command.getDefinicionsVersions().get(definicio.getJbpmKey()).equals(definicio.getVersio()))							
					exportacio.getDefinicions().add(
								definicioProcesHelper.getExportacio(
									definicio.getId(),
									null /* no especifica el filtre per a la exportació. */)
							);
		// Enumeracions
		if (command.getEnumeracions().size() > 0) {
			EnumeracioExportacio enumeracioExportacio;
			for (Enumeracio enumeracio : tipus.getEnumeracions())
				if (command.getEnumeracions().contains(enumeracio.getCodi())) {
					enumeracioExportacio = new EnumeracioExportacio(
							enumeracio.getCodi(), 
							enumeracio.getNom());
					for (EnumeracioValors enumeracioValor : enumeracio.getEnumeracioValors())
						enumeracioExportacio.getValors().add(
								new EnumeracioValorExportacio(
										enumeracioValor.getCodi(),
										enumeracioValor.getNom(),
										enumeracioValor.getOrdre()));
					exportacio.getEnumeracions().add(enumeracioExportacio);
				}
		}
		// Documents
		if (command.getDocuments().size() > 0) {
			DocumentExportacio documentExportacio;
			for (Document document : tipus.getDocuments())
				if (command.getDocuments().contains(document.getCodi())) {
					documentExportacio = new DocumentExportacio(
							document.getCodi(),
							document.getNom(),
							document.getDescripcio(),
							document.getArxiuContingut(),
							document.getArxiuNom(),
							document.isPlantilla(),
							document.isNotificable());
					documentExportacio.setCustodiaCodi(document.getCustodiaCodi());
					documentExportacio.setContentType(document.getContentType());
					documentExportacio.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
					documentExportacio.setAdjuntarAuto(document.isAdjuntarAuto());
					if (document.getCampData() != null)
						documentExportacio.setCodiCampData(document.getCampData().getCodi());
					documentExportacio.setConvertirExtensio(document.getConvertirExtensio());
					documentExportacio.setExtensionsPermeses(document.getExtensionsPermeses());
					documentExportacio.setIgnored(document.isIgnored());
					documentExportacio.setNtiTipoDocumental(document.getNtiTipoDocumental());
					exportacio.getDocuments().add(documentExportacio);
				}
		}		
		// Terminis
		if (command.getTerminis().size() > 0) {
			TerminiExportacio terminiExportacio;
			for (Termini termini : tipus.getTerminis())
				if (command.getTerminis().contains(termini.getCodi())) {
					terminiExportacio = new TerminiExportacio(
							termini.getCodi(),
							termini.getNom(),
							termini.getDescripcio(),
							termini.isDuradaPredefinida(),
							termini.getAnys(),
							termini.getMesos(),
							termini.getDies(),
							termini.isLaborable(),
							termini.getDiesPrevisAvis(),
							termini.isAlertaPrevia(),
							termini.isAlertaFinal(),
							termini.isAlertaCompletat(),
							termini.isManual());
					exportacio.getTerminis().add(terminiExportacio);
				}
		}	
		// Accions
		if (command.getAccions().size() > 0) {
			AccioExportacio accioExportacio;
			for (Accio accio : tipus.getAccions())
				if (command.getAccions().contains(accio.getCodi())) {
					accioExportacio = new AccioExportacio(
							accio.getCodi(),
							accio.getNom(),
							accio.getDescripcio(),
							accio.getDefprocJbpmKey(),
							accio.getJbpmAction(),
							accio.isPublica(),
							accio.isOculta(),
							accio.getRols());
					exportacio.getAccions().add(accioExportacio);
				}
		}	
		// Dominis
		if (command.getDominis().size() > 0) {
			DominiExportacio dominiExportacio;
			for (Domini domini : tipus.getDominis())
				if (command.getDominis().contains(domini.getCodi())) {
					dominiExportacio = new DominiExportacio(
							domini.getCodi(),
							domini.getNom(),
							domini.getTipus().toString());
					dominiExportacio.setDescripcio(domini.getDescripcio());
					dominiExportacio.setUrl(domini.getUrl());
					if (domini.getTipusAuth() != null)
						dominiExportacio.setTipusAuth(DominiDto.TipusAuthDomini.valueOf(domini.getTipusAuth().toString()));
					if (domini.getOrigenCredencials() != null)
						dominiExportacio.setOrigenCredencials(DominiDto.OrigenCredencials.valueOf(domini.getOrigenCredencials().toString()));
					dominiExportacio.setUsuari(domini.getUsuari());
					dominiExportacio.setContrasenya(domini.getContrasenya());
					dominiExportacio.setJndiDatasource(domini.getJndiDatasource());
					dominiExportacio.setSql(domini.getSql());
					dominiExportacio.setCacheSegons(domini.getCacheSegons());
					if (domini.getTimeout() != null)
						dominiExportacio.setTimeout(domini.getTimeout().intValue());
					exportacio.getDominis().add(dominiExportacio);
				}
		}	
		// Consultes
		if (command.getConsultes().size() > 0) {
			ConsultaExportacio consultaExportacio;
			for (Consulta consulta : tipus.getConsultes())
				if (command.getConsultes().contains(consulta.getCodi())) {
					consultaExportacio = new ConsultaExportacio(
							consulta.getCodi(),
							consulta.getNom());
					consultaExportacio.setDescripcio(consulta.getDescripcio());
					consultaExportacio.setValorsPredefinits(consulta.getValorsPredefinits());
					consultaExportacio.setExportarActiu(consulta.isExportarActiu());
					consultaExportacio.setOcultarActiu(consulta.isOcultarActiu());
					consultaExportacio.setOrdre(consulta.getOrdre());
					if (consulta.getInformeContingut() != null && consulta.getInformeContingut().length > 0) {
						consultaExportacio.setInformeNom(consulta.getInformeNom());
						consultaExportacio.setInformeContingut(consulta.getInformeContingut());
						consultaExportacio.setFormatExport(consulta.getFormatExport());
					}
					exportacio.getConsultes().add(consultaExportacio);
					// Camps de la consulta
					for (ConsultaCamp consultaCamp: consulta.getCamps()) {
						ConsultaCampExportacio consultaCampExp = new ConsultaCampExportacio(
								consultaCamp.getCampCodi(),
								consultaCamp.getDefprocJbpmKey(),
								ConsultaCampDto.TipusConsultaCamp.valueOf(consultaCamp.getTipus().toString()),
								consultaCamp.getParamTipus() != null?
										ConsultaCampDto.TipusParamConsultaCamp.valueOf(consultaCamp.getParamTipus().toString())
										: null,
								consultaCamp.getCampDescripcio(),
								consultaCamp.getOrdre(),
								consultaCamp.getAmpleCols(),
								consultaCamp.getBuitCols());
						consultaExportacio.getCamps().add(consultaCampExp);
					}
				}
		}	
		
		// Herència
		if (command.isTasquesHerencia())
			// Exporta la informació de les dades d'herència
			exportacio.setHerenciaTasques(definicioProcesHelper.getHerenciaTasques(tipus));
		
		return exportacio;
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ExpedientTipusDto importar(
			Long entornId, 
			Long expedientTipusId, 
			ExpedientTipusExportacioCommandDto command,
			ExpedientTipusExportacio importacio) {
		logger.debug(
				"Important un tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId = " + command.getId() + ", " + 
				"command = " + command + ", " + 
				"importacio = " + importacio + ")");
		// Control d'accés
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		// Dades del tipus d'expedient
		boolean expedientTipusExisteix = expedientTipusId != null;
		ExpedientTipus expedientTipus;
		ExpedientTipus expedientTipusPare = null;
		if (!expedientTipusExisteix) {
			// Nou tipus d'expedient
			expedientTipus = new ExpedientTipus();
			expedientTipus.setEntorn(entorn);
			expedientTipus.setCodi(command.getCodi());
			expedientTipus.setNom(importacio.getNom());
			expedientTipus = expedientTipusRepository.save(expedientTipus);
		} else			
			// Recupera el tipus d'expedient existent
			if (entornHelper.potDissenyarEntorn(entornId)) {
				// Si te permisos de disseny a damunt l'entorn pot veure tots els tipus
				expedientTipus = expedientTipusRepository.findByEntornAndId(
						entorn,
						command.getId());
			} else {
				// Si no te permisos de disseny a damunt l'entorn només es poden veure
				// els tipus amb permisos de disseny
				expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
						command.getId());
			}
		if (!expedientTipusExisteix || command.isDadesBasiques()) {
			expedientTipus.setNom(importacio.getNom());
			expedientTipus.setTipus(importacio.getTipus());
			expedientTipus.setAmbInfoPropia(importacio.isAmbInfoPropia());
			expedientTipus.setTeTitol(importacio.isTeTitol());
			expedientTipus.setDemanaTitol(importacio.isDemanaTitol());
			expedientTipus.setTeNumero(importacio.isTeNumero());
			expedientTipus.setDemanaNumero(importacio.isDemanaNumero());
			expedientTipus.setExpressioNumero(importacio.getExpressioNumero());
			expedientTipus.setJbpmProcessDefinitionKey(importacio.getJbpmProcessDefinitionKey());
			expedientTipus.setReiniciarCadaAny(importacio.isReiniciarCadaAny());
			expedientTipus.setSequencia(importacio.getSequencia());
			expedientTipus.setResponsableDefecteCodi(importacio.getResponsableDefecteCodi());
			expedientTipus.setRestringirPerGrup(importacio.isRestringirPerGrup());
			expedientTipus.setSeleccionarAny(importacio.isSeleccionarAny());
			expedientTipus.setAmbRetroaccio(importacio.isAmbRetroaccio());
			expedientTipus.setReindexacioAsincrona(importacio.isReindexacioAsincrona());
			expedientTipus.setTramitacioMassiva(importacio.isTramitacioMassiva());
			expedientTipus.setHeretable(importacio.isHeretable());
			if (importacio.getExpedientTipusPareCodi() !=  null) {
				expedientTipusPare = expedientTipusRepository.findByEntornAndCodi(entorn, importacio.getExpedientTipusPareCodi());
				expedientTipus.setExpedientTipusPare(expedientTipusPare);
			}
			if (importacio.isReiniciarCadaAny()) {
				Collection<SequenciaAnyDto> sequencies = importacio.getSequenciaAny().values();
				for (SequenciaAnyDto sequencia : sequencies) {
					SequenciaAny anyexpedientTipus = new SequenciaAny(
							expedientTipus, 
							sequencia.getAny(), 
							sequencia.getSequencia());
					expedientTipus.getSequenciaAny().put(anyexpedientTipus.getAny(), anyexpedientTipus);
				}
			}
		}
		// Metadades NTI i Arxiu
		expedientTipus.setNtiActiu(importacio.isNtiActiu());
		expedientTipus.setNtiOrgano(importacio.getNtiOrgano());
		expedientTipus.setNtiClasificacion(importacio.getNtiClasificacion());
		expedientTipus.setNtiSerieDocumental(importacio.getNtiSerieDocumental());
		expedientTipus.setArxiuActiu(importacio.isArxiuActiu());
		// Integracio amb DISTRIBUCIO
		expedientTipus.setDistribucioActiu(importacio.isDistribucioActiu());
		expedientTipus.setDistribucioCodiAssumpte(importacio.getDistribucioCodiAssumpte());
		expedientTipus.setDistribucioCodiProcediment(importacio.getDistribucioCodiProcediment());
		expedientTipus.setDistribucioProcesAuto(importacio.isDistribucioProcesAuto());
		expedientTipus.setDistribucioSistra(importacio.isDistribucioSistra());
		expedientTipus.setSistraActiu(importacio.isSistraActiu());
		// Integració amb NOTIB
		expedientTipus.setNotibActiu(importacio.getNotibActiu());
		expedientTipus.setNotibEmisor(importacio.getNotibEmisor());
		expedientTipus.setNotibCodiProcediment(importacio.getNotibCodiProcediment());
		// Integració amb formularis externs
		if (command.isIntegracioForms()) {
			expedientTipus.setFormextUrl(importacio.getFormextUrl());
			expedientTipus.setFormextUsuari(importacio.getFormextUsuari());
			expedientTipus.setFormextContrasenya(importacio.getFormextContrasenya());
		}
		// Integració amb Sistra
		if (command.isIntegracioSistra()) {
			expedientTipus.setSistraActiu(importacio.getSistraTramitCodi() != null);
			expedientTipus.setSistraTramitCodi(importacio.getSistraTramitCodi());
			if (expedientTipusExisteix) {
				// esborra els mapegos existents
				for (MapeigSistra mapeig : expedientTipus.getMapeigSistras())
					mapeigSistraRepository.delete(mapeig);
				expedientTipus.getMapeigSistras().clear();
				mapeigSistraRepository.flush();
			}
			for (MapeigSistraExportacio mapeig : importacio.getSistraMapejos())
				expedientTipus.getMapeigSistras().add(
						new MapeigSistra(
								expedientTipus,
								mapeig.getCodiHelium(), 
								mapeig.getCodiSistra(), 
								MapeigSistra.TipusMapeig.valueOf(mapeig.getTipus().toString())));
		}
		
		//Integració amb Pinbal
		expedientTipus.setPinbalActiu(importacio.isPinbalActiu());
		expedientTipus.setPinbalNifCif(importacio.getPinbalNifCif());
		
		boolean sobreEscriure = command.isSobreEscriure();
		// Estats
		Estat estat;
		if (command.getEstats().size() > 0)
			for(EstatExportacio estatExportat : importacio.getEstats() )
				if (command.getEstats().contains(estatExportat.getCodi())){
					estat = null;
					if (expedientTipusExisteix) {
						estat = estatRepository.findByExpedientTipusIdAndCodi(expedientTipus.getId(), estatExportat.getCodi());
					}
					if (estat == null || sobreEscriure) {
						if (estat == null) {
							estat = new Estat(
									expedientTipus, 
									estatExportat.getCodi(), 
									estatExportat.getNom());
							expedientTipus.getEstats().add(estat);
							estatRepository.save(estat);
						} else {
							estat.setNom(estatExportat.getNom());
						}
						estat.setOrdre(estatExportat.getOrdre());
					}
				}
		
		// Agrupacions
		Map<String, CampAgrupacio> agrupacions = new HashMap<String, CampAgrupacio>();
		CampAgrupacio agrupacio;
		if (command.getAgrupacions().size() > 0)
			for(AgrupacioExportacio agrupacioExportat : importacio.getAgrupacions() )
				if (command.getAgrupacions().contains(agrupacioExportat.getCodi())){
					agrupacio = null;
					if (expedientTipusExisteix) {
						agrupacio = campAgrupacioRepository.findByExpedientTipusIdAndCodi(expedientTipus.getId(), agrupacioExportat.getCodi());
					}
					if (agrupacio == null || sobreEscriure) {
						if (agrupacio == null) {
							agrupacio = new CampAgrupacio(
									expedientTipus, 
									agrupacioExportat.getCodi(), 
									agrupacioExportat.getNom(),
									agrupacioExportat.getOrdre());
							expedientTipus.getAgrupacions().add(agrupacio);
							campAgrupacioRepository.save(agrupacio);
						} else {
							agrupacio.setNom(agrupacioExportat.getNom());
							agrupacio.setOrdre(agrupacioExportat.getOrdre());
						}
						agrupacio.setDescripcio(agrupacioExportat.getDescripcio());
					}
					agrupacions.put(agrupacio.getCodi(), agrupacio);
				}

		// Enumeracions
		Map<String, Enumeracio> enumeracions = new HashMap<String, Enumeracio>();
		// Inicialitza amb les enumeracions a nivell de tipus d'expedient
		if (expedientTipusExisteix)
			for (Enumeracio e : enumeracioRepository.findAmbExpedientTipus(expedientTipusId))
				enumeracions.put(e.getCodi(), e);
		// Inicialitza amb les enumeracions a nivell d'entorn
		Map<String, Enumeracio> enumeracionsEntorn = new HashMap<String, Enumeracio>();
		for (Enumeracio e : enumeracioRepository.findGlobals(entorn.getId()))
			enumeracionsEntorn.put(e.getCodi(), e);
		Enumeracio enumeracio;
		if (command.getEnumeracions().size() > 0)
			for(EnumeracioExportacio enumeracioExportat : importacio.getEnumeracions() )
				if (command.getEnumeracions().contains(enumeracioExportat.getCodi())){
					enumeracio = enumeracions.get(enumeracioExportat.getCodi());
					if (enumeracio == null || sobreEscriure) {
						if (enumeracio == null) {
							enumeracio = new Enumeracio(
									entorn, 
									enumeracioExportat.getCodi(), 
									enumeracioExportat.getNom());
							enumeracio.setExpedientTipus(expedientTipus);
							expedientTipus.getEnumeracions().add(enumeracio);
							enumeracioRepository.save(enumeracio);
							enumeracions.put(enumeracio.getCodi(), enumeracio);
						} else {
							enumeracio.setNom(enumeracioExportat.getNom());
							// Esborra tots els valors existents
							for (EnumeracioValors valor : enumeracio.getEnumeracioValors())
								enumeracioValorsRepository.delete(valor);
							enumeracio.getEnumeracioValors().clear();
							enumeracioValorsRepository.flush();
						}
						// Afegeix tots els valors
						for (EnumeracioValorExportacio valorExportat : enumeracioExportat.getValors()) {
							EnumeracioValors valor = new EnumeracioValors(enumeracio, valorExportat.getCodi(), valorExportat.getNom());
							valor.setOrdre(valorExportat.getOrdre());
							enumeracio.getEnumeracioValors().add(valor);
							enumeracioValorsRepository.save(valor);
						}
					}
				}
		
		// Dominis
		Map<String, Domini> dominis = new HashMap<String, Domini>();
		// Inicialitza amb els dominis a nivell de tipus d'expedient
		if (expedientTipusExisteix)
			for (Domini d : dominiRepository.findAmbExpedientTipus(expedientTipusId))
				dominis.put(d.getCodi(), d);
		// Inicialitza amb els dominis a nivell d'entorn
		Map<String, Domini> dominisEntorn = new HashMap<String, Domini>();
		for (Domini d : dominiRepository.findGlobals(entorn.getId()))
			dominisEntorn.put(d.getCodi(), d);
		Domini domini;
		if (command.getDominis().size() > 0)
			for(DominiExportacio dominiExportat : importacio.getDominis() )
				if (command.getDominis().contains(dominiExportat.getCodi())){
					domini = dominis.get(dominiExportat.getCodi());
					if (domini == null || sobreEscriure) {
						if (domini == null) {
							domini = new Domini(
									dominiExportat.getCodi(), 
									dominiExportat.getNom(),
									entorn);
							domini.setExpedientTipus(expedientTipus);
							expedientTipus.getDominis().add(domini);
							domini.setTipus(TipusDomini.valueOf(dominiExportat.getTipus().toString()));
							dominiRepository.save(domini);
							dominis.put(domini.getCodi(), domini);
						} else {
							domini.setNom(dominiExportat.getNom());
							domini.setTipus(TipusDomini.valueOf(dominiExportat.getTipus().toString()));
						}
						domini.setDescripcio(dominiExportat.getDescripcio());
						domini.setCacheSegons(dominiExportat.getCacheSegons());
						domini.setTimeout(dominiExportat.getTimeout());
						// SQL
						domini.setJndiDatasource(dominiExportat.getJndiDatasource());
						domini.setSql(dominiExportat.getSql());
						// WS
						domini.setUrl(dominiExportat.getUrl());
						domini.setTipusAuth(dominiExportat.getTipusAuth() != null?
											 Domini.TipusAuthDomini.valueOf(dominiExportat.getTipusAuth().toString())
											 : null);
						domini.setOrigenCredencials(dominiExportat.getOrigenCredencials() != null?
											Domini.OrigenCredencials.valueOf(dominiExportat.getOrigenCredencials().toString())
											: null);
						domini.setUsuari(dominiExportat.getUsuari());
						domini.setContrasenya(dominiExportat.getContrasenya());
					}
				}
		
		// Camps
		Map<String, Camp> camps = new HashMap<String, Camp>();
		Map<Camp, CampExportacio> registres = new HashMap<Camp, CampExportacio>();
		Map<Camp, CampExportacio> campsTipusConsulta = new HashMap<Camp, CampExportacio>();
		Camp camp;
		if (command.getVariables().size() > 0) {
			for(CampExportacio campExportat : importacio.getCamps() )
				if (command.getVariables().contains(campExportat.getCodi())){
					camp = null;
					if (expedientTipusExisteix) {
						camp = campRepository.findByExpedientTipusAndCodi(expedientTipus.getId(), campExportat.getCodi(), false);
					}
					if (camp == null || sobreEscriure) {
						if (camp == null) {
							camp = new Camp(
									expedientTipus, 
									campExportat.getCodi(),
									TipusCamp.valueOf(campExportat.getTipus().toString()),
									campExportat.getEtiqueta());
							expedientTipus.getCamps().add(camp);
							camp = campRepository.save(camp);
						} else {
							camp.setTipus(TipusCamp.valueOf(campExportat.getTipus().toString()));
							camp.setEtiqueta(campExportat.getEtiqueta());
						}
						camp.setIgnored(campExportat.isIgnored());
						camp.setObservacions(campExportat.getObservacions());
						camp.setDominiId(campExportat.getDominiId());
						camp.setDominiParams(campExportat.getDominiParams());
						camp.setDominiCampText(campExportat.getDominiCampText());
						camp.setDominiCampValor(campExportat.getDominiCampValor());
						camp.setDominiCacheText(campExportat.isDominiCacheText());
						camp.setConsultaParams(campExportat.getConsultaParams());
						camp.setConsultaCampText(campExportat.getConsultaCampText());
						camp.setConsultaCampValor(campExportat.getConsultaCampValor());
						camp.setMultiple(campExportat.isMultiple());
						camp.setOcult(campExportat.isOcult());
						camp.setDominiIntern(campExportat.isDominiIntern());
						camp.setDefprocJbpmKey(campExportat.getDefprocJbpmKey());
						camp.setJbpmAction(campExportat.getJbpmAction());
						camp.setOrdre(campExportat.getOrdre());
						
						// Esborra les validacions existents
						for (Validacio validacio : camp.getValidacions())
							campValidacioRepository.delete(validacio);
						camp.getValidacions().clear();
						campValidacioRepository.flush();
						// Afegeix les noves validacions
						for (ValidacioExportacio validacioExportat : campExportat.getValidacions()) {
							Validacio validacio = new Validacio(
									camp, 
									validacioExportat.getExpressio(), 
									validacioExportat.getMissatge());
							validacio.setNom(validacioExportat.getNom());
							validacio.setOrdre(validacioExportat.getOrdre());
							campValidacioRepository.save(validacio);
							camp.getValidacions().add(validacio);
						}
						// Agrupació del camp
						if (campExportat.getAgrupacioCodi() != null && agrupacions.containsKey(campExportat.getAgrupacioCodi()))
							camp.setAgrupacio(agrupacions.get(campExportat.getAgrupacioCodi()));
						// Enumeració del camp
						if (campExportat.getCodiEnumeracio() != null) {
							enumeracio = null;
							if (campExportat.isDependenciaEntorn()) {
								// Enumeració de l'entorn
								enumeracio = enumeracionsEntorn.get(campExportat.getCodiEnumeracio());
							} else {
								// Enumeració TE o importades
								enumeracio = enumeracions.get(campExportat.getCodiEnumeracio());
							}
							if (enumeracio == null)
								throw new DeploymentException(
										messageHelper.getMessage(
											"exportar.validacio.variable.seleccio.enumeracio." + (campExportat.isDependenciaEntorn() ? "entorn" : "tipexp"), 
											new Object[]{
													camp.getCodi(),
													campExportat.getCodiEnumeracio()}));
							camp.setEnumeracio(enumeracio);
						}
						// Domini del camp
						if (campExportat.getCodiDomini() != null) {
							domini = null;
							if (campExportat.isDependenciaEntorn()) {
								// Domini de l'entorn
								domini = dominisEntorn.get(campExportat.getCodiDomini());
							} else {
								// Domini TE o importats
								domini = dominis.get(campExportat.getCodiDomini());
							}
							if (domini == null)
								throw new DeploymentException(
										messageHelper.getMessage(
											"exportar.validacio.variable.seleccio.domini." + (campExportat.isDependenciaEntorn() ? "entorn" : "tipexp"), 
											new Object[]{
													camp.getCodi(),
													campExportat.getCodiDomini()}));
							camp.setDomini(domini);
						}
						// Guarda els camps de tipus consulta per processar-los després de les consultes
						if (campExportat.getCodiConsulta() != null)
							campsTipusConsulta.put(camp, campExportat);
						// Guarda els registres per processar-los després de tots els camps
						if (camp.getTipus() == TipusCamp.REGISTRE) {
							registres.put(camp, campExportat);
						}						
					}
					expedientTipus.getCamps().add(camp);
					camps.put(camp.getCodi(), camp);
					campRepository.saveAndFlush(camp);
				}		
			// Tracta els registres
			CampExportacio campExportat;
			for (Camp registre : registres.keySet()) {
				// Esborra la relació amb els membres existents
				if (!registre.getRegistreMembres().isEmpty()) {
					for (CampRegistre campRegistre : registre.getRegistreMembres()) {
						campRegistre.getMembre().getRegistrePares().remove(campRegistre);
						campRegistre.setMembre(null);
						campRegistre.setRegistre(null);
						campRegistreRepository.delete(campRegistre);
					}
					registre.getRegistreMembres().clear();
					campRegistreRepository.flush();
				}
				// afegeix la informació dels registres
				campExportat = registres.get(registre);
				for (RegistreMembreExportacio registreMembre : campExportat.getRegistreMembres()) {
					CampRegistre campRegistre = new CampRegistre(
							camps.get(registre.getCodi()),
							camps.get(registreMembre.getCodi()),
							registreMembre.getOrdre());
					campRegistre.setLlistar(registreMembre.isLlistar());
					campRegistre.setObligatori(registreMembre.isObligatori());
					registre.getRegistreMembres().add(campRegistre);
					campRegistreRepository.save(campRegistre);
				}
			}
		}
		
		// Documents
		Map<String, Document> documents = new HashMap<String, Document>();
		Document document;
		if (command.getDocuments().size() > 0)
			for(DocumentExportacio documentExportat : importacio.getDocuments() )
				if (command.getDocuments().contains(documentExportat.getCodi())){
					document = null;
					if (expedientTipusExisteix) {
						document = documentRepository.findByExpedientTipusAndCodi(expedientTipus.getId(), documentExportat.getCodi(), false);
					}
					if (document == null || sobreEscriure) {
						if (document == null) {
							document = new Document(
									expedientTipus, 
									documentExportat.getCodi(), 
									documentExportat.getNom());
							expedientTipus.getDocuments().add(document);
							documentRepository.save(document);
						} else {
							document.setNom(documentExportat.getNom());
						}
						document.setDescripcio(documentExportat.getDescripcio());
						document.setArxiuNom(documentExportat.getArxiuNom());
						document.setArxiuContingut(documentExportat.getArxiuContingut());
						document.setPlantilla(documentExportat.isPlantilla());
						document.setNotificable(documentExportat.isNotificable());
						document.setCustodiaCodi(documentExportat.getCustodiaCodi());
						document.setContentType(documentExportat.getContentType());
						document.setTipusDocPortasignatures(documentExportat.getTipusDocPortasignatures());
						document.setAdjuntarAuto(documentExportat.isAdjuntarAuto());
						if (documentExportat.getCodiCampData() != null)
							document.setCampData(camps.get(documentExportat.getCodiCampData()));
						document.setConvertirExtensio(documentExportat.getConvertirExtensio());
						document.setExtensionsPermeses(documentExportat.getExtensionsPermeses());
						document.setIgnored(documentExportat.isIgnored());
						document.setNtiTipoDocumental(documentExportat.getNtiTipoDocumental());
					}
					expedientTipus.getDocuments().add(document);
					documents.put(documentExportat.getCodi(), document);
				}	
		
		// Terminis
		Termini termini;
		if (command.getTerminis().size() > 0)
			for(TerminiExportacio terminiExportat : importacio.getTerminis() )
				if (command.getTerminis().contains(terminiExportat.getCodi())){
					termini = null;
					if (expedientTipusExisteix) {
						termini = terminiRepository.findByExpedientTipusAndCodi(expedientTipus, terminiExportat.getCodi());
					}
					if (termini == null || sobreEscriure) {
						if (termini == null) {
							termini = new Termini(
									expedientTipus,
									terminiExportat.getCodi(),
									terminiExportat.getNom(),
									terminiExportat.getAnys(),
									terminiExportat.getMesos(),
									terminiExportat.getDies(),
									terminiExportat.isLaborable());
							expedientTipus.getTerminis().add(termini);
							terminiRepository.save(termini);
						} else {
							termini.setNom(terminiExportat.getNom());
							termini.setNom(terminiExportat.getNom());
							termini.setAnys(terminiExportat.getAnys());
							termini.setMesos(terminiExportat.getMesos());
							termini.setDies(terminiExportat.getDies());
							termini.setLaborable(terminiExportat.isLaborable());
						}
						termini.setDuradaPredefinida(terminiExportat.isDuradaPredefinida());
						termini.setDescripcio(terminiExportat.getDescripcio());
						termini.setDiesPrevisAvis(terminiExportat.getDiesPrevisAvis());
						termini.setAlertaPrevia(terminiExportat.isAlertaPrevia());
						termini.setAlertaFinal(terminiExportat.isAlertaFinal());
						termini.setAlertaCompletat(terminiExportat.isAlertaCompletat());
						termini.setManual(terminiExportat.isManual());
					}
				}

		// Accions
		Accio accio;
		if (command.getAccions().size() > 0)
			for(AccioExportacio accioExportat : importacio.getAccions() )
				if (command.getAccions().contains(accioExportat.getCodi())){
					accio = null;
					if (expedientTipusExisteix) {
						accio = accioRepository.findByExpedientTipusIdAndCodi(expedientTipus.getId(), accioExportat.getCodi());
					}
					if (accio == null || sobreEscriure) {
						if (accio == null) {
							accio = new Accio(
									expedientTipus, 
									accioExportat.getCodi(), 
									accioExportat.getNom(),
									accioExportat.getTipus(),
									accioExportat.getDefprocJbpmKey(),
									accioExportat.getJbpmAction(),
									accioExportat.getPredefinitClasse(),
									accioExportat.getPredefinitDades());
							expedientTipus.getAccions().add(accio);
							accioRepository.save(accio);
						} else {
							accio.setNom(accioExportat.getNom());
							accio.setJbpmAction(accioExportat.getJbpmAction());
						}
						accio.setDescripcio(accioExportat.getDescripcio());
						accio.setPublica(accioExportat.isPublica());
						accio.setOculta(accioExportat.isOculta());
						accio.setRols(accioExportat.getRols());
					}
				}
		
		// Definicions
		DefinicioProces definicioProces;
		if (command.getDefinicionsProces().size() > 0) {
			for(DefinicioProcesExportacio definicioExportat : importacio.getDefinicions() )
				if (command.getDefinicionsProces().contains(definicioExportat.getDefinicioProcesDto().getJbpmKey())){
					// Id de la definició de procés sobre la qual s'importa la informacó
					Long definicioProcesId = null;
					if (!command.isDesplegarDefinicions()) {
						// Busca la darrera versió de la definició de procés pel tipus d'expedient
						if (expedientTipusId != null) {
							DefinicioProces darreraVersio =
										definicioProcesRepository.findDarreraVersioAmbTipusExpedientIJbpmKey(expedientTipusId, 
												definicioExportat.getDefinicioProcesDto().getJbpmKey());
							if (darreraVersio != null) {
								definicioProcesId = darreraVersio.getId();
							}
						}
					}
					definicioProces = definicioProcesHelper.importar(
							entornId,
							expedientTipus.getId(),
							definicioProcesId,
							definicioExportat,
							null,
							command.isSobreEscriure());
					expedientTipus.addDefinicioProces(definicioProces);
				}
		}

		// Consultes
		Map<String, Consulta> consultes = new HashMap<String, Consulta>();
		Consulta consulta;
		if (command.getConsultes().size() > 0) {
			// Map<jbpmKey, versio> de les definicions de procés
			Map<String, Integer> definicionsProcesVersio = new HashMap<String, Integer>();
			// Consulta la darrera versió de totes les definicions de procés incloent les heretades i les de l'entorn
			for (DefinicioProces definicio : definicioProcesHelper.findAllDarreraVersio(entornId, expedientTipus))
				definicionsProcesVersio.put(definicio.getJbpmKey(), definicio.getVersio());			
			// Importa la informació de les consultes
			for(ConsultaExportacio consultaExportat : importacio.getConsultes() )
				if (command.getConsultes().contains(consultaExportat.getCodi())) {
					consulta = null;
					if (expedientTipusExisteix) {
						consulta = consultaRepository.findByExpedientTipusAndCodi(expedientTipus, consultaExportat.getCodi());
					}
					if (consulta == null || sobreEscriure) {
						if (consulta == null) {
							consulta = new Consulta(
									consultaExportat.getCodi(), 
									consultaExportat.getNom());
							consulta.setEntorn(entorn);
							consulta.setExpedientTipus(expedientTipus);
							expedientTipus.getConsultes().add(consulta);
							consultaRepository.save(consulta);
						} else {
							consulta.setNom(consultaExportat.getNom());
							// Esborra els paràmetres existents
							for (ConsultaCamp consultaCamp : consulta.getCamps())
								consultaCampRepository.delete(consultaCamp);
							consulta.getCamps().clear();
							consultaCampRepository.flush();
						}
						consulta.setDescripcio(consultaExportat.getDescripcio());
						consulta.setValorsPredefinits(consultaExportat.getValorsPredefinits());
						consulta.setExportarActiu(consultaExportat.isExportarActiu());
						consulta.setOcultarActiu(consultaExportat.isOcultarActiu());
						consulta.setOrdre(consultaExportat.getOrdre());	
						if (consultaExportat.getInformeContingut() != null) {
							consulta.setInformeNom(consultaExportat.getInformeNom());
							consulta.setInformeContingut(consultaExportat.getInformeContingut());
							consulta.setFormatExport(consultaExportat.getFormatExport());
						} else {
							consulta.setInformeNom(null);
							consulta.setInformeContingut(null);
							consulta.setFormatExport(null);
						}
						// Variables i paràmetres de la consulta
						int versio;
						for (ConsultaCampExportacio consultaCampExportacio : consultaExportat.getCamps()) {
							ConsultaCamp consultaCamp = new ConsultaCamp(
									consultaCampExportacio.getCampCodi(), 
									ConsultaCamp.TipusConsultaCamp.valueOf(
											consultaCampExportacio.getTipusConsultaCamp().toString()));
							consultaCamp.setConsulta(consulta);
							consultaCamp.setDefprocJbpmKey(consultaCampExportacio.getJbpmKey());
							if (definicionsProcesVersio.containsKey(consultaCampExportacio.getJbpmKey()))
								versio = definicionsProcesVersio.get(consultaCampExportacio.getJbpmKey());
							else
								versio = -1;			
							consultaCamp.setDefprocVersio(versio);
							consultaCamp.setCampDescripcio(consultaCampExportacio.getCampDescripcio());
							consultaCamp.setParamTipus(
									consultaCampExportacio.getTipusParamConsultaCamp() != null?
									TipusParamConsultaCamp.valueOf(consultaCampExportacio.getTipusParamConsultaCamp().toString())
									: null);
							consultaCamp.setOrdre(consultaCampExportacio.getOrdre());
							consultaCamp.setAmpleCols(consultaCampExportacio.getAmpleCols());
							consultaCamp.setBuitCols(consultaCampExportacio.getBuitCols());
							consultaCampRepository.save(consultaCamp);
							consulta.getCamps().add(consultaCamp);
						}
					}
					consultes.put(consulta.getCodi(), consulta);
				}
		}
		// Tracta camps de tipus consulta completant la referència a la consulta
		CampExportacio campExportat;
		for (Camp campConsulta : campsTipusConsulta.keySet()) {
			// afegeix la informació dels registres
			campExportat = campsTipusConsulta.get(campConsulta);
			if (consultes.containsKey(campExportat.getCodiConsulta()))
				campConsulta.setConsulta(consultes.get(campExportat.getCodiConsulta()));
		}

		// Herència
		if (command.isTasquesHerencia() && expedientTipusPare != null) {
			// Herencia
			List<TascaExportacio> tasquesExportacio;
			// Informacó de les tasques
			for ( String definicioProcesJbpmkey : importacio.getHerenciaTasques().keySet()) {
				// S'han de relacionar els camps, documents i firmes indicats amb la informació inclosa
				tasquesExportacio = importacio.getHerenciaTasques().get(definicioProcesJbpmkey);
				definicioProcesHelper.importarTascaHerencia(
						expedientTipus,
						expedientTipusPare,
						definicioProcesJbpmkey, 
						tasquesExportacio);
			}
		}

		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(expedientTipus),
				ExpedientTipusDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ExpedientTipusDto findAmbId(Long expedientTipusId) throws NoTrobatException {
		logger.debug(
				"Consultant tipus d'expedient amb id (" +
				"expedientTipusId=" + expedientTipusId + ")");
		
		ExpedientTipus tipus = expedientTipusRepository.findOne(expedientTipusId);
		
		if (tipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		
		return conversioTipusHelper.convertir(
				tipus,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public List<ExpedientTipusDto> findAmbEntornPermisConsultar(
			Long entornId) {
		logger.debug(
				"Consultant tipus d'expedient per un entorn i amb permisos de consulta (" +
				"entornId=" + entornId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<ExpedientTipus> tipuss = expedientTipusRepository.findByEntornOrderByNomAsc(entorn);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		permisosHelper.filterGrantedAny(
				tipuss,
				new ObjectIdentifierExtractor<ExpedientTipus>() {
					@Override
					public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
						return expedientTipus.getId();
					}
				},
				ExpedientTipus.class,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION},
				auth);
		return conversioTipusHelper.convertirList(
				tipuss,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findAmbIdPermisConsultar(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Consultant tipus d'expedient amb id i amb permisos de consulta (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId = " + expedientTipusId + ")");
		ExpedientTipus tipus = expedientTipusHelper.getExpedientTipusComprovantPermisLectura(
				expedientTipusId);
		return conversioTipusHelper.convertir(
				tipus,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public List<ExpedientTipusDto> findAmbEntornPermisDissenyar(
			Long entornId) {
		logger.debug(
				"Consultant tipus d'expedient per un entorn i amb permisos de disseny (" +
				"entornId=" + entornId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<ExpedientTipus> tipuss = expedientTipusRepository.findByEntornOrderByNomAsc(entorn);
		if (!entornHelper.potDissenyarEntorn(entornId)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAny(
					tipuss,
					new ObjectIdentifierExtractor<ExpedientTipus>() {
						@Override
						public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
							return expedientTipus.getId();
						}
					},
					ExpedientTipus.class,
					new Permission[] {
							ExtendedPermission.DESIGN, // permís antic
							ExtendedPermission.DESIGN_ADMIN,
							ExtendedPermission.DESIGN_DELEG,
							ExtendedPermission.ADMINISTRATION},
					auth);
		}
		return conversioTipusHelper.convertirList(
				tipuss,
				ExpedientTipusDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public List<ExpedientTipusDto> findAmbEntornPermisAnotacio(
			Long entornId) {
		logger.debug(
				"Consultant tipus d'expedient per un entorn i amb permisos sobre antotacions (" +
				"entornId=" + entornId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<ExpedientTipus> tipuss = expedientTipusRepository.findByEntornOrderByCodiAsc(entorn);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!UsuariActualHelper.isAdministrador(auth)) {
			permisosHelper.filterGrantedAny(
					tipuss,
					new ObjectIdentifierExtractor<ExpedientTipus>() {
						@Override
						public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
							return expedientTipus.getId();
						}
					},
					ExpedientTipus.class,
					new Permission[] {
							BasePermission.ADMINISTRATION,
							ExtendedPermission.RELATE},
					auth);
		}
		return conversioTipusHelper.convertirList(
				tipuss,
				ExpedientTipusDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public List<ExpedientTipusDto> findAmbEntornPermisExecucioScript(
			Long entornId) {
		logger.debug(
				"Consultant tipus d'expedient per un entorn i amb permisos d'execució d'scripts (" +
				"entornId=" + entornId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<ExpedientTipus> tipuss = expedientTipusRepository.findByEntornOrderByCodiAsc(entorn);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!UsuariActualHelper.isAdministrador(auth)) {
			permisosHelper.filterGrantedAny(
					tipuss,
					new ObjectIdentifierExtractor<ExpedientTipus>() {
						@Override
						public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
							return expedientTipus.getId();
						}
					},
					ExpedientTipus.class,
					new Permission[] {
							BasePermission.ADMINISTRATION,
							ExtendedPermission.SCRIPT_EXE},
					auth);
		}
		return conversioTipusHelper.convertirList(
				tipuss,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findAmbIdPermisDissenyar(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Consultant tipus d'expedient amb id i amb permisos de disseny (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId = " + expedientTipusId + ")");
		ExpedientTipus tipus;
		if (entornHelper.potDissenyarEntorn(entornId))
			tipus = expedientTipusRepository.findOne(expedientTipusId);
		else
			tipus = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
				expedientTipusId);
		
		ExpedientTipusDto tipusDto = conversioTipusHelper.convertir(
				tipus,
				ExpedientTipusDto.class); 
		
		// Omple els permisos del tipus d'expedient
		expedientHelper.omplirPermisosExpedientTipus(tipusDto);

		return tipusDto;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findAmbIdPermisDissenyarDelegat(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Consultant tipus d'expedient amb id i amb permisos de disseny delegat (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId = " + expedientTipusId + ")");
		ExpedientTipus tipus;
		if (entornHelper.potDissenyarEntorn(entornId))
			tipus = expedientTipusRepository.findOne(expedientTipusId);
		else
			tipus = expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(
				expedientTipusId);
		
		ExpedientTipusDto tipusDto = conversioTipusHelper.convertir(
				tipus,
				ExpedientTipusDto.class); 
		
		// Omple els permisos del tipus d'expedient
		expedientHelper.omplirPermisosExpedientTipus(tipusDto);

		return tipusDto;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public List<ExpedientTipusDto> findAmbEntornPermisCrear(
			Long entornId) {
		logger.debug(
				"Consultant tipus d'expedient per un entorn i amb permis de creació (" +
				"entornId=" + entornId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<ExpedientTipus> tipuss = expedientTipusRepository.findByEntornOrderByCodiAsc(entorn);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		permisosHelper.filterGrantedAny(
				tipuss,
				new ObjectIdentifierExtractor<ExpedientTipus>() {
					@Override
					public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
						return expedientTipus.getId();
					}
				},
				ExpedientTipus.class,
				new Permission[] {
						ExtendedPermission.CREATE,
						ExtendedPermission.ADMINISTRATION},
				auth);
		return conversioTipusHelper.convertirList(
				tipuss,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public List<ExpedientTipusDto> findAmbEntorn(
			Long entornId) {
		logger.debug(
				"Consultant tipus d'expedient per un entorn (" +
				"entornId=" + entornId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<ExpedientTipus> tipuss = expedientTipusRepository.findByEntornOrderByCodiAsc(entorn);
		return conversioTipusHelper.convertirList(
				tipuss,
				ExpedientTipusDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findAmbCodiPerValidarRepeticio(
			Long entornId,
			String codi) {
		logger.debug(
				"Consultant tipus d'expedient amb codi (" +
				"entornId=" + entornId + ", " +
				"codi = " + codi + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		return conversioTipusHelper.convertir(
				expedientTipusRepository.findByEntornAndCodi(entorn, codi),
				ExpedientTipusDto.class);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpedientTipusDto findAmbCodi(
			Long entornId, 
			String codi) throws NoTrobatException {
		logger.debug(
				"Consultant tipus d'expedient amb codi (" +
				"entornId=" + entornId + ", " +
				"codi = " + codi + ")");
		Entorn entorn = entornHelper.getEntorn( entornId);
		return conversioTipusHelper.convertir(
				expedientTipusRepository.findByEntornAndCodi(entorn, codi),
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientTipusDto> findPerDatatable(
			Long entornId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant tipus d'expedient per datatable (" +
				"entornId=" + entornId + ", " +
				"filtre=" + filtre + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbEntorn(entorn);
		// Filtra els expedients deixant només els permesos
		if (!entornHelper.potDissenyarEntorn(entornId)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Iterator<Long> it = tipusPermesosIds.iterator();
			while (it.hasNext()) {
				Long id = it.next();
				if (!permisosHelper.isGrantedAny(
						id,
						ExpedientTipus.class,
						new Permission[] {
								ExtendedPermission.DESIGN, // permís antic
								ExtendedPermission.DESIGN_ADMIN,
								ExtendedPermission.DESIGN_DELEG,
								ExtendedPermission.ADMINISTRATION},
						auth)) {
					it.remove();
				}
			}
		}
		PaginaDto<ExpedientTipusDto> pagina = paginacioHelper.toPaginaDto(
				tipusPermesosIds.size() > 0 ?
					expedientTipusRepository.findByFiltreGeneralPaginat(
							entorn, 
							tipusPermesosIds,
							filtre == null || "".equals(filtre),
							filtre,
							paginacioHelper.toSpringDataPageable(
									paginacioParams))
						// pàgina buida si no hi ha tipus permesos
					:	new PageImpl<ExpedientTipus>(new ArrayList<ExpedientTipus>()),
				ExpedientTipusDto.class);
		// Afegeix el contador de permisos
		List<Long> ids = new ArrayList<Long>();
		for (ExpedientTipusDto dto: pagina.getContingut()) {
			ids.add(dto.getId());
		}
		Map<Long, List<PermisDto>> permisos = permisosHelper.findPermisos(
				ids,
				ExpedientTipus.class);
		for (ExpedientTipusDto dto: pagina.getContingut()) {
			if (permisos.get(dto.getId()) != null) {
				dto.setPermisCount(permisos.get(dto.getId()).size());
			}
		}
		// Informa els permisos
		expedientHelper.omplirPermisosExpedientsTipus(pagina.getContingut());
		
		return pagina;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientTipusDto> findHeretables(Long entornId) {
		logger.debug(
				"Consultant la llista de tipus d'expedients heretables per entorn (" +
				"entornId=" + entornId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<ExpedientTipus> tipuss = expedientTipusRepository.findHeretablesByEntorn(entorn);
		return conversioTipusHelper.convertirList(
				tipuss,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientTipusDto> findHeretats(Long expedientTipusId) {
		logger.debug("Consultant els tipus heretats (expedientTipusId=" + expedientTipusId + ")");
		List<ExpedientTipus> heretats = expedientTipusRepository.findByExpedientTipusPareIdOrderByCodiAsc(expedientTipusId); 
		return conversioTipusHelper.convertirList(
				heretats, 
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void permisUpdate(
			Long entornId,
			Long expedientTipusId,
			PermisDto permis,
			boolean entornAdmin) {
		logger.debug(
				"Creant permis per al tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"permis=" + permis + ")");

		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
		Authentication authOrignal = this.permisComprovarPermisAdmin(expedientTipus, entornAdmin);
    	try {    		
    		permisosHelper.updatePermis(
    				expedientTipusId,
    				ExpedientTipus.class,
    				permis);
    	} finally {        		
			if (authOrignal != null)
				SecurityContextHolder.getContext().setAuthentication(authOrignal);
    	}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void permisDelete(
			Long entornId,
			Long expedientTipusId,
			Long permisId,
			boolean entornAdmin) {
		logger.debug(
				"Esborrant permis per al tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"permisId=" + permisId + ")");
		
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
		Authentication authOrignal = this.permisComprovarPermisAdmin(expedientTipus, entornAdmin);
    	try {    		
			permisosHelper.deletePermis(
					expedientTipusId,
					ExpedientTipus.class,
					permisId);
    	} finally {        		
			if (authOrignal != null)
				SecurityContextHolder.getContext().setAuthentication(authOrignal);
    	}
	}
	
	/** Mètode per afegir el rol d'administrador al context en el cas que l'usuari sigui administrador de
	 * l'entorn i no sigui administrador del tipus d'expedient per a poder editar els permisos del tipus
	 * d'expedient. 
	 * @param expedientTipus
	 * @param entornAdmin
	 * @return
	 */
	private Authentication permisComprovarPermisAdmin(ExpedientTipus expedientTipus, boolean entornAdmin) {
		Authentication authOrignal = null;
		// Si és administrador de l'entorn però no és administrador del TE se li afegirà el rol admin
		if (entornAdmin 
				&& !expedientTipusHelper.comprovarPermisos(
						expedientTipus, 
						null, 
						new Permission[] {ExtendedPermission.ADMINISTRATION})) {
			authOrignal = SecurityContextHolder.getContext().getAuthentication();
			// Afegeix el rol ROLE_ADMIN a la llista de rols
			@SuppressWarnings("unchecked")
			Collection<SimpleGrantedAuthority> nowAuthorities = 
				(Collection<SimpleGrantedAuthority>)SecurityContextHolder.getContext().getAuthentication().getAuthorities();
			Set<SimpleGrantedAuthority> newAuthorities = new HashSet<SimpleGrantedAuthority>(nowAuthorities);
			newAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			// Substitueix temporalent l'autenticació per poder modificar la llista tot i no ser ni propietari, ni administrador ni tenir permís d'administració
			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(
							authOrignal.getPrincipal(), 
							authOrignal.getCredentials(),
							newAuthorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);				
		}
		return authOrignal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PermisDto> permisFindAll(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Consultant permisos del tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ")");
		
		
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
		
		return permisosHelper.findPermisos(
				expedientTipusId,
				ExpedientTipus.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PermisDto permisFindById(
			Long entornId,
			Long expedientTipusId,
			Long permisId) {
		logger.debug(
				"Consultant un permis donat el seu id (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"permisId=" + permisId + ")");

		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
		
		List<PermisDto> permisos = permisosHelper.findPermisos(
				expedientTipusId,
				ExpedientTipus.class);
		for (PermisDto permis: permisos) {
			if (permis.getId().equals(permisId)) {
				return permis;
			}
		}
		throw new NoTrobatException(PermisDto.class, permisId);
	}
	
	// MANTENIMENT D'ENUMERACIONS

	@Override
	@Transactional(readOnly = true)
	public List<EnumeracioDto> enumeracioFindAll(
			Long expedientTipusId,
			boolean incloureGlobals) throws NoTrobatException, PermisDenegatException {
		List<Enumeracio> enumeracions;
		if (!incloureGlobals)
			enumeracions = enumeracioRepository.findAmbExpedientTipus(expedientTipusId);
		else
			enumeracions = enumeracioRepository.findAmbExpedientTipusIGlobals(expedientTipusId);			
		
		return conversioTipusHelper.convertirList(
									enumeracions, 
									EnumeracioDto.class);
	}
	
	// MANTENIMENT DE DOMINIS

	@Override
	@Transactional(readOnly = true)
	public List<DominiDto> dominiFindAll(
			Long expedientTipusId,
			boolean incloureGlobals) throws NoTrobatException, PermisDenegatException {
		List<Domini> dominis;
		if (!incloureGlobals)
			dominis = dominiRepository.findAmbExpedientTipus(expedientTipusId);
		else
			dominis = dominiRepository.findAmbExpedientTipusIGlobals(expedientTipusId);
		
		return conversioTipusHelper.convertirList(
									dominis, 
									DominiDto.class);
	}

	// MANTENIMENT DE CONSULTES

	@Override
	@Transactional(readOnly = true)
	public List<ConsultaDto> consultaFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		List<Consulta> consultans = consultaRepository.findAmbExpedientTipus(expedientTipusId);
		return conversioTipusHelper.convertirList(
									consultans, 
									ConsultaDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void definicioProcesDelete(Long id) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la definicioProces del tipus d'expedient (" +
				"definicioProcesId=" + id +  ")");
		DefinicioProces entity = definicioProcesRepository.findOne(id);
		
		if (entity.getExpedientTipus() != null)
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(entity.getExpedientTipus().getId());
		else
			entornHelper.getEntornComprovantPermisos(entity.getEntorn().getId(), true, true);
		
		definicioProcesRepository.delete(entity);	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DefinicioProcesDto> definicioFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
				
		List<DefinicioProces> definicions = definicioProcesRepository.findAmbExpedientTipus(expedientTipusId);
		return conversioTipusHelper.convertirList(
									definicions, 
									DefinicioProcesDto.class);
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<String> definicioProcesFindJbjmKey(
			Long entornId,
			Long expedientTipusId,
			boolean ambHerencia,
			boolean incloureGlobals) {
		logger.debug(
				"Consultant les jbpmKey de les definicions de processos per al tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"ambHerencia=" + ambHerencia + ", " +
				"incloureGlobals=" + incloureGlobals + ")");
		return definicioProcesRepository.findJbpmKeys(
				entornId, 
				expedientTipusId == null,
				expedientTipusId,
				incloureGlobals,
				ambHerencia);
	}	

	@Override
	@Transactional
	public boolean definicioProcesSetInicial(
			Long expedientTipusId, 
			Long id) {
		logger.debug(
				"Modificant la clau de la definicio de proces inicial d'un tipus d'expedient(" +
				"expedientTipusId=" + expedientTipusId +  ", definicioProcesId=" + id + ")");
		boolean ret = false;
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		DefinicioProces definicioProces = definicioProcesRepository.findOne(id);
		if (expedientTipus != null 
				&& definicioProces != null 
				&& expedientTipus.getEntorn().getId().equals(definicioProces.getEntorn().getId())) {
			expedientTipus.setJbpmProcessDefinitionKey(definicioProces.getJbpmKey());
			ret = true;
		}
		return ret;
	}
				
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ReassignacioDto reassignacioCreate(
			Long expedientTipusId, 
			ReassignacioDto reassignacio) throws PermisDenegatException {

		logger.debug(
				"Creant nova reassignacio per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"reassignacio=" + reassignacio + ")");
		
		Reassignacio entity = new Reassignacio();
				
		entity.setTipusExpedientId(reassignacio.getTipusExpedientId());
		entity.setId(reassignacio.getId());
		entity.setUsuariOrigen(reassignacio.getUsuariOrigen());
		entity.setUsuariDesti(reassignacio.getUsuariDesti());
		entity.setDataInici(reassignacio.getDataInici());
		entity.setDataFi(reassignacio.getDataFi());
		entity.setDataCancelacio(reassignacio.getDataCancelacio());

		return conversioTipusHelper.convertir(
				reassignacioRepository.save(entity),
				ReassignacioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ReassignacioDto reassignacioUpdate(ReassignacioDto reassignacio) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant la reassignacio del tipus d'expedient existent (" +
				"reassignacio.id=" + reassignacio.getId() + ", " +
				"reassignacio =" + reassignacio + ")");
		Reassignacio entity = reassignacioRepository.findOne(reassignacio.getId());

		entity.setTipusExpedientId(reassignacio.getTipusExpedientId());
		entity.setId(reassignacio.getId());
		entity.setUsuariOrigen(reassignacio.getUsuariOrigen());
		entity.setUsuariDesti(reassignacio.getUsuariDesti());
		entity.setDataInici(reassignacio.getDataInici());
		entity.setDataFi(reassignacio.getDataFi());
		entity.setDataCancelacio(reassignacio.getDataCancelacio());
				
		return conversioTipusHelper.convertir(
				reassignacioRepository.save(entity),
				ReassignacioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void reassignacioDelete(Long reassignacioReassignacioId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la reassignacio del tipus d'expedient (" +
				"reassignacioId=" + reassignacioReassignacioId +  ")");
		Reassignacio entity = reassignacioRepository.findOne(reassignacioReassignacioId);
		reassignacioRepository.delete(entity);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReassignacioDto reassignacioFindAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant la reassignacio del tipus d'expedient amb id (" +
				"reassignacioId=" + id +  ")");
		Reassignacio reassignacio = reassignacioRepository.findOne(id);
		if (reassignacio == null) {
			throw new NoTrobatException(Reassignacio.class, id);
		}
		return conversioTipusHelper.convertir(
				reassignacio,
				ReassignacioDto.class);
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ReassignacioDto> reassignacioFindPerDatatable(
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les reassignacions per al tipus d'expedient per datatable (" +
				"entornId=" + expedientTipusId + ", " +
				"filtre=" + filtre + ")");
						
		
		PaginaDto<ReassignacioDto> pagina = paginacioHelper.toPaginaDto(
				reassignacioRepository.findByFiltrePaginat(
						expedientTipusId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				ReassignacioDto.class);		
		return pagina;		
	}
	
	/***********************************************/
	/*******************ESTATS**********************/
	/***********************************************/
	
	
	@Override
	@Transactional(readOnly = true)
	public List<EstatDto> estatFindAll(Long expedientTipusId,
			boolean ambHerencia ) throws NoTrobatException, PermisDenegatException {
		// Recupera el tipus d'expedient
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(
				expedientTipusId);
		if (expedientTipus == null)
			throw new NoTrobatException(
					ExpedientTipus.class, 
					expedientTipusId);
		
		// Determina si hi ha herència 
		ambHerencia = ambHerencia && HerenciaHelper.ambHerencia(expedientTipus);
		
		// Consulta els estats
		List<Estat> estats;
		if (ambHerencia)
			estats = estatRepository.findAllAmbHerencia(expedientTipusId);
		else
			estats = estatRepository.findAll(expedientTipusId);				
		
		List<EstatDto> dtos = conversioTipusHelper.convertirList(
				estats,
				EstatDto.class);		

		if (ambHerencia) {
			// Llista d'heretats
			Set<Long> heretatsIds = new HashSet<Long>();
			for (Estat e : estats)
				if ( !expedientTipusId.equals(e.getExpedientTipus().getId()))
					heretatsIds.add(e.getId());
			// Completa l'informació del dto
			for (EstatDto dto : dtos) {
				// Heretat
				if (heretatsIds.contains(dto.getId()) && ! dto.isSobreescriu())
					dto.setHeretat(true);								
			}			
		}
		return dtos;		
	}

	@Override
	@Transactional(readOnly = true)
	public EstatDto estatFindAmbId(
			Long expedientTipusId,
			Long estatId) {
		logger.debug(
				"Consultant el l'estat amb id (" +
				"expedientTipusId=" + expedientTipusId + "," +		
				"estatId=" + estatId +  ")");
		ExpedientTipus tipus = expedientTipusRepository.findById(expedientTipusId);
		Estat estat = estatRepository.findOne(estatId);
		if (estat == null) {
			throw new NoTrobatException(Estat.class, estatId);
		}
		EstatDto dto = conversioTipusHelper.convertir(
				estat, 
				EstatDto.class);
		// Herencia
		if (tipus.getExpedientTipusPare() != null) {
			if (tipus.getExpedientTipusPare().getId().equals(estat.getExpedientTipus().getId()))
				dto.setHeretat(true);
			else
				dto.setSobreescriu(estatRepository.findByExpedientTipusIdAndCodi(tipus.getExpedientTipusPare().getId(), estat.getCodi()) != null);					
		}
		return dto;
	}
	
	@Override
	@Transactional(readOnly = true)
	public EstatDto estatFindAmbCodi(
			Long expedientTipusId, 
			String codi) {
		logger.debug(
				"Consultant l'estat amb codi(" +
				"expedientTipusId=" + expedientTipusId +  
				",codi=" + codi + ")");
		Estat estat = estatRepository.findByExpedientTipusIdAndCodi(
				expedientTipusId, 
				codi);
		EstatDto dto = null;
		if (estat != null) {
			dto = conversioTipusHelper.convertir(
					estat, 
					EstatDto.class);
		}
		return dto;
	}

	@Override
	@Transactional
	public EstatDto estatCreate(Long expedientTipusId, EstatDto dto) {
		Estat estat = new Estat();
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
		estat.setExpedientTipus(expedientTipus);
		estat.setCodi(dto.getCodi());
		estat.setNom(dto.getNom());
		if (ExpedientTipusTipusEnumDto.ESTAT.equals(expedientTipus.getTipus())) {
			estat.setOrdre(dto.getOrdre());
		} else {
			Integer seguentOrdre = estatRepository.getSeguentOrdre(expedientTipusId);
			estat.setOrdre(seguentOrdre == null ? 0 : seguentOrdre + 1);
		}
		return conversioTipusHelper.convertir(
				estatRepository.save(estat),
				EstatDto.class);
	}

	@Override
	@Transactional
	public EstatDto estatUpdate(EstatDto dto) {
		Estat estat = estatRepository.findOne(dto.getId());
		if (estat == null) {
			throw new NoTrobatException(Estat.class, dto.getId());
		}
		
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		
		estat.setCodi(dto.getCodi());
		estat.setNom(dto.getNom());

		if (ExpedientTipusTipusEnumDto.ESTAT.equals(estat.getExpedientTipus().getTipus())) {
			estat.setOrdre(dto.getOrdre());
		}

		return conversioTipusHelper.convertir(
				estatRepository.save(estat),
				EstatDto.class);
	}

	@Override
	@Transactional
	public void estatDelete(Long estatId) throws NoTrobatException, PermisDenegatException {
		Estat estat = estatRepository.findOne(estatId);
		if (estat == null) {
			throw new NoTrobatException(Estat.class, estatId);
		}
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(estat.getExpedientTipus().getId());

		int ordre = estat.getOrdre();
		expedientTipus.getEstats().remove(estat);
		if (ExpedientTipusTipusEnumDto.FLOW.equals(expedientTipus.getTipus())) {
			// Esborra
			estatRepository.delete(estat);
			estatRepository.flush();
			// Reordena els estats
			List<Estat> estats = estatRepository.findByExpedientTipusOrderByOrdreAsc(expedientTipus);
			int i = 0;
			for (Estat e : estats)
				e.setOrdre(i++);
		} else {
			// Esborra
			// Permisos
			List<PermisDto> permisos = permisosHelper.findPermisos(
					estatId,
					Estat.class);
			for(PermisDto permis: permisos)
				permisosHelper.deletePermis(estatId, Estat.class, permis.getId());
			// Regles
			estatReglaRepository.delete(estatReglaRepository.findByEstat(estat));
			// Estat
			estatRepository.delete(estat);
			estatRepository.flush();
			// Reordena els estats
			Long estatAmbMateixOrdre = estatRepository.countByExpedientTipusAndOrdre(expedientTipus, ordre);
			if (estatAmbMateixOrdre.intValue() == 0) {
				estatRepository.decreseEstatsWithBiggerOrder(expedientTipus, ordre);
			}
		}
	}

		/**
         * {@inheritDoc}
         */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<EstatDto> estatFindPerDatatable(
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els estats per al tipus d'expedient per datatable (" +
				"entornId=" + expedientTipusId + ", " +
				"filtre=" + filtre + ")");

		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(expedientTipusId);

		// Determina si hi ha herència 
		boolean ambHerencia = HerenciaHelper.ambHerencia(expedientTipus);
		
		Page<Estat> page = estatRepository.findByFiltrePaginat(
				expedientTipusId,
				filtre == null || "".equals(filtre), 
				filtre, 
				ambHerencia,
				paginacioHelper.toSpringDataPageable(
						paginacioParams)); 

		PaginaDto<EstatDto> pagina = paginacioHelper.toPaginaDto(
				page,
				EstatDto.class);

		List<Long> ids = new ArrayList<Long>();
		for (EstatDto dto: pagina.getContingut()) {
			ids.add(dto.getId());
		}
		Map<Long, List<PermisDto>> permisos = permisosHelper.findPermisos(
				ids,
				Estat.class);

		for (EstatDto dto: pagina.getContingut()) {
			List<PermisDto> permisosEstat = permisos.get(dto.getId());
			dto.setPermisCount(permisosEstat != null ? permisosEstat.size() : 0);
			dto.setReglesCount(estatReglaRepository.countByEstatId(dto.getId()).intValue());
			dto.setAccionsEntradaCount(estatAccioEntradaRepository.countByEstatId(dto.getId()).intValue());
			dto.setAccionsSortidaCount(estatAccioSortidaRepository.countByEstatId(dto.getId()).intValue());
		}

		if (ambHerencia) {
			// Llista d'heretats
			Set<Long> heretatsIds = new HashSet<Long>();
			for (Estat e : page.getContent())
				if ( !expedientTipusId.equals(e.getExpedientTipus().getId()))
					heretatsIds.add(e.getId());
			// Llistat d'elements sobreescrits
			Set<String> sobreescritsCodis = new HashSet<String>();
			for (Estat e : estatRepository.findSobreescrits(
					expedientTipus.getId()
				)) {
				sobreescritsCodis.add(e.getCodi());
			}
			// Completa l'informació del dto
			for (EstatDto dto: pagina.getContingut()) {
				// Sobreescriu
				if (sobreescritsCodis.contains(dto.getCodi()))
					dto.setSobreescriu(true);
				// Heretat
				if (heretatsIds.contains(dto.getId()) && ! dto.isSobreescriu())
					dto.setHeretat(true);			
			}				
		}
		return pagina;		
	}
	
	@Override
	@Transactional
	public boolean estatMoure(Long estatId, int posicio) throws NoTrobatException {
		logger.debug(
				"Moguent l'estat (" +
				"estatId=" + estatId + ", " +
				"posicio=" + posicio + ")");
		boolean ret = false;
		Estat estat = estatRepository.findOne(estatId);
		if (estat == null) {
			throw new NoTrobatException(Estat.class, estatId);
		}
		
		findAmbIdPermisDissenyar(
				estat.getExpedientTipus().getEntorn().getId(), 
				estat.getExpedientTipus().getId());
		
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		
		List<Estat> estats = estatRepository.findByExpedientTipusOrderByOrdreAsc(
				expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
						estat.getExpedientTipus().getId()));
		if(posicio != estats.indexOf(estat)) {
			estats.remove(estat);
			estats.add(posicio, estat);
			int i = 0;
			for (Estat e : estats) {
				e.setOrdre(i++);
				estatRepository.save(e);
			}
		}
		return ret;
	}

	@Override
	@Transactional
	public boolean estatMoureOrdre(Long estatId, int posicio, String tipusOrdre) throws NoTrobatException {
		// Possibles ordres:
		//  - auto  --> No hi ha ambiguitat, i per tant es calcularà de forma automàtica. (Es mou entre dos estats que tenen el mateix ordre)
		//  - 1-1   --> Es mou a l'inici, i agafa el mateix valor del que hi havia abans al inici
		//  - 1-2   --> Es mou a l'inici, agafa el valor 1 i es desplaça la resta d'estats
		//  - 8-9   --> Es mou al final, i agafa el valor del que estava abans al final + 1
		//  - 9-9   --> Es mou al final, i agafa el mateix valor del que hi havia abans al final
		//  - 1-1-2 --> Es moun entre dos estats que tenen ordres consecutius, i agafa el mateix valor que el primer d'ells
		//  - 1-2-2 --> Es moun entre dos estats que tenen ordres consecutius, i agafa el mateix valor que el segon d'ells
		//  - 1-2-3 --> Es moun entre dos estats que tenen ordres consecutius, agafa el mateix valor que el segon d'ells, i desplaça la resta d'estats

		logger.debug(
				"Moguent l'estat (" +
						"estatId=" + estatId + ", " +
						"posicio=" + posicio + ")");
		Estat estat = estatRepository.findOne(estatId);
		if (estat == null) {
			throw new NoTrobatException(Estat.class, estatId);
		}

		findAmbIdPermisDissenyar(
				estat.getExpedientTipus().getEntorn().getId(),
				estat.getExpedientTipus().getId());

		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		List<Estat> estats = estatRepository.findByExpedientTipusOrderByOrdreAsc(expedientTipus);

		int posicioActual = estats.indexOf(estat);
		int ordreActual = estat.getOrdre();
		Long estatAmbMateixOrdre = estatRepository.countByExpedientTipusAndOrdre(expedientTipus, estat.getOrdre());

		if ("auto".equals(tipusOrdre)) { 			// Agafa el mateix valor que l'estat que hi havia on es mou
			estat.setOrdre(estats.get(posicio).getOrdre());
		} else if ("1-1".equals(tipusOrdre)) {		// Posicio == 0. Ordre = 1
			estat.setOrdre(1);
		} else if ("1-2".equals(tipusOrdre)) {		// Posicio == 0. Ordre = 1. Tots els estats augmenten en 1
			estatRepository.increseEstatsWithBiggerOrder(expedientTipus, 0);
			estat.setOrdre(1);
		} else if ("8-9".equals(tipusOrdre)) {		// Posicio == final. Ordre = mateix que l'estat final + 1
			estat.setOrdre(estats.get(posicio).getOrdre() + 1);
		} else if ("9-9".equals(tipusOrdre)) {		// Posicio == final. Ordre = mateix que l'estat final
			estat.setOrdre(estats.get(posicio).getOrdre());
		} else if ("1-1-2".equals(tipusOrdre)) { 	// Ordre = mateix que l'estat anterior
			int ordre = posicioActual < posicio ? estats.get(posicio).getOrdre() : estats.get(posicio - 1).getOrdre();
			estat.setOrdre(ordre);
		} else if ("1-2-2".equals(tipusOrdre)) {	// Ordre = mateix que l'estat posterior
			int ordre = posicioActual < posicio ? estats.get(posicio + 1).getOrdre() : estats.get(posicio).getOrdre();
			estat.setOrdre(ordre);
		} else if ("1-2-3".equals(tipusOrdre)) {	// Ordre = mateix que l'estat posterior. Tots els estats posteriors augmenten en 1
			int ordre = posicioActual < posicio ? estats.get(posicio + 1).getOrdre() : estats.get(posicio).getOrdre();
			estatRepository.increseEstatsWithBiggerOrder(expedientTipus, ordre - 1);
			estat.setOrdre(ordre);
		} else {
			throw new RuntimeException("Tipus d'ordre invàlid");
		}

		// Reordenam els estats que hi havia darrera el que movem
		if (estatAmbMateixOrdre.intValue() == 1)
			estatRepository.decreseEstatsWithBiggerOrder(expedientTipus, ordreActual);
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public int getEstatSeguentOrdre(Long expedientTipusId) throws NoTrobatException {
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
		Integer seguentOrdre = estatRepository.getSeguentOrdre(expedientTipusId);
		return seguentOrdre == null ? 1 : seguentOrdre + 1;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EstatExportacio> estatExportacio(Long expedientTipusId, boolean ambPermisos) throws NoTrobatException {
		List<EstatExportacio> estatsExportacio = new ArrayList<EstatExportacio>();

		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
		HerenciaHelper.ambHerencia(expedientTipus);

		// Consulta els estats
		List<Estat> estats;
		if (HerenciaHelper.ambHerencia(expedientTipus))
			estats = estatRepository.findAllAmbHerencia(expedientTipusId);
		else
			estats = estatRepository.findAll(expedientTipusId);

		for (Estat estat: estats) {
			estatsExportacio.add(getEstatExportacio(estat, ambPermisos));
		}

		return estatsExportacio;
	}

	
	@Override
	@Transactional(readOnly = true)
	public List<EstatDto> estatGetAvancar(long expedientId) {
		logger.debug(
				"Consultant els possibles estats per avançar l'expedient (" +
				"expedientId=" + expedientId+ ")");
		Expedient expedient = expedientRepository.findOne(expedientId);		
		int estatOrdre= 0;
		if (expedient.getEstat() != null ) {
			estatOrdre =  expedient.getEstat().getOrdre();
		} else if (expedient.getDataFi() == null) {
			estatOrdre = 0;
		} else {
			estatOrdre = estatRepository.getSeguentOrdre(expedient.getTipus().getId());
		}
		List<Estat> estats = estatRepository.findByExpedientTipusAndOrdre(expedient.getTipus(), estatOrdre + 1);
		return conversioTipusHelper.convertirList(estats, EstatDto.class);

	}

	@Override
	@Transactional(readOnly = true)
	public List<EstatDto> estatGetRetrocedir(long expedientId) {
		List<EstatDto> estatsRetrocedir = new ArrayList<EstatDto>();
		logger.debug(
				"Consultant els possibles estats per retrocedir l'expedient (" +
				"expedientId=" + expedientId+ ")");
		logger.debug(
				"Consultant els possibles estats per avançar l'expedient (" +
				"expedientId=" + expedientId+ ")");
		Expedient expedient = expedientRepository.findOne(expedientId);		
		int estatOrdre= 0;
		if (expedient.getEstat() != null ) {
			estatOrdre =  expedient.getEstat().getOrdre();
		} else if (expedient.getDataFi() == null) {
			estatOrdre = 0;
		} else {
			estatOrdre = estatRepository.getSeguentOrdre(expedient.getTipus().getId());
		}
		List<Estat> estats = estatRepository.findByExpedientTipusAndOrdre(expedient.getTipus(), estatOrdre - 1);
		return conversioTipusHelper.convertirList(estats, EstatDto.class);
	}

	private EstatExportacio getEstatExportacio(Estat estat, boolean ambPermisos) {

		// Regles
		List<EstatReglaDto> reglesDto = null;
		List<EstatRegla> regles = estatReglaRepository.findByEstatOrderByOrdreAsc(estat);
		if (regles != null && !regles.isEmpty()) {
			reglesDto = conversioTipusHelper.convertirList(regles, EstatReglaDto.class);
		}
		// Permisos
		List<PermisEstatDto> permisosDto = null;
		if (ambPermisos) {
			List<PermisDto> permisos = permisosHelper.findPermisos(estat.getId(), Estat.class);
			if (permisos != null && !permisos.isEmpty()) {
				permisosDto = conversioTipusHelper.convertirList(permisos, PermisEstatDto.class);
			}
		}
		
		// Accions entrada
		List<EstatAccioDto> estatAccionsEntradaDto = new ArrayList<EstatAccioDto>();
		List<EstatAccioEntrada> estatAccioEntrada = estatAccioEntradaRepository.findByEstatOrderByOrdreAsc(estat);
		if (estatAccioEntrada != null && !estatAccioEntrada.isEmpty()) {
			estatAccionsEntradaDto = conversioTipusHelper.convertirList(estatAccioEntrada, EstatAccioDto.class);
		}

		// Accions sortida
		List<EstatAccioDto> estatAccionsSortidaDto = new ArrayList<EstatAccioDto>();
		List<EstatAccioSortida> estatAccioSortida = estatAccioSortidaRepository.findByEstatOrderByOrdreAsc(estat);
		if (estatAccioSortida != null && !estatAccioSortida.isEmpty()) {
			estatAccionsSortidaDto = conversioTipusHelper.convertirList(estatAccioSortida, EstatAccioDto.class);
		}

		return EstatExportacio.builder()
				.codi(estat.getCodi())
				.nom(estat.getNom())
				.ordre(estat.getOrdre())
				.regles(reglesDto)
				.permisos(permisosDto)
				.accionsEntrada(estatAccionsEntradaDto)
				.accionsSortida(estatAccionsSortidaDto)
				.build();
	}

	@Override
	@Transactional(readOnly = true)
	public List<PermisDto> estatPermisFindAll(Long estatId) {
		logger.debug("Consultant permisos de l'estat (estat=" + estatId + ")");

		Estat estat = estatRepository.findOne(estatId);
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());

		return permisosHelper.findPermisos(
				estatId,
				Estat.class);
	}

	@Override
	@Transactional(readOnly = true)
	public PermisDto estatPermisFindById(Long estatId, Long permisId) {
		logger.debug(
				"Consultant un permis donat el seu id (" +
						"estatId=" + estatId + ", " +
						"permisId=" + permisId + ")");

		Estat estat = estatRepository.findOne(estatId);
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());

		List<PermisDto> permisos = permisosHelper.findPermisos(
				estatId,
				Estat.class);
		for (PermisDto permis: permisos) {
			if (permis.getId().equals(permisId)) {
				return permis;
			}
		}
		throw new NoTrobatException(PermisDto.class, permisId);
	}

	@Override
	@Transactional
	public void estatPermisUpdate(Long estatId, PermisDto permis) throws NoTrobatException, PermisDenegatException {
		logger.debug("Actualitzant permis per a l'estat (" +
						"estatId=" + estatId + ", " +
						"permis=" + permis + ")");

		Estat estat = estatRepository.findOne(estatId);
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		permisosHelper.updatePermis(
				estatId,
				Estat.class,
				permis);
	}

	@Override
	@Transactional
	public void estatPermisDelete(Long estatId, Long permisId) throws NoTrobatException, PermisDenegatException {
		logger.debug("Esborrant permis per a l'estat (" +
				"estatId=" + estatId + ", " +
				"permisId=" + permisId + ")");

		Estat estat = estatRepository.findOne(estatId);
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		permisosHelper.deletePermis(
				estatId,
				Estat.class,
				permisId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<EstatReglaDto> estatReglaFindAll(Long estatId) {
		Estat estat = estatRepository.findOne(estatId);
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		List<EstatRegla> regles = estatReglaRepository.findByEstatOrderByOrdreAsc(estat);
		return conversioTipusHelper.convertirList(regles, EstatReglaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public EstatReglaDto estatReglaFindById(Long estatId, Long reglaId) {
		Estat estat = estatRepository.findOne(estatId);
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		EstatRegla regla = estatReglaRepository.findOne(reglaId);
		if (regla == null)
			throw new NoTrobatException(EstatRegla.class, reglaId);
		return conversioTipusHelper.convertir(regla, EstatReglaDto.class);
	}

	@Override
	public EstatReglaDto estatReglaFindByNom(Long estatId, String nom) {
		Estat estat = estatRepository.findOne(estatId);
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		EstatRegla regla = estatReglaRepository.findByNom(estatId, nom);
		return conversioTipusHelper.convertir(regla, EstatReglaDto.class);
	}

	@Override
	@Transactional
	public EstatReglaDto estatReglaCreate(Long estatId, EstatReglaDto reglaDto) throws NoTrobatException, PermisDenegatException {
		Estat estat = estatRepository.findOne(estatId);
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		Integer seguentOrdre = estatReglaRepository.getSeguentOrdre(estatId);
		seguentOrdre = seguentOrdre == null ? 0 : seguentOrdre + 1;
		EstatRegla regla = EstatRegla.builder()
				.nom(reglaDto.getNom())
				.ordre(seguentOrdre)
				.qui(reglaDto.getQui())
				.quiValor(reglaDto.getQuiValor())
				.que(reglaDto.getQue())
				.queValor(reglaDto.getQueValor())
				.accio(reglaDto.getAccio())
				.estat(estat)
				.expedientTipus(expedientTipus)
				.entorn(expedientTipus.getEntorn())
				.build();
		return conversioTipusHelper.convertir(estatReglaRepository.save(regla), EstatReglaDto.class);
	}

	@Override
	@Transactional
	public EstatReglaDto estatReglaUpdate(Long estatId, EstatReglaDto reglaDto) throws NoTrobatException, PermisDenegatException {
		Estat estat = estatRepository.findOne(estatId);
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		EstatRegla regla = estatReglaRepository.findOne(reglaDto.getId());
		if (regla == null)
			throw new NoTrobatException(EstatRegla.class, reglaDto.getId());

		regla.setNom(reglaDto.getNom());
		regla.setQui(reglaDto.getQui());
		regla.setQuiValor(reglaDto.getQuiValor());
		regla.setQue(reglaDto.getQue());
		regla.setQueValor(reglaDto.getQueValor());
		regla.setAccio(reglaDto.getAccio());
		return conversioTipusHelper.convertir(estatReglaRepository.save(regla), EstatReglaDto.class);
	}

	@Override
	@Transactional
	public void estatReglaDelete(Long estatId, Long reglaId) throws NoTrobatException, PermisDenegatException {
		Estat estat = estatRepository.findOne(estatId);
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		EstatRegla regla = estatReglaRepository.findOne(reglaId);
		if (regla == null)
			throw new NoTrobatException(EstatRegla.class, reglaId);

		estatReglaRepository.delete(regla);
	}

	@Override
	@Transactional
	public boolean estatReglaMoure(Long reglaId, int posicio) {
		logger.debug(
				"Moguent la regla (" +
						"reglaId=" + reglaId + ", " +
						"posicio=" + posicio + ")");
		boolean ret = false;
		EstatRegla regla = estatReglaRepository.findOne(reglaId);
		if (regla == null) {
			throw new NoTrobatException(EstatRegla.class, reglaId);
		}
		findAmbIdPermisDissenyar(
				regla.getExpedientTipus().getEntorn().getId(),
				regla.getExpedientTipus().getId());
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(regla.getExpedientTipus().getId());
		Estat estat = estatRepository.findOne(regla.getEstat().getId());
		if (estat == null) {
			throw new NoTrobatException(Estat.class, regla.getEstat().getId());
		}

		List<EstatRegla> regles = estatReglaRepository.findByEstatOrderByOrdreAsc(estat);
		if(posicio != regles.indexOf(regla)) {
			regles.remove(regla);
			regles.add(posicio, regla);
			int i = 0;
			for (EstatRegla r : regles) {
				r.setOrdre(i++);
				estatReglaRepository.save(r);
			}
		}
		return ret;
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<EstatAccioDto> estatAccioEntradaFindPerDatatable(Long estatId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		logger.debug(
				"Consultant les accions per l'estat per datatable (" +
				"estatId=" + estatId + ", " +
				"filtre=" + filtre + ")");
						
		
		return paginacioHelper.toPaginaDto(
				estatAccioEntradaRepository.findByFiltrePaginat(
						estatId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				EstatAccioDto.class);	
	}
	
	@Override
	@Transactional
	public void estatAccionsDeleteAll(Long estatId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant totes les accions d'entrada i sortida de l'estat  (" +
				"estatId=" + estatId + ")");
		Estat estat = estatRepository.findOne(estatId);
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		// Accions d'entrada
		for (EstatAccioEntrada estatAccio : estatAccioEntradaRepository.findByEstatOrderByOrdreAsc(estat)) {
			estatAccioEntradaRepository.delete(estatAccio);
		}
		// Accions de sortida
		for (EstatAccioSortida estatAccio : estatAccioSortidaRepository.findByEstatOrderByOrdreAsc(estat)) {
			estatAccioSortidaRepository.delete(estatAccio);
		}
	}

	@Override
	@Transactional
	public EstatAccioDto estatAccioEntradaAfegir(Long estatId, Long accioId) throws NoTrobatException, PermisDenegatException {
		Estat estat = estatRepository.findOne(estatId);
		Accio accio = accioRepository.findOne(accioId);
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		Integer seguentOrdre = estatAccioEntradaRepository.getSeguentOrdre(estatId);
		seguentOrdre = seguentOrdre == null ? 0 : seguentOrdre + 1;
		EstatAccioEntrada estatAccioEntrada = EstatAccioEntrada.builder()
				.estat(estat)
				.accio(accio)
				.ordre(seguentOrdre)
				.build();
		return conversioTipusHelper.convertir(
				estatAccioEntradaRepository.save(estatAccioEntrada), 
				EstatAccioDto.class);
	}
	
	@Override
	@Transactional
	public void estatAccioEntradaDelete(Long estatId, Long estatAccioId) throws NoTrobatException, PermisDenegatException {
		Estat estat = estatRepository.findOne(estatId);
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		EstatAccioEntrada estatAccioEntrada = estatAccioEntradaRepository.findOne(estatAccioId);
		if (estatAccioEntrada == null)
			throw new NoTrobatException(EstatAccioEntrada.class, estatAccioId);

		estatAccioEntradaRepository.delete(estatAccioEntrada);
		estatAccioEntradaRepository.flush();
		// Reordena els estats
		List<EstatAccioEntrada> estatAccions = estatAccioEntradaRepository.findByEstatOrderByOrdreAsc(estat);
		int i = 0;
		for (EstatAccioEntrada ea : estatAccions)
			ea.setOrdre(i++);
	}

	@Override
	@Transactional
	public boolean estatAccioEntradaMoure(
			Long estatAccioId, 
			int posicio) {
		boolean ret = false;
		EstatAccioEntrada estatAccio = estatAccioEntradaRepository.findOne(estatAccioId);
		if (estatAccio != null) {
			List<EstatAccioEntrada> estatAccions = estatAccioEntradaRepository.findByEstatOrderByOrdreAsc(estatAccio.getEstat());
			if(posicio != estatAccions.indexOf(estatAccio)) {
				estatAccions.remove(estatAccio);
				estatAccions.add(posicio, estatAccio);
				int i = 0;
				for (EstatAccioEntrada c : estatAccions) {
					c.setOrdre(i++);
				}
			}
		}
		return ret;
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<EstatAccioDto> estatAccioSortidaFindPerDatatable(Long estatId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		logger.debug(
				"Consultant les accions per l'estat per datatable (" +
				"estatId=" + estatId + ", " +
				"filtre=" + filtre + ")");
						
		
		return paginacioHelper.toPaginaDto(
				estatAccioSortidaRepository.findByFiltrePaginat(
						estatId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				EstatAccioDto.class);	
	}
	
	@Override
	@Transactional
	public EstatAccioDto estatAccioSortidaAfegir(Long estatId, Long accioId) throws NoTrobatException, PermisDenegatException {
		Estat estat = estatRepository.findOne(estatId);
		Accio accio = accioRepository.findOne(accioId);
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		Integer seguentOrdre = estatAccioSortidaRepository.getSeguentOrdre(estatId);
		seguentOrdre = seguentOrdre == null ? 0 : seguentOrdre + 1;
		EstatAccioSortida estatAccioSortida = EstatAccioSortida.builder()
				.estat(estat)
				.accio(accio)
				.ordre(seguentOrdre)
				.build();
		return conversioTipusHelper.convertir(
				estatAccioSortidaRepository.save(estatAccioSortida), 
				EstatAccioDto.class);
	}
	
	@Override
	@Transactional
	public void estatAccioSortidaDelete(Long estatId, Long estatAccioId) throws NoTrobatException, PermisDenegatException {
		Estat estat = estatRepository.findOne(estatId);
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(estat.getExpedientTipus().getId());
		EstatAccioSortida estatAccioSortida = estatAccioSortidaRepository.findOne(estatAccioId);
		if (estatAccioSortida == null)
			throw new NoTrobatException(EstatAccioSortida.class, estatAccioId);

		estatAccioSortidaRepository.delete(estatAccioSortida);
		estatAccioSortidaRepository.flush();
		// Reordena els estats
		List<EstatAccioSortida> estatAccions = estatAccioSortidaRepository.findByEstatOrderByOrdreAsc(estat);
		int i = 0;
		for (EstatAccioSortida ea : estatAccions)
			ea.setOrdre(i++);
	}

	@Override
	@Transactional
	public boolean estatAccioSortidaMoure(
			Long estatAccioId, 
			int posicio) {
		boolean ret = false;
		EstatAccioSortida estatAccio = estatAccioSortidaRepository.findOne(estatAccioId);
		if (estatAccio != null) {
			List<EstatAccioSortida> estatAccions = estatAccioSortidaRepository.findByEstatOrderByOrdreAsc(estatAccio.getEstat());
			if(posicio != estatAccions.indexOf(estatAccio)) {
				estatAccions.remove(estatAccio);
				estatAccions.add(posicio, estatAccio);
				int i = 0;
				for (EstatAccioSortida c : estatAccions) {
					c.setOrdre(i++);
				}
			}
		}
		return ret;
	}

	@Override
	@Transactional
	public void definicioProcesIncorporar(
			Long expedientTipusId, 
			Long definicioProcesId,
			boolean sobreescriure,
			boolean tasques) throws ExportException {
		logger.debug(
				"Importació la informació de la definicio de proces al tipus d'expedient(" +
				"expedientTipusId=" + expedientTipusId +  ", " + 
				"definicioProcesId=" + definicioProcesId +  ", " + 
				"sobreescriure=" + sobreescriure +  ", " + 
				"tasques=" + tasques + ")");
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
		Entorn entorn = expedientTipus.getEntorn();
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		if (expedientTipus != null 
				&& definicioProces != null) {
			// Propaga les agrupacions
			Map<String, CampAgrupacio> agrupacions = new HashMap<String, CampAgrupacio>();
			for (CampAgrupacio agrupacio : definicioProces.getAgrupacions()) {
				CampAgrupacio nova = campAgrupacioRepository.findByExpedientTipusIdAndCodi(
						expedientTipus.getId(),
						agrupacio.getCodi());
				if (nova == null || sobreescriure) {
					if (nova == null) {
						nova = new CampAgrupacio(
								expedientTipus,
								agrupacio.getCodi(),
								agrupacio.getNom(),
								agrupacio.getOrdre());
						expedientTipus.getAgrupacions().add(nova);
					} else if (sobreescriure) {
						nova.setNom(agrupacio.getNom());
						nova.setOrdre(agrupacio.getOrdre());
					}
					nova.setDescripcio(agrupacio.getDescripcio());
				}
				agrupacions.put(agrupacio.getCodi(), nova);
			}
			// Propaga les accions
			for (Accio accio: definicioProces.getAccions()) {
				Accio nova = accioRepository.findByExpedientTipusIdAndCodi(
						expedientTipus.getId(),
						accio.getCodi());
				if (nova == null || sobreescriure) {
					if (nova == null) {
						nova = new Accio(
								expedientTipus,
								accio.getCodi(),
								accio.getNom(),
								accio.getTipus(),
								definicioProces.getJbpmKey(),
								accio.getJbpmAction(),
								accio.getPredefinitClasse(),
								accio.getPredefinitDades());
						expedientTipus.getAccions().add(nova);
					} else if (sobreescriure) {
						nova.setNom(accio.getNom());
						nova.setJbpmAction(accio.getJbpmAction());
					}
					nova.setDescripcio(accio.getDescripcio());
					nova.setPublica(accio.isPublica());
					nova.setOculta(accio.isOculta());
					nova.setRols(accio.getRols());
				}
			}
			// Propaga els camps
			Map<String, Camp> camps = new HashMap<String, Camp>();
			Map<String, Camp> registres = new HashMap<String, Camp>();
			for (Camp camp: definicioProces.getCamps()) {
				Camp nou = campRepository.findByExpedientTipusAndCodi(
						expedientTipus.getId(),
						camp.getCodi(),
						false);
				if (nou == null || sobreescriure) {
					if (nou == null) {
						nou = new Camp(
								expedientTipus,
								camp.getCodi(),
								camp.getTipus(),
								camp.getEtiqueta());
						expedientTipus.getCamps().add(nou);
					} else {
						nou.setTipus(camp.getTipus());
						nou.setEtiqueta(camp.getEtiqueta());
					}
					nou.setIgnored(camp.isIgnored());
					nou.setObservacions(camp.getObservacions());
					nou.setDominiId(camp.getDominiId());
					nou.setDominiParams(camp.getDominiParams());
					nou.setDominiCampText(camp.getDominiCampText());
					nou.setDominiCampValor(camp.getDominiCampValor());
					nou.setDominiCacheText(camp.isDominiCacheText());
					nou.setMultiple(camp.isMultiple());
					nou.setOcult(camp.isOcult());
					nou.setDominiIntern(camp.getDominiIntern());
					nou.setJbpmAction(camp.getJbpmAction());
					if (camp.getTipus() == TipusCamp.ACCIO && camp.getDefinicioProces() != null)
						nou.setDefprocJbpmKey(camp.getDefinicioProces().getJbpmKey());
					nou.setOrdre(camp.getOrdre());
					
					if (camp.getEnumeracio() != null && camp.getEnumeracio().getCodi() != null) {
						// Propaga la enumeració referenciada pel camp
						Enumeracio enumeracioEntorn = enumeracioRepository.findByEntornAndCodi(
								entorn, 
								camp.getEnumeracio().getCodi());
						if (enumeracioEntorn != null){
							nou.setEnumeracio(enumeracioEntorn);
						} else {
							Enumeracio enumeracio = enumeracioRepository.findByEntornAndExpedientTipusAndCodi(
									entorn, 
									expedientTipus, 
									camp.getEnumeracio().getCodi());
							if (enumeracio != null) {
								nou.setEnumeracio(enumeracio);
							} else {
								throw new ExportException(
										messageHelper.getMessage(
												"expedient.tipus.definicioProces.llistat.definicioProces.incorporar.error.enumeracio",
												new Object[] {camp.getEnumeracio().getCodi(), camp.getCodi(), camp.getEtiqueta()}
										));
							}
						}						
					}
					// Propaga les validacions del camp
					for (Validacio validacio: nou.getValidacions())
						campValidacioRepository.delete(validacio);
					nou.getValidacions().clear();
					for (Validacio validacio: camp.getValidacions()) {
						Validacio nova = new Validacio(
								nou,
								validacio.getExpressio(),
								validacio.getMissatge());
						nova.setOrdre(validacio.getOrdre());
						nou.addValidacio(nova);
					}				
					if (nou.getTipus() == TipusCamp.REGISTRE) {
						registres.put(nou.getCodi(), nou);
					}
				}
				camps.put(nou.getCodi(), nou);				
				if (camp.getDomini() != null &&  !camp.getDominiIntern()) {
					// Propaga el domini referenciat pel camp
					Domini dominiEntorn = dominiRepository.findByEntornAndCodi(
							entorn, 
							camp.getDomini().getCodi());
					if (dominiEntorn != null) {
						nou.setDomini(dominiEntorn);
					} else {
						Domini domini = dominiRepository.findByExpedientTipusAndCodi(
								expedientTipus,
								camp.getDomini().getCodi());
						if (domini != null) {
							nou.setDomini(domini);
						} else {
							throw new ExportException(
									messageHelper.getMessage(
											"expedient.tipus.definicioProces.llistat.definicioProces.incorporar.error.domini",
											new Object[] {camp.getDomini().getCodi(), camp.getCodi(), camp.getEtiqueta()}
									));
						}
					}						
				}
				if (camp.getAgrupacio() != null)
					nou.setAgrupacio(agrupacions.get(camp.getAgrupacio().getCodi()));
				campRepository.save(nou);
			} // Fi propagació camps
			// Propaga els membres dels camps de tipus registre
			for (Camp camp: registres.values()) {
				for (CampRegistre campRegistre: camp.getRegistreMembres()) {
					campRegistre.getMembre().getRegistrePares().remove(campRegistre);
					campRegistre.setMembre(null);
					campRegistre.setRegistre(null);
					campRegistreRepository.delete(campRegistre);
				}
				camp.getRegistreMembres().clear();
			}
			campRegistreRepository.flush();
			for (Camp camp: definicioProces.getCamps()) {
				if (camp.getTipus() == TipusCamp.REGISTRE && registres.containsKey(camp.getCodi())) {
					for (CampRegistre membre: camp.getRegistreMembres()) {
						CampRegistre campRegistre = new CampRegistre(
								camps.get(camp.getCodi()),
								camps.get(membre.getMembre().getCodi()),
								membre.getOrdre());
						campRegistre.setLlistar(membre.isLlistar());
						campRegistre.setObligatori(membre.isObligatori());
						registres.get(camp.getCodi()).getRegistreMembres().add(campRegistre);
					}
				}
			}
			// Propaga els documents
			Map<String, Document> documents = new HashMap<String, Document>();
			for (Document document: definicioProces.getDocuments()) {
				Document nou = documentRepository.findByExpedientTipusAndCodi(
						expedientTipus.getId(),
						document.getCodi(),
						false);
				if (nou == null || sobreescriure) {
					if (nou == null) {
						nou = new Document(
								expedientTipus,
								document.getCodi(),
								document.getNom());
						expedientTipus.getDocuments().add(nou);
					} else if (sobreescriure) {
						nou.setNom(document.getNom());
					}
					nou.setDescripcio(document.getDescripcio());
					nou.setArxiuNom(document.getArxiuNom());
					nou.setArxiuContingut(document.getArxiuContingut());
					nou.setPlantilla(document.isPlantilla());
					nou.setCustodiaCodi(document.getCustodiaCodi());
					nou.setContentType(document.getContentType());
					nou.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
					nou.setAdjuntarAuto(document.isAdjuntarAuto());
					if (document.getCampData() != null && document.getCampData().getCodi() != null)
						nou.setCampData(camps.get(document.getCampData().getCodi()));
					nou.setConvertirExtensio(document.getConvertirExtensio());
					nou.setExtensionsPermeses(document.getExtensionsPermeses());
					nou.setIgnored(document.isIgnored());
					documents.put(nou.getCodi(), nou);
				}				
			}
			// Propaga els terminis
			for (Termini termini: definicioProces.getTerminis()) {
				Termini nou = terminiRepository.findByExpedientTipusAndCodi(
						expedientTipus,
						termini.getCodi());
				if (nou == null || sobreescriure) {
					if (nou == null) {
						nou = new Termini(
								expedientTipus,
								termini.getCodi(),
								termini.getNom(),
								termini.getAnys(),
								termini.getMesos(),
								termini.getDies(),
								termini.isLaborable());
						expedientTipus.getTerminis().add(nou);
					} else if (sobreescriure) {
						nou.setNom(termini.getNom());
						nou.setAnys(termini.getAnys());
						nou.setMesos(termini.getMesos());
						nou.setDies(termini.getDies());
						nou.setLaborable(termini.isLaborable());
					}
					nou.setDescripcio(termini.getDescripcio());
					nou.setDiesPrevisAvis(termini.getDiesPrevisAvis());
					nou.setAlertaPrevia(termini.isAlertaPrevia());
					nou.setAlertaFinal(termini.isAlertaFinal());
					nou.setAlertaCompletat(termini.isAlertaCompletat());
					nou.setManual(termini.isManual());
				}	
			}
			
			if (tasques 
					&& definicioProces.getExpedientTipus() != null 
					&& definicioProces.getExpedientTipus().equals(expedientTipus)) {
				
				// Canvia variables, documents i signatures per a que apuntin a les dades del tipus d'expedient
				
				for (Tasca tasca : definicioProces.getTasques() ) {
					// Relaciona les variables
					for (CampTasca campTasca : tasca.getCamps())  {
						if (campTasca.getCamp().getExpedientTipus() == null) {
							definicioProcesHelper.relacionarCampTasca(
									campTasca, 
									campTasca.getCamp().getCodi(), 
									true, 
									expedientTipus, 
									definicioProces);
						}
					}
					// Relaciona els documents
					for (DocumentTasca documentTasca : tasca.getDocuments())  {
						if (documentTasca.getDocument().getExpedientTipus() == null) {
							definicioProcesHelper.relacionarDocumentTasca(
									documentTasca, 
									documentTasca.getDocument().getCodi(), 
									true, 
									expedientTipus, 
									definicioProces);
						}
					}
					// Relaciona les firmes
					for (FirmaTasca firmaTasca : tasca.getFirmes())  {
						if (firmaTasca.getDocument().getExpedientTipus() == null) {
							definicioProcesHelper.relacionarFirmaTasca(
									firmaTasca, 
									firmaTasca.getDocument().getCodi(), 
									true, 
									expedientTipus, 
									definicioProces);
						}
					}
				}
				
			}
		}
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ConsultaDto consultaCreate(
			Long expedientTipusId, 
			ConsultaDto consulta) throws PermisDenegatException {

		logger.debug(
				"Creant nova consulta per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"consulta=" + consulta + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		
		Consulta entity = new Consulta();

		entity.setCodi(consulta.getCodi());
		entity.setNom(consulta.getNom());
		entity.setDescripcio(consulta.getDescripcio());
		entity.setFormatExport(consulta.getFormatExport());
		entity.setValorsPredefinits(consulta.getValorsPredefinits());
		entity.setExportarActiu(consulta.isExportarActiu());
		entity.setOcultarActiu(consulta.isOcultarActiu());
		entity.setInformeNom(consulta.getInformeNom());
		entity.setInformeContingut(consulta.getInformeContingut());
		entity.setOrdre(consultaRepository.getNextOrdre(expedientTipusId));

		entity.setExpedientTipus(expedientTipus);		
		entity.setEntorn(expedientTipus.getEntorn());		

		return conversioTipusHelper.convertir(
				consultaRepository.save(entity),
				ConsultaDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ConsultaDto consultaUpdate(
			ConsultaDto consulta,
			boolean actualitzarContingut) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant la consulta del tipus d'expedient existent (" +
				"consulta.id=" + consulta.getId() + ", " +
				"consulta =" + consulta + ")");
		Consulta entity = consultaRepository.findOne(consulta.getId());

		entity.setCodi(consulta.getCodi());
		entity.setNom(consulta.getNom());
		entity.setDescripcio(consulta.getDescripcio());
		entity.setFormatExport(consulta.getFormatExport());
		entity.setValorsPredefinits(consulta.getValorsPredefinits());
		entity.setExportarActiu(consulta.isExportarActiu());
		entity.setOcultarActiu(consulta.isOcultarActiu());
		entity.setInformeNom(consulta.getInformeNom());
		if (actualitzarContingut)
			entity.setInformeContingut(consulta.getInformeContingut());
				
		return conversioTipusHelper.convertir(
				consultaRepository.save(entity),
				ConsultaDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void consultaDelete(Long consultaConsultaId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la consulta del tipus d'expedient (" +
				"consultaId=" + consultaConsultaId +  ")");
		Consulta entity = consultaRepository.findOne(consultaConsultaId);
		entity.getExpedientTipus().getConsultes().remove(entity);
		consultaRepository.delete(entity);	
		consultaRepository.flush();
		reordenarConsultes(entity.getExpedientTipus().getId());		
	}

	/** Funció per reasignar el valor d'ordre per a les agrupacions d'un tipus d'expedient */
	private int reordenarConsultes(Long expedientTipusId) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		Set<Consulta> consultes = expedientTipus.getConsultes();
		int i = 0;
		for (Consulta consulta: consultes)
			consulta.setOrdre(i++);
		return consultes.size();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConsultaDto consultaFindAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant la consulta del tipus d'expedient amb id (" +
				"consultaId=" + id +  ")");
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			throw new NoTrobatException(Consulta.class, id);
		}
		return conversioTipusHelper.convertir(
				consulta,
				ConsultaDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConsultaDto consultaFindAmbCodiPerValidarRepeticio(Long expedientTipusId, String codi) throws NoTrobatException {
		logger.debug(
				"Consultant la consulta del tipus d'expedient per codi per validar repetició (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"codi = " + codi + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		return conversioTipusHelper.convertir(
				consultaRepository.findByExpedientTipusAndCodi(expedientTipus, codi),
				ConsultaDto.class);
	}	
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ConsultaDto> consultaFindPerDatatable(
			Long entornId,
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les consultes per al tipus d'expedient per datatable (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"filtre=" + filtre + ")");
								
		PaginaDto<ConsultaDto> pagina = paginacioHelper.toPaginaDto(
				consultaRepository.findByFiltrePaginat(
						entornId,
						expedientTipusId == null,
						expedientTipusId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				ConsultaDto.class);
		// Consulta els valors pels comptadors 
		// List< Object[Long consultaId, TipusConsultaCamp tipus, Long count]>
		List<Long> paginaIds = new ArrayList<Long>();
		for (ConsultaDto c : pagina.getContingut()) {
			paginaIds.add(c.getId());
		}
		if (paginaIds.size() > 0) {
			List<Object[]> countCamps = consultaRepository.countCamps(paginaIds);
			List<Object[]> processats = new ArrayList<Object[]>();	// per esborrar la informació processada i reduir la cerca
			// Omple els comptadors de tipus de camps
			for (ConsultaDto consulta: pagina.getContingut()) {
				for (Object[] countCamp: countCamps) {
					Long campId = (Long) countCamp[0];
					if (campId.equals(consulta.getId())) {
						Long count = (Long)countCamp[2];
						ConsultaCamp.TipusConsultaCamp tipus = (ConsultaCamp.TipusConsultaCamp) countCamp[1]; 
						switch(tipus) {
						case FILTRE:
							consulta.setVarsFiltreCount(count.intValue());
							break;
						case INFORME:
							consulta.setVarsInformeCount(count.intValue());
							break;
						case PARAM:
							consulta.setParametresCount(count.intValue());
							break;
						default:
							break;
						}
						processats.add(countCamp);
					}
				}
				countCamps.removeAll(processats);
				processats.clear();
			}		
		}
		return pagina;		
	}	
	
	@Override
	@Transactional(readOnly = true)
	public List<ConsultaDto> consultaFindRelacionadesAmbDefinicioProces(
			Long entornId,
			Long expedientTipusId, 
			String jbpmKey,
			int versio) {

		return conversioTipusHelper.convertirList(
				consultaRepository.findRelacionadesAmbDefinicioProces(
						entornId,
						expedientTipusId,
						jbpmKey,
						versio),
				ConsultaDto.class);
	}
	
	@Override
	@Transactional
	public boolean consultaMourePosicio(
			Long id, 
			int posicio) {
		boolean ret = false;
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta != null) {
			List<Consulta> consultes = consultaRepository.findConsultesAmbEntornIExpedientTipusOrdenat(
					consulta.getEntorn().getId(),
					consulta.getExpedientTipus().getId());
			if(posicio != consultes.indexOf(consulta)) {
				consultes.remove(consulta);
				consultes.add(posicio, consulta);
				int i = 0;
				for (Consulta c : consultes) {
					c.setOrdre(i++);
				}
			}
		}
		return ret;
	}	
	
	@Override
	@Transactional
	public ConsultaCampDto consultaCampCreate(
			Long consultaId, 
			ConsultaCampDto consultaCamp) throws PermisDenegatException {

		logger.debug(
				"Creant nou camp per una consulta del tipus d'expedient (" +
				"consultaId =" + consultaId + ", " +
				"consultaCamp=" + consultaCamp + ")");

		ConsultaCamp entity = new ConsultaCamp();
		
		entity.setCampCodi(consultaCamp.getCampCodi());
		entity.setCampDescripcio(consultaCamp.getCampDescripcio());
		entity.setConsulta(consultaRepository.findOne(consultaId));
		entity.setTipus(ConsultaCamp.TipusConsultaCamp.valueOf(consultaCamp.getTipus().toString()));

		entity.setDefprocJbpmKey(consultaCamp.getDefprocJbpmKey());
		entity.setDefprocVersio(consultaCamp.getDefprocVersio());

		entity.setOrdre(consultaCampRepository.getNextOrdre(
				consultaId,
				entity.getTipus()));
		if (consultaCamp.getParamTipus() != null )
			entity.setParamTipus(ConsultaCamp.TipusParamConsultaCamp.valueOf(consultaCamp.getParamTipus().toString()));
		
		return conversioTipusHelper.convertir(
				consultaCampRepository.save(entity),
				ConsultaCampDto.class);
	}

	@Override
	@Transactional
	public void consultaCampDelete(Long consultaCampId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la consultaCamp del tipus d'expedient (" +
				"consultaCampId=" + consultaCampId +  ")");
		
		ConsultaCamp consultaCamp = consultaCampRepository.findOne(consultaCampId);
		consultaCampRepository.delete(consultaCamp);	
		consultaCampRepository.flush();
		reordenarCampsConsulta(consultaCamp.getConsulta().getId(), consultaCamp.getTipus());
	}
	
	@Override
	@Transactional
	public void consultaCampCols(Long consultaCampId, String propietat, int valor) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Canviant ample de columnes de filtre de la consultaCamp del tipus d'expedient (" +
				"consultaCampId=" + consultaCampId +  ")");
		
		ConsultaCamp consultaCamp = consultaCampRepository.findOne(consultaCampId);
		if ("ampleCols".equals(propietat)) {
			consultaCamp.setAmpleCols(valor);
		} else if ("buitCols".equals(propietat)) {
			consultaCamp.setBuitCols(valor);
		}
		
		definirAmpleBuit(consultaCamp);
		
		consultaCampRepository.saveAndFlush(consultaCamp);
	}

	/** Funció per reasignar el valor d'ordre dins dels camps d'una consulta de tipus registre. */
	private void reordenarCampsConsulta(
			Long consultaId,
			ConsultaCamp.TipusConsultaCamp tipus) {
		List<ConsultaCamp> consultaCamps = consultaCampRepository.findAmbConsultaIdOrdenats(
				consultaId,
				tipus);		
		int i = 0;
		for (ConsultaCamp c : consultaCamps) {
			c.setOrdre(i);
			consultaCampRepository.saveAndFlush(c);
			i++;
		}
	}

	@Override
	@Transactional
	public boolean consultaCampMourePosicio(
			Long id, 
			int posicio) {
		
		boolean ret = false;
		ConsultaCamp consultaCamp = consultaCampRepository.findOne(id);
		if (consultaCamp != null) {
			List<ConsultaCamp> campsRegistre = consultaCampRepository.findAmbConsultaIdOrdenats(
					consultaCamp.getConsulta().getId(),
					consultaCamp.getTipus());
			int index = campsRegistre.indexOf(consultaCamp);
			if(posicio != index) {	
				consultaCamp = campsRegistre.get(index);
				campsRegistre.remove(consultaCamp);
				campsRegistre.add(posicio, consultaCamp);
				int i = 0;
				for (ConsultaCamp c : campsRegistre) {
					c.setOrdre(i);
					consultaCampRepository.saveAndFlush(c);
					i++;
				}
			}
		}
		return ret;				
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ConsultaCampDto> consultaCampFindPerDatatable(
			Long consultaId,
			TipusConsultaCamp tipus,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els consultaCamps per un camp registre del tipus d'expedient per datatable (" +
				"entornId=" + consultaId + ", " +
				"filtre=" + filtre + ")");
						
		Map<String, String> mapeigPropietatsOrdenacio = new HashMap<String, String>();
		mapeigPropietatsOrdenacio.put("campTipus", "camp.tipus");
		mapeigPropietatsOrdenacio.put("campEtiqueta", "camp.etiqueta");
		
		PaginaDto<ConsultaCampDto> paginaConsultaCamps = paginacioHelper.toPaginaDto(
				consultaCampRepository.findByFiltrePaginat(
						consultaId, 
						ConsultaCamp.TipusConsultaCamp.valueOf(tipus.toString()),
						paginacioHelper.toSpringDataPageable(
								paginacioParams,
								mapeigPropietatsOrdenacio)),
				ConsultaCampDto.class);
		
		// Completa el contingut de les etiquetes i els tipus
		for (ConsultaCampDto consultaCamp : paginaConsultaCamps.getContingut()) {
			if (consultaCamp.getCampCodi().startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
				// camp de l'expedient
				consultaCamp.setCampTipus(CampTipusDto.STRING);
				consultaCamp.setCampEtiqueta(this.getEtiquetaCampExpedient(consultaCamp.getCampCodi()));
			} else {
				Consulta consulta = consultaRepository.findOne(consultaId);
				Long expedientTipusId = consulta.getExpedientTipus() != null? consulta.getExpedientTipus().getId() : null;
				if (consultaCamp.getDefprocJbpmKey() != null) {
					// camp de la definició de procés
					DefinicioProces definicioProces = definicioProcesRepository.findByJbpmKeyAndVersio(
							consultaCamp.getDefprocJbpmKey(),
							consultaCamp.getDefprocVersio());
					if (definicioProces != null) {
						Camp camp = campRepository.findByDefinicioProcesAndCodi(
								definicioProces, 
								consultaCamp.getCampCodi());
						if (camp != null) {
							consultaCamp.setCampTipus(CampTipusDto.valueOf(camp.getTipus().toString()));
							consultaCamp.setCampEtiqueta(camp.getEtiqueta());
						}
					}
				} else {
					// camp de l'expedient
					Camp camp = campRepository.findByExpedientTipusAndCodi(
							expedientTipusId, 
							consultaCamp.getCampCodi(),
							false);
					if (camp != null) {
						consultaCamp.setCampTipus(CampTipusDto.valueOf(camp.getTipus().toString()));
						consultaCamp.setCampEtiqueta(camp.getEtiqueta());
					}
				}
					
			}
		}
		return paginaConsultaCamps;		
	}		
	
	/** Transforma el codi del camp de l'expedient pel seu literal corresponent. */
	private String getEtiquetaCampExpedient(String campCodi) {
		String etiqueta;
		if (ExpedientCamps.EXPEDIENT_CAMP_ID.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.id");
		else if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.numero");
		else if (ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.titol");
		else if (ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.comentari");
		else if (ExpedientCamps.EXPEDIENT_CAMP_INICIADOR.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.indicador");
		else if (ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.responsable");
		else if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.data_ini");
		else if (ExpedientCamps.EXPEDIENT_CAMP_ESTAT.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.estat");
		else if (ExpedientCamps.EXPEDIENT_CAMP_GEOREF.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("comuns.georeferencia.codi");
		else if (ExpedientCamps.EXPEDIENT_CAMP_GEOX.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("comuns.georeferencia.coordenadaX");
		else if (ExpedientCamps.EXPEDIENT_CAMP_GEOY.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("comuns.georeferencia.coordenadaY");
		else 
			etiqueta = campCodi;
		return etiqueta;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConsultaCampDto> consultaCampFindCampAmbConsultaIdAndTipus(
			Long consultaId, 
			TipusConsultaCamp tipus) {
		logger.debug(
				"Consultant els camps per una consulta i tipus (" +
				"consultaId=" + consultaId + ", " +
				"tipus=" + tipus + ")");
		return conversioTipusHelper.convertirList(
				consultaCampRepository.findCampsConsulta(
						consultaId,
						ConsultaCamp.TipusConsultaCamp.valueOf(tipus.toString())), 
				ConsultaCampDto.class);
	}
	
	@Override
	@Transactional
	public ConsultaCampDto consultaCampUpdate(ConsultaCampDto consultaCamp) 
						throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant el parametre de la consulta del tipus d'expedient existent (" +
				"consultaCamp.id=" + consultaCamp.getId() + ", " +
				"consultaCamp =" + consultaCamp + ")");
		
		ConsultaCamp entity = consultaCampRepository.findOne(consultaCamp.getId());

		entity.setCampCodi(consultaCamp.getCampCodi());
		entity.setCampDescripcio(consultaCamp.getCampDescripcio());
		entity.setTipus(ConsultaCamp.TipusConsultaCamp.valueOf(consultaCamp.getTipus().toString()));
		entity.setParamTipus(entity.getParamTipus());
		
		return conversioTipusHelper.convertir(
				consultaCampRepository.save(entity),
				ConsultaCampDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ConsultaCampDto consultaCampFindAmbTipusICodiPerValidarRepeticio(
			Long consultaId, 
			TipusConsultaCamp tipus,
			String campCodi) throws NoTrobatException {
		logger.debug(
				"Consultant el camp de la consulta del tipus d'expedient per codi per validar repetició (" +
				"consultaId=" + consultaId + ", " +
				"tipus=" + tipus + ", " +
				"campCodi = " + campCodi + ")");
		
		return conversioTipusHelper.convertir(
				consultaCampRepository.findByConsultaAndTipusAndCampCodi(
						consultaRepository.findOne(consultaId),
						ConsultaCamp.TipusConsultaCamp.valueOf(tipus.toString()),
						campCodi),
				ConsultaCampDto.class);
	}		
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<String> mapeigFindCodiHeliumAmbTipus(
			Long expedientTipusId, 
			TipusMapeig tipus) {
		logger.debug(
				"Consultant els codis helium dels mapegos segons un tipus de filtre");
		
		ExpedientTipus expedientTipus = 
				expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
						expedientTipusId);
		List<String> codisHelium = mapeigSistraRepository.findCodiHeliumByExpedientTipusAndTipus(
				expedientTipus, 
				MapeigSistra.TipusMapeig.valueOf(tipus.toString()));

		return codisHelium;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Map<TipusMapeig, Long> mapeigCountsByTipus(Long expedientTipusId) {
		logger.debug(
				"Consultant els codis helium dels mapegos segons un tipus de filtre");
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		List<Object[]> mapejosCount = mapeigSistraRepository.countTipus(
				expedientTipus);
		Map<TipusMapeig, Long> recomptes = new HashMap<TipusMapeig, Long>();
		MapeigSistra.TipusMapeig tipus;
		Long count;
		for (Object[] mc : mapejosCount) {
			tipus = (MapeigSistra.TipusMapeig) mc[0];
			count = (Long) mc[1];
			recomptes.put(TipusMapeig.valueOf(tipus.toString()), count);
		}
		// Assegura que el valor hi sigui en el resultat del recompte
		for (TipusMapeig t : TipusMapeig.values())
			if (! recomptes.containsKey(t))
				recomptes.put(t, 0L);

		return recomptes;
	}	
	
	@Override
	public PaginaDto<MapeigSistraDto> mapeigFindPerDatatable(
			Long expedientTipusId, 
			TipusMapeig tipus, 
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els mapejos per un tipus d'expedient per datatable (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"tipus=" + tipus + ")");
		
		return paginacioHelper.toPaginaDto(
				mapeigSistraRepository.findByFiltrePaginat(
						expedientTipusId, 
						MapeigSistra.TipusMapeig.valueOf(tipus.toString()),
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				MapeigSistraDto.class);		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public MapeigSistraDto mapeigCreate(
			Long expedientTipusId, 
			MapeigSistraDto mapeig) throws PermisDenegatException {

		logger.debug(
				"Creant un nou mapeig per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"mapeig=" + mapeig + ")");
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		
		MapeigSistra entity = new MapeigSistra();
				
		entity.setCodiSistra(mapeig.getCodiSistra());
		if (mapeig.getTipus() == TipusMapeig.Adjunt) 
			entity.setCodiHelium(mapeig.getCodiSistra());
		else
			entity.setCodiHelium(mapeig.getCodiHelium());
		entity.setTipus(MapeigSistra.TipusMapeig.valueOf(mapeig.getTipus().toString()));
		// MapeigSistra associat a l'expedient
		entity.setExpedientTipus(expedientTipus);		

		return conversioTipusHelper.convertir(
				mapeigSistraRepository.save(entity),
				MapeigSistraDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public MapeigSistraDto mapeigUpdate(MapeigSistraDto mapeig) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant el mapeig del tipus d'expedient existent (" +
				"mapeig.id=" + mapeig.getId() + ", " +
				"mapeig =" + mapeig + ")");
		MapeigSistra entity = mapeigSistraRepository.findOne(mapeig.getId());

		entity.setCodiSistra(mapeig.getCodiSistra());
		if (mapeig.getTipus() == TipusMapeig.Adjunt) 
			entity.setCodiHelium(mapeig.getCodiSistra());
		else
			entity.setCodiHelium(mapeig.getCodiHelium());
				
		return conversioTipusHelper.convertir(
				mapeigSistraRepository.save(entity),
				MapeigSistraDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void mapeigDelete(Long mapeigMapeigSistraId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant el mapeig del tipus d'expedient (" +
				"mapeigId=" + mapeigMapeigSistraId +  ")");
		MapeigSistra entity = mapeigSistraRepository.findOne(mapeigMapeigSistraId);
		mapeigSistraRepository.delete(entity);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public MapeigSistraDto mapeigFindAmbCodiHeliumPerValidarRepeticio(
			Long expedientTipusId, 
			String codiHelium) {
		logger.debug(
				"Consultant el mapeig del tipus d'expedient per codi helium per validar repetició (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"codiHelium = " + codiHelium + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		return conversioTipusHelper.convertir(
				mapeigSistraRepository.findByExpedientTipusAndCodiHelium(expedientTipus, codiHelium),
				MapeigSistraDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public MapeigSistraDto mapeigFindAmbCodiSistraPerValidarRepeticio(
			Long expedientTipusId, 
			TipusMapeig tipusMapeig,
			String codiSistra) {
		logger.debug(
				"Consultant el mapeig del tipus d'expedient per codi sistra per validar repetició (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"tipusMapeig=" + tipusMapeig + ", " +
				"codiSistra = " + codiSistra + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		MapeigSistra.TipusMapeig tipus = tipusMapeig != null ? 
				MapeigSistra.TipusMapeig.valueOf(tipusMapeig.toString())
				: null;
		return conversioTipusHelper.convertir(
				mapeigSistraRepository.findByExpedientTipusAndTipusAndCodiSistra(expedientTipus, tipus, codiSistra),
				MapeigSistraDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<MapeigSistraDto> mapeigFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		List<MapeigSistra> mapejosSistra = mapeigSistraRepository.findAmbExpedientTipus(expedientTipusId);
		return conversioTipusHelper.convertirList(
				mapejosSistra, 
				MapeigSistraDto.class);
	}	
	
	private void definirAmpleBuit(ConsultaCamp consultaCamp) {
		int ample = consultaCamp.getAmpleCols();
		int buit = consultaCamp.getBuitCols();
		
		if (ample > 12)
			ample = 12;
		else if (ample <= 0)
			ample = 0;
			
		if (buit > 12)
			buit = 12;
		else if (buit < -12)
			buit = -12;
		
		int absAmple = Math.abs(ample);
		int absBuit = Math.abs(buit);
		int totalCols = absAmple + absBuit;
		if (totalCols > 12) {
			int diff = totalCols - 12;
			if (buit >= 0)
				buit -= diff;
			else
				buit += diff;
		}
		
		consultaCamp.setAmpleCols(ample);
		consultaCamp.setBuitCols(buit);
	}

	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PersonaDto> personaFindAll(
			Long entornId,
			Long expedientTipusId) throws Exception {
		logger.debug(
				"Consultant persones amb permis del tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ")");
		
		expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
				expedientTipusId);
		
		// Recupera tots els codis d'usuaris a partir dels permisos
		List<PermisDto> permisos = permisosHelper.findPermisos(
				expedientTipusId,
				ExpedientTipus.class);
		Set<String> codis = new TreeSet<String>();
		String codi;
		List<ParellaCodiValor> parametres;
		for (PermisDto permis : permisos) {
			if (permis.getPrincipalTipus() == PrincipalTipusEnumDto.USUARI) {
				// usuari
				codi = permis.getPrincipalNom();
				if (!codis.contains(codi))
					codis.add(codi);
			} else {
				// rol
				parametres = new ArrayList<ParellaCodiValor>();
				parametres.add(new ParellaCodiValor("rol", permis.getPrincipalNom()));
				List<FilaResultat> files;

				files = dominiHelper.consultaDominiIntern(
						"USUARIS_PER_ROL",
						parametres);
				for (FilaResultat fila : files) {					
					codi = fila.getColumnes().get(0).getValor().toString();
					if (!codis.contains(codi))
						codis.add(codi);
				}
			}
		}
		
		// Consulta les dades de les persones a partir dels codis d'usuari
		List<PersonaDto> persones = new ArrayList<PersonaDto>();
		for (String c : codis)
			persones.add(pluginHelper.personaFindAmbCodi(c));
		
		return persones;
	}

	@Override
	@Transactional
	public ExpedientTipusDto updateMetadadesNti(
			Long entornId,
			Long expedientTipusId,
			boolean actiu,
			String organo,
			String clasificacion,
			String serieDocumental,
			boolean arxiuActiu) {
		logger.debug(
				"Modificant tipus d'expedient amb les metadades (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"actiu=" + actiu + ", " +
				"organo=" + organo + ", " +
				"clasificacion=" + clasificacion + ", " +
				"serieDocumental=" + serieDocumental + ", " +
				"arxiuActiu=" + arxiuActiu + ")");
		ExpedientTipus entity = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
				expedientTipusId);
		entity.setNtiActiu(actiu);
		entity.setNtiOrgano(organo);
		entity.setNtiClasificacion(clasificacion);
		entity.setNtiSerieDocumental(serieDocumental);
		entity.setArxiuActiu(arxiuActiu);
		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(entity),
				ExpedientTipusDto.class);	
	}

	
	@Override
	@Transactional
	public ExpedientTipusDto updateIntegracioPinbal(
			Long entornId,
			Long expedientTipusId,
			boolean pinbalActiu,
			String pinbalNifCif) {
		logger.debug(
				"Modificant tipus d'expedient amb les dades d'integració amb Pinbal (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"pinbalActiu=" + pinbalActiu + ", " +
				"pinbalNifCif=" + pinbalNifCif +")");
		ExpedientTipus entity = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(
				expedientTipusId);
		entity.setPinbalActiu(pinbalActiu);
		entity.setPinbalNifCif(pinbalNifCif);
		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(entity),
				ExpedientTipusDto.class);	
	}
	
	@Override
	@Transactional
	public ExpedientTipusDto updateIntegracioNotib(
			Long expedientTipusId, 
			String notibEmisor, 
			String notibCodiProcediment,
			boolean notibActiu) {

		logger.debug("Modificant tipus d'expedient amb dades d'integracio amb Notib (expedientTipus=" + expedientTipusId + ")");
		
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);

		expedientTipus.setNotibActiu(notibActiu);
		if (notibActiu) {
			expedientTipus.setNotibEmisor(notibEmisor);
			expedientTipus.setNotibCodiProcediment(notibCodiProcediment);
		}
		
		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(expedientTipus),
				ExpedientTipusDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findPerDistribucio(String codiProcediment, String codiAssumpte) {
		logger.debug(
				"Consultant el tipus d'expedient actiu per a un codi de procediment de Distribucio (" +
				"codiProcediment = " + codiProcediment + ", " + 
				"codiAssumpte = " + codiAssumpte + ")");
		ExpedientTipus expedientTipus = null;

		// Consulta els diferents tipus d'expedients aplicables el codi procediment i el codi assumpte
		List<ExpedientTipus> expedientsTipus = expedientTipusRepository.findPerDistribuir(
					codiProcediment == null ? "" : codiProcediment,
					codiAssumpte == null ? "" : codiAssumpte);
		
		// Si hi ha més d'un resultat retorna el primer
		if (!expedientsTipus.isEmpty())
			expedientTipus = expedientsTipus.get(0);
		
		return conversioTipusHelper.convertir(
				expedientTipus,
				ExpedientTipusDto.class);
	}	

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findPerDistribucioValidacio(String codiProcediment, String codiAssumpte) {
		logger.debug(
				"Consultant el tipus d'expedient actiu per a un codi de procediment de Distribucio per validar que no es repeteixin (" +
				"codiProcediment = " + codiProcediment + ", " + 
				"codiAssumpte = " + codiAssumpte + ")");
		ExpedientTipus expedientTipus = null;

		// Consulta els diferents tipus d'expedients aplicables el codi procediment i el codi assumpte
		List<ExpedientTipus> expedientsTipus = expedientTipusRepository.findPerDistribuirValidacio(
					codiProcediment == null || codiProcediment.isEmpty(),
					codiProcediment,
					codiAssumpte == null || codiAssumpte.isEmpty(),
					codiAssumpte);
		
		// Revisa el llistat per veure si n'hi ha cap que coincideixi exactament amb els valors nulls o valors informats
		for (ExpedientTipus et : expedientsTipus) {
			if (et.isDistribucioActiu()
					&& StringUtils.equals(codiProcediment, et.getDistribucioCodiProcediment())
					&& StringUtils.equals(codiAssumpte, et.getDistribucioCodiAssumpte())) {
				expedientTipus = et;
				break;
			}
		}		
		return conversioTipusHelper.convertir(
				expedientTipus,
				ExpedientTipusDto.class);
	}
	
	@Override
	public List<ExpedientTipusEstadisticaDto> findEstadisticaByFiltre(
			Integer anyInicial, 
			Integer anyFinal,
			Long entornId, 
			Long expedientTipusId, 
			Boolean anulats, 
			String numero, 
			String titol, 
			EstatTipusDto estatTipus,
			Long estatId,
			Boolean aturat) {
		
		
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		ExpedientTipus expedientTipus = null;
		if(expedientTipusId != null) {
			expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		}
		// Comprova l'accés a l'estat
		Estat estat = null;
		if (estatId != null) {
			if (estatId > 0) {
				estat = estatRepository.findByExpedientTipusAndIdAmbHerencia(
						expedientTipus.getId(), 
						estatId);
				if (estat == null) {
					logger.debug("No s'ha trobat l'estat (expedientTipusId=" + expedientTipusId + ", estatId=" + estatId + ")");
					throw new NoTrobatException(Estat.class,estatId);
				}
			} else {
				// Estat iniciat o finaltizat
				estatId = null;
			}
		}		
		
		return expedientTipusRepository.findEstadisticaByFiltre(
											anyInicial == null,
											anyInicial,
											anyFinal == null,
											anyFinal,
											entorn,
											expedientTipus == null,
											expedientTipus,
											anulats == null,
											anulats,
											numero == null || numero.isEmpty(),
											numero, 
											titol == null || titol.isEmpty(),
											titol, 
											EstatTipusDto.INICIAT.equals(estatTipus),
											EstatTipusDto.FINALITZAT.equals(estatTipus),
											estatId == null,
											estatId,
											aturat == null,
											aturat);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);

	@Override
	public PaginaDto<ExpedientTipusDto> findTipologiesByFiltrePaginat(
			Long entornId, 
			ExpedientTipusFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els tipus d'expedient per datatable (" +
				"expedientTipusFiltreDto=" + filtreDto + ")");

		String codiTipologia = filtreDto.getCodiTipologia();	
		String nomTipologia = filtreDto.getNomTipologia();
		String codiSIA = filtreDto.getCodiSIA();
		String numRegistre = filtreDto.getNumRegistre();

		
		List<Long> expedientsTipusIds;
		if (numRegistre == null || "".equals(numRegistre)) {
			expedientsTipusIds = new ArrayList<Long>();
		} else {
			expedientsTipusIds = expedientTipusRepository.findIdsByNumeroRegistre(numRegistre);
		}
		if (expedientsTipusIds.isEmpty()) {
			expedientsTipusIds.add(0L);
		} else if (expedientsTipusIds.size() > 1000) {
			throw new ValidacioException("La cerca per número de registre ha retornat " + expedientsTipusIds.size() + ". Concreti el número de registre.");
		}
		return paginacioHelper.toPaginaDto(
				expedientTipusRepository.findByTipologia(
						entornId == null,
						entornId == null? 0L : entornId,
						codiTipologia == null || codiTipologia.isEmpty(),
						codiTipologia == null || codiTipologia.isEmpty() ? "" : codiTipologia,
						nomTipologia == null || nomTipologia.isEmpty(),
						nomTipologia == null || nomTipologia.isEmpty()? "" : nomTipologia,
						codiSIA == null || codiSIA.isEmpty(),
						codiSIA == null || codiSIA.isEmpty() ? "" : codiSIA, 
						numRegistre == null || numRegistre.isEmpty(),
								expedientsTipusIds,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				ExpedientTipusDto.class);
		}
}