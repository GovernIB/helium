/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.jbpm3.handlers.BasicActionHandler;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ValidacioDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;

/**
 * Helper per a gestionar les variables dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class VariableHelper {

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
	private ExpedientTipusHelper expedientTipusHelper;
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
	@Resource
	private MessageHelper messageHelper;



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
		Expedient exp = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		ExpedientTipus expedientTipus = exp.getTipus();
		if (MesuresTemporalsHelper.isActiu()) {
			tipusExp = (exp != null ? exp.getTipus().getNom() : null);
			mesuresTemporalsHelper.mesuraIniciar("Expedient DADES v3", "expedient", tipusExp);
			mesuresTemporalsHelper.mesuraIniciar("Expedient DADES v3", "expedient", tipusExp, null, "0");
		}
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				processInstanceId);
		Map<String, Camp> campsIndexatsPerCodi = new HashMap<String, Camp>();
		Set<Camp> camps;
		if (expedientTipus.isAmbInfoPropia()) {
			if (expedientTipus.getExpedientTipusPare() != null) {
				// Camps heretats
				for (Camp camp: expedientTipus.getExpedientTipusPare().getCamps())
					campsIndexatsPerCodi.put(camp.getCodi(), camp);
			}
			camps = expedientTipus.getCamps();
		} else {
			camps = definicioProces.getCamps();
		}
		
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
				ExpedientDadaDto dto = null;
				boolean varAmbContingut = varsInstanciaProces.get(var) != null;
				Camp camp = campsIndexatsPerCodi.get(var);
				if (varAmbContingut) {
					dto = getDadaPerVariableJbpm(
							camp,
							var,
							varsInstanciaProces.get(var),
							null,
							null,
							processInstanceId,
							false);
					// Si és registre o múltiple comprova si té contingut. Pot haver error de simple a múltiple
					try {
						if (camp != null && (TipusCamp.REGISTRE.equals(camp.getTipus()) || camp.isMultiple())) {
							Object[] registreValors = (Object[])varsInstanciaProces.get(var);
							varAmbContingut = registreValors.length > 0;
						}						
					} catch(Exception e) {
						dto.setError(messageHelper.getMessage(
								"variable.helper.error.recuperant.valor", 
								new Object[] {camp.getTipus(), (camp.isMultiple() ? " múltiple" : "")}));
					}
				}
				if (varAmbContingut) {
					resposta.add(dto);
				} else if (incloureVariablesBuides) {
					dto = getDadaPerVariableJbpm(
							camp,
							var,
							null,
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
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		ExpedientTipus expedientTipus = expedient.getTipus();
		
		Camp camp;
		if (expedientTipus.isAmbInfoPropia())
			camp = campRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(),
					variableCodi,
					expedientTipus.getExpedientTipusPare() != null);
		else
			camp = campRepository.findByDefinicioProcesAndCodi(
					definicioProces,
					variableCodi);
		
		Object valor = jbpmHelper.getProcessInstanceVariable(
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
					false);
			// Si és registre o múltiple comprova si té contingut. Pot haver error de simple a múltiple
			try {
				if (camp != null && (TipusCamp.REGISTRE.equals(camp.getTipus()) || camp.isMultiple())) {
					Object[] registreValors = (Object[])valor;
					varAmbContingut = registreValors.length > 0;
				}						
			} catch(Exception e) {
				dto.setError(messageHelper.getMessage(
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
					false);
		}
		return dto;
	}
	
	public ExpedientDadaDto getDadaBuida(
			long campId) {
		Camp camp = campRepository.findOne(campId);
		ExpedientDadaDto dto = getDadaPerVariableJbpm(
					camp,
					camp.getCodi(),
					null,
					null,
					null,
					null,
					false);
		return dto;
	}

	public List<TascaDadaDto> findDadesPerInstanciaTascaDto(Long expedientTipusId, ExpedientTascaDto tasca) {		
		List<TascaDadaDto> resposta = new ArrayList<TascaDadaDto>();
		Tasca tascaEntity = tascaRepository.findOne(tasca.getTascaId());
		if (expedientTipusId == null)
			expedientTipusId = tascaEntity.getDefinicioProces().getExpedientTipus() != null ? tascaEntity.getDefinicioProces().getExpedientTipus().getId() : null; 
		for (CampTasca campTasca: campTascaRepository.findAmbTascaOrdenats(tasca.getTascaId(), expedientTipusId)) {
			Camp camp = campTasca.getCamp();
			
			ExpedientDadaDto expedientDadaDto = getDadaPerVariableJbpm(
					camp,
					camp.getCodi(),
					null,
					null,
					tasca.getId(),
					null,
					false);
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
		return resposta;
	}

	public List<TascaDadaDto> findDadesPerInstanciaTasca(
			JbpmTask task) {
		String tipusExp = null;
		Expedient exp = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		Long expedientTipusId = exp != null ? exp.getTipus().getId() : null;
		ExpedientTipus expedientTipus = exp != null? expedientTipusHelper.findAmbProcessInstanceId(task.getProcessInstanceId()) : null;
		if (MesuresTemporalsHelper.isActiu()) {
			tipusExp = (exp != null ? exp.getTipus().getNom() : null);
			mesuresTemporalsHelper.mesuraIniciar("Tasca DADES v3", "tasques", tipusExp, task.getTaskName());
			mesuresTemporalsHelper.mesuraIniciar("Tasca DADES v3", "tasques", tipusExp, task.getTaskName(), "0");
		}
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
		mesuresTemporalsHelper.mesuraCalcular("Tasca DADES v3", "tasques", tipusExp, task.getTaskName(), "0");
		mesuresTemporalsHelper.mesuraIniciar("Tasca DADES v3", "tasques", tipusExp, task.getTaskName(), "1");
		List<TascaDadaDto> resposta = new ArrayList<TascaDadaDto>();
		Map<String, Object> varsInstanciaTasca = jbpmHelper.getTaskInstanceVariables(
				task.getId());
		mesuresTemporalsHelper.mesuraCalcular("Tasca DADES v3", "tasques", tipusExp, task.getTaskName(), "1");
		mesuresTemporalsHelper.mesuraIniciar("Tasca DADES v3", "tasques", tipusExp, task.getTaskName(), "2");
		// Només es mostraran les variables donades d'alta al formulari
		// de la tasca. Les variables jBPM de la tasca que no siguin
		// al formulari no es mostraran.
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
					false);
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
		mesuresTemporalsHelper.mesuraCalcular("Tasca DADES v3", "tasques", tipusExp, task.getTaskName(), "2");
		mesuresTemporalsHelper.mesuraCalcular("Tasca DADES v3", "tasques", tipusExp, task.getTaskName());
		return resposta;
	}

	public TascaDadaDto findDadaPerInstanciaTasca(
			JbpmTask task,
			String variableCodi) {
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
					null,
					task.getId(),
					task.getProcessInstanceId(),
					false);
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
			String processInstanceId) {
		if (valor == null)
			return null;
		String valorFontExterna = null;
		if (TipusCamp.SELECCIO.equals(camp.getTipus()) || TipusCamp.SUGGEST.equals(camp.getTipus())) {
			
			ParellaCodiValorDto parella = getTextPerCampAmbValor(
					camp,
					valor,
					valorsAddicionals,
					taskInstanceId,
					processInstanceId);
			
			valorFontExterna = parella != null ? (String)(parella.getValor()) : "";
		}
		return Camp.getComText(
				camp.getTipus(),
				valor,
				valorFontExterna);
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
			if (camp.getDominiIntern() || camp.getDomini() != null ) {
				Map<String, Object> parametres = getParamsConsulta(
						taskInstanceId,
						processInstanceId,
						camp,
						registreCamp,
						registreIndex,
						valorsAddicionals);
				List<FilaResultat> resultatConsultaDomini;
				Domini domini;		
				if (camp.getDominiIntern()) {
					Entorn entorn;
					if (camp.getDefinicioProces() != null)
						entorn = camp.getDefinicioProces().getEntorn();
					else
						entorn = camp.getExpedientTipus().getEntorn();
					domini = getDominiIntern(entorn);
				} else { 
					domini = camp.getDomini();
				}
				resultatConsultaDomini = dominiHelper.consultar(
					domini,
					camp.getDominiId(),
					parametres);

				String columnaCodi = camp.getDominiCampValor();
				String columnaValor = camp.getDominiCampText();
				if (resultatConsultaDomini != null) {
					Iterator<FilaResultat> it = resultatConsultaDomini.iterator();
					while (it.hasNext()) {
						FilaResultat fr = it.next();
						for (ParellaCodiValor parellaCodi: fr.getColumnes()) {
							boolean ignoreCase = TipusDomini.CONSULTA_SQL.equals(camp.getDomini() != null? camp.getDomini().getTipus() : null);
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
						resposta.add(new ParellaCodiValorDto(
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

	public Domini getDominiIntern(Entorn entorn) {
		Domini domini = new Domini();
		domini.setId((long) 0);
		domini.setCacheSegons(30);
		domini.setCodi("");
		domini.setNom("Domini intern");
		domini.setEntorn(entorn);
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

	public TascaDadaDto getTascaDadaDtoParaConsultaDisseny(Camp camp, TipusConsultaCamp tipus) {
		TascaDadaDto tascaDto = new TascaDadaDto();
		String varCodi;
		if (TipusConsultaCamp.INFORME.equals(tipus) && camp.getDefinicioProces() != null  && camp.getExpedientTipus() == null ) {
			// Variable definició de procés
			varCodi = camp.getDefinicioProces().getJbpmKey()+"/"+camp.getCodi();
		} else {
			// Variable expedient o tipus d'expedient
			varCodi = camp.getCodi();
		}
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

	public Object getDescripcioVariable(String taskId, String processInstanceId, String codi) {
		Object valor = null;
		if (taskId != null)
			valor = jbpmHelper.getTaskInstanceVariable(taskId, JbpmVars.PREFIX_VAR_DESCRIPCIO + codi);
		if (valor == null)
			valor = jbpmHelper.getProcessInstanceVariable(processInstanceId, JbpmVars.PREFIX_VAR_DESCRIPCIO + codi);
		return valor;
	}

	private ExpedientDadaDto getDadaPerVariableJbpm(
			Camp camp,
			String varCodi,
			Object varValor,
			Map<String, Object> valorsAddicionals,
			String taskInstanceId,
			String processInstanceId,
			boolean forsarSimple) {
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
									true);
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
											processInstanceId));
							if (dto.getText() == null || dto.getText().isEmpty()) {
								dto.setText("");
							}
							dto.setCampMultiple(false);
						} catch (SistemaExternException ex) {
							dto.setText("");
							logger.error("Error al obtenir text per la dada de l'expedient (processInstanceId=" + processInstanceId + ", variable=" + camp.getCodi() + ")", ex);
							dto.setError(ex.getPublicMessage());
						}
					}
				} else {
					Object[] valorsMultiples = null;
					// Comprovam que el valor desat actual és de tipus array. En cas contrari el convertim a array
					if (varValor == null 
							|| (varValor instanceof Object[] 
									&& ((Object[]) varValor).length > 0)) {
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
									true);
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
								true);
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
		if (valor instanceof DominiCodiDescripcio) {
			return ((DominiCodiDescripcio)valor).getCodi();
		} else {
			return valor;
		}
	}

	private void filtrarVariablesUsIntern(Map<String, Object> variables) {
		if (variables != null) {
			variables.remove(JbpmVars.VAR_TASCA_VALIDADA);
			variables.remove(JbpmVars.VAR_TASCA_DELEGACIO);
			List<String> codisEsborrar = new ArrayList<String>();
			for (String codi: variables.keySet()) {
				if (	codi.startsWith(JbpmVars.PREFIX_DOCUMENT) ||
						codi.startsWith(JbpmVars.PREFIX_SIGNATURA) ||
						codi.startsWith(JbpmVars.PREFIX_ADJUNT) ||
						codi.startsWith(JbpmVars.PREFIX_VAR_DESCRIPCIO) ||
						codi.startsWith(BasicActionHandler.PARAMS_RETROCEDIR_VARIABLE_PREFIX))
					codisEsborrar.add(codi);
			}
			for (String codi: codisEsborrar)
				variables.remove(codi);
		}
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

	private ParellaCodiValorDto getTextPerCampAmbValor(
			Camp camp,
			Object valor,
			Map<String, Object> valorsAddicionals,
			String taskInstanceId,
			String processInstanceId){
		if (valor == null)
			return null;
		
		//mirem si hi ha una variable amb la texte, utilitzant el prefix PREFIX_VAR_DESCRIPCIO
		if (camp.isDominiCacheText()) {
			Object descVariable = getDescripcioVariable(taskInstanceId, processInstanceId, camp.getCodi());
			if (descVariable != null) {
				return new ParellaCodiValorDto(
						(String)valor,
						(String)descVariable);
			}
		}
		//////////
		
		if (valor instanceof DominiCodiDescripcio) {
			return new ParellaCodiValorDto(
					((DominiCodiDescripcio)valor).getCodi(),
					((DominiCodiDescripcio)valor).getDescripcio());
		}
		
		ParellaCodiValorDto resultat = null;
		List<ParellaCodiValorDto> lista = getPossiblesValorsCamp(
				camp,
				null,
				null,
				valor,
				valorsAddicionals,
				taskInstanceId,
				processInstanceId);
		if (!lista.isEmpty()) {
			// Cerca el valor resultant
			for (ParellaCodiValorDto parellaCodiValor : lista){
				if (parellaCodiValor.getCodi().equals(valor.toString()))
					resultat = parellaCodiValor;
			}
		}
		return resultat;
	}

	private static final Logger logger = LoggerFactory.getLogger(VariableHelper.class);

}
