/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampRegistreDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.ValidacioDto;
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
public class TascaFormHelper {

	private static final String VARIABLE_SESSIO_COMMAND_TMP = "TascaFormUtil_CommandSessioTmp";



	@SuppressWarnings("rawtypes")
	public static Object getCommandForTasca(
			ExpedientTascaDto tasca,
			Map<String, Object> campsAddicionals,
			Map<String, Class> campsAddicionalsClasses) {
		List<CampDto> camps = new ArrayList<CampDto>();
		for (CampTascaDto campTasca: tasca.getCamps())
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
			List<CampDto> camps,
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
			CampDto camp,
			Map<String, Object> valors,
			Map<String, Object> campsAddicionals,
			Map<String, Class> campsAddicionalsClasses) {
		List<CampDto> camps = new ArrayList<CampDto>();
		for (CampRegistreDto campRegistre: camp.getRegistreMembres())
			camps.add(campRegistre.getMembre());
		return getCommandForCamps(
				camps,
				valors,
				campsAddicionals,
				campsAddicionalsClasses,
				false);
	}

	public static Map<String, Object> getValorsFromCommand(
			List<CampDto> camps,
			Object command,
			boolean revisarArrays,
			boolean perFiltre) {
    	Map<String, Object> resposta = new HashMap<String, Object>();
    	for (CampDto camp: camps) {
    		if (!camp.getTipus().equals(CampTipusDto.REGISTRE) && !camp.getTipus().equals(CampTipusDto.ACCIO)) {
	    		try {
		    		String campCodi = getCampCodi(camp, perFiltre, true);
		    		Object valor = PropertyUtils.getSimpleProperty(command, campCodi);
		    		if (camp.getTipus().equals(CampTipusDto.BOOLEAN) && valor == null) {
		    			valor = Boolean.FALSE;
		    		}
		    		if (!perFiltre && camp.isMultiple() && revisarArrays) {
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
		    				resposta.put(
		    						getCampCodi(camp, perFiltre, false),
		    						newArray);
		    			else
		    				resposta.put(
		    						getCampCodi(camp, perFiltre, false),
		    						null);
		    		} else {
		    			resposta.put(
		    					getCampCodi(camp, perFiltre, false),
		    					valor);
		    		}
	    		} catch (Exception ignored) {}
    		}
    	}
    	return resposta;
	}

	public static Object addMultiple(String field, Object command, List<CampDto> camps) throws Exception {
		Object value = PropertyUtils.getSimpleProperty(command, field);
		Object newArray = cloneMultipleArray(field, command, camps, 1);
		for (int i = 0; i < Array.getLength(newArray) - 1; i++)
			Array.set(newArray, i, Array.get(value, i));
		return newArray;
	}
	public static Object deleteMultiple(String field, Object command, List<CampDto> camps, int index) throws Exception {
		Object value = PropertyUtils.getSimpleProperty(command, field);
		Object newArray = cloneMultipleArray(field, command, camps, -1);
		int j = 0;
		for (int i = 0; i < Array.getLength(value); i++)
			if (i != index)
				Array.set(newArray, j++, Array.get(value, i));
		return newArray;
	}

