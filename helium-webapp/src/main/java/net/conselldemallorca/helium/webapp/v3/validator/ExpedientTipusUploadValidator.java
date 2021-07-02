package net.conselldemallorca.helium.webapp.v3.validator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.emiserv.logic.intf.exportacio.ExpedientTipusExportacio;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusExportarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Validador per a la càrrega d'un fitxer d'exportació
 * durant la importació de dades del tipus d'expedient.
 */
public class ExpedientTipusUploadValidator implements ConstraintValidator<ExpedientTipusUpload, ExpedientTipusExportarCommand>{
	
	@Autowired
	ExpedientTipusService expedientTipusService;
	@Autowired
	private HttpServletRequest request;
	
	@Override
	public void initialize(ExpedientTipusUpload anotacio) {
	}

	@Override
	public boolean isValid(ExpedientTipusExportarCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		
		// Recupera l'exportació
		ExpedientTipusExportacio exportacio = null;
	 	try {
			if (command.getFile().getBytes() == null || command.getFile().getBytes().length == 0) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage( "expedient.tipus.importar.form.error.arxiu.buit"))
				.addNode("file")
				.addConstraintViolation();
				valid = false;
			}
			InputStream is = new ByteArrayInputStream(command.getFile().getBytes());
	    	ObjectInputStream input = new ObjectInputStream(is);
	    	Object deserialitzat = input.readObject();
	    	if (deserialitzat instanceof ExpedientTipusExportacio) {
	    		exportacio = (ExpedientTipusExportacio) deserialitzat;
	    	} else {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage( "expedient.tipus.importar.form.error.arxiu.erroni"))
				.addNode("file")
				.addConstraintViolation();
				valid = false;
	    	}
		} catch (IOException ex) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage( "expedient.tipus.importar.form.error.lectura"))
			.addNode("file")
			.addConstraintViolation();
			valid = false;

		} catch (ClassNotFoundException ex) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage( "expedient.tipus.importar.form.error.lectura"))
			.addNode("file")
			.addConstraintViolation();
			valid = false;
		} catch (Exception ex) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage( "expedient.tipus.importar.form.error.importacio", new Object[] {ex.getMessage()}))
			.addNode("file")
			.addConstraintViolation();
			valid = false;
		}	
		if ( exportacio != null)
		{	
			// Guarda la exportació per no haver de desserialitzar un altre cop el fitxer.
			command.setCodi(exportacio.getCodi());
			command.setExportacio(exportacio);
			
			if (command.getId() == null) {
	    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
				// Comprova que no existeixi ja un tipus d'expedient amb el mateix codi
				ExpedientTipusDto repetit = expedientTipusService.findAmbCodiPerValidarRepeticio(
						entornActual.getId(),
						exportacio.getCodi());
				if(repetit != null) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("expedient.tipus.importar.validacio.codi.repetit", new Object[]{exportacio.getCodi()}))
							.addNode("codi")
							.addConstraintViolation();	
					valid = false;
				}
			}
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}

}
