package es.caib.helium.client.integracio.notificacio;

import es.caib.helium.client.integracio.notificacio.model.ConsultaEnviament;
import es.caib.helium.client.integracio.notificacio.model.ConsultaNotificacio;
import es.caib.helium.client.integracio.notificacio.model.DadesNotificacioDto;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatEnviament;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatNotificacio;
import es.caib.helium.client.integracio.notificacio.model.RespostaEnviar;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;



public interface NotificacioFeignClient {

	String SERVEI = "-notificacio";

	@RequestMapping(method = RequestMethod.POST, value = NotificacioPath.ALTA_NOTIFICACIO)
	ResponseEntity<RespostaEnviar> altaNotificacio(
			@Valid @RequestBody DadesNotificacioDto dto);
	
	@RequestMapping(method = RequestMethod.GET, value = NotificacioPath.CONSULTA_NOTIFICACIO)
	ResponseEntity<RespostaConsultaEstatNotificacio> consultarNotificacio(
			@Valid @PathVariable("identificador") String identificador,
			@SpringQueryMap ConsultaNotificacio consulta);
	
	@RequestMapping(method = RequestMethod.GET, value = NotificacioPath.CONSULTA_ENVIAMENT)
	ResponseEntity<RespostaConsultaEstatEnviament> consultarEnviament(
			@Valid @PathVariable("referencia") String referencia,
			@SpringQueryMap ConsultaEnviament consulta);
}
