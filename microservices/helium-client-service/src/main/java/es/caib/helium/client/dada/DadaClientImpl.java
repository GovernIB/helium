package es.caib.helium.client.dada;

import es.caib.helium.client.dada.model.Consulta;
import es.caib.helium.client.dada.model.Dada;
import es.caib.helium.client.dada.model.Expedient;
import es.caib.helium.client.dada.model.ValidList;
import es.caib.helium.client.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
//@Profile(value = {"spring-cloud", "compose"})
public class DadaClientImpl implements DadaClient {

    private final DadaServiceFeignClient dadaServiceFeignClient;
    
    private final String missatgeLog = "Cridant Data Service - ";
    
    // Consultes filtrades
    
	@Override
    public PagedList<Expedient> consultaResultats(
			Integer entornId, 
			Integer expedientTipusId, 
			Integer page, 
			Integer size, 
			Consulta consulta) throws RuntimeException {
    	
	 	log.debug(missatgeLog + " Consulta paginada - entornId: " + entornId 
	 			+ " - expedientTipusId: " + expedientTipusId + " page: " + page + " size: " + size 
	 			+ " consulta: " + consulta.toString());
	 	try {
			var response = dadaServiceFeignClient.consultaResultatsPaginats(entornId, expedientTipusId, page, size, consulta);
			return Objects.requireNonNull(response.getBody());
    	} catch (Exception ex) {
			throw new RuntimeException("DadaClient.consultaResultats -> " +
					"Error inesperat entornId " + entornId + " expedientTipusId " + expedientTipusId
					+ " consulta " + consulta, ex);
		}
	}
 
	@Override
	public List<Expedient> consultaResultatsLlistat(
			Integer entornId,
			Integer expedientTipusId, 
			Consulta consulta) throws RuntimeException {
    	
		log.debug(missatgeLog + " Consulta paginada - entornId: " + entornId 
	 			+ " - expedientTipusId: " + expedientTipusId 
	 			+ " consulta: " + consulta.toString());
		try {
			return Objects.requireNonNull(dadaServiceFeignClient.consultaResultatsLlistat(entornId, expedientTipusId, consulta).getBody());
    	} catch (Exception ex) {
			throw new RuntimeException("DadaClient.consultaResultatsLlistat -> " +
					"INTERNAL_SERVER_ERROR entornId " + entornId + " expedientTipusId " + expedientTipusId
					+ " consulta " + consulta, ex);
		}
	}

	// Dades capcalera expedient
	
	@Override
	public Expedient findByExpedientId(Long expedientId) throws RuntimeException {
		
		log.debug(missatgeLog + "expedientId: " + expedientId);
		try {
			return Objects.requireNonNull(dadaServiceFeignClient.getExpedient(expedientId).getBody());
		} catch (Exception ex) {
			throw new RuntimeException("DadaClient.getDadaExpedientIdByProcesId -> " +
					"INTERNAL_SERVER_ERROR expedient " + expedientId, ex);
		}
	}

	@Override
	public void crearExpedient(Expedient expedient) {

		log.debug(missatgeLog + " creant expedient: " + expedient.toString());
		dadaServiceFeignClient.createExpedient(expedient);
	}
	
	@Override
    public void crearExpedients(List<Expedient> expedients) {

		log.debug(missatgeLog + " creant expedients: " + expedients.toString());
		var valid = new ValidList<Expedient>();
		valid.setList(expedients);
		dadaServiceFeignClient.createExpedients(valid);
    }
	

	@Override
	public void deleteExpedient(Long expedientId) {

		log.debug(missatgeLog + " esborrant expedient: " + expedientId);
		dadaServiceFeignClient.deleteExpedient(expedientId);
	}

	@Override
	public void deleteExpedients(List<Long> expedients) {
		
		log.debug(missatgeLog + " esborrant expedients: " + expedients.toString());
		dadaServiceFeignClient.deleteExpedients(expedients);
	}

	@Override
	public void putExpedient(Expedient expedient, Long expedientId) {
		
		log.debug(missatgeLog + " put expedient: " + expedientId + " " + expedient.toString());
		dadaServiceFeignClient.putExpedient(expedient, expedientId);
	}

	@Override
	public void putExpedients(List<Expedient> expedients) {
		
		var valid = new ValidList<Expedient>();
		valid.setList(expedients);
		log.debug(missatgeLog + " put expedients: " + expedients.toString());
		dadaServiceFeignClient.putExpedients(valid);
	}

	@Override
	public void patchExpedient(Expedient expedient, Long expedientId) {

		log.debug(missatgeLog + " patch expedient: " + expedientId + " " + expedient.toString());
		dadaServiceFeignClient.patchExpedient(expedient, expedientId);
	}

	@Override
	public void patchExpedients(List<Expedient> expedients) {

		var valid = new ValidList<Expedient>();
		valid.setList(expedients);
		log.debug(missatgeLog + " patch expedients: " + expedients.toString());
		dadaServiceFeignClient.patchExpedients(valid);
	}
	
	// Dades de l'expedient

	@Override
	public List<Dada> getDades(Long expedientId) throws RuntimeException {

		log.debug(missatgeLog + " Get dades per l'expedient: " + expedientId);
		try {
			var response = dadaServiceFeignClient.getDades(expedientId);
			if (response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
				return new ArrayList<>();
			}
			return Objects.requireNonNull(response.getBody());
		} catch (Exception ex) {
			throw new RuntimeException("DadaClient.getDades -> " +
					"INTERNAL_SERVER_ERROR expedient " + expedientId, ex);
		}
	}

