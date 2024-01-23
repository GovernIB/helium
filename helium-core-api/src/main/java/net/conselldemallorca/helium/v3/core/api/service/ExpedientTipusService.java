package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto.TipusConsultaCamp;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEstadisticaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.EstatAccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.EstatReglaDto;
import net.conselldemallorca.helium.v3.core.api.exception.ExportException;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exportacio.EstatExportacio;
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
	 * @param actualitzarContingutManual 
	 *            Indica si s'ha d'actualitzar el contingut del manual amb les dades del DTO. 			
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
			List<Long> sequenciesValor, 
			boolean actualitzarContingutManual) throws NoTrobatException, PermisDenegatException;

	/** 
	 * Modifica les dades del tipus d'expedient referents a la integració amb formularis externs.
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
	
	/** 
	 * Modifica les dades del tipus d'expedient referents a la integració amb DISTRIBUCIO.
	 * 
	 * @param entornId
	 * @param expedientTipusId
	 * @param actiu
	 * @param codiProcediment
	 * @param procesAuto Indica si processar automàticament
	 * @param sistra Indica si aplicar la integració i el mapeig de SISTRA
	 * @param presencial Indica si les anotacions s'han realitzat de forma presencial
	 * 
	 * @return El tipus d'expedient modificat.
	 * 
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto updateIntegracioDistribucio(
			Long entornId, 
			Long expedientTipusId, 
			boolean actiu, 
			String codiProcediment,
			String codiAssumpte,
			boolean procesAuto,
			boolean sistra,
			Boolean presencial);

	/** 
	 * Modifica les dades del tipus d'expedient referents amb la integració amb els tràmits de 
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
			boolean sistraActiu,
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

	/** 
	 * Mètode per importar la informació d'un fitxer d'exportació de tipus d'expedient cap a un nou tipus
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
	 * @return Els tipus d'expedient amb permis de consulta.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<ExpedientTipusDto> findAmbEntornPermisConsultar(
			Long entornId) throws NoTrobatException;

	/** Consulta el tipus d'expedient 
	 * 
	 * @param expedientTipusId Identificador.
	 * @return Retorna el tipus d'expedient a consultar.
	 * @throws NoTrobatException Excepció si no el troba per ID. 
	 */
	public ExpedientTipusDto findAmbId(Long expedientTipusId) throws NoTrobatException;

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
	 * @return Els tipus d'expedient amb permisos de disseny.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<ExpedientTipusDto> findAmbEntornPermisDissenyar(
			Long entornId) throws NoTrobatException;
	
	/**
	 * Retorna els tipus d'expedient d'un entorn sobre els quals l'usuari pot processar anotacions.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @return Els tipus d'expedient amb permisos sobre anotacions.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<ExpedientTipusDto> findAmbEntornPermisAnotacio(
			Long entornId) throws NoTrobatException;
	
	/**
	 * Retorna els tipus d'expedient d'un entorn sobre els quals l'usuari pot executar scripts.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @return Els tipus d'expedient amb permisos d'execució d'script.
	 */
	public List<ExpedientTipusDto> findAmbEntornPermisExecucioScript(
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
	 * @return Els tipus d'expedient amb permis de creació.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<ExpedientTipusDto> findAmbEntornPermisCrear(
			Long entornId) throws NoTrobatException;

	/**
	 * Retorna els tipus d'expedient d'un entorn.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @return Els tipus d'expedient de l'entorn.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<ExpedientTipusDto> findAmbEntorn(
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
	 * Retorna un tipus d'expedient donat el seu codi sense validar/autenticar.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param codi
	 *            El codi per a la consulta.
	 * @return El tipus d'expedient o null si no el troba.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public ExpedientTipusDto findAmbCodi(
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
	 * Retorna la llista de tipus d'expedients amb la propietat heretable certa.
	 * @param entornId
	 * @return La llista de tipus d'expedients que es poden heretar.
	 */
	public List<ExpedientTipusDto> findHeretables(Long entornId);
	
	/**
	 * Retorna la llista de tipus d'expedients que hereten del tipus d'expedient passat com a paràmetre.
	 * @param expedientTipusId Tipus d'expedient pare
	 * @return La llista de tipus d'expedients que tenen el tipus d'expedient com a heretat.
	 */
	public List<ExpedientTipusDto> findHeretats(Long expedientTipusId);
	
	/**
	 * Modifica un permis existent d'un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param unitatOrganitzativaId
	 *            Atribut id de la unitat organitzativa.
	 * @param permis
	 *            La informació del permis.
	 * @param entornAdmin
	 * 			  Indica si l'usuari és administrador de l'entorn i per tant pot modifica pot modificar permsios del tipus d'expeient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void permisUpdate(
			Long entornId,
			Long expedientTipusId,
			Long unitatOrganitzativaId,
			PermisDto permis,
			boolean entornAdmin) throws NoTrobatException, PermisDenegatException;

	/**
	 * Esborra un permis existent d'un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param permisId
	 *            Atribut id del permis.
	 * @param entornAdmin
	 * 			  Indica si l'usuari és administrador de l'entorn i per tant pot modifica pot modificar permsios del tipus d'expeient.
	 * @param unitatOrganitzativaCodi);
	 * 			  Atribut codi de la unitat organitzativa
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void permisDelete(
			Long entornId,
			Long expedientTipusId,
			Long permisId,
			boolean entornAdmin,
			String unitatOrganitzativaCodi) throws NoTrobatException, PermisDenegatException;

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
	 * @param unitatOrganitzativaCodi
	 *            Atribut codi de la unitat organitzativa.
	 * @return el permis amb l'id especificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public PermisDto permisFindById(
			Long entornId,
			Long expedientTipusId,
			Long permisId,
			String unitatOrganitzativaCodi) throws NoTrobatException, PermisDenegatException;

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
	 * @param herencia Consultar definicions de procés heretades
	 * @param incloureGlobals
	 * 
	 * @return La llista de codis de les diferents definicions de procés.
	 */
	public List<String> definicioProcesFindJbjmKey(
			Long entornId, 
			Long expedientTipusId,
			boolean herencia,
			boolean incloureGlobals);	

	/**
	 * Retorna les definicions de procés per a un tipus d'expedient sense tenir en compte l'herència.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return les definicions de procés associades al tipus d'expedient.
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
	 * @param id
	 *            Atribut id de la definicio de procés.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
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
	 * @param expedientTipusId Identificador del tipus d'expedient.
	 * @param sobreescriure Indica si les variables se sobreesciuran al tipus d'expedient o es deixaran sense sobreescriure.
	 * @param tasques Indica que es relacionin les variables, documents i signatures de la tasca amb les definides a nivell de tipus d'expedient.
	 * @throws ExportException Llença una excepció si no s'ha pogut acomplir alguna dependència o ha succeït algun error.
	 */
	public void definicioProcesIncorporar(
			Long expedientTipusId, 
			Long definicioProcesId, 
			boolean sobreescriure,
			boolean tasques) throws ExportException;

	/**
	 * Crea una nova reassignacio.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param reassignacio
	 *            La informació de la reassignacio a crear.
	 * @return la reassignacio creada.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ReassignacioDto reassignacioCreate(
			Long expedientTipusId,
			ReassignacioDto reassignacio) throws PermisDenegatException;

	/**
	 * Modificació d'una reassignacio existent.
	 * 
	 * @param reassignacio
	 *            La informació de la reassignacio a modificar.
	 * @return la reassignacio modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ReassignacioDto reassignacioUpdate(
			ReassignacioDto reassignacio) throws NoTrobatException, PermisDenegatException;

	/**
	 * Esborra un entitat.
	 * 
	 * @param reassignacioId
	 *            Atribut id de la reassignacio.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
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
	 * @param ambHerencia Indica si incloure els estats heretats per l'expedient tipus.
	 * @return
	 * @throws NoTrobatException
	 * @throws PermisDenegatException
	 */
	public List<EstatDto> estatFindAll(
			Long expedientTipusId,
			boolean ambHerencia) throws PermisDenegatException;

	/** 
	 * Retorna l'estat del tipus d'expedient donat el seu identificador. Té en compte els
	 * heretats i informa el camps d'herència del dto.
	 *
	 * @param expedientTipusId
	 * @param estatId
	 *
	 * @return L'estat del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public EstatDto estatFindAmbId( Long expedientTipusId, Long estatId);

	/**
	 * Mètode per recuperar l'estat d'un tipus d'expedient per codi. No té en compte la herència.
	 * @param expedientTipusId
	 * @param codi
	 * @return
	 */
	public EstatDto estatFindAmbCodi(Long expedientTipusId, String codi);

	/**
	 * Crea un nou estat.
	 * 
	 * @param expedientTipusId	Atribut id del tipus d'expedient.
	 * @param estat	La informació del camp a crear.
	 * @return el estat creat.
	 */
	public EstatDto estatCreate(Long expedientTipusId, EstatDto estat);

	/**
	 * Modificació d'un estat existent.
	 * 
	 * @param estat	Atribut id de l'entorn.
	 * @return el estat modificat.
	 * @throws NoTrobatException	Si no s'ha trobat el registre amb l'id especificat.
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
	 * @param estatId	Atribut id del estat.
	 * @throws NoTrobatException	Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException	Si no es tenen els permisos necessaris.
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
	 * Canvia un estat de posició
	 * @param estatId	Identificador de l'estat a moure
	 * @param posicio	Posició on situar l'estat
	 * @param ordre		Tipus d'ordre a aplicar
	 * @return
	 * @throws NoTrobatException	Si no es troba l'estat
	 */
	public boolean estatMoureOrdre(Long estatId, int posicio, String ordre) throws NoTrobatException;

	/**
	 * Obté el següent ordre no assignat dels estats
	 * @param expedientTipusId	Identificador del tipus d'expedient
	 * @return
	 * @throws NoTrobatException	Si no es troba el tipus d'exèdient
	 */
	public int getEstatSeguentOrdre(Long expedientTipusId) throws NoTrobatException;

	/**
	 * Retorna la informació dels estats d'un tipus d'expedient per exportar
	 *
	 * @param expedientTipusId	Identificador del tipus d'expedient
	 * @return
	 * @throws NoTrobatException	Si no es troba el tipus d'exèdient
	 */
	public List<EstatExportacio> estatExportacio(Long expedientTipusId, boolean ambPermisos) throws NoTrobatException;

	/** Retorna la llista d'estats als quals es pot avançar des de l'estat actual de l'expedient. 
	 * 
	 * @param expedientId
	 * @return Llista d'estats possibles als quals avançar.
	 */
	public List<EstatDto> estatGetAvancar(long expedientId);

	/** Retorna la llista d'estats als quals es pot avançar des de l'estat actual de l'expedient. 
	 * 
	 * @param expedientId
	 * @return Llista d'estats possibles als quals avançar.
	 */
	public List<EstatDto> estatGetRetrocedir(long expedientId);

	// PERMISOS ESTATS
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Retorna els permisos per a un estat.
	 *
	 * @param estatId
	 *            Atribut id de l'estat.
	 * @return els permisos del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<PermisDto> estatPermisFindAll(Long estatId);

	/**
	 * Retorna un permis donat el seu id.
	 *
	 * @param estatId
	 *            Atribut id de l'entorn.
	 * @param permisId
	 *            Atribut id del permis.
	 * @return el permis amb l'id especificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public PermisDto estatPermisFindById(Long estatId, Long permisId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Modifica un permis existent d'un estat.
	 *
	 * @param estatId
	 *            Atribut id de l'estat.
	 * @param permis
	 *            La informació del permis.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void estatPermisUpdate(Long estatId, PermisDto permis) throws NoTrobatException, PermisDenegatException;

	/**
	 * Esborra un permis existent d'un estat.
	 *
	 * @param estatId
	 *            Atribut id de l'estat.
	 * @param permisId
	 *            Atribut id del permis.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void estatPermisDelete(Long estatId, Long permisId) throws NoTrobatException, PermisDenegatException;

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// REGLES

	/**
	 * Retorna les regles per a un estat.
	 *
	 * @param estatId
	 *            Atribut id de l'estat.
	 * @return les regles del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<EstatReglaDto> estatReglaFindAll(Long estatId);

	/**
	 * Retorna una regla donat el seu id.
	 *
	 * @param estatId
	 *            Atribut id de l'entorn.
	 * @param reglaId
	 *            Atribut id de la regla.
	 * @return la regla amb l'id especificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public EstatReglaDto estatReglaFindById(Long estatId, Long reglaId);

	/**
	 * Retorna una regla donat el seu nom.
	 *
	 * @param estatId
	 *            Atribut id de l'entorn.
	 * @param nom
	 *            Atribut nom de la regla.
	 * @return la regla amb el nom especificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb el nom especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public EstatReglaDto estatReglaFindByNom(Long estatId, String nom);

	/**
	 * Crea una regla per un estat.
	 *
	 * @param estatId
	 *            Atribut id de l'estat.
	 * @param reglaDto
	 *            La informació de la regla.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'estat amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public EstatReglaDto estatReglaCreate(Long estatId, EstatReglaDto reglaDto) throws NoTrobatException, PermisDenegatException;

	/**
	 * Modifica una regla existent d'un estat.
	 *
	 * @param estatId
	 *            Atribut id de l'estat.
	 * @param reglaDto
	 *            La informació de la regla.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'estat o el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public EstatReglaDto estatReglaUpdate(Long estatId, EstatReglaDto reglaDto) throws NoTrobatException, PermisDenegatException;

	/**
	 * Esborra una regla existent d'un estat.
	 *
	 * @param estatId
	 *            Atribut id de l'estat.
	 * @param reglaId
	 *            Atribut id de la regla.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'estat o el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void estatReglaDelete(Long estatId, Long reglaId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Canvia una regla de posició
	 * @param reglaId	Identificador de la regla a moure
	 * @param posicio	Posició on situar la regla
	 * @return
	 * @throws NoTrobatException	Si no es troba l'estat
	 */
	public boolean estatReglaMoure(Long reglaId, int posicio);

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// ACCIONS DELS ESTATS
	
	/**
	 * Esborra totes les accions d'entrada i sortida d'un estat pels expedients basats en estats.
	 *
	 * @param estatId
	 *            Atribut id de l'estat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'estat amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void estatAccionsDeleteAll(Long estatId) throws NoTrobatException, PermisDenegatException;


	/** 
	 * Retorna la relació d'accions per un estat paginada per la datatable.
	 * 
	 * @param estatId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina de la llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<EstatAccioDto> estatAccioEntradaFindPerDatatable(
			Long estatId, 
			String filtre, 
			PaginacioParamsDto paginacioParams);
	
	/** 
	 * Retorna la llista d'accions d'entrada per un estat ordenada per la columna ordre.
	 * 
	 * @param estatId
	 * 
	 * @return La llista d'accions d'entrada.
	 * 
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<EstatAccioDto> estatAccioEntradaFindAll(
			Long estatId);

	/** 
	 * Retorna la llista d'accions de sortida per un estat ordenada per la columna ordre.
	 * 
	 * @param estatId
	 * 
	 * @return La llista d'accions de sortida.
	 * 
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<EstatAccioDto> estatAccioSortidaFindAll(
			Long estatId);
	
	/**
	 * Afegeix una acció a l'entrada d'un estat.
	 *
	 * @param estatId
	 *            Atribut id de l'estat.
	 * @param accioId
	 *            Identificador de l'estat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'estat amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public EstatAccioDto estatAccioEntradaAfegir(Long estatId, Long accioId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Esborra la relació estat-acció de la llista d'accions d'entrada de l'estat.
	 *
	 * @param estatId
	 *            Atribut id de l'estat.
	 * @param estatAccioId
	 *            Identificador de la relació entre l'estat i l'acció.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'estat amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void estatAccioEntradaDelete(Long estatId, Long estatAccioId);

	/**
	 * Canvia una acció de la llista d'accions d'entrada de l'estat.
	 * @param estatAccioId	Identificador de la relació d'estat i acció d'entrada.
	 * @param posicio	Posició on situar la regla
	 * @return
	 * @throws NoTrobatException	Si no es troba l'estat-acció
	 */
	public boolean estatAccioEntradaMoure(Long estatAccioId, int posicio);

	
	/** 
	 * Retorna la relació d'accions per un estat paginada per la datatable.
	 * 
	 * @param estatId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina de la llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<EstatAccioDto> estatAccioSortidaFindPerDatatable(
			Long estatId, 
			String filtre, 
			PaginacioParamsDto paginacioParams);
	
	/**
	 * Afegeix una acció a l'sortida d'un estat.
	 *
	 * @param estatId
	 *            Atribut id de l'estat.
	 * @param accioId
	 *            Identificador de l'estat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'estat amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public EstatAccioDto estatAccioSortidaAfegir(Long estatId, Long accioId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Esborra la relació estat-acció de la llista d'accions d'sortida de l'estat.
	 *
	 * @param estatId
	 *            Atribut id de l'estat.
	 * @param estatAccioId
	 *            Identificador de la relació entre l'estat i l'acció.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'estat amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void estatAccioSortidaDelete(Long estatId, Long estatAccioId);

	/**
	 * Canvia una acció de la llista d'accions d'sortida de l'estat.
	 * @param estatAccioId	Identificador de la relació d'estat i acció d'sortida.
	 * @param posicio	Posició on situar la regla
	 * @return
	 * @throws NoTrobatException	Si no es troba l'estat-acció
	 */
	public boolean estatAccioSortidaMoure(Long estatAccioId, int posicio);

	
	/**
	 * Crea una nova consulta.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param consulta
	 *            La informació de la consulta a crear.
	 * @return la consulta creada.
	 * @throws PermisDenegatException
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
	 * @throws PermisDenegatException
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
	 * @throws PermisDenegatException
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
	 * @param entornId
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
	 * Mètode per trobar les consultes pel tipus d'expedient amb camps relacionats amb una definicio de 
	 * procés concreta. Serveix per avisar de les consultes afectades després d'esborrar una definició de procés. 
	 * @param entornId
	 * @param expedientTipusId
	 * @param jbpmKey
	 * @param versio
	 * @return Retorna la llista de consultes afectades.
	 */
	public List<ConsultaDto> consultaFindRelacionadesAmbDefinicioProces(
			Long entornId,
			Long expedientTipusId, 
			String jbpmKey, 
			int versio);	

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

	/**
	 * Mou la consulta id cap a la posició indicada reassignant el valor pel camp ordre.
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
	 * @throws PermisDenegatException
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

	/** 
	 * Mou el camp de la consulta amb id de camp cap a la posició indicada reassignant el valor pel camp ordre.
	 * 
	 * @param id
	 * @param posicio
	 * @return Retorna true si ha anat bé o false si no té agrupació o la posició no és correcta.
	 */
	public boolean consultaCampMourePosicio(Long id, int posicio);

	/** 
	 * Mètode per consultar tots els camps d'una consulta filtrant per tipus
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

	/** 
	 * Mètode per consultar tots els mapejos d'un tipus d'expedient segons el tipus
	 * per validar la repetició i per filtrar variables ja utilitzades.
	 * 
	 * @param expedientTipusId
	 * @param tipus
	 * @return
	 */
	public List<String> mapeigFindCodiHeliumAmbTipus(
			Long expedientTipusId, 
			TipusMapeig tipus);

	/** 
	 * Compta les variables, documents i adjunts per a un tipus d'expedient. 
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
	 * @param mapeig
	 *            La informació del mapeig a modificar.
	 * @return el mapeig modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
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
	 * @param expedientTipusId
	 * @param codiHelium
	 *            El codi per a la consulta.
	 * @return La accio del tipus d'expedient o null si no el troba.
	 */
	public MapeigSistraDto mapeigFindAmbCodiHeliumPerValidarRepeticio(
			Long expedientTipusId, 
			String codiHelium);		

	/**
	 * Retorna una mapeig d'un tipus d'expedient donat el seu codi Sistra.
	 * @param tipusMapeig 
	 * 
	 * @param expedientTipusId
	 * @param tipusMapeig
	 * @param codiSistra
	 *            El codi per a la consulta.
	 * @return La accio del tipus d'expedient o null si no el troba.
	 */
	public MapeigSistraDto mapeigFindAmbCodiSistraPerValidarRepeticio(
			Long expedientTipusId, 
			TipusMapeig tipusMapeig, 
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
	
	/**
	 * Modifica les dades metadades nti d'un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param actiu
	 *            Indica si les metadades nti estana activades.
	 * @param organo
	 *            L’òrgan al qual està associat l’expedient.
	 * @param clasificacion
	 *            El codi SIA del procediment al qual pertany l’expedient.
	 * @param serieDocumental
	 *            El codi de la serie documental relacionada amb l'expedient.
	 * @param arxiuActiu
	 *            Indica si la integració amb l'arxiu està activada.
	 * @param arxiuActiu
	 *            Indica si el de procediment és comú.
	 * @return El tipus d'expedient modificat.
	 * 
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto updateMetadadesNti(
			Long entornId,
			Long expedientTipusId,
			boolean actiu,
			String organo,
			String clasificacion,
			String serieDocumental,
			boolean arxiuActiu,
			boolean procedimentComu);	
		
	public List<ExpedientTipusEstadisticaDto> findEstadisticaByFiltre(
			Integer anyInicial, 
			Integer anyFinal,
			Long entornId,
			Long expedientTipusId,
			Boolean anulats,
			String numero,
			String titol,
			EstatTipusDto estatTipus,
			Long estatId,
			Boolean aturat
			);
	
	/**
	 * Modifica les dades de la integració amb NOTIB
	 * 
	 * @param expedientTipusId
	 * 			Identificador del tipus d'expedient
	 * @param notibEmisor
	 * 			Codi de l'emisor
	 * @param notibCodiProcediment
	 * 			Codi del procediment SIA
	 * @param notibActiu
	 * 			Indica si la integració amb Notib està activa
	 * 
	 * @return El tipus d'expedient modificat.
	 * 
	 */
	public ExpedientTipusDto updateIntegracioNotib(
			Long expedientTipusId, 
			String notibEmisor, 
			String notibCodiProcediment,
			boolean notibActiu);

	/** Mètode per trobar el tipus d'expedient segons els criteris per a la integració amb Distribucio. Serveix
	 * per trobar el tipus d'expedient que està configurat segons el codi procediment i el codi d'assumpte. 
	 * 
	 * @param codiProcediment
	 * @param codiAssumpte 
	 * @return
	 *  Retorna el tipus d'expedient configurat per a la combinació de codi procediment i codi tipus assumpte
	 */
	public ExpedientTipusDto findPerDistribucio(String codiProcediment, String codiAssumpte);	
	
	/** Mètode per trobar un tipus d'expedient configurat segons els criteris de codi de procediment i codi assumpte per
	 * validar que no hi hagi cap altre configurat amb els mateixos valors.
	 * 
	 * @param codiProcediment
	 * @param codiAssumpte 
	 * @return
	 *  Retorna el tipus d'expedient configurat per a la combinació de codi procediment i codi tipus assumpte
	 */
	public ExpedientTipusDto findPerDistribucioValidacio(String codiProcediment, String codiAssumpte);

	/**Mètode per trobar els tipus d'expedients filtrats per tipologia*
	 * 
	 * @param filtreDto
	 * @param entornId
	 * */
	public PaginaDto<ExpedientTipusDto> findTipologiesByFiltrePaginat(
			Long entornId,
			ExpedientTipusFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams);	

	/**
	 * Modifica les dades d'integracio Pinbal d'un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param pinbalActiu
	 *            Indica si la integració amb Pinbal està activada.
	 * @param pinbalNifCif
	 *            El CIF/NIF de la unitat orgànica.
	 * 
	 * @return El tipus d'expedient modificat.
	 * 
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto updateIntegracioPinbal(
			Long entornId, 
			Long expedientTipusId, 
			boolean pinbalActiu,
			String pinbalNifCif);
	
	/**
	 * Retorna els permisos per a un tipus d'expedient amb procediment comú (relacionat amb una unitat organitzativa)
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return els permisos del tipus d'expedient.
	 * 
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<PermisDto> permisFindAllByExpedientTipusProcedimentComu(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Retorna un booleà per saber si té permís per a un tipus d'expedient amb procediment comú relacionat amb una unitat organitzativa en concret.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param unitatOrganitzativaCodi
	 * 
	 * @return boolea si té permís
	 * 
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public boolean tePermis(Long expedientId, 
			String unitatOrganitzativaCodi) throws NoTrobatException, PermisDenegatException;


	/** Mètode per obtenir el contingut del manual d'ajuda del tipus d'expedient.
	 * 
	 * @param expedientTipusId Identificador del tipus d'expedient.
	 * 
	 * @return Si el tipus d'expedient té manual d'ajuda llavors el retorna, si no retorna null.
	 */
	public ArxiuDto getManualAjuda(Long expedientTipusId);

}