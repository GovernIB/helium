/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
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
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public void createOrUpdate(
			Long expedientId,
			String processInstanceId,
			Long documentId,
			Long documentStoreId,
			String titol,
			String arxiuNom,
			byte[] arxiuContingut,
			Date data) throws NoTrobatException, PermisDenegatException;

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

	/* PER REVISAR! */
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

	/*
	public List<DocumentDto> dissenyFindAmbDefinicioProces(
			Long definicioProcesId,
			String processInstanceId,
			String expedientTipusNom) throws NoTrobatException, PermisDenegatException;
	public DocumentDto findById(Long id) throws NoTrobatException;
	public ArxiuDto arxiuPerMostrar(String token);
	public ArxiuDto arxiuPerSignar(String token);
	public ExpedientDocumentDto findDocumentPerDocumentStoreId(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) throws NoTrobatException;
	public Long findDocumentStorePerInstanciaProcesAndDocumentCodi(
			String processInstanceId,
			String documentCodi);*/

}