	public static Validator getBeanValidatorForCommand(List<CampDto> camps) {
		SimpleBeanValidationConfigurationLoader validationConfigurationLoader = new SimpleBeanValidationConfigurationLoader();
		DefaultBeanValidationConfiguration beanValidationConfiguration = new DefaultBeanValidationConfiguration();
		for (CampDto camp: camps) {
			for (ValidacioDto validacio: camp.getValidacions()) {
				ExpressionValidationRule validationRule = new ExpressionValidationRule(
						new ValangConditionExpressionParser(),
						validacio.getExpressio());
				String codiError = "error.camp." + camp.getCodi();
				validationRule.setErrorCode(codiError);
				validationRule.setDefaultErrorMessage(camp.getEtiqueta() + ": " + validacio.getMissatge());
				beanValidationConfiguration.addPropertyRule(
						camp.getCodi(),
						validationRule);
			}
			if (	camp.getTipus().equals(CampTipusDto.STRING)) {// ||
					//camp.getTipus().equals(TipusCamp.TEXTAREA)) {
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

	public static Map<String, List<Object>> getValorsPerSuggest(ExpedientTascaDto tasca, Object command) {
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
			List<CampDto> camps,
			Map<String, Object> valors,
			Map<String, Object> campsAddicionals,
			Map<String, Class> campsAddicionalsClasses,
			boolean perFiltre) {
		// Empram cglib per generar el command de manera dinàmica
		BeanGenerator bg = new BeanGenerator();
		if (campsAddicionalsClasses != null) {
			for (String codi: campsAddicionalsClasses.keySet()) {
				bg.addProperty(codi, campsAddicionalsClasses.get(codi));
			}
		}
		for (CampDto camp: camps) {
			if (!camp.getTipus().equals(TipusCamp.REGISTRE)) {
				String campCodi = getCampCodi(camp, perFiltre, true);
				if (camp.getTipus() != null)  {
					if (!perFiltre) {
						if (camp.isMultiple())
							bg.addProperty(
									campCodi,
									Array.newInstance(camp.getJavaClass(), 1).getClass());
						else 
							bg.addProperty(
									campCodi,
									camp.getJavaClass());
					} else {
						boolean ambArray = 	camp.getTipus().equals(CampTipusDto.DATE) ||
									camp.getTipus().equals(CampTipusDto.INTEGER) ||
									camp.getTipus().equals(CampTipusDto.FLOAT) ||
									camp.getTipus().equals(CampTipusDto.PRICE);
						if (ambArray)
							bg.addProperty(
									campCodi,
									Array.newInstance(camp.getJavaClass(), 2).getClass());
						else
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
		}
		
		Object command = bg.create();
		
		// Inicialitza els camps del command amb els valors de la tasca
		for (CampDto camp: camps) {
			if (!camp.getTipus().equals(TipusCamp.REGISTRE)) {
				String campCodi = getCampCodi(camp, perFiltre, true);
				String campCodiValors = getCampCodi(camp, perFiltre, false);
				String tipusCommand = null;
				try {
					Class propertyType = PropertyUtils.getPropertyType(command, campCodi);
					tipusCommand = (propertyType != null) ? propertyType.getName() : null;
					boolean ambArray;
					if (!perFiltre)
						ambArray = camp.isMultiple();
					else
						ambArray = 	camp.getTipus().equals(CampTipusDto.DATE) ||
									camp.getTipus().equals(CampTipusDto.INTEGER) ||
									camp.getTipus().equals(CampTipusDto.FLOAT) ||
									camp.getTipus().equals(CampTipusDto.PRICE);
					if (ambArray) {
						if (valors != null && getValueClass(campCodiValors, camp, valors) != null)
							PropertyUtils.setSimpleProperty(
									command,
									campCodi,
									getValueClass(campCodiValors, camp, valors));
						else
							PropertyUtils.setSimpleProperty(
									command,
									campCodi,
									Array.newInstance(
											camp.getJavaClass(),
											(perFiltre) ? 2 : 1));
					} else
						PropertyUtils.setSimpleProperty(
								command,
								campCodi,
								(valors != null) && (valors.get(campCodiValors) != null) ? getValueClass(campCodiValors, camp, valors) : null);
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp '" + campCodi + "' al command (" + tipusCommand + ")", ex);
				}
			}
		}
		
		if (campsAddicionals != null) {
			for (String codi: campsAddicionals.keySet()) {
				try {
					PropertyUtils.setSimpleProperty(
							command,
							codi,
							campsAddicionals.containsKey(codi) ? campsAddicionals.get(codi) : null);
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp addicional '" + codi + "'", ex);
				}
			}
		}
		
		return command;
	}

	public static String getCampCodi(
			CampDto camp,
			boolean perFiltre,
			boolean evitarProblema) {
		if (perFiltre) {
			if (camp.getCodi().startsWith(ExpedientCamps.EXPEDIENT_PREFIX) || camp.getDefinicioProces() == null) {
				return camp.getCodi();
			} else {
				String definicioProcesKey = camp.getDefinicioProces().getJbpmKey();
				// Per evitar el problema amb cglib quan la propietat
				// comença amb majúscula+minúscula
				if (evitarProblema && definicioProcesKey.matches("^[A-Z]{1}[a-z]{1}.*"))
					definicioProcesKey = definicioProcesKey.substring(0, 1).toLowerCase() + definicioProcesKey.substring(1);
				//
				return definicioProcesKey + "_" + camp.getCodi();
			}
		} else {
			return camp.getCodi();
		}
	}
	
	private static Object getValueClass(String codi, CampDto camp, Map<String, Object> valors) {
		return valors.get(codi);
	}	

	private static Object cloneMultipleArray(
			String field,
			Object command,
			List<CampDto> camps,
			int addTolength) throws Exception {
		for (CampDto camp: camps) {
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

	private static final Log logger = LogFactory.getLog(TascaFormHelper.class);
}
