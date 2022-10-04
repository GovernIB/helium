package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioDistribucioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

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
			
			if (expedientTipus != null && !expedientTipus.isSistraActiu() && command.isSistra()) {
				
				String errMsg = "expedient.tipus.integracio.tramits.validacio.no.activar";
						MessageHelper.getInstance().getMessage(errMsg, new Object[] {
								expedientTipus.getCodi(),
								expedientTipus.getNom(),
								expedientTipus.getEntorn().getCodi(),
								expedientTipus.getEntorn().getNom(),
								command.getCodiProcediment(),
								command.getCodiAssumpte()
						});
						context.buildConstraintViolationWithTemplate(errMsg).addNode("sistra").addConstraintViolation();
				valid = false;
			}
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}
}
