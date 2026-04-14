package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.commons.exception.ExecucioMassivaException;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.logic.intf.service.TascaProgramadaService;

@Stateless
//@Interceptors(SpringBeanAutowiringInterceptor.class)
public class TascaProgramadaServiceBean implements TascaProgramadaService {
	
	@Autowired
	TascaProgramadaService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void comprovarExecucionsMassives() {
		delegate.comprovarExecucionsMassives();
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void actualitzarEstatNotificacions(Long notificacioId) throws NoTrobatException {
		delegate.actualitzarEstatNotificacions(notificacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void comprovarEstatNotificacions() throws NoTrobatException {
		delegate.comprovarEstatNotificacions();
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void comprovarAnotacionsPendents() throws NoTrobatException {
		delegate.comprovarAnotacionsPendents();
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void processarAnotacionsAutomatiques() throws NoTrobatException {
		delegate.processarAnotacionsAutomatiques();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updatePeticionsAsincronesPinbal() throws ExecucioMassivaException {
		delegate.updatePeticionsAsincronesPinbal();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void actualitzarUnitatsIProcediments() {
		delegate.actualitzarUnitatsIProcediments();
	}

	@Override
	public void comprovarEmailAnotacionsNoAgrupats() {
		delegate.comprovarEmailAnotacionsNoAgrupats();
	}

	@Override
	public void comprovarEmailAnotacionsAgrupats() {
		delegate.comprovarEmailAnotacionsAgrupats();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void migrarExpedientsDocumentsArxiu() {
		delegate.migrarExpedientsDocumentsArxiu();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void restartSchedulledTasks(String taskCodi) {
		delegate.restartSchedulledTasks(taskCodi);
	}

}
