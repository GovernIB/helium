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
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentTipusEnumDto;

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
			response = findProcedimentsRolsac(
					Rolsac2ProcedimentFilterRequest.builder()
						.codigoUADir3(codiDir3)
						.estadoSia("A")
						.buscarEnDescendientesUA(1)
						.activo(1)
						.filtroPaginacion(new Rolsac2FiltrePaginacio(1, 9999))
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
		
		if (response != null && response.getStatus().equals("200")) {
			List<Procediment> procediments = new ArrayList<Procediment>();
			for (Rolsac2Procediment procediment : response.getResultado()) {
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
						.filtroPaginacion(new Rolsac2FiltrePaginacio(1, 9999))
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
			for (Rolsac2Servei procediment : response.getResultado()) {
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

	private Rolsac2ProcedimientosResponse findProcedimentsRolsac(
			Rolsac2ProcedimentFilterRequest body) throws UniformInterfaceException, ClientHandlerException, IOException {
		String url = getServiceUrl() + "/procedimientos";
		logger.debug("Enviant petició HTTP a l'arxiu (" +
				"url=" + url + ", " +
				"tipus=application/json, " +
				"body=" + body + ")");
		ClientResponse response = getJerseyClient().
				resource(url).
				accept("application/json").
				type("application/json").
				post(ClientResponse.class, body);
		return response.getEntity(Rolsac2ProcedimientosResponse.class);
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
			
			if (resposta.getResultado() != null && !resposta.getResultado().isEmpty()) {
				Rolsac2UnitatAdministrativa unitatAdministrativaRolsac = resposta.getResultado().get(0);
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
	

	private static final Logger logger = LoggerFactory.getLogger(Rolsac2ProcedimentPlugin.class);
}
