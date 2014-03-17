/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;

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
public class TascaFormValidatorHelper implements Validator {
	private static final int STRING_MAX_LENGTH = 2048;
	private static ThreadLocal<List<TascaDadaDto>> tascaThreadLocal = new ThreadLocal<List<TascaDadaDto>>();
	@Resource(name = "tascaServiceV3")
	private TascaService tascaService;
	@Resource(name = "expedientServiceV3")
	private ExpedientService expedientService;
	private Map<String, Object> valorsRegistre;
	boolean inicial;
	boolean validarObligatoris;

	public TascaFormValidatorHelper(TascaService tascaService) {
		this.tascaService = tascaService;
		this.inicial = false;
		this.validarObligatoris = true;
	}

	public TascaFormValidatorHelper(TascaService tascaService, boolean validarObligatoris) {
		this.tascaService = tascaService;
		this.inicial = false;
		this.validarObligatoris = validarObligatoris;
	}

	public TascaFormValidatorHelper(ExpedientService expedientService) {
		this.expedientService = expedientService;
		this.inicial = true;
		this.validarObligatoris = true;
	}

	public TascaFormValidatorHelper(ExpedientService expedientService, Map<String, Object> valorsRegistre) {
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
			List<TascaDadaDto> tascas = getTascaDades(command);
			for (TascaDadaDto camp : tascas) {
				if (validarObligatoris && camp.isRequired()) {
					if (camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
						if (tascaService != null) {
							String tascaId = (String) PropertyUtils.getSimpleProperty(command, "id");
							Object[] valor = (Object[]) tascaService.getVariable(EntornActual.getEntornId(), tascaId, camp.getVarCodi());
							if (valor == null || valor.length == 0)
								ValidationUtils.rejectIfEmpty(errors, camp.getVarCodi(), "not.blank");
						} else if (valorsRegistre != null) {
							Object valor = valorsRegistre.get(camp.getVarCodi());
							if (valor == null || (valor instanceof Object[] && ((Object[]) valor).length == 0))
								ValidationUtils.rejectIfEmpty(errors, camp.getVarCodi(), "not.blank");
						} else {
							ValidationUtils.rejectIfEmpty(errors, camp.getVarCodi(), "not.blank");
						}
					} else if (!camp.isCampMultiple()) {
						ValidationUtils.rejectIfEmpty(errors, camp.getVarCodi(), "not.blank");
					} else {
						Object valors = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
						boolean esBuit = true;
						for (int i = 0; i < Array.getLength(valors); i++) {
							Object valor = Array.get(valors, i);
							if ((valor instanceof String && !"".equals(valor)) || (!(valor instanceof String) && valor != null)) {
								esBuit = false;
								break;
							}
						}
						if (esBuit)
							errors.rejectValue(camp.getVarCodi(), "not.blank");
					}
				}
				if (camp != null && camp != null && camp.getCampTipus() != null) {
					if (camp.getCampTipus().equals(CampTipusDto.STRING)) {
						try {
							if (camp.isCampMultiple()) {
								String[] valors = (String[]) PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
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
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Error en el validator", ex);
			errors.reject("error.validator");
		}
	}

	public void setTasca(List<TascaDadaDto> tasca) {
		tascaThreadLocal.set(tasca);
	}

	private List<TascaDadaDto> getTascaDades(Object command) throws Exception {
		if (tascaThreadLocal.get() != null) {
			return tascaThreadLocal.get();
		}
		Long entornId = (Long) PropertyUtils.getSimpleProperty(command, "entornId");
		if (inicial) {
			Long expedientTipusId = (Long) PropertyUtils.getSimpleProperty(command, "expedientTipusId");
			Long definicioProcesId = (Long) PropertyUtils.getSimpleProperty(command, "definicioProcesId");
			ExpedientTascaDto tasca = expedientService.getStartTask(entornId, expedientTipusId, definicioProcesId, null);
			return tascaService.findDadesPerTasca(tasca.getId());
		} else {
			String id = (String) PropertyUtils.getSimpleProperty(command, "id");
			return tascaService.findDadesPerTasca(id);
		}
	}

	private static final Log logger = LogFactory.getLog(TascaFormValidatorHelper.class);
}
