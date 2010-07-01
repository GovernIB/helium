/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.integracio.domini.ParellaCodiValor;
import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.CampTasca;
import net.conselldemallorca.helium.model.hibernate.Validacio;
import net.conselldemallorca.helium.model.hibernate.Camp.TipusCamp;
import net.sf.cglib.beans.BeanGenerator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Validator;
import org.springmodules.validation.bean.BeanValidator;
import org.springmodules.validation.bean.conf.DefaultBeanValidationConfiguration;
import org.springmodules.validation.bean.conf.loader.SimpleBeanValidationConfigurationLoader;
import org.springmodules.validation.bean.rule.ExpressionValidationRule;
import org.springmodules.validation.util.cel.valang.ValangConditionExpressionParser;


/**
 * Mètodes comuns per a la gestió de formularis de tasca
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class TascaFormUtil {

	@SuppressWarnings("unchecked")
	public static Object getCommandForTasca(
			TascaDto tasca,
			Map<String, Object> campsAddicionals,
			Map<String, Class> campsAddicionalsClasses) {
		List<Camp> camps = new ArrayList<Camp>();
		for (CampTasca campTasca: tasca.getCamps())
			camps.add(campTasca.getCamp());
		return getCommandForCamps(
				camps,
				tasca.getVariables(),
				campsAddicionals,
				campsAddicionalsClasses,
				false);
	}
	@SuppressWarnings("unchecked")
	public static Object getCommandForFiltre(
			List<Camp> camps,
			Map<String, Object> valors,
			Map<String, Object> campsAddicionals,
			Map<String, Class> campsAddicionalsClasses) {
		return getCommandForCamps(
				camps,
				valors,
				campsAddicionals,
				campsAddicionalsClasses,
				true);
	}

	public static Map<String, Object> valorsFromCommand(
			List<Camp> camps,
			Object command,
			boolean revisarArrays,
			boolean perFiltre) {
    	Map<String, Object> resposta = new HashMap<String, Object>();
    	for (Camp camp: camps) {
    		if (!camp.getTipus().equals(TipusCamp.REGISTRE)) {
	    		try {
		    		String campCodi = getCampCodi(camp, perFiltre);
		    		Object valor = PropertyUtils.getSimpleProperty(command, campCodi);
		    		if (camp.isMultiple() && revisarArrays) {
	    				// Lleva els valors buits de l'array
		    			int tamany = 0;
		    			for (int i = 0; i < Array.getLength(valor); i++) {
		    				Object va = Array.get(valor, i);
		    				if (va != null && !"".equals(va))
		    					tamany++;
		    			}
		    			Object newArray = cloneMultipleArray(
		    					campCodi,
		    					command,
		    					camps,
		    					tamany - Array.getLength(valor));
		    			int index = 0;
		    			for (int i = 0; i < Array.getLength(valor); i++) {
		    				Object va = Array.get(valor, i);
		    				if (va != null && !"".equals(va))
		    					Array.set(newArray, index++, va);
		    			}
		    			if (Array.getLength(newArray) > 0)
		    				resposta.put(campCodi, newArray);
		    			else
		    				resposta.put(campCodi, null);
		    		} else {
		    			resposta.put(campCodi, valor);
		    		}
	    		} catch (Exception ignored) {}
    		}
    	}
    	return resposta;
	}

	public static Object addMultiple(String field, Object command, List<Camp> camps) throws Exception {
		Object value = PropertyUtils.getSimpleProperty(command, field);
		Object newArray = cloneMultipleArray(field, command, camps, 1);
		for (int i = 0; i < Array.getLength(newArray) - 1; i++)
			Array.set(newArray, i, Array.get(value, i));
		return newArray;
	}
	public static Object deleteMultiple(String field, Object command, List<Camp> camps, int index) throws Exception {
		Object value = PropertyUtils.getSimpleProperty(command, field);
		Object newArray = cloneMultipleArray(field, command, camps, -1);
		int j = 0;
		for (int i = 0; i < Array.getLength(value); i++)
			if (i != index)
				Array.set(newArray, j++, Array.get(value, i));
		return newArray;
	}

	public static Validator getBeanValidatorForCommand(TascaDto tasca) {
		SimpleBeanValidationConfigurationLoader validationConfigurationLoader = new SimpleBeanValidationConfigurationLoader();
		DefaultBeanValidationConfiguration beanValidationConfiguration = new DefaultBeanValidationConfiguration();
		for (CampTasca campTasca: tasca.getCamps()) {
			for (Validacio validacio: campTasca.getCamp().getValidacions()) {
				ExpressionValidationRule validationRule = new ExpressionValidationRule(
						new ValangConditionExpressionParser(),
						validacio.getExpressio());
				String codiError = "error.tasca." + tasca.getDefinicioProces().getJbpmKey() + "." +tasca.getJbpmName() + "." + campTasca.getCamp().getCodi();
				validationRule.setErrorCode(codiError);
				validationRule.setDefaultErrorMessage(validacio.getMissatge());
				beanValidationConfiguration.addPropertyRule(
						campTasca.getCamp().getCodi(),
						validationRule);
			}
			if (	campTasca.getCamp().getTipus().equals(TipusCamp.STRING) ||
					campTasca.getCamp().getTipus().equals(TipusCamp.TEXTAREA)) {
				ExpressionValidationRule validationRule = new ExpressionValidationRule(
						new ValangConditionExpressionParser(),
						"length(" + campTasca.getCamp().getCodi() + ") < 2049");
				validationRule.setErrorCode("max.length");
				validationRule.setDefaultErrorMessage("El contingut d'aquest camp excedeix la llargada màxima");
				beanValidationConfiguration.addPropertyRule(
						campTasca.getCamp().getCodi(),
						validationRule);
			}
		}
		validationConfigurationLoader.setClassValidation(
				Object.class,
				beanValidationConfiguration);
		return new BeanValidator(validationConfigurationLoader);
	}

	public static Map<String, List<String>> getValorsPerSuggest(TascaDto tasca, Object command) {
		Map<String, List<String>> resposta = new HashMap<String, List<String>>();
		if (tasca.getValorsMultiplesDomini() != null) {
			for (String key: tasca.getValorsMultiplesDomini().keySet()) {
				List<String> liniaResposta = new ArrayList<String>();
				try {
					Object value = PropertyUtils.getSimpleProperty(command, key);
					for (int i = 0; i < Array.getLength(value); i++) {
						String valor = null;
						for (ParellaCodiValor parella: tasca.getValorsMultiplesDomini().get(key)) {
							if (parella.getCodi().equals(Array.get(value, i))) {
								valor = parella.getValor().toString();
								break;
							}
						}
						liniaResposta.add(valor);
					}
					resposta.put(key, liniaResposta);
				} catch (Exception ex) {}
			}
		}
		return resposta;
	}



	private static Object cloneMultipleArray(
			String field,
			Object command,
			List<Camp> camps,
			int addTolength) throws Exception {
		for (Camp camp: camps) {
			if (camp.getCodi().equals(field)) {
				Object value = PropertyUtils.getSimpleProperty(command, field);
				if (value != null) {
					int length = ((Object[])value).length;
					return Array.newInstance(
							camp.getJavaClass(),
							length + addTolength);
				} else {
					return Array.newInstance(
							camp.getJavaClass(),
							1);
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static Object getCommandForCamps(
			List<Camp> camps,
			Map<String, Object> valors,
			Map<String, Object> campsAddicionals,
			Map<String, Class> campsAddicionalsClasses,
			boolean perFiltre) {
		// Empram cglib per generar el command de manera dinàmica
		BeanGenerator bg = new BeanGenerator();
		if (campsAddicionalsClasses != null) {
			for (String codi: campsAddicionalsClasses.keySet())
				bg.addProperty(codi, campsAddicionalsClasses.get(codi));
		}
		for (Camp camp: camps) {
			String campCodi = getCampCodi(camp, perFiltre);
			if (camp.getTipus() != null)  {
				boolean ambArray;
				if (!perFiltre) {
					ambArray = camp.isMultiple();
				} else {
					ambArray = 	camp.getTipus().equals(TipusCamp.DATE) ||
								camp.getTipus().equals(TipusCamp.INTEGER) ||
								camp.getTipus().equals(TipusCamp.FLOAT) ||
								camp.getTipus().equals(TipusCamp.PRICE);
				}
				if (ambArray) {
					bg.addProperty(
							campCodi,
							Array.newInstance(camp.getJavaClass(), 1).getClass());
				} else {
					bg.addProperty(
							campCodi,
							camp.getJavaClass());
				}
			} else {
				bg.addProperty(
						campCodi,
						Object.class);
			}
		}
		// Omplit el command amb els valors per defecte
		Object command = bg.create();
		try {
			if (campsAddicionals != null) {
				for (String codi: campsAddicionals.keySet()) {
					PropertyUtils.setSimpleProperty(command, codi, campsAddicionals.get(codi));
				}
			}
			// Inicialitza els camps del command amb els valors de la tasca
			for (Camp camp: camps) {
				String campCodi = getCampCodi(camp, perFiltre);
				boolean ambArray;
				if (!perFiltre) {
					ambArray = camp.isMultiple();
				} else {
					ambArray = 	camp.getTipus().equals(TipusCamp.DATE) ||
								camp.getTipus().equals(TipusCamp.INTEGER) ||
								camp.getTipus().equals(TipusCamp.FLOAT) ||
								camp.getTipus().equals(TipusCamp.PRICE);
				}
				if (ambArray) {
					if (valors != null && valors.get(campCodi) != null) {
						PropertyUtils.setSimpleProperty(
								command,
								campCodi,
								valors.get(campCodi));
					} else {
						PropertyUtils.setSimpleProperty(
								command,
								campCodi,
								Array.newInstance(
										camp.getJavaClass(),
										(perFiltre) ? 2 : 1));
					}
				} else {
					PropertyUtils.setSimpleProperty(
							command,
							campCodi,
							(valors != null) ? valors.get(campCodi) : null);
				}
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut afegir el camp al command", ex);
		}
		return command;
	}

	private static String getCampCodi(Camp camp, boolean perFiltre) {
		if (perFiltre)
			return camp.getDefinicioProces().getJbpmKey() + "_" + camp.getCodi();
		else
			return camp.getCodi();
	}

	private static final Log logger = LogFactory.getLog(TascaFormUtil.class);

}
