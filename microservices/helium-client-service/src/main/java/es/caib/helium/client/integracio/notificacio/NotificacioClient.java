package es.caib.helium.client.integracio.notificacio;

import es.caib.helium.client.integracio.notificacio.model.ConsultaEnviament;
import es.caib.helium.client.integracio.notificacio.model.ConsultaNotificacio;
import es.caib.helium.client.integracio.notificacio.model.DadesNotificacioDto;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatEnviament;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatNotificacio;
import es.caib.helium.client.integracio.notificacio.model.RespostaEnviar;
import org.springframework.stereotype.Service;

@Service
public interface NotificacioClient {

	RespostaEnviar altaNotificacio(DadesNotificacioDto dto);
	
	RespostaConsultaEstatNotificacio consultarNotificacio(String identificador, ConsultaNotificacio consulta);
	
	RespostaConsultaEstatEnviament consultarEnviament(String referencia, ConsultaEnviament consulta);
}
