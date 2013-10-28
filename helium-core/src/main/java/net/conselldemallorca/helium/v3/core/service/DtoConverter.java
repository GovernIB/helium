/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
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
import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.exception.DominiException;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.jbpm3.handlers.BasicActionHandler;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDto;
import net.conselldemallorca.helium.v3.core.api.service.PermissionService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.v3.core.helper.EnumeracioValorsHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.v3.core.helper.LuceneHelper;
import net.conselldemallorca.helium.v3.core.helper.PersonaHelper;
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
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private PersonaRepository personaRepository;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private PluginPersonaDao pluginPersonaDao;
	@Resource
	private EnumeracioValorsHelper enumeracioValorsHelper;
	@Resource
	private EnumeracioValorsRepository enumeracioValorsRepository;
	@Resource
	private DominiDao dominiDao;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource(name="serviceUtilsV3")
	private ServiceUtilsV3 serviceUtils;
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
	
	public TascaDto toTascaDto(
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
		TascaDto dto = new TascaDto();
//		dto.setId(task.getId());
//		dto.setDescription(task.getDescription());
//		dto.setAssignee(task.getAssignee());
//		dto.setPooledActors(task.getPooledActors());
//		dto.setCreateTime(task.getCreateTime());
//		dto.setPersonesMap(getPersonesMap(task.getAssignee(), task.getPooledActors()));
//		dto.setStartTime(task.getStartTime());
//		dto.setEndTime(task.getEndTime());
//		dto.setDueDate(task.getDueDate());
//		dto.setPriority(task.getPriority());
//		dto.setOpen(task.isOpen());
//		dto.setCompleted(task.isCompleted());
//		dto.setCancelled(task.isCancelled());
//		dto.setSuspended(task.isSuspended());
//		dto.setProcessInstanceId(task.getProcessInstanceId());
//		dto.setAgafada("true".equals(task.getFieldFromDescription(TascaService.TASKDESC_CAMP_AGAFADA)));
//		
//		ExpedientDto expedientDto = new ModelMapper().map(expedientHelper.findAmbProcessInstanceId(task.getProcessInstanceId()).getId(), ExpedientDto.class);
//		dto.setExpedient(expedientDto);
//		
//		dto.setOutcomes(jbpmHelper.findTaskInstanceOutcomes(task.getId()));
//		DelegationInfo delegationInfo = (DelegationInfo)jbpmHelper.getTaskInstanceVariable(
//				task.getId(),
//				TascaService.VAR_TASCA_DELEGACIO);
//		if (delegationInfo != null) {
//			boolean original = task.getId().equals(delegationInfo.getSourceTaskId());
//			dto.setDelegada(true);
//			dto.setDelegacioOriginal(original);
//			dto.setDelegacioData(delegationInfo.getStart());
//			dto.setDelegacioSupervisada(delegationInfo.isSupervised());
//			dto.setDelegacioComentari(delegationInfo.getComment());
//			JbpmTask tascaDelegacio = jbpmHelper.getTaskById(delegationInfo.getSourceTaskId());
//			dto.setDelegacioPersona(personaHelper.findAmbCodiPlugin(tascaDelegacio.getAssignee()));
//		}
		if (task.isCacheActiu()) {
			dto.setNom(task.getFieldFromDescription("titol"));
		} else {
			if (tasca != null)
				dto.setNom(tasca.getNom());
			else
				dto.setNom(task.getName());
		}
//		if (tasca != null) {
//			dto.setTascaId(tasca.getId());
//			dto.setMissatgeInfo(tasca.getMissatgeInfo());
//			dto.setMissatgeWarn(tasca.getMissatgeWarn());
//			dto.setDelegable(tasca.getExpressioDelegacio() != null);			
//			dto.setTipus(toTipusTascaDto(tasca.getTipus()));
//			dto.setJbpmName(tasca.getJbpmName());
//			dto.setDefinicioProces(toDefinicioProcesDto(tasca.getDefinicioProces()));			
//			dto.setValidacions(toValidacionsDto(tasca.getValidacions()));
//			dto.setRecursForm(tasca.getRecursForm());
//			dto.setFormExtern(tasca.getFormExtern());
//			dto.setValidada(validada);
//			dto.setDocumentsComplet(documentsComplet);
//			dto.setSignaturesComplet(signaturesComplet);			
//			
//			List<CampTascaDto> campsTasca = toCampsTascaDto(campTascaRepository.findAmbTascaOrdenats(tasca.getId()));
//			dto.setCamps(campsTasca);
//			List<DocumentTascaDto> documentsTasca = toDocumentTascaDto(documentTascaRepository.findAmbTascaOrdenats(tasca.getId()));
//			dto.setDocuments(documentsTasca);
//			List<FirmaTascaDto> signaturesTasca = toFirmaTascaDto(firmaTascaRepository.findAmbTascaOrdenats(tasca.getId()));
//			dto.setSignatures(signaturesTasca);
//			if (ambVariables) {
//				Map<String, Object> valors = jbpmHelper.getTaskInstanceVariables(task.getId());
//
//				dto.setVarsDocuments(
//						obtenirVarsDocumentsTasca(
//								task.getId(),
//								task.getProcessInstanceId(),
//								documentsTasca));
//				dto.setVarsDocumentsPerSignar(
//						obtenirVarsDocumentsPerSignarTasca(
//								task.getId(),
//								task.getProcessInstanceId(),
//								signaturesTasca));
//				filtrarVariablesTasca(valors);
//				if (varsCommand != null)
//					valors.putAll(varsCommand);
//				List<CampDto> camps = new ArrayList<CampDto>();
//				for (CampTascaDto campTasca: campsTasca)
//					camps.add(campTasca.getCamp());
//				if (ambTexts) {
//					Map<String, ParellaCodiValorDto> valorsDomini = obtenirValorsDominiDto(
//							task.getId(),
//							null,
//							camps,
//							valors);
//					dto.setValorsDomini(valorsDomini);
//					Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini = obtenirValorsMultiplesDomini(
//							task.getId(),
//							null,
//							camps,
//							valors);
//					dto.setValorsMultiplesDomini(valorsMultiplesDomini);
//					getServiceUtils().revisarVariablesJbpm(valors);
//					dto.setVarsComText(textPerCamps(task.getId(), null, camps, valors, valorsDomini, valorsMultiplesDomini));
//				}
//				dto.setVariables(valors);
//			}
//		}
		return dto;
	}

