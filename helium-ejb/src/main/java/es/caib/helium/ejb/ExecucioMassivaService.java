/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.ExecucioMassivaDto;
import es.caib.helium.logic.intf.dto.ExecucioMassivaListDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExecucioMassivaService extends AbstractService<es.caib.helium.logic.intf.service.ExecucioMassivaService> implements es.caib.helium.logic.intf.service.ExecucioMassivaService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void crearExecucioMassiva(ExecucioMassivaDto dto) {
		getDelegateService().crearExecucioMassiva(dto);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExecucioMassivaDto findAmbId(Long execucioMassivaId) {
		return getDelegateService().findAmbId(execucioMassivaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Object deserialize(byte[] bytes) {
		return getDelegateService().deserialize(bytes);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] serialize(Object obj) {
		return getDelegateService().serialize(obj);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public int cancelarExecucioMassiva(Long id) {
		return getDelegateService().cancelarExecucioMassiva(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void cancelarExecucioMassivaExpedient(Long id) {
		getDelegateService().cancelarExecucioMassivaExpedient(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getJsonExecucionsMassives(int numResults, String nivell) {
		return getDelegateService().getJsonExecucionsMassives(numResults, nivell);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getExecucioMassivaDetall(Long execucioMassivaId) {
		return getDelegateService().getExecucioMassivaDetall(execucioMassivaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long getExecucionsMassivesActiva(Long ultimaExecucioMassiva) {
		return getDelegateService().getExecucionsMassivesActiva(ultimaExecucioMassiva);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void executarExecucioMassiva(Long ome_id) {
		getDelegateService().executarExecucioMassiva(ome_id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void generaInformeError(Long ome_id, String error) {
		getDelegateService().generaInformeError(ome_id, error);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void actualitzaUltimaOperacio(Long ome_id) {
		getDelegateService().actualitzaUltimaOperacio(ome_id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void rependreExecucioMassiva(Long id) {
		getDelegateService().rependreExecucioMassiva(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void rependreExecucioMassivaExpedient(Long id) {
		getDelegateService().rependreExecucioMassivaExpedient(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExecucioMassivaListDto getDarreraAltaMassiva(Long expedientTipusId) {
		return getDelegateService().getDarreraAltaMassiva(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String[][] getResultatAltaMassiva(Long execucioMassivaId) {
		return getDelegateService().getResultatAltaMassiva(execucioMassivaId);
	}
}
