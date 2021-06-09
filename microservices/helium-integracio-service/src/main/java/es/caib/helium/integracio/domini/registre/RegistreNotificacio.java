package es.caib.helium.integracio.domini.registre;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistreNotificacio {

	//TODO @VALIDS als atributs que ho necessitin
	private DadesExpedient dadesExpedient;
	private DadesOficina dadesOficina;
	private DadesInteressat dadesInteressat;
	private DadesRepresentat dadesRepresentat;
	private DadesNotificacio dadesNotificacio;
	private List<DocumentRegistre> documents;
}
