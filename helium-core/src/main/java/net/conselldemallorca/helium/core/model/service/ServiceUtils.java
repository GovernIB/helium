/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.Registre;

/**
 * Utilitats comunes pels serveis
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServiceUtils {

	private DefinicioProcesDao definicioProcesDao;
	private DtoConverter dtoConverter;
	private JbpmDao jbpmDao;
	private MessageSource messageSource;



	public ServiceUtils(
			DefinicioProcesDao definicioProcesDao,
			DtoConverter dtoConverter,
			JbpmDao jbpmDao,
			MessageSource messageSource) {
		this.definicioProcesDao = definicioProcesDao;
		this.dtoConverter = dtoConverter;
		this.jbpmDao = jbpmDao;
		this.messageSource = messageSource;
	}



	/*
	 * Mètodes per a la  construcció d'estructures per a l'indexació d'expedients
	 */
	public Map<String, DefinicioProces> getMapDefinicionsProces(Expedient expedient) {
		Map<String, DefinicioProces> resposta = new HashMap<String, DefinicioProces>();
		List<JbpmProcessInstance> tree = jbpmDao.getProcessInstanceTree(expedient.getProcessInstanceId());
		for (JbpmProcessInstance pi: tree)
			resposta.put(
					pi.getId(),
					definicioProcesDao.findAmbJbpmId(pi.getProcessDefinitionId()));
		return resposta;
	}
	public Map<String, Set<Camp>> getMapCamps(Expedient expedient) {
		Map<String, Set<Camp>> resposta = new HashMap<String, Set<Camp>>();
		List<JbpmProcessInstance> tree = jbpmDao.getProcessInstanceTree(expedient.getProcessInstanceId());
		for (JbpmProcessInstance pi: tree) {
			resposta.put(
					pi.getId(),
					definicioProcesDao.findAmbJbpmId(pi.getProcessDefinitionId()).getCamps());
		}
		return resposta;
	}
	public Map<String, Map<String, Object>> getMapValors(Expedient expedient) {
		Map<String, Map<String, Object>> resposta = new HashMap<String, Map<String, Object>>();
		List<JbpmProcessInstance> tree = jbpmDao.getProcessInstanceTree(expedient.getProcessInstanceId());
		for (JbpmProcessInstance pi: tree)
			resposta.put(
					pi.getId(),
					jbpmDao.getProcessInstanceVariables(pi.getId()));
		return resposta;
	}
	public Map<String, Map<String, String>> getMapValorsDomini(
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
					String valorDomini = dtoConverter.getTextConsultaDomini(
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

}
