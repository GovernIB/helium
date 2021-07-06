package es.caib.helium.client.dada;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import es.caib.helium.client.dada.model.Consulta;
import es.caib.helium.client.dada.model.Dada;
import es.caib.helium.client.dada.model.Expedient;
import es.caib.helium.client.dada.model.ValidList;
import es.caib.helium.client.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
//@Profile(value = {"spring-cloud", "compose"})
public class DataServiceImpl implements DataService {

    private final DataServiceFeignClient dadaServiceFeignClient;
    
    private final String missatgeLog = "Cridant Data Service - ";
    
    // Consultes filtrades
    
	@Override
    public PagedList<Expedient> consultaResultats(
			Integer entornId, 
			Integer expedientTipusId, 
			Integer page, 
			Integer size, 
			Consulta consulta) {
    	
	 	log.debug(missatgeLog + " Consulta paginada - entornId: " + entornId 
	 			+ " - expedientTipusId: " + expedientTipusId + " page: " + page + " size: " + size 
	 			+ " consulta: " + consulta.toString());
	 	
	 	var responseEntity = dadaServiceFeignClient.consultaResultatsPaginats(entornId, expedientTipusId, page, size, consulta);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
    }
 
	@Override
	public List<Expedient> consultaResultatsLlistat(
			Integer entornId,
			Integer expedientTipusId, 
			Consulta consulta) { 
    	
		log.debug(missatgeLog + " Consulta paginada - entornId: " + entornId 
	 			+ " - expedientTipusId: " + expedientTipusId 
	 			+ " consulta: " + consulta.toString());
	 	
	 	var responseEntity = dadaServiceFeignClient.consultaResultatsLlistat(entornId, expedientTipusId, consulta);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
    }

	// Dades capcalera expedient
	
	@Override
	public Expedient findByExpedientId(Long expedientId) {
		
		log.debug(missatgeLog + "expedientId: " + expedientId);
		var responseEntity = dadaServiceFeignClient.getExpedient(expedientId);
		var expedient = Objects.requireNonNull(responseEntity.getBody());
		return expedient;
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
	public List<Dada> getDades(Long expedientId) {
		
		log.debug(missatgeLog + " Get dades per l'expedient: " + expedientId);
	 	var responseEntity = dadaServiceFeignClient.getDades(expedientId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public Dada getDadaByCodi(Long expedientId, String codi) {
		
		log.debug(missatgeLog + " Get dada per l'expedient: " + expedientId + " amb codi: " + codi);
	 	var responseEntity = dadaServiceFeignClient.getDadaByCodi(expedientId, codi);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<Dada> getDadesByProces(Long expedientId, Long procesId) {
		
		log.debug(missatgeLog + " Get dades per l'expedient: " + expedientId + " amb procesId: " + procesId);
	 	var responseEntity = dadaServiceFeignClient.getDadesByProces(expedientId, procesId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public Dada getDadaByExpedientIdProcesAndCodi(Long expedientId, Long procesId, String codi) {
		
		log.debug(missatgeLog + " Get dades per l'expedient: " + expedientId + " amb procesId: " + procesId + " i codi: " + codi);
	 	var responseEntity = dadaServiceFeignClient.getDadaByExpedientIdProcesAndCodi(expedientId, procesId, codi);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public Dada getDadaByProcesAndCodi(Long procesId, String codi) {
		
		log.debug(missatgeLog + " Get dada amb procesId: " + procesId + " i codi: " + codi);
	 	var responseEntity = dadaServiceFeignClient.getDadaByProcesAndCodi(procesId, codi);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public Long getDadaExpedientIdByProcesId(Long procesId) {
	
		log.debug(missatgeLog + " Get expedientId de la dada amb procesId: " + procesId);
	 	var responseEntity = dadaServiceFeignClient.getDadaExpedientIdByProcesId(procesId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void postDadesByExpedientId(Long expedientId, Long procesId, List<Dada> dada) {
		
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
	public void postDadesByExpedientIdProcesId(Long expedientId, Long procesId, List<Dada> dades) {

		log.debug(missatgeLog + " Post dades per l'expedient " + expedientId);
		var valid = new ValidList<Dada>();
		valid.setList(dades);
		dadaServiceFeignClient.postDadaByExpedientIdProcesId(expedientId, procesId, valid);
	}

	@Override
	public void putDadaByExpedientIdProcesIdAndCodi(Long expedientId, Long procesId, String codi, Dada dada) {

		log.debug(missatgeLog + " Put dada per l'expedient " + expedientId + " amb procesId: " + procesId + " i codi: " + codi);
		dadaServiceFeignClient.putDadaByExpedientIdProcesIdAndCodi(expedientId, procesId, codi, dada);
	}

	@Override
	public void deleteDadaByExpedientIdAndProcesIdAndCodi(Long expedientId, Long procesId, String codi) {
		
		log.debug(missatgeLog + " Delete dada per l'expedient " + expedientId + " amb procesId: " + procesId + " i codi: " + codi);
		dadaServiceFeignClient.deleteDadaByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
	}
}
