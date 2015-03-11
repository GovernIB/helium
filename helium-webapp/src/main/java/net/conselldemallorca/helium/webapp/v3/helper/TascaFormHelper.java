/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
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



	public static Object getCommandForFiltre(
			List<TascaDadaDto> campsFiltre,
			Map<String, Object> valors,
			Map<String, Object> campsAddicionals,
			Map<String, Class<?>> campsAddicionalsClasses) {
		return getCommandForCamps(
				campsFiltre,
				valors,
				campsAddicionals,
				campsAddicionalsClasses,
				true);
	}
	public static Object getCommandForRegistre(
			TascaDadaDto camp,
			Map<String, Object> valors,
			Map<String, Object> campsAddicionals,
			Map<String, Class<?>> campsAddicionalsClasses) {
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
    		try {
    			if (!camp.getCampTipus().equals(CampTipusDto.ACCIO)) {
		    		Object valor = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
	    			if (camp.isReadOnly() && !camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
	    				valor = camp.getVarValor();
	    				PropertyUtils.setSimpleProperty(
								command,
								camp.getVarCodi(),
								valor);
	    			} else if (camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
    					valor = getArrayFromRegistre(camp, valor);
    				}
		    		if (!perFiltre && camp.isCampMultiple()) {
	    				// Lleva els valors buits de l'array
		    			List<Object> valorSenseBiuts = new ArrayList<Object>();
		    			if (valor != null)
			    			for (int i = 0; i < Array.getLength(valor); i++) {
			    				Object va = Array.get(valor, i);
			    				if (camp.getCampTipus().equals(CampTipusDto.BOOLEAN) && va == null) {
					    			va = Boolean.FALSE;
					    		}
			    				if (!empty(va)) {
			    					if (camp.getCampTipus().equals(CampTipusDto.TERMINI)) {
				    					va = ((TerminiDto)va).toSavinString();
				    				}
			    					valorSenseBiuts.add(va);
			    				}
			    			}
		    			Object newArray = null;
		    			if (!valorSenseBiuts.isEmpty()) {
	    					newArray = Array.newInstance(camp.getCampTipus().equals(CampTipusDto.TERMINI) ? String.class : camp.getJavaClass(), valorSenseBiuts.size());
		    				int index = 0;
		    				for (Object val: valorSenseBiuts) {
		    					Array.set(newArray, index++, val);
		    				}
		    			}
	    				resposta.put(
	    						camp.getVarCodi(),
	    						newArray);
		    		} else {
		    			if (camp.getCampTipus().equals(CampTipusDto.TERMINI) && valor != null) {
	    					valor = ((TerminiDto)valor).toSavinString();
	    				}
		    			if (camp.getCampTipus().equals(CampTipusDto.BOOLEAN) && valor == null) {
			    			valor = Boolean.FALSE;
			    		}
		    			resposta.put(
		    					camp.getVarCodi(),
		    					valor);
		    		}
    			}
    		} catch (Exception ignored) {}
    	}
    	return resposta;
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
	
	public static Object getCommandForCamps(
			List<TascaDadaDto> tascaDadas,
			HttpServletRequest request) {
		return getCommandForCamps(tascaDadas, null, null, null, false);
	}
	
	public static Object getCommandForCampsExpedient(List<ExpedientDadaDto> expDadas, Map<String, Object> valors) {
		List<TascaDadaDto> tascaDadas = new ArrayList<TascaDadaDto>();
		for (ExpedientDadaDto expdada: expDadas) {
			TascaDadaDto tascaDada = toTascaDadaDto(expdada);
			tascaDadas.add(tascaDada);
		}
		
		return getCommandForCamps(tascaDadas, valors, null, null, false);
	}
	
	public static TascaDadaDto toTascaDadaDto(ExpedientDadaDto expdada) {
		TascaDadaDto tascaDada = new TascaDadaDto();
		tascaDada.setCampTipus(expdada.getCampTipus());
		tascaDada.setCampMultiple(expdada.isCampMultiple());
		tascaDada.setVarCodi(expdada.getVarCodi());
		tascaDada.setCampId(expdada.getCampId());
		tascaDada.setText(expdada.getText());
		tascaDada.setCampEtiqueta(expdada.getCampEtiqueta());
		tascaDada.setRequired(false);
		tascaDada.setValidacions(expdada.getValidacions());
		
		if (expdada.getMultipleDades() != null && !expdada.getMultipleDades().isEmpty()) {
			List<TascaDadaDto> dadesMult = new ArrayList<TascaDadaDto>();
			for(ExpedientDadaDto dadaMult : expdada.getMultipleDades()) {
				dadesMult.add(toTascaDadaDto(dadaMult));
			}
			tascaDada.setMultipleDades(dadesMult);
		}
		if (expdada.getRegistreDades() != null && !expdada.getRegistreDades().isEmpty()) {
			List<TascaDadaDto> dadesReg = new ArrayList<TascaDadaDto>();
			for(ExpedientDadaDto dadaReg : expdada.getRegistreDades()) {
				dadesReg.add(toTascaDadaDto(dadaReg));
			}
			tascaDada.setRegistreDades(dadesReg);
		}
		return tascaDada;
	}
	
	public static Object getCommandForCamps(
			List<TascaDadaDto> tascaDadas,
			Map<String, Object> valors,
			Map<String, Object> campsAddicionals,
			Map<String, Class<?>> campsAddicionalsClasses,
			boolean perFiltre) {
		Map<String, Object> registres = new HashMap<String, Object>();
		// Empram cglib per generar el command de manera dinàmica
		Object command = getCommandModelForCamps(tascaDadas, campsAddicionalsClasses, registres, perFiltre);
		
		// Inicialitza els camps del command amb els valors de la tasca
		for (TascaDadaDto camp: tascaDadas) {
			String tipusCommand = null;
			try {
				Class<?> propertyType = PropertyUtils.getPropertyType(command, camp.getVarCodi());
				tipusCommand = (propertyType != null) ? propertyType.getName() : null;
				
				// Obtenim el valor del camp
				Object valor = null;
				if (valors != null && valors.get(camp.getVarCodi()) != null) {
					valor = valors.get(camp.getVarCodi());
				} else {
					valor = camp.getVarValor();
				}
				
				if (!camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
					// Camps múltiples
					if (isCampMultiple(camp, perFiltre)) {
						Object valorMultiple = null;
						if (valor != null) {
							if (camp.getCampTipus().equals(CampTipusDto.TERMINI)) {
								int mida = 1;
								if (valor instanceof Object[]) {
									mida = ((Object[])valor).length;
									TerminiDto[] terminis = new TerminiDto[mida];
									int i = 0;
									for (String term: (String[])valor) {
										terminis[i++] = new TerminiDto(term);
									}
									valorMultiple = terminis;
								} else {
									valorMultiple = new TerminiDto[] {new TerminiDto((String)valor)};
								}
							} else if (!(valor instanceof Object[])) {
								valorMultiple = Array.newInstance(camp.getJavaClass(), 1);
								((Object[])valorMultiple)[0] = valor;
							} else {
								valorMultiple = valor;
							}
							PropertyUtils.setSimpleProperty(
									command,
									camp.getVarCodi(),
									valorMultiple);
						} else {
							PropertyUtils.setSimpleProperty(
									command,
									camp.getVarCodi(),
									Array.newInstance(camp.getJavaClass(), perFiltre ? 2 : 1));
						}
					// Camps senzills
					} else {
						if (camp.getCampTipus().equals(CampTipusDto.TERMINI)){
							if (valor != null) {
								valor = new TerminiDto((String)valor);
							} else {
								valor = new TerminiDto();
							}
						} else if (camp.getCampTipus().equals(CampTipusDto.STRING) && valor == null){
							valor = "";
						}
						PropertyUtils.setSimpleProperty(
								command,
								camp.getVarCodi(),
								valor);
					}
				// REGISTRES:
				} else if (!perFiltre) {
					// En al cas de que el camp a emplenar els valor sigui tipus registre, calcularem el seu contingut a valorRegistre:
					Object registre = registres.get(camp.getVarCodi());
					Object valorRegistre = null;
					// 3. Si el valor obtingut és null, llavors crearem un objecte Registre o Registre[1] amb els atributs buits, depenent de si és múltiple
					if (valor == null) {
						if (camp.isCampMultiple()) {
							valorRegistre = Array.newInstance(registre.getClass(), 1);
							((Object[])valorRegistre)[0] = registre;
						} else {
							valorRegistre = registre;
						}
					// 4. En cas contrari assignarem els valor obtinguts a l'objecte Registre
					} else {
						if (camp.isCampMultiple()) {
							int mida = ((Object[])valor).length;
							Object[] linies = (Object[])Array.newInstance(registre.getClass(), mida);
							
							for (int l = 0; l < mida; l++){
								linies[l] = registre.getClass().newInstance();
							}
							int i = 0; // Elements del registre
							for (TascaDadaDto campRegistre : camp.getMultipleDades().get(0).getRegistreDades()) {
								Method metodeSet = registre.getClass().getMethod(
										"set" + campRegistre.getVarCodi().substring(0, 1).toUpperCase() + campRegistre.getVarCodi().substring(1), 
										campRegistre.getJavaClass());
								int l = 0; // linies
								for (Object linia: linies){
									Object[] valin = (Object[])((Object[])valor)[l++];
									Object valent = (valin != null && valin.length > i) ? valin[i] : null; 
									metodeSet.invoke(linia, valent);
								}
								i++;
							}
							valorRegistre = linies;
						} else {
							valorRegistre = registre;
							int i = 0;
							for (TascaDadaDto campRegistre : camp.getRegistreDades()) {
								Method metodeSet = valorRegistre.getClass().getMethod(
										"set" + campRegistre.getVarCodi().substring(0, 1).toUpperCase() + campRegistre.getVarCodi().substring(1), 
										campRegistre.getJavaClass());
								Object valorReg = null;
								if (((Object[])valor).length > i)
									valorReg = ((Object[])valor)[i++];
								metodeSet.invoke(valorRegistre, valorReg);
							}
						}
					}
					// 5. Assignam el valor calculat a la propietat que representa el registre
//					PropertyUtils.setNestedProperty(
					PropertyUtils.setSimpleProperty(
							command, 
							camp.getVarCodi(),
							valorRegistre);
				}
			} catch (Exception ex) {
				logger.error("No s'ha pogut afegir el camp '" + camp.getVarCodi() + "' al command (" + tipusCommand + ")", ex);
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
	
	public static Object getCommandBuitForCamps(
			List<TascaDadaDto> tascaDadas,
			Map<String, Object> campsAddicionals,
			Map<String, Class<?>> campsAddicionalsClasses,
			boolean perFiltre) {
		Map<String, Object> registres = new HashMap<String, Object>();
		// Empram cglib per generar el command de manera dinàmica
		Object command = getCommandModelForCamps(tascaDadas, campsAddicionalsClasses, registres, perFiltre);
		// Inicialitza els camps del command amb valors buits
		for (TascaDadaDto camp: tascaDadas) {
			String tipusCommand = null;
			if (!camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
				try {
					Class<?> propertyType = PropertyUtils.getPropertyType(command, camp.getVarCodi());
					tipusCommand = (propertyType != null) ? propertyType.getName() : null;
					
					if (isCampMultiple(camp, perFiltre)) {
						PropertyUtils.setSimpleProperty(
								command,
								camp.getVarCodi(),
								Array.newInstance(camp.getJavaClass(), perFiltre ? 2 : 1));
					} else {
						Object valor = null;
						if (camp.getCampTipus().equals(CampTipusDto.TERMINI)){
							valor = new TerminiDto();
						}
						PropertyUtils.setSimpleProperty(
								command,
								camp.getVarCodi(),
								valor);
					}
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp '" + camp.getVarCodi() + "' al command (" + tipusCommand + ")", ex);
				}
			} else if (!perFiltre) {
				try {
					// En al cas de que el camp a emplenar els valor sigui tipus registre, calcularem el seu contingut a valorRegistre:
					Object registre = registres.get(camp.getVarCodi());
					Object valorRegistre = null;
					if (camp.isCampMultiple()) {
						valorRegistre = Array.newInstance(registre.getClass(), 1);
						((Object[])valorRegistre)[0] = registre;
					} else {
						valorRegistre = registre;
					}
					PropertyUtils.setSimpleProperty(
							command, 
							camp.getVarCodi(),
							valorRegistre);
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp tipus registre '" + camp.getVarCodi() + "' al command (" + tipusCommand + ")", ex);
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
	public static void ompleMultiplesBuits(
			Object command,
			List<TascaDadaDto> tascaDadas,
			boolean perFiltre) {
		Map<String, Object> registres = new HashMap<String, Object>();
		// Inicialitza els camps del command amb valors buits
		for (TascaDadaDto camp: tascaDadas) {
			String tipusCommand = null;
			if (!camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
				try {
					if (isCampMultiple(camp, perFiltre)) {
						Object valor = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
						if (valor == null)
							PropertyUtils.setSimpleProperty(
									command,
									camp.getVarCodi(),
									Array.newInstance(camp.getJavaClass(), perFiltre ? 2 : 1));
					}
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp '" + camp.getVarCodi() + "' al command", ex);
				}
			} else if (!perFiltre) {
				try {
					// En al cas de que el camp a emplenar els valor sigui tipus registre, calcularem el seu contingut a valorRegistre:
					Object registre = registres.get(camp.getVarCodi());
					Object valorRegistre = null;
					if (camp.isCampMultiple()) {
						Object valor = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
						if (valor == null) {
							valorRegistre = Array.newInstance(registre.getClass(), 1);
							((Object[])valorRegistre)[0] = registre;
							PropertyUtils.setSimpleProperty(
									command, 
									camp.getVarCodi(),
									valorRegistre);
						}
					}
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp tipus registre '" + camp.getVarCodi() + "' al command (" + tipusCommand + ")", ex);
				}
			}
		}
	}
	public static Object getCommandModelForCamps(
			List<TascaDadaDto> tascaDadas,
			Map<String, Class<?>> campsAddicionalsClasses,
			Map<String, Object> registres,
			boolean perFiltre) {
		// Empram cglib per generar el command de manera dinàmica
		BeanGenerator bg = new BeanGenerator();
		if (campsAddicionalsClasses != null) {
			for (String codi: campsAddicionalsClasses.keySet()) {
				bg.addProperty(codi, campsAddicionalsClasses.get(codi));
			}
		}
		if (registres == null) registres = new HashMap<String, Object>();
		for (TascaDadaDto camp: tascaDadas) {
			if (!camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
				if (camp.getCampTipus() != null)  {
					if (isCampMultiple(camp, perFiltre)) {
						bg.addProperty(
								camp.getVarCodi(),
								Array.newInstance(camp.getJavaClass(), 1).getClass());
					} else { 
						bg.addProperty(
								camp.getVarCodi(),
								camp.getJavaClass());
					}
				} else {
					bg.addProperty(
							camp.getVarCodi(),
							Object.class);
				}
			} else if (!perFiltre) {
				// En cas de registres cream un objecte amb els membres del registre com a atributs (l'anomenarem Registre)
				Object registre = getCommandModelForCamps(
						camp.isCampMultiple() ? camp.getMultipleDades().get(0).getRegistreDades() : camp.getRegistreDades(),
						null,
						registres,
						false);
				if (camp.isCampMultiple()) {
					// En cas de ser un registre múltiple el que cream és un array de Registre
					bg.addProperty(
							camp.getVarCodi(),
							Array.newInstance(registre.getClass(), 1).getClass());
				} else {
					bg.addProperty(camp.getVarCodi(), registre.getClass());
				}
				registres.put(camp.getVarCodi(), registre);
			}
		}
		return bg.create();
	}



	private static Object getArrayFromRegistre(TascaDadaDto camp, Object valor) throws Exception {
		try {
			if (camp.isCampMultiple()) {
				int midaLinia = camp.getMultipleDades().get(0).getRegistreDades().size();
				int mida = ((Object[])valor).length;
				Object[][] linies = new Object[mida][midaLinia];
				
				for (int l = 0; l < mida; l++) {
					Object registre = ((Object[])valor)[l];
					int i = 0;
					for (TascaDadaDto campRegistre : camp.getMultipleDades().get(0).getRegistreDades()) {
						Object oValor = PropertyUtils.getProperty(registre, campRegistre.getVarCodi());
						if (camp.isReadOnly()) {
							oValor = campRegistre.getVarValor();
						}
						if (oValor instanceof TerminiDto)
							oValor = ((TerminiDto)oValor).toSavinString();
						linies[l][i++] = oValor;
					}
				}
				return linies;
			} else {
				int midaLinia = camp.getRegistreDades().size();
				Object[] linia = new Object[midaLinia];
				int i = 0;
				for (TascaDadaDto campRegistre : camp.getRegistreDades()) {
					Object oValor = PropertyUtils.getProperty(valor, campRegistre.getVarCodi());
					if (camp.isReadOnly()) {
						oValor = campRegistre.getVarValor();
					}
					if (oValor instanceof TerminiDto)
						oValor = ((TerminiDto)oValor).toSavinString();
					linia[i++] = oValor;
				}
				return linia;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static boolean empty(Object valor) {
		boolean empty = true;
		// Registre
		if (valor instanceof Object[]) {
			for (Object membre: (Object[])valor) {
				if (!empty(membre)) {
					empty = false;
					break;
				}
			}
		} else {
			if (valor instanceof TerminiDto)
				empty = ((TerminiDto)valor).isEmpty();
			else if (valor != null && !"".equals(valor))
				empty = false;
		}
		return empty;
	}

	private static boolean isCampMultiple(TascaDadaDto camp, boolean perFiltre) {
		boolean ambArray = false;
		if (!perFiltre) {
			ambArray = camp.isCampMultiple();
		} else {
			ambArray = 	camp.getCampTipus().equals(CampTipusDto.DATE) ||
						camp.getCampTipus().equals(CampTipusDto.INTEGER) ||
						camp.getCampTipus().equals(CampTipusDto.FLOAT) ||
						camp.getCampTipus().equals(CampTipusDto.PRICE);
		}
		return ambArray;
	}

//	private static Object cloneMultipleArray(
//			String field,
//			Object command,
//			List<TascaDadaDto> tascaDadas,
//			int addTolength) throws Exception {
//		for (TascaDadaDto camp: tascaDadas) {
//			if (camp.getVarCodi().equals(field)) {
//				Object value = PropertyUtils.getSimpleProperty(command, field);
//				if (value != null) {
//					int length = ((Object[])value).length;
//					return Array.newInstance(
//							camp.getJavaClass(),
//							length + addTolength);
//				} else {
//					return Array.newInstance(
//							camp.getJavaClass(),
//							1);
//				}
//			}
//		}
//		return null;
//	}

//	public static Object addMultiple(String field, Object command, List<TascaDadaDto> tascaDadas) throws Exception {
//		Object value = PropertyUtils.getSimpleProperty(command, field);
//		Object newArray = cloneMultipleArray(field, command, tascaDadas, 1);
//		for (int i = 0; i < Array.getLength(newArray) - 1; i++)
//			Array.set(newArray, i, Array.get(value, i));
//		return newArray;
//	}
//	public static Object deleteMultiple(String field, Object command, List<TascaDadaDto> tascaDadas, int index) throws Exception {
//		Object value = PropertyUtils.getSimpleProperty(command, field);
//		Object newArray = cloneMultipleArray(field, command, tascaDadas, -1);
//		int j = 0;
//		for (int i = 0; i < Array.getLength(value); i++)
//			if (i != index)
//				Array.set(newArray, j++, Array.get(value, i));
//		return newArray;
//	}

	private static final Log logger = LogFactory.getLog(TascaFormHelper.class);

}
