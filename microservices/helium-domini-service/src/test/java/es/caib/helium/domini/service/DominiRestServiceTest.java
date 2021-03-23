package es.caib.helium.domini.service;

import es.caib.helium.domini.DominiTestHelper;
import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.model.ResultatDomini;
import es.caib.helium.domini.model.TipusAuthEnum;
import es.caib.helium.domini.model.TipusDominiEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DominiRestServiceTest {

    @Mock
    RestTemplate restTemplate;
    @Mock
    RestTemplateBuilder restTemplateBuilder;

    @Captor
    ArgumentCaptor<String> urlCaptor;
    @Captor
    ArgumentCaptor<HttpMethod> methodCaptor;
    @Captor
    ArgumentCaptor<HttpEntity> headerCaptor;

    @InjectMocks
    DominiRestServiceImpl dominiRestService;

    @Test
    @DisplayName("Consulta de domini REST")
    void consultaDomini() {
        // Given
        final String IDF = "IDF";
        Domini domini = DominiTestHelper.generateDomini(2, "D1",1L, 2L, TipusDominiEnum.CONSULTA_REST, TipusAuthEnum.HTTP_BASIC);
        ResultatDomini resultatDomini = DominiTestHelper.generateResultatDomini();
        ResponseEntity resposta = new ResponseEntity(resultatDomini, HttpStatus.OK);
        Map<String, String> parametres = DominiTestHelper.generateParams();
        given(restTemplate.exchange(urlCaptor.capture(), methodCaptor.capture(), headerCaptor.capture(), (Class<Object>) any())).willReturn(resposta);

        // When
        dominiRestService.consultaDomini(domini, IDF, parametres);

        // Then
        then(restTemplate).should().exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), (Class<Object>) any());
        then(restTemplate).shouldHaveNoMoreInteractions();
        String url = urlCaptor.getValue();
        assertAll("Comprovant Url",
                () -> assertThat(url).isNotNull(),
                () -> assertThat(url).isNotBlank(),
                () -> assertThat(url).startsWith(domini.getUrl()),
                () -> assertThat(url).contains("param1=valor1"),
                () -> assertThat(url).contains("param2=valor2"),
                () -> assertThat(url).contains("dominicodi=" + IDF)
        );
        assertThat(HttpMethod.GET).isEqualTo(methodCaptor.getValue());
        assertAll("Comprovant headers",
                () -> assertThat(headerCaptor.getValue()).isNotNull(),
                () -> assertThat(headerCaptor.getValue().getHeaders()).isNotNull(),
//                () -> assertThat(headerCaptor.getValue().getHeaders()).isNotEmpty(),
                () -> assertThat(headerCaptor.getValue().getHeaders().getAccept()).isNotNull(),
                () -> assertThat(headerCaptor.getValue().getHeaders().getAccept()).isNotEmpty(),
                () -> assertThat(headerCaptor.getValue().getHeaders().getAccept().get(0)).isEqualTo(MediaType.APPLICATION_JSON),
                () -> assertThat(headerCaptor.getValue().getHeaders().get("Authorization")).isNotNull(),
                () -> assertThat(headerCaptor.getValue().getHeaders().get("Authorization").get(0)).isEqualTo(DominiTestHelper.encodeBasicAuth(domini))
        );
    }

}