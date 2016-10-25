package net.conselldemallorca.helium.webapp.v3.validator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.exportacio.CampExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.CampTascaExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DocumentExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DocumentTascaExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.FirmaTascaExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.RegistreMembreExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.TascaExportacio;
import net.conselldemallorca.helium.v3.core.api.service.CampService;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesExportarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Validador per a la comanda d'exportació de dades de la definició de procés.
 * Valida:
 * - Que el fitxer es correspongui amb una exportació de definició de procés.
 * - Quan s'importa a:
 * 	- Entorn. No pot existir a l'entorn ni en cap tipus d'expedient
 * 	- Expedient. No pot existir a l'entorn ni en cap altra tipus d'expedient.
 * - Variables:
 *  	- Per les variables de tipus registre s'han d'importar els seus camps
 *  	- Per les variables de tipus selecció:
 * 		- Enumeració. Ha d'existir al tipus d'expedient o a l'entorn.
 * 		- Consulta. Ha d'existir al tipus d'expedient.
 * - Documents:
 * 	- Les variables tipus data han d'estar seleccionades
 * - Tasques:
 * 	- Les variables, documents i documents de les firmes han d'estar en el tipus
 * 	d'expedient destí si aquest està configurat com a ambDadesPropies o han d'estar
 * 	a la definició de procés destí o a la pròpia exportacio si no es fa el desplegament
 * 	sobre un tipus d'expedient amb dades propies.
 */
public class DefinicioProcesImportarValidator implements ConstraintValidator<DefinicioProcesImportar, DefinicioProcesExportarCommand>{

	private String codiMissatge;
	
	@Autowired
	ExpedientTipusService expedientTipusService;
	@Autowired
	DefinicioProcesService definicioProcesService;
	@Autowired
	CampService campService;
	@Autowired
	DocumentService documentService;
	@Autowired
	DissenyService dissenyService;
	@Autowired
	private HttpServletRequest request;
	
