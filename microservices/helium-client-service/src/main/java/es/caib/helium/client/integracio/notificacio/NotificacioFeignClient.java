package es.caib.helium.client.integracio.notificacio;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.helium.client.integracio.notificacio.model.ConsultaEnviament;
import es.caib.helium.client.integracio.notificacio.model.ConsultaNotificacio;
import es.caib.helium.client.integracio.notificacio.model.DadesNotificacioDto;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatEnviament;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatNotificacio;
import es.caib.helium.client.integracio.notificacio.model.RespostaNotificacio;



public interface NotificacioFeignClient {
	
	@RequestMapping(method = RequestMethod.POST, value = NotificacioPath.ALTA_NOTIFICACIO)
	public ResponseEntity<RespostaNotificacio> altaNotificacio(
			@Valid @RequestBody DadesNotificacioDto dto);
	
	@RequestMapping(method = RequestMethod.GET, value = NotificacioPath.CONSULTA_NOTIFICACIO)
	public ResponseEntity<RespostaConsultaEstatNotificacio> consultaNotificacio(
			@Valid @PathVariable("identificador") String identificador, 
			ConsultaNotificacio consulta);
	
	@RequestMapping(method = RequestMethod.GET, value = NotificacioPath.CONSULTA_ENVIAMENT)
	public ResponseEntity<RespostaConsultaEstatEnviament> consultaEnviament(
			@Valid @PathVariable("referencia") String referencia, 
			ConsultaEnviament consulta);
}
