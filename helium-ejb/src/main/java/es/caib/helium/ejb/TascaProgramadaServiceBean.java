package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.commons.exception.ExecucioMassivaException;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.ejb.base.AbstractServiceEjb;
import es.caib.helium.logic.intf.service.TascaProgramadaService;
import lombok.experimental.Delegate;

@Stateless
public class TascaProgramadaServiceBean extends AbstractServiceEjb<TascaProgramadaService> implements TascaProgramadaService {

	@Delegate
	TascaProgramadaService delegateService;

	protected void setDelegateService(TascaProgramadaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void comprovarExecucionsMassives() {
		delegateService.comprovarExecucionsMassives();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void comprovarAnotacionsPendents() throws NoTrobatException {
		delegateService.comprovarAnotacionsPendents();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void processarAnotacionsAutomatiques() throws NoTrobatException {
		delegateService.processarAnotacionsAutomatiques();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updatePeticionsAsincronesPinbal() throws ExecucioMassivaException {
		delegateService.updatePeticionsAsincronesPinbal();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void actualitzarUnitatsIProcediments() {
		delegateService.actualitzarUnitatsIProcediments();
	}

	@Override
	public void comprovarEmailAnotacionsNoAgrupats() {
		delegateService.comprovarEmailAnotacionsNoAgrupats();
	}

	@Override
	public void comprovarEmailAnotacionsAgrupats() {
		delegateService.comprovarEmailAnotacionsAgrupats();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void migrarExpedientsDocumentsArxiu() {
		delegateService.migrarExpedientsDocumentsArxiu();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void restartSchedulledTasks(String taskCodi) {
		delegateService.restartSchedulledTasks(taskCodi);
	}

}
