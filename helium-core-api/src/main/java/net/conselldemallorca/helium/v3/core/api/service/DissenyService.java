/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;


/**
 * Servei que proporciona la funcionalitat de disseny d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DissenyService {

	/**
	 * Retorna una llista amb els estats donats d'alta a dins un
	 * determinat tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @return
	 * @throws ExpedientTipusNotFoundException
	 */
	public List<EstatDto> findEstatByExpedientTipus(
			Long expedientTipusId) throws ExpedientTipusNotFoundException;

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual
	 * te permisos de lectura.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisReadUsuariActual(
			Long entornId) throws EntornNotFoundException;

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual
	 * te permisos de disseny.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisDissenyUsuariActual(
			Long entornId) throws EntornNotFoundException;

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual
	 * te permisos de gestió.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisGestioUsuariActual(
			Long entornId) throws EntornNotFoundException;

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual
	 * te permisos de creació.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisCrearUsuariActual(
			Long entornId) throws EntornNotFoundException;
	
	/**
	 * Retorna les consultes d'un tipus d'expedient per les quals l'usuari actual
	 * te permisos de lectura.
	 * 
	 * @param entornId
	 * @param expedientTipusId
	 * @return
	 * @throws EntornNotFoundException
	 */
	public List<ConsultaDto> findConsultesActivesAmbEntornIExpedientTipusOrdenat(
			Long entornId,
			Long expedientTipusId) throws EntornNotFoundException;

	public byte[] getDeploymentResource(Long id, String recursForm);

	public ExpedientTipusDto getExpedientTipusById(Long id);

	public DefinicioProcesDto getById(Long id);

	public DefinicioProcesDto findDarreraDefinicioProcesForExpedientTipus(Long expedientTipusId, boolean ambTascaInicial);

	public List<ExpedientTipusDto> findExpedientTipusAmbEntorn(EntornDto entornId);

	public ConsultaDto findConsulteById(Long id) throws EntornNotFoundException;

	public CampDto findCampAmbDefinicioProcesICodiSimple(Long definicioProcesId, String campCodi);

	public List<?> getResultatConsultaCamp(String taskId, String processInstanceId, Long definicioProcesId, String campCodi, String textInicial, Map<String, Object> mapDelsValors);
}
