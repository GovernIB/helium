/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.Nofiticacio;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
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


	public Nofiticacio create(
			ExpedientDto expedient,
			DocumentEnviamentEstatEnumDto estat,
			String registreNumero,
			String assumpte,
			Date dataEnviament,
			Date dataRecepcio) {
		Nofiticacio notificacio = new Nofiticacio();
		notificacio.setExpedient(expedientRepository.findOne(expedient.getId()));
		notificacio.setEstat(estat);
		notificacio.setRegistreNumero(registreNumero);
		notificacio.setAssumpte(assumpte);
		notificacio.setDataEnviament(dataEnviament);
		notificacio.setDataRecepcio(dataRecepcio);
//		notificacio.setDocument(documentStoreRepository.findById(Long.parseLong(document.getAdjuntId())));
		
		return notificacioRepository.save(notificacio);
	}

	public boolean delete(
			String numero,
			String clave,
			Long codigo) {
//		Nofiticacio notificacio = notificacioRepository.findByNumeroAndRdsCodiAndRdsClave(
//				numero,
//				codigo,
//				clave);
//		if (notificacio != null) {
//			notificacioRepository.delete(notificacio);
//			return true;
//		}
		return false;
	}

}
