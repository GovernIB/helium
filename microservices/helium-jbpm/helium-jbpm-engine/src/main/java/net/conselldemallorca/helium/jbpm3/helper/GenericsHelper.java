package net.conselldemallorca.helium.jbpm3.helper;

import net.conselldemallorca.helium.api.dto.ArxiuDto;
import net.conselldemallorca.helium.api.dto.FestiuDto;
import net.conselldemallorca.helium.api.dto.ReassignacioDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class GenericsHelper {

    @Value("${es.caib.helium.jbpm.bridge.service.host}")
    String bridgeAdress;

    @Autowired
    RestTemplate restTemplate;

    public void emailSend(Email email) {
        restTemplate.postForLocation(
                getGenericsBridgeAddress() + "/email",
                email);
    }

    public List<FestiuDto> getFestius() {
        FestiuDto[] festius = restTemplate.getForObject(
                getGenericsBridgeAddress() + "/festius",
                FestiuDto[].class);

        if (festius != null) {
            return Arrays.asList(festius);
        } else {
            return new ArrayList<FestiuDto>();
        }
    }

    public ReassignacioDto findReassignacioActivaPerUsuariOrigen(
            String processInstanceId,
            String usuariCodi) {
        return  restTemplate.getForObject(
                getGenericsBridgeAddress() + "/reassignacio/{usuariCodi}?processInstanceId={processInstanceId}",
                ReassignacioDto.class,
                usuariCodi,
                processInstanceId);
    }


    private String getGenericsBridgeAddress() {
        return bridgeAdress + "/generic";
    }

    @Data @Builder
    public static class Email {
        private String fromAddress;
        private List<String> recipients;
        private List<String> ccRecipients;
        private List<String> bccRecipients;
        private String subject;
        private String text;
        private List<ArxiuDto> attachments;
    }

}
