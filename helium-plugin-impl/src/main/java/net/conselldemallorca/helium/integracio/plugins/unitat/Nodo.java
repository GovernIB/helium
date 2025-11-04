package net.conselldemallorca.helium.integracio.plugins.unitat;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Nodo {
	private String codigo;
	private String codigoDir3;
	private String version;
	private String denominacion;
	private String denominacionCooficial;
	private String descripcionEstado;
	private String codigoEstado;
	private String raiz;
	private String superior;
	private String codigoSuperior;
	private String localidad;
	private String edpPrincipal;
	private String idPadre;
	private List<Nodo> hijos;
	private List<Nodo> oficinasDependientes;
	private List<Nodo> oficinasAuxiliares;
	private List<Nodo> oficinasFuncionales;
	private List<Nodo> historicos;
	private Boolean tieneOficinaSir;
	private Boolean esEdp;
	private Long nivel;
	private String cif;
	private String nivelAdministracion;
	private String tipoUnidad;
	private String direccion;
	private List<String> contactos;
	private String ambitoTerritorial;
	private String ambitoPais;
	private String ambitoComunAutonoma;
	private String ambitoProvincia;
	private String ambitoIsla;
	private Boolean referenciaUnica;
}
