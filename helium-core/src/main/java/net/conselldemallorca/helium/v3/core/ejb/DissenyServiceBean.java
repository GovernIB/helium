/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

/**
 * Servei que proporciona la funcionalitat de disseny d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DissenyServiceBean implements DissenyService {
	@Autowired
	DissenyService delegate;

	/**
	 * Retorna una llista amb els estats donats d'alta a dins un determinat tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @return
	 * @throws ExpedientTipusNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EstatDto> findEstatByExpedientTipus(Long expedientTipusId) throws ExpedientTipusNotFoundException {
		return delegate.findEstatByExpedientTipus(expedientTipusId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de lectura.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisReadUsuariActual(Long entornId) throws EntornNotFoundException {
		return delegate.findExpedientTipusAmbPermisReadUsuariActual(entornId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de disseny.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisDissenyUsuariActual(Long entornId) throws EntornNotFoundException {
		return delegate.findExpedientTipusAmbPermisDissenyUsuariActual(entornId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de gestió.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisGestioUsuariActual(Long entornId) throws EntornNotFoundException {
		return delegate.findExpedientTipusAmbPermisGestioUsuariActual(entornId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de creació.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisCrearUsuariActual(Long entornId) throws EntornNotFoundException {
		return delegate.findExpedientTipusAmbPermisReadUsuariActual(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] getDeploymentResource(Long id, String recursForm) {
		return delegate.getDeploymentResource(id, recursForm);
	}

	public ExpedientTipusDto getExpedientTipusById(Long id) {
		return delegate.getExpedientTipusById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto getById(Long id) {
		return delegate.getById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findDarreraDefinicioProcesForExpedientTipus(Long expedientTipusId) {
		return delegate.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbEntorn(EntornDto entornId) {
		return delegate.findExpedientTipusAmbEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> findConsultesActivesAmbEntornIExpedientTipusOrdenat(Long entornId, Long expedientTipusId) throws EntornNotFoundException {
		return delegate.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto findConsulteById(Long id) throws EntornNotFoundException {
		return delegate.findConsulteById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampDto findCampAmbDefinicioProcesICodiSimple(Long definicioProcesId, String campCodi) {
		return delegate.findCampAmbDefinicioProcesICodiSimple(definicioProcesId, campCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto findAccioAmbId(Long idAccio) {
		return delegate.findAccioAmbId(idAccio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void executarAccio(AccioDto accio, ExpedientDto expedient) {
		delegate.executarAccio(accio, expedient);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<?> getResultatConsultaCamp(String taskId, String processInstanceId, CampDto camp, String textInicial, Map<String, Object> mapDelsValors) {
		return delegate.getResultatConsultaCamp(taskId, processInstanceId, camp, textInicial, mapDelsValors);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiIniciatDto findIniciatAmbId(Long id) {
		return delegate.findIniciatAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiIniciatDto> findIniciatsAmbExpedientId(Long expedientId) {
		return delegate.findIniciatsAmbExpedientId(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiDto> findTerminisAmbExpedientId(Long expedientId) {
		return delegate.findTerminisAmbExpedientId(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto getByInstanciaProcesById(String processInstanceId) {
		return delegate.getByInstanciaProcesById(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesDto> getSubprocessosByProces(String jbpmId) {
		return delegate.getSubprocessosByProces(jbpmId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findDefinicioProcesAmbJbpmId(String jbpmId) {
		return delegate.findDefinicioProcesAmbJbpmId(jbpmId);
	}
}
