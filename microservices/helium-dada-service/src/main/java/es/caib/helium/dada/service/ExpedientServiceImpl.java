package es.caib.helium.dada.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import es.caib.helium.dada.domain.Dada;
import es.caib.helium.dada.domain.DadaMS;
import es.caib.helium.dada.domain.ExpedientCapcalera;
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
	public PagedList<Dada> consultaResultats(int page, int size) {
		log.debug("[SRV] Consultant resultats");
		var pageable = PageRequest.of(page, size);	
		var pagina = dadaRepository.findAll(pageable); 
		var content = pagina.getContent();
		return new PagedList<Dada>(pagina.getContent(), pageable, pagina.getContent().size());
	}
	
	@Override
	public void createExpedient(ExpedientCapcalera expedient) {

		log.debug("[SRV] Creant dades capçalera expedient (expedient= " + expedient.toString());
		var exp = expedientRepository.findByExpedientId(expedient.getExpedientId());
		if (exp.isPresent()) {
			// TODO TRACTAMENT ERROR
			return;
		}
		expedientRepository.save(expedient);
	}

	@Override
	public ExpedientCapcalera findByExpedientId(Long expedientId) {

		log.debug("[SRV] Buscant les dades de capçalera de l'expedient (expedientId= " + expedientId);
		var expedient = expedientRepository.findByExpedientId(expedientId);
		if (expedient.isEmpty()) {
			log.error("[ExpedientServiceImpl.findById] Expedient amb id " + expedientId + " no trobat");
			return null; // TODO EXCEPTIONS ?¿¿
		}
		return expedient.get();
	}

	@Override
	public void deleteExpedient(Long expedientId) {

		log.debug("[SRV] Borrant dades capçalera expedient (expedientId= " + expedientId);
		expedientRepository.deleteByExpedientId(expedientId);
	}

	@Override
	public void putExpedient(Long expedientId, ExpedientCapcalera expedient) {

		var exp = findByExpedientId(expedientId);
		exp.setEntornId(expedient.getEntornId());
		exp.setTipusId(expedient.getTipusId());
		exp.setNumero(expedient.getNumero());
		exp.setTitol(expedient.getTitol());
		exp.setProcesPrincipalId(expedient.getProcesPrincipalId());
		exp.setEstatId(expedient.getEstatId());
		exp.setDataInici(expedient.getDataInici());
		exp.setDataFi(expedient.getDataFi());
		expedientRepository.save(exp);
	}

	@Override
	public void patchExpedient(Long expedientId, ExpedientCapcalera expedient) {

		var exp = findByExpedientId(expedientId);
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
			// TODO VALIDAR SI JA EXISTEIX?¿
			
			var d = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, dada.getCodi());
			if (d.isPresent()) {
				// Evitar dades amb el codi repetit
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
	public void putDadaByExpedientIdAndCodi(Long expedientId, String codi, Dada dada) {
		var dadaOptional = dadaRepository.findByExpedientIdAndCodi(expedientId, codi);
		if (dadaOptional.isEmpty()) {
			log.error("[ExpedientServiceImpl.putDadaByExpedientIdAndCodi] No existeix la dada amb expedientId  "
					+ expedientId + " i codi " + codi);
			return;
		}

		var dadaMongo = dadaOptional.get();
		dadaMongo.setMultiple(dada.isMultiple());
		dadaMongo.setTipus(dada.getTipus());
		dadaMongo.setValor(dada.getValor());
		dadaRepository.save(dadaMongo);
	}

	@Override
	public void deleteDadaByExpedientIdAndCodi(Long expedientId, String codi) {
		// TODO PREGUNTAR IS ÉS UN DELETE ALL PERQUÈ SINO AIXÒ POT RETORNAR MÉS D'UNA DADA, O NO, DEPEN DE LES VALIDACIONS
		var dada = dadaRepository.findByExpedientIdAndCodi(expedientId, codi);
		if (dada.isEmpty()) {
			log.error("[ExpedientServiceImpl.deleteByExpedientIdAndCodi] No existeix la dada amb expedientId "
					+ expedientId + " i codi " + codi);
			return;
		}
		dadaRepository.delete(dada.get());
	}

	@Override
	public void postDadesByExpedientIdProcesId(Long expedientId, Long procesId, List<Dada> dades) {

		var dadesMongo = new ArrayList<Dada>();
		for (var dada : dades) {
			if (dada.getCodi() == null || dada.getCodi().isEmpty()) { // TODO MIRAR QUE NO ESTIGUI EL CODI REPETIT A LA VARIABLE dades
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
	public void putDadaByExpedientIdProcesIdAndCodi(Long expedientId, Long procesId, String codi, Dada dada) {

		var dadaOptional = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
		if (dadaOptional.isEmpty()) {
			log.error("[ExpedientServiceImpl.deleteByExpedientIdAndCodi] No existeix la dada amb expedientId "
					+ expedientId + " i codi " + codi);
			return;
		}
		var dadaMongo = dadaOptional.get();
		dadaMongo.setMultiple(dada.isMultiple());
		dadaMongo.setTipus(dada.getTipus());
		dadaMongo.setValor(dada.getValor());
		dadaRepository.save(dadaMongo);
	}

	@Override
	public void deleteDadaByExpedientIdAndProcesIdAndCodi(Long expedientId, Long procesId, String codi) {
		
		var dada = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
		if (dada.isEmpty()) {
			log.error("[ExpedientServiceImpl.deleteByExpedientIdAndCodi] No existeix la dada amb expedientId "
					+ expedientId + " i codi " + codi);
			return;
		}
		dadaRepository.delete(dada.get());
	}
}
