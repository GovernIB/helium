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
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusAuthDomini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
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
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ValidacioDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
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
	private ExpedientRepository expedientRepository;
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
				} else if (incloureVariablesBuides) {
					ExpedientDadaDto dto = getDadaPerVariableJbpm(
							camp,
							var,
							null,
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
		return getDadaPerInstanciaProces(processInstanceId, variableCodi, false);
	}
	public ExpedientDadaDto getDadaPerInstanciaProces(
			String processInstanceId,
			String variableCodi, 
			boolean incloureVariablesBuides) {
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
		ExpedientDadaDto dto = null;
		if (varAmbContingut) {
			dto = getDadaPerVariableJbpm(
					camp,
					variableCodi,
					valor,
					null,
					processInstanceId,
					false);
		} else if (incloureVariablesBuides) {
			dto = getDadaPerVariableJbpm(
					camp,
					variableCodi,
					null,
					null,
					processInstanceId,
					false);
		}
		return dto;
	}

	public List<TascaDadaDto> findDadesPerInstanciaTascaDto(ExpedientTascaDto tasca) {		
		List<TascaDadaDto> resposta = new ArrayList<TascaDadaDto>();
		
		for (CampTasca campTasca: campTascaRepository.findAmbTascaOrdenats(tasca.getTascaId())) {
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
//		Map<String, CampTasca> campsIndexatsPerCodi = new HashMap<String, CampTasca>();
//		for (CampTasca campTasca: campsTasca) {
//			campsIndexatsPerCodi.put(
//					campTasca.getCamp().getCodi(),
//					campTasca);
//		}
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
	public TascaDadaDto findDadaPerInstanciaTasca(
			JbpmTask task,
			String variableCodi) {
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getName(),
				definicioProces);
		CampTasca campTasca = campTascaRepository.findAmbTascaCodi(
				tasca.getId(),
				variableCodi);
		Camp camp = null;
		if (campTasca != null) {
			camp = campTasca.getCamp();
		} else {
			return null;
		}
		Object valor = jbpmHelper.getTaskInstanceVariable(
				task.getId(),
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
					task.getId(),
					task.getProcessInstanceId(),
					false);
			return getTascaDadaDtoFromExpedientDadaDto(
							dto,
							campTasca);
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
			if (camp.getOrdre() != null)
				dto.setOrdre(camp.getOrdre());
			if (camp.getAgrupacio() != null)
				dto.setAgrupacioId(camp.getAgrupacio().getId());
			if (camp.getValidacions() != null)
				dto.setValidacions(conversioTipusHelper.convertirList(camp.getValidacions(), ValidacioDto.class));
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
							dtoRegistre.setRequired(campRegistre.isObligatori());
							dtoRegistre.setOrdre(campRegistre.getOrdre());
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

	public String getTextPerCamp(
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
				valorFontExterna = (String)getTextPerCampAmbValor(
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

	public ParellaCodiValorDto getTextPerCampAmbValor(
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
		List<ParellaCodiValorDto> lista = getPossiblesValorsCamp(
				camp,
				null,
				null,
				valor,
				valorsAddicionals,
				taskInstanceId,
				processInstanceId);
		if (!lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}

	public List<ParellaCodiValorDto> getPossiblesValorsCamp(
			Camp camp,
			Camp registreCamp,
			Integer registreIndex,
			Object valor,
			Map<String, Object> valorsAddicionals,
			String taskInstanceId,
			String processInstanceId) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		TipusCamp tipus = camp.getTipus();
		if (tipus.equals(TipusCamp.SELECCIO) || tipus.equals(TipusCamp.SUGGEST)) {
			if (camp.getDomini() != null || camp.isDominiIntern()) {
				Domini domini;
				if (camp.isDominiIntern()) {
					domini = getDominiIntern(camp.getDefinicioProces().getEntorn());
				} else {
					domini = camp.getDomini();
				}
				List<FilaResultat> resultatConsultaDomini = dominiHelper.consultar(
						domini,
						camp.getDominiId(),
						getParamsConsulta(
								taskInstanceId,
								processInstanceId,
								camp,
								registreCamp,
								registreIndex,
								valorsAddicionals));
				String columnaCodi = camp.getDominiCampValor();
				String columnaValor = camp.getDominiCampText();
				Iterator<FilaResultat> it = resultatConsultaDomini.iterator();
				while (it.hasNext()) {
					FilaResultat fr = it.next();
					for (ParellaCodiValor parellaCodi: fr.getColumnes()) {
						boolean ignoreCase = TipusDomini.CONSULTA_SQL.equals(domini.getTipus());
						boolean matches = (ignoreCase) ? parellaCodi.getCodi().equalsIgnoreCase(columnaCodi) : parellaCodi.getCodi().equals(columnaCodi);
						if (matches &&
								(
									valor == null || 
									parellaCodi.getValor().toString().equals(valor) ||
									(tipus.equals(TipusCamp.SUGGEST) && parellaCodi.getValor().toString().toUpperCase().indexOf(valor.toString().toUpperCase()) != -1)
								)
							) {
							for (ParellaCodiValor parellaValor: fr.getColumnes()) {
								matches = (ignoreCase) ? parellaValor.getCodi().equalsIgnoreCase(columnaValor) : parellaValor.getCodi().equals(columnaValor);
								if (matches) {
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
			}/* else if (camp.getConsulta() != null) {
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
					if (valorDto == null){
						valorDto = exp.getDadesExpedient().get(consulta.getExpedientTipus().getJbpmProcessDefinitionKey()+"/"+camp.getConsultaCampValor());
					}
					if (valorDto != null){
						if (valorDto.getValor().toString().equals(valor)){
							DadaIndexadaDto textDto = exp.getDadesExpedient().get(camp.getConsultaCampText());
							if(textDto == null){
								textDto = exp.getDadesExpedient().get(consulta.getExpedientTipus().getJbpmProcessDefinitionKey()+"/"+camp.getConsultaCampText());
							}
							resposta.add(
									new ParellaCodiValorDto(
											valorDto.getValorMostrar(),
											textDto.getValorMostrar()));
							if (valor != null) {
								break;
							}
						}
					}
				}
			}*/
		}
		return resposta;
	}

	public Domini getDominiIntern(Entorn entorn) {
		Domini domini = new Domini();
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
			Camp registreCamp,
			Integer registreIndex,
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
				if (value == null) {
					if (registreCamp != null) {
						value = getValorCampRegistre(
								taskInstanceId,
								processInstanceId,
								registreCamp,
								registreIndex,
								campCodi);
					}
					if (value == null) {
						if (taskInstanceId != null)
							value = getVariableJbpmTascaValor(taskInstanceId, campCodi);
						else if (processInstanceId != null)
							value = getVariableJbpmProcesValor(processInstanceId, campCodi);
					}
				}
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
			variables.remove(TascaHelper.VAR_TASCA_VALIDADA);
			variables.remove(TascaHelper.VAR_TASCA_DELEGACIO);
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

	public TascaDadaDto getTascaDadaDtoFromExpedientDadaDto(
			ExpedientDadaDto expedientDadaDto,
			CampTasca campTasca) {
		TascaDadaDto tascaDadaDto = new TascaDadaDto();
		tascaDadaDto.setVarCodi(expedientDadaDto.getVarCodi());
		tascaDadaDto.setVarValor(expedientDadaDto.getVarValor());
		tascaDadaDto.setCampId(expedientDadaDto.getCampId());
		tascaDadaDto.setCampTipus(expedientDadaDto.getCampTipus());
		tascaDadaDto.setCampEtiqueta(expedientDadaDto.getCampEtiqueta());
		tascaDadaDto.setCampMultiple(expedientDadaDto.isCampMultiple());
		tascaDadaDto.setCampOcult(expedientDadaDto.isCampOcult());
		if (campTasca != null) {
			tascaDadaDto.setReadOnly(campTasca.isReadOnly());
			tascaDadaDto.setReadFrom(campTasca.isReadFrom());
			tascaDadaDto.setWriteTo(campTasca.isWriteTo());
			tascaDadaDto.setRequired(campTasca.isRequired());
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
						campTasca);
				// Si es un campo readonly quitamos la validación de los campos requeridos que contenga
				tascaDada.setRequired(campTasca.isReadOnly() ? false : dto.isRequired());
				multipleDades.add(tascaDada);
			}
			tascaDadaDto.setMultipleDades(multipleDades);
		}
		if (expedientDadaDto.getRegistreDades() != null) {
			List<TascaDadaDto> registreDades = new ArrayList<TascaDadaDto>();
			for (ExpedientDadaDto dto: expedientDadaDto.getRegistreDades()) {
				TascaDadaDto tascaDada = getTascaDadaDtoFromExpedientDadaDto(
						dto,
						campTasca);
				// Si es un campo readonly quitamos la validación de los campos requeridos que contenga
				tascaDada.setRequired(campTasca.isReadOnly() ? false : dto.isRequired());
				registreDades.add(tascaDada);
			}
			tascaDadaDto.setRegistreDades(registreDades);
		}
		if (campTasca.getCamp().getDominiParams() != null) {
			String dominiParams = campTasca.getCamp().getDominiParams();
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

	public List<TascaDadaDto> getTascaDadasDtoFromExpedientDadasDto(List<ExpedientDadaDto> expedientDadasDto) {
		List<TascaDadaDto> dadas = new ArrayList<TascaDadaDto>();
		for (ExpedientDadaDto expedientDada : expedientDadasDto) {
			dadas.add(getTascaDadaDtoFromExpedientDadaDto(expedientDada));
		}
		return dadas;
	}
		
	public TascaDadaDto getTascaDadaDtoFromExpedientDadaDto(ExpedientDadaDto expedientDadaDto) {
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
	}

	public TascaDadaDto getTascaDadaDtoParaConsultaDisseny(Camp camp, TipusConsultaCamp tipus) {
		TascaDadaDto tascaDto = new TascaDadaDto();
		String varCodi;
		if (TipusConsultaCamp.INFORME.equals(tipus) && camp.getDefinicioProces() != null) {
			varCodi = camp.getDefinicioProces().getJbpmKey()+"/"+camp.getCodi();
		} else {
			varCodi = camp.getCodi();
		}
		varCodi = varCodi.replace(ExpedientCamps.EXPEDIENT_CAMP_ESTAT, ExpedientCamps.EXPEDIENT_CAMP_ESTAT_JSP);
		tascaDto.setVarCodi(varCodi);
		tascaDto.setCampId(camp.getId());
		tascaDto.setCampTipus(conversioTipusHelper.convertir(camp.getTipus(), CampTipusDto.class));
		tascaDto.setCampEtiqueta(camp.getEtiqueta());
		tascaDto.setCampMultiple(camp.isMultiple());
		tascaDto.setObservacions(camp.getObservacions());
		tascaDto.setJbpmAction(camp.getJbpmAction());
		tascaDto.setValidacions(conversioTipusHelper.convertirList(camp.getValidacions(), ValidacioDto.class));
		
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
								null));
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
			String campCodi) {
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

	private static final Logger logger = LoggerFactory.getLogger(VariableHelper.class);

}
