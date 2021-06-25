package es.caib.helium.monitor.repository;

import java.util.List;

import es.caib.helium.jms.events.IntegracioEvent;
import es.caib.helium.monitor.domini.Consulta;

public interface IntegracioEventRepositoryCustom {

	List<IntegracioEvent> findByFiltres(Consulta consulta) throws Exception;
}
