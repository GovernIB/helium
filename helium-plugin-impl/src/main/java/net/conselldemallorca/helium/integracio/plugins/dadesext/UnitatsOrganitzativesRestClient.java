package net.conselldemallorca.helium.integracio.plugins.dadesext;


import java.util.List;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class UnitatsOrganitzativesRestClient extends RestClientBaseUnitats{

    private static final String OBTENER_ARBOL_UNITATS = "obtenerArbolUnidades";
    private static final String OBTENER_UNIDAD = "obtenerUnidad";

	
	
	protected String baseUrl;
	protected String username;
	protected String password;	
	

	protected boolean autenticacioBasic = true;

	
	public UnitatsOrganitzativesRestClient() {}
	public UnitatsOrganitzativesRestClient(
			String baseUrl,
			String username,
			String password) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}
	public UnitatsOrganitzativesRestClient(
			String baseUrl,
			String username,
			String password,
			boolean autenticacioBasic) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.autenticacioBasic = autenticacioBasic;
	}

	
	public List<UnidadRest> obtenerArbolUnidades(String codigo, String fechaActualizacion, String fechaSincronizacion, Boolean denominacioCooficial) {
		try {
			String dadaAct = null;
			String dadaSin = null;

			if (fechaActualizacion != null) {
				dadaAct = "fechaActualizacion=" + fechaActualizacion;
			}
			if (fechaSincronizacion != null) {
				dadaSin = "fechaSincronizacion=" + fechaSincronizacion;
			}

			String urlGet = baseUrl + OBTENER_ARBOL_UNITATS + "?codigo=" + codigo + "&"
					+ (dadaAct != null ? (dadaAct + "&") : "") + (dadaSin != null ? (dadaSin + "&") : "")
					+ "denominacionCooficial=" + denominacioCooficial;

			Client jerseyClient = generarIAuthenticarClient(baseUrl + OBTENER_ARBOL_UNITATS);
			jerseyClient.addFilter(new HTTPBasicAuthFilter(username, password));

			List<UnidadRest> listUnidadRest = jerseyClient.resource(urlGet).type(MediaType.APPLICATION_JSON)
					.get(new GenericType<List<UnidadRest>>() {
					});
			return listUnidadRest;
		} catch (Exception ex) {
			if (ex instanceof UniformInterfaceException) {
				if (((UniformInterfaceException) ex).getResponse().getStatus() == 204) { // com.sun.jersey.api.client.UniformInterfaceException:
																							// GET returned a response
																							// status of 204 No Content
					return null;
				}
			}
			throw new RuntimeException(ex);
		}
	}
	
	
	public UnidadRest obtenerUnidad(String codigo, String fechaActualizacion, String fechaSincronizacion, Boolean denominacioCooficial) {
		try {
			String dadaAct = null;
			String dadaSin = null;

			if (fechaActualizacion != null) {
				dadaAct = "fechaActualizacion=" + fechaActualizacion;
			}
			if (fechaSincronizacion != null) {
				dadaSin = "fechaSincronizacion=" + fechaSincronizacion;
			}

			String urlGet = baseUrl + OBTENER_UNIDAD + "?codigo=" + codigo + "&"
					+ (dadaAct != null ? (dadaAct + "&") : "") + (dadaSin != null ? (dadaSin + "&") : "")
					+ "denominacionCooficial=" + denominacioCooficial;

			Client jerseyClient = generarIAuthenticarClient(baseUrl + OBTENER_UNIDAD);
			jerseyClient.addFilter(new HTTPBasicAuthFilter(username, password));

			UnidadRest unidadRest = jerseyClient.resource(urlGet).type(MediaType.APPLICATION_JSON)
					.get(UnidadRest.class);
			return unidadRest;
		} catch (Exception ex) {
			if (ex instanceof UniformInterfaceException) {
				if (((UniformInterfaceException) ex).getResponse().getStatus() == 204) { // com.sun.jersey.api.client.UniformInterfaceException:
																							// GET returned a response
																							// status of 204 No Content
					return null;
				}
			}
			throw new RuntimeException(ex);
		}
	}	

}
