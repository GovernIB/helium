/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientNotFoundException;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper per a gestionar els expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientHelper {

	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ExpedientRepository expedientRepository;

	@Resource
	private DominiHelper dominiHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;



	public Expedient geExpedientComprovantPermisosAny(
			Long expedientId,
			Permission[] permisos) {
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null) {
			logger.debug("No s'ha trobat l'expedient (id=" + expedientId + ")");
			throw new ExpedientNotFoundException();
		}
		boolean ambPermis = permisosHelper.isGrantedAny(
				expedient.getTipus().getId(),
				ExpedientTipus.class,
				permisos);
		if (!ambPermis) {
			logger.debug("No es tenen permisos per accedir a l'expedient (id=" + expedientId + ")");
			throw new ExpedientNotFoundException();
		}
		return expedient;
	}

	public Expedient findAmbEntornIId(Long entornId, Long id) {
		return expedientRepository.findByEntornIdAndId(entornId, id);
	}
	
	public Expedient findExpedientByProcessInstanceId(String processInstanceId) {
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		return expedientRepository.findByProcessInstanceId(rootProcessInstance.getId());
	}
	public DefinicioProces findDefinicioProcesByProcessInstanceId(String processInstanceId) {
		String processDefinitionId = jbpmHelper.getProcessInstance(processInstanceId).getProcessDefinitionId();
		return definicioProcesRepository.findByJbpmId(processDefinitionId);
	}

	public boolean isFinalitzat(Expedient expedient) {
		return expedient.getDataFi() != null;
	}

	public List<Camp> findAllCampsExpedientConsulta() {
		List<Camp> resposta = new ArrayList<Camp>();
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_ID));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_NUMERO));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_TITOL));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_COMENTARI));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_INICIADOR));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_ESTAT));
		return resposta;
	}



	private Camp getCampExpedient(String campCodi) {
		if (ExpedientCamps.EXPEDIENT_CAMP_ID.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.INTEGER);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.id"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.numero"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.titol"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.comentari"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_INICIADOR.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.iniciador"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.responsable"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.DATE);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.data_ini"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_ESTAT.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SELECCIO);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.estat"));
			return campExpedient;
		}
		return null;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientHelper.class);

	/*public Object getVariableJbpmTascaValor(
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
	}*/
	/*public void revisarVariablesJbpm(Map<String, Object> variables) {
		if (variables != null) {
			for (String varCodi: variables.keySet()) {
				Object valor = variables.get(varCodi);
				variables.put(varCodi, valorVariableJbpmRevisat(valor));
			}
		}
	}*/

	/*public List<ExpedientDadaDto> findDadesPerInstanciaProces(
			String processInstanceId) {
		DefinicioProces definicioProces = findDefinicioProcesByProcessInstanceId(
				processInstanceId);
		Set<Camp> camps = definicioProces.getCamps();
		Map<String, Camp> campsIndexatsPerCodi = new HashMap<String, Camp>();
		for (Camp camp: camps)
			campsIndexatsPerCodi.put(camp.getCodi(), camp);
		List<ExpedientDadaDto> resposta = new ArrayList<ExpedientDadaDto>();
		Map<String, Object> varsInstanciaProces = jbpmHelper.getProcessInstanceVariables(
				processInstanceId);
		if (varsInstanciaProces != null) {
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
		}
		return resposta;
	}*/

	/*public List<ExpedientDocumentDto> findDocumentsPerInstanciaProces(
			String processInstanceId) {
		DefinicioProces definicioProces = findDefinicioProcesByProcessInstanceId(
				processInstanceId);
		Set<Document> documents = definicioProces.getDocuments();
		Map<String, Document> documentsIndexatsPerCodi = new HashMap<String, Document>();
		for (Document document: documents)
			documentsIndexatsPerCodi.put(document.getCodi(), document);
		List<ExpedientDadaDto> resposta = new ArrayList<ExpedientDadaDto>();
		Map<String, Object> varsInstanciaProces = jbpmHelper.getProcessInstanceVariables(
				processInstanceId);
		if (varsInstanciaProces != null) {
			for (String var: varsInstanciaProces.keySet()) {
				if (var.startsWith(DocumentHelper.PREFIX_VAR_DOCUMENT)) {
					boolean varAmbContingut = varsInstanciaProces.get(var) != null;
					String documentCodi;
					Document document = documentsIndexatsPerCodi.get(documentCodi);
					
				} else if (var.startsWith(DocumentHelper.PREFIX_ADJUNT)) {
					boolean varAmbContingut = varsInstanciaProces.get(var) != null;
					
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
		}
		return resposta;
	}*/



	/*private ExpedientDadaDto getDadaPerVariableJbpm(
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
			dto.setCampEtiqueta(camp.getEtiqueta());
			dto.setCampTipus(CampTipusDto.valueOf(camp.getTipus().name()));
			dto.setCampMultiple(camp.isMultiple());
			dto.setCampOcult(camp.isOcult());
			if (camp.getAgrupacio() != null)
				dto.setAgrupacioId(camp.getAgrupacio().getId());
		} else {
			dto.setCampEtiqueta(varCodi);
			dto.setCampTipus(CampTipusDto.STRING);
		}
		if (varValor != null && !TipusCamp.ACCIO.equals(camp.getTipus())) {
			try {
				if (!camp.isMultiple() || forsarSimple) {
					if (TipusCamp.REGISTRE.equals(camp.getTipus())) {
						List<ExpedientDadaDto> registreDades = new ArrayList<ExpedientDadaDto>();
						Object[] valorsRegistre = (Object[])varValor;
						int index = 0;
						for (CampRegistre campRegistre: camp.getRegistreMembres()) {
							ExpedientDadaDto dtoRegistre = getDadaPerVariableJbpm(
									campRegistre.getMembre(),
									campRegistre.getMembre().getCodi(),
									valorsRegistre[index++],
									taskInstanceId,
									processInstanceId,
									false);
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
						} catch (Exception ex) {
							dto.setText("[!]");
							logger.error("Error al obtenir text per la dada de l'expedient (processInstanceId=" + processInstanceId + ", variable=" + camp.getCodi() + ")", ex);
							dto.setError(ex.getMessage());
						}
					}
				} else {
					Object[] valorsMultiples = (Object[])varValor;
					List<ExpedientDadaDto> multipleDades = new ArrayList<ExpedientDadaDto>();
					for (int i = 0; i < valorsMultiples.length; i++) {
						ExpedientDadaDto dtoMultiple = getDadaPerVariableJbpm(
								camp,
								varCodi,
								valorsMultiples[i],
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
	private String getTextVariableSimple(
			Camp camp,
			Object valor,
			Map<String, Object> valorsAddicionals,
			String taskInstanceId,
			String processInstanceId) throws Exception {
		if (valor == null)
			return null;
		String valorFontExterna = null;
		if (TipusCamp.SELECCIO.equals(camp.getTipus()) || TipusCamp.SUGGEST.equals(camp.getTipus())) {
			valorFontExterna = getTextVariableSimpleFontExterna(
					camp,
					valor,
					null,
					null,
					processInstanceId);
		}
		return Camp.getComText(
				camp.getTipus(),
				valor,
				valorFontExterna);
	}
	private String getTextVariableSimpleFontExterna(
			Camp camp,
			Object valor,
			Map<String, Object> valorsAddicionals,
			String taskInstanceId,
			String processInstanceId) throws Exception {
		if (valor == null)
			return null;
		if (valor instanceof DominiCodiDescripcio) {
			return ((DominiCodiDescripcio)valor).getDescripcio();
		}
		String resposta = null;
		TipusCamp tipus = camp.getTipus();
		if (tipus.equals(TipusCamp.SELECCIO) || tipus.equals(TipusCamp.SUGGEST)) {
			if (camp.getDomini() != null || camp.isDominiIntern()) {
				Domini domini = camp.getDomini();
				if (camp.isDominiIntern()) {
					domini = new Domini();
					domini.setCacheSegons(30);
					domini.setTipus(TipusDomini.CONSULTA_WS);
					domini.setTipusAuth(TipusAuthDomini.NONE);
					domini.setUrl(GlobalProperties.getInstance().getProperty("app.domini.intern.url","http://localhost:8080/helium/ws/DominiIntern"));
				}
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
				for (FilaResultat filaResultat: resultatConsultaDomini) {
					for (ParellaCodiValor parellaCodi: filaResultat.getColumnes()) {
						if (parellaCodi.getCodi().equals(columnaCodi) && parellaCodi.getValor().toString().equals(valor)) {
							for (ParellaCodiValor parellaValor: filaResultat.getColumnes()) {
								if (parellaValor.getCodi().equals(columnaValor)) {
									if (parellaValor != null && parellaValor.getValor() != null)
										resposta = parellaValor.getValor().toString();
									break;
								}
							}
							break;
						}
					}
				}
			} else if (camp.getEnumeracio() != null) {
				Enumeracio enumeracio = camp.getEnumeracio();
				for (EnumeracioValors enumValor: enumeracio.getEnumeracioValors()) {
					if (valor.equals(enumValor.getCodi())) {
						resposta = enumValor.getNom();
					}
				}
			} else if (camp.getConsulta() != null) {
				throw new Exception("Consultes de disseny com a fonts de dades externes no disponibles.");
				Consulta consulta = camp.getConsulta();
				List<ExpedientConsultaDissenyDto> dadesExpedients = expedientService.findAmbEntornConsultaDisseny(
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
							resposta = textDto.getValorMostrar();
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
				} else if (campCodi.startsWith("#{'")) {
					int index = campCodi.lastIndexOf("'");
					if (index != -1 && index > 2)
						value = campCodi.substring(3, campCodi.lastIndexOf("'"));
				}
			} else {
				if (valorsAddicionals != null && valorsAddicionals.size() > 0)
					value = valorsAddicionals.get(campCodi);
				if (value == null && taskId != null)
					value = getVariableJbpmTascaValor(taskId, campCodi);
				if (value == null && processInstanceId != null)
					value = getVariableJbpmProcesValor(processInstanceId, campCodi);
			}
			if (value != null)
				params.put(paramCodi, value);
		}
		return params;
	}*/

	/*private Object valorVariableJbpmRevisat(Object valor) {
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
	}*/

}
