/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.lang.reflect.Array;
import java.util.Map;

import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.util.EntornActual;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validador per als formularis de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaFormValidator implements Validator {
	private static final int STRING_MAX_LENGTH = 2048;
	private static ThreadLocal<TascaDto> tascaThreadLocal = new ThreadLocal<TascaDto>();
	private TascaService tascaService;
	private ExpedientService expedientService;
	private Map<String, Object> valorsRegistre;
	boolean inicial;
	boolean validarObligatoris;
	public TascaFormValidator(TascaService tascaService) {
		this.tascaService = tascaService;
		this.inicial = false;
		this.validarObligatoris = true;
	}
	public TascaFormValidator(TascaService tascaService, boolean validarObligatoris) {
		this.tascaService = tascaService;
		this.inicial = false;
		this.validarObligatoris = validarObligatoris;
	}
	public TascaFormValidator(ExpedientService expedientService) {
		this.expedientService = expedientService;
		this.inicial = true;
		this.validarObligatoris = true;
	}
	public TascaFormValidator(
			ExpedientService expedientService,
			Map<String, Object> valorsRegistre) {
		this.expedientService = expedientService;
		this.valorsRegistre = valorsRegistre;
		this.validarObligatoris = true;
		this.inicial = true;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(Object.class);
	}
	public void validate(Object command, Errors errors) {
		try {
			TascaDto tasca = getTasca(command);
			for (CampTasca camp: tasca.getCamps()) {
				if (validarObligatoris && camp.isRequired()) {
					if (camp.getCamp().getTipus().equals(TipusCamp.REGISTRE)) {
						if (tascaService != null) {
							Object[] valor = (Object[])tascaService.getVariable(
									EntornActual.getEntornId(),
									tasca.getId(),
									camp.getCamp().getCodi());
							if (valor == null || valor.length == 0)
								ValidationUtils.rejectIfEmpty(errors, camp.getCamp().getCodi(), "not.blank");
						} else if (valorsRegistre != null) {
							Object valor = valorsRegistre.get(camp.getCamp().getCodi());
							if (valor == null || (valor instanceof Object[] && ((Object[])valor).length == 0))
								ValidationUtils.rejectIfEmpty(errors, camp.getCamp().getCodi(), "not.blank");
						} else {
							ValidationUtils.rejectIfEmpty(errors, camp.getCamp().getCodi(), "not.blank");
						}
					} else if (!camp.getCamp().isMultiple()) {
						ValidationUtils.rejectIfEmpty(errors, camp.getCamp().getCodi(), "not.blank");
					} else {
						Object valors = PropertyUtils.getSimpleProperty(command, camp.getCamp().getCodi());
						boolean esBuit = true;
						for (int i = 0; i < Array.getLength(valors); i++) {
							Object valor = Array.get(valors, i);
							if ((valor instanceof String && !"".equals(valor)) || (!(valor instanceof String) && valor != null)) {
								esBuit = false;
								break;
							}
						}
						if (esBuit)
							errors.rejectValue(camp.getCamp().getCodi(), "not.blank");
					}
				}
				if (camp != null && camp.getCamp() != null && camp.getCamp().getTipus() != null) {
					if (camp.getCamp().getTipus().equals(TipusCamp.STRING)) { // || camp.getCamp().getTipus().equals(TipusCamp.TEXTAREA)) {
						try {
							if (camp.getCamp().isMultiple()) {
								String[] valors = (String[])PropertyUtils.getSimpleProperty(command, camp.getCamp().getCodi());
								for (String valor: valors) {
									if (valor != null && valor.length() > STRING_MAX_LENGTH)
										errors.rejectValue(camp.getCamp().getCodi(), "max.length");
								}
							} else {
								String valor = (String)PropertyUtils.getSimpleProperty(command, camp.getCamp().getCodi());
								if (valor != null && valor.length() > STRING_MAX_LENGTH)
									errors.rejectValue(camp.getCamp().getCodi(), "max.length");
							}
						} catch (NoSuchMethodException ex) {
							logger.error("No s'ha pogut trobar la propietat '" + camp.getCamp().getCodi() + "' al command de la tasca " + tasca.getId());
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Error en el validator", ex);
			errors.reject("error.validator");
		}
	}

	public void setTasca(TascaDto tasca) {
		tascaThreadLocal.set(tasca);
	}



	private TascaDto getTasca(Object command) throws Exception {
		if (tascaThreadLocal.get() != null)
			return tascaThreadLocal.get();
		if (inicial) {
			Long entornId = (Long)PropertyUtils.getSimpleProperty(command, "entornId");
			Long expedientTipusId = (Long)PropertyUtils.getSimpleProperty(command, "expedientTipusId");
			Long definicioProcesId = (Long)PropertyUtils.getSimpleProperty(command, "definicioProcesId");
			return expedientService.getStartTask(
					entornId,
					expedientTipusId,
					definicioProcesId,
	        		null);
		} else {
			String id = (String)PropertyUtils.getSimpleProperty(command, "id");
			Long entornId = (Long)PropertyUtils.getSimpleProperty(command, "entornId");
			return tascaService.getById(
					entornId,
					id,
					null,
					null,
					false,
					false);
		}
	}

	private static final Log logger = LogFactory.getLog(TascaFormValidator.class);

}
