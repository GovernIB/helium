/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Validacio;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
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
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaFormUtil {

	private static final String VARIABLE_SESSIO_COMMAND_TMP = "TascaFormUtil_CommandSessioTmp";



	@SuppressWarnings("rawtypes")
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
	@SuppressWarnings("rawtypes")
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
	@SuppressWarnings("rawtypes")
	public static Object getCommandForRegistre(
			Camp camp,
			Map<String, Object> valors,
			Map<String, Object> campsAddicionals,
			Map<String, Class> campsAddicionalsClasses) {
		List<Camp> camps = new ArrayList<Camp>();
		for (CampRegistre campRegistre: camp.getRegistreMembres())
			camps.add(campRegistre.getMembre());
		return getCommandForCamps(
				camps,
				valors,
				campsAddicionals,
				campsAddicionalsClasses,
				false);
	}

	public static Map<String, Object> getValorsFromCommand(
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

	public static Validator getBeanValidatorForCommand(List<Camp> camps) {
		SimpleBeanValidationConfigurationLoader validationConfigurationLoader = new SimpleBeanValidationConfigurationLoader();
		DefaultBeanValidationConfiguration beanValidationConfiguration = new DefaultBeanValidationConfiguration();
		for (Camp camp: camps) {
			for (Validacio validacio: camp.getValidacions()) {
				ExpressionValidationRule validationRule = new ExpressionValidationRule(
						new ValangConditionExpressionParser(),
						validacio.getExpressio());
				String codiError = "error.camp." + camp.getCodi();
				validationRule.setErrorCode(codiError);
				validationRule.setDefaultErrorMessage(validacio.getMissatge());
				beanValidationConfiguration.addPropertyRule(
						camp.getCodi(),
						validationRule);
			}
			if (	camp.getTipus().equals(TipusCamp.STRING) ||
					camp.getTipus().equals(TipusCamp.TEXTAREA)) {
				ExpressionValidationRule validationRule = new ExpressionValidationRule(
						new ValangConditionExpressionParser(),
						camp.getCodi() + " is null or length(" + camp.getCodi() + ") < 2049");
				validationRule.setErrorCode("max.length");
				validationRule.setDefaultErrorMessage("El contingut d'aquest camp excedeix la llargada màxima");
				beanValidationConfiguration.addPropertyRule(
						camp.getCodi(),
						validationRule);
			}
		}
		validationConfigurationLoader.setClassValidation(
				Object.class,
				beanValidationConfiguration);
		return new BeanValidator(validationConfigurationLoader);
	}

	public static Map<String, List<Object>> getValorsPerSuggest(TascaDto tasca, Object command) {
		Map<String, List<Object>> resposta = new HashMap<String, List<Object>>();
		if (tasca.getValorsMultiplesDomini() != null) {
			for (String key: tasca.getValorsMultiplesDomini().keySet()) {
				List<Object> liniaResposta = new ArrayList<Object>();
				try {
					Object value = PropertyUtils.getSimpleProperty(command, key);
					for (int i = 0; i < Array.getLength(value); i++) {
						String valor = null;
						for (ParellaCodiValorDto parella: tasca.getValorsMultiplesDomini().get(key)) {
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

	public static void guardarCommandTemporal(
			HttpServletRequest request,
			Object command) {
		request.getSession().setAttribute(VARIABLE_SESSIO_COMMAND_TMP, command);
	}
	public static Object recuperarCommandTemporal(
			HttpServletRequest request,
			boolean esborrar) {
		Object command = request.getSession().getAttribute(VARIABLE_SESSIO_COMMAND_TMP);
		if (command != null && esborrar)
			request.getSession().removeAttribute(VARIABLE_SESSIO_COMMAND_TMP);
		return command;
	}
	@SuppressWarnings("rawtypes")
	public static Object getCommandForCamps(
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
		if (campsAddicionals != null) {
			for (String codi: campsAddicionals.keySet()) {
				String tipusCommand = null;
				String tipusCamp = null;
				if (campsAddicionals.get(codi) != null)
					tipusCamp = campsAddicionals.get(codi).getClass().getName();
				try {
					tipusCommand = PropertyUtils.getPropertyType(command, codi).getName();
					PropertyUtils.setSimpleProperty(command, codi, campsAddicionals.get(codi));
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp addicional '" + codi + "' al command (" + tipusCommand + ", " + tipusCamp + ")", ex);
				}
			}
		}
		
		// Inicialitza els camps del command amb els valors de la tasca
		for (Camp camp: camps) {
			if (!camp.getTipus().equals(TipusCamp.REGISTRE)) {
				String campCodi = getCampCodi(camp, perFiltre);
				String tipusCommand = null;
				String tipusCamp = (valors != null && valors.get(campCodi) != null) ? valors.get(campCodi).getClass().getName() : null;
				try {
					tipusCommand = PropertyUtils.getPropertyType(command, campCodi).getName();
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
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp '" + campCodi + "' al command (" + tipusCommand + ", " + tipusCamp + ")", ex);
				}
			}
		}
		return command;
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

	private static String getCampCodi(Camp camp, boolean perFiltre) {
		if (perFiltre)
			return camp.getDefinicioProces().getJbpmKey() + "_" + camp.getCodi();
		else
			return camp.getCodi();
	}

	private static final Log logger = LogFactory.getLog(TascaFormUtil.class);

}
