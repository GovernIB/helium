package es.caib.helium.dada.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import es.caib.helium.dada.domain.Dada;
import es.caib.helium.dada.domain.Expedient;
import es.caib.helium.dada.model.Filtre;
import es.caib.helium.dada.model.FiltreCapcalera;
import es.caib.helium.dada.model.Ordre;
import es.caib.helium.dada.model.PagedList;
import es.caib.helium.dada.repository.DadaRepository;
import es.caib.helium.dada.repository.ExpedientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "es.caib.helium") // , ignoreUnknownFields = true)
@Service
public class ExpedientServiceImpl implements ExpedientService {

	private final ExpedientRepository expedientRepository;
	private final DadaRepository dadaRepository;

	// Dades capçalera expedient

	@Override
	public PagedList<Expedient> consultaResultats(Map<String, Filtre> filtreValors, Integer entornId,
			Integer expedientTipusId, List<Ordre> ordre, Integer page, Integer size) {

		log.debug("[SRV] Consultant resultats paginats");
		var pagina = expedientRepository.findByFiltres(filtreValors, entornId, expedientTipusId, ordre, page, size);
		var pageable = PageRequest.of(page, pagina.size() > 0 ? pagina.size() : 1);
		return new PagedList<Expedient>(pagina, pageable, pagina.size());
	}

	@Override
	public List<Expedient> consultaResultats(Map<String, Filtre> filtreValors, Integer entornId,
			Integer expedientTipusId, List<Ordre> ordre) {

		log.debug("[SRV] Consultant resultats");
		return expedientRepository.findByFiltres(filtreValors, entornId, expedientTipusId, ordre, null, null);
	}

	@Override
	public void createExpedient(Expedient expedient) {

		log.debug("[SRV] Creant dades capçalera expedient (expedient= " + expedient.toString());
		var exp = expedientRepository.findByExpedientId(expedient.getExpedientId());
		if (exp.isPresent()) {
			// TODO TRACTAMENT ERROR
			return;
		}
		expedientRepository.save(expedient);
	}

	@Override
	public void createExpedients(List<Expedient> expedients) {

		log.debug("[SRV] Creant dades capçalera expedient per múltiples expedients");
		List<Expedient> exps = new ArrayList<>();
		for (var expedient : expedients) {
			if (expedientRepository.findByExpedientId(expedient.getExpedientId()).isPresent()) {
				log.debug(
						"[SRV] Expedient (expedientId=" + expedient.getExpedientId() + " ja existeix, no es guardarà");
				continue;
			}
			exps.add(expedient);
		}
		expedientRepository.saveAll(exps);
	}

	@Override
	public Expedient findByExpedientId(Long expedientId) {

		log.debug("[SRV] Buscant les dades de capçalera de l'expedient (expedientId= " + expedientId);
		var expedient = expedientRepository.findByExpedientId(expedientId);
		if (expedient.isEmpty()) {
			log.error("[ExpedientServiceImpl.findById] Expedient amb id " + expedientId + " no trobat");
			return null; // TODO EXCEPTIONS ?¿¿
		}
		return expedient.get();
	}

	@Override
	public boolean deleteExpedient(Long expedientId) {

		log.debug("[SRV] Borrant dades capçalera expedient (expedientId= " + expedientId);
		var expedient = findByExpedientId(expedientId);
		if (expedient == null) {
			return false; // Aixi es pot retornar el 404 al Controller
		}
		expedientRepository.deleteByExpedientId(expedientId);
		//TODO falta esborrar els documents de la coleccio dades que fan referencia a aquest expedient. 
		return true;
	}

	@Override
	public void deleteExpedients(List<Long> expedients) {

		log.debug("[SRV] Borrant dades capçalera múltiples expedients");
		var exps = expedientRepository.findByExpedients(expedients);
		if (exps.isPresent()) {
			expedientRepository.deleteAll(exps.get());
			//TODO falta esborrar els documents de la coleccio dades que fan referencia a aquests expedient. 
		}
	}

	@Override
	public boolean putExpedient(Long expedientId, Expedient expedient) {

		// TODO CANVIAR LES CRIDES QUE FALATEN AL REPOSITORI EN COMPTES DE LA FUNCIÓ
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
	}

	@Override
	public void putExpedients(List<Expedient> expedients) {

		List<Expedient> exps = new ArrayList<>();
		for (var expedient : expedients) {
			var exp = findByExpedientId(expedient.getExpedientId());
			if (exp != null) {
				exp.setEntornId(expedient.getEntornId());
				exp.setTipusId(expedient.getTipusId());
				exp.setNumero(expedient.getNumero());
				exp.setTitol(expedient.getTitol());
				exp.setProcesPrincipalId(expedient.getProcesPrincipalId());
				exp.setEstatId(expedient.getEstatId());
				exp.setDataInici(expedient.getDataInici());
				exp.setDataFi(expedient.getDataFi());
				exps.add(exp);
			}
		}
		expedientRepository.saveAll(exps);
	}

