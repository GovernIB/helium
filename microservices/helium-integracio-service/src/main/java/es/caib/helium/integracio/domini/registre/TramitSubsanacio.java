package es.caib.helium.integracio.domini.registre;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TramitSubsanacio {

	private String identificador;
	private int versio;
	private String descripcio;
	private List<TramitSubsanacioParametre> parametres;
}
