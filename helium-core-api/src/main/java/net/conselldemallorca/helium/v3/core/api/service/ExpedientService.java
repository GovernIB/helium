/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import org.springframework.security.acls.model.NotFoundException;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AlertaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exception.TramitacioException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;


/**
 * Servei encarregat de gestionar els expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientService {

	/**
	 * Crea/inicia un nou expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn l'expedient que es vol consultar.
	 * @param usuari
	 *            L'usuari de l'expedient.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            La definició de procés de l'expedient.
	 * @param any
	 *            L'any de l'expedient.
	 * @param numero
	 *            El número de l'expedient.
	 * @param titol
	 *            El títol de l'expedient.
	 * @param registreNumero
	 * @param registreData
	 * @param unitatAdministrativa
	 * @param idioma
	 * @param autenticat
	 * @param tramitadorNif
	 * @param tramitadorNom
	 * @param interessatNif
	 * @param interessatNom
	 * @param representantNif
	 * @param representantNom
	 * @param avisosHabilitats
	 * @param avisosEmail
	 * @param avisosMobil
	 * @param notificacioTelematicaHabilitada
	 * @param variables
	 * @param transitionName
	 * @param iniciadorTipus
	 * @param iniciadorCodi
	 * @param responsableCodi
	 * @param documents
	 * @param adjunts
	 * @return El nou expedient creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientDto create(
			Long entornId,
			String usuari,
			Long expedientTipusId,
			Long definicioProcesId,
			Integer any,
			String numero,
			String titol,
			String registreNumero,
			Date registreData,
			Long unitatAdministrativa,
			String idioma,
			boolean autenticat,
			String tramitadorNif,
			String tramitadorNom,
			String interessatNif,
			String interessatNom,
			String representantNif,
			String representantNom,
			boolean avisosHabilitats,
			String avisosEmail,
			String avisosMobil,
			boolean notificacioTelematicaHabilitada,
			Map<String, Object> variables,
			String transitionName,
			IniciadorTipusDto iniciadorTipus,
			String iniciadorCodi,
			String responsableCodi,
			Map<String, DadesDocumentDto> documents,
			List<DadesDocumentDto> adjunts) throws NoTrobatException;

	/**
	 * Modifica la informació d'un expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol modificar.
	 * @param numero
	 *            Número d'expedient.
	 * @param titol
	 *            Títol de l'expedient.
	 * @param responsableCodi
	 *            Codi de l'usuari responsable de l'expedient.
	 * @param dataInici
	 *            Data d'inici de l'expedient.
	 * @param comentari
	 *            Comentari de l'expedient.
	 * @param estatId
	 *            Estat de l'expedient.
	 * @param geoPosX
	 *            Posició X de la georeferència de l'expedient.
	 * @param geoPosY
	 *            Posició Y de la georeferència de l'expedient.
	 * @param geoReferencia
	 *            Codi de la georeferència de l'expedient.
	 * @param grupCodi
	 *            Codi del grup al qual pertany l'expedient.
	 * @param execucioDinsHandler
	 *            Indica si la invocació prové d'un handler.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void update(
			Long id,
			String numero,
			String titol,
			String responsableCodi,
			Date dataInici,
			String comentari,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String grupCodi,
			boolean execucioDinsHandler) throws NoTrobatException;

	/**
	 * Esborra un expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void delete(Long id) throws NoTrobatException;

	/**
	 * Retorna un expedient donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return L'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientDto findAmbId(Long id);

	/**
	 * Retorna varios expedients donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return L'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientDto> findAmbIds(Set<Long> ids);

	/**
	 * Consulta d'expedients per entorn paginada.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn l'expedient que es vol consultar.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param titol
	 *            Fragment del títol de l'expedient.
	 * @param numero
	 *            Fragment del número de l'expedient.
	 * @param dataInici1
	 *            Data d'inici inicial.
	 * @param dataInici2
	 *            Data d'inici final.
	 * @param dataFi1
	 *            Data de fi inicial.
	 * @param dataFi2
	 *            Data de fi final.
	 * @param estatTipus
	 *            Tipus d'estat a consultar (inicial, final o altres).
	 * @param estatId
	 *            Atribut id de l'estat de l'expedient.
	 * @param geoPosX
	 *            Posició X de la georeferència.
	 * @param geoPosY
	 *            Posició Y de la georeferència.
	 * @param geoReferencia
	 *            Codi de la georeferència.
	 * @param nomesTasquesPersonals
	 *            Indica que el resultat només ha d'incloure expedients amb tasques personals pendents.
	 * @param nomesTasquesGrup
	 *            Indica que el resultat només ha d'incloure expedients amb tasques de grup pendents.
	 * @param nomesAlertes
	 *            Indica que el resultat només ha d'incloure expedients amb alertes pendents.
	 * @param nomesErrors
	 *            Indica que el resultat només ha d'incloure expedients amb errors.
	 * @param mostrarAnulats
	 *            Indica si el resultat ha d'incloure expedients anulats.
	 * @param paginacioParams
	 *            Paràmetres de paginació.
	 * @return La pàgina del llistat d'expedients.
	 * @throws Exception 
	 * @throws NotFoundException
	 *             Si no s'ha trobat algun dels elements especificats
	 *             mitjançant el seu id (entorn, tipus, estat).
	 * @throws NotAllowedException
	 *             Si no es tenen permisos per a accedir als elements
	 *             especificats mitjançant el seu id (entorn, tipus, estat).
	 */
	public PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams) throws NoTrobatException;

	/**
	 * Consulta només ids d'expedient per entorn.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn l'expedient que es vol consultar.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param titol
	 *            Fragment del títol de l'expedient.
	 * @param numero
	 *            Fragment del número de l'expedient.
	 * @param dataInici1
	 *            Data d'inici inicial.
	 * @param dataInici2
	 *            Data d'inici final.
	 * @param dataFi1
	 *            Data de fi inicial.
	 * @param dataFi2
	 *            Data de fi final.
	 * @param estatTipus
	 *            Tipus d'estat a consultar (inicial, final o altres).
	 * @param estatId
	 *            Atribut id de l'estat de l'expedient.
	 * @param geoPosX
	 *            Posició X de la georeferència.
	 * @param geoPosY
	 *            Posició Y de la georeferència.
	 * @param geoReferencia
	 *            Codi de la georeferència.
	 * @param nomesTasquesPersonals
	 *            Indica que el resultat només ha d'incloure expedients amb tasques personals pendents.
	 * @param nomesTasquesGrup
	 *            Indica que el resultat només ha d'incloure expedients amb tasques de grup pendents.
	 * @param nomesAlertes
	 *            Indica que el resultat només ha d'incloure expedients amb alertes pendents.
	 * @param nomesErrors
	 *            Indica que el resultat només ha d'incloure expedients amb errors.
	 * @param mostrarAnulats
	 *            Indica si el resultat ha d'incloure expedients anulats.
	 * @return La pàgina del llistat d'expedients.
	 * @throws Exception 
	 * @throws NotFoundException
	 *             Si no s'ha trobat algun dels elements especificats
	 *             mitjançant el seu id (entorn, tipus, estat).
	 * @throws NotAllowedException
	 *             Si no es tenen permisos per a accedir als elements
	 *             especificats mitjançant el seu id (entorn, tipus, estat).
	 */
	public List<Long> findIdsAmbFiltre(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats);

	/**
	 * Retorna l'arxiu amb la imatge de la definició de procés.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @return La imatge.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ArxiuDto getImatgeDefinicioProces(
			Long id,
			String processInstanceId) throws NoTrobatException;

	/**
	 * Retorna la llista de persones que han fet alguna tasca de
	 * l'expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista de persones.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<PersonaDto> findParticipants(Long id) throws NoTrobatException;

	/**
	 * Retorna la llista d'accions visibles de l'expedient especificat.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista d'accions.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<AccioDto> findAccionsVisibles(Long id) throws NoTrobatException;

	/**
	 * Retorna la llista de tasques pendents de l'expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista de tasques pendents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientTascaDto> findTasquesPendents(
			Long id,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) throws NoTrobatException;

	/**
	 * Atura la tramitació d'un expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol aturar.
	 * @param motiu
	 *            El motiu per aturar la tramitació.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public void aturar(
			Long id,
			String motiu) throws NoTrobatException, PermisDenegatException;

	/**
	 * Repren la tramitació d'un expedient aturat.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol reprendre.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public void reprendre(Long id) throws NoTrobatException, PermisDenegatException;

	/**
	 * Anul·la la tramitació d'un expedient. Un espedient anul·lat és més o manco
	 * equivalent a esborrat, però les dades de l'expedient segueixen a dins la
	 * base de dades i en qualsevol moment es pot tornar a activar.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol anular.
	 * @param motiu
	 *            El motiu per anular l'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void anular(
			Long id,
			String motiu) throws NoTrobatException, PermisDenegatException;

	/**
	 * Reactiva un expedient prèviament anul·lat.
	 * 
	 * @param id
	 *            Atribut id de l'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void desanular(
			Long id) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retrocedeix la finalizació d'un expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void desfinalitzar(
			Long id) throws NoTrobatException, PermisDenegatException;

	/**
	 * Crea una relació entre dos expedients.
	 * 
	 * @param expedientOrigenId
	 *            Atribut id de l'expedient origen de la relació.
	 * @param expedientDestiId
	 *            Atribut id de l'expedient destí de la relació.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void relacioCreate(
			Long origenId,
			Long destiId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Esborra una relació entre dos expedients.
	 * 
	 * @param origenId
	 *            Atribut id de l'expedient origen de la relació.
	 * @param destiId
	 *            Atribut id de l'expedient destí de la relació.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void relacioDelete(
			Long origenId,
			Long destiId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna la llista d'expedients relacionats amb l'expedient
	 * especificat.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientDto> relacioFindAmbExpedient(
			Long id) throws NoTrobatException, PermisDenegatException;

	/**
	 * Executa un script sobre una instància de procés.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @param script
	 *            Script a executar.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void procesScriptExec(
			Long expedientId,
			String processInstanceId,
			String script) throws NoTrobatException, PermisDenegatException;

	/**
	 * Canvia la versió de la definició de procés.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol actualitzar.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés que es vol actualitzar.
	 * @param versio
	 *            Número de versió de la nova definició de procés.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void procesDefinicioProcesActualitzar(
			String processInstanceId,
			int versio) throws NoTrobatException, PermisDenegatException;

	/**
	 * Canvia la versió de la definició de procés de varis processos de l'expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol actualitzar.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés que es vol actualitzar.
	 * @param versio
	 *            Número de versió de la nova definició de procés.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void procesDefinicioProcesCanviVersio(
			Long expedientId,
			Long definicioProcesId,
			Long[] subProcesIds,
			List<DefinicioProcesExpedientDto> subDefinicioProces) throws NoTrobatException, PermisDenegatException;

	/**
	 * Consulta els logs d'un expedient ordenats per data.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param detall
	 *            Indica si s'ha de retornar la informació detallada o no.
	 * @return els logs de l'expedient organitzats per instància de procés.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public SortedSet<Entry<InstanciaProcesDto, List<ExpedientLogDto>>> registreFindLogsOrdenatsPerData(
			Long expedientId,
			boolean detall) throws NoTrobatException, PermisDenegatException;

	/**
	 * Obté les tasques associades als logs de l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @return el llistat de tasques.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public Map<String, ExpedientTascaDto> registreFindTasquesPerLogExpedient(
			Long expedientId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Fa un retrocés de l'expedient de totes les modificacions fetes a partir
	 * del log especificat.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param logId
	 *            Atribut id del log d'expedient.
	 * @param retrocedirPerTasques
	 *            Indica si el retrocés es per tasques.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void registreRetrocedir(
			Long expedientId,
			Long logId,
			boolean retrocedirPerTasques) throws NoTrobatException, PermisDenegatException;

	/**
	 * Elimina tots els logs associats a un expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void registreBuidarLog(
			Long expedientId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna els logs associats a una tasca de l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param logId
	 *            Atribut id del log d'expedient.
	 * @return la llista de logs.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientLogDto> registreFindLogsTascaOrdenatsPerData(
			Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Obté els logs associats a una acció de retrocés ordenats per data.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param logId
	 *            Atribut id del log d'expedient.
	 * @return la llista de logs.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientLogDto> registreFindLogsRetroceditsOrdenatsPerData(
			Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna la informació d'un registre de log de l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param logId
	 *            Atribut id del log d'expedient.
	 * @return la informació del log.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientLogDto registreFindLogById(
			//Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna la llista d'alertes no eliminades de l'expedient
	 * especificat
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista d'alertes.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<AlertaDto> findAlertes(Long id) throws NoTrobatException;

	/**
	 * Retorna la llista d'errors relacionats amb
	 * l'Expedient
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista d'errors.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	Object[] findErrorsExpedient(Long id) throws NoTrobatException;

	public List<ExpedientDto> findSuggestAmbEntornLikeIdentificador(Long entornid, String text);

	public List<InstanciaProcesDto> getArbreInstanciesProces(
			Long processInstanceId);

	public InstanciaProcesDto getInstanciaProcesById(String processInstanceId);

	public void cancelarTasca(Long expedientId, Long taskId) throws NoTrobatException, ValidacioException;

	public void suspendreTasca(Long expedientId, Long taskId) throws NoTrobatException, ValidacioException;

	public void reprendreTasca(Long expedientId, Long taskId) throws NoTrobatException, ValidacioException;

	public void reassignarTasca(String taskId, String expression) throws NoTrobatException, ValidacioException;

	/**
	 * Fa una consulta per tipus damunt un tipus d'expedient.
	 * 
	 * @param consultaId
	 *            Atribut id de la consulta a executar
	 * @param filtreValors
	 *            Els valors per filtrar els resultats de la consulta
	 * @param expedientIdsSeleccio
	 *            La llista d'expedients seleccionats
	 * @param nomesTasquesPersonals
	 *            Indica que el resultat ha d'incloure expedients amb tasques personals pendents.
	 * @param nomesTasquesGrup
	 *            Indica que el resultat ha d'incloure expedients amb tasques de grup pendents.
	 * @param nomesMeves
	 *            Indica que el resultat ha d'incloure expedients amb tasques de l'usuari actual.
	 * @param nomesAlertes
	 *            Indica que el resultat ha d'incloure expedients amb alertes pendents.
	 * @param nomesErrors
	 *            Indica que el resultat ha d'incloure expedients amb errors.
	 * @param mostrarAnulats
	 *            Indica si el resultat ha d'incloure expedients anulats.
	 * @param paginacioParams
	 *            Paràmetres de paginació.
	 * @return la pàgina d'expedients resultat d'executar la consulta
	 * @throws NotFoundException
	 *             Si no es troba la consulta amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public PaginaDto<ExpedientConsultaDissenyDto> consultaFindPaginat(
			Long consultaId,
			Map<String, Object> filtreValors,
			Set<Long> expedientIdsSeleccio,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams);

	/**
	 * Fa una consulta per tipus damunt un tipus d'expedient i retorna només els ids.
	 * 
	 * @param consultaId
	 *            Atribut id de la consulta a executar
	 * @param filtreValors
	 *            Els valors per filtrar els resultats de la consulta
	 * @param seleccioIds
	 *            La llista d'expedients seleccionats
	 * @param nomesTasquesPersonals
	 *            Indica que el resultat ha d'incloure expedients amb tasques personals pendents.
	 * @param nomesTasquesGrup
	 *            Indica que el resultat ha d'incloure expedients amb tasques de grup pendents.
	 * @param nomesMeves
	 *            Indica que el resultat ha d'incloure expedients amb tasques de l'usuari actual.
	 * @param nomesAlertes
	 *            Indica que el resultat ha d'incloure expedients amb alertes pendents.
	 * @param nomesErrors
	 *            Indica que el resultat ha d'incloure expedients amb errors.
	 * @param mostrarAnulats
	 *            Indica si el resultat ha d'incloure expedients anulats.
	 * @param paginacioParams
	 *            Paràmetres de paginació.
	 * @return la llista d'ids dels expedients resultat d'executar la consulta
	 * @throws NotFoundException
	 *             Si no es troba la consulta amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public PaginaDto<Long> consultaFindNomesIdsPaginat(
			Long consultaId,
			Map<String, Object> filtreValors,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams);

	public List<TascaDadaDto> findConsultaFiltre(Long consultaId);

	public List<TascaDadaDto> findConsultaInforme(Long consultaId);

	public List<ExpedientConsultaDissenyDto> findConsultaDissenyPaginat(
			Long consultaId,
			Map<String, Object> valors,
			PaginacioParamsDto paginacioParams, 
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			Set<Long> ids);
	
	public List<Long> findIdsPerConsultaInforme(Long consultaId, Map<String, Object> valors, 
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup);

	public enum FiltreAnulat {
		ACTIUS		("expedient.consulta.anulats.actius"),
		ANUL_LATS	("expedient.consulta.anulats.anulats"),
		TOTS		("expedient.consulta.anulats.tots");
		private final String codi;
		private final String id;
		FiltreAnulat(String codi) {
			this.codi = codi;
			this.id = this.name();
		}
		public String getCodi(){
			return this.codi;
		}
		public String getId() {
			return id;
		}
	}

	public PaginaDto<ExpedientConsultaDissenyDto> findConsultaInformePaginat(
			Long consultaId,
			Map<String, Object> valorsPerService, 
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			PaginacioParamsDto paginacioParams) throws NoTrobatException;

	public String getNumeroExpedientActual(
			Long entornId,
			Long expedientTipusId,
			Integer any);

	public ExpedientTascaDto getStartTask(Long entornId, Long expedientTipusId, Long definicioProcesId, Map<String, Object> valors);

	public List<TascaDadaDto> findConsultaInformeParams(Long consultaId);

	public List<CampDto> getCampsInstanciaProcesById(String processInstanceId);

	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId);

	public void deleteSignatura(Long expedientId, Long documentStoreId) throws NoTrobatException, SistemaExternException;

	public List<ExpedientTascaDto> findTasquesPerInstanciaProces(Long expedientId, String processInstanceId, boolean mostrarDeOtrosUsuarios) throws NoTrobatException;

	public boolean isDiferentsTipusExpedients(Set<Long> ids);

	public boolean luceneReindexarExpedient(Long expedientId) throws PermisDenegatException, NoTrobatException;

	public List<AccioDto> findAccionsVisiblesAmbProcessInstanceId(String processInstanceId, Long expedientId);

	public void accioExecutar(Long expedientId, String processInstanceId, Long accioId) throws NoTrobatException, TramitacioException, PermisDenegatException;

	public AccioDto findAccioAmbId(Long idAccio);

	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol);

	public boolean existsExpedientAmbEntornTipusINumero(Long entornId, Long expedientTipusId, String numero);

}
