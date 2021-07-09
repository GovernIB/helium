package es.caib.helium.back.validator;

import es.caib.helium.back.command.DefinicioProcesExportarCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.CampTascaDto;
import es.caib.helium.logic.intf.dto.CampTipusDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.DocumentTascaDto;
import es.caib.helium.logic.intf.dto.FirmaTascaDto;
import es.caib.helium.logic.intf.dto.TascaDto;
import es.caib.helium.logic.intf.service.CampService;
import es.caib.helium.logic.intf.service.DefinicioProcesService;
import es.caib.helium.logic.intf.service.DocumentService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Validador per a la comanda d'exportació de dades de la definició de procés.
 * Valida les variables i documents. 
 */
public class DefinicioProcesExportarValidator implements ConstraintValidator<DefinicioProcesExportar, DefinicioProcesExportarCommand>{

	private String codiMissatge;
	@Autowired
	private CampService campService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	protected DefinicioProcesService definicioProcesService;
	@Autowired
	protected ExpedientTipusService expedientTipusService;
	
	@Override
	public void initialize(DefinicioProcesExportar anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(DefinicioProcesExportarCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		
		if (command.getId() != null) {
			// Variables
			Map<String, CampDto> campsMap = new HashMap<String, CampDto>();
			for (CampDto camp : campService.findAllOrdenatsPerCodi(null, command.getId()))
				campsMap.put(camp.getCodi(), camp);
			CampDto camp;
			for (String campCodi : command.getVariables()) {
				camp = campsMap.get(campCodi);
				// Comprova que la agrupació s'hagi exportat
				if (camp.getAgrupacio() != null
						&& ! command.getAgrupacions().contains(camp.getAgrupacio().getCodi())) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage(
									this.codiMissatge + ".variable.agrupacio", 
									new Object[] {camp.getCodi(), camp.getAgrupacio().getCodi()}))
					.addNode("variables")
					.addConstraintViolation();
					valid = false;
				}					
				if (camp.getTipus() == CampTipusDto.REGISTRE) {
					// Comprova que les variables de tipus registre exportades tinguin les seves variables exportables.
					for (CampDto membre : campService.registreFindMembresAmbRegistreId(camp.getId()))
						if (!command.getVariables().contains(membre.getCodi())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.registre", 
											new Object[] {camp.getCodi(), membre.getCodi()}))
							.addNode("variables")
							.addConstraintViolation();
							valid = false;
						}
				}
			}				
			// Documents
			Map<String, DocumentDto> documentsMap = new HashMap<String, DocumentDto>();
			for (DocumentDto document : documentService.findAll(null, command.getId()))
				documentsMap.put(document.getCodi(), document);
			DocumentDto document;
			for (String documentCodi : command.getDocuments()) {
				document = documentsMap.get(documentCodi);
				if (document.getCampData() != null
					&& !command.getVariables().contains(document.getCampData().getCodi())) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage(
									this.codiMissatge + ".document.variable", 
									new Object[] {	document.getCodi(), 
											document.getCampData().getCodi()}))
					.addNode("documents")
					.addConstraintViolation();
					valid = false;
				}
			}	
			// Tasques
			for (TascaDto tasca : definicioProcesService.tascaFindAll(command.getId())) {
				if (command.getTasques().contains(tasca.getJbpmName())) {
					// variables de la tasca
					for (CampTascaDto campTasca : tasca.getCamps())
						if (campTasca.getCamp().getExpedientTipus() == null
								&& ! command.getVariables().contains(campTasca.getCamp().getCodi())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".tasca.variable", 
											new Object[] {	tasca.getJbpmName(), 
													campTasca.getCamp().getCodi()}))
							.addNode("tasques")
							.addConstraintViolation();
							valid = false;
						}
					// documents de la tasca
					for (DocumentTascaDto documentTasca : tasca.getDocuments())
						if (documentTasca.getDocument().getExpedientTipus() == null
								&& ! command.getDocuments().contains(documentTasca.getDocument().getCodi())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".tasca.document", 
											new Object[] {	tasca.getJbpmName(), 
													documentTasca.getDocument().getCodi()}))
							.addNode("tasques")
							.addConstraintViolation();
							valid = false;
						}
					// firmes de la tasca
					for (FirmaTascaDto firma : tasca.getFirmes())
						if (firma.getDocument().getExpedientTipus() == null
								&& ! command.getDocuments().contains(firma.getDocument().getCodi())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".tasca.firma", 
											new Object[] {	tasca.getJbpmName(), 
													firma.getDocument().getCodi()}))
							.addNode("tasques")
							.addConstraintViolation();
							valid = false;
						}
				}
			}
		} else {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(this.codiMissatge + ".definicioProcesIdNull", null));
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}
}
