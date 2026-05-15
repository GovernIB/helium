package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.MonitorTascaInfo;
import es.caib.helium.logic.intf.service.MonitorTasquesService;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class MonitorTasquesServiceBean extends AbstractServiceEjb<MonitorTasquesService> implements MonitorTasquesService {

	@Delegate
	MonitorTasquesService delegateService;

	protected void setDelegateService(MonitorTasquesService delegateService) {
		this.delegateService = delegateService;
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MonitorTascaInfo addTasca(String codiTasca) {
		return delegateService.addTasca(codiTasca);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updateProperaExecucio(String codi, Long plusValue) {
		delegateService.updateProperaExecucio(codi, plusValue);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MonitorTascaInfo> findAll() {
		return delegateService.findAll();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MonitorTascaInfo findByCodi(String codi) {
		return delegateService.findByCodi(codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void inici(String codiTasca) {
		delegateService.inici(codiTasca);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void fi(String codiTasca) {
		delegateService.fi(codiTasca);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void error(String codiTasca, String error) {
		delegateService.error(codiTasca, error);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reiniciarTasquesEnSegonPla(String codiTasca) {
		delegateService.reiniciarTasquesEnSegonPla(codiTasca);
	}

}
