package es.caib.helium.integracio.domini.registre;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistreEntrada {

	// TODO afegir annotacions de validacio
	private DadesOficina dadesOficina;
	private DadesInteressat dadesInteressat;
	private DadesRepresentat dadesRepresentat;
	private DadesAssumpte dadesAssumpte;
	private List<DocumentRegistre> documents;
}
