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
	public PagedList<Expedient> consultaResultats(Consulta consulta) {
		
		try {
			log.debug("[SRV] Consultant resultats paginats");
			var pagina = expedientRepository.findByFiltres(consulta);
			var pageable = PageRequest.of(consulta.getPage(), pagina.size() > 0 ? pagina.size() : 1);
			return new PagedList<Expedient>(pagina, pageable, pagina.size());
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.consultaResultatsLlistat] --->");
			e.printStackTrace();
			var pageable = PageRequest.of(consulta.getPage(), 1);
			return new PagedList<Expedient>(new ArrayList<Expedient>(), pageable, 10);
		}
	}

	/**
	 * Cerca els expedients i les seves dades filtrades segons la consulta
	 * @param consulta Objecte que conté la informació per filtrar
	 * @return Retorna una llista on cada element representa les dades d'un expedient
	 */
	@Override
	public List<Expedient> consultaResultatsLlistat(Consulta consulta) {
		try {
			log.debug("[SRV] Consultant resultats");
			return expedientRepository.findByFiltres(consulta);
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.consultaResultatsLlistat] --->");
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	/**
	 * Crea les dades de capçalera d'un expedient. Si ja existeix un expedientId no el crea.
	 * @param expedient Objecte amb les dades a guardar
	 * @return True si hi s'ha creat. False si ja existia ho es produeix una excepció
	 */
	@Override
	public boolean createExpedient(Expedient expedient) {

		try {
			log.debug("[SRV] Creant dades capçalera expedient (expedient= " + expedient.toString());
			var exp = expedientRepository.findByExpedientId(expedient.getExpedientId());
			if (exp.isPresent()) {
				return false;
			}
			expedientRepository.save(expedient);
			return true;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.createExpedient] --->");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Crea les dades de capçalera per cada expedientId que no existeixi prèviament. 
	 * Si ja existeix l'expedientId es descarta.
	 * @param expedients llista on cada element fa referencia a les dades de capçalera d'un expedient
	 * @return True si s'ha pogut guardar com a mínim un element de la llista. False tots existeixen o excepció.
	 */
	@Override
	public boolean createExpedients(List<Expedient> expedients) {

		try {
			log.debug("[SRV] Creant dades capçalera expedient per múltiples expedients");
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
			return true;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.createExpedients] --->");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Cerca les dades de capçalera de l'expedient
	 * @param expedientId identificador de l'expedient pel qual s'en cerquen les dades
	 * @return Retorna l'objecte que conté la informació de les dades de l'expedient.
	 */
	@Override
	public Expedient findByExpedientId(Long expedientId) {

		try {
			log.debug("[SRV] Buscant les dades de capçalera de l'expedient (expedientId= " + expedientId);
			var expedient = expedientRepository.findByExpedientId(expedientId);
			if (expedient.isEmpty()) {
				log.error("[ExpedientServiceImpl.findById] Expedient amb id " + expedientId + " no trobat");
				return null;
			}
			return expedient.get();
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.findByExpedientId] --->");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Esborra totes les dades que fan referencia a l'expedient
	 * @param expedientId identificador de l'expedient pel que es borraran les dades
	 * @return Retorna True si s'ha esobrrat. False si l'expedientId no existeix o excepció.
	 */
	@Override
	public boolean deleteExpedient(Long expedientId) {

		try {
			log.debug("[SRV] Borrant dades capçalera expedient (expedientId= " + expedientId);
			var expedient = expedientRepository.findByExpedientId(expedientId);
			if (expedient.isEmpty()) {
				return false; // Així es pot retornar el 404 al Controller
			}
			return expedientRepository.esborrarExpedientCascade(expedientId) == 1;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.deleteExpedient] --->");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Esborra totes les dades dels expedients continguts a la llista
	 * @param expedients llista de expedientsId a esborrar
	 * @return True si s'ha esborrat com a mínim un expedient. False altrament o excepció. 
	 */
	@Override
	public boolean deleteExpedients(List<Long> expedients) {

		try {
			log.debug("[SRV] Borrant dades capçalera múltiples expedients");
			if (expedients.isEmpty()) {
				return false;
			}
			return expedientRepository.esborrarExpedientsCascade(expedients) > 0;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.deleteExpedients] --->");
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * Actualitza les dades de capçalera de l'expedientId
	 * @param expedientId identificador de l'expedient 
	 * @param expedient dades a fer el put
	 * @return True si guarda correctament. False si l'expedient no existeix o excepció
	 */
	@Override
	public boolean putExpedient(Long expedientId, Expedient expedient) {

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
			return true;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.putExpedient] --->");
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * Actualitza les dades de capçalera per cada element de la llista
	 * @param expedients llista amb els expedients a actualitzar
	 * @return True si guarda correctament almenys un expedient. False altrament o excepció
	 */
	@Override
	public boolean putExpedients(List<Expedient> expedients) {
		
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
			return expedientRepository.saveAll(exps).size() > 0;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.putExpedients] --->");
			e.printStackTrace();
			return true;
		}
	}

	/**
	 * Actualitza les dades de capçalera de l'expedient. Només actualitza la dada si té contingut.
	 * @param expedientId identificador de l'expedient
	 * @param expedient dades a actualitzar
	 * @return True si s'actualitza correctament. False si no existeix l'expedient o excepció
	 */
	@Override
	public boolean patchExpedient(Long expedientId, Expedient expedient) {

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
			return true;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.patchExpedient] --->");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Actualitza els expedients continguts a la llista. Només actualitza els camps que contenen valor
	 * @param expedients llista d'expedients 
	 * @return True si s'actualitza almenys un expedient. False altrament o excepció
	 */
	@Override
	public boolean patchExpedients(List<Expedient> expedients) {
		
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
			return expedientRepository.saveAll(exps).size() > 0;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.patchExpedients] --->");
			e.printStackTrace();
			return false;
		}
	}

	// Dades expedient

	/**
	 * Cerca la llista de dades que fan referencia a l'expedient
	 * @param expedientId identificador de l'expedient
	 * @return Retorna una llista amb les dades de l'expedient (no son dades de capçalera). Llista buida si no hi han dades o excepció.
	 */
	@Override
	public List<Dada> getDades(Long expedientId) {
		
		try {
			var dades = dadaRepository.findByExpedientId(expedientId);
			return dades.isPresent() ? dades.get() : new ArrayList<Dada>();
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.getDades] --->");
			e.printStackTrace();
			return new ArrayList<Dada>();
		}
	}

	/**
	 * Cerca la dada amb expedientId i codi
	 * @param expedientId identificador de l'expedient
	 * @param codi codi de la dada
	 * @return Retorna una llista amb les dades de l'expedient (no son dades de capçalera). Null si no hi han dades o excepció.
	 */
	@Override
	public Dada getDadaByCodi(Long expedientId, String codi) {
		
		try {
			var dades = dadaRepository.findByExpedientIdAndCodi(expedientId, codi);
			return dades.isPresent() ? dades.get() : null;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.getDadaByCodi] --->");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Cerca la llista de dades que fan referencia a l'expedient i al procesId
	 * @param expedientId identificador de l'expedient
	 * @param procesId identificador del procés
	 * @return Retorna una llista amb les dades de l'expedient (no son dades de capçalera). Lista buida si no hi han dades o excepció.
	 */
	@Override
	public List<Dada> getDadesByProces(Long expedientId, Long procesId) {

		try {
			var dades = dadaRepository.findByExpedientIdAndProcesId(expedientId, procesId);
			return dades.isPresent() ? dades.get() : new ArrayList<Dada>();
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.getDadesByProces] --->");
			e.printStackTrace();
			return new ArrayList<Dada>();
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
	public Dada getDadaByExpedientIdProcesAndCodi(Long expedientId, Long procesId, String codi) {
		try {
			var dada = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
			return dada.isPresent() ? dada.get() : null;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.getDadaByProcesAndCodi] --->");
			e.printStackTrace();
			return null;
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
	public Dada getDadaByProcesAndCodi(Long procesId, String codi) {
		try {
			var dada = dadaRepository.findByProcesIdAndCodi(procesId, codi);
			return dada.isPresent() ? dada.get() : null;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.getDadaByProcesAndCodi] --->");
			e.printStackTrace();
			return null;
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
	public boolean createDades(Long expedientId, Long procesId, List<Dada> dades) {

		log.debug("[SRV] Creant dades per l'expedient " + expedientId + " amb procesId " + procesId);
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
			return dadaRepository.saveAll(dadesFoo).size() > 0;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.createDades] --->");
			e.printStackTrace();
			return false;
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
	public boolean putDadaByExpedientIdAndCodi(Long expedientId, String codi, Dada dada) {

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
			return true;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.putDadaByExpedientIdAndCodi] --->");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Esborra la dada amb expedientId i codi
	 * @param expedientId identificador de l'expedient
	 * @param codi codi de l'expedient
	 * @Return True si la dada s'esborra.
	 */
	@Override
	public boolean deleteDadaByExpedientIdAndCodi(Long expedientId, String codi) {

		try {
			// TODO PREGUNTAR IS ÉS UN DELETE ALL PERQUÈ SINO AIXÒ POT RETORNAR MÉS D'UNA
			// DADA, O NO, DEPEN DE LES VALIDACIONS
			var dada = dadaRepository.findByExpedientIdAndCodi(expedientId, codi);
			if (dada.isEmpty()) {
				return false;
			}
			dadaRepository.delete(dada.get());
			return true;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.deleteDadaByExpedientIdAndCodi] --->");
			e.printStackTrace();
			return false;
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
	public boolean postDadesByExpedientIdProcesId(Long expedientId, Long procesId, List<Dada> dades) {
		
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
			return dadaRepository.saveAll(dadesMongo).size() > 0;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.postDadesByExpedientIdProcesId] --->");
			e.printStackTrace();
			return true;
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
	public boolean putDadaByExpedientIdProcesIdAndCodi(Long expedientId, Long procesId, String codi, Dada dada) {

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
			return true;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.putDadaByExpedientIdProcesIdAndCodi] --->");
			e.printStackTrace();
			return false;
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
	public boolean deleteDadaByExpedientIdAndProcesIdAndCodi(Long expedientId, Long procesId, String codi) {

		try {
			var dada = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
			if (dada.isEmpty()) {
				log.error("[ExpedientServiceImpl.deleteByExpedientIdAndCodi] No existeix la dada amb expedientId "
						+ expedientId + " i codi " + codi);
				return false;
			}
			dadaRepository.delete(dada.get());
			return true;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.deleteDadaByExpedientIdAndProcesIdAndCodi] --->");
			e.printStackTrace();
			return false;
		}
	}
}
