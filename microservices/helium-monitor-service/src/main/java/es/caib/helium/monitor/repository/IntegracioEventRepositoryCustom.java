package es.caib.helium.monitor.repository;

import java.util.List;

import es.caib.helium.monitor.domini.Consulta;
import es.caib.helium.monitor.domini.IntegracioEvent;

public interface IntegracioEventRepositoryCustom {

	List<IntegracioEvent> findByFiltres(Consulta consulta) throws Exception;
}
