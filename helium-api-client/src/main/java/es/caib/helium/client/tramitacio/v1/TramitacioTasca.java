package es.caib.helium.client.tramitacio.v1;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Informació d'una tasca en tramitació
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class TramitacioTasca {
	private String id;
	private String codi;
	private String titol;
	private String expedient;
	private String missatgeInfo;
	private String missatgeWarn;
	private String responsable;
	private Set<String> responsables;

	private Date dataCreacio;
	private Date dataInici;
	private Date dataFi;
	private Date dataLimit;
	private int prioritat;

	private boolean open;
	private boolean completed;
	private boolean cancelled;
	private boolean suspended;

	private List<String> transicionsSortida;

	private String processInstanceId;
}