	@Override
	public boolean patchExpedient(Long expedientId, Expedient expedient) {

		var exp = findByExpedientId(expedientId);
		if (exp == null) {
			return false;
		}
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
		expedientRepository.save(exp);
		return true;
	}

	@Override
	public void patchExpedients(List<Expedient> expedients) {

		List<Expedient> exps = new ArrayList<>();
		for (var expedient : expedients) {
			var exp = findByExpedientId(expedient.getExpedientId());
			if (exp != null) {
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
		}
		expedientRepository.saveAll(exps);
	}

	// Dades expedient

	@Override
	public List<Dada> getDades(Long expedientId) {

		var dades = dadaRepository.findByExpedientId(expedientId);
		return dades.isPresent() ? dades.get() : new ArrayList<Dada>();
	}

	@Override
	public Dada getDadaByCodi(Long expedientId, String codi) {

		var dades = dadaRepository.findByExpedientIdAndCodi(expedientId, codi);
		return dades.isPresent() ? dades.get() : null; // TODO POT TORNAR NULL?
	}

	@Override
	public List<Dada> getDadesByProces(Long expedientId, Long procesId) {

		var dades = dadaRepository.findByExpedientIdAndProcesId(expedientId, procesId);
		return dades.isPresent() ? dades.get() : new ArrayList<Dada>();
	}

	@Override
	public Dada getDadaByProcesAndCodi(Long expedientId, Long procesId, String codi) {

		var dada = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
		return dada.isPresent() ? dada.get() : null; // TODO POT TORNAR NULL?
	}

	@Override
	public void createDades(Long expedientId, Long procesId, List<Dada> dades) {

		log.debug("[SRV] Creant dades per l'expedient " + expedientId + " amb procesId " + procesId);

		for (var dada : dades) {

			var d = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, dada.getCodi());
			if (d.isPresent()) {
				// Evitar dades amb el codi repetit segons proces i codi
				log.error("[ExpedientServiceImpl.crearDada] Expedient amb id " + expedientId + " procesId " + procesId
						+ " ja té una dada amb codi " + dada.getCodi());
				continue;
			}
			dada.setExpedientId(expedientId);
			dada.setProcesId(procesId);
		}
		dadaRepository.saveAll(dades);
	}

	@Override
	public boolean putDadaByExpedientIdAndCodi(Long expedientId, String codi, Dada dada) {
		
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
	}

	@Override
	public boolean deleteDadaByExpedientIdAndCodi(Long expedientId, String codi) {
		// TODO PREGUNTAR IS ÉS UN DELETE ALL PERQUÈ SINO AIXÒ POT RETORNAR MÉS D'UNA
		// DADA, O NO, DEPEN DE LES VALIDACIONS
		var dada = dadaRepository.findByExpedientIdAndCodi(expedientId, codi);
		if (dada.isEmpty()) {
			return false;
		}
		dadaRepository.delete(dada.get());
		return true;
	}

	@Override
	public void postDadesByExpedientIdProcesId(Long expedientId, Long procesId, List<Dada> dades) {

		var dadesMongo = new ArrayList<Dada>();
		for (var dada : dades) {
			if (dada.getCodi() == null || dada.getCodi().isEmpty()) { // TODO MIRAR QUE NO ESTIGUI EL CODI REPETIT A LA
																		// VARIABLE dades
				continue;
			}
			var dadaMongo = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, dada.getCodi());
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
		dadaRepository.saveAll(dadesMongo);
	}

	@Override
	public boolean putDadaByExpedientIdProcesIdAndCodi(Long expedientId, Long procesId, String codi, Dada dada) {

		var dadaOptional = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
		if (dadaOptional.isEmpty()) {
			return false;
		}
		var dadaMongo = dadaOptional.get();
		dadaMongo.setMultiple(dada.isMultiple());
		dadaMongo.setTipus(dada.getTipus());
		dadaMongo.setValor(dada.getValor());
		dadaRepository.save(dadaMongo);
		return true;
	}

	@Override
	public boolean deleteDadaByExpedientIdAndProcesIdAndCodi(Long expedientId, Long procesId, String codi) {

		var dada = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
		if (dada.isEmpty()) {
			log.error("[ExpedientServiceImpl.deleteByExpedientIdAndCodi] No existeix la dada amb expedientId "
					+ expedientId + " i codi " + codi);
			return false;
		}
		dadaRepository.delete(dada.get());
		return true;
	}
}
