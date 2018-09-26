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
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps;
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
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					command.getEntornId(), 
					command.getId());
			boolean herencia = expedientTipus.getExpedientTipusPareId() != null;
			Long expedientTipusPareId = expedientTipus.getExpedientTipusPareId();
			
			// Conjunt d'enumeracions i dominis del tipus d'expedient per comprovar si les dependències són globals
			// O no s'han escollit
			Set<Long> enumeracionsGlobalsIds = new HashSet<Long>();
			for (EnumeracioDto e : enumeracioService.findGlobals(command.getEntornId()))
				enumeracionsGlobalsIds.add(e.getId());
			Set<Long> dominisGlobalsIds = new HashSet<Long>();
			for (DominiDto d : dominiService.findGlobals(expedientTipus.getEntorn().getId()))
				dominisGlobalsIds.add(d.getId());
			
			// Definició de procés inicial
			if (expedientTipus.getJbpmProcessDefinitionKey() != null
					&& !herencia
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
								&& !enumeracionsGlobalsIds.contains(camp.getEnumeracio().getId()) ) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.seleccio.enumeracio.tipexp", 
											new Object[] {camp.getCodi(), camp.getEnumeracio().getCodi()}))
							.addNode("variables")
							.addConstraintViolation();
							valid = false;
						}
					if (camp.getDomini() != null)
						if (!command.getDominis().contains(camp.getDomini().getCodi())
								&& !dominisGlobalsIds.contains(camp.getDomini().getId())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".variable.seleccio.domini.tipexp", 
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
												this.codiMissatge + ".consulta.variable.definicioProces.versio", 
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

			// Conjunt de totes les variables per comprovar la integració amb Sistra, es completaran dins
			// de la comprovació de les defincions de procés
			Set<String> campCodis = new HashSet<String>(campsMap.keySet());
			Set<String> documentsCodis = new HashSet<String>(documentsMap.keySet());

			// Comprovació de les defincions de procés i les seves dependències amb el tipus d'expedient
			for (DefinicioProcesDto definicioProces : expedientTipusService.definicioFindAll(command.getId()))
				if (command.getDefinicionsProces().contains(definicioProces.getJbpmKey()) 
						&& command.getDefinicionsVersions().get(definicioProces.getJbpmKey()).equals(definicioProces.getVersio())) 
				valid = isDefinicioProcesValida(
							definicioProces,
							command,
							enumeracionsGlobalsIds,
							dominisGlobalsIds,
							context,
							campCodis,
							documentsCodis);
				
			// Integració amb tràmits de Sistra
			if (command.isIntegracioSistra())
				// Comprova que totes les variables mapejades s'exportin. Mira el tipus i si el TE te info pròpia o les dades estan a la DP
				for (MapeigSistraDto mapeig : expedientTipusService.mapeigFindAll(command.getId())) {

					if (mapeig.getTipus() == TipusMapeig.Variable
							&& ((expedientTipus.isAmbInfoPropia() && !command.getVariables().contains(mapeig.getCodiHelium())
								|| (!expedientTipus.isAmbInfoPropia() && !campCodis.contains(mapeig.getCodiHelium()))))) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".mapeigSistra.variable", 
											new Object[] {mapeig.getCodiHelium(), mapeig.getCodiSistra()}))
							.addNode("integracioSistra")
							.addConstraintViolation();
							valid = false;
						} else if (mapeig.getTipus() == TipusMapeig.Document
								&& ((expedientTipus.isAmbInfoPropia() && !command.getDocuments().contains(mapeig.getCodiHelium())
										|| (!expedientTipus.isAmbInfoPropia() && !documentsCodis.contains(mapeig.getCodiHelium()))))) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											this.codiMissatge + ".mapeigSistra.document", 
											new Object[] {mapeig.getCodiHelium(), mapeig.getCodiSistra()}))
							.addNode("integracioSistra")
							.addConstraintViolation();
							valid = false;
						}
				}
			
			// Herència
			
			// Camps, documents i signatures
			
		} else {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(this.codiMissatge + ".expedientTipusIdNull", null));
		}
		context.disableDefaultConstraintViolation();
		
		return valid;
	}

	/** Valida les dependències de la definció de procés. Ha de recuperar la informació de bbdd 
	 * per validar-ho amb el command.
	 * @param dominisGlobalsIds 
	 * @param enumeracionsGlobalsIds 
	 * @param command 
	 * @param definicioProcesCodi
	 * @param context
	 * @param documentsCodis 
	 * @param campCodis 
	 * @return
	 */
	private boolean isDefinicioProcesValida(
			DefinicioProcesDto definicioProces,
			ExpedientTipusExportarCommand command, 
			Set<Long> enumeracionsGlobalsIds, 
			Set<Long> dominisGlobalsIds, 
			ConstraintValidatorContext context, 
			Set<String> campCodis, 
			Set<String> documentsCodis) {

		boolean valid = true;
				
		// Comprova les consultes
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
			// Comprova l'enumeració
			if (campDp.getEnumeracio() != null 
					&& !enumeracionsGlobalsIds.contains(campDp.getEnumeracio().getId())
					&& !command.getEnumeracions().contains(campDp.getEnumeracio().getCodi())) {
				valid = false;
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(
								this.codiMissatge + ".definicio.variable.seleccio.enumeracio.tipexp", 
								new Object[] {campDp.getCodi(), definicioProces.getJbpmKey(), campDp.getEnumeracio().getCodi()}))
				.addNode("definicionsProces")
				.addConstraintViolation();
			}
			
			// Comprova el domini
			if (campDp.getDomini() != null 
					&& !dominisGlobalsIds.contains(campDp.getDomini().getId())
					&& !command.getDominis().contains(campDp.getDomini().getCodi())) {
				valid = false;
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(
								this.codiMissatge + ".definicio.variable.seleccio.domini.tipexp", 
								new Object[] {campDp.getCodi(), definicioProces.getJbpmKey(), campDp.getDomini().getCodi()}))
				.addNode("definicionsProces")
				.addConstraintViolation();
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
									this.codiMissatge + ".definicio.tasca.variable.tipexp", 
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
									this.codiMissatge + ".definicio.tasca.document.tipexp", 
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
									this.codiMissatge + ".definicio.tasca.signatura.tipexp", 
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
		
		return valid;
	}
}
