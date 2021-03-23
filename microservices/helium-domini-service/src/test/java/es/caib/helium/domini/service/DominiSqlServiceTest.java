package es.caib.helium.domini.service;

import es.caib.helium.domini.DominiTestHelper;
import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.model.ResultatDomini;
import es.caib.helium.domini.model.TipusAuthEnum;
import es.caib.helium.domini.model.TipusDominiEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.naming.NamingException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DominiSqlServiceTest {

    @Mock
    Map<Long, NamedParameterJdbcTemplate> jdbcTemplates;

    @Captor
    ArgumentCaptor<String> sqlCaptor;
    @Captor
    ArgumentCaptor<MapSqlParameterSource> paramsCaptor;

    @InjectMocks
    DominiSqlServiceImpl dominiSqlService;

    @BeforeEach
    void setUp() {
        dominiSqlService.setJdbcTemplates(jdbcTemplates);
    }

    @Test
    @DisplayName("Consulta de domini SQL")
    void whenConsultaDomini_thenReturn() throws NamingException {

        // Given
        Domini domini = DominiTestHelper.generateDomini(2, "D1",1L, 2L, TipusDominiEnum.CONSULTA_SQL, TipusAuthEnum.HTTP_BASIC);
        domini.setId(1L);
        Map<String, String> parametres = DominiTestHelper.generateParams();
        parametres.put("int", "3");
        parametres.put("int-type", "int");
        parametres.put("float", "3.2");
        parametres.put("float-type", "float");
        parametres.put("boolean", "true");
        parametres.put("boolean-type", "boolean");
        parametres.put("date", "22/11/2020");
        parametres.put("date-type", "date");
        parametres.put("price", "3.5");
        parametres.put("price-type", "price");
        parametres.put("altre", "altre");
        parametres.put("altre-type", "altre");
        parametres.put("error", "error");
        parametres.put("error-type", "int");
        NamedParameterJdbcTemplate sqlTemplate = mock(NamedParameterJdbcTemplate.class);
        ResultatDomini resultatDomini = DominiTestHelper.generateResultatDomini();
        given(jdbcTemplates.get(anyLong())).willReturn(sqlTemplate);
        given(sqlTemplate.query(sqlCaptor.capture(), paramsCaptor.capture(), any(RowMapper.class))).willReturn(resultatDomini);

        // When
        dominiSqlService.consultaDomini(domini, parametres);

        // Then
        then(jdbcTemplates).should().get(anyLong());
//        then(jdbcTemplates).shouldHaveNoMoreInteractions();
        assertThat(sqlCaptor.getValue()).isEqualTo(domini.getSql());
        assertAll("Comprovant paràmetres",
                () -> assertThat(paramsCaptor.getValue()).isNotNull(),
                () -> assertThat(paramsCaptor.getValue().getValue("param1")).isNotNull(),
                () -> assertThat(paramsCaptor.getValue().getValue("param1")).isEqualTo("valor1"),
                () -> assertThat(paramsCaptor.getValue().getValue("param2")).isNotNull(),
                () -> assertThat(paramsCaptor.getValue().getValue("param2")).isEqualTo("valor2")
        );
    }
}