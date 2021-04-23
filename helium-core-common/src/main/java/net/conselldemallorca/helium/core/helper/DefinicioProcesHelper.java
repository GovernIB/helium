/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import net.conselldemallorca.helium.core.api.WDeployment;
import net.conselldemallorca.helium.core.api.WProcessDefinition;
import net.conselldemallorca.helium.core.api.WorkflowEngineApi;
import net.conselldemallorca.helium.core.model.hibernate.*;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.Tasca.TipusTasca;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDto;
import net.conselldemallorca.helium.v3.core.api.exception.DeploymentException;
import net.conselldemallorca.helium.v3.core.api.exception.ExportException;
import net.conselldemallorca.helium.v3.core.api.exportacio.*;
import net.conselldemallorca.helium.v3.core.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Helper per a les definicions de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DefinicioProcesHelper {
	
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private CampAgrupacioRepository campAgrupacioRepository;
	@Resource
	private CampValidacioRepository campValidacioRepository;
	@Resource
	private CampRegistreRepository campRegistreRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private DominiRepository dominiRepository;
	@Resource
	private TerminiRepository terminiRepository;
	@Resource
	private AccioRepository accioRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private DocumentTascaRepository documentTascaRepository;
	@Resource
	private FirmaTascaRepository firmaTascaRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private EntornHelper entornHelper;
	
	/**
	 * Mètode per importar la informació d'una definició de procés. Si s'efectua sobre una definicó de procés
	 * existent llavors es realitza la importació de la seva informació. Si es realitza sobre un entorn
	 * o tipus d'expedient llavors es fa el desplegament de l'arxiu i la importació de la informació sobre una 
	 * nova versió de la definició de procés.
	 * 
	 * @param entornId
	 * @param expedientTipusId
	 * @param importacio
	 * @param command
	 * @param sobreEscriure Indica si actualitzar o no les entitats amb el mateix codi
	 * @return
	 */
	@Transactional
	public DefinicioProces importar(
			Long entornId, 
			Long expedientTipusId, 
			DefinicioProcesExportacio importacio,
			DefinicioProcesExportacioCommandDto command,
			boolean sobreEscriure) {
		
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId, 
				true);
		ExpedientTipus expedientTipus = expedientTipusId != null ? 
						expedientTipusRepository.findOne(expedientTipusId) : null;
		
		// Importació

		// si el command és null s'importa tot
		boolean importAll = command == null;
		boolean definicioProcesExisteix = false;
		if(command != null) {
			definicioProcesExisteix = command.getId() != null;
		} else {
			// Busca la darrera versió de la definició de procés pel tipus d'expedient
			if (expedientTipusId != null) {
				DefinicioProces darreraVersio =
							definicioProcesRepository.findDarreraVersioAmbTipusExpedientIJbpmKey(expedientTipusId, 
																								importacio.getDefinicioProcesDto().getJbpmKey());
				if (darreraVersio != null) {
					definicioProcesExisteix = true;
					// informa el command per reaprofitar l'id.
					command = new DefinicioProcesExportacioCommandDto();
					command.setId(darreraVersio.getId());
				}
			}
		}
		DefinicioProces definicio;
		if ( ! definicioProcesExisteix) {
			// Nova definició de procés
			WDeployment dpd = workflowEngineApi.desplegar(
					importacio.getNomDeploy(), 
					importacio.getContingutDeploy());
			if (dpd != null && dpd.getProcessDefinitions() != null && !dpd.getProcessDefinitions().isEmpty()) {
				// En el cas de importació de definició de procés, només es desplega 1 definició de procés
				WProcessDefinition wpd = dpd.getProcessDefinitions().get(0);
				// Crea la nova definició de procés
				definicio = new DefinicioProces(
						wpd.getId(),
						wpd.getKey(),
						wpd.getVersion(),
						entorn);
				definicio.setExpedientTipus(expedientTipus);
				if (expedientTipus != null)
					expedientTipus.getDefinicionsProces().add(definicio);
				definicio = definicioProcesRepository.saveAndFlush(definicio);
				// Crea les tasques publicades
				for (String nomTasca: workflowEngineApi.getTaskNamesFromDeployedProcessDefinition(dpd, wpd.getId())) {
					Tasca tasca = new Tasca(
							definicio,
							nomTasca,
							nomTasca,
							TipusTasca.ESTAT);
					String prefixRecursBo = "forms/" + nomTasca;
					for (String resourceName: workflowEngineApi.getResourceNames(dpd.getId())) {
						if (resourceName.startsWith(prefixRecursBo)) {
							tasca.setTipus(TipusTasca.FORM);
							tasca.setRecursForm(nomTasca);
							break;
						}
					}
					tascaRepository.save(tasca);
					definicio.getTasques().add(tasca);
				}				
			} else
				throw new DeploymentException(
						messageHelper.getMessage("exportar.validacio.definicio.deploy.error"));
		} else {
			definicio = definicioProcesRepository.findById(command.getId());
		}

		// Copia la informació importada

		// Agrupacions
		Map<String, CampAgrupacio> agrupacions = new HashMap<String, CampAgrupacio>();
		CampAgrupacio agrupacio;
		if (importAll || command.getAgrupacions().size() > 0)
			for(AgrupacioExportacio agrupacioExportat : importacio.getAgrupacions() )
				if (importAll || command.getAgrupacions().contains(agrupacioExportat.getCodi())){
					agrupacio = null;
					if (definicioProcesExisteix) {
						agrupacio = campAgrupacioRepository.findAmbDefinicioProcesICodi(definicio.getId(), agrupacioExportat.getCodi());
					}
					if (agrupacio == null || sobreEscriure) {
						if (agrupacio == null) {
							agrupacio = new CampAgrupacio(
									definicio, 
									agrupacioExportat.getCodi(), 
									agrupacioExportat.getNom(),
									agrupacioExportat.getOrdre());
							definicio.getAgrupacions().add(agrupacio);
							campAgrupacioRepository.save(agrupacio);
						} else {
							agrupacio.setNom(agrupacioExportat.getNom());
							agrupacio.setOrdre(agrupacioExportat.getOrdre());
						}
						agrupacio.setDescripcio(agrupacioExportat.getDescripcio());
					}
					agrupacions.put(agrupacio.getCodi(), agrupacio);
				}		
		// Camps
		Map<String, Camp> camps = new HashMap<String, Camp>();
		Map<Camp, CampExportacio> registres = new HashMap<Camp, CampExportacio>();
		Map<Camp, CampExportacio> campsTipusConsulta = new HashMap<Camp, CampExportacio>();
		Camp camp;
		if (importAll || command.getVariables().size() > 0) {
			for(CampExportacio campExportat : importacio.getCamps() )
				if (importAll || command.getVariables().contains(campExportat.getCodi())){
					camp = null;
					if (definicioProcesExisteix) {
						camp = campRepository.findByDefinicioProcesAndCodi(definicio, campExportat.getCodi());
					}
					if (camp == null || sobreEscriure) {
						if (camp == null) {
							camp = new Camp(
									definicio, 
									campExportat.getCodi(),
									TipusCamp.valueOf(campExportat.getTipus().toString()),
									campExportat.getEtiqueta());
							definicio.getCamps().add(camp);
							camp = campRepository.saveAndFlush(camp);
						} else {
							camp.setTipus(TipusCamp.valueOf(campExportat.getTipus().toString()));
							camp.setEtiqueta(campExportat.getEtiqueta());
						}
						camp.setIgnored(campExportat.isIgnored());
						camp.setObservacions(campExportat.getObservacions());
						camp.setDominiId(campExportat.getDominiId());
						camp.setDominiParams(campExportat.getDominiParams());
						camp.setDominiCampText(campExportat.getDominiCampText());
						camp.setDominiCampValor(campExportat.getDominiCampValor());
						camp.setDominiCacheText(campExportat.isDominiCacheText());
						camp.setConsultaParams(campExportat.getConsultaParams());
						camp.setConsultaCampText(campExportat.getConsultaCampText());
						camp.setConsultaCampValor(campExportat.getConsultaCampValor());
						camp.setMultiple(campExportat.isMultiple());
						camp.setOcult(campExportat.isOcult());
						camp.setDominiIntern(campExportat.isDominiIntern());
						camp.setDefprocJbpmKey(campExportat.getDefprocJbpmKey());
						camp.setJbpmAction(campExportat.getJbpmAction());
						camp.setOrdre(campExportat.getOrdre());
						
						// Esborra les validacions existents
						for (Validacio validacio : camp.getValidacions())
							campValidacioRepository.delete(validacio);
						camp.getValidacions().clear();
						campValidacioRepository.flush();
						// Afegeix les noves validacions
						for (ValidacioExportacio validacioExportat : campExportat.getValidacions()) {
							Validacio validacio = new Validacio(
									camp, 
									validacioExportat.getExpressio(), 
									validacioExportat.getMissatge());
							validacio.setNom(validacioExportat.getNom());
							validacio.setOrdre(validacioExportat.getOrdre());
							campValidacioRepository.save(validacio);
							camp.getValidacions().add(validacio);
						}
						// Agrupació del camp
						if (campExportat.getAgrupacioCodi() != null && agrupacions.containsKey(campExportat.getAgrupacioCodi()))
							camp.setAgrupacio(agrupacions.get(campExportat.getAgrupacioCodi()));
						// Enumeració del camp
						if (campExportat.getCodiEnumeracio() != null)
							this.relacionarCampEnumeracio(camp, campExportat.getCodiEnumeracio(), campExportat.isDependenciaEntorn(), entorn, expedientTipus);
						// Domini del camp
						if (campExportat.getCodiDomini() != null)
							this.relacionarCampDomini(camp, campExportat.getCodiDomini(), campExportat.isDependenciaEntorn(), entorn, expedientTipus);
						// Guarda els camps de tipus consulta per processar-los després de les consultes
						if (campExportat.getCodiConsulta() != null)
							campsTipusConsulta.put(camp, campExportat);
						// Guarda els registres per processar-los després de tots els camps
						if (camp.getTipus() == TipusCamp.REGISTRE) {
							registres.put(camp, campExportat);
						}						
					}
					camps.put(camp.getCodi(), camp);
				}		
			// Tracta els registres
			CampExportacio campExportat;
			for (Camp registre : registres.keySet()) {
				// Esborra la relació amb els membres existents
				if (!registre.getRegistreMembres().isEmpty()) {
					for (CampRegistre campRegistre : registre.getRegistreMembres()) {
						campRegistre.getMembre().getRegistrePares().remove(campRegistre);
						campRegistre.setMembre(null);
						campRegistre.setRegistre(null);
						campRegistreRepository.delete(campRegistre);
					}
					registre.getRegistreMembres().clear();
					campRegistreRepository.flush();
				}
				// afegeix la informació dels registres
				campExportat = registres.get(registre);
				for (RegistreMembreExportacio registreMembre : campExportat.getRegistreMembres()) {
					CampRegistre campRegistre = new CampRegistre(
							camps.get(registre.getCodi()),
							camps.get(registreMembre.getCodi()),
							registreMembre.getOrdre());
					campRegistre.setLlistar(registreMembre.isLlistar());
					campRegistre.setObligatori(registreMembre.isObligatori());
					registre.getRegistreMembres().add(campRegistre);
					campRegistreRepository.save(campRegistre);
				}
			}
		}		
		// Documents
		Map<String, Document> documents = new HashMap<String, Document>();
		Document document;
		if (importAll || command.getDocuments().size() > 0)
			for(DocumentExportacio documentExportat : importacio.getDocuments() )
				if (importAll || command.getDocuments().contains(documentExportat.getCodi())){
					document = null;
					if (definicioProcesExisteix) {
						document = documentRepository.findByDefinicioProcesAndCodi(definicio, documentExportat.getCodi());
					}
					if (document == null || sobreEscriure) {
						if (document == null) {
							document = new Document(
									definicio, 
									documentExportat.getCodi(), 
									documentExportat.getNom());
							definicio.getDocuments().add(document);
							documentRepository.saveAndFlush(document);
						} else {
							document.setNom(documentExportat.getNom());
						}
						document.setDescripcio(documentExportat.getDescripcio());
						document.setArxiuNom(documentExportat.getArxiuNom());
						document.setArxiuContingut(documentExportat.getArxiuContingut());
						document.setPlantilla(documentExportat.isPlantilla());
						document.setNotificable(documentExportat.isNotificable());
						document.setCustodiaCodi(documentExportat.getCustodiaCodi());
						document.setContentType(documentExportat.getContentType());
						document.setTipusDocPortasignatures(documentExportat.getTipusDocPortasignatures());
						document.setAdjuntarAuto(documentExportat.isAdjuntarAuto());
						if (documentExportat.getCodiCampData() != null)
							document.setCampData(camps.get(documentExportat.getCodiCampData()));
						document.setConvertirExtensio(documentExportat.getConvertirExtensio());
						document.setExtensionsPermeses(documentExportat.getExtensionsPermeses());
						document.setIgnored(documentExportat.isIgnored());
						document.setNtiTipoDocumental(documentExportat.getNtiTipoDocumental());
					}
					documents.put(documentExportat.getCodi(), document);
				}	
		
		// Terminis
		Termini termini;
		if (importAll || command.getTerminis().size() > 0)
			for(TerminiExportacio terminiExportat : importacio.getTerminis() )
				if (importAll || command.getTerminis().contains(terminiExportat.getCodi())){
					termini = null;
					if (definicioProcesExisteix) {
						termini = terminiRepository.findByDefinicioProcesAndCodi(definicio, terminiExportat.getCodi());
					}
					if (termini == null || sobreEscriure) {
						if (termini == null) {
							termini = new Termini(
									definicio,
									terminiExportat.getCodi(),
									terminiExportat.getNom(),
									terminiExportat.getAnys(),
									terminiExportat.getMesos(),
									terminiExportat.getDies(),
									terminiExportat.isLaborable());
							definicio.getTerminis().add(termini);
							terminiRepository.save(termini);
						} else {
							termini.setNom(terminiExportat.getNom());
							termini.setNom(terminiExportat.getNom());
							termini.setAnys(terminiExportat.getAnys());
							termini.setMesos(terminiExportat.getMesos());
							termini.setDies(terminiExportat.getDies());
							termini.setLaborable(terminiExportat.isLaborable());
						}
						termini.setDuradaPredefinida(terminiExportat.isDuradaPredefinida());
						termini.setDescripcio(terminiExportat.getDescripcio());
						termini.setDiesPrevisAvis(terminiExportat.getDiesPrevisAvis());
						termini.setAlertaPrevia(terminiExportat.isAlertaPrevia());
						termini.setAlertaFinal(terminiExportat.isAlertaFinal());
						termini.setAlertaCompletat(terminiExportat.isAlertaCompletat());
						termini.setManual(terminiExportat.isManual());
					}
				}
		
		// Accions
		Accio accio;
		if (importAll || command.getAccions().size() > 0)
			for(AccioExportacio accioExportat : importacio.getAccions() )
				if (importAll || command.getAccions().contains(accioExportat.getCodi())){
					accio = null;
					if (definicioProcesExisteix) {
						accio = accioRepository.findByDefinicioProcesIdAndCodi(definicio.getId(), accioExportat.getCodi());
					}
					if (accio == null || sobreEscriure) {
						if (accio == null) {
							accio = new Accio(
									definicio, 
									accioExportat.getCodi(), 
									accioExportat.getNom(),
									accioExportat.getJbpmAction());
							definicio.getAccions().add(accio);
							accioRepository.save(accio);
						} else {
							accio.setNom(accioExportat.getNom());
							accio.setJbpmAction(accioExportat.getJbpmAction());
						}
						accio.setDescripcio(accioExportat.getDescripcio());
						accio.setPublica(accioExportat.isPublica());
						accio.setOculta(accioExportat.isOculta());
						accio.setRols(accioExportat.getRols());
					}
				}		
		
		// Tasques
		Tasca tasca;
		if (importAll || command.getTasques().size() > 0)
			for(TascaExportacio tascaExportat : importacio.getTasques() )
				if (importAll || command.getTasques().contains(tascaExportat.getJbpmName())){
					tasca = tascaRepository.findByJbpmNameAndDefinicioProces(tascaExportat.getJbpmName(), definicio);
					if (tasca != null && (!definicioProcesExisteix || sobreEscriure)) {
						tasca.setNom(tascaExportat.getNom());
						tasca.setTipus(TipusTasca.valueOf(tascaExportat.getTipus().toString()));
						tasca.setMissatgeInfo(tascaExportat.getMissatgeInfo());
						tasca.setMissatgeWarn(tascaExportat.getMissatgeWarn());
						tasca.setNomScript(tascaExportat.getNomScript());
						tasca.setExpressioDelegacio(tascaExportat.getExpressioDelegacio());
						tasca.setRecursForm(tascaExportat.getRecursForm());
						tasca.setFormExtern(tascaExportat.getFormExtern());
						tasca.setTramitacioMassiva(tascaExportat.isTramitacioMassiva());
						tasca.setFinalitzacioSegonPla(tascaExportat.isFinalitzacioSegonPla());
						tasca.setAmbRepro(tascaExportat.isAmbRepro());
						tasca.setMostrarAgrupacions(tascaExportat.isMostrarAgrupacions());
						tascaRepository.save(tasca);
						
						// Si la tasca ja existia llavors esborra els camps, documents i firmes
						if (definicioProcesExisteix) {
							for(CampTasca c : tasca.getCamps())
								campTascaRepository.delete(c);
							tasca.getCamps().clear();
							documentTascaRepository.flush();
							
							for(DocumentTasca d : tasca.getDocuments())
								documentTascaRepository.delete(d);
							tasca.getDocuments().clear();
							documentTascaRepository.flush();
							
							for(FirmaTasca f : tasca.getFirmes())
								firmaTascaRepository.delete(f);
							tasca.getFirmes().clear();
							firmaTascaRepository.flush();
						}
						// Camps de la tasca
						for (CampTascaExportacio campExportat : tascaExportat.getCamps()) {
							CampTasca campTasca = new CampTasca();
							campTasca.setOrder(campExportat.getOrder());	
							campTasca.setAmpleCols(campExportat.getAmpleCols());
							campTasca.setBuitCols(campExportat.getBuitCols());
							campTasca.setReadFrom(campExportat.isReadFrom());
							campTasca.setWriteTo(campExportat.isWriteTo());
							campTasca.setRequired(campExportat.isRequired());
							campTasca.setReadOnly(campExportat.isReadOnly());
							campTasca.setTasca(tasca);
							this.relacionarCampTasca(
									campTasca, 
									campExportat.getCampCodi(), 
									campExportat.isTipusExpedient(),
									expedientTipus, 
									definicio);
							tasca.getCamps().add(campTasca);	
							campTascaRepository.save(campTasca);
						}
						// Documents de la tasca
						for (DocumentTascaExportacio documentExportat : tascaExportat.getDocuments()) {
							DocumentTasca documentTasca = new DocumentTasca();
							documentTasca.setRequired(documentExportat.isRequired());
							documentTasca.setReadOnly(documentExportat.isReadOnly());
							documentTasca.setOrder(documentExportat.getOrder());
							documentTasca.setTasca(tasca);
							this.relacionarDocumentTasca(
									documentTasca, 
									documentExportat.getDocumentCodi(), 
									documentExportat.isTipusExpedient(),
									expedientTipus, 
									definicio);
							tasca.getDocuments().add(documentTasca);	
							documentTascaRepository.save(documentTasca);
						}
						// Firmes de la tasca
						for (FirmaTascaExportacio firmaExportat : tascaExportat.getFirmes()) {
							FirmaTasca firmaTasca = new FirmaTasca();
							firmaTasca.setRequired(firmaExportat.isRequired());
							firmaTasca.setOrder(firmaExportat.getOrder());
							this.relacionarFirmaTasca(
									firmaTasca, 
									firmaExportat.getDocumentCodi(), 
									firmaExportat.isTipusExpedient(),
									expedientTipus, 
									definicio);
							firmaTasca.setTasca(tasca);
							tasca.getFirmes().add(firmaTasca);	
							firmaTascaRepository.save(firmaTasca);
						}
					}
				}
		if (expedientTipus != null && definicio != null) {
			// Si el tipus d'expedient no tenia cap definició de procés inicial llavors la marca com inicial
			if (expedientTipus.getJbpmProcessDefinitionKey() == null) {
				expedientTipus.setJbpmProcessDefinitionKey(definicio.getJbpmKey());
				expedientTipusRepository.save(expedientTipus);
			}			
			expedientTipus.getDefinicionsProces().add(definicio);
		}		
		return definicio;
	}

	/** Troba el domini per codi dins del tius d'expedient o l'entorn i el relaciona amb el camp. Si 
	 * no el troba llença una excepció de no trobat.
	 * @param camp
	 * @param codiDomini
	 * @param dependenciaEntorn 
	 * @param entorn
	 * @param expedientTipus
	 */
	private void relacionarCampDomini(
			Camp camp, 
			String codiDomini, 
			boolean dependenciaEntorn,
			Entorn entorn, 
			ExpedientTipus expedientTipus) throws DeploymentException {
		
		Domini domini = null;
		if (dependenciaEntorn) {
			// Domini a nivell d'entor
			domini = dominiRepository.findByEntornAndCodi(entorn, codiDomini);	
		} else {
			// Domini a nivell de TE
			if (expedientTipus != null)
				for (Domini d : expedientTipus.getDominis())
					if (d.getCodi().equals(codiDomini)) {
						domini = d;
						break;
					}
		}
		if (domini == null)
			throw new DeploymentException(
					messageHelper.getMessage(
					"exportar.validacio.variable.seleccio.domini." + (dependenciaEntorn ? "entorn" : "tipexp"), 
					new Object[]{
							camp.getCodi(),
							codiDomini}));
		camp.setDomini(domini);
	}

	/** Troba la enumeració per codi dins del tius d'expedient o l'entorn i el relaciona amb el camp. Si 
	 * no el troba llença una excepció de no trobat.
	 * @param camp
	 * @param codiEnumeracio
	 * @param dependenciaEntorn 
	 * @param entorn
	 * @param expedientTipus
	 */
	private void relacionarCampEnumeracio(
			Camp camp, 
			String codiEnumeracio, 
			boolean dependenciaEntorn, 
			Entorn entorn,
			ExpedientTipus expedientTipus) throws DeploymentException {

		Enumeracio enumeracio = null;
		if (dependenciaEntorn) {
			// Enumeració a nivell d'entor
			enumeracio = enumeracioRepository.findByEntornAndCodi(entorn, codiEnumeracio);	
		} else {
			// Enumeració a nivell de TE
			if (expedientTipus != null)
				for (Enumeracio e : expedientTipus.getEnumeracions())
					if (e.getCodi().equals(codiEnumeracio)) {
						enumeracio = e;
						break;
					}
			if (enumeracio == null && expedientTipus.isAmbHerencia())
				// Mira entre les enumeracions heretades
				for (Enumeracio e : expedientTipus.getExpedientTipusPare().getEnumeracions())
					if (e.getCodi().equals(codiEnumeracio)) {
						enumeracio = e;
						break;
					}

		}
		if (enumeracio == null)
			throw new DeploymentException(
					messageHelper.getMessage(
					"exportar.validacio.variable.seleccio.enumeracio." + (dependenciaEntorn ? "entorn" : "tipexp"), 
					new Object[]{
							camp.getCodi(),
							codiEnumeracio}));
		camp.setEnumeracio(enumeracio);
	}
	
	/** Troba el camp per al camp tasca segons si el tipus d'expedient està configurat amb info pròpia i si el camp tenia origen en la
	 * definició de procés o en el tipus d'expedient.
	 * @param campTasca
	 * @param codiCamp
	 * @param expedientTipus
	 * @param definicio
	 */
	@Transactional
	public void relacionarCampTasca(
			CampTasca campTasca, 
			String codiCamp, 
			boolean isTipusExpedient,
			ExpedientTipus expedientTipus,
			DefinicioProces definicio) throws DeploymentException {
		
		Camp camp = null;
		if (expedientTipus != null && isTipusExpedient) {
			// Mira primer en el tipus d'expedient
			for (Camp c : expedientTipus.getCamps())
				if (c.getCodi().equals(codiCamp)) {
					camp = c;
					break;
				}
			if (camp == null)
				// Camp del tipus d'expedient
				camp = campRepository.findByExpedientTipusAndCodi(expedientTipus.getId(), codiCamp, true);
		} else {
			// Camp de la definició de procés
			camp = campRepository.findByDefinicioProcesAndCodi(definicio, codiCamp);
			// Si no el troba a la definició de procés i l'expedient té info pròpia llavors ho prova al tipus d'expedient
			if (camp == null && expedientTipus != null && expedientTipus.isAmbInfoPropia())
				camp = campRepository.findByExpedientTipusAndCodi(expedientTipus.getId(), codiCamp, expedientTipus.getExpedientTipusPare() != null);
		}
		if (camp != null) {
			campTasca.setCamp(camp);
		} else
			throw new DeploymentException(
					messageHelper.getMessage(
					"exportar.validacio.tasca.variable" + (isTipusExpedient ? ".expedientTipus" : ""), 
					new Object[]{
							campTasca.getTasca().getNom(),
							codiCamp}));
	}	
	
	/** Troba el document per al document tasca segons si el tipus d'expedient està configurat amb info pròpia.
	 * @param documentTasca
	 * @param codiDocument
	 * @param expedientTipus
	 * @param definicio
	 */
	public void relacionarDocumentTasca(
			DocumentTasca documentTasca, 
			String codiDocument, 
			boolean isTipusExpedient,
			ExpedientTipus expedientTipus,
			DefinicioProces definicio) throws DeploymentException {
		
		Document document = null;
		if (expedientTipus != null && isTipusExpedient) {
			// Mira primer en el tipus d'expedient
			for (Document d : expedientTipus.getDocuments())
				if (d.getCodi().equals(codiDocument)) {
					document = d;
					break;
				}
			if (document == null)
				// Camp del tipus d'expedient
				document = documentRepository.findByExpedientTipusAndCodi(expedientTipus.getId(), codiDocument, true);
		} else {
			// Camp de la definició de procés
			document = documentRepository.findByDefinicioProcesAndCodi(definicio, codiDocument);
			// Si no el troba a la definició de procés i l'expedient té info pròpia llavors ho prova al tipus d'expedient
			if (document == null && expedientTipus != null && expedientTipus.isAmbInfoPropia())
				document = documentRepository.findByExpedientTipusAndCodi(expedientTipus.getId(), codiDocument, expedientTipus.getExpedientTipusPare() != null);
		}
		if (document != null)
			documentTasca.setDocument(document);
		else
			throw new DeploymentException(
					messageHelper.getMessage(
					"exportar.validacio.tasca.document" + (isTipusExpedient ? ".expedientTipus" : ""), 
					new Object[]{
							documentTasca.getTasca().getNom(),
							codiDocument}));
	}	

	/** Troba el document per a la firma tasca segons si el tipus d'expedient està configurat amb info pròpia.
	 * @param firmaTasca
	 * @param codiDocument
	 * @param expedientTipus
	 * @param definicio
	 */
	public void relacionarFirmaTasca(
			FirmaTasca firmaTasca, 
			String codiDocument, 
			boolean isTipusExpedient,
			ExpedientTipus expedientTipus,
			DefinicioProces definicio) throws DeploymentException {
		
		Document document = null;
		if (expedientTipus != null && isTipusExpedient) {
			// Mira primer en el tipus d'expedient
			for (Document d : expedientTipus.getDocuments())
				if (d.getCodi().equals(codiDocument)) {
					document = d;
					break;
				}
			if (document == null)
				// Camp del tipus d'expedient
				document = documentRepository.findByExpedientTipusAndCodi(expedientTipus.getId(), codiDocument, true);
		} else {
			// Camp de la definició de procés
			document = documentRepository.findByDefinicioProcesAndCodi(definicio, codiDocument);
			// Si no el troba a la definició de procés i l'expedient té info pròpia llavors ho prova al tipus d'expedient
			if (document == null && expedientTipus != null && expedientTipus.isAmbInfoPropia())
				document = documentRepository.findByExpedientTipusAndCodi(expedientTipus.getId(), codiDocument, expedientTipus.getExpedientTipusPare() != null);
		}
		if (document != null)
			firmaTasca.setDocument(document);
		else
			throw new DeploymentException(
					messageHelper.getMessage(
					"exportar.validacio.tasca.firma" + (isTipusExpedient ? ".expedientTipus" : ""), 
					new Object[]{
							firmaTasca.getTasca().getNom(),
							codiDocument}));
	}
	/** Mètode per a construir un objecte d'exportació de la definició de procés.
	 * 
	 * @param definicioProcesId Especifica una definició de procés.
	 * @param command Objecte amb els codis de la informació a incloure en la exportació. Si és null s'inclourà
	 * tota la informació sense filtrar pel command.
	 * @return
	 */
	@Transactional(readOnly = true)
	public DefinicioProcesExportacio getExportacio(
			Long definicioProcesId, 
			DefinicioProcesExportacioCommandDto command) {
		
		// si el command és null s'exporta tot
		boolean exportAll = command == null;
		
		DefinicioProcesExportacio exportacio = new DefinicioProcesExportacio();
		DefinicioProces definicio = 
				definicioProcesRepository.findOne(definicioProcesId);
		exportacio.setDefinicioProcesDto(
				conversioTipusHelper.convertir(
						definicio, 
						DefinicioProcesDto.class));
		exportacio.setNomDeploy("export.par");
		Set<String> resourceNames = workflowEngineApi.getResourceNames(definicio.getJbpmId());
		if (resourceNames != null && resourceNames.size() > 0) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(baos);
				byte[] data = new byte[1024];
				for (String resource: resourceNames) {
					byte[] bytes = workflowEngineApi.getResourceBytes(
							definicio.getJbpmId(), 
							resource);
					if (bytes != null) {
						InputStream is = new ByteArrayInputStream(bytes);
						zos.putNextEntry(new ZipEntry(resource));
						int count;
						while ((count = is.read(data, 0, 1024)) != -1)
							zos.write(data, 0, count);
				        zos.closeEntry();
					}
				}
				zos.close();
				exportacio.setContingutDeploy(baos.toByteArray());
			} catch (Exception ex) {
				String errMsg = messageHelper.getMessage(
						"error.dissenyService.generantContingut.definicioProces", 
						new Object[] {definicio.getJbpmKey() + " v." + definicio.getVersio()});
				logger.error(errMsg, ex);
				throw new ExportException(errMsg, ex);
			}
		}
		// Tasques
		if (exportAll || command.getTasques().size() > 0)
			for (Tasca tasca : definicio.getTasques()) 
				if (exportAll || command.getTasques().contains(tasca.getJbpmName())) {
					TascaExportacio tascaExportacio = new TascaExportacio(
							tasca.getNom(),
							TascaDto.TipusTascaDto.valueOf(tasca.getTipus().toString()),
							tasca.getJbpmName());
					tascaExportacio.setMissatgeInfo(tasca.getMissatgeInfo());
					tascaExportacio.setMissatgeWarn(tasca.getMissatgeWarn());
					tascaExportacio.setNomScript(tasca.getNomScript());
					tascaExportacio.setExpressioDelegacio(tasca.getExpressioDelegacio());
					tascaExportacio.setRecursForm(tasca.getRecursForm());
					tascaExportacio.setFormExtern(tasca.getFormExtern());
					tascaExportacio.setTramitacioMassiva(tasca.isTramitacioMassiva());
					tascaExportacio.setFinalitzacioSegonPla(tasca.isFinalitzacioSegonPla());
					tascaExportacio.setAmbRepro(tasca.isAmbRepro());
					tascaExportacio.setMostrarAgrupacions(tasca.isMostrarAgrupacions());
					// Afegeix els camps de la tasca
					for (CampTasca camp: campTascaRepository.findAmbTascaIdOrdenats(tasca.getId(), definicio.getExpedientTipus().getId())) {
						tascaExportacio.addCamp(
								new CampTascaExportacio(
									camp.getCamp().getCodi(),
									camp.isReadFrom(),
									camp.isWriteTo(),
									camp.isRequired(),
									camp.isReadOnly(),
									camp.getOrder(),
									camp.getAmpleCols(),
									camp.getBuitCols(),
									camp.getCamp().getExpedientTipus() != null,
									definicio.getExpedientTipus() != null && definicio.getExpedientTipus().equals(camp.getExpedientTipus())));
					}
					// Afegeix els documents de la tasca
					for (DocumentTasca document: tasca.getDocuments()) {
						tascaExportacio.addDocument(
								new DocumentTascaExportacio(
										document.getDocument().getCodi(),
										document.isRequired(),
										document.isReadOnly(),
										document.getOrder(),
										document.getDocument().getExpedientTipus() != null,
										definicio.getExpedientTipus() != null && definicio.getExpedientTipus().equals(document.getExpedientTipus())));
					}
					// Afegeix les signatures de la tasca
					for (FirmaTasca firma: tasca.getFirmes()) {
						tascaExportacio.addFirma(
								new FirmaTascaExportacio(
										firma.getDocument().getCodi(),
										firma.isRequired(),
										firma.getOrder(),
										firma.getDocument().getExpedientTipus() != null,
										definicio.getExpedientTipus() != null && definicio.getExpedientTipus().equals(firma.getExpedientTipus())));
					}
					// Afegeix les validacions de la tasca
					for (Validacio validacio: tasca.getValidacions()) {
						tascaExportacio.addValidacio(
								new ValidacioExportacio(
										validacio.getNom(),
										validacio.getExpressio(),
										validacio.getMissatge(),
										validacio.getOrdre()));
					}
					exportacio.getTasques().add(tascaExportacio);
				}	
		// Variables
		if (exportAll || command.getVariables().size() > 0)
			for (Camp camp : definicio.getCamps()) 
				if (exportAll || command.getVariables().contains(camp.getCodi())) {
					boolean necessitaDadesExternes = 
							TipusCamp.SELECCIO.equals(camp.getTipus()) 
							|| TipusCamp.SUGGEST.equals(camp.getTipus());			
					boolean necessitaDadesExternesEntorn = 
							(camp.getDomini() != null && camp.getDomini().getExpedientTipus() == null)
							|| (camp.getEnumeracio() != null && camp.getEnumeracio().getExpedientTipus() == null);
					CampExportacio campExportacio = new CampExportacio(
		                    camp.getCodi(),
		                    CampTipusDto.valueOf(camp.getTipus().toString()),
		                    camp.getEtiqueta(),
		                    camp.getObservacions(),
		                    (necessitaDadesExternes) ? camp.getDominiId() : null,
		                    (necessitaDadesExternes) ? camp.getDominiParams() : null,
		                    (necessitaDadesExternes) ? camp.getDominiCampText() : null,
		                    (necessitaDadesExternes) ? camp.getDominiCampValor() : null,
		                    (necessitaDadesExternes) ? camp.getConsultaParams() : null,
		                    (necessitaDadesExternes) ? camp.getConsultaCampText() : null,
		                    (necessitaDadesExternes) ? camp.getConsultaCampValor() : null,
		                    camp.isMultiple(),
		                    camp.isOcult(),
		                    camp.getDominiIntern(),
		                    camp.isDominiCacheText(),
		                    (necessitaDadesExternes && camp.getEnumeracio() != null) ? camp.getEnumeracio().getCodi() : null,
		                    (necessitaDadesExternes && camp.getDomini() != null) ? camp.getDomini().getCodi() : null,
		                    (necessitaDadesExternes && camp.getConsulta() != null) ? camp.getConsulta().getCodi() : null,
		                    (camp.getAgrupacio() != null) ? camp.getAgrupacio().getCodi() : null,
		                    camp.getDefprocJbpmKey(),
		                    camp.getJbpmAction(),
		                    camp.getOrdre(),
		                    camp.isIgnored(),
		                    necessitaDadesExternesEntorn);
					exportacio.getCamps().add(campExportacio);
					// Afegeix les validacions del camp
					for (Validacio validacio: camp.getValidacions()) {
						campExportacio.addValidacio(new ValidacioExportacio(
								validacio.getNom(),
								validacio.getExpressio(),
								validacio.getMissatge(),
								validacio.getOrdre()));
					}
					// Afegeix els membres dels camps de tipus registre
					for (CampRegistre membre: camp.getRegistreMembres()) {
						campExportacio.addRegistreMembre(new RegistreMembreExportacio(
								membre.getMembre().getCodi(),
								membre.isObligatori(),
								membre.isLlistar(),
								membre.getOrdre()));
					}
				}
		// Agrupacions
		if (exportAll || command.getAgrupacions().size() > 0)
			for (CampAgrupacio agrupacio: definicio.getAgrupacions()) 
				if (exportAll || command.getAgrupacions().contains(agrupacio.getCodi()))
					exportacio.getAgrupacions().add(new AgrupacioExportacio(
							agrupacio.getCodi(),
							agrupacio.getNom(),
							agrupacio.getDescripcio(),
							agrupacio.getOrdre()));		
		// Documents
		if (exportAll || command.getDocuments().size() > 0) {
			DocumentExportacio documentExportacio;
			for (Document document : definicio.getDocuments())
				if (exportAll || command.getDocuments().contains(document.getCodi())) {
					documentExportacio = new DocumentExportacio(
							document.getCodi(),
							document.getNom(),
							document.getDescripcio(),
							document.getArxiuContingut(),
							document.getArxiuNom(),
							document.isPlantilla(),
							document.isNotificable());
					documentExportacio.setCustodiaCodi(document.getCustodiaCodi());
					documentExportacio.setContentType(document.getContentType());
					documentExportacio.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
					documentExportacio.setAdjuntarAuto(document.isAdjuntarAuto());
					if (document.getCampData() != null)
						documentExportacio.setCodiCampData(document.getCampData().getCodi());
					documentExportacio.setConvertirExtensio(document.getConvertirExtensio());
					documentExportacio.setExtensionsPermeses(document.getExtensionsPermeses());
					documentExportacio.setIgnored(document.isIgnored());
					documentExportacio.setNtiTipoDocumental(document.getNtiTipoDocumental());
					exportacio.getDocuments().add(documentExportacio);
				}
		}		
		// Terminis
		if (exportAll || command.getTerminis().size() > 0) {
			TerminiExportacio terminiExportacio;
			for (Termini termini : definicio.getTerminis())
				if (exportAll || command.getTerminis().contains(termini.getCodi())) {
					terminiExportacio = new TerminiExportacio(
							termini.getCodi(),
							termini.getNom(),
							termini.getDescripcio(),
							termini.isDuradaPredefinida(),
							termini.getAnys(),
							termini.getMesos(),
							termini.getDies(),
							termini.isLaborable(),
							termini.getDiesPrevisAvis(),
							termini.isAlertaPrevia(),
							termini.isAlertaFinal(),
							termini.isAlertaCompletat(),
							termini.isManual());
					exportacio.getTerminis().add(terminiExportacio);
				}
		}	
		// Accions
		if (exportAll || command.getAccions().size() > 0) {
			AccioExportacio accioExportacio;
			for (Accio accio : definicio.getAccions())
				if (exportAll || command.getAccions().contains(accio.getCodi())) {
					accioExportacio = new AccioExportacio(
							accio.getCodi(),
							accio.getNom(),
							accio.getDescripcio(),
							accio.getDefprocJbpmKey(),
							accio.getJbpmAction(),
							accio.isPublica(),
							accio.isOculta(),
							accio.getRols());
					exportacio.getAccions().add(accioExportacio);
				}
		}
		
		return exportacio;
	}

	/** Copia la informació de la definició de procés origen cap a la definició de procés
	 * destí.
	 * @param origenId
	 * @param destiId
	 */
	@Transactional
	public void copiarDefinicioProces(
			Long origenId, 
			Long destiId) {
		DefinicioProces origen = definicioProcesRepository.findOne(origenId);
		DefinicioProces desti =  definicioProcesRepository.findOne(destiId);

		// Propaga les agrupacions
		Map<String, CampAgrupacio> agrupacions = new HashMap<String, CampAgrupacio>();
		for (CampAgrupacio agrupacio: origen.getAgrupacions()) {
			CampAgrupacio nova = new CampAgrupacio(
					desti,
					agrupacio.getCodi(),
					agrupacio.getNom(),
					agrupacio.getOrdre());
			nova.setDescripcio(agrupacio.getDescripcio());
			campAgrupacioRepository.save(nova);
			agrupacions.put(agrupacio.getCodi(), nova);
		}
		// Propaga les accions
		for (Accio accio: origen.getAccions()) {
			Accio nova = new Accio(
					desti,
					accio.getCodi(),
					accio.getNom(),
					accio.getJbpmAction());
			nova.setDescripcio(accio.getDescripcio());
			nova.setOculta(accio.isOculta());
			nova.setPublica(accio.isPublica());
			nova.setRols(accio.getRols());
			accioRepository.save(nova);
		}
		// Propaga els camps
		Map<String, Camp> camps = new HashMap<String, Camp>();
		for (Camp camp: origen.getCamps()) {
			Camp nou = new Camp(
					desti,
					camp.getCodi(),
					camp.getTipus(),
					camp.getEtiqueta());
			nou.setObservacions(camp.getObservacions());
			nou.setMultiple(camp.isMultiple());
			nou.setOcult(camp.isOcult());
			nou.setDomini(camp.getDomini());
			nou.setDominiId(camp.getDominiId());
			nou.setDominiCampText(camp.getDominiCampText());
			nou.setDominiCampValor(camp.getDominiCampValor());
			nou.setDominiParams(camp.getDominiParams());
			nou.setDominiIntern(camp.getDominiIntern());
			nou.setEnumeracio(camp.getEnumeracio());
			nou.setJbpmAction(camp.getJbpmAction());
			nou.setOrdre(camp.getOrdre());
			nou.setIgnored(camp.isIgnored());
			campRepository.save(nou);
			camps.put(nou.getCodi(), nou);
			// Copia les validacions dels camps
			for (Validacio validacio: camp.getValidacions()) {
				Validacio novaValidacio = new Validacio(
						nou,
						validacio.getExpressio(),
						validacio.getMissatge());
				nou.addValidacio(novaValidacio);
			}
			// Configura l'agrupació
			if (camp.getAgrupacio() != null)
				nou.setAgrupacio(agrupacions.get(camp.getAgrupacio().getCodi()));
		}
		// Propaga els membres dels camps de tipus registre
		for (Camp camp: origen.getCamps()) {
			if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
				for (CampRegistre membre: camp.getRegistreMembres()) {
					CampRegistre campRegistre = new CampRegistre(
							camps.get(camp.getCodi()),
							camps.get(membre.getMembre().getCodi()),
							membre.getOrdre());
					campRegistre.setLlistar(membre.isLlistar());
					campRegistre.setObligatori(membre.isObligatori());
					campRegistreRepository.save(campRegistre);
				}
			}
		}
		// Propaga els documents
		Map<String, Document> documents = new HashMap<String, Document>();
		for (Document document: origen.getDocuments()) {
			Document nou = new Document(
					desti,
					document.getCodi(),
					document.getNom());
			documentRepository.save(nou);
			nou.setDescripcio(document.getDescripcio());
			nou.setArxiuNom(document.getArxiuNom());
			nou.setArxiuContingut(document.getArxiuContingut());
			nou.setPlantilla(document.isPlantilla());
			nou.setCustodiaCodi(document.getCustodiaCodi());
			nou.setContentType(document.getContentType());
			nou.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
			nou.setAdjuntarAuto(document.isAdjuntarAuto());
			nou.setConvertirExtensio(document.getConvertirExtensio());
			nou.setIgnored(document.isIgnored());
			if (document.getCampData() != null)
				nou.setCampData(camps.get(document.getCampData().getCodi()));
			documentRepository.save(nou);
			documentRepository.flush();
			documents.put(nou.getCodi(), nou);
		}
		// Propaga els terminis
		for (Termini termini: origen.getTerminis()) {
			Termini nou = new Termini(
					desti,
					termini.getCodi(),
					termini.getNom(),
					termini.getAnys(),
					termini.getMesos(),
					termini.getDies(),
					termini.isLaborable());
			nou.setDescripcio(termini.getDescripcio());
			nou.setDiesPrevisAvis(termini.getDiesPrevisAvis());
			nou.setAlertaPrevia(termini.isAlertaPrevia());
			nou.setAlertaFinal(termini.isAlertaFinal());
			nou.setAlertaCompletat(termini.isAlertaCompletat());
			terminiRepository.save(nou);
		}
		// Propaga les dades de les tasques
		Map<String, Tasca> tasquesOrigen = new HashMap<String, Tasca>();
		Tasca tascaOrigen;
		for (Tasca t : origen.getTasques())
			tasquesOrigen.put(t.getJbpmName(), t);
		for (Tasca tascaDesti : desti.getTasques()) {
			tascaOrigen = tasquesOrigen.get(tascaDesti.getJbpmName());
			if (tascaOrigen != null) {
				tascaDesti.setNom(tascaOrigen.getNom());
				tascaDesti.setTipus(tascaOrigen.getTipus());
				tascaDesti.setMissatgeInfo(tascaOrigen.getMissatgeInfo());
				tascaDesti.setMissatgeWarn(tascaOrigen.getMissatgeWarn());
				tascaDesti.setNomScript(tascaOrigen.getNomScript());
				tascaDesti.setRecursForm(tascaOrigen.getRecursForm());
				tascaDesti.setFormExtern(tascaOrigen.getFormExtern());
				tascaDesti.setExpressioDelegacio(tascaOrigen.getExpressioDelegacio());
				tascaDesti.setTramitacioMassiva(tascaOrigen.isTramitacioMassiva());
				tascaDesti.setFinalitzacioSegonPla(tascaOrigen.isFinalitzacioSegonPla());
				tascaDesti.setAmbRepro(tascaOrigen.isAmbRepro());
				tascaDesti.setMostrarAgrupacions(tascaOrigen.isMostrarAgrupacions());
				// Copia els camps de les tasques
				for (CampTasca camp: tascaOrigen.getCamps()) {
					CampTasca nouCamp = new CampTasca(
							camp.getCamp().getExpedientTipus() != null?
									camp.getCamp()	// Camp a nivell de TE
									: camps.get(camp.getCamp().getCodi()), // Camp de la definició de procés
							tascaDesti,
							camp.isReadFrom(),
							camp.isWriteTo(),
							camp.isRequired(),
							camp.isReadOnly(),
							camp.getOrder(),
							camp.getAmpleCols(),
							camp.getBuitCols(),
							camp.getExpedientTipus());
					tascaDesti.addCamp(nouCamp);
				}
				// Copia els documents de la tasca
				for (DocumentTasca document: tascaOrigen.getDocuments()) {
					DocumentTasca nouDocument = new DocumentTasca(
							document.getDocument().getExpedientTipus() != null?
									document.getDocument()	// document a nivell de TE
									: documents.get(document.getDocument().getCodi()), // document de la definició de procés
							tascaDesti,
							document.isRequired(),
							document.isReadOnly(),
							document.getOrder(),
							document.getExpedientTipus());
					tascaDesti.addDocument(nouDocument);
				}
				// Copia les firmes de la tasca
				for (FirmaTasca firma: tascaOrigen.getFirmes()) {
					FirmaTasca novaFirma = new FirmaTasca(
							firma.getDocument().getExpedientTipus() != null?
									firma.getDocument()	// document a nivell de TE
									: documents.get(firma.getDocument().getCodi()), // document de la definició de procés
							tascaDesti,
							firma.isRequired(),
							firma.getOrder(),
							firma.getExpedientTipus());
					tascaDesti.addFirma(novaFirma);
				}
				// Copia les validacions de la tasca
				for (Validacio validacio: tascaOrigen.getValidacions()) {
					Validacio novaValidacio = new Validacio(
							tascaDesti,
							validacio.getExpressio(),
							validacio.getMissatge());
					tascaDesti.addValidacio(novaValidacio);
				}
				tascaRepository.save(tascaDesti);
			}
		}		
	}
	
	/** Cerca la darrera versió de la definició de procés per codi en:
	 * 1- Definicions de procés associades a l'expedient
	 * 2- Definicions de procés heretades
	 * 3- Definicions de procés de l'entorn
	 * Així doncs aquest mètode té en compte l'herència, la sobreescriptura i les definicions
	 * de procés globals.
	 * @param expedientTipus
	 * @param jbpmKey
	 * @return
	 */
	public DefinicioProces findDarreraVersioDefinicioProces(
			ExpedientTipus expedientTipus, 
			String jbpmKey) {
		DefinicioProces definicioProces = null;
		if (expedientTipus != null) {
			// Cerca la darrera versió de la definició de procés per codi pel tipus d'expedient
			definicioProces = definicioProcesRepository.findDarreraVersioAmbTipusExpedientIJbpmKey(
					expedientTipus.getId(),
					jbpmKey);
			// Si no la troba i hi ha herència la cerca al pare
			if (definicioProces == null && expedientTipus.getExpedientTipusPare() != null)
				definicioProces = definicioProcesRepository.findDarreraVersioAmbTipusExpedientIJbpmKey(
						expedientTipus.getExpedientTipusPare().getId(),
						jbpmKey);
		}
		// Si no la trova cerca a l'entorn
		if (definicioProces == null)
			definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
					expedientTipus.getEntorn().getId(),
					jbpmKey);
		return definicioProces;
	}
	
	public List<DefinicioProces> findVersionsDefinicioProces(Long entornId, ExpedientTipus expedientTipus, String jbpmKey) {
		List<DefinicioProces> versions = null;
		
		if (expedientTipus != null) {
			// Relacionades amb el tipus d'expedient
			versions = definicioProcesRepository.findByExpedientTipusIJpbmKey( expedientTipus.getId(), 
																jbpmKey);
			// Heretades
			if ((versions == null || versions.isEmpty()) && expedientTipus.getExpedientTipusPare() != null)
				versions = definicioProcesRepository.findByExpedientTipusIJpbmKey( expedientTipus.getExpedientTipusPare().getId(), 
																jbpmKey);
		}
		// De l'entorn
		if ((versions == null || versions.isEmpty()))
			versions = definicioProcesRepository.findByEntornIJbpmKey(entornId,
																	jbpmKey);
		return versions;
	}


	/** Cerca les darreres versins de les definicions de procés per un tipus d'expedient en:
	 * 1- Definicions de procés associades a l'expedient
	 * 2- Definicions de procés heretades
	 * 3- Definicions de procés de l'entorn
	 * Així doncs aquest mètode té en compte l'herència, la sobreescriptura i les definicions
	 * de procés globals.
	 * @param entornId
	 * @param expedientTipus a partir del qual se cercaran totes les definicions de procés.
	 * @return Una llista amb les darreres versions de defincions de procés.
	 */
	public List<DefinicioProces> findAllDarreraVersio(Long entornId, ExpedientTipus expedientTipus) {
		Map<String, DefinicioProces> definicionsProces = new TreeMap<String, DefinicioProces>();
		// Cerca les darreres versions de definicions de procés al tipus d'expedient
		for (DefinicioProces dp : definicioProcesRepository.findDarreresVersionsBy(
				expedientTipus.getEntorn().getId(), 
				false, 
				expedientTipus.getId(),
				false))
			if (!definicionsProces.containsKey(dp.getJbpmKey()))
				definicionsProces.put(dp.getJbpmKey(), dp);
		// Cerca les definicions de procés heretades
		if (expedientTipus.getExpedientTipusPare() != null)
			for (DefinicioProces dp : definicioProcesRepository.findDarreresVersionsBy(
					expedientTipus.getEntorn().getId(), 
					false, 
					expedientTipus.getExpedientTipusPare().getId(), 
					false))
				if (!definicionsProces.containsKey(dp.getJbpmKey()))
					definicionsProces.put(dp.getJbpmKey(), dp);
		// Cerca les definicions de procés de l'entorn
		for (DefinicioProces dp : definicioProcesRepository.findDarreresVersionsBy(
				expedientTipus.getEntorn().getId(), 
				false, 
				0L, 
				true))
			if (!definicionsProces.containsKey(dp.getJbpmKey()))
				definicionsProces.put(dp.getJbpmKey(), dp);

		// Retorna totes les definicions de procés trobades
		return new ArrayList<DefinicioProces>(definicionsProces.values());
	}

	/** Mètode per retornar una mapeig de definicions de procés i informació de tasques heretada pel tipus d'expedient
	 * passat com a paràmetre-
	 * 
	 * @param expedientTipus
	 * @return
	 */
	@Transactional(readOnly = true)
	public Map<String, List<TascaExportacio>> getHerenciaTasques(ExpedientTipus expedientTipus) {
		
		Map<String, List<TascaExportacio>> herenciaTasques = new HashMap<String, List<TascaExportacio>>();
		if (expedientTipus.getExpedientTipusPare() != null) {
			// Per guardar les darreres tasques per ID
			Map<Long, TascaExportacio> tascaMap = new HashMap<Long, TascaExportacio>();
			TascaExportacio tascaExportacio;
			// Troba tots els camps de tasca associats al tipus d'expedient
			for (CampTasca campTasca : campTascaRepository.findAllByExpedientTipus(expedientTipus)) {
				if (isTascaHeretada(campTasca.getTasca(), expedientTipus)
						&& expedientTipus.equals(campTasca.getExpedientTipus())) {
					// Afegeix tots els camps que calguin
					tascaExportacio = this.getHerenciaTascaExportacio(tascaMap, herenciaTasques, campTasca.getTasca());
					tascaExportacio.addCamp(new CampTascaExportacio(
													campTasca.getCamp().getCodi(),
													campTasca.isReadFrom(),
													campTasca.isWriteTo(),
													campTasca.isRequired(),
													campTasca.isReadOnly(),
													campTasca.getOrder(),
													campTasca.getAmpleCols(),
													campTasca.getBuitCols(),
													true,
													campTasca.getCamp().getExpedientTipus().getId().equals(expedientTipus.getId())));
				}
			}
			// Troba tots els documents de tasca assocats al tipus d'expedient
			for (DocumentTasca documentTasca : documentTascaRepository.findAllByExpedientTipus(expedientTipus)) {
				if (isTascaHeretada(documentTasca.getTasca(), expedientTipus)
						&& expedientTipus.equals(documentTasca.getExpedientTipus())) {
					// Afegeix tots els camps que calguin
					tascaExportacio = this.getHerenciaTascaExportacio(tascaMap, herenciaTasques, documentTasca.getTasca());
					tascaExportacio.addDocument(new DocumentTascaExportacio(
													documentTasca.getDocument().getCodi(),
													documentTasca.isRequired(),
													documentTasca.isReadOnly(),
													documentTasca.getOrder(),
													true,
													documentTasca.getDocument().getExpedientTipus().getId().equals(expedientTipus.getId())));
				}
			}
			// Troba totes les firmes associades al tipus d'expedient
			for (FirmaTasca firmaTasca : firmaTascaRepository.findAllByExpedientTipus(expedientTipus)) {
				if (isTascaHeretada(firmaTasca.getTasca(), expedientTipus)
						&& expedientTipus.equals(firmaTasca.getExpedientTipus())) {
					// Afegeix tots els camps que calguin
					tascaExportacio = this.getHerenciaTascaExportacio(tascaMap, herenciaTasques, firmaTasca.getTasca());
					tascaExportacio.addFirma(new FirmaTascaExportacio(
													firmaTasca.getDocument().getCodi(),
													firmaTasca.isRequired(),
													firmaTasca.getOrder(),
													true,
													firmaTasca.getDocument().getExpedientTipus().getId().equals(expedientTipus.getId())));
				}
			}
			
		}
		return herenciaTasques;
	}
	
	/** Mètode per obtenir del map de darreres tasques exportades la que es correspon amb la passada com a paràmetre. Si no està,
	 * llavors la crea.
	 * @param tascaMap Map de les darreres tasques d'exportació identificades pel seu ID
	 * @param herenciaTasques Llista de tasques d'exportació per retornar.
	 * @param tasca Tasca per comprovar.
	 * @return
	 */
	private TascaExportacio getHerenciaTascaExportacio(Map<Long, TascaExportacio> tascaMap, Map<String, List<TascaExportacio>> herenciaTasques, Tasca tasca) {
		TascaExportacio tascaExportacio = tascaMap.get(tasca.getId());
		if (tascaExportacio == null) {
			// Crea la tasca exportació
			tascaExportacio = new TascaExportacio(
									tasca.getNom(),
									TascaDto.TipusTascaDto.valueOf(tasca.getTipus().toString()),
									tasca.getJbpmName());
			// La afegeix al map de tasques utilitzades recentment
			tascaMap.put(tasca.getId(), tascaExportacio);
			// La afegeix al map per retornar
			if (!herenciaTasques.containsKey(tasca.getDefinicioProces().getJbpmKey()))
				herenciaTasques.put(tasca.getDefinicioProces().getJbpmKey(), new ArrayList<TascaExportacio>());
			herenciaTasques.get(tasca.getDefinicioProces().getJbpmKey()).add(tascaExportacio);
		}
		return tascaExportacio;
	}

	/** Mètode per determinar si la definició de procés passada per paràmetre està heretada pel tipus d'expedient.
	 *  Està heretada si:
	 *  - El tipus d'expedient de la definició de procés és el tipus d'expedient pare del tipus d'expedient.
	 *  
	 * @param definicioProces Definició de procés a comprovar si està heretada.
	 * @param expedientTipus Expedient tipus a comprovar si hereta la definició de procés.
	 * @return
	 */
	public boolean isDefinicioProcesHeretada(DefinicioProces definicioProces, ExpedientTipus expedientTipus) {

		boolean heretada = expedientTipus != null 
				&& expedientTipus.isAmbInfoPropia() 
				&& expedientTipus.getExpedientTipusPare() != null
				&& definicioProces != null
				&& definicioProces.getExpedientTipus() != null
				&& definicioProces.getExpedientTipus().getId().equals(expedientTipus.getExpedientTipusPare().getId());
		return heretada;
	}

	/** Mètode per determinar si el camp passat per paràmetre està heretat pel tipus d'expedient.
	 * Està heretat si:
	 * - El tipus d'expedient del camp és igual al tipus d'expedient pare de l'expedient tippus passat com a paràmetre.
	 * @param camp
	 * @param expedientTipus
	 * @return
	 */
	public boolean isCampHeretat(Camp camp, ExpedientTipus expedientTipus) {
		return camp.getExpedientTipus() != null 
				&& expedientTipus != null
				&& expedientTipus.isAmbHerencia()
				&& camp.getExpedientTipus().getId().equals(expedientTipus.getExpedientTipusPare().getId());
	}
	
	/** Mètode per determinar si el document passat per paràmetre està heretat pel tipus d'expedient.
	 * Està heretat si:
	 * - El tipus d'expedient del document és igual al tipus d'expedient pare de l'expedient tippus passat com a paràmetre.
	 * @param document
	 * @param expedientTipus
	 * @return
	 */
	public boolean isDocumentHeretat(Document document, ExpedientTipus expedientTipus) {
		return document.getExpedientTipus() != null 
				&& expedientTipus != null
				&& expedientTipus.isAmbHerencia()
				&& document.getExpedientTipus().getId().equals(expedientTipus.getExpedientTipusPare().getId());
	}	
	
	/** Mètode per determinar si la tasca passada per paràmetre està heretada pel tipus d'expedient.
	 *  Està heretada si:
	 *  - El tipus d'expedient de la definició de procés de la tasca és el tipus d'expedient pare del tipus d'expedient.
	 *  
	 * @param tasca Tasca a comprovar si està heretada.
	 * @param expedientTipus Expedient tipus a comprovar si hereta la definició de procés.
	 * @return
	 */
	public boolean isTascaHeretada(Tasca tasca, ExpedientTipus expedientTipus) {

		boolean heretada = this.isDefinicioProcesHeretada(tasca.getDefinicioProces(), expedientTipus);
		return heretada;
	}
	
	/** Mètode per determinar si el camp de la tasca està heretat pel tipus d'expedient.
	 * Està heretat si:
	 * - La tasca està heretada i el camp de la tasca no pertany a cap altra tipus d'expedient que no sigui el tipus d'expedient pare.
	 * 
	 * @param campTasca
	 * @param expedientTipus
	 * @return
	 */
	public boolean isCampTascaHeretat(CampTasca campTasca, ExpedientTipus expedientTipus) {
		boolean heretat = this.isTascaHeretada(campTasca.getTasca(), expedientTipus)
							&& (campTasca.getExpedientTipus() == null || campTasca.getExpedientTipus().getId().equals(expedientTipus.getExpedientTipusPare().getId()));
		return heretat;
	}
	
	/** Mètode per determinar si el document de la tasca està heretat pel tipus d'expedient.
	 * Està heretat si:
	 * - La tasca està heretada i el document de la tasca no pertany a cap altra tipus d'expedient que no sigui el tipus d'expedient pare.
	 * 
	 * @param documentTasca
	 * @param expedientTipus
	 * @return
	 */
	public boolean isDocumentTascaHeretat(DocumentTasca documentTasca, ExpedientTipus expedientTipus) {
		boolean heretat = this.isTascaHeretada(documentTasca.getTasca(), expedientTipus)
							&& (documentTasca.getExpedientTipus() == null || documentTasca.getExpedientTipus().getId().equals(expedientTipus.getExpedientTipusPare().getId()));
		return heretat;
	}
	
	/** Mètode per determinar si la firma de la tasca està heretada pel tipus d'expedient.
	 * Està heretada si:
	 * - La tasca està heretada i la firma de la tasca no pertany a cap altra tipus d'expedient que no sigui el tipus d'expedient pare.
	 * 
	 * @param firmaTasca
	 * @param expedientTipus
	 * @return
	 */
	public boolean isFirmaTascaHeretada(FirmaTasca firmaTasca, ExpedientTipus expedientTipus) {
		boolean heretat = this.isTascaHeretada(firmaTasca.getTasca(), expedientTipus)
							&& (firmaTasca.getExpedientTipus() == null || firmaTasca.getExpedientTipus().getId().equals(expedientTipus.getExpedientTipusPare().getId()));
		return heretat;
	}

	/** Mètode helper per incorporar la informació de les tasques per a un tipus d'expedient que hereta d'un tipus d'expedient pare. Importa
	 * la informació de les tasques d'una definició de procés concreta del tipus d'expedient pare.
	 * 
	 * @param expedientTipus
	 * @param expedientTipusPare
	 * @param definicioProcesJbpmkey
	 * @param tasquesExportacio
	 */
	@Transactional
	public void importarTascaHerencia(
			ExpedientTipus expedientTipus, 
			ExpedientTipus expedientTipusPare, 
			String definicioProcesJbpmkey, 
			List<TascaExportacio> tasquesExportacio) {
		// Troba la definició de procés
		DefinicioProces definicioProces = this.findDarreraVersioDefinicioProces(expedientTipusPare, definicioProcesJbpmkey);
		
		// Per cada tasca
		Tasca tasca;
		for (TascaExportacio tascaExportacio : tasquesExportacio) {
			// Troba la tasca
			tasca = tascaRepository.findByJbpmNameAndDefinicioProces(tascaExportacio.getJbpmName(), definicioProces);
			if (tasca == null)
				throw new DeploymentException(messageHelper.getMessage(
						"exportar.validacio.tasca.no.trobada", 
						new Object[] {tascaExportacio.getJbpmName(), definicioProces.getIdPerMostrar()}));
			// Per cada camp de la exportació
			// Camps de la tasca
			for (CampTascaExportacio campExportat : tascaExportacio.getCamps()) {
				// Primer mira si ja està relacionat, si no l'afegeix
				CampTasca campTasca = null;
				for (CampTasca ct : tasca.getCamps())
					if (ct.getCamp().getCodi().equals(campExportat.getCampCodi())) {
						campTasca = ct;
						break;
					}
				if (campTasca == null) {
					campTasca = new CampTasca();
					campTasca.setTasca(tasca);
					tasca.getCamps().add(campTasca);
				}
				// Actualitza la informació
				campTasca.setExpedientTipus(expedientTipus);
				campTasca.setOrder(campExportat.getOrder());	
				campTasca.setAmpleCols(campExportat.getAmpleCols());
				campTasca.setBuitCols(campExportat.getBuitCols());
				campTasca.setReadFrom(campExportat.isReadFrom());
				campTasca.setWriteTo(campExportat.isWriteTo());
				campTasca.setRequired(campExportat.isRequired());
				campTasca.setReadOnly(campExportat.isReadOnly());
				// Relaciona el camp tasca amb el camp
				this.relacionarCampTasca(
						campTasca, 
						campExportat.getCampCodi(), 
						campExportat.isTipusExpedient(),
						expedientTipus, 
						definicioProces);
				campTascaRepository.save(campTasca);
			}			
			
			// Documents de la tasca
			for (DocumentTascaExportacio documentExportat : tascaExportacio.getDocuments()) {
				// Primer mira si ja està relacionat, si no l'afegeix
				DocumentTasca documentTasca = null;
				for (DocumentTasca dt : tasca.getDocuments())
					if (dt.getDocument().getCodi().equals(documentExportat.getDocumentCodi())) {
						documentTasca = dt;
						break;
					}
				if (documentTasca == null) {
					documentTasca = new DocumentTasca();
					documentTasca.setTasca(tasca);
					tasca.getDocuments().add(documentTasca);	
				}
				// Actualitza la informació
				documentTasca.setRequired(documentExportat.isRequired());
				documentTasca.setReadOnly(documentExportat.isReadOnly());
				documentTasca.setOrder(documentExportat.getOrder());
				// Relaciona el camp tasca amb el camp
				this.relacionarDocumentTasca(
						documentTasca, 
						documentExportat.getDocumentCodi(), 
						documentExportat.isTipusExpedient(),
						expedientTipus, 
						definicioProces);
				documentTascaRepository.save(documentTasca);
			}			

			// Documents de la tasca
			for (FirmaTascaExportacio firmaExportat : tascaExportacio.getFirmes()) {
				// Primer mira si ja està relacionat, si no l'afegeix
				FirmaTasca firmaTasca = null;
				for (FirmaTasca ft : tasca.getFirmes())
					if (ft.getDocument().getCodi().equals(firmaExportat.getDocumentCodi())) {
						firmaTasca = ft;
						break;
					}
				if (firmaTasca == null) {
					firmaTasca = new FirmaTasca();
					firmaTasca.setTasca(tasca);
					tasca.getFirmes().add(firmaTasca);	
				}
				// Actualitza la informació
				firmaTasca.setRequired(firmaExportat.isRequired());
				firmaTasca.setOrder(firmaExportat.getOrder());
				// Relaciona el camp tasca amb el camp
				this.relacionarFirmaTasca(
						firmaTasca, 
						firmaExportat.getDocumentCodi(), 
						firmaExportat.isTipusExpedient(),
						expedientTipus, 
						definicioProces);
				firmaTascaRepository.save(firmaTasca);
			}			

		}
	}

	/** Mètode per relacionar la crida a les subdefinicions de procés per tal que es cridi la versió correcta en els nodes de tipus processState.
	 * Només s'actualitzen entre elles les darreres versions de les definicions de procés, d'aquesta forma les versions anteriors queden de la mateixa
	 * manera.
	 * 
	 * @param definicionsProces Llista de definicions de procés a relacionar les unes amb les altres i elles mateixes.
	 */
	@Transactional
	public void relacionarDarreresVersionsDefinicionsProces(Set<DefinicioProces> definicionsProces) {
		// Revisa les definicions de procés
		Map<String, DefinicioProces> darreresDefinicionsProces = new HashMap<String, DefinicioProces>();
		DefinicioProces aux;
		for (DefinicioProces dp: definicionsProces) {
			aux = darreresDefinicionsProces.get(dp.getJbpmKey());
			if (aux == null || (aux.getVersio() < dp.getVersio()))
				darreresDefinicionsProces.put(dp.getJbpmKey(), dp);
		}
		// Relaciona les darreres
		for (DefinicioProces dp1: darreresDefinicionsProces.values())
			for (DefinicioProces dp2 : darreresDefinicionsProces.values())
				workflowEngineApi.updateSubprocessDefinition(
						workflowEngineApi.getProcessDefinition(null, dp1.getJbpmId()), //.getProcessDefinition(),
						workflowEngineApi.getProcessDefinition(null, dp2.getJbpmId())); //.getProcessDefinition());
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DefinicioProcesHelper.class);
}
