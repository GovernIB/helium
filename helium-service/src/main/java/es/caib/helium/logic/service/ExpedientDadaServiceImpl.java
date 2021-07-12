/**
 * 
 */
package es.caib.helium.logic.service;

import es.caib.helium.logic.helper.ConversioTipusServiceHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.HerenciaHelper;
import es.caib.helium.logic.helper.IndexHelper;
import es.caib.helium.logic.helper.VariableHelper;
import es.caib.helium.logic.intf.WProcessDefinition;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi;
import es.caib.helium.logic.intf.dto.CampAgrupacioDto;
import es.caib.helium.logic.intf.dto.ExpedientDadaDto;
import es.caib.helium.logic.intf.dto.InstanciaProcesDto;
import es.caib.helium.logic.intf.service.ExpedientDadaService;
import es.caib.helium.logic.intf.util.JbpmVars;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.persist.entity.Camp;
import es.caib.helium.persist.entity.Camp.TipusCamp;
import es.caib.helium.persist.entity.CampAgrupacio;
import es.caib.helium.persist.entity.DefinicioProces;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.Registre;
import es.caib.helium.persist.repository.CampAgrupacioRepository;
import es.caib.helium.persist.repository.CampRepository;
import es.caib.helium.persist.repository.DefinicioProcesRepository;
import es.caib.helium.persist.repository.RegistreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Implementació dels mètodes del servei ExpedientDadaService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientDadaServiceImpl implements ExpedientDadaService {

	@Resource
	private CampRepository campRepository;
	@Resource
	private CampAgrupacioRepository campAgrupacioRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private RegistreRepository registreRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private WorkflowRetroaccioApi workflowRetroaccioApi;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;



	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void create(
			Long expedientId,
			String processInstanceId,
			String varCodi,
			Object varValor) {
		logger.debug("Modificant dada de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"varCodi=" + varCodi + ", " +
				"varValor=" + varValor + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DATA_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
				processInstanceId,
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.PROCES_VARIABLE_CREAR,
				varCodi);
		optimitzarValorPerConsultesDominiGuardar(expedient.getTipus(), processInstanceId, varCodi, varValor);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		Registre registre = crearRegistreInstanciaProces(
				expedientId,
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.MODIFICAR);
		registre.setMissatge("Crear variable '" + varCodi + "'");
		if (varValor != null)
			registre.setValorNou(varValor.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void update(
			Long expedientId,
			String processInstanceId,
			String varCodi,
			Object varValor) {
		
		logger.debug("Modificant dada de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"varCodi=" + varCodi + ", " +
				"varValor=" + varValor + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DATA_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		Object valorVell = variableHelper.getVariableJbpmProcesValor(
				processInstanceId,
				varCodi);
		workflowEngineApi.deleteProcessInstanceVariable(processInstanceId, varCodi);
		// Esborra la descripció per variables que mantenen el valor de la consulta
		Camp camp;
		InstanciaProcesDto instanciaProces = expedientHelper.getInstanciaProcesById(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.getById(instanciaProces.getDefinicioProces().getId());
		if (expedient.getTipus().isAmbInfoPropia()) {
			// obtenir el camp amb expedient tipus codi i codi de la variable
			camp = campRepository.findByExpedientTipusAndCodi(expedient.getTipus().getId(), varCodi, expedient.getTipus().getExpedientTipusPare() != null);
		}else {
			camp = campRepository.findByDefinicioProcesAndCodi(definicioProces, varCodi);
		}
		if (camp != null && camp.isDominiCacheText())
			workflowEngineApi.deleteProcessInstanceVariable(processInstanceId, JbpmVars.PREFIX_VAR_DESCRIPCIO + varCodi);

		workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
				processInstanceId,
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.PROCES_VARIABLE_MODIFICAR,
				varCodi);
		optimitzarValorPerConsultesDominiGuardar(
				expedient.getTipus(),
				processInstanceId,
				varCodi,
				varValor);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		Registre registre = crearRegistreInstanciaProces(
				expedientId,
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.MODIFICAR);
		registre.setMissatge("Modificar variable '" + varCodi + "'");
		if (valorVell != null)
			registre.setValorVell(valorVell.toString());
		if (varValor != null)
			registre.setValorNou(varValor.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(
			Long expedientId,
			String processInstanceId,
			String varCodi) {
		logger.debug("Esborrant dada de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"varCodi=" + varCodi + ")");
		Expedient e = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DATA_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
				processInstanceId,
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.PROCES_VARIABLE_ESBORRAR,
				varCodi);
		workflowEngineApi.deleteProcessInstanceVariable(processInstanceId, varCodi);
		// Esborra la descripció per variables que mantenen el valor de la consulta
		Camp camp;
		InstanciaProcesDto instanciaProces = expedientHelper.getInstanciaProcesById(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.getById(instanciaProces.getDefinicioProces().getId());
		if (e.getTipus().isAmbInfoPropia()) {
			// obtenir el camp amb expedient tipus codi i codi de la variable
			camp = campRepository.findByExpedientTipusAndCodi(e.getTipus().getId(), varCodi, e.getTipus().getExpedientTipusPare() != null);
		}else {
			camp = campRepository.findByDefinicioProcesAndCodi(definicioProces, varCodi);
		}			
		if (camp != null && camp.isDominiCacheText())
			workflowEngineApi.deleteProcessInstanceVariable(processInstanceId, JbpmVars.PREFIX_VAR_DESCRIPCIO + varCodi);
		
		if (e.getTipus().isAmbInfoPropia()) {
			indexHelper.expedientIndexLuceneDelete(processInstanceId, varCodi);
		} else {
			indexHelper.expedientIndexLuceneDelete(processInstanceId, definicioProces.getJbpmKey() + "." + varCodi);
		}
		
		Registre registre = crearRegistreInstanciaProces(
				expedientId,
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.MODIFICAR);
		registre.setMissatge("Esborrar variable '" + varCodi + "'");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientDadaDto findOnePerInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String varCodi) {
		logger.debug("Consultant dada de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"varCodi=" + varCodi + ")");
		return variableHelper.getDadaPerInstanciaProces(
				processInstanceId,
				varCodi,
				true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientDadaDto getDadaBuida(long campId) {
		logger.debug("Consultant dada buida per un camp (campId=" + campId + ")");
		return variableHelper.getDadaBuida(campId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDadaDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		logger.debug("Consultant les variables de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		if (processInstanceId == null) {
			return variableHelper.findDadesPerInstanciaProces(
					expedient.getProcessInstanceId(), 
					true);
		} else {
			expedientHelper.comprovarInstanciaProces(
					expedient,
					processInstanceId);
			return variableHelper.findDadesPerInstanciaProces(
					processInstanceId,
					true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CampAgrupacioDto> agrupacionsFindAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		logger.debug("Consulta de les agrupacions de dades de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		List<CampAgrupacio> agrupacions = new ArrayList<CampAgrupacio>();
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		
		ExpedientTipus expedientTipus = expedient.getTipus();
		
		boolean ambHerencia = HerenciaHelper.ambHerencia(expedientTipus);
		Set<Long> agrupacionsHeretadesIds = new HashSet<Long>();
		Set<String> sobreescritsCodis = new HashSet<String>();

		if (expedientTipus.isAmbInfoPropia()) {
			agrupacions = campAgrupacioRepository.findAmbExpedientTipusOrdenats(expedientTipus.getId(), ambHerencia);
			if (ambHerencia) {
				Long expedientTipusId = expedientTipus.getId();
				for(CampAgrupacio a : agrupacions)
					if(!expedientTipusId.equals(a.getExpedientTipus().getId()))
						agrupacionsHeretadesIds.add(a.getId());
				// Llistat d'elements sobreescrits
				for (CampAgrupacio a : campAgrupacioRepository.findSobreescrits(expedientTipusId)) 
					sobreescritsCodis.add(a.getCodi());
			}	
		} else {
			DefinicioProces definicioProces;
			if (processInstanceId == null) {
				definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
						expedient.getProcessInstanceId());
			} else {
				expedientHelper.comprovarInstanciaProces(
						expedient,
						processInstanceId);
				definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
						processInstanceId);
			}
			agrupacions = campAgrupacioRepository.findAmbDefinicioProcesOrdenats(definicioProces.getId());
		}
		List<CampAgrupacioDto> agrupacionsDto = conversioTipusServiceHelper.convertirList(
				agrupacions, 
				CampAgrupacioDto.class);

		if (ambHerencia) {
			// Completa l'informació del dto
			for(CampAgrupacioDto dto : agrupacionsDto) {
				// Sobreescriu
				if (sobreescritsCodis.contains(dto.getCodi()))
					dto.setSobreescriu(true);
				// Heretat
				if(agrupacionsHeretadesIds.contains(dto.getId()))
					dto.setHeretat(true);
			}
		}		
		return agrupacionsDto;
	}

	/*********************/

	private void optimitzarValorPerConsultesDominiGuardar(
			ExpedientTipus expedientTipus, 
			String processInstanceId,
			String varName,
			Object varValue) {
		WProcessDefinition jpd = workflowEngineApi.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(jpd.getId());
		Camp camp;
		if (expedientTipus.isAmbInfoPropia())
			camp = campRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(), 
					varName,
					expedientTipus.getExpedientTipusPare() != null);
		else {
			camp = campRepository.findByDefinicioProcesAndCodi(
					definicioProces,
					varName);			
		}
		if (camp != null && camp.isDominiCacheText()) {
			if (varValue != null) {
				if (camp.getTipus().equals(TipusCamp.SELECCIO) ||
					camp.getTipus().equals(TipusCamp.SUGGEST)) {
					
					String text;
					try {
						// Consultem el valor de la variable
						text = variableHelper.getTextPerCamp(
								camp, 
								varValue, 
								null, 
								null,
								processInstanceId,
								null,
								null);
					} catch (Exception e) {
						text = "";
					}

					workflowEngineApi.setProcessInstanceVariable(processInstanceId, JbpmVars.PREFIX_VAR_DESCRIPCIO + varName, text);
				}
			}
		}
		workflowEngineApi.setProcessInstanceVariable(processInstanceId, varName, varValue);
	}

	private Registre crearRegistreInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String responsableCodi,
			Registre.Accio accio) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				accio,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		return registreRepository.save(registre);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientDadaServiceImpl.class);

}
