package net.conselldemallorca.helium.webapp.v3.validator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto.TipusConsultaCamp;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDto;
import net.conselldemallorca.helium.v3.core.api.service.CampService;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.DominiService;
import net.conselldemallorca.helium.v3.core.api.service.EnumeracioService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusExportarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per a la comanda d'exportació de dades del tipus d'expedient.
 */
public class ExpedientTipusExportarValidator implements ConstraintValidator<ExpedientTipusExportar, ExpedientTipusExportarCommand>{

	private String codiMissatge;
	@Autowired
	private ExpedientTipusService expedientTipusService;
	@Autowired
	protected DefinicioProcesService definicioProcesService;
	@Autowired
	protected CampService campService;
	@Autowired
	DocumentService documentService;
	@Autowired
	private EnumeracioService enumeracioService;
	@Autowired
	private DominiService dominiService;
	
	@Override
	public void initialize(ExpedientTipusExportar anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusExportarCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		
		if (command.getId() != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisConsultar(
					command.getEntornId(), 
					command.getId());
			
			// Conjunt d'enumeracions i dominis del tipus d'expedient per comprovar si les dependències són globals
			// O no s'han escollit
			Set<String> enumeracionsGlobals = new HashSet<String>();
			for (EnumeracioDto e : enumeracioService.findGlobals(command.getEntornId()))
				enumeracionsGlobals.add(e.getCodi());
			Set<String> dominisGlobals = new HashSet<String>();
			for (DominiDto d : dominiService.findGlobals(expedientTipus.getEntorn().getId()))
				dominisGlobals.add(d.getCodi());
			
			// Definició de procés inicial
			if (expedientTipus.getJbpmProcessDefinitionKey() != null
					&& !command.getDefinicionsProces().contains(expedientTipus.getJbpmProcessDefinitionKey())) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(
								this.codiMissatge + ".definicio.inicial", 
								new Object[] {expedientTipus.getJbpmProcessDefinitionKey()}))
				.addNode("definicionsProces")
				.addConstraintViolation();
				valid = false;
			}
			
