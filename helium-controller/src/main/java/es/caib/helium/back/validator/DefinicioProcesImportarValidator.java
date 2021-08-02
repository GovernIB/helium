package es.caib.helium.back.validator;

import es.caib.helium.back.command.DefinicioProcesExportarCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.CampTipusDto;
import es.caib.helium.logic.intf.dto.ConsultaDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.EnumeracioDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.exportacio.CampExportacio;
import es.caib.helium.logic.intf.exportacio.CampTascaExportacio;
import es.caib.helium.logic.intf.exportacio.DefinicioProcesExportacio;
import es.caib.helium.logic.intf.exportacio.DocumentExportacio;
import es.caib.helium.logic.intf.exportacio.DocumentTascaExportacio;
import es.caib.helium.logic.intf.exportacio.FirmaTascaExportacio;
import es.caib.helium.logic.intf.exportacio.RegistreMembreExportacio;
import es.caib.helium.logic.intf.exportacio.TascaExportacio;
import es.caib.helium.logic.intf.service.CampService;
import es.caib.helium.logic.intf.service.DefinicioProcesService;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.DocumentService;
import es.caib.helium.logic.intf.service.DominiService;
import es.caib.helium.logic.intf.service.EnumeracioService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

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
	EnumeracioService enumeracioService;
	@Autowired
	DominiService dominiService;
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
    		boolean herencia = false;
    		if (command.getExpedientTipusId() != null) {
    			expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
    								entornActual.getId(), 
    								command.getExpedientTipusId());
    			herencia = expedientTipus != null && expedientTipus.getExpedientTipusPareId() != null;
    		}
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
						// Comprova l'enumeració
						EnumeracioDto enumeracio = null;
						if (camp.isDependenciaEntorn()) {
							// Enumeració entorn
							enumeracio = enumeracioService.findAmbCodi(
									entornActual.getId(), 
									null, 
									camp.getCodiEnumeracio());
						} else {
							// TE
							if (expedientTipus != null)
								enumeracio = enumeracioService.findAmbCodi(
										entornActual.getId(),
										expedientTipus.getId(), 
										camp.getCodiEnumeracio());
							if (enumeracio == null && herencia)
								// Mira entre les enumeracions heretades
								enumeracio = enumeracioService.findAmbCodi(
										entornActual.getId(),
										expedientTipus.getExpedientTipusPareId(), 
										camp.getCodiEnumeracio());
						}
						if (enumeracio == null) {
							// enumeracio no trobada
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.seleccio.enumeracio." + (camp.isDependenciaEntorn() ? "entorn" : "tipexp"), 
											new Object[] {camp.getCodi(), camp.getCodiEnumeracio()}))
							.addNode("variables")
							.addConstraintViolation();
							valid = false;							
						}
					}
					if (camp.getCodiConsulta() != null) {
						// Comprova la consulta
						ConsultaDto consulta = null;
						if (expedientTipus != null)
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
						if (camp.isDependenciaEntorn()) {
							// Entorn
							domini = dissenyService.dominiFindAmbCodi(entornActual.getId(), camp.getCodiDomini());
						} else {
							// TE
							if (expedientTipus != null)
								domini = dominiService.findAmbCodi(
										entornActual.getId(),
										expedientTipus.getId(), 
										camp.getCodiDomini());
						}
						if (domini == null) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.seleccio.domini." + (camp.isDependenciaEntorn() ? "entorn" : "tipexp"), 
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
					// Variables de la tasca: poden estar lligades a les del TE o les de la DP
					CampDto campDto;
					for (CampTascaExportacio tascaCamp : tasca.getCamps()) {
						campDto = null;
						// Comprova que el tipus expeident destí la tingui
						if (isAmbInfoPropia) {
							// Comprova que el camp estigui al tipus d'expedient
							campDto = campService.findAmbCodi(
									expedientTipus.getId(),
									null,
									tascaCamp.getCampCodi(),
									herencia);
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
							// Commprova que estigui exportada o en la DP destí
							if (! command.getVariables().contains(tascaCamp.getCampCodi())) {
								// comprova que el camp existeixi en la definició de procés destí
								if (command.getId() != null)
									campDto = campService.findAmbCodi(null, command.getId(), tascaCamp.getCampCodi(), false);
								// com a darrera opció, si no es troba i el TE té infor pròpia la cerca al TE
								if (campDto == null && isAmbInfoPropia)
									campDto = campService.findAmbCodi(
											expedientTipus.getId(),
											null,
											tascaCamp.getCampCodi(),
											herencia);
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
									documentCamp.getDocumentCodi(),
									herencia);
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
											documentCamp.getDocumentCodi(),
											false);
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
									firmaCamp.getDocumentCodi(),
									herencia);
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
											firmaCamp.getDocumentCodi(),
											false);
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