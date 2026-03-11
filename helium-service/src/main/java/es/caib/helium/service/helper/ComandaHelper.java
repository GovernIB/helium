package es.caib.helium.service.helper;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import es.caib.comanda.model.v1.tasca.Prioritat;
import es.caib.comanda.model.v1.tasca.Tasca;
import es.caib.comanda.model.v1.tasca.TascaEstat;
import es.caib.helium.logic.intf.dto.engine.WTaskInstance;
import es.caib.helium.service.utils.GlobalProperties;


/**
 * 
 * Helper de Comanda
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
//@Component
public class ComandaHelper {
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	private String API_URL			= GlobalProperties.getInstance().getProperty("app.comanda.api.url");
	private String API_USER			= GlobalProperties.getInstance().getProperty("app.comanda.api.user");
	private String API_PASS			= GlobalProperties.getInstance().getProperty("app.comanda.api.password");
	private String HELIUM_BASE_URL	= GlobalProperties.getInstance().getProperty("app.base.url");
	private String ENTORN			= GlobalProperties.getInstance().getProperty("app.entorn.helium");
	
	private static final Logger logger = LoggerFactory.getLogger(ComandaHelper.class);
	
	public Tasca getTasca(String tascaId) throws UniformInterfaceException, InterruptedException, ExecutionException {
		ClientResponse response = getClient()
								.resource(API_URL + "/v1/tasques/" + tascaId)
								.queryParam("appCodi", "HEL")
								.queryParam("entornCodi", ENTORN)
								.get(ClientResponse.class);
		
		if(response.getStatus() == 200) {
		 return response.getEntity(Tasca.class);
		}
		
		logger.error("[COMANDA] GET: " + response.toString());
		return null;
	}
	
	public void upsertTasca(String taskId, String nom, String expedientNumero, WTaskInstance task, TascaEstat estat) {
		try {
			Tasca tasca = getTasca(taskId);
			if(tasca == null) {
				createTasca(taskId, nom, expedientNumero, task, estat);
				return;
			}
			updateTasca(tasca, taskId, expedientNumero, task, estat);
		} catch(Exception e) {
			logger.error(
					"Error inesperat a la creació/actualització de la tasca amb id '" + taskId + "' del expedient " + expedientNumero,
					e);
		}
	}
	
	
	private void createTasca(String taskId, String nom, String expedientNumero, WTaskInstance task, TascaEstat estat) throws UniformInterfaceException, InterruptedException, ExecutionException, MalformedURLException {
		List<String> usuaris = new ArrayList<String>();
		
		if(task.getTask().getPooledActors() != null)
			usuaris.addAll(task.getPooledActors());
		
		List<String> grups = new ArrayList<String>();
		if(task.getTask().getRols() != null) {
			for(String rol : task.getTask().getRols().split(","))
				grups.add(rol);
		}
		
		Tasca tasca = Tasca.builder()
							.appCodi("HEL")
							.entornCodi(ENTORN)
							.identificador(String.valueOf(taskId))
							.tipus(task.getTaskName())
							.nom(nom)
							.descripcio(task.getDescription())
							.estat(estat)
							.estatDescripcio(null)
							.numeroExpedient(expedientNumero)
							.prioritat(Prioritat.NORMAL)
							.dataInici(task.getStartTime())
							.dataFi(task.getEndTime())
							.dataCaducitat(task.getDueDate())
							.redireccio(new URL(HELIUM_BASE_URL + "/v3/tasca/" + taskId))
							.grup(null)
							.responsable(task.getActorId())
							.usuarisAmbPermis(usuaris)
							.grupsAmbPermis(grups)
							.build();
		
		ClientResponse response = getClient()
									.resource(API_URL + "/v1/tasques")
									.type(MediaType.APPLICATION_JSON)
									.accept(MediaType.APPLICATION_JSON)
									.post(ClientResponse.class, tasca);
		
		if(response.getStatus() != 200)
			logger.error("[COMANDA] POST: " + response.toString());
	}
	
	private void updateTasca(Tasca tasca, String taskId, String expedientNumero, WTaskInstance task, TascaEstat estat) throws MalformedURLException, UniformInterfaceException, InterruptedException, ExecutionException {
		List<String> usuaris = new ArrayList<String>();
		
		if(task.getTask().getPooledActors() != null)
			usuaris.addAll(task.getPooledActors());
		
		List<String> grups = new ArrayList<String>();
		if(task.getTask().getRols() != null) {
			for(String rol : task.getTask().getRols().split(","))
				grups.add(rol);
		}
		
		tasca.setAppCodi("HEL");
		tasca.setEntornCodi(ENTORN);
		
		tasca.setEstat(estat);
		tasca.setEstatDescripcio(null);
		tasca.setNumeroExpedient(expedientNumero);
		tasca.setPrioritat(Prioritat.NORMAL);
		tasca.setDataInici(task.getStartTime());
		tasca.setDataFi(task.getEndTime());
		tasca.setDataCaducitat(task.getDueDate());
		tasca.setRedireccio(new URL(HELIUM_BASE_URL + "/v3/tasca/" + taskId));
		tasca.setGrup(null);
		tasca.setResponsable(task.getActorId());
		tasca.setUsuarisAmbPermis(usuaris);
		tasca.setGrupsAmbPermis(grups);
		
		
		ClientResponse response = getClient()
				.resource(API_URL + "/v1/tasques/" + tasca.getIdentificador())
				.queryParam("appCodi", "HEL")
				.queryParam("entornCodi", ENTORN)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, tasca);

		if(response.getStatus() != 200)
			logger.error("[COMANDA] PUT: " + response.toString());
	}
	
	public Client getClient() {
		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter(API_USER, API_PASS));
		return client;
	}
	
	private String formatDate(Date date) {
		if(date == null)
			return null;
		return sdf.format(date);
	}
}
