package es.caib.helium.back.validator;

import es.caib.helium.back.command.ExpedientTipusIntegracioDistribucioCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validador per a la comanda de modificar les dades d'integració amb Distribució. Valida:
 * - Que no hi hagi cap altre tipus d'expedient amb el mateix codi de procediment en cap altre entorn.
 */
public class ExpedientTipusIntegracioDistribucioValidator implements ConstraintValidator<ExpedientTipusIntegracioDistribucio, ExpedientTipusIntegracioDistribucioCommand>{

	private String codiMissatge;
	@Autowired
	private ExpedientTipusService expedientTipusService;
	
	@Override
	public void initialize(ExpedientTipusIntegracioDistribucio anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusIntegracioDistribucioCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		
		if (command.isActiu()) {
			// Comprova que no hi hagi cap altre tipus d'expedient a l'entorn actiu pel mateix codi de procediment i codi de tipus d'assumpte
			ExpedientTipusDto expedientTipus = expedientTipusService.findPerDistribucioValidacio(command.getCodiProcediment(), command.getCodiAssumpte());
			if (expedientTipus != null 
					&& expedientTipus.isDistribucioActiu() 
					&& !expedientTipus.getId().equals(command.getId())) 
			{
				String errMsg = // expedient.tipus.integracio.distribucio.validacio.codiProcediment.repetit=Ja existeix el tipus d''expedient "{0} - {1}" a l''entorn "{2} - {3}" actiu per la combinació [codi procediment="{4}", codi assumpte="{5}"]
						MessageHelper.getInstance().getMessage(codiMissatge, new Object[] {
								expedientTipus.getCodi(),
								expedientTipus.getNom(),
								expedientTipus.getEntorn().getCodi(),
								expedientTipus.getEntorn().getNom(),
								command.getCodiProcediment(),
								command.getCodiAssumpte()
						});
				context.buildConstraintViolationWithTemplate(errMsg)
						.addNode("codiProcediment")
						.addConstraintViolation();
				context.buildConstraintViolationWithTemplate(errMsg)
				.addNode("codiAssumpte")
				.addConstraintViolation();
				valid = false;
			}
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}
}
