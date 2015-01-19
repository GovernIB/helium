/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.LoopTagStatus;

import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.ValidacioDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springmodules.validation.bean.BeanValidator;
import org.springmodules.validation.bean.conf.DefaultBeanValidationConfiguration;
import org.springmodules.validation.bean.conf.loader.SimpleBeanValidationConfigurationLoader;
import org.springmodules.validation.bean.rule.ExpressionValidationRule;
import org.springmodules.validation.util.cel.valang.ValangConditionExpressionParser;

/**
 * Validador per als formularis de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaFormValidatorHelper implements Validator {
	private static final int STRING_MAX_LENGTH = 2048;
	private static ThreadLocal<List<TascaDadaDto>> tascaThreadLocal = new ThreadLocal<List<TascaDadaDto>>();
	@Resource(name = "tascaServiceV3")
	private TascaService tascaService;
	@Resource(name = "expedientServiceV3")
	private ExpedientService expedientService;
	private HttpServletRequest request;
	boolean inicial;
	boolean validarObligatoris;
	boolean validarExpresions;

	public TascaFormValidatorHelper(TascaService tascaService) {
		this.tascaService = tascaService;
		this.inicial = false;
		this.validarObligatoris = true;
		this.validarExpresions = true;
	}

	public TascaFormValidatorHelper(TascaService tascaService, boolean validarObligatoris) {
		this.tascaService = tascaService;
		this.inicial = false;
		// Si validam els obligatoris també validarem les expresions
		this.validarObligatoris = validarObligatoris;
		this.validarExpresions = validarObligatoris;
	}

	public TascaFormValidatorHelper(ExpedientService expedientService) {
		this.expedientService = expedientService;
		this.inicial = true;
		this.validarObligatoris = false;
		this.validarExpresions = true;
	}

	public void setValidarObligatoris(boolean validarObligatoris) {
		this.validarObligatoris = validarObligatoris;
	}
	public void setValidarExpresions(boolean validarExpresions) {
		this.validarExpresions = validarExpresions;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(Object.class);
	}
	
	public void validate(Object command, Errors errors) {
		try {
			List<TascaDadaDto> tascas = getTascaDades(command);
			for (TascaDadaDto camp : tascas) {
				if (validarObligatoris && camp.isRequired()) {
					if (camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
						Object valorRegistre = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
						if (valorRegistre == null || registreEmpty(camp, valorRegistre, errors))
							errors.rejectValue(camp.getVarCodi(), "not.blank");
						else {
							Object[] registres = null;
							List<TascaDadaDto> registreDades = null;
							if (valorRegistre != null) {
								if (camp.isCampMultiple()) {
									registreDades = camp.getMultipleDades().get(0).getRegistreDades();
									registres = (Object[])valorRegistre;
								} else {
									registreDades = camp.getRegistreDades();
									registres = new Object[] {valorRegistre};
								}
								int i = 0;
								for (Object reg: registres) {
									boolean emptyReg = true;
									for (TascaDadaDto campRegistre : registreDades) {
										boolean emptyVal = true;
										Object oValor = PropertyUtils.getProperty(reg, campRegistre.getVarCodi());
										if (oValor != null) {
											if (oValor instanceof TerminiDto) {
												emptyVal = ((TerminiDto)oValor).isEmpty();
											} else if (oValor instanceof String && "".equals(oValor)) {
												emptyVal = true;
											} else {
												emptyVal = false;
											}
										}
										if (emptyVal) {
											if (campRegistre.isRequired()) {
												errors.rejectValue(camp.getVarCodi() + (camp.isCampMultiple() ? "[" + i + "]." : ".") + campRegistre.getVarCodi(), "not.blank");
											}
										} else {
											emptyReg = false;
										}
									}
									if (emptyReg && (i > 0 || camp.isRequired()))
										errors.rejectValue(camp.getVarCodi() + (camp.isCampMultiple() ? "[" + i + "]" : ""), "fila.not.blank");
									i++;
								}
							}
						}
					} else if (!camp.isCampMultiple()) {
						if (camp.getCampTipus().equals(CampTipusDto.TERMINI)) {
							Object termini = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
							if (termini == null || ((TerminiDto)termini).isEmpty())
								errors.rejectValue(camp.getVarCodi(), "not.blank");
						} else {
							ValidationUtils.rejectIfEmpty(errors, camp.getVarCodi(), "not.blank");
						}
					} else {
						Object valors = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
						if (valors == null) {
							errors.rejectValue(camp.getVarCodi(), "not.blank");
						} else {
							for (int i = 0; i < Array.getLength(valors); i++) {
								Object valor = Array.get(valors, i);
								if (camp.getCampTipus().equals(CampTipusDto.TERMINI)) {
									if (valor == null || ((TerminiDto)valor).isEmpty())
										errors.rejectValue(camp.getVarCodi() + "[" + i + "]", "not.blank");
								} else {
									if ((valor instanceof String && "".equals(valor)) || (!(valor instanceof String) && valor == null)) {
										errors.rejectValue(camp.getVarCodi() + "[" + i + "]", "not.blank");
									}
								}
							}
						}
					}
				}
				comprovaCamp(camp, command, errors);
			}
			if (validarExpresions) {
				getValidatorExpresions(tascas, command).validate(command, errors);
			}
			if (request != null) {
				SessionHelper.setAttribute(
						request,
						SessionHelper.VARIABLE_TASCA_ERRROR,
						errors);
			}
			System.out.println(errors.toString());
		} catch (Exception ex) {
			logger.error("Error en el validator", ex);
			errors.reject("error.validator");
		}
	}
	
	public static String getErrorField(Errors errors, TascaDadaDto dada, LoopTagStatus index) {
		try {
			FieldError fieldError = errors.getFieldError(dada.getVarCodi() + "[" + index.getIndex()  + "]");
			return fieldError.getCode();
		} catch (Exception ex) {
			return null;
		}
	}
	private static boolean registreEmpty(TascaDadaDto camp, Object registre, Errors errors) throws Exception {
		boolean empty = true;
		// Registre
		if (registre != null) {
			if  (camp.isCampMultiple()) {
				List<TascaDadaDto> registreDades = camp.getMultipleDades().get(0).getRegistreDades();
				Object[] registres = (Object[])registre;
				for (Object reg: registres) {
					for (TascaDadaDto campRegistre : registreDades) {
						Object oValor = PropertyUtils.getProperty(reg, campRegistre.getVarCodi());
						if (oValor != null) {
							if (oValor instanceof TerminiDto) {
								empty = ((TerminiDto)oValor).isEmpty();
							} else {
								empty = false;
							}
							if (!empty) return false;
						}
					}
				}
			} else {
				List<TascaDadaDto> registreDades = camp.getRegistreDades();
				for (TascaDadaDto campRegistre : registreDades) {
					Object oValor = PropertyUtils.getProperty(registre, campRegistre.getVarCodi());
					if (oValor != null) {
						if (oValor instanceof TerminiDto) {
							empty = ((TerminiDto)oValor).isEmpty();
						} else {
							empty = false;
						}
						if (!empty) break;
					}
				}
			}
		}
		return empty;
	}
	
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

	public void setTasca(List<TascaDadaDto> tasca) {
		tascaThreadLocal.set(tasca);
	}
	
//	public void setExpedient(List<ExpedientDadaDto> expedient) {
//		List<TascaDadaDto> tasca = new ArrayList<TascaDadaDto>();
//		for (ExpedientDadaDto expdada: expedient) {
//			TascaDadaDto tascaDada = new TascaDadaDto();
//			tascaDada.setCampTipus(expdada.getCampTipus());
//			tascaDada.setCampMultiple(expdada.isCampMultiple());
//			tascaDada.setVarCodi(expdada.getVarCodi());
//			tascaDada.setCampId(expdada.getCampId());
//			tascaDada.setText(expdada.getText());
//			tascaDada.setCampEtiqueta(expdada.getCampEtiqueta());
//			tascaDada.setRequired(false);
//			tascaDada.setValidacions(expdada.getValidacions());
//			tasca.add(tascaDada);
//		}
//		tascaThreadLocal.set(tasca);
//	}

	@SuppressWarnings("unchecked")
	private List<TascaDadaDto> getTascaDades(Object command) throws Exception {
		if (tascaThreadLocal.get() != null) {
			return tascaThreadLocal.get();
		}
		if (inicial) {
			List<TascaDadaDto> tascas = null;
			if (PropertyUtils.getSimpleProperty(command, "listaDadas") != null) {
				tascas =  (List<TascaDadaDto>) PropertyUtils.getSimpleProperty(command, "listaDadas");
			} else {
				// TODO
				/*Long entornId = (Long) PropertyUtils.getSimpleProperty(command, "entornId");
				Long expedientTipusId = (Long) PropertyUtils.getSimpleProperty(command, "expedientTipusId");
				Long definicioProcesId = (Long) PropertyUtils.getSimpleProperty(command, "definicioProcesId");
				ExpedientTascaDto tasca = expedientService.getStartTask(entornId, expedientTipusId, definicioProcesId, null);
				tascas = tascaService.findDadesPerTasca(tasca.getId());*/
			}
			return tascas;
		} else {
			String id = (String) PropertyUtils.getSimpleProperty(command, "id");
			return tascaService.findDades(id);
		}
	}
	
	private Validator getValidatorExpresions(List<TascaDadaDto> tascaDadas, Object command) {
		SimpleBeanValidationConfigurationLoader validationConfigurationLoader = new SimpleBeanValidationConfigurationLoader();
		DefaultBeanValidationConfiguration beanValidationConfiguration = new DefaultBeanValidationConfiguration();
		for (TascaDadaDto camp: tascaDadas) {			
			for (ValidacioDto validacio: camp.getValidacions()) {
				if (camp.isCampMultiple()) {
					try {
						Object valors = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
						if (valors != null) {
							String expressio = validacio.getExpressio();
							if (expressio.indexOf("sum(" + camp.getVarCodi() + ")") != -1) {
								if (camp.getCampTipus().equals(CampTipusDto.INTEGER)) {
									Long suma = 0L;
									for (Long valor: (Long[])valors) {
										suma += (valor == null ? 0L : valor); 
									}
									expressio = expressio.replace("sum(" + camp.getVarCodi() + ")", suma.toString());
								} else if (camp.getCampTipus().equals(CampTipusDto.FLOAT)) {
									Double suma = 0.0;
									for (Double valor: (Double[])valors) {
										suma += (valor == null ? 0.0 : valor); 
									}
									expressio = expressio.replace("sum(" + camp.getVarCodi() + ")", suma.toString());
								} else if (camp.getCampTipus().equals(CampTipusDto.PRICE)) {
									BigDecimal suma = new BigDecimal(0);
									for (BigDecimal valor: (BigDecimal[])valors) {
										if (valor == null) valor = new BigDecimal(0);
										suma = suma.add(valor);
									}
									expressio = expressio.replace("sum(" + camp.getVarCodi() + ")", suma.toString());
								}
								ExpressionValidationRule validationRule = new ExpressionValidationRule(
										new ValangConditionExpressionParser(),
										expressio);
								String codiError = "error.camp." + camp.getVarCodi();
								validationRule.setErrorCode(codiError);
								validationRule.setDefaultErrorMessage(camp.getCampEtiqueta() + ": " + validacio.getMissatge());
								beanValidationConfiguration.addPropertyRule(
										camp.getVarCodi(),
										validationRule);
							} else {
								for (int i = 0; i < Array.getLength(valors); i++) {
									String expressio_fill = expressio.replaceAll(camp.getVarCodi() + "[^\\[]" , camp.getVarCodi() + "[" + i + "]");
									ExpressionValidationRule validationRule = new ExpressionValidationRule(
											new ValangConditionExpressionParser(),
											expressio_fill);
									String codiError = "error.camp." + camp.getVarCodi();
									validationRule.setErrorCode(codiError);
									validationRule.setDefaultErrorMessage(camp.getCampEtiqueta() + ": " + validacio.getMissatge());
									beanValidationConfiguration.addPropertyRule(
											camp.getVarCodi() + "[" + i + "]",
											validationRule);
								}
							}
						}
					} catch (Exception ex) {
						logger.error("No s'ha pogut generar la validació de l'expressió definida per a la variable '" + camp.getVarCodi() + "' amb campId " + camp.getCampId());
					}
				} else {
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
			}
		}
		validationConfigurationLoader.setClassValidation(
				Object.class,
				beanValidationConfiguration);
		return new BeanValidator(validationConfigurationLoader);
	}

	private static final Log logger = LogFactory.getLog(TascaFormValidatorHelper.class);

}
