package net.conselldemallorca.helium.webapp.v3.validator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto.TipusConsultaCamp;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.v3.core.api.exportacio.CampExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.CampTascaExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ConsultaCampExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ConsultaExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DocumentExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DocumentTascaExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ExpedientTipusExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.FirmaTascaExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.MapeigSistraExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.RegistreMembreExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.TascaExportacio;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusExportarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Validador per a la comanda d'exportació de dades del tipus d'expedient.
 */
public class ExpedientTipusImportarValidator implements ConstraintValidator<ExpedientTipusImportar, ExpedientTipusExportarCommand>{

	private String codiMissatge;
	
	@Autowired
	ExpedientTipusService expedientTipusService;
	@Autowired
	DefinicioProcesService definicioProcesService;
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
						if (!command.getEnumeracions().contains(camp.getCodiEnumeracio())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.seleccio.enumeracio", 
											new Object[] {camp.getCodi(), camp.getCodiEnumeracio()}))
							.addNode("variables")
							.addConstraintViolation();
							valid = false;
						}
					if (camp.getCodiDomini() != null)
						if (!command.getDominis().contains(camp.getCodiDomini())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.seleccio.domini", 
											new Object[] {camp.getCodi(), camp.getCodiDomini()}))
							.addNode("variables")
							.addConstraintViolation();
							valid = false;
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
			if (command.isIntegracioSistra()) {
				// Comprova que totes les variables mapejades s'exportin
				for (MapeigSistraExportacio mapeig : exportacio.getSistraMapejos())
					if (mapeig.getTipus() != TipusMapeig.Adjunt 
						&& !command.getVariables().contains(mapeig.getCodiHelium())) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(
										this.codiMissatge + ".mapeigSistra", 
										new Object[] {mapeig.getCodiHelium(), mapeig.getCodiSistra()}))
						.addNode("integracioSistra")
						.addConstraintViolation();
						valid = false;
					}
			}
			
			// Documents
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

			// Consultes
			Map<String, ConsultaExportacio> consultesMap = new HashMap<String, ConsultaExportacio>();
			for (ConsultaExportacio consulta : exportacio.getConsultes())
				consultesMap.put(consulta.getCodi(), consulta);
			ConsultaExportacio consulta;
			Set<ConsultaCampExportacio> campsConsulta;
			for (String consultaCodi : command.getConsultes()) {
				consulta = consultesMap.get(consultaCodi);
				campsConsulta = new HashSet<ConsultaCampExportacio>();
				campsConsulta.addAll(consulta.getCamps());
				// Comprova que tots els seus camps de tipus filtre i paràmetre s'exportin
				for (ConsultaCampExportacio consultaCamp : campsConsulta)
					if (consultaCamp.getTipusConsultaCamp() != TipusConsultaCamp.PARAM
							&& !command.getVariables().contains(consultaCamp.getCampCodi())) {
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
			
			// Definicions de procés
			Map<String, DefinicioProcesExportacio> definicionsMap = new HashMap<String, DefinicioProcesExportacio>();
			for (DefinicioProcesExportacio definicio : exportacio.getDefinicions())
				definicionsMap.put(definicio.getDefinicioProcesDto().getJbpmKey(), definicio);
			DefinicioProcesExportacio definicio;
			for (String definicioProcesJbpmKey : command.getDefinicionsProces()) {
				definicio = definicionsMap.get(definicioProcesJbpmKey);
				
				// Comprova que no existeixi ja una definició de procés per a un altre tipus d'expedient diferent o pera l'entorn
				DefinicioProcesDto definicioProcesDto = 
						definicioProcesService.findByEntornIdAndJbpmKey(
									entornActual.getId(), 
									definicioProcesJbpmKey); 
				if (definicioProcesDto != null)
					if ((definicioProcesDto.getExpedientTipus() != null // definició de procés lligada a un expedient
						&&  (command.getId() == null 				// es vol importar a un nou tipus d'expedient
							 || !definicioProcesDto.getExpedientTipus().getId().equals(command.getId()))) // es vol importar a un expedient diferent
						) 
					{	
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(
										"expedient.tipus.exportar.validacio.definicio.desplegada.tipus.expedient", 
										new Object[]{
												definicioProcesJbpmKey,
												definicioProcesDto.getExpedientTipus().getCodi(),
												definicioProcesDto.getExpedientTipus().getNom()}))
						.addNode("definicionsProces")
						.addConstraintViolation();
						valid = false;						
					} 
					else if (definicioProcesDto.getExpedientTipus() == null && command.getId() != null) // es vol importar una definició de procés de l'entorn
					{ 
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(
										"expedient.tipus.exportar.validacio.definicio.desplegada.entorn", 
										new Object[]{
												definicioProcesJbpmKey}))
						.addNode("definicionsProces")
						.addConstraintViolation();
						valid = false;						
					}
				
				// Comprova les dependències de les tasques
				for (TascaExportacio tasca : definicio.getTasques()){
					// Camps
					for (CampTascaExportacio campTasca : tasca.getCamps())
						if (! command.getVariables().contains(campTasca.getCampCodi())) {
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
					// Documents
					for (DocumentTascaExportacio documentTasca : tasca.getDocuments())
						if (! command.getDocuments().contains(documentTasca.getDocumentCodi())) {
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
					// Signatures
					for (FirmaTascaExportacio firmaTasca : tasca.getFirmes())
						if (! command.getDocuments().contains(firmaTasca.getDocumentCodi())) {
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
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}

}
