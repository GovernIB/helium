package es.caib.helium.client.tramitacio.v1;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un camp de tramitació
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class TramitacioCamp {
	private String codi;
	private String tipus;
	private String etiqueta;
	private String observacions;
	private String bpmAction;

//	private boolean readFrom;
//	private boolean writeTo;
	private boolean required;
	private boolean readOnly;
	private boolean multiple;
	private boolean ocult;
	private Object valor;
}
