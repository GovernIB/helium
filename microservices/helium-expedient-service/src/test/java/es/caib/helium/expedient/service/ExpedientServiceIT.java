package es.caib.helium.expedient.service;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.github.jenspiegsa.wiremockextension.Managed;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;

import es.caib.helium.expedient.ExpedientTestHelper;
import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.mapper.ExpedientMapper;
import es.caib.helium.expedient.model.ExpedientDto;
import es.caib.helium.expedient.repository.ExpedientRepository;

@ActiveProfiles("test")
@ExtendWith({WireMockExtension.class})
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ExpedientServiceIT {

    @Autowired
    ExpedientServiceImpl expedientService;
    @Autowired
    ExpedientRepository expedientRepository;
    @Autowired
    ExpedientMapper expedientMapper;

    @Value("${spring.datasource.url}") String db_url;

    @Managed
    WireMockServer wireMockServer = with(wireMockConfig().dynamicPort());

    private static Expedient expedient1;
    private static Expedient expedient2;
    private static Expedient expedient3;
    private static Expedient expedient4;
    private static Expedient expedient5;
    private static ExpedientDto dto1;
    private static ExpedientDto dto2;
    private static ExpedientDto dto3;
    private static ExpedientDto dto4;
    private static ExpedientDto dto5;

    @BeforeEach
    void setUp() {

        // Expedients de test:
        // Index, EntornId, ExpedientTipusId, ExpedientId, ProcessInstanceId, Numero, Titol
        expedient1 = ExpedientTestHelper.generateExpedient(1, 1L, 1L, 1L, "6", "1/2021", "Títol 1");
        expedient2 = ExpedientTestHelper.generateExpedient(2, 1L, 1L, 2L, "7", "2/2021", "Títol 2");
        expedient3 = ExpedientTestHelper.generateExpedient(3, 2L, 2L, 3L, "8", "3/2021", "Títol 3");
        expedient4 = ExpedientTestHelper.generateExpedient(4, 2L, 2L, 4L, "9", "4/2021", "Títol 4");
        expedient5 = ExpedientTestHelper.generateExpedient(5, 2L, 3L, 5L, "10", "5/2021", "Títol 5");

        dto1 = expedientService.createExpedient(expedientMapper.entityToDto(expedient1));
        dto2 = expedientService.createExpedient(expedientMapper.entityToDto(expedient2));
        dto3 = expedientService.createExpedient(expedientMapper.entityToDto(expedient3));
        dto4 = expedientService.createExpedient(expedientMapper.entityToDto(expedient4));
        dto5 = expedientService.createExpedient(expedientMapper.entityToDto(expedient5));
    }

    @AfterEach
    void tearDown() {
        expedientRepository.deleteAll();
        expedientRepository.flush();
    }
    
    @Test
    @DisplayName("Crear expedient")
    void whenCreateExpedient_thenReturn() {

        // Given
        Expedient expedient = ExpedientTestHelper.generateExpedient(1, 1L, 1L, 2000L, "2000", "2000/2021", "Títol 2000");
        ExpedientDto dto = expedientMapper.entityToDto(expedient);

        // When
        ExpedientDto creat = expedientService.createExpedient(dto);

        // Then
        assertThat(creat).isNotNull();
        assertThat(creat.getId()).isNotNull();

        dto = expedientService.getById(creat.getId());
        ExpedientTestHelper.comprovaExpedient(creat, dto);

    }
}