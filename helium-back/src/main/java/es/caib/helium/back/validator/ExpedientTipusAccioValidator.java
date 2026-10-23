package es.caib.helium.back.validator;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.back.command.ExpedientTipusAccioCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.commons.dto.AccioDto;
import es.caib.helium.commons.dto.AccioTipusEnumDto;
import es.caib.helium.commons.dto.DefinicioProcesDto;
import es.caib.helium.commons.dto.ExpedientTipusDto;
import es.caib.helium.commons.dto.ExpedientTipusTipusEnumDto;
import es.caib.helium.commons.dto.handlers.HandlerDto;
import es.caib.helium.commons.dto.handlers.HandlerParametreDto;
import es.caib.helium.logic.intf.service.AccioService;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;

/**
 * Validador per al manteniment d'accios del tipus d'expedient:
 * - Comprova que el codi:
 * 		- no estigui duplicat
 * - Segons el tipus
 * 	- Per les accions:
 * 		- Comprova que l'acció pertanyi a la definició de procés
 * 	- Pels handlers predefinits:
 * 		- Comprova que la classe no estigui buida
 * 		- Comprova que tots els paràmetres obligatoris estiguin informats
 * 		- Comprova que el handler predefinit existeixi a la llista.
 */
public class ExpedientTipusAccioValidator implements ConstraintValidator<ExpedientTipusAccio, ExpedientTipusAccioCommand>{

	private String codiMissatge;
	@Autowired
	private AccioService accioService;
	@Autowired
	private DissenyService dissenyService;
	@Autowired
	private ExpedientTipusService expedientTipusService;

	@Override
	public void initialize(ExpedientTipusAccio anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusAccioCommand accio, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (accio.getCodi() != null) {
			AccioDto repetit = accioService.findAmbCodi(
					accio.getExpedientTipusId(),
					accio.getDefinicioProcesId(),
					accio.getCodi());
			if(repetit != null && (accio.getId() == null || !accio.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.repetit", null))
						.addNode("codi")
						.addConstraintViolation();
				valid = false;
			}
		}
		// Si hi ha expedient tipus informat comprova que la acció pertany a la darrera
		// versió
		boolean perEstats = false;
		if (accio.getExpedientTipusId() != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbId(accio.getExpedientTipusId());
			perEstats = ExpedientTipusTipusEnumDto.ESTAT.equals(expedientTipus.getTipus());
			if (accio.getDefprocBpmKey() != null && accio.getBpmAction() != null ) {
				// Darrera versió de la definició de procés
				DefinicioProcesDto definicioProces = dissenyService.findDarreraVersioForExpedientTipusIDefProcCodi(
																	accio.getExpedientTipusId(),
																	accio.getDefprocBpmKey());
				List<String> accions = dissenyService.findAccionsBpmOrdenades(definicioProces.getId());
				if (!accions.contains(accio.getBpmAction())) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage(this.codiMissatge + ".accio.no.existeix", null))
							.addNode("bpmAction")
							.addConstraintViolation();
					valid = false;
				}
			}
		}
		if (accio.getTipus() == null) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(this.codiMissatge + ".tipus.null", null))
					.addNode("tipus")
					.addConstraintViolation();
			valid = false;
		} else {
			if (AccioTipusEnumDto.ACCIO.equals(accio.getTipus())) {
				if ((accio.getDefprocBpmKey() == null || accio.getDefprocBpmKey().trim().isEmpty())) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("NotEmpty", null))
							.addNode("defprocBpmKey")
							.addConstraintViolation();
					valid = false;
				}
				if (accio.getBpmAction() == null || accio.getBpmAction().trim().isEmpty()) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("NotEmpty", null))
							.addNode("bpmAction")
							.addConstraintViolation();
					valid = false;
				}
			} else if (AccioTipusEnumDto.HANDLER_PROPI.equals(accio.getTipus())) {

				if (!perEstats && (accio.getDefprocBpmKey() == null || accio.getDefprocBpmKey().trim().isEmpty())) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("NotEmpty", null))
							.addNode("defprocBpmKey")
							.addConstraintViolation();
					valid = false;
				}
				if (accio.getHandlerPropi() == null || accio.getHandlerPropi().trim().isEmpty()) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("NotEmpty", null))
							.addNode("handlerPropi")
							.addConstraintViolation();
					valid = false;
				}

			} else if (AccioTipusEnumDto.HANDLER_PREDEFINIT.equals(accio.getTipus())) {
				if (accio.getHandlerPredefinit() == null || accio.getHandlerPredefinit().isEmpty()) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("NotEmpty", null))
							.addNode("handlerPredefinit")
							.addConstraintViolation();
					valid = false;
				} else {
					HandlerDto handler = null;
					for (HandlerDto h : dissenyService.getHandlersPredefinits()) {
						if (accio.getHandlerPredefinit().equals(h.getClasse())) {
							handler = h;
							break;
						}
					}
					if (handler == null) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(this.codiMissatge + ".handler.classe.no.existeix", new Object[] { accio.getHandlerPredefinit()}))
								.addNode("handlerPredefinit")
								.addConstraintViolation();
						valid = false;
					} else {
						// Comprova que tots els paràmetres obligatoris estiguin informats
						List<String> parametresObligatoris = new ArrayList<String>();
						for (HandlerParametreDto parametre : handler.getParametres()) {
							if (parametre.isObligatori()
								&& (!accio.getHandlerDades().containsKey(parametre.getParam())
										|| accio.getHandlerDades().get(parametre.getParam()) == null)
								&& (!accio.getHandlerDades().containsKey(parametre.getVarParam())
										|| accio.getHandlerDades().get(parametre.getVarParam()) == null))
							{
								parametresObligatoris.add(parametre.getNom());
							}
						}
						if (!parametresObligatoris.isEmpty()) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(this.codiMissatge + ".handler.parametres.obligatoris", new Object[] { parametresObligatoris}))
									.addNode("handlerPredefinit")
									.addConstraintViolation();
							valid = false;
						}
					}
				}

			} else if (AccioTipusEnumDto.SCRIPT.equals(accio.getTipus())
					&& (accio.getScript() == null || accio.getScript().trim().isEmpty())) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("script")
						.addConstraintViolation();
				valid = false;
			}
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}

}
