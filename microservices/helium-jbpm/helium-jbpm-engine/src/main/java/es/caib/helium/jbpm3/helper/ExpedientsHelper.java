package es.caib.helium.jbpm3.helper;

import es.caib.helium.api.dto.ExpedientDto;
import es.caib.helium.api.dto.FestiuDto;
import es.caib.helium.api.dto.PersonaDto;
import es.caib.helium.api.dto.ReassignacioDto;
import es.caib.helium.api.dto.TerminiDto;
import es.caib.helium.api.exception.NoTrobatException;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class ExpedientsHelper {

    @Value("${es.caib.helium.jbpm.bridge.service.host}")
    String bridgeAdress;

    @Autowired
    RestTemplate restTemplate;


    public List<ExpedientDto> findExpedientsConsultaGeneral(
            Long entornId,
            String titol,
            String numero,
            Date dataInici1,
            Date dataInici2,
            Long expedientTipusId,
            Long estatId,
            boolean nomesIniciats,
            boolean nomesFinalitzats) {
        ExpedientDto[] expedients = restTemplate.getForObject(
                getExpedientBridgeAddress() + "/{entornId}" +
                        "?titol={titol}" +
                        "&numero={numero}" +
                        "&dataInici1={dataInici1}" +
                        "&dataInici2={dataInici2}" +
                        "&expedientTipusId={expedientTipusId}" +
                        "&estatId={estatId}" +
                        "&nomesIniciats={nomesIniciats}" +
                        "&nomesFinalitzats={nomesFinalitzats}",
                ExpedientDto[].class,
                entornId,
                titol,
                numero,
                dataInici1,
                dataInici2,
                expedientTipusId,
                estatId,
                nomesIniciats,
                nomesFinalitzats);

        if (expedients != null) {
            return Arrays.asList(expedients);
        } else {
            return new ArrayList<ExpedientDto>();
        }
    }

    public ExpedientDto getExpedientAmbEntornITipusINumero(
            Long entornId,
            String expedientTipusCodi,
            String numero) {
        return  restTemplate.getForObject(
                getExpedientBridgeAddress() + "/{entornId}/byNumero" +
                        "?expedientTipusCodi={expedientTipusCodi}" +
                        "&numero={numero}",
                ExpedientDto.class,
                entornId,
                expedientTipusCodi,
                numero);
    }

    public ExpedientDto getExpedientArrelAmbProcessInstanceId(
            String processInstanceId) {
        return  restTemplate.getForObject(
                getExpedientBridgeAddress() + "/{processInstanceId}/arrel",
                ExpedientDto.class,
                processInstanceId);
    }

    public void expedientAturar(
            String processInstanceId,
            String motiu) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/aturar",
                motiu,
                processInstanceId);
    }

    public void expedientReprendre(
            String processInstanceId) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/reprendre",
                processInstanceId);
    }

    public void finalitzarExpedient(String processInstanceId) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/finalitzar",
                processInstanceId);
    }

    public void desfinalitzarExpedient(String processInstanceId) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/desfinalitzar",
                processInstanceId);
    }

    public void expedientRelacionar(
            Long expedientIdOrigen,
            Long expedientIdDesti) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{expedientIdOrigen}/relacionar/{expedientIdDesti}",
                expedientIdOrigen,
                expedientIdDesti  );
    }

    public void expedientModificarEstat(
            String processInstanceId,
            String estatCodi) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/estat",
                estatCodi,
                processInstanceId);
    }

    public void expedientModificarComentari(
            String processInstanceId,
            String comentari) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/comentari",
                comentari,
                processInstanceId);
    }

    public void expedientModificarNumero(
            String processInstanceId,
            String numero) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/numero",
                numero,
                processInstanceId);
    }

    public void expedientModificarTitol(
            String processInstanceId,
            String titol) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/titol",
                titol,
                processInstanceId);
    }

    public void expedientModificarGeoref(
            String processInstanceId,
            Georeferencia georeferencia) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/georef",
                georeferencia,
                processInstanceId);
    }

    public void expedientModificarGrup(
            String processInstanceId,
            String grupCodi) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/grup",
                grupCodi,
                processInstanceId);
    }

    public void expedientModificarResponsable(
            String processInstanceId,
            String responsableCodi) {

        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/responsable",
                responsableCodi,
                processInstanceId);
    }

    public void updateExpedientError(
            Long expedientId,
            ExpedientError expedientError) {

    }

    private String getExpedientBridgeAddress() {
        return bridgeAdress + "/expedients";
    }

    @Data @Builder
    public class Georeferencia {
        private Double posx;
        private Double posy;
        private String referencia;
    }

    @Data @Builder
    public class ExpedientError {
        private Long jobId;
        private String errorDesc;
        private String errorFull;
    }

}
