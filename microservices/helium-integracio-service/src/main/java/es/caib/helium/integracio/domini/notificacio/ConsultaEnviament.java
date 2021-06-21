package es.caib.helium.integracio.domini.notificacio;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ConsultaEnviament {

	private String identificador;
	private Long expedientId;
	private Long entornId;
	@JsonIgnore
	private String enviamentReferencia;
}
