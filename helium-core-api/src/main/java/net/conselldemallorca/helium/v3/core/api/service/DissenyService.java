/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

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
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.NotAllowedException;



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
			Long expedientTipusId) throws NoTrobatException;

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual
	 * te permisos de lectura.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisReadUsuariActual(
			Long entornId) throws NoTrobatException;

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual
	 * te permisos de disseny.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisDissenyUsuariActual(
			Long entornId) throws NoTrobatException;

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual
	 * te permisos de gestió.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisGestioUsuariActual(
			Long entornId) throws NoTrobatException;

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual
	 * te permisos de creació.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisCrearUsuariActual(
			Long entornId) throws NoTrobatException;

	/**
	 * Retorna un tipus d'expedient comprovant el permís read per a
	 * l'usuari actual.
	 * 
	 * @param entornId
	 *            L'atribut id del entorn.
	 * @param expedientTipusId
	 *            L'atribut id del tipus d'expedient.
	 * @return
	 *            El tipus d'expedient.
	 * @throws NotFoundException
	 *             Si algun dels ids especificats no s'ha trobat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientTipusDto findExpedientTipusAmbPermisReadUsuariActual(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, NotAllowedException;

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
			Long expedientTipusId) throws NoTrobatException;

	public byte[] getDeploymentResource(Long id, String recursForm);

	public ExpedientTipusDto getExpedientTipusById(Long id);

	public DefinicioProcesDto getById(Long id);

	public DefinicioProcesDto findDarreraDefinicioProcesForExpedientTipus(Long expedientTipusId);

	public List<ExpedientTipusDto> findExpedientTipusAmbEntorn(EntornDto entorn);

	/**
	 * Consulta les tasques disponibles per entorn i expedient tipus per emplenar
	 * el camp de selecció del filtre de tasques.
	 * 
	 * @param entornId
	 *            L'atribut id del entorn.
	 * @param expedientTipusId
	 *            L'atribut id del tipus d'expedient.
	 * @return La llista de tasques
	 */
	public List<ParellaCodiValorDto> findTasquesAmbEntornIExpedientTipusPerSeleccio(
			Long entornId,
			Long expedientTipusId);

	public ConsultaDto findConsulteById(Long id);

	public List<CampDto> findCampsAmbDefinicioProcesOrdenatsPerCodi(Long definicioProcesId);

	public DefinicioProcesExpedientDto getDefinicioProcesByTipusExpedientById(Long expedientTipusId);

	public List<DefinicioProcesExpedientDto> getSubprocessosByProces(String jbpmId);

	public AreaDto findAreaById(Long areaId);

	public DefinicioProcesVersioDto getByVersionsInstanciaProcesById(String processInstanceId);

}
