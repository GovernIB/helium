package net.conselldemallorca.helium.v3.core.ejb;

import java.util.Date;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.TascaProgramadaService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
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
	public void comprovarReindexacioAsincrona() {
		delegate.comprovarReindexacioAsincrona();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void actualitzarEstatNotificacions(Long notificacioId) throws NoTrobatException {
		delegate.actualitzarEstatNotificacions(notificacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reindexarExpedient(Long expedientId) throws NoTrobatException {
		delegate.reindexarExpedient(expedientId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void actualitzarExpedientReindexacioData(Long expedientId, Date dataReindexacio) {
		delegate.actualitzarExpedientReindexacioData(expedientId, dataReindexacio);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void comprovarEstatNotificacions() throws NoTrobatException {
		delegate.comprovarEstatNotificacions();
	}
}
