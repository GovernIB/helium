package es.caib.helium.client.expedient.expedient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonPatchBuilder;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import es.caib.helium.client.expedient.expedient.enums.ExpedientEstatTipusEnum;
import es.caib.helium.client.expedient.expedient.model.ConsultaExpedientDades;
import es.caib.helium.client.expedient.expedient.model.ExpedientDto;
import es.caib.helium.client.helper.PatchHelper;
import es.caib.helium.client.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@RequiredArgsConstructor
public class ExpedientClientServiceImpl implements ExpedientClientService {
	
	private final String MISSATGE_LOG = "Cridant Expedient Service - Expedient - ";
	
	private final ExpedientFeignClient expedientClient;

	@Override
	public PagedList<ExpedientDto> findExpedientsAmbFiltrePaginatV1(ConsultaExpedientDades consultaExpedientDades) {
		
		log.debug(MISSATGE_LOG + " llista paginada d'expedients segons el filtre = " +  consultaExpedientDades);
		var responseEntity = expedientClient.findExpedientsAmbFiltrePaginatV1(consultaExpedientDades);
		if (HttpStatus.NO_CONTENT.equals(responseEntity.getStatusCode())) {
			PagedList<ExpedientDto> pagedList = PagedList.emptyPage();
			return pagedList;
		}
		var resultat = responseEntity.getBody();
		return resultat;
	}
	
	@Override
	public PagedList<Long> findExpedientsIdsAmbFiltrePaginatV1(ConsultaExpedientDades consultaExpedientDades) {
		
		log.debug(MISSATGE_LOG + " llista paginada d'identificadors d'expedients segons entorn " + consultaExpedientDades.getEntornId()
				+ " i consultaExpedientDades = " +  consultaExpedientDades);
		var responseEntity = expedientClient.findExpedientsIdsAmbFiltrePaginatV1(consultaExpedientDades);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}

	@Override
	public void createExpedientV1(ExpedientDto expedientDto) {

		log.debug(MISSATGE_LOG + " creant expedient " + expedientDto.getTitol() + " per l'entorn " + expedientDto.getEntornId());
		expedientClient.createExpedientV1(expedientDto);
	}

	@Override
	public void updateExpedientV1(Long expedientId, ExpedientDto expedientDto) {

		log.debug(MISSATGE_LOG + " update expedient amb id " + expedientId + " per l'entorn " + expedientDto.getEntornId());
		expedientClient.updateExpedientV1(expedientId, expedientDto);
	}

	@Override
	public void patchExpedientV1(Long expedientId, JsonNode expedientJson) {

		log.debug(MISSATGE_LOG + " patch expedient amb id " + expedientId);
		expedientClient.patchExpedientV1(expedientId, expedientJson);
	}

	@Override
	public void deleteExpedientV1(Long expedientId) {
		
		log.debug(MISSATGE_LOG + " delete expedient amb id " + expedientId);
		expedientClient.deleteExpedientV1(expedientId);
	}

