package es.caib.helium.client.tramitacio.v1;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class TramitacioExpedient {
	java.lang.String expedientTipus;
	java.lang.String numero;
	java.lang.String titol;
	List<TramitacioCodiValor> valors;
}
