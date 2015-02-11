/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusParamConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Persona;
import net.conselldemallorca.helium.core.model.service.LuceneHelper;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.Registre;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaCampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

/**
 * Utilitats comunes pels serveis
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("serviceUtilsV3")
public class ServiceUtils {
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private PluginPersonaHelper pluginPersonaHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private ConsultaCampRepository consultaCampRepository;
	@Resource
	private LuceneHelper luceneHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private MessageSource messageSource;
	
	/**
	 * Mètodes per a la reindexació d'expedients
	 */
	public void expedientIndexLuceneCreate(String processInstanceId) {
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		Map<String, Set<Camp>> mapCamps = getMapCamps(expedient.getProcessInstanceId());
		Map<String, Map<String, Object>> mapValors = getMapValors(expedient.getProcessInstanceId());
		luceneHelper.createExpedient(
				expedient,
				getMapDefinicionsProces(expedient.getProcessInstanceId()),
				mapCamps,
				mapValors,
				getMapValorsDomini(mapCamps, mapValors),
				isExpedientFinalitzat(expedient),
				false);
	}
	public void expedientIndexLuceneUpdate(String processInstanceId) {
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(rootProcessInstance.getId());
		Map<String, Set<Camp>> mapCamps = getMapCamps(rootProcessInstance.getId());
		Map<String, Map<String, Object>> mapValors = getMapValors(rootProcessInstance.getId());
		luceneHelper.updateExpedientCamps(
				expedient,
				getMapDefinicionsProces(rootProcessInstance.getId()),
				mapCamps,
				mapValors,
				getMapValorsDomini(mapCamps, mapValors),
				isExpedientFinalitzat(expedient));
	}
	public void expedientIndexLuceneRecrear(String processInstanceId) {
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(rootProcessInstance.getId());
		luceneHelper.deleteExpedient(expedient);
		Map<String, Set<Camp>> mapCamps = getMapCamps(rootProcessInstance.getId());
		Map<String, Map<String, Object>> mapValors = getMapValors(rootProcessInstance.getId());
		luceneHelper.createExpedient(
				expedient,
				getMapDefinicionsProces(rootProcessInstance.getId()),
				mapCamps,
				mapValors,
				getMapValorsDomini(mapCamps, mapValors),
				isExpedientFinalitzat(expedient),
				false);
	}
	public List<Map<String, DadaIndexadaDto>> expedientIndexLucenGetDades(String processInstanceId) {
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(rootProcessInstance.getId());
		List<Camp> informeCamps = new ArrayList<Camp>();
		Map<String, Set<Camp>> camps = getMapCamps(rootProcessInstance.getId());
		for (String clau: camps.keySet()) {
			for (Camp camp: camps.get(clau))
				informeCamps.add(camp);
		}
		informeCamps.addAll(findAllCampsExpedientConsulta());
		return luceneHelper.getDadesExpedientV3(
				expedient.getEntorn().getCodi(),
				expedient,
				informeCamps);
	}

	/*
	 * Mètodes per a obtenir els camps de les consultes per tipus
	 */
	public List<TascaDadaDto> findCampsPerCampsConsulta(Consulta consulta, TipusConsultaCamp tipus) {
		List<TascaDadaDto> resposta = new ArrayList<TascaDadaDto>();
		List<ConsultaCamp> camps = null;
		
		if (tipus != null)
			camps = consultaCampRepository.findCampsConsulta(consulta.getId(), tipus);
		else
			camps = new ArrayList<ConsultaCamp>(consulta.getCamps());
		for (ConsultaCamp camp: camps) {
			TascaDadaDto tascaDadaDto = null;
			DefinicioProces definicioProces = null;
			Camp campRes = null;
			if (camp.getCampCodi().startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
				campRes = getCampExpedient(camp.getCampCodi());
			} else {
				definicioProces = definicioProcesRepository.findByJbpmKeyAndVersio(
						camp.getDefprocJbpmKey(),
						camp.getDefprocVersio());
				if (definicioProces != null) {
					campRes = campRepository.findByDefinicioProcesAndCodi(
							definicioProces,
							camp.getCampCodi());
				}
			}
			if (campRes != null) {
				tascaDadaDto = variableHelper.getTascaDadaDtoParaConsultaDisseny(campRes,tipus);
			} else {
				tascaDadaDto = getTascaDadaPerCampsConsulta(camp);
			}
			if (definicioProces != null) {
				tascaDadaDto.setDefinicioProcesKey(definicioProces.getJbpmKey());
			}
			resposta.add(tascaDadaDto);
		}
		return resposta;
	}
	
	private TascaDadaDto getTascaDadaPerCampsConsulta(ConsultaCamp camp) {
		TascaDadaDto tascaDadaDto = null;
		if (TipusConsultaCamp.PARAM.equals(camp.getTipus())) {
			if (TipusParamConsultaCamp.SENCER.equals(camp.getParamTipus())) {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.INTEGER, camp.getCampCodi()));
			} else if (TipusParamConsultaCamp.FLOTANT.equals(camp.getParamTipus())) {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.FLOAT, camp.getCampCodi()));
			} else if (TipusParamConsultaCamp.DATA.equals(camp.getParamTipus())) {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.DATE, camp.getCampCodi()));
			} else if (TipusParamConsultaCamp.BOOLEAN.equals(camp.getParamTipus())) {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.BOOLEAN, camp.getCampCodi()));
			} else {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.STRING, camp.getCampCodi()));
			}
		} else {
			String description = camp.getCampDescripcio() == null ? camp.getCampCodi() : camp.getCampDescripcio();
			tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.STRING, description));
		}
		return tascaDadaDto;
	}
	
	private List<Camp> findAllCampsExpedientConsulta() {
		List<Camp> resposta = new ArrayList<Camp>();
		resposta.add(getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_ID));
		resposta.add(getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_NUMERO));
		resposta.add(getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_TITOL));
		resposta.add(getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_COMENTARI));
		resposta.add(getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_INICIADOR));
		resposta.add(getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE));
		resposta.add(getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI));
		resposta.add(getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_ESTAT));
		return resposta;
	}
	
	public String getValueCampExpedient(Expedient expedient, String campCodi) {		
		String text = null;
		if (ExpedientCamps.EXPEDIENT_CAMP_ID.equals(campCodi)) {
			text = expedient.getId().toString();
		} else if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(campCodi)) {
			text = expedient.getNumeroIdentificador();
		} else if (ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(campCodi)) {
			text = expedient.getIdentificadorLimitat();
		} else if (ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(campCodi)) {
			text = expedient.getComentari();
		} else if (ExpedientCamps.EXPEDIENT_CAMP_INICIADOR.equals(campCodi)) {
			Persona persona = pluginPersonaHelper.findByCodi(expedient.getIniciadorCodi());
			if (persona != null)
				text = persona.getNomSencer();
			else
				text = expedient.getIniciadorCodi();
		} else if (ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE.equals(campCodi)) {
			Persona persona = pluginPersonaHelper.findByCodi(expedient.getResponsableCodi());
			if (persona != null)
				text = persona.getNomSencer();
			else 
				text = expedient.getResponsableCodi();
		} else if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(campCodi)) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			text = df.format(expedient.getDataInici());
		} else if (ExpedientCamps.EXPEDIENT_CAMP_ESTAT_JSP.equals(campCodi)) {
			if (expedient.getEstat() != null)
				text = expedient.getEstat().getNom();
			else if (expedient.getDataFi() != null)
				text = getMessage("expedient.consulta.finalitzat");
			else
				text = getMessage("expedient.consulta.iniciat");	
		}
		return text;
	}
	
	private Camp getCampExpedient(String campCodi) {
		Camp campExpedient = new Camp();
		if (ExpedientCamps.EXPEDIENT_CAMP_ID.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.id"));
		} else if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.numero"));
		} else if (ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.titol"));
		} else if (ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.comentari"));
		} else if (ExpedientCamps.EXPEDIENT_CAMP_INICIADOR.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.iniciador"));
		} else if (ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.responsable"));
		} else if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.DATE);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.data_ini"));
		} else if (ExpedientCamps.EXPEDIENT_CAMP_ESTAT.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SELECCIO);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.estat"));
		}
		return campExpedient;
	}

	/*
	 * Mètodes pel multiidioma
	 */
	public String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}
	public String getMessage(String key) {
		return getMessage(key, null);
	}
	public Object getVariableJbpmProcesValor(
			String processInstanceId,
			String varCodi) {
		Object valor = jbpmHelper.getProcessInstanceVariable(processInstanceId, varCodi);
		if (valor instanceof DominiCodiDescripcio) {
			return ((DominiCodiDescripcio)valor).getCodi();
		} else {
			return valor;
		}
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

	/*
	 * Varis
	 */
	public boolean isExpedientFinalitzat(Expedient expedient) {
		if (expedient.getProcessInstanceId() != null) {
			JbpmProcessInstance processInstance = jbpmHelper.getProcessInstance(expedient.getProcessInstanceId());
			return processInstance.getEnd() != null;
		}
		return false;
	}

	private Map<String, DefinicioProces> getMapDefinicionsProces(String processInstanceId) {
		Map<String, DefinicioProces> resposta = new HashMap<String, DefinicioProces>();
		List<JbpmProcessInstance> tree = jbpmHelper.getProcessInstanceTree(processInstanceId);
		for (JbpmProcessInstance pi: tree)
			resposta.put(
					pi.getId(),
					definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId()));
		return resposta;
	}
	
	private Map<String, Set<Camp>> getMapCamps(String processInstanceId) {
		Map<String, Set<Camp>> resposta = new HashMap<String, Set<Camp>>();
		List<JbpmProcessInstance> tree = jbpmHelper.getProcessInstanceTree(processInstanceId);
		for (JbpmProcessInstance pi: tree) {
			resposta.put(
					pi.getId(),
					definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId()).getCamps());
		}
		return resposta;
	}
	
	private Map<String, Map<String, Object>> getMapValors(String processInstanceId) {
		Map<String, Map<String, Object>> resposta = new HashMap<String, Map<String, Object>>();
		List<JbpmProcessInstance> tree = jbpmHelper.getProcessInstanceTree(processInstanceId);
		for (JbpmProcessInstance pi: tree)
			resposta.put(
					pi.getId(),
					getVariablesJbpmProcesValor(pi.getId()));
		return resposta;
	}
	
	private Map<String, Map<String, String>> getMapValorsDomini(
			Map<String, Set<Camp>> mapCamps,
			Map<String, Map<String, Object>> mapValors) {
		Map<String, Map<String, String>> resposta = new HashMap<String, Map<String, String>>();
		for (String clau: mapCamps.keySet()) {
			Map<String, String> textDominis = new HashMap<String, String>();
			for (Camp camp: mapCamps.get(clau)) {
				if (mapValors.get(clau) != null) {
					Object valor = mapValors.get(clau).get(camp.getCodi());
					if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
						if (valor != null) {
							String[] columnesRegistre = new String[camp.getRegistreMembres().size()];
							for (int i = 0; i < camp.getRegistreMembres().size(); i++) {
								columnesRegistre[i] = camp.getRegistreMembres().get(i).getMembre().getCodi();
							}
							List<Registre> registres = new ArrayList<Registre>();
							if (camp.isMultiple()) {
								Object[] filesValor = (Object[])valor;
								for (int i = 0; i < filesValor.length; i++) {
									registres.add(
											new Registre(columnesRegistre, (Object[])filesValor[i]));
								}
							} else {
								registres.add(
										new Registre(columnesRegistre, (Object[])valor));
							}
							for (Registre registre: registres) {
								for (CampRegistre campRegistre: camp.getRegistreMembres()) {
									guardarValorDominiPerCamp(
											textDominis,
											clau,
											campRegistre.getMembre(),
											registre.getValor(campRegistre.getMembre().getCodi()));
								}
							}
						}
					} else {
						guardarValorDominiPerCamp(
								textDominis,
								clau,
								camp,
								valor);
					}
				}
			}
			resposta.put(clau, textDominis);
		}
		return resposta;
	}
	private void guardarValorDominiPerCamp(
			Map<String, String> textDominis,
			String processInstanceId,
			Camp camp,
			Object valor) {
		if (!(valor instanceof String) || ((String)valor).length() > 0) {
			if (camp.getTipus().equals(TipusCamp.SELECCIO) || camp.getTipus().equals(TipusCamp.SUGGEST)) {
				if (valor != null) {
					String valorDomini = variableHelper.getTextPerCamp(
							camp,
							valor,
							null,
							null,
							processInstanceId);
					textDominis.put(
							camp.getCodi() + "@" + valor.toString(),
							valorDomini);
				}
			}
		}
	}

	private Object valorVariableJbpmRevisat(Object valor) {
		if (valor instanceof DominiCodiDescripcio) {
			return ((DominiCodiDescripcio)valor).getCodi();
		} else {
			return valor;
		}
	}
}
