package es.caib.helium.integracio.plugins.procediment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rolsac2DadesContacte {
	private String servicioResponsable;
    private String personaResponsable;
    private String emailIncidencias;
}
