package es.caib.helium.integracio.controller.notificacio;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.helium.integracio.domini.notificacio.ConsultaEnviament;
import es.caib.helium.integracio.domini.notificacio.ConsultaNotificacio;
import es.caib.helium.integracio.domini.notificacio.DadesNotificacioDto;
import es.caib.helium.integracio.domini.notificacio.RespostaConsultaEstatEnviament;
import es.caib.helium.integracio.domini.notificacio.RespostaConsultaEstatNotificacio;
import es.caib.helium.integracio.domini.notificacio.RespostaNotificacio;
import es.caib.helium.integracio.service.notificacio.NotificacioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(NotificacioController.API_PATH)
public class NotificacioController {

	public static final String API_PATH = "/api/v1/notificacio";
	
	@Autowired
	private NotificacioService notificacioService;
	
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Void> handleException(Exception e) {
		e.printStackTrace();
		return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PostMapping(consumes = "application/json")
	public ResponseEntity<RespostaNotificacio> altaNotificacio(@Valid @RequestBody DadesNotificacioDto dto, BindingResult error) throws Exception {
		
		log.info("Donant d'alta la notifiacio " + dto.toString());
		if (error.hasErrors()) {
			return new ResponseEntity<RespostaNotificacio>(HttpStatus.BAD_REQUEST);
		}
		
		var resposta = notificacioService.altaNotificacio(dto);
		return new ResponseEntity<RespostaNotificacio>(resposta, HttpStatus.OK);
	}

	@GetMapping(value = "{identificador}/consulta", produces = "application/json")  
	public ResponseEntity<RespostaConsultaEstatNotificacio> consultaNotificacio(@Valid @PathVariable("identificador") String identificador, ConsultaNotificacio consulta) throws Exception {
 		
		log.info("Consultant la notificacio " + identificador + " " + consulta.toString()) ;
		consulta.setIdentificador(identificador);
		var resposta = notificacioService.consultarNotificacio(consulta);
		if (resposta == null) {
			return new ResponseEntity<RespostaConsultaEstatNotificacio>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<RespostaConsultaEstatNotificacio>(resposta, HttpStatus.OK);
	}

	@GetMapping(value = "enviament/{referencia}", produces = "application/json")  
	public ResponseEntity<RespostaConsultaEstatEnviament> consultaEnviament(@Valid @PathVariable("referencia") String referencia, ConsultaEnviament consulta) throws Exception {
		
		log.info("Consultant l'enviament " + referencia + " " + consulta.toString());
		consulta.setEnviamentReferencia(referencia);
		var resposta = notificacioService.consultarEnviament(consulta);
		if (resposta == null) {
			return new ResponseEntity<RespostaConsultaEstatEnviament>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<RespostaConsultaEstatEnviament>(resposta, HttpStatus.OK);
	}
}
