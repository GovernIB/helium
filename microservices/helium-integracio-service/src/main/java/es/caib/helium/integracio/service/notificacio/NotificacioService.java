package es.caib.helium.integracio.service.notificacio;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.notificacio.DadesNotificacioDto;
import es.caib.helium.integracio.domini.notificacio.Notificacio;
import es.caib.helium.integracio.domini.notificacio.RespostaConsultaEstatEnviament;
import es.caib.helium.integracio.domini.notificacio.RespostaConsultaEstatNotificacio;
import es.caib.helium.integracio.domini.notificacio.RespostaEnviar;
import es.caib.helium.integracio.domini.notificacio.RespostaNotificacio;
import es.caib.helium.integracio.excepcions.notificacio.NotificacioException;

@Service
public interface NotificacioService {
	
	public RespostaNotificacio altaNotificacio(DadesNotificacioDto dto);
	public RespostaConsultaEstatNotificacio consultaNotificacio(String identificador) throws NotificacioException;
	public RespostaConsultaEstatEnviament consultaEnviament(String referencia) throws NotificacioException;

}
