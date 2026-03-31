package es.caib.helium.integracio.plugins.portasignatures;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Estructura de dades del plugin d'un revisor
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class PortafirmesFluxReviser implements Serializable {
	private String llinatges;
	private String nif;
	private String nom;
	private boolean obligat;
	private static final long serialVersionUID = 7596085767014933225L;
}
