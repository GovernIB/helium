package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacioCommandDto;

/**
 * Servei per al manteniment de definicions de processos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DefinicioProcesService {

	/** Tasca la definició de proces per identificador.
	 * 
	 * @param definicioProcesId
	 * @return
	 */
	public DefinicioProcesDto findById(Long definicioProcesId);

	/**
	 * Retorna la darrera versió de la definició de procés donat l'identificador de l'entorn
	 * i l'identificador jbpm.
	 * @param entornId
	 * @param jbpmKey
	 * @return
	 */
	public DefinicioProcesDto findByEntornIdAndJbpmKey(
			Long entornId, 
			String jbpmKey);

	/**
	 * Retorna la llista de sub definicions de processos per a la definició de procés especificada.
	 * @param definicioProcesId
	 * @return
	 */
	public List<DefinicioProcesDto> findSubDefinicionsProces(Long definicioProcesId);
	
	/**
	 * Retorna la llista de darreres versions de les definicions de procés donat l'identificador de l'entorn
	 * i l'identificador del tipus d'expedient. Si no s'especifica el tipus d'expedient només es filtra per
	 * entorn.
	 * @param entornId
	 * @param expedientTipusId
	 * @return
	 */
	public List<DefinicioProcesDto> findAll(
			Long entornId, 
			Long expedientTipusId);
	
	/** 
	 * Retorna la llista de definicions de procés paginada per la datatable.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient si es volen mostrar només les de un expedient.
	 * @param incloureGlobals 
	 * 			  Especifica si incloure les definicions de procés amb expedientTipus null en cas d'especificar
	 * 				un expedientTipusId.
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param string 
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de definicions de procés.
	 */
	public PaginaDto<DefinicioProcesDto> findPerDatatable(
			Long entornId, 
			Long expedientTipusId,
			boolean incloureGlobals, 
			String filtre, 
			PaginacioParamsDto paginacioParams);

	/**
	 * Mètode per crear un objecte d'exportació per al tipus d'expedient amb la informació sol·licitada
	 * segons l'objecte DTO de la comanda d'exportació.
	 * @param entornId Id de l'entorn.
	 * @param Id de la definició de procés de la qual es realitza la exportació.
	 * @param command Objecte amb la informació que s'ha d'incloure a l'exportació.
	 * 
	 * @return Objecte d'exportació serialitzable.
	 */
	public DefinicioProcesExportacio exportar(
			Long entornId, 
			Long definicioProcesId,
			DefinicioProcesExportacioCommandDto command);

	

	/** Mètode per importar la informació d'un fitxer d'exportació de definicó de procés cap a una nova definició de
	 * procés si aquesta no està especificada o una definició de procés existent. La importació es fa de 
	 * forma selectiva segons la definicioProcesExportacioCommand.
	 * @param entornId Especifica l'entorn de treball de l'usuari.
	 * @param definicioProcesId Tipus d'expedient on fer la importació. Si està buit llavors es crea un de nou.
	 * @param expedientTipusId 
	 * @param command Llista de codis de la informació a importar.
	 * @param importacio Objecte desserialitzat amb la informació per a la importació.
	 * @return Retorna l'expedient tipus creat o modificat.
	 */
	public DefinicioProcesDto importar(
			Long entornId, 
			Long expedientTipusId, 
			Long definicioProcesId, 
			DefinicioProcesExportacioCommandDto command,
			DefinicioProcesExportacio importacio);
	
	/** Mètode per despublicar una definició de procés.
	 * 
	 * @param entornId Identificador de l'entorn per comprovar permisos.
	 * @param definicioProcesId Especifica la definició de procés a despublicar.
	 * @throws Exception Es llança excepció si no s'ha pogut esborrar amb el motiu com a missatge.
	 */
	public void delete(
			Long entornId,
			Long definicioProcesId) throws Exception;
	
	/** 
	 * Retorna la llista de tasques de la definició de procés paginada per la datatable.
	 * 
	 * @param definicioProcesId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param string 
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<TascaDto> tascaFindPerDatatable(
			Long entornId,
			Long definicioProcesId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;

	/** 
	 * Retorna la llista de tasques per a una definició de procés.
	 * 
	 * @param definicioProcesId
	 * 
	 * @return La llista de tasques de la definició de procés.
	 */
	public List<TascaDto> tascaFindAll(Long definicioProcesId);
	
	/** 
	 * Retorna la tasca de la definició de procés donat el seu identificador.
	 * 
	 * @param id
	 * 
	 * @return La tasca de la definició de procés.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public TascaDto tascaFindAmbId(
			Long id) throws NoTrobatException;
	
	/** Recupera la informació de la definició de procés d'una tasca donat el seu id. */
	public DefinicioProcesDto tascaFindDefinicioProcesDeTasca(Long tascaId);	
	
	/**
	 * Modificació d'una tasca existent.
	 * 
	 * @param tasca
	 *            La informació de la tasca a modificar.
	 * @return la tasca modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws CampDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public TascaDto tascaUpdate(TascaDto tasca) throws NoTrobatException, PermisDenegatException;	

	
	
	/**
	 * Crea un nou camp per la tasca.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca.
	 * @param tascaCamp
	 *            La informació del camp de la tasca a crear.
	 * @return el camp de la tasca creat.
	 * @throws TascaCampDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampTascaDto tascaCampCreate(
			Long tascaId,
			CampTascaDto tascaCamp) throws PermisDenegatException;
	
	/**
	 * Modificació d'un camp de tasca existent.
	 * 
	 * @param tascaCamp
	 *            La informació del camp del registre a modificar.
	 * @return el camp del registre modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampTascaDto tascaCampUpdate(
			CampTascaDto tascaCamp) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra un camp de la tasca.
	 * 
	 * @param id
	 *            Atribut id del camp de la tasca.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void tascaCampDelete(
			Long id) throws NoTrobatException, PermisDenegatException;	
	

	/** 
	 * Retorna la llista de camps de la tasca de la definició de procés paginada per la datatable.
	 * 
	 * @param tascaId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina de la llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<CampTascaDto> tascaCampFindPerDatatable(
			Long tascaId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;	

	/** Mou el camp de la tasca amb id de camp cap a la posició indicada reassignant el valor pel camp ordre.
	 * 
	 * @param id
	 * @param posicio
	 * @return Retorna true si ha anat bé o false si no té agrupació o la posició no és correcta.
	 */
	public boolean tascaCampMourePosicio(Long id, int posicio);

	/** Consulta els camps utilitzats per a una tasca.
	 * 
	 * @param tascaId
	 * @return
	 */
	public List<CampTascaDto> tascaCampFindCampAmbTascaId(Long tascaId);

	/** 
	 * Retorna el camp tasca de la definició de procés donat el seu identificador.
	 * 
	 * @param id
	 * 
	 * @return El camp tasca de la definició de procés.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public CampTascaDto tascaCampFindById(Long campTascaId);
	
	
	/**
	 * Crea un nou document per la tasca.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca.
	 * @param tascaDocument
	 *            La informació del document de la tasca a crear.
	 * @return el document de la tasca creat.
	 * @throws TascaDocumentDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public DocumentTascaDto tascaDocumentCreate(
			Long tascaId,
			DocumentTascaDto tascaDocument) throws PermisDenegatException;
	
	/**
	 * Modificació d'un document de tasca existent.
	 * 
	 * @param tascaDocument
	 *            La informació del document del registre a modificar.
	 * @return el document del registre modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public DocumentTascaDto tascaDocumentUpdate(
			DocumentTascaDto tascaDocument) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra un document de la tasca.
	 * 
	 * @param id
	 *            Atribut id del document de la tasca.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void tascaDocumentDelete(
			Long id) throws NoTrobatException, PermisDenegatException;	
	

	/** 
	 * Retorna la llista de documents de la tasca de la definició de procés paginada per la datatable.
	 * 
	 * @param tascaId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina de la llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<DocumentTascaDto> tascaDocumentFindPerDatatable(
			Long tascaId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;	

	/** Mou el document de la tasca amb id de document cap a la posició indicada reassignant el valor pel document ordre.
	 * 
	 * @param id
	 * @param posicio
	 * @return Retorna true si ha anat bé o false si no té agrupació o la posició no és correcta.
	 */
	public boolean tascaDocumentMourePosicio(Long id, int posicio);

	/** Consulta els documents utilitzats per a una tasca.
	 * 
	 * @param tascaId
	 * @return
	 */
	public List<DocumentTascaDto> tascaDocumentFindDocumentAmbTascaId(Long tascaId);

	/** 
	 * Retorna el document tasca de la definició de procés donat el seu identificador.
	 * 
	 * @param id
	 * 
	 * @return El document tasca de la definició de procés.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public DocumentTascaDto tascaDocumentFindById(Long documentTascaId);
	

	
	/**
	 * Crea una nova firma per la tasca.
	 * 
	 * @param tascaId
	 *            Atribut id de la tasca.
	 * @param tascaFirma
	 *            La informació de la firma de la tasca a crear.
	 * @return la firma de la tasca creat.
	 * @throws TascaFirmaDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public FirmaTascaDto tascaFirmaCreate(
			Long tascaId,
			FirmaTascaDto tascaFirma) throws PermisDenegatException;
	
	/**
	 * Modificació d'una firma de tasca existent.
	 * 
	 * @param tascaFirma
	 *            La informació de la firma del registre a modificar.
	 * @return la firma del registre modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public FirmaTascaDto tascaFirmaUpdate(
			FirmaTascaDto tascaFirma) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra una firma de la tasca.
	 * 
	 * @param id
	 *            Atribut id de la firma de la tasca.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void tascaFirmaDelete(
			Long id) throws NoTrobatException, PermisDenegatException;	
	

	/** 
	 * Retorna la llista de firmes de la tasca de la definició de procés paginada per la datatable.
	 * 
	 * @param tascaId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina de la llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<FirmaTascaDto> tascaFirmaFindPerDatatable(
			Long tascaId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;	

	/** Mou la firma de la tasca amb id de firma cap a la posició indicada reassignant el valor per la firma ordre.
	 * 
	 * @param id
	 * @param posicio
	 * @return Retorna true si ha anat bé o false si no té agrupació o la posició no és correcta.
	 */
	public boolean tascaFirmaMourePosicio(Long id, int posicio);

	/** Consulta les firmes utilitzades per a una tasca.
	 * 
	 * @param tascaId
	 * @return
	 */
	public List<FirmaTascaDto> tascaFirmaFindAmbTascaId(Long tascaId);

	/** 
	 * Retorna la firma tasca de la definició de procés donat el seu identificador.
	 * 
	 * @param id
	 * 
	 * @return La firma tasca de la definició de procés.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public FirmaTascaDto tascaFirmaFindById(Long firmaTascaId);	
	
	/**
	 * Retorna una definicio de procés donat el seu id per a dissenyar.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param definicioProcesId
	 *            Atribut id de la definicio de proces.
	 * @return Definicio de proces
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public DefinicioProcesDto findAmbIdAndEntorn(
			Long entornId,
			Long definicioProcesId) throws NoTrobatException;

	/**
	 * Retorna els terminis per a una definició de procés.
	 * 
	 * @param definicioProcesId
	 *            Atribut id del tipus d'expedient.
	 * @return els terminis del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<TerminiDto> terminiFindAll(
			Long definicioProcesId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna les consultes per l'entorn
	 * 
	 * @param entorndId
	 *            Atribut id de l'entorn
	 * @return les consultes de l'entorn
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<ConsultaDto> consultaFindByEntorn(Long entornId) throws NoTrobatException, PermisDenegatException;

	/** Copia la informació de la definició de procés origen cap a la definició de procés
	 * destí.
	 * @param origenId
	 * @param destiId
	 */
	public void copiarDefinicioProces(
			Long origenId, 
			Long destiId);
	
	/** Consulta el nom de la tasca inicial de la definició de procés.
	 * 
	 * @param definicioProcesId
	 * @return
	 */
	public String consultarStartTaskName(Long definicioProcesId);

}