package es.caib.helium.integracio.plugins.portasignatures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Estructura de dades del plugin d'un signant
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class PortafirmesFluxSigner implements Serializable {
	private String llinatges;
	private String nif;
	private String nom;
	private boolean obligat;
	private List<PortafirmesFluxReviser> revisors = new ArrayList<PortafirmesFluxReviser>();
	private static final long serialVersionUID = -368431499989945646L;
}
