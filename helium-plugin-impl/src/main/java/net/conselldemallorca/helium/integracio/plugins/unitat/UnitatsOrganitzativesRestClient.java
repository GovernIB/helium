package net.conselldemallorca.helium.integracio.plugins.unitat;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import net.conselldemallorca.helium.v3.core.api.dto.NivellAdministracioDto;

public class UnitatsOrganitzativesRestClient extends RestClientBaseUnitats{

    private static final String OBTENER_ARBOL_UNITATS = "obtenerArbolUnidades";
    private static final String OBTENER_UNIDAD = "obtenerUnidad";
    private static final String OBTENER_NIVELLS = "nivelesAdministracion";

	
	
	protected String baseUrl;
	protected String username;
	protected String password;
	
	

	protected boolean autenticacioBasic = true;
	protected String cercaUrl;
	
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

	public void setCercaUrl(String cercaUrl) {
		this.cercaUrl = cercaUrl;
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

	
	public List<Nodo> cercaUnitats(
			String codi, 
			String denominacio, 
			String nivellAdministracio,
			Long comunitatAutonoma, 
			Boolean ambOficines,
			Boolean esUnitatArrel, 
			String provincia,
			String municipi) {
		try {
			String urlGet = this.cercaUrl + "?codigo=" + codi + "&denominacion="
					+ URLEncoder.encode(denominacio, "UTF-8") + "&codNivelAdministracion="
					+ (nivellAdministracio != null ? nivellAdministracio : "-1") + "&codComunidadAutonoma="
					+ (comunitatAutonoma != null ? comunitatAutonoma : "-1") + "&conOficinas="
					+ (ambOficines != null && ambOficines ? "true" : "false") + "&unidadRaiz="
					+ (esUnitatArrel != null && esUnitatArrel ? "true" : "false") + "&provincia="
					+ (provincia != null ? provincia : "-1") + "&localidad=" + (municipi != null ? municipi : "-1")
					+ "&vigentes=true";
			
			Client jerseyClient = generarClient();
			// Client jerseyClient = generarIAuthenticarClient(baseUrl + OBTENER_UNIDAD);
			// jerseyClient.addFilter(new HTTPBasicAuthFilter(username, password));
			jerseyClient.setConnectTimeout(Integer.MAX_VALUE);
			List<Nodo> unidadRest = jerseyClient.resource(urlGet).type(MediaType.APPLICATION_JSON)
					.get(new GenericType<List<Nodo>>() {
					});
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
	
	public List<NivellAdministracioDto> nivellAdministracioFindAll() throws Exception {
		String catalogoUrlbase = baseUrl.replace("unidades", "catalogo");
		String urlGet = catalogoUrlbase + OBTENER_NIVELLS;
		List<CodiValor> catNivellsAdministracio = generarClient().resource(urlGet).type(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<CodiValor>>() {
				});
		List<NivellAdministracioDto> nivellsAdministracio = new ArrayList<NivellAdministracioDto>();
		if (catNivellsAdministracio != null) {
			for (CodiValor catNivellAdministracio: catNivellsAdministracio) {
				nivellsAdministracio.add(
						new NivellAdministracioDto(
							Long.valueOf(catNivellAdministracio.getId()), 
							catNivellAdministracio.getDescripcio())
						);
			}
		}
		return nivellsAdministracio;
	}
	
	
	
}