//	private List<FirmaTascaDto> toFirmaTascaDto(List<FirmaTasca> findAmbTascaOrdenats) {
//		List<FirmaTascaDto> val = new ArrayList<FirmaTascaDto>();
//		for(FirmaTasca validacio : findAmbTascaOrdenats) {
//			val.add(new ModelMapper().map(validacio, FirmaTascaDto.class));
//		}
//		return val;
//	}
//
//	private List<DocumentTascaDto> toDocumentTascaDto(List<DocumentTasca> findAmbTascaOrdenats) {
//		List<DocumentTascaDto> val = new ArrayList<DocumentTascaDto>();
//		for(DocumentTasca validacio : findAmbTascaOrdenats) {
//			val.add(new ModelMapper().map(validacio, DocumentTascaDto.class));
//		}
//		return val;
//	}
//
//	private List<CampTascaDto> toCampsTascaDto(List<CampTasca> findAmbTascaOrdenats) {
//		List<CampTascaDto> val = new ArrayList<CampTascaDto>();
//		for(CampTasca validacio : findAmbTascaOrdenats) {
//			val.add(new ModelMapper().map(validacio, CampTascaDto.class));
//		}
//		return val;
//	}
//
//	private List<ValidacioDto> toValidacionsDto(List<Validacio> validacions) {
//		List<ValidacioDto> val = new ArrayList<ValidacioDto>();
//		for(Validacio validacio : validacions) {
//			val.add(new ModelMapper().map(validacio, ValidacioDto.class));
//		}
//		return val;
//	}
//
//	private TipusTasca toTipusTascaDto(net.conselldemallorca.helium.core.model.hibernate.Tasca.TipusTasca tipus) {
//		if (tipus == null)
//			return null;
//		TipusTasca intern = null;
//		if (tipus.equals(net.conselldemallorca.helium.core.model.hibernate.Tasca.TipusTasca.ESTAT))
//			intern = TipusTasca.ESTAT;
//		if (tipus.equals(net.conselldemallorca.helium.core.model.hibernate.Tasca.TipusTasca.FORM))
//			intern = TipusTasca.FORM;
//		else
//			intern =  TipusTasca.SIGNATURA;
//		
//		return intern;
//	}

	private DefinicioProcesDto toDefinicioProcesDto(DefinicioProces definicioProces) {		
		return new ModelMapper().map(definicioProces, DefinicioProcesDto.class);
	}
	
