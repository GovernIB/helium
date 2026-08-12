package es.caib.helium.logic.helper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import es.caib.comanda.model.management.Prioritat;
import es.caib.comanda.model.management.Tasca;
import es.caib.comanda.model.management.TascaEstat;
import es.caib.helium.commons.config.PropertyConfig;
import es.caib.helium.logic.config.JacksonObjectMapperProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import es.caib.helium.disseny.engine.WTaskInstance;
import es.caib.helium.logic.intf.util.DatesUtils;

/**
 *
 * Helper de Comanda
 *
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Component
public class ComandaHelper {

	@Value("${" + PropertyConfig.PROP_COMANDA_API_URL + ":#{null}}")
	private String API_URL;
	@Value("${" + PropertyConfig.PROP_COMANDA_API_USER + ":#{null}}")
	private String API_USER;
	@Value("${" + PropertyConfig.PROP_COMANDA_API_PASSWORD + ":#{null}}")
	private String API_PASS;
	@Value("${" + PropertyConfig.PROP_BASE_URL + ":#{null}}")
	private String HELIUM_BASE_URL;
	@Value("${" + PropertyConfig.PROP_ENTORN_HELIUM + ":#{null}}")
	private String ENTORN;
	private final String APP_CODI = "HEL2";

	private Client client;

	private static final Logger logger = LoggerFactory.getLogger(ComandaHelper.class);

	public Tasca getTasca(String tascaId) throws UniformInterfaceException, InterruptedException, ExecutionException {
		ClientResponse response = getClient()
								.resource(API_URL + "/v1/tasques/" + tascaId)
								.queryParam("appCodi", APP_CODI)
								.queryParam("entornCodi", ENTORN)
								.get(ClientResponse.class);

		if(response.getStatus() == 200) {
		 return response.getEntity(Tasca.class);
		}

		logger.error("[COMANDA] GET: {}", response.toString());
		return null;
	}

	public void upsertTasca(String taskId, String nom, String expedientNumero, String tipusExpedientNom, WTaskInstance task, TascaEstat estat) {
		try {
			String descripcio = String.format("[%s] %s", expedientNumero, tipusExpedientNom);
			Tasca tasca = getTasca(taskId);
			if(tasca == null) {
				createTasca(taskId, nom, expedientNumero, descripcio, task, estat);
				return;
			}
			updateTasca(tasca, taskId, expedientNumero, descripcio, task, estat);
		} catch(Exception e) {
			logger.error(
					"Error inesperat a la creació/actualització de la tasca amb id '" + taskId + "' del expedient " + expedientNumero,
					e);
		}
	}


	private void createTasca(String taskId, String nom, String expedientNumero, String descripcio, WTaskInstance task, TascaEstat estat) throws UniformInterfaceException, InterruptedException, ExecutionException, MalformedURLException {
		List<String> usuaris = new ArrayList<String>();

		usuaris.addAll(task.getPooledActors());

		List<String> grups = new ArrayList<>(task.getRols());

		Tasca tasca = new Tasca()
							.appCodi(APP_CODI)
							.entornCodi(ENTORN)
							.identificador(String.valueOf(taskId))
							.tipus(task.getTaskName())
							.nom(nom)
							.descripcio(descripcio)
							.estat(estat)
							.estatDescripcio(null)
							.numeroExpedient(expedientNumero)
							.prioritat(Prioritat.NORMAL)
							.dataInici(DatesUtils.toOffsetDateTime(task.getStartTime()))
							.dataFi(DatesUtils.toOffsetDateTime(task.getEndTime()))
							.dataCaducitat(DatesUtils.toOffsetDateTime(task.getDueDate()))
							.redireccio(new URL(HELIUM_BASE_URL + "/v3/tasca/" + taskId))
							.grup(null)
							.responsable(task.getActorId())
							.usuarisAmbPermis(usuaris)
							.grupsAmbPermis(grups);

		ClientResponse response = getClient()
									.resource(API_URL + "/v1/tasques")
									.type(MediaType.APPLICATION_JSON)
									.accept(MediaType.APPLICATION_JSON)
									.post(ClientResponse.class, tasca);

		if(response.getStatus() != 200)
			logger.error("[COMANDA] POST: {}", response.toString());
	}

	private void updateTasca(Tasca tasca, String taskId, String expedientNumero, String descripcio, WTaskInstance task, TascaEstat estat) throws MalformedURLException, UniformInterfaceException, InterruptedException, ExecutionException {
		List<String> usuaris = new ArrayList<>(task.getPooledActors());

		List<String> grups = new ArrayList<>(task.getRols());

		tasca.setAppCodi(APP_CODI);
		tasca.setEntornCodi(ENTORN);

		tasca.setEstat(estat);
		tasca.setEstatDescripcio(null);
		tasca.descripcio(descripcio);
		tasca.setNumeroExpedient(expedientNumero);
		tasca.setPrioritat(Prioritat.NORMAL);
		tasca.setDataInici(DatesUtils.toOffsetDateTime(task.getStartTime()));
		tasca.setDataFi(DatesUtils.toOffsetDateTime(task.getEndTime()));
		tasca.setDataCaducitat(DatesUtils.toOffsetDateTime(task.getDueDate()));
		tasca.setRedireccio(new URL(HELIUM_BASE_URL + "/v3/tasca/" + taskId));
		tasca.setGrup(null);
		tasca.setResponsable(task.getActorId());
		tasca.setUsuarisAmbPermis(usuaris);
		tasca.setGrupsAmbPermis(grups);

		ClientResponse response = getClient()
				.resource(API_URL + "/v1/tasques/" + tasca.getIdentificador())
				.queryParam("appCodi", APP_CODI)
				.queryParam("entornCodi", ENTORN)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, tasca);

		if(response.getStatus() != 200)
			logger.error("[COMANDA] PUT: {}", response.toString());
	}

	public Client getClient() {
		if(client == null) {
			ClientConfig clientConfig = new DefaultClientConfig();
			clientConfig.getSingletons().add(new JacksonObjectMapperProvider());
			clientConfig.getClasses().add(JacksonJsonProvider.class);
			client = Client.create(clientConfig);
			// Temps de connnexió en ms
			client.setConnectTimeout(5000);
			// Temps de lectura en ms
			client.setReadTimeout(30000);
			client.addFilter(new HTTPBasicAuthFilter(API_USER, API_PASS));
		}
		return client;
	}

}
