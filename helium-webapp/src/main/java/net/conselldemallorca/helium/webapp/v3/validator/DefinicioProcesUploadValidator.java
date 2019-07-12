package net.conselldemallorca.helium.webapp.v3.validator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesExportarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per a la càrrega d'un fitxer d'exportació
 * durant la importació de dades del tipus d'expedient.
 */
public class DefinicioProcesUploadValidator implements ConstraintValidator<DefinicioProcesUpload, DefinicioProcesExportarCommand>{
	
	@Autowired
	DefinicioProcesService definicioProcesService;
	
	@Override
	public void initialize(DefinicioProcesUpload anotacio) {
	}

	@Override
	public boolean isValid(DefinicioProcesExportarCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		
		// Recupera l'exportació
		DefinicioProcesExportacio exportacio = null;
	 	try {
			if (command.getFile().getBytes() == null || command.getFile().getBytes().length == 0) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage( "definicio.proces.importar.form.error.arxiu.buit"))
				.addNode("file")
				.addConstraintViolation();
				valid = false;
			}
			InputStream is = new ByteArrayInputStream(command.getFile().getBytes());
	    	ObjectInputStream input = new ObjectInputStream(is);
	    	Object deserialitzat = input.readObject();
	    	if (deserialitzat instanceof DefinicioProcesExportacio) {
	    		exportacio = (DefinicioProcesExportacio) deserialitzat;
	    	} else {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage( "definicio.proces.importar.form.error.arxiu.erroni"))
				.addNode("file")
				.addConstraintViolation();
				valid = false;
	    	}
		} catch (IOException ex) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage( "definicio.proces.importar.form.error.lectura"))
			.addNode("file")
			.addConstraintViolation();
			valid = false;

		} catch (ClassNotFoundException ex) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage( "definicio.proces.importar.form.error.lectura"))
			.addNode("file")
			.addConstraintViolation();
			valid = false;
		} catch (Exception ex) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage( "definicio.proces.importar.form.error.importacio", new Object[] {ex.getMessage()}))
			.addNode("file")
			.addConstraintViolation();
			valid = false;
		}	
		if ( exportacio != null)
		{	
			// Guarda la exportació per no haver de desserialitzar un altre cop el fitxer.
			command.setCodi(exportacio.getDefinicioProcesDto().getJbpmKey());
			command.setExportacio(exportacio);
			
//			if (command.getId() == null) {
//
//				// Comprova que no existeixi ja una definició de procés amb el mateix codi en un expedient diferent o a l'entorn
//	    		// si es vol publicar des d'un expedient o l'entorn
//	    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
//				DefinicioProcesDto repetit = definicioProcesService.findByEntornIdAndJbpmKey(
//						entornActual.getId(),
//						exportacio.getDefinicioProcesDto().getJbpmKey());
//				if (repetit != null && command.getId() == null)	
//					if (command.getExpedientTipusId() == null ) {
//						// desplegament dins l'entorn
//						if (repetit.getExpedientTipus() != null) {
//							// ja està en un altre tipus d'expedient
//							context.buildConstraintViolationWithTemplate(
//									MessageHelper.getInstance().getMessage(
//											"definicio.proces.importar.validacio.codi.repetit.tipusExpedient", 
//											new Object[]{
//													exportacio.getDefinicioProcesDto().getJbpmKey(),
//													repetit.getExpedientTipus().getCodi()}))
//									.addNode("codi")
//									.addConstraintViolation();	
//							valid = false;
//						}
//					} else {
//						// desplegament dins el tipus d'expedient
//						if (repetit.getExpedientTipus() != null) { 
//							if(! repetit.getExpedientTipus().getId().equals(command.getExpedientTipusId())) {
//								// ja està en un altre tipus d'expedient
//								context.buildConstraintViolationWithTemplate(
//										MessageHelper.getInstance().getMessage(
//												"definicio.proces.importar.validacio.codi.repetit.tipusExpedient", 
//												new Object[]{
//														exportacio.getDefinicioProcesDto().getJbpmKey(),
//														repetit.getExpedientTipus().getCodi()}))
//										.addNode("codi")
//										.addConstraintViolation();	
//								valid = false;
//							}
//						} else {
//							// ja està a l'entorn
//							context.buildConstraintViolationWithTemplate(
//									MessageHelper.getInstance().getMessage(
//											"definicio.proces.importar.validacio.codi.repetit.entorn", 
//											new Object[]{
//													exportacio.getDefinicioProcesDto().getJbpmKey(),
//													repetit.getEntorn().getCodi()}))
//									.addNode("codi")
//									.addConstraintViolation();	
//							valid = false;
//						}
//					}
//			}
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}

}
