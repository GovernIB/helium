package es.caib.helium.client.integracio.registre.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistreNotificacio {

	private DadesExpedient dadesExpedient;
	private DadesOficina dadesOficina;
	private DadesInteressat dadesInteressat;
	private DadesRepresentat dadesRepresentat;
	private DadesNotificacio dadesNotificacio;
	private List<DocumentRegistre> documents;
}
