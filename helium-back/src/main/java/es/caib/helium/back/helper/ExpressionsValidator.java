package es.caib.helium.back.helper;

import es.caib.helium.commons.dto.CampTipusDto;
import es.caib.helium.commons.dto.TascaDadaDto;
import es.caib.helium.commons.dto.ValidacioDto;
import org.apache.commons.beanutils.PropertyUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.List;

public class ExpressionsValidator implements Validator {

	private static final Logger logger = LoggerFactory.getLogger(ExpressionsValidator.class);
	private final ExpressionParser parser;

	private final List<TascaDadaDto> tascaDadas;
	SimpleEvaluationContext ctx;

	public ExpressionsValidator(List<TascaDadaDto> tascaDadas) {
		this.tascaDadas = tascaDadas;
		this.parser = new SpelExpressionParser(
			new SpelParserConfiguration(
				SpelCompilerMode.OFF,   // compilerMode
				null,                   // compilerClassLoader
				false,                  // autoGrowNullReferences = false
				false,                  // autoGrowCollections = false
				Integer.MAX_VALUE,      // or set a real limit
				500                        // maximumExpressionLength
			)
		);
	}

	public boolean supports(Class clazz) {
		return true;
	}

	public void validate(@NotNull Object target,
						 @NotNull Errors errors) {
		this.ctx = SimpleEvaluationContext.forReadOnlyDataBinding().withRootObject(target).build();
		for (TascaDadaDto dada : tascaDadas) {
			getValidadorPerCamp(
				target,
				dada,
				null,
				null,
				errors);
		}
	}

	/**
	 * Afegeix en el beanValidationCofiguration una configuració pel camp en el cas que tingui validacions. Si
	 * el camp és un registre llavors invoca la funció amb cada camp del registre passant el registre com a paràmetre
	 * per adequar els codis de les variables.
	 *
	 * @param command       -
	 * @param camp          -
	 * @param registre      -
	 * @param indexMultiple -
	 * @param errors        -
	 */
	private void getValidadorPerCamp(
		Object command,
		TascaDadaDto camp,
		TascaDadaDto registre,
		Integer indexMultiple,
		Errors errors) {
		if (camp.getCampTipus() == CampTipusDto.REGISTRE) {
			if (camp.getRegistreDades() != null) {
				for (TascaDadaDto registreDada : camp.getRegistreDades()) {
					// Crida aquest mètode sobre els camps del registre passant el registre com a paràmetre
					this.getValidadorPerCamp(
						command,
						registreDada,
						camp, // registre
						indexMultiple,
						errors);
				}
			} else if (camp.isCampMultiple()) {
				int index = 0;
				for (TascaDadaDto registreDada : camp.getMultipleDades()) {
					if (registreDada.getVarValor() != null)
						// Crida la validació per cada dada múltiple
						this.getValidadorPerCamp(
							command,
							registreDada,
							camp, // registre
							index++,
							errors);
				}
			}
		}

		// Comprovoa les validacions del camp
		if (camp.getValidacions() != null) {
			// Si és un camp d'un registre llavors el codi de la variable estarà compost [registre codi].[variable codi]
			String codiVariable =
				(registre != null ? registre.getVarCodi() : "")
					+ (indexMultiple != null ? "[" + indexMultiple + "]" : "")
					+ (registre != null ? "." : "")
					+ camp.getVarCodi();

			String errorCodi = "error.camp." + codiVariable;

			for (ValidacioDto validacio : camp.getValidacions()) {
				// Si és una validació dins d'un registre llavors corregeix la ruta "var_nom is BLANK" -> "registre_nom.var_nom is BLANK"
				if (registre != null && validacio.getExpressio().contains(camp.getVarCodi())) {
					validacio.setExpressio(validacio.getExpressio().replace(camp.getVarCodi(), codiVariable));
				}

				if (camp.isCampMultiple()) {
					try {
						Object valors = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
						if (valors != null) {
							String expressio = validacio.getExpressio();
							if (expressio.contains("sum(" + codiVariable + ")")) {
								if (camp.getCampTipus().equals(CampTipusDto.INTEGER)) {
									long suma = 0L;
									for (Long valor : (Long[]) valors) {
										suma += (valor == null ? 0L : valor);
									}
									expressio = expressio.replace("sum(" + codiVariable + ")", Long.toString(suma));
								} else if (camp.getCampTipus().equals(CampTipusDto.FLOAT)) {
									double suma = 0.0;
									for (Double valor : (Double[]) valors) {
										suma += (valor == null ? 0.0 : valor);
									}
									expressio = expressio.replace("sum(" + codiVariable + ")", Double.toString(suma));
								} else if (camp.getCampTipus().equals(CampTipusDto.PRICE)) {
									BigDecimal suma = new BigDecimal(0);
									for (BigDecimal valor : (BigDecimal[]) valors) {
										if (valor == null) valor = new BigDecimal(0);
										suma = suma.add(valor);
									}
									expressio = expressio.replace("sum(" + codiVariable + ")", suma.toString());
								}
								afegirExpressioValidacio(
									codiVariable,
									expressio,
									camp.getCampEtiqueta() + ": " + validacio.getMissatge(),
									errorCodi,
									errors);
							} else {
								for (int i = 0; i < Array.getLength(valors); i++) {
									String expressioFill = expressio.replaceAll(camp.getVarCodi() + "[^\\[]", camp.getVarCodi() + "[" + i + "]");
									afegirExpressioValidacio(
										codiVariable + "[" + i + "]",
										expressioFill,
										camp.getCampEtiqueta() + ": " + validacio.getMissatge(),
										errorCodi,
										errors);
								}
							}
						}
					} catch (Exception ex) {
						logger.error("No s'ha pogut generar la validació de l'expressió definida per a la variable '{}' amb campId {}", codiVariable, camp.getCampId());
					}
				} else {
					afegirExpressioValidacio(
						codiVariable,
						validacio.getExpressio(),
						validacio.getMissatge(),
						errorCodi,
						errors);
				}
			}
		}
	}

	private void afegirExpressioValidacio(
		String varCodi,
		String validacioExpressio,
		String validacioMissatge,
		String errorCodi,
		Errors errors) {
		logger.debug("Afegint expressió SpEL al validador (camp={}, expressió={}, missatge={})", varCodi, validacioExpressio, validacioMissatge);
		if (Boolean.FALSE == parser.parseExpression(validacioExpressio).getValue(ctx, Boolean.class))
			errors.rejectValue(varCodi, errorCodi, validacioMissatge);
	}
}