			// Variables
			Map<String, CampDto> campsMap = new HashMap<String, CampDto>();
			for (CampDto camp : campService.findAllOrdenatsPerCodi(command.getId(), null))
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
					if (camp.getEnumeracio() != null)
						if (!command.getEnumeracions().contains(camp.getEnumeracio().getCodi())
								&& !enumeracionsGlobals.contains(camp.getEnumeracio().getCodi()) ) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.seleccio.enumeracio", 
											new Object[] {camp.getCodi(), camp.getEnumeracio().getCodi()}))
							.addNode("variables")
							.addConstraintViolation();
							valid = false;
						}
					if (camp.getDomini() != null)
						if (!command.getDominis().contains(camp.getDomini().getCodi())
								&& !dominisGlobals.contains(camp.getDomini().getCodi())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.seleccio.domini", 
											new Object[] {camp.getCodi(), camp.getDomini().getCodi()}))
							.addNode("variables")
							.addConstraintViolation();
							valid = false;
						}
					if (camp.getConsulta() != null)
						if (!command.getConsultes().contains(camp.getConsulta().getCodi())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.seleccio.consulta", 
											new Object[] {camp.getCodi(), camp.getConsulta().getCodi()}))
							.addNode("variables")
							.addConstraintViolation();
							valid = false;
						}
				}
			}
			
			// Documents
			Map<String, DocumentDto> documentsMap = new HashMap<String, DocumentDto>();
			for (DocumentDto document : documentService.findAll(command.getId(), null))
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

			// Consultes
			Map<String, ConsultaDto> consultesMap = new HashMap<String, ConsultaDto>();
			for (ConsultaDto consulta : expedientTipusService.consultaFindAll(command.getId()))
				consultesMap.put(consulta.getCodi(), consulta);
			ConsultaDto consulta;
			Set<ConsultaCampDto> campsConsulta;
			for (String consultaCodi : command.getConsultes()) {
				consulta = consultesMap.get(consultaCodi);
				campsConsulta = new HashSet<ConsultaCampDto>();
				campsConsulta.addAll(expedientTipusService.consultaCampFindCampAmbConsultaIdAndTipus(consulta.getId(), TipusConsultaCamp.FILTRE));
				campsConsulta.addAll(expedientTipusService.consultaCampFindCampAmbConsultaIdAndTipus(consulta.getId(), TipusConsultaCamp.INFORME));
				// Comprova que tots els seus camps de tipus filtre i paràmetre s'exportin
				for (ConsultaCampDto consultaCamp : campsConsulta)
					if (consultaCamp.getTipus() != TipusConsultaCamp.PARAM
							&& ! consultaCamp.getCampCodi().startsWith(ExpedientCamps.EXPEDIENT_PREFIX))
						if (consultaCamp.getDefprocJbpmKey() != null) {
							// variable lligada a una variable de la definició de procés
							if (!command.getDefinicionsProces().contains(consultaCamp.getDefprocJbpmKey())) {
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												this.codiMissatge + ".consulta.variable.definicioProces", 
												new Object[] {	consulta.getCodi(), 
																consultaCamp.getCampCodi(),
																consultaCamp.getDefprocJbpmKey(),
																consultaCamp.getDefprocVersio()}))
								.addNode("consultes")
								.addConstraintViolation();
								valid = false;
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

			// Conjunt de totes les variables per comprovar la integració amb Sistra
			Set<String> campCodis = new HashSet<String>(campsMap.keySet());
			Set<String> documentsCodis = new HashSet<String>(documentsMap.keySet());
			// Definicions de procés ha de coincidir jbpmKey i versió
			for (DefinicioProcesDto definicioProces : expedientTipusService.definicioFindAll(command.getId())) {
				if (command.getDefinicionsProces().contains(definicioProces.getJbpmKey()) 
						&& command.getDefinicionsVersions().get(definicioProces.getJbpmKey()).equals(definicioProces.getVersio())) {
					// Comprova les dependències de les variables.
					// Només comprova les consultes, perquè les enumeracions i dominis poden ser globals de l'entorn
					for (CampDto campDp : campService.findAllOrdenatsPerCodi(null, definicioProces.getId())) {
						// Afegeix el codi del camp al conjunt de camps
						campCodis.add(campDp.getCodi());
						if (campDp.getConsulta() != null
								&& ! command.getConsultes().contains(campDp.getConsulta().getCodi())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".definicio.variable.consulta", 
											new Object[] {	campDp.getCodi(),
															definicioProces.getJbpmKey(), 
															campDp.getConsulta().getCodi()}))
							.addNode("definicionsProces")
							.addConstraintViolation();
							valid = false;
						}
					}
					// Comprova les dependències de les tasques
					for (TascaDto tasca : definicioProcesService.tascaFindAll(definicioProces.getId())){
						// Camps
						for (CampTascaDto campTasca : tasca.getCamps())
							if ( campTasca.getCamp().getExpedientTipus() != null // camp del tipus d'expedient
									&& ! command.getVariables().contains(campTasca.getCamp().getCodi())) {
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												this.codiMissatge + ".definicio.variable", 
												new Object[] {	tasca.getJbpmName(),
																definicioProces.getJbpmKey(), 
																campTasca.getCamp().getCodi()}))
								.addNode("definicionsProces")
								.addConstraintViolation();
								valid = false;
							}
						// Documents
						for (DocumentTascaDto documentTasca : tasca.getDocuments())
							if (documentTasca.getDocument().getExpedientTipus() != null // document del tipus d'expedient
									&& ! command.getDocuments().contains(documentTasca.getDocument().getCodi())) {
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												this.codiMissatge + ".definicio.document", 
												new Object[] {	tasca.getJbpmName(),
																definicioProces.getJbpmKey(), 
																documentTasca.getDocument().getCodi()}))
								.addNode("definicionsProces")
								.addConstraintViolation();
								valid = false;
							}
						// Signatures
						for (FirmaTascaDto firmaTasca : tasca.getFirmes())
							if ( firmaTasca.getDocument().getExpedientTipus() != null // document del tipus d'expedient
									&& ! command.getDocuments().contains(firmaTasca.getDocument().getCodi())) {
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage(
												this.codiMissatge + ".definicio.firma", 
												new Object[] {	tasca.getJbpmName(),
																definicioProces.getJbpmKey(), 
																firmaTasca.getDocument().getCodi()}))
								.addNode("definicionsProces")
								.addConstraintViolation();
								valid = false;
							}
						// Guarda els codis dels documents de la definició de procés
						for(DocumentDto d : documentService.findAll(null, definicioProces.getId()))
							documentsCodis.add(d.getCodi());
					}
				}
			}
			// Integració amb tràmits de Sistra
			if (command.isIntegracioSistra()) {
				// Comprova que totes les variables mapejades s'exportin
				for (MapeigSistraDto mapeig : expedientTipusService.mapeigFindAll(command.getId()))
					if (mapeig.getTipus() == TipusMapeig.Variable
						&& !campCodis.contains(mapeig.getCodiHelium())) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(
										this.codiMissatge + ".mapeigSistra.variable", 
										new Object[] {mapeig.getCodiHelium(), mapeig.getCodiSistra()}))
						.addNode("integracioSistra")
						.addConstraintViolation();
						valid = false;
					} else if (mapeig.getTipus() == TipusMapeig.Document
							&& !documentsCodis.contains(mapeig.getCodiHelium())) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(
										this.codiMissatge + ".mapeigSistra.document", 
										new Object[] {mapeig.getCodiHelium(), mapeig.getCodiSistra()}))
						.addNode("integracioSistra")
						.addConstraintViolation();
						valid = false;
					}
			}
		} else {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(this.codiMissatge + ".expedientTipusIdNull", null));
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}
}
