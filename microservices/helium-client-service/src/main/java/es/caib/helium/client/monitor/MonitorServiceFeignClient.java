package es.caib.helium.client.monitor;

import java.util.List;

import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.helium.client.model.PagedList;
import es.caib.helium.client.monitor.model.Consulta;
import es.caib.helium.jms.events.IntegracioEvent;

public interface MonitorServiceFeignClient {

	@RequestMapping(method = RequestMethod.GET, value = MonitorMsApiPath.GET_EVENTS)
	public ResponseEntity<List<IntegracioEvent>> getEvents(@SpringQueryMap Consulta consulta);
	
	
	//TODO peta al deserielitzar
	@RequestMapping(method = RequestMethod.GET, value = MonitorMsApiPath.GET_EVENTS)
	public ResponseEntity<PagedList<IntegracioEvent>> getEventsPaginats(@SpringQueryMap Consulta consulta);
}
