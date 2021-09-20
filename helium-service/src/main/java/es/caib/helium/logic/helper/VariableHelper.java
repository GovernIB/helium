/**
 * 
 */
package es.caib.helium.logic.helper;

import es.caib.helium.client.dada.dades.DadaClient;
import es.caib.helium.client.dada.dades.model.Dada;
import es.caib.helium.client.engine.model.WTaskInstance;
import es.caib.helium.client.model.ParellaCodiValor;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.dto.CampAgrupacioDto;
import es.caib.helium.logic.intf.dto.CampTascaDto;
import es.caib.helium.logic.intf.dto.CampTipusDto;
import es.caib.helium.logic.intf.dto.ConsultaDominiDto;
import es.caib.helium.logic.intf.dto.DadaIndexadaDto;
import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.DominiDto.TipusDomini;
import es.caib.helium.logic.intf.dto.ExpedientDadaDto;
import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.dto.ValidacioDto;
import es.caib.helium.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.extern.domini.FilaResultat;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.util.Constants;
import es.caib.helium.logic.intf.util.GlobalProperties;
import es.caib.helium.logic.ms.DominiMs;
import es.caib.helium.persist.entity.Camp;
import es.caib.helium.persist.entity.Camp.TipusCamp;
import es.caib.helium.persist.entity.CampAgrupacio;
import es.caib.helium.persist.entity.CampRegistre;
import es.caib.helium.persist.entity.CampTasca;
import es.caib.helium.persist.entity.ConsultaCamp.TipusConsultaCamp;
import es.caib.helium.persist.entity.DefinicioProces;
import es.caib.helium.persist.entity.Entorn;
import es.caib.helium.persist.entity.Enumeracio;
import es.caib.helium.persist.entity.EnumeracioValors;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.Tasca;
import es.caib.helium.persist.repository.CampRepository;
import es.caib.helium.persist.repository.CampTascaRepository;
import es.caib.helium.persist.repository.DefinicioProcesRepository;
import es.caib.helium.persist.repository.ExpedientRepository;
import es.caib.helium.persist.repository.TascaRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Helper per a gestionar les variables dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class VariableHelper {
	
	public static final String PARAMS_RETROCEDIR_VARIABLE_PREFIX = "H3l1um#params.retroces.";
	public static final String PARAMS_RETROCEDIR_SEPARADOR = "#@#";

	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private DominiMs dominiMs;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private DominiHelper dominiHelper;
	@Resource
	private ExpedientService expedientService;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
//	@Resource
//	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
	@Resource
	private MessageServiceHelper messageServiceHelper;
	@Resource
	private GlobalProperties globalProperties;
	@Resource
	private DadaClient dadaClient;


	public Object getVariableJbpmTascaValor(
			String taskId,
			String varCodi) {
		Object valor = workflowEngineApi.getTaskInstanceVariable(taskId, varCodi);
		return valorVariableJbpmRevisat(valor);
	}
	public Map<String, Object> getVariablesJbpmTascaValor(
			String taskId) {
		Map<String, Object> valors = workflowEngineApi.getTaskInstanceVariables(taskId);
		Map<String, Object> valorsRevisats = new HashMap<String, Object>();
		for (String varCodi: valors.keySet()) {
			Object valor = valors.get(varCodi);
			valorsRevisats.put(varCodi, valorVariableJbpmRevisat(valor));
		}
		return valorsRevisats;
	}
	public Object getVariableJbpmProcesValor(
			String processInstanceId,
			String varCodi) throws Exception {
		Object valor = workflowEngineApi.getProcessInstanceVariable(processInstanceId, varCodi);
		return valorVariableJbpmRevisat(valor);
	}
	public Map<String, Object> getVariablesJbpmProcesValor(
			String processInstanceId) {
		Map<String, Object> valors = workflowEngineApi.getProcessInstanceVariables(processInstanceId);
		Map<String, Object> valorsRevisats = new HashMap<String, Object>();
		if (valors != null) {
			for (String varCodi: valors.keySet()) {
				Object valor = valors.get(varCodi);
				valorsRevisats.put(varCodi, valorVariableJbpmRevisat(valor));
			}
		}
		return valorsRevisats;
	}

	public Map<String, Object> findVarsDadesPerInstanciaProces(String processInstanceId) {
		Map<String, Object> resposta = new HashMap<String, Object>();
		for (ExpedientDadaDto dada : findDadesPerInstanciaProces(processInstanceId)) {
			resposta.put(dada.getVarCodi(), dada.getText());
		}
		return resposta;
	}

	/*public Map<String, TascaDadaDto> findDadesTascaPerExpedientId(Long expedientId) {
		Expedient expedient = expedientRepository.findOne(expedientId);
		Map<String, TascaDadaDto> dades = new HashMap<String, TascaDadaDto>();
		for (ExpedientDadaDto dada : findDadesPerInstanciaProces(expedient.getProcessInstanceId())) {
			dades.put(dada.getVarCodi(), getTascaDadaDtoFromExpedientDadaDto(dada));
		}
		return dades;
	}*/

	public List<ExpedientDadaDto> findDadesPerInstanciaProces(String processInstanceId) {
		return findDadesPerInstanciaProces(processInstanceId, false);
	}

	public List<ExpedientDadaDto> findDadesPerInstanciaProces(
			String processInstanceId,
			boolean incloureVariablesBuides) {

		String tipusExp = null;
		var exp = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		var expedientTipus = exp.getTipus();
		// TODO Mètriques
//		if (MesuresTemporalsHelper.isActiu()) {
//			tipusExp = (exp != null ? exp.getTipus().getNom() : null);
//			mesuresTemporalsHelper.mesuraIniciar("Expedient DADES v3", "expedient", tipusExp);
//			mesuresTemporalsHelper.mesuraIniciar("Expedient DADES v3", "expedient", tipusExp, null, "0");
//		}
		var definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(processInstanceId);
		Map<String, Camp> campsIndexatsPerCodi = new HashMap<>();
		var camps = definicioProces.getCamps();
		if (expedientTipus.isAmbInfoPropia()) {
			if (expedientTipus.getExpedientTipusPare() != null) {
				// Camps heretats
				for (var camp: expedientTipus.getExpedientTipusPare().getCamps()) {
					campsIndexatsPerCodi.put(camp.getCodi(), camp);
				}
			}
			camps = expedientTipus.getCamps();
		}
		for (var camp: camps) {
			campsIndexatsPerCodi.put(camp.getCodi(), camp);
		}
//		mesuresTemporalsHelper.mesuraCalcular("Expedient DADES v3", "expedient", tipusExp, null, "0");
//		mesuresTemporalsHelper.mesuraIniciar("Expedient DADES v3", "expedient", tipusExp, null, "1");
		List<ExpedientDadaDto> resposta = new ArrayList<>();

		List<Dada> dades = null;
		try {
			dades = dadaClient.getDadesByExpedientIdProcesId(exp.getId(), processInstanceId);
		} catch (Exception ex) {
			log.error("Error al servei de dades", ex);  //TODO LLENÇAR EXCEPCIONS?
		}
		if (dades == null) {
			return resposta;
		}

//		mesuresTemporalsHelper.mesuraCalcular("Expedient DADES v3", "expedient", tipusExp, null, "1");
		Map<Integer, ConsultaDominiDto> consultesDomini = new HashMap<>();

//			mesuresTemporalsHelper.mesuraIniciar("Expedient DADES v3", "expedient", tipusExp, null, "2");
		dades = filtrarVariablesUsIntern(dades);
		for (var dada : dades) {
			ExpedientDadaDto dto = null;
			var camp = campsIndexatsPerCodi.get(dada.getCodi());
		/*	if (incloureVariablesBuides) {
				dto = getDadaPerVariableJbpm(
						camp,
						dada.getCodi(),
						null,
						null,
						null,
						processInstanceId,
						false,
						consultesDomini);
			} else {*/
				dto = getDadaPerVariableJbpm(
						camp,
						dada.getCodi(),
						dada.getValorsAsObject(),
						null,
						null,
						processInstanceId,
						false,
						consultesDomini);
		//	}
			resposta.add(dto);
		}
//			mesuresTemporalsHelper.mesuraCalcular("Expedient DADES v3", "expedient", tipusExp, null, "2");
		if (consultesDomini != null && !consultesDomini.isEmpty()) {
			this.consultaDominisAgrupats(consultesDomini); // TODO arrancar dominis
		}
//		mesuresTemporalsHelper.mesuraCalcular("Expedient DADES v3", "expedient", tipusExp);
		return resposta;
	}
	public ExpedientDadaDto getDadaPerInstanciaProces(
			String processInstanceId,
			String variableCodi) throws Exception {
		return getDadaPerInstanciaProces(processInstanceId, variableCodi, false);
	}
	
	public ExpedientDadaDto getDadaPerInstanciaProces(
			String processInstanceId,
			String variableCodi, 
			boolean incloureVariablesBuides) throws Exception {
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				processInstanceId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		ExpedientTipus expedientTipus = expedient.getTipus();
		
		Camp camp;
		if (expedientTipus.isAmbInfoPropia()) {
			camp = campRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(),
					variableCodi,
					expedientTipus.getExpedientTipusPare() != null);
		} else {
			camp = campRepository.findByDefinicioProcesAndCodi(
					definicioProces,
					variableCodi);
		}
		Object valor = workflowEngineApi.getProcessInstanceVariable(
				processInstanceId,
				variableCodi);

		ExpedientDadaDto dto = null;
		boolean varAmbContingut = valor != null;
		if (varAmbContingut) {
			dto = getDadaPerVariableJbpm(
					camp,
					variableCodi,
					valor,
					null,
					null,
					processInstanceId,
					false,
					null);
			// Si és registre o múltiple comprova si té contingut. Pot haver error de simple a múltiple
			try {
				if (camp != null && (TipusCamp.REGISTRE.equals(camp.getTipus()) || camp.isMultiple())) {
					Object[] registreValors = null;
					if (valor instanceof Dada) {
						registreValors = (Object[])((Dada) valor).getValorsAsObject();
					} else {
						registreValors = (Object[]) valor;
					}
					varAmbContingut = registreValors.length > 0;
				}
			} catch(Exception e) {
				dto.setError(messageServiceHelper.getMessage(
						"variable.helper.error.recuperant.valor", 
						new Object[] {camp.getTipus(), (camp.isMultiple() ? " múltiple" : "")}));
			}
		}
		if (!varAmbContingut && incloureVariablesBuides) {
			dto = getDadaPerVariableJbpm(
					camp,
					variableCodi,
					null,
					null,
					null,
					processInstanceId,
					false,
					null);
		}
		return dto;
	}
	
	public ExpedientDadaDto getDadaBuida(
			long campId) {
		Camp camp = campRepository.findById(campId).get();
		ExpedientDadaDto dto = getDadaPerVariableJbpm(
					camp,
					camp.getCodi(),
					null,
					null,
					null,
					null,
					false,
					null);
		return dto;
	}

	public List<TascaDadaDto> findDadesPerInstanciaTascaDto(Long expedientTipusId, ExpedientTascaDto tasca) {		
		List<TascaDadaDto> resposta = new ArrayList<TascaDadaDto>();
		Tasca tascaEntity = tascaRepository.findById(tasca.getTascaId()).get();
		if (expedientTipusId == null)
			expedientTipusId = tascaEntity.getDefinicioProces().getExpedientTipus() != null ? tascaEntity.getDefinicioProces().getExpedientTipus().getId() : null; 
			Map<Integer, ConsultaDominiDto> consultesDomini = new HashMap<Integer, ConsultaDominiDto>();
		for (CampTasca campTasca: campTascaRepository.findAmbTascaOrdenats(tasca.getTascaId(), expedientTipusId)) {
			Camp camp = campTasca.getCamp();
			
			ExpedientDadaDto expedientDadaDto = getDadaPerVariableJbpm(
					camp,
					camp.getCodi(),
					null,
					null,
					tasca.getId(),
					null,
					false,
					consultesDomini);
			CampAgrupacio agrupacio = camp.getAgrupacio();
			resposta.add(
					getTascaDadaDtoFromExpedientDadaDto(
							expedientDadaDto,
							campTasca.getCamp(),
							campTasca.isReadOnly(),
							campTasca.isReadFrom(),
							campTasca.isWriteTo(),
							campTasca.isRequired(),
							campTasca.getAmpleCols(),
							campTasca.getBuitCols(),
							agrupacio));
		}
		this.consultaDominisAgrupats(consultesDomini);
		return resposta;
	}

	public List<TascaDadaDto> findDadesPerInstanciaTasca(
			WTaskInstance task) {
		String tipusExp = null;
		Expedient exp = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		Long expedientTipusId = exp != null ? exp.getTipus().getId() : null;
		ExpedientTipus expedientTipus = exp != null? expedientTipusHelper.findAmbProcessInstanceId(task.getProcessInstanceId()) : null;
		// TODO: Mètriques
//		if (MesuresTemporalsHelper.isActiu()) {
//			tipusExp = (exp != null ? exp.getTipus().getNom() : null);
//			mesuresTemporalsHelper.mesuraIniciar("Tasca DADES v3", "tasques", tipusExp, task.getTaskName());
//			mesuresTemporalsHelper.mesuraIniciar("Tasca DADES v3", "tasques", tipusExp, task.getTaskName(), "0");
//		}
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				task.getProcessInstanceId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getTaskName(),
				definicioProces);
		List<CampTasca> campsTasca = campTascaRepository.findAmbTascaIdOrdenats(tasca.getId(), expedientTipusId);
		boolean ambHerencia = HerenciaHelper.ambHerencia(expedientTipus);
		Map<String, Camp> sobreescrits = new HashMap<String, Camp>();
		if (ambHerencia) {
			for (Camp c : campRepository.findSobreescrits(expedientTipusId))
				sobreescrits.put(c.getCodi(), c);
		}
