/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.Nofiticacio;
import net.conselldemallorca.helium.v3.core.repository.NotificacioRepository;

/**
 * Helper per a gestionar els entorns.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificacioHelper {

	@Resource
	private NotificacioRepository notificacioRepository;



	public Nofiticacio create(
			Long expedientId,
			String numero,
			Date data,
			String RDSClave,
			Long RDSCodigo) {
		Nofiticacio notificacio = new Nofiticacio();
//		notificacio.setNumero(numero);
//		notificacio.setDataEnviament(data);
//		notificacio.setRdsClave(RDSClave);
//		notificacio.setRdsCodi(RDSCodigo);
//		notificacio.setExpedientId(expedientId);
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
