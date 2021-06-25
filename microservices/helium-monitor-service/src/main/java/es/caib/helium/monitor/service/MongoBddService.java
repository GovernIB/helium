package es.caib.helium.monitor.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import es.caib.helium.jms.events.IntegracioEvent;
import es.caib.helium.monitor.domini.Consulta;
import es.caib.helium.monitor.domini.PagedList;
import es.caib.helium.monitor.exception.MonitorIntegracionsException;
import es.caib.helium.monitor.repository.IntegracioEventRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MongoBddService extends ServiceBase implements BddService {
	
	@Autowired
	private IntegracioEventRepository integracioEventRepository;
	
	@Override
	public IntegracioEvent save(IntegracioEvent integracioEvent) throws MonitorIntegracionsException {
		
		IntegracioEvent event = null;
		try {
			event = integracioEventRepository.save(integracioEvent);
		} catch (Exception ex) {
			throwException("Error al guardar la " + integracioEvent.toString(), ex);
		}
		return event;
	}


	
	@Override
	public PagedList<IntegracioEvent> findByFiltresPaginat(Consulta consulta) throws MonitorIntegracionsException {
		
		if (consulta.getPage() == null || consulta.getSize() == null) {
			return null;
		}
		
		try {
			log.debug("Obtinguent els events filtrats i paginats per la " + consulta.toString());
			var pagina = integracioEventRepository.findByFiltres(consulta);
			var pageable = PageRequest.of(consulta.getPage(), pagina.size() > 0 ? pagina.size() : 1);
			return new PagedList<IntegracioEvent>(pagina, pageable, pagina.size());
		} catch (Exception ex) {
			throwException("Error obtinguent els events filtrats i paginats per la consulta", ex);
		}
		var pageable = PageRequest.of(consulta.getPage(), 1);
		return new PagedList<IntegracioEvent>(new ArrayList<IntegracioEvent>(), pageable, 10);
	}

	@Override
	public List<IntegracioEvent> findByFiltres(Consulta consulta) throws MonitorIntegracionsException {
		
		List<IntegracioEvent> accions = new ArrayList<>();
		try {
			log.info("Obtinguent els events filtrats per la " + consulta.toString());
			accions = integracioEventRepository.findByFiltres(consulta);
		} catch (Exception ex) {
			throwException("Error obtinguent els events filtrats per la consulta", ex);
		}
		return accions;
	}

}
