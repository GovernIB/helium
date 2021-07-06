package es.caib.helium.client.monitor;

import java.util.List;
import java.util.Objects;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import es.caib.helium.client.model.PagedList;
import es.caib.helium.client.monitor.model.Consulta;
import es.caib.helium.jms.events.IntegracioEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile(value = {"!spring-cloud & !compose"})
public class MonitorServiceImpl implements MonitorService {
	
	private final String missatgeLog = "Cridant Monitor Service - ";
	
	private final MonitorServiceFeignClient monitorService;
	
	@Override
	public List<IntegracioEvent> getEvents(Consulta consulta) throws Exception {
		
		log.debug(missatgeLog + " consultant events " + consulta.toString());
		var responseEntity = monitorService.getEvents(consulta);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public PagedList<IntegracioEvent> getEventsPaginats(Consulta consulta) throws Exception {
		
		log.debug(missatgeLog + " consultant events paginats " + consulta.toString());
		var responseEntity = monitorService.getEventsPaginats(consulta);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

}
