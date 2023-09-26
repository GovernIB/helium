/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.acls.model.NotFoundException;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AlertaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exception.TramitacioException;


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
	 * @param ntiActiu
	 * @param organ
	 * @param classificacio
	 * @param serieDocumental
	 * @param ntiTipoFirma
	 * @param ntiValorCsv
	 * @param ntiDefGenCsv
	 * @param anotacioId
	 * 			Id de la petició d'anotació de registre que s'associarà a l'expedient.
	 * @param anotacioInteressatsAssociar
	 * 			Indica si associar o no els interessats de l'anotació a l'expedient.
	 * 
	 * @return El nou expedient creat.
	 * @throws net.conselldemallorca.helium.integracio.plugins.SistemaExternException 
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
			List<DadesDocumentDto> adjunts,
			Long anotacioId,
			boolean anotacioInteressatsAssociar) throws Exception;

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
	public ExpedientDto findAmbIdAmbPermis(Long id);
	
	/**
	 * Retorna un expedient donat el seu id sense comprovar permisos.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return L'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 */
	public ExpedientDto findAmbId(Long expedientId);
	
	/**
	 * Retorna un expedient donat el processInstanceId sense comprovar permisos.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return L'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 */
	public ExpedientDto findExpedientAmbProcessInstanceId(String processInstanceId);


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
	 * @param registreNumero
	 * 			  Número de registre
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
			String registreNumero,
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
	 *@param registreNumero
	 *            Número de Registre
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
			String registreNumero,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats);
	
	/** Mètode per cercar expedients pel suggest d'expedients. Filtra pel tipus d'expedient i busca
	 * el text dins del número o el títol de l'expedient.
	 * @param expedientTipusId
	 * @param text
	 * @throws NotAllowedException
	 *             Si no es tenen permisos per a accedir als elements
	 *             especificats mitjançant el seu id (entorn, tipus, estat).
	 * @return Retorna la llista d'expedients el número o títol dels quals contenen el text
	 */
	public List<ExpedientDto> findPerSuggest(Long expedientTipusId, String text);
	
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
	 * Finalitza un expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void finalitzar(
			Long id) throws NoTrobatException, PermisDenegatException;

	/**
	 * Migra l'expedient a l'arxiu
	 * 
	 * @param id
	 *            Atribut id de l'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void migrarArxiu(
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
	 * @param expedientId
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
	 * Consulta les accions visibles per a un usuari donat un expedient i una
	 * instància de procés.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol actualitzar.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés que es vol actualitzar.
	 * @return la llista d'accions visibles
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 */
	public List<AccioDto> accioFindVisiblesAmbProcessInstanceId(
			Long expedientId,
			String processInstanceId) throws NoTrobatException;

	/**
	 * Consulta una acció d'una instància de procés.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol actualitzar.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés que es vol actualitzar.
	 * @param accioId
	 *             Atribut id de l'acció que es vol consultar.
	 * @return l'acció amb l'id especificat.
	 * @throws NoTrobatException
	 * @throws PermisDenegatException
	 */
	public AccioDto accioFindAmbId(
			Long expedientId,
			String processInstanceId,
			Long accioId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Executa una acció d'una instància de procés.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol actualitzar.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés que es vol actualitzar.
	 * @return la llista d'accions visibles
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws TramitacioException
	 *             Si s'ha produit algun error executant el handler jBPM.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void accioExecutar(
			Long expedientId,
			String processInstanceId,
			Long accioId) throws NoTrobatException, TramitacioException, PermisDenegatException;


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
	//public List<AccioDto> findAccionsVisibles(Long id) throws NoTrobatException;

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

	/**
	 * Retorna els camps de la instància de procés. Si el tipus d'expedient té informació pròpia
	 * es lligarà la informació per codi amb les variables del tipus d'expedient, si no es lligaran
	 * amb la de la definició de procés.
	 * @param expedientTipusId
	 * @param processInstanceId
	 * @return
	 */
	public List<CampDto> getCampsInstanciaProcesById(
			Long expedientTipusId, 
			String processInstanceId);

	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId);

	public void deleteSignatura(Long expedientId, Long documentStoreId) throws NoTrobatException, SistemaExternException;

	public boolean isDiferentsTipusExpedients(Set<Long> ids);

	public List<Map<String, DadaIndexadaDto>> luceneGetDades(long expedientId);
	
	public boolean luceneReindexarExpedient(Long expedientId) throws PermisDenegatException, NoTrobatException;

	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol);

	public boolean existsExpedientAmbEntornTipusINumero(Long entornId, Long expedientTipusId, String numero);

	public Long findIdAmbProcessInstanceId(String processInstanceId);
	
	public List<NotificacioDto> findNotificacionsPerExpedientId(Long expedientId) throws NoTrobatException;

	public List<DadesNotificacioDto> findNotificacionsNotibPerExpedientId(Long expedientId) throws NoTrobatException;
	
	public NotificacioDto findNotificacioPerId(Long notificacioId) throws NoTrobatException;

	/**
	 * Mètode per consulta els ids de les instàncies de procés per a una definició de procés
	 * identificada per la seva clau JBPM.
	 * @param entornId
	 * @param jbpmKey
	 * @return
	 */
	public List<String> findProcesInstanceIdsAmbEntornTipusAndProcessDefinitionName(
			Long entornId, 
			Long expedientTipusId,
			String jbpmKey);

	/**
	 * Mètode per consulta els ids de les instàncies de procés per a una definició de procés
	 * específica per el seu id a la taula de definicions de procés.
	 * @param definicioProcesId
	 * @return
	 */
	public List<String> findAmbDefinicioProcesId(Long definicioProcesId);

	public void notificacioReprocessar(Long notificacioId) throws NoTrobatException;

	/**
	 * Executa el handler associat a una variable de tipus acció amb les seves dades.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol actualitzar.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés que es vol actualitzar.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws TramitacioException
	 *             Si s'ha produit algun error executant el handler jBPM.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void executarCampAccio(
			Long expedientId, 
			String processInstanceId, 
			String accioCamp);

	/**
	 * Retorna la informació de l'expedient emmagatzemada a dins l'arxiu.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol actualitzar.
	 * @return la informació de l'expedient emmagatzemada a dins l'arxiu
	 */
	public ArxiuDetallDto getArxiuDetall(Long expedientId);
	
	/**
	 * Consulta expedients per a un número Sistra
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return L'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientDto> findAmbIniciadorCodi(String responsableCodi);

	/** Crea un .zip amb la documentació de l'expedient.
	 * 
	 * @param expedientId
	 * @return Retorna el contingut del zip.
	 */
	public byte[] getZipDocumentacio(Long expedientId);

	/** Actualitza les metadade NTI que falten a partir de la informació de l'Arxiu
	 * per l'expedient i pels documents. En principi a partir de la versió 3.2.112
	 * ja no s'haurien de produir errors després de migrar un expedient sense dades
	 * NTI a l'Arxiu i pert tant aquest mètode ja no seria necessari.
	 * 
	 * @param expedientId
	 */
	public void arreglarMetadadesNti(Long expedientId);

	/** Crea un .zip amb els documents a Notificar
	 * 
	 * @param expedientId
	 * @param documentsPerAfegir
	 * @return Retorna el contingut del zip.
	 */
	byte[] getZipPerNotificar(Long expedientId, List<ExpedientDocumentDto> documentsPerAfegir);

}
