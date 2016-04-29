/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExecucioMassivaServiceBean implements ExecucioMassivaService {

	@Autowired
	ExecucioMassivaService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void crearExecucioMassiva(ExecucioMassivaDto dto) throws Exception {
		delegate.crearExecucioMassiva(dto);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Object deserialize(byte[] bytes) throws Exception {
		return delegate.deserialize(bytes);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] serialize(Object obj) throws Exception {
		return delegate.serialize(obj);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void cancelarExecucio(Long id) throws Exception {
		delegate.cancelarExecucio(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getJsonExecucionsMassivesByUser(int numResults, boolean viewAll) {
		return delegate.getJsonExecucionsMassivesByUser(numResults,viewAll);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getExecucioMassivaDetall(Long execucioMassivaId) {
		return delegate.getExecucioMassivaDetall(execucioMassivaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long getExecucionsMassivesActiva(Long ultimaExecucioMassiva) {
		return delegate.getExecucionsMassivesActiva(ultimaExecucioMassiva);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void executarExecucioMassiva(Long ome_id) throws Exception {
		delegate.executarExecucioMassiva(ome_id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void generaInformeError(Long ome_id, Exception exception) {
		delegate.generaInformeError(ome_id, exception);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void actualitzaUltimaOperacio(Long ome_id) {
		delegate.actualitzaUltimaOperacio(ome_id);
	}
}
