/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
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
	public static Object getCommandForFiltre(
			List<TascaDadaDto> campsFiltre,
			Map<String, Object> valors,
			Map<String, Object> campsAddicionals,
			Map<String, Class> campsAddicionalsClasses) {
		return getCommandForCamps(
				campsFiltre,
				valors,
				campsAddicionals,
				campsAddicionalsClasses,
				true);
	}
	@SuppressWarnings("rawtypes")
	public static Object getCommandForRegistre(
			TascaDadaDto camp,
			Map<String, Object> valors,
			Map<String, Object> campsAddicionals,
			Map<String, Class> campsAddicionalsClasses) {
		return getCommandForCamps(
				camp.getRegistreDades(),
				valors,
				campsAddicionals,
				campsAddicionalsClasses,
				false);
	}
	
	public static Map<String, Object> getValorsFromCommand(
			List<TascaDadaDto> tascaDadas,
			Object command,
			boolean perFiltre) {
    	Map<String, Object> resposta = new HashMap<String, Object>();
    	for (TascaDadaDto camp: tascaDadas) {
    		if (!camp.getCampTipus().equals(CampTipusDto.REGISTRE) && !camp.getCampTipus().equals(CampTipusDto.ACCIO)) {
	    		try {
		    		Object valor = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
		    		if (camp.getCampTipus().equals(CampTipusDto.BOOLEAN) && valor == null) {
		    			valor = Boolean.FALSE;
		    		}
		    		if (!perFiltre && camp.isCampMultiple()) {
	    				// Lleva els valors buits de l'array
		    			int tamany = 0;
		    			for (int i = 0; i < Array.getLength(valor); i++) {
		    				Object va = Array.get(valor, i);
		    				if (va != null && !"".equals(va))
		    					tamany++;
		    			}
		    			Object newArray = cloneMultipleArray(
		    					camp.getVarCodi(),
		    					command,
		    					tascaDadas,
		    					tamany - Array.getLength(valor));
		    			int index = 0;
		    			for (int i = 0; i < Array.getLength(valor); i++) {
		    				Object va = Array.get(valor, i);
		    				if (va != null && !"".equals(va))
		    					Array.set(newArray, index++, va);
		    			}
		    			if (Array.getLength(newArray) > 0)
		    				resposta.put(
		    						camp.getVarCodi(),
		    						newArray);
		    			else
		    				resposta.put(
		    						camp.getVarCodi(),
		    						null);
		    		} else {
		    			resposta.put(
		    					camp.getVarCodi(),
		    					valor);
		    		}
	    		} catch (Exception ignored) {}
    		}
    	}
    	return resposta;
	}

	public static Object addMultiple(String field, Object command, List<TascaDadaDto> tascaDadas) throws Exception {
		Object value = PropertyUtils.getSimpleProperty(command, field);
		Object newArray = cloneMultipleArray(field, command, tascaDadas, 1);
		for (int i = 0; i < Array.getLength(newArray) - 1; i++)
			Array.set(newArray, i, Array.get(value, i));
		return newArray;
	}
	public static Object deleteMultiple(String field, Object command, List<TascaDadaDto> tascaDadas, int index) throws Exception {
		Object value = PropertyUtils.getSimpleProperty(command, field);
		Object newArray = cloneMultipleArray(field, command, tascaDadas, -1);
		int j = 0;
		for (int i = 0; i < Array.getLength(value); i++)
			if (i != index)
				Array.set(newArray, j++, Array.get(value, i));
		return newArray;
	}

	public static Validator getBeanValidatorForCommand(List<TascaDadaDto> tascaDadas) {
		SimpleBeanValidationConfigurationLoader validationConfigurationLoader = new SimpleBeanValidationConfigurationLoader();
		DefaultBeanValidationConfiguration beanValidationConfiguration = new DefaultBeanValidationConfiguration();
		for (TascaDadaDto camp: tascaDadas) {			
			for (ValidacioDto validacio: camp.getValidacions()) {
				ExpressionValidationRule validationRule = new ExpressionValidationRule(
						new ValangConditionExpressionParser(),
						validacio.getExpressio());
				String codiError = "error.camp." + camp.getVarCodi();
				validationRule.setErrorCode(codiError);
				validationRule.setDefaultErrorMessage(camp.getCampEtiqueta() + ": " + validacio.getMissatge());
				beanValidationConfiguration.addPropertyRule(
						camp.getVarCodi(),
						validationRule);
			}
			if (	camp.getCampTipus().equals(CampTipusDto.STRING)) {// ||
					//camp.getTipus().equals(CampTipusDto.TEXTAREA)) {
				ExpressionValidationRule validationRule = new ExpressionValidationRule(
						new ValangConditionExpressionParser(),
						camp.getVarCodi() + " is null or length(" + camp.getVarCodi() + ") < 2049");
				validationRule.setErrorCode("max.length");
				validationRule.setDefaultErrorMessage("El contingut d'aquest camp excedeix la llargada màxima");
				beanValidationConfiguration.addPropertyRule(
						camp.getVarCodi(),
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
//		if (tasca.getValorsMultiplesDomini() != null) {
//			for (String key: tasca.getValorsMultiplesDomini().keySet()) {
//				List<Object> liniaResposta = new ArrayList<Object>();
//				try {
//					Object value = PropertyUtils.getSimpleProperty(command, key);
//					for (int i = 0; i < Array.getLength(value); i++) {
//						String valor = null;
//						for (ParellaCodiValorDto parella: tasca.getValorsMultiplesDomini().get(key)) {
//							if (parella.getCodi().equals(Array.get(value, i))) {
//								valor = parella.getValor().toString();
//								break;
//							}
//						}
//						liniaResposta.add(valor);
//					}
//					resposta.put(key, liniaResposta);
//				} catch (Exception ex) {}
//			}
//		}
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
			List<TascaDadaDto> tascaDadas,
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
		for (TascaDadaDto camp: tascaDadas) {
			if (!camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
				if (camp.getCampTipus() != null)  {
					if (!perFiltre) {
						if (camp.isCampMultiple())
							bg.addProperty(
									camp.getVarCodi(),
									Array.newInstance(camp.getJavaClass(), 1).getClass());
						else 
							bg.addProperty(
									camp.getVarCodi(),
									camp.getJavaClass());
					} else {
						boolean ambArray = 	camp.getCampTipus().equals(CampTipusDto.DATE) ||
									camp.getCampTipus().equals(CampTipusDto.INTEGER) ||
									camp.getCampTipus().equals(CampTipusDto.FLOAT) ||
									camp.getCampTipus().equals(CampTipusDto.PRICE);
						if (ambArray)
							bg.addProperty(
									camp.getVarCodi(),
									Array.newInstance(camp.getJavaClass(), 2).getClass());
						else
							bg.addProperty(
									camp.getVarCodi(),
									camp.getJavaClass());
					}
				} else {
					bg.addProperty(
							camp.getVarCodi(),
							Object.class);
				}
			}			
		}
		
		Object command = bg.create();
		
		// Inicialitza els camps del command amb els valors de la tasca
		for (TascaDadaDto camp: tascaDadas) {
			if (!camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
				String tipusCommand = null;
				try {
					Class propertyType = PropertyUtils.getPropertyType(command, camp.getVarCodi());
					tipusCommand = (propertyType != null) ? propertyType.getName() : null;
					boolean ambArray;
					if (!perFiltre)
						ambArray = camp.isCampMultiple();
					else
						ambArray = 	camp.getCampTipus().equals(CampTipusDto.DATE) ||
									camp.getCampTipus().equals(CampTipusDto.INTEGER) ||
									camp.getCampTipus().equals(CampTipusDto.FLOAT) ||
									camp.getCampTipus().equals(CampTipusDto.PRICE);
					if (ambArray) {
						if (valors != null && valors.get(camp.getVarCodi()) != null)
							PropertyUtils.setSimpleProperty(
									command,
									camp.getVarCodi(),
									valors.get(camp.getVarCodi()));
						else
							PropertyUtils.setSimpleProperty(
									command,
									camp.getVarCodi(),
									Array.newInstance(
											camp.getJavaClass(),
											(perFiltre) ? 2 : 1));
					} else
						PropertyUtils.setSimpleProperty(
								command,
								camp.getVarCodi(),
								(valors != null) && (valors.get(camp.getVarCodi()) != null) ? valors.get(camp.getVarCodi()) : null);
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp '" + camp.getVarCodi() + "' al command (" + tipusCommand + ")", ex);
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

	private static Object cloneMultipleArray(
			String field,
			Object command,
			List<TascaDadaDto> tascaDadas,
			int addTolength) throws Exception {
		for (TascaDadaDto camp: tascaDadas) {
			if (camp.getVarCodi().equals(field)) {
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
