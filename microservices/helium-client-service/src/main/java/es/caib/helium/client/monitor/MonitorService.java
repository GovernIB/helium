package es.caib.helium.client.monitor;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.client.model.PagedList;
import es.caib.helium.client.monitor.model.Consulta;
import es.caib.helium.jms.events.IntegracioEvent;

@Service
public interface MonitorService {

	public List<IntegracioEvent> getEvents(Consulta consulta) throws Exception;
	
	public PagedList<IntegracioEvent> getEventsPaginats(Consulta consulta) throws Exception;
}
