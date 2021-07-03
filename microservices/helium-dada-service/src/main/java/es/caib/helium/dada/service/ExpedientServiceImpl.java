package es.caib.helium.dada.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import es.caib.helium.dada.domain.Dada;
import es.caib.helium.dada.domain.Expedient;
import es.caib.helium.dada.exception.DadaException;
import es.caib.helium.dada.model.Consulta;
import es.caib.helium.dada.model.PagedList;
import es.caib.helium.dada.repository.DadaRepository;
import es.caib.helium.dada.repository.ExpedientRepository;
import es.caib.helium.enums.ValorsValidacio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Classe d'implementació de ExpedientService
 * Encarregada de la lògica intermitja entre el controlador i els repositoris
 *
 */
@Slf4j
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "es.caib.helium") // , ignoreUnknownFields = true)
@Service
public class ExpedientServiceImpl implements ExpedientService {

	private final ExpedientRepository expedientRepository;
	private final DadaRepository dadaRepository;

	// Dades capçalera expedient

	/**
	 * Cerca els expedients i les seves dades filtrades segons la consulta
	 * @param consulta Objecte que conté la informació per filtrar
	 * @return Retorna una PagedList on cada element representa les dades d'un expedient
	 */
	@Override
	public PagedList<Expedient> consultaResultats(Consulta consulta) throws DadaException {
		
		try {
			log.debug("[SRV] Consultant resultats paginats");
			var pagina = expedientRepository.findByFiltres(consulta);
			var pageable = PageRequest.of(consulta.getPage(), pagina.size() > 0 ? pagina.size() : 1);
			var resultat = new PagedList<Expedient>(pagina, pageable, pagina.size());
			log.debug("Consulta resultats paginat correctament");
			return resultat;
			
		} catch (Exception ex) {
			var error = "Error consuslta resultats paginat";
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Cerca els expedients i les seves dades filtrades segons la consulta
	 * @param consulta Objecte que conté la informació per filtrar
	 * @return Retorna una llista on cada element representa les dades d'un expedient
	 */
	@Override
	public List<Expedient> consultaResultatsLlistat(Consulta consulta) throws DadaException {
		try {
			var resultat = expedientRepository.findByFiltres(consulta);
			log.debug("Consulta resultats llistat correctament" );
			return resultat;

		} catch (Exception ex) {
			var error = "Error consulta resultats llistat";
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Crea les dades de capçalera d'un expedient. Si ja existeix un expedientId no el crea.
	 * @param expedient Objecte amb les dades a guardar
	 * @return True si hi s'ha creat. False si ja existia ho es produeix una excepció
	 */
	@Override
	public boolean createExpedient(Expedient expedient) throws DadaException {

		try {
			var exp = expedientRepository.findByExpedientId(expedient.getExpedientId());
			if (exp.isPresent()) {
				return false;
			}
			expedientRepository.save(expedient);
			log.debug("Dades capçalera creades correctament");
			return true;
			
		} catch (Exception ex) {
			var error = "Error creant les dades de capçalera";
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Crea les dades de capçalera per cada expedientId que no existeixi prèviament. 
	 * Si ja existeix l'expedientId es descarta.
	 * @param expedients llista on cada element fa referencia a les dades de capçalera d'un expedient
	 * @return True si s'ha pogut guardar com a mínim un element de la llista. False tots existeixen o excepci)ó.
	 */
	@Override
	public boolean createExpedients(List<Expedient> expedients) throws DadaException {

		try {
			List<Expedient> exps = new ArrayList<>();
			for (var expedient : expedients) {
				if (expedientRepository.findByExpedientId(expedient.getExpedientId()).isPresent()) {
					log.debug("[SRV] Expedient (expedientId=" + expedient.getExpedientId()
							+ " ja existeix, no es guardarà");
					continue;
				}
				exps.add(expedient);
			}
			if (exps.isEmpty()) {
				return false;
			}
			expedientRepository.saveAll(exps);
			log.debug("Dades capçalera expedient creades per múltiples expedients");
			return true;
			
		} catch (Exception ex) {
			var error = "Error creant dades de capçalera per multiples expedients";
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Cerca les dades de capçalera de l'expedient
	 * @param expedientId identificador de l'expedient pel qual s'en cerquen les dades
	 * @return Retorna l'objecte que conté la informació de les dades de l'expedient.
	 */
	@Override
	public Expedient findByExpedientId(Long expedientId) throws DadaException {

		try {
			var expedient = expedientRepository.findByExpedientId(expedientId);
			if (expedient.isEmpty()) {
				log.error("Expedient amb id " + expedientId + " no trobat");
				return null;
			}
			log.debug("Dades de capçalera obtingudes correctament amb expedientId " + expedientId);
			return expedient.get();
			
		} catch (Exception ex) {
			var error = "Error buscant les dades de capçalera per l'expedient " + expedientId;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Esborra totes les dades que fan referencia a l'expedient
	 * @param expedientId identificador de l'expedient pel que es borraran les dades
	 * @return Retorna True si s'ha esobrrat. False si l'expedientId no existeix o excepció.
	 */
	@Override
	public boolean deleteExpedient(Long expedientId) throws DadaException {

		try {
			var expedient = expedientRepository.findByExpedientId(expedientId);
			if (expedient.isEmpty()) {
				return false; // Així es pot retornar el 404 al Controller
			}
			var esborrat = expedientRepository.esborrarExpedientCascade(expedientId);
			log.debug("Esborradesc dades capçalera expedient amb expedientId " + expedientId);
			return esborrat == 1;
			
		} catch (Exception ex) {
			var error = "Error esborrant les dades de capçalera per l'expedient " + expedientId;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Esborra totes les dades dels expedients continguts a la llista
	 * @param expedients llista de expedientsId a esborrar
	 * @return True si s'ha esborrat com a mínim un expedient. False altrament o excepció. 
	 */
	@Override
	public boolean deleteExpedients(List<Long> expedients) throws DadaException {

		try {
			if (expedients.isEmpty()) {
				return false;
			}
			var esborrades = expedientRepository.esborrarExpedientsCascade(expedients);
			log.debug("Dades esborrades per múltiples expedients " + expedients.toString());
			return esborrades > 0;
			
		} catch (Exception ex) {
			var error = "Error esborrant les dades de capçalera per multiples expedients " + expedients.toString();
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/*
	 * Actualitza les dades de capçalera de l'expedientId
	 * @param expedientId identificador de l'expedient 
	 * @param expedient dades a fer el put
	 * @return True si guarda correctament. False si l'expedient no existeix o excepció
	 */
	@Override
	public boolean putExpedient(Long expedientId, Expedient expedient) throws DadaException {

		try {
			var exp = expedientRepository.findByExpedientId(expedientId);
			if (exp.isEmpty()) {
				return false;
			}
			exp.get().setEntornId(expedient.getEntornId());
			exp.get().setTipusId(expedient.getTipusId());
			exp.get().setNumero(expedient.getNumero());
			exp.get().setTitol(expedient.getTitol());
			exp.get().setProcesPrincipalId(expedient.getProcesPrincipalId());
			exp.get().setEstatId(expedient.getEstatId());
			exp.get().setDataInici(expedient.getDataInici());
			exp.get().setDataFi(expedient.getDataFi());
			expedientRepository.save(exp.get());
			log.debug("Dades de capçalera actualitzades (put) correctament per l'expedient " + expedientId);
			return true;
			
		} catch (Exception ex) {
			var error = "Error actualitzant (put) les dades de capçalera per l'expedient " + expedientId;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/*
	 * Actualitza les dades de capçalera per cada element de la llista
	 * @param expedients llista amb els expedients a actualitzar
	 * @return True si guarda correctament almenys un expedient. False altrament o excepció
	 */
	@Override
	public boolean putExpedients(List<Expedient> expedients) throws DadaException {
		
		try {
			List<Expedient> exps = new ArrayList<>();
			for (var expedient : expedients) {
				var exp = expedientRepository.findByExpedientId(expedient.getExpedientId());
				if (exp.isEmpty()) {
					continue;
				}
				exp.get().setEntornId(expedient.getEntornId());
				exp.get().setTipusId(expedient.getTipusId());
				exp.get().setNumero(expedient.getNumero());
				exp.get().setTitol(expedient.getTitol());
				exp.get().setProcesPrincipalId(expedient.getProcesPrincipalId());
				exp.get().setEstatId(expedient.getEstatId());
				exp.get().setDataInici(expedient.getDataInici());
				exp.get().setDataFi(expedient.getDataFi());
				exps.add(exp.get());
			}
			if (exps.isEmpty()) {
				return false;
			}
			
			var guardats = expedientRepository.saveAll(exps).size();
			log.debug("Expedients (put) guardats correctament " + expedients.toString());
			return guardats > 0;
		} catch (Exception ex) {
			var error = "Error actualitzant (put) les dades de capçalera per els expedients " + expedients.toString();
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Actualitza les dades de capçalera de l'expedient. Només actualitza la dada si té contingut.
	 * @param expedientId identificador de l'expedient
	 * @param expedient dades a actualitzar
	 * @return True si s'actualitza correctament. False si no existeix l'expedient o excepció
	 */
	@Override
	public boolean patchExpedient(Long expedientId, Expedient expedient) throws DadaException {

		try {
			var expOpt = expedientRepository.findByExpedientId(expedientId);
			if (expOpt.isEmpty()) {
				return false;
			}
			var exp = expOpt.get();
			exp.setEntornId(expedient.getEntornId() != null ? expedient.getEntornId() : exp.getEntornId());
			exp.setTipusId(expedient.getTipusId() != null ? expedient.getTipusId() : exp.getTipusId());
			exp.setNumero(expedient.getNumero() != null && !expedient.getNumero().isEmpty() 
					&& expedient.getNumero().length() <= ValorsValidacio.TITOL_MAX_LENGTH.getValor() ? expedient.getNumero() : exp.getNumero());
			exp.setTitol(expedient.getTitol() != null && !expedient.getTitol().isEmpty()
					&& expedient.getTitol().length() <= ValorsValidacio.TITOL_MAX_LENGTH.getValor() ? expedient.getTitol() : exp.getTitol());
			exp.setProcesPrincipalId(expedient.getProcesPrincipalId() != null ? expedient.getProcesPrincipalId()
					: exp.getProcesPrincipalId());
			exp.setEstatId(expedient.getEstatId() != null ? expedient.getEstatId() : exp.getEstatId());
			exp.setDataInici(expedient.getDataInici() != null ? expedient.getDataInici() : exp.getDataInici());
			exp.setDataFi(expedient.getDataFi() != null ? expedient.getDataFi() : exp.getDataFi());
			expedientRepository.save(exp);
			log.debug("Expedient " + expedientId + " acutalitzat (patch) correctament");
			return true;
		} catch (Exception ex) {
			var error = "Error actualitzant (patch) les dades de capçalera per l'expedient " + expedientId;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Actualitza els expedients continguts a la llista. Només actualitza els camps que contenen valor
	 * @param expedients llista d'expedients 
	 * @return True si s'actualitza almenys un expedient. False altrament o excepció
	 */
	@Override
	public boolean patchExpedients(List<Expedient> expedients) throws DadaException {
		
		try {
			List<Expedient> exps = new ArrayList<>();
			for (var expedient : expedients) {
				var expOpt = expedientRepository.findByExpedientId(expedient.getExpedientId());
				if (expOpt.isEmpty()) {
					continue;
				}
				var exp = expOpt.get();
				exp.setEntornId(expedient.getEntornId() != null ? expedient.getEntornId() : exp.getEntornId());
				exp.setTipusId(expedient.getTipusId() != null ? expedient.getTipusId() : exp.getTipusId());
				exp.setNumero(expedient.getNumero() != null && !expedient.getNumero().isEmpty() ? expedient.getNumero()
						: exp.getNumero());
				exp.setTitol(expedient.getTitol() != null && !expedient.getTitol().isEmpty() ? expedient.getTitol()
						: exp.getTitol());
				exp.setProcesPrincipalId(expedient.getProcesPrincipalId() != null ? expedient.getProcesPrincipalId()
						: exp.getProcesPrincipalId());
				exp.setEstatId(expedient.getEstatId() != null ? expedient.getEstatId() : exp.getEstatId());
				exp.setDataInici(expedient.getDataInici() != null ? expedient.getDataInici() : exp.getDataInici());
				exp.setDataFi(expedient.getDataFi() != null ? expedient.getDataFi() : exp.getDataFi());
				exps.add(exp);
			}
			if (exps.isEmpty()) {
				return false;
			}
			var actualitzats = expedientRepository.saveAll(exps).size();
			log.debug("Actualitzats (patch) els expedients " + expedients.toString());
			return actualitzats > 0;
			
		} catch (Exception ex) {
			var error = "Error actualitzant (patch) les dades de capçalera pels expedients " + expedients.toString();
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	// Dades expedient

	/**
	 * Cerca la llista de dades que fan referencia a l'expedient
	 * @param expedientId identificador de l'expedient
	 * @return Retorna una llista amb les dades de l'expedient (no son dades de capçalera). Llista buida si no hi han dades o excepció.
	 */
	@Override
	public List<Dada> getDades(Long expedientId) throws DadaException {
		
		try {
			var dades = dadaRepository.findByExpedientId(expedientId);
			log.debug("Consulta de dades correctament per l'expedient " + expedientId);
			return dades.isPresent() ? dades.get() : new ArrayList<Dada>();
		} catch (Exception ex) {
			var error = "Error obtinguent les dades per l'expedientId " + expedientId;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Cerca la dada amb expedientId i codi
	 * @param expedientId identificador de l'expedient
	 * @param codi codi de la dada
	 * @return Retorna una llista amb les dades de l'expedient (no son dades de capçalera). Null si no hi han dades o excepció.
	 */
	@Override
	public Dada getDadaByCodi(Long expedientId, String codi) throws DadaException {
		
		try {
			var dades = dadaRepository.findByExpedientIdAndCodi(expedientId, codi);
			log.debug("Consulta de dades correctament per l'expedient " + expedientId + " amb codi " + codi);
			return dades.isPresent() ? dades.get() : null;
		} catch (Exception ex) {
			var error = "Error obtinguent les dades per l'expedientId " + expedientId + " amb codi " + codi;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Cerca la llista de dades que fan referencia a l'expedient i al procesId
	 * @param expedientId identificador de l'expedient
	 * @param procesId identificador del procés
	 * @return Retorna una llista amb les dades de l'expedient (no son dades de capçalera). Lista buida si no hi han dades o excepció.
	 */
	@Override
	public List<Dada> getDadesByProces(Long expedientId, Long procesId) throws DadaException {

		try {
			var dades = dadaRepository.findByExpedientIdAndProcesId(expedientId, procesId);
			log.debug("Consulta de dades correctament per l'expedient " + expedientId + " amb procesId " + procesId);
			return dades.isPresent() ? dades.get() : new ArrayList<Dada>();
		} catch (Exception ex) {
			var error = "Error obtinguent les dades per l'expedientId " + expedientId + " amb procesId " + procesId;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Cerca la dada segons el expedientId procesId i codi
	 * @param expedientId identificador de l'expedient
	 * @param procesId identificador del procés 
	 * @codi codi codi de la dada
	 * @return Retorna la Dada resultant de la cerca. Null si no existeix o excepció
	 */
	@Override
	public Dada getDadaByExpedientIdProcesAndCodi(Long expedientId, Long procesId, String codi) throws DadaException {
		try {
			var dada = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
			log.debug("Consulta de dades correctament per l'expedient "
					+ expedientId + " amb procesId " + procesId + " codi " + codi);
			return dada.isPresent() ? dada.get() : null;
		} catch (Exception ex) {
			var error = "Error obtinguent les dades per l'expedientId " 
					+ expedientId + " amb procesId " + procesId + " i codi " + codi;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Cerca la dada segons el expedientId procesId i codi
	 * @param expedientId identificador de l'expedient
	 * @param procesId identificador del procés 
	 * @codi codi codi de la dada
	 * @return Retorna la Dada resultant de la cerca. Null si no existeix o excepció
	 */
	@Override
	public Dada getDadaByProcesAndCodi(Long procesId, String codi) throws DadaException {
		try {
			var dada = dadaRepository.findByProcesIdAndCodi(procesId, codi);
			log.debug("Consulta de dades correctament pel procesId " + procesId + " amb codi " + codi);
			return dada.isPresent() ? dada.get() : null;
		} catch (Exception ex) {
			var error = "Error al consultar les dades pel procesId " + procesId + " amb codi " + codi;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Cerca l'expedientId segons el procesId 
	 * Totes les dades amb el mateix procesId han de tenir el mateix expedientId
	 * @param procesId identificador del procés 
	 * @return Retorna l'expedientId del procesId consultat. Null si no existeix o excepció si es troba més d'un expedientId diferent
	 */
	@Override
	public Long getDadaExpedientIdByProcesId(Long procesId) throws DadaException {
		
		try {
			var optional = dadaRepository.findByProcesId(procesId);
			if (optional.isEmpty() || optional.get().isEmpty()) {
				return null;
			}

			var dades = optional.get();
			var expedientId = dades.get(0).getExpedientId();
			for (var dada : dades) {
				if (!dada.getExpedientId().equals(expedientId)) {
					throw new DadaException("Dades amb expedientId diferents");
				}
			}
			return expedientId;
			
		} catch (Exception ex) {
			var error = "Error obtinguent l'expedient id per les dades amb procesId " + procesId;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/*
	 * Crea les dades per un expediente i un procés.
	 * Si ja existeix una dada amb expedientId, procesId i codi, la dada no es guardarà
	 * @param expedientId 
	 * @param procesId
	 * @param dades
	 * @return True si s'ha guardat almenys una dada. False si totes les dades ja existeixen. 
	 */
	@Override
	public boolean createDades(Long expedientId, Long procesId, List<Dada> dades) throws DadaException {

		try {
			List<Dada> dadesFoo = new ArrayList<>();
			for (var dada : dades) {
			
				var d = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, dada.getCodi());
				if (d.isPresent()) {
					// Evitar dades amb el codi repetit segons proces i codi
					log.error("[ExpedientServiceImpl.crearDada] Expedient amb id " + expedientId + " procesId "
							+ procesId + " ja té una dada amb codi " + dada.getCodi());
					continue;
				}
				dada.setExpedientId(expedientId);
				dada.setProcesId(procesId);
				dadesFoo.add(dada);
			}
			if (dadesFoo.isEmpty()) {
				return false;
			}
			log.debug("Dades per l'expedient " + expedientId + " amb procesId " + procesId + " creades correctament");
			var guardats = dadaRepository.saveAll(dadesFoo).size();
			return guardats > 0;
		} catch (Exception ex) {
			var error = "Error al crear les dades per l'expedient " + expedientId + " amb procesId " + procesId;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Actualitza la dada amb expedientId i codi
	 * @param expedientId identificador de l'expedient
	 * @param codi codi de l'expedient
	 * @param dada informació a actualitzar
	 * @return True si la dada s'ha actualitzat. False si no existeix la dada o excepció
	 */
	@Override
	public boolean putDadaByExpedientIdAndCodi(Long expedientId, String codi, Dada dada) throws DadaException {

		try {
			var dadaOptional = dadaRepository.findByExpedientIdAndCodi(expedientId, codi);
			if (dadaOptional.isEmpty()) {
				return false;
			}

			var dadaMongo = dadaOptional.get();
			dadaMongo.setMultiple(dada.isMultiple());
			dadaMongo.setTipus(dada.getTipus());
			dadaMongo.setValor(dada.getValor());
			dadaRepository.save(dadaMongo);
			log.debug("Dada amb codi " + codi + " actualitzada correctament per l'expedient " + expedientId); 
			return true;
		} catch (Exception ex) {
			var error = "Error al actualitzar (put) la dada per l'expedient " + expedientId + " amb codi " + codi;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Esborra la dada amb expedientId i codi
	 * @param expedientId identificador de l'expedient
	 * @param codi codi de l'expedient
	 * @Return True si la dada s'esborra.
	 */
	@Override
	public boolean deleteDadaByExpedientIdAndCodi(Long expedientId, String codi) throws DadaException {

		try {
			// TODO PREGUNTAR IS ÉS UN DELETE ALL PERQUÈ SINO AIXÒ POT RETORNAR MÉS D'UNA
			// DADA, O NO, DEPEN DE LES VALIDACIONS
			var dada = dadaRepository.findByExpedientIdAndCodi(expedientId, codi);
			if (dada.isEmpty()) {
				return false;
			}
			dadaRepository.delete(dada.get());
			log.debug("Dada amb codi " + codi + " esborrada correctament per l'expedientId " + expedientId);
			return true;
		} catch (Exception ex) {
			var error = "Error al esborrar la dada per l'expedient " + expedientId + " amb codi " + codi;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Crea dades per l'expedientId amb procesId
	 * @param expedientId identificador de l'expedient
	 * @param procesId identificador del procés
	 * @param dades llistat de dades a crear
	 * @return True si almenys s'ha guardat una dada. Fals altrament o excepció;
	 */
	@Override
	public boolean postDadesByExpedientIdProcesId(Long expedientId, Long procesId, List<Dada> dades) throws DadaException {
		
		try {
			Set<String> dadesSet = new HashSet<>();
			List<Dada> dadesDistinct = dades.stream().filter(d -> dadesSet.add(d.getCodi()))
					.collect(Collectors.toList()); // Eliminar repetits
			var dadesMongo = new ArrayList<Dada>();
			for (var dada : dadesDistinct) {
				if (dada.getCodi() == null || dada.getCodi().isEmpty()) {
					continue;
				}
				var dadaMongo = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId,
						dada.getCodi());
				if (dadaMongo.isPresent()) {
					var d = dadaMongo.get();
					d.setMultiple(dada.isMultiple());
					d.setTipus(dada.getTipus());
					d.setValor(dada.getValor());
					dadesMongo.add(d);
					continue;
				}
				dada.setExpedientId(expedientId);
				dada.setProcesId(procesId);
				dadesMongo.add(dada);
			}
			if (dadesMongo.isEmpty()) {
				return false;
			}
			log.debug("Dades guardades per l'expedientId" + expedientId + " amb procesId " + procesId);
			return dadaRepository.saveAll(dadesMongo).size() > 0;
		} catch (Exception ex) {
			var error = "Error al guardar les dades per l'expedient " + expedientId + " amb procesId " + procesId;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Actualitza la dada amb expedientId procesId i codi 
	 * @param expedientId identificador de l'expedient
	 * @param procesId identificador del procés
	 * @param codi codi de la dada
	 * @param dada informació a actualitzar
	 * @return True si la dada s'ha actualitzat correctament. False si o existeix la dada o excepció.
	 */
	@Override
	public boolean putDadaByExpedientIdProcesIdAndCodi(
			Long expedientId, Long procesId, String codi, Dada dada) throws DadaException {

		try {
			var dadaOptional = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
			if (dadaOptional.isEmpty()) {
				return false;
			}
			var dadaMongo = dadaOptional.get();
			dadaMongo.setMultiple(dada.isMultiple());
			dadaMongo.setTipus(dada.getTipus());
			dadaMongo.setValor(dada.getValor());
			dadaMongo.setValor(dada.getValor());
			dadaRepository.save(dadaMongo);
			log.debug("Dada actualitzada (put) per l'expedientId " 
					+ expedientId + " amb procesId " + procesId + " i codi " + codi);
			return true;
		} catch (Exception ex) {
			var error = "Error al actualitzar (put) la dada per l'expedient "
					+ expedientId + " amb procesId " + procesId + " i codi " + codi;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}

	/**
	 * Esborra la dada amb expedientId procesId i codi
	 * @param expedientId identificador de l'expedient
	 * @procesId identificador del procés
	 * @codi codi de la dada 
	 * @return True si la dada s'ha esborrat correctament. False si no existeix o excepció
	 */
	@Override
	public boolean deleteDadaByExpedientIdAndProcesIdAndCodi(
			Long expedientId, Long procesId, String codi) throws DadaException {

		try {
			var dada = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
			if (dada.isEmpty()) {
				log.error("[ExpedientServiceImpl.deleteByExpedientIdAndCodi] No existeix la dada amb expedientId "
						+ expedientId + " i codi " + codi);
				return false;
			}
			dadaRepository.delete(dada.get());
			log.debug("Dada actualitzada (put) per l'expedientId " 
					+ expedientId + " amb procesId " + procesId + " i codi " + codi);
			return true;
		} catch (Exception ex) {
			var error = "Error al esbsorrar la dada per l'expedient "
					+ expedientId + " amb procesId " + procesId + " i codi " + codi;
			log.error(error, ex);
			throw new DadaException(error, ex);
		}
	}
}