//		mesuresTemporalsHelper.mesuraCalcular("Tasca DADES v3", "tasques", tipusExp, task.getTaskName(), "0");
//		mesuresTemporalsHelper.mesuraIniciar("Tasca DADES v3", "tasques", tipusExp, task.getTaskName(), "1");
		List<TascaDadaDto> resposta = new ArrayList<TascaDadaDto>();
		Map<String, Object> varsInstanciaTasca = workflowEngineApi.getTaskInstanceVariables(
				task.getId());
//		mesuresTemporalsHelper.mesuraCalcular("Tasca DADES v3", "tasques", tipusExp, task.getTaskName(), "1");
//		mesuresTemporalsHelper.mesuraIniciar("Tasca DADES v3", "tasques", tipusExp, task.getTaskName(), "2");
		// Només es mostraran les variables donades d'alta al formulari
		// de la tasca. Les variables jBPM de la tasca que no siguin
		// al formulari no es mostraran.
		Map<Integer, ConsultaDominiDto> consultesDomini = new HashMap<Integer, ConsultaDominiDto>();
		for (CampTasca campTasca: campsTasca) {
			Camp camp = campTasca.getCamp();
			if (ambHerencia && sobreescrits.containsKey(camp.getCodi()))
				camp = sobreescrits.get(camp.getCodi());
			// Si el camp se sobreescriu llavors es passa el camp sobreescrit
			Object varValor = varsInstanciaTasca.get(camp.getCodi());
			ExpedientDadaDto expedientDadaDto = getDadaPerVariableJbpm(
					camp,
					camp.getCodi(),
					varValor,
					null,
					task.getId(),
					task.getProcessInstanceId(),
					false,
					consultesDomini);
			CampAgrupacio agrupacio = camp.getAgrupacio();
			resposta.add(
					getTascaDadaDtoFromExpedientDadaDto(
							expedientDadaDto,
							campTasca.getCamp(),
							campTasca.isReadOnly(),
							campTasca.isReadFrom(),
							campTasca.isWriteTo(),
							campTasca.isRequired(),
							campTasca.getAmpleCols(),
							campTasca.getBuitCols(),
							agrupacio));
		}
		// Consulta tots els dominis a la vegada
		this.consultaDominisAgrupats(consultesDomini);
		
