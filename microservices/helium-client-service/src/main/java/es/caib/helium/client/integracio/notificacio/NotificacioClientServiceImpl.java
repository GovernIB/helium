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

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificacioClientServiceImpl implements NotificacioClientService {
	
	private final String missatgeLog = "Cridant Integracio Service - Notificacio - ";
	
	private NotificacioFeignClient notificacioClient;

	@Override
	public RespostaEnviar altaNotificacio(DadesNotificacioDto dto) {
		
		log.debug(missatgeLog + " alta notificacio" + dto.getConcepte() + " per l'entorn " + dto.getEntornId());
		var responseEntity = notificacioClient.altaNotificacio(dto);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public RespostaConsultaEstatNotificacio consultaNotificacio(String identificador, ConsultaNotificacio consulta) {
	
		log.debug(missatgeLog + " consulta notificacio amb identificador " + identificador + " per l'entorn " + consulta.getEntornId());
		var responseEntity = notificacioClient.consultaNotificacio(identificador, consulta);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public RespostaConsultaEstatEnviament consultaEnviament(String referencia, ConsultaEnviament consulta) {
		
		log.debug(missatgeLog + " consulta enviament referencia " + referencia+ " per l'entorn " + consulta.getEntornId());
		var responseEntity = notificacioClient.consultaEnviament(referencia, consulta);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

}
