/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.FormulariExternDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
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
	 * @throws NotFoundException
	 *             Si no s'ha trobat algun dels elements especificats
	 *             mitjançant el seu id (entorn, tipus, estat).
	 * @throws NotAllowedException
	 *             Si no es tenen permisos per a accedir als elements
	 *             especificats mitjançant el seu id (entorn, tipus, estat).
	 */
	public List<Long> findIdsPerFiltre(
			Long entornId,
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
			boolean nomesTasquesMeves) throws NotFoundException, NotAllowedException;

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
	 * @throws NotFoundException
	 *             Si no s'ha trobat algun dels elements especificats
	 *             mitjançant el seu id (entorn, tipus, estat).
	 * @throws NotAllowedException
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
			PaginacioParamsDto paginacioParams) throws NotFoundException, NotAllowedException;

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
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<SeleccioOpcioDto> findValorsPerCampDesplegable(
			String tascaId,
			String processInstanceId,
			Long campId,
			String codiFiltre,
			String textFiltre,
			Long registreCampId,
			Integer registreIndex,
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
	 * @param tascaId
	 *            Atribut id de la tasca.
	 * @param expedientId 
	 * @param variables
	 *            Valors del formulari de la tasca.
	 * @return la tasca guardada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void guardar(
			String tascaId,
			Long expedientId, 
			Map<String, Object> variables);

	/**
	 * Valida el formulari de la tasca.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca.
	 * @param variables
	 *            Valors del formulari de la tasca.
	 * @return la tasca validada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void validar(
			String tascaId,
			Long expedientId,
			Map<String, Object> variables);

	/**
	 * Restaura (tornar enrere validació) el formulari de la tasca.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca.
	 * @param expedientId 
	 * @return la tasca restaurada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la tasca amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 * @throws IllegalStateException
	 *             Si la tasca no es troba en estat validada.
	 */
	public void restaurar(String tascaId, Long expedientId);

	/**
	 * Completa la tasca.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca.
	 * @param expedientId 
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
			String tascaId,
			Long expedientId,
			String outcome);

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
	 * Inicia un formulari extern i retorna les dades per a obrir
	 * una finestra amb el formulari.
	 * 
	 * @param id
	 *            Atribut id de la tasca.
	 * @return
	 */
	public FormulariExternDto formulariExternObrir(
			String tascaId);
	
	public FormulariExternDto formulariExternObrirTascaInicial(
			String tascaIniciId,
			Long expedientTipusId,
			Long definicioProcesId);

	public List<TascaDadaDto> findDadesPerTascaDto(ExpedientTascaDto tasca);
	
	public List<ExpedientTascaDto> findAmbIds(Set<Long> ids);

	/**
	 * Retorna l'arxiu corresponent a un document de la tasca.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca que es vol consultar.
	 * @param documentCodi
	 *            Codi del document de la tasca que es vol descarregar.
	 * @return L'arxiu del document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ArxiuDto getArxiuPerDocumentCodi(
			String tascaId,
			String documentCodi);
	
	public TascaDocumentDto findDocument(String tascaId, Long docId);

	public Long guardarDocumentTasca(Long entornId, String taskInstanceId, String documentCodi, Date documentData, String arxiuNom, byte[] arxiuContingut, String user);

	public void esborrarDocument(String taskInstanceId, String documentCodi, String user);

	public boolean signarDocumentTascaAmbToken(Long expedientId, Long docId, String token, String tascaId, byte[] signatura) throws Exception;

	public List<TascaDocumentDto> findDocumentsSignar(String id);

	public boolean hasFormulari(String tascaId);

	public boolean hasDocuments(String tascaId);

	public boolean hasSignatures(String tascaId);

	public boolean isTascaValidada(String tascaId);

	public boolean isDocumentsComplet(String tascaId);

	public boolean isSignaturesComplet(String tascaId);
	
	public void comprovarTasquesSegonPla();

	public void carregaTasquesSegonPla();
	
	public void completaTascaSegonPla(String tascaId, Date iniciFinalitzacio);
	
	public void guardarErrorFinalitzacio(String tascaId, String errorFinalitzacio);
}
