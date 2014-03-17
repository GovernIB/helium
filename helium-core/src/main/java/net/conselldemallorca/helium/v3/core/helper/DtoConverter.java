/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.jbpm3.handlers.BasicActionHandler;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto.TascaEstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto.TascaPrioritatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

/**
 * Convertidor de dades cap a DTOs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("dtoConverterV3")
public class DtoConverter {	
	@Resource
	private PersonaHelper personaHelper;	
	@Resource
	private TascaRepository tascaRepository;
	@Resource(name="tascaServiceV3")
	private TascaService tascaService;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private PluginPersonaDao pluginPersonaDao;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	public PersonaDto getResponsableTasca(String responsableCodi) {
		try {
			PersonaDto persona = pluginHelper.findPersonaAmbCodi(responsableCodi);
			if (persona == null) {
				persona = new PersonaDto();
				persona.setCodi(responsableCodi);
			}
			return persona;
		} catch (Exception ex) {
			logger.error("Error al obtenir informació de la persona (codi=" + responsableCodi + ")", ex);
			PersonaDto persona = new PersonaDto();
			persona.setCodi(responsableCodi);
			return persona;
		}
	}

	private List<String> getCampsExpressioTitol(String expressio) {
		List<String> resposta = new ArrayList<String>();
		String[] parts = expressio.split("\\$\\{");
		for (String part: parts) {
			int index = part.indexOf("}");
			if (index != -1)
				resposta.add(part.substring(0, index));
		}
		return resposta;
	}

	private String getTitolPerTasca(
			JbpmTask task,
			Tasca tasca) {
		String titol = null;
		if (tasca != null) {
			Map<String, Object> textPerCamps = new HashMap<String, Object>();
			titol = tasca.getNom();
			if (tasca.getNomScript() != null && tasca.getNomScript().length() > 0) {
				List<String> campsExpressio = getCampsExpressioTitol(tasca.getNomScript());
				Map<String, Object> valors = jbpmHelper.getTaskInstanceVariables(task.getId());
				valors.putAll(jbpmHelper.getProcessInstanceVariables(task.getProcessInstanceId()));
				for (String campCodi: campsExpressio) {
					Set<Camp> campsDefinicioProces = tasca.getDefinicioProces().getCamps();
					for (Camp camp: campsDefinicioProces) {
						if (camp.getCodi().equals(campCodi)) {
							ExpedientDadaDto expedientDada = variableHelper.getDadaPerInstanciaTasca(
									task.getId(),
									campCodi);
							if (expedientDada != null && expedientDada.getText() != null) {
								textPerCamps.put(
										campCodi,
										expedientDada.getText());
							}
							break;
						}
					}
				}
				try {
					titol = (String)jbpmHelper.evaluateExpression(
							task.getId(),
							task.getProcessInstanceId(),
							tasca.getNomScript(),
							textPerCamps);
				} catch (Exception ex) {
					logger.error("No s'ha pogut evaluar l'script per canviar el titol de la tasca", ex);
				}
			}
		} else {
			titol = task.getName();
		}
		return titol;
	}
	public DadesCacheTasca getDadesCacheTasca(
			JbpmTask task,
			Expedient expedient) {
		DadesCacheTasca dadesCache = null;
		if (!task.isCacheActiu()) {
			DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
					task.getProcessDefinitionId());
			Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
					task.getName(),
					definicioProces);
			String titol = tasca.getNom();
			if (tasca.getNomScript() != null && tasca.getNomScript().length() > 0)
				titol = getTitolPerTasca(task, tasca);
			task.setFieldFromDescription(
					"entornId",
					expedient.getEntorn().getId().toString());
			task.setFieldFromDescription(
					"titol",
					titol);
			task.setFieldFromDescription(
					"identificador",
					expedient.getIdentificador());
			task.setFieldFromDescription(
					"identificadorOrdenacio",
					expedient.getIdentificadorOrdenacio());
			task.setFieldFromDescription(
					"numeroIdentificador",
					expedient.getNumeroIdentificador());
			task.setFieldFromDescription(
					"expedientTipusId",
					expedient.getTipus().getId().toString());
			task.setFieldFromDescription(
					"expedientTipusNom",
					expedient.getTipus().getNom());
			task.setFieldFromDescription(
					"processInstanceId",
					expedient.getProcessInstanceId());
			task.setFieldFromDescription(
					"tramitacioMassiva",
					new Boolean(tasca.isTramitacioMassiva()).toString());
			task.setFieldFromDescription(
					"definicioProcesJbpmKey",
					tasca.getDefinicioProces().getJbpmKey());
			task.setCacheActiu();
			jbpmHelper.describeTaskInstance(
					task.getId(),
					titol,
					task.getDescriptionWithFields());
		}
		dadesCache = new DadesCacheTasca(
				new Long(task.getFieldFromDescription("entornId")),
				task.getFieldFromDescription("titol"),
				task.getFieldFromDescription("identificador"),
				task.getFieldFromDescription("identificadorOrdenacio"),
				task.getFieldFromDescription("numeroIdentificador"),
				new Long(task.getFieldFromDescription("expedientTipusId")),
				task.getFieldFromDescription("expedientTipusNom"),
				task.getFieldFromDescription("processInstanceId"),
				new Boolean(task.getFieldFromDescription("tramitacioMassiva")).booleanValue(),
				task.getFieldFromDescription("definicioProcesJbpmKey"));
		return dadesCache;
	}

	public class DadesCacheTasca {
		private Long entornId;
		private String titol;
		private String identificador;
		private String identificadorOrdenacio;
		private String numeroIdentificador;
		private Long expedientTipusId;
		private String expedientTipusNom;
		private String processInstanceId;
		private boolean tramitacioMassiva;
		private String definicioProcesJbpmKey;
		public DadesCacheTasca(
				Long entornId,
				String titol,
				String identificador,
				String identificadorOrdenacio,
				String numeroIdentificador,
				Long expedientTipusId,
				String expedientTipusNom,
				String processInstanceId,
				boolean tramitacioMassiva,
				String definicioProcesJbpmKey) {
			this.entornId = entornId;
			this.titol = titol;
			this.identificador = identificador;
			this.identificadorOrdenacio = identificadorOrdenacio;
			this.numeroIdentificador = numeroIdentificador;
			this.expedientTipusId = expedientTipusId;
			this.expedientTipusNom = expedientTipusNom;
			this.processInstanceId = processInstanceId;
			this.tramitacioMassiva = tramitacioMassiva;
			this.definicioProcesJbpmKey = definicioProcesJbpmKey;
		}
		public Long getEntornId() {
			return entornId;
		}
		public String getTitol() {
			return titol;
		}
		public String getIdentificador() {
			return identificador;
		}
		public String getIdentificadorOrdenacio() {
			return identificadorOrdenacio;
		}
		public String getNumeroIdentificador() {
			return numeroIdentificador;
		}
		public Long getExpedientTipusId() {
			return expedientTipusId;
		}
		public String getExpedientTipusNom() {
			return expedientTipusNom;
		}
		public String getProcessInstanceId() {
			return processInstanceId;
		}
		public boolean isTramitacioMassiva() {
			return tramitacioMassiva;
		}
		public String getDefinicioProcesJbpmKey() {
			return definicioProcesJbpmKey;
		}
	}
	
	public ExpedientTascaDto toExpedientTascaDto(
			JbpmTask task,
			Expedient expedient) {
		ExpedientTascaDto dto = new ExpedientTascaDto();
		dto.setId(task.getId());
		DadesCacheTasca dadesCacheTasca = getDadesCacheTasca(
				task,
				expedient);
		dto.setTitol(dadesCacheTasca.getTitol());
		dto.setDescripcio(task.getDescription());
		if (task.isCancelled()) {
			dto.setEstat(TascaEstatDto.CANCELADA);
		} else if (task.isSuspended()) {
			dto.setEstat(TascaEstatDto.SUSPESA);
		} else {
			if (task.isCompleted())
				dto.setEstat(TascaEstatDto.FINALITZADA);
			else
				dto.setEstat(TascaEstatDto.PENDENT);
		}
		dto.setDataLimit(task.getDueDate());
		dto.setDataCreacio(task.getCreateTime());
		dto.setDataInici(task.getStartTime());
		dto.setDataFi(task.getEndTime());
		dto.setValidada(tascaService.isTascaValidada(task));

		if (task.getAssignee() != null) {
			dto.setResponsable(
					getResponsableTasca(task.getAssignee()));
			dto.setResponsableCodi(task.getAssignee());
		}
		Set<String> pooledActors = task.getPooledActors();
		if (pooledActors != null && pooledActors.size() > 0) {
			List<PersonaDto> responsables = new ArrayList<PersonaDto>();
			for (String pooledActor: pooledActors)
				responsables.add(
						getResponsableTasca(pooledActor));
			dto.setResponsables(responsables);
		}
		switch (task.getPriority()) {
		case -2:
			dto.setPrioritat(TascaPrioritatDto.MOLT_BAIXA);
			break;
		case -1:
			dto.setPrioritat(TascaPrioritatDto.BAIXA);
			break;
		case 0:
			dto.setPrioritat(TascaPrioritatDto.NORMAL);
			break;
		case 1:
			dto.setPrioritat(TascaPrioritatDto.ALTA);
			break;
		case 2:
			dto.setPrioritat(TascaPrioritatDto.MOLT_ALTA);
			break;
		}
		dto.setOberta(task.isOpen());
		dto.setCancelada(task.isCancelled());
		dto.setSuspesa(task.isSuspended());
		List<String> outcomes = jbpmHelper.findTaskInstanceOutcomes(
				task.getId());
		if (outcomes != null && !outcomes.isEmpty()) {
			if (outcomes.size() == 1) {
				String primeraTransicio = outcomes.get(0);
				dto.setTransicioPerDefecte(primeraTransicio == null || "".equals(primeraTransicio));
			} else {
				dto.setTransicions(outcomes);
			}
		}
		dto.setExpedientId(expedient.getId());
		dto.setExpedientIdentificador(expedient.getIdentificador());
		dto.setExpedientTipusNom(expedient.getTipus().getNom());
		dto.setProcessInstanceId(task.getProcessInstanceId());
		
		Tasca tasca = tascaRepository.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
				task.getProcessDefinitionId());
		if (task.isCacheActiu()) {
			dto.setNom(task.getFieldFromDescription("titol"));
		} else {
			if (tasca != null)
				dto.setNom(tasca.getNom());
			else
				dto.setNom(task.getName());
		}
		dto.setFormExtern(tasca.getFormExtern());		
		
		dto.setDefinicioProces(conversioTipusHelper.convertir(tasca.getDefinicioProces(), DefinicioProcesDto.class));
		
		dto.setDocumentsComplet(tascaService.isDocumentsComplet(task));
		dto.setSignaturesComplet(tascaService.isSignaturesComplet(task));
		
		dto.setAgafada("true".equals(task.isAgafada()));
		dto.setOutcomes(jbpmHelper.findTaskInstanceOutcomes(task.getId()));

		Map<String, Object> valors = jbpmHelper.getTaskInstanceVariables(task.getId());
		
		DelegationInfo delegationInfo = (DelegationInfo)valors.get(
				TascaService.VAR_TASCA_DELEGACIO);
		
		if (delegationInfo != null) {
			boolean original = task.getId().equals(delegationInfo.getSourceTaskId());
			dto.setDelegada(true);
			dto.setDelegacioOriginal(original);
			dto.setDelegacioData(delegationInfo.getStart());
			dto.setDelegacioSupervisada(delegationInfo.isSupervised());
			dto.setDelegacioComentari(delegationInfo.getComment());
			JbpmTask tascaDelegacio = null;
			if (original) {
				tascaDelegacio = jbpmHelper.getTaskById(delegationInfo.getTargetTaskId());
			} else {
				tascaDelegacio = jbpmHelper.getTaskById(delegationInfo.getSourceTaskId());
			}			
			
			dto.setDelegacioPersona(conversioTipusHelper.convertir(pluginPersonaDao.findAmbCodiPlugin(tascaDelegacio.getAssignee()), PersonaDto.class));
		}
				
		return dto;
	}
	
	public InstanciaProcesDto toInstanciaProcesDto(String processInstanceId) {
		InstanciaProcesDto dto = new InstanciaProcesDto();
		dto.setId(processInstanceId);
		JbpmProcessInstance pi = jbpmHelper.getProcessInstance(processInstanceId);		
		dto.setInstanciaProcesPareId(pi.getParentProcessInstanceId());
		dto.setDefinicioProces(conversioTipusHelper.convertir(definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId()), DefinicioProcesDto.class));		
		return dto;
	}

	public ExpedientTascaDto toTascaInicialDto(
			String startTaskName,
			String jbpmId,
			Map<String, Object> valors) {
		Tasca tasca = tascaRepository.findAmbActivityNameIProcessDefinitionId(
				startTaskName,
				jbpmId);
		ExpedientTascaDto dto = new ExpedientTascaDto();
		dto.setNom(tasca.getNom());
		dto.setTipus(conversioTipusHelper.convertir(tasca.getTipus(), ExpedientTascaDto.TipusTasca.class));
		dto.setJbpmName(tasca.getJbpmName());
		dto.setValidada(false);
		dto.setDocumentsComplet(false);
		dto.setTascaId(tasca.getId());
		dto.setSignaturesComplet(false);
		dto.setDefinicioProces(conversioTipusHelper.convertir(tasca.getDefinicioProces(), DefinicioProcesDto.class));
		dto.setOutcomes(jbpmHelper.findStartTaskOutcomes(jbpmId, startTaskName));
		dto.setFormExtern(tasca.getFormExtern());
		
		return dto;
	}

	public void revisarDadesExpedientAmbValorsEnumeracionsODominis(
			Map<String, DadaIndexadaDto> dadesExpedient,
			List<Camp> campsInforme, 
			String processInstanceId) {
		for (Camp camp: campsInforme) {
			if (camp.getEnumeracio() != null && camp.getDefinicioProces() != null) {
				String dadaIndexadaClau = camp.getDefinicioProces().getJbpmKey() + "/" + camp.getCodi();
				DadaIndexadaDto dadaIndexada = dadesExpedient.get(dadaIndexadaClau);
				if (dadaIndexada != null) {
					String text = variableHelper.getTextVariableSimple(
							camp,
							dadaIndexada.getValorIndex(),
							null,
							null,
							processInstanceId);
					dadaIndexada.setValorMostrar(text);
				}
			}
		}
	}
	
	public ExpedientDto toExpedientDto(Expedient expedient) {
		ExpedientDto dto = new ExpedientDto();
		dto.setId(expedient.getId());
		dto.setProcessInstanceId(expedient.getProcessInstanceId());
		dto.setTitol(expedient.getTitol());
		dto.setNumero(expedient.getNumero());
		dto.setNumeroDefault(expedient.getNumeroDefault());
		dto.setComentari(expedient.getComentari());
		dto.setInfoAturat(expedient.getInfoAturat());
		dto.setAnulat(expedient.isAnulat());
		dto.setDataInici(expedient.getDataInici());
		dto.setIniciadorCodi(expedient.getIniciadorCodi());
		dto.setIniciadorTipus(conversioTipusHelper.convertir(expedient.getIniciadorTipus(), IniciadorTipusDto.class));
		dto.setResponsableCodi(expedient.getResponsableCodi());
		dto.setGrupCodi(expedient.getGrupCodi());
		if (expedient.getIniciadorTipus().equals(IniciadorTipus.INTERN)) {
			if (expedient.getIniciadorCodi() != null)
				dto.setIniciadorPersona(personaHelper.findAmbCodiPlugin(expedient.getIniciadorCodi()));
			if (expedient.getResponsableCodi() != null)
				dto.setResponsablePersona(personaHelper.findAmbCodiPlugin(expedient.getResponsableCodi()));
		}
		if (expedient.getIniciadorTipus().equals(IniciadorTipus.SISTRA))
			dto.setBantelEntradaNum(expedient.getNumeroEntradaSistra());
		dto.setTipus(conversioTipusHelper.convertir(expedient.getTipus(), ExpedientTipusDto.class));
		dto.setEntorn(conversioTipusHelper.convertir(expedient.getEntorn(), EntornDto.class));
		if (expedient.getEstat() != null)
			dto.setEstat(conversioTipusHelper.convertir(expedient.getEstat(), EstatDto.class));
		dto.setGeoPosX(expedient.getGeoPosX());
		dto.setGeoPosY(expedient.getGeoPosY());
		dto.setGeoReferencia(expedient.getGeoReferencia());
		dto.setRegistreNumero(expedient.getRegistreNumero());
		dto.setRegistreData(expedient.getRegistreData());
		dto.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
		dto.setIdioma(expedient.getIdioma());
		dto.setAutenticat(expedient.isAutenticat());
		dto.setTramitadorNif(expedient.getTramitadorNif());
		dto.setTramitadorNom(expedient.getTramitadorNom());
		dto.setInteressatNif(expedient.getInteressatNif());
		dto.setInteressatNom(expedient.getInteressatNom());
		dto.setRepresentantNif(expedient.getRepresentantNif());
		dto.setRepresentantNom(expedient.getRepresentantNom());
		dto.setAvisosHabilitats(expedient.isAvisosHabilitats());
		dto.setAvisosEmail(expedient.getAvisosEmail());
		dto.setAvisosMobil(expedient.getAvisosMobil());
		dto.setErrorDesc(expedient.getErrorDesc());
		dto.setErrorFull(expedient.getErrorFull());
		dto.setNotificacioTelematicaHabilitada(expedient.isNotificacioTelematicaHabilitada());
		dto.setTramitExpedientIdentificador(expedient.getTramitExpedientIdentificador());
		dto.setTramitExpedientClau(expedient.getTramitExpedientClau());
		dto.setErrorsIntegracions(expedient.isErrorsIntegracions());		
		dto.setDataFi(expedient.getDataFi());
		
		return dto;
	}

	public void filtrarVariablesTasca(Map<String, Object> variables) {
		if (variables != null) {
			variables.remove(TascaService.VAR_TASCA_VALIDADA);
			variables.remove(TascaService.VAR_TASCA_DELEGACIO);
			List<String> codisEsborrar = new ArrayList<String>();
			for (String codi: variables.keySet()) {
				if (	codi.startsWith(DocumentHelper.PREFIX_VAR_DOCUMENT) ||
						codi.startsWith(DocumentHelper.PREFIX_SIGNATURA) ||
						codi.startsWith(DocumentHelper.PREFIX_ADJUNT) ||
						codi.startsWith(BasicActionHandler.PARAMS_RETROCEDIR_VARIABLE_PREFIX))
					codisEsborrar.add(codi);
			}
			for (String codi: codisEsborrar)
				variables.remove(codi);
		}
	}
	
	private static final Log logger = LogFactory.getLog(DtoConverter.class);
}
