/**
 * 
 */
package es.caib.helium.logic.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.client.integracio.notificacio.enums.EnviamentTipus;
import es.caib.helium.client.integracio.notificacio.enums.NotificacioEstat;
import es.caib.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import es.caib.helium.logic.intf.dto.DadesEnviamentDto;
import es.caib.helium.logic.intf.dto.DadesNotificacioDto;
import es.caib.helium.logic.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.helium.logic.intf.dto.DocumentNotificacioDto;
import es.caib.helium.logic.intf.dto.EnviamentTipusEnumDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.NotificacioDto;
import es.caib.helium.logic.intf.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.helium.logic.intf.dto.NotificacioEstatEnumDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.integracio.notificacio.RespostaEnviar;
import es.caib.helium.persist.entity.DocumentNotificacio;
import es.caib.helium.persist.entity.DocumentStore;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.Notificacio;
import es.caib.helium.persist.repository.DocumentNotificacioRepository;
import es.caib.helium.persist.repository.DocumentStoreRepository;
import es.caib.helium.persist.repository.ExpedientRepository;
import es.caib.helium.persist.repository.NotificacioRepository;

/**
 * Helper per a les notificacions a la safata telemàtica de SISTRA i pel NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificacioHelper {

	@Resource
	NotificacioRepository notificacioRepository;
	@Resource
	DocumentNotificacioRepository documentNotificacioRepository;
	@Resource
	ExpedientRepository expedientRepository;
	@Resource
	ExpedientHelper expedientHelper;
	@Resource
	DocumentStoreRepository documentStoreRepository;
	@Resource
    ConversioTipusServiceHelper conversioTipusServiceHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private DocumentHelper documentHelperV3;


	// Notificació SISTRA
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Notificacio create(
			ExpedientDto expedient,
			NotificacioDto notificacioDto) {
		Notificacio notificacio = conversioTipusServiceHelper.convertir(notificacioDto, Notificacio.class);
		notificacio.setExpedient(expedientRepository.findById(expedient.getId()).get());
		notificacio.setDocument(documentStoreRepository.findById(notificacioDto.getDocument().getId()).get());
		
		List<DocumentStore> annexos = new ArrayList<DocumentStore>();
		for (DocumentNotificacioDto annex: notificacioDto.getAnnexos()) {
			annexos.add(documentStoreRepository.findById(annex.getId()).get());
		}
		notificacio.setAnnexos(annexos);
		
		return notificacioRepository.save(notificacio);
	}
	
	public List<Notificacio> findNotificacionsPerExpedientId(Long expedientId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId, 
				true, 
				false, 
				false, 
				false);
		
		return notificacioRepository.findByExpedientOrderByDataEnviamentDesc(expedient);
	}
	
	public void obtenirJustificantNotificacio(Notificacio notificacio) {
		try {
			RespostaJustificantRecepcio resposta = pluginHelper.tramitacioObtenirJustificant(notificacio.getRegistreNumero());
			if (resposta != null && resposta.isOk()) {
				if (resposta.getData() != null) {
					notificacio.setEstat(DocumentEnviamentEstatEnumDto.PROCESSAT_OK);
				} else {
					notificacio.setEstat(DocumentEnviamentEstatEnumDto.ENVIAT);
				}
				notificacio.setDataRecepcio(resposta.getData());
				notificacio.setError(null);
			} else {
				notificacio.setError(resposta.getErrorDescripcio());
				notificacio.setEstat(DocumentEnviamentEstatEnumDto.PROCESSAT_ERROR);
			}
		} catch (Exception ex) {
			logger.error(
					"Error actualitzant estat notificacio " + notificacio.getRegistreNumero(),
					ex);
			notificacio.setError(ex.getMessage());
			notificacio.setEstat(DocumentEnviamentEstatEnumDto.PROCESSAT_ERROR);
		}
	}

	public boolean delete(
			String numero,
			String clave,
			Long codigo) {
		Notificacio notificacio = notificacioRepository.findByRegistreNumeroAndRdsCodiAndRdsClau(
				numero,
				codigo,
				clave);
		if (notificacio != null) {
			notificacioRepository.delete(notificacio);
			return true;
		}
		return false;
	}

	// Notificació NOTIB
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Mètode per realitzar una alta de notificació al NOTIB. La guarda a la taula de notificacions de document i retorna
	 * les dades de la notificació.
	 * 
	 * @param expedient
	 * @param dadesNotificacioDto
	 * @return
	 */
	@Transactional
	public DocumentNotificacio altaNotificacio(
			Expedient expedient,
			DadesNotificacioDto dadesNotificacioDto) {
		
		RespostaEnviar resposta = null; 
		try {
			resposta = pluginHelper.altaNotificacio(expedient, dadesNotificacioDto);
		} catch (Exception e) {
			throw new SistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(Enviament de notificació)", 
					e);
		}		
		DocumentNotificacio notificacio = toDocumentNotificacio(dadesNotificacioDto);
		documentNotificacioRepository.save(notificacio);

		notificacio.setError(resposta.isError());
		notificacio.setErrorDescripcio(resposta.getErrorDescripcio());
		notificacio.setEnviamentIdentificador(resposta.getIdentificador());
		notificacio.setEnviatData(new Date());
		try {
			notificacio.setEstat(NotificacioEstat.valueOf(resposta.getEstat().name()));
		} catch(Exception e) {
			notificacio.setError(true);
			notificacio.setErrorDescripcio("No s'ha pogut reconèixer l'estat \"" + resposta.getEstat() + "\" de la resposta");
		}
		// Guarda la refererència de l'enviament
		notificacio.setEnviamentReferencia(resposta.getReferencies().get(0).getReferencia());
		//TODO: Posar les referències per cada interessat.
