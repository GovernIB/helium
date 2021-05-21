package es.caib.helium.integracio.service.notificacio;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.notificacio.Notificacio;
import es.caib.helium.integracio.domini.notificacio.RespostaConsultaEstatEnviament;
import es.caib.helium.integracio.domini.notificacio.RespostaConsultaEstatNotificacio;
import es.caib.helium.integracio.domini.notificacio.RespostaEnviar;
import es.caib.helium.integracio.excepcions.notificacio.NotificacioPluginException;

@Service
public interface NotificacioService {
	
	public RespostaEnviar crearNotificacio(Notificacio notificacio) throws NotificacioPluginException;
	public RespostaConsultaEstatNotificacio consultaNotificacio(String identificador) throws NotificacioPluginException;
	public RespostaConsultaEstatEnviament consultaEnviament(String referencia) throws NotificacioPluginException;

}