	@Override
	public ExpedientDto getExpedientV1(Long expedientId) {
		
		log.debug(MISSATGE_LOG + " obtinguent expedient amb id " + expedientId);
		var responseEntity = expedientClient.getExpedientV1(expedientId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}


	@Override
	public void aturar(Long expedientId, String motiu) {
		log.debug(MISSATGE_LOG + " aturar expedient amb id " + expedientId);
		expedientClient.aturar(expedientId, motiu);
	}

	@Override
	public void reprendre(Long expedientId) {
		log.debug(MISSATGE_LOG + " reprendre expedient amb id " + expedientId);
		expedientClient.reprendre(expedientId);
	}

	@Override
	public void anular(Long expedientId, String motiu) {
		log.debug(MISSATGE_LOG + " anul·lar expedient amb id " + expedientId);
		expedientClient.anular(expedientId, motiu);
	}

	@Override
	public void desanular(Long expedientId) {
		log.debug(MISSATGE_LOG + " desanul·lar expedient amb id " + expedientId);
		expedientClient.desanular(expedientId);
	}

	@Override
	public void finalitzar(long expedientId, Date dataFinalitzacio) {
		log.debug(MISSATGE_LOG + " finalitzar expedient amb id " + expedientId);
		
		// Informa la data fi i actualitza estat
		JsonPatchBuilder jpb = Json.createPatchBuilder();
        PatchHelper.replaceDateProperty(jpb, "dataFi", dataFinalitzacio);
        PatchHelper.replaceStringProperty(jpb, "dataTipus", ExpedientEstatTipusEnum.FINALITZAT.toString());
		expedientClient.patchExpedientV1(
				expedientId, 
				PatchHelper.toJsonNode(jpb));
	}
	
	@Override
	public void desfinalitzar(Long expedientId, Long estatId) {
		log.debug(MISSATGE_LOG + " desfinalitzar expedient amb id " + expedientId + " i estatid " + estatId);
		
		// Posa data fi a null i actualitza estat
		JsonPatchBuilder jpb = Json.createPatchBuilder();
		ExpedientEstatTipusEnum estatTipus = ExpedientEstatTipusEnum.INICIAT;
        PatchHelper.replaceDateProperty(jpb, "dataFi", null);
        PatchHelper.replaceStringProperty(jpb, "dataTipus", ExpedientEstatTipusEnum.FINALITZAT.toString());

        
		if (estatId == null) {
	        PatchHelper.replaceIntegerProperty(jpb, "estatId", null);
			estatTipus = ExpedientEstatTipusEnum.INICIAT;
		} else {
	        PatchHelper.replaceIntegerProperty(jpb, "estatId",  estatId.intValue());
			estatTipus = ExpedientEstatTipusEnum.CUSTOM;			
		}
        PatchHelper.replaceStringProperty(jpb, "estatTipus",  estatTipus.toString());
		expedientClient.patchExpedientV1(
				expedientId, 
				PatchHelper.toJsonNode(jpb));
	}
	
	@Override
	public void modificarExpedient(
			Long expedientId,
			Boolean teNumero, 
			String numero, 
			Boolean demanaTitol, 
			String titol, 
			Date dataInici,
			Date dataFi, 
			Long estatId) {
			log.debug(MISSATGE_LOG + " desfinalitzar expedient amb id " + expedientId + " i estatid " + estatId);

		//Actualitzem la informació al MS d'expedients i tasques
		JsonPatchBuilder jpb = Json.createPatchBuilder();
		if (teNumero != null & teNumero) {
			PatchHelper.replaceStringProperty(jpb, "numero", numero);
		}
		if (demanaTitol != null & demanaTitol) {
			PatchHelper.replaceStringProperty(jpb, "titol", titol);
		}
		PatchHelper.replaceDateProperty(jpb, "dataInici", dataInici);
		ExpedientEstatTipusEnum estatTipus = ExpedientEstatTipusEnum.INICIAT;
		if (dataFi != null) {
			estatTipus = ExpedientEstatTipusEnum.FINALITZAT;
		}
		if (estatId != null) {
			PatchHelper.replaceIntegerProperty(jpb, "estatId", estatId.intValue());
			estatTipus = ExpedientEstatTipusEnum.CUSTOM;
		} else {
			PatchHelper.replaceIntegerProperty(jpb, "estatId", null);
		}
		PatchHelper.replaceStringProperty(jpb, "estatTipus", estatTipus.toString());

		expedientClient.patchExpedientV1(
				expedientId, 
				PatchHelper.toJsonNode(jpb));
	}

	@Override
	public List<String> getParticipantsV1(Long expedientId) {
		
		log.debug(MISSATGE_LOG + " obtinguent participants de l'expedient amb id " + expedientId);
		var responseEntity = expedientClient.getParticipantsV1(expedientId);
		var resultat = responseEntity.getBody();
		if (HttpStatus.NO_CONTENT.equals(responseEntity.getStatusCode())) {			
			return new ArrayList<String>();
		}
		return resultat;
}

}