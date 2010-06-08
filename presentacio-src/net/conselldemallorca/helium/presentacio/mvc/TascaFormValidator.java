/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.lang.reflect.Array;

import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.hibernate.CampTasca;
import net.conselldemallorca.helium.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.util.EntornActual;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validador per als formularis de tasca
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class TascaFormValidator implements Validator {
	private static ThreadLocal<TascaDto> tascaThreadLocal = new ThreadLocal<TascaDto>();
	private TascaService tascaService;
	private ExpedientService expedientService;
	boolean inicial;
	public TascaFormValidator(TascaService tascaService) {
		this.tascaService = tascaService;
		this.inicial = false;
	}
	public TascaFormValidator(ExpedientService expedientService) {
		this.expedientService = expedientService;
		this.inicial = true;
	}
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(Object.class);
	}
	public void validate(Object command, Errors errors) {
		try {
			TascaDto tasca = getTasca(command);
			for (CampTasca camp: tasca.getCamps()) {
				if (camp.isRequired()) {
					if (camp.getCamp().getTipus().equals(TipusCamp.REGISTRE)) {
						Object[] valor = (Object[])tascaService.getVariable(
								EntornActual.getEntornId(),
								tasca.getId(),
								camp.getCamp().getCodi());
						if (valor == null || valor.length == 0)
							ValidationUtils.rejectIfEmpty(errors, camp.getCamp().getCodi(), "not.blank");
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
			}
		} catch (Exception ex) {
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
			return tascaService.getById(entornId, id);
		}
	}

}
