package es.caib.helium.client.integracio.notificacio;

import es.caib.helium.client.integracio.notificacio.model.ConsultaEnviament;
import es.caib.helium.client.integracio.notificacio.model.ConsultaNotificacio;
import es.caib.helium.client.integracio.notificacio.model.DadesNotificacioDto;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatEnviament;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatNotificacio;
import es.caib.helium.client.integracio.notificacio.model.RespostaEnviar;
import org.springframework.stereotype.Service;

@Service
public interface NotificacioClientService {

	RespostaEnviar altaNotificacio(DadesNotificacioDto dto);
	
	RespostaConsultaEstatNotificacio consultaNotificacio(String identificador, ConsultaNotificacio consulta);
	
	RespostaConsultaEstatEnviament consultaEnviament(String referencia, ConsultaEnviament consulta);
}
