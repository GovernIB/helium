package es.caib.helium.domini.controller;

import es.caib.helium.domini.DominiTestHelper;
import es.caib.helium.domini.model.DominiDto;
import es.caib.helium.domini.model.DominiPagedList;
import es.caib.helium.domini.model.PagedList;
import es.caib.helium.domini.model.TipusAuthEnum;
import es.caib.helium.domini.model.TipusDominiEnum;
import es.caib.helium.domini.repository.DominiRepository;
import es.caib.helium.domini.service.DominiService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class DominiControllerIT {

    public static final String API_V1_DOMINI = "/api/v1/dominis/";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    DominiService dominiService;
    @Autowired
    DominiRepository dominiRepository;

    @BeforeEach
    void setUp() {
        dominiService.createDomini(DominiTestHelper.generateDominiDto(1, "CD1",1L, 2L, TipusDominiEnum.CONSULTA_REST, TipusAuthEnum.HTTP_BASIC));
        dominiService.createDomini(DominiTestHelper.generateDominiDto(2, "CD1",2L, 3L, TipusDominiEnum.CONSULTA_SQL, TipusAuthEnum.NONE));
        dominiService.createDomini(DominiTestHelper.generateDominiDto(3, "CD2",1L, 2L, TipusDominiEnum.CONSULTA_WS, TipusAuthEnum.HTTP_BASIC));
        dominiService.createDomini(DominiTestHelper.generateDominiDto(4, "CD2",2L, null, TipusDominiEnum.CONSULTA_WS, TipusAuthEnum.USERNAMETOKEN));
        dominiService.createDomini(DominiTestHelper.generateDominiDto(5, "CD1",2L, 1L, TipusDominiEnum.CONSULTA_REST, TipusAuthEnum.NONE));
    }

    @Test
    @DisplayName("Consulta de dades de domini")
    void whenListDominisV1_thenReturnList() throws Exception {

        String url = API_V1_DOMINI + "?entornId=2";

        PagedList<DominiDto> pagedList = restTemplate.getForObject(
                url,
                DominiPagedList.class);

        assertThat(pagedList.getContent()).hasSize(3);

        pagedList.getContent().forEach(dominiDto -> {
            DominiDto fetchedDominidto = restTemplate.getForObject(API_V1_DOMINI + dominiDto.getId(), DominiDto.class);

            assertThat(dominiDto.getId()).isEqualByComparingTo(fetchedDominidto.getId());
        });
    }

    @AfterEach
    void tearDown() {
        dominiRepository.deleteAll();
        dominiRepository.flush();
    }

}