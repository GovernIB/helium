package net.conselldemallorca.helium.integracio.plugins.procediment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;

/**
 * Implementació del plugin de consulta de procediments emprant ROLSAC.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentPluginRolsac implements ProcedimentPlugin {

	private Client jerseyClient;
	private ObjectMapper mapper;

	public ProcedimentPluginRolsac() {
		super();
	}
	
	public ProcedimentPluginRolsac(Properties properties) {
	}
	
	@Override
	public List<Procediment> findAmbCodiDir3(String codiDir3) throws SistemaExternException {

		logger.debug("Consulta dels procediments de l'unitat organitzativa (" +
				"codiDir3=" + codiDir3 + ")");
		ProcedimientosResponse response = null;
		try {
			response = findProcedimentsRolsac(
					getServiceUrl() + "/procedimientos",
					"lang=ca&filtro={\"codigoUADir3\":\"" + codiDir3 + "\",\"estadoSia\":\"A\", \"buscarEnDescendientesUA\":\"1\", \"activo\":\"1\"}&filtroPaginacion={\"page\":\"1\", \"size\":\"9999\"}");
		} catch (Exception ex) {
			logger.error("No s'han pogut consultar els procediments de ROLSAC (" +
					"codiDir3=" + codiDir3 + ")",
					ex);
			throw new SistemaExternException(
					"No s'han pogut consultar els procediments de ROLSAC (" +
					"codiDir3=" + codiDir3 + ")",
					ex);
		}
		
		if (response != null && response.getStatus().equals("200")) {
			List<Procediment> procediments = new ArrayList<Procediment>();
			for (ProcedimentRolsac procediment : response.resultado) {
				procediments.add(this.toProcemiment(procediment));
			}
			return procediments;
		} else {
			logger.error("No s'han pogut consultar els procediments de ROLSAC (" +
					"codiDir3=" + codiDir3 + "). Resposta rebuda amb el codi " + response.getStatus());
			throw new SistemaExternException(
					"No s'han pogut consultar els procediments de ROLSAC (" +
					"codiDir3=" + codiDir3 + "). Resposta rebuda amb el codi " + response.getStatus());
		}
	}

	public Procediment toProcemiment (ProcedimentRolsac procediment) throws  SistemaExternException {
		Procediment dto = new Procediment();
		if (procediment != null) {
			dto.setCodi(procediment.getCodigo());
			dto.setCodiSia(procediment.getCodigoSIA());
			dto.setNom(procediment.getNombre());
			dto.setComu(procediment.isComu());
			if (procediment.getUnidadAdministrativa() != null) {
				dto.setUnitatAdministrativacodi(procediment.getUnidadAdministrativa().getCodigo());
			}
		}
		return dto;
	}

	private Client getJerseyClient() {
		if (jerseyClient == null) {
			jerseyClient = new Client();
			if (getServiceTimeout() != null) {
				jerseyClient.setConnectTimeout(getServiceTimeout());
				jerseyClient.setReadTimeout(getServiceTimeout());
			}
			if (getServiceUsername() != null) {
				jerseyClient.addFilter(new HTTPBasicAuthFilter(getServiceUsername(), getServicePassword()));
			}
			//jerseyClient.addFilter(new LoggingFilter(System.out));
			mapper = new ObjectMapper();
			// Permet rebre un sol objecte en el lloc a on hi hauria d'haver una llista.
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			// Mecanisme de deserialització dels enums
			mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
			// Per a no serialitzar propietats amb valors NULL
			mapper.setSerializationInclusion(Include.NON_NULL);
			// No falla si hi ha propietats que no estan definides a l'objecte destí
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		}
		return jerseyClient;
	}

	private ProcedimientosResponse findProcedimentsRolsac(
			String url,
			String body) throws UniformInterfaceException, ClientHandlerException, IOException {
		logger.debug("Enviant petició HTTP a l'arxiu (" +
				"url=" + url + ", " +
				"tipus=application/json, " +
				"body=" + body + ")");
		ClientResponse response = getJerseyClient().
				resource(url).
				accept("application/json").
				type("application/json").
				post(ClientResponse.class, body);
		String json = response.getEntity(String.class);
		return mapper.readValue(
				json,
				TypeFactory.defaultInstance().constructType(ProcedimientosResponse.class));
	}
	
	
	@Override
	public UnitatAdministrativa findUnitatAdministrativaAmbCodi(String codi) throws SistemaExternException {

		logger.debug("Consulta de la unitat administrativa amb codi (" +
				"codi=" + codi + ")");

		UnitatAdministrativa unitatAdministrativa = null;
		try {
			String urlAmbMetode = getServiceUrl() + "/unidades_administrativas/" + codi;
			
			Client jerseyClient = getJerseyClient();
			
			String json = jerseyClient.
					resource(urlAmbMetode).
					post(String.class);
			
			RespostaUnitatAdministrativa resposta = mapper.readValue(json, RespostaUnitatAdministrativa.class);
			if (resposta.getResultado() != null && !resposta.getResultado().isEmpty()) {
				UnitatAdministrativaRolsac unitatAdministrativaRolsac = resposta.getResultado().get(0);
				unitatAdministrativa = new UnitatAdministrativa();
				unitatAdministrativa.setCodi(String.valueOf(unitatAdministrativaRolsac.getCodigo()));
				unitatAdministrativa.setCodiDir3(unitatAdministrativaRolsac.getCodigoDIR3());
				unitatAdministrativa.setNom(unitatAdministrativaRolsac.getNombre());
				if (unitatAdministrativaRolsac.getPadre() != null) {
					unitatAdministrativa.setPareCodi(unitatAdministrativaRolsac.getPadre().getCodigo());					
				}
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut consultar la unitat administrativa amb codi " + codi + " via REST: " + ex.toString(),
					ex);
		}
		return unitatAdministrativa;
	}

	
	
	private String getServiceUrl() {
		return GlobalProperties.getInstance().getProperty(
				"app.plugins.procediments.rolsac.service.url");
	}
	private String getServiceUsername() {
		return GlobalProperties.getInstance().getProperty(
				"app.plugins.procediments.rolsac.service.username");
	}
	private String getServicePassword() {
		return GlobalProperties.getInstance().getProperty(
				"app.plugins.procediments.rolsac.service.password");
	}
	private Integer getServiceTimeout() {
		String key = "app.plugins.procediments.rolsac.service.timeout";
		if (GlobalProperties.getInstance().getProperty(key) != null) {
			return GlobalProperties.getInstance().getAsInt(key);
		} else {
			return null;
		}
	}
	

	private static final Logger logger = LoggerFactory.getLogger(ProcedimentPluginRolsac.class);
}
