package es.caib.helium.client.expedient.tasca;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonPatchBuilder;
import javax.json.JsonValue;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.client.expedient.tasca.model.ConsultaTascaDades;
import es.caib.helium.client.expedient.tasca.model.TascaDto;
import es.caib.helium.client.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TascaClientServiceImpl implements TascaClientService {
	
	private final String MISSATGE_LOG = "Cridant Expedient Service - Tasca - ";
	
	private final TascaFeignClient tascaClient;

	@Override
	public PagedList<TascaDto> findTasquesAmbFiltrePaginatV1(ConsultaTascaDades consultaTascaDades) {
		
		log.debug(MISSATGE_LOG + " llista paginada d'expedients segons el filtre = " +  consultaTascaDades);
		var responseEntity = tascaClient.findTasquesAmbFiltrePaginatV1(consultaTascaDades);
		if (HttpStatus.NO_CONTENT.equals(responseEntity.getStatusCode())) {
			PagedList<TascaDto> pagedList = PagedList.emptyPage();
			return pagedList;
		}
		var resultat = responseEntity.getBody();
    	return resultat;
	}

	@Override
	public PagedList<String> findTasquesIdsAmbFiltrePaginatV1(ConsultaTascaDades consultaTascaDades) {
		
		log.debug(MISSATGE_LOG + " llista paginada d'expedients segons el filtre = " +  consultaTascaDades);
		var responseEntity = tascaClient.findTasquesIdsAmbFiltrePaginatV1(consultaTascaDades);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}
	
	@Override
	public void createTascaV1(TascaDto tascaDto) {

		log.debug(MISSATGE_LOG + " creant tasca " + tascaDto.getTitol() + " per l'expedient " + tascaDto.getExpedientId());
		tascaClient.createTascaV1(tascaDto);
	}

	@Override
	public void updateTascaV1(String tascaId, TascaDto tascaDto) {

		log.debug(MISSATGE_LOG + " actualitzant tasca amb id " + tascaId + " per l'expedient " + tascaDto.getExpedientId());
		tascaClient.createTascaV1(tascaDto);
	}

	@Override
	public void patchTascaV1(String tascaId, JsonNode tascaJson) {
		
		log.debug(MISSATGE_LOG + " patch tasca amb id " + tascaId);
		tascaClient.patchTascaV1(tascaId, tascaJson);
	}

	@Override
	public void deleteTascaV1(String tascaId) {

		log.debug(MISSATGE_LOG + " esborrant tasca amb id " + tascaId);
		tascaClient.deleteTascaV1(tascaId);
	}

	@Override
	public TascaDto getTascaV1(String tascaId) {

		log.debug(MISSATGE_LOG + " obtinguent tasca amb id " + tascaId);
		var responseEntity = tascaClient.getTascaV1(tascaId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<String> getResponsablesV1(String tascaId) {
		
		log.debug(MISSATGE_LOG + " obtinguent responsables de la tasca amb id " + tascaId);
		var responseEntity = tascaClient.getResponsablesV1(tascaId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void setResponsablesV1(String tascaId, List<String> responsables) {
		
		log.debug(MISSATGE_LOG + " esborrant tasca amb id " + tascaId);
		tascaClient.deleteTascaV1(tascaId);
	}

	@Override
	public void deleteResponsablesV1(String tascaId) {
		
		log.debug(MISSATGE_LOG + " esborrant tasca amb id " + tascaId);
		tascaClient.deleteTascaV1(tascaId);
	}
	
	@Override
	public void setUsuariAssignat(String tascaId, String usuariAssignat) {
		
		log.debug(MISSATGE_LOG + " update usuari tasca " + tascaId + ": " + usuariAssignat);
		
		JsonPatchBuilder jpb = Json.createPatchBuilder();
		if (usuariAssignat != null) {
			jpb.replace("usuariAssignat", usuariAssignat);
		} else {
			jpb.replace("usuariAssignat", JsonValue.NULL);
		}
		tascaClient.patchTascaV1(
				tascaId, 
				new ObjectMapper().valueToTree(jpb.build()));
	}

	@Override
	public void setCancelada(String tascaId, boolean cancelada) {
		log.debug(MISSATGE_LOG + " update cancel·lada tasca " + tascaId + ": " + cancelada);
		
		JsonPatchBuilder jpb = Json.createPatchBuilder();
		jpb.replace("cancelada", cancelada);
		tascaClient.patchTascaV1(
				tascaId, 
				new ObjectMapper().valueToTree(jpb.build()));
	}

	@Override
	public void setSuspesa(String tascaId, boolean suspesa) {
		log.debug(MISSATGE_LOG + " update suspesa tasca " + tascaId + ": " + suspesa);
		
		JsonPatchBuilder jpb = Json.createPatchBuilder();
		jpb.replace("suspesa", suspesa);
		tascaClient.patchTascaV1(
				tascaId, 
				new ObjectMapper().valueToTree(jpb.build()));
	}
	
	@Override
	public void setErrorFinalitzacio(String tascaId, String errorFinalitzacio) {
		log.debug(MISSATGE_LOG + " update error finalitzacio " + tascaId + ": " + errorFinalitzacio);
		
		JsonPatchBuilder jpb = Json.createPatchBuilder();
		jpb.replace("errorFinalitzacio", errorFinalitzacio);
		tascaClient.patchTascaV1(
				tascaId, 
				new ObjectMapper().valueToTree(jpb.build()));
	}

	@Override
	public void marcarFinalitzar(String tascaId, Date marcadaFinalitzar) {
		log.debug(MISSATGE_LOG + " marcar finalitzar " + tascaId + ": " + marcadaFinalitzar);
		
		JsonPatchBuilder jpb = Json.createPatchBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		jpb.replace("marcadaFinalitzar", sdf.format(marcadaFinalitzar));
		jpb.replace("iniciFinalitzacio", JsonValue.NULL);
		jpb.replace("errorFinalitzacio", JsonValue.NULL);
		tascaClient.patchTascaV1(
				tascaId, 
				new ObjectMapper().valueToTree(jpb.build()));
	}

}
