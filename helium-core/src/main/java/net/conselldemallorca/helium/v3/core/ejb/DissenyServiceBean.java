/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.v3.core.api.dto.AreaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesVersioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
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
	public List<EstatDto> findEstatByExpedientTipus(Long expedientTipusId) {
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
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisReadUsuariActual(Long entornId) {
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
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisDissenyUsuariActual(Long entornId) {
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
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisGestioUsuariActual(Long entornId) {
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
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisCrearUsuariActual(Long entornId) {
		return delegate.findExpedientTipusAmbPermisCrearUsuariActual(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findExpedientTipusAmbPermisReadUsuariActual(
			Long entornId,
			Long expedientTipusId) {
		return findExpedientTipusAmbPermisReadUsuariActual(
				entornId,
				expedientTipusId);
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
	public List<ConsultaDto> findConsultesActivesAmbEntornIExpedientTipusOrdenat(Long entornId, Long expedientTipusId) {
		return delegate.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto findConsulteById(Long id) {
		return delegate.findConsulteById(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampDto> findCampsAmbDefinicioProcesOrdenatsPerCodi(
			Long definicioProcesId) {
		return delegate.findCampsAmbDefinicioProcesOrdenatsPerCodi(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesExpedientDto getDefinicioProcesByTipusExpedientById(Long expedientTipusId) {
		return delegate.getDefinicioProcesByTipusExpedientById(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesExpedientDto> getSubprocessosByProces(String jbpmId) {
		return delegate.getSubprocessosByProces(jbpmId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AreaDto findAreaById(Long areaId) {
		return delegate.findAreaById(areaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesVersioDto getByVersionsInstanciaProcesById(String processInstanceId) {
		return delegate.getByVersionsInstanciaProcesById(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ParellaCodiValorDto> findTasquesAmbEntornIExpedientTipusPerSeleccio(
			Long entornId,
			Long expedientTipusId) {
		return delegate.findTasquesAmbEntornIExpedientTipusPerSeleccio(
				entornId,
				expedientTipusId);
	}

	@Override
	public List<FilaResultat> consultaDominiIntern(String id, List<ParellaCodiValor> parametres) throws Exception {
		return delegate.consultaDominiIntern(id, parametres);
	}

}
