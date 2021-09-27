/**
 * 
 */
package es.caib.helium.logic.intf.service;

import es.caib.helium.logic.intf.dto.*;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.exception.SistemaExternTimeoutException;
import es.caib.helium.logic.intf.exception.ValidacioException;
import org.springframework.security.acls.model.NotFoundException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	 * @throws NoTrobatException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 */
	public ExpedientTascaDto findAmbIdPerExpedient(
			String id,
			Long expedientId) throws NoTrobatException;

	/**
	 * Consulta d'informació d'una tasca per a tramitar-la.
	 * 
	 * @param id
	 *            Atribut id de la tasca que es vol consultar.
	 * @return La informació de la tasca.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 */
	public ExpedientTascaDto findAmbIdPerTramitacio(
			String id) throws NoTrobatException;

	/**
	 *  Consulta d'ids de tasques segons el filtre.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn l'expedient que es vol consultar.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param titol
	 *            Fragment del títol de la tasca.
	 * @param tasca
	 *            Tasca que es vol consultar.
	 * @param responsable
	 *            Usuari responsable de la tasca.
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
	 * @param nomesTasquesPersonals
	 *            Check de mostrar només les tasques personals.
	 * @param nomesTasquesGrup
	 *            Check de mostrar només les tasques de grup.
	 * @return La llista d'ids de tasca.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat algun dels elements especificats
	 *             mitjançant el seu id (entorn, tipus, estat).
	 * @throws PermisDenegatException
	 *             Si no es tenen permisos per a accedir als elements
	 *             especificats mitjançant el seu id (entorn, tipus, estat).
	 */
	public List<String> findIdsPerFiltre(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String tasca,
			String responsable,
			List<String> grups,
			String expedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataLimitInici,
			Date dataLimitFi,
			Integer prioritat,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup, 
			boolean nomesTasquesMeves) throws NoTrobatException, PermisDenegatException;

	/**
	 *  Consulta de tasques segons el filtre amb paginació.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn l'expedient que es vol consultar.
	 * @param tramitacioMassivaTascaId 
	 *            Atribut id de la tasca que es vol tramitar massivament.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param titol
	 *            Fragment del títol de la tasca.
	 * @param tasca
	 *            Tasca que es vol consultar.
	 * @param responsable
	 *            Usuari responsable de la tasca.
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
	 * @param nomesTasquesPersonals
	 *            Check de mostrar només les tasques personals.
	 * @param nomesTasquesGrup
	 *            Check de mostrar només les tasques de grup.
	 * @param nomesTasquesMeves
	 *            Check de mostrar només les tasques de l'usuari actual.
	 * @param paginacioParams
	 *            Paràmetres de paginació.
	 * @return La pàgina del llistat de tasques.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat algun dels elements especificats
	 *             mitjançant el seu id (entorn, tipus, estat).
	 * @throws PermisDenegatException
	 *             Si no es tenen permisos per a accedir als elements
	 *             especificats mitjançant el seu id (entorn, tipus, estat).
	 */
	public PaginaDto<ExpedientTascaDto> findPerFiltrePaginat(
			Long entornId,
			String tramitacioMassivaTascaId,
			Long expedientTipusId,
			String titol,
			String tasca,
			String responsable,
			String expedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataLimitInici,
			Date dataLimitFi,
			Integer prioritat,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup, 
			boolean nomesTasquesMeves,
			PaginacioParamsDto paginacioParams) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna els camps i les dades de la tasca per a la construcció
	 * del formulari.
	 * 
	 * @param id
	 *            Atribut id de la tasca que es vol consultar.
	 * @return Les dades de la tasca.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 */
	public List<TascaDadaDto> findDades(
			String id);

	/**
	 * Retorna els documents de la tasca.
	 * 
	 * @param id
	 *            Atribut id de la tasca que es vol consultar.
	 * @return Els documents de la tasca.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 */
	public List<TascaDocumentDto> findDocuments(
			String id) throws Exception;

	/**
	 * Retorna la llista de possibles valors per a un camp de tipus
	 * selecció d'una tasca.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca.
	 * @param processInstanceId
	 *            Atribut processInstanceId de la tasca.
	 * @param campId
	 *            Atribut id del camp.
	 * @param codiFiltre
	 *            Codi per filtrar resultats.
	 * @param textFiltre
	 *            Text per filtrar resultats.
	 * @param registreCampId
	 *            El camp de la variable de tipus registre superior.
	 * @param registreIndex
	 *            La fila dins la variable de tipus registre.
	 * @param valorsFormulari
	 *            Els valors dels camps del formulari.
	 * @return la llista de valors
	 * @throws SistemaExternException
	 * @throws SistemaExternTimeoutException
	 */
	public List<SeleccioOpcioDto> findValorsPerCampDesplegable(
			String tascaId,
			String processInstanceId,
			Long campId,
			String codiFiltre,
			String textFiltre,
			Long registreCampId,
			Integer registreIndex,
			Map<String, Object> valorsFormulari) throws Exception;

	/**
	 * Agafa una tasca assignada a aquest usuari com a tasca de grup.
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @return la tasca agafada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat a dins
	 *             les tasques de grup de l'usuari.
	 */
	public ExpedientTascaDto agafar(
			String id) throws NoTrobatException;

	/**
	 * Allibera una tasca assignada a aquest usuari.
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @return la tasca agafada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 */
	public ExpedientTascaDto alliberar(
			String id);

	/**
	 * Guarda les variables del formulari de la tasca.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca.
	 * @param variables
	 *            Valors del formulari de la tasca.
	 * @return la tasca guardada.
	 */
	public void guardar(
			String tascaId,
			Map<String, Object> variables) throws Exception;

	/**
	 * Valida el formulari de la tasca.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca.
	 * @param variables
	 *            Valors del formulari de la tasca.
	 * @return la tasca validada.
	 */
	public void validar(
			String tascaId,
			Map<String, Object> variables) throws Exception;

	/**
	 * Restaura (tornar enrere validació) el formulari de la tasca.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca.
	 * @return la tasca restaurada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 */
	public void restaurar(String tascaId);

	/**
	 * Completa la tasca.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca.
	 * @param outcome
	 *            Transició de sortida de la tasca.
	 * @return la tasca completada.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws ValidacioException
	 *             Si la tasca no es troba en disposició de ser completada.
	 */
	public void completar(
			String tascaId,
			String outcome) throws NoTrobatException, ValidacioException;
	
	/**
	 * Completa la tasca des de l'execució massiva.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca.
	 * @param outcome
	 *            Transició de sortida de la tasca.
	 * @return la tasca completada.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws ValidacioException
	 *             Si la tasca no es troba en disposició de ser completada.
	 */
	public void completarMassiu(
			String tascaId,
			String outcome) throws NoTrobatException, ValidacioException;

	/**
	 * Cancel·la la delegació d'una tasca. Aquesta acció només la podrà fer
	 * l'usuari que ha creat la delegació.
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @param accio
	 *            Nom de l'acció a executar.
	 */
	public void executarAccio(
			String id,
			String accio);

	/**
	 * Inicia un formulari extern i retorna les dades per a obrir
	 * una finestra amb el formulari.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca.
	 * @return
	 */
	public FormulariExternDto formulariExternObrir(
			String tascaId);
	
	public FormulariExternDto formulariExternObrirTascaInicial(
			String tascaIniciId,
			Long expedientTipusId,
			Long definicioProcesId);

	public List<TascaDadaDto> findDadesPerTascaDto(Long expedientTipusId, ExpedientTascaDto tasca);
	
	public List<ExpedientTascaDto> findAmbIds(Set<String> ids);

	/**
	 * Retorna l'arxiu corresponent a un document de la tasca.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca que es vol consultar.
	 * @param documentCodi
	 *            Codi del document de la tasca que es vol descarregar.
	 * @return L'arxiu del document.
	 */
	public ArxiuDto getArxiuPerDocumentCodi(
			String tascaId,
			String documentCodi) throws Exception;
	
	public DocumentDto getDocumentPerDocumentCodi(String tascaId, String documentCodi) throws Exception;
	
	public TascaDocumentDto findDocument(String tascaId, Long docId, Long expedientTipusId) throws Exception;

	public Long guardarDocumentTasca(
			Long entornId, 
			String taskInstanceId, 
			String documentCodi, 
			Date documentData, 
			String arxiuNom, 
			byte[] arxiuContingut,
			String arxiuContentType, 
			boolean ambFirma,
			boolean firmaSeparada,
			byte[] firmaContingut,
			String user) throws Exception;

	public void esborrarDocument(String taskInstanceId, String documentCodi, String user) throws Exception;

	public boolean signarDocumentTascaAmbToken(String tascaId, String token, byte[] signatura) throws Exception;

	public List<TascaDocumentDto> findDocumentsSignar(String id) throws Exception;

	public boolean hasFormulari(String tascaId);

	public boolean hasDocuments(String tascaId);
	
	public boolean hasDocumentsNotReadOnly(String id);

	public boolean hasSignatures(String tascaId);

	public boolean isTascaValidada(String tascaId);

	public boolean isDocumentsComplet(String tascaId);

	public boolean isSignaturesComplet(String tascaId);
	
	public void comprovarTasquesSegonPla();

	public void carregaTasquesSegonPla();
	
	public void completaTascaSegonPla(String tascaId, Date iniciFinalitzacio);
	
	public void guardarErrorFinalitzacio(String tascaId, String errorFinalitzacio);

	public Map<String, Object> obtenirEstatsPerIds(List<String> tasquesSegonPlaIds);
	
	/**
	 * Retorna si la TaskInstance està registrada en segon pla
	 * en execució o per ser executada
	 * 
	 * @param tascaSegonPlaId
	 *            Atribut id de la tasca que es vol consultar.
	 * @return Retorna si està registrada en segón pla o no
	 */ 
	public boolean isEnSegonPla(String tascaSegonPlaId);
	
	public List<String[]> getMissatgesExecucioSegonPla(String tascaSegonPlaId);
	
	public void updateVariable(Long expedientId, String taskId, String codiVariable, Object valor) throws NotFoundException, IllegalStateException;
	
	/**
	 * Retiorna la tasca de la ID especificada.
	 * 
	 * @param id
	 * @return
	 */
	public TascaDto findTascaById(Long id);

	/** Mètode per guardar valors des del servei de formulari extern.
	 * 
	 * @param formulariId Identificador del formulari extern.
	 * @param valorsTasca Valors per les variables.
	 */
	public void guardarFormulariExtern(String formulariId, Map<String, Object> valorsTasca) throws Exception;

	/** Mètode per consultar si internament ha guardat les dades del formulari inicial. Ho guarda
	 * en un Map intern.
	 * 
	 * @param formulariId
	 * @return
	 */
	public Map<String, Object> obtenirValorsFormulariExternInicial(String formulariId);

}
