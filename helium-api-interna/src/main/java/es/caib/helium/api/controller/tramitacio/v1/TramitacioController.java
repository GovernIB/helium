package es.caib.helium.api.controller.tramitacio.v1;

import es.caib.helium.client.tramitacio.v1.*;
import es.caib.helium.commons.dto.*;
import es.caib.helium.logic.intf.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Tag(
	name = "Tramitació externa",
	description = "API REST per realitzar tramitació externa")
@RestController
@RequestMapping("/rest/v1/tramitacio/{entornCodi}")
@RequiredArgsConstructor
@Slf4j
public class TramitacioController {

	private final ExpedientService expedientService;
	private final EntornService entornService;
	private final TascaService tascaService;
	private final ExpedientTipusService expedientTipusService;
	private final ExpedientTascaService expedientTascaService;
	private final ExpedientDadaService expedientDadaService;
	private final ExpedientDocumentService expedientDocumentService;

	@Operation(
		summary = "Consulta expedients",
		description = "Llista expedients d'acord amb els filtres indicats per entorn")
	@GetMapping("/expedients")
	public List<TramitacioExpedientInfo> findExpedients(
		@PathVariable String entornCodi,
		@RequestParam(required = false) String expedientTipusCodi,
		@RequestParam(required = false) String titol,
		@RequestParam(required = false) String numero,
		@RequestParam(required = false) Date dataInici1,
		@RequestParam(required = false) Date dataInici2,
		@RequestParam(required = false) String estatCodi,
		@RequestParam(required = false) Boolean iniciat,
		@RequestParam(required = false) Boolean finalitzat,
		@RequestParam(required = false) Double geoPosX,
		@RequestParam(required = false) Double geoPosY,
		@RequestParam(required = false) String geoReferencia,
		@Parameter(description = "Numero de pagina") @RequestParam() Integer p,
		@Parameter(description = "Mida de pagina") @RequestParam() Integer s
		) throws TramitacioException {

		log.info( "Consulta expedients [entorn=" + entornCodi
				+ ", expedientTipusCodi=" + expedientTipusCodi
				+ ", titol=" + titol
				+ ", numero=" + numero
				+ ", dataInici1=" + dataInici1
				+ ", dataInici2=" + dataInici2
				+ ", estatCodi=" + estatCodi
				+ ", iniciat=" + iniciat
				+ ", finalitzat=" + finalitzat
				+ ", geoPosX=" + geoPosX
				+ ", geoPosY=" + geoPosY
				+ ", geoReferencia=" + geoReferencia
				+ "]");

		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if (entorn == null)
			throw new TramitacioException("No existeix cap entorn amb aquest codi '" + entornCodi + "'");

		try {
			Long expedientTipusId = null;
			Long estatId = null;
			if (expedientTipusCodi != null) {
				ExpedientTipusDto expedientTipus = expedientTipusService.findAmbCodi(entorn.getId(), expedientTipusCodi);
				expedientTipusId = expedientTipus != null? expedientTipus.getId() : null;
				if (expedientTipus != null && estatCodi != null && !estatCodi.isEmpty()) {
					for (EstatDto estat: expedientTipus.getEstats()) {
						if (estat.getCodi().equals(estatCodi)) {
							estatId = estat.getId();
							break;
						}
					}
				}
			} else if(estatCodi != null && !estatCodi.isEmpty()) {
				List<ExpedientTipusDto> expedientTipusList = expedientTipusService.findAmbEntornPermisConsultar(entorn.getId());
				for(ExpedientTipusDto et : expedientTipusList) {
					for (EstatDto estat : et.getEstats()) {
						if (estat.getCodi().equals(estatCodi)) {
							estatId = estat.getId();
							break;
						}
					}
				}
			}

			PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
			paginacioParams.setPaginaNum(p);
			paginacioParams.setPaginaTamany(s);
			paginacioParams.afegirOrdre(
				"id",
				PaginacioParamsDto.OrdreDireccioDto.ASCENDENT);
		PaginaDto<ExpedientDto> expedients =
			expedientService.findAmbFiltrePaginat(
				null,
				entorn.getId(),
				expedientTipusId,
				titol,
				numero,
				null,
				dataInici1,
				dataInici2,
				null,
				null,
				null,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				null,
				false,
				false,
				false,
				false,
				false,
				MostrarAnulatsDto.NO,
				new HashSet<>(),
				paginacioParams);

			return expedients
					.getContingut()
					.stream()
					.map(this::toExpedientInfo)
					.collect(Collectors.toList());
		} catch (Exception ex) {
			log.error("No s'ha pogut obtenir el llistat d'expedients: " + ex.getMessage(), ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat d'expedients: " + ex.getMessage());
		}
	}

	@Operation(
		summary = "Inicia un nou expedient",
		description = "Inicia un nou expedient i retorna l'identificador de la instancia de process principal")
	@PostMapping("/expedients")
	public Long iniciExpedient(
		@PathVariable String entornCodi,
		@RequestBody TramitacioExpedient expedientTramitacio) throws TramitacioException {

		String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		// Informació de l'inici d'expedient pels logs
		String expLog = "Iniciar expedient [entorn=" + entornCodi +
						", expedientTipus=" + expedientTramitacio.getExpedientTipus() +
						", numero=" + expedientTramitacio.getNumero() +
						", titol=" + expedientTramitacio.getTitol() +
						", usuari=" + usuari + "]";
		try {
			EntornDto entorn = entornService.findAmbCodi(entornCodi);
			if (entorn == null)
				throw new TramitacioException("No existeix cap entorn amb aquest codi '" + entornCodi + "'");
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbCodi(entorn.getId(), expedientTramitacio.getExpedientTipus());
			if (expedientTipus == null)
				throw new TramitacioException("No existeix cap tipus d'expedient amb el codi '" + expedientTramitacio.getExpedientTipus() + "'");
			if (expedientTramitacio.getNumero() != null && !expedientTipus.isTeNumero())
				throw new TramitacioException("Aquest tipus d'expedient no suporta número d'expedient");
			if (expedientTramitacio.getTitol() != null && !expedientTipus.isTeTitol())
				throw new TramitacioException("Aquest tipus d'expedient no suporta títol d'expedient");
			Map<String, Object> variables = new HashMap<>();
			if(expedientTramitacio.getValors() != null)
				for(TramitacioCodiValor cv : expedientTramitacio.getValors())
					variables.put(cv.getCodi(), cv.getValor());

			ExpedientDto expedient = expedientService.create(
											entorn.getId(),
											null,
											expedientTipus.getId(),
											null,
											null,
											expedientTramitacio.getNumero(),
											null,
											expedientTramitacio.getTitol(),
											null,
											null,
											null,
											null,
											false,
											null,
											null,
											null,
											null,
											null,
											null,
											false,
											null,
											null,
											false,
											variables,
											null,
											ExpedientDto.IniciadorTipusDto.INTERN,
											null,
											null,
											null,
											null,
											null,
											false);
			log.info("Expedient " + entornCodi + " iniciat correctament " + expLog);
			return expedient.getId();
		} catch (Exception ex) {
			log.error("Error iniciant l'expedient a través de tramitació externa "  + expLog + ": " + ex.getMessage(), ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	@Operation(summary = "Informació del expedient", description = "Retorna informació del expedient")
	@GetMapping("/expedients/{expedientId}")
	public TramitacioExpedientInfo getExpedientInfo(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId) throws TramitacioException {
		log.info( "Consultant informació d'expedient ["
				+ "expedient=" + expedientId
				+ ", entorn=" + entornCodi
				+ ", usuari=" + SecurityContextHolder.getContext().getAuthentication().getName()
				+"]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb ID '" + expedientId + "'");

		return toExpedientInfo(expedient);
	}

	@Operation(summary = "Esborrar expedient")
	@DeleteMapping("/expedients/{expedientId}")
	public void deleteExpedient(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId) throws TramitacioException {
		log.info( "Consultant informació d'expedient ["
				+ "expedient=" + expedientId
				+ ", entorn=" + entornCodi
				+ ", usuari=" + SecurityContextHolder.getContext().getAuthentication().getName()
				+"]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entornCodi + "'");
		try {
			expedientService.delete(expedientId);
		} catch (Exception ex) {
			log.error("No s'ha pogut esborrar l'expedient", ex);
			throw new TramitacioException("No s'ha pogut esborrar l'expedient: " + ex.getMessage());
		}
	}

	// PROCESOS

	@Operation(summary = "Consulta dades que conformen el procés d'expedient")
	@GetMapping("/expedients/{expedientId}/procesos/{procesInstanceId}/dades")
	public List<TramitacioCamp> consultaDadesProces(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String procesInstanceId) throws TramitacioException {
		log.info( "Consulta dades de procés ["
			+ "entorn=" + entornCodi
			+ ", expedient=" + expedientId
			+ ", proces=" + procesInstanceId
			+ ", usuari=" + SecurityContextHolder.getContext().getAuthentication().getName()
			+"]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			List<ExpedientDadaDto> dades = expedientDadaService.findAmbInstanciaProces(expedientId, procesInstanceId);
			return dades
				.stream()
				.map(this::convertirCampTasca)
				.collect(Collectors.toList());
		} catch (Exception ex) {
			log.error("No s'ha pogut consultar dades de procés", ex);
			throw new TramitacioException("No s'ha pogut consultar dades de procés: " + ex.getMessage());
		}
	}

	@Operation(summary = "Desa dada del procés d'expedient")
	@PostMapping("/expedients/{expedientId}/procesos/{procesInstanceId}/dades")
	public void guardarDadaProces(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String procesInstanceId,
		@RequestBody TramitacioCodiValor dada) throws TramitacioException {
		log.info( "Desar dada de procés ["
			+ "entorn=" + entornCodi
			+ ", expedient=" + expedientId
			+ ", proces=" + procesInstanceId
			+ ", usuari=" + SecurityContextHolder.getContext().getAuthentication().getName()
			+"]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			expedientDadaService.update(
				expedientId,
				procesInstanceId,
				dada.getCodi(),
				dada.getValor());
		} catch (Exception ex) {
			log.error("No s'ha pogut desar dada del procés d'expedient", ex);
			throw new TramitacioException("No s'ha pogut desar dada del procés d'expedient: " + ex.getMessage());
		}
	}

	@Operation(summary = "Esborra dada del procés d'expedient")
	@DeleteMapping("/expedients/{expedientId}/procesos/{procesInstanceId}/dades/{codi}")
	public void esborrarDadesProces(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String procesInstanceId,
		@PathVariable String codi) throws TramitacioException {
		log.info( "Esborrar dada de procés ["
			+ "entorn=" + entornCodi
			+ ", expedient=" + expedientId
			+ ", proces=" + procesInstanceId
			+ ", codi=" + codi
			+ ", usuari=" + SecurityContextHolder.getContext().getAuthentication().getName()
			+"]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			expedientDadaService.delete(
				expedientId,
				procesInstanceId,
				codi);
		} catch (Exception ex) {
			log.error("No s'ha pogut esborrar dada del process d'expedient", ex);
			throw new TramitacioException("No s'ha pogut esborrar dada del process d'expedient: " + ex.getMessage());
		}
	}

	@Operation(summary = "Consulta documents del procés d'expedient")
	@GetMapping("/expedients/{expedientId}/procesos/{procesInstanceId}/documents")
	public List<TramitacioDocument> consultaDocumentsProces(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String procesInstanceId) throws TramitacioException {
		log.info( "Consulta documents del procés ["
			+ "entorn=" + entornCodi
			+ ", expedient=" + expedientId
			+ ", proces=" + procesInstanceId
			+ ", usuari=" + SecurityContextHolder.getContext().getAuthentication().getName()
			+ "]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			List<ExpedientDocumentDto> documents =
				expedientDocumentService.findAmbInstanciaProces(expedientId, procesInstanceId);
			return documents
				.stream()
				.map(this::convertirDocumentTasca)
				.collect(Collectors.toList());
		} catch (Exception ex) {
			log.error("No s'ha pogut consultar documents del procés d'expedient", ex);
			throw new TramitacioException("No s'ha pogut consultar documents del procés d'expedient: " + ex.getMessage());
		}
	}

	@Operation(summary = "Afegeix document al procés d'expedient")
	@PostMapping("/expedients/{expedientId}/procesos/{procesInstanceId}/documents")
	public void guardarDocumentsProces(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String procesInstanceId,
		@RequestBody TramitacioDocument document) throws TramitacioException {
		String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info( "Afegeix document al procés ["
			+ "entorn=" + entornCodi
			+ ", expedient=" + expedientId
			+ ", proces=" + procesInstanceId
			+ ", usuari=" + usuari
			+ "]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			expedientDocumentService.create(
				expedientId,
				procesInstanceId,
				document.getCodi(),
				document.getData(),
				document.getArxiu(),
				document.getArxiu(),
				document.getContingut(),
				getContentType(document.getArxiu()),
				false,
				false,
				null,
				null,
				null,
				null,
				null,
				null
			);
		} catch (Exception ex) {
			log.error("No s'ha pogut afegir el document al procés d'expedient", ex);
			throw new TramitacioException("No s'ha pogut afegir el document al procés d'expedient: " + ex.getMessage());
		}
	}

	@Operation(summary = "Esborra un document del procés")
	@DeleteMapping("/expedients/{expedientId}/procesos/{procesInstanceId}/documents/{documentStoreId}")
	public void esborrarDocumentProces(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String procesInstanceId,
		@PathVariable Long documentStoreId) throws TramitacioException {
		String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info( "Esborra un document del procés  ["
			+ "entorn=" + entornCodi
			+ ", expedient=" + expedientId
			+ ", proces=" + procesInstanceId
			+ ", usuari=" + usuari
			+ ", document=" + documentStoreId
			+ "]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			expedientDocumentService.delete(expedientId, procesInstanceId, documentStoreId);
		} catch (Exception ex) {
			log.error("No s'ha pogut esborrar el document del procés", ex);
			throw new TramitacioException("No s'ha pogut esborrar el document del procés: " + ex.getMessage());
		}
	}

	@Operation(summary = "Consulta el contingut d'un document del procés")
	@GetMapping("/expedients/{expedientId}/procesos/{procesInstanceId}/documents/{documentStoreId}/contingut")
	public TramitacioDocument getDocumentProces(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String procesInstanceId,
		@PathVariable Long documentStoreId) throws TramitacioException {
		String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info( "Consulta el contingut d'un document del procés  ["
			+ "entorn=" + entornCodi
			+ ", expedient=" + expedientId
			+ ", proces=" + procesInstanceId
			+ ", usuari=" + usuari
			+ ", document=" + documentStoreId
			+ "]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			DocumentDto document = expedientDocumentService.findDocumentAmbId(documentStoreId);
			TramitacioDocument dt = new TramitacioDocument();
			dt.setId(document.getId());
			dt.setCodi(document.getDocumentCodi());
			dt.setNom(document.getDocumentNom());
			dt.setDescripcio(document.getDescripcio());
			dt.setArxiu(document.getArxiuNom());
			dt.setData(document.getDataDocument());
			dt.setContingut(document.getArxiuContingut());
			return dt;
		} catch (Exception ex) {
			log.error("No s'ha pogut recuperar el document del procés", ex);
			throw new TramitacioException("No s'ha pogut recuperar el document del procés: " + ex.getMessage());
		}
	}

	@Operation(summary = "Executa una acció d’un procés")
	@GetMapping("/expedients/{expedientId}/procesos/{procesInstanceId}/accio/{accioId}")
	public void execAccioProces(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String procesInstanceId,
		@PathVariable Long accioId) throws TramitacioException {
		String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info( "Executa una acció d’un procés  ["
			+ "entorn=" + entornCodi
			+ ", expedient=" + expedientId
			+ ", proces=" + procesInstanceId
			+ ", usuari=" + usuari
			+ ", accio=" + accioId
			+ "]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			expedientService.accioExecutar(expedientId, procesInstanceId, accioId);
		} catch (Exception ex) {
			log.error("No s'ha pogut executa una acció del procés", ex);
			throw new TramitacioException("No s'ha pogut executa una acció del procés: " + ex.getMessage());
		}
	}

	@Operation(summary = "Executa una acció d’un procés")
	@GetMapping("/expedients/{expedientId}/procesos/{procesInstanceId}/script")
	public void execAccioProces(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String procesInstanceId,
		@RequestBody TramitacioScript script) throws TramitacioException {
		String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info( "Executa una acció d’un procés  ["
			+ "entorn=" + entornCodi
			+ ", expedient=" + expedientId
			+ ", proces=" + procesInstanceId
			+ ", usuari=" + usuari
			+ "]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			expedientService.procesScriptExec(expedientId, procesInstanceId, script.getContingut());
		} catch (Exception ex) {
			log.error("No s'ha pogut executa una acció del procés", ex);
			throw new TramitacioException("No s'ha pogut executa una acció del procés: " + ex.getMessage());
		}
	}

	// TASQUES

	@Operation(summary = "Tasques del expedient", description = "Retorna el llistat de tasques del expedient")
	@GetMapping("/expedients/{expedientId}/tasques")
	public List<TramitacioTasca> consultaTasques(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@RequestParam(defaultValue = "false") Boolean personal,
		@RequestParam(defaultValue = "false") Boolean groupal) throws TramitacioException {
		log.info( "Consultant tasques d'expedient ["
				+ "expedient=" + expedientId
				+ ", entorn=" + entornCodi
				+ ", personal=" + personal
				+ ", groupal=" + groupal
				+ ", usuari=" + SecurityContextHolder.getContext().getAuthentication().getName()
				+"]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			List<ExpedientTascaDto> tasques = expedientTascaService
				.findPendents(expedient.getId(), personal, groupal);

			return tasques
					.stream()
					.map(this::convertirTascaTramitacio)
					.collect(Collectors.toList());
		} catch (Exception ex) {
			log.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	@Operation(summary = "Allibera tasca del expedient")
	@GetMapping("/expedients/{expedientId}/tasques/{tascaId}/alliberar")
	public void alliberaTasca(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String tascaId) throws TramitacioException {
		log.info( "Allibera tasca d'expedient ["
				+ "entorn=" + entornCodi
				+ ", expedient=" + expedientId
				+ ", tasca=" + tascaId
				+ ", usuari=" + SecurityContextHolder.getContext().getAuthentication().getName()
				+"]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			tascaService.alliberar(tascaId);
		} catch (Exception ex) {
			log.error("No s'ha pogut alliberar la tasca", ex);
			throw new TramitacioException("No s'ha pogut alliberar la tasca: " + ex.getMessage());
		}
	}

	@Operation(summary = "Agafa tasca del expedient")
	@GetMapping("/expedients/{expedientId}/tasques/{tascaId}/agafar")
	public void agafaTasca(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String tascaId) throws TramitacioException {
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info( "Agafa tasca d'expedient ["
				+ "entorn=" + entornCodi
				+ ", expedient=" + expedientId
				+ ", tasca=" + tascaId
				+ ", usuari=" + user
				+"]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			expedientTascaService.reassignar(expedientId, tascaId, user);
		} catch (Exception ex) {
			log.error("No s'ha pogut agafar la tasca", ex);
			throw new TramitacioException("No s'ha pogut agafar la tasca: " + ex.getMessage());
		}
	}

	@Operation(summary = "Completar tasca del expedient")
	@GetMapping("/expedients/{expedientId}/tasques/{tascaId}/completar")
	public void completarTasca(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String tascaId,
		@RequestParam(required=false) String sortida) throws TramitacioException {
		log.info( "Completar tasca d'expedient ["
			+ "entorn=" + entornCodi
			+ ", expedient=" + expedientId
			+ ", tasca=" + tascaId
			+ ", sortida=" + sortida
			+ ", usuari=" + SecurityContextHolder.getContext().getAuthentication().getName()
			+"]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			tascaService.validar(tascaId, new HashMap<>());
			tascaService.completar(tascaId, sortida);
		} catch (Exception ex) {
			log.error("No s'ha pogut completar la tasca", ex);
			throw new TramitacioException("No s'ha pogut completar la tasca: " + ex.getMessage());
		}
	}

	@Operation(summary = "Consulta camps que conformen el formulari per a una tasca")
	@GetMapping("/expedients/{expedientId}/tasques/{tascaId}/formulari")
	public List<TramitacioCamp> consultaFormulariTasca(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String tascaId) throws TramitacioException {
		log.info( "Consulta camps de tasca ["
				+ "entorn=" + entornCodi
				+ ", expedient=" + expedientId
				+ ", tasca=" + tascaId
				+ ", usuari=" + SecurityContextHolder.getContext().getAuthentication().getName()
				+"]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			List<TascaDadaDto> dades = tascaService.findDades(tascaId);
			return dades
					.stream()
					.map(this::convertirCampTasca)
					.collect(Collectors.toList());
		} catch (Exception ex) {
			log.error("No s'ha pogut consultar els camps de la tasca", ex);
			throw new TramitacioException("No s'ha pogut consultar els camps de la tasca: " + ex.getMessage());
		}
	}

	@Operation(summary = "Desa camps que conformen el formulari per una tasca")
	@PostMapping("/expedients/{expedientId}/tasques/{tascaId}/formulari")
	public void guardarFormulariTasca(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String tascaId,
		@RequestBody List<TramitacioCodiValor> variables) throws TramitacioException {
		log.info( "Desa camps de tasca  ["
				+ "entorn=" + entornCodi
				+ ", expedient=" + expedientId
				+ ", tasca=" + tascaId
				+ ", usuari=" + SecurityContextHolder.getContext().getAuthentication().getName()
				+ "]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			Map<String, Object> varaibles = new HashMap<String, Object>();
			for(TramitacioCodiValor cv : variables)
				varaibles.put(cv.getCodi(), cv.getValor());
			tascaService.guardar(tascaId, varaibles);
		} catch (Exception ex) {
			log.error("No s'ha pogut agafar la tasca", ex);
			throw new TramitacioException("No s'ha pogut agafar la tasca: " + ex.getMessage());
		}
	}

	@Operation(summary = "Consulta documents de tasca")
	@GetMapping("/expedients/{expedientId}/tasques/{tascaId}/documents")
	public List<TramitacioDocument> consultaDocumentsTasca(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String tascaId) throws TramitacioException {
		log.info( "Consulta documents de la tasca ["
				+ "entorn=" + entornCodi
				+ ", expedient=" + expedientId
				+ ", tasca=" + tascaId
				+ ", usuari=" + SecurityContextHolder.getContext().getAuthentication().getName()
				+ "]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			List<TascaDocumentDto> documents = tascaService.findDocuments(tascaId);
			return documents
					.stream()
					.map(this::convertirDocumentTasca)
					.collect(Collectors.toList());
		} catch (Exception ex) {
			log.error("No s'ha pogut agafar la tasca", ex);
			throw new TramitacioException("No s'ha pogut agafar la tasca: " + ex.getMessage());
		}
	}

	@Operation(summary = "Afegeix document a una tasca")
	@PostMapping("/expedients/{expedientId}/tasques/{tascaId}/documents")
	public void guardarDocumentsTasca(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String tascaId,
		@RequestBody TramitacioDocument document) throws TramitacioException {
		String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info( "Afegeix document a la tasca ["
			+ "entorn=" + entornCodi
			+ ", expedient=" + expedientId
			+ ", tasca=" + tascaId
			+ ", usuari=" + usuari
			+ "]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			tascaService.guardarDocumentTasca(
				entorn.getId(),
				tascaId,
				document.getCodi(),
				document.getData(),
				document.getArxiu(),
				document.getContingut(),
				getContentType(document.getArxiu()),
				false,
				false,
				null,
				usuari);
		} catch (Exception ex) {
			log.error("No s'ha pogut afegir el document a la tasca", ex);
			throw new TramitacioException("No s'ha pogut afegir el document a la tasca: " + ex.getMessage());
		}
	}

	@Operation(summary = "Esborra un document d'una tasca")
	@DeleteMapping("/expedients/{expedientId}/tasques/{tascaId}/documents/{documentCodi}")
	public void esborrarDocumentTasca(
		@PathVariable String entornCodi,
		@PathVariable Long expedientId,
		@PathVariable String tascaId,
		@PathVariable String documentCodi) throws TramitacioException {
		String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info( "Esborra un document de tasca  ["
			+ "entorn=" + entornCodi
			+ ", expedient=" + expedientId
			+ ", tasca=" + tascaId
			+ ", usuari=" + usuari
			+ ", document=" + documentCodi
			+ "]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if(expedient == null)
			throw new TramitacioException("No existeix cap expedient amb id: '" + expedientId + "'");

		try {
			tascaService.esborrarDocument(tascaId, documentCodi, usuari);
		} catch (Exception ex) {
			log.error("No s'ha pogut esborrar el document de la tasca", ex);
			throw new TramitacioException("No s'ha pogut esborrar el document de la tasca: " + ex.getMessage());
		}
	}

	@Operation(summary = "Tasques personals", description = "Retorna el llistat de tasques personals")
	@GetMapping("/tasques")
	public List<TramitacioTasca> consultaTasquesPersonals(
		@PathVariable String entornCodi) throws TramitacioException {
		String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info( "Consultant tasques personals ["
				+ "entorn=" + entornCodi
				+ ", usuari=" + usuari
				+ "]");
		EntornDto entorn = entornService.findAmbCodi(entornCodi);
		if(entorn == null)
			throw new TramitacioException("No existeix cap entorn amb el codi: '" + entornCodi + "'");

		try {
			PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
			paginacioParams.setPaginaNum(0);
			paginacioParams.setPaginaTamany(1<<15);
			paginacioParams.afegirOrdre(
				"id",
				PaginacioParamsDto.OrdreDireccioDto.DESCENDENT);

			PaginaDto<ExpedientTascaDto> pagina =
				tascaService.findPerFiltrePaginat(
					entorn.getId(),
					null,
					null,
					null,
					null,
					usuari,
					null,
					null,
					null,
					null,
					null,
					null,
					true,
					false,
					true,
					paginacioParams);

			return pagina
					.getContingut()
					.stream()
					.map(this::convertirTascaTramitacio)
					.collect(Collectors.toList());
		} catch (Exception ex) {
			log.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	private TramitacioDocument convertirDocumentTasca(
		TascaDocumentDto documentTasca) {
		TramitacioDocument dt = new TramitacioDocument();
		dt.setId(documentTasca.getId());

		dt.setCodi(documentTasca.getDocumentCodi());
		dt.setNom(documentTasca.getDocumentNom());
		dt.setDescripcio(null);
		dt.setArxiu(documentTasca.getArxiuNom());
		dt.setData(documentTasca.getDataDocument());
		return dt;
	}

	private TramitacioDocument convertirDocumentTasca(
		ExpedientDocumentDto document) {
		TramitacioDocument dt = new TramitacioDocument();
		dt.setId(document.getId());

		dt.setCodi(document.getDocumentCodi());
		dt.setNom(document.getDocumentNom());
		dt.setDescripcio(null);
		dt.setArxiu(document.getArxiuNom());
		dt.setData(document.getDataDocument());
		return dt;
	}

	private TramitacioCamp convertirCampTasca(
		TascaDadaDto dada) {

		TramitacioCamp ct = new TramitacioCamp();
		ct.setCodi(dada.getVarCodi());
		ct.setEtiqueta(dada.getCampEtiqueta());
		ct.setTipus(dada.getCampTipus().name());
		ct.setObservacions(dada.getObservacions());
		ct.setRequired(dada.isRequired());
		ct.setReadOnly(dada.isReadOnly());
		ct.setMultiple(dada.isCampMultiple());
		ct.setOcult(dada.isCampOcult());
		ct.setValor(dada.getVarValor());
		return ct;
	}
	private TramitacioCamp convertirCampTasca(
		ExpedientDadaDto dada) {
		TramitacioCamp ct = new TramitacioCamp();
		ct.setCodi(dada.getVarCodi());
		ct.setEtiqueta(dada.getCampEtiqueta());
		ct.setTipus(dada.getCampTipus().name());
		ct.setObservacions(dada.getObservacions());
		ct.setRequired(dada.isRequired());
		ct.setMultiple(dada.isCampMultiple());
		ct.setOcult(dada.isCampOcult());
		ct.setValor(dada.getVarValor());
		return ct;
	}

	private TramitacioExpedientInfo toExpedientInfo(ExpedientDto expedient) {
		if (expedient != null) {
			TramitacioExpedientInfo resposta = new TramitacioExpedientInfo();
			resposta.setTitol(expedient.getTitol());
			resposta.setNumero(expedient.getNumero());
			resposta.setNumeroDefault(expedient.getNumeroDefault());
			resposta.setIdentificador(expedient.getIdentificador());
			resposta.setDataInici(expedient.getDataInici());
			resposta.setDataFi(expedient.getDataFi());
			resposta.setComentari(expedient.getComentari());
			resposta.setInfoAturat(expedient.getInfoAturat());
			if (expedient.getIniciadorTipus().equals(ExpedientDto.IniciadorTipusDto.INTERN))
				resposta.setIniciadorTipus(TramitacioExpedientInfo.IniciadorTipus.INTERN);
			else if (expedient.getIniciadorTipus().equals(ExpedientDto.IniciadorTipusDto.SISTRA))
				resposta.setIniciadorTipus(TramitacioExpedientInfo.IniciadorTipus.SISTRA);
			resposta.setIniciadorCodi(expedient.getIniciadorCodi());
			resposta.setResponsableCodi(expedient.getResponsableCodi());
			resposta.setGeoPosX(expedient.getGeoPosX());
			resposta.setGeoPosY(expedient.getGeoPosY());
			resposta.setGeoReferencia(expedient.getGeoReferencia());
			resposta.setRegistreNumero(expedient.getRegistreNumero());
			resposta.setRegistreData(expedient.getRegistreData());
			resposta.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
			resposta.setIdioma(expedient.getIdioma());
			resposta.setAutenticat(expedient.isAutenticat());
			resposta.setTramitadorNif(expedient.getTramitadorNif());
			resposta.setTramitadorNom(expedient.getTramitadorNom());
			resposta.setInteressatNif(expedient.getInteressatNif());
			resposta.setInteressatNom(expedient.getInteressatNom());
			resposta.setRepresentantNif(expedient.getRepresentantNif());
			resposta.setRepresentantNom(expedient.getRepresentantNom());
			resposta.setAvisosHabilitats(expedient.isAvisosHabilitats());
			resposta.setAvisosEmail(expedient.getAvisosEmail());
			resposta.setAvisosMobil(expedient.getAvisosMobil());
			resposta.setNotificacioTelematicaHabilitada(expedient.isNotificacioTelematicaHabilitada());
			resposta.setTramitExpedientIdentificador(expedient.getTramitExpedientIdentificador());
			resposta.setTramitExpedientClau(expedient.getTramitExpedientClau());
			resposta.setExpedientTipusCodi(expedient.getTipus().getCodi());
			resposta.setExpedientTipusNom(expedient.getTipus().getNom());
			resposta.setEntornCodi(expedient.getEntorn().getCodi());
			if (expedient.getEstat() != null) {
				resposta.setEstatCodi(expedient.getEstat().getCodi());
				resposta.setEstatNom(expedient.getEstat().getNom());
			}
			resposta.setProcessInstanceId(expedient.getProcessInstanceId());
			return resposta;
		}
		return null;
	}

	private TramitacioTasca convertirTascaTramitacio(ExpedientTascaDto tasca) {
		TramitacioTasca tt = new TramitacioTasca();
		tt.setId(tasca.getId());
		tt.setCodi(tasca.getBpmName());
		tt.setTitol(tasca.getTitol());
		tt.setMissatgeInfo(tasca.getTascaMissatgeInfo());
		tt.setMissatgeWarn(tasca.getTascaMissatgeWarn());
		if (tasca.getResponsable() != null)
			tt.setResponsable(tasca.getResponsable().getCodi());
		Set<String> responsables = new HashSet<String>();
		if (tasca.getResponsables() != null)
			for (PersonaDto responsable: tasca.getResponsables()) {
				responsables.add(responsable.getCodi());
			}

		List<String> outcomes = null;
		if(tasca.getOutcomes() != null) {
			outcomes = tasca.getOutcomes()
				.stream()
				.map(TascaOutcomeDto::getId)
				.collect(Collectors.toList());
		}

		tt.setResponsables(responsables);
		tt.setDataCreacio(tasca.getCreateTime());
		tt.setDataInici(tasca.getStartTime());
		tt.setDataFi(tasca.getEndTime());
		tt.setDataLimit(tasca.getDueDate());
		tt.setPrioritat(tasca.getPriority());
		tt.setOpen(tasca.isOpen());
		tt.setCompleted(tasca.isCompleted());
		tt.setCancelled(tasca.isCancelled());
		tt.setSuspended(tasca.isSuspended());
		tt.setTransicionsSortida(outcomes);
		tt.setProcessInstanceId(tasca.getProcessInstanceId());
		return tt;
	}

	public String getContentType(String arxiuNom) {
		return MediaTypeFactory.getMediaType(arxiuNom)
			.map(MediaType::toString)
			.orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
	}

}
