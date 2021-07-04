package es.caib.helium.ejb;

import es.caib.helium.logic.intf.exception.NoTrobatException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.Date;

@Stateless
public class TascaProgramadaService extends AbstractService<es.caib.helium.logic.intf.service.TascaProgramadaService> implements es.caib.helium.logic.intf.service.TascaProgramadaService {
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void comprovarExecucionsMassives() {
		getDelegateService().comprovarExecucionsMassives();
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void comprovarReindexacioAsincrona() {
		getDelegateService().comprovarReindexacioAsincrona();
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void setReindexarAsincronament(boolean reindexar) {
		getDelegateService().setReindexarAsincronament(reindexar);
		// TODO Auto-generated method stub
		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isReindexarAsincronament() {
		return getDelegateService().isReindexarAsincronament();
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void actualitzarEstatNotificacions(Long notificacioId) throws NoTrobatException {
		getDelegateService().actualitzarEstatNotificacions(notificacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reindexarExpedient(Long expedientId) throws NoTrobatException {
		getDelegateService().reindexarExpedient(expedientId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void actualitzarExpedientReindexacioData(Long expedientId, Date dataReindexacio) {
		getDelegateService().actualitzarExpedientReindexacioData(expedientId, dataReindexacio);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void comprovarEstatNotificacions() throws NoTrobatException {
		getDelegateService().comprovarEstatNotificacions();
	}
}
