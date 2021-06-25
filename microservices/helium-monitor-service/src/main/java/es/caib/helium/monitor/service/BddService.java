package es.caib.helium.monitor.service;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.jms.events.IntegracioEvent;
import es.caib.helium.monitor.domini.Consulta;
import es.caib.helium.monitor.domini.PagedList;
import es.caib.helium.monitor.exception.MonitorIntegracionsException;

@Service
public interface BddService {

	public IntegracioEvent save(IntegracioEvent accio) throws MonitorIntegracionsException;
	public PagedList<IntegracioEvent> findByFiltresPaginat(Consulta consulta) throws MonitorIntegracionsException;
	public List<IntegracioEvent> findByFiltres(Consulta consulta) throws MonitorIntegracionsException;
}
