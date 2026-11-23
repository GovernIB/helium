package es.caib.helium.client.tramitacio.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class TramitacioClient {

	private final HttpClient client;
	private final ObjectMapper objectMapper;
	private final String baseUrl;
	private final String basic;

	public TramitacioClient(String baseUrl, String user, String password) {
		this(HttpClient.newHttpClient(), baseUrl, user, password);
	}

	protected TramitacioClient(HttpClient client, String baseUrl, String user, String password) {
		this.client = client;
		this.objectMapper = new ObjectMapper();
		this.baseUrl = baseUrl;
		this.basic = Base64.getEncoder()
						.encodeToString((user + ":" + password)
						.getBytes(StandardCharsets.UTF_8));
	}

	private void checkError(HttpResponse<String> response) throws TramitacioException {
		if (response.statusCode() != 200) {
			String msg = response.body();
			throw new TramitacioException("HTTP " + response.statusCode() + ": " + msg);
		}
	}

	private <T> T readEntity(HttpResponse<String> response, TypeReference<T> typeRef) throws TramitacioException {
		try {
			String json = response.body();
			return objectMapper.readValue(json, typeRef);
		} catch (Exception ex) {
			throw new TramitacioException("Error deserialitzant resposta: " + ex.getMessage(), ex);
		}
	}

	/**
	 *
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientTipusCodi Codi del tipus d'expedient
	 * @param titol Títol parcial pel que filtrar
	 * @param numero Número parcial
	 * @param dataInici1
	 * @param dataInici2
	 * @param estatCodi
	 * @param iniciat Mostrar només iniciats
	 * @param finalitzat Mostrar només finalitzats
	 * @param geoPosX
	 * @param geoPosY
	 * @param geoReferencia
	 * @param p Número de pàgina
	 * @param s Mida de pàgina
	 * @throws TramitacioException
	 */
	public List<TramitacioExpedientInfo> findExpedients(
		String entornCodi,
		String expedientTipusCodi,
		String titol,
		String numero,
		Date dataInici1,
		Date dataInici2,
		String estatCodi,
		Boolean iniciat,
		Boolean finalitzat,
		Double geoPosX,
		Double geoPosY,
		String geoReferencia,
		Integer p,
		Integer s
	) throws TramitacioException {
		String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients" + QueryParams.newQueryParams()
								.add("expedientTipusCodi", expedientTipusCodi)
								.add("titol", titol)
								.add("numero", numero)
								.add("dataInici1", dataInici1)
								.add("dataInici2", dataInici2)
								.add("estatCodi", estatCodi)
								.add("iniciat", iniciat)
								.add("finalitzat", finalitzat)
								.add("geoPosX", geoPosX)
								.add("geoPosY", geoPosY)
								.add("geoReferencia", geoReferencia)
								.add("p", p)
								.add("s", s)
								.build();

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(url))
			.header("Authorization", "Basic " + basic)
			.header("Content-Type", "application/json")
			.header("Accept", "application/json")
			.build();

		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			checkError(response);
			return readEntity(response, new TypeReference<List<TramitacioExpedientInfo>>() {});
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientTramitacio
	 *
	 * @throws TramitacioException
	 */
	public Long iniciExpedient(String entornCodi, TramitacioExpedient expedientTramitacio) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients";

			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(toJson(expedientTramitacio)))
				.build();

			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
			return readEntity(response, new TypeReference<Long>() {});
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 *
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId
	 * @return
	 * @throws TramitacioException
	 */
	public TramitacioExpedientInfo getExpedientInfo(String entornCodi, Long expedientId) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId;
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.GET()
				.build();

			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
			return readEntity(response, new TypeReference<TramitacioExpedientInfo>() {});
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 *
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @throws TramitacioException
	 */
	public void deleteExpedient(String entornCodi, Long expedientId) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId;
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.DELETE()
				.build();

			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());
			checkError(response);
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 *
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param procesInstanceId Id de la instància de procés
	 * @throws TramitacioException
	 */
	public List<TramitacioCamp> consultaDadesProces(String entornCodi, Long expedientId, String procesInstanceId) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/procesos/" + procesInstanceId + "/dades";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.GET()
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());
			checkError(response);
			return readEntity(response, new TypeReference<List<TramitacioCamp>>() {});
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Crea o modifica una dada a una instància de procés
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param procesInstanceId Id de la instància de procés
	 * @param dada Objecte CodiValor de la dada que es vol desar
	 * @throws TramitacioException
	 */
	public void guardarDadaProces(String entornCodi, Long expedientId, String procesInstanceId, TramitacioCodiValor dada) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/procesos/" + procesInstanceId + "/dades";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(toJson(dada)))
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Esborra una dada d'instància de procés
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param procesInstanceId Id de la instància de procés
	 * @param codi Codi de la dada
	 * @throws TramitacioException
	 */
	public void esborrarDadesProces(String entornCodi, Long expedientId, String procesInstanceId, String codi) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/procesos/" + procesInstanceId + "/dades/" + codi;
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.DELETE()
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Consulta els documents d'una instància de procés
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param procesInstanceId Id de la instància de procés
	 * @throws TramitacioException
	 */
	public List<TramitacioDocument> consultaDocumentsProces(String entornCodi, Long expedientId, String procesInstanceId) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/procesos/" + procesInstanceId + "/documents";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.GET()
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
			return readEntity(response, new TypeReference<List<TramitacioDocument>>() {});
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Desa un document a una instància de procés
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param procesInstanceId Id de la instància de procés
	 * @param document
	 * @throws TramitacioException
	 */
	public void guardarDocumentsProces(String entornCodi, Long expedientId, String procesInstanceId, TramitacioDocument document) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/procesos/" + procesInstanceId + "/documents";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(toJson(document)))
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Esborra un document a una instància de procés
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param procesInstanceId Id de la instància de procés
	 * @param documentStoreId Id del document
	 * @throws TramitacioException
	 */
	public void esborrarDocumentProces(String entornCodi, Long expedientId, String procesInstanceId, Long documentStoreId) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/procesos/" + procesInstanceId + "/documents/" + documentStoreId;
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.DELETE()
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Recupera un document a una instància de procés
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param procesInstanceId Id de la instància de procés
	 * @param documentStoreId Id del document
	 * @throws TramitacioException
	 */
	public TramitacioDocument getDocumentProces(String entornCodi, Long expedientId, String procesInstanceId, Long documentStoreId) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/procesos/" + procesInstanceId + "/documents/" + documentStoreId + "/contingut";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.GET()
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
			return readEntity(response, new TypeReference<TramitacioDocument>() {});
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Executa una acció
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param procesInstanceId Id de la instància de procés
	 * @param accioId Id de l'acció
	 * @throws TramitacioException
	 */
	public void execAccioProces(String entornCodi, Long expedientId, String procesInstanceId, Long accioId) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/procesos/" + procesInstanceId + "/accio/" + accioId;
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.GET()
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Executa un script damunt una instància de procés
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param procesInstanceId Id de la instància de procés
	 * @param script TramitacioScript
	 * @throws TramitacioException
	 */
	public void execScriptProces(String entornCodi, Long expedientId, String procesInstanceId, TramitacioScript script) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/procesos/" + procesInstanceId + "/script";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(toJson(script)))
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Recupera totes les tasques assignades a l'usuari connectat
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param personal Només tasques personals
	 * @param groupal Només tasques grupals
	 * @throws TramitacioException
	 */
	public List<TramitacioTasca> consultaTasques(String entornCodi, Long expedientId, Boolean personal, Boolean groupal) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/tasques";
			url = url + QueryParams.newQueryParams()
				.add("personal", personal)
				.add("groupal", groupal)
				.build();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.GET()
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
			return readEntity(response, new TypeReference<List<TramitacioTasca>>() {});
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Allibera una tasca assignada l'usuari connectat
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param tascaId Id de la tasca
	 * @throws TramitacioException
	 */
	public void alliberaTasca(String entornCodi, Long expedientId, String tascaId) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/tasques/" + tascaId + "/alliberar";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.GET()
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Assigna una tasca a l'usuari connectat
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param tascaId Id de la tasca
	 * @throws TramitacioException
	 */
	public void agafaTasca(String entornCodi, Long expedientId, String tascaId) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/tasques/" + tascaId + "/agafar";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.GET()
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Completa una tasca
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param tascaId Id de la tasca
	 * @param sortida (Opcional) Tasca de sortida
	 * @throws TramitacioException
	 */
	public void completarTasca(String entornCodi, Long expedientId, String tascaId, String sortida) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/tasques/" + tascaId + "/completar";
			if(sortida != null && !sortida.trim().isEmpty())
				url += "?sortida=" + sortida;
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.GET()
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Recupera el formulari (Camps) d'una tasca
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param tascaId Id de la tasca
	 * @throws TramitacioException
	 */
	public List<TramitacioCamp> consultaFormulariTasca(String entornCodi, Long expedientId, String tascaId) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/tasques/" + tascaId + "/formulari";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.GET()
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
			return readEntity(response, new TypeReference<List<TramitacioCamp>>() {});
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Desa les variables del formulari d'una tasca
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param tascaId Id de la tasca
	 * @param variables Llistat codi valor de les dades a desar
	 * @throws TramitacioException
	 */
	public void guardarFormulariTasca(String entornCodi, Long expedientId, String tascaId, List<TramitacioCodiValor> variables) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/tasques/" + tascaId + "/formulari";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(toJson(variables)))
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Recupera documents d'una tasca
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param tascaId Id de la tasca
	 * @throws TramitacioException
	 */
	public List<TramitacioDocument> consultaDocumentsTasca(String entornCodi, Long expedientId, String tascaId) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/tasques/" + tascaId + "/documents";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.GET()
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
			return readEntity(response, new TypeReference<List<TramitacioDocument>>() {});
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Desa un document a una tasca
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param tascaId Id de la tasca
	 * @param document
	 * @throws TramitacioException
	 */
	public void guardarDocumentsTasca(String entornCodi, Long expedientId, String tascaId, TramitacioDocument document) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/tasques/" + tascaId + "/documents";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(toJson(document)))
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());
			checkError(response);
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Esborra un document d'una tasca
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @param expedientId Id del expedient
	 * @param tascaId Id de la tasca
	 * @param documentCodi Codi del document
	 * @throws TramitacioException
	 */
	public void esborrarDocumentTasca(String entornCodi, Long expedientId, String tascaId, String documentCodi) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/expedients/" + expedientId + "/tasques/" + tascaId + "/documents/" + documentCodi;
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.DELETE()
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());
			checkError(response);
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	/**
	 * Consulta totes les dades personals del usuari a un entorn
	 * @param entornCodi Codi de l'entorn d'Helium
	 * @throws TramitacioException
	 */
	public List<TramitacioTasca> consultaTasquesPersonals(String entornCodi) throws TramitacioException {
		try {
			String url = baseUrl + "/rest/v1/tramitacio/" + entornCodi + "/tasques";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Basic " + basic)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.GET()
				.build();
			HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());

			checkError(response);
			return readEntity(response, new TypeReference<List<TramitacioTasca>>() {});
		} catch (Exception e) {
			throw new TramitacioException(e.getMessage(), e);
		}
	}

	private String toJson(Object obj) throws TramitacioException {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception ex) {
			throw new TramitacioException("Error serialitzant petició: " + ex.getMessage(), ex);
		}
	}
}
