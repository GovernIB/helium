/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.dao.DominiDao;
import net.conselldemallorca.helium.core.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.core.model.exception.DominiException;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Reassignacio;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.hibernate.Validacio;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.core.model.service.LuceneHelper;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.jbpm3.handlers.BasicActionHandler;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampRegistreDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto.TascaEstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto.TascaPrioritatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ValidacioDto;
import net.conselldemallorca.helium.v3.core.api.service.PermissionService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.repository.CampAgrupacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaCampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioValorsRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.FirmaTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.PersonaRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
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
	private ExpedientRepository expedientRepository;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private PersonaRepository personaRepository;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private PluginPersonaDao pluginPersonaDao;
	@Resource
	private EnumeracioValorsHelper enumeracioValorsHelper;
	@Resource
	private EnumeracioValorsRepository enumeracioValorsRepository;
	@Resource
	private DominiHelper dominiHelper;
	@Resource
	private DominiDao dominiDao;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource(name="serviceUtilsV3")
	private ServiceUtils serviceUtils;
	@Resource
	private ConsultaCampRepository consultaCampRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private DocumentTascaRepository documentTascaRepository;
	@Resource
	private FirmaTascaRepository firmaTascaRepository;
	@Resource(name="permissionServiceV3")
	private PermissionService permissionService;
	@Resource
	private MessageSource messageSource;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private LuceneHelper luceneHelper;
	@Resource
	private CampAgrupacioRepository campAgrupacioRepository;

	private Map<Long, Boolean> hasStartTask = new HashMap<Long, Boolean>();
	
	private PersonaDto getResponsableTasca(String responsableCodi) {
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
		dto.setProcessInstanceId(task.getProcessInstanceId());
		
		Tasca tasca = tascaRepository.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
				task.getProcessDefinitionId());
		List<CampTasca> campsTasca = campTascaRepository.findAmbTascaOrdenats(tasca.getId());
		dto.setCamps(toCampsTascaDto(campsTasca));
		List<DocumentTasca> documentsTasca = documentTascaRepository.findAmbTascaOrdenats(tasca.getId());
		dto.setDocuments(toDocumentTascaDto(documentsTasca));
		List<FirmaTasca> signaturesTasca = firmaTascaRepository.findAmbTascaOrdenats(tasca.getId());
		dto.setSignatures(toFirmaTascaDto(signaturesTasca));

		dto.setFormExtern(tasca.getFormExtern());
		dto.setVarsDocuments(
				obtenirVarsDocumentsTasca(
						task.getId(),
						task.getProcessInstanceId(),
						dto.getDocuments()));
		dto.setVarsDocumentsPerSignar(
				obtenirVarsDocumentsPerSignarTasca(
						task.getId(),
						task.getProcessInstanceId(),
						dto.getSignatures()));
		List<CampDto> camps = new ArrayList<CampDto>();
		for (CampTascaDto campTasca: dto.getCamps())
			camps.add(campTasca.getCamp());
		
		dto.setDefinicioProces(toDefinicioProcesDto(tasca.getDefinicioProces()));
		
		dto.setDocumentsComplet(tascaService.isDocumentsComplet(task));
		dto.setSignaturesComplet(tascaService.isSignaturesComplet(task));
		
		dto.setAgafada("true".equals(task.getFieldFromDescription(TascaService.TASKDESC_CAMP_AGAFADA)));
		dto.setOutcomes(jbpmHelper.findTaskInstanceOutcomes(task.getId()));

		Map<String, Object> valors = jbpmHelper.getTaskInstanceVariables(task.getId());
		
		DelegationInfo delegationInfo = (DelegationInfo)valors.get(
				TascaService.VAR_TASCA_DELEGACIO);
		
		dto.setExpedient(toExpedientDto(expedient, false));
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
			
			dto.setDelegacioPersona(new ModelMapper().map(pluginPersonaDao.findAmbCodiPlugin(tascaDelegacio.getAssignee()), PersonaDto.class));
		}
		
		filtrarVariablesTasca(valors);
		
		Map<String, ParellaCodiValorDto> valorsDomini = obtenirValorsDomini(
				task.getId(),
				null,
				camps,
				valors);
		dto.setValorsDomini(valorsDomini);
		Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini = obtenirValorsMultiplesDomini(
				task.getId(),
				null,
				camps,
				valors);
		dto.setValorsMultiplesDomini(valorsMultiplesDomini);
		getServiceUtils().revisarVariablesJbpm(valors);
		dto.setVarsComText(textPerCamps(task.getId(), null, camps, valors, valorsDomini, valorsMultiplesDomini));
		
		dto.setVariables(valors);
		
		return dto;
	}

	private Map<String, ParellaCodiValorDto> obtenirValorsDomini(
			String taskId,
			String processInstanceId,
			Collection<CampDto> camps,
			Map<String, Object> valors) throws DominiException {
		Map<String, ParellaCodiValorDto> resposta = new HashMap<String, ParellaCodiValorDto>();
		if (valors != null) {
			for (CampDto camp: camps) {
				if (!camp.isMultiple() && (camp.getTipus().equals(CampTipusDto.SELECCIO) || camp.getTipus().equals(CampTipusDto.SUGGEST))) {
					Object valor = valors.get(camp.getCodi());
					ParellaCodiValorDto codiValor = obtenirValorDomini(
							taskId,
							processInstanceId,
							null,
							camp,
							valor,
							true);
					resposta.put(camp.getCodi(), codiValor);
				}
			}
		}
		Map<String, ParellaCodiValorDto> respostaDto = new HashMap<String, ParellaCodiValorDto>();
		for (String clau: resposta.keySet()) {
			ParellaCodiValorDto parella = resposta.get(clau);
			ParellaCodiValorDto parellaDto = null;
			if (parella != null) {
				parellaDto = new ParellaCodiValorDto(
						parella.getCodi(),
						parella.getValor());
			}
			respostaDto.put(clau, parellaDto);
		}
		return respostaDto;
	}
	
	private String[] textsPerCampTipusRegistre(
			String taskId,
			String processInstanceId,
			CampDto camp,
			Object valorRegistre) {
		String[] texts = new String[camp.getRegistreMembres().size()];
		Map<String, Object> valorsAddicionalsConsulta = new HashMap<String, Object>();
		for (int j = 0; j < camp.getRegistreMembres().size(); j++) {
			if (j < Array.getLength(valorRegistre)) {
				valorsAddicionalsConsulta.put(
						camp.getRegistreMembres().get(j).getMembre().getCodi(),
						Array.get(valorRegistre, j));
			}
		}
		for (int j = 0; j < Array.getLength(valorRegistre); j++) {
			if (j == camp.getRegistreMembres().size())
				break;
			CampDto membreRegistre = camp.getRegistreMembres().get(j).getMembre();
			if (membreRegistre.getTipus().equals(CampTipusDto.SUGGEST) || membreRegistre.getTipus().equals(CampTipusDto.SELECCIO)) {
				ParellaCodiValorDto codiValor = obtenirValorDomini(
						taskId,
						processInstanceId,
						valorsAddicionalsConsulta,
						membreRegistre,
						Array.get(valorRegistre, j),
						true);
				ParellaCodiValorDto parellaDto = null;
				if (codiValor != null) {
					parellaDto = new ParellaCodiValorDto(
							codiValor.getCodi(),
							codiValor.getValor());
				}
				texts[j] = textPerCampDonatValorDomini(
						membreRegistre,
						Array.get(valorRegistre, j),
						parellaDto);
			} else {
				texts[j] = textPerCampDonatValorDomini(
						membreRegistre,
						Array.get(valorRegistre, j),
						null);
			}
		}
		return texts;
	}

	private Map<String, Object> textPerCamps(
			String taskId,
			String processInstanceId,
			Collection<CampDto> camps,
			Map<String, Object> valors,
			Map<String, ParellaCodiValorDto> valorsDomini,
			Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini) {
		Map<String, Object> resposta = new HashMap<String, Object>();
		if (valors != null) {
			for (String key: valors.keySet()) {
				boolean found = false;
				for (CampDto camp: camps) {
					if (camp.getCodi().equals(key)) {
						if (camp.getTipus().equals(CampTipusDto.REGISTRE)) {
							Object valor = valors.get(key);
							if (valor != null && valor instanceof Object[]) {
								List<String[]> grid = new ArrayList<String[]>();
								if (camp.isMultiple()) {
									for (int i = 0; i < Array.getLength(valor); i++) {
										Object valorRegistre = Array.get(valor, i);
										if (valorRegistre != null) {
											grid.add(textsPerCampTipusRegistre(
													taskId,
													processInstanceId,
													camp,
													valorRegistre));
										}
									}
								} else {
									grid.add(textsPerCampTipusRegistre(
											taskId,
											processInstanceId,
											camp,
											valor));
								}
								resposta.put(key, grid);
							} else {
								resposta.put(key, null);
							}
						} else if (camp.isMultiple()) {
							Object valor = valors.get(key);
							if (valor != null) {
								if (valor instanceof Object[]) {
									List<String> texts = new ArrayList<String>();
									for (int i = 0; i < Array.getLength(valor); i++) {
										String t = null;
										if (camp.getTipus().equals(CampTipusDto.SUGGEST) || camp.getTipus().equals(CampTipusDto.SELECCIO)) {
											if (valorsMultiplesDomini.get(key).size() > i)
												t = textPerCampDonatValorDomini(camp, Array.get(valor, i), valorsMultiplesDomini.get(key).get(i));
											else
												t = "!" + Array.get(valor, i) + "!";
										} else {
											t = textPerCampDonatValorDomini(camp, Array.get(valor, i), null);
										}
										if (t != null)
											texts.add(t);
									}
									resposta.put(key, texts);
								} else {
									logger.warn("No s'ha pogut convertir el camp " + camp.getCodi() + "a text: El camp és múltiple però el seu valor no és un array (" + valor.getClass().getName() + ")");
								}
							} else {
								resposta.put(key, null);
							}
						} else {
							if (camp.getTipus().equals(CampTipusDto.TERMINI)) {
								valors.put(key, valors.get(key) == null ? null : new ModelMapper().map(valors.get(key), TerminiDto.class));
							}
							resposta.put(
									key,
									textPerCampDonatValorDomini(camp, valors.get(key), valorsDomini.get(key)));
						}
						found = true;
						break;
					}
				}
				if (!found) {
					// Si no hi ha cap camp associat el mostra com un String
					Object valor = valors.get(key);
					if (valor != null)
						resposta.put(key, valor.toString());
				}
			}
		}
		return resposta;
	}
	
	private String textPerCampDonatValorDomini(
			CampDto camp,
			Object valor,
			ParellaCodiValorDto valorDomini) {
		if (valor == null) return null;
		if (camp == null)
			return valor.toString();
		else
			return CampDto.getComText(
					camp,
					valor,
					(valorDomini != null) ? (String)valorDomini.getValor() : null);
	}

	private Map<String, List<ParellaCodiValorDto>> obtenirValorsMultiplesDomini(
			String taskId,
			String processInstanceId,
			Collection<CampDto> camps,
			Map<String, Object> valors) throws DominiException {
		Map<String, List<ParellaCodiValorDto>> resposta = new HashMap<String, List<ParellaCodiValorDto>>();
		if (valors != null) {
			for (CampDto camp: camps) {
				if (camp.isMultiple()) {
					Object valor = valors.get(camp.getCodi());
					if (valor instanceof Object[]) {
						List<ParellaCodiValorDto> codisValor = new ArrayList<ParellaCodiValorDto>();
						for (int i = 0; i < Array.getLength(valor); i++) {
							ParellaCodiValorDto codiValor = obtenirValorDomini(
									taskId,
									processInstanceId,
									null,
									camp,
									Array.get(valor, i),
									true);
							ParellaCodiValorDto parellaDto = null;
							if (codiValor != null) {
								parellaDto = new ParellaCodiValorDto(
									codiValor.getCodi(),
									codiValor.getValor());
							}
							codisValor.add(parellaDto);
						}
						resposta.put(camp.getCodi(), codisValor);
					}
				}
			}
		}
		return resposta;
	}
	
	public ExpedientTascaDto toExpedientTascaDto(
			JbpmTask task,
			Map<String, Object> varsCommand,
			boolean ambVariables,
			boolean ambTexts,
			boolean validada,
			boolean documentsComplet,
			boolean signaturesComplet) {
		
		Tasca tasca = tascaRepository.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
				task.getProcessDefinitionId());
		ExpedientTascaDto dto = new ExpedientTascaDto();
		dto.setId(task.getId());
		dto.setDescription(task.getDescription());
		dto.setAssignee(task.getAssignee());
		dto.setPooledActors(task.getPooledActors());
		dto.setCreateTime(task.getCreateTime());
