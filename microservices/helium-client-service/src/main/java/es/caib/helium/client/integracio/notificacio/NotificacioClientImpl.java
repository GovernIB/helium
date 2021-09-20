package es.caib.helium.client.integracio.notificacio;

import es.caib.helium.client.integracio.notificacio.model.ConsultaEnviament;
import es.caib.helium.client.integracio.notificacio.model.ConsultaNotificacio;
import es.caib.helium.client.integracio.notificacio.model.DadesNotificacioDto;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatEnviament;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatNotificacio;
import es.caib.helium.client.integracio.notificacio.model.RespostaEnviar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificacioClientImpl implements NotificacioClient {
	
	private final String missatgeLog = "Cridant Integracio Service - Notificacio - ";
	
	private final NotificacioFeignClient notificacioClient;

	@Override
	public RespostaEnviar altaNotificacio(DadesNotificacioDto dto) {
		
		log.debug(missatgeLog + " alta notificacio" + dto.getConcepte() + " per l'entorn " + dto.getEntornId());
		return notificacioClient.altaNotificacio(dto).getBody();
	}

	@Override
	public RespostaConsultaEstatNotificacio consultarNotificacio(String identificador, ConsultaNotificacio consulta) {
	
		log.debug(missatgeLog + " consulta notificacio amb identificador " + identificador + " per l'entorn " + consulta.getEntornId());
		return notificacioClient.consultarNotificacio(identificador, consulta).getBody();
	}

	@Override
	public RespostaConsultaEstatEnviament consultarEnviament(String referencia, ConsultaEnviament consulta) {
		
		log.debug(missatgeLog + " consulta enviament referencia " + referencia+ " per l'entorn " + consulta.getEntornId());
		return notificacioClient.consultarEnviament(referencia, consulta).getBody();
	}

}
