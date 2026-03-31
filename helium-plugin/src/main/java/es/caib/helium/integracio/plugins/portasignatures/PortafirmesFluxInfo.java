package es.caib.helium.integracio.plugins.portasignatures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PortafirmesFluxInfo implements Serializable {
	private String nom;
	private String descripcio;
	private List<PortafirmesFluxSigner> signers = new ArrayList<PortafirmesFluxSigner>();
	private static final long serialVersionUID = -1665824823934702923L;
}