//	private Map<String, List<ParellaCodiValorDto>> obtenirValorsMultiplesDomini(
//			String taskId,
//			String processInstanceId,
//			Collection<CampDto> camps,
//			Map<String, Object> valors) throws DominiException {
//		Map<String, List<ParellaCodiValorDto>> resposta = new HashMap<String, List<ParellaCodiValorDto>>();
//		if (valors != null) {
//			for (CampDto camp: camps) {
//				if (camp.isMultiple()) {
//					Object valor = valors.get(camp.getCodi());
//					if (valor instanceof Object[]) {
//						List<ParellaCodiValorDto> codisValor = new ArrayList<ParellaCodiValorDto>();
//						for (int i = 0; i < Array.getLength(valor); i++) {
//							ParellaCodiValorDto codiValor = obtenirValorDomini(
//									taskId,
//									processInstanceId,
//									null,
//									camp,
//									Array.get(valor, i),
//									true);
//							ParellaCodiValorDto parellaDto = null;
//							if (codiValor != null) {
//								parellaDto = new ParellaCodiValorDto(
//									codiValor.getCodi(),
//									codiValor.getValor());
//							}
//							codisValor.add(parellaDto);
//						}
//						resposta.put(camp.getCodi(), codisValor);
//					}
//				}
//			}
//		}
//		return resposta;
//	}

//	private Map<String, ParellaCodiValorDto> obtenirValorsDominiDto(
//			String taskId,
//			String processInstanceId,
//			Collection<CampDto> camps,
//			Map<String, Object> valors) throws DominiException {
//		Map<String, ParellaCodiValorDto> resposta = new HashMap<String, ParellaCodiValorDto>();
//		if (valors != null) {
//			for (CampDto camp: camps) {
//				if (!camp.isMultiple() && (camp.getTipus().equals(TipusCamp.SELECCIO) || camp.getTipus().equals(TipusCamp.SUGGEST))) {
//					Object valor = valors.get(camp.getCodi());
//					ParellaCodiValorDto codiValor = obtenirValorDomini(
//							taskId,
//							processInstanceId,
//							null,
//							camp,
//							valor,
//							true);
//					resposta.put(camp.getCodi(), codiValor);
//				}
//			}
//		}
//		Map<String, ParellaCodiValorDto> respostaDto = new HashMap<String, ParellaCodiValorDto>();
//		for (String clau: resposta.keySet()) {
//			ParellaCodiValorDto parella = resposta.get(clau);
//			ParellaCodiValorDto parellaDto = null;
//			if (parella != null) {
//				parellaDto = new ParellaCodiValorDto(
//						parella.getCodi(),
//						parella.getValor());
//			}
//			respostaDto.put(clau, parellaDto);
//		}
//		return respostaDto;
//	}

