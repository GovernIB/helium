package es.caib.helium.expedient.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;

import es.caib.helium.expedient.ExpedientTestHelper;
import es.caib.helium.expedient.model.ExpedientDto;
import es.caib.helium.expedient.model.PagedList;
import es.caib.helium.expedient.repository.ExpedientRepository;
import es.caib.helium.expedient.service.ExpedientService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class ExpedientControllerIT {

    public static final String API_V1_EXPEDIENT = "/api/v1/expedients/";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ExpedientService expedientService;
    @Autowired
    ExpedientRepository expedientRepository;

    @BeforeEach
    void setUp() {
    	// int index, Long entorn, Long expedientTipus, Long expedientId, 
    	// Long expedientProcessInstanceId, String expedientNumero,String expedientTitol
        expedientService.createExpedient(ExpedientTestHelper.generateExpedientDto(1, 1L, 1L, 1L, "1", "1/2021", "Expedient 1"));
        expedientService.createExpedient(ExpedientTestHelper.generateExpedientDto(2, 1L, 1L, 2L, "2", "2/2021", "Expedient 2"));
        expedientService.createExpedient(ExpedientTestHelper.generateExpedientDto(3, 2L, 1L, 3L, "3", "3/2021", "Expedient 3"));
        expedientService.createExpedient(ExpedientTestHelper.generateExpedientDto(4, 2L, 1L, 4L, "4", "4/2021", "Expedient 4"));
        expedientService.createExpedient(ExpedientTestHelper.generateExpedientDto(5, 2L, 1L, 5L, "5", "5/2021", "Expedient 5"));
    }

    @Test
    @DisplayName("Consulta de dades de expedient")
    void whenListExpedientsV1_thenReturnList() throws Exception {

        String url = API_V1_EXPEDIENT + "?entornId=2";

        PagedList<ExpedientDto> pagedList = restTemplate.getForObject(
                url,
                PagedList.class);

        assertThat(pagedList.getContent()).hasSize(3);

        pagedList.getContent().forEach(expedientDto -> {
            ExpedientDto fetchedExpedientdto = restTemplate.getForObject(API_V1_EXPEDIENT + expedientDto.getId(), ExpedientDto.class);

            assertThat(expedientDto.getId()).isEqualByComparingTo(fetchedExpedientdto.getId());
        });
    }

    @AfterEach
    void tearDown() {
        expedientRepository.deleteAll();
        expedientRepository.flush();
    }

}