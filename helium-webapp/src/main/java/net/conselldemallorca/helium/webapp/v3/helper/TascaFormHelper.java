/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
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
			List<TascaDadaDto> tascaDades,
			Object command,
			boolean esConsultaPerTipus) {
		return getValorsFromCommand(
				tascaDades,
				command,
				esConsultaPerTipus,
				false);
	}

	public static Map<String, Object> getValorsFromCommand(
			List<TascaDadaDto> tascaDades,
			Object command,
			boolean esConsultaPerTipus,
			boolean esIniciExpedient) {
    	Map<String, Object> resposta = new HashMap<String, Object>();
    	for (TascaDadaDto tascaDada: tascaDades) {
    		try {
    			if (!tascaDada.getCampTipus().equals(CampTipusDto.ACCIO)) {
    				Object valor = PropertyUtils.getSimpleProperty(
			    				command,
			    				tascaDada.getVarCodi());
	    			if (tascaDada.isReadOnly() && !tascaDada.getCampTipus().equals(CampTipusDto.REGISTRE)) {
	    				valor = tascaDada.getVarValor();
	    				setSimpleProperty(
								command,
								tascaDada.getVarCodi(),
								valor);
	    			} else if (tascaDada.getCampTipus().equals(CampTipusDto.REGISTRE)) {
    					valor = getArrayFromRegistre(tascaDada, valor, esIniciExpedient);
    				}
		    		if (!esConsultaPerTipus && tascaDada.isCampMultiple()) {
	    				// Lleva els valors buits de l'array
		    			List<Object> valorSenseBuits = new ArrayList<Object>();
		    			if (valor != null)
			    			for (int i = 0; i < Array.getLength(valor); i++) {
			    				Object va = Array.get(valor, i);
			    				if (tascaDada.getCampTipus().equals(CampTipusDto.BOOLEAN) && va == null) {
					    			va = Boolean.FALSE;
					    		}
			    				if (!empty(va)) {
			    					if (tascaDada.getCampTipus().equals(CampTipusDto.TERMINI)) {
			    						String[] pre_va = (String[])va; 
				    					if((pre_va).length < 3)
				    						va = null;
				    					else
				    						va = obtenirValorTermini(pre_va);
				    				}
			    					valorSenseBuits.add(va);
			    				}
			    			}
		    			Object newArray = null;
		    			if (!valorSenseBuits.isEmpty()) {
	    					newArray = Array.newInstance(
	    							tascaDada.getCampTipus().equals(CampTipusDto.TERMINI) ? String.class : tascaDada.getJavaClass(),
	    							valorSenseBuits.size());
		    				int index = 0;
		    				for (Object val: valorSenseBuits) {
		    					Array.set(newArray, index++, val);
		    				}
		    			}
		    			if (!tascaDada.getCampTipus().equals(CampTipusDto.REGISTRE) || valor != null) {
		    				resposta.put(
		    						tascaDada.getVarCodi(),
		    						newArray);
		    			}
		    		} else {
		    			if (tascaDada.getCampTipus().equals(CampTipusDto.TERMINI) && valor != null) {
		    				String[] pre_valor = (String[])valor; 
	    					if((pre_valor).length < 3)
	    						valor = null;
	    					else
	    						valor = obtenirValorTermini(pre_valor);
	    				}
		    			if (tascaDada.getCampTipus().equals(CampTipusDto.BOOLEAN) && valor == null) {
			    			valor = Boolean.FALSE;
			    		}
//		    			valor = compatibilitat26(camp, valor);
		    			if (!tascaDada.getCampTipus().equals(CampTipusDto.REGISTRE) || valor != null) {
		    				resposta.put(
			    					tascaDada.getVarCodi(),
				    				valor);
		    			}
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
			List<TascaDadaDto> tascaDades,
			HttpServletRequest request) {
		return getCommandForCamps(
				tascaDades,
				null,
				null,
				null,
				false);
	}
	
	public static Object getCommandForCampsExpedient(
			List<ExpedientDadaDto> expedientDades,
			Map<String, Object> valors) {
		List<TascaDadaDto> tascaDades = new ArrayList<TascaDadaDto>();
		for (ExpedientDadaDto expedientDada: expedientDades) {
			TascaDadaDto tascaDada = toTascaDadaDto(expedientDada);
			tascaDades.add(tascaDada);
		}
		return getCommandForCamps(
				tascaDades,
				valors,
				null,
				null,
				false);
	}
	
	public static TascaDadaDto toTascaDadaDto(ExpedientDadaDto expdada) {
		TascaDadaDto tascaDada = new TascaDadaDto();
		tascaDada.setCampTipus(expdada.getCampTipus());
		tascaDada.setCampMultiple(expdada.isCampMultiple());
		tascaDada.setVarCodi(expdada.getVarCodi());
		tascaDada.setCampId(expdada.getCampId());
		tascaDada.setText(expdada.getText());
		tascaDada.setCampEtiqueta(expdada.getCampEtiqueta());
		tascaDada.setRequired(expdada.isRequired());
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
			List<TascaDadaDto> tascaDades,
			Map<String, Object> valors,
			Map<String, Object> campsAddicionals,
			Map<String, Class<?>> campsAddicionalsClasses,
			boolean esConsultaPerTipus) {
		Map<String, Object> registres = new HashMap<String, Object>();
		// Empram cglib per generar el command de manera dinàmica
		Object command = getCommandModelForCamps(
				tascaDades,
				campsAddicionalsClasses,
				registres,
				esConsultaPerTipus);
		// Inicialitza els camps del command amb els valors de la tasca
		for (TascaDadaDto camp: tascaDades) {
			Object valor = null;
			try {
				// Obtenim el valor del camp
				if (valors != null && valors.get(camp.getVarCodi()) != null) {
					valor = valors.get(camp.getVarCodi());
				} else {
					valor = camp.getVarValor();
				}
				if (!camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
					// Camps múltiples
					if (isCampMultiple(camp, esConsultaPerTipus)) {
						Object valorMultiple = null;
						if (valor != null) {
							if (camp.getCampTipus().equals(CampTipusDto.TERMINI)) {
								int mida = 1;
								if (valor instanceof Object[]) {
									mida = ((Object[])valor).length;
									String[][] terminis = new String[mida][3];
									int i = 0;
									for (String term: (String[])valor) {
										terminis[i++] = crearTermini(term);
									}
									valorMultiple = terminis;
								} else {
									String[] prevalor = ((String)valor).split("/");
									valorMultiple = new String[][] {prevalor};
								}
							} else if (!(valor instanceof Object[])) {
								valorMultiple = Array.newInstance(camp.getJavaClass(), 1);
								((Object[])valorMultiple)[0] = valor;
							} else {
								valorMultiple = valor;
							}
							setSimpleProperty(
									command,
									camp.getVarCodi(),
									valorMultiple);
						} else {
							Object final_valor;
							if (camp.getCampTipus().equals(CampTipusDto.TERMINI)) {
								String[][] terminis = new String[][]{new String[]{"0","0",""}};
								final_valor = terminis;
							} else {
								final_valor = Array.newInstance(camp.getJavaClass(), esConsultaPerTipus ? 2 : 1);
							}
							setSimpleProperty(
									command,
									camp.getVarCodi(),
									final_valor);
						}
					// Camps senzills
					} else {
						if (camp.getCampTipus().equals(CampTipusDto.TERMINI)){
							if (valor != null) {
								valor = crearTermini(valor);
							} else {
								valor = new String[3];
							}
						} else if (camp.getCampTipus().equals(CampTipusDto.STRING) && valor == null) {
							valor = "";
						}
						setSimpleProperty(
								command,
								camp.getVarCodi(),
								valor);
					}
				// REGISTRES:
				} else if (!esConsultaPerTipus) {
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
					setSimpleProperty(
							command, 
							camp.getVarCodi(),
							valorRegistre);
				}
			} catch (Exception ex) {
				logger.error("No s'ha pogut afegir el camp al command (" +
						"campCodi=" + camp.getVarCodi() + ", " +
						"valor=" + varValorToString(valor) + ", " +
						"class=" + varValorClassToString(valor) + ")", ex);
			}
		}
		if (campsAddicionals != null) {
			for (String codi: campsAddicionals.keySet()) {
				Object valor = campsAddicionals.containsKey(codi) ? campsAddicionals.get(codi) : null;
				try {
					setSimpleProperty(
							command,
							codi,
							valor);
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp addicional al command (" +
							"campCodi=" + codi + ", " +
							"valor=" + varValorToString(valor) + ", " +
							"class=" + varValorClassToString(valor) + ")", ex);
				}
			}
		}
		
		return command;
	}
	
	public static Object getCommandBuitForCamps(
			List<TascaDadaDto> tascaDades,
			Map<String, Object> campsAddicionals,
			Map<String, Class<?>> campsAddicionalsClasses,
			boolean esConsultaPerTipus) {
		Map<String, Object> registres = new HashMap<String, Object>();
		// Empram cglib per generar el command de manera dinàmica
		Object command = getCommandModelForCamps(
				tascaDades,
				campsAddicionalsClasses,
				registres,
				esConsultaPerTipus);
		// Inicialitza els camps del command amb valors buits
		for (TascaDadaDto camp: tascaDades) {
			if (!camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
				Object valor = null;
				try {
					if (isCampMultiple(camp, esConsultaPerTipus)) {
						if (camp.getCampTipus().equals(CampTipusDto.TERMINI)) {
							String[][] terminis = new String[][]{new String[]{"0","0",""}};
							valor = terminis;
						} else {
							valor = Array.newInstance(camp.getJavaClass(), esConsultaPerTipus ? 2 : 1);
						}
						setSimpleProperty(
								command,
								camp.getVarCodi(),
								valor);
					} else {
						if (camp.getCampTipus().equals(CampTipusDto.TERMINI)){
							valor = new String[3];
						}
						setSimpleProperty(
								command,
								camp.getVarCodi(),
								valor);
					}
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp al command (" +
							"campCodi=" + camp.getVarCodi() + ", " +
							"valor=" + varValorToString(valor) + ", " +
							"class=" + varValorClassToString(valor) + ")", ex);
				}
			} else if (!esConsultaPerTipus) {
				Object valorRegistre = null;
				try {
					// En al cas de que el camp a emplenar els valor sigui tipus registre, calcularem el seu contingut a valorRegistre:
					Object registre = registres.get(camp.getVarCodi());
					if (camp.isCampMultiple()) {
						valorRegistre = Array.newInstance(registre.getClass(), 1);
						((Object[])valorRegistre)[0] = registre;
					} else {
						valorRegistre = registre;
					}
					setSimpleProperty(
							command, 
							camp.getVarCodi(),
							valorRegistre);
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp tipus registre al command (" +
							"campCodi=" + camp.getVarCodi() + ", " +
							"valor=" + varValorToString(valorRegistre) + ", " +
							"class=" + varValorClassToString(valorRegistre) + ")", ex);
				}
			}
		}
		if (campsAddicionals != null) {
			for (String codi: campsAddicionals.keySet()) {
				Object valor = campsAddicionals.containsKey(codi) ? campsAddicionals.get(codi) : null;
				try {
					setSimpleProperty(
							command,
							codi,
							valor);
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp addicional al command (" +
							"campCodi=" + codi + ", " +
							"valor=" + varValorToString(valor) + ", " +
							"class=" + varValorClassToString(valor) + ")", ex);
				}
			}
		}
		return command;
	}
	public static void ompleMultiplesBuits(
			Object command,
			List<TascaDadaDto> tascaDadas,
			boolean esConsultaPerTipus) {
		Map<String, Object> registres = new HashMap<String, Object>();
		// Inicialitza els camps del command amb valors buits
		for (TascaDadaDto camp: tascaDadas) {
			if (!camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
				Object valor = null;
				try {
					if (isCampMultiple(camp, esConsultaPerTipus)) {
						valor = PropertyUtils.getSimpleProperty(
								command,
								camp.getVarCodi());
						if (valor == null)
							setSimpleProperty(
									command,
									camp.getVarCodi(),
									Array.newInstance(camp.getJavaClass(), esConsultaPerTipus ? 2 : 1));
					}
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp al command (" +
							"campCodi=" + camp.getVarCodi() + ", " +
							"valor=" + varValorToString(valor) + ", " +
							"class=" + varValorClassToString(valor) + ")", ex);
				}
			} else if (!esConsultaPerTipus) {
				Object valorRegistre = null;
				try {
					// En al cas de que el camp a emplenar els valor sigui tipus registre, calcularem el seu contingut a valorRegistre:
					Object registre = registres.get(camp.getVarCodi());
					if (camp.isCampMultiple()) {
						Object valor = PropertyUtils.getSimpleProperty(
								command,
								camp.getVarCodi());
						if (valor == null) {
							valorRegistre = Array.newInstance(registre.getClass(), 1);
							((Object[])valorRegistre)[0] = registre;
							setSimpleProperty(
									command, 
									camp.getVarCodi(),
									valorRegistre);
						}
					}
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp de tipus registre al command (" +
							"campCodi=" + camp.getVarCodi() + ", " +
							"valor=" + varValorToString(valorRegistre) + ", " +
							"class=" + varValorClassToString(valorRegistre) + ")", ex);
				}
			}
		}
	}

	public static String varValorToString(Object valor) {
		String valorComString = null;
		if (valor == null) return null;
		if (valor instanceof Object[]) {
			Object[] array = (Object[])valor;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < array.length; i++) {
				if (array[i] != null) {
					if (array[i] instanceof Object[]) {
						Object[] a = (Object[])array[i];
						sb.append("[");
						for (int j = 0; j < a.length; j++) {
							if (a[j] != null)
								sb.append(a[j].toString());
							else
								sb.append("null");
							if (j < a.length - 1)
								sb.append(", ");
						}
						sb.append("]");
					} else {
						sb.append(array[i].toString());
					}
				} else {
					sb.append("null");
				}
				if (i < array.length - 1)
					sb.append(", ");
			}
			sb.append("]");
			valorComString = sb.toString();
		} else if (valor != null) {
			valorComString = valor.toString();
		}
		return valorComString.toString();
	}

	public static String varValorClassToString(Object valor) {
		String valorComString = null;
		if (valor == null) return null;
		if (valor instanceof Object[]) {
			Object[] array = (Object[])valor;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < array.length; i++) {
				if (array[i] != null) {
					if (array[i] instanceof Object[]) {
						Object[] a = (Object[])array[i];
						sb.append("[");
						for (int j = 0; j < a.length; j++) {
							if (a[j] != null)
								sb.append(a[j].getClass().getName());
							else
								sb.append("null");
							if (j < a.length - 1)
								sb.append(", ");
						}
						sb.append("]");
					} else {
						sb.append(array[i].getClass().getName());
					}
				} else {
					sb.append("null");
				}
				if (i < array.length - 1)
					sb.append(", ");
			}
			sb.append("]");
			valorComString = sb.toString();
		} else if (valor != null) {
			valorComString = valor.getClass().toString();
		}
		return valorComString.toString();
	}



	private static Object getCommandModelForCamps(
			List<TascaDadaDto> tascaDades,
			Map<String, Class<?>> campsAddicionalsClasses,
			Map<String, Object> registres,
			boolean esConsultaPerTipus) {
		logger.debug("Generant command per tasca");
		// Empram cglib per generar el command de manera dinàmica
		BeanGenerator bg = new BeanGenerator();
		if (campsAddicionalsClasses != null) {
			for (String codi: campsAddicionalsClasses.keySet()) {
				addpropertyToBean(
						bg,
						codi,
						campsAddicionalsClasses.get(codi));
			}
		}
		if (registres == null) registres = new HashMap<String, Object>();
		for (TascaDadaDto tascaDada: tascaDades) {
			Class<?> propertyClass;
			if (!tascaDada.getCampTipus().equals(CampTipusDto.REGISTRE)) {
				if (tascaDada.getCampTipus() != null)  {
					if (isCampMultiple(tascaDada, esConsultaPerTipus)) {
						propertyClass = Array.newInstance(tascaDada.getJavaClass(), 1).getClass();
					} else {
						propertyClass = tascaDada.getJavaClass();
					}
				} else {
					propertyClass = Object.class;
				}
				addpropertyToBean(
						bg,
						tascaDada.getVarCodi(),
						propertyClass);
			} else if (!esConsultaPerTipus) {
				// En cas de registres cream un objecte amb els membres del registre com a atributs (l'anomenarem Registre)
				Object registre = getCommandModelForCamps(
						tascaDada.isCampMultiple() ? tascaDada.getMultipleDades().get(0).getRegistreDades(): tascaDada.getRegistreDades(),
						null,
						registres,
						false);
				if (tascaDada.isCampMultiple()) {
					// En cas de ser un registre múltiple el que cream és un array de Registre
					propertyClass = Array.newInstance(registre.getClass(), 1).getClass();
				} else {
					propertyClass = registre.getClass();
				}
				addpropertyToBean(
						bg,
						tascaDada.getVarCodi(),
						propertyClass);
				registres.put(tascaDada.getVarCodi(), registre);
			}
		}
		return bg.create();
	}

	private static Object getArrayFromRegistre(
			TascaDadaDto camp,
			Object valor,
			boolean esIniciExpedient) throws Exception {
		try {
			if (camp.isCampMultiple()) {
				int midaLinia = camp.getMultipleDades().get(0).getRegistreDades().size();
				int mida = camp.isReadOnly() ? camp.getMultipleDades().size() : ((Object[])valor).length;
				Object[][] linies = new Object[mida][midaLinia];
				boolean varIncloure = false;				
				for (int l = 0; l < mida; l++) {
					Object registre = camp.isReadOnly() ? null : ((Object[])valor)[l];
					int i = 0;
					for (TascaDadaDto campRegistre : camp.getMultipleDades().get(0).getRegistreDades()) {
						Object oValor = null;
						if (camp.isReadOnly()) {
							oValor = (camp.getMultipleDades().get(l).getRegistreDades().get(i)).getVarValor();
						} else {
							oValor = PropertyUtils.getProperty(registre, campRegistre.getVarCodi());
						}
						if (oValor instanceof String[])
							if (((String[])oValor).length < 3) {
								oValor = null;
							} else {
								String[] pre_oValor = (String[])oValor; 
								oValor = obtenirValorTermini(pre_oValor);
							}
						if (!esIniciExpedient || (oValor != null && !(oValor instanceof Boolean && !(Boolean) oValor)))
							varIncloure = true;
						oValor = compatibilitat26(campRegistre, oValor);
						linies[l][i++] = oValor;
					}
				}
				return varIncloure ? linies : null;
			} else {
				int midaLinia = camp.getRegistreDades().size();
				Object[] linia = new Object[midaLinia];
				int i = 0;
				boolean varIncloure = false;
				for (TascaDadaDto campRegistre : camp.getRegistreDades()) {
					Object oValor = PropertyUtils.getProperty(
							valor,
							campRegistre.getVarCodi());
					if (camp.isReadOnly()) {
						oValor = campRegistre.getVarValor();
					}
					if (oValor instanceof String[]) {
						if (((String[])oValor).length < 3) {
							oValor = null;
						} else {
							String[] pre_oValor = (String[])oValor; 
							oValor = obtenirValorTermini(pre_oValor);
						}
					}
					if (!esIniciExpedient || (oValor != null && !(oValor instanceof Boolean && !(Boolean) oValor)))
						varIncloure = true;
					oValor = compatibilitat26(campRegistre, oValor);
					linia[i++] = oValor;
				}
				return varIncloure ? linia : null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static boolean empty(Object valor) {
		boolean empty = true;
		if (valor instanceof Object[]) {
			for (Object membre: (Object[])valor) {
				if (!empty(membre)) {
					empty = false;
					break;
				}
			}
		} else {
			if (valor instanceof String[])
				empty = ((String[])valor).length < 3;
			else if (valor != null && !"".equals(valor))
				empty = false;
		}
		return empty;
	}

	private static boolean isCampMultiple(
			TascaDadaDto camp,
			boolean esConsultaPerTipus) {
		boolean ambArray = false;
		if (!esConsultaPerTipus) {
			ambArray = camp.isCampMultiple();
		} else {
			ambArray = 	camp.getCampTipus().equals(CampTipusDto.DATE) ||
						camp.getCampTipus().equals(CampTipusDto.INTEGER) ||
						camp.getCampTipus().equals(CampTipusDto.FLOAT) ||
						camp.getCampTipus().equals(CampTipusDto.PRICE);
		}
		return ambArray;
	}

	private static void addpropertyToBean(
			BeanGenerator bg,
			String propietatNom,
			Class<?> propietatClass) {
		logger.debug("Afegint propietat al command(" +
				"nom=" + propietatNom + ", " + 
				"class=" + propietatClass.getName() + ")");
		bg.addProperty(
				propietatNom,
				propietatClass);
	}

	private static void setSimpleProperty(
			Object command,
			String propietatNom,
			Object propietatValor) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Posant valor a propietat del command(" +
				"nom=" + propietatNom + ", " + 
				"valor=" + varValorToString(propietatValor) + ")");
		PropertyUtils.setSimpleProperty(
				command,
				propietatNom,
				propietatValor);
	}

	private static Object compatibilitat26(
			TascaDadaDto camp,
			Object valor) {
		// Per compatibilitat amb la forma de guardar els formularis v2.6
		if (	camp.getCampTipus().equals(CampTipusDto.STRING) ||
				camp.getCampTipus().equals(CampTipusDto.TEXTAREA) ||
				camp.getCampTipus().equals(CampTipusDto.SELECCIO) ||
				camp.getCampTipus().equals(CampTipusDto.SUGGEST)) {
			if (valor == null)
				return "";
		}
		return valor;
	}

	private static String obtenirValorTermini(String[] termini) {
		String anys = termini[0] == null ? "0" : termini[0];
		String mesos = termini[1] == null ? "0" : termini[1];
		String dies = termini[2] == null ? "" : termini[2];
		
		return anys + "/" + mesos + "/" + dies;
	}
	
	private static String[] crearTermini(Object valor) {
		String[] prevalor = ((String)valor).split("/");
		String anys = prevalor.length > 0 ? prevalor[0] : "0";
		String mesos = prevalor.length > 1 ? prevalor[1] : "0";
		String dies = prevalor.length > 2 ? prevalor[2] : "";
		
		String[] termini = new String[]{anys, mesos, dies}; 
		
		return termini;
	}

	private static final Log logger = LogFactory.getLog(TascaFormHelper.class);

}
