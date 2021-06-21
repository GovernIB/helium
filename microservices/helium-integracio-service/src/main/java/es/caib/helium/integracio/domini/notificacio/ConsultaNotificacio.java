package es.caib.helium.integracio.domini.notificacio;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ConsultaNotificacio {

	@JsonIgnore
	private String identificador;
	private Long expedientId;
	private Long entornId;
	private String enviamentReferencia;
}
