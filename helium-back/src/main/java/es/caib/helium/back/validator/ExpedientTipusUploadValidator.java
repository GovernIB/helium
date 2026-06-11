package es.caib.helium.back.validator;

import java.io.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.back.command.ExpedientTipusExportarCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.ExpedientTipusDto;
import es.caib.helium.commons.exportacio.ExpedientTipusExportacio;
import es.caib.helium.logic.intf.service.ExpedientTipusService;

/**
 * Validador per a la càrrega d'un fitxer d'exportació
 * durant la importació de dades del tipus d'expedient.
 */
public class ExpedientTipusUploadValidator implements ConstraintValidator<ExpedientTipusUpload, ExpedientTipusExportarCommand>{

	private static final String OLD_PREFIX1 = "net.conselldemallorca.helium.v3.core.api.exportacio.";
	private static final String NEW_PREFIX1 = "es.caib.helium.commons.exportacio.";
	private static final String OLD_PREFIX2 = "net.conselldemallorca.helium.v3.core.api.dto.";
	private static final String NEW_PREFIX2 = "es.caib.helium.commons.dto.";

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
			Object deserialitzat = deserializeExpedientTipusExportacio(command.getFile().getBytes());
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

	public static Object deserializeExpedientTipusExportacio(byte[] bytes) throws IOException, ClassNotFoundException {
		InputStream is = new ByteArrayInputStream(bytes);
		ObjectInputStream input = new ObjectInputStream(is) {
			@Override
			protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
				String className = desc.getName();
				if (className.startsWith(OLD_PREFIX1)) {
					String newClassName = NEW_PREFIX1 +
						className.substring(OLD_PREFIX1.length());
					return Class.forName(newClassName);
				} else if (className.startsWith(OLD_PREFIX2)) {
					String newClassName = NEW_PREFIX2 +
						className.substring(OLD_PREFIX2.length());
					return Class.forName(newClassName);
				}
				return super.resolveClass(desc);
			}
		};
		return input.readObject();
	}

}
