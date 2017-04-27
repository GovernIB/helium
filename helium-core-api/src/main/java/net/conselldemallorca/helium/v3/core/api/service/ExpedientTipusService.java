package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto.TipusConsultaCamp;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.exception.ExportException;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exportacio.ExpedientTipusExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ExpedientTipusExportacioCommandDto;;

/**
 * Servei per al manteniment de tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTipusService {

	/**
	 * Crea un nou tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipus
	 *            La informació del tipus d'expedient a crear.
	 * @param sequenciesAny
	 *            Els anys de les seqüències.
	 * @param sequenciesValor
	 *            Els valors de les seqüències.
	 * @return el tipus d'expedient creat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto create(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor) throws NoTrobatException, PermisDenegatException;

	/**
	 * Modificació d'un tipus d'expedient existent.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipus
	 *            La informació del tipus d'expedient per a fer la modificació.
	 * @param sequenciesAny
	 *            Els anys de les seqüències.
	 * @param sequenciesValor
	 *            Els valors de les seqüències.
	 * @return el tipus d'expedient modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto update(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor) throws NoTrobatException, PermisDenegatException;

	/** Modifica les dades del tipus d'expedient referents a la integració amb formularis externs.
	 * 
	 * @param entornId
	 * @param expedientTipusId
	 * @param url
	 * @param usuari
	 * @param contrasenya
	 * 
	 * @return El tipus d'expedient modificat.
	 * 
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto updateIntegracioForms(
			Long entornId, 
			Long expedientTipusId, 
			String url, 
			String usuari, 
			String contrasenya);		

	/** Modifica les dades del tipus d'expedient referents amb la integració amb els tràmits de 
	 * Sistra.
	 * 
	 * 
	 * @return El tipus d'expedient modificat.
	 * 
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto updateIntegracioTramits(
			Long entornId, 
			Long expedientTipusId, 
			String tramitCodi,
			boolean notificacionsActivades,
			String notificacioOrganCodi,
			String notificacioOficinaCodi,
			String notificacioUnitatAdministrativa,
			String notificacioCodiProcediment,
			String notificacioAvisTitol,
			String notificacioAvisText,
			String notificacioAvisTextSms,
			String notificacioOficiTitol,
			String notificacioOficiText);

	/**
	 * Esborra una entitat.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void delete(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;

	
	/**
	 * Mètode per crear un objecte d'exportació per al tipus d'expedient amb la informació sol·licitada
	 * segons l'objecte DTO de la comanda d'exportació.
	 * @param command Objecte amb la informació que s'ha d'incloure a l'exportació.
	 * @return Objecte d'exportació serialitzable.
	 */
	public ExpedientTipusExportacio exportar(
			Long entornId,
			Long expedientTipusId,
			ExpedientTipusExportacioCommandDto command);
	
	/** Mètode per importar la informació d'un fitxer d'exportació de tipus d'expedient cap a un nou tipus
	 * d'expedient si aquest no està especificat o un tipus d'expedient existent. La importació es fa de 
	 * forma selectiva segons el expedientTipusExportacioCommand.
	 * @param entornId Especifica l'entorn de treball de l'usuari.
	 * @param expedientTipusId Tipus d'expedient on fer la importació. Si està buit llavors es crea un de nou.
	 * @param command Llista de codis de la informació a importar.
	 * @param importacio Objecte desserialitzat amb la informació per a la importació.
	 * @return Retorna l'expedient tipus creat o modificat.
	 */
	public ExpedientTipusDto importar(
			Long entornId, 
			Long expedientTipusId, 
			ExpedientTipusExportacioCommandDto command,
			ExpedientTipusExportacio importacio);	

	
	/**
	 * Retorna els tipus d'expedient d'un entorn que es poden consultar.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return Els tipus d'expedient amb permis de consulta.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<ExpedientTipusDto> findAmbEntornPermisConsultar(
			Long entornId) throws NoTrobatException;

	/**
	 * Retorna un tipus d'expedient donat el seu id per a consultar.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return El tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto findAmbIdPermisConsultar(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna els tipus d'expedient d'un entorn que es poden dissenyar.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return Els tipus d'expedient amb permisos de disseny.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<ExpedientTipusDto> findAmbEntornPermisDissenyar(
			Long entornId) throws NoTrobatException;

	/**
	 * Retorna un tipus d'expedient donat el seu id per a dissenyar.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return El tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto findAmbIdPermisDissenyar(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna un tipus d'expedient donat el seu id per a dissenyar amb permís delegat. El permís
	 * delegat és menys restrictiu que el permís d'administrador.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return El tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto findAmbIdPermisDissenyarDelegat(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Retorna els tipus d'expedient d'un entorn que es poden iniciar.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return Els tipus d'expedient amb permis de creació.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<ExpedientTipusDto> findAmbEntornPermisCrear(
			Long entornId) throws NoTrobatException;

	/**
	 * Retorna un tipus d'expedient donat el seu codi.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param codi
	 *            El codi per a la consulta.
	 * @return El tipus d'expedient o null si no el troba.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public ExpedientTipusDto findAmbCodiPerValidarRepeticio(
			Long entornId,
			String codi) throws NoTrobatException;

	/** 
	 * Retorna la llista de tipus d'expedient paginada per la datatable.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<ExpedientTipusDto> findPerDatatable(
			Long entornId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;

	/**
	 * Modifica un permis existent d'un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param permis
	 *            La informació del permis.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void permisUpdate(
			Long entornId,
			Long expedientTipusId,
			PermisDto permis) throws NoTrobatException, PermisDenegatException;

	/**
	 * Esborra un permis existent d'un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param permisId
	 *            Atribut id del permis.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void permisDelete(
			Long entornId,
			Long expedientTipusId,
			Long permisId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna els permisos per a un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return els permisos del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<PermisDto> permisFindAll(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna un permis donat el seu id.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param permisId
	 *            Atribut id del permis.
	 * @return el permis amb l'id especificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public PermisDto permisFindById(
			Long entornId,
			Long expedientTipusId,
			Long permisId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna les enumeracions per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param incloureGlobals
	 * 			  Indica si incloure les enumeracions globals de l'entorn.
	 * @return les enumeracions del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<EnumeracioDto> enumeracioFindAll(
			Long expedientTipusId,
			boolean incloureGlobals);

	/**
	 * Retorna els dominis per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param incloureGlobals
	 * 			  Indica si incloure els dominis globals de l'entorn.
	 * @return els dominis del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<DominiDto> dominiFindAll(
			Long expedientTipusId,
			boolean incloureGlobals);

	/**
	 * Retorna les consultes per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return les consultes del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<ConsultaDto> consultaFindAll(Long expedientTipusId);
	
	/** 
	 * Retorna la llista de codis de definicions de procés per poder seleccionar
	 * una definició de procés ordenat per codi.
	 * 
	 * @param entornId
	 * @param expedientTipusId
	 * 
	 * @return La llista de codis de les diferents definicions de procés.
	 */
	public List<String> definicioProcesFindJbjmKey(
			Long entornId, 
			Long expedientTipusId,
			boolean incloureGlobals);	

	/**
	 * Retorna les definicions de procés per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return les definicions de procés del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<DefinicioProcesDto> definicioFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;
	
	
	/**
	 * Esborra una entitat.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param id
	 *            Atribut id de la definicio de procés.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws AccioDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void definicioProcesDelete(
			Long id) throws NoTrobatException, PermisDenegatException;

	/**
	 * Marca la definició de procés com a inicial al tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @param id
	 *            Identificador del tipus d'expedient.
	 */
	public boolean definicioProcesSetInicial(
			Long expedientTipusId, 
			Long id);
	
	/**
	 * Incorpora al tipus d'expedient la informació de la definició de procés
	 * com són:
	 * - Agrupacions
	 * - Variables amb les validacions
	 * - Tasques
	 * - Documents
	 * - Terminis
	 * - Accions
	 * - Recursos
	 * @param expedientTipusId
	 * @param id Identificador del tipus d'expedient.
	 * @param sobreescriure Indica si les variables se sobreesciuran al tipus d'expedient o es deixaran sense sobreescriure.
	 * @throws Llença una excepció si no s'ha pogut acomplir alguna dependència o ha succeït algun error.
	 */
	public void definicioProcesIncorporar(
			Long expedientTipusId, 
			Long id, 
			boolean sobreescriure) throws ExportException;
	
	/**
	 * Crea una nova reassignacio.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param reassignacio
	 *            La informació de la reassignacio a crear.
	 * @return la reassignacio creada.
	 * @throws ReassignacioDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ReassignacioDto reassignacioCreate(
			Long expedientTipusId,
			ReassignacioDto reassignacio) throws PermisDenegatException;
	
	/**
	 * Modificació d'una reassignacio existent.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param reassignacio
	 *            La informació de la reassignacio a modificar.
	 * @return la reassignacio modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws ReassignacioDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ReassignacioDto reassignacioUpdate(
			ReassignacioDto reassignacio) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra un entitat.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param reassignacioId
	 *            Atribut id de la reassignacio.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws ReassignacioDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void reassignacioDelete(
			Long reassignacioId) throws NoTrobatException, PermisDenegatException;	
	
	/** 
	 * Retorna la reassignacio del tipus d'expedient donat el seu identificador.
	 * 
	 * @param id
	 * 
	 * @return La reassignacio del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public ReassignacioDto reassignacioFindAmbId(
			Long id) throws NoTrobatException;	
	
	/**
	 * Retorna les reassignacions per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return les reassignacions del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<ReassignacioDto> reassignacioFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;
	/** 
	 * Retorna la llista d'reassignacions del tipus d'expedient paginada per la datatable.
	 * 
	 * @param expedientTipusId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<ReassignacioDto> reassignacioFindPerDatatable(
			Long expedientTipusId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;	
	
	/***********************************************/
	/********************ESTATS*********************/
	/***********************************************/
	
	/**
	 * Retorna els estats per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @return
	 * @throws NoTrobatException
	 * @throws PermisDenegatException
	 */
	public List<EstatDto> estatFindAll(
			Long expedientTipusId,
			PaginacioParamsDto paginacioParams) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna els estats per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @return
	 * @throws NoTrobatException
	 * @throws PermisDenegatException
	 */
	public List<EstatDto> estatFindAll(Long expedientTipusId) throws PermisDenegatException;
	
	/** 
	 * Retorna el estat del tipus d'expedient donat el seu identificador.
	 * 
	 * @param estatId
	 * 
	 * @return El estat del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public EstatDto estatFindAmbId(Long estatId);

	
	public EstatDto estatFindAmbCodi(Long expedientTipusId, String codi);
	
	/**
	 * Crea un nou estat.
	 * 
	 * @param entornId	Atribut id de l'entorn.
	 * @param expedientTipusId	Atribut id del tipus d'expedient.
	 * @param estat	La informació del camp a crear.
	 * @return el estat creat.
	 * @throws CampDenegatException Si no es tenen els permisos necessaris.
	 */
	public EstatDto estatCreate(Long expedientTipusId, EstatDto estat);
	
	/**
	 * Modificació d'un estat existent.
	 * 
	 * @param entornId	Atribut id de l'entorn.
	 * @param expedientTipusId	Atribut id del tipus d'expedient.
	 * @param trmini	La informació del estat a modificar.
	 * @return el estat modificat.
	 * @throws NoTrobatException	Si no s'ha trobat el registre amb l'id especificat.
	 * @throws CampDenegatException	Si no es tenen els permisos necessaris.
	 */
	public EstatDto estatUpdate(EstatDto estat);
	
	/** 
	 * Retorna la llista de estats del tipus d'expedient paginada per la datatable.
	 * 
	 * @param expedientTipusId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<EstatDto> estatFindPerDatatable(
			Long expedientTipusId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;		
	/**
	 * Esborra un estat.
	 * 
	 * @param entornId	Atribut id de l'entorn.
	 * @param estatId	Atribut id del estat.
	 * @throws NoTrobatException	Si no s'ha trobat el registre amb l'id especificat.
	 * @throws CampDenegatException	Si no es tenen els permisos necessaris.
	 */
	public void estatDelete(Long estatId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Canvia un estat de posició
	 * @param estatId	Identificador de l'estat a moure
	 * @param posicio	Posició on situar l'estat
	 * @return
	 * @throws NoTrobatException	Si no es troba l'estat
	 */
	public boolean estatMoure(Long estatId, int posicio) throws NoTrobatException;
	
	
	/**
	 * Crea una nova consulta.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param consulta
	 *            La informació de la consulta a crear.
	 * @return la consulta creada.
	 * @throws ConsultaDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ConsultaDto consultaCreate(
			Long expedientTipusId,
			ConsultaDto consulta) throws PermisDenegatException;
	
	/**
	 * Modificació d'una consulta existent.
	 * 
	 * @param consulta
	 *            La informació de la consulta a modificar.
	 * @param actualitzarContingut Indica si el contingut del blob ha canviat.
	 * @return la consulta modificada.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws ConsultaDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ConsultaDto consultaUpdate(
			ConsultaDto consulta, 
			boolean actualitzarContingut) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra un entitat.
	 * 
	 * @param consultaId
	 *            Atribut id de la consulta.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws ConsultaDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void consultaDelete(
			Long consultaId) throws NoTrobatException, PermisDenegatException;	
	
	/** 
	 * Retorna la consulta del tipus d'expedient donat el seu identificador.
	 * 
	 * @param id
	 * 
	 * @return La consulta del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public ConsultaDto consultaFindAmbId(
			Long id) throws NoTrobatException;	
	
	/** 
	 * Retorna la llista d'consultes del tipus d'expedient paginada per la datatable.
	 * 
	 * @param expedientTipusId
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
	public PaginaDto<ConsultaDto> consultaFindPerDatatable(
			Long entornId,
			Long expedientTipusId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;	
	
	/**
	 * Retorna una consulta d'una consulta d'un tipus d'expedient donat el seu codi.
	 * 
	 * @param tipusExpedientId
	 * @param codi
	 *            El codi per a la consulta.
	 * @return La consulta del tipus d'expedient o null si no el troba.
	 */
	public ConsultaDto consultaFindAmbCodiPerValidarRepeticio(
			Long tipusExpedientId,
			String codi) throws NoTrobatException;	
	
	/** Mou la consulta id cap a la posició indicada reassignant el valor pel camp ordre.
	 * 
	 * @param id
	 * @param posicio
	 * @return Retorna true si ha anat bé o false si no té agrupació o la posició no és correcta.
	 */
	public boolean consultaMourePosicio(Long id, int posicio);
	
	/**
	 * Crea un nou camp per la consulta.
	 * 
	 * @param consultaId
	 *            Atribut id de la consulta.
	 * @param consultaCamp
	 *            La informació del camp de la consulta a crear.
	 * @return el camp de la consulta creat.
	 * @throws ConsultaCampDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ConsultaCampDto consultaCampCreate(
			Long consultaId,
			ConsultaCampDto consultaCamp) throws PermisDenegatException;
	
	/**
	 * Modificació d'un paràmentre de consulta existent.
	 * 
	 * @param consultaCamp
	 *            La informació del camp del registre a modificar.
	 * @return el camp del registre modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ConsultaCampDto consultaCampUpdate(
			ConsultaCampDto consultaCamp) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra un camp de la consulta.
	 * 
	 * @param id
	 *            Atribut id del camp de la consulta.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void consultaCampDelete(
			Long id) throws NoTrobatException, PermisDenegatException;	
	
	/**
	 * Canvia el valor de l'ample i el buit de les variables
	 * que estan com a filtre de les consultes per tipus
	 * 
	 * @param id
	 *            Atribut id del camp de la consulta.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void consultaCampCols(
			Long id, 
			String propietat, 
			int valor) throws NoTrobatException, PermisDenegatException;	

	/** 
	 * Retorna la llista de camps de la consulta del tipus d'expedient paginada per la datatable.
	 * 
	 * @param consultaId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina de la llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<ConsultaCampDto> consultaCampFindPerDatatable(
			Long consultaId,
			TipusConsultaCamp tipus,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;	

	/** Mou el camp de la consulta amb id de camp cap a la posició indicada reassignant el valor pel camp ordre.
	 * 
	 * @param id
	 * @param posicio
	 * @return Retorna true si ha anat bé o false si no té agrupació o la posició no és correcta.
	 */
	public boolean consultaCampMourePosicio(Long id, int posicio);

	/** Mètode per consultar tots els camps d'una consulta filtrant per tipus
	 * 
	 * @param consultaId
	 * @param tipus
	 * @return
	 */
	public List<ConsultaCampDto> consultaCampFindCampAmbConsultaIdAndTipus(
			Long consultaId, 
			TipusConsultaCamp tipus);
	
	/**
	 * Retorna una consulta d'un camp de consulta d'un tipus d'expedient donat el seu codi i 
	 * el tipus.
	 * 
	 * @param consultaId
	 * @param tipus
	 * @param codi
	 *            El codi per a la consulta.
	 * @return La consulta del tipus d'expedient o null si no el troba.
	 */
	public ConsultaCampDto consultaCampFindAmbTipusICodiPerValidarRepeticio(
			Long consultaId,
			TipusConsultaCamp tipus,
			String codi) throws NoTrobatException;
	
	/** Mètode per consultar tots els mapejos d'un tipus d'expedient segons el tipus
	 * per validar la repetició i per filtrar variables ja utilitzades.
	 * 
	 * @param consultaId
	 * @param tipus
	 * @return
	 */
	public List<String> mapeigFindCodiHeliumAmbTipus(
			Long expedientTipusId, 
			TipusMapeig tipus);

	/** Compta les variables, documents i adjunts per a un tipus d'expedient. 
	 * @param expedientTipusId 
	 * @return Retorna un Map segons el tipus i el recompte.
	 */
	public Map<TipusMapeig, Long> mapeigCountsByTipus(Long expedientTipusId);

	/** 
	 * Retorna la llista de mapejos de la integració amb Sistra del tipus 
	 * d'expedient paginada per la datatable.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina de la llistat de tipus d'expedients.
	 */
	public PaginaDto<MapeigSistraDto> mapeigFindPerDatatable(
			Long expedientTipusId, 
			TipusMapeig tipus, 
			PaginacioParamsDto paginacioParams);

	/**
	 * Crea un nou mapeig per a la integració amb Sistra del tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del camp del tipus d'expedient.
	 * @param mapeig
	 *            La informació del mapeig a crear.
	 * @return el mapeig creat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public MapeigSistraDto mapeigCreate(
			Long expedientTipusId,
			MapeigSistraDto mapeig) throws PermisDenegatException;
	

	/**
	 * Modificació d'un mapeig existent.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param mapeig
	 *            La informació del mapeig a modificar.
	 * @return el mapeig modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws CampDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public MapeigSistraDto mapeigUpdate(
			MapeigSistraDto mapeig) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra un mapeig.
	 * 
	 * @param mapeigId
	 *            Atribut id del mapeig.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void mapeigDelete(
			Long mapeigId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna una mapeig d'un tipus d'expedient donat el seu codi helium.
	 * 
	 * @param tipusExpedientId
	 * @param codiHelium
	 *            El codi per a la consulta.
	 * @return La accio del tipus d'expedient o null si no el troba.
	 */
	public MapeigSistraDto mapeigFindAmbCodiHeliumPerValidarRepeticio(
			Long expedientTipusId, 
			String codiHelium);		
	
	/**
	 * Retorna una mapeig d'un tipus d'expedient donat el seu codi Sistra.
	 * 
	 * @param tipusExpedientId
	 * @param codiSistra
	 *            El codi per a la consulta.
	 * @return La accio del tipus d'expedient o null si no el troba.
	 */
	public MapeigSistraDto mapeigFindAmbCodiSistraPerValidarRepeticio(
			Long expedientTipusId, 
			String codiSistra);
	
	/**
	 * Retorna els mapejos de sistra per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return els mapejos del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<MapeigSistraDto> mapeigFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;

	
	/**
	 * Retorna la llista de persones amb permisos pel tipus d'expedient ordenades per codi.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return La llista de persones amb permís pel tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el tipus d'expedient.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris per llegir l'entorn o l'expedient.
	 */
	public List<PersonaDto> personaFindAll(
			Long entornId,
			Long expedientTipusId) throws Exception;


}