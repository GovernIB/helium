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

import net.conselldemallorca.helium.core.model.dao.LuceneDao;
import net.conselldemallorca.helium.core.model.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.service.DtoConverter;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.Registre;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;

import org.springframework.stereotype.Component;

/**
 * Helper per a gestionar la informació dels expedients emprant
 * Lucene.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class LuceneHelper {

	@Resource
	private DefinicioProcesRepository definicioProcesRepository;

	@Resource
	private VariableHelper variableHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private JbpmHelper jbpmHelper;

	@Resource
	private LuceneDao luceneDao;
	@Resource
	private DtoConverter dtoConverter;



	public void expedientIndexLuceneCreate(Expedient expedient) {
		Map<String, Set<Camp>> mapCamps = getMapCamps(
				expedient.getProcessInstanceId());
		Map<String, Map<String, Object>> mapValors = getMapValors(
				expedient.getProcessInstanceId());
		luceneDao.createExpedient(
				expedient,
				getMapDefinicionsProces(expedient.getProcessInstanceId()),
				mapCamps,
				mapValors,
				getMapValorsDomini(mapCamps, mapValors),
				expedientHelper.isFinalitzat(expedient));
	}

	public void expedientIndexLuceneUpdate(Expedient expedient) {
		Map<String, Set<Camp>> mapCamps = getMapCamps(
				expedient.getProcessInstanceId());
		Map<String, Map<String, Object>> mapValors = getMapValors(
				expedient.getProcessInstanceId());
		luceneDao.updateExpedientCamps(
				expedient,
				getMapDefinicionsProces(expedient.getProcessInstanceId()),
				mapCamps,
				mapValors,
				getMapValorsDomini(mapCamps, mapValors),
				expedientHelper.isFinalitzat(expedient));
	}

	public void expedientIndexLuceneRecrear(Expedient expedient) {
		luceneDao.deleteExpedient(expedient);
		Map<String, Set<Camp>> mapCamps = getMapCamps(
				expedient.getProcessInstanceId());
		Map<String, Map<String, Object>> mapValors = getMapValors(
				expedient.getProcessInstanceId());
		luceneDao.createExpedient(
				expedient,
				getMapDefinicionsProces(expedient.getProcessInstanceId()),
				mapCamps,
				mapValors,
				getMapValorsDomini(mapCamps, mapValors),
				expedientHelper.isFinalitzat(expedient));
	}

	public List<Map<String, DadaIndexadaDto>> expedientIndexLucenGetDades(Expedient expedient) {
		List<Camp> informeCamps = new ArrayList<Camp>();
		Map<String, Set<Camp>> camps = getMapCamps(
				expedient.getProcessInstanceId());
		for (String clau: camps.keySet()) {
			for (Camp camp: camps.get(clau))
				informeCamps.add(camp);
		}
		informeCamps.addAll(expedientHelper.findAllCampsExpedientConsulta());
		return luceneDao.getDadesExpedient(
				expedient.getEntorn().getCodi(),
				expedient,
				informeCamps);
	}



	private Map<String, DefinicioProces> getMapDefinicionsProces(String processInstanceId) {
		Map<String, DefinicioProces> resposta = new HashMap<String, DefinicioProces>();
		List<JbpmProcessInstance> tree = jbpmHelper.getProcessInstanceTree(processInstanceId);
		for (JbpmProcessInstance pi: tree)
			resposta.put(
					pi.getId(),
					definicioProcesRepository.findByJbpmId(
							pi.getProcessDefinitionId()));
		return resposta;
	}
	private Map<String, Set<Camp>> getMapCamps(String processInstanceId) {
		Map<String, Set<Camp>> resposta = new HashMap<String, Set<Camp>>();
		List<JbpmProcessInstance> tree = jbpmHelper.getProcessInstanceTree(processInstanceId);
		for (JbpmProcessInstance pi: tree) {
			resposta.put(
					pi.getId(),
					definicioProcesRepository.findByJbpmId(
							pi.getProcessDefinitionId()).getCamps());
		}
		return resposta;
	}
	private Map<String, Map<String, Object>> getMapValors(String processInstanceId) {
		Map<String, Map<String, Object>> resposta = new HashMap<String, Map<String, Object>>();
		List<JbpmProcessInstance> tree = jbpmHelper.getProcessInstanceTree(processInstanceId);
		for (JbpmProcessInstance pi: tree)
			resposta.put(
					pi.getId(),
					variableHelper.getVariablesJbpmProcesValor(pi.getId()));
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
					String valorDomini = dtoConverter.getCampText(
							null,
							processInstanceId,
							camp,
							valor);
					textDominis.put(
							camp.getCodi() + "@" + valor.toString(),
							valorDomini);
				}
			}
		}
	}

}
