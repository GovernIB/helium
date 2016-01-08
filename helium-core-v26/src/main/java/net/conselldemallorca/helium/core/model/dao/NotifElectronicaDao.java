/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.NotifElectronica;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

/**
 * Dao per als objectes que registren les notificacions electròniques en els expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotifElectronicaDao extends HibernateGenericDao<NotifElectronica, Long> {

	public NotifElectronicaDao() {
		super(NotifElectronica.class);
	}

	public NotifElectronica create(Long expedientId, String numero, Date data, String RDSClave, Long RDSCodigo) {
		NotifElectronica notifElectronica = new NotifElectronica();
		notifElectronica.setNumero(numero);
		notifElectronica.setData(data);
		notifElectronica.setRdsClave(RDSClave);
		notifElectronica.setRdsCodi(RDSCodigo);
		notifElectronica.setExpedientId(expedientId);
		return saveOrUpdate(notifElectronica);
	}

	public boolean delete(String numero, String clave, Long codigo) {
		List<NotifElectronica> llistat = findByCriteria(
				Restrictions.eq("numero", numero),
				Restrictions.eq("RDSCodi", codigo),
				Restrictions.eq("RDSClave", clave));
		if (llistat.size() > 0) {
			delete(llistat.get(0).getId());
			return true;
		}
		return false;
	}
}