//	private Map<String, DocumentDto> obtenirVarsDocumentsPerSignarTasca(
//			String taskId,
//			String processInstanceId,
//			List<FirmaTascaDto> signatures) {
//		Map<String, DocumentDto> resposta = new HashMap<String, DocumentDto>();
//		if (signatures != null) {
//			for (FirmaTascaDto signatura: signatures) {
//				DocumentDto dto = documentHelper.getDocumentSenseContingut(
//						taskId,
//						processInstanceId,
//						signatura.getDocument().getCodi(),
//						true,
//						false);
//				if (dto != null)
//					resposta.put(
//							signatura.getDocument().getCodi(),
//							dto);
//			}
//		}
//		return resposta;
//	}
	
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

//	private Map<String, PersonaDto> getPersonesMap(String assignee, Set<String> pooledActors) {
//		Map<String, PersonaDto> resposta = new HashMap<String, PersonaDto>();
//		if (assignee != null)
//			resposta.put(assignee, personaHelper.findAmbCodiPlugin(assignee));
//		if (pooledActors != null) {
//			for (String actorId: pooledActors)
//				resposta.put(actorId, personaHelper.findAmbCodiPlugin(actorId));
//		}
//		return resposta;
//	}
	
//	private String textPerCampDonatValorDomini(
//			CampDto camp,
//			Object valor,
//			ParellaCodiValorDto valorDomini) {
//		if (valor == null) return null;
//		if (camp == null)
//			return valor.toString();
//		else
//			return CampDto.getComText(
//					camp.getTipus(),
//					valor,
//					(valorDomini != null) ? (String)valorDomini.getValor() : null);
//	}
	
	public static EntornDto toEntornDto(Entorn entorn) {
		EntornDto ent = new EntornDto(entorn.getId(), entorn.getCodi(), entorn.getNom());
		return ent;		
	}
	
	private ParellaCodiValor obtenirValorDomini(
			String taskId,
			String processInstanceId,
			Map<String, Object> valorsAddicionals,
			Camp camp,
			Object valor,
			boolean actualitzarJbpm) throws DominiException {
		if (valor == null)
			return null;
		if (valor instanceof DominiCodiDescripcio) {
			return new ParellaCodiValor(
					((DominiCodiDescripcio)valor).getCodi(),
					((DominiCodiDescripcio)valor).getDescripcio());
		}
		ParellaCodiValor resposta = null;
		TipusCamp tipus = camp.getTipus();
		if (tipus.equals(TipusCamp.SELECCIO) || tipus.equals(TipusCamp.SUGGEST)) {
			if (camp.getDomini() != null || camp.isDominiIntern()) {
				Long dominiId = (long) 0;
				if (camp.getDomini() != null){
					Domini domini = camp.getDomini();
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
										ParellaCodiValor codiValor = new ParellaCodiValor(
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
				Enumeracio enumeracio = camp.getEnumeracio();
				for (ParellaCodiValor parella: enumeracioValorsHelper.getLlistaValors(enumeracio.getId())) {
					// Per a evitar problemes amb caràcters estranys al codi (EXSANCI)
					String codiBo = null;
					if (parella.getCodi() != null)
						codiBo = parella.getCodi().replaceAll("\\p{Cntrl}", "").trim();
					String valorBo = valor.toString().replaceAll("\\p{Cntrl}", "").trim();
					if (valorBo.equals(codiBo)) {
						resposta = new ParellaCodiValor(
								parella.getCodi(),
								parella.getValor());
					}
				}
			} else if (camp.getConsulta() != null) {
				Consulta consulta = camp.getConsulta();
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
							resposta = new ParellaCodiValor(
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
	
//	private ParellaCodiValorDto obtenirValorDomini(
//			String taskId,
//			String processInstanceId,
//			Map<String, Object> valorsAddicionals,
//			CampDto camp,
//			Object valor,
//			boolean actualitzarJbpm) throws DominiException {
//		if (valor == null)
//			return null;
//		if (valor instanceof DominiCodiDescripcio) {
//			return new ParellaCodiValorDto(
//					((DominiCodiDescripcio)valor).getCodi(),
//					((DominiCodiDescripcio)valor).getDescripcio());
//		}
//		ParellaCodiValorDto resposta = null;
//		TipusCampDto tipus = camp.getTipus();
//		if (tipus.equals(TipusCamp.SELECCIO) || tipus.equals(TipusCamp.SUGGEST)) {
//			if (camp.getDomini() != null || camp.isDominiIntern()) {
//				Long dominiId = (long) 0;
//				if (camp.getDomini() != null){
//					DominiDto domini = camp.getDomini();
//					dominiId = domini.getId();
//				}				
//				try {
//					Map<String, Object> paramsConsulta = getParamsConsulta(
//							taskId,
//							processInstanceId,
//							camp,
//							valorsAddicionals);
//					List<FilaResultat> resultat = dominiDao.consultar(
//							camp.getDefinicioProces().getEntorn().getId(),
//							dominiId,
//							camp.getDominiId(),
//							paramsConsulta);
//					String columnaCodi = camp.getDominiCampValor();
//					String columnaValor = camp.getDominiCampText();
//					Iterator<FilaResultat> it = resultat.iterator();
//					while (it.hasNext()) {
//						FilaResultat fr = it.next();
//						for (ParellaCodiValor parellaCodi: fr.getColumnes()) {
//							if (parellaCodi.getCodi().equals(columnaCodi) && parellaCodi.getValor().toString().equals(valor)) {
//								for (ParellaCodiValor parellaValor: fr.getColumnes()) {
//									if (parellaValor.getCodi().equals(columnaValor)) {
//										ParellaCodiValorDto codiValor = new ParellaCodiValorDto(
//												parellaCodi.getValor().toString(),
//												parellaValor.getValor());
//										resposta = codiValor;
//										break;
//									}
//								}
//								break;
//							}
//						}
//					}
//				} catch (Exception ex) {
//					//throw new DominiException("No s'ha pogut consultar el domini", ex);
//					logger.error("No s'ha pogut consultar el domini", ex);
//				}
//			} else if (camp.getEnumeracio() != null) {
//				EnumeracioDto enumeracio = camp.getEnumeracio();
//				for (ParellaCodiValor parella: enumeracioValorsHelper.getLlistaValors(enumeracio.getId())) {
//					// Per a evitar problemes amb caràcters estranys al codi (EXSANCI)
//					String codiBo = null;
//					if (parella.getCodi() != null)
//						codiBo = parella.getCodi().replaceAll("\\p{Cntrl}", "").trim();
//					String valorBo = valor.toString().replaceAll("\\p{Cntrl}", "").trim();
//					if (valorBo.equals(codiBo)) {
//						resposta = new ParellaCodiValorDto(
//								parella.getCodi(),
//								parella.getValor());
//					}
//				}
//			} else if (camp.getConsulta() != null) {
//				ConsultaDto consulta = camp.getConsulta();
//				List<ExpedientConsultaDissenyDto> dadesExpedients = expedientHelper.findAmbEntornConsultaDisseny(
//						consulta.getEntorn().getId(),
//						consulta.getId(),
//						new HashMap<String, Object>(),
//						null,
//						true);
//				
//				Iterator<ExpedientConsultaDissenyDto> it = dadesExpedients.iterator();
//				while(it.hasNext()){
//					ExpedientConsultaDissenyDto exp = it.next();
//					DadaIndexadaDto valorDto = exp.getDadesExpedient().get(camp.getConsultaCampValor());
//					if(valorDto == null){
//						valorDto = exp.getDadesExpedient().get(consulta.getExpedientTipus().getJbpmProcessDefinitionKey()+"/"+camp.getConsultaCampValor());
//					}
//					if(valorDto != null){
//						if(valorDto.getValor().toString().equals(valor)){
//							DadaIndexadaDto textDto = exp.getDadesExpedient().get(camp.getConsultaCampText());
//							if(textDto == null){
//								textDto = exp.getDadesExpedient().get(consulta.getExpedientTipus().getJbpmProcessDefinitionKey()+"/"+camp.getConsultaCampText());
//							}
//							resposta = new ParellaCodiValorDto(
//									valorDto.getValorMostrar(),
//									textDto.getValorMostrar()
//									);
//							break;
//						}
//					}
//				}
//			}
//		}
//		return resposta;
//	}

	private Map<String, Object> getParamsConsulta(
			String taskId,
			String processInstanceId,
			Camp camp,
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

//	private Map<String, Object> getParamsConsulta(
//			String taskId,
//			String processInstanceId,
//			CampDto camp,
//			Map<String, Object> valorsAddicionals) {
//		String dominiParams = camp.getDominiParams();
//		if (dominiParams == null || dominiParams.length() == 0)
//			return null;
//		Map<String, Object> params = new HashMap<String, Object>();
//		String[] pairs = dominiParams.split(";");
//		for (String pair: pairs) {
//			String[] parts = pair.split(":");
//			String paramCodi = parts[0];
//			String campCodi = parts[1];
//			Object value = null;
//			if (campCodi.startsWith("@")) {
//				value = (String)GlobalProperties.getInstance().get(campCodi.substring(1));
//			} else if (campCodi.startsWith("#{")) {
//				if (processInstanceId != null) {
//					value = jbpmHelper.evaluateExpression(taskId, processInstanceId, campCodi, null);
//				} else if (taskId != null) {
//					JbpmTask task = jbpmHelper.getTaskById(taskId);
//					value = jbpmHelper.evaluateExpression(taskId, task.getProcessInstanceId(), campCodi, null);
//				} else if (campCodi.startsWith("#{'")) {
//					int index = campCodi.lastIndexOf("'");
//					if (index != -1 && index > 2)
//						value = campCodi.substring(3, campCodi.lastIndexOf("'"));
//				}
//			} else {
//				if (valorsAddicionals != null && valorsAddicionals.size() > 0)
//					value = valorsAddicionals.get(campCodi);
//				if (value == null && taskId != null)
//					value = getServiceUtils().getVariableJbpmTascaValor(taskId, campCodi);
//				if (value == null && processInstanceId != null)
//					value = getServiceUtils().getVariableJbpmProcesValor(processInstanceId, campCodi);
//			}
//			if (value != null)
//				params.put(paramCodi, value);
//		}
//		return params;
//	}

//	private Map<String, DocumentDto> obtenirVarsDocumentsTasca(
//			String taskId,
//			String processInstanceId,
//			List<DocumentTascaDto> documents) {
//		Map<String, DocumentDto> resposta = new HashMap<String, DocumentDto>();
//		for (DocumentTascaDto document: documents) {
//			DocumentDto dto = documentHelper.getDocumentSenseContingut(
//					taskId,
//					processInstanceId,
//					document.getDocument().getCodi(),
//					false,
//					false);
//			if (dto != null)
//				resposta.put(document.getDocument().getCodi(), dto);
//		}
//		return resposta;
//	}

	public void revisarDadesExpedientAmbValorsEnumeracionsODominis(
			Map<String, DadaIndexadaDto> dadesExpedient,
			List<Camp> campsInforme) {
		for (Camp camp: campsInforme) {
			if (!camp.isDominiCacheText() && (TipusCamp.SELECCIO.equals(camp.getTipus()) || TipusCamp.SUGGEST.equals(camp.getTipus()))) {
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
//		Set<CampDto> camps = definicioProces.getCamps();
//		dto.setCamps(camps);
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
//			Map<String, ParellaCodiValorDto> valorsDomini = obtenirValorsDominiDto(
//					null,
//					processInstanceId,
//					camps,
//					valors);
//			dto.setValorsDomini(valorsDomini);
//			Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini = obtenirValorsMultiplesDomini(
//					null,
//					processInstanceId,
//					camps,
//					valors);
//			dto.setValorsMultiplesDomini(valorsMultiplesDomini);
			getServiceUtils().revisarVariablesJbpm(valors);
//			dto.setVarsComText(
//					textPerCamps(
//							null,
//							processInstanceId,
//							camps,
//							valors,
//							valorsDomini,
//							valorsMultiplesDomini));
//			dto.setVarsOcultes(obtenirVarsOcultes(camps));
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

//	private Map<String, Boolean> obtenirVarsOcultes(
//			Collection<CampDto> campsDto) {
//		Map<String, Boolean> resposta = new HashMap<String, Boolean>();
//		for (CampDto campDto: campsDto)
//			resposta.put(
//					campDto.getCodi(),
//					new Boolean(campDto.isOcult()));
//		return resposta;
//	}

	private ServiceUtilsV3 getServiceUtils() {
		if (serviceUtils == null) {
			serviceUtils = new ServiceUtilsV3();
		}
		return serviceUtils;
	}
	
//	private String[] textsPerCampTipusRegistre(
//			String taskId,
//			String processInstanceId,
//			CampDto camp,
//			Object valorRegistre) {
//		String[] texts = new String[camp.getRegistreMembres().size()];
//		Map<String, Object> valorsAddicionalsConsulta = new HashMap<String, Object>();
//		for (int j = 0; j < camp.getRegistreMembres().size(); j++) {
//			if (j < Array.getLength(valorRegistre)) {
//				valorsAddicionalsConsulta.put(
//						camp.getRegistreMembres().get(j).getMembre().getCodi(),
//						Array.get(valorRegistre, j));
//			}
//		}
//		for (int j = 0; j < Array.getLength(valorRegistre); j++) {
//			if (j == camp.getRegistreMembres().size())
//				break;
//			CampDto membreRegistre = camp.getRegistreMembres().get(j).getMembre();
//			if (membreRegistre.getTipus().equals(TipusCamp.SUGGEST) || membreRegistre.getTipus().equals(TipusCamp.SELECCIO)) {
//				ParellaCodiValorDto codiValor = obtenirValorDomini(
//						taskId,
//						processInstanceId,
//						valorsAddicionalsConsulta,
//						membreRegistre,
//						Array.get(valorRegistre, j),
//						true);
//				ParellaCodiValorDto parellaDto = null;
//				if (codiValor != null) {
//					parellaDto = new ParellaCodiValorDto(
//							codiValor.getCodi(),
//							codiValor.getValor());
//				}
//				texts[j] = textPerCampDonatValorDomini(
//						membreRegistre,
//						Array.get(valorRegistre, j),
//						parellaDto);
//			} else {
//				texts[j] = textPerCampDonatValorDomini(
//						membreRegistre,
//						Array.get(valorRegistre, j),
//						null);
//			}
//		}
//		return texts;
//	}

//	private Map<String, Object> textPerCamps(
//			String taskId,
//			String processInstanceId,
//			Collection<CampDto> camps,
//			Map<String, Object> valors,
//			Map<String, ParellaCodiValorDto> valorsDomini,
//			Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini) {
//		Map<String, Object> resposta = new HashMap<String, Object>();
//		if (valors != null) {
//			for (String key: valors.keySet()) {
//				boolean found = false;
//				for (CampDto camp: camps) {
//					if (camp.getCodi().equals(key)) {
//						if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
//							Object valor = valors.get(key);
//							if (valor != null && valor instanceof Object[]) {
//								List<String[]> grid = new ArrayList<String[]>();
//								if (camp.isMultiple()) {
//									for (int i = 0; i < Array.getLength(valor); i++) {
//										Object valorRegistre = Array.get(valor, i);
//										if (valorRegistre != null) {
//											grid.add(textsPerCampTipusRegistre(
//													taskId,
//													processInstanceId,
//													camp,
//													valorRegistre));
//										}
//									}
//								} else {
//									grid.add(textsPerCampTipusRegistre(
//											taskId,
//											processInstanceId,
//											camp,
//											valor));
//								}
//								resposta.put(key, grid);
//							} else {
//								resposta.put(key, null);
//							}
//						} else if (camp.isMultiple()) {
//							Object valor = valors.get(key);
//							if (valor != null) {
//								if (valor instanceof Object[]) {
//									List<String> texts = new ArrayList<String>();
//									for (int i = 0; i < Array.getLength(valor); i++) {
//										String t = null;
//										if (camp.getTipus().equals(TipusCamp.SUGGEST) || camp.getTipus().equals(TipusCamp.SELECCIO)) {
//											if (valorsMultiplesDomini.get(key).size() > i)
//												t = textPerCampDonatValorDomini(camp, Array.get(valor, i), valorsMultiplesDomini.get(key).get(i));
//											else
//												t = "!" + Array.get(valor, i) + "!";
//										} else {
//											t = textPerCampDonatValorDomini(camp, Array.get(valor, i), null);
//										}
//										if (t != null)
//											texts.add(t);
//									}
//									resposta.put(key, texts);
//								} else {
//									logger.warn("No s'ha pogut convertir el camp " + camp.getCodi() + "a text: El camp és múltiple però el seu valor no és un array (" + valor.getClass().getName() + ")");
//								}
//							} else {
//								resposta.put(key, null);
//							}
//						} else {
//							resposta.put(
//									key,
//									textPerCampDonatValorDomini(camp, valors.get(key), valorsDomini.get(key)));
//						}
//						found = true;
//						break;
//					}
//				}
//				if (!found) {
//					// Si no hi ha cap camp associat el mostra com un String
//					Object valor = valors.get(key);
//					if (valor != null)
//						resposta.put(key, valor.toString());
//				}
//			}
//		}
//		return resposta;
//	}

	public String getCampText(
			String taskId,
			String processInstanceId,
			Camp camp,
			Object valor) {
		if (valor == null) return null;
		if (camp == null) {
			return valor.toString();
		} else {
			String textDomini = null;
			if (	camp.getTipus().equals(TipusCamp.SELECCIO) ||
					camp.getTipus().equals(TipusCamp.SUGGEST)) {
				if (valor instanceof DominiCodiDescripcio) {
					textDomini = ((DominiCodiDescripcio)valor).getDescripcio();
				} else {
					ParellaCodiValor resultat = obtenirValorDomini(
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
			return Camp.getComText(
					camp.getTipus(),
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
			dto.setIniciadorPersona(personaHelper.findAmbCodiPlugin(expedient.getIniciadorCodi()));
			if (expedient.getResponsableCodi() != null)
				dto.setResponsablePersona(personaHelper.findAmbCodiPlugin(expedient.getResponsableCodi()));
		}
		if (expedient.getIniciadorTipus().equals(IniciadorTipus.SISTRA))
			dto.setBantelEntradaNum(expedient.getNumeroEntradaSistra());
		dto.setTipus(new ModelMapper().map(expedient.getTipus(), ExpedientTipusDto.class));
		dto.setEntorn(toEntornDto(expedient.getEntorn()));
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
//		dto.setErrorDesc(expedient.getErrorDesc());
//		dto.setErrorFull(expedient.getErrorFull());
		dto.setNotificacioTelematicaHabilitada(expedient.isNotificacioTelematicaHabilitada());
		dto.setTramitExpedientIdentificador(expedient.getTramitExpedientIdentificador());
		dto.setTramitExpedientClau(expedient.getTramitExpedientClau());
//		dto.setErrorsIntegracions(expedient.isErrorsIntegracions());
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
	
	private static final Log logger = LogFactory.getLog(DtoConverter.class);
}
