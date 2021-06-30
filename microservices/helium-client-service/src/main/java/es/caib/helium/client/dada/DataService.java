package es.caib.helium.client.dada;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.dada.model.Consulta;
import es.caib.helium.client.dada.model.Expedient;
import es.caib.helium.client.dada.model.PagedList;

@Service
public interface DataService {

	PagedList<Expedient> consultaResultats(
			Integer entornId, 
			Integer expedientTipusId, 
			Integer page, 
			Integer size, 
			Consulta body);
	
	List<Expedient> consultaResultatsLlistat(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId, 
			@RequestBody Consulta consulta);
	
	Expedient getExpedient(Long expedientId);
	
	void crearExpedient(Expedient expedient);

	void crearExpedients(List<Expedient> expedient);
	
    Long getExpedientIdByProcessInstanceId(String processInstanceId);
}