//		mesuresTemporalsHelper.mesuraCalcular("Tasca DADES v3", "tasques", tipusExp, task.getTaskName(), "2");
//		mesuresTemporalsHelper.mesuraCalcular("Tasca DADES v3", "tasques", tipusExp, task.getTaskName());
		return resposta;
	}

	public TascaDadaDto findDadaPerInstanciaTasca(
			WTaskInstance task,
			String variableCodi) throws Exception {
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getTaskName(),
				definicioProces);
		
		ExpedientTipus expedientTipus = expedientTipusHelper.findAmbProcessInstanceId(task.getProcessInstanceId());
		boolean ambHerencia = HerenciaHelper.ambHerencia(expedientTipus);
		
		CampTasca campTasca = campTascaRepository.findAmbTascaCodi(
				tasca.getId(),
				variableCodi,
				expedientTipus.getId());
		Camp camp = null;
		if (campTasca != null) {
			if (ambHerencia)
				camp = campRepository.findByExpedientTipusAndCodi(expedientTipus.getId(), campTasca.getCamp().getCodi(), ambHerencia);
			else
				camp = campTasca.getCamp();
		} else {
			return null;
		}
		Object valor = workflowEngineApi.getTaskInstanceVariable(
				task.getId(),
				variableCodi);
		if (valor == null) {
			valor = workflowEngineApi.getProcessInstanceVariable(
					task.getProcessInstanceId(),
					variableCodi);
		}
		boolean varAmbContingut = valor != null;
		if (varAmbContingut && camp != null && (TipusCamp.REGISTRE.equals(camp.getTipus()) || camp.isMultiple())) {
			Object[] registreValors = (Object[])valor;
			varAmbContingut = registreValors.length > 0;
		}
		if (varAmbContingut) {
			ExpedientDadaDto dto = getDadaPerVariableJbpm(
					camp,
					variableCodi,
					valor,
					null,
					task.getId(),
					task.getProcessInstanceId(),
					false,
					null);
			CampAgrupacio agrupacio = (camp != null)?camp.getAgrupacio() : null;
			return getTascaDadaDtoFromExpedientDadaDto(
							dto,
							campTasca.getCamp(),
							campTasca.isReadOnly(),
							campTasca.isReadFrom(),
							campTasca.isWriteTo(),
							campTasca.isRequired(),
							campTasca.getAmpleCols(),
							campTasca.getBuitCols(),
							agrupacio);
		} else {
			return null;
		}
	}

	public String getTextPerCamp(
			Camp camp,
			Object valor,
			Map<String, Object> valorsAddicionals,
			String taskInstanceId,
			String processInstanceId,
			Map<Integer, ConsultaDominiDto> consultesDominis,
			Object dadaDto) throws Exception {
		if (valor == null)
			return null;
		String valorFontExterna = null;
		if (TipusCamp.SELECCIO.equals(camp.getTipus()) || TipusCamp.SUGGEST.equals(camp.getTipus())) {
			
			ParellaCodiValor parella = getTextPerCampAmbValor(
					camp,
					valor,
					valorsAddicionals,
					taskInstanceId,
					processInstanceId,
					consultesDominis,
					dadaDto);
			
			valorFontExterna = parella != null ? (String)(parella.getValor()) : "";
		}
		return Camp.getComText(
				camp.getTipus(),
				valor,
				valorFontExterna);
	}

	public List<ParellaCodiValor> getPossiblesValorsCamp(
			Camp camp,
			Camp registreCamp,
			Integer registreIndex,
			Object valor,
			Map<String, Object> valorsAddicionals,
			String taskInstanceId,
			String processInstanceId, 
			Map<Integer, ConsultaDominiDto> consultesDominis,
			Object dadaDto) throws Exception {
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		TipusCamp tipus = camp.getTipus();
		if (tipus.equals(TipusCamp.SELECCIO) || tipus.equals(TipusCamp.SUGGEST)) {
			if (camp.getDominiIntern() || camp.getDomini() != null ) {
				Map<String, Object> parametres = getParamsConsulta(
						taskInstanceId,
						processInstanceId,
						camp,
						registreCamp,
						registreIndex,
						valorsAddicionals);
				List<FilaResultat> resultatConsultaDomini;
				DominiDto domini;
				if (camp.getDominiIntern()) {
					Entorn entorn;
					if (camp.getDefinicioProces() != null)
						entorn = camp.getDefinicioProces().getEntorn();
					else
						entorn = camp.getExpedientTipus().getEntorn();
					domini = getDominiIntern(entorn);
				} else { 
					domini = dominiMs.get(camp.getDomini());
				}
				
				// TODO: TOTES les consultes a dominis han de ser agrupades!!
				if (! globalProperties.getAsBoolean("es.caib.helium.domini.consulta.agrupada")
						|| consultesDominis == null) 
				{
					// Consulta el domini
					resultatConsultaDomini = dominiHelper.consultar(
							domini.getId(),
							camp.getDominiId(),
							parametres);
					

					String columnaCodi = camp.getDominiCampValor();
					String columnaValor = camp.getDominiCampText();
					
					List<ParellaCodiValor> resultat = this.findParellaCodiValor(
							camp.getTipus() != null ? CampTipusDto.valueOf(camp.getTipus().toString()) : null,
							domini.getTipus(),
							resultatConsultaDomini,
							columnaCodi, 
							columnaValor, 
							valor);
					if (resultat != null)
						resposta.addAll(resultat);			
				} 
				else {
					// Guarda la consulta per fer-la agrupada
					ConsultaDominiDto consultaDomini = new ConsultaDominiDto();
					consultaDomini.setDominiId(domini.getId());
					consultaDomini.setDominiWsId(camp.getDominiId());
					consultaDomini.setParametres(parametres);
					consultaDomini.setCampCodi(camp.getDominiCampValor());
					consultaDomini.setCampText(camp.getDominiCampText());
					consultaDomini.setValor(valor);
					if (consultesDominis.containsKey(consultaDomini.getIdentificadorConsulta()))
						consultaDomini = consultesDominis.get(consultaDomini.getIdentificadorConsulta());
					else {
						consultaDomini.setCampId(camp.getId());
						if (camp.getTipus() != null)
							consultaDomini.setCampTipus(CampTipusDto.valueOf(camp.getTipus().toString()));
						consultaDomini.setDominiTipus(domini.getTipus());
						consultesDominis.put(consultaDomini.getIdentificadorConsulta(), consultaDomini);
					}
					consultaDomini.getDadesDto().add(dadaDto);
					
				}
			} else if (camp.getEnumeracio() != null) {
				Enumeracio enumeracio = camp.getEnumeracio();
				for (EnumeracioValors enumValor: enumeracio.getEnumeracioValors()) {
					String codiBo = null;
					if (enumValor.getCodi() != null)
						codiBo = enumValor.getCodi().replaceAll("\\p{Cntrl}", "").trim();
					
					String valorBo = null;
					if (valor != null)
						valorBo = valor.toString().replaceAll("\\p{Cntrl}", "").trim();
					
					if (valorBo == null || valorBo.equalsIgnoreCase(codiBo)) {
						resposta.add(new ParellaCodiValor(
								enumValor.getCodi(),
								enumValor.getNom()));
						if (valor != null) {
							break;
						}
					}
				}
			}
		}
		return resposta;
	}


	public DominiDto getDominiIntern(Entorn entorn) {
		DominiDto domini = new DominiDto();
		domini.setId((long) 0);
		domini.setCacheSegons(30);
		domini.setCodi("");
		domini.setNom("DominiDto intern");
		domini.setEntornId(entorn.getId());
		return domini;
	}

	public Map<String, Object> getParamsConsulta(
			String taskInstanceId,
			String processInstanceId,
			Camp camp,
			Camp registreCamp,
			Integer registreIndex,
			Map<String, Object> valorsAddicionals) throws Exception {
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
				value = globalProperties.getProperty(campCodi.substring(1));
			} else if (campCodi.startsWith("#{")) {
				if (processInstanceId != null) {
					value = workflowEngineApi.evaluateExpression(
							taskInstanceId,
							processInstanceId,
							campCodi,
							null);
				} else if (campCodi.startsWith("#{'")) {
					int index = campCodi.lastIndexOf("'");
					if (index != -1 && index > 2)
						value = campCodi.substring(3, campCodi.lastIndexOf("'"));
				}
			} else {
				if (valorsAddicionals != null && valorsAddicionals.size() > 0)
					value = valorsAddicionals.get(campCodi);
				if (value == null) {
					if (registreCamp != null) {
						value = getValorCampRegistre(
								taskInstanceId,
								processInstanceId,
								registreCamp,
								registreIndex,
								campCodi);
					}
					if (value == null && taskInstanceId != null)
						value = getVariableJbpmTascaValor(taskInstanceId, campCodi);
					if (value == null && processInstanceId != null)
						value = getVariableJbpmProcesValor(processInstanceId, campCodi);
				}
			}
			if (value != null)
				params.put(paramCodi, value);
		}
		return params;
	}

	public TascaDadaDto getTascaDadaDtoFromExpedientDadaDto(
			ExpedientDadaDto expedientDadaDto,
			Camp camp,
			boolean readOnly,
			boolean readFrom,
			boolean writeTo,
			boolean required,
			int ampleCols,
			int buitCols,
			CampAgrupacio agrupacio) {
		CampAgrupacioDto agrupacioDto = null;
		TascaDadaDto tascaDadaDto = new TascaDadaDto();
		tascaDadaDto.setVarCodi(expedientDadaDto.getVarCodi());
		tascaDadaDto.setVarValor(expedientDadaDto.getVarValor());
		tascaDadaDto.setCampId(expedientDadaDto.getCampId());
		tascaDadaDto.setCampTipus(expedientDadaDto.getCampTipus());
		tascaDadaDto.setCampEtiqueta(expedientDadaDto.getCampEtiqueta());
		tascaDadaDto.setCampMultiple(expedientDadaDto.isCampMultiple());
		tascaDadaDto.setCampOcult(expedientDadaDto.isCampOcult());
		tascaDadaDto.setLlistar(expedientDadaDto.isLlistar());
		
		if(agrupacio != null) {
			agrupacioDto = new CampAgrupacioDto(
					agrupacio.getId(),
					agrupacio.getCodi(),
					agrupacio.getNom(),
					agrupacio.getDescripcio(),
					agrupacio.getOrdre());
		}
		
		tascaDadaDto.setAgrupacio(agrupacioDto);
		if (camp != null) {
			tascaDadaDto.setReadOnly(readOnly);
			tascaDadaDto.setReadFrom(readFrom);
			tascaDadaDto.setWriteTo(writeTo);
			tascaDadaDto.setRequired(required);
			tascaDadaDto.setAmpleCols((ampleCols == 0 && buitCols == 0) ? 12 : ampleCols);
			tascaDadaDto.setBuitCols((ampleCols == 0 && buitCols == 0) ? 0 : buitCols);
			tascaDadaDto.setDominiId(camp.getDominiId());
			tascaDadaDto.setDominiCampText(camp.getDominiCampText());
			tascaDadaDto.setDominiCampValor(camp.getDominiCampValor());
			tascaDadaDto.setDominiParams(camp.getDominiParams());
		}
		tascaDadaDto.setText(expedientDadaDto.getText());
		tascaDadaDto.setError(expedientDadaDto.getError());
		tascaDadaDto.setObservacions(expedientDadaDto.getObservacions());
		tascaDadaDto.setJbpmAction(expedientDadaDto.getJbpmAction());
		tascaDadaDto.setValidacions(expedientDadaDto.getValidacions());
		if (expedientDadaDto.getMultipleDades() != null) {
			List<TascaDadaDto> multipleDades = new ArrayList<TascaDadaDto>();
			for (ExpedientDadaDto dto: expedientDadaDto.getMultipleDades()) {
				TascaDadaDto tascaDada = getTascaDadaDtoFromExpedientDadaDto(
						dto,
						camp,
						readOnly,
						readFrom,
						writeTo,
						required,
						ampleCols,
						buitCols,
						agrupacio);
				// Si es un campo readonly quitamos la validación de los campos requeridos que contenga
				tascaDada.setRequired(readOnly ? false : dto.isRequired());
				multipleDades.add(tascaDada);
			}
			tascaDadaDto.setMultipleDades(multipleDades);
		}
		if (expedientDadaDto.getRegistreDades() != null) {
			List<CampRegistre> registreCamps = camp.getRegistreMembres();
			List<TascaDadaDto> registreDades = new ArrayList<TascaDadaDto>();
			for (ExpedientDadaDto dto: expedientDadaDto.getRegistreDades()) {
				CampRegistre registreCamp = null;
				for (CampRegistre cr: registreCamps) {
					if (cr.getMembre().getCodi().equals(dto.getVarCodi())) {
						registreCamp = cr;
						break;
					}
				}
				//CampAgrupacio agrupacio = camp.getAgrupacio();
				TascaDadaDto tascaDada = getTascaDadaDtoFromExpedientDadaDto(
						dto,
						registreCamp.getMembre(),
						false,
						false,
						false,
						registreCamp.isObligatori(),
						12,
						0,
						agrupacio);
				// Si es un campo readonly quitamos la validación de los campos requeridos que contenga
				//tascaDada.setRequired(readOnly ? false : dto.isRequired());
				registreDades.add(tascaDada);
			}
			tascaDadaDto.setRegistreDades(registreDades);
		}
		if (camp.getDominiParams() != null) {
			String dominiParams = camp.getDominiParams();
			String[] pairs = dominiParams.split(";");
			List<String> paramCampCodis = new ArrayList<String>();
			for (String pair: pairs) {
				String[] parts = pair.split(":");
				if (parts.length >= 2) {
					String campCodi = parts[1];
					if (!campCodi.startsWith("@") && !campCodi.startsWith("#{")) {
						paramCampCodis.add(campCodi);
					}
				}
			}
			tascaDadaDto.setCampParams(
					paramCampCodis.toArray(
							new String[paramCampCodis.size()]));
		}
		return tascaDadaDto;
	}
		
	/*public TascaDadaDto getTascaDadaDtoFromExpedientDadaDto(
			ExpedientDadaDto expedientDadaDto) {
		TascaDadaDto tascaDto = new TascaDadaDto();
		tascaDto.setVarCodi(expedientDadaDto.getVarCodi());
		tascaDto.setVarValor(expedientDadaDto.getVarValor());
		tascaDto.setCampId(expedientDadaDto.getCampId());
		tascaDto.setCampTipus(expedientDadaDto.getCampTipus());
		tascaDto.setCampEtiqueta(expedientDadaDto.getCampEtiqueta());
		tascaDto.setCampMultiple(expedientDadaDto.isCampMultiple());
		tascaDto.setCampOcult(expedientDadaDto.isCampOcult());
		tascaDto.setRequired(expedientDadaDto.isRequired());
		tascaDto.setText(expedientDadaDto.getText());
		tascaDto.setError(expedientDadaDto.getError());
		tascaDto.setObservacions(expedientDadaDto.getObservacions());
		tascaDto.setJbpmAction(expedientDadaDto.getJbpmAction());
		tascaDto.setValidacions(expedientDadaDto.getValidacions());
		if (expedientDadaDto.getMultipleDades() != null) {
			List<TascaDadaDto> multipleDades = new ArrayList<TascaDadaDto>();
			for (ExpedientDadaDto dto: expedientDadaDto.getMultipleDades()) {
				multipleDades.add(getTascaDadaDtoFromExpedientDadaDto(dto));
			}
			tascaDto.setMultipleDades(multipleDades);
		}
		if (expedientDadaDto.getRegistreDades() != null) {
			List<TascaDadaDto> registreDades = new ArrayList<TascaDadaDto>();
			for (ExpedientDadaDto dto: expedientDadaDto.getRegistreDades()) {
				registreDades.add(getTascaDadaDtoFromExpedientDadaDto(dto));
			}
			tascaDto.setRegistreDades(registreDades);
		}
		return tascaDto;
	}*/

	public TascaDadaDto getTascaDadaDtoParaConsultaDisseny(Camp camp, TipusConsultaCamp tipus, Map<Integer, ConsultaDominiDto> consultesDomini) {
		TascaDadaDto tascaDto = new TascaDadaDto();
		String varCodi;
		if (TipusConsultaCamp.INFORME.equals(tipus) && camp.getDefinicioProces() != null  && camp.getExpedientTipus() == null ) {
			// Variable definició de procés
			varCodi = camp.getDefinicioProces().getJbpmKey()+"/"+camp.getCodi();
		} else {
			// Variable expedient o tipus d'expedient
			varCodi = camp.getCodi();
		}
		varCodi = varCodi.replace(Constants.EXPEDIENT_CAMP_ESTAT, Constants.EXPEDIENT_CAMP_ESTAT_JSP);
		tascaDto.setVarCodi(varCodi);
		tascaDto.setCampId(camp.getId());
		tascaDto.setCampTipus(conversioTipusServiceHelper.convertir(camp.getTipus(), CampTipusDto.class));
		tascaDto.setCampEtiqueta(camp.getEtiqueta());
		tascaDto.setCampMultiple(camp.isMultiple());
		tascaDto.setObservacions(camp.getObservacions());
		tascaDto.setJbpmAction(camp.getJbpmAction());
		tascaDto.setValidacions(conversioTipusServiceHelper.convertirList(camp.getValidacions(), ValidacioDto.class));
		
		if (TipusCamp.SELECCIO.equals(camp.getTipus()) || TipusCamp.SUGGEST.equals(camp.getTipus())) {
			try {
				tascaDto.setVarValor(
						getPossiblesValorsCamp(
								camp,
								null,
								null,
								null,
								null,
								null,
								null,
								consultesDomini,
								tascaDto));
			} catch (Exception e) {
				tascaDto.setVarValor(null);
			}
		}
		if (camp.isMultiple()) {
			List<TascaDadaDto> multipleDades = new ArrayList<TascaDadaDto>();
			/*for (CampRegistre dto: camp.getRegistreMembres()) {
				multipleDades.add(getTascaDadaDtoParaConsultaDisseny(dto.getMembre()));
			}*/
			tascaDto.setMultipleDades(multipleDades);
		}
		
		List<TascaDadaDto> registreDades = new ArrayList<TascaDadaDto>();
		/*for (CampRegistre dto: camp.getRegistrePares()) {
			registreDades.add(getTascaDadaDtoParaConsultaDisseny(dto.getRegistre()));
		}*/
		tascaDto.setRegistreDades(registreDades);
		return tascaDto;
	}

	public Object getDescripcioVariable(String taskId, String processInstanceId, String codi) throws Exception {
		Object valor = null;
		if (taskId != null)
			valor = workflowEngineApi.getTaskInstanceVariable(taskId, Constants.PREFIX_VAR_DESCRIPCIO + codi);
		if (valor == null)
			valor = workflowEngineApi.getProcessInstanceVariable(processInstanceId, Constants.PREFIX_VAR_DESCRIPCIO + codi);
		return valor;
	}

	private ExpedientDadaDto getDadaPerVariableJbpm(
			Camp camp,
			String varCodi,
			Object varValor,
			Map<String, Object> valorsAddicionals,
			String taskInstanceId,
			String processInstanceId,
			boolean forsarSimple,
			Map<Integer, ConsultaDominiDto> consultesDomini) {
		ExpedientDadaDto dto = new ExpedientDadaDto();
		dto.setVarCodi(varCodi);
		dto.setVarValor(valorVariableJbpmRevisat(varValor));
		if (camp != null) {
			dto.setCampId(camp.getId());
			dto.setVarCodi(camp.getCodi());
			dto.setCampEtiqueta(camp.getEtiqueta());
			dto.setCampTipus(CampTipusDto.valueOf(camp.getTipus().name()));
			dto.setCampMultiple(camp.isMultiple());
			dto.setCampOcult(camp.isOcult());
			dto.setObservacions(camp.getObservacions());
			dto.setJbpmAction(camp.getJbpmAction());
			if (camp.getOrdre() != null)
				dto.setOrdre(camp.getOrdre());
			if (camp.getAgrupacio() != null)
				dto.setAgrupacioId(camp.getAgrupacio().getId());
			if (camp.getValidacions() != null)
				dto.setValidacions(conversioTipusServiceHelper.convertirList(camp.getValidacions(), ValidacioDto.class));
		} else {
			dto.setCampEtiqueta(varCodi);
			dto.setText(String.valueOf(varValor));
			dto.setCampTipus(CampTipusDto.STRING);
		}
		boolean esCampTipusAccio = camp != null && TipusCamp.ACCIO.equals(camp.getTipus());
		if (camp != null && !esCampTipusAccio) {
			try {
				if (!camp.isMultiple() || forsarSimple) {
					if (TipusCamp.REGISTRE.equals(camp.getTipus())) {
						List<ExpedientDadaDto> registreDades = new ArrayList<ExpedientDadaDto>();
						Object[] valorsRegistres;
						if (varValor instanceof Dada) {
							valorsRegistres = (Object[]) ((Dada) varValor).getValorsAsObject();
						} else {
							valorsRegistres = (Object[]) varValor;
						}
						// Construeix el map per als valors addicionals dels parámetres del domini
						Map<String, Object> valorsAddicionalsConsulta = new HashMap<String, Object>();
						if (valorsRegistres != null)
							for (int j = 0; j < camp.getRegistreMembres().size(); j++) {
								if (j < Array.getLength(valorsRegistres)) {
									valorsAddicionalsConsulta.put(
											camp.getRegistreMembres().get(j).getMembre().getCodi(),
											Array.get(valorsRegistres, j));
								}
							}
						int index = 0;
						// Consulta el valor per cada dada del registre
						for (CampRegistre campRegistre: camp.getRegistreMembres()) {							
							Object valorsRegistre = null;
							if (valorsRegistres != null && valorsRegistres.length>index) {
								valorsRegistre = valorsRegistres[index];
							}
							ExpedientDadaDto dtoRegistre = getDadaPerVariableJbpm(
									campRegistre.getMembre(),
									campRegistre.getMembre().getCodi(),
									valorsRegistre,
									valorsAddicionalsConsulta,
									taskInstanceId,
									processInstanceId,
									false, 
									consultesDomini);
							if (dtoRegistre.getText() == null && dtoRegistre.getMultipleDades() != null && dtoRegistre.getMultipleDades().size() == 1) {
								dtoRegistre.setText(dtoRegistre.getMultipleDades().get(0).getText());
							}
							dtoRegistre.setRequired(campRegistre.isObligatori());
							dtoRegistre.setOrdre(campRegistre.getOrdre());
							dtoRegistre.setLlistar(campRegistre.isLlistar());
							index += 1;
							registreDades.add(dtoRegistre);
						}
						dto.setRegistreDades(registreDades);
					} else {
						try {
							dto.setText(
									getTextPerCamp(
											camp,
											varValor,
											valorsAddicionals,
											taskInstanceId,
											processInstanceId,
											consultesDomini,
											dto));
							if (dto.getText() == null || dto.getText().isEmpty()) {
								dto.setText("");
							}
							if (dto.getVarValor() instanceof ParellaCodiValor) {
								dto.setVarValor(((ParellaCodiValor) dto.getVarValor()).getCodi());
							}
						} catch (SistemaExternException ex) {
							dto.setText("");
							logger.error("Error al obtenir text per la dada de l'expedient (processInstanceId=" + processInstanceId + ", variable=" + camp.getCodi() + ")", ex);
							dto.setError(ex.getPublicMessage());
						}
					}
				} else {
					Object[] valorsMultiples = null;
					// Comprovam que el valor desat actual és de tipus array. En cas contrari el convertim a array
					if (varValor == null || varValor instanceof Object[]) {
						valorsMultiples = (Object[])varValor;
					} else { 
						valorsMultiples = new Object[] {varValor};
					}	
					List<ExpedientDadaDto> multipleDades = new ArrayList<ExpedientDadaDto>();
					if (valorsMultiples != null) {
						for (Object valor : valorsMultiples) {
							ExpedientDadaDto dtoMultiple = getDadaPerVariableJbpm(
									camp,
									varCodi,
									valor,
									null,
									taskInstanceId,
									processInstanceId,
									true, 
									consultesDomini);
							multipleDades.add(dtoMultiple);
						}
					} else {
						ExpedientDadaDto dtoMultiple = getDadaPerVariableJbpm(
								camp,
								varCodi,
								null,
								null,
								taskInstanceId,
								processInstanceId,
								true, 
								consultesDomini);
						multipleDades.add(dtoMultiple);
					}
					dto.setMultipleDades(multipleDades);
				}
				if (camp.getDominiParams() != null) {
					String dominiParams = camp.getDominiParams();
					String[] pairs = dominiParams.split(";");
					List<String> paramCampCodis = new ArrayList<String>();
					for (String pair: pairs) {
						String[] parts = pair.split(":");
						if (parts.length >= 2) {
							String campCodi = parts[1];
							if (!campCodi.startsWith("@") && !campCodi.startsWith("#{")) {
								paramCampCodis.add(campCodi);
							}
						}
					}
					dto.setCampParams(
							paramCampCodis.toArray(
									new String[paramCampCodis.size()]));
				}			} catch (Exception ex) {
				logger.error("Error al processar dada de l'expedient (processInstanceId=" + processInstanceId + ", variable=" + varCodi + ")", ex);
				StringBuilder sb = new StringBuilder();
				getClassAsString(sb, varValor);
				logger.info("Detalls del valor: " + sb.toString());
				dto.setError(ex.getMessage());
			}
		}
		return dto;
	}

	private Object valorVariableJbpmRevisat(Object valor) {
		// TODO: pensar què fer amb DominiCodiDescripcio
//		if (valor instanceof DominiCodiDescripcio) {
//			return ((DominiCodiDescripcio)valor).getCodi();
//		} else {
//			return valor;
//		}
		return valor;
	}

	private List<Dada> filtrarVariablesUsIntern(List<Dada> dades) {
		return dades.stream()
				.filter(dada -> !dada.getCodi().equals(Constants.VAR_TASCA_VALIDADA))
				.filter(dada -> !dada.getCodi().equals(Constants.VAR_TASCA_DELEGACIO))
				.filter(dada -> !dada.getCodi().startsWith(Constants.PREFIX_DOCUMENT))
				.filter(dada -> !dada.getCodi().startsWith(Constants.PREFIX_SIGNATURA))
				.filter(dada -> !dada.getCodi().startsWith(Constants.PREFIX_ADJUNT))
				.filter(dada -> !dada.getCodi().startsWith(Constants.PREFIX_VAR_DESCRIPCIO))
				.filter(dada -> !dada.getCodi().startsWith(VariableHelper.PARAMS_RETROCEDIR_VARIABLE_PREFIX))
				.collect(Collectors.toList());
	}

	// TODO borrar mètode