	@Override
	public void initialize(DefinicioProcesImportar anotacio) {
		codiMissatge = anotacio.message();
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
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
    		DefinicioProcesDto definicioProces = null;
    		if (command.getId() != null) {
    			definicioProces = definicioProcesService.findById(command.getId());
    			if (definicioProces.getExpedientTipus() != null)
    				command.setExpedientTipusId(definicioProces.getExpedientTipus().getId());
    		}
    		ExpedientTipusDto expedientTipus = null;
    		if (command.getExpedientTipusId() != null)
    			expedientTipus = expedientTipusService.findAmbIdPermisConsultar(
    								entornActual.getId(), 
    								command.getExpedientTipusId());
    		// Guarda la importació per no haver de desserialitzar un altre cop el fitxer.
			command.setExportacio(exportacio);
			
			if (command.getId() == null) {
				// comprova que no estigui buit
				if (command.getCodi() == null || "".equals(command.getCodi())) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("NotEmpty"))
							.addNode("codi")
							.addConstraintViolation();	
					valid = false;
				} else {
					// Comprova que no existeixi ja una definició de procés amb el mateix codi en un expedient diferent o a l'entorn
		    		// si es vol publicar des d'un expedient o l'entorn
					DefinicioProcesDto repetit = definicioProcesService.findByEntornIdAndJbpmKey(
							entornActual.getId(),
							exportacio.getDefinicioProcesDto().getJbpmKey());
					if (repetit != null && command.getId() == null)	
						if (command.getExpedientTipusId() == null ) {
							// desplegament dins l'entorn
							if (repetit.getExpedientTipus() != null) {
								// ja està en un altre tipus d'expedient
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												"definicio.proces.importar.validacio.codi.repetit.tipusExpedient", 
												new Object[]{
														exportacio.getDefinicioProcesDto().getJbpmKey(),
														repetit.getExpedientTipus().getCodi()}))
										.addNode("codi")
										.addConstraintViolation();	
								valid = false;
							}
						} else {
							// desplegament dins el tipus d'expedient
							if (repetit.getExpedientTipus() != null) { 
								if(! repetit.getExpedientTipus().getId().equals(command.getExpedientTipusId())) {
									// ja està en un altre tipus d'expedient
									context.buildConstraintViolationWithTemplate(
											MessageHelper.getInstance().getMessage(
													"definicio.proces.importar.validacio.codi.repetit.tipusExpedient", 
													new Object[]{
															exportacio.getDefinicioProcesDto().getJbpmKey(),
															repetit.getExpedientTipus().getCodi()}))
											.addNode("codi")
											.addConstraintViolation();	
									valid = false;
								}
							} else {
								// ja està a l'entorn
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												"definicio.proces.importar.validacio.codi.repetit.entorn", 
												new Object[]{
														exportacio.getDefinicioProcesDto().getJbpmKey(),
														repetit.getEntorn().getCodi()}))
										.addNode("codi")
										.addConstraintViolation();	
								valid = false;
							}
						}
				}
			}			
			// Variables
			Map<String, CampExportacio> campsMap = new HashMap<String, CampExportacio>();
			for (CampExportacio camp : exportacio.getCamps())
				campsMap.put(camp.getCodi(), camp);
			CampExportacio camp;
			for (String campCodi : command.getVariables()) {
				camp = campsMap.get(campCodi);
				if (camp.getTipus() == CampTipusDto.REGISTRE) {
					// Comprova que les variables de tipus registre exportades tinguin les seves variables exportables.
					for (RegistreMembreExportacio membre : camp.getRegistreMembres())
						if (!command.getVariables().contains(membre.getCodi())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.registre", 
											new Object[] {camp.getCodi(), membre.getCodi()}))
							.addNode("variables")
							.addConstraintViolation();
							valid = false;
						}
				} else if (camp.getTipus() == CampTipusDto.SELECCIO) {
					// Comprova les dependències del camp de tipus seleció
					if (camp.getCodiEnumeracio() != null && !"".equals(camp.getCodiEnumeracio().trim())) {
						// Comprova la enumeració
						EnumeracioDto enumeracio = null;
						if (expedientTipus != null)
							// Busca primer dins del tipus d'expedient
							enumeracio = expedientTipusService.enumeracioFindAmbCodi(expedientTipus.getId(), camp.getCodiEnumeracio());
						if (enumeracio == null)
							// Si no el troba busca a l'entorn
							enumeracio = dissenyService.enumeracioFindAmbCodi(entornActual.getId(), camp.getCodiEnumeracio());
						if (enumeracio == null) {
							// enumeracio no trobada
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.seleccio.enumeracio", 
											new Object[] {camp.getCodi(), camp.getCodiEnumeracio()}))
							.addNode("variables")
							.addConstraintViolation();
							valid = false;							
						}
					}
					if (camp.getCodiConsulta() != null) {
						// Comprova la consulta
						ConsultaDto consulta = null;
						for (ConsultaDto c : expedientTipus.getConsultes())
							if (c.getCodi().equals(camp.getCodiConsulta())) {
								consulta = c;
								break;
							}
						if (consulta == null) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.seleccio.consulta", 
											new Object[] {camp.getCodi(), camp.getCodiConsulta()}))
							.addNode("variables")
							.addConstraintViolation();
							valid = false;
						}
					}
					if (camp.getCodiDomini() != null) {
						// Comprova el domini
						DominiDto domini = null;
						if (expedientTipus != null)
							// Busca primer dins del tipus d'expedient
							domini = expedientTipusService.dominiFindAmbCodi(expedientTipus.getId(), camp.getCodiDomini());
						if (domini == null)
							// Si no el troba busca a l'entorn
							domini = dissenyService.dominiFindAmbCodi(entornActual.getId(), camp.getCodiDomini());
						if (domini == null) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.seleccio.domini", 
											new Object[] {camp.getCodi(), camp.getCodiDomini()}))
							.addNode("variables")
							.addConstraintViolation();
							valid = false;
						}
					}
				}
			}	
			// Documents
			if (command.getDocuments().size() > 0) {
				Map<String, DocumentExportacio> documentsMap = new HashMap<String, DocumentExportacio>();
				for (DocumentExportacio document : exportacio.getDocuments())
					documentsMap.put(document.getCodi(), document);
				DocumentExportacio document;
				for (String documentCodi : command.getDocuments()) {
					document = documentsMap.get(documentCodi);
					if (document.getCodiCampData() != null
						&& !command.getVariables().contains(document.getCodiCampData())) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(
										this.codiMissatge + ".document.variable", 
										new Object[] {	document.getCodi(), 
												document.getCodiCampData()}))
						.addNode("documents")
						.addConstraintViolation();
						valid = false;
					}
				}		
			}
			// Tasques
			if (command.getTasques().size() > 0) {
				boolean isAmbInfoPropia = 
						expedientTipus != null && expedientTipus.isAmbInfoPropia();
				Map<String, TascaExportacio> tasquesMap = new HashMap<String, TascaExportacio>();
				for (TascaExportacio tasca : exportacio.getTasques())
					tasquesMap.put(tasca.getJbpmName(), tasca);
				TascaExportacio tasca;
				for (String tascaJbpmName : command.getTasques()) {
					tasca = tasquesMap.get(tascaJbpmName);
					// Variables de la tasca
					for (CampTascaExportacio tascaCamp : tasca.getCamps()) {
						CampDto campDto = null;
						if (isAmbInfoPropia) {
							// Comprova que el camp estigui al tipus d'expedient
							campDto = campService.findAmbCodi(
									expedientTipus.getId(),
									null,
									tascaCamp.getCampCodi());
							if (campDto == null) {
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												this.codiMissatge + ".tasca.variable.expedientTipus", 
												new Object[] {	tasca.getJbpmName(), 
														tascaCamp.getCampCodi()}))
								.addNode("tasques")
								.addConstraintViolation();
								valid = false;
							}
						} else {
							// comprova que estigui exportat o que existeixi en la def proc destí
							if (! command.getVariables().contains(tascaCamp.getCampCodi())) {
								// comprova que el camp existeixi en la definició de procés destí
								if (command.getId() != null)
									campDto = campService.findAmbCodi(null, command.getId(), tascaCamp.getCampCodi());
								if (campDto == null) {
									context.buildConstraintViolationWithTemplate(
											MessageHelper.getInstance().getMessage(
													this.codiMissatge + ".tasca.variable", 
													new Object[] {	tasca.getJbpmName(), 
															tascaCamp.getCampCodi()}))
									.addNode("tasques")
									.addConstraintViolation();
									valid = false;
								}
							}
						}
					}
					// Documents de la tasca
					for (DocumentTascaExportacio documentCamp : tasca.getDocuments()) {
						if (isAmbInfoPropia) {
							// Comprova que el camp estigui al tipus d'expedient
							DocumentDto document =documentService.findAmbCodi(
									expedientTipus.getId(), 
									null,
									documentCamp.getDocumentCodi());
							if (document == null) {
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												this.codiMissatge + ".tasca.document.expedientTipus", 
												new Object[] {	tasca.getJbpmName(), 
														documentCamp.getDocumentCodi()}))
								.addNode("tasques")
								.addConstraintViolation();
								valid = false;
							}
						} else {
							// comprova que estigui exportat o que existeixi en la def proc destí
							if (! command.getDocuments().contains(documentCamp.getDocumentCodi())) {
								// comprova que el camp existeixi en la definició de procés destí
								DocumentDto document = null;
								if (command.getId() != null)
									document = documentService.findAmbCodi(
											null,
											command.getId(), 
											documentCamp.getDocumentCodi());
								if (document == null) {
									context.buildConstraintViolationWithTemplate(
											MessageHelper.getInstance().getMessage(
													this.codiMissatge + ".tasca.document", 
													new Object[] {	tasca.getJbpmName(), 
															documentCamp.getDocumentCodi()}))
									.addNode("tasques")
									.addConstraintViolation();
									valid = false;
								}
							}
						}
					}
					// Firmes de la tasca
					for (FirmaTascaExportacio firmaCamp : tasca.getFirmes()) {
						if (isAmbInfoPropia) {
							// Comprova que el camp estigui al tipus d'expedient
							DocumentDto document =documentService.findAmbCodi(
									expedientTipus.getId(), 
									null,
									firmaCamp.getDocumentCodi());
							if (document == null) {
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												this.codiMissatge + ".tasca.firma.expedientTipus", 
												new Object[] {	tasca.getJbpmName(), 
														firmaCamp.getDocumentCodi()}))
								.addNode("tasques")
								.addConstraintViolation();
								valid = false;
							}
						} else {
							// comprova que estigui exportat o que existeixi en la def proc destí
							if (! command.getDocuments().contains(firmaCamp.getDocumentCodi())) {
								// comprova que el camp existeixi en la definició de procés destí
								DocumentDto document = null;
								if (command.getId() != null)
									document = documentService.findAmbCodi(
											null,
											command.getId(), 
											firmaCamp.getDocumentCodi());
								if (document == null) {
									context.buildConstraintViolationWithTemplate(
											MessageHelper.getInstance().getMessage(
													this.codiMissatge + ".tasca.firma", 
													new Object[] {	tasca.getJbpmName(), 
															firmaCamp.getDocumentCodi()}))
									.addNode("tasques")
									.addConstraintViolation();
									valid = false;
								}
							}
						}
					}
				}
			}
		} 
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}

}
