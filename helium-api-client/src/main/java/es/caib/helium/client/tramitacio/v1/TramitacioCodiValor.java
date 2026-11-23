package es.caib.helium.client.tramitacio.v1;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TramitacioCodiValor {
	String codi;
	Object valor;
}
