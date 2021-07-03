/**
 * 
 */
package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.helium.logic.intf.dto.ExecucioMassivaDto;
import es.caib.helium.logic.intf.dto.ExecucioMassivaListDto;
import es.caib.helium.logic.intf.service.ExecucioMassivaService;

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
	public void crearExecucioMassiva(ExecucioMassivaDto dto) {
		delegate.crearExecucioMassiva(dto);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExecucioMassivaDto findAmbId(Long execucioMassivaId) {
		return delegate.findAmbId(execucioMassivaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Object deserialize(byte[] bytes) {
		return delegate.deserialize(bytes);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] serialize(Object obj) {
		return delegate.serialize(obj);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public int cancelarExecucioMassiva(Long id) {
		return delegate.cancelarExecucioMassiva(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void cancelarExecucioMassivaExpedient(Long id) {
		delegate.cancelarExecucioMassivaExpedient(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getJsonExecucionsMassives(int numResults, String nivell) {
		return delegate.getJsonExecucionsMassives(numResults, nivell);
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
	public void executarExecucioMassiva(Long ome_id) {
		delegate.executarExecucioMassiva(ome_id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void generaInformeError(Long ome_id, String error) {
		delegate.generaInformeError(ome_id, error);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void actualitzaUltimaOperacio(Long ome_id) {
		delegate.actualitzaUltimaOperacio(ome_id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void rependreExecucioMassiva(Long id) {
		delegate.rependreExecucioMassiva(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void rependreExecucioMassivaExpedient(Long id) {
		delegate.rependreExecucioMassivaExpedient(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExecucioMassivaListDto getDarreraAltaMassiva(Long expedientTipusId) {
		return delegate.getDarreraAltaMassiva(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String[][] getResultatAltaMassiva(Long execucioMassivaId) {
		return delegate.getResultatAltaMassiva(execucioMassivaId);
	}
}