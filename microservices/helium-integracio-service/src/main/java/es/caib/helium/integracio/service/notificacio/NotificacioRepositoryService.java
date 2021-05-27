package es.caib.helium.integracio.service.notificacio;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.notificacio.DadesNotificacioDto;
import es.caib.helium.integracio.domini.notificacio.DocumentNotificacio;

@Service
public interface NotificacioRepositoryService {

	public DocumentNotificacio altaNotificacio(DadesNotificacioDto dadesNotificacioDto);
}
