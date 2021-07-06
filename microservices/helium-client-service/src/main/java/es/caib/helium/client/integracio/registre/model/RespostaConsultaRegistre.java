package es.caib.helium.client.integracio.registre.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespostaConsultaRegistre {

	private String registreNumero;
	private Date registreData;
	private String oficinaCodi;
	private String oficinaDenominacio;
	private String entitatCodi;
	private String entitatDenominacio;
}
