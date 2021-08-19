/**
 * 
 */
package es.caib.helium.back.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.logic.intf.dto.CampTipusDto;
import es.caib.helium.logic.intf.dto.ExpedientDadaDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.dto.ValidacioDto;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Mètodes comuns per a la gestió de formularis de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaFormHelper {

	private static final String VARIABLE_SESSIO_COMMAND_TMP = "TascaFormUtil_CommandSessioTmp";


	// VALORS
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
			    				}
		    					valorSenseBuits.add(va);
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
	    				} else if (tascaDada.getCampTipus().equals(CampTipusDto.BOOLEAN) && valor == null) {
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


	// COMMANDS
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// MassivaExpedientController --> validar {inicial: false, validarObligatoris: false, validarExpresions: false, consultaPerTipus: false}
	public static Object getCommandForCampsExpedient(
			List<ExpedientDadaDto> expedientDades,
			Map<String, Object> valors,
			String processInstanceId) throws Exception {
		List<TascaDadaDto> tascaDades = new ArrayList<TascaDadaDto>();
		for (ExpedientDadaDto expedientDada: expedientDades) {
			TascaDadaDto tascaDada = getTascaDadaDtoFromExpedientDadaDto(expedientDada);
			tascaDades.add(tascaDada);
		}
		return getCommandForCamps(
				tascaDades,
				valors,
				null,
				null,
				true,
				false,
				false,
				processInstanceId);
	}

	// - CampTascaController --> NO validar
	// - ExpedientDadaController: populateCommand --> NO validar
	// - ExpedientInicioPasFormController: populateCommand --> NO validar
	// - MassivaExpedientController: populateCommand --> NO validar
	// - ReproController: populateCommand --> NO validar
	// - TascaTramitacioController: pipelles --> NO validar
	// - TascaTramitacioController: inicialitzarTasca --> NO validar
	public static Object getCommandForCamps(
			List<TascaDadaDto> tascaDades,
			Map<String, Object> valors,
			Map<String, Object> campsAddicionals,
			Map<String, Class<?>> campsAddicionalsClasses) throws Exception {
		return getCommandForCamps(
				tascaDades,
				valors,
				campsAddicionals,
				campsAddicionalsClasses,
				false,
				false,
				false,
				null);
	}

	// - ExpedientDadaController: dadaEditar --> validar {inicial: true, validarObligatoris: true, validarExpresions: false, consultaPerTipus: false}
	// - ExpedientDadaController: novaDada --> validar {inicial: true, validarObligatoris: false, validarExpresions: false, consultaPerTipus: false}
	// - ExpedientInicioPasFormController: iniciarFormPost --> validar {inicial: false, validarObligatoris: true, validarExpresions: true, consultaPerTipus: false}
	// - TascaTramitacioController: validar --> validar {inicial: false, validarObligatoris: true, validarExpresions: true, consultaPerTipus: false}
	public static Object getCommandForCamps(
			List<TascaDadaDto> tascaDades,
			Map<String, Object> valors,
			Map<String, Object> campsAddicionals,
			Map<String, Class<?>> campsAddicionalsClasses,
			boolean perValidar,
			boolean validarObligatoris,
			boolean validarExpressions,
			String processInstanceId) throws Exception {
		Map<String, Object> registres = new HashMap<String, Object>();
		// Empram cglib per generar el command de manera dinàmica
		Object command = getCommandModelForCamps(
				tascaDades,
				campsAddicionalsClasses,
				registres,
				false,
				perValidar,
				validarObligatoris,
				validarExpressions,
				processInstanceId);
//				esConsultaPerTipus);
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
					if (isCampMultiple(camp, false)) {
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
								final_valor = Array.newInstance(camp.getJavaClass(), 1);
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
				} else {
					// En al cas de que el camp a emplenar els valor sigui tipus registre, calcularem el seu contingut a valorRegistre:
					Object registre = registres.get(camp.getVarCodi());
					Object valorRegistre = null;
					// 3. Si el valor obtingut és null, llavors crearem un objecte Registre o Registre[1] amb els atributs buits, depenent de si és múltiple
					if (valor == null) {
						if (camp.isCampMultiple()) {
							valorRegistre = Array.newInstance(registre.getClass(), camp.isRequired() ? 1 : 0);
							if (camp.isRequired())
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

	// ExpedientConsultaInformeController --> NO validar
	// ExpedientDadaController --> NO validar
	// ExpedientDadaController --> NO validar
	// TascaTramitacioController --> NO validar
	public static Object getCommandBuitForCamps(
			List<TascaDadaDto> tascaDades,
			Map<String, Object> campsAddicionals,
			Map<String, Class<?>> campsAddicionalsClasses) throws Exception {
		return getCommandBuitForCamps(
				tascaDades,
				campsAddicionals,
				campsAddicionalsClasses,
				null, // Sense valors per defecte
				false);
	}

	// ExpedientInformeController: getFiltreCommand --> NO validar {consultaPerTipus: true}
	// ExpedientInformeController: getFiltreParameterCommand --> NO validar {consultaPerTipus: true}
	public static Object getCommandBuitForCamps(
			List<TascaDadaDto> tascaDades,
			Map<String, Object> campsAddicionals,
			Map<String, Class<?>> campsAddicionalsClasses,
			boolean esConsultaPerTipus) throws Exception {
		return getCommandBuitForCamps(
				tascaDades, 
				campsAddicionals, 
				campsAddicionalsClasses, 
				null, // Sense valors per defecte
				esConsultaPerTipus);
	}

	// TascaTramitacioController: validar --> NO validar {consultaPerTipus: true}
	public static Object getCommandBuitForCamps(
			List<TascaDadaDto> tascaDades,
			Map<String, Object> campsAddicionals,
			Map<String, Class<?>> campsAddicionalsClasses,
			Map<String, String> valorsPerDefecte,
			boolean esConsultaPerTipus) throws Exception {
		Map<String, Object> registres = new HashMap<String, Object>();
		// Empram cglib per generar el command de manera dinàmica
		Object command = getCommandModelForCamps(
				tascaDades,
				campsAddicionalsClasses,
				registres,
				esConsultaPerTipus,
				false,
				false,
				false,
				null);
		// Inicialitza els camps del command amb valors buits o els valors per defecte
		for (TascaDadaDto camp: tascaDades) {
			if (!camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
				Object valor = obtenirValorDefecte(camp, valorsPerDefecte);
				try {
					if (isCampMultiple(camp, esConsultaPerTipus)) {
						if (camp.getCampTipus().equals(CampTipusDto.TERMINI)) {
							String[][] terminis = new String[][]{new String[]{"0","0",""}};
							valor = terminis;
						} else {
							int tamany = esConsultaPerTipus? 2 : 1;
							Object a = Array.newInstance(camp.getJavaClass(), tamany);
							for (int i=0; i<tamany; i++)
								Array.set(a, i, valor);
							valor = a;
						}
					} else {
						if (camp.getCampTipus().equals(CampTipusDto.TERMINI)){
							valor = new String[3];
						}
					}
					setSimpleProperty(
							command,
							camp.getVarCodi(),
							valor);
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
						valorRegistre = Array.newInstance(registre.getClass(), camp.isRequired() ? 1 : 0);
						if (camp.isRequired() && !camp.isReadOnly())
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
							valorRegistre = Array.newInstance(registre.getClass(), camp.isRequired() ? 1 : 0);
							if (camp.isRequired())
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

	/** Comprova que l'objecte command tingui tots els camps del filtre. Si no els retorna fals.*/
	public static boolean commandForCampsValid(
			Object command,
			List<TascaDadaDto> tascaDades) {

		if (command == null)
			return false;

		boolean valid = true;
		// Crea un conjunt de propietats
		Set<String> propietats = new HashSet<String>();
		String name;
		for  (Method method : command.getClass().getDeclaredMethods()) {
			if (method.getReturnType() != void.class) {
				name=method.getName();
				if(name.startsWith("get"))
				{
					name = name.substring(3);
				}else if(name.startsWith("is"))
				{
					name = name.substring(2);
				}
				propietats.add(name.toLowerCase());
			}
		}
		// Comprova que hi siguin totes
		for (TascaDadaDto dada :tascaDades)
			if (!propietats.contains(dada.getVarCodi().toLowerCase())) {
				valid = false;
				break;
			}
		return valid;
	}

	// DADES TASCA
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static TascaDadaDto getTascaDadaDtoFromExpedientDadaDto(
			ExpedientDadaDto expedientDadaDto) {
		TascaDadaDto tascaDto = new TascaDadaDto();
		tascaDto.setVarCodi(expedientDadaDto.getVarCodi());
		tascaDto.setVarValor(expedientDadaDto.getVarValor());
		tascaDto.setCampId(expedientDadaDto.getCampId());
		tascaDto.setCampTipus(expedientDadaDto.getCampTipus());
		tascaDto.setCampEtiqueta(expedientDadaDto.getCampEtiqueta());
		tascaDto.setCampMultiple(expedientDadaDto.isCampMultiple());
		tascaDto.setCampOcult(expedientDadaDto.isCampOcult());
		tascaDto.setCampParams(expedientDadaDto.getCampParams());
		tascaDto.setRequired(expedientDadaDto.isRequired());
		tascaDto.setText(expedientDadaDto.getText());
		tascaDto.setError(expedientDadaDto.getError());
		tascaDto.setObservacions(expedientDadaDto.getObservacions());
		tascaDto.setJbpmAction(expedientDadaDto.getJbpmAction());
		tascaDto.setValidacions(expedientDadaDto.getValidacions());
		if (expedientDadaDto.getMultipleDades() != null) {
			List<TascaDadaDto> multipleDades = new ArrayList<TascaDadaDto>();
			for (ExpedientDadaDto dto: expedientDadaDto.getMultipleDades()) {
				multipleDades.add(getTascaDadaDtoFromExpedientDadaDto(dto));
			}
			tascaDto.setMultipleDades(multipleDades);
		}
		if (expedientDadaDto.getRegistreDades() != null) {
			List<TascaDadaDto> registreDades = new ArrayList<TascaDadaDto>();
			for (ExpedientDadaDto dto: expedientDadaDto.getRegistreDades()) {
				registreDades.add(getTascaDadaDtoFromExpedientDadaDto(dto));
			}
			tascaDto.setRegistreDades(registreDades);
		}
		return tascaDto;
	}





	// PRIVATS
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// TODO: Validació --> Afegir validacions a nivell de tasca!!!

	private static Object getCommandModelForCamps(
			List<TascaDadaDto> tascaDades,
			Map<String, Class<?>> campsAddicionalsClasses,
			Map<String, Object> registres,
			boolean esConsultaPerTipus,
			boolean isPerValidar,
			boolean validarObligatoris,
			boolean validarExpresions,
			String processInstanceId) throws Exception {

		logger.debug("Generant command per tasca");

		// Empram javassist per generar el command de manera dinàmica
		String pkgName = "es.caib.helium.back";

		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.makeClass("TaskCommand_" + UUID.randomUUID());


		if (campsAddicionalsClasses != null) {
			for (String codi: campsAddicionalsClasses.keySet()) {
				addField(
						pool,
						cc,
						codi,
						campsAddicionalsClasses.get(codi).getName());
			}
		}
		if (registres == null) registres = new HashMap<String, Object>();
		for (TascaDadaDto tascaDada: tascaDades) {
			Class<?> propertyClass;
			if (!tascaDada.getCampTipus().equals(CampTipusDto.REGISTRE)) {
				if (tascaDada.getCampTipus() != null) {
					if (isCampMultiple(tascaDada, esConsultaPerTipus)) {
						propertyClass = Array.newInstance(tascaDada.getJavaClass(), 1).getClass();
					} else {
						propertyClass = tascaDada.getJavaClass();
					}
				} else {
					propertyClass = Object.class;
				}
				addField(
						pool,
						cc,
						tascaDada.getVarCodi(),
						propertyClass.getName());
			} else if (!esConsultaPerTipus) {
				// En cas de registres cream un objecte amb els membres del registre com a atributs (l'anomenarem Registre)
				Object registre = getCommandModelForCamps(
						tascaDada.isCampMultiple() ? tascaDada.getMultipleDades().get(0).getRegistreDades() : tascaDada.getRegistreDades(),
						null,
						registres,
						false,
						isPerValidar,
						validarObligatoris,
						validarExpresions,
						processInstanceId);
				if (tascaDada.isCampMultiple()) {
					// En cas de ser un registre múltiple el que cream és un array de Registre
					propertyClass = Array.newInstance(registre.getClass(), 1).getClass();
				} else {
					propertyClass = registre.getClass();
				}
				addField(
						pool,
						cc,
						tascaDada.getVarCodi(),
						propertyClass.getName());
				registres.put(tascaDada.getVarCodi(), registre);
			}
		}

		if (isPerValidar) {
			ObjectMapper objectMapper = new ObjectMapper();
			List<AnnotationData> classAnnotations = new ArrayList<>();
			for (TascaDadaDto camp : tascaDades) {
				List<AnnotationData> fieldAnnotations = new ArrayList<>();

				// Obligatoris
				if (validarObligatoris && (camp.isRequired() || camp.getCampTipus().equals(CampTipusDto.REGISTRE))) {
					if (camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
						List<TascaDadaDto> registreDades = camp.isCampMultiple() ? camp.getMultipleDades().get(0).getRegistreDades() : camp.getRegistreDades();
						if (camp.isRequired() || registreAmbCampsRequired(registreDades)) {
							if (camp.isReadOnly()) {
								fieldAnnotations.add(AnnotationData.builder()
										.annotationName("javax.validation.constraints.NotNull")
										.members(Map.of("message", MessageHelper.getInstance().getMessage("not.blank")))
										.build());
							}
						} else {
							List<ValidationHelper.CampRegistreRequired> campsRequired = registreDades
									.stream()
									.map(r -> ValidationHelper.CampRegistreRequired.builder()
											.varCodi(r.getVarCodi())
											.required(r.isRequired())
											.multiple(r.isCampMultiple())
											.build()
									).collect(Collectors.toList());
							String strCcampsRequired = objectMapper.writeValueAsString(campsRequired);
							fieldAnnotations.add(AnnotationData.builder()
									.annotationName("es.caib.helium.back.validator.SpELAssert")
									.members(Map.of(
											"value", "#registreNotEmpty(#this, " + strCcampsRequired + ", " + camp.isCampMultiple() + ", " + camp.isRequired() + ")",
											"message", MessageHelper.getInstance().getMessage("registre.not.blank")))
									.build());
						}
					// Camp simple - NO Registre
					} else if (!camp.isCampMultiple()) {
						if (camp.getCampTipus().equals(CampTipusDto.TERMINI)) {
							fieldAnnotations.add(AnnotationData.builder()
									.annotationName("es.caib.helium.back.validator.SpELAssert")
									.members(Map.of(
											"value", "#terminiNotEmpty(#this)",
											"message", MessageHelper.getInstance().getMessage("not.blank")))
									.build());
						} else {
							fieldAnnotations.add(AnnotationData.builder()
									.annotationName("javax.validation.constraints.NotEmpty")
									.members(Map.of("message", MessageHelper.getInstance().getMessage("not.blank")))
									.build());
						}
					// Camp Múltiple - NO Registre
					} else {
						if (camp.getCampTipus().equals(CampTipusDto.TERMINI)) {
							fieldAnnotations.add(AnnotationData.builder()
									.annotationName("es.caib.helium.back.validator.SpELAssert")
									.members(Map.of(
											"value", "#listTerminisNotEmpty(#this)",
											"message", MessageHelper.getInstance().getMessage("members.not.blank")))
									.build());
						} else {
							fieldAnnotations.add(AnnotationData.builder()
									.annotationName("es.caib.helium.back.validator.SpELAssert")
									.members(Map.of(
											"value", "#listMembersNotEmpty(#this)",
											"message", MessageHelper.getInstance().getMessage("members.not.blank")))
									.build());
						}
					}
				}

				// comprovaCamp
				if (camp.getCampTipus().equals(CampTipusDto.STRING)) {
					if (camp.isCampMultiple()) {
						fieldAnnotations.add(AnnotationData.builder()
								.annotationName("es.caib.helium.back.validator.SpELAssert")
								.members(Map.of(
										"value", "#listStringLengthValid(#this)",
										"message", MessageHelper.getInstance().getMessage("members.max.length")))
								.build());
					} else {
						fieldAnnotations.add(AnnotationData.builder()
								.annotationName("es.caib.helium.back.validator.SpELAssert")
								.members(Map.of(
										"value", "#stringLengthValid(#this)",
										"message", MessageHelper.getInstance().getMessage("max.length")))
								.build());
					}
				} else if (camp.getCampTipus().equals(CampTipusDto.DATE) && camp.getText() != null && !camp.getText().isEmpty()) {
					fieldAnnotations.add(AnnotationData.builder()
							.annotationName("es.caib.helium.back.validator.SpELAssert")
							.members(Map.of(
									"value", "#dataValida(#this)",
									"message", MessageHelper.getInstance().getMessage("error.camp.dada.valida")))
							.build());
				}

				if (!fieldAnnotations.isEmpty()) {
					addFieldAnnotations(
							cc,
							camp.getVarCodi(),
							fieldAnnotations);
				}

				// expressions
				if (validarExpresions) {
					var validacions = camp.getValidacions();
					for (ValidacioDto validacio: validacions) {
						if (processInstanceId != null || validExpression(validacio.getExpressio())) {
							String expression = transformExpression(processInstanceId, validacio.getExpressio());
							classAnnotations.add(AnnotationData.builder()
									.annotationName("es.caib.helium.back.validator.SpELAssert")
									.members(Map.of(
											"value", expression,
											"message", camp.getCampEtiqueta() + ": " + validacio.getMissatge()))
									.build());
						}
					}
				}
			}

			if (!classAnnotations.isEmpty()) {
				addClassAnnotations(
						cc,
						classAnnotations
				);
			}
		}
		var taskCommand = cc.toClass().getConstructor().newInstance();
		cc.detach();

		return taskCommand;
	}

	private static boolean validExpression(String expression) {
		return !expression.contains("#valor(");
	}

	private static String transformExpression(String processInstanceId, String expression) {
		expression = expression.replace("#valor(", "@expedientDadaService.findOnePerInstanciaProces(null, " + processInstanceId + ", ");
		return expression;
	}

	private static boolean registreAmbCampsRequired(List<TascaDadaDto> registreDades) {
		for (TascaDadaDto campRegistre : registreDades) {
			if (campRegistre.isRequired()) {
				return true;
			}
		}
		return false;
	}

	private static Object getArrayFromRegistre(
			TascaDadaDto camp,
			Object valor,
			boolean esIniciExpedient) throws Exception {
		try {
			if (camp.isCampMultiple()) {
				if (camp.isReadOnly() ) {
					valor = camp.getVarValor();
				}
				int midaLinia = camp.getMultipleDades().get(0).getRegistreDades().size();
				// Validar que és correcte el següent:
				//int mida = camp.isReadOnly() ? camp.getMultipleDades().size() : ((Object[])valor).length;
				int mida = valor != null ? ((Object[])valor).length : 0;
				
				Object[][] linies = new Object[mida][midaLinia];
				boolean varIncloure = false;
				if (mida > 0) {
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
				} else {
					varIncloure = !esIniciExpedient;
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
			logger.error(e);
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

//	private static void addPropertyToBean(
//			BeanGenerator bg,
//			String propietatNom,
//			Class<?> propietatClass) {
//		logger.debug("Afegint propietat al command(" +
//				"nom=" + propietatNom + ", " +
//				"class=" + propietatClass.getName() + ")");
//		bg.addProperty(
//				propietatNom,
//				propietatClass);
//	}

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
		String dies = termini[2] == null ? "0" : termini[2];
		
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
	
	/** Obté un objecte a partir del valor per defecte passat per String. */
	private static Object obtenirValorDefecte(
			TascaDadaDto camp, 
			Map<String, String> valorsPerDefecte) {
		Object valor = null;
		if (camp != null 
				&& valorsPerDefecte != null 
				&& valorsPerDefecte.containsKey(camp.getVarCodi())) {
			try {
				CampTipusDto tipus = camp.getCampTipus();
				String str = valorsPerDefecte.get(camp.getVarCodi());
				if (tipus != null && str != null && !"".equals(str.trim())) {
					switch(tipus) {
						case BOOLEAN:
							if ("S".equals(str)) {
								valor = new Boolean(true);
							} else if ("N".equals(str)) {
								valor = new Boolean(false);
							} else {
								valor = Boolean.parseBoolean(str);
							}
							break;
						case DATE:
							valor = new SimpleDateFormat("dd/MM/yyyy").parse(str);
							break;
						case PRICE:
							valor = new BigDecimal(str);
							break;
						case FLOAT:
							valor = Double.parseDouble(str);
							break;
						case INTEGER:
							valor = Long.parseLong(str);
							break;
						case STRING:
						case SELECCIO:
						case SUGGEST:
						case TEXTAREA:
							valor = str;
							break;
						default:
							break;					
					}
				}			
			} catch (Exception e) {
				logger.error("Error establint el paràmetre predefinit \"" + camp.getVarCodi() + "\" de tipus \"" + camp.getCampTipus() + "\" amb el valor \"" + valorsPerDefecte.get(camp.getVarCodi()) + "\" pel camp amb id=" + camp.getCampId() );
			}
		}
		return valor;
	}

	private static final Log logger = LogFactory.getLog(TascaFormHelper.class);













	private static void addField(
			ClassPool pool,
			CtClass cc,
			String name,
			String className) throws Exception {

		CtField field = new CtField(pool.get(className),name, cc);
		field.setModifiers(Modifier.PRIVATE);
		cc.addField(field);
		String capitalizedName = capitalize(name);
		cc.addMethod(CtNewMethod.setter("set" + capitalizedName, field));
		cc.addMethod(CtNewMethod.getter("get" + capitalizedName, field));
	}

	private static void addFieldAnnotations(
			CtClass cc,
			String fieldName,
			List<AnnotationData> annotationData) throws Exception {

		Assertions.assertNotNull(annotationData, "No s'ha indicat cap anotació");
		Assertions.assertFalse(annotationData.isEmpty(), "No s'ha indicat cap anotació");

		CtField field = cc.getField(fieldName);
		ConstPool cpool = cc.getClassFile().getConstPool();

		AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);

		if (annotationData.size() > 1) {
			Annotation rootAnnotation = new Annotation("es.caib.helium.back.validator.SpELAsserts", cpool);
			var childAnnotationsMemberList = new ArrayMemberValue(cpool);
			var childAnnotationMemberValues = new ArrayList<AnnotationMemberValue>();

			annotationData.forEach(a ->
					childAnnotationMemberValues.add(new AnnotationMemberValue(createAnnotation(cpool, a), cpool))
			);
			childAnnotationsMemberList.setValue(childAnnotationMemberValues.toArray(AnnotationMemberValue[]::new));
			rootAnnotation.addMemberValue("value", childAnnotationsMemberList);
			attr.addAnnotation(rootAnnotation);
		} else {
			AnnotationData data = annotationData.get(0);
			Annotation annotation = createAnnotation(cpool, data);
			attr.addAnnotation(annotation);
		}
		field.getFieldInfo().addAttribute(attr);
	}

	private static void addClassAnnotations(
			CtClass cc,
			List<AnnotationData> annotationData) throws Exception {

		Assertions.assertNotNull(annotationData, "No s'ha indicat cap anotació");
		Assertions.assertFalse(annotationData.isEmpty(), "No s'ha indicat cap anotació");

		ConstPool cpool = cc.getClassFile().getConstPool();

		AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);

		if (annotationData.size() > 1) {
			Annotation rootAnnotation = new Annotation("es.caib.helium.back.validator.SpELAsserts", cpool);
			var childAnnotationsMemberList = new ArrayMemberValue(cpool);
			var childAnnotationMemberValues = new ArrayList<AnnotationMemberValue>();

			annotationData.forEach(a ->
					childAnnotationMemberValues.add(new AnnotationMemberValue(createAnnotation(cpool, a), cpool))
			);
			childAnnotationsMemberList.setValue(childAnnotationMemberValues.toArray(AnnotationMemberValue[]::new));
			rootAnnotation.addMemberValue("value", childAnnotationsMemberList);
			attr.addAnnotation(rootAnnotation);
		} else {
			AnnotationData data = annotationData.get(0);
			Annotation annotation = createAnnotation(cpool, data);
			attr.addAnnotation(annotation);
		}
		cc.getClassFile().addAttribute(attr);
	}

	private static Annotation createAnnotation(ConstPool cpool, AnnotationData data) {
		var annotation = new Annotation(data.getAnnotationName(), cpool);
		if (data.getMembers() != null) {
			data.getMembers().entrySet().forEach(e -> annotation.addMemberValue(e.getKey(), new StringMemberValue(e.getValue(), cpool)));
		}
		annotation.addMemberValue("helpers", new ClassMemberValue(ValidationHelper.class.getName(), cpool));
		return annotation;
	}

	private static String capitalize(String nom) {
		String firstLetStr = nom.substring(0, 1).toUpperCase();
		String remLetStr = nom.substring(1);
		return firstLetStr + remLetStr;
	}

}
