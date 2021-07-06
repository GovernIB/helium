package es.caib.helium.client.integracio.notificacio;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.notificacio.model.ConsultaEnviament;
import es.caib.helium.client.integracio.notificacio.model.ConsultaNotificacio;
import es.caib.helium.client.integracio.notificacio.model.DadesNotificacioDto;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatEnviament;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatNotificacio;
import es.caib.helium.client.integracio.notificacio.model.RespostaNotificacio;

@Service
public interface NotificacioService {

	public RespostaNotificacio altaNotificacio(DadesNotificacioDto dto);
	
	public RespostaConsultaEstatNotificacio consultaNotificacio(String identificador, ConsultaNotificacio consulta);
	
	public RespostaConsultaEstatEnviament consultaEnviament(String referencia, ConsultaEnviament consulta);
}