	@Override
	public Dada getDadaByCodi(Long expedientId, String codi) throws RuntimeException {
		
		log.debug(missatgeLog + " Get dada per l'expedient: " + expedientId + " amb codi: " + codi);
		try {
			return Objects.requireNonNull(dadaServiceFeignClient.getDadaByCodi(expedientId, codi).getBody());
		} catch (Exception ex) {
			throw new RuntimeException("DadaClient.getDadaByCodi -> " +
					"INTERNAL_SERVER_ERROR codi " + codi + " expedient " + expedientId, ex);
		}
	}

	@Override
	public List<Dada> getDadesByProces(Long expedientId, String procesId) throws RuntimeException {

		log.debug(missatgeLog + " Get dades per l'expedient: " + expedientId + " amb procesId: " + procesId);
		try {
			var response = dadaServiceFeignClient.getDadesByProces(expedientId, procesId);
			if (response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
				return new ArrayList<>();
			}
			return Objects.requireNonNull(response.getBody());
		} catch (Exception ex) {
			throw new RuntimeException("DadaClient.getDadesByProces -> " +
					"INTERNAL_SERVER_ERROR procesId " + procesId + " expedient " + expedientId, ex);
		}
	}

	@Override
	public Dada getDadaByExpedientIdProcesAndCodi(Long expedientId, String procesId, String codi) throws RuntimeException {
		
		log.debug(missatgeLog + " Get dades per l'expedient: " + expedientId + " amb procesId: " + procesId + " i codi: " + codi);
	 	try {
			return Objects.requireNonNull(dadaServiceFeignClient.getDadaByExpedientIdProcesAndCodi(expedientId, procesId, codi).getBody());
		} catch (Exception ex) {
			throw new RuntimeException("DadaClient.getDadaByExpedientIdProcesAndCodi -> " +
					"INTERNAL_SERVER_ERROR procesId " + procesId + " expedient " + expedientId, ex);
		}
	}

	@Override
	public Dada getDadaByProcesAndCodi(String procesId, String codi) throws RuntimeException {
		
		log.debug(missatgeLog + " Get dada amb procesId: " + procesId + " i codi: " + codi);
		try {
			return Objects.requireNonNull(dadaServiceFeignClient.getDadaByProcesAndCodi(procesId, codi).getBody());
		} catch (Exception ex) {
			throw new RuntimeException("DadaClient.getDadaByProcesAndCodi -> " +
					"INTERNAL_SERVER_ERROR procesId " + procesId + " codi " + codi, ex);
		}
	}

	@Override
	public Long getDadaExpedientIdByProcesId(String procesId) throws RuntimeException {
	
		log.debug(missatgeLog + " Get expedientId de la dada amb procesId: " + procesId);
		try {
			var response = dadaServiceFeignClient.getDadaExpedientIdByProcesId(procesId);
			return Objects.requireNonNull(response.getBody());
		} catch (Exception ex) {
			throw new RuntimeException("DadaClient.getDadaExpedientIdByProcesId -> " +
					"INTERNAL_SERVER_ERROR procesId " + procesId, ex);
		}
	}

	@Override
	public void postDadesByExpedientId(Long expedientId, String procesId, List<Dada> dada) {
		
		log.debug(missatgeLog + " Post dades per l'expedient " + expedientId);
		var valid = new ValidList<Dada>();
		valid.setList(dada);
		dadaServiceFeignClient.postDadesByExpedientId(expedientId, procesId, valid);
	}

	@Override
	public void putDadaByExpedientIdAndCodi(Long expedientId, String codi, Dada dada) {
		
		log.debug(missatgeLog + " Put dada per l'expedient " + expedientId + " amb codi: " + codi);
		dadaServiceFeignClient.putDadaByExpedientIdAndCodi(expedientId, codi, dada);
	}

	@Override
	public void deleteDadaByExpedientIdAndCodi(Long expedientId, String codi) {

		log.debug(missatgeLog + " Delete dades per l'expedient " + expedientId + " amb codi: " + codi);
		dadaServiceFeignClient.deleteDadaByExpedientIdAndCodi(expedientId, codi);
	}

	@Override
	public void postDadesByExpedientIdProcesId(Long expedientId, String procesId, List<Dada> dades) {

		log.debug(missatgeLog + " Post dades per l'expedient " + expedientId);
		var valid = new ValidList<Dada>();
		valid.setList(dades);
		dadaServiceFeignClient.postDadaByExpedientIdProcesId(expedientId, procesId, valid);
	}

	@Override
	public void putDadaByExpedientIdProcesIdAndCodi(Long expedientId, String procesId, String codi, Dada dada) {

		log.debug(missatgeLog + " Put dada per l'expedient " + expedientId + " amb procesId: " + procesId + " i codi: " + codi);
		dadaServiceFeignClient.putDadaByExpedientIdProcesIdAndCodi(expedientId, procesId, codi, dada);
	}

	@Override
	public void deleteDadaByExpedientIdAndProcesIdAndCodi(Long expedientId, String procesId, String codi) {
		
		log.debug(missatgeLog + " Delete dada per l'expedient " + expedientId + " amb procesId: " + procesId + " i codi: " + codi);
		dadaServiceFeignClient.deleteDadaByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
	}
}
