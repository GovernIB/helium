package es.caib.helium.back.validator;

import es.caib.helium.back.command.ExpedientTipusExportarCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.CampTipusDto;
import es.caib.helium.logic.intf.dto.ConsultaCampDto.TipusConsultaCamp;
import es.caib.helium.logic.intf.dto.ConsultaDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.EnumeracioDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.MapeigSistraDto.TipusMapeig;
import es.caib.helium.logic.intf.exportacio.*;
import es.caib.helium.logic.intf.service.CampService;
import es.caib.helium.logic.intf.service.DefinicioProcesService;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.DocumentService;
import es.caib.helium.logic.intf.service.DominiService;
import es.caib.helium.logic.intf.service.EnumeracioService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import es.caib.helium.logic.intf.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validador per a la comanda d'exportació de dades del tipus d'expedient.
 */
public class ExpedientTipusImportarValidator implements ConstraintValidator<ExpedientTipusImportar, ExpedientTipusExportarCommand> {

	private String codiMissatge;
	
	@Autowired
	ExpedientTipusService expedientTipusService;
	@Autowired
	DissenyService dissenyService;
	@Autowired
	DefinicioProcesService definicioProcesService;
	@Autowired
	EnumeracioService enumeracioService;
	@Autowired
	DominiService dominiService;
	@Autowired 
	CampService campService;
	@Autowired 
	DocumentService documentService;
	@Autowired
	private HttpServletRequest request;
	
	@Override
	public void initialize(ExpedientTipusImportar anotacio) {
		codiMissatge = anotacio.message();
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
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
    		ExpedientTipusDto expedientTipus = null;
    		if (command.getId() != null)
    			expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
    					entornActual.getId(),
    					command.getId());
    		
    		// Conjunt d'enumeracions i dominis del tipus d'expedient per comprovar si les dependències són globals
    		// O no s'han escollit
    		Set<String> enumeracionsGlobals = new HashSet<String>();
    		for (EnumeracioDto e : enumeracioService.findGlobals(entornActual.getId()))
    			enumeracionsGlobals.add(e.getCodi());
    		Set<String> dominisGlobals = new HashSet<String>();
    		for (DominiDto d : dominiService.findGlobals(entornActual.getId()))
    			dominisGlobals.add(d.getCodi());
    		Set<String> enumeracionsTe = new HashSet<String>();
    		Set<String> dominisTe = new HashSet<String>();
    		if (expedientTipus != null) {
	    		for (EnumeracioDto e : expedientTipusService.enumeracioFindAll(expedientTipus.getId(), false))
	    			enumeracionsTe.add(e.getCodi());
	    		for (DominiDto d : expedientTipusService.dominiFindAll(entornActual.getId(), expedientTipus.getId(), false))
	    			dominisTe.add(d.getCodi());
	    		if (expedientTipus.isAmbHerencia()) {
	    			// Enumeracions i dominis heretats
		    		for (EnumeracioDto e : expedientTipusService.enumeracioFindAll(expedientTipus.getExpedientTipusPareId(), false))
		    			enumeracionsTe.add(e.getCodi());
		    		for (DominiDto d : expedientTipusService.dominiFindAll(entornActual.getId(), expedientTipus.getExpedientTipusPareId(), false))
		    			dominisTe.add(d.getCodi());
	    		}
    		}

    		// Si l'expedient destí està configurat amb info propia o la importació té info pròpia
    		// llavors haurà de tenir els camps i 
    		// els documents definits per a les tasques de les definicions de procés.
    		boolean isAmbInfoPropia = (expedientTipus != null && expedientTipus.isAmbInfoPropia()) 
    								|| (exportacio.isAmbInfoPropia());

