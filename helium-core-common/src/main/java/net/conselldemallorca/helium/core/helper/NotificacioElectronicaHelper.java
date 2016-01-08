/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.NotifElectronica;
import net.conselldemallorca.helium.v3.core.repository.NotifElectronicaRepository;

/**
 * Helper per a gestionar els entorns.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificacioElectronicaHelper {

	@Resource
	private NotifElectronicaRepository notifElectronicaRepository;



	public NotifElectronica create(
			Long expedientId,
			String numero,
			Date data,
			String RDSClave,
			Long RDSCodigo) {
		NotifElectronica notifElectronica = new NotifElectronica();
		notifElectronica.setNumero(numero);
		notifElectronica.setData(data);
		notifElectronica.setRdsClave(RDSClave);
		notifElectronica.setRdsCodi(RDSCodigo);
		notifElectronica.setExpedientId(expedientId);
		return notifElectronicaRepository.save(notifElectronica);
	}

	public boolean delete(
			String numero,
			String clave,
			Long codigo) {
		NotifElectronica notifElectronica = notifElectronicaRepository.findByNumeroAndRdsCodiAndRdsClave(
				numero,
				codigo,
				clave);
		if (notifElectronica != null) {
			notifElectronicaRepository.delete(notifElectronica);
			return true;
		}
		return false;
	}

}
