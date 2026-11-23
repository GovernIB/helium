package es.caib.helium.client.tramitacio.v1;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TramitacioErrorResponse {
	String timestamp;
	int status;
	String error;
	String message;
	String path;
}