//	private void filtrarVariablesUsIntern(Map<String, Object> variables) {
//		if (variables == null) {
//			return;
//		}
//		variables.remove(Constants.VAR_TASCA_VALIDADA);
//		variables.remove(Constants.VAR_TASCA_DELEGACIO);
//		List<String> codisEsborrar = new ArrayList<>();
//		for (String codi: variables.keySet()) {
//			if (	codi.startsWith(Constants.PREFIX_DOCUMENT) ||
//					codi.startsWith(Constants.PREFIX_SIGNATURA) ||
//					codi.startsWith(Constants.PREFIX_ADJUNT) ||
//					codi.startsWith(Constants.PREFIX_VAR_DESCRIPCIO) ||
//					codi.startsWith(VariableHelper.PARAMS_RETROCEDIR_VARIABLE_PREFIX)) {
//				codisEsborrar.add(codi);
//			}
//		}
//		for (String codi: codisEsborrar) {
//			variables.remove(codi);
//		}
//	}

	private void getClassAsString(StringBuilder sb, Object o) {
		if (o.getClass().isArray()) {
			sb.append("[");
			int length = Array.getLength(o);
			for (int i = 0; i < length; i++) {
				getClassAsString(sb, Array.get(o, i));
				if (i < length - 1)
					sb.append(", ");
			}
			sb.append("]");
		} else {
			sb.append(o.getClass().getName());
		}
	}

	private Object getValorCampRegistre(
			String taskInstanceId,
			String processInstanceId,
			Camp registreCamp,
			Integer registreIndex,
			String campCodi) throws Exception {
		Object registreValor = null;
		if (taskInstanceId != null)
			registreValor = getVariableJbpmTascaValor(
					taskInstanceId,
					registreCamp.getCodi());
		else if (processInstanceId != null)
			registreValor = getVariableJbpmProcesValor(
					processInstanceId,
					registreCamp.getCodi());
		if (registreValor != null) {
			Object[] filaValor = null;
			if (registreCamp.isMultiple() && registreValor instanceof Object[][]) {
				Object[][] registreArray = (Object[][])registreValor;
				if (registreArray.length > registreIndex)
					filaValor = registreArray[registreIndex];
			}
			if (!registreCamp.isMultiple() && registreValor instanceof Object[]) {
				if (registreIndex == null || registreIndex == 0)
					filaValor = (Object[])registreValor;
			}
			if (filaValor != null) {
				Object valor = null;
				int index = 0;
				for (CampRegistre campRegistre: registreCamp.getRegistreMembres()) {
					if (campCodi.equals(campRegistre.getMembre().getCodi())) {
						valor = filaValor[index];
						break;
					}
					index++;
				}
				return valor;
			}
		}
		return null;
	}

	private ParellaCodiValor getTextPerCampAmbValor(
			Camp camp,
			Object valor,
			Map<String, Object> valorsAddicionals,
			String taskInstanceId,
			String processInstanceId, 
			Map<Integer, ConsultaDominiDto> consultesDominis,
			Object dadaDto) throws Exception {
		if (valor == null)
			return null;

		//mirem si hi ha una variable amb la texte, utilitzant el prefix PREFIX_VAR_DESCRIPCIO
		if (valor instanceof ParellaCodiValor)
			if (camp.isDominiCacheText()) {
				return (ParellaCodiValor) valor;

//			Object descVariable = getDescripcioVariable(taskInstanceId, processInstanceId, camp.getCodi());
//			if (descVariable != null) {
//				return new ParellaCodiValor(
//						(String)valor,
//						(String)descVariable);
//			}
			} else {
				valor = ((ParellaCodiValor) valor).getCodi();
			}
		//////////
		
		//TODO pensar què fer amb DominiCodiDescripcio
//		if (valor instanceof DominiCodiDescripcio) {
//			return new ParellaCodiValor(
//					((DominiCodiDescripcio)valor).getCodi(),
//					((DominiCodiDescripcio)valor).getDescripcio());
//		}
		
		ParellaCodiValor resultat = null;
		List<ParellaCodiValor> lista = getPossiblesValorsCamp(
				camp,
				null,
				null,
				valor,
				valorsAddicionals,
				taskInstanceId,
				processInstanceId,
				consultesDominis,
				dadaDto);
		if (!lista.isEmpty()) {
			// Cerca el valor resultant
			for (ParellaCodiValor parellaCodiValor : lista){
				if (parellaCodiValor.getCodi().equals(valor.toString()))
					resultat = parellaCodiValor;
			}
		}
		return resultat;
	}

	/** Mètode per agrupar les consultes de domini en una sola i consultar tots els camps de tipus selecció
	 * o suggest sobre un domini a la vegada emprant el micro servei de dominis.
	 * 
	 * @param consultesDomini És un Map<identificador, ConsultaDominiDto> on cada consulta
	 * té un identificador y una llista de dades per fixar el valor consultat posteriorment.
	 */
	public void consultaDominisAgrupats(Map<Integer, ConsultaDominiDto> consultesDomini) {
		
		if (consultesDomini == null || consultesDomini.isEmpty()) {
			return;
		}
		
		ConsultaDominiDto consultaDomini;
		List<FilaResultat> resultat;
		// Realitza la consulta múltiple
		Map<Integer, List<FilaResultat>> resultats = dominiMs.consultarDominis(new ArrayList<ConsultaDominiDto>(consultesDomini.values()));
		// Processa les resposta múltiple
		for (Integer identificador : consultesDomini.keySet()) {
			consultaDomini = consultesDomini.get(identificador);
			resultat = resultats.get(identificador);
			
			// Processa els resultats
			String text = null;
			if (resultat != null && resultat.size() > 0) {
				List<ParellaCodiValor> parellesCodiValor =
						this.findParellaCodiValor(
							consultaDomini.getCampTipus(), 
							consultaDomini.getDominiTipus(), 
							resultat, 
							consultaDomini.getCampCodi(), 
							consultaDomini.getCampText(), 
							consultaDomini.getValor());
				if (parellesCodiValor.size() > 0) {
					text = parellesCodiValor.get(0).getValor() != null ?
							parellesCodiValor.get(0).getValor().toString()
							: "";
				}
				if (text != null) {
					// Estableix el text a les dades
					for (Object dadaDto : consultaDomini.getDadesDto()) {
						if (dadaDto instanceof ExpedientDadaDto) {
							((ExpedientDadaDto) dadaDto).setText(text);
						} else if (dadaDto instanceof TascaDadaDto) {
							((TascaDadaDto) dadaDto).setText(text);
						} else if (dadaDto instanceof DadaIndexadaDto) {
							((TascaDadaDto) dadaDto).setText(text);				
						}
					}
				}
			}
		}
	}
	
	/** Mètode per trobar entre el resultat de la consulta la fila amb el codi corresponent
	 * al valor.
	 * @param dominiTipus
	 * 
	 * @param resultatConsultaDomini
	 * @param columnaCodi
	 * @param columnaValor
	 * @param valor
	 * @return
	 */
	public List<ParellaCodiValor> findParellaCodiValor(
			CampTipusDto campTipus,
			TipusDomini dominiTipus, 
			List<FilaResultat> resultatConsultaDomini, 
			String columnaCodi,
			String columnaValor, 
			Object valor) {
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		if (resultatConsultaDomini != null) {
			Iterator<FilaResultat> it = resultatConsultaDomini.iterator();
			while (it.hasNext()) {
				FilaResultat fr = it.next();
				for (ParellaCodiValor parellaCodi: fr.getColumnes()) {
					boolean ignoreCase = TipusDomini.CONSULTA_SQL.equals(dominiTipus);
					boolean matches = (ignoreCase) ? parellaCodi.getCodi().equalsIgnoreCase(columnaCodi) : parellaCodi.getCodi().equals(columnaCodi);
					if (matches &&
							(
								valor == null || 
								parellaCodi.getValor().toString().equals(valor) ||
								(TipusCamp.SUGGEST.equals(campTipus) && parellaCodi.getValor().toString().toUpperCase().indexOf(valor.toString().toUpperCase()) != -1)
							)
						) {
						for (ParellaCodiValor parellaValor: fr.getColumnes()) {
							matches = (ignoreCase) ? parellaValor.getCodi().equalsIgnoreCase(columnaValor) : parellaValor.getCodi().equals(columnaValor);
							if (matches) {
								resposta.add(new ParellaCodiValor(
										parellaCodi.getValor().toString(),
										parellaValor.getValor()));
								if (valor != null) {
									break;
								}
							}
						}
						break;
					}
				}
			}
		}		
		return resposta;
	}

	public CampTascaDto getCampTascaPerInstanciaTasca(
			String taskName,
			String processDefinitionId,
			String processInstanceId,
			String variableCodi) {
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(processDefinitionId);
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				taskName,
				definicioProces);
		ExpedientTipus expedientTipus = expedientTipusHelper.findAmbProcessInstanceId(processInstanceId);
		CampTasca campTasca = campTascaRepository.findAmbTascaCodi(
				tasca.getId(),
				variableCodi,
				expedientTipus.getId());
		return conversioTipusServiceHelper.convertir(campTasca, CampTascaDto.class);
	}

	private static final Logger logger = LoggerFactory.getLogger(VariableHelper.class);

}
