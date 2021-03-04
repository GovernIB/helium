package es.caib.helium.domini.service;

import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.model.ResultatDomini;
import es.caib.helium.domini.model.TipusAuthEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class DominiRestServiceImpl implements DominiRestService {

    private final RestTemplate restTemplate;

    @Override
    public ResultatDomini consultaDomini(
            Domini domini,
            String identificador,
            Map<String, Object> parametres) {

        MultiValueMap<String, String> mvm = getMultiValueMap(identificador, parametres);

        String url = UriComponentsBuilder.fromHttpUrl(domini.getUrl())
                .queryParams(mvm)
                .toUriString();
        ResponseEntity<ResultatDomini> resultat = restTemplate.exchange(
                url,
                HttpMethod.GET,
                createRequest(domini),
                ResultatDomini.class);
        return resultat.getBody();
    }

    private MultiValueMap<String, String> getMultiValueMap(String identificador, Map<String, Object> parametres) {
        MultiValueMap<String,String> mvm = new LinkedMultiValueMap<>();
        for(Map.Entry<String, Object> entry : parametres.entrySet())
            mvm.add(entry.getKey(), entry.getValue().toString());
        if(identificador != null)
            mvm.add("dominicodi", identificador);
        return mvm;
    }

    private HttpEntity<?> createRequest(Domini domini){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (TipusAuthEnum.HTTP_BASIC.equals(domini.getTipusAuth())) {
            headers.setBasicAuth(domini.getUsuari(), domini.getContrasenya());
        }
        return new HttpEntity<>(headers);
    }
}
