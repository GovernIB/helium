/**
 *
 */
package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.ExecucioMassivaDto;
import es.caib.helium.commons.dto.ExecucioMassivaListDto;
import es.caib.helium.logic.intf.service.ExecucioMassivaService;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExecucioMassivaServiceBean extends AbstractServiceEjb<ExecucioMassivaService> implements ExecucioMassivaService {

	@Delegate
	ExecucioMassivaService delegateService;

	protected void setDelegateService(ExecucioMassivaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void crearExecucioMassiva(ExecucioMassivaDto dto) {
		delegateService.crearExecucioMassiva(dto);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExecucioMassivaDto findAmbId(Long execucioMassivaId) {
		return delegateService.findAmbId(execucioMassivaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Object deserialize(byte[] bytes) {
		return delegateService.deserialize(bytes);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] serialize(Object obj) {
		return delegateService.serialize(obj);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public int cancelarExecucioMassiva(Long id) {
		return delegateService.cancelarExecucioMassiva(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void cancelarExecucioMassivaExpedient(Long id) {
		delegateService.cancelarExecucioMassivaExpedient(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getJsonExecucionsMassives(int numResults, String nivell) {
		return delegateService.getJsonExecucionsMassives(numResults, nivell);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getExecucioMassivaDetall(Long execucioMassivaId) {
		return delegateService.getExecucioMassivaDetall(execucioMassivaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long getExecucionsMassivesActiva(Long ultimaExecucioMassiva) {
		return delegateService.getExecucionsMassivesActiva(ultimaExecucioMassiva);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void executarExecucioMassiva(Long ome_id) throws InterruptedException {
		delegateService.executarExecucioMassiva(ome_id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void generaInformeError(Long ome_id, String error) {
		delegateService.generaInformeError(ome_id, error);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void actualitzaUltimaOperacio(Long ome_id) {
		delegateService.actualitzaUltimaOperacio(ome_id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void rependreExecucioMassiva(Long id) {
		delegateService.rependreExecucioMassiva(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reintentarExecucioMassiva(Long id) {
		delegateService.reintentarExecucioMassiva(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void rependreExecucioMassivaExpedient(Long id) {
		delegateService.rependreExecucioMassivaExpedient(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExecucioMassivaListDto getDarreraAltaMassiva(Long expedientTipusId) {
		return delegateService.getDarreraAltaMassiva(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String[][] getResultatAltaMassiva(Long execucioMassivaId) {
		return delegateService.getResultatAltaMassiva(execucioMassivaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] getCsvOriginalContent(Long execucioMassivaId) {
		return delegateService.getCsvOriginalContent(execucioMassivaId);
	}

}
