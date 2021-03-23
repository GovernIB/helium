package es.caib.helium.domini.service;

import es.caib.helium.domini.DominiTestHelper;
import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.ws.ConsultaDomini;
import es.caib.helium.domini.ws.ConsultaDominiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.WebServiceMessageSender;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DominiWsServiceTest {

    @Mock
    WebServiceTemplate webServiceTemplate;

    @Captor
    ArgumentCaptor<String> urlCaptor;
    @Captor
    ArgumentCaptor<ConsultaDomini> requestCaptor;

    @InjectMocks
    DominiWsServiceImpl dominiWsService;

    @Test
    @DisplayName("Consulta de domini WS")
    void consultaDomini() {

        // Given
        final String IDF = "IDF";
        Domini domini = DominiTestHelper.generateDomini(1, "D1",1L, 2L);
        ConsultaDominiResponse consultaDominiResponse = DominiTestHelper.generateConsultaDominiResponse();
        Map<String, String> parametres = DominiTestHelper.generateParams();
        given(webServiceTemplate.marshalSendAndReceive(urlCaptor.capture(), requestCaptor.capture())).willReturn(consultaDominiResponse);
//        willDoNothing().given(webServiceTemplate).setMessageSender(messageCaptor.capture());

        // When
        dominiWsService.consultaDomini(domini, IDF, parametres);

        // Then
        then(webServiceTemplate).should().setMessageSender(any(WebServiceMessageSender.class));
        then(webServiceTemplate).should().marshalSendAndReceive(anyString(), any(ConsultaDomini.class));
        then(webServiceTemplate).shouldHaveNoMoreInteractions();
        assertThat(domini.getUrl()).isEqualTo(urlCaptor.getValue());
        assertAll("Comprovant request",
                () -> assertThat(requestCaptor.getValue()).isNotNull(),
                () -> assertThat(IDF).isEqualTo(requestCaptor.getValue().getArg0()),
                () -> assertThat(requestCaptor.getValue().getArg1()).isNotNull(),
                () -> assertThat(2).isEqualTo(requestCaptor.getValue().getArg1().size()),
                () -> assertThat("param1").isEqualTo(requestCaptor.getValue().getArg1().get(0).getCodi()),
                () -> assertThat("valor1").isEqualTo(requestCaptor.getValue().getArg1().get(0).getValor()),
                () -> assertThat("param2").isEqualTo(requestCaptor.getValue().getArg1().get(1).getCodi()),
                () -> assertThat("valor2").isEqualTo(requestCaptor.getValue().getArg1().get(1).getValor())
        );
    }

}