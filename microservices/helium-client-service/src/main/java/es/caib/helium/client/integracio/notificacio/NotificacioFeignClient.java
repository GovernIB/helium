package es.caib.helium.client.integracio.notificacio;

import es.caib.helium.client.integracio.notificacio.model.ConsultaEnviament;
import es.caib.helium.client.integracio.notificacio.model.ConsultaNotificacio;
import es.caib.helium.client.integracio.notificacio.model.DadesNotificacioDto;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatEnviament;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatNotificacio;
import es.caib.helium.client.integracio.notificacio.model.RespostaEnviar;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;



public interface NotificacioFeignClient {
	
	@RequestMapping(method = RequestMethod.POST, value = NotificacioPath.ALTA_NOTIFICACIO)
	ResponseEntity<RespostaEnviar> altaNotificacio(
			@Valid @RequestBody DadesNotificacioDto dto);
	
	@RequestMapping(method = RequestMethod.GET, value = NotificacioPath.CONSULTA_NOTIFICACIO)
	ResponseEntity<RespostaConsultaEstatNotificacio> consultaNotificacio(
			@Valid @PathVariable("identificador") String identificador, 
			ConsultaNotificacio consulta);
	
	@RequestMapping(method = RequestMethod.GET, value = NotificacioPath.CONSULTA_ENVIAMENT)
	ResponseEntity<RespostaConsultaEstatEnviament> consultaEnviament(
			@Valid @PathVariable("referencia") String referencia, 
			ConsultaEnviament consulta);
}
