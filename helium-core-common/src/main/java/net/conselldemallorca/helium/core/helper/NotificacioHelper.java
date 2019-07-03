/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.DocumentNotificacio;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Notificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnviamentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.repository.DocumentNotificacioRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.NotificacioRepository;

/**
 * Helper per a gestionar els entorns.
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
	ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PluginHelper pluginHelper;


	// Notificació SISTRA
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Notificacio create(
			ExpedientDto expedient,
			NotificacioDto notificacioDto) {
		Notificacio notificacio = conversioTipusHelper.convertir(notificacioDto, Notificacio.class);
		notificacio.setExpedient(expedientRepository.findOne(expedient.getId()));
		notificacio.setDocument(documentStoreRepository.findOne(notificacioDto.getDocument().getId()));
		
		List<DocumentStore> annexos = new ArrayList<DocumentStore>();
		for (DocumentNotificacioDto annex: notificacioDto.getAnnexos()) {
			annexos.add(documentStoreRepository.findOne(annex.getId()));
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
	
	public DocumentNotificacio create(
			DadesNotificacioDto notificacioDto) {
		DocumentNotificacio notificacio = toDocumentNotificacio(notificacioDto);
		return documentNotificacioRepository.save(notificacio);
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
				
		notificacio.setExpedient(expedientRepository.findOne(dadesNotificacio.getExpedientId()));
		if (dadesNotificacio.getEnviamentTipus() != null)
			notificacio.setTipus(EnviamentTipusEnumDto.valueOf(dadesNotificacio.getEnviamentTipus().name()));
		notificacio.setEmisorDir3Codi(dadesNotificacio.getEmisorDir3Codi());
		notificacio.setDataProgramada(dadesNotificacio.getEnviamentDataProgramada());
		notificacio.setRetard(dadesNotificacio.getRetard());
		notificacio.setDataCaducitat(dadesNotificacio.getCaducitat());
		
		notificacio.setDocument(documentStoreRepository.findOne(dadesNotificacio.getDocumentId()));

		// TODO: Només 1 enviament
		DadesEnviamentDto dadesEnviament = dadesNotificacio.getEnviaments().get(0);

		// Titular
		PersonaDto dadesTitular = dadesEnviament.getTitular();
		notificacio.setTitularNif(dadesTitular.getDni());
		notificacio.setTitularNom(dadesTitular.getNom());
		notificacio.setTitularLlinatge1(dadesTitular.getLlinatge1());
		notificacio.setTitularLlinatge2(dadesTitular.getLlinatge2());
		notificacio.setTitularTelefon(dadesTitular.getTelefon());
		notificacio.setTitularEmail(dadesTitular.getEmail());

		// Destinatari
		if (dadesEnviament.getDestinataris() != null && !dadesEnviament.getDestinataris().isEmpty()) {
			// TODO: Només 1 destinatari
			PersonaDto dadesDestinatari = dadesEnviament.getDestinataris().get(0);
			notificacio.setDestinatariNif(dadesDestinatari.getDni());
			notificacio.setDestinatariNom(dadesDestinatari.getNom());
			notificacio.setDestinatariLlinatge1(dadesDestinatari.getLlinatge1());
			notificacio.setDestinatariLlinatge2(dadesDestinatari.getLlinatge2());
			notificacio.setDestinatariTelefon(dadesDestinatari.getTelefon());
			notificacio.setDestinatariEmail(dadesDestinatari.getEmail());
		}
			
		notificacio.setEstat(NotificacioEnviamentEstatEnumDto.PENDENT);
		
		notificacio.setConcepte(dadesNotificacio.getConcepte());
		notificacio.setDescripcio(dadesNotificacio.getDescripcio());

		
		
		return notificacio;
	}
	public DadesNotificacioDto toDadesNotificacioDto(DocumentNotificacio notificacio) {
		DadesNotificacioDto dadesNotificacio = new DadesNotificacioDto();
		
		dadesNotificacio.setExpedientId(notificacio.getExpedient().getId());
		DocumentStore document = documentStoreRepository.findOne(notificacio.getDocument().getId());
		dadesNotificacio.setDocumentId(document.getId());
		dadesNotificacio.setDocumentArxiuNom(document.getArxiuNom());
		
		if (notificacio.getEnviamentCertificacio() != null) {
			DocumentStore justificant = documentStoreRepository.findOne(notificacio.getEnviamentCertificacio().getId());
			dadesNotificacio.setJustificantId(justificant.getId());
			dadesNotificacio.setJustificantArxiuNom(justificant.getArxiuNom());
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
		
		dadesNotificacio.setEstat(notificacio.getEstat());

		
		dadesNotificacio.setConcepte(notificacio.getConcepte());
		dadesNotificacio.setDescripcio(notificacio.getDescripcio());

		
		return dadesNotificacio;
	}
	
	
	private static final Log logger = LogFactory.getLog(NotificacioHelper.class);
}
