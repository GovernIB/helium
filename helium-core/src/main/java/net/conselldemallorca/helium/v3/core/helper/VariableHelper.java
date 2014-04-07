/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.jbpm3.handlers.BasicActionHandler;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto.TipusAuthDomini;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto.TipusDomini;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ValidacioDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Helper per a gestionar les variables dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class VariableHelper {

	public static final String PREFIX_VAR_DOCUMENT = DocumentHelper.PREFIX_VAR_DOCUMENT;
	public static final String PREFIX_VAR_ADJUNT = DocumentHelper.PREFIX_ADJUNT;
	public static final String PREFIX_VAR_SIGNATURA = DocumentHelper.PREFIX_SIGNATURA;

	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private DominiHelper dominiHelper;
	@Resource(name="expedientServiceV3")
	private ExpedientService expedientService;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;


	public Object getVariableJbpmTascaValor(
			String taskId,
			String varCodi) {
		Object valor = jbpmHelper.getTaskInstanceVariable(taskId, varCodi);
		return valorVariableJbpmRevisat(valor);
	}
	public Map<String, Object> getVariablesJbpmTascaValor(
			String taskId) {
		Map<String, Object> valors = jbpmHelper.getTaskInstanceVariables(taskId);
		Map<String, Object> valorsRevisats = new HashMap<String, Object>();
		for (String varCodi: valors.keySet()) {
			Object valor = valors.get(varCodi);
			valorsRevisats.put(varCodi, valorVariableJbpmRevisat(valor));
		}
		return valorsRevisats;
	}
	public Object getVariableJbpmProcesValor(
			String processInstanceId,
			String varCodi) {
		Object valor = jbpmHelper.getProcessInstanceVariable(processInstanceId, varCodi);
		return valorVariableJbpmRevisat(valor);
	}
	public Map<String, Object> getVariablesJbpmProcesValor(
			String processInstanceId) {
		Map<String, Object> valors = jbpmHelper.getProcessInstanceVariables(processInstanceId);
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
	
	public List<ExpedientDadaDto> findDadesPerInstanciaProces(String processInstanceId) {
		String tipusExp = null;
		if (MesuresTemporalsHelper.isActiu()) {
			Expedient exp = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
			tipusExp = (exp != null ? exp.getTipus().getNom() : null);
			mesuresTemporalsHelper.mesuraIniciar("Expedient DADES v3", "expedient", tipusExp);
			mesuresTemporalsHelper.mesuraIniciar("Expedient DADES v3", "expedient", tipusExp, null, "0");
		}
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				processInstanceId);
		Set<Camp> camps = definicioProces.getCamps();
		Map<String, Camp> campsIndexatsPerCodi = new HashMap<String, Camp>();
		for (Camp camp: camps)
			campsIndexatsPerCodi.put(camp.getCodi(), camp);
		mesuresTemporalsHelper.mesuraCalcular("Expedient DADES v3", "expedient", tipusExp, null, "0");
		mesuresTemporalsHelper.mesuraIniciar("Expedient DADES v3", "expedient", tipusExp, null, "1");
		List<ExpedientDadaDto> resposta = new ArrayList<ExpedientDadaDto>();
		Map<String, Object> varsInstanciaProces = jbpmHelper.getProcessInstanceVariables(
				processInstanceId);
		mesuresTemporalsHelper.mesuraCalcular("Expedient DADES v3", "expedient", tipusExp, null, "1");
		if (varsInstanciaProces != null) {
			mesuresTemporalsHelper.mesuraIniciar("Expedient DADES v3", "expedient", tipusExp, null, "2");
			filtrarVariablesUsIntern(varsInstanciaProces);
			for (String var: varsInstanciaProces.keySet()) {
				boolean varAmbContingut = varsInstanciaProces.get(var) != null;
				Camp camp = campsIndexatsPerCodi.get(var);
				if (varAmbContingut && camp != null && (TipusCamp.REGISTRE.equals(camp.getTipus()) || camp.isMultiple())) {
					Object[] registreValors = (Object[])varsInstanciaProces.get(var);
					varAmbContingut = registreValors.length > 0;
				}
				if (varAmbContingut) {
					ExpedientDadaDto dto = getDadaPerVariableJbpm(
							camp,
							var,
							varsInstanciaProces.get(var),
							null,
							processInstanceId,
							false);
					resposta.add(dto);
				}
			}
			mesuresTemporalsHelper.mesuraCalcular("Expedient DADES v3", "expedient", tipusExp, null, "2");
		}
		mesuresTemporalsHelper.mesuraCalcular("Expedient DADES v3", "expedient", tipusExp);
		return resposta;
	}
	public ExpedientDadaDto getDadaPerInstanciaProces(
			String processInstanceId,
			String variableCodi) {
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				processInstanceId);
		Camp camp = campRepository.findByDefinicioProcesAndCodi(
				definicioProces,
				variableCodi);
		Object valor = jbpmHelper.getProcessInstanceVariable(
				processInstanceId,
				variableCodi);
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
					processInstanceId,
					false);
			return dto;
		} else {
			return null;
		}
	}

	public List<TascaDadaDto> findDadesPerInstanciaTascaDto(ExpedientTascaDto tasca) {		
		List<TascaDadaDto> resposta = new ArrayList<TascaDadaDto>();
		
		for (CampTasca campTasca: campTascaRepository.findAmbTascaOrdenats(Long.valueOf(tasca.getTascaId()))) {
			Camp camp = campTasca.getCamp();
			
			ExpedientDadaDto expedientDadaDto = getDadaPerVariableJbpm(
					camp,
					camp.getCodi(),
					null,
					tasca.getId(),
					null,
					false);
			resposta.add(
					getTascaDadaDtoFromExpedientDadaDto(
							expedientDadaDto,
							campTasca));
		}
		return resposta;
	}
	
	public List<TascaDadaDto> findDadesPerInstanciaTasca(
			JbpmTask task) {
		String tipusExp = null;
		if (MesuresTemporalsHelper.isActiu()) {
			Expedient exp = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
			tipusExp = (exp != null ? exp.getTipus().getNom() : null);
			mesuresTemporalsHelper.mesuraIniciar("Tasca DADES v3", "tasques", tipusExp, task.getName());
			mesuresTemporalsHelper.mesuraIniciar("Tasca DADES v3", "tasques", tipusExp, task.getName(), "0");
		}
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				task.getProcessInstanceId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getName(),
				definicioProces);
		List<CampTasca> campsTasca = tasca.getCamps();
		Map<String, CampTasca> campsIndexatsPerCodi = new HashMap<String, CampTasca>();
		for (CampTasca campTasca: campsTasca) {
			campsIndexatsPerCodi.put(
					campTasca.getCamp().getCodi(),
					campTasca);
		}
		mesuresTemporalsHelper.mesuraCalcular("Tasca DADES v3", "tasques", tipusExp, task.getName(), "0");
		mesuresTemporalsHelper.mesuraIniciar("Tasca DADES v3", "tasques", tipusExp, task.getName(), "1");
		List<TascaDadaDto> resposta = new ArrayList<TascaDadaDto>();
		Map<String, Object> varsInstanciaTasca = jbpmHelper.getTaskInstanceVariables(
				task.getId());
		mesuresTemporalsHelper.mesuraCalcular("Tasca DADES v3", "tasques", tipusExp, task.getName(), "1");
		mesuresTemporalsHelper.mesuraIniciar("Tasca DADES v3", "tasques", tipusExp, task.getName(), "2");
		// Només es mostraran les variables donades d'alta al formulari
		// de la tasca. Les variables jBPM de la tasca que no siguin
		// al formulari no es mostraran.
		for (CampTasca campTasca: campsTasca) {
			Camp camp = campTasca.getCamp();
			ExpedientDadaDto expedientDadaDto = getDadaPerVariableJbpm(
					camp,
					camp.getCodi(),
					varsInstanciaTasca.get(camp.getCodi()),
					task.getId(),
					task.getProcessInstanceId(),
					false);
			resposta.add(
					getTascaDadaDtoFromExpedientDadaDto(
							expedientDadaDto,
							campTasca));
		}
		mesuresTemporalsHelper.mesuraCalcular("Tasca DADES v3", "tasques", tipusExp, task.getName(), "2");
		mesuresTemporalsHelper.mesuraCalcular("Tasca DADES v3", "tasques", tipusExp, task.getName());
		return resposta;
	}
	public ExpedientDadaDto getDadaPerInstanciaTasca(
			String taskInstanceId,
			String variableCodi) {
		JbpmTask task = jbpmHelper.getTaskById(taskInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		Camp camp = campRepository.findByDefinicioProcesAndCodi(
				definicioProces,
				variableCodi);
		Object valor = jbpmHelper.getTaskInstanceVariable(
				taskInstanceId,
				variableCodi);
		if (valor == null) {
			valor = jbpmHelper.getProcessInstanceVariable(
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
					taskInstanceId,
					task.getProcessInstanceId(),
					false);
			return dto;
		} else {
			return null;
		}
	}



	private ExpedientDadaDto getDadaPerVariableJbpm(
			Camp camp,
			String varCodi,
			Object varValor,
			String taskInstanceId,
			String processInstanceId,
			boolean forsarSimple) {
		ExpedientDadaDto dto = new ExpedientDadaDto();
		dto.setVarCodi(varCodi);
		dto.setVarValor(varValor);		
		if (camp != null) {
			dto.setCampId(camp.getId());
			dto.setVarCodi(camp.getCodi());
			dto.setCampEtiqueta(camp.getEtiqueta());
			dto.setCampTipus(CampTipusDto.valueOf(camp.getTipus().name()));
			dto.setCampMultiple(camp.isMultiple());
			dto.setCampOcult(camp.isOcult());
			dto.setObservacions(camp.getObservacions());
			dto.setJbpmAction(camp.getJbpmAction());
			if (camp.getAgrupacio() != null)
				dto.setAgrupacioId(camp.getAgrupacio().getId());
		} else {
			dto.setCampEtiqueta(varCodi);
			dto.setText((String) varValor);
			dto.setCampTipus(CampTipusDto.STRING);
		}
		boolean esCampTipusAccio = camp != null && TipusCamp.ACCIO.equals(camp.getTipus());
		if (camp != null && !esCampTipusAccio) {
			try {
				if (!camp.isMultiple() || forsarSimple) {
					if (TipusCamp.REGISTRE.equals(camp.getTipus())) {
						List<ExpedientDadaDto> registreDades = new ArrayList<ExpedientDadaDto>();
						Object[] valorsRegistres = (Object[])varValor;
						int index = 0;
						for (CampRegistre campRegistre: camp.getRegistreMembres()) {							
							Object valorsRegistre = null;
							if (valorsRegistres != null && valorsRegistres.length>index) {
								valorsRegistre = valorsRegistres[index];
							}
							ExpedientDadaDto dtoRegistre = getDadaPerVariableJbpm(
									campRegistre.getMembre(),
									campRegistre.getMembre().getCodi(),
									valorsRegistre,
									taskInstanceId,
									processInstanceId,
									false);
							index += 1;
							registreDades.add(dtoRegistre);
						}
						dto.setRegistreDades(registreDades);
					} else {
						try {
							dto.setText(
									getTextVariableSimple(
											camp,
											varValor,
											null,
											taskInstanceId,
											processInstanceId));
							if (dto.getText() == null || dto.getText().isEmpty()) {
								dto.setText("");
							}							
						} catch (Exception ex) {
							dto.setText("[!]");
							logger.error("Error al obtenir text per la dada de l'expedient (processInstanceId=" + processInstanceId + ", variable=" + camp.getCodi() + ")", ex);
							dto.setError(ex.getMessage());
						}
					}
				} else {
					Object[] valorsMultiples = (Object[])varValor;
					List<ExpedientDadaDto> multipleDades = new ArrayList<ExpedientDadaDto>();
					if (valorsMultiples != null) {
						for (Object valor : valorsMultiples) {
							ExpedientDadaDto dtoMultiple = getDadaPerVariableJbpm(
									camp,
									varCodi,
									valor,
									taskInstanceId,
									processInstanceId,
									true);
							multipleDades.add(dtoMultiple);
						}
					} else {
						ExpedientDadaDto dtoMultiple = getDadaPerVariableJbpm(
								camp,
								varCodi,
								null,
								taskInstanceId,
								processInstanceId,
								true);
						multipleDades.add(dtoMultiple);
					}
					dto.setMultipleDades(multipleDades);
				}
			} catch (Exception ex) {
				logger.error("Error al processar dada de l'expedient (processInstanceId=" + processInstanceId + ", variable=" + varCodi + ")", ex);
				StringBuilder sb = new StringBuilder();
				getClassAsString(sb, varValor);
				logger.info("Detalls del valor: " + sb.toString());
				dto.setError(ex.getMessage());
			}
		}
		return dto;
	}
	
	public String getTextVariableSimple(
			Camp camp,
			Object valor,
			Map<String, Object> valorsAddicionals,
			String taskInstanceId,
			String processInstanceId) {
		if (valor == null)
			return null;
		String valorFontExterna = null;
		if (TipusCamp.SELECCIO.equals(camp.getTipus()) || TipusCamp.SUGGEST.equals(camp.getTipus())) {
			try {					
				valorFontExterna = (String) getTextVariableSimpleFontExterna(
						camp,
						valor,
						null,
						taskInstanceId,
						processInstanceId).getValor();
			} catch (Exception e) {
				return null;
			}
		}
		return Camp.getComText(
				camp.getTipus(),
				valor,
				valorFontExterna);
	}
	
	public ParellaCodiValorDto getTextVariableSimpleFontExterna(
			Camp camp,
			Object valor,
			Map<String, Object> valorsAddicionals,
			String taskInstanceId,
			String processInstanceId) throws Exception {
		if (valor == null)
			return null;
		if (valor instanceof DominiCodiDescripcio) {
			return new ParellaCodiValorDto(
					((DominiCodiDescripcio)valor).getCodi(),
					((DominiCodiDescripcio)valor).getDescripcio());
		}
		
		List<ParellaCodiValorDto> lista = getTextVariablesSimpleFontExterna(
				camp,
				valor,
				valorsAddicionals,
				taskInstanceId,
				processInstanceId);
		
		if (!lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}
	
	public List<ParellaCodiValorDto> getTextVariablesSimpleFontExterna(
			Camp camp,
			Object valor,
			Map<String, Object> valorsAddicionals,
			String taskInstanceId,
			String processInstanceId) throws Exception {

		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		TipusCamp tipus = camp.getTipus();
		if (tipus.equals(TipusCamp.SELECCIO) || tipus.equals(TipusCamp.SUGGEST)) {
			if (camp.getDomini() != null || camp.isDominiIntern()) {
				DominiDto domini;
				if (camp.isDominiIntern()) {
					EntornDto entorn = conversioTipusHelper.convertir(camp.getDefinicioProces().getEntorn(), EntornDto.class);
					domini = findAmbEntornICodi(entorn, "intern");
				} else {
					domini = new DominiDto();
					domini.setCacheSegons(camp.getDomini().getCacheSegons());
					domini.setTipus(conversioTipusHelper.convertir(camp.getDomini().getTipus(),TipusDomini.class));
					domini.setId(camp.getDomini().getId());
					domini.setSql(camp.getDomini().getSql());
					domini.setJndiDatasource(camp.getDomini().getJndiDatasource());
				}
				try {
					List<FilaResultat> resultatConsultaDomini = dominiHelper.consultar(
							domini,
							camp.getDominiId(),
							getParamsConsulta(
									taskInstanceId,
									processInstanceId,
									camp,
									valorsAddicionals));
					String columnaCodi = camp.getDominiCampValor();
					String columnaValor = camp.getDominiCampText();
					Iterator<FilaResultat> it = resultatConsultaDomini.iterator();
					while (it.hasNext()) {
						FilaResultat fr = it.next();
						for (ParellaCodiValor parellaCodi: fr.getColumnes()) {
							if (parellaCodi.getCodi().equals(columnaCodi) && (valor == null || parellaCodi.getValor().toString().equals(valor))) {
								for (ParellaCodiValor parellaValor: fr.getColumnes()) {
									if (parellaValor.getCodi().equals(columnaValor)) {
										ParellaCodiValorDto codiValor = new ParellaCodiValorDto(
												parellaCodi.getValor().toString(),
												parellaValor.getValor());
										resposta.add(codiValor);
										if (valor != null) {
											break;
										}
									}
								}
								break;
							}
						}
					}
				} catch (Exception ex) {
					throw new Exception("Error en la consulta del domini (processInstanceId=" + processInstanceId + ", dominiCodi=" + domini.getCodi() + ", variable=" + camp.getCodi() + "): " + ex.getMessage(), ex);
				}
			} else if (camp.getEnumeracio() != null) {
				Enumeracio enumeracio = camp.getEnumeracio();
				for (EnumeracioValors enumValor: enumeracio.getEnumeracioValors()) {
					if (valor == null || valor.equals(enumValor.getCodi())) {
						resposta.add(new ParellaCodiValorDto(
								enumValor.getCodi(),
								enumValor.getNom()));
						if (valor != null) {
							break;
						}
					}
				}
			} else if (camp.getConsulta() != null) {
				Consulta consulta = camp.getConsulta();
				List<ExpedientConsultaDissenyDto> dadesExpedients = expedientService.findAmbEntornConsultaDisseny(
						consulta.getEntorn().getId(),
						consulta.getId(),
						new HashMap<String, Object>(),
						null);
				
				Iterator<ExpedientConsultaDissenyDto> it = dadesExpedients.iterator();
				while(it.hasNext()){
					ExpedientConsultaDissenyDto exp = it.next();
					DadaIndexadaDto valorDto = exp.getDadesExpedient().get(camp.getConsultaCampValor());
					if(valorDto == null){
						valorDto = exp.getDadesExpedient().get(consulta.getExpedientTipus().getJbpmProcessDefinitionKey()+"/"+camp.getConsultaCampValor());
					}
					if(valorDto != null){
						if (valorDto.getValor().toString().equals(valor)){
							DadaIndexadaDto textDto = exp.getDadesExpedient().get(camp.getConsultaCampText());
							if(textDto == null){
								textDto = exp.getDadesExpedient().get(consulta.getExpedientTipus().getJbpmProcessDefinitionKey()+"/"+camp.getConsultaCampText());
							}
							resposta.add(new ParellaCodiValorDto(
									valorDto.getValorMostrar(),
									textDto.getValorMostrar()
									));
							if (valor != null) {
								break;
							}
						}
					}
				}
			}
		}
		return resposta;
	}
	public DominiDto findAmbEntornICodi(EntornDto entorn, String codi) {
		DominiDto domini = new DominiDto();
		domini.setId((long) 0);
		domini.setCacheSegons(30);
		domini.setCodi("intern");
		domini.setNom("Domini intern");
		domini.setTipus(TipusDomini.CONSULTA_WS);
		domini.setTipusAuth(TipusAuthDomini.NONE);
		domini.setEntorn(entorn);
		domini.setUrl(GlobalProperties.getInstance().getProperty("app.domini.intern.url","http://localhost:8080/helium/ws/DominiIntern"));
		return domini;
	}

	public Map<String, Object> getParamsConsulta(
			String taskInstanceId,
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
					value = jbpmHelper.evaluateExpression(
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
				if (value == null && taskInstanceId != null)
					value = getVariableJbpmTascaValor(taskInstanceId, campCodi);
				if (value == null && processInstanceId != null)
					value = getVariableJbpmProcesValor(processInstanceId, campCodi);
			}
			if (value != null)
				params.put(paramCodi, value);
		}
		return params;
	}

	private Object valorVariableJbpmRevisat(Object valor) {
		if (valor instanceof DominiCodiDescripcio) {
			return ((DominiCodiDescripcio)valor).getCodi();
		} else {
			return valor;
		}
	}

	private void filtrarVariablesUsIntern(Map<String, Object> variables) {
		if (variables != null) {
			variables.remove(TascaService.VAR_TASCA_VALIDADA);
			variables.remove(TascaService.VAR_TASCA_DELEGACIO);
			List<String> codisEsborrar = new ArrayList<String>();
			for (String codi: variables.keySet()) {
				if (	codi.startsWith(PREFIX_VAR_DOCUMENT) ||
						codi.startsWith(PREFIX_VAR_SIGNATURA) ||
						codi.startsWith(PREFIX_VAR_ADJUNT) ||
						codi.startsWith(BasicActionHandler.PARAMS_RETROCEDIR_VARIABLE_PREFIX))
					codisEsborrar.add(codi);
			}
			for (String codi: codisEsborrar)
				variables.remove(codi);
		}
	}

	private TascaDadaDto getTascaDadaDtoFromExpedientDadaDto(
			ExpedientDadaDto expedientDadaDto,
			CampTasca campTasca) {
		TascaDadaDto tascaDto = new TascaDadaDto();
		tascaDto.setVarCodi(expedientDadaDto.getVarCodi());
		tascaDto.setVarValor(expedientDadaDto.getVarValor());
		tascaDto.setCampId(expedientDadaDto.getCampId());
		tascaDto.setCampTipus(expedientDadaDto.getCampTipus());
		tascaDto.setCampEtiqueta(expedientDadaDto.getCampEtiqueta());
		tascaDto.setCampMultiple(expedientDadaDto.isCampMultiple());
		tascaDto.setReadOnly(campTasca.isReadOnly());
		tascaDto.setReadFrom(campTasca.isReadFrom());
		tascaDto.setWriteTo(campTasca.isWriteTo());
		tascaDto.setRequired(campTasca.isRequired());
		tascaDto.setText(expedientDadaDto.getText());
		tascaDto.setError(expedientDadaDto.getError());
		tascaDto.setObservacions(expedientDadaDto.getObservacions());
		tascaDto.setJbpmAction(expedientDadaDto.getJbpmAction());
		if (expedientDadaDto.getMultipleDades() != null) {
			List<TascaDadaDto> multipleDades = new ArrayList<TascaDadaDto>();
			for (ExpedientDadaDto dto: expedientDadaDto.getMultipleDades()) {
				multipleDades.add(
						getTascaDadaDtoFromExpedientDadaDto(
								dto,
								campTasca));
			}
			tascaDto.setMultipleDades(multipleDades);
		}
		if (expedientDadaDto.getRegistreDades() != null) {
			List<TascaDadaDto> registreDades = new ArrayList<TascaDadaDto>();
			for (ExpedientDadaDto dto: expedientDadaDto.getRegistreDades()) {
				registreDades.add(
						getTascaDadaDtoFromExpedientDadaDto(
								dto,
								campTasca));
			}
			tascaDto.setRegistreDades(registreDades);
		}
		return tascaDto;
	}

	public TascaDadaDto getTascaDadaDtoParaConsultaDisseny(Camp camp, TipusConsultaCamp tipus) {
		TascaDadaDto tascaDto = new TascaDadaDto();
		String varCodi;
		if (TipusConsultaCamp.INFORME.equals(tipus)) {
			varCodi = camp.getDefinicioProces().getJbpmKey()+"/"+camp.getCodi().toLowerCase();
		} else {
			varCodi = camp.getCodi().toLowerCase();
		}
		tascaDto.setVarCodi(varCodi);
		tascaDto.setCampId(camp.getId());
		tascaDto.setCampTipus(conversioTipusHelper.convertir(camp.getTipus(), CampTipusDto.class));
		tascaDto.setCampEtiqueta(camp.getEtiqueta());
		tascaDto.setCampMultiple(camp.isMultiple());
		tascaDto.setObservacions(camp.getObservacions());
		tascaDto.setJbpmAction(camp.getJbpmAction());
		tascaDto.setValidacions(conversioTipusHelper.convertirList(camp.getValidacions(), ValidacioDto.class));
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

	private static final Logger logger = LoggerFactory.getLogger(VariableHelper.class);
}