    		// Guarda la exportació per no haver de desserialitzar un altre cop el fitxer.
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
					// Comprova que no existeixi ja un tipus d'expedient amb el mateix codi
					ExpedientTipusDto repetit = expedientTipusService.findAmbCodiPerValidarRepeticio(
							entornActual.getId(),
							command.getCodi());
					if(repetit != null) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("expedient.tipus.importar.validacio.codi.repetit", new Object[]{command.getCodi()}))
								.addNode("codi")
								.addConstraintViolation();	
						valid = false;
					}
				}
			} else {
				// Si el tipus d'expedient destí és heretable i es vol sobreescriure amb no heretable o no infor pròpia i té
				// tipus fills que hereten llavors no s'ha de deixar posar com a no info pròpia o no heretable
				if (expedientTipus != null 
						&& expedientTipus.isHeretable()
						&& command.isDadesBasiques()
						&& !exportacio.isHeretable()
						) {
					// Comprovem que el tipus d'expedient destí no tingui tipus d'expedient que hereten
					List<ExpedientTipusDto> heretats = expedientTipusService.findHeretats(command.getId());
					if (heretats.size() > 0) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(
										"expedient.tipus.importar.validacio.no.heretable.amb.heretats", 
										new Object[] {heretats.size()}))
								.addNode("file")
								.addConstraintViolation();	
						valid = false;
					}

				}
			}
			
			
			// Expedient tipus pare del qual hereta
			boolean herencia = false;
			Long expedientTipusPareId = null;
			ExpedientTipusDto expedientTipusPare = null;
			if (exportacio.getExpedientTipusPareCodi() != null) {
				expedientTipusPare = expedientTipusService.findAmbCodiPerValidarRepeticio(
						entornActual.getId(), 
						exportacio.getExpedientTipusPareCodi());
				if (expedientTipusPare == null) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("expedient.tipus.importar.validacio.expedient.tipus.pare.no.trobat", new Object[]{exportacio.getExpedientTipusPareCodi()}))
							.addNode("codi")
							.addConstraintViolation();	
					valid = false;
				} else {
					herencia = true;
					expedientTipusPareId = expedientTipusPare.getId();
				}
			}
			// Definició de procés inicial
			if (expedientTipus == null
					&& exportacio.getJbpmProcessDefinitionKey() != null
					&& !herencia
					&& ! command.getDefinicionsProces().contains(exportacio.getJbpmProcessDefinitionKey())) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(
								this.codiMissatge + ".definicio.inicial", 
								new Object[] {exportacio.getJbpmProcessDefinitionKey()}))
				.addNode("definicionsProces")
				.addConstraintViolation();
				valid = false;
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
				} else if (camp.getTipus() == CampTipusDto.ACCIO) {
					// Comprova que la definició de procés també s'exporti
					if (!command.getDefinicionsProces().contains(camp.getDefprocJbpmKey())) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(
										this.codiMissatge + ".variable.accio", 
										new Object[] {camp.getCodi(), camp.getDefprocJbpmKey()}))
						.addNode("variables")
						.addConstraintViolation();
						valid = false;
					}
				}  else if (camp.getTipus() == CampTipusDto.SELECCIO) {
					// Comprova les dependències del camp de tipus seleció
					if (camp.getCodiEnumeracio() != null && !"".equals(camp.getCodiEnumeracio().trim()))
						if (!command.getEnumeracions().contains(camp.getCodiEnumeracio()) || camp.isDependenciaEntorn()) {
							if (camp.isDependenciaEntorn() && !enumeracionsGlobals.contains(camp.getCodiEnumeracio())) {
								// El camp necessita una enumeració global
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												this.codiMissatge + ".variable.seleccio.enumeracio.entorn", 
												new Object[] {camp.getCodi(), camp.getCodiEnumeracio()}))
								.addNode("variables")
								.addConstraintViolation();
								valid = false;
							} else if (!camp.isDependenciaEntorn() 
									&& !command.getEnumeracions().contains(camp.getCodiEnumeracio())
									&& !enumeracionsTe.contains(camp.getCodiEnumeracio())) {
								// El camp necessita una enumeració a nivell de tipus d'expedient
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												this.codiMissatge + ".variable.seleccio.enumeracio.tipexp", 
												new Object[] {camp.getCodi(), camp.getCodiEnumeracio()}))
								.addNode("variables")
								.addConstraintViolation();
								valid = false;
							}
						}
					if (camp.getCodiDomini() != null && !"".equals(camp.getCodiDomini().trim()))
						if (!command.getDominis().contains(camp.getCodiDomini()) || camp.isDependenciaEntorn()) {
							if (camp.isDependenciaEntorn() && !dominisGlobals.contains(camp.getCodiDomini())) {
								// El camp necessita una domini global
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												this.codiMissatge + ".variable.seleccio.domini.entorn", 
												new Object[] {camp.getCodi(), camp.getCodiDomini()}))
								.addNode("variables")
								.addConstraintViolation();
								valid = false;
							} else if (!camp.isDependenciaEntorn() 
									&& !command.getDominis().contains(camp.getCodiDomini())
									&& ! dominisTe.contains(camp.getCodiDomini())) {
								// El camp necessita una domini a nivell de tipus d'expedient
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												this.codiMissatge + ".variable.seleccio.domini.tipexp", 
												new Object[] {camp.getCodi(), camp.getCodiDomini()}))
								.addNode("variables")
								.addConstraintViolation();
								valid = false;
							}
						}
					if (camp.getCodiConsulta() != null)
						if (!command.getConsultes().contains(camp.getCodiConsulta())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.seleccio.consulta", 
											new Object[] {camp.getCodi(), camp.getCodiConsulta()}))
							.addNode("variables")
							.addConstraintViolation();
							valid = false;
						}
				}
			}

			// Integració amb tràmits de Sistra
			if (isAmbInfoPropia && command.isIntegracioSistra()) {
				
				// Comprova que totes les variables o documents mapejats s'exportin
				for (MapeigSistraExportacio mapeig : exportacio.getSistraMapejos())
				{
					// Variables
					if (TipusMapeig.Variable.equals(mapeig.getTipus())) {
						if (!command.getVariables().contains(mapeig.getCodiHelium()))
						{
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".mapeigSistra.variable", 
											new Object[] {mapeig.getCodiHelium(), mapeig.getCodiSistra()}))
							.addNode("integracioSistra")
							.addConstraintViolation();
							valid = false;
						}
						
					// Documents
					}else if (TipusMapeig.Document.equals(mapeig.getTipus())) {
						if (!command.getDocuments().contains(mapeig.getCodiHelium()))
						{
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".mapeigSistra.document", 
											new Object[] {mapeig.getCodiHelium(), mapeig.getCodiSistra()}))
							.addNode("integracioSistra")
							.addConstraintViolation();
							valid = false;
						}
					}
				}
					
			}
			
			// Documents
			Map<String, DocumentExportacio> documentsMap = new HashMap<String, DocumentExportacio>();
			for (DocumentExportacio document : exportacio.getDocuments())
				documentsMap.put(document.getCodi(), document);
			for (String documentCodi : command.getDocuments()) {
				DocumentExportacio document = documentsMap.get(documentCodi);
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
			
			// Definicions de procés
			Map<String, DefinicioProcesExportacio> definicionsMap = new HashMap<String, DefinicioProcesExportacio>();
			for (DefinicioProcesExportacio definicio : exportacio.getDefinicions())
				definicionsMap.put(definicio.getDefinicioProcesDto().getJbpmKey(), definicio);
			DefinicioProcesExportacio definicio;
			// Camps per cada definició de proces per validar camps en les consultes
			// Map<jbpmKey, Set<campCodi>>
			Map<String, Set<String>> campsDefinicionsProces = new HashMap<String, Set<String>>();
			
			for (String definicioProcesJbpmKey : command.getDefinicionsProces()) {
				definicio = definicionsMap.get(definicioProcesJbpmKey);
				
				Set<String> campsDefinicioProces = new HashSet<String>();
				campsDefinicionsProces.put(definicioProcesJbpmKey, campsDefinicioProces);
				// Comprova les dependències de les variables
				for (CampExportacio campExportacio : definicio.getCamps()) {
					campsDefinicioProces.add(campExportacio.getCodi());
					// Consultes
					if (campExportacio.getCodiConsulta() != null
							&& ! command.getConsultes().contains(campExportacio.getCodiConsulta())) {
						ConsultaDto c = expedientTipus != null?
								c = expedientTipusService.consultaFindAmbCodiPerValidarRepeticio(
										expedientTipus.getId(), 
										campExportacio.getCodiConsulta())
								: null;
						if (c == null) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".definicio.variable.consulta", 
											new Object[] {	campExportacio.getCodi(),
															definicioProcesJbpmKey, 
															campExportacio.getCodiConsulta()}))
							.addNode("definicionsProces")
							.addConstraintViolation();
							valid = false;
						}
					}
					// Dominis
					if (campExportacio.getCodiDomini() != null
							&& (! command.getDominis().contains(campExportacio.getCodiDomini())
									||  campExportacio.isDependenciaEntorn())) {
						if (campExportacio.isDependenciaEntorn() && !dominisGlobals.contains(campExportacio.getCodiDomini())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".definicio.variable.domini.entorn", 
											new Object[] {	campExportacio.getCodi(),
															definicioProcesJbpmKey, 
															campExportacio.getCodiDomini()}))
							.addNode("definicionsProces")
							.addConstraintViolation();
							valid = false;
						} else if (! campExportacio.isDependenciaEntorn()
								&& ! command.getDominis().contains(campExportacio.getCodiDomini())
								&& ! dominisTe.contains(campExportacio.getCodiDomini())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".definicio.variable.domini.tipexp", 
											new Object[] {	campExportacio.getCodi(),
															definicioProcesJbpmKey, 
															campExportacio.getCodiDomini()}))
							.addNode("definicionsProces")
							.addConstraintViolation();
							valid = false;
						}
					}
					// Enumeracions
					if (campExportacio.getCodiEnumeracio() != null
							&& (! command.getEnumeracions().contains(campExportacio.getCodiEnumeracio())
									||  campExportacio.isDependenciaEntorn())) {
						if (campExportacio.isDependenciaEntorn() && !enumeracionsGlobals.contains(campExportacio.getCodiEnumeracio())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".definicio.variable.enumeracio.entorn", 
											new Object[] {	campExportacio.getCodi(),
															definicioProcesJbpmKey, 
															campExportacio.getCodiEnumeracio()}))
							.addNode("definicionsProces")
							.addConstraintViolation();
							valid = false;
						} else if (! campExportacio.isDependenciaEntorn() 
								&& ! command.getEnumeracions().contains(campExportacio.getCodiEnumeracio())
								&& ! enumeracionsTe.contains(campExportacio.getCodiEnumeracio())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".definicio.variable.enumeracio.tipexp", 
											new Object[] {	campExportacio.getCodi(),
															campExportacio.getCodiEnumeracio(), 
															campExportacio.getCodiEnumeracio()}))
							.addNode("definicionsProces")
							.addConstraintViolation();
							valid = false;
						}
					}
				}
				// Llistat de documents de la definició de procés
				Set<String> doumentsDefinicioProces = new HashSet<String>();
				for (DocumentExportacio document : definicio.getDocuments())
					doumentsDefinicioProces.add(document.getCodi());
				// Comprova les dependències de les tasques
				boolean campTrobat, documentTrobat;
				for (TascaExportacio tasca : definicio.getTasques()){
					// Camps
					for (CampTascaExportacio campTasca : tasca.getCamps()) {
						if (isAmbInfoPropia) {
							if (campTasca.isTipusExpedient()) {
								// Mira entre les variables exportades
								campTrobat = command.getVariables().contains(campTasca.getCampCodi()) ;
								if (!campTrobat && expedientTipus != null)
									// Mira en els camps del TE destí
									campTrobat = campService.findAmbCodi( // La variable no es troba en el TE destí	
											expedientTipus.getId(), 
											null, 
											campTasca.getCampCodi(), 
											true) != null;
								if (!campTrobat && herencia)
									// Mira entre els camps heretats
									campTrobat = campService.findAmbCodi(
								  			expedientTipusPareId,
								  			null,
								  			campTasca.getCampCodi(), 
								  			herencia) != null;
							} else {
								// Mira entre les variables de la definició de procés
								campTrobat = campsDefinicioProces.contains(campTasca.getCampCodi());
							}
							if (!campTrobat) {
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												this.codiMissatge + ".definicio.variable", 
												new Object[] {	tasca.getJbpmName(),
																definicioProcesJbpmKey, 
																campTasca.getCampCodi()}))
								.addNode("definicionsProces")
								.addConstraintViolation();
								valid = false;
							}
						}
					}
					// Documents
					for (DocumentTascaExportacio documentTasca : tasca.getDocuments()) {
						if (isAmbInfoPropia) {
							if (documentTasca.isTipusExpedient()) {
								// Mira entre els documents exportats
								documentTrobat = command.getDocuments().contains(documentTasca.getDocumentCodi()) ;
								if (!documentTrobat && expedientTipus != null)
									// Mira en els documents del TE destí
									documentTrobat = documentService.findAmbCodi( // La variable no es troba en el TE destí	
											expedientTipus.getId(), 
											null, 
											documentTasca.getDocumentCodi(), 
											true) != null;
								if (!documentTrobat && herencia)
									// Mira entre els camps heretats
									documentTrobat = documentService.findAmbCodi(
								  			expedientTipusPareId,
								  			null,
								  			documentTasca.getDocumentCodi(), 
								  			herencia) != null;
							} else {
								// Mira entre els documents de la definició de procés
								documentTrobat = doumentsDefinicioProces.contains(documentTasca.getDocumentCodi());
							}
							if (!documentTrobat) {
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												this.codiMissatge + ".definicio.document", 
												new Object[] {	tasca.getJbpmName(),
																definicioProcesJbpmKey, 
																documentTasca.getDocumentCodi()}))
								.addNode("definicionsProces")
								.addConstraintViolation();
								valid = false;
							}
						}
					}
					// Signatures
					for (FirmaTascaExportacio firmaTasca : tasca.getFirmes())
						if (isAmbInfoPropia) {
							if (firmaTasca.isTipusExpedient()) {
								// Mira entre els documents exportats
								documentTrobat = command.getDocuments().contains(firmaTasca.getDocumentCodi()) ;
								if (!documentTrobat && expedientTipus != null)
									// Mira en els documents del TE destí
									documentTrobat = documentService.findAmbCodi( // La variable no es troba en el TE destí	
											expedientTipus.getId(), 
											null, 
											firmaTasca.getDocumentCodi(), 
											true) != null;
								if (!documentTrobat && herencia)
									// Mira entre els camps heretats
									documentTrobat = documentService.findAmbCodi(
								  			expedientTipusPareId,
								  			null,
								  			firmaTasca.getDocumentCodi(), 
								  			herencia) != null;
							} else {
								// Mira entre els documents de la definició de procés
								documentTrobat = doumentsDefinicioProces.contains(firmaTasca.getDocumentCodi());								
							}
							if (!documentTrobat) {
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												this.codiMissatge + ".definicio.firma", 
												new Object[] {	tasca.getJbpmName(),
																definicioProcesJbpmKey, 
																firmaTasca.getDocumentCodi()}))
								.addNode("definicionsProces")
								.addConstraintViolation();
								valid = false;
							}
						}
				}
			}	
			
			// Consultes
			if (command.getConsultes().size() > 0) {
				if (expedientTipus != null) {
					// Completa els camps per definició de procés amb les existents en el tipus d'expedient
					// per validar que existeixin en la importació o en el TE destí
					Set<String> campsDefinicioProces;
					// Per totes les definicions de procés darrera versió
					for (DefinicioProcesDto dp : definicioProcesService.findAll(expedientTipus.getEntorn().getId(), expedientTipus.getId(), false)) {
						campsDefinicioProces = campsDefinicionsProces.get(dp.getJbpmKey());
						if (campsDefinicioProces == null) {
							campsDefinicioProces = new HashSet<String>();
							campsDefinicionsProces.put(dp.getJbpmKey(), campsDefinicioProces);
						}
						// Per cada camp
						for (CampDto c : dissenyService.findCampsOrdenatsPerCodi(expedientTipus.getId(), dp.getId(), expedientTipus.getExpedientTipusPareId() != null))
							if (!campsDefinicioProces.contains(c.getCodi()))
								campsDefinicioProces.add(c.getCodi());
					}
				}			
				Map<String, ConsultaExportacio> consultesMap = new HashMap<String, ConsultaExportacio>();
				for (ConsultaExportacio consulta : exportacio.getConsultes())
					consultesMap.put(consulta.getCodi(), consulta);
				ConsultaExportacio consulta;
				Set<ConsultaCampExportacio> campsConsulta;
				for (String consultaCodi : command.getConsultes()) {
					consulta = consultesMap.get(consultaCodi);
					campsConsulta = new HashSet<ConsultaCampExportacio>();
					campsConsulta.addAll(consulta.getCamps());
					// Comprova que tots els seus camps de tipus filtre i paràmetre s'exportin juntament amb les variables del tipus expedient o 
					// amb les variables de les defincions de procés
					for (ConsultaCampExportacio consultaCamp : campsConsulta)
						if (consultaCamp.getTipusConsultaCamp() != TipusConsultaCamp.PARAM
								&& ! consultaCamp.getCampCodi().startsWith(Constants.EXPEDIENT_PREFIX))
							if (consultaCamp.getJbpmKey() != null) {
								// variable lligada a una variable de la definició de procés
								if (!command.getDefinicionsProces().contains(consultaCamp.getJbpmKey())) {
									// Si no està en el command mira si està en una definició de procés existent
									if (!campsDefinicionsProces.containsKey(consultaCamp.getJbpmKey()) // definicions de procés
											|| !campsDefinicionsProces.get(consultaCamp.getJbpmKey()).contains(consultaCamp.getCampCodi())) { // codis de camps per DP
										context.buildConstraintViolationWithTemplate(
												MessageHelper.getInstance().getMessage(
														this.codiMissatge + ".consulta.variable.definicioProces", 
														new Object[] {	consulta.getCodi(), 
																		consultaCamp.getCampCodi(),
																		consultaCamp.getJbpmKey()}))
										.addNode("consultes")
										.addConstraintViolation();
										valid = false;
									}
								}
							} else 
								// variable lligada  al tipus d'expedient
								if (!command.getVariables().contains(consultaCamp.getCampCodi())) {
									context.buildConstraintViolationWithTemplate(
											MessageHelper.getInstance().getMessage(
													this.codiMissatge + ".consulta.variable", 
													new Object[] {	consulta.getCodi(), 
																	consultaCamp.getCampCodi()}))
									.addNode("consultes")
									.addConstraintViolation();
									valid = false;
								}						
				}		
			}		
			
			// Herència
			
			// Comprova les dades associades a tasques heretades
			if (herencia && expedientTipusPare != null) {
				// Consulta totes les definicions de procés del pare
				Map<String, DefinicioProcesDto> definicionsProcesPareMap = new HashMap<String, DefinicioProcesDto>();
				for (DefinicioProcesDto dp : definicioProcesService.findAll(entornActual.getId(), 
																			expedientTipusPareId, 
																			false)) {
					definicionsProcesPareMap.put(dp.getJbpmKey(), dp);
				}
				List<TascaExportacio> tasquesExportacio;
				for (String definicioProcesJbpmkey : exportacio.getHerenciaTasques().keySet()) {
					// Comprova que la definició de procés existeix
					if (!definicionsProcesPareMap.containsKey(definicioProcesJbpmkey)) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(
										this.codiMissatge + ".herencia.tasca.definicio.no.trobada", 
										new Object[] {	definicioProcesJbpmkey}))
						.addNode("tasquesHerencia")
						.addConstraintViolation();
						valid = false;
					}
					tasquesExportacio = exportacio.getHerenciaTasques().get(definicioProcesJbpmkey);
					// Per cada tasca de l'exportació valida que les variables o els documents estiguin al tipus d'expedient
					// del pare
					for (TascaExportacio tascaExportacio : tasquesExportacio) {
						for (CampTascaExportacio campTascaExportacio : tascaExportacio.getCamps()) {
							//TODO: comprovar que es pot afegir la relació amb la variable pertinent
						}
						for (DocumentTascaExportacio documentTascaExportacio : tascaExportacio.getDocuments()) {
							//TODO: comprovar que es pot afegir la relació amb el document pertinent
						}
						for (FirmaTascaExportacio firmaTascaExportacio : tascaExportacio.getFirmes()) {
							//TODO: comprovar que es pot afegir la relació amb el document pertinent
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
