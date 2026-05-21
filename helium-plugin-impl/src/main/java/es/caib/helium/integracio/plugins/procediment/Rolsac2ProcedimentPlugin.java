package es.caib.helium.integracio.plugins.procediment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import es.caib.helium.commons.config.PropertyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import es.caib.helium.commons.dto.procediment.ProcedimentTipusEnumDto;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.commons.utils.GlobalProperties;
import es.caib.helium.integracio.plugins.procediment.Rolsac2FiltreOrden.Rolsac2TipusOrdre;

/**
 * Implementació del plugin de consulta de procediments emprant ROLSAC2.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Rolsac2ProcedimentPlugin implements ProcedimentPlugin {

	private Client jerseyClient;
	private ObjectMapper mapper;

	public Rolsac2ProcedimentPlugin() {
		super();
	}

	public Rolsac2ProcedimentPlugin(Properties properties) {
	}

	@Override
	public List<Procediment> findAmbCodiDir3(String codiDir3) throws SistemaExternException {

		logger.debug("Consulta dels procediments de l'unitat organitzativa (" +
				"codiDir3=" + codiDir3 + ")");
		Rolsac2ProcedimientosResponse response = null;
		try {
			response = findAllProcedimentsRolsac(
					Rolsac2ProcedimentFilterRequest.builder()
						.codigoUADir3(codiDir3)
						.estadoSia("A")
						.buscarEnDescendientesUA(1)
						.activo(1)
						.orden(new Rolsac2FiltreOrden("codigo", Rolsac2TipusOrdre.DESC))
						.build()
					);
		} catch (Exception ex) {
			logger.error("No s'han pogut consultar els procediments de ROLSAC2 (" +
					"codiDir3=" + codiDir3 + ")",
					ex);
			throw new SistemaExternException(
					"No s'han pogut consultar els procediments de ROLSAC2 (" +
					"codiDir3=" + codiDir3 + ")",
					ex);
		}

		if (response != null && response.getItems() != null) {
			List<Procediment> procediments = new ArrayList<Procediment>();
			for (Rolsac2Procediment procediment : response.getItems()) {
				procediments.add(this.toProcemiment(procediment));
			}
			return procediments;
		} else {
			logger.error("No s'han pogut consultar els procediments de ROLSAC2 (" +
					"codiDir3=" + codiDir3 + "). Resposta rebuda amb el codi " + response.getStatus());
			throw new SistemaExternException(
					"No s'han pogut consultar els procediments de ROLSAC2 (" +
					"codiDir3=" + codiDir3 + "). Resposta rebuda amb el codi " + response.getStatus());
		}
	}

	@Override
	public List<Procediment> findServeisAmbCodiDir3(String codiDir3) throws SistemaExternException {
		logger.debug("Consulta dels serveis de l'unitat organitzativa (" +
				"codiDir3=" + codiDir3 + ")");
		Rolsac2ServiciosResponse response = null;
		try {
			response = findServeisRolsac(Rolsac2ServicioFilterRequest
						.builder()
						.codigoUADir3(codiDir3)
						.estadoSia("A")
						.buscarEnDescendientesUA(1)
						.activo(1)
						.filtroPaginacion(new Rolsac2FiltrePaginacio(0, 9999))
						.build());
		} catch (Exception ex) {
			logger.error("No s'han pogut consultar els serveis de ROLSAC2 (" +
					"codiDir3=" + codiDir3 + ")",
					ex);
			throw new SistemaExternException(
					"No s'han pogut consultar els serveis de ROLSAC2 (" +
					"codiDir3=" + codiDir3 + ")",
					ex);
		}

		if (response != null && response.getStatus().equals("200")) {
			List<Procediment> procediments = new ArrayList<Procediment>();
			for (Rolsac2Servei procediment : response.getItems()) {
				procediments.add(this.toProcemiment(procediment));
			}
			return procediments;
		} else {
			logger.error("No s'han pogut consultar els serveis de ROLSAC2 (" +
					"codiDir3=" + codiDir3 + "). Resposta rebuda amb el codi " + response.getStatus());
			throw new SistemaExternException(
					"No s'han pogut consultar els serveis de ROLSAC2 (" +
					"codiDir3=" + codiDir3 + "). Resposta rebuda amb el codi " + response.getStatus());
		}
	}

	public Procediment toProcemiment (Rolsac2Procediment procediment) throws  SistemaExternException {
		Procediment dto = new Procediment();
		if (procediment != null) {
			dto.setCodi(String.valueOf(procediment.getCodigo()));
			dto.setCodiSia(String.valueOf(procediment.getCodigoSIA()));
			dto.setNom(procediment.getNombreProcedimientoWorkFlow());
			dto.setComu(procediment.getComun());
			dto.setTipus(ProcedimentTipusEnumDto.PROCEDIMENT);
			if (procediment.getLinkUnidadAdministrativaResponsable() != null) {
				dto.setUnitatAdministrativacodi(procediment.getLinkUnidadAdministrativaResponsable().getCodigo());
			} else if (procediment.getLinkUnidadAdministrativaCompetente() != null) {
				dto.setUnitatAdministrativacodi(procediment.getLinkUnidadAdministrativaCompetente().getCodigo());
			} else if (procediment.getLinkUnidadAdministrativaInstructora() != null) {
				dto.setUnitatAdministrativacodi(procediment.getLinkUnidadAdministrativaInstructora().getCodigo());
			}
		}
		return dto;
	}

	public Procediment toProcemiment (Rolsac2Servei procediment) throws  SistemaExternException {
		Procediment dto = new Procediment();
		if (procediment != null) {
			dto.setCodi(String.valueOf(procediment.getCodigo()));
			dto.setCodiSia(String.valueOf(procediment.getCodigoSIA()));
			dto.setNom(procediment.getNombreProcedimientoWorkFlow());
			dto.setComu(procediment.getComun() != null && procediment.getComun().intValue() == 1);
			dto.setTipus(ProcedimentTipusEnumDto.SERVEI);
			if (procediment.getLinkUnidadAdministrativaResponsable() != null) {
				dto.setUnitatAdministrativacodi(procediment.getLinkUnidadAdministrativaResponsable().getCodigo());
			} else if (procediment.getLinkUnidadAdministrativaInstructora() != null) {
				dto.setUnitatAdministrativacodi(procediment.getLinkUnidadAdministrativaInstructora().getCodigo());
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

	private Rolsac2ProcedimientosResponse findAllProcedimentsRolsac(
			Rolsac2ProcedimentFilterRequest body) throws Exception {

		body.setFiltroPaginacion(new Rolsac2FiltrePaginacio(0, 100));

		final String url = getServiceUrl() + "/procedimientos";
		logger.debug("Enviant petició HTTP a l'arxiu (" +
				"url=" + url + ", " +
				"tipus=application/json, " +
				"body=" + body + ")");
		ClientResponse response = getJerseyClient().
				resource(url).
				accept("application/json").
				type("application/json").
				post(ClientResponse.class, body);

		Rolsac2ProcedimientosResponse procedimentsResponse = response.getEntity(Rolsac2ProcedimientosResponse.class);

		ExecutorService executor = Executors.newFixedThreadPool(procedimentsResponse.getTotalPages());
		List<Future<Rolsac2ProcedimientosResponse>> futures = new ArrayList<Future<Rolsac2ProcedimientosResponse>>();

		for (int i = 1; i <= procedimentsResponse.getTotalPages(); i++) {
			final Rolsac2ProcedimentFilterRequest taskBody = body.clone();
			taskBody.setFiltroPaginacion(new Rolsac2FiltrePaginacio(i, 100));
			Callable<Rolsac2ProcedimientosResponse> task = new Callable<Rolsac2ProcedimientosResponse>() {
				public Rolsac2ProcedimientosResponse call() throws Exception {
					int trys = 0;
					while(trys < 2) {
						trys++;
						ClientResponse response = getJerseyClient().
								resource(url).
								accept("application/json").
								type("application/json").
								post(ClientResponse.class, taskBody);
						if(response.getStatus() != 200) {
							continue;
						}
						return response.getEntity(Rolsac2ProcedimientosResponse.class);
					}
					return null;
				}
			};
			futures.add(executor.submit(task));
		}

		for (Future<Rolsac2ProcedimientosResponse> future : futures) {
			Rolsac2ProcedimientosResponse result = future.get();
			if(result != null)
				procedimentsResponse.getItems().addAll(result.getItems());
		}

		executor.shutdown();

		return procedimentsResponse;
	}


	@Override
	public UnitatAdministrativa findUnitatAdministrativaAmbCodi(String codi) throws SistemaExternException {

		logger.debug("Consulta de la unitat administrativa amb codi (" +
				"codi=" + codi + ")");

		UnitatAdministrativa unitatAdministrativa = null;
		try {
			String urlAmbMetode = getServiceUrl() + "/unidades_administrativas/" + codi;
			Client jerseyClient = getJerseyClient();

			Rolsac2UAResponse resposta = jerseyClient.
					resource(urlAmbMetode).
					post(Rolsac2UAResponse.class);

			if (resposta.getItems() != null && !resposta.getItems().isEmpty()) {
				Rolsac2UnitatAdministrativa unitatAdministrativaRolsac = resposta.getItems().get(0);
				unitatAdministrativa = new UnitatAdministrativa();
				unitatAdministrativa.setCodi(String.valueOf(unitatAdministrativaRolsac.getCodigo()));
				unitatAdministrativa.setCodiDir3(unitatAdministrativaRolsac.getCodigoDIR3());
				unitatAdministrativa.setNom(unitatAdministrativaRolsac.getNombre());
				if (unitatAdministrativaRolsac.getLink_padre() != null) {
					unitatAdministrativa.setPareCodi(unitatAdministrativaRolsac.getLink_padre().getCodigo());
				}
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut consultar la unitat administrativa amb codi " + codi + " via REST: " + ex.toString(),
					ex);
		}
		return unitatAdministrativa;
	}

	private Rolsac2ServiciosResponse findServeisRolsac(
			Rolsac2ServicioFilterRequest body) throws UniformInterfaceException, ClientHandlerException, IOException {
		String url = getServiceUrl() + "/servicios";
		logger.debug("Enviant petició HTTP a l'arxiu (" +
				"url=" + url + ", " +
				"tipus=application/json, " +
				"body=" + body + ")");
		ClientResponse response = getJerseyClient().
				resource(url).
				accept("application/json").
				type("application/json").
				post(ClientResponse.class, body);
		return response.getEntity(Rolsac2ServiciosResponse.class);
	}

	private String getServiceUrl() {
		return GlobalProperties.getInstance().getProperty(
			PropertyConfig.PROP_PLUGINS_PROCEDIMENTS_ROLSAC_SERVICE_URL);
	}
	private String getServiceUsername() {
		return GlobalProperties.getInstance().getProperty(
			PropertyConfig.PROP_PLUGINS_PROCEDIMENTS_ROLSAC_SERVICE_USERNAME);
	}
	private String getServicePassword() {
		return GlobalProperties.getInstance().getProperty(
			PropertyConfig.PROP_PLUGINS_PROCEDIMENTS_ROLSAC_SERVICE_PASSWORD);
	}
	private Integer getServiceTimeout() {
		String key = PropertyConfig.PROP_PLUGINS_PROCEDIMENTS_ROLSAC_SERVICE_TIMEOUT;
		if (GlobalProperties.getInstance().getProperty(key) != null) {
			return GlobalProperties.getInstance().getPropertyAsInteger(key);
		} else {
			return null;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(Rolsac2ProcedimentPlugin.class);
}
