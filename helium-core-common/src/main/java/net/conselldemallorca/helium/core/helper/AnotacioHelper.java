/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultat;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtils;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;
import es.caib.plugins.arxiu.caib.ArxiuConversioHelper;
import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Alerta.AlertaPrioritat;
import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioAnnex;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioInteressat;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogEstat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Interessat;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioAnnexEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioMapeigResultatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuEstat;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalTipus;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.repository.AnotacioAnnexRepository;
import net.conselldemallorca.helium.v3.core.repository.AnotacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusUnitatOrganitzativaRepository;
import net.conselldemallorca.helium.v3.core.repository.InteressatRepository;
import net.conselldemallorca.helium.v3.core.repository.MapeigSistraRepository;


/**
 * Helper per a gestionar els expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AnotacioHelper {
	@Resource
	private AnotacioRepository anotacioRepository;
	@Resource
	private AnotacioAnnexRepository anotacioAnnexRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private InteressatRepository interessatRepository;	
	@Resource
	private MapeigSistraRepository mapeigSistraRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private ExpedientTipusUnitatOrganitzativaRepository expedientTipusUnitatOrganitzativaRepository;
	
	@Resource
	private EntornHelper entornHelper;
	@Autowired
	private DistribucioHelper distribucioHelper;
	@Resource(name = "documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private ExceptionHelper exceptionHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource(name = "permisosHelperV3") 
	private PermisosHelper permisosHelper;
	@Resource
	private UsuariActualHelper usuariActualHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private AlertaHelper alertaHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private ExpedientDadaHelper expedientDadaHelper;
	@Resource
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	
	@Transactional
	public AnotacioDto incorporarReprocessarExpedient(
			Anotacio anotacio,
			Long anotacioId, 
			Long expedientTipusId, 
			Long expedientId, 
			boolean associarInteressats,
			boolean comprovarPermis,
			boolean reprocessar,
			BackofficeArxiuUtils backofficeUtils) {
		
		logger.debug(
				"Incorporant la petició d'anotació de registre a un expedient(" +
				"anotacioId=" + anotacioId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"expedientId=" + expedientId + ", " +
				"associarInteressats=" + associarInteressats + ", " +
				"comprovarPermis=" + comprovarPermis + ")");
				
		if(anotacio==null) {
			anotacio = anotacioRepository.findOne(anotacioId);
		}
		// Comprova els permisos
		if (comprovarPermis)
			this.comprovaPermisAccio(anotacio);
		// Comprova que està  pendent
		if (! AnotacioEstatEnumDto.PENDENT.equals(anotacio.getEstat())) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador() + " no es pot actualitzar perquè està en estat " + anotacio.getEstat());
		}

		// Recupera la informació del tipus d'expedient i l'expedient
		ExpedientTipus expedientTipus = null;
		Expedient expedient = null;
		expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		expedient = expedientRepository.findOne(expedientId);
		if (expedientTipus == null || expedient == null)
			throw new RuntimeException("No es pot incorporar l'anotació " + 
						anotacioId + " a l'expedient " + expedientId + " i tipus d'expedient " + 
						expedientTipusId + " perquè no s'ha pogut recuperar la informació associada.");

		if(reprocessar && expedientTipus.isDistribucioSistra()) {
			AnotacioMapeigResultatDto resultatMapeig = this.reprocessarMapeigAnotacioExpedient(expedientId, anotacioId);
			if (resultatMapeig.isError()) {
				Alerta alerta = alertaHelper.crearAlerta(
						expedient.getEntorn(), 
						expedient, 
						new Date(), 
						null, 
						resultatMapeig.getMissatgeAlertaErrors());
				alerta.setPrioritat(AlertaPrioritat.ALTA);	
			}
		}
		anotacio.setExpedientTipus(expedientTipus);
		anotacio.setExpedient(expedient);
		
		// Si l'anotació té annexos invàlids o en estat esborrany posa una alerta a l'expedient
		int nAnnexosInvalids = 0;
		int nAnnexosEsborrany = 0;
		for (AnotacioAnnex annex : anotacio.getAnnexos()) {
			if (!annex.isDocumentValid()) {
				nAnnexosInvalids++;
			}
			if (annex.getArxiuEstat() == ArxiuEstat.ESBORRANY) {
				nAnnexosEsborrany++;
			}
		}
		if (nAnnexosInvalids > 0 ) {
			Alerta alerta = alertaHelper.crearAlerta(
					expedient.getEntorn(), 
					expedient, 
					new Date(), 
					null, 
					"L'anotació " + anotacio.getIdentificador() + " té " + nAnnexosInvalids + " annexos invàlids que s'haurien de revisar i reparar.");
			alerta.setPrioritat(AlertaPrioritat.ALTA);
		}

		if (nAnnexosEsborrany > 0 ) {
			Alerta alerta = alertaHelper.crearAlerta(
					expedient.getEntorn(), 
					expedient, 
					new Date(), 
					null, 
					"L'anotació " + anotacio.getIdentificador() + " té " + nAnnexosEsborrany + " en estat esborrany.");
			alerta.setPrioritat(AlertaPrioritat.ALTA);
		}

		// Si l'expedient està  integrat amb l'arxiu s'utlitzarà la llibreria d'utilitats de backoffice de DistribuciÃ³ per moure tots els annexos i incorporar
		// la informació dels interessats.
		ArxiuResultat resultat = null;
		if (expedient.isArxiuActiu()) {

			// Utilitza la llibreria d'utilitats de Distribució per incorporar la informació de l'anotació directament a l'expedient dins l'Arxiu
			es.caib.plugins.arxiu.api.Expedient expedientArxiu = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid());
			// Posarà  els annexos en la carpeta de l'anotació
			backofficeUtils.setCarpeta(ArxiuConversioHelper.revisarContingutNom(anotacio.getIdentificador().replace("/", "_")));
			// S'enregistraran els events al monitor d'integració
			// Consulta la informació de l'anotació
			AnotacioRegistreEntrada anotacioRegistreEntrada = null;
			es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId idWs = new AnotacioRegistreId();
			try {
				idWs.setClauAcces(anotacio.getDistribucioClauAcces());
				idWs.setIndetificador(anotacio.getDistribucioId());
				anotacioRegistreEntrada = distribucioHelper.consulta(idWs);
				
			} catch(Exception e) {
				// Error no controlat consultant la informació de l'expedient, es posa una alerta
				String errMsg = "Error consultant la informació de l'anotació " + 
						anotacio.getIdentificador() + " a l'hora d'incorporar la anotació a l'expedient, és necessari reintentar el processament dels annexos.";
				logger.error(errMsg, e);
				String errorProcessament = "Error processant l'anotació " + anotacio.getIdentificador() + ":" + e;
				// Crida fent referència al bean per crear una nova transacció
				distribucioHelper.updateErrorProcessament(anotacio.getId(), errorProcessament);
				distribucioHelper.canviEstatErrorAnotacio(errorProcessament, anotacio, idWs, e);

				Alerta alerta = alertaHelper.crearAlerta(
						expedient.getEntorn(), 
						expedient, 
						new Date(), 
						null, 
						errMsg);
				alerta.setPrioritat(AlertaPrioritat.ALTA);
				resultat = new ArxiuResultat();
				for (AnotacioAnnex annex : anotacio.getAnnexos()) {
					annex.setEstat(AnotacioAnnexEstatEnumDto.PENDENT);
					annex.setError(errMsg);
				}
			}
			// Processa la informació amb la llibreria d'utilitats per moure els annexos
			if (anotacioRegistreEntrada != null) {
				resultat = backofficeUtils.crearExpedientAmbAnotacioRegistre(expedientArxiu, anotacioRegistreEntrada);
				if (resultat.getErrorCodi() != 0) {
					// Error en el processament
					String errMsg = "S'han produit errors processant l'anotació de Distribucio \"" + anotacio.getIdentificador() + "\" amb la llibreria de distribucio-backoffice-utils: " + resultat.getErrorCodi() + " " + resultat.getErrorMessage();
					logger.error(errMsg, resultat.getException());
					Alerta alerta = alertaHelper.crearAlerta(
							expedient.getEntorn(), 
							expedient, 
							new Date(), 
							null, 
							errMsg + ". Es necessari reintentar el processament.");
					alerta.setPrioritat(AlertaPrioritat.ALTA);
				}
			}
		} else {
			resultat = new ArxiuResultat();
		}
		
		// Si no s'integra amb Sistra2
		for ( AnotacioAnnex annex : anotacio.getAnnexos() ) {			
			// Incorpora cada annex de forma separada per evitar excepcions i continuar amb els altres
			// Si no s'integra amb Sistra crea un document per annex incorportat correctament
			distribucioHelper.incorporarAnnex(
					expedientTipus.isDistribucioSistra(),
					expedient, 
					anotacio, 
					annex, 
					resultat);
		}
		
		if (associarInteressats) {
			// Incorpora els interessats a l'expedient
			Interessat interessatEntity;
			for(AnotacioInteressat interessat : anotacio.getInteressats()) {
				// Comprova si ja existeix
				interessatEntity = interessatRepository.findByCodiAndExpedient(
						interessat.getDocumentNumero(), expedient);
				if (interessatEntity == null) {
					// Crea el nou interessat
					logger.debug("Creant l'interessat (interessat=" + interessat + ") a l'expedient " + expedient.getIdentificador());
					interessatEntity = new Interessat(
							interessat.getId(),
							interessat.getDocumentNumero(), // Codi
							interessat.getNom() != null? interessat.getNom() : interessat.getRaoSocial(),
							interessat.getDocumentNumero(),
							interessat.getOrganCodi(), //codiDir3
							interessat.getLlinatge1(), 
							interessat.getLlinatge2(), 
							this.getInteressatTipus(interessat),
							interessat.getEmail(), 
							interessat.getTelefon(),
							expedient,
							false, //interessat.getAdresa() != null && interessat.getCp() != null, // entregaPostalActiva o el nom correcte de la propietat //Forcem false issue #1675
							EntregaPostalTipus.SENSE_NORMALITZAR,
							interessat.getAdresa(), // adreça línia 1
							null, // linia2,
							interessat.getCp(),
							false, // entregaDeh,
							false // entregaDehObligat
							);
				} else {
					// Actualitza l'interessat existent
					logger.debug("Modificant l'interessat (interessat=" + interessat + ") a l'expedient " + expedient.getIdentificador());
					interessatEntity.setNom(interessat.getNom() != null? interessat.getNom() : interessat.getRaoSocial());
					interessatEntity.setNif(interessat.getDocumentNumero());
					interessatEntity.setLlinatge1(interessat.getLlinatge1());  
					interessatEntity.setLlinatge2(interessat.getLlinatge2());
					interessatEntity.setTipus(this.getInteressatTipus(interessat));
					interessatEntity.setEmail(interessat.getEmail());
					interessatEntity.setTelefon(interessat.getTelefon());
				}
				interessatRepository.save(interessatEntity);
			}
		}
		
		// Canvia l'estat del registre a la BBDD
		anotacio.setEstat(AnotacioEstatEnumDto.PROCESSADA);
		anotacio.setDataProcessament(new Date());
		try {
			// Notifica el nou estat a Distribucio
			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
			anotacioRegistreId.setClauAcces(anotacio.getDistribucioClauAcces());
			anotacioRegistreId.setIndetificador(anotacio.getDistribucioId());
			distribucioHelper.canviEstat(
					anotacioRegistreId,
					Estat.PROCESSADA,
					"Anotació incorporada a l'expedient d'Helium " + expedient.getIdentificadorLimitat());
		} catch (Exception e) {
			String errMsg = "Error comunicant l'estat de processada a Distribucio:" + e.getMessage();
			logger.warn(errMsg, e);
		}
		
		// Afegeix el log a l'expedient
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.ANOTACIO_RELACIONAR,
				anotacio.getIdentificador()
				);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);

		
		return conversioTipusHelper.convertir(
				anotacio, 
				AnotacioDto.class);
	}
	
	/** Poden realitzar accions els usuaris:
	 * - Administradors d'Helium
	 * - Amb permÃ­s de reassignaciÃ³ sobre el tipus d'expedient 
	 * 
	 * @param anotacio
	 */
	private void comprovaPermisAccio(Anotacio anotacio) {
		
		boolean isUsuariAdministrador = usuariActualHelper.isAdministrador();
		boolean potReassignar = false;
		// Si no és administrador d'Helium
		if (!isUsuariAdministrador) {
			if (anotacio.getExpedientTipus() != null) {
				// Comprova si té el permís de reassignar sobre el tipus d'expedient
				potReassignar = expedientTipusHelper.comprovarPermisos(
						anotacio.getExpedientTipus(), 
						null, 
						new Permission[] {
								ExtendedPermission.RELATE,
								ExtendedPermission.ADMINISTRATION
						});
			}
		}
		// Si no és administrador, ni pot reassignar ni llegir llença excepció
		if (!isUsuariAdministrador && !potReassignar) {
			throw new PermisDenegatException(
					anotacio.getId(), 
					Anotacio.class, 
					new Permission[] {
							BasePermission.READ,
							ExtendedPermission.RELATE
					});
		}
	}
	
	/** Resol el tipus d'interessat segons la infomració provinent de Distribució */
	private InteressatTipusEnumDto getInteressatTipus(AnotacioInteressat interessat) {
		InteressatTipusEnumDto tipus;
		if ("PERSONA_FISICA".equals(interessat.getTipus()))
				tipus = InteressatTipusEnumDto.FISICA;
		else if ("PERSONA_JURIDICA".equals(interessat.getTipus()))
			tipus = InteressatTipusEnumDto.JURIDICA;
		else if ("ADMINISTRACIO".equals(interessat.getTipus()))
			tipus = InteressatTipusEnumDto.ADMINISTRACIO;
		else {
			logger.error("Error incorporant l'interessat " + interessat + " a l'expedient: " +
					"El tipus d'interessat \"" + interessat.getTipus() + "\" no es reconeix. S'estableix el tipus FISICA");
			tipus = InteressatTipusEnumDto.FISICA;
		}
		return tipus;
	}
	
	/** Recupera el mapeig de Sistra i l'aplica a la pantalla d'inici d'expedient.
	 * @return	Retorna un objecte de tipus <code>AnotacioMapeigResultatDto</code> amb el resultat del mapeig
	 * de variables, documents i adjunts per poder advertir a l'usuari o afegir una alerta dels mapejos que han fallat.
	 * @throws Exception 
	 */
	public AnotacioMapeigResultatDto processarMapeigAnotacioExpedient(Long expedientTipusId, Long anotacioId) {
		AnotacioMapeigResultatDto resultatMapeig = new AnotacioMapeigResultatDto();
		logger.debug(
				"Processant el mapeig de l'anotació per l'inici d'expedient ( " +
				"anotacioId=" + anotacioId  + ", " +
				"expedientTipusId=" + expedientTipusId + ")");
		
		Anotacio anotacio = anotacioRepository.findOne(anotacioId);
		resultatMapeig.setAnotacioNumero(anotacio.getIdentificador());
	
		// Recupera la informació del tipus d'expedient
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId);
		// Comprovar que té integració amb Sistra2 activada
		if(expedientTipus.isDistribucioSistra()) {
			// Extreu variables i documents i annexos segons el mapeig sistra
			boolean ambContingut =  !expedientTipus.isArxiuActiu(); 
			resultatMapeig = distribucioHelper.getMapeig(expedientTipus, anotacio, ambContingut);
		}
		return resultatMapeig;
	}
	
	/** Recupera el mapeig de Sistra i l'aplica a l'expedient.
	 * @return	Retorna un objecte de tipus <code>AnotacioMapeigResultatDto</code> amb el resultat del mapeig
	 * de variables, documents i adjunts per poder advertir a l'usuari o afegir una alerta dels mapejos que han fallat.
	 * @throws Exception 
	 */
	public AnotacioMapeigResultatDto reprocessarMapeigAnotacioExpedient(Long expedientId, Long anotacioId) {
		AnotacioMapeigResultatDto resultatMapeig = new AnotacioMapeigResultatDto();
		logger.debug(
				"Reprocessant el mapeig de l'anotació de l'expedient ( " +
				"anotacioId=" + anotacioId + ", " +
				"expedientId=" + expedientId + ")");
		
		Anotacio anotacio = anotacioRepository.findOne(anotacioId);
		resultatMapeig.setAnotacioNumero(anotacio.getIdentificador());
	
		// Recupera la informació del tipus d'expedient i l'expedient
		ExpedientTipus expedientTipus = null;
		Expedient expedient = expedientRepository.findOne(expedientId);
		if(expedient!=null)
			expedientTipus = expedient.getTipus();
		
		// Comprovar que té integració amb Sistra2 activada
		if(expedientTipus.isDistribucioSistra()) {
			//Recuperar mapejos
			Map<String, Object> variables = null;
			Map<String, DadesDocumentDto> documents = null;
			List<DadesDocumentDto> annexos = null;
			MapeigSistra mapeigSistra = null;
			ExpedientDadaDto dada = null;
			DadesDocumentDto dadesDocumentDto = null;

			// Extreu variables i documents i annexos segons el mapeig sistra
			boolean ambContingut = expedient != null ? !expedient.isArxiuActiu() : !expedientTipus.isArxiuActiu(); 
			resultatMapeig = distribucioHelper.getMapeig(expedientTipus, anotacio, ambContingut);
			variables = resultatMapeig.getDades();
			documents = resultatMapeig.getDocuments();
			annexos = resultatMapeig.getAdjunts();			
			if(variables!=null) {
				for (String varCodi : variables.keySet()) {	
					// Obtenir la variable de l'expedient, comprovar si aquest mapeig existeix o no	
					mapeigSistra = mapeigSistraRepository.findByExpedientTipusAndCodiHelium(expedientTipus, varCodi);	
					dada = variableHelper.getDadaPerInstanciaProces(
							expedient.getProcessInstanceId(),
							varCodi,
							true);
					boolean variableExisteix = dada !=null && (dada.getMultipleValor() != null || dada.getVarValor() != null);
					processarVariablesAnotacio(
							expedientId,
							expedient.getProcessInstanceId(),
							varCodi,
							variables.get(varCodi),
							variableExisteix,
							mapeigSistra.isEvitarSobreescriptura());
				}
			}
			
			//Fem el mateix per els documents del mapeig
			for (String documentCodi : documents.keySet()) {
				mapeigSistra = mapeigSistraRepository.findByExpedientTipusAndCodiHelium(expedientTipus, documentCodi);	
				ExpedientDocumentDto document = documentHelper.findOnePerInstanciaProces(
						expedient.getProcessInstanceId(), 
						documentCodi);	
				
				boolean documentExisteix = document !=null ? true : false;
				
				
				if (documentExisteix && expedient.isArxiuActiu()) {
					// Si el document està firmat i a l'Arxiu llavors no es pot modifirar.
					if (document.isSignat() && !mapeigSistra.isEvitarSobreescriptura()) {
						// No es pot modificar un document firmat
						resultatMapeig.getErrorsDocuments().put(documentCodi, "El document no es pot sobreescriure perquè està firmat i no es pot modificar.");
						continue;						
					}
					// Si el document prové d'una anotació llavors ja està mapejat i no cal sobreescriure
					if (document.getAnotacioAnnexId() != null) {
						continue;
					}
				}	
				processarDocumentsAnotacio(
						dadesDocumentDto, 
						expedient, 
						document, 
						documentExisteix, 
						mapeigSistra.isEvitarSobreescriptura(), 
						documents, 
						mapeigSistra.getCodiHelium());
				
			}
			
			//Fem el mateix per els Annexos (adjunts), els creem encara que ja hi siguin
			for (DadesDocumentDto adjunt : annexos) {
				processarAdjuntsAnotacio( 
						expedient, 
						adjunt);		
			}
		}
		return resultatMapeig;
	}
	
	private void processarVariablesAnotacio(
			Long expedientId, String processInstanceId, String varCodi, Object varValor, boolean variableExisteix, boolean evitarSobreescriptura) {
		if (variableExisteix && !evitarSobreescriptura) {
			// actualitzar variable
			this.dadaUpdate(
					expedientId,
					processInstanceId,
					varCodi,
					varValor);	
		} else if (!variableExisteix){
			this.dadaCreate(
						expedientId,
						processInstanceId,								
						varCodi,
						varValor);
		}					
		
	}
	
	private void dadaCreate(
			Long expedientId,
			String processInstanceId,
			String varCodi,
			Object varValor) {
		logger.debug("Modificant dada de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"varCodi=" + varCodi + ", " +
				"varValor=" + varValor + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_CREAR,
				varCodi);
		expedientDadaHelper.optimitzarValorPerConsultesDominiGuardar(expedient.getTipus(), processInstanceId, varCodi, varValor);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
	}

	
	private void dadaUpdate(Long expedientId, String processInstanceId, String varCodi, Object varValor ) {
		logger.debug("Modificant dada de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"varCodi=" + varCodi + ", " +
				"varValor=" + varValor + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);		
		jbpmHelper.deleteProcessInstanceVariable(processInstanceId, varCodi);
		// Esborra la descripció per variables que mantenen el valor de la consulta
		Camp camp;
		InstanciaProcesDto instanciaProces = expedientHelper.getInstanciaProcesById(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findOne(instanciaProces.getDefinicioProces().getId());
		if (expedient.getTipus().isAmbInfoPropia()) {
			// obtenir el camp amb expedient tipus codi i codi de la variable
			camp = campRepository.findByExpedientTipusAndCodi(expedient.getTipus().getId(), varCodi, expedient.getTipus().getExpedientTipusPare() != null);
		}else {
			camp = campRepository.findByDefinicioProcesAndCodi(definicioProces, varCodi);
		}
		if (camp != null && camp.isDominiCacheText())
			jbpmHelper.deleteProcessInstanceVariable(processInstanceId, JbpmVars.PREFIX_VAR_DESCRIPCIO + varCodi);
		
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_MODIFICAR,
				varCodi);
		expedientDadaHelper.optimitzarValorPerConsultesDominiGuardar(
				expedient.getTipus(),
				processInstanceId,
				varCodi,
				varValor);
	}
	
	private void processarDocumentsAnotacio(
			DadesDocumentDto dadesDocumentDto, 
			Expedient expedient, 
			ExpedientDocumentDto document, 
			boolean documentExisteix,
			boolean evitarSobreescriptura,
			Map<String, DadesDocumentDto> documents,
			String codiHelium) {
		
		if (documentExisteix && !evitarSobreescriptura) {
			// Si existeix i es pot sobreescriure, l'actualitzem, sino el creem
			dadesDocumentDto = documents.get(codiHelium);
			DocumentStore documentStore = documentStoreRepository.findOne(document.getId());
			documentHelper.actualitzarDocument(
					documentStore.getId(),
					null,
					expedient.getProcessInstanceId(),
					dadesDocumentDto.getData(),
					dadesDocumentDto.getTitol(),
					dadesDocumentDto.getArxiuNom(),
					dadesDocumentDto.getArxiuContingut(),
					document.getArxiuExtensio(),
					document.isSignat(),
					false,
					null,
					document.getNtiOrigen(),
					document.getNtiEstadoElaboracion(),
					document.getNtiTipoDocumental(),
					document.getNtiIdOrigen(),
					dadesDocumentDto.isDocumentValid(),
					dadesDocumentDto.getDocumentError(),
					dadesDocumentDto.getAnnexId(), 
					dadesDocumentDto.getUuid());
			
			
			
		} else if (!documentExisteix) {
			dadesDocumentDto = documents.get(codiHelium);
			documentHelper.crearDocument(
					null, //taskInstanceId
					expedient.getProcessInstanceId(),
					dadesDocumentDto.getCodi(),
					dadesDocumentDto.getData(),
					false, // isAdjunt
					null, //adjuntTitol
					dadesDocumentDto.getArxiuNom(),
					dadesDocumentDto.getArxiuContingut(),
					dadesDocumentDto.getUuid(),
					dadesDocumentDto.getTipusMime(),
					expedient.isArxiuActiu() && dadesDocumentDto.getFirmaTipus() != null,	// amb firma
					false,	// firma separada
					null,	// firma contingut
					dadesDocumentDto.getNtiOrigen(),
					dadesDocumentDto.getNtiEstadoElaboracion(),
					dadesDocumentDto.getNtiTipoDocumental(),
					dadesDocumentDto.getNtiIdDocumentoOrigen(),
					dadesDocumentDto.isDocumentValid(),
					dadesDocumentDto.getDocumentError(),
					dadesDocumentDto.getAnnexId(),
					null);
		}
		
	}

	private void processarAdjuntsAnotacio(Expedient expedient, DadesDocumentDto adjunt ) {
		documentHelper.crearDocument(
				null,
				expedient.getProcessInstanceId(),
				null,
				adjunt.getData(),
				true, // isAdjunt
				adjunt.getTitol(),
				adjunt.getArxiuNom(),
				adjunt.getArxiuContingut(),
				adjunt.getUuid(),
				documentHelper.getContentType(adjunt.getArxiuNom()),
				expedient.isArxiuActiu() && adjunt.getFirmaTipus() != null,	// amb firma
				false,	// firma separada
				null,	// firma contingut
				adjunt.getNtiOrigen(),
				adjunt.getNtiEstadoElaboracion(),
				adjunt.getNtiTipoDocumental(),
				adjunt.getNtiIdDocumentoOrigen(),
				adjunt.isDocumentValid(),
				adjunt.getDocumentError(),
				adjunt.getAnnexId(),
				null);
	}
	

	private static final Logger logger = LoggerFactory.getLogger(AnotacioHelper.class);
}