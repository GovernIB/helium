package es.caib.helium.integracio.domini.registre;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespostaAnotacioRegistre extends RespostaBase {

	private String numero;
	private String numeroRegistroFormateado;
	private Date data;
	private ReferenciaRDSJustificante referenciaRDSJustificante;
}