package es.caib.helium.client.dada.dades;

import es.caib.helium.client.dada.dades.model.Consulta;
import es.caib.helium.client.dada.dades.model.Dada;
import es.caib.helium.client.dada.dades.model.Expedient;
import es.caib.helium.client.dada.dades.model.ValidList;
import es.caib.helium.client.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
//@Profile(value = {"spring-cloud", "compose"})
public class DadaClientImpl implements DadaClient {

    private final DadaFeignClient dadaServiceFeignClient;
    
    private final String missatgeLog = "Cridant Data Service - ";
    
    // Consultes filtrades
    
	@Override
    public PagedList<Expedient> consultaResultats(
			Integer entornId, 
			Integer expedientTipusId, 
			Integer page, 
			Integer size, 
			Consulta consulta) throws Exception {
    	
	 	log.debug(missatgeLog + " Consulta paginada - entornId: " + entornId 
	 			+ " - expedientTipusId: " + expedientTipusId + " page: " + page + " size: " + size 
	 			+ " consulta: " + consulta.toString());

		return dadaServiceFeignClient.consultaResultatsPaginats(entornId, expedientTipusId, page, size, consulta).getBody();
	}

	@Override
	public List<Expedient> consultaResultatsLlistat(
			Integer entornId,
			Integer expedientTipusId, 
			Consulta consulta) throws Exception {
    	
		log.debug(missatgeLog + " Consulta paginada - entornId: " + entornId 
	 			+ " - expedientTipusId: " + expedientTipusId 
	 			+ " consulta: " + consulta.toString());
		return dadaServiceFeignClient.consultaResultatsLlistat(entornId, expedientTipusId, consulta).getBody();
	}

	// Dades capcalera expedient
	
	@Override
	public Expedient findByExpedientId(Long expedientId) throws Exception {
		
		log.debug(missatgeLog + "expedientId: " + expedientId);
		return dadaServiceFeignClient.getExpedient(expedientId).getBody();
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
	public List<Dada> getDades(Long expedientId) throws Exception {

		log.debug(missatgeLog + " Get dades per l'expedient: " + expedientId);
		var response = dadaServiceFeignClient.getDades(expedientId);
		if (response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
			return new ArrayList<>();
		}
		return response.getBody();
	}

	@Override
	public Dada getDadaByCodi(Long expedientId, String codi) throws Exception {
		
		log.debug(missatgeLog + " Get dada per l'expedient: " + expedientId + " amb codi: " + codi);
		return dadaServiceFeignClient.getDadaByCodi(expedientId, codi).getBody();

	}

	@Override
	public List<Dada> getDadesByExpedientIdProcesId(Long expedientId, String procesId) throws Exception {

		log.debug(missatgeLog + " Get dades per l'expedient: " + expedientId + " amb procesId: " + procesId);
		var response = dadaServiceFeignClient.getDadesByExpedientIdProcesId(expedientId, procesId);
		if (response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
			return new ArrayList<>();
		}
		return response.getBody();
	}

	@Override
	public Dada getDadaByExpedientIdProcesAndCodi(Long expedientId, String procesId, String codi) throws Exception {
		
		log.debug(missatgeLog + " Get dades per l'expedient: " + expedientId + " amb procesId: " + procesId + " i codi: " + codi);
	 	return dadaServiceFeignClient.getDadaByExpedientIdProcesAndCodi(expedientId, procesId, codi).getBody();
	}

	@Override
	public Dada getDadaByProcesAndCodi(String procesId, String codi) {
		
		log.debug(missatgeLog + " Get dada amb procesId: " + procesId + " i codi: " + codi);
		return dadaServiceFeignClient.getDadaByProcesAndCodi(procesId, codi).getBody();
	}

	@Override
	public Long getDadaExpedientIdByProcesId(String procesId) {
	
		log.debug(missatgeLog + " Get expedientId de la dada amb procesId: " + procesId);
		return dadaServiceFeignClient.getDadaExpedientIdByProcesId(procesId).getBody();
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

	@Override
	public List<Dada> getDadesByProcessInstanceId(String procesId) {

		log.debug(missatgeLog + "Get dada pers procesId: " + procesId);
		return dadaServiceFeignClient.getDadesByProcessInstanceId(procesId).getBody();
	}

	@Override
	public Boolean postDadaByProcesId(String procesId, List<Dada> dades) {

		log.debug(missatgeLog + "Get dada per procesId: " + procesId + " dades: " + dades);
		return dadaServiceFeignClient.postDadaByProcesId(procesId, dades).getBody();
	}

	@Override
	public void deleteDadaByProcessInstanceIdAndCodi(String procesId, String codi) {

		log.debug(missatgeLog + " Delete dada per procesId: " + procesId + " codi: " + codi);
		dadaServiceFeignClient.deleteDadaByProcessInstanceIdAndCodi(procesId, codi);
	}

	@Override
	public List<Expedient> findRootProcessInstances(List<String> procesIds) {

		log.debug(missatgeLog + " Get root proces instances by procesIds: " + procesIds);
		return dadaServiceFeignClient.findRootProcessInstances(procesIds).getBody();
	}

	@Override
	public Expedient findRootProcessInstances(String procesId) {

		log.debug(missatgeLog + " Get root proces instances by procesId: " + procesId);
		return dadaServiceFeignClient.findRootProcessInstances(procesId).getBody();
	}
}
