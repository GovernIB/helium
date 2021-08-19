/**
 * 
 */
package es.caib.helium.back.helper;

import es.caib.helium.logic.intf.dto.CampTipusDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.TascaService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import javax.annotation.Resource;
import javax.servlet.jsp.jstl.core.LoopTagStatus;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Validador per als formularis de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaFormValidatorHelper {
	public static final int STRING_MAX_LENGTH = 2048;
	@Resource
	private TascaService tascaService;
	@Resource
	private ExpedientService expedientService;

	@Getter @Setter
	private List<TascaDadaDto> tascaDades;
	@Getter @Setter
	private boolean inicial;
	@Getter @Setter
	private boolean validarObligatoris;
	@Getter @Setter
	private boolean validarExpresions;



	public TascaFormValidatorHelper(
			TascaService tascaService,
			List<TascaDadaDto> tascaDades) {
		this.tascaService = tascaService;
		this.tascaDades = tascaDades;
		this.inicial = false;
		// Si validam els obligatoris també validarem les expresions
		this.validarObligatoris = false;
		this.validarExpresions = validarObligatoris;
	}

	public TascaFormValidatorHelper(
			ExpedientService expedientService,
			List<TascaDadaDto> tascaDades) {
		this.expedientService = expedientService;
		this.tascaDades = tascaDades;
		this.inicial = true;
		this.validarObligatoris = false;
		this.validarExpresions = true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(Object.class);
	}

//	public void validate(Object command, Errors errors) {
//		try {
//			List<TascaDadaDto> tascaDades = getTascaDades(command);
//			for (TascaDadaDto camp : tascaDades) {
//				if (validarObligatoris && (camp.isRequired() || camp.getCampTipus().equals(CampTipusDto.REGISTRE))) {
//					if (camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
//						Object valorRegistre = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
//						if ((valorRegistre == null && camp.isRequired()) || registreInvalid(camp, valorRegistre, errors)) {
//							if (camp.isReadOnly())
//								errors.rejectValue(camp.getVarCodi(), "not.blank");
//						} else {
//							Object[] registres = null;
//							List<TascaDadaDto> registreDades = null;
//							if (valorRegistre != null) {
//								if (camp.isCampMultiple()) {
//									registreDades = camp.getMultipleDades().get(0).getRegistreDades();
//									registres = (Object[])valorRegistre;
//								} else {
//									registreDades = camp.getRegistreDades();
//									registres = new Object[] {valorRegistre};
//								}
//								int i = 0;
//								for (Object reg: registres) {
//									boolean emptyReg = true;
//									for (TascaDadaDto campRegistre : registreDades) {
//										if (campRegistre.isRequired()) {
//											boolean emptyVal = true;
//											Object oValor = PropertyUtils.getProperty(reg, campRegistre.getVarCodi());
//											if (oValor != null) {
//												if (oValor instanceof String[]) {
//													String[] oValor_arr = (String[])oValor;
//													emptyVal = (oValor_arr.length < 3 || (oValor_arr[0].equalsIgnoreCase("0") && oValor_arr[1].equalsIgnoreCase("0") && (oValor_arr[2].equalsIgnoreCase("0") || oValor_arr[2] == null)));
//												} else if (oValor instanceof String && "".equals(oValor)) {
//													emptyVal = true;
//												} else {
//													emptyVal = false;
//												}
//											}
//											if (emptyVal) {
//												if (campRegistre.isRequired()) {
//													errors.rejectValue(camp.getVarCodi() + (camp.isCampMultiple() ? "[" + i + "]." : ".") + campRegistre.getVarCodi(), "not.blank");
//												}
//											} else {
//												emptyReg = false;
//											}
//										} else {
//											emptyReg = false;
//										}
//									}
//									if (emptyReg && (i > 0 || camp.isRequired()))
//										errors.rejectValue(camp.getVarCodi() + (camp.isCampMultiple() ? "[" + i + "]" : ""), "fila.not.blank");
//									i++;
//								}
//							}
//						}
//					} else if (!camp.isCampMultiple()) {
//						if (camp.getCampTipus().equals(CampTipusDto.TERMINI)) {
//							Object termini = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
//							String[] termini_arr = (String[])termini;
//							if (termini == null || termini_arr.length < 3 || (termini_arr[0].equalsIgnoreCase("0") && termini_arr[1].equalsIgnoreCase("0") && (termini_arr[2].equalsIgnoreCase("") || termini_arr[2] == null)))
//								errors.rejectValue(camp.getVarCodi(), "not.blank");
//						} else {
//							ValidationUtils.rejectIfEmpty(errors, camp.getVarCodi(), "not.blank");
//						}
//					} else {
//						Object valors = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
//						if (valors == null) {
//							errors.rejectValue(camp.getVarCodi(), "not.blank");
//						} else {
//							for (int i = 0; i < Array.getLength(valors); i++) {
//								Object valor = Array.get(valors, i);
//								if (camp.getCampTipus().equals(CampTipusDto.TERMINI)) {
//									String[] valor_arr = (String[])valor;
//									if (valor == null || (valor_arr).length < 3 || (valor_arr[0].equalsIgnoreCase("0") && valor_arr[1].equalsIgnoreCase("0") && (valor_arr[2].equalsIgnoreCase("0") || valor_arr[2] == null)))
//										errors.rejectValue(camp.getVarCodi() + "[" + i + "]", "not.blank");
//								} else {
//									if ((valor instanceof String && "".equals(valor)) || (!(valor instanceof String) && valor == null)) {
//										errors.rejectValue(camp.getVarCodi() + "[" + i + "]", "not.blank");
//									}
//								}
//							}
//						}
//					}
//				}
//				comprovaCamp(camp, command, errors);
//			}
//			// Només valida amb expressions si no hi ha errors previs
//			if (validarExpresions && !errors.hasErrors()) {
//				boolean errorPropietatNoPresent;
//				String propietatAddicional = null;
//				Object commandPerValidadorExpressions = command;
//				do {
//					errorPropietatNoPresent = false;
//					try {
//						commandPerValidadorExpressions = getCommandPerValidadorExpressions(commandPerValidadorExpressions, propietatAddicional);
//						// TODO: Validador
////						getValidatorPerExpressions(tascaDades, command).validate(
////								commandPerValidadorExpressions,
////								errors);
//					} catch (NotReadablePropertyException ex) {
//						// Si dona un error de que no troba una propietat l'afegeix amb el valor "" i torna a intentar la validació.
//						int iinici = ex.getMessage().indexOf("'");
//						if (iinici != -1) {
//							int ifi = ex.getMessage().indexOf("'", iinici + 1);
//							propietatAddicional = ex.getMessage().substring(iinici + 1, ifi);
//							errorPropietatNoPresent = true;
//						} else
//							errorPropietatNoPresent = false;
//					}
//				} while(errorPropietatNoPresent);
//			}
//			logger.debug(errors.toString());
//		} catch (Exception ex) {
//			logger.error("Error en el validator", ex);
//			errors.reject("error.validator");
//		}
//		logger.debug("Errors de validació en el formulari de la tasca: " + errors.toString());
//	}

	public static String getErrorField(Errors errors, TascaDadaDto dada, LoopTagStatus index) {
		try {
			FieldError fieldError = errors.getFieldError(dada.getVarCodi() + "[" + index.getIndex()  + "]");
			return fieldError.getCode();
		} catch (Exception ex) {
			return null;
		}
	}
	
	/** Comprova si el valor del camp del registre és un valor buit, */ 
	private boolean isCampRegistreEmpty(Object oValor) {
		boolean empty = false;
		if (oValor == null) {
			empty = true;
		} else {
			if (oValor instanceof String[]) {
				String[] oValor_arr = (String[])oValor;
				empty = (oValor_arr.length < 3 || (oValor_arr[0].equalsIgnoreCase("0") && oValor_arr[1].equalsIgnoreCase("0") && (oValor_arr[2].equalsIgnoreCase("0") || oValor_arr[2] == null)));
			} else if (oValor instanceof String) {
				empty = "".equals( ((String) oValor).trim());
			}
		}
		return empty;
	}
	
//	/** Valid el registre i retorna true si el registre és inválid. És invàlid si té algun camp obligatori buit. */
//	private boolean registreInvalid(TascaDadaDto camp, Object registre, Errors errors) throws Exception {
//		boolean invalid = false;
//		// Registre
//		if (registre != null) {
//			if  (camp.isCampMultiple()) {
//				List<TascaDadaDto> registreDades = camp.getMultipleDades().get(0).getRegistreDades();
//				Object[] registres = (Object[])registre;
//				int i = 0;
//				for (Object reg: registres) {
//					for (TascaDadaDto campRegistre : registreDades) {
//						if (campRegistre.isRequired()) {
//							if (isCampRegistreEmpty(PropertyUtils.getProperty(reg, campRegistre.getVarCodi()))) {
//								errors.rejectValue(camp.getVarCodi() + "[" + i + "]." + campRegistre.getVarCodi(), "not.blank");
//								invalid = true;
//							}
//						}
//					}
//					i++;
//				}
//			} else {
//				List<TascaDadaDto> registreDades = camp.getRegistreDades();
//				for (TascaDadaDto campRegistre : registreDades) {
//					if (campRegistre.isRequired()) {
//						if (isCampRegistreEmpty(PropertyUtils.getProperty(registre, campRegistre.getVarCodi()))) {
//							invalid = true;
//							errors.rejectValue(camp.getVarCodi() + "." + campRegistre.getVarCodi(), "not.blank");
//						}
//					}
//				}
//			}
//		}
//		return invalid;
//	}

	private void comprovaCamp(TascaDadaDto camp, Object command, Errors errors) throws Exception {
		if (camp != null && camp.getCampTipus() != null) {
			if (camp.getCampTipus().equals(CampTipusDto.STRING)) {
				try {
					if (camp.isCampMultiple()) {
						String[] valors = (String[]) PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
						if (valors != null)
							for (String valor : valors) {
								if (valor != null && valor.length() > STRING_MAX_LENGTH)
									errors.rejectValue(camp.getVarCodi(), "max.length");
							}
					} else {
						String valor = (String) PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
						if (valor != null && valor.length() > STRING_MAX_LENGTH)
							errors.rejectValue(camp.getVarCodi(), "max.length");
					}
				} catch (NoSuchMethodException ex) {
					logger.error("No s'ha pogut trobar la propietat '" + camp.getVarCodi() + "' con campId " + camp.getCampId());
				}
			} else if (camp.getCampTipus().equals(CampTipusDto.DATE) && camp.getText() != null && !camp.getText().isEmpty()) {
				try {
					PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
					String valor = camp.getText(); 
					if (valor != null) {
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						sdf.setLenient(false);
						sdf.parse(valor);
					}
				} catch (NoSuchMethodException ex) {
					logger.error("No s'ha pogut trobar la propietat '" + camp.getVarCodi() + "' con campId " + camp.getCampId());
				} catch (ParseException  ex) {
					errors.rejectValue(camp.getVarCodi(), "error.camp.dada.valida");
				}
			}
		}
	}

//	private List<TascaDadaDto> getTascaDades(Object command) throws Exception {
//		if (tascaDades != null) {
//			return tascaDades;
//		} else {
//			String id = (String)PropertyUtils.getSimpleProperty(command, "id");
//			return tascaService.findDades(id);
//		}
//	}

	// TODO: Validador
//	private Validator getValidatorPerExpressions(
//			List<TascaDadaDto> tascaDadas,
//			Object command) {
//		SimpleBeanValidationConfigurationLoader validationConfigurationLoader = new SimpleBeanValidationConfigurationLoader();
//		DefaultBeanValidationConfiguration beanValidationConfiguration = new DefaultBeanValidationConfiguration();
//		for (TascaDadaDto camp: tascaDadas) {
//			this.getValidadorPerCamp(
//					camp,
//					null,	// Registre del camp
//					null,	// índex dada múltiple
//					command,
//					beanValidationConfiguration);
//		}
//		validationConfigurationLoader.setClassValidation(
//				Object.class,
//				beanValidationConfiguration);
//		return new BeanValidator(validationConfigurationLoader);
//	}

	// TODO: Validador
//	/** Afegeix en el beanValidationCofiguration una configuració pel camp en el cas que tingui validacions. Si
//	 * el camp és un registre llavors invoca la funció amb cada camp del registre passant el registre com a paràmetre
//	 * per adequar els codis de les variables.
//	 * @param camp
//	 * @param registre
//	 * @param command
//	 * @param beanValidationConfiguration
//	 */
//	private void getValidadorPerCamp(
//			TascaDadaDto camp,
//			TascaDadaDto registre,
//			Integer indexMultiple,
//			Object command,
//			DefaultBeanValidationConfiguration beanValidationConfiguration) {
//
//		if (camp.getCampTipus() == CampTipusDto.REGISTRE) {
//			if (camp.getRegistreDades() != null) {
//				for (TascaDadaDto registreDada : camp.getRegistreDades()) {
//					// Crida aquest mètode sobre els camps del registre passant el registre com a paràmetre
//					this.getValidadorPerCamp(
//							registreDada, //
//							camp, // registre
//							indexMultiple,
//							command,
//							beanValidationConfiguration);
//				}
//			} else if (camp.isCampMultiple()) {
//				Integer index = 0;
//				for (TascaDadaDto registreDada : camp.getMultipleDades()) {
//					if (registreDada.getVarValor() != null)
//						// Crida la validació per cada dada múltiple
//						this.getValidadorPerCamp(
//								registreDada, //
//								camp, // registre
//								index++,
//								command,
//								beanValidationConfiguration);
//				}
//			}
//		}
//
//		// Comprovoa les validacions del camp
//		if (camp.getValidacions() != null) {
//			// Si és un camp d'un registre llavors el codi de la variable estarà compost [registre codi].[variable codi]
//			String codiVariable =
//					(registre != null? registre.getVarCodi() : "")
//					+ (indexMultiple != null? "["+indexMultiple+"]" : "")
//					+ (registre != null? "." : "")
//					+ camp.getVarCodi();
//
//			for (ValidacioDto validacio: camp.getValidacions()) {
//				// Si és una validació dins d'un registre llavors corregeix la ruta "var_nom is BLANK" -> "registre_nom.var_nom is BLANK"
//				if (registre != null && validacio.getExpressio().contains(camp.getVarCodi())) {
//					validacio.setExpressio(validacio.getExpressio().replace(camp.getVarCodi(), codiVariable));
//				}
//				if (camp.isCampMultiple()) {
//					try {
//						Object valors = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
//						if (valors != null) {
//							String expressio = validacio.getExpressio();
//							if (expressio.indexOf("sum(" + codiVariable + ")") != -1) {
//								if (camp.getCampTipus().equals(CampTipusDto.INTEGER)) {
//									Long suma = 0L;
//									for (Long valor: (Long[])valors) {
//										suma += (valor == null ? 0L : valor);
//									}
//									expressio = expressio.replace("sum(" + codiVariable + ")", suma.toString());
//								} else if (camp.getCampTipus().equals(CampTipusDto.FLOAT)) {
//									Double suma = 0.0;
//									for (Double valor: (Double[])valors) {
//										suma += (valor == null ? 0.0 : valor);
//									}
//									expressio = expressio.replace("sum(" + codiVariable + ")", suma.toString());
//								} else if (camp.getCampTipus().equals(CampTipusDto.PRICE)) {
//									BigDecimal suma = new BigDecimal(0);
//									for (BigDecimal valor: (BigDecimal[])valors) {
//										if (valor == null) valor = new BigDecimal(0);
//										suma = suma.add(valor);
//									}
//									expressio = expressio.replace("sum(" + codiVariable + ")", suma.toString());
//								}
//								afegirExpressioValidacio(
//										codiVariable,
//										expressio,
//										camp.getCampEtiqueta() + ": " + validacio.getMissatge(),
//										"error.camp." + codiVariable,
//										beanValidationConfiguration);
//							} else {
//								for (int i = 0; i < Array.getLength(valors); i++) {
//									String expressioFill = expressio.replaceAll(camp.getVarCodi() + "[^\\[]" , camp.getVarCodi() + "[" + i + "]");
//									afegirExpressioValidacio(
//											codiVariable + "[" + i + "]",
//											expressioFill,
//											camp.getCampEtiqueta() + ": " + validacio.getMissatge(),
//											"error.camp." + codiVariable,
//											beanValidationConfiguration);
//								}
//							}
//						}
//					} catch (Exception ex) {
//						logger.error("No s'ha pogut generar la validació de l'expressió definida per a la variable '" + codiVariable + "' amb campId " + camp.getCampId());
//					}
//				} else {
//					afegirExpressioValidacio(
//							codiVariable,
//							validacio.getExpressio(),
//							validacio.getMissatge(),
//							"error.camp." + codiVariable,
//							beanValidationConfiguration);
//				}
//			}
//		}
//	}

	// TODO: Validador
//	private void afegirExpressioValidacio(
//			String varCodi,
//			String validacioExpressio,
//			String validacioMissatge,
//			String errorCodi,
//			DefaultBeanValidationConfiguration beanValidationConfiguration) {
//		ExpressionValidationRule validationRule = new ExpressionValidationRule(
//				new ValangConditionExpressionParser(),
//				validacioExpressio);
//		logger.debug("Afegint expressió VALANG al validador (" +
//				"camp=" + varCodi + ", " +
//				"expressió=" + validacioExpressio + ", " +
//				"missatge=" + validacioMissatge + ")");
//		validationRule.setDefaultErrorMessage(validacioMissatge);
//		validationRule.setErrorCode(errorCodi);
//		beanValidationConfiguration.addPropertyRule(
//				varCodi,
//				validationRule);
//	}

	private Object getCommandPerValidadorExpressions(
			Object commandOriginal,
			String propietatAddicional) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		// Crea una còpia del command amb els strings que sigin NULL amb valor buit
		// per evitar excepcions en les expressions VALANG
		BeanGenerator bg = new BeanGenerator();
		for (PropertyDescriptor descriptor: PropertyUtils.getPropertyDescriptors(commandOriginal)) {
			if (!"class".equals(descriptor.getName())) {
				bg.addProperty(descriptor.getName(), descriptor.getPropertyType());
			}
		}
		if (propietatAddicional != null) {
			bg.addProperty(propietatAddicional, String.class);
		}
		Object command = bg.create();
		for (PropertyDescriptor descriptor: PropertyUtils.getPropertyDescriptors(commandOriginal)) {
			if (!"class".equals(descriptor.getName())) {
				Object valor = PropertyUtils.getProperty(commandOriginal, descriptor.getName());
				if (String.class.equals(descriptor.getPropertyType()) && valor == null) {
					valor = "";
				}
				PropertyUtils.setProperty(
						command,
						descriptor.getName(),
						valor);
			}
		}
		if (propietatAddicional != null) {
			PropertyUtils.setProperty(
					command,
					propietatAddicional,
					"");
		}
		return command;
	}
	
//	public void setTascaDades(List<TascaDadaDto> tascaDades) {
//		this.tascaDades = tascaDades;
//	}

	private static final Log logger = LogFactory.getLog(TascaFormValidatorHelper.class);

}
