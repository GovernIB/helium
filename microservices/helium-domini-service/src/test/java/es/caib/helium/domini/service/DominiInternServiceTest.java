package es.caib.helium.domini.service;

import es.caib.helium.domini.DominiTestHelper;
import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.model.ResultatDomini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DominiInternServiceTest {

    @Mock
    DominiWsService dominiWsService;

    @InjectMocks
    DominiInternServiceImpl dominiInternServiceImpl;

    private final Map<String, String> parametres = new HashMap<>();;

    @BeforeEach
    void setUp() {
        parametres.put("param1", "valor1");
        parametres.put("param2", "valor2");
    }

    @Test
    @DisplayName("Consulta de domini intern")
    void whenConsultaDomini_thenReturn() {
        // Given
        Domini domini = new Domini();
        parametres.put("entorn", "1");
        ResultatDomini resultatDomini = DominiTestHelper.generateResultatDomini();
        given(dominiWsService.consultaDomini(any(Domini.class), anyString(), anyMap())).willReturn(resultatDomini);

        // When
        dominiInternServiceImpl.consultaDomini(domini, "IDF", parametres);

        // Then
        then(dominiWsService).should().consultaDomini(any(Domini.class), anyString(), anyMap());
        then(dominiWsService).shouldHaveNoMoreInteractions();

    }

    @Test
    @DisplayName("Consulta de domini intern - Bad request per entorn null")
    void whenConsultaDomini_thenReturnError() {
        // Given
        Domini domini = new Domini();

        // When
        Exception exception = assertThrows(ResponseStatusException.class,
                () -> dominiInternServiceImpl.consultaDomini(domini, "IDF", parametres)
        );

        // Then
        assertThat(HttpStatus.BAD_REQUEST).isEqualTo(((ResponseStatusException)exception).getStatus());
    }
}