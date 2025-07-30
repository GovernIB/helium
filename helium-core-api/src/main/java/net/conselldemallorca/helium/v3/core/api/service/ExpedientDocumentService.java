/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentInfoDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentListDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentStoreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientFinalitzarDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaResultatDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesSimpleTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.document.DocumentDetallDto;
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
	 *             codi de document dins el disseny de l'expedient. Si aquest paràmetre no està informat llavors es tractarà 
	 *             el document com un adjunt i s'aprofitarà el títol pel nou document i es tractarà com a tal.
	 * @param data
	 *             data del document.
	 * @param adjuntTitol
	 *             Títol per l'adjunt en el cas que no s'informi del codi del document.
	 * @param arxiuNom
	 *             nom d'arxiu del document.
	 * @param arxiuContingut
	 *             contingut de l'arxiu del document.
	 * @param arxiuContentType
	 *             ContentType de l'arxiu.
	 * @param ntiOrigen
	 *             orígen NTI.
	 * @param ntiEstadoElaboracion
	 *             estat d'elaboració NTI.
	 * @param ntiTipoDocumental
	 *             tipus de document NTI.
	 * @param ntiIdOrigen
	 *             identificador NTI Del document original.
	 * @param annexosPerNotificar
	 * @throws NoTrobatException
	 */
	public DocumentStoreDto create(
			Long expedientId,
			String processInstanceId,
			String documentCodi,
			Date data,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut,
			String arxiuContentType,
			boolean ambFirma,
			boolean firmaSeparada,
			byte[] firmaContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen,
			List<ExpedientDocumentDto> annexosPerNotificar) throws NoTrobatException;

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
	 * @param adjuntTitol
	 *             Títol per l'adjunt en cas que sigui un adjunt.
	 * @param arxiuNom
	 *             nom d'arxiu del document.
	 * @param arxiuContingut
	 *             contingut de l'arxiu del document.
	 * @param arxiuContentType
	 *             ContentType de l'arxiu.
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
	public DocumentStoreDto update(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			Date data,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut,
			String arxiuContentType,
			boolean ambFirma,
			boolean firmaSeparada,
			byte[] firmaContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen) throws NoTrobatException;

	/** Mètode per crear o actualitzar un document a un procés
	 * 
	 * @param processInstanceId
	 * @param documentCodi
	 * @param data
	 * @param arxiu
	 * @param contingut
	 * @param annexosPerNotificar
	 * @return 
	 */
	public Long guardarDocumentProces(
			String processInstanceId, 
			String documentCodi, 
			Date data, 
			String arxiu,
			byte[] contingut,
			List<ExpedientDocumentDto> annexosPerNotificar);

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
	 * Al finalitzar un expedient, hem de recuperar tos els seus documents, tant de subprocessos, com de anotacions, notificacions o PINBAL.
	 * Alguns d'ells poden complir més de una condició: per exemple formar part de un procés i ser un annex de una anotació al mateix temps.
	 * Perque sigui més reutilitzable, no filtra per document firmat o no firmat. Els retorna tots.
	 */
	public ExpedientFinalitzarDto findDocumentsFinalitzar(Long expedientId) throws Exception;
	
	public boolean validarFinalitzaExpedient(Long expedientId) throws Exception;
	
	public List<DocumentListDto> findDocumentsExpedient(Long expedientId, Long nextEstatId, Boolean tots, PaginacioParamsDto paginacioParams) throws NoTrobatException, PermisDenegatException;
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
	
	/** Retorna l'arxiu PDF del document, si no és un PDF el converteix primer.
	 * 
	 * @param expedientId
	 * @param processInstanceId
	 * @param documentStoreId
	 * @return
	 */
	public ArxiuDto arxiuPdfFindAmbDocument(Long expedientId, String processInstanceId, Long documentStoreId);

	/**
	 * Retorna l'arxiu del document de la versió indicada.
	 * 
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @param documentStoreId
	 *             atribut id del document emmagatzemat.
	 * @param versio
	 *             atribut versio del document emmagatzemat.
	 * @return L'arxiu del document.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public ArxiuDto arxiuFindAmbDocumentVersio(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			String versio) throws NoTrobatException, PermisDenegatException;
	
	
	/**
	 * Retorna l'arxiu del document donat el seu documentStoreId.
	 * 
	 * @param documentId
	 * @return
	 * @throws NoTrobatException
	 */
	public ArxiuDto arxiuFindAmbDocumentStoreId(
			Long documentId) throws NoTrobatException;

	/** 
	 * Retorna el contingut original del document en cas d'estar integrat amb l'Arxiu. 
	 * 
	 * @param expedientId
	 * @param documentStoreId
	 * @return
	 */
	public ArxiuDto arxiuFindOriginal(
			Long expedientId, 
			Long documentStoreId) throws NoTrobatException;


	/**
	 * Consulta la llista de peticions al portafirmes en un estat pendent de processar a processat i rebutjat.
	 * 
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @return Retorna una llista de peticions pendents o processades i rebutjades. En el cas que hi hagi més d'una anotació per document llavors retorna només la darrera.
	 * 
	 * @throws NoTrobatException
	 * @throws PermisDenegatException
	 */
	public List<PortasignaturesDto> portasignaturesFindPendents(
			Long expedientId,
			String processInstanceId) throws NoTrobatException, PermisDenegatException;

	/** Mètode per consultar la informació del portasignatures pel documentId.
	 * 
	 * @param documentId
	 * @return
	 */
	public PortasignaturesDto getPortasignaturesByDocumentId(Integer documentId);
	
	/** Mètode per consultar la informació del portasignatures pel documentStoreId.
	 * 
	 * @param processInstanceId
	 * @param documentStoreId
	 * @return
	 */
	public PortasignaturesDto getPortasignaturesByDocumentStoreId(String processInstanceId, Long documentStoreId);

	public List<PortasignaturesDto> getPortasignaturesByProcessInstanceAndDocumentStoreId(
			String processInstanceId,
			Long documentStoreId);

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
	
	public ArxiuDto findArxiuAmbTokenPerMostrar(String token) throws NoTrobatException;

	public ArxiuDto findArxiuAmbTokenPerSignar(String token) throws NoTrobatException;

	public DocumentDto findDocumentAmbId(Long documentStoreId) throws NoTrobatException;

	public DocumentDetallDto getDocumentDetalls(Long expedientId, Long documentStoreId);

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

	/** Mètode per donar d'alta una notificació electrònica per un interessat. De moment crea
	 * una notificació per un interessat amb la possibilitat d'afegir un representant com a destinatari.
	 * 
	 * @param expedientId
	 * @param documentStoreId
	 * @param documentsDinsZip,
	 * @param dadesNotificacioDto
	 * @param interessatsId
	 * @param representantId
	 * @return
	 */
	public DadesNotificacioDto notificarDocument(
			Long expedientId, 
			Long documentStoreId, 
			List<DocumentStoreDto> documentsDinsZip,
			DadesNotificacioDto dadesNotificacioDto, 
			Long interessatsIds, 
			Long representantId);

	public PaginaDto<NotificacioDto> findNotificacionsPerDatatable(
			String filtre, 
			PaginacioParamsDto paginacioParams);

	/** Mètode per obtenir una firma en concret d'un arxiu
	 * 
	 * @param expedientId
	 * @param documentStoreId
	 * @param firmaIndex
	 * @return
	 */
	public ArxiuFirmaDto getArxiuFirma(
			Long expedientId, 
			Long documentStoreId, 
			int firmaIndex);

	/**
	 * Migra el document a l'arxiu
	 * 
	 * @param documentStoreId
	 *            Atribut documentStoreId del document.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap document amb el documentStoreId especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void migrarArxiu(
			Long expedientId,
			Long documentStoreId) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Migra el document a l'arxiu
	 * @param expedientId
	 * @param documentStoreId
	 */
	public void trySincronitzarArxiu(
			Long expedientId,
			Long documentStoreId);

    public Set<Long> findIdsDocumentsByExpedient(Long expedientId);

	/** Mètode per processar la firma en passarel·la des de la gestió de documents, validar la firma,
	 * gurardar el document firmat i deixar una entrada en el registre.
	 * @param documentStoreId
	 * @param arxiuNom
	 * @param processInstanceId 
	 * @param expedientId 
	 * @param contingutFirmat
	 */
	public void processarFirmaClient(
			Long expedientId, 
			String processInstanceId, 
			Long documentStoreId,
			String arxiuNom,
			byte[] contingutFirmat) throws PermisDenegatException;

	/** Envia un document al portasignatures.
	 * 
	 * @param document
	 * @param annexos
	 * @param personesPas
	 * @param minSignatarisPas
	 * @param expedient
	 * @param importancia
	 * @param dataLimit
	 * @param tokenId
	 * @param processInstanceId
	 * @param transicioOK
	 * @param transicioKO
	 * @param portafirmesTipus
	 * @param responsables
	 * @param portafirmesFluxId
	 * 
	 * @return Retorna l'identificador del document donat pel portasignatures.
	 */
	public void enviarPortasignatures(
			DocumentDto document,
			List<DocumentDto> annexos,
			ExpedientDto expedient,
			String importancia,
			Date dataLimit,
			Long tokenId,
			Long processInstanceId,
			String transicioOK,
			String transicioKO,
			PortafirmesSimpleTipusEnumDto portafirmesTipus,
			String[] responsables, 
			String portafirmesFluxId,
			PortafirmesTipusEnumDto fluxTipus) throws SistemaExternException;

	/**
	 * Cancela l'enviament d'un document a firmar al portafirmes.
	 * @param documentId	Id del portasignatures
	 */
	public void portafirmesCancelar(
		Integer documentId) throws SistemaExternException;



    public List<DocumentInfoDto> getDocumentsNoUtilitzatsPerEstats(Long expedientId);

    /** 
     * Mètode per iniciar una petició de firma per passarel·la web amb el Portafirmes.
     * 
     * @param persona Persona que ha de firmar.
     * @param arxiu Informació de l'arxiu a firmar.
     * @param signId Identificador assigat a la firma.
     * @param motiu Motiu de la firma.
     * @param lloc Lloc de la firma.
     * @param urlRetorn URL on es retornarà a Helium després de firmar o cancel·lar.
     * 
     * @return URL de retorn que s'ha de visualitzar per seguir amb la petició de firma delegada al Portafirmes.
     */
	public String firmaSimpleWebStart(
			PersonaDto persona, 
			ArxiuDto arxiu, 
			String signId,
			String motiu, 
			String lloc,
			String urlRetorn);

	/** 
	 * Mètode per finalitzar la petició de firma de la passarel·la i obtenir-ne el resultat.
	 * 
	 * @param transactionID Identificador de la petició de firma web.
	 * 
	 * @return Retorna un objecte amb informació del resultat de la firma i el document firmat en cas que hagi anat bé.
	 */
	public FirmaResultatDto firmaSimpleWebEnd(String transactionID);
	
	/**
	 * Genera un PDF amb una taula resum dels fitxers de l'expedient.
	 * @param expedientId
	 */
	public DocumentDto generarIndexExpedient(Long expedientId) throws Exception;
	/**
	 * Genera un ZIP amb els documents definitius de l'expedient i la informació ENI (format XML) per cada document i de l'expedient mateix
	 * a més de l'index en PDF de l'expedient.
	 * @param expedientId
	 */
	public DocumentDto exportarEniDocumentsAmbIndex(Long expedientId) throws Exception;
	/**
	 * Genera el fitxer ENI (format XML) de l'expedient.
	 * @param expedientId
	 */
	public DocumentDto exportarEniExpedient(Long expedientId) throws Exception;

	/**
	 * Mètode per migrar documents a Arxiu
	 * @param expedientId
	 * @param documentStoreId
	 */
	public void migrateDocument(Long expedientId, Long documentStoreId);
}