package es.caib.helium.dada.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import es.caib.helium.dada.domain.Dada;
import es.caib.helium.dada.domain.Expedient;
import es.caib.helium.dada.model.Consulta;
import es.caib.helium.dada.model.Filtre;
import es.caib.helium.dada.model.FiltreCapcalera;
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

	@Override
	public void createExpedient(Expedient expedient) {

		try {
			log.debug("[SRV] Creant dades capçalera expedient (expedient= " + expedient.toString());
			var exp = expedientRepository.findByExpedientId(expedient.getExpedientId());
			if (exp.isPresent()) {
				return;
			}
			expedientRepository.save(expedient);
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.createExpedient] --->");
			e.printStackTrace();
		}
	}

	@Override
	public void createExpedients(List<Expedient> expedients) {

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
			expedientRepository.saveAll(exps);
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.createExpedients] --->");
			e.printStackTrace();
		}
	}

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

	@Override
	public boolean deleteExpedient(Long expedientId) {

		try {
			log.debug("[SRV] Borrant dades capçalera expedient (expedientId= " + expedientId);
			var expedient = expedientRepository.findByExpedientId(expedientId);
			if (expedient.isEmpty()) {
				return false; // Així es pot retornar el 404 al Controller
			}
			expedientRepository.esborrarExpedientCascade(expedientId);
			return true;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.deleteExpedient] --->");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteExpedients(List<Long> expedients) {

		try {
			log.debug("[SRV] Borrant dades capçalera múltiples expedients");
			if (expedients.isEmpty()) {
				return false;
			}
			expedientRepository.esborrarExpedientsCascade(expedients);
			return true;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.deleteExpedients] --->");
			e.printStackTrace();
			return false;
		}
	}

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

	@Override
	public void putExpedients(List<Expedient> expedients) {
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
			expedientRepository.saveAll(exps);
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.putExpedients] --->");
			e.printStackTrace();
		}
	}

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
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.patchExpedient] --->");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void patchExpedients(List<Expedient> expedients) {
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
			expedientRepository.saveAll(exps);
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.patchExpedients] --->");
			e.printStackTrace();
		}
	}

	// Dades expedient

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

	@Override
	public Dada getDadaByProcesAndCodi(Long expedientId, Long procesId, String codi) {
		try {
			var dada = dadaRepository.findByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
			return dada.isPresent() ? dada.get() : null;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.getDadaByProcesAndCodi] --->");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void createDades(Long expedientId, Long procesId, List<Dada> dades) {

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
			dadaRepository.saveAll(dadesFoo);
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.createDades] --->");
			e.printStackTrace();
		}
	}

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

	@Override
	public void postDadesByExpedientIdProcesId(Long expedientId, Long procesId, List<Dada> dades) {
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
			dadaRepository.saveAll(dadesMongo);
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.postDadesByExpedientIdProcesId] --->");
			e.printStackTrace();
		}
	}

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
			dadaRepository.save(dadaMongo);
			return true;
		} catch (Exception e) {
			log.error("[ExpedientServiceImpl.putDadaByExpedientIdProcesIdAndCodi] --->");
			e.printStackTrace();
			return false;
		}
	}

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
