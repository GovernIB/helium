package net.conselldemallorca.helium.jbpm3.helper;

import net.conselldemallorca.helium.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.api.dto.ExpedientDto;
import lombok.Builder;
import lombok.Data;
import net.conselldemallorca.helium.api.dto.ExpedientInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class ExpedientsHelper {

    @Value("${es.caib.helium.jbpm.bridge.service.host}")
    String bridgeAdress;

    @Autowired
    RestTemplate restTemplate;


    public List<ExpedientInfoDto> findExpedientsConsultaGeneral(
            Long entornId,
            String titol,
            String numero,
            Date dataInici1,
            Date dataInici2,
            Long expedientTipusId,
            Long estatId,
            boolean nomesIniciats,
            boolean nomesFinalitzats) {
        ExpedientInfoDto[] expedients = restTemplate.getForObject(
                getExpedientBridgeAddress() + "/{entornId}" +
                        "?titol={titol}" +
                        "&numero={numero}" +
                        "&dataInici1={dataInici1}" +
                        "&dataInici2={dataInici2}" +
                        "&expedientTipusId={expedientTipusId}" +
                        "&estatId={estatId}" +
                        "&nomesIniciats={nomesIniciats}" +
                        "&nomesFinalitzats={nomesFinalitzats}",
                ExpedientInfoDto[].class,
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
            return new ArrayList<ExpedientInfoDto>();
        }
    }

    public List<ExpedientInfoDto> findExpedientsConsultaDades(
            Long entornId,
            String expedientTipusCodi,
            Map<String, String> filtreValors) {
        UriComponentsBuilder queryBuilder = UriComponentsBuilder.fromHttpUrl(getExpedientBridgeAddress() + "/{entornId}/byExpedientTipus/{expedientTipusCodi}");
        if (filtreValors != null) {
            for (Map.Entry<String, String> filtre: filtreValors.entrySet()) {
                queryBuilder.queryParam(filtre.getKey(), filtre.getValue());
            }
        }
        queryBuilder.buildAndExpand(filtreValors).toUriString();
        ExpedientInfoDto[] expedients = restTemplate.getForObject(
                queryBuilder.buildAndExpand(filtreValors).toUriString(),
                ExpedientInfoDto[].class,
                entornId,
                expedientTipusCodi);

        if (expedients != null) {
            return Arrays.asList(expedients);
        } else {
            return new ArrayList<ExpedientInfoDto>();
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

    public String getProcessInstanceIdAmbEntornITipusINumero(
            Long entornId,
            String expedientTipusCodi,
            String numero) {
        return  restTemplate.getForObject(
                getExpedientBridgeAddress() + "/{entornId}/processInstance" +
                        "?expedientTipusCodi={expedientTipusCodi}" +
                        "&numero={numero}",
                String.class,
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
                null,
                processInstanceId);
    }

    public void finalitzarExpedient(String processInstanceId, Date dataFinalitzacio) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/finalitzar",
                dataFinalitzacio,
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

    public void expedientModificarEstat(
            String processInstanceId,
            Long estatId) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/estatId",
                estatId,
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

    public void expedientModificarGeoreferencia(
            String processInstanceId,
            String referencia) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/georeferencia",
                referencia,
                processInstanceId);
    }

    public void expedientModificarGeoX(
            String processInstanceId,
            Double posx) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/posx",
                posx,
                processInstanceId);
    }

    public void expedientModificarGeoY(
            String processInstanceId,
            Double posy) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/posy",
                posy,
                processInstanceId);
    }

    public void expedientModificarDataInici(
            String processInstanceId,
            Date dataInici) {
        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{processInstanceId}/dataInici",
                dataInici,
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

        restTemplate.postForLocation(
                getExpedientBridgeAddress() + "/{expedientId}/error",
                expedientError,
                expedientId);
    }

    public String getDadaPerProcessInstance(
            String processInstanceId,
            String varCodi) {
        ExpedientDadaDto dada = restTemplate.getForObject(
                getExpedientBridgeAddress() + "/process/{processInstanceId}/dada/{codi}",
                ExpedientDadaDto.class,
                processInstanceId,
                varCodi);

        if (dada != null) {
            return dada.getText();
        }
        return null;
    }

    private String getExpedientBridgeAddress() {
        return bridgeAdress + "/expedients";
    }


    @Data @Builder
    public static class Georeferencia {
        private Double posx;
        private Double posy;
        private String referencia;
    }

    @Data @Builder
    public static class ExpedientError {
        private Long jobId;
        private String errorDesc;
        private String errorFull;
    }

}
