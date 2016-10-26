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
	private NotificacioRepository notifElectronicaRepository;



	public Nofiticacio create(
			Long expedientId,
			String numero,
			Date data,
			String RDSClave,
			Long RDSCodigo) {
		Nofiticacio notifElectronica = new Nofiticacio();
//		notifElectronica.setNumero(numero);
//		notifElectronica.setDataEnviament(data);
//		notifElectronica.setRdsClave(RDSClave);
//		notifElectronica.setRdsCodi(RDSCodigo);
//		notifElectronica.setExpedientId(expedientId);
		return notifElectronicaRepository.save(notifElectronica);
	}

	public boolean delete(
			String numero,
			String clave,
			Long codigo) {
//		Nofiticacio notifElectronica = notifElectronicaRepository.findByNumeroAndRdsCodiAndRdsClave(
//				numero,
//				codigo,
//				clave);
//		if (notifElectronica != null) {
//			notifElectronicaRepository.delete(notifElectronica);
//			return true;
//		}
		return false;
	}

}
