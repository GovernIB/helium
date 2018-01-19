/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;
import java.util.Set;

import org.springframework.security.acls.model.NotFoundException;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.v3.core.api.dto.AreaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesVersioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacio;



/**
 * Servei que proporciona la funcionalitat de disseny d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DissenyService {

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
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Retorna les definicions de procés no utilitzades amb paginació
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return les definiciones de procés no utilitzades per aquell
	 * 			tipus d'expedient
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public PaginaDto<DefinicioProcesDto> findDefinicionsProcesNoUtilitzadesExpedientTipus(
			Long entornId,
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Retorna els ids de totes les definicions de procés no utilitzades per
	 * a un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return ids de les definiciones de procés no utilitzades per aquell
	 * 			tipus d'expedient
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<Long> findIdsDefinicionsProcesNoUtilitzadesExpedientTipus(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;
	

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

	public byte[] getDeploymentResource(Long id, String recursForm) throws NoTrobatException;

	public ExpedientTipusDto getExpedientTipusById(Long id) throws NoTrobatException;

	public DefinicioProcesDto getById(Long id);

	public DefinicioProcesDto findDarreraDefinicioProcesForExpedientTipus(Long expedientTipusId) throws NoTrobatException;

	public List<ExpedientTipusDto> findExpedientTipusAmbEntorn(EntornDto entorn) throws NoTrobatException;

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

	public ConsultaDto findConsulteById(Long id) throws NoTrobatException;

	/** Retorna la llista de camps definits al tius d'expedient si està informat i està cofigurat amb informació pròpia o 
	 * la llista de camps de la definició de procés si està informat. El resultat està ordentat per codi.
	 * @param expedientTipusId
	 * @param definicioProcesId
	 * @return
	 */
	public List<CampDto> findCampsOrdenatsPerCodi(
			Long expedientTipusId,
			Long definicioProcesId,
			boolean herencia);
	
	/** Retorna la llista de documents definits al tius d'expedient si està informat i està cofigurat amb informació pròpia o 
	 * la llista de documents de la definició de procés si està informat. El resultat està ordentat per codi.
	 * @param expedientTipusId
	 * @param definicioProcesId
	 * @return
	 */
	public List<DocumentDto> findDocumentsOrdenatsPerCodi(
			Long expedientTipusId,
			Long definicioProcesId,
			boolean herencia);

	/** Retorna la informació de disseny d'una definició de procés donat l'identificador de l'entorn i l'id de
	 * la definició de procés. */
	public DefinicioProcesExpedientDto getDefinicioProcesByEntorIdAndProcesId(Long entornId, Long procesId);
	
 	public DefinicioProcesExpedientDto getDefinicioProcesByTipusExpedientById(Long expedientTipusId);

	public List<DefinicioProcesExpedientDto> getSubprocessosByProces(String jbpmId) throws NoTrobatException;

	public AreaDto findAreaById(Long areaId) throws NoTrobatException;

	public DefinicioProcesVersioDto getByVersionsInstanciaProcesById(String processInstanceId) throws NoTrobatException;

	public List<FilaResultat> consultaDominiIntern(String id, List<ParellaCodiValor> parametres) throws Exception;

	public DocumentDto documentFindOne(Long documentId) throws NoTrobatException;

	public List<DocumentDto> documentFindAmbDefinicioProces(Long definicioProcesId) throws NoTrobatException;

	/** Retorna la llista de recursos per a una definició de procés específica. */
	public Set<String> getRecursosNom(Long definicioProcesId);

	/** Retorna el contingut d'un recurs de la definició de procés. */
	public byte[] getRecursContingut(
			Long definicioProcesId, 
			String nom);	

	/**
	 * Retorna els exepdients relacionats amb la definició de procés no utilitzada
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param jbpmId
	 *            Atribut jbpmId de la definició de procés.
	 * @return expedients relacionats amb la definició de procés no utilitzada
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public PaginaDto<ExpedientDto> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
			Long entornId,
			Long expedientTipusId,
			Long jbpmId,
			PaginacioParamsDto paginacioParams) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Retorna les ids dels exepdients relacionats amb la definició de procés no utilitzada
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param jbpmId
	 *            Atribut jbpmId de la definició de procés.
	 * @return ids dels expedients relacionats amb la definició de procés no utilitzada
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<Long> findIdsExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
			Long entornId,
			Long expedientTipusId,
			Long jbpmId) throws NoTrobatException, PermisDenegatException;

	/** Cerca el domini global dins l'entorn. Retorna null si no hi és. */
	public DominiDto dominiFindAmbCodi(
			Long entornId, 
			String codiDomini);

	/** Mètode per rebre un arxiu .par i actualitzar els handlers de la darrera versió d'una definició
	 * de procés existent a l'entorn amb la informació dels handlers continguda a l'arxiu .par.
	 * @param entornId
	 * @param nomArxiu Nom per comprovar que acabi amb ar.
	 * @param contingut Contingut del fitxe d'exportació jbpm que conté entre altra informació els handlers
	 * per actualitzar.
	 * @return La definició de procés actualitzada si tot ha anat bé.
	 */
	public DefinicioProcesDto updateHandlers(
			Long entornId, 
			String nomArxiu, 
			byte[] contingut);

	/** Obté el contingut d'una exportació donat el nom del fitxer amb la extensió i el contingut del mateix.
	 * 
	 * @param fitxer Nom del fitxer. Si acaba amb .*ar o .xml és una exportació JBPM i si acaba en .exp és una
	 * exportació d'Helium.
	 * @param contingut
	 * Contingut del fitxer exportat.
	 * @return Retorna un objecte de la classe {@link DefinicioProcesExportacio} que s'utilitzarà per a la importació 
	 * o actualització d'una definició de procés.
	 */
	public DefinicioProcesExportacio getDefinicioProcesExportacioFromContingut(
			String fitxer, 
			byte[] contingut);

	/** Retorna el nom de les accions JBPM de la definició de procés ordenades alfabèticament. 
	 * Serveix per tenir una lllista dels noms dels seus handlers. 
	 */
	public List<String> findAccionsJbpmOrdenades(Long definicioProcesId);
	
	
	public List<DocumentDto> findDocumentsAmbDefinicioProcesOrdenatsPerCodi(Long definicioProcesId) throws NoTrobatException;
}
