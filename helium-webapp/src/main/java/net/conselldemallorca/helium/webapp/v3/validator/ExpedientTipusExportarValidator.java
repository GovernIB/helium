package net.conselldemallorca.helium.webapp.v3.validator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto.TipusConsultaCamp;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDto;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
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
	
	@Override
	public void initialize(ExpedientTipusExportar anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusExportarCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		
		if (command.getId() != null) {
			// Variables
			Map<String, CampDto> campsMap = new HashMap<String, CampDto>();
			for (CampDto camp : expedientTipusService.campFindAllOrdenatsPerCodi(command.getId()))
				campsMap.put(camp.getCodi(), camp);
			CampDto camp;
			for (String campCodi : command.getVariables()) {
				camp = campsMap.get(campCodi);
				if (camp.getTipus() == CampTipusDto.REGISTRE) {
					// Comprova que les variables de tipus registre exportades tinguin les seves variables exportables.
					for (CampDto membre : expedientTipusService.campRegistreFindMembresAmbRegistreId(camp.getId()))
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
						if (!command.getEnumeracions().contains(camp.getEnumeracio().getCodi())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.seleccio.enumeracio", 
											new Object[] {camp.getCodi(), camp.getEnumeracio().getCodi()}))
							.addNode("variables")
							.addConstraintViolation();
							valid = false;
						}
					if (camp.getDomini() != null)
						if (!command.getDominis().contains(camp.getDomini().getCodi())) {
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

			// Integració amb tràmits de Sistra
			if (command.isIntegracioSistra()) {
				// Comprova que totes les variables mapejades s'exportin
				for (MapeigSistraDto mapeig : expedientTipusService.mapeigFindAll(command.getId()))
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
			Map<String, ExpedientTipusDocumentDto> documentsMap = new HashMap<String, ExpedientTipusDocumentDto>();
			for (ExpedientTipusDocumentDto document : expedientTipusService.documentFindAll(command.getId()))
				documentsMap.put(document.getCodi(), document);
			ExpedientTipusDocumentDto document;
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
			
			// Definicions de procés ha de coincidir jbpmKey i versió
			for (DefinicioProcesDto definicioProces : expedientTipusService.definicioFindAll(command.getId())) {
				if (command.getDefinicionsProces().contains(definicioProces.getJbpmKey()) 
						&& command.getDefinicionsVersions().get(definicioProces.getJbpmKey()).equals(definicioProces.getVersio())) {
					// Comprova les dependències de les tasques
					for (TascaDto tasca : definicioProcesService.tascaFindAll(definicioProces.getId())){
						// Camps
						for (CampTascaDto campTasca : tasca.getCamps())
							if (! command.getVariables().contains(campTasca.getCamp().getCodi())) {
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
							if (! command.getDocuments().contains(documentTasca.getDocument().getCodi())) {
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
							if (! command.getDocuments().contains(firmaTasca.getDocument().getCodi())) {
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
					}
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
