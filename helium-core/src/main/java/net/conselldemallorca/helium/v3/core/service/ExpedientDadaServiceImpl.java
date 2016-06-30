/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.helper.VariableHelper;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDadaService;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;


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
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private RegistreRepository registreRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;



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
		expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DATA_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_CREAR,
				varCodi);
		optimitzarValorPerConsultesDominiGuardar(processInstanceId, varCodi, varValor);
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
		expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DATA_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_MODIFICAR,
				varCodi);
		Object valorVell = variableHelper.getVariableJbpmProcesValor(
				processInstanceId,
				varCodi);
		optimitzarValorPerConsultesDominiGuardar(
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
		expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DATA_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_ESBORRAR,
				varCodi);
		jbpmHelper.deleteProcessInstanceVariable(processInstanceId, varCodi);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
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
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
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
		return conversioTipusHelper.convertirList(
				definicioProces.getAgrupacions(),
				CampAgrupacioDto.class);
	}



	private void optimitzarValorPerConsultesDominiGuardar(
			String processInstanceId,
			String varName,
			Object varValue) {
		JbpmProcessDefinition jpd = jbpmHelper.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(jpd.getId());
		Camp camp = campRepository.findByDefinicioProcesAndCodi(
				definicioProces,
				varName);
		if (camp != null && camp.isDominiCacheText()) {
			if (varValue != null) {
				if (camp.getTipus().equals(TipusCamp.SELECCIO) ||
					camp.getTipus().equals(TipusCamp.SUGGEST)) {
					
					String text;
					try {
						text = variableHelper.getTextPerCamp(
								camp, 
								varValue, 
								null, 
								processInstanceId);
					} catch (Exception e) {
						text = "";
					}
					
					jbpmHelper.setProcessInstanceVariable(processInstanceId, JbpmVars.PREFIX_VAR_DESCRIPCIO + varName, text);
				}
			}
		}
		jbpmHelper.setProcessInstanceVariable(processInstanceId, varName, varValue);
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
