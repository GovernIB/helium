/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;


/**
 * Servei per a gestionar els documents d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientDocumentService {

	/**
	 * Crea un nou document a dins la instància de procés.
	 * 
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @param documentCodi
	 *             codi de document dins el disseny de l'expedient.
	 * @param data
	 *             data del document.
	 * @param arxiuNom
	 *             nom d'arxiu del document.
	 * @param arxiuContingut
	 *             contingut de l'arxiu del document.
	 * @param ntiOrigen
	 *             orígen NTI.
	 * @param ntiEstadoElaboracion
	 *             estat d'elaboració NTI.
	 * @param ntiTipoDocumental
	 *             tipus de document NTI.
	 * @param ntiIdOrigen
	 *             identificador NTI Del document original.
	 * @throws NoTrobatException
	 */
	public void create(
			Long expedientId,
			String processInstanceId,
			String documentCodi,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen) throws NoTrobatException;

	/**
	 * Crea un nou document a dins la instància de procés.
	 * 
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @param documentStoreId
	 *             identificador del document a modificar.
	 * @param data
	 *             data del document.
	 * @param arxiuNom
	 *             nom d'arxiu del document.
	 * @param arxiuContingut
	 *             contingut de l'arxiu del document.
	 * @param ntiOrigen
	 *             orígen NTI.
	 * @param ntiEstadoElaboracion
	 *             estat d'elaboració NTI.
	 * @param ntiTipoDocumental
	 *             tipus de document NTI.
	 * @param ntiIdOrigen
	 *             identificador NTI Del document original.
	 * @throws NoTrobatException
	 */
	public void update(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen) throws NoTrobatException;

	/**
	 * Crea un nou document adjunt a dins la instància de procés.
	 * 
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @param data
	 *             data del document.
	 * @param adjuntTitol
	 *             títol del document adjunt.
	 * @param arxiuNom
	 *             nom d'arxiu del document.
	 * @param arxiuContingut
	 *             contingut de l'arxiu del document.
	 * @param ntiOrigen
	 *             orígen NTI.
	 * @param ntiEstadoElaboracion
	 *             estat d'elaboració NTI.
	 * @param ntiTipoDocumental
	 *             tipus de document NTI.
	 * @param ntiIdOrigen
	 *             identificador NTI Del document original.
	 * @throws NoTrobatException
	 */
	public void createAdjunt(
			Long expedientId,
			String processInstanceId,
			Date data,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen) throws NoTrobatException;

	/**
	 * Modifica un document adjunt de la instància de procés.
	 * 
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @param documentStoreId
	 *             identificador del document a modificar.
	 * @param data
	 *             data del document.
	 * @param adjuntTitol
	 *             títol del document adjunt.
	 * @param arxiuNom
	 *             nom d'arxiu del document.
	 * @param arxiuContingut
	 *             contingut de l'arxiu del document.
	 * @param ntiOrigen
	 *             orígen NTI.
	 * @param ntiEstadoElaboracion
	 *             estat d'elaboració NTI.
	 * @param ntiTipoDocumental
	 *             tipus de document NTI.
	 * @param ntiIdOrigen
	 *             identificador NTI Del document original.
	 * @throws NoTrobatException
	 */
	public void updateAdjunt(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			Date data,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen) throws NoTrobatException;

	/**
	 * Crea o modifica un document de la instància de procés.
	 * 
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @param documentId
	 *             atribut id del document.
	 * @param documentStoreId
	 *             atribut id del document emmagatzemat.
	 * @param titol
	 *             títol del document.
	 * @param arxiuNom
	 *             nom d'arxiu del document.
	 * @param arxiuContingut
	 *             contingut de l'arxiu del document.
	 * @param data
	 *             data del document.
	 * @param ntiOrigen
	 *             orígen NTI.
	 * @param ntiEstadoElaboracion
	 *             estat d'elaboració NTI.
	 * @param ntiTipoDocumental
	 *             tipus de document NTI.
	 * @param ntiIdOrigen
	 *             identificador NTI Del document original.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	/*public void createOrUpdatea(
			Long expedientId,
			String processInstanceId,
			Long documentId,
			Long documentStoreId,
			String titol,
			String arxiuNom,
			byte[] arxiuContingut,
			Date data,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen) throws NoTrobatException, PermisDenegatException;*/

	/**
	 * Esborra un document d'una instància de procés.
	 * 
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @param documentStoreId
	 *             atribut id del document emmagatzemat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 * @throws SistemaExternException
	 *             Si es produeix algun error accedint a un sistema extern.
	 */
	public void delete(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) throws NoTrobatException, PermisDenegatException, SistemaExternException;

	/**
	 * Retorna la llista de documents d'una instància de procés de
	 * l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @return La llista de documents.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public List<ExpedientDocumentDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna un document d'una instància de procés de
	 * l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @param documentStoreId
	 *            Atribut id de la taula document_store del document que
	 *            es vol consultar.
	 * @return El document de l'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public ExpedientDocumentDto findOneAmbInstanciaProces(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna un document d'una instància de procés de
	 * l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @param documentCodi
	 *            Codi del document que es vol consultar.
	 * @return El document de l'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public ExpedientDocumentDto findOneAmbInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String documentCodi) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna l'arxiu del document.
	 * 
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @param documentStoreId
	 *             atribut id del document emmagatzemat.
	 * @return L'arxiu del document.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public ArxiuDto arxiuFindAmbDocument(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Genera l'arxiu d'un document a partir de la seva plantilla.
	 * 
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @return
	 * @throws NoTrobatException
	 * @throws PermisDenegatException
	 */
	public List<PortasignaturesDto> portasignaturesFindPendents(
			Long expedientId,
			String processInstanceId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Genera l'arxiu d'un document a partir de la seva plantilla.
	 * 
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @param documentCodi
	 *             codi del document a generar.
	 * @return l'arxiu del document generat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public ArxiuDto generarAmbPlantilla(
			Long expedientId,
			String processInstanceId,
			String documentCodi) throws NoTrobatException, PermisDenegatException;

	/**
	 * Comprovació d'extensió permesa d'un document.
	 * 
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @param documentCodi
	 *             codi del document a generar.
	 * @param arxiuNom
	 *             nom d'arxiu a verificar.
	 * @return true si l'extensió està permesa.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public boolean isExtensioPermesa(
			Long expedientId,
			String processInstanceId,
			String documentCodi,
			String arxiuNom) throws NoTrobatException, PermisDenegatException;

	/**
	 * Genera l'arxiu d'un document per una tasca a partir de la seva plantilla.
	 * 
	 * @param tascaId
	 *             atribut id de la tasca.
	 * @param documentCodi
	 *             codi del document a generar.
	 * @return l'arxiu del document generat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public ArxiuDto generarAmbPlantillaPerTasca(
			String tascaId,
			String documentCodi) throws NoTrobatException, PermisDenegatException;

	/**
	 * Comprovació d'extensió permesa d'un document.
	 * 
	 * @param tascaId
	 *             atribut id de la tasca.
	 * @param documentCodi
	 *             codi del document a generar.
	 * @param arxiuNom
	 *             nom d'arxiu a verificar.
	 * @return true si l'extensió està permesa.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public boolean isExtensioPermesaPerTasca(
			String tascaId,
			Long documentId,
			String arxiuNom) throws NoTrobatException, PermisDenegatException;

	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId);

	public Object findPortasignaturesInfo(Long expedientId, String processInstanceId, Long documentStoreId) throws NoTrobatException;

	public ArxiuDto findArxiuAmbTokenPerMostrar(String token) throws NoTrobatException;

	public ArxiuDto findArxiuAmbTokenPerSignar(String token) throws NoTrobatException;

	public DocumentDto findDocumentAmbId(Long documentStoreId) throws NoTrobatException;

	/**
	 * Retorna la informació del document emmagatzemada a dins l'arxiu.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol actualitzar.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @param documentCodi
	 *             codi de document dins el disseny de l'expedient.
	 * @return la informació del document emmagatzemada a dins l'arxiu
	 */
	public ArxiuDetallDto getArxiuDetall(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId);
	
	public void notificacioActualitzarEstat(
			String identificador, 
			String referenciaEnviament);

}
