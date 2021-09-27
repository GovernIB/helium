
package es.caib.helium.integracio.service.notificacio;

import es.caib.helium.integracio.domini.notificacio.ConsultaEnviament;
import es.caib.helium.integracio.domini.notificacio.ConsultaNotificacio;
import es.caib.helium.integracio.domini.notificacio.DadesNotificacioDto;
import es.caib.helium.integracio.domini.notificacio.RespostaConsultaEstatEnviament;
import es.caib.helium.integracio.domini.notificacio.RespostaConsultaEstatNotificacio;
import es.caib.helium.integracio.domini.notificacio.RespostaEnviar;
import es.caib.helium.integracio.excepcions.notificacio.NotificacioException;
import org.springframework.stereotype.Service;

@Service
public interface NotificacioService {
	
	RespostaEnviar altaNotificacio(DadesNotificacioDto dto);
	
	// TODO El metode consultarNotificacio fa referencia al PluginHelper.notificacioActualitzarEstat d'Helium 3.2
	// Aquí només s'ha implementat la consulta. Falta decidir on ficar el codi que hi ha post consulta
	RespostaConsultaEstatNotificacio consultarNotificacio(ConsultaNotificacio consulta) throws NotificacioException;
	RespostaConsultaEstatEnviament consultarEnviament(ConsultaEnviament consulta) throws NotificacioException;

}
