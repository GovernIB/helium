
package es.caib.helium.integracio.service.notificacio;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.notificacio.ConsultaEnviament;
import es.caib.helium.integracio.domini.notificacio.ConsultaNotificacio;
import es.caib.helium.integracio.domini.notificacio.DadesNotificacioDto;
import es.caib.helium.integracio.domini.notificacio.RespostaConsultaEstatEnviament;
import es.caib.helium.integracio.domini.notificacio.RespostaConsultaEstatNotificacio;
import es.caib.helium.integracio.domini.notificacio.RespostaNotificacio;
import es.caib.helium.integracio.excepcions.notificacio.NotificacioException;

@Service
public interface NotificacioService {
	
	public RespostaNotificacio altaNotificacio(DadesNotificacioDto dto);
	
	// TODO El metode consultarNotificacio fa referencia al PluginHelper.notificacioActualitzarEstat d'Helium 3.2
	// Aquí només s'ha implementat la consulta. Falta decidir on ficar el codi que hi ha post consulta
	public RespostaConsultaEstatNotificacio consultarNotificacio(ConsultaNotificacio consulta) throws NotificacioException;
	public RespostaConsultaEstatEnviament consultarEnviament(ConsultaEnviament consulta) throws NotificacioException;

}
