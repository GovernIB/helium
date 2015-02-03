/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.exception.IllegalStateException;
import net.conselldemallorca.helium.v3.core.api.exception.NotAllowedException;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TascaService {

	/**
	 * Consulta d'informació d'una tasca comprovant que pertany
	 * a l'expedient especificat.
	 * 
	 * @param id
	 *            Atribut id de la tasca que es vol consultar.
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol comprovar.
	 * @return La informació de la tasca.@throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientTascaDto findAmbIdPerExpedient(
			String id,
			Long expedientId);

	/**
	 * Consulta d'informació d'una tasca per a tramitar-la.
	 * 
	 * @param id
	 *            Atribut id de la tasca que es vol consultar.
	 * @return La informació de la tasca.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientTascaDto findAmbIdPerTramitacio(
			String id);

	/**
	 *  Consulta de tasques per entorn paginada.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn l'expedient que es vol consultar.
	 * @param consultaTramitacioMassiva 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param tasca
	 *            Fragment del títol de la tasca.
	 * @param expedient
	 *            Fragment del títol de l'expedient.
	 * @param dataCreacioInici
	 *            Data de creació inicial.
	 * @param dataCreacioFi
	 *            Data de creació final.
	 * @param dataLimitInici
	 *            Data límit inicial.
	 * @param dataLimitFi
	 *            Data límit final.
	 * @param prioritat
	 *            Prioritat de l atasca.
	 * @param mostrarTasquesPersonals
	 *            Check de mostrar tasques personals.
	 * @param mostrarTasquesGrup
	 *            Check de mostrar tasques de grup.
	 * @param paginacioParams
	 *            Paràmetres de paginació.
	 * @return La pàgina del llistat de tasques.
	 * @throws NotFoundException
	 *             Si no s'ha trobat algun dels elements especificats
	 *             mitjançant el seu id (entorn, tipus, estat).
	 * @throws NotAllowedException
	 *             Si no es tenen permisos per a accedir als elements
	 *             especificats mitjançant el seu id (entorn, tipus, estat).
	 */
	public PaginaDto<ExpedientTascaDto> findPerFiltrePaginat(
			Long entornId,
			String consultaTramitacioMassivaTascaId,
			Long expedientTipusId,
			String responsable,
			String tasca,
			String expedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataLimitInici,
			Date dataLimitFi,
			Integer prioritat,
			boolean mostrarTasquesPersonals,
			boolean mostrarTasquesGrup,
			PaginacioParamsDto paginacioParams);

	/**
	 * Retorna els camps i les dades de la tasca per a la construcció
	 * del formulari.
	 * 
	 * @param id
	 *            Atribut id de la tasca que es vol consultar.
	 * @return Les dades de la tasca.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<TascaDadaDto> findDades(
			String id);

	/**
	 * Retorna un camp del formulari de la tasca.
	 * 
	 * @param id
	 *            Atribut id de la tasca que es vol consultar.
	 * @param variableCodi
	 *            Codi de la variable que es vol consultar.
	 * @return La dada de la tasca.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public TascaDadaDto findDada(
			String id,
			String variableCodi);

	/**
	 * Retorna els documents de la tasca.
	 * 
	 * @param id
	 *            Atribut id de la tasca que es vol consultar.
	 * @return Els documents de la tasca.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<TascaDocumentDto> findDocuments(
			String id);

	/**
	 * Retorna la llista de possibles valors per a un camp de tipus
	 * selecció d'una tasca.
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @param campId
	 *            Atribut id del camp.
	 * @param valorsFormulari
	 *            Els valors dels camps del formulari.
	 * @return la llista de valors
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<SeleccioOpcioDto> findllistaValorsPerCampDesplegable(
			String id,
			Long campId,
			String textFiltre,
			Map<String, Object> valorsFormulari);

	/**
	 * Agafa una tasca assignada a aquest usuari com a tasca de grup.
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @return la tasca agafada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat a dins
	 *             les tasques de grup de l'usuari.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientTascaDto agafar(
			String id);

	/**
	 * Allibera una tasca assignada a aquest usuari.
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @return la tasca agafada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientTascaDto alliberar(
			String id);

	/**
	 * Guarda les variables del formulari de la tasca.
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @param variables
	 *            Valors del formulari de la tasca.
	 * @return la tasca guardada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientTascaDto guardar(
			String id,
			Map<String, Object> variables);

	/**
	 * Valida el formulari de la tasca.
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @param variables
	 *            Valors del formulari de la tasca.
	 * @return la tasca validada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientTascaDto validar(
			String id,
			Map<String, Object> variables);

	/**
	 * Restaura (tornar enrere validació) el formulari de la tasca.
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @return la tasca restaurada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 * @throws IllegalStateException
	 *             Si la tasca no es troba en estat validada.
	 */
	public ExpedientTascaDto restaurar(String id);

	/**
	 * Completa la tasca.
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @param outcome
	 *            Transició de sortida de la tasca.
	 * @return la tasca completada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 * @throws IllegalStateException
	 *             Si la tasca no es troba en disposició de ser completada.
	 */
	public void completar(
			String id,
			String outcome);

	/**
	 * Delega la tramitació d'una tasca a un altre usuari.
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @param usuariDesti
	 *            L'usuari destinatari de la delegació.
	 * @param comentari
	 *            El comentari de la delegació.
	 * @param supervisada
	 *            Indica si la delegació ha de ser supervisada o no.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void delegacioCrear(
			String id,
			String usuariDesti,
			String comentari,
			boolean supervisada);

	/**
	 * Cancel·la la delegació d'una tasca. Aquesta acció només la podrà fer
	 * l'usuari que ha creat la delegació.
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 * @throws IllegalStateException
	 *             Si la tasca no ha estat delegada.
	 */
	public void delegacioCancelar(
			String id);

	/**
	 * Cancel·la la delegació d'una tasca. Aquesta acció només la podrà fer
	 * l'usuari que ha creat la delegació.
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @param accio
	 *            Nom de l'acció a executar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void executarAccio(
			String id,
			String accio);

	/**
	 * Guarda una fila d'una variable de tipus registre
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @param campCodi
	 *            Codi de la variable de tipus registre.
	 * @param index
	 *            Posició a on insertar la fila del registre. Si s'especifica
	 *            en valor -1 s'inserta al final.
	 * @param valors
	 *            Valors de la fila del registre.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void guardarFilaRegistre(
			String id,
			String campCodi,
			int index,
			Object[] valors);

	/**
	 * Esborra una fila d'una variable de tipus registre
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @param campCodi
	 *            Codi de la variable de tipus registre.
	 * @param index
	 *            Posició a on esborrar la fila del registre.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void esborrarFilaRegistre(
			String id,
			String campCodi,
			int index);

	public List<TascaDadaDto> findDadesPerTascaDto(ExpedientTascaDto tasca);
	
	public List<ExpedientTascaDto> findDadesPerIds(Set<Long> ids);

	public List<Long> findIdsPerFiltre(Long entornId, Long expedientTipusId, String responsable, String tasca, String expedient, Date dataCreacioInici, Date dataCreacioFi, Date dataLimitInici, Date dataLimitFi, Integer prioritat, boolean mostrarTasquesPersonals, boolean mostrarTasquesGrup);

	public TascaDocumentDto findDocument(String tascaId, Long docId);

	public Long guardarDocumentTasca(Long entornId, String taskInstanceId, String documentCodi, Date documentData, String arxiuNom, byte[] arxiuContingut, String user);

	public void esborrarDocument(String taskInstanceId, String documentCodi, String user);

	public List<RespostaValidacioSignaturaDto> verificarSignatura(String tascaId, Long docId) throws Exception;

	public boolean signarDocumentTascaAmbToken(Long expedientId, Long docId, String token, String tascaId, byte[] signatura) throws Exception;

	public List<TascaDocumentDto> findDocumentsSignar(String id);

	public boolean hasDocuments(String tascaId);

	public boolean hasDocumentsSignar(String tascaId);

	public List<ParellaCodiValorDto> getTasquesExecucionsMassivesAmbDefinicioProcesId(Long definicioProcesId);
}
