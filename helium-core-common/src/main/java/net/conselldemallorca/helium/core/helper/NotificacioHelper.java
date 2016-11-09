/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Notificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
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
	ExpedientRepository expedientRepository;
	@Resource
	ExpedientHelper expedientHelper;
	@Resource
	DocumentStoreRepository documentStoreRepository;
	@Resource
	ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PluginHelper pluginHelper;


	public Notificacio create(
			ExpedientDto expedient,
			NotificacioDto notificacioDto) {
		Notificacio notificacio = conversioTipusHelper.convertir(notificacioDto, Notificacio.class);
		notificacio.setExpedient(expedientRepository.findOne(expedient.getId()));
		notificacio.setDocument(documentStoreRepository.findById(notificacioDto.getDocument().getId()));
		
		List<DocumentStore> annexos = new ArrayList<DocumentStore>();
		for (DocumentNotificacioDto annex: notificacioDto.getAnnexos()) {
			annexos.add(documentStoreRepository.findById(annex.getId()));
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
			if (resposta != null && resposta.getData() != null)
				notificacio.setEstat(DocumentEnviamentEstatEnumDto.PROCESSAT_OK);
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

	private static final Log logger = LogFactory.getLog(NotificacioHelper.class);
}