//		dto.setPersonesMap(getPersonesMap(task.getAssignee(), task.getPooledActors()));
		dto.setStartTime(task.getStartTime());
		dto.setEndTime(task.getEndTime());
		dto.setDueDate(task.getDueDate());
		dto.setPriority(task.getPriority());
		dto.setOberta(task.isOpen());
		dto.setCompleted(task.isCompleted());
		dto.setCancelled(task.isCancelled());
		dto.setSuspended(task.isSuspended());
		dto.setProcessInstanceId(task.getProcessInstanceId());
		dto.setAgafada("true".equals(task.getFieldFromDescription(TascaService.TASKDESC_CAMP_AGAFADA)));
		
		ExpedientDto expedientDto = new ModelMapper().map(expedientHelper.findAmbProcessInstanceId(task.getProcessInstanceId()), ExpedientDto.class);
		dto.setExpedient(expedientDto);
		
		dto.setOutcomes(jbpmHelper.findTaskInstanceOutcomes(task.getId()));
		DelegationInfo delegationInfo = (DelegationInfo)jbpmHelper.getTaskInstanceVariable(
				task.getId(),
				TascaService.VAR_TASCA_DELEGACIO);
		if (delegationInfo != null) {
			boolean original = task.getId().equals(delegationInfo.getSourceTaskId());
			dto.setDelegada(true);
			dto.setDelegacioOriginal(original);
			dto.setDelegacioData(delegationInfo.getStart());
			dto.setDelegacioSupervisada(delegationInfo.isSupervised());
			dto.setDelegacioComentari(delegationInfo.getComment());
			JbpmTask tascaDelegacio = jbpmHelper.getTaskById(delegationInfo.getSourceTaskId());
			dto.setDelegacioPersona(personaHelper.findAmbCodiPlugin(tascaDelegacio.getAssignee()));
		}
		if (task.isCacheActiu()) {
			dto.setNom(task.getFieldFromDescription("titol"));
		} else {
			if (tasca != null)
				dto.setNom(tasca.getNom());
			else
				dto.setNom(task.getName());
		}
		if (tasca != null) {
			dto.setTascaId(tasca.getId());
			dto.setMissatgeInfo(tasca.getMissatgeInfo());
			dto.setMissatgeWarn(tasca.getMissatgeWarn());
			dto.setDelegable(tasca.getExpressioDelegacio() != null);
			dto.setTipus(new ModelMapper().map(tasca.getTipus(), ExpedientTascaDto.TipusTasca.class));
			dto.setJbpmName(tasca.getJbpmName());
			dto.setDefinicioProces(toDefinicioProcesDto(tasca.getDefinicioProces()));			
			dto.setValidacions(toValidacionsDto(tasca.getValidacions()));
			dto.setRecursForm(tasca.getRecursForm());
			dto.setFormExtern(tasca.getFormExtern());
			dto.setValidada(validada);
			dto.setDocumentsComplet(documentsComplet);
			dto.setSignaturesComplet(signaturesComplet);			
			
			List<CampTascaDto> campsTasca = toCampsTascaDto(campTascaRepository.findAmbTascaOrdenats(tasca.getId()));
			dto.setCamps(campsTasca);
			List<DocumentTascaDto> documentsTasca = toDocumentTascaDto(documentTascaRepository.findAmbTascaOrdenats(tasca.getId()));
			dto.setDocuments(documentsTasca);
			List<FirmaTascaDto> signaturesTasca = toFirmaTascaDto(firmaTascaRepository.findAmbTascaOrdenats(tasca.getId()));
			dto.setSignatures(signaturesTasca);
			if (ambVariables) {
				Map<String, Object> valors = jbpmHelper.getTaskInstanceVariables(task.getId());

				dto.setVarsDocuments(
						obtenirVarsDocumentsTasca(
								task.getId(),
								task.getProcessInstanceId(),
								documentsTasca));
				dto.setVarsDocumentsPerSignar(
						obtenirVarsDocumentsPerSignarTasca(
								task.getId(),
								task.getProcessInstanceId(),
								signaturesTasca));
				filtrarVariablesTasca(valors);
				if (varsCommand != null)
					valors.putAll(varsCommand);
				List<CampDto> camps = new ArrayList<CampDto>();				
				for (CampTascaDto campTasca: campsTasca)
					camps.add(campTasca.getCamp());
				if (ambTexts) {
					Map<String, ParellaCodiValorDto> valorsDomini = obtenirValorsDomini(
							task.getId(),
							null,
							camps,
							valors);
					dto.setValorsDomini(valorsDomini);
					Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini = obtenirValorsMultiplesDomini(
							task.getId(),
							null,
							camps,
							valors);
					dto.setValorsMultiplesDomini(valorsMultiplesDomini);
					getServiceUtils().revisarVariablesJbpm(valors);
					dto.setVarsComText(textPerCamps(task.getId(), null, camps, valors, valorsDomini, valorsMultiplesDomini));
				}
				dto.setVariables(valors);
			}
		}
		return dto;
	}
	
	public static ReassignacioDto toReassignacioDto(Reassignacio reassignacio) {
		return new ModelMapper().map(reassignacio, ReassignacioDto.class);
	}
	
	public static TerminiIniciatDto toTerminiIniciatDto(TerminiIniciat terminiIniciat) {
		TerminiIniciatDto terminiIniciatDto = new TerminiIniciatDto();
		terminiIniciatDto.setAnys(terminiIniciat.getAnys());
		terminiIniciatDto.setDataAturada(terminiIniciat.getDataAturada());
		terminiIniciatDto.setDataCancelacio(terminiIniciat.getDataCancelacio());
		terminiIniciatDto.setDataCompletat(terminiIniciat.getDataCompletat());
		terminiIniciatDto.setDataFi(terminiIniciat.getDataFi());
		terminiIniciatDto.setDataFiProrroga(terminiIniciat.getDataFiProrroga());
		terminiIniciatDto.setDataInici(terminiIniciat.getDataInici());
		terminiIniciatDto.setDies(terminiIniciat.getDies());
		terminiIniciatDto.setDiesAturat(terminiIniciat.getDiesAturat());
		terminiIniciatDto.setId(terminiIniciat.getId());
		terminiIniciatDto.setMesos(terminiIniciat.getMesos());
		
		TerminiDto terminiDto = new ModelMapper().map(terminiIniciat.getTermini(), TerminiDto.class);
		terminiIniciatDto.setTermini(terminiDto);
		return terminiIniciatDto;
	}
	
	public static List<ReassignacioDto> toLlistatReassignacioDto(List<Reassignacio> llistat) {
		List<ReassignacioDto> val = new ArrayList<ReassignacioDto>();
		for(Reassignacio validacio : llistat) {
			val.add(toReassignacioDto(validacio));
		}
		return val;
	}

	private List<FirmaTascaDto> toFirmaTascaDto(List<FirmaTasca> findAmbTascaOrdenats) {
		List<FirmaTascaDto> val = new ArrayList<FirmaTascaDto>();
		for(FirmaTasca validacio : findAmbTascaOrdenats) {
//			val.add(new ModelMapper().map(validacio, FirmaTascaDto.class));
		}
		return val;
	}

	private List<ValidacioDto> toValidacionsDto(List<Validacio> validacions) {
		List<ValidacioDto> val = new ArrayList<ValidacioDto>();
		for(Validacio validacio : validacions) {
			val.add(new ModelMapper().map(validacio, ValidacioDto.class));
		}
		return val;
	}

	private List<DocumentTascaDto> toDocumentTascaDto(List<DocumentTasca> findAmbTascaOrdenats) {
		List<DocumentTascaDto> val = new ArrayList<DocumentTascaDto>();
		for(DocumentTasca validacio : findAmbTascaOrdenats) {
			val.add(new ModelMapper().map(validacio, DocumentTascaDto.class));
		}
		return val;
	}

	public List<CampTascaDto> toCampsTascaDto(List<CampTasca> findAmbTascaOrdenats) {
		List<CampTascaDto> res = new ArrayList<CampTascaDto>();
		for(CampTasca val : findAmbTascaOrdenats) {
			CampTascaDto camp = new CampTascaDto();
			camp.setId(val.getId());
			camp.setOrder(val.getOrder());
			camp.setReadOnly(val.isReadOnly());
//			camp.setTasca(new ModelMapper().map(val.getTasca(), TascaDto.class));
//			camp.setCamp(new ModelMapper().map(val.getCamp(), CampDto.class));			
			camp.setCamp(toCampDto(val.getCamp()));
			res.add(camp);
		}
		return res;
	}

	public CampDto toCampDto(Camp val) {
		CampDto campDto = new CampDto();
		if (val != null) {
			if (val.getDomini() != null) {
				EntornDto entornDto = new ModelMapper().map(val.getDomini().getEntorn(), EntornDto.class);
				DominiDto dom = new DominiDto(val.getDomini().getCodi(),val.getDomini().getNom(),entornDto);
				dom.setId(val.getDomini().getId());
				campDto.setDomini(dom);
				campDto.getDomini().setId(dom.getId());
				campDto.setDominiId(String.valueOf(dom.getId()));
				campDto.setDefinicioProces(new ModelMapper().map(val.getDefinicioProces(), DefinicioProcesDto.class));
			}

			campDto.setCodi(val.getCodi());
			List<CampRegistreDto> listRegistreMembres = new ArrayList<CampRegistreDto>();
			for(CampRegistre registreMembre : val.getRegistreMembres()) {
				CampRegistreDto campRegistreDto = new CampRegistreDto();
				campRegistreDto.setMembre(toCampDto(registreMembre.getMembre()));
				listRegistreMembres.add(campRegistreDto);
			}
			campDto.setRegistreMembres(listRegistreMembres);
			campDto.setTipus(new ModelMapper().map(val.getTipus(), CampTipusDto.class));
		}
		return campDto;
	}

	private DefinicioProcesDto toDefinicioProcesDto(DefinicioProces definicioProces) {
		DefinicioProcesDto def = new ModelMapper().map(definicioProces, DefinicioProcesDto.class);
		def.setId(definicioProces.getId());
		return def;
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
		dto.setTipus(new ModelMapper().map(tasca.getTipus(), ExpedientTascaDto.TipusTasca.class));
		dto.setJbpmName(tasca.getJbpmName());
		dto.setValidada(false);
		dto.setDocumentsComplet(false);
		dto.setTascaId(tasca.getId());
		dto.setSignaturesComplet(false);
		dto.setDefinicioProces(toDefinicioProcesDto(tasca.getDefinicioProces()));
		dto.setOutcomes(jbpmHelper.findStartTaskOutcomes(jbpmId, startTaskName));
		dto.setValidacions(toValidacionsDto(tasca.getValidacions()));
		dto.setFormExtern(tasca.getFormExtern());
		//Camps
		List<CampTasca> campsTasca = tasca.getCamps();
		for (CampTasca campTasca: campsTasca) {
			if (campTasca.getCamp().getTipus().equals(CampTipusDto.REGISTRE)) {
				campTasca.getCamp().getRegistreMembres().size();
			}
		}
		dto.setCamps(toCampsTascaDto(campsTasca));
		//Documents
		List<DocumentTasca> documentsTasca = tasca.getDocuments();		
		for (DocumentTasca document: documentsTasca)
			Hibernate.initialize(document.getDocument());
		
		dto.setDocuments(toDocumentTascaDto(documentsTasca));
		//Configuració de valors
		if (valors != null) {
			List<CampDto> camps = new ArrayList<CampDto>();
			for (CampTascaDto campTasca: dto.getCamps())
				camps.add(campTasca.getCamp());
			
			dto.setValorsDomini(obtenirValorsDomini(
					null,
					null,
					camps,
					valors));
			dto.setValorsMultiplesDomini(obtenirValorsMultiplesDomini(
					null,
					null,
					camps,
					valors));
			dto.setVarsComText(
					textPerCamps(null, null, camps, valors, dto.getValorsDomini(), dto.getValorsMultiplesDomini()));
		}
		return dto;
	}

	private Map<String, DocumentDto> obtenirVarsDocumentsPerSignarTasca(
			String taskId,
			String processInstanceId,
			List<FirmaTascaDto> signatures) {
		Map<String, DocumentDto> resposta = new HashMap<String, DocumentDto>();
		if (signatures != null) {
			for (FirmaTascaDto signatura: signatures) {
				DocumentDto dto = documentHelper.getDocumentSenseContingut(
						taskId,
						processInstanceId,
						signatura.getDocument().getCodi(),
						true,
						false);
				if (dto != null)
					resposta.put(
							signatura.getDocument().getCodi(),
							dto);
			}
		}
		return resposta;
	}
	
	public ExpedientDto findAmbProcessInstanceId(String processInstanceId) {
		List<Expedient> expedients = expedientRepository.findByProcessInstanceId(processInstanceId);
		if (expedients.size() > 0) {
			return new ModelMapper().map(expedients.get(0), ExpedientDto.class);
		} else {
			ExpedientDto expedientIniciant = new ModelMapper().map(ExpedientIniciantDto.getExpedient(), ExpedientDto.class);
			
			if (expedientIniciant != null && expedientIniciant.getProcessInstanceId().equals(processInstanceId))
				return expedientIniciant;
		}
		return null;
	}
	
	public static EntornDto toEntornDto(Entorn entorn) {
		EntornDto ent = new EntornDto(entorn.getId(), entorn.getCodi(), entorn.getNom());
		return ent;		
	}
	
	private ParellaCodiValorDto obtenirValorDomini(
			String taskId,
			String processInstanceId,
			Map<String, Object> valorsAddicionals,
			CampDto camp,
			Object valor,
			boolean actualitzarJbpm) throws DominiException {
		if (valor == null)
			return null;
		if (valor instanceof DominiCodiDescripcio) {
			return new ParellaCodiValorDto(
					((DominiCodiDescripcio)valor).getCodi(),
					((DominiCodiDescripcio)valor).getDescripcio());
		}
		ParellaCodiValorDto resposta = null;
		CampTipusDto tipus = camp.getTipus();
		if (tipus.equals(CampTipusDto.SELECCIO) || tipus.equals(CampTipusDto.SUGGEST)) {
			if (camp.getDomini() != null || camp.isDominiIntern()) {
				Long dominiId = (long) 0;
				if (camp.getDomini() != null){
					DominiDto domini = camp.getDomini();
					dominiId = domini.getId();
				}				
				try {
					Map<String, Object> paramsConsulta = getParamsConsulta(
							taskId,
							processInstanceId,
							camp,
							valorsAddicionals);
					List<FilaResultat> resultat = dominiDao.consultar(
							camp.getDefinicioProces().getEntorn().getId(),
							dominiId,
							camp.getDominiId(),
							paramsConsulta);
					String columnaCodi = camp.getDominiCampValor();
					String columnaValor = camp.getDominiCampText();
					Iterator<FilaResultat> it = resultat.iterator();
					while (it.hasNext()) {
						FilaResultat fr = it.next();
						for (ParellaCodiValor parellaCodi: fr.getColumnes()) {
							if (parellaCodi.getCodi().equals(columnaCodi) && parellaCodi.getValor().toString().equals(valor)) {
								for (ParellaCodiValor parellaValor: fr.getColumnes()) {
									if (parellaValor.getCodi().equals(columnaValor)) {
										ParellaCodiValorDto codiValor = new ParellaCodiValorDto(
												parellaCodi.getValor().toString(),
												parellaValor.getValor());
										resposta = codiValor;
										break;
									}
								}
								break;
							}
						}
					}
				} catch (Exception ex) {
					//throw new DominiException("No s'ha pogut consultar el domini", ex);
					logger.error("No s'ha pogut consultar el domini", ex);
				}
			} else if (camp.getEnumeracio() != null) {
				EnumeracioDto enumeracio = camp.getEnumeracio();
				for (ParellaCodiValor parella: enumeracioValorsHelper.getLlistaValors(enumeracio.getId())) {
					// Per a evitar problemes amb caràcters estranys al codi (EXSANCI)
					String codiBo = null;
					if (parella.getCodi() != null)
						codiBo = parella.getCodi().replaceAll("\\p{Cntrl}", "").trim();
					String valorBo = valor.toString().replaceAll("\\p{Cntrl}", "").trim();
					if (valorBo.equals(codiBo)) {
						resposta = new ParellaCodiValorDto(
								parella.getCodi(),
								parella.getValor());
					}
				}
			} else if (camp.getConsulta() != null) {
				ConsultaDto consulta = camp.getConsulta();
				List<ExpedientConsultaDissenyDto> dadesExpedients = expedientHelper.findAmbEntornConsultaDisseny(
						consulta.getEntorn().getId(),
						consulta.getId(),
						new HashMap<String, Object>(),
						null,
						true);
				
				Iterator<ExpedientConsultaDissenyDto> it = dadesExpedients.iterator();
				while(it.hasNext()){
					ExpedientConsultaDissenyDto exp = it.next();
					DadaIndexadaDto valorDto = exp.getDadesExpedient().get(camp.getConsultaCampValor());
					if(valorDto == null){
						valorDto = exp.getDadesExpedient().get(consulta.getExpedientTipus().getJbpmProcessDefinitionKey()+"/"+camp.getConsultaCampValor());
					}
					if(valorDto != null){
						if(valorDto.getValor().toString().equals(valor)){
							DadaIndexadaDto textDto = exp.getDadesExpedient().get(camp.getConsultaCampText());
							if(textDto == null){
								textDto = exp.getDadesExpedient().get(consulta.getExpedientTipus().getJbpmProcessDefinitionKey()+"/"+camp.getConsultaCampText());
							}
							resposta = new ParellaCodiValorDto(
									valorDto.getValorMostrar(),
									textDto.getValorMostrar()
									);
							break;
						}
					}
				}
			}
		}
		return resposta;
	}

	private Map<String, Object> getParamsConsulta(
			String taskId,
			String processInstanceId,
			CampDto camp,
			Map<String, Object> valorsAddicionals) {
		String dominiParams = camp.getDominiParams();
		if (dominiParams == null || dominiParams.length() == 0)
			return null;
		Map<String, Object> params = new HashMap<String, Object>();
		String[] pairs = dominiParams.split(";");
		for (String pair: pairs) {
			String[] parts = pair.split(":");
			String paramCodi = parts[0];
			String campCodi = parts[1];
			Object value = null;
			if (campCodi.startsWith("@")) {
				value = (String)GlobalProperties.getInstance().get(campCodi.substring(1));
			} else if (campCodi.startsWith("#{")) {
				if (processInstanceId != null) {
					value = jbpmHelper.evaluateExpression(taskId, processInstanceId, campCodi, null);
				} else if (taskId != null) {
					JbpmTask task = jbpmHelper.getTaskById(taskId);
					value = jbpmHelper.evaluateExpression(taskId, task.getProcessInstanceId(), campCodi, null);
				} else if (campCodi.startsWith("#{'")) {
					int index = campCodi.lastIndexOf("'");
					if (index != -1 && index > 2)
						value = campCodi.substring(3, campCodi.lastIndexOf("'"));
				}
			} else {
				if (valorsAddicionals != null && valorsAddicionals.size() > 0)
					value = valorsAddicionals.get(campCodi);
				if (value == null && taskId != null)
					value = getServiceUtils().getVariableJbpmTascaValor(taskId, campCodi);
				if (value == null && processInstanceId != null)
					value = getServiceUtils().getVariableJbpmProcesValor(processInstanceId, campCodi);
			}
			if (value != null)
				params.put(paramCodi, value);
		}
		return params;
	}

	private Map<String, DocumentDto> obtenirVarsDocumentsTasca(
			String taskId,
			String processInstanceId,
			List<DocumentTascaDto> documents) {
		Map<String, DocumentDto> resposta = new HashMap<String, DocumentDto>();
		for (DocumentTascaDto document: documents) {
			DocumentDto dto = documentHelper.getDocumentSenseContingut(
					taskId,
					processInstanceId,
					document.getDocument().getCodi(),
					false,
					false);
			if (dto != null)
				resposta.put(document.getDocument().getCodi(), dto);
		}
		return resposta;
	}

	public void revisarDadesExpedientAmbValorsEnumeracionsODominis(
			Map<String, DadaIndexadaDto> dadesExpedient,
			List<CampDto> campsInforme) {
		for (CampDto camp: campsInforme) {
			if (!camp.isDominiCacheText() && (CampTipusDto.SELECCIO.equals(camp.getTipus()) || CampTipusDto.SUGGEST.equals(camp.getTipus()))) {
				if (camp.getEnumeracio() != null) {
					String dadaIndexadaClau = camp.getDefinicioProces().getJbpmKey() + "/" + camp.getCodi();
					DadaIndexadaDto dadaIndexada = dadesExpedient.get(dadaIndexadaClau);
					if (dadaIndexada != null) {
						String text = getCampText(
								null,
								null,
								camp,
								dadaIndexada.getValorIndex());
						dadaIndexada.setValorMostrar(text);
					}
				}
			}
		}
	}

	private Map<String, DocumentDto> obtenirVarsDocumentsProces(
			String processInstanceId,
			List<DocumentDto> documents,
			Map<String, Object> valors) {
		Map<String, DocumentDto> resposta = new HashMap<String, DocumentDto>();
		if (valors != null) {
			// Afegeix els documents
			for (DocumentDto document: documents) {
				DocumentDto dto = documentHelper.getDocumentSenseContingut(
						null,
						processInstanceId,
						document.getCodi(),
						false,
						true);
				if (dto != null)
					resposta.put(
							document.getCodi(),
							dto);
			}
			// Afegeix els adjunts
			for (String var: valors.keySet()) {
				if (var.startsWith(DocumentHelper.PREFIX_ADJUNT)) {
					Long documentStoreId = (Long)valors.get(var);
					resposta.put(
							var.substring(DocumentHelper.PREFIX_ADJUNT.length()),
							documentHelper.getDocumentSenseContingut(documentStoreId));
				}
			}
		}
		return resposta;
	}

	public InstanciaProcesDto toInstanciaProcesDto(String processInstanceId , boolean ambImatgeProces, boolean ambVariables, boolean ambDocuments) {
		return toInstanciaProcesDto(processInstanceId , ambImatgeProces, ambVariables, ambDocuments, null, null);
	}
	
	public InstanciaProcesDto toInstanciaProcesDto(String processInstanceId , boolean ambImatgeProces, boolean ambVariables, boolean ambDocuments, String varRegistre, Object[] valorsRegistre) {
		JbpmProcessInstance pi = jbpmHelper.getProcessInstance(processInstanceId);
		JbpmProcessDefinition jpd = jbpmHelper.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		DefinicioProcesDto definicioProces = toDefinicioProcesDto(definicioProcesRepository.findByJbpmId(jpd.getId()));
		InstanciaProcesDto dto = new InstanciaProcesDto();
		dto.setId(processInstanceId);
		dto.setInstanciaProcesPareId(pi.getParentProcessInstanceId());
		JbpmProcessInstance jpi = jbpmHelper.getRootProcessInstance(processInstanceId);
		dto.setExpedient(new ModelMapper().map(expedientHelper.findAmbProcessInstanceId(jpi.getId()), ExpedientDto.class));
		dto.setDefinicioProces(definicioProces);
		if (pi.getDescription() != null && pi.getDescription().length() > 0)
			dto.setTitol(pi.getDescription());
		dto.setDataInici(pi.getStart());
		dto.setDataFi(pi.getEnd());
		if (ambImatgeProces) {
			Set<String> resourceNames = jbpmHelper.getResourceNames(jpd.getId());
			dto.setImatgeDisponible(resourceNames.contains("processimage.jpg"));
		}
		
		List<DocumentDto> documents = new ArrayList<DocumentDto>();
		if (ambDocuments) {
			Map<String, Object> valors = jbpmHelper.getProcessInstanceVariables(processInstanceId);
			documents = documentHelper.findAmbDefinicioProces(definicioProces.getId());
			dto.setDocuments(documents);
			dto.setVarsDocuments(obtenirVarsDocumentsProces(
					processInstanceId,
					documents,
					valors));
		}

		dto.setAgrupacions(toCampsAgrupacio(campAgrupacioRepository.findAmbDefinicioProcesOrdenats(definicioProces.getId())));
		if (ambVariables) {
			Map<String, Object> valors = jbpmHelper.getProcessInstanceVariables(processInstanceId);
			if (valors == null)
				valors = new HashMap<String, Object>();
			if (varRegistre != null) 
				valors.put(varRegistre, valorsRegistre);
			filtrarVariablesTasca(valors);
			getServiceUtils().revisarVariablesJbpm(valors);
			dto.setVariables(valors);
		}
		return dto;
	}
	
	private List<CampAgrupacioDto> toCampsAgrupacio(List<CampAgrupacio> camps) {
		List<CampAgrupacioDto> agrupacionsDto = new ArrayList<CampAgrupacioDto>();
		for (CampAgrupacio camp : camps) {
			agrupacionsDto.add(toCampAgrupacio(camp));
		}
		return agrupacionsDto;
	}

	private CampAgrupacioDto toCampAgrupacio(CampAgrupacio camp) {
		CampAgrupacioDto campAgrupacioDto = new CampAgrupacioDto(camp.getId(), camp.getCodi(), camp.getNom(), camp.getDescripcio(), camp.getOrdre());
		return campAgrupacioDto;
	}

	private ServiceUtils getServiceUtils() {
		if (serviceUtils == null) {
			serviceUtils = new ServiceUtils();
		}
		return serviceUtils;
	}

	public String getCampText(
			String taskId,
			String processInstanceId,
			CampDto camp,
			Object valor) {
		if (valor == null) return null;
		if (camp == null) {
			return valor.toString();
		} else {
			String textDomini = null;
			if (	camp.getTipus().equals(CampTipusDto.SELECCIO) ||
					camp.getTipus().equals(CampTipusDto.SUGGEST)) {
				if (valor instanceof DominiCodiDescripcio) {
					textDomini = ((DominiCodiDescripcio)valor).getDescripcio();
				} else {
					ParellaCodiValorDto resultat = obtenirValorDomini(
							taskId,
							processInstanceId,
							null,
							camp,
							valor,
							false);
					if (resultat != null && resultat.getValor() != null)
						textDomini = resultat.getValor().toString();
				}
			}
			return CampDto.getComText(
					camp,
					valor,
					textDomini);
		}
	}
	
	private IniciadorTipusDto toIniciadorTipus(IniciadorTipus iniciadorTipus) {
		if (iniciadorTipus == null)
			return null;
		IniciadorTipusDto intern = null;
		if (iniciadorTipus.equals(IniciadorTipus.INTERN))
			intern = IniciadorTipusDto.INTERN;
		else
			intern =  IniciadorTipusDto.SISTRA;
		
		return intern;
	}
	
	public ExpedientDto toExpedientDto(Expedient expedient, boolean starting) {
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
		dto.setIniciadorTipus(toIniciadorTipus(expedient.getIniciadorTipus()));
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
		dto.setTipus(new ModelMapper().map(expedient.getTipus(), ExpedientTipusDto.class));
		dto.setEntorn(toEntornDto(expedient.getEntorn()));
		if (expedient.getEstat() != null)
			dto.setEstat(new ModelMapper().map(expedient.getEstat(), EstatDto.class));
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
		if (!starting) {
			JbpmProcessInstance processInstance = jbpmHelper.getProcessInstance(expedient.getProcessInstanceId());
			dto.setDataFi(processInstance.getEnd());
		}
		for (Expedient relacionat: expedient.getRelacionsOrigen()) {
			ExpedientDto relacionatDto = new ExpedientDto();
			relacionatDto.setId(relacionat.getId());
			relacionatDto.setTitol(relacionat.getTitol());
			relacionatDto.setNumero(relacionat.getNumero());
			relacionatDto.setDataInici(relacionat.getDataInici());
			relacionatDto.setTipus(new ModelMapper().map(relacionat.getTipus(), ExpedientTipusDto.class));
			if (relacionat.getEstat() != null)
				relacionatDto.setEstat(new ModelMapper().map(relacionat.getEstat(), EstatDto.class));
			relacionatDto.setProcessInstanceId(relacionat.getProcessInstanceId());
			dto.addExpedientRelacionat(relacionatDto);
		}
		return dto;
	}

	private void filtrarVariablesTasca(Map<String, Object> variables) {
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
	
	public DefinicioProcesDto toDto(
			DefinicioProces definicioProces,
			boolean ambTascaInicial) {
		DefinicioProcesDto dto = toDefinicioProcesDto(definicioProces);
		JbpmProcessDefinition jpd = jbpmHelper.getProcessDefinition(definicioProces.getJbpmId());
		if (jpd != null)
			dto.setJbpmName(jpd.getName());
		else
			dto.setJbpmName("[" + definicioProces.getJbpmKey() + "]");

		List<DefinicioProces> mateixaKeyIEntorn = definicioProcesRepository.findByEntornAndJbpmKey(
				definicioProces.getEntorn(),
				definicioProces.getJbpmKey());
		dto.setIdsWithSameKey(new Long[mateixaKeyIEntorn.size()]);
		dto.setIdsMostrarWithSameKey(new String[mateixaKeyIEntorn.size()]);
		dto.setJbpmIdsWithSameKey(new String[mateixaKeyIEntorn.size()]);
		for (int i = 0; i < mateixaKeyIEntorn.size(); i++) {
			dto.getIdsWithSameKey()[i] = mateixaKeyIEntorn.get(i).getId();
			dto.getIdsMostrarWithSameKey()[i] = mateixaKeyIEntorn.get(i).getIdPerMostrar();
			dto.getJbpmIdsWithSameKey()[i] = mateixaKeyIEntorn.get(i).getJbpmId();
		}
		if (ambTascaInicial) {
			dto.setHasStartTask(hasStartTask(definicioProces));
			dto.setStartTaskName(jbpmHelper.getStartTaskName(definicioProces.getJbpmId()));
			dto.setHasStartTaskWithSameKey(new Boolean[mateixaKeyIEntorn.size()]);
			for (int i = 0; i < mateixaKeyIEntorn.size(); i++) {
				dto.getHasStartTaskWithSameKey()[i] = new Boolean(
						hasStartTask(mateixaKeyIEntorn.get(i)));
			}
		}
		return dto;
	}
	private boolean hasStartTask(DefinicioProces definicioProces) {
		Long definicioProcesId = definicioProces.getId();
		Boolean result = hasStartTask.get(definicioProcesId);
		if (result == null) {
			result = new Boolean(false);
			String startTaskName = jbpmHelper.getStartTaskName(
					definicioProces.getJbpmId());
			if (startTaskName != null) {
				Tasca tasca = tascaRepository.findAmbActivityNameIProcessDefinitionId(
						startTaskName,
						definicioProces.getJbpmId());
				if (tasca != null) {
					List<CampTasca> camps = campTascaRepository.findAmbTascaOrdenats(tasca.getId());
					result = new Boolean(camps.size() > 0);
				}
			}
			hasStartTask.put(definicioProcesId, result);
		}
		return result.booleanValue();
	}
	
	private static final Log logger = LogFactory.getLog(DtoConverter.class);
}