//		for (EnviamentReferencia enviamentReferencia : respostaEnviar.getReferencies()) {
//			for (DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity : notificacioEntity.getDocumentEnviamentInteressats()) {
//				if(documentEnviamentInteressatEntity.getInteressat().getDocumentNum().equals(enviamentReferencia.getTitularNif())) {
//					documentEnviamentInteressatEntity.updateEnviamentReferencia(enviamentReferencia.getReferencia());
//				}
//			}
//		}
		return notificacio;
	}
	
	public List<DocumentNotificacio> findNotificacionsNotibPerExpedientId(Long expedientId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId, 
				true, 
				false, 
				false, 
				false);
		
		return documentNotificacioRepository.findByExpedientOrderByEnviatDataDesc(expedient);
	}
	
	
	public DocumentNotificacio toDocumentNotificacio(DadesNotificacioDto dadesNotificacio) {
		DocumentNotificacio notificacio = new DocumentNotificacio();
		
		String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		if (usuari == null || usuari.isEmpty())
			usuari = "Handler";
		notificacio.setUsuariCodi(usuari);
		notificacio.setTipus(EnviamentTipus.valueOf(dadesNotificacio.getEnviamentTipus().toString()));
				
		notificacio.setExpedient(expedientRepository.findById(dadesNotificacio.getExpedientId()).get());
		if (dadesNotificacio.getEnviamentTipus() != null)
			notificacio.setTipus(EnviamentTipus.valueOf(dadesNotificacio.getEnviamentTipus().toString()));
		else
			notificacio.setTipus(EnviamentTipus.NOTIFICACIO);
		notificacio.setEmisorDir3Codi(dadesNotificacio.getEmisorDir3Codi());
		notificacio.setDataProgramada(dadesNotificacio.getEnviamentDataProgramada());
		notificacio.setRetard(dadesNotificacio.getRetard());
		notificacio.setDataCaducitat(dadesNotificacio.getCaducitat());
		
		notificacio.setDocument(documentStoreRepository.findById(dadesNotificacio.getDocumentId()).get());

		// TODO: Només 1 enviament
		var dadesEnviament = dadesNotificacio.getEnviaments().get(0);

		// Titular
		var dadesTitular = dadesEnviament.getTitular();
		notificacio.setTitularNif(dadesTitular.getDni());
		notificacio.setTitularNom(dadesTitular.getNom());
		notificacio.setTitularLlinatge1(dadesTitular.getLlinatge1());
		notificacio.setTitularLlinatge2(dadesTitular.getLlinatge2());
		notificacio.setTitularTelefon(dadesTitular.getTelefon());
		notificacio.setTitularEmail(dadesTitular.getEmail());

		// Destinatari
		if (dadesEnviament.getDestinataris() != null && !dadesEnviament.getDestinataris().isEmpty()) {
			// TODO: Només 1 destinatari
			var dadesDestinatari = dadesEnviament.getDestinataris().get(0);
			notificacio.setDestinatariNif(dadesDestinatari.getDni());
			notificacio.setDestinatariNom(dadesDestinatari.getNom());
			notificacio.setDestinatariLlinatge1(dadesDestinatari.getLlinatge1());
			notificacio.setDestinatariLlinatge2(dadesDestinatari.getLlinatge2());
			notificacio.setDestinatariTelefon(dadesDestinatari.getTelefon());
			notificacio.setDestinatariEmail(dadesDestinatari.getEmail());
		}
			
		notificacio.setEstat(NotificacioEstat.PENDENT);
		
		notificacio.setConcepte(dadesNotificacio.getConcepte());
		notificacio.setDescripcio(dadesNotificacio.getDescripcio());

		return notificacio;
	}
	public DadesNotificacioDto toDadesNotificacioDto(DocumentNotificacio notificacio) {
		DadesNotificacioDto dadesNotificacio = new DadesNotificacioDto();
		
		dadesNotificacio.setId(notificacio.getId());
		dadesNotificacio.setExpedientId(notificacio.getExpedient().getId());
		DocumentStore document = documentStoreRepository.findById(notificacio.getDocument().getId()).get();
		dadesNotificacio.setDocumentId(document.getId());
		dadesNotificacio.setDocumentArxiuNom(document.getArxiuNom());
		if (!document.isAdjunt()) {
			dadesNotificacio.setDocumentNom(documentHelperV3.findDocumentPerDocumentStoreId(document.getProcessInstanceId(), document.getId()).getDocumentNom());
		}
		if (notificacio.getEnviamentCertificacio() != null) {
			DocumentStore justificant = documentStoreRepository.findById(notificacio.getEnviamentCertificacio().getId()).get();
			dadesNotificacio.setJustificantId(justificant.getId());
			dadesNotificacio.setJustificantArxiuNom(justificant.getAdjuntTitol() != null? justificant.getAdjuntTitol() : justificant.getArxiuNom());
		}
		
		dadesNotificacio.setEnviatData(notificacio.getEnviatData());
//		dadesNotificacio.setDocumentArxiuNom(notificacio.getDocument().getArxiuNom());
//		dadesNotificacio.setDocumentArxiuContingut(notificacio.getDocument().getArxiuContingut());
		if (notificacio.getTipus() != null)
			dadesNotificacio.setEnviamentTipus(EnviamentTipusEnumDto.valueOf(notificacio.getTipus().name()));
		
		dadesNotificacio.setEmisorDir3Codi(notificacio.getEmisorDir3Codi());
		dadesNotificacio.setEnviamentDataProgramada(notificacio.getDataProgramada());
		dadesNotificacio.setRetard(notificacio.getRetard());
		dadesNotificacio.setCaducitat(notificacio.getDataCaducitat());

		// TODO: Només 1 enviament
		List<DadesEnviamentDto> enviaments = new ArrayList<DadesEnviamentDto>();
		DadesEnviamentDto dadesEnviament = new DadesEnviamentDto();
		if (notificacio.getEnviamentDatatEstat() != null) {
			dadesEnviament.setEstat(NotificacioEnviamentEstatEnumDto.valueOf(notificacio.getEnviamentDatatEstat().toString()));
		}
		dadesEnviament.setEstatData(notificacio.getEnviamentDatatData());

		// Titular
		PersonaDto titular = new PersonaDto();
		titular.setDni(notificacio.getTitularNif());
		titular.setNom(notificacio.getTitularNom());
		titular.setLlinatge1(notificacio.getTitularLlinatge1());
		titular.setLlinatge2(notificacio.getTitularLlinatge2());
		titular.setTelefon(notificacio.getTitularTelefon());
		titular.setEmail(notificacio.getTitularEmail());
		dadesEnviament.setTitular(titular);

		// Destinatari
		if (notificacio.getDestinatariNif() != null) {
			List<PersonaDto> destinataris = new ArrayList<PersonaDto>();
			PersonaDto destinatari = new PersonaDto();
			destinatari.setDni(notificacio.getDestinatariNif());
			destinatari.setNom(notificacio.getDestinatariNom());
			destinatari.setLlinatge1(notificacio.getDestinatariLlinatge1());
			destinatari.setLlinatge2(notificacio.getDestinatariLlinatge2());
			destinatari.setTelefon(notificacio.getDestinatariTelefon());
			destinatari.setEmail(notificacio.getDestinatariEmail());
			destinataris.add(destinatari);
			dadesEnviament.setDestinataris(destinataris);
		}
		enviaments.add(dadesEnviament);
		dadesNotificacio.setEnviaments(enviaments);
		
		if (notificacio.getEstat() != null) {
			dadesNotificacio.setEstat(NotificacioEstatEnumDto.valueOf(notificacio.getEstat().toString()));
		}
		dadesNotificacio.setError(notificacio.isError());
		dadesNotificacio.setErrorDescripcio(notificacio.getErrorDescripcio());
		
		dadesNotificacio.setConcepte(notificacio.getConcepte());
		dadesNotificacio.setDescripcio(notificacio.getDescripcio());
		
		dadesNotificacio.setEnviamentReferencia(notificacio.getEnviamentReferencia());
		dadesNotificacio.setEnviamentIdentificador(notificacio.getEnviamentIdentificador());
		
		return dadesNotificacio;
	}
	
	
	private static final Log logger = LogFactory.getLog(NotificacioHelper.class);
}